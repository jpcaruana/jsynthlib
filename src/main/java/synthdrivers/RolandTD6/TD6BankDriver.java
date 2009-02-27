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
import java.io.UnsupportedEncodingException;

import org.jsynthlib.core.BankDriver;
import org.jsynthlib.core.Patch;
import org.jsynthlib.core.SysexHandler;

/**
 * Bank Driver for Roland Percussion Sound Module TD-6
 *
 * @author <a href="mailto:hiroo.hayashi@computer.org">Hiroo Hayashi</a>
 * @version $Id$
 */
public class TD6BankDriver extends BankDriver {
    /** Size of a single patch */
    private static final int SINGLE_SIZE = 37 + 55 * 12;
    /** Number of patches. */
    private static final int NUM_PATCH = 99;
    /** Offset of patch name. */
    private static final int NAME_OFFSET = 10;
    /** Size of patch name. */
    private static final int NAME_SIZE = 8;

    /** Size of patches. */
    private static final int PATCH_SIZE = SINGLE_SIZE * NUM_PATCH;
    /** Number of columns for displaying the patches in a table. */
    private static final int NUM_COLUMNS = 9;
    /** Request data 1 RQ1 (11H) */
    private static final SysexHandler SYS_REQ = new SysexHandler
    ("F0 41 @@ 00 3F 11 41 7F 00 00 00 00 00 00 40 F7");

    private final TD6SingleDriver singleDriver;

    public TD6BankDriver(TD6SingleDriver singleDriver) {
	super("Bank", "Hiroo Hayashi <hiroo.hayashi@computer.org>",
	      NUM_PATCH, NUM_COLUMNS);

 	this.singleDriver = singleDriver;
	patchNameSize	= NAME_SIZE;
	bankNumbers	= new String[] {"Internal"};
	patchNumbers	= new String[NUM_PATCH];
	for (int i = 1; i <= NUM_PATCH; i++)
	    patchNumbers[i - 1] = (i < 10 ? "0" : "") +  String.valueOf(i);
	patchSize	= PATCH_SIZE;

	sysexID		= "F041**003F12";
	singleSysexID	= "F041**003F12";
	singleSize	= SINGLE_SIZE;
    }

    /**
     * Return the offset address of SysEX message (packet) specified
     * by <code>pkt</code>.
     *
     * @param patchNum patch number
     * @param pkt specifies the SysEX message
     * @return offset address of the message
     */
    private static int getPktOfst(int patchNum, int pkt) {
	return SINGLE_SIZE * patchNum + ((pkt == 0) ? 0 : 37 + (pkt - 1) * 55);
    }

    public String getPatchName(Patch p, int patchNum) {
	int nameOfst = SINGLE_SIZE * patchNum + NAME_OFFSET;
	try {
	    return new String(p.sysex, nameOfst, patchNameSize, "US-ASCII");
	} catch (UnsupportedEncodingException e) {
	    return "---";
	}
    }

    public void setPatchName(Patch p, int patchNum, String name) {
	int nameOfst = SINGLE_SIZE * patchNum + NAME_OFFSET;
	name += "       ";
	byte[] namebytes = name.getBytes();
	for (int i = 0; i < patchNameSize; i++)
	    p.sysex[nameOfst + i] = namebytes[i];
    }

    public void calculateChecksum (Patch p) {
	for (int i = 0; i < NUM_PATCH; i++)
	    singleDriver.calculateChecksum(p, SINGLE_SIZE * i);
    }

    public void putPatch(Patch bank, Patch p, int patchNum) {
	System.arraycopy(p.sysex, 0,
			 bank.sysex, SINGLE_SIZE * patchNum, SINGLE_SIZE);
	for (int i = 0; i < TD6SingleDriver.NUM_PKT; i++) {
	    // adjust address field
	    bank.sysex[getPktOfst(patchNum, i) + 7] = (byte) patchNum;
	}
	singleDriver.calculateChecksum(bank, SINGLE_SIZE * patchNum);
    }

    public Patch getPatch(Patch bank, int patchNum) {
	byte[] sysex = new byte[SINGLE_SIZE];
	System.arraycopy(bank.sysex, getPktOfst(patchNum, 0),
			 sysex, 0, SINGLE_SIZE);
 	return new Patch(sysex, singleDriver);
    }

    public Patch createNewPatch() {
	byte[] sysex = new byte[PATCH_SIZE];
	Patch bank = new Patch(sysex, this);
	Patch p = singleDriver.createNewPatch();
	for (int i = 0; i < NUM_PATCH; i++)
	    putPatch(bank, p, i);
	return bank;
    }

    // bankNum nor patchNum are not used.
    public void requestPatchDump(int bankNum, int patchNum) {
	send(SYS_REQ.toSysexMessage(getDeviceID()));
    }

    // bankNum nor patchNum are not used.
    public void storePatch (Patch p, int bankNum, int patchNum) {
	int ofst = 0;
	for (int i = 0; i < NUM_PATCH; i++, ofst += SINGLE_SIZE) {
	    singleDriver.storePatch(p.sysex, ofst, i);
	}
    }
}
