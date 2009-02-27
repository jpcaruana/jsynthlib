package org.jsynthlib.drivers.alesis.qs;

/**
 * Various constants used for the QS Synths
 * @author Zellyn Hunter (zellyn@bigfoot.com, zjh)
 * @version $Id$
 */

public class QSConstants
{
  // \u00A5 = Yen
  // \u00AB = left arrow
  // \u00BB = right arrow
  /** Characters as stored in the Alesis, for translation */
  public static final String QS_LETTERS = " !\"#$%&'{}*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\u00A5]^_`abcdefghijklmnopqrstuvwxyz{|}\u00BB\u00AB";
  /** Character to use if we can't translate from an Alesis character */
  public static final char QS_UNKNOWN_CHARACTER = '?';
  /** Character code to use if we can't translate to an Alesis character */
  public static final int QS_UNKNOWN_CHARACTER_CODE = QS_LETTERS.indexOf('?');

  // Constants that define various properties of Alesis sysex messages

  /** The size of the header for Alesis messages */
  public static final int HEADER = 7;

  /** The starting bit of the Name of a Program */
  public static final int PROG_NAME_START = (1<<3) + 6;
  /** The number of characters in the Name of a Program */
  public static final int PROG_NAME_LENGTH = 10;

  /** The starting bit of the Name of a Mix */
  public static final int MIX_NAME_START = (1<<3) + 3;
  /** The number of characters in the Name of a Mix */
  public static final int MIX_NAME_LENGTH = 10;

  /** The location in the Sysex where the function opcode is specified */
  public static final int POSITION_OPCODE = 5;
  /** The location in the Sysex where the location is specified */
  public static final int POSITION_LOCATION = 6;

  /** Opcode 00 - MIDI User Program Dump */
  public static final byte OPCODE_MIDI_USER_PROG_DUMP = 0;
  /** Opcode 01 - MIDI User Program Dump Request */
  public static final byte OPCODE_MIDI_USER_PROG_DUMP_REQ = 1;
  /** Opcode 02 - MIDI Edit Program Dump */
  public static final byte OPCODE_MIDI_EDIT_PROG_DUMP = 2;
  /** Opcode 03 - MIDI Edit Program Dump Request */
  public static final byte OPCODE_MIDI_EDIT_PROG_DUMP_REQ = 3;

  /** Opcode 06 - MIDI User Effects Dump */
  public static final byte OPCODE_MIDI_USER_EFFECTS_DUMP = 6;
  /** Opcode 07 - MIDI User Effects Dump Request */
  public static final byte OPCODE_MIDI_USER_EFFECTS_DUMP_REQ = 7;
  /** Opcode 08 - MIDI Edit Effects Dump */
  public static final byte OPCODE_MIDI_EDIT_EFFECTS_DUMP = 8;
  /** Opcode 09 - MIDI Edit Effects Dump Request */
  public static final byte OPCODE_MIDI_EDIT_EFFECTS_DUMP_REQ = 9;

  /** Opcode 0A - MIDI Global Data Dump */
  public static final byte OPCODE_MIDI_GLOBAL_DATA_DUMP = 0x0A;
  /** Opcode 0B - MIDI Global Data Dump Request */
  public static final byte OPCODE_MIDI_GLOBAL_DATA_DUMP_REQ = 0x0B;

  /** Opcode 0E - MIDI User Mix Dump */
  public static final byte OPCODE_MIDI_USER_MIX_DUMP = 0x0E;
  /** Opcode 0F - MIDI User Mix Dump Request */
  public static final byte OPCODE_MIDI_USER_MIX_DUMP_REQ = 0x0F;


  // Constants for Driver properties

  /** Manufacturer name */
  public static final String MANUFACTURER = "Alesis";
  /** Model names supported */
  public static final String MODEL = "QS7/QS8/QSR";
  /** Inquiry ID - the response of the Synth to the Midi inquiry */
  public static final String INQUIRY_ID="F07E7F060200000E0E000*00********f7";
  /** Size of program patches */
  public static final int PATCH_SIZE_PROGRAM = 408;
  /** Size of mix patches */
  public static final int PATCH_SIZE_MIX = 166;
  /** Size of effects patches */
  public static final int PATCH_SIZE_EFFECTS = 83;
  /** Size of global data */
  public static final int PATCH_SIZE_GLOBAL = 31;

  /** Maximum Program patch number - any higher, and it's edit buffers */
  public static final int MAX_LOCATION_PROG = 127;
  /** Maximum Effects patch number - any higher, and it's edit buffers */
  public static final int MAX_LOCATION_EFFECTS = 127;
  /** Maximum Mix patch number - any higher, and it's edit buffers */
  public static final int MAX_LOCATION_MIX = 99;


  /** Generic patch header - the bytes that start all patches */
  public static final byte[] GENERIC_HEADER = new byte[] {(byte)0xF0, 0x00, 0x00, 0x0E, 0x0E};

  /** Empty program name - for creating new programs */
  public static final String DEFAULT_NAME_PROG = "-New Prog-";

  /** Empty mix name - for creating new mixes */
  public static final String DEFAULT_NAME_MIX = "-New Mix-";

  /** Names of the banks that can be modified */
  public static final String[] WRITEABLE_BANK_NAMES =new String[] {"User"};

  /** Dummy bank names for global data */
  public static final String[] GLOBAL_BANK_NAME_DUMMY =new String[] {"(global)"};

  /** Dummy patch locations for global data */
  public static final String[] GLOBAL_PATCH_NUMBERS_DUMMY =new String[] {"(global)"};

  /** Patch locations for program patches, including edit buffers */
  public static final String[] PATCH_NUMBERS_PROGRAM_WITH_EDIT_BUFFERS =
    new String[] {"00-","01-","02-","03-","04-","05-","06-","07-",
                  "08-","09-","10-","11-","12-","13-","14-","15-",
                  "16-","17-","18-","19-","20-","21-","22-","23-",
                  "24-","25-","26-","27-","28-","29-","30-","31-",
                  "32-","33-","34-","35-","36-","37-","38-","39-",
                  "40-","41-","42-","43-","44-","45-","46-","47-",
                  "48-","49-","50-","51-","52-","53-","54-","55-",
                  "56-","57-","58-","59-","60-","61-","62-","63-",
                  "64-","65-","66-","67-","68-","69-","70-","71-",
                  "72-","73-","74-","75-","76-","77-","78-","79-",
                  "80-","81-","82-","83-","84-","85-","86-","87-",
                  "88-","89-","90-","91-","92-","93-","94-","95-",
                  "96-","97-","98-","99-","100-","101-","102-","103-",
                  "104-","105-","106-","107-",
                  "108-","109-","110-","111-","112-","113-","114-","115-",
                  "116-","117-","118-","119-","120-","121-","122-","123-",
                  "124-","125-","126-","127",
                  "Edit",
                  "Em0", "Em1", "Em2", "Em3", "Em4", "Em5", "Em6", "Em7",
                  "Em8", "Em9", "Em10", "Em11", "Em12", "Em13", "Em14", "Em15"};
  /** Patch locations for program patches, excluding edit buffers */
  public static final String[] PATCH_NUMBERS_PROGRAM =
    new String[] {"00-","01-","02-","03-","04-","05-","06-","07-",
                  "08-","09-","10-","11-","12-","13-","14-","15-",
                  "16-","17-","18-","19-","20-","21-","22-","23-",
                  "24-","25-","26-","27-","28-","29-","30-","31-",
                  "32-","33-","34-","35-","36-","37-","38-","39-",
                  "40-","41-","42-","43-","44-","45-","46-","47-",
                  "48-","49-","50-","51-","52-","53-","54-","55-",
                  "56-","57-","58-","59-","60-","61-","62-","63-",
                  "64-","65-","66-","67-","68-","69-","70-","71-",
                  "72-","73-","74-","75-","76-","77-","78-","79-",
                  "80-","81-","82-","83-","84-","85-","86-","87-",
                  "88-","89-","90-","91-","92-","93-","94-","95-",
                  "96-","97-","98-","99-","100-","101-","102-","103-",
                  "104-","105-","106-","107-",
                  "108-","109-","110-","111-","112-","113-","114-","115-",
                  "116-","117-","118-","119-","120-","121-","122-","123-",
                  "124-","125-","126-","127"};

  /** Patch locations for mix patches, including edit buffer */
  public static final String[] PATCH_NUMBERS_MIX_WITH_EDIT_BUFFER =
    new String[] {"00-","01-","02-","03-","04-","05-","06-","07-",
                  "08-","09-","10-","11-","12-","13-","14-","15-",
                  "16-","17-","18-","19-","20-","21-","22-","23-",
                  "24-","25-","26-","27-","28-","29-","30-","31-",
                  "32-","33-","34-","35-","36-","37-","38-","39-",
                  "40-","41-","42-","43-","44-","45-","46-","47-",
                  "48-","49-","50-","51-","52-","53-","54-","55-",
                  "56-","57-","58-","59-","60-","61-","62-","63-",
                  "64-","65-","66-","67-","68-","69-","70-","71-",
                  "72-","73-","74-","75-","76-","77-","78-","79-",
                  "80-","81-","82-","83-","84-","85-","86-","87-",
                  "88-","89-","90-","91-","92-","93-","94-","95-",
                  "96-","97-","98-","99",
                  "Edit"};

