/*
 * SysexSendToDialog.java  
 * $Id$
 */

package core;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

/**
 *
 * SysexSendToDialog.java - Dialog to choose a Device and Driver 
 * to send the patch into an Edit buffer
 * (more than one of each device is supported, but only devices/drivers are selectable, which support the patch)
 * @author  Torsten Tittmann
 * @version v 1.0, 2003-02-03 
 */
public class SysexSendToDialog extends JDialog {

  //===== Instance variables
//   private boolean driverMatched=false;
//   private Driver driver;
//   private Device device;
  private Patch p;
  private StringBuffer patchString;

  private JLabel myLabel;
  private JComboBox deviceComboBox;
  private JComboBox driverComboBox;

 /**
  * Constructor
  * @param patch The Patch to 'send to...'
  */
  public SysexSendToDialog (Patch patch) {
    super(PatchEdit.instance, "Send Sysex Data into Edit Buffer of a specified device", true);

    p           = patch;
    patchString = p.getPatchHeader();
    // now the panel
    JPanel dialogPanel = new JPanel(new BorderLayout(5, 5));

    myLabel=new JLabel("Please select a Device to 'Send to...'.",JLabel.CENTER);
    dialogPanel.add(myLabel, BorderLayout.NORTH);

    //=================================== Combo Panel ==================================
    //----- Create the combo boxes
    deviceComboBox = new JComboBox();
    deviceComboBox.addActionListener(new DeviceActionListener());

    driverComboBox = new JComboBox();

    //----- Populate the combo boxes only with devices, which supports the patch
    Driver driver = null;
    for (int i=0, n=0;i<PatchEdit.appConfig.deviceCount();i++)
    {
      Device device = (Device) PatchEdit.appConfig.getDevice(i);
      for (int j=0, m=0; j < device.driverList.size(); j++)
      {
	Driver drv = (Driver) device.driverList.get(j);
	if (drv.supportsPatch(patchString, p))
	{
	  driver = drv;
          deviceComboBox.addItem(device);
	  driverComboBox.addItem(drv);

	  if (p.getDriver() == drv) // default is patch internal deviceNum & driverNum
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

    JButton send = new JButton("Send to...");
    send.addActionListener(new SendToActionListener());
    buttonPanel.add(send);

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
    getRootPane().setDefaultButton(send);
    dialogPanel.add(buttonPanel, BorderLayout.SOUTH);

    //===== Final initialisation of dialog box
    getContentPane().add(dialogPanel);
    pack();
    centerDialog();

    // show() or not to show(), that's the question!
    if (driver != null)
    {
      if (deviceComboBox.getItemCount()>1 ||
          driverComboBox.getItemCount()>1 )
	this.show();
      else
      {
        driver.sendPatch(p);
	dispose();
      }
    }
    else
    {
      JOptionPane.showMessageDialog(null, "Oops, No driver was found, which support this patch! Nothing will happen", "Error while 'send to...' a patch", JOptionPane.WARNING_MESSAGE);
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
  * Makes the actual work after pressing the 'Send to...' button
  */
  class SendToActionListener implements ActionListener {
    public void actionPerformed (ActionEvent evt) {

//    Device device = (Device)deviceComboBox.getSelectedItem();
      Driver driver = (Driver)driverComboBox.getSelectedItem();

      driver.sendPatch(p);

      setVisible(false);
      dispose();
    }
  }


 /**
  * Repopulate the Driver ComboBox with valid drivers after a Device change 
  */
  class DeviceActionListener implements ActionListener {
    public void actionPerformed (ActionEvent evt) {

      Device device = (Device)deviceComboBox.getSelectedItem();
      driverComboBox.removeAllItems();

      if (device != null)
      {
        for (int j=0;j<device.driverList.size ();j++)
        {
          if ( !(Converter.class.isInstance (device.driverList.get (j))) &&
		( ((Driver)device.driverList.get(j)).supportsPatch(patchString,p)) )
              driverComboBox.addItem (device.getDriver(j));
        }
      }

      driverComboBox.setEnabled(driverComboBox.getItemCount() > 1);
    }
  }
}
