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

package synthdrivers.Line6Pod20;

import core.*;
import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;

/** Line6 Program Patch Editor
* 
* @author Jeff Weber
*/
public class Line6Pod20SingleEditor extends PatchEditorFrame {
    /** Size of program patch header--9 bytes.
    */
    static final int pgmDumpheaderSize = Constants.PDMP_HDR_SIZE;
    
    /** Array of strings representing the names of all amp models for the Line6
    * Pod.
    */
    static final String[] ampModel = {
        "Tube Preamp", 
        "Line 6 Clean", 
        "Line 6 Crunch", 
        "Line 6 Drive", 
        "Line 6 Layer", 
        "Small Tweed", 
        "Tweed Blues", 
        "Black Panel", 
        "Modern Class A", 
        "Brit Class A", 
        "Brit Blues", 
        "Brit Classic", 
        "Brit Hi Gain", 
        "Rectified", 
        "Modern Hi Gain", 
        "Fuzz Box", 
        "Jazz Clean", 
        "Boutique 1", 
        "Boutique 2", 
        "Brit Class A 2", 
        "Brit Class A 3", 
        "Small Tweed 2", 
        "Black Panel 2", 
        "Boutique 3", 
        "California Crunch 1", 
        "California Crunch 2", 
        "Rectified 2", 
        "Modern Hi Gain 2", 
        "Line 6 Twang",
        "Line 6 Crunch 2",
        "Line 6 Blues",
        "Line 6 Insane"
    };
    
    /** Array of strings representing the descriptions of all amp models for
        * the Line6 Pod.
        */
    static final String[] ampModelDesc = {
        "Tube Instrument Preamp", 
        "Line 6 21st Century Clean", 
        "Line 6 Thick Grindage", 
        "Line 6 Industrial Strength Overdrive", 
        "Line 6 Clean meets Psychotic Drive", 
        "Õ52 Fender Deluxe", 
        "Õ59 Fender Bassman", 
        "Õ65 Fender Deluxe", 
        "Õ96 Matchless Chieftain", 
        "Õ63 Vox AC 30 with Top Boost", 
        "Õ65 Marshall JTM-45", 
        "Õ68 Marshall Plexi 50 watt", 
        "Õ90 Marshall JCM-800", 
        "Õ94 Mesa Boogie Dual Rectifier Tremoverb Combo", 
        "Õ89 Soldano X88R Preamp", 
        "Õ60Õs Dallas Arbiter Fuzz Face", 
        "Õ87 Roland JC-120", 
        "Dumble Overdrive Special Clean Channel", 
        "Dumble Overdrive Special Drive Channel", 
        "Õ60 Vox AC 30 non-Top Boost", 
        "Õ60 Vox AC 15", 
        "Õ60 Tweed Fender Champ", 
        "Õ65 Blackface Fender Twin", 
        "Budda Twinmaster head", 
        "Õ85 Mesa Boogie Mk IIc+ Clean Channel", 
        "Õ85 Mesa Boogie Mk IIc+ Drive Channel", 
        "Õ95 Mesa Boogie Dual Rectifier Head", 
        "Õ89 Soldano SLO Super Lead Overdrive", 
        "Deluxe + Bassman, w/ more tone control range",
        "Õ68 Plexi w/ more tone control range",
        "Marshall JTM-45 + Budda Twinmaster + Line 6",
        "Way too many hours of shredding"
    };
    
    /** Array of strings representing the descriptions of all cabinet models
        * for the Line6 Pod.
        */
    static final String[] cabType = {
        "1x8 '60 Fender Tweed Champ",
        "1x12 '52 Fender Tweed Deluxe",
        "1x12 '60 Vox AC15",
        "1x12 '64 Fender Blackface Deluxe",
        "1x12 '98 Line6 Flextone",
        "2x12 '65 Fender Blackface Twin",
        "2x12 '67 VOX AC30",
        "2x12 '65 Matchless Chieftain",
        "2x12 '98 Line 6 Custom 2x12",
        "4x10 '59 Fender Bassman",
        "4x10 '98 Line 6 Custom 4x10",
        "4x12 '96 Marshall w/ Vintage 30s",
        "4x12 '78 Marshall w/ stock 70s",
        "'68 Marshall w/ Greenbacks",
        "4x12 '98 Line 6 Custom 4x12",
        "No Cabinet Emulation"
    };
    
