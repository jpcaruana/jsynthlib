// written by Kenneth L. Martinez
// $Id$
package synthdrivers.NordLead;

import core.*;
import javax.swing.*;

public class NLDrumBankDriver extends BankDriver {
  static final int BANK_NUM_OFFSET = 4;
  static final int PATCH_NUM_OFFSET = 5;
  static final int NUM_IN_BANK = 10;
  
  public NLDrumBankDriver() {
    super ("Drum Bank","Kenneth L. Martinez",NLDrumSingleDriver.PATCH_LIST.length,2);
    sysexID = "F033**04**";
    sysexRequestDump = new SysexHandler("F0 33 @@ 04 *bankNum* *patchNum* F7");
    singleSysexID = "F033**04**";
    singleSize = 1063;
    patchSize = singleSize * NUM_IN_BANK;
    patchNameStart = -1;
    patchNameSize = 0;
    deviceIDoffset = 2;
    bankNumbers  = NLDrumSingleDriver.BANK_LIST;
    patchNumbers = NLDrumSingleDriver.PATCH_LIST;
  }

  public void calculateChecksum(Patch p) {
    // doesn't use checksum
  }

  public void calculateChecksum(Patch p, int start, int end, int ofs) {
    // doesn't use checksum
  }

  public void storePatch (Patch p, int bankNum, int patchNum) {
    if (bankNum == 0) {
      JOptionPane.showMessageDialog(PatchEdit.getInstance(),
        "Cannot send to ROM bank",
        "Store Patch",
        JOptionPane.WARNING_MESSAGE
      );
    } else {
      setBankNum(bankNum); // must set bank - sysex patch dump always stored in current bank
      setPatchNum(patchNum); // must send program change to make bank change take effect
      sendPatchWorker(p, bankNum);
    }
  }

  public void putPatch(Patch bank, Patch p, int patchNum) {
    if (!canHoldPatch(p)) {
      ErrorMsg.reportError("Error", "This type of patch does not fit in to this type of bank.");
      return;
    }

    System.arraycopy(p.sysex, 0, bank.sysex, patchNum * singleSize, singleSize);
    bank.sysex[patchNum * singleSize + PATCH_NUM_OFFSET] =
       (byte)(patchNum + 99); // set program #
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

  protected void sendPatch (Patch p) {
    sendPatchWorker(p, 0);
  }

  protected void sendPatchWorker (Patch p, int bankNum) {
    byte tmp[] = new byte[singleSize];  // send in 10 single-program messages
    try {
      PatchEdit.waitDialog.show();
      for (int i = 0; i < NUM_IN_BANK; i++) {
        System.arraycopy(p.sysex, i * singleSize, tmp, 0, singleSize);
        tmp[deviceIDoffset] = (byte) (((NordLeadDevice) getDevice()).getGlobalChannel() - 1);
        tmp[BANK_NUM_OFFSET] = (byte)(bankNum + 1);
        tmp[PATCH_NUM_OFFSET] = (byte)(i + 99); // program #
        send(tmp);
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
    System.arraycopy(NLDrumSingleDriver.NEW_PATCH, 0, tmp, 0, singleSize);
    for (int i = 0; i < NUM_IN_BANK; i++) {
      tmp[PATCH_NUM_OFFSET] = (byte)i; // program #
      System.arraycopy(tmp, 0, sysex, i * singleSize, singleSize);
    }
    Patch p = new Patch(sysex, this);
    return p;
  }

  public void requestPatchDump(int bankNum, int patchNum) {
    for (int i = 0; i < NUM_IN_BANK; i++) {
      setBankNum(bankNum); // kludge: drum dump request sends 1063 bytes of garbage -
      setPatchNum(i + 99); // select drum sound, then get data from edit buffer
      sysexRequestDump.send(getPort(), (byte) (((NordLeadDevice) getDevice()).getGlobalChannel()),
        new NameValue("bankNum", 10),
        new NameValue("patchNum", 0)
      );
      try {
        Thread.sleep(400); // it takes some time for each drum patch to be sent
      } catch (Exception e) {
        ErrorMsg.reportStatus (e);
        ErrorMsg.reportError("Error", "Unable to request Patch " + i);
      }
    }
  }
  
}

