/*
 * Copyright 2004 Jeff Weber
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

package synthdrivers.AlesisDM5;

import core.*;

/** DM5 ComboBoxWidget. Adds functionality to the standard JSynthLib ComboBoxWidget
to allow dynamic updating of the options list, the control value, and the 
associated NRPNSender based on the associated NRPNModel. 
* @author Jeff Weber
*/
class DM5ComboBoxWidget extends ComboBoxWidget {
    IParamModel UCBWModel;
    NRPNSender UCBWSender;
    
    /** Constructs a new DM5ComboBoxWidget given the standard parameters for a
        JSynthLib ComboBoxWidget.
        */
    DM5ComboBoxWidget(String label, IPatch patch,
                      IParamModel pmodel, ISender sender, Object[] options) {
        super(label, patch, 0, pmodel, sender, options);
        cb.setPrototypeDisplayValue("MMMMMMMMMMMMMM");
        cb.setMaximumRowCount(30);
        UCBWModel = pmodel;
        UCBWSender = (NRPNSender)sender;
    }
    
    /** Updates the comboBox list with the list of objects (Strings) given by 
        options and updates the value of the associated NRPNSender based on the
        current value of the associated model.
        */
    void updateComboBoxWidgetList(Object[] options) {
        int newValue = UCBWModel.get();
        int newMax = options.length - 1;
        
        UCBWSender.setMax(newMax);
        
        cb.removeAllItems();
        for (int i = 0; i < options.length; i++) {
            cb.addItem(options[i]);
        }
        
        setMax(newMax);
        setValue(Math.min(newValue, newMax));
    }
}
