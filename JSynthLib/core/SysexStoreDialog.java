/*
 * SysexStoreDialog.java
 */

package core;

/**
 * Dialog to choose the Device, Driver, BankNumber and PatchNumber of
 * the location, where a Patch should be stored.  More than one of
 * each device is supported, but only devices/drivers are selectable,
 * which support the patch.
 * @author  Torsten Tittmann
 * @version $Id$
 */
public class SysexStoreDialog extends DevDrvPatchSelector {

    /**
     * Constructor with standard default patchNumber=0
     * @param patch The Patch to store
     */
    public SysexStoreDialog (IPatch patch) {
        this(patch,0);
    }

    /**
     * Constructor with choosable default patchNumber
     * @param patch The Patch to store
     * @param patchnum The default patchNumber
     */
    public SysexStoreDialog (IPatch patch, int patchnum) {
        super(patch,patchnum,"Store Sysex Data","Store...");
    }

    /**
     * getPatchNumbers is overridden for SystexStoreDialog.
     * Only storable patches are displayed.
     */
    protected String[] getPatchNumbers(IPatchDriver driver)
    {
        return driver.getPatchNumbersForStore();
    }

    /**
     * Makes the actual work after pressing the 'Store' button
     */
    protected void doit() {
        p.setDriver((IPatchDriver) driverComboBox.getSelectedItem());
        int bankNum  = bankComboBox.getSelectedIndex();
        int patchNum = patchNumComboBox.getSelectedIndex();
        p.send(bankNum, patchNum);

        setVisible(false);
        dispose();
    }
}
