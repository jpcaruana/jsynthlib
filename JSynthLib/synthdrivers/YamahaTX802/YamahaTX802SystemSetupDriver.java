/*
 * JSynthlib - "System Setup" Single Driver for Yamaha TX802
 * =========================================================
 * @author  Torsten Tittmann
 * file:    YamahaTX802SystemSetupDriver.java
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
import javax.swing.*;

public class YamahaTX802SystemSetupDriver extends Driver
{
  public YamahaTX802SystemSetupDriver()
  {
    manufacturer="Yamaha";
    model="TX802";
    patchType="System Setup";
    id="TX802";
    sysexID= "F0430*7E02114C4D2020383935325320";
    // inquiryID= NONE ;
    patchNameStart=0; // !!!! no patchName !!!!
    patchNameSize=0;  // !!!! no patchName !!!!
    deviceIDoffset=2;
    checksumOffset=279;
    checksumStart=6;
    checksumEnd=278;
    patchNumbers = TX802Constants.PATCH_NUMBERS_SYSTEM_SETUP;
    bankNumbers  = TX802Constants.BANK_NUMBERS_SYSTEM_SETUP;
    patchSize=281;
    trimSize=281;
    numSysexMsgs=1;         
    sysexRequestDump=new SysexHandler("F0 43 @@ 7E 4C 4D 20 20 38 39 35 32 53 20 F7");
    authors="Torsten Tittmann";

  }


  public void storePatch (Patch p, int bankNum,int patchNum)
  {
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

    sendPatchWorker (p);
  }


  public void requestPatchDump(int bankNum, int patchNum)
  {
    sysexRequestDump.send(port, (byte)(channel+0x20) );
  }


  public Patch createNewPatch()
  {
    Patch p = new Patch(TX802Constants.INIT_SYSTEM_SETUP,getDeviceNum(),getDriverNum());

    return p;
  }


  public JInternalFrame editPatch(Patch p)
  {
    if ( ((YamahaTX802Device)(PatchEdit.appConfig.getDevice(getDeviceNum()))).getSpinnerEditorFlag()==1 )
    {
      if ( (float)(Float.parseFloat(java.lang.System.getProperty("java.specification.version"))) >= (float)(1.4)  )
      {
	return new YamahaTX802SystemSetupEditor2(p); // use of JSpinner requires jdk>=1.4 !
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

	return new YamahaTX802SystemSetupEditor(p);
      }
    }
    else
	return new YamahaTX802SystemSetupEditor(p);
  }
}
