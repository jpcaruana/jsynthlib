/*
 * JSynthlib -	generic "Performance III" Single Driver for Yamaha DX7 Family
 *		(used by TX802)
 * ==========================================================================
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
import javax.swing.*;

public class DX7FamilyPerformanceIIISingleDriver extends Driver
{
	byte[]		initSysex;
	String[]	dxPatchNumbers;
	String[]	dxBankNumbers;

  
	public DX7FamilyPerformanceIIISingleDriver(byte[] initSysex, String[] dxPatchNumbers, String[] dxBankNumbers)
	{
		super ("Single Performance", "Torsten Tittmann");

		this.initSysex		= initSysex;
		this.dxPatchNumbers	= dxPatchNumbers;
		this.dxBankNumbers	= dxBankNumbers;
										
		sysexID= "F0430*7E01684C4D2020383935325045";
		patchNameStart=208;
		patchNameSize=40;
		deviceIDoffset=2;
		checksumOffset=248;
		checksumStart=6;
		checksumEnd=247;
		patchNumbers = dxPatchNumbers;	
		bankNumbers  = dxBankNumbers;
		patchSize=250;
		trimSize=250;
		numSysexMsgs=1;	    
		sysexRequestDump=new SysexHandler("F0 43 @@ 7E 4C 4D 20 20 38 39 35 32 50 45 F7");
	}

	
	public Patch createNewPatch()
	{
		return new Patch(initSysex, this);
	}


	public JInternalFrame editPatch(Patch p)
	{
		return new DX7FamilyPerformanceIIIEditor(getManufacturerName()+" "+getModelName()+" \""+getPatchType()+"\" Editor" ,p);
	}


        public void requestPatchDump(int bankNum, int patchNum)
        {
                sysexRequestDump.send(getPort(), (byte)(getChannel()+0x20) );
        }


	public String getPatchName (Patch p)
	{
		try {
			byte []b = new byte[patchNameSize/2];	// 1 character encoded in 2 bytes!

			for (int i=0; i < b.length; i++) {
			b[i] =(byte)(	DX7FamilyByteEncoding.AsciiHex2Value(p.sysex[16+2*(96+i)  ])*16 +
					DX7FamilyByteEncoding.AsciiHex2Value(p.sysex[16+2*(96+i)+1]) );
			}

			StringBuffer s= new StringBuffer(new String(b,0,patchNameSize/2,"US-ASCII"));

			return s.toString();
		} catch (Exception ex) {
			return "-";
		}
	}


	public void setPatchName (Patch p, String name)
	{
		byte [] namebytes = new byte[patchNameSize/2];	// 1 character encoded in 2 bytes!

		try{
			while (name.length()<patchNameSize/2) name=name+" ";

			namebytes=name.getBytes("US-ASCII");

			for (int i=0; i < namebytes.length; i++) {
				p.sysex[16+2*(96+i)  ] = (byte)(DX7FamilyByteEncoding.Value2AsciiHexHigh(namebytes[i]));
				p.sysex[16+2*(96+i)+1] = (byte)(DX7FamilyByteEncoding.Value2AsciiHexLow( namebytes[i]));
			}
		} catch (Exception e) {}

		calculateChecksum (p);
	}
}
