package CAProvider;

import com.apple.audio.midi.*;
import com.apple.audio.util.*;
import com.apple.audio.CAException;

import javax.sound.midi.*;

class CAMIDIReceiver implements Receiver {
  private MIDIEndpoint dest;
  private MIDIPacketList plist = null;

  CAMIDIReceiver (MIDIEndpoint ep) {
    dest = ep;
  }
  

  public void close() { }

  public void send(MidiMessage message, long timeStamp) {
    // I don't know the right way to deal with timeStamp, so
    timeStamp = 0;

    try {
      // I don't know why, but sending sometimes fails without this.
      String name = dest.getStringProperty(MIDIConstants.kMIDIPropertyName).asString();
      if (dest.getProperty(MIDIConstants.kMIDIPropertyOffline) == 1)
	return;
    } catch (CAException e) {}
    try {

      // Don't deal with message directly because of bugs
      if (message instanceof ShortMessage) {
	ShortMessage m = (ShortMessage) message;
	if (plist == null) 
	  plist = new MIDIPacketList();
	else
	  plist.init();
	
	MIDIData data = MIDIData.newMIDIChannelMessage((byte)m.getStatus(),
						       (byte)m.getData1(),
						       (byte)m.getData2());
	
	plist.add(timeStamp, data);
	CAMidiDeviceProvider.getOutputPort().send(dest, plist);
      } else if (message instanceof SysexMessage) {
	SysexMessage m = (SysexMessage) message;
	MIDIData data;
	if (m.getStatus() == SysexMessage.SPECIAL_SYSTEM_EXCLUSIVE) {
	  data = MIDIData.newMIDIRawData(m.getLength() - 1);
	  data.addRawData(m.getData());
	} else {
	  data = MIDIData.newMIDIRawData(m.getLength());
	  data.addRawData(m.getMessage());
	}
	MIDISysexSendRequest req = new MIDISysexSendRequest(dest, data);
	req.send(null);
      }
    } catch (CAException e) {
      e.printStackTrace();
    }
  }
}