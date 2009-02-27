/*
 * Copyright 2005 Ton Holsink
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

package synthdrivers.TCElectronicGMajor;

import org.jsynthlib.core.ParamModel;
import org.jsynthlib.core.Patch;

class TCModel extends ParamModel {
    private int delta;

    public TCModel(Patch p, int offset) {
        super(p, offset);
        delta = 0;
    }

    public TCModel(Patch p, int offset, int idelta) {
        super(p, offset);
        delta = idelta;
    }

    public void set(int i) {
        TCElectronicGMajorUtil.setValue(patch.sysex, i + delta, ofs);
    }

    public int get() {
        return TCElectronicGMajorUtil.getValue(patch.sysex, ofs) - delta;
    }

}