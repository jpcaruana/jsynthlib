// written by Kenneth L. Martinez
// $Id$
package synthdrivers.NordLead;

import core.*;

import javax.swing.*;

public class NL2PerfBankDriver extends BankDriver {
  static final int BANK_NUM_OFFSET = 4;
  static final int PATCH_NUM_OFFSET = 5;
  static final int NUM_IN_BANK = 100;

  public NL2PerfBankDriver() {
    super ("Perf Bank","Kenneth L. Martinez",NLPerfSingleDriver.PATCH_LIST.length,5);
    sysexID = "F033**04**";
    sysexRequestDump = new SysexHandler("F0 33 @@ 04 *bankNum* *patchNum* F7");
    singleSysexID = "F033**04**";
    singleSize = 715;
    patchSize = singleSize * NUM_IN_BANK;
    patchNameStart = -1;
    patchNameSize = 0;
    deviceIDoffset = 2;
    bankNumbers  = NLPerfSingleDriver.BANK_LIST;
    patchNumbers = NLPerfSingleDriver.PATCH_LIST;
  }

  public void calculateChecksum(Patch p) {
    // doesn't use checksum
  }

//  protected static void calculateChecksum(Patch p, int start, int end, int ofs) {
//    // doesn't use checksum
//  }

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
      sendPatchWorker((Patch)p, bankNum);
    }
  }

  public void putPatch(Patch bank, Patch p, int patchNum) {
    if (!canHoldPatch(p)) {
      ErrorMsg.reportError("Error", "This type of patch does not fit in to this type of bank.");
      return;
    }

    System.arraycopy(((Patch)p).sysex, 0, ((Patch)bank).sysex, patchNum * singleSize, singleSize);
    ((Patch)bank).sysex[patchNum * singleSize + PATCH_NUM_OFFSET] = (byte)patchNum; // set program #
  }

  public Patch getPatch(Patch bank, int patchNum) {
    byte sysex[] = new byte[singleSize];
    System.arraycopy(((Patch)bank).sysex, patchNum * singleSize, sysex, 0, singleSize);
    return new Patch(sysex);
  }

  public String getPatchName(Patch p, int patchNum) {
    return "-";
  }

  public void setPatchName(Patch p,int patchNum, String name) {}

  protected void sendPatch (Patch p) {
    sendPatchWorker((Patch)p, 0);
  }

  protected void sendPatchWorker (Patch p, int bankNum) {
    byte tmp[] = new byte[singleSize];  // send in 100 single-performance messages
    try {
      PatchEdit.showWaitDialog();
      for (int i = 0; i < NUM_IN_BANK; i++) {
        System.arraycopy(p.sysex, i * singleSize, tmp, 0, singleSize);
        tmp[deviceIDoffset] = (byte) (((NordLeadDevice) (getDevice())).getGlobalChannel() - 1);
        tmp[BANK_NUM_OFFSET] = (byte)31;
        tmp[PATCH_NUM_OFFSET] = (byte)i; // performance #
        send(tmp);
        Thread.sleep(50);
      }
      PatchEdit.hideWaitDialog();
    } catch (Exception e) {
      ErrorMsg.reportStatus (e);
      ErrorMsg.reportError("Error", "Unable to send Patch");
    }
  }

  public Patch createNewPatch() {
    byte tmp[] = new byte[singleSize];
    byte sysex[] = new byte[patchSize];
    System.arraycopy(NLPerfSingleDriver.NEW_PATCH, 0, tmp, 0, singleSize);
    for (int i = 0; i < NUM_IN_BANK; i++) {
      tmp[PATCH_NUM_OFFSET] = (byte)i; // program #
      System.arraycopy(tmp, 0, sysex, i * singleSize, singleSize);
    }
    return new Patch(sysex, this);
  }

  public void requestPatchDump(int bankNum, int patchNum) {
    int devID = ((NordLeadDevice) (getDevice())).getGlobalChannel();
    for (int i = 0; i < NUM_IN_BANK; i++) {
      send(sysexRequestDump.toSysexMessage(devID,
					   new SysexHandler.NameValue("bankNum", 41),
					   new SysexHandler.NameValue("patchNum", i)));
      try {
        Thread.sleep(250); // it takes some time for each performance to be sent
      } catch (Exception e) {
        ErrorMsg.reportStatus (e);
        ErrorMsg.reportError("Error", "Unable to request Patch " + i);
      }
    }
  }
  
}

