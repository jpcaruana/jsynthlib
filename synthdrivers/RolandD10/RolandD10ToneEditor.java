/*
 * Copyright 2002 Roger Westerlund
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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import core.CheckBoxWidget;
import core.ComboBoxWidget;
import core.EnvelopeWidget;
import core.Patch;
import core.PatchNameWidget;
import core.ScrollBarWidget;


/**
 *
 * @author  Roger Westerlund <roger.westerlund@home.se>
 */
public class RolandD10ToneEditor extends RolandD10EditorFrame {

    /** Creates a new instance of RolandD10ToneEditor */
    public RolandD10ToneEditor(Patch patch) {
        super("Roland D-10 Tone Editor", patch);
        
        EditSender.setDeviceId(patch.sysex[ 2 ]);
        
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.fill = GridBagConstraints.BOTH;

        gbc.gridx = 0; gbc.gridy = 0;
        scrollPane.add(createCommonPanel(patch), gbc);

        JTabbedPane partTabsPanel = new JTabbedPane();
        for (int part = 1; part < 5; part++) {
            partTabsPanel.add("Partitial " + part, createPartPanel(patch,part));
        }

        gbc.gridx = 1; gbc.gridy = 0;
        gbc.gridwidth = 1; gbc.gridheight = 2;
        scrollPane.add(partTabsPanel, gbc);
    }

    private JPanel createCommonPanel(Patch patch) {

        JPanel commonPanel = new JPanel(new GridBagLayout());
        setNamedBorder(commonPanel, "Common parameters");

        Object[] structureImages = RolandD10EditorFrame.getStructureImages();
        // A data model for the pratial mute checkboxes which operates on the same byte.
        PartMuteDataModel dataModel = new PartMuteDataModel(patch, D10Constants.COMMON_PARTIAL_MUTE);
        PartMuteSender muteSender = new PartMuteSender(dataModel, EditSender
                .getToneSender(D10Constants.COMMON_PARTIAL_MUTE));

        addWidget(commonPanel,new PatchNameWidget("Name", patch),0,GridBagConstraints.RELATIVE,1,1,0);
        addWidget(commonPanel,new ComboBoxWidget("Structure for partial 1 & 2",
                patch,new D10ParamModel(patch,D10Constants.COMMON_STRUCTURE_12),
                EditSender.getToneSender(D10Constants.COMMON_STRUCTURE_12),
                structureImages),0,GridBagConstraints.RELATIVE,1,1,0);
        addWidget(commonPanel,new ComboBoxWidget("Structure for partial 3 & 4",
                patch,new D10ParamModel(patch,D10Constants.COMMON_STRUCTURE_34),
                EditSender.getToneSender(D10Constants.COMMON_STRUCTURE_34),
                structureImages),0,GridBagConstraints.RELATIVE,1,1,0);
        addWidget(commonPanel,new ComboBoxWidget("Env mode", patch,
                new D10ParamModel(patch,D10Constants.COMMON_ENV_MODE), 
                EditSender.getToneSender(D10Constants.COMMON_ENV_MODE),
                new String[] {"Normal","No sustain"}),0,GridBagConstraints.RELATIVE,1,1,0);
        addWidget(commonPanel,new CheckBoxWidget("Partial 1",patch,
                new PartMuteParamModel(1,dataModel),muteSender),
                0,GridBagConstraints.RELATIVE,1,1,0);
        addWidget(commonPanel,new CheckBoxWidget("Partial 2",patch,
                new PartMuteParamModel(2,dataModel),muteSender),
                0,GridBagConstraints.RELATIVE,1,1,0);
        addWidget(commonPanel,new CheckBoxWidget("Partial 3",patch,
                new PartMuteParamModel(3,dataModel),muteSender),
                0,GridBagConstraints.RELATIVE,1,1,0);
        addWidget(commonPanel,new CheckBoxWidget("Partial 4",patch,
                new PartMuteParamModel(4,dataModel),muteSender),
                0,GridBagConstraints.RELATIVE,1,1,0);
//        gbc.weightx = 0;

        return commonPanel;
    }

