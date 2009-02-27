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

package synthdrivers.YamahaTG100;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.jsynthlib.core.ComboBoxWidget;
import org.jsynthlib.core.KnobWidget;
import org.jsynthlib.core.Patch;
import org.jsynthlib.core.PatchEditorFrame;
import org.jsynthlib.core.PatchNameWidget;
import org.jsynthlib.core.ScrollBarLookupWidget;
import org.jsynthlib.core.ScrollBarWidget;



/**
 *
 * @author  Joachim Backhaus
 * @version $Id$
 */
public class YamahaTG100SingleEditor extends PatchEditorFrame {

    // Needed here to disable the "Element 2" parameters
    private static JTabbedPane oTabs;
    private static JPanel element2Panel;

    public YamahaTG100SingleEditor(Patch iPatch) {
        super ("Yamaha TG100 Single Editor", iPatch);

        Patch patch = (Patch) iPatch;

        oTabs = new JTabbedPane();
        scrollPane.add(oTabs);

        oTabs.add(buildCommonWindow(patch), "Common");
        for (int i = 1; i <= 2; i++) {
            oTabs.add(buildElementWindow(i, patch), "Element " + i);
        }

        // Needed for initial tab enabling/disabling
        changeVoiceMode( (int)patch.sysex[7] );

        setSize(500, 600);
        //pack();
        //setVisible(true);
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

        JPanel commonPanel =  new JPanel(new BorderLayout());

        JPanel tempPanel = new JPanel();
        // Name
        tempPanel.add(new PatchNameWidget("Name", p) );
        // Voice Mode
        ComboBoxWidget voiceModeWidget = new ComboBoxWidget("Voice Mode", p, new TG100Model(patch, 7), new TG100Sender(0x00), TG100Constants.VOICE_MODE);
        voiceModeWidget.addEventListener(new ItemListener() {
            public void itemStateChanged (ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    JComboBox oComboBox = (JComboBox)e.getSource();

                    changeVoiceMode(oComboBox.getSelectedIndex() );
                }
            }
        });
        tempPanel.add(voiceModeWidget);
        // Dirty spacing trick
        tempPanel.add(new JLabel("      "));
        // Portamento Time
        tempPanel.add(new KnobWidget("Portamento", p, 0, 127, 0, new TG100Model(patch, 12),  new TG100Sender(0x05) ) );
        commonPanel.add(tempPanel, BorderLayout.NORTH);

        tempPanel = new JPanel();
        // Mod LFO Pitch Depth
        tempPanel.add(new ScrollBarWidget("Mod LFO pitch depth", p, 0, 15, 0, new TG100Model(patch, 13),  new TG100Sender(0x06) ) );
        // CAF LFO Pitch Depth
        tempPanel.add(new ScrollBarWidget("CAF LFO pitch depth", p, 0, 15, 0, new TG100Model(patch, 15),  new TG100Sender(0x08) ) );
        commonPanel.add(tempPanel, BorderLayout.CENTER);
        commonPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Common",TitledBorder.CENTER,TitledBorder.CENTER));
        verticalBox.add(commonPanel);

        // *** Element 1 Common Parameters ***
        JPanel element1Panel =  new JPanel();
        element1Panel.setLayout(gridbag);

        // Element 1 Level
        gbc.gridx = 0;
        gbc.gridy = 0;
        element1Panel.add(new ScrollBarWidget("Element 1 level", p, 0, 127, 0, new TG100Model(patch, 8),  new TG100Sender(0x01) ), gbc );
        // Element 1 Detune
        gbc.gridx = 1;
        element1Panel.add(new ScrollBarWidget("Element 1 detune", p, 32, 95, -64, new TG100Model(patch, 10),  new TG100Sender(0x03) ), gbc );
        // Element 1 Pitch Rate Scaling
        gbc.gridx = 0;
        gbc.gridy = 1;
        element1Panel.add(new ComboBoxWidget("Element 1 pitch rate scaling", p, new TG100Model(patch, 17), new TG100Sender(0x0A), TG100Constants.PITCH_RATE_SCALING), gbc);
        // Element 1 Pitch Rate Center Note
        gbc.gridx = 1;
        element1Panel.add(new ComboBoxWidget("Element 1 pitch rate center note", p, new TG100Model(patch, 18), new TG100Sender(0x0B), TG100Constants.NOTES), gbc);
        // Element 1 Note Shift
        gbc.gridy = 2;
        element1Panel.add(new ScrollBarWidget("Element 1 note shift", p, 40, 88, -64, new TG100Model(patch, 19),  new TG100Sender(0x0C) ), gbc );
        element1Panel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Element 1",TitledBorder.CENTER,TitledBorder.CENTER));
        verticalBox.add(element1Panel);

