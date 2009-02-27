/*
 * YamahaTX81zDevice.java
 *
 * Created on 10. Oktober 2001, 21:23
 */

package synthdrivers.YamahaTX81z;

import core.*;
import java.util.prefs.Preferences;

import org.jsynthlib.core.Device;
/**
 *
 * @author  Brian Klock
 * @version $Id$
 */
public class YamahaTX81zDevice extends Device
{
    private static final String infoText =
    "The Yamaha TX81z is susceptable to internal MIDI buffer overflow if you send it a lot of Data quickly. "
    + "With JSynthLib, this can happen if you are using a fader box and throwing the faders around rapidly. "
    + "Otherwise, it should not be a problem\n\n"
    + "JSynthLib supports the TX81z as both a Single and Bank Librarian and also supports Patch Editing.";

    /** Creates new YamahaTX81zDevice */
    public YamahaTX81zDevice ()
    {
	super ("Yamaha","TX81z",null,infoText,"Brian Klock");
    }

    /** Constructor for for actual work. */
    public YamahaTX81zDevice(Preferences prefs) {
	this();
	this.prefs = prefs;

        addDriver (new YamahaTX81zBankDriver ());
        addDriver (new YamahaTX81zSingleDriver ());
    }
}
