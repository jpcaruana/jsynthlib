/*
 * @version $Id$
 */
package synthdrivers.OberheimMatrix;
import core.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.util.*;
class OberheimMatrixSingleEditor extends PatchEditorFrame
{
  CheckBoxWidget  table1Check[] = new CheckBoxWidget[11];
  ScrollBarWidget table2Slider[] =new ScrollBarWidget[18];   //sliders for the modulation tables (are here to help  workaround a Swing bug)
  ScrollBarWidget table3Slider[] = new ScrollBarWidget [10];
  ComboBoxWidget table3Source[] = new ComboBoxWidget[10];
  ComboBoxWidget table3Dest[] = new ComboBoxWidget[10];

  JTable mod3Table;
  final String [] source = { "Unused Modulation","Envelope 1",
		                   "Envelope 2","Envelope 3",
				   "LFO 1","LFO 2","Vibrato","Ramp 1",
				   "Ramp 2","Keyboard Note No.",
				   "Portamento","Tracking Generator",
				   "Keyboard Gate","Velocity",
				   "Release Velocity", "Aftertouch",
				   "Pedal 1", "Pedal 2", "Lever 1",
		  	           "Lever 2","Lever 3"};
   final String [] dest = { "Unused Modulation", "DCO1 Frequency",
	   		    "DCO1 Pulse Width","DCO1 WaveShape",
			    "DCO2 Frequency","DCO2 Pulse Width",
	   		    "DCO2 WaveShape","Mix Level",
			    "VCF FM Amount","VCF Frequency",
			    "VCF Resonance","VCA1 Level",
			    "VCA2 Level",
			    "ENV1 Delay","ENV1 Attack",
			    "ENV1 Decay","ENV1 Release",
			    "ENV1 Amplitude",
			    "ENV2 Delay","ENV2 Attack",
			    "ENV2 Decay","ENV2 Release",
			    "ENV2 Amplitude",
			    "ENV3 Delay","ENV3 Attack",
			    "ENV3 Decay","ENV3 Release",
			    "ENV3 Amplitude",
			    "LFO1 Speed","LFO1 Amplitude",
			    "LFO2 Speed","LFO2 Amplitude",
		            "Portamento Time"};



  public OberheimMatrixSingleEditor(Patch patch)
  {
    super ("Oberheim Matrix Single Editor",patch);

  JTabbedPane tabPane=new JTabbedPane();

     JPanel modPanel = new JPanel();
     JPanel wavePanel = new JPanel();

     wavePanel.setLayout(new GridBagLayout());
     modPanel.setLayout(new GridBagLayout());
     tabPane.addTab("Wave",wavePanel);gbc.weightx=1;
     tabPane.addTab("Modulation",modPanel);gbc.weightx=1;

// DCO	1								*
  JPanel dco1Pane=new JPanel();
  dco1Pane.setLayout(new GridBagLayout());gbc.weightx=1;
  addWidget(dco1Pane,new ScrollBarWidget("Frequency",patch,0,63,0,new MtxModel(patch,9),new MtxSender(0)),0,0,5,1,1);
  addWidget(dco1Pane,new ScrollBarWidget("WaveShape",patch,0,63,0,new MtxModel(patch,10),new MtxSender(5)),0,1,5,1,2);
  addWidget(dco1Pane,new ScrollBarWidget("Pulse Width",patch,0,63,0,new MtxModel(patch,11),new MtxSender(3)),0,2,5,1,3);
  addWidget(dco1Pane,new CheckBoxWidget("Pulse",patch,new BitModel(patch,13,0),new BitSender(patch,6,13)),0,3,1,1,-1);
  addWidget(dco1Pane,new CheckBoxWidget("Wave",patch,new BitModel(patch,13,1),new BitSender(patch,6,13)),2,3,1,1,-2);
  addWidget(dco1Pane,new CheckBoxWidget("Click",patch,new MtxModel(patch,22),new MtxSender(9)),4,3,1,1,-3);
  addWidget(dco1Pane,new ScrollBarWidget("DCO Mix",patch,0,63,0,new MtxModel(patch,20), new MtxSender(20)),0,4,5,1,4);

   gbc.gridx=0;gbc.gridy=0;gbc.gridwidth=3;gbc.gridheight=8;
  dco1Pane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"DCO 1 Settings",TitledBorder.CENTER,TitledBorder.CENTER));
  wavePanel.add(dco1Pane,gbc);

 // DCO	2								*
  JPanel dco2Pane=new JPanel();
  dco2Pane.setLayout(new GridBagLayout());gbc.weightx=1;
  addWidget(dco2Pane,new ScrollBarWidget("Frequency",patch,0,63,0,new MtxModel(patch,14),new MtxSender(10)),0,0,7,1,5);
  addWidget(dco2Pane,new ScrollBarWidget("WaveShape",patch,0,63,0,new MtxModel(patch,15),new MtxSender(15)),0,1,7,1,6);
  addWidget(dco2Pane,new ScrollBarWidget("Pulse Width",patch,0,63,0,new MtxModel(patch,16),new MtxSender(13)),0,2,7,1,7);
  addWidget(dco2Pane,new CheckBoxWidget("Pulse",patch,new BitModel(patch,18,0),new BitSender(patch,16,18)),0,3,1,1,-4);
  addWidget(dco2Pane,new CheckBoxWidget("Wave",patch,new BitModel(patch,18,1),new BitSender(patch,16,18)),2,3,1,1,-5);
  addWidget(dco2Pane,new CheckBoxWidget("Click",patch,new MtxModel(patch,24),new MtxSender(19)),4,3,1,1,-6);
  addWidget(dco2Pane,new CheckBoxWidget("Noise",patch,new BitModel(patch,18,2),new BitSender(patch,16,18)),6,3,1,1,-7);
  addWidget(dco2Pane,new ScrollBarWidget("Detune",patch,0,62,-31,new DetuneModel(patch,19),new DetuneSender(12)),0,4,7,1,8);
  addWidget(dco2Pane,new ComboBoxWidget("Sync Mode",patch,new MtxModel(patch,25),new MtxSender(2),new String []
      			{"No Sync","Soft","Hard","Harder"}),0,5,3,1,9);

   gbc.gridx=0;gbc.gridy=8;gbc.gridwidth=3;gbc.gridheight=3;
  dco2Pane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"DCO 2 Settings",TitledBorder.CENTER,TitledBorder.CENTER));
  wavePanel.add(dco2Pane,gbc);
