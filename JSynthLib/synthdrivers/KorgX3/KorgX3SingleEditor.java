package synthdrivers.KorgX3;
import core.*;
import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;
/**
 * Class for editing Korg X3 Program sounds
 *
 * Includes at the moment all the basic sound setting properties,
 * as the multisounds, VDA EG and VDF EG editing for both oscillators
 * as well as Pitch EG.
 * Does not implement all features as pan positioning etc. Easy to add however.
 *
 * @author Juha Tukkinen
 * @version $Id$
 */
class KorgX3SingleEditor extends PatchEditorFrame
{

  private final static String [] multiSounds =
    new String [] {"000 A.Piano 1 ","001 A.Piano1LP","002 A.Piano 2 ","003 E.Piano 1 ","004 E.Piano1LP",
		   "005 E.Piano 2 ","006 E.Piano2LP","007 Soft EP   ","008 Soft EP LP","009 Hard EP   ",
		   "010 Hard Ep LP","011 PianoPad 1","012 PianoPad 2","013 Clav      ","014 Clav LP   ",
		   "015 Harpsicord","016 HarpsicdLP","017 PercOrgan1","018 PercOrg1LP","019 PercOrgan2",
		   "020 PercOrg2LP","021 Organ 1   ","022 Organ 1 LP","023 Organ 2   ","024 Organ 2 LP",
		   "025 Organ 3   ","026 Organ 4   ","027 Organ 5   ","028 RotaryOrg1","029 RotaryOrg2",
		   "030 PipeOrgan1","031 PipeOrg1LP","032 Pipeorgan2","033 PipeOrg2LP","034 PipeOrgan3",
		   "035 PipeOrg3LP","036 Musette   ","037 Musette V ","038 BandNeon  ","039 BandNeonLP",
		   "040 Accordion ","041 AcordionLP","042 Harmonica ","043 G.Guitar  ","044 G.GuitarLP",
		   "045 F.Guitar  ","046 F.GuitarLP","047 F.Guitar V","048 A.Gtr Harm","049 E.Guitar 1",
		   "050 E.Guitr1 V","051 E.Guitar 2","052 E.Guitar 3","053 MuteGuitar","054 Funky Gtr ",
		   "055 FunkyGtr V","056 E.Gtr Harm","057 DistGuitar","058 Dist GtrLP","059 DistGuitrV",
		   "060 Over Drive","061 OverDrv LP","062 OverDrv F4","063 MuteDstGtr","064 MtDstGtr V",
		   "065 PowerChord","066 PowerChd V","067 OverDvChrd","068 Gtr Slide ","069 GtrSlide V",
		   "070 Sitar 1   ","071 Sitar 2   ","072 Sitar 2 LP","073 Santur    ","074 Bouzouki  ",
		   "075 BouzoukiLP","076 Banjo     ","077 Shamisen  ","078 Koto      ","079 Uood      ",
		   "080 Harp      ","081 MandlinTrm","082 A.Bass 1  ","083 A.Bass1 LP","084 A.Bass 2  ",
		   "085 A.Bass2 LP","086 E.Bass 1  ","087 E.Bass 1LP","088 E.Bass 2  ","089 Pick Bass1",
		   "090 Pick Bass1","091 PicBass1LP","092 Pick Bass2","093 Fretless  ","094 FretlessLP",
		   "095 Slap Bass1","096 Slap Bass2","097 SlpBass2LP","098 Slap Bass3","099 SynthBass1",
		   "100 SynBass1LP","101 SynthBass2","102 SynBass2LP","103 House Bass","104 FM Bass   ",
		   "105 FM Bass LP","106 Kalimba   ","107 Music Box ","108 MusicBoxLP","109 Log Drum  ",
		   "110 Marimba   ","111 Xylophone ","112 Vibe      ","113 Celesta   ","114 Glocken   ",
		   "115 BrightBell","116 B.Bell LP ","117 Metal Bell","118 M.Bell LP ","119 Gamelan   ",
		   "120 Pole      ","121 Pole LP   ","122 Tubular   ","123 Split Drum","124 Split Bell",
		   "125 Flute     ","126 Pan Flute ","127 PanFluteLP","128 Shakuhachi","129 ShakhachLP",
		   "130 Bottle    ","131 Recorder  ","132 Ocarina   ","133 Oboe      ","134 EnglishHrn",
		   "135 Eng.HornLP","136 BasoonOboe","137 BsonOboeLP","138 Clarinet  ","139 ClarinetLP",
		   "140 Bari Sax  ","141 Bari.SaxLP","142 Tenor Sax ","143 T.Sax LP  ","144 Alto Sax  ",
		   "145 A.Sax LP  ","146 SopranoSax","147 S.Sax LP  ","148 Tuba      ","149 Tuba LP   ",
		   "150 Horn      ","151 FlugelHorn","152 Trombone 1","153 Trombone 2","154 Trumpet   ",
		   "155 Trumpet LP","156 Mute TP   ","157 Mute TP LP","158 Brass 1   ","159 Brass 1 LP",
		   "160 Brass 2   ","161 Brass 2 LP","162 StringEns.","163 StrEns. V1","164 StrEns. V2",
		   "165 StrEns. V3","166 AnaStrings","167 PWM       ","168 Violin    ","169 Cello     ",
		   "170 Cello LP  ","171 Pizzicato ","172 Voice     ","173 Choir     ","174 Soft Choir",
		   "175 Air Vox   ","176 Doo Voice ","177 DooVoiceLP","178 Syn Vox   ","179 Syn Vox LP",
		   "180 White Pad ","181 Ether Bell","182 E.Bell LP ","183 Mega Pad  ","184 Spectrum 1",
		   "185 Spectrum 2","186 Stadium   ","187 Stadium NT","188 BrushNoise","189 BruNoiseNT",
		   "190 Steel Drum","191 SteelDrmLP","192 BrushSwirl","193 Belltree  ","194 BelltreeNT",
		   "195 BeltreV NT","196 Tri Roll  ","197 TriRoll NT","198 Telephon  ","199 TelephonNT",
		   "200 Clicker   ","201 Clicker NT","202 Crickets 1","203 Crickts1NT","204 Crickets 2",
		   "205 Crickts2NT","206 Magic Bell","207 Sporing   ","208 Rattle    ","209 Kava 1    ",
		   "210 Kava 2    ","211 Fever 1   ","212 Fever 2   ","213 Zappers 1 ","214 Zappers 2 ",
		   "215 Bugs      ","216 Surfy     ","217 SleighBell","218 Elec Beat ","219 Idling    ",
		   "220 EthnicBeat","221 Taps      ","222 Tap 1     ","223 Tap 2     ","224 Tap 3     ",
		   "225 Tap 4     ","226 Tap 2     ","227 Orch Hit  ","228 SnareRl/Ht","229 Syn Snare ",
		   "230 Rev Snare ","231 PowerSnare","232 Orch Perc ","233 Crash Cym ","234 CrashCymLP",
		   "235 CrashLP NT","236 China Cym ","237 Splash Cym","238 Orch Crash","239 Tite HH   ",
		   "240 Tite HH NT","241 Bell Ride ","242 Ping Ride ","243 Timpani   ","244 Timpani LP",
		   "245 Cabasa    ","246 Cabasa LP ","247 Agogo     ","248 Cow Bell  ","249 Low Bongo ",
		   "250 Claves    ","251 Timbale   ","252 WoodBlock1","253 WoodBlock2","254 WoodBlock3",
		   "255 Taiko Hit ","256 Syn Claves","257 Melo Tom  ","258 ProccesTom","259 Syn Tom 1 ",
		   "260 Syn Tom 2 ","261 VocalSnare","262 Zap 1     ","263 Zap 2     ","264 Fret Zap 1",
		   "265 Fret Zap 2","266 Vibra Slap","267 Indust    ","268 Thing     ","269 Thing NT  ",
		   "270 FingerSnap","271 FingSnapNT","272 Tambourine","273 Hand Clap ","274 HandClapNT",
		   "275 Gun Shot  ","276 Castanet  ","277 CastanetNT","278 Snap      ","279 Snap NT   ",
		   "280 Gt Scratch","281 Side Stick","282 SideStikNT","283 TimbleSide","284 TimblSidNT",
		   "285 Syn Rim   ","286 Syn Rim NT","287 Open HH   ","288 OpenSyn HH","289 CloseSynHH",
		   "290 Sagat     ","291 Sagat NT  ","292 Sagatty   ","293 Sagatty NT","294 JingleBell",
		   "295 Taiko     ","296 Slap Bongo","297 Open Conga","298 Slap Conga","299 Palm Conga",
		   "300 Mute Conga","301 Tabla 1   ","302 Tabla 2   ","303 Maracas   ","304 SynMaracas",
		   "305 SynMarcsNT","306 MuteTriang","307 OpenTriang","308 Guiro     ","309 Guiro LP  ",
		   "310 Scratch Hi","311 ScratcHiNT","312 Scratch Lo","313 ScratcLoNT","314 ScracthDbl",
		   "315 ScratDblNT","316 Mini 1a   ","317 Digital 1 ","318 VS 102    ","319 VS 48     ",
		   "320 VS 52     ","321 VS 58     ","322 VS 71     ","323 VS 72     ","324 VS 88     ",
		   "325 VS 89     ","326 13 - 35   ","327 DWGSOrgan1","328 DWGSOrgan2","329 DWGS E.P. ",
		   "330 Saw       ","331 Square    ","332 Ramp      ","333 Pulse 25% ","334 Pulse 8%  ",
		   "335 Pulse 4%  ","336 Syn Sine  ","337 Sine      ","338 DJ Kit 1  ","339 DJ Kit 2  "
    };


