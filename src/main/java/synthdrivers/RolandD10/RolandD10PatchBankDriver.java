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

import static synthdrivers.RolandD10.D10Constants.BASE_PATCH_MEMORY;
import static synthdrivers.RolandD10.D10Constants.OFS_ADDRESS;
import static synthdrivers.RolandD10.D10Constants.OFS_DEVICE_ID;
import static synthdrivers.RolandD10.D10Constants.PATCH_COUNT;
import static synthdrivers.RolandD10.D10Constants.PATCH_SIZE;
import static synthdrivers.RolandD10.D10Constants.SIZE_HEADER_DT1;
import static synthdrivers.RolandD10.D10Constants.SIZE_TRAILER;

import org.jsynthlib.core.BankDriver;
import org.jsynthlib.core.Patch;

import synthdrivers.RolandD10.message.D10DataSetMessage;
import synthdrivers.RolandD10.message.D10RequestMessage;
import synthdrivers.RolandD10.message.D10TransferMessage;

public class RolandD10PatchBankDriver extends BankDriver {

    private static final int COLUMN_COUNT = 4;

    private RolandD10PatchDriver patchDriver;

    public RolandD10PatchBankDriver(RolandD10PatchDriver patchDriver) {
        super("Patch Bank", "Roger Westerlund", PATCH_COUNT, COLUMN_COUNT);
        this.patchDriver = patchDriver;
        sysexID = "F041**1612";

        singleSysexID = "F041**1612";
        patchSize = 19 * (SIZE_HEADER_DT1 + SIZE_TRAILER) + PATCH_COUNT * PATCH_SIZE.getIntValue();
        deviceIDoffset = OFS_DEVICE_ID;
        checksumOffset = patchSize - SIZE_TRAILER;
        checksumStart = OFS_ADDRESS;
        checksumEnd = checksumOffset - 1;
        bankNumbers = new String[] {};
        patchNumbers = RolandD10Support.createPatchNumbers();
    }

    public Patch createNewPatch() {
        D10TransferMessage message = new D10DataSetMessage(patchSize - (SIZE_HEADER_DT1 + SIZE_TRAILER), BASE_PATCH_MEMORY.getDataValue());
        Patch bank = new Patch(message.getBytes(), this);
        for (int patchNumber = 0; patchNumber < PATCH_COUNT; patchNumber++) {
            putPatch(bank, patchDriver.createNewPatch(), patchNumber);
        }
        return bank;
    }

    public void requestPatchDump(int bankNum, int patchNum) {
        D10RequestMessage requestMessage = new D10RequestMessage(BASE_PATCH_MEMORY, Entity
                .createFromIntValue(PATCH_COUNT).multiply(PATCH_SIZE));
        send(requestMessage.getBytes());
    }

    protected Patch getPatch(Patch bank, int patchNum) {
        Patch patch = patchDriver.createNewPatch();
        RolandD10Support.copyPatchFromBank(patchNum, bank.sysex, patch.sysex);
        return patch;
    }

    protected void putPatch(Patch bank, Patch patch, int patchNum) {
        RolandD10Support.copyPatchToBank(patchNum, bank.sysex, patch.sysex);
    }

    protected String getPatchName(Patch bank, int patchNum) {
        Patch patch = getPatch(bank, patchNum);
        return patch.getName();
    }

    protected void setPatchName(Patch bank, int patchNum, String name) {
        Patch patch = getPatch(bank, patchNum);
        patch.setName(name);
        putPatch(bank, patch, patchNum);
    }

}
