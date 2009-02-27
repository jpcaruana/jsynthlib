/*
 * Copyright 2006 Roger Westerlund
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
package synthdrivers.RolandD10;

import core.ParamModel;
import core.Patch;

/**
 * This class extends the ParamModel to relocate the offset specified into a
 * the sysex message offset.
 * 
 * @author Roger Westerlund
 */
public class D10ParamModel extends ParamModel {
    public D10ParamModel(Patch patch, int offset) {
        // Relocate the offset taking the header into account.
        super(patch, D10Constants.SIZE_HEADER_DT1 + offset);
    }
}
