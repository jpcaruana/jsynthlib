/*
 * JSynthlib - "Micro Tuning" Bank Driver for Yamaha TX802
 * =======================================================
 * @author  Torsten Tittmann
 * file:    YamahaTX802MicroTuningBankDriver.java
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

package synthdrivers.YamahaTX802;
import core.*;
import java.io.*;
import javax.swing.*;

public class YamahaTX802MicroTuningBankDriver extends BankDriver
{
  public YamahaTX802MicroTuningBankDriver()
  {
    manufacturer="Yamaha";
    model="TX802";
    patchType="Micro Tuning Bank";
    id="TX802";
    authors="Torsten Tittmann";
    sysexID="F0430*7e020a4c4d20204d4352594320";
    sysexRequestDump=new SysexHandler("f0 43 @@ 7e 4c 4d 20 20  4d 43 52 59 43 20 f7 ");
    deviceIDoffset=2;
    patchNameStart=0;
    patchNameSize=0;
    bankNumbers  = TX802Constants.BANK_NUMBERS_MICRO_TUNING_BANK;
    patchNumbers = TX802Constants.PATCH_NUMBERS_MICRO_TUNING_BANK;
    numPatches=patchNumbers.length;
    numColumns=7;
    singleSysexID="F0430*7E020A4C4D20204d4352594***";
    singleSize=274;
    //checksumOffset=16950;     // This patch doesn't uses an over-all checksum for bank bulk data
    //checksumStart=6;
    //checksumEnd=16949;
    numSysexMsgs=1;
    trimSize=16952;
  }


  public void storePatch (Patch p, int bankNum,int patchNum)
  {
    if( ((YamahaTX802Device)(PatchEdit.appConfig.getDevice(getDeviceNum()))).getTipsMsgFlag()==1 )
          // Information
          JOptionPane.showMessageDialog(PatchEdit.instance,
                                        getDriverName()+"Driver:"+ TX802Strings.MICRO_TUNING_CARTRIDGE_STRING,
                                        getDriverName()+"Driver",
                                        JOptionPane.INFORMATION_MESSAGE);
      
    if ( ((YamahaTX802Device)(PatchEdit.appConfig.getDevice(getDeviceNum()))).getSwOffMemProtFlag()==1 )
    {
      // switch off memory protection (internal+cartridge!)
      TX802ParamChanges.swOffMemProt(port, (byte)(channel+0x10), (byte)0 );
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
    if( ((YamahaTX802Device)(PatchEdit.appConfig.getDevice(getDeviceNum()))).getTipsMsgFlag()==1 )
          // Information
          JOptionPane.showMessageDialog(PatchEdit.instance,
                                        getDriverName()+"Driver:"+ TX802Strings.MICRO_TUNING_CARTRIDGE_STRING,
                                        getDriverName()+"Driver",
                                        JOptionPane.INFORMATION_MESSAGE);
    
    sysexRequestDump.send(port, (byte)(channel+0x20) );
  }


  public void calculateChecksum(Patch p)
  {
    // This patch doesn't uses an over-all checksum for bank bulk data
  }


  public int getPatchStart(int patchNum)
  {
    int start=(269*patchNum); // single-patch-size in bulked-dump-packed-format
    start+=4;                 //sysex header

    return start;
  }


  public void putPatch(Patch bank,Patch p,int patchNum) //puts a patch into the bank, converting it as needed
  {
    if (!canHoldPatch(p))
    {
      JOptionPane.showMessageDialog(null, "This type of patch does not fit in to this type of bank.",getDriverName()+" Driver Error", JOptionPane.ERROR_MESSAGE);
      return;
    }

    // Transform Voice Data to Bulk Dump Packed Format
    bank.sysex[getPatchStart(patchNum)+ 0   ]=(byte)(0x02);     // Byte Count MSB
    bank.sysex[getPatchStart(patchNum)+ 1   ]=(byte)(0x0a);     // Byte Count LSB
    bank.sysex[getPatchStart(patchNum)+ 2   ]=(byte)(0x4c);     // "L"
    bank.sysex[getPatchStart(patchNum)+ 3   ]=(byte)(0x4d);     // "M"
    bank.sysex[getPatchStart(patchNum)+ 4   ]=(byte)(0x20);     // " "
    bank.sysex[getPatchStart(patchNum)+ 5   ]=(byte)(0x20);     // " "
    bank.sysex[getPatchStart(patchNum)+ 6   ]=(byte)(0x4d);     // "M"
    bank.sysex[getPatchStart(patchNum)+ 7   ]=(byte)(0x43);     // "C"
    bank.sysex[getPatchStart(patchNum)+ 8   ]=(byte)(0x52);     // "R"
    bank.sysex[getPatchStart(patchNum)+ 9   ]=(byte)(0x59);     // "Y"
    bank.sysex[getPatchStart(patchNum)+10   ]=(byte)(0x43);     // "C"
    bank.sysex[getPatchStart(patchNum)+11   ]=(byte)(0x20);     // " "


    for (int i=0; i<256; i++)
    {
      bank.sysex[getPatchStart(patchNum)+12+i]=(byte)p.sysex[16+i];
    }

    calculateChecksum(bank,getPatchStart(patchNum)+2,getPatchStart(patchNum)+12+256-1,getPatchStart(patchNum)+12+256);	// Calculate checkSum of single bulk data
  }


  public Patch getPatch(Patch bank, int patchNum) //Gets a patch from the bank, converting it as needed
  {
    try
    {
      byte [] sysex=new byte[singleSize];

      // transform bulk-dump-packed-format to voice data (Edit Buffer!)
      sysex[  0]=(byte)0xf0;
      sysex[  1]=(byte)0x43;
      sysex[  2]=(byte)0x00;
      sysex[  3]=(byte)0x7e;
      sysex[  4]=(byte)0x02;
      sysex[  5]=(byte)0x0a;
      sysex[  6]=(byte)0x4c;    // "L"
      sysex[  7]=(byte)0x4d;    // "M"
      sysex[  8]=(byte)0x20;    // " "
      sysex[  9]=(byte)0x20;    // " "
      sysex[ 10]=(byte)0x4d;    // "M"
      sysex[ 11]=(byte)0x43;    // "C"
      sysex[ 12]=(byte)0x52;    // "R"
      sysex[ 13]=(byte)0x59;    // "Y"
      sysex[ 14]=(byte)0x45;    // "E"
      sysex[ 15]=(byte)0x20;    // " "
      sysex[singleSize-1]=(byte)0xf7;


      for (int i=0; i<256; i++)
      {
        sysex[16+i]=(byte)(bank.sysex[getPatchStart(patchNum)+12+i]);
      }

      Patch p = new Patch(getDeviceNum(),sysex);        // single sysex
      PatchEdit.getDriver(p.deviceNum,p.driverNum).calculateChecksum(p);

      return p;
    }catch (Exception e) {ErrorMsg.reportError(manufacturer+" "+model,"Error in "+getDriverName(),e);return null;}
  }


  public Patch createNewPatch() // create a bank with 64 micro tuning patches
  {
    byte [] sysex = new byte[trimSize];
    sysex[ 0]=(byte)0xF0;
    sysex[ 1]=(byte)0x43;
    sysex[ 2]=(byte)0x00;
    sysex[ 3]=(byte)0x7e;
    sysex[trimSize-1]=(byte)0xF7;

    Patch v = new Patch(getDeviceNum(),TX802Constants.INIT_MICRO_TUNING);      // single sysex
    Patch p = new Patch(sysex,getDeviceNum(),getDriverNum());   // bank sysex

    for (int i=0;i<numPatches;i++)
        putPatch(p,v,i);

    return p;
  }

}
