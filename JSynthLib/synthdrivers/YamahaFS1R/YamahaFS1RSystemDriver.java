package synthdrivers.YamahaFS1R;

import core.*;
import java.io.*;

/**
 *  System parameters driver for Yamaha FS1R.
 *
 * @author     Denis Queffeulou mailto:dqueffeulou@free.fr
 * @version    $Id$
 */
public class YamahaFS1RSystemDriver extends Driver
{
	/** size of patch without header */
	static final int PATCH_SIZE = 76;
	
	/** size of header begin + end */
	static final int HEADER_SIZE = 11;
	
	static final int DATA_OFFSET = 9;
	
	/** size of all */
	static final int PATCH_AND_HEADER_SIZE = PATCH_SIZE+HEADER_SIZE;
	
	/**
	 *  Constructor for the YamahaFS1RSystemDriver object
	 */
	public YamahaFS1RSystemDriver()
	{
		super ("System","Denis Queffeulou");
		sysexID = "F043005E004C";
//   inquiryID="F07E**06020F0200*************F7";
		patchSize = PATCH_AND_HEADER_SIZE;
		patchNameSize = 0;
		deviceIDoffset = 2;
		checksumStart = 4;
		checksumEnd = PATCH_AND_HEADER_SIZE - 3;
		checksumOffset = PATCH_AND_HEADER_SIZE - 2;
		sysexRequestDump=new SysexHandler("F0 43 20 5E 00 00 00 F7");
	}



	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	public Patch createNewPatch()
	{
		return newPatch();
	}
	
	/**
		Patch factory, static because no context is needed.
	*/
	static Patch newPatch()
	{
		byte[] sysex = new byte[PATCH_AND_HEADER_SIZE];
		sysex[0] = (byte) 0xF0;
		sysex[1] = (byte) 0x43;
		sysex[2] = (byte) 0x00;
		sysex[3] = (byte) 0x5E;
		sysex[4] = (byte) (PATCH_AND_HEADER_SIZE / 256);
		sysex[5] = (byte) (PATCH_AND_HEADER_SIZE % 256);
		sysex[6] = (byte) 0x00;
		sysex[7] = (byte) 0x00;	
		sysex[8] = (byte) 0x00;	
		sysex[PATCH_AND_HEADER_SIZE-1] = (byte) 0xF7;
		Patch oPatch = new Patch(sysex);
		return oPatch;
	}

	/**
	 *  Create individual patch from the sysex.
	 *
	 * @param aOffset offset in the sysex
	 * @return    the patch
	 */
	static Patch newPatch(byte aSysex[], int aOffset)
	{
		Patch oNewPatch = newPatch();
		System.arraycopy(aSysex, aOffset, oNewPatch.sysex, HEADER_SIZE-1, PATCH_SIZE);
		return oNewPatch;
	}	

	/**
	 *  Description of the Method
	 *
	 *@param  p  Description of the Parameter
	 *@return    Description of the Return Value
	 */
	public JSLFrame editPatch(Patch p)
	{
		return new YamahaFS1RSystemEditor(p);
	}
}

