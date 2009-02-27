package org.jsynthlib.drivers.alesis;

import org.jsynthlib.drivers.alesis.qs.QSConstants;

/**
 * Routines to help with Sysex'es.  Alesis compresses data to save space
 * This is taken from the manual.
 *
 * <p>Data is in a packed format in order to optimize data transfer.  Eight
 * MIDI bytes are used to transmit each block of 7 QS data bytes.  If the
 * 7 data bytes are looked at as one 56-bit word, the format for
 * transmission is eight 7-bit words beginning with the most significant
 * bit of the first byte, as follows:</p>
 *
 * SEVEN QUADRASYNTH BYTES:    <br/>
 * 0: A7 A6 A5 A4 A3 A2 A1 A0  <br/>
 * 1: B7 B6 B5 B4 B3 B2 B1 B0  <br/>
 * 2: C7 C6 C5 C4 C3 C2 C1 C0  <br/>
 * 3: D7 D6 D5 D4 D3 D2 D1 D0  <br/>
 * 4: E7 E6 E5 E4 E3 E2 E1 E0  <br/>
 * 5: F7 F6 F5 F4 F3 F2 F1 F0  <br/>
 * 6: G7 G6 G5 G4 G3 G2 G1 G0  <br/>
 *                             <br/>
 * 	     TRANSMITTED AS:       <br/>
 * 0: 0 A6 A5 A4 A3 A2 A1 A0   <br/>
 * 1: 0 B5 B4 B3 B2 B1 B0 A7   <br/>
 * 2: 0 C4 C3 C2 C1 C0 B7 B6   <br/>
 * 3: 0 D3 D2 D1 D0 C7 C6 C5   <br/>
 * 4: 0 E2 E1 E0 D7 D6 D5 D4   <br/>
 * 5: 0 F1 F0 E7 E6 E5 E4 E3   <br/>
 * 6: 0 G0 F7 F6 F5 F4 F3 F2   <br/>
 * 7: 0 G7 G6 G5 G4 G3 G2 G1   <br/>
 *
 * @author Zellyn Hunter (zellyn@bigfoot.com, zjh)
 * @version $Id$
 */

public class SysexRoutines
{
  // if true, then trace
  static boolean TRACE = false;

  /**
   * For testing
   */
  public static void main(String[] args)
  {
    System.out.println("\u00A5");
    byte[] bytes = { 0, 0, 0, 112, 14 };
    debug("Test1=" + getBits(bytes, 0, 30, 5, false));
    debug("Test2=" + getBits(bytes, 0, 30, 5, true));

    bigTest();
  }

  /**
   * Test it out on a real Sysex message that I captured - this is a
   * dump of Mix 90, which has "Jazz Piano" in position 0, and nothing
   * much in any of the other positions.
   */
  private static void bigTest()
  {
    boolean oldTrace = TRACE;
    TRACE = false;
    byte[] bytes =
    {
      (byte)0xf0, 0x00, 0x00, 0x0e, 0x0e, 0x0e, 0x5a, 0x41,
	  0x2a, 0x50, 0x56, 0x16, 0x00, 0x2c, 0x32, 0x50,
      0x73, 0x13, 0x00, 0x00, 0x47, 0x3f, 0x32, 0x0a,
	  0x03, 0x78, 0x7f, 0x07, 0x10, 0x58, 0x7d, 0x64,
      0x14, 0x46, 0x77, 0x7f, 0x0f, 0x20, 0x20, 0x7c,
	  0x49, 0x29, 0x0c, 0x60, 0x2e, 0x1f, 0x00, 0x40,
      0x75, 0x13, 0x53, 0x18, 0x40, 0x7f, 0x3e, 0x00,
	  0x60, 0x78, 0x27, 0x26, 0x31, 0x00, 0x7f, 0x79,
      0x00, 0x40, 0x71, 0x4f, 0x4c, 0x62, 0x00, 0x7e,
	  0x73, 0x01, 0x00, 0x63, 0x1f, 0x19, 0x45, 0x01,
      0x7c, 0x67, 0x03, 0x00, 0x46, 0x3f, 0x32, 0x0a,
	  0x03, 0x78, 0x4f, 0x07, 0x00, 0x0c, 0x7f, 0x64,
      0x14, 0x06, 0x70, 0x1f, 0x0f, 0x00, 0x18, 0x7e,
	  0x49, 0x29, 0x0c, 0x60, 0x3f, 0x1e, 0x00, 0x30,
      0x7c, 0x13, 0x53, 0x18, 0x40, 0x7f, 0x3c, 0x00,
	  0x60, 0x78, 0x27, 0x26, 0x31, 0x00, 0x7f, 0x79,
      0x00, 0x40, 0x71, 0x4f, 0x4c, 0x62, 0x00, 0x7e,
	  0x73, 0x01, 0x00, 0x63, 0x1f, 0x19, 0x45, 0x01,
      0x7c, 0x67, 0x03, 0x00, 0x46, 0x3f, 0x32, 0x0a,
	  0x03, 0x78, 0x4f, 0x07, 0x00, 0x0c, 0x7f, 0x64,
      0x14, 0x06, 0x70, 0x1f, 0x0f, (byte)0xf7
    };

    debug("Effect Channel     = " +
		  getBits(bytes, QSConstants.HEADER, (0 << 3) + 4, 4, false));
    String patchName = getChars(bytes, QSConstants.HEADER,
								QSConstants.MIX_NAME_START,
								QSConstants.MIX_NAME_LENGTH);
    debug("PatchName          = \"" + patchName + "\"");

    setChars("ZellynHunter", bytes, QSConstants.HEADER,
			 QSConstants.MIX_NAME_START, QSConstants.MIX_NAME_LENGTH);
    patchName = getChars(bytes, QSConstants.HEADER, QSConstants.MIX_NAME_START,
						 QSConstants.MIX_NAME_LENGTH);
    debug("PatchName          = \"" + patchName + "\"");

    TRACE = oldTrace;
  }

