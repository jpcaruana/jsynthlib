/*
 * JSynthlib - "Performance" Bank Driver for Yamaha DX7 MK1
 * ========================================================
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
package synthdrivers.YamahaDX7;
import org.jsynthlib.core.JSLFrame;
import org.jsynthlib.core.Patch;

import synthdrivers.YamahaDX7.common.DX7FamilyDevice;
import synthdrivers.YamahaDX7.common.DX7FamilyPerformanceBankDriver;

public class YamahaDX7PerformanceBankDriver extends DX7FamilyPerformanceBankDriver
{
	public YamahaDX7PerformanceBankDriver()
	{
		super (	YamahaDX7PerformanceConstants.INIT_PERFORMANCE,
			YamahaDX7PerformanceConstants.BANK_PERFORMANCE_PATCH_NUMBERS,
			YamahaDX7PerformanceConstants.BANK_PERFORMANCE_BANK_NUMBERS
		);
	}


	public Patch createNewPatch()
	{
		return super.createNewPatch();
	}
		
	
	/* Only dummy methods, because the DX7 doesn't support Performance banks */
	public void storePatch (Patch p, int bankNum,int patchNum)
	{
		if ( ( ((DX7FamilyDevice)(getDevice())).getTipsMsgFlag() & 0x01 ) == 1 )
			// show Information
			YamahaDX7Strings.dxShowInformation(toString(), YamahaDX7Strings.PERFORMANCE_STRING);
	}

	public void requestPatchDump(int bankNum, int patchNum)
	{
		if ( ( ((DX7FamilyDevice)(getDevice())).getTipsMsgFlag() & 0x01 ) == 1 )
			// show Information 
			YamahaDX7Strings.dxShowInformation(toString(), YamahaDX7Strings.PERFORMANCE_STRING);
	}


	public JSLFrame editPatch(Patch p)
	{
		if ( ( ((DX7FamilyDevice)(getDevice())).getTipsMsgFlag() & 0x01 ) == 1 )
			// show Information 
			YamahaDX7Strings.dxShowInformation(toString(), YamahaDX7Strings.PERFORMANCE_STRING);

		return super.editPatch(p);
	}
  
}
