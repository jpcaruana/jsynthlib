/*
 * JSynthlib-SingleDriver for Yamaha DX7 Mark-I
 * (with system ROM V 1.8 from October 24th 1985 - article no. IG114690)
 * =====================================================================
 * @author  Torsten Tittmann
 * file:    YamahaDX7SingleDriver.java
 * date:    23.08.2002
 * @version 0.1
 *
 * Copyright (C) 2002  Torsten.Tittmann@t-online.de
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
 */

package synthdrivers.YamahaDX7;
import core.*;
import javax.swing.*;

public class YamahaDX7SingleDriver extends Driver
{
  public YamahaDX7SingleDriver()
  {
    manufacturer="Yamaha";
    model="DX7";
    patchType="Single";
    id="DX7";
    sysexID= "F0430*00011B";
    // inquiryID= NONE ;
    patchNameStart=151;
    patchNameSize=10;
    deviceIDoffset=2;
    checksumOffset=161;
    checksumStart=6;
    checksumEnd=160;
    patchNumbers=new String[] {"01","02","03","04","05","06","07","08",
                               "09","10","11","12","13","14","15","16",
                               "17","18","19","20","21","22","23","24",
                               "25","26","27","28","29","30","31","32"};
    bankNumbers =new String[] {"Internal", "Cartridge"};
    patchSize=163;
    trimSize=163;
    numSysexMsgs=1;         
    sysexRequestDump=new SysexHandler("F0 43 @@ 00 F7"); // Dump Request Sysex of TX7 to request a single voice dump from the edit buffer!
    authors="Torsten Tittmann";

  }


  public void sendPatch (Patch p)
  {
    if ( ((YamahaDX7Device)(device)).whichSynth.compareTo("DX7")==0 )
    {
      if ( ((YamahaDX7Device)(device)).sPBPval==1 ) // DX7 Remote Control
      {
        // make Sys Info available
        ((YamahaDX7Device)(device)).mkDX7SysInfoAvail(port, (byte)(channel+0x10));
        // switch off memory protection of internal voices (edit buffer)
        ((YamahaDX7Device)(device)).swOffDX7MemProt(port, (byte)(channel+0x10), (byte)0x21, (byte)0x25);

        sendPatchWorker (p);
      }
      else // DX7 manually control
      {
        sendPatchWorker (p);
      }
    }
    else if ( ((YamahaDX7Device)(device)).whichSynth.compareTo("TX7")==0 )
    {
      if ( ((YamahaDX7Device)(device)).sPBPval==1 ) // TX7 Remote Control
      {
        // switch off TX7 memory protection (is it realy necessary?)
        ((YamahaDX7Device)(device)).swOffTX7MemProt.send(port, (byte)(channel+0x10)); // TX7 function parameter change

        sendPatchWorker (p);
      }
      else // TX7 manually control
      {
        sendPatchWorker (p);
      }
    }
  }


