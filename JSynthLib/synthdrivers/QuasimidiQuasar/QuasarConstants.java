/*
 * Copyright 2004 Joachim Backhaus
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

package synthdrivers.QuasimidiQuasar;

/**
 * Constants for the Quasimidi Quasar synthdriver
 *
 * @author  Joachim Backhaus
 * @version $Id$
 */
public class QuasarConstants {
	public static final byte SYSEX_START_BYTE			= (byte) 0xF0;
	public static final byte SYSEX_END_BYTE				= (byte) 0xF7;
	public static final int  SYSEX_HEADER_OFFSET		= 8;

	public static final int PATCH_SIZE			= 223;
	public static final int TEMPORARY_SIZE		= 644;
	public static final int PATCH_NAME_SIZE		= 8;
	public static final int PATCH_NAME_START	= 214;

    public static final int PART_SIZE			= 33;

	public static final int ARRAY_DEVICE_ID_OFFSET	 	= 2;
	/**
	* Offset for the performance offset in the byte array
	*/
	public static final int ARRAY_PERFORMANCE_OFFSET	= 5;
	/**
	* Offset for the performance part in the byte array
	*/
	public static final int ARRAY_PERF_PART_OFFSET		= 6;
	/**
	* Offset for the beginning of performance part 1 parameters in the byte array
	*/
	public static final int ARRAY_PART_1_OFFSET		= 74;
	/**
	* Offset for the beginning of performance part 2 parameters in the byte array
	*/
	public static final int ARRAY_PART_2_OFFSET		= 107;
	/**
	* Offset for the beginning of performance part 3 parameters in the byte array
	*/
	public static final int ARRAY_PART_3_OFFSET		= 140;
	/**
	* Offset for the beginning of performance part 4 parameters in the byte array
	*/
	public static final int ARRAY_PART_4_OFFSET		= 173;
	/**
	* Offset for the beginning of performance name parameters in the byte array
	*/
	public static final int ARRAY_NAME_OFFSET		= 206;

	/**
	* Offset for the "real" performance parameters
	*/
	public static final int SYSEX_PERFORMANCE_OFFSET	= 0x05;
	/**
	* Offset for the temporary parameters
	*/
	public static final int SYSEX_TEMPORARY_OFFSET	= 0x01;
	/**
	* The SysEx ID for the Quasimidi Quasar
	*/
	public static final String SYSEX_ID				= "F03F**2044";

	/**
	* Array holding the request headers for the full Performance<br>
	* (Common, Part 1-4 and name parameters)
	*/
	//"F03F**2052**********F7" is the format for requests
	public static final String[] SYSEX_PERFORMANCE_REQUEST   = {
		"F0 3F @@ 20 52 *perfNumber* 00 00 00 41 F7 ", // Request performance common parameter
		"F0 3F @@ 20 52 *perfNumber* 01 00 00 18 F7 ", // Request performance part 1 parameter
		"F0 3F @@ 20 52 *perfNumber* 02 00 00 18 F7 ", // Request performance part 2 parameter
		"F0 3F @@ 20 52 *perfNumber* 03 00 00 18 F7 ", // Request performance part 3 parameter
		"F0 3F @@ 20 52 *perfNumber* 04 00 00 18 F7 ", // Request performance part 4 parameter
		"F0 3F @@ 20 52 *perfNumber* 05 00 00 08 F7 "  // Request performance name
	};

	/**
	* Performance numbers are going from 00 to 99
	*/
	public static final String[] PATCH_NUMBERS   = {
		"00" , "01", "02", "03", "04", "05", "06", "07", "08", "09",
		"10" , "11", "12", "13", "14", "15", "16", "17", "18", "19",
		"20" , "21", "22", "23", "24", "25", "26", "27", "28", "29",
		"30" , "31", "32", "33", "34", "35", "36", "37", "38", "39",
		"40" , "41", "42", "43", "44", "45", "46", "47", "48", "49",
		"50" , "51", "52", "53", "54", "55", "56", "57", "58", "59",
		"60" , "61", "62", "63", "64", "65", "66", "67", "68", "69",
		"70" , "71", "72", "73", "74", "75", "76", "77", "78", "79",
		"80" , "81", "82", "83", "84", "85", "86", "87", "88", "89",
		"90" , "91", "92", "93", "94", "95", "96", "97", "98", "99",
	};

