/*
 * Copyright 2003 Hiroo Hayashi
 *
 * This file is part of JSynthLib.
 *
 * JSynthLib is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
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

package synthdrivers.RolandTD6;
import core.TreeWidget;

/**
 * Instrument.java
 *
 * Instrument Tree for Roland TD6 Percussion Module.
 *
 * Created: Sun Jun 15 10:49:50 2003
 *
 * @author <a href="mailto:hiroo.hayashi@computer.org">Hiroo Hayashi</a>
 * @version $Id$
 * @see TreeNodes
 * @see TD6SingleEditor
 */
final class Instrument implements TreeWidget.Nodes {
    private static final Object[] ROOT = {
	"Instrument",
	new Object[] {
	    "Kick",
	    "Double Head", "Sharp", "Acous", "Meat",
	    "R8 Low", "R8 Dry", "WdBeatr", "Open",
	    "Vintage", "26\" Deep", "Thick Head", "Round",
	    "Medium", "Big Room", "Big", "Big Low",
	    "Studio 1", "Studio 2", "Studio 3", "Studio 4",
	    "Studio 5", "Studio 6", "Studio 7", "Studio 8",
	    "Buzz 1", "Buzz 2", "Buzz 3", "Buzz 4", "Buzz 5",
	    "Room 1", "Room 2", "Room 3", "Room 4", "Room 5", "Room 6", "Room 7",
	    "Amb 1", "Amb 2", "Amb 3", "Amb 4",
	    "Solid 1", "Solid 2", "Solid 3",
	    "Jazz 1", "Jazz 2", "18\" Jazz", "Brush Hit",
	    "Wood 1", "Wood 2", "Wood 3", "Wood 4",
	    "Maple 1", "Maple 2", "Oak", "Birch", "Rose Wood", "One Ply", "Oyster",
	    "Dry", "Dry Medium", "Dry Hard", "Deep Dry",
	    "Fusion", "Sand Bag", "BsktBal", "Mondo",
	    "MdVrb1", "MdVrb2", "Sizzle", "Box",
	    "Ninja", "Dance", "House", "Pillow", "Rap",
	    "TR808", "808 Hard", "808 Boom", "808 Noize",
	    "TR909", "909 Wood", "909 HdAt",
	    "Elephant", "Cattle", "Door", "Punch",
	    "Machine", "Broken", "Bend Up", "Hard Noize",
	    "R8 Solid", "Thin Head", "Tight",
	    "Chunk", "Gate", "Giant", "Inside",
	    "Std1 1", "Std1 2", "Std2 1", "Std2 2",
	    "Room 8", "Room 9", "Power 1", "Power 2",
	    "Jazz 3", "Jazz 4", "Brush",
	    "Elec 1", "Elec 2", "ElBend", "Plastk 1", "Plastk 2",
	    "Gabba", "Gabba 2", "Tail", "Jungle", "Hip Hop",
	    "LoFi 1", "LoFi 2", "LoFi 3", "LoFi 4",
	    "Noisy", "Splat", "Scrach 1", "Scrach 2",
	    "Hi-Q", "Space", "Synth Bass" },
	new Object[] {
	    "Snare",
	    // 130 (129)
	    "Custom", "Custom Rim", "Custom Br", "Custom Br Rim", "Custom St", "Custom St Rim",
	    // 136
	    "Piccolo", "Piccolo Rim", "Piccolo Br", "Piccolo Br Rim", "Piccolo St", "Piccolo St Rim",
	    "Piccolo 2", "Piccolo 2 Rim", "Piccolo 2 Br", "Piccolo 2 Br Rim", "Piccolo 2 St", "Piccolo 2 St Rim",
	    "Piccolo 3", "Piccolo 3 Rim", "Piccolo 3 Br", "Piccolo 3 Br Rim", "Piccolo 3 St", "Piccolo 3 St Rim",
	    // 154
	    "Medium", "Medium Rim", "Medium XS",
	    "Medium Br", "Medium Br Rim", "Medium Br XS",
	    "Medium St", "Medium St Rim", "Medium St XS",
	    "Medium 2", "Medium 2 Rim", "Medium 2 Br", "Medium 2 Br Rim", "Medium 2 St", "Medium 2 St Rim",
	    "Medium 3", "Medium 3 Rim", "Medium 3 Br", "Medium 3 Br Rim", "Medium 3 St", "Medium 3 St Rim",
	    "Medium 4", "Medium 4 Rim", "Medium 4 Br", "Medium 4 Br Rim", "Medium 4 St", "Medium 4 St Rim",
	    // 181
	    "Fat 1", "Fat 1 Rim", "Fat 1 Br", "Fat 1 Br Rim", "Fat 1 St", "Fat 1 St Rim",
	    "Fat 2", "Fat 2 Rim", "Fat 2 Br", "Fat 2 Br Rim", "Fat 2 St", "Fat 2 St Rim",
	    // 193
	    "Acoustic", "Acoustic Rim", "Acoustic Br", "Acoustic Br Rim", "Acoustic St", "Acoustic St Rim",
	    "Vintage", "Vintage Rim", "Vintage Br", "Vintage Br Rim", "Vintage St", "Vintage St Rim",
	    "Comp", "Comp Rim", "Comp Br", "Comp Br Rim", "Comp St", "Comp St Rim",

	    "Jazz", "Jazz Rim", "Jazz x-stick",
	    "Jazz Brass", "Jazz Brass Rim", "Jazz Brass x-stick",
	    "Jazz Steel", "Jazz Steel Rim", "Jazz Steel x-stick",

	    "Dirty", "Dirty Rim", "Dirty Br", "Dirty Br Rim", "Dirty St", "Dirty St Rim",
	    "13\"", "13\" Rim", "Birch", "Birch Rim",
	    "TD7 Maple", "TD7 Maple Rim", "Ballad",
	    "Brush 1", "Brush 2", "Brush 3", "Brush Tap", "Brush Slp", "Brush Swl", "Brush Tmb",
	    "MIDI Br 1", "MIDI Br 2", "MIDI Br 3",
	    "Boston", "Boston Rim",
	    "Bronze", "Bronze Rim", "Bronze 2", "Bronz 2 Rim",
	    "Birch 2", "Copper", "Copper 2", "10\"", "L.A.", "London",
	    "Ring", "Ring Rim", "Rock", "Rock Rim", "R8 Maple", "R8 Maple Rim", "Big Shot",
	    "Std1 1", "Std1 2", "Std2 1", "Std2 2",
	    "Room 1", "Room 2", "Power 1", "Power 2", "Gate", "Jazz 2", "Jazz 3",
	    "Funk", "Funk Rim", "Bop", "Bop Rim",
	    "Piccolo 5", "Piccolo 5 Rim", "Piccolo 6", "Piccolo 6 Rim",
	    "Medium 5", "Medium 5 Rim", "Medium 6", "Medium 6 Rim",
	    "Medium 7", "Medium 7 Rim", "Medium 8", "Medium 8 Rim",
	    "Fat 3", "Fat 3 Rim", "Fat 4", "Fat 4 Rim",
	    "Dynamic", "Dynamic Rim",
	    "Roll", "Buzz", "Dopin 1", "Dopin 2", "Raggae", "Cruddy",
	    "Dance 1", "Dance 2", "House", "House Dopin", "Clap!", "Whack",
	    "TR808", "TR909", "Elec 1", "Elec 2", "Elec 3", "ElNoiz",
	    "Hip Hop 1", "Hip Hop 2", "LoFi", "LoFi Rim", "Radio",
	    "Cross Stick 1", "Cross Stick 2", "Cross Stick 3",
	    "Cross Stick 4", "Cross Stick 5", "Cross Stick 6", "808 Cross Stick" },
	new Object[] {
	    "Tom",
	    "Oyster 1", "Oyster 2", "Oyster 3", "Oyster 4",
	    "Comp 1",   "Comp 2", "Comp 3", "Comp 4",
	    "Fibre 1",  "Fibre 2", "Fibre 3", "Fibre 4",
	    "Dry 1 1",   "Dry 1 2", "Dry 1 3", "Dry 1 4",
	    "Dry 2 1",   "Dry 2 2", "Dry 2 3", "Dry 2 4",
	    "Maple 1",  "Maple 2", "Maple 3", "Maple 4",
	    "Rose 1",   "Rose 2", "Rose 3", "Rose 4",
	    "Sakura 1", "Sakura 2", "Sakura 3", "Sakura 4",
	    "Jazz 1-1", "Jazz 1-2", "Jazz 1-3", "Jazz 1-4",
	    "Jazz 2-1", "Jazz 2-2", "Jazz 2-3", "Jazz 2-4",
	    "Buzz 1 1", "Buzz 1 2", "Buzz 1 3", "Buzz 1 4",
	    "Buzz 2 1", "Buzz 2 2", "Buzz 2 3", "Buzz 2 4",
	    "Buzz 3 1", "Buzz 3 2", "Buzz 3 3", "Buzz 3 4",
	    "Buzz 4 1", "Buzz 4 2", "Buzz 4 3", "Buzz 4 4",
	    "Natural 1 1", "Natural 1 2", "Natural 1 3", "Natural 1 4",
	    "Natrual 2 16-1", "Natrual 2 16-2", "Natrual 2 16-3", "Natrual 2 16-4",
	    "Studio 1", "Studio 1 2", "Studio 3", "Studio 4",
	    "Slap 1", "Slap 2", "Slap 3", "Slap 4",
	    "Room 1 19-1", "Room 1 19-2", "Room 1 19-3", "Room 1 19-4",
	    "Room 2 20-1", "Room 2 20-2", "Room 2 20-3", "Room 2 20-4",
	    "Room 3 21-1", "Room 3 21-2", "Room 3 21-3", "Room 3 21-4",
	    "Room 4 22-1", "Room 4 22-2", "Room 4 22-3", "Room 4 22-4",
	    "Room 5 23-1", "Room 5 23-2", "Room 5 23-3", "Room 5 23-4",
	    "Big 1", "Big 2", "Big 3", "Big 4",
	    "Rock 1", "Rock 2", "Rock 3", "Rock 4",
	    "Punch 1", "Punch 2", "Punch 3", "Punch 4",
	    "Oak 1", "Oak 2", "Oak 3", "Oak 4",
	    "Balsa 1", "Balsa 2", "Balsa 3", "Balsa 4",
	    "Vintage 1", "Vintage 2", "Vintage 3", "Vintage 4",
	    "Brush 1-1", "Brush 1-2", "Brush 1-3", "Brush 1-4",
	    "Brush 2-1", "Brush 2-2", "Brush 2-3", "Brush 2-4",
	    "Dark 1", "Dark 2", "Dark 3", "Dark 4",
	    "Attack 1", "Attack 2", "Attack 3", "Attack 4",
	    "Hall 1", "Hall 2", "Hall 3", "Hall 4",
	    "Birch 1", "Birch 2", "Birch 3", "Birch 4",
	    "Beech 1", "Beech 2", "Beech 3", "Beech 4",
	    "Micro 1", "Micro 2", "Micro 3", "Micro 4",
	    "Bend 1", "Bend 2", "Bend 3", "Bend 4",
	    "Bowl 1", "Bowl 2", "Bowl 3", "Bowl 4",
	    "Dirty 1", "Dirty 2", "Dirty 3", "Dirty 4",
	    "Studio 1 1", "Studio 1 2", "Studio 1 3", "Studio 1 4", "Studio 1 5", "Studio 1 6",
	    "Studio 2 1", "Studio 2 2", "Studio 2 3", "Studio 2 4", "Studio 2 5", "Studio 2 6",
	    "Room 6 1", "Room 6 2", "Room 6 3", "Room 6 4", "Room 6 5", "Room 6 6",
	    "Power 1", "Power 2", "Power 3", "Power 4", "Power 5", "Power 6",
	    "Jazz 3-1", "Jazz 3-2", "Jazz 3-3", "Jazz 3-4", "Jazz 3-5", "Jazz 3-6",
	    "Brush 3-1", "Brush 3-2", "Brush 3-3", "Brush 3-4", "Brush 3-5", "Brush 3-6",
	    "Gate 1", "Gate 2", "Gate 3", "Gate 4",
	    "LoFi 1", "LoFi 2", "LoFi 3", "LoFi 4",
	    "ElBend 1 1", "ElBend 1 2", "ElBend 1 3", "ElBend 1 4",
	    "ElBend 2 1", "ElBend 2 2", "ElBend 2 3", "ElBend 2 4",
	    "ElBend 3 1", "ElBend 3 2", "ElBend 3 3", "ElBend 3 4",
	    "ElNoise 1", "ElNoise 2", "ElNoise 3", "ElNoise 4",
	    "ElDual 1", "ElDual 2", "ElDual 3", "ElDual 4",
	    "Elec 1", "Elec 2", "Elec 3", "Elec 4", "Elec 5", "Elec 6",
	    "TR808 1", "TR808 2", "TR808 3", "TR808 4", "TR808 5", "TR808 6" },
	new Object[] {
	    "Hi-Hat",
	    "Pure", "Pure Edge", "Bright", "Bright Edge",
	    "Jazz", "Jazz Edge", "Thin", "Thin Edge",
	    "Heavy", "Heavy Edge", "Light", "Light Edge",
	    "Dark", "Dark Edge", "12\"", "12\" Edge",
	    "13\"", "13\" Edge", "14\"", "14\" Edge",
	    "15\"", "15\" Edge", "Brush 1", "Brush 2",
	    "Sizzle 1", "Sizzle 2", "Voice", "HandC",
	    "Tambrn", "Maracs", "TR808", "TR909",
	    "CR78", "Mtl808", "Mtl909", "Mtl78",
	    "LoFi 1", "LoFi 2" },
	new Object[] {
	    "Crash",
	    "Medium 14", "Medium 16", "Medium 18", "Quik 16",
	    "Quik 18", "Thin 16", "Thin 18", "Brush 1",
	    "Brush 2", "Sizzle Brush", "Swell", "Splash 6",
	    "Splash 8", "Splash 10", "Splash 12", "Cup 4",
	    "Cup 6", "Hand Splash 8", "Hand Splash 10", "China 10",
	    "China 12", "China 18", "China 20", "Sizzle China",
	    "Swell China", "Piggyback", "Piggyback Crash 1", "Piggyback Crash 2",
	    "Piggyback Crash 3", "Piggyback Splash 1", "Piggyback Splash 2",
	    "Phase Cymbal", "Electric", "TR808", "LoFi 1", "LoFi 2" },
	new Object[] {
	    "Ride",
	    "Jazz", "Jazz (Edge)", "Jazz (Bow)", "Jazz (Bow/Bell)",
	    "Pop", "Pop (Edge)", "Pop (Bow)", "Pop (Bow/Bell)",
	    "Rock", "Rock (Edge)", "Rock (Bow)", "Rock (Bow/Bell)",
	    "Light", "Light (Edge)", "Light (Bow)", "Light (Bow/Bell)",
	    "Crash", "Crash (Edge)", "Dark Crash", "Dark Crash (Edge)",
	    "Brush 1", "Brush 2", "Sizzle Brush",
	    "Sizzle 1", "Sizzle 1 (Edge)", "Sizzle 1 (Bow)", "Sizzle 1 (Bow/Bell)",
	    "Sizzle 2", "Sizzle 2 (Edge)", "Sizzle 2 (Bow)", "Sizzle 2 (Bow/Bell)",
	    "Sizzle 3", "Sizzle 3 (Edge)", "Sizzle 3 (Bow)", "Sizzle 3 (Bow/Bell)",
	    "Sizzle 4",
	    "Piggyback 1", "Piggyback 1 (Bow)", "Piggyback 1 (Bow/Bell)",
	    "Piggyback 2", "Piggyback 2 (Bow)", "Piggyback 2 (Bow/Bell)",
	    "LoFi", "LoFi (Edge)", "LoFi (Bow)" },
	new Object[] {
	    "Percussion",
	    "R8 Bongo Hi", "R8 Bongo Lo", "R8 Bongo 2 Hi", "R8 Bongo 2 Lo",
	    "Bongo Hi", "Bongo Lo", "Bongo 2 Hi", "Bongo 2 Lo",
	    "R8 Conga Mute", "R8 Conga Hi", "R8 Conga Lo", "Conga Mute",
	    "Conga Sl", "Conga Open", "Conga Lo", "Conga Mute (VS)",
	    "Conga Sl (VS)", "Cowbell 1", "Cowbell 2", "Cowbell Duo",
	    "Claves", "Guiro Long 1", "Giuro Short", "Guiro Long 2",
	    "Giuro (VS)", "Maracas", "Shaker", "Small Shaker",
	    "tambourine 1", "tambourine 2", "tambourine 3", "tambourine 4",
	    "Tmbl 1 Hi", "Tmbl 1 Rim", "Tmbl 1 Low", "Paila",
	    "Tmbl 2 Hi", "Tmbl 2 Low", "VibraSlp", "Agogo Hi",
	    "Agogo Lo", "Agogo 2 Hi", "Agogo 2 Lo", "Cabasa Up",
	    "Cabasa Down", "Cabasa (VS)", "Cuica Mute 1", "Cuica Open",
	    "Cuica Lo", "Cuica Mute 2", "Pandro Mute", "Pandro Open",
	    "Pandro Sl", "Pandro (VS)", "Surdo Hi Mute", "Surdo Hi Open",
	    "Surdo Hi (VS)", "Surdo Lo Mute", "Surdo Lo Open", "Surdo Lo (VS)",
	    "Whistle", "Whistle Short", "Caxixi", "Tabla Na",
	    "TablaTin", "TablaTun", "Tabla Te", "Tabla Ti",
	    "Baya Ge", "Baya Ka", "Baya Gin", "Baya Sld",
	    "Pot Drum", "Pot Drum Mute", "Pot Drum (VS)", "Talking Drum",
	    "Thai Gong", "Thai Gong 2", "Bell Tree", "Tiny Gong",
	    "Gong", "TemplBell", "Wa-Daiko", "Taiko",
	    "Sleibell", "Tree Chime", "Tringl Open", "Tringl Mute",
	    "Tringl (VS)", "R70 Tri Open", "R70 Tri Mute", "R70 Tri (VS)",
	    "Castanet", "Wood Block Hi", "Wood Block Lo", "Concert BD",
	    "Concert BD Mute", "Hand Cymbal", "Hand Cymbal Mute", "Timpani G",
	    "Timpani C", "Timpani E", "Percussion Hit 1", "Percussion Hit 2",
	    "Orchestra Major", "Orchestra Minor", "Orchestra Diminish",
	    "Kick/Roll", "Kick/Cymbal", "Orchestra Roll", "Orchestra Chok",
	    "Hit Roll", "Finale",
	    "808 Clap", "808 Cwbl 1", "808 Cwbl 2", "808 Marcs", "808 Claves", "808 Conga", "909 Rim", "909 Clap",
	    "78 Cowbel", "78 Guiro", "78 Giro St", "78 Maracs", "78 MBeat",
	    "78 Tambrn", "78 Bongo", "78 Claves", "78 Rim",
	    "55 Claves",  },
	new Object[] {
	    "Special",
	    "Applause", "Encore", "Bird", "Dog",
	    "Bubbles", "Heart Beat", "Telephone", "Punch",
	    "Kung Foo", "Pistol", "Gun Shot", "Glass", "Hammer",
	    "Bucket", "Barrel", "Trash Can", "Af Stomp",
	    "Bounce", "Cuica Hit", "Monster", "Air Drive",
	    "Car Door", "Car Cell", "Car Engine", "Car Horn",
	    "Helicopter", "Thunder", "Bomb", "Sticks", "Click", "Tamb FX", "Tek Clik",
	    "Beep Hi", "Beep Low", "Metro Bel", "Metro Clk", "Snaps",
	    "Clap", "Noiz Clap", "Tek Noiz", "Mtl Slap", "R8 Slap",
	    "Vocoder 1", "Vocoder 2", "Vocorder 3", "Dyn Scrch",
	    "Scrach 1", "Scrach 2", "Scrach 3", "Scrach 4", "Scrach 5", "Scrach 6",
	    "Scrach LP", "Phil Hit", "LoFi Hit", "Hi-Q", "Hoo...",
	    "Dao Drill", "Scrape", "Martian", "Coro Coro",
	    "Coro Bend", "Burt", "Boing 1", "Boing 2",
	    "Tekno Brd", "Nantoka!", "Elec Brd",
	    "Mtl Bend 1", "Mtl Bend 2", "Mtl Noise", "Mtl Phase", "Laser",
	    "Mystery", "Time Trip", "Kick Amb", "Snare Amb",
	    "Tom Amb" },
	new Object[] {
	    "Melodic",
	    "Kalimba", "Steel Drum", "Glockenspiel", "Vibraphone",
	    "Marimba", "Xylophon", "Tubular Bells", "Celesta",
	    "Saw Wave", "TB Bass", "Slap Bass",
	    "Guitar Slide", "Guitar Scrach", "Guitar Distortion", "Guitar Bs 1", "Guitar Bs 2",
	    "Cut Guitar Down", "Cut Guitar Up", "Flet Noize", "Bass Slide",
	    "Wah Guitar Down 1", "Wah Guitar Up 1", "Wah Guitar Down 2", "Wah Guitar Up 2",
	    "Shami VS", "Brass VS", "Strings VS", "Pizicato",
	    "Tekno Hit", "Funk Hit 1", "Funk Hit 2", "Funk Hit 3" },
	new Object[] {
	    "Voice",
	    "Lady Ahh", "Aoouu!", "Hooh!", "Haa!", "Say Yeah!", "Yeah", "Ahhh", "Haaa",
	    "Achaa!", "Nope!", "Bap", "Dat", "Bap Dat VS", "Boot",
	    "Dao Fall 1", "Dao Fall 2", "Dao Fall 3", "Dao Fall 4",
	    "Do Dat VS", "Do Dao VS",
	    "Scat 1 VS", "Scat 2 VS", "Scat 3 VS", "Scat 4 VS", "Scat 5 VS",
	    "Voice K", "VoiceLoK", "Voice S",
	    "Voice T1", "Voice T2", "Voice T3", "Voice T4", "Voice Cr",
	    "Count 1", "Count 2", "Count 3", "Count 4", "Count 5",
	    "Count 6", "Count 7", "Count 8", "Count 9", "Count 10",
	    "Count 11", "Count 12", "Count 13", "Count And", "Count E",
	    "Count A", "Count Ti", "Count Ta" },
	new Object[] {
	    "Reverse",
	    "Kick 1", "Kick 2", "Snare 1", "Snare 2",
	    "Tom", "Crash 1", "Crash 2", "China",
	    "BelTr", "Hi-Q", "MFaze", "AirDr",
	    "Boin 1", "Boin 2", "Bend", "Vocoder",
	    "Carcl", "Engine" },
	new Object[] {
	    "Fixed Hi-Hat",
	    "Std 1 Closed", "Std 1 Edge Closed", "Std 1 Open", "Std 1 Edge Open", "Std 1 Pedal",
	    "Std 2 Closed", "Std 2 Edge Closed", "Std 2 Open", "Std 2 Pedal",
	    "Room Closed", "Room Edge Closed", "Room Open", "Room Edge Open", "Room Pedal",
	    "Power Closed", "Power Edge Closed", "Power Open", "Power Pedal",
	    "Brush Closed", "Brush Edge Closed", "Brush Open", "Brush Pedal",
	    "Elec Closed", "Elec Open", "Elec Pedal",
	    "808 Closed", "808 Edge Closed", "808 Open", "808 Edge Open", "808 Pedal",
	    "LoFi Closed", "LoFi Open", "LoFi Edige Open", "LoFi Pedal" },
	"Off"
    };				// Object[] root

    private static final int[] OFFSET = {
	0,			// kick
	129,			// snare
	324,			// tom
	560,			// hiHat
	598,			// crash
	634,			// ride
	679,			// percussion
	810,			// special
	888,			// melodic
	920,			// voice
	971,			// reverse
	989,			// fixedHiHat
	1023,			// off
    };

    /** Number of Instrument */
    //private static final int N_INSTRUMENT = 1024;

    // cannot use 'static' here, since core.TreeWidget.Nodes.getRoot() is not static.
    public Object[] getRoot() {
	return ROOT;
    }

    public int[] getIndices(int n) {
	int[] d;
	int i;
	for (i = 0; i < OFFSET.length - 1; i++) {
	    if (n < OFFSET[i + 1]) {
		d = new int[] {i, n - OFFSET[i]};
		return d;
	    }
	}
	// if (i != N_INSTRUMENT) throw error
	d = new int[] {i};
	return d;
    }

    public int getValue(int[] indices) {
	return (indices[0] < OFFSET.length - 1)
	    ? OFFSET[indices[0]] + indices[1] : OFFSET[OFFSET.length - 1];
    }

} // Instrument
