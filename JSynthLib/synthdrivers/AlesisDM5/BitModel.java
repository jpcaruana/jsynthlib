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

package synthdrivers.AlesisDM5;

import core.*;

/** The BitModel class allows a control to set the individual bits in a byte
* of the patch.sysex record. This is used when a single byte of the sysex record
* contains more than one parameter. Masks are provided for each of the parameters
* of the DM5 to define which bits are used for a given parameter.
*/
class BitModel extends ParamModel {
    static final byte ROOT_NOTE_MASK = (byte)0x7F;  //0111 1111
    static final byte NOTE_MASK      = (byte)0x3F;  //0011 1111
    static final byte VOL_MASK       = (byte)0x7F;  //0111 1111
    static final byte PAN_MASK       = (byte)0x70;  //0111 0000
    static final byte OUTP_MASK      = (byte)0x08;  //0000 1000
    static final byte BANK_MASK      = (byte)0x07;  //0000 0111
    static final byte DRUM_MASK      = (byte)0x7F;  //0111 1111
    static final byte FINE_TUNE_MASK = (byte)0x7F;  //0111 1111
    static final byte GROUP_MASK     = (byte)0x18;  //0001 1000
    static final byte CRSE_TUNE_MASK = (byte)0x07;  //0000 0111
    
    protected Patch patch;
    protected int ofs;
    protected byte mask;
    protected int power = 0;
    
    /** Constructs a BitModel given the patch, the offset into the sysex record,
        * and the mask representing the parameter.
        */
    BitModel(Patch p, int offset, byte mask) {
        this.patch = p;
        this.ofs = offset;
        this.mask = mask;
        
        int bitPos = 1;
        int rem = 0;
        while (rem == 0) { 
            bitPos *= 2;
            power += 1;
            rem = mask % bitPos;
        }
        power -= 1;
    }
    
    /** Updates the bits defined by mask within the byte in the sysex record 
        * defined by offset with the value supplied by i.
        */
    public void set(int i) {
        patch.sysex[ofs] = (byte)((patch.sysex[ofs] & (~mask)) | ((i << power) & mask));
    }
    
    /** Gets the value of the bits defined by mask within the byte in the
        * sysex record defined by offset and returns the value as in int.
        */
    public int get() {
        return ((patch.sysex[ofs] & mask) >> power);
    }
}
