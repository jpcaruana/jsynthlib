//=========================================================================
// Summary: SysexGetDialog.java
//=========================================================================

package core;

// import javax.swing.*;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.event.*;
import java.awt.*;

//=========================================================================
// Class: SysexGetDialog
//=========================================================================

/**
 * Dialog to choose the Device, Driver, BankNumber and PatchNumber of
 * the location, where a Patch should come from.  More than one of
 * each device is supported, but only devices/drivers are selectable,
 * which support the patch.
 * @author phil@muqus.com - 07/2001
 * @version $Id$
 */
public class SysexGetDialog extends JDialog {

  //===== Instance variables
  /** number of received data bytes. */
  private int sysexSize = 0;
  /** MIDI input port from which SysEX messages come. */
  private int inPort;

  private static byte[] buffer = new byte[256*1024]; // N.B. WireMidiWrapper ignores MaxSize parameter of readMessage
  private static byte[] sysex  = new byte[256*1024];

  private javax.swing.Timer timer;
  private JLabel myLabel;
  private JComboBox deviceComboBox;
  private JComboBox driverComboBox;
  private JComboBox bankComboBox;
  private JComboBox patchNumComboBox;

//--------------------------------------------------------------------------
// Constructor: SysexGetDialog()
//--------------------------------------------------------------------------

