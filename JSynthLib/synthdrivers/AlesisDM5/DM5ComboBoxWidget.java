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

class DM5ComboBoxWidget extends ComboBoxWidget {
    IParamModel UCBWModel;
    NRPNSender UCBWSender;
    
    DM5ComboBoxWidget(String label, IPatch patch,
                      IParamModel pmodel, ISender sender, Object [] options) {
        super(label, patch, 0, pmodel, sender, options);
        cb.setPrototypeDisplayValue("MMMMMMMMMMMMMM");
        cb.setMaximumRowCount(30);
        UCBWModel = pmodel;
        UCBWSender = (NRPNSender)sender;
    }
    
    void updateComboBoxWidgetList(int selectedIndex) {
        int newValue = UCBWModel.get();
        int newMax = DM5SoundList.DRUM_NAME[selectedIndex].length - 1;
        
        UCBWSender.setMax(newMax);
        
        String[] list = DM5SoundList.DRUM_NAME[selectedIndex];
        
        cb.removeAllItems();
        for (int i = 0; i < list.length; i++) {
            cb.addItem(list[i]);
        }
        
        setMax(newMax);
        setValue(Math.min(newValue, newMax));
    }
}
