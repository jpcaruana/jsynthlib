/*
 * JSynthlib - Parameter-Models/Sysex-Sender for Yamaha TX7
 * ========================================================
 * @author  Torsten Tittmann
 * file:    TX7ModelsSender.java
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

/*
 * SysexSender - Voice Parameter
 *               (g=0; h=0 & g=0; h=1)
 */
class VoiceSender extends SysexSender
{
  int parameter;
  int para_high=0;
  byte []b = new byte [7];
  public VoiceSender(int param)
  {
    parameter=param;
    if (parameter >= 128)
    {
      para_high  = 0x01;
      parameter -= 128;
    }
    b[0]=(byte)0xF0;
    b[1]=(byte)0x43;
    b[3]=(byte)para_high;       // group/subgroup number
    b[4]=(byte)parameter;
    b[6]=(byte)0xF7;
  }
  public byte [] generate (int value)
  {
    b[2]=(byte)(0x10+channel-1);
    b[5]=(byte)value;
    return b;
  }
}

/*
 * SysexSender - Performance Parameter
 *               (g=1; h=0)
 */
class PerformanceSender extends SysexSender
{
  Patch patch;
  int parameter;
  byte []b = new byte [7];

  public PerformanceSender(Patch p, int param)
  {
    patch = p;
    parameter = param;

    b[0]=(byte)0xF0;
    b[1]=(byte)0x43;
    b[3]=(byte)0x04;
    b[4]=(byte)parameter;
    b[6]=(byte)0xF7;
  }
  public byte [] generate (int value)
  {
    b[2]=(byte)(0x10+channel-1);
    b[5]=(byte)value;

    return b;
  }
}
