/*
 * Copyright 2005 Jeff Weber
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

package synthdrivers.BehringerFCB1010;

import core.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;

/**
* Behringer FCB1010 Patch Editor
 * 
 * @author Jeff Weber
 */
public class FCB1010Editor extends PatchEditorFrame implements ActionListener {
    
    /** The list of bank numbers displayed in the Bank comboxBox on the Presets
    * panel of the editor.
    */
    private static final String[] BANK_NUMS = new String[] { 
        "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"
    };
    
    /** The list of preset numbers displayed in the Preset comboBox on the Presets
    * panel of the editor.
    */
    private static final String[] PRESET_NUMS = new String[] { 
        "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"
    };
    
    /** The list of MIDI channel numbers displayed in all the ComboBoxWidgets on
        * the Global Settings panel of the editor.
        */
    private static final String[] MIDI_CHANNELS = new String[] { 
        "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16"
    };
    
    /** The list of program change numbers displayed in all the Program Change ComboBoxWidgets
        * on the Presets panel of the editor.
        */
    private static final String[] PRG_CHG_NUMS = new String[] { 
        "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15",
        "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31",
        "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47",
        "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "60", "61", "62", "63",
        "64", "65", "66", "67", "68", "69", "70", "71", "72", "73", "74", "75", "76", "77", "78", "79",
        "80", "81", "82", "83", "84", "85", "86", "87", "88", "89", "90", "91", "92", "93", "94", "95",
        "96", "97", "98", "99", "100", "101", "102", "103", "104", "105", "106", "107", "108", "109", "110", "111",
        "112", "113", "114", "115", "116", "117", "118", "119", "120", "121", "122", "123", "124", "125", "126", "127"
    };
    
    /** Action Command for Bank and Preset Select Comboboxes
        */
    private static final String SELECT_PRESET_ACTION_CMD = "Select Preset";
    
    /** Action Command for Copy Preset Button
        */
    private static final String COPY_PRESET_ACTION_CMD = "Copy Preset";

    /** Action Command for Copy Bank Button
        */
    private static final String COPY_BANK_ACTION_CMD = "Copy Bank";

    /** Action Command for Paste Button
        */
    private static final String PASTE_ACTION_CMD = "Paste";
    
    
    /** Length of a single preset in bytes */
    private static final int PRESET_LENGTH = 16;
    
    /** Length of a bank in bytes */
    private static final int BANK_LENGTH = 160;

    /** Reference to the Paste Button
        */
    private JButton pasteButton = null;

    /** Variable used to assign fader numbers to all widgets except CheckBoxWidgets.
        */
    private int posFaderNum = 1;

    /** Variable used to assign fader numbers to CheckBoxWidgets only.
        */
    private int negFaderNum = -1;
    
    /** Array used to hold all ControlGroupModels for Program Changes
        */
    private ControlGroupModel[] pcGroupModel = new ControlGroupModel[5];
    
    /** Array used to hold all ControlGroupModels for Control Changes
        */
    private ControlGroupModel[] ccGroupModel = new ControlGroupModel[2];
    
    /** Array used to hold all ControlGroupModels for Expression Pedals
        */
    private ControlGroupModel[] expGroupModel = new ControlGroupModel[2];
    
    /** Variable used to hold the ControlGroupModels for Note Control
        */
    private ControlGroupModel noteGroupModel = null;
    
    /** A reference to the JComboBox for bank select.
        */
    private JComboBox bankSelect;

    /** A reference to the JComboBox for preset select.
        */
    private JComboBox presetSelect;
    
    /** Local scrap used to store data for Copy Preset or Copy Patch
        */
    private byte[] localScrap = null;
    
    /** Constructs a FCB1010Editor for the selected patch.
    */
    FCB1010Editor(Patch patch) {
        super("FCB1010 Patch Editor", patch);
        
        SelectedPresetModel.init();
        
        JTabbedPane tabbedPane = new JTabbedPane();
        scrollPane.add(tabbedPane);
        
        JPanel mainPanel = new JPanel();        
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
//        mainPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Preset Settings",TitledBorder.CENTER,TitledBorder.CENTER));

        addSelectPanel(patch, mainPanel);
        addParmsPanel(patch, mainPanel);
        addCopyPastePanel(patch, mainPanel);
        
        tabbedPane.addTab("Presets", mainPanel);

        JPanel glPanel = addGlobalPanel(patch, scrollPane);
        tabbedPane.addTab("Global Settings", glPanel);
        
        pack();
    }
    
