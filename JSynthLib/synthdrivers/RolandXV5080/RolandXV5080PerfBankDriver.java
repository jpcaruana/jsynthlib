//======================================================================================================================
// Summary: RolandXV5080PerfBankDriver.java
// Author: phil@muqus.com - 07/2001
// Notes: Perf bank driver for Roland - XV5080
//  1) Assumes a perf bank dump consists of all 64 user patches.  Dumped from XV-5080 using:
//      - Press System/Utility twice so LED flashes
//      - Use inc/dev to select Menu 2
//      - F1 Data XFer
//      - Type = Perform | Block = USER 01 - 64 | Destination MIDI
//      - Trans
//======================================================================================================================

package synthdrivers.RolandXV5080;
import core.*;
import java.io.*;
import javax.swing.*;

//======================================================================================================================
// Class: RolandXV5080PerfDriver
//======================================================================================================================

public class RolandXV5080PerfBankDriver extends BankDriver {

  final static SysexHandler SYSEX_REQUEST_DUMP = new SysexHandler("F0 41 10 00 10 11 20 *patchNum* 00 00 00 40 00 00 00 F7");

//----------------------------------------------------------------------------------------------------------------------
// Constructor: RolandXV5080PerfBankDriver()
//----------------------------------------------------------------------------------------------------------------------

  public RolandXV5080PerfBankDriver() {
    manufacturer = "Roland";
    model = "XV5080";
    patchType = "PerfBank";
    id = "RdXv5";
    sysexID = "F0411000101220000000";
//    inquiryID = "F07E**06024000000A***********F7";
    sysexRequestDump = SYSEX_REQUEST_DUMP;

    patchSize = 64 * RolandXV5080PerfDriver.PATCH_SIZE;
//    patchNameStart =
//    patchNameSize =
    deviceIDoffset = 0;

    numColumns = 4;
    singleSysexID = "F0411000101220**0000";
    singleSize = RolandXV5080PerfDriver.PATCH_SIZE;
authors="Phil Shepherd";
//  checksumStart =
//  checksumEnd =
//  checksumOffset =

    bankNumbers = RolandXV5080PerfDriver.BANK_NUMBERS;
    patchNumbers = RolandXV5080PerfDriver.PATCH_NUMBERS;

    numPatches = patchNumbers.length;
  }

//----------------------------------------------------------------------------------------------------------------------
// RolandXV5080PerfBankDriver->setBankNum
//----------------------------------------------------------------------------------------------------------------------

  public void setBankNum(int bankNum) {
    try {
      // BnH 00H mmH  n=MIDI channel number, mm=65H
      PatchEdit.MidiOut.writeShortMessage(port, (byte)(0xB0+(channel-1)), (byte)0x00, (byte)0x65);
      // BnH 00H llH  n=MIDI channel number, ll=00H
      PatchEdit.MidiOut.writeShortMessage(port, (byte)(0xB0+(channel-1)), (byte)0x20, (byte)0);
    } catch (Exception e) {};
  }

//----------------------------------------------------------------------------------------------------------------------
// RolandXV5080PerfBankDriver->getPatchName(Patch, int)
//----------------------------------------------------------------------------------------------------------------------

  public String getPatchName(Patch p) {
    return numPatches + " patches";
  }

//----------------------------------------------------------------------------------------------------------------------
// RolandXV5080PerfBankDriver->getPatchName(Patch, int)
//----------------------------------------------------------------------------------------------------------------------

  public String getPatchName(Patch p, int patchNum) {
    try {
      return new String(
        p.sysex, RolandXV5080PerfDriver.PATCH_SIZE * patchNum + RolandXV5080PerfDriver.PATCH_NAME_START,
        RolandXV5080PerfDriver.PATCH_NAME_SIZE,
        "US-ASCII"
      );
    } catch (UnsupportedEncodingException ex) {
      return "-??????-";
    }
  }

//----------------------------------------------------------------------------------------------------------------------
// RolandXV5080PerfBankDriver->setPatchName(Patch, int, String)
//----------------------------------------------------------------------------------------------------------------------

  public void setPatchName(Patch bank, int patchNum, String name) {
    Patch p = getPatch(bank, patchNum);
    Driver singleDriver = p.getDriver();
    singleDriver.setPatchName(p, name);
    singleDriver.calculateChecksum(p);
    putPatch(bank, p, patchNum);
  }

//----------------------------------------------------------------------------------------------------------------------
// RolandXV5080PerfBankDriver->getPatch
//----------------------------------------------------------------------------------------------------------------------

  public Patch getPatch(Patch bank, int patchNum) {
    try {
      byte[] sysex = new byte[RolandXV5080PerfDriver.PATCH_SIZE];
      System.arraycopy(bank.sysex, RolandXV5080PerfDriver.PATCH_SIZE * patchNum, sysex, 0, RolandXV5080PerfDriver.PATCH_SIZE);
      Patch p = new Patch(sysex);
      p.ChooseDriver();
      return p;
    } catch (Exception ex) {
      ErrorMsg.reportError("Error", "Error in XV5080 Perf Bank Driver", ex);
      return null;
    }
  }

//----------------------------------------------------------------------------------------------------------------------
// RolandXV5080PerfBankDriver->putPatch
//----------------------------------------------------------------------------------------------------------------------

  public void putPatch(Patch bank, Patch p, int patchNum) {
    Patch pInsert = new Patch(p.sysex);
    RolandXV5080PerfDriver singleDriver = (RolandXV5080PerfDriver)pInsert.getDriver();
    singleDriver.updatePatchNum(pInsert, patchNum);
    singleDriver.calculateChecksum(pInsert);

    bank.sysex = Utility.byteArrayReplace(
      bank.sysex, RolandXV5080PerfDriver.PATCH_SIZE * patchNum, RolandXV5080PerfDriver.PATCH_SIZE,
      pInsert.sysex, 0, RolandXV5080PerfDriver.PATCH_SIZE);
  }

//----------------------------------------------------------------------------------------------------------------------
// RolandXV5080PerfBankDriver->requestPatchDump
//----------------------------------------------------------------------------------------------------------------------

  public void requestPatchDump(int bankNum, int patchNum) {
    byte[] sysex = SYSEX_REQUEST_DUMP.toByteArray((byte)channel, patchNum);
    RolandXV5080PatchDriver.calculateChecksum(sysex, 6, sysex.length - 3, sysex.length - 2);
    SysexHandler.send(port, sysex);
  }
}
