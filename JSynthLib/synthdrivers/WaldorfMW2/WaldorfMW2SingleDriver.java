/*
 * Copyright 2005 Joachim Backhaus
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

package synthdrivers.WaldorfMW2;

import java.io.UnsupportedEncodingException;

import javax.swing.JOptionPane;

import core.Driver;
import core.DriverUtil;
import core.ErrorMsg;
import core.JSLFrame;
import core.Patch;
import core.PatchEdit;
import core.SysexHandler;

/** 
 * Driver for Microwave 2 / XT / XTK single programs
 *
 * @author Joachim Backhaus
 * @version $Id$
 */
public class WaldorfMW2SingleDriver extends Driver {

    public WaldorfMW2SingleDriver() {
        super("Single program", "Joachim Backhaus");

        this.sysexID = MW2Constants.SYSEX_ID + "10";

        this.sysexRequestDump = new SysexHandler( "F0 3E 0E @@ 00 *BB* *NN* *XSUM* F7" );

        this.patchNameStart = MW2Constants.PATCH_NAME_START;
        this.patchNameSize = MW2Constants.PATCH_NAME_SIZE;
        this.deviceIDoffset = MW2Constants.DEVICE_ID_OFFSET;

        checksumStart = 5;
        checksumEnd = 262;
        checksumOffset = 263;

        this.bankNumbers = new String[] { "A", "B" };

        this.patchNumbers = DriverUtil.generateNumbers(1, 128, "#");

        // Patch size (265 Bytes)
        this.patchSize = MW2Constants.PATCH_SIZE;
    }

    public Patch createNewPatch() {
		byte[] sysex  = new byte[MW2Constants.PATCH_SIZE];
		Patch p;

        try {
			java.io.InputStream fileIn = getClass().getResourceAsStream(MW2Constants.DEFAULT_SYSEX_FILENAME);
			fileIn.read(sysex);
			fileIn.close();
			p = new Patch(sysex, this);

		} catch (Exception e) {
			System.err.println("Unable to find " + MW2Constants.DEFAULT_SYSEX_FILENAME + " using hardcoded default.");

			sysex[0] = MW2Constants.SYSEX_START_BYTE;
			sysex[1] = (byte) 0x3E; // Waldorf Electronics GmbH ID
			sysex[2] = (byte) 0x0E; // Microwave 2 ID
			sysex[3] = (byte) 0x00; // Device ID
			sysex[4] = (byte) 0x10; // Sound Dump
			sysex[5] = (byte) 0x20; // Location (use Edit Buffer)
			sysex[6] = (byte) 0x00; // Location (use Edit Buffer)

			sysex[263] = (byte) 0x00; // Checksum
			sysex[264] = MW2Constants.SYSEX_END_BYTE;

			p = new Patch(sysex, this);
			setPatchName(p, "New program");
			calculateChecksum(p);
		}

		return p;
    }

	/**
    * Request the dump of a single program
    *
    * @param bankNum    The bank number (0 = A, 1 = B)
    * @param patchNum   The number of the requested single program
    */
    public void requestPatchDump(int bankNum, int patchNum) {

        if (sysexRequestDump == null) {
            JOptionPane.showMessageDialog
                (PatchEdit.getInstance(),
                 "The " + toString()
                 + " driver does not support patch getting.\n\n"
                 + "Please start the patch dump manually...",
                 "Get Patch", JOptionPane.WARNING_MESSAGE);
        }
        else {
            SysexHandler.NameValue[] nameValues = {
                new SysexHandler.NameValue("BB", bankNum ),
                new SysexHandler.NameValue("NN", patchNum ),
                new SysexHandler.NameValue("XSUM", ((byte)(bankNum + patchNum)) & 0x7F )
            };

            send(sysexRequestDump.toSysexMessage(getDeviceID(), nameValues) );

            try {
                // Wait a little bit so that everything is in the correct sequence
                Thread.sleep(50);
            } catch (Exception ex) {
                // Ignore these exceptions
            }
        }
    }
}

