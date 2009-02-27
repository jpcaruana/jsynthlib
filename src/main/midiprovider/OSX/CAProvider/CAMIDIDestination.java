package CAProvider;

import javax.sound.midi.*;

import java.util.*;

import com.apple.audio.midi.*;
import com.apple.audio.util.*;
import com.apple.audio.CAException;

public class CAMIDIDestination implements MidiDevice {
  private boolean open = false;

  private CAMidiDeviceInfo info;
  private MIDIEndpoint dest;

  public CAMIDIDestination (MIDIEndpoint ep, Integer uid, String name_prefix)
           throws CAException {
    dest = ep;
    String name = "", vendor = "", description = "", version = "";
    try {
      name = name_prefix + " " + 
	dest.getStringProperty(MIDIConstants.kMIDIPropertyName).asString();
    } catch (CAException e) {}
    try {
      version = 
	dest.getStringProperty(MIDIConstants.kMIDIPropertyDriverVersion).asString();
    } catch (CAException e) {}
    try {
      vendor =
	dest.getStringProperty(MIDIConstants.kMIDIPropertyManufacturer).asString();
    } catch (CAException e) {}
    try {
      // Should I use something else for the description?
      description =
	dest.getStringProperty(MIDIConstants.kMIDIPropertyModel).asString();
    } catch (CAException e) {}
    info = new CAMidiDeviceInfo(name, vendor, description, version, uid);
  }
  public CAMIDIDestination(MIDIEndpoint ep, Integer uid) throws CAException {
    this(ep, uid, "");
  }

  public void close() {
    open = false;
  }
  public MidiDevice.Info getDeviceInfo() {
    return info;
  }

  public int getMaxTransmitters() {
    return 0;
  }

  public int getMaxReceivers() {
    return -1;
  }

  // Is this right?
  public long getMicrosecondPosition() {
    //Maybe 1000*HostTime.convertHostTimeToNanos(HostTime.getCurrentHostTime())
    return -1;
  }

  public boolean isOffline() {
    try {
      return dest.getProperty(MIDIConstants.kMIDIPropertyOffline) == 1;
    } catch (CAException e) {
      return false;
    }
  }

  public Transmitter getTransmitter() throws MidiUnavailableException {
    throw 
      new MidiUnavailableException("CAMIDIDestination currently has no Transmitters");
  }

  public Receiver getReceiver() {
    return new CAMIDIReceiver(dest);
  }
  public boolean isOpen() {
    return open;
  }
  public void open() {
    open = true;
  }
}