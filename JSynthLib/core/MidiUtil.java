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

import javax.sound.midi.*;
import java.util.*;

/**
 * MidiUtil.java
 *
 * MIDI Utility Routines.  This class contains methods and inner
 * classes for Java Sound API.<p>
 * Examples:<p>
 * MIDI output
 * <pre>
 *   Receiver rcvr = MidiUtil.getReceiver(outport);
 *   MidiUtil.send(rcvr, msg);
 *   rcvr.close();
 * </pre>
 * MIDI input
 * <pre>
 *   MidiUtil.clearSysexInputQueue(inport);
 *   msg = MidiUtil.getMessage(inport, 1000);
 * </pre>
 * See the description for each method for more details.
 *
 * @author Hiroo Hayashi
 * @version $Id$
 * @see <a href="http://java.sun.com/j2se/1.4.2/docs/guide/sound/programmer_guide/contents.html">
 * Java Sound Progremmer Guide</a>
 */
public final class MidiUtil {

    /**
     * holds the state if a SysexMessage is completely displayed or
     * shorten to one hexdump line. (Complete Sysex Message)
     */
    private static boolean CSMstate = false;

    private static MidiDevice.Info[] outputMidiDeviceInfo;
    private static MidiDevice.Info[] inputMidiDeviceInfo;
    private static Receiver[] midiOutRcvr;
    private static MidiUtil.SysexInputQueue[] sysexInputQueue;

    // static initialization
    static {
	try {
	    outputMidiDeviceInfo = createOutputMidiDeviceInfo();
	    inputMidiDeviceInfo = createInputMidiDeviceInfo();
	} catch (MidiUnavailableException e) {
	    ErrorMsg.reportStatus(e);
	}
	midiOutRcvr = new Receiver[outputMidiDeviceInfo.length];
	sysexInputQueue = new MidiUtil.SysexInputQueue[inputMidiDeviceInfo.length];
    }

    // don't have to call constructor for Utility class.
    private MidiUtil() {
    }

    /** Returns an array of MidiDevice.Info for MIDI input device. */
    private static MidiDevice.Info[] createInputMidiDeviceInfo()
	throws MidiUnavailableException {
	ArrayList list = new ArrayList();

	MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
        for (int i = 0; i < infos.length; i++) {
	    // throws MidiUnavailableException
	    MidiDevice device = MidiSystem.getMidiDevice(infos[i]);
	    if (device.getMaxTransmitters() != 0
		&& !(device instanceof Synthesizer)
		&& !(device instanceof Sequencer))
		list.add(infos[i]);
	}
	return (MidiDevice.Info[]) list.toArray(new MidiDevice.Info[0]);
    }

    /** Returns an array of MidiDevice.Info for MIDI output device. */
    private static MidiDevice.Info[] createOutputMidiDeviceInfo()
	throws MidiUnavailableException {
	ArrayList list = new ArrayList();

	MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
        for (int i = 0; i < infos.length; i++) {
	    // throws MidiUnavailableException
	    MidiDevice device = MidiSystem.getMidiDevice(infos[i]);
	    if (device.getMaxReceivers() != 0
		&& !(device instanceof Synthesizer)
		&& !(device instanceof Sequencer))
		list.add(infos[i]);
	}
	return (MidiDevice.Info[]) list.toArray(new MidiDevice.Info[0]);
    }

    /** Returns an array of MidiDevice.Info for MIDI Sequencer device. */
    // not used now
    private MidiDevice.Info[] createSequencerMidiDeviceInfo()
	throws MidiUnavailableException {
	ArrayList list = new ArrayList();

	MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
        for (int i = 0; i < infos.length; i++) {
	    // throws MidiUnavailableException
	    MidiDevice device = MidiSystem.getMidiDevice(infos[i]);
	    if (device instanceof Sequencer)
		list.add(infos[i]);
	}
	return (MidiDevice.Info[]) list.toArray(new MidiDevice.Info[0]);
    }

    /** Returns an array of MidiDevice.Info for MIDI Synthesizer device. */
    // not used now
    private static MidiDevice.Info[] createSynthesizerMidiDeviceInfo()
	throws MidiUnavailableException {
	ArrayList list = new ArrayList();

	MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
        for (int i = 0; i < infos.length; i++) {
	    // throws MidiUnavailableException
	    MidiDevice device = MidiSystem.getMidiDevice(infos[i]);
	    if (device instanceof Synthesizer)
		list.add(infos[i]);
	}
	return (MidiDevice.Info[]) list.toArray(new MidiDevice.Info[0]);
    }

