/*
 * This handles Midi I/O under Linux-like Operating Systems. Basically
 * it sends all output to a file (which should really be a device
 * pipe, and reads from a file (device pipe) to get input.
 */
package core; //TODO org.jsynthlib.midi;

import java.io.*;
import javax.swing.*;
import java.util.*;
import javax.sound.midi.*;

public class LinuxMidiWrapper extends MidiWrapper {
	private int faderInPort;
	private volatile int currentInPort;
	private volatile int currentOutPort;
// 	private byte[] midiBuffer;
// 	private int writePos;
// 	private int readPos;
	private /*static*/ OutputStream []outStream;
	private /*static*/ InputStream []inStream;
	private /*static*/ ArrayList midiDevList = new ArrayList();
	// Thread thread;
// 	private byte lastStatus;
// 	private boolean sysEx=false;
// 	private final int bufferSize=1024;
	private InputThread keyboardThread;
	// should be array of List
	private List list = Collections.synchronizedList (new LinkedList ());

	// This used to be the unnamed "static" method that got run when the overall
	// application ran. Now, it's the default constructor... so it only gets run
	// (and only complains about not finding linuxdevices.conf) when actually running
	// on a Linux system - emenaker 2003.03.12
	public LinuxMidiWrapper() throws Exception {
		loadDeviceNames();
		outStream = new OutputStream[midiDevList.size()];
		inStream = new InputStream[midiDevList.size()];
		System.out.println("LinuxMidiWrapper:static:Number of devices:"+midiDevList.size());
		for (int i=0;i<midiDevList.size();i++) {
			File file = new java.io.File( (String) midiDevList.get(i));
			try {
				inStream[i]  = new java.io.FileInputStream(file);
				outStream[i] = new java.io.FileOutputStream(file);
				//outStream[i] = new BufferedOutputStream(outStream[i], 4100);
			} catch (Exception e) {
				e.printStackTrace ();
			}
		}
	}

	// This used to be the constructor with (int,int) params
	// I needed to change the (int,int) constructor to some non-constructor thing, because I neede to
	// have an instance of every eligible midi wrapper ahead of time. - emenaker 2003.03.12
	public void init(int inport, int outport) {
		// loadDeviceNames ();
		currentOutPort=outport;
		currentInPort=inport;
		/*
		outStream = new OutputStream[getNumInputDevices ()];
		inStream = new InputStream[getNumInputDevices ()];
		File file = new java.io.File ( (String) midiDevList.get (outport));
		RandomAccessFile pipe = new java.io.RandomAccessFile (file , "rw");

		outStream[outport] = new java.io.FileOutputStream ( pipe.getFD () );
		inStream[inport] = new java.io.FileInputStream ( pipe.getFD () );
		outStream[outport] = new BufferedOutputStream (outStream[outport] ,4100);
		*/
		System.out.println("LinuxMidiWrapper Ctor inport "+inport+" outport "+outport);
// 		midiBuffer = new byte[bufferSize];
// 		writePos=0;
// 		readPos=0;
		keyboardThread = new InputThread();
		// thread = new Thread(keyboardThread);
		keyboardThread.start();
	}

	/*   commented out by emenaker 3/12/2003
	public  LinuxMidiWrapper() throws Exception {
		// this(0,0);
		// System.out.println("Debugging: LinuxMidiWrapper Constructor called w/ no params -- shouldn't happen");
	}
	*/

	private /*static*/ void loadDeviceNames()
	throws FileNotFoundException, IOException {
		String line;
		try {
			FileReader fileReader = new FileReader("linuxdevices.conf");
			LineNumberReader lineNumberReader = new LineNumberReader(fileReader);
			while ((line = lineNumberReader.readLine())!=null) {
				midiDevList.add(line);
			}
		} catch (FileNotFoundException e) {
			throw new FileNotFoundException("Unable to read from 'linuxdevices.conf'");
		}
	}

	private void setInputDeviceNum (int port) {
		if (port == currentInPort) return;
		currentInPort=port;
		/*
		if (inStream[port]==null) {
			File file = new java.io.File( (String) midiDevList.get(port));
			RandomAccessFile pipe = new java.io.RandomAccessFile(file , "rw");
			outStream[port] = new java.io.FileOutputStream( pipe.getFD() );
			outStream[port] = new BufferedOutputStream( outStream[port] ,4100);
			inStream[port] = new java.io.FileInputStream( pipe.getFD() );
		}
		*/
		// thread.stop() is deprecated. I switched this to thread.stopRunning
		keyboardThread.stopRunning();
// 		readPos=writePos;
// 		thread = new Thread(keyboardThread);
		keyboardThread.start();
	}

	private void setOutputDeviceNum (int port) {
		if (port == currentOutPort)
			return;
		currentOutPort=port;
		//  outStream.close();
		/*
		if (outStream[port]!=null) return;
		File file = new java.io.File( (String) midiDevList.get(port));
		RandomAccessFile pipe = new java.io.RandomAccessFile(file , "rw");
		outStream[port] = new java.io.FileOutputStream( pipe.getFD() );
		outStream[port] = new BufferedOutputStream( outStream[port] ,4100);
		inStream[port]= new java.io.FileInputStream(pipe.getFD());
		*/
	}

