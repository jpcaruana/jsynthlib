//
//  Line6BassPodSingleEditor.java
//  JSynthLib
//
//  Created by Jeff Weber on Sat Jun 26 2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package synthdrivers.Line6BassPod;

import core.*;
import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;

public class Line6BassPodSingleEditor extends PatchEditorFrame {
    static int pgmDumpheaderSize;
    static final String[] ampModel = {
        "Tube Preamp",
        "Session",
        "California",
        "Jazz Tone",
        "Adam and Eve",
        "Eighties",
        "Stadium",
        "Amp 360",
        "Rock Classic",
        "Brit Major",
        "Brit Super",
        "Silver Panel",
        "Brit Class A",
        "Motor City",
        "Flip Top",
        "Sub Dub"
    };
    
    static final String[] ampModelDesc = {
        "Tube Preamp",
        "SWR SM-400",
        "Mesa Boogie Bass 400+",
        "Polytone Mini-Brute",
        "Eden Traveller WT-300",
        "Gallien Kruger 800RB",
        "Sunn Colliseum",
        "Acoustic 360",
        "Ampeg SVT",
        "Marshall Major",
        "Marshall Super Bass",
        "Fender Bassman Head",
        "Vox AC-100",
        "Versatone Pan-O-Flex",
        "Ampeg B-15",
        "When it's time to go lower than low"
    };
    
    static final String[] cabType = {
        "8x10 Ampeg SVT",
        "4x10 Eden",
        "4x10 SWR Goliath",
        "4x10 Hartke",
        "1x12 Versatone Pan-O-Flex",
        "4x12 Marshall 1967 w/Celestions",
        "1x15 Ampeg B-15",
        "1x15 Polytone Mini-Brute",
        "2x15 Vox AC-100",
        "2x15 Mesa Boogie",
        "2x15 Fender Bassman w/JBLSs",
        "4x15 1976 Marshall",
        "1x18 SWR Big Ben",
        "1x18 Acoustic 360",
        "1x12-1x18 Sunn Coliseum 8028",
        "No Cabinet Emulation"
    };
    
    static final String[] effectType = {
        "Octave Down (Boss OC-2)",
        "Analog Chorus (Boss CE-1)",
        "Danish Chorus (t.c. Stereo Chorus)",
        "Orange Phase (Phase 90)",
        "Gray Flanger (MXR Flanger)",
        "Tron Down (Mu-Tron III)",
        "Tron Up (Mu-Tron III)",
        "Sample & Hold (Oberheim VCF)",
        "S&H Flanger",
        "S&H Driver",
        "Bass Synth (Boss SYB-3)",
        "Danish Driver (t.c. Line Driver)",
        "Large Pie (Big Muff)",
        "Pig Foot (Hog's Foot)",
        "Rodent (Rat)",
        "Bypass"
    };    
    
    static final int[] effectTranslate = {
        11, 9, 8, 0, 1, 3, 2, 6, 7, 5, 4, 12, 13, 15, 14, 10
    };
    
    static final String[] volPedLoc = {
        "Pre-Amp",
        "Post-Amp"
    };
    
    JPanel line6EditPanel;
    JPanel leftEditPanel;
    JPanel rightEditPanel;
    JPanel effParmsPanel;
    
    JLabel modelDesc;
    
    ComboBoxWidget patchSelector;
    HideableKnobWidget midSweepKnob;
    
    public Line6BassPodSingleEditor(Patch patch)
    {
        super ("Line 6 Bass POD Single Editor",patch);   
        pgmDumpheaderSize = 9;
        line6EditPanel = new JPanel();
        line6EditPanel.setLayout(new BoxLayout(line6EditPanel, BoxLayout.X_AXIS));
        scrollPane.add(line6EditPanel,gbc);
        
        leftEditPanel = new JPanel();
        leftEditPanel.setLayout(new BoxLayout(leftEditPanel, BoxLayout.Y_AXIS));
        line6EditPanel.add(leftEditPanel);
        addPatchNamePanel(leftEditPanel, patch);
        addAmpPane(leftEditPanel, patch);
        addStompBoxPane(leftEditPanel, patch);
        
        rightEditPanel = new JPanel();
        rightEditPanel.setLayout(new BoxLayout(rightEditPanel, BoxLayout.Y_AXIS));
        line6EditPanel.add(rightEditPanel);
        addEffectsPanel(rightEditPanel, patch);
        addParametricPanel(rightEditPanel, patch);
        addCompressPanel(rightEditPanel, patch);
        
        pack();
        show();
    }
    
