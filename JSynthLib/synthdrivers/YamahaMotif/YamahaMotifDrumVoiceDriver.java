package synthdrivers.YamahaMotif;
import core.*;

/** Driver for Yamaha Motif Normal Voices */
public class YamahaMotifDrumVoiceDriver extends YamahaMotifSingleDriver
{

   public YamahaMotifDrumVoiceDriver() {
     authors = "Rib Rdb (ribrdb at yahoo.com)";
     bankNumbers =new String[] {"User","User"};
     patchNumbers = new String[16];
     defaults_filename = "drum.syx";
     patchType="Drum Voice";
     base_address = "28";
     edit_buffer_base_address = "2F";
     parameter_base_address = "46";
     numSysexMsgs = 375;
     patchSize = 7988;
     trimSize = 7988;
     yamaha_init();
   }
  public Patch createNewPatch() { return super.createNewPatch(); }

}
