/*
 * AlesisDMProDevice.java
 *
 * Created on 25. December2001, 14:00
 */

package synthdrivers.AlesisDMPro;

import core.*;
/**
 *
 * @author  Peter Hageus
 * @version 1.0
 */
public class AlesisDMProDevice extends Device
{
 
    /** Creates new AlesisDMProDevice */
    public AlesisDMProDevice ()
    {
        inquiryID="F07E7F060200000E19000000*F7";
        manufacturerName="Alesis";
        modelName="DM Pro";
        setSynthName("DMPro");
        authors = "Peter Hageus (peter.hageus@comhem.se)";
        infoText="Known issues: Pitchslider defaults to wrong position when starting editor.\n";
	    infoText+="Name of drums aren't displayed until user selects a new category.\n";
		infoText+="Triggers are not implemented, and I probably never will. \n";
		infoText+="Single drum editing is on todo-list";
		 
      	addDriver (0,new AlesisDMProDrumKitDriver());
        addDriver (1,new AlesisDMProEffectDriver());
    }
}