    private JPanel createPartPanel(Patch patch, int part) {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
//        gbc.weightx = 1; gbc.weighty = 1;
//        gbc.gridwidth = 1; gbc.gridheight = 1;

        setBounds(gbc,0,0,1,1);
        panel.add(createWGPanel(patch, part), gbc);

        setBounds(gbc,0,1,1,1);
        panel.add(createTVFPanel(patch, part), gbc);

        setBounds(gbc,1,0,1,2);
        panel.add(createPENVPanel(patch, part), gbc);

        setBounds(gbc,0,2,1,2);
        panel.add(createTVAPanel(patch, part), gbc);

        setBounds(gbc,1,2,1,1);
        panel.add(createPLFOPanel(patch, part), gbc);

        setBounds(gbc,1,3,1,1);
        panel.add(createTVFENVPanel(patch, part), gbc);

        return panel;
    }

    private void setBounds(GridBagConstraints gbc, int x, int y, int width, int height) {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        gbc.gridheight = height;
    }

    private JComponent createWGPanel(Patch patch, int part) {
        JPanel panel = new JPanel(new GridBagLayout());
        setNamedBorder(panel, "Wave Generator (WG)");

        int base = getPartBaseOffset(part);

        addWidget(panel,new ComboBoxWidget("Pitch Coarse",patch,
                new D10ParamModel(patch,base+D10Constants.PART_WG_PITCH_COARSE),
                EditSender.getToneSender(base+D10Constants.PART_WG_PITCH_COARSE),
                generateKeys()),
                0,GridBagConstraints.RELATIVE,1,1,0);
        addWidget(panel,new ComboBoxWidget("Pitch Keyfollow",patch,
                new D10ParamModel(patch,base+D10Constants.PART_WG_PITCH_KEYFOLLOW),
                EditSender.getToneSender(base+D10Constants.PART_WG_PITCH_KEYFOLLOW),
                new String[] {"-1","-1/2","-1/4","0","1/8","1/4","3/8","1/2",
                        "5/8","3/4","7/8","1","5/4","3/2","2","s1","s2"}),
                1,GridBagConstraints.RELATIVE,1,1,0);
        addWidget(panel,new ScrollBarWidget("Pitch Fine",patch,0,100,-50,
                new D10ParamModel(patch,base+D10Constants.PART_WG_PITCH_FINE),
                EditSender.getToneSender(base+D10Constants.PART_WG_PITCH_FINE)),
                0,GridBagConstraints.RELATIVE,GridBagConstraints.REMAINDER,1,0);
        addWidget(panel,new ComboBoxWidget("Waveform / PCM Bank",patch,
                new D10ParamModel(patch,base+D10Constants.PART_WG_WAVEFORM),
                EditSender.getToneSender(base+D10Constants.PART_WG_WAVEFORM),
                new String[] {"SQU / 1","SAW / 1","SQU / 2","SAW / 2"}),
                0,GridBagConstraints.RELATIVE,1,1,0);
        addWidget(panel,new ComboBoxWidget("Pitch Bender Switch",patch,
                new D10ParamModel(patch,base+D10Constants.PART_WG_PITCH_FINE),
                EditSender.getToneSender(base+D10Constants.PART_WG_PITCH_FINE),
                new String[] {"Off","On"}),
                1,GridBagConstraints.RELATIVE,1,1,0);
        addWidget(panel,new ScrollBarWidget("PCM Wave Number",patch,0,127,1,
                new D10ParamModel(patch,base+D10Constants.PART_WG_PCM_WAVE_NO),
                EditSender.getToneSender(base+D10Constants.PART_WG_PCM_WAVE_NO)),
                0,GridBagConstraints.RELATIVE,GridBagConstraints.REMAINDER,1,0);
        addWidget(panel,new ScrollBarWidget("Pulse Width",patch,0,100,0,
                new D10ParamModel(patch,base+D10Constants.PART_WG_PULSE_WIDTH),
                EditSender.getToneSender(base+D10Constants.PART_WG_PULSE_WIDTH)),
                0,GridBagConstraints.RELATIVE,GridBagConstraints.REMAINDER,1,0);
        addWidget(panel,new ScrollBarWidget("PW Velocity Sens",patch,0,14,-7,
                new D10ParamModel(patch,base+D10Constants.PART_WG_PW_VELO_SENSE),
                EditSender.getToneSender(base+D10Constants.PART_WG_PW_VELO_SENSE)),
                0,GridBagConstraints.RELATIVE,GridBagConstraints.REMAINDER,1,0);

        return panel;
    }

