/*
 * JSynthlib - "Performance" Bank Driver for Yamaha TX802
 * ======================================================
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
 *
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * CAUTION: This is an experimental driver. It is not tested on a real device yet!
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 *
 */
package synthdrivers.YamahaTX802;
import core.*;
import java.io.*;
import javax.swing.*;

public class YamahaTX802PerformanceBankDriver extends BankDriver
{
  public YamahaTX802PerformanceBankDriver()
  {
    manufacturer="Yamaha";
    model="TX802";
    patchType="Performance Bank";
    id="TX802";
    authors="Torsten Tittmann";
    sysexID="F0430*7E01284C4D202038393532504d";
    sysexRequestDump=new SysexHandler("F0 43 @@ 7E 4C 4D 20 20 38 39 35 32 50 4D F7");
    deviceIDoffset=2;
    patchNameStart=0;
    patchNameSize=0;
    bankNumbers  = TX802Constants.BANK_NUMBERS_PERFORMANCE;
    patchNumbers = TX802Constants.PATCH_NUMBERS_PERFORMANCE;
    numPatches=patchNumbers.length;
    numColumns=4;
    singleSysexID="F0430*7E01684C4D2020383935325045";
    singleSize=250;
    //checksumOffset=11587;     // This patch doesn't uses an over-all checksum for bank bulk data
    //checksumStart=6;
    //checksumEnd=11586;
    numSysexMsgs=1;
    trimSize=11589;
  }


  public void storePatch (Patch p, int bankNum,int patchNum)
  {
    if ( ((YamahaTX802Device)(PatchEdit.appConfig.getDevice(getDeviceNum()))).getSwOffMemProtFlag()==1 )
    {
      // switch off memory protection 
      TX802ParamChanges.swOffMemProt(port, (byte)(channel+0x10) );
    }
    else
    {
      if( ((YamahaTX802Device)(PatchEdit.appConfig.getDevice(getDeviceNum()))).getTipsMsgFlag()==1 )
          // Information
          JOptionPane.showMessageDialog(PatchEdit.instance,
                                        getDriverName()+"Driver:"+ TX802Strings.MEMORY_PROTECTION_STRING,
                                        getDriverName()+"Driver",
                                        JOptionPane.INFORMATION_MESSAGE);
    }
   
    sendPatchWorker(p);
  };


  public void requestPatchDump(int bankNum, int patchNum)
  {
    sysexRequestDump.send(port, (byte)(channel+0x20) );
  }


  public void calculateChecksum(Patch p)
  {
    // This patch doesn't uses an over-all checksum for bank bulk data
  }


   public int getPatchStart(int patchNum)
  {
    int start=(181*patchNum); // single-patch-size in bulked-dump-packed-format (3+10+2*84)
    start+=4;                 // sysex header

    return start;

  }


 public String getPatchName(Patch p,int patchNum)
  {
    int nameStart=getPatchStart(patchNum);
    nameStart+=12+(2*64);                        //offset of name in patch data

    try {
          byte []b = new byte[20];
          for (int i=0; i < b.length; i++)
          {
            b[i] =(byte)( TX802ParamChanges.AsciiHex2Value(p.sysex[nameStart+(2*i)  ])*16 +
                          TX802ParamChanges.AsciiHex2Value(p.sysex[nameStart+(2*i)+1]) );
          }
          StringBuffer s= new StringBuffer(new String(b,0,20,"US-ASCII"));

          return s.toString();
        } catch (Exception ex) {return "-";}
  }


  public void setPatchName(Patch p,int patchNum, String name)
  {
    patchNameSize=20;
    patchNameStart=getPatchStart(patchNum)+12+(2*64);

    if (name.length()<patchNameSize) name=name+"                       ";
    byte [] namebytes = new byte [64];

    try
    {
      namebytes=name.getBytes("US-ASCII");
      for (int i=0;i<patchNameSize;i++)
      {
        p.sysex[patchNameStart+(2*i)  ]=(byte)(TX802ParamChanges.Value2AsciiHexHigh(namebytes[i]));
        p.sysex[patchNameStart+(2*i)+1]=(byte)(TX802ParamChanges.Value2AsciiHexLow( namebytes[i]));
      }
    } catch (UnsupportedEncodingException ex) {return;}
  }


