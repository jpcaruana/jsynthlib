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

import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;


/**
 *
 * @author  Joachim Backhaus
 * @version $Id$
 */
public class QuasimidiQuasarSingleEditor extends PatchEditorFrame {

    // Needed here to disable the "Part 2-4" parameters
    private static JTabbedPane oTabs;

    private KnobWidget perfValueWidget;
    private KnobWidget[] fx1ParWidgets = new KnobWidget[6];
    private KnobWidget[] fx2ParWidgets = new KnobWidget[9];

    public QuasimidiQuasarSingleEditor(Patch iPatch) {
	    super ("Quasimidi Quasar Performance Editor", iPatch);

	    Patch patch = (Patch) iPatch;

        oTabs = new JTabbedPane();
        scrollPane.add(oTabs);

        // Common Parameter
        oTabs.add(buildCommonWindow(patch), "Performance common");
        // Modulation Matrix
        oTabs.add(buildModulationMatrix(patch), "Modulation matrix");
        // FX 1
        oTabs.add(buildFX1Window(patch), "FX 1");
	    // FX 2
        oTabs.add(buildFX2Window(patch), "FX 2");
	    // Arpeggiator
	    oTabs.add(buildArpWindow(patch), "Arpeggiator");
        // Part Parameter x 4
		for (int i = 1; i <= 4; i++) {
			oTabs.add(buildPartWindow(i, patch), "Part " + i);
		}

		// Needed for initial tab enabling/disabling
		changePerfMode	( (int)patch.sysex[9]);
		changeFX1		( (int)patch.sysex[50]);
		changeFX2		( (int)patch.sysex[58]);

        setSize(500, 600);
		pack();
		setVisible(true);
    }

    /**
    * Common parameter tab
    */
	private Container buildCommonWindow(Patch patch) {
		Box verticalBox = Box.createVerticalBox();
		GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5,2,5,2);  // padding

		JPanel commonPanel =  new JPanel();
		commonPanel.setLayout(gridbag);
		// Name
		gbc.gridx = 0;
        gbc.gridy = 0;
		commonPanel.add(new PatchNameWidget("Name", p), gbc);
		// Performance level
		gbc.gridx = 1;
        gbc.gridy = 0;
		commonPanel.add(new KnobWidget("Performance level", p, 0, 127, 0, new ParamModel(patch, 8), new QuasarSender(0x00, 0x00) ), gbc );
		// Performance mode
		gbc.gridx = 2;
        gbc.gridy = 0;
        ComboBoxWidget perfModeWidget = new ComboBoxWidget("Performance mode", p, new ParamModel(patch, 9), new QuasarSender(0x00, 0x01), QuasarConstants.PERFORMANCE_MODES);
        perfModeWidget.addEventListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					JComboBox oComboBox = (JComboBox)e.getSource();
					int iPerfMode = oComboBox.getSelectedIndex();

