/*
 * JSynthlib - VoiceConstants for Yamaha DX7 MK-I
 * ==============================================
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
package synthdrivers.YamahaDX7;

class YamahaDX7VoiceConstants
{
	// single patch
	static final String[] SINGLE_VOICE_PATCH_NUMBERS = {
		"01","02","03","04","05","06","07","08",
		"09","10","11","12","13","14","15","16",
		"17","18","19","20","21","22","23","24",
		"25","26","27","28","29","30","31","32"
	};

	static final String[] SINGLE_VOICE_BANK_NUMBERS = {"Internal", "Cartridge"};

	// bank patch
	static final String[] BANK_VOICE_PATCH_NUMBERS = SINGLE_VOICE_PATCH_NUMBERS;
	
	static final String[] BANK_VOICE_BANK_NUMBERS = {"Internal"};

	// init single patch ("INIT VOICE")
	static final byte[] INIT_VOICE = {
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
