/*
 *  WaldorfPulseSingleDriver.java
 *
 *  Copyright (c) Scott Shedden, 2004
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */
 
package synthdrivers.WaldorfPulse;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import core.CheckBoxWidget;
import core.ComboBoxWidget;
import core.EnvelopeWidget;
import core.ParamModel;
import core.Patch;
import core.PatchEditorFrame;
import core.ScrollBarLookupWidget;
import core.ScrollBarWidget;
import core.SysexSender;
import core.SysexWidget;

class WaldorfPulseSingleEditor extends PatchEditorFrame
{
    private ImageIcon plus;
    private GridBagConstraints column_gbc;
    private GridBagConstraints page_gbc;
    private int id;
    private int button_id;
    private static String modSources[] = {
        "Off",
        "LFO1",
        "LFO1 \u00d7 Modwheel",
        "LFO1 \u00d7 Aftertouch",
        "LFO2",
        "LFO2 \u00d7 Envelope",
        "Envelope 1",
        "Envelope 2",
        "Velocity",
        "Keytrack",
        "Pitch follow",
        "Pitchbend",
        "Modwheel",
        "Aftertouch",
        "Breath Control",
        "Control X"
    };
    private static String modDestinations[] = {
        "Pitch",
        "Oscillator 1 Pitch",
        "Oscillator 2 Pitch",
        "Oscillator 3 Pitch",
        "Pulsewidth 1",
        "Pulsewidth 2",
        "Oscillator 1 Level",
        "Oscillator 2 Level",
        "Oscillator 3 Level",
        "Noise Level",
        "Cutoff",
        "Resonance",
        "Volume",
        "Panning",
        "LFO 1 Speed",
        "Mod 1 Amount",
    };

    public WaldorfPulseSingleEditor (Patch patch)
    {
        super("Waldorf Pulse/Pulse+ Single Editor", patch);
        
        // Initialise attibutes
        plus = new ImageIcon("synthdrivers/WaldorfPulse/plus.png");
        page_gbc = new GridBagConstraints();
        page_gbc.fill = GridBagConstraints.BOTH;
        page_gbc.anchor = GridBagConstraints.NORTHWEST;
        page_gbc.weightx = 1;
        page_gbc.weighty = 1;
        column_gbc = (GridBagConstraints)page_gbc.clone();
        column_gbc.insets = new Insets(4, 4, 4, 4);
        id = 1;
        button_id = -1;

        initTab1(patch);

        show();
    }


