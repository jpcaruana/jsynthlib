/*
 * Copyright 2005 Bill Zwicky
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
package synthdrivers.CasioCZ1000;

import core.ParamModel;
import core.Patch;


/** 
 * Unmangle CZ data.  Nybbles in a byte are swapped, and bytes in a word 
 * are *sometimes* swapped, then each nybble is stored in its own byte.  
 * So 0xABCD is stored as 0D 0C 0B 0A.
 * <P>
 * Offset constants are relative to the start of the "tone data" (sysex
 * payload area.)  The offset to the payload area is 7 for outgoing messages,
 * and 6 for incoming.  JSL converts incoming patches to the outgoing format,
 * so patches in memory always have the 7 byte header.
 */
class CZModel extends ParamModel {
    public CZModel(Patch p, int offset) {
        super(p,offset);
    }
    
    /**
     * Assemble lower nybbles from two bytes to make a data byte.
     * @param offset index of first byte, relative to start of sysex.
     * @return byte value, 0..255
     */
    public int getByte(int offset) {
        int b = patch.sysex[offset] + (patch.sysex[offset+1]<<4);
        return b;
    }
    
    /**
     * Save a byte value into the lower nybbles of two bytes.
     * @param offset index of first byte, relative to start of sysex.
     * @param value 0..255
     */
    public void setByte(int offset, int value) {
        patch.sysex[offset]   = (byte)((value)&0x0F);
        patch.sysex[offset+1] = (byte)((value>>4)&0x0F);
    }
    
    /**
     * Assemble four nybbles into a 16-bit word.
     * LSB comes first. 
     * @param offset index of first byte, relative to start of sysex.
     * @return word value, 0..64K
     */
    public int getWordL(int offset) {
        int w = patch.sysex[offset] + (patch.sysex[offset+1]<<4)
              + (patch.sysex[offset+2]<<8) + (patch.sysex[offset+3]<<12);
        return w;
    }

    /**
     * Save a 16-bit word into the lower nybbles of four bytes.
     * LSB comes first. 
     * @param offset index of first byte, relative to start of sysex.
     * @param value 0..64K
     */
    public void setWordL(int offset, int value) {
        patch.sysex[offset]   = (byte)((value)&0x0F);
        patch.sysex[offset+1] = (byte)((value>>4)&0x0F);
        patch.sysex[offset+2] = (byte)((value>>8)&0x0F);
        patch.sysex[offset+3] = (byte)((value>>12)&0x0F);
    }
    
    /**
     * Assemble four nybbles into a 16-bit word.
     * MSB comes first.
     * @param offset index of first byte, relative to start of sysex.
     * @return word value, 0..64K
     */
    public int getWordH(int offset) {
        int w = patch.sysex[offset+2] + (patch.sysex[offset+3]<<4)
              + (patch.sysex[offset]<<8) + (patch.sysex[offset+1]<<12);
        return w;
    }

    /**
     * Save a 16-bit word into the lower nybbles of four bytes.
     * MSB comes first.
     * @param offset index of first byte, relative to start of sysex.
     * @param value 0..64K
     */
    public void setWordH(int offset, int value) {
        patch.sysex[offset+2] = (byte)((value)&0x0F);
        patch.sysex[offset+3] = (byte)((value>>4)&0x0F);
        patch.sysex[offset]   = (byte)((value>>8)&0x0F);
        patch.sysex[offset+1] = (byte)((value>>12)&0x0F);
    }
    

    /** Line select, octave */
    public static final int PFLAG	= 0;
    /** Detune +/- */
    public static final int PDS		= 2;
    /** Detune Fine */
    public static final int PDL		= 4;
    /** Detune Step */
    public static final int PDH		= 6;
    /** Vibrato Wave */
    public static final int PVK		= 8;
    /** Vibrato Delay (displayed value) */
    public static final int PVDLD	= 10;
    /** Vibrato Delay (internal value) */ 
    public static final int PVDLV	= 12;
    /** Vibrato Rate (displayed value) */
    public static final int PVSD	= 16;
    /** Vibrato Rate (internal value) */
    public static final int PVSV	= 18;
    /** Vibrato Depth (displayed value) */
    public static final int PVDD	= 22;
    /** Vibrato Depth (internal value) */
    public static final int PVDV	= 24;

