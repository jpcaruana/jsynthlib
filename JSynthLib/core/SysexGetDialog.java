//======================================================================================================================
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
  protected int bankNum;
  protected int patchNum;

  static byte[] buffer = new byte[256*1024];                     // N.B. WireMidiWrapper ignores MaxSize parameter of readMessage
  static byte [] sysex = new byte [256*1024];

  javax.swing.Timer timer;

  JLabel myLabel;

  JComboBox driverComboBox;
  JComboBox bankComboBox;
  JComboBox patchNumComboBox;

//----------------------------------------------------------------------------------------------------------------------
// Constructor: SysexGetDialog()
//----------------------------------------------------------------------------------------------------------------------

  public SysexGetDialog (JFrame Parent) { //, Driver driver, int bankNum, int patchNum) {
    super(Parent, "Get Sysex Data", true);

    JPanel dialogPanel = new JPanel(new BorderLayout(5, 5));
//    dialogPanel.setLayout(new GridLayout(0, 1));

    myLabel = new JLabel(" ", JLabel.CENTER);
    dialogPanel.add(myLabel, BorderLayout.NORTH);

    //=================================== Combo Panel ==================================
    //----- Create the combo boxes
    driverComboBox = new JComboBox();
    driverComboBox.addActionListener(new DriverActionListener());
    driverComboBox.setRenderer(new DriverCellRenderer());

    bankComboBox = new JComboBox();
    bankComboBox.setRenderer(new ComboCellRenderer());

    patchNumComboBox = new JComboBox();
    patchNumComboBox.setRenderer(new ComboCellRenderer());

    //----- Populate the combo boxes
  Device dev; 
       for (int i=0;i<PatchEdit.appConfig.deviceCount();i++)
            {
                dev=(Device)PatchEdit.appConfig.getDevice(i);
                for (int j=0;j<dev.driverList.size ();j++)
                {
                    if (!(Converter.class.isInstance (dev.driverList.get (j))))
                        driverComboBox.addItem (PatchEdit.getDriver(i,j));
                }
            }
   
  /* Object[] drivers = PatchEdit.DriverList.toArray();
    if (drivers.length > 1) {
      for (int i = 1; i < drivers.length ; i++)               // Ignore first driver (the core driver)
        driverComboBox.addItem(drivers[i]);
      driverComboBox.setSelectedIndex(0);
    } else {
      driverComboBox.setEnabled(false);
      bankComboBox.setEnabled(false);
      patchNumComboBox.setEnabled(false);
    }*/

    //----- Layout the labels in a panel.
    JPanel labelPanel = new JPanel(new GridLayout(0, 1, 5, 5));
    labelPanel.add(new JLabel("Driver:", JLabel.LEFT));
    labelPanel.add(new JLabel("Bank:", JLabel.LEFT));
    labelPanel.add(new JLabel("Patch:", JLabel.LEFT));

    //----- Layout the fields in a panel
    JPanel fieldPanel = new JPanel(new GridLayout(0, 1));
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
	    
    Patch p = new Patch(patchSysex);
    Patch[] patarray = p.dissect();
    for (int k = 0; k < patarray.length; k++) {
      PatchEdit.Clipboard = patarray[k];
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
// InnerClass: DriverActionListener
//----------------------------------------------------------------------------------------------------------------------

  public class DriverActionListener implements ActionListener {
    public void actionPerformed (ActionEvent evt) {
//      ErrorMsg.reportStatus("DriverActionListener->actionPerformed");

      driver = (Driver)driverComboBox.getSelectedItem();
      bankComboBox.removeAllItems();
      patchNumComboBox.removeAllItems();

      if (driver != null) {
        if (driver.bankNumbers.length > 1) {
          for (int i = 0 ; i < driver.bankNumbers.length ; i++) {
            bankComboBox.addItem(driver.bankNumbers[i]);
          }
        }


        if (driver.patchNumbers.length > 1) {
          for (int i = 0 ; i < driver.patchNumbers.length ; i++) {
            patchNumComboBox.addItem(driver.patchNumbers[i]);
          }
        }
      }

      bankComboBox.setEnabled(bankComboBox.getItemCount() > 1);
      // N.B. Do not enable patch selection for banks
      patchNumComboBox.setEnabled(!(driver instanceof BankDriver) && patchNumComboBox.getItemCount() > 1);
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
      driver = (Driver)driverComboBox.getSelectedItem();
      bankNum = bankComboBox.getSelectedIndex();
      patchNum = patchNumComboBox.getSelectedIndex();

//      ErrorMsg.reportStatus("");
//      ErrorMsg.reportStatus("SysexGetDialog | port: " + driver.inPort + " | bankNum: " + bankNum + " | patchNum: " + patchNum);

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
      setText(value == null ? "" : ((Driver)value).getDriverName());
      return this;
    }
  } // End InnerClass: DriverCellRenderer
} // End Class: SysexGetDialog