  public void storePatch (Patch p, int bankNum,int patchNum)
  {
    if ( ((YamahaDX7Device)(device)).whichSynth.compareTo("DX7")==0 )
    {
      if ( ((YamahaDX7Device)(device)).sPBPval==1 ) // DX7 Remote Control
      {
        // make Sys Info available
        ((YamahaDX7Device)(device)).mkDX7SysInfoAvail(port, (byte)(channel+0x10));
        // switch off memory protection of internal/cartridge voices
        ((YamahaDX7Device)(device)).swOffDX7MemProt(port, (byte)(channel+0x10), (byte)(bankNum+0x21), (byte)(bankNum+0x25));

        //place patch in the edit buffer 
        sendPatchWorker(p);

        // internal memory or RAM cartridge?
        ((YamahaDX7Device)(device)).chDX7Bank(port, (byte)(channel+0x10), (byte)(bankNum+0x25));
        //start storing ...
        ((YamahaDX7Device)(device)).depressDX7Store.send(port,(byte)(channel+0x10));
        //put patch in the patch number
        ((YamahaDX7Device)(device)).chDX7Patch(port, (byte)(channel+0x10), (byte)(patchNum));
        //... finish storing
        ((YamahaDX7Device)(device)).releaseDX7Store.send(port,(byte)(channel+0x10));
      }
      else // DX7 manually control
      {
        if ( ((YamahaDX7Device)(device)).sPBPmsgVal==1 )
          // Informations about DX7 patch receiving
          JOptionPane.showMessageDialog(PatchEdit.instance,
                                        getDriverName()+"Driver:"+((YamahaDX7Device)(device)).dx7ReceiveString,
                                        getDriverName()+"Driver",
                                        JOptionPane.INFORMATION_MESSAGE);

        sendPatchWorker(p);

        if ( ((YamahaDX7Device)(device)).sPBPmsgVal==1 )
          // Informations about manually storing of a voice patch
          JOptionPane.showMessageDialog(PatchEdit.instance,
                                        getDriverName()+"Driver:"+((YamahaDX7Device)(device)).dx7StoreString,
                                        getDriverName()+"Driver",
                                        JOptionPane.INFORMATION_MESSAGE);
      }
    }
    else if ( ((YamahaDX7Device)(device)).whichSynth.compareTo("TX7")==0 )
    {
      if ( ((YamahaDX7Device)(device)).sPBPval==1 ) // TX7 Remote Control
      {
        // switch off TX7 memory protection (is it realy necessary?)
        ((YamahaDX7Device)(device)).swOffTX7MemProt.send(port, (byte)(channel+0x10)); // TX7 function parameter change

        sendPatchWorker(p);

        if( ((YamahaDX7Device)(device)).sPBPmsgVal==1 )
            // Informations about storing of a voice patch
            JOptionPane.showMessageDialog(PatchEdit.instance,
                                          getDriverName()+"Driver:"+((YamahaDX7Device)(device)).tx7StoreString,
                                          getDriverName()+"Driver",
                                          JOptionPane.INFORMATION_MESSAGE);
      }
      else // TX7 manually control
      {
        if( ((YamahaDX7Device)(device)).sPBPmsgVal==1 )
            // Informations about TX7 patch receiving
            JOptionPane.showMessageDialog(PatchEdit.instance,
                                          getDriverName()+"Driver:"+((YamahaDX7Device)(device)).tx7ReceiveString,
                                          getDriverName()+"Driver",
                                          JOptionPane.INFORMATION_MESSAGE);

        sendPatchWorker(p);

        if( ((YamahaDX7Device)(device)).sPBPmsgVal==1 )
            // Informations about storing of a voice patch
            JOptionPane.showMessageDialog(PatchEdit.instance,
                                          getDriverName()+"Driver:"+((YamahaDX7Device)(device)).tx7StoreString,
                                          getDriverName()+"Driver",
                                          JOptionPane.INFORMATION_MESSAGE);
      }
    }

  }


