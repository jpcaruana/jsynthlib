/*
 * JSynthlib - "Micro Tuning" Bank Driver for Yamaha DX7s
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
package org.jsynthlib.drivers.yamaha.dx7s;
import org.jsynthlib.core.Patch;
import org.jsynthlib.drivers.yamaha.dx7.common.DX7FamilyDevice;
import org.jsynthlib.drivers.yamaha.dx7.common.DX7FamilyMicroTuningBankDriver;


public class YamahaDX7sMicroTuningBankDriver extends DX7FamilyMicroTuningBankDriver
{
	public YamahaDX7sMicroTuningBankDriver()
	{
		super ( YamahaDX7sMicroTuningConstants.INIT_MICRO_TUNING,
			YamahaDX7sMicroTuningConstants.BANK_MICRO_TUNING_PATCH_NUMBERS,
			YamahaDX7sMicroTuningConstants.BANK_MICRO_TUNING_BANK_NUMBERS
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
			YamahaDX7sStrings.dxShowInformation(toString(), YamahaDX7sStrings.MICRO_TUNING_CARTRIDGE_STRING);

		if ( ( ((DX7FamilyDevice)(getDevice())).getSwOffMemProtFlag() & 0x01) == 1 ) {
			// switch off memory protection (internal+cartridge!)
			YamahaDX7sSysexHelpers.swOffMemProt(this, (byte)(getChannel()+0x10), (byte)0 );
		} else {
			if( ( ((DX7FamilyDevice)(getDevice())).getTipsMsgFlag() & 0x01) == 1 )
				// show Information
				YamahaDX7sStrings.dxShowInformation(toString(), YamahaDX7sStrings.MEMORY_PROTECTION_STRING);
		}

		sendPatchWorker(p);
	};


	public void requestPatchDump(int bankNum, int patchNum)
	{
		if( ( ((DX7FamilyDevice)(getDevice())).getTipsMsgFlag() & 0x01) == 1 )
			// show Information
			YamahaDX7sStrings.dxShowInformation(toString(), YamahaDX7sStrings.MICRO_TUNING_CARTRIDGE_STRING);

		send(sysexRequestDump.toSysexMessage(getChannel()+0x20));
	}
}
