
/**
 * This is a MidiWrapper for the generic Javasound API
 * Danger, to use it you need JDK1.4.2beta at least.....
 *
 * @author Gerrit Gehnen
 * @version $Id$
 */

package core; //TODO org.jsynthlib.midi;
import javax.sound.midi.*;
import java.util.*;

public class JavasoundMidiWrapper extends MidiWrapper implements Receiver {
    private int currentOutport = -1;
    private int currentInport = -1;
    private int faderPort;
    private Vector sourceInfoVector;
    private Vector destinationInfoVector;
    private Transmitter input=null;
    private Transmitter fader=null;
    private Receiver output=null;
    private List list = Collections.synchronizedList(new LinkedList());
    private boolean initialized;
    /**
     * MIDI Output buffer size.  If set to '0', Whole Sysex data is
     * sent in one packet.  Set '0' unless you have problem.
     */
    private static final int BUFSIZE = 0;

    /* this(0,0); Mustn't do this. We need to be able to instantiate
       without trying to grab any ports - emenaker 2003.09.01 */
    public JavasoundMidiWrapper() throws Exception {}

    // This used to be the constructor with (int,int) params
    // I needed to change the (int,int) constructor to some non-constructor thing, because I neede to
    // have an instance of every eligible midi wrapper ahead of time. - emenaker 2003.03.12
    public void init(int inport, int outport) throws /*DriverInitializationException,*/ MidiUnavailableException {
        initialized = false;
	/*
        currentInport=inport;
        currentOutport=outport;
        faderPort=PatchEdit.appConfig.getFaderPort();
	*/
        sourceInfoVector=new Vector();
        destinationInfoVector=new Vector();
// 	ErrorMsg.reportStatus("WireMidiWrapper.init" + sourceInfoVector + ", " + inport + ", " + outport);

	MidiDevice.Info[] mdi = MidiSystem.getMidiDeviceInfo();

        for (int i=0;i<mdi.length;i++) {
//             try {
		MidiDevice md = MidiSystem.getMidiDevice(mdi[i]);
                //md.open(); // This can really throw an MidiUnavailableException on my System

                if (md.getMaxReceivers()!=0) {
// 		    ErrorMsg.reportStatus("is possible Destination");
                    destinationInfoVector.add(mdi[i]);
                }
                if (md.getMaxTransmitters()!=0) {
// 		    ErrorMsg.reportStatus("is possible Source");
                    sourceInfoVector.add(mdi[i]);
                }
// 	    } catch (MidiUnavailableException e) { // Ignore, can happen.....
// 	    } catch (Exception e) {
// 		e.printStackTrace();
// 	    }
        }

	/*
        Mididevice destDevice=MidiSystem.getMidiDevice((MidiDevice.Info)destinationInfoVector.get(outport));
        //destDevice.open();
        output=destDevice.getReceiver();

        MidiDevice sourceDevice=MidiSystem.getMidiDevice((MidiDevice.Info)sourceInfoVector.get(inport));
        //sourceDevice.open();
        input=sourceDevice.getTransmitter();
        input.setReceiver(this);

        if (faderPort!=inport) {
            sourceDevice=MidiSystem.getMidiDevice((MidiDevice.Info)sourceInfoVector.get(faderPort));
            fader=sourceDevice.getTransmitter();
            fader.setReceiver(this);
        }
	*/
	setInputDeviceNum(inport);
	setOutputDeviceNum(outport);
        initialized = true;
    }

    //this gets called whenever a midimessage arrives at input
    public void send(MidiMessage msg,long l) {
	// ignore System Real Time Message
	if ((msg.getStatus() & 0xf8) == 0xf8)
	    return;
	//ErrorMsg.reportStatus("MidiWrapper:Got Message length "+msg.getLength());
        list.add(msg);
    }

    public void close() {
        if (input!=null) {/*input.setReceiver(null);*/ input.close();} //???
        if (fader!=null) {/*fader.setReceiver(null);*/ fader.close();} //???
	if (output!=null) { output.close();}
	MidiDevice.Info[] mdi = MidiSystem.getMidiDeviceInfo();
        for (int i=0;i<mdi.length;i++) {
	    try {
		MidiDevice md = MidiSystem.getMidiDevice(mdi[i]);
		if (md.isOpen())
		    md.close();
	    } catch (MidiUnavailableException e) {
		;		// ignore
	    }
        }
    }

    public void finalize() {
        close();
    }

