/*
 * JSynthlib - "Performance" Editor for Yamaha TX802
 * =================================================
 * @author  Torsten Tittmann
 * file:    YamahaTX802PerformanceEditor.java
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

package synthdrivers.YamahaTX802;
import core.*;
import java.lang.String.*;
import java.text.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;
import javax.swing.event.*;

class YamahaTX802PerformanceEditor extends PatchEditorFrame 
{
  static final String [] OnOffName          = new String [] {"Off","On"};

  static final String [] VoiChOfsName       = new String [] {"0","1","2","3","4","5","6","7"};

  static final String [] MIDIRcvChName       = new String [] {"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","OMNI on"};

  static final String [] VoiceNoName         = new String [] {
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
"Cartridge 120","Cartridge 121","Cartridge 122","Cartridge 123","Cartridge 124","Cartridge 125","Cartridge 126","Cartridge 127",
"Preset A  128","Preset A  129","Preset A  130","Preset A  131","Preset A  132","Preset A  133","Preset A  134","Preset A  135",
"Preset A  136","Preset A  137","Preset A  138","Preset A  139","Preset A  140","Preset A  141","Preset A  142","Preset A  143",
"Preset A  144","Preset A  145","Preset A  146","Preset A  147","Preset A  148","Preset A  149","Preset A  150","Preset A  151",
"Preset A  152","Preset A  153","Preset A  154","Preset A  155","Preset A  156","Preset A  157","Preset A  158","Preset A  159",
"Preset A  160","Preset A  161","Preset A  162","Preset A  163","Preset A  164","Preset A  165","Preset A  166","Preset A  167",
"Preset A  168","Preset A  169","Preset A  170","Preset A  171","Preset A  172","Preset A  173","Preset A  174","Preset A  175",
"Preset A  176","Preset A  177","Preset A  178","Preset A  179","Preset A  180","Preset A  181","Preset A  182","Preset A  183",
"Preset A  184","Preset A  185","Preset A  186","Preset A  187","Preset A  188","Preset A  189","Preset A  190","Preset A  191",
"Preset B  192","Preset B  193","Preset B  194","Preset B  195","Preset B  196","Preset B  197","Preset B  198","Preset B  199",
"Preset B  200","Preset B  201","Preset B  202","Preset B  203","Preset B  204","Preset B  205","Preset B  206","Preset B  207",
"Preset B  208","Preset B  209","Preset B  210","Preset B  211","Preset B  212","Preset B  213","Preset B  214","Preset B  215",
"Preset B  216","Preset B  217","Preset B  218","Preset B  219","Preset B  220","Preset B  221","Preset B  222","Preset B  223",
"Preset B  224","Preset B  225","Preset B  226","Preset B  227","Preset B  228","Preset B  229","Preset B  230","Preset B  231",
"Preset B  232","Preset B  233","Preset B  234","Preset B  235","Preset B  236","Preset B  237","Preset B  238","Preset B  239",
"Preset B  240","Preset B  241","Preset B  242","Preset B  243","Preset B  244","Preset B  245","Preset B  246","Preset B  247",
"Preset B  248","Preset B  249","Preset B  250","Preset B  251","Preset B  252","Preset B  253","Preset B  254","Preset B  255"
};

  static final String [] DetuneName       = new String [] {"-7","-6","-5","-4","-3","-2","-1","0","1","2","3","4","5","6","7"};

  static final String [] OutVolName       = new String [] {
"0","1","2","3","4","5","6","7","8","9","10","11","12","13","14","15",
"16","17","18","19","20","21","22","23","24","25","26","27","28","29","30","31",
"32","33","34","35","36","37","38","39","40","41","42","43","44","45","46","47",
"48","49","50","51","52","53","54","55","56","57","58","59","60","61","62","63",
"64","65","66","67","68","69","70","71","72","73","74","75","76","77","78","79",
"80","81","82","83","84","85","86","87","88","89","90","91","92","93","94","95",
"96","97","98","99"};

  static final String [] OutAssignName    = new String [] {"off","I","II","I+II"};

  static final String [] NoteLimitName    = new String [] {
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

  static final String [] NoteShiftName   = new String [] {
"-24","-23","-22","-21","-20","-19","-18","-17","-16","-15","-14","-13","-12","-11","-10","-9",
"-8","-7","-6","-5","-4","-3","-2","-1","0","1","2","3","4","5","6","7",
"8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23",
"24"};

  static final String [] AlternateAssignName = new String [] {"norm","alternate"};

  static final String [] MicroTuningName = new String [] {"Equal","Pure major","Pure minor","Mean tone","Pythagorean","Werckmeister","Kirnberger",
                                                          "Vallotti&Young","1/4 shifted equal","1/4 tone","1/8 tone","Internal 01","Internal 02",
"Cartridge C 1","Cartridge C 2","Cartridge C 3","Cartridge C 4","Cartridge C 5","Cartridge C 6","Cartridge C 7",
"Cartridge C 8","Cartridge C 9","Cartridge C10","Cartridge C11","Cartridge C12","Cartridge C13","Cartridge C14","Cartridge C15",
"Cartridge C16","Cartridge C17","Cartridge C18","Cartridge C19","Cartridge C20","Cartridge C21","Cartridge C22","Cartridge C23",
"Cartridge C24","Cartridge C25","Cartridge C26","Cartridge C27","Cartridge C28","Cartridge C29","Cartridge C30","Cartridge C31",
"Cartridge C32","Cartridge C33","Cartridge C34","Cartridge C35","Cartridge C36","Cartridge C37","Cartridge C38","Cartridge C39",
"Cartridge C40","Cartridge C41","Cartridge C42","Cartridge C43","Cartridge C44","Cartridge C45","Cartridge C46","Cartridge C47",
"Cartridge C48","Cartridge C49","Cartridge C50","Cartridge C51","Cartridge C52","Cartridge C53","Cartridge C54","Cartridge C55",
"Cartridge C56","Cartridge C57","Cartridge C58","Cartridge C59","Cartridge C60","Cartridge C61","Cartridge C62","Cartridge C63"};   

  public YamahaTX802PerformanceEditor(Patch patch)
  {
    super ("Yamaha TX802 \"Performance\" Editor",patch);

    JPanel perfPane=new JPanel();
    perfPane.setLayout(new GridBagLayout());gbc.weightx=0;

    gbc.gridx=0;gbc.gridy= 0;gbc.gridwidth=1;gbc.gridheight=1;perfPane.add(new JLabel(" "),gbc);
    addWidget(perfPane,new PatchNameWidget(patch,"Name (20 Char.)"), 0, 1,6, 1, 0);

    gbc.gridx=0;gbc.gridy= 2;gbc.gridwidth=1;gbc.gridheight=1;perfPane.add(new JLabel(" "),gbc);
    gbc.gridx=0;gbc.gridy= 3;gbc.gridwidth=3;gbc.gridheight=1;perfPane.add(new JLabel("Voice Channel offset"),gbc);
    addWidget(perfPane,new ComboBoxWidget("1",patch,new PerformanceModel(patch,  0),new PerformanceSender(  0),VoiChOfsName), 0, 4,2,1, 10);
    addWidget(perfPane,new ComboBoxWidget("2",patch,new PerformanceModel(patch,  1),new PerformanceSender(  1),VoiChOfsName), 2, 4,2,1, 11);
    addWidget(perfPane,new ComboBoxWidget("3",patch,new PerformanceModel(patch,  2),new PerformanceSender(  2),VoiChOfsName), 4, 4,2,1, 12);
    addWidget(perfPane,new ComboBoxWidget("4",patch,new PerformanceModel(patch,  3),new PerformanceSender(  3),VoiChOfsName), 6, 4,2,1, 13);
    addWidget(perfPane,new ComboBoxWidget("5",patch,new PerformanceModel(patch,  4),new PerformanceSender(  4),VoiChOfsName), 0, 5,2,1, 14);
    addWidget(perfPane,new ComboBoxWidget("6",patch,new PerformanceModel(patch,  5),new PerformanceSender(  5),VoiChOfsName), 2, 5,2,1, 15);
    addWidget(perfPane,new ComboBoxWidget("7",patch,new PerformanceModel(patch,  6),new PerformanceSender(  6),VoiChOfsName), 4, 5,2,1, 16);
    addWidget(perfPane,new ComboBoxWidget("8",patch,new PerformanceModel(patch,  7),new PerformanceSender(  7),VoiChOfsName), 6, 5,2,1, 17);

    gbc.gridx=0;gbc.gridy= 6;gbc.gridwidth=1;gbc.gridheight=1;perfPane.add(new JLabel(" "),gbc);
    gbc.gridx=0;gbc.gridy= 7;gbc.gridwidth=3;gbc.gridheight=1;perfPane.add(new JLabel("MIDI Receive Channel"),gbc);
    addWidget(perfPane,new ComboBoxWidget("1",patch,new PerformanceModel(patch,  8),new PerformanceSender(  8),MIDIRcvChName), 0, 8,2,1, 20);
    addWidget(perfPane,new ComboBoxWidget("2",patch,new PerformanceModel(patch,  9),new PerformanceSender(  9),MIDIRcvChName), 2, 8,2,1, 21);
    addWidget(perfPane,new ComboBoxWidget("3",patch,new PerformanceModel(patch, 10),new PerformanceSender( 10),MIDIRcvChName), 4, 8,2,1, 22);
    addWidget(perfPane,new ComboBoxWidget("4",patch,new PerformanceModel(patch, 11),new PerformanceSender( 11),MIDIRcvChName), 6, 8,2,1, 23);
    addWidget(perfPane,new ComboBoxWidget("5",patch,new PerformanceModel(patch, 12),new PerformanceSender( 12),MIDIRcvChName), 0, 9,2,1, 24);
    addWidget(perfPane,new ComboBoxWidget("6",patch,new PerformanceModel(patch, 13),new PerformanceSender( 13),MIDIRcvChName), 2, 9,2,1, 25);
    addWidget(perfPane,new ComboBoxWidget("7",patch,new PerformanceModel(patch, 14),new PerformanceSender( 14),MIDIRcvChName), 4, 9,2,1, 26);
    addWidget(perfPane,new ComboBoxWidget("8",patch,new PerformanceModel(patch, 15),new PerformanceSender( 15),MIDIRcvChName), 6, 9,2,1, 27);

    gbc.gridx=0;gbc.gridy=10;gbc.gridwidth=1;gbc.gridheight=1;perfPane.add(new JLabel(" "),gbc);
    gbc.gridx=0;gbc.gridy=11;gbc.gridwidth=3;gbc.gridheight=1;perfPane.add(new JLabel("Voice number (*)"),gbc);
    addWidget(perfPane,new ComboBoxWidget("1",patch,new PerformanceModel(patch, 16),null,VoiceNoName), 0,12,2,1, 30);
    addWidget(perfPane,new ComboBoxWidget("2",patch,new PerformanceModel(patch, 17),null,VoiceNoName), 2,12,2,1, 31);
    addWidget(perfPane,new ComboBoxWidget("3",patch,new PerformanceModel(patch, 18),null,VoiceNoName), 4,12,2,1, 32);
    addWidget(perfPane,new ComboBoxWidget("4",patch,new PerformanceModel(patch, 19),null,VoiceNoName), 6,12,2,1, 33);
    addWidget(perfPane,new ComboBoxWidget("5",patch,new PerformanceModel(patch, 20),null,VoiceNoName), 0,13,2,1, 34);
    addWidget(perfPane,new ComboBoxWidget("6",patch,new PerformanceModel(patch, 21),null,VoiceNoName), 2,13,2,1, 35);
    addWidget(perfPane,new ComboBoxWidget("7",patch,new PerformanceModel(patch, 22),null,VoiceNoName), 4,13,2,1, 36);
    addWidget(perfPane,new ComboBoxWidget("8",patch,new PerformanceModel(patch, 23),null,VoiceNoName), 6,13,2,1, 37);

    gbc.gridx=0;gbc.gridy=14;gbc.gridwidth=1;gbc.gridheight=1;perfPane.add(new JLabel(" "),gbc);
    gbc.gridx=0;gbc.gridy=15;gbc.gridwidth=3;gbc.gridheight=1;perfPane.add(new JLabel("Detune"),gbc);
    addWidget(perfPane,new ComboBoxWidget("1",patch,new PerformanceModel(patch, 24),new PerformanceSender( 24),DetuneName), 0,16,2,1, 40);
    addWidget(perfPane,new ComboBoxWidget("2",patch,new PerformanceModel(patch, 25),new PerformanceSender( 25),DetuneName), 2,16,2,1, 41);
    addWidget(perfPane,new ComboBoxWidget("3",patch,new PerformanceModel(patch, 26),new PerformanceSender( 26),DetuneName), 4,16,2,1, 42);
    addWidget(perfPane,new ComboBoxWidget("4",patch,new PerformanceModel(patch, 27),new PerformanceSender( 27),DetuneName), 6,16,2,1, 43);
    addWidget(perfPane,new ComboBoxWidget("5",patch,new PerformanceModel(patch, 28),new PerformanceSender( 28),DetuneName), 0,17,2,1, 44);
    addWidget(perfPane,new ComboBoxWidget("6",patch,new PerformanceModel(patch, 29),new PerformanceSender( 29),DetuneName), 2,17,2,1, 45);
    addWidget(perfPane,new ComboBoxWidget("7",patch,new PerformanceModel(patch, 30),new PerformanceSender( 30),DetuneName), 4,17,2,1, 46);
    addWidget(perfPane,new ComboBoxWidget("8",patch,new PerformanceModel(patch, 31),new PerformanceSender( 31),DetuneName), 6,17,2,1, 47);

    gbc.gridx=0;gbc.gridy=18;gbc.gridwidth=1;gbc.gridheight=1;perfPane.add(new JLabel(" "),gbc);
    gbc.gridx=0;gbc.gridy=19;gbc.gridwidth=3;gbc.gridheight=1;perfPane.add(new JLabel("Output Volume"),gbc);
    addWidget(perfPane,new ComboBoxWidget("1",patch,new PerformanceModel(patch, 32),new PerformanceSender( 32),OutVolName), 0,20,2,1, 50);
    addWidget(perfPane,new ComboBoxWidget("2",patch,new PerformanceModel(patch, 33),new PerformanceSender( 33),OutVolName), 2,20,2,1, 51);
    addWidget(perfPane,new ComboBoxWidget("3",patch,new PerformanceModel(patch, 34),new PerformanceSender( 34),OutVolName), 4,20,2,1, 52);
    addWidget(perfPane,new ComboBoxWidget("4",patch,new PerformanceModel(patch, 35),new PerformanceSender( 35),OutVolName), 6,20,2,1, 53);
    addWidget(perfPane,new ComboBoxWidget("5",patch,new PerformanceModel(patch, 36),new PerformanceSender( 36),OutVolName), 0,21,2,1, 54);
    addWidget(perfPane,new ComboBoxWidget("6",patch,new PerformanceModel(patch, 37),new PerformanceSender( 37),OutVolName), 2,21,2,1, 55);
    addWidget(perfPane,new ComboBoxWidget("7",patch,new PerformanceModel(patch, 38),new PerformanceSender( 38),OutVolName), 4,21,2,1, 56);
    addWidget(perfPane,new ComboBoxWidget("8",patch,new PerformanceModel(patch, 39),new PerformanceSender( 39),OutVolName), 6,21,2,1, 57);

    gbc.gridx=0;gbc.gridy=22;gbc.gridwidth=1;gbc.gridheight=1;perfPane.add(new JLabel(" "),gbc);
    gbc.gridx=0;gbc.gridy=23;gbc.gridwidth=3;gbc.gridheight=1;perfPane.add(new JLabel("Output Assign"),gbc);
    addWidget(perfPane,new ComboBoxWidget("1",patch,new PerformanceModel(patch, 40),new PerformanceSender( 40),OutAssignName), 0,24,2,1, 60);
    addWidget(perfPane,new ComboBoxWidget("2",patch,new PerformanceModel(patch, 41),new PerformanceSender( 41),OutAssignName), 2,24,2,1, 61);
    addWidget(perfPane,new ComboBoxWidget("3",patch,new PerformanceModel(patch, 42),new PerformanceSender( 42),OutAssignName), 4,24,2,1, 62);
    addWidget(perfPane,new ComboBoxWidget("4",patch,new PerformanceModel(patch, 43),new PerformanceSender( 43),OutAssignName), 6,24,2,1, 63);
    addWidget(perfPane,new ComboBoxWidget("5",patch,new PerformanceModel(patch, 44),new PerformanceSender( 44),OutAssignName), 0,25,2,1, 64);
    addWidget(perfPane,new ComboBoxWidget("6",patch,new PerformanceModel(patch, 45),new PerformanceSender( 45),OutAssignName), 2,25,2,1, 65);
    addWidget(perfPane,new ComboBoxWidget("7",patch,new PerformanceModel(patch, 46),new PerformanceSender( 46),OutAssignName), 4,25,2,1, 66);
    addWidget(perfPane,new ComboBoxWidget("8",patch,new PerformanceModel(patch, 47),new PerformanceSender( 47),OutAssignName), 6,25,2,1, 67);

    gbc.gridx=0;gbc.gridy=26;gbc.gridwidth=1;gbc.gridheight=1;perfPane.add(new JLabel(" "),gbc);
    gbc.gridx=0;gbc.gridy=27;gbc.gridwidth=3;gbc.gridheight=1;perfPane.add(new JLabel("Note limit low"),gbc);
    addWidget(perfPane,new ComboBoxWidget("1",patch,new PerformanceModel(patch, 48),new PerformanceSender( 48),NoteLimitName), 0,28,2,1, 70);
    addWidget(perfPane,new ComboBoxWidget("2",patch,new PerformanceModel(patch, 49),new PerformanceSender( 49),NoteLimitName), 2,28,2,1, 71);
    addWidget(perfPane,new ComboBoxWidget("3",patch,new PerformanceModel(patch, 50),new PerformanceSender( 50),NoteLimitName), 4,28,2,1, 72);
    addWidget(perfPane,new ComboBoxWidget("4",patch,new PerformanceModel(patch, 51),new PerformanceSender( 51),NoteLimitName), 6,28,2,1, 73);
    addWidget(perfPane,new ComboBoxWidget("5",patch,new PerformanceModel(patch, 52),new PerformanceSender( 52),NoteLimitName), 0,29,2,1, 74);
    addWidget(perfPane,new ComboBoxWidget("6",patch,new PerformanceModel(patch, 53),new PerformanceSender( 53),NoteLimitName), 2,29,2,1, 75);
    addWidget(perfPane,new ComboBoxWidget("7",patch,new PerformanceModel(patch, 54),new PerformanceSender( 54),NoteLimitName), 4,29,2,1, 76);
    addWidget(perfPane,new ComboBoxWidget("8",patch,new PerformanceModel(patch, 55),new PerformanceSender( 55),NoteLimitName), 6,29,2,1, 77);

    gbc.gridx=0;gbc.gridy=30;gbc.gridwidth=1;gbc.gridheight=1;perfPane.add(new JLabel(" "),gbc);
    gbc.gridx=0;gbc.gridy=31;gbc.gridwidth=3;gbc.gridheight=1;perfPane.add(new JLabel("Note limit high"),gbc);
    addWidget(perfPane,new ComboBoxWidget("1",patch,new PerformanceModel(patch, 56),new PerformanceSender( 56),NoteLimitName), 0,32,2,1, 80);
    addWidget(perfPane,new ComboBoxWidget("2",patch,new PerformanceModel(patch, 57),new PerformanceSender( 57),NoteLimitName), 2,32,2,1, 81);
    addWidget(perfPane,new ComboBoxWidget("3",patch,new PerformanceModel(patch, 58),new PerformanceSender( 58),NoteLimitName), 4,32,2,1, 82);
    addWidget(perfPane,new ComboBoxWidget("4",patch,new PerformanceModel(patch, 59),new PerformanceSender( 59),NoteLimitName), 6,32,2,1, 83);
    addWidget(perfPane,new ComboBoxWidget("5",patch,new PerformanceModel(patch, 60),new PerformanceSender( 60),NoteLimitName), 0,33,2,1, 84);
    addWidget(perfPane,new ComboBoxWidget("6",patch,new PerformanceModel(patch, 61),new PerformanceSender( 61),NoteLimitName), 2,33,2,1, 85);
    addWidget(perfPane,new ComboBoxWidget("7",patch,new PerformanceModel(patch, 62),new PerformanceSender( 62),NoteLimitName), 4,33,2,1, 86);
    addWidget(perfPane,new ComboBoxWidget("8",patch,new PerformanceModel(patch, 63),new PerformanceSender( 63),NoteLimitName), 6,33,2,1, 87);

    gbc.gridx=0;gbc.gridy=34;gbc.gridwidth=1;gbc.gridheight=1;perfPane.add(new JLabel(" "),gbc);
    gbc.gridx=0;gbc.gridy=35;gbc.gridwidth=3;gbc.gridheight=1;perfPane.add(new JLabel("Note shift"),gbc);
    addWidget(perfPane,new ComboBoxWidget("1",patch,new PerformanceModel(patch, 64),new PerformanceSender( 64),NoteShiftName), 0,36,2,1, 90);
    addWidget(perfPane,new ComboBoxWidget("2",patch,new PerformanceModel(patch, 65),new PerformanceSender( 65),NoteShiftName), 2,36,2,1, 91);
    addWidget(perfPane,new ComboBoxWidget("3",patch,new PerformanceModel(patch, 66),new PerformanceSender( 66),NoteShiftName), 4,36,2,1, 92);
    addWidget(perfPane,new ComboBoxWidget("4",patch,new PerformanceModel(patch, 67),new PerformanceSender( 67),NoteShiftName), 6,36,2,1, 93);
    addWidget(perfPane,new ComboBoxWidget("5",patch,new PerformanceModel(patch, 68),new PerformanceSender( 68),NoteShiftName), 0,37,2,1, 94);
    addWidget(perfPane,new ComboBoxWidget("6",patch,new PerformanceModel(patch, 69),new PerformanceSender( 69),NoteShiftName), 2,37,2,1, 95);
    addWidget(perfPane,new ComboBoxWidget("7",patch,new PerformanceModel(patch, 70),new PerformanceSender( 70),NoteShiftName), 4,37,2,1, 96);
    addWidget(perfPane,new ComboBoxWidget("8",patch,new PerformanceModel(patch, 71),new PerformanceSender( 71),NoteShiftName), 6,37,2,1, 97);

    gbc.gridx=0;gbc.gridy=38;gbc.gridwidth=1;gbc.gridheight=1;perfPane.add(new JLabel(" "),gbc);
    gbc.gridx=0;gbc.gridy=39;gbc.gridwidth=3;gbc.gridheight=1;perfPane.add(new JLabel("EG forced damp"),gbc);
    addWidget(perfPane,new ComboBoxWidget("1",patch,new PerformanceModel(patch, 72),new PerformanceSender( 72),OnOffName), 0,40,2,1,100);
    addWidget(perfPane,new ComboBoxWidget("2",patch,new PerformanceModel(patch, 73),new PerformanceSender( 73),OnOffName), 2,40,2,1,101);
    addWidget(perfPane,new ComboBoxWidget("3",patch,new PerformanceModel(patch, 74),new PerformanceSender( 74),OnOffName), 4,40,2,1,102);
    addWidget(perfPane,new ComboBoxWidget("4",patch,new PerformanceModel(patch, 75),new PerformanceSender( 75),OnOffName), 6,40,2,1,103);
    addWidget(perfPane,new ComboBoxWidget("5",patch,new PerformanceModel(patch, 76),new PerformanceSender( 76),OnOffName), 0,41,2,1,104);
    addWidget(perfPane,new ComboBoxWidget("6",patch,new PerformanceModel(patch, 77),new PerformanceSender( 77),OnOffName), 2,41,2,1,105);
    addWidget(perfPane,new ComboBoxWidget("7",patch,new PerformanceModel(patch, 78),new PerformanceSender( 78),OnOffName), 4,41,2,1,106);
    addWidget(perfPane,new ComboBoxWidget("8",patch,new PerformanceModel(patch, 79),new PerformanceSender( 79),OnOffName), 6,41,2,1,107);

    gbc.gridx=0;gbc.gridy=42;gbc.gridwidth=1;gbc.gridheight=1;perfPane.add(new JLabel(" "),gbc);
    gbc.gridx=0;gbc.gridy=43;gbc.gridwidth=3;gbc.gridheight=1;perfPane.add(new JLabel("Key assign group"),gbc);
    addWidget(perfPane,new ComboBoxWidget("1",patch,new PerformanceModel(patch, 80),new PerformanceSender( 80),AlternateAssignName), 0,44,2,1,110);
    addWidget(perfPane,new ComboBoxWidget("2",patch,new PerformanceModel(patch, 81),new PerformanceSender( 81),AlternateAssignName), 2,44,2,1,111);
    addWidget(perfPane,new ComboBoxWidget("3",patch,new PerformanceModel(patch, 82),new PerformanceSender( 82),AlternateAssignName), 4,44,2,1,112);
    addWidget(perfPane,new ComboBoxWidget("4",patch,new PerformanceModel(patch, 83),new PerformanceSender( 83),AlternateAssignName), 6,44,2,1,113);
    addWidget(perfPane,new ComboBoxWidget("5",patch,new PerformanceModel(patch, 84),new PerformanceSender( 84),AlternateAssignName), 0,45,2,1,114);
    addWidget(perfPane,new ComboBoxWidget("6",patch,new PerformanceModel(patch, 85),new PerformanceSender( 85),AlternateAssignName), 2,45,2,1,115);
    addWidget(perfPane,new ComboBoxWidget("7",patch,new PerformanceModel(patch, 86),new PerformanceSender( 86),AlternateAssignName), 4,45,2,1,116);
    addWidget(perfPane,new ComboBoxWidget("8",patch,new PerformanceModel(patch, 87),new PerformanceSender( 87),AlternateAssignName), 6,45,2,1,117);
 
    gbc.gridx=0;gbc.gridy=46;gbc.gridwidth=1;gbc.gridheight=1;perfPane.add(new JLabel(" "),gbc);
    gbc.gridx=0;gbc.gridy=47;gbc.gridwidth=3;gbc.gridheight=1;perfPane.add(new JLabel("Micro tuning table (*), (**)"),gbc);
    addWidget(perfPane,new ComboBoxWidget("1",patch,new PerformanceModel(patch, 88),null,MicroTuningName), 0,48,2,1,120);
    addWidget(perfPane,new ComboBoxWidget("2",patch,new PerformanceModel(patch, 89),null,MicroTuningName), 2,48,2,1,121);
    addWidget(perfPane,new ComboBoxWidget("3",patch,new PerformanceModel(patch, 90),null,MicroTuningName), 4,48,2,1,122);
    addWidget(perfPane,new ComboBoxWidget("4",patch,new PerformanceModel(patch, 91),null,MicroTuningName), 6,48,2,1,123);
    addWidget(perfPane,new ComboBoxWidget("5",patch,new PerformanceModel(patch, 92),null,MicroTuningName), 0,49,2,1,124);
    addWidget(perfPane,new ComboBoxWidget("6",patch,new PerformanceModel(patch, 93),null,MicroTuningName), 2,49,2,1,125);
    addWidget(perfPane,new ComboBoxWidget("7",patch,new PerformanceModel(patch, 94),null,MicroTuningName), 4,49,2,1,126);
    addWidget(perfPane,new ComboBoxWidget("8",patch,new PerformanceModel(patch, 95),null,MicroTuningName), 6,49,2,1,127);

    gbc.gridx=0;gbc.gridy=50;gbc.gridwidth=1;gbc.gridheight=1;perfPane.add(new JLabel(" "),gbc);
    gbc.gridx=0;gbc.gridy=51;gbc.gridwidth=6;gbc.gridheight=1;
    perfPane.add(new JLabel("(*) No Parameter Change Message yet!"),gbc);
    gbc.gridx=0;gbc.gridy=52;gbc.gridwidth=6;gbc.gridheight=1;
    perfPane.add(new JLabel("(**) Micro Tuning assignment probably faulty!"),gbc);


    gbc.gridx=0;gbc.gridy=1;gbc.gridwidth=1;gbc.gridheight=1;
    scrollPane.add(perfPane,gbc);
    pack();
    show();
  }
}