// Common									*
  JPanel commonPane=new JPanel();
  commonPane.setLayout(new GridBagLayout());gbc.weightx=1;
  addWidget(commonPane,new PatchNameWidget(" Name",patch),0,0,3,1,0);
  addWidget(commonPane,new ScrollBarWidget("VCA Amount",patch,0,63,0,new MtxModel(patch,31),new MtxSender(27)),0,1,5,1,10);
  addWidget(commonPane,new ScrollBarWidget("Portamento Rate",patch,0,63,0,new MtxModel(patch,32),new MtxSender(44)),0,2,5,1,11);
  addWidget(commonPane,new CheckBoxWidget("Legato Portamento Enable",patch,new MtxModel(patch,34),new MtxSender(47)),0,3,4,1,-8);

  addWidget(commonPane,new ComboBoxWidget("Lag Mode",patch,new MtxModel(patch,33),new MtxSender(46),new String []
      			{"Constant Speed. Lag","Constant Time Lag","Exponential Lag 1","Exponential Lag 2"}),0,4,5,1,12);
  addWidget(commonPane,new ComboBoxWidget("Key Mode",patch,new MtxModel(patch,8),new MtxSender(48),new String []
      			{"Reassign","Rotate","Unison","Reassign w/ Rob"}),0,5,5,1,13);

  gbc.gridx=0;gbc.gridy=12;gbc.gridwidth=3;gbc.gridheight=5;
  commonPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Common Settings",TitledBorder.CENTER,TitledBorder.CENTER));
  wavePanel.add(commonPane,gbc);
// LFO 1									*
  JPanel lfo1Pane=new JPanel();
  lfo1Pane.setLayout(new GridBagLayout());gbc.weightx=1;
  addWidget(lfo1Pane,new ScrollBarWidget("Speed",patch,0,63,0,new MtxModel(patch,35),new MtxSender(80)),0,0,7,1,17);
  addWidget(lfo1Pane,new ScrollBarWidget("Retrigger",patch,0,63,0,new MtxModel(patch,39),new MtxSender(83)),0,1,7,1,18);
  addWidget(lfo1Pane,new ScrollBarWidget("Amplitude",patch,0,63,0,new MtxModel(patch,41),new MtxSender(84)),0,2,7,1,19);
  addWidget(lfo1Pane,new ComboBoxWidget("Trigger",patch,new MtxModel(patch,36),new MtxSender(86),new String []
      			{"None","Single","Multi","External"}),3,3,2,1,21);
  addWidget(lfo1Pane,new ComboBoxWidget("Waveshape",patch,new MtxModel(patch,38),new MtxSender(82),new String []
      			{"Triangle","Up Sawtooth","Down Sawtooth","Square","Random","Noise","Sampled"}),0,3,3,1,20);
  addWidget(lfo1Pane,new ComboBoxWidget("Sample Src",patch,new MtxModel(patch,40),new MtxSender(88),source
      			   ),0,4,3,1,22);
  addWidget(lfo1Pane,new CheckBoxWidget("Lag Enable",patch,new MtxModel(patch,37),new MtxSender(87)),3,4,1,1,-17);

   gbc.gridx=5;gbc.gridy=0;gbc.gridwidth=3;gbc.gridheight=6;
  lfo1Pane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"LFO 1 Settings",TitledBorder.CENTER,TitledBorder.CENTER));
  wavePanel.add(lfo1Pane,gbc);
// LFO 2									*
  JPanel lfo2Pane=new JPanel();
  lfo2Pane.setLayout(new GridBagLayout());gbc.weightx=1;
  addWidget(lfo2Pane,new ScrollBarWidget("Speed",patch,0,63,0,new MtxModel(patch,42),new MtxSender(90)),0,0,7,1,23);
  addWidget(lfo2Pane,new ScrollBarWidget("Retrigger",patch,0,63,0,new MtxModel(patch,46),new MtxSender(93)),0,1,7,1,24);
  addWidget(lfo2Pane,new ScrollBarWidget("Amplitude",patch,0,63,0,new MtxModel(patch,48),new MtxSender(94)),0,2,7,1,25);
  addWidget(lfo2Pane,new ComboBoxWidget("Trigger",patch,new MtxModel(patch,43),new MtxSender(96),new String []
      			{"None","Single","Multi","External"}),3,3,2,1,27);
  addWidget(lfo2Pane,new ComboBoxWidget("Waveshape",patch,new MtxModel(patch,45),new MtxSender(92),new String []
      			{"Triangle","Up Sawtooth","Down Sawtooth","Square","Random","Noise","Sampled"}),0,3,3,1,26);
  addWidget(lfo2Pane,new ComboBoxWidget("Sample Src",patch,new MtxModel(patch,47),new MtxSender(98),source
      			   ),0,4,3,1,28);
  addWidget(lfo2Pane,new CheckBoxWidget("Lag Enable",patch,new MtxModel(patch,44),new MtxSender(97)),3,4,1,1,-18);

   gbc.gridx=5;gbc.gridy=6;gbc.gridwidth=3;gbc.gridheight=8;
  lfo2Pane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"LFO 2 Settings",TitledBorder.CENTER,TitledBorder.CENTER));
  wavePanel.add(lfo2Pane,gbc);
