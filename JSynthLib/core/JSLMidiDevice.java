/*
 * Copyright 2004 Hiroo Hayashi
 *
 * This file is part of JSynthLib.
 *
 * JSynthLib is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or(at your option) any later version.
 *
 * JSynthLib is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JSynthLib; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 */

package core;

//import javax.sound.midi.MidiDevice;
import javax.sound.midi.*;
//import java.util.Set;
import java.util.*;
//import core.Storable;

/**
 * JSLMidiDevice.java
 *
 * MidiDevice implements Storable interface.
 *
 * Created: Sat Feb 28 23:01:17 2004
 *
 * @author Hiroo Hayashi
 * @version 1.0
 * @see javax.sound.midi.MidiDevice
 * @see core.Storable
 */
public class JSLMidiDevice implements MidiDevice, Storable {
    private MidiDevice md;
    private MidiDevice.Info info;
    private String name;
    private String vendor;
    private boolean isInput;
    private boolean isOutput;
    /** MIDI Input queue */
    // These cause 'ClassCastException'
    //private LinkedList list = (LinkedList) Collections.synchronizedList(new LinkedList());
    //private LinkedList list = (LinkedList) Collections.synchronizedList((List) new LinkedList());
    private List list = Collections.synchronizedList(new LinkedList());

    /** only for Storage.restoreValue. */
    public JSLMidiDevice() {
    } // JSLMidiDevice constructor

    JSLMidiDevice(MidiDevice md) {
	this.md = md;
	info = md.getDeviceInfo();
	name = info.getName();
	vendor = info.getVendor();
	isInput = (md.getMaxTransmitters() != 0);
	isOutput = (md.getMaxReceivers() != 0);
    }

    JSLMidiDevice(MidiDevice.Info info)
	throws MidiUnavailableException {
	this(MidiSystem.getMidiDevice(info));
    }

    // MidiDevice Interface
    public void close() {
	md.close();
    }
    public MidiDevice.Info getDeviceInfo() {
	return info;
    }
    public int getMaxReceivers() {
	return md.getMaxReceivers();
    }
    public int getMaxTransmitters() {
	return md.getMaxTransmitters();
    }
    public long getMicrosecondPosition() {
	return md.getMicrosecondPosition();
    }
    public Receiver getReceiver() throws MidiUnavailableException {
	return md.getReceiver();
    }
    public Transmitter getTransmitter() throws MidiUnavailableException {
	return md.getTransmitter();
    }
    public boolean isOpen() {
	return md.isOpen();
    }
    public void open() throws MidiUnavailableException {
	md.open();
    }
    // end of MidiDevice Interface

    // Storable interface
    public Set storedProperties() {
	final String[] storedPropertyNames = {
	    "name", "vendor", "isInput", "isOutput"
	};
	TreeSet set = new TreeSet();
	set.addAll(Arrays.asList(storedPropertyNames));
	return set;
    }
    public void afterRestore() {
	try {
	    md = getMidiDevice(name, vendor, isInput, isOutput);
	    info = md.getDeviceInfo();
	} catch (MidiUnavailableException e) {
	}
    }
    // end of Storable interface

    private MidiDevice getMidiDevice(String name, String vendor,
				     boolean isInput, boolean isOutput)
	throws MidiUnavailableException {
	MidiDevice.Info[] mdi = MidiSystem.getMidiDeviceInfo();
	for (int i = 0; i < mdi.length; i++) {
	    if (mdi[i].getName().equals(name)
		&& mdi[i].getVendor().equals(vendor)) {
		MidiDevice md = MidiSystem.getMidiDevice(mdi[i]);
		if ((isInput && md.getMaxTransmitters() != 0)
		    || (isOutput && md.getMaxReceivers() != 0))
		    return md;
	    }
	}
	return null;	// throw a proper Exception!!!FIXIT!!!
    }
    // setter/getter for Storable interface
    public boolean getIsInput() {
	return isInput;
    }
    public void setIsInput(boolean isInput) {
	this.isInput = isInput;
    }
    public boolean getIsOutput() {
	return isOutput;
    }
    public void setIsOutput(boolean isOutput) {
	this.isOutput = isOutput;
    }
    public String getName() {
	return name;
    }
    public void setName(String name) {
	this.name = name;
    }
    public String getVendor() {
	return vendor;
    }
    public void setVendor(String vendor) {
	this.vendor = vendor;
    }

