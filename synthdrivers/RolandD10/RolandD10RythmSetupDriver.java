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

public class RolandD10RythmSetupDriver extends Driver {

    private static final String[] rythmSounds = new String[] {
            "r01 Closed High Hat-1", "r02 Closed High Hat-2", "r03 Open High Hat-1",
            "r04 Open High Hat-2", "r05 Crash Cymbal", "r06 Crash Cymbal (short)",
            "r07 Crash Cymbal (mute)", "r08 Ride Cymbal", "r09 Ride Cymbal (short)",
            "r10 Ride Cymbal (mute)", "r11 Cup", "r12 Cup (mute)", "r13 China Cymbal",
            "r14 Splash Cymbal", "r15 Bass Drum- 1", "r16 Bass Drum-2", "r17 Bass Drum-3",
            "r18 Bass Drum-4", "r19 Snare Drum- 1", "r20 Snare Drum-2", "r21 Snare Drum-3",
            "r22 Snare Drum-4", "r23 Snare Drum-5", "r24 Snare Drum-6", "r25 Rim Shot",
            "r26 Brush-1", "r27 Brush-2", "r28 High Tom Tom-1", "r29 Middle Tom Tom-1",
            "r30 Low Tom Tom-1", "r31 High Tom Tom-2", "r32 Middle Tom Tom-2", "r33 Low Tom Tom-2",
            "r34 High Tom Tom-3", "r35 Middle Tom Tom-3", "r36 Low Tom Tom-3",
            "r37 High Pitch Tom Tom-1", "r38 High Pitch Tom Tom-2", "r39 Hand Clap",
            "r40 Tambourine", "r41 Cowbell", "r42 High Bongo", "r43 Low Bongo",
            "r44 High Conga (mute)", "r45 High Conga", "r46 Low Conga", "r47 High Timbale",
            "r48 Low Timbale", "r49 High Agogo", "r50 Low Agogo", "r51 Cabasa", "r52 Maracas",
            "r53 Short Whistle", "r54 Long Whistle", "r55 Ouijada", "r56 Claves", "r57 Castanets",
            "r58 Triangle", "r59 Wood Block", "r60 Bell", "r61 Native Drum-1", "r62 Native Drum-2",
            "r63 Native Drum-3", "Off" };

    public RolandD10RythmSetupDriver() {
        super("Rythm Setup", "Roger Westerlund");
        sysexID = "F041**1612";

        patchSize = SIZE_HEADER_DT1 + D10Constants.RYTHM_SETUP_SIZE.getIntValue() + SIZE_TRAILER;
        deviceIDoffset = OFS_DEVICE_ID;
        checksumOffset = patchSize - SIZE_TRAILER;
        checksumStart = OFS_ADDRESS;
        checksumEnd = checksumOffset - 1;
        bankNumbers = new String[] {};
        patchNumbers = RolandD10Support.createRythmSetupNumbers();
    }

    protected String getPatchName(Patch patch) {

        // Patch has no name in data so we generate a name.
        D10DataSetMessage message = new D10DataSetMessage(patch.sysex);

        byte tone = message.getData(RYTHM_SETUP_TONE.getIntValue());

        int bank = tone / 64;
        if (bank == 0) {
            return "i" + Integer.toString(tone + 1);
        } else {
            return rythmSounds[tone - 64];
        }
    }

    public void requestPatchDump(int bankNum, int patchNum) {
        D10RequestMessage request = new D10RequestMessage(Entity.createFromIntValue(patchNum)
                .multiply(D10Constants.RYTHM_SETUP_SIZE).add(BASE_RYTHM_SETUP), D10Constants.RYTHM_SETUP_SIZE);
        send(request.getBytes());
    }

    public Patch createNewPatch() {
        D10TransferMessage message = new D10DataSetMessage(D10Constants.RYTHM_SETUP_SIZE, Entity.ZERO);
        Patch patch = new Patch(message.getBytes(), this);
        return patch;
    }

    protected void sendPatch(Patch patch) {
        D10DataSetMessage message = new D10DataSetMessage(patch.sysex);
        message.setAddress(Entity.createFromIntValue(1).multiply(RYTHM_SETUP_SIZE).add(BASE_RYTHM_SETUP_TEMP_AREA));
        send(message.getBytes());
    }

    protected void storePatch(Patch patch, int bankNum, int patchNum) {
        D10DataSetMessage message = new D10DataSetMessage(patch.sysex);
        message.setAddress(Entity.createFromIntValue(patchNum).multiply(RYTHM_SETUP_SIZE).add(BASE_RYTHM_SETUP));
        send(message.getBytes());
    }

    protected void playPatch(Patch p) {
        super.playPatch(p);
    }

    protected JSLFrame editPatch(Patch patch) {
        return new RolandD10RythmSetupEditor(patch);
    }
}
