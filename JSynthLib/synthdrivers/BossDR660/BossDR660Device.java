/*
 * BossDR660Device.java
 *
 * Created on 10. Oktober 2001, 09:25
 */

package synthdrivers.BossDR660;

import core.*;

/**
 *
 * @author  Gerrit Gehnen
 * @version 0.1
 */
public class BossDR660Device extends Device
{
    
    /** Creates new BossDR660Device */
    public BossDR660Device ()
    {
        manufacturerName="Boss";
        modelName="DR660";
        synthName="DR660";
	infoText="JSynthLib functions both as an editor and librarian for DR660 Drumkits. Banks of Drumkits are not "+
	         "supported due to Roland not documenting the format of the bank sysex dump. Also, while most parameters "+
		 "are editable, JSynthLib can not currently edit the effects settings of the DR660.\n\n"+
		 "Keep in mind that the first several locations to store drumkits on the DR660 are ROM"+
		 " locations and are not user writable. Though JSynthLib can store drumkits to these locations, "+
		 "they will revert back to their old values once a patch change message is received.";
        addDriver (new BossDR660DrumkitDriver ());
    }
    
}
