/*
 * JSynthlib - "Voice" Single Driver for Yamaha TX7
 * ================================================
 * @author  Torsten Tittmann
 * file:    YamahaTX7VoiceSingleDriver.java
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
import javax.swing.*;

public class YamahaTX7VoiceSingleDriver extends Driver
{
  public YamahaTX7VoiceSingleDriver()
  {
    manufacturer="Yamaha";
    model="TX7";
    patchType="Voice Single";
    id="TX7";
    sysexID= "F0430*00011B";
    // inquiryID= NONE ;
    patchNameStart=151;
    patchNameSize=10;
    deviceIDoffset=2;
    checksumOffset=161;
    checksumStart=6;
    checksumEnd=160;
    patchNumbers = TX7Constants.PATCH_NUMBERS_VOICE;
    bankNumbers  = TX7Constants.BANK_NUMBERS_SINGLE_VOICE;
    patchSize=163;
    trimSize=163;
    numSysexMsgs=1;         
    sysexRequestDump=new SysexHandler("F0 43 @@ 00 F7");
    authors="Torsten Tittmann";

  }


  public void storePatch (Patch p, int bankNum,int patchNum)
  {
    sendPatchWorker(p);

    if( ((YamahaTX7Device)(PatchEdit.appConfig.getDevice(getDeviceNum()))).getTipsMsgFlag()==1 )
          // Information
          JOptionPane.showMessageDialog(PatchEdit.instance,
                                        getDriverName()+"Driver:"+ TX7Strings.STORE_SINGLE_VOICE_STRING,
                                        getDriverName()+"Driver",
                                        JOptionPane.INFORMATION_MESSAGE);
  }


  public void requestPatchDump(int bankNum, int patchNum)
  {
    setPatchNum (patchNum);

    sysexRequestDump.send(port, (byte)(channel+0x20) );
  }


  public Patch createNewPatch()
  {
    Patch p = new Patch(TX7Constants.INIT_VOICE,getDeviceNum(),getDriverNum());

    return p;
  }


  public JInternalFrame editPatch(Patch p)
  {
    return new YamahaTX7VoiceEditor(p);
  }
}