    /** Array of strings representing the descriptions of all effect models for
        * the Line6 Pod.
        */
    static final String[] effectType = {
        "Compressor",
        "Tremolo",
        "Chorus 1",
        "Chorus 2",
        "Flanger 1",
        "Flanger 2",
        "Rotary Speaker",
        "Delay",
        "Delay/Compressor",
        "Delay/Chorus 1",
        "Delay/Chorus 2",
        "Delay/Flanger 1",
        "Delay/Flanger 2",
        "Delay/Tremolo",
        "Delay/Swell",
        "Bypass"
    }; 
    
    /** Array of integers representing the mapping of effect models to CC
        * message values. 
        */
    static final int[] effectTranslate = {
        11, 9, 8, 0, 1, 3, 2, 6, 7, 4, 12, 13, 15, 5, 14, 10
    };
    
    /** Array of integers representing the mapping of effect models to their
        * respective panels within the editor. 
        */
    static final int[] effPaneAssignment = {
        1, 2, 3, 3, 4, 4, 5, 6, 1, 3, 3, 4, 4, 2, 7, 6
    };
    
    /** Array of strings representing the two possible locations of the volume
        * pedal (pre-amp or post amp).
        */    
    static final String[] volPedLoc = {
        "Pre-tube",
        "Post-Tube"
    };
    
    /** Array of strings representing the names of all the effects panels.
        */    
    static final String[] effParmsPane = {
        "Compressor",
        "Tremolo",
        "Chorus",
        "Flanger",
        "Rotary",
        "Empty",
        "Swell"
    };
    
    /** A reference to the edit panel object. */
    private JPanel line6EditPanel;

    /** A reference to the left edit panel object. */
    private JPanel leftEditPanel;

    /** A reference to the right edit panel object. */
    private JPanel rightEditPanel;

    /** A reference to the effect parameters edit panel object. */
    private JPanel effParmsPanel;
    
    /** A JLabel used to display the currently selected amp model.*/
    private JLabel modelDesc;
    
    /** A reference to the combo box used to select the amp model.*/
    private ComboBoxWidget patchSelector;

    /** A reference to the drive 2 knob widget which is only shown for the
        * Line6 Layer amp model.
        */
    private HideableKnobWidget drive2Knob;

    /** A reference to the presence knob widget which is hidden for some amp 
        * models.
        */
    private HideableKnobWidget presenceKnob;

    /** A reference to the mid bright swithc  check box widget which is hidden
        * for some amp models.
        */
    private CheckBoxWidget brightSwitch;
    
    /** Constructs a Line6Pod20SingleEditor for the selected patch.*/
    Line6Pod20SingleEditor(Patch patch)
    {
        super ("Line 6 POD 2.0 Single Editor",patch);   
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
        addReverbPanel(rightEditPanel, patch);
        addDelayPanel(rightEditPanel, patch);
        
        pack();
        show();
    }
    
    /** Adds the patch name panel to the left edit panel.
        */
    private void addPatchNamePanel(JPanel parentPanel, Patch patch) {
        JPanel patchNamePanel = new JPanel();
        parentPanel.add(patchNamePanel);
        addWidget(patchNamePanel, new PatchNameWidget("Program Name ", patch),0,0,1,1,1);
    }
    
