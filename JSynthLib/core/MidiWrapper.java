// Create a subclass of this in order to support a new platform. These are the functions you must implement.
package core;
/**
 * @version $Id$
 */
abstract public class MidiWrapper
{
  public  MidiWrapper (int inport, int outport) throws Exception {}
  public  MidiWrapper () throws Exception {}
  protected abstract void setInputDeviceNum(int port)throws Exception;
  protected abstract void setOutputDeviceNum(int port)throws Exception;
  public abstract void writeLongMessage (int port,byte []sysex)throws Exception;
  public abstract void writeLongMessage (int port,byte []sysex,int size)throws Exception;
  public abstract void writeShortMessage(int port, byte b1, byte b2)throws Exception;
  public abstract void writeShortMessage (int port,byte b1, byte b2,byte b3)throws Exception;
  public abstract int getNumInputDevices()throws Exception;
  public abstract int getNumOutputDevices()throws Exception;
  public abstract String getInputDeviceName(int port)throws Exception;
  public abstract String getOutputDeviceName(int port)throws Exception;
  public abstract int messagesWaiting(int port)throws Exception;
  public abstract int readMessage(int port,byte []sysex,int maxSize)throws Exception;
  public void logMidi(int port,boolean in,byte []sysex,int length)
  {
    if ((PatchEdit.midiMonitor!=null) && PatchEdit.midiMonitor.isVisible())
	    PatchEdit.midiMonitor.log(port,in,sysex,length);
  }
  /** This method should return true, if this wrapper is
   * supported on the actual platform (a wrapper for
   * MacOSX should return true only on Mac's with OSX
   * etc.)
   * @return true, if wrapper is supported, false if wrapper is not supported at this
   * platform.
   */  
  public abstract boolean isSupported() throws Exception;
  public void close() {}
  
  
}
