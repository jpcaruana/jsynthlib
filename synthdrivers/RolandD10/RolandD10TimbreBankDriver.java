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
import synthdrivers.RolandD10.message.D10DataSetMessage;
import synthdrivers.RolandD10.message.D10RequestMessage;
import synthdrivers.RolandD10.message.D10TransferMessage;
import core.BankDriver;
import core.Patch;

public class RolandD10TimbreBankDriver extends BankDriver {

    private RolandD10TimbreDriver timbreDriver;

    /**
     * @param patchType
     * @param authors
     * @param numPatches
     * @param numColumns
     */
    public RolandD10TimbreBankDriver(RolandD10TimbreDriver timbreDriver) {
        super("Timbre Bank", "Roger Westerlund", TIMBRE_COUNT, 4);
        this.timbreDriver = timbreDriver;
        sysexID = "F041**1612";

        singleSysexID = "F041**1612";
        patchSize = 4 * (SIZE_HEADER_DT1 + SIZE_TRAILER) + TIMBRE_COUNT
                * TIMBRE_SIZE.getIntValue();
        deviceIDoffset = OFS_DEVICE_ID;
        checksumOffset = patchSize - SIZE_TRAILER;
        checksumStart = OFS_ADDRESS;
        checksumEnd = checksumOffset - 1;
        bankNumbers = new String[] {};
        patchNumbers = RolandD10Support.createPatchNumbers();
    }

    public Patch createNewPatch() {
        D10TransferMessage message = new D10DataSetMessage(patchSize - (SIZE_HEADER_DT1 + SIZE_TRAILER), BASE_TIMBRE_MEMORY.getDataValue());
        Patch bank = new Patch(message.getBytes(), this);
        for (int patchNumber = 0; patchNumber < TIMBRE_COUNT; patchNumber++) {
            putPatch(bank, timbreDriver.createNewPatch(), patchNumber);
        }
        return bank;
    }

    public void requestPatchDump(int bankNumber, int patchNumber) {
        D10RequestMessage requestMessage = new D10RequestMessage(BASE_TIMBRE_MEMORY, Entity
                .createFromIntValue(TIMBRE_COUNT).multiply(TIMBRE_SIZE));
        send(requestMessage.getBytes());
    }

    protected Patch getPatch(Patch bank, int patchNum) {
        Patch patch = timbreDriver.createNewPatch();
        RolandD10Support.copyPatchFromBank(patchNum, bank.sysex, patch.sysex);
        return patch;
    }

    protected String getPatchName(Patch bank, int patchNum) {
        return timbreDriver.getPatchName(getPatch(bank, patchNum));
    }

    protected void setPatchName(Patch bank, int patchNum, String name) {
        // Patch has no name in data.
    }

    protected void putPatch(Patch bank, Patch patch, int patchNum) {
        RolandD10Support.copyPatchFromBank(patchNum, bank.sysex, patch.sysex);
    }

}
