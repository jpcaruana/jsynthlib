/*
 * JSynthlib SysEx Sender MIDIbox FM
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

import core.SysexSender;
import core.Patch;

class MIDIboxFMSender extends SysexSender
{
    Patch patch;
    int parameter;
    int flag;
    int bitmask;
    int []mapped_values;
    byte []b = new byte [12];

    private void MIDIboxFMSender_Hlp(Patch _patch, int type, int _parameter)
    {
	patch = _patch;
	parameter = _parameter;
	b[0] = (byte)0xf0;
	b[1] = (byte)0x00;
	b[2] = (byte)0x00;
	b[3] = (byte)0x7e;
	b[4] = (byte)0x49;
	//b[5] = (byte)(getChannel()-1);
	b[6] = (byte)0x06;
	b[7] = (byte)type;
	b[8] = (byte)(parameter > 0x80 ? 0x01 : 0x00);
	b[9] = (byte)(parameter & 0x7f);
	//b[10] = (byte)(value&0x7f);
	b[11] = (byte)0xf7;
    }

    public MIDIboxFMSender(Patch _patch, int type, int parameter)
    {
	flag = -1;
	MIDIboxFMSender_Hlp(_patch, type, parameter);
    }

    public MIDIboxFMSender(Patch _patch, int type, int parameter, int _flag)
    {
	flag    = _flag;
	bitmask = (1 << _flag);
	mapped_values = new int[]{}; // (empty)
	MIDIboxFMSender_Hlp(_patch, type, parameter);
    }

    public MIDIboxFMSender(Patch _patch, int type, int parameter, int _flag, int _bitmask)
    {
	flag    = _flag;
	bitmask = _bitmask << flag;
	mapped_values  = new int[]{}; // (empty)
	MIDIboxFMSender_Hlp(_patch, type, parameter);
    }

    public MIDIboxFMSender(Patch _patch, int type, int parameter, int _flag, int _bitmask, int []_mapped_values)
    {
	flag    = _flag;
	bitmask = _bitmask << flag;
	mapped_values  = _mapped_values;
	MIDIboxFMSender_Hlp(_patch, type, parameter);
    }

    public byte [] generate (int value)
    {
	b[5] = (byte)(channel-1);

	if( flag == -1 ) {
	    b[10] = (byte)value;
	} else {
	    b[10] = (byte)(patch.sysex[10+parameter] & (~bitmask));

	    if( mapped_values.length > 0 )
		value = mapped_values[value];

	    b[10] |= (byte)value << flag;
	}

	return b;
    }
}
