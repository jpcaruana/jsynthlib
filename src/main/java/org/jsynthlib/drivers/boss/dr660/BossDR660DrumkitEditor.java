/*
 * @version $Id$
 */
package org.jsynthlib.drivers.boss.dr660;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.jsynthlib.core.CheckBoxWidget;
import org.jsynthlib.core.ComboBoxWidget;
import org.jsynthlib.core.ParamModel;
import org.jsynthlib.core.Patch;
import org.jsynthlib.core.PatchEditorFrame;
import org.jsynthlib.core.PatchNameWidget;
import org.jsynthlib.core.ScrollBarLookupWidget;
import org.jsynthlib.core.ScrollBarWidget;
import org.jsynthlib.core.SysexSender;
import org.jsynthlib.core.SysexWidget;

class BossDR660DrumkitEditor extends PatchEditorFrame{
 final String [] waveNameList = new String [] {
 "  0- ambient kick","  1- attack kick","  2- basket ball kick","  3- boing solid kick","  4- breater kick","  5- bright kick",
 "  6- dance kick","  7- deep kick","  8- delay kick","  9- door kick"," 10- deep reverb kick"," 11- dry kick 1",
 " 12- dry kick 2"," 13- dull kick"," 14- electronic kick 1"," 15- electronic kick 2"," 16- gate kick 1"," 17- gate kick 2",
 " 18- hard acoustic kick"," 19- house kick"," 20- hybrid kick"," 21- mondo reverb kick"," 22- mondo kick"," 23- mondo deep kick",
 " 24- pillow kick"," 25- punch kick"," 26- rap kick"," 27- real kick"," 28- reverb kick"," 29- room kick 1"," 30- room kick 2",
 " 31- sharp kick"," 32- shell kick"," 33- smash kick"," 34- soft acoustic kick"," 35- solid kick"," 36- strength kick",
 " 37- synthesizer kick"," 38- techno kick"," 39- thud kick"," 40- tight kick"," 41- tomtom kick"," 42- TR808 kick",
 " 43- TR909 kick"," 44- reverb solid kick"," 45- verby kick"," 46- wood kick"," 47- TR808 acoustic kick",
 " 48- TR808 electronic kick"," 49- TR808 gate kick"," 50- TR909 hard kick"," 51-attack snare"," 52-big shot snare",
 " 53- breath snare", " 54- bright snare"," 55- brush roll snare 1"," 56- brush roll snare 2"," 57- brush slap snare 1",
 " 58- brush slap snare 2"," 59- brush slap snare 3"," 60- brush swish snare"," 61- chop snare 1"," 62- chop snare 2",
 " 63- cracker snare"," 64- cruddy snare"," 65- dance snare"," 66- delay snare"," 67- digital snare"," 68- disco snare",
 " 69- dopin' snare"," 70- electronic snare 1"," 71- electronic snare 2"," 72- fat snare"," 73- fx snare",
 " 74- glass snare"," 75- grab snare"," 76- hard snare"," 77- house snare 1"," 78- house snare 2"," 79- house snare 3",
 " 80- house dopin' snare"," 81- huge snare"," 82- hyper snare"," 83- L.A. snare"," 84- L.A. fat snare"," 85- light snare 1",
 " 86- light snare 2"," 87- loose snare"," 88- nasty snare"," 89- noise snare"," 90- piccolo snare 1"," 91- piccolo snare 2",
 " 92- piccolo snare 3"," 93- power snare"," 94- radio snare"," 95- raspy snare"," 96- rocker snare"," 97- rockin' snare",
 " 98- rock light snare"," 99- rock power snare","100- rock rim shot snare","101- rock splatter snare","102- real snare 1",
 "103- real snare 2", "104-reggae snare 1","105- reggae snare 2","106- ring snare","107- rock snare 1","108- rock snare 2",
 "109- splatter snare","110- super light snare","111- supper whack snare","112- swing snare","113- thin snare",
 "114- light snare","115- tiny snare","116- trash snare","117- TR808 snare","118- TR909 snare","119- yep snare",
 "120- 90's snare","121- TR909 light snare","122- TR909 ring snare","123- ambient side stick", "124- hall side kick",
 "125- metal side stick","126- sticks","127- TR808 side stick","128- ambient tom 1","129- ambient tom 2","130- ambient tom 3",
 "131- ambient tom 4","132- boosh tom high","133- boosh tom low", "134- brush slap tom 1","135- brush slap tom 2",
 "136- brush slap tom 3","137- brush slap tom 4","138- dry tom 1","139- dry tom 2","140- dry tom 3","141- dry tom 4",
 "142- electronic tom 1","143- electronic tom 2", "144- electronic tom 3","145- electronic tom 4","146- light tom 1",
 "147- light tom 2","148- light tom 3","149- light tom 4","150- real tom 1","151-real tom 2","152-real tom 3","153-real tom 4",
 "154- rim tom 1","155- rim tom 2","156- rim tom 3","157- rim tom 4","158- rock tom 1","159- rock tom 2","160- rock tom 3",
 "161- rock tom 4","162- room tom 1","163- room tom 2", "164- room tom 3","165- room tom 4","166- TR808 tom",
 "167- pop closed hi-hat","168- pop open hi-hat","169- real closed hi-hat","170- real open hi-hat","171- real pedal closed hi-hat",
 "172- brush closed hi-hat","173- brush open hi-hat", "174- TR808 closed hi-hat","175- TR808 open hi-hat","176- CR78 closed hi-hat",
 "177- CR78 open hi-hat","178- crash cymbal 1","179- crash cymbal 2","180- splash cymbal","181- chinese cymbal","182- ride cymbal",
 "183- ride bell cymbal","184- ride brush cymbal","185- cowbell","186- tambourine","187- sleigh bell","188- hall castanets",
 "189- triangle","190- wood block","191- bongo high","192- bongo low","193- conga high mute","194- conga high slap",
 "195- conga high open","196- conga low open","197- timable","198- claves","199- vibra-slap","200- guiro short","201- guiro long",
 "202- maracas","203- shaker", "204- cabasa up","205- cabasa down","206- whistle short","207- whistle long","208- agogo",
 "209- cuica","210- DR55 claves","211- CR78 cowbell","212- CR78 metallic beat","213- CR78 guiro","214- CR78 tambourine",
 "215- CR78 maracas","216- TR808 conga","217- TR808 claves","218- TR808 maracas","219- TR808 hand clap","220- TR808 cowbell",
 "221- scratch 1","222- scratch 2","223-scratch 3","224- scratch 4","225- hi-Q","226- snaps","227- hoo!","228- uut?","229-fx noise",
 "230- chink","231- dance clap","232- reverb clap","233- reverb shot","234- light shot","235- fx shot","236- glass shot",
 "237- reverse kick","238- reverse snare","239- reverse tom","240- reverse cymbal","241- reverse castanets",
 "242- reverse metallic beat","243- reverse hi-Q","244- reverse clap","245- reverse shot","246- reverse ambience",
 "247- reverse reverb","248- kick ambience","249- snare ambience","250- tom ambience","251- long reverb","252- gate reverb",
 "253- slap bass","254- synth bass","255- OFF"


 };
 final String [] padList = new String [] {
 "A01","A02","A03","A04","A05","A06","A07","A08","A09","A10","A11","A12","A13","A14","A15","A16",
 "B01","B02","B03","B04","B05","B06","B07","B08","B09","B10","B11","B12","B13","B14","B15","B16",
 "-01","-02","-03","-04","-05","-06","-07","-08","-09","-10","-11","-12","-13","-14","-15","-16","-17","-18","-19","-20",
 "-21","-22","-23"};


