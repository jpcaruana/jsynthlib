/*
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

package core;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Vertical scrollbar SysexWidget.
 * @version $Id$
 * @see ScrollBarWidget
 * @see VertScrollBarWidget
 * @see ScrollBarLookupWidget
 */
public class VertScrollBarLookupWidget extends ScrollBarLookupWidget {
    public VertScrollBarLookupWidget(IPatch patch, IParameter param) {
        super(patch, param);
    }
    /** Constructor for setting up the VertScrollBarLookupWidget.
     * @param label Label for the Widget
     * @param patch The patch, which is edited
     * @param min Minimum value
     * @param max Maximum value
     * @param labelWidth width of label. If negative value, the width
     * will be determined by label strings.
     * @param pmodel a <code>ParamModel</code> instance.
     * @param sender sysexSender for transmitting the value at editing the parameter
     * @param options array of label string for each value.
     */
    public VertScrollBarLookupWidget(String label, IPatch patch, int min, int max,
                 int labelWidth,
                 IParamModel pmodel, ISender sender,
                 String[] options) {
        super(label, patch, min, max, labelWidth, pmodel, sender, options);        
    }

    public VertScrollBarLookupWidget(String label, IPatch patch, int min, int max,
                 IParamModel pmodel, ISender sender,
                 String[] options) {
        this(label, patch, min, max, -1, pmodel, sender, options);
    }

    public VertScrollBarLookupWidget(String label, IPatch patch,
                 IParamModel pmodel, ISender sender,
                 String[] options) {
        this(label, patch, 0, options.length, -1, pmodel, sender, options);
    }



    protected void createWidgets() {
        slider = new JSlider(JSlider.VERTICAL, getValueMin(), getValueMax(), getValue());
        slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                eventListener(e);
            }
        });
        text = new JTextField(options[getValue()], 4);
        text.setEditable(false);

        if (labelWidth > 0) {
            Dimension d = getJLabel().getPreferredSize();
            d.setSize(labelWidth, d.getHeight() + 3);
            getJLabel().setPreferredSize(d);
            d = getJLabel().getMinimumSize();
            d.setSize(labelWidth, d.getHeight() + 1);
            getJLabel().setMinimumSize(d);
        }
    }

    protected void layoutWidgets() {
        setLayout(new BorderLayout());

        slider.setMinimumSize(new Dimension(25, 50));
        slider.setMaximumSize(new Dimension(25, 100));

        add(getJLabel(), BorderLayout.NORTH);
        add(slider, BorderLayout.CENTER);
        add(text, BorderLayout.SOUTH);
    }
}

