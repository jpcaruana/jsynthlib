/*
 * KawaiK5000Device.java
 *
 * Created on 10. Oktober 2001, 21:46
 */

package org.jsynthlib.drivers.kawai.k5000;

import java.util.prefs.Preferences;

import org.jsynthlib.core.Device;

/**
 * @version $Id$
 */
public class KawaiK5000Device extends Device
{
     private static final String infoText="These Drivers should work with the Kawai K5000s, K5000r, and K5000w, though note that the 'B' bank PCM "+
	          "singles on the K5000w are not supported, only the additive type 'A' and 'D' bank type is supported. "+
                  "In addition, JSynthLib's librarian functions will also extend to K5k Combi patches. Only Librarian features "+
		  "are supported for the K5k so far, there is no ability to edit patches.\n"+
		  "The 'crossbreed' option does not work with the singles for this synth. Though you may occassionally get "+
		  "a good result, most of the time, you will get nothing at all because of the dynamic sizing of K5k Patches."+
		  "Also, note that changes made to patches in the K5000 may be lost when you turn it off, unless you perform the "+
		  "backup to non-volitile RAM from the front panel.\n\n"+
		  "Note that when sending single patches to the K5k, , Patch A01 will be used as the midi edit buffer since the K5k "+
		  "does not provide a MIDI accessable edit buffer.";

    /** Creates new KawaiK5000Device */
    public KawaiK5000Device ()
    {
	super ("Kawai","K5000","F07E..06024000000A..........F7",infoText,"Brian Klock & Phil Shepherd");
    }

    /** Constructor for for actual work. */
    public KawaiK5000Device(Preferences prefs) {
	this();
	this.prefs = prefs;

        //setSynthName("K5k");

        addDriver (new KawaiK5000BankDriver ());
        addDriver (new KawaiK5000ADDSingleDriver ());
        addDriver (new KawaiK5000CombiDriver ());
        addDriver (new KawaiK5000CombiBankDriver ());


    }
}
