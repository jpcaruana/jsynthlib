/*
 * JSynthlib - "Performance" Editor for Yamaha DX7s
 * ================================================
 * @author  Torsten Tittmann
 * file:    YamahaDX7sPerformanceEditor.java
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
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * CAUTION: This is an experimental driver. It is not tested on a real device yet!
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 *
 * history:
 *         25.02.2003 v0.1: first published release
 *                          
 *
 */

package synthdrivers.YamahaDX7s;
import core.*;
import java.lang.String.*;
import java.text.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;
import javax.swing.event.*;

class YamahaDX7sPerformanceEditor extends PatchEditorFrame 
{
  static final String [] OnOffName          = new String [] {"Off","On"};

  static final String [] VoiceModeName      = new String [] {"Single","Dual","Split"};

  static final String [] PanModeName        = new String [] {"mix"," on- on"," on-off", "off- on"};

  static final String [] FSOnOffName        = new String [] {"off - off"," on-off", "off- on"," on- on"};

  static final String [] FSAssignName       = new String [] {"Sustain","Portamento","Key Hold","Soft"};

  static final String [] PanAssignName      = new String [] {"LFO","Velocity","Key Number"};

  static final String [] DetuneName         = new String [] { "0", "1", "2", "3", "4", "5", "6", "7"};

  static final String [] VolumeName         = new String [] {
							    "  0","  1","  2","  3","  4","  5","  6","  7","  8","  9"," 10"," 11"," 12"," 13"," 14"," 15",
							    " 16"," 17"," 18"," 19"," 20"," 21"," 22"," 23"," 24"," 25"," 26"," 27"," 28"," 29"," 30"," 31",
							    " 32"," 33"," 34"," 35"," 36"," 37"," 38"," 39"," 40"," 41"," 42"," 43"," 44"," 45"," 46"," 47",
							    " 48"," 49"," 50"," 51"," 52"," 53"," 54"," 55"," 56"," 57"," 58"," 59"," 60"," 61"," 62"," 63",
							    " 64"," 65"," 66"," 67"," 68"," 69"," 70"," 71"," 72"," 73"," 74"," 75"," 76"," 77"," 78"," 79",
							    " 80"," 81"," 82"," 83"," 84"," 85"," 86"," 87"," 88"," 89"," 90"," 91"," 92"," 93"," 94"," 95",
							    " 96"," 97"," 98"," 99"};

  static final String [] BalanceName         = new String [] {
" -50"," -49"," -48"," -47"," -46"," -45"," -44"," -43",
" -42"," -41"," -40"," -39"," -38"," -37"," -36"," -35",
" -34"," -33"," -32"," -31"," -30"," -29"," -28"," -27",
" -26"," -25"," -24"," -23"," -22"," -21"," -20"," -19",
" -18"," -17"," -16"," -15"," -14"," -13"," -12"," -11",
" -10","  -9","  -8","  -7","  -6","  -5","  -4","  -3",
"  -2","  -1","   0","   1","   2","   3","   4","   5",
"   6","   7","   8","   9","  10","  11","  12","  13",
"  14","  15","  16","  17","  18","  19","  20","  21",
"  22","  23","  24","  25","  26","  27","  28","  29",
"  30","  31","  32","  33","  34","  35","  36","  37",
"  38","  39","  40","  41","  42","  43","  44","  45",
"  46","  47","  48","  49","  50"};


  static final String [] NoteKeyName        = new String [] {
                                                            "C-2","C#-2","D-2","D#-2","E-2","F-2","F#-2","G-2","G#-2","A-2","A#-2","B-2",
                                                            "C-1","C#-1","D-1","D#-1","E-1","F-1","F#-1","G-1","G#-1","A-1","A#-1","B-1",
                                                            "C0","C#0","D0","D#0","E0","F0","F#0","G0","G#0","A0","A#0","B0",
                                                            "C1","C#1","D1","D#1","E1","F1","F#1","G1","G#1","A1","A#1","B1",
                                                            "C2","C#2","D2","D#2","E2","F2","F#2","G2","G#2","A2","A#2","B2",
                                                            "C3","C#3","D3","D#3","E3","F3","F#3","G3","G#3","A3","A#3","B3",
                                                            "C4","C#4","D4","D#4","E4","F4","F#4","G4","G#4","A4","A#4","B4",
                                                            "C5","C#5","D5","D#5","E5","F5","F#5","G5","G#5","A5","A#5","B5",
                                                            "C6","C#6","D6","D#6","E6","F6","F#6","G6","G#6","A6","A#6","B6",
                                                            "C7","C#7","D7","D#7","E7","F7","F#7","G7","G#7","A7","A#7","B7",
                                                            "C8","C#8","D8","D#8","E8","F8","F#8","G8"};

