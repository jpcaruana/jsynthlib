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
	public static final int SYSEX_HEADER_OFFSET			= 8;

	public static final int QUASAR_PATCH_SIZE			= 223;
	public static final int QUASAR_PATCH_NAME_SIZE		= 8;
	public static final int QUASAR_PATCH_NAME_START		= 214;

	/** 
	* Offset for the "real" performance parameters 
	*/
	public static final int QUASAR_SYSEX_PERFORMANCE_OFFSET	= 0x05;
	/** 
	* Offset for the temporary parameters 
	*/
	public static final int QUASAR_SYSEX_TEMPORARY_OFFSET	= 0x01;
	/** 
	* The SysEx ID for the Quasimidi Quasar
	*/
	public static final String QUASAR_SYSEX_ID				= "F03F**2044";

	//"F03F**2052**********F7" is the format for requests
	public static final String[] QUASAR_SYSEX_PERFORMANCE_REQUEST   = {
		"F0 3F @@ 20 52 *perfNumber* 00 00 00 41 F7 ", // Request performance common parameter
		"F0 3F @@ 20 52 *perfNumber* 01 00 00 18 F7 ", // Request performance part 1 parameter
		"F0 3F @@ 20 52 *perfNumber* 02 00 00 18 F7 ", // Request performance part 2 parameter
		"F0 3F @@ 20 52 *perfNumber* 03 00 00 18 F7 ", // Request performance part 3 parameter
		"F0 3F @@ 20 52 *perfNumber* 04 00 00 18 F7 ", // Request performance part 4 parameter
		"F0 3F @@ 20 52 *perfNumber* 05 00 00 08 F7 "  // Request performance name
	};
	
	/** Performance numbers go from 00 to 99 */
	public static final String[] QUASAR_PATCH_NUMBERS   = {
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
}
