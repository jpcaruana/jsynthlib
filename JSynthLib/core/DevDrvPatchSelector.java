/*
 * DevDrvpatchSelector.java
 */

package core;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Dialog to choose the Device, Driver, BankNumber and PatchNumber of
 * a Patch. Only Devices, Drivers, Bank- and PatchNumbers are choosable,
 * which are supporting the Patch.
 * Is used for Reassign..., Store... and SendTo... a patch.
 * @author  Torsten Tittmann
 * @version $Id$
 */
public class DevDrvPatchSelector extends JDialog {

    //===== Instance variables
    /** The last index in driver Combo Box. */
    private int driverNum;
    protected int patchNum;
    protected Patch p;
    private StringBuffer patchString;

    private JLabel myLabel;
    private JComboBox deviceComboBox;
    protected JComboBox driverComboBox;
    protected JComboBox bankComboBox;
    protected JComboBox patchNumComboBox;

    /**
     * Constructor without Bank/Patch ComboBox.
     * @param patch The Patch to store
     * @param wintitle String which appears as window title
     * @param action   String which describe the used menu item
     */
    public DevDrvPatchSelector (Patch patch, String wintitle, String action) {
        this(patch,-1,wintitle,action);
    }

    /**
     * Constructor with Bank/Patch ComboBox
     * @param patch The Patch to store
     * @param patchnum The default patchNumber
     * @param wintitle String which appears as window title
     * @param action   String which describe the used menu item
     */
    public DevDrvPatchSelector (Patch patch, int patchnum, String wintitle, String action) {
        super(PatchEdit.getInstance(), wintitle, true);

        // initialising some variables
        p            = patch;
        patchNum     = patchnum;
        patchString  = p.getPatchHeader();

        // now the panel
        JPanel dialogPanel = new JPanel(new BorderLayout(5, 5));

        myLabel=new JLabel("Please select a Location to \""+action+"\".",JLabel.CENTER);
        dialogPanel.add(myLabel, BorderLayout.NORTH);

        //=================================== Combo Panel ==================================
        //----- Create the combo boxes
        deviceComboBox = new JComboBox();
        deviceComboBox.addActionListener(new DeviceActionListener());
        driverComboBox = new JComboBox();
        if (patchNum >= 0) {
            driverComboBox.addActionListener(new DriverActionListener());
            bankComboBox = new JComboBox();
            patchNumComboBox = new JComboBox();
        }

        //----- Populate the combo boxes only with devices, which supports the patch
        int nDriver = 0;

	
        for (int i=0; i < PatchEdit.appConfig.deviceCount(); i++) {
            Device device = PatchEdit.appConfig.getDevice(i);
            boolean newDevice = true;
            for (int j=0, m=0; j<device.driverCount();j++) {
	        Driver driver = device.getDriver(j);
	        if (!(driver instanceof Converter)
	                && (driver.supportsPatch(patchString, p))) {
	            if (newDevice) {	// only one entry for each supporting device
	    		deviceComboBox.addItem(device);
	    		newDevice = false;
	  	    }
	  	    if (p.getDriver() == driver) { // default is the driver associated with patch
	    		driverNum = m;
            		deviceComboBox.setSelectedIndex(deviceComboBox.getItemCount() - 1); // invoke DeviceActionListener
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
        if (patchNum >= 0) {
    	    labelPanel.add(new JLabel("Bank:", JLabel.LEFT));
    	    labelPanel.add(new JLabel("Patch:", JLabel.LEFT));
        }
    
        //----- Layout the fields in a panel
        JPanel fieldPanel = new JPanel(new GridLayout(0, 1));
        fieldPanel.add(deviceComboBox);
        fieldPanel.add(driverComboBox);
        if (patchNum >= 0) {
    	    fieldPanel.add(bankComboBox);
    	    fieldPanel.add(patchNumComboBox);
        }
    
        //----- Create the comboPanel, labels on left, fields on right
        JPanel comboPanel = new JPanel(new BorderLayout());
        comboPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        comboPanel.add(labelPanel, BorderLayout.CENTER);
        comboPanel.add(fieldPanel, BorderLayout.EAST);
        dialogPanel.add(comboPanel, BorderLayout.CENTER);

        //=================================== Button Panel ==================================
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout ( new FlowLayout (FlowLayout.CENTER) );

        JButton doit = new JButton(action);
        doit.addActionListener(new DoitActionListener());
        buttonPanel.add(doit);

        JButton cancel = new JButton ("Cancel");
        cancel.addActionListener( new ActionListener() {
	    public void actionPerformed (ActionEvent e) {
	        setVisible(false);
	        dispose();
	    }
        });

        buttonPanel.add(cancel);
        getRootPane().setDefaultButton(doit);
        dialogPanel.add(buttonPanel, BorderLayout.SOUTH);

        //===== Final initialisation of dialog box
        getContentPane().add(dialogPanel);
        pack();
        centerDialog();

        if (nDriver > 0) {
            show();
        } else {
            JOptionPane.showMessageDialog(null, "Oops, No driver was found, which support this patch! Nothing will happen",
		    "Error while \""+action+"\" a patch", JOptionPane.WARNING_MESSAGE);
            dispose();
        }
    }

    protected void doit() {}
  
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
    private class DoitActionListener implements ActionListener {
	public void actionPerformed (ActionEvent evt) {
	    doit();
	}
    }

 
    /**
     * Repopulate the Driver ComboBox with valid drivers after a Device change
     */
    private class DeviceActionListener implements ActionListener {
        public void actionPerformed (ActionEvent evt) {
            driverComboBox.removeAllItems();

            Device device = (Device) deviceComboBox.getSelectedItem();
            int nDriver = 0;
            for (int i = 0; i < device.driverCount(); i++) {
	        Driver driver = device.getDriver(i);
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
    private class DriverActionListener implements ActionListener {
        public void actionPerformed (ActionEvent evt) {

            Driver driver = (Driver)driverComboBox.getSelectedItem();
            bankComboBox.removeAllItems();
            patchNumComboBox.removeAllItems();

            if (driver != null) {
                if (driver.bankNumbers != null && driver.bankNumbers.length > 1) {
                    for (int i = 0 ; i < driver.bankNumbers.length ; i++) {
            	        bankComboBox.addItem(driver.bankNumbers[i]);
                    }
                }

                String patchNumbers[] = getPatchNumbers(driver);
                if (patchNumbers.length > 1) {
                    for (int i = 0 ; i < patchNumbers.length ; i++) {
                        patchNumComboBox.addItem(patchNumbers[i]);
                    }
                    patchNumComboBox.setSelectedIndex(Math.min(patchNum, patchNumComboBox.getItemCount() - 1));
                }
            }

            bankComboBox.setEnabled(bankComboBox.getItemCount() > 1);
            // N.B. Do not enable patch selection for banks
            patchNumComboBox.setEnabled(!(driver instanceof BankDriver) && patchNumComboBox.getItemCount() > 1);
        }
    }
    
    /**
     * This method returns the list of patch numbers, which may change according
     * to the dialog type (some have patch locations to which you can send but
     * not store)
     */
    protected String[] getPatchNumbers(Driver driver)
    {
        return driver.getPatchNumbers();
    }
}
