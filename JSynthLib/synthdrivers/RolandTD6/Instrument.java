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
import core.TreeNodes;

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
 */
final class Instrument implements TreeNodes {
  // Can anyone type correct instrument names?
  private static final Object[] ROOT = {
    "Instrument",
    new Object[] {
      "Kick",
      "Double Head", "Kick 2", "Kick 3", "Kick 4", 
      "Kick 5", "Kick 6", "Kick 7", "Kick 8", 
      "Kick 9", "Kick 10", "Kick 11", "Kick 12", 
      "Kick 13", "Kick 14", "Kick 15", "Kick 16", 
      "Studio 1", "Studio 2", "Studio 3", "Studio 4", 
      "Studio 5", "Studio 6", "Studio 7", "Studio 8", 
      "Kick 25", "Kick 26", "Kick 27", "Kick 28", 
      "Kick 29", "Kick 30", "Kick 31", "Kick 32", 
      "Kick 33", "Kick 34", "Kick 35", "Kick 36", 
      "Kick 37", "Kick 38", "Kick 39", "Kick 40", 
      "Kick 41", "Kick 42", "Kick 43", "Jazz 1", 
      "Jazz 2", "Kick 46", "Kick 47", "Kick 48", 
      "Kick 49", "Kick 50", "Kick 51", "Kick 52", 
      "Kick 53", "Kick 54", "Kick 55", "Kick 56", 
      "Kick 57", "Kick 58", "Kick 59", "Kick 60", 
      "Kick 61", "Kick 62", "Kick 63", "Kick 64", 
      "Kick 65", "Kick 66", "Kick 67", "Kick 68", 
      "Kick 69", "Kick 70", "Kick 71", "Kick 72", 
      "Kick 73", "Kick 74", "Kick 75", "Kick 76", 
      "Kick 77", "Kick 78", "Kick 79", "Kick 80", 
      "Kick 81", "Kick 82", "Kick 83", "Kick 84", 
      "Kick 85", "Kick 86", "Kick 87", "Kick 88", 
      "Kick 89", "Kick 90", "Kick 91", "Kick 92", 
      "Kick 93", "Kick 94", "Kick 95", "Kick 96", 
      "Inside", "Std1 1", "Std1 2", "Std2 1", 
      "Std2 2", "Kick 102", "Kick 103", "Kick 104", 
      "Kick 105", "Jazz 3", "Jazz 4", "Kick 108", 
      "Kick 109", "Kick 110", "Kick 111", "Kick 112", 
      "Kick 113", "Kick 114", "Kick 115", "Kick 116", 
      "Kick 117", "Kick 118", "Kick 119", "Kick 120", 
      "Kick 121", "Kick 122", "Kick 123", "Kick 124", 
      "Kick 125", "Kick 126", "Kick 127", "Kick 128", 
      "Synth Bass",  },
    new Object[] {
      "Snare",
      // 130 (129)
      "Custom", "Snare 2", "Snare 3", "Snare 4", 
      "Snare 5", "Snare 6",
      // 136
      "Snare 7", "Snare 8", 
      "Snare 9", "Snare 10", "Piccolo Steel", "Piccolo 1 Steel Rim", 
      "Snare 13", "Snare 14", "Snare 15", "Snare 16", 
      "Snare 17", "Snare 18", "Snare 19", "", 
      "Piccolo 3 Brass", "Piccolo 3 Brass Rim", "Snare 23", "Snare 24", 
      // 154
      "Snare 25", "Snare 26", "Snare 27", "Snare 28", 
      "Snare 29", "", "", "Snare 32", 
      "Snare 33", "Snare 34", "Snare 35", "Snare 36", 
      "Snare 37", "Snare 38", "Snare 39", "Midium 3", 
      "Midium 3 Rim", "Snare 42", "Snare 43", "Snare 44", 
      "Snare 45", "Snare 46", "Snare 47", "Snare 48", 
      "Snare 49", "Snare 50", "Snare 51",
      // 181
      "Fat 1", 
      "Snare 53", "Snare 54", "Snare 55", "Snare 56", 
      "Snare 57", "Snare 58", "Snare 59", "Snare 60", 
      "Snare 61", "Snare 62", "Snare 63",
      // 193
      "Acoustic", 
      "Snare 65", "Snare 66", "Snare 67", "Snare 68", 
      "Snare 69", "Snare 70", "Snare 71", "Snare 72", 
      "Snare 73", "Snare 74", "Snare 75", "Snare 76", 
      "Snare 77", "Snare 78", "Snare 79", "Snare 80", 
      "Snare 81", "Jazz", "Jazz Rim", "Jazz x-stick", 
      "Jazz Brass", "Jazz Brass Rim", "Jazz Brass x-stick", "Jazz Steel", 
      "Jazz Steel Rim", "Jazz Steel x-stick", "Snare 91", "Snare 92", 
      "Snare 93", "Snare 94", "Snare 95", "Snare 96", 
      "Snare 97", "Snare 98", "Snare 99", "Snare 100", 
      "Snare 101", "Snare 102", "Snare 103", "Brush 1", 
      "Brush 2", "Brush 3", "Brush Tmb", "Snare 108", 
      "Snare 109", "Snare 110", "Snare 111", "Snare 112", 
      "Snare 113", "Snare 114", "Snare 115", "Snare 116", 
      "Snare 117", "Snare 118", "Snare 119", "Snare 120", 
      "Snare 121", "Snare 122", "Snare 123", "Snare 124", 
      "Snare 125", "Snare 126", "Snare 127", "Snare 128", 
      "Snare 129", "Snare 130", "Snare 131", "Snare 132", 
      "Snare 133", "Snare 134", "Snare 135", "Snare 136", 
      "Snare 137", "Snare 138", "Snare 139", "Snare 140", 
      "Snare 141", "Jazz 2", "Jazz 3", "Snare 144", 
      "Snare 145", "Snare 146", "Snare 147", "Snare 148", 
      "Snare 149", "Snare 150", "Snare 151", "Snare 152", 
      "Snare 153", "Snare 154", "Snare 155", "Snare 156", 
      "Snare 157", "Snare 158", "Snare 159", "Snare 160", 
      "Snare 161", "Snare 162", "Snare 163", "Snare 164", 
      "Snare 165", "Snare 166", "Snare 167", "Snare 168", 
      "Snare 169", "Snare 170", "Snare 171", "Snare 172", 
      "Snare 173", "Snare 174", "Snare 175", "Snare 176", 
      "Snare 177", "Snare 178", "Snare 179", "Snare 180", 
      "Snare 181", "Snare 182", "Snare 183", "Snare 184", 
      "Snare 185", "Snare 186", "Snare 187", "Snare 188", 
      "Snare 189", "Snare 190", "Snare 191", "Snare 192", 
      "Snare 193", "Snare 194", "808 Cross Stick",  },
    new Object[] {
      "Tom",
      "Oyster 1", "Oyster 2", "Osyter 33", "Oyster 4", 
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
      "Palsa 1", "Palsa 2", "Palsa 3", "Palsa 4", 
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
      "TR808 1", "TR808 2", "TR808 3", "TR808 4", "TR808 5", "TR808 6",
    },
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
      "LoFi1", "LoFi2",  },
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
      "Phase Cymbal", "Electric", "TR808", "LoFi 1", "LoFi 2", 
    },
    new Object[] {
      "Ride",
      "Jazz", "Jazz (Edge)", "Jazz (Bow)", "Jazz (Bow/Bell)", 
      "Pop", "Pop (Edge)", "Pop (Bow)", "Pop (Bow/Bell)", 
      "Rock", "Rock (Edge)", "Rock (Bow)", "Rock (Bow/Bell)", 
      "Light", "Light (Edge)", "Light (Bow)", "Light (Bow/Bell)", 
      "Crash", "Crash (Edge)", "Dark Crash", "Dark Crash (Edge)", 
      "Brush 1", "Brush 2", "Sizzle Brush", "Sizzle 1", 
      "Sizzle 1 (Edge)", "Sizzle 1 (Bow)", "Sizzle 1 (Bow/Bell)", "Sizzle 2",
      "Sizzle 2 (Edge)", "Sizzle 2 (Bow)", "Sizzle 2 (Bow/Bell)", "Sizzle 3",
      "Sizzle 3 (Edge)", "Sizzle 3 (Bow)", "Sizzle 3 (Bow/Bell)", "Sizzle 4",
      "Piggyback 1", "Piggyback 1 (Bow)", "Piggyback 1 (Bow/Bell)",
      "Piggyback 2", "Piggyback 2 (Bow)", "Piggyback 2 (Bow/Bell)",
      "LoFi", "LoFi (Edge)", "LoFi (Bow)",  },
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
      "Percussion 114", "Percussion 115", "Percussion 116", 
      "Percussion 117", "Percussion 118", "Percussion 119", "Percussion 120",
      "Percussion 121", "Percussion 122", "Percussion 123", "Percussion 124",
      "Percussion 125", "Percussion 126", "Percussion 127", "Percussion 128",
      "Percussion 129", "Percussion 130", "55 Claves",  },
    new Object[] {
      "Special",
      "Applause", "Special 2", "Special 3", "Special 4", 
      "Special 5", "Special 6", "Special 7", "Special 8", 
      "Special 9", "Special 10", "Special 11", "Special 12", 
      "Special 13", "Special 14", "Special 15", "Special 16", 
      "Special 17", "Special 18", "Special 19", "Special 20", 
      "Special 21", "Special 22", "Special 23", "Special 24", 
      "Special 25", "Special 26", "Special 27", "Special 28", 
      "Special 29", "Special 30", "Special 31", "Special 32", 
      "Special 33", "Special 34", "Special 35", "Special 36", 
      "Special 37", "Special 38", "Special 39", "Special 40", 
      "Special 41", "Special 42", "Special 43", "Special 44", 
      "Special 45", "Special 46", "Special 47", "Special 48", 
      "Special 49", "Special 50", "Special 51", "Special 52", 
      "Special 53", "Special 54", "Special 55", "Special 56", 
      "Special 57", "Special 58", "Special 59", "Special 60", 
      "Special 61", "Special 62", "Special 63", "Special 64", 
      "Special 65", "Special 66", "Special 67", "Special 68", 
      "Special 69", "Special 70", "Special 71", "Special 72", 
      "Special 73", "Special 74", "Special 75", "Special 76", 
      "Special 77", "Rom Amb",  },
    new Object[] {
      "Melodic",
      "Kalimba", "Melodic 2", "Melodic 3", "Melodic 4", 
      "Melodic 5", "Melodic 6", "Melodic 7", "Melodic 8", 
      "Melodic 9", "Melodic 10", "Melodic 11", "Melodic 12", 
      "Melodic 13", "Melodic 14", "Melodic 15", "Melodic 16", 
      "Melodic 17", "Melodic 18", "Melodic 19", "Melodic 20", 
      "Melodic 21", "Melodic 22", "Melodic 23", "Melodic 24", 
      "Melodic 25", "Melodic 26", "Melodic 27", "Melodic 28", 
      "Melodic 29", "Melodic 30", "Melodic 31", "Funk Hit 3", 
    },
    new Object[] {
      "Voice",
      "Lady Ahh", "Voice 2", "Voice 3", "Voice 4", 
      "Voice 5", "Voice 6", "Voice 7", "Voice 8", 
      "Voice 9", "Voice 10", "Voice 11", "Voice 12", 
      "Voice 13", "Voice 14", "Voice 15", "Voice 16", 
      "Voice 17", "Voice 18", "Voice 19", "Voice 20", 
      "Voice 21", "Voice 22", "Voice 23", "Voice 24", 
      "Voice 25", "Voice 26", "Voice 27", "Voice 28", 
      "Voice 29", "Voice 30", "Voice 31", "Voice 32", 
      "Voice 33", "Voice 34", "Voice 35", "Voice 36", 
      "Voice 37", "Voice 38", "Voice 39", "Voice 40", 
      "Voice 41", "Voice 42", "Voice 43", "Voice 44", 
      "Voice 45", "Voice 46", "Voice 47", "Voice 48", 
      "Voice 49", "Voice 50", "Count Ta",  },
    new Object[] {
      "Reverse",
      "Reverse Kick 1", "Reverse 2", "Reverse 3", "Reverse 4", 
      "Reverse 5", "Reverse 6", "Reverse 7", "Reverse 8", 
      "Reverse 9", "Reverse 10", "Reverse 11", "Reverse 12", 
      "Reverse 13", "Reverse 14", "Reverse 15", "Reverse 16", 
      "Reverse 17", "Reverse Engine",  },
    new Object[] {
      "Fixed Hi-Hat",
      "Std 1 Closed Hi-Hat", "Fixed Hi-Hat 2", "Fixed Hi-Hat 3", "Fixed Hi-Hat 4",
      "Fixed Hi-Hat 5", "Fixed Hi-Hat 6", "Fixed Hi-Hat 7", "Fixed Hi-Hat 8",
      "Fixed Hi-Hat 9", "Fixed Hi-Hat 10", "Fixed Hi-Hat 11",
      "Fixed Hi-Hat 12", 
      "Fixed Hi-Hat 13", "Fixed Hi-Hat 14", "Fixed Hi-Hat 15",
      "Fixed Hi-Hat 16", 
      "Fixed Hi-Hat 17", "Fixed Hi-Hat 18", "Fixed Hi-Hat 19",
      "Fixed Hi-Hat 20", 
      "Fixed Hi-Hat 21", "Fixed Hi-Hat 22", "Fixed Hi-Hat 23",
      "Fixed Hi-Hat 24", 
      "Fixed Hi-Hat 25", "Fixed Hi-Hat 26", "Fixed Hi-Hat 27",
      "Fixed Hi-Hat 28", 
      "Fixed Hi-Hat 29", "Fixed Hi-Hat 30", "Fixed Hi-Hat 31",
      "Fixed Hi-Hat 32", 
      "Fixed Hi-Hat 33", "LoFi PdH",  },
    "Off"
  };				// Object[] root

  private static final int[] OFFSET = {  0, // kick
				       129, // snare
				       324, // tom
				       560, // hiHat
				       598, // crash
				       634, // ride
				       679, // percussion
				       810, // special
				       888, // melodic
				       920, // voice
				       971, // reverse
				       989, // fixedHiHat
				      1023, // off
  };

  /** Number of Instrument */
  //private static final int N_INSTRUMENT = 1024;

  // why cannot I use 'static' here?
  //public static Object[] getRoot() {
  public Object[] getRoot() {
    return ROOT;
  }

  public int[] getIndices(int n) {
    int[] d;
    int i;
    for (i = 0; i < OFFSET.length - 1; i++) {
      if (n < OFFSET[i + 1]) {
	d = new int[] { i, n - OFFSET[i] };
	return d;
      }
    }
    // if (i != N_INSTRUMENT) throw error
    d = new int[] { i };
    return d;
  }

  public int getValue(int[] indices) {
    return (indices[0] < OFFSET.length - 1)
      ? OFFSET[indices[0]] + indices[1] : OFFSET[OFFSET.length - 1];
  }

} // Instrument
