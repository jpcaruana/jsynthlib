// written by Kenneth L. Martinez

package synthdrivers.AccessVirus;

import core.*;
import javax.swing.*;
import synthdrivers.NordLead.NLGlobalDialog;

public class AccessVirusDevice extends Device {
  String channels[] = { "1", "2", "3", "4", "5", "6", "7", "8",
    "9", "10", "11", "12", "13", "14", "15", "16", "Omni"
  };

  /** Creates new AccessVirus */
  public AccessVirusDevice() {
    int deviceId;

    manufacturerName = "Access";
    modelName = "Virus";
    //patchType = "Bank";
    synthName = "Virus";

    JOptionPane.showMessageDialog(PatchEdit.instance,
      "Both the Virus desktop and Virus b can be used with this driver; note\n"
    + "that the Virus desktop doesn't have program banks E thru H.\n\n"
    + "The edit buffer for the global midi channel will be used to send and\n"
    + "play single programs and multis.  The midi device Id must match that\n"
    + "of the Virus to allow sysex send and receive to work.",
      "Access Virus Driver Release Notes",
      JOptionPane.WARNING_MESSAGE
    );
    NLGlobalDialog dlg = new NLGlobalDialog(null, channels,
        "Select Virus MIDI Device Id", 16);
    dlg.show();
    deviceId = dlg.getChannel() + 1;
    addDriver(new VirusProgBankDriver(deviceId));
    addDriver(new VirusProgSingleDriver(deviceId));
    addDriver(new VirusMultiBankDriver(deviceId));
    addDriver(new VirusMultiSingleDriver(deviceId));
  }
}
