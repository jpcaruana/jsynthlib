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
 * MIDI Utility Routines.
 *
 * Created: Mon Feb 02 01:14:09 2004
 *
 * @author Hiroo Hayashi
 * @version $Id$
 */
public final class MidiUtil {

    /**
     * holds the state if a SysexMessage is completely displayed or
     * shorten to one hexdump line.
     */
    private static boolean CSMstate=false;

    private MidiUtil() {
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
		int l = j - i;
		byte[] b = new byte[l];
		System.arraycopy(d, i, b, 0, l);
		SysexMessage m = new SysexMessage();
		m.setMessage(b, l);
		list.add(m);
		i = j;
	    }
	}
	return (SysexMessage[]) list.toArray();
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

    /** Returns an array of MidiDevice.Info for MIDI input device. */
    public static MidiDevice.Info[] getInputMidiDeviceInfo()
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
	return (MidiDevice.Info[]) list.toArray();
    }

    /** Returns an array of MidiDevice.Info for MIDI output device. */
    public static MidiDevice.Info[] getOutputMidiDeviceInfo()
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
	return (MidiDevice.Info[]) list.toArray();
    }

    /** Returns an array of MidiDevice.Info for MIDI Sequencer device. */
    public static MidiDevice.Info[] getSequencerMidiDeviceInfo()
	throws MidiUnavailableException {
	ArrayList list = new ArrayList();

	MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
        for (int i = 0; i < infos.length; i++) {
	    // throws MidiUnavailableException
	    MidiDevice device = MidiSystem.getMidiDevice(infos[i]);
	    if (device instanceof Sequencer)
		list.add(infos[i]);
	}
	return (MidiDevice.Info[]) list.toArray();
    }

    /** Returns an array of MidiDevice.Info for MIDI Synthesizer device. */
    public static MidiDevice.Info[] getSynthesizerMidiDeviceInfo()
	throws MidiUnavailableException {
	ArrayList list = new ArrayList();

	MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
        for (int i = 0; i < infos.length; i++) {
	    // throws MidiUnavailableException
	    MidiDevice device = MidiSystem.getMidiDevice(infos[i]);
	    if (device instanceof Synthesizer)
		list.add(infos[i]);
	}
	return (MidiDevice.Info[]) list.toArray();
    }

    //
    // convert MidiMessage or byte array into String
    //
    private static String hex(int c) {
	String s = Integer.toHexString(c);
	return s.length() == 1 ? "0" + s : s;
    }

    /**
     * convert a byte array into a hexa-dump string.
     *
     * @param d a <code>byte[]</code> array to be converted.
     * @param offset array index from which dump starts.
     * @param len number of bytes to be dumped.  If -1, dumps to the
     * end of the array.
     * @param bytes number of bytes per line.  If equal or less than
     * 0, no newlines are inserted.
     * @return hexa-dump string.
     */
    public static String hexDump(byte[] d, int offset, int len, int bytes) {
	StringBuffer buf = new StringBuffer();
	if (len == -1 || offset + len > d.length)
	    len = d.length - offset;
	for (int i = 0; i < len; i++) {
	    int c = (int) (d[offset + i] & 0xff);
	    if (c < 0x10)
		buf.append("0");
	    buf.append(Integer.toHexString(c));
	    if (bytes > 0
		&& (i % bytes == bytes - 1 && i != len - 1))
		buf.append("\n  ");
	    else if (i != len - 1)
		buf.append(" ");
	}
	return buf.toString();
    }

    /**
     * convert a byte array into a one-line hexa-dump string.
     *
     * @param d a <code>byte[]</code> array to be converted.
     * @param offset array index from which dump starts.
     * @param len number of bytes to be dumped.  If -1, dumps to the
     * end of the array.
     * @param bytes number of columns (bytes) per line.
     * @return hexa-dump string.
     */
    public static String hexDumpOneLine(byte[] d, int offset, int len, int bytes) {
	if (len == -1 || len > d.length - offset)
	    len = d.length - offset;

	if (len <= bytes || len < 8)
	    return hexDump(d, offset, len, 0);

	StringBuffer buf = new StringBuffer();
	for (int i = 0; i < bytes - 4; i++) {
	    int c = (int) (d[offset + i] & 0xff);
	    if (c < 0x10)
		buf.append("0");
	    buf.append(Integer.toHexString(c) + " ");
	}
	buf.append("..");
	for (int i = len - 3; i < len; i++) {
	    int c = (int) (d[offset + i] & 0xff);
	    buf.append(" ");
	    if (c < 0x10)
		buf.append("0");
	    buf.append(Integer.toHexString(c));
	}
	return buf.toString();
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
	return hexDump(d, 0, -1, bytes);
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
	return hexDumpOneLine(d, 0, -1, 16);
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
	    if (CSMstate == true) {
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

    /**
     * Dump output MidiMessage <code>msg</code> on the MIDI Monitor
     * Window with port number information.
     *
     * @param port port number
     * @param msg MidiMessage
     * @exception InvalidMidiDataException if an error occurs
     */
    public static void logIn(int port, MidiMessage msg) {
	log(port, " RECV ", msg);
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
	log(port, " XMIT ", msg);
    }

    private static void log(int port, String dir, MidiMessage msg) {
	try {
	    log("Port: " + port + dir + midiMessageToString(msg) + "\n");
	} catch	(InvalidMidiDataException e) {
	    log("InvalidMidiDataException: " + msg + "\n");
	}
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
	if (CSMstate == true ) {
	    log("Port: " + port + dir + length + " bytes :\n  "
	        + hexDump(sysex, 0, length, 16) + "\n");
	} else {
            log("Port: " + port + dir + length + " bytes :\n  "
		+hexDumpOneLine(sysex, 0, length, 16) + "\n");
	}
    }

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

	System.out.println(hexDump(bd, 0, -1, 16));
	System.out.println("------------");
	System.out.println(hexDump(bd, 5, 12, 4));
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

    /**
     * Get the state of displaying Midi Messages.
     * (Completely or Shorten Message)
     */
    static boolean getCSM() { return CSMstate; }

    /**
     * Toggle the state of displaying Midi messages.
     * (Completely or Shorten Message)
     */
    static void toggleCSM() { CSMstate = !CSMstate; }

} // MidiUtil
