/*
 * Copyright 2004 Jeff Weber
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

package synthdrivers.Line6Pod20;

import core.*;

/** Scalable ParamModel--to allow different max values for CC and Sysex*/
class ScaledParamModel extends ParamModel {
    private int maxCC;
    private int maxSysex;
    private boolean reverse = false;
    
    ScaledParamModel(Patch p, int o) {
        this(p, o, 1, 1);
    }
    
    ScaledParamModel(Patch p, int o, int maxCC, int maxSysex) {
        this(p, o, maxCC, maxSysex, false);
    }
    
    ScaledParamModel(Patch p, int o, int maxCC, int maxSysex, boolean reverse) {
        super(p, o);
        this.maxCC = maxCC;
        this.maxSysex = maxSysex;
        this.reverse = reverse;
    }
    
    public void set(int i) {
        if (reverse) {
            i = (maxSysex - i);
        }
        PatchBytes.setSysexByte(patch, 9, ofs, (byte)(i * maxSysex / maxCC));
    }
    
    public int get() {
        int returnValue = (int)PatchBytes.getSysexByte(patch.sysex, 9, ofs) * maxCC / maxSysex;
        return returnValue;
    }
}
