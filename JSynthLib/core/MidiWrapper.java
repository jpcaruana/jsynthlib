// Create a subclass of this in order to support a new platform. These are the functions you must implement.
package core; //TODO org.jsynthlib.midi;

//import java.lang.reflect.*;
import java.lang.reflect.Method;
import java.util.Vector;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.SysexMessage;
import javax.sound.midi.InvalidMidiDataException;

// TODO Add capability to have driver re-query the available ports - emenaker 2003.03.25
// This would be handy if you had some other program running (like Sonar, Cubase, etc) that
// was using some of the MIDI ports and JSynthLib was unable to use them. It would be nice
// if you could exit the other program and then have the wrapper re-scan for available midi
// ports so you didn't have to stop/restate JSynthLib

public abstract class MidiWrapper {
	private MidiWrapper(int inport, int outport) {} /* Deprecated. This is to prevent anyone from using it - emenaker 2003.09.01 */
        public abstract void init (int inport, int outport) throws Exception;
	public MidiWrapper() throws Exception {}
	//FIXME Made public so that PrefsDialog can call it until we straighten this mess out - emenaker 2003.03.12
	//public abstract void setInputDeviceNum(int port)throws Exception;
	//FIXME Made public so that PrefsDialog can call it until we straighten this mess out - emenaker 2003.03.12
        //public abstract void setOutputDeviceNum(int port)throws Exception;
	public abstract void writeLongMessage (int port,byte []sysex)throws Exception;
	public abstract void writeLongMessage (int port,byte []sysex,int size)throws Exception;
	public abstract void writeShortMessage(int port, byte b1, byte b2)throws Exception;
	public abstract void writeShortMessage (int port,byte b1, byte b2,byte b3)throws Exception;
	public abstract int getNumInputDevices()throws Exception; // FIXME Probably shouldn't throw an exception. Why not just return zero? emenaker 2003.03.12
	public abstract int getNumOutputDevices()throws Exception; // FIXME Probably shouldn't throw an exception. Why not just return zero? emenaker 2003.03.12
	public abstract String getInputDeviceName(int port)throws Exception;
	public abstract String getOutputDeviceName(int port)throws Exception;
	public abstract int messagesWaiting(int port)throws Exception;

	// replaced by getMessage(int port)
	//public abstract int readMessage(int port,byte []sysex,int maxSize)throws Exception;

	/**
	 * Dump <code>sysex</code> byte array to MIDI Monitor Window
	 * with port number information and input/output information.
	 *
	 * @param port port number
	 * @param in true for input, false for output
	 * @param sysex byte array
	 * @param length length of data to dump
	 */
	public void logMidi(int port,boolean in,byte []sysex,int length) {
		if ((core.PatchEdit.midiMonitor!=null) && core.PatchEdit.midiMonitor.isVisible())
			core.PatchEdit.midiMonitor.log(port,in,sysex,length);
	}

	/**
	 * Dump <code>msg</code> MidiMessage to MIDI Monitor Window
	 * with port number information and input/output information.
	 *
	 * @param port port number
	 * @param in true for input, false for output
	 * @param msg MidiMessage
	 */
	public void logMidi(int port,boolean in, MidiMessage msg) {
		if ((core.PatchEdit.midiMonitor!=null) && core.PatchEdit.midiMonitor.isVisible()) {
			byte sysex[] = msg.getMessage();
			int length = msg.getLength();
			core.PatchEdit.midiMonitor.log(port,in,sysex,length);
		}
	}

	/**
	 * Output string to MIDI Monitor Window.
	 *
	 * @param s string to be output
	 */
	public void logMidi(String s) {
		if ((core.PatchEdit.midiMonitor!=null) && core.PatchEdit.midiMonitor.isVisible())
			core.PatchEdit.midiMonitor.log(s);
	}

	/**
	 * This allows you to send JavaSound-compatible MidiMessage objects. - emenaker 2003.03.22
	 *
	 * @param port The port to send out through
	 * @param msg The midi message
	 * @throws Exception
	 */
	public void send(int port, MidiMessage msg) throws Exception {
		writeMessage(port, msg);
	}

	public void writeMessage(int port, MidiMessage msg) throws Exception {
		if (msg instanceof ShortMessage) {
			writeShortMessage(port, (ShortMessage) msg);
		} else {
			writeLongMessage(port, (SysexMessage) msg);
		}
	}

	public void writeLongMessage(int port, SysexMessage msg) throws Exception {
		int size = msg.getLength();
		byte[] sysex = msg.getMessage();
		writeLongMessage(port, sysex, size);
	}

	public void writeShortMessage(int port, ShortMessage msg) throws Exception {
		//System.out.println("Length is "+msg.getLength());
		switch (msg.getLength()) {
		case 1:
		case 2:
			writeShortMessage(port,
					  (byte) msg.getStatus(),
					  (byte) msg.getData1());
			break;
		case 3:
			writeShortMessage(port,
					  (byte) msg.getStatus(),
					  (byte) msg.getData1(),
					  (byte) msg.getData2());
			break;
		}
	}

