// written by Kenneth L. Martinez
// @version $Id$

package synthdrivers.SCIProphet600;

import core.BankDriver;
import core.ErrorMsg;
import core.IPatch;
import core.NameValue;
import core.Patch;
import core.PatchEdit;
import core.SysexHandler;

public class P600ProgBankDriver extends BankDriver {
  static final int PATCH_NUM_OFFSET = 3;
  static final int NUM_IN_BANK = 100;

  public P600ProgBankDriver() {
    super ("Prog Bank","Kenneth L. Martinez",P600ProgSingleDriver.PATCH_LIST.length,5);
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
  }

  public void calculateChecksum(IPatch p) {
    // doesn't use checksum
  }

  public void calculateChecksum(IPatch p, int start, int end, int ofs) {
    // doesn't use checksum
  }

  public void storePatch (IPatch p, int bankNum, int patchNum) {
    sendPatchWorker((Patch)p, bankNum);
  }

  public void putPatch(IPatch bank, IPatch p, int patchNum) {
    if (!canHoldPatch(p)) {
      ErrorMsg.reportError("Error", "This type of patch does not fit in to this type of bank.");
      return;
    }

    System.arraycopy(((Patch)p).sysex, 0, ((Patch)bank).sysex, patchNum * singleSize, singleSize);
    ((Patch)bank).sysex[patchNum * singleSize + PATCH_NUM_OFFSET] = (byte)patchNum; // set program #
  }

  public IPatch getPatch(IPatch bank, int patchNum) {
    byte sysex[] = new byte[singleSize];
    System.arraycopy(((Patch)bank).sysex, patchNum * singleSize, sysex, 0, singleSize);
    IPatch p = new Patch(sysex);
    return p;
  }

  public String getPatchName(IPatch p, int patchNum) {
    return "-";
  }

  public void setPatchName(IPatch p,int patchNum, String name) {}

  protected void sendPatch (IPatch p) {
    sendPatchWorker((Patch)p, 0);
  }

  protected void sendPatchWorker (Patch p, int bankNum) {
    byte tmp[] = new byte[singleSize];  // send in 100 single-program messages
    try {
      PatchEdit.showWaitDialog();
      for (int i = 0; i < NUM_IN_BANK; i++) {
        System.arraycopy(p.sysex, i * singleSize, tmp, 0, singleSize);
        tmp[PATCH_NUM_OFFSET] = (byte)i; // program #
        send(tmp);
        Thread.sleep(50);
      }
      PatchEdit.hideWaitDialog();
    } catch (Exception e) {
      ErrorMsg.reportStatus (e);
      ErrorMsg.reportError("Error", "Unable to send Patch");
    }
  }

  public IPatch createNewPatch() {
    byte tmp[] = new byte[singleSize];
    byte sysex[] = new byte[patchSize];
    System.arraycopy(P600ProgSingleDriver.NEW_PATCH, 0, tmp, 0, singleSize);
    for (int i = 0; i < NUM_IN_BANK; i++) {
      tmp[PATCH_NUM_OFFSET] = (byte)i; // program #
      System.arraycopy(tmp, 0, sysex, i * singleSize, singleSize);
    }
    IPatch p = new Patch(sysex, this);
    return p;
  }

  public void requestPatchDump(int bankNum, int patchNum) {
    for (int i = 0; i < NUM_IN_BANK; i++) {
      send(sysexRequestDump.toSysexMessage(((byte)getChannel()), new NameValue[] { new NameValue("bankNum", bankNum), new NameValue("patchNum", i)}));
      try {
        Thread.sleep(50);
      } catch (Exception e) {
        ErrorMsg.reportStatus (e);
        ErrorMsg.reportError("Error", "Unable to request Patch " + i);
      }
    }
  }
}

