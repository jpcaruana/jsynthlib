/*
 * Copyright 2004 Joachim Backhaus
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

package synthdrivers.YamahaTG100;

import core.*;

import java.util.prefs.Preferences;
/**
 * Constants for the Yamaha TG-100 synthdriver
 *
 * @author  Joachim Backhaus
 * @version $Id$
 */
public class TG100Constants {
    public static final byte SYSEX_START_BYTE           = (byte) 0xF0;
    public static final byte SYSEX_END_BYTE             = (byte) 0xF7;
    public static final int  SYSEX_HEADER_OFFSET        = 8;
    
    public static final String SYSEX_ID                 = "F043**27";

    public static final int PATCH_SIZE          = 105;
    public static final int PATCH_NAME_SIZE     = 8;
    public static final int PATCH_NAME_START    = 23;
    public static final int PATCH_NUMBER_LENGTH = 64;
            
    public static final int ALL_DUMP_SIZE       = 8266;
    public static final int ALL_DUMP_OFFSET     = 562;
    
    public static final int CHECKSUM_START      = 4;
    public static final int CHECKSUM_END        = 102;
    public static final int CHECKSUM_OFFSET     = 103;
    
    // Every patch is 0x60 (96 in decimal) bytes long
    public static final int SYSEX_SINGLE_VOICE_SIZE = 96;
    public static final byte SYSEX_VOICE_START_ADDRESS2 = (byte) 0x03;
    public static final byte SYSEX_VOICE_START_ADDRESS3 = (byte) 0x10;
    
}
