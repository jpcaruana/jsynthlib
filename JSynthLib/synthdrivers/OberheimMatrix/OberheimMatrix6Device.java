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
 * @version $Id$
 */
public class OberheimMatrix6Device extends Device
{
    private static final String infoText
    = "JSynthLib supports all librarian and editing functions on both Matrix 6/6r Single Patches, but\n"
    + "does not yet include support for manipulating banks of patches.\n"
    + "The Oberheim Matrix 6/6r responds slowly to changes of certain parameters\n"
    + "such as the Modulation Matrix. This is a limitation of the synthesizer\n"
    + "and not of JSynthLib. Luckily, the slow response parameters tend not to be\n"
    + "the ones you would usually want to tweak in real time";

    /** Creates new OberheimMatrixDevice */
    public OberheimMatrix6Device ()
    {
	super ("Oberheim","Matrix6/6R","F07E**06021006000200********F7",infoText,"Brian Klock");
        setSynthName("Matrix 6");
       addDriver ( new OberheimMatrixSingleDriver ());
    }
    
}
