/*
 * Copyright 2004 Sander Brandenburg
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

package org.jsynthlib.drivers.roland.jv80;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.jsynthlib.core.CheckBoxWidget;
import org.jsynthlib.core.ComboBoxWidget;
import org.jsynthlib.core.DriverUtil;
import org.jsynthlib.core.EnvelopeWidget;
import org.jsynthlib.core.ParamModel;
import org.jsynthlib.core.Patch;
import org.jsynthlib.core.PatchEditorFrame;
import org.jsynthlib.core.PatchNameWidget;
import org.jsynthlib.core.ScrollBarLookupWidget;
import org.jsynthlib.core.ScrollBarWidget;
import org.jsynthlib.core.SysexSender;


/**
 * @author Sander Brandenburg
 * @version $Id$
 */
public class RolandJV80PatchEditor extends PatchEditorFrame {
 	class JV2Model extends JVModel {
 	   JV2Model(Patch p, int tone, int msg_offset) {
 	       super(p, tone, msg_offset);
 	   }
 	   public int get() {
 	       return (patch.sysex[ofs] << 4) + (patch.sysex[ofs+1]);
 	   }
 	   public void set(int value) {
 	       patch.sysex[ofs] = (byte) (value >> 4);
 	       patch.sysex[ofs + 1] = (byte) (value & 0x0F);
 	   }
	}

 	class JV2Sender extends SysexSender {
	    byte addr3; byte addr4;
	    // retrieve default from patch
		public JV2Sender(int tone, int msg_offset) {
		    super("F041@@461200082000****00F7");
		    addr3 = 0x20;
		    if (tone >= 0)
		        addr3 += 8 + tone;
		    addr4 = (byte) msg_offset;
		}

        protected byte[] generate(int value) {
            byte[] data = super.generate(value);
            data[JV80Constants.ADDR3_IDX] = addr3;
            data[JV80Constants.ADDR4_IDX] = addr4;
            data[JV80Constants.ADDR4_IDX + 1] = (byte) (value >> 4);
            data[JV80Constants.ADDR4_IDX + 2] = (byte) (value & 0x0F);
            JV80Constants.calculateChecksum(data, 2);
            return data;
        }
	}
 	class JVModel extends ParamModel {
	    final static int DATA_OFFSET = 9;
	    // tone == -1 -> common
	    JVModel(Patch p, int tone, int msg_offset) {
	        super(p, DATA_OFFSET + msg_offset);
	        if (tone >= 0)
	            ofs += ((RolandJV80PatchDriver)p.getDriver()).patchToneOffsets[tone];
	    }
	}

 	// sends to patch mode temporary patch
 	class JVSender extends SysexSender {
	    byte addr3; byte addr4;
	    // retrieve default from patch
		public JVSender(int tone, int msg_offset) {
		    super("F041@@461200082000**00F7");
		    addr3 = 0x20;
		    if (tone >= 0)
		        addr3 += 8 + tone;
		    addr4 = (byte) msg_offset;
		}

        protected byte[] generate(int value) {
            byte[] data = super.generate(value);
            data[JV80Constants.ADDR3_IDX] = addr3;
            data[JV80Constants.ADDR4_IDX] = addr4;
            JV80Constants.calculateChecksum(data, 1);
            return data;
        }
	}

    static final String[] KEY_FOLLOW_100 = { "-100", "-70", "-50", "-40", "-30", "-20",
            "-10", "0", "+10", "+20", "+30", "+40", "+50", "+70", "+100"
    };
    static final String[] KEY_FOLLOW_200 = { "-100", "-70", "-50", "-30", "-10", "0",
        "+10", "+20", "+30", "+40", "+50", "+70", "+100", "+120", "+150", "+200"
    };

    final boolean isJV80;
    public RolandJV80PatchEditor(final Patch patch) {
        super("Roland JV80 Patch Editor", patch);

        ((RolandJV80Device) patch.getDevice()).getPatchDriver().setPatchNum(
                patch.sysex, -1, -1);

        isJV80 = ((RolandJV80PatchDriver) p.getDriver()).isJV80;

        // set to patch mode
        RolandJV80SystemSetupEditor.JVSender js = new RolandJV80SystemSetupEditor.JVSender(0);
        js.send(((RolandJV80Device) patch.getDevice()).getSystemSetupDriver(), 1);

        buildEditor(patch);
    }

    JPanel buildChorus(Patch patch) {
        GridBagConstraints gbc = new GridBagConstraints();
        JPanel panel = new JPanel(new GridBagLayout());
	    panel.setBorder(new TitledBorder(new EtchedBorder(
	                EtchedBorder.RAISED),"Chorus",
	                TitledBorder.CENTER,TitledBorder.CENTER));

	    gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
	    gbc.insets.left = gbc.insets.right = 4; gbc.insets.bottom = gbc.insets.bottom = 0;

	    gbc.gridy++; panel.add(new JLabel("Chorus Type"), gbc);
	    gbc.gridy++; panel.add(new JLabel("Chorus Level"), gbc);
	    gbc.gridy++; panel.add(new JLabel("Chorus Depth"), gbc);
	    gbc.gridy++; panel.add(new JLabel("Chorus Rate"), gbc);
	    gbc.gridy++; panel.add(new JLabel("Chorus Feedback"), gbc);
	    gbc.gridy++; panel.add(new JLabel("Chorus Output"), gbc);

	    gbc.gridx = 1; gbc.gridy = 0;
	    gbc.gridy++; panel.add(new ComboBoxWidget(null, patch,
		        new JVModel(patch, -1, 0x11),
		        new JVSender(-1, 0x11),
		        new String[] {"CHORUS1", "CHORUS2", "CHORUS3" }), gbc);
	    gbc.gridy++; panel.add(new ScrollBarWidget(null, patch,
                0, 127, 0, new JVModel(patch, -1, 0x12),
                new JVSender(-1, 0x12)), gbc);
	    gbc.gridy++; panel.add(new ScrollBarWidget(null, patch,
                0, 127, 0, new JVModel(patch, -1, 0x13),
                new JVSender(-1, 0x13)), gbc);
	    gbc.gridy++; panel.add(new ScrollBarWidget(null, patch,
                0, 127, 0, new JVModel(patch, -1, 0x14),
                new JVSender(-1, 0x14)), gbc);
	    gbc.gridy++; panel.add(new ScrollBarWidget(null, patch,
                0, 127, 0, new JVModel(patch, -1, 0x15),
                new JVSender(-1, 0x15)),  gbc);
	    gbc.gridy++; panel.add(new ComboBoxWidget(null, patch,
		        new JVModel(patch, -1, 0x11),
		        new JVSender(-1, 0x11),
		        new String[] {"MIX", "REV" }), gbc);

	    return panel;
    }

