/*
 * @version $Id: NovationXioSynthSingleEditor.java,v 1.10 2008/12/16 $
 */
package synthdrivers.NovationXioSynth;
import core.*;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;
import javax.swing.event.*;

class NXSParamModel extends ParamModel 
{
    private int bitmask;
    private int mult;

    /* use bitmasks since some bytes in the sysex are shared 
       among different parameters */
    
    public NXSParamModel (Patch p, int offset, int bitMask) {
	super (p, offset);
	
	this.bitmask = bitMask;
	if ((bitmask & 1) == 1)
	    mult = 1;
	else if ((bitmask & 2) == 2)
	    mult = 2;
	else if ((bitmask & 4) == 4)
	    mult = 4;
	else if ((bitmask & 8) == 8)
	    mult = 8;
	else if ((bitmask & 16) == 16)
	    mult = 16;
	else if ((bitmask & 32) == 32)
	    mult = 32;
	else if ((bitmask & 64) == 64)
	    mult = 64;
	else if ((bitmask & 128) == 128)
	    mult = 128;
    }

    public void set(int i) {
	patch.sysex[ofs] = (byte) ((i * mult) + (patch.sysex[ofs] & (~bitmask)) );
    }
    
    public int get() {
	return ((patch.sysex[ofs] & bitmask) / mult);
    }
}


/**
 * CC sender
 * TODO : implement midi channels !!
 */

class NXSSender extends SysexSender
{
    int parameter;
    /* type can be : */
    /* 0 : normal CC */
    /* 1 : NRPN CC */
    /* 2 : trick NRPN : 104 / 105 / 106 --> cf xioSynth midi implem */
    int type = 0;

    int offset = 0;

    private byte[] b = {
	(byte)0xF0, 
	(byte)0xB0, 
	(byte)0x00,
	(byte)0x00,
	(byte)0xF7
    };

    public NXSSender(int param) {
	b[2] = (byte)param;
	this.type = 0;
	this.parameter = param;
    }

    /* second constructor used for NRPN values */
    public NXSSender(int nrpn, int param) {
	this.type = 1;
	b = new byte [] {
	    (byte)(0xF0),
	    (byte)(0xB0),
	    (byte)(0x63),  /*NRPN MSB*/
	    (byte)(0x00),  /*NRPN MSB = 0*/
	    (byte)(0xB0),
	    (byte)(0x62),  /* NRPN LSB */
	    (byte)(param), /* NRPN LSB value */
	    (byte)(0xB0),
	    (byte)(0x06),  /* this is the value sent by xio, use it..*/
	    (byte)(0x00),  /* will be set by value */
	    (byte)(0xF7),  /* terminator */
	};
	this.parameter = param;
    }

    /* third constructor : param is NRPN value and offset is the
       selected value (cf xio specifications ) (CC104 / 105 / 106)
    */

    public NXSSender(int nrpn, int param, int offset) {
	this.type   = 2;
	this.offset = offset;
	b = new byte [] {
	    (byte)(0xF0),
	    (byte)(0xB0),
	    (byte)(0x63),  /*NRPN MSB*/
	    (byte)(0x00),  /*NRPN MSB = 0*/
	    (byte)(0xB0),
	    (byte)(0x62),  /* NRPN LSB */
	    (byte)(param), /* NRPN LSB value */
	    (byte)(0xB0),
	    (byte)(0x06),  /* this is the value sent by xio, use it..*/
	    (byte)(0x00),  /* will be set by value */
	    (byte)(0xF7),  /* terminator */
	};
	this.parameter = param;
    }

    public byte [] generate (int value) {

	if (parameter == 0) {
	    System.out.println("Not implemented CC !!\n");
	}
	     
	if (type == 0) {
	    b[3] = (byte)(value);
	}
	else if (type == 1) {
	    b[9] = (byte)(value);
	}
	else if (type == 2) {
	    b[9] = (byte)(value + offset);
	}
	return b;
    }
}


class NovationXioSynthSingleEditor extends PatchEditorFrame
{
    ImageIcon algoIcon[]=new ImageIcon[8];

