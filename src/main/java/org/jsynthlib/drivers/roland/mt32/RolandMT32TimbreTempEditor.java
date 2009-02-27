/*
 * Copyright 2004,2005 Fred Jan Kraan
 *
 * This file is part of JSynthLib.
 *
 * JSynthLib is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
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

/**
 * Timbre Temp Editor for Roland MT32.
 *
 * @version $Id$
 */

package org.jsynthlib.drivers.roland.mt32;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.jsynthlib.core.CheckBoxWidget;
import org.jsynthlib.core.ComboBoxWidget;
import org.jsynthlib.core.EnvelopeWidget;
import org.jsynthlib.core.ErrorMsg;
import org.jsynthlib.core.Patch;
import org.jsynthlib.core.PatchEditorFrame;
import org.jsynthlib.core.PatchNameWidget;
import org.jsynthlib.core.ScrollBarWidget;


class RolandMT32TimbreTempEditor extends PatchEditorFrame {
    
    ImageIcon algoIcon[]=new ImageIcon[13];
    
    private final String[] noteName = new String[] {
	"C1", "C#1", "D1", "D#1", "E1", "F1", "F#1", "G1", "G#1", "A1", "A#1", "B1",
	"C2", "C#2", "D2", "D#2", "E2", "F2", "F#2", "G2", "G#2", "A2", "A#2", "B2",
	"C3", "C#3", "D3", "D#3", "E3", "F3", "F#3", "G3", "G#3", "A3", "A#3", "B3",
	"C4", "C#4", "D4", "D#4", "E4", "F4", "F#4", "G4", "G#4", "A4", "A#4", "B4",
	"C5", "C#5", "D5", "D#5", "E5", "F5", "F#5", "G5", "G#5", "A5", "A#5", "B5",
	"C6", "C#6", "D6", "D#6", "E6", "F6", "F#6", "G6", "G#6", "A6", "A#6", "B6",
	"C7" 
    };

    final String [] pKeyFollow = new String [] {"-1",  "-1/2", "-1/4", "0", "1/8", "1/4", "3/8", "1/2",
                                                "5/8", "3/4",  "7/8",  "1", "5/4", "3/2", "2",   "s1",  "s2" 
    };
    final String [] tKeyFollow = new String [] {"-1", "-1/2","-1/4","0","1/8","1/4","3/8","1/2",
                                                "5/8","3/4", "7/8", "1","5/4","3/2","2" 
    };
                                            
    final String [] waveName  = new String [] {"Square", "Saw"
    };
 
    final String [] biasPoint = new String [] {"<A1","<A#1","<B1","<C2","<C#2","<D2",
                                               "<D#2","<E2","<F2","<F#2","<G2","<G#2",
                                               "<A2","<A#2","<B2","<C3","<C#3","<D3",
                                               "<D#3","<E3","<F3","<F#3","<G3","<G#3",
                                               "<A3","<A#3","<B3","<C4","<C#4","<D4",
                                               "<D#4","<E4","<F4","<F#4","<G4","<G#4",
                                               "<A4","<A#4","<B4","<C5","<C#5","<D5",
                                               "<D#5","<E5","<F5","<F#5","<G5","<G#5",
                                               "<A5","<A#5","<B5","<C6","<C#6","<D6",
                                               "<D#6","<E6","<F6","<F#6","<G6","<G#6",
                                               "<A6","<A#6","<B6","<C7",
                                               ">A1",">A#1",">B1",">C2",">C#2",">D2",
                                               ">D#2",">E2",">F2",">F#2",">G2",">G#2",
                                               ">A2",">A#2",">B2",">C3",">C#3",">D3",
                                               ">D#3",">E3",">F3",">F#3",">G3",">G#3",
                                               ">A3",">A#3",">B3",">C4",">C#4",">D4",
                                               ">D#4",">E4",">F4",">F#4",">G4",">G#4",
                                               ">A4",">A#4",">B4",">C5",">C#5",">D5",
                                               ">D#5",">E5",">F5",">F#5",">5",">G#5",
                                               ">A5",">A#5",">B5",">C6",">C#6",">D6",
                                               ">D#6",">E6",">F6",">F#6",">G6",">G#6",
                                               ">A6",">A#6",">B6",">C7" 
    };
    
