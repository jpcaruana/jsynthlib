/*
 * Copyright 2006 Robert Wirski
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

package org.jsynthlib.drivers.roland.jd800;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.swing.JOptionPane;

import org.jsynthlib.core.BankDriver;
import org.jsynthlib.core.DriverUtil;
import org.jsynthlib.core.ErrorMsg;
import org.jsynthlib.core.IPatch;
import org.jsynthlib.core.Patch;
import org.jsynthlib.core.SysexHandler;

/**
 * Patch Memory Area (bank) driver for Roland JD800. JD800 operates on memory areas
 * instead of a patch concept. The entire area size is 24576 bytes. The maximum
 * amount of data in one sysex message in 256 bytes so it needs to create 96 sysex 
 * messages to send a bank. To simplify its operation the driver needs to concatenate
 * the sysex messages into a one single message. 
 */
public class RolandJD800BankDriver extends BankDriver {

    private static final int patchNameSize = 16;
    
    private static final SysexHandler SYS_REQ = new SysexHandler("F0 41 @@ 3D 11 05 00 00 01 40 00 00 F7");

    public RolandJD800BankDriver() {
        super("Patch (Bank)Memory Area", "Robert Wirski", JD800.nrOfPatchesInABank, 4);

	sysexID = "F041**3D12050000";
	singleSize = 0;
	deviceIDoffset = 2;
	bankNumbers = new String[] {"Internal"};
        patchNumbers = new String[JD800.nrOfPatchesInABank];
        System.arraycopy(DriverUtil.generateNumbers(1, 8, "Patch I-10"), 0, patchNumbers, 0, 8);
        System.arraycopy(DriverUtil.generateNumbers(1, 8, "Patch I-20"), 0, patchNumbers, 8, 8);
        System.arraycopy(DriverUtil.generateNumbers(1, 8, "Patch I-30"), 0, patchNumbers, 16, 8);
        System.arraycopy(DriverUtil.generateNumbers(1, 8, "Patch I-40"), 0, patchNumbers, 24, 8);
        System.arraycopy(DriverUtil.generateNumbers(1, 8, "Patch I-50"), 0, patchNumbers, 32, 8);
        System.arraycopy(DriverUtil.generateNumbers(1, 8, "Patch I-60"), 0, patchNumbers, 40, 8);
        System.arraycopy(DriverUtil.generateNumbers(1, 8, "Patch I-70"), 0, patchNumbers, 48, 8);
        System.arraycopy(DriverUtil.generateNumbers(1, 8, "Patch I-80"), 0, patchNumbers, 56, 8);

	singleSysexID = "F041**3D12";
	patchSize = 25536;
    }

    /**
     * Gets the offset where the patch starts in the bank.
     * @param patchNum number of patch requested
     */
    public int getPatchStart(int patchNum) {
	return JD800.SizeOfSyxHeader + (JD800.SizeOfSinglePatch * patchNum);
    }

    /**
     * Gets the name of the patch at the given number.
     */
     public String getPatchName(Patch p, int patchNum) {
	int nameStart = getPatchStart(patchNum);
	try {
	    StringBuffer s = new StringBuffer(new String(p.sysex, nameStart,
                    patchNameSize, "US-ASCII"));
	    return s.toString();
	} catch (UnsupportedEncodingException ex) {
	    return "-";
	}
    }

     /** Sets the name of the patch at the given number <code>patchNum</code>. */
    public void setPatchName(Patch p, int patchNum, String name) {
	
	patchNameStart = getPatchStart(patchNum);

	if (name.length() < patchNameSize)
	    name = name + "                ";
	byte[] namebytes = new byte[JD800.nrOfPatchesInABank];
	try {
	    namebytes = name.getBytes("US-ASCII");
	    for (int i = 0; i < patchNameSize; i++)
		p.sysex[patchNameStart + i] = namebytes[i];

        } catch (UnsupportedEncodingException ex) {
	    return;
	}
    }

    /**
     * Gets a single patch at the given number <code>patchNum</code> from a bank.
     */
    public void putPatch(Patch bank, Patch p, int patchNum) {
	if (!canHoldPatch(p)) {
	    JOptionPane.showMessageDialog
		(null,
		 "This type of patch does not fit in to this type of bank.",
		 "Error", JOptionPane.ERROR_MESSAGE);
	    return;
	}
	System.arraycopy(p.sysex,
                JD800.SizeOfSyxHeader,
                bank.sysex,
                getPatchStart(patchNum),
                JD800.SizeOfSinglePatch);
    }
    
    /**
     * Compares the header & size of a Patch to this driver to see if
     * this driver is the correct one to support the patch. It is
     * overrided as it needs to serve for both sysex message sizes: original 96
     * sysex messages and single concatenated one.
     * @see org.jsynthlib.core.Driver#supportsPatch(String, byte[])
     */
    public boolean supportsPatch(String patchString, byte[] sysex) {
        // The statement below has been changed when compared to original. The rest is the same.
        if (((JD800.SizeOfPatchSyx1 * JD800.nrOfSyxForABank) != sysex.length) &
                ((JD800.SizeOfSyxHeader + JD800.SizeOfSinglePatch*JD800.nrOfPatchesInABank) != sysex.length))
            return false;

        if (sysexID == null || patchString.length() < sysexID.length())
            return false;

        StringBuffer compareString = new StringBuffer();
        for (int i = 0; i < sysexID.length(); i++) {
            switch (sysexID.charAt(i)) {
            case '*':
                compareString.append(patchString.charAt(i));
                break;
            default:
                compareString.append(sysexID.charAt(i));
            }
        }
        return (compareString.toString().equalsIgnoreCase
                (patchString.substring(0, sysexID.length())));
    }
 
