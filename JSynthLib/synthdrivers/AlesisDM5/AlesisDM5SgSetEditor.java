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
import javax.sound.midi.ShortMessage;


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
    private NRPNSender selectedNoteSender;
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

        JPanel triggerPanel = new JPanel();        
        triggerPanel.setLayout(new BoxLayout(triggerPanel, BoxLayout.Y_AXIS));
//        triggerPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"triggerPanel",TitledBorder.CENTER,TitledBorder.CENTER));
        triggerPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Trigger Note Assignments",TitledBorder.CENTER,TitledBorder.CENTER));
        panel.add(triggerPanel,gbc);        
        addTriggerWidgets(patch, triggerPanel);

        addSelectedNoteWidget(patch, noteSelectPanel);

        JPanel parmsPanel = new JPanel();        
        parmsPanel.setLayout(new BoxLayout(parmsPanel, BoxLayout.Y_AXIS));
//        parmsPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"parmsPanel",TitledBorder.CENTER,TitledBorder.CENTER));
        parmsPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Selected Note Parameters",TitledBorder.CENTER,TitledBorder.CENTER));
        noteSelectPanel.add(parmsPanel,gbc);        
        addParmsSubPanels(patch, parmsPanel);
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
        selectedNoteSender = new NRPNSender(NRPNSender.PREVIEW_NOTE, 60);
        selectedNoteWidget = new DM5ScrollBarLookupWidget("Selected Note", patch, 0, 60, -1,
                                                          null,
//                                                          selectedNoteSender,
                                                          null,
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
        selectedNoteSender.send(patch.getDriver(), noteValue);
        
        notePacketModel[0].setPacketIndex(noteValue);
        int familyValue = notePacketModel[0].get();

        notePacketModel[1].setPacketIndex(noteValue);
        int drumSoundValue = notePacketModel[1].get();

        notePacketModel[2].setPacketIndex(noteValue);
        int coarseTuneValue = notePacketModel[2].get();

        notePacketModel[3].setPacketIndex(noteValue);
        int fineTuneValue = notePacketModel[3].get();

        notePacketModel[4].setPacketIndex(noteValue);
        int volValue = notePacketModel[4].get();

        notePacketModel[5].setPacketIndex(noteValue);
        int panValue = notePacketModel[5].get();

        notePacketModel[6].setPacketIndex(noteValue);
        int outputValue = notePacketModel[6].get();
        
        notePacketModel[7].setPacketIndex(noteValue);
        int groupValue = notePacketModel[7].get();
        
        ((ComboBoxWidget)sysexWidget[0]).setValue(familyValue);
//        ((ComboBoxWidget)sysexWidget[1]).setValue(drumSoundValue);
        ((ScrollBarLookupWidget)sysexWidget[2]).setValue(coarseTuneValue);
        ((ScrollBarLookupWidget)sysexWidget[3]).setValue(fineTuneValue);
        ((ScrollBarWidget)sysexWidget[4]).setValue(volValue);
        ((ScrollBarLookupWidget)sysexWidget[5]).setValue(panValue);
        ((ComboBoxWidget)sysexWidget[6]).setValue(outputValue);
        ((ComboBoxWidget)sysexWidget[7]).setValue(groupValue);
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
        sysexWidget[2] = new CoarseTuneScrollBarLookupWidget("Coarse Tune", 
                                                             patch, 0, 7, -1,
                                                             notePacketModel[2],
                                                             new NRPNSender(NRPNSender.NOTE_COARSE_TUNE, new int [] {28, 42, 56, 71, 85, 99, 113, 127}));
        addWidget(panel, sysexWidget[2], ctrlBase++, 0, 1, 1, widgetCount++);
        ((ScrollBarLookupWidget)sysexWidget[2]).addEventListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                coarseTuneChanged(e);
            }
	    });
        
        notePacketModel[3] = new PacketModel(patch, 36+3, BitModel.FINE_TUNE_MASK);
        sysexWidget[3] = new FineTuneScrollBarLookupWidget("Fine Tune",
                                                           patch, 0, 99, 0,
                                                           notePacketModel[3],
                                                           new NRPNSender(NRPNSender.NOTE_FINE_TUNE, 99));
        addWidget(panel, sysexWidget[3], ctrlBase++, 0, 1, 1, widgetCount++);
        ((ScrollBarLookupWidget)sysexWidget[3]).addEventListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                fineTuneChanged(e);
            }
	    });
        
        ((CoarseTuneScrollBarLookupWidget)sysexWidget[2]).setOptions(sysexWidget[3].getValue());
        ((FineTuneScrollBarLookupWidget)sysexWidget[3]).setOptions(sysexWidget[2].getValue());
        
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

    public void coarseTuneChanged(ChangeEvent e) {
        JSlider sl = (JSlider)e.getSource();
        int slVal = (int)sl.getValue();
        ((FineTuneScrollBarLookupWidget)sysexWidget[3]).setOptions(slVal);
    }
    
    public void fineTuneChanged(ChangeEvent e) {
        JSlider sl = (JSlider)e.getSource();
        int slVal = (int)sl.getValue();
        ((CoarseTuneScrollBarLookupWidget)sysexWidget[2]).setOptions(slVal);
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
        
        JButton playButton = new JButton("Play Note");
        panel.add(playButton);        
        playButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                playPreviewNote();
            }
	    });
    }
    
    private void playPreviewNote() {
        int selNoteVal = selectedNoteWidget.getValue();
        Driver driver = (Driver)patch.getDriver();
        int noteVal = rootNoteWidget.getValue() + selNoteVal;
        try {
            Thread.sleep(10);
            ShortMessage msg = new ShortMessage();
            msg.setMessage(ShortMessage.NOTE_ON, driver.getChannel() - 1,
                           noteVal,
                           AppConfig.getVelocity());
            driver.send(msg);
            
            Thread.sleep(10);
            
            msg.setMessage(ShortMessage.NOTE_ON, driver.getChannel() - 1,
                           noteVal,
                           0);	// expecting running status
            driver.send(msg);
        } catch (Exception e) {
            ErrorMsg.reportStatus(e);
        }
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

class CoarseTuneScrollBarLookupWidget extends ScrollBarLookupWidget {
    private static final String[] posOptions = new String[] {
        "-4", "-3", "-2", "-1", "+0", "+1", "+2", "+3", "+4"        
    }; 
    
    private static final String[] negOptions = new String[] {
        "-3", "-2", "-1", "-0", "+0", "+1", "+2", "+3", "+4"        
    }; 
    
    CoarseTuneScrollBarLookupWidget(String label,
                                    IPatch patch, 
                                    int min, 
                                    int max,
                                    int labelWidth,
                                    IParamModel pmodel, 
                                    ISender sender) {
        super(label, patch, min, max, labelWidth, pmodel, sender, posOptions);
    }
    
    void setOptions(int fineTuneValue) {
        if (fineTuneValue > 0) {
            options = negOptions;
        } else {
            options = posOptions;
        }
        int v = slider.getValue();
        text.setText(options[v]);
    }
}

class FineTuneScrollBarLookupWidget extends ScrollBarLookupWidget {
    private static final String[] posOptions = new String[] {
        ".00", ".01", ".02", ".03", ".04", ".05", ".06", ".07", ".08", ".09",
        ".10", ".11", ".12", ".13", ".14", ".15", ".16", ".17", ".18", ".19",
        ".20", ".21", ".22", ".23", ".24", ".25", ".26", ".27", ".28", ".29",
        ".30", ".31", ".32", ".33", ".34", ".35", ".36", ".37", ".38", ".39",
        ".40", ".41", ".42", ".43", ".44", ".45", ".46", ".47", ".48", ".49",
        ".50", ".51", ".52", ".53", ".54", ".55", ".56", ".57", ".58", ".59",
        ".60", ".61", ".62", ".63", ".64", ".65", ".66", ".67", ".68", ".69",
        ".70", ".71", ".72", ".73", ".74", ".75", ".76", ".77", ".78", ".79",
        ".80", ".81", ".82", ".83", ".84", ".85", ".86", ".87", ".88", ".89",
        ".90", ".91", ".92", ".93", ".94", ".95", ".96", ".97", ".98", ".99"
    };
    
    private static final String[] negOptions = new String[] {
        ".00", "-.99", "-.98", "-.97", "-.96", "-.95", "-.94", "-.93", "-.92", "-.91", 
        "-.90", "-.89", "-.88", "-.87", "-.86", "-.85", "-.84", "-.83", "-.82", "-.81", 
        "-.80", "-.79", "-.78", "-.77", "-.76", "-.75", "-.74", "-.73", "-.72", "-.71", 
        "-.70", "-.69", "-.68", "-.67", "-.66", "-.65", "-.64", "-.63", "-.62", "-.61", 
        "-.60", "-.59", "-.58", "-.57", "-.56", "-.55", "-.54", "-.53", "-.52", "-.51", 
        "-.50", "-.49", "-.48", "-.47", "-.46", "-.45", "-.44", "-.43", "-.42", "-.41", 
        "-.40", "-.39", "-.38", "-.37", "-.36", "-.35", "-.34", "-.33", "-.32", "-.31", 
        "-.30", "-.29", "-.28", "-.27", "-.26", "-.25", "-.24", "-.23", "-.22", "-.21", 
        "-.20", "-.19", "-.18", "-.17", "-.16", "-.15", "-.14", "-.13", "-.12", "-.11", 
        "-.10", "-.09", "-.08", "-.07", "-.06", "-.05", "-.04", "-.03", "-.02", "-.01"
    };
    
    FineTuneScrollBarLookupWidget(String label,
                                  IPatch patch, 
                                  int min, 
                                  int max,
                                  int labelWidth,
                                  IParamModel pmodel, 
                                  ISender sender) {
        super(label, patch, min, max, labelWidth, pmodel, sender, posOptions);
    }
    
    void setOptions(int coarseTuneValue) {
        if (coarseTuneValue < 4) {
            options = negOptions;
        } else {
            options = posOptions;
            if (coarseTuneValue > 6) {
                slider.setValue(0);
                slider.setEnabled(false);
            } else {
                slider.setEnabled(true);
            }
        }
        int v = slider.getValue();
        text.setText(options[v]);
    }
}
