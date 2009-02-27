/*
 * JSynthlib - Parameter Changes for Yamaha TX802
 * ==============================================
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
package synthdrivers.YamahaTX802;
import org.jsynthlib.core.Driver;
import org.jsynthlib.core.SysexHandler;

import core.*;

public class YamahaTX802SysexHelpers
{
	// ###############################################  TX802  ############################################
	// simulate panel button pushes constants
	protected final static int DEPRESS   = 0x7F;
	protected final static int RELEASE   = 0x00;

	protected final static int SYSTEM_SETUP = 0x53;
	protected final static int TG8		= 0x60;       // Tongenerator 1 On/Off / Parameter Select
	protected final static int OFF		= 0x4e;       // Off / -1 / no
	protected final static int ON		= 0x4f;       // On  / +1 / yes

	// parameter change
	protected final static SysexHandler System	 = new SysexHandler("f0 43 @@ 19 *param* *action* f7");
	protected final static SysexHandler Button	 = new SysexHandler("f0 43 @@ 1B *switch* *OnOff* f7"); // don't care about "OnOff"

	// switch off internal/cartridge memory protection
	protected static void swOffMemProt(Driver d, byte ch)					// port, channel
	{
		d.send(Button.toSysexMessage(ch, new SysexHandler.NameValue("switch",SYSTEM_SETUP), new SysexHandler.NameValue("OnOff",DEPRESS)));
		d.send(Button.toSysexMessage(ch, new SysexHandler.NameValue("switch",TG8),	       new SysexHandler.NameValue("OnOff",DEPRESS)));
		d.send(Button.toSysexMessage(ch, new SysexHandler.NameValue("switch",OFF),	       new SysexHandler.NameValue("OnOff",DEPRESS)));
	}

	protected static void swOnMemProt(Driver d, byte ch)						// port, channel
	{
		d.send(Button.toSysexMessage(ch, new SysexHandler.NameValue("switch",SYSTEM_SETUP), new SysexHandler.NameValue("OnOff",DEPRESS)));
		d.send(Button.toSysexMessage(ch, new SysexHandler.NameValue("switch",TG8),	       new SysexHandler.NameValue("OnOff",DEPRESS)));
		d.send(Button.toSysexMessage(ch, new SysexHandler.NameValue("switch",ON),	       new SysexHandler.NameValue("OnOff",DEPRESS)));
	}

	// choose the desired MIDI receive/transmit block
	protected static void chBlock(Driver d, byte ch, byte bn)				// port, channel,
	{ d.send(System.toSysexMessage(ch, new SysexHandler.NameValue("param", 0x4d), new SysexHandler.NameValue("action",bn))); }	// bn: 0 = 1-32, 1 = 33-64

	// choose voice mode
	protected static void chVoiceMode(Driver d, byte ch)					// port, channel
	{ d.send(Button.toSysexMessage(ch, new SysexHandler.NameValue("switch", 0x52), new SysexHandler.NameValue("OnOff", 0x00))); }	// parameter 82, OnOff = don't care
}