    private JPanel createPENVPanel(Patch patch, int part) {
        JPanel panel = new JPanel(new GridBagLayout());
        setNamedBorder(panel, "Pitch Envelope");
        int base = getPartBaseOffset(part);
        int yMin = 0; int yMax = 100; int yBase = 0;
        EnvelopeWidget.Node[] nodes = new EnvelopeWidget.Node[] {
            new EnvelopeWidget.Node(
                0,0,null,yMin,yMax,
                new D10ParamModel(patch,base+D10Constants.PART_P_ENV_LEVEL_0),
                0,false,null,
                EditSender.getToneSender(base+D10Constants.PART_P_ENV_LEVEL_0),
                null,"Level 0"), 
            new EnvelopeWidget.Node(
                0,100,
                new D10ParamModel(patch,base+D10Constants.PART_P_ENV_TIME_1),
                yMin,yMax,
                new D10ParamModel(patch,base+D10Constants.PART_P_ENV_LEVEL_1),
                0,false,
                EditSender.getToneSender(base+D10Constants.PART_P_ENV_TIME_1),
                EditSender.getToneSender(base+D10Constants.PART_P_ENV_LEVEL_1),
                "Time 1","Level 1"), 
            new EnvelopeWidget.Node(
                0,100,
                new D10ParamModel(patch,base+D10Constants.PART_P_ENV_TIME_2),
                yMin,yMax,
                new D10ParamModel(patch,base+D10Constants.PART_P_ENV_LEVEL_2),
                0,false,
                EditSender.getToneSender(base+D10Constants.PART_P_ENV_TIME_2),
                EditSender.getToneSender(base+D10Constants.PART_P_ENV_LEVEL_2),
                "Time 2","Level 2"), 
            new EnvelopeWidget.Node(
                0,100,
                new D10ParamModel(patch,base+D10Constants.PART_P_ENV_TIME_3),
                50,50,null,0,false,
                EditSender.getToneSender(base+D10Constants.PART_P_ENV_TIME_3),null,
                "Time 3",null), 
            new EnvelopeWidget.Node(
                100,100,null,50,50,null,0,false,null,null,null,null),
            new EnvelopeWidget.Node(
                0,100,
                new D10ParamModel(patch,base+D10Constants.PART_P_ENV_TIME_4),
                yMin,yMax,
                new D10ParamModel(patch,base+D10Constants.PART_P_ENV_END_LEVEL),
                yBase,false,
                EditSender.getToneSender(base+D10Constants.PART_P_ENV_TIME_4),
                EditSender.getToneSender(base+D10Constants.PART_P_ENV_END_LEVEL),
                "Time 4","Sustain Level")
        };
        addWidget(panel,new ScrollBarWidget("Depth",patch,0,10,0,
                new D10ParamModel(patch,base+D10Constants.PART_P_ENV_DEPTH),
                EditSender.getToneSender(base+D10Constants.PART_P_ENV_DEPTH)),
                0,GridBagConstraints.RELATIVE,GridBagConstraints.REMAINDER,1,0);
        addWidget(panel,new ScrollBarWidget("Velocity Sens",patch,0,3,0,
                new D10ParamModel(patch,base+D10Constants.PART_P_ENV_VELO_SENS),
                EditSender.getToneSender(base+D10Constants.PART_P_ENV_VELO_SENS)),
                0,GridBagConstraints.RELATIVE,GridBagConstraints.REMAINDER,1,0);
        addWidget(panel,new ScrollBarWidget("Time Keyfollow",patch,0,4,0,
                new D10ParamModel(patch,base+D10Constants.PART_P_ENV_TIME_KEYF),
                EditSender.getToneSender(base+D10Constants.PART_P_ENV_TIME_KEYF)),
                0,GridBagConstraints.RELATIVE,GridBagConstraints.REMAINDER,1,0);
        addWidget(panel,new EnvelopeWidget(null,patch,nodes),
                0,GridBagConstraints.RELATIVE,GridBagConstraints.REMAINDER,1,0);

        return panel;
    }

