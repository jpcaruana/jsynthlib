// written by Kenneth L. Martinez

package synthdrivers.AccessVirus;

import core.*;
import javax.swing.*;

/**
 * @version $Id$
 * @author Kenneth L. Martinez
 */
public class VirusProgBankDriver extends BankDriver {
  static final int BANK_NUM_OFFSET = 7;
  static final int PATCH_NUM_OFFSET = 8;
  static final int NUM_IN_BANK = 128;
  
  public VirusProgBankDriver() {
    super ("Prog Bank","Kenneth L. Martinez",VirusProgSingleDriver.PATCH_LIST.length,4);
    sysexID = "F000203301**10";
    sysexRequestDump = new SysexHandler("F0 00 20 33 01 10 32 *bankNum* F7");
    singleSysexID = "F000203301**10";
    singleSize = 267;
    patchSize = singleSize * NUM_IN_BANK;
    patchNameStart = 249;
    patchNameSize = 10;
    deviceIDoffset = 5;
    checksumOffset = 265;
    checksumStart = 5;
    checksumEnd = 264;
    bankNumbers  = VirusProgSingleDriver.BANK_LIST;
    patchNumbers = VirusProgSingleDriver.PATCH_LIST;
  }

  public void calculateChecksum(byte sysex[], int start, int end, int ofs) {
    int sum = 0;
    for (int i = start; i <= end; i++) {
      sum += sysex[i];
    }
    sysex[ofs] = (byte)(sum & 0x7F);
  }

  public void calculateChecksum(Patch p, int start, int end, int ofs) {
    calculateChecksum(p.sysex, start, end, ofs);
  }

  public void storePatch (Patch p, int bankNum, int patchNum) {
    if (bankNum > 1) {
      JOptionPane.showMessageDialog(PatchEdit.getInstance(),
        "Cannot send to a preset bank",
        "Store Patch",
        JOptionPane.WARNING_MESSAGE
      );
    } else {
      sendPatchWorker(p, bankNum);
    }
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
    Patch p = new Patch(sysex, getDevice());
    return p;
  }

  public String getPatchName(Patch p, int patchNum) {
    Patch pgm = getPatch(p, patchNum);
    try {
      char c[] = new char[patchNameSize];
      for (int i = 0; i < patchNameSize; i++)
        c[i] = (char)pgm.sysex[i + patchNameStart];
      return new String(c);
    } catch (Exception ex) {
      return "-";
    }
  }

  public void setPatchName(Patch p,int patchNum, String name) {
    Patch pgm = getPatch(p, patchNum);
    if (name.length() < patchNameSize + 4) {
      name = name + "                ";
    }
    byte nameByte[] = name.getBytes();
    for (int i = 0; i < patchNameSize; i++) {
      pgm.sysex[i + patchNameStart] = nameByte[i];
    }
    putPatch(p, pgm, patchNum);
  }

  //protected void sendPatchWorker (Patch p) {
  protected void sendPatch (Patch p) {
    sendPatchWorker(p, 0);
  }

  protected void sendPatchWorker (Patch p, int bankNum) {
    byte tmp[] = new byte[singleSize];  // send in 128 single-program messages
    int max;
    try {
      PatchEdit.showWaitDialog();
      for (int i = 0; i < NUM_IN_BANK; i++) {
        System.arraycopy(p.sysex, i * singleSize, tmp, 0, singleSize);
        tmp[deviceIDoffset] = (byte) (getDeviceID() - 1);
        tmp[BANK_NUM_OFFSET] = (byte)(bankNum + 1);
        tmp[PATCH_NUM_OFFSET] = (byte)i; // program #
        calculateChecksum(tmp, checksumStart, checksumEnd,checksumOffset);
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
    System.arraycopy(VirusProgSingleDriver.NEW_PATCH, 0, tmp, 0, singleSize);
    for (int i = 0; i < NUM_IN_BANK; i++) {
      tmp[PATCH_NUM_OFFSET] = (byte)i; // program #
      System.arraycopy(tmp, 0, sysex, i * singleSize, singleSize);
    }
    Patch p = new Patch(sysex, this);
    return p;
  }

  public void requestPatchDump(int bankNum, int patchNum) {
    sysexRequestDump.send(getPort(), (byte) getDeviceID(),
			  new NameValue("bankNum", bankNum + 1));
  }
}

