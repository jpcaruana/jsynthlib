/*
 * Copyright 2004 Sander Brandenburg
 *
 * This file is part of JSynthLib.
 *
 * JSynthLib is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or(at your option) any later version.
 *
 * JSynthLib is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JSynthLib; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 */
package org.jsynthlib.drivers.roland.jv80;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.jsynthlib.core.Device;
import org.jsynthlib.core.DriverUtil;


/**
 * Copied the wavegroup data names from the jvtool project at sourceforge.net
 *
 * @author Sander Brandenburg
 * @version $Id$
 */
public class RolandJV80WaveBank {
    // TODO is there a map possible to maintain order of addition?
    protected final static Map bankMap = new TreeMap(); 

    public final static int BANKTYPE_INTERNAL = 0;
    public final static int BANKTYPE_EXPANSION = 1;
    public final static int BANKTYPE_PCM = 2;

    public final static RolandJV80WaveBank INTERNAL = new RolandJV80WaveBank("Internal",
            BANKTYPE_INTERNAL, new String[] { "001: Ac. Piano 1",
                    "002: SA Rhodes 1", "003: SA Rhodes 2", "004: E. Piano 1",
                    "005: E. Piano 2", "006: Clav 1", "007: Organ 1",
                    "008: Jazz Organ", "009: Pipe Organ", "010: Nylon GTR",
                    "011: 6STR GTR", "012: GTR HARM", "013: Mute GTR 1",
                    "014: Pop Strat", "015: Stratus", "016: SYN GTR",
                    "017: Harp 1", "018: SYN Bass", "019: Pick Bass",
                    "020: E. Bass", "021: Fretless 1", "022: Upright BS",
                    "023: Slap Bass 1", "024: Slap & Pop", "025: Slap Bass 2",
                    "026: Slap Bass 3", "027: Flute 1", "028: Trumpet 1",
                    "029: Trombone 1", "030: Harmon Mute1", "031: Alto Sax 1",
                    "032: Tenor Sax 1", "033: French 1", "034: Blow Pipe",
                    "035: Bottle", "036: Trumpet SECT", "037: ST.Strings-R",
                    "038: ST.Strings-L", "039: Mono Strings", "040: Pizz",
                    "041: SYN VOX 1", "042: SYN VOX 2", "043: Male Ooh",
                    "044: ORG VOX", "045: VOX Noise", "046: Soft Pad",
                    "047: JP Strings", "048: Pop Voice", "049: Fine Wine",
                    "050: Fantasynth", "051: Fanta Bell", "052: ORG Bell",
                    "053: Agogo", "054: Bottle Hit", "055: Vibes",
                    "056: Marimba Wave", "057: Log Drum", "058: DIGI Bell 1",
                    "059: DIGI Chime", "060: Steel Drums", "061: MMM VOX",
                    "062: Spark VOX", "063: Wave Scan", "064: Wire String",
                    "065: Lead Wave", "066: Synth Saw 1", "067: Synth Saw 2",
                    "068: Synth Saw 3", "069: Synth Square",
                    "070: Synth Pulse1", "071: Synth Pulse2", "072: Triangle",
                    "073: Sine", "074: ORG Click", "075: White Noise",
                    "076: Wind Agogo", "077: Metal Wind", "078: Feedbackwave",
                    "079: Anklungs", "080: Wind Chimes", "081: Rattles",
                    "082: Tin Wave", "083: Spectrum 1", "084: 808 SNR 1",
                    "085: 90's Snare", "086: Piccolo SN", "087: LA Snare",
                    "088: Whack Snare", "089: Rim Shot", "090: Bright Kick",
                    "091: Verb Kick", "092: Round Kick", "093: 808 Kick",
                    "094: Closed HAT 1", "095: Close HAT 2", "096: Open HAT 1",
                    "097: Crash 1", "098: Ride 1", "099: Ride Bell 1",
                    "100: Power Tom Hi", "101: Power Tom Lo",
                    "102: Cross Stick1", "103: 808 Claps", "104: Cowbell 1",
                    "105: Tambourine", "106: Timbale", "107: CGA Mute Hi",
                    "108: CGA Mute Lo", "109: CGA Slap", "110: Conga Hi",
                    "111: Conga Lo", "112: Maracas", "113: Cabasa Cut",
                    "114: Cabasa Up", "115: Cabasa Down", "116: REV Steel DR",
                    "117: REV Tin Wave", "118: REV SN 1", "119: REV SN 2",
                    "120: REV SN 3", "121: REV SN 4", "122: REV Kick 1",
                    "123: REV Cup", "124: REV Tom", "125: REV Cow Bell",
                    "126: REV TAMB", "127: REV Conga", "128: REV Maracas",
                    "129: REV Crash 1" });

    public final static RolandJV80WaveBank EXP_NONE = new RolandJV80WaveBank("Exp: None", BANKTYPE_EXPANSION, new String[] {});
    
