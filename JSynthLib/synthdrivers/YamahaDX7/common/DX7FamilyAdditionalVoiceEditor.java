/*
 * JSynthlib -	generic Additional "Voice" Editor for Yamaha DX7 Family
 *		(used by DX7-II, DX7s, TX802)
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
package synthdrivers.YamahaDX7.common;
import core.*;
import java.lang.String.*;
import java.text.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;
import javax.swing.event.*;

public class DX7FamilyAdditionalVoiceEditor extends PatchEditorFrame 
{
	static final String [] OnOffName	  = new String [] {
		"Off",
		"On"
	};

	static final String [] OpScaleModeName	  = new String [] {
		"normal",
		"fractional"
	};

	static final String [] OpAmpModSensName   = new String [] {
		"0","1","2","3","4","5","6","7"
	};

	static final String [] LfoKeyTriggerName  = new String [] {
		"single",
		"multi"
	};

	static final String [] PitchEgRangeName   = new String [] {
		"8 octave",
		"4 octave",
		"1 octave",
		"1/2 octave"
	};

	static final String [] KeyModeName	  = new String [] {
		"Polyphonic",
		"Monophonic",
		"Unison poly",
		"Unison Mono"
	};

	static final String [] PitchBendRangeName = new String [] {
		"Off",				//
		"perfect First (Unison)",	// Prime
		"minor Second",			// kleine Sekunde
		"major Second",			// groﬂe  Sekunde
		"minor Third",			// kleine Terz
		"major Third",			// groﬂe  Terz
		"perfect Fourth",		// Quarte
		"perfect Fifth",		// Quinte bzw. Tritonus 
		"minor Sixth",			// kleine Sexte
		"major Sixth",			// groﬂe  Sexte
		"minor Seventh",		// kleine Septine
		"major Seventh",		// groﬂe  Septine
		"Octave"			// Oktave
	};

	static final String [] PitchBendStepName  = new String [] {
		"Continuous",
		"±1 Octave	- 12 Steps",
		"±1 Octave	-  6 Steps",
		"±1 Octave	-  4 Steps",
		"±1 Octave	-  3 Steps",
		"±1 minor Seventh -  2 Steps",
		"±1 Octave	-  2 Steps",
		"±1 Fifth		-  1 Step ",
		"±1 major Fifth	-  1 Step ",
		"±1 Sixth		-  1 Step ",
		"±1 minor Seventh -  1 Step ",
		"±1 major Seventh -  1 Step ",
		"±1 Octave	-  1 Step "
	};

	static final String [] PitchBendModeName  = new String [] {
		"normal",
		"low",
		"high",
		"key on"
	};

	static final String [] PortamentoModeName = new String [] {
		"Retain/Fingered",
		"Follow/Full Time"
	};

	static final String [] PortamentoStepName = new String [] {
		"smooth",			//
		"perfect First (Unison)",	// Prime
		"minor Second",			// kleine Sekunde
		"major Second",			// groﬂe  Sekunde
		"minor Third",			// kleine Terz
		"major Third",			// groﬂe  Terz
		"perfect Fourth",		// Quarte
		"perfect Fifth",		// Quinte bzw. Tritonus 
		"minor Sixth",			// kleine Sexte
		"major Sixth",			// groﬂe  Sexte
		"minor Seventh",		// kleine Septine
		"major Seventh",		// groﬂe  Septine
		"Octave"			// Oktave
	};



	public DX7FamilyAdditionalVoiceEditor(String name, Patch patch)
	{
		super (name, patch);

		buildEditor(patch);
	}


	protected void buildEditor(Patch patch)
	{

		// Additional Voice settings
		JPanel opPane = new JPanel();
		opPane.setLayout(new GridBagLayout());gbc.weightx=1;

		gbc.gridx=0;gbc.gridy=1;gbc.gridwidth=3;gbc.gridheight=1; opPane.add(new JLabel("Operator Scaling Mode:"),gbc);
		addWidget(opPane,new ComboBoxWidget("OP1",patch,new ParamModel(patch,6+ 5),new AdditionalVoiceSender( 5),OpScaleModeName), 0, 2, 2, 1, 1);
		addWidget(opPane,new ComboBoxWidget("OP2",patch,new ParamModel(patch,6+ 4),new AdditionalVoiceSender( 4),OpScaleModeName), 2, 2, 2, 1, 2);
		addWidget(opPane,new ComboBoxWidget("OP3",patch,new ParamModel(patch,6+ 3),new AdditionalVoiceSender( 3),OpScaleModeName), 4, 2, 2, 1, 3);
		addWidget(opPane,new ComboBoxWidget("OP4",patch,new ParamModel(patch,6+ 2),new AdditionalVoiceSender( 2),OpScaleModeName), 6, 2, 2, 1, 4);
		addWidget(opPane,new ComboBoxWidget("OP5",patch,new ParamModel(patch,6+ 1),new AdditionalVoiceSender( 1),OpScaleModeName), 8, 2, 2, 1, 5);
		addWidget(opPane,new ComboBoxWidget("OP6",patch,new ParamModel(patch,6+ 0),new AdditionalVoiceSender( 0),OpScaleModeName),10, 2, 2, 1, 6);

		gbc.gridx=0;gbc.gridy=4;gbc.gridwidth=1;gbc.gridheight=1;opPane.add(new JLabel(" "),gbc);
		gbc.gridx=0;gbc.gridy=5;gbc.gridwidth=5;gbc.gridheight=1; opPane.add(new JLabel("Operator Amplitude Modulation Sensitivity:"),gbc);
		addWidget(opPane,new ComboBoxWidget("OP1",patch,new ParamModel(patch,6+11),new AdditionalVoiceSender(11),OpAmpModSensName), 0, 6, 2, 1, 7);
		addWidget(opPane,new ComboBoxWidget("OP2",patch,new ParamModel(patch,6+10),new AdditionalVoiceSender(10),OpAmpModSensName), 2, 6, 2, 1, 8);
		addWidget(opPane,new ComboBoxWidget("OP3",patch,new ParamModel(patch,6+ 9),new AdditionalVoiceSender( 9),OpAmpModSensName), 4, 6, 2, 1, 9);
		addWidget(opPane,new ComboBoxWidget("OP4",patch,new ParamModel(patch,6+ 8),new AdditionalVoiceSender( 8),OpAmpModSensName), 6, 6, 2, 1,10);
		addWidget(opPane,new ComboBoxWidget("OP5",patch,new ParamModel(patch,6+ 7),new AdditionalVoiceSender( 7),OpAmpModSensName), 8, 6, 2, 1,11);
		addWidget(opPane,new ComboBoxWidget("OP6",patch,new ParamModel(patch,6+ 6),new AdditionalVoiceSender( 6),OpAmpModSensName),10, 6, 2, 1,12);

		// Additional Voice settings
		JPanel cmnPane = new JPanel();
		cmnPane.setLayout(new GridBagLayout());gbc.weightx=1;

		gbc.gridx=0;gbc.gridy=8;gbc.gridwidth=1;gbc.gridheight=1; cmnPane.add(new JLabel(" "),gbc);
		gbc.gridx=0;gbc.gridy=9;gbc.gridwidth=6;gbc.gridheight=1; cmnPane.add(new JLabel("LFO: "),gbc);
		addWidget(cmnPane,new ComboBoxWidget("LFO Mode",patch,new ParamModel(patch,6+13),new AdditionalVoiceSender(13),LfoKeyTriggerName), 0,10,4,1,12);

		gbc.gridx=0;gbc.gridy=12;gbc.gridwidth=1;gbc.gridheight=1; cmnPane.add(new JLabel(" "),gbc);
		gbc.gridx=0;gbc.gridy=13;gbc.gridwidth=4;gbc.gridheight=1; cmnPane.add(new JLabel("Pitch EG: "),gbc);
		addWidget(cmnPane,new ComboBoxWidget("Octave Range"	,patch,new ParamModel(patch,6+12),new AdditionalVoiceSender(12),PitchEgRangeName)  ,0,14,4,1,15);
		addWidget(cmnPane,new ComboBoxWidget("Velocity Switch"	,patch,new ParamModel(patch,6+14),new AdditionalVoiceSender(14),OnOffName)	   ,4,14,4,1,16);
		addWidget(cmnPane,new ComboBoxWidget("Rate Scaling"	,patch,new ParamModel(patch,6+38),new AdditionalVoiceSender(38),OpAmpModSensName)  ,8,14,4,1,17);

		gbc.gridx=0;gbc.gridy=16;gbc.gridwidth=10;gbc.gridheight=1;cmnPane.add(new JLabel(" "),gbc);
		gbc.gridx=0;gbc.gridy=17;gbc.gridwidth=4;gbc.gridheight=1; cmnPane.add(new JLabel("Key Mode: "),gbc);
		addWidget(cmnPane,new ComboBoxWidget("Assign"		,patch,new ParamModel(patch,6+15),new AdditionalVoiceSender(15),KeyModeName)	   ,0,18,4,1,11);
		addWidget(cmnPane,new ComboBoxWidget("Unison Detune"	,patch,new ParamModel(patch,6+47),new AdditionalVoiceSender(72),OpAmpModSensName)  ,4,18,4,1,13);

		gbc.gridx=0;gbc.gridy=20;gbc.gridwidth=10;gbc.gridheight=1;cmnPane.add(new JLabel(" "),gbc);
		gbc.gridx=0;gbc.gridy=21;gbc.gridwidth=4;gbc.gridheight=1; cmnPane.add(new JLabel("Pitch: "),gbc);
		addWidget(cmnPane,new ComboBoxWidget("Random Pitch"	,patch,new ParamModel(patch,6+19),new AdditionalVoiceSender(19),OpAmpModSensName) , 0,22,4,1,14);

		gbc.gridx=0;gbc.gridy=24;gbc.gridwidth=1;gbc.gridheight=1; cmnPane.add(new JLabel(" "),gbc);
		gbc.gridx=0;gbc.gridy=25;gbc.gridwidth=4;gbc.gridheight=1; cmnPane.add(new JLabel("Pitch Bend: "),gbc);
		addWidget(cmnPane,new ComboBoxWidget("Mode"		,patch,new ParamModel(patch,6+18),new AdditionalVoiceSender(18),PitchBendModeName) ,0,26,4,1,20);
		addWidget(cmnPane,new ComboBoxWidget("Range"		,patch,new ParamModel(patch,6+16),new AdditionalVoiceSender(16),PitchBendRangeName),4,26,4,1,18);
		addWidget(cmnPane,new ComboBoxWidget("Step"		,patch,new ParamModel(patch,6+17),new AdditionalVoiceSender(17),PitchBendStepName) ,8,26,4,1,19);

		gbc.gridx=0;gbc.gridy=28;gbc.gridwidth=1;gbc.gridheight=1; cmnPane.add(new JLabel(" "),gbc);
		gbc.gridx=0;gbc.gridy=29;gbc.gridwidth=4;gbc.gridheight=1; cmnPane.add(new JLabel("Portamento: "),gbc);
		addWidget(cmnPane,new ComboBoxWidget("Mode (Poly/Mono)",patch,new ParamModel(patch,6+20),new AdditionalVoiceSender(20),PortamentoModeName) ,0,30,4,1,21);
		addWidget(cmnPane,new ComboBoxWidget("Step"	       ,patch,new ParamModel(patch,6+21),new AdditionalVoiceSender(21),PortamentoStepName) ,4,30,4,1,22);
		addWidget(cmnPane,new ScrollBarWidget("Time"	       ,patch,0,99,0,new ParamModel(patch,6+22),new AdditionalVoiceSender(22))		   ,8,30,4,1,23);

		// Additional Voice settings
		JPanel voicePane = new JPanel();
		voicePane.setLayout(new GridBagLayout());gbc.weightx=1;

		gbc.gridx=0;gbc.gridy=0;gbc.gridwidth=1;gbc.gridheight=1; voicePane.add(opPane,gbc);
		gbc.gridx=0;gbc.gridy=1;gbc.gridwidth=1;gbc.gridheight=1; voicePane.add(cmnPane,gbc);

		gbc.gridx=0;gbc.gridy=0;gbc.gridwidth=1;gbc.gridheight=1;gbc.weightx=1;
		voicePane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Voice Parameters",TitledBorder.CENTER,TitledBorder.CENTER));
		scrollPane.add(voicePane,gbc);

		// Controller Settings
		JPanel ctrlPane= new JPanel();
		ctrlPane.setLayout(new GridBagLayout());gbc.weightx=1;

		gbc.gridx=0;gbc.gridy=0;gbc.gridwidth=3;gbc.gridheight=1; ctrlPane.add(new JLabel("Modulation Wheel: "),gbc);
		addWidget(ctrlPane,new ScrollBarWidget("Pitch Modulation",patch,0,99,0,new ParamModel(patch,6+23),new AdditionalVoiceSender(23)),0, 1,6,1,31);
		gbc.gridx=6;gbc.gridy=1;gbc.gridwidth=1;gbc.gridheight=1; ctrlPane.add(new JLabel("	"),gbc);
		addWidget(ctrlPane,new ScrollBarWidget("Ampl. Modulation",patch,0,99,0,new ParamModel(patch,6+24),new AdditionalVoiceSender(24)),7, 1,6,1,32);
		addWidget(ctrlPane,new ScrollBarWidget("EG Bias"	 ,patch,0,99,0,new ParamModel(patch,6+25),new AdditionalVoiceSender(25)),0, 2,6,1,33);

		gbc.gridx=0;gbc.gridy=3;gbc.gridwidth=3;gbc.gridheight=1; ctrlPane.add(new JLabel(" "),gbc);
		gbc.gridx=0;gbc.gridy=4;gbc.gridwidth=3;gbc.gridheight=1; ctrlPane.add(new JLabel("Foot Control 1: "),gbc);
		addWidget(ctrlPane,new ComboBoxWidget("use as Continuous Slider 1",patch,new ParamModel(patch,6+48),new AdditionalVoiceSender(73),OnOffName),0, 5,4,1,34);
		addWidget(ctrlPane,new ScrollBarWidget("Pitch Modulation",patch,0,99,0,new ParamModel(patch,6+26),new AdditionalVoiceSender(26)),0, 6,6,1,35);
		gbc.gridx=6;gbc.gridy=6;gbc.gridwidth=1;gbc.gridheight=1; ctrlPane.add(new JLabel("	"),gbc);
		addWidget(ctrlPane,new ScrollBarWidget("Ampl. Modulation",patch,0,99,0,new ParamModel(patch,6+27),new AdditionalVoiceSender(27)),7, 6,6,1,36);
		addWidget(ctrlPane,new ScrollBarWidget("EG Bias"	 ,patch,0,99,0,new ParamModel(patch,6+28),new AdditionalVoiceSender(28)),0, 7,6,1,37);
		gbc.gridx=6;gbc.gridy=7;gbc.gridwidth=1;gbc.gridheight=1; ctrlPane.add(new JLabel("	"),gbc);
		addWidget(ctrlPane,new ScrollBarWidget("Volume"		 ,patch,0,99,0,new ParamModel(patch,6+29),new AdditionalVoiceSender(29)),7, 7,6,1,38);

		gbc.gridx=0;gbc.gridy=8;gbc.gridwidth=3;gbc.gridheight=1; ctrlPane.add(new JLabel(" "),gbc);
		gbc.gridx=0;gbc.gridy=9;gbc.gridwidth=3;gbc.gridheight=1; ctrlPane.add(new JLabel("Foot Control 2: "),gbc);
		addWidget(ctrlPane,new ScrollBarWidget("Pitch Modulation",patch,0,99,0,new ParamModel(patch,6+39),new AdditionalVoiceSender(64)),0,10,6,1,39);
		gbc.gridx=6;gbc.gridy=10;gbc.gridwidth=1;gbc.gridheight=1; ctrlPane.add(new JLabel("	"),gbc);
		addWidget(ctrlPane,new ScrollBarWidget("Ampl. Modulation",patch,0,99,0,new ParamModel(patch,6+40),new AdditionalVoiceSender(65)),7,10,6,1,40);
		addWidget(ctrlPane,new ScrollBarWidget("EG Bias"	 ,patch,0,99,0,new ParamModel(patch,6+41),new AdditionalVoiceSender(66)),0,11,6,1,41);
		gbc.gridx=6;gbc.gridy=11;gbc.gridwidth=1;gbc.gridheight=1; ctrlPane.add(new JLabel("	"),gbc);
		addWidget(ctrlPane,new ScrollBarWidget("Volume"		 ,patch,0,99,0,new ParamModel(patch,6+42),new AdditionalVoiceSender(67)),7,11,6,1,42);

		gbc.gridx=0;gbc.gridy=12;gbc.gridwidth=3;gbc.gridheight=1; ctrlPane.add(new JLabel(" "),gbc);
		gbc.gridx=0;gbc.gridy=13;gbc.gridwidth=3;gbc.gridheight=1; ctrlPane.add(new JLabel("Breath Control: "),gbc);
		addWidget(ctrlPane,new ScrollBarWidget("Pitch Modulation",patch,0,99,0,new ParamModel(patch,6+30),new AdditionalVoiceSender(30)),0,14,6,1,43);
		gbc.gridx=6;gbc.gridy=14;gbc.gridwidth=1;gbc.gridheight=1; ctrlPane.add(new JLabel("	"),gbc);
		addWidget(ctrlPane,new ScrollBarWidget("Ampl. Modulation",patch,0,99,0,new ParamModel(patch,6+31),new AdditionalVoiceSender(31)),7,14,6,1,44);
		addWidget(ctrlPane,new ScrollBarWidget("EG Bias"	 ,patch,0,99,0,new ParamModel(patch,6+32),new AdditionalVoiceSender(32)),0,15,6,1,45);
		gbc.gridx=6;gbc.gridy=15;gbc.gridwidth=1;gbc.gridheight=1; ctrlPane.add(new JLabel("	"),gbc);
		addWidget(ctrlPane,new ScrollBarWidget("Pitch Bias"	,patch,0,100,-50,new ParamModel(patch,6+33),new AdditionalVoiceSender(33)),7,15,6,1,46);

		gbc.gridx=0;gbc.gridy=16;gbc.gridwidth=3;gbc.gridheight=1; ctrlPane.add(new JLabel(" "),gbc);
		gbc.gridx=0;gbc.gridy=17;gbc.gridwidth=3;gbc.gridheight=1; ctrlPane.add(new JLabel("After Touch: "),gbc);
		addWidget(ctrlPane,new ScrollBarWidget("Pitch Modulation",patch,0,99,0,new ParamModel(patch,6+34),new AdditionalVoiceSender(34)),0,18,6,1,47);
		gbc.gridx=6;gbc.gridy=18;gbc.gridwidth=1;gbc.gridheight=1; ctrlPane.add(new JLabel("	"),gbc);
		addWidget(ctrlPane,new ScrollBarWidget("Ampl. Modulation",patch,0,99,0,new ParamModel(patch,6+35),new AdditionalVoiceSender(35)),7,18,6,1,48);
		addWidget(ctrlPane,new ScrollBarWidget("EG Bias"	 ,patch,0,99,0,new ParamModel(patch,6+36),new AdditionalVoiceSender(36)),0,19,6,1,49);
		gbc.gridx=6;gbc.gridy=19;gbc.gridwidth=1;gbc.gridheight=1; ctrlPane.add(new JLabel("	"),gbc);
		addWidget(ctrlPane,new ScrollBarWidget("Pitch Bias"	 ,patch,0,100,-50,new ParamModel(patch,6+37),new AdditionalVoiceSender(37)),7,19,6,1,50);

		gbc.gridx=0;gbc.gridy=20;gbc.gridwidth=3;gbc.gridheight=1; ctrlPane.add(new JLabel(" "),gbc);
		gbc.gridx=0;gbc.gridy=21;gbc.gridwidth=3;gbc.gridheight=1; ctrlPane.add(new JLabel("MIDI IN Control: "),gbc);
		addWidget(ctrlPane,new ScrollBarWidget("Pitch Modulation",patch,0,99,0,new ParamModel(patch,6+43),new AdditionalVoiceSender(68)),0,22,6,1,51);
		gbc.gridx=6;gbc.gridy=22;gbc.gridwidth=1;gbc.gridheight=1; ctrlPane.add(new JLabel("	"),gbc);
		addWidget(ctrlPane,new ScrollBarWidget("Ampl. Modulation",patch,0,99,0,new ParamModel(patch,6+44),new AdditionalVoiceSender(69)),7,22,6,1,52);
		addWidget(ctrlPane,new ScrollBarWidget("EG Bias"	 ,patch,0,99,0,new ParamModel(patch,6+45),new AdditionalVoiceSender(70)),0,23,6,1,53);
		gbc.gridx=6;gbc.gridy=23;gbc.gridwidth=1;gbc.gridheight=1; ctrlPane.add(new JLabel("	"),gbc);
		addWidget(ctrlPane,new ScrollBarWidget("Volume"		 ,patch,0,99,0,new ParamModel(patch,6+46),new AdditionalVoiceSender(71)),7,23,6,1,54);


		gbc.gridx=0;gbc.gridy=1;gbc.gridwidth=1;gbc.gridheight=1;gbc.weightx=1;
		ctrlPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Controllers",TitledBorder.CENTER,TitledBorder.CENTER));
		scrollPane.add(ctrlPane,gbc);
		pack();
		show();
	}


	/*
	 * SysexSender - Additional Voice
	 *		 (g=6; h=0)
	 */
	class AdditionalVoiceSender extends SysexSender
	{
		int parameter;
		byte []b = new byte [7];

		public AdditionalVoiceSender(int p)
		{
			parameter = p;

			b[0]=(byte)0xF0;
			b[1]=(byte)0x43;
			b[3]=(byte)0x18;
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
