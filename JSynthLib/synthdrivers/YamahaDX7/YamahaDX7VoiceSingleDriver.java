/*
 * JSynthlib - "Voice" Single Driver for Yamaha DX7 Mark-I
 * =======================================================
 * @author  Torsten Tittmann
 * file:    YamahaDX7VoiceSingleDriver.java
 * date:    25.02.2003
 * @version 0.3
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
 *         23.08.2002 v0.1: first published release
 *         31.10.2002 v0.2: - driver name changed (YamahaDX7SingleDriver -> YamahaDX7SingleVoiceDriver)
 *                          - changed driver configuration access
 *                          - unnecessary code removed
 *         25.02.2003 v0.3: - driver name changed (YamahaDX7SingleVoiceDriver -> YamahaDX7VoiceSingleDriver)
 */

package synthdrivers.YamahaDX7;
import core.*;
import javax.swing.*;

public class YamahaDX7VoiceSingleDriver extends Driver
{
  public YamahaDX7VoiceSingleDriver()
  {
    manufacturer="Yamaha";
    model="DX7";
    patchType="Voice Single";
    id="DX7";
    sysexID= "F0430*00011B";
    // inquiryID= NONE ;
    patchNameStart=151;
    patchNameSize=10;
    deviceIDoffset=2;
    checksumOffset=161;
    checksumStart=6;
    checksumEnd=160;
    patchNumbers = DX7Constants.PATCH_NUMBERS_VOICE;
    bankNumbers  = DX7Constants.BANK_NUMBERS_SINGLE_VOICE;
    patchSize=163;
    trimSize=163;
    numSysexMsgs=1;         
    sysexRequestDump=new SysexHandler("F0 43 @@ 00 F7");	// theoretically, but not implemented
    authors="Torsten Tittmann";

  }


  public void sendPatch (Patch p)
  {
    if ( ((YamahaDX7Device)(PatchEdit.appConfig.getDevice(getDeviceNum()))).getSwOffMemProtFlag()==1 )
    {
      // switch off memory protection of internal voices
      DX7ParamChanges.swOffMemProt(port, (byte)(channel+0x10), (byte)(0x21), (byte)(0x25));
    }

    if ( ((YamahaDX7Device)(PatchEdit.appConfig.getDevice(getDeviceNum()))).getDX7sPBPflag()==1 )
    {
      // make Sys Info available
      DX7ParamChanges.mkSysInfoAvail(port, (byte)(channel+0x10));
    }

    sendPatchWorker (p);
  }


  public void storePatch (Patch p, int bankNum,int patchNum)
  {
    if ( ((YamahaDX7Device)(PatchEdit.appConfig.getDevice(getDeviceNum()))).getSwOffMemProtFlag()==1 )
    {
      // switch off memory protection of internal/cartridge voices
      DX7ParamChanges.swOffMemProt(port, (byte)(channel+0x10), (byte)(bankNum+0x21), (byte)(bankNum+0x25));
    }
    else
    {
      if ( ((YamahaDX7Device)(PatchEdit.appConfig.getDevice(getDeviceNum()))).getTipsMsgFlag()==1 )
        // Information
        JOptionPane.showMessageDialog(PatchEdit.instance,
                                      getDriverName()+"Driver:"+ DX7Strings.MEMORY_PROTECTION_STRING,
                                      getDriverName()+"Driver",
                                      JOptionPane.INFORMATION_MESSAGE);
    }

    if ( ((YamahaDX7Device)(PatchEdit.appConfig.getDevice(getDeviceNum()))).getDX7sPBPflag()==1 )
    {
      // make Sys Info available
      DX7ParamChanges.mkSysInfoAvail(port, (byte)(channel+0x10));
      //place patch in the edit buffer 
      sendPatchWorker(p);

      // internal memory or RAM cartridge?
      DX7ParamChanges.chBank(port, (byte)(channel+0x10), (byte)(bankNum+0x25));
      //start storing ...          (depress Store button)
      DX7ParamChanges.depressStore.send(port,(byte)(channel+0x10));
      //put patch in the patch number
      DX7ParamChanges.chPatch(port, (byte)(channel+0x10), (byte)(patchNum));
      //... finish storing         (release Store button)
      DX7ParamChanges.releaseStore.send(port,(byte)(channel+0x10));
    }
    else
    {
      if ( ((YamahaDX7Device)(PatchEdit.appConfig.getDevice(getDeviceNum()))).getTipsMsgFlag()==1 )
        // Information
        JOptionPane.showMessageDialog(PatchEdit.instance,
                                      getDriverName()+"Driver:"+ DX7Strings.RECEIVE_STRING,
                                      getDriverName()+"Driver",
                                      JOptionPane.INFORMATION_MESSAGE);

      sendPatchWorker(p);

      if ( ((YamahaDX7Device)(PatchEdit.appConfig.getDevice(getDeviceNum()))).getTipsMsgFlag()==1 )
        // Information
        JOptionPane.showMessageDialog(PatchEdit.instance,
                                      getDriverName()+"Driver:"+ DX7Strings.STORE_SINGLE_VOICE_STRING,
                                      getDriverName()+"Driver",
                                      JOptionPane.INFORMATION_MESSAGE);
    }
  }


