/*
 * Copyright 2005 Jeff Weber
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

package synthdrivers.BehringerFCB1010;

import core.*;

/** Dummy do-nothing model. This model is used for the Global Wah on/off setting.
* Pod supports a CC number for Wah on off but it's not represented in the Sysex record.
*
* @author Jeff Weber
*/
class FCB1010ParamModel extends ParamModel {
    static final byte LSB_MASK      = (byte)0x7F;  //0111 1111
    static final byte MSB_MASK      = (byte)0x80;  //1000 0000
    static final byte BYTE          = (byte)0xFF;  //1111 1111

    static private final int PR_LENGTH = 16;

    static private int prOffset = 0;

    private byte mask;
    private int power = 0;
    private boolean reverse;
    
    /** Constructs a FCB1010ParamModel given the patch, the offset into the first preset,
        * the mask representing the parameter, and a boolean (reverse, used only with the MSB mask).
        * Normally, the MSB values are reversed (0 for 1 and 1 for 0). To prevent this from
        * happening a value of false can be set for the reverse parameter.
        */
    FCB1010ParamModel(Patch p, int offset, byte mask, boolean reverse) {
        super(p, offset);
        this.mask = mask;
        this.reverse = reverse;
        prOffset = 0;
        
        int bitPos = 1;
        int rem = 0;
        while (rem == 0) { 
            bitPos *= 2;
            power += 1;
            rem = mask % bitPos;
        }
        power -= 1;
    } 
    
    /** Constructs a FCB1010ParamModel given the patch, the offset into the first preset,
        * and the mask representing the parameter.
        */
    FCB1010ParamModel(Patch p, int offset, byte mask) {
        this(p, offset, mask, true);
    } 
    
    static void setPreset(int bank, int preset) {
        prOffset = ((bank * 10) + preset) * PR_LENGTH;
    }
    
	static byte[] deNibblize(byte[] array, int hdrSize) {
		byte endOfSysex = (byte)0xF7;
        
		byte[] deNibblizedArray = new byte[array.length];
        System.arraycopy(array, 0, deNibblizedArray, 0, hdrSize);
        
        byte[] msbArray = new byte[array.length / 8];
        
        // Remove msb bytes (every eigth byte following the header),
        // Scrunch everything down and place the result into deNibblizedArray.
        // Put msb bytes into separate array (msbArray)
        int i = 0;
        int j = 0;
        int k = 0;
        for (j = 0; array[j+hdrSize] != endOfSysex; j++) {
            if ((j+1) % 8 != 0) {
                deNibblizedArray[hdrSize + k++] = array[j+hdrSize];
            } else {
                msbArray[i++] = array[j+hdrSize];
            }
		}
        deNibblizedArray[hdrSize + k] = endOfSysex;
        
        // Parse out the msbs and apply to the data in deNibblizedArray
        i = -1;
        for (j = 0; j <= k-7; j = j + 7) {
            for (int l = 0; l < 7; l++) {
            }
            i++;
            for (int l = 0; l < 7; l++) {
                byte cByte = (byte)(msbArray[i] << 7-l);
                cByte = (byte)(cByte & 0x80);
                cByte = (byte)(deNibblizedArray[j+hdrSize + l] | cByte);
                deNibblizedArray[j+hdrSize + l] = cByte;
            }
        }
        
		deNibblizedArray[hdrSize + k] = endOfSysex;
		byte returnArray[] = new byte[hdrSize + k + 1];
        System.arraycopy(deNibblizedArray, 0, returnArray, 0, returnArray.length);
		return returnArray;
	}
	
	static byte[] reNibblize(byte[] array, int hdrSize) {
		byte[] reNibblizedArray = new byte[Constants.HDR_SIZE + Constants.FCB1010_NATIVE_SIZE + 1];
        System.arraycopy(array, 0, reNibblizedArray, 0, hdrSize);
		
        int j = 0;
        for (int i = 0; i < array.length - hdrSize; i++) {
            if ((j + 1) % 8 == 0) {
                int cByte = 0x00;
                for (int k = 1; k < 8; k++) {
                    int dByte = reNibblizedArray[hdrSize + j - k];
                    dByte = (dByte & 0x80);
                    dByte = (dByte >>> k);
                    cByte = (cByte | dByte);
                    reNibblizedArray[hdrSize + j - k] = (byte)(reNibblizedArray[hdrSize + j - k] & 0x7f);
                }
                reNibblizedArray[hdrSize + j++] = (byte)cByte;
            } 
            reNibblizedArray[hdrSize + j++] = array[hdrSize + i];
        }
        
		byte endOfSysex = (byte)0xF7;
		reNibblizedArray[reNibblizedArray.length - 1] = endOfSysex;
		return reNibblizedArray;
	}
	
    /** Updates the bits defined by mask within the byte in the sysex record 
        * defined by offset with the value supplied by i.
        */
    public void set(int i) {
        i = reverseMSB(i);

        byte[] denibblizedArray = deNibblize(this.patch.sysex, Constants.HDR_SIZE);
        
        byte newPatchVal = (byte)((denibblizedArray[getOffset()] & (~mask)) | ((i << power) & mask));
        denibblizedArray[getOffset()] = newPatchVal;
        
//        dump(denibblizedArray); //Test Code
        
        byte[] renibblizedArray = reNibblize(denibblizedArray, Constants.HDR_SIZE);
        System.arraycopy(renibblizedArray, 0, this.patch.sysex, 0, this.patch.sysex.length);
    }
    
    /** Gets the value of the bits defined by mask within the byte in the
        * sysex record defined by offset and returns the value as in int.
        */
    public int get() {
        byte[] denibblizedArray = deNibblize(this.patch.sysex, Constants.HDR_SIZE);
        int patchVal = (byte)(denibblizedArray[getOffset()] & mask);
        patchVal = patchVal & (int)0x000000FF;
        patchVal = (patchVal >>> power);
        patchVal = reverseMSB(patchVal);
        return patchVal;
    }
    
    private int getOffset() {
        int realOffset;
        if (this.mask == BYTE) {
            realOffset = Constants.HDR_SIZE + ofs;  // Used for Global MIDI channels
        } else {
            realOffset = Constants.HDR_SIZE + prOffset + ofs;  // Used for preset parameters
        }
        return realOffset;
    }
    
    private int reverseMSB (int patchVal) {
        if (reverse) {
            if (mask == MSB_MASK) {
                if (patchVal == 0) {
                    patchVal = 1;
                } else {
                    patchVal = 0;
                }
            }
        }
        return patchVal;
    }
    
/*
    static void dump(byte[] bytes) {
        for (int i = 0; i < 10; i++) {
            System.out.println("Bank " + i + " -----");
            System.out.println("  " + (Utility.hexDump(bytes, Constants.HDR_SIZE + (160 * i), 160, 16)));
        }
    }
*/ 
}