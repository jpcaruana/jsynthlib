/* This is a wrapper around the JavaMIDI routines which deal with Midi I/O under MacOS & Windows. It seemed clever to write
   a wrapper around the library rather than to use the Library directly. To support a new platform, all that needs to be
   done is to implement a wrapper class which extends MidiWrapper and change one line in PatchEdit.java to instantiate the
   correct wrapper 
*/

package core; //TODO org.jsynthlib.midi;

import jmidi.*;
import javax.swing.*;
import core.*; // FIXME Eventually, core.* will go away.

public class JavaMidiWrapper extends MidiWrapper {
	MidiPort javaMidiPort;
	// FIXME - This has to be public until PrefsDialog stops using it - emenaker 2003.03.12
	public MidiPort faderMidiPort;
	int faderInPort;
	int currentInPort;
	int currentOutPort;

	public  JavaMidiWrapper () throws Exception {
		currentInPort = currentOutPort = 0;
	}
 
	/**
	 * 	This used to be the constructor with (int,int) params
	 *  I needed to change the (int,int) constructor to some non-constructor thing, because I neede to
	 *  have an instance of every eligible midi wrapper ahead of time. - emenaker 3-12-2003
	 */
	public void init(int inport, int outport) throws DriverInitializationException {
		// First, try to load the driver
		try {
			// TODO Fix the faderMidiPort...  - emenaker 2003.03.19
			faderMidiPort = javaMidiPort=new  MidiPort();
		} catch(UnsatisfiedLinkError e) {
			// Catch ULE's thrown by the MidiPort(int,int) constructor called by initSingle(int,int)
			// This catches the case where the JVM can't even find the MidiPort DLL. - emenaker 2003.03.17
			String libPath = System.getProperty("java.library.path");
			//replace semi-colons with newlines
			char[] chars  = libPath.toCharArray();
			for(int i=0; i<chars.length; i++) {
				if(chars[i]==';') {
					chars[i]='\n';
				}
			}
			libPath = new String(chars);
			ErrorMsg.reportError ("Error!","Unable to load the native DLL for the driver.\n"+
				"Please make sure that the MidiPort.dll file exists somewhere in\n"+
				"one of the following directories:\n"+
				libPath
				,null);
			throw(new DriverInitializationException("JavaMidi Driver reports:\nUnable to load MidiPort DLL"));
		}

		try {
			if(getNumInputDevices() > 0) {
				if(inport > -1 && inport < getNumInputDevices()) {
					javaMidiPort.setDeviceNumber( MidiPort.MIDIPORT_INPUT, inport );
				} else {
					// We were asked for a MIDI port number that isn't available. Pick first one
					javaMidiPort.setDeviceNumber( MidiPort.MIDIPORT_INPUT, 0 );
				}					
			} else {
				// There aren't any input devices to choose from - emenaker 2003.03.19
				ErrorMsg.reportError ("Error!","JavaMidi Driver reports:\nNo MIDI input devices detected");
			}
		} catch (MidiPortException e) {
		} catch (Exception e) {
		}
		
		try {
			if(getNumOutputDevices() > 0) {
				if(outport > -1 && outport < getNumOutputDevices()) {
					javaMidiPort.setDeviceNumber( MidiPort.MIDIPORT_OUTPUT, outport );
				} else {
					// We were asked for a MIDI port number that isn't available. Pick first one
					javaMidiPort.setDeviceNumber( MidiPort.MIDIPORT_OUTPUT, 0 );
				}					
			} else {
				// There aren't any input devices to choose from - emenaker 2003.03.19
				ErrorMsg.reportError ("Error!","JavaMidi Driver reports:\nNo MIDI output devices detected");
			}
		} catch (MidiPortException e) {
		} catch (Exception e) {
		}
		
		try {
			javaMidiPort.open();
		} catch (MidiPortException e) {
		} catch (Exception e) {
		}
	}

