/*
 * Copyright 2005 Roger Westerlund
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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;

import core.ComboBoxWidget;
import core.Patch;
import core.PatchNameWidget;
import core.ScrollBarWidget;

public class RolandD10PatchEditor extends RolandD10EditorFrame {

    /**
     * @param patch
     */
    public RolandD10PatchEditor(Patch patch) {
        super("Roland D-10 Patch Editor", patch);

        EditSender.setDeviceId(patch.sysex[ 2 ]);

        JPanel panel;
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = GridBagConstraints.RELATIVE;

        // Common
 
        panel = createCommonPanel("Common parameters", patch);
        scrollPane.add(panel, constraints);

        // Upper

        panel = createTimbrePanel("Upper Timbre", patch, new int[] {
                PATCH_UPPER_TONE_GROUP.getIntValue(),
                PATCH_UPPER_TONE_NUMBER.getIntValue(),
                PATCH_UPPER_KEY_SHIFT.getIntValue(),
                PATCH_UPPER_FINE_TUNE.getIntValue(),
                PATCH_UPPER_BENDER_RANGE.getIntValue(),
                PATCH_UPPER_ASSIGN_MODE.getIntValue(),
                PATCH_UPPER_REVERB_SWITCH.getIntValue()
        });
        scrollPane.add(panel, constraints);

        // Lower

        panel = createTimbrePanel("Lower Timbre", patch, new int[] {
                PATCH_LOWER_TONE_GROUP.getIntValue(),
                PATCH_LOWER_TONE_NUMBER.getIntValue(),
                PATCH_LOWER_KEY_SHIFT.getIntValue(),
                PATCH_LOWER_FINE_TUNE.getIntValue(),
                PATCH_LOWER_BENDER_RANGE.getIntValue(),
                PATCH_LOWER_ASSIGN_MODE.getIntValue(),
                PATCH_LOWER_REVERB_SWITCH.getIntValue()
        });
        scrollPane.add(panel, constraints);

    }

    private JPanel createCommonPanel(String name, Patch patch) {
        JPanel panel = new JPanel(new GridBagLayout());

        setNamedBorder(panel, name);

        addWidget(panel,new PatchNameWidget("Name", patch),
                0,GridBagConstraints.RELATIVE,GridBagConstraints.REMAINDER,1,0);
        addWidget(panel,new ScrollBarWidget("Level",patch,0,100,0,
                new D10ParamModel(patch, PATCH_LEVEL.getIntValue()),
                EditSender.getPatchSender(PATCH_LEVEL.getIntValue())),
                0,GridBagConstraints.RELATIVE,GridBagConstraints.REMAINDER,1,0);
        addWidget(panel,new ScrollBarWidget("U/L balance",patch,0,100,-50,
                new D10ParamModel(patch, PATCH_U_L_BALANCE.getIntValue()),
                EditSender.getPatchSender(PATCH_U_L_BALANCE.getIntValue())),
                0,GridBagConstraints.RELATIVE,GridBagConstraints.REMAINDER,1,0);
        addWidget(panel,new ComboBoxWidget("Key mode", patch,
                new D10ParamModel(patch,PATCH_KEY_MODE.getIntValue()), 
                EditSender.getPatchSender(PATCH_KEY_MODE.getIntValue()),
                new String[] {"Whole","Dual","Split"}),
                0,GridBagConstraints.RELATIVE,1,1,0);
        // TODO Make this a combobox
        addWidget(panel,new ScrollBarWidget("Split point",patch,0,61,0,
                new D10ParamModel(patch, PATCH_SPLIT_POINT.getIntValue()),
                EditSender.getPatchSender(PATCH_SPLIT_POINT.getIntValue())),
                1,GridBagConstraints.RELATIVE,GridBagConstraints.REMAINDER,1,0);
        addWidget(panel,new ComboBoxWidget("Reverb mode", patch,
                new D10ParamModel(patch,PATCH_REVERB_MODE.getIntValue()), 
                EditSender.getPatchSender(PATCH_REVERB_MODE.getIntValue()),
                new String[] {"Room 1","Room 2","Hall 1","Hall 2","Plate","Tap delay 1","Tap delay 2","Tap delay 3","Off"}),
                0,GridBagConstraints.RELATIVE,1,1,0);
        addWidget(panel,new ScrollBarWidget("Reverb time",patch,0,7,1,
                new D10ParamModel(patch, PATCH_REVERB_TIME.getIntValue()),
                EditSender.getPatchSender(PATCH_REVERB_TIME.getIntValue())),
                0,GridBagConstraints.RELATIVE,GridBagConstraints.REMAINDER,1,0);
        addWidget(panel,new ScrollBarWidget("Reverb level",patch,0,7,0,
                new D10ParamModel(patch, PATCH_REVERB_LEVEL.getIntValue()),
                EditSender.getPatchSender(PATCH_REVERB_LEVEL.getIntValue())),
                0,GridBagConstraints.RELATIVE,GridBagConstraints.REMAINDER,1,0);

        return panel;
    }

    private JPanel createTimbrePanel(String name, Patch patch, int[] offsets) {
        EditSender[] editSenders = new EditSender[] {
                EditSender.getPatchSender(offsets[0]),
                EditSender.getPatchSender(offsets[1]),
                EditSender.getPatchSender(offsets[2]),
                EditSender.getPatchSender(offsets[3]),
                EditSender.getPatchSender(offsets[4]),
                EditSender.getPatchSender(offsets[5]),
                EditSender.getPatchSender(offsets[6])
        };
        return super.createTimbrePanel(patch, name, offsets, editSenders);
    }
}
