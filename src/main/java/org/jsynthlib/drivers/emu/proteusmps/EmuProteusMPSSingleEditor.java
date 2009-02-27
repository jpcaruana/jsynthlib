/*
 * @version $Id$
 */
package org.jsynthlib.drivers.emu.proteusmps;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.jsynthlib.core.CheckBoxWidget;
import org.jsynthlib.core.ComboBoxWidget;
import org.jsynthlib.core.EnvelopeWidget;
import org.jsynthlib.core.ParamModel;
import org.jsynthlib.core.Patch;
import org.jsynthlib.core.PatchEditorFrame;
import org.jsynthlib.core.PatchNameWidget;
import org.jsynthlib.core.ScrollBarLookupWidget;
import org.jsynthlib.core.ScrollBarWidget;
import org.jsynthlib.core.SysexSender;

class EmuProteusMPSSingleEditor extends PatchEditorFrame
{
final String [] realtimeModSource = new String[]  { "Pitch Weel","Midi Controller A","Midi Controller B",
					            "Midi Controller C","Midi Controller D","Mono Pressure",
						    "Polyphonic Pressure","LFO 1","LFO 2","Auxilary Envelope"};
final String [] realtimeModDest = new String[] { "Off","Pitch","Primary Pitch","Secondary Pitch","Volume","Primary Volume",
						 "Secondary Volume","Attack","Primary Attack","Secondary Attack",
			  			 "Decay","Primary Decay","Secondary Decay","Release","Primary Release",
						 "Secondary Release","Crossfade","LFO 1 Amount","LFO 1 Rate",
					         "LFO 2 Amount","LFO 2 Rate","Aux Envelope Amount",
						 "Aux Envelope Attack","Aux Envelope Decay",
						 "Aux Envelope Release"};
final String [] keyModDest = new String [] {	 "Off","Pitch","Primary Pitch","Secondary Pitch","Volume","Primary Volume",
						 "Secondary Volume","Attack","Primary Attack","Secondary Attack",
			  			 "Decay","Primary Decay","Secondary Decay","Release","Primary Release",
						 "Secondary Release","Crossfade","LFO 1 Amount","LFO 1 Rate",
					         "LFO 2 Amount","LFO 2 Rate","Aux Envelope Amount",
						 "Aux Envelope Attack","Aux Envelope Decay",
						 "Aux Envelope Release","Sample Start","Primary Sample Start",
						 "Secondary Sample Start","Pan","Primary Pan","Secondary Pan","Tone",
						 "Primary Tone","Secondary Tone"};


