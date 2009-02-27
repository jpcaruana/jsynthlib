/*
 * Copyright 2005 Jeff Weber
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

package org.jsynthlib.drivers.behringer.vamp2;

/** VAmp2 Edit Buffer Driver. Used for V-Amp 2 Edit Buffer patch.
 * Note that on the V-Amp 2, the edit buffer patch and the preset patch are
 * exactly the same format. The only difference is that for the program patch
 * the patch location is stored in byte 8 and is a value from 0 to 124 and for
 * the edit buffer patch the patch location stored in byte 8 is the value 127.
 * The only reason for having this driver is to be able to request an edit buffer
 * patch. From that point on it is handled like any other program patch.
 * 
 * @author Jeff Weber
 */
public class VAmp2EdBufDriver extends VAmp2SingleDriver {
    /** Constructs a VAmp2EdBufDriver.
     */
    public VAmp2EdBufDriver() {
        super(Constants.VAMP2_EDBUF_PATCH_TYP_STR);
        bankNumbers = new String[] { "" };
        patchNumbers = new String[] { "" };
    }

    /** Requests a dump of the Line6 edit buffer.
     * The bankNum and patchNum parameters are ignored.
     */
    public void requestPatchDump(int bankNum, int patchNum) {
        super.requestPatchDump(0, 127);
    }
}