  /** Patch locations for effects patches, including edit buffers */
  public static final String[] PATCH_NUMBERS_EFFECTS_WITH_EDIT_BUFFERS =
    new String[] {"00-","01-","02-","03-","04-","05-","06-","07-",
                  "08-","09-","10-","11-","12-","13-","14-","15-",
                  "16-","17-","18-","19-","20-","21-","22-","23-",
                  "24-","25-","26-","27-","28-","29-","30-","31-",
                  "32-","33-","34-","35-","36-","37-","38-","39-",
                  "40-","41-","42-","43-","44-","45-","46-","47-",
                  "48-","49-","50-","51-","52-","53-","54-","55-",
                  "56-","57-","58-","59-","60-","61-","62-","63-",
                  "64-","65-","66-","67-","68-","69-","70-","71-",
                  "72-","73-","74-","75-","76-","77-","78-","79-",
                  "80-","81-","82-","83-","84-","85-","86-","87-",
                  "88-","89-","90-","91-","92-","93-","94-","95-",
                  "96-","97-","98-","99-","100-","101-","102-","103-",
                  "104-","105-","106-","107-",
                  "108-","109-","110-","111-","112-","113-","114-","115-",
                  "116-","117-","118-","119-","120-","121-","122-","123-",
                  "124-","125-","126-","127",
                  "ProgEdit", "MixEdit"};


  // Names of Programs and Mixes of factory settings - these will be
  // used for the Mix Editor, so you can tell which programs you're
  // referencing in the Mix.  Captured from the Sysex files from
  // the Alesis website

  /** Preset Bank 1 Program names */
  public static final String[] PRESET_1_BANK_PROG_NAMES =
  {
    "TrueStereo", "Titanium88", "OctavPiano", "PianoMorph", "BellPianah", "Rayz Roadz", "QS Tines  ", "ClascWurly",
    "FM E Piano", "Wave Piano", "Clavitube ", "Real Clav ", "TrueHarpsi", "Cool Vibes", "BriteMarim", "Kalimba   ",
    "Brake Drum", "St. Thomas", "Basic Bell", "ClockTower", "Real Prc B", "High Life ", "Grit Organ", "ABCDrawbar",
    "WhitrShade", "Toccata&Fg", "KingsCourt", "3rdHrmPerc", "FrAccrdian", "WhammerJmr", "Steel Ride", "GuildedAge",
    "Gitarala  ", "ThickNylon", "Fat Strat ", "TreMellow ", "Total Chug", "FacePlantr", "WorldSitar", "Koto Pluck",
    "BigUpright", "QS Bass   ", "007 Bass  ", "Slap It!  ", "VolumeKnob", "Fat Mini  ", "Filter Wow", "IndstryRez",
    "DeutschBas", "CyberBass ", "Violinist ", "MedSection", "String Vox", "LA Phil   ", "Arco Ensm ", "Bali Hai  ",
    "Obersphere", "J Strings ", "Pizz Pluck", "Harp Pluck", "FlugelSolo", "ClsclTrmpt", "Solo Tromb", "Dual Horns",
    "Real Brass", "Pop Brass ", "Bigg Brass", "Brass Pump", "ClassBrass", "Ohbe Brass", "LyricFlute", "TronFlutes",
    "PanPeople ", "Bottle Pad", "Wind Ensmb", "SoloBasoon", "Tenor Solo", "ThoseSaxes", "Nautical  ", "FantaFlute",
    "Ooh Choir ", "Ahh Choir ", "Sunsrizer ", "Afterglow ", "TyrellCorp", "MindSweep ", "GenesisWav", "Rainforest",
    "Sahara Sun", "Water!!!  ", "Quadratix ", "VoltagePad", "Xpando Pad", "Scarlamare", "A/V Pad   ", "Air\"LAYER\"",
    "Kalimpanad", "Blacksmith", "Digidee   ", "Marburg   ", "Porta Lead", "ClassicSqr", "Triangular", "Maze Lead ",
    "BPF Lead  ", "Screamer! ", "ShineOn...", "Touchsaw  ", "Fuzz Box  ", "AquaTarkus", "Synergy MW", "Discotron ",
    "Bhangra   ", "Randomania", "Pop Thing ", "Loop-O-Mat", "Clockwork ", "Heartbeat ", "Nanites   ", "MonstrMash",
    "DM5 Drums ", "Straight 8", "Industro  ", "StreetBeat", "Outer Kit ", "AfricaPerc", "Marktree  ", "Orch Hits "
  };

  /** Preset Bank 1 Mix names */
  public static final String[] PRESET_1_BANK_MIX_NAMES =
  {
    "Zen Piano ", "Grandesign", "PianoStak1", "DualRoadz1", "TynoDine  ", "FM DigiPno", "EP&SilkPad", "OctaveHaus",
    "Piano&Wrly", "Pno&ThmpBs", "Fuzzy Clav", "Comp Clav ", "DoublStops", "TripEvibes", "Vibarimba ", "WoodyKalim",
    "SteelVibez", "Steel Panz", "Tron Ting ", "BigFM Tblr", "DistOrgan1", "OrgnBlend1", "PercN Pedl", "Orgn&Bass1",
    "Orgn&Bass2", "Basilica  ", "LitePipes1", "Pipes&Baz1", "AccrdBlnd1", "Harmnicas1", "12-String1", "BigAcoust1",
    "GtrHeaven1", "NylnPeople", "Tremelctro", "Guitr&Stik", "Chug&Lead1", "HeadBangrs", "Zithereens", "Kotograph ",
    "Bass&APno1", "Bass&APno2", "SynBs&Pad1", "Stik&Pad 1", "ABass&Pad1", "Frtls&Pad1", "FatSpunkBs", "Bzzz      ",
    "Beat&Bass1", "EarthWorks", "Pno&Violin", "Sect&Violn", "LightArcos", "LiteIn8ves", "Strgs&Orch", "BowledOver",
    "HiOctSynSt", "Obiblend  ", "PizziLayer", "Harpscape ", "Piano&Horn", "Pad & Horn", "Orch&Trump", "SmallEnsmb",
    "LargeEnsmb", "ChipBrass1", "BrtPopHrns", "Baz&MuteTr", "Bass&Brass", "HrnsInFrnt", "APno&Flute", "AGtr&Flute",
    "Synth&Wind", "Synth&Chif", "ThickChiff", "Ensm&Reed ", "Piano&Tenr", "MtBonz&Sax", "Airy Ensmb", "Windy Orch",
    "Vox Comp  ", "MovingMarb", "DigtlGlory", "TekSplit 1", "PercLd&Pad", "Andigenous", "AnaBazLead", "SwirLd&Pad",
    "Awakening ", "RezBaz&Saw", "Arpejimatr", "Searcher  ", "Roboticon ", "MoodMusic ", "NanoFactor", "Big Ac Kt1",
    "MondoRaver", "It's Alive", "2Contnents", "Bezt Hitz "
  };


  /** Preset Bank 2 Program names */
  public static final String[] PRESET_2_BANK_PROG_NAMES =
  {
    "DarkClascl", "InThePiano", "Player Pno", "PianoStrng", "EP & Strng", "Hard Roads", "Suitcase  ", "DirtyWurly",
    "Soft FM EP", "Toy Grand ", "Quack Clav", "Clavatar  ", "Harpsifunk", "Mad Vibes ", "Woody Xylo", "Potsticker",
    "Watercan  ", "AttakOfIce", "BlkBoxBell", "Tacko Bell", "AmericaOrg", "BluesOrgan", "Purple B  ", "Jazz Prc B",
    "Survival  ", "High Mass ", "SftPipeOrg", "2 Drawbars", "WrmAcrdion", "JazzHrmnca", "LegatoAGtr", "Big Body12",
    "GuitarsOoh", "AcHarmonic", "818 Guitar", "Silvertone", "Chunky    ", "Fuzzhead  ", "CoralLezli", "Spamisen  ",
    "FatUpright", "Face Bass ", "Heavy Bass", "GothamBass", "No Frets! ", "FM Pluxx  ", "Touch Bass", "Buzzz Bass",
    "TranceBass", "Dist Bass ", "Mi Viola  ", "SmlSection", "LushStrngs", "Violin Orc", "OctaString", "Pit String",
    "Tron Mood ", "SE Flange ", "Pitzi     ", "HeavenHarp", "Bone-afied", "Jazz Mute ", "RegalBones", "Ooh Horns ",
    "ClsclHorns", "Gold Brass", "BeBopHorns", "Sfz Brass ", "Orchestral", "ClscSynBrs", "SingleFlut", "SpaceFlute",
    "Hard Pipes", "Tripan    ", "Wind Orch ", "Oboe Blow ", "Brite Alto", "Big Band  ", "Wistelaan ", "Shamanixst",
    "Oohzee    ", "Glory Ahhs", "Dead Sea  ", "Anasthesia", "Sparks    ", "Hold&Sampl", "Dew Drops ", "Outland   ",
    "Emperor   ", "Ascent    ", "Fanfare GX", "PowerChirp", "BladeRunnr", "Distance  ", "Angelsynth", "HighGlissz",
    "Delecea   ", "PatchCords", "Silk&Satin", "FuzzyGlass", "FmDBgining", "EPROM Boy ", "EmoL7 Lead", "DiodeDoodl",
    "MellowGold", "PortaWheel", "Sweet Lead", "Brassy 5th", "SuperNova ", "AbdnsTriad", "Transcape ", "Groovy-bot",
    "Yonderland", "Robotechno", "JungleGruv", "WhereDrums", "Sardauker ", "Circles   ", "T-Minus 1 ", "Creeps    ",
    "Pop Up Kit", "9 Time    ", "HardcorKit", "UrbanBliss", "GuessTrips", "India Perc", "TimpaniHit", "Danz Hitz "
  };