  int drum=0;
 public BossDR660DrumkitEditor(Patch patch)
  {
    super ("Boss DR660 Drumkit Editor",patch);
  JPanel kitPane=new JPanel();
  kitPane.setLayout(new GridBagLayout());gbc.weightx=1;
  addWidget(kitPane,new PatchNameWidget(" Name", patch),1,1,2,1,0);
  addWidget(kitPane,new ComboBoxWidget("Sense Curve A",patch,new ParamModel(patch,0x554),new BankSender(0),new String []
      			{"Exp1","Lin1","Exp2","Lin2","XfdO","XfdI","Fix1","Fix2"}),1,2,1,1,1);
  addWidget(kitPane,new ComboBoxWidget("Sense Curve B",patch,new ParamModel(patch,0x555),new BankSender(1),new String []
      			{"Exp1","Lin1","Exp2","Lin2","XfdO","XfdI","Fix1","Fix2"}),1,3,1,1,2);
  addWidget(kitPane,new CheckBoxWidget("Bank Layer",patch,new ParamModel(patch,0x556),new BankSender(2)),1,4,1,1,-1);

  gbc.gridx=0;gbc.gridy=0;gbc.gridwidth=3;gbc.gridheight=4;
  kitPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"DrumKit Settings",TitledBorder.CENTER,TitledBorder.CENTER));
  scrollPane.add(kitPane,gbc);
