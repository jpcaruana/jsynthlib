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

package org.jsynthlib.drivers.yamaha.ub99;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public abstract class YamahaUB99Util {
    // don't have to call constructor for Utility class.
    private YamahaUB99Util() {
    }

    //Generate a string array of int values within a range
    public static String[] genString(int ifrom, int ito) {
        int from, to;
        if (ifrom > ito) {
            from = ito;
            to = ifrom;
        } else {
            from = ifrom;
            to = ito;
        }
        String[] s = new String[(to-from)+1];
        for(int i = 0; i <= (to-from); i++) {
            s[i] = "" + (from + i);
        }
        return s;
    }

    //Generate a string array of int values within a range
    public static String[] genString(int ifrom, int ito, String unit) {
        int from, to;
        if (ifrom > ito) {
            from = ito;
            to = ifrom;
        } else {
            from = ifrom;
            to = ito;
        }
        String[] s = new String[(to-from)+1];
        for(int i = 0; i <= (to-from); i++) {
            s[i] = "" + (from + i) + unit;
        }
        return s;
    }

    //Generate a string array of int values within a range and using a step value
    public static String[] genString(int from, int to, int step) {
        int l = ((to-from)/step)+1;
        String[] s = new String[l];
        for(int i = 0; i < l; i++) {
            s[i] = "" + (from + i*step);
        }
        return s;
    }

    //Generate a string array of double values within a range,
    //using a step value and a format string
    public static String[] genString(double from, double to, double step, String fs) {
        DecimalFormat df = new DecimalFormat(fs);
        BigDecimal bd = new BigDecimal(((to-from)/step)+1);
        bd = bd.setScale(0,BigDecimal.ROUND_HALF_UP);
        int l = bd.intValue();
        String[] s = new String[l];
        for(int i = 0; i < l; i++) {
            s[i] = df.format(from + (i*step));
        }
        return s;
    }

}
