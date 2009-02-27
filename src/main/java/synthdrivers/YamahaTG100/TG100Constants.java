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
    
    public static final int BITMASK_1111            = 0x0F;
    public static final int BITMASK_11110000        = 0xF0;

    public static final String SYSEX_ID                 = "F043**27";

    public static final int PATCH_SIZE          = 105;
    public static final int ELEMENT_SIZE        = 36;
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

    // Constants for the Editor
    public static final String[] VOICE_MODE = {"1 element", "2 elements"};
    public static final String[] PITCH_RATE_SCALING = {"100%", "50%", "20%", "10%", "5%", "0%"};
    
    public static final String [] NOTES = new String [] {	"C-2","C#-2","D-2","D#-2","E-2","F-2","F#-2","G-2","G#-2","A-2","A#-2","B-2",
	"C-1","C#-1","D-1","D#-1","E-1","F-1","F#-1","G-1","G#-1","A-1","A#-1","B-1",
	"C0","C#0","D0","D#0","E0","F0","F#0","G0","G#0","A0","A#0","B0",
	"C1","C#1","D1","D#1","E1","F1","F#1","G1","G#1","A1","A#1","B1",
	"C2","C#2","D2","D#2","E2","F2","F#2","G2","G#2","A2","A#2","B2",
	"C3","C#3","D3","D#3","E3","F3","F#3","G3","G#3","A3","A#3","B3",
	"C4","C#4","D4","D#4","E4","F4","F#4","G4","G#4","A4","A#4","B4",
	"C5","C#5","D5","D#5","E5","F5","F#5","G5","G#5","A5","A#5","B5",
	"C6","C#6","D6","D#6","E6","F6","F#6","G6","G#6","A6","A#6","B6",
	"C7","C#7","D7","D#7","E7","F7","F#7","G7","G#7","A7","A#7","B7",
    "C8","C#8","D8","D#8","E8","F8","F#8","G8"};
    
    public static final String[] PANPOT = { "center", 
                                            "right + 1", "right + 2", "right + 3", "right + 4", "right + 5", "right + 6", "right + 7",
                                            "left - 7", "left - 6", "left - 5", "left - 4", "left - 3", "left - 2", "left - 1"};
    
    public static final String[] PITCH_LFO_WAVE = {"triangle", "sample & hold"};
    
    public static final String[] P_EG_RANGE = {"1/2 oct", "1 oct", "2 oct"};
    
    public static final String[] SWITCH = {"on", "off"};
    
    public static final String[] VELOCITY_CURVE = {"curve-1", "curve-2", "curve-3", "curve-4", "curve-5", "curve-6", "curve-7", "curve-8"};

}
