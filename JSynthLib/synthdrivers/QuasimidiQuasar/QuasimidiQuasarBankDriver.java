/*
 * Copyright 2004 Joachim Backhaus
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

package synthdrivers.QuasimidiQuasar;

import core.*;
import javax.swing.*;
import java.io.*;

/**
 * Driver for Quasimidi Quasar Performance Bank's
 *
 * @author Joachim Backhaus
 * @version $Id$
 */
public class QuasimidiQuasarBankDriver extends BankDriver {
	private QuasimidiQuasarSingleDriver quasarSingleDriver;

	public QuasimidiQuasarBankDriver(QuasimidiQuasarSingleDriver quasarSingleDriver) {
		this.quasarSingleDriver = quasarSingleDriver;

		this.authors = this.quasarSingleDriver.getAuthors();
		this.manufacturer = this.quasarSingleDriver.manufacturer;
		this.model = this.quasarSingleDriver.model;
		this.patchType = "Performance Bank";
		this.id = this.quasarSingleDriver.id;
		this.sysexID = this.quasarSingleDriver.sysexID;

		//"F03F**2052**********F7" is the format for requests
		this.sysexRequestDump = this.quasarSingleDriver.sysexRequestDump;

		this.patchNameStart = 0;
		this.patchNameSize = 0; // Just one bank ("RAM")
		this.deviceIDoffset = 2;
		this.bankNumbers = new String[] { "RAM" };
		this.patchNumbers = this.quasarSingleDriver.patchNumbers;

		this.numPatches = patchNumbers.length;
		this.numColumns = 5;
		this.singleSysexID = this.sysexID;
		this.singleSize = this.quasarSingleDriver.patchSize;

		this.patchSize = singleSize * patchNumbers.length;
    }

    public void calculateChecksum(Patch p,int start,int end,int ofs) {
        // no checksum, do nothing
    }

    public void storePatch(Patch p, int bankNum, int patchNum) {
        setPatchNum(patchNum);
        sendPatch(p);
	}

	public int getPatchStart(int patchNum) {
		int start = (this.singleSize * patchNum);

		return start;
	}

	// Puts a patch into the bank, converting it as needed
	public void putPatch(Patch bank, Patch p, int patchNum) {
		if (!canHoldPatch(p)) {
			{
				JOptionPane.showMessageDialog(	null,
												"This type of patch does not fit in to this type of bank.",
												"Error",
												JOptionPane.ERROR_MESSAGE);
				return;
			}
		}

		System.arraycopy(	p.sysex,
							QuasimidiQuasarSingleDriver.SYSEX_HEADER_OFFSET,
							bank.sysex,
							getPatchStart(patchNum) + QuasimidiQuasarSingleDriver.SYSEX_HEADER_OFFSET,
							this.singleSize - QuasimidiQuasarSingleDriver.SYSEX_HEADER_OFFSET);
	}

	// Gets a patch from the bank, converting it as needed
	public Patch getPatch(Patch bank, int patchNum) {
		try{
			byte [] sysex = new byte[this.quasarSingleDriver.patchSize];

			System.arraycopy(	bank.sysex,
								getPatchStart(patchNum),
								sysex,
								0,
								this.singleSize);
			Patch p = new Patch(sysex);
			p.ChooseDriver();
			//PatchEdit.getDriver(p.deviceNum, p.driverNum).calculateChecksum(p);
			return p;
		} catch (Exception e) {
			ErrorMsg.reportError("Error", "Error in Quasar Bank Driver", e);
			return null;
		}
	}

	// Get the name of the patch at the given number
	public String getPatchName(Patch p, int patchNum) {
		int nameStart = getPatchStart(patchNum);
		nameStart += this.quasarSingleDriver.patchNameStart; //offset of name in patch data
		try {
			StringBuffer s = new StringBuffer(new String(	p.sysex,
															nameStart,
															this.quasarSingleDriver.patchNameSize,
															"US-ASCII"));
			return s.toString();
		} catch (UnsupportedEncodingException ex) {
			return "-";
		}

	}


	public void requestPatchDump(int bankNum, int patchNum) {
		//setBankNum(bankNum);
		//setPatchNum(patchNum);

		if (sysexRequestDump == null) {
			JOptionPane.showMessageDialog(PatchEdit.instance,
				"The " + getDriverName() + " driver does not support patch getting.\n\nPlease start the patch dump manually...",
				"Get Patch",
				JOptionPane.WARNING_MESSAGE
			);
			byte buffer[] = new byte[256*1024];

			try {
				while (PatchEdit.MidiIn.messagesWaiting(inPort) > 0)
					PatchEdit.MidiIn.readMessage(inPort, buffer, 1024);
			} catch (Exception ex) {
				ErrorMsg.reportError("Error", "Error Clearing Midi In buffer.", ex);
			}

		}
		else {
			// Request dumps for all 100 RAM peformances (that are 5 * 100 requests!!!)

			for(int patchNo = 0; patchNo < patchNumbers.length; patchNo++) {
				for(int count = 0; count < QuasimidiQuasarSingleDriver.QUASAR_SYSEX_PERFORMANCE_REQUEST.length; count++) {
					
					this.sysexRequestDump = new SysexHandler( QuasimidiQuasarSingleDriver.QUASAR_SYSEX_PERFORMANCE_REQUEST[count] );

					sysexRequestDump.send(
						port, (byte)channel,
						new NameValue("perfNumber", patchNo + QuasimidiQuasarSingleDriver.QUASAR_SYSEX_PERFORMANCE_OFFSET)
					);

					try {
						// Wait a little bit so that everything is in the correct sequence
						Thread.sleep(50);
					} catch (Exception ex) {
						ErrorMsg.reportError("Error", "Error requesting all Quasar RAM performances.", ex);
					}
				}
			}
		}
	}

    public Patch createNewPatch() {
        byte [] sysex = new byte[this.patchSize];

        Patch tempPatch;

        for(int patchNo = 0; patchNo < patchNumbers.length; patchNo++) {
        	tempPatch = this.quasarSingleDriver.createNewPatch(patchNo, QuasimidiQuasarSingleDriver.QUASAR_SYSEX_PERFORMANCE_OFFSET);

        	System.arraycopy(	tempPatch.sysex,
        						0,
        						sysex,
        						(patchNo * this.quasarSingleDriver.patchSize),
        						this.quasarSingleDriver.patchSize);
       	}

        Patch p = new Patch(sysex);
        p.ChooseDriver();

        return p;
    }
}

