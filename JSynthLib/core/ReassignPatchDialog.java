/*
 * ReassignPatchDialog.java  
 * $Id$
 */

package core;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;


/**
 *
 * ReassignPatchDialog.java - If more than two devices are loaded which supports the given patch,
 * show this Dialog to choose a new Device/Driver combination for the patch.
 * The internal patch assignment is used to send/play a patch.
 * @author  Torsten Tittmann
 * @version v 1.0, 2003-02-03 
 */
public class ReassignPatchDialog extends JDialog {

  //===== Instance variables
  private int driverNum;
  private Patch p;
  private StringBuffer patchString;

  private ArrayList deviceAssignmentList = new ArrayList();

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
    int deviceNum = 0;
    int nDriver = 0;		// number of matched driver
    //----- First Populate the Device/Driver List with Device/Driver. which supports the patch
    for (int i=0, n=0; i<PatchEdit.appConfig.deviceCount();i++)
    {
      Device device = PatchEdit.appConfig.getDevice(i);
      boolean newDevice = true;
      for (int j=0, m=0; j<device.driverList.size();j++)
      {
	Driver driver = (Driver) device.driverList.get(j);
	if (driver.supportsPatch(patchString, p))
	{
	  if (newDevice)	// only one entry for each supporting device
	  {
	    deviceAssignmentList.add(new deviceAssignment( i, device) );	// the original deviceNum/device
	    newDevice = false;
            n++;		// How many deviceAssignment?
	  }
	  ((deviceAssignment)deviceAssignmentList.get(n-1)).add(j, driver);	// the original driverNum/driver

	  if (p.getDriver() == driver) // default is patch internal deviceNum & driverNum
	  {
	    deviceNum = n-1;
            driverNum = m;
          }
	  nDriver++;
          m++;
        }
      }	// driver loop
    } // device loop

    //----- Create the combo boxes
    deviceComboBox = new JComboBox();
    deviceComboBox.addActionListener(new DeviceActionListener());
    driverComboBox = new JComboBox();

    //----- Populate the combo boxes with the entries of the deviceAssignmentList
    for (int i=0; i<deviceAssignmentList.size();i++)
    {
      deviceComboBox.addItem( deviceAssignmentList.get(i) );
    }
    deviceComboBox.setSelectedIndex(deviceNum);		// This was the original device
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

      p.setDriver(((driverAssignment) driverComboBox.getSelectedItem()).getDriver());
      driverNum = driverComboBox.getSelectedIndex();

      setVisible(false);
      dispose();
    }
  }


 /**
  * Repopulate the Driver ComboBox with valid drivers after a Device change 
  */
  class DeviceActionListener implements ActionListener {
    public void actionPerformed (ActionEvent evt) {

      deviceAssignment myDevAssign = (deviceAssignment)deviceComboBox.getSelectedItem();
//       driverAssignment myDrvAssign;

      driverComboBox.removeAllItems();

      if (myDevAssign != null)
      {
	ArrayList driverAssignmentList = myDevAssign.getDriverAssignmentList();
        for (int j=0;j<driverAssignmentList.size ();j++)
        {
	  driverAssignment myDrvAssign = (driverAssignment)driverAssignmentList.get(j);

          if ( !(Converter.class.isInstance (myDrvAssign.getDriver()) ) &&
	       ( myDrvAssign.getDriver().supportsPatch(patchString,p)) )  
              driverComboBox.addItem(myDrvAssign);
        }
      }
      driverComboBox.setSelectedIndex(driverNum);	// the original driver is the default
      driverComboBox.setEnabled(driverComboBox.getItemCount() > 1);
    }
  }
} 

