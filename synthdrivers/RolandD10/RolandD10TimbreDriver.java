/*
 * Copyright 2006 Roger Westerlund
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
package synthdrivers.RolandD10;

import static synthdrivers.RolandD10.D10Constants.*;

import org.jsynthlib.core.Driver;
import org.jsynthlib.core.JSLFrame;
import org.jsynthlib.core.Patch;

import synthdrivers.RolandD10.message.D10DataSetMessage;
import synthdrivers.RolandD10.message.D10RequestMessage;
import synthdrivers.RolandD10.message.D10TransferMessage;

public class RolandD10TimbreDriver extends Driver {

    public RolandD10TimbreDriver() {
        super("Timbre", "Roger Westerlund");
        sysexID = "F041**1612";

        patchSize = SIZE_HEADER_DT1 + TIMBRE_SIZE.getIntValue() + SIZE_TRAILER;
        deviceIDoffset = OFS_DEVICE_ID;
        checksumOffset = patchSize - SIZE_TRAILER;
        checksumStart = OFS_ADDRESS;
        checksumEnd = checksumOffset - 1;
        bankNumbers = new String[] {};
        patchNumbers = RolandD10Support.createPatchNumbers();
    }

    protected String getPatchName(Patch patch) {

        // Patch has no name in data so we generate a name.
        D10DataSetMessage message = new D10DataSetMessage(patch.sysex);

        byte toneGroup = message.getData(TIMBRE_TONE_GROUP.getIntValue());
        byte toneNumber = message.getData(TIMBRE_TONE_NUMBER.getIntValue());

        return "Tone " + "abir".substring(toneGroup, toneGroup + 1)
                + Integer.toString(toneNumber / 8 + 1) + Integer.toString(toneNumber % 8 + 1);
    }

    public void requestPatchDump(int bankNum, int patchNum) {
        D10RequestMessage request = new D10RequestMessage(Entity.createFromIntValue(patchNum)
                .multiply(TIMBRE_SIZE).add(BASE_TIMBRE_MEMORY), TIMBRE_SIZE);
        send(request.getBytes());
    }

    public Patch createNewPatch() {
        D10TransferMessage message = new D10DataSetMessage(TIMBRE_SIZE, Entity.ZERO);
        Patch patch = new Patch(message.getBytes(), this);
        return patch;
    }

    protected void sendPatch(Patch patch) {
        D10DataSetMessage message = new D10DataSetMessage(patch.sysex);
        message.setAddress(BASE_TIMBRE_TEMP_AREA);
        send(message.getBytes());
    }

    protected void storePatch(Patch patch, int bankNum, int patchNum) {
        sendPatch(patch);

        D10DataSetMessage message = new D10DataSetMessage(2, BASE_WRITE_REQUEST.add(TIMBRE_WRITE_REQUEST).getDataValue());
        message.setData(0, (byte) patchNum);
        message.setData(1, (byte) 0);
        send(message.getBytes());
    }

    public JSLFrame editPatch(Patch patch) {
        return new RolandD10TimbreEditor(patch);
    }
}
