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

package synthdrivers.MIDIboxFM;
import core.JSLFrame;
import core.Patch;

public class MIDIboxFMEnsDriver extends MIDIboxFMSingleDriver
{

    public MIDIboxFMEnsDriver()
    {
	super("Ensemble", 32, (byte)0x70);
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
	sysex[7]=(byte)0x70;
	sysex[8]=(byte)0x00;
	sysex[9]=(byte)0x00;

	for(int i=0; i<256; ++i)
	    sysex[10+i] = 0x00;

	sysex[10+0x00] = 0x00; // I1 bank
	sysex[10+0x01] = 0x04; // I1 patch
	sysex[10+0x02] = 0x00; // I1 MIDI channel
	sysex[10+0x03] = 0x00; // I1 lower split point
	sysex[10+0x04] = 0x7f; // I1 upper split point
	sysex[10+0x05] = 0x00; // I1 ensemble control flags
	sysex[10+0x06] = 0x7f; // I1 volume
	sysex[10+0x07] = 0x40; // I1 transpose
	sysex[10+0x08] = 0x00; // I1 unisono
	sysex[10+0x09] = 0x0f; // I1 audio channels OP12
	sysex[10+0x0a] = 0x0f; // I1 audio channels OP34

	sysex[10+0x10] = 0x00; // I2 bank
	sysex[10+0x11] = 0x24; // I2 patch
	sysex[10+0x12] = 0x01; // I2 MIDI channel
	sysex[10+0x13] = 0x00; // I2 lower split point
	sysex[10+0x14] = 0x7f; // I2 upper split point
	sysex[10+0x15] = 0x00; // I2 ensemble control flags
	sysex[10+0x16] = 0x7f; // I2 volume
	sysex[10+0x17] = 0x40; // I2 transpose
	sysex[10+0x18] = 0x00; // I2 unisono
	sysex[10+0x19] = 0x0f; // I2 audio channels OP12
	sysex[10+0x1a] = 0x0f; // I2 audio channels OP34

	sysex[10+0x20] = 0x00; // I3 bank
	sysex[10+0x21] = 0x53; // I3 patch
	sysex[10+0x22] = 0x02; // I3 MIDI channel
	sysex[10+0x23] = 0x00; // I3 lower split point
	sysex[10+0x24] = 0x7f; // I3 upper split point
	sysex[10+0x25] = 0x00; // I3 ensemble control flags
	sysex[10+0x26] = 0x7f; // I3 volume
	sysex[10+0x27] = 0x40; // I3 transpose
	sysex[10+0x28] = 0x00; // I3 unisono
	sysex[10+0x29] = 0x0f; // I3 audio channels OP12
	sysex[10+0x2a] = 0x0f; // I3 audio channels OP34

	sysex[10+0x30] = 0x00; // I4 bank
	sysex[10+0x31] = 0x62; // I4 patch
	sysex[10+0x32] = 0x03; // I4 MIDI channel
	sysex[10+0x33] = 0x00; // I4 lower split point
	sysex[10+0x34] = 0x7f; // I4 upper split point
	sysex[10+0x35] = 0x00; // I4 ensemble control flags
	sysex[10+0x36] = 0x7f; // I4 volume
	sysex[10+0x37] = 0x40; // I4 transpose
	sysex[10+0x38] = 0x00; // I4 unisono
	sysex[10+0x39] = 0x0f; // I4 audio channels OP12
	sysex[10+0x3a] = 0x0f; // I4 audio channels OP34

	sysex[267]=(byte)0xf7;
	Patch p = new Patch(sysex, this);
	setPatchName(p,"New Ensemble");
	calculateChecksum(p);	 
	return p;
    }

    public JSLFrame editPatch(Patch p)
    {
	return new MIDIboxFMEnsEditor((Patch)p);
    }
}
