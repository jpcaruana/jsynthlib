/*
 * JSynthlib -	generic "Performance" Editor for Yamaha DX7 Family
 * (used by DX1, DX5, DX7 MKI, TX7, TX816)
 * ===============================================================
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

public class DX7FamilyPerformanceEditor extends PatchEditorFrame
{
	private static final String [] OnOffName	   = new String [] {
		"Off",
		"On"
	};

	private static final String [] SourceSelectName    = new String [] {
		"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16"
	};

	private static final String [] PolyMonoName	   = new String [] {
		"Poly",
		"Mono"
	};

	private static final String [] VoiceSelectName	   = new String [] {
		"Voice A",
		"Voice B"
	};

	private static final String [] VoiceNumberName	   = new String [] {
		" 1"," 2"," 3"," 4"," 5"," 6"," 7"," 8"," 9","10","11","12","13","14","15","16",
		"17","18","19","20","21","22","23","24","25","26","27","28","29","30","31","32",
		"33","34","35","36","37","38","39","40","41","42","43","44","45","46","47","48",
		"49","50","51","52","53","54","55","56","57","58","59","60","61","62","63","64"
	};

	private static final String [] KeyAssignName	   = new String [] {
		"Single",
		"Dual",
		"Split"
	};
	
	private static final String [] PitchBendRangeName  = new String [] {
		"Off",				//
		"perfect First (Unison)",	// Prime
		"minor Second",			// kleine Sekunde
		"major Second",			// große  Sekunde
		"minor Third",			// kleine Terz
		"major Third",			// große  Terz
		"perfect Fourth",		// Quarte
		"perfect Fifth",		// Quinte bzw. Tritonus 
		"minor Sixth",			// kleine Sexte
		"major Sixth",			// große  Sexte
		"minor Seventh",		// kleine Septine
		"major Seventh",		// große  Septine
		"Octave"			// Oktave
	};

	private static final String [] PitchBendStepName   = new String [] {
		"Continuous",
		"±1 Octave	  - 12 Steps",
		"±1 Octave	  -  6 Steps",
		"±1 Octave	  -  4 Steps",
		"±1 Octave	  -  3 Steps",
		"±1 minor Seventh -  2 Steps",
		"±1 Octave	  -  2 Steps",
		"±1 Fifth	  -  1 Step ",
		"±1 major Fifth   -  1 Step ",
		"±1 Sixth	  -  1 Step ",
		"±1 minor Seventh -  1 Step ",
		"±1 major Seventh -  1 Step ",
		"±1 Octave	  -  1 Step "
	};

	private static final String [] PortGlissName	   = new String [] {
		"Portamento",
		"Glissando"
	};
	
	private static final String [] PortModeName	   = new String [] {
		"Retain/Fingered",
		"Follow/Full Time"
	};

	private static final String [] AssignName	   = new String [] {
		"Off",
		"Pitch",
		"Amplitude",
		"Pitch + Amplitude",
		"EG Bias",
		"Pitch + EG Bias",
		"Pitch + Amplitude",
		"Pitch + Amplitude + EG Bias"
	};


	public DX7FamilyPerformanceEditor(String name, Patch patch)
	{
		super (name ,patch);

		buildEditor(patch);
	}
   

	protected void buildEditor (Patch patch)
	{

		/*
		 *   Performance Parameter - Common settings
		 */

		JPanel cmnPane= new JPanel();
		cmnPane.setLayout(new GridBagLayout());gbc.weightx=0;
		addWidget(cmnPane,new PatchNameWidget("Performance Name (30 Char.)", patch),0,0,7,1,1);

		addWidget(cmnPane,new ComboBoxWidget("Key Assign Mode",patch,new ParamModel(patch,6+60),new PerformanceSender(patch,30),KeyAssignName),0,1,3,1,4);
		addWidget(cmnPane,new ComboBoxWidget("Voice Memory Select Flag",patch,new ParamModel(patch,6+61),new PerformanceSender(patch,31),VoiceSelectName),3,1,3,1,2);
		addWidget(cmnPane,new ComboBoxWidget("Corresponding Voice?",patch,new ParamModel(patch,6+0),null,VoiceNumberName),0,2,3,1,3);
		gbc.gridx=3;gbc.gridy=2;gbc.gridwidth=6;gbc.gridheight=1;gbc.anchor=gbc.EAST;
		//cmnPane.add(new JLabel("(Attention! This is an undocumented parameter and only valid for a single patch!)"),gbc);
		cmnPane.add(new JLabel("(Attention! This is an undocumented parameter!)					   "),gbc);

		gbc.gridx=0;gbc.gridy=0;gbc.gridwidth=3;gbc.gridheight=1;
		cmnPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Common",TitledBorder.CENTER,TitledBorder.CENTER));
		scrollPane.add(cmnPane,gbc);

		/*
		 *   Voice A+B Parameter
		 */
		JTabbedPane voicePane=new JTabbedPane();

		// Voice A
		JPanel voiceApanel = new JPanel();
		voiceApanel.setLayout(new GridBagLayout());
		voicePane.addTab("Voice A",voiceApanel);gbc.weightx=1;

		gbc.gridx=0;gbc.gridy=0;gbc.gridwidth=1;gbc.gridheight=1; voiceApanel.add(new JLabel(" "),gbc);
		addWidget(voiceApanel,new ComboBoxWidget("Key Mode Assign",patch,new ParamModel(patch,6+2),new PerformanceSender(patch,2),PolyMonoName),3,0,6,1,10);
		addWidget(voiceApanel,new ScrollBarWidget("Output Attenuator",patch,0,7,0,new ParamModel(patch,6+26),new PerformanceSender(patch,26)),9,0,6,1,19);

		gbc.gridx=0;gbc.gridy=1;gbc.gridwidth=1;gbc.gridheight=1; voiceApanel.add(new JLabel(" "),gbc);
		gbc.gridx=0;gbc.gridy=2;gbc.gridwidth=3;gbc.gridheight=1; voiceApanel.add(new JLabel("Pitch Bend: "),gbc);
		addWidget(voiceApanel,new ComboBoxWidget("Range",patch,new ParamModel(patch,6+3),new PerformanceSender(patch,3),PitchBendRangeName),3,2,6,1,11);
		addWidget(voiceApanel,new ComboBoxWidget("Step",patch,new ParamModel(patch,6+4),new PerformanceSender(patch,4),PitchBendStepName),9,2,6,1,12);

		gbc.gridx=0;gbc.gridy=3;gbc.gridwidth=1;gbc.gridheight=1; voiceApanel.add(new JLabel(" "),gbc);
		gbc.gridx=0;gbc.gridy=4;gbc.gridwidth=3;gbc.gridheight=1; voiceApanel.add(new JLabel("Portamento: "),gbc);
		addWidget(voiceApanel,new ComboBoxWidget("Portamento/Glissando",patch,new ParamModel(patch,6+6),new PerformanceSender(patch,6),PortGlissName),3,4,6,1,13);
		addWidget(voiceApanel,new ComboBoxWidget("Mode (Poly/Mono)",patch,new ParamModel(patch,6+7),new PerformanceSender(patch,7),PortModeName),3,5,6,1,14);
		addWidget(voiceApanel,new ScrollBarWidget("Time",patch,0,99,0,new ParamModel(patch,6+5),new PerformanceSender(patch,5)),9,5,6,1,20);

		gbc.gridx=0;gbc.gridy=6;gbc.gridwidth=1;gbc.gridheight=1; voiceApanel.add(new JLabel(" "),gbc);
		gbc.gridx=0;gbc.gridy=7;gbc.gridwidth=3;gbc.gridheight=1; voiceApanel.add(new JLabel("Modulation Wheel: "),gbc);
		addWidget(voiceApanel,new ComboBoxWidget("Assign",patch,new ParamModel(patch,6+10),new PerformanceSender(patch,10),AssignName),3,7,6,1,15);
		addWidget(voiceApanel,new ScrollBarWidget("Sensitivity",patch,0,15,0,new ParamModel(patch,6+9),new PerformanceSender(patch,9)),9,7,6,1,21);

		gbc.gridx=0;gbc.gridy=8;gbc.gridwidth=1;gbc.gridheight=1; voiceApanel.add(new JLabel(" "),gbc);
		gbc.gridx=0;gbc.gridy=9;gbc.gridwidth=3;gbc.gridheight=1; voiceApanel.add(new JLabel("Foot Controller: "),gbc);
		addWidget(voiceApanel,new ComboBoxWidget("Assign",patch,new ParamModel(patch,6+12),new PerformanceSender(patch,12),AssignName),3,9,6,1,16);
		addWidget(voiceApanel,new ScrollBarWidget("Sensitivity",patch,0,15,0,new ParamModel(patch,6+11),new PerformanceSender(patch,11)),9,9,6,1,22);

		gbc.gridx=0;gbc.gridy=10;gbc.gridwidth=1;gbc.gridheight=1; voiceApanel.add(new JLabel(" "),gbc);
		gbc.gridx=0;gbc.gridy=11;gbc.gridwidth=3;gbc.gridheight=1; voiceApanel.add(new JLabel("After Touch: "),gbc);
		addWidget(voiceApanel,new ComboBoxWidget("Assign",patch,new ParamModel(patch,6+14),new PerformanceSender(patch,14),AssignName),3,11,6,1,17);
		addWidget(voiceApanel,new ScrollBarWidget("Sensitivity",patch,0,15,0,new ParamModel(patch,6+13),new PerformanceSender(patch,13)),9,11,6,1,23);

		gbc.gridx=0;gbc.gridy=12;gbc.gridwidth=1;gbc.gridheight=1; voiceApanel.add(new JLabel(" "),gbc);
		gbc.gridx=0;gbc.gridy=13;gbc.gridwidth=3;gbc.gridheight=1; voiceApanel.add(new JLabel("Breath Controller: "),gbc);
		addWidget(voiceApanel,new ComboBoxWidget("Assign",patch,new ParamModel(patch,6+16),new PerformanceSender(patch,16),AssignName),3,13,6,1,18);
		addWidget(voiceApanel,new ScrollBarWidget("Sensitivity",patch,0,15,0,new ParamModel(patch,6+15),new PerformanceSender(patch,15)),9,13,6,1,24);

		// Voice B
		JPanel voiceBpanel = new JPanel();
		voiceBpanel.setLayout(new GridBagLayout());
		voicePane.addTab("Voice B",voiceBpanel);gbc.weightx=1;

		gbc.gridx=0;gbc.gridy=0;gbc.gridwidth=1;gbc.gridheight=1; voiceBpanel.add(new JLabel(" "),gbc);
		addWidget(voiceBpanel,new ComboBoxWidget("Key Mode Assign",patch,new ParamModel(patch,6+32),null,PolyMonoName),3,0,6,1,40);
		addWidget(voiceBpanel,new ScrollBarWidget("Output Attenuator",patch,0,7,0,new ParamModel(patch,6+56),null),9,0,6,1,49);

		gbc.gridx=0;gbc.gridy=1;gbc.gridwidth=1;gbc.gridheight=1; voiceBpanel.add(new JLabel(" "),gbc);
		gbc.gridx=0;gbc.gridy=2;gbc.gridwidth=3;gbc.gridheight=1; voiceBpanel.add(new JLabel("Pitch Bend: "),gbc);
		addWidget(voiceBpanel,new ComboBoxWidget("Range",patch,new ParamModel(patch,6+33),null,PitchBendRangeName),3,2,6,1,41);
		addWidget(voiceBpanel,new ComboBoxWidget("Step",patch,new ParamModel(patch,6+34),null,PitchBendStepName),9,2,6,1,42);

		gbc.gridx=0;gbc.gridy=3;gbc.gridwidth=1;gbc.gridheight=1; voiceBpanel.add(new JLabel(" "),gbc);
		gbc.gridx=0;gbc.gridy=4;gbc.gridwidth=3;gbc.gridheight=1; voiceBpanel.add(new JLabel("Portamento: "),gbc);
		addWidget(voiceBpanel,new ComboBoxWidget("Portamento/Glissando",patch,new ParamModel(patch,6+36),null,PortGlissName),3,4,6,1,43);
		addWidget(voiceBpanel,new ComboBoxWidget("Mode (Poly/Mono)",patch,new ParamModel(patch,6+37),null,PortModeName),3,5,6,1,44);
		addWidget(voiceBpanel,new ScrollBarWidget("Time",patch,0,99,0,new ParamModel(patch,6+35),null),9,5,6,1,50);

		gbc.gridx=0;gbc.gridy=6;gbc.gridwidth=1;gbc.gridheight=1; voiceBpanel.add(new JLabel(" "),gbc);
		gbc.gridx=0;gbc.gridy=7;gbc.gridwidth=3;gbc.gridheight=1; voiceBpanel.add(new JLabel("Modulation Wheel: "),gbc);
		addWidget(voiceBpanel,new ComboBoxWidget("Assign",patch,new ParamModel(patch,6+40),null,AssignName),3,7,6,1,45);
		addWidget(voiceBpanel,new ScrollBarWidget("Sensitivity",patch,0,15,0,new ParamModel(patch,6+39),null),9,7,6,1,51);

		gbc.gridx=0;gbc.gridy=8;gbc.gridwidth=1;gbc.gridheight=1; voiceBpanel.add(new JLabel(" "),gbc);
		gbc.gridx=0;gbc.gridy=9;gbc.gridwidth=3;gbc.gridheight=1; voiceBpanel.add(new JLabel("Foot Controller: "),gbc);
		addWidget(voiceBpanel,new ComboBoxWidget("Assign",patch,new ParamModel(patch,6+42),null,AssignName),3,9,6,1,46);
		addWidget(voiceBpanel,new ScrollBarWidget("Sensitivity",patch,0,15,0,new ParamModel(patch,6+41),null),9,9,6,1,52);

		gbc.gridx=0;gbc.gridy=10;gbc.gridwidth=1;gbc.gridheight=1; voiceBpanel.add(new JLabel(" "),gbc);
		gbc.gridx=0;gbc.gridy=11;gbc.gridwidth=3;gbc.gridheight=1; voiceBpanel.add(new JLabel("After Touch: "),gbc);
		addWidget(voiceBpanel,new ComboBoxWidget("Assign",patch,new ParamModel(patch,6+44),null,AssignName),3,11,6,1,47);
		addWidget(voiceBpanel,new ScrollBarWidget("Sensitivity",patch,0,15,0,new ParamModel(patch,6+43),null),9,11,6,1,53);

		gbc.gridx=0;gbc.gridy=12;gbc.gridwidth=1;gbc.gridheight=1; voiceBpanel.add(new JLabel(" "),gbc);
		gbc.gridx=0;gbc.gridy=13;gbc.gridwidth=3;gbc.gridheight=1; voiceBpanel.add(new JLabel("Breath Controller: "),gbc);
		addWidget(voiceBpanel,new ComboBoxWidget("Assign",patch,new ParamModel(patch,6+46),null,AssignName),3,13,6,1,48);
		addWidget(voiceBpanel,new ScrollBarWidget("Sensitivity",patch,0,15,0,new ParamModel(patch,6+45),null),9,13,6,1,54);

		gbc.gridx=0;gbc.gridy=10;gbc.gridwidth=3;gbc.gridheight=13;
		scrollPane.add(voicePane,gbc);
		pack();
		show();
	}


	/*
	 * SysexSender - Performance Parameter
	 *		 DX1, TX7	(g=1; h=0)
	 *		 DX7		(g=2; h=0)
	 *
	 * Since the DX7 doesn't support the performance patch directly,
	 * this SysexSender makes also the necessary translations between
	 * DX7 Function parameter change commands and
	 * DX Performance parameter change commands.
	 *
	 * So we don't need to write an own editor for the DX7!
	 *		 
	 */
	class PerformanceSender extends SysexSender
	{
		Patch patch;
		int parameter;
		byte []b = new byte [7];

		// translation table TX7->DX7 for Sensitivity parameters (ModulationWheel, FootCtrl, BreathCtrl, AfterTouch)
		private final byte []TX2DXsens = new byte [] {0x00,0x06,0x0d,0x13,0x1A,0x21,0x27,0x2E,0x35,0x3B,0x42,0x48,0x4F,0x56,0x5C,0x63};

		public PerformanceSender(Patch p, int param)
		{
			patch = p;

			if (patch.getDevice().getModelName() == "DX7") {
				parameter = getDX7Parameter(param);
				b[3]=(byte)0x08;
			} else {
				parameter = param;
				b[3]=(byte)0x04;
			}

			b[0]=(byte)0xF0;
			b[1]=(byte)0x43;
			b[4]=(byte)parameter;
			b[6]=(byte)0xF7;
		}

		public byte [] generate (int value)
		{
			b[2]=(byte)(0x10+channel-1);
			b[5]=(byte)value;		
		
			if (patch.getDevice().getModelName() == "DX7") 
				if ( parameter==0x46 || parameter==0x48 || parameter==0x4A || parameter==0x4C) 
					b[5]=(byte)TX2DXsens[value];

			if (b[4] == (byte)(-1)) return null;
			else  return b;
		}

		private int getDX7Parameter(int p)
		{
			if	(p ==  2) {return 64;}	// Mono/Poly
			else if (p ==  3) {return 65;}	// Pitch Bend Range
			else if (p ==  4) {return 66;}	// Pitch Bend Step
			else if (p ==  5) {return 69;}	// Portamento Time
			else if (p ==  6) {return 68;}	// Portamento Glissando
			else if (p ==  7) {return 67;}	// Portamento Mode
			else if (p ==  9) {return 70;}	// Modulation Wheel Sensitivity
			else if (p == 10) {return 71;}	// Modulation Wheel Assign
			else if (p == 11) {return 72;}	// Foot Control Sensitivity
			else if (p == 12) {return 73;}	// Foot Control Assign
			else if (p == 13) {return 76;}	// Aftertouch Sensitivity
			else if (p == 14) {return 77;}	// Aftertouch Assign
			else if (p == 15) {return 74;}	// Breath Control Sensitivity
			else if (p == 16) {return 75;}	// Breath Control Assign
			else { return -1;}		// not supported performance parameter!
		}
	}

}
