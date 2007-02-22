/*
 * Copyright 2002 Roger Westerlund
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
package synthdrivers.RolandD10;

/**
 * @author  Roger Westerlund <roger.westerlund@home.se>
 */
public class D10Constants {

    /** Start of a System Exclusive message. */
    public static final int SYSEX_START = 0xF0;

    /** End of a System Exclusive message. */
    public static final int SYSEX_END = 0xF7;
    
	public static final byte MANUFACTURER_ROLAND = 0x41;
	public static final byte MODEL_D10 = 0x16;

    public static final byte DEFAULT_DEVICE_ID = 0x10;

    //
	// Commands.
    //

	public static final byte CMD_RQ1 = 0x11;
	public static final byte CMD_DT1 = 0x12;

    //
	// Sysex offsets and sizes
    //

	public static final int OFS_MANUFACTURER   = 1;
	public static final int OFS_DEVICE_ID      = OFS_MANUFACTURER + 1;
	public static final int OFS_MODEL          = OFS_DEVICE_ID + 1;
	public static final int OFS_COMMAND        = OFS_MODEL + 1;
	public static final int OFS_ADDRESS        = OFS_COMMAND + 1;
    public static final int SIZE_ADDRESS       = 3;
	public static final int OFS_SIZE           = OFS_ADDRESS + SIZE_ADDRESS;
    public static final int SIZE_SIZE          = 3;

	public static final int SIZE_HEADER        = OFS_ADDRESS;
    public static final int SIZE_HEADER_RQ1    = SIZE_HEADER + SIZE_ADDRESS + SIZE_SIZE;
    public static final int SIZE_HEADER_DT1    = SIZE_HEADER + SIZE_ADDRESS;
	public static final int SIZE_TRAILER       = 2;	// Check sum plus 0xf7

    //
    // Memory map
    //

    public static final Entity BASE_TIMBRE_TEMP             = Entity.ZERO;
    public static final Entity BASE_TONE_TEMP               = Entity.createFromDataValue(0x020000);
    
    public static final Entity BASE_TIMBRE_TEMP_AREA        = Entity.createFromDataValue(0x030000);
    public static final Entity BASE_RYTHM_SETUP_TEMP_AREA   = Entity.createFromDataValue(0x030110);
    public static final Entity BASE_PATCH_TEMP_AREA         = Entity.createFromDataValue(0x030400);
    public static final Entity BASE_TONE_TEMP_AREA          = Entity.createFromDataValue(0x040000);

    public static final Entity BASE_TIMBRE_MEMORY           = Entity.createFromDataValue(0x050000);
    public static final Entity BASE_PATCH_MEMORY            = Entity.createFromDataValue(0x070000);
    public static final Entity BASE_TONE_MEMORY             = Entity.createFromDataValue(0x080000);

    public static final Entity BASE_RYTHM_SETUP             = Entity.createFromDataValue(0x090000);
    public static final Entity BASE_RYTHM_PATTERN           = Entity.createFromDataValue(0x0a0000);
    public static final Entity BASE_RYTHM_TRACK             = Entity.createFromDataValue(0x0c0000);
    
    public static final Entity BASE_SYSTEM_AREA             = Entity.createFromDataValue(0x100000);
    public static final Entity BASE_DISPLAY                 = Entity.createFromDataValue(0x200000);
    public static final Entity BASE_WRITE_REQUEST           = Entity.createFromDataValue(0x400000);

    //
    // Timbre memory
    //

    public static final int TIMBRE_COUNT        = 128;
    public static final Entity TIMBRE_SIZE             = Entity.createFromDataValue(0x000008);
    public static final Entity TIMBRE_TONE_GROUP       = Entity.ZERO;
    public static final Entity TIMBRE_TONE_NUMBER      = Entity.createFromDataValue(0x000001);
    public static final Entity TIMBRE_KEY_SHIFT        = Entity.createFromDataValue(0x000002);
    public static final Entity TIMBRE_FINE_TUNE        = Entity.createFromDataValue(0x000003);
    public static final Entity TIMBRE_BENDER_RANGE     = Entity.createFromDataValue(0x000004);
    public static final Entity TIMBRE_ASSIGN_MODE      = Entity.createFromDataValue(0x000005);
    public static final Entity TIMBRE_REVERB_SWITCH    = Entity.createFromDataValue(0x000006);
    public static final Entity TIMBRE_DUMMY            = Entity.createFromDataValue(0x000007);

    //
    // Patch memory
    //

