// written by Federico Ferri
// @version $Id$

package synthdrivers.RolandMKS7;

import core.*;
import java.util.prefs.Preferences;

public class RolandMKS7Device extends Device
{
  /** Creates new RolandMKS7Device */
  public RolandMKS7Device()
  {
    super ("Roland","MKS-7",null,null,"Federico Ferri");
  }

  /** Constructor for for actual work. */
  public RolandMKS7Device(Preferences prefs) {
    this();
    this.prefs = prefs;

    //addDriver(new MKS7ToneBankDriver());
    addDriver(new MKS7ToneSingleDriver());
    //addDriver(new MKS7PatchBankDriver());
    //addDriver(new MKS7PatchSingleDriver());
  }
}