    /** Adds the amp select panel. */
    private void addAmpPane(JPanel parentPanel, Patch patch) {
        JPanel modelPane = new JPanel();
        modelPane.setLayout(new BoxLayout(modelPane, BoxLayout.Y_AXIS));
        parentPanel.add(modelPane);
        
        JPanel patchSelPane = new JPanel();
        patchSelPane.setLayout(new BoxLayout(patchSelPane, BoxLayout.Y_AXIS));
        modelPane.add(patchSelPane);
        
        JPanel speakerPane = new JPanel();
        modelPane.add(speakerPane);
        addWidget(speakerPane,new ComboBoxWidget("Cabinet Type", patch, new ScaledParamModel(patch, 44 + pgmDumpheaderSize),new CCSender(71), cabType), 0,0,1,1,2);
        addWidget(speakerPane,new     KnobWidget("Air", patch, 0, 127,0, new ScaledParamModel(patch, 45 + pgmDumpheaderSize, 127, 63),new CCSender(72)),0,0,1,1,9);
        modelPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Amp Model",TitledBorder.CENTER,TitledBorder.CENTER));
        
        JPanel ampPane=new JPanel();
        ampPane.setLayout(new BoxLayout(ampPane, BoxLayout.Y_AXIS));
        parentPanel.add(ampPane,gbc); 
        
        JPanel ampKnobsPane = new JPanel();
        ampPane.add(ampKnobsPane);
        addWidget(ampKnobsPane,new KnobWidget("Drive",  patch, 0, 127,0,new ScaledParamModel(patch, 9 + pgmDumpheaderSize, 127, 63),new CCSender(13)),0,0,1,1,10);
        
        drive2Knob = new HideableKnobWidget("Drive 2",  patch, 0, 127,0,new ScaledParamModel(patch,10 + pgmDumpheaderSize, 127, 63),new CCSender(20));
        addWidget(ampKnobsPane,drive2Knob,1,0,1,1,11);
        
        addWidget(ampKnobsPane,new KnobWidget("Bass",     patch, 0, 127,0,new ScaledParamModel(patch,11 + pgmDumpheaderSize, 127, 63),new CCSender(14)),2,0,1,1,12);
        addWidget(ampKnobsPane,new KnobWidget("Mid",      patch, 0, 127,0,new ScaledParamModel(patch,12 + pgmDumpheaderSize, 127, 63),new CCSender(15)),3,0,1,1,13);
        addWidget(ampKnobsPane,new KnobWidget("Treble",   patch, 0, 127,0,new ScaledParamModel(patch,13 + pgmDumpheaderSize, 127, 63),new CCSender(16)),4,0,1,1,14);
        
        presenceKnob = new HideableKnobWidget("Presence", patch, 0, 127,0,new ScaledParamModel(patch,14 + pgmDumpheaderSize, 127, 63),new CCSender(21));
        addWidget(ampKnobsPane,presenceKnob,5,0,1,1,15);
        
        addWidget(ampKnobsPane,new KnobWidget("Volume",   patch, 0, 127,0,new ScaledParamModel(patch,15 + pgmDumpheaderSize, 127, 63),new CCSender(17)),6,0,1,1,16);
        
        JPanel ampBtnsPane = new JPanel();
        ampPane.add(ampBtnsPane);
        addWidget(ampBtnsPane, new CheckBoxWidget("Drive Boost", patch, new ScaledParamModel(patch, 1 + pgmDumpheaderSize), new CCSender(26, 127)),0,0,1,1,-1);
        
        brightSwitch = new CheckBoxWidget("Bright",      patch, new ScaledParamModel(patch, 7 + pgmDumpheaderSize), new CCSender(73, 127));
        addWidget(ampBtnsPane, brightSwitch,0,0,1,1,-2);
        
        addWidget(ampBtnsPane, new CheckBoxWidget("Distortion",  patch, new ScaledParamModel(patch, 0 + pgmDumpheaderSize), new CCSender(25, 127)),0,0,1,1,-3);
        addWidget(ampBtnsPane, new CheckBoxWidget("Presence",    patch, new ScaledParamModel(patch, 2 + pgmDumpheaderSize), new CCSender(27, 127)),0,0,1,1,-4);
        
        JPanel modelDescPanel = new JPanel();
        modelDesc = new JLabel();
        modelDescPanel.add(modelDesc);
        patchSelector = new ComboBoxWidget("Amp Model",    patch, new AmpModelModel(patch, 8 + pgmDumpheaderSize),new CCAmpModelSender(12), ampModel);
        addWidget(patchSelPane, patchSelector, 0,0,1,1,3);
        patchSelPane.add(modelDescPanel);
        
        ampPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Preamp",TitledBorder.CENTER,TitledBorder.CENTER));
    }
    
