//======================================================================================================================
// Summary: NibbleSysex.java
// Author: phil@muqus.com - 07/2001
// Notes: Originally developed for PeaveyPC1600 drivers to simplify sysex processing
//======================================================================================================================

package core;

import java.io.*;

//======================================================================================================================
// Class: NibbleSysex
//======================================================================================================================

public class NibbleSysex extends Object {

  //===== Instance variables
  public int offset;
  public byte[] sysex;

//----------------------------------------------------------------------------------------------------------------------
// Constructor: NibbleSysex(byte[])
//----------------------------------------------------------------------------------------------------------------------

  public NibbleSysex(byte[] asysex) {
    offset = 0;
    sysex = asysex;
  }

//----------------------------------------------------------------------------------------------------------------------
// Constructor: NibbleSysex(byte[], int)
//----------------------------------------------------------------------------------------------------------------------

  public NibbleSysex(byte[] sysex, int offset) {
    this.offset = offset;
    this.sysex = sysex;
  }

//=====================================================================================================================
// NibbleSysex->getNibbleInt
//=====================================================================================================================

  public int getNibbleInt (int nBytesPerData, int iMultiplier) {
    int iReturn = 0;

    for (int i = 0; i < nBytesPerData; i++) {
      iReturn = iReturn * iMultiplier;
      iReturn += sysex[offset++];
    }

    return iReturn;
  }

//=====================================================================================================================
// NibbleSysex->getNibbleStr
//=====================================================================================================================

  public String getNibbleStr(int nData, int nBytesPerData, int iMultiplier) {
    byte[] bytes = new byte[nData];

    for (int i = 0; i < nData; i++) {
      bytes[i] = (byte)getNibbleInt(nBytesPerData, iMultiplier);
    }

    try {
      return new String(bytes, "US-ASCII");
    } catch (UnsupportedEncodingException ex) {
      return "?Error?";
    }
  }

//=====================================================================================================================
// NibbleSysex->putSysex
//=====================================================================================================================

  public void putSysex(byte src) {
    sysex[offset++] = src;
  }

//=====================================================================================================================
// NibbleSysex->putSysex
//=====================================================================================================================

  public void putSysex(byte[] src) {
    System.arraycopy(src, 0, sysex, offset, src.length);
    offset += src.length;
  }

//=====================================================================================================================
// NibbleSysex->putSysex
//=====================================================================================================================

  public void putSysex(byte[] src, int srcOffset, int srcLength) {
    System.arraycopy(src, srcOffset, sysex, offset, srcLength);
    offset += srcLength;
  }

//=====================================================================================================================
// NibbleSysex->putNibbleInt
//=====================================================================================================================

  public void putNibbleInt(int anInt, int nBytesPerData, int iMultiplier) {
    int[] arNibbleInts = new int[nBytesPerData];

    for (int i = 0; i < nBytesPerData; i++) {
      arNibbleInts[i] = anInt % 16;
      anInt = (anInt - arNibbleInts[i])/16;
    }

    for (int i = 0; i < nBytesPerData; i++)
      sysex[offset++] = (byte)arNibbleInts[nBytesPerData - 1 - i];
  }

//=====================================================================================================================
// NibbleSysex->putNibbleStr
//=====================================================================================================================

  public void putNibbleStr(String str, int nData, int nBytesPerData, int iMultiplier) {
    byte[] bytes = new byte[str.length() > nData ? str.length() : nData];

    try {
      byte[] justStrBytes = str.getBytes("US-ASCII");
      System.arraycopy(justStrBytes, 0, bytes, 0, justStrBytes.length);
      //----- Pad from str.length to nData with " "
      if (str.length() < nData) {
        byte blank = (" ".getBytes("US-ASCII"))[0];
        for (int i = str.length(); i < nData; i++) {
          bytes[i] = blank;
        }
      }
    } catch (UnsupportedEncodingException ex) {
      ErrorMsg.reportError("Error", "NibbleSysex->putNibbleStr: " + str, ex);
    }

    for (int i = 0; i < nData; i++)
      this.putNibbleInt(bytes[i], nBytesPerData, iMultiplier);
  }
}