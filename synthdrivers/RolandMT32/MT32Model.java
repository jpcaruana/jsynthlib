/*
 * Copyright 2004,2005 Fred Jan Kraan
 *
 * This file is part of JSynthLib.
 *
 * JSynthLib is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
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

/**
 * MT32 Model for Roland MT32.
 *
 * @version $Id$
 */

package synthdrivers.RolandMT32;
import org.jsynthlib.core.ParamModel;
import org.jsynthlib.core.Patch;

import core.*;

class MT32Model extends ParamModel {
	    private int bitmask;
	    private int mult;

	    public MT32Model(Patch p, int offset) {
	        super(p, offset + 8);
	        bitmask = 255;
	        mult = 1;
	    }

	    public MT32Model(Patch p, int offset, int b) {
	        super(p, offset + 8);	        
		ofs = offset + 8; 
		patch = p; 
		bitmask = b;
		if ((bitmask & 1) == 1) mult = 1;
		else if ((bitmask & 2) == 2) mult = 2;
		else if ((bitmask & 4) == 4) mult = 4;
		else if ((bitmask & 8) == 8) mult = 8;
		else if ((bitmask & 16) == 16) mult = 16;
		else if ((bitmask & 32) == 32) mult = 32;
		else if ((bitmask & 64) == 64) mult = 64;
		else if ((bitmask & 128) == 128) mult = 128;
        //System.out.println("offset: " + offset + "  b: " + b);
	    }

	    public void set(int i) {
		patch.sysex[ofs] = (byte) i;
	    }

	    public int get() {
		return patch.sysex[ofs];
	    }
	}
