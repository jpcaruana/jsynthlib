//======================================================================================================================
// Summary: KawaiK5000CombiBankDriver.java
// Author: phil@muqus.com - 07/2001
// @version $Id$
// Notes: Combi (Multi) Bank driver for K5000 (only tested on K5000s)
//======================================================================================================================

package synthdrivers.KawaiK5000;
import java.io.UnsupportedEncodingException;

import core.BankDriver;
import core.Driver;
import core.ErrorMsg;
import core.Patch;
import core.SysexHandler;
import core.Utility;

//======================================================================================================================
// Class: KawaiK5000CombiDriver
//======================================================================================================================

public class KawaiK5000CombiBankDriver extends BankDriver {
  final static int FIRST_PATCH_START = 7;

  final static SysexHandler SYSEX_REQUEST_DUMP = new SysexHandler("F0 40 @@ 01 00 0A 20 00 *patchNum* F7");

//----------------------------------------------------------------------------------------------------------------------
// Constructor: KawaiK5000CombiBankDriver()
//----------------------------------------------------------------------------------------------------------------------

  public KawaiK5000CombiBankDriver() {
    super ("CombiBank","Phil Shepherd",KawaiK5000CombiDriver.PATCH_NUMBERS.length,4);
    sysexID = "F040**21000A20";
   sysexRequestDump = SYSEX_REQUEST_DUMP;

    patchSize = 0;
    numSysexMsgs = 1;
//    patchNameStart =
//    patchNameSize =
    deviceIDoffset = 2;

    singleSysexID = "F040**20000A20";
    singleSize = 0;

    bankNumbers = KawaiK5000CombiDriver.BANK_NUMBERS;
    patchNumbers = KawaiK5000CombiDriver.PATCH_NUMBERS;
  }

//----------------------------------------------------------------------------------------------------------------------
// KawaiK5000CombiBankDriver->setBankNum
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
// KawaiK5000CombiBankDriver->patchIndex
//----------------------------------------------------------------------------------------------------------------------

  public int patchIndex(int patchNum) {
    return FIRST_PATCH_START + patchNum * KawaiK5000CombiDriver.PATCH_DATA_SIZE;
  }

//----------------------------------------------------------------------------------------------------------------------
// KawaiK5000CombiBankDriver->getPatchName(Patch)
//----------------------------------------------------------------------------------------------------------------------

  public String getPatchName(Patch ip) {
    return (((Patch)ip).sysex.length/1024) + " Kilobytes";
  }

//----------------------------------------------------------------------------------------------------------------------
// KawaiK5000CombiBankDriver->getPatchNameStart
//----------------------------------------------------------------------------------------------------------------------

  public int getPatchNameStart(int patchNum) {
    return patchIndex(patchNum) + KawaiK5000CombiDriver.PATCH_NAME_START - KawaiK5000CombiDriver.PATCH_DATA_START;
  }

//----------------------------------------------------------------------------------------------------------------------
// KawaiK5000CombiBankDriver->getPatchName(Patch, int)
//----------------------------------------------------------------------------------------------------------------------

  public String getPatchName(Patch p, int patchNum) {
//ErrorMsg.reportStatus("KawaiK5000CombiBankDriver->getPatchName: " + patchNum);
    try {
      return new String(
        ((Patch)p).sysex, getPatchNameStart(patchNum),
        8, "US-ASCII");
    } catch (UnsupportedEncodingException ex) {
      return "-??????-";
    }
  }

//----------------------------------------------------------------------------------------------------------------------
// KawaiK5000CombiBankDriver->setPatchName(Patch, int, String)
//----------------------------------------------------------------------------------------------------------------------

  public void setPatchName(Patch bank, int patchNum, String name) {
    Patch p = getPatch(bank, patchNum);
    Driver singleDriver = p.getDriver();
    singleDriver.setPatchName(p, name);
    singleDriver.calculateChecksum(p);
    putPatch(bank, p, patchNum);
  }

//----------------------------------------------------------------------------------------------------------------------
// KawaiK5000CombiBankDriver->getPatch
//----------------------------------------------------------------------------------------------------------------------

  public Patch getPatch(Patch bank, int patchNum) {
//ErrorMsg.reportStatus("KawaiK5000CombiBankDriver->getPatch: " + patchNum);
    try{
      return KawaiK5000CombiDriver.createPatchFromData(((Patch)bank).sysex, patchIndex(patchNum), KawaiK5000CombiDriver.PATCH_DATA_SIZE);
    } catch (Exception ex) {
      ErrorMsg.reportError("Error", "Error in K5000 Combi Bank Driver", ex);
      return null;
    }
  }

//----------------------------------------------------------------------------------------------------------------------
// KawaiK5000CombiBankDriver->putPatch
//----------------------------------------------------------------------------------------------------------------------

  public void putPatch(Patch bank, Patch p, int patchNum) {
//ErrorMsg.reportStatus("KawaiK5000CombiBankDriver->putPatch: " + patchNum);
    ((Patch)bank).sysex = Utility.byteArrayReplace(
      ((Patch)bank).sysex, patchIndex(patchNum), KawaiK5000CombiDriver.PATCH_DATA_SIZE,
      ((Patch)p).sysex, KawaiK5000CombiDriver.PATCH_DATA_START, KawaiK5000CombiDriver.PATCH_DATA_SIZE);
  }

}
