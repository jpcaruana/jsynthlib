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

import org.jsynthlib.core.Converter;
import org.jsynthlib.core.Patch;

import core.*;

public class YamahaUB99Converter extends Converter {

    private final YamahaUB99Driver singleDriver;

    public YamahaUB99Converter(YamahaUB99Driver singleDriver) {
        super("Yamaha UB99 Converter", "Ton Holsink <a.j.m.holsink@chello.nl>");

        this.sysexID = "554239392056312E3030";
        this.patchSize = 0;
        this.singleDriver = singleDriver;
    }

    public boolean supportsPatch (String patchString, byte[] sysex) {
        StringBuffer compareString = new StringBuffer();
        for (int i = 0; i < sysexID.length(); i++) {
            compareString.append(sysexID.charAt(i));
        }

        boolean ok = (compareString.toString().equalsIgnoreCase
                (patchString.substring(0, sysexID.length()))) && (sysex.length == YamahaUB99Const.BANK_SIZE);

        return ok;
    }

    public Patch[] extractPatch(Patch p) {
        byte[] sysex = p.getByteArray();
        sysex[0] = (byte) 0xF0;
        Patch[] newPatchArray = new Patch[1];
        newPatchArray[0] = new Patch(sysex, new YamahaUB99BankDriver(singleDriver));
        return newPatchArray;
    }

}
