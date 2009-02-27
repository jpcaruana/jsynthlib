package CAProvider;

import javax.sound.midi.MidiDevice;

public class CAMidiDeviceInfo extends MidiDevice.Info {
  private Integer uid;
  public CAMidiDeviceInfo (String name, String vendor, String description,
			   String version, Integer _uid) {
    super(name, vendor, description, version);
    uid = _uid;
  }
  public Integer getUniqueID() {
    return uid;
  }
}