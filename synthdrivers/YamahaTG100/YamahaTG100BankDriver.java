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

package synthdrivers.YamahaTG100;

import core.*;

import javax.swing.*;

import org.jsynthlib.core.BankDriver;
import org.jsynthlib.core.DriverUtil;
import org.jsynthlib.core.ErrorMsg;
import org.jsynthlib.core.Patch;

import java.io.*;

/**
 * Driver for Yamaha TG100 Bank's
 *
 * @author Joachim Backhaus
 * @version $Id$
 */
public class YamahaTG100BankDriver extends BankDriver {

    /*
    * Sends only "all" dumps of size 8266 in Bytes
    */
    public YamahaTG100BankDriver() {
        super("Bank", "Joachim Backhaus", TG100Constants.PATCH_NUMBER_LENGTH, 4);

        this.sysexID = TG100Constants.SYSEX_ID;

        this.patchNameStart = TG100Constants.PATCH_NAME_START;
        this.patchNameSize = TG100Constants.PATCH_NAME_SIZE;

        // Use always 0x10 as Device ID, else it doesn't work
        //this.deviceIDoffset = 2;
        this.deviceIDoffset = 0;

        this.checksumStart = TG100Constants.CHECKSUM_START;
        this.checksumEnd = TG100Constants.CHECKSUM_END;
        this.checksumOffset = TG100Constants.CHECKSUM_OFFSET;

        this.bankNumbers = new String[] { "Internal voice bank" }; // Bank is ignored since there is only one bank for internal voices

        this.patchNumbers = DriverUtil.generateNumbers(1, TG100Constants.PATCH_NUMBER_LENGTH, "#");

        this.singleSysexID = this.sysexID;
        this.singleSize = TG100Constants.PATCH_SIZE;

        this.patchSize = this.singleSize * patchNumbers.length; // Should be 6720 Bytes
    }

    public void calculateChecksum(Patch p, int patchNum) {
        calculateChecksum(  p,
                            getPatchStart(patchNum) + this.checksumStart,
                            getPatchStart(patchNum) + this.checksumEnd,
                            getPatchStart(patchNum) + this.checksumOffset);
    }

    /**
    * Replacement for sendPatchWorker
    * which doesn't work here as each sysex sub part has to be send separately
    */
    private final void doSendPatch(Patch p) {
        byte[] tempSysex = new byte[this.singleSize];

        for(int patchNum = 0; patchNum < patchNumbers.length; patchNum++) {
            this.calculateChecksum(p, patchNum);

            System.arraycopy(   p.sysex,
                                getPatchStart(patchNum),
                                tempSysex,
                                0,
                                tempSysex.length);

            send(tempSysex);
        }

        try {
            // Wait a little bit else the TG-100 can't handle the SysEx
            Thread.sleep(30);
        } catch (Exception ex) {
            ErrorMsg.reportError("Error", "Error requesting the internal bank.", ex);
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
        int start = this.singleSize * patchNum;

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
                            0,
                            bank.sysex,
                            getPatchStart(patchNum) + TG100Constants.SYSEX_HEADER_OFFSET,
                            this.singleSize - TG100Constants.SYSEX_HEADER_OFFSET);

        this.calculateChecksum(bank, patchNum);
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
            calculateChecksum(p);

            return p;
        } catch (Exception e) {
            ErrorMsg.reportError("Error", "Error in Yamaha TG-100 Bank Driver", e);
            return null;
        }
    }

    /**
    * Get the name of the patch at the given number
    */
    public String getPatchName(Patch p, int patchNum) {
        int nameStart = getPatchStart(patchNum);
        nameStart += TG100Constants.PATCH_NAME_START; //offset of name in patch data
        try {
            StringBuffer s = new StringBuffer(new String(   p.sysex,
                                                            nameStart,
                                                            TG100Constants.PATCH_NAME_SIZE,
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

        this.calculateChecksum(p, patchNum);
    }

    public Patch createNewPatch() {
        byte [] sysex = new byte[this.patchSize];

        Patch p;
        Patch tempPatch;

        for(int patchNum = 0; patchNum < patchNumbers.length; patchNum++) {
            tempPatch = YamahaTG100SingleDriver.createNewPatch(patchNum);

            calculateChecksum(tempPatch);

            System.arraycopy(   tempPatch.sysex,
                                0,
                                sysex,
                                getPatchStart(patchNum),
                                this.singleSize );
        }

        p = new Patch(sysex);

        return p;
    }
}

