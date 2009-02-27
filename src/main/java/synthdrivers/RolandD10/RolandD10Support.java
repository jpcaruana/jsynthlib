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

import static synthdrivers.RolandD10.D10Constants.PATCH_COUNT;
import static synthdrivers.RolandD10.D10Constants.RYTHM_SETUP_COUNT;
import static synthdrivers.RolandD10.D10Constants.SIZE_HEADER_DT1;
import static synthdrivers.RolandD10.D10Constants.SIZE_TRAILER;
import static synthdrivers.RolandD10.D10Constants.TONE_COUNT;

/**
 * @author  Roger Westerlund <roger.westerlund@home.se>
 */
public class RolandD10Support {

    /** Creates a new instance of RolandD10Support */
    public RolandD10Support() {
    }

    private static final int BIT_MASK_7 = 0x7f;

    /**
     * Converts a Roland 7 bit hex entity to a number.
     * 
     * @param entity The 7 bit hex entity to convert.
     * @return The number corresponding to the parameter.
     */
    public static final int from7Bits(int entity) {
        int result = 0;
        int bitMask = BIT_MASK_7;
        for (int position = 0; position < 4; position++) {
            result |= entity & bitMask;
            bitMask <<= 7;
            entity >>= 1;
        }
        return result;
    }

    /**
     * Converts a number to a Roland 7 bit hex entity.
     * 
     * @param number The number to convert.
     * @return The 7 bit hex entity corresponding to the parameter.
     */
    public static final int to7Bits(int number) {
        int result = 0;
        int bitMask = BIT_MASK_7;
        for (int position = 0; position < 4; position++) {
            result |= number & bitMask;
            bitMask <<= 8;
            number <<= 1;
        }
        return result;
    }

	public static void dump(byte[] data) {
		for (int index = 0; index < data.length; index++) {
			if (index != 0) {
				System.out.print(" ");
			}
			System.out.print(Integer.toHexString(data[ index ]));
		}
		System.out.println("");
	}

    /**
     * Calculate an address for a memory record expressed in Roland 7 bit
     * hex entity.
     */
    public static final int getRecordAddress(int baseAddress, int recordSize, int recordNumber) {
        return to7Bits( from7Bits(baseAddress) + recordNumber * from7Bits(recordSize));
    }

    /**
     * @deprecated
     */
    public static final void storeAddress(byte[] buffer, int entity) {
        storeEntity(buffer, D10Constants.OFS_ADDRESS, entity);
    }

    /**
     * @deprecated
     */
    public static final void storeSize(byte[] buffer, int entity) {
        storeEntity(buffer, D10Constants.OFS_SIZE, entity);
    }

    /**
     * @deprecated
     */
    public static final void storeEntity(byte[] buffer, int offset, int entity) {
        buffer[ offset++ ] = (byte)((entity >>> 16) & 0x7f);
        buffer[ offset++ ] = (byte)((entity >>> 8) & 0x7f);
        buffer[ offset++ ] = (byte)(entity & 0x7f);
    }

    public static String[] createPatchNumbers() {
        String[] patchNumbers = new String[PATCH_COUNT];
        for (int ab = 0; ab < 2; ab++) {
            for (int bank = 0; bank < 8; bank++) {
                for (int number = 0; number < 8; number++) {
                    patchNumbers[ab * 64 + bank * 8 + number] = "AB".substring(ab, ab + 1) + Integer.toString(bank + 1) + Integer.toString(number + 1);
                }
            }
        }
        return patchNumbers;
    }

    public static String[] createToneNumbers() {
        String[] patchNumbers = new String[TONE_COUNT];
        return createSequenceNumbers(patchNumbers);
    }

    public static String[] createRythmSetupNumbers() {
        String[] patchNumbers = new String[RYTHM_SETUP_COUNT];
        return createSequenceNumbers(patchNumbers);
    }

    private static String[] createSequenceNumbers(String[] patchNumbers) {
        for (int number = 0; number < patchNumbers.length; number++) {
            patchNumbers[number] = Integer.toString(number + 1);
        }
        return patchNumbers;
    }

    public static void copyPatchFromBank(int patchNum, byte[] bankData, byte[] patchData) {
        copyPatchData(patchNum, bankData, patchData, true);
    }

    public static void copyPatchToBank(int patchNum, byte[] bankData, byte[] patchData) {
        copyPatchData(patchNum, bankData, patchData, false);
    }

    public static void copyPatchData(int patchNum, byte[] bankData, byte[] patchData, boolean fromBank) {
    
        final int MAX_BYTES_PER_MESSAGE = 256;
        final int SYSEX_FRAME_SIZE = SIZE_HEADER_DT1 + SIZE_TRAILER;
    
        final int sizeOfPatch = patchData.length - SYSEX_FRAME_SIZE;
    
        final int startLinearOffset = patchNum * sizeOfPatch;
        final int startsInMessage = startLinearOffset / MAX_BYTES_PER_MESSAGE;
    
        final int endLinearOffset = startLinearOffset + sizeOfPatch;
        final int endsInMessage = (endLinearOffset - 1) / MAX_BYTES_PER_MESSAGE;
    
        final int startOffsetInMessage = startLinearOffset
                - (startsInMessage * MAX_BYTES_PER_MESSAGE);
        final int startOffset = (SYSEX_FRAME_SIZE + MAX_BYTES_PER_MESSAGE) * startsInMessage + SIZE_HEADER_DT1 + startOffsetInMessage;
        if (startsInMessage == endsInMessage) {
            // Message in one piece.
            if (fromBank) {
                System.arraycopy(bankData, startOffset, patchData, SIZE_HEADER_DT1, sizeOfPatch);
            } else {
                System.arraycopy(patchData, SIZE_HEADER_DT1, bankData, startOffset, sizeOfPatch);
            }
        } else {
            final int firstPartLength = MAX_BYTES_PER_MESSAGE - startOffsetInMessage;
            final int secondPartLength = sizeOfPatch - firstPartLength;
            if (fromBank) {
                System.arraycopy(bankData, startOffset, patchData, SIZE_HEADER_DT1, firstPartLength);
                System.arraycopy(bankData, startOffset + firstPartLength + SYSEX_FRAME_SIZE, patchData, SIZE_HEADER_DT1 + firstPartLength, secondPartLength);
            } else {
                System.arraycopy(patchData, SIZE_HEADER_DT1, bankData, startOffset, firstPartLength);
                System.arraycopy(patchData, SIZE_HEADER_DT1 + firstPartLength, bankData, startOffset + firstPartLength + SYSEX_FRAME_SIZE, secondPartLength);
            }
        }
    }

    public static String trimName(String patchName) {
        if (null != patchName) {
            return patchName.trim();
        }
        return patchName;
    }

    public static String[] getToneBanks() {
        return new String[] {"a","b","i","r"};
    }
}
