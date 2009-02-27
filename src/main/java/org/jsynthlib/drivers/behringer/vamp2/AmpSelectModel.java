/*
 * Copyright 2005 Jeff Weber
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

package org.jsynthlib.drivers.behringer.vamp2;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jsynthlib.core.ComboBoxWidget;

/**
 * Behringer VAmp2 Amp Select Model
 * 
 * This class encapsulates the "Based on:" and "Suggested Cabinet:" JLabels so
 * that they update appropriately whenever the user changes the selection in the
 * Amp Model ComboBoxWidget.
 * 
 * @author Jeff Weber
 */
public class AmpSelectModel extends JPanel implements ActionListener {

    /**
     * Array of strings representing the descriptions of all amp models for the
     * V-Amp 2.
     */
    static final String[] AMP_DESC_STRING = { 
            "Fender 4x10 Bassman",
            "Matchless Chieftan",
            "Fender Tweed Deluxe",
            "Roland JC-120",
            "Marshall JTM 45",
            "Vox AC 30 Top-Boost",
            "'59 Marshal Plexi 100 Watt",
            "Marshal JCM 800",
            "'94 Mesa Boogie Dual Rect. Trem-O-Verb",
            "Soldano",
            "'60's Dallas Arbiter Fuzz Face",
            "V-Amp Souped-Up Rectifier",
            "Mesa Boogie Mk III",
            "Smooth Crunch",
            "Roland JC-120 + Marshall Plexi",
            "Tube Preamp",
            "'60 Blackface Deluxe + '50s Bassman",
            "Budda Twinmaster",
            "'60 Tweed Champ",
            "'65 Blackface Twin",
            "'65 JTM 45 + Budda Twinmaster",
            "Vox AC 30 Non-Top-Boost",
            "Marhall Plexi 50 w/wide range",
            "Vox AC 15 Channel 1",
            "Mesa Boogie Dual Rectifier Head",
            "Engl Savage 120",
            "'69 Marshal Plexi 50/Arrendondo",
            "V-Amp Souped-Up Rectifier Overdose",
            "Mesa Boogie Mk IIc Drive Channel",
            "Dumble Overdrive Special Drive Channel",
            "Mesa Boogie Mk IIc Clean Channel",
            "Dumble Overdrive Special Clean Channel"
    };

    /**
     * Array of strings representing the suggested cabinet models for each of
     * the 32 amp models of the V-Amp 2.
     */
    static final String[] SUGG_CAB_STRING = {
            "4 x 10'' Vintage Bass",
            "2 x 12'' US Class A",
            "1 x 8'' Vintage Tweed",
            "2 x 12'' Twin Combo",
            "4 x 12'' Vintage 30",
            "2 x 12'' Brit '67",
            "4 x 12'' Vintage 30",
            "4 x 12'' Vintage 30",
            "4 x 12'' V-AMP Custom",
            "4 x 12'' V-AMP Custom",
            "4 x 12'' Off Axis",
            "4 x 12'' V-AMP Custom",
            "4 x 12'' V-AMP Custom",
            "4 x 12'' V-AMP Custom",
            "4 x 12'' V-AMP Custom",
            "No Cabinet",
            "4 x 10'' Vintage Bass",
            "2 x 12'' US Class A",
            "1 x 8'' Vintage Tweed",
            "2 x 12'' Twin Combo",
            "4 x 12'' Vintage 30",
            "2 x 12'' Brit '67",
            "4 x 12'' Standard '78",
            "1 x 12'' Brit '60",
            "4 x 12'' V-AMP Custom",
            "4 x 12'' Standard '78",
            "4 x 12'' V-AMP Custom",
            "4 x 12'' V-AMP Custom",
            "1 x 12'' Mid Combo",
            "1 x 12'' Blackface",
            "1 x 12'' Mid Combo",
            "1 x 12'' Blackface"
    };

    /**
     * JLabel which displays "Based on:" and a description of the currently selected amp model.
     */
    private JLabel ampDesc;

    /**
     * JLabel which displays the suggested cabinet model for the currently selected amp model.
     */
    private JLabel suggestedCab;

    /**
     * Constructs an AmpSelectModel.
     * @param ampSelectCB
     *              The ComboBoxWidget for the amp model selection.
     * @param parentPanel
     *              The panel containing ampSelectCB.
     */
    AmpSelectModel(ComboBoxWidget ampSelectCB, JPanel parentPanel) {
        ampSelectCB.addEventListener(this);
        ampDesc = newJLabel("Based on: "
                + AMP_DESC_STRING[ampSelectCB.getValue()], parentPanel);
        suggestedCab = newJLabel("Suggested Cabinet: "
                + SUGG_CAB_STRING[ampSelectCB.getValue()], parentPanel);
    }

    /**
     * Creates a JLabel and adds it to the supplied parent JPanel, returning a
     * reference to the JLabel. This is used to create the "Based on:" and
     * "Suggested Cabinet:" JLabels. These labels have to be contained within
     * another JPanel with the default FlowLayout, otherwise the whole window
     * changes size everytime the user selects another amp model.
     * 
     * @param initText
     *            The initial text to be displayed in the JLabel.
     * @param parentPanel
     *            The JPanel to which the JPanel containing the JLabel is to be
     *            added.
     */
    private JLabel newJLabel(String initText, JPanel parentPanel) {
        JLabel aLabel = new JLabel(initText);
        JPanel aPanel = new JPanel();
        aPanel.add(aLabel);
        parentPanel.add(aPanel);
        return aLabel;
    }

    /** 
     * Handles ActionEvents for the amp select combo box.
     * @param e
     *          An ActionEvent.
     */
    public void actionPerformed(ActionEvent e) {
        JComboBox cb = (JComboBox) e.getSource();
        int selectedIndex = cb.getSelectedIndex();
        ampDesc.setText("Based on: " + AMP_DESC_STRING[selectedIndex]);
        suggestedCab.setText("Suggested Cabinet: "
                + SUGG_CAB_STRING[selectedIndex]);
    }
}