/*
 * LinuxCharDevMidiDevice.java - This file is part of JSynthLib
 * ============================================================
 * 
 * @version $Id$
 * @author  Torsten Tittmann
 *
 * Copyright (C) 2004 Torsten.Tittmann@gmx.de
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */

package LinuxCharDevMidiProvider;

import java.util.*;
import java.io.*;
import javax.sound.midi.*;


public class LinuxCharDevMidiDevice implements MidiDevice {
	private static 	String		devicePath;
	private static 	boolean		open        = false;
	private static 	InputStream	inStream    =null;
	private static 	OutputStream	outStream   =null;
	private 	Info		info;
	private 	InputThread	inThread;
	private 	List 		destinations = new ArrayList();
	
	public LinuxCharDevMidiDevice (String devicePath) {
		if ( open==false ) {
			this.devicePath = devicePath;
	
			this.info = new Info(devicePath, "JSynthLib", "Linux Character Device Interface", "0.1");

			inThread = new InputThread();
		}
	}
	
	public void open() {
		File file = new java.io.File(devicePath);
		try {
			/* TODO(if possible)! check if device file isn't occupied by an other process */
			inStream  = new FileInputStream (file);
			outStream = new FileOutputStream(file);
			if ( inStream != null && outStream != null  ) open = true;
		} catch (Exception e) {
			System.out.println("LinuxCharDevMidiDevice.open(): Exception: "+e);
			e.printStackTrace();
		}
			
		inThread.start(); /* start listening on the OS midi device to get MidiMessages */
	}
	public void close() {
		try {
			inThread.stopRunning();
			inStream.close();
			outStream.close();
			open = false;
		} catch (IOException e) {}
	}
	public boolean isOpen() {
		return open;
	}
	public MidiDevice.Info getDeviceInfo() {
		return info;
	}
	public int getMaxTransmitters() {
		return -1;
	}
	public int getMaxReceivers() {
		return -1;
	}
	public long getMicrosecondPosition() {
		return -1;	// not supported and not needed
	}
	public Transmitter getTransmitter() {
		return new LinuxCharDevTransmitter();
	}
	public Receiver getReceiver() {
		return new LinuxCharDevReceiver();
	}


	/*
	 * write MidiSystemMessages to OS midi device
	 * (we don't support timeStamp, it isn't needed yet)
	 */
	private void write(MidiMessage message, long timeStamp) throws IOException {
		byte[] sysex = message.getMessage();
		int length = message.getLength();

		if (outStream!=null) {
			final int BUFSIZE = 256;
			//final int BUFSIZE = 0;	// doesn't work properly!!!
													       
			if (BUFSIZE == 0) {
				synchronized (outStream) {
					outStream.write(sysex,0,length);
					outStream.flush ();
				}
			} else {
				byte[] tmpArray = new byte[BUFSIZE+1];
			
				for (int i = 0; length > 0; i += BUFSIZE, length -= BUFSIZE) {
					int s = Math.min(length, BUFSIZE);
												       
					System.arraycopy(sysex, i, tmpArray, 0, s);
					synchronized (outStream) {
						outStream.write(tmpArray,0,s);
						outStream.flush ();
					}
				}
			}
		}
	}


	/* 
	 * transmits complete MidiMessages to the connected MidiSystemReceiver(s) 
	 * (we don't support timeStamp, it isn't needed yet)
	 */
	void transmit(MidiMessage message) {
		Iterator iterator = destinations.iterator();

		while (iterator.hasNext()) {
			((Receiver)iterator.next()).send(message, (long)(-1));
		}
	}

	     
	/*     
	 * A Transmitter get MidiMessages from the OS Midi output interface and output these to the JAVA MidiSystem
	 */
	public class LinuxCharDevTransmitter implements Transmitter {
		private Receiver    outPort;
	
		public LinuxCharDevTransmitter () {
		}
		public void setReceiver(Receiver receiver) {
			this.outPort = receiver;
			destinations.add(outPort);
		}
		public Receiver getReceiver() {
			return outPort;
		}
		public void close() {
			destinations.remove(outPort);
		}
	} /* End of inner class LinuxCharDevTransmitter */

	
	/* 
	 * A Receiver get MidiMessages from the JAVA MidiSystem and output these to the OS Midi output interface
	 */
	public class LinuxCharDevReceiver implements Receiver  {
	
		public LinuxCharDevReceiver () {
		}

		public void send(MidiMessage message, long timeStamp) {
			// output only these two kinds of messages
			if (   message instanceof ShortMessage
			    || message instanceof SysexMessage) {
				try {
					write(message, timeStamp);
				} catch (IOException e) {}
			} else {
				// MetaMessages etc.
				// DO NOTHING!!
			}
		}
		public void close() {
		}
	} /* End of inner class LinuxCharDevReceiver */

	
	/*
	 * inner class Info 
	 */
	public class Info extends MidiDevice.Info {
		public Info(String name, String vendor, String description, String version) {
			super(name, vendor, description, version);
		}
	}  /* End of inner class Info */

	
	/*
	 * This defines the Thread which is reading the OS device
	 *
	 * Since the whole LinuxCharDevMidiDeviceProvider is derived from
	 * JSynthLibs core.LinuxMidiWrapper this part is taken from there
	 * and adapted to our needs
	 *
	 * This inner class used to implement Runnable but I changed it so that it
	 * extends Thread so that we can avoid having to do "new Thread(new InputThread())"
	 * in the LinuxMidiWrapper class. Besides, the option for implementing Runnable
	 * was created for cases where you were already extending another class and
	 * couldn't extend Thread - emenaker 2003.03.12
	 *
	 * Since Joe's initial thread code causes an exception while using start(), I changed
	 * the code considering SUN's recommendations in the document:
	 * http://java.sun.com/j2se/1.4.2/docs/guide/misc/threadPrimitiveDeprecation.html
	 * ttittmann 13. Januar 2004
	 */
	private class InputThread extends Thread {

