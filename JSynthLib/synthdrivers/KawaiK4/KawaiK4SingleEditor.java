/**
 * Single Editor for Kawai K4/K4r.
 * @version $Id$
 */
package synthdrivers.KawaiK4;
import core.*;
import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;

class KawaiK4SingleEditor extends PatchEditorFrame {
    private final String[] noteName = new String[] {
	"C-1", "C#-1", "D-1", "D#-1", "E-1", "F-1", "F#-1", "G-1", "G#-1", "A-1", "A#-1", "B-1",
	"C0", "C#0", "D0", "D#0", "E0", "F0", "F#0", "G0", "G#0", "A0", "A#0", "B0",
	"C1", "C#1", "D1", "D#1", "E1", "F1", "F#1", "G1", "G#1", "A1", "A#1", "B1",
	"C2", "C#2", "D2", "D#2", "E2", "F2", "F#2", "G2", "G#2", "A2", "A#2", "B2",
	"C3", "C#3", "D3", "D#3", "E3", "F3", "F#3", "G3", "G#3", "A3", "A#3", "B3",
	"C4", "C#4", "D4", "D#4", "E4", "F4", "F#4", "G4", "G#4", "A4", "A#4", "B4",
	"C5", "C#5", "D5", "D#5", "E5", "F5", "F#5", "G5", "G#5", "A5", "A#5", "B5",
	"C6", "C#6", "D6", "D#6", "E6", "F6", "F#6", "G6", "G#6", "A6", "A#6", "B6",
	"C7", "C#7", "D7", "D#7", "E7", "F7", "F#7", "G7", "G#7", "A7", "A#7", "B7",
	"C8", "C#8", "D8", "D#8", "E8", "F8", "F#8", "G8", "G#8", "A8", "A#8", "B8"
    };

