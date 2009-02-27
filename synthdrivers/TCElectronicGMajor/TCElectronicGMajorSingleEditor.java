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

package synthdrivers.TCElectronicGMajor;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import core.CheckBoxWidget;
import core.ComboBoxWidget;
import core.ErrorMsg;
import core.Patch;
import core.PatchEdit;
import core.PatchEditorFrame;
import core.PatchNameWidget;
import core.SysexWidget;

class TCElectronicGMajorSingleEditor extends PatchEditorFrame {

    private final String[] freqString = new String[] {"40.97", "42.17", "43.40", "44.67", "45.97", "47.32", "48.70", "50.12", "51.58", "53.09", "54.64", "56.23", "57.88", "59.57", "61.31", "63.10", "64.94", "66.83", "68.79", "70.79", "72.86", "74.99", "77.18", "79.43", "81.75", "84.14", "86.60", "89.13", "91.73", "94.41", "97.16", "100.0", "102.9", "105.9", "109.0", "112.2", "115.5", "118.9", "122.3", "125.9", "129.6", "133.4", "137.2", "141.3", "145.4", "149.6", "154.0", "158.5", "163.1", "167.9", "172.8", "177.8", "183.0", "188.4", "193.9", "199.5", "205.4", "211.3", "217.5", "223.9", "230.4", "237.1", "244.1", "251.2", "258.5", "266.1", "273.8", "281.8", "290.1", "298.5", "307.3", "316.2", "325.5", "335.0", "344.7", "354.8", "365.2", "375.8", "386.8", "398.1", "409.7", "421.7", "434.0", "446.7", "459.7", "473.2", "487.0", "501.2", "515.8", "530.9", "546.4", "562.3", "578.8", "595.7", "613.1", "631.0", "649.4", "668.3", "687.9", "707.9", "728.6", "749.9", "771.8", "794.3", "817.5", "841.4", "866.0", "891.3", "917.3", "944.1", "971.6", "1.00k", "1.03k", "1.06k", "1.09k", "1.12k", "1.15k", "1.19k", "1.22k", "1.26k", "1.30k", "1.33k", "1.37k", "1.41k", "1.45k", "1.50k", "1.54k", "1.58k", "1.63k", "1.68k", "1.73k", "1.78k", "1.83k", "1.88k", "1.94k", "2.00k", "2.05k", "2.11k", "2.18k", "2.24k", "2.30k", "2.37k", "2.44k", "2.51k", "2.59k", "2.66k", "2.74k", "2.82k", "2.90k", "2.99k", "3.07k", "3.16k", "3.25k", "3.35k", "3.45k", "3.55k", "3.65k", "3.76k", "3.87k", "3.98k", "4.10k", "4.22k", "4.34k", "4.47k", "4.60k", "4.73k", "4.87k", "5.01k", "5.16k", "5.31k", "5.46k", "5.62k", "5.79k", "5.96k", "6.13k", "6.31k", "6.49k", "6.68k", "6.88k", "7.08k", "7.29k", "7.50k", "7.72k", "7.94k", "8.18k", "8.41k", "8.66k", "8.91k", "9.17k", "9.44k", "9.72k", "10.0k", "10.3k", "10.6k", "10.9k", "11.2k", "11.5k", "11.9k", "12.2k", "12.6k", "13.0k", "13.3k", "13.7k", "14.1k", "14.5k", "15.0k", "15.4k", "15.8k", "16.3k", "16.8k", "17.3k", "17.8k", "18.3k", "18.8k", "19.4k", "20.0k", "Off"};

    private final String[] octString = new String [] {"0.2", "0.25", "0.32", "0.4", "0.5", "0.63", "0.8", "1.0", "1.25", "1.6", "2.0", "2.5", "3.2", "4.0"};

    private final String[] freqMaxString = new String[] {"1.00", "1.12", "1.26", "1.41", "1.58", "1.78", "2.00", "2.24", "2.51", "2.82", "3.16", "3.55", "3.98", "4.47", "5.01", "5.62", "6.31", "7.08", "7.94", "8.91", "10.0"};

private final String[] speedString = new String[] {"0.050", "0.052", "0.053", "0.055", "0.056", "0.058", "0.060", "0.061", "0.063", "0.065", "0.067", "0.069", "0.071", "0.073", "0.075", "0.077", "0.079", "0.082", "0.084", "0.087", "0.089", "0.092", "0.094", "0.097", "0.100", "0.103", "0.106", "0.109", "0.112", "0.115", "0.119", "0.122", "0.126", "0.130", "0.133", "0.137", "0.141", "0.145", "0.150", "0.154", "0.158", "0.163", "0.168", "0.173", "0.178", "0.183", "0.188", "0.194", "0.200", "0.205", "0.211", "0.218", "0.224", "0.230", "0.237", "0.244", "0.251", "0.259", "0.266", "0.274", "0.282", "0.290", "0.299", "0.307", "0.316", "0.325", "0.335", "0.345", "0.355", "0.365", "0.376", "0.387", "0.398", "0.410", "0.422", "0.434", "0.447", "0.460", "0.473", "0.487", "0.501", "0.516", "0.531", "0.546", "0.562", "0.579", "0.596", "0.613", "0.631", "0.649", "0.668", "0.688", "0.708", "0.729", "0.750", "0.772", "0.794", "0.818", "0.841", "0.866", "0.891", "0.917", "0.944", "0.972", "1.00", "1.03", "1.06", "1.09", "1.12", "1.15", "1.19", "1.22", "1.26", "1.30", "1.33", "1.37", "1.41", "1.45", "1.50", "1.54", "1.58", "1.63", "1.68", "1.73", "1.78", "1.83", "1.88", "1.94", "2.00", "2.05", "2.11", "2.18", "2.24", "2.30", "2.37", "2.44", "2.51", "2.59", "2.66", "2.74", "2.82", "2.90", "2.99", "3.07", "3.16", "3.25", "3.35", "3.45", "3.55", "3.65", "3.76", "3.87", "3.98", "4.10", "4.22", "4.34", "4.47", "4.60", "4.73", "4.87", "5.01", "5.16", "5.31", "5.46", "5.62", "5.79", "5.96", "6.13", "6.31", "6.49", "6.68", "6.88", "7.08", "7.29", "7.50", "7.72", "7.94", "8.18", "8.41", "8.66", "8.91", "9.17", "9.44", "9.72", "10.00", "10.29", "10.59", "10.90", "11.22", "11.55", "11.89", "12.23", "12.59", "12.96", "13.34", "13.72", "14.13", "14.54", "14.96", "15.40", "15.85", "16.31", "16.79", "17.28", "17.78", "18.30", "18.84", "19.39", "19.95"};

private final String[] filterHiCutString = new String[] {"158.5", "177.8", "199.5", "223.9", "251.2", "281.8", "316.2", "354.8", "398.1", "446.7", "501.2", "562.3", "631.0", "707.9", "794.3", "891.3", "1.00k", "1.12k", "1.26k", "1.41k", "1.58k", "1.78k", "2.00k", "2.24k", "2.51k", "2.82k", "3.16k", "3.55k", "3.98k", "4.47k", "5.01k", "5.62k", "6.31k", "7.08k", "7.94k", "8.91k", "10.0k", "11.2k", "12.6k", "14.1k"};

private final String[] tempoString = new String[] {"Ignored", "1", "1/2D", "1/2", "1/2T", "1/4D", "1/4", "1/4T", "1/8D", "1/8", "1/8T", "1/16D", "1/16", "1/16T", "1/32D", "1/32", "1/32T"};

private final String[] chorusHiCutString = new String[] {"19.95", "22.39", "25.12", "28.18", "31.62", "35.48", "39.81", "44.67", "50.12", "56.23", "63.10", "70.79", "79.43", "89.13", "100.0", "112.2", "125.9", "141.3", "158.5", "177.8", "199.5", "223.9", "251.2", "281.8", "316.2", "354.8", "398.1", "446.7", "501.2", "562.3", "631.0", "707.9", "794.3", "891.3", "1.00k", "1.12k", "1.26k", "1.41k", "1.58k", "1.78k", "2.00k", "2.24k", "2.51k", "2.82k", "3.16k", "3.55k", "3.98k", "4.47k", "5.01k", "5.62k", "6.31k", "7.08k", "7.94k", "8.91k", "10.0k", "11.2k", "12.6k", "14.1k", "15.8k", "17.8k", "Off"};

private final String[] delayFBHiCutString = new String[] {"2.00k", "2.24k", "2.51k", "2.82k", "3.16k", "3.55k", "3.98k", "4.47k", "5.01k", "5.62k", "6.31k", "7.08k", "7.94k", "8.91k", "10.0k", "11.2k", "12.6k", "14.1k", "15.8k", "17.8k", "Off"};

private final String[] delayLoCutString = new String[] {"Off", "22.39", "25.12", "28.18", "31.62", "35.48", "39.81", "44.67", "50.12", "56.23", "63.10", "70.79", "79.43", "89.13", "100.0", "112.2", "125.9", "141.3", "158.5", "177.8", "199.5", "223.9", "251.2", "281.8", "316.2", "354.8", "398.1", "446.7", "501.2", "562.3", "631.0", "707.9", "794.3", "891.3", "1.00k", "1.12k", "1.26k", "1.41k", "1.58k", "1.78k", "2.00k"};

private final String[] delayReleaseString = new String[] {"20", "30", "50", "70", "100", "140", "200", "300", "500", "700", "1.0s"};

private Patch patch;

