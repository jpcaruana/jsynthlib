//======================================================================================================================
// Summary: SysexMatcher.java
// Modifications by: phil@muqus.com - 07/2001
// Notes:
//   1) Developed to bring the Patch header match code used in Patch.ChooseDriver, Patch.dissect etc.. into one location.
//   2) This enabled addition of detectCorruptSysex, which gives useful debugging information on why a patch failed
//      to match any drivers (this info very useful to the user as patches can fail to match due to incorrectly
//      configured MIDI systems).
//======================================================================================================================

package org.jsynthlib.core;

//----------------------------------------------------------------------------------------------------------------------
// Class: SysexMatcher
//----------------------------------------------------------------------------------------------------------------------

public class SysexMatcher extends Object {
  //----- Constants
  public final int MAX_MATCH_LENGTH = 16;
  public final byte F7 = (byte)0xF7;
  public final byte F0 = (byte)0xF0;

  //----- Instance variables
  protected String sSysexHeader;                            // String representation of header of sysex data
  protected int numSysexMsgs;                               // Number of sysex messges in the sysex data
  protected byte[] sysex;                                   // The actual data (stored by reference, not copied!)

//----------------------------------------------------------------------------------------------------------------------
// Constructor: SysexMatcher(String)
//----------------------------------------------------------------------------------------------------------------------

  public SysexMatcher(byte[] sysex) {
    setSysex(sysex);
  }

//----------------------------------------------------------------------------------------------------------------------
// SysexMatcher->setSysex
//----------------------------------------------------------------------------------------------------------------------

  public void setSysex(byte[] sysex) {
    int nBytes = sysex.length < MAX_MATCH_LENGTH ? sysex.length : MAX_MATCH_LENGTH;

     //----- Convert sysex byte array to dense hex string, sSysexHeader
    StringBuffer sb = new StringBuffer();
    sb.append("F0");                                        // Ignore the first byte - assume it is "F0"
    for (int i = 1; i < nBytes; i++) {
      if (sysex[i] < 16)
        sb.append ('0');
      sb.append(Integer.toHexString(sysex[i]));
    }
    sSysexHeader = sb.toString();

    //----- Count number of sysex messages
    numSysexMsgs = 0;
    for (int i = 0; i < sysex.length; i++) {
      if (sysex[i] == F7)
        numSysexMsgs++;
    }

    //----- Store reference to the byte array
    this.sysex = sysex;

//ErrorMsg.reportStatus("SysexMatcher->setSysex | numSysexMsgs: " + numSysexMsgs + " | size: " + this.sysex.length + " | sSysexHeader: " + sSysexHeader);
  }

//----------------------------------------------------------------------------------------------------------------------
// SysexMatcher->matches
// Notes:
//    1) sysexID is a Dense hex strings of the form:
//        "F000001B*B0015**00**0*F7"
//       where * can match any character in sSysexHeader
// Returns: true/false - true if:
//        1) sysexID matches this.sSysexHeader
//    and 2) size is zero, or size matches this.sysex.length
//    and 2) numSysexMsgs is zero, or numSysexMsgs matches this.numSysexMsgs
//----------------------------------------------------------------------------------------------------------------------

  public boolean matches(String sysexID, int size, int numSysexMsgs) {
    if (size != 0 && size != sysex.length)
      return false;

    if (numSysexMsgs != 0 && numSysexMsgs != this.numSysexMsgs)
      return false;

    StringBuffer sbDriver = new StringBuffer(sysexID);
    for (int i = 0; i < sbDriver.length(); i++) {
      if (sbDriver.charAt(i) == '*')
        sbDriver.setCharAt(i, sSysexHeader.charAt(i));
    }

    return (sbDriver.toString().equalsIgnoreCase(sSysexHeader.substring(0, sbDriver.length())));
  }

//----------------------------------------------------------------------------------------------------------------------
// SysexMatcher->detectCorruptSysex(Driver, Patch, String, size, numSysexMsgs)
// Notes: Called during Patch.dissect for patches which have no detected driver
// Returns: String
//    If we have a sysexID match, return user friendly string saying why can not match this driver
//    If no match, or no detectable problem, return empty string
//----------------------------------------------------------------------------------------------------------------------

  public String detectCorruptSysex(IDriver driver, IPatch patch, String sysexID, int size, int numSysexMsgs) {
    if (!matches(sysexID, 0, 0))                            // Not a match
      return "";

    if (size != 0 && size != sysex.length) {
      return("Matches sysexID for driver: " + driver.toString() + "\n" +
        "   But patch size: " + sysex.length +
        " does not equal required size: " + size + "\n"
      );
    }

    if (numSysexMsgs != 0 && numSysexMsgs != this.numSysexMsgs) {
      return("Matches sysexID for driver: " + driver.toString() + "\n" +
        "   But number of sysex messages in patch: " + this.numSysexMsgs +
        " does not equal required number: " + numSysexMsgs + "\n"
      );
    }

    return "";
  }

//----------------------------------------------------------------------------------------------------------------------
// SysexMatcher->detectCorruptSysex()
// Notes: Called during Patch.dissect for patches which have no detected driver,
// Returns: String
//    User friendly message indicating problems with this sysex
//    If no detectable problem, return empty string
//----------------------------------------------------------------------------------------------------------------------

  public String detectCorruptSysex() {
    StringBuffer sb = new StringBuffer();
    if (sysex[0] != F0)
      sb.append("First character of sysex data is not F0.\n");

    if (sysex[sysex.length - 1] != F7)
      sb.append("Last character of sysex data is not F7.\n");

    return sb.toString();
  }

} // End Class: SysexMatcher