    public final static RolandJV80WaveBank EXP_PIANO = new RolandJV80WaveBank("Exp: Piano",
            BANKTYPE_EXPANSION, new String[] { "001: Grand sft 1A",
                    "002: Grand sft 1B", "003: Grand sft 1C",
                    "004: Grand hrd 1A", "005: Grand hrd 1B",
                    "006: Grand hrd 1C", "007: EuroPiano pA",
                    "008: EuroPiano pB", "009: EuroPiano pC",
                    "010: EuroPiano fA", "011: EuroPiano fB",
                    "012: EuroPiano fC", "013: Pop Piano 1",
                    "014: Pop Piano 1L", "015: Pop Piano 1R",
                    "016: SApiano p 1A", "017: SApiano p 1B",
                    "018: SApiano p 1C", "019: SApiano f 1A",
                    "020: SApiano f 1B", "021: SApiano f 1C", "022: SApiano 3",
                    "023: E.Grand BdyA", "024: E.Grand BdyB",
                    "025: E.Grand BdyC", "026: E.Grand 1A", "027: E.Grand 1B",
                    "028: E.Grand 1C", "029: E.Grand 2", "030: Rhodes 1 p",
                    "031: Rhodes 1 f", "032: Rhodes 2A", "033: Rhodes 2B",
                    "034: Rhodes 2C", "035: Rhodes 3A", "036: Rhodes 3B",
                    "037: Rhodes 3C", "038: Rhodes 4A p", "039: Rhodes 4B p",
                    "040: Rhodes 4C p", "041: Rhodes 4A f", "042: Rhodes 4B f",
                    "043: Rhodes 4C f", "044: Rhodes 5A p", "045: Rhodes 5B p",
                    "046: Rhodes 5C p", "047: Rhodes 5A f", "048: Rhodes 5B f",
                    "049: Rhodes 5C f", "050: Rhodes 6A p", "051: Rhodes 6B p",
                    "052: Rhodes 6C p", "053: Rhodes 6A f", "054: Rhodes 6B f",
                    "055: Rhodes 6C f", "056: Wurly soft A",
                    "057: Wurly soft B", "058: Wurly soft C",
                    "059: Wurly hard A", "060: Wurly hard B",
                    "061: Wurly hard C", "062: E.Piano A", "063: E.Piano B",
                    "064: E.Piano C", "065: Clav 2A", "066: Clav 2B",
                    "067: Clav 2C", "068: Clav 3A", "069: Clav 3B",
                    "070: Clav 3C", "071: Clav 4A", "072: Clav 4B",
                    "073: Clav 4C" });

    public final static RolandJV80WaveBank EXP_POP = new RolandJV80WaveBank("Exp: Pop",
            BANKTYPE_EXPANSION, new String[] { "001: Grand sft 1A",
                    "002: Grand sft 1B", "003: Grand sft 1C",
                    "004: Grand hrd 1A", "005: Grand hrd 1B",
                    "006: Grand hrd 1C", "007: E. Grand 2",
                    "008: Wurly soft A", "009: Wurly soft B",
                    "010: Wurly soft C", "011: Wurly hard A",
                    "012: Wurly hard B", "013: Wurly hard C",
                    "014: E. Piano 3A", "015: E. Piano 3B", "016: E. Piano 3C",
                    "017: Clav 2A", "018: Clav 2B", "019: Clav 2C",
                    "020: Clav 3A", "021: Clav 3B", "022: Clav 3C",
                    "023: Clav 4A", "024: Clav 4B", "025: Clav 4C",
                    "026: Jazz Organ 2", "027: Ballad Organ", "028: Even Bars",
                    "029: 8ft Stop", "030: Mad Organ A", "031: Mad Organ B",
                    "032: Mad Organ C", "033: 60's Organ 1",
                    "034: 60's Organ 2", "035: 60's Organ 3",
                    "036: 60's Organ 4", "037: Celeste", "038: Music Box",
                    "039: Jazz Bass 1", "040: Jazz Bass 2", "041: P Bass 1",
                    "042: P. Bass 2", "043: Stick", "044: Fretless 2A",
                    "045: Fretless 2B", "046: Fretless 2C", "047: Ac. Bass A",
                    "048: Ac. Bass B", "049: Ac. Bass C", "050: Bs Harmonix",
                    "051: Bs Slide", "052: Organ Bass 1", "053: Organ Bass 2",
                    "054: Jazz Guitar", "055: Mute GTR 2", "056: Funky Attack",
                    "057: Lead GTR 1A", "058: Lead GTR 1B", "059: Lead GTR 1C",
                    "060: Mute Dist", "061: Overdrive 1A", "062: Overdrive 1B",
                    "063: Overdrive 1C", "064: Power Chords",
                    "065: Harmo Lead", "066: E. Sitar", "067: Banjo",
                    "068: Pedal Steel", "069: Shamisen A", "070: Shamisen B",
                    "071: Shamisen C", "072: Koto Attack", "073: Sanza soft",
                    "074: Sanza hard", "075: Pad Hit", "076: Santur A",
                    "077: Santur B", "078: Santur C", "079: Glockenspiel",
                    "080: Xylophone", "081: Tubular Bell", "082: Jazz Flute A",
                    "083: Jazz Flute B", "084: Jazz Flute C",
                    "085: Sing Flute", "086: Trumbet 2A", "087: Trumpet 2B",
                    "088: Trumpet 2C", "089: Flugel A", "090: Flugel B",
                    "091: Flugel C", "092: Tuba 1", "093: Clarinet 1",
                    "094: Oboe 1", "095: Bassoon 1", "096: Bassoon&Oboe",
                    "097: F.HornSect 1", "098: Soprano Sax", "099: Alto mp A",
                    "100: Alto mp B", "101: Alto mp C", "102: TenorBreathy",
                    "103: Tenor Sax mf", "104: Baritone Sax",
                    "105: Multi Sax 1", "106: Multi Sax 2", "107: Shakuhachi",
                    "108: Harmonica 1", "109: Whistle 1", "110: Tb Sect A",
                    "111: Tb Sect B", "112: Tb Sect C", "113: T.Sax Sect A",
                    "114: T.Sax Sect B", "115: T.Sax Sect C", "116: Violin A",
                    "117: Violin B", "118: Violin C", "119: Cello A",
                    "120: Cello B", "121: Cello C", "122: Violin&Cello",
                    "123: STR Attack A", "124: STR Attack B",
                    "125: Str Attack C", "126: Choir 1A", "127: Choir 1B",
                    "128: Choir 1C", "129: Huge MIDI", "130: Bell VOX",
                    "131: Synharmon", "132: PWM", "133: Rip Lead",
                    "134: Bright Lead", "135: GR300 Saw", "136: Vocal Wave",
                    "137: Sync Wave", "138: Sync Sweep", "139: Cello Wave",
                    "140: Wally Wave", "141: Vox Noise 2", "142: Bottle Lp A",
                    "143: Bottle Lp B", "144: Bottle Lp C", "145: Breath Wind",
                    "146: Breath Atack", "147: Breath Whisl",
                    "148: Breath Hrmon", "149: Metal Rain", "150: Pink Noise",
                    "151: OrchestraHit", "152: Kong", "153: Timpani",
                    "154: Gong", "155: Hard Kick", "156: 909 Kick",
                    "157: 909 Snare", "158: Swing Snare", "159: Dance Snare",
                    "160: Room Snare", "161: Hard Snare", "162: Brush Slap",
                    "163: Brush Swish", "164: Brush Roll", "165: Syn Cl. Hat",
                    "166: Syn Open Hat", "167: Tom Hi", "168: Tom Mid",
                    "169: Tom Lo", "170: Ride 2", "171: China Cymbal",
                    "172: Cuico Hi", "173: Cuico Lo", "174: 808 Cowbell",
                    "175: Claves", "176: Wood Block", "177: Finger Snap",
                    "178: Scratch 1", "179: Scratch 2", "180: Tabla 1",
                    "181: Tabla 2", "182: Tabla 3", "183: Tabla 4",
                    "184: Tabla 5", "185: REV Kick 2", "186: REV Kick 3",
                    "187: REV SN 5", "188: REV SN 6", "189: REV SN 7",
                    "190: REV SN 8", "191: REV SN 9", "192: REV Syn Hat1",
                    "193: REV Syn Hat2", "194: REV China", "195: REV Tom 2",
                    "196: REV Tom 3", "197: REV Cuka Hi", "198: REV Cuka Lo",
                    "199: REV 808 Cow", "200: REV Claves", "201: REV W.Block",
                    "202: Rev F. Snap", "203: REV Scratch1",
                    "204: REV Scratch2", "205: REV Tabla 1",
                    "206: REV Tabla 2", "207: REV Tabla 3", "208: REV Tabla 4",
                    "209: REV Tabla 5", "210: REV Gong", "211: REV Ride",
                    "212: REV O. Hit", "213: REV Kong", "214: REV Timpani",
                    "215: Jazz Set", "216: House Set", "217: Cymbal Set",
                    "218: Perc Set", "219: Snare Set", "220: Tomtom" });