  /**
   * Constructor.
   *
   * @param patch Patch to be edited
   */
  public KorgX3SingleEditor(Patch patch)
  {
    super ("Korg X3 Single Editor",patch);

    /** The tab pane under which all other JPanels lie */
    JTabbedPane tabPane = new JTabbedPane();

    /** The actual tabs under tabPane */
    JPanel globalTabPane = new JPanel();
    JPanel pitchTabPane = new JPanel();
    JPanel osc1TabPane = new JPanel();
    JPanel osc2TabPane = new JPanel();
    JPanel modulationTabPane = new JPanel();
    JPanel vdfmodTabPane = new JPanel();

    /** All these have GridBagLayout */
    globalTabPane.setLayout(new GridBagLayout());
    pitchTabPane.setLayout(new GridBagLayout());
    osc1TabPane.setLayout(new GridBagLayout());
    osc2TabPane.setLayout(new GridBagLayout());
    modulationTabPane.setLayout(new GridBagLayout());
    vdfmodTabPane.setLayout(new GridBagLayout());

    tabPane.addTab("Global", globalTabPane);
    tabPane.addTab("Pitch EG", pitchTabPane);
    tabPane.addTab("Osc1", osc1TabPane);
    tabPane.addTab("Osc2", osc2TabPane);
    tabPane.addTab("Pitch Mod", modulationTabPane);
    tabPane.addTab("VDF Mod,AT&JC", vdfmodTabPane);

    /** Global pane under globalTabPane */

    JPanel globalPane = new JPanel();
    globalPane.setLayout(new GridBagLayout());
    addWidget(globalPane, new PatchNameWidget(" Name  ",patch),0,0,1,1,0);
    addWidget(globalPane, new ComboBoxWidget("Osc Mode",patch,new X3Model(patch,10),
					     new X3Sender(10),new String []{"SINGLE","DOUBLE","DRUMS"}),1,0,1,1,1);
    addWidget(globalPane, new ComboBoxWidget("Assign",patch,new X3Model(patch,11,0),
					     new X3Sender(11),new String []{"POLY","MONO"}),2,0,1,1,2);
    addWidget(globalPane, new ComboBoxWidget("hold",patch,new X3Model(patch,11,1),
					     new X3Sender(11),new String []{"OFF","ON"}),3,0,1,1,3);
    gbc.gridx=0;gbc.gridy=0;gbc.gridwidth=4;gbc.gridheight=2;gbc.fill=GridBagConstraints.BOTH;gbc.anchor=GridBagConstraints.EAST;
    globalPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Global",TitledBorder.CENTER,TitledBorder.CENTER));
    globalTabPane.add(globalPane,gbc);


    /* Osc1 setup pane under globalTabPane */

    JPanel osc1setupPane = new JPanel();
    osc1setupPane.setLayout(new GridBagLayout());gbc.weightx=1;
    addWidget(osc1setupPane,new ComboBoxWidget("Multisound",patch,new MultiSoundModel(patch, 12),new MultiSoundSender(12),multiSounds),0,0,1,1,4);
    addWidget(osc1setupPane,new ScrollBarWidget("Octave",patch,-2,1,0,new X3Model(patch,14),new X3Sender(14)),1,0,1,1,5);
    addWidget(osc1setupPane,new ScrollBarWidget("Level",patch,0,99,0,new X3Model(patch,65),new X3Sender(65)),0,1,2,1,6);


    addWidget(osc1setupPane,new ScrollBarWidget("Pitch EG Int",patch,-99,99,0,new X3Model(patch,40),new X3Sender(40)),0,2,2,1,7);

    gbc.gridx=0;gbc.gridy=6;gbc.gridwidth=4;gbc.gridheight=2;gbc.fill=GridBagConstraints.BOTH;gbc.anchor=GridBagConstraints.EAST;
    osc1setupPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Osc1 setup",TitledBorder.CENTER,TitledBorder.CENTER));

    globalTabPane.add(osc1setupPane, gbc);

    /* Osc2 setup pane under globalTabPane */

    JPanel osc2setupPane = new JPanel();
    osc2setupPane.setLayout(new GridBagLayout());gbc.weightx=1;
    addWidget(osc2setupPane,new ComboBoxWidget("Multisound",patch,new MultiSoundModel(patch, 15),new MultiSoundSender(15),multiSounds),0,0,1,1,8);
    addWidget(osc2setupPane,new ScrollBarWidget("Octave",patch,-2,1,0,new X3Model(patch,17),new X3Sender(17)),1,0,1,1,9);
    addWidget(osc2setupPane,new ScrollBarWidget("Level",patch,0,99,0,new X3Model(patch,112),new X3Sender(112)),0,1,2,1,10);

    addWidget(osc2setupPane,new ScrollBarWidget("Pitch EG Int",patch,-99,99,0,new X3Model(patch,87),new X3Sender(87)),0,2,2,1,11);
    addWidget(osc2setupPane,new ScrollBarWidget("Interval",patch,-12,12,0,new X3Model(patch,18),new X3Sender(18)),0,3,2,1,12);
    addWidget(osc2setupPane,new ScrollBarWidget("Detune",patch,-50,50,0,new X3Model(patch,19),new X3Sender(19)),0,4,2,1,13);
    addWidget(osc2setupPane,new ScrollBarWidget("Delay",patch,0,99,0,new X3Model(patch,20),new X3Sender(20)),0,5,2,1,14);

    gbc.gridx=0;gbc.gridy=11;gbc.gridwidth=1;gbc.gridheight=2;gbc.fill=GridBagConstraints.BOTH;gbc.anchor=GridBagConstraints.EAST;
    osc2setupPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Osc2 setup",TitledBorder.CENTER,TitledBorder.CENTER));

    globalTabPane.add(osc2setupPane, gbc);

    /** Pitch EG Tab */

    JPanel pitchegPane = new JPanel();
    pitchegPane.setLayout(new GridBagLayout());
    gbc.weightx=1;

    addWidget(pitchegPane,new ScrollBarWidget("Level Vel.",patch,-99,99,0,new X3Model(patch,28),new X3Sender(28)),0,1,1,1,15);
    addWidget(pitchegPane,new ScrollBarWidget("Time Vel.",patch,-99,99,0,new X3Model(patch,27),new X3Sender(27)),0,2,1,1,16);

    addWidget(pitchegPane,new EnvelopeWidget("PITCHEG",patch,new EnvelopeWidget.Node [] {
      new EnvelopeWidget.Node(0,0,null,-99,99,new X3Model(patch,21),99,false,null,new X3Sender(21),null,"SL"),
      new EnvelopeWidget.Node(0,99,new X3Model(patch,22),-99,99,new X3Model(patch,23),99,false,new X3Sender(22),new X3Sender(23),"AT","AL"),
      new EnvelopeWidget.Node(0,99,new X3Model(patch,24),99,99,null,0,false,new X3Sender(24),null,"DT",null),
      new EnvelopeWidget.Node(0,99,new X3Model(patch,25),-99,99,new X3Model(patch,26),99,false,new X3Sender(25),new X3Sender(26),"RT","RL")

    }),0,0,1,1,17);
    gbc.gridx=10;gbc.gridy=0;gbc.gridwidth=7;gbc.gridheight=9;
    pitchegPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"PITCH EG",TitledBorder.CENTER,TitledBorder.CENTER));

    pitchTabPane.add(pitchegPane, gbc);


    /** OSC 1 Tab */

    // VDF1EG pane under osc1TabPane
    JPanel vdf1egPane = new JPanel();
    vdf1egPane.setLayout(new GridBagLayout());
    gbc.weightx=1;
    addWidget(vdf1egPane,new ScrollBarWidget("Fc",patch,0,99,0,new X3Model(patch,50),new X3Sender(50)),0,1,1,1,23);
    addWidget(vdf1egPane,new ScrollBarWidget("EGint",patch,-99,99,0,new X3Model(patch,53),new X3Sender(53)),0,2,1,1,24);
    addWidget(vdf1egPane,new EnvelopeWidget("VDF1EG",patch,new EnvelopeWidget.Node [] {
      new EnvelopeWidget.Node(0,0,null,99,99,null,0,false,null,null,null,null),
      new EnvelopeWidget.Node(0,99,new X3Model(patch,57),-99,99,new X3Model(patch,58),99,false,new X3Sender(57),new X3Sender(58),"AT","AL"),
      new EnvelopeWidget.Node(0,99,new X3Model(patch,59),-99,99,new X3Model(patch,60),99,false,new X3Sender(59),new X3Sender(60),"DT","BP"),
      new EnvelopeWidget.Node(0,99,new X3Model(patch,61),-99,99,new X3Model(patch,62),99,false,new X3Sender(61),new X3Sender(62),"ST","SL"),
      new EnvelopeWidget.Node(0,99,new X3Model(patch,63),-99,99,new X3Model(patch,64),99,false,new X3Sender(63),new X3Sender(64),"RT","RL")
    }),0,0,1,1,25);
    gbc.gridx=10;gbc.gridy=0;gbc.gridwidth=7;gbc.gridheight=9;
    vdf1egPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"VDF1 EG",TitledBorder.CENTER,TitledBorder.CENTER));

    osc1TabPane.add(vdf1egPane, gbc);

    JPanel vda1egPane = new JPanel();
    vda1egPane.setLayout(new GridBagLayout());
    gbc.weightx=1;
    addWidget(vda1egPane,new EnvelopeWidget("VDA1EG",patch,new EnvelopeWidget.Node [] {
      new EnvelopeWidget.Node(0,0,null,0,0,null,0,false,null,null,null,null),
      new EnvelopeWidget.Node(0,99,new X3Model(patch,71),0,99,new X3Model(patch,72),0,false,new X3Sender(71),new X3Sender(72),"AT","AL"),
      new EnvelopeWidget.Node(0,99,new X3Model(patch,73),0,99,new X3Model(patch,74),0,false,new X3Sender(73),new X3Sender(74),"DT","BP"),
      new EnvelopeWidget.Node(0,99,new X3Model(patch,75),0,99,new X3Model(patch,76),0,false,new X3Sender(75),new X3Sender(76),"ST","SL"),
      new EnvelopeWidget.Node(0,99,new X3Model(patch,77),0,0,null,0,false,new X3Sender(77),null,"RT",null)
    }),0,0,1,1,33);
    gbc.gridx=10;gbc.gridy=10;gbc.gridwidth=7;gbc.gridheight=9;
    vda1egPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"VDA1 EG",TitledBorder.CENTER,TitledBorder.CENTER));

    osc1TabPane.add(vda1egPane, gbc);

    /** OSC 2 Tab */

    // VDF2EG pane under osc2TabPane
    JPanel vdf2egPane = new JPanel();
    vdf2egPane.setLayout(new GridBagLayout());
    gbc.weightx=1;
    addWidget(vdf2egPane,new ScrollBarWidget("Fc",patch,0,99,0,new X3Model(patch,97),new X3Sender(97)),0,1,1,1,39);
    addWidget(vdf2egPane,new ScrollBarWidget("EGint",patch,-99,99,0,new X3Model(patch,100),new X3Sender(100)),0,2,1,1,40);
    addWidget(vdf2egPane,new EnvelopeWidget("VDF2EG",patch,new EnvelopeWidget.Node [] {
      new EnvelopeWidget.Node(0,0,null,99,99,null,0,false,null,null,null,null),
      new EnvelopeWidget.Node(0,99,new X3Model(patch,104),-99,99,new X3Model(patch,105),99,false,new X3Sender(104),new X3Sender(105),"AT","AL"),
      new EnvelopeWidget.Node(0,99,new X3Model(patch,106),-99,99,new X3Model(patch,107),99,false,new X3Sender(106),new X3Sender(107),"DT","BP"),
      new EnvelopeWidget.Node(0,99,new X3Model(patch,108),-99,99,new X3Model(patch,109),99,false,new X3Sender(108),new X3Sender(109),"ST","SL"),
      new EnvelopeWidget.Node(0,99,new X3Model(patch,110),-99,99,new X3Model(patch,111),99,false,new X3Sender(110),new X3Sender(111),"RT","RL")
    }),0,0,1,1,41);
    gbc.gridx=10;gbc.gridy=0;gbc.gridwidth=7;gbc.gridheight=9;
    vdf2egPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"VDF2 EG",TitledBorder.CENTER,TitledBorder.CENTER));

    osc2TabPane.add(vdf2egPane, gbc);

    JPanel vda2egPane = new JPanel();
    vda2egPane.setLayout(new GridBagLayout());
    gbc.weightx=1;
    addWidget(vda2egPane,new EnvelopeWidget("VDA2EG",patch,new EnvelopeWidget.Node [] {
      new EnvelopeWidget.Node(0,0,null,0,0,null,0,false,null,null,null,null),
      new EnvelopeWidget.Node(0,99,new X3Model(patch,118),0,99,new X3Model(patch,119),0,false,new X3Sender(118),new X3Sender(119),"AT","AL"),
      new EnvelopeWidget.Node(0,99,new X3Model(patch,120),0,99,new X3Model(patch,121),0,false,new X3Sender(120),new X3Sender(121),"DT","BP"),
      new EnvelopeWidget.Node(0,99,new X3Model(patch,122),0,99,new X3Model(patch,123),0,false,new X3Sender(122),new X3Sender(123),"ST","SL"),
      new EnvelopeWidget.Node(0,99,new X3Model(patch,124),0,0,null,0,false,new X3Sender(125),null,"RT",null)
    }),0,0,1,1,49);
    gbc.gridx=10;gbc.gridy=10;gbc.gridwidth=7;gbc.gridheight=9;
    vda2egPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"VDA2 EG",TitledBorder.CENTER,TitledBorder.CENTER));

    osc2TabPane.add(vda2egPane, gbc);

    /** Modulation Tab */

    JPanel pitch1ModPane = new JPanel();
    pitch1ModPane.setLayout(new GridBagLayout());
    addWidget(pitch1ModPane, new ComboBoxWidget("Waveform",patch,new X3Model(patch,41,0,3),
						new X3Sender(41),new String []{"TRI","SAW UP","SAW DOWN","SQR1","RAND","SQR2"}),0,0,1,1,56);
    addWidget(pitch1ModPane, new CheckBoxWidget("Key Sync",patch,new X3Model(patch,41,7),new X3Sender(41)),1,0,1,1,57);
    addWidget(pitch1ModPane,new ScrollBarWidget("Frequency",patch,0,99,0,new X3Model(patch,42),new X3Sender(42)),0,1,2,1,57);
    addWidget(pitch1ModPane,new ScrollBarWidget("Intensity",patch,0,99,0,new X3Model(patch,45),new X3Sender(45)),0,2,2,1,58);
    addWidget(pitch1ModPane,new ScrollBarWidget("Delay",patch,0,99,0,new X3Model(patch,43),new X3Sender(43)),0,3,2,1,59);
    addWidget(pitch1ModPane,new ScrollBarWidget("Fade In",patch,0,99,0,new X3Model(patch,44),new X3Sender(44)),0,4,2,1,60);
    addWidget(pitch1ModPane,new ScrollBarWidget("Keyb. Tracking",patch,-99,99,0,new X3Model(patch,46),new X3Sender(46)),0,5,2,1,61);
    addWidget(pitch1ModPane,new ScrollBarWidget("AT Mod. Int.",patch,0,99,0,new X3Model(patch,47),new X3Sender(47)),0,6,2,1,62);
    addWidget(pitch1ModPane,new ScrollBarWidget("JS Mod. Int.",patch,0,99,0,new X3Model(patch,48),new X3Sender(48)),0,7,2,1,63);
    addWidget(pitch1ModPane,new ScrollBarWidget("After Touch+Joy",patch,0,9,0,new X3Model(patch,49),new X3Sender(49)),0,8,2,1,64);
    gbc.gridx=0;gbc.gridy=0;gbc.gridwidth=2;gbc.gridheight=9;
    pitch1ModPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Pitch 1 Modulation",TitledBorder.CENTER,TitledBorder.CENTER));

    modulationTabPane.add(pitch1ModPane, gbc);

    JPanel pitch2ModPane = new JPanel();
    pitch2ModPane.setLayout(new GridBagLayout());
    addWidget(pitch2ModPane, new ComboBoxWidget("Waveform",patch,new X3Model(patch,88,0,3),
						new X3Sender(87),new String []{"TRI","SAW UP","SAW DOWN","SQR1","RAND","SQR2"}),0,0,1,1,56);
    addWidget(pitch2ModPane, new CheckBoxWidget("Key Sync",patch,new X3Model(patch,88,7),new X3Sender(88)),1,0,1,1,57);
    addWidget(pitch2ModPane,new ScrollBarWidget("Frequency",patch,0,99,0,new X3Model(patch,89),new X3Sender(89)),0,1,2,1,57);
    addWidget(pitch2ModPane,new ScrollBarWidget("Intensity",patch,0,99,0,new X3Model(patch,92),new X3Sender(92)),0,2,2,1,58);
    addWidget(pitch2ModPane,new ScrollBarWidget("Delay",patch,0,99,0,new X3Model(patch,90),new X3Sender(90)),0,3,2,1,59);
    addWidget(pitch2ModPane,new ScrollBarWidget("Fade In",patch,0,99,0,new X3Model(patch,91),new X3Sender(91)),0,4,2,1,60);
    addWidget(pitch2ModPane,new ScrollBarWidget("Keyb. Tracking",patch,-99,99,0,new X3Model(patch,93),new X3Sender(93)),0,5,2,1,61);
    addWidget(pitch2ModPane,new ScrollBarWidget("AT Mod. Int.",patch,0,99,0,new X3Model(patch,94),new X3Sender(94)),0,6,2,1,62);
    addWidget(pitch2ModPane,new ScrollBarWidget("JS Mod. Int.",patch,0,99,0,new X3Model(patch,95),new X3Sender(95)),0,7,2,1,63);
    addWidget(pitch2ModPane,new ScrollBarWidget("After Touch+Joy",patch,0,9,0,new X3Model(patch,96),new X3Sender(96)),0,8,2,1,64);
    gbc.gridx=0;gbc.gridy=10;gbc.gridwidth=2;gbc.gridheight=9;
    pitch2ModPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Pitch 2 Modulation",TitledBorder.CENTER,TitledBorder.CENTER));

    modulationTabPane.add(pitch2ModPane, gbc);

    /** VDF Modulation */

    JPanel vdfModPane = new JPanel();
    vdfModPane.setLayout(new GridBagLayout());
    addWidget(vdfModPane, new ComboBoxWidget("Waveform",patch,new X3Model(patch,29,0,3),
						new X3Sender(29),new String []{"TRI","SAW UP","SAW DOWN","SQR1","RAND","SQR2"}),0,0,1,1,65);
    addWidget(vdfModPane, new CheckBoxWidget("OSC1 MG",patch,new X3Model(patch,29,5),new X3Sender(29)),1,0,1,1,66);
    addWidget(vdfModPane, new CheckBoxWidget("OSC2 MG",patch,new X3Model(patch,29,6),new X3Sender(29)),2,0,1,1,67);
    addWidget(vdfModPane, new CheckBoxWidget("K.Sync",patch,new X3Model(patch,29,7),new X3Sender(29)),3,0,1,1,68);
    addWidget(vdfModPane,new ScrollBarWidget("Frequency",patch,0,99,0,new X3Model(patch,30),new X3Sender(30)),0,1,4,1,69);
    addWidget(vdfModPane,new ScrollBarWidget("Intensity",patch,0,99,0,new X3Model(patch,32),new X3Sender(32)),0,2,4,1,70);
    addWidget(vdfModPane,new ScrollBarWidget("Delay",patch,0,99,0,new X3Model(patch,31),new X3Sender(31)),0,3,4,1,71);

    gbc.gridx=0;gbc.gridy=0;gbc.gridwidth=4;gbc.gridheight=5;
    vdfModPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"VDF Modulation",TitledBorder.CENTER,TitledBorder.CENTER));

    vdfmodTabPane.add(vdfModPane, gbc);

    // ---

    JPanel atModPane = new JPanel();
    atModPane.setLayout(new GridBagLayout());
    addWidget(atModPane,new ScrollBarWidget("Pitch Bend",patch,-12,12,0,new X3Model(patch,33),new X3Sender(33)),0,0,1,1,72);
    addWidget(atModPane,new ScrollBarWidget("VDF Cutoff Fc",patch,-99,99,0,new X3Model(patch,34),new X3Sender(34)),0,1,1,1,73);
    addWidget(atModPane,new ScrollBarWidget("VDF MG Int",patch,0,99,0,new X3Model(patch,35),new X3Sender(35)),0,2,1,1,74);
    addWidget(atModPane,new ScrollBarWidget("VDA Amplitude",patch,-99,99,0,new X3Model(patch,36),new X3Sender(36)),0,3,1,1,75);
    gbc.gridx=0;gbc.gridy=5;gbc.gridwidth=4;gbc.gridheight=5;
    atModPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"After Touch Control",TitledBorder.CENTER,TitledBorder.CENTER));

    vdfmodTabPane.add(atModPane, gbc);

    // ---

    JPanel jtModPane = new JPanel();
    jtModPane.setLayout(new GridBagLayout());
    addWidget(jtModPane,new ScrollBarWidget("Pitch Bend",patch,-12,12,0,new X3Model(patch,37),new X3Sender(37)),0,0,1,1,76);
    addWidget(jtModPane,new ScrollBarWidget("VDF MG Int",patch,0,99,0,new X3Model(patch,39),new X3Sender(39)),0,1,1,1,77);
    addWidget(jtModPane,new ScrollBarWidget("VDF Sweep",patch,-99,99,0,new X3Model(patch,38),new X3Sender(38)),0,2,1,1,78);
    gbc.gridx=0;gbc.gridy=10;gbc.gridwidth=4;gbc.gridheight=5;
    jtModPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Joystick Control",TitledBorder.CENTER,TitledBorder.CENTER));

    vdfmodTabPane.add(jtModPane, gbc);


    // show tab
    scrollPane.add(tabPane, gbc);
    pack();
    show();
  }
}

