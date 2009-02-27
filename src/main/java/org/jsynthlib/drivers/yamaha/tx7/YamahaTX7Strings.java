/*
 * JSynthlib - Strings for Yamaha TX7
 * ==================================
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
package org.jsynthlib.drivers.yamaha.tx7;

import org.jsynthlib.drivers.yamaha.dx7.common.DX7FamilyStrings;

public class YamahaTX7Strings extends DX7FamilyStrings
{
	protected static final String INFO_TEXT =
		"This JSynthLib \"Yamaha TX7\" device driver supports the following patch types"+
		" with librarian and editor function:"+
		"\n - Voice"+
		"\n - Performance"+
		"\n\n"+
		"GENERAL INFORMATION"+
		"\n==================="+
		"\n- DEVICE DRIVER"+
		"\nFunctions which are not part of the described patches are not supported by this driver."+
		"\nThese are at least:"+
		"\n- Master Tune"+
		"\n- Limit Key Lowest"+
		"\n- Limit Key Highest"+
		"\n- MIDI mode functions"+
		"\n- Cassette operations"+
		"\n\n"+
		"At this time only the direction JSynthLib->TX7 is working. If a parameter is changed on"+
		" the TX7 itself, JSynthlib doesn't become aware of this."+
	       "\n\n"+
		"- DEVICE EDITOR"+
		"\nGenerally I use the same labels for each parameter as the front panel display"+
		" of the TX7."+
		"\nI tried to extract the necessary information from the manual."+
		"\nBut surely this attempt wasn't successful in all parameters, since I don't own such a device."+
		"\n\n"+
		"- REPORTING BUGS"+
                "\nReport bugs to <Torsten.Tittmann@gmx.de>."+
                "\nAny feedback about bugs are welcome."+
		"\nAlso comments and proposals of improvements will be helpful to improve this driver."+
		"\n\n"+
		"- TODO"+
		"\nTo extend my driver family to the models DX1 and DX5 I'm looking for the MIDI specification of these"+
		" models. The access of the two internal \"DX7\"s as well as the complete specification of the"+
		" performance patch are unknown."+
		"\n\n"+
		"To extend my driver family to the model TX216/TX816 in a satisfying manner some major changes are"+
		" necessary for the JSynthLib architecture. But since the TF1 modul of the TX216/TX816 is compatible"+
		" with the TX7, a base support is given."+
		"\n\n"+
		"DEVICE CONFIGURATION"+
		"\n===================="+
		"\nYou will find some settings in the device configuration to suite the device driver"+
		" to your preferences:"+
		"\n\n"+
		"- \"Synthesizer Name\""+
		"\nThis is a free choosable identifier. It might be useful if you have several TX7 devices"+
		"\n\n"+
		"- \"Disable Memory Protection?\""+
		"\nThe \"Disable Memory Protection?\" function will switch off the memory protection."+
		"\n\n"+
		"- \"Display Hints and Tips?\""+
		"\nIf you are familiar with the TX7 you can switch off the message windows by disabling"+
		" the \"Display Hints and Tips?\" function. This will avoid all messages!"+
		"\n\n"+
		"PATCH TYPE SPECIFIC INFORMATION"+
		"\n==============================="+
		"\n\"VOICE\" SINGLE/BANK DRIVER/EDITOR"+
		"\nThe underlying patch is identical at least for all members of the \"DX7 family\""+
		" (DX1, DX5, DX7, TX7, TX816, DX7s, DX7-II and TX802)."+
		"\n\n"+
		"Only those parameters are implemented in the editor, which are stored in the patch."+
		"\nSo, you won't find any function parameter like pitchband, portamento, etc."+
		" These are part of the performance driver/editor."+
		"\nThere is only one exception: the OPERATOR ON/OFF buttons, because they are"+
		" useful for programming."+
		"\n\n"+
		"\"PERFORMANCE\" SINGLE/BANK DRIVER/EDITOR"+
		"\nThe underlying patch is identical for the DX1, DX5, DX7, TX7 and TX816."+
		"\nBut only those parameters are displayed in the editor, which are evaluated by the TX7."+
		"\n\n"+
		"The TX7 uses only 32 single performance patches of a performance bank patch"+
		" by itself. The remaining single performance patches of the bank are used by"+
		" an additional DX7 (32 voices) or DX9 (20 voices)";



	// TX7 information messages
	// ========================
	protected final static String STORE_SINGLE_VOICE_STRING =
		"\n\nThe TX7 doesn't support storing of a single voice patch."+
		"\nYou have to create a bank with all desired voices and transmit this bank"+
		"\nto the TX7."+
		"\n\nThe patch has been placed in the edit buffer!"+
		AVOID_MSG_STRING;

	protected final static String EDIT_BANK_PERFORMANCE_STRING =
		"\n\nThe TX7 uses only 32 single performance patches by itself."+
		"\nThe remaining single performance patches of the bank are used by"+
		"\nan additional DX7 (32 voices) or DX9 (20 voices)"+
		AVOID_MSG_STRING;

	protected final static String STORE_SINGLE_PERFORMANCE_STRING =
		"\n\nThe TX7 doesn't support storing of a single performance patch."+
		"\nYou have to create a bank with all desired performance patches and"+
		"\ntransmit this bank to the TX7."+
		"\n\nThe patch has been placed in the edit buffer!"+
		AVOID_MSG_STRING;
}
