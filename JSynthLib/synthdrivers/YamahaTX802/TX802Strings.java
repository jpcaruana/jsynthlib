/*
 * JSynthlib - Strings for Yamaha TX802
 * ====================================
 * @author  Torsten Tittmann
 * file:    TX802Strings.java
 * date:    25.02.2003
 * @version 0.1
 *
 * Copyright (C) 2002-2003  Torsten.Tittmann@t-online.de
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
 *
 * history:
 *         25.02.2003 v0.1: first published release
 *
 */
package synthdrivers.YamahaTX802;
public class TX802Strings
{
  protected static final String INFO_TEXT =
                "This JSynthLib \"Yamaha TX802\" device driver supports the following patch types"+
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
                "\n- DEVICE DRIVER"+
		"\nBecause of partly missing MIDI specifications the drivers of this device have some reductions."+
		" Please see the \"Patch Type\"-related part below."+
                "\n\n"+
		"At this time only the direction JSynthLib->TX802 is working. If a parameter is changed on"+
                " the TX802 itself, JSynthlib doesn't become aware of this."+
               "\n\n"+
                "- DEVICE EDITOR"+
                "\nGenerally I use the same labels for each parameter as the front panel display"+
                " of the TX802."+
                "\nI tried to extract the necessary information from the manual."+
                "\nBut surely this attempt wasn't successful in all parameters, since I don't own such a device."+
                "\n\n"+
                "- REPORTING BUGS"+
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
                "\nThis is a free choosable identifier. It might be useful if you have several TX802 devices"+
                "\n\n"+
                "- \"Disable Memory Protection?\""+
                "\nThe \"Disable Memory Protection?\" function will switch off the memory protection."+
                "\n\n"+
                "- \"Display Hints and Tips?\""+
                "\nIf you are familiar with the TX802 you can switch off the message windows by disabling"+
                " the \"Display Hints and Tips?\" function. This will avoid all messages!"+
                "\n\n"+
                "- \"use Spinner Elements?\""+
                "\nSince the spinner graphic element needs at least JDK/JRE 1.4 some editors are existing"+
                " in two versions (with and without spinner elements)."+
                "\nBoth versions have the same functionality, but different layouts."+
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
                "\nBut only those parameters are displayed in the editor, which are evaluated by the TX802."+
                "\n\n"+
                //"\nIf you choose a certain voice patch, then you automatically get the corresponding"+
                //" additional voice patch"+
                //"\n\n"+
                "\"MICRO TUNING\" SINGLE/BANK DRIVER/EDITOR"+
                "\nThe underlying patch is identical for the DX7s, DX7-II and TX802."+
                "\nBut only those parameters are displayed in the editor, which are evaluated by the TX802."+
                "\n\n"+
		"Micro tuning bank patches are only stored in cartridges!"+
                "\nSo you have to insert an appropriate cartridge into your device to receive/transmit"+
                " this kind of bank patches. Also it's not possible to receive/transmit single"+
                " micro tuning patches directly from a cartridge."+
                "\n\n"+
                "\"FRACTIONAL SCALING\" SINGLE/BANK DRIVER/EDITOR"+
                "\nThe underlying patch is identical for the DX7s, DX7-II and TX802."+
                "\nBut only those parameters are displayed in the editor, which are evaluated by the TX802."+
                "\n\n"+
		"Fractional scaling patches are only stored in cartridges!"+
                "\nSo you have to insert an appropriate cartridge into your device to receive/transmit"+
                " this kind of patches."+
                //"\nIf you want to use fractional scaling and you choose a certain voice patch, then you"+
                //" automatically get the corresponding fractional scaling patch"+
                "\n\n"+
		"\"PERFORMANCE\" SINGLE/BANK DRIVER/EDITOR"+
		"\nThe performance editor has some reductions:"+
		"\n - I've guessed the values of the \"Micro Tuning Table\" and so these are probably faulty."+
		"\n - the sending of parameter change messages of the \"Voice Number\" and of the \"Micro Tuning Table\""+
		" is disabled, because I don't know the format yet."+
		"\n\n"+
		"\"SYSTEM SETUP\" DRIVER/EDITOR"+
		"\nFor the lack of further information only two functions of the editor are able to"+
		" send parameter change messages!"+
                "\nThese are \"Master Tune\" and \"Voice Data Receive Block\".";


  // =====================================================================================================================
  // Message Strings
  // =====================================================================================================================

  protected final static String MEMORY_PROTECTION_MSG_STRING =
		"\n\n(You can automate this if you enable the \"Disable Memory Protection?\""+
                "\nfunction in the device config panel)";

  protected final static String AVOID_MSG_STRING =
                "\n\n(You can avoid this message generally if you disable the \"Display"+
                "\nHints and Tips?\" function in the device config panel)";

  protected final static String MEMORY_PROTECTION_STRING=
                "\n\nHave you disabled the memory protection of your TX802?"+
                MEMORY_PROTECTION_MSG_STRING+AVOID_MSG_STRING;


  // JAVA environment messages
  // =========================

  // JDK 1.4 or higher needed (JSpinner, ...)
  protected final static String JDK14_NEEDED_STRING =
		"\n\nThe editor with spinner element requires JDK/JRE 1.4 or higher!"+
		"\n\nAn alternative editor will be used."+
                AVOID_MSG_STRING;

  // information messages
  // ====================

  // voice patch
  protected final static String STORE_SINGLE_VOICE_STRING =
                "\n\nThis driver doesn't support storing of a single voice patch for the"+
                "\nTX802, but you can do it manually on the device."+
                "\n\nThe patch has been placed in the edit buffer!"+
                AVOID_MSG_STRING;

  // additional voice patch
  protected final static String STORE_SINGLE_ADDITIONAL_VOICE_STRING =
                "\n\nThis driver doesn't support storing of a single additional voice patch for the"+
                "\nTX802, but you can do it manually on the device."+
                "\n\nThe patch has been placed in the edit buffer!"+
                AVOID_MSG_STRING;

  // micro tuning patch
  protected final static String MICRO_TUNING_CARTRIDGE_STRING =
                "\n\nHave you prepared your TX802 to receive/transmit a micro tuning"+
                "\nbank patch (micro tuning formated cartridge inserted) ?"+
                AVOID_MSG_STRING;

  // fractional scaling patch
  protected final static String FRACTIONAL_SCALING_CARTRIDGE_STRING =
                "\n\nHave you prepared your TX802 to receive/transmit a fractional scaling"+
		"\nbank patch (fractional scaling formated cartridge inserted) ?"+
                AVOID_MSG_STRING;

  protected final static String STORE_SINGLE_FRACTIONAL_SCALING_STRING =
                "\n\nThis driver doesn't support storing of a single fractional scaling patch for the"+
                "\nTX802, but you can do it manually on the device."+
                "\n\nThe patch has been placed in the edit buffer!"+
                AVOID_MSG_STRING;

  // performance patch
  protected final static String STORE_SINGLE_PERFORMANCE_STRING =
                "\n\nThis driver doesn't support storing of a single performance patch for the"+
                "\nTX802, but you can do it manually on the device."+
                "\n\nThe patch has been placed in the edit buffer!"+
                AVOID_MSG_STRING;

}