/*  FIXME: Changing FX thru Sysex don't seem to affect the sound, only display on the DR
  JPanel fxPane=new JPanel();
  fxPane.setLayout(new GridBagLayout());gbc.weightx=1;
  addWidget(fxPane,new ComboBoxWidget("Effect Type",patch,new ParamModel(patch,0x53C),new FXSender(0),new String []
      			{"Hall","Room","Plate","Delay","Pan-Delay"}),1,1,2,1,3);

  addWidget(fxPane,new ScrollBarWidget("  Reverb Time",patch,0,31,0,new ParamModel(patch,0x53D),new FXSender(1)),1,2,3,1,4);

  gbc.gridx=0;gbc.gridy=4;gbc.gridwidth=3;gbc.gridheight=4;
  fxPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Effects Settings",TitledBorder.CENTER,TitledBorder.CENTER));
  scrollPane.add(fxPane,gbc);
  */
  JPanel padPane=new JPanel();
  padPane.setLayout(new GridBagLayout());gbc.weightx=1;
  final JComboBox cb=new JComboBox(padList);
  padPane.add(cb);
  cb.addActionListener (new ActionListener() { 
   public void actionPerformed(ActionEvent e)
   {drum=cb.getSelectedIndex();
   SysexWidget w;
   for (int i=0; i<widgetList.size();i++)
   {w= ((SysexWidget)widgetList.get(i)); 
   w.setValue();
       // Now w.setValue() does the following job.
       /*
	 if (w instanceof ScrollBarWidget) ((ScrollBarWidget)w).slider.setValue(w.getValue());
	 if (w instanceof ScrollBarLookupWidget) ((ScrollBarLookupWidget)w).slider.setValue(w.getValue());
         if (w instanceof ComboBoxWidget) ((ComboBoxWidget)w).cb.setSelectedIndex(w.getValue());
         if (w instanceof CheckBoxWidget) ((CheckBoxWidget)w).cb.setSelected((w.getValue()>0));
       */
   }
   }}
  );


  gbc.gridx=4;gbc.gridy=0;gbc.gridwidth=3;gbc.gridheight=2;
  padPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Choose a Pad",TitledBorder.CENTER,TitledBorder.CENTER));
  scrollPane.add(padPane,gbc);

  JPanel drumPane=new JPanel();
  drumPane.setLayout(new GridBagLayout());gbc.weightx=1;
  addWidget(drumPane,new ComboBoxWidget("Instrument",patch,new WaveModel(patch,9),new WaveSender(),waveNameList),1,1,2,1,3);

  addWidget(drumPane,new ScrollBarWidget("Level",patch,0,15,0,new DrumModel(patch,11),new DrumSender(2)),1,2,3,1,4);
  addWidget(drumPane,new ScrollBarWidget("Nuance",patch,0,15,-7,new DrumModel(patch,12),new DrumSender(3)),1,3,3,1,5);
  addWidget(drumPane,new ScrollBarWidget("Pitch",patch,0,512,-240,new PitchModel(patch,13),new PitchSender()),1,4,3,1,6);

  addWidget(drumPane,new ScrollBarWidget("Decay",patch,0,62,-31,new DrumModel(patch,15),new DrumSender(6)),1,5,3,1,7);
  addWidget(drumPane,new ScrollBarLookupWidget("Pan",patch,0,15,new DrumModel(patch,16),new DrumSender(7),
    new String [] {"L7","L6","L5","L4","L3","L2","L1","C","R1","R2","R3","R4","R5","R6","R7","Indiv"}),1,6,3,1,8);
  addWidget(drumPane,new CheckBoxWidget("Poly Mode",patch,new DrumModel(patch,17+drum*24),new DrumSender(8)),1,7,1,1,-2);
  addWidget(drumPane,new ComboBoxWidget("Assign Group",patch,new DrumModel(patch,18),new DrumSender(9),new String []
      			{"Off","Exc1","Exc2","Exc3","Exc4","Exc5","Exc6","Exc7"}),2,7,2,1,9);
  addWidget(drumPane,new ScrollBarWidget("Reverb Send",patch,0,15,0,new DrumModel(patch,19),new DrumSender(10)),1,8,3,1,10);
  addWidget(drumPane,new ScrollBarWidget("Chorus Send",patch,0,15,0,new DrumModel(patch,20),new DrumSender(11)),1,9,3,1,11);
  addWidget(drumPane,new ScrollBarWidget("Midi Note",patch,27,81,0,new NoteModel(patch,0x4FA),new NoteSender()),1,10,3,1,12);


  gbc.gridx=4;gbc.gridy=2;gbc.gridwidth=3;gbc.gridheight=4;
  drumPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Drum Setting",TitledBorder.CENTER,TitledBorder.CENTER));
  scrollPane.add(drumPane,gbc);
   pack();
  }