  static final String [] VoiceName         = new String [] {
"Internal  0","Internal  1","Internal  2","Internal  3","Internal  4","Internal  5","Internal  6","Internal  7",
"Internal  8","Internal  9","Internal  10","Internal  11","Internal  12","Internal  13","Internal  14","Internal  15",
"Internal  16","Internal  17","Internal  18","Internal  19","Internal  20","Internal  21","Internal  22","Internal  23",
"Internal  24","Internal  25","Internal  26","Internal  27","Internal  28","Internal  29","Internal  30","Internal  31",
"Internal  32","Internal  33","Internal  34","Internal  35","Internal  36","Internal  37","Internal  38","Internal  39",
"Internal  40","Internal  41","Internal  42","Internal  43","Internal  44","Internal  45","Internal  46","Internal  47",
"Internal  48","Internal  49","Internal  50","Internal  51","Internal  52","Internal  53","Internal  54","Internal  55",
"Internal  56","Internal  57","Internal  58","Internal  59","Internal  60","Internal  61","Internal  62","Internal  63",
"Cartridge 64","Cartridge 65","Cartridge 66","Cartridge 67","Cartridge 68","Cartridge 69","Cartridge 70","Cartridge 71",
"Cartridge 72","Cartridge 73","Cartridge 74","Cartridge 75","Cartridge 76","Cartridge 77","Cartridge 78","Cartridge 79",
"Cartridge 80","Cartridge 81","Cartridge 82","Cartridge 83","Cartridge 84","Cartridge 85","Cartridge 86","Cartridge 87",
"Cartridge 88","Cartridge 89","Cartridge 90","Cartridge 91","Cartridge 92","Cartridge 93","Cartridge 94","Cartridge 95",
"Cartridge 96","Cartridge 97","Cartridge 98","Cartridge 99","Cartridge 100","Cartridge 101","Cartridge 102","Cartridge 103",
"Cartridge 104","Cartridge 105","Cartridge 106","Cartridge 107","Cartridge 108","Cartridge 109","Cartridge 110","Cartridge 111",
"Cartridge 112","Cartridge 113","Cartridge 114","Cartridge 115","Cartridge 116","Cartridge 117","Cartridge 118","Cartridge 119",
"Cartridge 120","Cartridge 121","Cartridge 122","Cartridge 123","Cartridge 124","Cartridge 125","Cartridge 126","Cartridge 127"};

  static final String [] NoteShiftName   = new String [] {
"-24","-23","-22","-21","-20","-19","-18","-17","-16","-15","-14","-13","-12","-11","-10","-9",
"-8","-7","-6","-5","-4","-3","-2","-1","0","1","2","3","4","5","6","7",
"8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23",
"24"};

  static final String [] MicroTableName = new String [] {
"Equal","Pure major","Pure minor","Mean tone","Pythagorean","Werckmeister","Kirnberger",
"Vallotti&Young","1/4 shifted equal","1/4 tone","1/8 tone","Internal 01","Internal 02",
"Cartridge C 1","Cartridge C 2","Cartridge C 3","Cartridge C 4","Cartridge C 5","Cartridge C 6","Cartridge C 7",
"Cartridge C 8","Cartridge C 9","Cartridge C10","Cartridge C11","Cartridge C12","Cartridge C13","Cartridge C14","Cartridge C15",
"Cartridge C16","Cartridge C17","Cartridge C18","Cartridge C19","Cartridge C20","Cartridge C21","Cartridge C22","Cartridge C23",
"Cartridge C24","Cartridge C25","Cartridge C26","Cartridge C27","Cartridge C28","Cartridge C29","Cartridge C30","Cartridge C31",
"Cartridge C32","Cartridge C33","Cartridge C34","Cartridge C35","Cartridge C36","Cartridge C37","Cartridge C38","Cartridge C39",
"Cartridge C40","Cartridge C41","Cartridge C42","Cartridge C43","Cartridge C44","Cartridge C45","Cartridge C46","Cartridge C47",
"Cartridge C48","Cartridge C49","Cartridge C50","Cartridge C51","Cartridge C52","Cartridge C53","Cartridge C54","Cartridge C55",
"Cartridge C56","Cartridge C57","Cartridge C58","Cartridge C59","Cartridge C60","Cartridge C61","Cartridge C62","Cartridge C63"};

