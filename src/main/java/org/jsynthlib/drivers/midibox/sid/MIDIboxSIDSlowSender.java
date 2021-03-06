/*
 * JSynthlib Slow MIDI Sender for MIDIbox SID
 * =====================================================================
 * @author  Thorsten Klose
 * file:    MIDIboxSIDSlowSender.java
 * date:    2002-11-30
 * @version 1.0
 *
 * Copyright (C) 2002  Thorsten.Klose@gmx.de   
 *                     http://www.uCApps.de
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

package org.jsynthlib.drivers.midibox.sid;
import org.jsynthlib.core.Driver;
import org.jsynthlib.core.ErrorMsg;


public class MIDIboxSIDSlowSender
{
    public void sendSysEx(Driver driver, byte[] buffer, int delay)
    {
	try {
	    //PatchEdit.MidiOut.writeLongMessage(port, buffer);
	    driver.send(buffer);
	} catch(Exception ex) { ex.printStackTrace(); ErrorMsg.reportStatus(ex); }

	try { Thread.sleep(delay); } catch (Exception e) {};
    }

    public void sendParameter(Driver driver, int parameter, byte value, int delay)
    {
	byte[]b = new byte[11];

	b[0] = (byte)0xF0; 
	b[1] = (byte)0x00;
	b[2] = (byte)0x00;
	b[3] = (byte)0x7e;
	b[4] = (byte)0x46;
	b[5] = (byte)((driver.getDeviceID()-1) & 0x7f);
	b[6] = (byte)0x06;
	b[7] = (byte)(parameter >= 0x80 ? 0x01 : 0x00);
	b[8] = (byte)(parameter & 0x7f);
	b[9] = value;
	b[10] = (byte)0xF7;

	sendSysEx(driver, b, delay);
    }
}