class BankSender extends SysexSender
{
  int parameter;
  byte []b = new byte [12];
  public BankSender(int param)
  {parameter=param;
   b[0]=(byte)0xF0; b[1]=(byte)0x41;b[3]=(byte)0x52;b[4]=0x12;b[5]=0x0;b[6]=0x03;b[7]=0x0;
   b[8]=(byte)parameter;b[11]=(byte)0xF7;
  }
  public byte [] generate (int value)
  {b[9]=(byte)value;b[2]=(byte)(channel-1);
   int sum=0;
   for (int i=5;i<=9;i++)
      sum+=b[i];
    sum=(byte)(sum % 128);
    sum=(byte)(sum^127);
    sum=(byte)(sum+1);
    sum=(byte)(sum%128);
  b[10]=(byte)sum;
  return b;}

}

class FXSender extends SysexSender
{
  int parameter;
  byte []b = new byte [12];
  public FXSender(int param)
  {parameter=param;
   b[0]=(byte)0xF0; b[1]=(byte)0x41;b[3]=(byte)0x52;b[4]=0x12;b[5]=0x0;b[6]=0x02;b[7]=0x0;
   b[8]=(byte)parameter;b[11]=(byte)0xF7;
  }
  public byte [] generate (int value)
  {b[9]=(byte)value;b[2]=(byte)(channel-1);
   int sum=0;
   for (int i=5;i<=9;i++)
      sum+=b[i];
    sum=(byte)(sum % 128);
    sum=(byte)(sum^127);
    sum=(byte)(sum+1);
    sum=(byte)(sum%128);
  b[10]=(byte)sum;
  return b;}

}
class DrumSender extends SysexSender
{
  int parameter;
  byte []b = new byte [12];
  public DrumSender(int param)
  {parameter=param;
   b[0]=(byte)0xF0; b[1]=(byte)0x41;b[3]=(byte)0x52;b[4]=0x12;b[5]=0x0;b[6]=0x00;b[7]=(byte)drum;
   b[8]=(byte)parameter;b[11]=(byte)0xF7;
  }
  public byte [] generate (int value)
  {
   b[7]=(byte) drum;
   b[9]=(byte)value;b[2]=(byte)(channel-1);
   int sum=0;
   for (int i=5;i<=9;i++)
      sum+=b[i];
    sum=(byte)(sum % 128);
    sum=(byte)(sum^127);
    sum=(byte)(sum+1);
    sum=(byte)(sum%128);
  b[10]=(byte)sum;
  return b;}
}

