/*
 * Copyright 2004 Joachim Backhaus
 *
 * This file is part of JSynthLib.
 *
 * JSynthLib is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
 *
 * JSynthLib is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JSynthLib; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 */

package synthdrivers.QuasimidiQuasar;

import core.*;

import javax.swing.*;
import java.io.*;

/**
 * Driver for Quasimidi Quasar Performance Bank's
 *
 * @author Joachim Backhaus
 * @version $Id$
 */
public class QuasimidiQuasarBankDriver extends BankDriver {
    public QuasimidiQuasarBankDriver() {
        super("Performance Bank", "Joachim Backhaus", QuasarConstants.PATCH_NUMBERS.length, 5);

        this.sysexID = QuasarConstants.SYSEX_ID;

        // This one is never really used, just a dummy to prevent the "this synth doesn't support patch request" dialog
        this.sysexRequestDump = new SysexHandler( QuasarConstants.SYSEX_PERFORMANCE_REQUEST[0] );

        this.patchNameStart = 0;
        this.patchNameSize = 0; // Just one bank ("RAM")
        this.deviceIDoffset = QuasarConstants.ARRAY_DEVICE_ID_OFFSET;
        this.bankNumbers = new String[] { "RAM" };
        this.patchNumbers = QuasarConstants.PATCH_NUMBERS;

        this.singleSysexID = this.sysexID;
        this.singleSize = QuasarConstants.PATCH_SIZE;

        this.patchSize = singleSize * patchNumbers.length;
    }

    /**
    * The Quasar uses no checksum therefore this method is empty
    */
    public void calculateChecksum(Patch p) {
        // no checksum, do nothing
    }

    /**
    * Replacement for sendPatchWorker
    * which doesn't work here as the Device ID has to be set in each one of the 6 SysEx sub parts
    */
    private final void doSendPatch(Patch p) {
        if (deviceIDoffset > 0) {
            int deviceID = ( getDeviceID() - 1);
            int temporaryOffset = 0;

            for(int patchNo = 0; patchNo < patchNumbers.length; patchNo++) {
                temporaryOffset = deviceIDoffset + (patchNo * this.singleSize);

                p.sysex[temporaryOffset]                                       = (byte) deviceID;
                p.sysex[temporaryOffset + QuasarConstants.ARRAY_PART_1_OFFSET] = (byte) deviceID;
                p.sysex[temporaryOffset + QuasarConstants.ARRAY_PART_2_OFFSET] = (byte) deviceID;
                p.sysex[temporaryOffset + QuasarConstants.ARRAY_PART_3_OFFSET] = (byte) deviceID;
                p.sysex[temporaryOffset + QuasarConstants.ARRAY_PART_4_OFFSET] = (byte) deviceID;
                p.sysex[temporaryOffset + QuasarConstants.ARRAY_NAME_OFFSET]   = (byte) deviceID;
            }

            send(p.sysex);
        }
    }

    /**
    * Stores the bank
    *
    * @param p          The bank to store
    * @param bankNum    Ignored
    * @param patchNum   Ignored
    */
    public void storePatch(Patch p, int bankNum, int patchNum) {
        doSendPatch(p);
    }

    /**
    * Get the index where the patch starts in the banks SysEx data.
    */
    public int getPatchStart(int patchNum) {
        int start = (this.singleSize * patchNum);

        return start;
    }

