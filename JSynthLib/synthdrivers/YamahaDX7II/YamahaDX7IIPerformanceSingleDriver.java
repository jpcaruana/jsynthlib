/*
 * JSynthlib - "Performance" Single Driver for Yamaha DX7-II
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
package synthdrivers.YamahaDX7II;
import	synthdrivers.YamahaDX7.common.DX7FamilyDevice;
import	synthdrivers.YamahaDX7.common.DX7FamilyPerformanceIISingleDriver;
import core.*;
import javax.swing.*;

public class YamahaDX7IIPerformanceSingleDriver extends DX7FamilyPerformanceIISingleDriver
{
	public YamahaDX7IIPerformanceSingleDriver()
	{
		super ( YamahaDX7IIPerformanceConstants.INIT_PERFORMANCE,
			YamahaDX7IIPerformanceConstants.SINGLE_PERFORMANCE_PATCH_NUMBERS,
			YamahaDX7IIPerformanceConstants.SINGLE_PERFORMANCE_BANK_NUMBERS
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
			YamahaDX7IIStrings.dxShowInformation(toString(), YamahaDX7IIStrings.STORE_SINGLE_PERFORMANCE_STRING);
	}
}
