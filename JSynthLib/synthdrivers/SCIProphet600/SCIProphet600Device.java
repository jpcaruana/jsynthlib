// written by Kenneth L. Martinez
// @version $Id$

package synthdrivers.SCIProphet600;

import core.*;
import javax.swing.*;

public class SCIProphet600Device extends Device {
  static final String DRIVER_INFO =
  "The Prophet-600 lacks a MIDI addressable patch buffer. Therefore, when\n"
    + "you send or play a patch from within JSynthLib, user program 99 will be\n"
    + "overwritten. JSynthLib treats this location as an edit buffer.";

  /** Creates new SCIProphet600 */
  public SCIProphet600Device() {
    super ("Sequential","P600",null,DRIVER_INFO,"Kenneth L. Martinez");
    setSynthName("Prophet-600");

    addDriver(new P600ProgBankDriver());
    addDriver(new P600ProgSingleDriver());
    JOptionPane.showMessageDialog(PatchEdit.getInstance(),
      DRIVER_INFO, "Prophet-600 Driver Release Notes",
      JOptionPane.WARNING_MESSAGE
    );
  }
}
