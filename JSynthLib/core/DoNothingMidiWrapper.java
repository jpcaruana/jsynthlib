// Create a subclass of this in order to support a new platform. These are the functions you must implement.
package core; //TODO org.jsynthlib.midi;
import javax.sound.midi.MidiMessage;

public class DoNothingMidiWrapper extends MidiWrapper {
	public void init (int inport, int outport) throws Exception {}
	public  DoNothingMidiWrapper() throws Exception {}
	//TODO Change this back to non-public once PrefsDialog is moved out of core.* - emenaker 2003.03.12
	public void setInputDeviceNum(int port)throws Exception {}
	//TODO Change this back to non-public once PrefsDialog is moved out of core.* - emenaker 2003.03.12
	public void setOutputDeviceNum(int port)throws Exception {}
	public  void writeLongMessage (int port,byte []sysex)throws Exception {}
	public  void writeLongMessage (int port,byte []sysex,int size)throws Exception {} 
	public  void writeShortMessage(int port, byte b1, byte b2)throws Exception {}
	public  void writeShortMessage (int port,byte b1, byte b2,byte b3)throws Exception {}
	public  int getNumInputDevices()throws Exception{return 1;}
	public  int getNumOutputDevices()throws Exception{return 1;}
	public  String getInputDeviceName(int port)throws Exception{return "No Devices";}
	public  String getOutputDeviceName(int port)throws Exception{return "No Devices";}
	public  int messagesWaiting(int port)throws Exception{return 0;}
// 	public  int readMessage(int port,byte []sysex,int maxSize)throws Exception{return 0;}
 	public  MidiMessage getMessage(int port) {return null;}
	public String getWrapperName() {return("No Midi Enabled");}
	// Cause the comboboxes to get greyed-out when using this driver.
	public boolean isReady() {
		return(false);
	}
	public void close() {}
}