  // returns the byte value of the ASCII Hex encoded high and low nibble
  public int getByte(Patch p, int index)
  { return ( TX802ParamChanges.AsciiHex2Value(p.sysex[index  ])*16 + TX802ParamChanges.AsciiHex2Value(p.sysex[index+1]) ); }


  public void putPatch(Patch bank,Patch p,int patchNum) //puts a patch into the bank, converting it as needed
  {
    if (!canHoldPatch(p))
    {
      JOptionPane.showMessageDialog(null, "This type of patch does not fit in to this type of bank.",getDriverName()+" Driver Error", JOptionPane.ERROR_MESSAGE);
      return;
    }

    // Transform Voice Data to Bulk Dump Packed Format
    int value;

    bank.sysex[getPatchStart(patchNum)+ 0   ]=(byte)(0x01);	// Byte Count MSB
    bank.sysex[getPatchStart(patchNum)+ 1   ]=(byte)(0x28);	// Byte Count LSB
    bank.sysex[getPatchStart(patchNum)+ 2   ]=(byte)(0x4C);	// "L"
    bank.sysex[getPatchStart(patchNum)+ 3   ]=(byte)(0x4D);	// "M"
    bank.sysex[getPatchStart(patchNum)+ 4   ]=(byte)(0x20);	// " "
    bank.sysex[getPatchStart(patchNum)+ 5   ]=(byte)(0x20);	// " "
    bank.sysex[getPatchStart(patchNum)+ 6   ]=(byte)(0x38);	// "8"
    bank.sysex[getPatchStart(patchNum)+ 7   ]=(byte)(0x39);	// "9"
    bank.sysex[getPatchStart(patchNum)+ 8   ]=(byte)(0x35);	// "5"
    bank.sysex[getPatchStart(patchNum)+ 9   ]=(byte)(0x32);	// "2"
    bank.sysex[getPatchStart(patchNum)+10   ]=(byte)(0x50);	// "P"
    bank.sysex[getPatchStart(patchNum)+11   ]=(byte)(0x4D);	// "M"

    for (int i=0; i<8;i++)					// TG1-8 - Voice Channel Offset (0-7) | MIDI Receive Channel (0-16)
    {
      value = getByte(p,16+(2* 0)+(2*i))*32 +
              getByte(p,16+(2* 8)+(2*i));
      bank.sysex[getPatchStart(patchNum)+12+(2* 0)+(2*i)  ]=(byte)(TX802ParamChanges.Value2AsciiHexHigh(value));
      bank.sysex[getPatchStart(patchNum)+12+(2* 0)+(2*i)+1]=(byte)(TX802ParamChanges.Value2AsciiHexLow( value));
    }

    for (int i=0; i<8;i++)					// TG1-8 - Voice Number (0-255)
    {
      value = getByte(p,16+(2*16)+(2*i));
      bank.sysex[getPatchStart(patchNum)+12+(2* 8)+(2*i)  ]=(byte)(TX802ParamChanges.Value2AsciiHexHigh(value));
      bank.sysex[getPatchStart(patchNum)+12+(2* 8)+(2*i)+1]=(byte)(TX802ParamChanges.Value2AsciiHexLow( value));
    }

    for (int i=0; i<8;i++)					// TG1-8 - Micro Tuning Table # (0-254)
    {
      value = getByte(p,16+(2*88)+(2*i));
      bank.sysex[getPatchStart(patchNum)+12+(2*16)+(2*i)  ]=(byte)(TX802ParamChanges.Value2AsciiHexHigh(value));
      bank.sysex[getPatchStart(patchNum)+12+(2*16)+(2*i)+1]=(byte)(TX802ParamChanges.Value2AsciiHexLow( value));
    }

    for (int i=0; i<8;i++)					// TG1-8 - Output Volume (0-99)
    {
      value = getByte(p,16+(2*32)+(2*i));
      bank.sysex[getPatchStart(patchNum)+12+(2*24)+(2*i)  ]=(byte)(TX802ParamChanges.Value2AsciiHexHigh(value));
      bank.sysex[getPatchStart(patchNum)+12+(2*24)+(2*i)+1]=(byte)(TX802ParamChanges.Value2AsciiHexLow( value));
    }

    for (int i=0; i<8;i++)					// TG1-8 - Detune (0-14) | Key Assign Group (0-1) | Output Assign (0-3)
    {
      value = getByte(p,16+(2*24)+(2*i))*16 +
              getByte(p,16+(2*80)+(2*i))*8  +
              getByte(p,16+(2*40)+(2*i));
      bank.sysex[getPatchStart(patchNum)+12+(2*32)+(2*i)  ]=(byte)(TX802ParamChanges.Value2AsciiHexHigh(value));
      bank.sysex[getPatchStart(patchNum)+12+(2*32)+(2*i)+1]=(byte)(TX802ParamChanges.Value2AsciiHexLow( value));
    }

    for (int i=0; i<8;i++)					// TG1-8 - Note Limit Low (0-127)
    {
      value = getByte(p,16+(2*48)+(2*i));
      bank.sysex[getPatchStart(patchNum)+12+(2*40)+(2*i)  ]=(byte)(TX802ParamChanges.Value2AsciiHexHigh(value));
      bank.sysex[getPatchStart(patchNum)+12+(2*40)+(2*i)+1]=(byte)(TX802ParamChanges.Value2AsciiHexLow( value));
    }

    for (int i=0; i<8;i++)					// TG1-8 - Note Limit High (0-127)
    {
      value = getByte(p,16+(2*56)+(2*i));
      bank.sysex[getPatchStart(patchNum)+12+(2*48)+(2*i)  ]=(byte)(TX802ParamChanges.Value2AsciiHexHigh(value));
      bank.sysex[getPatchStart(patchNum)+12+(2*48)+(2*i)+1]=(byte)(TX802ParamChanges.Value2AsciiHexLow( value));
    }

    for (int i=0; i<8;i++)					// TG1-8 - EG forced Damp (0-1) | Note Shift (0-48)
    {
      value = getByte(p,16+(2*72)+(2*i))*64 +
              getByte(p,16+(2*64)+(2*i));
      bank.sysex[getPatchStart(patchNum)+12+(2*56)+(2*i)  ]=(byte)(TX802ParamChanges.Value2AsciiHexHigh(value));
      bank.sysex[getPatchStart(patchNum)+12+(2*56)+(2*i)+1]=(byte)(TX802ParamChanges.Value2AsciiHexLow( value));
    }

    for (int i=0; i<20;i++)					// Performance Name (ASCII)
    {
      value = getByte(p,16+(2*96)+(2*i));
      bank.sysex[getPatchStart(patchNum)+12+(2*64)+(2*i)  ]=(byte)(TX802ParamChanges.Value2AsciiHexHigh(value));
      bank.sysex[getPatchStart(patchNum)+12+(2*64)+(2*i)+1]=(byte)(TX802ParamChanges.Value2AsciiHexLow( value));
    }

    calculateChecksum(bank,getPatchStart(patchNum)+2,getPatchStart(patchNum)+179,getPatchStart(patchNum)+180);	// Calculate checkSum of single bulk data
  }