    public final static RolandJV80WaveBank EXP_VINTAGE = new RolandJV80WaveBank("Exp: Vintage",
            BANKTYPE_EXPANSION, new String[] { "001: JP-8 Saw A",
                    "002: JP-8 Saw C", "003: Sys700 Saw", "004: JX-10 Saw",
                    "005: D-50 Saw 1", "006: D-50 Saw 2", "007: SH-5 Saw",
                    "008: SH-2 Saw", "009: SH-101 Saw", "010: SH-1000 Saw",
                    "011: GR-300 Saw 1", "012: GR-300 Saw 2", "013: JU-2 Saw",
                    "014: MG Saw 1A", "015: MG Saw 1C", "016: MG Saw 2",
                    "017: OB Saw 1A", "018: OB Saw 1C", "019: OB Saw 2",
                    "020: P5 Saw 1A", "021: P5 Saw 1C", "022: 2600 Saw",
                    "023: AP Saw", "024: OSC Saw", "025: OSC Reso Saw",
                    "026: KG700 Saw", "027: KG800 Saw 1", "028: KG800 Saw 2",
                    "029: KG MS Saw", "030: CS Saw 1A", "031: CS Saw 1C",
                    "032: CS Saw 2", "033: JP-8 SquareA", "034: JP-8 SquareC",
                    "035: JX-10 Square", "036: SH-5 Square",
                    "037: SH-2 Square", "038: MG Square A", "039: MG Square C",
                    "040: OB Square A", "041: OB Square C", "042: P5 Square A",
                    "043: P5 Square C", "044: 2600 Square", "045: OSC Square",
                    "046: KG800 Square", "047: KG MS Square", "048: CS Square",
                    "049: JP-8 Pulse 1", "050: JP-8 Pulse 2",
                    "051: JP-8 Pulse 3", "052: JP-8 Pulse 4",
                    "053: JP-8 Pulse 5", "054: SH-1000 Puls",
                    "055: MG Pulse 1A", "056: MG Pulse 1C", "057: MG Pulse 2A",
                    "058: MG Pulse 2C", "059: OB Pulse 1", "060: OB Pulse 2",
                    "061: OB Pulse 3", "062: 2600 Pulse 1",
                    "063: 2600 Pulse 2", "064: EM Pulse", "065: CS Pulse 1",
                    "066: CS Pulse 2", "067: JU-2 Sub OSC", "068: MG Ramp",
                    "069: MG Triangle", "070: 2600Triangle", "071: 2600 Sine",
                    "072: JP-8 PWM A", "073: JP-8 PWM B", "074: JP-8 PWM C",
                    "075: MG Dt.Saw A", "076: MG Dt.Saw B", "077: MG Dt.Saw C",
                    "078: P5 Dt.Saw A", "079: P5 Dt.Saw B", "080: P5 Dt.Saw C",
                    "081: MG Dt.Squ A", "082: MG Dt.Squ B", "083: MG Dt.Squ C",
                    "084: JP-8 Str A", "085: JP-8 Str B", "086: JP-8 Str C",
                    "087: OB Str 1A", "088: OB Str 1B", "089: OB Str 1C",
                    "090: OB Str 2A", "091: OB Str 2B", "092: OB Str 2C",
                    "093: AP Str Ens A", "094: AP Str Ens B",
                    "095: AP Str Ens C", "096: OBXP Str A", "097: OBXP Str B",
                    "098: OBXP Str C", "099: OBXP Str Lp", "100: MG Oct A",
                    "101: MG Oct B", "102: MG Oct C", "103: MG Dt.Oct A",
                    "104: MG Dt.Oct B", "105: MG Dt.Oct C",
                    "106: OBXP Brass A", "107: OBXP Brass B",
                    "108: OBXP Brass C", "109: OBXP BrassLp", "110: FM Brass",
                    "111: Waspy", "112: Waspy Lp", "113: OB Lead",
                    "114: OB Lead Lp", "115: JP-6 SqLead", "116: JP-6 SqLd Lp",
                    "117: Blown 1", "118: Blown 2", "119: PG Sweep 1A",
                    "120: PG Sweep 1C", "121: PG Sweep 2A", "122: PG Sweep 2C",
                    "123: D-50 HeavenA", "124: D-50 HeavenB",
                    "125: D-50 HeavenC", "126: JX-8P Vox", "127: JX-8P Vox Lp",
                    "128: VP-330ChoirA", "129: VP-330ChoirB",
                    "130: VP-330ChoirC", "131: P5 Unisync",
                    "132: P5 UnisyncLp", "133: P5 Dipthong",
                    "134: P5 DipthngLp", "135: FM Lead", "136: KG800 Lead",
                    "137: MG Lead", "138: MG Lead Lp", "139: JP-8 Lead",
                    "140: Digwave 1", "141: Digwave 2", "142: Digwave 3",
                    "143: Frog wave", "144: SRG FM", "145: Shimmer wave",
                    "146: VS Organ A", "147: VS Organ C", "148: Juno Organ",
                    "149: Juno OrganLp", "150: FM Punch", "151: Mondigital",
                    "152: MondigitalLp", "153: JP-8 Clavi A",
                    "154: JP-8 Clavi C", "155: JP-8 ClaviLp",
                    "156: Juno Clavi", "157: P5 X-mod", "158: Steam Drum",
                    "159: Kalimba Atk", "160: Additive", "161: MG Blip",
                    "162: MG Blip Lp", "163: MG Thump", "164: MG Thump Lp",
                    "165: MG Attack", "166: MG Attack Lp", "167: VS Bell 1",
                    "168: VS Bell 2", "169: JP-6 Bell", "170: MKS-80 Xmod1",
                    "171: MKS-80 Xmod2", "172: MKS-80 Xmod3",
                    "173: MKS-80 Xmod4", "174: MKS-80 Xmod5", "175: OB Bass",
                    "176: OB Bass Lp A", "177: OB Bass Lp B",
                    "178: OB Bass Lp C", "179: MG BsPedal",
                    "180: MG BsBdl LpA", "181: MG BsBdl LpB", "182: MG Fat Bs",
                    "183: MG Sharp Bs1", "184: MG Big Bs", "185: MG ClassicBs",
                    "186: MG Sharp Bs2", "187: TB-303 Bass",
                    "188: JP-4 Bass 1", "189: JP-4 Bass 2", "190: SH-101 Bs 1",
                    "191: SH-101 Bs 2", "192: SH-101 Bs 3", "193: SH-101 Bs 4",
                    "194: SH-101 Bs 5", "195: Sys700 Bs 1", "196: Sys700 Bs 2",
                    "197: FM Super Bs", "198: KG Poly Bs", "199: KG Poly BsLp",
                    "200: Power B slwA", "201: Power B slwB",
                    "202: Power B slwC", "203: Power B fstA",
                    "204: Power B fstB", "205: Power B fstC",
                    "206: Tron Choir A", "207: Tron Choir B",
                    "208: Tron Choir C", "209: Tron Flute A",
                    "210: Tron Flute B", "211: Tron Flute C",
                    "212: Tron Str A", "213: Tron Str B", "214: Tron Str C",
                    "215: MG White Nz", "216: MG Pink Nz", "217: SH-5 Pink Nz",
                    "218: JP-8 X-mod 1", "219: JP-8 X-mod 2",
                    "220: P5 Noise 1", "221: P5 Noise 2", "222: ZZZ loop",
                    "223: Atmosphere", "224: FX1A-L(RSS)", "225: FX1B-L(RSS)",
                    "226: FX1C-L(RSS)", "227: FX1A-R(RSS)", "228: FX1B-R(RSS)",
                    "229: FX1C-R(RSS)", "230: FX2A-L(RSS)", "231: FX2B-L(RSS)",
                    "232: FX2C-L(RSS)", "233: FX2A-R(RSS)", "234: FX2B-R(RSS)",
                    "235: FX2C-R(RSS)", "236: FX3A-L(RSS)", "237: FX3B-L(RSS)",
                    "238: FX3C-L(RSS)", "239: FX3A-R(RSS)", "240: FX3B-R(RSS)",
                    "241: FX3C-R(RSS)", "242: REV Waspy", "243: REV P5 X-mod",
                    "244: REV SteamDrm", "245: REV Kalimba",
                    "246: REV Additive", "247: REV Blip", "248: REV Thump",
                    "249: REV Attack", "250: REV FX1L RSS",
                    "251: REV FX1R RSS", "252: REV FX2L RSS",
                    "253: REV FX2R RSS", "254: REV FX3L RSS",
                    "255: REV FX3R RSS" });

