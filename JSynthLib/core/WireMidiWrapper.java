// These are the MIDI routines using WireProvider for Windows
package core; //TODO org.jsynthlib.midi;
import javax.sound.midi.*;
import java.util.*;
// import core.*; // FIXME Eventually, core.* will go away.

public class WireMidiWrapper extends MidiWrapper implements Receiver {
    private int currentOutport;
    private int currentInport;
    private int faderPort;
    private MidiDevice.Info[] mdi;
    private Vector sourceInfoVector;
    private Vector destinationInfoVector;
    private MidiDevice sourceDevice;
    private MidiDevice destinationDevice;
    private Transmitter input=null;
    private Transmitter fader=null;
    private Receiver output=null;
    private MidiDevice md;
    private List list = Collections.synchronizedList (new LinkedList ());
    private boolean initialized;

    public WireMidiWrapper() throws Exception {}

    // This used to be the constructor with (int,int) params
    // I needed to change the (int,int) constructor to some non-constructor thing, because I neede to
    // have an instance of every eligible midi wrapper ahead of time. - emenaker 2003.03.12
    public void init(int inport, int outport) throws DriverInitializationException, MidiUnavailableException {
	initialized = false;
	currentInport=inport;
	currentOutport=outport;
	faderPort=PatchEdit.appConfig.getFaderPort();

	sourceInfoVector=new Vector ();
	destinationInfoVector=new Vector ();
// 	ErrorMsg.reportStatus("WireMidiWrapper.init" + sourceInfoVector + ", " + inport + ", " + outport);

	mdi=MidiSystem.getMidiDeviceInfo ();

	// This loop populates the InfoVectors with MidiDeviceInfo objects that are NOT
	// from the built-in JavaSound system
	for (int i=0;i<mdi.length;i++) {
//  	    try {
	    md=MidiSystem.getMidiDevice (mdi[i]);

	    if (md.getMaxReceivers ()!=0) {
		if ((mdi[i].getClass ().toString ().indexOf ("com.sun.media.sound")<0))
		    destinationInfoVector.add (mdi[i]);
	    }

	    if (md.getMaxTransmitters ()!=0) {
		if ((mdi[i].getClass ().toString ().indexOf ("com.sun.media.sound")<0))
		    sourceInfoVector.add (mdi[i]);
	    }
//  	} catch (Exception e) {
//  	    ErrorMsg.reportError("Error","Something really strange happened during initialization?",e);
//  	    e.printStackTrace();
//  	}
	}
	if(destinationInfoVector.size()==0) {
	    ErrorMsg.reportStatus("No output ports");
	    ErrorMsg.reportError("Error","No Midi output ports were found\nIs WireProvider installed?");
	    output=null;
	    initialized = false;
	    return;
	}
	if(sourceInfoVector.size()==0) {
	    ErrorMsg.reportStatus("No input ports");
	    ErrorMsg.reportError("Error","No Midi input ports were found\nIs WireProvider installed?");
	    input=null;
	    fader=null;
	    initialized = false;
	    return;
	}
        try {
	    MidiDevice destDevice=MidiSystem.getMidiDevice((MidiDevice.Info)destinationInfoVector.get(outport));
	    output=destDevice.getReceiver();

	    MidiDevice sourceDevice=MidiSystem.getMidiDevice((MidiDevice.Info)sourceInfoVector.get(inport));
	    input=sourceDevice.getTransmitter();
	    input.setReceiver(this);
	} catch (Exception e) {
	    output=null;
	    input=null;
	    initialized = false;
	    ErrorMsg.reportError("Error","No Midi ports avaliable\nIs WireProvider installed?",e);
// 	    e.printStackTrace();
        }
	if (faderPort!=inport) {
	    sourceDevice=MidiSystem.getMidiDevice((MidiDevice.Info)sourceInfoVector.get(faderPort));
	    fader=sourceDevice.getTransmitter();
	    fader.setReceiver(this);
	}
	initialized = true;
    }

    //this gets called whenever a midimessage arrives at input
    public void send (MidiMessage msg,long l) {
	// ignore System Real Time Message
	if ((msg.getStatus() & 0xf8) == 0xf8)
	    return;
//         if (msg instanceof ShortMessage
//             && ((ShortMessage) msg).getStatus() == ShortMessage.ACTIVE_SENSING)
//                 return;
	//System.out.println("MidiWrapper:Got Message length "+msg.getLength());
	list.add (msg);
    }

    public void close () {
	if (input!=null) {input.setReceiver(null); input.close();}
	if (fader!=null) {fader.setReceiver(null); fader.close();}
	if (output!=null) {output.close();}
    }

    public void finalize() {
	close();
    }

    //FIXME Made public so that PrefsDialog can call it until we straighten this mess out - emenaker 2003.03.12
    public void setInputDeviceNum (int port)  {
	try {
	    if ((port==PatchEdit.appConfig.getFaderPort()) && (fader!=null)) {
		MidiDevice srcDevice=MidiSystem.getMidiDevice ((MidiDevice.Info)sourceInfoVector.get (port));
		fader=srcDevice.getTransmitter ();
		return;
	    };
	    if (currentInport!=port) {
 		input.setReceiver (null);
		input.close ();
// 		ErrorMsg.reportStatus(sourceInfoVector + ", " + port);
 		MidiDevice srcDevice=MidiSystem.getMidiDevice ((MidiDevice.Info)sourceInfoVector.get (port));
		input=srcDevice.getTransmitter ();
		input.setReceiver (this);
	    }
	    currentInport=port;
	} catch (Exception e) {
// 	    e.printStackTrace ();
	    ErrorMsg.reportError ("Error","Wire MIDI is flipping out.",e);
	}
    }