    /** Adds the stompbox panel.*/
    private void addStompBoxPane(JPanel parentPanel, Patch patch) {
        JPanel stompBoxPane=new JPanel();
        stompBoxPane.setLayout(new BoxLayout(stompBoxPane, BoxLayout.X_AXIS));
        parentPanel.add(stompBoxPane,gbc);
        
        JPanel gatePane=new JPanel();
        gatePane.setLayout(new BoxLayout(gatePane, BoxLayout.Y_AXIS));
        stompBoxPane.add(gatePane);
        addWidget(gatePane, new CheckBoxWidget("Noise Gate", patch, new ScaledParamModel(patch, 6 + pgmDumpheaderSize), new CCSender(22, 127)),0,0,1,1,-5);
        JPanel gateKnobsPane=new JPanel();
        gatePane.add(gateKnobsPane);
        addWidget(gateKnobsPane,new KnobWidget("Threshold", patch, 0, 127,0,new ScaledParamModel(patch,16 + pgmDumpheaderSize, 127, 96),new CCSender(23, true)),0,0,1,1,17);
        addWidget(gateKnobsPane,new KnobWidget("Decay",     patch, 0, 127,0,new ScaledParamModel(patch,17 + pgmDumpheaderSize, 127, 63),new CCSender(24)),1,0,1,1,18);
        gatePane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Noise Gate",TitledBorder.CENTER,TitledBorder.CENTER));
        
        JPanel wahPane = new JPanel();
        wahPane.setLayout(new BoxLayout(wahPane, BoxLayout.Y_AXIS));
        stompBoxPane.add(wahPane);
        addWidget(wahPane, new CheckBoxWidget("Global Wah Wah", patch, new NullModel(), new CCSender(43, 127)),0,0,1,1,-6);
        JPanel wahKnobsPane=new JPanel();
        wahPane.add(wahKnobsPane);
        addWidget(wahKnobsPane,new KnobWidget("Bottom",    patch, 0, 127,0,new ScaledParamModel(patch,19 + pgmDumpheaderSize),new CCSender(44)),2,0,1,1,19);
        addWidget(wahKnobsPane,new KnobWidget("Top",       patch, 0, 127,0,new ScaledParamModel(patch,20 + pgmDumpheaderSize),new CCSender(45)),3,0,1,1,20);
        addWidget(wahKnobsPane,new KnobWidget("Current",   patch, 0, 127,0,new ScaledParamModel(patch,18 + pgmDumpheaderSize),new CCSender(4)),4,0,1,1,21);
        wahPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Wah Wah",TitledBorder.CENTER,TitledBorder.CENTER));
        
        JPanel volPedPane = new JPanel();
        volPedPane.setLayout(new BoxLayout(volPedPane, BoxLayout.Y_AXIS));
        stompBoxPane.add(volPedPane);
        addWidget(volPedPane, new ComboBoxWidget("Vol Pedal", patch, new ScaledParamModel(patch,24 + pgmDumpheaderSize),new CCSender(47, 127),volPedLoc), 0,0,1,1,4);
        JPanel volPedKnobsPane=new JPanel();
        volPedPane.add(volPedKnobsPane);
        addWidget(volPedKnobsPane,new KnobWidget("Minimum",   patch, 0, 127,0,new ScaledParamModel(patch,23 + pgmDumpheaderSize),new CCSender(46)),5,0,1,1,22);
        addWidget(volPedKnobsPane,new KnobWidget("Current",   patch, 0, 127,0,new ScaledParamModel(patch,22 + pgmDumpheaderSize),new CCSender(7)),6,0,1,1,23);
        volPedPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Volume Pedal",TitledBorder.CENTER,TitledBorder.CENTER));
    }
    
    /** Adds the effects panel.*/
    private void addEffectsPanel(JPanel parentPanel, Patch patch) {
        JPanel effectsPanel = new JPanel();
        effectsPanel.setLayout(new BoxLayout(effectsPanel, BoxLayout.Y_AXIS));
        effParmsPanel = new JPanel(new CardLayout());
        createCards(effParmsPanel, patch);
        addWidget(effectsPanel,new ComboBoxWidget("Effects", patch, new EffectModel(patch, 46 + pgmDumpheaderSize),new CCEffectSender(19), effectType), 0,0,1,1,5);
        effectsPanel.add(effParmsPanel);
        effectsPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Effects",TitledBorder.CENTER,TitledBorder.CENTER));
        parentPanel.add(effectsPanel);
    }
    
