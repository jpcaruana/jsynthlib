package synthdrivers.EnsoniqVFX;
import javax.swing.JOptionPane;

import core.BankDriver;
import core.ErrorMsg;
import core.Patch;
import core.SysexHandler;

/**
 *  Bank driver for Ensoniq VFX
 *
 * @author     <a href="mailto:dqueffeulou@free.fr">Denis Queffeulou</a>  (created 17 Sep 2002)
 * @version $Id$
 */
public class EnsoniqVFXBankDriver extends BankDriver
{
	static final int BANK_NB_PATCHES = 60;
	static final int BANK_SIZE = EnsoniqVFXSingleDriver.PATCH_SIZE * BANK_NB_PATCHES;
	static final int BANK_AND_HEADER_SIZE = BANK_SIZE+7;
	/**
	 *  Constructor for the EnsoniqVFXBankDriver object
	 */
	public EnsoniqVFXBankDriver()
	{
		super ("Bank","Denis Queffeulou",BANK_NB_PATCHES,4);
		sysexID = "F00F0500**03";
		deviceIDoffset = 4;

		singleSysexID = "F00F0500**02";
		singleSize = EnsoniqVFXSingleDriver.PATCH_AND_HEADER_SIZE;
		bankNumbers = new String[]{"0-Internal"};
		patchNumbers = new String[]{"01", "02", "03", "04", "05", "06", "07", "08",
				"09", "10", "11", "12", "13", "14", "15", "16",
				"17", "18", "19", "20", "21", "22", "23", "24",
				"25", "26", "27", "28", "29", "30", "31", "32",
				"33", "34", "35", "36", "37", "38", "39", "40",
				"41", "42", "43", "44", "45", "46", "47", "48", "49", "50",
				"51", "52", "53", "54", "55", "56", "57", "58", "59", "60"};

		// request the internal bank
		sysexRequestDump=new SysexHandler("F0 0F 05 00 00 00 00 09 F7");
		patchSize = BANK_AND_HEADER_SIZE;

	}


	/**
	 *  Gets the patchStart attribute of the EnsoniqVFXBankDriver object
	 *
	 *@param  patchNum  Description of the Parameter
	 *@return           The patchStart value
	 */
	public int getPatchStart(int patchNum)
	{
		int start = (EnsoniqVFXSingleDriver.PATCH_SIZE * patchNum);
		start += 6;
		//sysex header
		return start;
	}


	/**
	 *  Gets the patchName of a patch in the bank
	 *
	 *@param  p         bank sysex
	 *@param  patchNum  number of the patch in the bank
	 *@return           The patchName 
	 */
	public String getPatchName(Patch p, int patchNum)
	{
		int oPatchStart = getPatchStart(patchNum);
		return EnsoniqVFXSingleDriver.getPatchName(((Patch)p).sysex, oPatchStart);
	}


	/**
	 *  Sets the patchName attribute of the EnsoniqVFXBankDriver object
	 *
	 *@param  p         The new patchName value
	 *@param  patchNum  The new patchName value
	 *@param  name      The new patchName value
	 */
	public void setPatchName(Patch p, int patchNum, String name)
	{
		int oPatchStart = getPatchStart(patchNum);
		EnsoniqVFXSingleDriver.setPatchName(((Patch)p).sysex, name, oPatchStart);
	}


	public void calculateChecksum(Patch p, int start, int end, int ofs)
	{
	}

	public void calculateChecksum(Patch p)
	{
	}


	/**
	 *  Description of the Method
	 *
	 *@param  bank      Description of the Parameter
	 *@param  p         Description of the Parameter
	 *@param  patchNum  Description of the Parameter
	 */
	public void putPatch(Patch bank, Patch p, int patchNum)
	{
		if (!canHoldPatch(p))
		{
			JOptionPane.showMessageDialog(null, "This type of patch does not fit in to this type of bank.", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		System.arraycopy(((Patch)p).sysex, 6, ((Patch)bank).sysex, getPatchStart(patchNum), EnsoniqVFXSingleDriver.PATCH_SIZE);
	}


	/**
	 *  Gets an individual patch from the bank
	 *
	 *@param  bank      the bank object
	 *@param  patchNum  number of patch in the bank
	 *@return           The patch 
	 */
	public Patch getPatch(Patch bank, int patchNum)
	{
		try
		{
			return EnsoniqVFXSingleDriver.newPatch(((Patch)bank).sysex, getPatchStart(patchNum));
		}
		catch (Exception e)
		{
			ErrorMsg.reportError("Error", "Error in VFX Bank Driver", e);
			return null;
		}
	}


	/**
	 *  Bank factory
	 *
	 * @return    the new "empty" bank
	 */
	public Patch createNewPatch()
	{
		byte[] sysex = new byte[BANK_AND_HEADER_SIZE];
		sysex[0] = (byte) 0xF0;
		sysex[1] = (byte) 0x0F;
		sysex[2] = (byte) 0x05;
		sysex[3] = (byte) 0x00;
		sysex[4] = (byte) 0x00;
		sysex[5] = (byte) 0x03;
		sysex[BANK_AND_HEADER_SIZE-1] = (byte) 0xF7;
		Patch p = new Patch(sysex, this);
		for (int i = 0; i < BANK_NB_PATCHES; i++)
		{
			setPatchName(p, i, "NEWSND");
		}
		return p;
	}

}