  /** Preset Bank 2 Mix names */
  public static final String[] PRESET_2_BANK_MIX_NAMES =
  {
    "A/V Piano ", "PianoScore", "Piano&Suit", "ElectRodes", "Pop Tines ", "FMulation ", "TineyVoice", "WaterTines",
    "WrmWurlPno", "2Pnos&Stik", "DamgIsDone", "ZippitClav", "Harpsilog ", "IslandVibz", "ManicMrmba", "Yankers   ",
    "Potzdammer", "Panznstrng", "BentBeauty", "BatzBelfry", "DistOrgan2", "OrgnBlend2", "TwoManual2", "Orgn&Bass3",
    "Orgn&Bass4", "Cathedral ", "LitePipes2", "Pipes&Baz2", "AccrdBlnd2", "Harmnicas2", "12-String2", "SteelPsych",
    "GtrHeaven2", "Nylotation", "Too Clean ", "Guitr&Slap", "Chug&Lead2", "Bass&PwrCh", "RaviSyntar", "Kotosphere",
    "Bass&APno3", "Bass&APno4", "SynBs&Pad2", "Stik&Pad 2", "ABass&Pad2", "Frtls&Pad2", "FatFingerd", "Rezo & Sub",
    "Beat Knack", "Byte Beat ", "EPno&ChVla", "Sect&Solo2", "StringLyr1", "StringLyr2", "Strngs&Brs", "Pit & Pizz",
    "LiteSynStr", "Obermello ", "PizziLayr2", "HarpLayer2", "Pno&MuteTr", "Blat & Pad", "Orch&Blat ", "StatelyEns",
    "FrnchBones", "ChipBrass2", "Pop Swells", "Bass&JetBr", "SBaz&CBraz", "HiHornOrch", "Pno&FltChf", "6Str&Flute",
    "Vox&Flute ", "Gray Wind ", "Sharp Pans", "BigHit&Sax", "EPno&ASax ", "Slinky'Boe", "Many Winds", "WindPassag",
    "Ooh Time  ", "Beyond Vox", "Lickety Ld", "Digi Split", "Starfire  ", "Rez Bath  ", "HdAtkSplit", "SmokeSplit",
    "Borealis  ", "GrooveThis", "Arkham2000", "Lead4Life ", "VenusDisco", "Floating  ", "Algorhythm", "So Funky  ",
    "DrumMonkey", "KlockAways", "Percolator", "Mobile Hit"
  };

  /** Preset Bank 3 Program names */
  public static final String[] PRESET_3_BANK_PROG_NAMES =
  {
    "64 Grand  ", "HyperPiano", "HousePiano", "Piano Pad ", "EP & Oohs ", "SuperRoadz", "SoftSuitcs", "TrampWurly",
    "Chrysalis ", "PnoStrVox ", "LiquidClav", "ProfitClav", "8'4'Harpsi", "Rezophone ", "Yanklungs ", "Roundup   ",
    "AlloyGlock", "FairyBellz", "Ice Bell  ", "Waterphone", "3Draw Rock", "KeyClikOrg", "Rockin' B3", "GospelOrgn",
    "MetalOrgan", "Full Ranks", "Communion ", "KiknPedals", "Surf Organ", "Synthonica", "SteelHorse", "TuesdayAft",
    "Dulcioto  ", "ElHarmonic", "PassGuitar", "PedalSteel", "Hyperdrivr", "HeroHarmnx", "Dulcimer  ", "Mando Trem",
    "SharpStick", "Deep Bass ", "Roundwound", "Pop'n Bass", "Octaver   ", "FunkSnapBs", "Funky Acid", "MellowBass",
    "ArndsHouse", "BassHarmnc", "Solo Cello", "Solodious ", "RichString", "Film Score", "HugeString", "Strng&Perc",
    "True Tron ", "StrgMachin", "PizzViolin", "Harp Gliss", "Francaise ", "Orch Mutes", "Tromb Ens ", "3rdImpTrpt",
    "TrumpetEns", "Four Horns", "Dixi Brass", "HornExpans", "GhostHorns", "OB Horns  ", "Hard Flute", "Mutablow  ",
    "PetersPipe", "Minotaur  ", "Dark Winds", "G. Soprano", "Sax Touch ", "Sax Mass  ", "Transformr", "1001Nights",
    "VelOoz&Aaz", "Voxalon   ", "Final Dawn", "1stContact", "Applewine ", "Shiftaling", "Comet Rain", "7th Wave  ",
    "Eno Pad   ", "Tsynami   ", "Touch & Go", "EmersonSaw", "Fluid Pad ", "Vector Pad", "Fuzz Choir", "Hihowareya",
    "Scientific", "Pop Out   ", "Voice Bell", "PebbleBell", "Fast Sync ", "Spork Boy ", "Tri Lead  ", "Beta Lead ",
    "WhstleLead", "Alpha Lead", "Rezzathing", "Trilogy Ld", "Hazy Lead ", "The Sage  ", "Pitch-Bot ", "Disco Boy ",
    "Braveheart", "NineIncher", "TheSandMan", "Consumrism", "Fanfare   ", "Big Sur   ", "BubbleHead", "Hyperspace",
    "CountryKit", "See Our 78", "Gruvy Lube", "Disco Kit ", "UFO Drums ", "Asia Perc ", "Doom Toms ", "Film Hit  "
  };

  /** Preset Bank 3 Mix names */
  public static final String[] PRESET_3_BANK_MIX_NAMES =
  {
    "Octo Rock ", "MajestyPno", "PianoStak3", "LayrRoadz3", "TineO'Mine", "WurlRoadEP", "EP&ThikPad", "NutcrkrPno",
    "Trampled  ", "Roadz&EBaz", "Clav Stack", "Snow Clav ", "BrandnBrgr", "Vibropots ", "Plucktron ", "Pizzi This",
    "OilDroplet", "Panznvox  ", "CleanBelEP", "AlloyBellz", "DistOrgan3", "OrgnBlend3", "TwoManual3", "Orgn&Bass5",
    "Orgn&Bass6", "ToTheGlory", "LitePipes3", "Pipes&Baz3", "Accrd&Baz3", "Hrmca&Baz3", "12-String3", "BigAcoust3",
    "GtrHeaven3", "SloNylnPad", "Velocaster", "Gtr&Fretls", "Chug&Chord", "Rock Split", "Sitaration", "Kotobird  ",
    "Bass&APno5", "Bass&APno6", "SynBs&Pad3", "Stik&Pad 3", "ABass&Pad3", "Frtls&Pad3", "FatSlapBs1", "FatSlapBs2",
    "Beat&Bass3", "BeatAround", "Cello&APno", "Violn&Sect", "StringLyr3", "StringLyr4", "Strgs&Hrns", "SynStr&Piz",
    "LoOctSynSt", "String Bed", "Pizz N Tmp", "HarpBelVox", "Piano&Bone", "BeautyTute", "Orch&Horn ", "Rich Horns",
    "Trump Card", "ChipBrass3", "SlowSwells", "Bass&SawBr", "PrcBaz&Brs", "mf Orch   ", "Harpsi&Flt", "Gtr&Bottle",
    "Flut&Spher", "SynBz&Pipe", "WindyBrite", "Bed&Brkfst", "SoftEP&Sax", "TrumpaSax ", "Windinflyt", "Saxieland!",
    "Cumulus   ", "WingedEyes", "SilverDrop", "SciencSplt", "OohVox&Zip", "Double Emo", "Baz&EdgeLd", "BelPad&Shn",
    "Padulation", "Baz&DigPad", "Padlands  ", "Dramatis  ", "Akbar's   ", "Pop N Pad ", "The Greys ", "Big Ac Kt3",
    "NoizGroove", "Scanner X ", "Purcules  ", "Huge Hit  "
  };

