/*
 * Copyright 2005 Ton Holsink
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

package org.jsynthlib.drivers.yamaha.ub99;

public class YamahaUB99Const {
    // don't have to call constructor for Constant class.
    private YamahaUB99Const() {
    }

    /** Size of a single patch */
    public static final int SINGLE_SIZE = 159;
    /** Size of a bank */
    public static final int BANK_SIZE = 17408;
    /** Number of patches. */
    public static final int NUM_PATCH = 99;
    /** Offset of patch name in bank. */
    public static final int BANK_NAME_OFFSET = 128;
    /** Offset of patch name in bank. */
    public static final int BANK_PATCH_OFFSET = 1536;
    /** Offset of patch name. */
    public static final int NAME_OFFSET = 16;
    /** Size of patch name. */
    public static final int NAME_SIZE = 12;
    /** Start of area for calculating checksum within patch. */
    public static final int CHECKSUMSTART = 8;
    /** patch file name for createNewPatch() */
    public static final String PATCHFILENAME = "newpatch.syx";
    /** Number of columns for displaying the patches in a table. */
    public static final int NUM_COLUMNS = 3;
    public static final String DEFAULT_FILENAME = "default.dat";
    public static final int NO_OF_FX = 63;
    public static final int DEFAULT_SIZE = SINGLE_SIZE * NO_OF_FX;
    public static final byte[] NEW_PATCH_NAME = {73, 110, 105, 116, 32, 80, 97, 116, 99, 104, 32, 32}; //"Init Patch  "

}
