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
                                 new DM5Sender(trigNum, 3, 99)),
                  0, 0,
                  1, 1,
                  fdrNbrBase+1);
        
        addWidget(panel,
                  new KnobWidget("Velocy",
                                 patch, 0, 7, 0,
                                 new ParamModel(patch, 19 + (trigNum * 4)),
                                 new DM5Sender(trigNum, 1, 7)),
                  1, 0,
                  1, 3,
                  fdrNbrBase+2);
        
        addWidget(panel,
                  new KnobWidget("X-Talk",
                                 patch, 0, 99, 0,
                                 new ParamModel(patch, 20 + (trigNum * 4)),
                                 new DM5Sender(trigNum, 4, 99)),
                  2, 0,
                  1, 3,
                  fdrNbrBase+3);
        
        addWidget(panel,
                  new KnobWidget("Noise Flr",
                                 patch, 0, 99, 0,
                                 new ParamModel(patch, 21 + (trigNum * 4)),
                                 new DM5Sender(trigNum, 6, 99)),
                  3, 0,
                  1, 3,
                  fdrNbrBase+4);
        
        addWidget(panel,
                  new KnobWidget("Decay",
                                 patch, 0, 99, 0,
                                 new ParamModel(patch, 22 + (trigNum * 4)),
                                 new DM5Sender(trigNum, 5, 99)),
                  4, 0,
                  1, 3,
                  fdrNbrBase+5);
    }
    
/*    
    private void addWidgets(JPanel panel, Patch patch) {
        int i = 0;
        EnvelopeWidget.Node[] nodes = new EnvelopeWidget.Node[] {
            new EnvelopeWidget.Node(0, 0, null, 0, 0, null, 0, false, null, null, null, null),
            new EnvelopeWidget.Node(0, 100,
                                    new ParamModel(patch, 30 + i), 0, 0, null, 0, false,
                                    new CCSender(34, i), null, "Dly", null),
            new EnvelopeWidget.Node(0, 100,
                                    new ParamModel(patch, 62 + i), 100, 100, null, 25, false,
                                    new CCSender(45, i), null, "A", null),
            new EnvelopeWidget.Node(0, 100,
                                    new ParamModel(patch, 66 + i), 0, 100,
                                    new ParamModel(patch, 10 + i), 25, false,
                                    new CCSender(46, i),
                                    new CCSender(47, i), "D", "S"),
            new EnvelopeWidget.Node(100, 100, null, 5000, 5000, null, 0, false, null, null, null, null),
            new EnvelopeWidget.Node(0, 100,
                                    new ParamModel(patch, 55 + i), 0, 0, null, 0, false,
                                    new CCSender(48, i), null, "R", null),
        };
        addWidget(panel,
                  new EnvelopeWidget("DCA Envelope", patch, nodes),
                  0, 0, 3, 5, 33);
    }
*/
    private int getLabelWidth(String s) {
        return (int) (new JLabel(s)).getPreferredSize().getWidth();
    }
}

class DM5Sender implements SysexWidget.ISender {
    private int param;
    private int trigNum;
    private int max;
    
    public DM5Sender(int trigNum, int param, int max) {
        this.trigNum = trigNum;
        this.param = param;
        this.max = max;
    }
    
    public void send(IPatchDriver driver, int value) {
        ShortMessage msgMSB = new ShortMessage();
        ShortMessage msgTrigNumLSB = new ShortMessage();
        ShortMessage msgTrigNumDataEntry = new ShortMessage();
        ShortMessage msgParamLSB = new ShortMessage();
        ShortMessage msgValueDataEntry = new ShortMessage();
        try {
            msgMSB.setMessage(ShortMessage.CONTROL_CHANGE,
                           driver.getDevice().getChannel() - 1, 99, 0);
            
            msgTrigNumLSB.setMessage(ShortMessage.CONTROL_CHANGE,
                                     driver.getDevice().getChannel() - 1, 98, 0);
            
            msgTrigNumDataEntry.setMessage(ShortMessage.CONTROL_CHANGE,
                                           driver.getDevice().getChannel() - 1, 6, trigNum);
            
            msgParamLSB.setMessage(ShortMessage.CONTROL_CHANGE,
                           driver.getDevice().getChannel() - 1, 98, param);
            
            msgValueDataEntry.setMessage(ShortMessage.CONTROL_CHANGE,
                           driver.getDevice().getChannel() - 1, 6, value * 127 / max);

            driver.send(msgMSB);
            driver.send(msgTrigNumLSB);
            driver.send(msgTrigNumDataEntry);
            driver.send(msgParamLSB);
            driver.send(msgValueDataEntry);
        } catch (InvalidMidiDataException e) {
            ErrorMsg.reportStatus(e);
        }
    }
}