    public final static RolandJV80WaveBank EXP_UNKNOWN = new RolandJV80WaveBank("EXP: Unknown", BANKTYPE_EXPANSION, DriverUtil.generateNumbers(1, 256, "000: Unknown"));
    public final static RolandJV80WaveBank EXP_WORLD = new RolandJV80WaveBank("Exp: World",
            BANKTYPE_EXPANSION, new String[] { "001: Sitar A", "002: Sitar B",
                    "003: Sitar C", "004: Sitar Gliss", "005: Tambura A",
                    "006: Tambura B", "007: Tambura C", "008: TamburaDrone",
                    "009: Zither A", "010: Zither B", "011: Zither C",
                    "012: HmrDulcimer", "013: Yuehchin", "014: Yangchin",
                    "015: Bandolim", "016: Cavaquinho", "017: Oud A",
                    "018: Oud B", "019: Oud C", "020: Kanoun", "021: Koto",
                    "022: Shamisen 2", "023: Shami Attack", "024: Kayakeum",
                    "025: Oct Harp", "026: Afro Harp", "027: Biwa MENU",
                    "028: Biwa 1", "029: Biwa 2", "030: Biwa 3",
                    "031: Esraj 1", "032: Esraj 2", "033: Kemanche",
                    "034: Erhu", "035: Zampona 1", "036: Zampo Attack",
                    "037: Zampo Trem A", "038: Zampo Trem B",
                    "039: Zampo Trem C", "040: Sicu Pipe", "041: Quena",
                    "042: Ocarina", "043: Kawala A", "044: Kawala B",
                    "045: Kawala C", "046: Shakuhachi2", "047: Shaku Attack",
                    "048: Shaku Ornam", "049: Hunt Pipe", "050: Hunt Noise",
                    "051: Bagpipes 1", "052: Bagpipes 2", "053: Bagpipes 3",
                    "054: Bagpipes 4", "055: Hichiriki", "056: Hichiriki Lp",
                    "057: Shahnai", "058: Mizmar", "059: Mizmar Lp",
                    "060: Piri", "061: Piri Lp", "062: Steel Dr 2",
                    "063: Bonang", "064: Gender", "065: Saron",
                    "066: Blossom Bell", "067: Spokes", "068: Satellite Dr",
                    "069: Finger Cym", "070: Ramacymbal", "071: Atarigane",
                    "072: AsiaGng MENU", "073: Asian Gong 1",
                    "074: Asian Gong 2", "075: Asian Gong 3",
                    "076: Asian Gong 4", "077: Asian Gong 5",
                    "078: Asian Gong 6", "079: Asian Gong 7",
                    "080: REV Gong 5", "081: REV Gong 7", "082: AsiaCym MENU",
                    "083: Chenchen Ptn", "084: Chenchen Opn",
                    "085: Chenchen Cls", "086: BaliCym Opn",
                    "087: BaliCym Cls", "088: Sagat Open", "089: Sagat Close",
                    "090: Sarna Bell", "091: Kalimba 1", "092: Kalimba 2",
                    "093: Kalimba 3", "094: Kalimba Glis", "095: Kalim Gls Lp",
                    "096: Balaphone 1", "097: Balaphone 2", "098: Log Drum 2",
                    "099: Hyoshigi", "100: Clapstick", "101: Slit Drum",
                    "102: Boomerang", "103: Ban Gu 1", "104: Ban Gu 2",
                    "105: Ban Gu 3", "106: TablaBY MENU", "107: TablaBayaSld",
                    "108: TablaBayaGin", "109: TablaBaya Ge",
                    "110: TablaBaya Ka", "111: TablaBaya Na",
                    "112: TablaBayaTin", "113: TablaBayaTun",
                    "114: TablaBaya Te", "115: TablaBaya Ti",
                    "116: Udu Pot MENU", "117: Udu Potl Lo",
                    "118: Udu Potl Hi", "119: Udu Potl Sip",
                    "120: Udu Potl Acc", "121: Udu Pot2 Lng",
                    "122: Udu Pot2 Sht", "123: Udu Pot2 Mut",
                    "124: Dholak MENU", "125: Dholak Ga", "126: Dholak Ta",
                    "127: Dholak Tun", "128: Dholak Na", "129: Madal MENU",
                    "130: Madal Da", "131: Madal Din", "132: Madal Ta",
                    "133: TalkingDr Up", "134: TalkingDr Dn",
                    "135: AfroDrm MENU", "136: AfroDrum Op1",
                    "137: AfroDrum Op2", "138: AfroDrum Flm",
                    "139: AfroDrum Rat", "140: Tablah MENU",
                    "141: Tablah Bend", "142: Tablah Dom", "143: Tablah Tak",
                    "144: Tablah Rim", "145: Tablah Roll", "146: Doira Dun",
                    "147: Doira Tik", "148: Doholla MENU", "149: Doholla Dom",
                    "150: Doholla Sak", "151: Doholla Tak",
                    "152: Doholla Roll", "153: Doholla Stop", "154: Rek MENU",
                    "155: Rek Dom", "156: Rek Tek", "157: Rek Open",
                    "158: Rek Trill", "159: Bendir 1", "160: Bendir 2",
                    "161: Dawul", "162: JapanPrcMENU", "163: Taiko",
                    "164: Sime Taiko", "165: Tsuzumi Lo", "166: Tsuzumi Hi",
                    "167: Ohkawa", "168: ChinaPrcMENU", "169: Gu Roll",
                    "170: Gu Hi", "171: Rot Drum", "172: BerimbauMENU",
                    "173: Berimbau Opn", "174: Berimbau Up",
                    "175: Berimbau Dn", "176: Berimbau Mut", "177: Angklung 2",
                    "178: Afro Zither", "179: JawHarp MENU",
                    "180: Jaw Harp Opn", "181: Jaw Harp Wow",
                    "182: Afro Feet 1", "183: Afro Feet 2", "184: Afro Clap",
                    "185: Rainstick", "186: Didge MENU", "187: Didgeridoo 1",
                    "188: Didgeridoo 2", "189: Didgeridoo 3",
                    "190: Voice MENU", "191: Yoh Tribe", "192: How Tribe",
                    "193: Hey Brazil", "194: Yyoo Dude", "195: ZaghrutaLoop",
                    "196: ZaghrutaStop", "197: Bull Scream",
                    "198: Conch Shell1", "199: Conch Shell2",
                    "200: Samba MENU", "201: SambaBateria",
                    "202: PandeiroMENU", "203: PandeiroL Lo",
                    "204: PandeiroL Hi", "205: PandeiroL Sp",
                    "206: PandeiroL Rm", "207: PandeiroS Op",
                    "208: PandeiroS Sp", "209: PandeiroS Rm",
                    "210: TamborimMENU", "211: Tamborim Opn",
                    "212: Tamborim Mut", "213: Tamborim Slp",
                    "214: Surdo MENU", "215: Surdo Open L",
                    "216: Surdo Open H", "217: Surdo Mute", "218: Surdo Rim",
                    "219: Caixa MENU", "220: Caixa Open1", "221: Caixa 0",
                    "222: Caixa Roll", "223: Caixa Mute", "224: Agogo MENU",
                    "225: Agogo 2 Lo", "226: Agogo 2 Hi", "227: Agogo 3 Lo",
                    "228: Agogo 3 Hi", "229: Cowbell 1", "230: Cowbell 2",
                    "231: Cowbell 3", "232: Cuica 2", "233: Cuica 3",
                    "234: Shaker MENU", "235: Shaker Ptn", "236: Shaker 1",
                    "237: Shaker 2", "238: Chekere 1", "239: Chekere 2",
                    "240: SambaWhistle", "241: Guiro Long", "242: Guiro Short",
                    "243: Timbale MENU", "244: Timbale Lo", "245: Timbale Hi",
                    "246: Timbale Side", "247: Bongo MENU", "248: Bongo 1 Lo",
                    "249: Bongo 1 Hi", "250: Bongo 2 Lo", "251: Bongo 2 Hi",
                    "252: Korean Ens", "253: Morocco Ens", "254: African Ens",
                    "255: World Tour" });

