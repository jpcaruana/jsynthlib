/*
 * JSynthlib -	generic "Voice" Bank Driver for DX7 Family
 * (used by DX1, DX5, DX7 MKI, TX7, TX816, DX7-II, DX7s, TX802)
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
package org.jsynthlib.drivers.yamaha.dx7.common;
import java.io.UnsupportedEncodingException;

import org.jsynthlib.core.BankDriver;
import org.jsynthlib.core.ErrorMsg;
import org.jsynthlib.core.Patch;
import org.jsynthlib.core.SysexHandler;


public class DX7FamilyVoiceBankDriver extends BankDriver
{
	byte[]		initSysex;
	String[]	dxPatchNumbers;
	String[]	dxBankNumbers;

	private static final int	dxPatchNameSize		= 10;	// size of patchname of single patch
	private static final int	dxPatchNameOffset	= 118;	// offset in packed bank format
	private static final int	dxSinglePackedSize	= 128;	// size of single patch in packed bank format
	private static final int	dxSysexHeaderSize	= 6;	// length of sysex header

	
	public DX7FamilyVoiceBankDriver(byte[] initSysex, String[] dxPatchNumbers, String[] dxBankNumbers)
	{
		super ("Voice Bank", "Torsten Tittmann", dxPatchNumbers.length, 4);
   
		this.initSysex		= initSysex;
		this.dxPatchNumbers	= dxPatchNumbers;
		this.dxBankNumbers	= dxBankNumbers;
		
		sysexID="F0430*092000";
		sysexRequestDump=new SysexHandler("F0 43 @@ 09 F7");
		deviceIDoffset=2;
		bankNumbers  = dxBankNumbers;
		patchNumbers = dxPatchNumbers;
		singleSysexID="F0430*00011B";
		singleSize=163;
		checksumOffset=4102;
		checksumStart=6;
		checksumEnd=4101;
		numSysexMsgs=1;
		patchSize=4104;
		trimSize=patchSize;
	}


	public int getPatchStart(int patchNum)
	{
		return (dxSinglePackedSize*patchNum)+dxSysexHeaderSize;
	}

	
	public int getPatchNameStart(int patchNum)
	{
		return getPatchStart(patchNum)+dxPatchNameOffset;
	}


	public String getPatchName(Patch p,int patchNum)
	{
		int nameStart=getPatchNameStart(patchNum);
		
		try {
			StringBuffer s= new StringBuffer(new String(((Patch)p).sysex,nameStart,dxPatchNameSize,"US-ASCII"));
			return s.toString();
		} catch (UnsupportedEncodingException ex) {
			return "-";
		}
	}


	public void setPatchName(Patch p,int patchNum, String name)
	{
		int nameStart=getPatchNameStart(patchNum);

		while (name.length()<dxPatchNameSize)
			name=name+" ";
		
		byte [] namebytes = new byte [dxPatchNameSize];

		try {
			namebytes=name.getBytes("US-ASCII");
			for (int i=0;i<dxPatchNameSize;i++)
				((Patch)p).sysex[nameStart+i]=namebytes[i];

		} catch (UnsupportedEncodingException ex) {
			return;
		}
	}


	public void putPatch(Patch bank,Patch p,int patchNum)		//puts a patch into the bank, converting it as needed
	{
		if (!canHoldPatch(p)) {
			DX7FamilyStrings.dxShowError(toString(), "This type of patch does not fit in to this type of bank.");
			return;
		}

		// Transform Voice Data to Bulk Dump Packed Format
		
		//    ***** OPERATOR 6 *****
		((Patch)bank).sysex[getPatchStart(patchNum)+  0]=(byte)((((Patch)p).sysex[6+  0]));			// EG Rate  1 .................(0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+  1]=(byte)((((Patch)p).sysex[6+  1]));			// EG Rate  2 .................(0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+  2]=(byte)((((Patch)p).sysex[6+  2]));			// EG Rate  3 .................(0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+  3]=(byte)((((Patch)p).sysex[6+  3]));			// EG Rate  4 .................(0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+  4]=(byte)((((Patch)p).sysex[6+  4]));			// EG Level 1 .................(0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+  5]=(byte)((((Patch)p).sysex[6+  5]));			// EG Level 2 .................(0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+  6]=(byte)((((Patch)p).sysex[6+  6]));			// EG Level 3 .................(0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+  7]=(byte)((((Patch)p).sysex[6+  7]));			// EG Level 4 .................(0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+  8]=(byte)((((Patch)p).sysex[6+  8]));			// Kbd Level Scale Break Point (0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+  9]=(byte)((((Patch)p).sysex[6+  9]));			// Kbd Level Scale Left Depth .(0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 10]=(byte)((((Patch)p).sysex[6+ 10]));			// Kbd Level Scale Right Depth (0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 11]=(byte)((((Patch)p).sysex[6+ 12]*4+((Patch)p).sysex[6+ 11]));	// Kbd Level Scale Right Curve .(0-3)
													// | Left Curve ...............(0-3)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 12]=(byte)((((Patch)p).sysex[6+ 20]*8+((Patch)p).sysex[6+ 13]));	// Osc Detune .................(0-14)
													// | Kbd Rate Scaling .........(0-7)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 13]=(byte)((((Patch)p).sysex[6+ 15]*4+((Patch)p).sysex[6+ 14]));	// Key Velocity Sensitivity ....(0-7)
													// | Mod Sensitivity Amplitude (0-3)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 14]=(byte)((((Patch)p).sysex[6+ 16]));			// Operator Output Level ......(0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 15]=(byte)((((Patch)p).sysex[6+ 18]*2+((Patch)p).sysex[6+ 17]));	// Osc Frequency Coarse .......(0-31)
													// | Osc Mode .................(0-1)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 16]=(byte)((((Patch)p).sysex[6+ 19]));			// Osc Frequency Fine .........(0-99)

		//    ***** OPERATOR 5 *****
		((Patch)bank).sysex[getPatchStart(patchNum)+ 17]=(byte)((((Patch)p).sysex[6+ 21]));			// EG Rate  1 .................(0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 18]=(byte)((((Patch)p).sysex[6+ 22]));			// EG Rate  2 .................(0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 19]=(byte)((((Patch)p).sysex[6+ 23]));			// EG Rate  3 .................(0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 20]=(byte)((((Patch)p).sysex[6+ 24]));			// EG Rate  4 .................(0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 21]=(byte)((((Patch)p).sysex[6+ 25]));			// EG Level 1 .................(0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 22]=(byte)((((Patch)p).sysex[6+ 26]));			// EG Level 2 .................(0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 23]=(byte)((((Patch)p).sysex[6+ 27]));			// EG Level 3 .................(0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 24]=(byte)((((Patch)p).sysex[6+ 28]));			// EG Level 4 .................(0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 25]=(byte)((((Patch)p).sysex[6+ 29]));			// Kbd Level Scale Break Point (0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 26]=(byte)((((Patch)p).sysex[6+ 30]));			// Kbd Level Scale Left Depth .(0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 27]=(byte)((((Patch)p).sysex[6+ 31]));			// Kbd Level Scale Right Depth (0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 28]=(byte)((((Patch)p).sysex[6+ 33]*4+((Patch)p).sysex[6+ 32]));	// Kbd Level Scale Right Curve .(0-3)
													// | Left Curve ...............(0-3)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 29]=(byte)((((Patch)p).sysex[6+ 41]*8+((Patch)p).sysex[6+ 34]));	// Osc Detune .................(0-14)
													// | Kbd Rate Scaling .........(0-7)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 30]=(byte)((((Patch)p).sysex[6+ 36]*4+((Patch)p).sysex[6+ 35]));	// Key Velocity Sensitivity ....(0-7)
													// | Mod Sensitivity Amplitude (0-3)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 31]=(byte)((((Patch)p).sysex[6+ 37]));			// Operator Output Level ......(0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 32]=(byte)((((Patch)p).sysex[6+ 39]*2+((Patch)p).sysex[6+ 38]));	// Osc Frequency Coarse .......(0-31)
													// | Osc Mode .................(0-1)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 33]=(byte)((((Patch)p).sysex[6+ 40]));			// Osc Frequency Fine .........(0-99)

		//    ***** OPERATOR 4 *****
		((Patch)bank).sysex[getPatchStart(patchNum)+ 34]=(byte)((((Patch)p).sysex[6+ 42]));			// EG Rate  1 .................(0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 35]=(byte)((((Patch)p).sysex[6+ 43]));			// EG Rate  2 .................(0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 36]=(byte)((((Patch)p).sysex[6+ 44]));			// EG Rate  3 .................(0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 37]=(byte)((((Patch)p).sysex[6+ 45]));			// EG Rate  4 .................(0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 38]=(byte)((((Patch)p).sysex[6+ 46]));			// EG Level 1 .................(0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 39]=(byte)((((Patch)p).sysex[6+ 47]));			// EG Level 2 .................(0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 40]=(byte)((((Patch)p).sysex[6+ 48]));			// EG Level 3 .................(0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 41]=(byte)((((Patch)p).sysex[6+ 49]));			// EG Level 4 .................(0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 42]=(byte)((((Patch)p).sysex[6+ 50]));			// Kbd Level Scale Break Point (0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 43]=(byte)((((Patch)p).sysex[6+ 51]));			// Kbd Level Scale Left Depth .(0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 44]=(byte)((((Patch)p).sysex[6+ 52]));			// Kbd Level Scale Right Depth (0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 45]=(byte)((((Patch)p).sysex[6+ 54]*4+((Patch)p).sysex[6+ 53]));	// Kbd Level Scale Right Curve .(0-3)
													// | Left Curve ...............(0-3)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 46]=(byte)((((Patch)p).sysex[6+ 62]*8+((Patch)p).sysex[6+ 55]));	// Osc Detune .................(0-14)
													// | Kbd Rate Scaling .........(0-7)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 47]=(byte)((((Patch)p).sysex[6+ 57]*4+((Patch)p).sysex[6+ 56]));	// Key Velocity Sensitivity ....(0-7)
													// | Mod Sensitivity Amplitude (0-3)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 48]=(byte)((((Patch)p).sysex[6+ 58]));			// Operator Output Level ......(0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 49]=(byte)((((Patch)p).sysex[6+ 60]*2+((Patch)p).sysex[6+ 59]));	// Osc Frequency Coarse .......(0-31)
													// | Osc Mode .................(0-1)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 50]=(byte)((((Patch)p).sysex[6+ 61]));			// Osc Frequency Fine .........(0-99)

		//    ***** OPERATOR 3 *****
		((Patch)bank).sysex[getPatchStart(patchNum)+ 51]=(byte)((((Patch)p).sysex[6+ 63]));			// EG Rate  1 .................(0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 52]=(byte)((((Patch)p).sysex[6+ 64]));			// EG Rate  2 .................(0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 53]=(byte)((((Patch)p).sysex[6+ 65]));			// EG Rate  3 .................(0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 54]=(byte)((((Patch)p).sysex[6+ 66]));			// EG Rate  4 .................(0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 55]=(byte)((((Patch)p).sysex[6+ 67]));			// EG Level 1 .................(0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 56]=(byte)((((Patch)p).sysex[6+ 68]));			// EG Level 2 .................(0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 57]=(byte)((((Patch)p).sysex[6+ 69]));			// EG Level 3 .................(0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 58]=(byte)((((Patch)p).sysex[6+ 70]));			// EG Level 4 .................(0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 59]=(byte)((((Patch)p).sysex[6+ 71]));			// Kbd Level Scale Break Point (0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 60]=(byte)((((Patch)p).sysex[6+ 72]));			// Kbd Level Scale Left Depth .(0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 61]=(byte)((((Patch)p).sysex[6+ 73]));			// Kbd Level Scale Right Depth (0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 62]=(byte)((((Patch)p).sysex[6+ 75]*4+((Patch)p).sysex[6+ 74]));	// Kbd Level Scale Right Curve .(0-3)
													// | Left Curve ...............(0-3)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 63]=(byte)((((Patch)p).sysex[6+ 83]*8+((Patch)p).sysex[6+ 76]));	// Osc Detune .................(0-14)
													// | Kbd Rate Scaling .........(0-7)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 64]=(byte)((((Patch)p).sysex[6+ 78]*4+((Patch)p).sysex[6+ 77]));	// Key Velocity Sensitivity ....(0-7)
													// | Mod Sensitivity Amplitude (0-3)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 65]=(byte)((((Patch)p).sysex[6+ 79]));			// Operator Output Level ......(0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 66]=(byte)((((Patch)p).sysex[6+ 81]*2+((Patch)p).sysex[6+ 80]));	// Osc Frequency Coarse .......(0-31)
													// | Osc Mode .................(0-1)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 67]=(byte)((((Patch)p).sysex[6+ 82]));			// Osc Frequency Fine .........(0-99)

		//    ***** OPERATOR 2 *****
		((Patch)bank).sysex[getPatchStart(patchNum)+ 68]=(byte)((((Patch)p).sysex[6+ 84]));			// EG Rate  1 .................(0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 69]=(byte)((((Patch)p).sysex[6+ 85]));			// EG Rate  2 .................(0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 70]=(byte)((((Patch)p).sysex[6+ 86]));			// EG Rate  3 .................(0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 71]=(byte)((((Patch)p).sysex[6+ 87]));			// EG Rate  4 .................(0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 72]=(byte)((((Patch)p).sysex[6+ 88]));			// EG Level 1 .................(0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 73]=(byte)((((Patch)p).sysex[6+ 89]));			// EG Level 2 .................(0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 74]=(byte)((((Patch)p).sysex[6+ 90]));			// EG Level 3 .................(0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 75]=(byte)((((Patch)p).sysex[6+ 91]));			// EG Level 4 .................(0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 76]=(byte)((((Patch)p).sysex[6+ 92]));			// Kbd Level Scale Break Point (0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 77]=(byte)((((Patch)p).sysex[6+ 93]));			// Kbd Level Scale Left Depth .(0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 78]=(byte)((((Patch)p).sysex[6+ 94]));			// Kbd Level Scale Right Depth (0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 79]=(byte)((((Patch)p).sysex[6+ 96]*4+((Patch)p).sysex[6+ 95]));	// Kbd Level Scale Right Curve .(0-3)
													// | Left Curve ...............(0-3)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 80]=(byte)((((Patch)p).sysex[6+104]*8+((Patch)p).sysex[6+ 97]));	// Osc Detune .................(0-14)
													// | Kbd Rate Scaling .........(0-7)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 81]=(byte)((((Patch)p).sysex[6+ 99]*4+((Patch)p).sysex[6+ 98]));	// Key Velocity Sensitivity ....(0-7)
													// | Mod Sensitivity Amplitude (0-3)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 82]=(byte)((((Patch)p).sysex[6+100]));			// Operator Output Level ......(0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 83]=(byte)((((Patch)p).sysex[6+102]*2+((Patch)p).sysex[6+101]));	// Osc Frequency Coarse .......(0-31)
													// | Osc Mode .................(0-1)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 84]=(byte)((((Patch)p).sysex[6+103]));			// Osc Frequency Fine .........(0-99)
	
		//    ***** OPERATOR 1 *****
		((Patch)bank).sysex[getPatchStart(patchNum)+ 85]=(byte)((((Patch)p).sysex[6+105]));			// EG Rate  1 .................(0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 86]=(byte)((((Patch)p).sysex[6+106]));			// EG Rate  2 .................(0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 87]=(byte)((((Patch)p).sysex[6+107]));			// EG Rate  3 .................(0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 88]=(byte)((((Patch)p).sysex[6+108]));			// EG Rate  4 .................(0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 89]=(byte)((((Patch)p).sysex[6+109]));			// EG Level 1 .................(0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 90]=(byte)((((Patch)p).sysex[6+110]));			// EG Level 2 .................(0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 91]=(byte)((((Patch)p).sysex[6+111]));			// EG Level 3 .................(0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 92]=(byte)((((Patch)p).sysex[6+112]));			// EG Level 4 .................(0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 93]=(byte)((((Patch)p).sysex[6+113]));			// Kbd Level Scale Break Point (0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 94]=(byte)((((Patch)p).sysex[6+114]));			// Kbd Level Scale Left Depth .(0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 95]=(byte)((((Patch)p).sysex[6+115]));			// Kbd Level Scale Right Depth (0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 96]=(byte)((((Patch)p).sysex[6+117]*4+((Patch)p).sysex[6+116]));	// Kbd Level Scale Right Curve .(0-3)
													// | Left Curve ...............(0-3)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 97]=(byte)((((Patch)p).sysex[6+125]*8+((Patch)p).sysex[6+118]));	// Osc Detune .................(0-14)
													// | Kbd Rate Scaling .........(0-7)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 98]=(byte)((((Patch)p).sysex[6+120]*4+((Patch)p).sysex[6+119]));	// Key Velocity Sensitivity ....(0-7)
													// | Mod Sensitivity Amplitude (0-3)
		((Patch)bank).sysex[getPatchStart(patchNum)+ 99]=(byte)((((Patch)p).sysex[6+121]));			// Operator Output Level ......(0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+100]=(byte)((((Patch)p).sysex[6+123]*2+((Patch)p).sysex[6+122]));	// Osc Frequency Coarse .......(0-31)
													// | Osc Mode .................(0-1)
		((Patch)bank).sysex[getPatchStart(patchNum)+101]=(byte)((((Patch)p).sysex[6+124]));			// Osc Frequency Fine .........(0-99)

		//    ***** other Parameters *****
		((Patch)bank).sysex[getPatchStart(patchNum)+102]=(byte)((((Patch)p).sysex[6+126]));			// Pitch EG Rate 1 ............(0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+103]=(byte)((((Patch)p).sysex[6+127]));			// Pitch EG Rate 2 ............(0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+104]=(byte)((((Patch)p).sysex[6+128]));			// Pitch EG Rate 3 ............(0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+105]=(byte)((((Patch)p).sysex[6+129]));			// Pitch EG Rate 4 ............(0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+106]=(byte)((((Patch)p).sysex[6+130]));			// Pitch EG Level 1 ...........(0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+107]=(byte)((((Patch)p).sysex[6+131]));			// Pitch EG Level 2 ...........(0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+108]=(byte)((((Patch)p).sysex[6+132]));			// Pitch EG Level 3 ...........(0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+109]=(byte)((((Patch)p).sysex[6+133]));			// Pitch EG Level 4 ...........(0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+110]=(byte)((((Patch)p).sysex[6+134]));			// Algorithmic Select .........(0-31)
		((Patch)bank).sysex[getPatchStart(patchNum)+111]=(byte)((((Patch)p).sysex[6+136]*8+((Patch)p).sysex[6+135]));	// Oscillator Sync .............(0-1)| Feedback (0-7)
		((Patch)bank).sysex[getPatchStart(patchNum)+112]=(byte)((((Patch)p).sysex[6+137]));			// LFO Speed ..................(0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+113]=(byte)((((Patch)p).sysex[6+138]));			// LFO Delay ..................(0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+114]=(byte)((((Patch)p).sysex[6+139]));			// LFO PMD ....................(0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+115]=(byte)((((Patch)p).sysex[6+140]));			// LFO AMD ....................(0-99)
		((Patch)bank).sysex[getPatchStart(patchNum)+116]=(byte)((((Patch)p).sysex[6+143]*16+((Patch)p).sysex[6+142]*2+((Patch)p).sysex[6+141]));
													// LFO Mod Sensitivity Pitch ...(0-7)
													// | LFO Wave (0-5)| LFO Sync (0-1)
		((Patch)bank).sysex[getPatchStart(patchNum)+117]=(byte)((((Patch)p).sysex[6+144]));			// Transpose ..................(0-48)
		((Patch)bank).sysex[getPatchStart(patchNum)+118]=(byte)((((Patch)p).sysex[6+145]));			// Voice name  1 ...............ASCII
		((Patch)bank).sysex[getPatchStart(patchNum)+119]=(byte)((((Patch)p).sysex[6+146]));			// Voice name  2 ...............ASCII
		((Patch)bank).sysex[getPatchStart(patchNum)+120]=(byte)((((Patch)p).sysex[6+147]));			// Voice name  3 ...............ASCII
		((Patch)bank).sysex[getPatchStart(patchNum)+121]=(byte)((((Patch)p).sysex[6+148]));			// Voice name  4 ...............ASCII
		((Patch)bank).sysex[getPatchStart(patchNum)+122]=(byte)((((Patch)p).sysex[6+149]));			// Voice name  5 ...............ASCII
		((Patch)bank).sysex[getPatchStart(patchNum)+123]=(byte)((((Patch)p).sysex[6+150]));			// Voice name  6 ...............ASCII
		((Patch)bank).sysex[getPatchStart(patchNum)+124]=(byte)((((Patch)p).sysex[6+151]));			// Voice name  7 ...............ASCII
		((Patch)bank).sysex[getPatchStart(patchNum)+125]=(byte)((((Patch)p).sysex[6+152]));			// Voice name  8 ...............ASCII
		((Patch)bank).sysex[getPatchStart(patchNum)+126]=(byte)((((Patch)p).sysex[6+153]));			// Voice name  9 ...............ASCII
		((Patch)bank).sysex[getPatchStart(patchNum)+127]=(byte)((((Patch)p).sysex[6+154]));			// Voice name 10 ...............ASCII

		calculateChecksum(bank);
	}


	public Patch getPatch(Patch bank, int patchNum)		//Gets a patch from the bank, converting it as needed
	{
		try {
			byte [] sysex=new byte[singleSize];

			// transform bulk-dump-packed-format to voice data
			sysex[0]=(byte)0xF0;
			sysex[1]=(byte)0x43;
			sysex[2]=(byte)0x00;
			sysex[3]=(byte)0x00;
			sysex[4]=(byte)0x01;
			sysex[5]=(byte)0x1B;
			
			//    ***** OPERATOR 6 *****
			sysex[6+  0]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+  0]));			// EG Rate  1 .................(0-99)
			sysex[6+  1]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+  1]));			// EG Rate  2 .................(0-99)
			sysex[6+  2]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+  2]));			// EG Rate  3 .................(0-99)
			sysex[6+  3]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+  3]));			// EG Rate  4 .................(0-99)
			sysex[6+  4]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+  4]));			// EG Level 1 .................(0-99)
			sysex[6+  5]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+  5]));			// EG Level 2 .................(0-99)
			sysex[6+  6]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+  6]));			// EG Level 3 .................(0-99)
			sysex[6+  7]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+  7]));			// EG Level 4 .................(0-99)
			sysex[6+  8]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+  8]));			// Kbd Level Scale Break Point (0-99)
			sysex[6+  9]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+  9]));			// Kbd Level Scale Left Depth .(0-99)
			sysex[6+ 10]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 10]));			// Kbd Level Scale Right Depth (0-99)
			sysex[6+ 11]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 11]&  3));		// Kbd Level Scale Left Curve ..(0-3)
			sysex[6+ 12]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 11]& 12)/ 4);		// Kbd Level Scale Right Curve .(0-3)
			sysex[6+ 13]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 12]&  7));		// Kbd Rate Scaling ............(0-7)
			sysex[6+ 14]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 13]&  3));		// Mod Sensitivity Amplitude ...(0-3)
			sysex[6+ 15]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 13]& 28)/ 4);		// Key Velocity Sensitivity ....(0-7)
			sysex[6+ 16]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 14]));			// Operator Output Level ......(0-99)
			sysex[6+ 17]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 15]&  1));		// Osc Mode ....................(0-1)
			sysex[6+ 18]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 15]& 62)/ 2);		// Osc Frequency Coarse .......(0-31)
			sysex[6+ 19]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 16]));			// Osc Frequency Fine .........(0-99)
			sysex[6+ 20]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 12]&120)/ 8);		// Osc Detune .................(0-14)
 
			//    ***** OPERATOR 5 *****
			sysex[6+ 21]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 17]));			// EG Rate  1 .................(0-99)
			sysex[6+ 22]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 18]));			// EG Rate  2 .................(0-99)
			sysex[6+ 23]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 19]));			// EG Rate  3 .................(0-99)
			sysex[6+ 24]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 20]));			// EG Rate  4 .................(0-99)
			sysex[6+ 25]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 21]));			// EG Level 1 .................(0-99)
			sysex[6+ 26]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 22]));			// EG Level 2 .................(0-99)
			sysex[6+ 27]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 23]));			// EG Level 3 .................(0-99)
			sysex[6+ 28]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 24]));			// EG Level 4 .................(0-99)
			sysex[6+ 29]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 25]));			// Kbd Level Scale Break Point (0-99)
			sysex[6+ 30]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 26]));			// Kbd Level Scale Left Depth .(0-99)
			sysex[6+ 31]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 27]));			// Kbd Level Scale Right Depth (0-99)
			sysex[6+ 32]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 28]&  3));		// Kbd Level Scale Left Curve ..(0-3)
			sysex[6+ 33]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 28]& 12)/ 4);		// Kbd Level Scale Right Curve .(0-3)
			sysex[6+ 34]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 29]&  7));		// Kbd Rate Scaling ............(0-7)
			sysex[6+ 35]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 30]&  3));		// Mod Sensitivity Amplitude ...(0-3)
			sysex[6+ 36]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 30]& 28)/ 4);		// Key Velocity Sensitivity ....(0-7)
			sysex[6+ 37]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 31]));			// Operator Output Level ......(0-99)
			sysex[6+ 38]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 32]&  1));		// Osc Mode ....................(0-1)
			sysex[6+ 39]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 32]& 62)/ 2);		// Osc Frequency Coarse .......(0-31)
			sysex[6+ 40]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 33]));			// Osc Frequency Fine .........(0-99)
			sysex[6+ 41]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 29]&120)/ 8);		// Osc Detune .................(0-14)
 
			//    ***** OPERATOR 4 *****
			sysex[6+ 42]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 34]));			// EG Rate  1 .................(0-99)
			sysex[6+ 43]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 35]));			// EG Rate  2 .................(0-99)
			sysex[6+ 44]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 36]));			// EG Rate  3 .................(0-99)
			sysex[6+ 45]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 37]));			// EG Rate  4 .................(0-99)
			sysex[6+ 46]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 38]));			// EG Level 1 .................(0-99)
			sysex[6+ 47]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 39]));			// EG Level 2 .................(0-99)
			sysex[6+ 48]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 40]));			// EG Level 3 .................(0-99)
			sysex[6+ 49]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 41]));			// EG Level 4 .................(0-99)
			sysex[6+ 50]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 42]));			// Kbd Level Scale Break Point (0-99)
			sysex[6+ 51]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 43]));			// Kbd Level Scale Left Depth .(0-99)
			sysex[6+ 52]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 44]));			// Kbd Level Scale Right Depth (0-99)
			sysex[6+ 53]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 45]&  3));		// Kbd Level Scale Left Curve ..(0-3)
			sysex[6+ 54]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 45]& 12)/ 4);		// Kbd Level Scale Right Curve .(0-3)
			sysex[6+ 55]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 46]&  7));		// Kbd Rate Scaling ............(0-7)
			sysex[6+ 56]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 47]&  3));		// Mod Sensitivity Amplitude ...(0-3)
			sysex[6+ 57]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 47]& 28)/ 4);		// Key Velocity Sensitivity ....(0-7)
			sysex[6+ 58]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 48]));			// Operator Output Level ......(0-99)
			sysex[6+ 59]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 49]&  1));		// Osc Mode ....................(0-1)
			sysex[6+ 60]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 49]& 62)/ 2);		// Osc Frequency Coarse .......(0-31)
			sysex[6+ 61]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 50]));			// Osc Frequency Fine .........(0-99)
			sysex[6+ 62]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 46]&120)/ 8);		// Osc Detune .................(0-14)
 
			//    ***** OPERATOR 3 *****
			sysex[6+ 63]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 51]));			// EG Rate  1 .................(0-99)
			sysex[6+ 64]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 52]));			// EG Rate  2 .................(0-99)
			sysex[6+ 65]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 53]));			// EG Rate  3 .................(0-99)
			sysex[6+ 66]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 54]));			// EG Rate  4 .................(0-99)
			sysex[6+ 67]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 55]));			// EG Level 1 .................(0-99)
			sysex[6+ 68]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 56]));			// EG Level 2 .................(0-99)
			sysex[6+ 69]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 57]));			// EG Level 3 .................(0-99)
			sysex[6+ 70]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 58]));			// EG Level 4 .................(0-99)
			sysex[6+ 71]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 59]));			// Kbd Level Scale Break Point (0-99)
			sysex[6+ 72]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 60]));			// Kbd Level Scale Left Depth .(0-99)
			sysex[6+ 73]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 61]));			// Kbd Level Scale Right Depth (0-99)
			sysex[6+ 74]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 62]&  3));		// Kbd Level Scale Left Curve ..(0-3)
			sysex[6+ 75]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 62]& 12)/ 4);		// Kbd Level Scale Right Curve .(0-3)
			sysex[6+ 76]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 63]&  7));		// Kbd Rate Scaling ............(0-7)
			sysex[6+ 77]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 64]&  3));		// Mod Sensitivity Amplitude ...(0-3)
			sysex[6+ 78]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 64]& 28)/ 4);		// Key Velocity Sensitivity ....(0-7)
			sysex[6+ 79]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 65]));			// Operator Output Level ......(0-99)
			sysex[6+ 80]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 66]&  1));		// Osc Mode ....................(0-1)
			sysex[6+ 81]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 66]& 62)/ 2);		// Osc Frequency Coarse .......(0-31)
			sysex[6+ 82]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 67]));			// Osc Frequency Fine .........(0-99)
			sysex[6+ 83]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 63]&120)/ 8);		// Osc Detune .................(0-14)
 
			//    ***** OPERATOR 2 *****
			sysex[6+ 84]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 68]));			// EG Rate  1 .................(0-99)
			sysex[6+ 85]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 69]));			// EG Rate  2 .................(0-99)
			sysex[6+ 86]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 70]));			// EG Rate  3 .................(0-99)
			sysex[6+ 87]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 71]));			// EG Rate  4 .................(0-99)
			sysex[6+ 88]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 72]));			// EG Level 1 .................(0-99)
			sysex[6+ 89]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 73]));			// EG Level 2 .................(0-99)
			sysex[6+ 90]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 74]));			// EG Level 3 .................(0-99)
			sysex[6+ 91]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 75]));			// EG Level 4 .................(0-99)
			sysex[6+ 92]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 76]));			// Kbd Level Scale Break Point (0-99)
			sysex[6+ 93]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 77]));			// Kbd Level Scale Left Depth .(0-99)
			sysex[6+ 94]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 78]));			// Kbd Level Scale Right Depth (0-99)
			sysex[6+ 95]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 79]&  3));		// Kbd Level Scale Left Curve ..(0-3)
			sysex[6+ 96]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 79]& 12)/ 4);		// Kbd Level Scale Right Curve .(0-3)
			sysex[6+ 97]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 80]&  7));		// Kbd Rate Scaling ............(0-7)
			sysex[6+ 98]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 81]&  3));		// Mod Sensitivity Amplitude ...(0-3)
			sysex[6+ 99]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 81]& 28)/ 4);		// Key Velocity Sensitivity ....(0-7)
			sysex[6+100]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 82]));			// Operator Output Level ......(0-99)
			sysex[6+101]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 83]&  1));		// Osc Mode ....................(0-1)
			sysex[6+102]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 83]& 62)/ 2);		// Osc Frequency Coarse .......(0-31)
			sysex[6+103]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 84]));			// Osc Frequency Fine .........(0-99)
			sysex[6+104]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 80]&120)/ 8);		// Osc Detune .................(0-14)
 
			//    ***** OPERATOR 1 *****
			sysex[6+105]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 85]));			// EG Rate  1 .................(0-99)
			sysex[6+106]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 86]));			// EG Rate  2 .................(0-99)
			sysex[6+107]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 87]));			// EG Rate  3 .................(0-99)
			sysex[6+108]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 88]));			// EG Rate  4 .................(0-99)
			sysex[6+109]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 89]));			// EG Level 1 .................(0-99)
			sysex[6+110]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 90]));			// EG Level 2 .................(0-99)
			sysex[6+111]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 91]));			// EG Level 3 .................(0-99)
			sysex[6+112]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 92]));			// EG Level 4 .................(0-99)
			sysex[6+113]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 93]));			// Kbd Level Scale Break Point (0-99)
			sysex[6+114]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 94]));			// Kbd Level Scale Left Depth .(0-99)
			sysex[6+115]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 95]));			// Kbd Level Scale Right Depth (0-99)
			sysex[6+116]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 96]&  3));		// Kbd Level Scale Left Curve ..(0-3)
			sysex[6+117]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 96]& 12)/ 4);		// Kbd Level Scale Right Curve .(0-3)
			sysex[6+118]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 97]&  7));		// Kbd Rate Scaling ............(0-7)
			sysex[6+119]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 98]&  3));		// Mod Sensitivity Amplitude ...(0-3)
			sysex[6+120]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 98]& 28)/ 4);		// Key Velocity Sensitivity ....(0-7)
			sysex[6+121]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 99]));			// Operator Output Level ......(0-99)
			sysex[6+122]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+100]&  1));		// Osc Mode ....................(0-1)
			sysex[6+123]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+100]& 62)/ 2);		// Osc Frequency Coarse .......(0-31)
			sysex[6+124]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+101]));			// Osc Frequency Fine .........(0-99)
			sysex[6+125]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+ 97]&120)/ 8);		// Osc Detune .................(0-14)
 
			//    ***** other Parameters *****
			sysex[6+126]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+102]));			// Pitch EG Rate 1 ............(0-99)
			sysex[6+127]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+103]));			// Pitch EG Rate 2 ............(0-99)
			sysex[6+128]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+104]));			// Pitch EG Rate 3 ............(0-99)
			sysex[6+129]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+105]));			// Pitch EG Rate 4 ............(0-99)
			sysex[6+130]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+106]));			// Pitch EG Level 1 ...........(0-99)
			sysex[6+131]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+107]));			// Pitch EG Level 2 ...........(0-99)
			sysex[6+132]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+108]));			// Pitch EG Level 3 ...........(0-99)
			sysex[6+133]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+109]));			// Pitch EG Level 4 ...........(0-99)
			sysex[6+134]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+110]& 31));		// Algorithmic Select .........(0-31)
			sysex[6+135]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+111]&  7));		// Feedback ....................(0-7)
			sysex[6+136]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+111]&  8)/ 8);		// Oscillator Sync .............(0-1)
			sysex[6+137]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+112]));			// LFO Speed ..................(0-99)
			sysex[6+138]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+113]));			// LFO Delay ..................(0-99)
			sysex[6+139]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+114]));			// LFO PMD ....................(0-99)
			sysex[6+140]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+115]));			// LFO AMD ....................(0-99)
			sysex[6+141]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+116]&  1));		// LFO Sync ....................(0-1)
			sysex[6+142]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+116]& 14)/ 2);		// LFO Wave ....................(0-5)
			sysex[6+143]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+116]&112)/16);		// LFO Mod Sensitivity Pitch ...(0-7)
			sysex[6+144]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+117]));			// Transpose ................. (0-48)
			sysex[6+145]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+118]));			// Voice name  1 .............. ASCII
			sysex[6+146]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+119]));			// Voice name  2 .............. ASCII
			sysex[6+147]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+120]));			// Voice name  3 .............. ASCII
			sysex[6+148]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+121]));			// Voice name  4 .............. ASCII
			sysex[6+149]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+122]));			// Voice name  5 .............. ASCII
			sysex[6+150]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+123]));			// Voice name  6 .............. ASCII
			sysex[6+151]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+124]));			// Voice name  7 .............. ASCII
			sysex[6+152]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+125]));			// Voice name  8 .............. ASCII
			sysex[6+153]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+126]));			// Voice name  9 .............. ASCII
			sysex[6+154]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+127]));			// Voice name 10 .............. ASCII
 
			sysex[singleSize-1]=(byte)0xF7;
 

			Patch p = new Patch(sysex, getDevice());	// single sysex
			p.calculateChecksum();

			return p;
		} catch (Exception e) {
			ErrorMsg.reportError(getManufacturerName()+" "+getModelName(),"Error in "+toString(),e);
			return null;
		}
	}


	public Patch createNewPatch()	// create a bank with 32 "init voice"-patches
	{
		byte [] sysex = new byte[trimSize];
		
		sysex[00]=(byte)0xF0;
		sysex[01]=(byte)0x43;
		sysex[02]=(byte)0x00;
		sysex[03]=(byte)0x09;
		sysex[04]=(byte)0x20;
		sysex[05]=(byte)0x00;
		sysex[trimSize-1]=(byte)0xF7;

		Patch v = new Patch(initSysex, getDevice());	// single sysex
		Patch p = new Patch(sysex,     this);		// bank sysex

		for (int i=0;i<getNumPatches();i++)
			putPatch(p,v,i);

		calculateChecksum(p);

		return p;
	 }

}
