/*
 * @version $Id$
 */
package synthdrivers.RolandGP16;
import core.*;
import javax.swing.*;
import java.awt.*;

import javax.swing.border.*;

class RolandGP16SingleEditor extends PatchEditorFrame
{
  private String [] expAssChoices = new String []{"Off",
  	"Compressor-Tone", "Compressor-Attack", "Compressor-Sustain", "Compressor-Level",
	"Distortion-Tone", "Distortion-Distortion", "Distortion-Level", 
	"Overdrive-Tone", "Overdrive-Drive", "Overdrive-Turbo", "Overdrive-Level",
	"Picking Filter-Sens", "Picking Filter-Cutoff Freq", "Picking Filter-Q", "Picking Filter-Up/Down",
  	"Step Phaser-Rate", "Step Phaser-Depth", "Step Phaser-Manual", "Step Phaser-Resonance", 
		"Step Phaser-LFO Step",
	"Parametric EQ-Hi Freq", "Parametric EQ-Hi Level", "Parametric EQ-H.M Freq", "Parametric EQ-H.Mid Q",
		"Parametric EQ-H.M Lev", "Parametric EQ-L.M Freq", "Parametric EQ-L.Mid Q", 
		"Parametric EQ-L.M Lev", "Parametric EQ-Lo Freq", "Parametric EQ-Lo Level", "Parametric EQ-Out Lev",
	"Noise Suppressor-Sens", "Noise Suppressor-Release", "Noise Suppressor-Level",
	"Short Delay-D.Time", "Short Delay-E.Level", 
	"Chorus-P.Delay", "Chorus-Rate", "Chorus-Depth", "Chorus-E.Level",
	"Flanger-Rate", "Flanger-Depth", "Flanger-Manual", "Flanger-Resonance", 
	"Pitch Shifter-Balance", "Pitch Shifter-Chromatic", "Pitch Shifter-Fine", "Pitch Shifter-F.Back", 
		"Pitch Shifter-P.Delay",
	"Space-D-Mode", 
	"Auto Panpot-Rate", "Auto Panpot-Depth", "Auto Panpot-Mode", 
	"Tap Delay-C.Tap", "Tap Delay-L.Tap", "Tap Delay-R.Tap", "Tap Delay-C.Level", "Tap Delay-L.Level", 
		"Tap Delay-R.Level", "Tap Delay-F.Back", "Tap Delay-Cutoff",
	"Reverb-Decay", "Reverb-Mode", "Reverb-Cutoff", "Reverb-P.Delay", "Reverb-E.Level", 
	"Lineout Filter-Presence", "Lineout Filter-Treble", "Lineout Filter-Middle", "Lineout Filter-Bass", 
	"Master Volume"};

  private String [] jointAChoices = new String []{"Compressor", "Dist/Overdrive", "Picking Filter", "Phaser",
  	"Parametric EQ"};

  private String [] jointBChoices = new String []{"Short Delay", "Ch/Fl/PS/Sp", "Auto Panpot", "Tap Delay",
  	"Reverb"};

