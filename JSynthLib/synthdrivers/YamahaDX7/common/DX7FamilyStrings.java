/*
 * JSynthlib - generic Strings for Yamaha DX7 Family
 * =================================================
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
import javax.swing.*;

public class DX7FamilyStrings
{
	public final static void dxShowInformation(String driver, String string)
	{
		 JOptionPane.showMessageDialog(PatchEdit.getInstance(),
			 driver+"Driver:"+ string,
			 driver+"Driver",
			 JOptionPane.INFORMATION_MESSAGE);
	}
	
	public final static void dxShowError(String driver, String string)
	{
		 JOptionPane.showMessageDialog(null,
			 string,
			 driver+"Driver Error",
			 JOptionPane.ERROR_MESSAGE);
	}
	
	// atomics of the information messages
	protected final static String AUTOMATION_MSG_STRING =
		"\n\n(You can automate this if you enable the \"Enable Remote Control?\""+
		"\nand \"Disable Memory Protection?\" function in the device configuration panel)";
	protected final static String RC_MSG_STRING =
		"\n\n(You can automate this if you enable the \"Enable Remote Control?\""+
		"\nfunction in the device configuration panel)";

	protected final static String MEMORY_PROTECTION_MSG_STRING =
		"\n\n(You can automate this if you enable the \"Disable Memory Protection?\""+
		"\nfunction in the device configuration panel)";

	protected final static String AVOID_MSG_STRING =
		"\n\n(You can avoid this message generally if you disable the \"Display"+
		"\nHints and Tips?\" function in the device configuration panel)";

	public final static String MEMORY_PROTECTION_STRING=
		"\n\nHave you disabled the memory protection?"+
		MEMORY_PROTECTION_MSG_STRING+AVOID_MSG_STRING;
}