  /** General Midi Bank Program names */
  public static final String[] GM_BANK_PROG_NAMES =
  {
    "AcGrandPno", "BrtAcPiano", "Elec Grand", "Honky-Tonk", "E.Piano 1 ", "E.Piano 2 ", "Harpsichrd", "Clavinet  ",
    "Celesta   ", "Glockenspl", "Music Box ", "Vibraphone", "Marimba   ", "Xylophone ", "TubularBel", "Dulcimer  ",
    "DrawbarOrg", "Perc Organ", "Rock Organ", "Church Org", "Reed Organ", "Accordian ", "Harmonica ", "TangoAccrd",
    "Nylon Gtr ", "SteelStrGt", "JazzGuitar", "Clean Gtr ", "Mute Gtr  ", "OvrdriveGt", "Distortion", "GtHarmonic",
    "AcoustBass", "FingerBass", "Pick Bass ", "FretlessBs", "SlapBass 1", "SlapBass 2", "SynthBass1", "SynthBass2",
    "Violin    ", "Viola     ", "Cello     ", "ContraBass", "TremStrngs", "Pizzicato ", "Harp      ", "Timpani   ",
    "String Ens", "Slow Str  ", "SynString1", "SynString2", "Choir Ahhs", "Voice Oohs", "SynthVoice", "OrcstraHit",
    "Trumpet   ", "Trombone  ", "Tuba      ", "MtdTrumpet", "FrenchHorn", "Brass Sect", "SynBrass 1", "SynBrass 2",
    "SopranoSax", "Alto Sax  ", "Tenor Sax ", "BaritonSax", "Oboe      ", "EnglshHorn", "Bassoon   ", "Clarinet  ",
    "Piccolo   ", "Flute     ", "Recorder  ", "Pan Flute ", "BottleBlow", "Shakuhachi", "Whistle   ", "Ocarina   ",
    "SquareLead", "Saw Lead  ", "Calliope  ", "Chiff Lead", "Charang   ", "Voice Lead", "5ths Lead ", "Bass&Lead ",
    "Bell Pad  ", "Warm Pad  ", "Polysynth ", "GlassChoir", "BowedGlass", "Metallic  ", "Halo Pad  ", "Echo Sweep",
    "Ice Rain  ", "Soundtrack", "Crystaline", "Atmosphere", "Briteness ", "Goblins   ", "Echoes    ", "Sci-Fi    ",
    "Sitar     ", "Banjo     ", "Shamisen  ", "Koto      ", "Kalimba   ", "Bagpipe   ", "Fiddle    ", "Shanai    ",
    "TinkleBell", "Agogo     ", "SteelDrums", "Woodblock ", "Taiko Drum", "MelodicTom", "Synth Drum", "Rev Cymbal",
    "Fret Noise", "BreathNois", "Seashore  ", "Bird Tweet", "Telephone ", "Helicopter", "Applause  ", "Gunshot   "
  };

  /** General Midi Bank Mix names */
  public static final String[] GM_BANK_MIX_NAMES =
  {
    "GM Multi  ", "Piano&JStr", "GrandTines", "Scootr'sEP", "Roads Rule", "CrystalPno", "EP&SynStak", "Burlesque ",
    "MetalSwirl", "Big Split1", "Zip Clav  ", "Clavatron ", "Tralane   ", "Vibe Pad  ", "All 'o Dem", "BrokenXylo",
    "LeakyChymz", "Xpanzer   ", "Chromagnet", "Big Chimes", "Agro-Organ", "OOrrggaann", "TwoFaceOrg", "Organ Slap",
    "Lezly Slap", "SaintsAliv", "Prelude   ", "Holy Split", "NewOrleans", "TravelBlue", "Lifeson 12", "Steel, XL ",
    "GtrSonnets", "SynthNylon", "Mellocastr", "Gtr&SynBaz", "Dist.Bros.", "Stix&Stuff", "Curryean  ", "Kototronic",
    "SynStk&Pno", "FatBs&APno", "RezBs&Pad ", "Stik&Flow ", "ABass&Glaz", "Frtls&Frst", "Fat Stik  ", "TekSomeMor",
    "Psycho&Bas", "BouncerBob", "Piano&Pizz", "Sect&Cello", "Huge Sectn", "Huge8vaSct", "FromThePit", "Replipizzi",
    "SynAuraStr", "Obertronic", "WoodenPizz", "Angel Army", "Piano&Horn", "Horn&Blade", "SaxBrs&Mte", "Bone Sect ",
    "French Rev", "OberBrassX", "PowerBrass", "BrassBlast", "Dist&Trix ", "JazzyBrass", "Age ofWind", "Harm &Flut",
    "Hell Floot", "SynthnWind", "Panosphere", "Wind&Tenor", "Pno&Saxes ", "Straxmute ", "OrchrWinds", "Saxestra  ",
    "Mellow Pad", "Morphings ", "Wiggie's  ", "Synthesite", "Digi-Goo  ", "Wacky Tech", "Bass & BPF", "Shine Thru",
    "Stardeaf  ", "PadPhoriah", "dWelcoming", "Tranquilty", "Sync Power", "Gom Jabbar", "CirceStack", "Big O Kit ",
    "MassiveKit", "Vulcanizer", "Shockra   ", "MassDriver"
  };

  /** User Bank Factory Default Program names */
  public static final String[] USER_BANK_PROG_NAMES =
  {
    "PureStereo", "Rave Knave", "AntiquePno", "Pianooohs ", "LA Studio ", "No Quarter", "Fat Roadz ", "Whirl Lee ",
    "Mars E Pno", "TineString", "Clavislap ", "Digi Clav ", "Ana Harpsi", "Toy Hammer", "Chasers   ", "Sebastian ",
    "Met Talls ", "GlassBells", "MorphBells", "MW RvrsBel", "4Draw Rock", "DrawbarCtl", "Keith's C3", "Ballad B  ",
    "LFO Lezly ", "PhantomOrg", "PostivPuff", "Eng Organ ", "Gypsies   ", "JamHarmnca", "Quiet Time", "FolkBarGtr",
    "GuitarPoem", "SteelNylon", "InstntEdge", "PulpGuitar", "Hard 5ths ", "Feedbacker", "Ethnoba   ", "Kotobaba  ",
    "Dance X   ", "PsychoBass", "Space Bass", "House Bass", "Jazzy Bass", "LatelyBass", "SynAtkBass", "Quack     ",
    "Trick Bass", "DanceGlide", "ContraBass", "DSP Violin", "DiamondStr", "SynthEnsmb", "Syn Arcos ", "StarDustMW",
    "DreamStrgs", "Ana String", "SpacePluck", "Waterfalls", "SmokeyRoom", "MW Mute Tp", "Hard Tuba ", "FusionHorn",
    "Vivaldian ", "BigBrsSect", "ButtahHrns", "TrumpletMW", "SkyWatcher", "Mighty5ths", "ChiffFlute", "TalkinPipe",
    "PanBristle", "BlowDeTune", "Wind Woods", "Croccodile", "BreathySax", "SprnoRcrdr", "HybridBlow", "Cartoonin'",
    "PavlovsDog", "MorphChoir", "Air Pad MW", "GlideVoxMW", "JoshuaTree", "Metal Wash", "HeavenCent", "Ocean Mood",
    "MoonRise  ", "InnerPhase", "TicSawlead", "Spaceport ", "FlashBack ", "VintageRez", "Mink Pad  ", "Chromaphon",
    "Amakudari ", "RubberMetl", "TinSynLead", "Marimpanad", "Zoo Lead  ", "Spring Boy", "3oh3 SawMW", "3oh3 SqrMW",
    "HiPassCtrl", "'74 Square", "RaveSaw QS", "RaveSqr QS", "Buzz Clip ", "SyncNSaws!", "Wormholes ", "Bonk      ",
    "No Age    ", "Funkngruvn", "New Waves ", "Press Roll", "TseTse Fly", "DogsInSpac", "Insectagon", "Laboratory",
    "Real Rock ", "Asylum Kit", "Harlem Tek", "15ips Kit ", "GuessDrums", "VocoderKit", "Rainstick ", "Deja Hitz "
  };

  /** User Bank Factory Default Mix names */
  public static final String[] USER_BANK_MIX_NAMES =
  {
    "Multitmbrl", "Pno&Strngs", "AP & Roadz", "Brie Piano", "Tine Pad  ", "ChromeTine", "Masseeve  ", "WaterWurld",
    "WrliStrngs", "SlinkySplt", "Digiclav! ", "Arco Clav ", "RkMeAmdeus", "Toy Vibers", "Marmbacker", "CollidrBel",
    "Impact    ", "SteelStuff", "Toy Fifths", "MW Rewind ", "ZippyHamnd", "SynOrganic", "Orgasplits", "FunkySplit",
    "DeepFunkSp", "MightyPipe", "PeacePipes", "Pipe&Bass ", "SurferSplt", "Hrmnca&Bas", "12Str Sect", "PickaStrng",
    "Nylooh    ", "WorldOfSix", "Pick On Me", "Pulp Split", "NazT Split", "Axeslinger", "Sitaria   ", "Kotobed   ",
    "Bass&Piano", "FrndlySplt", "Boink Zone", "Stik&Pluk ", "BalladSplt", "Bassilisk ", "BallO'Bass", "Synic Layr",
    "Funky Love", "Waveaux   ", "ClassyDuet", "Sect&Solo ", "TheRealStr", "SessionStr", "WithanOrch", "WithiPizzi",
    "String Tek", "StringSoup", "ZizziPizzi", "Harparound", "Rex Regal ", "Pad&Flugel", "MuchBrazSp", "Tromic Lyr",
    "Jurbrassic", "ForwardBrs", "Stabby Lyr", "Frtls&SBrz", "Bass&SBraz", "RathaLarge", "SynWndSplt", "Guit&Flute",
    "SpaceSplit", "SynthiPans", "Flooty Lyr", "AmiablSplt", "Piano Sack", "Jazz Room ", "SmallReeds", "Orchey Lyr",
    "Air Wheel ", "Wooly Vox ", "Glorilogic", "Doom Split", "ControlrLd", "MW 5th Rez", "DangerSplt", "SilverSplt",
    "LiquidMetl", "Bass&SnPad", "Big Mover ", "WonderLead", "Rezz'nRoll", "Bye Bye!  ", "Etherama  ", "Sloppy Kit",
    "Greasy Kit", "Cacophany ", "Drum Storm", "Dirt Hit  "
  };




