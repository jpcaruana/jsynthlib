/*
 * YamahaMotifDevice.java
 *
 */

package synthdrivers.YamahaMotif;

import core.*;
/**
 * @author  Ryan Brown
 * @version $Id$
 */
public class YamahaMotifDevice extends Device {
    
    /** Creates new MotifDevice */
    public YamahaMotifDevice() {
        inquiryID="F07E7F06024300417*040000007FF7";
        manufacturerName="Yamaha";
        modelName="Motif";
        setSynthName("Motif");
        infoText="This version has librarian support for single voices (drum "+
	  "and normal), and some editor support for voices. There's a ton "+
	  "missing from the editor (control sets, lfo's, effects, and filters "+
	  "to name a few). Other than that, there are no known issues or "+
	  "problems with this synthesizer. Please let me (ribrdb@yahoo.com) "+
	  "know if you have any problems. For now it only supports the Motif "+
	  "6, 7, and 8. I may add support for the Motif Rack and Motif ES "+
	  "series if I feel like it. As soon as I can I will finish the "+
	  "editor support and and support for performances, masters, etc.";
        
	/*addDriver(0,new YamahaMotifBulkConverter());
        addDriver(new YamahaMotifSingleDriver());
        addDriver(new YamahaMotifBankDriver());
        addDriver(new YamahaMotifMultiDriver());
        addDriver(new YamahaMotifMultiBankDriver());
        addDriver(new YamahaMotifEffectDriver());
        addDriver(new YamahaMotifEffectBankDriver());
        addDriver(new YamahaMotifDrumsetDriver());*/
	addDriver(new YamahaMotifDrumVoiceDriver());
	addDriver(new YamahaMotifNormalVoiceDriver());
        
    }
    
}
