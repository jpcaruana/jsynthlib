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

package synthdrivers.YamahaUB99;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Vector;

import javax.swing.JComboBox;

import org.jsynthlib.core.IPatch;
import org.jsynthlib.core.SysexWidget;

public class IdComboWidget extends SysexWidget {
    protected JComboBox cb;
    /** A vector of the list of the options in the ComboBox. */
    protected Vector options;

    public IdComboWidget(String label, IPatch patch, IParamModel pmodel, ISender sender, Vector options) {
        super(label, patch, 0, Integer.MAX_VALUE, pmodel, sender);
        this.options = options;
        createWidgets();
        layoutWidgets();
    }

    protected void createWidgets() {
        cb = new JComboBox(options);
        int v = getValue();
        for (int i = 0; i < options.size(); i++)
            if (v == ((IdItem) options.elementAt(i)).getID()) {
                cb.setSelectedIndex(i);
                break;
            }

        cb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
            eventListener(e);
        }
        });
    }

    /** invoked when the an item is selected. */
    protected void eventListener(ItemEvent e) {
    if (e.getStateChange() == ItemEvent.SELECTED) {
        sendSysex(((IdItem)cb.getSelectedItem()).getID());
    }
    }

    /** Adds an <code>ActionListener</code> to the ComboBox. */
    public void addEventListener(ActionListener l) {
    cb.addActionListener(l);
    }

    /** Adds an <code>ItemListener</code> to the ComboBox. */
    public void addEventListener(ItemListener l) {
    cb.addItemListener(l);
    }

    protected void layoutWidgets() {
        setLayout(new FlowLayout());
        cb.setMaximumSize(new Dimension(125, 25));

        add(getJLabel());
        add(cb);
    }

    public void setValue(int v) {
    super.setValue(v);
    for (int i = 0; i < options.size(); i++)
        if (v == ((IdItem) options.elementAt(i)).getID()) {
            cb.setSelectedIndex(i);
            break;
        }
    }

    public void setEnabled(boolean e) {
        cb.setEnabled(e);
    }
}
