//======================================================================================================================
// Summary: SysexHandler.java
// Modifications by: phil@muqus.com - 07/2001
// Notes:
//   A class for efficient and convenient processing of sysex messages.
//   1) Introduced during development of SysexGetDialog
//   2) Understands space seperated hex strings of the form:
//        "F0 00 00 1B 0B @@ 14 *patchNum* 00 *bankNum* 00 F7"
//      Or dense hex strings of the form:
//        "F000001B*B0015**00**0*F7"  (though this syntax can not handled multiple values such as *patchNum*, *bankNum*)
//   3) On converstion to byte array (prior to being sent as a sysex message):
//       *name* - Replaced by the appropriate value
//       ** - Replace by THE value
//       @@ - Replaced by the channel number
//       ## - Replaced by (channel number + 16)
//   4) See Driver->requestPatchDump for example usage
//   5) Basic concept is to store the sysex as a byte array, using an index to the special bytes (eg @@, *patchNum*, ..)
//      to insert values during toByteArray/send.
//
//======================================================================================================================

package core;
import java.io.*;
import java.util.*;

//----------------------------------------------------------------------------------------------------------------------
// Class: SysexHandler
//----------------------------------------------------------------------------------------------------------------------

public class SysexHandler extends Object implements Serializable {
  //----- Instance variables
  protected byte[] sysex = null;
  protected Vector vNameValueIndex = new Vector();
  protected int channelIndex = -1;
  protected int channel16Index = -1;

//----------------------------------------------------------------------------------------------------------------------
// Constructor: SysexHandler(String)
//----------------------------------------------------------------------------------------------------------------------

  public SysexHandler(String src) {
    setSysex(src);
  }

//----------------------------------------------------------------------------------------------------------------------
// SysexHandler->length
//----------------------------------------------------------------------------------------------------------------------

  public int length() {
    return sysex.length;
  }

//----------------------------------------------------------------------------------------------------------------------
// SysexHandler->setSysex
// Notes:
//   2) Understands space seperated hex strings of the form:
//        "F0 00 00 1B 0B @@ 14 *patchNum* 00 *bankNum* 00 F7"
//      Or dense hex strings of the form:
//        "F000001B*B0015**00**0*F7"  (though this syntax can not handled multiple values such as *patchNum*, *bankNum*)
//----------------------------------------------------------------------------------------------------------------------

  public void setSysex(String src) {
    //----- Reset instance variables
    channelIndex = -1;
    channel16Index = -1;
    vNameValueIndex.removeAllElements();

    if (src.length() < 3 || src.charAt(2) != ' ') {            // src is a dense hex string
      setSysexFromDenseHexStr(src);
      return;
    }


    StringTokenizer st = new StringTokenizer(src);
    sysex = new byte[st.countTokens()];

    int iByte = 0;
    for( ; st.hasMoreTokens(); iByte++) {
      String sToken = st.nextToken();

      switch (sToken.charAt(0)) {
        case '*':
          vNameValueIndex.addElement(new NameValue(sToken.substring(1, sToken.length()-1), iByte));
          sysex[iByte] = (byte)0;
          break;

        case '@':
          channelIndex = iByte;
          sysex[iByte] = (byte)0;
          break;

        case '#':
          channel16Index = iByte;
          sysex[iByte] = (byte)0;
          break;

        default:
          try {
        	  sysex[iByte] = (byte)Integer.parseInt(sToken, 16);
            } catch (Exception ex) {
              ErrorMsg.reportError(
                "SysexHandler",
                "Invalid number, " + sToken + " in space separated hex string: " + src
              );
              return;
            }

          break;
      }
    }
  }

//----------------------------------------------------------------------------------------------------------------------
// SysexHandler->setSysexFromDenseHexStr
// Notes:
//   1) Dense hex strings of the form:
//        "F000001B0B@@15**00**00F7"  (though this syntax can not handled multiple values such as *patchNum*, *bankNum*)
//   2) The ** values end up with the name ** such that they can be replaced using:
//       Either       - sysexHandler.toByteArray((byte)channel, new SysexHandler.NameValue("**", patchNum));
//       Or(simpler)  - sysexHandler.toByteArray((byte)channel, patchNum);
//   3) private as setSysex initialises channelIndex etc..
//----------------------------------------------------------------------------------------------------------------------