					changePerfMode(iPerfMode);
				}
			}
		});
		commonPanel.add(perfModeWidget, gbc );
		commonPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED), "Name, etc.", TitledBorder.CENTER, TitledBorder.CENTER));
		verticalBox.add(commonPanel, gbc);

		commonPanel =  new JPanel();
		commonPanel.setLayout(gridbag);
		// Performance value (Splitkey, detune)
		gbc.gridx = 1;
        gbc.gridy = 0;
        perfValueWidget = new KnobWidget(" ", p, 0, 127, 0, new ParamModel(patch, 10), new QuasarSender(0x00, 0x02) );
		commonPanel.add(perfValueWidget, gbc );
		// Free controller number (0 - 97)
		gbc.gridx = 2;
        gbc.gridy = 0;
		commonPanel.add(new KnobWidget("Free ctrl. no.", p, 0, 97, 0, new ParamModel(patch, 12), new QuasarSender(0x00, 0x04) ), gbc );
		commonPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED), "Diverse", TitledBorder.CENTER, TitledBorder.CENTER));
		verticalBox.add(commonPanel, gbc);

		JPanel footCtrlPanel =  new JPanel(new FlowLayout(FlowLayout.CENTER));
		// Foot controller number
		footCtrlPanel.add(new KnobWidget("Controller no.", p, 0, 127, 0, new ParamModel(patch, 13), new QuasarSender(0x00, 0x05) ) );
        // Foot control on value
        footCtrlPanel.add(new KnobWidget("On value", p, 0, 127, 0, new ParamModel(patch, 14), new QuasarSender(0x00, 0x06) ) );
        // Foot control off value
        footCtrlPanel.add(new KnobWidget("Off value", p, 0, 127, 0, new ParamModel(patch, 15), new QuasarSender(0x00, 0x07) ) );
        // Foot control toggle mode (00h = off, 01h = on)
        footCtrlPanel.add(new ComboBoxWidget("Toggle mode", p, new ParamModel(patch, 16), new QuasarSender(0x00, 0x08), QuasarConstants.SWITCH) );
        footCtrlPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED), "Foot controller", TitledBorder.CENTER, TitledBorder.CENTER));
		verticalBox.add(footCtrlPanel);

		return verticalBox;
	}

	/**
	* Modulation matrix tab
	*/
	private Container buildModulationMatrix(Patch patch) {
	    Box verticalBox = Box.createVerticalBox();
		GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5,2,5,2);  // padding

		JPanel modPanel =  new JPanel();
		modPanel.setLayout(gridbag);

		int offset = 0;

		// Sources
		gbc.gridwidth = 2;
		gbc.gridx = 0;
        gbc.gridy = 1;
        modPanel.add(new JLabel("Mod. wheel"), gbc );
        gbc.gridx = 0;
        gbc.gridy = 2;
        modPanel.add(new JLabel("Aftertouch"), gbc );
        gbc.gridx = 0;
        gbc.gridy = 3;
        modPanel.add(new JLabel("Pitchbend"), gbc );
        gbc.gridx = 0;
        gbc.gridy = 4;
        modPanel.add(new JLabel("Free ctrl."), gbc );

        // Destinations
        gbc.gridwidth = 1;
        gbc.gridx = 2;
        gbc.gridy = 0;
        modPanel.add(new JLabel("LFO"), gbc );
        gbc.gridx = 3;
        gbc.gridy = 0;
        modPanel.add(new JLabel("Volume"), gbc );
        gbc.gridx = 4;
        gbc.gridy = 0;
        modPanel.add(new JLabel("Pitch"), gbc );
        gbc.gridx = 5;
        gbc.gridy = 0;
        modPanel.add(new JLabel("Tone"), gbc );
        gbc.gridx = 6;
        gbc.gridy = 0;
        modPanel.add(new JLabel("FXMA"), gbc );
        gbc.gridx = 7;
        gbc.gridy = 0;
        modPanel.add(new JLabel("FXMB"), gbc );
        gbc.gridx = 8;
        gbc.gridy = 0;
        modPanel.add(new JLabel("ARPD"), gbc );
        gbc.gridx = 9;
        gbc.gridy = 0;
        modPanel.add(new JLabel("ARPGT"), gbc );

        // Values
		for(int count = 1; count <= 4; count++) {
            // mod.depth[SOURCEx][DEST1]
            gbc.gridx = 2;
            gbc.gridy = count;
            modPanel.add(new SpinnerWidget("", p, 0, 127, 0, new ParamModel(patch, 17 + offset), new QuasarSender(0x00, 0x09 + offset ) ), gbc );
            // mod.depth[SOURCEx][DEST2]
            gbc.gridx = 3;
            gbc.gridy = count;
            modPanel.add(new SpinnerWidget("", p, 0, 127, 0, new ParamModel(patch, 18 + offset), new QuasarSender(0x00, 0x0A + offset ) ), gbc );
            // mod.depth[SOURCEx][DEST3]
            gbc.gridx = 4;
            gbc.gridy = count;
            modPanel.add(new SpinnerWidget("", p, 0, 127, 0, new ParamModel(patch, 19 + offset), new QuasarSender(0x00, 0x0B + offset ) ), gbc );
            // mod.depth[SOURCEx][DEST4]
            gbc.gridx = 5;
            gbc.gridy = count;
            modPanel.add(new SpinnerWidget("", p, 0, 127, 0, new ParamModel(patch, 20 + offset), new QuasarSender(0x00, 0x0C + offset ) ), gbc );
            // mod.depth[SOURCEx][DEST5]
            gbc.gridx = 6;
            gbc.gridy = count;
            modPanel.add(new SpinnerWidget("", p, 0, 127, 0, new ParamModel(patch, 21 + offset), new QuasarSender(0x00, 0x0D + offset ) ), gbc );
            // mod.depth[SOURCEx][DEST6]
            gbc.gridx = 7;
            gbc.gridy = count;
            modPanel.add(new SpinnerWidget("", p, 0, 127, 0, new ParamModel(patch, 22 + offset), new QuasarSender(0x00, 0x0E + offset ) ), gbc );
            // mod.depth[SOURCEx][DEST7]
            gbc.gridx = 8;
            gbc.gridy = count;
            modPanel.add(new SpinnerWidget("", p, 0, 127, 0, new ParamModel(patch, 23 + offset), new QuasarSender(0x00, 0x0F + offset ) ), gbc );
            // mod.depth[SOURCEx][DEST8]
            gbc.gridx = 9;
            gbc.gridy = count;
            modPanel.add(new SpinnerWidget("", p, 0, 127, 0, new ParamModel(patch, 24 + offset), new QuasarSender(0x00, 0x10 + offset ) ), gbc );

            offset += 8;
        }

		verticalBox.add(modPanel);

		return verticalBox;
	}

	/**
	* FX 1 tab
	*/
	private Container buildFX1Window(Patch patch) {
	    Box verticalBox = Box.createVerticalBox();
		GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5,2,5,2);  // padding

        fx1ParWidgets[0] = new KnobWidget(" ", p, 0, 127, 0, new ParamModel(patch, 51), new QuasarSender(0x00, 0x2B) );
        fx1ParWidgets[1] = new KnobWidget(" ", p, 0, 63, 0, new ParamModel(patch, 52), new QuasarSender(0x00, 0x2C) );
        fx1ParWidgets[2] = new KnobWidget(" ", p, 0, 127, 0, new ParamModel(patch, 53), new QuasarSender(0x00, 0x2D) );
        fx1ParWidgets[3] = new KnobWidget(" ", p, 0, 127, 0, new ParamModel(patch, 54), new QuasarSender(0x00, 0x2E) );
        fx1ParWidgets[4] = new KnobWidget(" ", p, 0, 127, 0, new ParamModel(patch, 55), new QuasarSender(0x00, 0x2F) );
        fx1ParWidgets[5] = new KnobWidget(" ", p, 0, 127, 0, new ParamModel(patch, 56), new QuasarSender(0x00, 0x30) );

		JPanel tempPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		// FX1 Activity
		tempPanel.add(new CheckBoxWidget("FX 1 activity", p, new ParamModel(patch, 49), new QuasarSender(0x00, 0x29) ) );
		// FX1 effect typ
		ComboBoxWidget fx1TypWidget = new ComboBoxWidget("FX 1 typ", p, new ParamModel(patch, 50), new QuasarSender(0x00, 0x2A), QuasarConstants.FX1_EFFECTS );
		fx1TypWidget.addEventListener(new ItemListener() {
			public void itemStateChanged (ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					JComboBox oComboBox = (JComboBox)e.getSource();
					int fx1 = oComboBox.getSelectedIndex();

					changeFX1(fx1);
				}
			}
		});
		tempPanel.add(fx1TypWidget);
		verticalBox.add(tempPanel);

		tempPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		// FX1 parameter[Page1][Par1]
		tempPanel.add(fx1ParWidgets[0]);
		// FX1 parameter[Page1][Par2]
		tempPanel.add(fx1ParWidgets[1]);
		// FX1 parameter[Page1][Par3]
		tempPanel.add(fx1ParWidgets[2]);
		verticalBox.add(tempPanel);

		tempPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		// FX1 parameter[Page2][Par1]
		tempPanel.add(fx1ParWidgets[3]);
		// FX1 parameter[Page2][Par2]
		tempPanel.add(fx1ParWidgets[4]);
		// FX1 parameter[Page2][Par3]
		tempPanel.add(fx1ParWidgets[5]);
		verticalBox.add(tempPanel);

		return verticalBox;
	}

	/**
	* FX 2 tab
	*/
	private Container buildFX2Window(Patch patch) {
	    Box verticalBox = Box.createVerticalBox();
		GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5,2,5,2);  // padding

        fx2ParWidgets[0] = new KnobWidget(" ", p, 0, 127, 0, new ParamModel(patch, 59), new QuasarSender(0x00, 0x33) );
        fx2ParWidgets[1] = new KnobWidget(" ", p, 0, 127, 0, new ParamModel(patch, 60), new QuasarSender(0x00, 0x34) );
        fx2ParWidgets[2] = new KnobWidget(" ", p, 0, 127, 0, new ParamModel(patch, 61), new QuasarSender(0x00, 0x35) );
        fx2ParWidgets[3] = new KnobWidget(" ", p, 0, 127, 0, new ParamModel(patch, 62), new QuasarSender(0x00, 0x36) );
        fx2ParWidgets[4] = new KnobWidget(" ", p, 0, 127, 0, new ParamModel(patch, 63), new QuasarSender(0x00, 0x37) );
        fx2ParWidgets[5] = new KnobWidget(" ", p, 0, 127, 0, new ParamModel(patch, 64), new QuasarSender(0x00, 0x38) );
        fx2ParWidgets[6] = new KnobWidget(" ", p, 0, 127, 0, new ParamModel(patch, 65), new QuasarSender(0x00, 0x39) );
        fx2ParWidgets[7] = new KnobWidget(" ", p, 0, 127, 0, new ParamModel(patch, 66), new QuasarSender(0x00, 0x3A) );
        fx2ParWidgets[8] = new KnobWidget(" ", p, 0, 127, 0, new ParamModel(patch, 67), new QuasarSender(0x00, 0x3B) );

		JPanel tempPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		// FX2 Activity
		tempPanel.add(new CheckBoxWidget("FX 2 activity", p, new ParamModel(patch, 57), new QuasarSender(0x00, 0x31) ) );
		// FX2 effect typ
		ComboBoxWidget fx2TypWidget = new ComboBoxWidget("FX 2 typ", p, new ParamModel(patch, 58), new QuasarSender(0x00, 0x32), QuasarConstants.FX2_EFFECTS );
		fx2TypWidget.addEventListener(new ItemListener() {
			public void itemStateChanged (ItemEvent e) {
				JComboBox oComboBox = (JComboBox)e.getSource();
				int fx2 = oComboBox.getSelectedIndex();

				changeFX2(fx2);
			}
		});
		tempPanel.add(fx2TypWidget);
		verticalBox.add(tempPanel);

		tempPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		// FX2 parameter[Page1][Par1]
		tempPanel.add(fx2ParWidgets[0]);
		// FX2 parameter[Page1][Par2]
		tempPanel.add(fx2ParWidgets[1]);
		// FX2 parameter[Page1][Par3]
		tempPanel.add(fx2ParWidgets[2]);
		verticalBox.add(tempPanel);

		tempPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		// FX2 parameter[Page2][Par1]
		tempPanel.add(fx2ParWidgets[3]);
		// FX2 parameter[Page2][Par2]
		tempPanel.add(fx2ParWidgets[4]);
		// FX2 parameter[Page2][Par3]
		tempPanel.add(fx2ParWidgets[5]);
		verticalBox.add(tempPanel);

		tempPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		// FX2 parameter[Page3][Par1]
		tempPanel.add(fx2ParWidgets[6]);
		// FX2 parameter[Page3][Par2]
		tempPanel.add(fx2ParWidgets[7]);
		// FX2 parameter[Page3][Par3]
		tempPanel.add(fx2ParWidgets[8]);
		verticalBox.add(tempPanel);

		return verticalBox;
	}

	/**
	* FX 1 tab
	*/
	private Container buildArpWindow(Patch patch) {
	    Box verticalBox = Box.createVerticalBox();
		GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5,2,5,2);  // padding

		JPanel tempPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

