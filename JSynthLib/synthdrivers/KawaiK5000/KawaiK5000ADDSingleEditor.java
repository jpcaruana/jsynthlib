/*
 * @version $Id$
 */
package synthdrivers.KawaiK5000;
import core.*;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import javax.swing.border.*;

class KawaiK5000ADDSingleEditor extends PatchEditorFrame
{
  final String [] noteName = new String [] {
                                            "C#-2","D-2","D#-2","E-2","F-2","F#-2","G-2","G#-2","A-2","A#-2","B-2",
                                            "C-1","C#-1","D-1","D#-1","E-1","F-1","F#-1","G-1","G#-1","A-1","A#-1","B-1",
                                            "C0","C#0","D0","D#0","E0","F0","F#0","G0","G#0","A0","A#0","B0",
                                            "C1","C#1","D1","D#1","E1","F1","F#1","G1","G#1","A1","A#1","B1",
                                            "C2","C#2","D2","D#2","E2","F2","F#2","G2","G#2","A2","A#2","B2",
                                            "C3","C#3","D3","D#3","E3","F3","F#3","G3","G#3","A3","A#3","B3",
                                            "C4","C#4","D4","D#4","E4","F4","F#4","G4","G#4","A4","A#4","B4",
                                            "C5","C#5","D5","D#5","E5","F5","F#5","G5","G#5","A5","A#5","B5",
                                            "C6","C#6","D6","D#6","E6","F6","F#6","G6","G#6","A6","A#6","B6",
                                            "C7","C#7","D7","D#7","E7","F7","F#7","G7","G#7","A7","A#7","B7",
                                            "C8","C#8","D8","D#8"
                                            
  };    
  final String [] macroList = new String [] {"Pitch","Cutoff","Level","Vibrato Depth","Growl Depth","Tremolo Depth",
	  				     "LFO Speed","Attack Time","Decay 1 Time","Release Time","Velocity",
					     "Resonance","Panpot","FF Bias","FF Env/LFO Depth","FF Env/LFO Speed",
  				             "Harmonic Lo","Harmonic Hi","Harmonic Even","Harmonic Odd"};
 					     
  final String [] switchList = new String [] {"Off","Harm Max","Harm Bright","Harm Dark","Harm Saw","Select Loud",
	  				      "Add Loud","Add 5th","Add Odd","Add Even","HE #1","HE #2","HE Loop",
  				              "FF Max","FF Comb","FF Hi Cut","FF Comb 2"};
  final String [] waveName = new String [] {"342 Piano Noise Attack",
        "343 EP Noise Attack" ,       "344 Percus Noise Attack",
        "345 Dist Gtr Noise Attack" , "346 Orch Noise Attack",
        "347 Flanged Noise Attack"  , "348 Saw Noise Attack",
        "349 Zipper Noise Attack"   , "350 Organ Noise Looped",
        "351 Violin Noise Looped"   , "352 Crystal Noise Looped",
        "353 Sax Breath Looped"     , "354 Panflute Noise Looped",
        "355 Pipe Noise Looped"     , "356 Saw Noise Looped",
        "357 Gorgo Noise Looped"    , "358 Enhancer Noise Looped",
        "359 Tabla Spectrum Noise Looped","360 Cave Spectrum Noise Looped",
        "361 White Noise Looped"    , "362 Clavi Attack",
        "363 Digi EP Attack"        , "364 Glocken Attack",
        "365 Vibe Attack"           , "366 Marimba Attack",
        "367 Org Key Click"         , "368 Slap Bass Attack",
        "369 Folk Guitar Attack"    , "370 Gut Gtr Attack",
        "371 Dist Gtr Attack"       , "372 Clean Gtr Attack",
        "373 Muted Gtr Attack"      , "374 Cello & Violin Attack",
        "375 Pizz Violin Attack"    , "376 Pizz Double Bass Attack",
        "377 Doo Attack"            , "378 Trombone Attack",
        "379 Brass Attack"          , "380 F. Horn 1 Attack",
        "381 F. Horn 2 Attack"      , "382 Flute Attack",
        "383 T. Sax Attack"         , "384 Shamisen Attack"
  };