    private final String[] waveName = new String[] {
	"1   SINE 1ST", "2   SINE 2ND", "3    SINE 3RD", "4    SINE 4TH",
	"5    SINE 5TH", "6    SINE 6TH", "7    SINE 7TH", "8    SINE 8TH",
	"9    SINE 9TH", "10   SAW 1", "11   SAW 2", "12   SAW 3",
	"13   SAW 4", "14   SAW 5", "15   SAW 6", "16   SAW 7",
	"17   SAW 8", "18   PULSE", "19   TRIANGLE", "20   SQUARE",
	"21   RECTANGULAR 1", "22   RECTANGULAR 2", "23   RECTANGULAR 3", "24   RECTANGULAR 4",
	"25   RECTANGULAR 5", "26   RECTANGULAR 6", "27   PURE HORN L", "28   PUNCH BRASS 1",
	"29   OBOE 1", "30   OBOE 2", "31   CLASSIC GRAND", "32   ELEC PIANO 1",
	"33   ELEC PIANO 2", "34   ELEC PIANO 3", "35   ELEC ORGAN 1", "36   ELEC ORGAN 2",
	"37   POSITIF", "38   ELEC ORGAN 3", "39   ELEC ORGAN 4", "40   ELEC ORGAN 5",
	"41   ELEC ORGAN 6", "42   ELEC ORGAN 7", "43   ELEC ORGAN 8", "44   ELEC ORGAN 9",
	"45   CLASSIC GUITAR", "46   STEEL STRINGS", "47   HARP", "48   WOOD BASS",
	"49   SYN BASS 3", "50   DIGI BASS", "51   FINGER BASS", "52   MARIMBA",
	"53   SYN VOICE", "54   GLASS HARP 1", "55   CELLO", "56   XYLO",
	"57   ELEC PIANO 4", "58   SYN CLAV 1", "59   ELEC PIANO 5", "60   ELEC ORGAN 10",
	"61   ELEC ORGAN 11", "62   ELEC ORGAN 12", "63   BIG PIPE", "64   GLASS HARP 2",
	"65   RANDOM", "66   ELEC PIANO 6", "67   SYN BASS 4", "68   SYN BASS 1",
	"69   SYN BASS 2", "70   QUENA", "71   OBOE 3", "72   PURE HORN H",
	"73   FAT BRASS", "74   PUNCH BRASS 2", "75   ELEC PIANO 7", "76   ELEC PIANO 8",
	"77   SYN CLAVI 2", "78   HARPSICHORD M", "79   HARPSICHORD L", "80   HARPSICHORD H",
	"81   ELEC ORGAN 13", "82   KOTO", "83   SITAR L", "84   SITAR H",
	"85   PICK BASS", "86   SYN BASS 5", "87   SYN BASS 6", "88   VIBRA ATTACK",
	"89   VIBRAPHONE 1", "90   HORN VIBE", "91   STEEL DRUM 1", "92   STEEL DRUM 2",
	"93   VIBRAPHONE 2", "94   MARIMBA ATTACK", "95   HARMONICA", "96   SYNTH",
	"97   KICK DRUM", "98   GATED KICK", "99   SNARE TITE", "100  SNARE DEEP",
	"101  SNARE HI", "102  RIM SNARE", "103  RIM SHOT", "104  TOM",
	"105  TOM VR", "106  E. TOM", "107  CLOSED HH", "108  OPEN HH",
	"109  OPEN VR HH", "110  FOOT HH", "111  CRASH", "112  CRASH VR",
	"113  CRASH VR 2", "114  RIDE EDGE", "115  RIDE EDGE VR", "116  RIDE CUP",
	"117  RIDE CUP VR", "118  CLAPS", "119  COWBELL", "120  CONGA",
	"121  CONGA SLAP", "122  TAMBOURINE", "123  TAMBOURINE VR", "124  CLAVES",
	"125  TIMBALE", "126  SHAKER", "127  SHAKER VR", "128  TIMPANI",
	"129  TIMPANI VR", "130  SLEIBELL", "131  BELL", "132  METAL HI",
	"133  CLICK", "134  POLE", "135  GLOCKEN", "136  MARIMBA",
	"137  PIANO ATTK", "138  WATER DROP", "139  CHAR", "140  PIANO NORM",
	"141  PIANO VR", "142  CELLO NORML", "143  CELLO VR1", "144  CELLO VR2",
	"145  CELLO 1 SHOT", "146  STRINGS NORML", "147  STRINGS VR", "148  SLAP BASS L",
	"149  SLAP BASS L VR", "150  SLAP BASS L 1SHOT", "151  SLAP BASS H", "152  SLAP BASS H VR",
	"153  SLAP BASS H 1SHOT", "154  PICK BASS ", "155  PICK BASS VR", "156  PICK BASS 1SHOT",
	"157  WOOD BASS ATTACK", "158  WOOD BASS", "159  WOOD BASS VR", "160  FRETLESS",
	"161  FRETLESS VR", "162  SYNBASS", "163  SYN BASS VR", "164  MUTE E GUITAR",
	"165  MUTE VR E GUITAR", "166  MUTE E G 1SHOT", "167  DIST MUTE", "168  DIST MUTE VR",
	"169  DIST MUTE 1SHOT", "170  DIST LEAD", "171  DIST LEAD VR", "172  ELEC GUITAR",
	"173  GUT GUITAR", "174  GUT GUITAR VR", "175  GUT GUITAR 1SHOT", "176  FLUTE",
	"177  FLUTE 1SHOT", "178  BOTTLE BLOW", "179  BOTTLE BLOW VR", "180  SAX",
	"181  SAX VR 1", "182  SAX VR 2", "183  SAX 1SHOT", "184  TRUMPET",
	"185  TRUMPET VR 1", "186  TRUMPET VR 2", "187  TRUMPET 1SHOT", "188  TROMBONE",
	"189  TROMBONE VR", "190  TROMBONE 1SHOT", "191  VOICE", "192  NOISE",
	"193  PIANO 1", "194  PIANO 2", "195  PIANO 3", "196  PIANO 4",
	"197  PIANO 5", "198  CELLO 1", "199  CELLO 2", "200  CELLO 3",
	"201  CELLO 4 1SHOT", "202  CELLO 5 1SHOT", "203  CELLO 6 1SHOT", "204  STRINGS 1",
	"205  STRINGS 2", "206  SLAP BASS L", "207  SLAP BASS L 1SHOT", "208  SLAP BASS H",
	"209  SLAP BASS H 1SHOT", "210  PICK BASS 1", "211  PICK BASS 2 1SHOT", "212  PICK BASS 3 1SHOT",
	"213  MUTE ELEC GUITAR", "214  MUTE ELEC G 1SHOT", "215  DIST LEAD 1", "216  DIST LEAD 2",
	"217  DIST LEAD 3", "218  GUT GUITAR 1", "219  GUT GUITAR 2", "221  GUT GUITAR 3 1SHOT",
	"220  GUT GUITAR 4 1SHOT", "222  FLUTE 1", "223  FLUTE 2", "224  SAX 1",
	"225  SAX 2", "226  SAX 3", "227  SAX 4 1SHOT", "228  SAX 5 1SHOT",
	"229  SAX 6 1SHOT", "230  TRUMPET", "231  TRUMPET 1SHOT", "232  VOICE 1",
	"233  VOICE 2", "234  REVERSE 1", "235  REVERSE 2", "236  REVERSE 3",
	"237  REVERSE 4", "238  REVERSE 5", "239  REVERSE 6", "240  REVERSE 7",
	"241  REVERSE 8", "242  REVERSE 9", "243  REVERSE 10", "244  REVERSE 11",
	"245  LOOP 1", "246  LOOP 2", "247  LOOP 3", "248  LOOP 4",
	"249  LOOP 5", "250  LOOP 6", "251  LOOP 7", "252  LOOP 8",
	"253  LOOP 9", "254  LOOP 10", "255  LOOP 11", "256  LOOP 12"
    };

