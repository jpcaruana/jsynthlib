/*
 * EnsoniqESQ1Device.java
 *
 * Created on 10. Oktober 2001, 22:07
 */

package synthdrivers.EnsoniqESQ1;

import core.*;
/**
 *
 * @author  Gerrit Gehnen
 * @version 0.1
 */
public class EnsoniqESQ1Device extends Device
{
    
    /** Creates new EnsoniqESQ1Device */
    public EnsoniqESQ1Device ()
    {
        manufacturerName="Ensoniq";
        modelName="ESQ-1";
        synthName="ESQ1";
        infoText="Whenever JSynthLib sends a Patch to the ESQ, the synthesizer will go to a screen where you must hit "+
	          "'exit' on the front panel before you can send another patch. This is kind of annoying, so if anyone "+
		  "knows a way around this let me know. The other ESQ librarians I checked out had the same problem, so "+
		   "it may be impossible";
     inquiryID="F07E**06020F0200*************F7";
        addDriver (new EnsoniqESQ1BankDriver ());
        addDriver (new EnsoniqESQ1SingleDriver ());
    }
    
}