// VCF									*
  JPanel vcfPane=new JPanel();
  vcfPane.setLayout(new GridBagLayout());gbc.weightx=1;
  addWidget(vcfPane,new ScrollBarWidget("Frequency",patch,0,127,0,new MtxModel(patch,26),new MtxSender(21)),0,0,7,1,14);
  addWidget(vcfPane,new ScrollBarWidget("Resonance",patch,0,63,0,new MtxModel(patch,27),new MtxSender(24)),0,1,7,1,15);
  addWidget(vcfPane,new ScrollBarWidget("FM Amount",patch,0,63,0,new MtxModel(patch,30),new MtxSender(30)),0,2,7,1,16);

   gbc.gridx=0;gbc.gridy=17;gbc.gridwidth=3;gbc.gridheight=6;
  vcfPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"VCF Settings",TitledBorder.CENTER,TitledBorder.CENTER));
  wavePanel.add(vcfPane,gbc);
 // Ramps									*
  JPanel rampPane=new JPanel();
  rampPane.setLayout(new GridBagLayout());gbc.weightx=1;
  addWidget(rampPane,new ScrollBarWidget("Ramp 1 Rate",patch,0,63,0,new MtxModel(patch,82),new MtxSender(40)),0,0,7,1,33);
  addWidget(rampPane,new ComboBoxWidget("Ramp 1 Mode",patch,new MtxModel(patch,83),new MtxSender(41),new String []
      			{"Single Trigger","Multi Trigger","External Trigger","External Gated"}),0,1,7,1,34);
  addWidget(rampPane,new ScrollBarWidget("Ramp 2 Rate",patch,0,63,0,new MtxModel(patch,84),new MtxSender(42)),0,2,7,1,35);
  addWidget(rampPane,new ComboBoxWidget("Ramp 2 Mode",patch,new MtxModel(patch,85),new MtxSender(43),new String []
      			{"Single Trigger","Multi Trigger","External Trigger","External Gated"}),0,7,3,1,36);

  gbc.gridx=5;gbc.gridy=14;gbc.gridwidth=3;gbc.gridheight=4;
  rampPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Ramp Settings",TitledBorder.CENTER,TitledBorder.CENTER));
  wavePanel.add(rampPane,gbc);
// Envelope Amplitudes     						*
  JPanel ampPane=new JPanel();
  ampPane.setLayout(new GridBagLayout());gbc.weightx=1;
  addWidget(ampPane,new ScrollBarWidget("Fnv1 Amplitude",patch,0,63,0,new MtxModel(patch,55),new MtxSender(55)),0,0,7,1,37);
  addWidget(ampPane,new ScrollBarWidget("Env2 Amplitude",patch,0,63,0,new MtxModel(patch,64),new MtxSender(65)),0,1,7,1,38);
  addWidget(ampPane,new ScrollBarWidget("Env3 Amplitude",patch,0,63,0,new MtxModel(patch,73),new MtxSender(75)),0,2,7,1,39);

   gbc.gridx=5;gbc.gridy=17;gbc.gridwidth=3;gbc.gridheight=6;
  ampPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Envelope Amplitudes",TitledBorder.CENTER,TitledBorder.CENTER));
  wavePanel.add(ampPane,gbc);

  JPanel envPane=new JPanel();
  envPane.setLayout(new GridBagLayout());gbc.weightx=1;

// Envelope 1								*
  JPanel env1Pane=new JPanel();
  env1Pane.setLayout(new GridBagLayout());gbc.weightx=1;
    addWidget(env1Pane,new EnvelopeWidget("Envelope",patch,new EnvelopeWidget.Node [] {
        new EnvelopeWidget.Node(0,0,null,0,0,null,0,false,null,null,null,null),
	new EnvelopeWidget.Node(0,63,new MtxModel(patch,50),0,0,null,0,false,new MtxSender(50),null,"Dly",null),
	new EnvelopeWidget.Node(0,63,new MtxModel(patch,51),63,63,null,25,false,new MtxSender(51),null,"A",null),
	new EnvelopeWidget.Node(0,63,new MtxModel(patch,52),0,63,new MtxModel(patch,53),25,false,new MtxSender(52),new MtxSender(53),"D","S"),
        new EnvelopeWidget.Node(63,63,null,5000,5000,null,0,false,null,null,null,null),
	new EnvelopeWidget.Node(0,63,new MtxModel(patch,54),0,0,null,0,false,new MtxSender(54),null,"R",null),
      }     ),0,0,7,5,49);
  addWidget(env1Pane,new CheckBoxWidget("Reset Tr",patch,new BitModel(patch,49,0),new BitSender(patch,57,49)),0,6,1,1,-49);
  addWidget(env1Pane,new CheckBoxWidget("Multi Tr",patch,new BitModel(patch,49,1),new BitSender(patch,57,49)),2,6,1,1,-50);
  addWidget(env1Pane,new CheckBoxWidget("Extrn Tr",patch,new BitModel(patch,49,2),new BitSender(patch,57,49)),4,6,1,1,-51);
  addWidget(env1Pane,new CheckBoxWidget("Gated Tr",patch,new BitModel(patch,56,0),new BitSender(patch,59,56)),0,7,1,1,-52);
  addWidget(env1Pane,new CheckBoxWidget("Lfo Tr",patch,new BitModel(patch,56,1),new BitSender(patch,59,56)),2,7,1,1,-53);
  addWidget(env1Pane,new CheckBoxWidget("DADR",patch,new BitModel(patch,57,0),new BitSender(patch,60,57)),4,7,1,1,-54);
  addWidget(env1Pane,new CheckBoxWidget("Free Run",patch,new BitModel(patch,57,1),new BitSender(patch,60,57)),6,7,1,1,-55);
   gbc.gridx=10;gbc.gridy=0;gbc.gridwidth=7;gbc.gridheight=9;
  env1Pane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Envelope 1",TitledBorder.CENTER,TitledBorder.CENTER));
  envPane.add(env1Pane,gbc);
// Envelope 2								*
  JPanel env2Pane=new JPanel();
  env2Pane.setLayout(new GridBagLayout());gbc.weightx=1;
    addWidget(env2Pane,new EnvelopeWidget("Envelope",patch,new EnvelopeWidget.Node [] {
        new EnvelopeWidget.Node(0,0,null,0,0,null,0,false,null,null,null,null),
	new EnvelopeWidget.Node(0,63,new MtxModel(patch,59),0,0,null,0,false,new MtxSender(60),null,"Dly",null),
	new EnvelopeWidget.Node(0,63,new MtxModel(patch,60),63,63,null,25,false,new MtxSender(61),null,"A",null),
	new EnvelopeWidget.Node(0,63,new MtxModel(patch,61),0,63,new MtxModel(patch,62),25,false,new MtxSender(62),new MtxSender(63),"D","S"),
        new EnvelopeWidget.Node(63,63,null,5000,5000,null,0,false,null,null,null,null),
	new EnvelopeWidget.Node(0,63,new MtxModel(patch,63),0,0,null,0,false,new MtxSender(64),null,"R",null),
      }     ),0,0,7,5,54);
  addWidget(env2Pane,new CheckBoxWidget("Reset Tr",patch,new BitModel(patch,58,0),new BitSender(patch,67,58)),0,6,1,1,-56);
  addWidget(env2Pane,new CheckBoxWidget("Multi Tr",patch,new BitModel(patch,58,1),new BitSender(patch,67,58)),2,6,1,1,-57);
  addWidget(env2Pane,new CheckBoxWidget("Extrn Tr",patch,new BitModel(patch,58,2),new BitSender(patch,67,58)),4,6,1,1,-58);
  addWidget(env2Pane,new CheckBoxWidget("Gated Tr",patch,new BitModel(patch,65,0),new BitSender(patch,69,65)),0,7,1,1,-59);
  addWidget(env2Pane,new CheckBoxWidget("Lfo Tr",patch,new BitModel(patch,65,1),new BitSender(patch,69,65)),2,7,1,1,-60);
  addWidget(env2Pane,new CheckBoxWidget("DADR",patch,new BitModel(patch,66,0),new BitSender(patch,68,66)),4,7,1,1,-61);
  addWidget(env2Pane,new CheckBoxWidget("Free Run",patch,new BitModel(patch,66,2),new BitSender(patch,68,66)),6,7,1,1,-62);
   gbc.gridx=10;gbc.gridy=9;gbc.gridwidth=7;gbc.gridheight=7;
  env2Pane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Envelope 2",TitledBorder.CENTER,TitledBorder.CENTER));
  envPane.add(env2Pane,gbc);
