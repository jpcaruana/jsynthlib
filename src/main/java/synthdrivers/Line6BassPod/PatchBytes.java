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
import org.jsynthlib.core.Patch;

/** Utility class for converting between nibblized and un-nibblized formats of
* Line6 sysex records.
*
* @author Jeff Weber
*/
class PatchBytes {
    /** Converts two nibblized bytes to a single byte. The value of ofs
    * refers to the position within the un-nibblized record.
    */
    static byte getSysexByte(byte[] sysex, int hSize, int ofs) {
        int offset = (ofs - hSize) * 2 + hSize;
        byte upper = sysex[offset];
        byte lower = sysex[offset + 1];
        int trueValue = (upper << 4) | lower;
        return (byte)trueValue;
    }
    
    /** Converts a single byte to two nibblized bytes and writes it to the 
    * nibblized sysex record at the position given by ofs. The value of ofs
    * refers to the position within the un-nibblized record.
    */
    static void setSysexByte (Patch p, int hSize, int ofs, byte b) {
        int offset = (ofs - hSize) * 2 + hSize;
        int upper = (b & 0xF0) >>> 4;
        int lower = b & 0x0F;
        p.sysex[offset] = (byte)upper;
        p.sysex[offset+1] = (byte)lower;
    }
    
    /** Converts the bytes in a nibblized sysex records to a String, starting at
    * offset, ofs for len bytes. The value of ofs is the position within the 
    * un-nibblized sysex record and the length, len is given in un-nibblized
    * bytes.
    */
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
