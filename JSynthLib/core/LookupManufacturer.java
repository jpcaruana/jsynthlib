package core;

/*
 * LookupManufacturer.java
 *
 * Created on 12. Oktober 2000, 22:16
 */
/**
 *
 * @author  Gerrit Gehnen <Gerrit.Gehnen@gmx.de>
 * @version $Id$
 */
public class LookupManufacturer {
    private static String normal[]=
    {"Sequential Circuits", //0x01
     "IDP",
     "Voytera",
     "Moog Music",
     "Passport Design",
     "Lexicon",
     "Kurzweil",
     "Fender",
     "Gulbransen",
     "AKG Acoustics",
     "Voyce Music",
     "Waveframe",
     "ADA",
     "Garfield Electronics",
     "Ensoniq",
     "Oberheim",
     "Apple Computer",
     "Grey Matter",
     "Digidesign",
     "Palm Tree Instruments",
     "J L Cooper",
     "Lowrey",
     "Adams-Smith",
     "E-mu Systems",
     "Harmony Systems",
     "ART",
     "Baldwin",
     "Eventide",
     "Inventronics",
     "Key Concepts",
     "Clarity",         // 0x1F
     "Passac",   // 0x20
     "SIEL",
     "Synthaxe",
     "Stepp",
     "Hohner",
     "Twister",
     "Solton",
     "Jellinghaus",
     "Southworth",
     "PPG",
     "JEN",
     "Solid State Logic",
     "Audio Veritrieb",
     "Hinton Instruments",
     "Soundtracs",
     "Elka",
     "Dynacord",
     "UNASSIGNED!",
     "UNASSIGNED!",
     "Clavia Digital Instruments",
     "Audio Architecture",
     "UNASSIGNED!",
     "UNASSIGNED!",
     "UNASSIGNED!",
     "UNASSIGNED!",
     "Soundcraft Electronics",
     "UNASSIGNED!",
     "Wersi",
     "Avab Electronik",
     "Digigram",
     "Waldorf Electronics",
     "Quasimidi", // 0x3F
     "Kawai", // 0x40
     "Roland",
     "Korg",
     "Yamaha",
     "Casio",
     "Moridaira",
     "Kamiya",
     "Akai",
     "Japan Victor",
     "Meisosha",
     "Hoshino Gakki",
     "Fujitsu Electric",
     "Sony",
     "Nishin Onpa",
     "TEAC",
     "UNASSIGNED!",
     "Matsushita Electric", //0x50
     "Fostex",
     "Zoom",
     "Midori Electronics",
     "Matsushita Communication Industrial",
     "Suzuki Musical Instrument Mfg.",
     "Fuji Sound Corporation Ltd.",
     "Acoustic Technical Laboratory,Inc"
    };

    private static String extended00[]=    {
        "Warner New Media", //0x01
        "UNASSIGNED!",
        "UNASSIGNED!",
        "UNASSIGNED!",
        "UNASSIGNED!",
        "UNASSIGNED!",
        "Digital Music Corporation", //0x07
        "IOTA Systems",
        "New England Digital",
        "Artisyn",
        "IVL Technologies",
        "Southern Music Systems",
        "Lake Butler Sound Company",
        "Alesis",
        "UNASSIGNED!",
        "DOD Electronics",
        "Studer-Editech",
        "UNASSIGNED!",
        "UNASSIGNED!",
        "Perfect Fretworks",    // 0x14
        "KAT",
        "Opcode",
        "Rane Corporation",
        "Spatial Sound/Anadi Inc",
        "KMX",
        "Allen & Heath Brenell",
        "Peavey Electronics",
        "360 Systems",
        "Spectrum Design & Development",
        "Marquis Musi",
        "Zeta Systems",
        "Axxes",        // 0x20
        "Orban",
        "UNASSIGNED!",
        "UNASSIGNED!",
        "KTI",          // 0x24
        "Breakaway Technologies",
        "CAE",
        "UNASSIGNED!",
        "UNASSIGNED!",
        "Rocktron Corp.",   //0x29
        "PianoDisc",
        "Cannon Research Corporation",
        "UNASSIGNED!",
        "Rogers Instrument Corp.",
        "Blue Sky Logic",
        "Encore Electronics",
        "Uptown",
        "Voce",
        "CTI Audio",
        "S&S Research",
        "Broderbund Software",
        "Allen Organ Co.",
        "UNASSIGNED!",
        "Music Quest",  // 0x37
        "Aphex",
        "Gallien Krueger",
        "IBM",
        "UNASSIGNED!",
        "Hotz Instruments Technologies",
        "ETA Lighting",
        "NSI Corporation",
        "Ad Lib",
        "Richmond Sound Design",
        "Microsoft",
        "The Software Toolworks",
        "RJMG/Niche",
        "Intone",
        "UNASSIGNED!",
        "UNASSIGNED!",
        "GT Electronics/Groove Tubes",  //0x47
        "UNASSIGNED!",
        "UNASSIGNED!",
        "UNASSIGNED!",
        "UNASSIGNED!",
        "UNASSIGNED!",
        "UNASSIGNED!",
        "Euphonix", //0x4e
        "InterMIDI",
        "UNASSIGNED!",
        "UNASSIGNED!",
        "UNASSIGNED!",
        "UNASSIGNED!",
        "UNASSIGNED!",
        "Lone Wolf",    //0x55
        "UNASSIGNED!",
        "UNASSIGNED!",
        "UNASSIGNED!",
        "UNASSIGNED!",
        "UNASSIGNED!",
        "UNASSIGNED!",
        "UNASSIGNED!",
        "UNASSIGNED!",
        "UNASSIGNED!",
        "UNASSIGNED!",
        "UNASSIGNED!",
        "UNASSIGNED!",
        "UNASSIGNED!",
        "UNASSIGNED!",
        "Musonix",      // 0x64
        "UNASSIGNED!",
        "UNASSIGNED!",
        "UNASSIGNED!",
        "UNASSIGNED!",
        "UNASSIGNED!",
        "UNASSIGNED!",
        "UNASSIGNED!",
        "UNASSIGNED!",
        "UNASSIGNED!",
        "UNASSIGNED!",
        "UNASSIGNED!",
        "UNASSIGNED!",
        "UNASSIGNED!",
        "UNASSIGNED!",
        "UNASSIGNED!",
        "Ta Horng Musical Inst. ",  // 0x74
        "eTek (formerly Forte) ",
        "Electrovoice",
        "Midisoft",
        "Q-Sound Labs",
        "Westrex",
        "NVidia",
        "ESS Technology",
        "MediaTrix Peripherals",
        "Brooktree",
        "Otari",
        "Key Electronics"   //0x7F
    };

