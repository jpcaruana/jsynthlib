/*
 * JSynthlib - Parameter Changes for Yamaha TX7 
 * ============================================
 * @author  Torsten Tittmann
 * file:    TX7ParamChanges.java
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
package synthdrivers.YamahaTX7;
import core.*;
public class TX7ParamChanges
{
 
  // ############################################### Common  ############################################
  // Voice Operator State parameter change command
  protected final static SysexHandler VoiceOPstate = new SysexHandler("f0 43 @@ 01 1b *value* f7");

  // send voice operator state 
  protected static void xmitVoiceOperatorState(int p, byte ch, byte st)				// port, channel, OperatorState
  { VoiceOPstate.send(p, ch, new NameValue("value", st)); }

  // ###############################################  TX7  ############################################
  // TX7 parameter change 
  protected final static SysexHandler swOffMemProt = new SysexHandler("f0 43 @@ 11 07 00 f7");	// switch off internal memory protection
}