    JPanel buildCommon(Patch patch) {
        GridBagConstraints gbc = new GridBagConstraints();

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new TitledBorder(new EtchedBorder(
                EtchedBorder.RAISED),"Common",
                TitledBorder.CENTER,TitledBorder.CENTER));

        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        gbc.insets.left = gbc.insets.right = 4; gbc.insets.bottom = gbc.insets.bottom = 0;

        gbc.gridy++; panel.add(new JLabel("Patch Name"), gbc);
        gbc.gridy++; panel.add(new JLabel("Patch level"), gbc);
        gbc.gridy++; panel.add(new JLabel("Patch pan"), gbc);
        gbc.gridy++; panel.add(new JLabel("Velocity Switch"), gbc);
        gbc.gridy++; panel.add(new JLabel("Analog Feel"), gbc);
        gbc.gridy++; panel.add(new JLabel("Bend Range Down"), gbc);
        gbc.gridy++; panel.add(new JLabel("Bend Range Up"), gbc);
        gbc.gridy++; panel.add(new JLabel("Key Assign"), gbc);
        gbc.gridy++; panel.add(new JLabel("Solo Legato"), gbc);

        gbc.gridx = 1; gbc.gridy = 0;

        gbc.gridy++; panel.add(new PatchNameWidget(null, patch), gbc);
        gbc.gridy++; panel.add(new ScrollBarWidget(null, patch, 0, 127, 0,
                new JVModel(patch, -1, 0x18), new JVSender(-1, 0x18)), gbc);
        gbc.gridy++; panel.add(new ScrollBarWidget(null, patch, 0, 127, 0,
                new JVModel(patch, -1, 0x19), new JVSender(-1, 0x19)), gbc);
        gbc.gridy++; panel.add(new CheckBoxWidget(null, patch,
				new JVModel(patch, -1, 0x0C),
				new JVSender(-1, 0x0C)), gbc);
        gbc.gridy++; panel.add(new ScrollBarWidget(null, patch,
                0, 127, 0, new JVModel(patch, -1, 0x17),
                new JVSender(-1, 0x17)), gbc);
        gbc.gridy++; panel.add(new ScrollBarWidget(null, patch,
                16, 64, -64, new JVModel(patch, -1, 0x1A),
                new JVSender(-1, 0x1A)), gbc);
        gbc.gridy++; panel.add(new ScrollBarWidget(null, patch,
                0, 12, 0, new JVModel(patch, -1, 0x1B),
                new JVSender(-1, 0x1B)), gbc);
        gbc.gridy++; panel.add(new ComboBoxWidget(null, patch,
		        new JVModel(patch, -1, 0x1C),
		        new JVSender(-1, 0x1C),
		        new String[] {"POLY", "SOLO" }), gbc);
        gbc.gridy++; panel.add(new CheckBoxWidget(null, patch,
				new JVModel(patch, -1, 0x1D),
				new JVSender(-1, 0x1D)), gbc);

