/*
 * JSynthlib - "Performance" Single Driver for Yamaha DX7 Mark-I
 * =============================================================
 * @author  Torsten Tittmann
 * file:    YamahaDX7PerformanceSingleDriver.java
 * date:    25.02.2003
 * @version 0.2
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
 *         31.10.2002 v0.1: first published release
 *         25.02.2003 v0.2: - driver name changed (YamahaTX7SinglePerformanceDriver -> YamahaTX7PerformanceSingleDriver)
 *
 */

package synthdrivers.YamahaDX7;
import core.*;
import javax.swing.*;

public class YamahaDX7PerformanceSingleDriver extends Driver
{
  public YamahaDX7PerformanceSingleDriver()
  {
    manufacturer="Yamaha";
    model="DX7";
    patchType="Performance Single";
    id="DX7";
    sysexID= "F0430*01005E";
    // inquiryID= NONE ;
    patchNameStart=70;
    patchNameSize=30;
    deviceIDoffset=2;
    checksumOffset=100;
    checksumStart=6;
    checksumEnd=99;
    bankNumbers  = DX7Constants.BANK_NUMBERS_PERFORMANCE;
    patchNumbers = DX7Constants.PATCH_NUMBERS_PERFORMANCE;
    patchSize=102;
    trimSize=102;
    numSysexMsgs=1;         
    sysexRequestDump=new SysexHandler("F0 43 @@ 01 F7"); // theoretically, but not implemented
    authors="Torsten Tittmann";

  }


  public void storePatch (Patch p, int bankNum,int patchNum)
  {
    if ( ((YamahaDX7Device)(PatchEdit.appConfig.getDevice(getDeviceNum()))).getTipsMsgFlag()==1 )
      // Information 
      JOptionPane.showMessageDialog(PatchEdit.instance,
                                    getDriverName()+"Driver:"+ DX7Strings.STORE_SINGLE_PERFORMANCE_STRING,
                                    getDriverName()+"Driver",
                                    JOptionPane.INFORMATION_MESSAGE);

      sendPatchWorker (p);
  }



  public void requestPatchDump(int bankNum, int patchNum)
  {
    if ( ((YamahaDX7Device)(PatchEdit.appConfig.getDevice(getDeviceNum()))).getTipsMsgFlag()==1 )
      // Information 
      JOptionPane.showMessageDialog(PatchEdit.instance,
                                    getDriverName()+"Driver:"+ DX7Strings.PERFORMANCE_STRING,
                                    getDriverName()+"Driver",
                                    JOptionPane.INFORMATION_MESSAGE);
  }


  public Patch createNewPatch()
  {
    Patch p = new Patch(DX7Constants.INIT_PERFORMANCE,getDeviceNum(),getDriverNum());

    return p;
  }

  // needed because the longer patchName!
  public void setPatchName (Patch p, String name)
  {
    if (patchNameSize==0)
    {
      ErrorMsg.reportError ("Error", "The Driver for this patch does not support Patch Name Editing.");
      return;
    }

    if (name.length ()<patchNameSize) name=name+"                              ";

    byte [] namebytes = new byte [64];
    try
    {
      namebytes=name.getBytes ("US-ASCII");

      for (int i=0;i<patchNameSize;i++)
           p.sysex[patchNameStart+i]=namebytes[i];

    } catch (Exception e){}

    calculateChecksum (p);
  }



  public JInternalFrame editPatch(Patch p)
  {
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
                                      getDriverName()+"Driver:"+ DX7Strings.PERFORMANCE_EDITOR_STRING,
                                      getDriverName()+"Driver",
                                      JOptionPane.INFORMATION_MESSAGE);
    }

    return new YamahaDX7PerformanceEditor(p);
  }
}