    public final static RolandJV80WaveBank EXP_ORCHESTRAL = new RolandJV80WaveBank(
            "Exp: Orchestral", BANKTYPE_EXPANSION, new String[] { "001: Vl Sect A",
                    "002: Vl Sect B", "003: Vl Sect C", "004: Va Sect A",
                    "005: Va Sect B", "006: Va Sect C", "007: Vc Sect A",
                    "008: Vc Sect B", "009: Vc Sect C", "010: Cb Sect",
                    "011: Multi STR A", "012: Multi STR B", "013: Multi STR C",
                    "014: Vl Sect Lp", "015: Va Sect Lp", "016: Vc Sect Lp",
                    "017: Cb Sect Lp", "018: Multi Str Lp", "019: Vl Solo A",
                    "020: Vl Solo B", "021: Vl Solo C", "022: Va Solo A",
                    "023: Va Solo B", "024: Va Solo C", "025: Vc Solo A",
                    "026: Vc Solo B", "027: Vc Solo C", "028: Cb Solo",
                    "029: Multi Solo 1", "030: Multi Solo 2",
                    "031: Vls Spicc A", "032: Vls Spicc B", "033: Vls Spicc C",
                    "034: Vas Spicc A", "035: Vas Spicc B", "036: Vas Spicc C",
                    "037: Vcs Spicc A", "038: Vcs Spicc B", "039: Vcs Spicc C",
                    "040: Cbs Spicc A", "041: Cbs Spicc B", "042: Cbs Spicc C",
                    "043: Multi Spicc", "044: VlSolo Spicc",
                    "045: VcSolo Spicc", "046: MultSl Spicc",
                    "047: Str Attacko", "048: Pizzicato 1", "049: Pizzicato 2",
                    "050: Piccolo", "051: Oboe 1A", "052: Oboe 1B",
                    "053: Oboe 1C", "054: Oboe 2A", "055: Oboe 2B",
                    "056: Oboe 2C", "057: Eng.Horn A", "058: Eng.Horn B",
                    "059: Eng.Horn C", "060: Clarinet", "061: Bs Clarinet",
                    "062: Multi Cla", "063: Bassoon", "064: Multi Reed",
                    "065: Tnr.Recorder", "066: F.Horn Solo",
                    "067: F.Horn Sect1", "068: F.Horn Sect2",
                    "069: F.Horn Mute", "070: Trumpet 2", "071: Flugelhorn",
                    "072: Cornet", "073: HarmonMute2A", "074: HarmonMute2B",
                    "075: HarmonMute2C", "076: Solo Tb A", "077: Solo Tb B",
                    "078: Solo Tb C", "079: Bass Tb", "080: Tb Sect",
                    "081: Tuba", "082: BRS Ensemble", "083: Brass ff",
                    "084: Full Orch.", "085: Orch Hit Maj",
                    "086: Orch Hit Min", "087: Orch Hit Dim", "088: Choir A",
                    "089: Choir B", "090: Choir C", "091: F.Hrn Sc1 Lp",
                    "092: F.Hrn Sc2 Lp", "093: F.Hrn MuteLp",
                    "094: Tb Sect Lp", "095: BRS Ens Lp", "096: ff Brass Lp",
                    "097: Full Orch Lp", "098: Breath Wind",
                    "099: Breath Atack", "100: Breath Whisl",
                    "101: Breath Hrmon", "102: EuroPiano pA",
                    "103: EuroPiano pB", "104: EuroPiano pC",
                    "105: EuroPiano fA", "106: EuroPiano fB",
                    "107: EuroPiano fC", "108: Harpsichord", "109: Celesta A",
                    "110: Celesta B", "111: Celesta C", "112: Harp A",
                    "113: Harp B", "114: Harp C", "115: Glockenspiel",
                    "116: Xylophone", "117: Bass Marimba", "118: TubularBells",
                    "119: Church Bells", "120: Timpani p", "121: Timpani f",
                    "122: Timp Roll p", "123: Timp Roll f",
                    "124: Concert SNR1", "125: Concert SNR2",
                    "126: Concert SNR3", "127: SNR Roll", "128: Concert BD 1",
                    "129: Concert BD 2", "130: Concert BD 3", "131: BD Roll",
                    "132: Crash Cymbal", "133: Crash Cym Lp",
                    "134: Cymbal Hit", "135: Tam Tam", "136: Gong",
                    "137: Perc Hit 1", "138: Perc Hit 2", "139: Triangle",
                    "140: Castanets 1", "141: Castanets 2", "142: Slapstick",
                    "143: Ratchet", "144: Sleigh Bell ", "145: Tambourine",
                    "146: Wind Chime 2", "147: REV Hit Maj",
                    "148: REV Hit Min", "149: REV Hit Dim", "150: REV Bell",
                    "151: REV Timp 1", "152: REV Timp 2", "153: REV SNR 1",
                    "154: REV SNR 2", "155: REV SNR 3", "156: REV BD 1",
                    "157: REV BD 2", "158: REV BD 3", "159: REV BD Roll",
                    "160: REV Crash", "161: REV Cym Hit", "162: REV Tam Tam",
                    "163: REV Gong", "164: REV PercHit1", "165: REV PercHit2",
                    "166: REV Casta 1", "167: REV Casta2", "168: REV S.Stick",
                    "169: REV Sleigh", "170: REV Tamb", "171: SNR Set",
                    "172: Perc Set 1", "173: Perc Set 2", "174: Perc Set 3" });