    private void addPatchNamePanel(JPanel parentPanel, Patch patch) {
        JPanel patchNamePanel = new JPanel();
        parentPanel.add(patchNamePanel);
        addWidget(patchNamePanel, new PatchNameWidget("Program Name ", patch, ((Driver) patch.getDriver()).getPatchNameSize()),0,0,1,1,1);
    }
    
    private void addAmpPane(JPanel parentPanel, Patch patch) {
        JPanel modelPane = new JPanel();
        modelPane.setLayout(new BoxLayout(modelPane, BoxLayout.Y_AXIS));
        parentPanel.add(modelPane);
        
        JPanel patchSelPane = new JPanel();
        patchSelPane.setLayout(new BoxLayout(patchSelPane, BoxLayout.Y_AXIS));
        modelPane.add(patchSelPane);
        
        JPanel speakerPane = new JPanel();
        modelPane.add(speakerPane);
        addWidget(speakerPane,new ComboBoxWidget("Cabinet Type", patch, new CabTypeModel(patch, 31 + pgmDumpheaderSize),new CCCabTypeSender(71), cabType), 0,0,1,1,2);
        addWidget(speakerPane,new     KnobWidget("Air", patch, 0, 127,0, new ScaledParamModel(patch, 32 + pgmDumpheaderSize, 127, 63),new CCSender(72)),0,0,1,1,7);
        modelPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Amp Model",TitledBorder.CENTER,TitledBorder.CENTER));
        
        JPanel ampPane=new JPanel();
        ampPane.setLayout(new BoxLayout(ampPane, BoxLayout.Y_AXIS));
        parentPanel.add(ampPane,gbc);    
        
        JPanel ampKnobsPane = new JPanel();
        ampPane.add(ampKnobsPane);
        addWidget(ampKnobsPane,new KnobWidget("Drive",    patch, 0, 127,0,new ScaledParamModel(patch, 4 + pgmDumpheaderSize, 127, 63),new CCSender(13)),0,0,1,1,8);
        
        addWidget(ampKnobsPane,new KnobWidget("Bass",     patch, 0, 127,0,new ScaledParamModel(patch,6 + pgmDumpheaderSize, 127, 63),new CCSender(14)),2,0,1,1,9);
        addWidget(ampKnobsPane,new KnobWidget("Mid",      patch, 0, 127,0,new ScaledParamModel(patch,7 + pgmDumpheaderSize, 127, 63),new CCSender(15)),3,0,1,1,10);
        
        midSweepKnob = new HideableKnobWidget("Mid Freq", patch, 0, 127,0,new ScaledParamModel(patch,12 + pgmDumpheaderSize, 127, 63),new CCSender(28));
        addWidget(ampKnobsPane,midSweepKnob,5,0,1,1,11);
        
        addWidget(ampKnobsPane,new KnobWidget("Treble",   patch, 0, 127,0,new ScaledParamModel(patch,8 + pgmDumpheaderSize, 127, 63),new CCSender(16)),4,0,1,1,12);
        addWidget(ampKnobsPane,new KnobWidget("Volume",   patch, 0, 127,0,new ScaledParamModel(patch,10 + pgmDumpheaderSize, 127, 63),new CCSender(17)),6,0,1,1,13);
        JPanel ampBtnsPane = new JPanel();
        
        JPanel modelDescPanel = new JPanel();
        modelDesc = new JLabel();
        modelDescPanel.add(modelDesc);
        patchSelector = new ComboBoxWidget("Amp Model",    patch, new AmpModelModel(patch, 3 + pgmDumpheaderSize),new CCAmpModelSender(12), ampModel);
        addWidget(patchSelPane, patchSelector, 0,0,1,1,3);
        patchSelPane.add(modelDescPanel);
        
        ampPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Preamp",TitledBorder.CENTER,TitledBorder.CENTER));
    }
    