	// Tries to initialize the MidiPort layer for a single inport/outport pair
	// An exception will get thrown if it fails
	private void initSingle(int inport, int outport) throws Exception {
		try {
			javaMidiPort=new  MidiPort(inport, outport);
			currentInPort=inport; currentOutPort=outport;
			if ((currentInPort!=PatchEdit.appConfig.getFaderPort()) && (PatchEdit.appConfig.getFaderEnable())) {
				// We need to open a different port for the faders
				try {
					faderMidiPort=new MidiPort(PatchEdit.appConfig.getFaderPort(),outport);
				} catch (Exception e) {
					// Opening the fader port failed, but we can still do basic in/out
					ErrorMsg.reportError("Error",
					"Unable to obtain the midi port for the faders.\n"+
					"Faders will not work unless you choose a different midi port\n"+
					"in Config->Preferences->Faders.",e);
					try {
						faderMidiPort.open();
					} catch (Exception e2) {
						// Opening the fader port failed, but we can still do basic in/out
						ErrorMsg.reportError("Error", 
							"Unable to open the midi port for the faders.\n"+
							"It appears that this is because the midi driver you're using\n"+
							"does not support listening to multiple midi ports at the same\n"+
							"time. Either try using your faders on the same port as your main"+
							"\"IN\" port, or try a different midi driver in"+
							"Config->Preferences->Midi.",e);
					}
				}
			}
			//try {
				javaMidiPort.open();
			//} catch (Exception e) {
			//	JOptionPane.showMessageDialog(null, "Unable to Initialize MIDI IN/OUT! \nMidi Transfer will be unavailable this session.\n Try changing initialization ports under Config->Preferences->MIDI and restarting.","Error",JOptionPane.ERROR_MESSAGE);
			//}
		} catch(Exception e) {
			// We need to catch this exception so that we can release any classes that we instantiated and then
			// we must re-throw the exception
			javaMidiPort = faderMidiPort = null;
			currentInPort = currentOutPort = 0;
			throw e;
		}
	}

	public void close() {
		if(javaMidiPort != null) {
			try {
				javaMidiPort.close();
			} catch(MidiPortException e) {
				// Don't know what could possibly go wrong with closing a midi port... but
				// we'll catch the exception here.
				ErrorMsg.reportError("Error",
				"There was a problem closing the Midi port used by JavaMidi.\n" +
				"This may or may not have an effect on the rest of your activities.",e);
			}
		}
	}

	//FIXME: Never call this even though its public, I need to call it from prefsDialog to work around a JavaMIDI bug though.
	public  void setInputDeviceNum(int port) throws Exception {
System.out.println("setInputPort("+port+")   was "+currentInPort);
		if (currentInPort!=port) {
			javaMidiPort.setDeviceNumber(MidiPort.MIDIPORT_INPUT,port);
		} 
		currentInPort=port;
	}

	//FIXME Made public so that PrefsDialog can call it until we straighten this mess out - emenaker 2003.03.12
	public  void setOutputDeviceNum(int port) throws Exception {
System.out.println("setOutputPort("+port+")   was "+currentOutPort);
		if (currentOutPort!=port) { 
			javaMidiPort.setDeviceNumber(MidiPort.MIDIPORT_OUTPUT,port);
		}
		currentOutPort=port;
	}
  
	public  void writeLongMessage (int port,byte []sysex) throws Exception {
		setOutputDeviceNum(port);
		javaMidiPort.writeLongMessage(sysex,sysex.length,0);
	}
  
	public  void writeLongMessage (int port,byte []sysex,int length) throws Exception {
		setOutputDeviceNum(port);
		javaMidiPort.writeLongMessage(sysex,length,0);
	}
	
	public void writeShortMessage(int port, byte b1, byte b2) throws Exception {
		setOutputDeviceNum(port);
		javaMidiPort.writeShortMessage(b1,b2);
	}
  
	public void writeShortMessage (int port,byte b1, byte b2,byte b3) throws Exception {
		setOutputDeviceNum(port);
		javaMidiPort.writeShortMessage(b1,b2,b3);
	}
  
	public  int getNumInputDevices() throws Exception{return javaMidiPort.getNumDevices(MidiPort.MIDIPORT_INPUT);}
	
	public  int getNumOutputDevices()throws Exception {return javaMidiPort.getNumDevices(MidiPort.MIDIPORT_OUTPUT);}
	
	public  String getInputDeviceName(int port)throws Exception {
		return javaMidiPort.getDeviceName(MidiPort.MIDIPORT_INPUT,port);
	}
	
	public  String getOutputDeviceName(int port)throws Exception {
		return javaMidiPort.getDeviceName(MidiPort.MIDIPORT_OUTPUT,port);
	}
	
	public  int messagesWaiting(int port)throws Exception {
		if (port==PatchEdit.appConfig.getFaderPort()) return (faderMidiPort.messagesWaiting());
			setInputDeviceNum(port);
		return javaMidiPort.messagesWaiting();
	}
  
	public  int readMessage(int port,byte []sysex,int maxSize) throws Exception {
		if (port==PatchEdit.appConfig.getFaderPort()) return (faderMidiPort.readMessage(sysex,maxSize));
			setInputDeviceNum(port);
		return(javaMidiPort.readMessage(sysex,maxSize));
	}

	public String getWrapperName() {
		return("MS Windows (JavaMIDI)");
	}

	public static boolean supportsPlatform(String platform) {
		if(platform.length()==0 || platform.indexOf("Windows")>-1) {
			return(true);
		}
		return(false);
	}

	public static boolean supportsScanning() {
		return(false);
	}
}