    private JPanel createPLFOPanel(Patch patch, int part) {
        JPanel panel = new JPanel(new GridBagLayout());
        setNamedBorder(panel, "Pitch LFO");
        int base = getPartBaseOffset(part);
        addWidget(panel,new ScrollBarWidget("Rate",patch,0,100,0,
                new D10ParamModel(patch,base+D10Constants.PART_P_LFO_RATE),
                EditSender.getToneSender(base+D10Constants.PART_P_LFO_RATE)),
                0,GridBagConstraints.RELATIVE,GridBagConstraints.REMAINDER,1,0);
        addWidget(panel,new ScrollBarWidget("Depth",patch,0,100,0,
                new D10ParamModel(patch,base+D10Constants.PART_P_LFO_DEPTH),
                EditSender.getToneSender(base+D10Constants.PART_P_LFO_DEPTH)),
                0,GridBagConstraints.RELATIVE,GridBagConstraints.REMAINDER,1,0);
        addWidget(panel,new ScrollBarWidget("Modulation Sens",patch,0,100,0,
                new D10ParamModel(patch,base+D10Constants.PART_P_LFO_MOD_SENS),
                EditSender.getToneSender(base+D10Constants.PART_P_LFO_MOD_SENS)),
                0,GridBagConstraints.RELATIVE,GridBagConstraints.REMAINDER,1,0);
        return panel;
    }

    private JPanel createTVFPanel(Patch patch, int part) {
        JPanel panel = new JPanel(new GridBagLayout());
        setNamedBorder(panel, "Time Variant Filter (TVF)");
        int base = getPartBaseOffset(part);
        addWidget(panel,new ScrollBarWidget("Cutoff Frequency",patch,0,100,0,
                new D10ParamModel(patch,base+D10Constants.PART_TVF_CUTOFF_FREQ),
                EditSender.getToneSender(base+D10Constants.PART_TVF_CUTOFF_FREQ)),
                0,0,2,1,0);
        addWidget(panel,new ScrollBarWidget("Resonance",patch,0,30,0,
                new D10ParamModel(patch,base+D10Constants.PART_TVF_RESONANCE),
                EditSender.getToneSender(base+D10Constants.PART_TVF_RESONANCE)),
                0,1,2,1,0);
        addWidget(panel,new ComboBoxWidget("Keyfollow",patch,
                new D10ParamModel(patch,base+D10Constants.PART_TVF_KEYFOLLOW),
                EditSender.getToneSender(base+D10Constants.PART_TVF_KEYFOLLOW),
                new String[] {"-1","-1/2","-1/4","0","1/8","1/4","3/8","1/2",
                        "5/8","3/4","7/8","1","5/4","3/2","2"}),
                2,0,GridBagConstraints.REMAINDER,2,0);
        addWidget(panel,new ComboBoxWidget("Bias Point",patch,
                new D10ParamModel(patch,base+D10Constants.PART_TVF_BIAS_POINT_DIR),
                EditSender.getToneSender(base+D10Constants.PART_TVF_BIAS_POINT_DIR),
                getBiasPointArray()),
                0,GridBagConstraints.RELATIVE,1,1,0);
        addWidget(panel,new ScrollBarWidget("Bias Level",patch,0,14,-7,
                new D10ParamModel(patch,base+D10Constants.PART_TVF_BIAS_LEVEL),
                EditSender.getToneSender(base+D10Constants.PART_TVF_BIAS_LEVEL)),
                1,GridBagConstraints.RELATIVE,GridBagConstraints.REMAINDER,1,0);
        return panel;
    }

