/*
 * OberheimMatrixDevice.java
 *
 * Created on 10. Oktober 2001, 21:39
 */

package synthdrivers.OberheimMatrix;

import core.*;
/**
 *
 * @author Brian Klock 
 * @version $Id$
 */
public class OberheimMatrix1000Device extends Device
{
    private static final String infoText
    = "JSynthLib supports all librarian and editing functions on both Matrix 1000"
    + "Single Patches and on Banks of patches.\n"
    + "The Oberheim Matrix 1000 responds slowly to changes of certain parameters\n"
    + "such as the Modulation Matrix. This is a limitation of the synthesizer\n"
    + "and not of JSynthLib. Luckily, the slow response parameters tend not to be\n"
    + "the ones you would usually want to tweak in real time.";
    
    /** Creates new OberheimMatrixDevice */
    public OberheimMatrix1000Device ()
    {
	super ("Oberheim","Matrix 1000","F07E**06021006000200********F7",infoText,"Brian Klock");
        addDriver ( new OberheimMatrixBankDriver ());
        addDriver ( new OberheimMatrixSingleDriver ());
	
    }
    
}