  public void requestPatchDump(int bankNum, int patchNum)
  {
    if ( ((YamahaDX7Device)(device)).whichSynth.compareTo("DX7")==0 )
    {
      if ( ((YamahaDX7Device)(device)).sPBPval==1 ) // DX7 Remote Control
      {
        // make Sys Info available
        ((YamahaDX7Device)(device)).mkDX7SysInfoAvail(port, (byte)(channel+0x10));
        // internal memory or RAM cartridge?
        ((YamahaDX7Device)(device)).chDX7Bank(port, (byte)(channel+0x10), (byte)(bankNum+0x25));
        // which patch do you want
        ((YamahaDX7Device)(device)).chDX7Patch(port, (byte)(channel+0x10), (byte)(patchNum));
      }
      else // DX7 manually control
      {
        if ( ((YamahaDX7Device)(device)).sPBPmsgVal==1 )
          // Information about requesting a patch
          JOptionPane.showMessageDialog(PatchEdit.instance,
                                        getDriverName()+"Driver:"+((YamahaDX7Device)(device)).dx7RequestVoiceString,
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
    else if ( ((YamahaDX7Device)(device)).whichSynth.compareTo("TX7")==0 )
    {
      if ( ((YamahaDX7Device)(device)).sPBPmsgVal==1 )
        // Information about requesting a single voice patch
        JOptionPane.showMessageDialog(PatchEdit.instance,
                                      getDriverName()+"Driver:"+((YamahaDX7Device)(device)).tx7RequestVoiceString,
                                      "Get "+getDriverName()+"Patch",
                                      JOptionPane.WARNING_MESSAGE);

      sysexRequestDump.send(port, (byte)(channel+0x20) );
    }

  }


  public Patch createNewPatch() // the new Patch has the same parameters as a DX7-created "init voice"
  {
    byte [] init_voice = {
    -16,67,0,0,1,27,99,99,99,99,99,99,99,0,0,0,0,0,0,0,
    0,0,0,0,1,0,7,99,99,99,99,99,99,99,0,0,0,0,0,0,
    0,0,0,0,0,1,0,7,99,99,99,99,99,99,99,0,0,0,0,0,
    0,0,0,0,0,0,1,0,7,99,99,99,99,99,99,99,0,0,0,0,
    0,0,0,0,0,0,0,1,0,7,99,99,99,99,99,99,99,0,0,0,
    0,0,0,0,0,0,0,0,1,0,7,99,99,99,99,99,99,99,0,0,
    0,0,0,0,0,0,0,99,0,1,0,7,99,99,99,99,50,50,50,50,
    0,0,1,35,0,0,0,1,0,3,24,73,78,73,84,32,86,79,73,67,
    69,81,-9};

    Patch p = new Patch(init_voice);
    p.ChooseDriver();

    return p;
  }


  public JInternalFrame editPatch(Patch p)
  {
    if ( ((YamahaDX7Device)(device)).whichSynth.compareTo("DX7")==0 )
    {
      if ( ((YamahaDX7Device)(device)).sPBPval==1 ) // DX7 Remote Control
      {
        // make Sys Info available
        ((YamahaDX7Device)(device)).mkDX7SysInfoAvail(port, (byte)(channel+0x10));
        // switch off memory protection of internal voices
        ((YamahaDX7Device)(device)).swOffDX7MemProt(port, (byte)(channel+0x10), (byte)0x21, (byte)0x25);
      }
      else // DX7 manually control
      {
        if ( ((YamahaDX7Device)(device)).sPBPmsgVal==1 )
          // Informations about DX7 patch receiving
          JOptionPane.showMessageDialog(PatchEdit.instance,
                                        getDriverName()+"Driver:"+((YamahaDX7Device)(device)).dx7ReceiveString,
                                        getDriverName()+"Driver",
                                        JOptionPane.INFORMATION_MESSAGE);
      }
    }
    else if ( ((YamahaDX7Device)(device)).whichSynth.compareTo("TX7")==0 )
    {
      if ( ((YamahaDX7Device)(device)).sPBPval==1 ) // TX7 Remote Control
      {
        // switch off TX7 memory protection (is it realy necessary?)
        ((YamahaDX7Device)(device)).swOffTX7MemProt.send(port, (byte)(channel+0x10)); // TX7 function parameter change
      }
      else // TX7 manually control
      {
        if( ((YamahaDX7Device)(device)).sPBPmsgVal==1 )
            // Informations about TX7 patch receiving
            JOptionPane.showMessageDialog(PatchEdit.instance,
                                          getDriverName()+"Driver:"+((YamahaDX7Device)(device)).tx7ReceiveString,
                                          getDriverName()+"Driver",
                                          JOptionPane.INFORMATION_MESSAGE);
      }
    }

    return new YamahaDX7SingleEditor(p);
  }

}