    private void setInputDeviceNum(int port) {
	if (PatchEdit.newMidiAPI)
	    return;

	if (port >= getNumInputDevices()) {
	    ErrorMsg.reportStatus("setInputDeviceNum: port, " + port + ", is out of range. Set to 0.");
	    port = 0; // temporally fix
	}
        try {
            if ((port==PatchEdit.appConfig.getFaderPort()) && (fader!=null)) {
                MidiDevice srcDevice=MidiSystem.getMidiDevice((MidiDevice.Info)sourceInfoVector.get(port));
                fader=srcDevice.getTransmitter();
                return;
            }

            if (currentInport!=port) {
		if (input != null)
		    //input.setReceiver(null); // ???
		    input.close();
                // input.close();
// 		ErrorMsg.reportStatus(sourceInfoVector + ", " + port);
                MidiDevice srcDevice=MidiSystem.getMidiDevice((MidiDevice.Info)sourceInfoVector.get(port));
		if (!srcDevice.isOpen())
		    srcDevice.open();
                input=srcDevice.getTransmitter();
                input.setReceiver(this);
		currentInport=port;
		ErrorMsg.reportStatus("Inport: " + srcDevice.getDeviceInfo().getName()
				      + " is Open: " + srcDevice.isOpen());
            }
        } catch (Exception e) {
            ErrorMsg.reportError("Error","Wire MIDI is flipping out.",e);
 	    //e.printStackTrace();
	}
    }

    private void setOutputDeviceNum(int port) throws MidiUnavailableException {
	if (PatchEdit.newMidiAPI)
	    return;

        if (currentOutport!=port) {
	    if (output != null)
		output.close();
            MidiDevice destDevice=MidiSystem.getMidiDevice((MidiDevice.Info)destinationInfoVector.get(port));
            if (!destDevice.isOpen())
                destDevice.open();
            output=destDevice.getReceiver();
            ErrorMsg.reportStatus("Outport: " + destDevice.getDeviceInfo().getName()
				  + " is Open: " + destDevice.isOpen());
	    currentOutport=port;
        }
    }

    public void send(int port, MidiMessage msg)
	throws MidiUnavailableException, InvalidMidiDataException {
	setOutputDeviceNum(port);
	int size = msg.getLength();

	if (BUFSIZE == 0 || size <= BUFSIZE) {
	    output.send(msg, -1);
	    MidiUtil.logOut(port, msg);
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
		output.send(msg, -1);
		MidiUtil.logOut(port, msg);
	    }
	}
    }

    public  int getNumInputDevices() {
        return sourceInfoVector.size();
    }

    public  int getNumOutputDevices() {
        return destinationInfoVector.size();
    }

    public  String getInputDeviceName(int port) {
        return ((MidiDevice.Info)(sourceInfoVector.get(port))).getName();
    }

    public  String getOutputDeviceName(int port) {
        return ((MidiDevice.Info)(destinationInfoVector.get(port))).getName();
    }

    public  int messagesWaiting(int port) {
        setInputDeviceNum(port);
        return list.size();
    }

    public void clearMidiInBuffer(int port) {
	list.clear();
    }

    MidiMessage getMessage(int port)
	throws InvalidMidiDataException {
	setInputDeviceNum(port);

	// pop the oldest message
	MidiMessage msg = (MidiMessage) list.remove(0);
	MidiUtil.logIn(port, msg);
	// for java 1.4.2 bug
	msg = (MidiMessage) MidiUtil.fixShortMessage(msg);
	MidiUtil.logIn(port, msg);
	return msg;
    }

    public String getWrapperName() {
	return("JavaSound");
    }

    public static boolean supportsPlatform(String platform) {
	String implementationVersion;

	implementationVersion=java.lang.System.getProperty("java.vm.version");
	if (implementationVersion.startsWith("1.0")   ||      //Everything below 1.4.2 is not good
	    implementationVersion.startsWith("1.1")   ||
	    implementationVersion.startsWith("1.2")   ||
	    implementationVersion.startsWith("1.3")   ||
	    implementationVersion.startsWith("1.4.0") ||
	    implementationVersion.startsWith("1.4.1"))
	    return false;
	if(platform.length()==0 || platform.indexOf("Windows")>-1) {
	    return(true);
	}
	return(false);
    }

    public boolean isReady() {
	return(initialized);
    }

    /**
     * Get input JSLMidiDevice from port number. This method will be
     * useless when "port" is not used.
     */
    public JSLMidiDevice getInputDevice(int port) {
	MidiDevice md = null;
	try {
	    md = MidiSystem.getMidiDevice((MidiDevice.Info) sourceInfoVector.get(port));
	} catch (MidiUnavailableException e) {
	    ErrorMsg.reportStatus("getInputDevice : " + e);
	}
	ErrorMsg.reportStatus("getInputDevice : " + md);
	return new JSLMidiDevice(md);
    }

    /**
     * Get output JSLMidiDevice from port number. This method will be
     * useless when "port" is not used.
     */
    public JSLMidiDevice getOutputDevice(int port) {
	MidiDevice md = null;
	try {
	    md = MidiSystem.getMidiDevice((MidiDevice.Info) destinationInfoVector.get(port));
	} catch (MidiUnavailableException e) {
	    ErrorMsg.reportStatus("getOutputDevice : " + e);
	}
	ErrorMsg.reportStatus("getOutputDevice : " + md);
	return new JSLMidiDevice(md);
    }
}
