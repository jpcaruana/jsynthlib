/*
 * YamahaMotifDevice.java
 *
 */

package org.jsynthlib.drivers.yamaha.motif;

import java.util.prefs.Preferences;

import org.jsynthlib.core.Device;
/**
 * @author  Ryan Brown
 * @version $Id$
 */
public class YamahaMotifDevice extends Device {

    /** Creates new MotifDevice */
    public YamahaMotifDevice() {
	super("Yamaha", "Motif", "F07E7F06024300417.040000007FF7",
	      "This version has librarian support for single voices (drum "+
	      "and normal), and some editor support for normal voices. "+
	      "There's a ton missing from the editor (control sets, lfo's, "+
	      "effects, and filters to name a few). Other than that, there "+
	      "are no known issues or problems with this synthesizer. Please "+
	      "let me (ribrdb@yahoo.com) know if you have any problems. For "+
	      "now it only supports the Motif 6, 7, and 8. I may add support "+
	      "for the S90, Rack, and ES series eventually. As soon as I can "+
	      "I will finish the editor support and and support for "+
	      "performances, masters, etc.",
	      "Rib Rdb");
    }

    /** Constructor for for actual work. */
    public YamahaMotifDevice(Preferences prefs) {
	this();
	this.prefs = prefs;

        //setSynthName("Motif");

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