	public void send(int port, MidiMessage msg) throws IOException {
		setOutputDeviceNum (port);
		byte[] sysex = msg.getMessage();
		int length = msg.getLength();

		if (outStream[port]!=null) {
			final int BUFSIZE = 250;
			//final int BUFSIZE = 0;

			if (BUFSIZE == 0) {
				outStream[port].write(sysex,0,length);
				outStream[port].flush ();
				MidiUtil.logOut(port, sysex, length);
			} else {
				byte[] tmpArray = new byte[BUFSIZE+1];

				for (int i = 0; length > 0; i += BUFSIZE, length -= BUFSIZE) {
					int s = Math.min(length, BUFSIZE);

					System.arraycopy(sysex, i, tmpArray, 0, s);
					outStream[port].write(tmpArray,0,s);
					outStream[port].flush ();
					MidiUtil.logOut(port, tmpArray, s);
				}
			}
		}
	}
	/*
	public void writeShortMessage (int port, byte b1, byte b2) throws IOException {
		setOutputDeviceNum (port);
		outStream[port].write (b1);
		outStream[port].write (b2);
		outStream[port].flush ();
	}

	public void writeShortMessage (int port,byte b1, byte b2,byte b3) throws IOException {
		setOutputDeviceNum (port);
		outStream[port].write (b1);
		outStream[port].write (b2);
		outStream[port].write (b3);
		outStream[port].flush ();
	}
	*/
	public  int getNumInputDevices () {
		return midiDevList.size ();
	}

	public  int getNumOutputDevices () {
		return midiDevList.size ();
	}

	public  String getInputDeviceName (int port) {
		return (String)midiDevList.get (port);
	}

	public  String getOutputDeviceName (int port) {
		return (String)midiDevList.get (port);
	}

	public  int messagesWaiting (int port) {
		setInputDeviceNum (port);
// 		if (readPos!=writePos) return 1; else return 0;
		return list.size();
	}

	public void clearMidiInBuffer(int port) {
		while (!list.isEmpty())
			list.remove(0);
	}

	/*
	public  int readMessage (int port,byte []sysex,int maxSize) throws Exception {
		byte statusByte;
		int msgLen=0;
		int i=0;
		int temp=writePos;
		if (sysEx==true) {
			while ((midiBuffer[readPos]!=-9) && (readPos!=temp) && (i<maxSize-1)) {
				sysex[i++]=midiBuffer[readPos++];
				if (midiBuffer[readPos-1]==-16) i--; //kludge to kill extraneus 0xF0's
				readPos%=bufferSize;
			}
			if ((midiBuffer[readPos]==-9) && (readPos!=temp)) {
				sysex[i++]=midiBuffer[readPos++];
				readPos%=bufferSize;
				sysEx=false;
				MidiUtil.logOut(port,sysex,i);
				return i;
			}
			logMidi(port,true,sysex,i);
			return i;
		}

		statusByte=midiBuffer[readPos++];
		if (statusByte>=0) {
			statusByte=lastStatus;readPos--;
		}
		switch (statusByte&0xF0) {
			case 0x80:case 0x90:case 0xA0:case 0xB0: case 0xE0: msgLen=3; break;
			case 0xC0:case 0xD0: msgLen=2;break;
			case 0xF0: sysEx=true;msgLen=1;break;
		}
		lastStatus=statusByte;
		sysex[0]=statusByte;
		temp=writePos;
		if (temp<readPos)
			temp+=bufferSize-1;
		temp-=readPos;

		while (temp<msgLen-1) {
			keyboardThread.yield ();
			temp=writePos;
			if (temp<readPos)
				temp+=bufferSize;
			temp-=readPos;
		}  //if msg not complete-- get more
		for (i=1;i<msgLen;i++)
			sysex[i]=midiBuffer[(readPos++)%bufferSize];
		readPos%=bufferSize;
		logMidi(port,true,sysex,msgLen);
		return msgLen;
	}
	*/

	MidiMessage getMessage (int port) {
		setInputDeviceNum(port);

		// pop the oldest message
		MidiMessage msg = (MidiMessage) list.remove(0);
		MidiUtil.logIn(port, msg);
		return msg;
	}

	public void close() {
		keyboardThread.stopRunning();
		try {
			//outStream[currentOutPort].close();
			//inStream[currentInPort].close();
		} catch (Exception e) {
		}
	}
	/*
	public  void writeLongMessage (int port,byte []sysex) throws IOException {
		writeLongMessage (port,sysex,sysex.length);
	}
	*/
	public String getWrapperName() {
		return("GNU/Linux");
	}

	// This runs on Linux, and nothing else
	public static boolean supportsPlatform(String platform) {
		if(platform.length()==0 || platform.equals("Linux")) {
			return(true);
		}
		return(false);
	}

	// This inner class used to implement Runnable but I changed it so that it
	// extends Thread so that we can avoid having to do "new Thread(new InputThread())"
	// in the LinuxMidiWrapper class. Besides, the option for implementing Runnable
	// was created for cases where you were already extending another class and
	// couldn't extend Thread - emenaker 2003.03.12

	/* Since Joe's initial thread code causes an exception while using start(), I changed
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
				if (inStream[currentInPort]==null)
					break;
				try {
					c = inStream[currentInPort].read();
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
				} catch (Exception ex) {
					ErrorMsg.reportError("Error", "MIDI input error", ex);
				}
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
			    list.add(msg);
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
				    list.add(msg);
				}
				break;
			    case ShortMessage.TUNE_REQUEST: // 0xf6
				// 0 byte message
				msg = (MidiMessage) new ShortMessage();
				((ShortMessage) msg).setMessage(c);
				list.add(msg);
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
			    list.add(msg);
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
				list.add(msg);
				break;

			    case SysexMessage.SYSTEM_EXCLUSIVE:	// 0xf0
				buf[size++] = (byte) c;
				if (size == buf.length) {
				    // Sysex data buffer is full
				    byte[] d = new byte[size];
				    System.arraycopy(buf, 0, d, 0, size);
				    msg = (MidiMessage) new SysexMessage();
				    ((SysexMessage) msg).setMessage(d, size);
				    list.add(msg);
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

	}
}
//(setq c-basic-offset 8)
//(setq c-basic-offset 4)
