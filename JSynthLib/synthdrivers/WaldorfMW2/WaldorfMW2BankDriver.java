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

package synthdrivers.WaldorfMW2;

import java.io.UnsupportedEncodingException;

import javax.swing.JOptionPane;

import core.BankDriver;
import core.DriverUtil;
import core.ErrorMsg;
import core.Patch;
import core.SysexHandler;

/**
 * Driver for Microwave 2 / XT / XTK banks
 *
 * @author Joachim Backhaus
 * @version $Id$
 */
public class WaldorfMW2BankDriver extends BankDriver {
    
    public WaldorfMW2BankDriver() {
        super("Bank", "Joachim Backhaus", MW2Constants.PATCH_NUMBERS, 8);
        
        this.sysexID = MW2Constants.SYSEX_ID + "10";
        // Get all sounds in both banks
        this.sysexRequestDump = new SysexHandler( "F0 3E 0E @@ 00 10 00 01 F7" );
        
        this.patchNameStart = 0;
        this.patchNameSize = 0;
        this.deviceIDoffset = MW2Constants.DEVICE_ID_OFFSET;               
        
        this.singleSysexID = this.sysexID;
        this.singleSize = MW2Constants.PATCH_SIZE;
        
        this.bankNumbers = MW2Constants.BANK_NAMES;
        
        patchNumbers = DriverUtil.generateNumbers(1, MW2Constants.PATCH_NUMBERS, "000");                       
        
        this.patchSize = (MW2Constants.PURE_PATCH_SIZE * MW2Constants.PATCH_NUMBERS) + MW2Constants.SYSEX_HEADER_OFFSET + MW2Constants.SYSEX_FOOTER_SIZE;
        
        this.checksumStart = MW2Constants.SYSEX_HEADER_OFFSET;  // Fuck it! The SysEx documentation said 5 but that's wrong!
        this.checksumOffset = this.patchSize - MW2Constants.SYSEX_FOOTER_SIZE;
        this.checksumEnd = this.checksumOffset - 1;
        
    } 
    
    /**
     * Calculate check sum of a <code>Patch</code>.<p>
     *
     * @param p a <code>Patch</code> value
     */
    protected void calculateChecksum(Patch p) {
        int sum = 0;
        for (int i = this.checksumStart; i <= this.checksumEnd; i++)
            sum += p.sysex[i];
        p.sysex[this.checksumOffset] = (byte) (sum & 0x7F);        
    }
    
    /**
     * Get the index where the patch starts in the banks SysEx data.
     */
    public static int getPatchStart(int patchNum) {                                                    
        return (MW2Constants.PURE_PATCH_SIZE * patchNum) + MW2Constants.SYSEX_HEADER_OFFSET;
    }
    
    /**
     * Get the name of the patch at the given number
     */
    public String getPatchName(Patch p, int patchNum) {
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
    public void setPatchName(Patch p, int patchNum, String name) {
        setPatchName(p.sysex, patchNum, name);
    }
    
    /** Set the name of the patch at the given number <code>patchNum</code>. */
    public void setPatchName(byte[] tempSysex, int patchNum, String name) {
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
    public void putPatch(Patch bank, Patch p, int patchNum) {
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
                MW2Constants.PURE_PATCH_SIZE);
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
                    MW2Constants.SYSEX_HEADER_OFFSET,
                    MW2Constants.PURE_PATCH_SIZE);
            Patch p = new Patch(sysex);
            WaldorfMW2SingleDriver.createPatchHeader(p);              
            WaldorfMW2SingleDriver.calculateChecksum(   p, 
                    MW2Constants.SYSEX_HEADER_OFFSET, 
                    MW2Constants.SYSEX_HEADER_OFFSET + MW2Constants.PURE_PATCH_SIZE, 
                    MW2Constants.SYSEX_HEADER_OFFSET + MW2Constants.PURE_PATCH_SIZE - 1);
            
            return p;
        } catch (Exception e) {
            ErrorMsg.reportError("Error", "Error in WaldorfMW2 Bank Driver", e);
            return null;
        }
    }
    
    public void requestPatchDump(int bankNum, int patchNum) {               
        send(sysexRequestDump.toSysexMessage(getDeviceID() ) );
    }
    
    public static void createPatchFooter(byte[] tempSysex) {
        if ( (MW2Constants.ALL_SOUNDS_SIZE - 1) <= tempSysex.length) {
            //tempSysex[MW2Constants.ALL_SOUNDS_SIZE - 2] = (byte) 0x00; // Checksum
            tempSysex[MW2Constants.ALL_SOUNDS_SIZE - 1] = MW2Constants.SYSEX_END_BYTE;
        }
    }
    
    /**
     * Creates a new bank with all 256 user sounds
     */
    public Patch createNewPatch() {
        byte[] sysex = new byte[this.patchSize];
        byte[] patchSysex  = new byte[MW2Constants.PATCH_SIZE];        
        
        for(int patchNo = 0; patchNo < patchNumbers.length; patchNo++) {                       
            System.arraycopy(   patchSysex,
                    MW2Constants.SYSEX_HEADER_OFFSET,
                    sysex,
                    getPatchStart(patchNo),
                    MW2Constants.PURE_PATCH_SIZE );
            setPatchName(sysex, patchNo, "New sound " + patchNo);
        }
                
        Patch p = new Patch(sysex);
        WaldorfMW2SingleDriver.createPatchHeader(p);
        WaldorfMW2SingleDriver.createPatchFooter(p);
        calculateChecksum(p);
        
        return p;
    }
    
    
}

