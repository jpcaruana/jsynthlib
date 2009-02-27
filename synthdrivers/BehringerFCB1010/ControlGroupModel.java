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

import core.*;
import java.awt.event.*;

import org.jsynthlib.core.CheckBoxWidget;
import org.jsynthlib.core.ComboBoxWidget;

/** Class ControlGroupModel associates a group of ComboBoxWidgets to a single
* CheckBoxWidget and handles enabling/disabling the state of all of the ComboBoxWidgets
* in the group based on the state of the associated CheckBoxWidget.
*
* @author Jeff Weber
*/
class ControlGroupModel implements ItemListener {
    /***/
    private ComboBoxWidget[] cbWidget;

    /** Constructs a ControlGroupModel given a single CheckBoxWidget and list of
    * ComboBoxWidgets.*/
    ControlGroupModel(CheckBoxWidget onOffWidget, ComboBoxWidget[] cbWidget) {
        this.cbWidget = cbWidget;
        onOffWidget.addEventListener(this);
        setEnabled(onOffWidget.getValue() == 1);
    }
    
    /** Detects the state of the CheckBoxWidget and calls the ControlGroupModel.setEnabled
        * method.*/
    public void itemStateChanged(ItemEvent e) {
        this.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
    }
    
    /** Sets all of the ComboBoxWidgets in the group to enabled or disabled based
        * on the value of boolean enabled.
        */
    private void setEnabled(boolean enabled) {
        for (int i = 0; i < cbWidget.length; i++) {
            cbWidget[i].setEnabled(enabled);
        }
    }
}