 final String [] noteName = new String [] {"C-2","C#-2","D-2","D#-2","E-2","F-2","F#-2","G-2","G#21","A-2","A#-2","B-2",
	  				    "C-1","C#-1","D-1","D#-1","E-1","F-1","F#-1","G-1","G#-1","A-1","A#-1","B-1",
                                            "C0","C#0","D0","D#0","E0","F0","F#0","G0","G#0","A0","A#0","B0",
                                            "C1","C#1","D1","D#1","E1","F1","F#1","G1","G#1","A1","A#1","B1",
                                            "C2","C#2","D2","D#2","E2","F2","F#2","G2","G#2","A2","A#2","B2",
                                            "C3","C#3","D3","D#3","E3","F3","F#3","G3","G#3","A3","A#3","B3",
                                            "C4","C#4","D4","D#4","E4","F4","F#4","G4","G#4","A4","A#4","B4",
                                            "C5","C#5","D5","D#5","E5","F5","F#5","G5","G#5","A5","A#5","B5",
                                            "C6","C#6","D6","D#6","E6","F6","F#6","G6","G#6","A6","A#6","B6",
                                            "C7","C#7","D7","D#7","E7","F7","F#7","G7","G#7","A7","A#7","B7",
                                            "C8","C#8","D8","D#8","E8","F8","F#8","G8"
  };
  final String [] waveName = new String [] {"0   None","1   Piano","2   Piano Pad","3    Loose Piano","4    Tight Piano",
  "5    Strings","6    Long Strings","7    Slow Strings","8    Dark Strings",
"9    Voices","10   Slow Voices","11   Dark Choir","12   Synth Flute",
"13   Soft Flute","14   Alto Sax","15   Tenor Sax","16   Baritone Sax",
"17   Dark Sax","18   Soft Trumpet","19   Dark Soft Trumpet","20   Hard Trumpet",
"21   Dark Hard Trumpet","22   Horn Falls","23   Trombone 1","24   Trombone 2",
"25   French Horn","26   Brass 1","27   Brass 2","28   Brass 3",
"29   Trumbone / Sax","30   Guitar Mute","31   Electric Guitar","32   Acoustic Guitar",
"33   Rock Bass","34   Stone Bass","35   Flint Bass","36   Funk Slap",
"37   Funk Pop","38   Harmonics","39   Rock / Harmonics","40   Stone / Harmonics",
"41   Nose Bass","42   Bass Synth 1","43   Bass Synth 2","44   Synth Pad",
"45   Medium Envelope Pad","46   Long Envelope Pad","47   Dark Synth","48   Percussive Organ",
"49   Marimba","50   Vibraphone","51   All Percussion (Balanced)","52   All Percussion (Unballanced)",
"53   Standard Percussion 1","54   Standard Percussion 2","55   Standard Percussion 3","56   Kicks",
"57   Snares","58   Toms","59   Cymbals","60   Latin Drums",
"61   Latin Percussion","62   Agogo Bell","63   Wood Block","64   Conga",
"65   Timbale","66   Ride Cymbal","67   Percussion FX 1","68   Percussion FX 2",
"69   Metal","70   Oct 1 (Sine)","71   Oct 2 All","72   Oct 3 All",
"73   Oct 4 All","74   Oct 5 All","75   Oct 6 All","76   Oct 7 All",
"77   Oct 2 Odd 2","78   Oct 3 Odd","79   Oct 4 Odd","80   Oct 5 Odd",
"81   Oct 6 Odd","82   Oct 7 Odd","83   Oct 2 Even","84   Oct 3 Even",
"85   Oct 4 Even","86   Oct 5 Even","87   Oct 6 Even","88   Oct 7 Even",
"89   Low Odds","90   Low Evens","91   Four Octaves","92   Synth Cycle 1",
"93   Synth Cycle 2","94   Synth Cycle 3","95   Synth Cycle 4","96   Fundamental Gone 1",
"97   Fundamental Gone 2","98   Bite Cycle","99   Buzzy Cycle","100  Metalphone 1",
"101  Metalphone 2","102  Metal Phone 3","103  Metal Phone 4","104  Duck Cycle 1",
"105  Duck Cycle 2","106  Duck Cycle 3","107  Wind Cycle 1","108  Wind Cycle 2",
"109  Wind Cycle 3","110  Wind Cycle 4","111  Organ Cycle 1","112  Organ Cycle 2",
"113  Noise","114  Stray Voice 1","115  Stray Voice 2","116  Stray Voice 3",
"117  Stray Voice 4","118  String Synth 1","119  String Synth 2","120  Animals",
"121  Reed","122  Pluck 1","123  Pluck 2","124  Mallet 1",
"125  Mallet 2",
	/*Second Sound Set*/
"126  Solo Cello","127  Solo Viola","128  Solo Violin",
"129  Gambambo","130  Quartet 1","131  Quartet 2","132  Quartet 3",
"133  Quartet 4","134  Pizzicato Basses","135  Pizzicato Celli","136  Pizzicato Violas",
"137  Pizzicato Violins","138  Pizzicombo","139  Bass Clarinet","140  Clarinet",
"141  Bass Clarinet / Clarinet","142  Contrabasoon","143  Basoon","144  English Horn",
"145  Oboe w/ Vib","146  Woodwinds","147  Harmon Mute","148  Tubular Bell",
"149  Timpani","150  Timpani / Tubular Bell","151  Tamborine","152  Tam Tam",
"153  Percussion 3","154  Special FX","155  Oboe w/ no Vib","156  Upright Pizz",
"157  Sine Wave","158  Tiangle Wav","159  Square Wave","160  Buzz Wave 1",
"161  Buzz Wave 2","162  Buzz Wave 3","163  Sawtooth","164  Saw Odd Gone",
"165  Ramp","166  Ramp Even Only","167  Vio Essence","168  Buzzoon",
"169  Brassy Wave","170  Reedy Buzz","171  Growl Wave","172  Harpsiwave",
"173  Fuzzy Gruzz","174  Power 5ths","175  Filt Saw","176  Ice Bell",
"177  Bronze Age","178  Iron Plate","179  Aluminum","180  Lead Beam",
"181  SteelXtract","182  Winterglass","183  TwinBellWash","184  Orch Bells",
"185  Tubular SE","186  SoftBellWave","187  Swirly","188  Tack Attack",
"189  Shimmer Wave","190  Moog Lead","191  B3 SE","192  Mild Tone",
"193  Piper","194  Ah Wave","195  Vocal Wave","196  Fuzzy Clav",
"197  Electrhode","198  Whine 1","199  Whine 2","200  Metal Drone",
"201  Silver Race","202  Metal Attack","203  Filter Bass","204  Alt. Oboe",
  };

  String [][] effectAParams = new String[][] {{},{"Decay Time"},{"Decay Time"},{"Decay Time"},{"Decay Time"},
						 {"Decay Time"},{"Decay Time"},{"Decay Time"},{"Decay Time"},
 		        {"Left Delay Time","Left Tap Level","Right Delay Time","Right Tap Level","Feedback"},
			{"Right Delay Time","Right Tap Level","Left Delay Time","Left Tap Level","Feedback"},
			{"LFO Rate","LFO Depth","Min Delay","Feedback"},
			{"LFO Rate","LFO Depth","Min Delay","Feedback"},
			{"LFO Rate","LFO Depth","Min Delay","Feedback","Mix"},
			{"Right Delay Time","Right Tap Level","Left Delay Time","Left Tap Level","Feedback"},
			{"Frequency 1","Bandwith 1","Boost/Cut 1","Frequency 2","Bandwith 2","Boost/Cut 2"},
			{"Decay Time"},{"Decay Time"},{"Decay Time"},{"Decay Time"},
		        {"Decay Time"},{"Decay Time"},{"Decay Time"},{"Decay Time"}};
int [][][] effectARanges = new int [][][] {{},{{100,255}},{{100,255}},{{100,255}},{{0,127}},
					      {{100,255}},{{100,255}},{{100,255}},{{100,255}},
					      {{1,255},{0,127},{1,255},{0,127},{0,255}},
					      {{1,255},{0,127},{1,255},{0,127},{0,255}},
					      {{0,255},{0,255},{1,255},{0,127}},                  //phaser
					      {{0,255},{0,255},{1,255},{-127,127}},               //flanger
					      {{0,255},{0,255},{1,255},{-127,127},{0,100}},       //chorus
					      {{1,255},{0,127},{1,255},{0,127},{0,255}},          //echo
					      {{0,127},{1,127},{-18,18},{0,127},{1,127},{-18,18}}, //eq
					      {{100,255}},{{100,255}},{{100,255}},{{0,255}},
					      {{0,255}},{{0,255}},{{0,255}},{{100,255}}};
  String [][] effectBParams = new String[][] {{},
             	         {"LFO Rate","LFO Depth","Min Delay","Feedback"},
		  	 {"LFO Rate","LFO Depth","Min Delay","Feedback","Mix"},
			 {"LFO Rate","LFO Depth","Min Delay","Feedback"},
			 {"Input Filter","Output Filter","Output Volume"},
			 {},
	 	         {"Left Delay Time","Left Tap Level","Right Delay Time","Right Tap Level","Feedback"},
			 {"Right Delay Time","Right Tap Level","Left Delay Time","Left Tap Level","Feedback"},
	 		 {"Frequency L","Bandwith L","Boost/Cut L","Frequency R","Bandwith R","Boost/Cut R"},
		         {"Input Filter","Output Filter"}};
  int [][][] effectBRanges = new int [][][] {{},{{0,255},{0,255},{1,255},{-127,127}},             //flanger
			      	                {{0,255},{0,255},{1,255},{-127,127},{0,100}},     //chorus
						{{0,255},{0,255},{1,255},{0,127}},		  //phaser
						{{0,255},{0,255},{0,127}},			  //fuzz
						{},						  //Ringmod
						{{1,255},{0,127},{1,255},{0,127},{0,255}},	  //delay
						{{1,255},{0,127},{1,255},{0,127},{0,255}},	  //crossdelay
 					        {{0,127},{1,127},{-18,18},{0,127},{1,127},{-18,18}}, //eq
						{{0,255},{0,255}}};				   //fuzzlite