    //FIXME Made public so that PrefsDialog can call it until we straighten this mess out - emenaker 2003.03.12
    /*
    public void setOutputDeviceNum (int port) throws MidiUnavailableException
    {
	if (currentOutport!=port) {
	    output.close ();
	    MidiDevice destDevice=MidiSystem.getMidiDevice ((MidiDevice.Info)destinationInfoVector.get (port));
	    output=destDevice.getReceiver ();
	}
	currentOutport=port;
    }
    */
    public  void writeLongMessage (int port,byte []sysex)
	throws InvalidMidiDataException {
	writeLongMessage (port,sysex,sysex.length);
    }

    public  void writeLongMessage (int port,byte []sysex,int size)
	throws InvalidMidiDataException {
        //setOutputDeviceNum(port);
        if (size==2) {
            writeShortMessage(port,sysex[0],sysex[1]);
            return;
        }
        if (size==3) {
            writeShortMessage(port,sysex[0],sysex[1],sysex[2]);
            return;
        }

	// System Exclusive Message
	SysexMessage msg = new SysexMessage ();
	//final int BUFSIZE = 250;
	final int BUFSIZE = 0;
	if (BUFSIZE == 0) {
	    msg.setMessage(sysex, size);
	    logMidi(port, false, sysex, size);
	    output.send(msg, -1);
	} else {
	    byte[] tmpArray = new byte[BUFSIZE + 1];
	    for (int i = 0; size > 0; i += BUFSIZE, size -= BUFSIZE) {
		int s = Math.min(size, BUFSIZE);
// 		ErrorMsg.reportStatus("writeLongMessage: size = " + size + ", s = " + s);
		if (i == 0) {
		    System.arraycopy(sysex, i, tmpArray, 0, s);
		    msg.setMessage(tmpArray, s);
		} else {
		    tmpArray[0] = (byte) SysexMessage.SPECIAL_SYSTEM_EXCLUSIVE;
		    System.arraycopy(sysex, i, tmpArray, 1, s);
		    msg.setMessage(tmpArray, ++s);
		}
		logMidi(port, false, tmpArray, s);
		output.send(msg, -1);
	    }
	}
    }

    public  void writeShortMessage (int port, byte b1, byte b2)
	throws InvalidMidiDataException {
	writeShortMessage (port,b1,b2,(byte)0);
    }

    public  void writeShortMessage (int port,byte b1, byte b2,byte b3)
	throws InvalidMidiDataException {
	//setOutputDeviceNum (port);
	ShortMessage msg=new ShortMessage ();
	msg.setMessage ((int) (b1 & 0xff), (int) (b2 & 0xff), (int) (b3 & 0xff));
	output.send (msg,-1);
    }

    public  int getNumInputDevices () {
	return sourceInfoVector.size ();
    }

    public  int getNumOutputDevices () {
	return destinationInfoVector.size ();
    }

    public  String getInputDeviceName (int port) {
	return ((MidiDevice.Info)(sourceInfoVector.get (port))).getName ().substring (5);
    }

    public  String getOutputDeviceName (int port) {
	return ((MidiDevice.Info)(destinationInfoVector.get(port))).getName().substring(5);
    }

    public  int messagesWaiting (int port) {
	setInputDeviceNum (port);
	return list.size();
    }

    public void clearMidiInBuffer(int port) {
	while (!list.isEmpty())
	    list.remove(0);
    }

    /*
    public  int readMessage (int port,byte []sysex,int maxSize)throws Exception {
	setInputDeviceNum(port); // Is this necessary?

	// pop the oldest message
	MidiMessage msg = (MidiMessage) list.get(0);
	list.remove(0);
//   	logMidi("readMessage:\n");
//   	logMidi(port, true, msg.getMessage(), msg.getLength());

	int offset, size;
	if (msg.getMessage()[0] == (byte) SysexMessage.SPECIAL_SYSTEM_EXCLUSIVE
	    && msg.getLength() != 1) {
	    // If the 1st byte is 0xf0 and it's not END_OF_EXCLUSIVE, skip it.
	    offset = 1;
	    size = msg.getLength() - 1;
	} else {
	    offset = 0;
	    size = msg.getLength();
	}

	if (size > maxSize) {
	    // push back the remaining message into the 'list'.
	    byte[] b = new byte[size - maxSize + 1];
	    b[0] = (byte) SysexMessage.SPECIAL_SYSTEM_EXCLUSIVE;
	    System.arraycopy(msg.getMessage(), maxSize + offset, b, 1, b.length - 1);
	    SysexMessage msgRemained = new SysexMessage();
	    msgRemained.setMessage(b, b.length);
	    list.add(0, msgRemained);

	    size = maxSize;
	}
	// copy to sysex
//  	logMidi("readMessage: size=" + size + ", offset=" + offset +"\n");
	System.arraycopy(msg.getMessage (), offset, sysex, 0, size);
	logMidi(port, true, sysex, size);
	return size;
    }
    */
    MidiMessage getMessage (int port) {
	setInputDeviceNum(port);

	// pop the oldest message
	MidiMessage msg = (MidiMessage) list.remove(0);
	logMidi(port, true, msg);
	return msg;
    }

    public String getWrapperName() {
	return("MS Windows (WireProvider)");
    }

    public static boolean supportsPlatform(String platform) {
	if(platform.length()==0 || platform.indexOf("Windows")>-1) {
	    return(true);
	}
	return(false);
    }

    public boolean isReady() {
	return(initialized);
    }
}