    private void addStompBoxPane(JPanel parentPanel, Patch patch) {
        JPanel stompBoxPane=new JPanel();
        stompBoxPane.setLayout(new BoxLayout(stompBoxPane, BoxLayout.X_AXIS));
        parentPanel.add(stompBoxPane,gbc);
        
        JPanel gatePane=new JPanel();
        gatePane.setLayout(new BoxLayout(gatePane, BoxLayout.Y_AXIS));
        stompBoxPane.add(gatePane);
        addWidget(gatePane, new CheckBoxWidget("Noise Gate", patch, new ScaledParamModel(patch, 0 + pgmDumpheaderSize), new CCSender(22, 127)),0,0,1,1,-1);
        JPanel gateKnobsPane=new JPanel();
        gatePane.add(gateKnobsPane);
        addWidget(gateKnobsPane,new KnobWidget("Threshold", patch, 0, 127,0,new ScaledParamModel(patch,16 + pgmDumpheaderSize, 127, 96),new CCSender(23, true)),0,0,1,1,14);
        addWidget(gateKnobsPane,new KnobWidget("Decay",     patch, 0, 127,0,new ScaledParamModel(patch,17 + pgmDumpheaderSize, 127, 63),new CCSender(24)),1,0,1,1,15);
        gatePane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Noise Gate",TitledBorder.CENTER,TitledBorder.CENTER));
        
        JPanel wahPane = new JPanel();
        wahPane.setLayout(new BoxLayout(wahPane, BoxLayout.Y_AXIS));
        stompBoxPane.add(wahPane);
        // Global Wah does is not represented in sysex record. Use NullModel
        addWidget(wahPane, new CheckBoxWidget("Global Wah Wah", patch, new NullModel(), new CCSender(43, 127)),0,0,1,1,-2);
        JPanel wahKnobsPane=new JPanel();
        wahPane.add(wahKnobsPane);
        addWidget(wahKnobsPane,new KnobWidget("Bottom",    patch, 0, 127,0,new ScaledParamModel(patch,19 + pgmDumpheaderSize),new CCSender(44)),2,0,1,1,16);
        addWidget(wahKnobsPane,new KnobWidget("Top",       patch, 0, 127,0,new ScaledParamModel(patch,20 + pgmDumpheaderSize),new CCSender(45)),3,0,1,1,17);
        addWidget(wahKnobsPane,new KnobWidget("Current",   patch, 0, 127,0,new ScaledParamModel(patch,18 + pgmDumpheaderSize),new CCSender(4)),4,0,1,1,18);
        wahPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Wah Wah",TitledBorder.CENTER,TitledBorder.CENTER));
        
        JPanel volPedPane = new JPanel();
        volPedPane.setLayout(new BoxLayout(volPedPane, BoxLayout.Y_AXIS));
        stompBoxPane.add(volPedPane);
        addWidget(volPedPane, new ComboBoxWidget("Vol Pedal", patch, new ScaledParamModel(patch,24 + pgmDumpheaderSize),new CCSender(47, 127),volPedLoc), 0,0,1,1,4);
        JPanel volPedKnobsPane=new JPanel();
        volPedPane.add(volPedKnobsPane);
        addWidget(volPedKnobsPane,new KnobWidget("Minimum",   patch, 0, 127,0,new ScaledParamModel(patch,23 + pgmDumpheaderSize),new CCSender(46)),5,0,1,1,19);
        addWidget(volPedKnobsPane,new KnobWidget("Current",   patch, 0, 127,0,new ScaledParamModel(patch,22 + pgmDumpheaderSize),new CCSender(7)),6,0,1,1,20);
        volPedPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Volume Pedal",TitledBorder.CENTER,TitledBorder.CENTER));
    }
    