		private volatile Thread InputMidi;

		public void start() {
			InputMidi = new Thread(this);
			InputMidi.start();
		}

		public void run () {
			Thread thisThread=Thread.currentThread();
			int c;

			while (InputMidi==thisThread) {
				if (inStream==null)
					break;
				try {
					c = inStream.read();
					// read() does not block as documented.
					if (c == -1) {
					    Thread.sleep(10);
					    continue;
					}
					// Ignore Active Sensing & Timing
					if (c == ShortMessage.ACTIVE_SENSING
					    || c == ShortMessage.TIMING_CLOCK
					    || c == ShortMessage.SYSTEM_RESET)
						continue;
					addToList(c);
				} catch (Exception ex) {}
			}
		}

		
		/**
		 * convert a MIDI byte stream into MidiMessage objects
		 * and add them to input list, <code>list</code>.
		 *
		 * @author Hiroo Hayashi
		 * @see "MIDI 1.0 Detailed Specification, Page A-3"
		 * @See javax.sound.midi.SystemMessage
		 */
		private int runningStatus = 0;
		private boolean thirdByte = false;
		private static final int BUFSIZE = 1024;
		private byte[] buf = new byte[BUFSIZE]; // data buffer
		private int size; // only for Sysex

		private void addToList(int c) throws InvalidMidiDataException {
		    MidiMessage msg;
		    if ((c & ~0xff) != 0)
			throw new InvalidMidiDataException();
		    if ((c & 0x80) == 0x80) {
			// status byte
			if ((c & 0xf8) == 0xf8) { // System Real Time Message
			    // 0 byte message
			    msg = (MidiMessage) new ShortMessage();
			    ((ShortMessage) msg).setMessage(c);
			    transmit(msg);
			} else {
			    thirdByte = false;
			    switch (c) {
			    case ShortMessage.END_OF_EXCLUSIVE:
				if (runningStatus == SysexMessage.SYSTEM_EXCLUSIVE) {
				    byte[] d = new byte[size + 1];
				    System.arraycopy(buf, 0, d, 0, size);
				    d[size++] = (byte) c;
				    msg = (MidiMessage) new SysexMessage();
				    ((SysexMessage) msg).setMessage(d, size);
				    transmit(msg);
				}
				break;
			    case ShortMessage.TUNE_REQUEST: // 0xf6
				// 0 byte message
				msg = (MidiMessage) new ShortMessage();
				((ShortMessage) msg).setMessage(c);
				transmit(msg);
				break;
			    case SysexMessage.SYSTEM_EXCLUSIVE:
				size = 1;
				// FALLTHROUGH
			    default:
				buf[0] = (byte) c;
			    }
			    runningStatus = c;
			}
		    } else {
			// data byte
			if (thirdByte) {
			    thirdByte = false;
			    msg = (MidiMessage) new ShortMessage();
			    ((ShortMessage) msg).setMessage((int) (buf[0] & 0xff),
							    (int) (buf[1] & 0xff), c);
			    transmit(msg);
			} else {
			    switch (runningStatus < 0xf0 ? runningStatus & 0xf0 : runningStatus) {
			    case ShortMessage.SONG_POSITION_POINTER: // 0xf2
				// 2 byte message
				runningStatus = 0;
				// FALLTHROUGH
			    case ShortMessage.NOTE_OFF:		// 0x8n
			    case ShortMessage.NOTE_ON:		// 0x9n
			    case ShortMessage.POLY_PRESSURE:	// 0xAn
			    case ShortMessage.CONTROL_CHANGE:	// 0xBn
			    case ShortMessage.PITCH_BEND:	// 0xEn
				// 2 byte message
				thirdByte = true;
				buf[1] = (byte) c;
				break;

			    case ShortMessage.MIDI_TIME_CODE:	// 0xf1
			    case ShortMessage.SONG_SELECT:	// 0xf3
				// 1 byte message
				runningStatus = 0;
				// FALLTHROUGH
			    case ShortMessage.PROGRAM_CHANGE:	// 0xCn
			    case ShortMessage.CHANNEL_PRESSURE:	// 0xDn
				// 1 byte message
				msg = (MidiMessage) new ShortMessage();
				((ShortMessage) msg).setMessage((int) (buf[0] & 0xff), c, 0);
				transmit(msg);
				break;

			    case SysexMessage.SYSTEM_EXCLUSIVE:	// 0xf0
				buf[size++] = (byte) c;
				if (size == buf.length) {
				    // Sysex data buffer is full
				    byte[] d = new byte[size];
				    System.arraycopy(buf, 0, d, 0, size);
				    msg = (MidiMessage) new SysexMessage();
				    ((SysexMessage) msg).setMessage(d, size);
				    transmit(msg);
				    // See SysexMessage document.
				    buf[0] = (byte) SysexMessage.SPECIAL_SYSTEM_EXCLUSIVE;
				    size = 1;
				}
				break;

			    default: // 0, 0xf4 (undef), 0xf5 (undef), 0xf6 (Tune Request), 0xf7 (EOX)
				runningStatus = 0;
				// ignore data
			    }
			}
		    }
		}
		/**
		 * Tells the InputThread to stop polling the midi input port
		 * We need this if we are to stop using thread.stop(), which is deprecated  - emenaker 2003.03.12
		 */
		void stopRunning() {
			InputMidi = null;
		}
	} /* End of inner class InputThread */
}
