/*
 * JSynthlib - "Performance" Single Driver for Yamaha DX7 Mark-I
 * =============================================================
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
package org.jsynthlib.drivers.yamaha.dx7;
import org.jsynthlib.core.JSLFrame;
import org.jsynthlib.core.Patch;
import org.jsynthlib.drivers.yamaha.dx7.common.DX7FamilyDevice;
import org.jsynthlib.drivers.yamaha.dx7.common.DX7FamilyPerformanceSingleDriver;


public class YamahaDX7PerformanceSingleDriver extends DX7FamilyPerformanceSingleDriver
{
	public YamahaDX7PerformanceSingleDriver()
	{
		super (	YamahaDX7PerformanceConstants.INIT_PERFORMANCE,
			YamahaDX7PerformanceConstants.SINGLE_PERFORMANCE_PATCH_NUMBERS,
			YamahaDX7PerformanceConstants.SINGLE_PERFORMANCE_BANK_NUMBERS
		);
	}


	public Patch createNewPatch()
	{
		return super.createNewPatch();
	}


	public void storePatch (Patch p, int bankNum,int patchNum)
	{
		if ( ( ((DX7FamilyDevice)(getDevice())).getTipsMsgFlag() & 0x01 ) == 1 )
			// show Information
			YamahaDX7Strings.dxShowInformation(toString(), YamahaDX7Strings.STORE_SINGLE_PERFORMANCE_STRING);

		sendPatchWorker (p);
	}


	public void requestPatchDump(int bankNum, int patchNum)
	{
		if ( ( ((DX7FamilyDevice)(getDevice())).getTipsMsgFlag() & 0x01 ) == 1 )
			// show Information
			YamahaDX7Strings.dxShowInformation(toString(), YamahaDX7Strings.PERFORMANCE_STRING);
	}

	
	public JSLFrame editPatch(Patch p)
	{
		if ( ( ((DX7FamilyDevice)(getDevice())).getSPBPflag() & 0x01 ) == 1 ) {
			// make Sys Info available
			YamahaDX7SysexHelper.mkSysInfoAvail(this, (byte)(getChannel()+0x10));
		} else {
			if ( ( ((DX7FamilyDevice)(getDevice())).getTipsMsgFlag() & 0x01 ) == 1 )
				// show Information
				YamahaDX7Strings.dxShowInformation(toString(), YamahaDX7Strings.PERFORMANCE_EDITOR_STRING);
		}

		return super.editPatch(p);
	}
}
