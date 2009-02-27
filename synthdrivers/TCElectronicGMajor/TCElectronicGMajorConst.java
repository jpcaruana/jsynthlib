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

package synthdrivers.TCElectronicGMajor;

public class TCElectronicGMajorConst {
    // don't have to call constructor for Constant class.
    private TCElectronicGMajorConst() {
    }

    /** Size of a single patch */
    public static final int SINGLE_SIZE = 615;
    /** Number of patches. */
    public static final int NUM_PATCH = 100;
    /** Offset of patch name. */
    public static final int NAME_OFFSET = 9;
    /** Size of patch name. */
    public static final int NAME_SIZE = 20;
    /** Start of area for calculating checksum within patch. */
    public static final int CHECKSUMSTART = NAME_OFFSET+NAME_SIZE;
    /** End of area for calculating checksum within patch. */
    public static final int CHECKSUMEND = SINGLE_SIZE-6;
    /** Offset of checksum within patch. */
    public static final int CHECKSUMOFFSET = SINGLE_SIZE-2;
    /** patch file name for createNewPatch() */
    public static final String PATCHFILENAME = "newpatch.syx";
    /** Number of columns for displaying the patches in a table. */
    public static final int NUM_COLUMNS = 5;

    public static final int ROUTING_MASK   = 0x0003;  //0000 0000 0000 0011
    public static final int PRESETOUT_MASK = 0x01FC;  //0000 0001 1111 1100
    public static final int RELAY1_MASK    = 0x0200;  //0000 0010 0000 0000
    public static final int RELAY2_MASK    = 0x0400;  //0000 0100 0000 0000

    public static final int MOD_MASK    = 0x0007;  //0000 0000 0000 0111
    public static final int MINOUT_MASK = 0x03F8;  //0000 0011 1111 1000
    public static final int MIDOUT_MASK = 0x03F8;  //0000 0011 1111 1000
    public static final int MAXOUT_MASK = 0x03F8;  //0000 0011 1111 1000

    public static final int FILTER_TYPE_OFS = 229;
    public static final int PITCH_TYPE_OFS  = 293;
    public static final int CHORUS_TYPE_OFS = 357;
    public static final int DELAY_TYPE_OFS  = 421;
    public static final int REVERB_TYPE_OFS = 485;

    public static final String[] modString = new String[] {"Off", "M1", "M2", "M3", "M4"};
}
