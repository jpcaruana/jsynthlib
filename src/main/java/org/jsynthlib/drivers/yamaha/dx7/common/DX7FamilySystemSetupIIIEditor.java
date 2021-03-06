/*
 * JSynthlib -	generic "System Setup III" Editor for Yamaha DX7 Family
 *		(used by TX802)
 * ====================================================================
 * @version $Id$
 * @author  Torsten Tittmann
 *
 * Copyright (C) 2002-2004 Torsten.Tittmann@gmx.de
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
 */
package org.jsynthlib.drivers.yamaha.dx7.common;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.text.DecimalFormat;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jsynthlib.core.ComboBoxWidget;
import org.jsynthlib.core.ParamModel;
import org.jsynthlib.core.Patch;
import org.jsynthlib.core.PatchEdit;
import org.jsynthlib.core.PatchEditorFrame;
import org.jsynthlib.core.ScrollBarWidget;
import org.jsynthlib.core.SpinnerWidget;
import org.jsynthlib.core.SysexSender;


public class DX7FamilySystemSetupIIIEditor extends PatchEditorFrame
{
	static final String [] OnOffName = new String [] {
		"Off",
		"On"
	};

	static final String [] MidiReceiveName = new String [] {
		"off","norm",
		"G-1","G-2","G-3","G-4","G-5","G-6","G-7","G-8",
		"G-9","G-10","G-11","G-12","G-13","G-14","G-15","G-16"
	};

	static final String [] NoteOnOffName = new String [] {
		"all",
		"odd",
		"even"
	};

	static final String [] CtrlNoAssignName = new String [] {
		"off",
		"Modulation Wheel",
		"Breath Control",
		"Foot Control",
		"Portamento Time",
		"Volume",
		"Sustain Switch",
		"Portamento Switch"
	};

	static final String [] SelectChgName = new String [] {
		"off","1","2","3","4","5","6","7","8",
		"9","10","11","12","13","14","15","16","all"
	};

	static final String [] PerfSelNoName = new String [] {
		"  0","  1","  2","  3","  4","  5","  6","  7","  8","  9"," 10"," 11"," 12"," 13"," 14"," 15",
		" 16"," 17"," 18"," 19"," 20"," 21"," 22"," 23"," 24"," 25"," 26"," 27"," 28"," 29"," 30"," 31",
		" 32"," 33"," 34"," 35"," 36"," 37"," 38"," 39"," 40"," 41"," 42"," 43"," 44"," 45"," 46"," 47",
		" 48"," 49"," 50"," 51"," 52"," 53"," 54"," 55"," 56"," 57"," 58"," 59"," 60"," 61"," 62"," 63",
		" 64"," 65"," 66"," 67"," 68"," 69"," 70"," 71"," 72"," 73"," 74"," 75"," 76"," 77"," 78"," 79",
		" 80"," 81"," 82"," 83"," 84"," 85"," 86"," 87"," 88"," 89"," 90"," 91"," 92"," 93"," 94"," 95",
		" 96"," 97"," 98"," 99","100","101","102","103","104","105","106","107","108","109","110","111",
		"112","113","114","115","116","117","118","119","120","121","122","123","124","125","126","127",
	};

	static final String [] BlockSelName = new String [] {
		" 1-32",
		"33-64"
	};

	static final String [] CartrBankSelName = new String [] {
		"  0","  1","  2","  3","  4","  5","  6","  7","  8","  9"," 10"," 11"," 12"," 13"," 14"," 15",
	};


	static final String[] MIDIcontroller = {
		"off", "1 Mod. Wheel", "2 Breath Ctrl", "3 ", "4 Foot Ctrl", "5 Porta. Time", "6 ", "7 Volume",
		"8 ", "9 ", "10 ", "11 ", "12 ", "13 ", "14 ", "15 ", "16 ", "17 ", "18 ", "19 ", "20 ", "21 ",
		"22 ", "23 ", "24 ", "25 ", "26 ", "27 ", "28 ", "29 ", "30 ", "31 ", "32 ", "33 ", "34 ", "35 ",
		"36 ", "37 ", "38 ", "39 ", "40 ", "41 ", "42 ", "43 ", "44 ", "45 ", "46 ", "47 ", "48 ", "49 ",
		"50 ", "51 ", "52 ", "53 ", "54 ", "55 ", "56 ", "57 ", "58 ", "59 ", "60 ", "61 ", "62 ", "63 ",
		"64 Sustain", "65 Portamento", "66 ", "67 ", "68 ", "69 ", "70 ", "71 ", "72 ", "73 ", "74 ", "75 ",
		"76 ", "77 ", "78 ", "79 ", "80 ", "81 ", "82 ", "83 ", "84 ", "85 ", "86 ", "87 ", "88 ", "89 ",
		"90 ", "91 ", "92 ", "93 ", "94 ", "95 ", "96 ", "97 ", "98 ", "99 ", "100 ", "101 ", "102 ", "103 ",
		"104 ", "105 ", "106 ", "107 ", "108 ", "109 ", "110 ", "111 ", "112 ", "113 ", "114 ", "115 ", "116 ",
		"117 ", "118 ", "119 ", "120 ", "121 ", "122 ", "123 ", "124 ", "125 ", "126 ", "127 "
	};


