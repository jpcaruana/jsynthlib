/*
 * JSynthlib - Parameter-Models/Sysex-Sender for Yamaha DX7 Mark-I
 * ===============================================================
 * @version $Id$
 * @author  Torsten Tittmann
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
 */
package synthdrivers.YamahaDX7;
import core.*;

/*
 * SysexSender - Voice Parameter
 *		 (g=0; h=0 & g=0; h=1)
 */
class VoiceSender extends SysexSender
{
	int parameter;
	int para_high=0;
	byte []b = new byte [7];
	
	public VoiceSender(int param)
	{
		parameter=param;
		
		if (parameter >= 128) {
			para_high  = 0x01;
			parameter -= 128;
		}
		
		b[0]=(byte)0xF0;
		b[1]=(byte)0x43;
		b[3]=(byte)para_high;	// group/subgroup number
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
 *		 (g=2; h=0)
 */
class PerformanceSender extends SysexSender
{
	Patch patch;
	int parameter;
	byte []b = new byte [7];
	// translation table TX7->DX7 for Sensitivity parameters (ModulationWheel, FootCtrl, BreathCtrl, AfterTouch)
	byte []TX2DXsens = new byte [] {0x00,0x06,0x0d,0x13,0x1A,0x21,0x27,0x2E,0x35,0x3B,0x42,0x48,0x4F,0x56,0x5C,0x63};

	public PerformanceSender(Patch p, int param)
	{
		patch = p;
		parameter = param;


		b[0]=(byte)0xF0;
		b[1]=(byte)0x43;
		b[3]=(byte)0x08;
		b[4]=(byte)parameter;
		b[6]=(byte)0xF7;
	}
	
	public byte [] generate (int value)
	{
		b[2]=(byte)(0x10+channel-1);
		b[5]=(byte)value;

		if ( parameter==0x46 || parameter==0x48 || parameter==0x4A || parameter==0x4C)
			b[5]=(byte)TX2DXsens[value];

		if (b[4] == (byte)(-1)) return null;
		else  return b;
	}
}