    private static String extended10[]=  {
        "Crystalake Multimedia",    //0x01
        "Crystal Semiconductor",
        "Rockwell Semiconductor",
        "Silicon Graphics",
        "Midiman",
        "PreSonus",
        "UNASSIGNED!",
        "Topaz Enterprises",
        "Cast Lighting",
        "Microsoft Consumer Division",
        "UNASSIGNED!",
        "Fast Forward Designs",     //0x0C
        "Headspace (Igor's Labs)",
        "Van Koevering Company ",
        "Altech Systems ",
        "S & S Research",
        "VLSI Technology ",
        "Chromatic Research",
        "Sapphire ",
        "IDRC ",
        "Justonic Tuning ",
        "TorComp Researc, Inc ",
        "Newtek Inc ",
        "Sound Sculpture ",
        "Walker Technical ",
        "PAVO ",
        "InVision Interactive ",
        "T-Square Design ",
        "Nemesys Music Technology",
        "DBX Professional",
        "Syndyne Corporation",
        "Bitheadz",     // 0x20
        "Cakewalk Music Software",
        "Staccato Systems",
        "National Semiconductor",
        "Boom Theory / Adinolfi Alternative Percussion",
        "Virtual DSP Corporation",
        "Antares Systems",
        "Angel Software",
        "St Louis Music",
        "Lyrrus dba G-VOX" //0x29
    };

    private static String extended20[]= {
        "Dream",    // 0x00 !!!!
        "Strand Lighting",
        "AMEK Systems & Controls",
        "UNASSIGNED!",
        "Dr.Böhm/Musician International",
        "UNASSIGNED!",
        "Trident",
        "Real World Design",
        "UNASSIGNED!",
        "Yes Technology",
        "Audiomatica",
        "Bontempi/Farfisa",
        "F.B.T. Electronica",
        "MIDITEMP",
        "Larking Audio",
        "Zero 88 Lighting",
        "Micon Audio Electronics",  // 0x10
        "Forefront Technology",
        "UNASSIGNED!",
        "Kenton Electronics",
        "UNASSIGNED!",
        "ADB",
        "Jim Marshall Products",
        "DDA",
        "BSS Audio",
        "UNASSIGNED!",
        "UNASSIGNED!",
        "UNASSIGNED!",
        "UNASSIGNED!",
        "UNASSIGNED!",
        "UNASSIGNED!",
        "TC Electronic",    // 0x1F
        "UNASSIGNED!",
        "UNASSIGNED!",
        "UNASSIGNED!",
        "UNASSIGNED!",
        "UNASSIGNED!",
        "UNASSIGNED!",
        "UNASSIGNED!",
        "UNASSIGNED!",
        "UNASSIGNED!",
        "UNASSIGNED!",
        "UNASSIGNED!",
        "Medeli Electronics Co",    // 0x2B
        "Charlie Lab SRL",
        "Blue Chip Music Technology",
        "BEE OH",
        "LG Semiconductor",
        "TESI",
        "Emagic",
        "Behringer",
        "Access",
        "Synoptic",
        "Hanmesoft",
        "Terratec Electronic",
        "Proel SpA",
        "IBK MIDI"      // 0x38
    };

    /** Creates new LookupManufacturer */
    private LookupManufacturer() {
    }

    /**
     * Returns Manufacturer name.
     */
    public static String get(byte number1,byte number2, byte number3) {
        if ((number1>normal.length)||(number1<0))
            return "Invalid Manufacturer";
        if (number1>0)
            return normal[number1-1];

        if (number2==0) {
            if (number3<=0)
                return "Invalid Manufacturer";
            return extended00[number3-1];
        }

        if (number2==0x10) {
            if ((number3<=0)||(number3>extended10.length))
                return "Invalid Manufacturer";
            return extended10[number3-1];
        }

        if (number2==0x20) {
            if ((number3<=0)||(number3>=extended20.length))
                return "Invalid Manufacturer";
            return extended20[number3];
        }

        return "Invalid Manufacturer";
    }
}
