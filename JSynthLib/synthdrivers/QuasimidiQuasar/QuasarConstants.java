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

import core.*;

import java.util.prefs.Preferences;
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
	public static final int PATCH_NAME_START		= 214;


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

	/** The effect names for FX1 */
	public static final String[] FX1_EFFECTS   = {
		"Room", "Small Room", "Warm Room", "Chamber", "Chamber 2",
		"Plate 1", "Plate 2", "Hall", "Large Hall", "Cathedral",
		"Gated Reverb 1", "Gated Reverb 2", "Gated Reverb 3", "Duck Reverb",
		"Early Reflections 1", "Early Reflections 2", "Early Reflections 3", "Early Reflections 4",
		"Raindrops", "Long Delay", "Duck Delay", "HQ Delay", "Bypass"
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
}