  static final String [] MicroKeyName = new String [] {"C","C#","D","D#","E","F","F#","G","G#","A","A#","B"};

  static final String [] CSAssignName = new String [] {
"Off-Off ; Off-Off", " ON-Off ; Off-Off", "Off- ON ; Off-Off", " On- On ; Off-Off",
"Off-Off ; Off-Off", " ON-Off ;  ON-Off", "Off- ON ; Off- ON", " On- On ;  On- On",
};


  static final String [] CSParaName = new String [] {
"No Effects", "Total Volume", "Output Balance (A/B)", "PAN Select", "Dual Detune", "Algorithm", "Feedback Level",
"LFO - Wave", "LFO - Speed", "LFO - Delay", "LFO - PMS", "LFO - PMD", "LFO - AMD",
"Pitch EG - Rate 1", "Pitch EG - Rate 2", "Pitch EG - Rate 3", "Pitch EG - Rate 4",
"Pitch EG - Level 1", "Pitch EG - Level 2", "Pitch EG - Level 3", "Pitch EG - Level 4",
"Portamento time",
"Frequency coarse - OP 1", "Frequency coarse - OP 2", "Frequency coarse - OP 3", "Frequency coarse - OP 4", "Frequency coarse - OP 5", "Frequency coarse - OP 6", 
"Frequency fine - OP 1", "Frequency fine - OP 2", "Frequency fine - OP 3", "Frequency fine - OP 4", "Frequency fine - OP 5", "Frequency fine - OP 6",
"OSC. detune - OP 1", "OSC. detune - OP 2", "OSC. detune - OP 3", "OSC. detune - OP 4", "OSC. detune - OP 5", "OSC. detune - OP 6", 
"EG Rate 1 - OP 1", "EG Rate 1 - OP 2", "EG Rate 1 - OP 3", "EG Rate 1 - OP 4", "EG Rate 1 - OP 5", "EG Rate 1 - OP 6",
"EG Rate 2 - OP 1", "EG Rate 2 - OP 2", "EG Rate 2 - OP 3", "EG Rate 2 - OP 4", "EG Rate 2 - OP 5", "EG Rate 2 - OP 6",
"EG Rate 3 - OP 1", "EG Rate 3 - OP 2", "EG Rate 3 - OP 3", "EG Rate 3 - OP 4", "EG Rate 3 - OP 5", "EG Rate 3 - OP 6",
"EG Rate 4 - OP 1", "EG Rate 4 - OP 2", "EG Rate 4 - OP 3", "EG Rate 4 - OP 4", "EG Rate 4 - OP 5", "EG Rate 4 - OP 6",
"EG Level 1 - OP 1", "EG Level 1 - OP 2", "EG Level 1 - OP 3", "EG Level 1 - OP 4", "EG Level 1 - OP 5", "EG Level 1 - OP 6",
"EG Level 2 - OP 1", "EG Level 2 - OP 2", "EG Level 2 - OP 3", "EG Level 2 - OP 4", "EG Level 2 - OP 5", "EG Level 2 - OP 6",
"EG Level 3 - OP 1", "EG Level 3 - OP 2", "EG Level 3 - OP 3", "EG Level 3 - OP 4", "EG Level 3 - OP 5", "EG Level 3 - OP 6",
"EG Level 4 - OP 1", "EG Level 4 - OP 2", "EG Level 4 - OP 3", "EG Level 4 - OP 4", "EG Level 4 - OP 5", "EG Level 4 - OP 6",
"Key Velocity - OP 1", "Key Velocity - OP 2", "Key Velocity - OP 3", "Key Velocity - OP 4", "Key Velocity - OP 5", "Key Velocity - OP 6", 
"AMP MOD Sens - OP 1", "AMP MOD Sens - OP 2", "AMP MOD Sens - OP 3", "AMP MOD Sens - OP 4", "AMP MOD Sens - OP 5", "AMP MOD Sens - OP 6", 
"Total Level - OP 1", "Total Level - OP 2", "Total Level - OP 3", "Total Level - OP 4", "Total Level - OP 5", "Total Level - OP 6" 
 };