    /**
     * return an array of MidiDevice.Info for MIDI output.
     * @see #getOutputMidiDeviceInfo(int)
     * @see #getReceiver
     * @see #send
     */
    static MidiDevice.Info[] getOutputMidiDeviceInfo() {
	return outputMidiDeviceInfo;
    }

    /**
     * return an array of MidiDevice.Info for MIDI input.
     * @see #getInputMidiDeviceInfo(int)
     * @see #clearSysexInputQueue
     * @see #getMessage
     */
    static MidiDevice.Info[] getInputMidiDeviceInfo() {
	return inputMidiDeviceInfo;
    }

    /**
     * return an entry of MidiDevice.Info for MIDI output.
     * @see #getOutputMidiDeviceInfo()
     * @see #getReceiver
     * @see #send
     */
    static MidiDevice.Info getOutputMidiDeviceInfo(int i) {
	return outputMidiDeviceInfo[i];
    }

    /**
     * return an entry of MidiDevice.Info for MIDI input.
     * @see #getInputMidiDeviceInfo()
     * @see #clearSysexInputQueue
     * @see #getMessage
     */
    static MidiDevice.Info getInputMidiDeviceInfo(int i) {
	return inputMidiDeviceInfo[i];
    }

    /** for SynthTabelModel (will be obsoleted) */
    static int getOutPort(MidiDevice.Info info) {
	for (int i = 0; i < outputMidiDeviceInfo.length; i++) {
	    if (outputMidiDeviceInfo[i] == info)
		return i;
	}
	return -1;
    }

    /** for SynthTabelModel (will be obsoleted) */
    static int getInPort(MidiDevice.Info info) {
	for (int i = 0; i < inputMidiDeviceInfo.length; i++) {
	    if (inputMidiDeviceInfo[i] == info)
		return i;
	}
	return -1;
    }

    /**
     * get MidiDevice for Output.
     *
     * @param port an index in an array returned by
     * <code>getOutputMidiDeviceInfo()</code>.
     * @return a <code>MidiDevice</code> object for MIDI output.  The
     * MidiDevice is already opened.
     * @see #getOutputMidiDeviceInfo()
     * @see #getReceiver
     * @see #send
     */
    private static MidiDevice getOutputMidiDevice(int port)
	throws MidiUnavailableException {
	MidiDevice dev = null;

	dev = MidiSystem.getMidiDevice(outputMidiDeviceInfo[port]);
	if (!dev.isOpen()) {
	    ErrorMsg.reportStatus("open outport: "
				  + dev.getDeviceInfo().getName());
	    dev.open();
	}
	return dev;
    }

    /**
     * get a Receiver for Output.
     *
     * @param port an index in an array returned by
     * <code>getOutputMidiDeviceInfo()</code>.
     * @return a <code>Receiver</code> object for MIDI output.
     * @see #getOutputMidiDeviceInfo()
     * @see #getReceiver
     * @see #send
     * @throws MidiUnavailableException
     */
    static Receiver getReceiver(int port) throws MidiUnavailableException {
	if (midiOutRcvr[port] != null)
	    return midiOutRcvr[port];

	MidiDevice dev = getOutputMidiDevice(port);

	Receiver r = dev.getReceiver();
	midiOutRcvr[port] = r;
	return r;
    }

    /**
     * get MidiDevice for Input.
     *
     * @param port an index in an array returned by
     * <code>getInputMidiDeviceInfo()</code>.
     * @return a <code>MidiDevice</code> object for MIDI input.  The
     * MidiDevice is already opened.
     * @see #getInputMidiDeviceInfo()
     * @see #clearSysexInputQueue
     * @see #getMessage
     */
    private static MidiDevice getInputMidiDevice(int port) {
	MidiDevice dev = null;
	try {
	    dev = MidiSystem.getMidiDevice(inputMidiDeviceInfo[port]);
	    if (!dev.isOpen()) {
		ErrorMsg.reportStatus("open inport: "
				      + dev.getDeviceInfo().getName());
		dev.open();
	    }
	} catch (MidiUnavailableException e) {
	    ErrorMsg.reportStatus(e);
	}
	return dev;
    }

