/*
 * JSynthlib - Parameter Changes for Yamaha DX7 Mark-I
 * ===================================================
 * @author  Torsten Tittmann
 * file:    DX7ParamChanges.java
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
package synthdrivers.YamahaDX7;
import core.*;
public class DX7ParamChanges
{
 
  // ############################################### Common  ############################################
  // Voice Operator State parameter change command
  protected final static SysexHandler VoiceOPstate = new SysexHandler("f0 43 @@ 01 1b *value* f7");

  // send voice operator state 
  protected static void xmitVoiceOperatorState(int p, byte ch, byte st)				// port, channel, OperatorState
  { VoiceOPstate.send(p, ch, new NameValue("value", st)); }

  // ###############################################  DX7 mark I  ############################################
  // simulate panel button pushes constants
  protected final static int DEPRESS   = 0x7F;
  protected final static int RELEASE   = 0x00;

  protected final static int FUNCTION  = 0x27;
  protected final static int BATTERY   = 0x0d;
  protected final static int MIDI_CH   = 0x07;
  protected final static int SYSINFO   = 0x07;
  protected final static int MIDI_XMIT = 0x07;
  protected final static int YES       = 0x29;
  protected final static int NO        = 0x28;

  // simulate single panel button pushes
  protected final static SysexHandler Button       = new SysexHandler("f0 43 @@ 08 *button* *action* f7"); // common button push
  protected final static SysexHandler depressStore = new SysexHandler("f0 43 @@ 08 20 7f f7");             //BUTTON depress: Store
  protected final static SysexHandler releaseStore = new SysexHandler("f0 43 @@ 08 20 00 f7");             //BUTTON release: 

  // make system informations available
  protected static void mkSysInfoAvail(int p, byte ch) // port, channel
  {
    Button.send(p, ch, new NameValue("button",FUNCTION) , new NameValue("action",DEPRESS));
    Button.send(p, ch, new NameValue("button",FUNCTION) , new NameValue("action",RELEASE));

    Button.send(p, ch, new NameValue("button",BATTERY)  , new NameValue("action",DEPRESS));
    Button.send(p, ch, new NameValue("button",BATTERY)  , new NameValue("action",RELEASE));

    Button.send(p, ch, new NameValue("button",MIDI_CH)  , new NameValue("action",DEPRESS));
    Button.send(p, ch, new NameValue("button",MIDI_CH)  , new NameValue("action",RELEASE));

    Button.send(p, ch, new NameValue("button",SYSINFO)  , new NameValue("action",DEPRESS));
    Button.send(p, ch, new NameValue("button",SYSINFO)  , new NameValue("action",RELEASE));

    Button.send(p, ch, new NameValue("button",YES)      , new NameValue("action",DEPRESS));
    Button.send(p, ch, new NameValue("button",YES)      , new NameValue("action",RELEASE));
  }

  // switch off memory protection
  protected static void swOffMemProt(int p, byte ch, byte mp, byte bn) // port, channel, memory protection of internal/cartridge, internal/cartridge
  {
    Button.send(p, ch, new NameValue("button",mp),        new NameValue("action",DEPRESS));
    Button.send(p, ch, new NameValue("button",mp),        new NameValue("action",RELEASE));

    Button.send(p, ch, new NameValue("button",NO),        new NameValue("action",DEPRESS));
    Button.send(p, ch, new NameValue("button",NO),        new NameValue("action",RELEASE));

    Button.send(p, ch, new NameValue("button",bn),        new NameValue("action",DEPRESS));
    Button.send(p, ch, new NameValue("button",bn),        new NameValue("action",RELEASE));
  }

  // transmit bank dump
  protected static void xmitBankDump(int p, byte ch) // port, channel
  {
    Button.send(p, ch, new NameValue("button",MIDI_XMIT), new NameValue("action",DEPRESS));
    Button.send(p, ch, new NameValue("button",MIDI_XMIT), new NameValue("action",RELEASE));

    Button.send(p, ch, new NameValue("button",YES)      , new NameValue("action",DEPRESS));
    Button.send(p, ch, new NameValue("button",YES)      , new NameValue("action",RELEASE));
  }

  // switch to desired bank
  protected static void chBank(int p, byte ch, byte bn) // port, channel, internal/cartridge
  {
    Button.send(p, ch, new NameValue("button",bn),        new NameValue("action",DEPRESS));
    Button.send(p, ch, new NameValue("button",bn),        new NameValue("action",RELEASE));
  }

  // switch to desired patch number
  protected static void chPatch(int p, byte ch, byte pn) // port, channel, patch number
  {
    Button.send(p, ch, new NameValue("button",pn),        new NameValue("action",DEPRESS));
    Button.send(p, ch, new NameValue("button",pn),        new NameValue("action",RELEASE));
  }

}
