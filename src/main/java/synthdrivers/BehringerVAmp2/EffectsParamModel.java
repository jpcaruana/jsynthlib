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

package synthdrivers.BehringerVAmp2;

import org.jsynthlib.core.ErrorMsg;
import org.jsynthlib.core.ParamModel;
import org.jsynthlib.core.Patch;
import org.jsynthlib.core.Utility;

/**
 * The EffectsParamModel class allows a control to set the individual bits in a
 * byte of the patch.sysex record. This is used when a single byte of the sysex
 * record contains more than one parameter. Masks are provided for each of the
 * parameters of the DM5 to define which bits are used for a given parameter.
 * 
 * @author Jeff Weber
 */
class EffectsParamModel extends ParamModel {
    
    /**
     * Array of byte arrays representing default values for the effect parameters.
     * Every time one of the sixteen pre-defined effects configurations is selected,
     * the V-Amp sets up default effects parameters in it's internal memory. The V-Amp
     * editor has to emulate this to keep it's internal buffer in sync with the device.
     * These are the values the editor uses to set up the defaults.
     */
    private static byte[][] EFFECT_DEFAULT = {
    {  // 00 Echo
        (byte)0x00, (byte)0x40, (byte)0x40, (byte)0x40, (byte)0x40, (byte)0x01, (byte)0x18, (byte)0x00,
        (byte)0x00, (byte)0x2D, (byte)0x58, (byte)0x04, (byte)0x1B, (byte)0x49, (byte)0x40, (byte)0x00
    },
    {  // 01 Delay
        (byte)0x00, (byte)0x40, (byte)0x40, (byte)0x40, (byte)0x40, (byte)0x00, (byte)0x18, (byte)0x19,
        (byte)0x20, (byte)0x26, (byte)0x52, (byte)0x04, (byte)0x1B, (byte)0x49, (byte)0x40, (byte)0x00
    },
    {  // 02 Ping Pong
        (byte)0x00, (byte)0x40, (byte)0x40, (byte)0x40, (byte)0x40, (byte)0x02, (byte)0x39, (byte)0x61,
        (byte)0x00, (byte)0x26, (byte)0x60, (byte)0x04, (byte)0x1B, (byte)0x49, (byte)0x40, (byte)0x00
    },
    {  // 03 Phaser/Delay
        (byte)0x00, (byte)0x40, (byte)0x40, (byte)0x40, (byte)0x40, (byte)0x00, (byte)0x1F, (byte)0x00,
        (byte)0x20, (byte)0x2F, (byte)0x45, (byte)0x01, (byte)0x1F, (byte)0x64, (byte)0x59, (byte)0x54
    },
    {  // 04 Flanger/Delay 1
        (byte)0x00, (byte)0x40, (byte)0x40, (byte)0x40, (byte)0x40, (byte)0x00, (byte)0x1F, (byte)0x00,
        (byte)0x20, (byte)0x2F, (byte)0x3F, (byte)0x06, (byte)0x1F, (byte)0x5B, (byte)0x14, (byte)0x5B
    },
    {  // 05 Flanger/Delay 2
        (byte)0x00, (byte)0x40, (byte)0x40, (byte)0x40, (byte)0x40, (byte)0x02, (byte)0x0C, (byte)0x60,
        (byte)0x00, (byte)0x24, (byte)0x60, (byte)0x06, (byte)0x27, (byte)0x3F, (byte)0x07, (byte)0x46
    },
    {  // 06 Chorus/Delay 1
        (byte)0x00, (byte)0x40, (byte)0x40, (byte)0x40, (byte)0x40, (byte)0x00, (byte)0x1D, (byte)0x44,
        (byte)0x20, (byte)0x37, (byte)0x32, (byte)0x04, (byte)0x1B, (byte)0x49, (byte)0x40, (byte)0x3E
    },
    {  // 07 Chorus/Delay 2
        (byte)0x00, (byte)0x40, (byte)0x40, (byte)0x40, (byte)0x40, (byte)0x02, (byte)0x1B, (byte)0x14,
        (byte)0x00, (byte)0x1E, (byte)0x61, (byte)0x04, (byte)0x2B, (byte)0x2B, (byte)0x40, (byte)0x3F
    },
    {  // 08 Chorus/Compressor
        (byte)0x01, (byte)0x42, (byte)0x30, (byte)0x40, (byte)0x40, (byte)0x00, (byte)0x20, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x04, (byte)0x1B, (byte)0x49, (byte)0x40, (byte)0x3F
    },
    {  // 09 Compressor
        (byte)0x01, (byte)0x42, (byte)0x30, (byte)0x40, (byte)0x40, (byte)0x00, (byte)0x20, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x04, (byte)0x1B, (byte)0x49, (byte)0x40, (byte)0x00
    },
    {  // 10 Auto Wah
        (byte)0x02, (byte)0x37, (byte)0x53, (byte)0x47, (byte)0x42, (byte)0x00, (byte)0x20, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x04, (byte)0x1B, (byte)0x49, (byte)0x40, (byte)0x00
    },
    {  // 11 Phaser
        (byte)0x00, (byte)0x40, (byte)0x40, (byte)0x40, (byte)0x40, (byte)0x00, (byte)0x20, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x01, (byte)0x22, (byte)0x7F, (byte)0x5F, (byte)0x64
    },
    {  // 12 Chorus
        (byte)0x00, (byte)0x40, (byte)0x40, (byte)0x40, (byte)0x40, (byte)0x00, (byte)0x20, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x04, (byte)0x13, (byte)0x4C, (byte)0x40, (byte)0x44
    },
    {  // 13 Flanger
        (byte)0x00, (byte)0x40, (byte)0x40, (byte)0x40, (byte)0x40, (byte)0x00, (byte)0x20, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x06, (byte)0x1F, (byte)0x45, (byte)0x18, (byte)0x60
    },
    {  // 14 Tremelo
        (byte)0x00, (byte)0x40, (byte)0x40, (byte)0x40, (byte)0x40, (byte)0x00, (byte)0x20, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x02, (byte)0x53, (byte)0x40, (byte)0x40, (byte)0x50
    },
    {  // 15 Rotary
        (byte)0x00, (byte)0x40, (byte)0x40, (byte)0x40, (byte)0x40, (byte)0x00, (byte)0x20, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x49, (byte)0x7F, (byte)0x40, (byte)0x5B
    }
    };
    
    /**
     * Constructs a EffectsParamModel given the patch, the offset into the sysex
     * record, and the mask representing the parameter.
     *
     * @param p
     *          The patch to be edited.
     * @param offset
     *          The offset into the patch (including the size of the sysex header)
     *          representing the value to be edited.
     */
    EffectsParamModel(Patch p, int offset) {
        super(p, offset);
    }
    
    /**
     * Updates the byte in the sysex record defined by this EffectsParamModel.
     *
     * @param value
     *          The new value of this parameter.
     */
    public void set(int value) {
        patch.sysex[ofs] = (byte) value;
        patch.sysex[Constants.HDR_SIZE + 9] = 1; // Set effects on
        System.arraycopy(EFFECT_DEFAULT[value], 0, patch.sysex,
                Constants.HDR_SIZE + 16, 16);

        ErrorMsg.reportStatus(">>>>>>> Patch After Edit <<<<<<<<<");
        ErrorMsg.reportStatus("  " + Utility.hexDump(patch.sysex, 0, -1, 16));
    }
    
    /**
     * Gets the value of the byte in the sysex record defined by this EffectsParamModel.
     * @return
     *            The value of this parameter.
     */
    public int get() {
        return (int) patch.sysex[ofs];
    }
}