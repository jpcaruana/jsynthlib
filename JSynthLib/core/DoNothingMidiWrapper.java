// Create a subclass of this in order to support a new platform. These are the functions you must implement.
package core;
 public class DoNothingMidiWrapper extends MidiWrapper
{
  public  DoNothingMidiWrapper (int inport, int outport) throws Exception {}
  public  DoNothingMidiWrapper () throws Exception {}
  protected  void setInputDeviceNum(int port)throws Exception {}
  protected  void setOutputDeviceNum(int port)throws Exception {}
  public  void writeLongMessage (int port,byte []sysex)throws Exception {}
  public  void writeLongMessage (int port,byte []sysex,int size)throws Exception {} 
  public  void writeShortMessage(int port, byte b1, byte b2)throws Exception {}
  public  void writeShortMessage (int port,byte b1, byte b2,byte b3)throws Exception {}
  public  int getNumInputDevices()throws Exception{return 1;}
  public  int getNumOutputDevices()throws Exception{return 1;}
  public  String getInputDeviceName(int port)throws Exception{return "-";}
  public  String getOutputDeviceName(int port)throws Exception{return "-";}
  public  int messagesWaiting(int port)throws Exception{return 0;}
  public  int readMessage(int port,byte []sysex,int maxSize)throws Exception{return 0;}
  
  /** This method should return true, if this wrapper is
   * supported on the actual platform (a wrapper for
   * MacOSX should return true only on Mac's with OSX
   * etc.)
   * @return true, if wrapper is supported, false if wrapper is not supported at this
   * platform.
   *
   */
  public boolean isSupported() throws Exception {
      // Yep, that's the real implementation.....
      return true;
  }
  
}