  //
  // Voice names for Program editing
  //

  // ------------ Built-in Keyboard-type Voice Groups -------------------------------------------------------------

  /** Keyboard-type Voice Group: Piano */
  public static final String[] VOICE_NAMES_KYBD_PIANO =
  {
    "GrndPianoL", "GrndPianoR", "DarkPno1 L", "DarkPno1 R", "DarkPno2 L", "DarkPno2 R", "DarkPno3 L", "DarkPno3 R",
    "BritePno1L", "BritePno1R", "BritePno2L", "BritePno2R", "BritePno3L", "BritePno3R", "NoHammer L", "NoHammer R",
    "SoftPianoL", "SoftPianoR", "VeloPianoL", "VeloPianoR", "TapPiano L", "TapPiano R", "E Spinet 1", "E Spinet 2",
    "Toy Pno L ", "Toy Pno R ", "KeyTrack1 ", "KeyTrack2 ", "Stretch L ", "Stretch R ", "PianoWaveL", "PianoWaveR",
    "BriteRoads", "Dark Roads", "Soft Roads", "VeloRoads1", "VeloRoads2", "VeloRoads3", "Wurly     ", "VeloWurly1",
    "VeloWurly2", "FM Piano  ", "FM Tines  ", "Soft Tines", "VelAtkTine", "Vel FM Pno", "BrtRdsWave", "DrkRdsWave",
    "SftRdsWave", "Wurly Wave"
  };

  /** Keyboard-type Voice Group: Chromatic */
  public static final String[] VOICE_NAMES_KYBD_CHRMTC =
  {
    "Clavinet  ", "VelAtkClav", "ClavntWave", "Harpsicord", "VAtkHarpsi", "HarpsiWave", "Glock     ", "Xylophone ",
    "Marimba Hd", "Marimba Sf", "MarimbaVel", "Vibraphone", "VibesWave ", "Ice Block ", "Brake Drum", "TubulrWave",
    "TubWv/Null", "FMTblrBell", "FMTublrSft", "FMTublrVel", "FMTub/Null"
  };

  /** Keyboard-type Voice Group: Organ */
  public static final String[] VOICE_NAMES_KYBD_ORGAN =
  {
    "Rock Organ", "Perc Organ", "FullDrwbr1", "FullDrwbr2", "3 Drawbars", "4 Drawbars", "UpprDrwbrs", "16'Drawbar",
    "5 1/3' bar", "8' Drawbar", "4' Drawbar", "2 2/3' bar", "2' Drawbar", "1 3/5' bar", "1 1/3' bar", "1' Drawbar",
    "Percus 2nd", "Percus 3rd", "Percus Wav", "HollowWave", "60's Combo", "RotarySpkr", "ChurchOrgn", "Principale",
    "Positive  "
  };

  /** Keyboard-type Voice Group: Guitar */
  public static final String[] VOICE_NAMES_KYBD_GUITAR =
  {
    "SteelStrng", "NylonGuitr", "Nylon/Harm", "Nylon/Harp", "JazzGuitar", "SingleCoil", "Sngle/Mute", "DoubleCoil",
    "DCoil/Harm", "DCoil/Jazz", "D/S Coil  ", "MicroGuitr", "PwrH/MGtr1", "PwrH/MGtr2", "MuteGuitar", "Mute Velo ",
    "Metal Mute", "MGtr/MtlMt", "MtlMut/Hrm", "Fuzz Wave ", "ClsHarmncs", "ElecHarmnc", "Pwr Harm 1", "Pwr Harm 2",
    "Pwr Harm 3", "PwrHrmVel1", "PwrHrmVel2", "PwrHrmVel3"
  };

  /** Keyboard-type Voice Group: Bass */
  public static final String[] VOICE_NAMES_KYBD_BASS =
  {
    "StudioBass", "Studio&Hrm", "Studio/Hrm", "Slp/Studio", "Slap Bass ", "Slap&Harm ", "Slap/Harm ", "Slap/Pop  ",
    "Pop/Slap  ", "Bass Pop  ", "Pop/Harm  ", "Harm/Pop  ", "JazzFingrd", "Fingr&Harm", "JazzPicked", "Pickd&Harm",
    "Jazz Velo ", "Muted Bass", "Stik Bass ", "Stik&Harm ", "Stik/Harm ", "Harm/Stik ", "Fretless  ", "Frtls&Harm",
    "AcousBass1", "AcoBs1&Hrm", "AcousBass2", "AcoBs2&Hrm", "VelAcoBass", "3-VelBass1", "3-VelBass2", "3-VelBass3",
    "3-VelBass4", "BassHarmnc"
  };

  /** Keyboard-type Voice Group: String */
  public static final String[] VOICE_NAMES_KYBD_STRING =
  {
    "StringEnsm", "TapeStrngs", "SoloString", "SoloViolin", "Solo Viola", "Solo Cello", "Contrabass", "Pizz Sectn",
    "Pizz Split", "Pizz/Strng", "Strng/Pizz", "StringAttk", "Harp      ", "Hi Bow    ", "Low Bow   "
  };

  /** Keyboard-type Voice Group: Brass */
  public static final String[] VOICE_NAMES_KYBD_BRASS =
  {
    "Pop Brass ", "ClasclBras", "AttakBrass", "Trumpet   ", "HarmonMute", "Trombone  ", "FrenchHorn", "Bari Horn ",
    "Tuba      "
  };

  /** Keyboard-type Voice Group: Woodwind */
  public static final String[] VOICE_NAMES_KYBD_WDWIND =
  {
    "Bassoon   ", "Oboe      ", "EnglishHrn", "Clarinet  ", "Bari Sax  ", "BrthyTenor", "Alto Sax  ", "SopranoSax",
    "Velo Sax  ", "Flute     ", "Flute Wave", "Shakuhachi", "PanPipe Hd", "PanPipe Md", "PanPipe Sf", "PanPipeVel",
    "Pan Wave  ", "BottleBlow", "BottleWave"
  };

  /** Keyboard-type Voice Group: Synth */
  public static final String[] VOICE_NAMES_KYBD_SYNTH =
  {
    "J Pad     ", "M Pad     ", "X Pad     ", "Velo Pad 1", "Velo Pad 2", "Velo Pad 3", "AcidSweep1", "AcidSweep2",
    "AcidSweep3", "AcidSweep4", "AcidSweep5", "VeloAcid 1", "VeloAcid 2", "VeloAcid 3", "VeloAcid 4", "Chirp Rez1",
    "Chirp Rez2", "Chirp RezV", "Quack Rez1", "Quack Rez2", "Quack Rez3", "Quack Rez4", "QuackRezV1", "QuackRezV2",
    "QuackRezV3", "Uni Rez 1 ", "Uni Rez 2 ", "Uni Rez 3 ", "Uni Rez V ", "AnalogSqr1", "AnalogSqr2", "AnalogSqrV",
    "SyncLead 1", "SyncLead 2", "SyncLead V", "Seq Bass  ", "Seq BassV1", "Seq BassV2", "FatSynBass", "TranceBas1",
    "TranceBas2", "VeloTrance", "FunkSynBs1", "FunkSynBs2", "FunkSynBs3", "FunkSynBsV", "FilterBass", "FM Bass   ",
    "FM/FiltVel", "Soft Chirp", "Soft Rez  "
  };

  /** Keyboard-type Voice Group: Wave */
  public static final String[] VOICE_NAMES_KYBD_WAVE =
  {
    "Pure Sine ", "10% Pulse ", "20% Pulse ", "50% Pulse ", "Velo Pulse", "Mini Saw  ", "Saw Fltr 1", "Saw Fltr 2",
    "Saw Fltr 3", "Saw Fltr 4", "Saw Fltr 5", "Saw Fltr 6", "Saw Fltr 7", "RezSaw UK ", "RezSaw USA", "Acid Saw  ",
    "Velo Saw 1", "Velo Saw 2", "Velo Saw 3", "Velo Saw 4", "Velo Saw 5", "Velo Saw 6", "AcidRezSqr", "VelAcidWav",
    "MiniSquare", "Sqr Fltr 1", "Sqr Fltr 2", "VeloSquare", "Mini Tri  ", "Tri Filter", "Velo Tri  ", "Rectanglar",
    "Hard Sync ", "HSync/Rect", "BrightSync", "Rez Sync  ", "Ring Mod  ", "RingMod V1", "RingMod V2", "OctaveLock",
    "Diet Saw  ", "Band Saw  ", "Notch Saw ", "HiPassSaw1", "HiPassSaw2", "HiPassSaw3", "HiPassSaw4", "HiPassVel1",
    "HiPassVel2", "HiPassVel3", "HiPassVel4", "HiPassVel5", "HiPassVel6", "Cognitive ", "Additive 1", "Additive 2",
    "VeloAdditv", "Digital 1 ", "Digital 2 ", "Digital 3 ", "Digital 4 ", "Science 1 ", "Science 2 ", "Science 3 ",
    "Science 4 ", "VelScience", "Metal Wave", "Inharmonc1", "Inharmonc2"
  };

