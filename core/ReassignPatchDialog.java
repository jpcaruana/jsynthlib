/*
 * ReassignPatchDialog.java
 */

package core;


/**
 * If more than two devices are loaded which supports the given patch,
 * show this Dialog to choose a new Device/Driver combination for the patch.
 * The internal patch assignment is used to send/play a patch.
 * @author  Torsten Tittmann
 * @version $Id$
 */
public class ReassignPatchDialog extends DevDrvPatchSelector {

    /**
     * Constructor
     * @param patch The Patch to reassign
     */
    public ReassignPatchDialog (IPatch patch) {
        super(patch, "Reassign Patch to another Device/Driver", "Reassign...");
    }

  
    /**
     * Makes the actual work after pressing the 'Reassign' button
     */
    protected void doit () {
        p.setDriver((IPatchDriver) driverComboBox.getSelectedItem());

        setVisible(false);
        dispose();
    }
}
