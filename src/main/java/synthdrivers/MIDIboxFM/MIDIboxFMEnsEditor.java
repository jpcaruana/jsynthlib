/*
 * JSynthlib-Editor for MIDIbox FM
 * =====================================================================
 * @author  Thorsten Klose
 * @version $Id$
 *
 * Copyright (C) 2005  Thorsten.Klose@gmx.de
 *                     http://www.uCApps.de
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
 */

package synthdrivers.MIDIboxFM;

import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.jsynthlib.core.CheckBoxWidget;
import org.jsynthlib.core.ComboBoxWidget;
import org.jsynthlib.core.Patch;
import org.jsynthlib.core.PatchEditorFrame;
import org.jsynthlib.core.ScrollBarWidget;


class MIDIboxFMEnsEditor extends PatchEditorFrame
{

    final String[] NoteName = new String[] {
	"c-2","c#2","d-2","d#2","e-2","f-2","f#2","g-2","g#2","a-2","a#2","b-2",
	"c-1","c#1","d-1","d#1","e-1","f-1","f#1","g-1","g#1","a-1","a#1","b-1",
	"C-0","C#0","D-0","D#0","E-0","F-0","F#0","G-0","G#0","A-0","A#0","B-0",
	"C-1","C#1","D-1","D#1","E-1","F-1","F#1","G-1","G#1","A-1","A#1","B-1",
	"C-2","C#2","D-2","D#2","E-2","F-2","F#2","G-2","G#2","A-2","A#2","B-2",
	"C-3","C#3","D-3","D#3","E-3","F-3","F#3","G-3","G#3","A-3","A#3","B-3",
	"C-4","C#4","D-4","D#4","E-4","F-4","F#4","G-4","G#4","A-4","A#4","B-4",
	"C-5","C#5","D-5","D#5","E-5","F-5","F#5","G-5","G#5","A-5","A#5","B-5",
	"C-6","C#6","D-6","D#6","E-6","F-6","F#6","G-6","G#6","A-6","A#6","B-6",
	"C-7","C#7","D-7","D#7","E-7","F-7","F#7","G-7","G#7","A-7","A#7","B-7",
	"C-8","C#8","D-8","D#8","E-8","F-8","F#8","G-8"};

    final String[] ChannelName = new String[] {
	" 1", " 2", " 3", " 4", " 5", " 6", " 7", " 8", 
	" 9", "10", "11", "12", "13", "14", "15", "16"};

    final String[] BankName = new String[] {
	"A", "B", "C", "D", "E", "F", "G", "H"};

