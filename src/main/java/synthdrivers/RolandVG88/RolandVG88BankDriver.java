/*
 * Copyright 2006 Nacho Alonso
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

package synthdrivers.RolandVG88;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.jsynthlib.core.BankDriver;
import org.jsynthlib.core.DriverUtil;
import org.jsynthlib.core.ErrorMsg;
import org.jsynthlib.core.Patch;

 /**
 * Bank Driver for Roland VG88
 */
public class RolandVG88BankDriver extends BankDriver {

    /** Number of columns for displaying the patches in a table. */
    private static final int NUM_COLUMNS = 5;

    private static final int BANK_NAME_SIZE = 0;
	
    private final RolandVG88SingleDriver singleDriver;

    /** bank file name for createNewPatch() */
    private static final String bankDefFileName = "RolandVG88DefaultBank.syx";


    public RolandVG88BankDriver(RolandVG88SingleDriver singleDriver) {
		super("Bank", "Nacho Alonso",RolandVG88SingleDriver.NUM_PATCH, NUM_COLUMNS);

		this.singleDriver = singleDriver;
		singleSize = RolandVG88SingleDriver.SINGLE_SIZE;
		patchSize = RolandVG88SingleDriver.SINGLE_SIZE * RolandVG88SingleDriver.NUM_PATCH;

		patchNameStart = RolandVG88SingleDriver.SINGLE_SIZE * RolandVG88SingleDriver.NUM_PATCH ;
		patchNameSize = BANK_NAME_SIZE;

		bankNumbers	= RolandVG88SingleDriver.BANK_NUMBERS;
		patchNumbers = RolandVG88SingleDriver.PATCH_NUMBERS;

		sysexID		= RolandVG88SingleDriver.SYSEX_ID;
		singleSysexID	= RolandVG88SingleDriver.SYSEX_ID;
	}

    /**
     * 	Get Bank Name (not soported, nameSize for bank is 0)
     */
    public String getPatchName(Patch p) {
  		return bankNumbers[0];
    }

    /**
     * 	Set Bank Name (not soported, nameSize for bank is 0)
     */
    public void setPatchName(Patch p, String name) {
		JOptionPane.showMessageDialog((JFrame) null,
		    "If you want to assign a name to this bank inside JSynth, use 'Field1' or 'Filed2' or 'Comment' fields",
			"Advice: VG88 don't use 'Bank name'",
			JOptionPane.INFORMATION_MESSAGE ); 
	}

    /**
     * 	Get a patch name 
     */
    public String getPatchName(Patch p, int patchNum) {
		return singleDriver.getPatchName(getPatch(p, patchNum));
    }

    /**
     * 	Set a patch name 
     */
    public void setPatchName(Patch p, int patchNum, String name) {
		Patch pAux = getPatch(p, patchNum);
		singleDriver.setPatchName(pAux, name);
		putPatch(p, pAux, patchNum);
    }

    /**
     * Calculate checkSum for each patch in a bank 
     */
    public void calculateChecksum(Patch p) {
		for (int i = 0; i < RolandVG88SingleDriver.NUM_PATCH; i++)
			singleDriver.calculateChecksum(p, singleSize * i);
    }

    /**
     * Put a patch into a bank 
     */
    public void putPatch(Patch bank, Patch p, int patchNum) {
		singleDriver.arrangePatchVG88(p, patchNum);
		System.arraycopy(p.sysex, 0, bank.sysex, singleSize * patchNum, singleSize);
	}

    /**
     * Get a patch into a bank 
     */
    public Patch getPatch(Patch bank, int patchNum) {
		byte[] sysex = new byte[singleSize];
		System.arraycopy(bank.sysex, singleSize * patchNum, sysex, 0, singleSize);
		return new Patch(sysex, singleDriver);
    }

    /**
     * Create a new bank 
     */
    public Patch createNewPatch() {
        Patch bank = (Patch) DriverUtil.createNewPatch(this, bankDefFileName, this.patchSize);
//		byte[] sysex = new byte[patchSize];
//		Patch bank = new Patch(sysex, this);
//		Patch p = singleDriver.createNewPatch();
//		for (int i = 0; i < singleDriver.NUM_PATCH; i++) {
//			putPatch(bank, p, i);
//		}
        bank.setComment("User VG88 - V2");
        return bank;
    }

    /**
     * Request all user Patchs 
     * BankNum nor patchNum are not used. Request all user Patchs
     */
    public void requestPatchDump(int bankNum, int patchNum) {
		patchSize = patchSize - BANK_NAME_SIZE;
		for (int i = 0; i < RolandVG88SingleDriver.NUM_PATCH; i++) {
			singleDriver.requestPatchDump(0, i);
			try {
				Thread.sleep(600);	// wait .
			} catch (Exception e) {
				ErrorMsg.reportStatus(e);
			}
		}
		patchSize = patchSize + BANK_NAME_SIZE;
    }

    /**
     * Store all user Patchs 
     * BankNum nor patchNum are not used. Request all user Patchs
     */
    public void storePatch(Patch p, int bankNum, int patchNum) {
		int ofst = 0;
		for (int i = 0; i < RolandVG88SingleDriver.NUM_PATCH; i++, ofst += singleSize) {
			singleDriver.storePatch(getPatch(p,i), 0, i);
		}
    }
}