    /** Adds the preset select panel to the given panel for the given patch.
    */
    private void addSelectPanel(Patch patch, JPanel panel) {
        JPanel selectPanel = new JPanel();        
//        selectPanel.setLayout(new GridLayout(1, 7));
//        selectPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Selected Preset",TitledBorder.CENTER,TitledBorder.CENTER));
        panel.add(selectPanel);
        
        selectPanel.add(new JLabel(""));
        JLabel bankLabel = new JLabel("Bank");
        bankLabel.setHorizontalAlignment(bankLabel.RIGHT);
        selectPanel.add(bankLabel);
        
        bankSelect = new JComboBox(BANK_NUMS);
        bankSelect.setActionCommand(SELECT_PRESET_ACTION_CMD);
        selectPanel.add(bankSelect);
        bankSelect.addActionListener(this);
        
        selectPanel.add(new JLabel(""));
        JLabel presetLabel = new JLabel("Preset");
        presetLabel.setHorizontalAlignment(presetLabel.RIGHT);
        selectPanel.add(presetLabel);
        
        presetSelect = new JComboBox(PRESET_NUMS);
        presetSelect.setActionCommand(SELECT_PRESET_ACTION_CMD);
        selectPanel.add(presetSelect);
        presetSelect.addActionListener(this);
        
        selectPanel.add(new JLabel(""));
    }
 
    /** Adds the copy/paste buttons to the given panel for the given patch.
        */
    private void addCopyPastePanel(Patch patch, JPanel panel) {
        JPanel copyPastePanel = new JPanel();        
        //        selectPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Selected Preset",TitledBorder.CENTER,TitledBorder.CENTER));
        panel.add(copyPastePanel);
        
        JButton copyPresetButton = new JButton("Copy Single Preset");
        copyPresetButton.setActionCommand(COPY_PRESET_ACTION_CMD);
        copyPresetButton.addActionListener(this);
        copyPastePanel.add(copyPresetButton);
        
        JButton copyBankButton = new JButton("Copy Entire Bank");
        copyBankButton.setActionCommand(COPY_BANK_ACTION_CMD);
        copyBankButton.addActionListener(this);
        copyPastePanel.add(copyBankButton);
        
        copyPastePanel.add(new JLabel("       "));
        
        pasteButton = new JButton("Paste");
        pasteButton.setActionCommand(PASTE_ACTION_CMD);
        pasteButton.setEnabled(false);
        pasteButton.addActionListener(this);
        copyPastePanel.add(pasteButton);
    }
    