    private JPanel createTVFENVPanel(Patch patch, int part) {
        JPanel panel = new JPanel(new GridBagLayout());
        setNamedBorder(panel, "Time Variant Filter Envelope (TVF ENV)");
        int base = getPartBaseOffset(part);

        EnvelopeWidget.Node[] nodes = new EnvelopeWidget.Node[] {
            new EnvelopeWidget.Node(
                0,0,null,0,0,null,0,false,null,null,null,null), 
            new EnvelopeWidget.Node(
                0,100,
                new D10ParamModel(patch,base+D10Constants.PART_TVF_ENV_TIME_1),
                0,100,
                new D10ParamModel(patch,base+D10Constants.PART_TVF_ENV_LEVEL_1),
                0,false,
                EditSender.getToneSender(base+D10Constants.PART_TVF_ENV_TIME_1),
                EditSender.getToneSender(base+D10Constants.PART_TVF_ENV_LEVEL_1),
                "Time 1","Level 1"), 
            new EnvelopeWidget.Node(
                0,100,
                new D10ParamModel(patch,base+D10Constants.PART_TVF_ENV_TIME_2),
                0,100,
                new D10ParamModel(patch,base+D10Constants.PART_TVF_ENV_LEVEL_2),
                0,false,
                EditSender.getToneSender(base+D10Constants.PART_TVF_ENV_TIME_2),
                EditSender.getToneSender(base+D10Constants.PART_TVF_ENV_LEVEL_2),
                "Time 2","Level 2"), 
            new EnvelopeWidget.Node(
                0,100,
                new D10ParamModel(patch,base+D10Constants.PART_TVF_ENV_TIME_3),
                0,100,
                new D10ParamModel(patch,base+D10Constants.PART_TVF_ENV_SUSTAIN_LEVEL),
                0,false,
                EditSender.getToneSender(base+D10Constants.PART_TVF_ENV_TIME_3),
                EditSender.getToneSender(base+D10Constants.PART_TVF_ENV_SUSTAIN_LEVEL),
                "Time 3","Sustain level"), 
            new EnvelopeWidget.Node(
                100,100,null,EnvelopeWidget.Node.SAME,EnvelopeWidget.Node.SAME,null,0,false,
                null,null,null,null),
            new EnvelopeWidget.Node(
                0,100,
                new D10ParamModel(patch,base+D10Constants.PART_P_ENV_TIME_4),
                0,0,null,0,false,
                EditSender.getToneSender(base+D10Constants.PART_P_ENV_TIME_4),null,
                "Time 4",null)
        };
        addWidget(panel,new ScrollBarWidget("Depth",patch,0,100,0,
                new D10ParamModel(patch,base+D10Constants.PART_TVF_ENV_DEPTH),
                EditSender.getToneSender(base+D10Constants.PART_TVF_ENV_DEPTH)),
                0,GridBagConstraints.RELATIVE,GridBagConstraints.REMAINDER,1,0);
        addWidget(panel,new ScrollBarWidget("Velocity Sens",patch,0,100,0,
                new D10ParamModel(patch,base+D10Constants.PART_TVF_ENV_VELO_SENS),
                EditSender.getToneSender(base+D10Constants.PART_TVF_ENV_VELO_SENS)),
                0,GridBagConstraints.RELATIVE,GridBagConstraints.REMAINDER,1,0);
        addWidget(panel,new ScrollBarWidget("Depth Keyfollow",patch,0,4,0,
                new D10ParamModel(patch,base+D10Constants.PART_TVF_ENV_DEPTH_KEYF),
                EditSender.getToneSender(base+D10Constants.PART_TVF_ENV_DEPTH_KEYF)),
                0,GridBagConstraints.RELATIVE,GridBagConstraints.REMAINDER,1,0);
        addWidget(panel,new ScrollBarWidget("Time Keyfollow",patch,0,4,0,
                new D10ParamModel(patch,base+D10Constants.PART_TVF_ENV_TIME_KEYF),
                EditSender.getToneSender(base+D10Constants.PART_TVF_ENV_TIME_KEYF)),
                0,GridBagConstraints.RELATIVE,GridBagConstraints.REMAINDER,1,0);
        addWidget(panel,new EnvelopeWidget(null,patch,nodes),
                0,GridBagConstraints.RELATIVE,GridBagConstraints.REMAINDER,1,0);
        return panel;
    }