    public static final int PATCH_COUNT         = 128;
    public static final Entity PATCH_SIZE                   = Entity.createFromDataValue(0x000026);
    public static final Entity PATCH_KEY_MODE               = Entity.ZERO;
    public static final Entity PATCH_SPLIT_POINT            = Entity.createFromDataValue(0x000001);
    public static final Entity PATCH_LOWER_TONE_GROUP       = Entity.createFromDataValue(0x000002);
    public static final Entity PATCH_LOWER_TONE_NUMBER      = Entity.createFromDataValue(0x000003);
    public static final Entity PATCH_UPPER_TONE_GROUP       = Entity.createFromDataValue(0x000004);
    public static final Entity PATCH_UPPER_TONE_NUMBER      = Entity.createFromDataValue(0x000005);
    public static final Entity PATCH_LOWER_KEY_SHIFT        = Entity.createFromDataValue(0x000006);
    public static final Entity PATCH_UPPER_KEY_SHIFT        = Entity.createFromDataValue(0x000007);
    public static final Entity PATCH_LOWER_FINE_TUNE        = Entity.createFromDataValue(0x000008);
    public static final Entity PATCH_UPPER_FINE_TUNE        = Entity.createFromDataValue(0x000009);
    public static final Entity PATCH_LOWER_BENDER_RANGE     = Entity.createFromDataValue(0x00000a);
    public static final Entity PATCH_UPPER_BENDER_RANGE     = Entity.createFromDataValue(0x00000b);
    public static final Entity PATCH_LOWER_ASSIGN_MODE      = Entity.createFromDataValue(0x00000c);
    public static final Entity PATCH_UPPER_ASSIGN_MODE      = Entity.createFromDataValue(0x00000d);
    public static final Entity PATCH_LOWER_REVERB_SWITCH    = Entity.createFromDataValue(0x00000e);
    public static final Entity PATCH_UPPER_REVERB_SWITCH    = Entity.createFromDataValue(0x00000f);
    public static final Entity PATCH_REVERB_MODE            = Entity.createFromDataValue(0x000010);
    public static final Entity PATCH_REVERB_TIME            = Entity.createFromDataValue(0x000011);
    public static final Entity PATCH_REVERB_LEVEL           = Entity.createFromDataValue(0x000012);
    public static final Entity PATCH_U_L_BALANCE            = Entity.createFromDataValue(0x000013);
    public static final Entity PATCH_LEVEL                  = Entity.createFromDataValue(0x000014);
    public static final Entity PATCH_NAME                   = Entity.createFromDataValue(0x000015);
    public static final Entity PATCH_NAME_SIZE              = Entity.createFromDataValue(0x000010);
    public static final Entity PATCH_DUMMY                  = Entity.createFromDataValue(0x000025);

    //
    // Tone memory
    //

    public static final int TONE_COUNT          = 64;
    public static final Entity TONE_SIZE               = Entity.createFromDataValue(0x000176);
    // Tones are not stored back to back in memory.
    public static final Entity TONE_RECORD_SIZE        = Entity.createFromDataValue(0x000200);
    public static final Entity TONE_NAME_START         = Entity.ZERO;
    public static final Entity TONE_NAME_SIZE          = Entity.createFromDataValue(0x00000a);

    // Common parameter block.

    public static final int COMMON_SIZE = 0xe;
    public static final int COMMON_NAME             = 0x0;
    public static final int COMMON_STRUCTURE_12     = 0xa;
    public static final int COMMON_STRUCTURE_34     = 0xb;
    public static final int COMMON_PARTIAL_MUTE     = 0xc;
    public static final int COMMON_ENV_MODE         = 0xd;

    // Partial parameter block.

    public static final int PART_SIZE = 0x3a;
    public static final int PART_WG_PITCH_COARSE        = 0x0;
    public static final int PART_WG_PITCH_FINE          = 0x1;
    public static final int PART_WG_PITCH_KEYFOLLOW     = 0x2;
    public static final int PART_WG_PITCH_BENDER_SW     = 0x3;
    public static final int PART_WG_WAVEFORM            = 0x4;
    public static final int PART_WG_PCM_BANK            = 0x4;
    public static final int PART_WG_PCM_WAVE_NO         = 0x5;
    public static final int PART_WG_PULSE_WIDTH         = 0x6;
    public static final int PART_WG_PW_VELO_SENSE       = 0x7;
    