  /**
   * Given a compressed sysex, retrieve a run of consecutive bits as an integer
   * @param sysex the actual bytes of the sysex message
   * @param headerCount how many bytes of the sysex are used up as a header -
   *                    compression starts after the header
   * @param msBit the starting (most significant) bit
   * @param bitSize the number of bits to return - 1 to 8
   * @param signed if true, take the two's complement of the result
   * @return int the value of the requested bits
   */
  public static int getBits(byte[] sysex, int headerCount, int msBit,
							int bitSize, boolean signed)
  {
    int returnVal = 0;

    // calculate ending bit
    int lsBit = msBit - bitSize + 1;
    // calculate which byte the bits fall in
    int msByte = msBit / 7;
    int lsByte = lsBit / 7;
    // calculate where in the byte the bits fall
    int msBitPos = msBit % 7;
    int lsBitPos = lsBit % 7;

    trace("msBit       = " + msBit);
    trace("lsBit       = " + lsBit);
    trace("msByte      = " + msByte);
    trace("lsByte      = " + lsByte);
    trace("msBitPos    = " + msBitPos);
    trace("lsBitPos    = " + lsBitPos);

    // check if the value spans two bytes
    if (msByte!=lsByte)
    {
      trace("Calculating top part");
      // grab the bits of the top part, shift up above lower part

      returnVal = (sysex[headerCount + msByte] & 0xff) << (7 - lsBitPos);
      trace("Sysex byte  = " + sysex[headerCount + msByte]);
      trace("& 0xff      = " + (sysex[headerCount + msByte] & 0xff));
      trace("7-lsBitPos  = " + (7 - lsBitPos));
      trace("returnVal   = " + returnVal);
    }

    trace("Calculating bottom part");
    // get the lower part, shift it down, and OR it in
    returnVal |= (sysex[headerCount + lsByte] & 0xff) >> (lsBitPos);
    trace("Sysex byte  = " + sysex[headerCount + lsByte]);
    trace("& 0xff      = " + (sysex[headerCount + lsByte] & 0xff));
    trace(">>lsBitPos  = " +
		  ((sysex[headerCount + lsByte] & 0xff) >> (lsBitPos)));
    trace("returnVal   = " + returnVal);

    // mask out only the bytes within the length we want
    // use a power of 2, minus 1, to get a bunch of 1's
    int mask = (1 << bitSize) - 1;
    trace("Mask        = " + mask);
    
    returnVal &= mask;

    // if it's signed, and the high bit is 1, then fill out all bits
    // above the value with 1's for 2's complement

    // only do it if it's signed, and the high bit is 1
    if (signed
        && (  (returnVal & (1<<(bitSize-1))) > 0  )   )
    {
      trace("returnVal   = " + returnVal);
      trace("Signed value");

      // all 1's, except for the old mask;
      int signMask = -1 ^ mask;
      trace("signMask    = " + signMask);

      returnVal |= signMask;
    }

    trace("return value= " + returnVal);
    return returnVal;
  }

