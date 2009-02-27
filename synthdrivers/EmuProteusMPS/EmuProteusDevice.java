/*
 * EmuProteusDevice.java
 *
 * Created on 10. Oktober 2001, 22:04
 */

package synthdrivers.EmuProteusMPS;

import core.*;
import java.util.prefs.Preferences;

import org.jsynthlib.core.Device;
/**
 *
 * @author Brian Klock
 * @version $Id$
 */
public class EmuProteusDevice extends Device
{
    private static final String infoText="This synthesizer lacks a MIDI addressable patch buffer. Therefore, when you send or play a patch "+
             "from within JSynthLib, the patch at location 100 on the Proteus will be overwritten. JSynthLib "+
             "treats this location as an edit buffer.";

    /** Creates new EmuProteusDevice */
    public EmuProteusDevice () {
        super ("Emu","Proteus MPS","F07E..06021804040800........F7",infoText,"Brian Klock");
    }

    /** Constructor for for actual work. */
    public EmuProteusDevice(Preferences prefs) {
        this();
        this.prefs = prefs;

        //setSynthName("MPS");

        addDriver(new EmuProteusMPSBankDriver());
        addDriver(new EmuProteusMPSSingleDriver());
    }
}
