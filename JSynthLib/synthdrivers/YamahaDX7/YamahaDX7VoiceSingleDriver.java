/*
 * JSynthlib - "Voice" Single Driver for Yamaha DX7 Mark-I
 * =======================================================
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
 */
package synthdrivers.YamahaDX7;
import core.*;
import javax.swing.*;

public class YamahaDX7VoiceSingleDriver extends Driver
{
	protected static byte[] initSysex = DX7Constants.INIT_VOICE;

	public YamahaDX7VoiceSingleDriver()
	{
		super ("Single","Torsten Tittmann");
    
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
		//patchSize=163;	// disabled, because of the behaviour of the SER-7 firmware
		trimSize=163;
		numSysexMsgs=1;		
		sysexRequestDump=new SysexHandler("F0 43 @@ 00 F7");	// theoretically, but not implemented
	}


	public void sendPatch (Patch p)
	{
		if ( ((YamahaDX7Device)(PatchEdit.appConfig.getDevice(getDeviceNum()))).getSwOffMemProtFlag()==1 ) {
			// switch off memory protection of internal voices
			DX7ParamChanges.swOffMemProt(getPort(), (byte)(getChannel()+0x10), (byte)(0x21), (byte)(0x25));
		}

		if ( ((YamahaDX7Device)(PatchEdit.appConfig.getDevice(getDeviceNum()))).getDX7sPBPflag()==1 ) {
			// make Sys Info available
			DX7ParamChanges.mkSysInfoAvail(getPort(), (byte)(getChannel()+0x10));
		}

		sendPatchWorker (p);
	}


	public void storePatch (Patch p, int bankNum,int patchNum)
	{
		if ( ((YamahaDX7Device)(PatchEdit.appConfig.getDevice(getDeviceNum()))).getSwOffMemProtFlag()==1 ) {
			// switch off memory protection of internal/cartridge voices
			DX7ParamChanges.swOffMemProt(getPort(), (byte)(getChannel()+0x10), (byte)(bankNum+0x21), (byte)(bankNum+0x25));
		} else {
			if ( ((YamahaDX7Device)(PatchEdit.appConfig.getDevice(getDeviceNum()))).getTipsMsgFlag()==1 )
				// Information
				JOptionPane.showMessageDialog(PatchEdit.instance,
				      getDriverName()+"Driver:"+ DX7Strings.MEMORY_PROTECTION_STRING,
				      getDriverName()+"Driver",
				      JOptionPane.INFORMATION_MESSAGE);
		}

		
		if ( ((YamahaDX7Device)(PatchEdit.appConfig.getDevice(getDeviceNum()))).getDX7sPBPflag()==1 ) {
			// make Sys Info available
			DX7ParamChanges.mkSysInfoAvail(getPort(), (byte)(getChannel()+0x10));
			//place patch in the edit buffer 
			sendPatchWorker(p);

			// internal memory or RAM cartridge?
			DX7ParamChanges.chBank(getPort(), (byte)(getChannel()+0x10), (byte)(bankNum+0x25));
			//start storing ...	     (depress Store button)
			DX7ParamChanges.depressStore.send(getPort(),(byte)(getChannel()+0x10));
			//put patch in the patch number
			DX7ParamChanges.chPatch(getPort(), (byte)(getChannel()+0x10), (byte)(patchNum));
			//... finish storing	     (release Store button)
			DX7ParamChanges.releaseStore.send(getPort(),(byte)(getChannel()+0x10));
		} else {
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
		if ( ((YamahaDX7Device)(PatchEdit.appConfig.getDevice(getDeviceNum()))).getDX7sPBPflag()==1 ) {
			// make Sys Info available
			DX7ParamChanges.mkSysInfoAvail(getPort(), (byte)(getChannel()+0x10));
			// internal memory or RAM cartridge?
			DX7ParamChanges.chBank(getPort(), (byte)(getChannel()+0x10), (byte)(bankNum+0x25));
			// which patch do you want
			DX7ParamChanges.chPatch(getPort(), (byte)(getChannel()+0x10), (byte)(patchNum));
		} else {
			if ( ((YamahaDX7Device)(PatchEdit.appConfig.getDevice(getDeviceNum()))).getTipsMsgFlag()==1 )
				// Information
				JOptionPane.showMessageDialog(PatchEdit.instance,
				      getDriverName()+"Driver:"+ DX7Strings.REQUEST_VOICE_STRING,
				      "Get "+getDriverName()+"Patch",
				      JOptionPane.WARNING_MESSAGE);

			byte buffer[] = new byte[256*1024];
			try {
				while (PatchEdit.MidiIn.messagesWaiting(getInPort()) > 0)
					PatchEdit.MidiIn.readMessage(getInPort(), buffer, 1024);
				} catch (Exception ex) {
					ErrorMsg.reportError("Error", "Error Clearing Midi In buffer.",ex);
				}
		}
	}


	public Patch createNewPatch()
	{
		Patch p = new Patch(initSysex,this);

		return p;
	}


	public JInternalFrame editPatch(Patch p)
	{
		if ( ((YamahaDX7Device)(PatchEdit.appConfig.getDevice(getDeviceNum()))).getSwOffMemProtFlag()==1 ) {
			// switch off memory protection of internal/cartridge voices
			DX7ParamChanges.swOffMemProt(getPort(), (byte)(getChannel()+0x10), (byte)(0x21), (byte)(0x25));
		} else {
			if ( ((YamahaDX7Device)(PatchEdit.appConfig.getDevice(getDeviceNum()))).getTipsMsgFlag()==1 )
				// Information
				JOptionPane.showMessageDialog(PatchEdit.instance,
				      getDriverName()+"Driver:"+ DX7Strings.MEMORY_PROTECTION_STRING,
				      getDriverName()+"Driver",
				      JOptionPane.INFORMATION_MESSAGE);
		}

		if ( ((YamahaDX7Device)(PatchEdit.appConfig.getDevice(getDeviceNum()))).getDX7sPBPflag()==1 ) {
			// make Sys Info available
			DX7ParamChanges.mkSysInfoAvail(getPort(), (byte)(getChannel()+0x10));
		} else {
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
