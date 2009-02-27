/*
 * BossDR660Device.java
 *
 * Created on 10. Oktober 2001, 09:25
 */

package synthdrivers.BossDR660;

import core.*;
import java.util.prefs.Preferences;

import org.jsynthlib.core.Device;

/**
 *
 * @author  Gerrit Gehnen
 * @version $Id$
 */
public class BossDR660Device extends Device
{
    private static final String	infoText="JSynthLib functions both as an editor and librarian for DR660 Drumkits. Banks of Drumkits are not "+
	         "supported due to Roland not documenting the format of the bank sysex dump. Also, while most parameters "+
		 "are editable, JSynthLib can not currently edit the effects settings of the DR660.\n\n"+
		 "Keep in mind that the first several locations to store drumkits on the DR660 are ROM"+
		 " locations and are not user writable. Though JSynthLib can store drumkits to these locations, "+
		 "they will revert back to their old values once a patch change message is received.";

    /** Creates new BossDR660Device */
    public BossDR660Device ()
    {
	super ("Boss","DR660",null,infoText,"Brian Klock");
    }

    /** Constructor for for actual work. */
    public BossDR660Device(Preferences prefs) {
	this();
	this.prefs = prefs;

        addDriver (new BossDR660DrumkitDriver ());
    }
}
