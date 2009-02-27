package synthdrivers.YamahaMotif;
import core.*;

import java.io.*;

import org.jsynthlib.core.Driver;
import org.jsynthlib.core.ErrorMsg;
import org.jsynthlib.core.Patch;
import org.jsynthlib.core.SysexHandler;

/** Base driver for Yamaha Motif voices */
public abstract class YamahaMotifSingleDriver extends Driver
{

  String base_address;
  String edit_buffer_base_address;
  String parameter_base_address;
  String defaults_filename = null;

  public YamahaMotifSingleDriver() {
    super("Single", "Rib Rdb");
  }
  public YamahaMotifSingleDriver(String patchType, String authors) {
    super(patchType, authors);
  }

  protected void yamaha_init() {
    /*manufacturer="Yamaha";
    model="Motif 6/7/8";
    id="Motif";*/
    sysexID="F0430*6B00000E"+base_address+"**";
    //inquiryID="F07E7F06024300417*040000007FF7";
    sysexRequestDump=new SysexHandler("F0 43 @@ 6B 0E " + base_address+ " ** F7");

    for (int i=0;i<patchNumbers.length; i++) {
      StringBuffer sb = new StringBuffer(4);
      sb.append( (char)('A' + i/16 ) );
      sb.append( '-' );
      sb.append( (i % 16) + 1);
      patchNumbers[i]=new String(sb);
    }
  }

  public String getPatchName (Patch ip) {
    int address = Byte.parseByte(parameter_base_address, 16);
    address = (address << 16) | 0x007000;
    int offset=YamahaMotifSysexUtility.findBaseAddressOffset(((Patch)ip).sysex, address);
    try {
      return new String(((Patch)ip).sysex, offset + YamahaMotifSysexUtility.DATA_OFFSET,
			10, "US-ASCII");
    } catch (UnsupportedEncodingException e) {
      return "-";
    }
  }
  public void setPatchName (Patch p, String name) {
    byte[] namebytes;
    int address = Byte.parseByte(parameter_base_address, 16);
    address = (address << 16) | 0x007000;
    int offset=YamahaMotifSysexUtility.findBaseAddressOffset(((Patch)p).sysex, address);
    try {
      namebytes=name.getBytes ("US-ASCII");
      for (int i = 0; i < 10; i++) {
	if (i >= namebytes.length)
	  ((Patch)p).sysex[offset + i + YamahaMotifSysexUtility.DATA_OFFSET] =(byte)' ';
	else
	  ((Patch)p).sysex[offset+ i+ YamahaMotifSysexUtility.DATA_OFFSET]=namebytes[i];
      }
      YamahaMotifSysexUtility.checksum(((Patch)p).sysex, offset);
    } catch (UnsupportedEncodingException e) { return; }
  }

  public void storePatch (Patch p, int bankNum,int patchNum) {
    sendPatchWorker ((Patch)p, patchNum);
  }

  /** Send to edit buffer */
  protected void sendPatch (Patch p) {
    sendPatchWorker((Patch)p, -1);
  }
  /**Does the actual work to send a patch to the synth*/
  protected void sendPatchWorker(Patch p, int patchnum) {
    // Fix the header/footer
    for (int offset = 0;
	 offset <= p.sysex.length - YamahaMotifSysexUtility.SYSEX_OVERHEAD;
	 offset += p.sysex.length - YamahaMotifSysexUtility.SYSEX_OVERHEAD) {
      p.sysex[offset + YamahaMotifSysexUtility.ADDRESS_OFFSET + 1] =
	(patchnum == -1) ? Byte.parseByte(edit_buffer_base_address, 16) :
	Byte.parseByte(base_address, 16);
      // If sending to edit buffer (patchnum is -1) patch needs
      // to be set to 0.
      p.sysex[offset + YamahaMotifSysexUtility.ADDRESS_OFFSET + 2] =
	(byte) ((patchnum == -1) ? 0 : ( patchnum & 128 ));
      YamahaMotifSysexUtility.checksum(p.sysex, offset);
    }
    // Send each message separately so it doesn't get screwed up by the
    // midi wrapper.
    YamahaMotifSysexUtility.splitAndSendBulk( p.sysex, this,
					      getChannel() - 1 );
    // Put header back so that it will be recognized.
    p.sysex[YamahaMotifSysexUtility.ADDRESS_OFFSET + 1] =
      Byte.parseByte(base_address, 16);
  }

  public void calculateChecksum (Patch p) {
    YamahaMotifSysexUtility.checksum( ((Patch)p).sysex );
  }

  public void requestPatchDump(int bankNum, int patchNum) {
    send(sysexRequestDump.toSysexMessage((getChannel()+32), patchNum));
  }
  // Stolen from the YamahaFS1RVoiceEditor
  // I probably should use some other method to do this, but I'm lazy.
  public Patch createNewPatch() { 
    try {
      InputStream fileIn= getClass().getResourceAsStream(defaults_filename);
      byte [] buffer =new byte [patchSize];
      fileIn.read(buffer);
      fileIn.close();
      return new Patch(buffer, this);
    }catch (Exception e) {ErrorMsg.reportError("Error","Unable to find " + defaults_filename,e);return null;}
  }

}

