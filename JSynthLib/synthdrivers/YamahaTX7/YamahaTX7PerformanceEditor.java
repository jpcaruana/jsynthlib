/*
 * JSynthlib - "Performance" Editor for Yamaha TX7
 * ===============================================
 * @author  Torsten Tittmann
 * file:    YamahaTX7PerformanceEditor.java
 * date:    25.02.2003
 * @version 0.1
 *
 * Copyright (C) 2002-2003  Torsten.Tittmann@t-online.de
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 *
 * history:
 *         25.02.2003 v0.1: first published release
 * 
 */

package synthdrivers.YamahaTX7;
import core.*;
import java.lang.String.*;
import java.text.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;
import javax.swing.event.*;

class YamahaTX7PerformanceEditor extends PatchEditorFrame
{
  static final String [] OnOffName           = new String [] {"Off","On"};

  static final String [] SourceSelectName    = new String [] {"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16"};

  static final String [] PolyMonoName        = new String [] {"Poly","Mono"};

  static final String [] VoiceSelectName     = new String [] {"Voice A","Voice B"};

  static final String [] VoiceNumberName     = new String [] { " 1"," 2"," 3"," 4"," 5"," 6"," 7"," 8"," 9","10","11","12","13","14","15","16",
                                                               "17","18","19","20","21","22","23","24","25","26","27","28","29","30","31","32",
                                                               "33","34","35","36","37","38","39","40","41","42","43","44","45","46","47","48",
                                                               "49","50","51","52","53","54","55","56","57","58","59","60","61","62","63","64"
                                                             };

  static final String [] KeyAssignName       = new String [] {"Single","Dual","Split"};

  static final String [] PitchBendRangeName  = new String [] {"Off",			//
                                                              "perfect First (Unison)",	// Prime
                                                              "minor Second",		// kleine Sekunde
                                                              "major Second",		// groﬂe  Sekunde
                                                              "minor Third",		// kleine Terz
                                                              "major Third",		// groﬂe  Terz
                                                              "perfect Fourth",		// Quarte
                                                              "perfect Fifth",		// Quinte bzw. Tritonus 
                                                              "minor Sixth",		// kleine Sexte
                                                              "major Sixth",		// groﬂe  Sexte
                                                              "minor Seventh",		// kleine Septine
                                                              "major Seventh",		// groﬂe  Septine
                                                              "Octave"};		// Oktave

  static final String [] PitchBendStepName   = new String [] {"Continuous",
                                                              "±1 Octave        - 12 Steps",
                                                              "±1 Octave        -  6 Steps",
                                                              "±1 Octave        -  4 Steps",
                                                              "±1 Octave        -  3 Steps",
                                                              "±1 minor Seventh -  2 Steps",
                                                              "±1 Octave        -  2 Steps",
                                                              "±1 Fifth         -  1 Step ",
                                                              "±1 major Fifth   -  1 Step ",
                                                              "±1 Sixth         -  1 Step ",
                                                              "±1 minor Seventh -  1 Step ",
                                                              "±1 major Seventh -  1 Step ",
                                                              "±1 Octave        -  1 Step "};

  static final String [] PortGlissName       = new String [] {"Portamento","Glissando"};

  static final String [] PortModeName        = new String [] {"Retain/Fingered","Follow/Full Time"};

  static final String [] AssignName          = new String [] {"Off",
                                                              "Pitch",
                                                              "Amplitude",
                                                              "Pitch + Amplitude",
                                                              "EG Bias",
                                                              "Pitch + EG Bias",
                                                              "Pitch + Amplitude",
                                                              "Pitch + Amplitude + EG Bias"};