    final String[] GMPatchName = new String[] {
	"  1 | Acoustic Grand  ",
	"  2 | Bright Acoustic ",
	"  3 | Electric Grand P",
	"  4 | Honky-tonk      ",
	"  5 | Rhodes Piano    ",
	"  6 | Chorused Piano  ",
	"  7 | Harpsichord     ",
	"  8 | Clavinet        ",
	"  9 | Celesta         ",
	" 10 | Glockenspiel    ",
	" 11 | Musicbox        ",
	" 12 | Vibraphone      ",
	" 13 | Marimba         ",
	" 14 | Xylophone       ",
	" 15 | Tubular Bells   ",
	" 16 | Dulcimer        ",
	" 17 | Hammond Organ   ",
	" 18 | Percussive Organ",
	" 19 | Rock Organ      ",
	" 20 | Church Organ    ",
	" 21 | Reed Organ      ",
	" 22 | Accordion       ",
	" 23 | Harmonica       ",
	" 24 | Tango Accordion ",
	" 25 | Nylon Guitar    ",
	" 26 | Steel Guitar    ",
	" 27 | Jazz Guitar     ",
	" 28 | Clean Guitar    ",
	" 29 | Muted Guitar    ",
	" 30 | OverdrivenGuitar",
	" 31 | Distortion Guita",
	" 32 | Guitar Harmonics",
	" 33 | Acoustic Bass   ",
	" 34 | Finger Bass     ",
	" 35 | Pick Bass       ",
	" 36 | Fretless Bass   ",
	" 37 | Slap Bass 1     ",
	" 38 | Slap Bass 2     ",
	" 39 | Synth Bass 1    ",
	" 40 | Synth Bass 2    ",
	" 41 | Violin          ",
	" 42 | Viola           ",
	" 43 | Cello           ",
	" 44 | Contrabass      ",
	" 45 | Tremolo Strings ",
	" 46 | Pizzicato String",
	" 47 | Orchestral Harp ",
	" 48 | Timpani         ",
	" 49 | String Ensemble1",
	" 50 | String Ensemble2",
	" 51 | SynthStrings 1  ",
	" 52 | SynthStrings 2  ",
	" 53 | Choir Aahs      ",
	" 54 | Voice Oohs      ",
	" 55 | Synth Voice     ",
	" 56 | Orchestra Hit   ",
	" 57 | Trumpet         ",
	" 58 | Trombone        ",
	" 59 | Tuba            ",
	" 60 | Muted Trumpet   ",
	" 61 | French Horn     ",
	" 62 | Brass Section   ",
	" 63 | Synth Brass 1   ",
	" 64 | Synth Brass 2   ",
	" 65 | Soprano Sax     ",
	" 66 | Alto Sax        ",
	" 67 | Tenor Sax       ",
	" 68 | Baritone Sax    ",
	" 69 | Oboe            ",
	" 70 | English Horn    ",
	" 71 | Bassoon         ",
	" 72 | Clarinet        ",
	" 73 | Piccolo         ",
	" 74 | Flute           ",
	" 75 | Recorder        ",
	" 76 | Pan Flute       ",
	" 77 | Bottle Blow     ",
	" 78 | Shakuhachi      ",
	" 79 | Whistle         ",
	" 80 | Ocarina         ",
	" 81 | Lead1 squareea  ",
	" 82 | Lead2 sawtooth  ",
	" 83 | Lead3 calliope  ",
	" 84 | Lead4 chiff     ",
	" 85 | Lead5 charang   ",
	" 86 | Lead6 voice     ",
	" 87 | Lead7 fifths    ",
	" 88 | Lead8 brass+ld  ",
	" 89 | Pad1 new age    ",
	" 90 | Pad2 warm       ",
	" 91 | Pad3 polysynth  ",
	" 92 | Pad4 choir      ",
	" 93 | Pad5 bowed      ",
	" 94 | Pad6 metallic   ",
	" 95 | Pad7 halo       ",
	" 96 | Pad8 sweep      ",
	" 97 | FX1 rain        ",
	" 98 | FX2 soundtrack  ",
	" 99 | FX3 crystal     ",
	"100 | FX4  atmosphere ",
	"101 | FX5 brightness  ",
	"102 | FX6 goblins     ",
	"103 | FX7  echoes     ",
	"104 | FX8 sci-fi      ",
	"105 | Sitar           ",
	"106 | Banjo           ",
	"107 | Shamisen        ",
	"108 | Koto            ",
	"109 | Kalimba         ",
	"110 | Bagpipe         ",
	"111 | Fiddle          ",
	"112 | Shanai          ",
	"113 | Tinkle Bell     ",
	"114 | Agogo Bells     ",
	"115 | Steel Drums     ",
	"116 | Woodblock       ",
	"117 | Taiko Drum      ",
	"118 | Melodic Tom     ",
	"119 | Synth Drum      ",
	"120 | Reverse Cymbal  ",
	"121 | Guitar Fret Nois",
	"122 | Breath Noise    ",
	"123 | Seashore        ",
	"124 | Bird Tweet      ",
	"125 | Telephone       ",
	"126 | Helicopter Blade",
	"127 | Applause/Noise  ",
	"128 | Gunshot         "};

