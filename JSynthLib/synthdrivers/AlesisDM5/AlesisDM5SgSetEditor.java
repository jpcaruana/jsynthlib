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

// packages needed for class DummySender
import javax.sound.midi.ShortMessage;
import javax.sound.midi.InvalidMidiDataException;

/** Alesis DM5 Single Drumset Editor
* 
* @author Jeff Weber
*/
public class AlesisDM5SgSetEditor extends PatchEditorFrame {
    /** Size of program patch header--7 bytes.
    */
    static final int headerSize = Constants.HDR_SIZE;
    
    static final String[] noteNames = new String[] {
            "C",
            "C#",
            "D",
            "D#",
            "E",
            "F",
            "F#",
            "G",
            "G#",
            "A",
            "A#",
            "B"        
    }; 
 
    private int ctrlBase = 1;
    private int widgetCount = 1;

   /** Constructs a AlesisDM5SgSetEditor for the selected patch.*/
    AlesisDM5SgSetEditor(Patch patch)
    {
        super ("Alesis DM5 Single Drumset Editor",patch);   
        scrollPane.setLayout(new GridLayout(0,1));
        addMainPanel(patch);
        pack();
        show();
    }
    
    private void addMainPanel(Patch patch) {
        JPanel mainPanel = new JPanel();        
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"mainPanel",TitledBorder.CENTER,TitledBorder.CENTER));
        addWidget(mainPanel, new PatchNameWidget("Program Name ", patch),0,0,1,1,widgetCount++);
        addSubPanels(patch, mainPanel);
        addTriggerWidgets(patch, mainPanel);
        scrollPane.add(mainPanel,gbc);        
    }
    
    private void addSubPanels(Patch patch, JPanel panel) {
        JPanel noteSelectPanel = new JPanel();        
        noteSelectPanel.setLayout(new BoxLayout(noteSelectPanel, BoxLayout.Y_AXIS));
        noteSelectPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"noteSelectPanel",TitledBorder.CENTER,TitledBorder.CENTER));
        panel.add(noteSelectPanel,gbc);        

        JPanel notePanel = new JPanel();        
        notePanel.setLayout(new BoxLayout(notePanel, BoxLayout.X_AXIS));
//        notePanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"notePanel",TitledBorder.CENTER,TitledBorder.CENTER));
        noteSelectPanel.add(notePanel,gbc);        
        addNoteWidgets(patch, notePanel);

        JPanel parmsPanel = new JPanel();        
        parmsPanel.setLayout(new BoxLayout(parmsPanel, BoxLayout.Y_AXIS));
