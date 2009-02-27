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
package synthdrivers.RolandD10;

import static synthdrivers.RolandD10.D10Constants.BASE_TONE_MEMORY;
import static synthdrivers.RolandD10.D10Constants.SIZE_HEADER_DT1;
import static synthdrivers.RolandD10.D10Constants.SIZE_TRAILER;
import static synthdrivers.RolandD10.D10Constants.TONE_COUNT;
import static synthdrivers.RolandD10.D10Constants.TONE_RECORD_SIZE;

import org.jsynthlib.core.BankDriver;
import org.jsynthlib.core.Patch;

import synthdrivers.RolandD10.message.D10DataSetMessage;
import synthdrivers.RolandD10.message.D10RequestMessage;
import synthdrivers.RolandD10.message.D10TransferMessage;

/**
 * The bank will contain individual sysex dumps for each tone since retrieving
 * all tones will result in 64 sysex messages.
 * 
 * 
 * @author Roger Westerlund <roger.westerlund@home.se>
 */
public class RolandD10ToneBankDriver extends BankDriver {

    private static final int EDIT_WINDOW_COLUMNS = 4;

    private static final int TONE_RECORD_SYSEX_SIZE = Entity.createFromIntValue(
            SIZE_HEADER_DT1 + SIZE_TRAILER).add(TONE_RECORD_SIZE).getIntValue();

    private RolandD10ToneDriver toneDriver;

    /**
     * Creates a new instance of RolandD10ToneBankDriver
     * 
     * @param toneDriver
     */
    public RolandD10ToneBankDriver(RolandD10ToneDriver toneDriver) {
        super("Tone Bank", "Roger Westerlund", TONE_COUNT, EDIT_WINDOW_COLUMNS);
        this.toneDriver = toneDriver;

        sysexID = "F041**1612";

        singleSysexID = "F041**1612";
        singleSize = TONE_RECORD_SYSEX_SIZE;
        patchSize = TONE_RECORD_SYSEX_SIZE * TONE_COUNT;
        bankNumbers = new String[] {};
        patchNumbers = RolandD10Support.createToneNumbers();
    }

    public Patch createNewPatch() {
        D10TransferMessage message = new D10DataSetMessage(patchSize - (SIZE_HEADER_DT1 + SIZE_TRAILER), BASE_TONE_MEMORY.getDataValue());
        Patch bank = new Patch(message.getBytes(), this);
        for (int patchNumber = 0; patchNumber < TONE_COUNT; patchNumber++) {
            putPatch(bank, toneDriver.createNewPatch(), patchNumber);
        }
        return bank;
    }

    public void requestPatchDump(int bankNumber, int patchNumber) {
        D10RequestMessage requestMessage = new D10RequestMessage(BASE_TONE_MEMORY, Entity
                .createFromIntValue(TONE_COUNT * TONE_RECORD_SYSEX_SIZE));
        send(requestMessage.getBytes());
    }

    public Patch getPatch(Patch bank, int patchNum) {
        Patch patch = toneDriver.createNewPatch();
        System.arraycopy(bank.sysex, patchNum * TONE_RECORD_SYSEX_SIZE, patch.sysex, 0, TONE_RECORD_SYSEX_SIZE);
        return patch;
    }

    public void putPatch(Patch bank, Patch patch, int patchNum) {
        System.arraycopy(patch.sysex, 0, bank.sysex, patchNum * TONE_RECORD_SYSEX_SIZE,
                TONE_RECORD_SYSEX_SIZE);
    }

    public String getPatchName(Patch bank, int patchNum) {
        Patch patch = getPatch(bank, patchNum);
        return patch.getName();
    }

    public void setPatchName(Patch bank, int patchNum, String name) {
        Patch patch = getPatch(bank, patchNum);
        patch.setName(name);
        putPatch(bank, patch, patchNum);
    }
}
