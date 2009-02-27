/*
 * @version $Id$
 */
package synthdrivers.KawaiK4;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import core.ComboBoxWidget;
import core.ParamModel;
import core.Patch;
import core.PatchEditorFrame;
import core.ScrollBarWidget;
import core.SysexSender;
import core.SysexWidget;

class KawaiK4DrumsetEditor extends PatchEditorFrame {
    static final String[] NOTE_NAME = new String[] {
        "C1", "C#1", "D1", "D#1", "E1", "F1", "F#1", "G1", "G#1", "A1", "A#1", "B1",
        "C2", "C#2", "D2", "D#2", "E2", "F2", "F#2", "G2", "G#2", "A2", "A#2", "B2",
        "C3", "C#3", "D3", "D#3", "E3", "F3", "F#3", "G3", "G#3", "A3", "A#3", "B3",
        "C4", "C#4", "D4", "D#4", "E4", "F4", "F#4", "G4", "G#4", "A4", "A#4", "B4",
        "C5", "C#5", "D5", "D#5", "E5", "F5", "F#5", "G5", "G#5", "A5", "A#5", "B5",
        "C6"
    };

    static final String[] WAVE_NAME = new String[] {
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

    static final String[] SUBMIX = new String[] {
	"A", "B", "C", "D", "E", "F", "G", "H"
    };
    private int drum = 0;

    public KawaiK4DrumsetEditor(Patch patch) {
        super("Kawai K4 Drumset Editor", patch);
        // Common Pane
        gbc.weightx = 0;
        JPanel cmnPane = new JPanel();
        JPanel keyPane;
        cmnPane.setLayout(new GridBagLayout());

	addWidget(cmnPane,
		  new ScrollBarWidget("Recv.Ch.", patch, 0, 15, 1,
				      new K4Model(patch, 0, 0x0f),
				      new K4Sender(70)),
		  0, 0, 1, 1, 1);
        addWidget(cmnPane,
		  new ScrollBarWidget("Volume", patch, 0, 100, 0,
				      new K4Model(patch, 1),
				      new K4Sender(71)),
		  0, 1, 1, 1, 1);
	addWidget(cmnPane,
		  new ScrollBarWidget("Velocity Depth", patch, 0, 100, -50,
				      new K4Model(patch, 2),
				      new K4Sender(72)),
		  0, 2, 1, 1, 9);

        JPanel padPane = new JPanel();
        padPane.setLayout(new GridBagLayout());

        final JComboBox cb = new JComboBox(NOTE_NAME);
        padPane.add(cb);
        cb.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    drum = cb.getSelectedIndex();
		    SysexWidget w;
		    for (int i = 0; i < widgetList.size(); i++) {
			w  = ((SysexWidget) widgetList.get(i));
			w.setValue();
			// Now w.setValue() does the following job.
			/*
			if (w instanceof ScrollBarWidget)
			    ((ScrollBarWidget) w).slider.setValue(w.getValue());
			if (w instanceof ScrollBarLookupWidget)
			    ((ScrollBarLookupWidget) w).slider.setValue(w.getValue());
			if (w instanceof ComboBoxWidget)
			    ((ComboBoxWidget) w).cb.setSelectedIndex(w.getValue());
			if (w instanceof CheckBoxWidget)
			    ((CheckBoxWidget) w).cb.setSelected((w.getValue() > 0));
			*/
		    }
		}
	    }
			     );

        keyPane = new JPanel();
        keyPane.setLayout(new GridBagLayout());

        addWidget(keyPane,
		  new ComboBoxWidget("Output", patch,
				     new K4Model(patch, 11/* +11*i */, 0x70),
				     new K4DrumSender(73), SUBMIX),
		  0, 0, 1, 1, 8);

        addWidget(keyPane,
		  new ComboBoxWidget("Wave 1", patch,
				     new K4DrumWaveModel(patch, 0),
				     new K4DrumSender(74), WAVE_NAME),
		  0, 1, 2, 1, 39);
        addWidget(keyPane,
		  new ScrollBarWidget("Decay 1", patch, 0, 100, 0,
				      new K4DrumModel(patch, 11 + 4/*+11*i*/),
				      new K4DrumSender(76)),
		  0, 2, 2, 1, 0);
	addWidget(keyPane,
		  new ScrollBarWidget("Tune 1", patch, 0, 100, -50,
				      new K4DrumModel(patch, 11 + 6/*+11*i*/),
				      new K4DrumSender(78)),
		  0, 3, 2, 1, 0);
        addWidget(keyPane,
		  new ScrollBarWidget("Level 1", patch, 0, 100, 0,
				      new K4DrumModel(patch, 11 + 8/*+11*i*/),
				      new K4DrumSender(80)),
		  0, 4, 2, 1, 0);
	addWidget(keyPane,
		  new ComboBoxWidget("Wave 2", patch,
				     new K4DrumWaveModel(patch, 1),
				     new K4DrumSender(75), WAVE_NAME),
		  0, 5, 2, 1, 39);
	addWidget(keyPane,
		  new ScrollBarWidget("Decay 2", patch, 0, 100, 0,
				      new K4DrumModel(patch, 11 + 5/*+11*i*/),
				      new K4DrumSender(77)),
		  0, 6, 2, 1, 0);
	addWidget(keyPane,
		  new ScrollBarWidget("Tune 2", patch, 0, 100, -50,
				      new K4DrumModel(patch, 11 + 7/*+11*i*/),
				      new K4DrumSender(79)),
		  0, 7, 2, 1, 0);
	addWidget(keyPane,
		  new ScrollBarWidget("Level 2", patch, 0, 100, 0,
				      new K4DrumModel(patch, 11 + 9/*+11*i*/),
				      new K4DrumSender(81)),
		  0, 9, 2, 1, 0);

	cmnPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
					   "Common", TitledBorder.CENTER, TitledBorder.CENTER));
        gbc.gridx = GridBagConstraints.REMAINDER; gbc.gridy = 0;
	gbc.gridwidth = GridBagConstraints.REMAINDER; gbc.gridheight = 1;
	gbc.fill = GridBagConstraints.BOTH;
	gbc.anchor = GridBagConstraints.EAST;
        scrollPane.add(cmnPane, gbc);

        //gbc.gridx = gbc.REMAINDER;
        gbc.gridy = GridBagConstraints.RELATIVE;

	//gbc.gridwidth=gbc.REMAINDER;gbc.gridheight=1;
	//gbc.fill=GridBagConstraints.BOTH;gbc.anchor=GridBagConstraints.EAST;
        padPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
					   "Choose a Pad", TitledBorder.CENTER, TitledBorder.CENTER));
        scrollPane.add(padPane, gbc);

	keyPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
					   "Drum Setting", TitledBorder.CENTER, TitledBorder.CENTER));
        //gbc.gridx=gbc.REMAINDER;gbc.gridy=gbc.RELATIVE;gbc.gridwidth=gbc.REMAINDER;
        gbc.gridheight = 3;
        //gbc.fill=GridBagConstraints.BOTH;gbc.anchor=GridBagConstraints.EAST;
        scrollPane.add(keyPane, gbc);

        pack();
    }

    class K4DrumWaveModel implements SysexWidget.IParamModel {
        private Patch patch;
        private int source;
        public K4DrumWaveModel(Patch p, int s) {
	    patch = p; source = s;
	}

        public void set(int i) {
            patch.sysex[8 + 11 + drum * 11 + source]
		= (byte) ((patch.sysex[8 + 11 + drum * 11 + source] & 254)
			  + (byte) (i / 128));
            patch.sysex[8 + 11 + 2 + drum * 11 + source] = (byte) (i & 0x7f);
        }
        public int get() {
            return (((patch.sysex[8 + 11 + drum * 11 + source] & 1) * 128)
		    + (patch.sysex[8 + 11 + 2 + drum * 11 + source]));
        }
    }

    class K4DrumModel extends ParamModel {
        private int bitmask;
        private int mult;

        public K4DrumModel(Patch p, int o) {
            super(p, o + 8);
	    bitmask = 255;
	    mult = 1;
	}

        public K4DrumModel(Patch p, int o, int b) {
            super(p, o + 8);
            bitmask = b;
	    if ((bitmask & 1) == 1) mult = 1;
	    else if ((bitmask & 2) == 2) mult = 2;
	    else if ((bitmask & 4) == 4) mult = 4;
	    else if ((bitmask & 8) == 8) mult = 8;
	    else if ((bitmask & 16) == 16) mult = 16;
	    else if ((bitmask & 32) == 32) mult = 32;
	    else if ((bitmask & 64) == 64) mult = 64;
	    else if ((bitmask & 128) == 128) mult = 128;
        }

        public void set(int i) {
            patch.sysex[ofs + 11  * drum]
		= (byte) ((i * mult)
			  + (patch.sysex[ofs + 11 * drum] & (~bitmask)));
        }

        public int get() {
            return ((patch.sysex[ofs + 11 * drum] & bitmask) / mult);
        }
    }

    class K4DrumSender extends SysexSender {
        private int parameter;
        private byte[] b = new byte[10];

        public K4DrumSender(int param) {
            parameter = param;
            b[0] = (byte) 0xF0; b[1] = (byte) 0x40; b[3] = (byte) 0x10;
	    b[4] = 0; b[5] = 0x04; b[6] = (byte) parameter; b[9] = (byte) 0xF7;
        }

        public byte[] generate(int value) {
	    b[7] = (byte) ((value / 128) + (drum * 2));
	    b[8] = (byte) (value & 127); b[2] = (byte) (channel - 1);
	    return b;
	}
    }
}
