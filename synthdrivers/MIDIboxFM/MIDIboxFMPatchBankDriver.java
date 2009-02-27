/*
 * Bank Driver for MIDIbox FM
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

import org.jsynthlib.core.Patch;

import core.*;

import synthdrivers.MIDIboxFM.MIDIboxFMBankDriver;
import synthdrivers.MIDIboxFM.MIDIboxFMPatchDriver;

public class MIDIboxFMPatchBankDriver extends MIDIboxFMBankDriver
{

    public MIDIboxFMPatchBankDriver()
    {
	super("PatchBank", 128, (byte)0x00);
    }
    
    public Patch createNewPatch()
    {
	byte [] sysex = new byte[128*256+11];
	sysex[0]=(byte)0xF0; 
	sysex[1]=(byte)0x00;
	sysex[2]=(byte)0x00;
	sysex[3]=(byte)0x7e;
	sysex[4]=(byte)0x49;
	sysex[5]=(byte)((getDeviceID()-1)&0x7f);
	sysex[6]=(byte)0x04;
	sysex[7]=(byte)0x00;
	sysex[8]=(byte)0x00;
	sysex[128*256+10]=(byte)0xF7;

	Patch p = new Patch(sysex, this);
	MIDIboxFMPatchDriver PatchDriver = new MIDIboxFMPatchDriver();
	Patch ps = PatchDriver.createNewPatch();

	for(int i=0; i<128; i++)
	    putPatch(p, ps, i);

	calculateChecksum(p);	 
	return p;
    }
}
