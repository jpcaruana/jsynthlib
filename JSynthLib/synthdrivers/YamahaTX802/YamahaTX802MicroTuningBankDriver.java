/*
 * JSynthlib - "Micro Tuning" Bank Driver for Yamaha TX802
 * =======================================================
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
package synthdrivers.YamahaTX802;
import synthdrivers.YamahaDX7.common.DX7FamilyDevice;
import synthdrivers.YamahaDX7.common.DX7FamilyMicroTuningBankDriver;
import core.IPatch;

public class YamahaTX802MicroTuningBankDriver extends DX7FamilyMicroTuningBankDriver
{
	public YamahaTX802MicroTuningBankDriver()
	{
		super ( YamahaTX802MicroTuningConstants.INIT_MICRO_TUNING,
			YamahaTX802MicroTuningConstants.BANK_MICRO_TUNING_PATCH_NUMBERS,
			YamahaTX802MicroTuningConstants.BANK_MICRO_TUNING_BANK_NUMBERS
		);
	}


	public IPatch createNewPatch()
	{
		return super.createNewPatch();
	}


	public void storePatch (IPatch p, int bankNum,int patchNum)
	{
		if( ( ((DX7FamilyDevice)(getDevice())).getTipsMsgFlag() & 0x01) == 1 )
			// show Information
			YamahaTX802Strings.dxShowInformation(toString(), YamahaTX802Strings.MICRO_TUNING_CARTRIDGE_STRING);

		if( ( ((DX7FamilyDevice)(getDevice())).getSwOffMemProtFlag() & 0x01) == 1 ) {
			// switch off memory protection
			YamahaTX802SysexHelpers.swOffMemProt(this, (byte)(getChannel()+0x10) );
		} else {
			if( ( ((DX7FamilyDevice)(getDevice())).getTipsMsgFlag() & 0x01) == 1 )
				// show Information
				YamahaTX802Strings.dxShowInformation(toString(), YamahaTX802Strings.MEMORY_PROTECTION_STRING);
		}

		sendPatchWorker(p);
	};


	public void requestPatchDump(int bankNum, int patchNum)
	{
		if( ( ((DX7FamilyDevice)(getDevice())).getTipsMsgFlag() & 0x01) == 1 )
			// show Information
			YamahaTX802Strings.dxShowInformation(toString(), YamahaTX802Strings.MICRO_TUNING_CARTRIDGE_STRING);

		send(sysexRequestDump.toSysexMessage(getChannel()+0x20));
	}
}
