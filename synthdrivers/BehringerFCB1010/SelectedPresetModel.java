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

package synthdrivers.BehringerFCB1010;

import org.jsynthlib.core.SysexWidget;

import core.*;

/**
* Behringer FCB1010 Selected Preset Model - Handles updating all widgets on the
 * presets pane of the FCB1010 editor when the user changes the bank or present.
 * 
 * @author Jeff Weber
 */
final class SelectedPresetModel {
    /** Private array used to store references to all of the SysexWidgets used in 
    * the presets panel of the FCB1010Editor.
    */
    private static SysexWidget[] widget = new SysexWidget[28];
    
    /** Internal index used to indicate the next unused occurrence of the 
    * widget array.
    */
    private static int widgetIx = 0;
    
    /** Sets the widgetIx variable back to zero and sets all members of the
        * widget array to null. This is called by the FCB1010Editor each time
        * it is instantiated.
        */
    static void init () {
        widgetIx = 0;
        for (int i = 0; i < widget.length; i++) {
            widget[i] = null;
        }
    }
    
    /** Stores a reference to the supplied SysexWidget into the next available
        * occurrence of the widget array.
        */
    static void putWidget (SysexWidget xWidget) {
        widget[widgetIx++] = xWidget;
    }
    
    /** Sets the values of all the widgets on the presets panel of the FCB1010Editor
        * to the current values of the preset given by bank and preset.
        */
    static void setPreset(int bank, int preset) {
        FCB1010ParamModel.setPreset(bank, preset);
        for (int i = 0; i < widget.length; i++) {
            if (widget[i] != null) {
                widget[i].setValue();
            }
        }
    }
}
