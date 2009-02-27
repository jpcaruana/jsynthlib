/*
 * NovationXioSynth.java
 *
 * Created on 10. Oktober 2001, 21:23
 */

package synthdrivers.NovationXioSynth;

import java.util.prefs.Preferences;

import org.jsynthlib.core.Device;
/**
 *
 * @author  Nicolas Boulicault
 * @version $Id: NovationXioSynth.java,v 1.0 2008/12/16 16:28:04 
 */

public class NovationXioSynthDevice extends Device
{
    private static final String infoText =
	"Novation XioSynth Driver\nArpegiator and X-Gator are not implemented,\nnor different midi channels";
    
    public NovationXioSynthDevice ()
    {
	super ("Novation","Xio",null,infoText,"Nicolas Boulicault");
    }

    public NovationXioSynthDevice(Preferences prefs) 
    {
	this();
	this.prefs = prefs;
	
	NovationXioSynthSingleDriver singleDriver = new NovationXioSynthSingleDriver();
	addDriver (new NovationXioSynthBankDriver (singleDriver));
	addDriver (singleDriver);
    }
}
