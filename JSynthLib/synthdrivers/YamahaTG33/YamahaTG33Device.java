/*
 * YamahaTG33Device.java
 *
 * Created on 10. Oktober 2001, 22:16
 */

package synthdrivers.YamahaTG33;

import core.*;
/**
 *
 * @author  Gerrit Gehnen
 * @version 0.1
 */
public class YamahaTG33Device extends Device
{
    
    /** Creates new YamahaTG33Device */
    public YamahaTG33Device ()
    {
        manufacturerName="Yamaha";
        modelName="TG33/SY22";
        synthName="TG33";
	infoText=" There are no known issues or problems with support for this synthesizer. Only Librarian support "+
	"is available. I am looking for information which would allow me to write a Single Editor. The information "+
	"available in the manuel and on the web is incomplete. If you can help, please email jsynthlib@overwhelmed.org";

        addDriver (new YamahaTG33BankDriver ());
        addDriver (new YamahaTG33SingleDriver ());
    }
    
}