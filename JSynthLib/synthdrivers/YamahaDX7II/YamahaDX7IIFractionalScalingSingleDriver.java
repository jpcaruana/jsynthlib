/*
 * JSynthlib - "Fractional Scaling" Single Driver for Yamaha DX7-II
 * ================================================================
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
import synthdrivers.YamahaDX7.common.DX7FamilyDevice;
import synthdrivers.YamahaDX7.common.DX7FamilyFractionalScalingSingleDriver;
import core.IPatch;
import core.JSLFrame;

public class YamahaDX7IIFractionalScalingSingleDriver extends DX7FamilyFractionalScalingSingleDriver
{
	public YamahaDX7IIFractionalScalingSingleDriver()
	{
		super ( YamahaDX7IIFractionalScalingConstants.INIT_FRACTIONAL_SCALING,
			YamahaDX7IIFractionalScalingConstants.SINGLE_FRACTIONAL_SCALING_PATCH_NUMBERS,
			YamahaDX7IIFractionalScalingConstants.SINGLE_FRACTIONAL_SCALING_BANK_NUMBERS
		);
	}


	public IPatch createNewPatch()
	{
		return super.createNewPatch();
	}

	
	public JSLFrame editPatch(IPatch p)
	{
		return super.editPatch(p);
	}


	public void storePatch (IPatch p, int bankNum,int patchNum)
	{
		if( ( ((DX7FamilyDevice)(getDevice())).getTipsMsgFlag() & 0x01) == 1 )
			// show Information
			YamahaDX7IIStrings.dxShowInformation(toString(), YamahaDX7IIStrings.STORE_SINGLE_FRACTIONAL_SCALING_STRING);

		sendPatchWorker (p);
	}


	public void requestPatchDump(int bankNum, int patchNum)
	{
		if( ( ((DX7FamilyDevice)(getDevice())).getTipsMsgFlag() & 0x01) == 1 )
			// show Information
			YamahaDX7IIStrings.dxShowInformation(toString(), YamahaDX7IIStrings.FRACTIONAL_SCALING_CARTRIDGE_STRING);

		// keyswitch to voice mode
		YamahaDX7IISysexHelpers.chVoiceMode(this, (byte)(getChannel()+0x10));
		// 0-63 int voices, 64-127 cartridge voices
		setPatchNum(patchNum+32*bankNum);

		send(sysexRequestDump.toSysexMessage(getChannel()+0x20));
	}
}