  public Patch getPatch(Patch bank, int patchNum) //Gets a patch from the bank, converting it as needed
  {
    try
    {
      byte [] sysex=new byte[singleSize];
      int value;

      // transform bulk-dump-packed-format to voice data
      sysex[  0]=(byte)0xF0;
      sysex[  1]=(byte)0x43;
      sysex[  2]=(byte)0x00;
      sysex[  3]=(byte)0x7E;
      sysex[  4]=(byte)0x01;
      sysex[  5]=(byte)0x68;
      sysex[  6]=(byte)0x4C;	// "L"
      sysex[  7]=(byte)0x4D;	// "M"
      sysex[  8]=(byte)0x20;	// " "
      sysex[  9]=(byte)0x20;	// " "
      sysex[ 10]=(byte)0x38;	// "8"
      sysex[ 11]=(byte)0x39;	// "9"
      sysex[ 12]=(byte)0x35;	// "5"
      sysex[ 13]=(byte)0x32;	// "2"
      sysex[ 14]=(byte)0x50;	// "P"
      sysex[ 15]=(byte)0x45;	// "E"
      sysex[singleSize-1]=(byte)0xF7;
                                                                                   
      for (int i=0; i<8;i++)					// TG1-8 - Voice Channel Offset (0-7) 
      {
        value = (getByte(bank,getPatchStart(patchNum)+12+(2* 0)+(2*i))&224) /32;
        sysex[16+(2* 0)+(2*i)  ] = (byte)(TX802ParamChanges.Value2AsciiHexHigh(value));
        sysex[16+(2* 0)+(2*i)+1] = (byte)(TX802ParamChanges.Value2AsciiHexLow( value));
      }

      for (int i=0; i<8;i++)					// TG1-8 - MIDI Receive Channel (0-16)
      {
        value = (getByte(bank,getPatchStart(patchNum)+12+(2* 0)+(2*i))&31);
        sysex[16+(2* 8)+(2*i)  ] = (byte)(TX802ParamChanges.Value2AsciiHexHigh(value));
        sysex[16+(2* 8)+(2*i)+1] = (byte)(TX802ParamChanges.Value2AsciiHexLow( value));
      }

      for (int i=0; i<8;i++)					// TG1-8 - Voice Number (0-255)
      {
        value = (getByte(bank,getPatchStart(patchNum)+12+(2* 8)+(2*i))&255);
        sysex[16+(2*16)+(2*i)  ] = (byte)(TX802ParamChanges.Value2AsciiHexHigh(value));
        sysex[16+(2*16)+(2*i)+1] = (byte)(TX802ParamChanges.Value2AsciiHexLow( value));
      }

      for (int i=0; i<8;i++)					// TG1-8 - Detune (0-14)
      {
        value = (getByte(bank,getPatchStart(patchNum)+12+(2*32)+(2*i))&112)/16;
        sysex[16+(2*24)+(2*i)  ] = (byte)(TX802ParamChanges.Value2AsciiHexHigh(value));
        sysex[16+(2*24)+(2*i)+1] = (byte)(TX802ParamChanges.Value2AsciiHexLow( value));
      }

      for (int i=0; i<8;i++)					// TG1-8 - Output Volume (0-99)
      {
        value = (getByte(bank,getPatchStart(patchNum)+12+(2*24)+(2*i))&127);
        sysex[16+(2*32)+(2*i)  ] = (byte)(TX802ParamChanges.Value2AsciiHexHigh(value));
        sysex[16+(2*32)+(2*i)+1] = (byte)(TX802ParamChanges.Value2AsciiHexLow( value));
      }

      for (int i=0; i<8;i++)					// TG1-8 - Output Assign (0-3)
      {
        value = (getByte(bank,getPatchStart(patchNum)+12+(2*32)+(2*i))&7);
        sysex[16+(2*40)+(2*i)  ] = (byte)(TX802ParamChanges.Value2AsciiHexHigh(value));
        sysex[16+(2*40)+(2*i)+1] = (byte)(TX802ParamChanges.Value2AsciiHexLow( value));
      }

      for (int i=0; i<8;i++)					// TG1-8 - Note Limit low (0-127)
      {
        value = (getByte(bank,getPatchStart(patchNum)+12+(2*40)+(2*i))&127);
        sysex[16+(2*48)+(2*i)  ] = (byte)(TX802ParamChanges.Value2AsciiHexHigh(value));
        sysex[16+(2*48)+(2*i)+1] = (byte)(TX802ParamChanges.Value2AsciiHexLow( value));
      }

      for (int i=0; i<8;i++)					// TG1-8 - Note Limit high (0-127)
      {
        value = (getByte(bank,getPatchStart(patchNum)+12+(2*48)+(2*i))&127);
        sysex[16+(2*56)+(2*i)  ] = (byte)(TX802ParamChanges.Value2AsciiHexHigh(value));
        sysex[16+(2*56)+(2*i)+1] = (byte)(TX802ParamChanges.Value2AsciiHexLow( value));
      }

      for (int i=0; i<8;i++)					// TG1-8 - Note Shift (0-48)
      {
        value = (getByte(bank,getPatchStart(patchNum)+12+(2*56)+(2*i))&63);
        sysex[16+(2*64)+(2*i)  ] = (byte)(TX802ParamChanges.Value2AsciiHexHigh(value));
        sysex[16+(2*64)+(2*i)+1] = (byte)(TX802ParamChanges.Value2AsciiHexLow( value));
      }

      for (int i=0; i<8;i++)					// TG1-8 - EG forced damp (0-1)
      {
        value = (getByte(bank,getPatchStart(patchNum)+12+(2*56)+(2*i))&64)/64;
        sysex[16+(2*72)+(2*i)  ] = (byte)(TX802ParamChanges.Value2AsciiHexHigh(value));
        sysex[16+(2*72)+(2*i)+1] = (byte)(TX802ParamChanges.Value2AsciiHexLow( value));
      }

      for (int i=0; i<8;i++)					// TG1-8 - Key Assign Group (0-1)
      {
        value = (getByte(bank,getPatchStart(patchNum)+12+(2*32)+(2*i))&8)/8;
        sysex[16+(2*80)+(2*i)  ] = (byte)(TX802ParamChanges.Value2AsciiHexHigh(value));
        sysex[16+(2*80)+(2*i)+1] = (byte)(TX802ParamChanges.Value2AsciiHexLow( value));
      }

      for (int i=0; i<8;i++)					// TG1-8 - Micro Tuning Table # (0-254)
      {
        value = (getByte(bank,getPatchStart(patchNum)+12+(2*16)+(2*i))&255);
        sysex[16+(2*88)+(2*i)  ] = (byte)(TX802ParamChanges.Value2AsciiHexHigh(value));
        sysex[16+(2*88)+(2*i)+1] = (byte)(TX802ParamChanges.Value2AsciiHexLow( value));
      }

      for (int i=0; i<20;i++)					// Performance Name (ASCII)
      {
        value = (getByte(bank,getPatchStart(patchNum)+12+(2*64)+(2*i))&255);
        sysex[16+(2*96)+(2*i)  ] = (byte)(TX802ParamChanges.Value2AsciiHexHigh(value));
        sysex[16+(2*96)+(2*i)+1] = (byte)(TX802ParamChanges.Value2AsciiHexLow( value));
      }


      Patch p = new Patch(getDeviceNum(),sysex);        // single sysex
      PatchEdit.getDriver(p.deviceNum,p.driverNum).calculateChecksum(p);

      return p;
    } catch (Exception e) {ErrorMsg.reportError(manufacturer+" "+model,"Error in " + getDriverName(),e);return null;}
  }


  public Patch createNewPatch() // create a bank with 64 performance patches
  {
    byte [] sysex = new byte[trimSize];
    sysex[ 0]=(byte)0xF0;
    sysex[ 1]=(byte)0x43;
    sysex[ 2]=(byte)0x00;
    sysex[ 3]=(byte)0x7E;
    sysex[trimSize-1]=(byte)0xF7;

    Patch v = new Patch(getDeviceNum(),TX802Constants.INIT_PERFORMANCE);       // single sysex
    Patch p = new Patch(sysex,getDeviceNum(),getDriverNum());   // bank sysex

    for (int i=0;i<numPatches;i++)
       putPatch(p,v,i);

    return p;
  }

}