    private void addEffectsPanel(JPanel parentPanel, Patch patch) {
        JPanel effectsPanel = new JPanel();
        effectsPanel.setLayout(new BoxLayout(effectsPanel, BoxLayout.Y_AXIS));
        
        effParmsPanel = new JPanel(new CardLayout());
        createCards(effParmsPanel, patch);
        
        addWidget(effectsPanel,new ComboBoxWidget("Effects", patch, new EffectModel(patch, 49 + pgmDumpheaderSize),new CCEffectSender(19), effectType), 0,0,1,1,5);
        
        JPanel effOnOffPanel = new JPanel();
        effectsPanel.add(effOnOffPanel);
        addWidget(effOnOffPanel, new CheckBoxWidget("Enable FX", patch, new ScaledParamModel(patch, 52 + pgmDumpheaderSize), new CCSender(60, 127)),0,0,1,1,-3);
        addWidget(effOnOffPanel, new CheckBoxWidget("FX to D.I.", patch, new ScaledParamModel(patch, 2 + pgmDumpheaderSize), new CCSender(64, 127)),0,0,1,1,-4);
        
        effectsPanel.add(effParmsPanel);
        effectsPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Effects",TitledBorder.CENTER,TitledBorder.CENTER));
        parentPanel.add(effectsPanel);
    }
    
    private void createCards(JPanel parentPanel, Patch patch) {
        // Panel 0--OctaveDown
        JPanel panel0 = new JPanel();
        setupOctaveDownPanel(panel0, patch);
        
        // Panel 1--AnalogChorus
        JPanel panel1 = new JPanel();
        setupAnalogChorusPanel(panel1, patch);
        
        // Panel 2--Danish Chorus
        JPanel panel2 = new JPanel();
        setupDanishChorusPanel(panel2, patch);
        
        // Panel 3--Orange Phase
        JPanel panel3 = new JPanel();
        setupOPhasePanel(panel3, patch);
        
        // Panel 4--Gray Flanger
        JPanel panel4 = new JPanel();
        setupGrayFlangerPanel(panel4, patch);
        
        // Panel 5--Tron Down
        JPanel panel5 = new JPanel();
        setupTronDownPanel(panel5, patch);
        
        // Panel 6--Tron Up
        JPanel panel6 = new JPanel();
        setupTronUpPanel(panel6, patch);
        
        // Panel 7--Sample & Hold
        JPanel panel7 = new JPanel();
        setupSHPanel(panel7, patch);
        
        // Panel 7--S&H Flanger
        JPanel panel8 = new JPanel();
        setupSHFlangerPanel(panel8, patch);
        
        // Panel 9--S&H Driver
        JPanel panel9 = new JPanel();
        setupSHDriverPanel(panel9, patch);
        
        // Panel 10--Bass Synth
        JPanel panel10 = new JPanel();
        setupBassSynthPanel(panel10, patch);
        
        // Panel 11--Danish Driver, Large Pie, and Pig Foot
        JPanel panel11 = new JPanel();
        setupDanishDrvrPanel(panel11, patch);
        
        // Panel 12--Large Pie
        JPanel panel12 = new JPanel();
        setupLargePiePanel(panel12, patch);
        
        // Panel 13--Pig Foot
        JPanel panel13 = new JPanel();
        setupPigFootPanel(panel13, patch);
        
        // Panel 14--Rodent
        JPanel panel14 = new JPanel();
        setupRodentPanel(panel14, patch);
        
        // Panel 15--Bypass
        JPanel panel15 = new JPanel();
        
        
        parentPanel.add(panel0, effectType[0]);
        parentPanel.add(panel1, effectType[1]);
        parentPanel.add(panel2, effectType[2]);
        parentPanel.add(panel3, effectType[3]);
        parentPanel.add(panel4, effectType[4]);
        parentPanel.add(panel5, effectType[5]);
        parentPanel.add(panel6, effectType[6]);
        parentPanel.add(panel7, effectType[7]);
        parentPanel.add(panel8, effectType[8]);
        parentPanel.add(panel9, effectType[9]);
        parentPanel.add(panel10, effectType[10]);
        parentPanel.add(panel11, effectType[11]);
        parentPanel.add(panel12, effectType[12]);
        parentPanel.add(panel13, effectType[13]);
        parentPanel.add(panel14, effectType[14]);
        parentPanel.add(panel15, effectType[15]);
    }
    
