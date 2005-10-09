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

package synthdrivers.TCElectronicGMajor;

import core.*;

class TCBitModel extends ParamModel {
    protected int mask;
    protected int shift;

    TCBitModel(Patch p, int offset, int mask) {
        super(p, offset);

        this.mask = mask;

        shift = 0;
        int j = mask;
        if (j != 0) {
            while ((j & 1) == 0) {
                shift++;
                j = (j >> 1);
            }
        }
    }

    protected int getValue() {
        int value = (patch.sysex[ofs+1] << 7);
        value = (value ^ patch.sysex[ofs]);
        return value;
    }

    public void set(int i) {
        int j = ((getValue() & (~mask)) | ((i << shift) & mask));

        patch.sysex[ofs+1] = (byte)((j >> 7) & 127);
        patch.sysex[ofs] = (byte)(j & 127);

    }

    public int get() {
        int result = ((getValue() & mask) >> shift);
        return result;
    }

}