	public DX7FamilySystemSetupIIIEditor(String name, Patch patch)
	{
		super (name, patch);

		buildEditor(patch);
	}


	protected void buildEditor(Patch patch)
	{
		PatchEdit.showWaitDialog();	// Because it needs some time to build up the editor frame


		int ofs=1;				// auxiliary value, because parameter 0 (PROTCT) isn't transmitted!
		final DecimalFormat freqFormatter = new DecimalFormat("###0.00");

		JTabbedPane systemPane=new JTabbedPane();
		systemPane.setPreferredSize(new Dimension(800,450));

		JPanel cmnPane = new JPanel();
		cmnPane.setLayout(new GridBagLayout());gbc.weightx=1;
		systemPane.addTab("System Setup",cmnPane);

		final ScrollBarWidget MasterTune = new ScrollBarWidget(" ",patch,0,127,-64,new ParamModel(patch,16+12-ofs),new MasterTuneSender(64));
		MasterTune.addEventListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				MasterTune.setLabel("Master Tuning (Concert Pitch: " +
					freqFormatter.format(440+(4.400*0.059463094359*1.2 * (MasterTune.getValue()-64))) + " Hz)");
			}
		});
		MasterTune.setLabel("Master Tuning (Concert Pitch: " +
			freqFormatter.format(440+(4.400*0.059463094359*1.2 * (MasterTune.getValue()-64))) + " Hz)");
		gbc.gridx=0;gbc.gridy=0;gbc.gridwidth=1;gbc.gridheight=1; cmnPane.add(new JLabel(" "),gbc);
		addWidget(cmnPane,MasterTune,0,1,8,1,1);

		gbc.gridx=0;gbc.gridy=2;gbc.gridwidth=1;gbc.gridheight=1; cmnPane.add(new JLabel(" "),gbc);
		gbc.gridx=0;gbc.gridy=3;gbc.gridwidth=4;gbc.gridheight=1; cmnPane.add(new JLabel("System Exclusiv:"),gbc);
		addWidget(cmnPane,new ComboBoxWidget("Device Number",patch,new ParamModel(patch,16+ 2-ofs),null,SelectChgName), 0, 4,4,1, 3);
		addWidget(cmnPane,new ComboBoxWidget("Voice Data Receive Block",patch,
					new ParamModel(patch,16+ 3-ofs),new SystemSetupSender(77),BlockSelName),4,4,4,1, 4);

		gbc.gridx=0;gbc.gridy=5;gbc.gridwidth=1;gbc.gridheight=1;cmnPane.add(new JLabel(" "),gbc);
		gbc.gridx=0;gbc.gridy=6;gbc.gridwidth=4;gbc.gridheight=1; cmnPane.add(new JLabel("Performance Change:"),gbc);
		addWidget(cmnPane,new ComboBoxWidget("Select by program change",patch,new ParamModel(patch,16+ 1-ofs),null,SelectChgName), 0, 7,4,1, 5);
		addWidget(cmnPane,new ComboBoxWidget("Select Assign Table",patch,new ParamModel(patch,16+ 8-ofs),null,OnOffName), 4, 7,4,1, 6);

		gbc.gridx=0;gbc.gridy=8;gbc.gridwidth=1;gbc.gridheight=1;cmnPane.add(new JLabel(" "),gbc);
		gbc.gridx=0;gbc.gridy=9;gbc.gridwidth=4;gbc.gridheight=1;cmnPane.add(new JLabel("Receive Switch:"),gbc);
		addWidget(cmnPane,new ComboBoxWidget("Note On/Off",patch,new ParamModel(patch,16+ 7-ofs),null,NoteOnOffName), 0,10,4,1, 7);
		addWidget(cmnPane,new ComboBoxWidget("Program Change",patch,new ParamModel(patch,16+ 4-ofs),null,MidiReceiveName), 4,10,4,1, 8);
		addWidget(cmnPane,new ComboBoxWidget("Control Change",patch,new ParamModel(patch,16+13-ofs),null,MidiReceiveName), 8,10,4,1,11);
		addWidget(cmnPane,new ComboBoxWidget("Pitch Bend",patch,new ParamModel(patch,16+ 6-ofs),null,MidiReceiveName), 0,11,4,1,10);
		addWidget(cmnPane,new ComboBoxWidget("After Touch",patch,new ParamModel(patch,16+ 5-ofs),null,MidiReceiveName), 4,11,4,1, 9);

		gbc.gridx=0;gbc.gridy=13;gbc.gridwidth=1;gbc.gridheight=1; cmnPane.add(new JLabel(" "),gbc);
		gbc.gridx=0;gbc.gridy=14;gbc.gridwidth=4;gbc.gridheight=1; cmnPane.add(new JLabel("Bank Select:"),gbc);
		addWidget(cmnPane,new ComboBoxWidget("for TX802 format",patch,new ParamModel(patch,16+ 9-ofs),null,CartrBankSelName), 0,15,4,1,12);
		addWidget(cmnPane,new ComboBoxWidget("for fractional scaling",patch,new ParamModel(patch,16+10-ofs),null,CartrBankSelName), 4,15,4,1,13);
		addWidget(cmnPane,new ComboBoxWidget("for micro tuning",patch,new ParamModel(patch,16+11-ofs),null,CartrBankSelName), 8,15,4,1,14);


		JPanel perfPane = new JPanel();
		perfPane.setLayout(new GridBagLayout());
		JScrollPane perfScrollPane = new JScrollPane(perfPane);
		systemPane.addTab("Performance Select Number Assign",perfScrollPane);

		gbc.gridx=0;gbc.gridy=0;gbc.gridwidth=30;gbc.gridheight=1;
		perfPane.add(new JLabel("Performance Select Tabel for re-assigning incoming MIDI Program Change Messages"),gbc);
		gbc.gridx=0;gbc.gridy=1;gbc.gridwidth=30;gbc.gridheight=1;
		perfPane.add(new JLabel("  ( \"Incoming Program Change Message\" will determine the selected Performance Number)  "),gbc);
		//gbc.gridx=0;gbc.gridy=2;gbc.gridwidth=1;gbc.gridheight=1; perfPane.add(new JLabel(" "),gbc);
		for (int i=0, j=0; i<128; i++) {
			if (i%8==0) j++;

			gbc.gridx=4*(i%8);gbc.gridy=3+j;gbc.gridwidth=1;gbc.gridheight=1; perfPane.add(new JLabel(" "),gbc);
			gbc.gridx=4*(i%8)+1;gbc.gridy=3+j;gbc.gridwidth=1;gbc.gridheight=1;gbc.fill=GridBagConstraints.BOTH;gbc.anchor=GridBagConstraints.EAST;
			perfPane.add(new JLabel(Integer.toString(i),SwingConstants.RIGHT),gbc);

			addWidget(perfPane,new SpinnerWidget("",patch,0,127,0,new ParamModel(patch,16+136+i-ofs),null),4*(i%8)+2,3+j,1,1,150+i);

			gbc.gridx=4*(i%8)+3;gbc.gridy=3+j;gbc.gridwidth=1;gbc.gridheight=1; perfPane.add(new JLabel(" "),gbc);
		}

		JPanel ctrlPane = new JPanel();
		ctrlPane.setLayout(new GridBagLayout());
		JScrollPane ctrlScrollPane = new JScrollPane(ctrlPane);
		systemPane.addTab("Control Number Assign",ctrlScrollPane);

		gbc.gridx=0;gbc.gridy=0;gbc.gridwidth=8;gbc.gridheight=1;gbc.weightx=1;
		ctrlPane.add(new JLabel("Control Number Tabel for re-assigning incoming MIDI Control Change Messages"),gbc);
		gbc.gridx=0;gbc.gridy=1;gbc.gridwidth=8;gbc.gridheight=1;
		ctrlPane.add(new JLabel("  ( \"Incoming Control Message\" will control the selected Controller)  "),gbc);
		gbc.gridx=0;gbc.gridy=2;gbc.gridwidth=1;gbc.gridheight=1; ctrlPane.add(new JLabel(" "),gbc);
		for (int i=0, j=0; i<122; i++) {
			if (i%4==0) j++;

			addWidget(ctrlPane,new ComboBoxWidget(Integer.toString(i),patch,
						new ParamModel(patch,16+14+i-ofs),null,MIDIcontroller),0+2*(i%4),3+j,2,1,150+i);
		}

		gbc.gridx=0;gbc.gridy=1;gbc.gridwidth=1;gbc.gridheight=1;
		scrollPane.add(systemPane,gbc);
		pack();

		PatchEdit.hideWaitDialog();	// Okay, the editor frame is ready
	}


	/*
	 * SysexSender - System Setup
	 *		     (g=6; h=1)
	 */
	class SystemSetupSender extends SysexSender
	{
		int parameter;
		byte []b = new byte [7];

		public SystemSetupSender(int param)
		{
			parameter=param;

			b[0]=(byte)0xF0;
			b[1]=(byte)0x43;
			b[3]=(byte)0x19;
			b[4]=(byte)parameter;
			b[6]=(byte)0xF7;
		}

		public byte [] generate (int value)
		{
			b[2]=(byte)(0x10+channel-1);
			b[5]=(byte)value;

			return b;
		}
	}

	/*
	 * SysexSender - Master Tune
	 *		     (g=1; h=0)
	 */
	class MasterTuneSender extends SysexSender
	{
		int parameter;
		byte []b = new byte [7];

		public MasterTuneSender(int param)
		{
			parameter=param;

			b[0]=(byte)0xF0;
			b[1]=(byte)0x43;
			b[3]=(byte)0x04;
			b[4]=(byte)parameter;
			b[6]=(byte)0xF7;
		}

		public byte [] generate (int value)
		{
			b[2]=(byte)(0x10+channel-1);
			b[5]=(byte)value;

			return b;
		}
	}
}