    /**
     * Returns one concatenated message created from 96 sysex messages.
     */
    public IPatch createPatch(byte[] sysex) {
        byte[] out = 
            new byte[JD800.SizeOfSinglePatch * JD800.nrOfPatchesInABank + JD800.SizeOfSyxHeader];
        System.arraycopy(sysex, 0, out, 0, JD800.SizeOfSyxHeader);   
        for (int i = 0; i < JD800.nrOfSyxForABank; i++ )
            System.arraycopy(sysex,
                    JD800.SizeOfSyxHeader + i * JD800.SizeOfPatchSyx1,
                    out,
                    JD800.SizeOfSyxHeader + i * JD800.MaxSyxDataBlock,
                    JD800.MaxSyxDataBlock);
        return new Patch(out, this);
    };
 
    /**
     * Returns a single patch from <code>bank</code> at the given number
     * <code>patchNum</code>. 
     */
    public Patch getPatch(Patch bank, int patchNum) {
        byte[] sysex = new byte[JD800.SizeOfSyxHeader + JD800.SizeOfSinglePatch];
        sysex[0] = (byte) 0xF0;
        sysex[1] = (byte) 0x41;
        sysex[2] = (byte) getDeviceID();
        sysex[3] = (byte) 0x3D;
        sysex[4] = (byte) 0x12;
        sysex[5] = (byte) RolandJD800SinglePatchDriver.AddrMSB[patchNum];
        sysex[6] = (byte) RolandJD800SinglePatchDriver.Addr[patchNum];
        sysex[7] = 0;
        System.arraycopy(bank.sysex,
                getPatchStart(patchNum),
                sysex,
                JD800.SizeOfSyxHeader,
                JD800.SizeOfSinglePatch);
        return new Patch(sysex, getDevice());
    }

    /**
     * Creates a new bank from initial patch in JD800_InitialPatch.syx
     */
    public Patch createNewPatch() {
        byte[] sysex = new byte[JD800.SizeOfSyxHeader + JD800.SizeOfSinglePatch * JD800.nrOfPatchesInABank];
        byte [] buffer =new byte [JD800.SizeOfPatchSyx1 + JD800.SizeOfPatchSyx2];
        try {
            InputStream fileIn= getClass().getResourceAsStream("JD800_InitialPatch.syx");
            fileIn.read(buffer);
            fileIn.close();
          } catch (Exception e) {ErrorMsg.reportError("Error","Unable to find JD800_InitialPatch.syx",e);return null;}
          sysex[0] = (byte) 0xF0;
          sysex[1] = (byte) 0x41;
          sysex[2] = (byte) getDeviceID();
          sysex[3] = (byte) 0x3D;
          sysex[4] = (byte) 0x12;
          sysex[5] = (byte) 0x05;
          sysex[6] = (byte) 0x00;
          sysex[7] = (byte) 0x00;
          for (int i=0; i < JD800.nrOfPatchesInABank; i++) {
              System.arraycopy(buffer,
                      JD800.SizeOfSyxHeader,
                      sysex,
                      JD800.SizeOfSyxHeader + i * JD800.SizeOfSinglePatch,
                      JD800.MaxSyxDataBlock);
              System.arraycopy(buffer,
                      JD800.SizeOfSyxHeader + JD800.SizeOfPatchSyx1,
                      sysex,
                      JD800.SizeOfSyxHeader + JD800.MaxSyxDataBlock + i * JD800.SizeOfSinglePatch,
                      JD800.SizeOfSinglePatch - JD800.MaxSyxDataBlock);
          };              
          return new Patch(sysex, this);
    }

    public void requestPatchDump(int bankNum, int patchNum) {
        send(SYS_REQ.toSysexMessage(getDeviceID()));
    }

    /**
     * Stores the bank to JD800. Ignores the parameters.
     * It splits the data into 96 sysex messages.
     */
     public void storePatch(Patch p, int bankNum, int patchNum) {
        byte[] s = new byte[JD800.SizeOfPatchSyx1];
        int AddrMSB, Addr;
        s[0] = (byte) 0xF0;
        s[1] = (byte) 0x41;
        s[2] = (byte) getDeviceID();
        s[3] = (byte) 0x3D;
        s[4] = (byte) 0x12;
        s[265] = (byte) 0xF7;
        AddrMSB = 0x05;
        Addr = 0x00;
        for(int i = 0; i<JD800.nrOfSyxForABank; i++) {
            s[5] = (byte) AddrMSB; 
            s[6] = (byte) Addr;
            s[7] = (byte) 0x00;
            System.arraycopy(p.sysex,
                    JD800.SizeOfSyxHeader + i * JD800.MaxSyxDataBlock,
                    s,
                    JD800.SizeOfSyxHeader,
                    JD800.MaxSyxDataBlock);
            DriverUtil.calculateChecksum(s,
                    JD800.checksumStartSyx1,
                    JD800.checksumEndSyx1,
                    JD800.checksumOffsetSyx1);
            sendPatchWorker(new Patch(s, this));
            try { 
                Thread.sleep(25);
            } catch (Exception e) {}
            if (Addr == 0x7E) {
                AddrMSB++;
                Addr = 0x00;}
            else
                Addr += 0x02;
        }
    }
}
