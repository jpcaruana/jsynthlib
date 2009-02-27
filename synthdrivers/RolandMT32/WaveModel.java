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

/*
 * WaveModel.java for Roland MT-32
 * 
 * @version $Id$
 */

package synthdrivers.RolandMT32;
import core.Patch;
import core.SysexWidget;


class WaveModel implements SysexWidget.IParamModel {
    private Patch patch;
    private int source;

    public WaveModel(Patch p, int s) {
	patch = p; source = s;
    }

    public void set(int i) {
	patch.sysex[34 + 8 + source]
	    = (byte) ((patch.sysex[34 + 8 + source] & 254) + (byte) (i / 128));
	patch.sysex[38 + 8 + source] = (byte) (i % 128);
    }

    public int get() {
	return (((patch.sysex[34 + 8 + source] & 1) * 128)
		+ (patch.sysex[38 + 8 + source]));
    }
}
