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

package org.jsynthlib.drivers.tcelectronic.gmajor;

import org.jsynthlib.core.Patch;
import org.jsynthlib.core.SysexSender;


class TCSender extends SysexSender {
    int offs, delta;
    Patch patch;

    public TCSender(Patch iPatch, int iParam) {
        patch = iPatch;
        offs = iParam;
    }

    public TCSender(Patch iPatch, int iParam, int idelta) {
        this(iPatch, iParam);
        delta = idelta;
    }

    public byte [] generate (int value) {
        //TODO: EEN SEND METHODE BEDENKEN WAARBIJ DIT AUTOMATISCH WORDT GEREGELD, ZONDER DE STORE FUNCTIE TE ONTREGELEN.
        //TODO: STORE ZET PATCH- EN BANKNUMMER EN GEBRUIKT SEND.
        patch.sysex[7]=(byte)0x00;
        patch.sysex[8]=(byte)0x00;

        value = value + delta;

        TCElectronicGMajorUtil.setValue(patch.sysex, value, offs);
        patch.sysex[TCElectronicGMajorConst.CHECKSUMOFFSET] = TCElectronicGMajorUtil.calcChecksum(patch.sysex);
        return patch.sysex;
    }
}
