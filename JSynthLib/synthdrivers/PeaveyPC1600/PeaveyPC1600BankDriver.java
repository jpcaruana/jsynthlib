//======================================================================================================================
// Summary: PeaveyPC1600BankDriver.java
// Author: phil@muqus.com - 07/2001
// @version $Id$
// Notes: Peavey PC1600 bank driver (only tested on PC1600x)
//
//   F0 00 00 1B 0B 00 01 32 nn <nibbleized data> F7
//     where nn is current unit preset (unimportant)- 0-50 (50=Edit Buffer)
//     <nibbleized data> = <name (16 2byte chars)> * (num of presets (51))
//                         <memory pointers (4 bytes each)> * (num of presets (51) + 1 point to first free byte after data)
//                         <preset data> * 51
//
//======================================================================================================================

package synthdrivers.PeaveyPC1600;
import core.BankDriver;
import core.ErrorMsg;
import core.IPatch;
import core.NibbleSysex;
import core.Patch;
import core.SysexHandler;
import core.Utility;

//======================================================================================================================
// Class: PeaveyPC1600SingleDriver
//======================================================================================================================

public class PeaveyPC1600BankDriver extends BankDriver {
  final static int NIBBLE_MULTIPLIER = PeaveyPC1600SingleDriver.NIBBLE_MULTIPLIER;

  final static int PATCH_NAME_SIZE = PeaveyPC1600SingleDriver.PATCH_NAME_SIZE;
  final static int PATCH_NAME_CHAR_BYTES = PeaveyPC1600SingleDriver.PATCH_NAME_CHAR_BYTES;

  final static int NUM_PATCHES = PeaveyPC1600SingleDriver.PATCH_NUMBERS.length;

  final static int FIRST_NAME_START = 9;                // (F0 00 00 1B 0B 00 01 32 nn).size

  // <memory pointer> == Preset Pointer
  final static int FIRST_PRESET_POINTER_START =
    FIRST_NAME_START + NUM_PATCHES*PATCH_NAME_SIZE*PeaveyPC1600SingleDriver.PATCH_SIZE_FACTOR;
  final static int PRESET_POINTER_BYTES = 4;
  final static int FIRST_PRESET_START = FIRST_PRESET_POINTER_START + PRESET_POINTER_BYTES*(NUM_PATCHES + 1);
  final static int PRESET_POINTER_FACTOR = 2;

  public int[] patchIndex = new int[NUM_PATCHES + 1];
  public IPatch indexedBank = null;

  final static byte[] BSYSEX_INIT_PRESET = (new SysexHandler(
    "f0 00 00 1b 0b 0d 04 02 0d 02 0d 04 09 06 0e 06 09 07 04 06 09 06 " +
    "01 06 0c 06 09 07 0a 06 05 06 04 02 0d 02 0d 02 00 00 00 04 08 00 " +
    "01 00 00 00 01 00 00 00 01 00 00 00 01 00 00 00 01 00 00 00 01 00 " +
    "00 00 01 00 00 00 01 00 00 00 01 00 00 00 01 00 00 00 01 00 00 00 " +
    "01 00 00 00 01 00 00 00 01 00 00 00 01 00 00 00 01 00 00 00 01 00 " +
    "00 00 01 00 00 00 01 00 00 00 01 00 00 00 01 00 00 00 01 00 00 00 " +
    "01 00 00 00 01 00 00 00 01 00 00 00 01 00 00 00 01 00 00 00 01 00 " +
    "00 00 01 00 00 00 01 00 00 00 01 00 00 00 01 00 00 00 01 00 00 00 " +
    "01 00 00 00 01 00 00 00 01 00 00 f7")).toByteArray();

  final static byte[] BSYSEX_HEADER = (new SysexHandler("F0 00 00 1B 0B 00 04")).toByteArray();

  final static SysexHandler SYSEX_REQUEST_DUMP = new SysexHandler("F0 00 00 1B 0B @@ 11 F7");

//----------------------------------------------------------------------------------------------------------------------
// Constructor: PeaveyPC1600BankDriver()
//----------------------------------------------------------------------------------------------------------------------

