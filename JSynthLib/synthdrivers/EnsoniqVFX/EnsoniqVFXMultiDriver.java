package synthdrivers.EnsoniqVFX;
import core.*;
import javax.swing.*;
import java.io.*;

/**
 *  Multi driver for VFX. Multi have no name.
 * The message type seems to be 0B instead of 06 as the doc says. The size is
 * also changed. 
 *@author     Denis Queffeulou mailto:dqueffeulou@free.fr
 *@created    17 septembre 2002
 */
public class EnsoniqVFXMultiDriver extends Driver
{
	/** size of patch without header */
	static final int SIZE = 574;
	
	/**
	 *  Constructor for the EnsoniqVFXSingleDriver object
	 */
	public EnsoniqVFXMultiDriver()
	{
		manufacturer = "Ensoniq";
		model = "VFX";
		patchType = "Multi";
		id = "VFX";
		authors="Denis Queffeulou";
		sysexID = "F00F0500**0B";
		patchSize = SIZE+7;
		deviceIDoffset = 4;
		checksumStart = 0;
		checksumEnd = 0;
		checksumOffset = 0;
		sysexRequestDump=new SysexHandler("F0 0F 05 00 00 00 00 07 F7");
		bankNumbers = new String[]{"0-Internal"};//, "1-Cart A", "2-Cart B"};
		patchNumbers = new String[]{"Multi set"};

	}



	/**
	 *  Description of the Method
	 *
	 *@param  p  Description of the Parameter
	 */
	public void choosePatch(Patch p)
	{
		storePatch(p, 0, 0);
	}



	/**
	 *  Description of the Method
	 *
	 *@param  p         Description of the Parameter
	 *@param  bankNum   Description of the Parameter
	 *@param  patchNum  Description of the Parameter
	 */
	public void storePatch(Patch p, int bankNum, int patchNum)
	{
		sendPatchWorker(p);
	}


	/**
	 *  Description of the Method
	 *
	 *@param  p  Description of the Parameter
	 */
	public void sendPatch(Patch p)
	{
		sendPatchWorker(p);
	}


	public void calculateChecksum(Patch p, int start, int end, int ofs)
	{
		//This synth does not use a checksum
	}


	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	public Patch createNewPatch()
	{
		byte[] sysex = new byte[SIZE+7];
		sysex[0] = (byte) 0xF0;
		sysex[1] = (byte) 0x0F;
		sysex[2] = (byte) 0x05;
		sysex[3] = (byte) 0x00;
		sysex[4] = (byte) 0x00;
		sysex[5] = (byte) 0x0B;	
		sysex[SIZE+6] = (byte) 0xF7;
		Patch p = new Patch(sysex);
		p.ChooseDriver();
		return p;
	}


	/**
	 *  Description of the Method
	 *
	 *@param  p  Description of the Parameter
	 *@return    Description of the Return Value
	 */
	public JSLFrame editPatch(Patch p)
	{
		return null;
	}
}

