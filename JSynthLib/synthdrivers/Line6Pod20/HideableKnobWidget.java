/*
 * Copyright 2004 Jeff Weber
 *
 * This file is part of JSynthLib.
 *
 * JSynthLib is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or(at your option) any later version.
 *
 * JSynthLib is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JSynthLib; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 */

package synthdrivers.Line6Pod20;

import core.*;

/** Hideable version of the KnobWidget*/
class HideableKnobWidget extends KnobWidget {
    
    HideableKnobWidget(String l, Patch p, int min, int max, int base, ParamModel ofs, SysexSender s) {
        super(l, p, min, max, base, ofs, s);
    }
    
    public void setVisible(boolean visibleFlag) {
        mKnob.setVisible(visibleFlag);
        this.getJLabel().setVisible(visibleFlag);
    }
}