    /**
     * get a Transmitter for Input.
     *
     * @param port an index in an array returned by
     * <code>getInputMidiDeviceInfo()</code>.
     * @return a <code>Transmitter</code> object for MIDI input.
     * @see #getInputMidiDeviceInfo()
     * @see #clearSysexInputQueue
     * @see #getMessage
     */
    static Transmitter getTransmitter(int port) {
	// Transmitter cannot be shared.
	MidiDevice dev = getInputMidiDevice(port);
	try {
	    return dev.getTransmitter();
	} catch (MidiUnavailableException e) {
	    ErrorMsg.reportStatus(e);
	}
	return null;
    }

    /**
     * Setup an input queue for MIDI System Exclusive Message input.
     * The input queue is shared.  If the input queue is already
     * opened, nothing is done.
     * @see #clearSysexInputQueue
     */
    static void setSysexInputQueue(int port) {
	if (sysexInputQueue[port] != null)
	    return;
	MidiUtil.SysexInputQueue rcvr = new MidiUtil.SysexInputQueue();
	Transmitter trns;
	try {
	    trns = getInputMidiDevice(port).getTransmitter();
	    trns.setReceiver(rcvr);
	    sysexInputQueue[port] = rcvr;
	} catch (MidiUnavailableException e) {
	    ErrorMsg.reportStatus(e);
	}
    }

    /**
     * clear MIDI input queue specified.  Internally
     * setSysexInputQueue(port) is called.
     * @see #getInputMidiDeviceInfo()
     * @see #setSysexInputQueue
     */
    static void clearSysexInputQueue(int port) {
	setSysexInputQueue(port);
	sysexInputQueue[port].clearQueue();
    }

    /**
     * return <code>true</code> when MIDI input queue is empty.
     * @see #getInputMidiDeviceInfo()
     * @see #clearSysexInputQueue
     */
    static boolean isSysexInputQueueEmpty(int port) {
	return sysexInputQueue[port].isEmpty();
    }

    /**
     * get Sysex Message from MIDI input queue.
     * @see #getInputMidiDeviceInfo()
     * @see #clearSysexInputQueue
     */
    static MidiMessage getMessage(int port, long timeout)
	throws MidiUtil.TimeoutException, InvalidMidiDataException {
	return sysexInputQueue[port].getMessage(timeout);
    }

    /**
     * Converts a byte array into an array of SysexMessages.  Each
     * SysexMessage must be terminated by END_OF_EXCLUSIVE.
     */
    public static SysexMessage[] byteArrayToSysexMessages(byte[] d)
	throws InvalidMidiDataException {
	ArrayList list = new ArrayList();

        for (int i = 0; i < d.length; i++) {
	    if ((int) (d[i] & 0xFF) == SysexMessage.SYSTEM_EXCLUSIVE) {
		int j;
		// let cause exception if there is no END_OF_EXCLUSIVE
		for (j = i + 1; (int) (d[j] & 0xff) != ShortMessage.END_OF_EXCLUSIVE; j++)
		    ;
		// here d[j] is EOX.
		j++;
		int l = j - i;
		byte[] b = new byte[l];
		System.arraycopy(d, i, b, 0, l);
		SysexMessage m = new SysexMessage();
		m.setMessage(b, l);
		list.add(m);
		i = j;
	    }
	}
	return (SysexMessage[]) list.toArray(new SysexMessage[0]);
    }

    /**
     * Javasound 1.4.2 has a bug which returns
     * com.sun.media.sound.FastShortMessage object instead of
     * ShortMessage.
     */
    public static MidiMessage fixShortMessage(MidiMessage msg)
	throws InvalidMidiDataException {
	// We cannot use
	//   "msg instanceof com.sun.media.sound.FastShortMessage"
	// since we don't have the class.
	if (msg.getClass().toString().equals("class com.sun.media.sound.FastShortMessage"))
	    return (MidiMessage) conv(msg);
	else
	    return msg;
    }

