/**
 * Single Editor for Roland MT-32.
 * @version $Id$
 */
package synthdrivers.RolandMT32;
import core.*;
import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;

class RolandMT32SingleEditor extends PatchEditorFrame {
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
        " 48 Bowed Attack", " 49 Organ", " 50 Xylophone (Tubes)", " 51 Tubes Repeat",
        " 52 ", " 53 Space Noise Maintain", " 54 Base Drum", " 55 Snare Drum",
        " 56 Snare Drum", " 57 Floor Tom", " 58 Closed HiHat", " 59 Open HiHat",
        " 60 Closed HiHat Loop", " 61 Open HiHat Loop", " 62 Closed HiHat", " 63 Rim Shot",
        " 64 Hand Clap", " 65 ", " 66 ", " 67 ",
        " 68 ", " 69 ", " 70 ", " 71 ", " 72 ", " 73 ", " 74 ", " 75 ",
        " 76 ", " 77 ", " 78 ", " 79 ", " 80 ", " 81 ", " 82 ", " 83 ", 
        " 84 ", " 85 ", " 86 ", " 87 ", " 88 ", " 89 ", " 90 ", " 91 ", 
        " 92 ", " 93 ", " 94 ", " 95 ", " 96 ", " 97 ", " 98 ", " 99 ",
        "100 ", "101 ", "102 ", "103 ", "104 ", "105 ", "106 ", "107 ", 
        "108 ", "109 ", "110 ", "111 ", "112 ", "113 ", "114 ", "115 ", 
        "116 ", "117 ", "118 ", "119 ", "120 ", "121 ", "122 ", "123 ", 
        "124 ", "125 ", "126 ", "127 "
    };
                                            
    /** For Alignment, a size to scrollbar labels */
    //private int labelWidth;

    public RolandMT32SingleEditor(Patch patch) {
	super ("Roland MT-32 Single Editor", patch);
//      Common Pane
        gbc.weightx=5;
        int gy = 0;  // row count
        int k = 0;  // timbre number offset. Not implemented yet.
        int lwc = getLabelWidth("Part 1&2 Struct  ");  // Longest label length
        JPanel cmnPane=new JPanel();
        cmnPane.setLayout(new GridBagLayout());	 
        gbc.weightx=0;   							         
        addWidget(cmnPane, new PatchNameWidget(" Name  ", patch), 0, gy, 2, 1, 0);
        gy++;
        addWidget(cmnPane, new ScrollBarWidget("Part 1&2 Struct", patch,0,12,1,lwc, 
            new MT32Model(patch,0x0A), new MT32Sender(k+0x0A)),0,gy,5,1,1);
        gy++;
        addWidget(cmnPane, new ScrollBarWidget("Part 3&4 Struct", patch,0,12,1,lwc, 
            new MT32Model(patch,0x0B), new MT32Sender(k+0x0B)),0,gy,5,1,2);
        gy++;
        addWidget(cmnPane, new ScrollBarWidget("Partial Mute", patch,0,15,0,lwc, 
            new MT32Model(patch,0x0C), new MT32Sender(k+0x0C)),0,gy,5,1,3);
        gy++;
        addWidget(cmnPane, new CheckBoxWidget ("Env Mode", patch, 
            new MT32Model(patch,0x0D), new MT32Sender(k+0x0D)),0,gy,5,1,4);   

        gbc.gridx=0;
        gbc.gridy=0;
        gbc.gridwidth=5;
        gbc.gridheight=3;
        gbc.fill=GridBagConstraints.BOTH;
        gbc.anchor=GridBagConstraints.EAST;
        cmnPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
            "Common",TitledBorder.CENTER, TitledBorder.CENTER));  
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

            // WG and P_ENV panel on partial panel
            JPanel WGPane = new JPanel();
            WGPane.setLayout(new GridBagLayout());
            partTabPane.addTab("Partial"+(i+1)+" WG & P", WGPane);

            addWidget(WGPane, new ComboBoxWidget("WG PITCH COARSE", patch, 
                new MT32Model(patch,j+0x00), new MT32Sender(j+k+0x00), noteName), 0, gy, 1, 1, 11);
            gy++;
            addWidget(WGPane, new ScrollBarWidget("WG PITCH FINE", patch, 0, 100, -50, lww, 
                new MT32Model(patch,j+0x01), new MT32Sender(j+k+0x01)),0,gy,3,1,12);
            gy++;
            addWidget(WGPane,new ComboBoxWidget("WG PITCH FOLLOW", patch, 
                new MT32Model(patch,j+0x02), new MT32Sender(j+k+0x02), pKeyFollow), 0, gy, 1, 1, 13);
            gy++;
            addWidget(WGPane,new CheckBoxWidget("WG PITCH BENDER SW", patch, 
                new MT32Model(patch,j+0x03), new MT32Sender(j+k+0x03)),1,gy,1,1,14);   
            gy++;
            addWidget(WGPane,new ComboBoxWidget("WG WAVEFORM", patch, 
                new MT32Model(patch,j+0x04), new MT32Sender(j+k+0x04),waveName), 0, gy, 1, 1, 15);
            gy++;
            addWidget(WGPane,new ComboBoxWidget("WG PCM WAVE #", patch, 
                new MT32Model(patch,j+0x05), new MT32Sender(j+k+0x05),WGPCMWAVE), 0, gy, 1, 1, 15);
