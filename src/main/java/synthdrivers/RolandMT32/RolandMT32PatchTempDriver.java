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
 * Patch Temp Driver for Roland MT32.
 *
 * @version $Id$
 */

package synthdrivers.RolandMT32;
import org.jsynthlib.core.Driver;
import org.jsynthlib.core.ErrorMsg;
import org.jsynthlib.core.JSLFrame;
import org.jsynthlib.core.Patch;
import org.jsynthlib.core.SysexHandler;

/**
 * Single Voice Patch Driver for Roland MT32.
 *
 * @version $Id$
 */
public class RolandMT32PatchTempDriver extends Driver {
    /** Header Size of the Data set DT1 message. */
    private static final int HSIZE = 5;
    /** Single Patch size */
    private static final int SSIZE = 0x14;
    /** Definition of the Request message RQ1 */
    private static final SysexHandler SYS_REQ = new SysexHandler(
        "F0 41 10 16 11 03 *partAddrM* *partAddrL* 00 00 10 *checkSum* F7");

    public RolandMT32PatchTempDriver() {
	    super("Patch Temp", "Fred Jan Kraan");
	    sysexID = "F041**16";
	    patchSize = HSIZE + SSIZE + 1;
	    patchNameStart = 0;
	    patchNameSize  = 0;
	    deviceIDoffset = 0;
        checksumStart  = 5;
        checksumEnd    = 10;
        checksumOffset = 0;
        bankNumbers  = new String[] {""};
        patchNumbers = new String[] {"PTA-1","PTA-2","PTA-3","PTA-4",
        		                     "PTA-5","PTA-6","PTA-7","PTA-8"};  
    }

    /* Send a patch (bulk dump system exclusive message) to MIDI device. 
     * Target should be Timbre Memory 1 - 64. TTA will do for now. 
     * The message format here is Data set DT1 */
    public void storePatch(Patch p, int bankNum, int patchNum) {
	    setBankNum(bankNum);    // Control change
	    setPatchNum(patchNum);  // Program change
	    try {
	        Thread.sleep(100);
	    } catch (Exception e) {
            ErrorMsg.reportStatus(e);
	    }

//	    int timbreAddr = patchNum * (SSIZE - 1);
	    int patchAddr  = patchNum * 0x10;
	    int patAddrM   = patchAddr / 0x80;
	    int patAddrL   = patchAddr & 0x7F;	

        p.sysex[0]   = (byte) 0xF0;
//	    p.sysex[2]   = (byte) 0x10;
        p.sysex[5]   = (byte) 0x03;     // point to Patch Temp Area
	    p.sysex[6]   = (byte) patAddrM;
	    p.sysex[7]   = (byte) patAddrL;

	    calculateChecksum(p, HSIZE, HSIZE+SSIZE-2, HSIZE+SSIZE-1 );

        ErrorMsg.reportStatus ("Store patchNum " + patchNum + " to patAddrM/L " + patAddrM + " / " + patAddrL);
//	    sendPatchWorker(p);
//      send(p.sysex);
	    try {
            sendPatchWorker(p);
	        Thread.sleep(100);
	    } catch (Exception e) {
            ErrorMsg.reportStatus(e);
	    }
	    setPatchNum(patchNum);  // Program change
    }

    /*  Send a Patch (bulk dump system exclusive message) to an edit
     *  buffer of MIDI device. Target should be Timbre Temp Area 1 - 8. 
     *  The message format here is Data set DT1 */
    public void sendPatch(Patch p) {
//        int patchNum = (HSIZE + SSIZE + 1);
//	p.sysex[0] = (byte) 0xF0;
//	p.sysex[1] = (byte) 0x41;
//	p.sysex[2] = (byte) 0x10;
//	p.sysex[3] = (byte) 0x16;
// 	p.sysex[4] = (byte) 0x08;
//	p.sysex[5] = (byte) 0x00;
//	p.sysex[6] = (byte) 0x00;
// 	p.sysex[4] = (byte) 0x04;
//	p.sysex[5] = (byte) (byte)(patchNum / 0x80);
//	p.sysex[6] = (byte) (byte)(patchNum & 0x7F);

        //this.calculateChecksum(p, 4, 253, 0);
// 	p.sysex[255] = (byte) 0xF7;

//        System.out.println("sendPatch: Not implemented yet.");
	sendPatchWorker(p);
    }

    // not used
    protected void calculateChecksum(Patch p, int start, int end, int ofs) {
	int sum = 0;
	for (int i = start; i <= end; i++) {
	    sum += p.sysex[i];
	}
    sum = (0 - sum) & 0x7F;
	p.sysex[ofs] = (byte) (sum % 128);
    }

    // New Patch has Data set format DT1
    public Patch createNewPatch() {
        byte[] sysex = new byte[HSIZE + SSIZE + 1];
	    sysex[0] = (byte) 0xF0; sysex[1] = (byte) 0x41; sysex[2] = (byte) 0x10;
	    sysex[3] = (byte) 0x16; sysex[4] = (byte) 0x12; sysex[5] = (byte) 0x03;
	    sysex[6] = (byte) 0x0; sysex[HSIZE + SSIZE] = (byte) 0xF7;
	    Patch p = new Patch(sysex, this);
//	    setPatchName(p, "New PatchT");
	    calculateChecksum(p);
	    return p;
    }

    public JSLFrame editPatch(Patch p) {
	    return new RolandMT32PatchTempEditor(p);
    }

    public void requestPatchDump(int bankNum, int patNum) {
        // The message format here is Request RQ1
        int patchAddr = patNum * 0x10;
        int patAddrH = 0x03;               // Always Patch Temp Area
        int patAddrM = patchAddr / 0x80;  // patchAddr >> 8
        int patAddrL = patchAddr & 0x7F;
        int patSizeH = 0x00;
        int patSizeM = 0x00;
        int patSizeL = 0x10;
        int checkSum = ( 0 - (patAddrH + patAddrM + patAddrL + patSizeH + patSizeM + patSizeL)) & 0x7F;
        SysexHandler.NameValue nVs[] = new SysexHandler.NameValue[3];
        nVs[0] = new SysexHandler.NameValue("partAddrM", patAddrM);
        nVs[1] = new SysexHandler.NameValue("partAddrL", patAddrL);
        nVs[2] = new SysexHandler.NameValue("checkSum",checkSum);

        send(SYS_REQ.toSysexMessage(getChannel(),  nVs));
    }
}
