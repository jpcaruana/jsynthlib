//======================================================================================================================
// Summary: KawaiK5000CombiBankDriver.java
// Author: phil@muqus.com - 07/2001
// Notes: Combi (Multi) Bank driver for K5000 (only tested on K5000s)
//======================================================================================================================

package synthdrivers.KawaiK5000;
import core.*;
import java.io.*;
import javax.swing.*;

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
    manufacturer = "Kawai";
    model = "K5000";
    patchType = "CombiBank";
    id = "K5k";
    sysexID = "F040**21000A20";
//    inquiryID = "F07E**06024000000A***********F7";
   authors="Phil Shepherd";
   sysexRequestDump = SYSEX_REQUEST_DUMP;

    patchSize = 0;
    numSysexMsgs = 1;
//    patchNameStart =
//    patchNameSize =
    deviceIDoffset = 2;

    numColumns = 4;
    singleSysexID = "F040**20000A20";
    singleSize = 0;

    bankNumbers = KawaiK5000CombiDriver.BANK_NUMBERS;
    patchNumbers = KawaiK5000CombiDriver.PATCH_NUMBERS;
    numPatches = patchNumbers.length;
  }

//----------------------------------------------------------------------------------------------------------------------
// KawaiK5000CombiBankDriver->setBankNum
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
// KawaiK5000CombiBankDriver->patchIndex
//----------------------------------------------------------------------------------------------------------------------

  public int patchIndex(int patchNum) {
    return FIRST_PATCH_START + patchNum * KawaiK5000CombiDriver.PATCH_DATA_SIZE;
  }

//----------------------------------------------------------------------------------------------------------------------
// KawaiK5000CombiBankDriver->getPatchName(Patch)
//----------------------------------------------------------------------------------------------------------------------

  public String getPatchName(Patch p) {
    return (p.sysex.length/1024) + " Kilobytes";
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
        p.sysex, getPatchNameStart(patchNum),
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
      return KawaiK5000CombiDriver.createPatchFromData(bank.sysex, patchIndex(patchNum), KawaiK5000CombiDriver.PATCH_DATA_SIZE);
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
    bank.sysex = Utility.byteArrayReplace(
      bank.sysex, patchIndex(patchNum), KawaiK5000CombiDriver.PATCH_DATA_SIZE,
      p.sysex, KawaiK5000CombiDriver.PATCH_DATA_START, KawaiK5000CombiDriver.PATCH_DATA_SIZE);
  }

}