// written by Kenneth L. Martinez

package synthdrivers.NordLead;

import core.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class NordLeadDevice extends Device implements ItemListener {
  static final String DRIVER_INFO =
      "Slot one's edit buffer will be used to send and play patches.\n"
    + "Please be sure to properly set the two required midi channels -\n"
    + "the global channel (default is 16), and the first slot's midi\n"
    + "channel (default is 1).  Set the global channel on the\n"
    + "Configuration tab of Show Details.\n\n"
    + "If the global channel is incorrect, patches can't be sent\n"
    + "or received via sysex.\n\n"
    + "The Nord Lead requires the correct bank to be selected before\n"
    + "storing a patch; if the slot one channel is incorrect,\n"
    + "JSynthLib's attempt to select the bank will fail and the patch\n"
    + "may be stored in the wrong bank!\n\n"
    + "When receiving performances from the Nord, you must manually\n"
    + "select the ROM or card performance bank.  Patch or drum banks\n"
    + "can be selected automatically by JSynthLib if the slot one midi\n"
    + "channel is correct.";
  String channels[] = { "1", "2", "3", "4", "5", "6", "7", "8",
    "9", "10", "11", "12", "13", "14", "15", "16"
  };
  NordLeadConfig nlConfig;
  JComboBox channelList;

  /** Creates new NordLead */
  public NordLeadDevice() {
    manufacturerName = "Nord";
    modelName = "Lead";
    //patchType = "Bank";
    synthName = "NL1-2";
    infoText = DRIVER_INFO;
    nlConfig = new NordLeadConfig();

    JOptionPane.showMessageDialog(PatchEdit.instance,
      DRIVER_INFO, "Nord Lead Driver Release Notes",
      JOptionPane.WARNING_MESSAGE
    );
    addDriver(new NLPatchBankDriver(nlConfig));
    addDriver(new NLPatchSingleDriver(nlConfig));
    addDriver(new NLDrumBankDriver(nlConfig));
    addDriver(new NLDrumSingleDriver(nlConfig));
    addDriver(new NLPerfBankDriver(nlConfig));
    addDriver(new NLPerfSingleDriver(nlConfig));
    addDriver(new NL2PerfBankDriver(nlConfig));
    addDriver(new NL2PerfSingleDriver(nlConfig));
  }

  public JPanel config() {
    JPanel panel= new JPanel();

    panel.add(new JLabel("Select Nord Lead Global Channel"));
    channelList = new JComboBox(channels);
    channelList.setMaximumSize(new Dimension(150, 25));
    channelList.setSelectedIndex(nlConfig.getGlobalChannel() - 1);
    channelList.addItemListener(this);
    panel.add(channelList);
    return panel;
  }

  public void itemStateChanged(ItemEvent e) {
    if (e.getStateChange() != ItemEvent.SELECTED) {
      return;
    }
    if (e.getItemSelectable() == channelList) {
      nlConfig.setGlobalChannel(channelList.getSelectedIndex() + 1);
    }
  }
}
