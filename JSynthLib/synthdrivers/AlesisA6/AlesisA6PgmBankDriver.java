// written by Kenneth L. Martinez

package synthdrivers.AlesisA6;

import core.*;
import javax.swing.*;

public class AlesisA6PgmBankDriver extends BankDriver
{

  public AlesisA6PgmBankDriver()
  {
    manufacturer = "Alesis";
    authors = "Kenneth L. Martinez";
    model = "A6";
    patchType = "Prog Bank";
    id = "Andromeda";
    sysexID = "F000000E1D00**00";
    sysexRequestDump = new SysexHandler("F0 00 00 0E 1D 0A *bankNum* F7");
    patchSize = 300800;
    patchNameStart = 2; // does NOT include sysex header
    patchNameSize = 16;
    deviceIDoffset = -1;
    bankNumbers  = AlesisA6PgmSingleDriver.bankList;
    patchNumbers = AlesisA6PgmSingleDriver.patchList;
    numPatches = patchNumbers.length;
    numColumns = 4;
    singleSize = 2350;
    singleSysexID = "F000000E1D00007F";
  }

  public void calculateChecksum(Patch p)
  {
    // A6 doesn't use checksum
  }

  public void calculateChecksum(Patch p, int start, int end, int ofs)
  {
    // A6 doesn't use checksum
  }

  public void storePatch (Patch p, int bankNum, int patchNum)
  {
    if (bankNum == 1 || bankNum == 2)
      JOptionPane.showMessageDialog(PatchEdit.instance,
        "Cannot send to a preset bank",
        "Store Patch",
        JOptionPane.WARNING_MESSAGE
      );
    else
      sendPatchWorker(p, bankNum);
  }

  public void putPatch(Patch bank, Patch p, int patchNum)
  {
    if (!canHoldPatch(p))
    {
      ErrorMsg.reportError("Error", "This type of patch does not fit in to this type of bank.");
      return;
    }

    System.arraycopy(p.sysex, 0, bank.sysex, patchNum * 2350, 2350);
    bank.sysex[patchNum * 2350 + 6] = 0; // user bank
    bank.sysex[patchNum * 2350 + 7] = (byte)patchNum; // set program #
  }

  public Patch getPatch(Patch bank, int patchNum)
  {
    byte sysex[] = new byte[2350];
    System.arraycopy(bank.sysex, patchNum * 2350, sysex, 0, 2350);
    Patch p = new Patch(sysex);
    return p;
  }

  public String getPatchName(Patch p, int patchNum)
  {
    Patch pgm = getPatch(p, patchNum);
    try {
      char c[] = new char[patchNameSize];
      for (int i = 0; i < patchNameSize; i++)
        c[i] = (char)(AlesisA6PgmSingleDriver.getA6PgmByte(pgm.sysex, i + patchNameStart));
      return new String(c);
    }
    catch (Exception ex)
    {
      return "-";
    }
  }

  public void setPatchName(Patch p,int patchNum, String name)
  {
    Patch pgm = getPatch(p, patchNum);
    if (name.length() < patchNameSize + 4)
      name = name + "                ";
    byte nameByte[] = name.getBytes();
    for (int i = 0; i < patchNameSize; i++)
    {
      AlesisA6PgmSingleDriver.setA6PgmByte(nameByte[i], pgm.sysex, i + patchNameStart);
    }
    putPatch(p, pgm, patchNum);
  }

  protected void sendPatchWorker (Patch p)
  {
    sendPatchWorker(p, 0);
  }

  protected void sendPatchWorker (Patch p, int bankNum)
  {
    byte tmp[] = new byte[2350];  // send in 128 single-program messages
    try
    {
      PatchEdit.waitDialog.show();
      for (int i = 0; i < 128; i++)
      {
        System.arraycopy(p.sysex, i * 2350, tmp, 0, 2350);
        tmp[6] = (byte)bankNum;
        tmp[7] = (byte)i; // program #
        PatchEdit.MidiOut.writeLongMessage(port, tmp);
        Thread.sleep(15);
      }
      PatchEdit.waitDialog.hide();
    }
    catch (Exception e)
    {
      ErrorMsg.reportStatus (e);
      ErrorMsg.reportError("Error", "Unable to send Patch");
    }
  }

  public void requestPatchDump(int bankNum, int patchNum)
  {
    sysexRequestDump.send(port, (byte)channel, new NameValue("bankNum", bankNum),
      new NameValue("patchNum", patchNum)
    );
  }
}

