/*
 * JSynthlib - Parameter Changes for Yamaha TX802
 * ==============================================
 * @author  Torsten Tittmann
 * file:    TX802ParamChanges.java
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
import core.*;
public class TX802ParamChanges
{
 
  // ############################################### Common  ############################################
  // Voice Operator State parameter change command
  protected final static SysexHandler VoiceOPstate = new SysexHandler("f0 43 @@ 01 1b *value* f7");

  // send voice operator state 
  protected static void xmitVoiceOperatorState(int p, byte ch, byte st)				// port, channel, OperatorState
  { VoiceOPstate.send(p, ch, new NameValue("value", st)); }

  // ###############################################  TX802  ############################################
  // parameter change
  protected final static SysexHandler System       = new SysexHandler("f0 43 @@ 19 *param* *action* f7"); 
  protected final static SysexHandler Button       = new SysexHandler("f0 43 @@ 1B *switch* *OnOff* f7"); 

  // switch off internal/cartridge memory protection (!!!!!!! Not valid, just dummy !!!!!) 
  protected static void swOffMemProt(int p, byte ch, byte bn)				// port, channel, 
  { System.send(p, ch, new NameValue("param", 0x53), new NameValue("action",bn)); }	// bn: bit0 = internal, bit1 = cartridge

  // choose the desired MIDI receive/transmit block 
  protected static void chBlock(int p, byte ch, byte bn)				// port, channel, 
  { System.send(p, ch, new NameValue("param", 0x4d), new NameValue("action",bn)); }	// bn: 0 = 1-32, 1 = 33-64

  // choose voice mode 
  protected static void chVoiceMode(int p, byte ch)					// port, channel
  { Button.send(p, ch, new NameValue("switch", 0x52), new NameValue("OnOff", 0x00)); }	// parameter 82, OnOff = don't care



  // ################################# ASCII Hex format of TX802 Performance patch <-> Parameter Value ########################
  // 'Value' is split into high and low nibble. Each nibble is encoded as a Hex number, written in ASCII
  // E.g.: value = 0x7F --> ASCII String = 7F --> high nibble = 0x37, low nibble = 0x46
  //
  // Attention! The values 0 - 9 correlate with ASCII value 0x30 - 0x39
  //                and    A - F corralate with ASCII value 0x41 - 0x46
  //            The correlation is made by the arrays ASCII_HEX_2_PARAMETER_VALUE and PARAMETER_VALUE_2_ASCII_HEX
  // ##########################################################################################################################
  // Convertion of ASCII HEX to Parameter value 
  protected final static int AsciiHex2Value(int value)
  { return ( TX802Constants.ASCII_HEX_2_PARAMETER_VALUE[Byte.parseByte(Integer.toHexString(value-0x30))]); }

  // Convertion of Parameter value to ASCII HEX - High Nibble
  protected final static int Value2AsciiHexHigh(int value)
  { return ( Integer.valueOf(Integer.toString(TX802Constants.PARAMETER_VALUE_2_ASCII_HEX[value/16  ]),16).intValue()+0x30); }

  // Convertion of Parameter value to ASCII HEX - Low Nibble
  protected final static int Value2AsciiHexLow(int value)
  { return ( Integer.valueOf(Integer.toString(TX802Constants.PARAMETER_VALUE_2_ASCII_HEX[value&0x0F]),16).intValue()+0x30); }

}
