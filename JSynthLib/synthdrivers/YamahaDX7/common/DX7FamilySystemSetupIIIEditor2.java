/*
 * JSynthlib -	generic "System Setup III" Editor No.2" for Yamaha DX7 Family
 *		(used by TX802)
 * ==========================================================================
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
package synthdrivers.YamahaDX7.common;
import core.*;
import java.lang.String.*;
import java.text.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;
import javax.swing.event.*;

public class DX7FamilySystemSetupIIIEditor2 extends DX7FamilySystemSetupIIIEditor 
{
	public DX7FamilySystemSetupIIIEditor2(String name, Patch patch)
	{
		super (name, patch);

		buildEditor(patch);
	}


	protected void buildEditor(Patch patch)
	{
		PatchEdit.waitDialog.show();	// Because it needs some time to build up the editor frame


		int ofs=1;				// auxiliary value, because parameter 0 (PROTCT) isn't transmitted!
		final DecimalFormat freqFormatter = new DecimalFormat("###0.00");

		JTabbedPane systemPane=new JTabbedPane();
		systemPane.setPreferredSize(new Dimension(800,450));

		JPanel cmnPane = new JPanel();
		cmnPane.setLayout(new GridBagLayout());gbc.weightx=1;
		systemPane.addTab("System Setup",cmnPane);

		final ScrollBarWidget MasterTune = new ScrollBarWidget(" ",patch,0,127,-64,new ParamModel(patch,16+12-ofs),new MasterTuneSender(64));
		MasterTune.slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				MasterTune.setLabel("Master Tuning (Concert Pitch: " +
					freqFormatter.format(440+(4.400*0.059463094359*1.2 * (MasterTune.slider.getValue()-64))) + " Hz)");
			}
		});
		MasterTune.setLabel("Master Tuning (Concert Pitch: " +
			freqFormatter.format(440+(4.400*0.059463094359*1.2 * (MasterTune.slider.getValue()-64))) + " Hz)");
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
			gbc.gridx=4*(i%8)+1;gbc.gridy=3+j;gbc.gridwidth=1;gbc.gridheight=1;gbc.fill=gbc.BOTH;gbc.anchor=gbc.EAST;
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
		show();

		PatchEdit.waitDialog.hide();	// Okay, the editor frame is ready
	}
}