    /**
    * Puts a patch into the bank, converting it as needed
    */
    public void putPatch(Patch bank, Patch p, int patchNum) {
        if (!canHoldPatch(p)) {
            {
                JOptionPane.showMessageDialog(  null,
                                                "This type of patch does not fit in to this type of bank.",
                                                "Error",
                                                JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        System.arraycopy(   p.sysex,
                            QuasarConstants.SYSEX_HEADER_OFFSET,
                            bank.sysex,
                            getPatchStart(patchNum) + QuasarConstants.SYSEX_HEADER_OFFSET,
                            this.singleSize - QuasarConstants.SYSEX_HEADER_OFFSET);
    }

    /**
    * Gets a patch from the bank, converting it as needed
    */
    public Patch getPatch(Patch bank, int patchNum) {
        try{
            byte [] sysex = new byte[this.singleSize];

            System.arraycopy(   bank.sysex,
                                getPatchStart(patchNum),
                                sysex,
                                0,
                                this.singleSize);
            Patch p = new Patch(sysex);

            return p;
        } catch (Exception e) {
            ErrorMsg.reportError("Error", "Error in Quasar Bank Driver", e);
            return null;
        }
    }

    /**
    * Get the name of the patch at the given number
    */
    public String getPatchName(Patch p, int patchNum) {
        int nameStart = getPatchStart(patchNum);
        nameStart += QuasarConstants.PATCH_NAME_START; //offset of name in patch data
        try {
            StringBuffer s = new StringBuffer(new String(   p.sysex,
                                                            nameStart,
                                                            QuasarConstants.PATCH_NAME_SIZE,
                                                            "US-ASCII"));
            return s.toString();
        } catch (UnsupportedEncodingException ex) {
            return "-";
        }
    }

    /** Set the name of the patch at the given number <code>patchNum</code>. */
    public void setPatchName(Patch p, int patchNum, String name) {
        int tempPatchNameStart = this.patchNameStart + this.getPatchStart(patchNum);

        while (name.length() < patchNameSize)
		    name = name + " ";

        byte[] namebytes = new byte[patchNameSize];
        try {
	        namebytes = name.getBytes("US-ASCII");
	        for (int i = 0; i < patchNameSize; i++)
		        p.sysex[tempPatchNameStart + i] = namebytes[i];
	    } catch (UnsupportedEncodingException ex) {
	        return;
	    }
    }

    /**
    * Request a dump of all 100 RAM Performances
    */
    public void requestPatchDump(int bankNum, int patchNum) {
        if (sysexRequestDump == null) {
            JOptionPane.showMessageDialog
                (PatchEdit.getInstance(),
                 "The " + toString()
                 + " driver does not support patch getting.\n\n"
                 + "Please start the patch dump manually...",
                 "Get Patch", JOptionPane.WARNING_MESSAGE);
        }
        else {
            // Request dumps for all 100 RAM peformances (that are 5 * 100 requests!!!)
            for(int patchNo = 0; patchNo < patchNumbers.length; patchNo++) {
                for(int count = 0; count < QuasarConstants.SYSEX_PERFORMANCE_REQUEST.length; count++) {

                    this.sysexRequestDump = new SysexHandler( QuasarConstants.SYSEX_PERFORMANCE_REQUEST[count] );

                    send(sysexRequestDump.toSysexMessage(getDeviceID(),
                         new NameValue("perfNumber", patchNo + QuasarConstants.SYSEX_PERFORMANCE_OFFSET)
                                                        )
                    );

                    try {
                        // Wait a little bit so that everything is in the correct sequence
                        Thread.sleep(50);
                    } catch (Exception ex) {
                        ErrorMsg.reportError("Error", "Error requesting all Quasar RAM performances.", ex);
                    }
                }
            }
        }
    }

    /**
    * Creates a new bank with 100 new Quasar Performances
    */
    public Patch createNewPatch() {
        byte [] sysex = new byte[this.patchSize];

        Patch tempPatch;

        for(int patchNo = 0; patchNo < patchNumbers.length; patchNo++) {
            tempPatch = QuasimidiQuasarSingleDriver.createNewPatch(patchNo, QuasarConstants.SYSEX_PERFORMANCE_OFFSET);

            System.arraycopy(   tempPatch.sysex,
                                0,
                                sysex,
                                (patchNo * singleSize ),
                                singleSize );
        }

        Patch p = new Patch(sysex);

        return p;
    }
}

