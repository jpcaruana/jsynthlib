/*
 * Copyright 2005 Joachim Backhaus
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

package org.jsynthlib.drivers.waldorf.mw2;

import java.io.UnsupportedEncodingException;

import javax.swing.JOptionPane;

import org.jsynthlib.core.BankDriver;
import org.jsynthlib.core.DriverUtil;
import org.jsynthlib.core.ErrorMsg;
import org.jsynthlib.core.ISinglePatch;
import org.jsynthlib.core.Patch;
import org.jsynthlib.core.SysexHandler;


/*
 * 
 * TODO 
 * - Storing banks doesn't work
 */

/**
 * Driver for Microwave 2 / XT / XTK banks
 *
 * @author Joachim Backhaus
 * @version $Id$
 */
public class WaldorfMW2BankDriver extends BankDriver {
    
    public WaldorfMW2BankDriver() {
        super("Bank", "Joachim Backhaus", MW2Constants.PATCH_NUMBERS, 4);
        
        this.sysexID = MW2Constants.SYSEX_ID + "10";
        // Get all sounds in both banks
        this.sysexRequestDump = new SysexHandler( "F0 3E 0E @@ 00 *BB* *NN* *XSUM* F7" );
        
        this.patchNameStart = 0;
        this.patchNameSize = 0;
        this.deviceIDoffset = MW2Constants.DEVICE_ID_OFFSET;               
        
        this.singleSysexID = this.sysexID;
        this.singleSize = MW2Constants.PATCH_SIZE;
        
        this.bankNumbers = MW2Constants.BANK_NAMES;
        
        this.patchNumbers = DriverUtil.generateNumbers(1, MW2Constants.PATCH_NUMBERS, "000");                       
        
        // Should be 33.920 Bytes
        this.patchSize = MW2Constants.PATCH_SIZE * MW2Constants.PATCH_NUMBERS;
        
        // The SysEx documentation said 5 but that's wrong!
        this.checksumStart = MW2Constants.SYSEX_HEADER_OFFSET;
        this.checksumOffset = this.singleSize - MW2Constants.SYSEX_FOOTER_SIZE;
        this.checksumEnd = this.checksumOffset - 1;        
    } 
    
    /**
     * Calculate check sum of a single program in the bank.
     *
     * @param p a <code>Patch</code> value
     * @param patchNo the number of the single program
     */
    private void calculateChecksum(Patch p, int patchNo) {
        int sum = 0;
        int offset = patchNo * this.singleSize;
        
        for (int i = this.checksumStart; i <= this.checksumEnd; i++)
            sum += p.sysex[offset + i];
        p.sysex[offset + this.checksumOffset] = (byte) (sum & 0x7F);
    }
    
    /**
     * Calculate check sum of a complete bank.
     *
     * @param p a <code>Patch</code> value
     */
    protected void calculateChecksum(Patch p) {
        int sum;        
        for (int patchNo = 0; patchNo < this.patchNumbers.length; patchNo++) {
            sum = 0;
            calculateChecksum(p, patchNo);            
        }
    }
    
    /**
     * Get the index where the patch starts in the banks SysEx data.
     */
    protected int getPatchStart(int patchNum) {                                                    
        return (MW2Constants.PATCH_SIZE * patchNum) + MW2Constants.SYSEX_HEADER_OFFSET;
    }
    
    /**
     * Get the name of the patch at the given number
     */
    protected String getPatchName(Patch p, int patchNum) {
        int nameStart = getPatchStart(patchNum) + MW2Constants.PATCH_NAME_START - MW2Constants.SYSEX_HEADER_OFFSET;
        
        try {
            StringBuffer s = new StringBuffer(new String(   p.sysex,
                    nameStart,
                    MW2Constants.PATCH_NAME_SIZE,
                    "US-ASCII"));
            return s.toString();
        } catch (UnsupportedEncodingException ex) {
            return "-";
        }
    }       
    
    /** Set the name of the patch at the given number <code>patchNum</code>. */
    protected void setPatchName(Patch p, int patchNum, String name) {
        setPatchName(p.sysex, patchNum, name);
        calculateChecksum(p, patchNum);
    }
    
    /** Set the name of the patch at the given number <code>patchNum</code>. */
    protected void setPatchName(byte[] tempSysex, int patchNum, String name) {
        int tempPatchNameStart = getPatchStart(patchNum) + MW2Constants.PATCH_NAME_START - MW2Constants.SYSEX_HEADER_OFFSET;
        
        while (name.length() < MW2Constants.PATCH_NAME_SIZE)
            name = name + " ";
        
        byte[] namebytes = new byte[MW2Constants.PATCH_NAME_SIZE];
        try {
            namebytes = name.getBytes("US-ASCII");
            for (int i = 0; i < MW2Constants.PATCH_NAME_SIZE; i++)
                tempSysex[tempPatchNameStart + i] = namebytes[i];
        } catch (UnsupportedEncodingException ex) {
            return;
        }
    }
    
