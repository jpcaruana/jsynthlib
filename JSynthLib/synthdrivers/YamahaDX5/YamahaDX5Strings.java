/*
 * JSynthlib - Strings for Yamaha DX5
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
package synthdrivers.YamahaDX5;

import synthdrivers.YamahaDX7.common.DX7FamilyStrings;

public class YamahaDX5Strings extends DX7FamilyStrings
{
	protected static final String INFO_TEXT =
		"This JSynthLib \"Yamaha DX5\" device driver supports the following patch types"+
		" with librarian and editor function:"+
		"\n - Voice"+
		"\n - Performance"+
		"\n\n"+
		"GENERAL INFORMATION"+
		"\n==================="+
		"\n- DEVICE DRIVER"+
		"\nFunctions which are not part of the described patches are not supported by this driver."+
		"\n\n"+
		"At this time only the direction JSynthLib->DX5 is working. If a parameter is changed on"+
		" the DX5 itself, JSynthlib doesn't become aware of this."+
	       "\n\n"+
		"- DEVICE EDITOR"+
		"\nGenerally I use the same labels for each parameter as the front panel display"+
		" of the DX5."+
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
		"\nThis is a free choosable identifier. It might be useful if you have several DX5 devices"+
		"\n\n"+
		"- \"Disable Memory Protection?\""+
		"\nThe \"Disable Memory Protection?\" function will switch off the memory protection."+
		"\n\n"+
		"- \"Display Hints and Tips?\""+
		"\nIf you are familiar with the DX5 you can switch off the message windows by disabling"+
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
		"\nBut only those parameters are displayed in the editor, which are evaluated by the DX5.";



	// DX5 information messages
	// ========================
	
	protected final static String SELECT_PATCH_STRING =
		"\n\nCurrently it's not possible to select a singular Voice, Voice Bank and Performance"+
		"\nfor sending/storing/requesting!"+
		"\nA patch is send/store to the edit buffer resp. the current patch at the DX5 is requested."+
		"\n\nIt's also not possible yet to disable Memory Protection or enable SysEx communication!"+
		"\n\nPlease prepare your DX5 to receive/transmit patches and choose the desired single"+
		"\nPatch or Voice Bank manually"+
		AVOID_MSG_STRING;
}
