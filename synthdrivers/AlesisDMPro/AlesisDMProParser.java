/*
 * Copyright 2004 Peter Hageus
 *
 * This file is part of JSynthLib.
 *
 * JSynthLib is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
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

package synthdrivers.AlesisDMPro;
import core.Patch;

/**
 *Support class...
 */
public class AlesisDMProParser {

    private Patch m_p = null;

    public  AlesisDMProParser(Patch p) {

        m_p = p;
   }


    /**
     * Gets value from raw bytes. nBits is number of bits
     * No parameter covers more than 2 bytes
     * Arguments are sent according to docs (compressed, "raw")
     * TODO: Variable names could be more clear
     */
    public int getValue(int nByte, int nBit, int nBits) {


        int nStartBit = (nByte * 8) + nBit;     //Picture a bitarray...
        int nStartByte = (nStartBit / 7) + 7;   //Sysex only holds 7 bits in each byte, + 7 for header
        int nBitNumber = nStartBit % 7;

        int nShiftDown = 8 - nBitNumber;
        int nRemain = (nBits + nBitNumber) - 7;

        int n1 = (m_p.sysex[nStartByte] >> nBitNumber) & LengthBitMask(nShiftDown);
        int n2 = 0;

        if (nRemain > 0)
            n2 = (m_p.sysex[nStartByte+1] & LengthBitMask(nRemain)) << (nShiftDown - 1);

        int nValue = (n1|n2) & LengthBitMask(nBits);

        return nValue;
    }
    /**
     *Masks returnvalue
     */
    private int LengthBitMask(int nBits) {
        switch (nBits) {
            case 8: return 255;
            case 7: return 127;
            case 6: return 63;
            case 5: return 31;
            case 4: return 15;
            case 3: return 7;
            case 2: return 3;
            case 1: return 1;
            case 0: return 0;
            default: return 255;
        }
    }

    /**
     * Sets value in raw bytes, and updates sysex
     * Arguments are sent according to docs (compressed, "raw")
     * TODO: Variable names could be more clear
     */
    public void setValue(int nByte, int nBit, int nBits, int nValue) {

        int nStartBit = (nByte * 8) + nBit;     //Picture a bitarray...
        int nStartByte = (nStartBit / 7) + 7;     //Sysex only holds 7 bits in each byte. + 7 for header
        int nBitNumber = nStartBit % 7;
        int nRemain = (nBits + nBitNumber) - 7;

        //Bitmask
        int nClear = 0;


        //We need to adjust mask to empty 8'th bit in sysex if it spans more than one byte
        if (nRemain > 0)
            nClear = LengthBitMask(nBits + 1);
        else
            nClear = LengthBitMask(nBits);

        nClear = nClear << nBitNumber;
        nClear ^= 0xFFFF;	//Invert


        //Current value
        int nTarget = m_p.sysex[nStartByte];

        if (nRemain > 0)
        	nTarget |= (m_p.sysex[nStartByte+1] << 8);

	    //Clear and set value
	    nTarget &= nClear;
	    //Shift value to correct pos
	    nValue = (nValue << nBitNumber);

	    //Low byte
	    nTarget |= (nValue & 127);

	    //High byte (mask lower 7 bit, and shift up to next byte
	    nTarget |= (nValue & 0xFF80) << 1;

        //Insert new value
        m_p.sysex[nStartByte] = (byte) (nTarget & 127);

        if (nRemain > 0)
        	m_p.sysex[nStartByte+1] = (byte) ((nTarget >> 8) & 127);

    }

}
