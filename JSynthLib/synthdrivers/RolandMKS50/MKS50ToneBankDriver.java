// written by Kenneth L. Martinez
// @version $Id$

package synthdrivers.RolandMKS50;

import core.BankDriver;
import core.ErrorMsg;
import core.Patch;

public class MKS50ToneBankDriver extends BankDriver
{

   public MKS50ToneBankDriver()
   {
     super ("Tone Bank","Kenneth L. Martinez",64,4);
     sysexID = "F041370*23200100";
     patchSize = 4256;
     patchNameStart = 5;
     patchNameSize = 8;
     deviceIDoffset = 3;
     bankNumbers  = new String[] {"Tone Bank"};
     patchNumbers = new String[] {"11-", "12-", "13-", "14-", "15-", "16-", "17-", "18-",
                                  "21-", "22-", "23-", "24-", "25-", "26-", "27-", "28-",
                                  "31-", "32-", "33-", "34-", "35-", "36-", "37-", "38-",
                                  "41-", "42-", "43-", "44-", "45-", "46-", "47-", "48-",
                                  "51-", "52-", "53-", "54-", "55-", "56-", "57-", "58-",
                                  "61-", "62-", "63-", "64-", "65-", "66-", "67-", "68-",
                                  "71-", "72-", "73-", "74-", "75-", "76-", "77-", "78-",
                                  "81-", "82-", "83-", "84-", "85-", "86-", "87-", "88-"};
    singleSize = 54;
    singleSysexID = "F041350*232001";
  }

  public void calculateChecksum(Patch p)
  {
    // MKS-50 doesn't use checksum
  }

//  protected static void calculateChecksum(Patch p, int start, int end, int ofs)
//  {
//    // MKS-50 doesn't use checksum
//  }

  public void storePatch (Patch p, int bankNum, int patchNum)
  {
    sendPatchWorker(p);
  }

