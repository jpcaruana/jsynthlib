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
import java.io.UnsupportedEncodingException;
import javax.swing.JOptionPane;

/**
 * Bank Driver for Roland Percussion Sound Module TD-6
 *
 * @author <a href="mailto:hiroo.hayashi@computer.org">Hiroo Hayashi</a>
 * @version $Id$
 */
public class TD6BankDriver extends BankDriver {
    /** reference to single driver. This is not necessary if 'static' is
	used properly by Driver class. */
    private TD6SingleDriver singleDriver;

    public TD6BankDriver() {
	manufacturer	= "Roland";
	model		= "TD6";
	patchType	= "Bank";
	id		= "TD6";
	sysexID		= "F041**003F12";
	//inquiryID	= "F07E**0602413F01000000020000f7";
	// Request data 1 RQ1 (11H)
	sysexRequestDump = new SysexHandler
	    ("F0 41 @@ 00 3F 11 41 7F 00 00 00 00 00 00 40 F7");

	deviceIDoffset	= 2;	// This cannot work...

	bankNumbers		= new String[] {"Internal"};
	patchNumbers	= new String[99];
	for (int i = 0; i < 99; i++)
	    patchNumbers[i] = (i < 9 ? "0" : "") +  String.valueOf(i + 1);

	numPatches	= patchNumbers.length;
	numColumns	= 5;
	singleSysexID	= "F041**003F12";

	singleSize	= 37 + 55 * 12;
	patchSize	= singleSize * numPatches;
	patchNameSize	= 8;
	authors		= "Hiroo Hayashi <hiroo.hayashi@computer.org>";

	singleDriver	= new synthdrivers.RolandTD6.TD6SingleDriver();
    }


    /**
     * Return the offset address of SysEX message (packet) specified by <code>pkt</code>.
     *
     * @param patchNum patch number
     * @param pkt specifies the SysEX message
     * @return offset address of the message
     */
    private int getPktOfst(int patchNum, int pkt) {
	return singleSize * patchNum + ((pkt == 0) ? 0 : 37 + (pkt - 1) * 55);
    }

    /**
     * Return the size of SysEX message (packet) specified by <code>pkt</code>.
     *
     * @param pkt specifies the SysEX message
     * @return size of the message
     */
    private int getPktSize(int pkt) {
	return pkt == 0 ? 37 : 55;
    }


    /**
     * Return the offset address of the check sum in a SysEX message
     * (packet) specified by <code>pkt</code>.
     *
     * @param patchNum patch number
     * @param pkt specifies the SysEX message
     * @return offset address of check sum
     */
    private int getChkSumOfst(int patchNum, int pkt) {
	return singleSize * patchNum + ((pkt == 0) ? 37 : 37 + pkt * 55) - 2;
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

    public String getPatchName(Patch p, int patchNum) {
	int nameOfst = singleSize * patchNum + 10;
	try {
	    return new String(p.sysex, nameOfst, patchNameSize, "US-ASCII");
	} catch (UnsupportedEncodingException e) {
	    return "---";
	}
    }

    public void setPatchName(Patch p, int patchNum, String name) {
	int nameOfst = singleSize * patchNum + 10;
	name += "       ";
	byte[] namebytes = name.getBytes();
	for (int i = 0; i < patchNameSize; i++)
	    p.sysex[nameOfst + i] = namebytes[i];
    }

    public void calculateChecksum (Patch p) {
	for (int i = 0; i < numPatches; i++)
	    for (int j = 0; j < 13; j++) {
		int chkSumIdx = getChkSumOfst(i, j);
		p.sysex[chkSumIdx] = calcChkSum(p.sysex,
						getPktOfst(i, j) + 6, chkSumIdx - 1);
	    }
    }

    public void putPatch(Patch bank, Patch p, int patchNum) {
	//ErrorMsg.reportStatus("putPatch : patchNum : " + patchNum);
	if (!canHoldPatch(p)) {	// This check should be done by superclass.
	    // This is a callers job. Driver should just throw Exception.
	    JOptionPane.showMessageDialog(null,
					  "This type of patch does not fit in to this type of bank.",
					  "Error", JOptionPane.ERROR_MESSAGE);
	    return;
	}
	for (int i = 0; i < 13; i++) {
	    int pktOfst = getPktOfst(patchNum, i);
	    System.arraycopy(p.sysex, 0, bank.sysex, pktOfst, getPktSize(i));
	    // adjust address field
	    bank.sysex[pktOfst + 7] = (byte) patchNum;
	    // calculate check sum
	    int chkSumIdx = getChkSumOfst(patchNum, i);
	    bank.sysex[chkSumIdx] = calcChkSum(bank.sysex, pktOfst + 6, chkSumIdx - 1);
	}
    }

    public Patch getPatch(Patch bank, int patchNum) {
	byte[] sysex = new byte[singleSize];
	System.arraycopy(bank.sysex, getPktOfst(patchNum, 0),
			 sysex, 0, singleSize);
	return new Patch(sysex);
    }

    public Patch createNewPatch() {
	byte[] sysex = new byte[patchSize];
	Patch bank = new Patch(sysex);
	Patch p = singleDriver.createNewPatch();
	for (int i = 0; i < numPatches; i++) {
	    for (int j = 0; j < 13; j++)
		p.sysex[getPktOfst(0, j) + 7] = (byte) i; // adjust address field
	    singleDriver.calculateChecksum(p);
	    putPatch(bank, p, i);
	}
	return bank;
    }

    // bankNum or patchNum are not used.
    public void requestPatchDump(int bankNum, int patchNum) {
	sysexRequestDump.send(port, (byte) channel);
    }

    // bankNum or patchNum are not used.
    public void storePatch (Patch p, int bankNum, int patchNum) {
	for (int i = 0; i < numPatches; i++)
	    singleDriver.storePatch(p, 0, i);
    }
}
