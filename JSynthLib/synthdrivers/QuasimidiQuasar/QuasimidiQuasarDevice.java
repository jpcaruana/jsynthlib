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

import core.Device;

import java.util.prefs.Preferences;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Device class for the Quasimidi Quasar
 *
 * @author  Joachim Backhaus
 * @version $Id$
 */
public class QuasimidiQuasarDevice extends Device {
    private static final String INFO_TEXT
    = "Librarian & editor for single perfomances and performance banks.\n\n"
    + "If you have a card installed, please select the QuasimidiQuasar driver "
    + "and click on 'Show Details' and then select the 'Configuration' tab.\n"
    + "There you can specifiy which cards you have installed.";

    protected JComboBox	firstCardSlot;
    protected JComboBox	secondCardSlot;

    /**
    * Constructor for DeviceListWriter.
    */
    public QuasimidiQuasarDevice() {
        super(  "Quasimidi",
                "Quasar",
                "F07E..06023F20..............F7",
                INFO_TEXT,
                "Joachim Backhaus");
    }


    /**
    * Constructor for the actual work.
    *
    * @param prefs  The Preferences for this device
    */
    public QuasimidiQuasarDevice(Preferences prefs) {
        this();

        this.prefs = prefs;

        addDriver(new QuasimidiQuasarTemporaryConverter());
        addDriver(new QuasimidiQuasarSingleDriver());
        addDriver(new QuasimidiQuasarBankDriver());
    }

    /**
     * Create a configration panel.  Override this if your device
     * supports a configration panel.
     */
    protected JPanel config() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));

		firstCardSlot = new JComboBox(QuasarConstants.CARDS);
		firstCardSlot.setSelectedIndex( getCardInFirstSlot() );
		firstCardSlot.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    JComboBox cBox = (JComboBox)e.getSource();

			    setCardInFirstSlot( cBox.getSelectedIndex() );
			}
		});

		secondCardSlot = new JComboBox(QuasarConstants.CARDS);
		secondCardSlot.setSelectedIndex( getCardInSecondSlot() );
		secondCardSlot.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    JComboBox cBox = (JComboBox)e.getSource();

			    setCardInSecondSlot( cBox.getSelectedIndex() );
			}
	    });

		panel.add(new JLabel("Select the cards."));		
		panel.add(firstCardSlot);
		panel.add(secondCardSlot);
		return panel;
    }

    /** Getter for firstCardSlot */
    public int getCardInFirstSlot() {
		return this.prefs.getInt("cardInFirstSlot", 0);
    }
    /** Setter for firstCardSlot */
    public void setCardInFirstSlot(int cardInSlot) {
		this.prefs.putInt("cardInFirstSlot", cardInSlot);
    }

    /** Getter for secondCardSlot */
    public int getCardInSecondSlot() {
		return this.prefs.getInt("cardInSecondSlot", 0);
    }
    /** Setter for secondCardSlot */
    public void setCardInSecondSlot(int cardInSlot) {
		this.prefs.putInt("cardInSecondSlot", cardInSlot);
    }

}
