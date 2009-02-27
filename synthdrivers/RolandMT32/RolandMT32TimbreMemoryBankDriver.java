/*
 * Copyright 2004,2005 Fred Jan Kraan
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
/**
 * Timbre Memory Bank driver for Roland MT32.
 * @version $Id$
 */

package synthdrivers.RolandMT32;

import java.io.UnsupportedEncodingException;

import javax.swing.JOptionPane;

import core.BankDriver;
import core.DriverUtil;
import core.ErrorMsg;
import core.Patch;
import core.SysexHandler;


public class RolandMT32TimbreMemoryBankDriver extends BankDriver {
    /** Header Size */
    private static final int HSIZE = 8;
    /** Single Patch size */
    private static final int SSIZE = 247;
    /** the number of single patches in a bank patch. */
    private static final int NS = 64;
    /** Time to sleep when doing sysex data transfers. */
    private static final int sleepTime = 100;


// Patch sice is 01 76 (246) bytes, patch distance is 02 00 (256) bytes
    private static final SysexHandler SYS_REQ = 
    	new SysexHandler("F0 41 10 16 11 08 *partAddrM* *partAddrL* 00 01 76 *checkSum* F7");

    public RolandMT32TimbreMemoryBankDriver() {
	    super("Timbre Memory Bank", "Fred Jan Kraan", NS, 4);

	    sysexID = "F041**16";
	    deviceIDoffset = 0;
	    bankNumbers = new String[] {
	        ""
	    };
	    patchNumbers = new String[16 * 4];
	    System.arraycopy(DriverUtil.generateNumbers(1, 64, "##"), 0, patchNumbers,  0, 64);

	    singleSysexID = "F041**16";
	    singleSize = HSIZE + SSIZE + 1;
	    // To distinguish from the Effect bank, which has the same sysexID
	    patchSize = (HSIZE + SSIZE) * NS + 1;
    }

    public int getPatchStart(int patchNum) {
	    return ((HSIZE + SSIZE) * patchNum);
    }

    public String getPatchName(Patch p, int patchNum) {
	    int nameStart = getPatchStart(patchNum);
	    nameStart += 0; //offset of name in patch data
	    try {
	        StringBuffer s = new StringBuffer(new String(p.sysex, nameStart,
							 10, "US-ASCII"));
	        return s.toString();
	    } catch (UnsupportedEncodingException ex) {
	    return "-";
	    }
    }

    public void setPatchName(Patch p, int patchNum, String name) {
	    patchNameSize = 10;
	    patchNameStart = getPatchStart(patchNum);

	    if (name.length() < patchNameSize)
	        name = name + "            ";
	        byte[] namebytes = new byte[64];
	    try {
	        namebytes = name.getBytes("US-ASCII");
	        for (int i = 0; i < patchNameSize; i++)
		    p.sysex[patchNameStart + i] = namebytes[i];

        } catch (UnsupportedEncodingException ex) {
	        return;
	    }
    }

    protected void calculateChecksum(Patch p, int start, int end, int ofs)  {
    	int i;
	    int sum = 0;

	    for (i = start; i <= end; i++)
	        sum += p.sysex[i];
	    sum = (0 - sum) & 0x7F;
	    p.sysex[ofs] = (byte) (sum % 128);
    }

    public void calculateChecksum(Patch p) {
	    for (int i = 0; i < NS; i++)
	        calculateChecksum(p, HSIZE + (i * SSIZE),
			      HSIZE + (i * SSIZE) + SSIZE - 2,
			      HSIZE + (i * SSIZE) + SSIZE - 1);
    }

    public void putPatch(Patch bank, Patch p, int patchNum) {
	    if (!canHoldPatch(p)) {
	        JOptionPane.showMessageDialog
		    (null, "This type of patch does not fit in to this type of bank.",
		         "Error", JOptionPane.ERROR_MESSAGE);
	        return;
	    }
	    System.arraycopy(p.sysex, HSIZE, bank.sysex, getPatchStart(patchNum), SSIZE);
	    calculateChecksum(bank);
    }

