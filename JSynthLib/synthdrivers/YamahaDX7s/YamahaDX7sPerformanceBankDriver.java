/*
 * JSynthlib - "Performance" Bank Driver for Yamaha DX7s
 * =====================================================
 * @author  Torsten Tittmann
 * file:    YamahaDX7sPerformanceBankDriver.java
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
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * CAUTION: This is an experimental driver. It is not tested on a real device yet!
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 *
 * history:
 *         25.02.2003 v0.1: first published release
 *
 */

package synthdrivers.YamahaDX7s;
import core.*;
import java.io.*;
import javax.swing.*;

public class YamahaDX7sPerformanceBankDriver extends BankDriver
{
  public YamahaDX7sPerformanceBankDriver()
  {
    manufacturer="Yamaha";
    model="DX7s";
    patchType="Performance Bank";
    id="DX7s";
    authors="Torsten Tittmann";
    sysexID="f0430*7e0c6a4c4d202038393733504d";
    sysexRequestDump=new SysexHandler("f0 43 @@ 7e 4c 4d 20 20 38 39 37 33 50 4d f7");
    deviceIDoffset=2;
    patchNameStart=0;
    patchNameSize=0;
    patchNumbers = DX7sConstants.PATCH_NUMBERS_PERFORMANCE;
    bankNumbers  =  DX7sConstants.BANK_NUMBERS_PERFORMANCE;
    numPatches=patchNumbers.length;
    numColumns=4;
    singleSysexID="F0430*7E003d4c4d2020383937335045";;
    singleSize=69;
    checksumOffset=1648;
    checksumStart=6;
    checksumEnd=1647;
    numSysexMsgs=1;
    trimSize=1650;
  }


  public void storePatch (Patch p, int bankNum,int patchNum)
  {
    if ( ((YamahaDX7sDevice)(PatchEdit.appConfig.getDevice(getDeviceNum()))).getSwOffMemProtFlag()==1 )
    {
      // switch off memory protection (internal+cartridge!)
      DX7sParamChanges.swOffMemProt(port, (byte)(channel+0x10), (byte)0 );
    }
    else
    {
      if( ((YamahaDX7sDevice)(PatchEdit.appConfig.getDevice(getDeviceNum()))).getTipsMsgFlag()==1 )
          // Information 
          JOptionPane.showMessageDialog(PatchEdit.instance,
                                        getDriverName()+"Driver:"+ DX7sStrings.MEMORY_PROTECTION_STRING,
                                        getDriverName()+"Driver",
                                        JOptionPane.INFORMATION_MESSAGE);
    }

    sendPatchWorker(p);
  };


  public void requestPatchDump(int bankNum, int patchNum)
  {
    // choose the desired MIDI transmit block (internal (1-32), internal (33-64))
    DX7sParamChanges.chXmitBlock(port, (byte)(channel+0x10), (byte)(bankNum));
      
    sysexRequestDump.send(port, (byte)(channel+0x20) );
  }


  public int getPatchStart(int patchNum)
  {
    int start=(51*patchNum);	// single-patch-size in bulked-dump-packed-format
    start+=16;			//sysex header

    return start;
  }


  public String getPatchName(Patch p,int patchNum)
  {
    int nameStart=getPatchStart(patchNum);
    nameStart+=31;                        //offset of name in patch data

    try
    {
      StringBuffer s= new StringBuffer(new String(p.sysex,nameStart,20,"US-ASCII")); // patchname size
      return s.toString();
    } catch (UnsupportedEncodingException ex) {return "-";}
  }


  public void setPatchName(Patch p,int patchNum, String name)
  {
    patchNameSize=20;
    patchNameStart=getPatchStart(patchNum)+31;

    if (name.length()<patchNameSize) name=name+"                       ";
    byte [] namebytes = new byte [64];

    try
    {
      namebytes=name.getBytes("US-ASCII");
      for (int i=0;i<patchNameSize;i++)
          p.sysex[patchNameStart+i]=namebytes[i];

    } catch (UnsupportedEncodingException ex) {return;}
  }


  public void putPatch(Patch bank,Patch p,int patchNum) //puts a patch into the bank, converting it as needed
  {
    if (!canHoldPatch(p))
    {
      JOptionPane.showMessageDialog(null, "This type of patch does not fit in to this type of bank.",getDriverName()+" Driver Error", JOptionPane.ERROR_MESSAGE);
      return;
    }


    for (int i=0; i<51; i++)
    {
      bank.sysex[getPatchStart(patchNum)+i]=(byte)(p.sysex[16+i]);
    }

    calculateChecksum(bank);
  }


  public Patch getPatch(Patch bank, int patchNum) //Gets a patch from the bank, converting it as needed
  {
    try
    {
      byte [] sysex=new byte[singleSize];

      // transform bulk-dump-packed-format to voice data
      sysex[  0]=(byte)0xf0;
      sysex[  1]=(byte)0x43;
      sysex[  2]=(byte)0x00;
      sysex[  3]=(byte)0x7e;
      sysex[  4]=(byte)0x00;
      sysex[  5]=(byte)0x3d;
      sysex[  6]=(byte)0x4c;    // "L"
      sysex[  7]=(byte)0x4d;    // "M"
      sysex[  8]=(byte)0x20;    // " "
      sysex[  9]=(byte)0x20;    // " "
      sysex[ 10]=(byte)0x38;    // "8"
      sysex[ 11]=(byte)0x39;    // "9"
      sysex[ 12]=(byte)0x37;    // "7"
      sysex[ 13]=(byte)0x33;    // "3"
      sysex[ 14]=(byte)0x50;    // "P"
      sysex[ 15]=(byte)0x45;    // "E"
      sysex[singleSize-1]=(byte)0xf7;


      for (int i=0; i<51; i++)
      {
        sysex[16+i]=(byte)(bank.sysex[getPatchStart(patchNum)+i]);
      }


      Patch p = new Patch(getDeviceNum(),sysex);        // single sysex
      PatchEdit.getDriver(p.deviceNum,p.driverNum).calculateChecksum(p);

      return p;
    }catch (Exception e) {ErrorMsg.reportError(manufacturer+" "+model,"Error in "+getDriverName(),e);return null;}
  }


  public Patch createNewPatch() // create a bank with 32 performance patches
  {
    byte [] sysex = new byte[trimSize];
    sysex[ 0]=(byte)0xf0;
    sysex[ 1]=(byte)0x43;
    sysex[ 2]=(byte)0x00;
    sysex[ 3]=(byte)0x7e;
    sysex[ 4]=(byte)0x0c;
    sysex[ 5]=(byte)0x6a;
    sysex[ 6]=(byte)0x4c;
    sysex[ 7]=(byte)0x4d;
    sysex[ 8]=(byte)0x20;
    sysex[ 9]=(byte)0x20;
    sysex[10]=(byte)0x38;
    sysex[11]=(byte)0x39;
    sysex[12]=(byte)0x37;
    sysex[13]=(byte)0x33;
    sysex[14]=(byte)0x50;
    sysex[15]=(byte)0x4d;
    sysex[trimSize-1]=(byte)0xf7;


    Patch v = new Patch(getDeviceNum(),DX7sConstants.INIT_PERFORMANCE);        // single sysex
    Patch p = new Patch(sysex,getDeviceNum(),getDriverNum());   // bank sysex

    for (int i=0;i<numPatches;i++)
        putPatch(p,v,i);
    calculateChecksum(p);

    return p;
  }

}
