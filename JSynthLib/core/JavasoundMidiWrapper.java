
/**
 * This is a MidiWrapper for the generic Javasound API
 * Danger, to use it you need JDK1.4.2beta at least.....
 *
 * @author Gerrit Gehnen
 * @version $Id$
 */

package core;
import javax.sound.midi.*;
import java.util.*;

public class JavasoundMidiWrapper extends MidiWrapper implements Receiver {
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
    private List list = Collections.synchronizedList(new LinkedList());
    private boolean initialized;

    // This used to be the constructor with (int,int) params
    // I needed to change the (int,int) constructor to some non-constructor thing, because I neede to
    // have an instance of every eligible midi wrapper ahead of time. - emenaker 2003.03.12
    public void init(int inport, int outport) throws DriverInitializationException, MidiUnavailableException {
        initialized = false;
        currentInport=inport;
        currentOutport=outport;
        faderPort=PatchEdit.appConfig.getFaderPort();

        sourceInfoVector=new Vector();
        destinationInfoVector=new Vector();

        mdi=MidiSystem.getMidiDeviceInfo();

        for (int i=0;i<mdi.length;i++) {
            try {
                md=MidiSystem.getMidiDevice(mdi[i]);
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

    public  JavasoundMidiWrapper() throws Exception {
        /* this(0,0); Mustn't do this. We need to be able to instantiate without trying to grab any ports - emenaker 2003.09.01 */
    }

    //this gets called whenever a midimessage arrives at input
    public void send(MidiMessage msg,long l) {
        if (msg instanceof ShortMessage) {
            //ErrorMsg.reportStatus("Status: "+((ShortMessage)msg).getStatus());
            // Filter out Active Sensing
            if (((ShortMessage)msg).getStatus()==ShortMessage.ACTIVE_SENSING)
                return;
        }
        //ErrorMsg.reportStatus("JavasoundMidiWrapper:Got Message length "+msg.getLength()+" Status "+msg.getStatus());
        if (msg instanceof SysexMessage) {
            ErrorMsg.reportStatus("SYSEX: Status: "+msg.getStatus()+" Last Byte "+msg.getMessage()[msg.getMessage().length-1]);
        }
        list.add(msg);
    }

    public void close() {
        if (input!=null) {input.setReceiver(null); /*input.close();*/}
        if (fader!=null) {fader.setReceiver(null);/* fader.close();*/}
        /*   if (output!=null) { output.close();}*/
        for (int i=0;i<mdi.length;i++) {
            md.close();
        }
    }

    public void finalize() {
        close();
    }

    /*TODO protected*/public  void setInputDeviceNum(int port) {
        try {
            if ((port==PatchEdit.appConfig.getFaderPort()) && (fader!=null)) {
                MidiDevice srcDevice=MidiSystem.getMidiDevice((MidiDevice.Info)sourceInfoVector.get(port));
                fader=srcDevice.getTransmitter();

                return;
            };

            if (currentInport!=port) {
                input.setReceiver(null);
                //        input.close ();
                MidiDevice srcDevice=MidiSystem.getMidiDevice((MidiDevice.Info)sourceInfoVector.get(port));
                //        srcDevice.open();
                input=srcDevice.getTransmitter();
                input.setReceiver(this);
            }
            currentInport=port;
        }
        catch (Exception e) {
            e.printStackTrace();
            ErrorMsg.reportError("Error","Wire MIDI is flipping out.",e);}
    }

    /*TODO protected*/public  void setOutputDeviceNum(int port)
	throws MidiUnavailableException {
        if (currentOutport!=port) {
            //output.close ();
            MidiDevice destDevice=MidiSystem.getMidiDevice((MidiDevice.Info)destinationInfoVector.get(port));
            output=destDevice.getReceiver();
            if (destDevice.isOpen())
                destDevice.open();
            ErrorMsg.reportStatus("Outport: "+destDevice.getDeviceInfo().getName()+" is Open: "+destDevice.isOpen());
        }
        currentOutport=port;
    }

    public  void writeLongMessage(int port,byte []sysex)
	throws InvalidMidiDataException, MidiUnavailableException {
        writeLongMessage(port,sysex,sysex.length);
    }

    public  void writeLongMessage(int port,byte []sysex,int size)
	throws InvalidMidiDataException, MidiUnavailableException {
        //ErrorMsg.reportStatus("JavaSoundMapper:Writing to port "+port);

        setOutputDeviceNum(port);
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
	    output.send(msg, -1);
	    MidiUtil.logOut(port, msg);
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
		output.send(msg, -1);
		MidiUtil.logOut(port, msg);
	    }
	}
	/*
	// original code
	byte [] tmpArray=new byte[255];
        //msg.setMessage(sysex,size);
        for (int i=0 ;i<sysex.length;i+=250) {
            if (i==0) {
                if (((i+250))<sysex.length) {
                    System.arraycopy(sysex,i,tmpArray,0,250);
                }
                else {
                    System.arraycopy(sysex,i,tmpArray,0,(sysex.length%250));
                }
                if (((i+250))<sysex.length)
                    msg.setMessage(tmpArray,250);
                else
                    msg.setMessage(tmpArray,sysex.length%250);
            }
            else {
                if (((i+250))<sysex.length) {
                    tmpArray[0]=(byte)0xF7;System.arraycopy(sysex,i,tmpArray,1,250);
                }
                else {
                    tmpArray[0]=(byte)0xF7;System.arraycopy(sysex,i,tmpArray,1,sysex.length%250);
                }
                if (((i+250))<sysex.length)
                    msg.setMessage(tmpArray,251);
                else
                    msg.setMessage(tmpArray,(sysex.length%250)+1);
            }
            logMidi(port,false,sysex,size);
            output.send(msg,-1);
        }
    */
    }
    public  void writeShortMessage(int port, byte b1, byte b2)
	throws InvalidMidiDataException, MidiUnavailableException {
        writeShortMessage(port,b1,b2,(byte)0);
    }

    public  void writeShortMessage(int port,byte b1, byte b2,byte b3)
	throws InvalidMidiDataException, MidiUnavailableException {
        setOutputDeviceNum(port);
        ShortMessage msg=new ShortMessage();
	msg.setMessage ((int) (b1 & 0xff), (int) (b2 & 0xff), (int) (b3 & 0xff));
        output.send(msg,-1);
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
	while (! list.isEmpty())
	    list.remove(0);
    }

    /*
    public  int readMessage(int port,byte []sysex,int maxSize)throws Exception {
        setInputDeviceNum(port);
        MidiMessage msg = (MidiMessage) list.get(0);
        list.remove(0);
        //ErrorMsg.reportStatus("MidiMessagelength:"+msg.getLength());
        //ErrorMsg.reportStatus("MidiMessage: "+msg.getMessage()[0]);
        if (msg.getMessage()[0]==-9) {
            System.arraycopy(msg.getMessage(),1,sysex,0,msg.getLength()-1);
            logMidi(port,true,sysex,msg.getLength()-1);
            return msg.getLength()-1;
        }
        System.arraycopy(msg.getMessage(),0,sysex,0,msg.getLength());
        logMidi(port,true,sysex,msg.getLength());
        return msg.getLength();
    }
    */
    MidiMessage getMessage (int port) {
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
