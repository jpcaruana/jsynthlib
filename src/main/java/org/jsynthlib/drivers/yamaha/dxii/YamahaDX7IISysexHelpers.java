/*
 * JSynthlib - Sysex Parameter Changes for Yamaha DX7-II
 * =====================================================
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
import org.jsynthlib.core.Driver;
import org.jsynthlib.core.SysexHandler;

public class YamahaDX7IISysexHelpers
{
	// ###############################################  DX7-II  ############################################
	// parameter change
	protected final static SysexHandler System	 = new SysexHandler("f0 43 @@ 19 *param* *action* f7"); 
	protected final static SysexHandler Button	 = new SysexHandler("f0 43 @@ 1B *switch* *OnOff* f7"); 

	// switch off internal/cartridge memory protection
	protected static void swOffMemProt(Driver d, byte ch, byte bn)				// port, channel, 
	{ d.send(System.toSysexMessage(ch, new SysexHandler.NameValue("param", 0x53), new SysexHandler.NameValue("action",bn))); }	// bn: bit0 = internal, bit1 = cartridge

	// choose the desired MIDI transmit block 
	protected static void chXmitBlock(Driver d, byte ch, byte bn)				// port, channel, 
	{ d.send(System.toSysexMessage(ch, new SysexHandler.NameValue("param", 0x4c), new SysexHandler.NameValue("action",bn))); }	// bn: 0 = 1-32, 1 = 33-64

	// choose the desired MIDI receive block 
	protected static void chRcvBlock(Driver d, byte ch, byte bn)				// port, channel, 
	{ d.send(System.toSysexMessage(ch, new SysexHandler.NameValue("param", 0x4d), new SysexHandler.NameValue("action",bn))); }	// bn: 0 = 1-32, 1 = 33-64

	// choose voice mode ('single' button)
	protected static void chVoiceMode(Driver d, byte ch)					// port, channel
	{ d.send(Button.toSysexMessage(ch, new SysexHandler.NameValue("switch", 0x24), new SysexHandler.NameValue("OnOff", 0x7f))); }	// switch 36
}
