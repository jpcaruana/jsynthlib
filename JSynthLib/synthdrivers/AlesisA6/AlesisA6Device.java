// written by Kenneth L. Martinez

package synthdrivers.AlesisA6;

import core.*;
import javax.swing.*;

public class AlesisA6Device extends Device
{
  static final String DRIVER_INFO =
      "The A6 lacks a MIDI addressable patch buffer. Therefore, when you\n"
    + "send or play a patch from within JSynthLib, user program 127 will be\n"
    + "overwritten. JSynthLib treats this location as an edit buffer.";

  /** Creates new AlesisA6Device */
  public AlesisA6Device()
  {
    manufacturerName = "Alesis";
    modelName = "A6";
    //patchType = "Bank";
    synthName = "Andromeda";
    infoText = DRIVER_INFO;

    addDriver(new AlesisA6PgmBankDriver());
    addDriver(new AlesisA6PgmSingleDriver());
    addDriver(new AlesisA6MixBankDriver());
    addDriver(new AlesisA6MixSingleDriver());
    JOptionPane.showMessageDialog(PatchEdit.getInstance(),
      DRIVER_INFO, "A6 Driver Release Notes",
      JOptionPane.WARNING_MESSAGE
    );
  }
}
