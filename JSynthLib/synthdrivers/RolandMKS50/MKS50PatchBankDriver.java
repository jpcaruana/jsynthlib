// written by Kenneth L. Martinez

package synthdrivers.RolandMKS50;

import core.*;
import javax.swing.*;

public class MKS50PatchBankDriver extends BankDriver
{

   public MKS50PatchBankDriver()
   {
     manufacturer = "Roland";
     model = "MKS-50";
     patchType = "Patch Bank";
     id = "MKS-50";
     sysexID = "F041370*23300100";
     authors = "Kenneth L. Martinez";
     patchSize = 4256;
     patchNameStart = 5;
     patchNameSize = 8;
     deviceIDoffset = 3;
     bankNumbers  = new String[] {"Patch Bank"};
     patchNumbers = new String[] {"11-", "12-", "13-", "14-", "15-", "16-", "17-", "18-",
                                  "21-", "22-", "23-", "24-", "25-", "26-", "27-", "28-",
                                  "31-", "32-", "33-", "34-", "35-", "36-", "37-", "38-",
                                  "41-", "42-", "43-", "44-", "45-", "46-", "47-", "48-",
                                  "51-", "52-", "53-", "54-", "55-", "56-", "57-", "58-",
                                  "61-", "62-", "63-", "64-", "65-", "66-", "67-", "68-",
                                  "71-", "72-", "73-", "74-", "75-", "76-", "77-", "78-",
                                  "81-", "82-", "83-", "84-", "85-", "86-", "87-", "88-"};
    numPatches = patchNumbers.length;
    numColumns = 4;
    singleSize = 31;
    singleSysexID = "F041350*233001";
  }

  public void calculateChecksum(Patch p)
  {
    // MKS-50 doesn't use checksum
  }

  public void calculateChecksum(Patch p, int start, int end, int ofs)
  {
    // MKS-50 doesn't use checksum
  }

  public void storePatch (Patch p, int bankNum, int patchNum)
  {
    sendPatchWorker(p);
  }

  public void choosePatch (Patch p)
  {
    JOptionPane.showMessageDialog(PatchEdit.instance,
      "To send a Patch bank:\n"
      +"1. turn Memory Protect off\n"
      +"2. make sure the MKS-50's midi channel matches JSynthLib\n"
      +"3. press Data Transfer, select Bulk*Load and the bank (P-A or P-B)\n"
      +"4. press Write\n"
      +"5. now click OK to send the bank",
      "Store Patch Bank",
      JOptionPane.PLAIN_MESSAGE
    );
    storePatch(p, 0, 0);
  }

  public void putPatch(Patch bank, Patch p, int patchNum)
  {
    if (!canHoldPatch(p))
    {
      ErrorMsg.reportError("Error", "This type of patch does not fit in to this type of bank.");
      return;
    }

    byte bankSysex[] = new byte[32];
    // TONE NUMBER
    bankSysex[0] |= p.sysex[7];
    // KEY RANGE LOW
    bankSysex[1] |= (byte)(p.sysex[8] + 4);  // sysex docs didn't show, but needed to get correct value
    // KEY RANGE HIGH
    bankSysex[2] |= (byte)(p.sysex[9] + 4);  // sysex docs didn't show, but needed to get correct value
    // PORTAMENTO TIME
    bankSysex[3] |= p.sysex[10];
    // PORTAMENTO
    bankSysex[10] |= (byte)(p.sysex[11] << 4);
    // MOD SENS
    bankSysex[4] |= p.sysex[12];
    // KEY SHIFT
    bankSysex[5] |= p.sysex[13];
    // VOLUME
    bankSysex[6] |= p.sysex[14];
    // DETUNE
    bankSysex[7] |= p.sysex[15];
    // MIDI FUNCTION
    bankSysex[9] |= p.sysex[16];
    // MONO BENDER RANGE
    bankSysex[8] |= (byte)(p.sysex[17] << 4);
    // CHORD MEMORY
    bankSysex[8] |= p.sysex[18];
    // KEY ASSIGN MODE
    bankSysex[10] |= (byte)(p.sysex[19] & 0x60);
    // PATCH NAME (10 bytes)
    for (int i = 0; i < 10; i++)
    {
      bankSysex[i+11] = p.sysex[20+i];
    }
    byte bankSysexNibbles[] = new byte[64];
    for (int i = 0; i < 32; i++)
    {
      bankSysexNibbles[i*2] = (byte)(bankSysex[i] & 0x0F);
      bankSysexNibbles[i*2+1] = (byte)((bankSysex[i] & 0xF0) >> 4);
    }
    int patchOffset = getPatchStart(patchNum);
    System.arraycopy(bankSysexNibbles, 0, bank.sysex, patchOffset, 64);
  }