    // Panel 0--OctaveDown
    private void setupOctaveDownPanel(JPanel parentPanel, Patch patch) {
        parentPanel.setLayout(new BoxLayout(parentPanel, BoxLayout.Y_AXIS));
        JPanel parmsPanel = new JPanel();
        parentPanel.add(parmsPanel);
        addWidget(parmsPanel,new KnobWidget("Mix", patch, 0, 127,0,new ScaledParamModel(patch,53 + pgmDumpheaderSize, 127, 63),new CCSender(29)),6,0,1,1,21);
    }
    
    // Panel 1--AnalogChorus
    private void setupAnalogChorusPanel(JPanel parentPanel, Patch patch) {
        parentPanel.setLayout(new BoxLayout(parentPanel, BoxLayout.Y_AXIS));
        JPanel parmsPanel = new JPanel();
        parentPanel.add(parmsPanel);
        addWidget(parmsPanel,new KnobWidget("Speed", patch, 0, 127,0,new ScaledParamModel(patch, 53 + pgmDumpheaderSize, 127, 63),new CCSender(30)),6,0,1,1,22);
        addWidget(parmsPanel,new KnobWidget("Depth", patch, 0, 127,0,new ScaledParamModel(patch, 54 + pgmDumpheaderSize, 127, 63),new CCSender(31)),6,0,1,1,23);
    }
    
    // Panel 2--Danish Chorus
    private void setupDanishChorusPanel(JPanel parentPanel, Patch patch) {
        parentPanel.setLayout(new BoxLayout(parentPanel, BoxLayout.Y_AXIS));
        JPanel parmsPanel = new JPanel();
        parentPanel.add(parmsPanel);
        addWidget(parmsPanel,new KnobWidget("Width",     patch, 0, 127,0,new ScaledParamModel(patch, 53 + pgmDumpheaderSize, 127, 63), new CCSender(34)),6,0,1,1,24);
        addWidget(parmsPanel,new KnobWidget("Speed",     patch, 0, 127,0,new ScaledParamModel(patch, 54 + pgmDumpheaderSize, 127, 63), new CCSender(32)),6,0,1,1,25);
        addWidget(parmsPanel,new KnobWidget("Intensity", patch, 0, 127,0,new ScaledParamModel(patch, 55 + pgmDumpheaderSize, 127, 63), new CCSender(33)),6,0,1,1,26);
    }
    
    // Panel 3--Orange Phase
    private void setupOPhasePanel(JPanel parentPanel, Patch patch) {
        parentPanel.setLayout(new BoxLayout(parentPanel, BoxLayout.Y_AXIS));
        JPanel parmsPanel = new JPanel();
        parentPanel.add(parmsPanel);
        addWidget(parmsPanel,new KnobWidget("Speed", patch, 0, 127,0,new ScaledParamModel(patch, 53 + pgmDumpheaderSize, 127, 63), new CCSender(35)),6,0,1,1,27);
    }
    
    // Panel 4--Gray Flanger
    private void setupGrayFlangerPanel(JPanel parentPanel, Patch patch) {
        parentPanel.setLayout(new BoxLayout(parentPanel, BoxLayout.Y_AXIS));
        JPanel parmsPanel = new JPanel();
        parentPanel.add(parmsPanel);
        addWidget(parmsPanel,new KnobWidget("Width",  patch, 0, 127,0,new ScaledParamModel(patch, 55 + pgmDumpheaderSize, 127, 63),new CCSender(36)),6,0,1,1,28);
        addWidget(parmsPanel,new KnobWidget("Speed",  patch, 0, 127,0,new ScaledParamModel(patch, 53 + pgmDumpheaderSize, 127, 63),new CCSender(37)),6,0,1,1,29);
        addWidget(parmsPanel,new KnobWidget("Regen",  patch, 0, 127,0,new ScaledParamModel(patch, 54 + pgmDumpheaderSize, 127, 63),new CCSender(38)),6,0,1,1,30);
    }
    
