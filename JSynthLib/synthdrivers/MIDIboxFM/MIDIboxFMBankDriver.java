/*
 * Generic Bank Driver for MIDIbox FM
 * =====================================================================
 * @author  Thorsten Klose
 * @version $Id$
 *
 * Copyright (C) 2005  Thorsten.Klose@gmx.de   
 *                     http://www.uCApps.de
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
 */

package synthdrivers.MIDIboxFM;

import core.*;

import java.io.*;
import javax.swing.*;

public class MIDIboxFMBankDriver extends BankDriver
{
    private byte sysex_type;
    private int num_patches;

    public MIDIboxFMBankDriver(String bank_name, int _num_patches, byte _sysex_type)
    {
	super(bank_name, "Thorsten Klose", _num_patches, 4);

	num_patches = _num_patches;
	sysex_type = _sysex_type;

	String type_digit1 = Integer.toHexString(((int)sysex_type >> 4) & 0xf);
	String type_digit0 = Integer.toHexString(((int)sysex_type >> 0) & 0xf);

	sysexID="F000007E49**04" + type_digit1 + "*";
	sysexRequestDump=new SysexHandler("F0 00 00 7E 49 @@ 03 " + type_digit1 + type_digit0 + " *bankNum* F7");

	bankNumbers =new String[] {"A","B","C","D","E","F","G","H"};
	patchNumbers=new String[num_patches];
	System.arraycopy(DriverUtil.generateNumbers(1, num_patches, "000"), 0, patchNumbers,  0, num_patches);
	
	patchSize=num_patches*256+11;
	deviceIDoffset=5;

	singleSysexID="F000007E49**02" + type_digit1 + "*";
	singleSize=268;

	checksumStart=9;
	checksumEnd=num_patches*256+9;
	checksumOffset=num_patches*256+10;
    }

    public void storePatch (Patch p, int bankNum, int patchNum)
    { 
	for(int i=0; i<num_patches; ++i) {
	    Patch ps = getPatch(p, i);
	    ps.sysex[8]=(byte)bankNum;
	    System.out.println("Sending Patch #" + i);
	    send(ps.sysex);
	    try { Thread.sleep(600); } catch (Exception e) {};
	}
    }

//    public void sendPatch(Patch p)
//    {
//	storePatch(p, 0, 0);
//    }

    public int getPatchStart(int patchNum) {
	int start=(256*patchNum) + 10;
	return start;
    }

    public String getPatchName(Patch p, int patchNum) {
	if( sysex_type < 0x10 )
	{
	    int nameStart=getPatchStart(patchNum);
	    nameStart+=0; //offset of name in patch data
	    try {
		StringBuffer s= new StringBuffer(new String(((Patch)p).sysex,nameStart, 16, "US-ASCII"));
		return s.toString();
	    } catch (UnsupportedEncodingException ex) {return "-";}
	}
	else
	{
	    return "-";
	}
    }
    
    public void setPatchName(Patch p, int patchNum, String name)
    {
	if( sysex_type < 0x10 )
	{
	    patchNameSize=16;
	    patchNameStart=getPatchStart(patchNum);
	
	    if( name.length() < patchNameSize ) name=name + "                ";
	    byte [] namebytes = new byte [64];
	    try {
		namebytes=name.getBytes("US-ASCII");
		for(int i=0;i<patchNameSize;i++)
		    ((Patch)p).sysex[patchNameStart+i]=namebytes[i];
	    } catch (UnsupportedEncodingException ex) {return;}
	}
    }
    
    public void putPatch(Patch bank, Patch p, int patchNum)
    { 
	if( !canHoldPatch(p) ) {
	    JOptionPane.showMessageDialog(null, "This type of patch does not fit in to this type of bank.","Error", JOptionPane.ERROR_MESSAGE); 
	    return;
	}
	
	System.arraycopy(((Patch)p).sysex, 10, ((Patch)bank).sysex, getPatchStart(patchNum), 256);
	calculateChecksum(bank);
    }

    public Patch getPatch(Patch bank, int patchNum)
    {
	try{
	    byte [] sysex=new byte[268];
	    sysex[0]=(byte)0xF0; 
	    sysex[1]=(byte)0x00;
	    sysex[2]=(byte)0x00;
	    sysex[3]=(byte)0x7e;
	    sysex[4]=(byte)0x49;
	    sysex[5]=(byte)((getChannel()-1)&0x7f);
	    sysex[6]=(byte)0x02;
	    sysex[7]=(byte)sysex_type;
	    sysex[8]=(byte)0x00;
	    sysex[9]=(byte)(patchNum);
	    sysex[267]=(byte)0xF7;

	    System.arraycopy(((Patch)bank).sysex, getPatchStart(patchNum), sysex, 10, 256);
	    Patch p = new Patch(sysex, getDevice());
	    p.calculateChecksum();   
	    return p;
	} catch( Exception e ) { ErrorMsg.reportError("Error","Error in MIDIboxFM Bank Driver",e);return null; }
    }    

}
