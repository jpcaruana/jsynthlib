/*
 * SysexStoreDialog.java  
 * $Id$
 */

package core;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

/**
 *
 * SysexStoreDialog.java - Dialog to choose the Device, Driver, BankNumber and PatchNumber of 
 * the location, where a Patch should be stored
 * (more than one of each device is supported, but only devices/drivers are selectable, which support the patch)
 * @author  Torsten Tittmann
 * @version v 1.0, 2003-02-03 
 */
public class SysexStoreDialog extends JDialog {

  //===== Instance variables
//   private Driver driver;
//   private Device device;
//   private int bankNum;
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
    super(PatchEdit.instance, "Store Sysex Data", true);

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
    for (int i=0, n=0;i<PatchEdit.appConfig.deviceCount();i++)
    {
      Device device=(Device)PatchEdit.appConfig.getDevice(i);

      for (int j=0, m=0;j<device.driverList.size();j++)
      {
	Driver driver = (Driver) device.driverList.get(j);
	if (driver.supportsPatch(patchString, p))
	{
          deviceComboBox.addItem(device);
	  driverMatched=true;

	  if (p.getDriver() == driver)
	  {
            deviceComboBox.setSelectedIndex(n);
            driverComboBox.setSelectedIndex(m);
          }
	  n++;
	  m++;
        }
      }
    }

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

//       Device device   = (Device)deviceComboBox.getSelectedItem();
      Driver driver   = (Driver)driverComboBox.getSelectedItem();
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
//       bankComboBox.removeAllItems();
//       patchNumComboBox.removeAllItems();

      Device device = (Device)deviceComboBox.getSelectedItem();
      if (device != null)
      {
        for (int j=0;j<device.driverList.size ();j++)
        {
	  Driver driver = (Driver) device.driverList.get(j);
          if (!(Converter.class.isInstance(driver))
	      && driver.supportsPatch(patchString, p))
              driverComboBox.addItem (device.getDriver(j));
        }
      }
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
          patchNumComboBox.setSelectedIndex(patchNum);
        }
      }

      bankComboBox.setEnabled(bankComboBox.getItemCount() > 1);
      // N.B. Do not enable patch selection for banks
      patchNumComboBox.setEnabled(!(driver instanceof BankDriver) && patchNumComboBox.getItemCount() > 1);
    }
  }
}
