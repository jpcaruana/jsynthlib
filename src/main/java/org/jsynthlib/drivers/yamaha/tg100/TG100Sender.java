/*
 * Copyright 2004 Joachim Backhaus
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
package org.jsynthlib.drivers.yamaha.tg100;

/**
 * Sender for the Yamaha TG-100 synthdriver
 *
 * @author  Joachim Backhaus
 * @version $Id$
 */

import org.jsynthlib.core.SysexSender;

public class TG100Sender extends SysexSender {

    private static final int SYSEX_2BYTE_LENGTH = 11;
    private static final int SYSEX_1BYTE_LENGTH = 10;
    private int offsetAddress;
    private byte[] sysex;

    // Only 0x10 works here
    protected byte channel = (byte) 0x10;

    public TG100Sender(int offsetAddress) {
        this(offsetAddress, false);
    }

    public TG100Sender(int offsetAddress, boolean has2ByteValue) {
        this.offsetAddress = offsetAddress;

        // 2 Byte values are at offset addresses: 0x18, 0x26, 0x28, 0x30, 0x32
        if(has2ByteValue) {
            sysex = new byte[SYSEX_2BYTE_LENGTH];

            // Data 2nd byte
            sysex[8]  = (byte) 0x00;

            // Checksum
            sysex[9] = (byte) 0x00;

            sysex[10] = TG100Constants.SYSEX_END_BYTE;
        }
        else {
            sysex = new byte[SYSEX_1BYTE_LENGTH];

            // Checksum
            sysex[8] = (byte) 0x00;

            sysex[9] = TG100Constants.SYSEX_END_BYTE;
        }

        sysex[0]  = TG100Constants.SYSEX_START_BYTE;
        sysex[1]  = (byte) 0x43;
        sysex[2]  = channel; // Device number
        sysex[3]  = (byte) 0x27;

        // Internal voice
        sysex[4]  = (byte) 0x30;
        sysex[5]  = (byte) ((offsetAddress / 128) + TG100Constants.SYSEX_VOICE_START_ADDRESS2 );
        sysex[6]  = (byte) (offsetAddress % 128);

        // Data
        sysex[7]  = (byte) 0x00;
    }

    private byte[] generate2ByteValue(int value) {
        int sum = 0;
        int bitmask_1111     = 0x0F;
        int bitmask_11110000 = 0xF0;

        // Data
        sysex[7]  = (byte)((value & TG100Constants.BITMASK_11110000) >> 4);
        sysex[8]  = (byte) (value & TG100Constants.BITMASK_1111);

        sum = sysex[4] + sysex[5] + sysex[6] + sysex[7] + sysex[8];

        // Checksum
        sysex[9] = (byte) (-sum & 0x7f);;

        return sysex;
    }

    private byte[] generate1ByteValue(int value) {
        int sum = 0;

        // Data
        sysex[7]  = (byte) value;

        sum = sysex[4] + sysex[5] + sysex[6] + sysex[7];

        // Checksum
        sysex[8] = (byte) (-sum & 0x7f);;

        return sysex;
    }

    public byte[] generate(int value) {
        if(sysex.length == SYSEX_2BYTE_LENGTH) {
            return generate2ByteValue(value);
        }
        else {
            return generate1ByteValue(value);
        }
    }
}


