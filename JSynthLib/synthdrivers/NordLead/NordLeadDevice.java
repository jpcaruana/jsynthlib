// written by Kenneth L. Martinez

package synthdrivers.NordLead;

import core.*;
import javax.swing.*;

public class NordLeadDevice extends Device {
  String channels[] = { "1", "2", "3", "4", "5", "6", "7", "8",
    "9", "10", "11", "12", "13", "14", "15", "16"
  };

  /** Creates new NordLead */
  public NordLeadDevice() {
    int globalChannel;

    manufacturerName = "Nord";
    modelName = "Lead";
    //patchType = "Bank";
    synthName = "Lead";

    JOptionPane.showMessageDialog(PatchEdit.instance,
      "Slot one's edit buffer will be used to send and play patches.\n"
    + "Please be sure to properly set the two required midi channels -\n"
    + "the global channel (default is 16), and the first slot's midi\n"
    + "channel (default is 1).\n\n"
    + "If the global channel is incorrect, patches can't be sent\n"
    + "or received via sysex.\n\n"
    + "The Nord Lead requires the correct bank to be selected before\n"
    + "storing a patch; if the slot one channel is incorrect,\n"
    + "JSynthLib's attempt to select the bank will fail and the patch\n"
    + "may be stored in the wrong bank!\n\n"
    + "When receiving performances from the Nord, you must manually\n"
    + "select the ROM or card performance bank.  Patch or drum banks\n"
    + "can be selected automatically by JSynthLib if the slot one midi\n"
    + "channel is correct.",
      "Nord Lead Driver Release Notes",
      JOptionPane.WARNING_MESSAGE
    );
    NLGlobalDialog dlg = new NLGlobalDialog(null, channels,
        "Select Nord Lead Global Channel", 15);
    dlg.show();
    globalChannel = dlg.getChannel() + 1;
    addDriver(new NLPatchBankDriver(globalChannel));
    addDriver(new NLPatchSingleDriver(globalChannel));
    addDriver(new NLDrumBankDriver(globalChannel));
    addDriver(new NLDrumSingleDriver(globalChannel));
    addDriver(new NLPerfBankDriver(globalChannel));
    addDriver(new NLPerfSingleDriver(globalChannel));
  }
}