    // Panel 5--Tron Down
    private void setupTronDownPanel(JPanel parentPanel, Patch patch) {
        addWidget(parentPanel,new KnobWidget("Peak", patch, 0, 127,0,new ScaledParamModel(patch, 53 + pgmDumpheaderSize, 127, 63),new CCSender(39)),6,0,1,1,31);
    }
    
    // Panel 6--Tron Up
    private void setupTronUpPanel(JPanel parentPanel, Patch patch) {
        addWidget(parentPanel,new KnobWidget("Peak", patch, 0, 127,0,new ScaledParamModel(patch, 53 + pgmDumpheaderSize, 127, 63),new CCSender(40)),6,0,1,1,32);
    }
    
    // Panel 7--Sample & Hold
    private void setupSHPanel(JPanel parentPanel, Patch patch) {
        addWidget(parentPanel,new KnobWidget("Speed", patch, 0, 127,0,new ScaledParamModel(patch, 53 + pgmDumpheaderSize, 127, 63),new CCSender(41)),6,0,1,1,33);
    }
    
    // Panel 7--S&H Flanger
    private void setupSHFlangerPanel(JPanel parentPanel, Patch patch) {
        addWidget(parentPanel,new KnobWidget("Width",     patch, 0, 127,0,new ScaledParamModel(patch, 56 + pgmDumpheaderSize, 127, 63),new CCSender(36)),6,0,1,1,34);
        addWidget(parentPanel,new KnobWidget("Speed",     patch, 0, 127,0,new ScaledParamModel(patch, 53 + pgmDumpheaderSize, 127, 63),new CCSender(41)),6,0,1,1,35);
        addWidget(parentPanel,new KnobWidget("Fl. Speed", patch, 0, 127,0,new ScaledParamModel(patch, 54 + pgmDumpheaderSize, 127, 63),new CCSender(37)),6,0,1,1,36);
        addWidget(parentPanel,new KnobWidget("Regen",     patch, 0, 127,0,new ScaledParamModel(patch, 55 + pgmDumpheaderSize, 127, 63),new CCSender(38)),6,0,1,1,37);
    }
    
    // Panel 9--S&H Driver
    private void setupSHDriverPanel(JPanel parentPanel, Patch patch) {
        addWidget(parentPanel,new KnobWidget("Speed",      patch, 0, 127,0,new ScaledParamModel(patch, 53 + pgmDumpheaderSize, 127, 63), new CCSender(41)),6,0,1,1,38);
        addWidget(parentPanel,new KnobWidget("Distortion", patch, 0, 127,0,new ScaledParamModel(patch, 54 + pgmDumpheaderSize, 127, 127),new CCSender(55)),6,0,1,1,39);
    }
    
    // Panel 10--Bass Synth
    private void setupBassSynthPanel(JPanel parentPanel, Patch patch) {
        addWidget(parentPanel,new KnobWidget("Decay",    patch, 0, 127,0,new ScaledParamModel(patch, 53 + pgmDumpheaderSize, 127, 63),new CCSender(52)),6,0,1,1,40);
        addWidget(parentPanel,new KnobWidget("Dry Mix",  patch, 0, 127,0,new ScaledParamModel(patch, 54 + pgmDumpheaderSize, 127, 63),new CCSender(48)),6,0,1,1,41);
        addWidget(parentPanel,new KnobWidget("Lowpass",  patch, 0, 127,0,new ScaledParamModel(patch, 55 + pgmDumpheaderSize, 127, 63),new CCSender(49)),6,0,1,1,42);
        addWidget(parentPanel,new KnobWidget("Bandpass", patch, 0, 127,0,new ScaledParamModel(patch, 56 + pgmDumpheaderSize, 127, 63),new CCSender(50)),6,0,1,1,43);
    }
    
    // Panel 11--Danish Driver
    private void setupDanishDrvrPanel(JPanel parentPanel, Patch patch) {
        addWidget(parentPanel,new KnobWidget("Distortion", patch, 0, 127,0,new ScaledParamModel(patch, 53 + pgmDumpheaderSize, 127, 127),new CCSender(55)),6,0,1,1,44);
    }
    