//            addWidget(WGPane,new ScrollBarWidget("WG PCM WAVE #", patch, 0, 127, 1, lww, 
//                new MT32Model(patch,j+0x05), new MT32Sender(j+k+0x05)), 0, gy, 3, 1, 16);
            gy++;
            addWidget(WGPane,new ScrollBarWidget("WG PULSE WIDTH", patch, 0, 100, 0, lww,
                new MT32Model(patch,j+0x06), new MT32Sender(j+k+0x06)), 0, gy, 3, 1, 17);
            gy++;
            addWidget(WGPane,new ScrollBarWidget("WG PW VELO SENS", patch, 0, 14, -7, lww,
                new MT32Model(patch,j+0x07), new MT32Sender(j+k+0x07)), 0, gy, 3, 1, 18);
            gy++;
            addWidget(WGPane,new ScrollBarWidget("P-ENV DEPTH", patch, 0, 10, 0, lww,
                new MT32Model(patch,j+0x08), new MT32Sender(j+k+0x08)), 0, gy, 3, 1, 19);
            gy++;
            addWidget(WGPane,new ScrollBarWidget("P-ENV VELO SENS", patch, 0, 100, 0, lww,
                new MT32Model(patch,j+0x09), new MT32Sender(j+k+0x09)), 0, gy, 3, 1, 20);
            gy++;
            addWidget(WGPane,new ScrollBarWidget("P-ENV TIME KEYF", patch, 0, 4, 0, lww,
                new MT32Model(patch,j+0x0A), new MT32Sender(j+k+0x0A)), 0, gy, 3, 1, 21);
            gy++;

            EnvelopeWidget.Node[] pNodes = new EnvelopeWidget.Node[] {
                new EnvelopeWidget.Node(
                    0, 0,     null, 0, 100, new MT32Model(patch,j+0x0F), 
                    0, false, null,         new MT32Sender(j+k+0x0F), 
                    null,           "LEVEL 0"),     
	        new EnvelopeWidget.Node(
                    0, 100,   new MT32Model(patch,j+0x0B), 0, 100, new MT32Model(patch,j+0x10), 
                    0, false, new MT32Sender(j+k+0x0B),            new MT32Sender(j+k+0x10), 
                    "TIME 1 Atk",                          "LEVEL 1 Atk"),     	
	        new EnvelopeWidget.Node(
                    0, 100,    new MT32Model(patch,j+0x0C), 0, 100, new MT32Model(patch,j+0x11), 
                    25, false, new MT32Sender(j+k+0x0C),            new MT32Sender(j+k+0x11), 
                    "TIME 2 Dcy",                           "LEVEL 2 Dcy"),
	        new EnvelopeWidget.Node(
                    0,100,     new MT32Model(patch,j+0x0D), 0, 100, new MT32Model(patch,j+0x12), 
                    25, false, new MT32Sender(j+k+0x0D),            new MT32Sender(j+k+0x12), 
                    "TIME 3 Sus",                          "LEVEL S Sus"),
                new EnvelopeWidget.Node(
                    50, 50,  null,   EnvelopeWidget.Node.SAME, EnvelopeWidget.Node.SAME, null, 
                    0, false, null,                                                      null, 
                    null,            null),     
	        new EnvelopeWidget.Node(
                    0, 100,   new MT32Model(patch,j+0x0E), 0, 100, new MT32Model(patch,j+0x13), 
                    0, false, new MT32Sender(j+k+0x0E),            new MT32Sender(j+k+0x13), 
                    "TIME 4 Rel",                          "LEVEL E End"),
            };
            addWidget(WGPane,
	        new EnvelopeWidget("P-ENV", patch, pNodes), 0, gy, 3, 5, 19);
            gy=gy+9; // Ajust for number of parameters
            addWidget(WGPane, new ScrollBarWidget("P_LFO RATE", patch, 0, 100, 0, lww,
                new MT32Model(patch,j+0x14), new MT32Sender(j+k+0x14)), 0, gy, 3, 1, 38);
            gy++;
            addWidget(WGPane,new ScrollBarWidget("P_LFO DEPTH", patch, 0, 100, 0, lww,
                new MT32Model(patch,j+0x15), new MT32Sender(j+k+0x15)), 0, gy, 3, 1, 38);
            gy++;
            addWidget(WGPane,new ScrollBarWidget("P_LFO MOD SENS", patch, 0, 100, 0, lww,
                new MT32Model(patch,j+0x16), new MT32Sender(j+k+0x16)), 0, gy, 3, 1, 38);
            gy++;
         
            WGPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
                "WG & P",TitledBorder.CENTER,TitledBorder.CENTER));

      
            // TVF panel on partial panel
            JPanel TVFPane = new JPanel();
            TVFPane.setLayout(new GridBagLayout());
            int lwf = getLabelWidth("TVF ENV VELO SENS  ");  // Longest label string

            gy = 0;
            addWidget(TVFPane,new ScrollBarWidget("TVF CUTOFF FREQ",patch,0,100,0, lwf,
                new MT32Model(patch,j+0x17),new MT32Sender(j+k+0x17)),0,gy,3,1,55);
            gy++;
            addWidget(TVFPane,new ScrollBarWidget("TVF RESONANCE",patch,0,30,0, lwf,
                new MT32Model(patch,j+0x18),new MT32Sender(j+k+0x18)),0,gy,3,1,55);
            gy++;
            addWidget(TVFPane,new ComboBoxWidget("TVF KEYFOLLOW",patch,
                new MT32Model(patch,j+0x19),new MT32Sender(j+k+0x19),tKeyFollow),0,gy,1,1,13);									    
            gy++;
            addWidget(TVFPane,new ComboBoxWidget("TVF BIAS POINT/DIR",patch,
                new MT32Model(patch,j+0x1A),new MT32Sender(j+k+0x1A),biasPoint),0,gy,3,1,55);									    
            gy++;
            addWidget(TVFPane,new ScrollBarWidget("TVF BIAS LEVEL",patch,0,14,-7, lwf,
                new MT32Model(patch,j+0x1B),new MT32Sender(j+k+0x1B)),0,gy,3,1,55);
            gy++;
            addWidget(TVFPane,new ScrollBarWidget("TVF ENV DEPTH",patch,0,100,0, lwf,
                new MT32Model(patch,j+0x1C),new MT32Sender(j+k+0x1C)),0,gy,3,1,55);
            gy++;
            addWidget(TVFPane,new ScrollBarWidget("TVF ENV VELO SENS",patch,0,100,0, lwf,
                new MT32Model(patch,j+0x1D),new MT32Sender(j+k+0x1D)),0,gy,3,1,55);
            gy++;
            addWidget(TVFPane,new ScrollBarWidget("TVF ENV DEPTH KEYF",patch,0,4,0, lwf,
                new MT32Model(patch,j+0x1E),new MT32Sender(j+k+0x1E)),0,gy,3,1,55);
            gy++;
            addWidget(TVFPane,new ScrollBarWidget("TVF ENV TIME KEYF",patch,0,4,0, lwf,
                new MT32Model(patch,j+0x1F),new MT32Sender(j+k+0x1F)),0,gy,3,1,55);
            gy++;
            EnvelopeWidget.Node[] fNodes = new EnvelopeWidget.Node[] {
                new EnvelopeWidget.Node(0,  0,  
                    null, 0,  0, null, 0,false,
                    null, null, 
                    null, null),     
	        new EnvelopeWidget.Node(
                    0, 100,    new MT32Model(patch,j+0x20), 0, 100, new MT32Model(patch,j+0x25),
                    25, false, new MT32Sender(j+k+0x20),            new MT32Sender(j+k+0x25),
                    "TIME 1",                               "LEVEL 1"),
	        new EnvelopeWidget.Node(
                    0, 100,    new MT32Model(patch,j+0x21), 0, 100, new MT32Model(patch,j+0x26),
                    25, false, new MT32Sender(j+k+0x21),            new MT32Sender(j+k+0x26),
                    "TIME 2",                               "LEVEL 2"),
	        new EnvelopeWidget.Node(
                    0, 100, new MT32Model(patch,j+0x22), 0, 100, new MT32Model(patch,j+0x27),
                    25, false, new MT32Sender(j+k+0x22),         new MT32Sender(j+k+0x27),
                    "TIME 3",                            "LEVEL 3"),
	        new EnvelopeWidget.Node(
                    0,100,    new MT32Model(patch,j+0x23), 0,100, new MT32Model(patch,j+0x28),
                    25,false, new MT32Sender(j+k+0x23),           new MT32Sender(j+k+0x28),
                    "TIME 4",                              "LEVEL S"),
                new EnvelopeWidget.Node(
                    50,50,   null, 5000,5000, null, 
                    0,false, null,            null,  
                    null,          null),     
	        new EnvelopeWidget.Node(0,100,  
                    new MT32Model(patch,j+0x24),0,  0, null, 0,false,
                    new MT32Sender(j+k+0x24),          null,                  
                    "TIME 5",                          null)
            };
            addWidget(TVFPane,
	        new EnvelopeWidget("TVF", patch, fNodes), 0, gy, 3, 5, 56);

            TVFPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
                "TVF",TitledBorder.CENTER,TitledBorder.CENTER));

