/*
 * JSynthlib - "Performance" Single Driver for Yamaha DX7-II
 * =========================================================
 * @author  Torsten Tittmann
 * file:    YamahaDX7IIPerformanceSingleDriver.java
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

package synthdrivers.YamahaDX7II;
import core.*;
import javax.swing.*;

public class YamahaDX7IIPerformanceSingleDriver extends Driver
{
  public YamahaDX7IIPerformanceSingleDriver()
  {
    manufacturer="Yamaha";
    model="DX7-II";
    patchType="Performance Single";
    id="DX7-II";
    sysexID= "F0430*7E003d4c4d2020383937335045";
    // inquiryID= NONE ;
    patchNameStart=47;
    patchNameSize=20;
    deviceIDoffset=2;
    checksumOffset=67;
    checksumStart=6;
    checksumEnd=66;
    patchNumbers = DX7IIConstants.PATCH_NUMBERS_PERFORMANCE;
    bankNumbers  = DX7IIConstants.BANK_NUMBERS_PERFORMANCE;
    patchSize=69;
    trimSize=69;
    numSysexMsgs=1;         
    sysexRequestDump=new SysexHandler("f0 43 @@ 7e 4c 4d 20 20 38 39 37 33 50 45 f7");
    authors="Torsten Tittmann";

  }


  public void storePatch (Patch p, int bankNum,int patchNum)
  {
    sendPatchWorker (p);

    if( ((YamahaDX7IIDevice)(PatchEdit.appConfig.getDevice(getDeviceNum()))).getTipsMsgFlag()==1 )
        // Information
        JOptionPane.showMessageDialog(PatchEdit.instance,
                                      getDriverName()+"Driver:"+ DX7IIStrings.STORE_SINGLE_PERFORMANCE_STRING,
                                      getDriverName()+"Driver",
                                      JOptionPane.INFORMATION_MESSAGE);
  }


  public void requestPatchDump(int bankNum, int patchNum)
  {
    sysexRequestDump.send(port, (byte)(channel+0x20) );
  }


  public Patch createNewPatch()
  {
    Patch p = new Patch(DX7IIConstants.INIT_PERFORMANCE,getDeviceNum(),getDriverNum());

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
    return new YamahaDX7IIPerformanceEditor(p);
  }
}
