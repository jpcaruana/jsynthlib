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
        
    }
    
}
