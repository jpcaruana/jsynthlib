/*
 * JSynthlib-SingleEditor for Yamaha DX7 Mark-I (with Firmware IG114690)
 * =====================================================================
 * @author  Torsten Tittmann
 * email:   Torsten.Tittmann@t-online.de
 * file:    YamahaDX7SingleEditor.java
 * date:    15.01.2002
 * @version 0.1
 */

package synthdrivers.YamahaDX7;
import core.*;
import java.lang.String.*;
import java.text.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;
import javax.swing.event.*;

class YamahaDX7SingleEditor extends PatchEditorFrame
{
  final String [] OnOffName = new String [] {"Off","On"};

  final String [] KeyTransposeName = new String [] {"C1","C#1","D1","D#1","E1","F1","F#1","G1","G#1","A1","A#1","B1",
                                                    "C2","C#2","D2","D#2","E2","F2","F#2","G2","G#2","A2","A#2","B2",
                                                    "C3","C#3","D3","D#3","E3","F3","F#3","G3","G#3","A3","A#3","B3",
                                                    "C4","C#4","D4","D#4","E4","F4","F#4","G4","G#4","A4","A#4","B4",
                                                    "C5"};

  final String [] LfoWaveName = new String [] {"Triangle","Saw down","Saw up","Square","Sine","S/Hold"};

  final String [] FreqModeName = new String [] {"Ratio","Fixed (Hz)"};

  final String [][] FreqCoarseName = new String [][] {
                                                      {"0.5","1","2","3","4","5","6","7","8","9","10",
                                                       "11","12","13","14","15","16","17","18","19","20",
                                                       "21","22","23","24","25","26","27","28","29","30","31"
                                                      },
                                                      {
                                                       "1.000","10.00","100.0","1000","1.000","10.00","100.0","1000",
                                                       "1.000","10.00","100.0","1000","1.000","10.00","100.0","1000", 
                                                       "1.000","10.00","100.0","1000","1.000","10.00","100.0","1000", 
                                                       "1.000","10.00","100.0","1000","1.000","10.00","100.0","1000"
                                                      }
                                                     };

  final String [][] FreqFineName = new String [][]{ 
                                                    {"1.00","1.01","1.02","1.03","1.04","1.05","1.06","1.07","1.08","1.09",
                                                     "1.10","1.11","1.12","1.13","1.14","1.15","1.16","1.17","1.18","1.19",
                                                     "1.20","1.21","1.22","1.23","1.24","1.25","1.26","1.27","1.28","1.29",
                                                     "1.30","1.31","1.32","1.33","1.34","1.35","1.36","1.37","1.38","1.39",
                                                     "1.40","1.41","1.42","1.43","1.44","1.45","1.46","1.47","1.48","1.49",
                                                     "1.50","1.51","1.52","1.53","1.54","1.55","1.56","1.57","1.58","1.59",
                                                     "1.60","1.61","1.62","1.63","1.64","1.65","1.66","1.67","1.68","1.69",
                                                     "1.70","1.71","1.72","1.73","1.74","1.75","1.76","1.77","1.78","1.79",
                                                     "1.80","1.81","1.82","1.83","1.84","1.85","1.86","1.87","1.88","1.89",
                                                     "1.90","1.91","1.92","1.93","1.94","1.95","1.96","1.97","1.98","1.99"
                                                    },
                                                    {
                                                     "1.000","1.023","1.047","1.072","1.096","1.122","1.148","1.175","1.202","1.230",
                                                     "1.259","1.288","1.318","1.349","1.380","1.413","1.445","1.479","1.514","1.549",
                                                     "1.585","1.622","1.660","1.698","1.738","1.778","1.820","1.862","1.905","1.950",
                                                     "1.995","2.042","2.089","2.138","2.188","2.239","2.291","2.344","2.399","2.455",
                                                     "2.512","2.570","2.630","2.692","2.716","2.818","2.884","2.951","3.020","3.090",
                                                     "3.162","3.236","3.311","3.388","3.467","3.548","3.631","3.715","3.802","3.890",
                                                     "3.981","4.074","4.169","4.266","4.365","4.467","4.571","4.677","4.786","4.898",
                                                     "5.012","5.129","5.248","5.370","5.495","5.623","5.754","5.888","6.026","6.166",
                                                     "6.310","6.457","6.607","6.761","6.918","7.079","7.244","7.413","7.586","7.762",
                                                     "7.943","8.128","8.318","8.511","8.718","8.913","9.120","9.333","9.550","9.772"
                                                    }
                                                  };


