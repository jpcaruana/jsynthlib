/*
 * JSynthlib - Fractional Scaling Constants for Yamaha DX7-II
 * ==========================================================
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
package org.jsynthlib.drivers.yamaha.dxii;

public class YamahaDX7IIFractionalScalingConstants
{
	// Fractional Scaling patch/bank numbers
	protected static final String[] SINGLE_FRACTIONAL_SCALING_PATCH_NUMBERS = {
		"01/33","02/34","03/35","04/36","05/37","06/38","07/39","08/40",
		"09/41","10/42","11/43","12/44","13/45","14/46","15/47","16/48",
		"17/49","18/50","19/51","20/52","21/53","22/54","23/55","24/56",
		"25/57","26/58","27/59","28/60","29/61","30/62","31/63","32/64"
	};

	protected static final String[] SINGLE_FRACTIONAL_SCALING_BANK_NUMBERS	= {
		"Cartridge (1-32)",
		"Cartridge (33-64)"
	};

	protected static final String[] BANK_FRACTIONAL_SCALING_PATCH_NUMBERS = SINGLE_FRACTIONAL_SCALING_PATCH_NUMBERS;

	protected static final String[] BANK_FRACTIONAL_SCALING_BANK_NUMBERS  = SINGLE_FRACTIONAL_SCALING_BANK_NUMBERS;

	// Init Fractional Scaling patch (no patchname)
	protected static final byte []INIT_FRACTIONAL_SCALING = {
		-16,67,0,126,3,118,76,77,32,32,70,75,83,89,69,32,
		48,48,63,63,63,50,62,50,61,52,60,52,59,52,58,54,
		57,54,56,56,55,56,54,56,53,58,52,58,51,58,50,60,
		49,60,48,62,48,62,48,62,48,62,48,62,48,62,48,62,
		48,62,48,62,48,62,48,62,48,62,48,62,48,62,48,62,
		48,62,48,62,48,62,48,62,48,62,48,62,48,62,48,62,
		48,62,48,48,48,48,48,48,48,48,48,48,48,48,48,48,
		48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,
		48,48,48,48,48,48,48,50,48,50,48,50,48,50,48,52,
		48,52,48,54,48,54,48,56,48,58,48,60,48,62,49,48,
		49,50,49,50,49,50,49,50,49,50,49,50,49,50,49,50,
		49,50,49,50,48,48,56,56,56,50,55,58,55,52,54,62,
		54,54,54,48,53,56,53,50,52,58,52,52,51,60,51,54,
		51,48,50,56,50,50,49,58,49,52,48,60,48,54,48,48,
		48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,
		48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,
		48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,
		48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,
		48,48,48,48,48,48,48,50,48,50,48,50,48,50,48,52,
		48,52,48,54,48,54,48,56,48,58,48,60,48,62,49,50,
		49,54,49,58,49,60,49,60,49,60,49,60,49,60,49,60,
		49,60,49,60,49,60,49,60,48,48,54,50,54,48,53,62,
		53,60,53,58,53,56,53,52,53,50,53,48,52,62,52,60,
		52,58,52,56,52,54,52,50,52,48,51,62,51,60,51,58,
		51,56,51,54,51,52,51,48,50,62,50,60,50,58,50,56,
		50,54,50,52,50,50,50,50,50,50,50,50,50,50,50,50,
		50,50,50,50,50,50,50,50,50,50,48,48,48,48,48,48,
		48,48,48,48,48,48,48,48,48,50,48,50,48,50,48,52,
		48,52,48,52,48,54,48,54,48,54,48,56,48,56,48,56,
		48,58,48,58,48,58,48,58,48,60,48,60,48,60,48,62,
		48,62,48,62,49,48,49,48,49,48,49,50,49,50,49,50,
		49,50,49,50,49,50,49,50,49,50,49,50,34,-9
	};

}