    /**
     * Convert a <code>com.sun.media.sound.FastShortMessage</code>
     * object to a <code>ShortMessage</code> object.
     */
    private static ShortMessage conv(MidiMessage mm)
	throws InvalidMidiDataException {
	ShortMessage m = (ShortMessage) mm;
	ShortMessage msg = new ShortMessage();
	int c = m.getStatus();
	switch (c < 0xf0 ? c & 0xf0 : c) {
	case 0x80: case 0x90: case 0xa0: case 0xb0:
	case 0xe0: case 0xf2:
	    msg.setMessage(c, m.getData1(), m.getData2());
	    break;
	case 0xc0: case 0xd0: case 0xf1: case 0xf3:
	    msg.setMessage(c, m.getData1(), 0);
	    break;
	case 0xf4: case 0xf5: case 0xf6: case 0xf7:
	case 0xf8: case 0xf9: case 0xfa: case 0xfb:
	case 0xfc: case 0xfd: case 0xfe: case 0xff:
	    msg.setMessage(c);
	    break;
	default:
	    throw new InvalidMidiDataException();
	}
	return msg;
    }

    /**
     * MIDI Output buffer size.  If set to '0', Whole Sysex data is
     * sent in one packet.  Set '0' unless you have problem.
     */
    private static final int BUFSIZE = 0;

    /**
     * Send a <code>MidiMessage</code>.  If BUFSIZE is non-zero, data
     * size will be limited to the size.
     */
    public static void send(Receiver rcv, MidiMessage msg)
	throws MidiUnavailableException, InvalidMidiDataException {
	int size = msg.getLength();

	if (BUFSIZE == 0 || size <= BUFSIZE) {
	    rcv.send(msg, -1);
	    log("XMIT: ", msg);
	} else {
	    // divide large System Exclusive Message into multiple
	    // small messages.
	    byte[] sysex = msg.getMessage();
	    byte[] tmpArray = new byte[BUFSIZE + 1];
	    for (int i = 0; size > 0; i += BUFSIZE, size -= BUFSIZE) {
		int s = Math.min(size, BUFSIZE);

		if (i == 0) {
		    System.arraycopy(sysex, i, tmpArray, 0, s);
		    ((SysexMessage) msg).setMessage(tmpArray, s);
		} else {
		    tmpArray[0] = (byte) SysexMessage.SPECIAL_SYSTEM_EXCLUSIVE;
		    System.arraycopy(sysex, i, tmpArray, 1, s);
		    ((SysexMessage) msg).setMessage(tmpArray, ++s);
		}
		rcv.send(msg, -1);
		log("XMIT: ", msg);
	    }
	}
    }

    //
    // MIDI Data Input
    //
    static class InputQueue implements Receiver {
	private List list = Collections.synchronizedList(new LinkedList());

	InputQueue() {
	}

	//Receiver interface
	public void close() {
	}

	public void send(MidiMessage msg, long timeStamp) {
	    //ErrorMsg.reportStatus("InputQueue: " + msg);
	    list.add(msg);
	    log("XMIT: ", msg);
	}

	void clearQueue() {
	    list.clear();
	}

	boolean isEmpty() {
	    return list.size() == 0;
	}

	MidiMessage getMessage() throws
	    InvalidMidiDataException, TimeoutException {
	    // pop the oldest message
	    MidiMessage msg = (MidiMessage) list.remove(0);
	    // for java 1.4.2 bug
	    msg = (MidiMessage) MidiUtil.fixShortMessage(msg);
	    log("RECV: ", msg);
	    return msg;
	}
    }

    static class SysexInputQueue extends InputQueue {
	public void send(MidiMessage msg, long timeStamp) {
	    int status = msg.getStatus();
	    if ((status == SysexMessage.SYSTEM_EXCLUSIVE)
		|| (status == SysexMessage.SPECIAL_SYSTEM_EXCLUSIVE)) {
		super.send(msg, timeStamp);
	    }
	}

	// for 32KB sysex message which is biggest we know.
	private static final int DEFAULT_TIMEOUT = 10000;
	private static final int MIN_TIMEOUT = 1000;

