/*
 * 
 * KorgER1Device.java
 *
 * Created on 10. Oktober 2001, 22:18
 */

package synthdrivers.KorgER1;

import core.Device;
import java.util.prefs.Preferences;
/**
 *
 * @author Gerrit Gehnen
 * @version $Id$
 */
public class KorgER1Device extends Device
{
    /** Creates new KorgER1Device */
    public KorgER1Device ()
    {
	super ("Korg","Electribe ER1","F07E..06024251..............F7",null,"Yves Lefebvre");
    }

    /** Constructor for for actual work. */
    public KorgER1Device(Preferences prefs) {
	this();
	this.prefs = prefs;

        //setSynthName("ER1");
        addDriver (new KorgER1SingleDriver ());
    }
}
