package CAProvider;

import java.util.*;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.spi.MidiDeviceProvider;

import com.apple.audio.midi.*;
import com.apple.audio.util.*;
import com.apple.audio.CAException;

public class CAMidiDeviceProvider extends MidiDeviceProvider {

  private static MIDIClient client = null;
  private static MIDIOutputPort output;
  private static HashMap deviceMap = new LinkedHashMap(20);
  private static NotificationReciever nproc = null;

  public CAMidiDeviceProvider() throws CAException {
    if (client == null) {
      nproc = new NotificationReciever();
      client = new MIDIClient(new CAFString("CAProvider"), null);
      output = client.outputPortCreate(
                       new CAFString("CAMidiDeviceProvider Output")); 
      try {
	buildDeviceMap();
      } catch (CAException e) {
	e.printStackTrace();
	throw e;
      }
    }
  }

  public MidiDevice getDevice(MidiDevice.Info info) {
    if (!isDeviceSupported(info))
      throw new IllegalArgumentException();

    CAMidiDeviceInfo cainfo = (CAMidiDeviceInfo)info;
    return (MidiDevice)deviceMap.get(cainfo.getUniqueID());
  }

  public MidiDevice.Info[] getDeviceInfo() {
    if (deviceMap == null)
      return new MidiDevice.Info[0]; 
    MidiDevice.Info[] info = new MidiDevice.Info[deviceMap.size()];
    Iterator it = deviceMap.values().iterator();

    MidiDevice i;
    int counter = 0;
    while (it.hasNext()) {
      i = (MidiDevice)it.next();
      info[counter++] = (CAMidiDeviceInfo)i.getDeviceInfo();
    }

    return info;
  }

  public boolean isDeviceSupported(MidiDevice.Info info) {
    if (deviceMap == null || info == null)
      return false;

    if (info instanceof CAMidiDeviceInfo) {
      CAMidiDeviceInfo cainfo = (CAMidiDeviceInfo)info;
      if (deviceMap.containsKey(cainfo.getUniqueID()))
	return true;
    }
    return false;
  }

  static MIDIClient getMIDIClient () throws CAException {
    if (client == null)
      new CAMidiDeviceProvider();
    return client;
  }
  static MIDIOutputPort getOutputPort() {
    return output;
  }

  private void buildDeviceMap() throws CAException {
    int count = MIDISetup.getNumberOfSources();
    for (int source = 0; source < count; source++) {
      MIDIEndpoint ep = MIDISetup.getSource(source);
      Integer uid =
	new Integer(ep.getProperty(MIDIConstants.kMIDIPropertyUniqueID));
      
      if (!deviceMap.containsKey(uid))
	deviceMap.put(uid, new CAMIDISource(ep, uid));
    }
    count = MIDISetup.getNumberOfDestinations();
    for (int dest = 0; dest < count; dest++) {
      MIDIEndpoint ep = MIDISetup.getDestination(dest);
      Integer uid =
	new Integer(ep.getProperty(MIDIConstants.kMIDIPropertyUniqueID));
      
      if (!deviceMap.containsKey(uid))
	deviceMap.put(uid, new CAMIDIDestination(ep, uid));
    }
  }

  private class NotificationReciever implements MIDINotifyProc {
    public void execute(MIDIClient c, MIDINotification messageID) {
      switch (messageID.getMessageID()) {
      case MIDIConstants.kMIDIMsgObjectAdded:
      case MIDIConstants.kMIDIMsgObjectRemoved:
	deviceMap.clear();
	try {
	  buildDeviceMap();
	} catch (CAException e) {
	  e.printStackTrace();
	}
      }
    }
  }
}