/*
 * OberheimMatrixDevice.java
 *
 * Created on 10. Oktober 2001, 21:39
 */

package synthdrivers.OberheimMatrix;

import core.*;
/**
 *
 * @author  Gerrit Gehnen
 * @version 0.1
 */
public class OberheimMatrix6Device extends Device
{
    
    /** Creates new OberheimMatrixDevice */
    public OberheimMatrix6Device ()
    {
        manufacturerName="Oberheim";
        modelName="Matrix 6/6R";
        synthName="Matrix 6";
        inquiryID="F07E**06021006000200*********F7";
        addDriver ( new OberheimMatrixSingleDriver ());
    }
    
}
