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

/** Class ControlGroupModel used to enable disable the state of a group of
* ComboxBoxWidgets associated with a CheckBoxWidget.
*
* @author Jeff Weber
*/
class ControlGroupModel implements ItemListener {
    private ComboBoxWidget[] cbWidget;
    private ParamModel onOffPModel;
    
    ControlGroupModel(CheckBoxWidget onOffWidget, ComboBoxWidget[] cbWidget) {
        this.cbWidget = cbWidget;
        onOffWidget.addEventListener(this);
        setEnabled(onOffWidget.getValue() == 1);
    }
    
    public void itemStateChanged(ItemEvent e) {
        this.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
    }
    
    private void setEnabled(boolean enabled) {
        for (int i = 0; i < cbWidget.length; i++) {
            cbWidget[i].setEnabled(enabled);
        }
    }
}