  final JPanel srcPanel[] = new JPanel[6];
  Patch p;
  public KawaiK5000ADDSingleEditor(Patch patch)
  {
    super ("Kawai K5000 Single Editor",patch);   
    p=patch;
// Common Pane
  final JTabbedPane tabPane=new JTabbedPane();
  JPanel cmnPanel = new JPanel();
  cmnPanel.setLayout(new GridBagLayout());
  //modPanel.setLayout(new GridBagLayout()); 
  tabPane.addTab("Common",cmnPanel);gbc.weightx=1;
 gbc.weightx=5;
  JPanel cmn2Panel = new JPanel();
  cmn2Panel.setLayout(new GridBagLayout());
  addWidget(cmn2Panel,new PatchNameWidget(" Name  ", patch, ((Driver) patch.getDriver()).getPatchNameSize()),0,0,2,1,0);
  addWidget(cmn2Panel,new ScrollBarWidget("Volume",patch,0,127,0,new K5kCmnModel(patch,48),new K5kCmnSender(8)),0,1,2,1,1);
  final ScrollBarWidget numSources = new ScrollBarWidget("Num Sources",patch,2,6,0,new K5kCmnModel(patch,51),new K5kCmnSender(0x0B));
  addWidget(cmn2Panel,numSources,0,2,2,1,2);

    numSources.addEventListener(new ChangeListener() {
	   public void stateChanged(ChangeEvent e) {
                int i=numSources.getValue();

                for (int j = i; j<6; j++)
                  tabPane.remove(srcPanel[j]);

                for (int j = 0; j<i ; j++)
                  tabPane.addTab("Source "+(j+1),srcPanel[j]);
               }});


  addWidget(cmn2Panel,new ComboBoxWidget("Poly Mode",patch,new K5kCmnModel(patch,49),new K5kCmnSender(9),new String []   {"POLY","SOLO1","SOLO2"}),0,3,1,1,3);
  addWidget(cmn2Panel,new ComboBoxWidget("AM",patch,new K5kCmnModel(patch,53),new K5kCmnSender(0x0D),new String []   {"None","Source 2","Source 3","Source 4","Source 5","Source 6"}),1,3,1,1,4);
  addWidget(cmn2Panel,new ScrollBarWidget("Portamento Speed",patch,0,127,0,new K5kCmnModel(patch,61),new K5kCmnSender(0x15)),0,4,2,1,2);
  addWidget(cmn2Panel,new CheckBoxWidget("Portamento On",patch,new K5kCmnModel(patch,60),new K5kCmnSender(0x14)),0,5,2,1,-1);

  gbc.gridx=0;gbc.gridy=0;gbc.gridwidth=1;gbc.gridheight=3;gbc.fill=GridBagConstraints.NONE;gbc.anchor=GridBagConstraints.WEST;
  cmn2Panel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"General",TitledBorder.CENTER,TitledBorder.CENTER));  
  
   cmnPanel.add(cmn2Panel,gbc);
  JPanel macro1Panel = new JPanel();
  macro1Panel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Macro Controller 1",TitledBorder.CENTER,TitledBorder.CENTER));  
  macro1Panel.setLayout(new GridBagLayout());
  addWidget(macro1Panel,new ComboBoxWidget("Param 1",patch,new K5kCmnModel(patch,62),new K5kCmnSender(0x16),macroList),0,4,1,1,5);
  addWidget(macro1Panel,new ScrollBarWidget("Depth",patch,33,95,-64,new K5kCmnModel(patch,70),new K5kCmnSender(0x1E)),1,4,2,1,1);
  addWidget(macro1Panel,new ComboBoxWidget("Param 2",patch,new K5kCmnModel(patch,63),new K5kCmnSender(0x17),macroList),0,5,1,1,5);
  addWidget(macro1Panel,new ScrollBarWidget("Depth",patch,33,95,-64,new K5kCmnModel(patch,71),new K5kCmnSender(0x1F)),1,5,2,1,1);

  gbc.gridx=0;gbc.gridy=4;gbc.gridwidth=4;gbc.gridheight=3;gbc.fill=GridBagConstraints.BOTH;gbc.anchor=GridBagConstraints.EAST;
  cmnPanel.add(macro1Panel,gbc);
  
  JPanel macro2Panel = new JPanel();
  macro2Panel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Macro Controller 2",TitledBorder.CENTER,TitledBorder.CENTER));  
  macro2Panel.setLayout(new GridBagLayout());
  addWidget(macro2Panel,new ComboBoxWidget("Param 1",patch,new K5kCmnModel(patch,64),new K5kCmnSender(0x18),macroList),0,6,1,1,5);
  addWidget(macro2Panel,new ScrollBarWidget("Depth",patch,33,95,-64,new K5kCmnModel(patch,72),new K5kCmnSender(0x20)),1,6,2,1,1);
  addWidget(macro2Panel,new ComboBoxWidget("Param 2",patch,new K5kCmnModel(patch,65),new K5kCmnSender(0x19),macroList),0,7,1,1,5);
  addWidget(macro2Panel,new ScrollBarWidget("Depth",patch,33,95,-64,new K5kCmnModel(patch,73),new K5kCmnSender(0x21)),1,7,2,1,1);
 
  gbc.gridx=0;gbc.gridy=7;gbc.gridwidth=4;gbc.gridheight=3;gbc.fill=GridBagConstraints.BOTH;gbc.anchor=GridBagConstraints.EAST;
  cmnPanel.add(macro2Panel,gbc);
  
  JPanel macro3Panel = new JPanel();
  macro3Panel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Macro Controller 3",TitledBorder.CENTER,TitledBorder.CENTER));  
  macro3Panel.setLayout(new GridBagLayout());
  addWidget(macro3Panel,new ComboBoxWidget("Param 1",patch,new K5kCmnModel(patch,66),new K5kCmnSender(0x1A),macroList),0,8,1,1,5);
  addWidget(macro3Panel,new ScrollBarWidget("Depth",patch,33,95,-64,new K5kCmnModel(patch,74),new K5kCmnSender(0x22)),1,8,2,1,1);
  addWidget(macro3Panel,new ComboBoxWidget("Param 2",patch,new K5kCmnModel(patch,67),new K5kCmnSender(0x1B),macroList),0,9,1,1,5);
  addWidget(macro3Panel,new ScrollBarWidget("Depth",patch,33,95,-64,new K5kCmnModel(patch,75),new K5kCmnSender(0x23)),1,9,2,1,1);
  gbc.gridx=0;gbc.gridy=10;gbc.gridwidth=4;gbc.gridheight=3;gbc.fill=GridBagConstraints.BOTH;gbc.anchor=GridBagConstraints.EAST;
  cmnPanel.add(macro3Panel,gbc);
  
  JPanel macro4Panel = new JPanel();
  macro4Panel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Macro Controller 4",TitledBorder.CENTER,TitledBorder.CENTER));  
  macro4Panel.setLayout(new GridBagLayout());
  addWidget(macro4Panel,new ComboBoxWidget("Param 1",patch,new K5kCmnModel(patch,68),new K5kCmnSender(0x1C),macroList),0,10,1,1,5);
  addWidget(macro4Panel,new ScrollBarWidget("Depth",patch,33,95,-64,new K5kCmnModel(patch,76),new K5kCmnSender(0x24)),1,10,2,1,1);
  addWidget(macro4Panel,new ComboBoxWidget("Param 2",patch,new K5kCmnModel(patch,69),new K5kCmnSender(0x1D),macroList),0,11,1,1,5);
  addWidget(macro4Panel,new ScrollBarWidget("Depth",patch,33,95,-64,new K5kCmnModel(patch,77),new K5kCmnSender(0x25)),1,11,2,1,1);
  gbc.gridx=0;gbc.gridy=13;gbc.gridwidth=4;gbc.gridheight=3;gbc.fill=GridBagConstraints.BOTH;gbc.anchor=GridBagConstraints.EAST;
  cmnPanel.add(macro4Panel,gbc);

  JPanel switchPanel = new JPanel();
  switchPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Switches",TitledBorder.CENTER,TitledBorder.CENTER));  
  switchPanel.setLayout(new GridBagLayout());
  addWidget(switchPanel,new ComboBoxWidget("      Switch 1",patch,new K5kCmnModel(patch,78),new K5kCmnSender(0x26),switchList),0,1,2,1,5);
  addWidget(switchPanel,new ComboBoxWidget("      Switch 2",patch,new K5kCmnModel(patch,79),new K5kCmnSender(0x27),switchList),0,2,2,1,5);
  addWidget(switchPanel,new ComboBoxWidget("Foot Switch 1",patch,new K5kCmnModel(patch,80),new K5kCmnSender(0x28),switchList),0,3,2,1,5);
  addWidget(switchPanel,new ComboBoxWidget("Foot Switch 2",patch,new K5kCmnModel(patch,81),new K5kCmnSender(0x29),switchList),0,4,2,1,5);
  gbc.gridx=2;gbc.gridy=0;gbc.gridwidth=2;gbc.gridheight=3;gbc.fill=GridBagConstraints.BOTH;gbc.anchor=GridBagConstraints.WEST;
  cmnPanel.add(switchPanel,gbc);

  
   gbc.gridx=0;gbc.gridy=0;gbc.gridwidth=5;gbc.gridheight=3;gbc.fill=GridBagConstraints.BOTH;
   gbc.anchor=GridBagConstraints.NORTH;
   cmnPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Common",TitledBorder.CENTER,TitledBorder.CENTER));  
   scrollPane.add(tabPane,gbc);
   for (int i=0;i<6;i++)
     {
       srcPanel[i]=createSourcePanel(i);
       if (patch.sysex[60]>i) tabPane.addTab("Source "+(i+1),srcPanel[i]);
     }

   pack();
   show();
 }

