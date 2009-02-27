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

import core.*;
import synthdrivers.YamahaUB99.format.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import com.dreamfabric.DKnob;

/**
 * A rotary knob widget. Uses the class DKnob by <a
 * href="http://www.dreamfabric.com/">DreamFabric</a> which is
 * responsible for drawing the widget. This component should rather be
 * used for parameters which do not need fine adjustment.
 *
 * A tooltip is used to display the current value. A prior version
 * used a text field instead, but the resulting component was
 * inconsistent and diffucult to place.
 *
 * @version $Id$
 * @author denis queffeulou mailto:dqueffeulou@free.fr
 * @see KnobLookupWidget
 */
public class Knob extends SysexWidget {
    /** display offset */
    private int mBase;
    /** DKnob widget object */
    protected DKnob mKnob = new DKnob();
    protected ClickLabel mLabel = new ClickLabel();
    private IFormat mFormat;

    public Knob(IPatch patch, IParameter param) {
        super(patch, param);

        mBase = 0;
        createWidgets();
        layoutWidgets();
    }

    /**
     * Creates a new <code>Knob</code> instance.
     *
     * @param label label String.
     * @param patch a <code>Patch</code> value.
     * @param min minimum value.
     * @param max maximum value.
     * @param base value display offset.
     * @param pmodel a <code>ParamModel</code> object.
     * @param sender a <code>ISender</code> object.
     */
    public Knob(String label, IPatch patch, int min, int max, int base,
              IParamModel pmodel, ISender sender) {
        super(label, patch, min, max, pmodel, sender);
        mBase = base;

        createWidgets();
        layoutWidgets();
    }

    public Knob(String label, IPatch patch, int min, int max, IFormat format,
              IParamModel pmodel, ISender sender) {
        super(label, patch, min, max, pmodel, sender);
        mFormat = format;

        createWidgets();
        layoutWidgets();
    }

    protected void createWidgets() {
        mLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mKnob.setDragType(DKnob.SIMPLE_MOUSE_DIRECTION);
        if (getLabel() != null) {
            getJLabel().setHorizontalAlignment(SwingConstants.CENTER);
        }
        int oValue = getValue();
        mLabel.setText(display(oValue));
        mKnob.setValue(((float) getValue() - getValueMin()) / (getValueMax() - getValueMin()));

        // Add a change listener to the knob
        mKnob.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                eventListener(e);
            }
        });
        // mouse wheel event is supported by J2SE 1.4 and later
        mKnob.addMouseWheelListener(new MouseWheelListener() {
            public void mouseWheelMoved(MouseWheelEvent e) {
                eventListener(e);
            }
        });
        // Add a change listener to the label
        mLabel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                labelListener(e);
            }
        });
    }

    /** invoked when knob is moved. */
    protected void eventListener(ChangeEvent e) {
        DKnob t = (DKnob) e.getSource();
        int oValue = Math.round(t.getValue() * (getValueMax() - getValueMin())) + getValueMin();
        String oVStr = display(oValue);
        mLabel.setText(oVStr);

        sendSysex(oValue);
    }

    /** invoked when label is clicked. */
    protected void labelListener(ChangeEvent e) {
        ClickLabel l = (ClickLabel) e.getSource();
        int oValue = getValue() + l.getUpDown();
        mKnob.setValue(((float) oValue - getValueMin()) / (getValueMax() - getValueMin()));
    }

    /** invoked when mouse wheel is moved. */
    protected void eventListener(MouseWheelEvent e) {
        DKnob t = (DKnob) e.getSource();
        if (t.hasFocus()) // to make consistent with other operation.
            t.setValue(t.getValue() - (e.getWheelRotation() / (float) (getValueMax() - getValueMin())));
    }

    /** Adds a <code>ChangeListener</code> to the Knob. */
    public void addEventListener(ChangeListener l) {
        mKnob.addChangeListener(l);
    }

    /** Adds a <code>MouseWheelListener</code> to the Knob. */
    public void addEventListener(MouseWheelListener l) {
        mKnob.addMouseWheelListener(l);
    }

    protected void layoutWidgets() {
        setLayout(new BorderLayout());
        add(mKnob, BorderLayout.CENTER);
        if (getLabel() != null) {
            add(getJLabel(), BorderLayout.NORTH);
        }
        add(mLabel, BorderLayout.SOUTH);
    }

    protected String display(int v) {
        if (mFormat == null) {
            return Integer.toString(v + mBase);
        } else {
            return mFormat.fmtString(v);
        }
    }

    public void setValue(int v) {
        super.setValue(v);
        String oVStr = display(v);
        mKnob.setValue(((float) v - getValueMin()) / (getValueMax() - getValueMin()));
    }

    public void setEnabled(boolean e) {
        mKnob.setEnabled(e);
        getJLabel().setEnabled(e);
    }

}