  private GP16ComboBoxWidget expAssWidget;
  private GP16ComboBoxWidget[] jointWidgetsA;
  private GP16ComboBoxWidget[] jointWidgetsB;
  private ExpLevScrollBarWidget expLevMaxWidget;
  private ExpLevScrollBarWidget expLevMinWidget;
  
/** The constructor sets up everything. */  
  public RolandGP16SingleEditor(Patch patch)
  {
    super ("Roland GP16 Single Editor",patch);

  int faderVal=1; int checkVal=-1;

  
  JPanel misc=new JPanel();
  misc.setLayout(new GridBagLayout());
  
  addWidget(misc,new RolandGP16PatchNameWidget("Name",patch), 0, 0, 1, 1, faderVal); faderVal++;
  addWidget(misc,new ExpLevScrollBarWidget("Master Volume",patch,0,100,0,new ParamModel(patch,0x5B+8),
  	new VSender(0x5B),1),0,1,1,1,faderVal); faderVal++;
  addWidget(misc,new GP16ComboBoxWidget("Output Channel",patch,new ParamModel(patch,0x63+8),
    	new VSender(0x63),new String[]{"Channel 1","Channel 2", "Channel 1&2"}, true),0,2,1,1,faderVal);
	faderVal++; 

  misc.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
  	"Miscellaneous",TitledBorder.CENTER,TitledBorder.CENTER));
  
	  
  JPanel joint=new JPanel();
  joint.setLayout(new GridBagLayout());
  
  gbc.anchor=GridBagConstraints.CENTER;
  
  gbc.gridy=0;
  gbc.gridx=1; joint.add(new JLabel("      Block A"),gbc);
  gbc.gridx=2; joint.add(new JLabel("      Block B"),gbc);
  
  gbc.gridx=0; 
  gbc.gridy=1; joint.add(new JLabel("Effect 1"),gbc);
  gbc.gridy=2; joint.add(new JLabel("Effect 2"),gbc);
  gbc.gridy=3; joint.add(new JLabel("Effect 3"),gbc);
  gbc.gridy=4; joint.add(new JLabel("Effect 4"),gbc);
  gbc.gridy=5; joint.add(new JLabel("Effect 5"),gbc);
  gbc.gridy=6; joint.add(new JLabel("Effect 6"),gbc);
  
  jointWidgetsA = new GP16ComboBoxWidget[5];
  jointWidgetsB = new GP16ComboBoxWidget[5];
   
  for (int dum=0;dum<5;dum++){
	jointWidgetsA[dum] = new GP16ComboBoxWidget("",patch, new ParamModel(patch,dum+8), 
		new VSender(dum), jointAChoices, false, new GP16JointPolice(patch, 0));
	jointWidgetsB[dum] = new GP16ComboBoxWidget("",patch, new BBParamModel(patch,dum+6+8), 
		new VSender(dum+6), jointBChoices, false, new GP16JointPolice(patch, 6));
  	addWidget(joint,jointWidgetsA[dum],1,dum+1,1,1,faderVal); faderVal++;
  	addWidget(joint,jointWidgetsB[dum],2,dum+1,1,1,faderVal); faderVal++;
  }
  gbc.gridx=1; gbc.gridy=6; joint.add(new JLabel("    Noise Suppressor"),gbc);
  gbc.gridx=2; gbc.gridy=6; joint.add(new JLabel("    Lineout Filter"),gbc);
    
  joint.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
  	"Effects Sequence",TitledBorder.CENTER,TitledBorder.CENTER));
  
  
  JPanel expression=new JPanel();
  expression.setLayout(new GridBagLayout());

  expAssWidget = new GP16ComboBoxWidget("Expression Assign",patch,new EParamModel(patch,0x5C+8),
  	new ESender(0x5C),expAssChoices, true);
  expLevMaxWidget = createExpLevScrollBarWidget("Expression Max Level", patch, 0x5F);
  expLevMinWidget = createExpLevScrollBarWidget("Expression Min Level", patch, 0x61);
  expAssWidget.addEventListener(expLevMaxWidget);
  expAssWidget.addEventListener(expLevMinWidget);
	    
  addWidget(expression,expAssWidget,0,0,1,1,faderVal); faderVal++;
  addWidget(expression,new GP16ComboBoxWidget("Expression Device",patch,new ParamModel(patch,0x5D+8),
    	new VSender(0x5D),new String[]{"Pedal","LFO"}, true),0,1,1,1,faderVal); faderVal++; 
  addWidget(expression,new ExpLevScrollBarWidget("LFO Rate",patch,0,100,0,new ParamModel(patch,0x5E+8),
  	new VSender(0x5E),1),0,2,1,1,faderVal); faderVal++;
  addWidget(expression,expLevMaxWidget,0,3,1,1,faderVal); faderVal++;
  addWidget(expression,expLevMinWidget,0,4,1,1,faderVal); faderVal++;
  
  expression.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
  	"Expression Pedal",TitledBorder.CENTER,TitledBorder.CENTER));
  
  
  JPanel comp = new JPanel();
  comp.setLayout(new GridBagLayout());
  
  addWidget(comp,new ExpLevScrollBarWidget("Tone",patch,0,100,-50,new ParamModel(patch,0x0F+8),
  	new VSender(0x0F),1),0,0,1,1,faderVal); faderVal++;
  addWidget(comp,new ExpLevScrollBarWidget("Attack",patch,0,100,0,new ParamModel(patch,0x10+8),
  	new VSender(0x10),1),0,1,1,1,faderVal); faderVal++;
  addWidget(comp,new ExpLevScrollBarWidget("Sustain",patch,0,100,0,new ParamModel(patch,0x11+8),
  	new VSender(0x11),1),0,2,1,1,faderVal); faderVal++;
  addWidget(comp,new ExpLevScrollBarWidget("Level",patch,0,100,0,new ParamModel(patch,0x12+8),
  	new VSender(0x12),1),0,3,1,1,faderVal); faderVal++;
  addWidget(comp,new GP16CheckBoxWidget("Effect On",patch, new GP16BitModel(patch,0x0E+8,0),
  	new GP16BitSender(patch,0x0E,0)),0,4,1,1,checkVal); checkVal--;

  comp.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
	"Compressor",TitledBorder.CENTER,TitledBorder.CENTER));
 

  JPanel distOd = new JPanel();
  distOd.setLayout(new GridBagLayout());

  addWidget(distOd,new ExpLevScrollBarWidget("Dist Tone",patch,0,100,-50,new ParamModel(patch,0x13+8),
  	new VSender(0x13),1),0,0,1,1,faderVal); faderVal++;
  addWidget(distOd,new ExpLevScrollBarWidget("Dist Dist",patch,0,100,0,new ParamModel(patch,0x14+8),
  	new VSender(0x14),1),0,1,1,1,faderVal); faderVal++;
  addWidget(distOd,new ExpLevScrollBarWidget("Dist Level",patch,0,100,0,new ParamModel(patch,0x15+8),
  	new VSender(0x15),1),0,2,1,1,faderVal); faderVal++;
  addWidget(distOd,new ExpLevScrollBarWidget("OD Tone",patch,0,100,-50,new ParamModel(patch,0x16+8),
  	new VSender(0x16),1),0,3,1,1,faderVal); faderVal++;
  addWidget(distOd,new ExpLevScrollBarWidget("OD Drive",patch,0,100,0,new ParamModel(patch,0x17+8),
  	new VSender(0x17),1),0,4,1,1,faderVal); faderVal++;
  addWidget(distOd,new GP16CheckBoxWidget("OD Turbo",patch, new ParamModel(patch,0x18+8),
  	new VSender(0x18)),0,5,1,1,checkVal); checkVal--;
  addWidget(distOd,new ExpLevScrollBarWidget("OD Level",patch,0,100,0,new ParamModel(patch,0x19+8),
  	new VSender(0x19),1),0,6,1,1,faderVal); faderVal++;
  addWidget(distOd,new GP16ComboBoxWidget("Dist / OD",patch,new GP16BitModel(patch,0x0D+8,6),
  	new GP16BitSender(patch,0x0D,6), new String[] {"Distortion", "Overdrive"}, true),0,7,1,1,faderVal);
	faderVal++;
  addWidget(distOd,new GP16CheckBoxWidget("Effect On",patch, new GP16BitModel(patch,0x0E+8,1),
  	new GP16BitSender(patch,0x0E,1)),0,8,1,1,checkVal); checkVal--;
 
 distOd.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
	"Distortion / Overdrive",TitledBorder.CENTER,TitledBorder.CENTER));
	
	
  JPanel picking = new JPanel();
  picking.setLayout(new GridBagLayout());

  addWidget(picking,new ExpLevScrollBarWidget("Sensitivity",patch,0,100,0,new ParamModel(patch,0x1A+8),
  	new VSender(0x1A),1),0,0,1,1,faderVal); faderVal++;
  addWidget(picking,new ExpLevScrollBarWidget("Cutoff Freq",patch,0,100,0,new ParamModel(patch,0x1B+8),
  	new VSender(0x1B),1),0,1,1,1,faderVal); faderVal++;
  addWidget(picking,new ExpLevScrollBarWidget("Q Value",patch,0,40,10,new ParamModel(patch,0x1C+8),
  	new VSender(0x1C),10),0,2,1,1,faderVal); faderVal++;
  addWidget(picking,new GP16ComboBoxWidget("UP / Down",patch,new ParamModel(patch,0x1D+8),
  	new VSender(0x1D), new String[] {"Down", "Up"}, true),0,3,1,1,faderVal); faderVal++;
  addWidget(picking,new GP16CheckBoxWidget("Effect On",patch, new GP16BitModel(patch,0x0E+8,2),
  	new GP16BitSender(patch,0x0E,2)),0,4,1,1,checkVal); checkVal--;
 
  picking.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
	"Picking Filter",TitledBorder.CENTER,TitledBorder.CENTER));
  
  
  JPanel step = new JPanel();
  step.setLayout(new GridBagLayout());

  addWidget(step,new ExpLevScrollBarWidget("Rate",patch,0,100,0,new ParamModel(patch,0x1E+8),
  	new VSender(0x1E),1),0,0,1,1,faderVal); faderVal++;
  addWidget(step,new ExpLevScrollBarWidget("Depth",patch,0,100,0,new ParamModel(patch,0x1F+8),
  	new VSender(0x1F),1),0,1,1,1,faderVal); faderVal++;
  addWidget(step,new ExpLevScrollBarWidget("Manual",patch,0,100,0,new ParamModel(patch,0x20+8),
  	new VSender(0x20),1),0,2,1,1,faderVal); faderVal++;
  addWidget(step,new ExpLevScrollBarWidget("Resonance",patch,0,100,0,new ParamModel(patch,0x21+8),
  	new VSender(0x21),1),0,3,1,1,faderVal); faderVal++;
  addWidget(step,new ExpLevScrollBarWidget("LFO Step",patch,0,100,0,new ParamModel(patch,0x22+8),
  	new VSender(0x22),1),0,4,1,1,faderVal); faderVal++;
  addWidget(step,new GP16CheckBoxWidget("Effect On",patch, new GP16BitModel(patch,0x0E+8,3),
  	new GP16BitSender(patch,0x0E,3)),0,5,1,1,checkVal); checkVal--;
  
  step.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
	"Step Phaser",TitledBorder.CENTER,TitledBorder.CENTER));
  
  
  JPanel eq = new JPanel();
  eq.setLayout(new GridBagLayout());

  addWidget(eq,new ExpLevScrollBarWidget("High Freq",patch,0,100,0,new ParamModel(patch,0x23+8),
  	new VSender(0x23),1),0,0,1,1,faderVal); faderVal++;
  addWidget(eq,new ExpLevScrollBarWidget("High Level",patch,0,48,-24,new ParamModel(patch,0x24+8),
  	new VSender(0x24),2),0,1,1,1,faderVal); faderVal++;
  addWidget(eq,new ExpLevScrollBarWidget("H.Mid Freq",patch,0,100,0,new ParamModel(patch,0x25+8),
  	new VSender(0x25),1),0,2,1,1,faderVal); faderVal++;
  addWidget(eq,new ExpLevScrollBarWidget("H.Mid Q",patch,0,40,10,new ParamModel(patch,0x26+8),
  	new VSender(0x26),10),0,3,1,1,faderVal); faderVal++;
  addWidget(eq,new ExpLevScrollBarWidget("H.Mid Level",patch,0,48,-24,new ParamModel(patch,0x27+8),
  	new VSender(0x27),2),0,4,1,1,faderVal); faderVal++;
  addWidget(eq,new ExpLevScrollBarWidget("L.Mid Freq",patch,0,100,0,new ParamModel(patch,0x28+8),
  	new VSender(0x28),1),0,5,1,1,faderVal); faderVal++;
  addWidget(eq,new ExpLevScrollBarWidget("L.Mid Q",patch,0,40,10,new ParamModel(patch,0x29+8),
  	new VSender(0x29),10),0,6,1,1,faderVal); faderVal++;
  addWidget(eq,new ExpLevScrollBarWidget("L.Mid Level",patch,0,48,-24,new ParamModel(patch,0x2A+8),
  	new VSender(0x2A),2),0,7,1,1,faderVal); faderVal++;
  addWidget(eq,new ScrollBarWidget("Low Freq",patch,0,100,0,new ParamModel(patch,0x2B+8),
  	new VSender(0x2B)),0,8,1,1,faderVal); faderVal++;
  addWidget(eq,new ExpLevScrollBarWidget("Low Level",patch,0,48,-24,new ParamModel(patch,0x2C+8),
  	new VSender(0x2C),2),0,9,1,1,faderVal); faderVal++;
  addWidget(eq,new ExpLevScrollBarWidget("Out Level",patch,0,48,-24,new ParamModel(patch,0x2D+8),
  	new VSender(0x2D),2),0,10,1,1,faderVal); faderVal++;
  addWidget(eq,new GP16CheckBoxWidget("Effect On",patch, new GP16BitModel(patch,0x0E+8,4),
  	new GP16BitSender(patch,0x0E,4)),0,11,1,1,checkVal); checkVal--;
  
  eq.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
	"Parametric Equaliser",TitledBorder.CENTER,TitledBorder.CENTER));
  
  
  JPanel noise = new JPanel();
  noise.setLayout(new GridBagLayout());

  addWidget(noise,new ExpLevScrollBarWidget("Sensitivity",patch,0,100,0,new ParamModel(patch,0x2E+8),
  	new VSender(0x2E),1),0,0,1,1,faderVal); faderVal++;
  addWidget(noise,new ExpLevScrollBarWidget("Release",patch,0,100,0,new ParamModel(patch,0x2F+8),
  	new VSender(0x2F),1),0,1,1,1,faderVal); faderVal++;
  addWidget(noise,new ExpLevScrollBarWidget("Level",patch,0,100,0,new ParamModel(patch,0x30+8),
  	new VSender(0x30),1),0,2,1,1,faderVal); faderVal++;
  addWidget(noise,new GP16CheckBoxWidget("Effect On",patch, new GP16BitModel(patch,0x0E+8,5),
  	new GP16BitSender(patch,0x0E,5)),0,5,1,1,checkVal); checkVal--;
  
  noise.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
	"Noise Suppressor",TitledBorder.CENTER,TitledBorder.CENTER));
  
  
  JPanel shortD = new JPanel();
  shortD.setLayout(new GridBagLayout());

  addWidget(shortD,new ExpLevScrollBarWidget("Sensitivity",patch,0,100,0,new ParamModel(patch,0x31+8),
  	new VSender(0x31),1),0,0,1,1,faderVal); faderVal++;
  addWidget(shortD,new ExpLevScrollBarWidget("Effect Level",patch,0,100,0,new ParamModel(patch,0x32+8),
  	new VSender(0x32),1),0,1,1,1,faderVal); faderVal++;
  addWidget(shortD,new GP16CheckBoxWidget("Effect On",patch, new GP16BitModel(patch,0x0E+8,6),
  	new GP16BitSender(patch,0x0E,6)),0,5,1,1,checkVal); checkVal--;
  
  shortD.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
	"Short Delay",TitledBorder.CENTER,TitledBorder.CENTER));
  
  
  JPanel cfps = new JPanel();
  cfps.setLayout(new GridBagLayout());

  addWidget(cfps,new ExpLevScrollBarWidget("Chorus P.Delay",patch,0,100,0,new ParamModel(patch,0x33+8),
  	new VSender(0x33),1),0,0,1,1,faderVal); faderVal++;
  addWidget(cfps,new ExpLevScrollBarWidget("Chorus Rate",patch,0,100,0,new ParamModel(patch,0x34+8),
  	new VSender(0x34),1),0,1,1,1,faderVal); faderVal++;
  addWidget(cfps,new ExpLevScrollBarWidget("Chorus Depth",patch,0,100,0,new ParamModel(patch,0x35+8),
  	new VSender(0x35),1),0,2,1,1,faderVal); faderVal++;
  addWidget(cfps,new ExpLevScrollBarWidget("Chorus E.Level",patch,0,100,0,new ParamModel(patch,0x36+8),
  	new VSender(0x36),1),0,3,1,1,faderVal); faderVal++;
  addWidget(cfps,new ExpLevScrollBarWidget("Flanger Rate",patch,0,100,0,new ParamModel(patch,0x37+8),
  	new VSender(0x37),1),0,4,1,1,faderVal); faderVal++;
  addWidget(cfps,new ExpLevScrollBarWidget("Flanger Depth",patch,0,100,0,new ParamModel(patch,0x38+8),
  	new VSender(0x38),1),0,5,1,1,faderVal); faderVal++;
  addWidget(cfps,new ExpLevScrollBarWidget("Flanger Manual",patch,0,100,0,new ParamModel(patch,0x39+8),
  	new VSender(0x39),1),0,6,1,1,faderVal); faderVal++;
  addWidget(cfps,new ExpLevScrollBarWidget("Flanger Resonance",patch,0,100,0,new ParamModel(patch,0x3A+8),
  	new VSender(0x3A),1),0,7,1,1,faderVal); faderVal++;
  addWidget(cfps,new ExpLevScrollBarWidget("Pitch Balance",patch,0,200,-100,new BigValParamModel(patch,0x3B+8), 
	new BigValSender(0x3B),1),0,8,1,1,faderVal); faderVal++;
  addWidget(cfps,new ExpLevScrollBarWidget("Pitch Chromatic",patch,0,24,-12,new ParamModel(patch,0x3D+8),
  	new VSender(0x3D),1),0,9,1,1,faderVal); faderVal++;
  addWidget(cfps,new ExpLevScrollBarWidget("Pitch Fine",patch,0,100,-50,new ParamModel(patch,0x3E+8),
  	new VSender(0x3E),1),0,10,1,1,faderVal); faderVal++;
  addWidget(cfps,new ExpLevScrollBarWidget("Pitch Feedback",patch,0,100,0,new ParamModel(patch,0x3F+8),
  	new VSender(0x3F),1),0,11,1,1,faderVal); faderVal++;
  addWidget(cfps,new ExpLevScrollBarWidget("Pitch Pre Delay",patch,0,100,0,new ParamModel(patch,0x40+8),
  	new VSender(0x40),1),0,12,1,1,faderVal); faderVal++;
  addWidget(cfps,new ExpLevScrollBarWidget("Space-D Mode",patch,0,3,1,new ParamModel(patch,0x41+8),
  	new VSender(0x41),1),0,13,1,1,faderVal); faderVal++;
  addWidget(cfps,new GP16ComboBoxWidget("Select Effect",patch,new ParamModel(patch,0x0C+8),
  	new VSender(0x0C), new String[] {"Chorus", "Flanger", "Pitch Shifter", "Space-D"}, true),0,14,1,1,faderVal);
	faderVal++;
  addWidget(cfps,new GP16CheckBoxWidget("Effect On",patch, new GP16BitModel(patch,0x0D+8,0),
  	new GP16BitSender(patch,0x0D,0)),0,15,1,1,checkVal); checkVal--;
  
  cfps.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
	"Chorus / Flanger / Pitch Shifter / Space-D",TitledBorder.CENTER,TitledBorder.CENTER));
  
  
  JPanel auto = new JPanel();
  auto.setLayout(new GridBagLayout());

  addWidget(auto,new ExpLevScrollBarWidget("Rate",patch,0,100,0,new ParamModel(patch,0x42+8),
  	new VSender(0x42),1),0,0,1,1,faderVal); faderVal++;
  addWidget(auto,new ExpLevScrollBarWidget("Depth",patch,0,100,0,new ParamModel(patch,0x43+8),
  	new VSender(0x43),1),0,1,1,1,faderVal); faderVal++;
  addWidget(auto,new GP16ComboBoxWidget("Mode",patch,new ParamModel(patch,0x44+8),
  	new VSender(0x44), new String[] {"Panning", "Tremolo"}, true),0,2,1,1,faderVal); faderVal++;
  addWidget(auto,new GP16CheckBoxWidget("Effect On",patch, new GP16BitModel(patch,0x0D+8,1),
  	new GP16BitSender(patch,0x0D,1)),0,3,1,1,checkVal); checkVal--;
  
  auto.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
	"Auto Panpot",TitledBorder.CENTER,TitledBorder.CENTER));
  
  
  JPanel delay = new JPanel();
  delay.setLayout(new GridBagLayout());

  addWidget(delay,new ExpLevScrollBarWidget("Center Tap",patch,0,1200,0,new BigValParamModel(patch,0x45+8), 
	new BigValSender(0x45),1),0,0,1,1,faderVal); faderVal++;
  addWidget(delay,new ExpLevScrollBarWidget("Left Tap",patch,0,1200,0,new BigValParamModel(patch,0x47+8), 
	new BigValSender(0x47),1),0,1,1,1,faderVal); faderVal++;
  addWidget(delay,new ExpLevScrollBarWidget("Right Tap",patch,0,1200,0,new BigValParamModel(patch,0x49+8), 
	new BigValSender(0x49),1),0,2,1,1,faderVal); faderVal++;
  addWidget(delay,new ExpLevScrollBarWidget("Center Level",patch,0,100,0,new ParamModel(patch,0x4B+8),
  	new VSender(0x4B),1),0,3,1,1,faderVal); faderVal++;
  addWidget(delay,new ExpLevScrollBarWidget("Left Level",patch,0,100,0,new ParamModel(patch,0x4C+8),
  	new VSender(0x4C),1),0,4,1,1,faderVal); faderVal++;
  addWidget(delay,new ExpLevScrollBarWidget("Right Level",patch,0,100,0,new ParamModel(patch,0x4D+8),
  	new VSender(0x4D),1),0,5,1,1,faderVal); faderVal++;
  addWidget(delay,new ExpLevScrollBarWidget("Feedback",patch,0,100,0,new ParamModel(patch,0x4E+8),
  	new VSender(0x4E),1),0,6,1,1,faderVal); faderVal++;
  addWidget(delay,new ExpLevScrollBarWidget("Cutoff",patch,0,200,0,new BigValParamModel(patch,0x4F+8), 
	new BigValSender(0x4F),1),0,7,1,1,faderVal); faderVal++;
  addWidget(delay,new GP16CheckBoxWidget("Effect On",patch, new GP16BitModel(patch,0x0D+8,2),
  	new GP16BitSender(patch,0x0D,2)),0,8,1,1,checkVal); checkVal--;
  
  delay.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
	"Tap Delay",TitledBorder.CENTER,TitledBorder.CENTER));
  
  
  JPanel reverb = new JPanel();
  reverb.setLayout(new GridBagLayout());

  addWidget(reverb,new ExpLevScrollBarWidget("Decay",patch,0,75,0,new ParamModel(patch,0x51+8),
  	new VSender(0x51),1),0,0,1,1,faderVal); faderVal++;
  addWidget(reverb,new GP16ComboBoxWidget("Mode",patch,new ParamModel(patch,0x52+8),
  	new VSender(0x52), new String[] {"Room 1", "Room 2", "Room 3", "Hall 1", "Hall 2", 
		"Hall 3", "Plate 1", "Plate 2", "Spring 1", "Spring 2"}, true),0,1,1,1,faderVal); faderVal++;
  addWidget(reverb,new ExpLevScrollBarWidget("Cutoff",patch,0,200,0,new BigValParamModel(patch,0x53+8), 
	new BigValSender(0x53),1),0,2,1,1,faderVal); faderVal++;
  addWidget(reverb,new ExpLevScrollBarWidget("Pre Delay",patch,0,100,0,new ParamModel(patch,0x55+8),
  	new VSender(0x55),1),0,3,1,1,faderVal); faderVal++;
  addWidget(reverb,new ExpLevScrollBarWidget("Effect Level",patch,0,100,0,new ParamModel(patch,0x56+8),
  	new VSender(0x56),1),0,4,1,1,faderVal); faderVal++;
  addWidget(reverb,new GP16CheckBoxWidget("Effect On",patch, new GP16BitModel(patch,0x0D+8,3),
  	new GP16BitSender(patch,0x0D,3)),0,5,1,1,checkVal); checkVal--;
  
  reverb.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
	"Reverb",TitledBorder.CENTER,TitledBorder.CENTER));
  
  
  JPanel lineout = new JPanel();
  lineout.setLayout(new GridBagLayout());

  addWidget(lineout,new ExpLevScrollBarWidget("Presence",patch,0,100,0,new ParamModel(patch,0x57+8),
  	new VSender(0x57),1),0,0,1,1,faderVal); faderVal++;
  addWidget(lineout,new ExpLevScrollBarWidget("Treble",patch,0,100,0,new ParamModel(patch,0x58+8),
  	new VSender(0x58),1),0,1,1,1,faderVal); faderVal++;
  addWidget(lineout,new ExpLevScrollBarWidget("Middle",patch,0,100,0,new ParamModel(patch,0x59+8),
  	new VSender(0x59),1),0,2,1,1,faderVal); faderVal++;
  addWidget(lineout,new ExpLevScrollBarWidget("Bass",patch,0,100,0,new ParamModel(patch,0x5A+8),
  	new VSender(0x5A),1),0,3,1,1,faderVal); faderVal++;
  addWidget(lineout,new GP16CheckBoxWidget("Effect On",patch, new GP16BitModel(patch,0x0D+8,4),
  	new GP16BitSender(patch,0x0D,4)),0,4,1,1,checkVal); checkVal--;
  
  lineout.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
	"Lineout Filter",TitledBorder.CENTER,TitledBorder.CENTER));
  
  
  JPanel general = new JPanel();
  general.setLayout(new GridBagLayout());
  
  general.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
  	"General Settings",TitledBorder.CENTER,TitledBorder.CENTER));
  
  gbc.weightx=0.5; gbc.weighty=0.5; 
  gbc.gridx=0; gbc.gridy=0; general.add(misc,gbc);
  gbc.gridx=0; gbc.gridy=1; general.add(joint,gbc);
  gbc.gridx=0; gbc.gridy=2; general.add(expression,gbc);

  
  JPanel leftA = new JPanel();
  leftA.setLayout(new GridBagLayout());
  JPanel rightA = new JPanel();
  rightA.setLayout(new GridBagLayout());

  gbc.weightx=0.5; gbc.weighty=0.5; 
  gbc.gridx=0; gbc.gridy=0; leftA.add(comp,gbc);
  gbc.gridx=0; gbc.gridy=1; leftA.add(distOd,gbc);
  gbc.gridx=0; gbc.gridy=2; leftA.add(picking,gbc);
  gbc.gridx=0; gbc.gridy=0; rightA.add(step,gbc);
  gbc.gridx=0; gbc.gridy=1; rightA.add(eq,gbc);
  gbc.gridx=0; gbc.gridy=2; rightA.add(noise,gbc);
  
  JPanel blockA = new JPanel();
  blockA.setLayout(new GridBagLayout());
  
  gbc.weightx=0.5; gbc.weighty=0.5; 
  gbc.gridx=0; gbc.gridy=0; blockA.add(leftA,gbc);
  gbc.gridx=1; gbc.gridy=0; blockA.add(rightA,gbc);
  
  
  JPanel leftB = new JPanel();
  leftB.setLayout(new GridBagLayout());
  JPanel rightB = new JPanel();
  rightB.setLayout(new GridBagLayout());

  gbc.weightx=0.5; gbc.weighty=0.5; 
  gbc.gridx=0; gbc.gridy=0; leftB.add(cfps,gbc);
  gbc.gridx=0; gbc.gridy=1; leftB.add(reverb,gbc);
  gbc.gridx=0; gbc.gridy=0; rightB.add(shortD,gbc);
  gbc.gridx=0; gbc.gridy=1; rightB.add(auto,gbc);
  gbc.gridx=0; gbc.gridy=2; rightB.add(delay,gbc);
  gbc.gridx=0; gbc.gridy=3; rightB.add(lineout,gbc);
  
  JPanel blockB = new JPanel();
  blockB.setLayout(new GridBagLayout());

  gbc.weightx=0.5; gbc.weighty=0.5; 
  gbc.gridx=0; gbc.gridy=0; blockB.add(leftB,gbc);
  gbc.gridx=1; gbc.gridy=0; blockB.add(rightB,gbc);

    
  JTabbedPane tabPane = new JTabbedPane();
  tabPane.addTab("General Settings",general);
  tabPane.addTab("Block A",blockA);
  tabPane.addTab("Block B",blockB);
  
  scrollPane.add(tabPane,gbc);
     
  
  pack();
  }

