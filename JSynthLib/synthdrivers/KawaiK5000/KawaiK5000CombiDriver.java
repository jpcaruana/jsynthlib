//======================================================================================================================
// Summary: KawaiK5000CombiDriver.java
// Author: phil@muqus.com - 07/2001
// Notes: Combi (Multi) driver for K5000 (only tested on K5000s)
//======================================================================================================================

package synthdrivers.KawaiK5000;
import java.io.*;
import core.*;

//======================================================================================================================
// Class: KawaiK5000CombiDriver
//======================================================================================================================

public class KawaiK5000CombiDriver extends Driver {
  final static int PATCH_DATA_START = 8;
  // PATCH_DATA_SIZE = (check sum) + (effect DATA) + (common DATA) + (Section DATA)*4 = 1 + 38 + 15 + 13 * 4 = 106
  // ... however 103 works!!
  final static int PATCH_DATA_SIZE = 103;

  final static int PATCH_NAME_START = 47;
  final static int PATCH_NAME_SIZE = 8;

  final static String[] BANK_NUMBERS = new String[] {"Multi"};
  final static String[] PATCH_NUMBERS = new String[] {
    "01-","02-","03-","04-","05-","06-","07-","08-","09-","10-",
    "11-","12-","13-","14-","15-","16-","17-","18-","19-","20-",
    "21-","22-","23-","24-","25-","26-","27-","28-","29-","30-",
    "31-","32-","33-","34-","35-","36-","37-","38-","39-","40-",
    "41-","42-","43-","44-","45-","46-","47-","48-","49-","50-",
    "51-","52-","53-","54-","55-","56-","57-","58-","59-","60-",
    "61-","62-","63-","64-"
  };

  final static byte[] BSYSEX_HEADER = (new SysexHandler("F0 40 00 20 00 0A 20 00")).toByteArray();

  final static SysexHandler SYSEX_REQUEST_DUMP = new SysexHandler("F0 40 @@ 00 00 0A 20 00 *patchNum* F7");

//----------------------------------------------------------------------------------------------------------------------
// Constructor: KawaiK5000CombiDriver()
//----------------------------------------------------------------------------------------------------------------------

  public KawaiK5000CombiDriver() {
    manufacturer = "Kawai";
    model = "K5000";
    patchType = "Combi";
    id = "K5k";
    sysexID = "F040**20000A20";
//    inquiryID = "F07E**06024000000A***********F7";
    sysexRequestDump = SYSEX_REQUEST_DUMP;

    patchSize = 0;
    numSysexMsgs = 1;
    patchNameStart = PATCH_NAME_START;
    patchNameSize = PATCH_NAME_SIZE;
    deviceIDoffset = 2;

    checksumStart = PATCH_DATA_START + 1;
    checksumEnd = PATCH_DATA_START + PATCH_DATA_SIZE - 1;
    checksumOffset = PATCH_DATA_START;

    bankNumbers = BANK_NUMBERS;
    patchNumbers = PATCH_NUMBERS;
  }

//----------------------------------------------------------------------------------------------------------------------
// KawaiK5000CombiDriver->storePatch
//----------------------------------------------------------------------------------------------------------------------

  public void storePatch (Patch p, int bankNum, int patchNum) {
ErrorMsg.reportStatus("KawaiK5000CombiDriver->storePatch: " + bankNum + " | " + patchNum);
    setBankNum(bankNum);
    setPatchNum(patchNum);
    try {Thread.sleep(300); } catch (Exception e){}
    p.sysex[3]=(byte)0x20;
    p.sysex[7]=(byte)(patchNum);

    sendPatchWorker(p);
    try {Thread.sleep(300); } catch (Exception e){}
    setPatchNum(patchNum);
  }

//----------------------------------------------------------------------------------------------------------------------
// KawaiK5000CombiDriver->sendPatch
//----------------------------------------------------------------------------------------------------------------------

  public void sendPatch (Patch p) {
    storePatch(p, 0, 0);
  }

//----------------------------------------------------------------------------------------------------------------------
// KawaiK5000CombiDriver->setBankNum
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
// KawaiK5000CombiDriver->calculateChecksum(Patch, int, int, int)
//----------------------------------------------------------------------------------------------------------------------

  public void calculateChecksum(Patch p, int start, int end, int ofs) {
//ErrorMsg.reportStatus("KawaiK5000CombiDriver->calculateChecksum");
    int i;
    int sum = 0;
    for (i = start; i <= end; i++)
      sum += p.sysex[i];
    sum += (byte)0xA5;
    p.sysex[ofs] = (byte)(sum % 128);
  }

//----------------------------------------------------------------------------------------------------------------------
// KawaiK5000CombiDriver->createNewPatch
//----------------------------------------------------------------------------------------------------------------------

  public Patch createNewPatch() {
//ErrorMsg.reportStatus("KawaiK5000CombiDriver->createNewPatch");
    Patch p = createPatchFromData(new byte[PATCH_DATA_SIZE], 0, PATCH_DATA_SIZE);
    Driver singleDriver = p.getDriver();
    singleDriver.setPatchName(p, "New Patch");
    singleDriver.calculateChecksum(p);
    return p;
  }


//----------------------------------------------------------------------------------------------------------------------
// static KawaiK5000CombIDriver->createPatchFromData
//----------------------------------------------------------------------------------------------------------------------

  static public Patch createPatchFromData(byte[] data, int dataOffset, int dataLength) {
	  byte[] sysex = new byte[dataLength + BSYSEX_HEADER.length + 1];
    System.arraycopy(BSYSEX_HEADER, 0, sysex, 0, BSYSEX_HEADER.length);
    System.arraycopy(data, dataOffset, sysex, BSYSEX_HEADER.length, dataLength);
    sysex[sysex.length-1] = (byte)0xF7;

    Patch p = new Patch(sysex);
    p.ChooseDriver();
    p.getDriver().calculateChecksum(p);

    return p;
  }
}