    public Patch getPatch(Patch bank, int patchNum) {
        int addressMSB = 0x08;
        int addressISB = 0x00;
        int addressLSB = 0x00;
        byte[] sysex = new byte[HSIZE + SSIZE + 1];
        sysex[0] = (byte) 0xF0; 
        sysex[1] = (byte) 0x41; 
        sysex[2] = (byte) 0x10;
        sysex[3] = (byte) 0x16; 
        sysex[4] = (byte) 0x11;     // DT1
        sysex[5] = (byte) addressMSB;     // address MSB
        sysex[6] = (byte) addressISB;     // address ISB
        sysex[7] = (byte) addressLSB;     // address ISB
//	    sysex[8] = (byte) patchNum; // address LSB
        sysex[8] = (byte) 0x00;     // size MSB
        sysex[9] = (byte) 0x01;     // size ISB
        sysex[10] = (byte) 0x76;    // size LSB
        sysex[11] = (byte) 0x04;    // checksum
        sysex[HSIZE + SSIZE] = (byte) 0xF7;
        System.arraycopy(bank.sysex, getPatchStart(patchNum), sysex, HSIZE, SSIZE);
        try {
	        // pass Single Driver !!!FIXIT!!!
	        Patch p = new Patch(sysex, getDevice());
	        p.calculateChecksum();
	        return p;
	    } catch (Exception e) {
	        ErrorMsg.reportError("Error", "Error in MT32 Bank Driver", e);
	        return null;
	    }
    }

    public Patch createNewPatch() {
	    byte[] sysex = new byte[(HSIZE + SSIZE) * NS + 1];
        sysex[0] = (byte) 0xF0; 
        sysex[1] = (byte) 0x41; 
        sysex[2] = (byte) 0x10;
        sysex[3] = (byte) 0x16; 
        sysex[4] = (byte) 0x11;     // DT1
        sysex[5] = (byte) 0x08;     // address MSB
        sysex[6] = (byte) 0x00;     // address ISB
        sysex[7] = (byte) 0x00;     // address LSB
        sysex[8] = (byte) 0x00;     // size MSB
        sysex[9] = (byte) 0x01;     //
        sysex[10] = (byte) 0x76;    // size LSB
        sysex[11] = (byte) 0x00;    // checksum
        sysex[(HSIZE + SSIZE) * NS] = (byte) 0xF7;
        Patch p = new Patch(sysex, this);
        for (int i = 0; i < NS; i++)
	        setPatchName(p, i, "New TM Patch");
        calculateChecksum(p);
        return p;
    }
  
    public void requestPatchDump(int bankNum, int patchNum) {
    	for (int i=0;i<NS;i++) {
    		requestSingleTimbreMemoryDump(bankNum,i);
    	}
    }    
    
    public void requestSingleTimbreMemoryDump(int bankNum,int timNum) {
        int timbreAddr = timNum * 0x100; //patch distance is greater than size;
        int timAddrH = 0x08;               // Always Timbre Memory Area
        int timAddrM = timbreAddr / 0x80;  // timbreAddr >> 8
        int timAddrL = timbreAddr & 0x7F;
        int timSizeH = 0x00;
        int timSizeM = 0x01;
        int timSizeL = 0x76;
        int checkSum = ( 0 - (timAddrH + timAddrM + timAddrL + timSizeH + timSizeM + timSizeL)) & 0x7F;
        SysexHandler.NameValue nVs[] = new SysexHandler.NameValue[3];
        nVs[0] = new SysexHandler.NameValue("partAddrM", timAddrM);
        nVs[1] = new SysexHandler.NameValue("partAddrL", timAddrL);
        nVs[2] = new SysexHandler.NameValue("checkSum",  checkSum);

        send(SYS_REQ.toSysexMessage(getChannel(),  nVs));
    	try {
    		Thread.sleep(sleepTime);
    	} catch (Exception e){}
    }
    

    public void storePatch(Patch p, int bankNum, int patchNum) {
        try {
	    Thread.sleep(100);
	    } catch (Exception e) {}
        p.sysex[5] = (byte) 0x08;            // Address MSB Timbre Memory Region
        p.sysex[6] = (byte) (patchNum << 1); // Address ISB 
        p.sysex[7] = (byte) 0x00;
        sendPatchWorker(p);
        try {
	        Thread.sleep(sleepTime);
	    } catch (Exception e) {}
    }
}
