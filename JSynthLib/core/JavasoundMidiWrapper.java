
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
    private int currentOutport;
    private int currentInport;
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
        currentInport=inport;
        currentOutport=outport;
        faderPort=PatchEdit.appConfig.getFaderPort();

        sourceInfoVector=new Vector();
        destinationInfoVector=new Vector();
// 	ErrorMsg.reportStatus("WireMidiWrapper.init" + sourceInfoVector + ", " + inport + ", " + outport);

	MidiDevice.Info[] mdi = MidiSystem.getMidiDeviceInfo();

        for (int i=0;i<mdi.length;i++) {
            try {
		MidiDevice md = MidiSystem.getMidiDevice(mdi[i]);
                md.open(); // This can really throw an MidiUnavailableException on my System

                if (md.getMaxReceivers()!=0) {
// 		    ErrorMsg.reportStatus("is possible Destination");
                    destinationInfoVector.add(mdi[i]);
                }
                if (md.getMaxTransmitters()!=0) {
// 		    ErrorMsg.reportStatus("is possible Source");
                    sourceInfoVector.add(mdi[i]);
                }
            }
            catch (MidiUnavailableException e) {} // Ignore, can happen.....
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        MidiDevice destDevice=MidiSystem.getMidiDevice((MidiDevice.Info)destinationInfoVector.get(outport));
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
        initialized = true;
    }

    //this gets called whenever a midimessage arrives at input
    public void send(MidiMessage msg,long l) {
	// ignore System Real Time Message
	if ((msg.getStatus() & 0xf8) == 0xf8)
	    return;
	//System.out.println("MidiWrapper:Got Message length "+msg.getLength());
        list.add(msg);
    }

    public void close() {
        if (input!=null) {input.setReceiver(null); /*input.close();*/}
        if (fader!=null) {fader.setReceiver(null); /*fader.close();*/}
        /*   if (output!=null) { output.close();}*/
	MidiDevice.Info[] mdi = MidiSystem.getMidiDeviceInfo();
        for (int i=0;i<mdi.length;i++) {
	    try {
		MidiDevice md = MidiSystem.getMidiDevice(mdi[i]);
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
        try {
            if ((port==PatchEdit.appConfig.getFaderPort()) && (fader!=null)) {
                MidiDevice srcDevice=MidiSystem.getMidiDevice((MidiDevice.Info)sourceInfoVector.get(port));
                fader=srcDevice.getTransmitter();
                return;
            }

            if (currentInport!=port) {
                input.setReceiver(null);
                // input.close();
// 		ErrorMsg.reportStatus(sourceInfoVector + ", " + port);
                MidiDevice srcDevice=MidiSystem.getMidiDevice((MidiDevice.Info)sourceInfoVector.get(port));
                // srcDevice.open();
                input=srcDevice.getTransmitter();
                input.setReceiver(this);
            }
            currentInport=port;
        } catch (Exception e) {
// 	    e.printStackTrace();
            ErrorMsg.reportError("Error","Wire MIDI is flipping out.",e);
	}
    }

    private void setOutputDeviceNum(int port) throws MidiUnavailableException {
        if (currentOutport!=port) {
            //output.close();
            MidiDevice destDevice=MidiSystem.getMidiDevice((MidiDevice.Info)destinationInfoVector.get(port));
            output=destDevice.getReceiver();
            if (destDevice.isOpen()) // !!!FIXIT!!!
                destDevice.open();
            ErrorMsg.reportStatus("Outport: "+destDevice.getDeviceInfo().getName()+" is Open: "+destDevice.isOpen());
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
	while (!list.isEmpty())
	    list.remove(0);
    }

    MidiMessage getMessage(int port) {
	setInputDeviceNum(port);

	// pop the oldest message
	MidiMessage msg = (MidiMessage) list.remove(0);
	MidiUtil.logIn(port, msg);
	return msg;
    }

    public String getWrapperName() {
	return("MS Windows (JavaSound)");
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
}