    final String [] WGPCMWAVE = new String [] {
        "  0 Bass Drum", "  1 Snare Drum", "  2 Elec.Snare Drum", "  3 Acou.Tom", 
        "  4 Closed HiHat", "  5 Open HiHat", "  6 Closed HiHat", "  7 Open HiHat",
        "  8 Closed HiHat", "  9 Rim Shot", " 10 HandClap", " 11 Bongo High", 
        " 12 Bongo", " 13 Bongo ", " 14 Cow Bell", " 15 Tambourine",
        " 16 Cow Bell", " 17 Claves", " 18 Timbale", " 19 Casaba", 
        " 20 Tick", " 21 Piano Attack", " 22 Trumpet Att Low", " 23 Trumpet Att High",
        " 24 Space Noise Att", " 25 Organ Attack", " 26 Organ Attack", " 27 Flute Attack",
        " 28 Traverse Attack", " 29 Strings Attack", " 30 Tuba Attack", "31 Claves Attack",
        " 32 Xylophone Att", " 33 Hit Attack", " 34 Tubes Attack", " 35 Trumpet Attack",
        " 36 Strings Attack", " 37 Strings Maintain", " 38 Strings Attack", " 39 String Att Decay1",
        " 40 String Att Decay2", " 41 String Att Decay3", " 42 String Att Decay4", " 43 Strings Maintain",
        " 44 Bow Attack 1", " 45 Bow Attack 2", " 46 Kettle Drum" ," 47 Kettle & String",
        " 48 Bowed Attack", " 49 Organ", " 50 Tubes High Loop", " 51 Phone Ring Loop",
        " 52 Tubes Low Loop", " 53 Space Noise Loop", " 54 Base Drum", " 55 Snare Drum",
        " 56 Snare Drum", " 57 Floor Tom", " 58 Closed HiHat", " 59 Open HiHat Cont.",
        " 60 Closed HiHat", " 61 Open HiHat Cont.", " 62 Closed HiHat", " 63 Rim Shot",
        " 64 Hand Clap", " 65 ", " 66 ", " 67 ",
        " 68 Cow Bell Attack", " 69 ", " 70 ", " 71 ", 
        " 72 ", " 73 ", " 74 BassD Loop", " 75 Snare Loop",
        " 76 Elec.Snare Loop", " 77 Tom Loop", " 78 HiHat Loop", " 79 Loop", 
        " 80 Loop", " 81 Loop", " 82 Loop", " 83 Loop", 
        " 84 ", " 85 ", " 86 ", " 87 ", 
        " 88 ", " 89 ", " 90 Clave Attack Loop", " 91 Loop", 
        " 92  Loop", " 93  Loop", " 94  Loop", " 95  Loop", 
        " 96  Loop", " 97  Loop", " 98  Loop",  " 99 Loop",
        "100 Loop", "101 Loop", "102 Loop", "103 Loop", 
        "104 Loop", "105 Loop", "106 Loop", "107 Loop", 
        "108 Loop", "109 Loop", "110 Loop", "111 Loop", 
        "112 Loop", "113 Loop", "114 Loop", "115 Loop", 
        "116 Loop", "117 Loop", "118 Loop", "119 Loop", 
        "120 Loop", "121 Loop", "122 Loop", "123 Loop", 
        "124 Loop", "125 Loop", "126 Loop", "127 BassD/Snare Loop"
    };
    /* 'S' is Synth, 'P' is PCM sample, 'R' is reverb
     * First is partial 1 or 3, second is partial 2 or 4
     * '-' is mono mix, '=' is each partial own channel
     * +P and +S means the first partial (1 or 3) is also bypassing
     * the reverb
     */
    final String [] PartMuteName = new String [] { "",    "1",     "2",     "1,2", 
    		                                       "3",   "1,2",   "1,3",   "1,2,3",
                                                   "4",   "1,4",   "2,4",   "1,2,4", 
                                                   "3,4", "1,2,4", "2,3,4", "1,2,3,4"
    };

//    final String [] PartStrName = new String [] { "S,S -",     "S,S -R +S", "P,S -",     "P,S -R +P",
//                                                  "S,P -R +S", "P,P -",     "P,P -R +P", "S,S =",
//                                                  "P,P =",     "S,S -R",    "P,S -R",    "S,P -R",
//                                                  "P,P -R"
//    };
    
