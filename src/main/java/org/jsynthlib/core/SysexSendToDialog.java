/*
 * SysexSendToDialog.java
 */

package org.jsynthlib.core;


/**
 * Dialog to choose a Device and Driver to send the patch into an Edit
 * buffer.  More than one of each device is supported, but only
 * devices/drivers are selectable, which support the patch.
 * @author  Torsten Tittmann
 * @version $Id$ */
public class SysexSendToDialog extends DevDrvPatchSelector {
    /**
     * Constructor
     * @param patch The Patch to 'send to...'
     */
    public SysexSendToDialog (IPatch patch) {
        //super(PatchEdit.getInstance(), "Send Sysex Data into Edit Buffer of a specified device", true);
        super(patch,"Send Sysex Data into Edit Buffer of a specified device","Send To...");
    }


    /**
     * Makes the actual work after pressing the 'Send to...' button
     */
    protected void doit () {
        p.setDriver((IPatchDriver) driverComboBox.getSelectedItem());
        ((ISinglePatch) p).send();

        setVisible(false);
        dispose();
    }
}
