/*
 * JSynthlib - "Performance" Editor for Yamaha DX7 Mark-I
 * ======================================================
 * @author  Torsten Tittmann
 * file:    YamahaDX7PerformanceEditor.java
 * date:    25.02.2003
 * @version 0.2
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
 * comment: The underlaying patch is identical for the DX1, DX5, DX7, TX7 and TX816.
 *          But only those parameters are displayed, which are evaluated by the DX7 Mark-I.
 *
 * history:
 *         31.10.2002 v0.1: first published release
 *         25.02.2003 v0.2: - driver name changed (YamahaTX7SinglePerformanceEditor -> YamahaTX7PerformanceEditor)
 * 
 */

package synthdrivers.YamahaDX7;
import core.*;
import java.lang.String.*;
import java.text.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;
import javax.swing.event.*;

class YamahaDX7PerformanceEditor extends PatchEditorFrame
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


  public YamahaDX7PerformanceEditor(Patch patch)
  {
    super ("Yamaha DX7 \"Performance\" Editor",patch);
   
    /*
     *   Performance Parameter - Common settings
     */

    JPanel cmnPane= new JPanel();
    cmnPane.setLayout(new GridBagLayout());gbc.weightx=0;
    addWidget(cmnPane,new PatchNameWidget(patch,"Performance Name (30 Char.)"),0,0,7,1,1);

    addWidget(cmnPane,new ComboBoxWidget("Corresponding Voice Number?",patch,new ParamModel(patch,6+0),null,VoiceNumberName),0,2,3,1,3);
    gbc.gridx=4;gbc.gridy=2;gbc.gridwidth=3;gbc.gridheight=1;gbc.anchor=gbc.EAST;
    cmnPane.add(new JLabel("(Attention! This is an undocumented parameter!) "),gbc);

    gbc.gridx=0;gbc.gridy=0;gbc.gridwidth=3;gbc.gridheight=1;
    cmnPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Common",TitledBorder.CENTER,TitledBorder.CENTER));
    scrollPane.add(cmnPane,gbc);

    /*
     *   Voice A Parameter (only voice A accessible)
     */
    JPanel voicePane = new JPanel();
    voicePane.setLayout(new GridBagLayout());

    gbc.gridx=0;gbc.gridy=1;gbc.gridwidth=3;gbc.gridheight=1; voicePane.add(new JLabel("Key Mode: "),gbc);
    addWidget(voicePane,new ComboBoxWidget("Assign"              ,patch,new ParamModel(patch,6+2),new PerformanceSender(patch,64),PolyMonoName),3,1,6,1,10);

    gbc.gridx=0;gbc.gridy=2;gbc.gridwidth=1;gbc.gridheight=1; voicePane.add(new JLabel(" "),gbc);
    gbc.gridx=0;gbc.gridy=3;gbc.gridwidth=3;gbc.gridheight=1; voicePane.add(new JLabel("Pitch Bend: "),gbc);
    addWidget(voicePane,new ComboBoxWidget("Range"               ,patch,new ParamModel(patch,6+3),new PerformanceSender(patch,65),PitchBendRangeName),3,3,6,1,11);
    addWidget(voicePane,new ComboBoxWidget("Step"                ,patch,new ParamModel(patch,6+4),new PerformanceSender(patch,66),PitchBendStepName),9,3,6,1,12);

    gbc.gridx=0;gbc.gridy=4;gbc.gridwidth=1;gbc.gridheight=1; voicePane.add(new JLabel(" "),gbc);
    gbc.gridx=0;gbc.gridy=5;gbc.gridwidth=3;gbc.gridheight=1; voicePane.add(new JLabel("Portamento: "),gbc);
    addWidget(voicePane,new ComboBoxWidget("Portamento/Glissando",patch,new ParamModel(patch,6+6),new PerformanceSender(patch,68),PortGlissName),3,5,6,1,13);
    addWidget(voicePane,new ComboBoxWidget("Mode (Poly/Mono)"    ,patch,new ParamModel(patch,6+7),new PerformanceSender(patch,67),PortModeName),3,6,6,1,14);
    addWidget(voicePane,new ScrollBarWidget("Time"               ,patch,0,99,0,new ParamModel(patch,6+5),new PerformanceSender(patch,69)),9,6,6,1,20);

    gbc.gridx=0;gbc.gridy=7;gbc.gridwidth=1;gbc.gridheight=1; voicePane.add(new JLabel(" "),gbc);
    gbc.gridx=0;gbc.gridy=8;gbc.gridwidth=3;gbc.gridheight=1; voicePane.add(new JLabel("Modulation Wheel: "),gbc);
    addWidget(voicePane,new ComboBoxWidget("Assign"              ,patch,new ParamModel(patch,6+10),new PerformanceSender(patch,71),AssignName),3,8,6,1,15);
    addWidget(voicePane,new ScrollBarWidget("Sensitivity"        ,patch,0,15,0,new ParamModel(patch,6+9),new PerformanceSender(patch,70)),9,8,6,1,21);

    gbc.gridx=0;gbc.gridy=9;gbc.gridwidth=1;gbc.gridheight=1; voicePane.add(new JLabel(" "),gbc);
    gbc.gridx=0;gbc.gridy=10;gbc.gridwidth=3;gbc.gridheight=1; voicePane.add(new JLabel("Foot Controller: "),gbc);
    addWidget(voicePane,new ComboBoxWidget("Assign"              ,patch,new ParamModel(patch,6+12),new PerformanceSender(patch,73),AssignName),3,10,6,1,16);
    addWidget(voicePane,new ScrollBarWidget("Sensitivity"        ,patch,0,15,0,new ParamModel(patch,6+11),new PerformanceSender(patch,72)),9,10,6,1,22);

    gbc.gridx=0;gbc.gridy=11;gbc.gridwidth=1;gbc.gridheight=1; voicePane.add(new JLabel(" "),gbc);
    gbc.gridx=0;gbc.gridy=12;gbc.gridwidth=3;gbc.gridheight=1; voicePane.add(new JLabel("After Touch: "),gbc);
    addWidget(voicePane,new ComboBoxWidget("Assign"              ,patch,new ParamModel(patch,6+14),new PerformanceSender(patch,77),AssignName),3,12,6,1,17);
    addWidget(voicePane,new ScrollBarWidget("Sensitivity"        ,patch,0,15,0,new ParamModel(patch,6+13),new PerformanceSender(patch,76)),9,12,6,1,23);

    gbc.gridx=0;gbc.gridy=13;gbc.gridwidth=1;gbc.gridheight=1; voicePane.add(new JLabel(" "),gbc);
    gbc.gridx=0;gbc.gridy=14;gbc.gridwidth=3;gbc.gridheight=1; voicePane.add(new JLabel("Breath Controller: "),gbc);
    addWidget(voicePane,new ComboBoxWidget("Assign"              ,patch,new ParamModel(patch,6+16),new PerformanceSender(patch,75),AssignName),3,14,6,1,18);
    addWidget(voicePane,new ScrollBarWidget("Sensitivity"        ,patch,0,15,0,new ParamModel(patch,6+15),new PerformanceSender(patch,74)),9,14,6,1,24);

    gbc.gridx=0;gbc.gridy=1;gbc.gridwidth=3;gbc.gridheight=1;
    voicePane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Voice",TitledBorder.CENTER,TitledBorder.CENTER));
    scrollPane.add(voicePane,gbc);
    pack();
    show();
  }
}
