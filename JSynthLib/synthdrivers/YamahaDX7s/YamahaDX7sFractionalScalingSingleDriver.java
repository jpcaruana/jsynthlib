/*
 * JSynthlib - "Fractional Scaling" Single Driver for Yamaha DX7s
 * =======================================================================
 * @author  Torsten Tittmann
 * file:    YamahaDX7sFractionalScalingSingleDriver.java
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

public class YamahaDX7sFractionalScalingSingleDriver extends Driver
{

  public YamahaDX7sFractionalScalingSingleDriver()
  {
    manufacturer="Yamaha";
    model="DX7s";
    patchType="Fractional Scaling Single";
    id="DX7s";
    sysexID= "f0430*7e03764c4d2020464b53594520";
    sysexRequestDump=new SysexHandler("f0 43 @@ 7e 4c 4d 20 20  46 4b 53 59 45 20 f7");
    // inquiryID= NONE ;
    patchNameStart=0;	// !!!! no patchName !!!!
    patchNameSize=0;	// !!!! no patchName !!!!
    deviceIDoffset=2;
    checksumOffset=508;
    checksumStart=6;
    checksumEnd=507;
    bankNumbers  = DX7sConstants.BANK_NUMBERS_FRACTIONAL_SCALING;
    patchNumbers = DX7sConstants.PATCH_NUMBERS_FRACTIONAL_SCALING;
    patchSize=510;
    trimSize=510;
    numSysexMsgs=1;         
    authors="Torsten Tittmann";

  }


  public void storePatch (Patch p, int bankNum,int patchNum)
  {
    if( ((YamahaDX7sDevice)(PatchEdit.appConfig.getDevice(getDeviceNum()))).getTipsMsgFlag()==1 )
        // Information
        JOptionPane.showMessageDialog(PatchEdit.instance,
                                        getDriverName()+"Driver:"+ DX7sStrings.STORE_SINGLE_FRACTIONAL_SCALING_STRING,
                                        getDriverName()+"Driver",
                                        JOptionPane.INFORMATION_MESSAGE);

    sendPatchWorker (p);
  }


  public void requestPatchDump(int bankNum, int patchNum)
  {
    if( ((YamahaDX7sDevice)(PatchEdit.appConfig.getDevice(getDeviceNum()))).getTipsMsgFlag()==1 )
        // Information
        JOptionPane.showMessageDialog(PatchEdit.instance,
                                      getDriverName()+"Driver:"+ DX7sStrings.FRACTIONAL_SCALING_CARTRIDGE_STRING,
                                      getDriverName()+"Driver",
                                      JOptionPane.INFORMATION_MESSAGE);

    // keyswitch to voice mode
    DX7sParamChanges.chVoiceMode(port, (byte)(channel+0x10));
    // 0-63 int voices, 64-127 cartridge voices
    setPatchNum(patchNum+32*bankNum);

    sysexRequestDump.send(port, (byte)(channel+0x20) );
  }


  public Patch createNewPatch()
  {
    Patch p = new Patch(DX7sConstants.INIT_FRACTIONAL_SCALING,getDeviceNum(),getDriverNum());

    return p;
  }


  public JInternalFrame editPatch(Patch p)
  {
    if ( ((YamahaDX7sDevice)(PatchEdit.appConfig.getDevice(getDeviceNum()))).getSpinnerEditorFlag()==1 )
    {
	if ( (float)(Float.parseFloat(java.lang.System.getProperty("java.specification.version"))) >= (float)(1.4)  )
        {
	  return new YamahaDX7sFractionalScalingEditor2(p);	// use of JSpinner requires jdk>=1.4 !
	}
	else
	{
	  if ( ((YamahaDX7sDevice)(PatchEdit.appConfig.getDevice(getDeviceNum()))).getTipsMsgFlag()==1 )
	     // Information
      	     JOptionPane.showMessageDialog(PatchEdit.instance,
                                           getDriverName()+"Driver:"+
                                           DX7sStrings.JDK14_NEEDED_STRING,
                                           getDriverName()+"Driver",
                                           JOptionPane.INFORMATION_MESSAGE);

 	  return new YamahaDX7sFractionalScalingEditor(p);
	}
    }
    else
	return new YamahaDX7sFractionalScalingEditor(p);
  }
}
