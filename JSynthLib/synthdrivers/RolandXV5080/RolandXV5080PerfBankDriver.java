//======================================================================================================================
// Summary: RolandXV5080PerfBankDriver.java
// Author: phil@muqus.com - 07/2001
// @version $Id$
// Notes: Perf bank driver for Roland - XV5080
//  1) Assumes a perf bank dump consists of all 64 user patches.  Dumped from XV-5080 using:
//      - Press System/Utility twice so LED flashes
//      - Use inc/dev to select Menu 2
//      - F1 Data XFer
//      - Type = Perform | Block = USER 01 - 64 | Destination MIDI
//      - Trans
//======================================================================================================================

package synthdrivers.RolandXV5080;
import java.io.UnsupportedEncodingException;

import core.BankDriver;
import core.Driver;
import core.ErrorMsg;
import core.Patch;
import core.SysexHandler;
import core.Utility;

//======================================================================================================================
// Class: RolandXV5080PerfDriver
//======================================================================================================================

public class RolandXV5080PerfBankDriver extends BankDriver {

  final static SysexHandler SYSEX_REQUEST_DUMP = new SysexHandler("F0 41 10 00 10 11 20 *patchNum* 00 00 00 40 00 00 00 F7");

//----------------------------------------------------------------------------------------------------------------------
// Constructor: RolandXV5080PerfBankDriver()
//----------------------------------------------------------------------------------------------------------------------

  public RolandXV5080PerfBankDriver() {
    super ("PerfBank","Phil Shepherd",RolandXV5080PerfDriver.PATCH_NUMBERS.length,4);
    sysexID = "F0411000101220000000";
//    inquiryID = "F07E**06024000000A***********F7";
    sysexRequestDump = SYSEX_REQUEST_DUMP;

    patchSize = 64 * RolandXV5080PerfDriver.PATCH_SIZE;
//    patchNameStart =
//    patchNameSize =
    deviceIDoffset = 0;

    singleSysexID = "F0411000101220**0000";
    singleSize = RolandXV5080PerfDriver.PATCH_SIZE;
//  checksumStart =
//  checksumEnd =
//  checksumOffset =

    bankNumbers = RolandXV5080PerfDriver.BANK_NUMBERS;
    patchNumbers = RolandXV5080PerfDriver.PATCH_NUMBERS;

  }

//----------------------------------------------------------------------------------------------------------------------
// RolandXV5080PerfBankDriver->setBankNum
//----------------------------------------------------------------------------------------------------------------------

  public void setBankNum(int bankNum) {
    try {
      // BnH 00H mmH  n=MIDI channel number, mm=65H
      send(0xB0+(getChannel()-1), 0x00, 0x65);
      // BnH 00H llH  n=MIDI channel number, ll=00H
      send(0xB0+(getChannel()-1), 0x20, 0);
    } catch (Exception e) {};
  }

//----------------------------------------------------------------------------------------------------------------------
// RolandXV5080PerfBankDriver->getPatchName(Patch, int)
//----------------------------------------------------------------------------------------------------------------------

  public String getPatchName(Patch ip) {
    return getNumPatches() + " patches";
  }

//----------------------------------------------------------------------------------------------------------------------
// RolandXV5080PerfBankDriver->getPatchName(Patch, int)
//----------------------------------------------------------------------------------------------------------------------

  public String getPatchName(Patch p, int patchNum) {
    try {
      return new String(
        ((Patch)p).sysex, RolandXV5080PerfDriver.PATCH_SIZE * patchNum + RolandXV5080PerfDriver.PATCH_NAME_START,
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
      System.arraycopy(((Patch)bank).sysex, RolandXV5080PerfDriver.PATCH_SIZE * patchNum, sysex, 0, RolandXV5080PerfDriver.PATCH_SIZE);
      return new Patch(sysex, getDevice());
    } catch (Exception ex) {
      ErrorMsg.reportError("Error", "Error in XV5080 Perf Bank Driver", ex);
      return null;
    }
  }

//----------------------------------------------------------------------------------------------------------------------
// RolandXV5080PerfBankDriver->putPatch
//----------------------------------------------------------------------------------------------------------------------

  public void putPatch(Patch bank, Patch p, int patchNum) {
    Patch pInsert = new Patch(((Patch)p).sysex);
    RolandXV5080PerfDriver singleDriver = (RolandXV5080PerfDriver)pInsert.getDriver();
    singleDriver.updatePatchNum(pInsert, patchNum);
    singleDriver.calculateChecksum(pInsert);

    ((Patch)bank).sysex = Utility.byteArrayReplace(
      ((Patch)bank).sysex, RolandXV5080PerfDriver.PATCH_SIZE * patchNum, RolandXV5080PerfDriver.PATCH_SIZE,
      pInsert.sysex, 0, RolandXV5080PerfDriver.PATCH_SIZE);
  }

//----------------------------------------------------------------------------------------------------------------------
// RolandXV5080PerfBankDriver->requestPatchDump
//----------------------------------------------------------------------------------------------------------------------

  public void requestPatchDump(int bankNum, int patchNum) {
    byte[] sysex = SYSEX_REQUEST_DUMP.toByteArray(getChannel(), patchNum);
    RolandXV5080PatchDriver.calculateChecksum(sysex, 6, sysex.length - 3, sysex.length - 2);
    send(sysex);
  }
}
