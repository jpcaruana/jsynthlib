/*
 * JSynthlib - Voice Constants for Yamaha DX7s
 * ===========================================
 * @version $Id$
 * @author  Torsten Tittmann
 *
 * Copyright (C) 2002-2004 Torsten.Tittmann@gmx.de
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
package synthdrivers.YamahaDX7s;

public class YamahaDX7sVoiceConstants
{
	// voice patch/bank numbers of single/bank driver
	protected static final String[] SINGLE_VOICE_PATCH_NUMBERS = {
		"01/33","02/34","03/35","04/36","05/37","06/38","07/39","08/40",
		"09/41","10/42","11/43","12/44","13/45","14/46","15/47","16/48",
		"17/49","18/50","19/51","20/52","21/53","22/54","23/55","24/56",
		"25/57","26/58","27/59","28/60","29/61","30/62","31/63","32/64"
	};

	protected static final String[] SINGLE_VOICE_BANK_NUMBERS = {
		"Internal (01-32)",
		"Internal (33-64)",
		"Cartridge (01-32)",
		"Cartridge (33-64)"
	};

	protected static final String[] BANK_VOICE_PATCH_NUMBERS = SINGLE_VOICE_PATCH_NUMBERS;
	
	protected static final String[] BANK_VOICE_BANK_NUMBERS = {
		"Internal (01-32)",
		"Internal (33-64)"
	};

	// Init Single Voice patch ("INIT VOICE")
	protected static final byte [] INIT_VOICE = {
		-16,67,0,0,1,27,99,99,99,99,99,99,99,0,0,0,0,0,0,0,
		0,0,0,0,1,0,7,99,99,99,99,99,99,99,0,0,0,0,0,0,
		0,0,0,0,0,1,0,7,99,99,99,99,99,99,99,0,0,0,0,0,
		0,0,0,0,0,0,1,0,7,99,99,99,99,99,99,99,0,0,0,0,
		0,0,0,0,0,0,0,1,0,7,99,99,99,99,99,99,99,0,0,0,
		0,0,0,0,0,0,0,0,1,0,7,99,99,99,99,99,99,99,0,0,
		0,0,0,0,0,0,0,99,0,1,0,7,99,99,99,99,50,50,50,50,
		0,0,1,35,0,0,0,1,0,3,24,73,78,73,84,32,86,79,73,67,
		69,81,-9
	};
}