    private JPanel createTVAPanel(Patch patch, int part) {
        JPanel panel = new JPanel(new GridBagLayout());
        setNamedBorder(panel, "Time Variant Amplifier (TVA)");
        int base = getPartBaseOffset(part);

        EnvelopeWidget.Node[] nodes = new EnvelopeWidget.Node[] {
            new EnvelopeWidget.Node(
                0,0,null,0,0,null,0,false,null,null,null,null), 
            new EnvelopeWidget.Node(
                0,100,
                new D10ParamModel(patch,base+D10Constants.PART_TVA_ENV_TIME_1),
                0,100,
                new D10ParamModel(patch,base+D10Constants.PART_TVA_ENV_LEVEL_1),
                0,false,
                EditSender.getToneSender(base+D10Constants.PART_TVA_ENV_TIME_1),
                EditSender.getToneSender(base+D10Constants.PART_TVA_ENV_LEVEL_1),
                "Time 1","Level 1"), 
            new EnvelopeWidget.Node(
                0,100,
                new D10ParamModel(patch,base+D10Constants.PART_TVA_ENV_TIME_2),
                0,100,
                new D10ParamModel(patch,base+D10Constants.PART_TVA_ENV_LEVEL_2),
                0,false,
                EditSender.getToneSender(base+D10Constants.PART_TVA_ENV_TIME_2),
                EditSender.getToneSender(base+D10Constants.PART_TVA_ENV_LEVEL_2),
                "Time 2","Level 2"), 
            new EnvelopeWidget.Node(
                0,100,
                new D10ParamModel(patch,base+D10Constants.PART_TVA_ENV_TIME_3),
                0,100,
                new D10ParamModel(patch,base+D10Constants.PART_TVA_ENV_SUSTAIN_LEVEL),
                0,false,
                EditSender.getToneSender(base+D10Constants.PART_TVA_ENV_TIME_3),
                EditSender.getToneSender(base+D10Constants.PART_TVA_ENV_SUSTAIN_LEVEL),
                "Time 3","Sustain Level"), 
            new EnvelopeWidget.Node(
                100,100,null,EnvelopeWidget.Node.SAME,EnvelopeWidget.Node.SAME,null,0,false,
                null,null,null,null),
            new EnvelopeWidget.Node(
                0,100,
                new D10ParamModel(patch,base+D10Constants.PART_TVA_ENV_TIME_4),
                0,0,null,0,false,
                EditSender.getToneSender(base+D10Constants.PART_TVA_ENV_TIME_4),null,
                "Time 5",null)
        };
        addWidget(panel,new ScrollBarWidget("Level",patch,0,100,0,
                new D10ParamModel(patch,base+D10Constants.PART_TVA_LEVEL),
                EditSender.getToneSender(base+D10Constants.PART_TVA_LEVEL)),
                0,GridBagConstraints.RELATIVE,GridBagConstraints.REMAINDER,1,0);
        addWidget(panel,new ScrollBarWidget("Velocity sens",patch,0,100,-50,
                new D10ParamModel(patch,base+D10Constants.PART_TVA_VELO_SENS),
                EditSender.getToneSender(base+D10Constants.PART_TVA_VELO_SENS)),
                0,GridBagConstraints.RELATIVE,GridBagConstraints.REMAINDER,1,0);
        addWidget(panel,new ComboBoxWidget("Bias point 1",patch,
                new D10ParamModel(patch,base+D10Constants.PART_TVA_BIAS_POINT_1),
                EditSender.getToneSender(base+D10Constants.PART_TVA_BIAS_POINT_1),
                getBiasPointArray()),
                0,GridBagConstraints.RELATIVE,1,1,0);
        addWidget(panel,new ScrollBarWidget("Bias level 1",patch,0,12,-12,
                new D10ParamModel(patch,base+D10Constants.PART_TVA_BIAS_LEVEL_1),
                EditSender.getToneSender(base+D10Constants.PART_TVA_BIAS_LEVEL_1)),
                1,GridBagConstraints.RELATIVE,GridBagConstraints.REMAINDER,1,0);
        addWidget(panel,new ComboBoxWidget("Bias point 2",patch,
                new D10ParamModel(patch,base+D10Constants.PART_TVA_BIAS_POINT_2),
                EditSender.getToneSender(base+D10Constants.PART_TVA_BIAS_POINT_2),
                getBiasPointArray()),
                0,GridBagConstraints.RELATIVE,1,1,0);
        addWidget(panel,new ScrollBarWidget("Bias level 2",patch,0,12,-12,
                new D10ParamModel(patch,base+D10Constants.PART_TVA_BIAS_LEVEL_2),
                EditSender.getToneSender(base+D10Constants.PART_TVA_BIAS_LEVEL_2)),
                1,GridBagConstraints.RELATIVE,GridBagConstraints.REMAINDER,1,0);
        addWidget(panel,new EnvelopeWidget(null,patch,nodes),
                0,GridBagConstraints.RELATIVE,GridBagConstraints.REMAINDER,1,0);
        return panel;
    }

