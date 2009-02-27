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
 * Timbre Memory Driver for Roland MT32.
 *
 * @version $Id$
 */

package synthdrivers.RolandMT32;

import core.Driver;
import core.ErrorMsg;
import core.JSLFrame;
import core.Patch;
import core.SysexHandler;


public class RolandMT32TimbreMemoryDriver extends Driver {
    /** Header Size of the Data set DT1 message. */
    private static final int HSIZE = 8;
    /** Single Patch size */
    private static final int SSIZE = 247;
    /** Definition of the Request message RQ1 */
    private static final SysexHandler SYS_REQ = new SysexHandler(
        "F0 41 10 16 11 08 *partAddrM* *partAddrL* 00 01 76 *checkSum* F7");

    public RolandMT32TimbreMemoryDriver() {
	    super("Timbre Memory", "Fred Jan Kraan");
	    sysexID        = "F041**16";
	    patchSize      = HSIZE + SSIZE + 1;
//	    patchSize      = 0xFF; // For Timbre Memory the patch distance is greater than the size. 
	    patchNameStart = HSIZE;
	    patchNameSize  = 10;
	    deviceIDoffset = 0;
        checksumStart  = 5;
        checksumEnd    = 10;
        checksumOffset = 0;
        bankNumbers    = new String[] {"Memory"};
        patchNumbers = new String[] {"TM-1", "TM-2", "TM-3", "TM-4", "TM-5",
        		                     "TM-6", "TM-7", "TM-8", "TM-9", "TM-10",
        		                     "TM-11","TM-12","TM-13","TM-14","TM-15",
        		                     "TM-16","TM-17","TM-18","TM-19","TM-20",
        		                     "TM-21","TM-22","TM-23","TM-24","TM-25",
        		                     "TM-26","TM-27","TM-28","TM-29","TM-30",
        		                     "TM-31","TM-32","TM-33","TM-34","TM-35",
        		                     "TM-36","TM-37","TM-38","TM-39","TM-40",
        		                     "TM-41","TM-42","TM-43","TM-44","TM-45",
        		                     "TM-46","TM-47","TM-48","TM-49","TM-50",
        		                     "TM-51","TM-52","TM-53","TM-54","TM-55",
        		                     "TM-56","TM-57","TM-58","TM-59","TM-60",
        		                     "TM-61","TM-62","TM-63","TM-64"
        }; 
    } 

    /* Send a patch (bulk dump system exclusive message) to MIDI device. 
     * The message format here is Data set DT1 */
    public void storePatch(Patch p, int bankNum, int patchNum) {
	    setBankNum(bankNum);    // Control change
	    setPatchNum(patchNum);  // Program change
	    try {
	        Thread.sleep(100);
	    } catch (Exception e) {
            ErrorMsg.reportStatus(e);
	    }

	    int timbreAddr = patchNum * 0x100;
	    int timAddrM = (timbreAddr / 0x80) & 0x7F;
	    int timAddrL = timbreAddr & 0x7F;	
        p.sysex[0]   = (byte) 0xF0;

        p.sysex[5]   = (byte) 0x08;     // point to Timbre Memory
	    p.sysex[6]   = (byte) timAddrM;
	    p.sysex[7]   = (byte) timAddrL;
	    calculateChecksum(p, 5, HSIZE+SSIZE-2, HSIZE+SSIZE-1 );
	    
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

        p.sysex[0]   = (byte) 0xF0;
 	    p.sysex[5]   = (byte) 0x08;

	    calculateChecksum(p, 5, HSIZE+SSIZE-2, HSIZE+SSIZE-1 );
	    sendPatchWorker(p);
    }

    protected void calculateChecksum(Patch p, int start, int end, int ofs) {
	    int sum = 0;
	    for (int i = start; i <= end; i++) {
	        sum += p.sysex[i];
	    }
	    sum = (0 - sum) & 0x7F;
	    p.sysex[ofs] = (byte) (sum % 128);
    }
    
    // not used
    public Patch createNewPatch() {
        byte[] sysex = new byte[HSIZE + SSIZE + 1];
	    sysex[0] = (byte) 0xF0; 
	    sysex[1] = (byte) 0x41; 
	    sysex[2] = (byte) 0x10;
	    sysex[3] = (byte) 0x16; 
	    sysex[4] = (byte) 0x12; 
	    sysex[5] = (byte) 0x08;
	    sysex[6] = (byte) 0x00; 
	    sysex[HSIZE + SSIZE] = (byte) 0xF7;
	    Patch p = new Patch(sysex, this);
	    setPatchName(p, "New Timbre");
	    calculateChecksum(p, 5, HSIZE+SSIZE-2, HSIZE+SSIZE-1 );
	    return p;
    }

    public JSLFrame editPatch(Patch p) {
	    return new RolandMT32TimbreTempEditor(p);
    }

    public void requestPatchDump(int bankNum, int timNum) {
      // timNum misuses patchNum
        // The message format here is Request RQ1
        int timbreAddr = timNum * 0x100; //patch distance is greater than size;
        int timAddrH = 0x08;               // Always Timbre Temp Area
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
    }
}