  public EmuProteusMPSSingleEditor(Patch patch)
  {
    super ("Emu ProteusMPS Single Editor",patch);
  JTabbedPane tabPane=new JTabbedPane();
  JPanel wavePane = new JPanel();
  JPanel modPane = new JPanel();
  wavePane.setLayout(new GridBagLayout());
  modPane.setLayout(new GridBagLayout());
  tabPane.addTab("Main Parameters",wavePane);gbc.weightx=0;
  tabPane.addTab("Modulation & Effects",modPane);gbc.weightx=0;

   JPanel leftPane=new JPanel();
   leftPane.setLayout(new GridBagLayout());

  // Common Pane
  gbc.weightx=5;
  JPanel cmnPane=new JPanel();
  cmnPane.setLayout(new GridBagLayout());	 gbc.weightx=0;
   addWidget(cmnPane,new PatchNameWidget(" Name  ", patch),0,0,4,1,0);
// gbc.weightx=1;
   addWidget(cmnPane,new ScrollBarLookupWidget("Pitch Bend Range",patch,0,13,new MPSModel(patch,0x7B),new MPSSender(123),new String[]{
      "None","+/-1","+/-2","+/-3","+/-4","+/-5","+/-6","+/-7","+/-8","+/-9","+/-10","+/-11","+/-12","Global"}),0,1,5,1,1);
   addWidget(cmnPane,new ScrollBarWidget("Pressure Amount",patch,-128,127,0,new MPSModel(patch,0x7A),new MPSSender(122)),0,2,5,1,2);
   addWidget(cmnPane,new ScrollBarLookupWidget("Keyboard Center",patch,0,127,new MPSModel(patch,0x7D),new MPSSender(125),noteName),0,3,5,1,3);
   addWidget(cmnPane,new ComboBoxWidget("Vel Curve",patch,new MPSModel(patch,0x7C),new MPSSender(124),new String[]{
                        "Off","1","2","3","4","5","6","7","8","Global"}),0,4,2,1,4);
   addWidget(cmnPane,new ComboBoxWidget("Tuning",patch,new MPSModel(patch,0x7F),new MPSSender(127),new String []{
		                 "Equal","Just C","Vallotti","19 Tone","Gamelon","User 1","User 2","User 3","User 4"}),2,4,2,1,5);

   gbc.gridx=0;gbc.gridy=0;gbc.gridwidth=5;gbc.gridheight=2;gbc.fill=GridBagConstraints.BOTH;gbc.anchor=GridBagConstraints.EAST;
   cmnPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Common",TitledBorder.CENTER,TitledBorder.CENTER));
   leftPane.add(cmnPane,gbc);
 gbc.weightx=5;

// CrossFade Pane														*
  gbc.weightx=5;
  JPanel crxPane=new JPanel();
  crxPane.setLayout(new GridBagLayout());	 gbc.weightx=0;
   addWidget(crxPane,new ComboBoxWidget("XFade Mode",patch,new MPSModel(patch,0x3B),new MPSSender(59),new String []{
							                 "Off","XFade","XSwitch"}),0,1,2,1,6);
   addWidget(crxPane,new ComboBoxWidget("Key Switch Point",patch,new MPSModel(patch,0x3F),new MPSSender(63),noteName),2,1,2,1,7);
   addWidget(crxPane,new ComboBoxWidget("Direction",patch,new MPSModel(patch,0x3C),new MPSSender(60),new String []{
							                 "Primary->Secondary","Secondary->Primary"}),0,2,5,1,8);
   addWidget(crxPane,new ScrollBarWidget("Balance",patch,0,127,0,new MPSModel(patch,0x3D),new MPSSender(61)),0,3,5,1,9);
   addWidget(crxPane,new ScrollBarWidget("Amount",patch,0,255,0,new MPSModel(patch,0X3E),new MPSSender(62)),0,4,5,1,10);

   gbc.gridx=0;gbc.gridy=2;gbc.gridwidth=5;gbc.gridheight=2;gbc.fill=GridBagConstraints.BOTH;gbc.anchor=GridBagConstraints.EAST;
   crxPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Cross Fade",TitledBorder.CENTER,TitledBorder.CENTER));
   leftPane.add(crxPane,gbc);
 gbc.weightx=5;
// LFO1													*
  gbc.weightx=5;
  JPanel lfo1Pane=new JPanel();
   lfo1Pane.setLayout(new GridBagLayout());	 gbc.weightx=0;
   addWidget(lfo1Pane,new ComboBoxWidget("Waveform",patch,new MPSModel(patch,0x40),new MPSSender(64),new String []{
						                 "Random","Triangle","Sine","Saw","Square"}),0,0,1,1,11);
   addWidget(lfo1Pane,new ScrollBarWidget("LFO Frequency",patch,0,127,0,new MPSModel(patch,0x41),new MPSSender(65)),0,1,5,1,12);
   addWidget(lfo1Pane,new ScrollBarWidget("Delay",patch,0,127,0,new MPSModel(patch,0x42),new MPSSender(66)),0,2,5,1,13);
   addWidget(lfo1Pane,new ScrollBarWidget("Variation",patch,0,127,0,new MPSModel(patch,0x43),new MPSSender(67)),0,3,5,1,14);
   addWidget(lfo1Pane,new ScrollBarWidget("Amount",patch,-128,127,0,new MPSModel(patch,0x44),new MPSSender(68)),0,4,5,1,15);

   gbc.gridx=0;gbc.gridy=5;gbc.gridwidth=5;gbc.gridheight=3;gbc.fill=GridBagConstraints.BOTH;gbc.anchor=GridBagConstraints.EAST;
   lfo1Pane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"LFO1",TitledBorder.CENTER,TitledBorder.CENTER));
   leftPane.add(lfo1Pane,gbc);
 gbc.weightx=5;
