/*
 * JSynthlib - Strings for Yamaha DX7-II
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
package synthdrivers.YamahaDX7II;
import	synthdrivers.YamahaDX7.common.DX7FamilyStrings;

public class YamahaDX7IIStrings extends DX7FamilyStrings
{
	protected static final String INFO_TEXT =
		"This JSynthLib \"Yamaha DX7-II\" device driver supports the following patch types"+
		" with librarian and editor function:"+
		"\n - Voice"+
		"\n - Additional Voice"+
		"\n - Micro Tuning"+
		"\n - Fractional Scaling"+
		"\n - Performance"+
		"\n - System Setup"+
		"\n\n"+
		"GENERAL INFORMATION"+
		"\n==================="+
		"\n"+
		"- DEVICE DRIVER"+
		"\nFunctions which are not part of the described patches, are not supported by this driver."+
		"\nThese are at least:"+
		"\n- Master Tune"+
		"\n\n"+
		"At this time only the direction JSynthLib->DX7-II is working. If a parameter is changed on"+
		" the DX7-II itself, JSynthlib doesn't become aware of this."+
	       "\n\n"+
		"- DEVICE EDITOR"+
		"\nGenerally I use the same labels for each parameter as the front panel display"+
		" of the DX7-II."+
		"\nI tried to extract the necessary information from the manual."+
		"\nBut surely this attempt wasn't succesful in all parameters, since I don't own such a device."+
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
		"\nThis is a free choosable identifier. It might be useful if you have several DX7-II devices"+
		"\n\n"+
		"- \"Disable Memory Protection?\""+
		"\nThe \"Disable Memory Protection?\" function will switch off the memory protection."+
		"\n\n"+
		"- \"Display Hints and Tips?\""+
		"\nIf you are familiar with the DX7-II you can switch off the message windows by disabling"+
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
		" These are part of the additional voice driver/editor."+
		"\nThere is only one exception: the OPERATOR ON/OFF buttons, because they are"+
		" useful for programming."+
		"\n\n"+
		"\"ADDITIONAL VOICE\" SINGLE/BANK DRIVER/EDITOR"+
		"\nThe underlying patch is identical for the DX7s, DX7-II and TX802."+
		"\nBut only those parameters are displayed in the editor, which are evaluated by the DX7-II."+
		"\n\n"+
		//"\nIf you choose a certain voice patch, then you automatically get the corresponding"+
		//" additional voice patch"+
		//"\n\n"+
		"\"MICRO TUNING\" SINGLE/BANK DRIVER/EDITOR"+
		"\nThe underlying patch is identical for the DX7s, DX7-II and TX802."+
		"\nBut only those parameters are displayed in the editor, which are evaluated by the DX7-II."+
		"\n\n"+
		"Micro tuning bank patches are only stored in cartridges!"+
		"\nSo you have to insert an appropriate cartridge into your device to receive/transmit"+
		" this kind of bank patches. Also it's not possible to receive/transmit single"+
		" micro tuning patches directly from a cartridge."+
		"\n\n"+
		"\"FRACTIONAL SCALING\" SINGLE/BANK DRIVER/EDITOR"+
		"\nThe underlying patch is identical for the DX7s, DX7-II and TX802."+
		"\nBut only those parameters are displayed in the editor, which are evaluated by the DX7-II."+
		"\n\n"+
		"Fractional scaling patches are only stored in cartridges!"+
		"\nSo you have to insert an appropriate cartridge into your device to receive/transmit"+
		" this kind of patches."+
		//"\nIf you want to use fractional scaling and you choose a certain voice patch, then you"+
		//" automatically get the corresponding fractional scaling patch"+
		"\n\n"+
		"\"PERFORMANCE\" DRIVER/EDITOR"+
		"\nThe underlying patch is identical for the DX7s and DX7-II."+
		"\nBut only those parameters are displayed in the editor, which are evaluated by the DX7-II."+
		"\n\n"+
		"\"SYSTEM SETUP\" DRIVER/EDITOR"+
		"\nThe underlying patch is identical for the DX7s and DX7-II."+
		"\nBut only those parameters are displayed in the editor, which are evaluated by the DX7-II.";


	// DX7-II information messages
	// ===========================

	// voice patch
	protected final static String STORE_SINGLE_VOICE_STRING =
		"\n\nThis driver doesn't support storing of a single voice patch for the"+
		"\nDX7-II, but you can do it manually on the device."+
		"\n\nThe patch has been placed in the edit buffer!"+
		AVOID_MSG_STRING;

	// additional voice patch
	protected final static String STORE_SINGLE_ADDITIONAL_VOICE_STRING =
		"\n\nThis driver doesn't support storing of a single additional voice patch for the"+
		"\nDX7-II, but you can do it manually on the device."+
		"\n\nThe patch has been placed in the edit buffer!"+
		AVOID_MSG_STRING;

	// micro tuning patch
	protected final static String MICRO_TUNING_CARTRIDGE_STRING =
		"\n\nHave you prepared your DX7-II to receive/transmit a micro tuning"+
		"\nbank patch (micro tuning formated cartridge inserted) ?"+
		AVOID_MSG_STRING;

	// fractional scaling patch
	protected final static String FRACTIONAL_SCALING_CARTRIDGE_STRING =
		"\n\nHave you prepared your DX7-II to receive/transmit a fractional scaling"+
		"\nbank patch (fractional scaling formated cartridge inserted) ?"+
		AVOID_MSG_STRING;

	protected final static String STORE_SINGLE_FRACTIONAL_SCALING_STRING =
		"\n\nThis driver doesn't support storing of a single fractional scaling patch for the"+
		"\nDX7-II, but you can do it manually on the device."+
		"\n\nThe patch has been placed in the edit buffer!"+
		AVOID_MSG_STRING;

	// performance patches commonly
	protected final static String STORE_SINGLE_PERFORMANCE_STRING =
		"\n\nThis driver doesn't support storing of a single performance patch for the"+
		"\nDX7-II, but you can do it manually on the device."+
		"\n\nThe patch has been placed in the edit buffer!"+
		AVOID_MSG_STRING;

}