    public static final int PART_P_ENV_DEPTH            = 0x8;
    public static final int PART_P_ENV_VELO_SENS        = 0x9;
    public static final int PART_P_ENV_TIME_KEYF        = 0xa;
    public static final int PART_P_ENV_TIME_1           = 0xb;
    public static final int PART_P_ENV_TIME_2           = 0xc;
    public static final int PART_P_ENV_TIME_3           = 0xd;
    public static final int PART_P_ENV_TIME_4           = 0xe;
    public static final int PART_P_ENV_LEVEL_0          = 0xf;
    public static final int PART_P_ENV_LEVEL_1          = 0x10;
    public static final int PART_P_ENV_LEVEL_2          = 0x11;
    public static final int PART_P_ENV_END_LEVEL        = 0x13;

    public static final int PART_P_LFO_RATE             = 0x14;
    public static final int PART_P_LFO_DEPTH            = 0x15;
    public static final int PART_P_LFO_MOD_SENS         = 0x16;

    public static final int PART_TVF_CUTOFF_FREQ        = 0x17;
    public static final int PART_TVF_RESONANCE          = 0x18;
    public static final int PART_TVF_KEYFOLLOW          = 0x19;
    public static final int PART_TVF_BIAS_POINT_DIR     = 0x1a;
    public static final int PART_TVF_BIAS_LEVEL         = 0x1b;
    public static final int PART_TVF_ENV_DEPTH          = 0x1c;
    public static final int PART_TVF_ENV_VELO_SENS      = 0x1d;
    public static final int PART_TVF_ENV_DEPTH_KEYF     = 0x1e;
    public static final int PART_TVF_ENV_TIME_KEYF      = 0x1f;
    public static final int PART_TVF_ENV_TIME_1         = 0x20;
    public static final int PART_TVF_ENV_TIME_2         = 0x21;
    public static final int PART_TVF_ENV_TIME_3         = 0x22;
    public static final int PART_TVF_ENV_TIME_4         = 0x24;
    public static final int PART_TVF_ENV_LEVEL_1        = 0x25;
    public static final int PART_TVF_ENV_LEVEL_2        = 0x26;
    public static final int PART_TVF_ENV_SUSTAIN_LEVEL  = 0x27;

    public static final int PART_TVA_LEVEL              = 0x29;
    public static final int PART_TVA_VELO_SENS          = 0x2a;
    public static final int PART_TVA_BIAS_POINT_1       = 0x2b;
    public static final int PART_TVA_BIAS_LEVEL_1       = 0x2c;
    public static final int PART_TVA_BIAS_POINT_2       = 0x2d;
    public static final int PART_TVA_BIAS_LEVEL_2       = 0x2e;
    public static final int PART_TVA_ENV_TIME_KEYF      = 0x2f;
    public static final int PART_TVA_ENV_TIME_V_FOLLOW  = 0x30;
    public static final int PART_TVA_ENV_TIME_1         = 0x31;
    public static final int PART_TVA_ENV_TIME_2         = 0x32;
    public static final int PART_TVA_ENV_TIME_3         = 0x33;
    public static final int PART_TVA_ENV_TIME_4         = 0x35;
    public static final int PART_TVA_ENV_LEVEL_1        = 0x36;
    public static final int PART_TVA_ENV_LEVEL_2        = 0x37;
    public static final int PART_TVA_ENV_SUSTAIN_LEVEL  = 0x38;

    //
    // Rythm setup memory
    //

    public static final int RYTHM_SETUP_COUNT   = 85;
    public static final Entity RYTHM_SETUP_SIZE             = Entity.createFromIntValue(0x000004);
    public static final Entity RYTHM_SETUP_TONE             = Entity.ZERO;
    public static final Entity RYTHM_SETUP_LEVEL            = Entity.createFromDataValue(0x000001);
    public static final Entity RYTHM_SETUP_PANPOT           = Entity.createFromDataValue(0x000002);
    public static final Entity RYTHM_SETUP_REVERB_SWITCH    = Entity.createFromDataValue(0x000003);

    //
    // Display memory
    //

    public static final Entity DISPLAY_SIZE         = Entity.createFromDataValue(32);
    public static final Entity DISPLAY_ROW_SIZE     = Entity.createFromDataValue(0x000010);
    public static final Entity DISPLAY_ROW_1        = Entity.ZERO;
    public static final Entity DISPLAY_ROW_2        = Entity.createFromDataValue(0x000010);

    //
    // Write request memory
    //

    public static final Entity TONE_WRITE_REQUEST       = Entity.ZERO;
    public static final Entity TIMBRE_WRITE_REQUEST     = Entity.createFromDataValue(0x000100);
    public static final Entity PATCH_WRITE_REQUEST      = Entity.createFromDataValue(0x000300);

}
