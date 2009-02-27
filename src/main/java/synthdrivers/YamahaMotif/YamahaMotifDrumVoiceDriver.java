package synthdrivers.YamahaMotif;
import org.jsynthlib.core.Patch;

/** Driver for Yamaha Motif Normal Voices */
public class YamahaMotifDrumVoiceDriver extends YamahaMotifSingleDriver
{

   public YamahaMotifDrumVoiceDriver() {
     super("Drum Voice", "Rib Rdb (ribrdb at yahoo.com)");
     bankNumbers =new String[] {"User","User"};
     patchNumbers = new String[16];
     defaults_filename = "drum.syx";
     base_address = "28";
     edit_buffer_base_address = "2F";
     parameter_base_address = "46";
     numSysexMsgs = 375;
     patchSize = 7988;
     trimSize = 7988;
     yamaha_init();
   }
  // The driver has to define this method for the option to be available.
  public Patch createNewPatch() { return super.createNewPatch(); }

}
