// written by Kenneth L. Martinez

package synthdrivers.AlesisA6;

import core.*;

public class AlesisA6Device extends Device
{
  /** Creates new AlesisA6Device */
  public AlesisA6Device()
  {
    manufacturerName = "Alesis";
    modelName = "A6";
    //patchType = "Bank";
    synthName = "Andromeda";

    addDriver(new AlesisA6PgmBankDriver());
    addDriver(new AlesisA6PgmSingleDriver());
    addDriver(new AlesisA6MixBankDriver());
    addDriver(new AlesisA6MixSingleDriver());
  }
}
