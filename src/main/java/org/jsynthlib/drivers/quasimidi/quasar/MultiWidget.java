/*
 * Copyright 2005 Joachim Backhaus
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

package org.jsynthlib.drivers.quasimidi.quasar;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import org.jsynthlib.core.IPatch;
import org.jsynthlib.core.SysexWidget;

/** * Special widget
 *
 * @author Joachim Backhaus
 * @version $Id$
 */
public class MultiWidget extends SysexWidget {	
	MultiWidgetParams[] multiWidgetParams;

	private JCheckBox[] checkBoxes;

	private JLabel[] labels;
	private JComboBox[] comboBoxes;

	/**
	* @param params	The parameters for the MultiWidget
	* @param patch 	The patch data
	* @param pmodel	Model
	* @param sender	Sender
	*/
	public MultiWidget(MultiWidgetParams[] params, IPatch patch, IParamModel pmodel, ISender sender) {
		super("", patch, pmodel, sender);

		int numberOfCheckBoxes = 0;
		int maxLength = params.length;

		for (int count = 0; count < params.length; count++) {
			if (params[count] == null)
				maxLength--;
			else if (params[count].isCheckBox() )
				numberOfCheckBoxes++;
		}

		// Avoid nasty NPEs if the array is not fully initialised
		if (params.length != maxLength)
			throw new IllegalArgumentException("At least one MultiWidgetParams is null, check your MultiWidget creation code!");

		this.multiWidgetParams = params;

		this.checkBoxes = new JCheckBox[numberOfCheckBoxes];
		this.labels = new JLabel[maxLength];
		this.comboBoxes = new JComboBox[maxLength - numberOfCheckBoxes];

		createWidgets();
        layoutWidgets();
	}

	private void createCheckBox(MultiWidgetParams multiWidgetParams, int index) {
		int tempValue = multiWidgetParams.getRealValue(getValue() );

		checkBoxes[index] = new JCheckBox("", (tempValue != 0) );
		checkBoxes[index].addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
			    eventListener(e);
			}
	    });
	}

	private void createComboBox(MultiWidgetParams multiWidgetParams, int index) {
	    comboBoxes[index] = new JComboBox(multiWidgetParams.getOptions() );
		comboBoxes[index].setSelectedIndex(multiWidgetParams.getRealValue(getValue()) );
		comboBoxes[index].addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
			    eventListener(e);
			}
	    });
	}

	protected void createWidgets() {
		int countCheckBoxes = 0;
		int countComboBoxes = 0;

		for (int count = 0; count < multiWidgetParams.length; count++) {
			labels[count] = new JLabel(multiWidgetParams[count].getLabel() );

			if (multiWidgetParams[count].isCheckBox() ) {
				multiWidgetParams[count].setIndex(countCheckBoxes);
				createCheckBox(multiWidgetParams[count], countCheckBoxes);
				countCheckBoxes++;
			}
			else {
				multiWidgetParams[count].setIndex(countComboBoxes);
				createComboBox(multiWidgetParams[count], countComboBoxes);
				countComboBoxes++;
			}
		}
//System.err.println("Check boxes: " + countCheckBoxes);
//System.err.println("Combo boxes: " + countComboBoxes);
	}

	protected void layoutWidgets() {
        setLayout(new FlowLayout());

        int index = 0;

        for (int count = 0; count < multiWidgetParams.length; count++) {
        	index = multiWidgetParams[count].getIndex();

			add(labels[count]);

        	if (multiWidgetParams[count].isCheckBox() ) {
        		add(checkBoxes[index] );
        	}
        	else {
        		comboBoxes[index].setMaximumSize(new Dimension(125, 25));
        		add(comboBoxes[index] );
        	}
        }
    }

	public void setEnabled(boolean e) {
		for (int count = 0; count < checkBoxes.length; count++) {
			checkBoxes[count].setEnabled(e);
		}
		for (int count = 0; count < comboBoxes.length; count++) {
			comboBoxes[count].setEnabled(e);
		}
	}

	/** invoked when the an item is selected. */
    protected void eventListener(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.ITEM_STATE_CHANGED ) {
			int iValue = 0;
			int iTemp = 0;

			for (int count = 0; count < multiWidgetParams.length; count++) {
				iTemp = (int) Math.pow(2, multiWidgetParams[count].getStartBit() );

				if (multiWidgetParams[count].isCheckBox() ) {
					if (checkBoxes[multiWidgetParams[count].getIndex()].isSelected())
						iValue += iTemp;
				}
				else {
					iValue += iTemp * comboBoxes[multiWidgetParams[count].getIndex()].getSelectedIndex();
				}
			}

		    sendSysex(iValue);
		}
    }

    /** Adds an <code>ActionListener</code> check & combo boxes. */
    public void addEventListener(ActionListener l) {
    	for (int count = 0; count < checkBoxes.length; count++) {
			checkBoxes[count].addActionListener(l);
		}
		for (int count = 0; count < comboBoxes.length; count++) {
			comboBoxes[count].addActionListener(l);
		}
    }

    /** Adds an <code>ItemListener</code> check & combo boxes. */
    public void addEventListener(ItemListener l) {
    	for (int count = 0; count < checkBoxes.length; count++) {
			checkBoxes[count].addItemListener(l);
		}
		for (int count = 0; count < comboBoxes.length; count++) {
			comboBoxes[count].addItemListener(l);
		}
    }

	public void setValue(int iValue) {
		super.setValue(iValue);
		for (int count = 0; count < checkBoxes.length; count++) {
			checkBoxes[count].setSelected(this.multiWidgetParams[count].getRealValue(getValue()) != 0);
			checkBoxes[count].doClick();
		}
		for (int count = 0; count < comboBoxes.length; count++) {
			comboBoxes[count].setSelectedIndex(this.multiWidgetParams[count].getRealValue(getValue()) );
		}
	}
}