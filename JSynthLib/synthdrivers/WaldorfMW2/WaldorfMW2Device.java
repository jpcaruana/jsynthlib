// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package synthdrivers.WaldorfMW2;

import core.Device;
import java.util.prefs.Preferences;

// Referenced classes of package synthdrivers.WaldorfMW2:
//            WaldorfMW2SingleDriver

public class WaldorfMW2Device extends Device
{

    public WaldorfMW2Device()
    {
        super("Waldorf", "Microwave 2/XT/XTK", "F07E06023E0E00............F7", "Microwave 2 / XT / XTK", "Joachim Backhaus");
    }

    public WaldorfMW2Device(Preferences preferences)
    {
        this();
        prefs = preferences;
        addDriver(new WaldorfMW2SingleDriver());
    }

    private static final String INFO_TEXT = "Microwave 2 / XT / XTK";
}
