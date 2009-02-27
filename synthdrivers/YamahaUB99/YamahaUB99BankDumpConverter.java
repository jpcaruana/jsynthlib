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

package synthdrivers.YamahaUB99;

import core.*;
import javax.sound.midi.SysexMessage;

import org.jsynthlib.core.Converter;
import org.jsynthlib.core.Patch;

public class YamahaUB99BankDumpConverter extends Converter {

    private final YamahaUB99Driver singleDriver;

    public YamahaUB99BankDumpConverter(YamahaUB99Driver singleDriver) {
        super("Yamaha UB99 Bankdump Converter", "Ton Holsink <a.j.m.holsink@chello.nl>");

        this.sysexID = "F0437D305542393900003001004FF7";
        this.patchSize = 0;
        this.singleDriver = singleDriver;
    }

    public boolean supportsPatch (String patchString, byte[] sysex) {
        StringBuffer compareString = new StringBuffer();
        for (int i = 0; i < sysexID.length(); i++) {
            compareString.append(sysexID.charAt(i));
        }

        boolean ok = compareString.toString().equalsIgnoreCase
                (patchString.substring(0, sysexID.length()));

        return ok;
    }

    public Patch[] extractPatch(Patch p) {
        byte[] header = {85, 66, 57, 57, 32, 86, 49, 46, 48, 48};
        byte[] sysex = new byte[YamahaUB99Const.BANK_SIZE];
        System.arraycopy(header, 0, sysex, 0, header.length);
        System.arraycopy(header, 0, sysex, 64, header.length);

        SysexMessage[] sm = p.getMessages();
        byte[] msg;
        int nr = -1;
        for (int i = 0; i < sm.length; i++) {
            msg = sm[i].getMessage();

            //Signals start of patch data
            if ((msg[9] == (byte)0x00) && (msg[10] == (byte)0x30) && (msg[11] == (byte)0x01) && (nr == -1)) {
                nr = msg[12];
            }

            //Part 1 of patch data
            if ((msg[9] == (byte)0x20) && (msg[10] == (byte)0x20) && (nr > -1)) {
                System.arraycopy(msg, 13, sysex, YamahaUB99Const.BANK_PATCH_OFFSET + nr*YamahaUB99Const.SINGLE_SIZE, 32);
                System.arraycopy(msg, 29, sysex, YamahaUB99Const.BANK_NAME_OFFSET + nr*YamahaUB99Const.NAME_SIZE, YamahaUB99Const.NAME_SIZE);
            }

            //Part 2 of patch data
            if ((msg[9] == (byte)0x7F) && (msg[10] == (byte)0x20) && (nr > -1)) {
                System.arraycopy(msg, 13, sysex, YamahaUB99Const.BANK_PATCH_OFFSET + nr*YamahaUB99Const.SINGLE_SIZE + 32, 127);
            }

            //Signals end of patch data
            if ((msg[9] == (byte)0x00) && (msg[10] == (byte)0x30) && (msg[11] == (byte)0x11) && (msg[12] == nr)) {
                nr = -1;
            }

        }

        Patch[] newPatchArray = new Patch[1];
        newPatchArray[0] = new Patch(sysex, new YamahaUB99BankDriver(singleDriver));
        return newPatchArray;
    }

}
