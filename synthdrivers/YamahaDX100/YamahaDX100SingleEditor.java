/*
 * This editor is a modified version of the YamahaTX81z Editor
 * @version $Id$
 */
package synthdrivers.YamahaDX100;
import core.*;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;
import javax.swing.event.*;

import org.jsynthlib.core.CheckBoxWidget;
import org.jsynthlib.core.ComboBoxWidget;
import org.jsynthlib.core.EnvelopeWidget;
import org.jsynthlib.core.ParamModel;
import org.jsynthlib.core.Patch;
import org.jsynthlib.core.PatchEditorFrame;
import org.jsynthlib.core.PatchNameWidget;
import org.jsynthlib.core.ScrollBarWidget;
import org.jsynthlib.core.SysexSender;
class YamahaDX100SingleEditor extends PatchEditorFrame
{
  ImageIcon algoIcon[]=new ImageIcon[8];
  public YamahaDX100SingleEditor(Patch patch)
  {
    super ("Yamaha DX21/27/100 Single Editor",patch);
  algoIcon[0]=new ImageIcon(getClass().getResource("1.gif"));
  algoIcon[1]=new ImageIcon(getClass().getResource("2.gif"));
  algoIcon[2]=new ImageIcon(getClass().getResource("3.gif"));
  algoIcon[3]=new ImageIcon(getClass().getResource("4.gif"));
  algoIcon[4]=new ImageIcon(getClass().getResource("5.gif"));
  algoIcon[5]=new ImageIcon(getClass().getResource("6.gif"));
  algoIcon[6]=new ImageIcon(getClass().getResource("7.gif"));
  algoIcon[7]=new ImageIcon(getClass().getResource("8.gif"));
  final JLabel l=new JLabel(algoIcon[patch.sysex[99-41]]);

  JPanel lfoPane=new JPanel();
  lfoPane.setLayout(new GridBagLayout());gbc.weightx=1;
  addWidget(lfoPane,new ScrollBarWidget("LFO Speed",patch,0,99,0,new ParamModel(patch,101-41),new VcedSender(54)),0,0,7,1,20);
  addWidget(lfoPane,new ScrollBarWidget("LFO Delay",patch,0,99,0,new ParamModel(patch,102-41),new VcedSender(55)),0,1,7,1,21);
  addWidget(lfoPane,new ScrollBarWidget("Pitch Mod Depth",patch,0,99,0,new ParamModel(patch,103-41),new VcedSender(56)),0,2,7,1,22);
  addWidget(lfoPane,new ScrollBarWidget("Pitch Mod Sensitivity",patch,0,7,0,new ParamModel(patch,107-41),new VcedSender(60)),0,3,7,1,23);
  addWidget(lfoPane,new ScrollBarWidget("Amplitude Mod Depth",patch,0,99,0,new ParamModel(patch,104-41),new VcedSender(57)),0,4,7,1,24);
  addWidget(lfoPane,new ScrollBarWidget("Amplitude Mod Sensitivity",patch,0,3,0,new ParamModel(patch,108-41),new VcedSender(61)),0,5,7,1,25);
  addWidget(lfoPane,new CheckBoxWidget("LFO Sync",patch,new ParamModel(patch,105-41),new VcedSender(58)),1,6,1,1,-18);
  addWidget(lfoPane,new ComboBoxWidget("LFO Wave",patch,new ParamModel(patch,106-41),new VcedSender(59),new String []{"Saw Up","Square",
	   							           "Triangle","S/Hold"}),0,6,1,1,26);
  gbc.gridx=2;gbc.gridy=6;gbc.gridwidth=1;gbc.gridheight=1;
  lfoPane.add(new JLabel(" AM->Osc "),gbc);
  addWidget(lfoPane,new CheckBoxWidget("1",patch,new ParamModel(patch,-41+55+3*13),new VcedSender(3*13+8)),3,6,1,1,-19);
  addWidget(lfoPane,new CheckBoxWidget("2",patch,new ParamModel(patch,-41+55+1*13),new VcedSender(1*13+8)),4,6,1,1,-20);
  addWidget(lfoPane,new CheckBoxWidget("3",patch,new ParamModel(patch,-41+55+2*13),new VcedSender(2*13+8)),5,6,1,1,-21);
  addWidget(lfoPane,new CheckBoxWidget("4",patch,new ParamModel(patch,-41+55+0*13),new VcedSender(0*13+8)),6,6,1,1,-22);
  gbc.gridx=0;gbc.gridy=9;gbc.gridwidth=3;gbc.gridheight=4;
  lfoPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"LFO Settings",TitledBorder.CENTER,TitledBorder.CENTER));
  scrollPane.add(lfoPane,gbc);

  JPanel cmnPane= new JPanel();
  cmnPane.setLayout(new GridBagLayout());
  final ScrollBarWidget algo=new ScrollBarWidget(" Algorithm",patch,0,7,1,new ParamModel(patch,-41+99),new VcedSender(52));
  addWidget(cmnPane,algo,1,0,3,1,17);

    algo.addEventListener(new ChangeListener() {
	   public void stateChanged(ChangeEvent e) {
                l.setIcon(algoIcon[algo.getValue()]);
               }});



  addWidget(cmnPane,new ScrollBarWidget(" Feedback",patch,0,7,0,new ParamModel(patch,-41+100),new VcedSender(53)),1,1,3,1,18);
  addWidget(cmnPane,new ScrollBarWidget(" Transpose",patch,0,48,-24,new ParamModel(patch,-41+109),new VcedSender(62)),1,2,3,1,19);
  addWidget(cmnPane,new PatchNameWidget(" Name", patch),1,3,2,1,0);
  addWidget(cmnPane,new CheckBoxWidget("Mono Mode",patch,new ParamModel(patch,-41+110),new VcedSender(63)),3,3,1,1,-17);
  gbc.gridx=0;gbc.gridy=0;gbc.gridwidth=1;gbc.gridheight=4;
  cmnPane.add(l,gbc);
  gbc.gridx=0;gbc.gridy=5;gbc.gridwidth=3;gbc.gridheight=4;
  cmnPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Common",TitledBorder.CENTER,TitledBorder.CENTER));
  scrollPane.add(cmnPane,gbc);

  JPanel cntlPane=new JPanel();
  cntlPane.setLayout(new GridBagLayout());
  addWidget(cntlPane,new ScrollBarWidget("Wheel to Pitch",patch,0,99,0,new ParamModel(patch,-41+118),new VcedSender(71)),0,0,3,1,33);
  addWidget(cntlPane,new ScrollBarWidget("Wheel to Amplitude",patch,0,99,0,new ParamModel(patch,-41+119),new VcedSender(72)),0,1,3,1,34);
  addWidget(cntlPane,new ScrollBarWidget("B.C to Pitch ",patch,0,99,0,new ParamModel(patch,-41+120),new VcedSender(73)),0,2,3,1,35);
  addWidget(cntlPane,new ScrollBarWidget("B.C to Amplitude",patch,0,99,0,new ParamModel(patch,-41+121),new VcedSender(74)),0,3,3,1,36);
  addWidget(cntlPane,new ScrollBarWidget("B.C Pitch Bias",patch,0,99,0,new ParamModel(patch,-41+122),new VcedSender(75)),0,4,3,1,37);
  addWidget(cntlPane,new ScrollBarWidget("B.C Envelope Bias",patch,0,99,0,new ParamModel(patch,-41+123),new VcedSender(76)),0,5,3,1,38);
  addWidget(cntlPane,new ScrollBarWidget("F.C to Volume",patch,0,99,0,new ParamModel(patch,-41+114),new VcedSender(67)),0,6,3,1,39);
 // addWidget(cntlPane,new ScrollBarWidget("F.C to Pitch",patch,0,99,0,new ParamModel(patch,-41+37),new AcedSender(21)),0,7,3,1,40);
  //addWidget(cntlPane,new ScrollBarWidget("F.C to Amplitude",patch,0,99,0,new ParamModel(patch,-41+38),new AcedSender(22)),0,8,3,1,41);
  addWidget(cntlPane,new ScrollBarWidget("Pitch Bend Range",patch,0,12,0,new ParamModel(patch,-41+111),new VcedSender(64)),0,9,3,1,42);
  gbc.gridx=3;gbc.gridy=9;gbc.gridwidth=3;gbc.gridheight=10;
  cntlPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Modulation",TitledBorder.CENTER,TitledBorder.CENTER));
  scrollPane.add(cntlPane,gbc);

  JPanel effectPane=new JPanel();
  effectPane.setLayout(new GridBagLayout());
  //addWidget(effectPane,new ScrollBarLookupWidget("Reverb Rate",patch,0,7,new ParamModel(patch,-41+36),new AcedSender(20),
   //     new String[] {"Off","1","2","3","4","5","6","7"}),0,0,3,1,27);
  addWidget(effectPane,new ScrollBarWidget("Portamento Time",patch,0,12,0,new ParamModel(patch,-41+113),new VcedSender(66)),0,1,3,1,28);
  ComboBoxWidget portaMode=new ComboBoxWidget("Portamento Mode",patch,new ParamModel(patch,-41+112),new VcedSender(65),new String []
                                                                   {"Full Time","Fingered"});
  addWidget(effectPane,portaMode,0,2,1,1,29);
  addWidget(effectPane,new CheckBoxWidget("Chorus",patch,new ParamModel(patch,-41+117),new VcedSender(70)),0,3,1,1,-24);
  gbc.gridx=3;gbc.gridy=5;gbc.gridwidth=3;gbc.gridheight=2;
  effectPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Effects",TitledBorder.CENTER,TitledBorder.CENTER));
  scrollPane.add(effectPane,gbc);


   JTabbedPane oscPane=new JTabbedPane();
   int i=3;int j=1;
   while (i!=5)
   {
     JPanel panel = new JPanel();
     panel.setLayout(new GridBagLayout());
     oscPane.addTab("Osc"+(j),panel);gbc.weightx=0;
  //    addWidget(panel,new ScrollBarWidget("Wave",patch,0,7,1,new ParamModel(patch,-41+19+(i*5)),new AcedSender(i*5+3)),0,0,3,1,1);
      addWidget(panel,new ScrollBarWidget("Level",patch,0,99,0,new ParamModel(patch,-41+57+(i*13)),new VcedSender(i*13+10)),0,1,3,1,2);
      addWidget(panel,new ScrollBarWidget("Frequency",patch,0,63,0,new ParamModel(patch,-41+58+(i*13)),new VcedSender(i*13+11)),0,2,3,1,3);
 //     addWidget(panel,new ScrollBarWidget("Frequency (Fine)",patch,0,15,0,new ParamModel(patch,-41+18+(i*5)),new AcedSender(i*5+2)),0,3,3,1,4);
  //    addWidget(panel,new ComboBoxWidget("Fix Range",patch,new ParamModel(patch,-41+17+(i*5)),new AcedSender(i*5+1),new String []
   //   			{"255Hz","510Hz","1Khz","2Khz","4Khz","8Khz","16Khz","32Khz"}),1,4,1,1,6);
   //   addWidget(panel,new CheckBoxWidget("Fix Freq",patch,new ParamModel(patch,-41+16+(i*5)),new AcedSender(i*5+0)),2,4,1,1,-1);
      addWidget(panel,new ScrollBarWidget("Detune",patch,0,6,-3,new ParamModel(patch,-41+59+(i*13)),new VcedSender(i*13+12)),0,3,3,1,7);
      addWidget(panel,new ScrollBarWidget("Level Scaling",patch,0,99,0,new ParamModel(patch,-41+52+(i*13)),new VcedSender(i*13+5)),0,7,3,1,15);
      addWidget(panel,new ScrollBarWidget("  Rate Scaling",patch,0,3,0,new ParamModel(patch,-41+53+(i*13)),new VcedSender(i*13+6)),3,7,3,1,16);
      addWidget(panel,new ScrollBarWidget("EG Bias Sensitivity",patch,0,7,0,new ParamModel(patch,-41+54+(i*13)),new VcedSender(i*13+7)),0,5,3,1,9);
      addWidget(panel,new ScrollBarWidget("Key Velocity Sensitivity",patch,0,7,0,new ParamModel(patch,-41+54+(i*13)),new VcedSender(i*13+9)),0,4,3,1,8);
//      addWidget(panel,new ComboBoxWidget("EG Shift",patch,new ParamModel(patch,-41+20+(i*5)),new AcedSender(i*5+4),new String []
//      			{"96db","48db","24db","12db"}),0,4,1,1,5);

     addWidget(panel,new EnvelopeWidget("  Envelope",patch,new EnvelopeWidget.Node [] {
        new EnvelopeWidget.Node(0,0,null,0,0,null,0,false,null,null,null,null),
	new EnvelopeWidget.Node(0,31,new ParamModel(patch,-41+i*13+47),30,30,null,10,true,new VcedSender(i*13),null," AR",null),
	new EnvelopeWidget.Node(0,31,new ParamModel(patch,-41+i*13+48),0,15,new ParamModel(patch,-41+i*13+51),25,true,new VcedSender(i*13+1),new VcedSender(i*13+4),"D1R","D1L"),
	new EnvelopeWidget.Node(0,31,new ParamModel(patch,-41+i*13+49),0,0,null,10,true,new VcedSender(i*13+2),null,"D2R",null),
	new EnvelopeWidget.Node(0,15,new ParamModel(patch,-41+i*13+50),0,0,null,0,true,new VcedSender(i*13+3),null,"RR",null),

      }     ),3,0,3,7,10);
      if (i==3) i=1; else if (i==1) i=2; else if (i==2) i=0;else if (i==0) i=5;
     j++;
   }
  JPanel pegPane = new JPanel();
  pegPane.setLayout(new GridBagLayout());
  if (((YamahaDX100Device)(patch.getDevice())).getWhichSynth()==21) oscPane.addTab("P.E.G.",pegPane);