  /** Keyboard-type Voice Group: Noise */
  public static final String[] VOICE_NAMES_KYBD_NOISE =
  {
    "WhiteNoise", "Spectral  ", "Crickets  ", "Rain Noise", "FiltrNoise", "ShapeNoise", "VeloNoise1", "VeloNoise2",
    "VeloNoise3", "NoiseLoop1", "NoiseLoop2", "NoiseLoop3", "NoiseLoop4", "NoiseLoop5"
  };

  /** Keyboard-type Voice Group: Voice */
  public static final String[] VOICE_NAMES_KYBD_VOICE =
  {
    "VocalAhhs", "Soft Ahhs  ", "Ahhs Wave ", "VocalOohs ", "Soft Oohs ", "Oohs/Ahhs ", "Ahhs/Oohs ", "Whistle   ",
    "Phonic   "
  };

  /** Keyboard-type Voice Group: Ethnic */
  public static final String[] VOICE_NAMES_KYBD_ETHNIC =
  {
    "Sitar    ", "Sitar Wave ", "Shamisen  ", "Koto      ", "DulcimerHd", "DulcimerMd", "DulcimerSf", "DulcimrVel",
    "DlcmrWave", "MandlnTrem ", "Accordian ", "Harmonica ", "Banjo     ", "Kalimba   ", "Steel Drum", "Tuned Pipe"
  };

  /** Keyboard-type Voice Group: Drums */
  public static final String[] VOICE_NAMES_KYBD_DRUMS =
  {
    "Stndrd Kit", "Rock Kit 1", "Rock Kit 2", "Dance Kit ", "Brush Kit ", "ElctricKit", "Tek Kit   ", "Rap Kit   ",
    "Street Kit", "MetalliKit", "HvyMtliKit", "VeloMtlKit", "Trip Kit 1", "Trip Kit 2", "Trip Kit 3", "Wild Kit  ",
    "Octave Kit", "OrchstraKt", "Raga Kit  ", "FloppyKick", "PillowKick", "MasterKick", "Metal Kick", "Smoke Kick",
    "GrooveKik1", "GrooveKik2", "Sharp Kick", "Tek Kick  ", "AnalogKick", "Rap Kick  ", "FatWoodSnr", "HR Snare  ",
    "Master Snr", "PiccoloSnr", "Electrnic1", "Electrnic2", "Rap Snare1", "Rap Snare2", "Tek Snare ", "Brush Snr ",
    "Crosstick ", "Hi Tom    ", "Mid Tom   ", "Low Tom   ", "Cannon Tom", "Hex Tom   ", "Rap Tom   ", "Closed Hat",
    "HalfOpnHat", "Open Hat  ", "Foot Hat  ", "TekHatClsd", "TekHatOpen", "RapHatClsd", "RapHatOpen", "CricketCHH",
    "CricketTIK", "CricktsOHH", "FltrNoisCH", "FltrNoisOH", "Ride Cym  ", "Ride Bell ", "Crash Cym ", "Null/Crash",
    "Splash Cym", "China Cym ", "Rap Cymbal", "RapCymWave", "StndrdKtDM", "RockKit1DM", "RockKit2DM", "DanceKitDM",
    "BrushKitDM", "ElctrcKtDM", "Tek Kit DM", "Rap Kit DM", "StreetKtDM", "TripKit1DM", "TripKit2DM", "TripKit3DM",
    "OctavKitDM", "OrchstraDM"
  };

  /** Keyboard-type Voice Group: Percussion */
  public static final String[] VOICE_NAMES_KYBD_PERCUS =
  {
    "Agogo     ", "Bongo     ", "Cabasa    ", "Castanet  ", "Chimes 1  ", "Chimes 2  ", "Chimes 3  ", "Clap Rap  ",
    "Clap Tek  ", "Clave 1   ", "Clave 2   ", "Conga Hit1", "Conga Hit2", "CongaSlap1", "CongaSlap2", "Rap Conga ",
    "Rap Rim   ", "Cowbell   ", "RapCowbell", "Cuica     ", "Djembe Hi ", "Djembe Low", "Drumstix  ", "FingerSnap",
    "GuiroLong1", "GuiroLong2", "GuiroShort", "Maracas   ", "SmbaWhstl1", "SmbaWhstl2", "ShortWhstl", "Shaker Hi ",
    "Shaker Low", "Sleighbel1", "Sleighbel2", "Tabla Ga  ", "Tabla Ka  ", "Tabla Ka 2", "Tabla Na  ", "Tabla Te  ",
    "Tabla Te 2", "Tabla Tin ", "Taiko Drum", "Taiko Rim ", "Talk Down ", "Talk Up   ", "Tambourine", "Timbale   ",
    "Timpani   ", "Null/Timp ", "Triangle 1", "Triangle 2", "TrianglSf1", "TrianglSf2", "Udu Hi    ", "Udu Mid   ",
    "Udu Low   ", "Udu Slap  ", "Vibrasmak1", "Vibrasmak2", "Wood Block"
  };

  /** Keyboard-type Voice Group: Sound Effects */
  public static final String[] VOICE_NAMES_KYBD_SND_FX =
  {
    "Rain 1    ", "Rain 2    ", "Bird Tweet", "Bird Loop", "Telephone  ", "Jungle 1  ", "Jungle 2  ", "Jungle 3  ",
    "Jungle 4  ", "GoatsNails", "ScrtchPul1", "ScrtchPul2", "ScrtchPsh1", "ScrtchPsh2", "ScratchLp1", "ScratchLp2",
    "ScrtchPLp1", "ScrtchPLp2", "ScrtchPLp3", "ScrtchPLp4", "Orch Hit  ", "Null/Orch ", "Dance Hit ", "Null/Dance",
    "Rez Zip   ", "RezAttack1", "RezAttack2", "RezAttkVel", "Zap Attk 1", "Zap Attk 2", "Zap Attk 3", "Fret Noise",
    "Sci Loop 1", "Sci Loop 2", "Sci Loop 3", "Bit Field1", "Bit Field2", "Bit Field3", "Bit Field4", "Bit Field5",
    "Bit Field6", "WavLoop1.0", "WavLoop1.1", "WavLoop1.2", "WavLoop1.3", "WavLoop1.4", "WavLoop1.5", "WavLoop1.6",
    "WavLoop1.7", "WavLoop1.8", "WavLoop2.0", "WavLoop2.1", "WavLoop2.2", "WavLoop2.3", "WavLoop2.4", "WavLoop2.5",
    "WavLoop2.6", "WavLoop2.7", "WavLoop2.8", "WavLoop3.0", "WavLoop3.1", "WavLoop3.2", "WavLoop3.3", "WavLoop3.4",
    "WavLoop3.5", "WavLoop4.0", "WavLoop4.1", "WavLoop4.2", "WavLoop4.3", "WavLoop4.4", "WavLoop4.5", "D-Scrape  ",
    "D-ScrapeLp"
  };

  /** Keyboard-type Voice Group: Rhythm */
  public static final String[] VOICE_NAMES_KYBD_RHYTHM =
  {
    "Psi Beat 1", "Psi Beat 2", "Psi Beat 3", "Psi Beat 4", "Psi Beat 5", "Psi Beat 6", "Psi Beat 7", "Psi Beat 8",
    "Psi Beat 9", "Psi Beat10", "Psi Beat11", "Psi Beat12", "Kick Loop1", "Kick Loop2", "Kick Loop3", "Kick Loop4",
    "Kick Loop5", "Kick Loop6", "Kick Loop7", "Kick Loop8", "Kick Loop9", "KickLoop10", "KickLoop11", "Snare Lp 1",
    "Snare Lp 2", "Snare Lp 3", "Snare Lp 4", "Snare Lp 5", "Snare Lp 6", "Snare Lp 7", "Snare Lp 8", "Snare Lp 9",
    "SnareBeat1", "SnareBeat2", "SnareBeat3", "SnareBeat4", "SnareBeat5", "Back Beat1", "Back Beat2", "Back Beat3",
    "Back Beat4", "Hat1 Clsd1", "Hat1 Clsd2", "Hat1 Foot ", "Hat1 Open1", "Hat1 Open2", "Hat2 Clsd1", "Hat2 Clsd2",
    "Hat2 Foot ", "Hat2 Open1", "Hat2 Open2", "Hat3 Clsd1", "Hat3 Clsd2", "Hat3 Open1", "Hat3 Open2", "Hat Beat 1",
    "Hat Beat 2", "Hat Beat 3", "Hat Beat 4", "Hat Beat 5", "Hat Beat 6", "Hat Beat 7", "Hat Beat 8", "Hat Beat 9",
    "Hat Beat10", "Agogo Loop", "Bongo Loop", "CabasaLoop", "CastanetLp", "Conga Loop", "Shaker Lp1", "Shaker Lp2",
    "SleighLoop", "TablaGa Lp", "TablaKa Lp", "TablaNa Lp", "TablaTe Lp", "TablaTinLp", "Taiko Loop", "PercBeat1 ",
    "PercBeat2 ", "PercBeat3 ", "PercBeat4 ", "VoiceLoop1", "VoiceLoop2", "PhonicLoop", "SpinalLoop", "Tri Loop  ",
    "Tri Loop 2", "Orch Loop "
  };


