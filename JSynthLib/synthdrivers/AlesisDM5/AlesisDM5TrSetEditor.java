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

package synthdrivers.AlesisDM5;

import core.*;
import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;

// packages needed for class DM5Sender
import javax.sound.midi.ShortMessage;
import javax.sound.midi.InvalidMidiDataException;

/** Alesis DM5 Trigger Setup Editor
* 
* @author Jeff Weber
*/
public class AlesisDM5TrSetEditor extends PatchEditorFrame {
    /** Size of program patch header--7 bytes.
    */
    static final int headerSize = Constants.HDR_SIZE;
    
    /** A reference to the edit panel object. */
//    private JPanel dm5EditPanel;

    /** Constructs a AlesisDM5TrSetEditor for the selected patch.*/
    AlesisDM5TrSetEditor(Patch patch)
    {
        super ("Alesis DM5 Trigger Setup Editor",patch);   
        scrollPane.setLayout(new GridLayout(0,2));
        for (int i = 0; i < 6; i++) {
            addTrigPane(patch, i);
            addTrigPane(patch, i+6);
        }
        pack();
        show();
    }
    
    private void addTrigPane(Patch patch, int trigNum) {
        JPanel dm5EditPanel = new JPanel();        
        dm5EditPanel.setLayout(new BoxLayout(dm5EditPanel, BoxLayout.X_AXIS));
        dm5EditPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),String.valueOf(trigNum+1),TitledBorder.CENTER,TitledBorder.CENTER));
        addWidgets(dm5EditPanel, patch, trigNum);
        scrollPane.add(dm5EditPanel,gbc);        
    }
    
    private void addWidgets(JPanel panel, Patch patch, int trigNum) {
        int fdrNbrBase = trigNum * 5;
        addWidget(panel,
                  new KnobWidget("Gain", patch, 0, 99, 0,
                                 new ParamModel(patch, headerSize + trigNum),
                                 new DM5Sender(trigNum, DM5Sender.TR_GAIN, 99)),
                  0, 0,
                  1, 1,
                  fdrNbrBase+1);
        
        addWidget(panel,
                  new KnobWidget("Velocy",
                                 patch, 0, 7, 0,
                                 new ParamModel(patch, 19 + (trigNum * 4)),
                                 new DM5Sender(trigNum, DM5Sender.TR_V_CURVE, 7)),
                  1, 0,
                  1, 3,
                  fdrNbrBase+2);
        
        addWidget(panel,
                  new KnobWidget("X-Talk",
                                 patch, 0, 99, 0,
                                 new ParamModel(patch, 20 + (trigNum * 4)),
                                 new DM5Sender(trigNum, DM5Sender.TR_X_TALK, 99)),
                  2, 0,
                  1, 3,
                  fdrNbrBase+3);
        
        addWidget(panel,
                  new KnobWidget("Noise Flr",
                                 patch, 0, 99, 0,
                                 new ParamModel(patch, 21 + (trigNum * 4)),
                                 new DM5Sender(trigNum, DM5Sender.TR_NOISE_FLR, 99)),
                  3, 0,
                  1, 3,
                  fdrNbrBase+4);
        
        addWidget(panel,
                  new KnobWidget("Decay",
                                 patch, 0, 99, 0,
                                 new ParamModel(patch, 22 + (trigNum * 4)),
                                 new DM5Sender(trigNum, DM5Sender.TR_DECAY, 99)),
                  4, 0,
                  1, 3,
                  fdrNbrBase+5);
    }
}

class DM5Sender implements SysexWidget.ISender {
    final static int TR_V_CURVE        = 1;
    final static int TR_NOTE_NBR       = 2;
    final static int TR_GAIN           = 3;
    final static int TR_X_TALK         = 4;
    final static int TR_DECAY          = 5;
    final static int TR_NOISE_FLR      = 6;
    
    private final static int NRPN_MSB          = 99;
    private final static int NRPN_LSB          = 98;
    private final static int DATA_ENTRY_MSB    = 6;
    private final static int SELECT_ACTIVE_TRG = 0;
    
    private final static int MAX_TRIG_NUM = 11;
    private static int LAST_TRIG_NUM = 99;
    private int param;
    private int trigNum;
    private int max;
    
    public DM5Sender(int trigNum, int param, int max) {
        this.trigNum = trigNum;
        this.param = param;
        this.max = max;
    }
    
    public void send(IPatchDriver driver, int value) {
        if (trigNum != LAST_TRIG_NUM) {
            try {
                driver.send(newControlChange(driver, NRPN_MSB, 0));  // Send NRPN MSB
                driver.send(newControlChange(driver, NRPN_LSB, SELECT_ACTIVE_TRG));  // Command to select active trigger number (NRPN LSB)
                driver.send(newControlChange(driver, 6, trigNum * 127 / MAX_TRIG_NUM));  // Set the Trigger Number
            } catch  (InvalidMidiDataException e) {
                ErrorMsg.reportStatus(e);
            }
            LAST_TRIG_NUM = trigNum;            

            try {
                Thread.sleep (50);
            } catch (Exception e) {}
        }

        try {
            driver.send(newControlChange(driver, NRPN_LSB, param));  // Command to select the parameter
            driver.send(newControlChange(driver, DATA_ENTRY_MSB, value * 127 / max));  // Set the value of the parameter
        } catch (InvalidMidiDataException e) {
            ErrorMsg.reportStatus(e);
        }
    }
    
    private ShortMessage newControlChange(IPatchDriver driver, int controlNumber, int value) throws InvalidMidiDataException {
        ShortMessage ccMessage = new ShortMessage();
        ccMessage.setMessage(ShortMessage.CONTROL_CHANGE, driver.getDevice().getChannel() - 1, controlNumber, value);
        return ccMessage;
    }
}
