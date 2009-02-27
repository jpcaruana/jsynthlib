/*
 * Copyright 2006 Roger Westerlund
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

import static org.jsynthlib.drivers.roland.d10.D10Constants.*;

public class DisplayMessage extends D10DataSetMessage {

    public DisplayMessage(String row1, String row2) {
        super(DISPLAY_SIZE, BASE_DISPLAY);

        clearMessage();

        setMessage(DISPLAY_ROW_1.getIntValue(), row1);
        setMessage(DISPLAY_ROW_1.getIntValue(), row1);
    }

    private void clearMessage() {
        for (int index = 0; index < DISPLAY_SIZE.getIntValue(); index++) {
            setData((byte)' ');
        }
    }

    private void setMessage(int ofs_row_1, String row1) {
        byte[] bytes = row1.getBytes();
        for (int index = 0; index < bytes.length; index++) {
            setData(bytes[index]);
        }
    }
}