    public MIDIboxFMEnsEditor(Patch patch)
    {
	super ("MIDIbox FM Ensemble Editor",patch);
	gbc.weightx=0; gbc.weighty=0;

	///////////////////////////////////////////////////////////////////////////////
	//  Instrument Panel
	///////////////////////////////////////////////////////////////////////////////
	JTabbedPane IPanel=new JTabbedPane();
	for(int i=0; i<4; ++i)
	{
	    JPanel panel = new JPanel();
	    panel.setLayout(new GridBagLayout());
	    IPanel.addTab("I" + i, panel);

	    ///////////////////////////////////////////////////////////////////
	    JPanel midipane=new JPanel();
	    midipane.setLayout(new GridBagLayout());

	    addWidget(midipane,new ComboBoxWidget("MIDI channel",patch,new MIDIboxFMModel(patch,0x02+i*0x10),new MIDIboxFMSender(patch,0x70,0x02+i*0x10),ChannelName),0,0,5,1,1);
	    addWidget(midipane,new ComboBoxWidget("Lower Limit",patch,new MIDIboxFMModel(patch,0x03+i*0x10),new MIDIboxFMSender(patch,0x70,0x03+i*0x10),NoteName),0,1,5,1,2);
	    addWidget(midipane,new ComboBoxWidget("Upper Limit",patch,new MIDIboxFMModel(patch,0x04+i*0x10),new MIDIboxFMSender(patch,0x70,0x04+i*0x10),NoteName),0,2,5,1,3);
	    addWidget(midipane,new ComboBoxWidget("Velocity Mode",patch,new MIDIboxFMModel(patch,0x05+i*0x10,0,0x3,new int[]{0x0,0x01,0x2}),new MIDIboxFMSender(patch,0x70,0x05+i*0x10,0,0x3,new int[]{0x0,0x01,0x2}),new String []{"FLAT", "HARD", "SOFT"}),0,3,5,1,4);

	    gbc.gridx=0;gbc.gridy=0;gbc.gridwidth=5;gbc.gridheight=6;
	    panel.add(midipane,gbc);

	    ///////////////////////////////////////////////////////////////////
	    JPanel compane=new JPanel();
	    compane.setLayout(new GridBagLayout());

	    addWidget(compane,new ComboBoxWidget("Bank",patch,new MIDIboxFMModel(patch,0x00+i*0x10),new MIDIboxFMSender(patch,0x70,0x00+i*0x10),BankName),0,0,4,1,5);
	    addWidget(compane,new ComboBoxWidget("Patch",patch,new MIDIboxFMModel(patch,0x01+i*0x10),new MIDIboxFMSender(patch,0x70,0x01+i*0x10),GMPatchName),4,0,6,1,6);
	    addWidget(compane,new ScrollBarWidget("Volume",patch,0,127,0,new MIDIboxFMModel(patch,0x06+i*0x10),new MIDIboxFMSender(patch,0x70,0x06+i*0x10)),0,1,10,1,7);
	    addWidget(compane,new ScrollBarWidget("Transpose",patch,0,127,-64,new MIDIboxFMModel(patch,0x07+i*0x10),new MIDIboxFMSender(patch,0x70,0x07+i*0x10)),0,2,10,1,8);
	    addWidget(compane,new ScrollBarWidget("Unisono",patch,0,127,0,new MIDIboxFMModel(patch,0x08+i*0x10),new MIDIboxFMSender(patch,0x70,0x08+i*0x10)),0,3,10,1,9);

	    gbc.gridx=5;gbc.gridy=0;gbc.gridwidth=5;gbc.gridheight=3;
	    panel.add(compane,gbc);

	    ///////////////////////////////////////////////////////////////////
	    JPanel chnpane=new JPanel();
	    chnpane.setLayout(new GridBagLayout());

	    gbc.gridx= 0;      gbc.gridy=0;gbc.gridwidth=3;gbc.gridheight=1; chnpane.add(new JLabel("Chn"),gbc);
	    gbc.gridx= 3+1+0*3;gbc.gridy=0;gbc.gridwidth=3;gbc.gridheight=1; chnpane.add(new JLabel("#1"),gbc);
	    gbc.gridx= 3+1+1*3;gbc.gridy=0;gbc.gridwidth=3;gbc.gridheight=1; chnpane.add(new JLabel("#2"),gbc);
	    gbc.gridx= 3+1+2*3;gbc.gridy=0;gbc.gridwidth=3;gbc.gridheight=1; chnpane.add(new JLabel("#3"),gbc);
	    gbc.gridx= 3+1+3*3;gbc.gridy=0;gbc.gridwidth=3;gbc.gridheight=1; chnpane.add(new JLabel("#4"),gbc);

	    gbc.gridx=0;gbc.gridy=1;gbc.gridwidth=3;gbc.gridheight=1; chnpane.add(new JLabel("OP12"),gbc);
	    for(int j=0; j<4; ++j) 
	    {
		gbc.gridx= 3+j*3;gbc.gridy=1;gbc.gridwidth=1;gbc.gridheight=1; chnpane.add(new JLabel("    "),gbc);
		addWidget(chnpane,new CheckBoxWidget("",  patch,new MIDIboxFMModel(patch,0x09+0x10*i,j),new MIDIboxFMSender(patch,0x70,0x09+0x10*i,j)),3+1+3*j,1,1,1,-1-j);
	    }

	    gbc.gridx=0;gbc.gridy=2;gbc.gridwidth=3;gbc.gridheight=1; chnpane.add(new JLabel("OP34"),gbc);
	    for(int j=0; j<4; ++j) 
	    {
		gbc.gridx= 3+j*3;gbc.gridy=2;gbc.gridwidth=1;gbc.gridheight=1; chnpane.add(new JLabel("    "),gbc);
		addWidget(chnpane,new CheckBoxWidget("",  patch,new MIDIboxFMModel(patch,0x0a+0x10*i,j),new MIDIboxFMSender(patch,0x70,0x0a+0x10*i,j)),3+1+3*j,2,2,1,-4-j);
	    }

	    gbc.gridx=5;gbc.gridy=3;gbc.gridwidth=5;gbc.gridheight=2;
	    panel.add(chnpane,gbc);
	}

	gbc.gridx=0;gbc.gridy=0;gbc.gridwidth=15;gbc.gridheight=6;
	scrollPane.add(IPanel,gbc);

	pack();
    }
}
