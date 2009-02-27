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
public class D10RequestMessage extends D10TransferMessage {

	public D10RequestMessage(int address, int size) {
		super(D10Constants.CMD_RQ1, D10Constants.SIZE_ADDRESS + D10Constants.SIZE_SIZE, address);
        setSize(size);
	}

    public D10RequestMessage(Entity address, Entity size) {
        super(D10Constants.CMD_RQ1, D10Constants.SIZE_ADDRESS + D10Constants.SIZE_SIZE, address.getDataValue());
        setSize(size.getDataValue());
    }

    /**
     * Sets the size.
     * @param size The size to set
     */
    public void setSize(int size) {
    	set3Bytes(D10Constants.OFS_SIZE, size);
    }
}
