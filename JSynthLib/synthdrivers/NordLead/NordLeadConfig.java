// written by Kenneth L. Martinez

package synthdrivers.NordLead;

import java.io.Serializable;

public class NordLeadConfig implements Serializable {
  private int globalChannel;

  public NordLeadConfig() {
    globalChannel = 16;
  }

  public int getGlobalChannel() {
    return globalChannel;
  }

  public void setGlobalChannel(int chan) {
    globalChannel = chan;
  }
}
