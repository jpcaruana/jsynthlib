/*
 * Copyright 2005 Joachim Backhaus
 *
 * This file is part of JSynthLib.
 *
 * JSynthLib is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
 *
 * JSynthLib is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JSynthLib; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 */

package synthdrivers.WaldorfMW2;

/**
 * Constants for the Microwave 2 / XT / XTK synthdriver
 *
 * @author  Joachim Backhaus
 * @version $Id$
 */
public class MW2Constants {
	public static final byte SYSEX_START_BYTE			= (byte) 0xF0;
	public static final byte SYSEX_END_BYTE				= (byte) 0xF7;
	public static final int  SYSEX_HEADER_OFFSET		= 8;

	public static final int PATCH_SIZE			= 265;
	public static final int PATCH_NAME_SIZE		= 16;
	public static final int PATCH_NAME_START	= 247; // 240 + 7

	public static final int DEVICE_ID_OFFSET	= 3;

	// Sound Mode Edit Buffer: 20 00

	// Sysex ID: F0 3E 0E .. 00 .. .. XSUM F7
	public static final String SYSEX_ID = "F03E0E**";
	
	public static final String DEFAULT_SYSEX_FILENAME = "mw2_default.syx";
}
