/*
 * Patch Editor for MIDIbox FM
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
import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableColumn;

import core.Patch;
import core.CheckBoxWidget;
import core.ComboBoxWidget;
import core.Driver;
import core.EnvelopeWidget;
import core.PatchEditorFrame;
import core.PatchNameWidget;
import core.ScrollBarWidget;
import core.KnobWidget;

class MIDIboxFMPatchEditor extends PatchEditorFrame
{
    final String[] parName = new String[] {
	"  0 | <reserved>",
	"  1 | Modulation Wheel",
	"  2 | Transpose",
	"  3 | Unisono",
	"  4 | <reserved>",
	"  5 | <reserved>",
	"  6 | <reserved>",
	"  7 | Volume",
	"  8 | <reserved>",
	"  9 | <reserved>",
	" 10 | Audio Channels",
	" 11 | Audio Chn. OP12",
	" 12 | Audio Chn. OP34",
	" 13 | <reserved>",
	" 14 | <reserved>",
	" 15 | <reserved>",
	" 16 | OP1 Flags",
	" 17 | OP2 Flags",
	" 18 | OP3 Flags",
	" 19 | OP4 Flags",
	" 20 | OP1 Multiplier",
	" 21 | OP2 Multiplier",
	" 22 | OP3 Multiplier",
	" 23 | OP4 Multiplier",
	" 24 | OP1 KSL",
	" 25 | OP2 KSL",
	" 26 | OP3 KSL",
	" 27 | OP4 KSL",
	" 28 | OP1 Volume",
	" 29 | OP2 Volume",
	" 30 | OP3 Volume",
	" 31 | OP4 Volume",
	" 32 | OP1 Attack Rate",
	" 33 | OP2 Attack Rate",
	" 34 | OP3 Attack Rate",
	" 35 | OP4 Attack Rate",
	" 36 | OP1 Decay Rate",
	" 37 | OP2 Decay Rate",
	" 38 | OP3 Decay Rate",
	" 39 | OP4 Decay Rate",
	" 40 | OP1 Sustain Level",
	" 41 | OP2 Sustain Level",
	" 42 | OP3 Sustain Level",
	" 43 | OP4 Sustain Level",
	" 44 | OP1 Release Rate",
	" 45 | OP2 Release Rate",
	" 46 | OP3 Release Rate",
	" 47 | OP4 Release Rate",
	" 48 | OP1 Waveform",
	" 49 | OP2 Waveform",
	" 50 | OP3 Waveform",
	" 51 | OP4 Waveform",
	" 52 | <reserved>",
	" 53 | <reserved>",
	" 54 | <reserved>",
	" 55 | <reserved>",
	" 56 | OP1 Feedback",
	" 57 | <reserved>",
	" 58 | <reserved>",
	" 59 | <reserved>",
	" 60 | OP Connections",
	" 61 | <reserved>",
	" 62 | <reserved>",
	" 63 | <reserved>",
	" 64 | LFO1 Mode",
	" 65 | LFO1 Phase",
	" 66 | LFO1 Rate",
	" 67 | LFO1 Pitch Depth",
	" 68 | LFO1 OP1 Volume Depth",
	" 69 | LFO1 OP2 Volume Depth",
	" 70 | LFO1 OP3 Volume Depth",
	" 71 | LFO1 OP4 Volume Depth",
	" 72 | LFO1 -> LFO2 Depth",
	" 73 | LFO1 AOUT Depth",
	" 74 | <reserved>",
	" 75 | <reserved>",
	" 76 | Velocity Assign",
	" 77 | Velocity Init Value",
	" 78 | Velocity Depth",
	" 79 | <reserved>",
	" 80 | LFO2 Mode",
	" 81 | LFO2 Phase",
	" 82 | LFO2 Rate",
	" 83 | LFO2 Pitch Depth",
	" 84 | LFO2 OP1 Volume Depth",
	" 85 | LFO2 OP2 Volume Depth",
	" 86 | LFO2 OP3 Volume Depth",
	" 87 | LFO2 OP4 Volume Depth",
	" 88 | LFO2 -> LFO1 Depth",
	" 89 | LFO2 AOUT Depth",
	" 90 | <reserved>",
	" 91 | <reserved>",
	" 92 | Aftertouch Assign",
	" 93 | Aftertouch Init Value",
	" 94 | Aftertouch Depth",
	" 95 | <reserved>",
	" 96 | EG5 Mode",
	" 97 | EG5 Attack",
	" 98 | EG5 Attack Level",
	" 99 | EG5 Decay1",
	"100 | EG5 Decay Level",
	"101 | EG5 Decay2",
	"102 | EG5 Sustain",
	"103 | EG5 Release",
	"104 | EG5 Curve",
	"105 | EG5 Pitch Depth",
	"106 | EG5 OP1 Volume Depth",
	"107 | EG5 OP2 Volume Depth",
	"108 | EG5 OP3 Volume Depth",
	"109 | EG5 OP4 Volume Depth",
	"100 | EG5 -> LFO1 Depth",
	"111 | EG5 AOUT Depth",
	"112 | CTRL1_L",
	"113 | CTRL1_H",
	"114 | CTRL2_L",
	"115 | CTRL2_H",
	"116 | Finetune",
	"117 | Pitchrange",
	"118 | Portamento Rate",
	"119 | Wavetable Rate",
	"120 | WT Parameter #1",
	"121 | WT Parameter #2",
	"122 | WT Parameter #3",
	"123 | <reserved>",
	"124 | <reserved>",
	"125 | Modwheel Assign",
	"126 | Modwheel Init Value",
	"127 | Modwheel Depth",
	"128 | <reserved>",
    };

    MIDIboxFMWavetableModel dataModel = new MIDIboxFMWavetableModel();
    JTable table = new JTable(dataModel);
    JButton switchViewButton = new JButton(dataModel.getHexView() ? "Switch to Dec" : "Switch to Hex");
    JButton updateWavetableButton = new JButton("Update Wavetable");
    ImageIcon wsIcon[]=new ImageIcon[8];
    ImageIcon algIcon[]=new ImageIcon[4];

    public MIDIboxFMPatchEditor(Patch patch)
    {
	super ("MIDIbox FM Patch Editor",patch);
	gbc.weightx=0; gbc.weighty=0;

        wsIcon[0]=new ImageIcon("synthdrivers/MIDIboxFM/images/ws_0.gif");
        wsIcon[1]=new ImageIcon("synthdrivers/MIDIboxFM/images/ws_1.gif");
        wsIcon[2]=new ImageIcon("synthdrivers/MIDIboxFM/images/ws_2.gif");
        wsIcon[3]=new ImageIcon("synthdrivers/MIDIboxFM/images/ws_3.gif");
        wsIcon[4]=new ImageIcon("synthdrivers/MIDIboxFM/images/ws_4.gif");
        wsIcon[5]=new ImageIcon("synthdrivers/MIDIboxFM/images/ws_5.gif");
        wsIcon[6]=new ImageIcon("synthdrivers/MIDIboxFM/images/ws_6.gif");
        wsIcon[7]=new ImageIcon("synthdrivers/MIDIboxFM/images/ws_7.gif");

        algIcon[0]=new ImageIcon("synthdrivers/MIDIboxFM/images/alg_0.gif");
        algIcon[1]=new ImageIcon("synthdrivers/MIDIboxFM/images/alg_1.gif");
        algIcon[2]=new ImageIcon("synthdrivers/MIDIboxFM/images/alg_2.gif");
        algIcon[3]=new ImageIcon("synthdrivers/MIDIboxFM/images/alg_3.gif");


///////////////////////////////////////////////////////////////////////////////
	JPanel LeftPanel=new JPanel();
	LeftPanel.setLayout(new GridBagLayout());
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  General
///////////////////////////////////////////////////////////////////////////////
	JTabbedPane generalPane=new JTabbedPane();
	for(int i=0; i<1; ++i)
	{
	    JPanel panel = new JPanel();
	    panel.setLayout(new GridBagLayout());
	    generalPane.addTab("General",panel);gbc.weightx=0;

	    addWidget(panel,new PatchNameWidget("                   Name",patch),0,0,12,1,0);

	    // horizontal location, the vertical location, the horizontal size, and the vertical size). The last number is the fader number

	    addWidget(panel,new ComboBoxWidget("Play Mode", patch,new MIDIboxFMModel(patch,0x70,0,0x3,new int[]{0x0,0x01,0x2}),new MIDIboxFMSender(patch,0x00,0x70,0,0x3,new int[]{0x0,0x01,0x2}),new String []{"MONO", "LEGATO", "POLY"}),0,1,12,1,-1);
	    addWidget(panel,new ComboBoxWidget("Portamento Mode",patch,new MIDIboxFMModel(patch,0x70,2),new MIDIboxFMSender(patch,0x00,0x70,2),new String []{"Full Time", "Fingered (SusKey)"}),0,2,12,1,-2);
	    addWidget(panel,new KnobWidget("  Finetune  ",patch,0,127,-64,new MIDIboxFMModel(patch,0x74),new MIDIboxFMSender(patch,0x00,0x74)),0,3,3,3,1);
	    addWidget(panel,new KnobWidget(" Pitchrange ",patch,0,127,0,new MIDIboxFMModel(patch,0x75),new MIDIboxFMSender(patch,0x00,0x75)),3,3,3,3,2);
	    addWidget(panel,new KnobWidget(" Portamento ",patch,0,127,0,new MIDIboxFMModel(patch,0x76),new MIDIboxFMSender(patch,0x00,0x76)),6,3,3,3,3);
	}
	gbc.gridx=0;gbc.gridy=0;gbc.gridwidth=5;gbc.gridheight=5;
	LeftPanel.add(generalPane,gbc);

///////////////////////////////////////////////////////////////////////////////
// MIDI Sync
///////////////////////////////////////////////////////////////////////////////
	JTabbedPane syncPane=new JTabbedPane();
	for(int i=0; i<1; ++i)
	{
	    JPanel panel = new JPanel();
	    panel.setLayout(new GridBagLayout());
	    syncPane.addTab("MIDI Clock Synchronization",panel);gbc.weightx=0;

	    addWidget(panel,new CheckBoxWidget("WT Sequencer", patch,new MIDIboxFMModel(patch,0x72,0),new MIDIboxFMSender(patch,0x00,0x72,0)),0,1,1,1,-3);
	    addWidget(panel,new CheckBoxWidget("LFOs", patch,new MIDIboxFMModel(patch,0x72,1),new MIDIboxFMSender(patch,0x00,0x72,1)),3,1,1,1,-4);
	    addWidget(panel,new CheckBoxWidget("EG5", patch,new MIDIboxFMModel(patch,0x72,2),new MIDIboxFMSender(patch,0x00,0x72,2)),6,1,1,1,-5);
	}
	gbc.gridx=0;gbc.gridy=GridBagConstraints.RELATIVE;gbc.gridwidth=5;gbc.gridheight=2;
	LeftPanel.add(syncPane,gbc);


///////////////////////////////////////////////////////////////////////////////
// Wavetables
///////////////////////////////////////////////////////////////////////////////
	JTabbedPane WAVTPane=new JTabbedPane();
	for(int i=0; i<1; ++i)
	{
	    JPanel panel = new JPanel();
	    panel.setLayout(new GridBagLayout());
	    WAVTPane.addTab("Wavetable Sequencer",panel);gbc.weightx=0;

	    addWidget(panel,new ComboBoxWidget("Parameter #1",patch,new MIDIboxFMModel(patch,0x78),new MIDIboxFMSender(patch,0x00,0x78),parName),0,0,3,1,4);
	    addWidget(panel,new ComboBoxWidget("Parameter #2",patch,new MIDIboxFMModel(patch,0x79),new MIDIboxFMSender(patch,0x00,0x79),parName),0,1,3,1,5);
	    addWidget(panel,new ComboBoxWidget("Parameter #3",patch,new MIDIboxFMModel(patch,0x7a),new MIDIboxFMSender(patch,0x00,0x7a),parName),0,2,3,1,6);

	    addWidget(panel,new KnobWidget("Rate                  ",patch,0,127,0,new MIDIboxFMModel(patch,0x77),new MIDIboxFMSender(patch,0x00,0x77)),0,3,1,3,7);
      	    gbc.gridx=1;gbc.gridy=3;gbc.gridwidth=1;gbc.gridheight=1; panel.add(new JLabel(" "),gbc);
	    addWidget(panel,new ComboBoxWidget("Synchronization", patch,new MIDIboxFMModel(patch,0x73,0,0x3,new int[]{0x0,0x01,0x2}),new MIDIboxFMSender(patch,0x00,0x73,0,0x3,new int[]{0x0,0x01,0x2}),new String []{"Note", "Note Step", "Freerunning"}),1,4,2,1,-6);

       
	    table.setPreferredScrollableViewportSize(new Dimension(100, 100));
	    JScrollPane scrollpane = new JScrollPane(table);
	    gbc.gridx=0;gbc.gridy=7;gbc.gridwidth=3;gbc.gridheight=20;
	    panel.add(scrollpane, gbc);
        
	    TableColumn column = null;

	    column = table.getColumnModel().getColumn(0);
	    column = table.getColumnModel().getColumn(1);
	    JComboBox comboBox = new JComboBox();
	    for(int j=0; j<dataModel.modeNames.length; ++j) {
		comboBox.addItem(dataModel.modeNames[j]);
	    }
	    column.setCellEditor(new DefaultCellEditor(comboBox));
	    column = table.getColumnModel().getColumn(2);
	    column = table.getColumnModel().getColumn(3);
	    column = table.getColumnModel().getColumn(4);
	    
	    byte[] cooked_dump = dataModel.getCookedDump();
	    for(int j=0; j<cooked_dump.length; ++j)
		cooked_dump[j] = ((Patch)p).sysex[10+0x80+j];
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
	    panel.add(switchViewButton, gbc);

	    // JButton updateWavetableButton = new JButton("Update Wavetable");	
	    // (defined as global button)
	    updateWavetableButton.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e)
		    {
			sendDumpChanges();
		    }
		});
	    gbc.gridx=1;gbc.gridy=GridBagConstraints.RELATIVE;gbc.gridwidth=1;gbc.gridheight=1;
	    panel.add(updateWavetableButton, gbc);
	}
	gbc.gridx=0;gbc.gridy=GridBagConstraints.RELATIVE;gbc.gridwidth=5;gbc.gridheight=10;
	LeftPanel.add(WAVTPane,gbc);


///////////////////////////////////////////////////////////////////////////////
	gbc.gridx=0;gbc.gridy=0;gbc.gridwidth=5;gbc.gridheight=20;
	scrollPane.add(LeftPanel,gbc);
///////////////////////////////////////////////////////////////////////////////



///////////////////////////////////////////////////////////////////////////////
	JPanel MidPanel=new JPanel();
	MidPanel.setLayout(new GridBagLayout());
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
// Pot Panel
///////////////////////////////////////////////////////////////////////////////
	JTabbedPane knobPane=new JTabbedPane();
	for(int i=0; i<1; ++i)
	{
	    JPanel panel = new JPanel();
	    panel.setLayout(new GridBagLayout());
	    knobPane.addTab("All Operators",panel);gbc.weightx=0;

	    gbc.gridx= 3+1+0*5;gbc.gridy=0;gbc.gridwidth=3;gbc.gridheight=1; panel.add(new JLabel("    OP1"),gbc);
	    gbc.gridx= 3+1+1*5;gbc.gridy=0;gbc.gridwidth=3;gbc.gridheight=1; panel.add(new JLabel("    OP2"),gbc);
	    gbc.gridx= 3+1+2*5;gbc.gridy=0;gbc.gridwidth=3;gbc.gridheight=1; panel.add(new JLabel("    OP3"),gbc);
	    gbc.gridx= 3+1+3*5;gbc.gridy=0;gbc.gridwidth=3;gbc.gridheight=1; panel.add(new JLabel("    OP4"),gbc);

	    gbc.gridx=0;gbc.gridy=1;gbc.gridwidth=3;gbc.gridheight=1; panel.add(new JLabel(" "),gbc);
	    gbc.gridx=0;gbc.gridy=2;gbc.gridwidth=3;gbc.gridheight=1; panel.add(new JLabel("Volume"),gbc);
	    for(int j=0; j<4; ++j)
	    {
		gbc.gridx=3+0+j*5;gbc.gridy=1;gbc.gridwidth=1;gbc.gridheight=1; panel.add(new JLabel("        "),gbc);
		addWidget(panel,new KnobWidget("",patch,0,0x3f,0x00,new MIDIboxFMModel(patch,0x1c+j),new MIDIboxFMSender(patch,0x00,0x1c+j)),3+1+5*j, 1,3,3,8+j);
	    }

	    gbc.gridx=0;gbc.gridy=4;gbc.gridwidth=3;gbc.gridheight=1; panel.add(new JLabel(" "),gbc);
	    gbc.gridx=0;gbc.gridy=5;gbc.gridwidth=3;gbc.gridheight=1; panel.add(new JLabel("Attack"),gbc);
	    for(int j=0; j<4; ++j)
	    {
		gbc.gridx=3+0+j*5;gbc.gridy=4;gbc.gridwidth=1;gbc.gridheight=1; panel.add(new JLabel("        "),gbc);
		addWidget(panel,new KnobWidget("",patch,0,0x0f,0x00,new MIDIboxFMModel(patch,0x20+j),new MIDIboxFMSender(patch,0x00,0x20+j)),3+1+5*j, 4,3,3,12+j);
	    }

	    gbc.gridx=0;gbc.gridy=7;gbc.gridwidth=3;gbc.gridheight=1; panel.add(new JLabel(" "),gbc);
	    gbc.gridx=0;gbc.gridy=8;gbc.gridwidth=3;gbc.gridheight=1; panel.add(new JLabel("Decay"),gbc);
	    for(int j=0; j<4; ++j)
	    {
		gbc.gridx=3+0+j*5;gbc.gridy=7;gbc.gridwidth=1;gbc.gridheight=1; panel.add(new JLabel("        "),gbc);
		addWidget(panel,new KnobWidget("",patch,0,0x0f,0x00,new MIDIboxFMModel(patch,0x24+j),new MIDIboxFMSender(patch,0x00,0x24+j)),3+1+5*j, 7,3,3,16+j);
	    }

	    gbc.gridx=0;gbc.gridy=10;gbc.gridwidth=3;gbc.gridheight=1; panel.add(new JLabel(" "),gbc);
	    gbc.gridx=0;gbc.gridy=11;gbc.gridwidth=3;gbc.gridheight=1; panel.add(new JLabel("Sustain"),gbc);
	    for(int j=0; j<4; ++j)
	    {
		gbc.gridx=3+0+j*5;gbc.gridy=10;gbc.gridwidth=1;gbc.gridheight=1; panel.add(new JLabel("        "),gbc);
		addWidget(panel,new KnobWidget("",patch,0,0x0f,0x00,new MIDIboxFMModel(patch,0x28+j),new MIDIboxFMSender(patch,0x00,0x28+j)),3+1+5*j, 10,3,3,20+j);
	    }

	    gbc.gridx=0;gbc.gridy=13;gbc.gridwidth=3;gbc.gridheight=1; panel.add(new JLabel(" "),gbc);
	    gbc.gridx=0;gbc.gridy=14;gbc.gridwidth=3;gbc.gridheight=1; panel.add(new JLabel("Release"),gbc);
	    for(int j=0; j<4; ++j)
	    {
		gbc.gridx=3+0+j*5;gbc.gridy=13;gbc.gridwidth=1;gbc.gridheight=1; panel.add(new JLabel("        "),gbc);
		addWidget(panel,new KnobWidget("",patch,0,0x0f,0x00,new MIDIboxFMModel(patch,0x2c+j),new MIDIboxFMSender(patch,0x00,0x2c+j)),3+1+5*j, 13,3,3,24+j);
	    }

	    gbc.gridx=0;gbc.gridy=16;gbc.gridwidth=3;gbc.gridheight=1; panel.add(new JLabel(" "),gbc);
	    gbc.gridx=0;gbc.gridy=17;gbc.gridwidth=3;gbc.gridheight=1; panel.add(new JLabel("Key Scaling"),gbc);
	    for(int j=0; j<4; ++j)
	    {
		addWidget(panel,new ComboBoxWidget("", patch,new MIDIboxFMModel(patch,0x18+j,0,0x3,new int[]{0,1,2,3}),new MIDIboxFMSender(patch,0x00,0x18+j,0,0x3,new int[]{0,1,2,3}),new String []{"0 dB", "1.5 dB", "3 dB", "6 dB"}),3+5*j, 16,5,2,-8-j);
	    }

	    gbc.gridx=0;gbc.gridy=18;gbc.gridwidth=3;gbc.gridheight=1; panel.add(new JLabel(" "),gbc);
	    gbc.gridx=0;gbc.gridy=19;gbc.gridwidth=3;gbc.gridheight=1; panel.add(new JLabel("Waveform"),gbc);
	    for(int j=0; j<4; ++j)
	    {
		final KnobWidget wsBar=new KnobWidget("",patch,0,7,0,new MIDIboxFMModel(patch,0x30+j),new MIDIboxFMSender(patch,0x00,0x30+j));
		addWidget(panel,wsBar,3+1+5*j,18,3,3,28+j);
		final JLabel wsImg=new JLabel(wsIcon[wsBar.getValue()&7]);
		gbc.gridx=3+5*j;gbc.gridy=21;gbc.gridwidth=5;gbc.gridheight=3;
		panel.add(wsImg,gbc);
		wsBar.addEventListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
			    wsImg.setIcon(wsIcon[wsBar.getValue()&7]);
			}});
	    }

	    gbc.gridx=0;gbc.gridy=24;gbc.gridwidth=3;gbc.gridheight=1; panel.add(new JLabel(" "),gbc);
	    gbc.gridx=0;gbc.gridy=25;gbc.gridwidth=3;gbc.gridheight=1; panel.add(new JLabel("Multiplier"),gbc);
	    for(int j=0; j<4; ++j)
	    {
		gbc.gridx=3+0+j*5;gbc.gridy=24;gbc.gridwidth=1;gbc.gridheight=1; panel.add(new JLabel("        "),gbc);
		addWidget(panel,new KnobWidget("",patch,0,0x07,0x00,new MIDIboxFMModel(patch,0x14+j),new MIDIboxFMSender(patch,0x00,0x14+j)),3+1+5*j,24,3,3,32+j);
	    }

	    gbc.gridx=0;gbc.gridy=27;gbc.gridwidth=3;gbc.gridheight=1; panel.add(new JLabel("Sustain"),gbc);
	    for(int j=0; j<4; ++j)
	    {
		gbc.gridx=3+0+j*5;gbc.gridy=27;gbc.gridwidth=1;gbc.gridheight=1; panel.add(new JLabel("        "),gbc);
		gbc.gridx=3+1+j*5;gbc.gridy=27;gbc.gridwidth=1;gbc.gridheight=1; panel.add(new JLabel("     "),gbc);
		addWidget(panel,new CheckBoxWidget("",  patch,new MIDIboxFMModel(patch,0x10+j,1),new MIDIboxFMSender(patch,0x00,0x10+j,1)),3+2+5*j,27,3,1,-12-j);
	    }

	    gbc.gridx=0;gbc.gridy=28;gbc.gridwidth=3;gbc.gridheight=1; panel.add(new JLabel("Tremolo"),gbc);
	    for(int j=0; j<4; ++j)
	    {
		gbc.gridx=3+0+j*5;gbc.gridy=28;gbc.gridwidth=1;gbc.gridheight=1; panel.add(new JLabel("        "),gbc);
		gbc.gridx=3+1+j*5;gbc.gridy=28;gbc.gridwidth=1;gbc.gridheight=1; panel.add(new JLabel("     "),gbc);
		addWidget(panel,new CheckBoxWidget("",  patch,new MIDIboxFMModel(patch,0x10+j,3),new MIDIboxFMSender(patch,0x00,0x10+j,3)),3+2+5*j,28,3,1,-16-j);
	    }

	    gbc.gridx=0;gbc.gridy=29;gbc.gridwidth=3;gbc.gridheight=1; panel.add(new JLabel("Vibrato"),gbc);
	    for(int j=0; j<4; ++j)
	    {
		gbc.gridx=3+0+j*5;gbc.gridy=29;gbc.gridwidth=1;gbc.gridheight=1; panel.add(new JLabel("        "),gbc);
		gbc.gridx=3+1+j*5;gbc.gridy=29;gbc.gridwidth=1;gbc.gridheight=1; panel.add(new JLabel("     "),gbc);
		addWidget(panel,new CheckBoxWidget("",  patch,new MIDIboxFMModel(patch,0x10+j,2),new MIDIboxFMSender(patch,0x00,0x10+j,2)),3+2+5*j,29,3,1,-20-j);
	    }

	    gbc.gridx=0;gbc.gridy=30;gbc.gridwidth=3;gbc.gridheight=1; panel.add(new JLabel(" "),gbc);
	    gbc.gridx=0;gbc.gridy=31;gbc.gridwidth=3;gbc.gridheight=1; panel.add(new JLabel("LFO1->Volume"),gbc);
	    for(int j=0; j<4; ++j)
	    {
		gbc.gridx=3+0+j*5;gbc.gridy=30;gbc.gridwidth=1;gbc.gridheight=1; panel.add(new JLabel("        "),gbc);
		addWidget(panel,new KnobWidget("",patch,0,0x7f,-0x40,new MIDIboxFMModel(patch,0x44+j),new MIDIboxFMSender(patch,0x00,0x44+j)),3+1+5*j,30,3,3,36+j);
	    }

	    gbc.gridx=0;gbc.gridy=33;gbc.gridwidth=3;gbc.gridheight=1; panel.add(new JLabel(" "),gbc);
	    gbc.gridx=0;gbc.gridy=34;gbc.gridwidth=3;gbc.gridheight=1; panel.add(new JLabel("LFO2->Volume"),gbc);
	    for(int j=0; j<4; ++j)
	    {
		gbc.gridx=3+0+j*5;gbc.gridy=33;gbc.gridwidth=1;gbc.gridheight=1; panel.add(new JLabel("        "),gbc);
		addWidget(panel,new KnobWidget("",patch,0,0x7f,-0x40,new MIDIboxFMModel(patch,0x54+j),new MIDIboxFMSender(patch,0x00,0x54+j)),3+1+5*j,33,3,3,40+j);
	    }

	    gbc.gridx=0;gbc.gridy=36;gbc.gridwidth=3;gbc.gridheight=1; panel.add(new JLabel(" "),gbc);
	    gbc.gridx=0;gbc.gridy=37;gbc.gridwidth=3;gbc.gridheight=1; panel.add(new JLabel("EG5->Volume"),gbc);
	    for(int j=0; j<4; ++j)
	    {
		gbc.gridx=3+0+j*5;gbc.gridy=36;gbc.gridwidth=1;gbc.gridheight=1; panel.add(new JLabel("        "),gbc);
		addWidget(panel,new KnobWidget("",patch,0,0x7f,-0x40,new MIDIboxFMModel(patch,0x6a+j),new MIDIboxFMSender(patch,0x00,0x6a+j)),3+1+5*j,36,3,3,44+j);
	    }
	}
	gbc.gridx=0;gbc.gridy=0;gbc.gridwidth=5;gbc.gridheight=15;
	MidPanel.add(knobPane,gbc);




///////////////////////////////////////////////////////////////////////////////
	gbc.gridx=5;gbc.gridy=0;gbc.gridwidth=5;gbc.gridheight=20;
	scrollPane.add(MidPanel,gbc);
///////////////////////////////////////////////////////////////////////////////



///////////////////////////////////////////////////////////////////////////////
	JPanel RightPanel=new JPanel();
	RightPanel.setLayout(new GridBagLayout());
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
// Algorithm
///////////////////////////////////////////////////////////////////////////////
	JTabbedPane algPane=new JTabbedPane();
	for(int i=0; i<1; ++i)
	{
	    JPanel panel = new JPanel();
	    panel.setLayout(new GridBagLayout());
	    algPane.addTab("Connections",panel);gbc.weightx=0;

	    final KnobWidget algBar=new KnobWidget(" Algorithm ",patch,0,3,0,new MIDIboxFMModel(patch,0x3c),new MIDIboxFMSender(patch,0x00,0x3c));
	    addWidget(panel,algBar,3,0,3,3,-24);
	    final JLabel algImg=new JLabel(algIcon[algBar.getValue()&3]);
	    gbc.gridx=0;gbc.gridy=0;gbc.gridwidth=3;gbc.gridheight=4;
	    panel.add(algImg,gbc);
	    algBar.addEventListener(new ChangeListener() {
		    public void stateChanged(ChangeEvent e) {
			algImg.setIcon(algIcon[algBar.getValue()&3]);
		    }});

	    gbc.gridx=6;gbc.gridy=0;gbc.gridwidth=1;gbc.gridheight=1; panel.add(new JLabel("         "),gbc);

	    addWidget(panel,new KnobWidget("  Feedback  ",patch,0,7,0,new MIDIboxFMModel(patch,0x38),new MIDIboxFMSender(patch,0x00,0x38)),7,0,3,3,48);
	}
	gbc.gridx=0;gbc.gridy=0;gbc.gridwidth=5;gbc.gridheight=4;
	RightPanel.add(algPane,gbc);


///////////////////////////////////////////////////////////////////////////////
// EG5
///////////////////////////////////////////////////////////////////////////////
	JTabbedPane EGPane=new JTabbedPane();   
	for(int i=0; i<1; i++) {     
	    JPanel panel = new JPanel();
	    panel.setLayout(new GridBagLayout());
	    EGPane.addTab("EG5",panel);gbc.weightx=0;

	    addWidget(panel,new EnvelopeWidget("SW Envelope",patch,new EnvelopeWidget.Node[] {
		new EnvelopeWidget.Node(0,0,null,0,0,null,0,false,null,null,null,null),     
		new EnvelopeWidget.Node(0,127,new MIDIboxFMModel(patch,0x61+i*16),0,127,new MIDIboxFMModel(patch,0x62+i*16),0,false,new MIDIboxFMSender(patch,0x00,0x61+i*16),new MIDIboxFMSender(patch,0x00,0x62+i*16),"Atck","ALev"),
		new EnvelopeWidget.Node(0,127,new MIDIboxFMModel(patch,0x63+i*16),0,127,new MIDIboxFMModel(patch,0x64+i*16),0,false,new MIDIboxFMSender(patch,0x00,0x63+i*16),new MIDIboxFMSender(patch,0x00,0x64+i*16),"Dec1","DLev"),
		new EnvelopeWidget.Node(0,127,new MIDIboxFMModel(patch,0x65+i*16),0,127,new MIDIboxFMModel(patch,0x66+i*16),0,false,new MIDIboxFMSender(patch,0x00,0x65+i*16),new MIDIboxFMSender(patch,0x00,0x66+i*16),"Dec2","Sust"),
		new EnvelopeWidget.Node(127,127,null,5000,5000,null,0,false,null,null,null,null),     
		new EnvelopeWidget.Node(0,127,new MIDIboxFMModel(patch,0x67+i*16),0,0,null,0,false,new MIDIboxFMSender(patch,0x00,0x67+i*16),null,"Rel.",null),
		}     ),0,0,10,3,49);

	    JPanel curvepanel=new JPanel();
	    curvepanel.setLayout(new GridBagLayout());
	    addWidget(curvepanel,new KnobWidget("Curve",patch,0,127,-64,new MIDIboxFMModel(patch,0x68+i*16),new MIDIboxFMSender(patch,0x00,0x68+i*16)),0,0,3,3,50);
	    gbc.gridx=3;gbc.gridy=0;gbc.gridwidth=3;gbc.gridheight=1; curvepanel.add(new JLabel("Curve used on"),gbc);
	    addWidget(curvepanel,new CheckBoxWidget("Attack", patch,new MIDIboxFMModel(patch,0x60+i*16,0),new MIDIboxFMSender(patch,0x00,0x60+i*16,0)),6,0,1,1,-25);
	    addWidget(curvepanel,new CheckBoxWidget("Decay1", patch,new MIDIboxFMModel(patch,0x60+i*16,1),new MIDIboxFMSender(patch,0x00,0x60+i*16,1)),6,1,1,1,-26);
	    addWidget(curvepanel,new CheckBoxWidget("Decay2", patch,new MIDIboxFMModel(patch,0x60+i*16,2),new MIDIboxFMSender(patch,0x00,0x60+i*16,2)),6,2,1,1,-27);
	    addWidget(curvepanel,new CheckBoxWidget("Release", patch,new MIDIboxFMModel(patch,0x60+i*16,3),new MIDIboxFMSender(patch,0x00,0x60+i*16,3)),6,3,1,1,-28);
	    gbc.gridx=0;gbc.gridy=3;gbc.gridwidth=10;gbc.gridheight=4;
	    panel.add(curvepanel,gbc);

	    JPanel potpanel=new JPanel();
	    potpanel.setLayout(new GridBagLayout());
	    addWidget(potpanel,new KnobWidget("Pitch D.",patch,0,127,-64,new MIDIboxFMModel(patch,0x69+i*16),new MIDIboxFMSender(patch,0x00,0x69+i*16)),0,0,1,3,51);
	    addWidget(potpanel,new KnobWidget("-> LFO1 D.",patch,0,127,-64,new MIDIboxFMModel(patch,0x6e+i*16),new MIDIboxFMSender(patch,0x00,0x6e+i*16)),3,0,1,3,52);
	    addWidget(potpanel,new KnobWidget("AOUT D.",patch,0,127,-64,new MIDIboxFMModel(patch,0x6f+i*16),new MIDIboxFMSender(patch,0x00,0x6f+i*16)),6,0,1,3,53);
	    gbc.gridx=9;gbc.gridy=0;gbc.gridwidth=6;gbc.gridheight=1; potpanel.add(new JLabel("                                     "),gbc);
	    gbc.gridx=0;gbc.gridy=7;gbc.gridwidth=10;gbc.gridheight=3;
	    panel.add(potpanel,gbc);
	}
	gbc.gridx=0;gbc.gridy=GridBagConstraints.RELATIVE;gbc.gridwidth=5;gbc.gridheight=3;
        RightPanel.add(EGPane,gbc);


///////////////////////////////////////////////////////////////////////////////
// LFOs
///////////////////////////////////////////////////////////////////////////////
	JTabbedPane LFOPane=new JTabbedPane();   
	for(int i=0; i<2; i++) {     
	    JPanel panel = new JPanel();
	    panel.setLayout(new GridBagLayout());
	    LFOPane.addTab("LFO"+(i+1),panel);gbc.weightx=0;

	    addWidget(panel,new KnobWidget("Phase",patch,0,127,0,new MIDIboxFMModel(patch,0x41+i*16),new MIDIboxFMSender(patch,0x00,0x41+i*16)),0,0,1,3,54);
	    addWidget(panel,new KnobWidget("Rate",patch,0,127,0,new MIDIboxFMModel(patch,0x42+i*16),new MIDIboxFMSender(patch,0x00,0x42+i*16)),1,0,1,3,55);
	    addWidget(panel,new KnobWidget("Pitch D.",patch,0,127,-64,new MIDIboxFMModel(patch,0x43+i*16),new MIDIboxFMSender(patch,0x00,0x43+i*16)),2,0,1,3,56);
	    addWidget(panel,new KnobWidget("->LFO" +((i+1)%2+1)+" D.",patch,0,127,-64,new MIDIboxFMModel(patch,0x48+i*16),new MIDIboxFMSender(patch,0x00,0x48+i*16)),3,0,1,3,57);
	    addWidget(panel,new KnobWidget("AOUT D.",patch,0,127,-64,new MIDIboxFMModel(patch,0x49+i*16),new MIDIboxFMSender(patch,0x00,0x49+i*16)),4,0,1,3,58);

	    addWidget(panel,new ComboBoxWidget("Mode",patch,new MIDIboxFMModel(patch,0x40+i*16,0,0x7,new int[]{0x0,0x01,0x05}),new MIDIboxFMSender(patch,0x00,0x40+i*16,0,0x7,new int[]{0x0,0x01,0x05}),new String []{"off", "unsynced","synced"}),0,3,2,1,-30);
	    addWidget(panel,new ComboBoxWidget("Waveform",patch,new MIDIboxFMModel(patch,0x40+i*16,4,0x7),new MIDIboxFMSender(patch,0x00,0x40+i*16,4,0x7),new String []{"SINE", "TRIANGLE","SAW","PULSE","RANDOM"}),2,3,3,1,-31);
	}
	gbc.gridx=0;gbc.gridy=GridBagConstraints.RELATIVE;gbc.gridwidth=5;gbc.gridheight=2;
	RightPanel.add(LFOPane,gbc);


///////////////////////////////////////////////////////////////////////////////
// Controller Assigns
///////////////////////////////////////////////////////////////////////////////
	JTabbedPane assignPane=new JTabbedPane();

	// Assign Velocity Pane
  	JPanel LFOAPane=new JPanel();
	LFOAPane.setLayout(new GridBagLayout());
	assignPane.addTab("Velocity", LFOAPane); gbc.weightx=0;
  	addWidget(LFOAPane,new ComboBoxWidget("Parameter",patch,new MIDIboxFMModel(patch,0x4c),new MIDIboxFMSender(patch,0x00,0x4c),parName),0,0,5,1,-32);
  	addWidget(LFOAPane,new ScrollBarWidget("Init Value",patch,0,127,0,new MIDIboxFMModel(patch,0x4d),new MIDIboxFMSender(patch,0x00,0x4d)),0,1,5,1,59);
  	addWidget(LFOAPane,new ScrollBarWidget("Depth",patch,0,127,-64,new MIDIboxFMModel(patch,0x4e),new MIDIboxFMSender(patch,0x00,0x4e)),0,2,5,1,60);

  	// Assign Mod Pane
  	JPanel MODAPane=new JPanel();
  	MODAPane.setLayout(new GridBagLayout());
	assignPane.addTab("ModWheel", MODAPane); gbc.weightx=0;
  	addWidget(MODAPane,new ComboBoxWidget("Parameter",patch,new MIDIboxFMModel(patch,0x7c),new MIDIboxFMSender(patch,0x00,0x7c),parName),0,0,5,1,-33);
  	addWidget(MODAPane,new ScrollBarWidget("Init Value",patch,0,127,0,new MIDIboxFMModel(patch,0x7d),new MIDIboxFMSender(patch,0x00,0x7d)),0,1,5,1,67);
  	addWidget(MODAPane,new ScrollBarWidget("Depth",patch,0,127,-64,new MIDIboxFMModel(patch,0x7e),new MIDIboxFMSender(patch,0x00,0x7e)),0,2,5,1,68);

  	// Assign Aftertouch Pane
  	JPanel AFTAPane=new JPanel();
  	AFTAPane.setLayout(new GridBagLayout());
	assignPane.addTab("Aftertouch", AFTAPane); gbc.weightx=0;
  	addWidget(AFTAPane,new ComboBoxWidget("Parameter",patch,new MIDIboxFMModel(patch,0x5c),new MIDIboxFMSender(patch,0x00,0x5c),parName),0,0,5,1,-34);
  	addWidget(AFTAPane,new ScrollBarWidget("Init Value",patch,0,127,0,new MIDIboxFMModel(patch,0x5d),new MIDIboxFMSender(patch,0x00,0x5d)),0,1,5,1,69);
  	addWidget(AFTAPane,new ScrollBarWidget("Depth",patch,0,127,-64,new MIDIboxFMModel(patch,0x5e),new MIDIboxFMSender(patch,0x00,0x5e)),0,2,5,1,70);

	gbc.gridx=0;gbc.gridy=GridBagConstraints.RELATIVE;gbc.gridwidth=5;gbc.gridheight=3;
	RightPanel.add(assignPane,gbc);


///////////////////////////////////////////////////////////////////////////////
	gbc.gridx=10;gbc.gridy=0;gbc.gridwidth=5;gbc.gridheight=20;
	scrollPane.add(RightPanel,gbc);
///////////////////////////////////////////////////////////////////////////////


	pack();
    }


    public void sendDumpChanges()
    {
	MIDIboxFMSlowSender SlowSender = new MIDIboxFMSlowSender();
	byte[] cooked_dump = dataModel.getCookedDump();

	for(int i=0; i<4*32; ++i) {
	    byte stored_value = ((Patch)p).sysex[10+0x80+i];
	    if( stored_value != cooked_dump[i] )
	    {
		System.out.println("Wavetable Field changed: " + i);
	        ((Patch)p).sysex[10+0x80+i] = cooked_dump[i];
		SlowSender.sendParameter((Driver) ((Patch)p).getDriver(), 0x80+i, cooked_dump[i], 50);
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