    // Panel 12--Large Pie
    private void setupLargePiePanel(JPanel parentPanel, Patch patch) {
        addWidget(parentPanel,new KnobWidget("Distortion", patch, 0, 127,0,new ScaledParamModel(patch, 53 + pgmDumpheaderSize, 127, 127),new CCSender(56)),6,0,1,1,45);
    }
    
    // Panel 13--Pig Foot
    private void setupPigFootPanel(JPanel parentPanel, Patch patch) {
        addWidget(parentPanel,new KnobWidget("Distortion", patch, 0, 127,0,new ScaledParamModel(patch, 53 + pgmDumpheaderSize, 127, 127),new CCSender(57)),6,0,1,1,46);
    }
    
    // Panel 14--Rodent
    private void setupRodentPanel(JPanel parentPanel, Patch patch) {
        addWidget(parentPanel,new KnobWidget("Filter",     patch, 0, 127,0,new ScaledParamModel(patch, 54 + pgmDumpheaderSize, 127,  63),new CCSender(59)),6,0,1,1,47);
        addWidget(parentPanel,new KnobWidget("Distortion", patch, 0, 127,0,new ScaledParamModel(patch, 53 + pgmDumpheaderSize, 127, 127),new CCSender(58)),6,0,1,1,48);
    }
    
