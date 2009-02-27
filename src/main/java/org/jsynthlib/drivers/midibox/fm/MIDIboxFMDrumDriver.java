/*
 * JSynthlib-Device for MIDIbox FM
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

package org.jsynthlib.drivers.midibox.fm;
import org.jsynthlib.core.JSLFrame;
import org.jsynthlib.core.Patch;

public class MIDIboxFMDrumDriver extends MIDIboxFMSingleDriver
{

    public MIDIboxFMDrumDriver()
    {
	super ("Drums", 128, (byte)0x10);
    }

    public void setPatchName(Patch p, String name) 
    {
    };

    public String getPatchName(Patch ip) 
    {
        return "-";
    }
    
    public Patch createNewPatch()
    {
	byte [] sysex = new byte[268];

	sysex[0]=(byte)0xf0;
	sysex[1]=(byte)0x00;
	sysex[2]=(byte)0x00;
	sysex[3]=(byte)0x7e;
	sysex[4]=(byte)0x49;
	sysex[5]=(byte)((getDeviceID()-1)&0x7f);
	sysex[6]=(byte)0x02;
	sysex[7]=(byte)0x10;
	sysex[8]=(byte)0x00;
	sysex[9]=(byte)0x00;

	for(int i=0; i<256; ++i)
	    sysex[10+i] = 0x00;

	sysex[10+0x00] = 0x00; // BD_M_MULT
	sysex[10+0x01] = 0x00; // BD_C_MULT
	sysex[10+0x02] = 0x00; // HH_O_MULT
	sysex[10+0x03] = 0x00; // HH_C_MULT
	sysex[10+0x04] = 0x04; // SD_MULT
	sysex[10+0x05] = 0x00; // TOM_MULT
	sysex[10+0x06] = 0x0f; // CYM_MULT

	sysex[10+0x08] = 0x28; // BD_M_TL
	sysex[10+0x09] = 0x3f; // BD_C_TL
	sysex[10+0x0a] = 0x3f; // HH_O_TL
	sysex[10+0x0b] = 0x3f; // HH_C_TL
	sysex[10+0x0c] = 0x3f; // SD_TL
	sysex[10+0x0d] = 0x3f; // TOM_TL
	sysex[10+0x0e] = 0x3f; // CYM_TL

	sysex[10+0x10] = 0x00; // BD_M_AR
	sysex[10+0x11] = 0x00; // BD_C_AR
	sysex[10+0x12] = 0x00; // HH_O_AR
	sysex[10+0x13] = 0x00; // HH_C_AR
	sysex[10+0x14] = 0x00; // SD_AR
	sysex[10+0x15] = 0x00; // TOM_AR
	sysex[10+0x16] = 0x00; // CYM_AR

	sysex[10+0x18] = 0x05; // BD_M_DR
	sysex[10+0x19] = 0x0a; // BD_C_DR
	sysex[10+0x1a] = 0x0f; // HH_O_DR
	sysex[10+0x1b] = 0x0f; // HH_C_DR
	sysex[10+0x1c] = 0x0f; // SD_DR
	sysex[10+0x1d] = 0x0b; // TOM_DR
	sysex[10+0x1e] = 0x0f; // CYM_DR

	sysex[10+0x20] = 0x04; // BD_M_SL
	sysex[10+0x21] = 0x08; // BD_C_SL
	sysex[10+0x22] = 0x0f; // HH_O_SL
	sysex[10+0x23] = 0x0f; // HH_C_SL
	sysex[10+0x24] = 0x0f; // SD_SL
	sysex[10+0x25] = 0x08; // TOM_SL
	sysex[10+0x26] = 0x0f; // CYM_SL

	sysex[10+0x28] = 0x07; // BD_M_RR
	sysex[10+0x29] = 0x08; // BD_C_RR
	sysex[10+0x2a] = 0x0a; // HH_O_RR
	sysex[10+0x2b] = 0x07; // HH_C_RR
	sysex[10+0x2c] = 0x08; // SD_RR
	sysex[10+0x2d] = 0x0a; // TOM_RR
	sysex[10+0x2e] = 0x0a; // CYM_RR

	sysex[10+0x30] = 0x00; // BD_M_WS
	sysex[10+0x31] = 0x00; // BD_C_WS
	sysex[10+0x32] = 0x00; // HH_O_WS
	sysex[10+0x33] = 0x00; // HH_C_WS
	sysex[10+0x34] = 0x00; // SD_WS
	sysex[10+0x35] = 0x00; // TOM_WS
	sysex[10+0x36] = 0x00; // CYM_WS

	sysex[10+0x38] = 0x00; // BD_FEEDBACK
	sysex[10+0x39] = 0x00; // BD_CON
	sysex[10+0x3a] = 0x00; // SD_HH_FEEDBACK
	sysex[10+0x3b] = 0x00; // SD_HH_CON
	sysex[10+0x3c] = 0x00; // CYM_TOM_FEEDBACK
	sysex[10+0x3d] = 0x00; // CYM_TOM_CON

	sysex[10+0x41] = 0x24; // BD_SPLIT_LOWER
	sysex[10+0x42] = 0x2e; // HH_O_SPLIT_LOWER
	sysex[10+0x43] = 0x2c; // HH_C_SPLIT_LOWER
	sysex[10+0x44] = 0x26; // SD_SPLIT_LOWER
	sysex[10+0x45] = 0x28; // TOM_SPLIT_LOWER
	sysex[10+0x46] = 0x30; // CYM_SPLIT_LOWER

	sysex[10+0x49] = 0x24; // BD_SPLIT_UPPER
	sysex[10+0x4a] = 0x2e; // HH_O_SPLIT_UPPER
	sysex[10+0x4b] = 0x2c; // HH_C_SPLIT_UPPER
	sysex[10+0x4c] = 0x26; // SD_SPLIT_UPPER
	sysex[10+0x4d] = 0x28; // TOM_SPLIT_UPPER
	sysex[10+0x4e] = 0x30; // CYM_SPLIT_UPPER

	sysex[10+0x50] =   96; // BD_FRQ
	sysex[10+0x51] =   72; // BD_FRQ_DECR
	sysex[10+0x52] =  109; // SD_FRQ
	sysex[10+0x53] =    0; // SD_FRQ_DECR
	sysex[10+0x54] =  112; // TOM_FRQ
	sysex[10+0x55] =    0; // TOM_FRQ_DECR

	sysex[10+0x58] = 0x01; // BD_OUT
	sysex[10+0x5a] = 0x02; // SD_OUT
	sysex[10+0x5c] = 0x02; // TOM_OUT

	sysex[267]=(byte)0xf7;
	Patch p = new Patch(sysex, this);
	setPatchName(p,"New Drumset");
	calculateChecksum(p);	 
	return p;
    }

    public JSLFrame editPatch(Patch p)
    {
	return new MIDIboxFMDrumEditor((Patch)p);
    }
}
