// written by Kenneth L. Martinez

package synthdrivers.SCIProphet600;

import core.*;
import javax.swing.*;

public class SCIProphet600Device extends Device {
  /** Creates new SCIProphet600 */
  public SCIProphet600Device() {
    manufacturerName = "Sequential";
    modelName = "P600";
    //patchType = "Bank";
    synthName = "Prophet-600";

    addDriver(new P600ProgBankDriver());
    addDriver(new P600ProgSingleDriver());
    JOptionPane.showMessageDialog(PatchEdit.instance,
      "The Prophet-600 lacks a MIDI addressable patch buffer. Therefore, when\n"
    + "you send or play a patch from within JSynthLib, user program 99 will be\n"
    + "overwritten. JSynthLib treats this location as an edit buffer.",
      "Prophet-600 Driver Release Notes",
      JOptionPane.WARNING_MESSAGE
    );
  }
}