public JPanel createSourcePanel(int src)
{
  JPanel panel = new JPanel();
  gbc.gridx=0;gbc.gridy=0;gbc.gridwidth=2;gbc.gridheight=3;gbc.fill=GridBagConstraints.BOTH;gbc.anchor=GridBagConstraints.WEST;
  panel.setLayout(new GridBagLayout());
  final JTabbedPane tabPane = new JTabbedPane();
  JPanel srcPane = new JPanel();
  JPanel addPane = new JPanel();
  tabPane.addTab("Source",srcPane);
  tabPane.addTab("Additive",addPane);
  srcPane.setLayout(new GridBagLayout());
  JPanel cmnPane = new JPanel();
  cmnPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Common",TitledBorder.CENTER,TitledBorder.CENTER));  

  addWidget(cmnPane,new ComboBoxWidget("Zone Low",p,new K5kSrcModel(p,src,1),new K5kSrcSender(src,0),noteName),0,0,1,1,5);
  addWidget(cmnPane,new ComboBoxWidget("Zone Hi",p,new K5kSrcModel(p,src,2),new K5kSrcSender(src,1),noteName),1,0,1,1,5);



  srcPane.add(cmnPane,gbc);
  gbc.gridx=0;gbc.gridy=0;gbc.gridwidth=2;gbc.gridheight=3;gbc.fill=GridBagConstraints.BOTH;gbc.anchor=GridBagConstraints.WEST;
  

  addPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Common",TitledBorder.CENTER,TitledBorder.CENTER));

  panel.add(tabPane,gbc);
  return panel;


}



}

