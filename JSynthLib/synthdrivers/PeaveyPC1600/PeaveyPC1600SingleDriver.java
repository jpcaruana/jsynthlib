//======================================================================================================================
// Summary: PeaveyPC1600SingleDriver.java
// Author: phil@muqus.com - 07/2001
// Notes: Peavey PC1600 single driver (only tested on PC1600x)
//    When you dump "curr prst" from the PC 1600x, you get the following string:
//
//    F0 00 00 1B 0B ch 04 <name> <size> <data> F7
//
//    where   ch = MIDI channel - 1 (0-F)
//        name = 16-character name (nibbleized hi,lo -- 32 bytes)
//        size = 16-bit size of patch data (nibbleized hi,lo -- 4 bytes)
//            (not including name or size bytes)
//        data = patch data (nibblized hi,lo -- [size*2] bytes)
//======================================================================================================================

package synthdrivers.PeaveyPC1600;
import java.io.*;
import core.*;

//======================================================================================================================
// Class: PeaveyPC1600SingleDriver
//======================================================================================================================

public class PeaveyPC1600SingleDriver extends Driver {
  final static int NIBBLE_MULTIPLIER = 16;

  final static int PATCH_SIZE_START = 39;           // (F0 00 00 1B 0B ch 04).size + <name>.size = 7 + 32 = 39;
  final static int PATCH_SIZE_BYTES = 4;
  final static int PATCH_SIZE_FACTOR = 2;           // Sizes required multipling by factor of 2

  final static int PATCH_DATA_START = PATCH_SIZE_START + PATCH_SIZE_BYTES;
  final static int NON_DATA_SIZE = PATCH_DATA_START + 1;    // Number of bytes of sysex which are not <data>

  final static int PATCH_NAME_START = 7;            // (F0 00 00 1B 0B ch 04).size
  final static int PATCH_NAME_SIZE = 16;            // (Beware data is nibbleized hi, lo -- 32 bytes)
  final static int PATCH_NAME_CHAR_BYTES = 2;

  final static int DEVICE_ID_OFFSET = 5;            // (F0 00 00 1B 0B).size

  final static String[] BANK_NUMBERS = new String[] {"User"};
  final static String[] PATCH_NUMBERS = new String[] {
     "00-", "01-", "02-", "03-", "04-", "05-", "06-", "07-", "08-", "09-",
     "10-", "11-", "12-", "13-", "14-", "15-", "16-", "17-", "18-", "19-",
     "20-", "21-", "22-", "23-", "24-", "25-", "26-", "27-", "28-", "29-",
     "30-", "31-", "32-", "33-", "34-", "35-", "36-", "37-", "38-", "39-",
     "40-", "41-", "42-", "43-", "44-", "45-", "46-", "47-", "48-", "49-",
     "Ed-"
  };

  final static SysexHandler SYSEX_WRITE_EDIT_BUFFER = new SysexHandler("F0 00 00 1B 0B @@ 20 *patchNum* F7");
  final static SysexHandler SYSEX_RECALL_PRESET = new SysexHandler("F0 00 00 1B 0B @@ 00 *patchNum* F7");
  final static SysexHandler SYSEX_REQUEST_EDIT_BUFFER = new SysexHandler("F0 00 00 1B 0B @@ 14 F7");

//----------------------------------------------------------------------------------------------------------------------
// Constructor: PeaveyPC1600SingleDriver()
//----------------------------------------------------------------------------------------------------------------------

  public PeaveyPC1600SingleDriver() {
    manufacturer = "Peavey";
    model = "PC1600";
    patchType = "Single";
    id = "PvPC16";
    sysexID = "F000001B0B**04";
//    inquiryID = "F07E**06024000000A***********F7";
    authors="Phil Shepherd";
//    patchSize = PATCH_SIZE;
    numSysexMsgs = 1;
    patchNameStart = PATCH_NAME_START;
    patchNameSize = PATCH_NAME_SIZE;
    deviceIDoffset = DEVICE_ID_OFFSET;

//  checksumStart =
//  checksumEnd =
//  checksumOffset =

    bankNumbers = BANK_NUMBERS;
    patchNumbers = PATCH_NUMBERS;
  }

//----------------------------------------------------------------------------------------------------------------------
// PeaveyPC1600SingleDriver->getPatchName
//----------------------------------------------------------------------------------------------------------------------

   public String getPatchName(Patch p) {
     NibbleSysex nibbleSysex = new NibbleSysex(p.sysex, PATCH_NAME_START);
     return nibbleSysex.getNibbleStr(PATCH_NAME_SIZE, PATCH_NAME_CHAR_BYTES, NIBBLE_MULTIPLIER);
   }

//----------------------------------------------------------------------------------------------------------------------
// PeaveyPC1600SingleDriver->setPatchName
//----------------------------------------------------------------------------------------------------------------------

  public void setPatchName(Patch p, String name) {
     NibbleSysex nibbleSysex = new NibbleSysex(p.sysex, PATCH_NAME_START);
     nibbleSysex.putNibbleStr(name, PATCH_NAME_SIZE, PATCH_NAME_CHAR_BYTES, NIBBLE_MULTIPLIER);
  }

//----------------------------------------------------------------------------------------------------------------------
// PeaveyPC1600SingleDriver->sendPatch
//----------------------------------------------------------------------------------------------------------------------

  public void sendPatch(Patch p) {
    storePatch(p, 0, 0);
  }

//----------------------------------------------------------------------------------------------------------------------
// PeaveyPC1600SingleDriver->storePatch
//----------------------------------------------------------------------------------------------------------------------

  public void storePatch (Patch p, int bankNum, int patchNum) {
    sendPatchWorker(p);

    // Request PC1600 stores edit buffer with this patchNum
    SYSEX_WRITE_EDIT_BUFFER.send(port, (byte)channel, patchNum);
  }

//----------------------------------------------------------------------------------------------------------------------
// PeaveyPC1600SingleDriver->setBankNum
//----------------------------------------------------------------------------------------------------------------------

  public void setBankNum(int bankNum) {
  }

//----------------------------------------------------------------------------------------------------------------------
// PeaveyPC1600SingleDriver->requestPatchDump
//----------------------------------------------------------------------------------------------------------------------

  public void requestPatchDump(int bankNum, int patchNum) {
    SYSEX_RECALL_PRESET.send(port, (byte)channel, patchNum);
    SYSEX_REQUEST_EDIT_BUFFER.send(port, (byte)channel);
  }
}