  /*
  public void choosePatch (Patch p)
  {
    JOptionPane.showMessageDialog(PatchEdit.getInstance(),
      "To send a Tone bank:\n"
      +"1. turn Memory Protect off\n"
      +"2. make sure the MKS-50's midi channel matches JSynthLib\n"
      +"3. press Data Transfer, select Bulk*Load and the bank (T-a or T-b)\n"
      +"4. press Write\n"
      +"5. now click OK to send the bank",
      "Store Tone Bank",
      JOptionPane.PLAIN_MESSAGE
    );
    storePatch(p, 0, 0);
  }
  */
  public void putPatch(Patch bank, Patch p, int patchNum)
  {
    if (!canHoldPatch(p))
    {
      ErrorMsg.reportError("Error", "This type of patch does not fit in to this type of bank.");
      return;
    }

    byte bankSysex[] = new byte[32];
    // DCO ENV MODE
    bankSysex[5] |= (byte)((((Patch)p).sysex[7] & 0x02) << 6);
    bankSysex[6] |= (byte)((((Patch)p).sysex[7] & 0x01) << 7);
    // VCF ENV MODE
    bankSysex[7] |= (byte)((((Patch)p).sysex[8] & 0x02) << 6);
    bankSysex[8] |= (byte)((((Patch)p).sysex[8] & 0x01) << 7);
    // VCA ENV MODE
    bankSysex[9] |= (byte)((((Patch)p).sysex[9] & 0x02) << 6);
    bankSysex[10] |= (byte)((((Patch)p).sysex[9] & 0x01) << 7);
    // DCO WAVEFORM PULSE
    bankSysex[17] |= (byte)((((Patch)p).sysex[10] & 0x02) << 6);
    bankSysex[18] |= (byte)((((Patch)p).sysex[10] & 0x01) << 7);
    // DCO WAVEFORM SAWTOOTH
    bankSysex[14] |= (byte)((((Patch)p).sysex[11] & 0x04) << 5);
    bankSysex[15] |= (byte)((((Patch)p).sysex[11] & 0x02) << 6);
    bankSysex[16] |= (byte)((((Patch)p).sysex[11] & 0x01) << 7);
    // DCO WAVEFORM SUB
    bankSysex[11] |= (byte)((((Patch)p).sysex[12] & 0x04) << 5);
    bankSysex[12] |= (byte)((((Patch)p).sysex[12] & 0x02) << 6);
    bankSysex[13] |= (byte)((((Patch)p).sysex[12] & 0x01) << 7);
    // DCO RANGE
    bankSysex[21] |= (byte)((((Patch)p).sysex[13] & 0x02) << 6);
    bankSysex[22] |= (byte)((((Patch)p).sysex[13] & 0x01) << 7);
    // DCO SUB LEVEL
    bankSysex[23] |= (byte)((((Patch)p).sysex[14] & 0x02) << 6);
    bankSysex[24] |= (byte)((((Patch)p).sysex[14] & 0x01) << 7);
    // DCO NOISE LEVEL
    bankSysex[25] |= (byte)((((Patch)p).sysex[15] & 0x02) << 6);
    bankSysex[26] |= (byte)((((Patch)p).sysex[15] & 0x01) << 7);
    // HPF CUTOFF FREQ
    bankSysex[19] |= (byte)((((Patch)p).sysex[16] & 0x02) << 6);
    bankSysex[20] |= (byte)((((Patch)p).sysex[16] & 0x01) << 7);
    // CHORUS
    bankSysex[4] |= (byte)((((Patch)p).sysex[17] & 0x01) << 7);
    // DCO LFO MOD DEPTH
    bankSysex[3] |= ((Patch)p).sysex[18];
    // DCO ENV MOD DEPTH
    bankSysex[4] |= ((Patch)p).sysex[19];
    // DCO AFTER DEPTH (sysex docs say 0-127 value, but real range is 0-15 and it's stored shifted)
    bankSysex[0] |= (byte)((((Patch)p).sysex[20] & 0x78) << 1);
    // DCO PW/PWM DEPTH
    bankSysex[5] |= ((Patch)p).sysex[21];
    // DCO PWM RATE
    bankSysex[6] |= ((Patch)p).sysex[22];
    // VCF CUTOFF FREQ
    bankSysex[7] |= ((Patch)p).sysex[23];
    // VCF RESONANCE
    bankSysex[8] |= ((Patch)p).sysex[24];
    // VCF LFO MOD DEPTH
    bankSysex[10] |= ((Patch)p).sysex[25];
    // VCF ENV MOD DEPTH
    bankSysex[9] |= ((Patch)p).sysex[26];
    // VCF KEY FOLLOW (sysex docs say 0-127 value, but real range is 0-15 and it's stored shifted)
    bankSysex[0] |= (byte)((((Patch)p).sysex[27] & 0x78) >> 3);
    // VCF AFTER DEPTH (sysex docs say 0-127 value, but real range is 0-15 and it's stored shifted)
    bankSysex[1] |= (byte)((((Patch)p).sysex[28] & 0x78) << 1);
    // VCA LEVEL
    bankSysex[11] |= ((Patch)p).sysex[29];
    // VCA AFTER DEPTH (sysex docs say 0-127 value, but real range is 0-15 and it's stored shifted)
    bankSysex[1] |= (byte)((((Patch)p).sysex[30] & 0x78) >> 3);
    // LFO RATE
    bankSysex[12] |= ((Patch)p).sysex[31];
    // LFO DELAY TIME
    bankSysex[13] |= ((Patch)p).sysex[32];
    // ENV T1
    bankSysex[14] |= ((Patch)p).sysex[33];
    // ENV L1
    bankSysex[15] |= ((Patch)p).sysex[34];
    // ENV T2
    bankSysex[16] |= ((Patch)p).sysex[35];
    // ENV L2
    bankSysex[17] |= ((Patch)p).sysex[36];
    // ENV T3
    bankSysex[18] |= ((Patch)p).sysex[37];
    // ENV L3
    bankSysex[19] |= ((Patch)p).sysex[38];
    // ENV T4
    bankSysex[20] |= ((Patch)p).sysex[39];
    // ENV KEY FOLLOW (sysex docs say 0-127 value, but real range is 0-15 and it's stored shifted)
    bankSysex[2] |= (byte)((((Patch)p).sysex[40] & 0x78) << 1);
    // CHORUS RATE
    bankSysex[27] |= (byte)((((Patch)p).sysex[41] & 0x03) << 6);
    bankSysex[28] |= (byte)((((Patch)p).sysex[41] & 0x0C) << 4);
    bankSysex[29] |= (byte)((((Patch)p).sysex[41] & 0x30) << 2);
    bankSysex[30] |= (byte)(((Patch)p).sysex[41] & 0x40);
    // BENDER RANGE
    bankSysex[2] |= ((Patch)p).sysex[40];
    // TONE NAME (10 bytes)
    for (int i = 0; i < 10; i++)
    {
      bankSysex[i+21] |= ((Patch)p).sysex[43+i];
    }
    byte bankSysexNibbles[] = new byte[64];
    for (int i = 0; i < 32; i++)
    {
      bankSysexNibbles[i*2] = (byte)(bankSysex[i] & 0x0F);
      bankSysexNibbles[i*2+1] = (byte)((bankSysex[i] & 0xF0) >> 4);
    }
    int patchOffset = getPatchStart(patchNum);
    System.arraycopy(bankSysexNibbles, 0, ((Patch)bank).sysex, patchOffset, 64);
  }

