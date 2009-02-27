/*
 * Copyright 2003, 2004, 2005 Hiroo Hayashi
 *
 * This file is part of JSynthLib.
 *
 * JSynthLib is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
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

package org.jsynthlib.drivers.roland.td6;
//If other devices use this, package `synthdrivers.Roland' should be created.
//package synthdrivers.Roland;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.SysexMessage;

import org.jsynthlib.core.ErrorMsg;
import org.jsynthlib.core.IPatchDriver;
import org.jsynthlib.core.SysexWidget;
import org.jsynthlib.core.Utility;


/**
 * RolandDT1Sender.java
 *
 * Generic SysexWidget.ISender class for Roland DT1 (Data Transfer) system
 * exclusive message.
 *
 * Created: Wed Jun 25 22:57:37 2003
 *
 * @author <a href="mailto:hiroo.hayashi@computer.org">Hiroo Hayashi</a>
 * @version $Id$
 */
public class RolandDT1Sender implements SysexWidget.ISender {
    /** System Exclusive Data Stream */
    private byte [] b;
    /** address size : 3 or 4 byte */
    private int addrSize;
    /** address offset */
    private int addrOfst;
    /** data size : 1 or 4 byte */
    private int dataSize;
    /** data byte offset */
    private int dataOfst;
    /** Device ID offset */
    private int devIdOfst;
    /** offset from which check sum is calculated. */
    private int chkSumOfst;

    /**
     * Creates a new <code>RolandDT1Sender</code> instance.
     *
     * @param manufacturesId Manufacture's ID for System Exclusive Message.
     *	Roland : 0x41, 3 byte ID : "00 00 01" -> 0x010000
     * @param modelId  Model ID for System Exclusive Message.
     *	("00 3F" -> 0x3f00)
     * @param addrSize Number of byte for address field in System
     * Exclusive Message.  3 byte or 4 byte.
     * @param addr Address offset.
     * @param dataSize Number of byte for data field in System
     * Exclusive Message.  1 byte or 4 byte.
     * @exception IllegalArgumentException if an error occurs
     */
    public RolandDT1Sender(int manufacturesId, int modelId,
			   int addrSize, int addr, int dataSize)
	throws IllegalArgumentException {
	this.addrSize = addrSize;	// save for setValue()
	this.dataSize = dataSize;	// save for setValue()

	// Setup all data except channel, data and check-sum here for efficiency.
	b = new byte [5 + (manufacturesId > 255 ? 3 : 1) + (modelId > 255 ? 2 : 1)
		      + addrSize + dataSize];
	int i = 0;
	b[i++] = (byte) SysexMessage.SYSTEM_EXCLUSIVE;
	if (manufacturesId < 256) {
	    b[i++] = (byte) manufacturesId;
	} else {
	    b[i++] = (byte) ( manufacturesId        & 0x7f);
	    b[i++] = (byte) ((manufacturesId >>  8) & 0x7f);
	    b[i++] = (byte) ((manufacturesId >> 16) & 0x7f);
	}
	// device ID is not set yet here and set by send() method.
	devIdOfst = i++;

	if (modelId < 256) {
	    b[i++] = (byte) modelId;
	} else {
	    b[i++] = (byte) ( modelId       & 0x7f);
	    b[i++] = (byte) ((modelId >> 8) & 0x7f);
	}
	b[i++] = (byte) 0x12;	// DT1
	addrOfst = chkSumOfst = i;

	if (addrSize == 3) {
	    b[i++] = (byte) ((addr >> 16) & 0x7f);
	    b[i++] = (byte) ((addr >>  8) & 0x7f);
	    b[i++] = (byte) ( addr        & 0x7f);
	} else if (addrSize == 4) {
	    b[i++] = (byte) ((addr >> 24) & 0x7f);
	    b[i++] = (byte) ((addr >> 16) & 0x7f);
	    b[i++] = (byte) ((addr >>  8) & 0x7f);
	    b[i++] = (byte) ( addr        & 0x7f);
	} else {
	    throw new IllegalArgumentException();
	}
	dataOfst = i;
	b[b.length - 1] = (byte) ShortMessage.END_OF_EXCLUSIVE;
    }

    /**
     * Constructor without address offset. The address offset must be specified
     * by using setValue(int, int) method in an overridden setValue(int) method.
     * This is useful for a Sender whose parameter offset address changes
     * dynamically.
     */
    public RolandDT1Sender(int manufacturesId, int modelId,
			   int addrSize, int dataSize)
	throws IllegalArgumentException {
	this(manufacturesId, modelId, addrSize, 0, dataSize);
    }

    /**
     * Generate SysEX byte sequence.  Called by SysexWidgets.
     *
     * @param addr Address
     * @param value Data
     * @exception IllegalArgumentException if an error occurs
     */
    protected void setValue(int addr, int value) throws IllegalArgumentException {
	ErrorMsg.reportStatus("RolandDT1Sender: addr 0x"
			      + Integer.toHexString(addr)
			      + ", data 0x" + Integer.toHexString(value));
	if (addr >= 0) {
	    if (addrSize == 3) {
		b[addrOfst + 0] = (byte) ((addr >> 16) & 0x7f);
		b[addrOfst + 1] = (byte) ((addr >>  8) & 0x7f);
		b[addrOfst + 2] = (byte) ( addr        & 0x7f);
	    } else if (addrSize == 4) {
		b[addrOfst + 0] = (byte) ((addr >> 24) & 0x7f);
		b[addrOfst + 1] = (byte) ((addr >> 16) & 0x7f);
		b[addrOfst + 2] = (byte) ((addr >>  8) & 0x7f);
		b[addrOfst + 3] = (byte) ( addr	       & 0x7f);
	    } else {
		throw new IllegalArgumentException();
	    }
	}

	if (dataSize == 1) {
	    // 1 byte data
	    b[dataOfst] = (byte) (value & 0x7f);
	} else if (dataSize == 4) {
	    // 4 byte data (MSB first) !!!document!!!
	    b[dataOfst + 0] = (byte) ((value >> 24) & 0x7f);
	    b[dataOfst + 1] = (byte) ((value >> 16) & 0x7f);
	    b[dataOfst + 2] = (byte) ((value >>  8) & 0x7f);
	    b[dataOfst + 3] = (byte) ( value	    & 0x7f);
	} else {
	    throw new IllegalArgumentException();
	}
    }

    /**
     * Generate SysEX byte sequence.  Address offset must be specified
     * by constructor.
     *
     * @param value Data
     * @exception IllegalArgumentException if an error occurs
     */
    protected void setValue(int value) throws IllegalArgumentException {
	setValue(-1, value);
    }

    // SysexWidget.ISender method
    public void send(IPatchDriver driver, int value) {
        // set data (and address optionally).
        setValue(value);

	// set Device ID
	b[devIdOfst] = (byte) (driver.getDevice().getDeviceID() - 1);
	// calculate check sum
	int sum = 0;
	for (int i = chkSumOfst; i <= b.length - 3; i++)
	    sum += b[i];
	b[b.length - 2] = (byte) (-sum & 0x7f);
	ErrorMsg.reportStatus(toString());
	// create and send MIDI message
	SysexMessage m = new SysexMessage();
        try {
            m.setMessage(b, b.length);
            driver.send(m);
        } catch (InvalidMidiDataException e) {
            ErrorMsg.reportStatus(e);
        }
    }

    /**
     * Dump byte data array.  Only for debugging.
     *
     * @return string like "f0 a3 00 "
     */
    public String toString() {
	return Utility.hexDump(b, 0, b.length, 0);
    }
} // RolandDT1Sender
