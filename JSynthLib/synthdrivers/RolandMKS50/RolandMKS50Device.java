// written by Kenneth L. Martinez

package synthdrivers.RolandMKS50;

import core.*;

public class RolandMKS50Device extends Device
{
  /** Creates new RolandMKS50Device */
  public RolandMKS50Device()
  {
    manufacturerName = "Roland";
    modelName = "MKS-50";
    //patchType = "Bank";
    synthName = "MKS-50";

    addDriver(new MKS50ToneBankDriver());
    addDriver(new MKS50ToneSingleDriver());
    addDriver(new MKS50PatchBankDriver());
    addDriver(new MKS50PatchSingleDriver());
  }
}
