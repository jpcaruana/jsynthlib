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

package org.jsynthlib.drivers.yamaha.ub99;

import org.jsynthlib.core.SysexSender;

class YamahaUB99Sender extends SysexSender{
    byte[] sysex;
    boolean twobytes;

    public YamahaUB99Sender (int address) {
        if (address > 0x200114)
            twobytes = false;
        else
            twobytes = true;
        sysex = new byte[(twobytes ? 12 : 11)];
        setup(address);
    }

    private void setup(int address) {
        sysex[0] = (byte) 0xF0;
        sysex[1] = (byte) 0x43;
        sysex[2] = (byte) 0x7D;
        sysex[3] = (byte) 0x40;
        sysex[4] = (byte) 0x55;
        sysex[5] = (byte) 0x42;
        sysex[6] = (byte) ((address >> 16) & 127);
        sysex[7] = (byte) ((address >> 8) & 127);
        sysex[8] = (byte) (address & 127);
        sysex[sysex.length - 1] = (byte) 0xF7;
    }

    public byte[] generate(int value) {
        if (twobytes) {
            sysex[9] = (byte)(( value >> 7 ) & 127);
            sysex[10] = (byte)(value & 127);
        } else {
            sysex[9] = (byte)(value & 127);
        }
        return sysex;
    }
}