/**
 * X3Sender. NOTE that this actually does nothing, it is
 * obligatory to use this. You cannot send parameters one by
 * one to KorgX3 easily because of the changed indexes.
 * When you for example play the patch using JSynthLib, the
 * whole patch is sent to the synthesizer.
 */
class X3Sender extends SysexSender
{

  int param;
  int src;
  byte[]b=new byte[10];

  public X3Sender(int param) {
    this.param=param;
  }

  public X3Sender(int param, int src) {
    this.param=param;
    this.src=src;
  }

}

/**
 * See X3Sender.
 */
class MultiSoundSender extends SysexSender
{
  int param;

  public MultiSoundSender(int param) {
    this.param=param;
  }
}

/**
 * Class for setting values in banks.
 */
class X3Model extends ParamModel
{
  private int bit=-1;
  private int bits=-1;

  /**
   * Constructor for normal case, we want to set all the 8 bits of a byte
   *
   * @param p Patch
   * @param o Offset
   */
  public X3Model(Patch p, int o) {
    ofs = o;
    patch = p;
  }

  /**
   * Constructor for setting just one bit of byte
   *
   * @param p Patch
   * @param o Offset
   * @param bit Which bit to set (0-7)
   */
  public X3Model(Patch p, int o, int bit) {
    ofs = o;
    patch = p;
    this.bit = bit;
  }

