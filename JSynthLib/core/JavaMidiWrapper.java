/* This is a wrapper around the JavaMIDI routines which deal with Midi I/O under MacOS & Windows. It seemed clever to write
   a wrapper around the library rather than to use the Library directly. To support a new platform, all that needs to be
   done is to implement a wrapper class which extends MidiWrapper and change one line in PatchEdit.java to instantiate the
   correct wrapper 
*/

package core;
import javax.swing.*;
import jmidi.*;
public class JavaMidiWrapper extends MidiWrapper
{
  MidiPort javaMidiPort;
  MidiPort faderMidiPort;
  int faderInPort;
  int currentInPort;
  int currentOutPort;
  public  JavaMidiWrapper (int inport, int outport) throws Exception
  {
   javaMidiPort=new MidiPort(inport,outport);
   currentInPort=inport; currentOutPort=outport;
   
   if ((currentInPort!=PatchEdit.appConfig.getFaderPort()) && (PatchEdit.appConfig.getFaderEnable()))
   {faderMidiPort=new MidiPort(PatchEdit.appConfig.getFaderPort(),outport);
   try{faderMidiPort.open();} catch (Exception e) {JOptionPane.showMessageDialog(null, "Unable to Initialize MIDI IN/OUT! \nMidi Transfer will be unavailable this session.\n The MidiOut Driver you have chosen for initialization is not a multi client driver.\n Either chose a multi-client driver, or disable faders under Config->Preferences->Faders.\n Then Restart. .","Error", JOptionPane.ERROR_MESSAGE);};
  };
   try{javaMidiPort.open();} catch (Exception e) {JOptionPane.showMessageDialog(null, "Unable to Initialize MIDI IN/OUT! \nMidi Transfer will be unavailable this session.\n Try changing initialization ports under Config->Preferences->MIDI and restarting.","Error",JOptionPane.ERROR_MESSAGE);};
  }
 public  JavaMidiWrapper () throws Exception
  {
   javaMidiPort=new MidiPort(0,0);
   currentInPort=0; currentOutPort=0;
  }
 
//FIXME: Never call this even though its public, I need to call it from prefsDialog to work around a JavaMIDI bug though.
  public  void setInputDeviceNum(int port) throws Exception
  {
	  if (currentInPort!=port)
	    {
	      javaMidiPort.setDeviceNumber(MidiPort.MIDIPORT_INPUT,port);
	    } 
    currentInPort=port;
  
  }
  protected  void setOutputDeviceNum(int port) throws Exception
  {
   if (currentOutPort!=port) 
     { 
	javaMidiPort.setDeviceNumber(MidiPort.MIDIPORT_OUTPUT,port);
     }
    currentOutPort=port;
  }
  public  void writeLongMessage (int port,byte []sysex) throws Exception
  {
     setOutputDeviceNum(port);
	   javaMidiPort.writeLongMessage(sysex,sysex.length,0);
  }
  public  void writeLongMessage (int port,byte []sysex,int length) throws Exception
  {
     setOutputDeviceNum(port);
     javaMidiPort.writeLongMessage(sysex,length,0);
  }
  public void writeShortMessage(int port, byte b1, byte b2) throws Exception
  {
    setOutputDeviceNum(port);
    javaMidiPort.writeShortMessage(b1,b2);
  }
  public void writeShortMessage (int port,byte b1, byte b2,byte b3) throws Exception
  {
    setOutputDeviceNum(port);
    javaMidiPort.writeShortMessage(b1,b2,b3);
  }
  
  public  int getNumInputDevices() throws Exception{return javaMidiPort.getNumDevices(MidiPort.MIDIPORT_INPUT);}
  public  int getNumOutputDevices()throws Exception{return javaMidiPort.getNumDevices(MidiPort.MIDIPORT_OUTPUT);}
  public  String getInputDeviceName(int port)throws Exception {return javaMidiPort.getDeviceName(MidiPort.MIDIPORT_INPUT,port);}
  public  String getOutputDeviceName(int port)throws Exception {return javaMidiPort.getDeviceName(MidiPort.MIDIPORT_OUTPUT,port);}
  public  int messagesWaiting(int port)throws Exception
  {
   if (port==PatchEdit.appConfig.getFaderPort()) return (faderMidiPort.messagesWaiting());
   setInputDeviceNum(port);
   return javaMidiPort.messagesWaiting();
  }
  public  int readMessage(int port,byte []sysex,int maxSize) throws Exception
  {
   if (port==PatchEdit.appConfig.getFaderPort()) return (faderMidiPort.readMessage(sysex,maxSize));
   setInputDeviceNum(port);
   return(javaMidiPort.readMessage(sysex,maxSize));
  }
  
  
  
}
