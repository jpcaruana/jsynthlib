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
    private static final String INFO_TEXT
    = "Known issues: Pitchslider defaults to wrong position when starting editor.\n"
    + "Name of drums aren't displayed until user selects a new category.\n"
    + "Triggers are not implemented, and I probably never will. \n"
    + "Single drum editing is on todo-list";

    /** Creates new AlesisDMProDevice */
    public AlesisDMProDevice ()
    {
	super("Alesis", "DM Pro", "F07E7F060200000E19000000*F7",
	      INFO_TEXT, "Peter Hageus (peter.hageus@comhem.se)");
      	addDriver (0,new AlesisDMProDrumKitDriver());
        addDriver (1,new AlesisDMProEffectDriver());
    }
}