  /** Keyboard-type Voice Group Names */
  public static final String[] VOICE_GROUP_NAMES_KYBD =
  {
    "Piano     ", "Chrmtc    ", "Organ     ", "Guitar    ", "Bass      ", "String    ", "Brass     ", "Wdwind    ",
    "Synth     ", "Wave      ", "Noise     ", "Voice     ", "Ethnic    ", "Drums     ", "Percus    ", "Snd FX    ",
    "Rhythm    "
  };

  /** All Keyboard-type Voice Groups */
  public static final String[][] VOICE_NAMES_KYBD =
  {
    VOICE_NAMES_KYBD_PIANO,
    VOICE_NAMES_KYBD_CHRMTC,
    VOICE_NAMES_KYBD_ORGAN,
    VOICE_NAMES_KYBD_GUITAR,
    VOICE_NAMES_KYBD_BASS,
    VOICE_NAMES_KYBD_STRING,
    VOICE_NAMES_KYBD_BRASS,
    VOICE_NAMES_KYBD_WDWIND,
    VOICE_NAMES_KYBD_SYNTH,
    VOICE_NAMES_KYBD_WAVE,
    VOICE_NAMES_KYBD_NOISE,
    VOICE_NAMES_KYBD_VOICE,
    VOICE_NAMES_KYBD_ETHNIC,
    VOICE_NAMES_KYBD_DRUMS,
    VOICE_NAMES_KYBD_PERCUS,
    VOICE_NAMES_KYBD_SND_FX,
    VOICE_NAMES_KYBD_RHYTHM
  };


  // ------------ Built-in Drum-type Voice Groups -----------------------------------------------------------------

  /** Drum-type Voice Group: Kick */
  public static final String[] VOICE_NAMES_DRUM_KICK =
  {
    "FloppyKik1", "FloppyKik2", "FloppyKikV", "MasterKik1", "MasterKik2", "MasterKikV", "MetalKick1", "MetalKick2",
    "MetalKickV", "GrooveKik1", "GrooveKik2", "GrooveKikV", "Sharp Kick", "Tek Kick 1", "Tek Kick 2", "Tek Kick V",
    "AnalogKik1", "AnalogKik2", "AnalogKik3", "AnalogKikV", "Rap Kick  "
  };

  /** Drum-type Voice Group: Sname */
  public static final String[] VOICE_NAMES_DRUM_SNARE =
  {
    "Fat Wood 1", "Fat Wood 2", "Fat Wood V", "HR Snare 1", "HR Snare 2", "HR Snare V", "MasterSnr1", "MasterSnr2",
    "MasterSnrV", "Piccolo 1 ", "Piccolo 2 ", "Piccolo V ", "Electronc1", "Electronc2", "ElectroncV", "Rap Snare1",
    "Rap Snare2", "Tek Snare1", "Tek Snare2", "Tek SnareV", "Brush Hit1", "Brush Hit2", "Brush HitV", "Crosstick1",
    "Crosstick2", "CrosstickV"
  };

  /** Drum-type Voice Group: Toms */
  public static final String[] VOICE_NAMES_DRUM_TOMS =
  {
    "HiRackTom1", "HiRackTom2", "HiRackTomV", "MdRackTom1", "MdRackTom2", "MdRackTomV", "LoRackTom1", "LoRackTom2",
    "LoRackTomV", "HiFlrTom 1", "HiFlrTom 2", "HiFlrTom V", "MidFlrTom1", "MidFlrTom2", "MidFlrTomV", "LowFlrTom1",
    "LowFlrTom2", "LowFlrTomV", "CanonTomH1", "CanonTomH2", "CanonTomHV", "CanonTomM1", "CanonTomM2", "CanonTomMV",
    "CanonTomL1", "CanonTomL2", "CanonTomLV", "Hex Tom Hi", "Hex Tom Md", "Hex Tom Lo", "RapTomHi  ", "RapTomMid ",
    "RapTomLow "
  };

  /** Drum-type Voice Group: Cymbal */
  public static final String[] VOICE_NAMES_DRUM_CYMBAL =
  {
    "ClosedHat1", "ClosedHat2", "ClosedHatV", "Tight Hat ", "Loose Hat ", "Slosh Hat ", "Foot Hat 1", "Foot Hat 2",
    "Velo Hat 1", "Velo Hat 2", "Velo Hat 3", "TekHatClsd", "TekHatOpen", "RapHatClsd", "RapHatHalf", "RapHatOpen",
    "CricktHat1", "CricktHat2", "FilterHat1", "FilterHat2", "FilterHat3", "Ride Cym 1", "Ride Cym 2", "RideCym V1",
    "RideCym V2", "RideBell 1", "RideBell 2", "RideBell V", "Crash Cym1", "Crash Cym2", "SplashCym1", "SplashCym2",
    "SplashCym3", "China Cym1", "China Cym2", "RapCymbal1", "RapCymbal2", "RapCymWave", "Open Hat 1", "Open Hat 2",
    "Open Hat 3", "Open Hat V", "RideCym V3"
  };

  /** Drum-type Voice Group: Percussion */
  public static final String[] VOICE_NAMES_DRUM_PERCUS =
  {
    "Agogo Hi  ", "Agogo Low ", "Bongo Hi  ", "Bongo Low ", "Brake Drum", "Cabasa    ", "Castanet  ", "Chimes 1  ",
    "Chimes 2  ", "Clap Rap  ", "Clap Tek  ", "Clave     ", "Conga Hi  ", "Conga Low ", "Conga Slap", "RapCongaHi",
    "RapCongaMd", "RapCongaLo", "Rap Rim   ", "Rap Tone  ", "Cowbell   ", "RapCowbell", "Cuica     ", "Djembe Hi ",
    "Djembe Low", "Drumstix  ", "FingerSnap", "Guiro Long", "Guiro Med ", "GuiroShort", "Ice Block ", "Kalimba Hi",
    "KalimbaLow", "Maracas   ", "SambaWhstl", "SambaShort", "Shaker1 Hi", "Shaker1Low", "Shaker2 Hi", "Shaker2Low",
    "Sleighbl 1", "Sleighbl 2", "SteelDrmHi", "SteelDrmLo", "TablaGa Hi", "TablaGaLow", "Tabla Ka  ", "TablaNa Hi",
    "TablaNaLow", "Tabla Te  ", "TablaTinHi", "TablaTinLo", "Taiko Hi  ", "Taiko Low ", "Taiko Rim ", "Talk Up Hi",
    "Talk Up Lo", "TalkDownHi", "TalkDownLo", "Tambourin1", "Tambourin2", "Timbale Hi", "TimbaleLow", "Timpani Hi",
    "TimpaniMid", "TimpaniLow", "Triangle  ", "TriangleSf", "Udu Hi    ", "Udu Mid   ", "Udu Low   ", "Udu Slap  ",
    "Vibrasmack", "WoodBlokHi", "WoodBlokLo"
  };

  /** Drum-type Voice Group: Sound Effects */
  public static final String[] VOICE_NAMES_DRUM_SND_FX =
  {
    "Bird Tweet", "Bird Chirp", "Bird Loop ", "Fret Noise", "Fret Wipe ", "Orch Hit  ", "Dance Hit ", "Jungle 1  ",
    "Jungle 2  ", "Applause  ", "GoatsNails", "Brook     ", "Hi Bow    ", "Low Bow   ", "ShapeNzHi ", "ShapeNzMid",
    "ShapeNzLow", "ScrtchPull", "ScrtchPush", "ScrtchLoop", "ScrtchPlLp", "ScrtcPshLp", "RezAttkHi ", "RezAttkMid",
    "RezAttkLow", "RezZipHi  ", "RezZipMid ", "RezZipLow ", "Zap 1 Hi  ", "Zap 1 Mid ", "Zap 1 Low ", "Zap 2 Hi  ",
    "Zap 2 Mid ", "Zap 2 Low ", "Zap 3 Hi  ", "Zap 3 Mid ", "Zap 3 Low ", "FltrNzLoop", "Romscrape ", "Rain      ",
    "Telephone ", "Sci Loop 1", "Sci Loop 2", "Sci Loop 3", "Bit Field1", "Bit Field2", "Bit Field3", "Bit Field4",
    "Bit Field5", "Bit Field6", "WavLoop1.0", "WavLoop1.1", "WavLoop1.2", "WavLoop1.3", "WavLoop1.4", "WavLoop1.5",
    "WavLoop1.6", "WavLoop1.7", "WavLoop1.8", "WavLoop2.0", "WavLoop2.1", "WavLoop2.2", "WavLoop2.3", "WavLoop2.4",
    "WavLoop2.5", "WavLoop2.6", "WavLoop2.7", "WavLoop2.8", "WavLoop3.0", "WavLoop3.1", "WavLoop3.2", "WavLoop3.3",
    "WavLoop3.4", "WavLoop3.5", "WavLoop4.0", "WavLoop4.1", "WavLoop4.2", "WavLoop4.3", "WavLoop4.4", "WavLoop4.5",
    "D-Scrape  ", "D-ScrapeLp"
  };