    private void initTab1(Patch patch)
    {
        // Column 1 Pane
        {
            JPanel column1Pane = new JPanel();
            column1Pane.setLayout(new GridBagLayout());
            // Oscillator 1 Pane
            {
                gbc.weightx = 5;
                JPanel osc1Pane = new JPanel();
                osc1Pane.setLayout(new GridBagLayout());
                addWidget(osc1Pane,
                          new ScrollBarWidget("Semitone",
                                              patch,
                                              16,  // min
                                              112, // max
                                              -64, // display value offset
                                              new ParamModel(patch, 6),
                                              new WaldorfPulseSender(32)),
                          0, // grid x
                          0, // grid y
                          1, // grid width
                          1, // grid height
                          id++);
                addWidget(osc1Pane,
                          new ScrollBarWidget("Tune",
                                              patch,
                                              0,   // min
                                              63,  // max
                                              -32, // display value offset
                                              new TuneParamModel(patch, 7),
                                              new WaldorfPulseSender(33, 2)),
                          0, // grid x
                          1, // grid y
                          1, // grid width
                          1, // grid height
                          id++);
                addWidget(osc1Pane,
                          new ComboBoxWidget("Shape",
                                              patch,
                                              new ParamModel(patch, 8),
                                              new WaldorfPulseSender(34),
                                              new String [] {"Pulse",
                                                             "Sawtooth",
                                                             "Triangle"}),
                          0, // grid x
                          2, // grid y
                          1, // grid width
                          1, // grid height
                          GridBagConstraints.WEST, GridBagConstraints.NONE, id++);
                addWidget(osc1Pane,
                          new ScrollBarWidget("Pulse Width",
                                              patch,
                                              0,   // min
                                              127, // max
                                              0,   // display value offset
                                              new ParamModel(patch, 9),
                                              new WaldorfPulseSender(35)),
                          0, // grid x
                          3, // grid y
                          1, // grid width
                          1, // grid height
                          id++);
                osc1Pane.setBorder(
                    new TitledBorder(
                        new EtchedBorder(EtchedBorder.RAISED),
                        "Oscillator 1",
                        TitledBorder.LEFT,
                        TitledBorder.CENTER));  
                column_gbc.gridy = 1;
                column1Pane.add(osc1Pane,column_gbc);
            }
    
            // Oscillator 2 Pane
            {
                gbc.weightx = 5;
                JPanel osc2Pane = new JPanel();
                osc2Pane.setLayout(new GridBagLayout());
                addWidget(osc2Pane,
                          new ScrollBarWidget("Semitone",
                                              patch,
                                              16,  // min
                                              112, // max
                                              -64, // display value offset
                                              new ParamModel(patch, 10),
                                              new WaldorfPulseSender(36)),
                          0, // grid x
                          0, // grid y
                          3, // grid width
                          1, // grid height
                          id++);
                addWidget(osc2Pane,
                          new ScrollBarWidget("Tune",
                                              patch,
                                              0,   // min
                                              63,  // max
                                              -32, // display min
                                              new TuneParamModel(patch, 11),
                                              new WaldorfPulseSender(37, 2)),
                          0, // grid x
                          1, // grid y
                          3, // grid width
                          1, // grid height
                          id++);
                addWidget(osc2Pane,
                          new ComboBoxWidget("Shape",
                                              patch,
                                              new ParamModel(patch, 12),
                                              new WaldorfPulseSender(38),
                                              new String [] {"Pulse",
                                                             "Sawtooth",
                                                             "Triangle",
                                                             "Cross Modulation"
                                                            }),
                          0, // grid x
                          2, // grid y
                          1, // grid width
                          1, // grid height
                          GridBagConstraints.WEST, GridBagConstraints.NONE, id++);
                addWidget(osc2Pane,
                          new ScrollBarWidget("Pulse Width",
                                              patch,
                                              0,   // min
                                              127, // max
                                              0,   // display value offset
                                              new ParamModel(patch, 13),
                                              new WaldorfPulseSender(39)),
                          0, // grid x
                          3, // grid y
                          3, // grid width
                          1, // grid height
                          id++);
                addWidget(osc2Pane,
                          new CheckBoxWidget("Keytrack",
                                             patch,
                                             new ParamModel(patch, 15),
                                             new WaldorfPulseSender(40)),
                          1, // grid x
                          2, // grid y
                          1, // grid width
                          1, // grid height
                          button_id--);
                addWidget(osc2Pane,
                          new CheckBoxWidget("Sync",
                                             patch,
                                             new ParamModel(patch, 14),
                                             new WaldorfPulseSender(41)),
                          2, // grid x
                          2, // grid y
                          1, // grid width
                          1, // grid height
                          button_id--);
                osc2Pane.setBorder(
                    new TitledBorder(
                        new EtchedBorder(EtchedBorder.RAISED),
                        "Oscillator 2",
                        TitledBorder.LEFT,
                        TitledBorder.CENTER));
                column_gbc.gridy = 2;
                column1Pane.add(osc2Pane,column_gbc);
            }
            
            // Oscillator 3 Pane
            {
                JPanel osc3Pane = new JPanel();
                osc3Pane.setLayout(new GridBagLayout());
                addWidget(osc3Pane,
                          new ScrollBarWidget("Semitone",
                                              patch,
                                              16,  // min
                                              112, // max
                                              -64, // display value offset
                                              new ParamModel(patch, 16),
                                              new WaldorfPulseSender(42)),
                          0, // grid x
                          0, // grid y
                          3, // grid width
                          1, // grid height
                          id++);
                addWidget(osc3Pane,
                          new ScrollBarWidget("Tune",
                                              patch,
                                              0,   // min
                                              63,  // max
                                              -32, // display min
                                              new TuneParamModel(patch, 17),
                                              new WaldorfPulseSender(43, 2)),
                          0, // grid x
                          1, // grid y
                          3, // grid width
                          1, // grid height
                          id++);
                addWidget(osc3Pane,
                          new ComboBoxWidget("Shape",
                                              patch,
                                              new ParamModel(patch, 18),
                                              new WaldorfPulseSender(44),
                                              new String [] {"Pulse",
                                                             "Sawtooth",
                                                             "Triangle"}),
                          1, // grid x
                          2, // grid y
                          3, // grid width
                          1, // grid height
                          GridBagConstraints.WEST, GridBagConstraints.NONE, id++);
                osc3Pane.setBorder(
                    new TitledBorder(
                        new EtchedBorder(EtchedBorder.RAISED),
                        "Oscillator 3",
                        TitledBorder.LEFT,
                        TitledBorder.CENTER));
                column_gbc.gridy = 3;
                column1Pane.add(osc3Pane,column_gbc);
            }
            
            // Filter
            {
                JPanel filterPane = new JPanel();
                filterPane.setLayout(new GridBagLayout());
                addWidget(filterPane,
                          new ScrollBarWidget("Resonance",
                                              patch,
                                              0,   // min
                                              127, // max
                                              0,   // display value offset
                                              new ParamModel(patch, 67),
                                              new WaldorfPulseSender(56)),
                          0, // grid x
                          0, // grid y
                          3, // grid width
                          1, // grid height
                          id++);
                addWidget(filterPane,
                          new ScrollBarWidget("Cutoff",
                                              patch,
                                              0,   // min
                                              127, // max
                                              0,   // display value offset
                                              new ParamModel(patch, 61),
                                              new WaldorfPulseSender(50)),
                          0, // grid x
                          1, // grid y
                          3, // grid width
                          1, // grid height
                          id++);
                addWidget(filterPane,
                          new ScrollBarWidget("Cutoff Env1 Sens",
                                              patch,
                                              0,   // min
                                              127, // max
                                              -64, // display value offset
                                              new ParamModel(patch, 63),
                                              new WaldorfPulseSender(52)),
                          0, // grid x
                          2, // grid y
                          3, // grid width
                          1, // grid height
                          id++);
                addWidget(filterPane,
                          new ScrollBarWidget("Cutoff Velocity Sens",
                                              patch,
                                              0,   // min
                                              127, // max
                                              -64, // display value offset
                                              new ParamModel(patch, 64),
                                              new WaldorfPulseSender(53)),
                          0, // grid x
                          3, // grid y
                          3, // grid width
                          1, // grid height
                          id++);
                addWidget(filterPane,
                          new ComboBoxWidget("Cutoff Mod Source",
                                              patch,
                                              new ParamModel(patch, 66),
                                              new WaldorfPulseSender(54),
                                              modSources),
                          0, // grid x
                          4, // grid y
                          3, // grid width
                          1, // grid height
                          GridBagConstraints.WEST, GridBagConstraints.NONE, id++);
                addWidget(filterPane,
                          new ScrollBarWidget("Cutoff Mod Amount",
                                              patch,
                                              0,   // min
                                              127, // max
                                              -64, // display value offset
                                              new ParamModel(patch, 65),
                                              new WaldorfPulseSender(55)),
                          0, // grid x
                          5, // grid y
                          3, // grid width
                          1, // grid height
                          id++);
                addWidget(filterPane,
                          new ScrollBarWidget("Cutoff Keytrack",
                                              patch,
                                              0,   // min
                                              127, // max
                                              -64, // display value offset
                                              new ParamModel(patch, 62),
                                              new WaldorfPulseSender(51)),
                          0, // grid x
                          6, // grid y
                          3, // grid width
                          1, // grid height
                          id++);

                filterPane.setBorder(
                    new TitledBorder(
                        new EtchedBorder(EtchedBorder.RAISED),
                        "Filter",
                        TitledBorder.LEFT,
                        TitledBorder.CENTER));
                column_gbc.gridy = 4;
                column1Pane.add(filterPane,column_gbc);
            }

            // Arpeggiator
            {
                JPanel arpPane = new JPanel();
                arpPane.setLayout(new GridBagLayout());
                addWidget(arpPane,
                          new ComboBoxWidget("Active",
                                              patch,
                                              new ParamModel(patch, 56),
                                              new WaldorfPulseSender(102),
                                              new String [] {"Off",
                                                             "On",
                                                             "Hold"}),
                          0, // grid x
                          0, // grid y
                          1, // grid width
                          1, // grid height
                          GridBagConstraints.WEST, GridBagConstraints.NONE, id++);
                addWidget(arpPane,
                          new ComboBoxWidget("Mode",
                                              patch,
                                              new ParamModel(patch, 60),
                                              new WaldorfPulseSender(106),
                                              new String [] {
                                                 "Up",
                                                 "Down",
                                                 "Alternating",
                                                 "Random",
                                                 "Assign Up",
                                                 "Assign Down",
                                                 "Assign Alternating"}),
                          1, // grid x
                          0, // grid y
                          1, // grid width
                          1, // grid height
                          GridBagConstraints.WEST, GridBagConstraints.NONE, id++);
                addWidget(arpPane,
                          new ComboBoxWidget("Range",
                                              patch,
                                              new ParamModel(patch, 57),
                                              new WaldorfPulseSender(103),
                                              new String [] {"1", "2", "3", "4",
                                                             "5", "6", "7", "8",
                                                             "9", "10"}),
                          2, // grid x
                          0, // grid y
                          1, // grid width
                          1, // grid height
                          GridBagConstraints.WEST, GridBagConstraints.NONE, id++);
                String clockStr [] = new String[32];
                clockStr[0] = "1";
                for (int i = 0; i < 5; i++)
                {
                   String cl = new Integer(2 << i).toString();
                   clockStr[1 + i*3] = cl + " .";
                   clockStr[2 + i*3] = cl + "t";
                   clockStr[3 + i*3] = cl;
                }
                for (int i = 0; i < 16; i++)
                    clockStr[16 + i] = "Pat " + new Integer(i + 1).toString();
                    
                addWidget(arpPane,
                          new ScrollBarLookupWidget("Clock",
                                              patch,
                                              0,  // min
                                              31, // max
                                              new ParamModel(patch, 58),
                                              new WaldorfPulseSender(104),
                                              clockStr),
                          0, // grid x
                          1, // grid y
                          4, // grid width
                          1, // grid height
                          id++);

                String tempoStr[] = new String[128];
                tempoStr[0] = "MIDI";
                for (int t = 0; t < 127; t++)
                {
                    tempoStr[t+1] = new Integer(t*2 + 48).toString();
                }
                addWidget(arpPane,
                          new ScrollBarLookupWidget("Tempo",
                                              patch,
                                              0,   // min
                                              127, // max
                                              new ParamModel(patch, 59),
                                              new WaldorfPulseSender(105),
                                              tempoStr),
                          0, // grid x
                          2, // grid y
                          4, // grid width
                          1, // grid height
                          id++);
                          
                arpPane.setBorder(
                    new TitledBorder(
                        new EtchedBorder(EtchedBorder.RAISED),
                        "Arpeggiator",
                        TitledBorder.LEFT,
                        TitledBorder.CENTER));
                column_gbc.gridy = 5;
                column1Pane.add(arpPane,column_gbc);
            }
            page_gbc.gridx = 0;
            page_gbc.gridy = 0;
            scrollPane.add(column1Pane,page_gbc);
        }

        // Column 2 Pane
        {
            JPanel column2Pane = new JPanel();
            column2Pane.setLayout(new GridBagLayout());
            String triggerTypes [] = {
                "Single-Trigger without Reset to Zero",
                "Single-Trigger with Reset to Zero",
                "Retrigger without Reset to Zero",
                "Retrigger with Reset to Zero"};
            int envBase = 0;

            // Envelopes
            for (int e = 0; e < 2; e++)
            {
                JPanel envPane = new JPanel();
                envPane.setLayout(new GridBagLayout());
                addWidget(envPane,
                    new EnvelopeWidget(
                        "",
                        patch,
                        new EnvelopeWidget.Node []
                        {
                            new EnvelopeWidget.Node(0, 0, null, 0, 0, null, envBase,
                                             false, null, null, null, null),
                            new EnvelopeWidget.Node(0, 127,
                                             new ParamModel(patch, 27 + 6*e),
                                             127, 127, null, envBase, false,
                                             new WaldorfPulseSender(14 + 4*e),
                                             null,
                                             "Attack", null),
                            new EnvelopeWidget.Node(0, 127,
                                             new ParamModel(patch, 28 + 6*e),
                                             0, 127,
                                             new ParamModel(patch, 29 + 6*e),
                                             envBase, false,
                                             new WaldorfPulseSender(15 + 4*e),
                                             new WaldorfPulseSender(16 + 4*e),
                                             "Decay", "Sustain"),
                            new EnvelopeWidget.Node(127, 127, null,
                                             EnvelopeWidget.Node.SAME,
                                             EnvelopeWidget.Node.SAME, null,
                                             envBase, false,
                                             null, null, null, null),
                            new EnvelopeWidget.Node(0, 127,
                                             new ParamModel(patch, 30 + 6*e),
                                             0, 0, null, envBase, false,
                                             new WaldorfPulseSender(17 + 4*e),
                                             null,
                                             "Release", null)
                        },
                        0, 4),
                    0, // grid x
                    0, // grid y
                    1, // grid width
                    1, // grid height
                    id++);
                addWidget(envPane,
                          new ScrollBarWidget("Keytrack",
                                              patch,
                                              0,   // min
                                              127, // max
                                              -64, // display value offset
                                              new ParamModel(patch, 31 + 6*e),
                                              new WaldorfPulseSender(28 + 2*e)),
                          0, // grid x
                          1, // grid y
                          1, // grid width
                          1, // grid height
                          id++);
                addWidget(envPane,
                          new ComboBoxWidget("Trigger",
                                              patch,
                                              new ParamModel(patch, 32 + 6*e),
                                              new WaldorfPulseSender(29 + 2*e),
                                              triggerTypes),
                          0, // grid x
                          2, // grid y
                          1, // grid width
                          1, // grid height
                          GridBagConstraints.WEST, GridBagConstraints.NONE, id++);
                envPane.setBorder(
                    new TitledBorder(
                        new EtchedBorder(EtchedBorder.RAISED),
                        "Envelope "+new Integer(e+1).toString(),
                        TitledBorder.LEFT,
                        TitledBorder.CENTER));
                column_gbc.gridy = e;
                column2Pane.add(envPane,column_gbc);
            }

            // Mix
            {
                JPanel mixPane = new JPanel();
                mixPane.setLayout(new GridBagLayout());
                addWidget(mixPane,
                          new ScrollBarWidget("Oscillator 1",
                                              patch,
                                              0,   // min
                                              127, // max
                                              0,   // display value offset
                                              new ParamModel(patch, 19),
                                              new WaldorfPulseSender(45)),
                          0, // grid x
                          0, // grid y
                          2, // grid width
                          1, // grid height
                          id++);
                addWidget(mixPane,
                          new ScrollBarWidget("Oscillator 2",
                                              patch,
                                              0,   // min
                                              127, // max
                                              0,   // display value offset
                                              new ParamModel(patch, 20),
                                              new WaldorfPulseSender(46)),
                          0, // grid x
                          1, // grid y
                          2, // grid width
                          1, // grid height
                          id++);
                addWidget(mixPane,
                          new ScrollBarWidget("Oscillator 3",
                                              patch,
                                              0,   // min
                                              127, // max
                                              0,   // display value offset
                                              new ParamModel(patch, 21),
                                              new WaldorfPulseSender(47)),
                          0, // grid x
                          2, // grid y
                          2, // grid width
                          1, // grid height
                          id++);
                addWidget(mixPane,
                          new ScrollBarWidget("Noise",
                                              patch,
                                              0,   // min
                                              127, // max
                                              0,   // display value offset
                                              new ParamModel(patch, 22),
                                              new WaldorfPulseSender(48)),
                          0, // grid x
                          3, // grid y
                          2, // grid width
                          1, // grid height
                          id++);

                gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 1;
                gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.weightx = 0;

                mixPane.add(new JLabel(plus), gbc);
                addWidget(mixPane,
                          new ScrollBarWidget("External Signal",
                                              patch,
                                              0,   // min
                                              127, // max
                                              0,   // display value offset
                                              new ParamModel(patch, 71),
                                              new WaldorfPulseSender(49)),
                          1, // grid x
                          4, // grid y
                          1, // grid width
                          1, // grid height
                          id++);

                mixPane.setBorder(
                    new TitledBorder(
                        new EtchedBorder(EtchedBorder.RAISED),
                        "Mix",
                        TitledBorder.LEFT,
                        TitledBorder.CENTER));
                column_gbc.gridy = 2;
                column2Pane.add(mixPane,column_gbc);
            }

            // VCA
            {
                gbc.weightx = 3;
                JPanel vcaPane = new JPanel();
                vcaPane.setLayout(new GridBagLayout());

                addWidget(vcaPane,
                          new ScrollBarWidget("Volume",
                                              patch,
                                              0,   // min
                                              127, // max
                                              0,   // display value offset
                                              new ParamModel(patch, 68),
                                              new WaldorfPulseSender(57)),
                          0, // grid x
                          0, // grid y
                          1, // grid width
                          1, // grid height
                          id++);
                          
                String panningStr [] = new String[128];
                int pan;
                for (pan = 0; pan < 64; pan++)
                {
                    panningStr[pan] = "L" + new Integer(64-pan).toString();
                }
                panningStr[64] = "Centre";
                for (pan = 0; pan < 63; pan++)
                {
                    panningStr[pan+65] = "R" + new Integer(pan+1).toString();
                }
                addWidget(vcaPane,
                          new ScrollBarLookupWidget("Panning",
                                              patch,
                                              0,   // min
                                              127, // max
                                              new ParamModel(patch, 70),
                                              new WaldorfPulseSender(10),
                                              panningStr),
                          0, // grid x
                          1, // grid y
                          1, // grid width
                          1, // grid height
                          id++);
                          
                addWidget(vcaPane,
                          new ScrollBarWidget("Volume Velocity Sens",
                                              patch,
                                              0,   // min
                                              127, // max
                                              -64, // display value offset
                                              new ParamModel(patch, 69),
                                              new WaldorfPulseSender(58)),
                          0, // grid x
                          2, // grid y
                          1, // grid width
                          1, // grid height
                          id++);
                vcaPane.setBorder(
                    new TitledBorder(
                        new EtchedBorder(EtchedBorder.RAISED),
                        "VCA",
                        TitledBorder.LEFT,
                        TitledBorder.CENTER));
                column_gbc.gridy = 3;
                column2Pane.add(vcaPane,column_gbc);
            }
            
            // Portamento
            {
                JPanel portaPane = new JPanel();
                portaPane.setLayout(new GridBagLayout());

                String timeStr [] = new String [128];
                timeStr[0] = "Off";
                for (int t = 1; t < 128; t++)
                {
                    timeStr[t] = new Integer(t).toString();
                }
                addWidget(portaPane,
                          new ScrollBarLookupWidget("Time",
                                              patch,
                                              0,   // min
                                              127, // max
                                              new ParamModel(patch, 41),
                                              new WaldorfPulseSender(5),
                                              timeStr),
                          0, // grid x
                          0, // grid y
                          2, // grid width
                          1, // grid height
                          id++);
                          
                addWidget(portaPane,
                    new ComboBoxWidget("Mode",
                        patch,
                        new ParamModel(patch, 42),
                        new WaldorfPulseSender(62),
                        new String [] {"On All Notes",
                                       "On Legato Notes Only"}),
                    0, // grid x
                    1, // grid y
                    1, // grid width
                    1, // grid height
                    GridBagConstraints.WEST, GridBagConstraints.NONE, id++);

                portaPane.setBorder(
                    new TitledBorder(
                        new EtchedBorder(EtchedBorder.RAISED),
                        "Portamento",
                        TitledBorder.LEFT,
                        TitledBorder.CENTER));
                column_gbc.gridy = 4;
                column2Pane.add(portaPane,column_gbc);
            }
            page_gbc.gridx = 1;
            page_gbc.gridy = 0;
            scrollPane.add(column2Pane,page_gbc);
        }

        // Column 3 Pane
        {
            JPanel column3Pane = new JPanel();
            column3Pane.setLayout(new GridBagLayout());

            // LFO 1
            {
                gbc.weightx = 3;
                JPanel lfo1Pane = new JPanel();
                lfo1Pane.setLayout(new GridBagLayout());
                final String [] lfo1SpeedStrNormal = new String[128];
                for (int i = 0; i < 128; i++)
                   lfo1SpeedStrNormal[i] = new Integer(i).toString();
                String [] syncStr = new String [] {
                    "8 Bars", "6 Bars", "4 Bars", "3 Bars", "2 Bars", "1 Bar",
                    "2 .", "2", "4 .", "4", "8 .", "8",
                    "16 .", "16", "32 .", "32"
                };
                final String [] lfo1SpeedStrSync = new String[128];
                for (int i = 0; i < 16; i++)
                   for (int j = 0; j < 8; j++)
                       lfo1SpeedStrSync[i*8 + j] = syncStr[i];
                final ScrollBarLookupWidget lfo1Speed =
                    new ScrollBarLookupWidget(
                        "Speed",
                        patch,
                        0,   // min
                        127, // max
                        new ParamModel(patch, 23),
                        new WaldorfPulseSender(24),
                        lfo1SpeedStrNormal);
                addWidget(lfo1Pane,
                          lfo1Speed,
                          0, // grid x
                          0, // grid y
                          2, // grid width
                          1, // grid height
                          id++);
                if (patch.sysex[24] >= 5)
                {
                    lfo1Speed.changeOptions(lfo1SpeedStrSync);
                }
                else
                {
                    lfo1Speed.changeOptions(lfo1SpeedStrNormal);
                }
                final ComboBoxWidget lfo1Shape =
                    new ComboBoxWidget("Shape",
                                       patch,
                                       new ParamModel(patch, 24),
                                       new WaldorfPulseSender(25),
                                       new String[] {"Sine",
                                                     "Triangle",
                                                     "Sawtooth",
                                                     "Pulse",
                                                     "Sample & Hold",
                                                     "Triangle Sync",
                                                     "Sawtooth Sync",
                                                     "Pulse Sync"});
                lfo1Shape.addEventListener(new ItemListener()
                    {
                        public void itemStateChanged (ItemEvent e)
                        {
                            if (e.getStateChange() == ItemEvent.SELECTED)
                            {
                                if (lfo1Shape.getValue() >= 5)
                                {
                                    lfo1Speed.changeOptions(lfo1SpeedStrSync);
                                }
                                else
                                {
                                    lfo1Speed.changeOptions(lfo1SpeedStrNormal);
                                }
                            }
                        }
                    });
                addWidget(lfo1Pane,
                          lfo1Shape,
                          0, // grid x
                          1, // grid y
                          2, // grid width
                          1, // grid height
                          GridBagConstraints.WEST, GridBagConstraints.NONE, id++);
                lfo1Pane.setBorder(
                    new TitledBorder(
                        new EtchedBorder(EtchedBorder.RAISED),
                        "LFO 1",
                        TitledBorder.LEFT,
                        TitledBorder.CENTER));
                column_gbc.gridy = 0;
                column3Pane.add(lfo1Pane,column_gbc);
            }

            // LFO 2
            {
                JPanel lfo2Pane = new JPanel();
                lfo2Pane.setLayout(new GridBagLayout());
                addWidget(lfo2Pane,
                          new ScrollBarWidget("Speed",
                                              patch,
                                              0,   // min
                                              127, // max
                                              0,   // display value offset
                                              new ParamModel(patch, 25),
                                              new WaldorfPulseSender(26)),
                          0, // grid x
                          0, // grid y
                          2, // grid width
                          1, // grid height
                          id++);
                String delayStr[] = new String[128];
                delayStr[0] = "Off";
                for (int i = 1; i < 128; i++)
                {
                    delayStr[i] = new Integer(i).toString();
                }
                addWidget(lfo2Pane,
                          new ScrollBarLookupWidget("Delay",
                                              patch,
                                              0,   // min
                                              127, // max
                                              new ParamModel(patch, 26),
                                              new WaldorfPulseSender(27),
                                              delayStr),
                          0, // grid x
                          1, // grid y
                          2, // grid width
                          1, // grid height
                          id++);
                lfo2Pane.setBorder(
                    new TitledBorder(
                        new EtchedBorder(EtchedBorder.RAISED),
                        "LFO 2",
                        TitledBorder.LEFT,
                        TitledBorder.CENTER));
                column_gbc.gridy = 1;
                column3Pane.add(lfo2Pane,column_gbc);
            }
            
            // Pitch Mod
            {
                JPanel pitchModPane = new JPanel();
                pitchModPane.setLayout(new GridBagLayout());
                addWidget(pitchModPane,
                          new ComboBoxWidget("Source",
                                              patch,
                                              new ParamModel(patch, 40),
                                              new WaldorfPulseSender(60),
                                              modSources),
                          0, // grid x
                          0, // grid y
                          1, // grid width
                          1, // grid height
                          GridBagConstraints.WEST, GridBagConstraints.NONE, id++);
                addWidget(pitchModPane,
                          new ScrollBarWidget("Amount",
                                              patch,
                                              0,   // min
                                              127, // max
                                              -64, // display value offset
                                              new ParamModel(patch, 39),
                                              new WaldorfPulseSender(61)),
                          0, // grid x
                          1, // grid y
                          3, // grid width
                          1, // grid height
                          id++);
                addWidget(pitchModPane,
                          new ScrollBarWidget("Pitchbend Scale",
                                              patch,
                                              0,  // min
                                              24, // max
                                              0,  // display value offset
                                              new ParamModel(patch, 43),
                                              new WaldorfPulseSender(63)),
                          0, // grid x
                          2, // grid y
                          3, // grid width
                          1, // grid height
                          id++);
                pitchModPane.setBorder(
                    new TitledBorder(
                        new EtchedBorder(EtchedBorder.RAISED),
                        "Pitch Mod",
                        TitledBorder.LEFT,
                        TitledBorder.CENTER));
                column_gbc.gridy = 2;
                column3Pane.add(pitchModPane,column_gbc);
            }

            // Mod Units
            for (int m = 0; m < 4; m++)
            {
                gbc.weightx = 3;
                JPanel modPane = new JPanel();
                modPane.setLayout(new GridBagLayout());
                addWidget(modPane,
                          new ComboBoxWidget("Source",
                                              patch,
                                              new ParamModel(patch, 44 + m*3),
                                              new WaldorfPulseSender(108 + m*3),
                                              modSources),
                          0, // grid x
                          0, // grid y
                          1, // grid width
                          1, // grid height
                          GridBagConstraints.WEST, GridBagConstraints.NONE, id++);
                addWidget(modPane,
                          new ComboBoxWidget("Destination",
                                              patch,
                                              new ParamModel(patch, 46 + m*3),
                                              new WaldorfPulseSender(110 + m*3),
                                              modDestinations),
                          1, // grid x
                          0, // grid y
                          1, // grid width
                          1, // grid height
                          GridBagConstraints.WEST, GridBagConstraints.NONE, id++);
                addWidget(modPane,
                          new ScrollBarWidget("Amount",
                                              patch,
                                              0,   // min
                                              127, // max
                                              -64, // display value offset
                                              new ParamModel(patch, 45 + m*3),
                                              new WaldorfPulseSender(109 +m*3)),
                          0, // grid x
                          1, // grid y
                          3, // grid width
                          1, // grid height
                          id++);
                modPane.setBorder(
                    new TitledBorder(
                        new EtchedBorder(EtchedBorder.RAISED),
                        "Mod Unit " + new Integer(m+1).toString(),
                        TitledBorder.LEFT,
                        TitledBorder.CENTER));
                column_gbc.gridy = 3 + m;
                column3Pane.add(modPane,column_gbc);
            }

            // CV2
            {
                JPanel cv2Pane = new JPanel();
                cv2Pane.setLayout(new GridBagLayout());

                gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1;
                gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.weightx = 0;
                cv2Pane.add(new JLabel(plus), gbc);

                gbc.gridy = 1;
                cv2Pane.add(new JLabel(plus), gbc);

                gbc.weightx = 3;
                addWidget(cv2Pane,
                          new ComboBoxWidget("Source",
                                              patch,
                                              new ParamModel(patch, 72),
                                              new WaldorfPulseSender(22),
                                              modSources),
                          1, // grid x
                          0, // grid y
                          1, // grid width
                          1, // grid height
                          GridBagConstraints.WEST, GridBagConstraints.NONE, id++);

                addWidget(cv2Pane,
                          new ScrollBarWidget("Amount",
                                              patch,
                                              0,   // min
                                              127, // max
                                              -64, // display value offset
                                              new ParamModel(patch, 73),
                                              new WaldorfPulseSender(23)),
                          1, // grid x
                          1, // grid y
                          2, // grid width
                          1, // grid height
                          id++);

                cv2Pane.setBorder(
                    new TitledBorder(
                        new EtchedBorder(EtchedBorder.RAISED),
                        "CV 2",
                        TitledBorder.LEFT,
                        TitledBorder.CENTER));
                column_gbc.gridy = 7;
                column3Pane.add(cv2Pane,column_gbc);
            }

            page_gbc.gridx = 2;
            page_gbc.gridy = 0;
            scrollPane.add(column3Pane,page_gbc);
        }
    }