//   }
   gbc.gridx=10;gbc.gridy=0;gbc.gridwidth=5;gbc.gridheight=9;
            partTabPane.addTab("Partial"+(i+1)+" TVF", TVFPane);


            // TVA panel on partial panel
            JPanel TVAPane = new JPanel();
            TVAPane.setLayout(new GridBagLayout());
            gy = 0;
            int lwa = getLabelWidth("TVA ENV TIME_FOLLOW ");  // Longest label string
            
            addWidget(TVAPane,new ScrollBarWidget("TVA LEVEL",patch,0,100,0, lwa,
                new MT32Model(patch,j+0x29), new MT32Sender(j+k+0x29)),0,gy,3,1,55);
            gy++;
            addWidget(TVAPane,new ScrollBarWidget("TVA VELO SENS",patch,0,100,0, lwa,
                new MT32Model(patch,j+0x2A), new MT32Sender(j+k+0x2A)),0,gy,3,1,55);
            gy++;
            addWidget(TVAPane,new ScrollBarWidget("TVA BIAS POINT 1",patch,0,127,0, lwa,
                new MT32Model(patch,j+0x2B), new MT32Sender(j+k+0x2B)),0,gy,3,1,55);
            gy++;
            addWidget(TVAPane,new ScrollBarWidget("TVA BIAS LEVEL 1",patch,0,12,-12, lwa,
                new MT32Model(patch,j+0x2C),new MT32Sender(j+k+0x2C)),0,gy,3,1,55);
            gy++;
            addWidget(TVAPane,new ScrollBarWidget("TVA BIAS POINT 2",patch,0,127,0, lwa,
                new MT32Model(patch,j+0x2D),new MT32Sender(j+k+0x2D)),0,gy,3,1,55);
            gy++;
            addWidget(TVAPane,new ScrollBarWidget("TVA BIAS LEVEL 2",patch,0,12,-12, lwa,
                new MT32Model(patch,j+0x2E),new MT32Sender(j+k+0x2E)),0,gy,3,1,55);
            gy++;
            addWidget(TVAPane,new ScrollBarWidget("TVA ENV TIME KEYF",patch,0,4,0, lwa,
                new MT32Model(patch,j+0x2F),new MT32Sender(j+k+0x2F)),0,gy,3,1,55);
            gy++;
            addWidget(TVAPane,new ScrollBarWidget("TVA ENV TIME_FOLLOW",patch,0,4,0, lwa,
                new MT32Model(patch,j+0x30),new MT32Sender(j+k+0x30)),0,gy,3,1,55);
            gy++;
