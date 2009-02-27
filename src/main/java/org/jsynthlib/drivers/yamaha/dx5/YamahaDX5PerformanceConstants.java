/*
 * JSynthlib - Performance Constants for Yamaha DX5
 * ================================================
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
package org.jsynthlib.drivers.yamaha.dx5;

class YamahaDX5PerformanceConstants
{
	// DX5 performance patch/bank numbers
	static final String[] SINGLE_PERFORMANCE_PATCH_NUMBERS = {
		"01","02","03","04","05","06","07","08",
		"09","10","11","12","13","14","15","16",
		"17","18","19","20","21","22","23","24",
		"25","26","27","28","29","30","31","32",
		"33","34","35","36","37","38","39","40",
		"41","42","43","44","45","46","47","48",
		"49","50","51","52","53","54","55","56",
		"57","58","59","60","61","62","63","64"
	};

	static final String[] SINGLE_PERFORMANCE_BANK_NUMBERS = {"Internal"};


	// DX5 performance patch/bank numbers
	static final String[] BANK_PERFORMANCE_PATCH_NUMBERS = SINGLE_PERFORMANCE_PATCH_NUMBERS;
	static final String[] BANK_PERFORMANCE_BANK_NUMBERS  = SINGLE_PERFORMANCE_BANK_NUMBERS;

	
	// Init TX7 Single Performance patch (" YAMAHA TX7 FUNCTION DATA  ")
	static final byte [] INIT_PERFORMANCE = {
		-16,67,0,1,0,94,0,0,0,7,0,0,0,0,1,8,
		1,8,0,8,0,15,0,0,0,0,0,0,0,0,99,99,
		7,0,1,24,0,0,0,7,0,0,0,0,1,8,1,8,
		0,8,0,15,0,0,0,0,0,0,0,0,99,99,7,0,
		1,24,0,0,0,39,32,89,65,77,65,72,65,32,32,84,
		88,55,32,32,70,85,78,67,84,73,79,78,32,32,68,65,
		84,65,32,32,121,-9
	};
}
