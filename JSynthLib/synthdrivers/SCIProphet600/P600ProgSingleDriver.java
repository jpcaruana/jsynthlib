// written by Kenneth L. Martinez
// @version $Id$

package synthdrivers.SCIProphet600;

import core.Driver;
import core.ErrorMsg;
import core.JSLFrame;
import core.Patch;
import core.SysexHandler;

public class P600ProgSingleDriver extends Driver {
  static final String BANK_LIST[] = new String[] { "User" };
  static final String PATCH_LIST[] = new String[] {
    "00", "01", "02", "03", "04", "05", "06", "07", "08", "09",
    "10", "11", "12", "13", "14", "15", "16", "17", "18", "19",
    "20", "21", "22", "23", "24", "25", "26", "27", "28", "29",
    "30", "31", "32", "33", "34", "35", "36", "37", "38", "39",
    "40", "41", "42", "43", "44", "45", "46", "47", "48", "49",
    "50", "51", "52", "53", "54", "55", "56", "57", "58", "59",
    "60", "61", "62", "63", "64", "65", "66", "67", "68", "69",
    "70", "71", "72", "73", "74", "75", "76", "77", "78", "79",
    "80", "81", "82", "83", "84", "85", "86", "87", "88", "89",
    "90", "91", "92", "93", "94", "95", "96", "97", "98", "99"
  };
  static final int PATCH_NUM_OFFSET = 3;
  static final byte NEW_PATCH[] = {
    (byte)0xF0, (byte)0x01, (byte)0x02, (byte)0x63, (byte)0x03, (byte)0x0B, (byte)0x0F, (byte)0x01,
    (byte)0x00, (byte)0x04, (byte)0x03, (byte)0x06, (byte)0x08, (byte)0x01, (byte)0x08, (byte)0x04,
    (byte)0x08, (byte)0x0F, (byte)0x07, (byte)0x02, (byte)0x0E, (byte)0x07, (byte)0x0F, (byte)0x0D,
    (byte)0x00, (byte)0x04, (byte)0x0E, (byte)0x0F, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x09,
    (byte)0x02, (byte)0x05, (byte)0x03, (byte)0x00, (byte)0x0F7
  };

  public P600ProgSingleDriver() {
    super ("Prog Single","Kenneth L. Martinez");
    sysexID = "F00102**";
    sysexRequestDump = new SysexHandler("F0 01 00 *patchNum* F7");

    patchSize = 37;
    patchNameStart = -1;
    patchNameSize = 0;
    deviceIDoffset = -1;
    bankNumbers = BANK_LIST;
    patchNumbers = PATCH_LIST;
  }

  public void calculateChecksum(Patch p) {
    // doesn't use checksum
  }

  public void calculateChecksum(Patch p, int start, int end, int ofs) {
    // doesn't use checksum
  }

  public String getPatchName(Patch ip) {
    return "prog" + ((Patch)ip).sysex[PATCH_NUM_OFFSET];
  }

  public void setPatchName(Patch p, String name) {}

  public void sendPatch(Patch p) {
    sendPatch((Patch)p, 0, 99); // using user program # 99 as edit buffer
  }

  public void sendPatch(Patch p, int bankNum, int patchNum) {
    Patch p2 = new Patch(p.sysex);
    p2.sysex[PATCH_NUM_OFFSET] = 99; // program # 99
    sendPatchWorker(p2);
  }

  // Sends a patch to a set location in the user bank
  public void storePatch(Patch p, int bankNum, int patchNum) {
    sendPatch((Patch)p, bankNum, patchNum);
  }

  // program 99 is being used for edit buffer
  public void playPatch(Patch p) {
    byte sysex[] = new byte[patchSize];
    System.arraycopy(((Patch)p).sysex, 0, sysex, 0, patchSize);
    sysex[PATCH_NUM_OFFSET] = 99; // program # 99
    Patch p2 = new Patch(sysex);
    try {
      Thread.sleep (50);  // kludge: patch sent twice for Ctl-P from editor, so add
                          //  delay between them (otherwise P600 may not process properly)
      sendPatch(p2);  // kludge to send midi pgm change without modifying super.playPatch
      Thread.sleep (50);
      send(0xC0 + getChannel() - 1, 99);
      Thread.sleep (50);
      super.playPatch(p2);
    } catch (Exception e) {
      ErrorMsg.reportStatus (e);
    }
  }

  public Patch createNewPatch() {
     return new Patch(NEW_PATCH, this);
  }

  public JSLFrame editPatch(Patch p) {
     return new P600ProgSingleEditor((Patch)p);
  }
}

