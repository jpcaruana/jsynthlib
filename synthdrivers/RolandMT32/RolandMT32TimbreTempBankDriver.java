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
 * Timbre temp Bank driver for Roland MT32.
 * @version $Id$
 */
package synthdrivers.RolandMT32;
import core.*;

import java.io.*;
import javax.swing.*;

import org.jsynthlib.core.BankDriver;
import org.jsynthlib.core.DriverUtil;
import org.jsynthlib.core.ErrorMsg;
import org.jsynthlib.core.Patch;
import org.jsynthlib.core.SysexHandler;


public class RolandMT32TimbreTempBankDriver extends BankDriver {
    /** Header Size */
    private static final int HSIZE = 8;
    /** Single Patch size */
    private static final int SSIZE = 247;
    /** the number of single patches in a bank patch. */
    private static final int NS = 8;

// The variables *bankNum* and *patchNum* are misused for addressing 
//  patch size and patch distance is both 01 76 (246) bytes
    private static final SysexHandler SYS_REQ = 
    	new SysexHandler("F0 41 10 16 11 04 *bankNum* *patchNum* 00 01 76 *checkSum* F7");

        public RolandMT32TimbreTempBankDriver() {
	    super("Timbre Temp Bank", "Fred Jan Kraan", NS, 4);

	    sysexID        = "F041**16";
	    deviceIDoffset = 0;
	    bankNumbers    = new String[] { ""	};
	    patchNumbers   = new String[2 * 4];
	    System.arraycopy(DriverUtil.generateNumbers(1, 8, "##"), 0, patchNumbers,  0, 8);

	    singleSysexID  = "F041**16";
	    singleSize     = HSIZE + SSIZE + 1;
	    // To distinguish from the Effect bank, which has the same sysexID
	    patchSize = HSIZE + SSIZE * NS + 1;
    }

    public int getPatchStart(int patchNum) {
	return HSIZE + (SSIZE * patchNum);
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
	sum += 0xA5;
	p.sysex[ofs] = (byte) (sum % 128);
	// p.sysex[ofs]=(byte)(p.sysex[ofs]^127);
	// p.sysex[ofs]=(byte)(p.sysex[ofs]+1);
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
		(null,
		 "This type of patch does not fit in to this type of bank.",
		 "Error", JOptionPane.ERROR_MESSAGE);
	    return;
	}

	System.arraycopy(p.sysex, HSIZE, bank.sysex, getPatchStart(patchNum), SSIZE);
	calculateChecksum(bank);
    }

    public Patch getPatch(Patch bank, int patchNum) {
    int addressMSB = 0x03;
    int addressISB = 0x00;
    int addressLSB = 0x00;
	byte[] sysex = new byte[HSIZE + SSIZE + 1];
	sysex[0] = (byte) 0xF0; 
	sysex[1] = (byte) 0x41; 
	sysex[2] = (byte) 0x10;
	sysex[3] = (byte) 0x16; 
	sysex[4] = (byte) 0x11;     // DT1
	sysex[5] = (byte) 0x04;     // address MSB
	sysex[6] = (byte) addressISB;     // address ISB
	sysex[7] = (byte) addressLSB;     // address ISB
	sysex[8] = (byte) patchNum; // address LSB
	sysex[9] = (byte) 0x00;     // size MSB
	sysex[10] = (byte) 0x01;     // size ISB
	sysex[11] = (byte) 0x76;    // size LSB
	sysex[12] = (byte) 0x04;    // checksum
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
	byte[] sysex = new byte[HSIZE + SSIZE * NS + 1];
	sysex[0] = (byte) 0xF0; 
	sysex[1] = (byte) 0x41; 
	sysex[2] = (byte) 0x10;
	sysex[3] = (byte) 0x16; 
	sysex[4] = (byte) 0x11;     // DT1
	sysex[5] = (byte) 0x04;     // address MSB
	sysex[6] = (byte) 0x00; 
	sysex[7] = (byte) 0x00;     // address LSB
	sysex[8] = (byte) 0x00;     // size MSB
	sysex[9] = (byte) 0x01;     //
	sysex[10] = (byte) 0x76;    // size LSB
	sysex[11] = (byte) 0x00;    // checksum
	sysex[HSIZE + SSIZE * NS] = (byte) 0xF7;
	Patch p = new Patch(sysex, this);
	for (int i = 0; i < NS; i++)
	    setPatchName(p, i, "New TT Patch");
	calculateChecksum(p);
	return p;
    }
  
    public void requestPatchDump(int bankNum, int patchNum) {
        send(SYS_REQ.toSysexMessage(getChannel(),
				    new SysexHandler.NameValue("bankNum", bankNum << 1)));
    }

    public void storePatch(Patch p, int bankNum, int patchNum) {
        try {
	    Thread.sleep(100);
	} catch (Exception e) {
	}
        p.sysex[3] = (byte) 0x16;
//        p.sysex[6] = (byte) (bankNum << 1);
        p.sysex[6] = (byte) bankNum;
        p.sysex[7] = (byte) patchNum;
        sendPatchWorker(p);
        try {
	    Thread.sleep(100);
	} catch (Exception e) {
	}
    }
}