// Envelope 3								*
  JPanel env3Pane=new JPanel();
  env3Pane.setLayout(new GridBagLayout());gbc.weightx=1;
    addWidget(env3Pane,new EnvelopeWidget("Envelope",patch,new EnvelopeWidget.Node [] {
        new EnvelopeWidget.Node(0,0,null,0,0,null,0,false,null,null,null,null),
	new EnvelopeWidget.Node(0,63,new MtxModel(patch,68),0,0,null,0,false,new MtxSender(70),null,"Dly",null),
	new EnvelopeWidget.Node(0,63,new MtxModel(patch,69),63,63,null,25,false,new MtxSender(71),null,"A",null),
	new EnvelopeWidget.Node(0,63,new MtxModel(patch,70),0,63,new MtxModel(patch,71),25,false,new MtxSender(72),new MtxSender(73),"D","S"),
        new EnvelopeWidget.Node(63,63,null,5000,5000,null,0,false,null,null,null,null),
	new EnvelopeWidget.Node(0,63,new MtxModel(patch,72),0,0,null,0,false,new MtxSender(74),null,"R",null),
      }     ),0,0,7,5,59);
  addWidget(env3Pane,new CheckBoxWidget("Reset Tr",patch,new BitModel(patch,67,0),new BitSender(patch,77,67)),0,6,1,1,-33);
  addWidget(env3Pane,new CheckBoxWidget("Multi Tr",patch,new BitModel(patch,67,1),new BitSender(patch,77,67)),2,6,1,1,-34);
  addWidget(env3Pane,new CheckBoxWidget("Extrn Tr",patch,new BitModel(patch,67,2),new BitSender(patch,77,67)),4,6,1,1,-35);
  addWidget(env3Pane,new CheckBoxWidget("Gated Tr",patch,new BitModel(patch,74,0),new BitSender(patch,79,74)),0,7,1,1,-36);
  addWidget(env3Pane,new CheckBoxWidget("Lfo Tr",patch,new BitModel(patch,74,1),new BitSender(patch,79,74)),2,7,1,1,-37);
  addWidget(env3Pane,new CheckBoxWidget("DADR",patch,new BitModel(patch,75,0),new BitSender(patch,78,75)),4,7,1,1,-38);
  addWidget(env3Pane,new CheckBoxWidget("Free Run",patch,new BitModel(patch,75,1),new BitSender(patch,78,75)),6,7,1,1,-39);
   gbc.gridx=10;gbc.gridy=16;gbc.gridwidth=7;gbc.gridheight=13;
  env3Pane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Envelope 3",TitledBorder.CENTER,TitledBorder.CENTER));
  envPane.add(env3Pane,gbc);

  gbc.gridx=10;gbc.gridy=0;gbc.gridwidth=7;gbc.gridheight=30;
  wavePanel.add(envPane,gbc);

// Modulation Matrix                                                *
//   Setup HardWired Table (Table1)
  Mod1TableModel mod1TableModel=new Mod1TableModel();
  JTable mod1Table = new JTable(mod1TableModel);
  JScrollPane mod1FramePane=new JScrollPane(mod1Table);
  gbc.gridx=0;gbc.gridy=0;gbc.gridwidth=10;gbc.gridheight=6;gbc.fill=GridBagConstraints.BOTH;gbc.anchor=GridBagConstraints.NORTH;
  mod1FramePane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Hard Wired Fixed Modulations",TitledBorder.CENTER,TitledBorder.CENTER));
  modPanel.add(mod1FramePane,gbc);
   mod1Table.setPreferredScrollableViewportSize(new Dimension(450, 180));
  table1Check[0] =new CheckBoxWidget("",patch,new BitModel(patch,12,0),new BitSender(patch,7,12));
  table1Check[1] =new CheckBoxWidget("",patch,new BitModel(patch,12,1),new BitSender(patch,7,12));
  table1Check[2] =new CheckBoxWidget("",patch,new BitModel(patch,21,0),new BitSender(patch,8,21));
  table1Check[3] =new CheckBoxWidget("",patch,new BitModel(patch,17,0),new BitSender(patch,17,17));
  table1Check[4] =new CheckBoxWidget("",patch,new BitModel(patch,17,1),new BitSender(patch,17,17));
  table1Check[5] =new CheckBoxWidget("",patch,new BitModel(patch,23,0),new BitSender(patch,18,23));
  table1Check[6] =new CheckBoxWidget("",patch,new BitModel(patch,23,1),new BitSender(patch,18,23));
  table1Check[7] =new CheckBoxWidget("",patch,new BitModel(patch,28,0),new BitSender(patch,25,28));
  table1Check[8] =new CheckBoxWidget("",patch,new BitModel(patch,28,1),new BitSender(patch,25,28));
  table1Check[9] =new CheckBoxWidget("",patch,new BitModel(patch,29,0),new BitSender(patch,26,29));
  table1Check[10] =new CheckBoxWidget("",patch,new BitModel(patch,29,1),new BitSender(patch,26,29));

  TableColumn column = null;
    column = mod1Table.getColumnModel().getColumn(0);
    column.setPreferredWidth(250);
    column = mod1Table.getColumnModel().getColumn(1);
    column.setPreferredWidth(250);
    column = mod1Table.getColumnModel().getColumn(2);
    column.setPreferredWidth(100);
    CheckBoxCellRenderer checkBoxCellRenderer = new CheckBoxCellRenderer(patch);
    column.setCellRenderer (checkBoxCellRenderer);
    CheckBoxCellEditor checkBoxCellEditor = new CheckBoxCellEditor();
    column.setCellEditor(checkBoxCellEditor);


