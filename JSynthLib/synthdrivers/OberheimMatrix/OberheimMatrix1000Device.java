/*
 * OberheimMatrixDevice.java
 *
 * Created on 10. Oktober 2001, 21:39
 */

package synthdrivers.OberheimMatrix;

import core.*;
/**
 *
 * @author  Administrator
 * @version
 */
public class OberheimMatrix1000Device extends Device
{
    
    /** Creates new OberheimMatrixDevice */
    public OberheimMatrix1000Device ()
    {
        manufacturerName="Oberheim";
        modelName="Matrix 1000";
        //patchType="Bank";
        synthName="Matrix 1000";
        
        inquiryID="F07E**06021006000200*********F7";
        addDriver ( new OberheimMatrixBankDriver ());
        addDriver ( new OberheimMatrixSingleDriver ());
        infoText="JSynthLib supports all librarian and editing functions on both Matrix 1000 Single Patches and "+
	          "on Banks of patches.\n"+	       
	         "The Oberheim Matrix 1000 responds slowly to changes of certain parameters "+
	         "such as the Modulation Matrix. This is a limitation of the synthesizer "+
		 "and not of JSynthLib. Luckily, the slow response parameters tend not to be "+
		 "the ones you would usually want to tweak in real time\n";
	
    }
    
}
