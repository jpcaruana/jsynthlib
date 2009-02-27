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

import org.jsynthlib.core.ParamModel;
import org.jsynthlib.core.Patch;

/** Scalable Double (two-byte) ParamModel--Used for two-byte sysex parameters.
* Also allows different max values for CC and Sysex.
*
* @author Jeff Weber
*/
class ScaledDblParamModel extends ParamModel {
    /** The mminimum CC value that will be used for the control. This is used
    * when calculating a scaling factor. The scaling factor is calculated as 
    * (maxSysex - minSysex) / (maxCC - minCC).
    */
    private int minCC = 0;

    /** The minimum CC value that will be used for the control. This is used
    * when calculating a scaling factor. The scaling factor is calculated as 
    * (maxSysex - minSysex) / (maxCC - minCC).
    */
    private int maxCC;

    /** The mminimum CC value that will be used for the control. This is used
        * when calculating a scaling factor. The scaling factor is calculated as 
        * (maxSysex - minSysex) / (maxCC - minCC).
        */
    private int minSysex = 0;

    /** The mminimum CC value that will be used for the control. This is used
        * when calculating a scaling factor. The scaling factor is calculated as 
        * (maxSysex - minSysex) / (maxCC - minCC).
        */
    private int maxSysex;

    /** Reverses the range of values sent by the sysexWidget over the range
        * max to min. A value of true causes the range to be reversed.
        */
    private boolean reverse = false;
    
    /** Constructs a ScaledDblParamModel. Patch p is the reference to the patch
        * containing the sysex record. int o is the offset into the sysex record
        * in non-nibblized bytes, not including the header bytes. */
    ScaledDblParamModel(Patch p, int o) {
        this(p, o, 0, 1, 0, 1, false);
    }
    
    /** Constructs a ScaledDblParamModel. This is used to scale values when the
        * max sysex value is different than the max CC value. Patch p is the
        * reference to the patch containing the sysex record. int o is the offset
        * into the sysex record in non-nibblized bytes, not including the header
        * bytes. int maxCC is the maximum CC value. int maxSysex is the max sysex
        * value*/
    ScaledDblParamModel(Patch p, int o, int maxCC, int maxSysex) {
        this(p, o, 0, maxCC, 0, maxSysex, false);
    }
    
    /** Constructs a ScaledDblParamModel. This is used to scale values when the
        * max sysex value is different than the max CC value and the minimum CC
        * and sysex values are not zero. Patch p is the reference to the patch
        * containing the sysex record. int o is the offset into the sysex record
        * in non-nibblized bytes, not including the header bytes. int minCC and
        * int maxCC are the the minimum and maximum CC values, respectively. int
        * minSysex and int maxSysex are the minimum and maximum sysex values, 
        * respectively. boolean reverse reverses the range to min to max when
        * set to true.*/
    ScaledDblParamModel(Patch p, int o, int minCC, int maxCC, int minSysex, int maxSysex, boolean reverse) {
        super(p, o);
        this.reverse = reverse;
        this.minCC = minCC;
        this.maxCC = maxCC;
        this.minSysex = minSysex;
        this.maxSysex = maxSysex;
    }
    
    /** Scales the value of i according maxSysex and sets the sysex value at
        * offset ofs to the result. Also reverses the value over the range if
        * reverse is true. */
    public void set(int i) {
        if (reverse) {
            i = (maxCC - minCC) - (i - minCC) + minCC;
        }
        int sysexValue = ((i - minCC) * (maxSysex - minSysex) / (maxCC - minCC)) + minSysex;
        byte msB = (byte)(sysexValue / 256);
        byte lsB = (byte)(sysexValue % 256);
        PatchBytes.setSysexByte(patch, 9, ofs, msB);
        PatchBytes.setSysexByte(patch, 9, ofs+1, lsB);
    }
    
    /** Returns the value at offset ofs, scaled according to maxCC and maxSysex.
        Also reverses the value over the range if reverse is true. */
    public int get() {
        int msB = 0xFF & (int)PatchBytes.getSysexByte(patch.sysex, 9, ofs);
        int lsB = 0xFF & (int)PatchBytes.getSysexByte(patch.sysex, 9, ofs+1);

        int returnValue = msB << 8;
        returnValue = returnValue | lsB;
        returnValue = ((returnValue - minSysex) * (maxCC - minCC) / (maxSysex - minSysex)) + minCC;
        if (reverse) {
            returnValue = (maxCC - minCC) - (returnValue - minCC) + minCC;
        }
        return returnValue;
    }
}