    public TCElectronicGMajorSingleEditor(Patch iPatch) {
        super ("TC Electronic G-Major Single Editor", iPatch);

        patch = (Patch) iPatch;

        Box box = Box.createVerticalBox();
        box.add(buildTopPanel());

        JTabbedPane oTabs = new JTabbedPane();
        oTabs.setPreferredSize(new Dimension(640, 240));
        oTabs.add(buildNGPanel(), "NG/EQ");
        oTabs.add(buildCmpPanel(), "Comp");
        oTabs.add(buildFilterPanel(), "Filter/Mod");
        oTabs.add(buildPitchPanel(), "Pitch");
        oTabs.add(buildChorusPanel(), "Cho/Fla");
        oTabs.add(buildDelayPanel(), "Delay");
        oTabs.add(buildReverbPanel(), "Reverb");
        oTabs.add(buildLvlPanel(), "Levels/Relay");
        box.add(oTabs);
        scrollPane.add(box);

        box.add(buildBottomPanel());

        pack();

    }

    /**
     * This is addWidget from PatchEditorFrame, without the slider bank functionality.
     * All the widgets in the filter-, pitch-, chorus,- delay,- and reverb panel are created dynamically depending on the effect type.
     * This means that the widgetList doesn't contain all available widgets or becomes very crowded with double registered widgets.
     * A lot of widgets share the same offset but have different values to choose from and different min and max values.
     * I could create all available widgets at once, register them in the widget list and change the visibility, comboboxlist, min and max values
     * depending on the effect type chosen. Maybe I will do that some time.
     * Creating widgets dynamically is much more lightweight, but I have to sacrifice the slider bank functionality.
     */
    protected void addWidget(JComponent parent, SysexWidget widget,
                             int gridx, int gridy, int gridwidth, int gridheight,
                             int anchor, int fill,
                             int slidernum) {
        try {
            gbc.gridx = gridx;
            gbc.gridy = gridy;
            gbc.gridwidth = gridwidth;
            gbc.gridheight = gridheight;
            gbc.anchor = anchor;
            gbc.fill = fill;
            parent.add(widget, gbc);

        } catch (Exception e) {
            ErrorMsg.reportStatus(e);
        }
    }

