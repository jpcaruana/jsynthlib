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
public class ArpPak2Widget extends SysexWidget {
	// Arp velo
	private final int BITMASK_1 = 1;
	// Arp hold
	private final int BITMASK_10 = 2;
	// Arp sort
	private final int BITMASK_100 = 4;
	// Arp dir
	private final int BITMASK_11000 = 24;
	// Arp sync
	private final int BITMASK_1100000 = 96;

	
	private JCheckBox arpHoldBox;
	
	private JCheckBox arpSortBox;
		
	private JCheckBox arpVeloBox;

	private JLabel arpDirLabel = new JLabel("Direction");
	private JComboBox arpDirBox;
	
	private JLabel arpSyncLabel = new JLabel("Sync");
	private JComboBox arpSyncBox;

	/**
	*
	* @param label	Label for the check box
	* @param patch 	The patch data
	* @param pmodel	Model
	* @param sender	Sender
	* @param options	The options for the combo box
	*/
	public ArpPak2Widget(String label, IPatch patch, IParamModel pmodel, ISender sender) {
		super(label, patch, pmodel, sender);

		createWidgets();
        layoutWidgets();
	}

	protected void createWidgets() {
		int iTemp = getValue() & BITMASK_1;
		arpVeloBox = new JCheckBox("Dyn", (iTemp != 0) );
		arpVeloBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
			    eventListener(e);
			}
	    });

	    iTemp = (getValue() & BITMASK_10) >> 1;
		arpHoldBox = new JCheckBox("Hold", (iTemp != 0) );
		arpHoldBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
			    eventListener(e);
			}
	    });

	    iTemp = (getValue() & BITMASK_100) >> 2;
		arpSortBox = new JCheckBox("Sort", (iTemp != 0) );
		arpSortBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
			    eventListener(e);
			}
	    });

		iTemp = (getValue() & BITMASK_11000) >> 3;
		arpDirBox = new JComboBox(QuasarConstants.ARP_DIRECTIONS);
		arpDirBox.setSelectedIndex(iTemp);
		arpDirBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
			    eventListener(e);
			}
	    });

		iTemp = (getValue() & BITMASK_1100000) >> 5;
		arpSyncBox = new JComboBox(QuasarConstants.ARP_SYNC);
		arpSyncBox.setSelectedIndex(iTemp);
		arpSyncBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
			    eventListener(e);
			}
	    });
	}

	protected void layoutWidgets() {
        setLayout(new FlowLayout());
        arpSyncBox.setMaximumSize(new Dimension(125, 25));
        arpDirBox.setMaximumSize(new Dimension(125, 25));

		add(arpSyncLabel);
		add(arpSyncBox);
		add(arpDirLabel);
		add(arpDirBox);
		add(arpSortBox);		
		add(arpHoldBox);		
		add(arpVeloBox);                
    }

	public void setEnabled(boolean e) {
		arpVeloBox.setEnabled(e);
		arpHoldBox.setEnabled(e);
		arpSortBox.setEnabled(e);
		arpDirBox.setEnabled(e);
		arpSyncBox.setEnabled(e);
	}

	/** invoked when the an item is selected. */
    protected void eventListener(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.ITEM_STATE_CHANGED ) {
			int iValue = 0;

			if (arpVeloBox.isSelected())
				iValue = 1;

			if (arpHoldBox.isSelected())
				iValue += 2;

			if (arpSortBox.isSelected())
				iValue += 4;

			iValue += (arpDirBox.getSelectedIndex() * 8);

			iValue += (arpSyncBox.getSelectedIndex() * 32);

		    sendSysex(iValue);
		}
    }

    /** Adds an <code>ActionListener</code> to the ComboBox. */
    public void addEventListener(ActionListener l) {
    	arpVeloBox.addActionListener(l);
    	arpHoldBox.addActionListener(l);
    	arpSortBox.addActionListener(l);
		arpDirBox.addActionListener(l);
		arpSyncBox.addActionListener(l);
    }

    /** Adds an <code>ItemListener</code> to the ComboBox. */
    public void addEventListener(ItemListener l) {
    	arpVeloBox.addItemListener(l);
		arpHoldBox.addItemListener(l);
		arpSortBox.addItemListener(l);
		arpDirBox.addItemListener(l);
		arpSyncBox.addItemListener(l);
    }

	public void setValue(int iValue) {
		super.setValue(iValue);

		int iTemp = iValue & BITMASK_1;
		arpVeloBox.setSelected(iTemp != 0);
		arpVeloBox.doClick();

		iTemp = (iValue & BITMASK_10) >> 1;
		arpHoldBox.setSelected(iTemp != 0);
		arpHoldBox.doClick();

		iTemp = (iValue & BITMASK_100) >> 2;
		arpSortBox.setSelected(iTemp != 0);
		arpSortBox.doClick();

		iTemp = (iValue & BITMASK_11000) >> 3;
		arpDirBox.setSelectedIndex(iTemp);

		iTemp = (iValue & BITMASK_1100000) >> 5;
		arpSyncBox.setSelectedIndex(iTemp);
	}
}