    public RolandMT32TimbreTempEditor(Patch patch) {
	super ("Roland MT-32 Timbre Temp Editor", patch);
        
        algoIcon[0] = new ImageIcon(getClass().getResource("01_ss-.gif"));
        algoIcon[1] = new ImageIcon(getClass().getResource("02_ss-r+s.gif"));
        algoIcon[2] = new ImageIcon(getClass().getResource("03_ps-.gif"));
        algoIcon[3] = new ImageIcon(getClass().getResource("04_ps-r+p.gif"));
        algoIcon[4] = new ImageIcon(getClass().getResource("05_sp-r+s.gif"));
        algoIcon[5] = new ImageIcon(getClass().getResource("06_pp-.gif"));
        algoIcon[6] = new ImageIcon(getClass().getResource("07_pp-r+p.gif"));
        algoIcon[7] = new ImageIcon(getClass().getResource("08_ss_-.gif"));
        algoIcon[8] = new ImageIcon(getClass().getResource("09_pp_-.gif"));
        algoIcon[9] = new ImageIcon(getClass().getResource("10_ss-r.gif"));
        algoIcon[10]= new ImageIcon(getClass().getResource("11_ps-r.gif"));
        algoIcon[11]= new ImageIcon(getClass().getResource("12_sp-r.gif"));
        algoIcon[12]= new ImageIcon(getClass().getResource("13_pp-r.gif"));
        int algoVal12 = patch.sysex[0x0A];
        if (algoVal12 > 12 || algoVal12 < 0) { 
            algoVal12 = 0; 
        }
        final JLabel structImg12=new JLabel(algoIcon[algoVal12]);
        int algoVal34 = patch.sysex[0x0B];
        if (algoVal34 > 12 || algoVal34 < 0) { 
            algoVal34 = 0; 
        }
        final JLabel structImg34=new JLabel(algoIcon[algoVal34]);
        
        MT32Model TTAModH = new MT32Model(patch,-3);
        MT32Model TTAModM = new MT32Model(patch,-2);
        MT32Model TTAModL = new MT32Model(patch,-1);
        int TTAAddrH = TTAModH.get();
        int TTAAddrM = TTAModM.get();
        int TTAAddrL = TTAModL.get();
        
        ErrorMsg.reportStatus("Timbre source address: " + TTAAddrH + " / " + TTAAddrM + " / " + TTAAddrL);

//      Common Pane
        gbc.weightx=5;
        int gx = 1;  // column count
        int gy = 0;  // row count
        int k = TTAAddrL + (TTAAddrM * 0x80);  // timbre address offset. 
        int basad = TTAAddrH;
        int lwc = getLabelWidth("Partial 3&4 Structure ");  // Longest label length
        JPanel cmnPane=new JPanel();
        cmnPane.setLayout(new GridBagLayout());	 
        gbc.weightx=0;   
        gbc.gridx=2;gbc.gridy=6;gbc.gridwidth=1;gbc.gridheight=1;

        addWidget(cmnPane, new PatchNameWidget(" Name  ", patch), gx, gy, 2, 1, 0);
        gy++;
        final ComboBoxWidget struct12 = new ComboBoxWidget("Partial 1&2 Structure", patch, 0,
                new MT32Model(patch, 0x0A), new MT32Sender(k+0x0A, basad), algoIcon);
        addWidget(cmnPane,struct12,gx,gy,3,1,11);

        gy++;

        final ComboBoxWidget struct34 = new ComboBoxWidget("Partial 3&4 Structure", patch, 0,
                new MT32Model(patch, 0x0A), new MT32Sender(k+0x0A, basad), algoIcon);
        addWidget(cmnPane,struct34,gx,gy,3,1,11);;

        gy++;
        addWidget(cmnPane, new ComboBoxWidget("Partial On", patch, 
                new MT32Model(patch,0x0C), new MT32Sender(k+0x0C, basad), 
                PartMuteName), gx, gy, 1, 1, 11); 
        gy++;
        addWidget(cmnPane, new CheckBoxWidget ("Env Mode", patch, 
            new MT32Model(patch,0x0D), new MT32Sender(k+0x0D, basad)),gx,gy,5,1,4);   

        gbc.gridx=0;gbc.gridy=0;gbc.gridwidth=1;gbc.gridheight=4;

        gbc.gridx=0; gbc.gridy=0; gbc.gridwidth=5; gbc.gridheight=3;
        gbc.fill=GridBagConstraints.BOTH;
        gbc.anchor=GridBagConstraints.EAST;
        cmnPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
            "COMMON",TitledBorder.CENTER, TitledBorder.CENTER));  
        scrollPane.add(cmnPane,gbc);
    
        gbc.weightx = 5;
        JPanel partialPane = new JPanel();
        partialPane.setLayout(new GridBagLayout());
        
        JTabbedPane partTabPane = new JTabbedPane();
   
        for (int i=0; i<4; i++)
        {
            int j = i * 0x3A + 0x0E;  // Point to partial parameters 
            gy = 0;
             // Partial panel
            JPanel partNPane = new JPanel();
      
            int lww = getLabelWidth("WG PW VELO SENS  "); // Longest label lenght
            JTabbedPane partNTabPane = new JTabbedPane();
            
            // WG panel on partial panel
            JPanel WGPane = new JPanel();
            WGPane.setLayout(new GridBagLayout());

            addWidget(WGPane, new ComboBoxWidget("WG PITCH COARSE", patch, 
                new MT32Model(patch,j+0x00), new MT32Sender(j+k+0x00, basad), noteName), 0, gy, 1, 1, 11);
            gy++;
            addWidget(WGPane, new ScrollBarWidget("WG PITCH FINE", patch, 0, 100, -50, lww, 
                new MT32Model(patch,j+0x01), new MT32Sender(j+k+0x01, basad)),0,gy,3,1,12);
            gy++;
            addWidget(WGPane,new ComboBoxWidget("WG PITCH FOLLOW", patch, 
                new MT32Model(patch,j+0x02), new MT32Sender(j+k+0x02, basad), pKeyFollow), 0, gy, 1, 1, 13);
            gy++;
            addWidget(WGPane,new CheckBoxWidget("WG PITCH BENDER SW", patch, 
                new MT32Model(patch,j+0x03), new MT32Sender(j+k+0x03, basad)),1,gy,1,1,14);   
            gy++;
            addWidget(WGPane,new ComboBoxWidget("WG WAVEFORM", patch, 
                new MT32Model(patch,j+0x04), new MT32Sender(j+k+0x04, basad),waveName), 0, gy, 1, 1, 15);
            gy++;
            addWidget(WGPane,new ComboBoxWidget("WG PCM WAVE #", patch, 
                new MT32Model(patch,j+0x05), new MT32Sender(j+k+0x05, basad),WGPCMWAVE), 0, gy, 1, 1, 15);
            gy++;
            addWidget(WGPane,new ScrollBarWidget("WG PULSE WIDTH", patch, 0, 100, 0, lww,
                new MT32Model(patch,j+0x06), new MT32Sender(j+k+0x06, basad)), 0, gy, 3, 1, 17);
            gy++;
            addWidget(WGPane,new ScrollBarWidget("WG PW VELO SENS", patch, 0, 14, -7, lww,
                new MT32Model(patch,j+0x07), new MT32Sender(j+k+0x07, basad)), 0, gy, 3, 1, 18);

            WGPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
                "",TitledBorder.CENTER,TitledBorder.CENTER));
            partNTabPane.add(WGPane, gbc);
            partNTabPane.addTab("Wave Generator", WGPane);
            
            // P_ENV panel on partial panel
            JPanel PEPane = new JPanel();
            PEPane.setLayout(new GridBagLayout());

            gy = 0;
            addWidget(PEPane,new ScrollBarWidget("P-ENV DEPTH", patch, 0, 10, 0, lww,
                new MT32Model(patch,j+0x08), new MT32Sender(j+k+0x08, basad)), 0, gy, 3, 1, 19);
            gy++;
            addWidget(PEPane,new ScrollBarWidget("P-ENV VELO SENS", patch, 0, 100, 0, lww,
                new MT32Model(patch,j+0x09), new MT32Sender(j+k+0x09, basad)), 0, gy, 3, 1, 20);
            gy++;
            addWidget(PEPane,new ScrollBarWidget("P-ENV TIME KEYF", patch, 0, 4, 0, lww,
                new MT32Model(patch,j+0x0A), new MT32Sender(j+k+0x0A, basad)), 0, gy, 3, 1, 21);
            gy++;

            EnvelopeWidget.Node[] pNodes = new EnvelopeWidget.Node[] {
                new EnvelopeWidget.Node(
                    0, 0,     null, 0, 100, new MT32Model(patch,j+0x0F), 
                    0, false, null,         new MT32Sender(j+k+0x0F, basad), 
                    null,           "LEVEL 0"),     
	        new EnvelopeWidget.Node(
                    0, 100,   new MT32Model(patch,j+0x0B), 0, 100, new MT32Model(patch,j+0x10), 
                    0, false, new MT32Sender(j+k+0x0B, basad),     new MT32Sender(j+k+0x10, basad), 
                    "TIME 1 Atk",                          "LEVEL 1 Atk"),     	
	        new EnvelopeWidget.Node(
                    0, 100,    new MT32Model(patch,j+0x0C), 0, 100, new MT32Model(patch,j+0x11), 
                    25, false, new MT32Sender(j+k+0x0C, basad),     new MT32Sender(j+k+0x11, basad), 
                    "TIME 2 Dcy",                           "LEVEL 2 Dcy"),
	        new EnvelopeWidget.Node(
                    0,100,     new MT32Model(patch,j+0x0D), 0, 100, new MT32Model(patch,j+0x12), 
                    25, false, new MT32Sender(j+k+0x0D, basad),     new MT32Sender(j+k+0x12, basad), 
                    "TIME 3 Sus",                          "LEVEL S Sus"),
                new EnvelopeWidget.Node(
                    50, 50,  null,   EnvelopeWidget.Node.SAME, EnvelopeWidget.Node.SAME, null, 
                    0, false, null,                                                      null, 
                    null,            null),     
	        new EnvelopeWidget.Node(
                    0, 100,   new MT32Model(patch,j+0x0E), 0, 100, new MT32Model(patch,j+0x13), 
                    0, false, new MT32Sender(j+k+0x0E, basad),     new MT32Sender(j+k+0x13, basad), 
                    "TIME 4 Rel",                          "LEVEL E End"),
            };
            addWidget(PEPane,
	        new EnvelopeWidget("P-ENV", patch, pNodes), 0, gy, 3, 5, 19);
            gy=gy+9; // Ajust for number of parameters
            addWidget(PEPane, new ScrollBarWidget("P_LFO RATE", patch, 0, 100, 0, lww,
                new MT32Model(patch,j+0x14), new MT32Sender(j+k+0x14, basad)), 0, gy, 3, 1, 38);
            gy++;
            addWidget(PEPane,new ScrollBarWidget("P_LFO DEPTH", patch, 0, 100, 0, lww,
                new MT32Model(patch,j+0x15), new MT32Sender(j+k+0x15, basad)), 0, gy, 3, 1, 38);
            gy++;
            addWidget(PEPane,new ScrollBarWidget("P_LFO MOD SENS", patch, 0, 100, 0, lww,
                new MT32Model(patch,j+0x16), new MT32Sender(j+k+0x16, basad)), 0, gy, 3, 1, 38);
            gy++;
         
            PEPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
                "",TitledBorder.CENTER,TitledBorder.CENTER));
            partNTabPane.add(PEPane, gbc);
            partNTabPane.addTab("Pitch Envelope", PEPane);

            
            // TVF panel on partial panel
            JPanel TVFPane = new JPanel();
            TVFPane.setLayout(new GridBagLayout());
            int lwf = getLabelWidth("TVF ENV VELO SENS  ");  // Longest label string

            gy = 0;
            addWidget(TVFPane,new ScrollBarWidget("TVF CUTOFF FREQ",patch,0,100,0, lwf,
                new MT32Model(patch,j+0x17),new MT32Sender(j+k+0x17, basad)),0,gy,3,1,55);
            gy++;
            addWidget(TVFPane,new ScrollBarWidget("TVF RESONANCE",patch,0,30,0, lwf,
                new MT32Model(patch,j+0x18),new MT32Sender(j+k+0x18, basad)),0,gy,3,1,55);
            gy++;
            addWidget(TVFPane,new ComboBoxWidget("TVF KEYFOLLOW",patch,
                new MT32Model(patch,j+0x19),new MT32Sender(j+k+0x19, basad),tKeyFollow),0,gy,1,1,13);									    
            gy++;
            addWidget(TVFPane,new ComboBoxWidget("TVF BIAS POINT/DIR",patch,
                new MT32Model(patch,j+0x1A),new MT32Sender(j+k+0x1A, basad),biasPoint),0,gy,3,1,55);									    
            gy++;
            addWidget(TVFPane,new ScrollBarWidget("TVF BIAS LEVEL",patch,0,14,-7, lwf,
                new MT32Model(patch,j+0x1B),new MT32Sender(j+k+0x1B, basad)),0,gy,3,1,55);
            gy++;
            addWidget(TVFPane,new ScrollBarWidget("TVF ENV DEPTH",patch,0,100,0, lwf,
                new MT32Model(patch,j+0x1C),new MT32Sender(j+k+0x1C, basad)),0,gy,3,1,55);
            gy++;
            addWidget(TVFPane,new ScrollBarWidget("TVF ENV VELO SENS",patch,0,100,0, lwf,
                new MT32Model(patch,j+0x1D),new MT32Sender(j+k+0x1D, basad)),0,gy,3,1,55);
            gy++;
            addWidget(TVFPane,new ScrollBarWidget("TVF ENV DEPTH KEYF",patch,0,4,0, lwf,
                new MT32Model(patch,j+0x1E),new MT32Sender(j+k+0x1E, basad)),0,gy,3,1,55);
            gy++;
            addWidget(TVFPane,new ScrollBarWidget("TVF ENV TIME KEYF",patch,0,4,0, lwf,
                new MT32Model(patch,j+0x1F),new MT32Sender(j+k+0x1F, basad)),0,gy,3,1,55);
            gy++;
            EnvelopeWidget.Node[] fNodes = new EnvelopeWidget.Node[] {
                new EnvelopeWidget.Node(0,  0,  
                    null, 0,  0, null, 0,false,
                    null, null, 
                    null, null),     
	        new EnvelopeWidget.Node(
                    0, 100,    new MT32Model(patch,j+0x20), 0, 100, new MT32Model(patch,j+0x25),
                    25, false, new MT32Sender(j+k+0x20, basad),     new MT32Sender(j+k+0x25, basad),
                    "TIME 1",                               "LEVEL 1"),
	        new EnvelopeWidget.Node(
                    0, 100,    new MT32Model(patch,j+0x21), 0, 100, new MT32Model(patch,j+0x26),
                    25, false, new MT32Sender(j+k+0x21, basad),     new MT32Sender(j+k+0x26, basad),
                    "TIME 2",                               "LEVEL 2"),
	        new EnvelopeWidget.Node(
                    0, 100, new MT32Model(patch,j+0x22), 0, 100, new MT32Model(patch,j+0x27),
                    25, false, new MT32Sender(j+k+0x22, basad),  new MT32Sender(j+k+0x27, basad),
                    "TIME 3",                            "LEVEL 3"),
	        new EnvelopeWidget.Node(
                    0,100,    new MT32Model(patch,j+0x23), 0,100, new MT32Model(patch,j+0x28),
                    25,false, new MT32Sender(j+k+0x23, basad),    new MT32Sender(j+k+0x28, basad),
                    "TIME 4",                              "LEVEL S"),
                new EnvelopeWidget.Node(
                    50,50,   null, 5000,5000, null, 
                    0,false, null,            null,  
                    null,          null),     
	        new EnvelopeWidget.Node(0,100,  
                    new MT32Model(patch,j+0x24),0,  0, null, 0,false,
                    new MT32Sender(j+k+0x24, basad),   null,                  
                    "TIME 5",                          null)
            };
            addWidget(TVFPane,
	        new EnvelopeWidget("TVF", patch, fNodes), 0, gy, 3, 5, 56);

            TVFPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
                "",TitledBorder.CENTER,TitledBorder.CENTER));
            partNTabPane.add(TVFPane, gbc);
            partNTabPane.addTab("Time Variant Filter", TVFPane);