class K5kCmnSender extends SysexSender
{
  int parameter; 
  byte []b = new byte [14];
  public K5kCmnSender(int param) 
  {parameter=param;
   b[0]=(byte)0xF0; b[1]=(byte)0x40;b[3]=(byte)0x10;b[4]=0; b[5]=0x0A;
   b[6]=01;b[7]=0;b[8]=0;b[9]=0;b[10]=((byte)parameter);
   b[13]=(byte)0xF7;
  }
  public byte [] generate (int value)
  {
     b[11]=(byte)(value/128);
     b[12]=(byte)(value&127);
     b[2]=(byte)(channel-1);
     return b;}
}


class K5kSrcSender extends SysexSender
{
  int parameter; 
  byte []b = new byte [14];
  public K5kSrcSender(int src,int param) 
  {parameter=param;
   b[0]=(byte)0xF0; b[1]=(byte)0x40;b[3]=(byte)0x10;b[4]=0; b[5]=0x0A;
   b[6]=01;b[7]=0;b[8]=((byte)(src-1));b[9]=0;b[10]=((byte)parameter);
   b[13]=(byte)0xF7;
  }
  public byte [] generate (int value)
  {
     b[11]=(byte)(value/128);
     b[12]=(byte)(value&127);
     b[2]=(byte)(channel-1);
     return b;}
}


 class K5kCmnModel extends ParamModel
{ 
 public K5kCmnModel(Patch p,int o) {ofs=o+9;patch=p;}
 public void set(int i) {patch.sysex[ofs]=(byte)(i);}
 public int get() {return (patch.sysex[ofs]);}
}

 class K5kSrcModel extends ParamModel
{
  public K5kSrcModel(Patch p, int src, int o) {ofs=91-1+o+86*(src-1);patch=p;}
  public void set(int i) {patch.sysex[ofs]=(byte)(i);}
  public int get() {return (patch.sysex[ofs]);}

}

  class K5kVelSwModel extends ParamModel
{
  int part;
  //pt=0 means we are doing the "T" part in manual, pt=1 is "V"
  public K5kVelSwModel (Patch p, int src,int pt)
   {
     ofs=91-1+3+86*(src-1);patch=p;part=pt;
   }
  public void set(int i)
    {
      if (part==0)
        patch.sysex[ofs]=((byte)(i*32+(get() & 31)));
      else
        patch.sysex[ofs]=((byte)(i+(get() &(127-31))));
    }
  public int get()
   {
     if (part==0) return (patch.sysex[ofs] & (127-31) /32)  ;
     else return (patch.sysex[ofs] &31);
   }                         
}


//this code needs to be altered to handle the bitfields
class K5kVelSwSender extends SysexSender
{
  int parameter; 
  byte []b = new byte [14];
  public K5kVelSwSender(int src,int param) 
  {parameter=4;
   b[0]=(byte)0xF0; b[1]=(byte)0x40;b[3]=(byte)0x10;b[4]=0; b[5]=0x0A;
   b[6]=01;b[7]=0;b[8]=((byte)(src-1));b[9]=0;b[10]=((byte)parameter);
   b[13]=(byte)0xF7;
  }
  public byte [] generate (int value)
  {
     b[11]=(byte)(value/128);
     b[12]=(byte)(value&127);
     b[2]=(byte)(channel-1);
     return b;}
}


