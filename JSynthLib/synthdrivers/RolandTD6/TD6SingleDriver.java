/*
 * Copyright 2003 Hiroo Hayashi
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

package synthdrivers.RolandTD6;
import core.*;
import javax.swing.*;
import java.io.*;

/**
 * Single Driver for Roland Percussion Sound Module TD-6
 *
 * @author <a href="mailto:hiroo.hayashi@computer.org">Hiroo Hayashi</a>
 * @version $Id$
 */
public final class TD6SingleDriver extends Driver {
    /**
     * Creates a new <code>TD6SingleDriver</code> instance.
     *
     */
    public TD6SingleDriver() {
	manufacturer	= "Roland";
	model		= "TD6";
	patchType	= "Drumkit";
	id		= "TD6";
	// Data set 1 DT1 followed by 4 byte address (MSB first) and data
	//		   0 1 2 3 4 5
	sysexID		= "F041**003F12";
	// obsoleted
	inquiryID	= "F07E**0602413F01000000020000f7";

	patchNameStart	= 10;	// offset 0
	patchNameSize	= 8;
	deviceIDoffset	= 2;

	// can be replaced by patch name???
	patchNumbers	= new String[99];
	for (int i = 0; i < 99; i++)
	    patchNumbers[i] = (i < 9 ? "0" : "") +  String.valueOf(i + 1);

	//bankNumbers =new String[] {"0-Internal"}; //???

	patchSize	= 37 + 55 * 12;
	numSysexMsgs	= 13;	// Who use this?

	// channel	= 17;	// default Device ID

	// Request data 1 RQ1 (11H)
	sysexRequestDump = new SysexHandler
	    ("F0 41 @@ 00 3F 11 41 *patchNum* 00 00 00 00 00 00 *checkSum* F7");
	authors		= "Hiroo Hayashi <hiroo.hayashi@computer.org>";
    }

    /**
     * Send a patch (bulk dump system exclusive message) to MIDI device.
     *
     * TD-6 has 99 drum kits.  Drum kit can be selected only by a button
     * on the device.  Either Bank select or Program Change message
     * cannot select a drum kit.
     *
     * A drum kit number is used as address for System Exclusive
     * message.  Thus this driver ignores bank number and uses patch
     * number as Drum Kit number.
     *
     * "sendPatch()" is a proper name.!!!FIXIT!!!
     *
     * @param p a <code>Patch</code> value
     * @param bankNum ignored.
     * @param patchNum drum kit number (0: drum kit 1, ..., 98: drum kit 99)
     */
    public void storePatch (Patch p, int bankNum, int patchNum) {
	int i, size, ofst;
	// Patch must be sent in 13 packets.  data packet for the 1st
	// packet is 13 byte, and one for others is 55 byte.
	for (i = ofst = 0; i < 13; i++, ofst += size) {
	    // create a Patch data for each packet
	    size = (i == 0) ? 37 : 55; // SysEX data size
	    byte [] tmpSysex = new byte [size];
	    System.arraycopy(p.sysex, ofst, tmpSysex, 0, size);

	    // Drum kit : kk,  address 41 kk ii 00
	    tmpSysex[6] = (byte) 0x41;
	    tmpSysex[7] = (byte) patchNum;
	    tmpSysex[8] = (byte) i;
	    tmpSysex[size - 2] = calcChkSum(tmpSysex, 6, size - 3);

	    // send created patch to synthersizer
	    Patch tmpPatch = new Patch(tmpSysex);
	    //ErrorMsg.reportStatus(tmpPatch.toString());
	    sendPatchWorker(tmpPatch);
	    try {
		Thread.sleep(50);	// wait at least 50 milliseconds.
	    } catch (Exception e) {	// What's this?
	    }
	}
    }

    /**
     * Send a Patch (bulk dump system exclusive message) to an edit
     * buffer of MIDI device.
     *
     * Use Drum Kit 99 as an edit buffer.
     *
     * @param p a <code>Patch</code> value
     */
    public void sendPatch (Patch p) {
	storePatch (p, 0, 99 - 1);
    }