  final String [] KbdBreakPointName = new String [] {"A-1","A#-1","B-1",
                                                     "C0","C#0","D0","D#0","E0","F0","F#0","G0","G#0","A0","A#0","B0",
                                                     "C1","C#1","D1","D#1","E1","F1","F#1","G1","G#1","A1","A#1","B1",
                                                     "C2","C#2","D2","D#2","E2","F2","F#2","G2","G#2","A2","A#2","B2",
                                                     "C3","C#3","D3","D#3","E3","F3","F#3","G3","G#3","A3","A#3","B3",
                                                     "C4","C#4","D4","D#4","E4","F4","F#4","G4","G#4","A4","A#4","B4",
                                                     "C5","C#5","D5","D#5","E5","F5","F#5","G5","G#5","A5","A#5","B5",
                                                     "C6","C#6","D6","D#6","E6","F6","F#6","G6","G#6","A6","A#6","B6",
                                                     "C7","C#7","D7","D#7","E7","F7","F#7","G7","G#7","A7","A#7","B7",
                                                     "C8"};

  final String [] KbdCurveName = new String [] {"-Lin","-Exp","+Exp","+Lin"};
  ImageIcon algoIcon[]=new ImageIcon[32];;

  public YamahaDX7SingleEditor(Patch patch)
  {
    super ("Yamaha DX7 Single Editor",patch);
    algoIcon[ 0]=new ImageIcon("synthdrivers/YamahaDX7/gif/algo01.gif");
    algoIcon[ 1]=new ImageIcon("synthdrivers/YamahaDX7/gif/algo02.gif");
    algoIcon[ 2]=new ImageIcon("synthdrivers/YamahaDX7/gif/algo03.gif");
    algoIcon[ 3]=new ImageIcon("synthdrivers/YamahaDX7/gif/algo04.gif");
    algoIcon[ 4]=new ImageIcon("synthdrivers/YamahaDX7/gif/algo05.gif");
    algoIcon[ 5]=new ImageIcon("synthdrivers/YamahaDX7/gif/algo06.gif");
    algoIcon[ 6]=new ImageIcon("synthdrivers/YamahaDX7/gif/algo07.gif");
    algoIcon[ 7]=new ImageIcon("synthdrivers/YamahaDX7/gif/algo08.gif");
    algoIcon[ 8]=new ImageIcon("synthdrivers/YamahaDX7/gif/algo09.gif");
    algoIcon[ 9]=new ImageIcon("synthdrivers/YamahaDX7/gif/algo10.gif");
    algoIcon[10]=new ImageIcon("synthdrivers/YamahaDX7/gif/algo11.gif");
    algoIcon[11]=new ImageIcon("synthdrivers/YamahaDX7/gif/algo12.gif");
    algoIcon[12]=new ImageIcon("synthdrivers/YamahaDX7/gif/algo13.gif");
    algoIcon[13]=new ImageIcon("synthdrivers/YamahaDX7/gif/algo14.gif");
    algoIcon[14]=new ImageIcon("synthdrivers/YamahaDX7/gif/algo15.gif");
    algoIcon[15]=new ImageIcon("synthdrivers/YamahaDX7/gif/algo16.gif");
    algoIcon[16]=new ImageIcon("synthdrivers/YamahaDX7/gif/algo17.gif");
    algoIcon[17]=new ImageIcon("synthdrivers/YamahaDX7/gif/algo18.gif");
    algoIcon[18]=new ImageIcon("synthdrivers/YamahaDX7/gif/algo19.gif");
    algoIcon[19]=new ImageIcon("synthdrivers/YamahaDX7/gif/algo20.gif");
    algoIcon[20]=new ImageIcon("synthdrivers/YamahaDX7/gif/algo21.gif");
    algoIcon[21]=new ImageIcon("synthdrivers/YamahaDX7/gif/algo22.gif");
    algoIcon[22]=new ImageIcon("synthdrivers/YamahaDX7/gif/algo23.gif");
    algoIcon[23]=new ImageIcon("synthdrivers/YamahaDX7/gif/algo24.gif");
    algoIcon[24]=new ImageIcon("synthdrivers/YamahaDX7/gif/algo25.gif");
    algoIcon[25]=new ImageIcon("synthdrivers/YamahaDX7/gif/algo26.gif");
    algoIcon[26]=new ImageIcon("synthdrivers/YamahaDX7/gif/algo27.gif");
    algoIcon[27]=new ImageIcon("synthdrivers/YamahaDX7/gif/algo28.gif");
    algoIcon[28]=new ImageIcon("synthdrivers/YamahaDX7/gif/algo29.gif");
    algoIcon[29]=new ImageIcon("synthdrivers/YamahaDX7/gif/algo30.gif");
    algoIcon[30]=new ImageIcon("synthdrivers/YamahaDX7/gif/algo31.gif");
    algoIcon[31]=new ImageIcon("synthdrivers/YamahaDX7/gif/algo32.gif");
    final JLabel l=new JLabel(algoIcon[patch.sysex[6+134]]);

   
    /*
     *   DX7 Voice Parameter - Common settings
     */

    JPanel cmnPane= new JPanel();
    cmnPane.setLayout(new GridBagLayout());gbc.weightx=0;
    final ScrollBarWidget algo=new ScrollBarWidget("Algorithm",patch,0,31,1,new ParamModel(patch,6+134),new VcedSender(134));
    addWidget(cmnPane,algo,1,2,7,1,17);
    algo.slider.addChangeListener(new ChangeListener()
    {
      public void stateChanged(ChangeEvent e)
      {
        l.setIcon(algoIcon[algo.slider.getValue()]);
      }
    });
    addWidget(cmnPane,new ScrollBarWidget("Feedback",patch,0,7,0,new ParamModel(patch,6+135),new VcedSender(135)),1,3,7,1,18);
    addWidget(cmnPane,new ScrollBarLookupWidget("Key Transpose",patch,0,48,new ParamModel(patch,6+144),new VcedSender(144),KeyTransposeName),1,1,7,1,19);
    addWidget(cmnPane,new PatchNameWidget(patch,"Name (10 Char.)"),1,0,7,1,0);
    gbc.gridx=0;gbc.gridy=4;gbc.gridwidth=1;gbc.gridheight=1;
    cmnPane.add(new JLabel(" "),gbc);
    gbc.gridx=0;gbc.gridy=5;gbc.gridwidth=1;gbc.gridheight=1;
    cmnPane.add(new JLabel(" "),gbc);
    gbc.gridx=0;gbc.gridy=6;gbc.gridwidth=1;gbc.gridheight=1;
    cmnPane.add(new JLabel(" "),gbc);
    gbc.gridx=0;gbc.gridy=7;gbc.gridwidth=1;gbc.gridheight=1;
    cmnPane.add(new JLabel(" "),gbc);

    gbc.gridx=1;gbc.gridy=5;gbc.gridwidth=7;gbc.gridheight=1;
    cmnPane.add(new JLabel("Operator on/off ( not stored as voice-parameter )"),gbc);
    final CheckBoxWidget op_init = new CheckBoxWidget("1-6       ",patch,new DX7OpModel(patch,0x3F),new DX7OpSender(patch,0x3F));
    addWidget(cmnPane,op_init,1,6,1,2,-9);
    op_init.cb.addActionListener (new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          SysexWidget w;
          for (int i=0; i<widgetList.size();i++)
          {
            w= ((SysexWidget)widgetList.get(i));
	    if (w.paramModel!=null)
            {
              w.setValue(p);
              if (w instanceof CheckBoxWidget) ((CheckBoxWidget)w).cb.setSelected((w.getValue()>0));
            }
          }
        }
      });
    addWidget(cmnPane,new CheckBoxWidget("1  ",patch,new DX7OpModel(patch,32),new DX7OpSender(patch,32)),2,6,1,2,-10);
    addWidget(cmnPane,new CheckBoxWidget("2  ",patch,new DX7OpModel(patch,16),new DX7OpSender(patch,16)),3,6,1,2,-11);
    addWidget(cmnPane,new CheckBoxWidget("3  ",patch,new DX7OpModel(patch, 8),new DX7OpSender(patch, 8)),4,6,1,2,-12);
    addWidget(cmnPane,new CheckBoxWidget("4  ",patch,new DX7OpModel(patch, 4),new DX7OpSender(patch, 4)),5,6,1,2,-13);
    addWidget(cmnPane,new CheckBoxWidget("5  ",patch,new DX7OpModel(patch, 2),new DX7OpSender(patch, 2)),6,6,1,2,-14);
    addWidget(cmnPane,new CheckBoxWidget("6  ",patch,new DX7OpModel(patch, 1),new DX7OpSender(patch, 1)),7,6,1,2,-15);
    gbc.gridx=0;gbc.gridy=0;gbc.gridwidth=1;gbc.gridheight=6;
    cmnPane.add(l,gbc);
    gbc.gridx=0;gbc.gridy=0;gbc.gridwidth=3;gbc.gridheight=8;
    cmnPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Common",TitledBorder.CENTER,TitledBorder.CENTER));
    scrollPane.add(cmnPane,gbc);


    /*
     *   DX7 Voice Paramter - LFO settings
     */

    JPanel lfoPane=new JPanel();
    lfoPane.setLayout(new GridBagLayout());gbc.weightx=0;
    addWidget(lfoPane,new ScrollBarWidget("Speed",patch,0,99,0,new ParamModel(patch,6+137),new VcedSender(137)),0,0,6,1,20);
    addWidget(lfoPane,new ScrollBarWidget("Delay",patch,0,99,0,new ParamModel(patch,6+138),new VcedSender(138)),0,1,6,1,21);
    addWidget(lfoPane,new ScrollBarWidget("PMD",patch,0,99,0,new ParamModel(patch,6+139),new VcedSender(139)),0,2,6,1,22);
    addWidget(lfoPane,new ScrollBarWidget("AMD",patch,0,99,0,new ParamModel(patch,6+140),new VcedSender(140)),0,3,6,1,24);
    gbc.gridx=0;gbc.gridy=4;gbc.gridwidth=6;gbc.gridheight=1;
    lfoPane.add(new JLabel(" "),gbc);
    gbc.gridx=0;gbc.gridy=5;gbc.gridwidth=6;gbc.gridheight=1;
    lfoPane.add(new JLabel(" "),gbc);
    addWidget(lfoPane,new ComboBoxWidget("Wave",patch,new ParamModel(patch,6+142),new VcedSender(142),LfoWaveName),1,5,1,3,26);
    addWidget(lfoPane,new ComboBoxWidget("Sync",patch,new ParamModel(patch,6+141),new VcedSender(141),OnOffName),2,5,1,3,27);
    gbc.gridx=0;gbc.gridy=6;gbc.gridwidth=6;gbc.gridheight=1;
    lfoPane.add(new JLabel(" "),gbc);
    gbc.gridx=0;gbc.gridy=7;gbc.gridwidth=6;gbc.gridheight=1;
    lfoPane.add(new JLabel(" "),gbc);
    gbc.gridx=4;gbc.gridy=0;gbc.gridwidth=3;gbc.gridheight=8;
    lfoPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"LFO",TitledBorder.CENTER,TitledBorder.CENTER));
    scrollPane.add(lfoPane,gbc);


    /*
     *   DX7 Voice parameter - Operator Settings
     */

    JTabbedPane oscPane=new JTabbedPane();
    final DecimalFormat freqFormatter = new DecimalFormat("###0.000");

    int i=5;int j=1;
    while (i!=6)
    {
      JPanel panel = new JPanel();
      panel.setLayout(new GridBagLayout());
      oscPane.addTab("OP"+(j),panel);gbc.weightx=1;

      // left side of "oscPane"
      final ComboBoxWidget FreqMode = new ComboBoxWidget("Mode",patch,new ParamModel(patch,6+(i*21)+17),new VcedSender((i*21)+17),FreqModeName);
      final ComboBoxWidget OscSync = new ComboBoxWidget("Sync (*)",patch,new ParamModel(patch,6+136),new VcedSender(136),OnOffName);
      final ScrollBarWidget FreqCoarse = new ScrollBarWidget("Frequency Coarse",patch,0,31,0,new ParamModel(patch,6+(i*21)+18),new VcedSender((i*21)+18));
      final ScrollBarWidget FreqFine = new ScrollBarWidget("Frequency Fine",patch,0,99,0,new ParamModel(patch,6+(i*21)+19),new VcedSender((i*21)+19));
      final JLabel OscFreqLabel = new JLabel("Frequency");
      addWidget(panel,FreqMode,0,1,3,2,1);

      FreqMode.cb.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
            OscFreqLabel.setText("Oscillator                    Frequency "+FreqModeName[FreqMode.cb.getSelectedIndex()]+": "+ freqFormatter.format( (Float.valueOf(FreqCoarseName[FreqMode.cb.getSelectedIndex()][FreqCoarse.getValue()]).floatValue()) * (Float.valueOf(FreqFineName[FreqMode.cb.getSelectedIndex()][FreqFine.getValue()]).floatValue()) ));
        }
      });

      OscSync.cb.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          int OscSyncValue=OscSync.cb.getSelectedIndex();
          SysexWidget w;
          for (int i=0; i<widgetList.size();i++)
          {
            w= ((SysexWidget)widgetList.get(i));
	    if (w.paramModel!=null)
            {
              if ( (w instanceof ComboBoxWidget) && (((ComboBoxWidget)w).jlabel.getText().equals("Sync (*)")) ) ((ComboBoxWidget)w).cb.setSelectedIndex(OscSyncValue);
            }
          }
        }
      });

      FreqCoarse.slider.addChangeListener(new ChangeListener()
      {
        public void stateChanged(ChangeEvent e)
        {
            OscFreqLabel.setText("Oscillator                    Frequency "+FreqModeName[FreqMode.cb.getSelectedIndex()]+": "+ freqFormatter.format((Float.valueOf(FreqCoarseName[FreqMode.cb.getSelectedIndex()][FreqCoarse.slider.getValue()]).floatValue()) * (Float.valueOf(FreqFineName[FreqMode.cb.getSelectedIndex()][FreqFine.getValue()]).floatValue())) );
        }
      });

      FreqFine.slider.addChangeListener(new ChangeListener()
      {
        public void stateChanged(ChangeEvent e)
        {
            OscFreqLabel.setText("Oscillator                    Frequency "+FreqModeName[FreqMode.cb.getSelectedIndex()]+": "+ freqFormatter.format((Float.valueOf(FreqCoarseName[FreqMode.cb.getSelectedIndex()][FreqCoarse.getValue()]).floatValue()) * (Float.valueOf(FreqFineName[FreqMode.cb.getSelectedIndex()][FreqFine.slider.getValue()]).floatValue())) );
        }
      });
      addWidget(panel,OscSync,3,1,3,2,28);
      addWidget(panel,FreqCoarse,0,3,6,1,2);
      addWidget(panel,FreqFine,0,4,6,1,3);
      gbc.gridx=0;gbc.gridy=0;gbc.gridwidth=6;gbc.gridheight=1;
      OscFreqLabel.setText("Oscillator                    Frequency "+FreqModeName[FreqMode.cb.getSelectedIndex()]+": "+ freqFormatter.format(( (Float.valueOf(FreqCoarseName[FreqMode.cb.getSelectedIndex()][FreqCoarse.getValue()]).floatValue()) * (Float.valueOf(FreqFineName[FreqMode.cb.getSelectedIndex()][FreqFine.getValue()]).floatValue())  )));
      panel.add(OscFreqLabel,gbc);

      addWidget(panel,new ScrollBarWidget("Detune",patch,0,14,-7,new ParamModel(patch,6+(i*21)+20),new VcedSender((i*21)+20)),0,6,6,1,4);
      gbc.gridx=0;gbc.gridy=7;gbc.gridwidth=6;gbc.gridheight=1;
      panel.add(new JLabel(" "),gbc);
      gbc.gridx=0;gbc.gridy=8;gbc.gridwidth=6;gbc.gridheight=1;
      panel.add(new JLabel("Keyboard Level Scaling"),gbc);
      addWidget(panel,new ScrollBarLookupWidget("Breakpoint",patch,0,99,new ParamModel(patch,6+(i*21)+8),new VcedSender((i*21)+8),KbdBreakPointName),0,9,6,1,5);
      gbc.gridx=0;gbc.gridy=10;gbc.gridwidth=6;gbc.gridheight=1;
      panel.add(new JLabel(" "),gbc);
      addWidget(panel,new ComboBoxWidget("Curve Left",patch,new ParamModel(patch,6+(i*21)+11),new VcedSender((i*21)+11),KbdCurveName),0,11,3,2,6);
      addWidget(panel,new ComboBoxWidget("Curve Right",patch,new ParamModel(patch,6+(i*21)+12),new VcedSender((i*21)+12),KbdCurveName),3,11,3,2,8);
      addWidget(panel,new ScrollBarWidget("Depth Left",patch,0,99,0,new ParamModel(patch,6+(i*21)+9),new VcedSender((i*21)+9)),0,13,6,1,17);
      addWidget(panel,new ScrollBarWidget("Depth Right",patch,0,99,0,new ParamModel(patch,6+(i*21)+10),new VcedSender((i*21)+10)),0,14,6,1,9);
      gbc.gridx=0;gbc.gridy=15;gbc.gridwidth=6;gbc.gridheight=1;
      panel.add(new JLabel(" "),gbc);
      addWidget(panel,new ScrollBarWidget("Keyboard Rate Scaling",patch,0,7,0,new ParamModel(patch,6+(i*21)+13),new VcedSender((i*21)+13)),0,16,6,1,10);

      gbc.gridx=0;gbc.gridy=16;gbc.gridwidth=6;gbc.gridheight=1;
      panel.add(new JLabel(" "),gbc);
      gbc.gridx=0;gbc.gridy=17;gbc.gridwidth=6;gbc.gridheight=1;
      panel.add(new JLabel("(*) one single parameter for six operators"),gbc);

      // space between left and right column
      gbc.gridx=6;gbc.gridy=0;gbc.gridwidth=1;gbc.gridheight=1;
      panel.add(new JLabel("    "),gbc);

      // right side of "oscPane"
      gbc.gridx=7;gbc.gridy=0;gbc.gridwidth=6;gbc.gridheight=1;
      panel.add(new JLabel("Operator"),gbc);
      addWidget(panel,new ScrollBarWidget("Output Level",patch,0,99,0,new ParamModel(patch,6+(i*21)+16),new VcedSender((i*21)+16)),7,1,6,1,11);
      addWidget(panel,new ScrollBarWidget("Velocity Sensitivity",patch,0,7,0,new ParamModel(patch,6+(i*21)+15),new VcedSender((i*21)+15)),7,2,6,1,12);
      gbc.gridx=7;gbc.gridy=4;gbc.gridwidth=6;gbc.gridheight=1;
      panel.add(new JLabel("Modulation Sensitivity"),gbc);
      addWidget(panel,new ScrollBarWidget("Amplitude",patch,0,3,0,new ParamModel(patch,6+(i*21)+14),new VcedSender((i*21)+14)),7,5,6,1,13);
      final ScrollBarWidget PitchModSens = new ScrollBarWidget("Pitch (*)",patch,0,7,0,new ParamModel(patch,6+143),new VcedSender(143));
      addWidget(panel,PitchModSens,7,6,6,1,23);
      PitchModSens.slider.addChangeListener (new ChangeListener()
      {
        public void stateChanged(ChangeEvent e)
        {
          int pmsValue=PitchModSens.slider.getValue();
          SysexWidget w;
          for (int i=0; i<widgetList.size();i++)
          {
            w= ((SysexWidget)widgetList.get(i));
	    if (w.paramModel!=null)
            {
              if ( (w instanceof ScrollBarWidget) && (((ScrollBarWidget)w).jlabel.getText().equals("Pitch (*)")) ) ((ScrollBarWidget)w).slider.setValue(pmsValue);
            }
          }
        }
      });

      addWidget(panel,new EnvelopeWidget("Envelope Generator",patch,new EnvelopeNode []
      {
        new EnvelopeNode(0,0,null,0,0,null,0,false,null,null,null,null),
        new EnvelopeNode(0,99,new ParamModel(patch,6+(i*21)+0),0,99,new ParamModel(patch,6+(i*21)+4),0,true,new VcedSender((i*21)+0),new VcedSender((i*21)+4),"R1","L1"),
        new EnvelopeNode(0,99,new ParamModel(patch,6+(i*21)+1),0,99,new ParamModel(patch,6+(i*21)+5),0,true,new VcedSender((i*21)+1),new VcedSender((i*21)+5),"R2","L2"),
        new EnvelopeNode(0,99,new ParamModel(patch,6+(i*21)+2),0,99,new ParamModel(patch,6+(i*21)+6),0,true,new VcedSender((i*21)+2),new VcedSender((i*21)+6),"R3","L3"),
        new EnvelopeNode(0,99,new ParamModel(patch,6+(i*21)+3),0,99,new ParamModel(patch,6+(i*21)+7),0,true,new VcedSender((i*21)+3),new VcedSender((i*21)+7),"R4","L4"),
      }),7,8,6,9,14);


      if (i==5) i=4;
      else if (i==4) i=3;
      else if (i==3) i=2;
      else if (i==2) i=1;
      else if (i==1) i=0;
      else if (i==0) i=6;

      j++;
    }


    /*
     * DX7 Voice Parameter - Pitch Envelope Generator
     */

    JPanel pegPane = new JPanel();
    pegPane.setLayout(new GridBagLayout());
    oscPane.addTab("PEG",pegPane);
    gbc.fill=gbc.BOTH;
    addWidget(pegPane,new EnvelopeWidget("Pitch Envelope Generator",patch,new EnvelopeNode []
    {
      new EnvelopeNode(0,0,null,0,0,null,0,false,null,null,null,null),
      new EnvelopeNode(0,99,new ParamModel(patch,6+126),0,99,new ParamModel(patch,6+130),0,true,new VcedSender(126),new VcedSender(130),"Rate 1","Pitch 1"),
      new EnvelopeNode(0,99,new ParamModel(patch,6+127),0,99,new ParamModel(patch,6+131),0,true,new VcedSender(127),new VcedSender(131),"Rate 2","Pitch 2"),
      new EnvelopeNode(0,99,new ParamModel(patch,6+128),0,99,new ParamModel(patch,6+132),0,true,new VcedSender(128),new VcedSender(132),"Rate 3","Pitch 3"),
      new EnvelopeNode(0,99,new ParamModel(patch,6+129),0,99,new ParamModel(patch,6+133),0,true,new VcedSender(129),new VcedSender(133),"Rate 4","Pitch 4"),
    }),0,0,1,9,1);

    gbc.gridx=0;gbc.gridy=18;gbc.gridwidth=1;gbc.gridheight=1;
    //kludge for envelopewidget size problem.
    pegPane.add(new JLabel("                                                                                        ",JLabel.LEFT),gbc);
    pegPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"PEG",TitledBorder.CENTER,TitledBorder.CENTER));

    gbc.gridx=0;gbc.gridy=10;gbc.gridwidth=7;gbc.gridheight=18;
    scrollPane.add(oscPane,gbc);
    pack();
    show();
  }
}


