package synthdrivers.AlesisQS;

import core.Driver;
import core.ErrorMsg;
import core.NameValue;
import core.Patch;
import core.SysexHandler;

/**
 * AlesisQSEffectsDriver.java
 *
 * Effects program driver for Alesis QS series synths
 * Feb 2002
 * @author Zellyn Hunter (zellyn@bigfoot.com, zjh)
 * @version $Id$
 * GPL v2
 */

public class AlesisQSEffectsDriver extends Driver
{
  public AlesisQSEffectsDriver()
  {
    super ("Effects","Zellyn Hunter");
    sysexID="F000000E0E**";
    sysexRequestDump=new SysexHandler("F0 00 00 0E 0E *opcode* *patchNum* F7");
    //patchSize=350/7*8;
    patchSize=QSConstants.PATCH_SIZE_EFFECTS;
    // zjh - I think this should be 0 so sendPatchWorker doesn't use it
    deviceIDoffset=0;;
    checksumStart=0;
    checksumEnd=0;
    checksumOffset=0;
    //bankNumbers =new String[] {"Internal 1", "Internal 2", "Internal 3", "GenMIDI", "User"};
    bankNumbers = QSConstants.WRITEABLE_BANK_NAMES;
    patchNumbers = QSConstants.PATCH_NUMBERS_EFFECTS_WITH_EDIT_BUFFERS;
  }

  /**
   * Print a byte in binary, for debugging packing/unpacking code
   **/
  public String toBinaryStr(byte b) {
    String output = new String();
    for(int i=7; i>=0 ; i--) {
      output += ( (b>>i) & 1);
    }
    return output;
  }

  /**
   * Override the checksum and do nothing - the Alesis does not use checksums
   * @param p the ignored
   * @param start ignored
   * @param end ignored
   * @param ofs ignored
   */
  public void calculateChecksum(Patch p,int start,int end,int ofs)
  {
    //This synth does not use a checksum
  }

  /**
   * Create a new effects patch
   * @return the new Patch
   */
  public Patch createNewPatch()
  {
    // Copy over the Alesis QS header
    byte [] sysex = new byte[patchSize];
    for (int i=0; i<QSConstants.GENERIC_HEADER.length; i++)
    {
      sysex[i] = QSConstants.GENERIC_HEADER[i];
    }
    // Set it to be a effects, at position 0
    sysex[QSConstants.POSITION_OPCODE] = QSConstants.OPCODE_MIDI_USER_EFFECTS_DUMP;
    sysex[QSConstants.POSITION_LOCATION] = 0;

    // Create the patch
    Patch p = new Patch(sysex, this);
    return p;
  }

  //public JSLFrame editPatch(Patch p)
  // {
  //     return new AlesisQSEffectsEditor(p);
  // }



  /**
   * Copied from Driver.java by zjh.  Requests a patch dump.  If the
   * patch number is 0..127, then use opcode 07 - MIDI User Effects
   * Dump Request.  If the patch number is 128..129, use opcode 09 -
   * MIDI Edit Effects Dump Request.  128 corresponds to the Program
   * mode effects edit buffer, and 129 to the Mix mode effects edit
   * buffer
   * @param bankNum not used
   * @param patchNum the patch number
   */
  public void requestPatchDump(int bankNum, int patchNum)
  {
    //setBankNum(bankNum);
    //setPatchNum(patchNum);

    // default to simple case - get specified patch from the User bank
    int location = patchNum;
    int opcode = QSConstants.OPCODE_MIDI_USER_EFFECTS_DUMP_REQ;

    // if the patch number is > max location, get from Edit buffers
    if (location>QSConstants.MAX_LOCATION_PROG)
    {
      location -= (QSConstants.MAX_LOCATION_PROG + 1);
      opcode = QSConstants.OPCODE_MIDI_EDIT_EFFECTS_DUMP_REQ;
    }

    send(sysexRequestDump.toSysexMessage(getChannel(),
					 new NameValue("opcode", opcode),
					 new NameValue("patchNum", location)));
  }

  /**
   * Sends a patch to the synth's program effects edit buffer.
   * @param p the patch to send to the edit buffer
   */
  public void sendPatch (Patch p)
  {
    storePatch(p, 0, QSConstants.MAX_LOCATION_PROG + 1);
  }
  
  /**
   * Sends a patch to a set location on a synth.  See comment for requestPatchDump for
   * explanation of patch numbers > 127.  We save the old values, then set the
   * opcode and target location, then send it, then restore the old values
   * @param p the patch to send
   * @param bankNum ignored - you can only send to the User bank on Alesis QS synths
   * @param patchNum the patch number to send it to
   */
  public void storePatch (Patch p, int bankNum, int patchNum)
  {
    // default to simple case - set specified patch in the User bank
    int location = patchNum;
    byte opcode = QSConstants.OPCODE_MIDI_USER_EFFECTS_DUMP;
    byte oldOpcode = p.sysex[QSConstants.POSITION_OPCODE];
    byte oldLocation = p.sysex[QSConstants.POSITION_LOCATION];

    // if the patch number is > max location, get from Edit buffers
    if (location>QSConstants.MAX_LOCATION_PROG)
    {
      location -= (QSConstants.MAX_LOCATION_PROG + 1);
      opcode = QSConstants.OPCODE_MIDI_EDIT_EFFECTS_DUMP;
    }
    // set the opcode and target location
    p.sysex[QSConstants.POSITION_OPCODE] = opcode;
    p.sysex[QSConstants.POSITION_LOCATION] = (byte)location;

    ErrorMsg.reportStatus("foo", p.sysex);
    //setBankNum (bankNum);
    //setPatchNum (patchNum);

    // actually send the patch
    sendPatchWorker (p);

    // restore the old values
    p.sysex[QSConstants.POSITION_OPCODE] = oldOpcode;
    p.sysex[QSConstants.POSITION_LOCATION] = oldLocation;
  }
}
