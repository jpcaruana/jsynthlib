/*
 * Copyright 2005 Federico Ferri
 *
 * This file is part of JSynthLib.
 *
 * JSynthLib is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or(at your option) any later version.
 *
 * JSynthLib is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JSynthLib; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 */
// written by Federico Ferri
// @version $Id$

package org.jsynthlib.drivers.roland.mks7;

import org.jsynthlib.core.Driver;
import org.jsynthlib.core.JSLFrame;
import org.jsynthlib.core.Patch;

public class MKS7ToneSingleDriver extends Driver
{
  public MKS7ToneSingleDriver()
  {
    super ("Tone Single","Federico Ferri");
    sysexID = "F041300*";
    //sysexRequestDump = new SysexHandler("F0 10 06 04 01 *patchNum* F7");
    patchSize = 24;
    patchNameStart = 0;
    patchNameSize = 0;
    deviceIDoffset = 3;
    bankNumbers  = new String[] {"Tone Bank"};
    patchNumbers = new String[] {"11-", "12-", "13-", "14-", "15-", "16-", "17-", "18-",
                                 "21-", "22-", "23-", "24-", "25-", "26-", "27-", "28-",
                                 "31-", "32-", "33-", "34-", "35-", "36-", "37-", "38-",
                                 "41-", "42-", "43-", "44-", "45-", "46-", "47-", "48-",
                                 "51-", "52-", "53-", "54-", "55-", "56-", "57-", "58-",
                                 "61-", "62-", "63-", "64-", "65-", "66-", "67-", "68-",
                                 "71-", "72-", "73-", "74-", "75-", "76-", "77-", "78-",
                                 "81-", "82-", "83-", "84-", "85-", "86-", "87-", "88-"};
  }

  public void calculateChecksum(Patch p)
  {
    // MKS-7 doesn't use checksum
  }

  public void setBankNum(int bankNum)
  {
    // MKS-7 doesn't have banks: pgm# 0-99 is the only accepted range
  }

  public Patch createNewPatch()
  {
    byte sysex[] = {
      (byte)0xF0, (byte)0x41, (byte)0x30, (byte)0x00, (byte)0x00,
      (byte)0x39, (byte)0x2D, (byte)0x00, (byte)0x37, (byte)0x00,
      (byte)0x55, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x19,
      (byte)0x34, (byte)0x3B, (byte)0x20, (byte)0x56, (byte)0x28,
      (byte)0x00, (byte)0x1A, (byte)0x18, (byte)0xF7
    };
    Patch p = new Patch(sysex, this);
    return p;
  }

  public JSLFrame editPatch(Patch p)
  {
     return new MKS7ToneSingleEditor((Patch)p);
  }
}