    public void addWidget(JComponent parent,SysexWidget widget,
                          int gridx, int gridy, int gridwidth, int gridheight,
                          int anchor, int fill, int slidernum)
    {
        gbc.ipadx = 4;
        if (widget instanceof ScrollBarWidget ||
            widget instanceof EnvelopeWidget ||
            widget instanceof ScrollBarLookupWidget) {
            gbc.insets = new Insets(4,8,4,8);
        } else {
            gbc.insets = new Insets(4,1,4,1);
        }

        super.addWidget(parent, widget, gridx, gridy, gridwidth, gridheight,
                        anchor, fill, slidernum);
    }

    class TuneParamModel extends ParamModel
    {
        public TuneParamModel(Patch p, int o)
        {
            patch = p;
            ofs = o;
        }
        public void set(int i)
        {
            patch.sysex[ofs] = (byte)(i * 2);
        }
        public int get()
        {
            return patch.sysex[ofs] / 2;
        }
    }

    class WaldorfPulseSender extends SysexSender
    {        
        private byte []b = new byte [3];
        private int mult;
        
        public WaldorfPulseSender(int param)
        {
            this(param, 1);
        }
        
        public WaldorfPulseSender(int param, int multiplier)
        {            
            b[0] = (byte)0xB0; b[1]= (byte)param;
            mult = multiplier;
        }

        public byte [] generate(int value)
        {
            b[0] = (byte)(b[0] & (byte)0xf0 | (byte)(channel-1));
            b[2] = (byte)(value*mult);
            return b;
        }
    }
}
