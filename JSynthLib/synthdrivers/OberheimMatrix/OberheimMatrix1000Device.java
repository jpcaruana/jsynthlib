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
    private static final String infoText="JSynthLib supports all librarian and editing functions on both Matrix 1000 Single Patches and "+
	          "on Banks of patches.\n"+	       
	         "The Oberheim Matrix 1000 responds slowly to changes of certain parameters "+
	         "such as the Modulation Matrix. This is a limitation of the synthesizer "+
		 "and not of JSynthLib. Luckily, the slow response parameters tend not to be "+
		 "the ones you would usually want to tweak in real time\n";
    
    /** Creates new OberheimMatrixDevice */
    public OberheimMatrix1000Device ()
    {
	super ("Oberheim","Matrix 1000","F07E**06021006000200********F7",infoText,"Brian Klock");
        addDriver ( new OberheimMatrixBankDriver ());
        addDriver ( new OberheimMatrixSingleDriver ());
	
    }
    
}
