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



import core.Patch;

public class RolandD10TimbreEditor extends RolandD10EditorFrame {

    protected RolandD10TimbreEditor(Patch patch) {
        super("Roland D-10 Timbre Editor", patch);

        EditSender.setDeviceId(patch.sysex[ 2 ]);

        String name = "Timbre";
        int[] offsets = new int[] {
                TIMBRE_TONE_GROUP.getIntValue(),
                TIMBRE_TONE_NUMBER.getIntValue(),
                TIMBRE_KEY_SHIFT.getIntValue(),
                TIMBRE_FINE_TUNE.getIntValue(),
                TIMBRE_BENDER_RANGE.getIntValue(),
                TIMBRE_ASSIGN_MODE.getIntValue(),
                TIMBRE_REVERB_SWITCH.getIntValue()
        };
        EditSender[] editSenders = new EditSender[] {
                EditSender.getTimbreSender(offsets[0]),
                EditSender.getTimbreSender(offsets[1]),
                EditSender.getTimbreSender(offsets[2]),
                EditSender.getTimbreSender(offsets[3]),
                EditSender.getTimbreSender(offsets[4]),
                EditSender.getTimbreSender(offsets[5]),
                EditSender.getTimbreSender(offsets[6])
        };

        scrollPane.add(createTimbrePanel(patch, name, offsets, editSenders));
    }
}
