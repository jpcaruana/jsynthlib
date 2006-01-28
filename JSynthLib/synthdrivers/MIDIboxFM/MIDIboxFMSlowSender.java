/*
 * JSynthlib Slow MIDI Sender for MIDIbox FM
 * =====================================================================
 * @author  Thorsten Klose
 * @version $Id$
 *
 * Copyright (C) 2005  Thorsten.Klose@gmx.de   
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

package synthdrivers.MIDIboxFM;
import core.Driver;
import core.ErrorMsg;


public class MIDIboxFMSlowSender
{
    public void sendSysEx(Driver driver, byte[] buffer, int delay)
    {
	try {
	    driver.send(buffer);
	} catch(Exception ex) { ex.printStackTrace(); ErrorMsg.reportStatus(ex); }

	try { Thread.sleep(delay); } catch (Exception e) {};
    }

    public void sendParameter(Driver driver, int parameter, byte value, int delay)
    {
	byte[]b = new byte[12];

	b[0] = (byte)0xF0; 
	b[1] = (byte)0x00;
	b[2] = (byte)0x00;
	b[3] = (byte)0x7e;
	b[4] = (byte)0x49;
	b[5] = (byte)(driver.getDeviceID()-1);
	b[6] = (byte)0x06;
	b[7] = (byte)0x00;
	b[8] = (byte)(parameter >= 0x80 ? 0x01 : 0x00);
	b[9] = (byte)(parameter & 0x7f);
	b[10] = value;
	b[11] = (byte)0xF7;

	sendSysEx(driver, b, delay);
    }
}
