/*
 * JSynthlib - "Micro Tuning" Single Driver for Yamaha DX7-II
 * ==========================================================
 * @author  Torsten Tittmann
 * file:    YamahaDX7IIMicroTuningSingleDriver.java
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

public class YamahaDX7IIMicroTuningSingleDriver extends Driver
{
  protected final static SysexHandler SYSEX_REQUEST_DUMP[]= {new SysexHandler("F0 43 @@ 7E 4C 4D 20 20 4D 43 52 59 45 20 F7"),
							     new SysexHandler("F0 43 @@ 7E 4C 4D 20 20 4D 43 52 59 4D 00 F7"),
							     new SysexHandler("F0 43 @@ 7E 4C 4D 20 20 4D 43 52 59 4D 01 F7")};

  public YamahaDX7IIMicroTuningSingleDriver()
  {
    manufacturer="Yamaha";
    model="DX7-II";
    patchType="Micro Tuning Single";
    id="DX7-II";
    sysexID= "F0430*7E020A4C4D20204d4352594***";
    // inquiryID= NONE ;
    patchNameStart=0;	// !!!! no patchName !!!!
    patchNameSize=0;	// !!!! no patchName !!!!
    deviceIDoffset=2;
    checksumOffset=272;
    checksumStart=6;
    checksumEnd=271;
    patchNumbers = DX7IIConstants.PATCH_NUMBERS_MICRO_TUNING_SINGLE;
    bankNumbers  = DX7IIConstants.BANK_NUMBERS_MICRO_TUNING_SINGLE;
    patchSize=274;
    trimSize=274;
    numSysexMsgs=1;         
    authors="Torsten Tittmann";

  }


  public void sendPatch (Patch p)
  {
    // This is an edit buffer patch!
    p.sysex[14]=(byte)(0x45);
    p.sysex[15]=(byte)(0x20);

    sendPatchWorker (p);
  }


  public void storePatch (Patch p, int bankNum,int patchNum)
  {
    // Is it necessary to switch off Memory Protection for edit buffer and/or User 1,2?

    if (patchNum==0)	// edit buffer
    {
      p.sysex[14]=(byte)(0x45);
      p.sysex[15]=(byte)(0x20);
    }
    else		// User 1,2
    {
      p.sysex[14]=(byte)(0x4D);
      p.sysex[15]=(byte)(0x00+patchNum-1);
    }
    sendPatchWorker (p);
  }


  public void requestPatchDump(int bankNum, int patchNum)
  {
    SYSEX_REQUEST_DUMP[patchNum].send(port, (byte)(channel+0x20) );
  }


  public Patch createNewPatch()
  {
    Patch p = new Patch(DX7IIConstants.INIT_MICRO_TUNING,getDeviceNum(),getDriverNum());

    return p;
  }


  public JInternalFrame editPatch(Patch p)
  {
    if ( ((YamahaDX7IIDevice)(PatchEdit.appConfig.getDevice(getDeviceNum()))).getSpinnerEditorFlag()==1 )
    {
      if ( (float)(Float.parseFloat(java.lang.System.getProperty("java.specification.version"))) >= (float)(1.4)  )
      {
	return new YamahaDX7IIMicroTuningEditor2(p);	// use of JSpinner requires jdk>=1.4 !
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

	return new YamahaDX7IIMicroTuningEditor(p);
      }
    }
    else
      return new YamahaDX7IIMicroTuningEditor(p);
  }
}
