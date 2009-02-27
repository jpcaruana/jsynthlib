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
package org.jsynthlib.drivers.roland.d10.message;

import org.jsynthlib.drivers.roland.d10.D10Constants;
import org.jsynthlib.drivers.roland.d10.Entity;

/**
 * @author Roger
 */
public class D10DataSetMessage extends D10TransferMessage {

    public D10DataSetMessage(int bodySize, int address) {
        super(D10Constants.CMD_DT1, D10Constants.SIZE_ADDRESS + bodySize, address);
    }

    public D10DataSetMessage(Entity bodySize, Entity address) {
        super(D10Constants.CMD_DT1, D10Constants.SIZE_ADDRESS + bodySize.getIntValue(), address
                .getDataValue());
    }

    public D10DataSetMessage(byte[] data) {
        super(data);
    }

    public int getDataOffset() {
        return D10Constants.SIZE_HEADER_DT1;
    }

    public byte getData() {
        return getData(0);
    }

    public byte getData(int offset) {
        return message[getDataOffset() + offset];
    }

    public void setData(byte data) {
        setData(0, data);
    }

    public void setData(int offset, byte data) {
        message[getDataOffset() + offset] = data;
    }

    public void setData(int offset, byte[] data) {
        setData(offset, data, data.length);
    }

    public void setData(int offset, byte[] data, int length) {
        System.arraycopy(data, 0, message, getDataOffset() + offset, length);
    }

    public void copyData(int offset, byte[] data, int dataOffset, int length) {
        System.arraycopy(data, dataOffset, message, getDataOffset() + offset, length);
    }
}
