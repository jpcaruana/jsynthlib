/*
* Copyright 2005 Ton Holsink
*
* This file is part of JSynthLib.
*
* JSynthLib is free software; you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published
* by the Free Software Foundation; either version 2 of the License,
* or(at your option) any later version.
*
* JSynthLib is distributed in the hope that it will be useful, but
* WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
* General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with JSynthLib; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
* USA
*/

package synthdrivers.YamahaUB99;

import org.jsynthlib.core.ParamModel;
import org.jsynthlib.core.Patch;

import core.*;

class YamahaUB99Model extends ParamModel {
    boolean twobytes;

    public YamahaUB99Model (Patch p, int address) {
        super(p, address);
        if (address > 0x200114)
            twobytes = false;
        else
            twobytes = true;
        ofs = (byte)address;
        byte mid = (byte) (address >> 8);
        if (mid == (byte)0x01)
            ofs += 32;
    }

    private int getShortParameter() {
        int retval = patch.sysex[ofs] << 7;
        retval |= (patch.sysex[ofs + 1] & 127);
        return retval;
    }

    private void setShortParameter(int value) {
        patch.sysex[ofs] = (byte)((value >> 7) & 127);
        patch.sysex[ofs + 1] = (byte)(value & 127);
    }

    public void set(int value) {
        if (twobytes)
            setShortParameter(value);
        else
            patch.sysex[ofs] = (byte)value;
    }

    public int get () {
        if (twobytes)
            return getShortParameter();
        else
            return patch.sysex[ofs];
    }
}
