/*
 * JSynthlib - "Additional Voice" Single Driver Yamaha TX802
 * =========================================================
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
package org.jsynthlib.drivers.yamaha.tx802;
import org.jsynthlib.core.JSLFrame;
import org.jsynthlib.core.Patch;
import org.jsynthlib.drivers.yamaha.dx7.common.DX7FamilyAdditionalVoiceSingleDriver;
import org.jsynthlib.drivers.yamaha.dx7.common.DX7FamilyDevice;


public class YamahaTX802AdditionalVoiceSingleDriver extends DX7FamilyAdditionalVoiceSingleDriver
{
	public YamahaTX802AdditionalVoiceSingleDriver()
	{
		super ( YamahaTX802AdditionalVoiceConstants.INIT_ADDITIONAL_VOICE,
			YamahaTX802AdditionalVoiceConstants.SINGLE_ADDITIONAL_VOICE_PATCH_NUMBERS,
			YamahaTX802AdditionalVoiceConstants.SINGLE_ADDITIONAL_VOICE_BANK_NUMBERS
		);
	}


	public Patch createNewPatch()
	{
		return super.createNewPatch();
	}


	public JSLFrame editPatch(Patch p)
	{
		return super.editPatch(p);
	}


	public void storePatch (Patch p, int bankNum,int patchNum)
	{
		sendPatchWorker (p);

		if( ( ((DX7FamilyDevice)(getDevice())).getTipsMsgFlag() & 0x01) == 1 )
			// show Information
			YamahaTX802Strings.dxShowInformation(toString(), YamahaTX802Strings.STORE_SINGLE_ADDITIONAL_VOICE_STRING);
	}


	public void requestPatchDump(int bankNum, int patchNum)
	{
		// keyswitch to voice mode
		YamahaTX802SysexHelpers.chVoiceMode(this, (byte)(getChannel()+0x10));
		// 0-63 int voices
		setPatchNum(patchNum+32*bankNum);

		send(sysexRequestDump.toSysexMessage(getChannel()+0x20));
	}
}
