/*
 * JSynthlib - "Performance" Single Driver for Yamaha TX802
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
package org.jsynthlib.drivers.yamaha.tx802;
import org.jsynthlib.core.JSLFrame;
import org.jsynthlib.core.Patch;
import org.jsynthlib.drivers.yamaha.dx7.common.DX7FamilyPerformanceIIISingleDriver;


public class YamahaTX802PerformanceSingleDriver extends DX7FamilyPerformanceIIISingleDriver
{
	public YamahaTX802PerformanceSingleDriver()
	{
		super ( YamahaTX802PerformanceConstants.INIT_PERFORMANCE,
			YamahaTX802PerformanceConstants.SINGLE_PERFORMANCE_PATCH_NUMBERS,
			YamahaTX802PerformanceConstants.SINGLE_PERFORMANCE_BANK_NUMBERS
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

		if ( ((YamahaTX802Device) getDevice()).getTipsMsgFlag()==1 )
			// show Information 
			YamahaTX802Strings.dxShowInformation(toString(), YamahaTX802Strings.STORE_SINGLE_PERFORMANCE_STRING);
	}
}