// LFO2											*
  gbc.weightx=5;
  JPanel lfo2Pane=new JPanel();
   lfo2Pane.setLayout(new GridBagLayout());	 gbc.weightx=0;
   addWidget(lfo2Pane,new ComboBoxWidget("Waveform",patch,new MPSModel(patch,0x45),new MPSSender(69),new String []{
						                 "Random","Triangle","Sine","Saw","Square"}),0,0,1,1,17);
   addWidget(lfo2Pane,new ScrollBarWidget("LFO Frequency",patch,0,127,0,new MPSModel(patch,0x46),new MPSSender(70)),0,1,5,1,18);
   addWidget(lfo2Pane,new ScrollBarWidget("Delay",patch,0,127,0,new MPSModel(patch,0x47),new MPSSender(71)),0,2,5,1,19);
   addWidget(lfo2Pane,new ScrollBarWidget("Variation",patch,0,127,0,new MPSModel(patch,0x48),new MPSSender(72)),0,3,5,1,20);
   addWidget(lfo2Pane,new ScrollBarWidget("Amount",patch,-128,127,0,new MPSModel(patch,0x49),new MPSSender(73)),0,4,5,1,21);

   gbc.gridx=0;gbc.gridy=8;gbc.gridwidth=5;gbc.gridheight=3;gbc.fill=GridBagConstraints.BOTH;gbc.anchor=GridBagConstraints.EAST;
   lfo2Pane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"LFO2",TitledBorder.CENTER,TitledBorder.CENTER));
   leftPane.add(lfo2Pane,gbc);
 gbc.weightx=5;
   gbc.gridx=0;gbc.gridy=0;gbc.gridwidth=5;gbc.gridheight=15;gbc.fill=GridBagConstraints.BOTH;gbc.anchor=GridBagConstraints.NORTH;
   wavePane.add(leftPane,gbc);
