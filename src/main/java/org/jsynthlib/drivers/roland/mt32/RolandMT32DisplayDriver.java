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
 * Rhythm Setup Driver for Roland MT32.
 *
 * @version $Id$
 */

package org.jsynthlib.drivers.roland.mt32;
import org.jsynthlib.core.Driver;
import org.jsynthlib.core.ErrorMsg;
import org.jsynthlib.core.JSLFrame;
import org.jsynthlib.core.Patch;
import org.jsynthlib.core.SysexHandler;

/**
 * Display Driver for Roland MT32.
 *
 * @version $Id$
 */
public class RolandMT32DisplayDriver extends Driver {
//
    /** Header Size of the Data set DT1 message. */
// Skeleton definition of the message received when sending the request
    private static final int HSIZE = 0x08;
    /** Single Patch size */
    private static final int SSIZE = 0x14;
    
    /** Definition of the Request message RQ1 */
// Not very useful as the MT32 doesn't support retrieval from the display
    private static final SysexHandler SYS_REQ = new SysexHandler(
        "");
    
    public RolandMT32DisplayDriver() {
	    super("Display", "Fred Jan Kraan");
	    sysexID         = "F041**16";
	    patchSize	    = HSIZE + SSIZE + 1;
	    patchNameStart	= HSIZE;
	    patchNameSize	= 0x14;
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
	    try {
	        Thread.sleep(100);
	    } catch (Exception e) {
            ErrorMsg.reportStatus(e);
	    }
        p.sysex[0]   = (byte) 0xF0;
        p.sysex[5]   = (byte) 0x20;	//Point to Display area
	    p.sysex[6]   = (byte) 0x00;
	    p.sysex[7]   = (byte) 0x00;
	    calculateChecksum(p, 5, HSIZE+SSIZE-2, HSIZE+SSIZE-1 );

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

	   sendPatchWorker(p);
    }

    // not used
    protected void calculateChecksum(Patch p, int start, int end, int ofs) {
//      Calculate the checksum 
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
        sysex[5] = (byte) 0x20; 
        sysex[6] = (byte) 0x00; 
        sysex[7] = (byte) 0x00; // address
        for (int i=0; i < 0x14; i++) {
        	sysex[HSIZE + i] = 0x20;
        }
        sysex[HSIZE + 0x00] = (byte) ' ';
        sysex[HSIZE + 0x01] = (byte) '-';
        sysex[HSIZE + 0x02] = (byte) '-';
        sysex[HSIZE + 0x03] = (byte) '*';
        sysex[HSIZE + 0x04] = (byte) '*';
        sysex[HSIZE + 0x05] = (byte) 'J';
        sysex[HSIZE + 0x06] = (byte) 'S';
        sysex[HSIZE + 0x07] = (byte) 'y';
        sysex[HSIZE + 0x08] = (byte) 'n';
        sysex[HSIZE + 0x09] = (byte) 't';
        sysex[HSIZE + 0x0A] = (byte) 'h';
        sysex[HSIZE + 0x0B] = (byte) 'L';
        sysex[HSIZE + 0x0C] = (byte) 'i';
        sysex[HSIZE + 0x0D] = (byte) 'b';
        sysex[HSIZE + 0x0E] = (byte) '*';
        sysex[HSIZE + 0x0F] = (byte) '*';
        sysex[HSIZE + 0x10] = (byte) '-';
        sysex[HSIZE + 0x11] = (byte) '-';

        sysex[HSIZE + SSIZE] = (byte) 0xF7;
	    Patch p = new Patch(sysex, this);
	    calculateChecksum(p);
	    return p;
    }

    public JSLFrame editPatch(Patch p) {
	    return new RolandMT32DisplayEditor(p);
    }

/*    public void requestPatchDump(int bankNum, int patchNum) {
        // The message format here is Request RQ1
    	// All but the checksum is already set up in SYS_REQ
    	
//        send(SYS_REQ.toSysexMessage(getChannel(),  
//        		new SysexHandler.NameValue("checkSum", 0x69)
          send(SYS_REQ.toSysexMessage(getChannel()));
    } */
} 