//   
               gbc.gridx=10;gbc.gridy=8;gbc.gridwidth=0;gbc.gridheight=9;

            
            // TVA panel on partial panel
            JPanel TVAPane = new JPanel();
            TVAPane.setLayout(new GridBagLayout());
            gy = 0;
            int lwa = getLabelWidth("TVA ENV TIME_FOLLOW ");  // Longest label string
            
            addWidget(TVAPane,new ScrollBarWidget("TVA LEVEL",patch,0,100,0, lwa,
                new MT32Model(patch,j+0x29), new MT32Sender(j+k+0x29, basad)),0,gy,3,1,55);
            gy++;
            addWidget(TVAPane,new ScrollBarWidget("TVA VELO SENS",patch,0,100,0, lwa,
                new MT32Model(patch,j+0x2A), new MT32Sender(j+k+0x2A, basad)),0,gy,3,1,55);
            gy++;
            addWidget(TVAPane,new ScrollBarWidget("TVA BIAS POINT 1",patch,0,127,0, lwa,
                new MT32Model(patch,j+0x2B), new MT32Sender(j+k+0x2B, basad)),0,gy,3,1,55);
            gy++;
            addWidget(TVAPane,new ScrollBarWidget("TVA BIAS LEVEL 1",patch,0,12,-12, lwa,
                new MT32Model(patch,j+0x2C),new MT32Sender(j+k+0x2C, basad)),0,gy,3,1,55);
            gy++;
            addWidget(TVAPane,new ScrollBarWidget("TVA BIAS POINT 2",patch,0,127,0, lwa,
                new MT32Model(patch,j+0x2D),new MT32Sender(j+k+0x2D, basad)),0,gy,3,1,55);
            gy++;
            addWidget(TVAPane,new ScrollBarWidget("TVA BIAS LEVEL 2",patch,0,12,-12, lwa,
                new MT32Model(patch,j+0x2E),new MT32Sender(j+k+0x2E, basad)),0,gy,3,1,55);
            gy++;
            addWidget(TVAPane,new ScrollBarWidget("TVA ENV TIME KEYF",patch,0,4,0, lwa,
                new MT32Model(patch,j+0x2F),new MT32Sender(j+k+0x2F, basad)),0,gy,3,1,55);
            gy++;
            addWidget(TVAPane,new ScrollBarWidget("TVA ENV TIME_FOLLOW",patch,0,4,0, lwa,
                new MT32Model(patch,j+0x30),new MT32Sender(j+k+0x30, basad)),0,gy,3,1,55);
            gy++;
