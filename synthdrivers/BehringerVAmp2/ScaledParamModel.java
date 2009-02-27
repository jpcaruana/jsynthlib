/*
 * Copyright 2005 Jeff Weber
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

package synthdrivers.BehringerVAmp2;

import org.jsynthlib.core.ParamModel;
import org.jsynthlib.core.Patch;

import core.*;

/** Scalable ParamModel--to allow different max values for CC and Sysex.
 *
 * @author Jeff Weber
 */
class ScaledParamModel extends ParamModel {
    /** The maximum CC value that will be used for the control. This is used
     * when calculating a scaling factor. The scaling factor is calculated as 
     * maxSysex / maxCC.
     */
    private int maxCC;

    /** The maximum sysex value that will be used for the control. This is used
     * when calculating a scaling factor. The scaling factor is calculated as 
     * maxSysex / maxCC.
     */
    private int maxSysex;

    /** Reverses the range of values sent by the sysexWidget over the range
     * 127 to 0. A value of true causes the range to be reversed.
     */
    private boolean reverse = false;

    /** Constructs a ScaledParamModel. Patch p is the reference to the patch
     * containing the sysex record. int o is the offset into the sysex record
     * in non-nibblized bytes, not including the header bytes. 
     * @param p
     *          The patch to be edited.
     * @param o
     *          The offset into the patch to be edited.
     */
    ScaledParamModel(Patch p, int o) {
        this(p, o, 1, 1);
    }

    /** Constructs a ScaledParamModel. Patch p is the reference to the patch
     * containing the sysex record. int o is the offset into the sysex record
     * in non-nibblized bytes, not including the header bytes. int maxCC is
     * the maximum CC value that will be sent by the control. int maxSysex
     * is the maximum sysex value represented the control in the sysex record.
     * @param p
     *          The patch to be edited.
     * @param o
     *          The offset into the patch to be edited.
     * @param maxCC
     *          The maximum CC value that will be used for this parameter.
     * @param maxSysex
     *          The maximum sysex value that will be used for this parameter.
     */
    ScaledParamModel(Patch p, int o, int maxCC, int maxSysex) {
        this(p, o, maxCC, maxSysex, false);
    }

    /** Constructs a ScaledParamModel. Patch p is the reference to the patch
     * containing the sysex record. int o is the offset into the sysex record
     * in non-nibblized bytes, not including the header bytes. int maxCC is
     * the maximum CC value that will be sent by the control. int maxSysex
     * is the maximum sysex value represented the control in the sysex record.
     * boolean reverse reverses the range of values sent by the sysexWidget
     * over the range 127 to 0. A value of true causes the range to be reversed.
     * @param p
     *          The patch to be edited.
     * @param o
     *          The offset into the patch to be edited.
     * @param maxCC
     *          The maximum CC value that will be used for this parameter.
     * @param maxSysex
     *          The maximum sysex value that will be used for this parameter.
     * @param reverse
     *          A boolean value representing whether the values are reversed
     *          across the range of the parameter (eg. 127 to 0. A value of true
     *          indicates the values will be reversed.
     */
    ScaledParamModel(Patch p, int o, int maxCC, int maxSysex, boolean reverse) {
        super(p, o);
        this.maxCC = maxCC;
        this.maxSysex = maxSysex;
        this.reverse = reverse;
    }

    /** Scales the value of i according maxSysex and sets the sysex value at
     * offset ofs to the result. Also reverses the value over the range if
     * reverse is true.
     * @param i
     *          The current value of the parameter.
     */
public void set(int i) {
        if (reverse) {
            i = (maxSysex - i);
        }
        patch.sysex[ofs] = (byte) (i * maxSysex / maxCC);
    }

    /** Returns the value at offset ofs, scaled according to maxCC and maxSysex.
     *  Also reverses the value over the range if reverse is true.
      * @return
     *            The value of this ParamModel.
    */
    public int get() {
        int returnValue = (int) patch.sysex[ofs] * maxCC / maxSysex;
        return returnValue;
    }
}