/*
 * JSynthlib - generic Byte encoding/decoding for Yamaha DX7 Family
 * ================================================================
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
package synthdrivers.YamahaDX7.common;
import core.*;

public class DX7FamilyByteEncoding
{
	// ################################# ASCII Hex format of TX802 Performance patch <-> Parameter Value ########################
	// 'Value' is split into high and low nibble. Each nibble is encoded as a Hex number, written in ASCII
	// E.g.: value = 0x7F --> ASCII String = 7F --> high nibble = 0x37, low nibble = 0x46
	//
	// Attention! The values 0 - 9 correlate with ASCII value 0x30 - 0x39
	//		  and	 A - F corralate with ASCII value 0x41 - 0x46
	//	      The correlation is made by the arrays ASCII_HEX_2_PARAMETER_VALUE and PARAMETER_VALUE_2_ASCII_HEX
	// ##########################################################################################################################

	// Convertion Table of ASCII Hex Patch Data <-> Parameter value
	protected static final byte [] ASCII_HEX_2_PARAMETER_VALUE = {
		0,1,2,3,4,5,6,7,8,9,10,10,11,12,13,14,15
	};
	
	protected static final byte [] PARAMETER_VALUE_2_ASCII_HEX = {
		0,1,2,3,4,5,6,7,8,9,11,12,13,14,15,16
	};
	
	// Convertion of ASCII HEX to Parameter value 
	protected final static int AsciiHex2Value(int value)
	{ return ( ASCII_HEX_2_PARAMETER_VALUE[Byte.parseByte(Integer.toHexString(value-0x30))]); }

	// Convertion of Parameter value to ASCII HEX - High Nibble
	protected final static int Value2AsciiHexHigh(int value)
	{ return ( Integer.valueOf(Integer.toString(PARAMETER_VALUE_2_ASCII_HEX[value/16  ]),16).intValue()+0x30); }

	// Convertion of Parameter value to ASCII HEX - Low Nibble
	protected final static int Value2AsciiHexLow(int value)
	{ return ( Integer.valueOf(Integer.toString(PARAMETER_VALUE_2_ASCII_HEX[value&0x0F]),16).intValue()+0x30); }
}