//Source Pane															*
   JTabbedPane oscPane=new JTabbedPane();

   for (int i=0;i<2;i++)
   {
     JPanel panel = new JPanel();
     panel.setLayout(new GridBagLayout());
     if (i==0)oscPane.addTab("Primary Source",panel);
     else oscPane.addTab("Secondary Source",panel);
     gbc.weightx=0;
   addWidget(panel,new EnvelopeWidget("Alternate Envelope",patch,new EnvelopeWidget.Node [] {
        new EnvelopeWidget.Node(0,0,null,0,0,null,0,false,null,null,null,null),
	new EnvelopeWidget.Node(0,99,new MPSModel(patch,0x1D+i*18),0,0,null,0,false,new MPSSender(29+i*18),null,"Dly",null),
	new EnvelopeWidget.Node(0,99,new MPSModel(patch,0x20+i*18),100,100,null,25,false,new MPSSender(32+i*18),null,"A",null),
	new EnvelopeWidget.Node(0,99,new MPSModel(patch,0x21+i*18),5000,5000,null,0,false,new MPSSender(33+i*18),null,"Hld",null),
	new EnvelopeWidget.Node(0,99,new MPSModel(patch,0x22+i*18),0,99,new MPSModel(patch,0x23+i*18),25,false,new MPSSender(34+i*18),new MPSSender(35+i*18),"D","S"),
        new EnvelopeWidget.Node(100,100,null,5000,5000,null,0,false,null,null,null,null),
	new EnvelopeWidget.Node(0,100,new MPSModel(patch,0x24+i*18),0,0,null,0,false,new MPSSender(36+i*18),null,"R",null),
      }     ),0,0,3,5,33);
     addWidget(panel,new CheckBoxWidget("Alternate Envelope Enable",patch,new MPSModel(patch,0x25+i*18),new MPSSender(37+i*18)),0,5,2,1,-33);
     addWidget(panel,new ComboBoxWidget("Instrument",patch,new InstModel(patch,0x17+i*18),new InstSender(23+i*18),waveName),0,6,2,1,39);
     addWidget(panel,new ScrollBarWidget("Volume",patch,0,127,0,new MPSModel(patch,0x1B+i*18),new MPSSender(27+i*18)),0,7,3,1,40);
     addWidget(panel,new ScrollBarWidget("Detune",patch,0,15,0,new MPSModel(patch,0x27+i*18),new MPSSender(39+i*18)),0,8,3,1,41);
     addWidget(panel,new ScrollBarWidget("Sample Start",patch,0,127,0,new MPSModel(patch,0x18+i*18),new MPSSender(24+i*18)),0,9,3,1,42);
     addWidget(panel,new ScrollBarWidget("Course Tune",patch,-36,36,0,new MPSModel(patch,0x19+i*18),new MPSSender(25+i*18)),0,10,3,1,43);
     addWidget(panel,new ScrollBarWidget("Fine Tune",patch,-64,64,0,new MPSModel(patch,0x1A+i*18),new MPSSender(26+i*18)),0,11,3,1,44);
     addWidget(panel,new ScrollBarWidget("Pan",patch,-7,7,0,new MPSModel(patch,0x1C+i*18),new MPSSender(28+i*18)),0,12,3,1,45);
     addWidget(panel,new ComboBoxWidget("Low Key",patch,new MPSModel(patch,0x1E+i*18),new MPSSender(30+i*18),noteName),0,13,1,1,46);
     addWidget(panel,new ComboBoxWidget("High Key",patch,new MPSModel(patch,0x1F+i*18),new MPSSender(31+i*18),noteName),1,13,1,1,47);
     addWidget(panel,new CheckBoxWidget("Solo Mode",patch,new MPSModel(patch,0x26+i*18),new MPSSender(38+i*18)),0,14,1,1,-34);
     addWidget(panel,new CheckBoxWidget("Reverse Sound",patch,new MPSModel(patch,0x28+i*18),new MPSSender(40+i*18)),1,14,1,1,-35);

   }
 gbc.gridx=5;gbc.gridy=0;gbc.gridwidth=5;gbc.gridheight=9;
 wavePane.add(oscPane,gbc);

 // Auxillary Envelope										*
  gbc.weightx=5;
  JPanel auxPane=new JPanel();
   auxPane.setLayout(new GridBagLayout());	 gbc.weightx=0;
   addWidget(auxPane,new EnvelopeWidget("Auxilery Envelope                             ",patch,new EnvelopeWidget.Node [] {
        new EnvelopeWidget.Node(0,0,null,0,0,null,0,false,null,null,null,null),
	new EnvelopeWidget.Node(0,99,new MPSModel(patch,0x4A),0,0,null,0,false,new MPSSender(74),null,"Dly",null),
	new EnvelopeWidget.Node(0,99,new MPSModel(patch,0x4B),100,100,null,25,false,new MPSSender(75),null,"A",null),
	new EnvelopeWidget.Node(0,99,new MPSModel(patch,0x4C),5000,5000,null,0,false,new MPSSender(76),null,"Hld",null),
	new EnvelopeWidget.Node(0,99,new MPSModel(patch,0x4D),0,99,new MPSModel(patch,0x4E),25,false,new MPSSender(77),new MPSSender(78),"D","S"),
        new EnvelopeWidget.Node(100,100,null,5000,5000,null,0,false,null,null,null,null),
	new EnvelopeWidget.Node(0,100,new MPSModel(patch,0x4F),0,0,null,0,false,new MPSSender(79),null,"R",null),
      }     ),0,0,3,5,49);
     addWidget(auxPane,new ScrollBarWidget("Amount",patch,-128,127,0,new MPSModel(patch,0x50),new MPSSender(80)),0,7,3,1,55);

   gbc.gridx=10;gbc.gridy=0;gbc.gridwidth=5;gbc.gridheight=3;gbc.fill=GridBagConstraints.BOTH;gbc.anchor=GridBagConstraints.EAST;
   auxPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Auxilery",TitledBorder.CENTER,TitledBorder.CENTER));
   wavePane.add(auxPane,gbc);
 //Controller Amounts
  gbc.weightx=5;
  JPanel cntPane=new JPanel();
   cntPane.setLayout(new GridBagLayout());	 gbc.weightx=0;
     addWidget(cntPane,new ScrollBarWidget("Controller A",patch,-128,127,0,new MPSModel(patch,0x76),new MPSSender(118)),0,1,3,1,56);
     addWidget(cntPane,new ScrollBarWidget("Controller B",patch,-128,127,0,new MPSModel(patch,0x77),new MPSSender(119)),0,2,3,1,57);
     addWidget(cntPane,new ScrollBarWidget("Controller C",patch,-128,127,0,new MPSModel(patch,0x78),new MPSSender(120)),0,3,3,1,58);
     addWidget(cntPane,new ScrollBarWidget("Controller D",patch,-128,127,0,new MPSModel(patch,0x79),new MPSSender(121)),0,4,3,1,59);

   gbc.gridx=10;gbc.gridy=3;gbc.gridwidth=5;gbc.gridheight=3;gbc.fill=GridBagConstraints.BOTH;gbc.anchor=GridBagConstraints.EAST;
   cntPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Controller Amounts",TitledBorder.CENTER,TitledBorder.CENTER));
   wavePane.add(cntPane,gbc);