    /**
     * Puts a patch into the bank, converting it as needed
     */
    protected void putPatch(Patch bank, Patch p, int patchNum) {
        if (!canHoldPatch(p)) {             
            JOptionPane.showMessageDialog(  null,
                    "This type of patch does not fit in to this type of bank.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;             
        }
        
        System.arraycopy(   p.sysex,
                MW2Constants.SYSEX_HEADER_OFFSET,
                bank.sysex,
                getPatchStart(patchNum),
                // The SYSEX_FOOTER_SIZE is for that the checksum is copied, too!
                MW2Constants.PURE_PATCH_SIZE + MW2Constants.SYSEX_FOOTER_SIZE);
    }
    
    /**
     * Sends a patch to the synth's edit buffer.<p>
     *
     * Override this in the subclass if parameters or warnings need to
     * be sent to the user (aka if the particular synth does not have
     * a edit buffer or it is not MIDI accessable).
     * @see Patch#send()
     * @see ISinglePatch#send()
     */
    protected void sendPatch(Patch p) {
        p.sysex[MW2Constants.DEVICE_ID_OFFSET] = (byte) ( getDeviceID() - 1);
        
        send(p.sysex);
    }
    
    /**
     * Store the bank to a given bank on the synth. Ignores the
     * patchNum parameter.
     * @see Patch#send(int, int)
     */
    protected void storePatch(Patch bank, int bankNum, int patchNum) {
        // The Microwave XT hang up when I tried to send all 128 single programs
        // of the bank at one time
        calculateChecksum(bank);
        Patch singlePatch;
        
        for (int patchNo = 0; patchNo < this.patchNumbers.length; patchNo++) {
            singlePatch = getPatch(bank, bankNum, patchNo);

            sendPatch(singlePatch);
            
            try {
                // Wait a little bit so that the Microwave 2 / XT doesn't hang up
                Thread.sleep(50);
            } catch (InterruptedException ex) {}

        }
    }
    
    /**
     * Gets a patch from the bank, converting it as needed
     */
    protected Patch getPatch(Patch bank, int bankNum, int patchNum) {
        try {
            byte [] sysex = new byte[this.singleSize];
            
            System.arraycopy(   bank.sysex,
                    getPatchStart(patchNum),
                    sysex,
                    MW2Constants.SYSEX_HEADER_OFFSET,
                    MW2Constants.PURE_PATCH_SIZE);
            Patch p = new Patch(sysex);
            WaldorfMW2SingleDriver.createPatchHeader(   p, bankNum, patchNum);
            p.sysex[264] = MW2Constants.SYSEX_END_BYTE;
            WaldorfMW2SingleDriver.calculateChecksum(   p.sysex, 
                    MW2Constants.SYSEX_HEADER_OFFSET, 
                    MW2Constants.SYSEX_HEADER_OFFSET + MW2Constants.PURE_PATCH_SIZE - 1, 
                    MW2Constants.SYSEX_HEADER_OFFSET + MW2Constants.PURE_PATCH_SIZE);
            
            return p;
        } catch (Exception e) {
            ErrorMsg.reportError("Error", "Error in WaldorfMW2 bank driver", e);
            return null;
        }
    }
    
    /**
     * Gets a patch from the bank, converting it as needed.
     */
    protected Patch getPatch(Patch bank, int patchNum) {
        return getPatch(bank, 0, patchNum);
    }
    
    public void requestPatchDump(int bankNum, int patchNum) {
        // Request dumps for all 128 single programs of a bank (that are 128 requests!!!)
        for(int patchNo = 0; patchNo < patchNumbers.length; patchNo++) {
            SysexHandler.NameValue[] nameValues = {
                    new SysexHandler.NameValue("BB", bankNum ),
                    new SysexHandler.NameValue("NN", patchNo ),
                    new SysexHandler.NameValue("XSUM", ((byte)(bankNum + patchNo)) & 0x7F )
            };

            send(sysexRequestDump.toSysexMessage(getDeviceID(), nameValues ) );            

            try {
                // Wait a little bit so that everything is in the correct sequence
                Thread.sleep(50);
            } catch (Exception ex) {}
        }                
    }
    
    /**
     * Creates a new bank (128 user sounds)
     */
    protected Patch createNewPatch() {
        byte[] bankSysex = new byte[this.patchSize];
        Patch p;
        byte[] patchSysex  = new byte[MW2Constants.PATCH_SIZE];     
        Patch tempPatch;
        int offset = 0;
        
        for(int patchNo = 0; patchNo < patchNumbers.length; patchNo++) {            
            tempPatch = new Patch(patchSysex, getDevice());
            WaldorfMW2SingleDriver.createPatchHeader(tempPatch, 0, patchNo);            
            tempPatch.sysex[264] = MW2Constants.SYSEX_END_BYTE;
            System.arraycopy(   patchSysex,
                    0,
                    bankSysex,
                    getPatchStart(patchNo) - MW2Constants.SYSEX_HEADER_OFFSET,
                    MW2Constants.PATCH_SIZE );
            setPatchName(bankSysex, patchNo, "New sound " + patchNo);
        }
        
        p = new Patch(bankSysex);
            
        for(int patchNo = 0; patchNo < patchNumbers.length; patchNo++) {            
            WaldorfMW2SingleDriver.calculateChecksum(   p.sysex, 
                    offset + this.checksumStart,
                    offset + this.checksumEnd,
                    offset + this.checksumOffset);
            offset += this.singleSize;
        }                                       
        
        return p;
    }        
}

