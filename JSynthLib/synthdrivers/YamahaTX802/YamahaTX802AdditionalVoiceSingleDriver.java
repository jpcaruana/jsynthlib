/*
 * JSynthlib - "Additional Voice" Single Driver Yamaha TX802
 * =========================================================
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
import javax.swing.*;

public class YamahaTX802AdditionalVoiceSingleDriver extends Driver
{
  public YamahaTX802AdditionalVoiceSingleDriver()
  {
    manufacturer="Yamaha";
    model="TX802";
    patchType="Additional Voice Single";
    id="TX802";
    sysexID= "F0430*050031";
    // inquiryID= NONE ;
    patchNameStart=0; // !!!! no patchName !!!!
    patchNameSize=0;  // !!!! no patchName !!!!
    deviceIDoffset=2;
    checksumOffset=55;
    checksumStart=6;
    checksumEnd=54;
    patchNumbers = TX802Constants.PATCH_NUMBERS_ADDITIONAL_VOICE;
    bankNumbers  = TX802Constants.BANK_NUMBERS_SINGLE_ADDITIONAL_VOICE;
    patchSize=57;
    trimSize=57;
    numSysexMsgs=1;         
    sysexRequestDump=new SysexHandler("f0 43 @@ 05 f7");
    authors="Torsten Tittmann";

  }


  public void storePatch (Patch p, int bankNum,int patchNum)
  {
    sendPatchWorker (p);

    if( ((YamahaTX802Device)(PatchEdit.appConfig.getDevice(getDeviceNum()))).getTipsMsgFlag()==1 )
         // Information
         JOptionPane.showMessageDialog(PatchEdit.instance,
                                       getDriverName()+"Driver:"+ TX802Strings.STORE_SINGLE_ADDITIONAL_VOICE_STRING,
                                       getDriverName()+"Driver",
                                       JOptionPane.INFORMATION_MESSAGE);
  }


  public void requestPatchDump(int bankNum, int patchNum)
  {
    // keyswitch to voice mode
    TX802ParamChanges.chVoiceMode(port, (byte)(channel+0x10));
    // 0-63 int voices
    setPatchNum(patchNum+32*bankNum);

    sysexRequestDump.send(port, (byte)(channel+0x20) );
  }


  public Patch createNewPatch()
  {
    Patch p = new Patch(TX802Constants.INIT_ADDITIONAL_VOICE,getDeviceNum(),getDriverNum());

    return p;
  }


  public JInternalFrame editPatch(Patch p)
  {
    return new YamahaTX802AdditionalVoiceEditor(p);
  }
}