//Footswitch Destinations
  gbc.weightx=5;
  JPanel fswPane=new JPanel();
   fswPane.setLayout(new GridBagLayout());	 gbc.weightx=0;
   addWidget(fswPane,new ComboBoxWidget("FSW 1",patch,new MPSModel(patch,0x73),new MPSSender(115),new String []{
      "Off","Sustain","Sustain Pri","Sustain Sec","Alt Env", "Alt Env Pri", "Alt Env Sec","Alt Rel",
      "Alt Rel Pri", "Alt Rel Sec","XSwitch"}),0,0,1,1,60);
   addWidget(fswPane,new ComboBoxWidget("FSW 2",patch,new MPSModel(patch,0x74),new MPSSender(116),new String []{
      "Off","Sustain","Sustain Pri","Sustain Sec","Alt Env", "Alt Env Pri", "Alt Env Sec","Alt Rel",
      "Alt Rel Pri", "Alt Rel Sec","XSwitch"}),0,1,1,1,61);
   addWidget(fswPane,new ComboBoxWidget("FSW 3",patch,new MPSModel(patch,0x75),new MPSSender(117),new String []{
      "Off","Sustain","Sustain Pri","Sustain Sec","Alt Env", "Alt Env Pri", "Alt Env Sec","Alt Rel",
      "Alt Rel Pri", "Alt Rel Sec","XSwitch"}),2,1,1,1,62);

   gbc.gridx=10;gbc.gridy=6;gbc.gridwidth=4;gbc.gridheight=1;gbc.fill=GridBagConstraints.BOTH;gbc.anchor=GridBagConstraints.EAST;
   fswPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Footswitch Destinations",TitledBorder.CENTER,TitledBorder.CENTER));
   wavePane.add(fswPane,gbc);
   //Links
  gbc.weightx=5;
  JPanel lnkPane=new JPanel();
   lnkPane.setLayout(new GridBagLayout());	 gbc.weightx=0;
     gbc.gridx=0;gbc.gridy=0;gbc.anchor=GridBagConstraints.NORTH;lnkPane.add(new JLabel("This Patch"),gbc);
     addWidget(lnkPane,new ComboBoxWidget("Low Key",patch,new MPSModel(patch,0x0F),new MPSSender(15),noteName),2,0,1,2,65);
     addWidget(lnkPane,new ComboBoxWidget("High Key",patch,new MPSModel(patch,0x13),new MPSSender(19),noteName),3,0,1,2,66);

     addWidget(lnkPane,new ScrollBarWidget("Patch Num.",patch,-1,499,0,new MPSModel(patch,0x0C),new MPSSender(12)),0,2,1,1,67);
     addWidget(lnkPane,new ComboBoxWidget("Low Key",patch,new MPSModel(patch,0x10),new MPSSender(16),noteName),2,2,1,1,68);
     addWidget(lnkPane,new ComboBoxWidget("High Key",patch,new MPSModel(patch,0x14),new MPSSender(20),noteName),3,2,1,1,69);

     addWidget(lnkPane,new ScrollBarWidget("Patch Num.",patch,-1,499,0,new MPSModel(patch,0x0D),new MPSSender(13)),0,3,1,1,70);
     addWidget(lnkPane,new ComboBoxWidget("Low Key",patch,new MPSModel(patch,0x11),new MPSSender(17),noteName),2,3,1,1,71);
     addWidget(lnkPane,new ComboBoxWidget("High Key",patch,new MPSModel(patch,0x15),new MPSSender(21),noteName),3,3,1,1,72);

     addWidget(lnkPane,new ScrollBarWidget("Patch Num.",patch,-1,499,0,new MPSModel(patch,0x0E),new MPSSender(14)),0,4,1,1,73);
     addWidget(lnkPane,new ComboBoxWidget("Low Key",patch,new MPSModel(patch,0x12),new MPSSender(18),noteName),2,4,1,1,74);
     addWidget(lnkPane,new ComboBoxWidget("High Key",patch,new MPSModel(patch,0x16),new MPSSender(22),noteName),3,4,1,1,75);

     gbc.gridx=5;gbc.gridy=7;gbc.gridwidth=10;gbc.gridheight=5;gbc.fill=GridBagConstraints.BOTH;gbc.anchor=GridBagConstraints.EAST;
   lnkPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"                                        Interpatch Linking",TitledBorder.CENTER,TitledBorder.CENTER));
   wavePane.add(lnkPane,gbc);
//Modulations and Effects Tab											**
   JPanel modsPane=new JPanel();
   modsPane.setLayout(new GridBagLayout());	 gbc.weightx=0;
// RealTime Modulations
     JPanel realPane=new JPanel();
     realPane.setLayout(new GridBagLayout());	 gbc.weightx=0;
     for (int i=0;i<8;i++)
     {
	     addWidget(realPane,new ComboBoxWidget("",patch,new MPSModel(patch,0x63+i),new MPSSender(99+i),realtimeModSource),0,i,1,1,1+i*2);
	     addWidget(realPane,new ComboBoxWidget(" to ",patch,new MPSModel(patch,0x6B+i),new MPSSender(107+i),realtimeModDest),1,i,1,1,2+i*2);
     }
     realPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Realtime Controller Modulations",TitledBorder.CENTER,TitledBorder.CENTER));
     gbc.gridx=0;gbc.gridy=0;gbc.gridwidth=5;gbc.gridheight=5;gbc.fill=GridBagConstraints.BOTH;gbc.anchor=GridBagConstraints.EAST;
     modsPane.add(realPane,gbc);