  public Patch getPatch(Patch bank, int patchNum)
  {
      byte bankSysexNibbles[] = new byte[64];
      byte bankSysex[] = new byte[32];
      byte sysex[] = new byte[31];
      sysex[0] = (byte)0xF0;
      sysex[1] = (byte)0x41;
      sysex[2] = (byte)0x35;
      sysex[3] = (byte)0x00;
      sysex[4] = (byte)0x23;
      sysex[5] = (byte)0x30;
      sysex[6] = (byte)0x01;
      sysex[30] = (byte)0xF7;
      int patchOffset = getPatchStart(patchNum);
      System.arraycopy(bank.sysex, patchOffset, bankSysexNibbles, 0, 64);

      //   convert bank patch (31 bytes, lo/hi nibble) to single patch (46 bytes)
      for (int i = 0; i < 32; i++)
      {
        bankSysex[i] = (byte)(bankSysexNibbles[i*2] | bankSysexNibbles[i*2+1] << 4);
      }
      // TONE NUMBER
      sysex[7] = bankSysex[0];
      // KEY RANGE LOW
      sysex[8] = (byte)(bankSysex[1] - 4);  // sysex docs didn't show, but needed to get correct value
      // KEY RANGE HIGH
      sysex[9] = (byte)(bankSysex[2] - 4);  // sysex docs didn't show, but needed to get correct value
      // PORTAMENTO TIME
      sysex[10] = bankSysex[3];
      // PORTAMENTO
      sysex[11] = (byte)((bankSysex[10] & 0x10) >> 4);
      // MOD SENS
      sysex[12] = bankSysex[4];
      // KEY SHIFT
      sysex[13] = bankSysex[5];
      // VOLUME
      sysex[14] = bankSysex[6];
      // DETUNE
      sysex[15] = bankSysex[7];
      // MIDI FUNCTION
      sysex[16] = bankSysex[9];
      // MONO BENDER RANGE
      sysex[17] = (byte)((bankSysex[8] & 0xF0) >> 4);
      // CHORD MEMORY
      sysex[18] = (byte)(bankSysex[8] & 0x0F);
      // KEY ASSIGN MODE
      sysex[19] = (byte)(bankSysex[10] & 0x60);
      // PATCH NAME (10 bytes)
      for (int i = 0; i < 10; i++)
      {
        sysex[20+i] = (byte)(bankSysex[i+11] & 0x3F);
      }

      Patch p = new Patch(sysex);
      return p;
  }

  public String getPatchName(Patch p, int patchNum)
  {
      byte bankSysexNibbles[] = new byte[64];
      byte bankSysex[] = new byte[32];
      char patchName[] = new char[10];
      int patchOffset = getPatchStart(patchNum);
      System.arraycopy(p.sysex, patchOffset, bankSysexNibbles, 0, 64);
      for (int i = 0; i < 32; i++)
      {
        bankSysex[i] = (byte)(bankSysexNibbles[i*2] | bankSysexNibbles[i*2+1] << 4);
      }
      // TONE NAME (10 bytes)
      for (int i = 0; i < 10; i++)
      {
        patchName[i] = MKS50ToneSingleDriver.nameChars[(byte)(bankSysex[i+11] & 0x3F)];
      }
      return new String(patchName);
  }

  public int getPatchStart(int PatchNum)
  {
    return PatchNum/4*266 + PatchNum%4*64 + 9;
  }

  protected void sendPatch (Patch p)
  {
    byte []tmp = new byte[266];  // send in 16 messages containing 4 tones each
    try
    {
      for (int i = 0; i < 16; i++)
      {
        System.arraycopy(p.sysex, 266*i, tmp, 0, 266);
        if (deviceIDoffset > 0)
          tmp[deviceIDoffset] = (byte)(channel-1);
        PatchEdit.MidiOut.writeLongMessage(port, tmp);
        Thread.sleep(15);
      }
    }
    catch (Exception e)
    {
      ErrorMsg.reportStatus (e);
      ErrorMsg.reportError("Error", "Unable to send Patch");
    }
  }
}

