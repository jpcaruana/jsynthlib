/*
 * JSynthlib Parameter Model for MIDIbox FM
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

import org.jsynthlib.core.ParamModel;
import org.jsynthlib.core.Patch;

class MIDIboxFMModel extends ParamModel
{
    int flag;
    int bitmask;
    int []mapped_values;

    public MIDIboxFMModel(Patch _patch, int _offset)
    {
        super(_patch, _offset + 10);
	flag = -1;
    }

    public MIDIboxFMModel(Patch _patch, int _offset, int _flag)
    {
        super(_patch, _offset + 10);
	flag    = _flag;
	bitmask = (1 << flag);
	mapped_values = new int[]{}; // (empty)
    }

    public MIDIboxFMModel(Patch _patch, int _offset, int _flag, int _bitmask)
    {
	super(_patch, _offset + 10);
	flag    = _flag;
	bitmask = _bitmask << flag;
	mapped_values = new int[]{}; // (empty)
    }

    public MIDIboxFMModel(Patch _patch, int _offset, int _flag, int _bitmask, int []_mapped_values)
    {
	super(_patch, _offset + 10);
	flag    = _flag;
	bitmask = _bitmask << flag;
	mapped_values = _mapped_values;
    }

    public void set(int i)
    {
	if( flag == -1 ) {
	    patch.sysex[ofs] = (byte)i;
	} else {
	    patch.sysex[ofs]=(byte)(patch.sysex[ofs]&(~bitmask));
	    if( mapped_values.length > 0 )
		patch.sysex[ofs] |= (byte)mapped_values[i];
	    else
		patch.sysex[ofs] |= (byte)i << flag;
	}
    }

    public int get()
    {
	if( flag == -1 )
	    return patch.sysex[ofs];
	else {
	    if( mapped_values.length > 0 ) {
		int value;

		value = (patch.sysex[ofs] & bitmask) >> flag;
		for(int i=0; i<mapped_values.length; ++i)
		    if( mapped_values[i] == value )
			return i;
		return 0;
	    } else
		return (patch.sysex[ofs] & bitmask) >> flag;
	}
    }
}