  /** Drum-type Voice Group: Wave */
  public static final String[] VOICE_NAMES_DRUM_WAVE =
  {
    "High Sine ", "Mid Sine  ", "Low Sine  ", "HiWhitNoiz", "MidWhtNoiz", "LowWhtNoiz", "HiSpectral", "LoSpectral",
    "HiCrickets", "LoCrickets", "Inharm 1  ", "Inharm 2  ", "High Saw  ", "Low Saw   ", "High Pulse", "Low Pulse ",
    "Hi AcidRez", "LowAcidRez", "Metal Wave", "HiMetlMute", "LoMetlMute", "Hi DistGtr", "LowDistGtr", "Hi PwrHarm",
    "LowPwrHarm", "Hi FunkGtr", "LowFunkGtr", "Hi MuteGtr", "LowMuteGtr", "HiElecHarm", "LoElecHarm", "ClsclHarm ",
    "HiBassHarm", "MidBassHrm", "LowBassHrm", "HiSlpBass ", "LoSlpBass ", "Hi BassPop", "LowBassPop", "Muted Bass",
    "Stik Bass ", "StudioBass", "JazzFingrd", "JazzPicked", "Fretless  ", "AcousBass ", "60's Combo", "Hi Piano  ",
    "Mid Piano ", "Low Piano ", "High Sync ", "Low Sync  ", "Hi Synth  ", "LowSynth  ", "Ahhs High ", "Ahhs Mid  ",
    "Ahhs Low  ", "Oohs High ", "Oohs Mid  ", "Oohs Low  ", "TunePipeHi", "TunePipeMd", "TunePipeLo"

  };

  /** Drum-type Voice Group: Rhthym */
  public static final String[] VOICE_NAMES_DRUM_RHTHYM =
  {
    "Psi Beat 1", "Psi Beat 2", "Psi Beat 3", "Psi Beat 4", "Psi Beat 5", "Psi Beat 6", "Psi Beat 7", "Psi Beat 8",
    "Psi Beat 9", "Psi Beat10", "Psi Beat11", "Psi Beat12", "Kick Loop1", "Kick Loop2", "Kick Loop3", "Kick Loop4",
    "Kick Loop5", "Kick Loop6", "Kick Loop7", "Kick Loop8", "Kick Loop9", "KickLoop10", "KickLoop11", "Snare Lp 1",
    "Snare Lp 2", "Snare Lp 3", "Snare Lp 4", "Snare Lp 5", "Snare Lp 6", "Snare Lp 7", "Snare Lp 8", "Snare Lp 9",
    "SnareBeat1", "SnareBeat2", "SnareBeat3", "SnareBeat4", "SnareBeat5", "Back Beat1", "Back Beat2", "Back Beat3",
    "Back Beat4", "Hat1 Clsd1", "Hat1 Clsd2", "Hat1 Foot ", "Hat1 Open1", "Hat1 Open2", "Hat2 Clsd1", "Hat2 Clsd2",
    "Hat2 Foot ", "Hat2 Open1", "Hat2 Open2", "Hat3 Clsd1", "Hat3 Clsd2", "Hat3 Open1", "Hat3 Open2", "Hat Beat1 ",
    "Hat Beat2 ", "Hat Beat3 ", "Hat Beat4 ", "Hat Beat5 ", "Hat Beat6 ", "Hat Beat 7", "Hat Beat 8", "Hat Beat 9",
    "Hat Beat10", "Agogo Loop", "Bongo Loop", "CabasaLoop", "CastanetLp", "Conga Loop", "Shaker Lp1", "Shaker Lp2",
    "SleighLoop", "TablaGa Lp", "TablaKa Lp", "TablaNa Lp", "TablaTe Lp", "TablaTinLp", "Taiko Loop", "Perc Beat1",
    "Perc Beat2", "Perc Beat3", "Perc Beat4", "VoiceLoop1", "VoiceLoop2", "PhonicLoop", "SpinalLoop", "Tri Loop  ",
    "Tri Loop 2", "Orch Loop "
  };



  /** Keyboard-type Voice Group Names */
  public static final String[] VOICE_GROUP_NAMES_DRUM =
  {
    "Kick      ", "Snare     ", "Toms      ", "Cymbal    ", "Percus    ", "Snd FX    ", "Wave      ", "Rhythm    "
  };

  /** All Keyboard-type Voice Groups */
  public static final String[][] VOICE_NAMES_DRUM =
  {
    VOICE_NAMES_DRUM_KICK,
    VOICE_NAMES_DRUM_SNARE,
    VOICE_NAMES_DRUM_TOMS,
    VOICE_NAMES_DRUM_CYMBAL,
    VOICE_NAMES_DRUM_PERCUS,
    VOICE_NAMES_DRUM_SND_FX,
    VOICE_NAMES_DRUM_WAVE,
    VOICE_NAMES_DRUM_RHTHYM
  };


  // ------------ QCard Group and Voice Names----------------------------------------------------------------------

  //
  // To add support for a new QCard:
  //
  // (1) Add the Voice Groups and Voice Names arrays in a section for
  // that QCard (Copy the Jazz Piano QCard section and modify)
  //
  // (2) Add the new QCard to the Look-up data section for all QCards
  //


  // ------------ Jazz Piano QCard --------------------------------------------------------------------------------

  /** Jazz Piano QCard Keyboard-type Voice Group:  ZA tun */
  public static final String[] VOICE_NAMES_KYBD_Q_CARD_JAZZ_PIANO_ZA_TUN =
  {
    "ZA-L      ", "ZA-R      ", "ZAsoft-L  ", "ZAsoft-R  ", "ZAvelo-L  ", "ZAvelo-R  ", "ZAbrt-L   ", "ZAbrt-R   ",
    "ZAdrk-L   ", "ZAdrk-R   "
  };

  /** Jazz Piano QCard Keyboard-type Voice Group:  ZA no */
  public static final String[] VOICE_NAMES_KYBD_Q_CARD_JAZZ_PIANO_ZA_NO =
  {
    "ZAU-L     ", "ZAU-R     ", "ZAUsoft-L ", "ZAUsoft-R ", "ZAUvelo-L ", "ZAUvelo-R ", "ZAUbrt-L  ", "ZAUbrt-R  ",
    "ZAUdrk-L  ", "ZAUdrk-R  "
  };

  /** Jazz Piano QCard Keyboard-type Voice Group Names */
  public static final String[] VOICE_GROUP_NAMES_KYBD_Q_CARD_JAZZ_PIANO =
  {
    "ZA tun    ", "ZA no     "
  };

  /** All Jazz Piano QCard Keyboard-type Voice Groups */
  public static final String[][] VOICE_NAMES_KYBD_Q_CARD_JAZZ_PIANO =
  {
    VOICE_NAMES_KYBD_Q_CARD_JAZZ_PIANO_ZA_TUN,
    VOICE_NAMES_KYBD_Q_CARD_JAZZ_PIANO_ZA_NO
  };

  /** Jazz Piano QCard Drum-type Voice Group Names */
  public static final String[] VOICE_GROUP_NAMES_DRUM_Q_CARD_JAZZ_PIANO =
  {
    // No drum-type voices on Jazz Piano QCard
  };

  /** All Jazz Piano QCard Drum-type Voice Groups */
  public static final String[][] VOICE_NAMES_DRUM_Q_CARD_JAZZ_PIANO =
  {
    // No drum-type voices on Jazz Piano QCard
  };



  // ------------ Look-up data for all supported QCards -----------------------------------------------------------

  /** Supported QCards: Names: One entry for each QCard */
  public static final String[] SUPPORTED_QCARD_NAMES =
  {
    "Jazz Piano"
  };

  /** Supported QCards: Voice Group Names: [QCard][Keyboard|Drum][Group Name] */
  public static final String[][][] SUPPORTED_QCARD_VOICE_GROUP_NAMES =
  {
    {
      VOICE_GROUP_NAMES_KYBD_Q_CARD_JAZZ_PIANO,
      VOICE_GROUP_NAMES_DRUM_Q_CARD_JAZZ_PIANO,
    }
  };

  /** Supported QCards: Voice Names [QCard][Keyboard|Drum][Group][Voice Name] */
  public static final String[][][][] SUPPORTED_QCARD_VOICE_NAMES =
  {
    {
      VOICE_NAMES_KYBD_Q_CARD_JAZZ_PIANO,
      VOICE_NAMES_DRUM_Q_CARD_JAZZ_PIANO
    }
  };

  /** Complete range of Alesis notes */
  public static final String [] NOTE_NAMES = new String [] {
        "C-2","C#-2","D-2","D#-2","E-2","F-2","F#-2","G-2","G#-2","A-2","A#-2","B-2",
        "C-1","C#-1","D-1","D#-1","E-1","F-1","F#-1","G-1","G#-1","A-1","A#-1","B-1",
        "C0","C#0","D0","D#0","E0","F0","F#0","G0","G#0","A0","A#0","B0",
        "C1","C#1","D1","D#1","E1","F1","F#1","G1","G#1","A1","A#1","B1",
        "C2","C#2","D2","D#2","E2","F2","F#2","G2","G#2","A2","A#2","B2",
        "C3","C#3","D3","D#3","E3","F3","F#3","G3","G#3","A3","A#3","B3",
        "C4","C#4","D4","D#4","E4","F4","F#4","G4","G#4","A4","A#4","B4",
        "C5","C#5","D5","D#5","E5","F5","F#5","G5","G#5","A5","A#5","B5",
        "C6","C#6","D6","D#6","E6","F6","F#6","G6","G#6","A6","A#6","B6",
        "C7","C#7","D7","D#7","E7","F7","F#7","G7","G#7","A7","A#7","B7",
        "C8","C#8","D8","D#8","E8","F8","F#8","G8"};


}
