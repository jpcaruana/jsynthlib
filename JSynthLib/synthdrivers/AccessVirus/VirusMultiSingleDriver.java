// written by Kenneth L. Martinez

package synthdrivers.AccessVirus;

import core.Driver;
import core.Patch;
import core.SysexHandler;
/**
 * @version $Id$
 * @author Kenneth L. Martinez
 */
public class VirusMultiSingleDriver extends Driver {
  static final String BANK_LIST[] = new String[] { "User" };
  static final String PATCH_LIST[] = new String[] {
    "000", "001", "002", "003", "004", "005", "006", "007", "008", "009",
    "010", "011", "012", "013", "014", "015", "016", "017", "018", "019",
    "020", "021", "022", "023", "024", "025", "026", "027", "028", "029",
    "030", "031", "032", "033", "034", "035", "036", "037", "038", "039",
    "040", "041", "042", "043", "044", "045", "046", "047", "048", "049",
    "050", "051", "052", "053", "054", "055", "056", "057", "058", "059",
    "060", "061", "062", "063", "064", "065", "066", "067", "068", "069",
    "070", "071", "072", "073", "074", "075", "076", "077", "078", "079",
    "080", "081", "082", "083", "084", "085", "086", "087", "088", "089",
    "090", "091", "092", "093", "094", "095", "096", "097", "098", "099",
    "100", "101", "102", "103", "104", "105", "106", "107", "108", "109",
    "110", "111", "112", "113", "114", "115", "116", "117", "118", "119",
    "120", "121", "122", "123", "124", "125", "126", "127"
  };
  static final int BANK_NUM_OFFSET = 7;
  static final int PATCH_NUM_OFFSET = 8;
  static final byte NEW_PATCH[] = {
    (byte)0xF0, (byte)0x00, (byte)0x20, (byte)0x33, (byte)0x01, (byte)0x10, (byte)0x11, (byte)0x00,
    (byte)0x7F, (byte)0x01, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x53, (byte)0x65, (byte)0x71,
    (byte)0x75, (byte)0x65, (byte)0x6E, (byte)0x63, (byte)0x65, (byte)0x72, (byte)0x20, (byte)0x00,
    (byte)0x48, (byte)0x01, (byte)0x7F, (byte)0x00, (byte)0x10, (byte)0x00, (byte)0x01, (byte)0x01,
    (byte)0x09, (byte)0x40, (byte)0x40, (byte)0x40, (byte)0x40, (byte)0x40, (byte)0x40, (byte)0x40,
    (byte)0x40, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
    (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
    (byte)0x00, (byte)0x00, (byte)0x01, (byte)0x02, (byte)0x03, (byte)0x04, (byte)0x05, (byte)0x06,
    (byte)0x07, (byte)0x08, (byte)0x09, (byte)0x0D, (byte)0x0B, (byte)0x0C, (byte)0x0D, (byte)0x0E,
    (byte)0x0F, (byte)0x00, (byte)0x01, (byte)0x02, (byte)0x03, (byte)0x04, (byte)0x05, (byte)0x06,
    (byte)0x07, (byte)0x08, (byte)0x09, (byte)0x0D, (byte)0x0B, (byte)0x0C, (byte)0x0D, (byte)0x0E,
    (byte)0x0F, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
    (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
    (byte)0x00, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F,
    (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F,
    (byte)0x7F, (byte)0x40, (byte)0x40, (byte)0x40, (byte)0x40, (byte)0x40, (byte)0x40, (byte)0x40,
    (byte)0x40, (byte)0x40, (byte)0x40, (byte)0x40, (byte)0x40, (byte)0x40, (byte)0x40, (byte)0x40,
    (byte)0x40, (byte)0x40, (byte)0x40, (byte)0x40, (byte)0x40, (byte)0x40, (byte)0x40, (byte)0x40,
    (byte)0x40, (byte)0x40, (byte)0x40, (byte)0x40, (byte)0x40, (byte)0x40, (byte)0x40, (byte)0x40,
    (byte)0x40, (byte)0x40, (byte)0x40, (byte)0x40, (byte)0x40, (byte)0x40, (byte)0x40, (byte)0x40,
    (byte)0x40, (byte)0x40, (byte)0x40, (byte)0x40, (byte)0x40, (byte)0x40, (byte)0x40, (byte)0x40,
    (byte)0x40, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
    (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
    (byte)0x00, (byte)0x01, (byte)0x01, (byte)0x01, (byte)0x01, (byte)0x01, (byte)0x01, (byte)0x01,
    (byte)0x01, (byte)0x01, (byte)0x01, (byte)0x01, (byte)0x01, (byte)0x01, (byte)0x01, (byte)0x01,
    (byte)0x01, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
    (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
    (byte)0x00, (byte)0x41, (byte)0x46, (byte)0x40, (byte)0x48, (byte)0x41, (byte)0x49, (byte)0x47,
    (byte)0x41, (byte)0x42, (byte)0x47, (byte)0x40, (byte)0x45, (byte)0x41, (byte)0x49, (byte)0x47,
    (byte)0x46, (byte)0x40, (byte)0x40, (byte)0x40, (byte)0x40, (byte)0x40, (byte)0x40, (byte)0x40,
    (byte)0x40, (byte)0x40, (byte)0x40, (byte)0x40, (byte)0x40, (byte)0x40, (byte)0x40, (byte)0x40,
    (byte)0x40, (byte)0x47, (byte)0x47, (byte)0x47, (byte)0x47, (byte)0x47, (byte)0x47, (byte)0x47,
    (byte)0x47, (byte)0x47, (byte)0x47, (byte)0x47, (byte)0x47, (byte)0x47, (byte)0x47, (byte)0x47,
    (byte)0x47, (byte)0x7B, (byte)0xF7
  };
  
 
 
  
  public VirusMultiSingleDriver() {
    super ("Multi Single","Kenneth L. Martinez");
    sysexID = "F000203301**11";
    sysexRequestDump = new SysexHandler("F0 00 20 33 01 10 31 01 *patchNum* F7");

    patchSize = 267;
    patchNameStart = 13;
    patchNameSize = 10;
    deviceIDoffset = 5;
    checksumOffset = 265;
    checksumStart = 5;
    checksumEnd = 264;
    bankNumbers = BANK_LIST;
    patchNumbers = PATCH_LIST;
  }

  protected static void calculateChecksum(Patch p, int start, int end, int ofs) {
    int sum = 0;
    for (int i = start; i <= end; i++) {
      sum += p.sysex[i];
    }
    p.sysex[ofs] = (byte)(sum & 0x7F);
  }

  public void sendPatch(Patch p) {
    sendPatch((Patch)p, 0, 0); // using single mode edit buffer
  }

  public void sendPatch(Patch p, int bankNum, int patchNum) {
    Patch p2 = new Patch(p.sysex);
    p2.sysex[deviceIDoffset] = (byte) (getDeviceID() - 1);
    p2.sysex[BANK_NUM_OFFSET] = (byte)bankNum;
    p2.sysex[PATCH_NUM_OFFSET] = (byte)patchNum;
    calculateChecksum(p2);
    sendPatchWorker(p2);
  }

  // Sends a patch to a set location in the user bank
  public void storePatch(Patch p, int bankNum, int patchNum) {
    sendPatch((Patch)p, 1, patchNum);
  }

  protected void playPatch(Patch p) {
    Patch p2 = new Patch(((Patch)p).sysex);
    p2.sysex[deviceIDoffset] = (byte) (getDeviceID() - 1);
    p2.sysex[BANK_NUM_OFFSET] = 0; // edit buffer
    p2.sysex[PATCH_NUM_OFFSET] = 0; // single mode
    calculateChecksum(p2);
    super.playPatch(p2);
  }

  public Patch createNewPatch() {
    return new Patch(NEW_PATCH, this);
  }

  public void requestPatchDump(int bankNum, int patchNum) {
    send(sysexRequestDump.toSysexMessage(getDeviceID(),
         new SysexHandler.NameValue("bankNum", 1), new SysexHandler.NameValue("patchNum", patchNum)));
  }
}

