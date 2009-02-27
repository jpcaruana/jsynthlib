package synthdrivers.KawaiK4;
import core.*;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;

import org.jsynthlib.core.CheckBoxWidget;
import org.jsynthlib.core.ComboBoxWidget;
import org.jsynthlib.core.LabelWidget;
import org.jsynthlib.core.Patch;
import org.jsynthlib.core.PatchEditorFrame;
import org.jsynthlib.core.PatchNameWidget;
import org.jsynthlib.core.ScrollBarWidget;

/** Editor for Multi-Banks of the Kawai K4.
 * It has two parts:
 * 1. The common part for Name,Volume and Effect
 * 2. The part for the 8 sections, styled like a matrix
 *    in 8 rows, each row for a section.
 * Because the editor doesn't know the names of all the
 * single patches loaded in the instrument, it shows only
 * the patch numbers.
 * @author Gerrit Gehnen <Gerrit.Gehnen@gmx.de>
 * @version $Id$
 */
class KawaiK4MultiEditor extends PatchEditorFrame {
    private final String[] mode = new String[] {
	"KEYB", "MIDI", "MIX"}
    ;
    private final String[] velocitySwitch = new String[] {
	"Soft", "Loud", "All"
    };
    private final String[] noteName = new String[] {
	"C-2", "C#-2", "D-2", "D#-2", "E-2", "F-2", "F#-2", "G-2", "G#-2", "A-2", "A#-2", "B-2",
	"C-1", "C#-1", "D-1", "D#-1", "E-1", "F-1", "F#-1", "G-1", "G#-1", "A-1", "A#-1", "B-1",
	"C0", "C#0", "D0", "D#0", "E0", "F0", "F#0", "G0", "G#0", "A0", "A#0", "B0",
	"C1", "C#1", "D1", "D#1", "E1", "F1", "F#1", "G1", "G#1", "A1", "A#1", "B1",
	"C2", "C#2", "D2", "D#2", "E2", "F2", "F#2", "G2", "G#2", "A2", "A#2", "B2",
	"C3", "C#3", "D3", "D#3", "E3", "F3", "F#3", "G3", "G#3", "A3", "A#3", "B3",
	"C4", "C#4", "D4", "D#4", "E4", "F4", "F#4", "G4", "G#4", "A4", "A#4", "B4",
	"C5", "C#5", "D5", "D#5", "E5", "F5", "F#5", "G5", "G#5", "A5", "A#5", "B5",
	"C6", "C#6", "D6", "D#6", "E6", "F6", "F#6", "G6", "G#6", "A6", "A#6", "B6",
	"C7", "C#7", "D7", "D#7", "E7", "F7", "F#7", "G7", "G#7", "A7", "A#7", "B7",
	"C8", "C#8", "D8", "D#8", "E8", "F8", "F#8", "G8"
    };
    private final String[] channels = new String[] {
	"1", "2", "3", "4", "5", "6", "7", "8",
	"9", "10", "11", "12", "13", "14", "15", "16"
    };
    private final String[] transpose = new String[] {
	"-24", "-23", "-22", "-21", "-20", "-19", "-18", "-17", "-16", "-15",
	"-14", "-13", "-12", "-11", "-10", "-9", "-8", "-7", "-6", "-5", "-4",
	"-3", "-2", "-1", "0", "1", "2", "3", "4", "5", "6", "7", "8",
	"9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
	"21", "22", "23", "24"
    };
    private final String[] tune = new String[] {
	"-50", "-49", "-48", "-47", "-46", "-45", "-44", "-43", "-42", "-41",
	"-40", "-39", "-38", "-37", "-36", "-35", "-34", "-33", "-32", "-31",
	"-30", "-29", "-28", "-27", "-26", "-25",
	"-24", "-23", "-22", "-21", "-20", "-19", "-18", "-17", "-16", "-15",
	"-14", "-13", "-12", "-11", "-10",
	"-9", "-8", "-7", "-6", "-5", "-4", "-3", "-2", "-1", "0",
	"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13",
	"14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25",
	"26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37",
	"38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50"
    };

    private final String[] submix = new String[] {
	"A", "B", "C", "D", "E", "F", "G", "H"
    };

