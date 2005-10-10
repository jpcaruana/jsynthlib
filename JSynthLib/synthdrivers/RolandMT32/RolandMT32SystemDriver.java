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
 * System Driver for Roland MT32.
 *
 * @version $Id$
 */

package synthdrivers.RolandMT32;
import core.Driver;
import core.JSLFrame;
import core.Patch;
import core.SysexHandler;
import core.ErrorMsg;

public class RolandMT32SystemDriver extends Driver {
    /** Header Size of the Data set DT1 message. */
	//  F0 41 10 16 12 10 00 00 .. .. .. *chkSum* F7
	// |              | Addr   | Msg    |
    private static final int HSIZE = 5;
    /** Single Patch size */
    private static final int SSIZE = 3 + 0x17 + 1;
    /** Definition of the Request message RQ1 */
    private static final SysexHandler SYS_REQ = new SysexHandler(
        "F0 41 10 16 11 10 00 00 00 00 17 *checkSum* F7");

    public RolandMT32SystemDriver() {
	    super("System", "Fred Jan Kraan");
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

    /* Send a patch (bulk dump system exclusive message) to MIDI device. 
     * Target should be Timbre Memory 1 - 64. TTA will do for now. 
     * The message format here is Data set DT1 */
    public void storePatch(Patch p, int bankNum, int patchNum) {
//	    setBankNum(bankNum);    // Control change
//	    setPatchNum(patchNum);  // Program change
	    try {
	        Thread.sleep(100);
	    } catch (Exception e) {
            ErrorMsg.reportStatus(e);
	    }

        p.sysex[0]   = (byte) 0xF0;
        p.sysex[5]   = (byte) 0x10;     // point to System Area
	    p.sysex[6]   = (byte) 0x00;
	    p.sysex[7]   = (byte) 0x00;

	    calculateChecksum(p, HSIZE, HSIZE+SSIZE-2, HSIZE+SSIZE-1 );

//        System.out.println ("store System");
//	    sendPatchWorker(p);
//      send(p.sysex);
	    try {
            sendPatchWorker(p);
	        Thread.sleep(100);
	    } catch (Exception e) {
            ErrorMsg.reportStatus(e);
	    }
//	setPatchNum(patchNum);  // Program change
    }

    /*  Send a Patch (bulk dump system exclusive message) to an edit
     *  buffer of MIDI device. Target should be Timbre Temp Area 1 - 8. 
     *  The message format here is Data set DT1 */
    public void sendPatch(Patch p) {
	    try {
            sendPatchWorker(p);
	        Thread.sleep(100);
	    } catch (Exception e) {
            ErrorMsg.reportStatus(e);
	    }
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

    // not used
    public Patch createNewPatch() {
        // The message format here is DT1 Data Set
        byte[] sysex = new byte[HSIZE + SSIZE + 1];
	    sysex[0] = (byte) 0xF0; 
	    sysex[1] = (byte) 0x41; 
	    sysex[2] = (byte) 0x10;
	    sysex[3] = (byte) 0x16; 
	    sysex[4] = (byte) 0x12; 
	    sysex[5] = (byte) 0x10; // Address MSB
	    sysex[6] = (byte) 0x00; // Address ISB
	    sysex[7] = (byte) 0x00; // Address LSB
	    // System
	    sysex[HSIZE + 0] = (byte) 0x28;  // Master tune A = 440.1 Hz
	    sysex[HSIZE + 1] = (byte) 0x00;  // Reverb mode 
	    sysex[HSIZE + 2] = (byte) 0x05;  // Reverb time 
	    sysex[HSIZE + 3] = (byte) 0x03;  // Reverb level 
	    sysex[HSIZE + 22] = (byte) 0x3F; // Master volume 
	    // Partial reserve
	    sysex[HSIZE + 4] = (byte) 0x03;  // Part 1
	    sysex[HSIZE + 5] = (byte) 0x0A;  // Part 2
	    sysex[HSIZE + 6] = (byte) 0x06;  // Part 3
	    sysex[HSIZE + 7] = (byte) 0x04;  // Part 4
	    sysex[HSIZE + 8] = (byte) 0x03;  // Part 5
	    sysex[HSIZE + 9] = (byte) 0x00;  // Part 6
	    sysex[HSIZE + 10] = (byte) 0x00; // Part 7
	    sysex[HSIZE + 11] = (byte) 0x00; // Part 8
	    sysex[HSIZE + 12] = (byte) 0x06; // Part R
	    // Midi channel
	    sysex[HSIZE + 13] = (byte) 0x01; // Midi Channel 2
	    sysex[HSIZE + 14] = (byte) 0x02; // Midi Channel 3
	    sysex[HSIZE + 15] = (byte) 0x03; // Midi Channel 4
	    sysex[HSIZE + 16] = (byte) 0x04; // Midi Channel 5
	    sysex[HSIZE + 17] = (byte) 0x05; // Midi Channel 6
	    sysex[HSIZE + 18] = (byte) 0x06; // Midi Channel 7
	    sysex[HSIZE + 19] = (byte) 0x07; // Midi Channel 8
	    sysex[HSIZE + 20] = (byte) 0x08; // Midi Channel 9
	    sysex[HSIZE + 21] = (byte) 0x09; // Midi Channel 10
	    sysex[HSIZE + SSIZE] = (byte) 0xF7;
	    Patch p = new Patch(sysex, this);
//	    setPatchName(p, "System");
//	    calculateChecksum(p);
	    calculateChecksum(p, 5, HSIZE+SSIZE-2, HSIZE+SSIZE-1 );
//	    System.out.println ("Checksumcalc " + 5 + " to " + (HSIZE+SSIZE-2) + " into " + (HSIZE+SSIZE-1));
	    return p;
    }

    public JSLFrame editPatch(Patch p) {
	    return new RolandMT32SystemEditor(p);
    }

    public void requestPatchDump(int bankNum, int patchNum) {
        // The message format here is Request RQ1
    	// All but the checksum is already set up in SYS_REQ

        send(SYS_REQ.toSysexMessage(getChannel(),  
        		new SysexHandler.NameValue("checkSum", 0x59)
        ));
    }
}