        // *** Element 2 Common Parameters ***
        element2Panel =  new JPanel();
        element2Panel.setLayout(gridbag);

        // Element 2 Level
        gbc.gridx = 0;
        gbc.gridy = 0;
        element2Panel.add(new ScrollBarWidget("Element 2 level", p, 0, 127, 0, new TG100Model(patch, 9),  new TG100Sender(0x02) ), gbc  );
        // Element 2 Detune
        gbc.gridx = 1;
        element2Panel.add(new ScrollBarWidget("Element 2 detune", p, 32, 95, -64, new TG100Model(patch, 11),  new TG100Sender(0x04) ), gbc  );
        // Element 2 Pitch Rate Scaling
        gbc.gridx = 0;
        gbc.gridy = 1;
        element2Panel.add(new ComboBoxWidget("Element 2 pitch rate scaling", p, new TG100Model(patch, 21), new TG100Sender(0x0E), TG100Constants.PITCH_RATE_SCALING), gbc );
        // Element 2 Pitch Rate Center Note
        gbc.gridx = 1;
        element2Panel.add(new ComboBoxWidget("Element 2 pitch rate center note", p, new TG100Model(patch, 22), new TG100Sender(0x0F), TG100Constants.NOTES), gbc );
        // Element 2 Note Shift
        gbc.gridy = 2;
        element2Panel.add(new ScrollBarWidget("Element 2 note shift", p, 40, 88, -64, new TG100Model(patch, 20),  new TG100Sender(0x0D) ), gbc  );
        element2Panel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Element 2",TitledBorder.CENTER,TitledBorder.CENTER));
        verticalBox.add(element2Panel);

