//======================================================================================================================
// Summary: RolandXV5080PerfDriver.java
// Author: phil@muqus.com - 07/2001
// @version $Id$
// Notes: Perf driver for Roland XV-5080
//  1) A user patch dump consists of 52 sysex messages, total length 2205 bytes
//======================================================================================================================

package synthdrivers.RolandXV5080;
import java.io.*;
import core.*;

//======================================================================================================================
// Class: RolandXV5080PerfDriver
//======================================================================================================================

public class RolandXV5080PerfDriver extends Driver {
  final static int[] PATCH_SYSEX_START = new int[] { 0, 65, 222, 286, 381, 405, 429, 453, 477, 501, 525, 549, 573, 597, 621, 645, 669, 693, 717, 741, 765, 810, 855, 900, 945, 990, 1035, 1080, 1125, 1170, 1215, 1260, 1305, 1350, 1395, 1440, 1485, 1530, 1575, 1620, 1665, 1710, 1755, 1800, 1845, 1890, 1935, 1980, 2025, 2070, 2115, 2160};
  final static int[] PATCH_SYSEX_SIZE = new int[]  { 65, 157, 64, 95, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45};

  final static int PATCH_SIZE = 2205;

  final static int PATCH_NUMBER_OFFSET = 7;         // (F0 41 10 00 10 12 20 **).size - 1

  final static int PATCH_NAME_START = 10;           // (F0 41 10 00 10 12 20 ** 00 00).size
  final static int PATCH_NAME_SIZE = 12;

  final static int CHECKSUM_START = 6;              // (F0 41 10 00 10 12).size

  final static String[] BANK_NUMBERS = new String[] {"User"};
  final static String[] PATCH_NUMBERS = new String[] {
     "01-", "02-", "03-", "04-", "05-", "06-", "07-", "08-", "09-", "10-",
     "11-", "12-", "13-", "14-", "15-", "16-", "17-", "18-", "19-", "20-",
     "21-", "22-", "23-", "24-", "25-", "26-", "27-", "28-", "29-", "30-",
     "31-", "32-", "33-", "34-", "35-", "36-", "37-", "38-", "39-", "40-",
     "41-", "42-", "43-", "44-", "45-", "46-", "47-", "48-", "49-", "50-",
     "51-", "52-", "53-", "54-", "55-", "56-", "57-", "58-", "59-", "60-",
     "61-", "62-", "63-", "64-"
  };

  final static SysexHandler SYSEX_REQUEST_DUMP = new SysexHandler("F0 41 10 00 10 11 20 *patchNum* 00 00 00 01 00 00 00 F7");

//----------------------------------------------------------------------------------------------------------------------
// Constructor: RolandXV5080PerfDriver()
//----------------------------------------------------------------------------------------------------------------------

  public RolandXV5080PerfDriver() {
    super ("Perf","Phil Shepherd");
    sysexID = "F0411000101220**0000";
//    inquiryID = "F07E**06024000000A***********F7";
    sysexRequestDump = SYSEX_REQUEST_DUMP;
    patchSize = PATCH_SIZE;
    patchNameStart = PATCH_NAME_START;
    patchNameSize = PATCH_NAME_SIZE;
    deviceIDoffset = 0;

//  checksumStart =
//  checksumEnd =
//  checksumOffset =

    bankNumbers = BANK_NUMBERS;
    patchNumbers = PATCH_NUMBERS;
  }

//----------------------------------------------------------------------------------------------------------------------
// RolandXV5080PerfDriver->storePatch
//----------------------------------------------------------------------------------------------------------------------

  public void storePatch (Patch p, int bankNum, int patchNum) {
    updatePatchNum(p, patchNum);
    calculateChecksum(p);

    sendPatchWorker(p);
    try {Thread.sleep(300); } catch (Exception e){}
    setBankNum(bankNum);
    setPatchNum(patchNum);
  }

//----------------------------------------------------------------------------------------------------------------------
// RolandXV5080PerfDriver->sendPatch
//----------------------------------------------------------------------------------------------------------------------

  public void sendPatch (Patch p) {
    storePatch(p, 0, 0);
  }

//----------------------------------------------------------------------------------------------------------------------
// RolandXV5080PerfDriver->setBankNum
//----------------------------------------------------------------------------------------------------------------------

  public void setBankNum(int bankNum) {
    try {
      // BnH 00H mmH  n=MIDI channel number, mm=85
      send(0xB0+(getChannel()-1), 0x00, 85);
      // BnH 00H llH  n=MIDI channel number, ll=00H
      send(0xB0+(getChannel()-1), 0x20, 0);
    } catch (Exception e) {};
  }


//----------------------------------------------------------------------------------------------------------------------
// RolandXV5080PerfDriver->updatePatchNum
//----------------------------------------------------------------------------------------------------------------------

  public void updatePatchNum (Patch p, int patchNum) {
    for (int i = 0; i < PATCH_SYSEX_START.length; i++)
      p.sysex[PATCH_SYSEX_START[i] + PATCH_NUMBER_OFFSET] = (byte)(patchNum);
  }

//----------------------------------------------------------------------------------------------------------------------
// RolandXV5080PerfDriver->calculateChecksum(Patch)
//----------------------------------------------------------------------------------------------------------------------

  public void calculateChecksum(Patch p) {
    for (int i = 0; i < PATCH_SYSEX_START.length; i++) {
      int checksumStart = PATCH_SYSEX_START[i] + CHECKSUM_START;
      int checksumEnd = PATCH_SYSEX_START[i] + PATCH_SYSEX_SIZE[i] - 3;
      int checksumOffset = checksumEnd + 1;
      RolandXV5080PatchDriver.calculateChecksum(p.sysex, checksumStart, checksumEnd, checksumOffset);
    }
  }

//----------------------------------------------------------------------------------------------------------------------
// RolandXV5080PerfDriver->requestPatchDump
//----------------------------------------------------------------------------------------------------------------------

  public void requestPatchDump(int bankNum, int patchNum) {
    byte[] sysex = SYSEX_REQUEST_DUMP.toByteArray((byte)getChannel(), patchNum);
    RolandXV5080PatchDriver.calculateChecksum(sysex, 6, sysex.length - 3, sysex.length - 2);
    SysexHandler.send(getPort(), sysex);
  }
}
