// written by Kenneth L. Martinez
// @version $Id$

package synthdrivers.RolandMKS50;

import core.*;

public class RolandMKS50Device extends Device
{
  /** Creates new RolandMKS50Device */
  public RolandMKS50Device()
  {
    super ("Roland","MKS-50",null,null,"Kenneth L. Martinez");

    addDriver(new MKS50ToneBankDriver());
    addDriver(new MKS50ToneSingleDriver());
    addDriver(new MKS50PatchBankDriver());
    addDriver(new MKS50PatchSingleDriver());
  }
}
