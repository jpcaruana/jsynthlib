/*
 * Copyright 2004 Fred Jan Kraan
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

/*
 * RolandMT32SingleDriver.java v.0.3
 * part of the Roland MT-32 driver
 */

package synthdrivers.RolandMT32;
import core.Driver;
import core.JSLFrame;
import core.Patch;
import core.SysexHandler;

/**
 * Single Voice Patch Driver for Roland MT32.
 *
 * @version $Id$
 */
public class RolandMT32SingleDriver extends Driver {
    /** Header Size */
    private static final int HSIZE = 10;
    /** Single Patch size */
    private static final int SSIZE = 245;
    String TTABaseAddress="04";
    String TTAPartAddress="00 00";
    private static final SysexHandler SYS_REQ = new SysexHandler(
        "F0 41 10 16 11 04 *partAddrM* *partAddrL* 00 01 76 *checkSum* F7");

    public RolandMT32SingleDriver() {
	super("Single", "Fred Jan Kraan");
	sysexID = "F041**16";
	patchSize	= HSIZE + SSIZE + 1;
	patchNameStart	= HSIZE - 2;
	patchNameSize	= 10;
	deviceIDoffset	= 2;
        checksumStart=5;
        checksumEnd=10;
        checksumOffset=0;
//	checksumStart	= HSIZE;
//	checksumEnd	= HSIZE + SSIZE - 2;
//	checksumOffset	= HSIZE + SSIZE - 1;
        bankNumbers =new String[] {"0-Internal","1-External"};
        patchNumbers=new String[] {"TTA-1","TTA-2","TTA-3","TTA-4","TTA-5","TTA-6","TTA-7","TTA-8"};  
    }

    // not used
    public void storePatch(Patch p, int bankNum, int patchNum) {
        System.out.println("storePatch: Not implemented yet.");

	setBankNum(bankNum);
	setPatchNum(patchNum);
	try {
	    Thread.sleep(100);
	} catch (Exception e) {
	}
	p.sysex[3] = (byte) 0x20;
	p.sysex[6] = (byte) (bankNum << 1);
	p.sysex[7] = (byte) (patchNum);
	sendPatchWorker(p);
	try {
	    Thread.sleep(100);
	} catch (Exception e) {
	}
	setPatchNum(patchNum);
    }

    // not used
    public void sendPatch(Patch p) {
	p.sysex[0] = (byte) 0xF0;
	p.sysex[1] = (byte) 0x41;
	p.sysex[2] = (byte) 0x10;
	p.sysex[3] = (byte) 0x16;
 	p.sysex[4] = (byte) 0x08;
	p.sysex[5] = (byte) 0x00;
	p.sysex[6] = (byte) 0x00;
	p.sysex[7] = (byte) 0x00;
	p.sysex[8] = (byte) 0x01;
	p.sysex[9] = (byte) 0x76;
        //this.calculateChecksum(p, 4, 253, 0);
 	p.sysex[255] = (byte) 0xF7;

        System.out.println("sendPatch: Not implemented yet.");
	sendPatchWorker(p);
    }

    // not used
    protected void calculateChecksum(Patch p, int start, int end, int ofs) {
	int sum = 0;
	for (int i = start; i <= end; i++) {
	    sum += p.sysex[i];
	}
	sum += 0xA5;
	p.sysex[ofs] = (byte) (sum % 128);
    }

    // not used
    public Patch createNewPatch() {
        byte[] sysex = new byte[HSIZE + SSIZE + 1];
	sysex[0] = (byte) 0xF0; sysex[1] = (byte) 0x41; sysex[2] = (byte) 0x10;
	sysex[3] = (byte) 0x16; sysex[4] = (byte) 0x12; sysex[5] = (byte) 0x04;
	sysex[6] = (byte) 0x0; sysex[HSIZE + SSIZE] = (byte) 0xF7;
	Patch p = new Patch(sysex, this);
	setPatchName(p, "New Patch");
	calculateChecksum(p);
	return p;
    }

    public JSLFrame editPatch(Patch p) {
	return new RolandMT32SingleEditor(p);
    }

    public void requestPatchDump(int bankNum, int timNum) {
      // timNum misuses patchNum
        SysexHandler.NameValue nv[]=new SysexHandler.NameValue[5];
        int timbreAddr = timNum * 0xF6;
        int timSizeH = 0x00;
        int timSizeM = 0x01;
        int timSizeL = 0x76;
        int timAddrH = 0x04;
        int timAddrM = timbreAddr / 0x80;
        int timAddrL = timbreAddr & 0x7F;
        nv[0]=new SysexHandler.NameValue("partAddrM", timAddrM);
        nv[1]=new SysexHandler.NameValue("partAddrL", timAddrL);
        int checkSum = ( 0 - (timAddrH + timAddrM + timAddrL + timSizeH + timSizeM + timSizeL)) & 0x7F;
        nv[2]=new SysexHandler.NameValue("checkSum",checkSum);
        nv[3]=new SysexHandler.NameValue("bankNum", bankNum << 1);
        nv[4]=new SysexHandler.NameValue("timNum", timNum );
        
        send(SYS_REQ.toSysexMessage(getChannel(),  nv));
    }
}
