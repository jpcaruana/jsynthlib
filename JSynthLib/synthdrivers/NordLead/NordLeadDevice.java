// written by Kenneth L. Martinez
// $Id$
package synthdrivers.NordLead;

import core.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Properties;
import java.util.Arrays;
import java.util.Set;


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

  JComboBox channelList;

  /** Holds value of property globalChannel. */
  private int globalChannel;
  
  /** Creates new NordLead */
  public NordLeadDevice() {
    manufacturerName = "Nord";
    modelName = "Lead";
    //patchType = "Bank";
    synthName = "NL1-2";
    infoText = DRIVER_INFO;
    Driver drv;
    
    JOptionPane.showMessageDialog(PatchEdit.getInstance(),
      DRIVER_INFO, "Nord Lead Driver Release Notes",
      JOptionPane.WARNING_MESSAGE
    );
    
    addDriver(new NLPatchBankDriver());
    addDriver(new NLPatchSingleDriver());
    addDriver(new NLDrumBankDriver());
    addDriver(new NLDrumSingleDriver());
    addDriver(new NLPerfBankDriver());
    addDriver(new NLPerfSingleDriver());
    addDriver(new NL2PerfBankDriver());
    addDriver(new NL2PerfSingleDriver());
    }

  public JPanel config() {
    JPanel panel= new JPanel();

    panel.add(new JLabel("Select Nord Lead Global Channel"));
    channelList = new JComboBox(channels);
    channelList.setMaximumSize(new Dimension(150, 25));
    channelList.setSelectedIndex(getGlobalChannel() - 1);
    channelList.addItemListener(this);
    panel.add(channelList);
    return panel;
  }

  public void itemStateChanged(ItemEvent e) {
    if (e.getStateChange() != ItemEvent.SELECTED) {
      return;
    }
    if (e.getItemSelectable() == channelList) {
      setGlobalChannel(channelList.getSelectedIndex() + 1);
    }
  }
  
  /** Getter for property globalChannel.
   * @return Value of property globalChannel.
   *
   */
  public int getGlobalChannel() {
      return this.globalChannel;
  }
  
  /** Setter for property globalChannel.
   * @param globalChannel New value of property globalChannel.
   *
   */
  public void setGlobalChannel(int globalChannel) {
      this.globalChannel = globalChannel;
  }
  
  /**
   * Get the names of properties that should be stored and loaded.
   * @return a Set of field names
   */
  public Set storedProperties()
  {
    final String[] storedPropertyNames = {"globalChannel"};
    Set set = super.storedProperties();
    set.addAll(Arrays.asList(storedPropertyNames));
    return set;
  }

  /**
   * Method that will be called after loading
   */
  public void afterRestore()
  {
  }
}