    /** Creates the different panels for each of the effects configurations.*/
    private void createCards(JPanel parentPanel, Patch patch) {
        // Panel 1--Compressor
        JPanel panel1 = new JPanel();
        setupCompressorPanel(panel1, patch);
        
        // Panel 2--Tremolo
        JPanel panel2 = new JPanel();
        setupTremeloPanel(panel2, patch);
        
        // Panel 3--Chorus
        JPanel panel3 = new JPanel();
        setupChorusPanel(panel3, patch);
        
        // Panel 4--Flanger
        JPanel panel4 = new JPanel();
        setupFlangerPanel(panel4, patch);
        
        // Panel 5--Rotary
        JPanel panel5 = new JPanel();
        setupRotaryPanel(panel5, patch);
        
        // Panel 6--Delay & Bypass
        JPanel panel6 = new JPanel();
        //  JLabel aLabel6 = new JLabel(effParmsPane[5] + "--6");
        //  panel6.add(aLabel6);
        
        // Panel 7--Swell
        JPanel panel7 = new JPanel();
        setupSwellPanel(panel7, patch);
        
        parentPanel.add(panel1, effParmsPane[0]);
        parentPanel.add(panel2, effParmsPane[1]);
        parentPanel.add(panel3, effParmsPane[2]);
        parentPanel.add(panel4, effParmsPane[3]);
        parentPanel.add(panel5, effParmsPane[4]);
        parentPanel.add(panel6, effParmsPane[5]);
        parentPanel.add(panel7, effParmsPane[6]);
    }
    
    /** Sets up the compressor panel.*/
    private void setupCompressorPanel(JPanel parentPanel, Patch patch) {
        parentPanel.setLayout(new BoxLayout(parentPanel, BoxLayout.Y_AXIS));
        JPanel btnPanel = new JPanel();
        parentPanel.add(btnPanel);
        addWidget(btnPanel, new ComboBoxWidget("Compression Ratio", patch, new ScaledParamModel(patch,48 + pgmDumpheaderSize),new CCSender(42, 25),new String[] {"None", "1.4:1", "2:1", "3:1", "6:1", "Inf:1"}), 0,0,1,1,6);
    }
    
    /** Sets up the tremelo panel.*/
    private void setupTremeloPanel(JPanel parentPanel, Patch patch) {
        parentPanel.setLayout(new BoxLayout(parentPanel, BoxLayout.Y_AXIS));
        JPanel btnPanel = new JPanel();
        parentPanel.add(btnPanel);
        addWidget(btnPanel, new CheckBoxWidget("Tremolo Enable", patch, new ScaledParamModel(patch, 4 + pgmDumpheaderSize), new CCSender(50, 127)),0,0,1,1,-7);
        JPanel parmsPanel = new JPanel();
        parentPanel.add(parmsPanel);
        addWidget(parmsPanel,new KnobWidget("Speed", patch, 6, 127, 0, new ScaledDblParamModel(patch,48 + pgmDumpheaderSize, 6, 127, 150, 3175, true),new CCSender(58, true, 6)),6,0,1,1,24);
        addWidget(parmsPanel,new KnobWidget("Depth", patch, 0, 127, 0,    new ScaledParamModel(patch,50 + pgmDumpheaderSize),new CCSender(59)),6,0,1,1,25);
    }
    
    /** Sets up the chorus panel.*/
    private void setupChorusPanel(JPanel parentPanel, Patch patch) {
        parentPanel.setLayout(new BoxLayout(parentPanel, BoxLayout.Y_AXIS));
        JPanel btnPanel = new JPanel();
        parentPanel.add(btnPanel);
        addWidget(btnPanel, new CheckBoxWidget("Chorus Enable", patch, new ScaledParamModel(patch, 4 + pgmDumpheaderSize), new CCSender(50, 127)),0,0,1,1,-8);
        JPanel parmsPanel = new JPanel();
        parentPanel.add(parmsPanel);
        addWidget(parmsPanel,new KnobWidget("Predelay", patch, 0, 127,0,new ScaledDblParamModel(patch,53 + pgmDumpheaderSize, 127, 774),        new CCSender(54)),6,0,1,1,26);
        addWidget(parmsPanel,new KnobWidget("Speed",    patch, 4, 127,0,new ScaledDblParamModel(patch,48 + pgmDumpheaderSize, 4, 127, 200, 6350, true), new CCSender(51, true, 4)),6,0,1,1,27);
        addWidget(parmsPanel,new KnobWidget("Depth",    patch, 0, 127,0,new ScaledDblParamModel(patch,50 + pgmDumpheaderSize, 127, 312),       new CCSender(52)),6,0,1,1,28);
        addWidget(parmsPanel,new KnobWidget("Feedback", patch, 0, 127,0,   new ScaledParamModel(patch,52 + pgmDumpheaderSize),                 new CCSender(53)),6,0,1,1,29);
    }
    
