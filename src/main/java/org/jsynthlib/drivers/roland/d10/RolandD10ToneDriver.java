/*
 * Copyright 2002 Roger Westerlund
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
package org.jsynthlib.drivers.roland.d10;

import static org.jsynthlib.drivers.roland.d10.D10Constants.BASE_TONE_MEMORY;
import static org.jsynthlib.drivers.roland.d10.D10Constants.BASE_TONE_TEMP_AREA;
import static org.jsynthlib.drivers.roland.d10.D10Constants.BASE_WRITE_REQUEST;
import static org.jsynthlib.drivers.roland.d10.D10Constants.OFS_ADDRESS;
import static org.jsynthlib.drivers.roland.d10.D10Constants.OFS_DEVICE_ID;
import static org.jsynthlib.drivers.roland.d10.D10Constants.SIZE_HEADER_DT1;
import static org.jsynthlib.drivers.roland.d10.D10Constants.SIZE_TRAILER;
import static org.jsynthlib.drivers.roland.d10.D10Constants.TONE_NAME_SIZE;
import static org.jsynthlib.drivers.roland.d10.D10Constants.TONE_NAME_START;
import static org.jsynthlib.drivers.roland.d10.D10Constants.TONE_RECORD_SIZE;
import static org.jsynthlib.drivers.roland.d10.D10Constants.TONE_SIZE;
import static org.jsynthlib.drivers.roland.d10.D10Constants.TONE_WRITE_REQUEST;

import org.jsynthlib.core.Driver;
import org.jsynthlib.core.JSLFrame;
import org.jsynthlib.core.Patch;
import org.jsynthlib.drivers.roland.d10.message.D10DataSetMessage;
import org.jsynthlib.drivers.roland.d10.message.D10RequestMessage;
import org.jsynthlib.drivers.roland.d10.message.D10TransferMessage;


public class RolandD10ToneDriver extends Driver {

    public RolandD10ToneDriver() {
        super("Tone", "Roger Westerlund");
        sysexID = "F041**1612";

        patchSize = SIZE_HEADER_DT1 + TONE_RECORD_SIZE.getIntValue() + SIZE_TRAILER;
        patchNameStart = SIZE_HEADER_DT1 + TONE_NAME_START.getIntValue();
        patchNameSize = TONE_NAME_SIZE.getIntValue();
        deviceIDoffset = OFS_DEVICE_ID;
        checksumOffset = patchSize - SIZE_TRAILER;
        checksumStart = OFS_ADDRESS;
        checksumEnd = checksumOffset - 1;
        bankNumbers = new String[] { "0-Internal" };
        patchNumbers = RolandD10Support.createToneNumbers();
    }

    public void requestPatchDump(int bankNumber, int patchNumber) {
        D10RequestMessage message = new D10RequestMessage(Entity.createFromIntValue(patchNumber)
                .multiply(TONE_RECORD_SIZE).add(BASE_TONE_MEMORY), TONE_RECORD_SIZE);
        send(message.getBytes());
    }

    public void storePatch(Patch patch, int bankNumber, int patchNumber) {

        // First we send the patch, then we press the write switch.
        sendPatch(patch);

        D10DataSetMessage message = new D10DataSetMessage(2, BASE_WRITE_REQUEST.add(TONE_WRITE_REQUEST).getDataValue());
        message.setData(0, (byte) patchNumber);
        message.setData(1, (byte) 0);
        send(message.getBytes());
    }

    public void sendPatch(Patch patch) {
        // The tone temp area has a record size that is the same as the data
        // size so we can not send the whole patch.
        D10DataSetMessage message = new D10DataSetMessage(TONE_SIZE, BASE_TONE_TEMP_AREA);
        System.arraycopy(patch.sysex, SIZE_HEADER_DT1, message.getBytes(), SIZE_HEADER_DT1, TONE_SIZE.getIntValue());
        send(message.getBytes());
    }

    public Patch createNewPatch() {
        D10TransferMessage message = new D10DataSetMessage(TONE_RECORD_SIZE, BASE_TONE_MEMORY);
        Patch patch = new Patch(message.getBytes(), this);
        setPatchName(patch, "New Tone");
        calculateChecksum(patch);
        return patch;
    }

    public JSLFrame editPatch(Patch patch) {
        return new RolandD10ToneEditor(patch);
    }

    protected String getPatchName(Patch patch) {
        return RolandD10Support.trimName(super.getPatchName(patch));
    }

//    int endIndex;
//
//    public IPatch[] createPatches(byte[] sysex) {
//        int startIndex = 128;
//        endIndex = 0x27c80;
//
//        List<Patch> patchList = new ArrayList<Patch>();
//        for (int index = startIndex; index < endIndex; index += 256) {
//            Patch patch = createNewPatch();
//            System.arraycopy(sysex, index, patch.sysex, SIZE_HEADER_DT1, TONE_SIZE.getIntValue());
//            patchList.add(patch);
//        }
//        IPatch[] patchArray = new IPatch[patchList.size()];
//        patchList.toArray(patchArray);
//        return patchArray;
//    }
//
//    public boolean supportsPatch(String patchString, byte[] sysex) {
//        if (sysex.length > endIndex) {
//            return true;
//        }
//        return super.supportsPatch(patchString, sysex);
//    }
//
//    public boolean isConverter() {
//        return true;
//    }
}