  public PeaveyPC1600BankDriver() {
    super ("Bank","Phil Shepherd",PeaveyPC1600SingleDriver.PATCH_NUMBERS.length,3);
    sysexID = "F000001b0b**0132";
//    inquiryID = "F07E**06024000000A***********F7";
    sysexRequestDump = SYSEX_REQUEST_DUMP;
//    patchSize =
    numSysexMsgs = 1;
//    patchNameStart =
//    patchNameSize =
    deviceIDoffset = PeaveyPC1600SingleDriver.DEVICE_ID_OFFSET;

//    singleSysexID = "F0411000101220**0000";
//    singleSize =

//  checksumStart =
//  checksumEnd =
//  checksumOffset =

    bankNumbers = PeaveyPC1600SingleDriver.BANK_NUMBERS;
    patchNumbers = PeaveyPC1600SingleDriver.PATCH_NUMBERS;
  }

//----------------------------------------------------------------------------------------------------------------------
// PeaveyPC1600BankDriver->setBankNum
//----------------------------------------------------------------------------------------------------------------------

  public void setBankNum(int bankNum) {
  }

//----------------------------------------------------------------------------------------------------------------------
// PeaveyPC1600BankDriver->getPatchName(Patch, int)
//----------------------------------------------------------------------------------------------------------------------

  public String getPatchName(IPatch bank) {
    return (((Patch)bank).sysex.length/1024) + " Kilobytes";
  }

//----------------------------------------------------------------------------------------------------------------------
// PeaveyPC1600BankDriver->generateIndex
//----------------------------------------------------------------------------------------------------------------------

  public void generateIndex(Patch bank) {
     if (indexedBank == bank)
       return;

     int currentPatchStart = 0;

//    Utility.interrogateSysex(p.sysex);
    NibbleSysex nibbleSysex = new NibbleSysex(bank.sysex, FIRST_PRESET_POINTER_START);
    for (int i = 0; i < getNumPatches() + 1; i++) {
      patchIndex[i] = nibbleSysex.getNibbleInt(PRESET_POINTER_BYTES, NIBBLE_MULTIPLIER)*PRESET_POINTER_FACTOR;
    }

  //   patchIndex = Utility.indexSysex(bank.sysex, numPatches);
   }

//----------------------------------------------------------------------------------------------------------------------
// PeaveyPC1600BankDriver->getPatchName(Patch, int)
//----------------------------------------------------------------------------------------------------------------------

  public String getPatchName(IPatch bank, int patchNum) {
    NibbleSysex nibbleSysex = new NibbleSysex(((Patch)bank).sysex,  FIRST_NAME_START + patchNum*PATCH_NAME_SIZE*PATCH_NAME_CHAR_BYTES);
    return nibbleSysex.getNibbleStr(PATCH_NAME_SIZE, PATCH_NAME_CHAR_BYTES, NIBBLE_MULTIPLIER);
  }

//----------------------------------------------------------------------------------------------------------------------
// PeaveyPC1600BankDriver->setPatchName(Patch, int, String)
//----------------------------------------------------------------------------------------------------------------------

  public void setPatchName(IPatch bank, int patchNum, String name) {
    NibbleSysex nibbleSysex = new NibbleSysex(
      ((Patch)bank).sysex,  FIRST_NAME_START + patchNum*PATCH_NAME_SIZE*PATCH_NAME_CHAR_BYTES
    );
    nibbleSysex.putNibbleStr(name, PATCH_NAME_SIZE, PATCH_NAME_CHAR_BYTES, NIBBLE_MULTIPLIER);
  }

//----------------------------------------------------------------------------------------------------------------------
// PeaveyPC1600BankDriver->getPatch
//----------------------------------------------------------------------------------------------------------------------

