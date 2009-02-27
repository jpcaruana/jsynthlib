/*
 * JSynthlib - Strings for Yamaha DX7 Mark-I
 * =========================================
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
package org.jsynthlib.drivers.yamaha.dx7;
import org.jsynthlib.drivers.yamaha.dx7.common.DX7FamilyStrings;

class YamahaDX7Strings extends DX7FamilyStrings
{
	protected static final String INFO_TEXT =
		"This JSynthLib \"Yamaha DX7 Mark-I\" device driver supports the following patch types"+
		" with librarian and editor function:"+
		"\n - Voice"+
		"\n - Performance (=Function Parameter)"+
		"\n\n"+
		"GENERAL INFORMATION"+
		"\n==================="+
		"\n- DX7 MARK-I"+
		"\nBecause of the MIDI implementation of the system ROM of the early units"+
		" (especially the ActiveSensing- and the SysEx-handling), problems will occur with"+
		" an early DX7 in cooperation with patch libraries/editors."+
		"\nIt's advisable to upgrade early system ROMs to a newer version."+
		"\n\n"+
		"- DEVICE DRIVER"+
		"\nThis driver is tested with a DX7 in an original state (system ROM V1.8 from"+
		" October 24th 1985). Even so there might be some bugs."+
		"\nSince this driver uses special methods you should choose this driver only if you have a DX7 Mark-I."+
		" These special methods concern the \"Remote Control\" function, the \"Parameter Change\" MIDI messages"+
		" of the performance editor and others."+
		"\n\n"+
		"I made the experience that you can send a single performance patch to the DX7"+
		" function edit buffer. At least to a DX7 with the described System ROM."+
		"\n\n"+
		"At this time only the direction JSynthLib->DX7 is working. If a parameter is changed on"+
		" the DX7 itself, JSynthlib doesn't become aware of this."+
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
		"\nThis is a free choosable identifier. It might be useful if you have several DX7 Mark-I devices"+
		"\n\n"+
		"- \"Enable Remote Control?\""+
		"\nThe MIDI specification of the DX7 doesn't support requesting of a patch. As an"+
		" alternative this driver can simulate the necessary panel button pushes, if you"+
		" switch on the \"Enable Remote Control\" function."+
		"\nThe \"Enable Remote Control\" function makes the SysInfo available, chooses the"+
		" desired patch number, etc."+
		"\n\n"+
		"- \"Disable Memory Protection?\""+
		"\nThe \"Disable Memory Protection?\" function will switch off the memory protection."+
		"\n\n"+
		"- \"Display Hints and Tips?\""+
		"\nIf you are familiar with the DX7 you can switch off the message windows by disabling"+
		" the \"Display Hints and Tips?\" function. This will avoid all messages!"+
		"\n\n"+
		"PATCH TYPE SPECIFIC INFORMATION"+
		"\n==============================="+
		"\n\"VOICE\" SINGLE/BANK DRIVER/EDITOR"+
		"\nThe underlying patch is identical at least for all members of the \"DX7 family\""+
		" (DX1, DX5, DX7, TX7, TX816, DX7s, DX7-II and TX802)."+
		"\n\n"+
		"This driver also supports the use of cartridges with the DX7 as far as possible."+
		" Of course, you have to use the switches of the cartridges themselves to choose bank A"+
		" or bank B of a \"DX7 VOICE ROM\" cartridge or to switch on/off the memory protection of"+
		" \"RAM1\" cartridges. Further, the storing of a voice bank to a cartridge \"RAM1\" isn't"+
		" supported!"+
		"\n\n"+
		"Only those parameters are implemented in the editor, which are stored in the patch."+
		"\nSo, you won't find any function parameter like pitchband, portamento, etc."+
		" These are part of the performance driver/editor."+
		"\nThere is only one exception: the OPERATOR ON/OFF buttons, because they are"+
		" useful for programming."+
		"\n\n"+
		"\"PERFORMANCE\" SINGLE DRIVER/EDITOR"+
		"\nThe underlying patch is identical for the DX1, DX5, DX7, TX7 and TX816."+
		"\nBut only those parameters are displayed in the editor, which are evaluated by the DX7."+
		"\n\n"+
		"The DX7 supports only the receiving of a single performance patch; no transmitting,"+
		" or requesting.";


	// DX7 information messages
	// ========================
	protected final static String RECEIVE_STRING=
		"\n\nHave you prepared your DX7 to receive a patch?"+
		"\n(Memory Protect Off & Sys Info Available)"+
		AUTOMATION_MSG_STRING + AVOID_MSG_STRING;

	protected final static String STORE_SINGLE_VOICE_STRING =
		"\n\nThe patch has been placed in the edit buffer of your DX7!"+
		"\n\nYou must now hold the \"STORE\" button on the DX7 and choose a location"+
		"\n(1-32) to store the patch."+
		AUTOMATION_MSG_STRING + AVOID_MSG_STRING;

	protected final static String REQUEST_VOICE_STRING =
		"\n\nPlease start the patch dump manually, when you have prepared your DX7"+
		"\nto send a patch! (Sys Info Available)"+
		RC_MSG_STRING + AVOID_MSG_STRING;

	protected final static String STORE_SINGLE_PERFORMANCE_STRING =
		"\n\nThe DX7 doesn't support storing of a single performance patch."+
		"\n\nThe patch has been placed in the edit buffer!"+
		AVOID_MSG_STRING;

	protected final static String PERFORMANCE_STRING =
		"\n\nThe DX7 doesn't support the requesting and transmitting of a"+
		"\nsingle or bank performance patch!"+
		"\n\nOnly the DX7 with Firmware V1.8  supports the receiving of a"+
		"\nsingle performance patch and \"parameter change\" commands."+
		AVOID_MSG_STRING;

	protected final static String PERFORMANCE_EDITOR_STRING =
		"\n\nHave you prepared your DX7 to receive the \"parameter change\" commands"+
		"\nfor the singular performance parameters? (Sys Info Available)"+
		RC_MSG_STRING + AVOID_MSG_STRING;
}