  /**
   * Given a compressed sysex, set a run of consecutive bits to a value
   * @param value the integer value to set the bits to
   * @param sysex the actual bytes of the sysex message
   * @param headerCount how many bytes of the sysex are used up as a header -
   *                    compression starts after the header
   * @param msBit the starting (most significant) bit
   * @param bitSize the number of bits to return - 1 to 8
   */
  public static void setBits(int value, byte[] sysex, int headerCount,
							 int msBit, int bitSize)
  {
    // calculate ending bit
    int lsBit = msBit - bitSize + 1;
    // calculate which byte the bits fall in
    int msByte = msBit / 7;
    int lsByte = lsBit / 7;
    // calculate where in the byte the bits fall
    int msBitPos = msBit % 7;
    int lsBitPos = lsBit % 7;

    trace("msBit    = " + msBit);
    trace("lsBit    = " + lsBit);
    trace("msByte   = " + msByte);
    trace("lsByte   = " + lsByte);
    trace("msBitPos = " + msBitPos);
    trace("lsBitPos = " + lsBitPos);

    // check if the value spans two bytes
    if (msByte!=lsByte)
    {
      trace("Setting top part");

      // get just the top part
      int topPart = value >> (7 - lsBitPos);
      trace("top         = " + topPart);

      // mask the relevant bits: 2 ^ width - 1, for 'width' 1's
      int mask = (1 << (msBitPos + 1)) - 1;
      trace("mask        = " + mask);

      // mask value in to cut off high sign bits if we're negative
      topPart &= mask;
      trace("masked top  = " + topPart);

      // then reverse the mask - to clear bits
      mask = -1 ^ mask;
      trace("reverse mask= " + mask);

      // clear out old value
      sysex[headerCount + msByte] &= mask;

      // write in new value
      sysex[headerCount + msByte] |= topPart;
    }

    trace("Setting bottom part");

    // calculate how many bits the bottom part uses
    int bottomLen;
    if (msByte==lsByte)     // no split
    {
      // it's all in one byte, so it's just the length
      bottomLen = bitSize;
    }
    else                        // split
    {
      // it's everything up to the end of the bottom part
      bottomLen = 7 - lsBitPos;
    }

    // mask is as wide as 'width' 1's, so use 2 ^ width - 1
    int mask = (1 << (bottomLen + 1)) - 1;
    trace("mask        = " + mask);

    // apply mask to get just bottom bits
    int bottomPart = value & mask;
    trace("bottom part = " + bottomPart);

    // shift the value up to the correct position
    bottomPart <<= lsBitPos;
    trace("and shifted = " + bottomPart);

    // shift the mask up to the correct position
    mask <<= lsBitPos;
    trace("shifted mask= " + mask);

    // then reverse the mask - to clear bits
    mask = -1 ^ mask;
    trace("reverse mask= " + mask);

    // clear out old value
    sysex[headerCount + lsByte] &= mask;

    // write in new value
    sysex[headerCount + lsByte] |= bottomPart;

  }


  /**
   * Given a compressed sysex, retrieve a run of consecutive characters,
   * starting at the given bit
   * @param sysex the actual bytes of the sysex message
   * @param headerCount how many bytes of the sysex are used up as a header -
   *                    compression starts after the header
   * @param msBit the starting (most significant) bit
   * @param count the number of characters to return
   * @return the uncompressed and decoded characters
   */
  public static String getChars(byte[] sysex, int headerCount, int msBit,
								int count)
  {

    StringBuffer buf = new StringBuffer();
    int charCode;

    for (int i=0; i<count; i++)
    {
      charCode = getBits(sysex, headerCount, msBit + i*7, 7, false);

      buf.append(decodeChar(charCode));
    }

    return buf.toString();
  }

  /**
   * Given a compressed sysex, set a run of consecutive characters,
   * starting at the given bit.
   * If chars has more characters than count, set only count characters
   * @param chars the characters to set
   * @param sysex the actual bytes of the sysex message
   * @param headerCount how many bytes of the sysex are used up as a header -
   *                    compression starts after the header
   * @param msBit the starting (most significant) bit
   * @param count the maximum number of characters to set
   */
  public static void setChars(String chars, byte[] sysex, int headerCount,
							  int msBit, int count)
  {
    int length = chars.length();
    int alesisCharCode;

    for (int i=0; ((i<count) && (i<length)); i++)
    {
      alesisCharCode = encodeChar(chars.charAt(i));
      
      setBits(alesisCharCode, sysex, headerCount, msBit + i*7, 7);
    }

  }

  /**
   * Given an Alesis character code for a character, return the character it
   * represents
   * @param charCode the code for the character, as stored in the Alesis
   * @return the char represented by charCode
   */
  public static char decodeChar(int charCode)
  {
    // check if it's a known character
    if ((charCode>=0) && (charCode<QSConstants.QS_LETTERS.length()))
    {
      return QSConstants.QS_LETTERS.charAt(charCode);
    }
    else
    {
      // return a default character if we can't match it
      return QSConstants.QS_UNKNOWN_CHARACTER;
    }
  }

  /**
   * Given a character, return the Alesis character code that represents it
   * @param ch the character
   * @return the Alesis character code that represents ch
   */
  public static int encodeChar(char ch)
  {
    // check if it's a known character
    int index = QSConstants.QS_LETTERS.indexOf(ch);

    return (index==-1) ? QSConstants.QS_UNKNOWN_CHARACTER_CODE : index;
  }
      
  public static void trace(String message)
  {
    if (TRACE)
    {
      System.out.println(message);
    }
  }

  public static void debug(String message)
  {
    System.out.println(message);
  }
}