  /**
   * Constructor for setting <code>bits</code> bits starting from bit <code>bit</code>
   *
   * @param p Patch
   * @param o Offset
   * @param bit Starting bit
   * @param bits How many bits affected from <code>bit</code>
   */
  public X3Model(Patch p, int o, int bit, int bits) {
    ofs = o;
    patch = p;
    this.bit = bit;
    this.bits = bits;
  }

  /**
   * Sets the value of the certain patch's offset.
   * By default changes all the 8 bits of byte.
   * If variable bit is different from -1, that bit
   * will be changed. If variable bits is set,
   * that many bits starting from 'bit' will be set.
   *
   * @param i value to be set
   */
  public void set(int i) {
    if(bit==-1) {
      // using all 8 bits of byte
      patch.sysex[ofs+KorgX3SingleDriver.EXTRA_HEADER]=(byte)i;
    } else {
      if(bits==-1) {
	//using just one bit of byte
	patch.sysex[ofs+KorgX3SingleDriver.EXTRA_HEADER]=
	  (byte)((patch.sysex[ofs+KorgX3SingleDriver.EXTRA_HEADER] & ~(1<<bit)) + i<<bit);
      } else {
	//using 'bits' bits of byte starting from 'bit'
	byte mask = (byte)0xFF;

	for(int j = 0; j < bits; j++) {
	  mask = (byte)(mask & ~(1<<(bit+j)));
	}

	// now we have a mask for example   11100011.
	// and the appropriate addition ex. 00011000.

	patch.sysex[ofs+KorgX3SingleDriver.EXTRA_HEADER]=
	  (byte)((patch.sysex[ofs+KorgX3SingleDriver.EXTRA_HEADER] & mask) + i<<bit);
      }
    }
  }

