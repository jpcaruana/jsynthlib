package synthdrivers.KorgX3;

import core.*;

/**
 * This class is a device driver for Korg X3 -synthesizer to be used in
 * JSynthLib-program. Might work directly with Korg X2 as well.
 * Making drivers for N-series (N264, N364) should be an easy
 * task if one has the original reference guide.
 *
 * @author  Juha Tukkinen
 */
public class KorgX3Device extends Device
{
  
  /**
   * Creates a new KorgX3Device 
   */
  public KorgX3Device ()
  {
    inquiryID="F07E**0602423500**00**00**00F7";
    manufacturerName="Korg";
    modelName="X3";
    setSynthName("X3");
    infoText="This is the Korg X3 Driver. The Single Editor allows you to edit "+
      "most of the attributes in this synthesizer. A few are unimplemented as "+
      "the pan setting for example. Playing the patch in JSynthLib moves first "+
      "the patch to Korg's patch buffer.";
    addDriver(0, new KorgX3SingleConverter());
    addDriver(1, new KorgX3BankConverter());    
    addDriver(new KorgX3SingleDriver());
    addDriver(new KorgX3BankDriver());
  }
}
