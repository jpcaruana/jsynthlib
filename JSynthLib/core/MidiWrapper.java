// Create a subclass of this in order to support a new platform. These are the functions you must implement.
package core; //TODO org.jsynthlib.midi;

import java.lang.reflect.*;
import java.util.Vector;
import javax.sound.midi.*;

// TODO Add capability to have driver re-query the available ports - emenaker 2003.03.25
// This would be handy if you had some other program running (like Sonar, Cubase, etc) that 
// was using some of the MIDI ports and JSynthLib was unable to use them. It would be nice
// if you could exit the other program and then have the wrapper re-scan for available midi
// ports so you didn't have to stop/restate JSynthLib

abstract public class MidiWrapper {
	private MidiWrapper(int inport, int outport) {} /* Deprecated. This is to prevent anyone from using it - emenaker 2003.09.01 */
	public void init (int inport, int outport) throws Exception {}
	public MidiWrapper() throws Exception {}
	//FIXME Made public so that PrefsDialog can call it until we straighten this mess out - emenaker 2003.03.12
	public abstract void setInputDeviceNum(int port)throws Exception;
	//FIXME Made public so that PrefsDialog can call it until we straighten this mess out - emenaker 2003.03.12
	public abstract void setOutputDeviceNum(int port)throws Exception;
	public abstract void writeLongMessage (int port,byte []sysex)throws Exception;
	public abstract void writeLongMessage (int port,byte []sysex,int size)throws Exception;
	public abstract void writeShortMessage(int port, byte b1, byte b2)throws Exception;
	public abstract void writeShortMessage (int port,byte b1, byte b2,byte b3)throws Exception;
	public abstract int getNumInputDevices()throws Exception; // FIXME Probably shouldn't throw an exception. Why not just return zero? emenaker 2003.03.12
	public abstract int getNumOutputDevices()throws Exception; // FIXME Probably shouldn't throw an exception. Why not just return zero? emenaker 2003.03.12
	public abstract String getInputDeviceName(int port)throws Exception;
	public abstract String getOutputDeviceName(int port)throws Exception;
	public abstract int messagesWaiting(int port)throws Exception;
	public abstract int readMessage(int port,byte []sysex,int maxSize)throws Exception;
	public void logMidi(int port,boolean in,byte []sysex,int length) {
		if ((core.PatchEdit.midiMonitor!=null) && core.PatchEdit.midiMonitor.isVisible())
			core.PatchEdit.midiMonitor.log(port,in,sysex,length);
	}
	
	/**
	 * This allows you to send JavaSound-compatible MidiMessage objects. - emenaker 2003.03.22
	 * 
	 * @param port The port to send out through
	 * @param msg The midi message
	 * @throws Exception
	 */
	public void send(int port, javax.sound.midi.MidiMessage msg) throws Exception {
		if(msg instanceof ShortMessage) {
			ShortMessage shortmsg = (ShortMessage) msg;
			//System.out.println("Length is "+msg.getLength());
			switch(shortmsg.getLength()) {
				case 1:
				case 2:
					writeShortMessage(port, (byte) shortmsg.getStatus(), (byte) shortmsg.getData1());
					break;
				case 3:
					writeShortMessage(port, (byte) shortmsg.getStatus(), (byte) shortmsg.getData1(), (byte) shortmsg.getData2());
					break;
				
			}
		} else {
			writeLongMessage(port,msg.getMessage());
		}
	}
	
	/**
	 * This allows you to read a JavaSound-compatible MidiMessage object. It automatically detects
	 * if it's a SysexMessage or ShortMessage and returns the appropriate object. - emenaker 2003.03.22
	 * 
	 * @param port The port to read from
	 * @return
	 * @throws Exception
	 */
	public MidiMessage readMessage(int port) throws Exception {
		MidiMessage msg = null;
		int buffersize = 100;
		byte[] buffer = new byte[buffersize];
		int messages = messagesWaiting(port);
		if(messages != 0) {
			int bytesread = readMessage(port, buffer, buffer.length);
			if(bytesread >0) {
				if(bytesread < 4) {
					// Looks like a short message
					javax.sound.midi.ShortMessage shortmsg = new ShortMessage();
					shortmsg.setMessage(buffer[0]&0xFF,buffer[1]&0xFF,buffer[2]&0xFF);
					msg = shortmsg;
				} else {
					// It's a sysex message
					javax.sound.midi.SysexMessage sysexmsg = new SysexMessage();
					
					// Did we get all of the message?
					if(messages == messagesWaiting(port)) {
						// If we didn't, we need to start getting more chunks and sticking them together
						byte[] buffer2;
						byte[] combineBuffer;
						int bytesread2;
						while(messages == messagesWaiting(port)) {
							buffer2 = new byte[buffer.length * 2];
							bytesread2 = readMessage(port, buffer2, buffer2.length);
							// Combine the newly-read stuff into an new array with the existing stuff
							bytesread += bytesread2;
							combineBuffer = new byte[buffer.length + buffer2.length];
							System.arraycopy(buffer,0,combineBuffer,0,buffer.length);
							System.arraycopy(buffer2,0,combineBuffer,buffer.length,buffer2.length);
							buffer = combineBuffer;
						}
					}
					sysexmsg.setMessage(buffer,bytesread);
					msg = sysexmsg;
				}
			}
		}
		return(msg);
	}
	
	/**
	 * This is used by higher level code to determine if the wrapper is initialized and
	 * ready for use. Currently, it's used by the MidiConfigPanel to decide whether or not
	 * to enable to in/out/master device selectors. - emenaker 2003.03.18
	 * @return
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
			"core.JavaMidiWrapper",
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
	 * @return
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
}