	/**
	 * get a MidiMessage from a MIDI port <code>port</code>.
	 *
	 * From MIDI specification<p>
	 *   EXCLUSIVE:<p>
	 *     Exclusive messages can contain any number of Data bytes,
	 *     and can be terminated either by an End of Exclusive
	 *     (EOX) or any other Status byte (except Real Time
	 *     messages).  An EOX should always be sent at the end of a
	 *     System Exclusive message. ...<p>
	 *
	 * An OS dependent MIDI wrapper must insert 0xF7 at the
	 * beginning of data array if the message is following to
	 * previous one.<p>
 	 *
	 * @param port MIDI port
	 * @return a <code>MidiMessage</code> value
	 */
	abstract MidiMessage getMessage(int port) throws InvalidMidiDataException;

	/**
	 * <code>readMessage</code> allows you to read a
	 * JavaSound-compatible MidiMessage object. It automatically
	 * detects if it's a SysexMessage or ShortMessage and returns
	 * the appropriate object.
	 *
	 * JavaSound allows to divide a System Exclusive message into
	 * multiple SysexMessage objects.  <code>readMessage</code>
	 * concatinates the divided SysexMessages received from low
	 * layer into one SysexMessage terminated by EOX (End of
	 * Exclusive) and returns it.  If a SysexMessage is terminated
	 * by a ShortMessage, InvalidMidiDataException is thrown.
	 *
	 * TimeoutException is thrown if a MIDI message cannot read in the
	 * time specified by <code>timeout</code>.
	 *
	 * @param port The port to read from
	 * @param timeout timeout count (millisecond)
	 * @return MidiMessage object read from the port.
	 * @throws Exception
	 */
	// emenaker 2003.03.22
	public MidiMessage readMessage(int port, long timeout) throws Exception {
		return readMessage(port, timeout, false);
	}

	/**
	 * Causes timeout after 5 second. About 16KB System exclusive
	 * message can be received.
	 */
	public MidiMessage readMessage(int port) throws Exception {
		return readMessage(port, 5000, false); // 5 second
	}

	/**
	 * This returns only SysexMessage.  ShortMessages are discarded.
	 */
	public SysexMessage readSysexMessage(int port, long timeout) throws Exception {
		return (SysexMessage) readMessage(port, timeout, true);
	}

