/*
 * JSynthlib - "System Setup" Driver for Yamaha DX7-II
 * ===================================================
 * @author  Torsten Tittmann
 * file:    YamahaDX7IISystemSetupDriver.java
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

public class YamahaDX7IISystemSetupDriver extends Driver
{
  public YamahaDX7IISystemSetupDriver()
  {
    manufacturer="Yamaha";
    model="DX7-II";
    patchType="System Setup";
    id="DX7-II";
    sysexID= "F0430*7E005f4c4d2020383937335320";
    // inquiryID= NONE ;
    patchNameStart=0; // !!!! no patchName !!!!
    patchNameSize=0;  // !!!! no patchName !!!!
    deviceIDoffset=2;
    checksumOffset=101;
    checksumStart=6;
    checksumEnd=100;
    patchNumbers = DX7IIConstants.PATCH_NUMBERS_SYSTEM_SETUP;
    bankNumbers  = DX7IIConstants.BANK_NUMBERS_SYSTEM_SETUP;
    patchSize=103;
    trimSize=103;
    numSysexMsgs=1;         
    sysexRequestDump=new SysexHandler("f0 43 @@ 7e 4c 4d 20 20 38 39 37 33 53 20 f7");
    authors="Torsten Tittmann";

  }


  public void storePatch (Patch p, int bankNum,int patchNum)
  {
    if ( ((YamahaDX7IIDevice)(PatchEdit.appConfig.getDevice(getDeviceNum()))).getSwOffMemProtFlag()==1 )
    {
      // switch off memory protection (internal+cartridge!)
      DX7IIParamChanges.swOffMemProt(port, (byte)(channel+0x10), (byte)0 );
    }
    else
    {
      if( ((YamahaDX7IIDevice)(PatchEdit.appConfig.getDevice(getDeviceNum()))).getTipsMsgFlag()==1 )
          // Information
          JOptionPane.showMessageDialog(PatchEdit.instance,
                                        getDriverName()+"Driver:"+ DX7IIStrings.MEMORY_PROTECTION_STRING,
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
    Patch p = new Patch(DX7IIConstants.INIT_SYSTEM_SETUP,getDeviceNum(),getDriverNum());

    return p;
  }


  public JInternalFrame editPatch(Patch p)
  {
    if ( ((YamahaDX7IIDevice)(PatchEdit.appConfig.getDevice(getDeviceNum()))).getSpinnerEditorFlag()==1 )
    {
      if ( (float)(Float.parseFloat(java.lang.System.getProperty("java.specification.version"))) >= (float)(1.4)  )
      {
	return new YamahaDX7IISystemSetupEditor2(p); // use of JSpinner requires jdk>=1.4 !
      }
      else
      {
        if ( ((YamahaDX7IIDevice)(PatchEdit.appConfig.getDevice(getDeviceNum()))).getTipsMsgFlag()==1 )
          // Information
          JOptionPane.showMessageDialog(PatchEdit.instance,
                                        getDriverName()+"Driver:"+
                                        DX7IIStrings.JDK14_NEEDED_STRING,
                                        getDriverName()+"Driver",
                                        JOptionPane.INFORMATION_MESSAGE);

	return new YamahaDX7IISystemSetupEditor(p);
      }
    }
    else
	return new YamahaDX7IISystemSetupEditor(p);
  }
}