    /** Sets up the flanger panel.*/
    private void setupFlangerPanel(JPanel parentPanel, Patch patch) {
        parentPanel.setLayout(new BoxLayout(parentPanel, BoxLayout.Y_AXIS));
        JPanel btnPanel = new JPanel();
        parentPanel.add(btnPanel);
        addWidget(btnPanel, new CheckBoxWidget("Flanger Enable", patch, new ScaledParamModel(patch, 4 + pgmDumpheaderSize), new CCSender(50, 127)),0,0,1,1,-9);
        JPanel parmsPanel = new JPanel();
        parentPanel.add(parmsPanel);
        addWidget(parmsPanel,new KnobWidget("Predelay", patch, 0, 127,0,new ScaledDblParamModel(patch,53 + pgmDumpheaderSize, 127, 774),        new CCSender(54)),6,0,1,1,30);
        addWidget(parmsPanel,new KnobWidget("Speed",    patch, 4, 127,0,new ScaledDblParamModel(patch,48 + pgmDumpheaderSize, 4, 127, 200, 6350, true), new CCSender(51, true, 4)),6,0,1,1,31);
        addWidget(parmsPanel,new KnobWidget("Depth",    patch, 0, 127,0,new ScaledDblParamModel(patch,50 + pgmDumpheaderSize, 127, 312),       new CCSender(52)),6,0,1,1,32);
        addWidget(parmsPanel,new KnobWidget("Feedback", patch, 0, 127,0,   new ScaledParamModel(patch,52 + pgmDumpheaderSize),                 new CCSender(53)),6,0,1,1,33);
    }
    
    private void setupRotaryPanel(JPanel parentPanel, Patch patch) {
        parentPanel.setLayout(new BoxLayout(parentPanel, BoxLayout.Y_AXIS));
        JPanel btnPanel = new JPanel();
        parentPanel.add(btnPanel);
        addWidget(btnPanel, new ComboBoxWidget("Rotor Speed", patch, new ScaledParamModel(patch,48 + pgmDumpheaderSize),new CCSender(55, 127),new String[] {"Slow", "Fast"}), 0,0,1,1,7);
        addWidget(btnPanel, new CheckBoxWidget("Rotary Enable", patch, new ScaledParamModel(patch, 4 + pgmDumpheaderSize), new CCSender(50, 127)),0,0,1,1,-10);
        JPanel parmsPanel = new JPanel();
        parentPanel.add(parmsPanel);
        JPanel speedPanel = new JPanel();
        speedPanel.setLayout(new BoxLayout(speedPanel, BoxLayout.Y_AXIS));
        parmsPanel.add(speedPanel);
        addWidget(parmsPanel,new KnobWidget("Slow",  patch, 0, 127,0,new ScaledDblParamModel(patch,51 + pgmDumpheaderSize, 0, 127, 100, 2894, true),new CCSender(57, true)),6,0,1,1,34);
        addWidget(parmsPanel,new KnobWidget("Fast",  patch, 0, 127,0,new ScaledDblParamModel(patch,49 + pgmDumpheaderSize, 0, 127, 100, 2894, true),new CCSender(56, true)),6,0,1,1,35);
        addWidget(parmsPanel,new KnobWidget("Depth", patch, 0, 127,0,new ScaledParamModel(patch,47 + pgmDumpheaderSize, 127, 63),new CCSender(1)),6,0,1,1,36);
    }
    
    /** Sets up the swell panel.*/
    private void setupSwellPanel(JPanel parentPanel, Patch patch) {
        addWidget(parentPanel,new KnobWidget("Attack Time", patch, 0, 127,0,new ScaledParamModel(patch,48 + pgmDumpheaderSize, 127, 63),new CCSender(49)),6,0,1,1,37);
    }
    
