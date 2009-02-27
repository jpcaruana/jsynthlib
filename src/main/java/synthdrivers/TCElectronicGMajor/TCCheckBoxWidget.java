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

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;

import org.jsynthlib.core.CheckBoxWidget;
import org.jsynthlib.core.IPatch;

public class TCCheckBoxWidget extends CheckBoxWidget {

    public TCCheckBoxWidget(IPatch patch, IParameter param) {
        super(patch, param);
    }

    public TCCheckBoxWidget(String label, IPatch patch, IParamModel pmodel, ISender sender) {
        super(label, patch, pmodel, sender);
    }

    protected void createWidgets() {
    if (getValue() == 0)
        cb = new JCheckBox(getLabel(), true);
    else
        cb = new JCheckBox(getLabel(), false);
    cb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
            eventListener(e);
        }
        });
    }

    protected void eventListener(ItemEvent e) {
    if (e.getStateChange() == ItemEvent.SELECTED)
        sendSysex(0);
    else
        sendSysex(1);
    }

    public void setValue(int v) {
    super.setValue(v);
    cb.setSelected(v != 1);
    }

    public void setValue(boolean v) {
    super.setValue(v ? 0 : 1);
    cb.setSelected(v);
    }
}