//    Setup Variable Table (Table2)
  Mod2TableModel mod2TableModel=new Mod2TableModel();
  JTable mod2Table = new JTable(mod2TableModel);
  JScrollPane mod2FramePane=new JScrollPane(mod2Table);
  gbc.gridx=10;gbc.gridy=0;gbc.gridwidth=10;gbc.gridheight=10;gbc.fill=GridBagConstraints.BOTH;gbc.anchor=GridBagConstraints.NORTH;
  mod2FramePane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Hardwired Adjustable Modulations",TitledBorder.CENTER,TitledBorder.CENTER));
  modPanel.add(mod2FramePane,gbc);
  mod2Table.setRowHeight(20);
  mod2Table.setPreferredScrollableViewportSize(new Dimension(450, 375));
       table2Slider[0] =new ScrollBarWidget("",patch,0,126,-63,new ModModel(patch,86),new SliderSender(01));
       table2Slider[1] =new ScrollBarWidget("",patch,0,126,-63,new ModModel(patch,87),new SliderSender(04));
       table2Slider[2] =new ScrollBarWidget("",patch,0,126,-63,new ModModel(patch,88),new SliderSender(11));
       table2Slider[3] =new ScrollBarWidget("",patch,0,126,-63,new ModModel(patch,89),new SliderSender(14));
       table2Slider[4] =new ScrollBarWidget("",patch,0,126,-63,new ModModel(patch,90),new SliderSender(22));
       table2Slider[5] =new ScrollBarWidget("",patch,0,126,-63,new ModModel(patch,91),new SliderSender(23));
       table2Slider[6] =new ScrollBarWidget("",patch,0,126,-63,new ModModel(patch,92),new SliderSender(28));
       table2Slider[7] =new ScrollBarWidget("",patch,0,126,-63,new ModModel(patch,93),new SliderSender(29));
       table2Slider[8] =new ScrollBarWidget("",patch,0,126,-63,new ModModel(patch,94),new SliderSender(56));
       table2Slider[9] =new ScrollBarWidget("",patch,0,126,-63,new ModModel(patch,95),new SliderSender(66));
       table2Slider[10] =new ScrollBarWidget("",patch,0,126,-63,new ModModel(patch,96),new SliderSender(76));
       table2Slider[11] =new ScrollBarWidget("",patch,0,126,-63,new ModModel(patch,97),new SliderSender(85));
       table2Slider[12] =new ScrollBarWidget("",patch,0,126,-63,new ModModel(patch,98),new SliderSender(95));
       table2Slider[13] =new ScrollBarWidget("",patch,0,126,-63,new ModModel(patch,99),new SliderSender(45));
       table2Slider[14] =new ScrollBarWidget("",patch,0,126,-63,new ModModel(patch,100),new SliderSender(31));
       table2Slider[15] =new ScrollBarWidget("",patch,0,126,-63,new ModModel(patch,101),new SliderSender(32));
       table2Slider[16] =new ScrollBarWidget("",patch,0,126,-63,new ModModel(patch,102),new SliderSender(81));
       table2Slider[17] =new ScrollBarWidget("",patch,0,126,-63,new ModModel(patch,103),new SliderSender(91));
       gbc.gridx=0;gbc.fill=GridBagConstraints.NONE;
       for (int i=0;i<18;i++) {
	  //sliderList.add(table2Slider[i].slider);
          modPanel.add(table2Slider[i],gbc);    //work around for Java Swing Bug
       }

    column = mod2Table.getColumnModel().getColumn(0);
    column.setPreferredWidth(200);
    column = mod2Table.getColumnModel().getColumn(1);
    column.setPreferredWidth(250);
    column = mod2Table.getColumnModel().getColumn(2);
    column.setPreferredWidth(500);
    SliderCellRenderer sliderCellRenderer = new SliderCellRenderer(2);
    column.setCellRenderer (sliderCellRenderer);
    SliderCellEditor sliderCellEditor = new SliderCellEditor(2);
    column.setCellEditor(sliderCellEditor);