gbc.fill=GridBagConstraints.BOTH;
     addWidget(pegPane,new EnvelopeWidget("Pitch Envelope",patch,new EnvelopeWidget.Node [] {
        new EnvelopeWidget.Node(50,50,null,0,0,null,50,false,null,null,null,null),
	new EnvelopeWidget.Node(0,99,new ParamModel(patch,93),0,99,new ParamModel(patch,96),0,true,new VcedSender(87),new VcedSender(90),"Rate 1","Pitch 1"),
	new EnvelopeWidget.Node(0,99,new ParamModel(patch,94),0,99,new ParamModel(patch,97),0,true,new VcedSender(88),new VcedSender(91),"Rate 2","Pitch 2"),
	new EnvelopeWidget.Node(0,99,new ParamModel(patch,95),0,99,new ParamModel(patch,98),0,true,new VcedSender(89),new VcedSender(92),"Rate 3","Pitch 3"),
      }     ),0,0,1,1,1);
      gbc.gridy=1;
//kludge for envelopewidget size problem. Should really fix that soon.
      pegPane.add(new JLabel("                The Pitch Envelope will only Effect the Sound on a DX21              "),gbc);
 pegPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"P.E.G",TitledBorder.CENTER,TitledBorder.CENTER));

  gbc.gridx=0;gbc.gridy=0;gbc.gridwidth=6;gbc.gridheight=5;
 scrollPane.add(oscPane,gbc);
   pack();
  }
}

class VcedSender extends SysexSender
{
  int parameter;
  byte []b = new byte [7];
  public VcedSender(int param)
  {parameter=param;
   b[0]=(byte)0xF0; b[1]=(byte)0x43;b[3]=(byte)0x12;b[4]=(byte)parameter;b[6]=(byte)0xF7;
  }
  public byte [] generate (int value)
  {b[5]=(byte)value;b[2]=(byte)(16+channel-1);return b;}

}


class AcedSender extends SysexSender
{
  int parameter;
  byte []b = new byte [7];
  public AcedSender(int param)
  {parameter=param;
   b[0]=(byte)0xF0; b[1]=(byte)0x43;b[3]=(byte)0x13;b[4]=(byte)parameter;b[6]=(byte)0xF7;
  }
  public byte [] generate (int value)
  {b[5]=(byte)value;b[2]=(byte)(16+channel-1);return b;}

}
