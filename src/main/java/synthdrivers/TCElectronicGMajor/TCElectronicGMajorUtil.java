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

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class TCElectronicGMajorUtil {
    // don't have to call constructor for Utility class.
    private TCElectronicGMajorUtil() {
    }

    private static final int calcPatchNumHelper(int bankNum,int patchNum) {
        return bankNum*TCElectronicGMajorConst.NUM_PATCH+patchNum+1;
    }

    public static final int calcPatchNum(int bankNum,int patchNum) {
        return calcPatchNumHelper(bankNum, patchNum) / 0x80;
    }

    public static final int calcBankNum(int bankNum,int patchNum) {
        return calcPatchNumHelper(bankNum, patchNum) % 0x80;
    }

    //Calculate a checksum
    public static byte calcChecksum(byte[] b) {
        return calcChecksum(b,
                    TCElectronicGMajorConst.CHECKSUMSTART,
                    TCElectronicGMajorConst.CHECKSUMEND);
    }

    //Calculate a checksum
    public static byte calcChecksum(byte[] b, int start, int end) {
        int sum=0;
        for (int i=start;i<=end;i++) {
            sum+=b[i];
        }
        return (byte) (sum & 127);
    }

    public static void setValue(byte[] b, int value, int ofs) {
        int j = value;
        b[ofs] = (byte)(j & 127);
        j = (j >> 7);
        b[ofs+1] = (byte)(j & 127);
        b[ofs+2] = (byte)((j >> 7) & 127);
        j = (j >> 7);
        b[ofs+3] = (byte)((j >> 7) & 7);
    }

    public static int getValue(byte[] sysex, int ofs) {
        int value;

        value = sysex[ofs+3];
        value = (value << 7);
        value = ((value ^ sysex[ofs+2]) << 7);
        value = ((value ^ sysex[ofs+1]) << 7);
        value = (value ^ sysex[ofs]);

        if ((value & 0x10000) == 0x10000) {
            value = value + 0xFF000000;
        }

        return value;
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
        System.out.println("" + l);
        String[] s = new String[l];
        for(int i = 0; i < l; i++) {
            s[i] = df.format(from + (i*step));
        }
        return s;
    }

    /**
     * Place a JDialog window to the center of computer screen.
     */
    public static void centerDialog (javax.swing.JDialog dialog) {
        java.awt.Dimension screenSize = dialog.getToolkit().getScreenSize();
        java.awt.Dimension size = dialog.getSize();
        screenSize.height = screenSize.height / 2;
        screenSize.width = screenSize.width / 2;
        size.height = size.height / 2;
        size.width = size.width / 2;
        int y = screenSize.height - size.height;
        int x = screenSize.width - size.width;
        dialog.setLocation(x, y);
    }

}