    /** For Alignment, a size to scrollbar labels */
    //private int labelWidth;

    public KawaiK4SingleEditor(Patch patch) {
	super ("Kawai K4 Single Editor", patch);
	int lw; 		// label width
	// Common Pane
	gbc.weightx = 5;
	JPanel cmnPane = new JPanel();
	cmnPane.setLayout(new GridBagLayout());	 gbc.weightx = 0;
	//setLongestLabel("Volume ");
	lw = getLabelWidth("Volume ");
	addWidget(cmnPane,
		  new PatchNameWidget(" Name  ", patch),
		  0, 0, 2, 1, 0);
	// gbc.weightx=1;
	addWidget(cmnPane,
		  new ScrollBarWidget("Volume", patch, 0, 100, 0, lw,
				      new K4Model(patch, 10),
				      new K4Sender(10)),
		  0, 1, 5, 1, 1);
	addWidget(cmnPane,
		  new ScrollBarWidget("Effect", patch, 0, 31, 1, lw,
				      new K4Model(patch, 11),
				      new K4Sender(11)),
		  0, 2, 5, 1, 2);
	addWidget(cmnPane,
		  new ComboBoxWidget("Source Mode", patch,
				     new K4Model(patch, 13, 03),
				     new K4Sender(13),  new String[] {
					 "NORMAL", "TWIN", "DOUBLE"
				     }),
		  0, 3, 2, 1, 3);
	addWidget(cmnPane,
		  new ComboBoxWidget("Poly Mode", patch,
				     new K4Model(patch, 13, 12),
				     new K4Sender(14), new String[] {
					 "POLY 1", "POLY 2", "SOLO 1", "SOLO 2"
				     }),
		  2, 3, 2, 1, 4);

	//setLongestLabel("Mod Wheel Depth");
	lw = getLabelWidth("Mod Wheel Depth");
	addWidget(cmnPane,
		  new ScrollBarWidget("Pitchbend Depth", patch, 0, 12, 0, lw,
				      new K4Model(patch, 15, 15),
				      new K4Sender(18)),
		  0, 4, 4, 1, 5);
	addWidget(cmnPane,
		  new ScrollBarWidget("Mod Wheel Depth", patch, 0, 100, -50, lw,
				      new K4Model(patch, 17),
				      new K4Sender(21)),
		  0, 5, 5, 1, 6);
	addWidget(cmnPane,
		  new ComboBoxWidget("Mod Wheel ->", patch,
				     new K4Model(patch, 15, 48),
				     new K4Sender(19), new String[] {
					 "VIB", "LFO", "DCF"
				     }),
		  0, 6, 2, 1, 7);
	addWidget(cmnPane,
		  new ComboBoxWidget("Out Select", patch,
				     new K4Model(patch, 12),
				     new K4Sender(12), new String[] {
					 "A", "B", "C", "D", "E", "F", "G", "H"
				     }),
		  2, 6, 1, 1, 8);
	addWidget(cmnPane,
		  new CheckBoxWidget("AM 1->2", patch,
				     new K4Model(patch, 13, 16),
				     new K4Sender(15)),
		  0, 7, 1, 1, -1);
	addWidget(cmnPane,
		  new CheckBoxWidget("AM 3->4", patch,
				     new K4Model(patch, 13, 32),
				     new K4Sender(16)),
		  1, 7, 1, 1, -2);

	gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 5; gbc.gridheight = 3;
	gbc.fill = GridBagConstraints.BOTH; gbc.anchor = GridBagConstraints.EAST;
	cmnPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
					   "Common", TitledBorder.CENTER, TitledBorder.CENTER));
	scrollPane.add(cmnPane, gbc);
	gbc.weightx = 5;
	//Vibrato Pane
	JPanel vibPane = new JPanel();
	vibPane.setLayout(new GridBagLayout());
	//setLongestLabel("Pressure to Depth");
	lw = getLabelWidth("Pressure to Depth");
	addWidget(vibPane,
		  new ScrollBarWidget("Depth", patch, 0, 100, -50, lw,
				      new K4Model(patch, 23),
				      new K4Sender(27)),
		  0, 0, 5, 1, 9);
	gbc.weightx = 0;
	addWidget(vibPane,
		  new ScrollBarWidget("Speed", patch, 0, 100, 0, lw,
				      new K4Model(patch, 16),
				      new K4Sender(20)),
		  0, 1, 5, 1, 10);
	addWidget(vibPane,
		  new ScrollBarWidget("Pressure to ", patch, 0, 100, -50, lw,
				      new K4Model(patch, 22),
				      new K4Sender(26)),
		  0, 2, 5, 1, 11);
	addWidget(vibPane,
		  new ComboBoxWidget("Shape", patch,
				     new K4Model(patch, 14, 48),
				     new K4Sender(17), new String[] {
					 "TRIANGLE", "SAW", "SQUARE", "RANDOM"
				     }),
		  0, 3, 1, 1, 12);
	gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 5; gbc.gridheight = 3;
	gbc.fill = GridBagConstraints.BOTH; gbc.anchor = GridBagConstraints.EAST;
	vibPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
					   "Vibrato", TitledBorder.CENTER, TitledBorder.CENTER));
	scrollPane.add(vibPane, gbc);
	gbc.weightx = 5;
	// LFO Pane
	JPanel lfoPane = new JPanel();
	lfoPane.setLayout(new GridBagLayout());
	addWidget(lfoPane,
		  new ScrollBarWidget("Depth", patch, 0, 100, -50, lw,
				      new K4Model(patch, 27),
				      new K4Sender(31)),
		  0, 0, 5, 1, 17);
	gbc.weightx = 0;
	addWidget(lfoPane,
		  new ScrollBarWidget("Speed", patch, 0, 100, 0, lw,
				      new K4Model(patch, 25),
				      new K4Sender(29)),
		  0, 1, 5, 1, 18);
	addWidget(lfoPane,
		  new ScrollBarWidget("Delay", patch, 0, 100, 0, lw,
				      new K4Model(patch, 26),
				      new K4Sender(30)),
		  0, 2, 5, 1, 19);
	addWidget(lfoPane,
		  new ScrollBarWidget("Pressure to Depth", patch, 0, 100, -50, lw,
				      new K4Model(patch, 28),
				      new K4Sender(32)),
		  0, 3, 5, 1, 20);
	addWidget(lfoPane,
		  new ComboBoxWidget("Shape", patch,
				     new K4Model(patch, 24),
				     new K4Sender(28), new String[] {
					 "TRIANGLE", "SAW", "SQUARE", "RANDOM"
				     }),
		  0, 4, 1, 1, 21);
	gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 5; gbc.gridheight = 3;
	gbc.fill = GridBagConstraints.BOTH; gbc.anchor = GridBagConstraints.EAST;
	lfoPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
					   "LFO Settings", TitledBorder.CENTER, TitledBorder.CENTER));
	scrollPane.add(lfoPane, gbc);
	// Bend Pane
	JPanel bndPane = new JPanel();
	bndPane.setLayout(new GridBagLayout());	 gbc.weightx = 1;
	addWidget(bndPane,
		  new ScrollBarWidget("Time", patch, 0, 100, 0, lw,
				      new K4Model(patch, 18),
				      new K4Sender(22)),
		  0, 0, 5, 1, 22);
	gbc.weightx = 0;
	addWidget(bndPane,
		  new ScrollBarWidget("Depth", patch, 0, 100, -50, lw,
				      new K4Model(patch, 19),
				      new K4Sender(23)),
		  0, 1, 5, 1, 23);
	addWidget(bndPane,
		  new ScrollBarWidget("Key Scale to Time", patch, 0, 100, -50, lw,
				      new K4Model(patch, 20),
				      new K4Sender(24)),
		  0, 2, 5, 1, 24);
	addWidget(bndPane,
		  new ScrollBarWidget("Velocity to Depth", patch, 0, 100, -50, lw,
				      new K4Model(patch, 21),
				      new K4Sender(25)),
		  0, 3, 5, 1, 25);

	gbc.gridx = 0; gbc.gridy = 9; gbc.gridwidth = 5; gbc.gridheight = 3;
	gbc.fill = GridBagConstraints.BOTH; gbc.anchor = GridBagConstraints.EAST;
	bndPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
					   "Auto Bend", TitledBorder.CENTER, TitledBorder.CENTER));
	scrollPane.add(bndPane, gbc);

	JTabbedPane oscPane = new JTabbedPane();
	//setLongestLabel("Vel to Cutoff");
	lw = getLabelWidth("Vel to Cutoff");
	for (int i = 0; i < 4; i++) {
	    JPanel panel = new JPanel();
	    panel.setLayout(new GridBagLayout());
	    oscPane.addTab("Source" + (i + 1), panel); gbc.weightx = 0;
	    EnvelopeWidget.Node[] nodes = new EnvelopeWidget.Node[] {
		new EnvelopeWidget.Node(0, 0, null, 0, 0, null, 0, false, null, null, null, null),
		new EnvelopeWidget.Node(0, 100,
					new K4Model(patch, 30 + i), 0, 0, null, 0, false,
					new K4Sender(34, i), null, "Dly", null),
		new EnvelopeWidget.Node(0, 100,
					new K4Model(patch, 62 + i), 100, 100, null, 25, false,
					new K4Sender(45, i), null, "A", null),
		new EnvelopeWidget.Node(0, 100,
					new K4Model(patch, 66 + i), 0, 100,
					new K4Model(patch, 70 + i), 25, false,
					new K4Sender(46, i),
					new K4Sender(47, i), "D", "S"),
		new EnvelopeWidget.Node(100, 100, null, 5000, 5000, null, 0, false, null, null, null, null),
		new EnvelopeWidget.Node(0, 100,
					new K4Model(patch, 74 + i), 0, 0, null, 0, false,
					new K4Sender(48, i), null, "R", null),
	    };
	    addWidget(panel,
		      new EnvelopeWidget("DCA Envelope", patch, nodes),
		      0, 0, 3, 5, 33);
	    addWidget(panel,
		      new ScrollBarWidget("Level", patch, 0, 100, 0, lw,
					  new K4Model(patch, 58 + i),
					  new K4Sender(44, i)),
		      0, 5, 3, 1, 38);
	    addWidget(panel,
		      new ComboBoxWidget("Wave", patch,
					 new WaveModel(patch, i),
					 new K4Sender(36, i), waveName),
		      0, 6, 2, 1, 39);
	    addWidget(panel,
		      new ScrollBarWidget("Transpose", patch, 0, 48, -24, lw,
					  new K4Model(patch, 42 + i, 63),
					  new K4Sender(37, i)),
		      0, 7, 3, 1, 40);
	    addWidget(panel,
		      new ComboBoxWidget("Fixed", patch,
					 new K4Model(patch, 46 + i),
					 new K4Sender(39, i), noteName),
		      0, 8, 1, 1, 41);
	    addWidget(panel,
		      new CheckBoxWidget("Key Track", patch,
					 new K4Model(patch, 42, 64),
					 new K4Sender(38, i)),
		      1, 8, 2, 1, -33);
	    addWidget(panel,
		      new ScrollBarWidget("Tune", patch, 0, 100, -50, lw,
					  new K4Model(patch, 50 + i),
					  new K4Sender(40, i)),
		      0, 9, 3, 1, 42);
	    addWidget(panel,
		      new ScrollBarWidget("Vel to Level", patch, 0, 100, -50, lw,
					  new K4Model(patch, 78 + i),
					  new K4Sender(49, i)),
		      0, 10, 3, 1, 43);
	    addWidget(panel,
		      new ScrollBarWidget("Prs to Level", patch, 0, 100, -50, lw,
					  new K4Model(patch, 82 + i),
					  new K4Sender(50, i)),
		      0, 11, 3, 1, 44);
	    addWidget(panel,
		      new ScrollBarWidget("KS to Level", patch, 0, 100, -50, lw,
					  new K4Model(patch, 86 + i),
					  new K4Sender(51, i)),
		      0, 12, 3, 1, 45);
	    addWidget(panel,
		      new ScrollBarWidget("OnVel Time", patch, 0, 100, -50, lw,
					  new K4Model(patch, 90 + i),
					  new K4Sender(52, i)),
		      0, 13, 3, 1, 46);
	    addWidget(panel,
		      new ScrollBarWidget("OffVel Time", patch, 0, 100, -50, lw,
					  new K4Model(patch, 94 + i),
					  new K4Sender(53, i)),
		      0, 14, 3, 1, 47);
	    addWidget(panel,
		      new ScrollBarWidget("KS to Time", patch, 0, 100, -50, lw,
					  new K4Model(patch, 98 + i),
					  new K4Sender(54, i)),
		      0, 15, 3, 1, 48);
	    addWidget(panel,
		      new ComboBoxWidget("KS Curve", patch,
					 new K4Model(patch, 34, 112),
					 new K4Sender(35, i),
					 new String[] {
					     "1", "2", "3", "4",
					     "5", "6", "7", "8"
					 }),
		      0, 16, 1, 1, 49);
	    addWidget(panel,
		      new ComboBoxWidget("Vel Curve", patch,
					 new K4Model(patch, 54, 28),
					 new K4Sender(43, i),
					 new String[] {
					     "1", "2", "3", "4",
					     "5", "6", "7", "8"
					 }),
		      1, 16, 2, 1, 50);
	    addWidget(panel,
		      new CheckBoxWidget("Prs to Freq", patch,
					 new K4Model(patch, 54, 1),
					 new K4Sender(41, i)),
		      0, 17, 1, 1, -34);
	    addWidget(panel,
		      new CheckBoxWidget("Vibrato/ Auto Bend", patch,
					 new K4Model(patch, 54, 2),
					 new K4Sender(42, i)),
		      1, 17, 1, 1, -35);
	}
	gbc.gridx = 5; gbc.gridy = 0; gbc.gridwidth = 5; gbc.gridheight = 9;

	scrollPane.add(oscPane, gbc);
	JTabbedPane dcfPane = new JTabbedPane();

	for (int i = 0; i < 2; i++) {
	    JPanel panel = new JPanel();
	    panel.setLayout(new GridBagLayout());
	    dcfPane.addTab("Filter" + (i + 1), panel); gbc.weightx = 0;
	    EnvelopeWidget.Node[] nodes = new EnvelopeWidget.Node[] {
		new EnvelopeWidget.Node(0, 0, null, 0, 0, null, 0, false, null, null, null, null),
		new EnvelopeWidget.Node(0, 100,
					new K4Model(patch, 116 + i), 100, 100, null, 25, false,
					new K4Sender(63, i), null, "A", null),
		new EnvelopeWidget.Node(0, 100,
					new K4Model(patch, 118 + i), 0, 100,
					new K4Model(patch, 120 + i), 25, false,
					new K4Sender(64, i), new K4Sender(65, i), "D", "S"),
		new EnvelopeWidget.Node(100, 100, null, 5000, 5000, null, 0, false, null, null, null, null),
		new EnvelopeWidget.Node(0, 100,
					new K4Model(patch, 122 + i), 0, 0, null, 0, false,
					new K4Sender(66, i), null, "R", null),
	    };
	    addWidget(panel,
		      new EnvelopeWidget("DCF Envelope", patch, nodes),
		      0, 0, 3, 5, 51);
	    addWidget(panel,
		      new ScrollBarWidget("Cutoff", patch, 0, 100, 0, lw,
					  new K4Model(patch, 102 + i),
					  new K4Sender(55, i)),
		      0, 5, 3, 1, 55);
	    addWidget(panel,
		      new ScrollBarWidget("DCF Depth", patch, 0, 100, -50, lw,
					  new K4Model(patch, 112 + i),
					  new K4Sender(61, i)),
		      0, 6, 3, 1, 56);
	    addWidget(panel,
		      new ScrollBarWidget("Resonance", patch, 0, 7, 1, lw,
					  new K4Model(patch, 104 + i, 7),
					  new K4Sender(56, i)),
		      0, 7, 3, 1, 57);
	    addWidget(panel,
		      new CheckBoxWidget("LFO to Cutoff", patch,
					 new K4Model(patch, 104, 8),
					 new K4Sender(57, i)),
		      0, 8, 2, 1, -36);
	    addWidget(panel,
		      new ScrollBarWidget("Vel to Cutoff", patch, 0, 100, -50, lw,
					  new K4Model(patch, 106 + i),
					  new K4Sender(58, i)),
		      0, 9, 3, 1, 58);
	    addWidget(panel,
		      new ScrollBarWidget("Prs to Cutoff", patch, 0, 100, -50, lw,
					  new K4Model(patch, 108 + i),
					  new K4Sender(59, i)),
		      0, 10, 3, 1, 59);
	    addWidget(panel,
		      new ScrollBarWidget("KS to Cutoff", patch, 0, 100, -50, lw,
					  new K4Model(patch, 110 + i),
					  new K4Sender(60, i)),
		      0, 11, 3, 1, 60);
	    addWidget(panel,
		      new ScrollBarWidget("Vel Depth", patch, 0, 100, -50, lw,
					  new K4Model(patch, 114 + i),
					  new K4Sender(62, i)),
		      0, 12, 3, 1, 61);
	    addWidget(panel,
		      new ScrollBarWidget("OnVel Time", patch, 0, 100, -50, lw,
					  new K4Model(patch, 124 + i),
					  new K4Sender(67, i)),
		      0, 13, 3, 1, 62);
	    addWidget(panel,
		      new ScrollBarWidget("OffVel Time", patch, 0, 100, -50, lw,
					  new K4Model(patch, 126 + i),
					  new K4Sender(68, i)),
		      0, 14, 3, 1, 63);
	    addWidget(panel,
		      new ScrollBarWidget("KS to Time", patch, 0, 100, -50, lw,
					  new K4Model(patch, 128 + i),
					  new K4Sender(69, i)),
		      0, 15, 3, 1, 64);
	}
	gbc.gridx = 10; gbc.gridy = 0; gbc.gridwidth = 5; gbc.gridheight = 9;
	scrollPane.add(dcfPane, gbc);
	pack();
	show();
    }

    private int getLabelWidth(String s) {
	return (int) (new JLabel(s)).getPreferredSize().getWidth();
    }
}