    private static String[] biasPoints;

    private String[] getBiasPointArray() {
        String[] result;
        if (null != biasPoints) {
            result = biasPoints;
        } else {
            final String prefixes = "<>";
            final int items = 64;
            int offset = 0;
            int start = 9;
            result = new String[ 128 ];
            for (int index = 0; index < prefixes.length(); index++) {
                generateKeyArray(result, offset,
                    prefixes.substring(index, index+1), start, items);
                offset += items;
            }
            biasPoints = result;
        }
        return result;
    }

    private String[] generateKeys() {
        final int items = 97;
        final String[] result = new String[ items ];
        return generateKeyArray(result, 0, null, 0, items);
    }

    private String[] generateKeyArray(String[] result, int offset,
                                      String prefix, int start, int items) {
        final String keyString = "CDEFGAH";
        final StringBuffer noteString = new StringBuffer();
        
        for (int index = 0; index < items; index++) {
            int noteNumber = start + index;
            int octave = noteNumber / 12 + 1;
            int note = noteNumber % 12;

            // Get rid of the gap between E and F.
            if (note > 4) {
                note++;
            }

            noteString.setLength(0);    // Truncate to reuse
            if (null != prefix) {
                noteString.append(prefix);
            }
            noteString.append(keyString.charAt(note >>> 1));
            if ((note & 1) != 0) {
                noteString.append('#');
            }
            noteString.append(octave);
            result[ index + offset ] = noteString.toString();
        }

        return result;
    }
    
    /**
     * Calculates the base address for the specified part.
     */
    private int getPartBaseOffset(int part) {
        return D10Constants.COMMON_SIZE + (part - 1) * D10Constants.PART_SIZE;
    }

    /**
     * TODO This code with friends should be moved out of this project.
     * 
     * @param data
     * @return
     */
    public static String dump(byte[] data) {
        return dump(data, 8);
    }

    public static String dump(byte[] data, int width) {
        return dump(data, data.length, width);
    }

    public static String dump(byte[] data, int length, int width) {
        StringBuffer result = new StringBuffer();
        int start = 0;
        for (int rowStart = start; rowStart < length; rowStart += width) {
            StringBuffer asciiData = new StringBuffer(width);
            StringBuffer hexData = new StringBuffer(width*3);
            for (int index = 0; index < width; index++) {
                int offset = rowStart + index;
                if ( offset < length) {
                    byte dataByte = data[ offset ];
                    if (!Character.isISOControl((char)dataByte)) {
                        asciiData.append((char)dataByte);
                    } else {
                        asciiData.append('.');
                    }
                    hexData.append(makeHexByte(dataByte));
                    hexData.append(' ');
                } else {
                    hexData.append("   ");
                }
            }
            result.append(makeHex(rowStart,4));
            result.append(' ');
            result.append(hexData);
            result.append(asciiData);
            result.append('\n');
        }
        return result.toString();
    }

    public static String makeHexByte(int number) {
        return makeHex(number, 2);
    }

    public static String makeHex(int number, int positions) {
        String hexDigits = "0123456789ABCDEF";
        StringBuffer hex = new StringBuffer(positions);
        for (int index = 0; index < positions; index++) {
            hex.append(hexDigits.charAt(number & 0xf));
            number >>= 4;
        }
        return hex.reverse().toString();
    }

}
