/*
 * JSynthlib - "Fractional Scaling" Bank Driver for Yamaha DX7s
 * =====================================================================
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
package synthdrivers.YamahaDX7s;
import synthdrivers.YamahaDX7.common.DX7FamilyDevice;
import synthdrivers.YamahaDX7.common.DX7FamilyFractionalScalingBankDriver;
import core.Patch;

public class YamahaDX7sFractionalScalingBankDriver extends DX7FamilyFractionalScalingBankDriver
{
	public YamahaDX7sFractionalScalingBankDriver()
	{
		super ( YamahaDX7sFractionalScalingConstants.INIT_FRACTIONAL_SCALING,
			YamahaDX7sFractionalScalingConstants.BANK_FRACTIONAL_SCALING_PATCH_NUMBERS,
			YamahaDX7sFractionalScalingConstants.BANK_FRACTIONAL_SCALING_BANK_NUMBERS
		);
	}


	public Patch createNewPatch()
	{
		return super.createNewPatch();
	}


	public void storePatch (Patch p, int bankNum,int patchNum)
	{
		if( ( ((DX7FamilyDevice)(getDevice())).getTipsMsgFlag() & 0x01) == 1 )
			// show Information
			YamahaDX7sStrings.dxShowInformation(toString(), YamahaDX7sStrings.FRACTIONAL_SCALING_CARTRIDGE_STRING);

		if ( ( ((DX7FamilyDevice)(getDevice())).getSwOffMemProtFlag() & 0x01) == 1 ) {
			// switch off memory protection (internal+cartridge!)
			YamahaDX7sSysexHelpers.swOffMemProt(this, (byte)(getChannel()+0x10), (byte)0 );
		} else {
			if( ( ((DX7FamilyDevice)(getDevice())).getTipsMsgFlag() & 0x01) == 1 )
				// show Information
			YamahaDX7sStrings.dxShowInformation(toString(), YamahaDX7sStrings.MEMORY_PROTECTION_STRING);
		}
		
		// choose the desired MIDI Receive block ((1-32), (33-64))
		YamahaDX7sSysexHelpers.chRcvBlock(this, (byte)(getChannel()+0x10), (byte)(bankNum));

		sendPatchWorker(p);
	};


	public void requestPatchDump(int bankNum, int patchNum)
	{
		if( ( ((DX7FamilyDevice)(getDevice())).getTipsMsgFlag() & 0x01) == 1 )
			// show Information
			YamahaDX7sStrings.dxShowInformation(toString(), YamahaDX7sStrings.FRACTIONAL_SCALING_CARTRIDGE_STRING);

		// choose the desired MIDI transmit block (internal (1-32), internal (33-64))
		YamahaDX7sSysexHelpers.chXmitBlock(this, (byte)(getChannel()+0x10), (byte)(bankNum));

		send(sysexRequestDump.toSysexMessage(getChannel()+0x20));
	}
}