    private Container buildTopPanel() {
        // Top panel
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JPanel pnl = new JPanel();
        pnl.setLayout(gridbag);
        gbc.gridx = 0;
        gbc.gridy = 0;
        pnl.add(new PatchNameWidget("Patch Name ", patch), gbc);

        addWidget(pnl, new ComboBoxWidget("Tap Tempo (ms)", patch,
                new TCModel(patch, 161, 100),
                new TCSender(patch, 161, 100),
                TCElectronicGMajorUtil.genString(100, 3000)),
                1, 0, 1, 1, gbc.anchor, gbc.fill, 0);

        gbc.gridx = 2;
        gbc.gridy = 0;
        JButton modButton = new JButton();
        modButton.setText("Mod");
        modButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TCElectronicGMajorModDialog.showDialog(PatchEdit.getInstance(), null, patch);
            }
        });

        pnl.add(modButton, gbc);

        return pnl;
    }

    private Container buildLvlPanel() {
        // Level panel
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;

        JPanel pnl = new JPanel();
        pnl.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
            "Levels All/Relay 1+2",TitledBorder.CENTER,TitledBorder.CENTER));
        pnl.setLayout(gridbag);

        addWidget(pnl, new CheckBoxWidget("Relay 1", patch,
                new TCBitModel(patch, 157, TCElectronicGMajorConst.RELAY1_MASK),
                new TCBitSender(patch, 157, TCElectronicGMajorConst.RELAY1_MASK)),
                0, 0, 1, 1, gbc.anchor, gbc.fill, 0);
        addWidget(pnl, new CheckBoxWidget("Relay 2", patch,
                new TCBitModel(patch, 157, TCElectronicGMajorConst.RELAY2_MASK),
                new TCBitSender(patch, 157, TCElectronicGMajorConst.RELAY2_MASK)),
                0, 1, 1, 1, gbc.anchor, gbc.fill, 0);
        addWidget(pnl, new ComboBoxWidget("Preset OutLvl (dB)", patch,
            new TCBitModel(patch, 157, TCElectronicGMajorConst.PRESETOUT_MASK),
            new TCBitSender(patch, 157, TCElectronicGMajorConst.PRESETOUT_MASK),
            TCElectronicGMajorUtil.genString(-100, 0)),
            0, 2, 1, 1, gbc.anchor, gbc.fill, 0);
        addWidget(pnl, new ComboBoxWidget("Routing", patch,
            new TCBitModel(patch, 157, TCElectronicGMajorConst.ROUTING_MASK),
            new TCBitSender(patch, 157, TCElectronicGMajorConst.ROUTING_MASK),
            new String [] {"Serial", "SemiPar", "Parallel"}),
            0, 3, 1, 1, gbc.anchor, gbc.fill, 0);

        return pnl;
    }

    private Container buildNGPanel() {
        // Noise Gate and EQ panel
        Box box = Box.createHorizontalBox();
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;

        //NG Panel
        JPanel ngPanel = new JPanel();
        ngPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
            "Noise Gate",TitledBorder.CENTER,TitledBorder.CENTER));
        ngPanel.setLayout(gridbag);
        addWidget(ngPanel, new ComboBoxWidget("Mode", patch,
            new TCModel(patch,549),
            new TCSender(patch, 549),
            new String [] {"Soft", "Hard"}),
            0, 0, 1, 1, gbc.anchor, gbc.fill, 0);
        addWidget(ngPanel, new ComboBoxWidget("Treshold (dB)", patch,
            new TCModel(patch,553,-60),
            new TCSender(patch,553,-60),
            TCElectronicGMajorUtil.genString(-60, 0)),
            0, 1, 1, 1, gbc.anchor, gbc.fill, 0);
        addWidget(ngPanel, new ComboBoxWidget("Damp (dB)", patch,
            new TCModel(patch,557),
            new TCSender(patch, 557),
            TCElectronicGMajorUtil.genString(0, 90)),
            0, 2, 1, 1, gbc.anchor, gbc.fill, 0);
        addWidget(ngPanel, new ComboBoxWidget("Release (dB/s)", patch,
            new TCModel(patch,561,3),
            new TCSender(patch, 561,3),
            TCElectronicGMajorUtil.genString(3, 200)),
            0, 3, 1, 1, gbc.anchor, gbc.fill, 0);

        //EQ Panel
        JPanel eqPanel = new JPanel();
        eqPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
            "EQ",TitledBorder.CENTER,TitledBorder.CENTER));
        eqPanel.setLayout(gridbag);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;

        gbc.gridy = 0;
        for (int i = 1; i < 4; i++) {
            gbc.gridx = i;
            eqPanel.add(new JLabel("EQ"+ (i)), gbc);
        }
        gbc.gridx = 0;
        gbc.gridy = 1;
        eqPanel.add(new JLabel("Freq (Hz)"), gbc);
        gbc.gridy++;
        eqPanel.add(new JLabel("Gain (dB)"), gbc);
        gbc.gridy++;
        eqPanel.add(new JLabel("Width (Oct)"), gbc);

        //EQ freq
        for (int i = 0; i < 3; i++) {
            addWidget(eqPanel, new ComboBoxWidget("", patch,
                new TCModel(patch, 569 + 12*i, 25),
                new TCSender(patch, 569 + 12*i, 25),
                freqString),
                1 + i, 1, 1, 1, gbc.anchor, gbc.fill, 0);
        }
        //EQ Gain
        for (int i = 0; i < 3; i++) {
            addWidget(eqPanel, new ComboBoxWidget("", patch,
                new TCModel(patch, 573 + 12*i, -12),
                new TCSender(patch, 573 + 12*i, -12),
                TCElectronicGMajorUtil.genString(-12, 12)),
                1 + i, 2, 1, 1, gbc.anchor, gbc.fill, 0);
        }
        //EQ Width
        for (int i = 0; i < 3; i++) {
            addWidget(eqPanel, new ComboBoxWidget("", patch,
                new TCModel(patch, 577 + 12*i, 3),
                new TCSender(patch, 577 + 12*i, 3),
                octString),
                1 + i, 3, 1, 1, gbc.anchor, gbc.fill, 0);
        }

        box.add(ngPanel);
        box.add(eqPanel);

        return box;
    }

    private Container buildCmpPanel() {
        // Compressor panel
        Box box = Box.createVerticalBox();
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;

        JPanel pnl = new JPanel();
        pnl.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
            "Compressor",TitledBorder.CENTER,TitledBorder.CENTER));
        pnl.setLayout(gridbag);
        addWidget(pnl, new ComboBoxWidget("Treshold (dB)", patch,
            new TCModel(patch, 165, -40),
            new TCSender(patch, 165, -40),
            TCElectronicGMajorUtil.genString(-40, 0)),
            0, 0, 1, 1, gbc.anchor, gbc.fill, 0);
        addWidget(pnl, new ComboBoxWidget("Ratio", patch,
            new TCModel(patch, 169),
            new TCSender(patch, 169),
            new String [] {"off", "1.12 : 1", "1.25 : 1", "1.40 : 1", "1.60 : 1", "1.80 : 1", "2.0 : 1", "2.5 : 1", "3.2 : 1", "4.0 : 1", "5.6 : 1", "8.0 : 1", "16 : 1", "32 : 1", "64 : 1", "inf : 1"}),
            0, 1, 1, 1, gbc.anchor, gbc.fill, 0);
        addWidget(pnl, new ComboBoxWidget("Attack (ms)", patch,
            new TCModel(patch, 173, 3),
            new TCSender(patch, 173, 3),
            new String [] {"1.0", "1.4", "2.0", "3.0", "5.0", "7.0", "10", "14", "20", "30", "50", "70"}),
            0, 2, 1, 1, gbc.anchor, gbc.fill, 0);
        addWidget(pnl, new ComboBoxWidget("Release", patch,
            new TCModel(patch, 177, 13),
            new TCSender(patch, 177, 13),
            new String [] {"50 ms", "70 ms", "100 ms", "140 ms", "200 ms", "300 ms", "500 ms", "700 ms", "1.0 s", "1.4 s", "2.0 s"}),
            0, 3, 1, 1, gbc.anchor, gbc.fill, 0);
        addWidget(pnl, new ComboBoxWidget("Gain (dB)", patch,
            new TCModel(patch, 181, -6),
            new TCSender(patch, 181, -6),
            TCElectronicGMajorUtil.genString(-6, 6)),
            0, 4, 1, 1, gbc.anchor, gbc.fill, 0);

        box.add(pnl);

        return box;
    }

    private Container buildFilterPanel() {
        // Filter panel

        Box box = Box.createHorizontalBox();
        box.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
            "Filter/Mod",TitledBorder.CENTER,TitledBorder.CENTER));
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        Box pnl = Box.createHorizontalBox();
        ComboBoxWidget cmbType = new ComboBoxWidget("Type", patch,
            new TCModel(patch, 229),
            new TCSender(patch, 229),
            new String[] {"Auto Res", "Resonan", "VinPhas", "SmthPhas", "Tremolo", "Panner"});
        addWidget(pnl, cmbType,
            0, 0, 1, 1, 0);

        box.add(pnl);

        final JPanel pnl2 = new JPanel();
        pnl2.setLayout(gridbag);
        box.add(pnl2);

        initFilterPanel(pnl2, cmbType.getValue());

        cmbType.addEventListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {

                    initFilterPanel(pnl2, ((JComboBox) e.getSource()).getSelectedIndex());

                }
            }});


        return box;
    }

    private void initFilterPanel(JPanel pnl, int i) {

        ComboBoxWidget cmbStyle;
        ComboBoxWidget cmbSens;
        ComboBoxWidget cmbResp;
        ComboBoxWidget cmbSpeed;
        ComboBoxWidget cmbDepth;
        ComboBoxWidget cmbFreqMax;
        ComboBoxWidget cmbTempo;
        ComboBoxWidget cmbTrmType;
        ComboBoxWidget cmbPWidth;
        ComboBoxWidget cmbFB;
        ComboBoxWidget cmbRange;
        ComboBoxWidget cmbResHiCut;
        ComboBoxWidget cmbTrmHiCut;
        ComboBoxWidget cmbRes;
        CheckBoxWidget chkPhaRev;

        pnl.setVisible(false);
        pnl.removeAll();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;

        switch (i) {
            case 0: //Auto Res
                cmbStyle = new ComboBoxWidget("Style", patch,
                    new TCModel(patch, 233),
                    new TCSender(patch, 233),
                    new String[] {"2nd", "4th"});
                addWidget(pnl, cmbStyle, 0, 0, 1, 1, gbc.anchor, gbc.fill, 0);
                cmbSens = new ComboBoxWidget("Sens", patch,
                    new TCModel(patch, 237),
                    new TCSender(patch, 237),
                    TCElectronicGMajorUtil.genString(0, 10));
                addWidget(pnl, cmbSens, 0, 1, 1, 1, gbc.anchor, gbc.fill, 0);
                cmbResp = new ComboBoxWidget("Response", patch,
                    new TCModel(patch, 241),
                    new TCSender(patch, 241),
                    new String[] {"Slow", "Medium", "Fast"});
                addWidget(pnl, cmbResp, 0, 2, 1, 1, gbc.anchor, gbc.fill, 0);
                cmbFreqMax = new ComboBoxWidget("Freq Max (kHz)", patch,
                    new TCModel(patch, 253, 34),
                    new TCSender(patch, 253, 34),
                    freqMaxString);
                addWidget(pnl, cmbFreqMax, 0, 3, 1, 1, gbc.anchor, gbc.fill, 0);
            break;
            case 1: //Resonan
                cmbStyle = new ComboBoxWidget("Style", patch,
                    new TCModel(patch, 233),
                    new TCSender(patch, 233),
                    new String[] {"2nd", "4th"});
                addWidget(pnl, cmbStyle, 0, 0, 1, 1, gbc.anchor, gbc.fill, 0);
                cmbResHiCut = new ComboBoxWidget("Hi Cut (Hz)", patch,
                    new TCModel(patch, 273, 18),
                    new TCSender(patch, 273, 18),
                    filterHiCutString);
                addWidget(pnl, cmbResHiCut, 0, 1, 1, 1, gbc.anchor, gbc.fill, 0);
                cmbRes = new ComboBoxWidget("Resonance (%)", patch,
                    new TCModel(patch, 277),
                    new TCSender(patch, 277),
                    TCElectronicGMajorUtil.genString(0, 100));
                addWidget(pnl, cmbRes, 0, 2, 1, 1, gbc.anchor, gbc.fill, 0);
            break;
            case 2: //VinPhas
            case 3: //SmthPhas
                cmbSpeed = new ComboBoxWidget("Speed (Hz)", patch,
                    new TCModel(patch, 245),
                    new TCSender(patch, 245),
                    speedString);
                addWidget(pnl, cmbSpeed, 0, 0, 1, 1, gbc.anchor, gbc.fill, 0);
                cmbDepth = new ComboBoxWidget("Depth (%)", patch,
                    new TCModel(patch, 249),
                    new TCSender(patch, 249),
                    TCElectronicGMajorUtil.genString(0, 100));
                addWidget(pnl, cmbDepth, 0, 1, 1, 1, gbc.anchor, gbc.fill, 0);
                cmbTempo = new ComboBoxWidget("Tempo", patch,
                    new TCModel(patch, 253),
                    new TCSender(patch, 253),
                    tempoString);
                addWidget(pnl, cmbTempo, 0, 2, 1, 1, gbc.anchor, gbc.fill, 0);
                cmbFB = new ComboBoxWidget("FB (%)", patch,
                    new TCModel(patch, 261, -100),
                    new TCSender(patch, 261, -100),
                    TCElectronicGMajorUtil.genString(-100, 100));
                addWidget(pnl, cmbFB, 1, 0, 1, 1, gbc.anchor, gbc.fill, 0);
                cmbRange = new ComboBoxWidget("Range", patch,
                    new TCModel(patch, 265),
                    new TCSender(patch, 265),
                    new String[] {"Low", "High"});
                addWidget(pnl, cmbRange, 1, 1, 1, 1, gbc.anchor, gbc.fill, 0);
                chkPhaRev = new CheckBoxWidget("Reverse", patch,
                    new TCModel(patch,269),
                    new TCSender(patch, 269));
                addWidget(pnl, chkPhaRev, 1, 2, 1, 1, gbc.anchor, gbc.fill, 0);
            break;
            case 4: //Tremolo
                cmbSpeed = new ComboBoxWidget("Speed (Hz)", patch,
                    new TCModel(patch, 245),
                    new TCSender(patch, 245),
                    speedString);
                addWidget(pnl, cmbSpeed, 0, 0, 1, 1, gbc.anchor, gbc.fill, 0);
                cmbDepth = new ComboBoxWidget("Depth (%)", patch,
                    new TCModel(patch, 249),
                    new TCSender(patch, 249),
                    TCElectronicGMajorUtil.genString(0, 100));
                addWidget(pnl, cmbDepth, 0, 1, 1, 1, gbc.anchor, gbc.fill, 0);
                cmbTempo = new ComboBoxWidget("Tempo", patch,
                    new TCModel(patch, 253),
                    new TCSender(patch, 253),
                    tempoString);
                addWidget(pnl, cmbTempo, 0, 2, 1, 1, gbc.anchor, gbc.fill, 0);
                cmbTrmType = new ComboBoxWidget("Trm Type", patch,
                    new TCModel(patch, 257),
                    new TCSender(patch, 257),
                    new String[] {"Soft", "Hard"});
                addWidget(pnl, cmbTrmType, 1, 0, 1, 1, gbc.anchor, gbc.fill, 0);
                cmbPWidth = new ComboBoxWidget("P Width (%)", patch,
                    new TCModel(patch, 261),
                    new TCSender(patch, 261),
                    TCElectronicGMajorUtil.genString(0, 100));
                addWidget(pnl, cmbPWidth, 1, 1, 1, 1, gbc.anchor, gbc.fill, 0);
                cmbTrmHiCut = new ComboBoxWidget("Hi Cut (Hz)", patch,
                    new TCModel(patch, 273),
                    new TCSender(patch, 273),
                    chorusHiCutString);
                addWidget(pnl, cmbTrmHiCut, 1, 2, 1, 1, gbc.anchor, gbc.fill, 0);
            break;
            case 5: //Panner
                //TODO: Pan Speed klopt niet
                cmbSpeed = new ComboBoxWidget("Speed (Hz)", patch,
                    new TCModel(patch, 245),
                    new TCSender(patch, 245),
                    speedString);
                addWidget(pnl, cmbSpeed, 0, 0, 1, 1, gbc.anchor, gbc.fill, 0);
                cmbDepth = new ComboBoxWidget("Width (%)", patch,
                    new TCModel(patch, 249),
                    new TCSender(patch, 249),
                    TCElectronicGMajorUtil.genString(0, 100));
                addWidget(pnl, cmbDepth, 0, 1, 1, 1, gbc.anchor, gbc.fill, 0);
                cmbTempo = new ComboBoxWidget("Tempo", patch,
                    new TCModel(patch, 253),
                    new TCSender(patch, 253),
                    tempoString);
                addWidget(pnl, cmbTempo, 0, 2, 1, 1, gbc.anchor, gbc.fill, 0);
            break;
            default: ErrorMsg.reportStatus("Switch value " + i + " not handled! ");
        }
        pnl.setVisible(true);

    }

    private Container buildPitchPanel() {
        // Pitch panel

        Box box = Box.createHorizontalBox();
        box.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
            "Pitch",TitledBorder.CENTER,TitledBorder.CENTER));
        GridBagLayout gridbag = new GridBagLayout();

        Box pnl = Box.createHorizontalBox();
        ComboBoxWidget cmbType = new ComboBoxWidget("Type", patch,
            new TCModel(patch, 293),
            new TCSender(patch, 293),
            new String[] {"Detune", "Whammy", "Octaver", "Shifter"});
        addWidget(pnl, cmbType,
            0, 0, 1, 1, 0);
        box.add(pnl);

        final JPanel pnl2 = new JPanel();
        pnl2.setLayout(gridbag);
        box.add(pnl2);

        initPitchPanel(pnl2, cmbType.getValue());

        cmbType.addEventListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {

                    initPitchPanel(pnl2, ((JComboBox) e.getSource()).getSelectedIndex());

                }
            }});


        return box;
    }

    private void initPitchPanel(JPanel pnl, int i) {

        ComboBoxWidget cmbVoice1;
        ComboBoxWidget cmbVoice2;
        ComboBoxWidget cmbDly1;
        ComboBoxWidget cmbDly2;
        ComboBoxWidget cmbPitch;
        ComboBoxWidget cmbDir;
        ComboBoxWidget cmbRange;
        ComboBoxWidget cmbPan1;
        ComboBoxWidget cmbPan2;
        ComboBoxWidget cmbLvl1;
        ComboBoxWidget cmbLvl2;
        ComboBoxWidget cmbFB1;
        ComboBoxWidget cmbFB2;

        pnl.setVisible(false);
        pnl.removeAll();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;

        switch (i) {
            case 0: //Detune
                cmbVoice1 = new ComboBoxWidget("Voice L (cent)", patch,
                    new TCModel(patch, 297, -100),
                    new TCSender(patch, 297, -100),
                    TCElectronicGMajorUtil.genString(-100, 100));
                addWidget(pnl, cmbVoice1, 0, 0, 1, 1, gbc.anchor, gbc.fill, 0);
                cmbVoice2 = new ComboBoxWidget("Voice R (cent)", patch,
                    new TCModel(patch, 301, -100),
                    new TCSender(patch, 301, -100),
                    TCElectronicGMajorUtil.genString(-100, 100));
                addWidget(pnl, cmbVoice2, 0, 1, 1, 1, gbc.anchor, gbc.fill, 0);
                cmbDly1 = new ComboBoxWidget("Delay L (ms)", patch,
                    new TCModel(patch, 313),
                    new TCSender(patch, 313),
                    TCElectronicGMajorUtil.genString(0, 50));
                addWidget(pnl, cmbDly1, 0, 2, 1, 1, gbc.anchor, gbc.fill, 0);
                cmbDly2 = new ComboBoxWidget("Delay R (ms)", patch,
                    new TCModel(patch, 317),
                    new TCSender(patch, 317),
                    TCElectronicGMajorUtil.genString(0, 50));
                addWidget(pnl, cmbDly2, 0, 3, 1, 1, gbc.anchor, gbc.fill, 0);
            break;
            case 1: //Whammy
                cmbPitch = new ComboBoxWidget("Pitch (%)", patch,
                    new TCModel(patch, 329),
                    new TCSender(patch, 329),
                    TCElectronicGMajorUtil.genString(0, 100));
                addWidget(pnl, cmbPitch, 0, 0, 1, 1, gbc.anchor, gbc.fill, 0);
                cmbDir = new ComboBoxWidget("Dir", patch,
                    new TCModel(patch, 333),
                    new TCSender(patch, 333),
                    new String[] {"Down", "Up"});
                addWidget(pnl, cmbDir, 0, 1, 1, 1, gbc.anchor, gbc.fill, 0);
                cmbRange = new ComboBoxWidget("Range (oct)", patch,
                    new TCModel(patch, 337, 1),
                    new TCSender(patch, 337, 1),
                    TCElectronicGMajorUtil.genString(1, 2));
                addWidget(pnl, cmbRange, 0, 2, 1, 1, gbc.anchor, gbc.fill, 0);
            break;
            case 2: //Octaver
                cmbDir = new ComboBoxWidget("Dir", patch,
                    new TCModel(patch, 333),
                    new TCSender(patch, 333),
                    new String[] {"Down", "Up"});
                addWidget(pnl, cmbDir, 0, 1, 1, 1, gbc.anchor, gbc.fill, 0);
                cmbRange = new ComboBoxWidget("Range (oct)", patch,
                    new TCModel(patch, 337, 1),
                    new TCSender(patch, 337, 1),
                    TCElectronicGMajorUtil.genString(1, 2));
                addWidget(pnl, cmbRange, 0, 2, 1, 1, gbc.anchor, gbc.fill, 0);
            break;
            case 3: //Shifter
                cmbVoice1 = new ComboBoxWidget("Voice 1 (cent)", patch,
                    new TCModel(patch, 297, -100),
                    new TCSender(patch, 297, -100),
                    TCElectronicGMajorUtil.genString(-100, 100));
                addWidget(pnl, cmbVoice1, 0, 0, 1, 1, gbc.anchor, gbc.fill, 0);
                cmbVoice2 = new ComboBoxWidget("Voice 2 (cent)", patch,
                    new TCModel(patch, 301, -100),
                    new TCSender(patch, 301, -100),
                    TCElectronicGMajorUtil.genString(-100, 100));
                addWidget(pnl, cmbVoice2, 0, 1, 1, 1, gbc.anchor, gbc.fill, 0);
                cmbPan1 = new ComboBoxWidget("Pan 1", patch,
                    new TCModel(patch, 305, -50),
                    new TCSender(patch, 305, -50),
                    TCElectronicGMajorUtil.genString(-50, 50));
                addWidget(pnl, cmbPan1, 0, 2, 1, 1, gbc.anchor, gbc.fill, 0);
                cmbPan2 = new ComboBoxWidget("Pan 2", patch,
                    new TCModel(patch, 309, -50),
                    new TCSender(patch, 309, -50),
                    TCElectronicGMajorUtil.genString(-50, 50));
                addWidget(pnl, cmbPan2, 0, 3, 1, 1, gbc.anchor, gbc.fill, 0);
                cmbDly1 = new ComboBoxWidget("Delay 1 (ms)", patch,
                    new TCModel(patch, 313),
                    new TCSender(patch, 313),
                    TCElectronicGMajorUtil.genString(0, 350));
                addWidget(pnl, cmbDly1, 1, 0, 1, 1, gbc.anchor, gbc.fill, 0);
                cmbDly2 = new ComboBoxWidget("Delay 2 (ms)", patch,
                    new TCModel(patch, 317),
                    new TCSender(patch, 317),
                    TCElectronicGMajorUtil.genString(0, 350));
                addWidget(pnl, cmbDly2, 1, 1, 1, 1, gbc.anchor, gbc.fill, 0);
                //TODO: FB1 en FB2 zijn samen altijd 100% in het Pitch Shifter blok.
                cmbFB1 = new ComboBoxWidget("FB 1 (%)", patch,
                    new TCModel(patch, 321),
                    new TCSender(patch, 321),
                    TCElectronicGMajorUtil.genString(0, 100));
                addWidget(pnl, cmbFB1, 1, 2, 1, 1, gbc.anchor, gbc.fill, 0);
                cmbFB2 = new ComboBoxWidget("FB 2 (%)", patch,
                    new TCModel(patch, 325),
                    new TCSender(patch, 325),
                    TCElectronicGMajorUtil.genString(0, 100));
                addWidget(pnl, cmbFB2, 1, 3, 1, 1, gbc.anchor, gbc.fill, 0);
                cmbLvl1 = new ComboBoxWidget("Lvl 1 (dB)", patch,
                    new TCModel(patch, 329, -100),
                    new TCSender(patch, 329, -100),
                    TCElectronicGMajorUtil.genString(-100, 0));
                addWidget(pnl, cmbLvl1, 2, 0, 1, 1, gbc.anchor, gbc.fill, 0);
                cmbLvl2 = new ComboBoxWidget("Lvl 2 (dB)", patch,
                    new TCModel(patch, 333, -100),
                    new TCSender(patch, 333, -100),
                    TCElectronicGMajorUtil.genString(-100, 0));
                addWidget(pnl, cmbLvl2, 2, 1, 1, 1, gbc.anchor, gbc.fill, 0);
            break;
            default: ErrorMsg.reportStatus("Switch value " + i + " not handled! ");
        }
        pnl.setVisible(true);

    }

    private Container buildChorusPanel() {
        // Chorus panel

        Box box = Box.createHorizontalBox();
        box.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
            "Chorus/Flanger",TitledBorder.CENTER,TitledBorder.CENTER));
        GridBagLayout gridbag = new GridBagLayout();

        Box pnl = Box.createHorizontalBox();
        ComboBoxWidget cmbType = new ComboBoxWidget("Type", patch,
            new TCModel(patch, 357),
            new TCSender(patch, 357),
            new String[] {"Classic Chorus", "Advanced Chorus", "Classic Flanger", "Advanced Flanger", "Vibrator"});
        addWidget(pnl, cmbType,
            0, 0, 1, 1, 0);
        box.add(pnl);

        final JPanel pnl2 = new JPanel();
        pnl2.setLayout(gridbag);
        box.add(pnl2);

        initChorusPanel(pnl2, cmbType.getValue());

        cmbType.addEventListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {

                    initChorusPanel(pnl2, ((JComboBox) e.getSource()).getSelectedIndex());

                }
            }});


        return box;
    }

    private void initChorusPanel(JPanel pnl, int i) {

        ComboBoxWidget cmbSpeed;
        ComboBoxWidget cmbDepth;
        ComboBoxWidget cmbTempo;
        ComboBoxWidget cmbHiCut;
        ComboBoxWidget cmbFBHiCut;
        ComboBoxWidget cmbFB;
        ComboBoxWidget cmbDly;
        CheckBoxWidget chkGold;
        CheckBoxWidget chkPhaRev;

        pnl.setVisible(false);
        pnl.removeAll();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;

        switch (i) {
            case 0: //Classic Chorus
            case 4: //Vibrator
                cmbSpeed = new ComboBoxWidget("Speed (Hz)", patch,
                    new TCModel(patch, 361),
                    new TCSender(patch, 361),
                    speedString);
                addWidget(pnl, cmbSpeed, 0, 0, 1, 1, gbc.anchor, gbc.fill, 0);
                cmbDepth = new ComboBoxWidget("Depth (%)", patch,
                    new TCModel(patch, 365),
                    new TCSender(patch, 365),
                    TCElectronicGMajorUtil.genString(0, 100));
                addWidget(pnl, cmbDepth, 0, 1, 1, 1, gbc.anchor, gbc.fill, 0);
                cmbTempo = new ComboBoxWidget("Tempo", patch,
                    new TCModel(patch, 369),
                    new TCSender(patch, 369),
                    tempoString);
                addWidget(pnl, cmbTempo, 0, 2, 1, 1, gbc.anchor, gbc.fill, 0);
                cmbHiCut = new ComboBoxWidget("Hi Cut (Hz)", patch,
                    new TCModel(patch, 373),
                    new TCSender(patch, 373),
                    chorusHiCutString);
                addWidget(pnl, cmbHiCut, 0, 3, 1, 1, gbc.anchor, gbc.fill, 0);
            break;
            case 1: //Advanced Chorus
                cmbSpeed = new ComboBoxWidget("Speed (Hz)", patch,
                    new TCModel(patch, 361),
                    new TCSender(patch, 361),
                    speedString);
                addWidget(pnl, cmbSpeed, 0, 0, 1, 1, gbc.anchor, gbc.fill, 0);
                cmbDepth = new ComboBoxWidget("Depth (%)", patch,
                    new TCModel(patch, 365),
                    new TCSender(patch, 365),
                    TCElectronicGMajorUtil.genString(0, 100));
                addWidget(pnl, cmbDepth, 0, 1, 1, 1, gbc.anchor, gbc.fill, 0);
                cmbTempo = new ComboBoxWidget("Tempo", patch,
                    new TCModel(patch, 369),
                    new TCSender(patch, 369),
                    tempoString);
                addWidget(pnl, cmbTempo, 0, 2, 1, 1, gbc.anchor, gbc.fill, 0);
                cmbHiCut = new ComboBoxWidget("Hi Cut (Hz)", patch,
                    new TCModel(patch, 373),
                    new TCSender(patch, 373),
                    chorusHiCutString);
                addWidget(pnl, cmbHiCut, 0, 3, 1, 1, gbc.anchor, gbc.fill, 0);
                cmbDly = new ComboBoxWidget("Delay (ms)", patch,
                    new TCModel(patch, 385, 1),
                    new TCSender(patch, 385, 1),
                    TCElectronicGMajorUtil.genString(0.1, 50.0, 0.1, "0.0"));
                addWidget(pnl, cmbDly, 1, 0, 1, 1, gbc.anchor, gbc.fill, 0);
                chkGold = new CheckBoxWidget("Gold", patch,
                    new TCModel(patch, 389),
                    new TCSender(patch, 389));
                addWidget(pnl, chkGold, 1, 1, 1, 1, gbc.anchor, gbc.fill, 0);
                chkPhaRev = new CheckBoxWidget("Reverse", patch,
                    new TCModel(patch, 393),
                    new TCSender(patch, 393));
                addWidget(pnl, chkPhaRev, 1, 2, 1, 1, gbc.anchor, gbc.fill, 0);
            break;
            case 2: //Classic Flanger
                cmbSpeed = new ComboBoxWidget("Speed (Hz)", patch,
                    new TCModel(patch, 361),
                    new TCSender(patch, 361),
                    speedString);
                addWidget(pnl, cmbSpeed, 0, 0, 1, 1, gbc.anchor, gbc.fill, 0);
                cmbDepth = new ComboBoxWidget("Depth (%)", patch,
                    new TCModel(patch, 365),
                    new TCSender(patch, 365),
                    TCElectronicGMajorUtil.genString(0, 100));
                addWidget(pnl, cmbDepth, 0, 1, 1, 1, gbc.anchor, gbc.fill, 0);
                cmbTempo = new ComboBoxWidget("Tempo", patch,
                    new TCModel(patch, 369),
                    new TCSender(patch, 369),
                    tempoString);
                addWidget(pnl, cmbTempo, 0, 2, 1, 1, gbc.anchor, gbc.fill, 0);
                cmbHiCut = new ComboBoxWidget("Hi Cut (Hz)", patch,
                    new TCModel(patch, 373),
                    new TCSender(patch, 373),
                    chorusHiCutString);
                addWidget(pnl, cmbHiCut, 1, 0, 1, 1, gbc.anchor, gbc.fill, 0);
                cmbFB = new ComboBoxWidget("FB (%)", patch,
                    new TCModel(patch, 377, -100),
                    new TCSender(patch, 377, -100),
                    TCElectronicGMajorUtil.genString(-100, 100));
                addWidget(pnl, cmbFB, 1, 1, 1, 1, gbc.anchor, gbc.fill, 0);
                cmbFBHiCut = new ComboBoxWidget("FB Hi Cut (Hz)", patch,
                    new TCModel(patch, 381),
                    new TCSender(patch, 381),
                    chorusHiCutString);
                addWidget(pnl, cmbFBHiCut, 1, 2, 1, 1, gbc.anchor, gbc.fill, 0);
            break;
            case 3: //Advanced Flanger
                cmbSpeed = new ComboBoxWidget("Speed (Hz)", patch,
                    new TCModel(patch, 361),
                    new TCSender(patch, 361),
                    speedString);
                addWidget(pnl, cmbSpeed, 0, 0, 1, 1, gbc.anchor, gbc.fill, 0);
                cmbDepth = new ComboBoxWidget("Depth (%)", patch,
                    new TCModel(patch, 365),
                    new TCSender(patch, 365),
                    TCElectronicGMajorUtil.genString(0, 100));
                addWidget(pnl, cmbDepth, 0, 1, 1, 1, gbc.anchor, gbc.fill, 0);
                cmbTempo = new ComboBoxWidget("Tempo", patch,
                    new TCModel(patch, 369),
                    new TCSender(patch, 369),
                    tempoString);
                addWidget(pnl, cmbTempo, 0, 2, 1, 1, gbc.anchor, gbc.fill, 0);
                cmbHiCut = new ComboBoxWidget("Hi Cut (Hz)", patch,
                    new TCModel(patch, 373),
                    new TCSender(patch, 373),
                    chorusHiCutString);
                addWidget(pnl, cmbHiCut, 0, 3, 1, 1, gbc.anchor, gbc.fill, 0);
                cmbFB = new ComboBoxWidget("FB (%)", patch,
                    new TCModel(patch, 377, -100),
                    new TCSender(patch, 377, -100),
                    TCElectronicGMajorUtil.genString(-100, 100));
                addWidget(pnl, cmbFB, 0, 4, 1, 1, gbc.anchor, gbc.fill, 0);
                cmbFBHiCut = new ComboBoxWidget("FB Hi Cut (Hz)", patch,
                    new TCModel(patch, 381),
                    new TCSender(patch, 381),
                    chorusHiCutString);
                addWidget(pnl, cmbFBHiCut, 1, 0, 1, 1, gbc.anchor, gbc.fill, 0);
                cmbDly = new ComboBoxWidget("Delay (ms)", patch,
                    new TCModel(patch, 385, 1),
                    new TCSender(patch, 385, 1),
                    TCElectronicGMajorUtil.genString(0.1, 50.0, 0.1, "0.0"));
                addWidget(pnl, cmbDly, 1, 1, 1, 1, gbc.anchor, gbc.fill, 0);
                chkGold = new CheckBoxWidget("Gold", patch,
                    new TCModel(patch, 389),
                    new TCSender(patch, 389));
                addWidget(pnl, chkGold, 1, 2, 1, 1, gbc.anchor, gbc.fill, 0);
                chkPhaRev = new CheckBoxWidget("Reverse", patch,
                    new TCModel(patch, 393),
                    new TCSender(patch, 393));
                addWidget(pnl, chkPhaRev, 1, 3, 1, 1, gbc.anchor, gbc.fill, 0);
            break;
            default: ErrorMsg.reportStatus("Switch value " + i + " not handled! ");
        }
        pnl.setVisible(true);

    }

    private Container buildDelayPanel() {
        // Pitch panel

        Box box = Box.createHorizontalBox();
        box.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
            "Delay",TitledBorder.CENTER,TitledBorder.CENTER));
        GridBagLayout gridbag = new GridBagLayout();

        Box pnl = Box.createHorizontalBox();
        ComboBoxWidget cmbType = new ComboBoxWidget("Type", patch,
            new TCModel(patch, 421),
            new TCSender(patch, 421),
            new String[] {"Ping Pong", "Dynamic", "Dual"});
        addWidget(pnl, cmbType,
            0, 0, 1, 1, 0);
        box.add(pnl);

        final JPanel pnl2 = new JPanel();
        pnl2.setLayout(gridbag);
        box.add(pnl2);

        initDelayPanel(pnl2, cmbType.getValue());

        cmbType.addEventListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {

                    initDelayPanel(pnl2, ((JComboBox) e.getSource()).getSelectedIndex());

                }
            }});


        return box;
    }

    private void initDelayPanel(JPanel pnl, int i) {

        ComboBoxWidget cmbDly1;
        ComboBoxWidget cmbDly2;
        ComboBoxWidget cmbTempo1;
        ComboBoxWidget cmbTempo2;
        ComboBoxWidget cmbWidth;
        ComboBoxWidget cmbFB1;
        ComboBoxWidget cmbFB2;
        ComboBoxWidget cmbFBHiCut;
        ComboBoxWidget cmbFBLoCut;
        ComboBoxWidget cmbOffs;
        ComboBoxWidget cmbSens;
        ComboBoxWidget cmbDamp;
        ComboBoxWidget cmbRel;
        ComboBoxWidget cmbPan1;
        ComboBoxWidget cmbPan2;

        pnl.setVisible(false);
        pnl.removeAll();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;

        switch (i) {
            case 0: //Ping Pong
                cmbDly1 = new ComboBoxWidget("Delay (ms)", patch,
                    new TCModel(patch, 425),
                    new TCSender(patch, 425),
                    TCElectronicGMajorUtil.genString(0, 1800));
                addWidget(pnl, cmbDly1, 0, 0, 1, 1, gbc.anchor, gbc.fill, 0);
                cmbTempo1 = new ComboBoxWidget("Tempo", patch,
                    new TCModel(patch, 433),
                    new TCSender(patch, 433),
                    tempoString);
                addWidget(pnl, cmbTempo1, 0, 1, 1, 1, gbc.anchor, gbc.fill, 0);
                cmbWidth = new ComboBoxWidget("Width (%)", patch,
                    new TCModel(patch, 437),
                    new TCSender(patch, 437),
                    TCElectronicGMajorUtil.genString(0, 100));
                addWidget(pnl, cmbWidth, 0, 2, 1, 1, gbc.anchor, gbc.fill, 0);
                cmbFB1 = new ComboBoxWidget("FB (%)", patch,
                    new TCModel(patch, 441),
                    new TCSender(patch, 441),
                    TCElectronicGMajorUtil.genString(0, 100));
                addWidget(pnl, cmbFB1, 1, 0, 1, 1, gbc.anchor, gbc.fill, 0);
                cmbFBHiCut = new ComboBoxWidget("FB Hi Cut (Hz)", patch,
                    new TCModel(patch, 449, 40),
                    new TCSender(patch, 449, 40),
                    delayFBHiCutString);
                addWidget(pnl, cmbFBHiCut, 1, 1, 1, 1, gbc.anchor, gbc.fill, 0);
                cmbFBLoCut = new ComboBoxWidget("FB Lo Cut (Hz)", patch,
                    new TCModel(patch, 453),
                    new TCSender(patch, 453),
                    delayLoCutString);
                addWidget(pnl, cmbFBLoCut, 1, 2, 1, 1, gbc.anchor, gbc.fill, 0);
            break;
            case 1: //Dynamic
                cmbDly1 = new ComboBoxWidget("Delay (ms)", patch,
                    new TCModel(patch, 425),
                    new TCSender(patch, 425),
                    TCElectronicGMajorUtil.genString(0, 1800));
                addWidget(pnl, cmbDly1, 0, 0, 1, 1, gbc.anchor, gbc.fill, 0);
                cmbTempo1 = new ComboBoxWidget("Tempo", patch,
                    new TCModel(patch, 433),
                    new TCSender(patch, 433),
                    tempoString);
                addWidget(pnl, cmbTempo1, 0, 1, 1, 1, gbc.anchor, gbc.fill, 0);
                cmbFB1 = new ComboBoxWidget("FB (%)", patch,
                    new TCModel(patch, 441),
                    new TCSender(patch, 441),
                    TCElectronicGMajorUtil.genString(0, 100));
                addWidget(pnl, cmbFB1, 0, 2, 1, 1, gbc.anchor, gbc.fill, 0);
                cmbFBHiCut = new ComboBoxWidget("FB Hi Cut (Hz)", patch,
                    new TCModel(patch, 449, 40),
                    new TCSender(patch, 449, 40),
                    delayFBHiCutString);
                addWidget(pnl, cmbFBHiCut, 0, 3, 1, 1, gbc.anchor, gbc.fill, 0);
                cmbFBLoCut = new ComboBoxWidget("FB Lo Cut (Hz)", patch,
                    new TCModel(patch, 453),
                    new TCSender(patch, 453),
                    delayLoCutString);
                addWidget(pnl, cmbFBLoCut, 0, 4, 1, 1, gbc.anchor, gbc.fill, 0);
                cmbOffs = new ComboBoxWidget("Offset (ms)", patch,
                    new TCModel(patch, 457),
                    new TCSender(patch, 457),
                    TCElectronicGMajorUtil.genString(0, 200));
                addWidget(pnl, cmbOffs, 1, 0, 1, 1, gbc.anchor, gbc.fill, 0);
                cmbSens = new ComboBoxWidget("Sense (dB)", patch,
                    new TCModel(patch, 461, -50),
                    new TCSender(patch, 461, -50),
                    TCElectronicGMajorUtil.genString(-50, 0));
                addWidget(pnl, cmbSens, 1, 1, 1, 1, gbc.anchor, gbc.fill, 0);
                cmbDamp = new ComboBoxWidget("Damp (dB)", patch,
                    new TCModel(patch, 465),
                    new TCSender(patch, 465),
                    TCElectronicGMajorUtil.genString(0, 100));
                addWidget(pnl, cmbDamp, 1, 2, 1, 1, gbc.anchor, gbc.fill, 0);
                cmbRel = new ComboBoxWidget("Release (ms)", patch,
                    new TCModel(patch, 469, 11),
                    new TCSender(patch, 469, 11),
                    delayReleaseString);
                addWidget(pnl, cmbRel, 1, 3, 1, 1, gbc.anchor, gbc.fill, 0);
            break;
            case 2: //Dual
                cmbDly1 = new ComboBoxWidget("Delay 1 (ms)", patch,
                    new TCModel(patch, 425),
                    new TCSender(patch, 425),
                    TCElectronicGMajorUtil.genString(0, 1800));
                addWidget(pnl, cmbDly1, 0, 0, 1, 1, gbc.anchor, gbc.fill, 0);
                cmbDly2 = new ComboBoxWidget("Delay 2 (ms)", patch,
                    new TCModel(patch, 429),
                    new TCSender(patch, 429),
                    TCElectronicGMajorUtil.genString(0, 1800));
                addWidget(pnl, cmbDly2, 0, 1, 1, 1, gbc.anchor, gbc.fill, 0);
                cmbTempo1 = new ComboBoxWidget("Tempo 1", patch,
                    new TCModel(patch, 433),
                    new TCSender(patch, 433),
                    tempoString);
                addWidget(pnl, cmbTempo1, 0, 2, 1, 1, gbc.anchor, gbc.fill, 0);
                cmbTempo2 = new ComboBoxWidget("Tempo 2", patch,
                    new TCModel(patch, 437),
                    new TCSender(patch, 437),
                    tempoString);
                addWidget(pnl, cmbTempo2, 0, 3, 1, 1, gbc.anchor, gbc.fill, 0);
                cmbFB1 = new ComboBoxWidget("FB 1 (%)", patch,
                    new TCModel(patch, 441),
                    new TCSender(patch, 441),
                    TCElectronicGMajorUtil.genString(0, 100));
                addWidget(pnl, cmbFB1, 0, 4, 1, 1, gbc.anchor, gbc.fill, 0);
                cmbFB2 = new ComboBoxWidget("FB 2 (%)", patch,
                    new TCModel(patch, 445),
                    new TCSender(patch, 445),
                    TCElectronicGMajorUtil.genString(0, 100));
                addWidget(pnl, cmbFB2, 1, 0, 1, 1, gbc.anchor, gbc.fill, 0);
                cmbFBHiCut = new ComboBoxWidget("FB Hi Cut (Hz)", patch,
                    new TCModel(patch, 449, 40),
                    new TCSender(patch, 449, 40),
                    delayFBHiCutString);
                addWidget(pnl, cmbFBHiCut, 1, 1, 1, 1, gbc.anchor, gbc.fill, 0);
                cmbFBLoCut = new ComboBoxWidget("FB Lo Cut (Hz)", patch,
                    new TCModel(patch, 453),
                    new TCSender(patch, 453),
                    delayLoCutString);
                addWidget(pnl, cmbFBLoCut, 1, 2, 1, 1, gbc.anchor, gbc.fill, 0);
                cmbPan1 = new ComboBoxWidget("Pan 1", patch,
                    new TCModel(patch, 457, -50),
                    new TCSender(patch, 457, -50),
                    TCElectronicGMajorUtil.genString(-50, 50));
                addWidget(pnl, cmbPan1, 1, 3, 1, 1, gbc.anchor, gbc.fill, 0);
                cmbPan2 = new ComboBoxWidget("Pan 2", patch,
                    new TCModel(patch, 461, -50),
                    new TCSender(patch, 461, -50),
                    TCElectronicGMajorUtil.genString(-50, 50));
                addWidget(pnl, cmbPan2, 1, 4, 1, 1, gbc.anchor, gbc.fill, 0);
            break;
            default: ErrorMsg.reportStatus("Switch value " + i + " not handled! ");
        }

        pnl.setVisible(true);

    }

    private Container buildReverbPanel() {
        // Reverb panel

        Box box = Box.createHorizontalBox();
        box.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
            "Reverb",TitledBorder.CENTER,TitledBorder.CENTER));
        GridBagLayout gridbag = new GridBagLayout();

        Box pnl = Box.createHorizontalBox();
        ComboBoxWidget cmbType = new ComboBoxWidget("Type", patch,
            new TCModel(patch, 485),
            new TCSender(patch, 485),
            new String[] {"Spring", "Hall", "Room", "Plate"});
        addWidget(pnl, cmbType,
            0, 0, 1, 1, 0);
        box.add(pnl);

        final JPanel pnl2 = new JPanel();
        pnl2.setLayout(gridbag);
        box.add(pnl2);

        initReverbPanel(pnl2, cmbType.getValue());

        cmbType.addEventListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {

                    initReverbPanel(pnl2, ((JComboBox) e.getSource()).getSelectedIndex());

                }
            }});


        return box;
    }

    private void initReverbPanel(JPanel pnl, int i) {

        ComboBoxWidget cmbDecay;
        ComboBoxWidget cmbPreDly;
        ComboBoxWidget cmbShape;
        ComboBoxWidget cmbSize;
        ComboBoxWidget cmbHiCol;
        ComboBoxWidget cmbHiFact;
        ComboBoxWidget cmbLoCol;
        ComboBoxWidget cmbLoFact;
        ComboBoxWidget cmbRoomLvl;
        ComboBoxWidget cmbRevLvl;
        ComboBoxWidget cmbDiff;

        pnl.setVisible(false);
        pnl.removeAll();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;

        switch (i) {
            case 0: //Spring
            case 1: //Hall
            case 2: //Room
            case 3: //Plate
                cmbDecay = new ComboBoxWidget("Decay (s)", patch,
                    new TCModel(patch, 489, 1),
                    new TCSender(patch, 489, 1),
                    TCElectronicGMajorUtil.genString(0.1, 20.0, 0.1, "0.0"));
                addWidget(pnl, cmbDecay, 0, 0, 1, 1, gbc.anchor, gbc.fill, 0);
                cmbPreDly = new ComboBoxWidget("Pre Dly (ms)", patch,
                    new TCModel(patch, 493),
                    new TCSender(patch, 493),
                    TCElectronicGMajorUtil.genString(0, 100));
                addWidget(pnl, cmbPreDly, 0, 1, 1, 1, gbc.anchor, gbc.fill, 0);
                cmbShape = new ComboBoxWidget("Shape", patch,
                    new TCModel(patch, 497),
                    new TCSender(patch, 497),
                    new String[] {"Round", "Curved", "Square"});
                addWidget(pnl, cmbShape, 0, 2, 1, 1, gbc.anchor, gbc.fill, 0);
                cmbSize = new ComboBoxWidget("Shape", patch,
                    new TCModel(patch, 501),
                    new TCSender(patch, 501),
                    new String[] {"Box", "Tiny", "Small", "Medium", "Large", "XL"});
                addWidget(pnl, cmbSize, 0, 3, 1, 1, gbc.anchor, gbc.fill, 0);
                cmbHiCol = new ComboBoxWidget("Hi Col", patch,
                    new TCModel(patch, 505),
                    new TCSender(patch, 505),
                    new String[] {"Wool", "Warm", "Real", "Clear", "Bright", "Crisp", "Glass"});
                addWidget(pnl, cmbHiCol, 1, 0, 1, 1, gbc.anchor, gbc.fill, 0);
                cmbHiFact = new ComboBoxWidget("Hi Fact", patch,
                    new TCModel(patch, 509, -25),
                    new TCSender(patch, 509, -25),
                    TCElectronicGMajorUtil.genString(-25, 25));
                addWidget(pnl, cmbHiFact, 1, 1, 1, 1, gbc.anchor, gbc.fill, 0);
                cmbLoCol = new ComboBoxWidget("Lo Col", patch,
                    new TCModel(patch, 513),
                    new TCSender(patch, 513),
                new String[] {"Thick", "Round", "Real", "Light", "Tight", "Thin", "No Bass"});
                addWidget(pnl, cmbLoCol, 1, 2, 1, 1, gbc.anchor, gbc.fill, 0);
                cmbLoFact = new ComboBoxWidget("Lo Fact", patch,
                    new TCModel(patch, 517, -25),
                    new TCSender(patch, 517, -25),
                    TCElectronicGMajorUtil.genString(-25, 25));
                addWidget(pnl, cmbLoFact, 1, 3, 1, 1, gbc.anchor, gbc.fill, 0);
                cmbRoomLvl = new ComboBoxWidget("Room Lvl (dB)", patch,
                    new TCModel(patch, 521, -100),
                    new TCSender(patch, 521, -100),
                    TCElectronicGMajorUtil.genString(-100, 0));
                addWidget(pnl, cmbRoomLvl, 2, 0, 1, 1, gbc.anchor, gbc.fill, 0);
                cmbRevLvl = new ComboBoxWidget("Rev Lvl (dB)", patch,
                    new TCModel(patch, 525, -100),
                    new TCSender(patch, 525, -100),
                    TCElectronicGMajorUtil.genString(-100, 0));
                addWidget(pnl, cmbRevLvl, 2, 1, 1, 1, gbc.anchor, gbc.fill, 0);
                cmbDiff = new ComboBoxWidget("Diffuse", patch,
                    new TCModel(patch, 529, -25),
                    new TCSender(patch, 529, -25),
                    TCElectronicGMajorUtil.genString(-25, 25));
                addWidget(pnl, cmbDiff, 2, 2, 1, 1, gbc.anchor, gbc.fill, 0);
            break;
            default: ErrorMsg.reportStatus("Switch value " + i + " not handled! ");
        }
        pnl.setVisible(true);

    }

    private Container buildBottomPanel() {
        // Bottom panel
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;

        Dimension shortField = new Dimension(50, 20);
        Dimension longField = new Dimension(80, 20);
        JPanel pnl = new JPanel();
        pnl.setLayout(gridbag);

        gbc.gridx = 1;
        gbc.gridy = 0;

        JLabel lbl = new JLabel("NG");
        lbl.setPreferredSize(shortField);
        pnl.add(lbl, gbc);

        gbc.gridx++;
        lbl = new JLabel("EQ");
        lbl.setPreferredSize(shortField);
        pnl.add(lbl, gbc);
        gbc.gridx++;
        lbl = new JLabel("Comp");
        lbl.setPreferredSize(shortField);
        pnl.add(lbl, gbc);
        gbc.gridx++;
        lbl = new JLabel("Filter/Mod");
        lbl.setPreferredSize(longField);
        pnl.add(lbl, gbc);
        gbc.gridx++;
        lbl = new JLabel("Pitch");
        lbl.setPreferredSize(longField);
        pnl.add(lbl, gbc);
        gbc.gridx++;
        lbl = new JLabel("Cho/Fla");
        lbl.setPreferredSize(longField);
        pnl.add(lbl, gbc);
        gbc.gridx++;
        lbl = new JLabel("Delay");
        lbl.setPreferredSize(longField);
        pnl.add(lbl, gbc);
        gbc.gridx++;
        lbl = new JLabel("Reverb");
        lbl.setPreferredSize(longField);
        pnl.add(lbl, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        pnl.add(new JLabel("On"), gbc);
        gbc.gridy++;
        pnl.add(new JLabel("Mix (%)"), gbc);
        gbc.gridy++;
        pnl.add(new JLabel("Out (dB)"), gbc);

        //On NG
        addWidget(pnl, new TCCheckBoxWidget("     ", patch,
            new TCModel(patch,609),
            new TCSender(patch, 609)),
            1,1,1,1,
            gbc.anchor, gbc.fill,
            0);
        //On EQ
        addWidget(pnl, new CheckBoxWidget("     ", patch,
            new TCModel(patch,565),
            new TCSender(patch, 565)),
            2,1,1,1,
            gbc.anchor, gbc.fill,
            0);
        //On Comp
        addWidget(pnl, new TCCheckBoxWidget("     ", patch,
            new TCModel(patch,225),
            new TCSender(patch, 225)),
            3,1,1,1,
            gbc.anchor, gbc.fill,
            0);
        //On Filter/Mod
        addWidget(pnl, new TCCheckBoxWidget("     ", patch,
            new TCModel(patch,289),
            new TCSender(patch, 289)),
            4,1,1,1,
            gbc.anchor, gbc.fill,
            0);
        //On Pitch
        addWidget(pnl, new TCCheckBoxWidget("     ", patch,
            new TCModel(patch,353),
            new TCSender(patch, 353)),
            5,1,1,1,
            gbc.anchor, gbc.fill,
            0);
        //On Chorus
        addWidget(pnl, new TCCheckBoxWidget("     ", patch,
            new TCModel(patch,417),
            new TCSender(patch, 417)),
            6,1,1,1,
            gbc.anchor, gbc.fill,
            0);
        //On Delay
        addWidget(pnl, new TCCheckBoxWidget("     ", patch,
            new TCModel(patch,481),
            new TCSender(patch, 481)),
            7,1,1,1,
            gbc.anchor, gbc.fill,
            0);
        //On Reverb
        addWidget(pnl, new TCCheckBoxWidget("     ", patch,
            new TCModel(patch,545),
            new TCSender(patch, 545)),
            8,1,1,1,
            gbc.anchor, gbc.fill,
            0);

        //Mix Filter/Mod
        addWidget(pnl, new ComboBoxWidget("", patch,
            new TCModel(patch, 281),
            new TCSender(patch, 281),
            TCElectronicGMajorUtil.genString(0, 100)),
            4,2,1,1,
            gbc.anchor, gbc.fill,
            0);
        //Mix Pitch
        addWidget(pnl, new ComboBoxWidget("", patch,
            new TCModel(patch, 341),
            new TCSender(patch, 341),
            TCElectronicGMajorUtil.genString(0, 100)),
            5,2,1,1,
            gbc.anchor, gbc.fill,
            0);
        //Mix Chorus/Flanger
        addWidget(pnl, new ComboBoxWidget("", patch,
            new TCModel(patch, 397),
            new TCSender(patch, 397),
            TCElectronicGMajorUtil.genString(0, 100)),
            6,2,1,1,
            gbc.anchor, gbc.fill,
            0);
        //Mix Delay
        addWidget(pnl, new ComboBoxWidget("", patch,
            new TCModel(patch, 473),
            new TCSender(patch, 473),
            TCElectronicGMajorUtil.genString(0, 100)),
            7,2,1,1,
            gbc.anchor, gbc.fill,
            0);
        //Mix Reverb
        addWidget(pnl, new ComboBoxWidget("", patch,
            new TCModel(patch, 533),
            new TCSender(patch, 533),
            TCElectronicGMajorUtil.genString(0, 100)),
            8,2,1,1,
            gbc.anchor, gbc.fill,
            0);

        //Out Filter/Mod
        addWidget(pnl, new ComboBoxWidget("", patch,
            new TCModel(patch, 285, -100),
            new TCSender(patch, 285, -100),
            TCElectronicGMajorUtil.genString(-100, 0)),
            4,3,1,1,
            gbc.anchor, gbc.fill,
            0);
        //Out Pitch
        addWidget(pnl, new ComboBoxWidget("", patch,
            new TCModel(patch, 345, -100),
            new TCSender(patch, 345, -100),
            TCElectronicGMajorUtil.genString(-100, 0)),
            5,3,1,1,
            gbc.anchor, gbc.fill,
            0);
        //Out Chorus/Flanger
        addWidget(pnl, new ComboBoxWidget("", patch,
            new TCModel(patch, 401, -100),
            new TCSender(patch, 401, -100),
            TCElectronicGMajorUtil.genString(-100, 0)),
            6,3,1,1,
            gbc.anchor, gbc.fill,
            0);
        //Out Delay
        addWidget(pnl, new ComboBoxWidget("", patch,
            new TCModel(patch, 477, -100),
            new TCSender(patch, 477, -100),
            TCElectronicGMajorUtil.genString(-100, 0)),
            7,3,1,1,
            gbc.anchor, gbc.fill,
            0);
        //Out Reverb
        addWidget(pnl, new ComboBoxWidget("", patch,
            new TCModel(patch, 537, -100),
            new TCSender(patch, 537, -100),
            TCElectronicGMajorUtil.genString(-100, 0)),
            8,3,1,1,
            gbc.anchor, gbc.fill,
            0);

        return pnl;
    }
}