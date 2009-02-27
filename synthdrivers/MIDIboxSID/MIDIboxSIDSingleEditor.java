/*
 * JSynthlib-Device for MIDIbox SID
 * =====================================================================
 * @author  Thorsten Klose
 * file:    MIDIboxSIDSingleEditor.java
 * date:    2002-11-30
 * @version $Id$
 *
 * Copyright (C) 2002  Thorsten.Klose@gmx.de
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

package synthdrivers.MIDIboxSID;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;

import core.CheckBoxWidget;
import core.ComboBoxWidget;
import core.Driver;
import core.EnvelopeWidget;
import core.ParamModel;
import core.Patch;
import core.PatchEditorFrame;
import core.PatchNameWidget;
import core.ScrollBarWidget;
import core.KnobWidget;
import core.SysexSender;

class MIDIboxSIDSingleEditor extends PatchEditorFrame
{
    final String [] ccName = new String [] {
	"  0 | -",              
	"  1 | Modulation Wheel",
	"  2 | Velocity Init Value",
	"  3 | Modulation Wheel Init Value",
	"  4 | Aftertouch Init Value",
	"  5 | Voice 123 Portamento",
	"  6 | -",
	"  7 | Volume (00h-7Fh)",
	"  8 | Voice 1/2/3 Note",
	"  9 | Voice 1 Note",
	" 10 | Voice 2 Note",
	" 11 | Voice 3 Note",
	" 12 | WT Patch number",               
	" 13 | Velocity Depth",  
	" 14 | Modulation Wheel",
	" 15 | Aftertouch Depth",
	" 16 | Voice 1/2/3 Transpose",
	" 17 | Voice 1 Single Transpose",
	" 18 | Voice 2 Single Tranpose",
	" 19 | Voice 3 Single Tranpose",
	" 20 | Voice 1/2/3 Finetune",
	" 21 | Voice 1 Single Finetune",
	" 24 | Voice 2 Single Finetune",
	" 23 | Voice 3 Single Finetune",
	" 24 | Voice 1/2/3 Pitch",
	" 25 | Voice 1 Single Pitch",
	" 26 | Voice 2 Single Pitch",
	" 27 | Voice 3 Single Pitch",
	" 28 | Voice 1/2/3 Portamento",
	" 29 | Voice 1 Portamento",
	" 30 | Voice 2 Portamento",
	" 31 | Voice 3 Portamento",
	" 32 | Voice 1/2/3 Waveform",
	" 33 | Voice 1 Waveform", 
	" 34 | Voice 2 Waveform", 
	" 35 | Voice 3 Waveform", 
	" 36 | Voice 1/2/3 Pulse",
	" 37 | Voice 1 Pulsewidth",
	" 38 | Voice 2 Pulsewidth",
	" 39 | Voice 3 Pulsewidth",
	" 40 | Voice 1/2/3 Arpeggiator",
	" 41 | Voice 1 Arpeggiator",
	" 42 | Voice 2 Arpeggiator",
	" 43 | Voice 3 Arpeggiator",
	" 44 | Filter Channels",  
	" 45 | Filter Mode",      
	" 46 | Filter CutOff frequency",
	" 47 | Filter Resonance", 
	" 48 | Voice 1/2/3 Attack",
	" 49 | Voice 1 Single Attack",
	" 50 | Voice 2 Single Attack",
	" 51 | Voice 3 Single Attack",
	" 52 | Voice 1/2/3 Decay",
	" 53 | Voice 1 Single Decay",
	" 54 | Voice 2 Single Decay",
	" 55 | Voice 3 Single Decay",
	" 56 | Voice 1/2/3 Sustain",
	" 57 | Voice 1 Single Sustain",
	" 58 | Voice 2 Single Sustain",
	" 59 | Voice 3 Single Sustain",
	" 60 | Voice 1/2/3 Release",
	" 61 | Voice 1 Release",  
	" 62 | Voice 2 Release",  
	" 63 | Voice 3 Release",  
	" 64 | LFO1 Rate",        
	" 65 | LFO2 Rate",        
	" 66 | LFO3 Rate",        
	" 67 | LFO4 Rate",        
	" 68 | LFO5 Rate",        
	" 69 | LFO6 Rate",        
	" 70 | -",                
	" 71 | -",                
	" 72 | LFO1 Depth",       
	" 73 | LFO2 Depth",       
	" 74 | LFO3 Depth",       
	" 75 | LFO4 Depth",       
	" 76 | LFO5 Depth",       
	" 77 | LFO6 Depth",       
	" 78 | ENV1 Depth",       
	" 79 | ENV2 Depth",       
	" 80 | LFO1 Mode",        
	" 81 | LFO2 Mode",        
	" 82 | LFO3 Mode",        
	" 83 | LFO4 Mode",        
	" 84 | LFO5 Mode",        
	" 85 | LFO6 Mode",        
	" 86 | Envelope 1 Curve",
	" 87 | Envelope 2 Curve",
	" 88 | Envelope 1 Attack",
	" 89 | Envelope 1 Decay", 
	" 90 | Envelope 1 Sustain",
	" 91 | Envelope 1 Release",
	" 92 | Envelope 2 Attack",
	" 93 | Envelope 2 Decay", 
	" 94 | Envelope 2 Sustain",
	" 95 | Envelope 2 Release",
	" 96 | Voice 1/2/3 assigned LFOs for Pitch",
	" 97 | Voice 1 assigned LFOs for Pitch", 
	" 98 | Voice 2 assigned LFOs for Pitch", 
	" 99 | Voice 3 assigned LFOs for Pitch", 
	"100 | Voice 1/2/3 assigned LFOs for PW",
	"101 | Voice 1 assigned LFOs for PW", 
	"102 | Voice 2 assigned LFOs for PW", 
	"103 | Voice 3 assigned LFOs for PW", 
	"104 | Voice 1/2/3 assigned Envelopes",
	"105 | Voice 1 assigned Envelopes", 
	"106 | Voice 2 assigned Envelopes", 
	"107 | Voice 3 assigned Envelopes", 
	"108 | Assigned LFOs for Filter",
	"109 | Assigned Envelopes for Filter",
	"110 | Assigned Envelope Curves",
	"111 | Sound Engine Options",
	"112 | Voice 1/2/3 Note Delay",
	"113 | Voice 1 Single Note Delay",
	"114 | Voice 2 Single Note Delay",
	"115 | Voice 3 Single Note Delay",
	"116 | Sus-Key behaviour",
	"117 | Assign Velocity to Controller",
	"118 | Assign Modulation to Controller",
	"119 | Assign Aftertouch to Controller",
	"120 | Wavetable Rate",
	"121 | Assign Wavetable Par. #1 to CC",
	"122 | Assign Wavetable Par. #2 to CC",
	"123 | Assign Wavetable Par. #3 to CC",
	"124 | Oscillator Phase Synchronization",                
	"125 | MIDI Clock Synchronization",                
	"126 | Mono On",
	"127 | Poly On",
    };

    final String [] NoteName = new String [] {
	"---","c#2","d-2","d#2","e-2","f-2","f#2","g-2","g#2","a-2","a#2","b-2",
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

    MIDIboxSIDWavetableModel dataModel = new MIDIboxSIDWavetableModel();
    JTable table = new JTable(dataModel);
    JButton switchViewButton = new JButton(dataModel.getHexView() ? "Switch to Dec" : "Switch to Hex");
    JButton updateWavetableButton = new JButton("Update Wavetable");

    public MIDIboxSIDSingleEditor(Patch patch)
    {
	super ("MIDIbox SID Patch Editor",patch);
	gbc.weightx=0; gbc.weighty=0;

///////////////////////////////////////////////////////////////////////////////
//  Common Pane
///////////////////////////////////////////////////////////////////////////////
	JPanel cmnPane=new JPanel();
	cmnPane.setLayout(new GridBagLayout());
	addWidget(cmnPane,new PatchNameWidget(" Name  ",patch),0,0,5,1,0);

	// horizontal location, the vertical location, the horizontal size, and the vertical size). The last number is the fader number

        addWidget(cmnPane,new ComboBoxWidget("Play Mode",patch,new SIDModel(patch,0x11,0,0x7f,new int[]{0x0,0x01,0x3,0x7f}),new SIDSender(patch,0x11,0,0x7f,new int[]{0x0,0x1,0x3,0x7f}),new String []{"MONO", "LEGATO","WT Seq only","POLY"}),0,1,3,1,1);
	addWidget(cmnPane,new ComboBoxWidget("Portam. Mode",patch,new SIDModel(patch,0x12),new SIDSender(patch,0x12),new String []{"Full Time", "Fingered (SusKey)"}),0,2,3,1,2);
	addWidget(cmnPane,new KnobWidget("Volume",patch,0,127,0,new SIDModel(patch,0x10),new SIDSender(patch,0x10)),3,1,2,3,3);

	gbc.gridx=0;gbc.gridy=3;gbc.gridwidth=2;gbc.gridheight=1; cmnPane.add(new JLabel("SE Options:"),gbc);
	JPanel cmnOptPane=new JPanel();
	cmnOptPane.setLayout(new GridBagLayout());
	addWidget(cmnOptPane,new CheckBoxWidget("6",patch,new SIDModel(patch,0x16,6),new SIDSender(patch,0x16,6)),0,0,1,1,-200);
	addWidget(cmnOptPane,new CheckBoxWidget("5",patch,new SIDModel(patch,0x16,5),new SIDSender(patch,0x16,5)),1,0,1,1,-200);
	addWidget(cmnOptPane,new CheckBoxWidget("4",patch,new SIDModel(patch,0x16,4),new SIDSender(patch,0x16,4)),2,0,1,1,-200);
	addWidget(cmnOptPane,new CheckBoxWidget("3",patch,new SIDModel(patch,0x16,3),new SIDSender(patch,0x16,3)),3,0,1,1,-200);
	addWidget(cmnOptPane,new CheckBoxWidget("2",patch,new SIDModel(patch,0x16,2),new SIDSender(patch,0x16,2)),4,0,1,1,-200);
	addWidget(cmnOptPane,new CheckBoxWidget("1",patch,new SIDModel(patch,0x16,1),new SIDSender(patch,0x16,1)),5,0,1,1,-200);
	addWidget(cmnOptPane,new CheckBoxWidget("0",patch,new SIDModel(patch,0x16,0),new SIDSender(patch,0x16,0)),6,0,1,1,-200);
	gbc.gridx=2;gbc.gridy=3;gbc.gridwidth=3;gbc.gridheight=1;
	cmnPane.add(cmnOptPane,gbc);

	gbc.gridx=0;gbc.gridy=0;gbc.gridwidth=5;gbc.gridheight=2;
	cmnPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Common",TitledBorder.CENTER,TitledBorder.CENTER));  
	scrollPane.add(cmnPane,gbc);


///////////////////////////////////////////////////////////////////////////////
// ENVs
///////////////////////////////////////////////////////////////////////////////
	JTabbedPane ENVPane=new JTabbedPane();   
	for(int i=0; i<2; i++) {     
	    JPanel panel = new JPanel();
	    panel.setLayout(new GridBagLayout());
	    ENVPane.addTab("Envelope"+(i+1),panel);gbc.weightx=0;

	    addWidget(panel,new EnvelopeWidget("SW Envelope",patch,new EnvelopeWidget.Node[] {
		new EnvelopeWidget.Node(0,0,null,0,0,null,0,false,null,null,null,null),     
		new EnvelopeWidget.Node(0,127,new SIDModel(patch,0x73+i*5),127,127,null,25,false,new SIDSender(patch,0x73+i*5),null,"A",null),
		new EnvelopeWidget.Node(0,127,new SIDModel(patch,0x74+i*5),0,127,new SIDModel(patch,0x75+i*5),25,false,new SIDSender(patch,0x74+i*5),new SIDSender(patch,0x75+i*5),"D","S"),
		new EnvelopeWidget.Node(127,127,null,5000,5000,null,0,false,null,null,null,null),     
		new EnvelopeWidget.Node(0,127,new SIDModel(patch,0x76+i*5),0,0,null,0,false,new SIDSender(patch,0x76+i*5),null,"R",null),
		}     ),0,0,10,3,50);
	    addWidget(panel,new KnobWidget("Depth",patch,0,127,-64,new SIDModel(patch,0x72+i*5),new SIDSender(patch,0x72+i*5)),0,5,1,3,51);
	    addWidget(panel,new KnobWidget("Curve",patch,0,127,-64,new SIDModel(patch,0x1e+i),new SIDSender(patch,0x1e+i)),1,5,1,3,52);
	    gbc.gridx=2;gbc.gridy=5;gbc.gridwidth=3;gbc.gridheight=1; panel.add(new JLabel("Curve used on"),gbc);
	    addWidget(panel,new CheckBoxWidget("Attack", patch,new SIDModel(patch,0x15,0+i*4),new SIDSender(patch,0x15,0+i*4)),5,5,1,1,-100);
	    addWidget(panel,new CheckBoxWidget("Decay", patch,new SIDModel(patch,0x15,1+i*4),new SIDSender(patch,0x15,1+i*4)),5,6,1,1,-101);
	    addWidget(panel,new CheckBoxWidget("Release", patch,new SIDModel(patch,0x15,2+i*4),new SIDSender(patch,0x15,2+i*4)),5,7,1,1,-102);
	}
	gbc.gridx=0;gbc.gridy=GridBagConstraints.RELATIVE;gbc.gridwidth=5;gbc.gridheight=2;
	scrollPane.add(ENVPane,gbc);


///////////////////////////////////////////////////////////////////////////////
// LFOs
///////////////////////////////////////////////////////////////////////////////
	JTabbedPane LFOPane=new JTabbedPane();   
	for(int i=0; i<6; i++) {     
	    JPanel panel = new JPanel();
	    panel.setLayout(new GridBagLayout());
	    LFOPane.addTab("LFO"+(i+1),panel);gbc.weightx=0;

	    addWidget(panel,new KnobWidget("Rate",patch,0,127,0,new SIDModel(patch,0x61+i*3),new SIDSender(patch,0x61+i*3)),0,0,1,3,40);
	    addWidget(panel,new KnobWidget("Depth",patch,0,127,-64,new SIDModel(patch,0x62+i*3),new SIDSender(patch,0x62+i*3)),1,0,1,3,41);
	    addWidget(panel,new ComboBoxWidget("Mode",patch,new SIDModel(patch,0x60+i*3,0,0x7,new int[]{0x0,0x01,0x3,0x7}),new SIDSender(patch,0x60+i*3,0,0x7,new int[]{0x0,0x01,0x3,0x7}),new String []{"off", "unsynced","Sync w/ assigned notes","Sync w/ all notes"}),2,0,5,1,42);
	    addWidget(panel,new ComboBoxWidget("Waveform",patch,new SIDModel(patch,0x60+i*3,4,0x7),new SIDSender(patch,0x60+i*3,4,0x7),new String []{"SINE", "TRIANGLE","SAW","PULSE","RANDOM","AIN"}),3,1,5,1,43);
	}
	gbc.gridx=0;gbc.gridy=GridBagConstraints.RELATIVE;gbc.gridwidth=5;gbc.gridheight=1;
	scrollPane.add(LFOPane,gbc);


///////////////////////////////////////////////////////////////////////////////
// Controller Assigns
///////////////////////////////////////////////////////////////////////////////
	JTabbedPane assignPane=new JTabbedPane();

	// Assign Velocity Pane
  	JPanel LFOAPane=new JPanel();
	LFOAPane.setLayout(new GridBagLayout());
	assignPane.addTab("Velocity", LFOAPane); gbc.weightx=0;
  	addWidget(LFOAPane,new ComboBoxWidget("Controller",patch,new SIDModel(patch,0x50),new SIDSender(patch,0x50),ccName),0,0,5,1,4);
  	addWidget(LFOAPane,new ScrollBarWidget("Init Value",patch,0,127,0,new SIDModel(patch,0x51),new SIDSender(patch,0x51)),0,1,5,1,5);
  	addWidget(LFOAPane,new ScrollBarWidget("Depth",patch,0,127,-64,new SIDModel(patch,0x52),new SIDSender(patch,0x52)),0,2,5,1,6);

  	// Assign Mod Pane
  	JPanel MODAPane=new JPanel();
  	MODAPane.setLayout(new GridBagLayout());
	assignPane.addTab("ModWheel", MODAPane); gbc.weightx=0;
  	addWidget(MODAPane,new ComboBoxWidget("Controller",patch,new SIDModel(patch,0x53),new SIDSender(patch,0x53),ccName),0,0,5,1,7);
  	addWidget(MODAPane,new ScrollBarWidget("Init Value",patch,0,127,0,new SIDModel(patch,0x54),new SIDSender(patch,0x54)),0,1,5,1,8);
  	addWidget(MODAPane,new ScrollBarWidget("Depth",patch,0,127,-64,new SIDModel(patch,0x55),new SIDSender(patch,0x55)),0,2,5,1,9);

  	// Assign Aftertouch Pane
  	JPanel AFTAPane=new JPanel();
  	AFTAPane.setLayout(new GridBagLayout());
	assignPane.addTab("Aftertouch", AFTAPane); gbc.weightx=0;
  	addWidget(AFTAPane,new ComboBoxWidget("Controller",patch,new SIDModel(patch,0x56),new SIDSender(patch,0x56),ccName),0,0,5,1,10);
  	addWidget(AFTAPane,new ScrollBarWidget("Init Value",patch,0,127,0,new SIDModel(patch,0x57),new SIDSender(patch,0x57)),0,1,5,1,11);
  	addWidget(AFTAPane,new ScrollBarWidget("Depth",patch,0,127,-64,new SIDModel(patch,0x58),new SIDSender(patch,0x58)),0,2,5,1,12);

	gbc.gridx=0;gbc.gridy=GridBagConstraints.RELATIVE;gbc.gridwidth=5;gbc.gridheight=1;
	scrollPane.add(assignPane,gbc);


///////////////////////////////////////////////////////////////////////////////
// Oscillators
///////////////////////////////////////////////////////////////////////////////
	JTabbedPane oscPane=new JTabbedPane();
   
	for(int i=0; i<3; i++) {     
	    JPanel panel = new JPanel();
	    panel.setLayout(new GridBagLayout());
	    oscPane.addTab("OSC"+(i+1),panel);gbc.weightx=0;
	    addWidget(panel,new EnvelopeWidget("VCA Envelope",patch,new EnvelopeWidget.Node[] {
		new EnvelopeWidget.Node(0,0,null,0,0,null,0,false,null,null,null,null),     
		new EnvelopeWidget.Node(0,127,new SIDModel(patch,0x27+i*16),0,0,null,0,false,new SIDSender(patch,0x27+i*16),null,"Dly",null),
		new EnvelopeWidget.Node(0,127,new SIDModel(patch,0x28+i*16),127,127,null,25,false,new SIDSender(patch,0x28+i*16),null,"A",null),
		new EnvelopeWidget.Node(0,127,new SIDModel(patch,0x29+i*16),0,127,new SIDModel(patch,0x2a+i*16),25,false,new SIDSender(patch,0x29+i*16),new SIDSender(patch,0x2a+i*16),"D","S"),
		new EnvelopeWidget.Node(127,127,null,5000,5000,null,0,false,null,null,null,null),     
		new EnvelopeWidget.Node(0,127,new SIDModel(patch,0x2b+i*16),0,0,null,0,false,new SIDSender(patch,0x2b+i*16),null,"R",null),
		}     ),0,0,5,5,33);
	    addWidget(panel,new KnobWidget(" Transpose ",patch,0,127,-64,new SIDModel(patch,0x20+i*16),new SIDSender(patch,0x20+i*16)),0,6,1,3,20);
	    addWidget(panel,new KnobWidget(" FineTune ",patch,0,127,-64,new SIDModel(patch,0x21+i*16),new SIDSender(patch,0x21+i*16)),1,6,1,3,21);
	    addWidget(panel,new KnobWidget(" PitchRange ",patch,0,127,0,new SIDModel(patch,0x22+i*16),new SIDSender(patch,0x22+i*16)),2,6,1,3,22);
	    addWidget(panel,new KnobWidget(" Arpeggiator ",patch,0,127,0,new SIDModel(patch,0x26+i*16),new SIDSender(patch,0x26+i*16)),0,9,1,3,23);
	    addWidget(panel,new KnobWidget(" Portamento ",patch,0,127,0,new SIDModel(patch,0x23+i*16),new SIDSender(patch,0x23+i*16)),1,9,1,3,25);
	    addWidget(panel,new KnobWidget(" PulseWidth ",patch,0,127,0,new SIDModel(patch,0x25+i*16),new SIDSender(patch,0x25+i*16)),2,9,1,3,26);

	    addWidget(panel,new CheckBoxWidget("Tri",  patch,new SIDModel(patch,0x24+i*16,0),new SIDSender(patch,0x24+i*16,0)),3,7,1,1,-20);
	    addWidget(panel,new CheckBoxWidget("Saw",  patch,new SIDModel(patch,0x24+i*16,1),new SIDSender(patch,0x24+i*16,1)),3,8,1,1,-21);
	    addWidget(panel,new CheckBoxWidget("Rec",  patch,new SIDModel(patch,0x24+i*16,2),new SIDSender(patch,0x24+i*16,2)),4,7,1,1,-22);
	    addWidget(panel,new CheckBoxWidget("Noise",patch,new SIDModel(patch,0x24+i*16,3),new SIDSender(patch,0x24+i*16,3)),4,8,1,1,-23);
     
	    addWidget(panel,new CheckBoxWidget("Sync",patch,new SIDModel(patch,0x24+i*16,5),new SIDSender(patch,0x24+i*16,5)),3,9,1,1,-24);
	    addWidget(panel,new CheckBoxWidget("Ring",patch,new SIDModel(patch,0x24+i*16,6),new SIDSender(patch,0x24+i*16,6)),3,10,1,1,-25);
	    addWidget(panel,new CheckBoxWidget("Off", patch,new SIDModel(patch,0x24+i*16,4),new SIDSender(patch,0x24+i*16,4)),4,9,1,1,-26);
	    addWidget(panel,new CheckBoxWidget("Phase", patch,new SIDModel(patch,0x14,i),new SIDSender(patch,0x14,i)),4,10,1,1,-27);
	    addWidget(panel,new ComboBoxWidget("Lower Limit",patch,new SIDModel(patch,0x2f+i*16),new SIDSender(patch,0x2f+i*16),NoteName),0,12,2,1,27);
	    addWidget(panel,new ComboBoxWidget("Upper Limit",patch,new SIDModel(patch,0x5d+i),new SIDSender(patch,0x5d+i),NoteName),2,12,2,1,28);
	}
	gbc.gridx=5;gbc.gridy=0;gbc.gridwidth=5;gbc.gridheight=4;
	scrollPane.add(oscPane,gbc);


///////////////////////////////////////////////////////////////////////////////
// Wavetables
///////////////////////////////////////////////////////////////////////////////
	JPanel WAVTPane=new JPanel();
	WAVTPane.setLayout(new GridBagLayout());

	ComboBoxWidget  WT1CCBox  = new ComboBoxWidget("Parameter #1",patch,new SIDModel(patch,0x5a),new SIDSender(patch,0x5a),ccName);
	dataModel.setColumnCC(0, (byte)((Patch)p).sysex[8+0x5a]);
	ComboBoxWidget  WT2CCBox  = new ComboBoxWidget("Parameter #2",patch,new SIDModel(patch,0x5b),new SIDSender(patch,0x5b),ccName);
	dataModel.setColumnCC(1, (byte)((Patch)p).sysex[8+0x5b]);
	ComboBoxWidget  WT3CCBox  = new ComboBoxWidget("Parameter #3",patch,new SIDModel(patch,0x5c),new SIDSender(patch,0x5c),ccName);
	dataModel.setColumnCC(2, (byte)((Patch)p).sysex[8+0x5c]);
	
	table.repaint();

        WT1CCBox.addEventListener(new ActionListener() { public void actionPerformed(ActionEvent e) {
	    dataModel.setColumnCC(0, (byte)((Patch)p).sysex[8+0x5a]); table.repaint(); } });
        WT2CCBox.addEventListener(new ActionListener() { public void actionPerformed(ActionEvent e) {
	    dataModel.setColumnCC(1, (byte)((Patch)p).sysex[8+0x5b]); table.repaint(); } });
        WT3CCBox.addEventListener(new ActionListener() { public void actionPerformed(ActionEvent e) {
	    dataModel.setColumnCC(2, (byte)((Patch)p).sysex[8+0x5c]); table.repaint(); } });


	ScrollBarWidget WTRateBar = new ScrollBarWidget("Rate",patch,0,127,0,new SIDModel(patch,0x59),new SIDSender(patch,0x59));

	addWidget(WAVTPane,WT1CCBox,0,0,3,1,13);
	addWidget(WAVTPane,WT2CCBox,0,1,3,1,14);
	addWidget(WAVTPane,WT3CCBox,0,2,3,1,15);
	addWidget(WAVTPane,WTRateBar,0,3,5,1,16);


        table.setPreferredScrollableViewportSize(new Dimension(100, 100));
        JScrollPane scrollpane = new JScrollPane(table);
	gbc.gridx=0;gbc.gridy=5;gbc.gridwidth=5;gbc.gridheight=20;
        WAVTPane.add(scrollpane, gbc);
        
        TableColumn column = null;

        column = table.getColumnModel().getColumn(0);
        column = table.getColumnModel().getColumn(1);
	JComboBox comboBox = new JComboBox();
	for(int i=0; i<dataModel.modeNames.length; ++i) {
	    comboBox.addItem(dataModel.modeNames[i]);
	}
	column.setCellEditor(new DefaultCellEditor(comboBox));
        column = table.getColumnModel().getColumn(2);
        column = table.getColumnModel().getColumn(3);
        column = table.getColumnModel().getColumn(4);

	byte[] cooked_dump = dataModel.getCookedDump();
	for(int i=0; i<cooked_dump.length; ++i)
	    cooked_dump[i] = ((Patch)p).sysex[8+0x80+i];
	dataModel.setCookedDump(cooked_dump);

	table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	ListSelectionModel tableSM = table.getSelectionModel();
	tableSM.addListSelectionListener(new ListSelectionListener() {
		public void valueChanged(ListSelectionEvent e) {
		    sendDumpChanges();
		}
	    });

	// JButton switchViewButton = new JButton(dataModel.getHexView() ? "Switch to Dec" : "Switch to Hex");
	// (defined as global button)
        switchViewButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e)
		{
		    viewPressed();
		}
	    });
	gbc.gridx=0;gbc.gridy=GridBagConstraints.RELATIVE;gbc.gridwidth=1;gbc.gridheight=1;
        WAVTPane.add(switchViewButton, gbc);

	// JButton updateWavetableButton = new JButton("Update Wavetable");	
	// (defined as global button)
        updateWavetableButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e)
		{
		    sendDumpChanges();
		}
	    });
	gbc.gridx=1;gbc.gridy=GridBagConstraints.RELATIVE;gbc.gridwidth=1;gbc.gridheight=1;
        WAVTPane.add(updateWavetableButton, gbc);

	gbc.gridx=5;gbc.gridy=GridBagConstraints.RELATIVE;gbc.gridwidth=5;gbc.gridheight=3;
	WAVTPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Wavetable",TitledBorder.CENTER,TitledBorder.CENTER));  
	scrollPane.add(WAVTPane,gbc);



///////////////////////////////////////////////////////////////////////////////
// Filter
///////////////////////////////////////////////////////////////////////////////
	JPanel FILTPane=new JPanel();
	FILTPane.setLayout(new GridBagLayout());

	addWidget(FILTPane,new KnobWidget("CutOff",patch,0,127,0,new SIDModel(patch,0x1a),new SIDSender(patch,0x1a)),0,0,2,3,30);
	addWidget(FILTPane,new KnobWidget("Resonance",patch,0,127,0,new SIDModel(patch,0x1b),new SIDSender(patch,0x1b)),2,0,2,3,31);

	gbc.gridx=4;gbc.gridy=0;gbc.gridwidth=2;gbc.gridheight=1; FILTPane.add(new JLabel("Channel"),gbc);
	addWidget(FILTPane,new CheckBoxWidget("O1", patch,new SIDModel(patch,0x18,0),new SIDSender(patch,0x18,0)),4,1,1,1,-13);
	addWidget(FILTPane,new CheckBoxWidget("O2", patch,new SIDModel(patch,0x18,1),new SIDSender(patch,0x18,1)),5,1,1,1,-14);
	addWidget(FILTPane,new CheckBoxWidget("O3", patch,new SIDModel(patch,0x18,2),new SIDSender(patch,0x18,2)),4,2,1,1,-15);
	addWidget(FILTPane,new CheckBoxWidget("Ext",patch,new SIDModel(patch,0x18,3),new SIDSender(patch,0x18,3)),5,2,1,1,-16);


	addWidget(FILTPane,new CheckBoxWidget("LP",patch,new SIDModel(patch,0x19,0),new SIDSender(patch,0x19,0)),0,4,1,1,-17);
	addWidget(FILTPane,new CheckBoxWidget("HP",patch,new SIDModel(patch,0x19,1),new SIDSender(patch,0x19,1)),1,4,1,1,-18);
	addWidget(FILTPane,new CheckBoxWidget("BP",patch,new SIDModel(patch,0x19,2),new SIDSender(patch,0x19,2)),2,4,1,1,-19);
	addWidget(FILTPane,new CheckBoxWidget("3O",patch,new SIDModel(patch,0x19,3),new SIDSender(patch,0x19,3)),3,4,1,1,-20);

	gbc.gridx=10;gbc.gridy=GridBagConstraints.RELATIVE;gbc.gridwidth=5;gbc.gridheight=1;
	FILTPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Filter",TitledBorder.CENTER,TitledBorder.CENTER));  
	scrollPane.add(FILTPane,gbc);


///////////////////////////////////////////////////////////////////////////////
// Modulation Matrix
///////////////////////////////////////////////////////////////////////////////
	JPanel MODMPane=new JPanel();
	MODMPane.setLayout(new GridBagLayout());

	gbc.gridx=0;gbc.gridy=1;gbc.gridwidth=1;gbc.gridheight=1; MODMPane.add(new JLabel("OSC1 Pitch"),gbc);
	gbc.gridx=0;gbc.gridy=2;gbc.gridwidth=1;gbc.gridheight=1; MODMPane.add(new JLabel("OSC2 Pitch"),gbc);
	gbc.gridx=0;gbc.gridy=3;gbc.gridwidth=1;gbc.gridheight=1; MODMPane.add(new JLabel("OSC3 Pitch"),gbc);
	gbc.gridx=0;gbc.gridy=4;gbc.gridwidth=1;gbc.gridheight=1; MODMPane.add(new JLabel("OSC1 PW"),gbc);
	gbc.gridx=0;gbc.gridy=5;gbc.gridwidth=1;gbc.gridheight=1; MODMPane.add(new JLabel("OSC2 PW"),gbc);
	gbc.gridx=0;gbc.gridy=6;gbc.gridwidth=1;gbc.gridheight=1; MODMPane.add(new JLabel("OSC3 PW"),gbc);
	gbc.gridx=0;gbc.gridy=7;gbc.gridwidth=1;gbc.gridheight=1; MODMPane.add(new JLabel("Filter"),gbc);

	gbc.gridx=1;gbc.gridy=0;gbc.gridwidth=1;gbc.gridheight=1; MODMPane.add(new JLabel("E1"),gbc);
	gbc.gridx=2;gbc.gridy=0;gbc.gridwidth=1;gbc.gridheight=1; MODMPane.add(new JLabel("E2"),gbc);
	gbc.gridx=3;gbc.gridy=0;gbc.gridwidth=1;gbc.gridheight=1; MODMPane.add(new JLabel("L1"),gbc);
	gbc.gridx=4;gbc.gridy=0;gbc.gridwidth=1;gbc.gridheight=1; MODMPane.add(new JLabel("L2"),gbc);
	gbc.gridx=5;gbc.gridy=0;gbc.gridwidth=1;gbc.gridheight=1; MODMPane.add(new JLabel("L3"),gbc);
	gbc.gridx=6;gbc.gridy=0;gbc.gridwidth=1;gbc.gridheight=1; MODMPane.add(new JLabel("L4"),gbc);
	gbc.gridx=7;gbc.gridy=0;gbc.gridwidth=1;gbc.gridheight=1; MODMPane.add(new JLabel("L5"),gbc);
	gbc.gridx=8;gbc.gridy=0;gbc.gridwidth=1;gbc.gridheight=1; MODMPane.add(new JLabel("L6"),gbc);

	for(int i=0; i<3; i++) {
	    addWidget(MODMPane,new CheckBoxWidget("",patch,new SIDModel(patch,0x2e+i*16,0),new SIDSender(patch,0x2e+i*16,0)),1,1+i,1,1,-40);
	    addWidget(MODMPane,new CheckBoxWidget("",patch,new SIDModel(patch,0x2e+i*16,1),new SIDSender(patch,0x2e+i*16,1)),2,1+i,1,1,-41);
	    addWidget(MODMPane,new CheckBoxWidget("",patch,new SIDModel(patch,0x2c+i*16,0),new SIDSender(patch,0x2c+i*16,0)),3,1+i,1,1,-42);
	    addWidget(MODMPane,new CheckBoxWidget("",patch,new SIDModel(patch,0x2c+i*16,1),new SIDSender(patch,0x2c+i*16,1)),4,1+i,1,1,-43);
	    addWidget(MODMPane,new CheckBoxWidget("",patch,new SIDModel(patch,0x2c+i*16,2),new SIDSender(patch,0x2c+i*16,2)),5,1+i,1,1,-44);
	    addWidget(MODMPane,new CheckBoxWidget("",patch,new SIDModel(patch,0x2c+i*16,3),new SIDSender(patch,0x2c+i*16,3)),6,1+i,1,1,-45);
	    addWidget(MODMPane,new CheckBoxWidget("",patch,new SIDModel(patch,0x2c+i*16,4),new SIDSender(patch,0x2c+i*16,4)),7,1+i,1,1,-46);
	    addWidget(MODMPane,new CheckBoxWidget("",patch,new SIDModel(patch,0x2c+i*16,5),new SIDSender(patch,0x2c+i*16,5)),8,1+i,1,1,-47);
	}
	for(int i=0; i<3; i++) {

	    addWidget(MODMPane,new CheckBoxWidget("",patch,new SIDModel(patch,0x2e+i*16,4),new SIDSender(patch,0x2e+i*16,4)),1,4+i,1,1,-48);
	    addWidget(MODMPane,new CheckBoxWidget("",patch,new SIDModel(patch,0x2e+i*16,5),new SIDSender(patch,0x2e+i*16,5)),2,4+i,1,1,-49);
	    addWidget(MODMPane,new CheckBoxWidget("",patch,new SIDModel(patch,0x2d+i*16,0),new SIDSender(patch,0x2d+i*16,0)),3,4+i,1,1,-50);
	    addWidget(MODMPane,new CheckBoxWidget("",patch,new SIDModel(patch,0x2d+i*16,1),new SIDSender(patch,0x2d+i*16,1)),4,4+i,1,1,-51);
	    addWidget(MODMPane,new CheckBoxWidget("",patch,new SIDModel(patch,0x2d+i*16,2),new SIDSender(patch,0x2d+i*16,2)),5,4+i,1,1,-52);
	    addWidget(MODMPane,new CheckBoxWidget("",patch,new SIDModel(patch,0x2d+i*16,3),new SIDSender(patch,0x2d+i*16,3)),6,4+i,1,1,-53);
	    addWidget(MODMPane,new CheckBoxWidget("",patch,new SIDModel(patch,0x2d+i*16,4),new SIDSender(patch,0x2d+i*16,4)),7,4+i,1,1,-54);
	    addWidget(MODMPane,new CheckBoxWidget("",patch,new SIDModel(patch,0x2d+i*16,5),new SIDSender(patch,0x2d+i*16,5)),8,4+i,1,1,-55);
	}
	addWidget(MODMPane,new CheckBoxWidget("",patch,new SIDModel(patch,0x1d,0),new SIDSender(patch,0x1d,0)),1,7,1,1,-56);
	addWidget(MODMPane,new CheckBoxWidget("",patch,new SIDModel(patch,0x1d,1),new SIDSender(patch,0x1d,1)),2,7,1,1,-57);
	addWidget(MODMPane,new CheckBoxWidget("",patch,new SIDModel(patch,0x1c,0),new SIDSender(patch,0x1c,0)),3,7,1,1,-58);
	addWidget(MODMPane,new CheckBoxWidget("",patch,new SIDModel(patch,0x1c,1),new SIDSender(patch,0x1c,1)),4,7,1,1,-59);
	addWidget(MODMPane,new CheckBoxWidget("",patch,new SIDModel(patch,0x1c,2),new SIDSender(patch,0x1c,2)),5,7,1,1,-60);
	addWidget(MODMPane,new CheckBoxWidget("",patch,new SIDModel(patch,0x1c,3),new SIDSender(patch,0x1c,3)),6,7,1,1,-61);
	addWidget(MODMPane,new CheckBoxWidget("",patch,new SIDModel(patch,0x1c,4),new SIDSender(patch,0x1c,4)),7,7,1,1,-62);
	addWidget(MODMPane,new CheckBoxWidget("",patch,new SIDModel(patch,0x1c,5),new SIDSender(patch,0x1c,5)),8,7,1,1,-63);

	gbc.gridx=10;gbc.gridy=GridBagConstraints.RELATIVE;gbc.gridwidth=5;gbc.gridheight=3;
	MODMPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Modulation Matrix",TitledBorder.CENTER,TitledBorder.CENTER));  
	scrollPane.add(MODMPane,gbc);


///////////////////////////////////////////////////////////////////////////////
// MIDI Sync
///////////////////////////////////////////////////////////////////////////////
	JPanel CLKPane=new JPanel();
	CLKPane.setLayout(new GridBagLayout());
	gbc.gridx=0;gbc.gridy=0;gbc.gridwidth=1;gbc.gridheight=1; CLKPane.add(new JLabel("Targets   "),gbc);
	addWidget(CLKPane,new CheckBoxWidget("Wavetable Seq./Arp.", patch,new SIDModel(patch,0x13,0),new SIDSender(patch,0x13,0)),1,1,1,1,-10);
	addWidget(CLKPane,new CheckBoxWidget("LFOs", patch,new SIDModel(patch,0x13,1),new SIDSender(patch,0x13,1)),1,2,1,1,-11);
	addWidget(CLKPane,new CheckBoxWidget("ENVs", patch,new SIDModel(patch,0x13,2),new SIDSender(patch,0x13,2)),1,3,1,1,-12);

	gbc.gridx=10;gbc.gridy=GridBagConstraints.RELATIVE;gbc.gridwidth=5;gbc.gridheight=1;
	CLKPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"MIDI Clock Synchronization",TitledBorder.CENTER,TitledBorder.CENTER));  
	scrollPane.add(CLKPane,gbc);


	pack();
    }


    public void sendDumpChanges()
    {
	MIDIboxSIDSlowSender SlowSender = new MIDIboxSIDSlowSender();
	byte[] cooked_dump = dataModel.getCookedDump();

	for(int i=0; i<4*32; ++i) {
	    byte stored_value = ((Patch)p).sysex[8+0x80+i];
	    if( stored_value != cooked_dump[i] )
	    {
		System.out.println("Wavetable Field changed: " + i);
	        ((Patch)p).sysex[8+0x80+i] = cooked_dump[i];
		SlowSender.sendParameter((Driver) ((Patch)p).getDriver(), 0x80+i, cooked_dump[i], 10);
	    }
	}
    }

    void viewPressed() 
    {
	if( dataModel.getHexView() ) {
	    dataModel.setHexView(false);
	    switchViewButton.setText("Switch to Hex");
	} else {
	    dataModel.setHexView(true);
	    switchViewButton.setText("Switch to Dec");
	}
	table.repaint();
    }

}


class SIDSender extends SysexSender
{
    Patch patch;
    int parameter;
    int flag;
    int bitmask;
    int []mapped_values;
    byte []b = new byte [11];

    private void SIDSender_Hlp(Patch _patch, int _parameter)
    {
	patch = _patch;
	parameter = _parameter;
	b[0] = (byte)0xf0;
	b[1] = (byte)0x00;
	b[2] = (byte)0x00;
	b[3] = (byte)0x7e;
	b[4] = (byte)0x46;
	//b[5] = (byte)(getDeviceID()-1);
	b[6] = (byte)0x06;
	b[7] = (byte)(parameter > 0x80 ? 0x01 : 0x00);
	b[8] = (byte)(parameter & 0x7f);
	//b[9] = (byte)(value&0x7f);
	b[10] = (byte)0xf7;
    }

    public SIDSender(Patch _patch, int parameter)
    {
	flag = -1;
	SIDSender_Hlp(_patch, parameter);
    }

    public SIDSender(Patch _patch, int parameter, int _flag)
    {
	flag    = _flag;
	bitmask = (1 << _flag);
	mapped_values = new int[]{}; // (empty)
	SIDSender_Hlp(_patch, parameter);
    }

    public SIDSender(Patch _patch, int parameter, int _flag, int _bitmask)
    {
	flag    = _flag;
	bitmask = _bitmask << flag;
	mapped_values  = new int[]{}; // (empty)
	SIDSender_Hlp(_patch, parameter);
    }

    public SIDSender(Patch _patch, int parameter, int _flag, int _bitmask, int []_mapped_values)
    {
	flag    = _flag;
	bitmask = _bitmask << flag;
	mapped_values  = _mapped_values;
	SIDSender_Hlp(_patch, parameter);
    }

    public byte [] generate (int value)
    {
	b[5] = (byte)(channel-1);

	if( flag == -1 ) {
	    b[9] = (byte)value;
	} else {
	    b[9] = (byte)(patch.sysex[8+parameter] & (~bitmask));

	    if( mapped_values.length > 0 )
		value = mapped_values[value];

	    b[9] |= (byte)value << flag;
	}

	return b;
    }
}


class SIDModel extends ParamModel
{
    int flag;
    int bitmask;
    int []mapped_values;

    public SIDModel(Patch _patch, int _offset)
    {
        super(_patch, _offset + 8);
	flag = -1;
    }

    public SIDModel(Patch _patch, int _offset, int _flag)
    {
        super(_patch, _offset + 8);
	flag    = _flag;
	bitmask = (1 << flag);
	mapped_values = new int[]{}; // (empty)
    }

    public SIDModel(Patch _patch, int _offset, int _flag, int _bitmask)
    {
	super(_patch, _offset + 8);
	flag    = _flag;
	bitmask = _bitmask << flag;
	mapped_values = new int[]{}; // (empty)
    }

    public SIDModel(Patch _patch, int _offset, int _flag, int _bitmask, int []_mapped_values)
    {
        super(_patch, _offset + 8);
	flag    = _flag;
	bitmask = _bitmask << flag;
	mapped_values = _mapped_values;
    }

    public void set(int i)
    {
	if( flag == -1 ) {
	    patch.sysex[ofs] = (byte)i;
	} else {
	    patch.sysex[ofs]=(byte)(patch.sysex[ofs]&(~bitmask));
	    if( mapped_values.length > 0 )
		patch.sysex[ofs] |= (byte)mapped_values[i];
	    else
		patch.sysex[ofs] |= (byte)i << flag;
	}
    }

    public int get()
    {
	if( flag == -1 )
	    return patch.sysex[ofs];
	else {
	    if( mapped_values.length > 0 ) {
		int value;

		value = (patch.sysex[ofs] & bitmask) >> flag;
		for(int i=0; i<mapped_values.length; ++i)
		    if( mapped_values[i] == value )
			return i;
		return 0;
	    } else
		return (patch.sysex[ofs] & bitmask) >> flag;
	}
    }

}