  public YamahaDX7sPerformanceEditor(Patch patch)
  {
    super ("Yamaha DX7s \"Performance\" Editor",patch);
    

    PatchEdit.waitDialog.show();        // Because it needs some time to build up the editor frame


    JPanel cmnPane = new JPanel();
    cmnPane.setLayout(new GridBagLayout());gbc.weightx=1;gbc.anchor=gbc.EAST;


    gbc.gridx=0;gbc.gridy=0;gbc.gridwidth=1;gbc.gridheight=1; cmnPane.add(new JLabel(" "),gbc);
    addWidget(cmnPane,new PatchNameWidget(patch,"Name (20 Char.)"), 0, 1,6, 1, 0);

    gbc.gridx=0;gbc.gridy=2;gbc.gridwidth=1;gbc.gridheight=1; cmnPane.add(new JLabel(" "),gbc);
    addWidget(cmnPane,new ComboBoxWidget("Voice Number - A"        ,patch,new ParamModel(patch,16+ 1),new PerformanceSender( 1),VoiceName)     ,0, 3,4,1, 2);
    addWidget(cmnPane,new ComboBoxWidget("Voice Number - B"        ,patch,new ParamModel(patch,16+ 2),new PerformanceSender( 2),VoiceName)     ,4, 3,4,1, 3);

    gbc.gridx=0;gbc.gridy=4;gbc.gridwidth=1;gbc.gridheight=1; cmnPane.add(new JLabel(" "),gbc);
    addWidget(cmnPane,new ComboBoxWidget("Total Volume"            ,patch,new ParamModel(patch,16+16),new PerformanceSender(16),VolumeName)    ,0, 5,4,1, 4);
    addWidget(cmnPane,new ComboBoxWidget("Balance"                 ,patch,new ParamModel(patch,16+15),new PerformanceSender(15),BalanceName)   ,4, 5,4,1, 5);
    addWidget(cmnPane,new ComboBoxWidget("Dual Detune Depth"       ,patch,new ParamModel(patch,16+ 6),new PerformanceSender( 6),DetuneName)    ,8, 5,4,1, 6);

    gbc.gridx=0;gbc.gridy=6;gbc.gridwidth=1;gbc.gridheight=1; cmnPane.add(new JLabel(" "),gbc);
    addWidget(cmnPane,new ComboBoxWidget("Voice Mode"              ,patch,new ParamModel(patch,16+ 0),new PerformanceSender( 0),VoiceModeName) ,0, 7,4,1, 7);
    addWidget(cmnPane,new ComboBoxWidget("Split Point"             ,patch,new ParamModel(patch,16+ 7),new PerformanceSender( 7),NoteKeyName)   ,4, 7,4,1, 8);
    addWidget(cmnPane,new ComboBoxWidget("EG Forced Damp"          ,patch,new ParamModel(patch,16+ 8),new PerformanceSender( 8),OnOffName)     ,8, 7,4,1, 9);

    gbc.gridx=0;gbc.gridy=8;gbc.gridwidth=1;gbc.gridheight=1; cmnPane.add(new JLabel(" "),gbc);
    gbc.gridx=0;gbc.gridy=9;gbc.gridwidth=1;gbc.gridheight=1; cmnPane.add(new JLabel("Note Shift Range for:"),gbc);
    addWidget(cmnPane,new ComboBoxWidget("Channel A (SemiTones)"   ,patch,new ParamModel(patch,16+13),new PerformanceSender(13),NoteShiftName) ,0,10,4,1,10);
    addWidget(cmnPane,new ComboBoxWidget("Channel B (SemiTones)"   ,patch,new ParamModel(patch,16+14),new PerformanceSender(14),NoteShiftName) ,4,10,4,1,11);

    gbc.gridx=0;gbc.gridy=11;gbc.gridwidth=1;gbc.gridheight=1; cmnPane.add(new JLabel(" "),gbc);
    gbc.gridx=0;gbc.gridy=12;gbc.gridwidth=1;gbc.gridheight=1; cmnPane.add(new JLabel("Footswitch Controllers:"),gbc);
    addWidget(cmnPane,new ComboBoxWidget("Sustain / FS 1 (A - B)"  ,patch,new ParamModel(patch,16+ 9),new PerformanceSender( 9),FSOnOffName)   ,0,13,4,1,12);
    addWidget(cmnPane,new ComboBoxWidget("FS 2 (A - B)"            ,patch,new ParamModel(patch,16+11),new PerformanceSender(11),FSOnOffName)   ,0,14,4,1,13);
    addWidget(cmnPane,new ComboBoxWidget("FS 2 Assign"             ,patch,new ParamModel(patch,16+10),new PerformanceSender(10),FSAssignName)  ,4,14,4,1,14);
    addWidget(cmnPane,new ComboBoxWidget("Soft Pedal Range"        ,patch,new ParamModel(patch,16+12),new PerformanceSender(12),DetuneName)    ,8,14,4,1,15);

    gbc.gridx=0;gbc.gridy=15;gbc.gridwidth=1;gbc.gridheight=1; cmnPane.add(new JLabel(" "),gbc);
    gbc.gridx=0;gbc.gridy=16;gbc.gridwidth=1;gbc.gridheight=1; cmnPane.add(new JLabel("Continuous Slider:"),gbc);
    addWidget(cmnPane,new ComboBoxWidget("CS Assign (A - B)"       ,patch,new ParamModel(patch,16+19),new PerformanceSender(19),CSAssignName)  ,0,17,4,1,16);
    addWidget(cmnPane,new ComboBoxWidget("CS 1"                    ,patch,new ParamModel(patch,16+17),new PerformanceSender(17),CSParaName)    ,0,18,4,1,17);
    addWidget(cmnPane,new ComboBoxWidget("CS 2"                    ,patch,new ParamModel(patch,16+18),new PerformanceSender(18),CSParaName)    ,4,18,4,1,18);

    gbc.gridx=0;gbc.gridy=19;gbc.gridwidth=1;gbc.gridheight=1; cmnPane.add(new JLabel(" "),gbc);
    gbc.gridx=0;gbc.gridy=20;gbc.gridwidth=1;gbc.gridheight=1; cmnPane.add(new JLabel("Micro Tuning:"),gbc);
    addWidget(cmnPane,new ComboBoxWidget("Switch \"A - B\""        ,patch,new ParamModel(patch,16+ 5),new PerformanceSender( 5),FSOnOffName)   ,0,21,4,1,19);
    addWidget(cmnPane,new ComboBoxWidget("Table Select"            ,patch,new ParamModel(patch,16+ 3),new PerformanceSender( 3),MicroTableName),4,21,4,1,20);
    addWidget(cmnPane,new ComboBoxWidget("Key Select"              ,patch,new ParamModel(patch,16+ 4),new PerformanceSender( 4),MicroKeyName)  ,8,21,4,1,21);

    gbc.gridx=0;gbc.gridy=22;gbc.gridwidth=1;gbc.gridheight=1; cmnPane.add(new JLabel(" "),gbc);
    gbc.gridx=0;gbc.gridy=23;gbc.gridwidth=1;gbc.gridheight=1; cmnPane.add(new JLabel("Pan(orama) Function:"),gbc);
    addWidget(cmnPane,new ComboBoxWidget("Mode \"A - B\""          ,patch,new ParamModel(patch,16+20),new PerformanceSender(20),PanModeName)   ,0,24,4,1,22);
    addWidget(cmnPane,new ComboBoxWidget("Control Range"           ,patch,new ParamModel(patch,16+21),new PerformanceSender(21),VolumeName)    ,4,24,4,1,23);
    addWidget(cmnPane,new ComboBoxWidget("Control Assign"          ,patch,new ParamModel(patch,16+22),new PerformanceSender(22),PanAssignName) ,8,24,4,1,24);

    gbc.gridx=0;gbc.gridy=24;gbc.gridwidth=1;gbc.gridheight=1; cmnPane.add(new JLabel(" "),gbc);
    addWidget(cmnPane,new EnvelopeWidget("Pan Envelope Generator",patch,new EnvelopeNode []
    {
      new EnvelopeNode(0,0,null,0,0,null,0,false,null,null,null,null),
      new EnvelopeNode(0,99,new ParamModel(patch,16+23),0,99,new ParamModel(patch,16+27),0,true,new PerformanceSender(23),new PerformanceSender(27),"R1","L1"),
      new EnvelopeNode(0,99,new ParamModel(patch,16+24),0,99,new ParamModel(patch,16+28),0,true,new PerformanceSender(24),new PerformanceSender(28),"R2","L2"),
      new EnvelopeNode(0,99,new ParamModel(patch,16+25),0,99,new ParamModel(patch,16+29),0,true,new PerformanceSender(25),new PerformanceSender(29),"R3","L3"),
      new EnvelopeNode(0,99,new ParamModel(patch,16+26),0,99,new ParamModel(patch,16+30),0,true,new PerformanceSender(26),new PerformanceSender(30),"R4","L4"),
    }),0,26,10,9,25);


    scrollPane.add(cmnPane,gbc);
    pack();
    show();

    PatchEdit.waitDialog.hide();        // Okay, the editor frame is ready
  }
}
