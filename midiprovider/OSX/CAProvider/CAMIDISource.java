package CAProvider;

import javax.sound.midi.*;

import java.util.*;

import com.apple.audio.midi.*;
import com.apple.audio.util.*;
import com.apple.audio.CAException;

public class CAMIDISource implements MidiDevice, MIDIReadProc {
  private ArrayList transmitters = new ArrayList();
  private boolean open = false;

  private CAMidiDeviceInfo info;
  private MIDIEndpoint source;
  private MIDIInputPort input = null;

  public CAMIDISource (MIDIEndpoint ep, Integer uid, String name_prefix)
          throws CAException {
    source = ep;
    String name="", vendor="", description="", version="";
    try {
      name = name_prefix + " " + 
	source.getStringProperty(MIDIConstants.kMIDIPropertyName).asString();
    } catch (CAException e) {}
    try {
      version = 
	source.getStringProperty(MIDIConstants.kMIDIPropertyDriverVersion).asString();
    } catch (CAException e) {}
    try {
      vendor =
	source.getStringProperty(MIDIConstants.kMIDIPropertyManufacturer).asString();
    } catch (CAException e) {}
    try {
      // Should I use something else for the description?
      description =
	source.getStringProperty(MIDIConstants.kMIDIPropertyModel).asString();
    } catch (CAException e) {}
    info = new CAMidiDeviceInfo(name, vendor, description, version, uid);
  }
  public CAMIDISource(MIDIEndpoint ep, Integer uid) throws CAException {
    this(ep, uid, "");
  }

  public void close() {
    open = false;
    if (input != null) {
      try {
	input.disconnectSource(source);
      } catch (CAException e) {
	e.printStackTrace();
      }
    }
  }
  public MidiDevice.Info getDeviceInfo() {
    return info;
  }

  public int getMaxReceivers() {
    return 0;
  }

  public int getMaxTransmitters() {
    return -1;
  }

  // Is this right?
  public long getMicrosecondPosition() {
    return -1;
  }

  public boolean isOffline() {
    try {
      return source.getProperty(MIDIConstants.kMIDIPropertyOffline) == 1;
    } catch (CAException e) {
      return false;
    }
  }

  public Receiver getReceiver() throws MidiUnavailableException {
    throw 
      new MidiUnavailableException("CAMIDISource currently has no Receivers");
  }

  public Transmitter getTransmitter() {
    Transmitter t = new Transmitter() {
	private Receiver r = null;
	public void close() {}
	public Receiver getReceiver() { return r; }
	public void setReceiver(Receiver _r) { r = _r; }
      };
    transmitters.add(t);
    return t;
  }
  public boolean isOpen() {
    return open;
  }
  public void open() {
    try {
      if (!isOffline()) {
	if (input == null) {
	  input = 
	    CAMidiDeviceProvider.getMIDIClient()
	    .inputPortCreate( new CAFString(info.getName()), this );
	}
	input.connectSource(source);
      }
    open = true;
    } catch (CAException e) {
      e.printStackTrace();
    }
  }

  public void execute (MIDIInputPort p, MIDIEndpoint e, MIDIPacketList list) {
    int count = list.numPackets();
    try {
      for (int i = 0; i < count; i++) {
	findMessages(list.getPacket(i).getData());
      }
    } catch (CAException ex) {
      ex.printStackTrace();
    } catch (InvalidMidiDataException ex) {
      ex.printStackTrace();
    }
  }

  // From MacOSXMidiWrapper
  private void findMessages(MIDIData data)
          throws CAException, InvalidMidiDataException {
    int status = (int)(data.getByteAt(0) & 0xFF);
    int len = data.getMIDIDataLength();
    if (status == SysexMessage.SYSTEM_EXCLUSIVE || (status & 0x80) == 0) {
      byte[] d = new byte[len];
      data.copyToArray(0, d, 0, len);

      SysexMessage msg = new SysexMessage();
      if (status == SysexMessage.SYSTEM_EXCLUSIVE)
	msg.setMessage(d, len);
      else
	msg.setMessage(SysexMessage.SPECIAL_SYSTEM_EXCLUSIVE, d, len);

      transmitMessage(msg);

      return;
    } else {
      int d1, d2;
      for (int i = 0; i < len; i++) {
	ShortMessage msg = new ShortMessage();
	status = (int)(data.getByteAt(i) & 0xFF);
	if ( (i + 1 < len) && (data.getByteAt(i + 1) & 0x80) == 0) {
	  d1 = (int)(data.getByteAt(++i) & 0xFF);
	  if ( (i + 1 < len) && (data.getByteAt(i + 1) & 0x80) == 0) {
	    d2 = (int)(data.getByteAt(++i) & 0xFF);
	    msg.setMessage(status, d1, d2);
	  } else {
	    msg.setMessage(status, d1, 0);
	  }
	} else {
	  msg.setMessage(status);
	}
	transmitMessage(msg);
      }
    }
  }

  private void transmitMessage(MidiMessage msg) {
    Iterator it = transmitters.iterator();
    Transmitter t;
    Receiver r;
    while (it.hasNext()) {
      t = (Transmitter)it.next();;
      if (t != null) {
	r = t.getReceiver();
	if (r != null)
	  r.send(msg, -1);
      }
    }
  }

}