/** Helper function for initialising the expression min/max levels. See ExpLevScrollBar for comments.*/
  private ExpLevScrollBarWidget createExpLevScrollBarWidget(String l, Patch p, int adress){
	  int chosen = (expAssWidget.cb).getSelectedIndex()-1;
		
	  switch (chosen) {
	      case -1: return new ExpLevScrollBarWidget(l, p, 0, 0, 0, new BigValParamModel(p,adress+8), 
			new BigValSender(adress), 1); 
	      case 0: case 4: case 7: case 46: return new ExpLevScrollBarWidget(l, p, 0, 100, -50, 
	    	new BigValParamModel(p,adress+8), new BigValSender(adress), 1); 
              case 1: case 2: case 3: case 5: case 6: case 8: case 10: case 11: case 12: case 15:
		case 16: case 17: case 18: case 19: case 20: case 22: case 25: case 28: case 31:
		case 32: case 33: case 34: case 35: case 36: case 37: case 38: case 39: case 40:
		case 41: case 42: case 43: case 47: case 48: case 50: case 51: case 56: case 57:
		case 58: case 59: case 64: case 65: case 66: case 67: case 68: case 69: case 70:
		return new ExpLevScrollBarWidget(l, p, 0, 100, 0, new BigValParamModel(p,adress+8), 
			new BigValSender(adress), 1);
	      case 9: case 14: case 52: return new ExpLevScrollBarWidget(l, p, 0, 1, 0, 
	    	new BigValParamModel(p,adress+8), new BigValSender(adress), 1); 
	      case 13: case 23: case 26: return new ExpLevScrollBarWidget(l, p, 0, 40, 10, 
	    	new BigValParamModel(p,adress+8), new BigValSender(adress), 10); 
	      case 21: case 24: case 27: case 29: case 30: return new ExpLevScrollBarWidget(l, p, 0, 48, -24, 
	    	new BigValParamModel(p,adress+8), new BigValSender(adress), 2); 
	      case 44: return new ExpLevScrollBarWidget(l, p, 0, 200, -100, new BigValParamModel(p,adress+8), 
			new BigValSender(adress), 1); 
	      case 45: return new ExpLevScrollBarWidget(l, p, 0, 24, -12, new BigValParamModel(p,adress+8), 
			new BigValSender(adress), 1); 
	      case 49: return new ExpLevScrollBarWidget(l, p, 0, 3, 0, new BigValParamModel(p,adress+8), 
			new BigValSender(adress), 1); 
	      case 53: case 54: case 55: return new ExpLevScrollBarWidget(l, p, 0, 1200, 0, 
	    	new BigValParamModel(p,adress+8), new BigValSender(adress), 1); 
	      case 60: case 63: return new ExpLevScrollBarWidget(l, p, 0, 200, 0, new BigValParamModel(p,adress+8), 
			new BigValSender(adress), 1); 
	      case 61: return new ExpLevScrollBarWidget(l, p, 0, 75, 0, new BigValParamModel(p,adress+8), 
			new BigValSender(adress), 1); 
	      case 62: return new ExpLevScrollBarWidget(l, p, 0, 9, 0, new BigValParamModel(p,adress+8), 
			new BigValSender(adress), 1); 
	  }
	return null;
	}
}

