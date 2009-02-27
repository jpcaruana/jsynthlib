/*
 * Copyright 2005 Ton Holsink
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

package synthdrivers.TCElectronicGMajor;

// import core.*;

class TCBit {
//     static char hexDigits[] = {'0', '1', '2', '3','4', '5', '6', '7','8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    private int m_mask;
    private int m_shift;
    private int m_ofs;
    private byte[] m_sysex;


    /** Constructs a BitModel given the patch, the offset into the m_sysex record,
        * and the m_mask representing the parameter.
        */
    TCBit(byte[] sysex, int offset, int mask) {
        m_sysex = sysex;
        m_ofs = offset;
        m_mask = mask;

        calcShift();
    }

    TCBit(byte[] sysex) {
        this(sysex, 0, 0);
    }

    private void calcShift() {
        m_shift = 0;
        int j = m_mask;
        if (j != 0) {
            while ((j & 1) == 0) {
                m_shift++;
                j = (j >> 1);
            }
        }
    }

    public void setMask(int mask) {
        m_mask = mask;
        calcShift();
    }

    public void setOffset(int offset) {
        m_ofs = offset;
    }

    public void setMaskOffset(int mask, int offset) {
        setMask(mask);
        setOffset(offset);
    }

    private int getValue() {
//         System.out.println(">>>>GETVALUE :" + printHex() + " " + printBin());
        int value = (m_sysex[m_ofs+1] << 7);
        value = (value ^ m_sysex[m_ofs]);
        return value;
    }

    /** Updates the bits defined by m_mask within the byte in the m_sysex record
        * defined by offset with the value supplied by i.
        */
    public void set(int i) {
//         int v = getValue(); //Actuele waarde ophalen

        int j = ((getValue() & (~m_mask)) | ((i << m_shift) & m_mask));

        m_sysex[m_ofs+1] = (byte)((j >> 7) & 127);
        m_sysex[m_ofs] = (byte)(j & 127);

//         System.out.println(">>>>SET :" + i + " " + printHex() + " " + printBin());
    }

    /** Gets the value of the bits defined by m_mask within the byte in the
        * m_sysex record defined by offset and returns the value as in int.
        */
    public int get() {
        int result = ((getValue() & m_mask) >> m_shift);
//         System.out.println(">>>>GET: value = " + result);
        return result;
    }


/*    String byteToHex(byte i) {
        return ""+hexDigits[(i & 0xF0) >> 4] +hexDigits[i & 0x0F];
    }

    String printHex() {
        return byteToHex(m_sysex[m_ofs]) + byteToHex(m_sysex[m_ofs+1]);

    }

    String printBin() {
        return Integer.toBinaryString(m_sysex[m_ofs]) + " " + Integer.toBinaryString(m_sysex[m_ofs+1]);

    }
*/
}
