/*
 * JSynthlib - "Additional Voice" Single Driver for Yamaha DX7s
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
package synthdrivers.YamahaDX7s;
import	synthdrivers.YamahaDX7.common.DX7FamilyDevice;
import	synthdrivers.YamahaDX7.common.DX7FamilyAdditionalVoiceSingleDriver;
import core.*;
import javax.swing.*;

public class YamahaDX7sAdditionalVoiceSingleDriver extends DX7FamilyAdditionalVoiceSingleDriver
{
	public YamahaDX7sAdditionalVoiceSingleDriver()
	{
		super ( YamahaDX7sAdditionalVoiceConstants.INIT_ADDITIONAL_VOICE,
			YamahaDX7sAdditionalVoiceConstants.SINGLE_ADDITIONAL_VOICE_PATCH_NUMBERS,
			YamahaDX7sAdditionalVoiceConstants.SINGLE_ADDITIONAL_VOICE_BANK_NUMBERS
		);
	}


	public Patch createNewPatch()
	{
		return super.createNewPatch();
	}


	public JInternalFrame editPatch(Patch p)
	{
		return super.editPatch(p);
	}


	public void storePatch (Patch p, int bankNum,int patchNum)
	{
		sendPatchWorker (p);

		if( ( ((DX7FamilyDevice)(getDevice())).getTipsMsgFlag() & 0x01) == 1 )
			// show Information
			YamahaDX7sStrings.dxShowInformation(toString(), YamahaDX7sStrings.STORE_SINGLE_ADDITIONAL_VOICE_STRING);
	}


	public void requestPatchDump(int bankNum, int patchNum)
	{
		// keyswitch to voice mode
		YamahaDX7sSysexHelpers.chVoiceMode(getPort(), (byte)(getChannel()+0x10));
		// 0-63 int voices, 64-127 cartridge voices
		setPatchNum(patchNum+32*bankNum);

		sysexRequestDump.send(getPort(), (byte)(getChannel()+0x20) );
	}
}