  public void requestPatchDump(int bankNum, int patchNum)
  {
    if ( ((YamahaDX7Device)(PatchEdit.appConfig.getDevice(getDeviceNum()))).getDX7sPBPflag()==1 )
    {
      // make Sys Info available
      DX7ParamChanges.mkSysInfoAvail(port, (byte)(channel+0x10));
      // internal memory or RAM cartridge?
      DX7ParamChanges.chBank(port, (byte)(channel+0x10), (byte)(bankNum+0x25));
      // which patch do you want
      DX7ParamChanges.chPatch(port, (byte)(channel+0x10), (byte)(patchNum));
    }
    else
    {
      if ( ((YamahaDX7Device)(PatchEdit.appConfig.getDevice(getDeviceNum()))).getTipsMsgFlag()==1 )
        // Information
        JOptionPane.showMessageDialog(PatchEdit.instance,
                                      getDriverName()+"Driver:"+ DX7Strings.REQUEST_VOICE_STRING,
                                      "Get "+getDriverName()+"Patch",
                                      JOptionPane.WARNING_MESSAGE);

      byte buffer[] = new byte[256*1024];
      try {
            while (PatchEdit.MidiIn.messagesWaiting(inPort) > 0)
            PatchEdit.MidiIn.readMessage(inPort, buffer, 1024);
           } catch (Exception ex)
             {
               ErrorMsg.reportError("Error", "Error Clearing Midi In buffer.",ex);
             }
    }
  }


  public Patch createNewPatch()
  {
    Patch p = new Patch(DX7Constants.INIT_VOICE,getDeviceNum(),getDriverNum());

    return p;
  }


  public JInternalFrame editPatch(Patch p)
  {
    if ( ((YamahaDX7Device)(PatchEdit.appConfig.getDevice(getDeviceNum()))).getSwOffMemProtFlag()==1 )
    {
      // switch off memory protection of internal/cartridge voices
      DX7ParamChanges.swOffMemProt(port, (byte)(channel+0x10), (byte)(0x21), (byte)(0x25));
    }
    else
    {
      if ( ((YamahaDX7Device)(PatchEdit.appConfig.getDevice(getDeviceNum()))).getTipsMsgFlag()==1 )
        // Information
        JOptionPane.showMessageDialog(PatchEdit.instance,
                                      getDriverName()+"Driver:"+ DX7Strings.MEMORY_PROTECTION_STRING,
                                      getDriverName()+"Driver",
                                      JOptionPane.INFORMATION_MESSAGE);
    }

    if ( ((YamahaDX7Device)(PatchEdit.appConfig.getDevice(getDeviceNum()))).getDX7sPBPflag()==1 )
    {
      // make Sys Info available
      DX7ParamChanges.mkSysInfoAvail(port, (byte)(channel+0x10));
    }
    else
    {
      if ( ((YamahaDX7Device)(PatchEdit.appConfig.getDevice(getDeviceNum()))).getTipsMsgFlag()==1 )
        // Information
        JOptionPane.showMessageDialog(PatchEdit.instance,
                                      getDriverName()+"Driver:"+ DX7Strings.RECEIVE_STRING,
                                      getDriverName()+"Driver",
                                      JOptionPane.INFORMATION_MESSAGE);
    }

    return new YamahaDX7VoiceEditor(p);
  }
}
