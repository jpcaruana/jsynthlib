//
//  HideableKnobWidget.java
//  JSynthLib
//
//  Created by Jeff Weber on 9/6/04.
//  Copyright 2004 __MyCompanyName__. All rights reserved.
//

package synthdrivers.Line6BassPod;

import core.*;

/** Hideable version of the KnobWidget*/
class HideableKnobWidget extends KnobWidget {
    
    public HideableKnobWidget(String l, Patch p, int min, int max, int base, ParamModel ofs, SysexSender s) {
        super(l, p, min, max, base, ofs, s);
    }
    
    public void setVisible(boolean visibleFlag) {
        mKnob.setVisible(visibleFlag);
        this.getJLabel().setVisible(visibleFlag);
    }
}