/*
 * SysexSender - Voice Parameter
 */
class VcedSender extends SysexSender
{
  int parameter;
  int para_high=0;
  byte []b = new byte [7];
  public VcedSender(int param)
  {
    parameter=param;
    if (parameter >= 128)
    {
      para_high  = 0x01;
      parameter -= 128;
    }
    b[0]=(byte)0xF0;
    b[1]=(byte)0x43;
    b[3]=(byte)para_high;
    b[4]=(byte)parameter;
    b[6]=(byte)0xF7;
  }
  public byte [] generate (int value)
  {
    b[2]=(byte)(16+channel-1);
    b[5]=(byte)value;
    return b;
  }
}


/*
 * SysexSender - Funktion Parameter (are not stored as part of a voice)
 *                                 (Poly/Mono, Pitchbend, Portamento, Modulation Wheel, ...)
 */
class AcedSender extends SysexSender
{
  int parameter;
  byte []b = new byte [7];
  public AcedSender(int param)
  {
    parameter=param;
    b[0]=(byte)0xF0;
    b[1]=(byte)0x43;
    b[3]=(byte)0x08;
    b[4]=(byte)parameter;
    b[6]=(byte)0xF7;
  }
  public byte [] generate (int value)
  {
    b[2]=(byte)(16+channel-1);
    b[5]=(byte)value;
    return b;
  }
}


