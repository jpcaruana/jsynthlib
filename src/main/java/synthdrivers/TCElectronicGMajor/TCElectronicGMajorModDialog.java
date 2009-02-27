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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jsynthlib.core.Patch;

public class TCElectronicGMajorModDialog extends JDialog
                                         implements ItemListener {
    private static TCElectronicGMajorModDialog dialog;

    private final String[][] blockString = {
                                                {"[AutoRes] Mix", "[Res] Hi Cut", "[VinPhas] Speed", "[SmthPhas] Speed", "[Tremolo] Speed", "[Pan] Speed"},
                                                {"[AutoRes] OutLev", "[Res] Res", "[VinPhas] Depth", "[SmthPhas] Depth", "[Tremolo] Depth", "[Pan] Width"},
                                                {"", "[Res] Mix", "[VinPhas] FB", "[SmthPhas] FB", "[Tremolo] Hi Cut", "[Pan] OutLev"},
                                                {"", "[Res] OutLev", "[VinPhas] Mix", "[SmthPhas] Mix", "[Tremolo] OutLev", ""},
                                                {"", "", "[VinPhas] OutLev", "[SmthPhas] OutLev", "", ""},
                                                {"[Detune] Mix", "[Whammy] Pitch", "[Octaver] Mix", "[Shifter] Voice1"},
                                                {"[Detune] OutLev", "[Whammy] OutLev", "[Octaver] OutLev", "[Shifter] Voice2"},
                                                {"", "", "", "[Shifter] Pan1"},
                                                {"", "", "", "[Shifter] Pan2"},
                                                {"", "", "", "[Shifter] FB1"},
                                                {"", "", "", "[Shifter] FB2"},
                                                {"", "", "", "[Shifter] Mix"},
                                                {"", "", "", "[Shifter] OutLev"},
                                                {"[ClasCho] Speed", "[AdvCho] Speed", "[ClasFla] Speed", "[AdvFla] Speed", "[Vibrator] Speed"},
                                                {"[ClasCho] Depth", "[AdvCho] Depth", "[ClasFla] Depth", "[AdvFla] Depth", "[Vibrator] Depth"},
                                                {"[ClasCho] Hi Cut", "[AdvCho] Hi Cut", "[ClasFla] Hi Cut", "[AdvFla] Hi Cut", "[Vibrator] Hi Cut"},
                                                {"[ClasCho] Mix", "[AdvCho] Mix", "[ClasFla] FB", "[AdvFla] FB", "[Vibrator] OutLev"},
                                                {"[ClasCho] OutLev", "[AdvCho] OutLev", "[ClasFla] FB Cut", "[AdvFla] FB Cut", ""},
                                                {"", "", "[ClasFla] Mix", "[AdvFla] Mix", ""},
                                                {"", "", "[ClasFla] OutLev", "[AdvFla] OutLev", ""},
                                                {"[PingPong] Dly", "[Dynamic] Dly", "[Dual] Dly1"},
                                                {"[PingPong] FB", "[Dynamic] FB", "[Dual] Dly2"},
                                                {"[PingPong] FB Hi Cut", "[Dynamic] FB Hi Cut", "[Dual] FB1"},
                                                {"[PingPong] FB Lo Cut", "[Dynamic] FB Lo Cut", "[Dual] FB2"},
                                                {"[PingPong] Mix", "[Dynamic] Mix", "[Dual] FB Hi Cut"},
                                                {"[PingPong] OutLev", "[Dynamic] OutLev", "[Dual] FB Lo Cut"},
                                                {"", "", "[Dual] Pan1"},
                                                {"", "", "[Dual] Pan2"},
                                                {"", "", "[Dual] Mix"},
                                                {"", "", "[Dual] OutLev"},
                                                {"[Spring] Mix", "[Hall] Mix", "[Room] Mix", "[Plate] Mix"},
                                                {"[Spring] OutLev", "[Hall] OutLev", "[Room] OutLev", "[Plate] OutLev"}
                                            };

    private JList modList;
    private JLabel modLabel;
    private JComboBox modCombo;
    private JComboBox minCombo;
    private JComboBox midCombo;
    private JComboBox maxCombo;
    private JButton updateButton;

    private Vector listVector;
    private TabListCellRenderer renderer;

    private Patch patch;

    /**
     * Set up and show the dialog.  The first Component argument
     * determines which frame the dialog depends on; it should be
     * a component in the dialog's controlling frame. The second
     * Component argument should be null if you want the dialog
     * to come up with its left corner in the center of the screen;
     * otherwise, it should be the component on top of which the
     * dialog should appear.
     */
    public static void showDialog(Component frameComp,
                                  Component locationComp,
                                  Patch p) {
        Frame frame = JOptionPane.getFrameForComponent(frameComp);
        dialog = new TCElectronicGMajorModDialog(frame,
                                locationComp,
                                p);
    try {
        Thread.sleep(30);
        dialog.setVisible(true);
        Thread.sleep(30);
    } catch (InterruptedException ex) {}
    }

    private TCElectronicGMajorModDialog(Frame frame,
                       Component locationComp,
                       Patch p) {
        super(frame, "Modifier Editor", true);
        patch = p;

        updateButton = new JButton("Update");
        updateButton.setEnabled(false);
        updateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                updateButtonActionPerformed(evt);
            }
        });

        JButton okButton = new JButton("Ok");
        okButton.setActionCommand("Ok");
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        modLabel = new JLabel("");
        modLabel.setMinimumSize(new Dimension(150, 10));
        modLabel.setPreferredSize(new Dimension(150, 10));
        modLabel.setLabelFor(modCombo);

        modCombo = new JComboBox(TCElectronicGMajorConst.modString);
        modCombo.addItemListener(this);
        minCombo = new JComboBox(TCElectronicGMajorUtil.genString(0, 100, "%"));
        minCombo.addItemListener(this);
        midCombo = new JComboBox(TCElectronicGMajorUtil.genString(0, 100, "%"));
        midCombo.addItemListener(this);
        maxCombo = new JComboBox(TCElectronicGMajorUtil.genString(0, 100, "%"));
        maxCombo.addItemListener(this);
        getRootPane().setDefaultButton(okButton);

        listVector = fillList();
        modList = new JList(listVector);
        renderer = new TabListCellRenderer();
        renderer.setTabs(new int[] {150, 210, 280, 350});
        modList.setCellRenderer(renderer);

        modList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                updateInfo();
            }
        });

        modList.setSelectedIndex(0);


        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        JScrollPane listScroller = new JScrollPane(modList);
        listScroller.setPreferredSize(new Dimension(200, 200));
        listScroller.setAlignmentX(LEFT_ALIGNMENT);

        JPanel listPane = new JPanel();
        listPane.setLayout(new BoxLayout(listPane, BoxLayout.PAGE_AXIS));
        JLabel label = new JLabel("Parameter                 Modifier   Min Out      Mid Out      Max Out");
        label.setLabelFor(modList);
        listPane.add(label);
        listPane.add(Box.createRigidArea(new Dimension(0,5)));
        listPane.add(listScroller);
        listPane.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JPanel rightButtonPane = new JPanel();
        rightButtonPane.setLayout(new BoxLayout(rightButtonPane, BoxLayout.Y_AXIS));
        rightButtonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        rightButtonPane.add(Box.createVerticalGlue());
        rightButtonPane.add(okButton);
        rightButtonPane.add(Box.createRigidArea(new Dimension(0, 100)));

        JPanel bottomButtonPane = new JPanel();
        bottomButtonPane.setLayout(new BoxLayout(bottomButtonPane, BoxLayout.LINE_AXIS));
        bottomButtonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        bottomButtonPane.add(modLabel);
        bottomButtonPane.add(Box.createHorizontalGlue());
        bottomButtonPane.add(modCombo);
        bottomButtonPane.add(Box.createHorizontalStrut(5));
        bottomButtonPane.add(minCombo);
        bottomButtonPane.add(Box.createHorizontalStrut(5));
        bottomButtonPane.add(midCombo);
        bottomButtonPane.add(Box.createHorizontalStrut(5));
        bottomButtonPane.add(maxCombo);
        bottomButtonPane.add(Box.createHorizontalStrut(20));
        bottomButtonPane.add(updateButton);

        Container contentPane = getContentPane();
        contentPane.add(listPane, BorderLayout.CENTER);
        contentPane.add(rightButtonPane, BorderLayout.EAST);
        contentPane.add(bottomButtonPane, BorderLayout.PAGE_END);

        pack();
        setLocationRelativeTo(locationComp);
    }

    private void updateInfo() {
        modCombo.removeItemListener(this);
        minCombo.removeItemListener(this);
        midCombo.removeItemListener(this);
        maxCombo.removeItemListener(this);

        TCModListItem tc;
        tc = (TCModListItem)modList.getSelectedValue();

        modLabel.setText(tc.getParam());
        modCombo.setSelectedIndex(tc.getMod());
        minCombo.setSelectedIndex(tc.getMin());
        midCombo.setSelectedIndex(tc.getMid());
        maxCombo.setSelectedIndex(tc.getMax());

        modCombo.addItemListener(this);
        minCombo.addItemListener(this);
        midCombo.addItemListener(this);
        maxCombo.addItemListener(this);
    }

    private void getModItems(Vector v, int from, int to, int ofs) {
        TCBit bit = new TCBit(patch.sysex);
        String param = new String();
        int mod;
        int min;
        int mid;
        int max;

        for (int i = from; i < to; i++) {
            param = blockString[i][TCElectronicGMajorUtil.getValue(patch.sysex, ofs)];

            if (param.length() > 0) {
                bit.setMaskOffset(TCElectronicGMajorConst.MOD_MASK, 29 + i*4);
                mod = bit.get();
                bit.setMaskOffset(TCElectronicGMajorConst.MINOUT_MASK, 29 + i*4);
                min = bit.get();
                bit.setMaskOffset(TCElectronicGMajorConst.MIDOUT_MASK, 30 + i*4);
                mid = bit.get();
                bit.setMaskOffset(TCElectronicGMajorConst.MAXOUT_MASK, 31 + i*4);
                max = bit.get();

                v.add(new TCModListItem(29 + i*4, param, mod, min, mid, max));
            }
        }
    }

   private Vector fillList() {
        Vector v = new Vector(32);

        getModItems(v, 0, 5, TCElectronicGMajorConst.FILTER_TYPE_OFS);
        getModItems(v, 5, 13, TCElectronicGMajorConst.PITCH_TYPE_OFS);
        getModItems(v, 13, 20, TCElectronicGMajorConst.CHORUS_TYPE_OFS);
        getModItems(v, 20, 30, TCElectronicGMajorConst.DELAY_TYPE_OFS);
        getModItems(v, 30, 32, TCElectronicGMajorConst.REVERB_TYPE_OFS);

        return v;
    }

    private void okButtonActionPerformed(ActionEvent evt) {
        setVisible(false);
        dispose();

    }

    private void updateButtonActionPerformed(ActionEvent evt) {
        TCBit bit = new TCBit(patch.sysex);
        for (int i = 0; i < listVector.size(); i++ ) {
            TCModListItem tc = (TCModListItem)listVector.elementAt(i);
            bit.setMaskOffset(TCElectronicGMajorConst.MOD_MASK, tc.getOfs());
            bit.set(tc.getMod());
            bit.setMaskOffset(TCElectronicGMajorConst.MINOUT_MASK, tc.getOfs());
            bit.set(tc.getMin());
            bit.setMaskOffset(TCElectronicGMajorConst.MIDOUT_MASK, tc.getOfs() + 1);
            bit.set(tc.getMid());
            bit.setMaskOffset(TCElectronicGMajorConst.MAXOUT_MASK, tc.getOfs() + 2);
            bit.set(tc.getMax());
        }
        patch.send();
        updateButton.setEnabled(false);
    }

    private void closeDialog(WindowEvent evt) {
        setVisible(false);
        dispose();
    }

    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {

            TCModListItem tc = (TCModListItem)modList.getSelectedValue();
            listVector.set(modList.getSelectedIndex(),
                           new TCModListItem(tc.getOfs(), tc.getParam(), modCombo.getSelectedIndex(), minCombo.getSelectedIndex(), midCombo.getSelectedIndex(), maxCombo.getSelectedIndex()));
            modList.repaint();

            if (! updateButton.isEnabled()) {
                updateButton.setEnabled(true);
            }

        }
    }

}
