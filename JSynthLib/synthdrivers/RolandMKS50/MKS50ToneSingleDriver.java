// written by Kenneth L. Martinez
// @version $Id$

package synthdrivers.RolandMKS50;

import core.Driver;
import core.JSLFrame;
import core.Patch;
import core.SysexHandler;

public class MKS50ToneSingleDriver extends Driver
{
  static final char nameChars[] = {
    'A', 'B', 'C', 'D',   'E', 'F', 'G', 'H',
    'I', 'J', 'K', 'L',   'M', 'N', 'O', 'P',
    'Q', 'R', 'S', 'T',   'U', 'V', 'W', 'X',
    'Y', 'Z', 'a', 'b',   'c', 'd', 'e', 'f',
    'g', 'h', 'i', 'j',   'k', 'l', 'm', 'n',
    'o', 'p', 'q', 'r',   's', 't', 'u', 'v',
    'w', 'x', 'y', 'z',   '0', '1', '2', '3',
    '4', '5', '6', '7',   '8', '9', ' ', '-'
  };

  public MKS50ToneSingleDriver()
  {
    super ("Tone Single","Kenneth L. Martinez");
    sysexID = "F041350*232001";
    sysexRequestDump = new SysexHandler("F0 10 06 04 01 *patchNum* F7");
    patchSize = 54;
    patchNameStart = 43;
    patchNameSize = 10;
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
  }

  public void calculateChecksum(Patch p)
  {
    // MKS-50 doesn't use checksum
  }

  public void calculateChecksum(Patch p, int start, int end, int ofs)
  {
    // MKS-50 doesn't use checksum
  }

  public void setBankNum(int bankNum)
  {
    // MKS-50 doesn't have banks: pgm# 0-63 is group A, 64-127 is group B
  }

  public String getPatchName(Patch ip) {
    try {
      char c[] = new char[patchNameSize];
      for (int i = 0; i < patchNameSize; i++)
        c[i] = nameChars[((Patch)ip).sysex[i+patchNameStart]];
      return new String(c);
    }
    catch (Exception ex)
    {
      return "-";
    }
  }

  public void setPatchName(Patch p, String name)
  {
    String s = new String(nameChars);
    for (int i = 0; i < patchNameSize; i++)
    {
      int j;
      if (i < name.length())
      {
        j = s.indexOf(name.charAt(i));
        if (j == -1)
          j = 62;  // convert invalid character to space
      }
      else
        j = 62;  // pad with spaces
      ((Patch)p).sysex[i+patchNameStart] = (byte)j;
    }
  }

  public Patch createNewPatch()
  {
    byte sysex[] = {
      (byte)0xF0, (byte)0x41, (byte)0x35, (byte)0x00, (byte)0x23,
      (byte)0x20, (byte)0x01, (byte)0x00, (byte)0x00, (byte)0x01,
      (byte)0x00, (byte)0x01, (byte)0x00, (byte)0x02, (byte)0x00,
      (byte)0x00, (byte)0x01, (byte)0x00, (byte)0x00, (byte)0x00,
      (byte)0x00, (byte)0x40, (byte)0x00, (byte)0x4D, (byte)0x00,
      (byte)0x00, (byte)0x62, (byte)0x50, (byte)0x00, (byte)0x60,
      (byte)0x00, (byte)0x57, (byte)0x00, (byte)0x00, (byte)0x7F,
      (byte)0x00, (byte)0x7A, (byte)0x30, (byte)0x34, (byte)0x28,
      (byte)0x08, (byte)0x50, (byte)0x02, (byte)0x0D, (byte)0x1E,
      (byte)0x30, (byte)0x0F, (byte)0x1A, (byte)0x2D, (byte)0x1C,
      (byte)0x21, (byte)0x3E, (byte)0x3E, (byte)0xF7
    };
    Patch p = new Patch(sysex, this);
    setPatchName(p, "NewPatch");
    return p;
  }

  public JSLFrame editPatch(Patch p)
  {
     return new MKS50ToneSingleEditor((Patch)p);
  }
}