//        parmsPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"parmsPanel",TitledBorder.CENTER,TitledBorder.CENTER));
        noteSelectPanel.add(parmsPanel,gbc);        
        addParmsSubPanels(patch, parmsPanel);
    }
    
    private void addParmsSubPanels(Patch patch, JPanel panel) {
        JPanel voicePanel = new JPanel();        
        voicePanel.setLayout(new BoxLayout(voicePanel, BoxLayout.X_AXIS));
//        voicePanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"voicePanel",TitledBorder.CENTER,TitledBorder.CENTER));
        panel.add(voicePanel,gbc);        
        addVoiceWidgets(patch, voicePanel);

        addParmWidgets(patch, panel);

        JPanel outputPanel = new JPanel();        
        outputPanel.setLayout(new BoxLayout(outputPanel, BoxLayout.X_AXIS));
//        outputPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"outputPanel",TitledBorder.CENTER,TitledBorder.CENTER));
        panel.add(outputPanel,gbc);
        addOutputWidgets(patch, outputPanel);
}
    
    private void addNoteWidgets(Patch patch, JPanel panel) {
        String baseNote[] = new String[61];
        for (int i = 0; i < 61; i++) {
                baseNote[i] = noteNames[i % 12] + String.valueOf((i / 12) + 1);
        }
        addWidget(panel,
                  new ScrollBarLookupWidget("Selected Note", patch, 0, 67, -1,
                        new ParamModel(patch, 20 + 0 * 4),
                        new DummySender(0, 3, 99),
                        baseNote),
                  ctrlBase++, 0,
                  1, 1,
                  widgetCount++);
    }
    
    private void addVoiceWidgets(Patch patch, JPanel panel) {
        addWidget(panel,
                  new ComboBoxWidget("Family", patch,
                                     new ParamModel(patch, 20 + 0 * 4),
                                     new DummySender(0, 3, 99),
                                     new String[] {"Kick", "Snare", "Tom", "Hi-Hat", "Cymbal", "Percussion", "Effects", "Random"}),
                  ctrlBase++, 0,
                  1, 1,
                  widgetCount++);

        addWidget(panel,
                  new ComboBoxWidget("Drum Sound", patch,
                        new ParamModel(patch, 20 + 0 * 4),
                        new DummySender(0, 3, 99),
                        new String[] {"Kick", "Snare", "Tom", "Hi-Hat", "Cymbal", "Percussion", "Effects", "Random"}),
                  ctrlBase++, 0,
                  1, 1,
                  widgetCount++);        
    }

    private void addParmWidgets(Patch patch, JPanel panel) {
    	    addWidget(panel,
                    new ScrollBarLookupWidget("Coarse Tune", patch, 0, 7, -1,
                            new ParamModel(patch, 20 + 0 * 4),
                            new DummySender(0, 3, 99),
                            new String[] {"-4", "-3", "-2", "-1", "0", "1", "2", "3"}),
                  ctrlBase++, 0,
                  1, 1,
                  widgetCount++);
        
        addWidget(panel,
                  new ScrollBarWidget("Fine Tune",
                                 patch, 0, 99, 0,
                                 new ParamModel(patch, 21 + (0 * 4)),
                                 new DummySender(0, 6, 99)),
                  ctrlBase++, 0,
                  1, 1,
                  widgetCount++);
        
        addWidget(panel,
                  new ScrollBarWidget("Volume",
                                 patch, 0, 99, 0,
                                 new ParamModel(patch, 22 + (0 * 4)),
                                 new DummySender(0, 5, 99)),
                  ctrlBase++, 0,
                  1, 1,
                  widgetCount++);
        
        addWidget(panel,
                new ScrollBarLookupWidget("Pan", patch, 0, 6, -1,
                        new ParamModel(patch, 20 + 0 * 4),
                        new DummySender(0, 3, 99),
                        new String[] {"-3", "-2", "-1", "0", "1", "2", "3"}),
                  ctrlBase++, 0,
                  1, 1,
                  widgetCount++);
    }

    private void addOutputWidgets(Patch patch, JPanel panel) {
        addWidget(panel,
                new ComboBoxWidget("Output", patch,
                        new ParamModel(patch, 20 + 0 * 4),
                        new DummySender(0, 3, 99),
                        new String[] {"Main", "Aux"}),
                  ctrlBase++, 0,
                  1, 1,
                  widgetCount++);

        addWidget(panel,
                new ComboBoxWidget("Group", patch,
                        new ParamModel(patch, 20 + 0 * 4),
                        new DummySender(0, 3, 99),
                        new String[] {"Multi", "Single", "Group 1", "Group 2"}),
                  ctrlBase++, 0,
                  1, 1,
                  widgetCount++);
    }
    
    private void addTriggerWidgets(Patch patch, JPanel panel) {
        String rootNote[] = new String[68];
        for (int i = 0; i < 68; i++) {
        	    rootNote[i] = noteNames[i % 12] + String.valueOf(i / 12 - 2);
        }
        addWidget(panel,
                  new ScrollBarLookupWidget("Root Note", patch, 0, 67, -1,
                        new ParamModel(patch, 20 + 0 * 4),
                        new DummySender(0, 3, 99),
                        rootNote),
                  ctrlBase++, 0,
                  1, 1,
                  widgetCount++);
    
        String baseNote[] = new String[61];
        for (int i = 0; i < 61; i++) {
                baseNote[i] = noteNames[i % 12] + String.valueOf((i / 12) + 1);
        }

        addWidget(panel,
                new ScrollBarLookupWidget("Closing Note", patch, 0, 60, -1,
                      new ParamModel(patch, 20 + 0 * 4),
                      new DummySender(0, 3, 99),
                      baseNote),
                ctrlBase++, 0,
                1, 1,
                widgetCount++);
  
        addWidget(panel,
                new ScrollBarLookupWidget("Held Note", patch, 0, 60, -1,
                      new ParamModel(patch, 20 + 0 * 4),
                      new DummySender(0, 3, 99),
                      baseNote),
                ctrlBase++, 0,
                1, 1,
                widgetCount++);
  
        for (int i = 1; i <= 12; i++) { 
            addWidget(panel,
                    new ScrollBarLookupWidget("Trigger " + String.valueOf(i),
                          patch, 0, 60, -1,
                          new ParamModel(patch, 20 + 0 * 4),
                          new DummySender(0, 3, 99),
                          baseNote),
                    ctrlBase++, 0,
                    1, 1,
                    widgetCount++);
        }
    }
}

class DummySender implements SysexWidget.ISender {
    private final static int maxTrigNum = 11;
    private static int lastTrigNum = 99;
    private int param;
    private int trigNum;
    private int max;
    
    public DummySender(int trigNum, int param, int max) {
        this.trigNum = trigNum;
        this.param = param;
        this.max = max;
    }
    
    public void send(IPatchDriver driver, int value) {
/*
        if (trigNum != lastTrigNum) {
            ShortMessage msgMSB = new ShortMessage();
            ShortMessage msgTrigNumLSB = new ShortMessage();
            ShortMessage msgTrigNumDataEntry = new ShortMessage();
            try {
                msgMSB.setMessage(ShortMessage.CONTROL_CHANGE,
                                  driver.getDevice().getChannel() - 1, 99, 0);
                
                msgTrigNumLSB.setMessage(ShortMessage.CONTROL_CHANGE,
                                         driver.getDevice().getChannel() - 1, 98, 0);
                
                msgTrigNumDataEntry.setMessage(ShortMessage.CONTROL_CHANGE,
                                               driver.getDevice().getChannel() - 1, 6, trigNum * 127 / maxTrigNum);
                
                driver.send(msgMSB);
                driver.send(msgTrigNumLSB);
                driver.send(msgTrigNumDataEntry);
            } catch  (InvalidMidiDataException e) {
                ErrorMsg.reportStatus(e);
            }
            lastTrigNum = trigNum;            

            try {
                Thread.sleep (50);
            } catch (Exception e) {}
        }

        ShortMessage msgParamLSB = new ShortMessage();
        ShortMessage msgValueDataEntry = new ShortMessage();
        try {
            msgParamLSB.setMessage(ShortMessage.CONTROL_CHANGE,
                                   driver.getDevice().getChannel() - 1, 98, param);
            
            msgValueDataEntry.setMessage(ShortMessage.CONTROL_CHANGE,
                                         driver.getDevice().getChannel() - 1, 6, value * 127 / max);

            driver.send(msgParamLSB);
            driver.send(msgValueDataEntry);
        } catch (InvalidMidiDataException e) {
            ErrorMsg.reportStatus(e);
        }
*/
    }
}