    // Panel 15--Bypass--no setup needed
    //>>----    
    //
    private void addParametricPanel(JPanel parentPanel, Patch patch) {
        JPanel parametricPanel = new JPanel();
        parametricPanel.setLayout(new BoxLayout(parametricPanel, BoxLayout.X_AXIS));
        parentPanel.add(parametricPanel);
        
        JPanel parmPanel = new JPanel();
        parmPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Parametric EQ",TitledBorder.CENTER,TitledBorder.CENTER));
        parmPanel.setLayout(new BoxLayout(parmPanel, BoxLayout.X_AXIS));
        parametricPanel.add(parmPanel);
        addWidget(parmPanel,new KnobWidget("Gain", patch, 0, 127,0,new ScaledParamModel(patch,15 + pgmDumpheaderSize, 127, 63),new CCSender(27)),6,0,1,1,49);
        addWidget(parmPanel,new KnobWidget("Q",    patch, 0, 127,0,new ScaledParamModel(patch,14 + pgmDumpheaderSize, 127, 63),new CCSender(26)),6,0,1,1,50);
        addWidget(parmPanel,new KnobWidget("Freq", patch, 0, 127,0,new ScaledParamModel(patch,13 + pgmDumpheaderSize, 127, 63),new CCSender(25)),6,0,1,1,51);
        
        JPanel loCutPanel = new JPanel();
        loCutPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"FX Lo-Cut Filter",TitledBorder.CENTER,TitledBorder.CENTER));
        loCutPanel.setLayout(new BoxLayout(loCutPanel, BoxLayout.Y_AXIS));
        parametricPanel.add(loCutPanel);
        addWidget(loCutPanel,new KnobWidget("Off   1kHz", patch, 0, 127,0,new ScaledParamModel(patch,51 + pgmDumpheaderSize, 127, 63),new CCSender(21)),6,0,1,1,52);
    }
    
    private void addCompressPanel(JPanel parentPanel, Patch patch) {
        JPanel compressPanel = new JPanel();
        compressPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Compression",TitledBorder.CENTER,TitledBorder.CENTER));
        compressPanel.setLayout(new BoxLayout(compressPanel, BoxLayout.Y_AXIS));
        parentPanel.add(compressPanel);
        
        JPanel btnPanel = new JPanel();
        compressPanel.add(btnPanel);
        addWidget(btnPanel, new ComboBoxWidget("Ratio",   patch, new ScaledParamModel(patch,25 + pgmDumpheaderSize, 4, 100), new CCSender(42, 25),new String[] {"2:1", "3.3:1(LA-2A)", "8:1", "12:1", "Inf:1"}), 0,0,1,1,6);
        
        JPanel parmPanel = new JPanel();
        compressPanel.add(parmPanel);
        addWidget(parmPanel,new KnobWidget("Lev/Thresh", patch, 0, 127, 0,new ScaledParamModel(patch, 11 + pgmDumpheaderSize, 127, 63),new CCSender(18)),6,0,1,1,53);
        addWidget(parmPanel,new KnobWidget("Attack",     patch, 0, 127, 0,new ScaledParamModel(patch, 28 + pgmDumpheaderSize, 127, 127),new CCSender(51)),6,0,1,1,54);
        addWidget(parmPanel,new KnobWidget("Release",    patch, 0, 127, 0,new ScaledParamModel(patch, 27 + pgmDumpheaderSize, 127, 127),new CCSender(63)),6,0,1,1,55);
    }
    
    // Classes and and methods to support selecting amp models and showing and hiding
    // various controls in the Amp Settings pane
    class CCAmpModelSender extends CCSender {
        public CCAmpModelSender(int param) {
            super(param);
        }
        
        public void send(IPatchDriver driver, int value) {
            setCtrlVisibility(value);            
            super.send(driver, value);
        }
    }

    class AmpModelModel extends ParamModel {
        public AmpModelModel(Patch p,int o) {
            super(p, o);
        }
        
        public void set(int i) {
            PatchBytes.setSysexByte(patch, 9, ofs, (byte)i);
            modelDesc.setText(ampModelDesc[i]);
        }
        
        public int get() {
            int i = (int)PatchBytes.getSysexByte(patch.sysex, 9, ofs);
            modelDesc.setText(ampModelDesc[i]);
            setCtrlVisibility(i);
            return i;
        }
    }
    
    private void setCtrlVisibility(int value) {
        final boolean[] midSweepKnobVisible = {
            //0    1      2      3      4     5      6      7
            false, true , false, false, true, false, true , true,
            //8    9      10     11     12     13     14     15
            true , false, false, false, false, false, false, true
        };
        
        midSweepKnob.setVisible(midSweepKnobVisible[value]);
    }
    
    // Classes and methods to support selecting cabinets
    class CCCabTypeSender extends CCSender {
        public CCCabTypeSender(int param) {
            super(param);
        }
        
        public void send(IPatchDriver driver, int value) {
            super.send(driver, Constants.CAB_TRANSLATE[value]);
        }
    }
    
    class CabTypeModel extends ParamModel {
        public CabTypeModel(Patch p,int o) {
            super(p, o);
        }
        
        public void set(int i) {
            PatchBytes.setSysexByte(patch, 9, ofs, (byte)Constants.CAB_TRANSLATE[i]);
        }
        
        public int get() {
            int i = (int)PatchBytes.getSysexByte(patch.sysex, 9, ofs);
            int j;
            for (j = 0; j < Constants.CAB_TRANSLATE.length; j++) {
                if (i == Constants.CAB_TRANSLATE[j]) {
                    break;
                }
            }
            return j;
        }
    }
    
    // Classes and methods to support selecting effects combos and displaying
    // the correct effects controls in the effects pane
    class CCEffectSender extends CCSender {
        public CCEffectSender(int param) {
            super(param);
        }
        
        public void send(IPatchDriver driver, int value) {
            setEffectPane(value);
            super.send(driver, effectTranslate[value]);
        }
    }

    class EffectModel extends ParamModel {
        public EffectModel(Patch p,int o) {
            super(p, o);
        }
        
        public void set(int i) {
            PatchBytes.setSysexByte(patch, 9, ofs, (byte)effectTranslate[i]);
        }
        
        public int get() {
            int i = (int)PatchBytes.getSysexByte(patch.sysex, 9, ofs);
            int j;
            for (j = 0; j < effectTranslate.length; j++) {
                if (i == effectTranslate[j]) {
                    break;
                }
            }
            setEffectPane(j);
            return j;
        }
    }
    
    private void setEffectPane(int value) {
        CardLayout cl = (CardLayout)(effParmsPanel.getLayout());
        cl.show(effParmsPanel, effectType[value]);
    }
}