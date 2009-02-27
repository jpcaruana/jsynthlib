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

public class MIDIboxFMPatchDriver extends MIDIboxFMSingleDriver
{

    public MIDIboxFMPatchDriver()
    {
	super("Patch", 128, (byte)0x00);
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
	sysex[7]=(byte)0x00;
	sysex[8]=(byte)0x00;
	sysex[9]=(byte)0x00;

	for(int i=0; i<256; ++i)
	    sysex[10+i] = 0x00;

	sysex[10+0x10] = 0x01; // OP1 flags
	sysex[10+0x11] = 0x00; // OP2 flags
	sysex[10+0x12] = 0x00; // OP3 flags
	sysex[10+0x13] = 0x00; // OP4 flags

	sysex[10+0x14] = 0x06; // OP1 mult
	sysex[10+0x15] = 0x01; // OP2 mult
	sysex[10+0x16] = 0x0b; // OP3 mult
	sysex[10+0x17] = 0x01; // OP4 mult

	sysex[10+0x18] = 0x01; // OP1 KSL
	sysex[10+0x19] = 0x00; // OP2 KSL
	sysex[10+0x1a] = 0x00; // OP3 KSL
	sysex[10+0x1b] = 0x00; // OP4 KSL

	sysex[10+0x1c] = 0x28; // OP1 TL
	sysex[10+0x1d] = 0x37; // OP2 TL
	sysex[10+0x1e] = 0x14; // OP3 TL
	sysex[10+0x1f] = 0x37; // OP4 TL

	sysex[10+0x20] = 0x00; // OP1 AR
	sysex[10+0x21] = 0x00; // OP2 AR
	sysex[10+0x22] = 0x01; // OP3 AR
	sysex[10+0x23] = 0x01; // OP4 AR

	sysex[10+0x24] = 0x00; // OP1 DR
	sysex[10+0x25] = 0x0c; // OP2 DR
	sysex[10+0x26] = 0x0a; // OP3 DR
	sysex[10+0x27] = 0x0d; // OP4 DR

	sysex[10+0x28] = 0x07; // OP1 SL
	sysex[10+0x29] = 0x09; // OP2 SL
	sysex[10+0x2a] = 0x04; // OP3 SL
	sysex[10+0x2b] = 0x07; // OP4 SL

	sysex[10+0x2c] = 0x05; // OP1 RR
	sysex[10+0x2d] = 0x09; // OP2 RR
	sysex[10+0x2e] = 0x0a; // OP3 RR
	sysex[10+0x2f] = 0x09; // OP4 RR

	sysex[10+0x30] = 0x00; // OP1 WS
	sysex[10+0x31] = 0x00; // OP2 WS
	sysex[10+0x32] = 0x00; // OP3 WS
	sysex[10+0x33] = 0x00; // OP4 WS

	sysex[10+0x38] = 0x00; // OP1 feedback

	sysex[10+0x3c] = 0x02; // OP connections

	sysex[10+0x40] = 0x05; // LFO1 mode
	sysex[10+0x41] = 0x00; // LFO1 phase
	sysex[10+0x42] = 0x50; // LFO1 rate
	sysex[10+0x43] = 0x40; // LFO1 pitch depth
	sysex[10+0x44] = 0x40; // LFO1 OP1 volume depth
	sysex[10+0x45] = 0x40; // LFO1 OP2 volume depth
	sysex[10+0x46] = 0x40; // LFO1 OP3 volume depth
	sysex[10+0x47] = 0x40; // LFO1 OP4 volume depth
	sysex[10+0x48] = 0x40; // LFO1 LFO2 depth
	sysex[10+0x49] = 0x40; // LFO1 AOUT depth

	sysex[10+0x4c] = 0x00; // Velocity Assign
	sysex[10+0x4d] = 0x40; // Velocity Init Value
	sysex[10+0x4e] = 0x40; // Velocity Depth

	sysex[10+0x50] = 0x05; // LFO2 mode
	sysex[10+0x51] = 0x00; // LFO2 phase
	sysex[10+0x52] = 0x70; // LFO2 rate
	sysex[10+0x53] = 0x40; // LFO2 pitch depth
	sysex[10+0x54] = 0x40; // LFO2 OP1 volume depth
	sysex[10+0x55] = 0x40; // LFO2 OP2 volume depth
	sysex[10+0x56] = 0x40; // LFO2 OP3 volume depth
	sysex[10+0x57] = 0x40; // LFO2 OP4 volume depth
	sysex[10+0x58] = 0x40; // LFO2 LFO1 depth
	sysex[10+0x59] = 0x40; // LFO2 AOUT depth

	sysex[10+0x5c] = 0x00; // Aftertouch Assign
	sysex[10+0x5d] = 0x40; // Aftertouch Init Value
	sysex[10+0x5e] = 0x40; // Aftertouch Depth

	sysex[10+0x60] = 0x00; // EG5 mode
	sysex[10+0x61] = 0x20; // EG5 attack rate
	sysex[10+0x62] = 0x60; // EG5 attack level
	sysex[10+0x63] = 0x20; // EG5 decay1 rate
	sysex[10+0x64] = 0x20; // EG5 decay level
	sysex[10+0x65] = 0x20; // EG5 decay2 rate
	sysex[10+0x66] = 0x40; // EG5 sustain
	sysex[10+0x67] = 0x20; // EG5 release
	sysex[10+0x68] = 0x40; // EG5 curve
	sysex[10+0x69] = 0x40; // EG5 pitch depth
	sysex[10+0x6a] = 0x40; // EG5 OP1 volume depth
	sysex[10+0x6b] = 0x40; // EG5 OP2 volume depth
	sysex[10+0x6c] = 0x40; // EG5 OP3 volume depth
	sysex[10+0x6d] = 0x40; // EG5 OP4 volume depth
	sysex[10+0x6e] = 0x40; // EG5 LFO1 depth
	sysex[10+0x6f] = 0x40; // EG5 AOUT depth

	sysex[10+0x70] = 0x02; // CTRL1_L
	sysex[10+0x71] = 0x00; // CTRL1_H
	sysex[10+0x72] = 0x00; // CTRL2_L
	sysex[10+0x73] = 0x00; // CTRL2_H
	sysex[10+0x74] = 0x40; // finetune
	sysex[10+0x75] = 0x02; // pitchrange
	sysex[10+0x76] = 0x00; // portamento rate
	sysex[10+0x77] = 0x00; // wavetable rate
	sysex[10+0x78] = 0x15; // wt parameter #1
	sysex[10+0x79] = 0x17; // wt parameter #1
	sysex[10+0x7a] = 0x52; // wt parameter #1

	sysex[10+0x7c] = 0x00; // Modwheel Assign
	sysex[10+0x7d] = 0x40; // Modwheel Init Value
	sysex[10+0x7e] = 0x40; // Modwheel Depth

	sysex[267]=(byte)0xf7;
	Patch p = new Patch(sysex, this);
	setPatchName(p,"New Patch");
	calculateChecksum(p);	 
	return p;
    }

    public JSLFrame editPatch(Patch p)
    {
	return new MIDIboxFMPatchEditor((Patch)p);
    }
}
