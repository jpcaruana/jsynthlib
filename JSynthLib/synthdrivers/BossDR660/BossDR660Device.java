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
        addDriver (new BossDR660DrumkitDriver ());
    }
    
}