// Arp pak 1
// new ParamModel(patch, 68), new QuasarSender(0x00, 0x3C)
		// Arp on/off
		tempPanel.add(new JCheckBox("On/Off") );
		// Arpeggiator resolution		
		tempPanel.add(new JComboBox(QuasarConstants.ARP_RESOLUTIONS) );

		// Arpeggiator speed
		tempPanel.add(new KnobWidget("Speed", p, 0, 127, 0, new ParamModel(patch, 69), new QuasarSender(0x00, 0x3D) ) );
		// Arpeggiator gate
		tempPanel.add(new KnobWidget("Gate", p, 0, 127, 0, new ParamModel(patch, 70), new QuasarSender(0x00, 0x3E) ) );
		verticalBox.add(tempPanel);

		return verticalBox;
	}

	/**
    * Performance part parameter tab
    */
	private Container buildPartWindow(final int partNo, Patch patch) {
	    int offset = QuasarConstants.PART_SIZE * (partNo - 1);

		Box verticalBox = Box.createVerticalBox();
		GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5,2,5,2);  // padding

		JPanel partPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

		int cardInFirstSlot		= ( (QuasimidiQuasarDevice)p.getDevice() ).getCardInFirstSlot();
		int cardInSecondSlot	= ( (QuasimidiQuasarDevice)p.getDevice() ).getCardInSecondSlot();
		int banks = QuasarConstants.BANK_NAMES.length;

		// If a card is in the second slot it is not relevant
		// for the number of banks if a card is also in the first slot
		// so process this first
		if (cardInSecondSlot > 0) {
			banks += 4;
		}
		else if (cardInFirstSlot > 0) {
			banks += 2;
		}

		String bankNames[] = new String[banks];
		// Copy the bank names from the default banks to the temporary array
		int count = 0;
		for ( ; count < QuasarConstants.BANK_NAMES.length; count++) {
			bankNames[count] = QuasarConstants.BANK_NAMES[count];
		}

		if (banks > QuasarConstants.BANK_NAMES.length) {
			if (cardInFirstSlot > 0) {
				// No ++count here as the count is incremented enough in the for loop!
				bankNames[count] = QuasarConstants.CARDS[cardInFirstSlot] + " Bank 1";
				bankNames[++count] = QuasarConstants.CARDS[cardInFirstSlot] + " Bank 2";
			}
			// There is no card in slot 1 but one in slot 2
			else if (cardInSecondSlot > 0) {
				bankNames[++count] = "No card in slot 1";
				bankNames[++count] = "No card in slot 1";
			}

			if (cardInSecondSlot > 0) {
				bankNames[++count] = QuasarConstants.CARDS[cardInSecondSlot] + " Bank 1";
				bankNames[++count] = QuasarConstants.CARDS[cardInSecondSlot] + " Bank 2";
			}
		}

		// Bank number (up to 7 w/o cards, up to 11 with cards)
		partPanel.add(new ComboBoxWidget("Bank No.", p, new ParamModel(patch, 82 + offset), new QuasarSender(partNo, 0x00), bankNames) );
		// Patch number
		partPanel.add(new SpinnerWidget("Patch No.", p, 0, 127, 0, new ParamModel(patch, 83 + offset), new QuasarSender(partNo, 0x01) ) );
        // Trackmode (00h = muted, 01h = poly, 02h = mono)
        partPanel.add(new ComboBoxWidget("Trackmode", p, new ParamModel(patch, 84 + offset), new QuasarSender(partNo, 0x02), QuasarConstants.TRACKMODE) );
        // Level
        partPanel.add(new KnobWidget("Level", p, 0, 127, 0, new ParamModel(patch, 85 + offset), new QuasarSender(partNo, 0x03) ) );
        // Panorama
        partPanel.add(new ComboBoxWidget("Pan", p, new ParamModel(patch, 86 + offset), new QuasarSender(partNo, 0x04), QuasarConstants.PANORAMA) );
        verticalBox.add(partPanel);

        partPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
         // FX1 Send
        partPanel.add(new KnobWidget("FX1 Send", p, 0, 63, 0, new ParamModel(patch, 87 + offset), new QuasarSender(partNo, 0x05) ) );
         // FX2 Send
        partPanel.add(new KnobWidget("FX2 Send", p, 0, 63, 0, new ParamModel(patch, 88 + offset), new QuasarSender(partNo, 0x06) ) );
        // Transpose (18h = no transpose, 00h = -24, 30h = +24)
        partPanel.add(new KnobWidget("Transpose", p, 0, 48, -24, new ParamModel(patch, 89 + offset), new QuasarSender(partNo, 0x07) ) );
        // Tune (Offset value, -64 ... +63, 40h = 0)
        partPanel.add(new KnobWidget("Tune", p, 0, 127, -64, new ParamModel(patch, 90 + offset), new QuasarSender(partNo, 0x08) ) );
        // Cutoff frequency (Offset value, -64 ... +63, 40h = 0)
        partPanel.add(new KnobWidget("Cutoff", p, 0, 127, -64, new ParamModel(patch, 91 + offset), new QuasarSender(partNo, 0x09) ) );
        // Resonance (Offset value, -64 ... +63, 40h = 0)
        partPanel.add(new KnobWidget("Resonance", p, 0, 127, -64, new ParamModel(patch, 92 + offset), new QuasarSender(partNo, 0x0A) ) );
        verticalBox.add(partPanel);

        partPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        // EG Attack (Offset value, -64 ... +63, 40h = 0)
        partPanel.add(new KnobWidget("EG Attack", p, 0, 127, -64, new ParamModel(patch, 93 + offset), new QuasarSender(partNo, 0x0B) ) );
        // EG Decay (Offset value, -64 ... +63, 40h = 0)
        partPanel.add(new KnobWidget("EG Decay", p, 0, 127, -64, new ParamModel(patch, 94 + offset), new QuasarSender(partNo, 0x0C) ) );
        // EG Release (Offset value, -64 ... +63, 40h = 0)
        partPanel.add(new KnobWidget("EG Release", p, 0, 127, -64, new ParamModel(patch, 95 + offset), new QuasarSender(partNo, 0x0D) ) );
        // Vibrato rate (Offset value, -64 ... +63, 40h = 0)
        partPanel.add(new KnobWidget("Vibrato rate", p, 0, 127, -64, new ParamModel(patch, 96 + offset), new QuasarSender(partNo, 0x0E) ) );
        // Vibrato depth (Offset value, -64 ... +63, 40h = 0)
        partPanel.add(new KnobWidget("Vibrato depth", p, 0, 127, -64, new ParamModel(patch, 97 + offset), new QuasarSender(partNo, 0x0F) ) );
        // Vibrato delay (Offset value, -64 ... +63, 40h = 0)
        partPanel.add(new KnobWidget("Vibrato delay", p, 0, 127, -64, new ParamModel(patch, 98 + offset), new QuasarSender(partNo, 0x10) ) );
        verticalBox.add(partPanel);

        partPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        // Velocity curve
        partPanel.add(new ComboBoxWidget("Velocity curve", p, new ParamModel(patch, 99 + offset), new QuasarSender(partNo, 0x11), QuasarConstants.VELOCITY_CURVES) );
        // Holdpedal (00h = off, 01h = on)
        partPanel.add(new ComboBoxWidget("Holdpedal", p, new ParamModel(patch, 100 + offset), new QuasarSender(partNo, 0x12), QuasarConstants.SWITCH) );
        // Modulation depth
        partPanel.add(new KnobWidget("Mod. depth", p, 0, 127, 0, new ParamModel(patch, 101 + offset), new QuasarSender(partNo, 0x13) ) );
        // Pitch sensivity (00h = -12, 0Ch = 0, 18h = +12)
        partPanel.add(new KnobWidget("Pitch sensivity", p, 0, 24, -12, new ParamModel(patch, 102 + offset), new QuasarSender(partNo, 0x14) ) );
        // Volume mod. sens.(Offset value, -64 ... +63, 40h = 0)
        partPanel.add(new KnobWidget("Vol. mod. sens.", p, 0, 127, -64, new ParamModel(patch, 103 + offset), new QuasarSender(partNo, 0x15) ) );
        // Tone mod. sens.(Offset value, -64 ... +63, 40h = 0)
        partPanel.add(new KnobWidget("Vol. mod. sens.", p, 0, 127, -64, new ParamModel(patch, 104 + offset), new QuasarSender(partNo, 0x16) ) );
        // Portamento time
        partPanel.add(new KnobWidget("Portamento time", p, 0, 127, 0, new ParamModel(patch, 105 + offset), new QuasarSender(partNo, 0x17) ) );
        verticalBox.add(partPanel);

		return verticalBox;
	}

	private void changePerfMode(final int iPerfMode) {
		int activatedParts = QuasarConstants.PERFORMANCE_HELPER[iPerfMode][0];

		for (int count = 1; count <= 4; count++) {
			if (count <= activatedParts) {
				oTabs.setEnabledAt(4 + count, true);
			}
			else {
				oTabs.setEnabledAt(4 + count, false);
			}
		}
		int performanceValue = QuasarConstants.PERFORMANCE_HELPER[iPerfMode][1];

		if (performanceValue > -1) {
			perfValueWidget.setEnabled(true);
			perfValueWidget.setLabel(QuasarConstants.PERF_VALUE_HELPER[performanceValue]);
		}
		else {
			perfValueWidget.setEnabled(false);
			perfValueWidget.setLabel(" ");
		}
	}

	private void changeFX1(final int fx1) {
		int parameterIndex = QuasarConstants.FX1_HELPER[fx1];

		if (parameterIndex > -1) {
			int maxPar = QuasarConstants.FX1_EFFECTS_PARAMETER[parameterIndex].length;

			for (int count = 0; count < fx1ParWidgets.length; count++) {
				if (count < maxPar) {
					fx1ParWidgets[count].setEnabled(true);
					fx1ParWidgets[count].setLabel(QuasarConstants.FX1_EFFECTS_PARAMETER[parameterIndex][count]);
				}
				else {
					fx1ParWidgets[count].setEnabled(false);
					fx1ParWidgets[count].setLabel(" ");
				}
			}
		}
		else {
			for (int count = 0; count < fx1ParWidgets.length; count++) {
				fx1ParWidgets[count].setEnabled(false);
				fx1ParWidgets[count].setLabel(" ");
			}
		}
	}

	private void changeFX2(final int fx2) {
		int parameterIndex = QuasarConstants.FX2_HELPER[fx2];

		if (parameterIndex > -1) {
			int maxPar = QuasarConstants.FX2_EFFECTS_PARAMETER[parameterIndex].length;

			for (int count = 0; count < fx2ParWidgets.length; count++) {
				if (count < maxPar) {
					fx2ParWidgets[count].setEnabled(true);
					fx2ParWidgets[count].setLabel(QuasarConstants.FX2_EFFECTS_PARAMETER[parameterIndex][count]);
				}
				else {
					fx2ParWidgets[count].setEnabled(false);
					fx2ParWidgets[count].setLabel(" ");
				}
			}
		}
		else {
			for (int count = 0; count < fx2ParWidgets.length; count++) {
				fx2ParWidgets[count].setEnabled(false);
				fx2ParWidgets[count].setLabel(" ");
			}
		}
	}

}