  public IPatch getPatch(IPatch bank, int patchNum) {
    try {
      generateIndex((Patch)bank);
      return createPatchFromData(
        ((Patch)bank).sysex, FIRST_PRESET_START + patchIndex[patchNum],
        patchIndex[patchNum + 1] - patchIndex[patchNum], getPatchName(bank, patchNum)
      );
    } catch (Exception ex) {
      ErrorMsg.reportStatus("PeaveyPC1600BankDriver->getPatch");
      ErrorMsg.reportStatus(ex);
      return null;
    }
  }

//----------------------------------------------------------------------------------------------------------------------
// PeaveyPC1600BankDriver->putPatch
//----------------------------------------------------------------------------------------------------------------------


  public void putPatch(IPatch bank, IPatch p, int patchNum) {
    generateIndex((Patch)bank);

    // Set the <name>
    setPatchName(bank, patchNum, p.getDriver().getPatchName(p));

    // Update the <data>
    int oldPatchSize = patchIndex[patchNum + 1] - patchIndex[patchNum];
    int newPatchSize = ((Patch)p).sysex.length - PeaveyPC1600SingleDriver.NON_DATA_SIZE;


    ((Patch)bank).sysex = Utility.byteArrayReplace(
      ((Patch)bank).sysex, FIRST_PRESET_START + patchIndex[patchNum], oldPatchSize,
      ((Patch)p).sysex, PeaveyPC1600SingleDriver.PATCH_DATA_START, newPatchSize);

    // Update the <memory pointers>
    int sizeDiff = newPatchSize - oldPatchSize;
    for (int i = patchNum + 1; i < getNumPatches(); i++) {
      patchIndex[i] += sizeDiff;
    }
    NibbleSysex nibbleSysex = new NibbleSysex(((Patch)bank).sysex, FIRST_PRESET_POINTER_START);
    for (int i = 0; i < getNumPatches(); i++) {
      nibbleSysex.putNibbleInt(patchIndex[i]/PRESET_POINTER_FACTOR, PRESET_POINTER_BYTES, NIBBLE_MULTIPLIER);
    }
  }

//----------------------------------------------------------------------------------------------------------------------
// PeaveyPC1600BankDriver->deletePatch(Patch, int)
//----------------------------------------------------------------------------------------------------------------------

  public void deletePatch (IPatch bank, int patchNum) {
//ErrorMsg.reportStatus("PeaveyPC1600BankDriver->deletePatch: " + patchNum);

    IPatch p = new Patch(BSYSEX_INIT_PRESET, getDevice());
//ErrorMsg.reportStatus("  PeaveyPC1600BankDriver->deletePatch: " + p.getDriver().getPatchName(p));
    putPatch(bank,  p, patchNum);
  }

//----------------------------------------------------------------------------------------------------------------------
// PeaveyPC1600BankDriver->createPatchFromData()
// Notes: F0 00 00 1B 0B ch 04 <name> <size> <data> F7
//----------------------------------------------------------------------------------------------------------------------

  public IPatch createPatchFromData(byte[] data, int dataOffset, int dataLength, String name) {
	  byte[] sysex = new byte[
      dataLength + BSYSEX_HEADER.length +
      PATCH_NAME_SIZE*PATCH_NAME_CHAR_BYTES +
      PeaveyPC1600SingleDriver.PATCH_SIZE_BYTES + 1];

    NibbleSysex nibbleSysex = new NibbleSysex(sysex);
    nibbleSysex.putSysex(BSYSEX_HEADER);
    nibbleSysex.putNibbleStr(name, PATCH_NAME_SIZE, PATCH_NAME_CHAR_BYTES, NIBBLE_MULTIPLIER);
    nibbleSysex.putNibbleInt(
      dataLength/PeaveyPC1600SingleDriver.PATCH_SIZE_FACTOR, PeaveyPC1600SingleDriver.PATCH_SIZE_BYTES, NIBBLE_MULTIPLIER
    );
    nibbleSysex.putSysex(data, dataOffset, dataLength);
    nibbleSysex.putSysex((byte)0xF7);

    IPatch p = new Patch(sysex);
    //p.ChooseDriver();
	  return p;
  }

}