  private void setSysexFromDenseHexStr(String src) {
    int nBytes = src.length()/2;
    sysex = new byte[nBytes];

    int iSrc = 0;
    for (int iByte = 0; iByte < nBytes; iByte++, iSrc += 2) {
      switch (src.charAt(iSrc)) {
        case '*':
          vNameValueIndex.addElement(new NameValue(src.substring(iSrc, iSrc + 2), iByte));
          break;

        case '@':
          channelIndex = iSrc;
          break;

        case '#':
          channel16Index = iSrc;
          break;

        default:
          if (src.charAt(iSrc + 1) == '*') {                // eg 0*
            vNameValueIndex.addElement(new NameValue(src.substring(iSrc, iSrc + 2), iByte));
          } else {
            try {
          	  sysex[iByte] = (byte)Integer.parseInt(src.substring(iSrc, iSrc + 2), 16);
            } catch (Exception ex) {
              ErrorMsg.reportError(
                "SysexHandler",
                "Invalid number, " + src.substring(iSrc, iSrc + 2) + " in dense hex string: " + src
              );
              return;
            }
          break;
        }
      }
    }
  }

//----------------------------------------------------------------------------------------------------------------------
// SysexHandler->toByteArray(byte, NameValue[])
// Returns: sysex as a byte array with replacable values set via data passed as arguments
//----------------------------------------------------------------------------------------------------------------------

  public byte[] toByteArray(byte channel, NameValue[] nameValues) {
    // Replace the channel number
    if (channelIndex != -1)
      sysex[channelIndex] = (byte)((int)channel - 1);
    if (channel16Index != -1)
      sysex[channel16Index] = (byte)((int)channel - 1 + 16);

    // Replace values
    for (Enumeration en = vNameValueIndex.elements(); en.hasMoreElements();) {
      NameValue nameValueIndex = (NameValue)en.nextElement();

      boolean bValueFound = false;
      for (int i = 0; i < nameValues.length; i++) {
        if (nameValueIndex.getName().equalsIgnoreCase(nameValues[i].getName())) {
          sysex[nameValueIndex.getValue()] = (byte)nameValues[i].getValue();
          bValueFound = true;
          break;
        }
      }
      if (!bValueFound) {
        ErrorMsg.reportError("SysexHandler", "No value specified for: " + nameValueIndex.getName());
        return sysex;
      }
    }

    return sysex;
  }

//----------------------------------------------------------------------------------------------------------------------
// SysexHandler->toByteArray(byte, int)
// Notes: A simplified methodology to use when there's only one value to be replaced (so the *patchNum* syntax is
//   wasteful)
//----------------------------------------------------------------------------------------------------------------------

  public byte[] toByteArray(byte channel, int value) {
    // Replace the channel number
    if (channelIndex != -1)
      sysex[channelIndex] = (byte)((int)channel - 1);
    if (channel16Index != -1)
      sysex[channel16Index] = (byte)((int)channel - 1 + 16);

    // Replace values
    for (Enumeration en = vNameValueIndex.elements(); en.hasMoreElements();)
      sysex[((NameValue)en.nextElement()).getValue()] = (byte)value;

    return sysex;
  }

//----------------------------------------------------------------------------------------------------------------------
// SysexHandler->toByteArray()
// Notes: Returns a byte array where @@, ** etc.. have been replaced by 0
//----------------------------------------------------------------------------------------------------------------------

  public byte[] toByteArray() {
    return toByteArray((byte)0, 0);
  }

//----------------------------------------------------------------------------------------------------------------------
// SysexHandler->send(int, byte, ....)
//----------------------------------------------------------------------------------------------------------------------

  public void send(int port, byte channel) {
    send(port, channel, 0);                                 // N.B. Uses simplified toByteArray methodology
  }

  public void send(int port, byte channel, int value) {
    byte[] sysex = toByteArray(channel, value);             // N.B. Uses simplified toByteArray methodology
    send(port, sysex);
  }

  public void send(int port, byte channel, NameValue nameValue1) {
    NameValue[] nameValues = new NameValue[] {nameValue1};
    send(port, channel, nameValues);
  }

  public void send(int port, byte channel, NameValue nameValue1, NameValue nameValue2) {
    NameValue[] nameValues = new NameValue[] {nameValue1, nameValue2};
    send(port, channel, nameValues);
  }

  public void send(int port, byte channel, NameValue[] nameValues) {
    byte[] sysex = toByteArray(channel, nameValues);
    send(port, sysex);
   }

//----------------------------------------------------------------------------------------------------------------------
// static SysexHandler->send
// Notes: Convenience method for sending a sysex message (static so can be accessed from non-class methods)
//----------------------------------------------------------------------------------------------------------------------

  public static void send(int port, byte[] sysex) {
    try {
ErrorMsg.reportStatus("static SysexHandler->send | port: " + port, sysex);
      PatchEdit.MidiOut.writeLongMessage(port, sysex);
    } catch (Exception ex) {
      ErrorMsg.reportStatus(ex);
    }
  }
} // End Class: SysexHandler