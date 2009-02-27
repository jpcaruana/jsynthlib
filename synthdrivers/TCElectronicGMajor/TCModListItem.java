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

public class TCModListItem {
    private int m_ofs;
    private String m_param;
    private int m_mod;
    private int m_min;
    private int m_mid;
    private int m_max;

    public TCModListItem(int ofs, String param, int mod, int min, int mid, int max) {
        m_ofs = ofs;
        m_param = param;
        m_mod = mod;
        m_min = min;
        m_mid = mid;
        m_max = max;
    }

    public String toString() {
        return m_param + "\t" + TCElectronicGMajorConst.modString[m_mod] + "\t" + m_min + "%" + "\t" + m_mid + "%" + "\t" + m_max + "%";
    }

    public int getOfs() {
        return m_ofs;
    }

    public String getParam() {
        return m_param;
    }

    public int getMod() {
        return m_mod;
    }

    public int getMin() {
        return m_min;
    }

    public int getMid() {
        return m_mid;
    }

    public int getMax() {
        return m_max;
    }

}
