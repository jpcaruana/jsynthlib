/*
 * JSynthlib - Parameter Changes for Yamaha DX7s
 * =============================================
 * @author  Torsten Tittmann
 * file:    DX7sParamChanges.java
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
package synthdrivers.YamahaDX7s;
import core.*;
public class DX7sParamChanges
{
 
  // ############################################### Common  ############################################
  // Voice Operator State parameter change command
  protected final static SysexHandler VoiceOPstate = new SysexHandler("f0 43 @@ 01 1b *value* f7");

  // send voice operator state 
  protected static void xmitVoiceOperatorState(int p, byte ch, byte st)				// port, channel, OperatorState
  { VoiceOPstate.send(p, ch, new NameValue("value", st)); }

  // ###############################################  DX7s ############################################
  // parameter change
  protected final static SysexHandler System       = new SysexHandler("f0 43 @@ 19 *param* *action* f7"); 
  protected final static SysexHandler Button       = new SysexHandler("f0 43 @@ 1B *switch* *OnOff* f7"); 

  // switch off internal/cartridge memory protection
  protected static void swOffMemProt(int p, byte ch, byte bn)				// port, channel, 
  { System.send(p, ch, new NameValue("param", 0x53), new NameValue("action",bn)); }	// bn: bit0 = internal, bit1 = cartridge

  // choose the desired MIDI transmit block 
  protected static void chXmitBlock(int p, byte ch, byte bn)				// port, channel, 
  { System.send(p, ch, new NameValue("param", 0x4c), new NameValue("action",bn)); }	// bn: 0 = 1-32, 1 = 33-64

  // choose the desired MIDI receive block 
  protected static void chRcvBlock(int p, byte ch, byte bn)				// port, channel, 
  { System.send(p, ch, new NameValue("param", 0x4d), new NameValue("action",bn)); }	// bn: 0 = 1-32, 1 = 33-64

  // choose voice mode (voice button)
  protected static void chVoiceMode(int p, byte ch)						// port, channel
  { Button.send(p, ch, new NameValue("switch", 0x20), new NameValue("OnOff", 0x7f)); }	// switch 32
}
