/*
 * JSynthlib - Micro Tuning Constants for Yamaha TX802
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
package org.jsynthlib.drivers.yamaha.tx802;

public class YamahaTX802MicroTuningConstants
{
	// Micro Tuning patch/bank numbers of single/bank driver
	protected static final String[] SINGLE_MICRO_TUNING_PATCH_NUMBERS = {
		"Edit Buffer",
		"User 1",
		"User 2"
	};
	
	protected static final String[] SINGLE_MICRO_TUNING_BANK_NUMBERS = {
		"Internal"
	};

	protected static final String[] BANK_MICRO_TUNING_PATCH_NUMBERS = {
		"CRT  1","CRT  2","CRT  3","CRT  4","CRT  5","CRT  6","CRT  7","CRT  8",
		"CRT  9","CRT 10","CRT 11","CRT 12","CRT 13","CRT 14","CRT 15","CRT 16",
		"CRT 17","CRT 18","CRT 19","CRT 20","CRT 21","CRT 22","CRT 23","CRT 24",
		"CRT 25","CRT 26","CRT 27","CRT 28","CRT 29","CRT 30","CRT 31","CRT 32",
		"CRT 33","CRT 34","CRT 35","CRT 36","CRT 37","CRT 38","CRT 39","CRT 40",
		"CRT 41","CRT 42","CRT 43","CRT 44","CRT 45","CRT 46","CRT 47","CRT 48",
		"CRT 49","CRT 50","CRT 51","CRT 52","CRT 53","CRT 54","CRT 55","CRT 56",
		"CRT 57","CRT 58","CRT 59","CRT 60","CRT 61","CRT 62","CRT 63"
	};
 
	protected static final String[] BANK_MICRO_TUNING_BANK_NUMBERS = {
		"Cartridge"
	};


	// Init Micro Tuning patch (no patchname)
	protected static final byte []INIT_MICRO_TUNING = {
		-16,67,0,126,2,10,76,77,32,32,77,67,82,89,77,0,
		42,0,42,0,42,0,42,0,42,0,42,0,42,0,42,0,
		42,0,42,0,42,0,42,0,42,0,42,0,42,0,42,0,
		42,0,42,0,42,0,42,0,42,0,42,0,42,0,42,0,
		42,0,42,0,42,0,42,0,42,0,42,0,42,0,42,0,
		42,0,42,0,42,0,42,0,42,0,42,0,42,0,42,0,
		42,0,42,0,42,0,42,0,42,0,42,0,42,0,42,0,
		42,0,42,0,42,0,42,0,42,0,42,0,42,0,42,0,
		42,0,42,0,42,0,42,0,42,0,42,0,42,0,42,0,
		42,0,42,0,42,0,42,0,42,0,42,0,42,0,42,0,
		42,0,42,0,42,0,42,0,42,0,42,0,42,0,42,0,
		42,0,42,0,42,0,42,0,42,0,42,0,42,0,42,0,
		42,0,42,0,42,0,42,0,42,0,42,0,42,0,42,0,
		42,0,42,0,42,0,42,0,42,0,42,0,42,0,42,0,
		42,0,42,0,42,0,42,0,42,0,42,0,42,0,42,0,
		42,0,42,0,42,0,42,0,42,0,42,0,42,0,42,0,
		42,0,42,0,42,0,42,0,42,0,42,0,42,0,42,0,
		31,-9
	};
}
