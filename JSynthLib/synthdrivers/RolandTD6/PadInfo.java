/*
 * Copyright 2003 Hiroo Hayashi
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

package synthdrivers.RolandTD6;

import javax.swing.JButton;

/**
 * PadInfo.java
 *
 * Pad Attribute Data Structure for Roland TD6 Percussion Module.
 *
 * Created: Tue Jun 10 22:56:06 2003
 *
 * @author <a href="mailto:hiroo.hayashi@computer.org">Hiroo Hayashi</a>
 * @version $Id$
 */
// deviceclass.newInstance() in core/DeviceListWriter fails without
// `public'.  Why???!!!
public final class PadInfo {
    /**
     * Name of Pad. "Kick", "Ride", etc.
     */
    final String name;
    /**
     * Pad Number for SysEX message.
     */
    final int offset;
    /**
     *  dual trigger pad or not.
     *  (Snare, Tom1, Hi-Hat, Crash1, Crash2, Ride only)
     */
    final boolean dualTrigger;
    /**
     *  Dual trigger pad or not.
     */
    boolean dualTriggerActive;
    /**
     *  head or rim (only for dual pad)
     */
    boolean rim = false;		// default false
    /**
     * Enable or not.  Disable a pad which is not connected.
     */
    boolean padActive;
    /**
     * JButton for head
     */
    JButton buttonHead = null;
    /**
     * JButton for rim
     */
    JButton buttonRim = null;

    /**
     * Creates a new <code>PadInfo</code> instance.
     *
     * @param name a <code>String</code> value.
     * @param offset an <code>int</code> value.
     * @param dualTrigger a <code>boolean</code> value.
     * @param dualTriggerActive a <code>boolean</code> value.
     * @param padActive a <code>boolean</code> value
     */
    public PadInfo(String name, int offset,
		   boolean dualTrigger, boolean dualTriggerActive,
		   boolean padActive) {
	this.name = name;
	this.offset = offset;
	this.dualTrigger = dualTrigger;
	this.dualTriggerActive = dualTriggerActive;
	this.padActive = padActive;
    } // PadInfo constructor

    /**
     * @return a name field value.  For dual trigger pad " [head]" or "
     * [Rim]" is appended.
     */
    /*
    public String toString() {
	return (this.dualTrigger
		? (this.name + (this.rim ? " [Rim]" : " [Head]"))
		: this.name);
    }

    // clone() must be defined since super.clone() is a protected method.
    public Object clone () {
	try {
	    return super.clone ();
	} catch (CloneNotSupportedException e) {
	    throw new InternalError(e.toString());
	}
    }

    // only for test
    public static void main(String[] args) {
	PadInfo p0 = new PadInfo("P0", 0, true, true, true, true);
	PadInfo p1 = (PadInfo) p0.clone();
	PadInfo p2 = new PadInfo("P2", 1, true, true, true, true);
	p2 = (PadInfo) p0.clone();
	System.out.println(p2);
    }
    */
} // PadInfo