  public YamahaTX7PerformanceEditor(Patch patch)
  {
    super ("Yamaha TX7 \"Performance\" Editor",patch);
   
    /*
     *   Performance Parameter - Common settings
     */

    JPanel cmnPane= new JPanel();
    cmnPane.setLayout(new GridBagLayout());gbc.weightx=0;
    addWidget(cmnPane,new PatchNameWidget(patch,"Performance Name (30 Char.)"),0,0,7,1,1);

    addWidget(cmnPane,new ComboBoxWidget("Key Assign Mode (Only \"Single\" is supported)",patch,new ParamModel(patch,6+60),new PerformanceSender(patch,30),KeyAssignName),0,1,6,1,4);
    addWidget(cmnPane,new ComboBoxWidget("Voice Memory Select Flag",patch,new ParamModel(patch,6+61),new PerformanceSender(patch,31),VoiceSelectName),6,1,3,1,2);
    addWidget(cmnPane,new ComboBoxWidget("Corresponding Voice Number?",patch,new ParamModel(patch,6+0),null,VoiceNumberName),0,2,3,1,3);
    gbc.gridx=4;gbc.gridy=2;gbc.gridwidth=6;gbc.gridheight=1;gbc.anchor=gbc.EAST;
    cmnPane.add(new JLabel("Attention! This is an undocumented parameter and only valid for a single patch! "),gbc);

    gbc.gridx=0;gbc.gridy=0;gbc.gridwidth=3;gbc.gridheight=1;
    cmnPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Common",TitledBorder.CENTER,TitledBorder.CENTER));
    scrollPane.add(cmnPane,gbc);

    /*
     *   Voice A+B Parameter
     */
    JTabbedPane voicePane=new JTabbedPane();

    // Voice A
    JPanel voiceApanel = new JPanel();
    voiceApanel.setLayout(new GridBagLayout());
    voicePane.addTab("Voice A",voiceApanel);gbc.weightx=1;

    gbc.gridx=0;gbc.gridy=0;gbc.gridwidth=1;gbc.gridheight=1; voiceApanel.add(new JLabel(" "),gbc);
    addWidget(voiceApanel,new ComboBoxWidget("Key Mode Assign",patch,new ParamModel(patch,6+2),new PerformanceSender(patch,2),PolyMonoName),3,0,6,1,10);
    addWidget(voiceApanel,new ScrollBarWidget("Output Attenuator",patch,0,7,0,new ParamModel(patch,6+26),new PerformanceSender(patch,26)),9,0,6,1,19);

    gbc.gridx=0;gbc.gridy=1;gbc.gridwidth=1;gbc.gridheight=1; voiceApanel.add(new JLabel(" "),gbc);
    gbc.gridx=0;gbc.gridy=2;gbc.gridwidth=3;gbc.gridheight=1; voiceApanel.add(new JLabel("Pitch Bend: "),gbc);
    addWidget(voiceApanel,new ComboBoxWidget("Range",patch,new ParamModel(patch,6+3),new PerformanceSender(patch,3),PitchBendRangeName),3,2,6,1,11);
    addWidget(voiceApanel,new ComboBoxWidget("Step",patch,new ParamModel(patch,6+4),new PerformanceSender(patch,4),PitchBendStepName),9,2,6,1,12);

    gbc.gridx=0;gbc.gridy=3;gbc.gridwidth=1;gbc.gridheight=1; voiceApanel.add(new JLabel(" "),gbc);
    gbc.gridx=0;gbc.gridy=4;gbc.gridwidth=3;gbc.gridheight=1; voiceApanel.add(new JLabel("Portamento: "),gbc);
    addWidget(voiceApanel,new ComboBoxWidget("Portamento/Glissando",patch,new ParamModel(patch,6+6),new PerformanceSender(patch,6),PortGlissName),3,4,6,1,13);
    addWidget(voiceApanel,new ComboBoxWidget("Mode (Poly/Mono)",patch,new ParamModel(patch,6+7),new PerformanceSender(patch,7),PortModeName),3,5,6,1,14);
    addWidget(voiceApanel,new ScrollBarWidget("Time",patch,0,99,0,new ParamModel(patch,6+5),new PerformanceSender(patch,5)),9,5,6,1,20);

    gbc.gridx=0;gbc.gridy=6;gbc.gridwidth=1;gbc.gridheight=1; voiceApanel.add(new JLabel(" "),gbc);
    gbc.gridx=0;gbc.gridy=7;gbc.gridwidth=3;gbc.gridheight=1; voiceApanel.add(new JLabel("Modulation Wheel: "),gbc);
    addWidget(voiceApanel,new ComboBoxWidget("Assign",patch,new ParamModel(patch,6+10),new PerformanceSender(patch,10),AssignName),3,7,6,1,15);
    addWidget(voiceApanel,new ScrollBarWidget("Sensitivity",patch,0,15,0,new ParamModel(patch,6+9),new PerformanceSender(patch,9)),9,7,6,1,21);

    gbc.gridx=0;gbc.gridy=8;gbc.gridwidth=1;gbc.gridheight=1; voiceApanel.add(new JLabel(" "),gbc);
    gbc.gridx=0;gbc.gridy=9;gbc.gridwidth=3;gbc.gridheight=1; voiceApanel.add(new JLabel("Foot Controller: "),gbc);
    addWidget(voiceApanel,new ComboBoxWidget("Assign",patch,new ParamModel(patch,6+12),new PerformanceSender(patch,12),AssignName),3,9,6,1,16);
    addWidget(voiceApanel,new ScrollBarWidget("Sensitivity",patch,0,15,0,new ParamModel(patch,6+11),new PerformanceSender(patch,11)),9,9,6,1,22);

    gbc.gridx=0;gbc.gridy=10;gbc.gridwidth=1;gbc.gridheight=1; voiceApanel.add(new JLabel(" "),gbc);
    gbc.gridx=0;gbc.gridy=11;gbc.gridwidth=3;gbc.gridheight=1; voiceApanel.add(new JLabel("After Touch: "),gbc);
    addWidget(voiceApanel,new ComboBoxWidget("Assign",patch,new ParamModel(patch,6+14),new PerformanceSender(patch,14),AssignName),3,11,6,1,17);
    addWidget(voiceApanel,new ScrollBarWidget("Sensitivity",patch,0,15,0,new ParamModel(patch,6+13),new PerformanceSender(patch,13)),9,11,6,1,23);

    gbc.gridx=0;gbc.gridy=12;gbc.gridwidth=1;gbc.gridheight=1; voiceApanel.add(new JLabel(" "),gbc);
    gbc.gridx=0;gbc.gridy=13;gbc.gridwidth=3;gbc.gridheight=1; voiceApanel.add(new JLabel("Breath Controller: "),gbc);
    addWidget(voiceApanel,new ComboBoxWidget("Assign",patch,new ParamModel(patch,6+16),new PerformanceSender(patch,16),AssignName),3,13,6,1,18);
    addWidget(voiceApanel,new ScrollBarWidget("Sensitivity",patch,0,15,0,new ParamModel(patch,6+15),new PerformanceSender(patch,15)),9,13,6,1,24);

    // Voice B
    JPanel voiceBpanel = new JPanel();
    voiceBpanel.setLayout(new GridBagLayout());
    voicePane.addTab("Voice B",voiceBpanel);gbc.weightx=1;

    gbc.gridx=0;gbc.gridy=0;gbc.gridwidth=1;gbc.gridheight=1; voiceBpanel.add(new JLabel(" "),gbc);
    addWidget(voiceBpanel,new ComboBoxWidget("Key Mode Assign",patch,new ParamModel(patch,6+32),null,PolyMonoName),3,0,6,1,40);
    addWidget(voiceBpanel,new ScrollBarWidget("Output Attenuator",patch,0,7,0,new ParamModel(patch,6+56),null),9,0,6,1,49);

    gbc.gridx=0;gbc.gridy=1;gbc.gridwidth=1;gbc.gridheight=1; voiceBpanel.add(new JLabel(" "),gbc);
    gbc.gridx=0;gbc.gridy=2;gbc.gridwidth=3;gbc.gridheight=1; voiceBpanel.add(new JLabel("Pitch Bend: "),gbc);
    addWidget(voiceBpanel,new ComboBoxWidget("Range",patch,new ParamModel(patch,6+33),null,PitchBendRangeName),3,2,6,1,41);
    addWidget(voiceBpanel,new ComboBoxWidget("Step",patch,new ParamModel(patch,6+34),null,PitchBendStepName),9,2,6,1,42);

    gbc.gridx=0;gbc.gridy=3;gbc.gridwidth=1;gbc.gridheight=1; voiceBpanel.add(new JLabel(" "),gbc);
    gbc.gridx=0;gbc.gridy=4;gbc.gridwidth=3;gbc.gridheight=1; voiceBpanel.add(new JLabel("Portamento: "),gbc);
    addWidget(voiceBpanel,new ComboBoxWidget("Portamento/Glissando",patch,new ParamModel(patch,6+36),null,PortGlissName),3,4,6,1,43);
    addWidget(voiceBpanel,new ComboBoxWidget("Mode (Poly/Mono)",patch,new ParamModel(patch,6+37),null,PortModeName),3,5,6,1,44);
    addWidget(voiceBpanel,new ScrollBarWidget("Time",patch,0,99,0,new ParamModel(patch,6+35),null),9,5,6,1,50);

    gbc.gridx=0;gbc.gridy=6;gbc.gridwidth=1;gbc.gridheight=1; voiceBpanel.add(new JLabel(" "),gbc);
    gbc.gridx=0;gbc.gridy=7;gbc.gridwidth=3;gbc.gridheight=1; voiceBpanel.add(new JLabel("Modulation Wheel: "),gbc);
    addWidget(voiceBpanel,new ComboBoxWidget("Assign",patch,new ParamModel(patch,6+40),null,AssignName),3,7,6,1,45);
    addWidget(voiceBpanel,new ScrollBarWidget("Sensitivity",patch,0,15,0,new ParamModel(patch,6+39),null),9,7,6,1,51);

    gbc.gridx=0;gbc.gridy=8;gbc.gridwidth=1;gbc.gridheight=1; voiceBpanel.add(new JLabel(" "),gbc);
    gbc.gridx=0;gbc.gridy=9;gbc.gridwidth=3;gbc.gridheight=1; voiceBpanel.add(new JLabel("Foot Controller: "),gbc);
    addWidget(voiceBpanel,new ComboBoxWidget("Assign",patch,new ParamModel(patch,6+42),null,AssignName),3,9,6,1,46);
    addWidget(voiceBpanel,new ScrollBarWidget("Sensitivity",patch,0,15,0,new ParamModel(patch,6+41),null),9,9,6,1,52);

    gbc.gridx=0;gbc.gridy=10;gbc.gridwidth=1;gbc.gridheight=1; voiceBpanel.add(new JLabel(" "),gbc);
    gbc.gridx=0;gbc.gridy=11;gbc.gridwidth=3;gbc.gridheight=1; voiceBpanel.add(new JLabel("After Touch: "),gbc);
    addWidget(voiceBpanel,new ComboBoxWidget("Assign",patch,new ParamModel(patch,6+44),null,AssignName),3,11,6,1,47);
    addWidget(voiceBpanel,new ScrollBarWidget("Sensitivity",patch,0,15,0,new ParamModel(patch,6+43),null),9,11,6,1,53);

    gbc.gridx=0;gbc.gridy=12;gbc.gridwidth=1;gbc.gridheight=1; voiceBpanel.add(new JLabel(" "),gbc);
    gbc.gridx=0;gbc.gridy=13;gbc.gridwidth=3;gbc.gridheight=1; voiceBpanel.add(new JLabel("Breath Controller: "),gbc);
    addWidget(voiceBpanel,new ComboBoxWidget("Assign",patch,new ParamModel(patch,6+46),null,AssignName),3,13,6,1,48);
    addWidget(voiceBpanel,new ScrollBarWidget("Sensitivity",patch,0,15,0,new ParamModel(patch,6+45),null),9,13,6,1,54);

    gbc.gridx=0;gbc.gridy=10;gbc.gridwidth=3;gbc.gridheight=13;
    scrollPane.add(voicePane,gbc);
    pack();
    show();
  }
}