    private final String[] patchNumbers = new String[] {
	"A-1", "A-2", "A-3", "A-4", "A-5", "A-6", "A-7", "A-8",
        "A-9", "A-10", "A-11", "A-12", "A-13", "A-14", "A-15", "A-16",
        "B-1", "B-2", "B-3", "B-4", "B-5", "B-6", "B-7", "B-8",
        "B-9", "B-10", "B-11", "B-12", "B-13", "B-14", "B-15", "B-16",
        "C-1", "C-2", "C-3", "C-4", "C-5", "C-6", "C-7", "C-8",
        "C-9", "C-10", "C-11", "C-12", "C-13", "C-14", "C-15", "C-16",
        "D-1", "D-2", "D-3", "D-4", "D-5", "D-6", "D-7", "D-8",
        "D-9", "D-10", "D-11", "D-12", "D-13", "D-14", "D-15", "D-16"
    };

    public KawaiK4MultiEditor(Patch patch) {
	super("Kawai K4 Multi Editor", patch);
	// Common Pane
	//  gbc.weightx=5;
	JPanel cmnPane = new JPanel();
	cmnPane.setLayout(new GridBagLayout());
	cmnPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
					   "Common", TitledBorder.CENTER, TitledBorder.CENTER));
	gbc.weightx = 0;
	addWidget(cmnPane,
		  new PatchNameWidget(" Name  ", patch),
		  0, 0, 4, 1, 0);
	addWidget(cmnPane,
		  new ScrollBarWidget("Volume", patch, 0, 100, 0, new K4Model(patch, 10), null),
		  4, 0, 3, 1, 1);
	addWidget(cmnPane,
		  new ScrollBarWidget("Effect", patch, 0, 31, 1, new K4Model(patch, 11), null),
		  8, 0, 5, 1, 2);
	// gbc.weightx=1;
	JPanel secPane = new JPanel();
	secPane.setLayout(new GridBagLayout());
	secPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
					   "Sections", TitledBorder.CENTER, TitledBorder.CENTER));
	addWidget(secPane, new LabelWidget("1"), 0, 3, 1, 1, 0);
	addWidget(secPane, new LabelWidget("2"), 0, 4, 1, 1, 0);
	addWidget(secPane, new LabelWidget("3"), 0, 5, 1, 1, 0);
	addWidget(secPane, new LabelWidget("4"), 0, 6, 1, 1, 0);
	addWidget(secPane, new LabelWidget("5"), 0, 7, 1, 1, 0);
	addWidget(secPane, new LabelWidget("6"), 0, 8, 1, 1, 0);
	addWidget(secPane, new LabelWidget("7"), 0, 9, 1, 1, 0);
	addWidget(secPane, new LabelWidget("8"), 0, 10, 1, 1, 0);

	addWidget(secPane, new LabelWidget("Mute"), 1, 2, 1, 1, 0);
	addWidget(secPane,
		  new CheckBoxWidget("  ", patch, new K4Model(patch, 12 + 0 * 8 + 3, 0x40), null),
		  1, 3, 1, 1, -1);
	addWidget(secPane,
		  new CheckBoxWidget("  ", patch, new K4Model(patch, 12 + 1 * 8 + 3, 0x40), null),
		  1, 4, 1, 1, -2);
	addWidget(secPane,
		  new CheckBoxWidget("  ", patch, new K4Model(patch, 12 + 2 * 8 + 3, 0x40), null),
		  1, 5, 1, 1, -3);
	addWidget(secPane,
		  new CheckBoxWidget("  ", patch, new K4Model(patch, 12 + 3 * 8 + 3, 0x40), null),
		  1, 6, 1, 1, -4);
	addWidget(secPane,
		  new CheckBoxWidget("  ", patch, new K4Model(patch, 12 + 4 * 8 + 3, 0x40), null),
		  1, 7, 1, 1, -5);
	addWidget(secPane,
		  new CheckBoxWidget("  ", patch, new K4Model(patch, 12 + 5 * 8 + 3, 0x40), null),
		  1, 8, 1, 1, -6);
	addWidget(secPane,
		  new CheckBoxWidget("  ", patch, new K4Model(patch, 12 + 6 * 8 + 3, 0x40), null),
		  1, 9, 1, 1, -7);
	addWidget(secPane,
		  new CheckBoxWidget("  ", patch, new K4Model(patch, 12 + 7 * 8 + 3, 0x40), null),
		  1, 10, 1, 1, -8);

	addWidget(secPane, new LabelWidget("Single No."), 2, 2, 1, 1, 0);
	addWidget(secPane,
		  new ComboBoxWidget("", patch, new K4Model(patch, 12 + 0 * 8 + 0), null, patchNumbers),
		  2, 3, 1, 1, 1);
	addWidget(secPane,
		  new ComboBoxWidget("", patch, new K4Model(patch, 12 + 1 * 8 + 0), null, patchNumbers),
		  2, 4, 1, 1, 2);
	addWidget(secPane,
		  new ComboBoxWidget("", patch, new K4Model(patch, 12 + 2 * 8 + 0), null, patchNumbers),
		  2, 5, 1, 1, 3);
	addWidget(secPane,
		  new ComboBoxWidget("", patch, new K4Model(patch, 12 + 3 * 8 + 0), null, patchNumbers),
		  2, 6, 1, 1, 4);
	addWidget(secPane,
		  new ComboBoxWidget("", patch, new K4Model(patch, 12 + 4 * 8 + 0), null, patchNumbers),
		  2, 7, 1, 1, 5);
	addWidget(secPane,
		  new ComboBoxWidget("", patch, new K4Model(patch, 12 + 5 * 8 + 0), null, patchNumbers),
		  2, 8, 1, 1, 6);
	addWidget(secPane,
		  new ComboBoxWidget("", patch, new K4Model(patch, 12 + 6 * 8 + 0), null, patchNumbers),
		  2, 9, 1, 1, 7);
	addWidget(secPane,
		  new ComboBoxWidget("", patch, new K4Model(patch, 12 + 7 * 8 + 0), null, patchNumbers),
		  2, 10, 1, 1, 8);

	addWidget(secPane, new LabelWidget("Zone Low"), 3, 2, 1, 1, 0);
	addWidget(secPane,
		  new ComboBoxWidget("", patch, new K4Model(patch, 12 + 0 * 8 + 1), null, noteName),
		  3, 3, 1, 1, 9);
	addWidget(secPane,
		  new ComboBoxWidget("", patch, new K4Model(patch, 12 + 1 * 8 + 1), null, noteName),
		  3, 4, 1, 1, 10);
	addWidget(secPane,
		  new ComboBoxWidget("", patch, new K4Model(patch, 12 + 2 * 8 + 1), null, noteName),
		  3, 5, 1, 1, 11);
	addWidget(secPane,
		  new ComboBoxWidget("", patch, new K4Model(patch, 12 + 3 * 8 + 1), null, noteName),
		  3, 6, 1, 1, 12);
	addWidget(secPane,
		  new ComboBoxWidget("", patch, new K4Model(patch, 12 + 4 * 8 + 1), null, noteName),
		  3, 7, 1, 1, 13);
	addWidget(secPane,
		  new ComboBoxWidget("", patch, new K4Model(patch, 12 + 5 * 8 + 1), null, noteName),
		  3, 8, 1, 1, 14);
	addWidget(secPane,
		  new ComboBoxWidget("", patch, new K4Model(patch, 12 + 6 * 8 + 1), null, noteName),
		  3, 9, 1, 1, 15);
	addWidget(secPane,
		  new ComboBoxWidget("", patch, new K4Model(patch, 12 + 7 * 8 + 1), null, noteName),
		  3, 10, 1, 1, 16);

	addWidget(secPane, new LabelWidget("Zone High"), 4, 2, 1, 1, 0);
	addWidget(secPane,
		  new ComboBoxWidget("", patch, new K4Model(patch, 12 + 0 * 8 + 2), null, noteName),
		  4, 3, 1, 1, 17);
	addWidget(secPane,
		  new ComboBoxWidget("", patch, new K4Model(patch, 12 + 1 * 8 + 2), null, noteName),
		  4, 4, 1, 1, 18);
	addWidget(secPane,
		  new ComboBoxWidget("", patch, new K4Model(patch, 12 + 2 * 8 + 2), null, noteName),
		  4, 5, 1, 1, 19);
	addWidget(secPane,
		  new ComboBoxWidget("", patch, new K4Model(patch, 12 + 3 * 8 + 2), null, noteName),
		  4, 6, 1, 1, 20);
	addWidget(secPane,
		  new ComboBoxWidget("", patch, new K4Model(patch, 12 + 4 * 8 + 2), null, noteName),
		  4, 7, 1, 1,  21);
	addWidget(secPane,
		  new ComboBoxWidget("", patch, new K4Model(patch, 12 + 5 * 8 + 2), null, noteName),
		  4, 8, 1, 1, 22);
	addWidget(secPane,
		  new ComboBoxWidget("", patch, new K4Model(patch, 12 + 6 * 8 + 2), null, noteName),
		  4, 9, 1, 1, 23);
	addWidget(secPane,
		  new ComboBoxWidget("", patch, new K4Model(patch, 12 + 7 * 8 + 2), null, noteName),
		  4, 10, 1, 1, 24);

	addWidget(secPane,
		  new LabelWidget("Vel. Sw."),
		  5, 2, 2, 1, 0);
	addWidget(secPane,
		  new ComboBoxWidget("", patch, new K4Model(patch, 12 + 0 * 8 + 3, 0x30), null, velocitySwitch),
		  5, 3, 2, 1, 1);
	addWidget(secPane,
		  new ComboBoxWidget("", patch, new K4Model(patch, 12 + 1 * 8 + 3, 0x30), null, velocitySwitch),
		  5, 4, 2, 1, 2);
	addWidget(secPane,
		  new ComboBoxWidget("", patch, new K4Model(patch, 12 + 2 * 8 + 3, 0x30), null, velocitySwitch),
		  5, 5, 2, 1, 3);
	addWidget(secPane,
		  new ComboBoxWidget("", patch, new K4Model(patch, 12 + 3 * 8 + 3, 0x30), null, velocitySwitch),
		  5, 6, 2, 1, 4);
	addWidget(secPane,
		  new ComboBoxWidget("", patch, new K4Model(patch, 12 + 4 * 8 + 3, 0x30), null, velocitySwitch),
		  5, 7, 2, 1, 5);
	addWidget(secPane,
		  new ComboBoxWidget("", patch, new K4Model(patch, 12 + 5 * 8 + 3, 0x30), null, velocitySwitch),
		  5, 8, 2, 1, 6);
	addWidget(secPane,
		  new ComboBoxWidget("", patch, new K4Model(patch, 12 + 6 * 8 + 3, 0x30), null, velocitySwitch),
		  5, 9, 2, 1, 7);
	addWidget(secPane,
		  new ComboBoxWidget("", patch, new K4Model(patch, 12 + 7 * 8 + 3, 0x30), null, velocitySwitch),
		  5, 10, 2, 1, 8);

	addWidget(secPane, new LabelWidget("Recv. Ch."), 7, 2, 1, 1, 0);
	addWidget(secPane,
		  new ComboBoxWidget("", patch, new K4Model(patch, 12 + 0 * 8 + 3, 0x0f), null, channels),
		  7, 3, 1, 1, 8);
	addWidget(secPane,
		  new ComboBoxWidget("", patch, new K4Model(patch, 12 + 1 * 8 + 3, 0x0f), null, channels),
		  7, 4, 1, 1, 8);
	addWidget(secPane,
		  new ComboBoxWidget("", patch, new K4Model(patch, 12 + 2 * 8 + 3, 0x0f), null, channels),
		  7, 5, 1, 1, 8);
	addWidget(secPane,
		  new ComboBoxWidget("", patch, new K4Model(patch, 12 + 3 * 8 + 3, 0x0f), null, channels),
		  7, 6, 1, 1, 8);
	addWidget(secPane,
		  new ComboBoxWidget("", patch, new K4Model(patch, 12 + 4 * 8 + 3, 0x0f), null, channels),
		  7, 7, 1, 1, 8);
	addWidget(secPane,
		  new ComboBoxWidget("", patch, new K4Model(patch, 12 + 5 * 8 + 3, 0x0f), null, channels),
		  7, 8, 1, 1, 8);
	addWidget(secPane,
		  new ComboBoxWidget("", patch, new K4Model(patch, 12 + 6 * 8 + 3, 0x0f), null, channels),
		  7, 9, 1, 1, 8);
	addWidget(secPane,
		  new ComboBoxWidget("", patch, new K4Model(patch, 12 + 7 * 8 + 3, 0x0f), null, channels),
		  7, 10, 1, 1, 8);

	addWidget(secPane, new LabelWidget("Play Mode"), 9, 2, 2, 1, 0);
	addWidget(secPane,
		  new ComboBoxWidget("", patch, new K4Model(patch, 12 + 0 * 8 + 4, 0x18), null, mode),
		  9, 3, 2, 1, 9);
	addWidget(secPane,
		  new ComboBoxWidget("", patch, new K4Model(patch, 12 + 1 * 8 + 4, 0x18), null, mode),
		  9, 4, 2, 1, 10);
	addWidget(secPane,
		  new ComboBoxWidget("", patch, new K4Model(patch, 12 + 2 * 8 + 4, 0x18), null, mode),
		  9, 5, 2, 1, 11);
	addWidget(secPane,
		  new ComboBoxWidget("", patch, new K4Model(patch, 12 + 3 * 8 + 4, 0x18), null, mode),
		  9, 6, 2, 1, 12);
	addWidget(secPane,
		  new ComboBoxWidget("", patch, new K4Model(patch, 12 + 4 * 8 + 4, 0x18), null, mode),
		  9, 7, 2, 1, 13);
	addWidget(secPane,
		  new ComboBoxWidget("", patch, new K4Model(patch, 12 + 5 * 8 + 4, 0x18), null, mode),
		  9, 8, 2, 1, 14);
	addWidget(secPane,
		  new ComboBoxWidget("", patch, new K4Model(patch, 12 + 6 * 8 + 4, 0x18), null, mode),
		  9, 9, 2, 1, 15);
	addWidget(secPane,
		  new ComboBoxWidget("", patch, new K4Model(patch, 12 + 7 * 8 + 4, 0x18), null, mode),
		  9, 10, 2, 1, 16);

	addWidget(secPane, new LabelWidget("Level"), 11, 2, 2, 1, 0);
	addWidget(secPane,
		  new ScrollBarWidget("", patch, 0, 100, 0, new K4Model(patch, 12 + 0 * 8 + 5), null),
		  11, 3, 2, 1, 1);
	addWidget(secPane,
		  new ScrollBarWidget("", patch, 0, 100, 0, new K4Model(patch, 12 + 1 * 8 + 5), null),
		  11, 4, 2, 1, 1);
	addWidget(secPane,
		  new ScrollBarWidget("", patch, 0, 100, 0, new K4Model(patch, 12 + 2 * 8 + 5), null),
		  11, 5, 2, 1, 1);
	addWidget(secPane,
		  new ScrollBarWidget("", patch, 0, 100, 0, new K4Model(patch, 12 + 3 * 8 + 5), null),
		  11, 6, 2, 1, 1);
	addWidget(secPane,
		  new ScrollBarWidget("", patch, 0, 100, 0, new K4Model(patch, 12 + 4 * 8 + 5), null),
		  11, 7, 2, 1, 1);
	addWidget(secPane,
		  new ScrollBarWidget("", patch, 0, 100, 0, new K4Model(patch, 12 + 5 * 8 + 5), null),
		  11, 8, 2, 1, 1);
	addWidget(secPane,
		  new ScrollBarWidget("", patch, 0, 100, 0, new K4Model(patch, 12 + 6 * 8 + 5), null),
		  11, 9, 2, 1, 1);
	addWidget(secPane,
		  new ScrollBarWidget("", patch, 0, 100, 0, new K4Model(patch, 12 + 7 * 8 + 5), null),
		  11, 10, 2, 1, 1);

	addWidget(secPane, new LabelWidget("Transpose"), 13, 2, 1, 1, 0);
	addWidget(secPane,
		  new ComboBoxWidget("", patch, new K4Model(patch, 12 + 0 * 8 + 6), null, transpose),
		  13, 3, 1, 1, 8);
	addWidget(secPane,
		  new ComboBoxWidget("", patch, new K4Model(patch, 12 + 1 * 8 + 6), null, transpose),
		  13, 4, 1, 1, 8);
	addWidget(secPane,
		  new ComboBoxWidget("", patch, new K4Model(patch, 12 + 2 * 8 + 6), null, transpose),
		  13, 5, 1, 1, 8);
	addWidget(secPane,
		  new ComboBoxWidget("", patch, new K4Model(patch, 12 + 3 * 8 + 6), null, transpose),
		  13, 6, 1, 1, 8);
	addWidget(secPane,
		  new ComboBoxWidget("", patch, new K4Model(patch, 12 + 4 * 8 + 6), null, transpose),
		  13, 7, 1, 1, 8);
	addWidget(secPane,
		  new ComboBoxWidget("", patch, new K4Model(patch, 12 + 5 * 8 + 6), null, transpose),
		  13, 8, 1, 1, 8);
	addWidget(secPane,
		  new ComboBoxWidget("", patch, new K4Model(patch, 12 + 6 * 8 + 6), null, transpose),
		  13, 9, 1, 1, 8);
	addWidget(secPane,
		  new ComboBoxWidget("", patch, new K4Model(patch, 12 + 7 * 8 + 6), null, transpose),
		  13, 10, 1, 1, 8);

	addWidget(secPane, new LabelWidget("Tune"), 14, 2, 1, 1, 0);
	addWidget(secPane,
		  new ComboBoxWidget("", patch, new K4Model(patch, 12 + 0 * 8 + 7), null, tune),
		  14, 3, 1, 1, 8);
	addWidget(secPane,
		  new ComboBoxWidget("", patch, new K4Model(patch, 12 + 1 * 8 + 7), null, tune),
		  14, 4, 1, 1, 8);
	addWidget(secPane,
		  new ComboBoxWidget("", patch, new K4Model(patch, 12 + 2 * 8 + 7), null, tune),
		  14, 5, 1, 1, 8);
	addWidget(secPane,
		  new ComboBoxWidget("", patch, new K4Model(patch, 12 + 3 * 8 + 7), null, tune),
		  14, 6, 1, 1, 8);
	addWidget(secPane,
		  new ComboBoxWidget("", patch, new K4Model(patch, 12 + 4 * 8 + 7), null, tune),
		  14, 7, 1, 1, 8);
	addWidget(secPane,
		  new ComboBoxWidget("", patch, new K4Model(patch, 12 + 5 * 8 + 7), null, tune),
		  14, 8, 1, 1, 8);
	addWidget(secPane,
		  new ComboBoxWidget("", patch, new K4Model(patch, 12 + 6 * 8 + 7), null, tune),
		  14, 9, 1, 1, 8);
	addWidget(secPane,
		  new ComboBoxWidget("", patch, new K4Model(patch, 12 + 7 * 8 + 7), null, tune),
		  14, 10, 1, 1, 8);

	addWidget(secPane, new LabelWidget("Submix"), 15, 2, 1, 1, 0);
	addWidget(secPane,
		  new ComboBoxWidget("", patch, new K4Model(patch, 12 + 0 * 8 + 4, 7), null, submix),
		  15, 3, 1, 1, 8);
	addWidget(secPane,
		  new ComboBoxWidget("", patch, new K4Model(patch, 12 + 1 * 8 + 4, 7), null, submix),
		  15, 4, 1, 1, 8);
	addWidget(secPane,
		  new ComboBoxWidget("", patch, new K4Model(patch, 12 + 2 * 8 + 4, 7), null, submix),
		  15, 5, 1, 1, 8);
	addWidget(secPane,
		  new ComboBoxWidget("", patch, new K4Model(patch, 12 + 3 * 8 + 4, 7), null, submix),
		  15, 6, 1, 1, 8);
	addWidget(secPane,
		  new ComboBoxWidget("", patch, new K4Model(patch, 12 + 4 * 8 + 4, 7), null, submix),
		  15, 7, 1, 1, 8);
	addWidget(secPane,
		  new ComboBoxWidget("", patch, new K4Model(patch, 12 + 5 * 8 + 4, 7), null, submix),
		  15, 8, 1, 1, 8);
	addWidget(secPane,
		  new ComboBoxWidget("", patch, new K4Model(patch, 12 + 6 * 8 + 4, 7), null, submix),
		  15, 9, 1, 1, 8);
	addWidget(secPane,
		  new ComboBoxWidget("", patch, new K4Model(patch, 12 + 7 * 8 + 4, 7), null, submix),
		  15, 10, 1, 1, 8);

	gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1; gbc.gridheight = 1;
	gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.NORTHWEST;
	scrollPane.add(cmnPane, gbc);

	gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1; gbc.gridheight = 1;
	gbc.fill = GridBagConstraints.BOTH; gbc.anchor = GridBagConstraints.NORTHWEST;
	scrollPane.add(secPane, gbc);

	pack();
    }
}
