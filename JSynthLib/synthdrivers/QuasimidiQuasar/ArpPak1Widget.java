/*
 * Copyright 2004 Joachim Backhaus
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

package synthdrivers.QuasimidiQuasar;

import core.*;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.*;
import javax.swing.*;

/**
 * Special widget for the Quasimidi Quasar
 *
 * @author Joachim Backhaus
 * @version $Id$
 */
public class ArpPak1Widget extends SysexWidget {
	private final int BITMASK_11 = 3;
	private final int BITMASK_100 = 4;


	private JCheckBox arpSwitchBox;
	
	private JLabel arpResLabel = new JLabel("Resolution");
	private JComboBox arpResBox;

	/** An array of the list of the options in the ComboBox. */
    private Object[] options;

	/**
	*
	* @param label	Label for the check box
	* @param patch 	The patch data
	* @param pmodel	Model
	* @param sender	Sender
	* @param options	The options for the combo box
	*/
	public ArpPak1Widget(String label, IPatch patch, IParamModel pmodel, ISender sender) {
		super(label, patch, pmodel, sender);		

		createWidgets();
        layoutWidgets();
	}

	protected void createWidgets() {
		int arpSwitchValue = (getValue() & BITMASK_100) >> 2;
		int arpResValue = getValue() & BITMASK_11;		

		if (arpSwitchValue != 0) {
			arpSwitchBox = new JCheckBox(getLabel(), true );
		}
		else {
			arpSwitchBox = new JCheckBox(getLabel(), false );
		}		
		arpSwitchBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
			    eventListener(e);
			}
	    });

		arpResBox = new JComboBox(QuasarConstants.ARP_RESOLUTIONS);
		arpResBox.setSelectedIndex(arpResValue);
		arpResBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
			    eventListener(e);
			}
	    });
	}

	protected void layoutWidgets() {
        setLayout(new FlowLayout());
        arpResBox.setMaximumSize(new Dimension(125, 25));

        add(arpSwitchBox);
        add(arpResLabel);
        add(arpResBox);
    }

	public void setEnabled(boolean e) {
		arpSwitchBox.setEnabled(e);
		arpResBox.setEnabled(e);
	}

	/** invoked when the an item is selected. */
    protected void eventListener(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.ITEM_STATE_CHANGED ) {
			int iValue = arpResBox.getSelectedIndex();

			if (arpSwitchBox.isSelected()) {
				iValue += 4;
			}

		    sendSysex(iValue);
		}
    }

    /** Adds an <code>ActionListener</code> to the ComboBox. */
    public void addEventListener(ActionListener l) {
    	arpSwitchBox.addActionListener(l);
		arpResBox.addActionListener(l);
    }

    /** Adds an <code>ItemListener</code> to the ComboBox. */
    public void addEventListener(ItemListener l) {
		arpSwitchBox.addItemListener(l);
		arpResBox.addItemListener(l);
    }

	public void setValue(int iValue) {
		super.setValue(iValue);

		int arpSwitchValue = (iValue & BITMASK_100) >> 2;
		int arpResValue = iValue & BITMASK_11;

		arpSwitchBox.setSelected(arpSwitchValue != 0);
		arpSwitchBox.doClick();
		arpResBox.setSelectedIndex(arpResValue);
	}
}