    public final static RolandJV80WaveBank PCM_NONE = new RolandJV80WaveBank("PCM: None", BANKTYPE_PCM, new String[] {});
    public final static RolandJV80WaveBank PCM_UNKNOWN = new RolandJV80WaveBank("PCM: Unknown", BANKTYPE_PCM, DriverUtil.generateNumbers(1, 256, "000: Unknown"));
    public final static RolandJV80WaveBank PCM_01 = new RolandJV80WaveBank("PCM: SO_PCM1_01",
            BANKTYPE_PCM, new String[] { "001: U Ac. Piano P",
                    "002: U Ac. Piano F", "003: U E. Grand P",
                    "004: U E. Grand F", "005: Rhodes", "006: Rhodes D",
                    "007: Rhodes B", "008: Bright EP", "009: Bright EP D",
                    "010: Bright EP B" });

    public final static RolandJV80WaveBank PCM_02 = new RolandJV80WaveBank("PCM: SO_PCM1_02",
            BANKTYPE_PCM, new String[] { "001: Jazz Gt P", "002: Jazz Gt PD",
                    "003: Jazz Gt PB", "004: Jazz Gt F", "005: Jazz Gt FD",
                    "006: Jazz Gt FB", "007: Funk Gt", "008: Funk Gt Mute",
                    "009: Over Drv P", "010: Over Drv PD", "011: Over Drv PB",
                    "012: Over Drv F", "013: Over Drv FD", "014: Over Drv FB",
                    "015: Dist Gt P", "016: Dist Gt PD", "017: Dist Gt PB",
                    "018: Dist Gt F", "019: Dist Gt FD", "020: Dist Gt FB",
                    "021: Sax P", "022: Sax PD", "023: Sax PB", "024: Sax M",
                    "025: Sax MD", "026: Sax MB", "027: Trombopet",
                    "028: Trombopet D", "029: Trombopet B", "030: Trombone P",
                    "031: Trombone PD", "032: Trombone PB", "033: Trombone M",
                    "034: Trombone MD", "035: Trombone MB", "036: Trombone F",
                    "037: Trombone Fd", "038: Trombone FB" });