//            System.out.print ("i / j " + i + " / " + j);
            EnvelopeWidget.Node[] aNodes = new EnvelopeWidget.Node[] {
 
                new EnvelopeWidget.Node(
                    0,  0,    null, 0,  0, null,            // minx,  maxx,    ofsx,  miny, maxy, ofsy
                    0, false, null,        null,            // basey, invertx, x,                 y, 
                    null,           null),                  // namex,                 namey
	        new EnvelopeWidget.Node(
                    0, 100,   new MT32Model(patch,j+0x31), 0, 100, new MT32Model(patch,j+0x36),
                    25,false, new MT32Sender(j+k+0x31, basad),     new MT32Sender(j+k+0x36, basad),
                    "TIME 1",                              "LEVEL 1"),
	        new EnvelopeWidget.Node(
                    0, 100,    new MT32Model(patch,j+0x32), 0, 100, new MT32Model(patch,j+0x37),
                    25, false, new MT32Sender(j+k+0x32, basad),     new MT32Sender(j+k+0x37, basad),
                    "TIME 2",                               "LEVEL 2"),
	        new EnvelopeWidget.Node(
                    0, 100, new MT32Model(patch,j+0x33), 0, 100, new MT32Model(patch,j+0x38),
                    25, false, new MT32Sender(j+k+0x33, basad),  new MT32Sender(j+k+0x38, basad),
                    "TIME 3",                            "LEVEL 3"),
	        new EnvelopeWidget.Node(
                    0, 100,    new MT32Model(patch,j+0x34), 0, 100, new MT32Model(patch,j+0x39), 
                    25, false, new MT32Sender(j+k+0x34, basad),     new MT32Sender(j+k+0x39, basad),
                    "TIME 4",                               "LEVEL S"),
                new EnvelopeWidget.Node(
                    50, 50,   null, 5000, 5000, null, 
                    0, false, null,             null,  
                    null,           null),     
	        new EnvelopeWidget.Node(
                    0, 100,   new MT32Model(patch,j+0x35), 0,  0, null, 
                    0, false, new MT32Sender(j+k+0x35, basad),    null,                  
                    "TIME 5",                              null)
            };
            addWidget(TVAPane,
	        new EnvelopeWidget("TVA", patch, aNodes), 0, gy, 3, 5, 56);

            TVAPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
                "",TitledBorder.CENTER,TitledBorder.CENTER));
            partNTabPane.add(TVAPane, gbc);
            partNTabPane.addTab("Time Variant Amplifier", TVAPane);

            
//            partNPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED), 
//                "PART "+(i+1)+" (partNPane) ",TitledBorder.CENTER,TitledBorder.CENTER));
            
            partNPane.add(partNTabPane, gbc);
            partTabPane.addTab("Partial "+(i+1), partNPane);
 
        } // end for loop

        gbc.gridx=5;gbc.gridy=0;gbc.gridwidth=0;gbc.gridheight=0;
        partialPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED), 
            "PARTIALS",TitledBorder.CENTER,TitledBorder.CENTER));  
        partialPane.add(partTabPane, gbc);
//        gbc.anchor=GridBagConstraints.WEST;
         
        gbc.gridx=5;gbc.gridy=0;gbc.gridwidth=8;gbc.gridheight=9;

        scrollPane.add(partialPane, gbc);

        pack();
    }

    private int getLabelWidth(String s) {
	return (int) (new JLabel(s)).getPreferredSize().getWidth();
    }
}
