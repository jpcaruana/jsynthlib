// These are the MIDI routines using WireProvider for Windows
package core;
import javax.sound.midi.*;
import java.util.*;

public class WireMidiWrapper extends MidiWrapper implements Receiver
{
  int currentOutport;
  int currentInport;
  int faderPort;
  MidiDevice.Info mdi[];
  Vector sourceInfoVector;
  Vector destinationInfoVector;
  MidiDevice sourceDevice;
  MidiDevice destinationDevice;
  Transmitter input=null;
  Transmitter fader=null;
  Receiver output=null;
  MidiDevice md;
  List list = Collections.synchronizedList (new LinkedList ());
  public  WireMidiWrapper (int inport, int outport) throws Exception
  {
    currentInport=inport;
    currentOutport=outport;
    faderPort=PatchEdit.prefsDialog.faderPort;
    
    sourceInfoVector=new Vector ();
    destinationInfoVector=new Vector ();
    
    mdi=MidiSystem.getMidiDeviceInfo ();
    
    for (int i=0;i<mdi.length;i++)
    {
      try
      {
        md=MidiSystem.getMidiDevice (mdi[i]);
        
        if (md.getMaxReceivers ()!=0)
        {
          if ((mdi[i].getClass ().toString ().indexOf ("com.sun.media.sound")<0))
            destinationInfoVector.add (mdi[i]);
        }
        
        
        if (md.getMaxTransmitters ()!=0)
        {
          if ((mdi[i].getClass ().toString ().indexOf ("com.sun.media.sound")<0))
            sourceInfoVector.add (mdi[i]);
        }
      }
      catch (Exception e)
      {e.printStackTrace ();}
    }
    
    MidiDevice destDevice=MidiSystem.getMidiDevice ((MidiDevice.Info)destinationInfoVector.get (outport));
    output=destDevice.getReceiver ();
    
    MidiDevice sourceDevice=MidiSystem.getMidiDevice ((MidiDevice.Info)sourceInfoVector.get (inport));
    input=sourceDevice.getTransmitter ();
    input.setReceiver (this);
    
    if (faderPort!=inport)
    {
      sourceDevice=MidiSystem.getMidiDevice ((MidiDevice.Info)sourceInfoVector.get (faderPort));
      fader=sourceDevice.getTransmitter ();
      fader.setReceiver (this);
    }
  }
  
  public  WireMidiWrapper () throws Exception
  {this(0,0);}
  
  //this gets called whenever a midimessage arrives at input
  public void send (MidiMessage msg,long l)
  {
      //System.out.println("MidiWrapper:Got Message length "+msg.getLength());
    list.add (msg);
  }
  public void close ()
  {
   if (input!=null) {input.setReceiver(null); input.close();}
   if (fader!=null) {fader.setReceiver(null); fader.close();}
   if (output!=null) {output.close();}
  }
     
     public void finalize() 
     {
         close();
     }
     
  protected  void setInputDeviceNum (int port)throws Exception
  {
    try
    {
      if ((port==PatchEdit.prefsDialog.faderPort) && (fader!=null))
      {
        MidiDevice srcDevice=MidiSystem.getMidiDevice ((MidiDevice.Info)sourceInfoVector.get (port));
        fader=srcDevice.getTransmitter ();
        
        return;
      };
      if (currentInport!=port)
      {
        input.setReceiver (null);
        input.close ();
        MidiDevice srcDevice=MidiSystem.getMidiDevice ((MidiDevice.Info)sourceInfoVector.get (port));
        input=srcDevice.getTransmitter ();
        input.setReceiver (this);
      }
      currentInport=port;
    }catch (Exception e)
    {e.printStackTrace ();
ErrorMsg.reportError ("Error","Wire MIDI is flipping out.",e);}
  }
  protected  void setOutputDeviceNum (int port)throws Exception
  {
    if (currentOutport!=port)
    {
      output.close ();
      MidiDevice destDevice=MidiSystem.getMidiDevice ((MidiDevice.Info)destinationInfoVector.get (port));
      output=destDevice.getReceiver ();
    }
    currentOutport=port;
  }
  
  public  void writeLongMessage (int port,byte []sysex)throws Exception
  {writeLongMessage (port,sysex,sysex.length);}
  public  void writeLongMessage (int port,byte []sysex,int size)throws Exception
  {
      //System.out.println("WireMidiWrapper:Writing to port "+port);
    setOutputDeviceNum (port);
    byte [] tmpArray=new byte[255];
    if (size==2)
    {writeShortMessage (port,sysex[0],sysex[1]);return;}
    if (size==3)
    {writeShortMessage (port,sysex[0],sysex[1],sysex[2]);return;}
    
    SysexMessage msg = new SysexMessage ();
    //msg.setMessage(sysex,size);
    for (int i=0 ;i<sysex.length;i+=250)
    {
      if (i==0)
      {
        if (((i+250))<sysex.length)
        {System.arraycopy (sysex,i,tmpArray,0,250);}
        else
        {System.arraycopy (sysex,i,tmpArray,0,(sysex.length%250));}
        if (((i+250))<sysex.length) msg.setMessage (tmpArray,250); else
          msg.setMessage (tmpArray,sysex.length%250);
      }
      else
      {
        if (((i+250))<sysex.length)
        { tmpArray[0]=(byte)0xF7;System.arraycopy (sysex,i,tmpArray,1,250);}
        else
        {tmpArray[0]=(byte)0xF7;System.arraycopy (sysex,i,tmpArray,1,sysex.length%250);}
        if (((i+250))<sysex.length) msg.setMessage (tmpArray,251); else
          msg.setMessage (tmpArray,(sysex.length%250)+1);
      }
       logMidi(port,false,sysex,size);
      output.send (msg,-1);
    }
  }
  
  public  void writeShortMessage (int port, byte b1, byte b2)throws Exception
  {
    writeShortMessage (port,b1,b2,(byte)0);
  }
  
  public  void writeShortMessage (int port,byte b1, byte b2,byte b3)throws Exception
  {
    
    setOutputDeviceNum (port);
    ShortMessage msg=new ShortMessage ();
    msg.setMessage ((int)b1,(int)b2,(int)b3);
    output.send (msg,-1);
     
  }
  
  public  int getNumInputDevices ()throws Exception
  {return sourceInfoVector.size ();}
  public  int getNumOutputDevices ()throws Exception
  {return destinationInfoVector.size ();}
  public  String getInputDeviceName (int port)throws Exception
  {return ((MidiDevice.Info)(sourceInfoVector.get (port))).getName ().substring (5);}
  public  String getOutputDeviceName (int port)throws Exception
  {return ((MidiDevice.Info)(destinationInfoVector.get (port))).getName ().substring (5);}
  public  int messagesWaiting (int port)throws Exception
  {setInputDeviceNum (port);return list.size ();}
  public  int readMessage (int port,byte []sysex,int maxSize)throws Exception
  {
    setInputDeviceNum (port);
    MidiMessage msg = (MidiMessage) list.get (0);
    list.remove (0);
    if (msg.getMessage ()[0]==-9)
    {
      System.arraycopy (msg.getMessage (),1,sysex,0,msg.getLength ()-1);
      logMidi(port,true,sysex,msg.getLength()-1);
      return msg.getLength ()-1;
    }
    System.arraycopy (msg.getMessage (),0,sysex,0,msg.getLength ());
     logMidi(port,true,sysex,msg.getLength());
    return msg.getLength ();
  }
}