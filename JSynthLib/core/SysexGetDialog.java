//======================================================================================================================
// $Id$
// Summary: SysexGetDialog.java
// Author: phil@muqus.com - 07/2001
//======================================================================================================================

package core;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import javax.swing.JScrollPane;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;
import javax.swing.JDialog;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.metal.*;

import java.beans.*;
import java.util.*;
import java.awt.event.*;
import java.awt.*;
import java.io.*;


//======================================================================================================================
// Class: SysexGetDialog
//======================================================================================================================

public class SysexGetDialog extends JDialog {

  //===== Instance variables
  int sysexSize = 0;
  protected Driver driver;
  protected Device device;
  protected int deviceNum;
  protected int driverNum;
  protected int bankNum;
  protected int patchNum;

  static byte[] buffer = new byte[256*1024];                     // N.B. WireMidiWrapper ignores MaxSize parameter of readMessage
  static byte [] sysex = new byte [256*1024];

  javax.swing.Timer timer;

  JLabel myLabel;

  JComboBox deviceComboBox;
  JComboBox driverComboBox;
  JComboBox bankComboBox;
  JComboBox patchNumComboBox;
  private ArrayList deviceAssignmentList = new ArrayList();

//----------------------------------------------------------------------------------------------------------------------
// Constructor: SysexGetDialog()
//----------------------------------------------------------------------------------------------------------------------