// Key & Velocity Modulations
     JPanel keyPane=new JPanel();
     keyPane.setLayout(new GridBagLayout());	 gbc.weightx=0;
     for (int i=0;i<6;i++)
     {
	     addWidget(keyPane,new ComboBoxWidget("",patch,new MPSModel(patch,0x51+i),new MPSSender(81+i),new String[] {"Key Number","Velocity"}),0,i,1,1,17+i*3);
	     addWidget(keyPane,new ComboBoxWidget(" to ",patch,new MPSModel(patch,0x57+i),new MPSSender(87+i),keyModDest),1,i,1,1,18+i*3);
             addWidget(keyPane,new ScrollBarWidget("Amount.",patch,-128,127,0,new MPSModel(patch,0x5D+i),new MPSSender(93+i)),2,i,1,1,19+i*3);
     }
     keyPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Keyboard & Velocity Modulations",TitledBorder.CENTER,TitledBorder.CENTER));
     gbc.gridx=5;gbc.gridy=0;gbc.gridwidth=5;gbc.gridheight=5;gbc.fill=GridBagConstraints.BOTH;gbc.anchor=GridBagConstraints.EAST;
     modsPane.add(keyPane,gbc);

   modsPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Modulation Patching",TitledBorder.CENTER,TitledBorder.CENTER));
   gbc.gridx=0;gbc.gridy=0;gbc.gridwidth=10;gbc.gridheight=5;gbc.fill=GridBagConstraints.BOTH;gbc.anchor=GridBagConstraints.EAST;

   modPane.add(modsPane,gbc);
   JPanel fxPane=new JPanel();
   fxPane.setLayout(new GridBagLayout());	 gbc.weightx=0;
//Panel A
     JPanel aPane=new JPanel();
     aPane.setLayout(new GridBagLayout());	 gbc.weightx=0;
     aPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Effect Bus A",TitledBorder.CENTER,TitledBorder.CENTER));
     final ComboBoxWidget fxTypeBox=new ComboBoxWidget("Effect Type",patch,new MPSModel(patch,0x82),new MPSSender(130),new String []{
	"No Effect","Room","Warm Room","Plate 1","Plate 2","Chamber 1","Chamber 2","Hall 1","Hall 2",
        "Delay","Cross Delay","Phaser","Stereo Flange","Stereo Chorus","Echo","Stereo EQ","Small Room 1",
        "Small Room 2","Hall 3","Early Reflections 1","Early Reflections 2","Early Reflections 3",
        "Early Reflections 4","Rain"});
	addWidget(aPane,fxTypeBox,0,0,1,1,49);
      final ScrollBarWidget effectASliders []=new ScrollBarWidget[10];
        fxTypeBox.addEventListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
              for (int i=0;i<6;i++)
              {
    	        try {
			int fxa=fxTypeBox.getValue();
			effectASliders[i].setLabel(effectAParams[fxa][i]);
            ScrollBarWidget r = effectASliders[i];
	 	        r.setMin(effectARanges[fxa][i][0]);
                r.setMax(effectARanges[fxa][i][1]);
		    }
		catch (Exception e1){effectASliders[i].setLabel("Unused");}
              }
                }});
     addWidget(aPane,new ScrollBarWidget("Effect Amount (%)",patch,0,100,0,new MPSModel(patch,0x83),new MPSSender(131)),0,1,1,1,50);
     int effectA=(new MPSModel(patch,0x82)).get();
     for (int i=0;i<6;i++)
       {
          effectASliders[i]=new ScrollBarWidget("Unused",patch,0,100,0,new MPSModel(patch,0x84+i),new MPSSender(132+i));
	  addWidget(aPane,effectASliders[i],0,i+2,1,1,51+i);
	  try {
            ScrollBarWidget r = effectASliders[i];
            r.setMin(effectARanges[effectA][i][0]);
            r.setMax(effectARanges[effectA][i][1]);
	    effectASliders[i].setLabel(effectAParams[effectA][i]);}
	   catch (Exception e){effectASliders[i].setLabel("Unused");}

       }
     gbc.gridx=0;gbc.gridy=0;gbc.gridwidth=10;gbc.gridheight=5;gbc.fill=GridBagConstraints.BOTH;gbc.anchor=GridBagConstraints.EAST;
     fxPane.add(aPane,gbc);