    public final static RolandJV80WaveBank PCM_03 = new RolandJV80WaveBank("PCM: SO_PCM1_03",
            BANKTYPE_PCM, new String[] { "001: Mondo Kick", "002: Deep Kick 1",
                    "003: Solid Kick", "004: Ambo Kick", "005: Reverb Kick 1",
                    "006: Deep Kick 2", "007: Reverb Kick 2",
                    "008: Room Stick", "009: Bigshot SN", "010: Crack Snare",
                    "011: Atomic Snare", "012: Power Snare",
                    "013: Trash Snare", "014: Hard Snare", "015: Combo Snare",
                    "016: Induced SN", "017: Tiny Snare", "018: Rock Snare 1",
                    "019: Rock Snare 2", "020: Reverb Snare",
                    "021: Rock Tom 1", "022: Rock Tom 2", "023: Rock Tom 3",
                    "024: Rock Tom 4", "025: Ambo Tom 1", "026: Ambo Tom 2",
                    "027: Ambo Tom 3", "028: Ambo Tom 4", "029: Room Hat 1",
                    "030: Room Hat 2", "031: Room Hat 3", "032: Room Hat 4",
                    "033: Open HH", "034: Crash Cym", "035: Ride Cym",
                    "036: Ride Bell", "037: China Cym", "038: Cowbell 2",
                    "039: Tambourine", "040: Gong", "041: REV Kick 1",
                    "042: REV Kick 2 ", "043: REV Kick 3", "044: REV Kick 4",
                    "045: REV Kick 5", "046: REV Kick 6", "047: REV Kick 7",
                    "048: REV Rim shot", "049: REV Snare 1",
                    "050: REV Snare 2", "051: REV Snare 3", "052: REV Snare 4",
                    "053: REV Snare 5", "054: REV Snare 6", "055: REV Snare 7",
                    "056: REV Snare 8", "057: REV Snare 9",
                    "058: REV Snare 10", "059: REV Snare 11",
                    "060: REV Snare 12", "061: REV Tom 1", "062: REV Tom 2",
                    "063: REV Tom 3", "064: REV Tom 4", "065: REV Tom 5",
                    "066: REV Tom 6", "067: REV Tom 7", "068: REV Tom 8",
                    "069: REV Hat 1", "070: REV Hat 2", "071: REV Hat 3",
                    "072: REV Hat 4", "073: REV Hat 5", "074: REV Crash",
                    "075: REV Ride", "076: REV R.Bell", "077: REV China",
                    "078: REV Cow Bell", "079: REV Tamb", "080: REV Gong",
                    "081: Hat & Tom 1", "082: Hat & Tom 2", "083: Cymbal",
                    "084: Kick & SN 1", "085: Kick & SN 2", "086: Kick & SN 3",
                    "087: Kick & SN 4", "088: Kick & SN 5", "089: Kick & SN 6",
                    "090: Kick & SN 7" });

    public final static RolandJV80WaveBank PCM_04 = new RolandJV80WaveBank("PCM: SO_PCM1_04",
            BANKTYPE_PCM, new String[] { "001: AcP 1LoP A", "002: Acp 1 LoP B",
                    "003: Acp 1 LoP c", "004: AcP 1 P A", "005: AcP 1 P B",
                    "006: AcP 1 P C", "007: AcP 1 LoF A", "008: AcP 1 LoF B",
                    "009: AcP 1 LoF C", "010: AcP 1 F A", "011: AcP 1 F B",
                    "012: AcP 1 F C", "013: AcP 1 UpA", "014: AcP 1 Upb",
                    "015: AcP 1 UpC", "016: AcP 1 Up TH", "017: AcP 1 Thump" });

