/*
 * JSynthlib - "Fractional Scaling" Single Driver for Yamaha TX802
 * ===============================================================
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

public class YamahaTX802FractionalScalingSingleDriver extends Driver
{

  public YamahaTX802FractionalScalingSingleDriver()
  {
    manufacturer="Yamaha";
    model="TX802";
    patchType="Fractional Scaling Single";
    id="TX802";
    sysexID= "f0430*7e03764c4d2020464b53594520";
    sysexRequestDump=new SysexHandler("f0 43 @@ 7e 4c 4d 20 20  46 4b 53 59 45 20 f7");
    // inquiryID= NONE ;
    patchNameStart=0;	// !!!! no patchName !!!!
    patchNameSize=0;	// !!!! no patchName !!!!
    deviceIDoffset=2;
    checksumOffset=508;
    checksumStart=6;
    checksumEnd=507;
    bankNumbers  = TX802Constants.BANK_NUMBERS_FRACTIONAL_SCALING;
    patchNumbers = TX802Constants.PATCH_NUMBERS_FRACTIONAL_SCALING;
    patchSize=510;
    trimSize=510;
    numSysexMsgs=1;         
    authors="Torsten Tittmann";

  }


  public void storePatch (Patch p, int bankNum,int patchNum)
  {
    if( ((YamahaTX802Device)(PatchEdit.appConfig.getDevice(getDeviceNum()))).getTipsMsgFlag()==1 )
        // Information
        JOptionPane.showMessageDialog(PatchEdit.instance,
                                      getDriverName()+"Driver:"+ TX802Strings.STORE_SINGLE_FRACTIONAL_SCALING_STRING,
                                      getDriverName()+"Driver",
                                      JOptionPane.INFORMATION_MESSAGE);

    sendPatchWorker (p);
  }


  public void requestPatchDump(int bankNum, int patchNum)
  {
    if( ((YamahaTX802Device)(PatchEdit.appConfig.getDevice(getDeviceNum()))).getTipsMsgFlag()==1 )
        // Information
        JOptionPane.showMessageDialog(PatchEdit.instance,
                                      getDriverName()+"Driver:"+ TX802Strings.FRACTIONAL_SCALING_CARTRIDGE_STRING,
                                      getDriverName()+"Driver",
                                      JOptionPane.INFORMATION_MESSAGE);

    // keyswitch to voice mode
    TX802ParamChanges.chVoiceMode(port, (byte)(channel+0x10));
    // 0-63 int voices
    setPatchNum(patchNum+32*bankNum);

    sysexRequestDump.send(port, (byte)(channel+0x20) );
  }


  public Patch createNewPatch()
  {
    Patch p = new Patch(TX802Constants.INIT_FRACTIONAL_SCALING,getDeviceNum(),getDriverNum());

    return p;
  }


  public JInternalFrame editPatch(Patch p)
  {
    if ( ((YamahaTX802Device)(PatchEdit.appConfig.getDevice(getDeviceNum()))).getSpinnerEditorFlag()==1 )
    {
	if ( (float)(Float.parseFloat(java.lang.System.getProperty("java.specification.version"))) >= (float)(1.4)  )
        {
	  return new YamahaTX802FractionalScalingEditor2(p);	// use of JSpinner requires jdk>=1.4 !
	}
	else
	{
	  if ( ((YamahaTX802Device)(PatchEdit.appConfig.getDevice(getDeviceNum()))).getTipsMsgFlag()==1 )
	     // Information
      	     JOptionPane.showMessageDialog(PatchEdit.instance,
                                           getDriverName()+"Driver:"+
                                           TX802Strings.JDK14_NEEDED_STRING,
                                           getDriverName()+"Driver",
                                           JOptionPane.INFORMATION_MESSAGE);

 	  return new YamahaTX802FractionalScalingEditor(p);
	}
    }
    else
	return new YamahaTX802FractionalScalingEditor(p);
  }
}
