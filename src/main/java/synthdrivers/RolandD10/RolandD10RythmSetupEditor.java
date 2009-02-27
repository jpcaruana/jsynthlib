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

import static synthdrivers.RolandD10.D10Constants.RYTHM_SETUP_LEVEL;
import static synthdrivers.RolandD10.D10Constants.RYTHM_SETUP_PANPOT;
import static synthdrivers.RolandD10.D10Constants.RYTHM_SETUP_REVERB_SWITCH;
import static synthdrivers.RolandD10.D10Constants.RYTHM_SETUP_TONE;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;

import org.jsynthlib.core.ComboBoxWidget;
import org.jsynthlib.core.Patch;
import org.jsynthlib.core.ScrollBarWidget;


public class RolandD10RythmSetupEditor extends RolandD10EditorFrame {

    /**
     * @param patch
     */
    public RolandD10RythmSetupEditor(Patch patch) {
        super("Roland D-10 Rythm Setup Editor", patch);

        EditSender.setDeviceId(patch.sysex[ 2 ]);

        JPanel panel = new JPanel(new GridBagLayout());

        setNamedBorder(panel, "Common parameters");

        addWidget(panel,new ScrollBarWidget("Tone",patch,0,127,0,
                new D10ParamModel(patch, RYTHM_SETUP_TONE.getIntValue()),
                EditSender.getRythmSetupSender(RYTHM_SETUP_TONE.getIntValue())),
                0,GridBagConstraints.RELATIVE,GridBagConstraints.REMAINDER,1,0);
        addWidget(panel,new ScrollBarWidget("Level",patch,0,100,0,
                new D10ParamModel(patch, RYTHM_SETUP_LEVEL.getIntValue()),
                EditSender.getRythmSetupSender(RYTHM_SETUP_LEVEL.getIntValue())),
                0,GridBagConstraints.RELATIVE,GridBagConstraints.REMAINDER,1,0);
        addWidget(panel,new ScrollBarWidget("Balance",patch,0,100,-50,
                new D10ParamModel(patch, RYTHM_SETUP_PANPOT.getIntValue()),
                EditSender.getRythmSetupSender(RYTHM_SETUP_PANPOT.getIntValue())),
                0,GridBagConstraints.RELATIVE,GridBagConstraints.REMAINDER,1,0);
        addWidget(panel,new ComboBoxWidget("Reverb switch", patch,
                new D10ParamModel(patch,RYTHM_SETUP_REVERB_SWITCH.getIntValue()),
                EditSender.getRythmSetupSender(RYTHM_SETUP_REVERB_SWITCH.getIntValue()),
                new String[] {"Off","On"}),
                0,GridBagConstraints.RELATIVE,GridBagConstraints.REMAINDER,1,0);

        scrollPane.add(panel);
    }
}