  public SysexGetDialog (JFrame Parent) { //, Driver driver, int bankNum, int patchNum) {
    super(Parent, "Get Sysex Data", true);

    JPanel dialogPanel = new JPanel(new BorderLayout(5, 5));
//    dialogPanel.setLayout(new GridLayout(0, 1));

    //myLabel = new JLabel(" ", JLabel.CENTER);
    myLabel=new JLabel("Please select a Patch Type to Get.",JLabel.CENTER);
    dialogPanel.add(myLabel, BorderLayout.NORTH);

    //=================================== Combo Panel ==================================
    //----- Create the combo boxes
    driverComboBox = new JComboBox();
    driverComboBox.addActionListener(new DriverActionListener());
    driverComboBox.setRenderer(new DriverCellRenderer());

    deviceComboBox = new JComboBox();
    deviceComboBox.addActionListener(new DeviceActionListener());
    deviceComboBox.setRenderer(new DeviceCellRenderer());

    bankComboBox = new JComboBox();
    bankComboBox.setRenderer(new ComboCellRenderer());

    patchNumComboBox = new JComboBox();
    patchNumComboBox.setRenderer(new ComboCellRenderer());

    //----- First Populate the Device/Driver List with all Device/Driver combinations except converters
       for (int i=0;i<PatchEdit.appConfig.deviceCount();i++)
            {
      device=(Device)PatchEdit.appConfig.getDevice(i);
      deviceAssignmentList.add(new deviceAssignment( i, device) );        // the original deviceNum/device
    
      for (int j=0;j<device.driverList.size();j++)
      {
        // Skipping a converter
        if (!(Converter.class.isInstance(driver=(Driver)device.driverList.get(j))))
        {
          ((deviceAssignment)deviceAssignmentList.get(i)).add(j, driver);     // the original driverNum/driver
        }
      }
    }
    
    //----- Populate the combo boxes with the entries of the deviceAssignmentList
    for (int i=0; i<deviceAssignmentList.size();i++)
    {
        deviceComboBox.addItem( deviceAssignmentList.get(i) );
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

    JButton get = new JButton("Get");
    get.addActionListener(new GetActionListener());
    buttonPanel.add(get);

    JButton paste = new JButton("Paste");
    paste.addActionListener(new PasteActionListener());
    buttonPanel.add(paste);

    JButton done = new JButton("Done");
    done.addActionListener(new DoneActionListener());
    buttonPanel.add(done);

    JButton cancel = new JButton ("Cancel");
    cancel.addActionListener (
      new ActionListener () {
        public void actionPerformed (ActionEvent e) {
          setVisible(false);
          timer.stop();
        }
      }
    );
    buttonPanel.add(cancel);
    getRootPane().setDefaultButton(done);
    dialogPanel.add(buttonPanel, BorderLayout.SOUTH);

    //===================================== Timer ========================================
    PatchEdit.echoTimer.stop();
    timer = new javax.swing.Timer(0, new TimerActionListener());

    //===== Listener

    //===== Final initialisation of dialog box
    getContentPane().add(dialogPanel);
    pack();
//    setSize(600, 200);
    centerDialog();
    sysexSize = 0;
  }

//----------------------------------------------------------------------------------------------------------------------
// SysexGetDialog->centerDialog
//----------------------------------------------------------------------------------------------------------------------

  protected void centerDialog () {
    Dimension screenSize = this.getToolkit().getScreenSize();
    Dimension size = this.getSize ();
    this.setLocation((screenSize.width - size.width)/2, (screenSize.height - size.height)/2);
  }

//----------------------------------------------------------------------------------------------------------------------
// SysexGetDialog->pasteIntoSelectedFrame
//----------------------------------------------------------------------------------------------------------------------

  protected void pasteIntoSelectedFrame() {
// ErrorMsg.reportStatus("SysexGetDialog->pasteIntoSelectedFrame: " + sysexSize);

    if (sysexSize < 20)
      return;

    byte[] patchSysex;
       if (sysex[0]==-16) //F0 
    {  patchSysex= new byte[sysexSize];
       System.arraycopy(sysex, 0, patchSysex, 0, sysexSize);
    } else
     { 
     int i=0;
      while ((sysex[i]!=-16) && (i<sysexSize)) i++;
      if (i==sysexSize) return;
      patchSysex= new byte[sysexSize-i];
      System.arraycopy(sysex, i, patchSysex, 0, sysexSize-i);
     }
	    
    Patch p = new Patch(patchSysex, deviceNum, driverNum );

    Patch[] patarray = p.dissect();
    for (int k = 0; k < patarray.length; k++) {
      PatchEdit.Clipboard = patarray[k];
      StringBuffer patchString = PatchEdit.Clipboard.getPatchHeader();

      // Maybe you don't get the expected patch!
      // Check all devices/drivers again!
      if ( !(((Driver)PatchEdit.getDriver(PatchEdit.Clipboard.deviceNum,PatchEdit.Clipboard.driverNum)).supportsPatch(patchString,PatchEdit.Clipboard)) )
      {
	boolean firstRun=true;
	boolean foundDriver=false;

	// first check the requested device
	testforDriver:
	for ( int i =PatchEdit.Clipboard.deviceNum;i<PatchEdit.appConfig.deviceCount (); i++)
        {
          device=(Device)PatchEdit.appConfig.getDevice(i);
 
          for (int j=0;j<device.driverList.size();j++)
          {
            if ( ((Driver)device.driverList.get(j)).supportsPatch(patchString,PatchEdit.Clipboard) )
            {
	      PatchEdit.Clipboard.deviceNum=i;
	      PatchEdit.Clipboard.driverNum=j;
	      PatchEdit.getDriver(i,j).trimSysex(PatchEdit.Clipboard);
	      foundDriver=true;
              break testforDriver;
            }
          }
	  if (firstRun)
	  {
	    i=0;
	    firstRun=false;
	  }
	}

        if (foundDriver)
	{
	  JOptionPane.showMessageDialog (null,
		"You requested a "+((Driver)PatchEdit.getDriver(deviceNum,driverNum)).getDriverName()+"patch!"+
		"\nBut you got a "+((Driver)PatchEdit.getDriver(PatchEdit.Clipboard.deviceNum,PatchEdit.Clipboard.driverNum)).getDriverName()+"patch.",
		"Warning", JOptionPane.WARNING_MESSAGE);
	}
	else
	{
	  PatchEdit.Clipboard.deviceNum=0;	//reset
	  PatchEdit.Clipboard.driverNum=0;	//reset
	  PatchEdit.Clipboard.comment=new StringBuffer("Probably a "+LookupManufacturer.get(PatchEdit.Clipboard.sysex[1],PatchEdit.Clipboard.sysex[2],PatchEdit.Clipboard.sysex[3])+" Patch, Size: "+PatchEdit.Clipboard.sysex.length);

	  JOptionPane.showMessageDialog (null,
		"You requested a "+((Driver)PatchEdit.getDriver(deviceNum,driverNum)).getDriverName()+"patch!"+
		"\nBut you got a not supported patch!\n"+PatchEdit.Clipboard.comment,
		"Warning", JOptionPane.WARNING_MESSAGE);
	}
      }

      try {
        ((PatchBasket)PatchEdit.desktop.getSelectedFrame()).PastePatch();
      } catch (Exception ex) {
        JOptionPane.showMessageDialog (null, "Library to Receive into must be the focused Window.","Error", JOptionPane.ERROR_MESSAGE);
      }
    }
  }

//----------------------------------------------------------------------------------------------------------------------
// InnerClass: DoneActionListener
//----------------------------------------------------------------------------------------------------------------------

  public class DoneActionListener implements ActionListener {
    public void actionPerformed (ActionEvent evt) {
      setVisible(false);
      timer.stop();
      PatchEdit.echoTimer.start();
      pasteIntoSelectedFrame();
    }
  } // End InnerClass: DoneActionListener

//----------------------------------------------------------------------------------------------------------------------
// InnerClass: DeviceActionListener
//----------------------------------------------------------------------------------------------------------------------
 
   public class DeviceActionListener implements ActionListener {
     public void actionPerformed (ActionEvent evt) {
 //      ErrorMsg.reportStatus("DeviceActionListener->actionPerformed");
 
      deviceAssignment myDevAssign = (deviceAssignment)deviceComboBox.getSelectedItem();

       driverComboBox.removeAllItems();
       bankComboBox.removeAllItems();
       patchNumComboBox.removeAllItems();
 
      if (myDevAssign != null)
       {
        for (int j=0;j<myDevAssign.driverAssignmentList.size ();j++)
         {
          driverComboBox.addItem( (driverAssignment)myDevAssign.driverAssignmentList.get(j) );
         }
       }
 
       driverComboBox.setEnabled(driverComboBox.getItemCount() > 1);
     }
   } // End InnerClass: DeviceActionListener
 
 

//----------------------------------------------------------------------------------------------------------------------
// InnerClass: DriverActionListener
//----------------------------------------------------------------------------------------------------------------------

  public class DriverActionListener implements ActionListener {
    public void actionPerformed (ActionEvent evt) {
//      ErrorMsg.reportStatus("DriverActionListener->actionPerformed");

      driverAssignment myDrvAssign = (driverAssignment)driverComboBox.getSelectedItem();

      bankComboBox.removeAllItems();
      patchNumComboBox.removeAllItems();

      if (myDrvAssign != null) {
        if (myDrvAssign.driver.bankNumbers.length > 1) {
          for (int i = 0 ; i < myDrvAssign.driver.bankNumbers.length ; i++) {
            bankComboBox.addItem(myDrvAssign.driver.bankNumbers[i]);
        }



        if (myDrvAssign.driver.patchNumbers.length > 1) {
          for (int i = 0 ; i < myDrvAssign.driver.patchNumbers.length ; i++) {
            patchNumComboBox.addItem(myDrvAssign.driver.patchNumbers[i]);
          }
        }
      }

      bankComboBox.setEnabled(bankComboBox.getItemCount() > 1);
      // N.B. Do not enable patch selection for banks
      patchNumComboBox.setEnabled(!(myDrvAssign.driver instanceof BankDriver) && patchNumComboBox.getItemCount() > 1);
    }
      }
  } // End InnerClass: DriverActionListener

//----------------------------------------------------------------------------------------------------------------------
// InnerClass: PasteActionListener
//----------------------------------------------------------------------------------------------------------------------

  public class PasteActionListener implements ActionListener {
    public void actionPerformed (ActionEvent evt) {
      myLabel.setText(" ");
      timer.stop();
      pasteIntoSelectedFrame();
      sysexSize = 0;
    }
  } // End InnerClass: PasteActionListener

//----------------------------------------------------------------------------------------------------------------------
// InnerClass: GetActionListener
//----------------------------------------------------------------------------------------------------------------------

  public class GetActionListener implements ActionListener {
    public void actionPerformed (ActionEvent evt) {
      device    = (Device) ((deviceAssignment)deviceComboBox.getSelectedItem()).device;
      driver    = (Driver) ((driverAssignment)driverComboBox.getSelectedItem()).driver;
      deviceNum = (int) ((deviceAssignment)deviceComboBox.getSelectedItem()).deviceNum;
      driverNum = (int) ((driverAssignment)driverComboBox.getSelectedItem()).driverNum;
      bankNum = bankComboBox.getSelectedIndex();
      patchNum = patchNumComboBox.getSelectedIndex();

      ErrorMsg.reportStatus("");
      ErrorMsg.reportStatus("SysexGetDialog | port: " + driver.inPort + " | bankNum: " + bankNum + " | patchNum: " + patchNum);

      //----- Clear MidiIn buffer
      try {
        while (PatchEdit.MidiIn.messagesWaiting(driver.inPort) > 0)
          PatchEdit.MidiIn.readMessage(driver.inPort, buffer, 1024);

      } catch (Exception ex) {
        ErrorMsg.reportError("Error", "Error Clearing Midi In buffer.",ex);
      }

      //----- Start timer and request dump
      myLabel.setText("Getting sysex dump...");
      sysexSize = 0;
      timer.start();
      driver.requestPatchDump(bankNum, patchNum);
    }
  } // End InnerClass: GetActionListener

//----------------------------------------------------------------------------------------------------------------------
// InnerClass: TimerActionListener
//----------------------------------------------------------------------------------------------------------------------

  public class TimerActionListener implements ActionListener {
    public void actionPerformed (ActionEvent evt) {
      try {
        while (PatchEdit.MidiIn.messagesWaiting(driver.inPort) > 0) {
          int size;
          size = PatchEdit.MidiIn.readMessage(driver.inPort, buffer, 1024);
//ErrorMsg.reportStatus ("TimerActionListener | size more bytes: " + size);
          System.arraycopy (buffer, 0, sysex, sysexSize, size);
          sysexSize += size;
          myLabel.setText(sysexSize + " Bytes Received");
        }
      } catch (Exception ex) {
        setVisible(false);
        timer.stop();
        ErrorMsg.reportError ("Error", "Unable to receive Sysex", ex);
      }
    }
  } // End InnerClass: SysexGetTimer


//----------------------------------------------------------------------------------------------------------------------
// InnerClass: ComboCellRenderer
//----------------------------------------------------------------------------------------------------------------------

  class ComboCellRenderer extends JLabel implements ListCellRenderer {
    public ComboCellRenderer() {
      setOpaque(true);
    }

    public Component getListCellRendererComponent (
        JList list,
        Object value,
        int index,
        boolean isSelected,
        boolean cellHasFocus
      ) {

      setText(value == null ? "" : value.toString());
      setBackground(isSelected ? Color.red : Color.white);
      setForeground(isSelected ? Color.white : Color.black);
      return this;
    }
  } // End InnerClass: ComboCellRenderer

//----------------------------------------------------------------------------------------------------------------------
// InnerClass: DeviceCellRenderer
//----------------------------------------------------------------------------------------------------------------------
 
   class DeviceCellRenderer extends ComboCellRenderer {
     public Component getListCellRendererComponent (
         JList list,
         Object value,
         int index,
         boolean isSelected,
         boolean cellHasFocus
       ) {
 
       super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
       setText(value == null ? "" : ((deviceAssignment)value).device.getDeviceName());
       return this;
     }
   } // End InnerClass: DeviceCellRenderer
 
//----------------------------------------------------------------------------------------------------------------------
// InnerClass: DriverCellRenderer
//----------------------------------------------------------------------------------------------------------------------

  class DriverCellRenderer extends ComboCellRenderer {
    public Component getListCellRendererComponent (
        JList list,
        Object value,
        int index,
        boolean isSelected,
        boolean cellHasFocus
      ) {

      super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
      setText(value == null ? "" : ((driverAssignment)value).driver.getPatchType());
      return this;
    }
  } // End InnerClass: DriverCellRenderer


 /**
  * We need to remember the original deviceNum variable of the Device and the Device itself.
  * Each deviceAssignment Object contains a List of driverAssignments which remembers
  * the original driverNum resp. driver.
  *
  * @see driverAssignment
  */
  class deviceAssignment
  {
    protected int       deviceNum;
    protected Device    device;
    protected ArrayList driverAssignmentList = new ArrayList();

    deviceAssignment(int deviceNum, Device device)
    {
      this.deviceNum = deviceNum;
      this.device    = device;
    }

    void add(int driverNum, Driver driver)
    {
      this.driverAssignmentList.add(new driverAssignment(driverNum, driver));
    }
  }


 /**
  * We need to remember the original driverNum variable of the Driver.
  *
  * @see deviceAssignment
  */
  class driverAssignment
  {
    protected int    driverNum;
    protected Driver driver;

    driverAssignment(int driverNum, Driver driver)
    {
      this.driverNum = driverNum;
      this.driver    = driver;
    }
  }

} // End Class: SysexGetDialog


