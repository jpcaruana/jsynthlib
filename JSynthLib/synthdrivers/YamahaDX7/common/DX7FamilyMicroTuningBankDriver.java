/*
 * JSynthlib -	generic "Micro Tuning" Bank Driver for Yamaha DX7 Family
 *		(used by DX7-II, DX7s, TX802)
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
package synthdrivers.YamahaDX7.common;
import core.*;
import java.io.*;
import javax.swing.*;

public class DX7FamilyMicroTuningBankDriver extends BankDriver
{
	byte[]		initSysex;
	String[]	dxPatchNumbers;
	String[]	dxBankNumbers;

	private static final int	dxSinglePackedSize	= 269;	// size of single patch in packed bank format
	private static final int	dxSysexHeaderSize	= 4;	// length of sysex header

	
	public DX7FamilyMicroTuningBankDriver(byte[] initSysex, String[] dxPatchNumbers, String[] dxBankNumbers)
	{
		super ("Micro Tuning Bank", "Torsten Tittmann", dxPatchNumbers.length, 7);

		this.initSysex	       = initSysex;
		this.dxPatchNumbers	= dxPatchNumbers;
		this.dxBankNumbers	= dxBankNumbers;

		sysexID="F0430*7e020a4c4d20204d4352594320";
		sysexRequestDump=new SysexHandler("f0 43 @@ 7e 4c 4d 20 20	4d 43 52 59 43 20 f7 ");
		deviceIDoffset=2;
		patchNameStart=0;
		patchNameSize=0;
		bankNumbers  = dxBankNumbers;
		patchNumbers = dxPatchNumbers;
		singleSysexID="F0430*7E020A4C4D20204d4352594***";
		singleSize=274;
		//checksumOffset=16950;	// This patch doesn't uses an over-all checksum for bank bulk data
		//checksumStart=6;
		//checksumEnd=16949;
		numSysexMsgs=1;
		trimSize=16952;
	}

	
	public void calculateChecksum(Patch p)
	{
		// This patch doesn't uses an over-all checksum for bank bulk data
	}


	public int getPatchStart(int patchNum)
	{
		return (dxSinglePackedSize*patchNum)+dxSysexHeaderSize;
	}


	public void putPatch(Patch bank,Patch p,int patchNum)		//puts a patch into the bank, converting it as needed
	{
		if (!canHoldPatch(p)) {
			DX7FamilyStrings.dxShowError(getDriverName(), "This type of patch does not fit in to this type of bank.");
			return;
		}

		// Transform Voice Data to Bulk Dump Packed Format
		bank.sysex[getPatchStart(patchNum)+ 0	]=(byte)(0x02);	// Byte Count MSB
		bank.sysex[getPatchStart(patchNum)+ 1	]=(byte)(0x0a);	// Byte Count LSB
		bank.sysex[getPatchStart(patchNum)+ 2	]=(byte)(0x4c);	// "L"
		bank.sysex[getPatchStart(patchNum)+ 3	]=(byte)(0x4d);	// "M"
		bank.sysex[getPatchStart(patchNum)+ 4	]=(byte)(0x20);	// " "
		bank.sysex[getPatchStart(patchNum)+ 5	]=(byte)(0x20);	// " "
		bank.sysex[getPatchStart(patchNum)+ 6	]=(byte)(0x4d);	// "M"
		bank.sysex[getPatchStart(patchNum)+ 7	]=(byte)(0x43);	// "C"
		bank.sysex[getPatchStart(patchNum)+ 8	]=(byte)(0x52);	// "R"
		bank.sysex[getPatchStart(patchNum)+ 9	]=(byte)(0x59);	// "Y"
		bank.sysex[getPatchStart(patchNum)+10	]=(byte)(0x43);	// "C"
		bank.sysex[getPatchStart(patchNum)+11	]=(byte)(0x20);	// " "


		for (int i=0; i<256; i++) {
			bank.sysex[getPatchStart(patchNum)+12+i]=(byte)p.sysex[16+i];
		}

		// Calculate checkSum of single bulk data
		calculateChecksum(bank,getPatchStart(patchNum)+2,getPatchStart(patchNum)+12+256-1,getPatchStart(patchNum)+12+256);
	}


	public Patch getPatch(Patch bank, int patchNum)			//Gets a patch from the bank, converting it as needed
	{
		try {
			byte [] sysex=new byte[singleSize];

			// transform bulk-dump-packed-format to voice data (Edit Buffer!)
			sysex[	0]=(byte)0xf0;
			sysex[	1]=(byte)0x43;
			sysex[	2]=(byte)0x00;
			sysex[	3]=(byte)0x7e;
			sysex[	4]=(byte)0x02;
			sysex[	5]=(byte)0x0a;
			sysex[	6]=(byte)0x4c;	// "L"
			sysex[	7]=(byte)0x4d;	// "M"
			sysex[	8]=(byte)0x20;	// " "
			sysex[	9]=(byte)0x20;	// " "
			sysex[ 10]=(byte)0x4d;	// "M"
			sysex[ 11]=(byte)0x43;	// "C"
			sysex[ 12]=(byte)0x52;	// "R"
			sysex[ 13]=(byte)0x59;	// "Y"
			sysex[ 14]=(byte)0x45;	// "E"
			sysex[ 15]=(byte)0x20;	// " "
			sysex[singleSize-1]=(byte)0xf7;


			for (int i=0; i<256; i++) {
				sysex[16+i]=(byte)(bank.sysex[getPatchStart(patchNum)+12+i]);
			}

			Patch p = new Patch(sysex, getDevice());	// single sysex
			p.getDriver().calculateChecksum(p);

			return p;
		} catch (Exception e) {
			ErrorMsg.reportError(getManufacturerName()+" "+getModelName(),"Error in "+getDriverName(),e);return null;
		}
	}


	public Patch createNewPatch()		// create a bank with 64 micro tuning patches
	{
		byte [] sysex = new byte[trimSize];
		
		sysex[ 0]=(byte)0xF0;
		sysex[ 1]=(byte)0x43;
		sysex[ 2]=(byte)0x00;
		sysex[ 3]=(byte)0x7e;
		sysex[trimSize-1]=(byte)0xF7;

		Patch v = new Patch(initSysex, getDevice());	// single sysex
		Patch p = new Patch(sysex,     this);		// bank sysex

		for (int i=0;i<getNumPatches();i++)
			putPatch(p,v,i);

		return p;
	}

}
