//
//  NullModel.java
//  JSynthLib
//
//  Created by Jeff Weber on 9/6/04.
//  Copyright 2004 __MyCompanyName__. All rights reserved.
//

package synthdrivers.Line6BassPod;

import core.*;

/** Dummy do-nothing model. This model is used for the Global Wah on/off setting.
Pod supports a CC number for Wah on off but it's not represented in the Sysex record.*/
class NullModel extends ParamModel {
    public NullModel() {} 
    public void set(int i) {}
    public int get() {return 0;}
}
