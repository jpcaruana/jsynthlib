package synthdrivers.AlesisQS;

import core.*;
import javax.swing.*;
import java.io.*;

/**
 * AlesisQSGlobalDriver.java
 *
 * Global program driver for Alesis QS series synths
 * Feb 2002
 * @author Zellyn Hunter (zjh, zellyn@zellyn.com)
 * @version $Id$
 * GPL v2
 */

public class AlesisQSGlobalDriver extends Driver
{
	public AlesisQSGlobalDriver()
	{
		super ("Global","Zellyn Hunter");
		sysexID="F000000E0E**";
		sysexRequestDump=new SysexHandler("F0 00 00 0E 0E 0B F7");
		//patchSize=350/7*8;
		patchSize=QSConstants.PATCH_SIZE_GLOBAL;
		// zjh - I think this should be 0 so sendPatchWorker doesn't use it
		deviceIDoffset=0;;
		checksumStart=0;
		checksumEnd=0;
		checksumOffset=0;
		//bankNumbers =new String[] {"Internal 1", "Internal 2", "Internal 3", "GenMIDI", "User"};
		bankNumbers = QSConstants.GLOBAL_BANK_NAME_DUMMY;
		patchNumbers = QSConstants.GLOBAL_PATCH_NUMBERS_DUMMY;
	}

	/**
	 * Print a byte in binary, for debugging packing/unpacking code
	 **/
	public String toBinaryStr(byte b) {
		String output = new String();
		for(int i=7; i>=0 ; i--) {
			output += ( (b>>i) & 1);
		}
		return output;
	}

	/**
	 * Override the checksum and do nothing - the Alesis does not use checksums
	 * @param p the ignored
	 * @param start ignored
	 * @param end ignored
	 * @param ofs ignored
	 */
	public void calculateChecksum(Patch p,int start,int end,int ofs)
	{
		//This synth does not use a checksum
	}

	/**
	 * Create a new global patch
	 * @return the new Patch
	 */
	public Patch createNewPatch()
	{
		// Copy over the Alesis QS header
		byte [] sysex = new byte[patchSize];
		for (int i=0; i<QSConstants.GENERIC_HEADER.length; i++)
		{
			sysex[i] = QSConstants.GENERIC_HEADER[i];
		}
		// Set it to be a global
		sysex[QSConstants.POSITION_OPCODE] = QSConstants.OPCODE_MIDI_GLOBAL_DATA_DUMP;

		// Create the patch, and set the name
		Patch p = new Patch(sysex, this);
		return p;
	}

	public JSLFrame editPatch(Patch p)
	{
		return new GlobalEditor(p);
	}



	/**
	 * Copied from Driver.java by zjh.  Requests a global data dump.
	 * Use opcode 0B - MIDI Global Data Dump Request.
	 * @param bankNum not used
	 * @param patchNum not used
	 */
	public void requestPatchDump(int bankNum, int patchNum)
	{
		//setBankNum(bankNum);
		//setPatchNum(patchNum);

		sysexRequestDump.send( getPort(), (byte)getChannel() );
	}


	/**
	 * Sends a patch to the synth's global data buffer.
	 * @param p the patch to send to the global data area
	 */
	public void sendPatch (Patch p)
	{
		storePatch(p, 0, 0);
	}
  
	/**
	 * Sends global data.  Location is irrelevant
	 * @param p the patch to send
	 * @param bankNum ignored
	 * @param patchNum ignored
	 */
	public void storePatch (Patch p, int bankNum, int patchNum)
	{
		byte opcode = QSConstants.OPCODE_MIDI_GLOBAL_DATA_DUMP;
		byte oldOpcode = p.sysex[QSConstants.POSITION_OPCODE];

		// set the opcode
		p.sysex[QSConstants.POSITION_OPCODE] = opcode;

		ErrorMsg.reportStatus("foo", p.sysex);
		//setBankNum (bankNum);
		//setPatchNum (patchNum);

		// actually send the patch
		sendPatchWorker (p);

		// restore the old values
		p.sysex[QSConstants.POSITION_OPCODE] = oldOpcode;
	}
}
