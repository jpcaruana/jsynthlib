/*
 * JSynthlib - "Additional Voice" Bank Driver for Yamaha DX7-II
 * ============================================================
 * @version $Id$
 * @author  Torsten Tittmann
 *
 * Copyright (C) 2002-2004 Torsten.Tittmann@gmx.de
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
package synthdrivers.YamahaDX7II;
import synthdrivers.YamahaDX7.common.DX7FamilyAdditionalVoiceBankDriver;
import synthdrivers.YamahaDX7.common.DX7FamilyDevice;
import core.Patch;

public class YamahaDX7IIAdditionalVoiceBankDriver extends DX7FamilyAdditionalVoiceBankDriver
{
	public YamahaDX7IIAdditionalVoiceBankDriver()
	{
		super ( YamahaDX7IIAdditionalVoiceConstants.INIT_ADDITIONAL_VOICE,
			YamahaDX7IIAdditionalVoiceConstants.BANK_ADDITIONAL_VOICE_PATCH_NUMBERS,
			YamahaDX7IIAdditionalVoiceConstants.BANK_ADDITIONAL_VOICE_BANK_NUMBERS
		);
	}

	
	public Patch createNewPatch()
	{
		return super.createNewPatch();
	}
		

	public void storePatch (Patch p, int bankNum,int patchNum)
	{
		if ( ( ((DX7FamilyDevice)(getDevice())).getSwOffMemProtFlag() & 0x01) == 1 ) {
			// switch off memory protection (internal+cartridge!)
			YamahaDX7IISysexHelpers.swOffMemProt(this, (byte)(getChannel()+0x10), (byte)0 );
		} else {
			if( ( ((DX7FamilyDevice)(getDevice())).getTipsMsgFlag() & 0x01) == 1 )
				// show Information
				YamahaDX7IIStrings.dxShowInformation(toString(), YamahaDX7IIStrings.MEMORY_PROTECTION_STRING);
		}

		// choose the desired MIDI Receive block (internal (1-32), internal (33-64))
		YamahaDX7IISysexHelpers.chRcvBlock(this, (byte)(getChannel()+0x10), (byte)(bankNum));

		sendPatchWorker(p);
	};


	public void requestPatchDump(int bankNum, int patchNum)
	{
		// choose the desired MIDI transmit block (internal (1-32), internal (33-64))
		YamahaDX7IISysexHelpers.chXmitBlock(this, (byte)(getChannel()+0x10), (byte)(bankNum));
      
		send(sysexRequestDump.toSysexMessage(getChannel()+0x20));
	}

}