    /** Sets up the reverb panel.*/
    private void addReverbPanel(JPanel parentPanel, Patch patch) {
        JPanel reverbPanel = new JPanel();
        reverbPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Reverb",TitledBorder.CENTER,TitledBorder.CENTER));
        reverbPanel.setLayout(new BoxLayout(reverbPanel, BoxLayout.Y_AXIS));
        parentPanel.add(reverbPanel);
        
        JPanel btnPanel = new JPanel();
        reverbPanel.add(btnPanel);
        addWidget(btnPanel, new ComboBoxWidget("Reverb Type",   patch, new ScaledParamModel(patch,38 + pgmDumpheaderSize), new CCSender(37, 127),new String[] {"Spring", "Hall"}), 0,0,1,1,8);
        addWidget(btnPanel, new CheckBoxWidget("Reverb Enable", patch, new ScaledParamModel(patch, 5 + pgmDumpheaderSize), new CCSender(36, 127)),0,0,1,1,-11);
        
        JPanel parmPanel = new JPanel();
        reverbPanel.add(parmPanel);
        addWidget(parmPanel,new KnobWidget("Density", patch, 0, 127, 0,new ScaledParamModel(patch,42 + pgmDumpheaderSize, 127, 63),new CCSender(41)),6,0,1,1,38);
        addWidget(parmPanel,new KnobWidget("Diffuse", patch, 0, 127, 0,new ScaledParamModel(patch,41 + pgmDumpheaderSize, 127, 63),new CCSender(40)),6,0,1,1,29);
        addWidget(parmPanel,new KnobWidget("Tone",    patch, 0, 127, 0,new ScaledParamModel(patch,40 + pgmDumpheaderSize, 127, 63),new CCSender(39)),6,0,1,1,40);
        addWidget(parmPanel,new KnobWidget("Decay",   patch, 0, 127, 0,new ScaledParamModel(patch,39 + pgmDumpheaderSize, 127, 63),new CCSender(38)),6,0,1,1,41);
        addWidget(parmPanel,new KnobWidget("Level",   patch, 0, 127, 0,new ScaledParamModel(patch,43 + pgmDumpheaderSize, 127, 63),new CCSender(18)),6,0,1,1,42);
    }
    
    /** Sets up the delay panel.*/
    private void addDelayPanel(JPanel parentPanel, Patch patch) {
        JPanel delayPanel = new JPanel();
        delayPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Delay",TitledBorder.CENTER,TitledBorder.CENTER));
        delayPanel.setLayout(new BoxLayout(delayPanel, BoxLayout.Y_AXIS));
        parentPanel.add(delayPanel);
        
        JPanel btnPanel = new JPanel();
        delayPanel.add(btnPanel);
        addWidget(btnPanel, new CheckBoxWidget("Delay Enable", patch, new ScaledParamModel(patch, 3 + pgmDumpheaderSize), new CCSender(28, 127)),0,0,1,1,-12);
        
        JPanel parmPanel = new JPanel();
        delayPanel.add(parmPanel);
        addWidget(parmPanel,new KnobWidget("Coarse",   patch, 0, 127,0,new DelayCoarseSpeedModel(patch,27 + pgmDumpheaderSize, 127, 381),new CCSender(30)),6,0,1,1,43);
        addWidget(parmPanel,new KnobWidget("Fine",     patch, 0, 127,0,new DelayFineSpeedModel(patch,29 + pgmDumpheaderSize, 127, 762),new CCSender(62)),6,0,1,1,44);
        addWidget(parmPanel,new KnobWidget("Feedback", patch, 0, 127,0,new ScaledParamModel(patch,34 + pgmDumpheaderSize, 127, 63),new CCSender(32)),6,0,1,1,45);
        addWidget(parmPanel,new KnobWidget("Level",    patch, 0, 127,0,new ScaledParamModel(patch,36 + pgmDumpheaderSize, 127, 63),new CCSender(34)),6,0,1,1,46);
    }
    
    /** CCSender class for amp model. The behavior is the same as the CCSender
        *  class except that the send method will also call the setCtrlVisibility
        *  method, which determines if the mid sweep knob should be visible or not.
        */
    class CCAmpModelSender extends CCSender {
        /** Constructs a CCAmpModelSender. */
        private CCAmpModelSender(int param) {
            super(param);
        }
        
        /** Sends the CC message for the amp model and calls the setCtrlVisibility
        * method, which determines if the mid sweep knob should be visible or not.
        */
        public void send(IPatchDriver driver, int value) {
            setCtrlVisibility(value);            
            super.send(driver, value);
        }
    }
    