//Panel B
     JPanel bPane=new JPanel();
     bPane.setLayout(new GridBagLayout());	 gbc.weightx=0;
     bPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Effect Bus B",TitledBorder.CENTER,TitledBorder.CENTER));
   final ComboBoxWidget fxbTypeBox=new ComboBoxWidget("Effect Type",patch,new MPSModel(patch,0x8E),new MPSSender(142),new String []{
    "Off","Stereo Flange","Stereo Chorus","Phaser","Fuzz 1","Ring Modulator","Delay","Cross Delay","Stereo EQ","Fuzz Lite"});
	addWidget(bPane,fxbTypeBox,0,0,1,1,57);
      final ScrollBarWidget effectBSliders []=new ScrollBarWidget[10];
        fxbTypeBox.addEventListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
              for (int i=0;i<6;i++)
              {
    	        try {
			int fxb=fxbTypeBox.getValue();
			effectBSliders[i].setLabel(effectBParams[fxb][i]);
            ScrollBarWidget r = effectBSliders[i];
	 	        r.setMin(effectBRanges[fxb][i][0]);
                r.setMax(effectBRanges[fxb][i][1]);
		    }
		catch (Exception e1){effectBSliders[i].setLabel("Unused");}
              }
                }});
     addWidget(bPane,new ScrollBarWidget("Effect Amount (%)",patch,0,100,0,new MPSModel(patch,0x8B),new MPSSender(143)),0,1,1,1,58);
     int effectB=(new MPSModel(patch,0x8E)).get();
     for (int i=0;i<6;i++)
       {
          effectBSliders[i]=new ScrollBarWidget("Unused",patch,0,100,0,new MPSModel(patch,0x90+i),new MPSSender(144+i));
	  addWidget(bPane,effectBSliders[i],0,i+2,1,1,59+i);
	  try {
            effectBSliders[i].setLabel(effectBParams[effectB][i]);
            ScrollBarWidget r = effectBSliders[i];
 	    r.setMin(effectBRanges[effectA][i][0]);
        r.setMax(effectBRanges[effectA][i][1]); }
 	 catch (Exception e){effectBSliders[i].setLabel("Unused");}

       }
     gbc.gridx=10;gbc.gridy=0;gbc.gridwidth=10;gbc.gridheight=5;gbc.fill=GridBagConstraints.BOTH;gbc.anchor=GridBagConstraints.EAST;
     fxPane.add(bPane,gbc);

//Panel C
     JPanel cPane=new JPanel();
     cPane.setLayout(new GridBagLayout());	 gbc.weightx=0;
     cPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Effects Common",TitledBorder.CENTER,TitledBorder.CENTER));
     addWidget(cPane,new ComboBoxWidget("Primary Source Effects Bus",patch,new MPSModel(patch,0x80),new MPSSender(128),new String [] {
             "A","B","Dry","Sub"}),0,0,1,1,35);
     addWidget(cPane,new ComboBoxWidget("Secondary Source Effects Bus",patch,new MPSModel(patch,0x81),new MPSSender(129),new String [] {
             "A","B","Dry","Sub"}),0,1,1,1,36);
     addWidget(cPane,new ScrollBarWidget("B to A",patch,0,100,0,new MPSModel(patch,0x9A),new MPSSender(154)),0,2,1,1,37);

     gbc.gridx=20;gbc.gridy=0;gbc.gridwidth=10;gbc.gridheight=5;gbc.fill=GridBagConstraints.BOTH;gbc.anchor=GridBagConstraints.EAST;
     fxPane.add(cPane,gbc);

   fxPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Effects Processing",TitledBorder.CENTER,TitledBorder.CENTER));
   gbc.gridx=0;gbc.gridy=5;gbc.gridwidth=10;gbc.gridheight=5;gbc.fill=GridBagConstraints.BOTH;gbc.anchor=GridBagConstraints.NORTH;
   modPane.add(fxPane,gbc);

   gbc.weightx=5;
   scrollPane.add(tabPane,gbc);
   pack();
  }
}

class MPSSender extends SysexSender
{
  int parameter;
  byte []b = new byte [10];
  public MPSSender(int param)
  {parameter=param;
   b[0]=(byte)0xF0; b[1]=(byte)0x18;b[2]=(byte)0x8; b[4]=0x03;
   b[5]=(byte)(parameter&127);
   b[6]=(byte)(parameter/128);
   b[9]=(byte)0xF7;
  }

  public byte [] generate (int value)
  {
     if (value>=0)
     {b[8]=(byte)(value/128);
     b[7]=(byte)(value&127);
     b[3]=(byte)(channel-1);
     return b;}
     //otherwise we need to handle the negative case
     value=-value;
     value=~value;
     value=value&16383;
     value++;
     b[8]=(byte)(value/128);
     b[7]=(byte)(value&127);
     b[3]=(byte)(channel-1);
     return b;

  }

}
//This sends the Instrument Wave-- special case due to SoundSet information
class InstSender extends SysexSender
{
  int parameter;
  byte []b = new byte [10];
  public InstSender(int param)
  {parameter=param;
   b[0]=(byte)0xF0; b[1]=(byte)0x18;b[2]=(byte)0x8; b[4]=0x03;
   b[5]=(byte)(parameter&127);
   b[6]=(byte)(parameter/128);
   b[9]=(byte)0xF7;
  }

  public byte [] generate (int value)
  {
    if (value>125) {value-=125; value+=512;}
     b[8]=(byte)(value/128);
     b[7]=(byte)(value&127);
     b[3]=(byte)(channel-1);
     return b;

  }

}



 class MPSModel extends ParamModel
{
 public MPSModel(Patch p,int o) {super(p,o*2+7);}
 public void set(int i)
 {
    if (i<0) {
	  i=-i;
          i=~i;
          i=i&16383;
          i++;;
	 }

    patch.sysex[ofs+1]=(byte)(i/128);patch.sysex[ofs]=(byte)(i%128);
 }
 public int get()
   {
     int i= (((patch.sysex[ofs+1]*128)+(patch.sysex[ofs])));
     if (i>8000) {i=16384-i;i=-i;}
     return i;
   }

}
 class InstModel extends ParamModel
{
 public InstModel(Patch p,int o) {super(p,o*2+7);}
 public void set(int i)
 {
	 if (i>125) {i-=125; i+=512;}
    patch.sysex[ofs+1]=(byte)(i/128);patch.sysex[ofs]=(byte)(i%128);
 }
 public int get()
   {
     int i= (((patch.sysex[ofs+1]*128)+(patch.sysex[ofs])));
     if (i>512) {i-=512; i+=125;}
    return i;
   }

}
