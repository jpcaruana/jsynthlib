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
		super("Performance Bank", "Joachim Backhaus", QuasarConstants.QUASAR_PATCH_NUMBERS.length, 5);
		
		this.quasarSingleDriver = quasarSingleDriver;		
		
		this.sysexID = QuasarConstants.QUASAR_SYSEX_ID;		
		
		// This one is never really used, just a dummy to prevent the "this synth doesn't support patch request" dialog
		this.sysexRequestDump = new SysexHandler( QuasarConstants.QUASAR_SYSEX_PERFORMANCE_REQUEST[0] );

		this.patchNameStart = 0;
		this.patchNameSize = 0; // Just one bank ("RAM")
		this.deviceIDoffset = 2;
		this.bankNumbers = new String[] { "RAM" };
		this.patchNumbers = QuasarConstants.QUASAR_PATCH_NUMBERS;
				
		this.singleSysexID = this.sysexID;
		this.singleSize = QuasarConstants.QUASAR_PATCH_SIZE;

		this.patchSize = singleSize * patchNumbers.length;
    }

	/**
	* The Quasar uses no checksum therefore this method is empty
	*/
    public void calculateChecksum(Patch p) {
        // no checksum, do nothing
    }

    public void storePatch(Patch p, int bankNum, int patchNum) {
        setPatchNum(patchNum);
        sendPatch(p);
	}

	/**
	* Get the index where the patch starts in the banks SysEx data.
	*/
	public int getPatchStart(int patchNum) {
		int start = (this.singleSize * patchNum);

		return start;
	}

	/**
	* Puts a patch into the bank, converting it as needed
	*/
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
							QuasarConstants.SYSEX_HEADER_OFFSET,
							bank.sysex,
							getPatchStart(patchNum) + QuasarConstants.SYSEX_HEADER_OFFSET,
							this.singleSize - QuasarConstants.SYSEX_HEADER_OFFSET);
	}

	/**
	* Gets a patch from the bank, converting it as needed
	*/
	public Patch getPatch(Patch bank, int patchNum) {
		try{
			byte [] sysex = new byte[QuasarConstants.QUASAR_PATCH_SIZE];

			System.arraycopy(	bank.sysex,
								getPatchStart(patchNum),
								sysex,
								0,
								this.singleSize);
			Patch p = new Patch(sysex);
//			p.ChooseDriver();
			
			return p;
		} catch (Exception e) {
			ErrorMsg.reportError("Error", "Error in Quasar Bank Driver", e);
			return null;
		}
	}

	/**
	* Get the name of the patch at the given number
	*/
	public String getPatchName(Patch p, int patchNum) {
		int nameStart = getPatchStart(patchNum);
		nameStart += QuasarConstants.QUASAR_PATCH_NAME_START; //offset of name in patch data
		try {
			StringBuffer s = new StringBuffer(new String(	p.sysex,
															nameStart,
															QuasarConstants.QUASAR_PATCH_NAME_SIZE,
															"US-ASCII"));
			return s.toString();
		} catch (UnsupportedEncodingException ex) {
			return "-";
		}

	}

	/**
	* Request a dump of all 100 RAM Performances
	*/
	public void requestPatchDump(int bankNum, int patchNum) {		
		if (sysexRequestDump == null) {
			JOptionPane.showMessageDialog
				(PatchEdit.getInstance(),
				 "The " + toString()
				 + " driver does not support patch getting.\n\n"
				 + "Please start the patch dump manually...",
				 "Get Patch", JOptionPane.WARNING_MESSAGE);
				 /*
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
*/
		}
		else {
			// Request dumps for all 100 RAM peformances (that are 5 * 100 requests!!!)
			for(int patchNo = 0; patchNo < patchNumbers.length; patchNo++) {
				for(int count = 0; count < QuasarConstants.QUASAR_SYSEX_PERFORMANCE_REQUEST.length; count++) {
					
					this.sysexRequestDump = new SysexHandler( QuasarConstants.QUASAR_SYSEX_PERFORMANCE_REQUEST[count] );
					
					send(sysexRequestDump.toSysexMessage(super.getDeviceID(),
						 new NameValue("perfNumber", patchNo + QuasarConstants.QUASAR_SYSEX_PERFORMANCE_OFFSET)
						 								)
					);
/*
					sysexRequestDump.send(
						port, (byte)channel,
						new NameValue("perfNumber", patchNo + QuasarConstants.QUASAR_SYSEX_PERFORMANCE_OFFSET)
					);
*/
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

	/**
	* Creates a new bank with 100 new Quasar Performances
	*/
    public Patch createNewPatch() {
        byte [] sysex = new byte[this.patchSize];

        Patch tempPatch;

        for(int patchNo = 0; patchNo < patchNumbers.length; patchNo++) {
        	tempPatch = this.quasarSingleDriver.createNewPatch(patchNo, QuasarConstants.QUASAR_SYSEX_PERFORMANCE_OFFSET);

        	System.arraycopy(	tempPatch.sysex,
        						0,
        						sysex,
        						(patchNo * QuasarConstants.QUASAR_PATCH_SIZE ),
        						QuasarConstants.QUASAR_PATCH_SIZE );
       	}

        Patch p = new Patch(sysex);
//        p.ChooseDriver();

        return p;
    }
}

