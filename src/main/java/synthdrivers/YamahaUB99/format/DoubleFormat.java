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

package synthdrivers.YamahaUB99.format;

import java.text.DecimalFormat;

public class DoubleFormat implements IFormat {
    private double start;
    private double step;
    private String fmt;

    public DoubleFormat(double start, double step, String fmt) {
        this.start = start;
        this.step = step;
        this.fmt = fmt;
    }

    public String fmtString(int v) {
        DecimalFormat df = new DecimalFormat(fmt);
        return df.format(start + (v*step));
    }
}
