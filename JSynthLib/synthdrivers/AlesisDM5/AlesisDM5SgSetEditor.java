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
import javax.swing.event.*;
import java.awt.event.*;

/** Alesis DM5 Single Drumset Editor
* 
* @author Jeff Weber
*/
public class AlesisDM5SgSetEditor extends PatchEditorFrame {
    /** Size of program patch header--7 bytes.
    */
    static final int headerSize = Constants.HDR_SIZE;
    
    private int ctrlBase = 1;
    private int widgetCount = 1;
    
    private Patch patch;
    
    private JTextField selectedNoteField;
    
    private PacketModel[] notePacketModel= new PacketModel[8];
    private SysexWidget[] sysexWidget = new SysexWidget[8];
    
    private DM5ScrollBarLookupWidget selectedNoteWidget;
    private DM5ScrollBarLookupWidget rootNoteWidget;
    private DM5ScrollBarLookupWidget closeNoteWidget;
    private DM5ScrollBarLookupWidget heldNoteWidget;
    private DM5ScrollBarLookupWidget[] triggerWidget = new DM5ScrollBarLookupWidget[12];
    
   /** Constructs a AlesisDM5SgSetEditor for the selected patch.*/
    AlesisDM5SgSetEditor(Patch patch)
    {
        super ("Alesis DM5 Single Drumset Editor",patch);   
        scrollPane.setLayout(new GridLayout(0,1));
        addMainPanel(patch);
        this.patch = patch;
        pack();
        show();
    }
    
    private void addMainPanel(Patch patch) {
        JPanel mainPanel = new JPanel();        
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
//        mainPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"mainPanel",TitledBorder.CENTER,TitledBorder.CENTER));
//        mainPanel.setBorder(new EtchedBorder(EtchedBorder.RAISED));
        mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        JPanel progNamePanel = new JPanel();
        progNamePanel.setLayout(new FlowLayout());
        progNamePanel.add(new JLabel(" "));
        addWidget(progNamePanel, new PatchNameWidget("Program Name ", patch),0,0,1,1,widgetCount++);
        progNamePanel.add(new JLabel(" "));
        mainPanel.add(progNamePanel,gbc);
        addSubPanels(patch, mainPanel);
        scrollPane.add(mainPanel,gbc);        
    }
    
    private void addSubPanels(Patch patch, JPanel panel) {
        JPanel noteSelectPanel = new JPanel();        
        noteSelectPanel.setLayout(new BoxLayout(noteSelectPanel, BoxLayout.Y_AXIS));
//        noteSelectPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"noteSelectPanel",TitledBorder.CENTER,TitledBorder.CENTER));
        panel.add(noteSelectPanel,gbc);        

        JPanel selectPanel = new JPanel();        
        selectPanel.setLayout(new BoxLayout(selectPanel, BoxLayout.Y_AXIS));
        //        noteSelectPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"selectPanel",TitledBorder.CENTER,TitledBorder.CENTER));
        noteSelectPanel.add(selectPanel,gbc);        
        
        JPanel parmsPanel = new JPanel();        
        parmsPanel.setLayout(new BoxLayout(parmsPanel, BoxLayout.Y_AXIS));
//        parmsPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"parmsPanel",TitledBorder.CENTER,TitledBorder.CENTER));
        parmsPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Selected Note Parameters",TitledBorder.CENTER,TitledBorder.CENTER));
        noteSelectPanel.add(parmsPanel,gbc);        
        addParmsSubPanels(patch, parmsPanel);
        
        JPanel triggerPanel = new JPanel();        
        triggerPanel.setLayout(new BoxLayout(triggerPanel, BoxLayout.Y_AXIS));
        //        parmsPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"triggerPanel",TitledBorder.CENTER,TitledBorder.CENTER));
        triggerPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Trigger Parameters",TitledBorder.CENTER,TitledBorder.CENTER));
        panel.add(triggerPanel,gbc);        
        addTriggerWidgets(patch, triggerPanel);

        addSelectedNoteWidget(patch, selectPanel);
    }
    