    public NovationXioSynthSingleEditor(Patch patch)
    {
	super ("Novation XioSynth Single Editor" + " - " + patch.getName(),patch);

	int i = 0;
	int j = 32;

	JPanel tmpPane;
	final Patch p = patch;

	String[] oscShapeStrings = new String [] {
	    "SINUS",
	    "TRI",
	    "SAW",
	    "SQU",
	    
	    "WhiteNoise",
	    "HiPassNoise",
	    "BandPassNoise",
	    "BandHiPassNoise",
	    
	    "ORGAN",
	    "HARPSI",
	    "ELEC PIANO",
	    "SLAP BASS",
	    "RHODES",
	    "RHODES TINE",
	    "WHURLY",
	    "CLAVINET",
	    "ANA BASS"};
	
	String[] lfoShapeStrings = new String[] {
	    "SINUS",
	    "TRI",
	    "SAW",
	    "SQUARE",
	    "RAND S/H",
	    "QUANT S/H",
	    "CROSSFADE",
	    "EXP DEC1",
	    "EXP DEC2",
	    "EXP DEC3",
	    "ATT EXP1",
	    "ATT EXP2",
	    "ATT EXP3",
	    "SUST EXP1",
	    "SUST EXP2",
	    "SUST EXP3",
	    "PIANO ENV1",
	    "PIANO ENV2",
	    "PIANO ENV3",
	    "EXP UPDOWN",
	    "CHROMATIC",
	    "MAJ MODES",
	    "MAJ 7",
	    "PATTERN1",
	    "PATTERN2",
	    "PATTERN3",
	    "PATTERN4",
	    "PATTERN5",
	    "PATTERN6",
	    "PATTERN7",
	    "PATTERN8",
	    "PATTERN9",
	};
    
	int [] oscShapeOffsets	      = new int[] {0x8D, 0x8E, 0x8F};
	int [] oscLevelOffsets	      = new int[] {0x32, 0x33, 0x34};
	int [] oscDetuneOffsets	      = new int[] {0x15, 0x1D, 0x25};
	int [] oscSemitoneOffsets     = new int[] {0x14, 0x1C, 0x24};
	int [] oscEnvToPitchOffsets   = new int[] {0x18, 0x20, 0x28};
	int [] oscOctaveOffsets	      = new int[] {0x13, 0X13, 0x13};
	int [] oscOctaveBitmasks      = new int[] {0x03, 0x0C, 0x30};
	int [] oscPwPosOffsets	      = new int[] {0x19, 0x21, 0x29};
	int [] oscLfo2ToPwOffsets     = new int[] {0x1A, 0x22, 0x2A};
	int [] oscPwOsc1ModEnvOffsets = new int[] {0x1B, 0x23, 0x2B};

	int [] oscShapeCCs	      = new int[]   {50, 51, 52};
	int [] oscLevelCCs	      = new int[]   {72, 73, 74};
	int [] oscDetuneCCs	      = new int[]   {41, 49, 57};
	int [] oscSemitoneCCs	      = new int[]   {40, 48, 56};
	int [] oscEnvToPitchCCs	      = new int[]   {44, 52, 60};
	int [][] oscOctaveCCs	      = new int[][] {{105, 16},
						     {105, 20},
						     {105, 24}};
	int [] oscPwPosCCs	      = new int[] {45, 53, 61};
	int [] oscLfo2ToPwCCs	      = new int[] {46, 54, 62};
	int [] oscPwOsc1ModEnvCCs     = new int[] {47, 55, 63};


	int [] lfoShapeOffsets = new int[] {0x90, 0x91};
	int [] lfoSpeedOffsets = new int[] {0x55, 0x58};
	int [] lfoDelayOffsets = new int[] {0x57, 0x5A};
	int [] lfoVelocityOffsets = new int[] {0xB6, 0xB7};
	int [] lfoMonoDelTrigOffsets = new int[] {0x5B, 0x5B};
	int [] lfoMonoDelTrigBitmasks = new int[] {0x01, 0x02};
	int [] lfoKeySyncOffsets = new int[] {0x5C, 0x5C};
	int [] lfoKeySyncBitmasks = new int[] {0x02, 0x10};
	int [] lfoInitPhaseOffsets = new int[] {0x94, 0x95};
	int [] lfoUnipolarOffsets = new int[] {0x92, 0x93};
	int [] lfoUnipolarBitmasks = new int[] {0x01, 0x01};
	int [] lfoCommonOffsets = new int[] {0x5C, 0x5C};
	int [] lfoCommonBitmasks = new int[] {0x04, 0x20};
	int [] lfoOneShotOffsets = new int[] {0x5C, 0x5C};
	int [] lfoOneShotBitmasks = new int[] {0x01, 0x08};

	int [] lfoShapeCCs = new int[] {53, 54};
	int [] lfoSpeedCCs = new int[] {80, 83};
	int [] lfoDelayCCs = new int[] {82, 85};
	int [] lfoVelocityCCs = new int[] {75, 76};
	int [][] lfoMonoDelTrigCCs = new int[][] {{104, 12},
						  {104, 14}};
	int [][] lfoKeySyncCCs = new int [][] {{104, 18},
					       {104, 24}};
	int [] lfoInitPhaseCCs = new int [] {57, 58};
	int [] lfoUnipolarCCs = new int[] {55, 56};
	int [][] lfoCommonCCs = new int[][] {{104, 20},
					     {104, 26}};
	int [][] lfoOneShotCCs	= new int [][] {{104, 16},
						{104, 22}};

	JPanel globalPane = new JPanel();
	globalPane.setLayout(new GridBagLayout());

	JPanel oscillatorsPane = new JPanel();
	oscillatorsPane.setLayout(new GridBagLayout());
	oscillatorsPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
					   "Oscillators", TitledBorder.CENTER, TitledBorder.CENTER));

	/* oscillators panel */
	final JTabbedPane oscPane = new JTabbedPane();
	ScrollBarWidget kw;

	addWidget(globalPane,
		  new PatchNameWidget(" Name  ", patch),
		  0, 0, 1, 1, 0);

	for (i = 0; i < 3; i++) {
	    JPanel panel = new JPanel();
	    panel.setLayout(new GridBagLayout());

	    addWidget(panel,
		      new ComboBoxWidget("Shape",
					 patch,
					 new NXSParamModel(patch, oscShapeOffsets[i], 0xFF),
					 new NXSSender(1, oscShapeCCs[i]),
					 oscShapeStrings), 
		      0, 0, 1, 1, j);
	    j++;
					   
	    kw = new ScrollBarWidget("level", patch, 0, 127, 0,
				     new NXSParamModel(patch, oscLevelOffsets[i], 0xFF),
				     new NXSSender(oscLevelCCs[i]));
	    addWidget(panel, kw,  0, 1, 1, 1, j);
	    j++;
	    
	    kw = new ScrollBarWidget("detune", patch, 14, 114, -64,
				     new NXSParamModel(patch, oscDetuneOffsets[i], 0xFF),
				     new NXSSender(oscDetuneCCs[i]));
	    addWidget(panel, kw,  0, 2, 1, 1, j);
	    j++;
	    
	    kw = new ScrollBarWidget("semitone", patch, 52, 76, -64,
				     new NXSParamModel(patch, oscSemitoneOffsets[i], 0xFF),
				     new NXSSender(oscSemitoneCCs[i]));
	    addWidget(panel, kw,  0, 3, 1, 1, j);
	    j++;
	    
	    kw = new ScrollBarWidget("ModEnv>pitch", patch, 0, 127, -64,
				     new NXSParamModel(patch, oscEnvToPitchOffsets[i], 0xFF),
				     new NXSSender(oscEnvToPitchCCs[i]));
	    addWidget(panel, kw,  0, 4, 1, 1, j);
	    j++;
	    
	    kw = new ScrollBarWidget("octave", patch, 0, 3, -1,
				     new NXSParamModel(patch, oscOctaveOffsets[i], oscOctaveBitmasks[i]),
				     new NXSSender(2, oscOctaveCCs[i][0], oscOctaveCCs[i][1]) );
	    addWidget(panel, kw,  0, 5, 1, 1, j);
	    j++;

	    /* PULSE WIDTH PANEL */
	    JPanel pwPanel = new JPanel();
	    pwPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
					       "Pulse Width", TitledBorder.CENTER, TitledBorder.CENTER));
	    pwPanel.setLayout(new GridBagLayout());
	    
	    ScrollBarWidget sbw = new ScrollBarWidget("PW pos", patch, 0, 127, -64,
						      new NXSParamModel(patch, oscPwPosOffsets[i], 0xFF),
						      new NXSSender(oscPwPosCCs[i]));
	    addWidget(pwPanel, sbw,  0, 0, 1, 1, j);
	    j++;
	    
	    kw = new ScrollBarWidget("PW LFO2>PW", patch, 0, 127, -64,
				     new NXSParamModel(patch, oscLfo2ToPwOffsets[i], 0xFF),
				     new NXSSender(oscLfo2ToPwCCs[i]));
	    addWidget(pwPanel, kw,  0, 1, 1, 1, j);
	    j++;
	    
	    kw = new ScrollBarWidget("PW OSC1 ModEnv>", patch, 0, 127, -64,
				     new NXSParamModel(patch, oscPwOsc1ModEnvOffsets[i], 0xFF),
				     new NXSSender(oscPwOsc1ModEnvCCs[i]));
	    addWidget(pwPanel, kw,  0, 2, 1, 1, j);
	    j++;
	    
	    gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 1; gbc.gridheight = 3;
	    panel.add(pwPanel, gbc);

	    /* END PULSE WIDTH PANEL */
	    oscPane.addTab("Osc " + (i + 1), panel);

	    /* add a listener to send midi messages for osc selectors */
	    oscPane.addChangeListener(new ChangeListener() {
		    public void stateChanged(ChangeEvent e) {

			/* select one of the oscs */
			if (oscPane.getSelectedIndex() <= 2) {
			    byte [] b = new byte [] {
				(byte)(0xF0),
				(byte)(0xB0),
				(byte)(0x63),  /*NRPN MSB*/
				(byte)(0x00),  /*NRPN MSB = 0*/
				(byte)(0xB0),
				(byte)(0x62),  /* NRPN LSB */
				(byte)(105), /* NRPN LSB value */
				(byte)(0xB0),
				(byte)(0x06),  /* this is the value sent by xio, use it..*/
				(byte)(56 + oscPane.getSelectedIndex()),  /* will be set by value */
				(byte)(0xF7),  /* terminator */
			    };

			    NovationXioSynthSingleDriver d = (NovationXioSynthSingleDriver)p.getDriver();
			    d.send(b);
			}
			/* select one of the lfos */
			else if (oscPane.getSelectedIndex() > 2) {
			    byte [] b = new byte [] {
				(byte)(0xF0),
				(byte)(0xB0),
				(byte)(0x63),  /*NRPN MSB*/
				(byte)(0x00),  /*NRPN MSB = 0*/
				(byte)(0xB0),
				(byte)(0x62),  /* NRPN LSB */
				(byte)(104), /* NRPN LSB value */
				(byte)(0xB0),
				(byte)(0x06),  /* this is the value sent by xio, use it..*/
				(byte)(42 + oscPane.getSelectedIndex()-3),  /* will be set by value */
				(byte)(0xF7),  /* terminator */
			    };

			    NovationXioSynthSingleDriver d = (NovationXioSynthSingleDriver)p.getDriver();
			    d.send(b);
			}
		    }
		});
	}
	gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1; gbc.gridheight = 6;
	oscillatorsPane.add(oscPane, gbc);
	
	kw = new ScrollBarWidget("Portamento", patch, 0, 127, 0,
				 new NXSParamModel(patch, 0x10, 0xFF),
				new NXSSender(05));
	addWidget(oscillatorsPane, kw, 0, 8, 1, 1, j);
	j++;

	tmpPane = new JPanel();
	tmpPane.setLayout(new GridBagLayout());

	addWidget(tmpPane,	   
		  new ComboBoxWidget("Porta Mode", patch,
				     new NXSParamModel(patch, 0x12, 0x01),
				     new NXSSender(70),
				     new String[] {"EXP", "LIN"}),
		  0, 0, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, j);
	j++;

	addWidget(tmpPane,	   
		  new ComboBoxWidget("PolyMode", patch,
				     new NXSParamModel(patch, 0x0D, 0x18),
				     new NXSSender(2, 105, 0),
				     new String[] {"Mono", "Mono AG", "Poly 1", "Poly 2"}),
		  1, 0, 1, 1,  GridBagConstraints.WEST, GridBagConstraints.NONE, j);
	j++;

	addWidget(tmpPane,	   
		  new ComboBoxWidget("Unisson", patch,
				     new NXSParamModel(patch, 0x0D, 0x07),
				     new NXSSender(2, 106, 0),
				     new String[] {"Off", "2", "3", "4", "5", "6", "7", "8"}),
		  2, 0, 1, 1,  GridBagConstraints.WEST, GridBagConstraints.NONE, j);
	j++;

	gbc.gridx = 0; gbc.gridy = 9; gbc.gridwidth = 1; gbc.gridheight = 1; gbc.anchor = GridBagConstraints.CENTER;
	oscillatorsPane.add(tmpPane, gbc);

	kw = new ScrollBarWidget("Uni Detune", patch, 0, 127, 0,
				 new NXSParamModel(patch, 0x0E, 0xFF),
				new NXSSender(68));
	addWidget(oscillatorsPane, kw, 0, 11, 1, 1, j);
	j++;

	kw = new ScrollBarWidget("VCO Drift", patch, 0, 127, 0,
				 new NXSParamModel(patch, 0x0F, 0xFF),
				new NXSSender(69));
	addWidget(oscillatorsPane, kw, 0, 12, 1, 1, j);
	j++;

	kw = new ScrollBarWidget("Preglide", patch, 52, 76, -64,
				 new NXSParamModel(patch, 0x11, 0xFF),
				new NXSSender(8));
	addWidget(oscillatorsPane, kw, 0, 13, 1, 1, j);
	j++;

	kw = new ScrollBarWidget("Start phase", patch, 0, 127, 0,
				 new NXSParamModel(patch, 0xA0, 0xFF),
				 new NXSSender(1, 69));
	addWidget(oscillatorsPane, kw, 0, 15, 1, 1, j);
	j++;

	addWidget(oscillatorsPane,	   
		  new ComboBoxWidget("OSC 1>2 Sync", patch,
				     new NXSParamModel(patch, 0x13, 0x40),
				     new NXSSender(2, 104, 10),
				     new String[] {"OFF", "ON"}),
		  0, 16, 1, 1, j);
	j++;

	kw = new ScrollBarWidget("OSC 23 FM Level", patch, 0, 127, 0,
				 new NXSParamModel(patch, 0x45, 0xFF),
				 new NXSSender(1, 0));
	addWidget(oscillatorsPane, kw, 0, 17, 1, 1, j);
	j++;

	kw = new ScrollBarWidget("OSC 2>3 FM ADEnv", patch, 0, 127, -64,
				 new NXSParamModel(patch, 0x46, 0xFF),
				 new NXSSender(1, 1));
	addWidget(oscillatorsPane, kw, 0, 18, 1, 1, j);
	j++;

	kw = new ScrollBarWidget("OSC 2>3 FM Lfo1", patch, 0, 127, -64,
				 new NXSParamModel(patch, 0xB9, 0xFF),
				 new NXSSender(1, 78));
	addWidget(oscillatorsPane, kw, 0, 19, 1, 1, j);
	j++;

	kw = new ScrollBarWidget("Fixed Note", patch, 0, 127, 0,
				 new NXSParamModel(patch, 0xB5, 0xFF),
				 new NXSSender(1, 74));
	addWidget(oscillatorsPane, kw, 0, 20, 1, 1, j);
	j++;

	gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1; gbc.gridheight = 6;
	globalPane.add(oscillatorsPane, gbc);

	/**************************/
	/* END OF OSCILLATOR PANE */
	/**************************/

	/********************/
	/* BEGIN MIXER PANE */
	/********************/

	JPanel mixerPane = new JPanel();
	mixerPane.setLayout(new GridBagLayout());
	mixerPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
					   "Mixer", TitledBorder.CENTER, TitledBorder.CENTER));

	kw = new ScrollBarWidget("Mix output", patch, 0, 30, 0,
				 new NXSParamModel(patch, 0x8A, 0x1F),
				new NXSSender(119));
	addWidget(mixerPane, kw, 0, 0, 1, 1, j);
	j++;

	kw = new ScrollBarWidget("Mix noise level", patch, 0, 127, 0,
				 new NXSParamModel(patch, 0x35, 0xFF),
				new NXSSender(75));
	addWidget(mixerPane, kw, 0, 1, 1, 1, j);
	j++;

	kw = new ScrollBarWidget("Mix ring12 level", patch, 0, 127, 0,
				 new NXSParamModel(patch, 0x36, 0xFF),
				new NXSSender(76));
	addWidget(mixerPane, kw, 0, 2, 1, 1, j);
	j++;

	kw = new ScrollBarWidget("Mix lfo1 > osc1", patch, 0, 127, -64,
				 new NXSParamModel(patch, 0x9A, 0xFF),
				 new NXSSender(1, 63));
	addWidget(mixerPane, kw, 0, 3, 1, 1, j);
	j++;

	kw = new ScrollBarWidget("Mix lfo2 > osc1", patch, 0, 127, -64,
				 new NXSParamModel(patch, 0xB8, 0xFF),
				 new NXSSender(1, 77));
	addWidget(mixerPane, kw, 0, 4, 1, 1, j);
	j++;

	kw = new ScrollBarWidget("Mix lfo2 > osc2", patch, 0, 127, -64,
				 new NXSParamModel(patch, 0x9B, 0xFF),
				 new NXSSender(1, 64));
	addWidget(mixerPane, kw, 0, 5, 1, 1, j);
	j++;

	kw = new ScrollBarWidget("A/D Env > osc3", patch, 0, 127, -64,
				 new NXSParamModel(patch, 0x9C, 0xFF),
				 new NXSSender(1, 65));
	addWidget(mixerPane, kw, 0, 6, 1, 1, j);
	j++;

	kw = new ScrollBarWidget("lfo1 > noise", patch, 0, 127, -64,
				 new NXSParamModel(patch, 0x9D, 0xFF),
				 new NXSSender(1, 66));
	addWidget(mixerPane, kw, 0, 7, 1, 1, j);
	j++;

	kw = new ScrollBarWidget("lfo1 > ring12", patch, 0, 127, -64,
				 new NXSParamModel(patch, 0x9E, 0xFF),
				 new NXSSender(1, 67));
	addWidget(mixerPane, kw, 0, 8, 1, 1, j);
	j++;

	addWidget(mixerPane,	     
		  new ComboBoxWidget("Noise type", patch,
				     new NXSParamModel(patch, 0xA1, 0x03),
				     new NXSSender(1, 70),
				     new String[] {"White", "HP", "BP", "HP*BP"}),
		  0, 9, 1, 1, j);
	j++;

	gbc.gridx = 1; gbc.gridy = 2; gbc.gridwidth = 1; gbc.gridheight = 1;
	globalPane.add(mixerPane, gbc);

	/**************************/
	/* END OF MIXER PANE */
	/**************************/

	/********************/
	/* BEGIN FILTER PANE */
	/********************/

	JPanel filterPane = new JPanel();
	filterPane.setLayout(new GridBagLayout());
	filterPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
					   "Filter", TitledBorder.CENTER, TitledBorder.CENTER));

	kw = new ScrollBarWidget("Freq", patch, 0, 127, 0,
				 new NXSParamModel(patch, 0x3B, 0xFF),
				new NXSSender(105));
	addWidget(filterPane, kw, 0, 0, 1, 1, j);
	j++;

	kw = new ScrollBarWidget("Kb tracking", patch, 0, 127, 0,
				 new NXSParamModel(patch, 0x3C, 0xFF),
				 new NXSSender(1, 11));
	addWidget(filterPane, kw, 0, 1, 1, 1, j);
	j++;

	kw = new ScrollBarWidget("Resonance", patch, 0, 127, 0,
				 new NXSParamModel(patch, 0x39, 0xFF),
				new NXSSender(106));
	addWidget(filterPane, kw, 0, 2, 1, 1, j);
	j++;

	kw = new ScrollBarWidget("Overdrive", patch, 0, 127, 0,
				 new NXSParamModel(patch, 0x38, 0xFF),
				new NXSSender(104));
	addWidget(filterPane, kw, 0, 3, 1, 1, j);
	j++;

	kw = new ScrollBarWidget("Mod env depth", patch, 0, 127, -64,
				 new NXSParamModel(patch, 0x41, 0xFF),
				new NXSSender(107));
	addWidget(filterPane, kw, 0, 4, 1, 1, j);
	j++;
	
	kw = new ScrollBarWidget("Lfo2 depth", patch, 0, 127, -64,
				 new NXSParamModel(patch, 0x40, 0xFF),
				new NXSSender(102));
	addWidget(filterPane, kw, 0, 5, 1, 1, j);
	j++;

	kw = new ScrollBarWidget("Shape", patch, 0, 127, 0,
				 new NXSParamModel(patch, 0xEF, 0xFF),
				 new NXSSender(1, 36));
	addWidget(filterPane, kw, 0, 6, 1, 1, j);
	j++;

	kw = new ScrollBarWidget("Velocity", patch, 0, 127, 0,
				 new NXSParamModel(patch, 0xA3, 0xFF),
				 new NXSSender(1, 72));
	addWidget(filterPane, kw, 0, 7, 1, 1, j);
	j++;

	kw = new ScrollBarWidget("Q-Norm", patch, 0, 127, 0,
				 new NXSParamModel(patch, 0x3A, 0xFF),
				new NXSSender(103));
	addWidget(filterPane, kw, 0, 8, 1, 1, j);
	j++;

	addWidget(filterPane,	      
		  new ComboBoxWidget("Att", patch,
				     new NXSParamModel(patch, 0x0D, 0x20),
				     new NXSSender(2, 104, 6),
				     new String[] {"12dB/oct", "24dB/oct"}),
		  0, 9, 1, 1, j);
	j++;

	addWidget(filterPane,	      
		  new ComboBoxWidget("Type", patch,
				     new NXSParamModel(patch, 0x99, 0x03),
				     new NXSSender(1, 62),
				     new String[] {"Low-Pass", "BandPass", "HiPass"}),
		  0, 10, 1, 1, j);
	j++;

	gbc.gridx = 1; gbc.gridy = 1; gbc.gridwidth = 1; gbc.gridheight = 1;
	globalPane.add(filterPane, gbc);

	/**********************************/
	/* END OF FILTER PANE		  */
	/**********************************/

	/**********************************/
	/* LFOs PANE			  */
	/**********************************/

	JPanel lfosPane = new JPanel();
	lfosPane.setLayout(new GridBagLayout());
	lfosPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
					   "LFOs", TitledBorder.CENTER, TitledBorder.CENTER));

	/* lfos panel */

	for (i = 0; i < 2; i++) {
	    JPanel panel = new JPanel();
	    panel.setLayout(new GridBagLayout());

	    addWidget(panel,
		      new ComboBoxWidget("Shape",
					 patch,
					 new NXSParamModel(patch, lfoShapeOffsets[i], 0xFF),
					 new NXSSender(1, lfoShapeCCs[i]),
					 lfoShapeStrings), 
		      0, 0, 1, 1, j);
	    j++;

	    kw = new ScrollBarWidget("Speed", patch, 0, 127, 0,
				     new NXSParamModel(patch, lfoSpeedOffsets[i], 0xFF),
				     new NXSSender(lfoSpeedCCs[i]));
	    addWidget(panel, kw, 0, 1, 1, 1, j);
	    j++;

	    kw = new ScrollBarWidget("Delay", patch, 0, 127, 0,
				     new NXSParamModel(patch, lfoDelayOffsets[i], 0xFF),
				     new NXSSender(lfoDelayCCs[i]));
	    addWidget(panel, kw, 0, 2, 1, 1, j);
	    j++;

	    kw = new ScrollBarWidget("Velocity", patch, 0, 127, -64,
				     new NXSParamModel(patch, lfoVelocityOffsets[i], 0xFF),
				     new NXSSender(1, lfoVelocityCCs[i]));
	    addWidget(panel, kw, 0, 3, 1, 1, j);
	    j++;

	    kw = new ScrollBarWidget("InitPhase", patch, 0, 127, 0,
				     new NXSParamModel(patch, lfoInitPhaseOffsets[i], 0xFF),
				     new NXSSender(1, lfoInitPhaseCCs[i]));
	    addWidget(panel, kw, 0, 4, 1, 1, j);
	    j++;

	    tmpPane = new JPanel();
	    tmpPane.setLayout(new GridBagLayout());

	    addWidget(tmpPane,
		      new ComboBoxWidget("MonoDelTrig",
					 patch,
					 new NXSParamModel(patch, lfoMonoDelTrigOffsets[i],lfoMonoDelTrigBitmasks[i]),
					 new NXSSender(2, lfoMonoDelTrigCCs[i][0], lfoMonoDelTrigCCs[i][1]),
					 new String[] {"SGL", "MULT"}), 
		      0, 0, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, j);
	    j++;

	    addWidget(tmpPane,
		      new ComboBoxWidget("KeySync",
					 patch,
					 new NXSParamModel(patch, lfoKeySyncOffsets[i],lfoKeySyncBitmasks[i]),
					 new NXSSender(2, lfoKeySyncCCs[i][0], lfoKeySyncCCs[i][1]),
					 new String[] {"OFF", "ON"}), 
		      1, 0, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, j);
	    j++;

	    addWidget(tmpPane,
		      new ComboBoxWidget("Unipolar",
					 patch,
					 new NXSParamModel(patch, lfoUnipolarOffsets[i],lfoUnipolarBitmasks[i]),
					 new NXSSender(1, lfoUnipolarCCs[i]),
					 new String[] {"OFF", "ON"}), 
		      2, 0, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, j);
	    j++;

	    gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 1; gbc.gridheight = 1; gbc.anchor = GridBagConstraints.CENTER;
	    panel.add(tmpPane, gbc);

	    tmpPane = new JPanel();
	    tmpPane.setLayout(new GridBagLayout());

	    addWidget(tmpPane,
		      new ComboBoxWidget("Common",
					 patch,
					 new NXSParamModel(patch, lfoCommonOffsets[i],lfoCommonBitmasks[i]),
					 new NXSSender(2, lfoCommonCCs[i][0], lfoCommonCCs[i][1]),
					 new String[] {"OFF", "ON"}), 
		      0, 0, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, j);
	    j++;

	    addWidget(tmpPane,
		      new ComboBoxWidget("OneShot",
					 patch,
					 new NXSParamModel(patch, lfoOneShotOffsets[i],lfoOneShotBitmasks[i]),
					 new NXSSender(2, lfoOneShotCCs[i][0], lfoOneShotCCs[i][1]),
					 new String[] {"OFF", "ON"}), 
		      1, 0, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, j);
	    j++;

	    gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 1; gbc.gridheight = 1; gbc.anchor = GridBagConstraints.CENTER;
	    panel.add(tmpPane, gbc);
	    
	    oscPane.addTab("Lfo " + (i + 1), panel);
	}

	/**********************************/
	/* END OF LFOs PANEL		  */
	/**********************************/

	/**********************************/
	/* ENVELOPES PANEL		  */
	/**********************************/

	JPanel ampEnvPanel = new JPanel();
	ampEnvPanel.setLayout(new GridBagLayout());
	ampEnvPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
					   "Envelopes", TitledBorder.CENTER, TitledBorder.CENTER));


	EnvelopeWidget envWidget = new EnvelopeWidget("Amplitude envelope",
						      patch,
						      new EnvelopeWidget.Node[] {
							  new EnvelopeWidget.Node(0, 0, null, 0, 0, null, 0, false, null, null, null, null),
							  new EnvelopeWidget.Node(0, 127, new NXSParamModel(patch, 0x4B, 0xFF),
										  127, 127, null,
										  0, false, new NXSSender(108), null,
										  "A", ""),
							  new EnvelopeWidget.Node(0, 127, new NXSParamModel(patch, 0x4C, 0xFF),
										  0, 127, new NXSParamModel(patch, 0x4D, 0xFF),
										  0, false, new NXSSender(109), new NXSSender(110),
										  "D", "S"),

							  new EnvelopeWidget.Node(0, 127, new NXSParamModel(patch, 0x4E, 0xFF), 
										  0, 0, null, 0, false, new NXSSender(111), null, 
										  "R", null),
						      },
						      1, 1);

	addWidget(ampEnvPanel,
		  envWidget,
		  0, 0, 2, 1, j);
	j++;

	envWidget = new EnvelopeWidget("Modulation envelope",
						      patch,
						      new EnvelopeWidget.Node[] {
							  new EnvelopeWidget.Node(0, 0, null, 0, 0, null, 0, false, null, null, null, null),
							  new EnvelopeWidget.Node(0, 127, new NXSParamModel(patch, 0x50, 0xFF),
										  127, 127, null,
										  0, false, new NXSSender(114), null,
										  "A", ""),
							  new EnvelopeWidget.Node(0, 127, new NXSParamModel(patch, 0x51, 0xFF),
										  0, 127, new NXSParamModel(patch, 0x52, 0xFF),
										  0, false, new NXSSender(115), new NXSSender(116),
										  "D", "S"),

							  new EnvelopeWidget.Node(0, 127, new NXSParamModel(patch, 0x53, 0xFF), 
										  0, 0, null, 0, false, new NXSSender(117), null, 
										  "R", null),
						      },
						      1, 1);

	
	addWidget(ampEnvPanel,
		  envWidget,
		  0, 1, 1, 1, j);
	j++;

	addWidget(ampEnvPanel,
		  new ComboBoxWidget("AmpEnvTrig",
				     patch,
				     new NXSParamModel(patch, 0x5D, 0x01),
				     new NXSSender(2, 104, 0),
				     new String[] {"SGL", "MULT"}), 
		  0, 2, 1, 1, j);
	j++;

	addWidget(ampEnvPanel,
		  new ComboBoxWidget("ModEnvTrig",
				     patch,
				     new NXSParamModel(patch, 0x5D, 0x02),
				     new NXSSender(2, 104, 2),
				     new String[] {"SGL", "MULT"}), 
		  0, 3, 1, 1, j);
	j++;

	addWidget(ampEnvPanel,
		  new ComboBoxWidget("A/D EnvTrig",
				     patch,
				     new NXSParamModel(patch, 0x5D, 0x04),
				     new NXSSender(2, 104, 4),
				     new String[] {"SGL", "MULT"}), 
		  0, 4, 1, 1, j);
	j++;

	kw = new ScrollBarWidget("Amp velocity", patch, 0, 127, -64,
				 new NXSParamModel(patch, 0x4A, 0xFF),
				 new NXSSender(112));
	addWidget(ampEnvPanel, kw, 0, 5, 1, 1, j);
	j++;

	kw = new ScrollBarWidget("Mod velocity", patch, 0, 127, -64,
				 new NXSParamModel(patch, 0x4F, 0xFF),
				 new NXSSender(118));
	addWidget(ampEnvPanel, kw, 0, 6, 1, 1, j);
	j++;

	kw = new ScrollBarWidget("A/D velocity", patch, 0, 127, -64,
				 new NXSParamModel(patch, 0x47, 0xFF),
				 new NXSSender(1, 2));
	addWidget(ampEnvPanel, kw, 0, 7, 1, 1, j);
	j++;

	kw = new ScrollBarWidget("A/D attack", patch, 0, 127, 0,
				 new NXSParamModel(patch, 0x48, 0xFF),
				 new NXSSender(1, 3));
	addWidget(ampEnvPanel, kw, 0, 8, 1, 1, j);
	j++;

	kw = new ScrollBarWidget("A/D decay", patch, 0, 127, 0,
				 new NXSParamModel(patch, 0x49, 0xFF),
				 new NXSSender(1, 4));
	addWidget(ampEnvPanel, kw, 0, 9, 1, 1, j);
	j++;

	gbc.gridx = 3; gbc.gridy = 1; gbc.gridwidth = 1; gbc.gridheight = 6;
	globalPane.add(ampEnvPanel, gbc);

	/********************************/
	/* END ENVELOPES PANEL		*/
	/********************************/

	/********************************/
	/* PITCH/MOD/PAD		*/
	/********************************/

	JPanel pmpPane = new JPanel();
	pmpPane.setLayout(new GridBagLayout());
	pmpPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
					   "Pitch/Mod/Pad", TitledBorder.CENTER, TitledBorder.CENTER));

	kw = new ScrollBarWidget("BW Osc1 Bend", patch, 52, 76, -64,
				 new NXSParamModel(patch, 0x16, 0xFF),
				 new NXSSender(42));
	addWidget(pmpPane, kw, 0, 0, 1, 1, j);
	j++;
	
	kw = new ScrollBarWidget("BW Osc2 Bend", patch, 52, 76, -64,
				 new NXSParamModel(patch, 0x1E, 0xFF),
				 new NXSSender(50));
	addWidget(pmpPane, kw, 0, 1, 1, 1, j);
	j++;

	kw = new ScrollBarWidget("BW Osc3 Bend", patch, 52, 76, -64,
				 new NXSParamModel(patch, 0x26, 0xFF),
				 new NXSSender(58));
	addWidget(pmpPane, kw, 0, 2, 1, 1, j);
	j++;

	kw = new ScrollBarWidget("MW Pitch Direct", patch, 0, 127, -64,
				 new NXSParamModel(patch, 0x2C, 0xFF),
				 new NXSSender(1, 5));
	addWidget(pmpPane, kw, 0, 3, 1, 1, j);
	j++;

	kw = new ScrollBarWidget("MW Pitch Mod", patch, 0, 127, -64,
				 new NXSParamModel(patch, 0x2F, 0xFF),
				 new NXSSender(1, 8));
	addWidget(pmpPane, kw, 0, 4, 1, 1, j);
	j++;

	kw = new ScrollBarWidget("MW Filter Freq", patch, 0, 127, -64,
				 new NXSParamModel(patch, 0x3D, 0xFF),
				 new NXSSender(1, 12));
	addWidget(pmpPane, kw, 0, 5, 1, 1, j);
	j++;

	kw = new ScrollBarWidget("MW Filter Freq Mod", patch, 0, 127, -64,
				 new NXSParamModel(patch, 0x42, 0xFF),
				 new NXSSender(1, 15));
	addWidget(pmpPane, kw, 0, 6, 1, 1, j);
	j++;

	kw = new ScrollBarWidget("MW Output Level", patch, 0, 127, -64,
				 new NXSParamModel(patch, 0x5E, 0xFF),
				 new NXSSender(1, 18));
	addWidget(pmpPane, kw, 0, 7, 1, 1, j);
	j++;

	kw = new ScrollBarWidget("MW Delay Level", patch, 0, 127, -64,
				 new NXSParamModel(patch, 0x72, 0xFF),
				 new NXSSender(18));
	addWidget(pmpPane, kw, 0, 8, 1, 1, j);
	j++;

	kw = new ScrollBarWidget("MW Reverb Level", patch, 0, 127, -64,
				 new NXSParamModel(patch, 0x79, 0xFF),
				 new NXSSender(24));
	addWidget(pmpPane, kw, 0, 9, 1, 1, j);
	j++;

	kw = new ScrollBarWidget("MW Chorus Level", patch, 0, 127, -64,
				 new NXSParamModel(patch, 0x7C, 0xFF),
				 new NXSSender(26));
	addWidget(pmpPane, kw, 0, 10, 1, 1, j);
	j++;

	kw = new ScrollBarWidget("MW Distort Level", patch, 0, 127, -64,
				 new NXSParamModel(patch, 0x6F, 0xFF),
				 new NXSSender(16));
	addWidget(pmpPane, kw, 0, 11, 1, 1, j);
	j++;

	addWidget(pmpPane,
		  new ComboBoxWidget("Touchpad X Type",
				     patch,
				     new NXSParamModel(patch, 0xEB, 0x03),
				     new NXSSender(1, 108),
				     new String[] {"No spring", "Spring left", "Spring center"}), 
		  0, 12, 1, 1, j);
	j++;

	addWidget(pmpPane,
		  new ComboBoxWidget("Touchpad Y Type",
				     patch,
				     new NXSParamModel(patch, 0xEC, 0x03),
				     new NXSSender(1, 109),
				     new String[] {"No spring", "Spring down", "Spring center"}), 
		  0, 13, 1, 1, j);
	j++;

	addWidget(pmpPane,
		  new ComboBoxWidget("Touchpad X Assign",
				     patch,
				     new NXSParamModel(patch, 0xED, 0x0F),
				     new NXSSender(1, 110),
				     new String[] {"Off", "Bend", "Mod", "AfterTouch", "Breath", "Filter freq", "Filter res", "Lfo1 rate",
						   "Lfo2 rate", "Lfo2>FilterFreq"}), 
		  0, 14, 1, 1, j);
	j++;

	addWidget(pmpPane,
		  new ComboBoxWidget("Touchpad Y Assign",
				     patch,
				     new NXSParamModel(patch, 0xEE, 0x0F),
				     new NXSSender(1, 111),
				     new String[] {"Off", "Bend", "Mod", "AfterTouch", "Breath", "Filter freq", "Filter res", "Lfo1 rate",
						   "Lfo2 rate", "Lfo2>FilterFreq"}), 
		  0, 15, 1, 1, j);
	j++;

	gbc.gridx = 5; gbc.gridy = 1; gbc.gridwidth = 1; gbc.gridheight = 6;
	globalPane.add(pmpPane, gbc);

	/***********************************/
	/* END PITCH MOD PAD		   */
	/***********************************/

	/***********************************/
	/* SYNC MENU			   */
	/***********************************/

	JPanel syncPane = new JPanel();
	syncPane.setLayout(new GridBagLayout());
	syncPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
					   "Sync", TitledBorder.CENTER, TitledBorder.CENTER));
	
	addWidget(syncPane,
		  new ComboBoxWidget("XGate tempo",
				     patch,
				     new NXSParamModel(patch, 0xF1, 0xFF),
				     new NXSSender(1, 40),
				     new String[] {"64th T", "64th", "32nd T", "64th D", "32nd", "16th T", "32nd D", "16th", "8th T", "16th D",
						   "8th", "4th T", "8th D", "4th", "2nd T", "4th D", "2nd", "1bar T", "2nd D", "1bar"}), 
		  0, 0, 1, 1, j);
	j++;

	addWidget(syncPane,
		  new ComboBoxWidget("ARP tempo",
				     patch,
				     new NXSParamModel(patch, 0x62, 0xFF),
				     new NXSSender(87),
				     new String[] {"32nd T", "32nd", "16th T", "16th", "8th T", "16th D",
						   "8th", "4th T", "8th D", "4th", "2nd T", "4th D", "2nd", "1bar T", "2nd D", "1bar"}), 
		  0, 1, 1, 1, j);
	j++;

	addWidget(syncPane,
		  new ComboBoxWidget("lfo1 delay",
				     patch,
				     new NXSParamModel(patch, 0x96, 0xFF),
				     new NXSSender(1, 59),
				     new String[] {"OFF", "32nd T", "32nd","16th T", "16th", "8th T", "16th D", "8th", "4th T", "8th D",
						   "4th", "2nd T", "4th D", "2nd", "1bar T", "2nd D", "1 bar", "2bar T", "1 bar D", "2 bars", 
						   "4bar T", "3 bars", "5bar T", "4 bars", "3bar D", "7bar T", "5 bars", "8bar T", "6 bars", 
						   "7 bars", "5bar D", "8 bars", "9 bars", "7bar D", "12 bars"}), 
		  0, 2, 1, 1, j);
	j++;

	addWidget(syncPane,
		  new ComboBoxWidget("lfo1 speed",
				     patch,
				     new NXSParamModel(patch, 0x56, 0xFF),
				     new NXSSender(81),
				     new String[] {"OFF", "32nd T", "32nd","16th T", "16th", "8th T", "16th D", "8th", "4th T", "8th D",
						   "4th", "2nd T", "4th D", "2nd", "1bar T", "2nd D", "1 bar", "2bar T", "1 bar D", "2 bars", 
						   "4bar T", "3 bars", "5bar T", "4 bars", "3bar D", "7bar T", "5 bars", "8bar T", "6 bars", 
						   "7 bars", "5bar D", "8 bars", "9 bars", "7bar D", "12 bars"}), 
		  0, 3, 1, 1, j);
	j++;

	addWidget(syncPane,
		  new ComboBoxWidget("lfo2 delay",
				     patch,
				     new NXSParamModel(patch, 0x97, 0xFF),
				     new NXSSender(1, 60),
				     new String[] {"OFF", "32nd T", "32nd","16th T", "16th", "8th T", "16th D", "8th", "4th T", "8th D",
						   "4th", "2nd T", "4th D", "2nd", "1bar T", "2nd D", "1 bar", "2bar T", "1 bar D", "2 bars", 
						   "4bar T", "3 bars", "5bar T", "4 bars", "3bar D", "7bar T", "5 bars", "8bar T", "6 bars", 
						   "7 bars", "5bar D", "8 bars", "9 bars", "7bar D", "12 bars"}), 
		  0, 4, 1, 1, j);
	j++;

	addWidget(syncPane,
		  new ComboBoxWidget("lfo2 speed",
				     patch,
				     new NXSParamModel(patch, 0x59, 0xFF),
				     new NXSSender(84),
				     new String[] {"OFF", "32nd T", "32nd","16th T", "16th", "8th T", "16th D", "8th", "4th T", "8th D",
						   "4th", "2nd T", "4th D", "2nd", "1bar T", "2nd D", "1 bar", "2bar T", "1 bar D", "2 bars", 
						   "4bar T", "3 bars", "5bar T", "4 bars", "3bar D", "7bar T", "5 bars", "8bar T", "6 bars", 
						   "7 bars", "5bar D", "8 bars", "9 bars", "7bar D", "12 bars"}), 
		  0, 5, 1, 1, j);
	j++;

	addWidget(syncPane,
		  new ComboBoxWidget("delay time",
				     patch,
				     new NXSParamModel(patch, 0x74, 0xFF),
				     new NXSSender(20),
				     new String[] {"OFF", "32nd T", "32nd","16th T", "16th", "8th T", "16th D", "8th", "4th T", "8th D",
						   "4th", "2nd T", "4th D", "2nd", "1bar T", "2nd D", "1 bar", "2bar T", "1 bar D", "2 bars"}), 
		  0, 6, 1, 1, j);
	j++;

	addWidget(syncPane,
		  new ComboBoxWidget("chorus rate",
				     patch,
				     new NXSParamModel(patch, 0x7E, 0xFF),
				     new NXSSender(28),
				     new String[] {"OFF", "32nd T", "32nd","16th T", "16th", "8th T", "16th D", "8th", "4th T", "8th D",
						   "4th", "2nd T", "4th D", "2nd", "1bar T", "2nd D", "1 bar", "2bar T", "1 bar D", "2 bars", 
						   "4bar T", "3 bars", "5bar T", "4 bars", "3bar D", "7bar T", "5 bars", "8bar T", "6 bars", 
						   "7 bars", "5bar D", "8 bars", "9 bars", "7bar D", "12 bars"}), 
		  0, 7, 1, 1, j);
	j++;

	addWidget(syncPane,
		  new ComboBoxWidget("pan rate",
				     patch,
				     new NXSParamModel(patch, 0x84, 0xFF),
				     new NXSSender(13),
				     new String[] {"OFF", "32nd T", "32nd","16th T", "16th", "8th T", "16th D", "8th", "4th T", "8th D",
						   "4th", "2nd T", "4th D", "2nd", "1bar T", "2nd D", "1 bar", "2bar T", "1 bar D", "2 bars", 
						   "4bar T", "3 bars", "5bar T", "4 bars", "3bar D", "7bar T", "5 bars", "8bar T", "6 bars", 
						   "7 bars", "5bar D", "8 bars", "9 bars", "7bar D", "12 bars"}), 
		  0, 8, 1, 1, j);
	j++;



	gbc.gridx = 6; gbc.gridy = 1; gbc.gridwidth = 1; gbc.gridheight = 1;
	globalPane.add(syncPane, gbc);

	/*******************************/
	/* END SYNC MENU	       */
	/*******************************/

	/*******************************/
	/* AFTERTOUCH / BREATH MENU    */
	/*******************************/

	JPanel atPane = new JPanel();
	atPane.setLayout(new GridBagLayout());
	atPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
					   "Aftertouch / Breath", TitledBorder.CENTER, TitledBorder.CENTER));

	kw = new ScrollBarWidget("AT pitch direct", patch, 0, 127, -64,
				 new NXSParamModel(patch, 0x2D, 0xFF),
				 new NXSSender(1, 6));
	addWidget(atPane, kw, 0, 0, 1, 1, j);
	j++;

	kw = new ScrollBarWidget("AT pitch mode", patch, 0, 127, -64,
				 new NXSParamModel(patch, 0x30, 0xFF),
				 new NXSSender(1, 9));
	addWidget(atPane, kw, 0, 1, 1, 1, j);
	j++;

	kw = new ScrollBarWidget("AT filter freq", patch, 0, 127, -64,
				 new NXSParamModel(patch, 0x3E, 0xFF),
				 new NXSSender(1, 13));
	addWidget(atPane, kw, 0, 2, 1, 1, j);
	j++;

	kw = new ScrollBarWidget("AT filter freq mode", patch, 0, 127, -64,
				 new NXSParamModel(patch, 0x43, 0xFF),
				 new NXSSender(1, 16));
	addWidget(atPane, kw, 0, 3, 1, 1, j);
	j++;

	kw = new ScrollBarWidget("AT output level", patch, 0, 127, -64,
				 new NXSParamModel(patch, 0x5F, 0xFF),
				 new NXSSender(1, 19));
	addWidget(atPane, kw, 0, 4, 1, 1, j);
	j++;

	kw = new ScrollBarWidget("BR pitch direct", patch, 0, 127, -64,
				 new NXSParamModel(patch, 0x2E, 0xFF),
				 new NXSSender(1, 7));
	addWidget(atPane, kw, 0, 5, 1, 1, j);
	j++;

	kw = new ScrollBarWidget("BR pitch mode", patch, 0, 127, -64,
				 new NXSParamModel(patch, 0x31, 0xFF),
				 new NXSSender(1, 10));
	addWidget(atPane, kw, 0, 6, 1, 1, j);
	j++;

	kw = new ScrollBarWidget("BR filter freq", patch, 0, 127, -64,
				 new NXSParamModel(patch, 0x3F, 0xFF),
				 new NXSSender(1, 14));
	addWidget(atPane, kw, 0, 7, 1, 1, j);
	j++;

	kw = new ScrollBarWidget("BR filter freq mode", patch, 0, 127, -64,
				 new NXSParamModel(patch, 0x44, 0xFF),
				 new NXSSender(1, 17));
	addWidget(atPane, kw, 0, 8, 1, 1, j);
	j++;

	kw = new ScrollBarWidget("BR output level", patch, 0, 127, -64,
				 new NXSParamModel(patch, 0x60, 0xFF),
				 new NXSSender(1, 20));
	addWidget(atPane, kw, 0, 9, 1, 1, j);
	j++;

	gbc.gridx = 6; gbc.gridy = 2; gbc.gridwidth = 1; gbc.gridheight = 1;
	globalPane.add(atPane, gbc);

	/********************************/
	/* END OF AFTERTOUCH / BREATH	*/
	/********************************/

	/********************************/
	/* EFFECTS PANEL		*/
	/********************************/

	JPanel effectsPane = new JPanel();
	
	effectsPane.setLayout(new GridBagLayout());
	effectsPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
					   "Effects", TitledBorder.CENTER, TitledBorder.CENTER));

	kw = new ScrollBarWidget("Delay level", patch, 0, 127, 0,
				 new NXSParamModel(patch, 0x71, 0xFF),
				 new NXSSender(92));
	addWidget(effectsPane, kw, 0, 0, 1, 1, j);
	j++;

	kw = new ScrollBarWidget("Delay time", patch, 0, 127, 0,
				 new NXSParamModel(patch, 0x73, 0xFF),
				 new NXSSender(19));
	addWidget(effectsPane, kw, 0, 1, 1, 1, j);
	j++;

	kw = new ScrollBarWidget("Delay feedback", patch, 0, 127, 0,
				 new NXSParamModel(patch, 0x75, 0xFF),
				 new NXSSender(21));
	addWidget(effectsPane, kw, 0, 2, 1, 1, j);
	j++;

	kw = new ScrollBarWidget("Delay stereo width", patch, 0, 127, 0,
				 new NXSParamModel(patch, 0x76, 0xFF),
				 new NXSSender(22));
	addWidget(effectsPane, kw, 0, 3, 1, 1, j);
	j++;

	kw = new ScrollBarWidget("Reverb level", patch, 0, 127, 0,
				 new NXSParamModel(patch, 0x78, 0xFF),
				 new NXSSender(91));
	addWidget(effectsPane, kw, 0, 4, 1, 1, j);
	j++;

	addWidget(effectsPane,
		  new ComboBoxWidget("Reverb type",
				     patch,
				     new NXSParamModel(patch, 0x86, 0x07),
				     new NXSSender(2, 106, 8),
				     new String[] {"Chamber", "S-Room", "L-Room", "S-Hall", "L-Hall", "G-Hall"}), 
		  0, 5, 1, 1, j);
	j++;

	kw = new ScrollBarWidget("Reverb decay time", patch, 0, 127, 0,
				 new NXSParamModel(patch, 0x7A, 0xFF),
				 new NXSSender(25));
	addWidget(effectsPane, kw, 0, 6, 1, 1, j);
	j++;

	kw = new ScrollBarWidget("Chorus level", patch, 0, 127, 0,
				 new NXSParamModel(patch, 0x7B, 0xFF),
				 new NXSSender(93));
	addWidget(effectsPane, kw, 0, 7, 1, 1, j);
	j++;

	addWidget(effectsPane,
		  new ComboBoxWidget("Chorus type",
				     patch,
				     new NXSParamModel(patch, 0x86, 0x08),
				     new NXSSender(2, 104, 34),
				     new String[] {"Chorus", "Phaser"}), 
		  0, 8, 1, 1, j);
	j++;

	kw = new ScrollBarWidget("Chorus mod rate", patch, 0, 127, 0,
				 new NXSParamModel(patch, 0x7D, 0xFF),
				 new NXSSender(27));
	addWidget(effectsPane, kw, 0, 9, 1, 1, j);
	j++;

	kw = new ScrollBarWidget("Chorus mod depth", patch, 0, 127, 0,
				 new NXSParamModel(patch, 0x80, 0xFF),
				 new NXSSender(30));
	addWidget(effectsPane, kw, 0, 10, 1, 1, j);
	j++;

	kw = new ScrollBarWidget("Chorus center pos", patch, 0, 127, -64,
				 new NXSParamModel(patch, 0x81, 0xFF),
				 new NXSSender(31));
	addWidget(effectsPane, kw, 0, 11, 1, 1, j);
	j++;

	kw = new ScrollBarWidget("Chorus feedback", patch, 0, 127, -64,
				 new NXSParamModel(patch, 0x7F, 0xFF),
				 new NXSSender(29));
	addWidget(effectsPane, kw, 0, 12, 1, 1, j);
	j++;

	addWidget(effectsPane,
		  new ComboBoxWidget("Chorus init pos",
				     patch,
				     new NXSParamModel(patch, 0x87, 0x03),
				     new NXSSender(2, 105, 44),
				     new String[] {"Off", "Left", "Middle", "Right"}), 
		  0, 13, 1, 1, j);
	j++;

	kw = new ScrollBarWidget("Disto level", patch, 0, 127, 0,
				 new NXSParamModel(patch, 0x6E, 0xFF),
				 new NXSSender(90));
	addWidget(effectsPane, kw, 0, 14, 1, 1, j);
	j++;

	kw = new ScrollBarWidget("Disto compensate", patch, 0, 127, 0,
				 new NXSParamModel(patch, 0x70, 0xFF),
				 new NXSSender(17));
	addWidget(effectsPane, kw, 0, 15, 1, 1, j);
	j++;

	kw = new ScrollBarWidget("Disto output level", patch, 52, 76, -64,
				 new NXSParamModel(patch, 0xDF, 0xFF),
				 new NXSSender(1, 27));
	addWidget(effectsPane, kw, 0, 16, 1, 1, j);
	j++;

	kw = new ScrollBarWidget("Pan mod depth", patch, 0, 127, 0,
				 new NXSParamModel(patch, 0x85, 0xFF),
				 new NXSSender(94));
	addWidget(effectsPane, kw, 0, 17, 1, 1, j);
	j++;

	kw = new ScrollBarWidget("Pan position", patch, 0, 127, -64,
				 new NXSParamModel(patch, 0x82, 0xFF),
				 new NXSSender(10));
	addWidget(effectsPane, kw, 0, 18, 1, 1, j);
	j++;

	kw = new ScrollBarWidget("Pan mod rate", patch, 0, 127, 0,
				 new NXSParamModel(patch, 0x83, 0xFF),
				 new NXSSender(12));
	addWidget(effectsPane, kw, 0, 19, 1, 1, j);
	j++;

	addWidget(effectsPane,
		  new ComboBoxWidget("Pan init pos",
				     patch,
				     new NXSParamModel(patch, 0x87, 0x0C),
				     new NXSSender(2, 105, 48),
				     new String[] {"Off", "Left", "Middle", "Right"}), 
		  0, 20, 1, 1, j);
	j++;

	kw = new ScrollBarWidget("EQ bass", patch, 0, 127, -64,
				 new NXSParamModel(patch, 0xE0, 0xFF),
				 new NXSSender(1, 29));
	addWidget(effectsPane, kw, 0, 21, 1, 1, j);
	j++;

	kw = new ScrollBarWidget("EQ medium", patch, 0, 127, -64,
				 new NXSParamModel(patch, 0xE2, 0xFF),
				 new NXSSender(1, 30));
	addWidget(effectsPane, kw, 0, 22, 1, 1, j);
	j++;

	kw = new ScrollBarWidget("EQ treble", patch, 0, 127, -64,
				 new NXSParamModel(patch, 0xE1, 0xFF),
				 new NXSSender(1, 31));
	addWidget(effectsPane, kw, 0, 23, 1, 1, j);
	j++;


	gbc.gridx = 8; gbc.gridy = 1; gbc.gridwidth = 1; gbc.gridheight = 6;
	globalPane.add(effectsPane, gbc);

	/********************************/
	/* END OF EFFECTS PANEL		*/
	/********************************/

	scrollPane.add(globalPane);
	
	pack();
    }
}
