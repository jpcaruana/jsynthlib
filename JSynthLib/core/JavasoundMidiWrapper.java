// This is a MidiWrapper for the generic Javasound API
// Danger, to use it you need JDK1.4.2beta at least.....

/**
 *
 * @author Gerrit Gehnen
 * @version $Id$
 */

package core;
import javax.sound.midi.*;
import java.util.*;

public class JavasoundMidiWrapper extends MidiWrapper implements Receiver {
    int currentOutport;
    int currentInport;
    int faderPort;
    MidiDevice.Info[] mdi;
    Vector sourceInfoVector;
    Vector destinationInfoVector;
    MidiDevice sourceDevice;
    MidiDevice destinationDevice;
    Transmitter input=null;
    Transmitter fader=null;
    Receiver output=null;
    MidiDevice md;
    List list = Collections.synchronizedList(new LinkedList());
    boolean initialized;
    
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
                    System.out.println("is possible Destination");
                    destinationInfoVector.add(mdi[i]);
                }
                if (md.getMaxTransmitters()!=0) {
                    System.out.println("is possible Source");
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
        
        int dummy;
        dummy=msg.getStatus();
        if (msg instanceof ShortMessage) {
            //System.out.println("Status: "+((ShortMessage)msg).getStatus());
            // Filter out Active Sensing
            if (((ShortMessage)msg).getStatus()==ShortMessage.ACTIVE_SENSING)
                return;
        }
        //System.out.println("JavasoundMidiWrapper:Got Message length "+msg.getLength()+" Status "+dummy);
        if (msg instanceof SysexMessage) {
            System.out.println("SYSEX: Status: "+msg.getStatus()+" Last Byte "+msg.getMessage()[msg.getMessage().length-1]);
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
    
    /*TODO protected*/public  void setInputDeviceNum(int port)throws Exception {
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
    
    /*TODO protected*/public  void setOutputDeviceNum(int port)throws Exception {
        if (currentOutport!=port) {
            //output.close ();
            MidiDevice destDevice=MidiSystem.getMidiDevice((MidiDevice.Info)destinationInfoVector.get(port));
            output=destDevice.getReceiver();
            if (destDevice.isOpen())
                destDevice.open();
            System.out.println("Outport: "+destDevice.getDeviceInfo().getName()+" is Open: "+destDevice.isOpen());
        }
        currentOutport=port;
    }
    
    public  void writeLongMessage(int port,byte []sysex)throws Exception {
        writeLongMessage(port,sysex,sysex.length);
    }
    
    public  void writeLongMessage(int port,byte []sysex,int size)throws Exception {
        //System.out.println("JavaSoundMapper:Writing to port "+port);
        
        setOutputDeviceNum(port);
        byte [] tmpArray=new byte[255];
        if (size==2) {
            writeShortMessage(port,sysex[0],sysex[1]);
            return;
        }
        if (size==3) {
            writeShortMessage(port,sysex[0],sysex[1],sysex[2]);
            return;
        }
        
        SysexMessage msg = new SysexMessage();
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
    }
    
    public  void writeShortMessage(int port, byte b1, byte b2)throws Exception {
        writeShortMessage(port,b1,b2,(byte)0);
    }
    
    public  void writeShortMessage(int port,byte b1, byte b2,byte b3)throws Exception {
        setOutputDeviceNum(port);
        ShortMessage msg=new ShortMessage();
        msg.setMessage((int)b1,(int)b2,(int)b3);
        output.send(msg,-1);
    }
    
    public  int getNumInputDevices()throws Exception {
        return sourceInfoVector.size();
    }
    
    public  int getNumOutputDevices()throws Exception {
        return destinationInfoVector.size();
    }
    
    public  String getInputDeviceName(int port)throws Exception {
        return ((MidiDevice.Info)(sourceInfoVector.get(port))).getName();
    }
    
    public  String getOutputDeviceName(int port)throws Exception {
        return ((MidiDevice.Info)(destinationInfoVector.get(port))).getName();
    }
    
    public  int messagesWaiting(int port)throws Exception {
        setInputDeviceNum(port);
        return list.size();
    }
    
    public  int readMessage(int port,byte []sysex,int maxSize)throws Exception {
        setInputDeviceNum(port);
        MidiMessage msg = (MidiMessage) list.get(0);
        list.remove(0);
        //System.out.println("MidiMessagelength:"+msg.getLength());
        //System.out.println("MidiMessage: "+msg.getMessage()[0]);
        if (msg.getMessage()[0]==-9) {
            System.arraycopy(msg.getMessage(),1,sysex,0,msg.getLength()-1);
            logMidi(port,true,sysex,msg.getLength()-1);
            return msg.getLength()-1;
        }
        System.arraycopy(msg.getMessage(),0,sysex,0,msg.getLength());
        logMidi(port,true,sysex,msg.getLength());
        return msg.getLength();
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
