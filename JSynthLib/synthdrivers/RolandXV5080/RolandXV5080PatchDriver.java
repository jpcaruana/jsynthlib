//======================================================================================================================
// Summary: RolandXV5080PatchDriver.java
// Author: phil@muqus.com - 07/2001
// @version $Id$
// Notes: Patch driver for Roland XV-5080
//  1) A user patch dump consists of 9 sysex messages, total length 1056 bytes
//======================================================================================================================

package synthdrivers.RolandXV5080;
import core.Driver;
import core.Patch;
import core.SysexHandler;

//======================================================================================================================
// Class: RolandXV5080PatchDriver
//======================================================================================================================

public class RolandXV5080PatchDriver extends Driver {
  final static int[] PATCH_SYSEX_START = new int[] {  0,  91, 248, 312, 407, 460, 609, 758, 907};
  final static int[] PATCH_SYSEX_SIZE = new int[]  { 91, 157,  64,  95,  53, 149, 149, 149, 149};

  final static int PATCH_SIZE = 1056;

  final static int PATCH_NUMBER_OFFSET = 7;       // (F0 41 10 00 10 12 30 **).size - 1

  final static int PATCH_NAME_START = 10;
  final static int PATCH_NAME_SIZE = 12;

  final static int CHECKSUM_START = 6;           // (F0 41 10 00 10 12).size

  final static String[] BANK_NUMBERS = new String[] {"User"};
  final static String[] PATCH_NUMBERS = new String[] {
     "01-", "02-", "03-", "04-", "05-", "06-", "07-", "08-", "09-", "10-",
     "11-", "12-", "13-", "14-", "15-", "16-", "17-", "18-", "19-", "20-",
     "21-", "22-", "23-", "24-", "25-", "26-", "27-", "28-", "29-", "30-",
     "31-", "32-", "33-", "34-", "35-", "36-", "37-", "38-", "39-", "40-",
     "41-", "42-", "43-", "44-", "45-", "46-", "47-", "48-", "49-", "50-",
     "51-", "52-", "53-", "54-", "55-", "56-", "57-", "58-", "59-", "60-",
     "61-", "62-", "63-", "64-", "65-", "66-", "67-", "68-", "69-", "70-",
     "71-", "72-", "73-", "74-", "75-", "76-", "77-", "78-", "79-", "80-",
     "81-", "82-", "83-", "84-", "85-", "86-", "87-", "88-", "89-", "90-",
     "91-", "92-", "93-", "94-", "95-", "96-", "97-", "98-", "99-", "100-",
    "101-","102-","103-","104-","105-","106-","107-","108-","109-","110-",
    "111-","112-","113-","114-","115-","116-","117-","118-","119-","120-",
    "121-","122-","123-","124-","125-","126-","127-","128-"
  };

  final static SysexHandler SYSEX_REQUEST_DUMP = new SysexHandler("F0 41 10 00 10 11 30 *patchNum* 00 00 00 01 00 00 00 F7");

//----------------------------------------------------------------------------------------------------------------------
// Constructor: RolandXV5080PatchDriver()
//----------------------------------------------------------------------------------------------------------------------

  public RolandXV5080PatchDriver() {
    super ("Patch","Phil Shepherd");
    sysexID = "F0411000101230**0000";
    //    inquiryID = "F07E**06024000000A***********F7";
//    sysexRequestDump = SYSEX_REQUEST_DUMP;

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
// RolandXV5080PatchDriver->storePatch
//----------------------------------------------------------------------------------------------------------------------

  public void storePatch (Patch p, int bankNum, int patchNum) {
//ErrorMsg.reportStatus("RolandXV5080PatchDriver->storePatch: " + bankNum + " | " + patchNum);

    updatePatchNum(p, patchNum);

    sendPatchWorker(p);
    try {Thread.sleep(300); } catch (Exception e){}
    setBankNum(bankNum);
    setPatchNum(patchNum);
  }

//----------------------------------------------------------------------------------------------------------------------
// RolandXV5080PatchDriver->sendPatch
//----------------------------------------------------------------------------------------------------------------------

  public void sendPatch (Patch p) {
    storePatch(p, 0, 0);
  }

//----------------------------------------------------------------------------------------------------------------------
// RolandXV5080PatchDriver->setBankNum
// Notes:
//   Bn 00 57 Bn 20 00 Cn <patchNum>,  where n is the XV-5080 patch receive channel (Rx-Ch)
//----------------------------------------------------------------------------------------------------------------------

  public void setBankNum(int bankNum) {
    try {
      // BnH 00H mmH  n=MIDI channel number, mm=85
      send(0xB0+(getChannel()-1), 0x00, 87);
      // BnH 00H llH  n=MIDI channel number, ll=00H
      send(0xB0+(getChannel()-1), 0x20, 0);
    } catch (Exception e) {};
  }

//----------------------------------------------------------------------------------------------------------------------
// RolandXV5080PatchDriver->updatePatchNum
//----------------------------------------------------------------------------------------------------------------------

  public void updatePatchNum (Patch p, int patchNum) {
    for (int i = 0; i < PATCH_SYSEX_START.length; i++)
      p.sysex[PATCH_SYSEX_START[i] + PATCH_NUMBER_OFFSET] = (byte)(patchNum);
  }

//----------------------------------------------------------------------------------------------------------------------
// RolandXV5080PatchDriver->calculateChecksum(Patch)
//----------------------------------------------------------------------------------------------------------------------

  public void calculateChecksum(Patch p) {
    for (int i = 0; i < PATCH_SYSEX_START.length; i++) {
      int checksumStart = PATCH_SYSEX_START[i] + CHECKSUM_START;
      int checksumEnd = PATCH_SYSEX_START[i] + PATCH_SYSEX_SIZE[i] - 3;
      int checksumOffset = checksumEnd + 1;
      calculateChecksum(p.sysex, checksumStart, checksumEnd, checksumOffset);
    }
  }

//----------------------------------------------------------------------------------------------------------------------
// RolandXV5080PatchDriver->requestPatchDump
//----------------------------------------------------------------------------------------------------------------------

  public void requestPatchDump(int bankNum, int patchNum) {
    byte[] sysex = SYSEX_REQUEST_DUMP.toByteArray((byte)getChannel(), patchNum);
    calculateChecksum(sysex, 6, sysex.length - 3, sysex.length - 2);
    SysexHandler.send(getPort(), sysex);
  }

//----------------------------------------------------------------------------------------------------------------------
// static RolandXV5080PatchDriver->calculateChecksum(Patch, int, int, int)
//----------------------------------------------------------------------------------------------------------------------

  public static void calculateChecksum(byte[] sysex, int start, int end, int ofs) {
    int i;
    int sum = 0;
    for (i = start; i <= end; i++)
      sum += sysex[i];
    int remainder = sum % 128;
    sysex[ofs] = (byte)(128 - remainder);
  }
}