    /**
     * Calculate checksum from <code>start</code> to <code>end</code>.
     *
     * @param b a byte array
     * @param start start offset
     * @param end end offset
     * @return a <code>byte</code> value
     */
    private static byte calcChkSum(byte[] b, int start, int end) {
	int sum = 0;
	for (int i = start; i <= end; i++)
	    sum += b[i];
	return (byte) (-sum & 0x7f);
    }

    /**
     * Calculate and update checksum of a Patch.
     *
     * @param p a <code>Patch</code> value
     */
    public void calculateChecksum(Patch p) {
	for (int i = 0; i < 13; i++) {
	    int ofst = (i == 0) ? 0 : 37 + (i - 1) * 55;
	    int chkSumIdx = ofst + (i == 0 ? 37 : 55) - 2;
	    p.sysex[chkSumIdx] = calcChkSum(p.sysex, ofst + 6, chkSumIdx - 1);
	}
    }

    /** patch file name for createNewPatch() */
    private final static String patchFileName = "synthdrivers/RolandTD6/newpatch.syx";
    /**
     * Create new patch using "synthdrivers/RolandTD6/newpatch.syx".
     *
     * This can be defined in Driver.java. !!!FIXIT!!!
     * @return a <code>Patch</code> value
     */
    public Patch createNewPatch() { // Borrowed from DR660 driver
	try {
	    FileInputStream fileIn = new FileInputStream(new File(patchFileName));
	    byte [] buffer = new byte [patchSize];
	    fileIn.read(buffer);
	    fileIn.close();

	    Patch p = new Patch(buffer);
	    //p.ChooseDriver(); done by Patch(buffer)
	    return p;
	} catch (Exception e) {
	    ErrorMsg.reportError("Error", "Unable to open " + patchFileName, e);
	    return null;
	}
    }

    /*
    public Patch createNewPatch () {
	byte [] sysex = new byte[37+55*12];	// 37+55*12=697
	for (int i = 0; i < 13; i++) {
	    int ofst = (i == 0) ? 0 : 37 + (i-1)*55;
	    sysex[ofst+0] = (byte)0xF0;
	    sysex[ofst+1] = (byte)0x41; // Roland
	    sysex[ofst+2] = (byte)0x10; // device ID (default 17)
	    sysex[ofst+3] = (byte)0x00; // model ID (TD6)
	    sysex[ofst+4] = (byte)0x3F;
	    sysex[ofst+5] = (byte)0x12; // command ID (DT1)
	    sysex[ofst+6] = (byte)0x41; // address 0x41 mm 00 00
	    sysex[ofst+7] = (byte)i;
	    sysex[ofst+8] = (byte)0x00;
	    sysex[ofst+9] = (byte)0x00;
	    sysex[ofst+(i == 0 ? 37 : 55)-1] = (byte)0xF7;
	}
	Patch p = new Patch(sysex);
	p.ChooseDriver();
	setPatchName(p, "New Patch");
	calculateChecksum(p);
	return p;
    }
    */

    /**
     * Request a Patch (bulk dump system exclusive message) to MIDI
     * device.
     *
     * @param bankNum ignored
     * @param patchNum drum kit number (0: drum kit 1, ..., 98: drum kit 99)
     */
    public void requestPatchDump(int bankNum, int patchNum) {
	// checksum depends on drum kit number (patchNum).
	int checkSum = -(0x41 + patchNum) & 0x7f;
	// see core/SysexHandler.java
	sysexRequestDump.send(port, (byte) channel,
			      new NameValue("patchNum", patchNum),
			      new NameValue("checkSum", checkSum));
    }

    /**
     * Invoke Single Editor.
     *
     * @param p a <code>Patch</code> value
     * @return a <code>JInternalFrame</code> value
     */
    public JInternalFrame editPatch(Patch p) {
	return new TD6SingleEditor(p);
    }
}
