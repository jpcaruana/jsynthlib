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
import synthdrivers.RolandD10.Entity;

public class D10TransferMessage extends D10SysexMessage {

    /**
     * @param command
     * @param bodySize
     */
    public D10TransferMessage(byte command, int bodySize, int address) {
        super(command, bodySize);
        setAddress(address);
    }

    /**
     * @param data
     */
    public D10TransferMessage(byte[] data) {
        super(data);
    }

    /**
     * Sets the address.
     * @param address The address to set
     */
    public void setAddress(int address) {
    	set3Bytes(D10Constants.OFS_ADDRESS, address);
    }

    /**
     * Sets the address.
     * @param address The address to set
     */
    public void setAddress(Entity address) {
        set3Bytes(D10Constants.OFS_ADDRESS, address.getDataValue());
    }

    protected void set3Bytes(int offset, int data) {
        int value = data;
        for (int index = 2; index >= 0; index--) {
            setByte(offset + index, (byte)(value & 0x7f));
            value >>= 8;
        }
    }
}