	/**
	* Note names
	*/
	public static final String [] NOTES = new String [] {	"C-2","C#-2","D-2","D#-2","E-2","F-2","F#-2","G-2","G#-2","A-2","A#-2","B-2",
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

    /** The bank names */
	public static final String[] BANK_NAMES   = {
		"General MIDI",
		"Bank S01",
		"Bank S02 (Best of)",
		"Bank S03",
		"Bank S04",
		"Bank S05",
		"Bank S06",
		"Bank S07",
		"Bank 1, Card 1",
		"Bank 2, Card 1",
		"Bank 1, Card 2",
		"Bank 2, Card 2"
	};

	/** The effect names for FX1 */
	public static final String[] FX1_EFFECTS   = {
		"Room", "Small Room", "Warm Room", "Chamber", "Chamber 2",
		"Plate 1", "Plate 2", "Hall", "Large Hall", "Cathedral",
		"Gated Reverb 1", "Gated Reverb 2", "Gated Reverb 3", "Duck Reverb",
		"Early Reflections 1", "Early Reflections 2", "Early Reflections 3", "Early Reflections 4",
		"Raindrops", "Long Delay", "Duck Delay", "HQ Delay", "Bypass"
	};

	/** Helper for FX1 */
	public static final int[] FX1_HELPER   = {
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0,   // Room to cathedral
		1, 1, 1, 1,                     // GatedReverb0 to DuckReverb
		0, 0, 0, 0, 0,                  // EarlyReflection0 to Raindrops
		2, 3, 2, -1                     // LongDelay to bypass
	};

	/**
	* FX1 parameter labels
	*/
	public static final String[][] FX1_EFFECTS_PARAMETER   = {
		 {"Inp. Level", "SFeed", "Decay"},                                      // Typ 0
		 {"Inp. Level", "SFeed", "Treshold", "Hold-time", "Attack", "Release"}, // Typ 1
		 {"Inp. Level", "SFeed", "Delay", "Feedback"},                          // Typ 2
		 {"Inp. Level", "SFeed", "Delay", "Feedback", "Treshold", "Hold-time"}  // Typ 3
	};

	/** The effect names for FX2 */
	public static final String[] FX2_EFFECTS   = {
		"Chorus 1", "Chorus 2", "Chorus 3",
		"Flanger 1", "Flanger 2",
		"Phaser 1", "Phaser 2",
		"Rotary & Overdrive", "Rotor 2", "Rotor 3", "Rotor 4",
		"Vibrato", "Panning", "Tremolo",
		"Short Delay", "Long Delay", "Cross Delay",
		"Ping Pong", "Gated Delay", "Duck Delay",
		"Special FX", "Equalizer", "WahWah+Ov",
		"Auto WahWah", "Warm Overdrive", "Distortion",
		"Ring Modulation", "Vocoder",
		"HQ Delay", "Bypass"
	};

	/** Helper for FX2 */
	public static final int[] FX2_HELPER   = {
		0, 0, 0,		// Chorus 1-3
		1, 1, 0, 0,		// Flanger 1-2 / Phaser 1-2
		2, 2, 2, 2,		// Rotaries
		3, 4, 3, 		// Vibrato / Panning / Tremolo
		5, 5, 6, 3,		// Short-/Long Delay / Cross Delay / Ping-Pong
		7, 7,			// Gated Delay / Duck Delay
		8, 9, 10,		// Special FX / Equalizer / WahWah + Overdrive
		11,	12, 13, 12,	// Auto WahWah / Overdrive / Distortion / Ring Mod
		14, 5, -1		// Vocoder / HQ-Delay / Bypass		
	};

	/**
	* FX2 parameter labels
	*/
	public static final String[][] FX2_EFFECTS_PARAMETER   = {
		// Typ 0
		{"Inp. Level", "Depth", "Rate", "Center", "Out. level" },
		// Typ 1
		{"Inp. Level", "Depth", "Rate", "Center", "Feedback", "Out. level"},
		// Typ 2
		{"Inp. Level", "Rot Lo", "Rot Hi", "Decay", "Rot level", "Switch", "Drive", "Out. level" },
		// Typ 3
		{"Inp. Level", "Depth", "Rate", "Out. level"},
		// Typ 4
		{"Inp. Level", "Depth", "Rate", "Phase", "Manual pan", "Out. level"},
		// Typ 5
		{"Inp. Level", "Delay", "Feedback", "Out. level"},
		// Typ 6
		{"Inp. Level", "Delay left", "Feedback left", "Delay right", "Feedback right", "Cross feedback", "Mute right", "Sync time", "Out. level"},
		// Typ 7 (Is there an empty parameter between "Release" and "Out. level"?
		{"Inp. Level", "Delay", "Feedback", "Treshold", "Hold time", "Attack", "Release", "Out. level"},
		// Typ 8 (Special FX)
		{"Inp. Level", "Depth", "Rate", "Delay", "Feedback", "Out. level"},
		// Typ 9
		{"Inp. Level", "Out. level", "100 Hz", "500 Hz", "3 kHz"},
		// Typ 10
		{"Inp. Level", "Cutoff", "Drive", "Clip level", "Out. level"},
		// Typ 11
		{"Inp. Level", "Drive", "Clip level", "Out. level"},
		// Typ 12
		{"Inp. Level", "Out. level"},
		// Typ 13
		{"Inp. Level", "Out. level", "Drive"},
		// Typ 14		
		{"Inp. Level", "150 Hz", "350 Hz", "760 Hz", "1,6 kHz", "3,6 kHz", "Attack", "Decay", "Out. level"}
		
	};

	/** The Performance modes of the Quasar */
	public static final String[] PERFORMANCE_MODES   = {
		"Single", // Only Part 13 is used
		"Double", // Only Parts 13 & 14  are used
		"Layer 3", // Parts 13 - 15 layered
		"Layer 4",
		"Split 1+1", // Performance value parameter is used as split key parameter
		"Split 1+2",
		"Split 1+3",
		"Split 2+2",
		"Split 2+1",
		"Split 3+1",
		// Performance value parameter is used as velocity parameter
		// (Parts 13 is played below this value, Part 14 above)
		"DynSplit",
		// Same as "DynSplit" but using Parts 13/14 & 15/16
		"DynSplit2",
		"Ensemble",
		"SndRotate",
		"UpVocSolo",
		"Unisono" // Performance value parameter is used as detune parameter
	};

	public static final int[][] PERFORMANCE_HELPER   = {
		{1, -1},	// Single
		{2, -1},	// Double
		{3,	-1},	// Layer 3
		{4,	-1},	// Layer 4
		{2,	0},		// Split 1+1
		{3,	0},		// Split 1+2
		{4,	0},		// Split 1+3
		{4,	0},		// Split 2+2
		{3,	0},		// Split 2+1
		{4,	0},		// Split 3+1
		{2,	1},		// DynSplit
		{4, 1},		// DynSplit2
		{4, -1},	// Ensemble
		{4,	-1},	// SndRotate
		{4,	-1},		// UpVocSolo
		{4, 2}			// Unisono
	};

	public static final String[] PERF_VALUE_HELPER	= {
		"Split key",
		"Dyn split",
		"Detune"
	};

	/**
	* Velocity curves
	*/
	/*
	* 00 Lin
	* 01 Lin-
	* 02 Lin+
	* 03 Exp-
	* 04 Ex--
	* 05 Exp+
	* 06 Ex++
	* 07 Fix
	* 08 -Lin
	* 09 -Lin-
	* 0A -Lin+
	* 0B -Exp-
	* 0C -Ex--
	* 0D -Exp+
	* 0E -Ex++
	*/
	public static final String[] VELOCITY_CURVES   = {
		"Lin", "Lin-", "Lin+", "Exp-", "Ex--", "Exp+", "Ex++", "Fix",
		"-Lin", "-Lin-", "-Lin+", "-Exp", "-Ex--", "-Exp+", "-Ex++"
	};

	public static final String[] MASTER_VELOCITY_CURVES   = {
		"Lin", "Lin-", "Lin+", "Exp-", "Ex--", "Exp+", "Ex++", "Fix"
	};

	/**
	* Panorama:
	*/
	/*
	* 00 off
	* 01 7L
	* 02 6L
	* 03 5L
	* 04 4L
	* 05 3L
	* 06 2L
	* 07 1L
	* 08 C
	* 09 1R
	* 0A 2R
	* 0B 3R
	* 0C 4R
	* 0D 5R
	* 0E 6R
	* 0F 7R
	* 10 Rnd
	* 11 Key
	* 12 Yek
	* 13 Dyn
	* 14 Nyd
	*/
	public static final String[] PANORAMA   = {
		"off",
		"Left 7", "Left 6", "Left 5", "Left 4", "Left 3", "Left 2", "Left 1",
		"Center",
		"Right 1", "Right 2", "Right 3", "Right 4", "Right 5", "Right 6", "Right 7",
		"Random", "KEY", "YEK", "DYN", "NYD"
	};

	public static final String[] TRACKMODE   = {
		"muted", "poly", "mono"
	};

	public static final String SWITCH_OFF   = "off";
	public static final String SWITCH_ON    = "on";

	public static final String[] SWITCH    = {
	    SWITCH_OFF,
	    SWITCH_ON
	};
}