    private void addParmsSubPanels(Patch patch, JPanel panel) {
        JPanel voicePanel = new JPanel();        
//        voicePanel.setLayout(new BoxLayout(voicePanel, BoxLayout.X_AXIS));
        voicePanel.setLayout(new GridLayout(0, 2));
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
    
    private void addSelectedNoteWidget(Patch patch, JPanel panel) {
        JPanel selectedNoteWidgetPanel = new JPanel();        
        selectedNoteWidgetPanel.setLayout(new BorderLayout());
        panel.add(selectedNoteWidgetPanel,gbc);
        
        NRPNSender selectedNoteSender = new NRPNSender(NRPNSender.PREVIEW_NOTE, 60);
        selectedNoteWidget = new DM5ScrollBarLookupWidget("Selected Note", patch, 0, 60, -1,
                                                                        null,
                                                                        selectedNoteSender,
                                                                        rootNoteWidget.getValue(), 61);
        selectedNoteSender.send(patch.getDriver(), 0);
        addWidget(panel, selectedNoteWidget, ctrlBase++, 0, 1, 1, widgetCount++);
        selectedNoteWidget.addEventListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                selNoteChanged(e);
            }
	    });
        
    }

    public void selNoteChanged(ChangeEvent e) {
        JSlider sl = (JSlider)e.getSource();
        int noteVal = (int)sl.getValue();
        if (!sl.getValueIsAdjusting()) {
            setPacketModels(noteVal);
        }
    }    

    private void setPacketModels(int noteValue) {
        int newValue;
        notePacketModel[0].setPacketIndex(noteValue);
        int newValue0 = notePacketModel[0].get();

        notePacketModel[1].setPacketIndex(noteValue);
        int newValue1 = notePacketModel[1].get();

        notePacketModel[2].setPacketIndex(noteValue);
        int newValue2 = notePacketModel[2].get();

        notePacketModel[3].setPacketIndex(noteValue);
        int newValue3 = notePacketModel[3].get();

        notePacketModel[4].setPacketIndex(noteValue);
        int newValue4 = notePacketModel[4].get();

        notePacketModel[5].setPacketIndex(noteValue);
        int newValue5 = notePacketModel[5].get();

        notePacketModel[6].setPacketIndex(noteValue);
        int newValue6 = notePacketModel[6].get();
        
        notePacketModel[7].setPacketIndex(noteValue);
        int newValue7 = notePacketModel[7].get();
        
        ((ComboBoxWidget)sysexWidget[0]).setValue(newValue0);
//        ((ComboBoxWidget)sysexWidget[1]).setValue(newValue1);
        ((ScrollBarLookupWidget)sysexWidget[2]).setValue(newValue2);
        ((ScrollBarWidget)sysexWidget[3]).setValue(newValue3);
        ((ScrollBarWidget)sysexWidget[4]).setValue(newValue4);
        ((ScrollBarLookupWidget)sysexWidget[5]).setValue(newValue5);
        ((ComboBoxWidget)sysexWidget[6]).setValue(newValue6);
        ((ComboBoxWidget)sysexWidget[7]).setValue(newValue7);
    }
        
    private void addVoiceWidgets(Patch patch, JPanel panel) {
        notePacketModel[0] = new PacketModel(patch, 36+1, BitModel.BANK_MASK);
        sysexWidget[0] = new ComboBoxWidget("Family", patch,
                                            notePacketModel[0],
                                            new NRPNSender(NRPNSender.NOTE_BANK, 7),
                                            new String[] {
                                                "0--Kick      ", 
                                                "1--Snare     ",
                                                "2--Tom       ",
                                                "3--Hi-Hat    ",
                                                "4--Cymbal    ",
                                                "5--Percussion",
                                                "6--Effects   ",
                                                "7--Random    "
                                            });
        addWidget(panel, sysexWidget[0], ctrlBase++, 0, 1, 1, widgetCount++);
        ((ComboBoxWidget)sysexWidget[0]).addEventListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                familyChanged(e);
            }
	    });

        notePacketModel[1] = new PacketModel(patch, 36+2, BitModel.DRUM_MASK);
        int bankIndex = sysexWidget[0].getValue();
        int listMax = DM5SoundList.DRUM_NAME[bankIndex].length - 1;
        sysexWidget[1] = new DM5ComboBoxWidget("Drum Sound", patch,
                                                      notePacketModel[1],
                                                      new NRPNSender(NRPNSender.NOTE_SOUND, listMax),
                                                      DM5SoundList.DRUM_NAME[bankIndex]);
        addWidget(panel, sysexWidget[1], ctrlBase++, 0, 1, 1, widgetCount++);
    }

    public void familyChanged(ActionEvent e) {
        JComboBox cb = (JComboBox)e.getSource();
        int selectedIndex = cb.getSelectedIndex();
        ((DM5ComboBoxWidget)sysexWidget[1]).updateComboBoxWidgetList(DM5SoundList.DRUM_NAME[selectedIndex]);
    }
    
    private void addParmWidgets(Patch patch, JPanel panel) {
        notePacketModel[2] = new PacketModel(patch, 36+4, BitModel.CRSE_TUNE_MASK);
        sysexWidget[2] = new ScrollBarLookupWidget("Coarse Tune", patch, 0, 7, -1,
                                                   notePacketModel[2],
                                                   new NRPNSender(NRPNSender.NOTE_COARSE_TUNE, new int [] {28, 42, 56, 71, 85, 99, 113, 127}),
                                                   new String[] {"-4", "-3", "-2", "-1", "0", "1", "2", "3"});
        addWidget(panel, sysexWidget[2], ctrlBase++, 0, 1, 1, widgetCount++);
        
        notePacketModel[3] = new PacketModel(patch, 36+3, BitModel.FINE_TUNE_MASK);
        sysexWidget[3] = new ScrollBarWidget("Fine Tune",
                                             patch, 0, 99, 0,
                                             notePacketModel[3],
                                             new NRPNSender(NRPNSender.NOTE_FINE_TUNE, 99));
        addWidget(panel, sysexWidget[3], ctrlBase++, 0, 1, 1, widgetCount++);
        
        notePacketModel[4] = new PacketModel(patch, 36+0, BitModel.VOL_MASK);
        sysexWidget[4] = new ScrollBarWidget("Volume",
                                             patch, 0, 99, 0,
                                             notePacketModel[4],
                                             new NRPNSender(NRPNSender.NOTE_VOLUME, 99));
        addWidget(panel, sysexWidget[4], ctrlBase++, 0, 1, 1, widgetCount++);
        
        notePacketModel[5] = new PacketModel(patch, 36+1, BitModel.PAN_MASK);
        sysexWidget[5] = new ScrollBarLookupWidget("Pan", patch, 0, 6, -1,
                                                   notePacketModel[5],
                                                   new NRPNSender(NRPNSender.NOTE_PAN, 6),
                                                   new String[] {"-3", "-2", "-1", "0", "1", "2", "3"});
        addWidget(panel,sysexWidget[5], ctrlBase++, 0, 1, 1, widgetCount++);
    }

    private void addOutputWidgets(Patch patch, JPanel panel) {
        notePacketModel[6] = new PacketModel(patch, 36+1, BitModel.OUTP_MASK);
        sysexWidget[6] = new ComboBoxWidget("Output", patch,
                                            notePacketModel[6],
                                            new NRPNSender(NRPNSender.NOTE_OUTPUT, 1),
                                            new String[] {"Main", "Aux"});
        addWidget(panel, sysexWidget[6], ctrlBase++, 0, 1, 1, widgetCount++);

        notePacketModel[7] = new PacketModel(patch, 36+4, BitModel.GROUP_MASK);
        sysexWidget[7] = new ComboBoxWidget("Group", patch,
                                            notePacketModel[7],
                                            new NRPNSender(NRPNSender.NOTE_GROUP, 3),
                                            new String[] {"Multi", "Single", "Group 1", "Group 2"});
        addWidget(panel, sysexWidget[7], ctrlBase++, 0, 1, 1, widgetCount++);
    }
    
    private void addTriggerWidgets(Patch patch, JPanel panel) {
        rootNoteWidget = new DM5ScrollBarLookupWidget("Root Note", patch, 0, 67, -1,
                                                      new BitModel(patch, 21, BitModel.ROOT_NOTE_MASK),
                                                      new NRPNSender(NRPNSender.SET_ROOT_NOTE, 67),
                                                      0, 68);
        addWidget(panel,
                  rootNoteWidget,
                  ctrlBase++, 0,
                  1, 1,
                  widgetCount++);
        rootNoteWidget.addEventListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                rootNoteChangeListener(e);
            }
	    });
                

        closeNoteWidget =  new DM5ScrollBarLookupWidget("Closing Note", patch, 0, 60, -1,
                                                               new BitModel(patch, 22, BitModel.NOTE_MASK),
                                                               new NRPNSender(NRPNSender.FW_CLOSE_NOTE, 60),
                                                               rootNoteWidget.getValue(), 61);
        addWidget(panel,
                  closeNoteWidget,
                  ctrlBase++, 0,
                  1, 1,
                  widgetCount++);
        

        heldNoteWidget = new DM5ScrollBarLookupWidget("Held Note", patch, 0, 60, -1,
                                                             new BitModel(patch, 23, BitModel.NOTE_MASK),
                                                             new NRPNSender(NRPNSender.FW_HELD_NOTE, 60),
                                                             rootNoteWidget.getValue(), 61);
        addWidget(panel,
                  heldNoteWidget,
                  ctrlBase++, 0,
                  1, 1,
                  widgetCount++);
        
        for (int i = 0; i < 12; i++) { 
            triggerWidget[i] = new DM5ScrollBarLookupWidget("Trigger " + String.valueOf(i + 1),
                                                                   patch, 0, 60, -1,
                                                                   new BitModel(patch, 24 + i, BitModel.NOTE_MASK),
                                                                   new TrigSender(i, TrigSender.TR_NOTE_NBR, 60),
                                                                   rootNoteWidget.getValue(), 61);
            addWidget(panel,
                      triggerWidget[i],
                      ctrlBase++, 0,
                      1, 1,
                      widgetCount++);
        }
    }

    public void rootNoteChangeListener(ChangeEvent e) {
        JSlider sl = (JSlider)e.getSource();
        int noteVal = (int)sl.getValue();
        if (!sl.getValueIsAdjusting()) {   
            selectedNoteWidget.updateRootNote(noteVal);
            closeNoteWidget.updateRootNote(noteVal);
            heldNoteWidget.updateRootNote(noteVal);
            for (int i = 0; i < 12; i++) { 
                triggerWidget[i].updateRootNote(noteVal);
            }
        }
    }
}
