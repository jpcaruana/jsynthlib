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

package org.jsynthlib.drivers.line6.pod20;

import org.jsynthlib.core.ParamModel;
import org.jsynthlib.core.Patch;

/** Delay Speed is represented by three bytes in offsets 27-29. There are two
* controls in the editor, one for coarse adjustment and the other for fine
* adjustment. The range for coarse sysex values is 0-97536, where the max value
* is represented in bytes 27-29 as 0x017D00. The range for fine sysex values is 
* 0-762, where the max value is represented in bytes 27-29 as 0x0002FA. The value
* stored in offset 27-29 of the sysex record is simply the sum of the course and
* fine values. So the value with the coarse and fine values both set to maximum
* is 98298 or 0x017FFA. For the coarse control the control range of 0-127 split
* over the range 0-97536 gives steps of 768 or 0x300. The two values are 
* separated as follows:
* <br />
* <code>
*     Coarse Value = Combined Value mod 300
* <br />
*     Fine Value = Combined Value - Coarse Value
* </code>
*
* @author Jeff Weber
*/
class DelayCoarseSpeedModel extends ParamModel {
    /** The minimum CC value that will be used for the control. This is used
    * when calculating a scaling factor. The scaling factor is calculated as 
    * (maxSysex - minSysex) / (maxCC - minCC).
    */
    private int minCC = 0;

    /** The maximum CC value that will be used for the control. This is used
    * when calculating a scaling factor. The scaling factor is calculated as 
    * (maxSysex - minSysex) / (maxCC - minCC).
    */
    private int maxCC;

    /** The minimum CC value that will be used for the control. This is used
        * when calculating a scaling factor. The scaling factor is calculated as 
        * (maxSysex - minSysex) / (maxCC - minCC).
        */
    private int minSysex = 0;

    /** The maximum CC value that will be used for the control. This is used
        * when calculating a scaling factor. The scaling factor is calculated as 
        * (maxSysex - minSysex) / (maxCC - minCC).
        */
    private int maxSysex;

    /** The scaling factor, calculated as 
        * (maxSysex - minSysex) / (maxCC - minCC).
        */
    private int scaleFactor;
    
    /** Constructs a DelayCoarseSpeedModel. Patch p is the reference to the patch
        * containing the sysex record. int o is the offset into the sysex record
        * in non-nibblized bytes, not including the header bytes. int maxCC is 
        * the maximum CC value. int maxSysex is the max sysex value */
    DelayCoarseSpeedModel(Patch p, int o, int maxCC, int maxSysex) {
        this(p, o, 0, maxCC, 0, maxSysex);
    }
    
    /** Constructs a DelayCoarseSpeedModel. Patch p is the reference to the patch
        * containing the sysex record. int o is the offset into the sysex record
        * in non-nibblized bytes, not including the header bytes. int min CC and
        * int maxCC are the minimum and maximum CC values, respectively. int 
        * minSysex and int maxSysex are the minimum and maximum sysex values, 
        * respectively */
    private DelayCoarseSpeedModel(Patch p, int o, int minCC, int maxCC, int minSysex, int maxSysex) {
        super(p, o);
        this.minCC = minCC;
        this.maxCC = maxCC;
        this.minSysex = minSysex;
        this.maxSysex = maxSysex;
        this.scaleFactor = maxSysex / maxCC;
    }
    
    /** Scales the value of i according scaleFactor and sets the sysex value at
        * offset ofs to the result. */
    public void set(int i) {
        int sysexValue = ((i - minCC) * (maxSysex - minSysex) / (maxCC - minCC)) + minSysex;
        sysexValue += getFineValue();
        byte msB = (byte)(sysexValue / 256);
        byte lsB = (byte)(sysexValue % 256);
        PatchBytes.setSysexByte(patch, 9, ofs, msB);
        PatchBytes.setSysexByte(patch, 9, ofs+1, lsB);
    }
    
    /** Returns the value at offset ofs, scaled according to scaleFactor.
        */
    public int get() {
        int returnValue = getCoarseValue();
        returnValue = ((returnValue - minSysex) * (maxCC - minCC) / (maxSysex - minSysex)) + minCC;
        return returnValue;
    }
    
    /** Returns the value of the two bytes representing the delay speed as an
        * int. The value returned is the composite value representing both the
        * coarse speed and the fine speed.*/
    private int getBytesValue() {
        int msB = (int)PatchBytes.getSysexByte(patch.sysex, 9, ofs);
        int lsB = (int)PatchBytes.getSysexByte(patch.sysex, 9, ofs+1);
        int bytesValue = msB << 8;
        bytesValue = bytesValue | lsB;
        return bytesValue;
    }
    
    /** Returns the coarse speed value.*/
    private int getCoarseValue() {
        int coarseValue = getBytesValue() - getFineValue();
        return coarseValue;
    }
    
    /** Returns the fine speed value.*/
    private int getFineValue() {
        int fineValue = getBytesValue() % scaleFactor;
        return fineValue;
    }
}