//    Setup User Defines Table (Table3)
  Mod3TableModel mod3TableModel=new Mod3TableModel();
  mod3Table = new JTable(mod3TableModel);
  JScrollPane mod3FramePane=new JScrollPane(mod3Table);
  gbc.gridx=00;gbc.gridy=06;gbc.gridwidth=10;gbc.gridheight=6;gbc.fill=GridBagConstraints.BOTH;gbc.anchor=GridBagConstraints.NORTH;
  mod3FramePane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"User Defined Modulations",TitledBorder.CENTER,TitledBorder.CENTER));
  modPanel.add(mod3FramePane,gbc);
  mod3Table.setRowHeight(26);
  mod3Table.setPreferredScrollableViewportSize(new Dimension(450, 65));
       table3Source[0] = new ComboBoxWidget("",patch,new
MtxModel(patch,104),new ModSender(patch,0),source);
       table3Source[1] = new ComboBoxWidget("",patch,new MtxModel(patch,107),new ModSender(patch,1),source);
       table3Source[2] = new ComboBoxWidget("",patch,new MtxModel(patch,110),new ModSender(patch,2),source);
       table3Source[3] = new ComboBoxWidget("",patch,new MtxModel(patch,113),new ModSender(patch,3),source);
       table3Source[4] = new ComboBoxWidget("",patch,new MtxModel(patch,116),new ModSender(patch,4),source);
       table3Source[5] = new ComboBoxWidget("",patch,new MtxModel(patch,119),new ModSender(patch,5),source);
       table3Source[6] = new ComboBoxWidget("",patch,new MtxModel(patch,122),new ModSender(patch,6),source);
       table3Source[7] = new ComboBoxWidget("",patch,new MtxModel(patch,125),new ModSender(patch,7),source);
       table3Source[8] = new ComboBoxWidget("",patch,new MtxModel(patch,128),new ModSender(patch,3),source);
       table3Source[9] = new ComboBoxWidget("",patch,new MtxModel(patch,131),new ModSender(patch,9),source);
       table3Dest[0] = new ComboBoxWidget("",patch,new MtxModel(patch,106),new ModSender(patch,0),dest);
       table3Dest[1] = new ComboBoxWidget("",patch,new MtxModel(patch,109),new ModSender(patch,1),dest);
       table3Dest[2] = new ComboBoxWidget("",patch,new MtxModel(patch,112),new ModSender(patch,2),dest);
       table3Dest[3] = new ComboBoxWidget("",patch,new MtxModel(patch,115),new ModSender(patch,3),dest);
       table3Dest[4] = new ComboBoxWidget("",patch,new MtxModel(patch,118),new ModSender(patch,4),dest);
       table3Dest[5] = new ComboBoxWidget("",patch,new MtxModel(patch,121),new ModSender(patch,5),dest);
       table3Dest[6] = new ComboBoxWidget("",patch,new MtxModel(patch,123),new ModSender(patch,6),dest);
       table3Dest[7] = new ComboBoxWidget("",patch,new MtxModel(patch,127),new ModSender(patch,7),dest);
       table3Dest[8] = new ComboBoxWidget("",patch,new MtxModel(patch,130),new ModSender(patch,8),dest);
       table3Dest[9] = new ComboBoxWidget("",patch,new MtxModel(patch,133),new ModSender(patch,9),dest);

       table3Slider[0] =new ScrollBarWidget("",patch,0,126,-63,new ModModel(patch,105),new ModSender(patch,0));
       table3Slider[1] =new ScrollBarWidget("",patch,0,126,-63,new ModModel(patch,108),new ModSender(patch,1));
       table3Slider[2] =new ScrollBarWidget("",patch,0,126,-63,new ModModel(patch,111),new ModSender(patch,2));
       table3Slider[3] =new ScrollBarWidget("",patch,0,126,-63,new ModModel(patch,114),new ModSender(patch,3));
       table3Slider[4] =new ScrollBarWidget("",patch,0,126,-63,new ModModel(patch,117),new ModSender(patch,4));
       table3Slider[5] =new ScrollBarWidget("",patch,0,126,-63,new ModModel(patch,120),new ModSender(patch,5));
       table3Slider[6] =new ScrollBarWidget("",patch,0,126,-63,new ModModel(patch,123),new ModSender(patch,6));
       table3Slider[7] =new ScrollBarWidget("",patch,0,126,-63,new ModModel(patch,126),new ModSender(patch,7));
       table3Slider[8] =new ScrollBarWidget("",patch,0,126,-63,new ModModel(patch,129),new ModSender(patch,8));
       table3Slider[9] =new ScrollBarWidget("",patch,0,126,-63,new ModModel(patch,132),new ModSender(patch,9));

       gbc.gridx=0;gbc.gridy=0;gbc.fill=GridBagConstraints.NONE;
       for (int i=0;i<10;i++) {
	  //sliderList.add(table3Slider[i].slider);
          modPanel.add(table3Slider[i],gbc);    //work around for Java Swing Bug
       }

    column = mod3Table.getColumnModel().getColumn(0);
    column.setPreferredWidth(240);
    ComboBoxCellEditor sourceEditor = new ComboBoxCellEditor(0);
    column.setCellEditor(sourceEditor);
    column = mod3Table.getColumnModel().getColumn(1);
    column.setPreferredWidth(240);
    ComboBoxCellEditor destEditor = new ComboBoxCellEditor(1);
    column.setCellEditor(destEditor);
    column = mod3Table.getColumnModel().getColumn(2);
    column.setPreferredWidth(500);
    SliderCellRenderer sliderCellRenderer2 = new SliderCellRenderer(3);
    column.setCellRenderer (sliderCellRenderer2);
    SliderCellEditor sliderCellEditor2 = new SliderCellEditor(3);
    column.setCellEditor(sliderCellEditor2);