  public Patch getPatch(Patch bank, int patchNum)
  {
      byte bankSysexNibbles[] = new byte[64];
      byte bankSysex[] = new byte[32];
      byte sysex[] = new byte[54];
      sysex[0] = (byte)0xF0;
      sysex[1] = (byte)0x41;
      sysex[2] = (byte)0x35;
      sysex[3] = (byte)0x00;
      sysex[4] = (byte)0x23;
      sysex[5] = (byte)0x20;
      sysex[6] = (byte)0x01;
      sysex[53] = (byte)0xF7;
      int patchOffset = getPatchStart(patchNum);
      System.arraycopy(((Patch)bank).sysex, patchOffset, bankSysexNibbles, 0, 64);

      //   convert bank tone (31 bytes, lo/hi nibble) to single tone (46 bytes)
      for (int i = 0; i < 32; i++)
      {
        bankSysex[i] = (byte)(bankSysexNibbles[i*2] | bankSysexNibbles[i*2+1] << 4);
      }
      // DCO ENV MODE
      sysex[7] = (byte)((bankSysex[5] & 0x80) >> 6 | (bankSysex[6] & 0x80) >> 7);
      // VCF ENV MODE
      sysex[8] = (byte)((bankSysex[7] & 0x80) >> 6 | (bankSysex[8] & 0x80) >> 7);
      // VCA ENV MODE
      sysex[9] = (byte)((bankSysex[9] & 0x80) >> 6 | (bankSysex[10] & 0x80) >> 7);
      // DCO WAVEFORM PULSE
      sysex[10] = (byte)((bankSysex[17] & 0x80) >> 6 | (bankSysex[18] & 0x80) >> 7);
      // DCO WAVEFORM SAWTOOTH
      sysex[11] = (byte)((bankSysex[14] & 0x80) >> 5 | (bankSysex[15] & 0x80) >> 6 | (bankSysex[16] & 0x80) >> 7);
      // DCO WAVEFORM SUB
      sysex[12] = (byte)((bankSysex[11] & 0x80) >> 5 | (bankSysex[12] & 0x80) >> 6 | (bankSysex[13] & 0x80) >> 7);
      // DCO RANGE
      sysex[13] = (byte)((bankSysex[21] & 0x80) >> 6 | (bankSysex[22] & 0x80) >> 7);
      // DCO SUB LEVEL
      sysex[14] = (byte)((bankSysex[23] & 0x80) >> 6 | (bankSysex[24] & 0x80) >> 7);
      // DCO NOISE LEVEL
      sysex[15] = (byte)((bankSysex[25] & 0x80) >> 6 | (bankSysex[26] & 0x80) >> 7);
      // HPF CUTOFF FREQ
      sysex[16] = (byte)((bankSysex[19] & 0x80) >> 6 | (bankSysex[20] & 0x80) >> 7);
      // CHORUS
      sysex[17] = (byte)((bankSysex[4] & 0x80) >> 7);
      // DCO LFO MOD DEPTH
      sysex[18] = (byte)(bankSysex[3] & 0x7F);
      // DCO ENV MOD DEPTH
      sysex[19] = (byte)(bankSysex[4] & 0x7F);
      // DCO AFTER DEPTH (sysex docs say 0-127 value, but real range is 0-15 and it's stored shifted)
      sysex[20] = (byte)((bankSysex[0] & 0xF0) >> 1);
      // DCO PW/PWM DEPTH
      sysex[21] = (byte)(bankSysex[5] & 0x7F);
      // DCO PWM RATE
      sysex[22] = (byte)(bankSysex[6] & 0x7F);
      // VCF CUTOFF FREQ
      sysex[23] = (byte)(bankSysex[7] & 0x7F);
      // VCF RESONANCE
      sysex[24] = (byte)(bankSysex[8] & 0x7F);
      // VCF LFO MOD DEPTH
      sysex[25] = (byte)(bankSysex[10] & 0x7F);
      // VCF ENV MOD DEPTH
      sysex[26] = (byte)(bankSysex[9] & 0x7F);
      // VCF KEY FOLLOW (sysex docs say 0-127 value, but real range is 0-15 and it's stored shifted)
      sysex[27] = (byte)((bankSysex[0] & 0x0F) << 3);
      // VCF AFTER DEPTH (sysex docs say 0-127 value, but real range is 0-15 and it's stored shifted)
      sysex[28] = (byte)((bankSysex[1] & 0xF0) >> 1);
      // VCA LEVEL
      sysex[29] = (byte)(bankSysex[11] & 0x7F);
      // VCA AFTER DEPTH (sysex docs say 0-127 value, but real range is 0-15 and it's stored shifted)
      sysex[30] = (byte)((bankSysex[1] & 0x0F) << 3);
      // LFO RATE
      sysex[31] = (byte)(bankSysex[12] & 0x7F);
      // LFO DELAY TIME
      sysex[32] = (byte)(bankSysex[13] & 0x7F);
      // ENV T1
      sysex[33] = (byte)(bankSysex[14] & 0x7F);
      // ENV L1
      sysex[34] = (byte)(bankSysex[15] & 0x7F);
      // ENV T2
      sysex[35] = (byte)(bankSysex[16] & 0x7F);
      // ENV L2
      sysex[36] = (byte)(bankSysex[17] & 0x7F);
      // ENV T3
      sysex[37] = (byte)(bankSysex[18] & 0x7F);
      // ENV L3
      sysex[38] = (byte)(bankSysex[19] & 0x7F);
      // ENV T4
      sysex[39] = (byte)(bankSysex[20] & 0x7F);
      // ENV KEY FOLLOW (sysex docs say 0-127 value, but real range is 0-15 and it's stored shifted)
      sysex[40] = (byte)((bankSysex[2] & 0xF0) >> 1);
      // CHORUS RATE
      sysex[41] = (byte)((bankSysex[27] & 0xC0) >> 6 | (bankSysex[28] & 0xC0) >> 4
                       | (bankSysex[29] & 0xC0) >> 2 | (bankSysex[30] & 0x40));
      // BENDER RANGE
      sysex[42] = (byte)(bankSysex[2] & 0x0F);
      // TONE NAME (10 bytes)
      for (int i = 0; i < 10; i++)
      {
        sysex[43+i] = (byte)(bankSysex[i+21] & 0x3F);
      }

      return new Patch(sysex);
  }

  public String getPatchName(Patch p, int patchNum)
  {
      byte bankSysexNibbles[] = new byte[64];
      byte bankSysex[] = new byte[32];
      char patchName[] = new char[10];
      int patchOffset = getPatchStart(patchNum);
      System.arraycopy(((Patch)p).sysex, patchOffset, bankSysexNibbles, 0, 64);
      for (int i = 0; i < 32; i++)
      {
        bankSysex[i] = (byte)(bankSysexNibbles[i*2] | bankSysexNibbles[i*2+1] << 4);
      }
      // TONE NAME (10 bytes)
      for (int i = 0; i < 10; i++)
      {
        patchName[i] = MKS50ToneSingleDriver.nameChars[(byte)(bankSysex[i+21] & 0x3F)];
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
        System.arraycopy(((Patch)p).sysex, 266*i, tmp, 0, 266);
        if (deviceIDoffset > 0)
          tmp[deviceIDoffset] = (byte)(getChannel()-1);
        send(tmp);
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

