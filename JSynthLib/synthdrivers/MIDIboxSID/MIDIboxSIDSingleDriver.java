/*
 * JSynthlib-Device for MIDIbox SID
 * =====================================================================
 * @author  Thorsten Klose
 * file:    MIDIboxSIDSingleDriver.java
 * date:    2002-11-30
 * @version $Id$
 *
 * Copyright (C) 2002  Thorsten.Klose@gmx.de   
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

package synthdrivers.MIDIboxSID;
import core.*;
import javax.swing.*;
import java.io.*;
import java.awt.event.*;

public class MIDIboxSIDSingleDriver extends Driver
{

   public MIDIboxSIDSingleDriver()
   {
       super ("Single","Thorsten Klose");
       sysexID="F000007E46**02";
       sysexRequestDump=new SysexHandler("F0 00 00 7E 46 @@ 01 *patchNum* F7");

       patchSize=266;

       patchNameStart=8;
       patchNameSize=16;

       deviceIDoffset=5;

       checksumStart=8;
       checksumEnd=263;
       checksumOffset=264;

       bankNumbers =new String[] {"BankStick"};
       patchNumbers=new String[] {
	   "INT","002","003","004","005","006","007","008","009","010",
	   "011","012","013","014","015","016","017","018","019","020",
	   "021","022","023","024","025","026","027","028","029","030",
	   "031","032","033","034","035","036","037","038","039","040",
	   "041","042","043","044","045","046","047","048","049","050",
	   "051","052","053","054","055","056","057","058","059","060",
	   "061","062","063","064","065","066","067","068","069","070",
	   "071","072","073","074","075","076","077","078","079","080",
	   "081","082","083","084","085","086","087","088","089","090",
	   "091","092","093","094","095","096","097","098","099","020",
	   "101","102","103","104","105","106","107","108","109","110",
	   "111","112","113","114","115","116","117","118","119","120",
	   "121","122","123","124","125","126","127","128"};  

    }

    public void storePatch(Patch p, int bankNum,int patchNum)
    {
	p.sysex[5]=(byte)((getChannel()-1)&0x7f);
	p.sysex[6]=(byte)0x02;
	p.sysex[7]=(byte)(patchNum);
	sendPatchWorker(p);
	try {Thread.sleep(100); } catch (Exception e){}   
	setPatchNum(patchNum);
    }

    public void sendPatch(Patch p)
    { 
	p.sysex[5]=(byte)((getChannel()-1)&0x7f);
	p.sysex[6]=(byte)0x02;
	p.sysex[7]=(byte)0x00;

	sendPatchWorker(p);
    }
    
    public Patch createNewPatch()
    {
	byte [] sysex = new byte[266];

	sysex[0]=(byte)0xF0; 
	sysex[1]=(byte)0x00;
	sysex[2]=(byte)0x00;
	sysex[3]=(byte)0x7e;
	sysex[4]=(byte)0x46;
	sysex[5]=(byte)((getChannel()-1)&0x7f);
	sysex[6]=(byte)0x02;
	sysex[7]=(byte)0x00;

	for(int i=0; i<256; ++i)
	    sysex[8+i] = 0x00;

	sysex[8+0x10] = 0x7f; // volume
	sysex[8+0x19] = 0x01; // Filter mode
	sysex[8+0x1a] = 0x40; // cutoff frq.
	sysex[8+0x20] = 0x40; // V1 transpose
	sysex[8+0x21] = 0x40; // V1 finetune
	sysex[8+0x22] = 0x02; // V1 pitchrange
	sysex[8+0x24] = 0x04; // V1 waveform
	sysex[8+0x25] = 0x40; // V1 pulsewidth
	sysex[8+0x2a] = 0x7f; // V1 sustain
	sysex[8+0x30] = 0x40; // V2 transpose
	sysex[8+0x31] = 0x3e; // V2 finetune
	sysex[8+0x32] = 0x02; // V2 pitchrange
	sysex[8+0x34] = 0x12; // V2 waveform (off)
	sysex[8+0x35] = 0x40; // V2 pulsewidth
	sysex[8+0x3a] = 0x7f; // V2 sustain
	sysex[8+0x40] = 0x40; // V3 transpose
	sysex[8+0x41] = 0x42; // V3 finetune
	sysex[8+0x42] = 0x02; // V3 pitchrange
	sysex[8+0x44] = 0x14; // V3 waveform (off)
	sysex[8+0x45] = 0x40; // V3 pulsewidth
	sysex[8+0x4a] = 0x7f; // V3 sustain
	sysex[8+0x52] = 0x7f; // vel. depth
	sysex[8+0x55] = 0x7f; // mod. depth
	sysex[8+0x58] = 0x7f; // aft. depth
	sysex[8+0x5a] = 0x20; // WT P1 assign
	sysex[8+0x5b] = 0x10; // WT P2 assign
	sysex[8+0x5c] = 0x2e; // WT P3 assign
	sysex[8+0x60] = 0x07; // LFO1 mode
	sysex[8+0x61] = 0x10; // LFO1 rate
	sysex[8+0x62] = 0x7f; // LFO1 depth
	sysex[8+0x63] = 0x07; // LFO2 mode
	sysex[8+0x64] = 0x20; // LFO2 rate
	sysex[8+0x65] = 0x00; // LFO2 depth
	sysex[8+0x66] = 0x01; // LFO3 mode
	sysex[8+0x67] = 0x30; // LFO3 rate
	sysex[8+0x68] = 0x7f; // LFO3 depth
	sysex[8+0x69] = 0x01; // LFO4 mode
	sysex[8+0x6a] = 0x40; // LFO4 rate
	sysex[8+0x6b] = 0x00; // LFO4 depth
	sysex[8+0x6c] = 0x01; // LFO5 mode
	sysex[8+0x6d] = 0x50; // LFO5 rate
	sysex[8+0x6e] = 0x7f; // LFO5 depth
	sysex[8+0x6f] = 0x01; // LFO6 mode
	sysex[8+0x70] = 0x60; // LFO6 rate
	sysex[8+0x71] = 0x00; // LFO6 depth
	sysex[8+0x72] = 0x7f; // ENV1 depth
	sysex[8+0x73] = 0x40; // ENV1 attack
	sysex[8+0x74] = 0x40; // ENV1 decay
	sysex[8+0x75] = 0x40; // ENV1 sustain
	sysex[8+0x76] = 0x40; // ENV1 release
	sysex[8+0x77] = 0x00; // ENV2 depth
	sysex[8+0x78] = 0x60; // ENV2 attack
	sysex[8+0x79] = 0x60; // ENV2 decay
	sysex[8+0x7a] = 0x20; // ENV2 sustain
	sysex[8+0x7b] = 0x40; // ENV2 release

	sysex[265]=(byte)0xF7;
	Patch p = new Patch(sysex, this);
	setPatchName(p,"New Patch");
	calculateChecksum(p);	 
	return p;
    }

    public JSLFrame editPatch(Patch p)
    {
	return new MIDIboxSIDSingleEditor(p);
    }
}
