// written by Kenneth L. Martinez

package synthdrivers.AccessVirus;

import java.io.Serializable;

public class AccessVirusConfig implements Serializable {
  private int deviceId;

  public AccessVirusConfig() {
    deviceId = 17;
  }

  public int getDeviceId() {
    return deviceId;
  }

  public void setDeviceId(int id) {
    deviceId = id;
  }
}