class NoteSender extends SysexSender
{
  byte []b = new byte [12];
  public NoteSender()
  {
   b[0]=(byte)0xF0; b[1]=(byte)0x41;b[3]=(byte)0x52;b[4]=0x12;b[5]=0x0;b[6]=0x01;b[7]=0;
   b[8]=(byte)drum;b[11]=(byte)0xF7;
  }
  public byte [] generate (int value)
  {
   b[8]=(byte) drum;
   b[9]=(byte)value;b[2]=(byte)(channel-1);
   int sum=0;
   for (int i=5;i<=9;i++)
      sum+=b[i];
    sum=(byte)(sum % 128);
    sum=(byte)(sum^127);
    sum=(byte)(sum+1);
    sum=(byte)(sum%128);
  b[10]=(byte)sum;
  return b;}
}

class PitchSender extends SysexSender
{

  byte []b = new byte [13];
  public PitchSender()
  {
   b[0]=(byte)0xF0; b[1]=(byte)0x41;b[3]=(byte)0x52;b[4]=0x12;b[5]=0x0;b[6]=0x00;b[7]=(byte)drum;
   b[8]=(byte)4;b[12]=(byte)0xF7;
  }
  public byte [] generate (int value)
  {
   b[7]=(byte) drum;
   b[9]=(byte)(value/128);
   b[10]=(byte)(value%128);
   b[2]=(byte)(channel-1);
   int sum=0;
   for (int i=5;i<=10;i++)
      sum+=b[i];
    sum=(byte)(sum % 128);
    sum=(byte)(sum^127);
    sum=(byte)(sum+1);
    sum=(byte)(sum%128);
  b[11]=(byte)sum;
  return b;}

}
class WaveSender extends SysexSender
{

  byte []b = new byte [13];
  public WaveSender()
  {
   b[0]=(byte)0xF0; b[1]=(byte)0x41;b[3]=(byte)0x52;b[4]=0x12;b[5]=0x0;b[6]=0x00;b[7]=(byte)drum;
   b[8]=(byte)0;b[12]=(byte)0xF7;
  }
  public byte [] generate (int value)
  {
   b[7]=(byte) drum;
   b[9]=(byte)(value/16);
   b[10]=(byte)(value%16);
   b[2]=(byte)(channel-1);
   int sum=0;
   for (int i=5;i<=10;i++)
      sum+=b[i];
    sum=(byte)(sum % 128);
    sum=(byte)(sum^127);
    sum=(byte)(sum+1);
    sum=(byte)(sum%128);
  b[11]=(byte)sum;
  return b;}

}

 class DrumModel extends ParamModel
{
    // int ofs;
 public DrumModel(Patch p,int o) {super(p,o);}
 public void set(int i) {patch.sysex[ofs+drum*23]=(byte)(i);}
 public int get() {return ((patch.sysex[ofs+drum*23]));}

}

 class NoteModel extends ParamModel
{
    // int ofs;
 public NoteModel(Patch p,int o) {super(p,o);}
 public void set(int i) {patch.sysex[ofs+drum]=(byte)(i);}
 public int get() {return ((patch.sysex[ofs+drum]));}

}

 class PitchModel extends ParamModel
{
    // int ofs;
 public PitchModel(Patch p,int o) {super(p,o);}
 public void set(int i) {patch.sysex[ofs+drum*23]=(byte)(i/128);
                         patch.sysex[ofs+drum*23+1]=(byte)(i%128);}
 public int get() {return ((patch.sysex[ofs+drum*23]*128+patch.sysex[ofs+1+drum*23]));}

}
 class WaveModel extends ParamModel
{
    // int ofs;
 public WaveModel(Patch p,int o) {super(p,o);}
 public void set(int i) {patch.sysex[ofs+drum*23]=(byte)(i/16);
                         patch.sysex[ofs+drum*23+1]=(byte)(i%16);}
 public int get() { return((patch.sysex[ofs+drum*23]*16+patch.sysex[ofs+1+drum*23]));}

}
}