    /** ParamModel class for amp model. Gets and sets the amp model value in the
        * sysex record. Also the get method calls the setCtrlVisibility method,
        * which determines if the mid sweep knob should be visible or not.
        */
    class AmpModelModel extends ParamModel {
        /** Constructs an AmpModelModel. */
        private AmpModelModel(Patch p,int o) {
            super(p, o);
        }
        
        /** Updates the sysex record for the amp model parameter.
        */
        public void set(int i) {
            PatchBytes.setSysexByte(patch, 9, ofs, (byte)i);
            modelDesc.setText(ampModelDesc[i]);
        }
        
        /** Gets the amp model parameter value from the sysex record and calls
        * the setCtrlVisibility method, which determines if the mid sweep knob
        * should be visible or not.
        */
        public int get() {
            int i = (int)PatchBytes.getSysexByte(patch.sysex, 9, ofs);
            modelDesc.setText(ampModelDesc[i]);
            setCtrlVisibility(i);
            return i;
        }
    }
    
    /** Sets the drive 2 knob, the presence knob, and the bright switch to
        * visible or not, depending upon the amp model
        * selected by the user.
        */
    private void setCtrlVisibility(int value) {
        boolean[] drive2KnobVisible = {
            //0    1      2      3      4     5      6      7
            false, false, false, false, true, false, false, false,
            //8    9      10     11     12     13     14     15
            false, false, false, false, false, false, false, false,
            //16   17     18     19     20     21     22     23
            false, false, false, false, false, false, false, false,
            //24   25     26     27     28     29     30     31
            false, false, false, false, false, false, false, false
        };
        
        boolean[] presenceKnobVisible = {
            //0    1      2      3      4     5      6      7
            true,  true,  true,  true,  true, false, true , false,
            //8    9      10     11     12     13     14     15
            true , false, true,  true,  true,  true,  false, true,
            //16   17     18     19     20     21     22     23
            true,  true,  false, false, false, false, false, true,
            //24   25     26     27     28     29     30     31
            true,  true,  true,  true,  true,  true,  true,  true
        };
        
        boolean[] brightSwitchVisible = {
            //0    1      2      3      4     5      6      7
            false, true,  true,  true,  true, false, false, false,
            //8    9      10     11     12     13     14     15
            false, false, true,  false, false, false, false, false,
            //16   17     18     19     20     21     22     23
            true,  false, false, false, false, false, true,  false,
            //24   25     26     27     28     29     30     31
            true,  false, false, false, false, false, false, false
        };
        
        drive2Knob.setVisible(drive2KnobVisible[value]);
        presenceKnob.setVisible(presenceKnobVisible[value]);
        brightSwitch.setVisible(brightSwitchVisible[value]);
    }
    
    /** CCSender class for effect model. Translates the value sent by the 
        * comboboxWidget to the CC numbers for the effect parameter.
        */
    class CCEffectSender extends CCSender {
        /** Constructs a CCEffectSender. */
        private CCEffectSender(int param) {
            super(param);
        }
        
        /** Translates the value sent by the comboBoxWidget to the CC parameter
        * for the effect model and sends the CC message.
        */
        public void send(IPatchDriver driver, int value) {
            setEffectPane(value);
            super.send(driver, effectTranslate[value]);
        }
    }
    
    /** ParamModel class for effect model. Gets and sets the effect model
        * value in the sysex record. Also handles the translation of the values
        * between the CC values and the comboBoxWidget.
        */
    class EffectModel extends ParamModel {
        /** Constructs a EffectModel. */
        private EffectModel(Patch p,int o) {
            super(p, o);
        }
        
        /** Updates the sysex record for the effect model parameter. Converts
        * the comboBoxWidget value to a valid effect model CC value.
        */
        public void set(int i) {
//            super.set(effectTranslate[i]); 
            PatchBytes.setSysexByte(patch, 9, ofs, (byte)effectTranslate[i]);
        }
        
        /** Gets the effect type value from the sysex record and translates it
        * to the comboBoxWidget value.
        */
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
    
    /** Displays the effects panel for the selected effect. */
    private void setEffectPane(int value) {
        CardLayout cl = (CardLayout)(effParmsPanel.getLayout());
        cl.show(effParmsPanel, effParmsPane[effPaneAssignment[value]-1]);
    }
}