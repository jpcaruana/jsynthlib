/*
 * ReassignPatchDialog.java
 */

package core;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * If more than two devices are loaded which supports the given patch,
 * show this Dialog to choose a new Device/Driver combination for the patch.
 * The internal patch assignment is used to send/play a patch.
 * @author  Torsten Tittmann
 * @version $Id$
 */
public class ReassignPatchDialog extends JDialog {

  //===== Instance variables
  /** The last index in driver Combo Box. */
  private int driverNum;
  private Patch p;
  private StringBuffer patchString;

  private JLabel myLabel;
  private JComboBox deviceComboBox;
  private JComboBox driverComboBox;

 /**
  * Constructor
  * @param patch The Patch to reassign
  */
  public ReassignPatchDialog (Patch patch) {
    super(PatchEdit.instance, "Reassign Patch to another Device/Driver", true);

    p           = patch;
    patchString = p.getPatchHeader();
    // now the panel
    JPanel dialogPanel = new JPanel(new BorderLayout(5, 5));

    myLabel=new JLabel("Please select a Device/Driver to Reassign.",JLabel.CENTER);
    dialogPanel.add(myLabel, BorderLayout.NORTH);

    //=================================== Combo Panel ==================================
    //----- Create the combo boxes
    deviceComboBox = new JComboBox();
    deviceComboBox.addActionListener(new DeviceActionListener());
    driverComboBox = new JComboBox();

    int nDriver = 0;		// number of matched driver
    // First Populate the Device/Driver List with Device/Driver. which
    // supports the patch
    for (int i=1; i < PatchEdit.appConfig.deviceCount(); i++)
    {
      Device device = PatchEdit.appConfig.getDevice(i);
      boolean newDevice = true;
      for (int j=0, m=0; j<device.driverList.size();j++)
      {
	Driver driver = (Driver) device.driverList.get(j);
	if (!(driver instanceof Converter)
	    && (driver.supportsPatch(patchString, p)))
	{
	  if (newDevice)	// only one entry for each supporting device
	  {
	    deviceComboBox.addItem(device);
	    newDevice = false;
	  }

	  if (p.getDriver() == driver) // default is the driver associated with patch
	  {
	    int n = deviceComboBox.getItemCount();
	    driverNum = m;
            deviceComboBox.setSelectedIndex(n - 1); // invoke DeviceActionListener
          }
	  nDriver++;
          m++;
        }
      }	// driver loop
    } // device loop
    deviceComboBox.setEnabled(deviceComboBox.getItemCount() > 1);

    //----- Layout the labels in a panel.
    JPanel labelPanel = new JPanel(new GridLayout(0, 1, 5, 5));
    labelPanel.add(new JLabel("Device:", JLabel.LEFT));
    labelPanel.add(new JLabel("Driver:", JLabel.LEFT));

    //----- Layout the fields in a panel
    JPanel fieldPanel = new JPanel(new GridLayout(0, 1));
    fieldPanel.add(deviceComboBox);
    fieldPanel.add(driverComboBox);

    //----- Create the comboPanel, labels on left, fields on right
    JPanel comboPanel = new JPanel(new BorderLayout());
    comboPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    comboPanel.add(labelPanel, BorderLayout.CENTER);
    comboPanel.add(fieldPanel, BorderLayout.EAST);
    dialogPanel.add(comboPanel, BorderLayout.CENTER);

    //=================================== Button Panel ==================================
    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout ( new FlowLayout (FlowLayout.CENTER) );

    JButton reassign = new JButton("Reassign");
    reassign.addActionListener(new ReassignActionListener());
    buttonPanel.add(reassign);

    JButton cancel = new JButton ("Cancel");
    cancel.addActionListener (
      new ActionListener () {
        public void actionPerformed (ActionEvent e) {
          setVisible(false);
	  dispose();
        }
      }
    );
    buttonPanel.add(cancel);
    getRootPane().setDefaultButton(reassign);
    dialogPanel.add(buttonPanel, BorderLayout.SOUTH);

    //===== Final initialisation of dialog box
    getContentPane().add(dialogPanel);
    pack();
    centerDialog();

    // show() or not to show(), that's the question!
    if (nDriver > 1)
    {
      this.show();
    }
    else if (nDriver == 1)
    {
      JOptionPane.showMessageDialog(null, "Only one driver was found, which support this patch! Nothing will happen", "Error while reassigning a patch", JOptionPane.INFORMATION_MESSAGE);
      dispose();
    }
    else
    {
      JOptionPane.showMessageDialog(null, "Oops, No driver was found, which support this patch! Nothing will happen", "Error while reassigning a patch", JOptionPane.WARNING_MESSAGE);
      dispose();
    }
  }

 /**
  *
  */
  private void centerDialog () {
    Dimension screenSize = this.getToolkit().getScreenSize();
    Dimension size = this.getSize ();
    this.setLocation((screenSize.width - size.width)/2, (screenSize.height - size.height)/2);
  }

 /**
  * Makes the actual work after pressing the 'Reassign' button
  */
  class ReassignActionListener implements ActionListener {
    public void actionPerformed (ActionEvent evt) {
      p.setDriver((Driver) driverComboBox.getSelectedItem());

      setVisible(false);
      dispose();
    }
  }

 /**
  * Repopulate the Driver ComboBox with valid drivers after a Device change
  */
  class DeviceActionListener implements ActionListener {
    public void actionPerformed (ActionEvent evt) {
      driverComboBox.removeAllItems();

      Device device = (Device) deviceComboBox.getSelectedItem();
      int nDriver = 0;
      for (int i = 0; i < device.driverList.size(); i++) {
        Driver driver = (Driver) device.driverList.get(i);
        if (!(driver instanceof Converter)
            && driver.supportsPatch(patchString, p)) {
          driverComboBox.addItem(driver);
          nDriver++;
        }
      }
      // the original driver is the default
      // When a different device is selected, driverNum can be out of range.
      driverComboBox.setSelectedIndex(Math.min(driverNum, nDriver - 1));
      driverComboBox.setEnabled(driverComboBox.getItemCount() > 1);
    }
  }
}
