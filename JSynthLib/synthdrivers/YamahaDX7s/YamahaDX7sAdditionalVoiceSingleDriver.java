/*
 * JSynthlib - "Additional Voice" Single Driver for Yamaha DX7s
 * ============================================================
 * @author  Torsten Tittmann
 * file:    YamahaDX7sSingleAdditionalVoiceDriver.java
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
import javax.swing.*;

public class YamahaDX7sAdditionalVoiceSingleDriver extends Driver
{
  public YamahaDX7sAdditionalVoiceSingleDriver()
  {
    manufacturer="Yamaha";
    model="DX7s";
    patchType="Additional Voice Single";
    id="DX7s";
    sysexID= "F0430*050031";
    // inquiryID= NONE ;
    patchNameStart=0; // !!!! no patchName !!!!
    patchNameSize=0;  // !!!! no patchName !!!!
    deviceIDoffset=2;
    checksumOffset=55;
    checksumStart=6;
    checksumEnd=54;
    patchNumbers = DX7sConstants.PATCH_NUMBERS_ADDITIONAL_VOICE;
    bankNumbers = DX7sConstants.BANK_NUMBERS_SINGLE_ADDITIONAL_VOICE;
    patchSize=57;
    trimSize=57;
    numSysexMsgs=1;         
    sysexRequestDump=new SysexHandler("f0 43 @@ 05 f7");
    authors="Torsten Tittmann";

  }


  public void storePatch (Patch p, int bankNum,int patchNum)
  {
    sendPatchWorker (p);

    if( ((YamahaDX7sDevice)(PatchEdit.appConfig.getDevice(getDeviceNum()))).getTipsMsgFlag()==1 )
          // Information
          JOptionPane.showMessageDialog(PatchEdit.instance,
                                        getDriverName()+"Driver:"+ DX7sStrings.STORE_SINGLE_ADDITIONAL_VOICE_STRING,
                                        getDriverName()+"Driver",
                                        JOptionPane.INFORMATION_MESSAGE);
  }


  public void requestPatchDump(int bankNum, int patchNum)
  {
    // keyswitch to voice mode
    DX7sParamChanges.chVoiceMode(port, (byte)(channel+0x10));
    // 0-63 int voices, 64-127 cartridge voices
    setPatchNum(patchNum+32*bankNum);

    sysexRequestDump.send(port, (byte)(channel+0x20) );
  }


  public Patch createNewPatch()
  {
    Patch p = new Patch(DX7sConstants.INIT_ADDITIONAL_VOICE,getDeviceNum(),getDriverNum());

    return p;
  }


  public JInternalFrame editPatch(Patch p)
  {
    return new YamahaDX7sAdditionalVoiceEditor(p);
  }
}
