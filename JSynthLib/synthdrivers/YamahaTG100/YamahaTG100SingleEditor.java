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
/*
 * Device class for the Yamaha TG-100
 *
 * YamahaTG100Device.java
 *
 * Created on 31. July 2004, 21:39
 */

package synthdrivers.YamahaTG100;

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
public class YamahaTG100SingleEditor extends PatchEditorFrame {

    // Needed here to disable the "Element 2" parameters
    private static JTabbedPane oTabs;
    private static JPanel element2Panel;

    public YamahaTG100SingleEditor(IPatch patch) {
	    super ("Yamaha TG100 Single Editor", patch);

        oTabs = new JTabbedPane();
        scrollPane.add(oTabs);

        oTabs.add(buildCommonWindow(), "Common");
		for (int i = 1; i <= 2; i++) {
			oTabs.add(buildElementWindow(i), "Element " + i);
		}

        setSize(400, 600);
		pack();
		setVisible(true);
    }

    /**
    * Common parameter tab
    */
	private Container buildCommonWindow() {
		Box verticalBox = Box.createVerticalBox();
		GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5,2,5,2);  // padding
        Patch patch = (Patch) p;

		JPanel commonPanel =  new JPanel();
		commonPanel.setLayout(gridbag);

		// Name
		gbc.gridx = 0;
        gbc.gridy = 0;
		commonPanel.add(new PatchNameWidget("Name", p), gbc);
		// Voice Mode
		ComboBoxWidget voiceModeWidget = new ComboBoxWidget("Voice Mode", p, new TG100Model(patch, 7), new TG100Sender(0x00), TG100Constants.VOICE_MODE);
		voiceModeWidget.addEventListener(new ItemListener() {
			public void itemStateChanged (ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					JComboBox oComboBox = (JComboBox)e.getSource();
					boolean enable = (0 == oComboBox.getSelectedIndex())? false: true;
					oTabs.setEnabledAt(2, enable);
					element2Panel.setEnabled(enable);
				}
			}
		});
		gbc.gridx = 1;
        gbc.gridy = 0;
		commonPanel.add(voiceModeWidget, gbc);

		// Portamento Time
		gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
		commonPanel.add(new ScrollBarWidget("Portamento Time", p, 0, 127, 0, new TG100Model(patch, 12),  new TG100Sender(0x05) ), gbc );
		// Mod LFO Pitch Depth
		gbc.gridy = 2;
		commonPanel.add(new ScrollBarWidget("Mod LFO Pitch Depth", p, 0, 15, 0, new TG100Model(patch, 13),  new TG100Sender(0x06) ), gbc );
		// CAF LFO Pitch Depth
		gbc.gridx = 2;
		gbc.gridwidth = 1;
		commonPanel.add(new ScrollBarWidget("CAF LFO Pitch Depth", p, 0, 15, 0, new TG100Model(patch, 15),  new TG100Sender(0x08) ), gbc );
		commonPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Common",TitledBorder.CENTER,TitledBorder.CENTER));
		verticalBox.add(commonPanel, gbc);

        // *** Element 1 Common Parameters ***
		JPanel element1Panel =  new JPanel();
        element1Panel.setLayout(gridbag);

		// Element 1 Level
		gbc.gridx = 0;
        gbc.gridy = 0;
		element1Panel.add(new ScrollBarWidget("Element 1 Level", p, 0, 127, 0, new TG100Model(patch, 8),  new TG100Sender(0x01) ), gbc );
		// Element 1 Detune
		gbc.gridx = 2;
		element1Panel.add(new ScrollBarWidget("Element 1 Detune", p, 32, 95, -64, new TG100Model(patch, 10),  new TG100Sender(0x03) ), gbc );
		// Element 1 Pitch Rate Scaling
		gbc.gridx = 0;
        gbc.gridy = 1;
		element1Panel.add(new ComboBoxWidget("Element 1 Pitch Rate Scaling", p, new TG100Model(patch, 17), new TG100Sender(0x0A), TG100Constants.PITCH_RATE_SCALING), gbc);
		// Element 1 Pitch Rate Center Note
		gbc.gridx = 1;
		element1Panel.add(new ComboBoxWidget("Element 1 Pitch Rate Center Note", p, new TG100Model(patch, 18), new TG100Sender(0x0B), TG100Constants.NOTES), gbc);
		// Element 1 Note Shift
		gbc.gridx = 2;
		element1Panel.add(new ScrollBarWidget("Element 1 Note Shift", p, 40, 88, -64, new TG100Model(patch, 19),  new TG100Sender(0x0C) ), gbc );
        element1Panel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Element 1",TitledBorder.CENTER,TitledBorder.CENTER));
        verticalBox.add(element1Panel);

		// *** Element 2 Common Parameters ***
		element2Panel =  new JPanel();
        element2Panel.setLayout(gridbag);

		// Element 2 Level
		gbc.gridx = 0;
        gbc.gridy = 0;
		element2Panel.add(new ScrollBarWidget("Element 2 Level", p, 0, 127, 0, new TG100Model(patch, 9),  new TG100Sender(0x02) ), gbc  );
		// Element 2 Detune
		gbc.gridx = 2;
		element2Panel.add(new ScrollBarWidget("Element 2 Detune", p, 32, 95, -64, new TG100Model(patch, 11),  new TG100Sender(0x04) ), gbc  );
		// Element 2 Pitch Rate Scaling
		gbc.gridx = 0;
        gbc.gridy = 1;
		element2Panel.add(new ComboBoxWidget("Element 2 Pitch Rate Scaling", p, new TG100Model(patch, 21), new TG100Sender(0x0E), TG100Constants.PITCH_RATE_SCALING), gbc );
		// Element 2 Pitch Rate Center Note
		gbc.gridx = 1;
		element2Panel.add(new ComboBoxWidget("Element 2 Pitch Rate Center Note", p, new TG100Model(patch, 22), new TG100Sender(0x0F), TG100Constants.NOTES), gbc );
		// Element 2 Note Shift
		gbc.gridx = 2;
		element2Panel.add(new ScrollBarWidget("Element 2 Note Shift", p, 40, 88, -64, new TG100Model(patch, 20),  new TG100Sender(0x0D) ), gbc  );
		element2Panel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Element 2",TitledBorder.CENTER,TitledBorder.CENTER));
		verticalBox.add(element2Panel);

		return verticalBox;
	}

	/**
    * Common element tab
    */
	private Container buildElementWindow(final int element) {
	    int offset = TG100Constants.ELEMENT_SIZE * (element - 1);

		Box verticalBox = Box.createVerticalBox();

		JPanel oPanel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		// Waveform
		oPanel1.add(new ScrollBarWidget("Waveform", p, 0, 139, 0, new TG100Model(patch, 31 + offset, true),  new TG100Sender(0x18 + offset) ) );
		// EG AR
		oPanel1.add(new ScrollBarWidget("EG AR", p, 49, 79, -64, new TG100Model(patch, 33 + offset),  new TG100Sender(0x1A + offset) ) );
		// EG RR
		oPanel1.add(new ScrollBarWidget("EG RR", p, 49, 79, -64, new TG100Model(patch, 34 + offset),  new TG100Sender(0x1B + offset) ) );

		verticalBox.add(oPanel1);

		// Level Scaling Break Point 1
		// Level Scaling Break Point 2
		// Level Scaling Break Point 3
		// Level Scaling Break Point 4
		// Level Scaling Offset 1
		// Level Scaling Offset 2
		// Level Scaling Offset 3
		// Level Scaling Offset 4
		// Panpot
		// LFO Speed
		// LFO Delay
		// LFO Pitch Mod Depth
		// LFO Amp Mod Depth
		// Pitch LFO Wave
		// P-EG Range
		// P-EG Velocity Switch
		// P-EG LO
/*
		JPanel envelopePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

		// P-EG R1
		// P-EG R2
		// P-EG R3
		// P-EG RR
		// P-EG L1
		// P-EG L2
		// P-EG L3
		// P-EG RL
		EnvelopeWidget pitchEnvelope = new EnvelopeWidget("  Envelope", p, new EnvelopeWidget.Node [] {
		    new EnvelopeWidget.Node(0, 0, null, 0, 0, null, 0, false, null, null, null, null),
        	new EnvelopeWidget.Node(0, 63, new TG100Model(patch, 57 + offset),
        	                        0, 127, new TG100Model(patch, 62 + offset),
        	                        -64, true, new TG100Sender(0x3E + offset), new TG100Sender(0x43 + offset),
        	                        "P-EG R1","P-EG L1"),
            new EnvelopeWidget.Node(0, 63, new TG100Model(patch, 58 + offset),
        	                        0, 127, new TG100Model(patch, 63 + offset),
        	                        -64, true, new TG100Sender(0x3F + offset), new TG100Sender(0x44 + offset),
        	                        "P-EG R2","P-EG L2"),
            new EnvelopeWidget.Node(0, 63, new TG100Model(patch, 59 + offset),
        	                        0, 127, new TG100Model(patch, 64 + offset),
        	                        -64, true, new TG100Sender(0x40 + offset), new TG100Sender(0x45 + offset),
        	                        "P-EG R3","P-EG L3"),
            new EnvelopeWidget.Node(0, 63, new TG100Model(patch, 60 + offset),
        	                        0, 127, new TG100Model(patch, 65 + offset),
        	                        -64, true, new TG100Sender(0x41 + offset), new TG100Sender(0x46 + offset),
        	                        "P-EG RR","P-EG RL"),
        });
        envelopePanel.add(pitchEnvelope);

        verticalBox.add(envelopePanel);
        */

		// Veloctiy Curve

		return verticalBox;
	}

}
