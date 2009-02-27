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
 * (Rhytm) Setup Temp Driver for Roland MT32.
 *
 * @version $Id$
 */

package org.jsynthlib.drivers.roland.mt32;
import org.jsynthlib.core.Driver;
import org.jsynthlib.core.ErrorMsg;
import org.jsynthlib.core.JSLFrame;
import org.jsynthlib.core.Patch;
import org.jsynthlib.core.SysexHandler;


public class RolandMT32RhythmSetupTempDriver extends Driver {
//
    /** Header Size of the Data set DT1 message. */
// Skeleton definition of the message received when sending the request
    private static final int HSIZE = 0x05;
    /** Single Patch size */
    private static final int SSIZE = 0x08;
    
    /** Definition of the Request message RQ1 */
// Used to request the Rhythm setup data from the synth
// There is only one such setup, so the message is fixed. The checkSum 
// calculation is mandatory and done in requestPatchDump
    private static final SysexHandler SYS_REQ = new SysexHandler(
        "F0 41 10 16 11 03 01 10 00 00 04 *checkSum* F7");
//      |    Header    |  Addr  |  Size  |          |  |
    public RolandMT32RhythmSetupTempDriver() {
	    super("Rhythm Setup Temp", "Fred Jan Kraan");
	    sysexID         = "F041**16";
	    patchSize	    = HSIZE + SSIZE + 1;
	    patchNameStart	= 0;
	    patchNameSize	= 0;
	    deviceIDoffset	= 0;
        checksumStart   = 5;
        checksumEnd     = 10;
        checksumOffset  = 0;
        bankNumbers     = new String[] {""};
        patchNumbers    = new String[] {""};  
    }

     /* The message format here is Data set DT1 */
    public void storePatch(Patch p, int bankNum, int patchNum) {
//	    setBankNum(bankNum);    // Control change
//	    setPatchNum(patchNum);  // Program change
//	    try {
//	        Thread.sleep(100);
//	    } catch (Exception e) {
//            ErrorMsg.reportStatus(e);
//	    }

        p.sysex[0]   = (byte) 0xF0;
        p.sysex[5]   = (byte) 0x03;	// Point to setup Temp area
	    p.sysex[6]   = (byte) 0x01;
	    p.sysex[7]   = (byte) 0x10;

//         System.out.println  ("store patchNum " + patchNum + " to patAddrM/L " + patAddrM + " / " + patAddrL);
//	    sendPatchWorker(p);
//      send(p.sysex);
 	    calculateChecksum(p, HSIZE, HSIZE+SSIZE-2, HSIZE+SSIZE-1 );

	    try {
            sendPatchWorker(p);
	        Thread.sleep(100);
	    } catch (Exception e) {
            ErrorMsg.reportStatus(e);
	    }
//	    setPatchNum(patchNum);  // Program change
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
// Calculate the checksum 
	int sum = 0;
	for (int i = start; i <= end; i++) {
	    sum += p.sysex[i];
	}
	sum = (0 - sum) & 0x7F;
	p.sysex[ofs] = (byte) (sum % 128);
    }

    // not used
    public Patch createNewPatch() {
// This is the data placed in a newly created Rythm setup 
// The format is the same as a DT1, the checksum not filled in  
        byte[] sysex = new byte[HSIZE + SSIZE + 1];
	    sysex[0] = (byte) 0xF0; 
	    sysex[1] = (byte) 0x41; 
	    sysex[2] = (byte) 0x10;
	    sysex[3] = (byte) 0x16; 
	    sysex[4] = (byte) 0x12; 
        sysex[5] = (byte) 0x03; 
        sysex[6] = (byte) 0x01; 
        sysex[7] = (byte) 0x10; // address
        sysex[HSIZE + 0] = (byte) 0x41;   // Timbre Number: R1
        sysex[HSIZE + 1] = (byte) 0x50;   // Output Level:  80
        sysex[HSIZE + 2] = (byte) 0x07;   // Panpot:        middle
        sysex[HSIZE + 3] = (byte) 0x00;   // Reverb Switch: off
        sysex[HSIZE + SSIZE] = (byte) 0xF7;
	    Patch p = new Patch(sysex, this);
//	    setPatchName(p, "New Rhythm Setup");
	    calculateChecksum(p);
	    return p;
    }

    public JSLFrame editPatch(Patch p) {
// Calling the editor for edit. 
	    return new RolandMT32RhythmSetupTempEditor(p);
    }

    public void requestPatchDump(int bankNum, int patchNum) {
        // The message format here is Request RQ1
    	// All but the checksum is already set up in SYS_REQ
    	
        send(SYS_REQ.toSysexMessage(getChannel(),  
        		new SysexHandler.NameValue("checkSum", 0x68)
        ));
    }
}