  public SysexGetDialog (JFrame parent) { //, Driver driver, int bankNum, int patchNum) {
    super(parent, "Get Sysex Data", true);

    JPanel dialogPanel = new JPanel(new BorderLayout(5, 5));
//    dialogPanel.setLayout(new GridLayout(0, 1));

    //myLabel = new JLabel(" ", JLabel.CENTER);
    myLabel=new JLabel("Please select a Patch Type to Get.",JLabel.CENTER);
    dialogPanel.add(myLabel, BorderLayout.NORTH);

    //=================================== Combo Panel ======================
    //----- Create the combo boxes
    deviceComboBox = new JComboBox();
    deviceComboBox.addActionListener(new DeviceActionListener());
    driverComboBox = new JComboBox();
    driverComboBox.addActionListener(new DriverActionListener());
    bankComboBox = new JComboBox();
    patchNumComboBox = new JComboBox();

    // First Populate the Device/Driver List with all Device/Driver
    // combinations except converters
    // skip 0 (Generic Device)
    for (int i=1; i < PatchEdit.appConfig.deviceCount(); i++) {
      Device device=PatchEdit.appConfig.getDevice(i);
      for (int j=0; j <device.driverList.size(); j++) {
	Driver driver = (Driver) device.driverList.get(j);
        if (!(driver instanceof Converter)) { // Skipping a converter
	  deviceComboBox.addItem(device);
	  break;
        }
      }
    }
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

    //=================================== Button Panel =====================
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

    //===================================== Timer ==========================
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

//--------------------------------------------------------------------------
// SysexGetDialog->centerDialog
//--------------------------------------------------------------------------

  protected void centerDialog () {
    Dimension screenSize = this.getToolkit().getScreenSize();
    Dimension size = this.getSize ();
    this.setLocation((screenSize.width - size.width)/2, (screenSize.height - size.height)/2);
  }

//--------------------------------------------------------------------------
// SysexGetDialog->pasteIntoSelectedFrame
//--------------------------------------------------------------------------

  protected void pasteIntoSelectedFrame() {
//     ErrorMsg.reportStatus("SysexGetDialog->pasteIntoSelectedFrame: " + sysexSize);
    if (sysexSize < 20)
      return;

    byte[] patchSysex;
    if (sysex[0]==-16) { //F0
      patchSysex= new byte[sysexSize];
      System.arraycopy(sysex, 0, patchSysex, 0, sysexSize);
    } else {
      int i=0;
      while ((sysex[i]!=-16) && (i<sysexSize))
	i++;
      if (i==sysexSize)
	return;
      patchSysex= new byte[sysexSize-i];
      System.arraycopy(sysex, i, patchSysex, 0, sysexSize-i);
    }

    Driver driver = (Driver) driverComboBox.getSelectedItem();
    Patch p = new Patch(patchSysex, driver);
    Patch[] patarray = p.dissect();

    for (int k = 0; k < patarray.length; k++) {
      Patch pk = patarray[k];
      StringBuffer patchString = pk.getPatchHeader();

      // Maybe you don't get the expected patch!
      // Check all devices/drivers again!
      if (!(pk.getDriver().supportsPatch(patchString,pk)))
      {
      testforDriver:
	{
	  for (int i = 0; i < PatchEdit.appConfig.deviceCount(); i++)
	  {
	    // first check the requested device.
	    // then starting index '1'. (index 0 is 'generic driver')
	    Device device = (i == 0) ? pk.getDevice() : PatchEdit.appConfig.getDevice(i);
	    for (int j=0;j<device.driverList.size();j++)
	    {
	      Driver d = (Driver) device.driverList.get(j);
	      if (!(d instanceof Converter)
		  && d.supportsPatch(patchString, pk)) {
		// driver found
		driver = d;
		pk.setDriver(driver);
		driver.trimSysex(pk);
		JOptionPane.showMessageDialog
		  (null,
		   "You requested a "+driver.toString()+" patch!"+
		   "\nBut you got a "+pk.getDriver().toString()+" patch.",
		   "Warning", JOptionPane.WARNING_MESSAGE);
		break testforDriver;
	      }
	    } // end of driver (j) loop
	  } // end of device (i) loop

	  // driver not found
	  pk.setDriver(null); //reset
	  pk.setComment("Probably a "
			+ LookupManufacturer.get(pk.sysex[1],
						 pk.sysex[2],
						 pk.sysex[3])
			+ " Patch, Size: " + pk.sysex.length);
	  JOptionPane.showMessageDialog
	    (null,
	     "You requested a "+driver.toString()+" patch!"+
	     "\nBut you got a not supported patch!\n"+pk.getComment(),
	     "Warning", JOptionPane.WARNING_MESSAGE);
	} // testforDriver
      } // !(pk.getDriver().supportsPatch(patchString,pk))

      // paste the patch.
      PatchEdit.Clipboard = pk;	// for PastePatch()
      try {
	((PatchBasket)PatchEdit.desktop.getSelectedFrame()).PastePatch();
      } catch (Exception ex) {
	JOptionPane.showMessageDialog (null, "Library to Receive into must be the focused Window.","Error", JOptionPane.ERROR_MESSAGE);
      }
    } // end of k loop
  }

//--------------------------------------------------------------------------
// InnerClass: DoneActionListener
//--------------------------------------------------------------------------

  public class DoneActionListener implements ActionListener {
    public void actionPerformed (ActionEvent evt) {
      timer.stop();
      pasteIntoSelectedFrame();
      setVisible(false);
      PatchEdit.echoTimer.start();
    }
  } // End InnerClass: DoneActionListener

//--------------------------------------------------------------------------
// InnerClass: DeviceActionListener
//--------------------------------------------------------------------------

   public class DeviceActionListener implements ActionListener {
     public void actionPerformed (ActionEvent evt) {
//     ErrorMsg.reportStatus("DeviceActionListener->actionPerformed");
       driverComboBox.removeAllItems();

       Device device = (Device) deviceComboBox.getSelectedItem();
       for (int i = 0; i < device.driverList.size(); i++) {
	 Driver driver = (Driver) device.driverList.get(i);
	 if (!(driver instanceof Converter)) {
	   driverComboBox.addItem(driver);
	 }
       }
       driverComboBox.setEnabled(driverComboBox.getItemCount() > 1);
     }
   } // End InnerClass: DeviceActionListener

//--------------------------------------------------------------------------
// InnerClass: DriverActionListener
//--------------------------------------------------------------------------

  public class DriverActionListener implements ActionListener {
    public void actionPerformed (ActionEvent evt) {
//       ErrorMsg.reportStatus("DriverActionListener->actionPerformed");
      Driver driver = (Driver) driverComboBox.getSelectedItem();
      if (driver == null) 	// for driverComboBox.removeAllItems()
	return;

      bankComboBox.removeAllItems();
      patchNumComboBox.removeAllItems();

      if (driver.bankNumbers.length > 1) {
	for (int i = 0 ; i < driver.bankNumbers.length ; i++) {
	  bankComboBox.addItem(driver.bankNumbers[i]);
	}
      }
      bankComboBox.setEnabled(bankComboBox.getItemCount() > 1);

      if (driver.patchNumbers.length > 1) {
	for (int i = 0 ; i < driver.patchNumbers.length ; i++) {
	  patchNumComboBox.addItem(driver.patchNumbers[i]);
	}
      }
      // N.B. Do not enable patch selection for banks
      patchNumComboBox.setEnabled(!(driver instanceof BankDriver)
				  && patchNumComboBox.getItemCount() > 1);
    }
  } // End InnerClass: DriverActionListener

//--------------------------------------------------------------------------
// InnerClass: PasteActionListener
//--------------------------------------------------------------------------

  public class PasteActionListener implements ActionListener {
    public void actionPerformed (ActionEvent evt) {
      myLabel.setText(" ");
      timer.stop();
      pasteIntoSelectedFrame();
      sysexSize = 0;
    }
  } // End InnerClass: PasteActionListener

//--------------------------------------------------------------------------
// InnerClass: GetActionListener
//--------------------------------------------------------------------------

  public class GetActionListener implements ActionListener {
    public void actionPerformed (ActionEvent evt) {
      Driver driver = (Driver) driverComboBox.getSelectedItem();
      int bankNum = bankComboBox.getSelectedIndex();
      int patchNum = patchNumComboBox.getSelectedIndex();
      inPort = driver.getInPort();

      ErrorMsg.reportStatus("");
      ErrorMsg.reportStatus("SysexGetDialog | port: " + inPort + " | bankNum: " + bankNum + " | patchNum: " + patchNum);

      //----- Clear MidiIn buffer
      try {
      	if(PatchEdit.MidiIn == null) {
	  System.err.println("Yup... it's null!");
      	}
        while (PatchEdit.MidiIn.messagesWaiting(inPort) > 0)
          PatchEdit.MidiIn.readMessage(inPort, buffer, 1024);

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

//--------------------------------------------------------------------------
// InnerClass: TimerActionListener
//--------------------------------------------------------------------------

  public class TimerActionListener implements ActionListener {
    public void actionPerformed (ActionEvent evt) {
      try {
        while (PatchEdit.MidiIn.messagesWaiting(inPort) > 0) {
          int size = PatchEdit.MidiIn.readMessage(inPort, buffer, 1024);
// 	  ErrorMsg.reportStatus ("TimerActionListener | size more bytes: " + size);
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

} // End Class: SysexGetDialog
