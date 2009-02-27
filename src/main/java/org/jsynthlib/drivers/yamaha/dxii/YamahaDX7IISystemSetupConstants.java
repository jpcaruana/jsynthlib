/*
 * JSynthlib - System Setup Constants for Yamaha DX7-II
 * ====================================================
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

public class YamahaDX7IISystemSetupConstants
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
		-16,67,0,126,0,95,76,77,32,32,56,57,55,51,83,32,
		0,1,0,0,1,11,12,13,14,0,1,1,0,0,0,1,
		0,2,3,3,64,0,1,2,3,4,5,6,7,8,9,10,
		11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,
		27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,
		43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,
		59,60,61,62,63,122,-9
	};

}