//   Set up Tracking Generator

   addWidget(modPanel,new EnvelopeWidget("Tracking Generator Function",patch,new EnvelopeWidget.Node [] {
        new EnvelopeWidget.Node(0,0,    null,0,63,new MtxModel(patch,77),0,false,null,new MtxSender(34),null,"1"),
	new EnvelopeWidget.Node(100,100,  null,0,63,new MtxModel(patch,78),0,false,null,new MtxSender(35),null,"2"),
	new EnvelopeWidget.Node(100,100,null,0,63,new MtxModel(patch,79),0,false,null,new MtxSender(36),null,"3"),
	new EnvelopeWidget.Node(100,100,null,0,63,new MtxModel(patch,80),0,false,null,new MtxSender(37),null,"4"),
        new EnvelopeWidget.Node(100,100,null,0,63,new MtxModel(patch,81),0,false,null,new MtxSender(38),null,"5"),
      }     ),11,11,8,3,2);
   addWidget(modPanel,new ComboBoxWidget("Tracking Generator Source ",patch,new MtxModel(patch,76),new MtxSender(33),source
      			   ),11,10,8,1,1);


 gbc.gridx=0;gbc.gridy=0;gbc.gridwidth=6;gbc.gridheight=5;gbc.fill=GridBagConstraints.BOTH;
  scrollPane.add(tabPane,gbc);
   pack();
   if (Utility.getOSName().equals("Linux")) // Does J2SE 1.4 still require this?
   {
     ErrorMsg.reportStatus("Matrix1000Editor:  Linux Detected-- adding 30 pixels to window height to compensate for Sun/JRE bug");
      Dimension rv = new Dimension();
      rv=getSize(rv);
      reshape(getX(),getY(),rv.width,rv.height+30);
   }
   show();

}

    class ModTableModel extends AbstractTableModel {
	final String[] columnNames;
	final int rowcount;
	final Object[][] tableValues;

	ModTableModel(String[] cols, int rows, Object[][] values) {
	    columnNames = cols;
	    rowcount = rows;
	    tableValues = values;
	}
	public int getColumnCount() { return columnNames.length; }
	public String getColumnName(int col) { return columnNames[col]; }
	public int getRowCount() { return rowcount; }
	public Class getColumnClass(int c) {
	    return getValueAt(0, c).getClass();
	}
	public Object getValueAt(int row, int col) {
	    try {
		return tableValues[col][row];
	    } catch (IndexOutOfBoundsException ex) {
		return new Integer(0);
	    }
	}
	public boolean isCellEditable(int row, int col) {
	    if (col < 2)
		return false;
	    return true;
	}
	public void setValueAt(Object value, int row, int col) {
	    fireTableCellUpdated(row, col);
	}
    }
    class Mod1TableModel extends ModTableModel {
	Mod1TableModel() {
	    super(new String[] {"Modulation Source", "Modulation Destination",
				"Enabled?"},
		  11, new String[][] {
		    {
			"Midi Lever 1","Vibrato","Portamento","Midi Lever 1",
			"Vibrato","Portamento","Keyboard Note No.",
			"Midi Lever 1","Vibrato","Portamento","Keyboard."
		    },
		    {
			"DCO1 Frequency","DCO1 Frequency","DCO1 Frequency",
			"DC02 Frequency","DC02 Frequency","DC02 Frequency",
			"DC02 Frequency",
			"VCF Cutoff Frequency","VCF Cutoff Frequency",
			"VCF Cutoff Frequency","VCF Cutoff Frequency"
		    }
		});
	}
	public void setValueAt(Object value, int row, int col) {}
    }
    class Mod2TableModel extends ModTableModel {
	Mod2TableModel() {
	    super( new String[] {"Modulation Source", "Modulation Destination",
				 "Strength"},
		   18,
		   new String[][] {
		       { "LFO 1","LFO 2","LFO 1","LFO 2","Envelope 1",
			 "Aftertouch","Velocity","Envelope 2","Velocity",
			 "Velocity","Velocity","Ramp 1","Ramp 2","Velocity",
			 "Envelope 3","Aftertouch","Aftertouch","Keyboard",
		       },
		       {
			   "DCO 1 Frequency","DCO 1 Pulse Width",
			   "DCO 2 Frequency","DCO 2 Pulse Width",
			   "VCF Cutoff Frequency","VCF Cutoff Frequency",
			   "VCA 1 Level","VCA 2 Level","Envelope 1 Amplitude",
			   "Envelope 2 Amplitude","Envelope 3 Amplitude",
			   "LFO 1 Amplitude","LFO 2 Amplitude","Portamento Rate",
			   "VCF FM Mod Level","VCF FM Mod Level","LFO 1 Speed",
			   "LFO 2 Speed.",
		       }
		   });
	}
    }

    class Mod3TableModel extends ModTableModel {
	Mod3TableModel() {
	    super( new String[] {"Modulation Source", "Modulation Destination",
				 "Strength"},
		   10,
		   null );
	}

	public Object getValueAt(int row, int col)
	{
	    if (col==0)
		return source[table3Source[row].getValue()];
	    if (col==1)
		return dest[table3Dest[row].getValue()];
	    return new Integer(0);
	}

        public boolean isCellEditable(int row, int col) {
             return true;
        }
    }

 class SliderCellRenderer  implements TableCellRenderer {
  int tablenum;
  SliderCellRenderer(int t) {tablenum=t;}
  public Component getTableCellRendererComponent (JTable table, Object value, boolean isSelected, boolean hasFocus,
                                                  int row, int col)
    {
       if (tablenum==2) return table2Slider[row]; else return table3Slider[row];
    }
 }
 class SliderCellEditor implements TableCellEditor  {
   int tablenum;
     SliderCellEditor(int t) {tablenum=t;}
  public Component getTableCellEditorComponent (JTable table, Object value, boolean isSelected,
                                                  int row, int col)
    {
       if (tablenum==2) return table2Slider[row]; else return table3Slider[row];
    }
    public void cancelCellEditing(){};
  public Object getCellEditorValue() {return new Integer(0);}
  public void addCellEditorListener (CellEditorListener l) {}
  public boolean isCellEditable (EventObject e) {return true;}
  public void removeCellEditorListener (CellEditorListener l) {}
  public boolean shouldSelectCell (EventObject e) {return true;}
  public boolean stopCellEditing() {return true;}
 }

 class CheckBoxCellRenderer  implements TableCellRenderer {
  Patch patch;
  CheckBoxCellRenderer(Patch p) {patch=p;}
  public Component getTableCellRendererComponent (JTable table, Object value, boolean isSelected, boolean hasFocus,
                                                  int row, int col)
    {
       return table1Check[row];
    }
 }
 class CheckBoxCellEditor implements TableCellEditor  {

  public Component getTableCellEditorComponent (JTable table, Object value, boolean isSelected,
                                                  int row, int col)
    {
       return table1Check[row];
    }
    public void cancelCellEditing(){};
  public Object getCellEditorValue() {return new Integer(0);}
  public void addCellEditorListener (CellEditorListener l) {}
  public boolean isCellEditable (EventObject e) {return true;}
  public void removeCellEditorListener (CellEditorListener l) {}
  public boolean shouldSelectCell (EventObject e) {return true;}
  public boolean stopCellEditing() {return true;}
 }
 class ComboBoxCellEditor implements TableCellEditor  {
     int editrow;
     int col;
  ComboBoxCellEditor (int c) {col=c;};
  public Component getTableCellEditorComponent (JTable table, Object value, boolean isSelected,
                                                  int row, int col)
    {
       if (col==0) table3Source[row].setVisible(true); else table3Dest[row].setVisible(true);
       editrow=row;
       if (col==0) return table3Source[row]; else return table3Dest[row];
    }
    public void cancelCellEditing()
      {if (col==0) table3Source[editrow].setVisible(false);
        else table3Dest[editrow].setVisible(false);mod3Table.repaint();};
  public Object getCellEditorValue() {return "getCellEditorValue";}
  public void addCellEditorListener (CellEditorListener l) {}
  public boolean isCellEditable (EventObject e) {return true;}
  public void removeCellEditorListener (CellEditorListener l) {}
  public boolean shouldSelectCell (EventObject e) {return true;}
  public boolean stopCellEditing()
     {if (col==0) table3Source[editrow].setVisible(false); else table3Dest[editrow].setVisible(false);
      mod3Table.repaint();return true;}
 }


//*****************************************************************************************************************
//  After this point is all of the SysexSender and ParamModel Definitions for this Synth


