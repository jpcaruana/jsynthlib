/*
 * Copyright 2004 Jeff Weber
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

package org.jsynthlib.drivers.alesis.dm5;

import java.awt.GridLayout;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.jsynthlib.core.KnobWidget;
import org.jsynthlib.core.ParamModel;
import org.jsynthlib.core.Patch;
import org.jsynthlib.core.PatchEditorFrame;

/** Alesis DM5 Trigger Setup Editor. Edits Gain, Velocity, Cross-Talk, Noise
* Floor, and Decay parameters for each of the twelve external triggers on the
* DM5.
* 
* @author Jeff Weber
*/
public class AlesisDM5TrSetEditor extends PatchEditorFrame {
    /** Size of program patch header--7 bytes.
    */
    static final int headerSize = Constants.HDR_SIZE;
    
    /** Constructs an AlesisDM5TrSetEditor given the patch. The editor contains
    * twelve panels containing the trigger parameters for each of the twelve
    * external triggers.
    */
    AlesisDM5TrSetEditor(Patch patch)
    {
        super ("Alesis DM5 Trigger Setup Editor",patch);   
        scrollPane.setLayout(new GridLayout(0,2));
        for (int i = 0; i < 6; i++) {
            addTrigPane(patch, i);
            addTrigPane(patch, i+6);
        }
        pack();
    }
    
    /** Creates a panel in the editor window for a single external trigger.
        */
    private void addTrigPane(Patch patch, int trigNum) {
        JPanel dm5EditPanel = new JPanel();        
        dm5EditPanel.setLayout(new BoxLayout(dm5EditPanel, BoxLayout.X_AXIS));
        dm5EditPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),String.valueOf(trigNum+1),TitledBorder.CENTER,TitledBorder.CENTER));
        addWidgets(dm5EditPanel, patch, trigNum);
        scrollPane.add(dm5EditPanel,gbc);        
    }
    
    /** Creates the KnobWidgets within a single panel of the editor for a single
        external trigger.
        */
    private void addWidgets(JPanel panel, Patch patch, int trigNum) {
        int fdrNbrBase = trigNum * 5;
        addWidget(panel,
                  new KnobWidget("Gain", patch, 0, 99, 0,
                                 new ParamModel(patch, headerSize + trigNum),
                                 new TrigSender(trigNum, TrigSender.TR_GAIN, 99)),
                  0, 0,
                  1, 1,
                  fdrNbrBase+1);
        
        addWidget(panel,
                  new KnobWidget("Velocy",
                                 patch, 0, 7, 0,
                                 new ParamModel(patch, 19 + (trigNum * 4)),
                                 new TrigSender(trigNum, TrigSender.TR_V_CURVE, 7)),
                  1, 0,
                  1, 3,
                  fdrNbrBase+2);
        
        addWidget(panel,
                  new KnobWidget("X-Talk",
                                 patch, 0, 99, 0,
                                 new ParamModel(patch, 20 + (trigNum * 4)),
                                 new TrigSender(trigNum, TrigSender.TR_X_TALK, 99)),
                  2, 0,
                  1, 3,
                  fdrNbrBase+3);
        
        addWidget(panel,
                  new KnobWidget("Noise Flr",
                                 patch, 0, 99, 0,
                                 new ParamModel(patch, 21 + (trigNum * 4)),
                                 new TrigSender(trigNum, TrigSender.TR_NOISE_FLR, 99)),
                  3, 0,
                  1, 3,
                  fdrNbrBase+4);
        
        addWidget(panel,
                  new KnobWidget("Decay",
                                 patch, 0, 99, 0,
                                 new ParamModel(patch, 22 + (trigNum * 4)),
                                 new TrigSender(trigNum, TrigSender.TR_DECAY, 99)),
                  4, 0,
                  1, 3,
                  fdrNbrBase+5);
    }
}