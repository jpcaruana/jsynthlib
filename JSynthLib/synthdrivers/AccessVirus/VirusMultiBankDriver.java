// written by Kenneth L. Martinez

package synthdrivers.AccessVirus;

import core.BankDriver;
import core.ErrorMsg;
import core.IPatch;
import core.NameValue;
import core.Patch;
import core.PatchEdit;
import core.SysexHandler;

/**
 * @version $Id$
 * @author Kenneth L. Martinez
 */
public class VirusMultiBankDriver extends BankDriver {
  static final int BANK_NUM_OFFSET = 7;
  static final int PATCH_NUM_OFFSET = 8;
  static final int NUM_IN_BANK = 128;
  
   public VirusMultiBankDriver ()  {
    super ("Multi Bank","Kenneth L. Martinez",VirusMultiSingleDriver.PATCH_LIST.length,4);
    sysexID = "F000203301**11";
    sysexRequestDump = new SysexHandler("F0 00 20 33 01 10 33 01 F7");
    singleSysexID = "F000203301**11";
    singleSize = 267;
    patchSize = singleSize * NUM_IN_BANK;
    patchNameStart = 13;
    patchNameSize = 10;
    deviceIDoffset = 5;
    checksumOffset = 265;
    checksumStart = 5;
    checksumEnd = 264;
    bankNumbers  = VirusMultiSingleDriver.BANK_LIST;
    patchNumbers = VirusMultiSingleDriver.PATCH_LIST;
  }

  public void calculateChecksum(byte sysex[], int start, int end, int ofs) {
    int sum = 0;
    for (int i = start; i <= end; i++) {
      sum += sysex[i];
    }
    sysex[ofs] = (byte)(sum & 0x7F);
  }

  public void calculateChecksum(IPatch p, int start, int end, int ofs) {
    calculateChecksum(((Patch)p).sysex, start, end, ofs);
  }

  public void storePatch (IPatch p, int bankNum, int patchNum) {
    sendPatchWorker((Patch)p, 1);
  }

  public void putPatch(IPatch bank, IPatch p, int patchNum) {
    if (!canHoldPatch(p)) {
      ErrorMsg.reportError("Error", "This type of patch does not fit in to this type of bank.");
      return;
    }

    System.arraycopy(((Patch)p).sysex, 0, ((Patch)bank).sysex, patchNum * singleSize, singleSize);
    ((Patch)bank).sysex[patchNum * singleSize + PATCH_NUM_OFFSET] = (byte)patchNum; // set multi #
  }

  public IPatch getPatch(IPatch bank, int patchNum) {
    byte sysex[] = new byte[singleSize];
    System.arraycopy(((Patch)bank).sysex, patchNum * singleSize, sysex, 0, singleSize);
    IPatch p = new Patch(sysex, getDevice());
    return p;
  }

  public String getPatchName(IPatch p, int patchNum) {
    Patch pgm = (Patch)getPatch(p, patchNum);
    try {
      char c[] = new char[patchNameSize];
      for (int i = 0; i < patchNameSize; i++)
        c[i] = (char)pgm.sysex[i + patchNameStart];
      return new String(c);
    } catch (Exception ex) {
      return "-";
    }
  }

  public void setPatchName(IPatch p,int patchNum, String name) {
    Patch pgm = (Patch)getPatch(p, patchNum);
    if (name.length() < patchNameSize + 4) {
      name = name + "                ";
    }
    byte nameByte[] = name.getBytes();
    for (int i = 0; i < patchNameSize; i++) {
      pgm.sysex[i + patchNameStart] = nameByte[i];
    }
    putPatch(p, pgm, patchNum);
  }

  protected void sendPatch (IPatch p) {
    sendPatchWorker((Patch)p, 0);
  }

  protected void sendPatchWorker (Patch p, int bankNum) {
    byte tmp[] = new byte[singleSize];  // send in 128 single-multi messages
    int max;
    try {
      PatchEdit.showWaitDialog();
      for (int i = 0; i < NUM_IN_BANK; i++) {
        System.arraycopy(p.sysex, i * singleSize, tmp, 0, singleSize);
        tmp[deviceIDoffset] = (byte) (getDeviceID() - 1);
        tmp[BANK_NUM_OFFSET] = (byte)1;
        tmp[PATCH_NUM_OFFSET] = (byte)i; // multi #
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

  public IPatch createNewPatch() {
    byte tmp[] = new byte[singleSize];
    byte sysex[] = new byte[patchSize];
    System.arraycopy(VirusMultiSingleDriver.NEW_PATCH, 0, tmp, 0, singleSize);
    for (int i = 0; i < NUM_IN_BANK; i++) {
      tmp[PATCH_NUM_OFFSET] = (byte)i; // multi #
      System.arraycopy(tmp, 0, sysex, i * singleSize, singleSize);
    }
    IPatch p = new Patch(sysex, this);
    return p;
  }

  public void requestPatchDump(int bankNum, int patchNum) {
    send(sysexRequestDump.toSysexMessage(getDeviceID(), new NameValue("bankNum", 1)));
  }
}

