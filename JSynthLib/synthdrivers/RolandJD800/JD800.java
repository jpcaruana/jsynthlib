/*
 * Copyright 2006 Robert Wirski
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

package synthdrivers.RolandJD800;

/**
 * Constants for RolandJD800 package.
 * @author Robert Wirski
 *
 */
public class JD800 {
    /** Number of bytes of a JD800 single patch. */
    public static final int SizeOfSinglePatch = 384;
    /** Size of a JD800 sysex message header. */
    public static final int SizeOfSyxHeader = 8;
    /** The first sysex message size of a JD800 single patch. */
    public static final int SizeOfPatchSyx1 = 266;
    /** The second sysex message size of a JD800 single patch. */ 
    public static final int SizeOfPatchSyx2 = 138;
    /** Maximum data block size which can be sent in a one JD800 sysex message. */
    public static final int MaxSyxDataBlock = 256;
    /** Checksum start offset for the first sysex message of a JD800 single patch. */
    public static final int checksumStartSyx1 = 5;
    /** Checksum end offset for the first sysex message of a JD800 single patch. */
    public static final int checksumEndSyx1 = SizeOfPatchSyx1 - 3;
    /** Checksum result offset for the first sysex message of a JD800 single patch. */
    public static final int checksumOffsetSyx1 = SizeOfPatchSyx1 - 2;
    /** Checksum start offset for the second sysex message of a JD800 single patch. */
    public static final int checksumStartSyx2 = 5;
    /** Checksum end offset for the second sysex message of a JD800 single patch. */
    public static final int checksumEndSyx2 = SizeOfPatchSyx2 -3;
    /** Checksum result offset for the second sysex message of a JD800 single patch. */
    public static final int checksumOffsetSyx2 = SizeOfPatchSyx2 - 2;
    /** Number of sysex messages needed to send a JD800 bank. */
    public static final int nrOfSyxForABank = 96;
    /** Number of single patches in a JD800 bank. */
    public static final int nrOfPatchesInABank = 64;
}