// written by Kenneth L. Martinez

package synthdrivers.AccessVirus;

import core.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AccessVirusDevice extends Device implements ItemListener {
  static final String DRIVER_INFO =
      "Both the Virus desktop and Virus b can be used with this driver; note\n"
    + "that the Virus desktop doesn't have program banks E thru H.\n\n"
    + "The edit buffer for the global midi channel will be used to send and\n"
    + "play single programs and multis.  The midi device Id must match that\n"
    + "of the Virus to allow sysex send and receive to work.  Set the device\n"
    + "Id on the Configuration tab of Show Details.";
  String channels[] = { "1", "2", "3", "4", "5", "6", "7", "8",
    "9", "10", "11", "12", "13", "14", "15", "16", "Omni"
  };
  AccessVirusConfig avConfig;
  
  JComboBox channelList;

  /** Creates new AccessVirus */
  public AccessVirusDevice() {
    manufacturerName = "Access";
    modelName = "Virus";
    //patchType = "Bank";
    synthName = "Virus";
    infoText = DRIVER_INFO;
    avConfig = new AccessVirusConfig();
    Driver drv;
    JOptionPane.showMessageDialog(PatchEdit.instance,
      DRIVER_INFO, "Access Virus Driver Release Notes",
      JOptionPane.WARNING_MESSAGE
    );
    drv=new VirusProgBankDriver();
    ((VirusProgBankDriver)drv).setAvConfig(avConfig);
    addDriver(drv);
    
    drv=new VirusProgSingleDriver();
    ((VirusProgSingleDriver)drv).setAvConfig(avConfig);
    addDriver(drv);
    
    drv=new VirusMultiBankDriver();
    ((VirusMultiBankDriver)drv).setAvConfig(avConfig);
    addDriver(drv);
    
    drv=new VirusMultiSingleDriver();
    ((VirusMultiSingleDriver)drv).setAvConfig(avConfig);
    addDriver(drv);
    
  }

  public JPanel config() {
    JPanel panel= new JPanel();

    panel.add(new JLabel("Select Virus MIDI Device Id"));
    channelList = new JComboBox(channels);
    channelList.setMaximumSize(new Dimension(150, 25));
    channelList.setSelectedIndex(avConfig.getDeviceId() - 1);
    channelList.addItemListener(this);
    panel.add(channelList);
    return panel;
  }

  public void itemStateChanged(ItemEvent e) {
    if (e.getStateChange() != ItemEvent.SELECTED) {
      return;
    }
    if (e.getItemSelectable() == channelList) {
      avConfig.setDeviceId(channelList.getSelectedIndex() + 1);
    }
  }
}
