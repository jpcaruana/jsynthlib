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

/** For the coarse speed, we're only interested in looking at bytes 27-28. So to get the coarse value we just
do a mod 3 (or mod 0x03) instead of mod 768 (or mod 0x0300) and subtract that result from the original value.*/
class DelayCoarseSpeedModel extends ParamModel {
    private int minCC = 0;
    private int maxCC;
    private int minSysex = 0;
    private int maxSysex;
    private boolean reverse = false;
    private int scaleFactor;
    
    DelayCoarseSpeedModel(Patch p, int o) {
        this(p, o, 0, 1, 0, 1, false);
    }
    
    DelayCoarseSpeedModel(Patch p, int o, int maxCC, int maxSysex) {
        this(p, o, 0, maxCC, 0, maxSysex, false);
    }
    
    DelayCoarseSpeedModel(Patch p, int o, int minCC, int maxCC, int minSysex, int maxSysex, boolean reverse) {
        super(p, o);
        this.reverse = reverse;
        this.minCC = minCC;
        this.maxCC = maxCC;
        this.minSysex = minSysex;
        this.maxSysex = maxSysex;
        this.scaleFactor = maxSysex / maxCC;
    }
    
    public void set(int i) {
        if (reverse) {
            i = (maxCC - minCC) - (i - minCC) + minCC;
        }
        int sysexValue = ((i - minCC) * (maxSysex - minSysex) / (maxCC - minCC)) + minSysex;
        sysexValue += getFineValue();
        byte msB = (byte)(sysexValue / 256);
        byte lsB = (byte)(sysexValue % 256);
        PatchBytes.setSysexByte(patch, 9, ofs, msB);
        PatchBytes.setSysexByte(patch, 9, ofs+1, lsB);
    }
    
    public int get() {
        int returnValue = getCoarseValue();
        returnValue = ((returnValue - minSysex) * (maxCC - minCC) / (maxSysex - minSysex)) + minCC;
        if (reverse) {
            returnValue = (maxCC - minCC) - (returnValue - minCC) + minCC;
        }
        return returnValue;
    }
    
    private int getBytesValue() {
        int msB = (int)PatchBytes.getSysexByte(patch.sysex, 9, ofs);
        int lsB = (int)PatchBytes.getSysexByte(patch.sysex, 9, ofs+1);
        int bytesValue = msB << 8;
        bytesValue = bytesValue | lsB;
        return bytesValue;
    }
    
    private int getCoarseValue() {
        int coarseValue = getBytesValue() - getFineValue();
        return coarseValue;
    }
    
    private int getFineValue() {
        int fineValue = getBytesValue() % scaleFactor;
        return fineValue;
    }
}