//            System.out.print ("i / j " + i + " / " + j);
            EnvelopeWidget.Node[] aNodes = new EnvelopeWidget.Node[] {
 
                new EnvelopeWidget.Node(
                    0,  0,    null, 0,  0, null,            // minx,  maxx,    ofsx,  miny, maxy, ofsy
                    0, false, null,        null,            // basey, invertx, x,                 y, 
                    null,           null),                  // namex,                 namey
	        new EnvelopeWidget.Node(
                    0, 100,   new MT32Model(patch,j+0x31), 0, 100, new MT32Model(patch,j+0x36),
                    25,false, new MT32Sender(j+k+0x31),            new MT32Sender(j+k+0x36),
                    "TIME 1",                              "LEVEL 1"),
	        new EnvelopeWidget.Node(
                    0, 100,    new MT32Model(patch,j+0x32), 0, 100, new MT32Model(patch,j+0x37),
                    25, false, new MT32Sender(j+k+0x32),            new MT32Sender(j+k+0x37),
                    "TIME 2",                               "LEVEL 2"),
	        new EnvelopeWidget.Node(
                    0, 100, new MT32Model(patch,j+0x33), 0, 100, new MT32Model(patch,j+0x38),
                    25, false, new MT32Sender(j+k+0x33),         new MT32Sender(j+k+0x38),
                    "TIME 3",                            "LEVEL 3"),
	        new EnvelopeWidget.Node(
                    0, 100,    new MT32Model(patch,j+0x34), 0, 100, new MT32Model(patch,j+0x39), 
                    25, false, new MT32Sender(j+k+0x34),            new MT32Sender(j+k+0x39),
                    "TIME 4",                               "LEVEL S"),
                new EnvelopeWidget.Node(
                    50, 50,   null, 5000, 5000, null, 
                    0, false, null,             null,  
                    null,           null),     
	        new EnvelopeWidget.Node(
                    0, 100,   new MT32Model(patch,j+0x35), 0,  0, null, 
                    0, false, new MT32Sender(j+k+0x35),           null,                  
                    "TIME 5",                              null)
            };
            addWidget(TVAPane,
	        new EnvelopeWidget("TVA", patch, aNodes), 0, gy, 3, 5, 56);

            TVAPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
                "TVA",TitledBorder.CENTER,TitledBorder.CENTER));
            partTabPane.add(TVAPane, gbc);
            
            partNPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED), 
                "PART" + i,TitledBorder.CENTER,TitledBorder.CENTER));  
            partTabPane.addTab("Partial"+(i+1)+" TVA", TVAPane);
            partialPane.add(partTabPane, gbc);

        } // end for loop

        gbc.gridx=5;gbc.gridy=0;gbc.gridwidth=5;gbc.gridheight=9;
        partialPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED), 
            "PARTIALS",TitledBorder.CENTER,TitledBorder.CENTER));  
        scrollPane.add(partialPane, gbc);

        gbc.gridx=5;gbc.gridy=0;gbc.gridwidth=5;gbc.gridheight=9;

        pack();
        show();
    }

    private int getLabelWidth(String s) {
	return (int) (new JLabel(s)).getPreferredSize().getWidth();
    }
}
