/*
 * JSynthlib - Parameter Models/Sysex Sender for Yamaha DX7-II
 * ===========================================================
 * @author  Torsten Tittmann
 * file:    DX7IIModelsSender.java
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
package synthdrivers.YamahaDX7II;
import core.*;

/*
 * SysexSender - Voice
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
 * SysexSender - Additional Voice
 *               (g=6; h=0)
 */
class AdditionalVoiceSender extends SysexSender
{
  int parameter;
  byte []b = new byte [7];

  public AdditionalVoiceSender(int p)
  {
    parameter = p;

    b[0]=(byte)0xF0;
    b[1]=(byte)0x43;
    b[3]=(byte)0x18;
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
 * Parameter Model - Fractional Scaling
 */
class FractionalScalingModel extends ParamModel
{
  int upper_nibble,lower_nibble;

  public FractionalScalingModel(Patch p, int o)
  {
    patch=p;
    ofs=16+(2*o);
  }
  public void set(int i)
  {
    patch.sysex[ofs  ] = (byte)( (i / 16  ) + 0x30 );
    patch.sysex[ofs+1] = (byte)( (i & 0x0f) + 0x30 );
  }
  public int get()
  {
     return (
              ( ( patch.sysex[ofs  ] - 0x30 ) *16 +
                ( patch.sysex[ofs+1] - 0x30 )       ) & 0xff
            );
  }
}


/*
 * SysexSender - Fractional Scaling
 *               (g=6; h=0)
 */
class FractionalScalingSender extends SysexSender
{
  Patch patch;
  int keyNumber, opNumber;
  byte []b = new byte [10];

  public FractionalScalingSender(Patch p, int o, int k)
  {
    patch     = p;
    opNumber  = o;
    keyNumber = k;

    b[0]=(byte)0xF0;
    b[1]=(byte)0x43;
    b[3]=(byte)0x18;
    b[4]=(byte)0x7F;
    b[5]=(byte)opNumber;
    b[6]=(byte)keyNumber;
    b[9]=(byte)0xF7;
  }
  public byte [] generate (int value)
  {
    b[2]=(byte)(0x10+channel-1);
    b[7]=(byte)( (value / 16  ) + 0x30 );
    b[8]=(byte)( (value & 0x0f) + 0x30 );

    return b;
  }
}


/*
 * SysexSender - Micro Tuning
 *               (g=6; h=0)
 */
class MicroTuningSender extends SysexSender
{
  Patch patch;
  int keyNumber, offset;
  boolean coarse;
  byte []b = new byte [9];

  public MicroTuningSender(Patch p, int k, boolean hl)
  {
    patch     = p;
    keyNumber = k;
    coarse    = hl;

    offset    = 16+2*keyNumber;

    b[0]=(byte)0xF0;
    b[1]=(byte)0x43;
    b[3]=(byte)0x18;
    b[4]=(byte)0x7E;
    b[5]=(byte)keyNumber;
    b[8]=(byte)0xF7;
  }
  public byte [] generate (int value)
  {
    b[2]=(byte)(0x10+channel-1);
    if (coarse)
    {
      b[6]=(byte)value;
      b[7]=(byte)(patch.sysex[offset+1]);
    }
    else
    {
      b[6]=(byte)(patch.sysex[offset  ]);
      b[7]=(byte)value;
    }
    return b;
  }
}

/*
 * SysexSender - Performance
 *               (g=6; h=1)
 */
class PerformanceSender extends SysexSender
{
  int parameter;
  byte []b = new byte [7];
  public PerformanceSender(int param)
  {
    parameter=param;

    b[0]=(byte)0xF0;
    b[1]=(byte)0x43;
    b[3]=(byte)0x19;
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
 * SysexSender - System Setup
 *               (g=6; h=1)
 */
class SystemSetupSender extends SysexSender
{
  int parameter;
  byte []b = new byte [7];
  public SystemSetupSender(int param)
  {
    parameter=param;

    b[0]=(byte)0xF0;
    b[1]=(byte)0x43;
    b[3]=(byte)0x19;
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
 * SysexSender - Master Tune
 *               (g=1; h=0)
 */
class MasterTuneSender extends SysexSender
{
  int parameter;
  byte []b = new byte [7];
  public MasterTuneSender(int param)
  {
    parameter=param;

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
