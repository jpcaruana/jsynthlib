//
//  PatchBytes.java
//  JSynthLib
//
//  Created by Jeff Weber on Sat Aug 07 2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package synthdrivers.Line6Pod20;
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