		return panel;
    }

    JPanel buildHeader(Patch patch) {
        GridBagConstraints gbc = new GridBagConstraints();

        JPanel panel = new JPanel(new GridBagLayout());

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets.left = gbc.insets.right = gbc.insets.top = gbc.insets.bottom = 4;

        panel.add(new JLabel("Enable Tones"), gbc);
        for (int i = 0; i < 4; i++) {
            gbc.gridx++;
            panel.add(new CheckBoxWidget("" + (i+1), patch,
    				new JVModel(patch, i, 0x03),
    				new JVSender(i, 0x03)), gbc);
        }
        gbc.gridx++; gbc.weightx = 1;
        panel.add(Box.createHorizontalGlue(), gbc);
        gbc.weightx = 0;
        /*final JButton save = new JButton("Save");
        final JButton cancel = new JButton("Cancel");
        final JButton clipboard = new JButton("To Clipboard");

        ActionListener al = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == save) {

                } else if (e.getSource() == cancel) {
                    p = originalPatch; // FIXME <-- originalPatch not visible - made this code useless
                } else if (e.getSource() == clipboard) {
                    copySelectedPatch();
                }
                dispose();
            }
        };
        save.addActionListener(al);
        cancel.addActionListener(al);
        clipboard.addActionListener(al);

        gbc.gridx++; panel.add(save, gbc);
        gbc.gridx++; panel.add(cancel, gbc);
        gbc.gridx++; panel.add(clipboard, gbc); */

        return panel;
    }

    void buildEditor(Patch patch) {
        GridBagConstraints	gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill   = GridBagConstraints.BOTH;

        JPanel panel = new JPanel(new GridBagLayout());
        gbc.insets.left = gbc.insets.right = 2;
        gbc.insets.top = gbc.insets.bottom = 0;

	    gbc.gridx = 0; gbc.gridy = 0; gbc.gridheight = 2;
	    panel.add(buildCommon(patch), gbc);

	    gbc.gridheight = 1; gbc.gridy = 2;
	    panel.add(buildChorus(patch), gbc);

	    gbc.gridy++; panel.add(buildReverb(patch), gbc);
        gbc.gridy++; panel.add(buildPortamento(patch), gbc);

        gbc.gridy = 0; gbc.gridx = 1;
	    panel.add(buildHeader(patch), gbc);
	    gbc.gridy = 1; gbc.gridheight = 5;

        JTabbedPane tones = new JTabbedPane();
        panel.add(tones, gbc);

	    for (int i = 0; i < 4; i++) {
	        JTabbedPane part = new JTabbedPane();

	        JPanel general = new JPanel(new GridBagLayout());
	        gbc.gridx = 0; gbc.gridy = 0; gbc.gridheight = 1;

	        JPanel wavegroup = buildToneWaveGroup(patch, i);
	        wavegroup.setBorder(new TitledBorder(new EtchedBorder(
	                EtchedBorder.RAISED),"Wave Group",
	                TitledBorder.CENTER,TitledBorder.CENTER));
	        JPanel velocity = buildVelocity(patch, i);
	        velocity.setBorder(new TitledBorder(new EtchedBorder(
	                EtchedBorder.RAISED),"Velocity",
	                TitledBorder.CENTER,TitledBorder.CENTER));
	        JPanel output = buildOutput(patch, i);
	        output.setBorder(new TitledBorder(new EtchedBorder(
	                EtchedBorder.RAISED),"Output",
	                TitledBorder.CENTER,TitledBorder.CENTER));
	        gbc.gridy++; general.add(wavegroup, gbc);
	        gbc.gridy++; general.add(velocity, gbc);
	        gbc.gridy++; general.add(output, gbc);
	        part.add("General", general);

		    part.add("TVA",            buildTVA(patch, i));
		    part.add("TVF",            buildTVF(patch, i));
		    part.add("Pitch",          buildPitch(patch, i));
		    part.add("LFO",            buildLFOs(patch, i));
		    part.add("Control Change", buildControlChange(patch, i));

		    tones.add("Tone " + (i+1), part);
	    }
	    scrollPane.add(panel);

	    pack();
    }

    JPanel buildPortamento(Patch patch) {
        GridBagConstraints gbc = new GridBagConstraints();

		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(new TitledBorder(new EtchedBorder(
	                EtchedBorder.RAISED),"Portamento",
	                TitledBorder.CENTER,TitledBorder.CENTER));

		gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
		gbc.insets.left = gbc.insets.right = 4; gbc.insets.bottom = gbc.insets.bottom = 0;

		gbc.gridy++; panel.add(new JLabel("Portamento Switch"),  gbc);
		gbc.gridy++; panel.add(new JLabel("Portamento Mode"),  gbc);
		gbc.gridy++; panel.add(new JLabel("Portamento Type"),  gbc);
		gbc.gridy++; panel.add(new JLabel("Portamento Time"),  gbc);

		gbc.gridx = 1; gbc.gridy = 0;
		gbc.gridy++; panel.add(new CheckBoxWidget(null, patch,
		        new JVModel(patch, -1, 0x1E),
		        new JVSender(-1, 0x1E)),  gbc);
		gbc.gridy++; panel.add(new ComboBoxWidget(null, patch,
		        new JVModel(patch, -1, 0x1F),
		        new JVSender(-1, 0x1F),
		        new String[] {"LEGATO", "NORMAL" }),  gbc);
		gbc.gridy++; panel.add(new ComboBoxWidget(null, patch,
		        new JVModel(patch, -1, 0x20),
		        new JVSender(-1, 0x20),
		        new String[] {"TIME", "RATE" }),  gbc);
		gbc.gridy++; panel.add(new ScrollBarWidget(null, patch,
                0, 127, 0, new JVModel(patch, -1, 0x21),
                new JVSender(-1, 0x21)),  gbc);
		return panel;
    }

    JPanel buildReverb(Patch patch) {
        GridBagConstraints gbc = new GridBagConstraints();

		JPanel panel = new JPanel(new GridBagLayout());
	    panel.setBorder(new TitledBorder(new EtchedBorder(
	                EtchedBorder.RAISED),"Reverb",
	                TitledBorder.CENTER,TitledBorder.CENTER));

	    gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
	    gbc.insets.left = gbc.insets.right = 4; gbc.insets.bottom = gbc.insets.bottom = 0;

	    gbc.gridy++; panel.add(new JLabel("Reverb Type"), gbc);
	    gbc.gridy++; panel.add(new JLabel("Reverb Level"), gbc);
	    gbc.gridy++; panel.add(new JLabel("Reverb Time"), gbc);
	    gbc.gridy++; panel.add(new JLabel("Delay Feedback"), gbc);

	    gbc.gridx = 1; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
	    gbc.gridy++; panel.add(new ComboBoxWidget(null, patch,
		        new JVModel(patch, -1, 0x0D),
		        new JVSender(-1, 0x0D),
		        new String[] {"ROOM1", "ROOM2", "STAGE1", "HALL1",
	        "HALL2", "DELAY", "PAN-DLY" }), gbc);
	    gbc.gridy++; panel.add(new ScrollBarWidget(null, patch,
                0, 127, 0, new JVModel(patch, -1, 0x0E),
                new JVSender(-1, 0x0E)), gbc);
	    gbc.gridy++; panel.add(new ScrollBarWidget(null, patch,
                0, 127, 0, new JVModel(patch, -1, 0x0F),
                new JVSender(-1, 0x0F)), gbc);
	    gbc.gridy++; panel.add(new ScrollBarWidget(null, patch,
                0, 127, 0, new JVModel(patch, -1, 0x10),
                new JVSender(-1, 0x10)), gbc);

        return panel;
    }

    JPanel buildToneWaveGroup(final Patch patch, final int tone) {
        GridBagConstraints gbc = new GridBagConstraints();
        JPanel panel = new JPanel(new GridBagLayout());

        gbc.insets.left = gbc.insets.right = 4; gbc.insets.top = gbc.insets.bottom = 2;
//        panel.setBorder(new TitledBorder(new EtchedBorder(
//                EtchedBorder.RAISED),"Wave Group",
//                TitledBorder.CENTER,TitledBorder.CENTER));

        final JComboBox bank  = new JComboBox(RolandJV80WaveBank.getAvailableBanks(patch.getDevice()));
	    final JComboBox waves = new JComboBox();
	    bank.addActionListener(new ActionListener() {
	        final JVModel jm    = new JVModel(patch, tone, 0x00);
	        final JVSender js   = new JVSender(tone, 0x00);
	        { bank.setSelectedIndex(jm.get()); actionPerformed(null);}
	        public void actionPerformed(ActionEvent e) {
	            RolandJV80WaveBank wb = (RolandJV80WaveBank) bank.getSelectedItem();
	            waves.setModel(new DefaultComboBoxModel(wb.getWaves()));
	            if (e != null) {
	                jm.set(wb.getBankType());
	                js.send(patch.getDriver(), wb.getBankType());
	            }
            }
	    });
	    waves.addActionListener(new ActionListener() {
	        final JV2Model j2m  = new JV2Model(patch, tone, 0x01);
	        final JV2Sender j2s = new JV2Sender(tone, 0x01);
	        {
	            int value = j2m.get();
	            if (value < waves.getItemCount())
	                waves.setSelectedIndex(value);
	        }
            public void actionPerformed(ActionEvent e) {
                j2m.set(waves.getSelectedIndex());
                j2s.send(patch.getDriver(), waves.getSelectedIndex());
            }
	    });

	    gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
	    panel.add(new JLabel("Wave Group"), gbc);
	    gbc.gridy++; panel.add(new JLabel("Wave Number"), gbc);
	    gbc.gridy++; panel.add(new JLabel("FXM Switch"), gbc);
	    gbc.gridy++; panel.add(new JLabel("FXM Depth"), gbc);

	    gbc.gridx = 1; gbc.gridy = 0;
	    panel.add(bank, gbc);
	    gbc.gridy++; panel.add(waves, gbc);
	    gbc.gridy++; panel.add(new CheckBoxWidget(null, patch,
				new JVModel(patch, tone, 0x04),
				new JVSender(tone, 0x04)), gbc);
	    gbc.gridy++; panel.add(new ScrollBarWidget(null, patch,
                0, 15, 1, new JVModel(patch, tone, 0x05),
                new JVSender(tone, 0x05)), gbc);
        return panel;
    }

    JPanel buildTVA(final Patch patch, final int tone) {
        GridBagConstraints gbc = new GridBagConstraints();
        JPanel panel = new JPanel(new GridBagLayout());

//        panel.setBorder(new TitledBorder(new EtchedBorder(
//                EtchedBorder.RAISED),"TVA",
//                TitledBorder.CENTER,TitledBorder.CENTER));

        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy++; panel.add(new JLabel("Level"), gbc);
        gbc.gridy++; panel.add(new JLabel("Level Key Follow"), gbc);
        gbc.gridy++; panel.add(new JLabel("Pan"), gbc);
        gbc.gridy++; panel.add(new JLabel("Panning Key Follow"), gbc);
        gbc.gridy++; panel.add(new JLabel("Tone Delay Mode"), gbc);
        gbc.gridy++; panel.add(new JLabel("Tone Delay Time"), gbc);
        gbc.gridy++; panel.add(new JLabel("Velocity Curve"), gbc);
        gbc.gridy++; panel.add(new JLabel("Velocity Level Sense"), gbc);
        gbc.gridy++; panel.add(new JLabel("Velocity On Time Sense"), gbc);
        gbc.gridy++; panel.add(new JLabel("Velocity Off Time Sense"), gbc);
        gbc.gridy++; panel.add(new JLabel("Time Key Follow"), gbc);

        List pan = new ArrayList();

        pan.addAll(Arrays.asList(DriverUtil.generateNumbers(-64, -1, "L00; L00")));
        pan.add("Center");
        pan.addAll(Arrays.asList(DriverUtil.generateNumbers(1, 63, "00R")));
        pan.add("Random");
        String pans[] = (String[]) pan.toArray(new String[pan.size()]);

        gbc.gridx = 1; gbc.gridy = 0;
        gbc.gridy++; panel.add(new ScrollBarWidget(null, patch,
                0, 127, 0, new JVModel(patch, tone, 0x5C),
                new JVSender(tone, 0x5C)), gbc);
        gbc.gridy++; panel.add(new ScrollBarLookupWidget(null, patch,
                0, KEY_FOLLOW_100.length - 1, new JVModel(patch, tone, 0x5D),
                new JVSender(tone, 0x5D), KEY_FOLLOW_100), gbc);
        gbc.gridy++; panel.add(new ScrollBarLookupWidget(null, patch,
                0, pans.length - 1, new JV2Model(patch, tone, 0x5E),
                new JV2Sender(tone, 0x5E), pans), gbc);
        gbc.gridy++; panel.add(new ScrollBarLookupWidget(null, patch,
                0, KEY_FOLLOW_100.length - 1, new JVModel(patch, tone, 0x60),
                new JVSender(tone, 0x60), KEY_FOLLOW_100), gbc);
        gbc.gridy++; panel.add(new ComboBoxWidget(null, patch,
                0, new JVModel(patch, tone, 0x61),
                new JVSender(tone, 0x61),
                new String[] { "NORMAL", "HOLD", "PLAY-MATE"}), gbc);

        final String[] TONEDELAYTIME = DriverUtil.generateNumbers(0, 128, "0");
        TONEDELAYTIME[128] = "KEY-OFF";
        gbc.gridy++; panel.add(new ScrollBarLookupWidget(null, patch,
                0, TONEDELAYTIME.length - 1, new JV2Model(patch, tone, 0x62),
                new JV2Sender(tone, 0x62), TONEDELAYTIME), gbc);
        gbc.gridy++; panel.add(new ScrollBarWidget(null, patch,
                0, 6, 1, new JVModel(patch, tone, 0x64),
                new JVSender(tone, 0x64)), gbc);
        gbc.gridy++; panel.add(new ScrollBarWidget(null, patch,
                1, 127, -64, new JVModel(patch, tone, 0x65),
                new JVSender(tone, 0x65)), gbc);
        gbc.gridy++; panel.add(new ScrollBarLookupWidget(null, patch,
                0, KEY_FOLLOW_100.length - 1, new JVModel(patch, tone, 0x66),
                new JVSender(tone, 0x66), KEY_FOLLOW_100), gbc);
        gbc.gridy++; panel.add(new ScrollBarLookupWidget(null, patch,
                0, KEY_FOLLOW_100.length - 1, new JVModel(patch, tone, 0x67),
                new JVSender(tone, 0x67), KEY_FOLLOW_100), gbc);
        gbc.gridy++; panel.add(new ScrollBarLookupWidget(null, patch,
                0, KEY_FOLLOW_100.length - 1, new JVModel(patch, tone, 0x68),
                new JVSender(tone, 0x68), KEY_FOLLOW_100), gbc);
        gbc.fill = GridBagConstraints.BOTH; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.gridy++; gbc.gridheight=8; panel.add(new EnvelopeWidget(null, patch,
                new EnvelopeWidget.Node[] {
    				new EnvelopeWidget.Node(0, 0, null, 0, 0, null, 0, false, null, null, null, null),
                	new EnvelopeWidget.Node(0, 127, new JVModel(patch, tone, 0x69), 0, 127, new JVModel(patch, tone, 0x6A), 0, false, new JVSender(tone, 0x69), new JVSender(tone, 0x6A), "T1 Time", "T1 Level"),
                	new EnvelopeWidget.Node(0, 127, new JVModel(patch, tone, 0x6B), 0, 127, new JVModel(patch, tone, 0x6C), 0, false, new JVSender(tone, 0x6B), new JVSender(tone, 0x6C), "T2 Time", "T2 Level"),
                	new EnvelopeWidget.Node(0, 127, new JVModel(patch, tone, 0x6D), 0, 127, new JVModel(patch, tone, 0x6E), 0, false, new JVSender(tone, 0x6D), new JVSender(tone, 0x6E), "T3 Time", "T3 Level"),
                	new EnvelopeWidget.Node(0, 127, new JVModel(patch, tone, 0x6F), 0, 0,   null,                           0, false, new JVSender(tone, 0x6F), null, "T4 Time", null),
        }), gbc);

        return panel;
    }

    JPanel buildVelocity(Patch patch, int tone) {
        GridBagConstraints gbc = new GridBagConstraints();
        JPanel panel = new JPanel(new GridBagLayout());

        gbc.insets.left = gbc.insets.right = 4; gbc.insets.top = gbc.insets.bottom = 2;

//        panel.setBorder(new TitledBorder(new EtchedBorder(
//                EtchedBorder.RAISED),"Velocity",
//                TitledBorder.CENTER,TitledBorder.CENTER));

        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy++; panel.add(new JLabel("Velocity Range Lower"), gbc);
        gbc.gridy++; panel.add(new JLabel("Velocity Range Upper"), gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        gbc.gridy++; panel.add(new ScrollBarWidget(null, patch, 0, 127, 0,
                new JVModel(patch, tone, 0x06), new JVSender(tone, 0x06)), gbc);
        gbc.gridy++; panel.add(new ScrollBarWidget(null, patch, 0, 127, 0,
                new JVModel(patch, tone, 0x07), new JVSender(tone, 0x07)), gbc);

        return panel;
    }

    JPanel buildOutput(Patch patch, int tone) {
        GridBagConstraints gbc = new GridBagConstraints();
        JPanel panel = new JPanel(new GridBagLayout());

        gbc.insets.left = gbc.insets.right = 4; gbc.insets.top = gbc.insets.bottom = 2;
//        panel.setBorder(new TitledBorder(new EtchedBorder(
//                EtchedBorder.RAISED),"Output",
//                TitledBorder.CENTER,TitledBorder.CENTER));

        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy++; panel.add(new JLabel("Dry Level"), gbc);
        gbc.gridy++; panel.add(new JLabel("Reverb Send Level"), gbc);
        gbc.gridy++; panel.add(new JLabel("Chorus Send Level"), gbc);
        if (!isJV80) {
            gbc.gridy++; panel.add(new JLabel("Output Select"), gbc);
        }

        gbc.gridx = 1; gbc.gridy = 0;
        gbc.gridy++; panel.add(new ScrollBarWidget(null, patch, 0, 127, 0,
                new JVModel(patch, tone, 0x70), new JVSender(tone, 0x70)), gbc);
        gbc.gridy++; panel.add(new ScrollBarWidget(null, patch, 0, 127, 0,
                new JVModel(patch, tone, 0x71), new JVSender(tone, 0x71)), gbc);
        gbc.gridy++; panel.add(new ScrollBarWidget(null, patch, 0, 127, 0,
                new JVModel(patch, tone, 0x72), new JVSender(tone, 0x72)), gbc);
        if (!isJV80) {
	        gbc.gridy++; panel.add(new ComboBoxWidget(null, patch, 0,
	                new JVModel(patch, tone, 0x73), new JVSender(tone, 0x73),
	                new String[] { "MAIN", "SUB"}), gbc);
        }

        return panel;
    }

    JPanel buildTVF(Patch patch, int tone) {
        GridBagConstraints gbc = new GridBagConstraints();
        JPanel panel = new JPanel(new GridBagLayout());

//        panel.setBorder(new TitledBorder(new EtchedBorder(
//                EtchedBorder.RAISED), "TVF Envelope",
//                TitledBorder.CENTER,TitledBorder.CENTER));

        final ImageIcon[] velocityCurves = new ImageIcon[7];
        for (int i = 0; i < 7; i++) {
        	velocityCurves[i] = new ImageIcon(getClass().getResource("images/velocity" + (i+1) + ".png"));
        }

        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy++; panel.add(new JLabel("Filter Mode"), gbc);
        gbc.gridy++; panel.add(new JLabel("Cut-off Frequency"), gbc);
        gbc.gridy++; panel.add(new JLabel("Resonance"), gbc);
        gbc.gridy++; panel.add(new JLabel("Resonance Mode"), gbc);
        gbc.gridy++; panel.add(new JLabel("Cut-off Key Follow"), gbc);
        gbc.gridy++; panel.add(new JLabel("Velocity Curve"), gbc);
        gbc.gridy++; panel.add(new JLabel("Velocity Level Sense"), gbc);
        gbc.gridy++; panel.add(new JLabel("Velocity On Time Sense"), gbc);
        gbc.gridy++; panel.add(new JLabel("Velocity Off Time Sense"), gbc);
        gbc.gridy++; panel.add(new JLabel("Time Key Follow"), gbc);
        gbc.gridy++; panel.add(new JLabel("Depth"), gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        gbc.gridy++; panel.add(new ComboBoxWidget(null, patch, 0,
                new JVModel(patch, tone, 0x49), new JVSender(tone, 0x49),
                new String[] {"OFF", "LPF", "HPF"}), gbc);
        gbc.gridy++; panel.add(new ScrollBarWidget(null, patch, 0, 127, 0,
                new JVModel(patch, tone, 0x4A), new JVSender(tone, 0x4A)),
                gbc);
        gbc.gridy++; panel.add(new ScrollBarWidget(null, patch, 0, 127, 0,
                new JVModel(patch, tone, 0x4B), new JVSender(tone, 0x4B)),
                gbc);
        gbc.gridy++; panel.add(new ComboBoxWidget(null, patch, 0,
                new JVModel(patch, tone, 0x4C), new JVSender(tone, 0x4C),
                new String[] {"SOFT", "HARD"}), gbc);
        gbc.gridy++; panel.add(new ScrollBarLookupWidget(null, patch,
                0, KEY_FOLLOW_200.length - 1, new JVModel(patch, tone, 0x4D),
                new JVSender(tone, 0x4D), KEY_FOLLOW_200), gbc);
        gbc.gridy++; panel.add(new ComboBoxWidget(null, patch, 0,
                new JVModel(patch, tone, 0x4E), new JVSender(tone, 0x4E),
                velocityCurves), gbc);
        gbc.gridy++; panel.add(new ScrollBarWidget(null, patch, 1, 127, -64,
                new JVModel(patch, tone, 0x4F), new JVSender(tone, 0x4F)),
                gbc);
        gbc.gridy++; panel.add(new ScrollBarLookupWidget(null, patch,
                0, KEY_FOLLOW_100.length - 1, new JVModel(patch, tone, 0x50),
                new JVSender(tone, 0x50), KEY_FOLLOW_100), gbc);
        gbc.gridy++; panel.add(new ScrollBarLookupWidget(null, patch,
                0, KEY_FOLLOW_100.length - 1, new JVModel(patch, tone, 0x51),
                new JVSender(tone, 0x51), KEY_FOLLOW_100), gbc);
        gbc.gridy++; panel.add(new ScrollBarLookupWidget(null, patch,
                0, KEY_FOLLOW_100.length - 1, new JVModel(patch, tone, 0x52),
                new JVSender(tone, 0x52), KEY_FOLLOW_100), gbc);
        gbc.gridy++; panel.add(new ScrollBarWidget(null, patch, 1, 127, -64,
                new JVModel(patch, tone, 0x53), new JVSender(tone, 0x53)),
                gbc);

        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2; gbc.gridheight=8;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(new EnvelopeWidget(null, patch,
                new EnvelopeWidget.Node[] {
    				new EnvelopeWidget.Node(0, 0,   null,                           0, 0,   null,                           0, false, null,                            null,                            null,      null),
                	new EnvelopeWidget.Node(0, 127, new JVModel(patch, tone, 0x54), 0, 127, new JVModel(patch, tone, 0x55), 0, false, new JVSender(tone, 0x54), new JVSender(tone, 0x55), "T1 Time", "T1 Level"),
                	new EnvelopeWidget.Node(0, 127, new JVModel(patch, tone, 0x56), 0, 127, new JVModel(patch, tone, 0x57), 0, false, new JVSender(tone, 0x56), new JVSender(tone, 0x57), "T2 Time", "T2 Level"),
                	new EnvelopeWidget.Node(0, 127, new JVModel(patch, tone, 0x58), 0, 127, new JVModel(patch, tone, 0x59), 0, false, new JVSender(tone, 0x58), new JVSender(tone, 0x59), "T3 Time", "T3 Level"),
                	new EnvelopeWidget.Node(0, 127, new JVModel(patch, tone, 0x5A), 0, 127, new JVModel(patch, tone, 0x5B), 0, false, new JVSender(tone, 0x5A), new JVSender(tone, 0x5B), "T4 Time", "T4 Level"),
        }), gbc);
        return panel;
    }

    JPanel buildPitch(Patch patch, int tone) {
        GridBagConstraints gbc = new GridBagConstraints();
        JPanel panel = new JPanel(new GridBagLayout());

//        panel.setBorder(new TitledBorder(new EtchedBorder(
//                EtchedBorder.RAISED), "Pitch Envelope",
//                TitledBorder.CENTER,TitledBorder.CENTER));

        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy++; panel.add(new JLabel("Pitch Coarse"), gbc);
        gbc.gridy++; panel.add(new JLabel("Pitch Fine"), gbc);
        gbc.gridy++; panel.add(new JLabel("Random Pitch Depth"), gbc);
        gbc.gridy++; panel.add(new JLabel("Pitch Key Follow"), gbc);
        gbc.gridy++; panel.add(new JLabel("Velocity Level Sense"), gbc);
        gbc.gridy++; panel.add(new JLabel("Velocity On Time Sense"), gbc);
        gbc.gridy++; panel.add(new JLabel("Velocity Off Time Sense"), gbc);
        gbc.gridy++; panel.add(new JLabel("Time Key Follow"), gbc);
        gbc.gridy++; panel.add(new JLabel("Depth"), gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        gbc.gridy++; panel.add(new ScrollBarWidget(null, patch, 16, 112, -64,
                new JVModel(patch, tone, 0x38), new JVSender(tone, 0x38)),
                gbc);
        gbc.gridy++; panel.add(new ScrollBarWidget(null, patch, 14, 114, -64,
                new JVModel(patch, tone, 0x39), new JVSender(tone, 0x39)),
                gbc);
        final String[] RANDOM_PITCH_DEPTHS = new String[] {
        	"0", "5", "10", "20", "30", "40", "50", "70", "100", "200", "300",
        	"400", "500", "600", "800", "1200"
        };
        gbc.gridy++; panel.add(new ScrollBarLookupWidget(null, patch,
                0, RANDOM_PITCH_DEPTHS.length - 1, new JVModel(patch, tone, 0x3A),
                new JVSender(tone, 0x3A), RANDOM_PITCH_DEPTHS), gbc);
        gbc.gridy++; panel.add(new ScrollBarLookupWidget(null, patch,
                0, KEY_FOLLOW_200.length - 1, new JVModel(patch, tone, 0x3B),
                new JVSender(tone, 0x3B), KEY_FOLLOW_200), gbc);
        gbc.gridy++; panel.add(new ScrollBarWidget(null, patch, 1, 127, -64,
                new JVModel(patch, tone, 0x3C), new JVSender(tone, 0x3C)),
                gbc);
        gbc.gridy++; panel.add(new ScrollBarLookupWidget(null, patch,
                0, KEY_FOLLOW_100.length - 1, new JVModel(patch, tone, 0x3D),
                new JVSender(tone, 0x3D), KEY_FOLLOW_100), gbc);
        gbc.gridy++; panel.add(new ScrollBarLookupWidget(null, patch,
                0, KEY_FOLLOW_100.length - 1, new JVModel(patch, tone, 0x3E),
                new JVSender(tone, 0x3E), KEY_FOLLOW_100), gbc);
        gbc.gridy++; panel.add(new ScrollBarLookupWidget(null, patch,
                0, KEY_FOLLOW_100.length - 1, new JVModel(patch, tone, 0x3F),
                new JVSender(tone, 0x3F), KEY_FOLLOW_100), gbc);
        gbc.gridy++; panel.add(new ScrollBarWidget(null, patch, 52, 76, -64,
                new JVModel(patch, tone, 0x40), new JVSender(tone, 0x40)),
                gbc);

        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2; gbc.gridheight=8;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(new EnvelopeWidget(null, patch,
                new EnvelopeWidget.Node[] {
    				new EnvelopeWidget.Node(0, 0,   null,                           64, 64,   null,                           0, false, null,                            null,                            null,      null),
                	new EnvelopeWidget.Node(0, 127, new JVModel(patch, tone, 0x41), 1, 127, new JVModel(patch, tone, 0x42), 0, false, new JVSender(tone, 0x41), new JVSender(tone, 0x42), "T1 Time", "T1 Level"),
                	new EnvelopeWidget.Node(0, 127, new JVModel(patch, tone, 0x43), 1, 127, new JVModel(patch, tone, 0x44), 0, false, new JVSender(tone, 0x43), new JVSender(tone, 0x44), "T2 Time", "T2 Level"),
                	new EnvelopeWidget.Node(0, 127, new JVModel(patch, tone, 0x45), 1, 127, new JVModel(patch, tone, 0x46), 0, false, new JVSender(tone, 0x45), new JVSender(tone, 0x46), "T3 Time", "T3 Level"),
                	new EnvelopeWidget.Node(0, 127, new JVModel(patch, tone, 0x47), 1, 127, new JVModel(patch, tone, 0x48), 0, false, new JVSender(tone, 0x47), new JVSender(tone, 0x48), "T4 Time", "T4 Level"),
        }), gbc);

        return panel;
    }

    JPanel buildLFOs(Patch patch, int tone) {
        GridBagConstraints gbc = new GridBagConstraints();
        JPanel panel = new JPanel(new GridBagLayout());

//        panel.setBorder(new TitledBorder(new EtchedBorder(
//                EtchedBorder.RAISED), "LFO",
//                TitledBorder.CENTER,TitledBorder.CENTER));

        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        JTabbedPane jtp = new JTabbedPane();
        panel.add(jtp, gbc);

        final String[] FORMS = new String[] {
                "TRI", "SIN", "SAW", "SQR", "RND1", "RND2" };
        final String[] OFFSET = new String[] {
                "-100", "-50", "0", "+50", "+100" };

        final String DELAYS[] = DriverUtil.generateNumbers(0, 128, "0");
        DELAYS[128] = "KEY-OFF";

        /*final String[] PITCHDEPTH = new String[124];
        for (int i = 0; i < PITCHDEPTH.length; i++) {
            PITCHDEPTH[i] = "" + (i > 64 ? "+": "") + (i * 10 - 640);
        }*/

        gbc.insets.left = gbc.insets.right = 4;
        gbc.insets.bottom = gbc.insets.bottom = 2;
        gbc.anchor = GridBagConstraints.WEST;

        for (int i = 0; i < 2; i++) {
	        JPanel lfo = new JPanel(new GridBagLayout());
	        gbc.gridx = 0; gbc.gridy = 0;
	        gbc.gridy++; lfo.add(new JLabel("Form"), gbc);
	        gbc.gridy++; lfo.add(new JLabel("Offset"), gbc);
	        gbc.gridy++; lfo.add(new JLabel("Synchro"), gbc);
	        gbc.gridy++; lfo.add(new JLabel("Rate"), gbc);
	        gbc.gridy++; lfo.add(new JLabel("Delay"), gbc);
	        gbc.gridy++; lfo.add(new JLabel("Fade Polarity"), gbc);
	        gbc.gridy++; lfo.add(new JLabel("Fade Time"), gbc);
	        gbc.gridy++; lfo.add(new JLabel("Pitch Depth"), gbc);
	        gbc.gridy++; lfo.add(new JLabel("TVF Depth"), gbc);
	        gbc.gridy++; lfo.add(new JLabel("TVA Depth"), gbc);

            int lfo_offset = i * 11;
            gbc.gridx = 1; gbc.gridy = 0;
            gbc.gridy++; lfo.add(new ComboBoxWidget(null, patch, 0,
                    new JVModel(patch, tone, 0x22 + lfo_offset), new JVSender(tone, 0x22 + lfo_offset),
                    FORMS), gbc); // TODO: make this a graphic of some kind
            gbc.gridy++; lfo.add(new ScrollBarLookupWidget(null, patch,
                    0, OFFSET.length - 1, new JVModel(patch, tone, 0x23 + lfo_offset),
                    new JVSender(tone, 0x23 + lfo_offset), OFFSET), gbc);
            gbc.gridy++; lfo.add(new CheckBoxWidget(null, patch,
    				new JVModel(patch, tone, 0x24 + lfo_offset),
    				new JVSender(tone, 0x24 + lfo_offset)), gbc);
            gbc.gridy++; lfo.add(new ScrollBarWidget(null, patch, 0, 127, 0,
                    new JVModel(patch, tone, 0x25 + lfo_offset), new JVSender(tone, 0x25 + lfo_offset)),
                    gbc);
            gbc.gridy++; lfo.add(new ScrollBarLookupWidget(null, patch,
                    0, DELAYS.length - 1, new JV2Model(patch, tone, 0x26 + lfo_offset),
                    new JV2Sender(tone, 0x26 + lfo_offset), DELAYS), gbc);
            gbc.gridy++; lfo.add(new ComboBoxWidget(null, patch, 0,
                    new JVModel(patch, tone, 0x28 + lfo_offset), new JVSender(tone, 0x28 + lfo_offset),
                    new String[] { "IN", "OUT"}), gbc);
            gbc.gridy++; lfo.add(new ScrollBarWidget(null, patch, 0, 127, 0,
                    new JVModel(patch, tone, 0x29 + lfo_offset), new JVSender(tone, 0x29 + lfo_offset)),
                    gbc);
            // DIFFERS FROM MANUAL
            gbc.gridy++; lfo.add(new ScrollBarWidget(null, patch, 0, 127, -64,
                    new JVModel(patch, tone, 0x2A + lfo_offset), new JVSender(tone, 0x2A + lfo_offset)),
                    gbc);
            gbc.gridy++; lfo.add(new ScrollBarWidget(null, patch, 1, 127, -64,
                    new JVModel(patch, tone, 0x2B + lfo_offset), new JVSender(tone, 0x2B + lfo_offset)),
                    gbc);
            gbc.gridy++; lfo.add(new ScrollBarWidget(null, patch, 1, 127, -64,
                    new JVModel(patch, tone, 0x2C + lfo_offset), new JVSender(tone, 0x2C + lfo_offset)),
                    gbc);

            jtp.add("LFO " + (i + 1), lfo);
        }

        return panel;
    }

    JPanel buildControlChange(Patch patch, int tone) {
        GridBagConstraints gbc = new GridBagConstraints();
        JPanel panel = new JPanel(new GridBagLayout());

//        panel.setBorder(new TitledBorder(new EtchedBorder(
//                EtchedBorder.RAISED), "Control Change",
//                TitledBorder.CENTER,TitledBorder.CENTER));

        final String[] CONTROLS = new String[] {
                "OFF", "PITCH", "CUTOFF", "RESONANCE", "LEVEL",
                "PITCH LFO1", "PITCH LFO2", "TVF LFO1", "TVF LFO2",
                "TVA LFO1", "TVA LFO2", "LFO1 RATE", "LFO2 RATE"
        };

        gbc.weightx = 1; gbc.insets.left = gbc.insets.right = 4;
        gbc.insets.bottom = gbc.insets.bottom = 2;
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
        gbc.gridy++; panel.add(new JLabel("Volume Switch"), gbc);
        gbc.gridy++; panel.add(new JLabel("Hold-1 Switch"), gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy++; panel.add(new CheckBoxWidget(null, patch,
				new JVModel(patch, tone, 0x08),
				new JVSender(tone, 0x08)), gbc);
        gbc.gridy++; panel.add(new CheckBoxWidget(null, patch,
				new JVModel(patch, tone, 0x09),
				new JVSender(tone, 0x09)), gbc);


        JTabbedPane jtp = new JTabbedPane();
        String[] menus = { "Modulation", "Aftertouch", "Expression" };
        byte[] bases = { 0x0A, 0x12, 0x1A };
        for (int i = 0; i < menus.length; i++) {
            JPanel module = new JPanel(new GridBagLayout());

            gbc.gridy = 0;
            for (int j = 0; j < 4; j++) {
                gbc.gridx = 0; gbc.gridy++;
                module.add(new JLabel(menus[i] + " Destination " + (j + 1)),
                        gbc);

                gbc.gridx = 1;
                module.add(new ComboBoxWidget(null, patch, 0, new JVModel(
                        patch, tone, bases[i] + 2 * j),
                        new JVSender(tone, bases[i] + 2 * j), CONTROLS), gbc);

                gbc.gridx = 0; gbc.gridy++;
                module.add(new JLabel(menus[i] + " Sense " + (j + 1)), gbc);

                gbc.gridx = 1;
                module.add(new ScrollBarWidget(null, patch, 1, 127, -64,
                        new JVModel(patch, tone, bases[i] + 2 * j),
                        new JVSender(tone, bases[i] + 2 * j)), gbc);
            }

            jtp.add(menus[i], module);
        }
        gbc.anchor = GridBagConstraints.CENTER; gbc.gridwidth = 2;
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(jtp, gbc);

        return panel;
    }
}