/*
 * SysexSender & ParamModel - Workaround for Operator On/Off Function
 * (since the Operator on/off function isn't stored at a part of the voice
 *  this ParamModel only meet the requirement of CheckBoxWidget()!)
 */
class DX7OpSender extends SysexSender
{
  Patch patch;
  int op_state;
  int bitmask;
  byte []b = new byte [7];

  public DX7OpSender(Patch p,int bm)
  {
    patch=p;
    bitmask=bm;
    b[0]=(byte)0xF0;
    b[1]=(byte)0x43;
    b[3]=(byte)0x01;    // voice parameter 155
    b[4]=(byte)0x1B;    // voice parameter 155
    b[6]=(byte)0xF7;
  }

  public byte [] generate (int value)
  {
    b[2]=(byte)(16+channel-1);
    if (value == 1)
    {
      b[5]=(byte)(patch.sysex[6+155] | bitmask);
    }
    else
    {
      b[5]=(byte)(patch.sysex[6+155] & (~bitmask));
    }
    return b;
  }
}


class DX7OpModel extends ParamModel
{
  public int bitmask;
  public int mult=1;

  public DX7OpModel(Patch p, int b)
  {
    patch=p;
    bitmask=b;

    if ((bitmask&1)==1) mult=1;
    else
    if ((bitmask&2)==2) mult=2;
    else
    if ((bitmask&4)==4) mult=4;
    else
    if ((bitmask&8)==8) mult=8;
    else
    if ((bitmask&16)==16) mult=16;
    else
    if ((bitmask&32)==32) mult=32;
  }

  public void set(int i)
  {
    patch.sysex[6+155] &= 0x3F;  // binary: 11 1111
    if (i == 1)
    {
      patch.sysex[6+155]=(byte)(patch.sysex[6+155] | (bitmask));
    }
    else
    {
      patch.sysex[6+155]=(byte)(patch.sysex[6+155] & (~bitmask));
    }
  }

  public int get()
  {
    return ((patch.sysex[6+155]&bitmask)/mult);
  }
}