	/**
	 * Causes timeout after 5 second. About 16KB System exclusive
	 * message can be received.
	 */
	public SysexMessage readSysexMessage(int port) throws Exception {
		return (SysexMessage) readMessage(port, 5000, true); // 5 second
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
	private MidiMessage readMessage(int port, long timeout, boolean ignoreShortMessage)
	    throws TimeoutException, InvalidMidiDataException, Exception {
	    long start = System.currentTimeMillis();
	    byte [] buffer = {};
	    int totalLen = 0;
	    boolean firstMsg = true;
	    do {
		// wait for data
		while (messagesWaiting(port) == 0) {
		    Thread.sleep(10); // define const!!!FIXIT!!!
		    if (System.currentTimeMillis() - start > timeout)
			throw new TimeoutException(port);
		}
		MidiMessage msg = getMessage(port);
		if (msg == null)
		    throw new InvalidMidiDataException(getInputDeviceName(port));
		int len = msg.getLength();
		if (firstMsg) {
		    if (msg.getStatus() != SysexMessage.SYSTEM_EXCLUSIVE)
			if (ignoreShortMessage)
			    continue;
			else
			    return msg;
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
		    if (status != SysexMessage.SPECIAL_SYSTEM_EXCLUSIVE)
			throw new InvalidMidiDataException(getInputDeviceName(port));
		    // Combine the newly-read stuff into an new array
		    // with the existing stuff
		    byte [] buf = msg.getMessage();
		    byte[] combineBuffer = new byte[totalLen + len];
		    System.arraycopy(buffer,  0, combineBuffer, 0, totalLen);
		    if (len == 1) {// I think this is javax.sound.midi bug.
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

	/**
	 * Clear MIDI Input Buffer for <code>inPort</code>.  Wrapper
	 * class may override this method with efficient way.
	 */
	public void clearMidiInBuffer (int inPort) throws Exception {
		// read out data remained in buffer
		while (messagesWaiting(inPort) > 0)
			readMessage(inPort, 0);
	}
	/**
	 * This is used by higher level code to determine if the wrapper is initialized and
	 * ready for use. Currently, it's used by the MidiConfigPanel to decide whether or not
	 * to enable to in/out/master device selectors. - emenaker 2003.03.18
	 * @return <code>true</code>
	 */
	public boolean isReady() {
		return(true);
	}

	/**
	 * This is pretty cool. Although is an abstract class, you can call its static
	 * methods. This method tries to locate all of the wrapper classes that it can
	 * and it calls their "supportsPlatform" method for each one. It returns a Vector
	 * of MidiWrapper objects that should work on this system. Note that the vector
	 * ALSO will always contain the DoNothingMidiWrapper as the first element of the
	 * vector. - emenaker 2003.03.13
	 * @return A Vector of MidiWrapper objects suitable to the current platform
	 */
	public static Vector getSuitableWrappers() {
		Vector wrappervec = new Vector();
		// TODO: Read this list from a file or discover them in a directory, etc. - emenaker 2003.03.13
		String drivers[] = {
//			"org.jsynthlib.midi.JavaMidiWrapper",
//			"org.jsynthlib.midi.WireMidiWrapper",
//			"org.jsynthlib.midi.LinuxMidiWrapper",
//			"org.jsynthlib.midi.MacOSXMidiWrapper",
//			"org.jsynthlib.midi.JavaSoundMidiWrapper"
			// implement JavaMidiWrapper.getMessage if you need to use JavaMidi
			//"core.JavaMidiWrapper",
			"core.WireMidiWrapper",
			"core.LinuxMidiWrapper",
			"core.MacOSXMidiWrapper",
			"core.JavasoundMidiWrapper"
		};
		String thisplatform = core.PatchEdit.appConfig.getOSName();
		Class wrapperclass;
		try {
			wrappervec.add (new DoNothingMidiWrapper());
		} catch(Exception e) {
			core.ErrorMsg.reportError ("Error!",
						   "Couldn't instantiate the DoNothingMidiWrapper.\n" +
						   "That shouldn't have happened!");
		}
		for(int i=0; i<drivers.length; i++) {
			try {
				// Find the class specified by a String
				wrapperclass = Class.forName(drivers[i]);
				// Get the method for that class called supportsPlatform(String)
				Method method = wrapperclass.getMethod("supportsPlatform",new Class[] {String.class});
				// Run that method and get the result
				Boolean result = (Boolean) method.invoke(null,new Object[] {thisplatform});
				if(result.equals(Boolean.TRUE)) {
					// We were able to run the static supportsPlatform(String) method.
					// Now try to get an instance of it.
					// TODO: This might have to be taken out. Some wrappers, like WireProvider and
					// JavaMidi, load some DLL's when they are instantiated that cannot be unloaded
					// and it's possible that they might not play nice together. - emenaker 2003.09.01
					try {
						wrappervec.add(wrapperclass.newInstance());
					} catch (Exception e) {
						// If this gets executed, then we FOUND the class, we just couldn't instantiate it
						core.ErrorMsg.reportError (
							"Error!","There was a problem starting the MIDI driver named\n"+
							"\""+drivers[i]+"\"\n"+
							"It will not be available for use during this session.\n"+
							"The description of the problem is given as:\n"+
							"\""+e.getMessage()+"\"",e);
					}
				}
			} catch(Exception e) {
				// If this gets executed, then we either couldn't find the class or it didn't have
				// a supportsPlatform(String) method
				core.ErrorMsg.reportError ("Error!","There was a problem starting the MIDI driver named\n"+drivers[i]+"\nIt will not be available for use during this session.",e);
			}
		}
	  	return(wrappervec);
	}

	/**
	 * It's crucial enough that each driver release the ports when we switch to another driver
	 * that we need to make this abstract (it used to be an empty non-abstract method) so that
	 * all subclasses have to give attention to it. - emenaker 2003.03.18
	 *
	 */
	public abstract void close();

	/**
	* Returns a boolean indicating whether this Midi implementation supports the
	* OS in question. By default, it returns true. Use this to exclude obviously
	* incompatible implementation/OS combinations. For example, the MaxOSXMidiWrapper
	* would probably return false if it could find "Windows" mentioned in the platform name.
	* emenaker 2003.03.12
	* @param platform A string representation of the OS (ie, "Windows XP", "Linux 2.4.19", etc.)
	*/
	public static boolean supportsPlatform(String platform) {
		return(true);
	}

	/**
	 * This is used to determine if this midi implementation supports "scanning". I'm not sure
	 * what's involved in scanning, but Gerrit was checking in another location if the Midi
	 * wrapper was the Linux one, so I'm figuring that the Linux one does *not* and others
	 * do. - emenaker 2003.03.13
	 * @return <code>true</code>
	 */
	public static boolean supportsScanning() {
		return(true);
	}

	/**
	* Returns a string describing the name of this midi implementation (ie, "JavaMidi", "WireProvider", etc)
	* emenaker 2003.03.12
	*/
	public abstract String getWrapperName();

	public String toString() {
		return(getWrapperName());
	}

	/**
	* Compares the wrapper names (via getWrapperName()) of this instance and of the object
	* (provided that the object is an instance of MidiWrapper). Returns true if they're equal
	* emenaker 2003.03.12
	* @param o The object to compare to
	*/
	public boolean equals(Object o) {
		if(o instanceof MidiWrapper) {
			if(toString().equals(o.toString())) {
				return(true);
			}
		}
		return(false);
	}

	public class TimeoutException extends Exception {
	    public TimeoutException(int port) throws Exception {
		super("Timeout on MIDI input port "
		      + getInputDeviceName(port));
	    }
	}

}
//(setq c-basic-offset 8)
//(setq c-basic-offset 4)