    public final static RolandJV80WaveBank PCM_06 = new RolandJV80WaveBank("PCM: SO_PCM1_06",
            BANKTYPE_PCM, new String[] { "001: HPS_Front A", "002: HPS_Front B",
                    "003: HPS_Front C", "004: HPS_Back A", "005: HPS_Back B",
                    "006: HPS_Back C", "007: HPS_Click A", "008: HPS_Click B",
                    "009: HPS_Click C", "010: HPS_Lute A", "011: HPS_Lute B",
                    "012: HPS_Lute C", "013: ORG_Prncpl A",
                    "014: ORG_Prncpl B", "015: ORG_Prncpl C",
                    "016: ORG_Flute A", "017: ORG_Flute B", "018: ORG_Flute C",
                    "019: S_Recorder A", "020: S_Recorder B",
                    "021: S_Recorder C", "022: T_Recorder A",
                    "023: T_Recorder B", "024: T_Recorder C" });

    public final static RolandJV80WaveBank PCM_08 = new RolandJV80WaveBank("PCM: SO_PCM1_08",
            BANKTYPE_PCM, new String[] { "001: Ac.Guitar A", "002: Ac.Guitar B",
                    "003: Ac.Guitar C", "004: 335Pick A", "005: 335Pick B",
                    "006: 335Pick C", "007: 335Mute A", "008: 335Mute B",
                    "009: 335Mute C", "010: CleanEG A", "011: CleanEG B",
                    "012: CleanEG C", "013: Reso.GTR P A", "014: Reso.GTR P B",
                    "015: Reso.GTR P C", "016: Reso.GTR F A",
                    "017: Reso.GTR F B", "018: Reso.GTR F C",
                    "019: F.Mandolin A", "020: F.Mandolin B",
                    "021: F.Mandolin C", "022: Dulcimer A", "023: Dulcimer B",
                    "024: Dulcimer C", "025: Banjo A", "026: Banjo B",
                    "027: Banjo C", "028: Pd.Steel 1A", "029: Pd.Steel 1B",
                    "030: Pd.Steel 1C", "031: Pd.Steel 2A", "032: Pd.Steel 2B",
                    "033: Pd.Steel 2C", "034: FretNoise 1", "035: FretNoise 2",
                    "036: Fingerd Bs A", "037: Fingerd Bs B",
                    "038: Fingerd Bs C", "039: Ac.Bass A", "040: Ac.Bass B",
                    "041: Ac.Bass C", "042: Fiddle A", "043: Fiddle B",
                    "044: Fiddle C", "045: Harmonica A", "046: Harmonica B",
                    "047: Harmonica C", "048: Fat BD", "049: Pillow BD",
                    "050: Snappy SN", "051: Cross SN", "052: Crisp SN",
                    "053: Brash SN", "054: SharpTom Lo", "055: SharpTom Hi",
                    "056: Closed Hat1", "057: Closed Hat2", "058: Open Hihat",
                    "059: Pedal Hihat", "060: Ride Cymbal", "061: Claps Real",
                    "062: REV Fat BD", "063: REV PillowBD",
                    "064: REV SnappySN", "065: REV Cross SN",
                    "066: REV Crisp SN", "067: REV Brash SN",
                    "068: REV Tom Lo", "069: REV Tom Hi", "070: REV ClHat1",
                    "071: REV ClHat2", "072: REV Ophat", "073: REV Pdhat",
                    "074: REV RideCYM", "075: REV Claps", "076: Kick & Snare",
                    "077: Tom Set", "078: HiHat Set", "079: Ride & Clap" });

    private final String name;

    private final int bankType;

    private final String[] names;

    RolandJV80WaveBank(String name, int bank, String[] names) {
        this.name = name;
        this.bankType = bank;
        this.names = names;

        if (bankMap.put(name, this) != null) {
            throw new RuntimeException("double identifier!");
        }
    }

    public String getName() {
        return name;
    }

    public int getBankType() {
        return bankType;
    }

    public String[] getWaves() {
        return names;
    }
    
    public static RolandJV80WaveBank getWaveBank(Device dev, int bank) {
        String pref = null;
        if (bank == BANKTYPE_EXPANSION)
            pref = RolandJV80Device.PREF_EXPANSION;
        else if (bank == BANKTYPE_PCM) 
            pref = RolandJV80Device.PREF_PCM;
        else
            throw new IllegalArgumentException("banknumber is invalid");
            
        return getWaveBank(RolandJV80Device.getPref(dev, pref));
    }

    public static RolandJV80WaveBank getWaveBank(String name) {
        return (RolandJV80WaveBank) bankMap.get(name);
    }

    // returns the possible bank names installed for a bank type
    public static String[] getBanks(int bankType) {
        List list = new ArrayList();
        Iterator i = bankMap.values().iterator();
        while (i.hasNext()) {
            RolandJV80WaveBank wb = (RolandJV80WaveBank) i.next();
            if (wb.getBankType() == bankType)
                list.add(wb.getName());
        }
        return (String[]) list.toArray(new String[list.size()]);
    }
    // returns the current banks installed on the device, according to the device preferences
    public static RolandJV80WaveBank[] getAvailableBanks(Device dev) {
        RolandJV80WaveBank wbExp = (RolandJV80WaveBank) bankMap.get(RolandJV80Device.getPref(dev, RolandJV80Device.PREF_EXPANSION));
        RolandJV80WaveBank wbPcm = (RolandJV80WaveBank) bankMap.get(RolandJV80Device.getPref(dev, RolandJV80Device.PREF_PCM));
        return new RolandJV80WaveBank[] { INTERNAL, wbExp, wbPcm };
    }
   
    public String toString() {
        return name;
    }
}