        return verticalBox;
    }

    /**
     * Common element tab
     */
    private Container buildElementWindow(final int element, Patch patch) {
        int offset = TG100Constants.ELEMENT_SIZE * (element - 1);

        Box verticalBox = Box.createVerticalBox();
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5,2,5,2);  // padding

        JPanel elementPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        // Waveform
        elementPanel.add(new KnobWidget("Waveform", p, 0, 139, 0, new TG100Model(patch, 31 + offset, true),  new TG100Sender(0x18 + offset) ) );
        // EG AR
        elementPanel.add(new KnobWidget("EG AR", p, 49, 79, -64, new TG100Model(patch, 33 + offset),  new TG100Sender(0x1A + offset) ) );
        // EG RR
        elementPanel.add(new KnobWidget("EG RR", p, 49, 79, -64, new TG100Model(patch, 34 + offset),  new TG100Sender(0x1B + offset) ) );
        // Panpot
        elementPanel.add(new ComboBoxWidget("Panpot", p,
                new TG100Model(patch, 47 + offset),
                new TG100Sender(0x28 + offset), TG100Constants.PANPOT ) );
        // Veloctiy Curve
        elementPanel.add(new ComboBoxWidget("Velocity curve", p,
                new TG100Model(patch, 66 + offset),
                new TG100Sender(0x3B + offset),
                TG100Constants.VELOCITY_CURVE ) );
        elementPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Element " + element,TitledBorder.CENTER,TitledBorder.CENTER));
        verticalBox.add(elementPanel);

        JPanel levelScalingPanel = new JPanel();
        levelScalingPanel.setLayout(gridbag);


        // Level Scaling Break Point 1
        gbc.gridx = 0;
        gbc.gridy = 0;
        levelScalingPanel.add(new ScrollBarLookupWidget(  "Breakpoint 1", p,
                new TG100Model(patch, 35 + offset),
                new TG100Sender(0x1C + offset), TG100Constants.NOTES ), gbc );
        // Level Scaling Offset 1
        gbc.gridx = 1;
        levelScalingPanel.add(new ScrollBarWidget(  "Offset 1", p, 0, 256, -128,
                new TG100Model(patch, 39 + offset, true),
                new TG100Sender(0x20 + offset)), gbc );

        // Level Scaling Break Point 2
        gbc.gridx = 0;
        gbc.gridy = 1;
        levelScalingPanel.add(new ScrollBarLookupWidget(  "Breakpoint 2", p,
                new TG100Model(patch, 36 + offset),
                new TG100Sender(0x1D + offset), TG100Constants.NOTES ), gbc );
        // Level Scaling Offset 2
        gbc.gridx = 1;
        levelScalingPanel.add(new ScrollBarWidget(  "Offset 2", p, 0, 256, -128,
                new TG100Model(patch, 41 + offset, true),
                new TG100Sender(0x22 + offset)), gbc );

        // Level Scaling Break Point 3
        gbc.gridx = 0;
        gbc.gridy = 2;
        levelScalingPanel.add(new ScrollBarLookupWidget(  "Breakpoint 3", p,
                new TG100Model(patch, 37 + offset),
                new TG100Sender(0x1E + offset), TG100Constants.NOTES ), gbc );
        // Level Scaling Offset 3
        gbc.gridx = 1;
        levelScalingPanel.add(new ScrollBarWidget(  "Offset 3", p, 0, 256, -128,
                new TG100Model(patch, 43 + offset, true),
                new TG100Sender(0x24 + offset)), gbc );

        // Level Scaling Break Point 4
        gbc.gridx = 0;
        gbc.gridy = 3;
        levelScalingPanel.add(new ScrollBarLookupWidget(  "Breakpoint 4", p,
                new TG100Model(patch, 38 + offset),
                new TG100Sender(0x1F + offset), TG100Constants.NOTES ), gbc );
        // Level Scaling Offset 4
        gbc.gridx = 1;
        levelScalingPanel.add(new ScrollBarWidget(  "Offset 4", p, 0, 256, -128,
                new TG100Model(patch, 45 + offset, true),
                new TG100Sender(0x26 + offset)), gbc );

        levelScalingPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Level scaling",TitledBorder.CENTER,TitledBorder.CENTER));
        verticalBox.add(levelScalingPanel);


        JPanel lfoEGPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JPanel lfoPanel = new JPanel();
        lfoPanel.setLayout(gridbag);

        // LFO Speed
        gbc.gridx = 0;
        gbc.gridy = 0;
        lfoPanel.add(new KnobWidget(    "Speed", p, 0, 7, 0,
                new TG100Model(patch, 48 + offset),
                new TG100Sender(0x29 + offset) ), gbc );
        // LFO Delay
        gbc.gridx = 1;
        lfoPanel.add(new KnobWidget(    "Delay", p, 0, 127, 0,
                new TG100Model(patch, 49 + offset),
                new TG100Sender(0x2A + offset) ), gbc );
        // Pitch LFO Wave
        gbc.gridx = 2;
        lfoPanel.add(new ComboBoxWidget(     "Pitch LFO wave", p,
                new TG100Model(patch, 53 + offset),
                new TG100Sender(0x2E + offset),
                TG100Constants.PITCH_LFO_WAVE ), gbc );
        // LFO Pitch Mod Depth
        gbc.gridx = 0;
        gbc.gridy = 1;
        lfoPanel.add(new KnobWidget(    "Pitch mod depth", p, 0, 15, 0,
                new TG100Model(patch, 51 + offset),
                new TG100Sender(0x2C + offset) ), gbc );
        // LFO Amp Mod Depth
        gbc.gridx = 1;
        lfoPanel.add(new KnobWidget(    "Amp mod depth", p, 0, 7, 0,
                new TG100Model(patch, 52 + offset),
                new TG100Sender(0x2D + offset) ), gbc );
        lfoPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"LFO",TitledBorder.CENTER,TitledBorder.CENTER));
        lfoEGPanel.add(lfoPanel);


        JPanel pEGPanel = new JPanel();
        pEGPanel.setLayout(gridbag);

        // P-EG Range
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        pEGPanel.add(new ComboBoxWidget(     "Range", p,
                new TG100Model(patch, 54 + offset),
                new TG100Sender(0x2F + offset),
                TG100Constants.P_EG_RANGE ), gbc );
        // P-EG Velocity Switch
        // Can't use checkbox because the values are "on", "off" instead of "off", "on"
        gbc.gridx = 2;
        pEGPanel.add(new ComboBoxWidget(     "Velocity switch", p,
                new TG100Model(patch, 55 + offset),
                new TG100Sender(0x30 + offset),
                TG100Constants.SWITCH ), gbc );
        // P-EG Rate Scaling
        gbc.gridx = 4;
        pEGPanel.add(new KnobWidget(    "Rate scaling", p, 0, 7, 0,
                new TG100Model(patch, 56 + offset),
                new TG100Sender(0x31 + offset) ), gbc );


        // P-EG R1
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        pEGPanel.add(new KnobWidget(    "Rate 1", p, 0, 63, 0,
                new TG100Model(patch, 57 + offset),
                new TG100Sender(0x32 + offset) ), gbc );
        // P-EG R2
        gbc.gridx = 2;
        pEGPanel.add(new KnobWidget(    "Rate 2", p, 0, 63, 0,
                new TG100Model(patch, 58 + offset),
                new TG100Sender(0x33 + offset) ), gbc );
        // P-EG R3
        gbc.gridx = 3;
        pEGPanel.add(new KnobWidget(    "Rate 3", p, 0, 63, 0,
                new TG100Model(patch, 59 + offset),
                new TG100Sender(0x34 + offset) ), gbc );
        // P-EG RR
        gbc.gridx = 4;
        pEGPanel.add(new KnobWidget(    "Rate R", p, 0, 63, 0,
                new TG100Model(patch, 60 + offset),
                new TG100Sender(0x35 + offset) ), gbc );
        // P-EG LO
        gbc.gridx = 0;
        gbc.gridy = 2;
        pEGPanel.add(new KnobWidget(    "LO", p, 0, 127, -64,
                new TG100Model(patch, 61 + offset),
                new TG100Sender(0x36 + offset) ), gbc );
        // P-EG L1
        gbc.gridx = 1;
        pEGPanel.add(new KnobWidget(    "Level 1", p, 0, 127, -64,
                new TG100Model(patch, 62 + offset),
                new TG100Sender(0x37 + offset) ), gbc );
        // P-EG L2
        gbc.gridx = 2;
        pEGPanel.add(new KnobWidget(    "Level 2", p, 0, 127, -64,
                new TG100Model(patch, 63 + offset),
                new TG100Sender(0x38 + offset) ), gbc );
        // P-EG L3
        gbc.gridx = 3;
        pEGPanel.add(new KnobWidget(    "Level 3", p, 0, 127, -64,
                new TG100Model(patch, 64 + offset),
                new TG100Sender(0x39 + offset) ), gbc );
        // P-EG RL
        gbc.gridx = 4;
        pEGPanel.add(new KnobWidget(    "Release level", p, 0, 127, -64,
                new TG100Model(patch, 65 + offset),
                new TG100Sender(0x3A + offset) ), gbc );

        pEGPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Pitch envelope (P-EG)",TitledBorder.CENTER,TitledBorder.CENTER));
        lfoEGPanel.add(pEGPanel);

        verticalBox.add(lfoEGPanel);

        return verticalBox;
    }

    private void changeVoiceMode(final int index) {
        boolean enable = (0 == index)? false: true;
        oTabs.setEnabledAt(2, enable);
        element2Panel.setEnabled(enable);
        Component[] elem2components = element2Panel.getComponents();

        for (int count = 0; count < elem2components.length; count++) {
            elem2components[count].setEnabled(enable);
        }
    }
}
