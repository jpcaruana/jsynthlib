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
class DelayFineSpeedModel extends ParamModel {
    /** The maximum CC value that will be used for the control. This is used
    * when calculating a scaling factor. The scaling factor is calculated as 
    * maxSysex / maxCC.
    */
    private int maxCC;

    /** The maximum CC value that will be used for the control. This is used
    * when calculating a scaling factor. The scaling factor is calculated as 
    * maxSysex / maxCC.
    */
    private int maxSysex;
    
    /** Constructs a DelayFineSpeedModel. Patch p is the reference to the patch
        * containing the sysex record. int o is the offset into the sysex record
        * in non-nibblized bytes, not including the header bytes. int maxCC is 
        * the maximum CC value. int maxSysex is the max sysex value */
    DelayFineSpeedModel(Patch p, int o, int maxCC, int maxSysex) {
        super(p, o);
        this.maxCC = maxCC;
        this.maxSysex = maxSysex;
    }
    
    /** Scales the value of i according maxSysex and sets the sysex value at
        * offset ofs to the result. */
    public void set(int i) {
        int coarseValue = getCoarseValue();
        int newValue = coarseValue + (i * maxSysex / maxCC);

        byte byte1 = (byte)(newValue >>> 16);
        PatchBytes.setSysexByte(patch, 9, ofs-2, byte1);

        byte byte2 = (byte)((newValue >>> 8) & 0xFF);
        PatchBytes.setSysexByte(patch, 9, ofs-1, byte2);

        byte byte3 = (byte)(newValue & 0xFF);
        PatchBytes.setSysexByte(patch, 9, ofs, byte3);
    }
    
    /** Returns the value at offset ofs, scaled according to maxCC and maxSysex.
        */
    public int get() {
        int returnValue =  getFineValue() * maxCC / maxSysex;
        return returnValue;
    }

    /** Returns the value of the three bytes representing the delay speed as an
        * int. The value returned is the composite value representing both the
        * coarse speed and the fine speed.*/
    private int getBytesValue() {
        int msB  = (int)(0xFF & PatchBytes.getSysexByte(patch.sysex, 9, ofs-2));
        int midB = (int)(0xFF & PatchBytes.getSysexByte(patch.sysex, 9, ofs-1));
        int lsB  = (int)(0xFF & PatchBytes.getSysexByte(patch.sysex, 9, ofs));
        
        int bytesValue = msB << 16;
        bytesValue = bytesValue | midB << 8;
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
        int fineValue = getBytesValue() % (3 * 256);
        return fineValue;
    }
}