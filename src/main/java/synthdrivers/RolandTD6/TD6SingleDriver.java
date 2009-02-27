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

import org.jsynthlib.core.Driver;
import org.jsynthlib.core.DriverUtil;
import org.jsynthlib.core.ErrorMsg;
import org.jsynthlib.core.JSLFrame;
import org.jsynthlib.core.Patch;
import org.jsynthlib.core.SysexHandler;

/**
 * Single Driver for Roland Percussion Sound Module TD-6
 *
 * @author <a href="mailto:hiroo.hayashi@computer.org">Hiroo Hayashi</a>
 * @version $Id$
 */
public final class TD6SingleDriver extends Driver {
    /** Size of a single patch */
    private static final int SINGLE_SIZE = 37 + 55 * 12;
    /** Number of patches. */
    private static final int NUM_PATCH = 99;
    /** Offset of patch name. */
    private static final int NAME_OFFSET = 10;
    /** Size of patch name. */
    private static final int NAME_SIZE = 8;

    /** patch file name for createNewPatch() */
    private static final String patchFileName = "newpatch.syx";
    /**
     * Patch must be sent in 13 packets.  data packet for the 1st
     * packet is 37 byte, and one for others is 55 byte.
     */
    private static final int[] PKT_SIZE = {37,
					   55, 55, 55, 55, 55, 55,
					   55, 55, 55, 55, 55, 55};
    /** Number of packets in a single patch. */
    static final int NUM_PKT = PKT_SIZE.length;

    private static final SysexHandler SYS_REQ = new SysexHandler
    ("F0 41 @@ 00 3F 11 41 *patchNum* 00 00 00 00 00 00 *checkSum* F7");

    /**
     * Creates a new <code>TD6SingleDriver</code> instance.
     *
     */
    public TD6SingleDriver() {
	super("Drumkit", "Hiroo Hayashi <hiroo.hayashi@computer.org>");

	patchNameStart	= NAME_OFFSET;
	patchNameSize	= NAME_SIZE;

	bankNumbers	= new String[] {"Internal"};
	patchNumbers	= new String[NUM_PATCH];
	/*
	for (int i = 1; i <= NUM_PATCH; i++)
	    patchNumbers[i - 1] = (i < 10 ? "0" : "") + String.valueOf(i);
	*/
	patchNumbers	= DriverUtil.generateNumbers(1, NUM_PATCH, "Patch 00");
	patchSize	= SINGLE_SIZE;

	// Data set 1 DT1 followed by 4 byte address (MSB first) and data
	//		   0 1 2 3 4 5
	sysexID		= "F041**003F12";
	deviceIDoffset	= 2;
	// Request data 1 RQ1 (11H)
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
// 	ErrorMsg.reportStatus("storePatch: " + p);
//  	ErrorMsg.reportStatus("storePatch: " + device);
	storePatch(p.sysex, 0, patchNum);
    }

    /**
     * Send a patch (bulk dump system exclusive message) to MIDI device.
     *
     * @param sysex SysEX byte array.
     * @param offset offset index in <code>sysex</code>.
     * @param patchNum the patch number.
     */
    void storePatch (byte[] sysex, int offset, int patchNum) {
	int size;
	for (int i = 0; i < NUM_PKT; i++, offset += size) {
	    // create a Patch data for each packet
	    size = PKT_SIZE[i];
	    byte [] tmpSysex = new byte [size];
	    System.arraycopy(sysex, offset, tmpSysex, 0, size);
	    Patch p = new Patch(tmpSysex, (Driver) null);

	    p.sysex[2] = (byte) (getDeviceID() - 1);
	    // Drum kit : kk,  address 41 kk ii 00
	    p.sysex[6] = (byte) 0x41;
	    p.sysex[7] = (byte) patchNum;
	    p.sysex[8] = (byte) i;
	    calculateChecksum(p, 6, size - 3, size - 2);
// 	    p.sysex[size - 2] = calcChkSum(tmpSysex, 6, size - 3);
	    try {
		send(p.sysex);
	    } catch (Exception e) {
		ErrorMsg.reportStatus(e);
	    }
	    try {
		Thread.sleep(50);	// wait at least 50 milliseconds.
	    } catch (Exception e) {
		ErrorMsg.reportStatus(e);
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
	storePatch (p, 0, NUM_PATCH - 1);
    }

    /**
     * Calculate checksum from <code>start</code> to <code>end</code>.
     *
     * @param b a byte array
     * @param start start offset
     * @param end end offset
     * @return a <code>byte</code> value
     */
    /*
    private static byte calcChkSum(byte[] b, int start, int end) {
// 	ErrorMsg.reportStatus("  start = " + start + ", end = " + end);
	int sum = 0;
	for (int i = start; i <= end; i++)
	    sum += b[i];
	return (byte) (-sum & 0x7f);
    }
    */
    /**
     * Calculate and update checksum of a Patch.
     *
     * @param p a <code>Patch</code> value.
     * @param offset offset index to calculate the check sum.
     */
    void calculateChecksum(Patch p, int offset) {
// 	ErrorMsg.reportStatus("offset = " + offset);
	int size;
	for (int i = 0; i < NUM_PKT; i++, offset += size) {
	    size = PKT_SIZE[i];
	    int chkSumIdx = offset + size - 2;
// 	    sysex[chkSumIdx] = calcChkSum(sysex, offset + 6, chkSumIdx - 1);
	    calculateChecksum(p, offset + 6, chkSumIdx - 1, chkSumIdx);
	}
    }

    /**
     * Calculate and update checksum of a Patch.
     *
     * @param p a <code>Patch</code> value
     */
    public void calculateChecksum(Patch p) {
	calculateChecksum(p, 0);
    }

    /**
     * Create new patch using a patch file <code>patchFileName</code>.
     *
     * @return a <code>Patch</code> value
     */
    public Patch createNewPatch() {
        return (Patch) DriverUtil.createNewPatch(this, patchFileName, SINGLE_SIZE);
    }

    /**
     * Request a Patch (bulk dump system exclusive message) to MIDI device.
     * 
     * @param bankNum
     *            ignored
     * @param patchNum
     *            drum kit number (0: drum kit 1, ..., 98: drum kit 99)
     */
    public void requestPatchDump(int bankNum, int patchNum) {
	// checksum depends on drum kit number (patchNum).
	int checkSum = -(0x41 + patchNum) & 0x7f;
	send(SYS_REQ.toSysexMessage(getDeviceID(),
				    new SysexHandler.NameValue("patchNum", patchNum),
				    new SysexHandler.NameValue("checkSum", checkSum)));
    }

    /**
     * Invoke Single Editor.
     *
     * @param p a <code>Patch</code> value
     * @return a <code>JSLFrame</code> value
     */
    public JSLFrame editPatch(Patch p) {
// 	ErrorMsg.reportStatus("editPatch: " + device);
	return new TD6SingleEditor(p);
    }
}
