/*
 * AlesisQSDevice.java
 *
 * Synth driver for Alesis QS series synths
 * Feb 2002
 * Chris Halls <chris.halls@nikocity.de>
 * GPL v2
 */

package synthdrivers.AlesisQS;

import core.*;
/**
 *
 * @author  Chris Halls
 * @version 0.1
 */
public class AlesisQSDevice extends Device
{

    /** Creates new QSDevice */
    public AlesisQSDevice ()
    {
        inquiryID="F07E7F060200000E0E000*00********f7";
        authors="Chris Halls";
        manufacturerName="Alesis";
        modelName="QS7/QS8/QSR";
        setSynthName("QS");
        addDriver (new AlesisQSProgramDriver ());
        addDriver (new AlesisQSMixDriver ());
        addDriver (new AlesisQSEffectsDriver ());
        addDriver (new AlesisQSGlobalDriver ());
    }

}