  /**
   * The appropriate getter. See set() and constructors for examples.
   *
   * @return either all 8 bits, 1 bit or n bits
   */
  public int get() {
    if(bit==-1) {
      return patch.sysex[ofs+KorgX3SingleDriver.EXTRA_HEADER];
    } else {
      if(bits==-1) {
	//using just one bit of byte
	return (byte)((patch.sysex[ofs+KorgX3SingleDriver.EXTRA_HEADER] & (byte)(1<<bit)) >> bit)  ;
      } else {
	//using 'bits' bits
	byte mask=(byte)0x00;
	for(int j = 0 ; j < bits; j++) {
	  mask+=(byte)(1<<(bit+j));
	}
	return (byte)((patch.sysex[ofs+KorgX3SingleDriver.EXTRA_HEADER] & mask) >> bit);
      }
    }
  }
}

/**
 * Special case for setting multisounds. Actually just uses two bytes
 * instead of one that is has LSB and MSB.
 */
class MultiSoundModel extends ParamModel
{
  public MultiSoundModel(Patch p, int o) {
    ofs = o;
    patch = p;
  }

  // ofs+EXTRA_HEADER+1 has the MSB and ofs+EXTRA_HEADER has the LSB
  // example: 314 = 0x13A = 1 00111010
  public void set(int i) {
    patch.sysex[ofs+KorgX3SingleDriver.EXTRA_HEADER]=(byte)(i&0xFF); //LSB
    patch.sysex[ofs+KorgX3SingleDriver.EXTRA_HEADER+1]=(byte)(i>>8); //MSB
  }

  public int get() {
    return ((patch.sysex[ofs+KorgX3SingleDriver.EXTRA_HEADER+1]<<8) + ((patch.sysex[ofs+KorgX3SingleDriver.EXTRA_HEADER]+256)%256));
  }
}