	MidiMessage getMessage(long timeout)
	    throws TimeoutException, InvalidMidiDataException {
	    long start = System.currentTimeMillis();
	    byte [] buffer = {};
	    int totalLen = 0;
	    boolean firstMsg = true;
	    if (timeout == 0)
		timeout = DEFAULT_TIMEOUT;
	    else if (timeout < MIN_TIMEOUT)
		timeout = MIN_TIMEOUT;
	    do {
		// wait for data
		while (isEmpty()) {
		    try {
			Thread.sleep(10); // define const!!!FIXIT!!!
		    } catch (InterruptedException e) {
			;	// ignore
		    }
		    if (System.currentTimeMillis() - start > timeout)
			throw new TimeoutException();
		}
		MidiMessage msg = super.getMessage();
		if (msg == null)
		    throw new InvalidMidiDataException(); // !!!add info
		int len = msg.getLength();
		if (firstMsg) {
		    if (msg.getStatus() != SysexMessage.SYSTEM_EXCLUSIVE)
			// this is illegal and just ignore
			continue;
		    buffer = msg.getMessage();
		    totalLen = len;
		    if (buffer[totalLen - 1] == (byte) ShortMessage.END_OF_EXCLUSIVE)
			return msg;
		    firstMsg = false;
		} else {
		    int status = msg.getStatus();
		    // take the Real Time messages (0xf8-0xff) out of
		    // the messages a MidiWrapper returns.
		    if ((status & 0xf8) == 0xf8)
			continue;
		    // throw an Exception, if an exclusive message is
		    // terminated by "any other Status byte (except
		    // Real Time messages)".
		    // THIS IS NOT CORRECT BEHAVIOR. !!!FIXIT!!!
		    if (status != SysexMessage.SPECIAL_SYSTEM_EXCLUSIVE)
			throw new InvalidMidiDataException(); // add info !!!
		    // Combine the newly-read stuff into an new array
		    // with the existing stuff
		    byte [] buf = msg.getMessage();
		    byte[] combineBuffer = new byte[totalLen + len];
		    System.arraycopy(buffer,  0, combineBuffer, 0, totalLen);
		    if (len == 1) { // I think this is javax.sound.midi bug.
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
	    return (MidiMessage) sysexmsg;
	}

	MidiMessage getMessage()
	    throws TimeoutException, InvalidMidiDataException {
	    return getMessage(DEFAULT_TIMEOUT);
	}
    }

    public static class TimeoutException extends Exception {
	public TimeoutException() {
	    super();
	    //super("Timeout on MIDI input port " + getInputDeviceName(port));
	}
    }

    /**
     * Convert <code>SysexMessage</code> into a hexa-dump string.
     *
     * @param m a <code>SysexMessage</code> value
     * @param bytes number of bytes per line.  If equal or less than
     * 0, no newlines are inserted.
     * @return a <code>String</code> value
     */
    public static String sysexMessageToString(SysexMessage m, int bytes) {
	byte[] d = m.getMessage();
	return Utility.hexDump(d, 0, -1, bytes);
    }

    /**
     * Convert <code>SysexMessage</code> into a hexa-dump string.  If
     * the length is longer than 16bytes, bytes of middle of the array
     * are not ignored.
     *
     * @param m a <code>SysexMessage</code> value
     * @return a <code>String</code> value
     * @exception InvalidMidiDataException if an error occurs
     */
    public static String sysexMessageToString(SysexMessage m)
	throws InvalidMidiDataException {
	byte[] d = m.getMessage();
	return Utility.hexDumpOneLine(d, 0, -1, 16);
    }

    private static String hex(int c) {
	String s = Integer.toHexString(c);
	return s.length() == 1 ? "0" + s : s;
    }

    /**
     * Convert <code>ShortMessage</code> into a hexa-dump string.
     *
     * @param m a <code>ShortMessage</code> value
     * @return a <code>String</code> value
     * @exception InvalidMidiDataException if an error occurs
     */
    public static String shortMessageToString(ShortMessage m)
	throws InvalidMidiDataException {
	int c = m.getStatus();
	switch (c < 0xf0 ? c & 0xf0 : c) {
	case 0x80: case 0x90: case 0xa0: case 0xb0:
	case 0xe0: case 0xf2:
	    return (hex(c) + " "
		    + hex(m.getData1()) + " " + hex(m.getData2()));
	case 0xc0: case 0xd0: case 0xf1: case 0xf3:
	    return (hex(c) + " " + hex(m.getData1()));
	case 0xf4: case 0xf5: case 0xf6: case 0xf7:
	case 0xf8: case 0xf9: case 0xfa: case 0xfb:
	case 0xfc: case 0xfd: case 0xfe: case 0xff:
	    return (hex(c));
	default:
	    throw new InvalidMidiDataException();
	}
    }

    /**
     * Return a <code>String</code> of the name of status byte of a
     * <code>MidiMessage</code>.
     *
     * @param m a <code>MidiMessage</code> value
     * @return a <code>String</code> value
     * @exception InvalidMidiDataException if an error occurs
     */
    public static String statusString(MidiMessage m)
	throws InvalidMidiDataException {
	int c = m.getStatus();
	switch (c < 0xf0 ? c & 0xf0 : c) {
	case 0x80: return "Note Off";
	case 0x90: return "Note On";
	case 0xa0: return "Poly Pressure";
	case 0xb0: return "Control Change";
	case 0xc0: return "Program Change";
	case 0xd0: return "Channel Pressure";
	case 0xe0: return "Pitch Bend";
	case 0xf0: return "System Exclusive";
	case 0xf1: return "MIDI Time Code";
	case 0xf2: return "Song Position Pointer";
	case 0xf3: return "Song Select";
	case 0xf4: return "Undefined";
	case 0xf5: return "Undefined";
	case 0xf6: return "Tune Request";
	//case 0xf7: return "End of System Exclusive";
	case 0xf7: return "Special System Exclusive";
	case 0xf8: return "Timing Clock";
	case 0xf9: return "Undefined";
	case 0xfa: return "Start";
	case 0xfb: return "Continue";
	case 0xfc: return "Stop";
	case 0xfd: return "Undefined";
	case 0xfe: return "Active Sensing";
	case 0xff: return "System Reset";
	default: throw new InvalidMidiDataException();
	}
    }

    /**
     * convert <code>MidiMessage</code> into a string.
     *
     * @param m a <code>MidiMessage</code> value
     * @return a <code>String</code> value
     */
    public static String midiMessageToString(MidiMessage m)
	throws InvalidMidiDataException {
	if (m instanceof ShortMessage)
	    return (statusString(m) + "\n  "
		    + shortMessageToString((ShortMessage) m));
	else if (m instanceof SysexMessage) {
	    if (CSMstate) {
		return ("SysEX:length="
			+ m.getLength() + "\n  "
			+ sysexMessageToString((SysexMessage) m, 16));
	    } else {
	    	return ("SysEX:length="
			+ m.getLength() + "\n  "
			+ sysexMessageToString((SysexMessage) m));
	    }
	} else
	    throw new InvalidMidiDataException();
    }

    //
    // MidiMonitor Utilities
    //
    /**
     * Output string to MIDI Monitor Window.
     *
     * @param s string to be output
     */
    public static void log(String s) {
	PatchEdit.midiMonitorLog(s);
    }

    public static void log(String str, MidiMessage msg) {
	try {
	    log(str + midiMessageToString(msg) + "\n");
	} catch	(InvalidMidiDataException e) {
	    log("InvalidMidiDataException: " + msg + "\n");
	}
    }

    /**
     * Dump output MidiMessage <code>msg</code> on the MIDI Monitor
     * Window with port number information.
     *
     * @param port port number
     * @param msg MidiMessage
     * @exception InvalidMidiDataException if an error occurs
     */
    public static void logIn(int port, MidiMessage msg) {
	log("Port: " + port + " RECV ", msg);
    }

    public static void logIn(Transmitter trns, MidiMessage msg) {
	log(trns.toString() + " RECV ", msg);
    }

    /**
     * Dump input MidiMessage <code>msg</code> on the MIDI Monitor
     * Window with port number information.
     *
     * @param port port number
     * @param msg MidiMessage
     * @exception InvalidMidiDataException if an error occurs
     */
    public static void logOut(int port, MidiMessage msg) {
	log("Port: " + port + " XMIT ", msg);
    }

    public static void logOut(Receiver rcv, MidiMessage msg) {
	log(rcv.toString() + " XMIT ", msg);
    }

    /**
     * Dump input MIDI <code>sysex</code> byte array one the MIDI
     * Monitor Window with port number information.
     *
     * @param port port number
     * @param sysex byte array
     * @param length length of data to dump
     */
    public static void logIn(int port, byte[] sysex, int length) {
	log(port, " RECV ", sysex, length);
    }

    /**
     * Dump output MIDI <code>sysex</code> byte array one the MIDI
     * Monitor Window with port number information.
     *
     * @param port port number
     * @param sysex byte array
     * @param length length of data to dump
     */
    public static void logOut(int port, byte[] sysex, int length) {
	log(port, " XMIT ", sysex, length);
    }

    private static void log(int port, String dir, byte[] sysex, int length) {
	if (CSMstate) {
	    log("Port: " + port + dir + length + " bytes :\n  "
	        + Utility.hexDump(sysex, 0, length, 16) + "\n");
	} else {
            log("Port: " + port + dir + length + " bytes :\n  "
		+ Utility.hexDumpOneLine(sysex, 0, length, 16) + "\n");
	}
    }

    /**
     * Get the state of displaying Midi Messages in the MIDI Monitor.
     * (Complete Sysex Message)
     */
    static boolean getCSM() { return CSMstate; }

    /**
     * Toggle the state of displaying Midi messages in the MIDI
     * Monitor.  (Complete Sysex Message)
     */
    static void toggleCSM() { CSMstate = !CSMstate; }

    /** Only for debugging. */
    public static void main(String[] args) throws InvalidMidiDataException {
	int[] id = {
	    // Sysex Message
	    0xf0, 0, 1, 2, 3, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,
	    16, 17, 18, 0xf7,
	    // MIDI 1.0 Detailed Specification 4.2 page A-1
	    0x90, 0x3c, 0x27, 0x40, 0x2b, 0x43, 0x25,
	    0x90, 0x3c, 0x27, 0x3c, 0x00, 0x3e, 0x29,
	    // MIDI 1.0 Detailed Specification 4.2 page A-2
	    0xb0, 0x7c, 0x00, 0x01, 0x37,
	    // sysex
	    0xf0, 0, 1, 2, 3, 0xf7,
	    // note on, running status
	    0x80, 3, 4, 6, 7,
	    // control change
	    0xbf, 8, 9,
	    // program chage, channel presure, RS
	    0xc0, 0, 0xdf, 1, 2,
	    // pitch bend x 3
	    0xe0, 5, 6, 0xef, 7, 8, 9, 0,
	    // system real time
	    0xF8, 0xFF,
	    // sysex + system real time
	    0xf0, 0, 1, 2, 0xf8, 0xf7,
	    0xf0, 0, 1, 0xf8, 2, 0xf7,
	    0xf0, 0, 0xf8, 1, 2, 0xf7,
	    // system common
	    0xf1, 3, 0xf2, 4, 5, 0xf3, 6, 0xf4, 0xf5, 0xf6, 0xf7,
	    //256, -1,
	};
	byte[] bd = new byte[id.length];
	for (int i = 0; i < id.length; i++)
	    bd[i] = (byte) id[i];

	System.out.println(Utility.hexDump(bd, 0, -1, 16));
	System.out.println("------------");
	System.out.println(Utility.hexDump(bd, 5, 12, 4));
	System.out.println("------------");

	SysexMessage sysex = new SysexMessage();
	System.out.println(sysexMessageToString(sysex, 5));
	System.out.println("------------");
	sysex.setMessage(bd, 15);
	System.out.println(sysexMessageToString(sysex));
	sysex.setMessage(bd, 16);
	System.out.println(sysexMessageToString(sysex));
	sysex.setMessage(bd, 17);
	System.out.println(sysexMessageToString(sysex));
	sysex.setMessage(bd, 20);
	System.out.println(sysexMessageToString(sysex));

	ShortMessage smsg = new ShortMessage();
	smsg.setMessage(ShortMessage.NOTE_ON, 0x4B, 0x70); // 2B
	System.out.println(shortMessageToString(smsg));
	smsg.setMessage(ShortMessage.PROGRAM_CHANGE, 0x4B, 0x70); // 1B
	System.out.println(shortMessageToString(smsg));
	smsg.setMessage(ShortMessage.TIMING_CLOCK, 0x4B, 0x70);
	System.out.println(shortMessageToString(smsg));

	System.out.println(midiMessageToString(sysex));
	smsg.setMessage(ShortMessage.MIDI_TIME_CODE, 0x4B, 0x70); // 1B
	System.out.println(midiMessageToString(smsg));
	MidiMessage msg = (MidiMessage) new ShortMessage();
	((ShortMessage) msg).setMessage(ShortMessage.SONG_POSITION_POINTER, 0x4B, 0x70); // 2B
	System.out.println(midiMessageToString(msg));
    }

} // MidiUtil
