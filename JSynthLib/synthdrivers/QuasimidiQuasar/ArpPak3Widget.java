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
public class ArpPak3Widget extends SysexWidget {
	// Arp freeze
	private final int BITMASK_1 = 1;
	// Arp out
	private final int BITMASK_10 = 2;
	// Arp thru
	private final int BITMASK_100 = 4;
	// Arp track
	private final int BITMASK_1111000 = 120;


	private JCheckBox arpFreezeBox;
	private JCheckBox arpOutBox;
	private JCheckBox arpThruBox;

	private JLabel arpTrackLabel = new JLabel("Track");
	private JComboBox arpTrackBox;

	/**
	*
	* @param label	Label for the check box
	* @param patch 	The patch data
	* @param pmodel	Model
	* @param sender	Sender
	* @param options	The options for the combo box
	*/
	public ArpPak3Widget(String label, IPatch patch, IParamModel pmodel, ISender sender) {
		super(label, patch, pmodel, sender);

		createWidgets();
        layoutWidgets();
	}

	protected void createWidgets() {
		int iTemp = getValue() & BITMASK_1;
		arpFreezeBox = new JCheckBox("Freeze", (iTemp != 0) );
		arpFreezeBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
			    eventListener(e);
			}
	    });

	    iTemp = (getValue() & BITMASK_10) >> 1;
		arpOutBox = new JCheckBox("Out", (iTemp != 0) );
		arpOutBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
			    eventListener(e);
			}
	    });

	    iTemp = (getValue() & BITMASK_100) >> 2;
		arpThruBox = new JCheckBox("Thru", (iTemp != 0) );
		arpThruBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
			    eventListener(e);
			}
	    });

		iTemp = (getValue() & BITMASK_1111000) >> 3;
		arpTrackBox = new JComboBox(QuasarConstants.ARP_TRACKS);
		arpTrackBox.setSelectedIndex(iTemp);
		arpTrackBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
			    eventListener(e);
			}
	    });
	}

	protected void layoutWidgets() {
        setLayout(new FlowLayout());
        arpTrackBox.setMaximumSize(new Dimension(125, 25));

		add(arpTrackLabel);
		add(arpTrackBox);
		add(arpThruBox);
		add(arpOutBox);
		add(arpFreezeBox);
    }

	public void setEnabled(boolean e) {
		arpFreezeBox.setEnabled(e);
		arpOutBox.setEnabled(e);
		arpThruBox.setEnabled(e);
		arpTrackBox.setEnabled(e);
	}

	/** invoked when the an item is selected. */
    protected void eventListener(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.ITEM_STATE_CHANGED ) {
			int iValue = 0;

			if (arpFreezeBox.isSelected())
				iValue = 1;

			if (arpOutBox.isSelected())
				iValue += 2;

			if (arpThruBox.isSelected())
				iValue += 4;

			iValue += (arpTrackBox.getSelectedIndex() * 8);

		    sendSysex(iValue);
		}
    }

    /** Adds an <code>ActionListener</code> to the ComboBox. */
    public void addEventListener(ActionListener l) {
    	arpFreezeBox.addActionListener(l);
    	arpOutBox.addActionListener(l);
    	arpThruBox.addActionListener(l);
		arpTrackBox.addActionListener(l);
    }

    /** Adds an <code>ItemListener</code> to the ComboBox. */
    public void addEventListener(ItemListener l) {
    	arpFreezeBox.addItemListener(l);
		arpOutBox.addItemListener(l);
		arpThruBox.addItemListener(l);
		arpTrackBox.addItemListener(l);
    }

	public void setValue(int iValue) {
		super.setValue(iValue);

		int iTemp = iValue & BITMASK_1;
		arpFreezeBox.setSelected(iTemp != 0);
		arpFreezeBox.doClick();

		iTemp = (iValue & BITMASK_10) >> 1;
		arpOutBox.setSelected(iTemp != 0);
		arpOutBox.doClick();

		iTemp = (iValue & BITMASK_100) >> 2;
		arpThruBox.setSelected(iTemp != 0);
		arpThruBox.doClick();

		iTemp = (iValue & BITMASK_1111000) >> 3;
		arpTrackBox.setSelectedIndex(iTemp);
	}
}