/*
 * JSynthlib -	generic "Performance" Single Driver for Yamaha DX7 Family
 * (used by DX1, DX5, DX7 MKI, TX7, TX816)
 * ======================================================================
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
package synthdrivers.YamahaDX7.common;
import org.jsynthlib.core.Driver;
import org.jsynthlib.core.JSLFrame;
import org.jsynthlib.core.Patch;
import org.jsynthlib.core.SysexHandler;

public class DX7FamilyPerformanceSingleDriver extends Driver
{
	byte[]		initSysex;
	String[]	dxPatchNumbers;
	String[]	dxBankNumbers;
	 
	public DX7FamilyPerformanceSingleDriver(byte[] initSysex, String[] dxPatchNumbers, String[] dxBankNumbers)
	{
		super ("Single Performance", "Torsten Tittmann");

		this.initSysex		= initSysex;
		this.dxPatchNumbers	= dxPatchNumbers;
		this.dxBankNumbers	= dxBankNumbers;
								
		sysexID= "F0430*01005E";
		patchNameStart=70;
		patchNameSize=30;
		deviceIDoffset=2;
		checksumOffset=100;
		checksumStart=6;
		checksumEnd=99;
		patchNumbers = dxPatchNumbers;
		bankNumbers  = dxBankNumbers;
		patchSize=102;
		trimSize=102;
		numSysexMsgs=1;		
		sysexRequestDump=new SysexHandler("F0 43 @@ 01 F7");
	}


	public Patch createNewPatch()
	{
		return new Patch(initSysex, this);
	}


	public JSLFrame editPatch(Patch p)
	{
		return new DX7FamilyPerformanceEditor(getManufacturerName()+" "+getModelName()+" \""+getPatchType()+"\" Editor", (Patch)p);
	}
}
