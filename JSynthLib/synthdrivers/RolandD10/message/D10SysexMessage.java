/*
 * Copyright 2003 Roger Westerlund
 *
 * This file is part of JSynthLib.
 *
 * JSynthLib is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or(at your option) any later version.
 *
 * JSynthLib is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JSynthLib; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 */
package synthdrivers.RolandD10.message;

import synthdrivers.RolandD10.D10Constants;

/**
 * @author Roger Westerlund
 */
public class D10SysexMessage {

	protected byte[] message;

	public D10SysexMessage(byte[] data) {
		message = data;
	}

/*
	byte manufacturer;
	byte deviceId;
	byte model;
	byte command;
	int address;
	int size;
*/	
	public D10SysexMessage(byte command, int bodySize) {
		message = new byte [D10Constants.SIZE_HEADER + bodySize + D10Constants.SIZE_TRAILER];
		setManufacturer(D10Constants.MANUFACTURER_ROLAND);
		setDeviceId(D10Constants.DEFAULT_DEVICE_ID);
		setModel(D10Constants.MODEL_D10);
		setCommand(command);
	}

    public void getBytes(byte[] buffer, int offset) {
        setSysexFrame();
        System.arraycopy(message, 0, buffer, offset, message.length);
    }

    public int getSize() {
        return message.length;
    }

    public byte[] getBytes() {
		setSysexFrame();
		return message;
	}

	private void setSysexFrame() {
		message[ 0 ] = (byte)D10Constants.SYSEX_START;
		message[ message.length - 1 ] = (byte)D10Constants.SYSEX_END;
		D10SysexMessage.calculateChecksum(message);
	}

	protected void setByte(int offset, byte data) {
		message[ offset ] = data;
	}

	/**
	 * Sets the command.
	 * @param command The command to set
	 */
	public void setCommand(byte command) {
		setByte(D10Constants.OFS_COMMAND, command);
	}

	/**
	 * Sets the deviceId.
	 * @param deviceId The deviceId to set
	 */
	public void setDeviceId(byte deviceId) {
		setByte(D10Constants.OFS_DEVICE_ID, deviceId);
	}

	/**
	 * Sets the manufacturer.
	 * @param manufacturer The manufacturer to set
	 */
	public void setManufacturer(byte manufacturer) {
		setByte(D10Constants.OFS_MANUFACTURER, manufacturer);
	}

	/**
	 * Sets the model.
	 * @param model The model to set
	 */
	public void setModel(byte model) {
		setByte(D10Constants.OFS_MODEL, model);
	}

    private static void calculateChecksum (byte[] buf,int start,int end,int ofs)
    {
        int sum=0;
        for (int i = start; i <= end; i++) {
            sum += buf[i];
        }
        buf[ofs] = (byte)(sum % 128);
        buf[ofs] = (byte)(buf[ofs]^127);
        buf[ofs] = (byte)(buf[ofs]+1);
    }

    private static void calculateChecksum (byte[] buf) {
        int size = buf.length;
        D10SysexMessage.calculateChecksum(buf, D10Constants.OFS_ADDRESS, size - 3, size - 2);
    }
}
