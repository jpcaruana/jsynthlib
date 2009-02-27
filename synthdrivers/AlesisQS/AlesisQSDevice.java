/*
 * AlesisQSDevice.java
 *
 * Synth driver for Alesis QS series synths
 * Feb 2002
 * Chris Halls <halls@debian.org>
 * GPL v2
 */

package synthdrivers.AlesisQS;

import core.*;
import java.util.prefs.Preferences;

import org.jsynthlib.core.Device;
/**
 *
 * @author  Chris Halls
 * @version $Id$
 */
public class AlesisQSDevice extends Device
{

    /** Creates new QSDevice */
    public AlesisQSDevice ()
    {
        super("Alesis", "QS7/QS8/QSR", "F07E7F060200000E0E000.00........f7",
              "Librarian support is working.  Work on editors is in progress.",
              "Chris Halls/Zellyn Hunter");
    }

    /** Constructor for for actual work. */
    public AlesisQSDevice(Preferences prefs) {
	this();
	this.prefs = prefs;

        //setSynthName("QS");

        addDriver (new AlesisQSProgramDriver ());
        addDriver (new AlesisQSMixDriver ());
        addDriver (new AlesisQSEffectsDriver ());
        addDriver (new AlesisQSGlobalDriver ());
    }

}
