/*
 * JSynthlib - Constants for Yamaha DX7s
 * =====================================
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
package org.jsynthlib.drivers.yamaha.dx7s;

public class YamahaDX7sPerformanceConstants
{
	// Performance patch/bank numbers
	protected static final String[] SINGLE_PERFORMANCE_PATCH_NUMBERS = {
		"01","02","03","04","05","06","07","08",
		"09","10","11","12","13","14","15","16",
		"17","18","19","20","21","22","23","24",
		"25","26","27","28","29","30","31","32"
	};

	protected static final String[] SINGLE_PERFORMANCE_BANK_NUMBERS = {
		"Internal"
	};

	protected static final String[] BANK_PERFORMANCE_PATCH_NUMBERS = SINGLE_PERFORMANCE_PATCH_NUMBERS;

	protected static final String[] BANK_PERFORMANCE_BANK_NUMBERS = SINGLE_PERFORMANCE_BANK_NUMBERS;

			
	// INIT Performance patch ("INIT PERFORMANCE")
	protected static final byte []INIT_PERFORMANCE = {
		-16,67,0,126,0,61,76,77,32,32,56,57,55,51,80,69,
		1,0,0,0,0,0,0,60,0,3,1,3,0,24,24,50,
		99,0,0,0,1,0,0,99,99,99,99,50,50,50,50,73,
		78,73,84,32,80,69,82,70,79,82,77,65,78,67,69,32,
		32,32,32,83,-9
	};
}
