/*
 * JSynthlib - "Performance" Single Driver for Yamaha TX802
 * ========================================================
 * @author  Torsten Tittmann
 * file:    YamahaTX802PerformanceSingleDriver.java
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

public class YamahaTX802PerformanceSingleDriver extends Driver
{
  public YamahaTX802PerformanceSingleDriver()
  {
    manufacturer="Yamaha";
    model="TX802";
    patchType="Performance Single";
    id="TX802";
    sysexID= "F0430*7E01684C4D2020383935325045";
    // inquiryID= NONE ;
    patchNameStart=208;
    patchNameSize=40;
    deviceIDoffset=2;
    checksumOffset=248;
    checksumStart=6;
    checksumEnd=247;
    patchNumbers = TX802Constants.PATCH_NUMBERS_PERFORMANCE;	
    bankNumbers  = TX802Constants.BANK_NUMBERS_PERFORMANCE;
    patchSize=250;
    trimSize=250;
    numSysexMsgs=1;         
    sysexRequestDump=new SysexHandler("F0 43 @@ 7E 4C 4D 20 20 38 39 35 32 50 45 F7");
    authors="Torsten Tittmann";

  }


  public void storePatch (Patch p, int bankNum,int patchNum)
  {
    sendPatchWorker (p);

    if ( ((YamahaTX802Device)(PatchEdit.appConfig.getDevice(getDeviceNum()))).getTipsMsgFlag()==1 )
       // Information 
       JOptionPane.showMessageDialog(PatchEdit.instance,
                                     getDriverName()+"Driver:"+ TX802Strings.STORE_SINGLE_PERFORMANCE_STRING,
                                     getDriverName()+"Driver",
                                     JOptionPane.INFORMATION_MESSAGE);
  }


  public void requestPatchDump(int bankNum, int patchNum)
  {
    sysexRequestDump.send(port, (byte)(channel+0x20) );
  }


  public Patch createNewPatch()
  {
    Patch p = new Patch(TX802Constants.INIT_PERFORMANCE,getDeviceNum(),getDriverNum());

    return p;
  }


  public JInternalFrame editPatch(Patch p)
  {
    return new YamahaTX802PerformanceEditor(p);
  }


  public String getPatchName (Patch p)
  {
    try {
          byte []b = new byte[20];
          for (int i=0; i < b.length; i++)
          {
            b[i] =(byte)( TX802ParamChanges.AsciiHex2Value(p.sysex[16+2*(96+i)  ])*16 +
                          TX802ParamChanges.AsciiHex2Value(p.sysex[16+2*(96+i)+1]) );
          }
          StringBuffer s= new StringBuffer(new String(b,0,20,"US-ASCII"));

          return s.toString();
        } catch (Exception ex) {return "-";}

  }


  public void setPatchName (Patch p, String name)
  {
    byte [] namebytes = new byte[20];
    try{
         if (name.length()<20) name=name+"                    ";
         namebytes=name.getBytes("US-ASCII");

         for (int i=0; i < 20; i++)
         {
           p.sysex[16+2*(96+i)  ] = (byte)(TX802ParamChanges.Value2AsciiHexHigh(namebytes[i]));
           p.sysex[16+2*(96+i)+1] = (byte)(TX802ParamChanges.Value2AsciiHexLow( namebytes[i]));
         }
       }catch (Exception e) {}
    calculateChecksum (p);
  }

}
