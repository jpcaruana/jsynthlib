/*
 * JSynthlib - "Voice" Single Driver for Yamaha DX7-II
 * ===================================================
 * @author  Torsten Tittmann
 * file:    YamahaDX7IIVoiceSingleDriver.java
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
import javax.swing.*;

public class YamahaDX7IIVoiceSingleDriver extends Driver
{
  public YamahaDX7IIVoiceSingleDriver()
  {
    manufacturer="Yamaha";
    model="DX7-II";
    patchType="Voice Single";
    id="DX7-II";
    sysexID= "F0430*00011B";
    // inquiryID= NONE ;
    patchNameStart=151;
    patchNameSize=10;
    deviceIDoffset=2;
    checksumOffset=161;
    checksumStart=6;
    checksumEnd=160;
    patchNumbers = DX7IIConstants.PATCH_NUMBERS_VOICE;
    bankNumbers  = DX7IIConstants.BANK_NUMBERS_SINGLE_VOICE;
    patchSize=163;
    trimSize=163;
    numSysexMsgs=1;         
    sysexRequestDump=new SysexHandler("F0 43 @@ 00 F7");
    authors="Torsten Tittmann";

  }


  public void storePatch (Patch p, int bankNum,int patchNum)
  {
    sendPatchWorker(p);

    if( ((YamahaDX7IIDevice)(PatchEdit.appConfig.getDevice(getDeviceNum()))).getTipsMsgFlag()==1 )
        // Information 
        JOptionPane.showMessageDialog(PatchEdit.instance,
                                      getDriverName()+"Driver:"+ DX7IIStrings.STORE_SINGLE_VOICE_STRING,
                                      getDriverName()+"Driver",
                                      JOptionPane.INFORMATION_MESSAGE);
  }


  public void requestPatchDump(int bankNum, int patchNum)
  {
    // keyswitch to voice mode
    DX7IIParamChanges.chVoiceMode(port, (byte)(channel+0x10));
    // 0-63 int voices, 64-127 cartridge voices
    setPatchNum(patchNum+32*bankNum);

    sysexRequestDump.send(port, (byte)(channel+0x20) );
  }


  public Patch createNewPatch()
  {
    Patch p = new Patch(DX7IIConstants.INIT_VOICE,getDeviceNum(),getDriverNum());

    return p;
  }


  public JInternalFrame editPatch(Patch p)
  {
    return new YamahaDX7IIVoiceEditor(p);
  }
}