//SysexSender for most of the Matrix1000 Parameters
class MtxSender extends SysexSender
{
  int parameter;
  byte []b = new byte [7];
  public MtxSender(int param)
  {parameter=param;
   b[0]=(byte)0xF0; b[1]=(byte)0x10;b[2]=(byte)0x06;b[3]=0x06; b[4]=(byte)parameter;b[6]=(byte)0xF7;
  }
  public byte [] generate (int value)
  {b[5]=(byte)value;return b;}

}
//Parameter Model for most of the Matrix1000 Parameters
 class MtxModel extends ParamModel
{
 public MtxModel(Patch p,int o) {ofs=o*2+5;patch=p;}
 public void set(int i) {patch.sysex[ofs]=(byte)(i%16); patch.sysex[ofs+1]=(byte)(i/16);}
 public int get() {return patch.sysex[ofs]+patch.sysex[ofs+1]*16 ;}

}
//Parameter Model for the Matrix1000 Parameters which are bits combined w/ other parameters
 class BitModel extends ParamModel
{
 int bit;
 public BitModel(Patch p,int o,int b) {ofs=o*2+5;patch=p;bit=b;}
 public void set(int i) {
  if (bit==0) patch.sysex[ofs]=(byte)((patch.sysex[ofs]&6)+i);
  if (bit==1) patch.sysex[ofs]=(byte)((patch.sysex[ofs]&5)+i*2);
  if (bit==2) patch.sysex[ofs]=(byte)((patch.sysex[ofs]&3)+i*4);
 }
 public int get() {
   if (bit==0) return (patch.sysex[ofs]+patch.sysex[ofs+1]*16)&1 ;
   if (bit==1) return ((patch.sysex[ofs]+patch.sysex[ofs+1]*16)&2)/2 ;
   if (bit==2) return ((patch.sysex[ofs]+patch.sysex[ofs+1]*16)&4)/4 ;
  return 0;
 }
}
//kind of a kluge: in order to send sysex msg we need to know values of all the parameters in the same bitmask as the
//current. The correct bits have already been placed in the sysexdata by the BitModel, so lets just read it outta there
//and send it out. We actually ignore the value sent in to us, cause the sysexdata already represents status w/ that change
class BitSender extends SysexSender
{
  int parameter;int ofs;core.Patch patch;
  byte []b = new byte [7];
  public BitSender(core.Patch p,int param,int o)
  {parameter=param;ofs=o*2+5;patch=p;
   b[0]=(byte)0xF0; b[1]=(byte)0x10;b[2]=(byte)0x06;b[3]=0x06; b[4]=(byte)parameter;b[6]=(byte)0xF7;
  }
  public byte [] generate (int value)
  {b[5]=(byte)patch.sysex[ofs];return b;}
}

//The Matrix1000 Detune parameter uses a unique format, basically its 5 bits of data w/ bits 6 and 7 both being the
//sign bit if the number is negative. Hence the need for seperate objects to
//manipulate it. This is a kluge done by ear, I tried reverse engineering another Matrix editor only to find it didn't
//handle detune correctly. This sounds like it does the right thing though.
 class DetuneModel extends ParamModel
{
 public DetuneModel(Patch p,int o) {ofs=o*2+5;patch=p;}
 public void set(int i) {
   i-=31;
   if (i<0)i=64+32+32+i;
   patch.sysex[ofs]=(byte)(i%16); patch.sysex[ofs+1]=(byte)(i/16);
 }
 public int get() {
   int value=patch.sysex[ofs]+patch.sysex[ofs+1]*16;

   if (value<=0x1F) return value+31;  //the number is positive
   value=value&31;   //otherwise its negative, so strip bits 6 and 7
   value--;
   return value;
 }
}
class DetuneSender extends SysexSender
{
  int parameter;
  byte []b = new byte [7];
  public DetuneSender(int param)
  {parameter=param;
   b[0]=(byte)0xF0; b[1]=(byte)0x10;b[2]=(byte)0x06;b[3]=0x06; b[4]=(byte)parameter;b[6]=(byte)0xF7;
  }
  public byte [] generate (int value)
  {  value-=31;
     if (value<0) value=32+64+(32+value);
     b[5]=(byte)value;
     return b;}

}
//Model for Modulation Matrix Amount Parameters
 class ModModel extends ParamModel
{
 public ModModel(Patch p,int o) {ofs=o*2+5;patch=p;}
 public void set(int i) {
   i-=63;
   if (i<0)i=64+128+i;
  patch.sysex[ofs]=(byte)(i%16); patch.sysex[ofs+1]=(byte)(i/16);
 }
 public int get() {
   int value=patch.sysex[ofs]+patch.sysex[ofs+1]*16;

   if (value<=63) return value+63;  //the number is positive
   value=value&63;   //otherwise its negative, so strip 7
   value--;
   return value;
 }
}
//In order to send a change to a ModMatrix entry, you gotta resend the entire entry...
class ModSender extends SysexSender
{
  int modnum; Patch patch;
  byte []b = new byte [9];
  public ModSender(core.Patch p,int m)   //m=# of mod matrix to send (0-9)
  {modnum=m;patch=p;
   b[0]=(byte)0xF0; b[1]=(byte)0x10;b[2]=(byte)0x06;b[3]=0x0B; b[4]=(byte)modnum;b[8]=(byte)0xF7;
  }
  public byte [] generate (int value)
  {
	  b[5]=(byte)get(104+modnum*3);
	  b[6]=(byte)get(105+modnum*3);
	  b[7]=(byte)get(106+modnum*3);
	  b[6]=(byte)(b[6]%64);
	  if (b[6]<0) b[6]=(byte)(128+b[6]);
	  return b;
  }
  public int get(int ofs) {int value=patch.sysex[ofs*2+5]+patch.sysex[ofs*2+5+1]*16; return value;}

}
// Sends the -63 to +63 sliders which aren't part of the mod matrix
class SliderSender extends SysexSender
{
  int parameter;
  byte []b = new byte [7];
  public SliderSender(int param)
  {parameter=param;
   b[0]=(byte)0xF0; b[1]=(byte)0x10;b[2]=(byte)0x06;b[3]=0x06; b[4]=(byte)parameter;b[6]=(byte)0xF7;
  }
  public byte [] generate (int value)
  {  value-=63;
     if (value<0) value=32+64+(32+value);
     b[5]=(byte)value;
     return b;}
}

}
