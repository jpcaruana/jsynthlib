/*
 * YamahaTX81zDevice.java
 *
 * Created on 10. Oktober 2001, 21:23
 */

package synthdrivers.YamahaTX81z;

import core.*;
/**
 *
 * @author  Administrator
 * @version
 */
public class YamahaTX81zDevice extends Device
{
    
    /** Creates new YamahaTX81zDevice */
    public YamahaTX81zDevice ()
    {
        manufacturerName="Yamaha";
        modelName="TX81z";
        synthName="TX81z";
        infoText="The Yamaha TX81z is susceptable to internal midi buffer overflow if you send it a lot of Data"+
	          "quickly. With JSynthLib, this can happenif you are using a fader box and throwing the faders"+
		  "around rapidly. Otherwise, it should not be a problem\n\n"+
		  "JSynthLib supports the TX81z as both a Single and Bank Librarian and also supports Patch Editing.";
        addDriver (new YamahaTX81zBankDriver ());
        addDriver (new YamahaTX81zSingleDriver ());
        
    }
    
}
