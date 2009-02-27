/*
 * JSynthlib - Performance Constants for Yamaha TX802
 * ==================================================
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

public class YamahaTX802PerformanceConstants
{
	// Performance patch/bank numbers
	protected static final String[] SINGLE_PERFORMANCE_PATCH_NUMBERS = {
		"01","02","03","04","05","06","07","08",
		"09","10","11","12","13","14","15","16",
		"17","18","19","20","21","22","23","24",
		"25","26","27","28","29","30","31","32",
		"33","34","35","36","37","38","39","40",
		"41","42","43","44","45","46","47","48",
		"49","50","51","52","53","54","55","56",
		"57","58","59","60","61","62","63","64"
	};

	protected static final String[] SINGLE_PERFORMANCE_BANK_NUMBERS = {
		"Internal"
	};

	protected static final String[] BANK_PERFORMANCE_PATCH_NUMBERS = SINGLE_PERFORMANCE_PATCH_NUMBERS;
	
	protected static final String[] BANK_PERFORMANCE_BANK_NUMBERS = SINGLE_PERFORMANCE_BANK_NUMBERS;

	// INIT Performance patch ("INIT PERFORMANCE")
	protected static final byte []INIT_PERFORMANCE = {
		-16,67,0,126,1,104,76,77,32,32,56,57,53,50,80,69,
		48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,
		48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,
		48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,
		48,55,48,55,48,55,48,55,48,55,48,55,48,55,48,55,
		53,65,53,65,53,65,53,65,53,65,53,65,53,65,53,65,
		48,51,48,51,48,51,48,51,48,51,48,51,48,51,48,51,
		48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,
		55,70,55,70,55,70,55,70,55,70,55,70,55,70,55,70,
		49,56,49,56,49,56,49,56,49,56,49,56,49,56,49,56,
		48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,
		48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,
		48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,
		52,57,52,69,52,57,53,52,50,48,53,48,52,53,53,50,
		52,54,52,70,53,50,52,68,52,49,52,69,52,51,52,53,
		50,48,50,48,50,48,50,48,62,-9
	};
}