    /** DCO1 Waveform */
    public static final int MFW		= 28;
    /** DCA1 Key Follow (displayed value) */
    public static final int MAMD	= 32;
    /** DCA1 Key Follow (internal value) */
    public static final int MAMV	= 34;
    /** DCW1 Key Follow (displayed value) */
    public static final int MWMD	= 36;
    /** DCW1 Key Follow (internal value) */
    public static final int MWMV	= 38;

    /** END step of DCA1 envelope */
    public static final int PMAL	= 40;
    /** DCA1 envelope data */
    public static final int PMA		= 42;
    /** END step of DCW1 envelope */
    public static final int PMWL	= 74;
    /** DCW1 envelope data */
    public static final int PMW		= 76;    
    /** END step of DCO1 envelope */
    public static final int PMPL	= 108;
    /** DCO1 envelope data */
    public static final int PMP		= 110;
    
    /** DCO2 Waveform */
    public static final int SFW		= 142;
    /** DCA2 Key Follow (displayed value) */
    public static final int SAMD	= 146;
    /** DCA2 Key Follow (internal value) */
    public static final int SAMV	= 148;
    /** DCW2 Key Follow (displayed value) */
    public static final int SWMD	= 150;
    /** DCW2 Key Follow (internal value) */
    public static final int SWMV	= 152;
    
    /** END step of DCA2 envelope */
    public static final int PSAL	= 154;
    /** DCA2 envelope data */
    public static final int PSA		= 156;
    /** END step of DCW2 envelope */
    public static final int PSWL	= 188;
    /** DCW2 envelope data */
    public static final int PSW		= 190;    
    /** END step of DCO2 envelope */
    public static final int PSPL	= 222;
    /** DCO2 envelope data */
    public static final int PSP		= 224;

    /** End marker (0xF7) for sysex message */
    public static final int SYSEX_END_MARKER = 258;
}


/*
Rough structure of patch dump from synth.
Header is 6 bytes from synth, 7 bytes to synth.
1st column is offset from start of "tone data" area.

sysex:  f0 44 00 00 70 30
00 PFLAG   00 00 
02 PDS     00 00 
04 PDL,PDH 00 00 00 00
08 PVK     08 00
10 PVDLD,V 00 00 00 00 00 00 
16 PVSD,V  00 00 00 02 00 00 
22 PVDD,V  00 00 01 00 00 00 
28 MFW     00 00 00 00 
32 MAMD,V  00 00 00 00 
36 MWMD,V  00 00 00 00 
40 PMAL    07 00
42 PMA     07 07 0f 0f 0c 0b 00 00 0c 03 00 00 0c 03 00 00
58         0c 03 00 00 0c 03 00 00 0c 03 00 00 0c 0b 00 00
74 PMWL    07 00 
76 PMW     0f 07 0f 0f 04 0c 00 00 04 04 00 00 04 04 00 00 
92         04 04 00 00 04 04 00 00 04 04 00 00 04 0c 00 00 
108 PMPL    07 00 
110 PMP     00 04 00 08 00 04 00 00 00 04 00 00 00 04 00 00 
126         00 04 00 00 00 04 00 00 00 04 00 00 00 0c 00 00 
142 SFW     00 00 00 00
146 SAMD,V  00 00 00 00 
150 SWMD,V  00 00 00 00 
154 PSAL    07 00
156 PSA     07 07 0f 0f 0c 0b 00 00 0c 03 00 00 0c 03 00 00 
172         0c 03 00 00 0c 03 00 00 0c 03 00 00 0c 0b 00 00

188 PSWL    07 00 
190 PSW     0f 07 0f 0f 04 0c 00 00 04 04 00 00 04 04 00 00
206         04 04 00 00 04 04 00 00 04 04 00 00 04 0c 00 00
222 PSPL    07 00 
224 PSP     00 04 00 08 00 04 00 00 00 04 00 00 00 04 00 00
240         00 04 00 00 00 04 00 00 00 04 00 00 00 0c 00 00
258 end     f7
*/