    // MIDI System Exclusive Message Input support routines
    private class SysexReceiver implements Receiver {
	//Receiver interface
	public void close() {
	}

	public void send(MidiMessage message, long timeStamp) {
	    int status = message.getStatus();
	    // ignore the Real Time messages (0xf8-0xff)
	    if ((status & 0xf8) == 0xf8)
		return;
	    list.add(message);
	}
    }

    /**
     * This allows you to read a JavaSound-compatible MidiMessage
     * object. It automatically detects if it's a SysexMessage or
     * ShortMessage and returns the appropriate object.
     *
     * Throw TimeoutException if a MIDI message cannot read in the
     * time specified by <code>timeout</code>.
     *
     * @param port The port to read from
     * @param timeout timeout count (in millisecond)
     * @param ignoreShortMessage ignore ShortMessage
     * @return MidiMessage object read from the port.
     * @throws Exception
     */
    // original comment was added by emenaker 2003.03.22
    private SysexMessage readSysexMessage(long timeout)
	throws TimeoutException, InvalidMidiDataException, Exception {
	long start = System.currentTimeMillis();
	byte [] buffer = {};
	int totalLen = 0;
	boolean firstMsg = true;
	do {
	    // wait for data
	    while (messagesWaiting() == 0) {
		Thread.sleep(10); // define const!!!FIXIT!!!
		if (System.currentTimeMillis() - start > timeout)
		    throw new TimeoutException(this);
	    }
	    MidiMessage msg = getMessage();
	    int len = msg.getLength();
	    if (firstMsg) {
		if (msg.getStatus() != SysexMessage.SYSTEM_EXCLUSIVE)
		    continue;
		buffer = msg.getMessage();
		totalLen = len;
		if (buffer[totalLen - 1] == (byte) ShortMessage.END_OF_EXCLUSIVE)
		    return (SysexMessage) msg;
		firstMsg = false;
	    } else {
		int status = msg.getStatus();
		// If an exclusive message is terminated by "any other
		// Status byte (except Real Time messages)".
		if (status != SysexMessage.SPECIAL_SYSTEM_EXCLUSIVE) {
		    len = 1;
		    if (status == SysexMessage.SYSTEM_EXCLUSIVE)
			((LinkedList) list).addFirst(msg);
		}
		// Combine the newly-read stuff into an new array
		// with the existing stuff
		byte [] buf = msg.getMessage();
		byte[] combineBuffer = new byte[totalLen + len];
		System.arraycopy(buffer,  0, combineBuffer, 0, totalLen);
		if (len == 1) {
		    combineBuffer[totalLen] = (byte) ShortMessage.END_OF_EXCLUSIVE;
		    totalLen++;
		} else {
		    System.arraycopy(buf, 1, combineBuffer, totalLen, len - 1);
		    totalLen += len - 1;
		}
		buffer = combineBuffer;
	    }
	} while (firstMsg || buffer[totalLen - 1] != (byte) ShortMessage.END_OF_EXCLUSIVE);
	SysexMessage sysexmsg = new SysexMessage();
	sysexmsg.setMessage(buffer, totalLen);
	return sysexmsg;
    }

    private int messagesWaiting() {
        return list.size();
    }

    public void clearMidiInBuffer() {
	list.clear();
    }

    private MidiMessage getMessage()
	throws InvalidMidiDataException {
	// pop the oldest message
	MidiMessage msg = (MidiMessage) list.remove(0);

	msg = MidiUtil.fixShortMessage(msg);
	//MidiUtil.logIn(port, msg);
	return msg;
    }

    public class TimeoutException extends Exception {
	public TimeoutException(Object o) throws Exception {
	    super("Timeout error on " + o.toString());
	}
    }

    // only for test
    public static void main(String[] args)  {
	JSLMidiDevice md = new JSLMidiDevice();
	try {
	    //System.out.println("MidiWrapper.JSLMidiDevice");
	    Class storableClass = Class.forName("core.JSLMidiDevice");
	    Storable storable = (Storable) storableClass.newInstance();
	    System.out.println(storable.toString());
	} catch (ClassNotFoundException e) {
	    System.out.println(e);
	} catch (InstantiationException e) {
	    System.out.println(e);
	} catch (IllegalAccessException e) {
	    System.out.println(e);
	}
    }
} // JSLMidiDevice
