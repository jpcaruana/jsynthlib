// written by Kenneth L. Martinez

package synthdrivers.SCIProphet600;

import core.*;
import javax.swing.*;

public class P600ProgBankDriver extends BankDriver {
  static final int PATCH_NUM_OFFSET = 3;
  static final int NUM_IN_BANK = 100;

  public P600ProgBankDriver() {
    authors = "Kenneth L. Martinez";
    manufacturer = "Sequential";
    model = "P600";
    patchType = "Prog Bank";
    id = "Prophet-600";
    sysexID = "F00102**";
    sysexRequestDump = new SysexHandler("F0 01 00 *patchNum* F7");
    singleSysexID = "F0010263";
    singleSize = 37;
    patchSize = singleSize * NUM_IN_BANK;
    patchNameStart = -1;
    patchNameSize = 0;
    deviceIDoffset = -1;
    bankNumbers  = P600ProgSingleDriver.BANK_LIST;
    patchNumbers = P600ProgSingleDriver.PATCH_LIST;
    numPatches = patchNumbers.length;
    numColumns = 5;
  }

  public void calculateChecksum(Patch p) {
    // doesn't use checksum
  }

  public void calculateChecksum(Patch p, int start, int end, int ofs) {
    // doesn't use checksum
  }

  public void storePatch (Patch p, int bankNum, int patchNum) {
    sendPatchWorker(p, bankNum);
  }

  public void putPatch(Patch bank, Patch p, int patchNum) {
    if (!canHoldPatch(p)) {
      ErrorMsg.reportError("Error", "This type of patch does not fit in to this type of bank.");
      return;
    }

    System.arraycopy(p.sysex, 0, bank.sysex, patchNum * singleSize, singleSize);
    bank.sysex[patchNum * singleSize + PATCH_NUM_OFFSET] = (byte)patchNum; // set program #
  }

  public Patch getPatch(Patch bank, int patchNum) {
    byte sysex[] = new byte[singleSize];
    System.arraycopy(bank.sysex, patchNum * singleSize, sysex, 0, singleSize);
    Patch p = new Patch(sysex);
    return p;
  }

  public String getPatchName(Patch p, int patchNum) {
    return "-";
  }

  public void setPatchName(Patch p,int patchNum, String name) {}

  protected void sendPatchWorker (Patch p) {
    sendPatchWorker(p, 0);
  }

  protected void sendPatchWorker (Patch p, int bankNum) {
    byte tmp[] = new byte[singleSize];  // send in 100 single-program messages
    try {
      PatchEdit.waitDialog.show();
      for (int i = 0; i < NUM_IN_BANK; i++) {
        System.arraycopy(p.sysex, i * singleSize, tmp, 0, singleSize);
        tmp[PATCH_NUM_OFFSET] = (byte)i; // program #
        PatchEdit.MidiOut.writeLongMessage(port, tmp);
        Thread.sleep(50);
      }
      PatchEdit.waitDialog.hide();
    } catch (Exception e) {
      ErrorMsg.reportStatus (e);
      ErrorMsg.reportError("Error", "Unable to send Patch");
    }
  }

  public Patch createNewPatch() {
    byte tmp[] = new byte[singleSize];
    byte sysex[] = new byte[patchSize];
    System.arraycopy(P600ProgSingleDriver.NEW_PATCH, 0, tmp, 0, singleSize);
    for (int i = 0; i < NUM_IN_BANK; i++) {
      tmp[PATCH_NUM_OFFSET] = (byte)i; // program #
      System.arraycopy(tmp, 0, sysex, i * singleSize, singleSize);
    }
    Patch p = new Patch(sysex);
    p.ChooseDriver();
    return p;
  }

  public void requestPatchDump(int bankNum, int patchNum) {
    for (int i = 0; i < NUM_IN_BANK; i++) {
      sysexRequestDump.send(port, (byte)channel, new NameValue("bankNum", bankNum),
        new NameValue("patchNum", i)
      );
      try {
        Thread.sleep(50);
      } catch (Exception e) {
        ErrorMsg.reportStatus (e);
        ErrorMsg.reportError("Error", "Unable to request Patch " + i);
      }
    }
  }
}

