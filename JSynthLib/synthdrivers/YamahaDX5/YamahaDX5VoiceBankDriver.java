/*
 * JSynthlib - "Voice" Bank Driver for Yamaha DX5
 * ==============================================
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
package synthdrivers.YamahaDX5;
import synthdrivers.YamahaDX7.common.DX7FamilyDevice;
import synthdrivers.YamahaDX7.common.DX7FamilyVoiceBankDriver;
import core.IPatch;

public class YamahaDX5VoiceBankDriver extends DX7FamilyVoiceBankDriver
{
	public YamahaDX5VoiceBankDriver()
	{
		super (	YamahaDX5VoiceConstants.INIT_VOICE,
			YamahaDX5VoiceConstants.BANK_VOICE_PATCH_NUMBERS,
			YamahaDX5VoiceConstants.BANK_VOICE_BANK_NUMBERS
		);
	}

	
	public IPatch createNewPatch()
	{
		return super.createNewPatch();
	}

		
	public void storePatch (IPatch p, int bankNum,int patchNum)
	{
		if( ( ((DX7FamilyDevice)(getDevice())).getTipsMsgFlag() & 0x01 ) == 1 )
			// show Information
			YamahaDX5Strings.dxShowInformation(toString(), YamahaDX5Strings.SELECT_PATCH_STRING);

		sendPatchWorker(p);
	}


	public void requestPatchDump(int bankNum, int patchNum)
	{
		if( ( ((DX7FamilyDevice)(getDevice())).getTipsMsgFlag() & 0x01 ) == 1 )
			// show Information
			YamahaDX5Strings.dxShowInformation(toString(), YamahaDX5Strings.SELECT_PATCH_STRING);

		send(sysexRequestDump.toSysexMessage(getChannel()+0x20));
	}
}
