/*
 * JSynthlib - System Setup Constants for Yamaha TX802
 * ===================================================
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

public class YamahaTX802SystemSetupConstants
{
	// System Setup patch/bank numbers
	protected static final String[] SINGLE_SYSTEM_SETUP_PATCH_NUMBERS = {
		"System Setup"
	};
	
	protected static final String[] SINGLE_SYSTEM_SETUP_BANK_NUMBERS = {
		"Internal"
	};


	// Init System Setup patch (no patchname)
	protected static final byte [] INIT_SYSTEM_SETUP = {
		-16,67,0,126,2,17,76,77,32,32,56,57,53,50,83,32,
		0,17,0,1,1,1,0,0,0,2,3,64,1,0,1,2,
		0,4,5,0,7,0,0,0,0,0,0,0,0,0,0,0,
		0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
		0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
		0,0,0,0,0,0,0,0,0,0,0,0,0,64,65,0,
		0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
		0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
		0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
		0,0,0,0,0,0,0,0,1,2,3,4,5,6,7,8,
		9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,
		25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,
		41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,
		57,58,59,60,61,62,63,64,65,66,67,68,69,70,71,72,
		73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,
		89,90,91,92,93,94,95,96,97,98,99,100,101,102,103,104,
		105,106,107,108,109,110,111,112,113,114,115,116,117,118,119,120,
		121,122,123,124,125,126,127,121,-9
	};
}
