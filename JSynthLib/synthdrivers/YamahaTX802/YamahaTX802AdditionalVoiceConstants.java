/*
 * JSynthlib - Additional Voice Constants for Yamaha TX802
 * =======================================================
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
package synthdrivers.YamahaTX802;

public class YamahaTX802AdditionalVoiceConstants
{
	// Additional Voice patch/bank numbers
	protected static final String[] SINGLE_ADDITIONAL_VOICE_PATCH_NUMBERS = {
		"01/33","02/34","03/35","04/36","05/37","06/38","07/39","08/40",
		"09/41","10/42","11/43","12/44","13/45","14/46","15/47","16/48",
		"17/49","18/50","19/51","20/52","21/53","22/54","23/55","24/56",
		"25/57","26/58","27/59","28/60","29/61","30/62","31/63","32/64"
	};

	protected static final String[] SINGLE_ADDITIONAL_VOICE_BANK_NUMBERS = {
		"Internal (01-32)",
		"Internal (33-64)"//,
		//"Cartridge (01-32)",
		//"Cartridge (33-64)",
		//"Bank A (01-32)",
		//"Bank A (33-64)",
		//"Bank B (01-32)",
		//"Bank B (33-64)"
	};

	protected static final String[] BANK_ADDITIONAL_VOICE_PATCH_NUMBERS = SINGLE_ADDITIONAL_VOICE_PATCH_NUMBERS;
	
	protected static final String[] BANK_ADDITIONAL_VOICE_BANK_NUMBERS = {
		"Internal (01-32)",
		"Internal (33-64)"
	};


	// Init Additional Voice patch (no patchname)
	protected static final byte []INIT_ADDITIONAL_VOICE = {
		-16,67,0,5,0,49,0,0,0,0,0,0,0,0,0,0,
		0,0,0,0,0,0,2,0,0,0,0,0,0,0,0,0,
		0,0,0,0,0,0,0,50,0,0,0,50,0,0,0,0,
		0,0,0,0,0,0,0,26,-9
	};
}
