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

package synthdrivers.Line6BassPod;
import core.*;

class PatchBytes {
    static byte getSysexByte(byte[] sysex, int hSize, int ofs) {
        int offset = (ofs - hSize) * 2 + hSize;
        byte upper = sysex[offset];
        byte lower = sysex[offset + 1];
        int trueValue = (upper << 4) | lower;
        return (byte)trueValue;
    }
    
    static void setSysexByte (Patch p, int hSize, int ofs, byte b) {
        int offset = (ofs - hSize) * 2 + hSize;
        int upper = (b & 0xF0) >>> 4;
        int lower = b & 0x0F;
        p.sysex[offset] = (byte)upper;
        p.sysex[offset+1] = (byte)lower;
    }
    
    static String getPatchString(byte[] sysex, int ofs, int len) {
        StringBuffer patchstring = new StringBuffer();
        
        // This code is borrowed from Patch.getPatchHeader
        for (int i = ofs; i < ofs+len; i++) {
            if ((int) (sysex[i] & 0xff) < 0x10)
                patchstring.append("0");
            patchstring.append(Integer.toHexString((int) (sysex[i] & 0xff)));
        }
        return patchstring.toString();
    }    
}
