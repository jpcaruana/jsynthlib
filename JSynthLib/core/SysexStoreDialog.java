/*
 * SysexStoreDialog.java
 */

package core;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Dialog to choose the Device, Driver, BankNumber and PatchNumber of
 * the location, where a Patch should be stored.  More than one of
 * each device is supported, but only devices/drivers are selectable,
 * which support the patch.
 * @author  Torsten Tittmann
 * @version $Id$
 */
public class SysexStoreDialog extends JDialog {

  //===== Instance variables
  /** The last index in driver Combo Box. */
  private int driverNum;
  private int patchNum;
  private Patch p;
  private StringBuffer patchString;

  private JLabel myLabel;
  private JComboBox deviceComboBox;
  private JComboBox driverComboBox;
  private JComboBox bankComboBox;
  private JComboBox patchNumComboBox;

 /**
  * Constructor with standard default patchNumber=0
  * @param patch The Patch to store
  */
  public SysexStoreDialog (Patch patch)
  {
    this(patch,0);
  }

 /**
  * Constructor with choosable default patchNumber
  * @param patch The Patch to store
  * @param patchnum The default patchNumber
  */
  public SysexStoreDialog (Patch patch, int patchnum) {
    super(PatchEdit.getInstance(), "Store Sysex Data", true);

    // initialising some variables
    p           = patch;
    patchNum    = patchnum;
    patchString = p.getPatchHeader();
    // now the panel
    JPanel dialogPanel = new JPanel(new BorderLayout(5, 5));

    myLabel=new JLabel("Please select a Location to Store.",JLabel.CENTER);
    dialogPanel.add(myLabel, BorderLayout.NORTH);

    //=================================== Combo Panel ==================================
    //----- Create the combo boxes
    driverComboBox = new JComboBox();
    driverComboBox.addActionListener(new DriverActionListener());
    deviceComboBox = new JComboBox();
    deviceComboBox.addActionListener(new DeviceActionListener());
    bankComboBox = new JComboBox();
    patchNumComboBox = new JComboBox();

    //----- Populate the combo boxes only with devices, which supports the patch
    boolean driverMatched = false;
    for (int i=0; i < PatchEdit.appConfig.deviceCount(); i++)
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
	  driverMatched = true;
          m++;
        }
      }	// driver loop
    } // device loop
    deviceComboBox.setEnabled(deviceComboBox.getItemCount() > 1);

    //----- Layout the labels in a panel.
    JPanel labelPanel = new JPanel(new GridLayout(0, 1, 5, 5));
    labelPanel.add(new JLabel("Device:", JLabel.LEFT));
    labelPanel.add(new JLabel("Driver:", JLabel.LEFT));
    labelPanel.add(new JLabel("Bank:", JLabel.LEFT));
    labelPanel.add(new JLabel("Patch:", JLabel.LEFT));

    //----- Layout the fields in a panel
    JPanel fieldPanel = new JPanel(new GridLayout(0, 1));
    fieldPanel.add(deviceComboBox);
    fieldPanel.add(driverComboBox);
    fieldPanel.add(bankComboBox);
    fieldPanel.add(patchNumComboBox);

   //----- Create the comboPanel, labels on left, fields on right
    JPanel comboPanel = new JPanel(new BorderLayout());
    comboPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    comboPanel.add(labelPanel, BorderLayout.CENTER);
    comboPanel.add(fieldPanel, BorderLayout.EAST);
    dialogPanel.add(comboPanel, BorderLayout.CENTER);

    //=================================== Button Panel ==================================
    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout ( new FlowLayout (FlowLayout.CENTER) );

    JButton store = new JButton("Store");
    store.addActionListener(new StoreActionListener());
    buttonPanel.add(store);

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
    getRootPane().setDefaultButton(store);
    dialogPanel.add(buttonPanel, BorderLayout.SOUTH);

    //===== Final initialisation of dialog box
    getContentPane().add(dialogPanel);
    pack();
    centerDialog();

    if (driverMatched)
      this.show();
    else
    {
      JOptionPane.showMessageDialog(null, "Oops, No driver was found, which support this patch! Nothing will happen",
				    "Error while storing a patch", JOptionPane.WARNING_MESSAGE);
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
  * Makes the actual work after pressing the 'Store' button
  */
  class StoreActionListener implements ActionListener {
    public void actionPerformed (ActionEvent evt) {

      Driver driver = (Driver) driverComboBox.getSelectedItem();
      int bankNum  = bankComboBox.getSelectedIndex();
      int patchNum = patchNumComboBox.getSelectedIndex();
      driver.storePatch(p, bankNum, patchNum);

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
          driverComboBox.addItem (driver);
          nDriver++;
	}
      }
      // the original driver is the default
      // When a different device is selected, driverNum can be out of range.
      driverComboBox.setSelectedIndex(Math.min(driverNum, nDriver - 1));
      driverComboBox.setEnabled(driverComboBox.getItemCount() > 1);
    }
  }

 /**
  * Repopulate the Bank/Patch ComboBox with valid entries after a Device/Driver change
  */
  class DriverActionListener implements ActionListener {
    public void actionPerformed (ActionEvent evt) {

      Driver driver = (Driver)driverComboBox.getSelectedItem();
      bankComboBox.removeAllItems();
      patchNumComboBox.removeAllItems();

      if (driver != null) {
        if (driver.bankNumbers.length > 1)
	{
          for (int i = 0 ; i < driver.bankNumbers.length ; i++)
	  {
            bankComboBox.addItem(driver.bankNumbers[i]);
          }
        }

        if (driver.patchNumbers.length > 1)
	{
          for (int i = 0 ; i < driver.patchNumbers.length ; i++)
	  {
            patchNumComboBox.addItem(driver.patchNumbers[i]);
          }
          patchNumComboBox.setSelectedIndex(Math.min(patchNum, patchNumComboBox.getItemCount() - 1));
        }
      }

      bankComboBox.setEnabled(bankComboBox.getItemCount() > 1);
      // N.B. Do not enable patch selection for banks
      patchNumComboBox.setEnabled(!(driver instanceof BankDriver) && patchNumComboBox.getItemCount() > 1);
    }
  }
}