    /** Handles ActionEvents for the preset select combo box and the copy and
        * paste buttons.*/
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals(SELECT_PRESET_ACTION_CMD)) {
            SelectedPresetModel.setPreset(bankSelect.getSelectedIndex(), presetSelect.getSelectedIndex());
        } else if(e.getActionCommand().equals(COPY_PRESET_ACTION_CMD)) {
            doCopyPreset(p);
        } else if(e.getActionCommand().equals(COPY_BANK_ACTION_CMD)) {
            doCopyBank(p);
        } else if(e.getActionCommand().equals(PASTE_ACTION_CMD)) {
            doPaste(p);
        }
    }
    
    /** Copies the currently selected preset to the local scrap.
        */
    private void doCopyPreset(ISinglePatch p) {
        pasteButton.setEnabled(true);
        pasteButton.setText("Paste Preset");
        
        int bank = bankSelect.getSelectedIndex();
        int preset = presetSelect.getSelectedIndex();
        Patch patch = (Patch)p;
        byte[] deNibblizedSysex = FCB1010ParamModel.deNibblize(patch.sysex, Constants.HDR_SIZE);
        int startPos = Constants.HDR_SIZE + (bank * BANK_LENGTH) + (preset * PRESET_LENGTH);
        localScrap = new byte[PRESET_LENGTH];
        System.arraycopy(deNibblizedSysex, startPos, localScrap, 0, PRESET_LENGTH);
//        System.out.println("  " + (Utility.hexDump(localScrap, 0, -1, 16)));
    }
    
    /** Copies the currently selected bank to the local scrap.
        */
    private void doCopyBank(ISinglePatch p) {
        pasteButton.setEnabled(true);
        pasteButton.setText("Paste Bank");

        int bank = bankSelect.getSelectedIndex();
        Patch patch = (Patch)p;
        byte[] deNibblizedSysex = FCB1010ParamModel.deNibblize(patch.sysex, Constants.HDR_SIZE);
        int startPos = Constants.HDR_SIZE + (bank * BANK_LENGTH);
        localScrap = new byte[BANK_LENGTH];
        System.arraycopy(deNibblizedSysex, startPos, localScrap, 0, BANK_LENGTH);
//        System.out.println("  " + (Utility.hexDump(localScrap, 0, -1, 16)));
    }
    
    /** Pastes the local scrap into the currently selected bank or preset. If
        * the local scrap contains a single preset, the preset is pasted. If the
        * local scrap contains an entire bank, the bank is pasted.
        */
    private void doPaste(ISinglePatch p) {
        int startPos = 0;
        int pasteLength = 0;
        
        int bank = bankSelect.getSelectedIndex();
        int preset = presetSelect.getSelectedIndex();
        
        if (localScrap.length == BANK_LENGTH) {
            startPos = Constants.HDR_SIZE + (bank * BANK_LENGTH);            
        } else if (localScrap.length == PRESET_LENGTH) {
            startPos = Constants.HDR_SIZE + (bank * BANK_LENGTH) + (preset * PRESET_LENGTH);
        }
        
        if (startPos != 0) {
            Patch patch = (Patch)p;
            byte[] denibblizedArray = FCB1010ParamModel.deNibblize(patch.sysex, Constants.HDR_SIZE);
            
            System.arraycopy(localScrap, 0, denibblizedArray, startPos, localScrap.length);
            
            byte[] renibblizedArray = FCB1010ParamModel.reNibblize(denibblizedArray, Constants.HDR_SIZE);
            System.arraycopy(renibblizedArray, 0, patch.sysex, 0, patch.sysex.length);

            SelectedPresetModel.setPreset(bankSelect.getSelectedIndex(), presetSelect.getSelectedIndex());
//            FCB1010ParamModel.dump(denibblizedArray); //Test Code
        } 
    }

    /** Adds the parameters panel to the given panel for the given patch.
        */
    private void addParmsPanel(Patch patch, JPanel panel) {
        JPanel parmPanel = new JPanel();        
        parmPanel.setLayout(new BoxLayout(parmPanel, BoxLayout.X_AXIS));
//        parmPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Parameters",TitledBorder.CENTER,TitledBorder.CENTER));
        panel.add(parmPanel);
        
        addProgChgPanel(patch, parmPanel);
        addCntlChgPanel(patch, parmPanel);
        addExpPdlPanel(patch, parmPanel);
    }
    
    /** Adds the program change panel to the given panel for the given patch.
        */
    private void addProgChgPanel(Patch patch, JPanel panel) {
        JPanel progChgPanel = new JPanel();        
        progChgPanel.setLayout(new BoxLayout(progChgPanel, BoxLayout.Y_AXIS));
        progChgPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Program Change",TitledBorder.CENTER,TitledBorder.CENTER));
        panel.add(progChgPanel);
        
        for (int i = 0; i < 5; i++) {
            addProgChgWidgets(patch, progChgPanel, i);
        } 
    }
    
    /** Adds a program change widget to the given panel for the given patch.
        */
    private void addProgChgWidgets(Patch patch, JPanel panel, int pcGroupIx) {
        JPanel progChgPanel = new JPanel();        
        progChgPanel.setLayout(new BoxLayout(progChgPanel, BoxLayout.X_AXIS));
//        progChgPanel.setBorder(new EtchedBorder(EtchedBorder.RAISED));
        panel.add(progChgPanel);
        
        ComboBoxWidget[] cbWidget = new ComboBoxWidget[1];

        cbWidget[0] = new ComboBoxWidget(String.valueOf(pcGroupIx + 1), patch,
                                         new FCB1010ParamModel(patch, pcGroupIx, FCB1010ParamModel.LSB_MASK),
                                         null,
                                         PRG_CHG_NUMS);
        SelectedPresetModel.putWidget(cbWidget[0]);
        
        addWidget(progChgPanel, cbWidget[0],
                  0, 0,
                  1, 1, posFaderNum++);
        
        CheckBoxWidget onOffWidget = new CheckBoxWidget("", patch,
                                                        new FCB1010ParamModel(patch, pcGroupIx, FCB1010ParamModel.MSB_MASK),
                                                        null);
        SelectedPresetModel.putWidget(onOffWidget);
        addWidget(progChgPanel, onOffWidget,
                  0, 0,
                  1, 1,
                  negFaderNum--);
        
        pcGroupModel[pcGroupIx] = new ControlGroupModel(onOffWidget, cbWidget);
    }
    
    /** Adds the control change panel to the given panel for the given patch.
        */
    private void addCntlChgPanel(Patch patch, JPanel panel) {
        JPanel midPanel = new JPanel();        
        midPanel.setLayout(new BoxLayout(midPanel, BoxLayout.Y_AXIS));
//        midPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Mid Panel",TitledBorder.CENTER,TitledBorder.CENTER));
        panel.add(midPanel);
        
        JPanel cntlChgPanel = new JPanel();        
        cntlChgPanel.setLayout(new BoxLayout(cntlChgPanel, BoxLayout.Y_AXIS));
        cntlChgPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Control Change",TitledBorder.CENTER,TitledBorder.CENTER));
        midPanel.add(cntlChgPanel);
        
        addCntlChgLabels(patch, cntlChgPanel);
        
        for (int i = 0; i < 2; i++) {
            addCntlChgWidgets(patch, cntlChgPanel, i);
        } 

        addSwitchWidgets(patch, midPanel);
    }
    
    /** Adds CC and Value labels to the given panel for the given patch.
        */
    private void addCntlChgLabels(Patch patch, JPanel panel) {
        JPanel cntlChgPanel = new JPanel();        
        cntlChgPanel.setLayout(new BoxLayout(cntlChgPanel, BoxLayout.X_AXIS));
//        cntlChgPanel.setBorder(new EtchedBorder(EtchedBorder.RAISED));
        panel.add(cntlChgPanel);
        
        JLabel ccLabel = new JLabel("CC      ");
        cntlChgPanel.add(ccLabel);
        
        JLabel valLabel = new JLabel("      Value");
        cntlChgPanel.add(valLabel);
    }
    
    /** Adds a control change widgets to the given panel for the given patch.
        */
    private void addCntlChgWidgets(Patch patch, JPanel panel, int ccGroupIx) {
        JPanel cntlChgPanel = new JPanel();        
        cntlChgPanel.setLayout(new BoxLayout(cntlChgPanel, BoxLayout.X_AXIS));
//        cntlChgPanel.setBorder(new EtchedBorder(EtchedBorder.RAISED));
        panel.add(cntlChgPanel);
        
        ComboBoxWidget[] cbWidget = new ComboBoxWidget[2];
        
        cbWidget[0] = new ComboBoxWidget(String.valueOf(ccGroupIx + 1), patch,
                                         new FCB1010ParamModel(patch, 5 + (ccGroupIx * 2), FCB1010ParamModel.LSB_MASK),
                                         null,
                                         PRG_CHG_NUMS);
        SelectedPresetModel.putWidget(cbWidget[0]);
        addWidget(cntlChgPanel, cbWidget[0],
                  0, 0,
                  1, 1, posFaderNum++);
        
        cbWidget[1] = new ComboBoxWidget("", patch,
                                         new FCB1010ParamModel(patch, 6 + (ccGroupIx * 2), FCB1010ParamModel.LSB_MASK),
                                         null,
                                         PRG_CHG_NUMS);
        SelectedPresetModel.putWidget(cbWidget[1]);
        addWidget(cntlChgPanel, cbWidget[1],
                  0, 0,
                  1, 1, posFaderNum++);
        
        CheckBoxWidget onOffWidget = new CheckBoxWidget("", patch,
                                                        new FCB1010ParamModel(patch, 5 + (ccGroupIx * 2), FCB1010ParamModel.MSB_MASK),
                                                        null);
        SelectedPresetModel.putWidget(onOffWidget);
        addWidget(cntlChgPanel, onOffWidget,
                  0, 0,
                  1, 1,
                  negFaderNum--);
        
        ccGroupModel[ccGroupIx] = new ControlGroupModel(onOffWidget, cbWidget);
    }
    
    /** Adds a switch widgets to the given panel for the given patch.
        */
    private void addSwitchWidgets(Patch patch, JPanel panel) {
        JPanel switchPanel = new JPanel();        
        switchPanel.setLayout(new BoxLayout(switchPanel, BoxLayout.X_AXIS));
        switchPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Relay Switch",TitledBorder.CENTER,TitledBorder.CENTER));
        panel.add(switchPanel);

        CheckBoxWidget cbWidget = new CheckBoxWidget("Switch 1", patch,
                                                     new FCB1010ParamModel(patch, 6, FCB1010ParamModel.MSB_MASK, false),
                                                     null);
        SelectedPresetModel.putWidget(cbWidget);
        addWidget(switchPanel, cbWidget,
                  0, 0,
                  1, 1,
                  negFaderNum--);
        
        cbWidget = new CheckBoxWidget("Switch 2", patch,
                                      new FCB1010ParamModel(patch, 8, FCB1010ParamModel.MSB_MASK, false),
                                      null);
        SelectedPresetModel.putWidget(cbWidget);
        addWidget(switchPanel, cbWidget,
                  0, 0,
                  1, 1,
                  negFaderNum--);
    }

    /** Adds the expression pedals panel to the given panel for the given patch.
        */
    private void addExpPdlPanel(Patch patch, JPanel panel) {
        JPanel rtPanel = new JPanel();        
        rtPanel.setLayout(new BoxLayout(rtPanel, BoxLayout.Y_AXIS));
        //        rtPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Right Panel",TitledBorder.CENTER,TitledBorder.CENTER));
        panel.add(rtPanel);
        
        JPanel expPdlPanel = new JPanel();        
        expPdlPanel.setLayout(new BoxLayout(expPdlPanel, BoxLayout.Y_AXIS));
        expPdlPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Expression Pedals",TitledBorder.CENTER,TitledBorder.CENTER));
        rtPanel.add(expPdlPanel);
        
        addExpPdlLabels(patch, expPdlPanel);
        
        for (int i = 0; i < 2; i++) {
            addExpPdlWidgets(patch, expPdlPanel, i);
        } 
        
        addNoteCtrlWidgets(patch, rtPanel);
    }
    
    /** Adds Controller, Minimum and Maximum labels to the given panel for the given patch.
        */
    private void addExpPdlLabels(Patch patch, JPanel panel) {
        JPanel expPdlPanel = new JPanel();        
        expPdlPanel.setLayout(new BoxLayout(expPdlPanel, BoxLayout.X_AXIS));
//        expPdlPanel.setBorder(new EtchedBorder(EtchedBorder.RAISED));
        panel.add(expPdlPanel);
        
        JLabel ctlrLabel = new JLabel("Controller");
        expPdlPanel.add(ctlrLabel);
        
        JLabel minLabel = new JLabel("    Minimum");
        expPdlPanel.add(minLabel);
        
        JLabel maxLabel = new JLabel("    Maximum");
        expPdlPanel.add(maxLabel);
    }
    
    /** Adds a control change widgets to the given panel for the given patch.
        */
    private void addExpPdlWidgets(Patch patch, JPanel panel, int expGroupIx) {
        JPanel expPdlPanel = new JPanel();        
//        expPdlPanel.setLayout(new BoxLayout(expPdlPanel, BoxLayout.X_AXIS));
//        expPdlPanel.setBorder(new EtchedBorder(EtchedBorder.RAISED));
        panel.add(expPdlPanel);
        
        ComboBoxWidget[] cbWidget = new ComboBoxWidget[3];
        
        cbWidget[0] = new ComboBoxWidget(String.valueOf(expGroupIx + 1), patch,
                                         new FCB1010ParamModel(patch, 9 + (expGroupIx * 3), FCB1010ParamModel.LSB_MASK),
                                         null,
                                         PRG_CHG_NUMS);
        SelectedPresetModel.putWidget(cbWidget[0]);
        addWidget(expPdlPanel, cbWidget[0],
                  0, 0,
                  1, 1, posFaderNum++);
        
        cbWidget[1] = new ComboBoxWidget("", patch,
                                         new FCB1010ParamModel(patch, 10 + (expGroupIx * 3), FCB1010ParamModel.LSB_MASK),
                                         null,
                                         PRG_CHG_NUMS);
        SelectedPresetModel.putWidget(cbWidget[1]);
        addWidget(expPdlPanel, cbWidget[1],
                  0, 0,
                  1, 1, posFaderNum++);
        
        cbWidget[2] = new ComboBoxWidget("", patch,
                                         new FCB1010ParamModel(patch, 11 + (expGroupIx * 3), FCB1010ParamModel.LSB_MASK),
                                         null,
                                         PRG_CHG_NUMS);
        SelectedPresetModel.putWidget(cbWidget[2]);
        addWidget(expPdlPanel, cbWidget[2],
                  0, 0,
                  1, 1, posFaderNum++);
        
        CheckBoxWidget onOffWidget = new CheckBoxWidget("", patch,
                                                        new FCB1010ParamModel(patch, 9 + (expGroupIx * 3), FCB1010ParamModel.MSB_MASK),
                                                        null);
        SelectedPresetModel.putWidget(onOffWidget);
        addWidget(expPdlPanel, onOffWidget,
                  0, 0,
                  1, 1,
                  negFaderNum--);

        expGroupModel[expGroupIx] = new ControlGroupModel(onOffWidget, cbWidget);
}
    
    /** Adds a noteCtrl widgets to the given panel for the given patch.
        */
    private void addNoteCtrlWidgets(Patch patch, JPanel panel) {
        JPanel noteCtrlPanel = new JPanel();        
//        noteCtrlPanel.setLayout(new BoxLayout(noteCtrlPanel, BoxLayout.X_AXIS));
        noteCtrlPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Note Control",TitledBorder.CENTER,TitledBorder.CENTER));
        panel.add(noteCtrlPanel);
        
        ComboBoxWidget[] cbWidget = new ComboBoxWidget[1];

        cbWidget[0] = new ComboBoxWidget("Note Value", patch,
                                         new FCB1010ParamModel(patch, 15, FCB1010ParamModel.LSB_MASK),
                                         null,
                                         PRG_CHG_NUMS);
        SelectedPresetModel.putWidget(cbWidget[0]);
        addWidget(noteCtrlPanel, cbWidget[0],
                  0, 0,
                  1, 1, posFaderNum++);

        CheckBoxWidget onOffWidget = new CheckBoxWidget("", patch,
                                                        new FCB1010ParamModel(patch, 15, FCB1010ParamModel.MSB_MASK),
                                                        null);
        SelectedPresetModel.putWidget(onOffWidget);
        addWidget(noteCtrlPanel, onOffWidget,
                  0, 0,
                  1, 1,
                  negFaderNum--);
        
        noteGroupModel = new ControlGroupModel(onOffWidget, cbWidget);
    }
    
    /** Adds the global panel to the given panel for the given patch.
        */
    private JPanel addGlobalPanel(Patch patch, JPanel panel) {
        final String[] ccString = new String[] { 
            "Control Chg. 1", "Control Chg. 2", "    Exp. Pedal A", "    Exp. Pedal B", "  Note Number"
        };
        
        final int MIDI_CHANNELS_OFFSET = 2016; // Does not include 7 byte sysex header
        
        JPanel glPanel = new JPanel();        
        glPanel.setLayout(new BoxLayout(glPanel, BoxLayout.X_AXIS));
//        glPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Global Settings",TitledBorder.CENTER,TitledBorder.CENTER));
        
        JPanel pcMidiPanel = new JPanel();        
        pcMidiPanel.setLayout(new BoxLayout(pcMidiPanel, BoxLayout.Y_AXIS));
        //        pcMidiPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"MIDI Channel",TitledBorder.CENTER,TitledBorder.CENTER));
        glPanel.add(pcMidiPanel);
        
        pcMidiPanel.add(new JLabel("MIDI Channel"));
        
        for (int i = 0; i < 5; i++) {
            addWidget(pcMidiPanel, new ComboBoxWidget("Program Change " + (i + 1), patch,
                                                      new FCB1010ParamModel(patch, MIDI_CHANNELS_OFFSET + i, FCB1010ParamModel.BYTE),
                                                      null,
                                                      MIDI_CHANNELS),
                      0, 0,
                      1, 1, posFaderNum++);
        }
        
        JPanel ccMidiPanel = new JPanel();        
        ccMidiPanel.setLayout(new BoxLayout(ccMidiPanel, BoxLayout.Y_AXIS));
        //        ccMidiPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"MIDI Channel",TitledBorder.CENTER,TitledBorder.CENTER));
        glPanel.add(ccMidiPanel);
        
        ccMidiPanel.add(new JLabel("MIDI Channel"));
        
        for (int i = 0; i < 5; i++) {
            addWidget(ccMidiPanel, new ComboBoxWidget(ccString[i], patch,
                                                      new FCB1010ParamModel(patch, MIDI_CHANNELS_OFFSET + 5 + i, FCB1010ParamModel.BYTE),
                                                      null,
                                                      MIDI_CHANNELS),
                      0, 0,
                      1, 1, posFaderNum++);
        }
        
        return glPanel;
    }
}