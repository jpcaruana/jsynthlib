/*
 * Copyright 2003, 2004, 2005 Hiroo Hayashi
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

package synthdrivers.RolandTD6;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.jsynthlib.core.CheckBoxWidget;
import org.jsynthlib.core.ComboBoxWidget;
import org.jsynthlib.core.ErrorMsg;
import org.jsynthlib.core.KnobLookupWidget;
import org.jsynthlib.core.ParamModel;
import org.jsynthlib.core.Patch;
import org.jsynthlib.core.PatchEditorFrame;
import org.jsynthlib.core.PatchNameWidget;
import org.jsynthlib.core.ScrollBarLookupWidget;
import org.jsynthlib.core.ScrollBarWidget;
import org.jsynthlib.core.SysexWidget;
import org.jsynthlib.core.TreeWidget;
import org.jsynthlib.core.VertScrollBarWidget;


/**
 * Single Patch Editor for Roland TD-6 Percussion Module.
 *
 * @author <a href="mailto:hiroo.hayashi@computer.org">Hiroo Hayashi</a>
 * @version $Id$
 */
final class TD6SingleEditor extends PatchEditorFrame {
    /** selected pad */
    private int pad;
    /** false : head is selected, true : rim is relected */
    private boolean isRim;
    /** array of PadInfo for pad information */
    private final PadInfo [] padList;
    /** TreeWidget for instruction tree */
    private TreeWidget treeWidget;
    /** slider number */
    private int snum = 0;

    /**
     * Creates a new <code>TD6SingleEditor</code> instance.
     *
     * @param patch a <code>Patch</code> value
     */
    TD6SingleEditor(Patch patch) {
	super ("Roland TD-6 Single Editor", patch);

	// defined here since padList is used by TD6PadModel()
	padList = ((RolandTD6Device) patch.getDevice()).activePadInfo();
	// create treeWidget here since treeWidget.getNode() is called for
	// pad list initialization.
	treeWidget = new TreeWidget("Instrument", patch, new Instrument(),
				    new TD6InstModel(patch),
				    new TD6PadSender(0x0, true));
	// default
	gbc.gridwidth = 1; gbc.gridheight = 1; gbc.weightx = 1; gbc.weighty = 1;

	// Kit Title
	JPanel topPane = new JPanel();
	topPane.setLayout(new GridBagLayout()); gbc.weightx = 1;
	gbc.gridx = 0; gbc.gridy = 0;
	scrollPane.add(topPane, gbc);
	addWidget(topPane,
		  new PatchNameWidget("Drum Kit Name ", patch),
		  0, 0, 1, 1, 0);

	// Tabbed Pane
	JTabbedPane tabbedPane = new JTabbedPane();
	gbc.gridx = 0; gbc.gridy = 1;
	scrollPane.add(tabbedPane, gbc);

	// Pad Pane
	JPanel padPane = createPadPane(patch);
	tabbedPane.addTab("Pad", padPane);

	// Mixer Pane
	JPanel mixerPane = createMixerPane(patch);
	tabbedPane.addTab("Mixer", mixerPane);

	// Effect Pane
	JPanel effectPane = createEffectPane(patch);
	tabbedPane.addTab("Effect", effectPane);

	pack();
    }

    //   pad pattern off,1-250
    private final static String[] padPattern = new String[251];
    static {
        padPattern[0] = "Off";
        for (int i = 1; i < padPattern.length; i++)
            padPattern[i] = String.valueOf(i);
    }
    private JPanel createPadPane(Patch patch) {
        JPanel padPane = new JPanel();
        padPane.setLayout(new GridBagLayout());

        /*
         * Pad List
         */
        JPanel padListPane = new JPanel();
        padListPane.setLayout(new GridBagLayout());
        padListPane.setBorder
            (new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
        		      "Choose a Pad",
        		      TitledBorder.CENTER,
        		      TitledBorder.CENTER));
        gbc.gridx = 0; gbc.gridy = 0;
        padPane.add(padListPane, gbc);

        JLabel l;
        l = new JLabel("Head", JLabel.CENTER);
        gbc.gridx = 1; gbc.gridy = 0;
        padListPane.add(l, gbc);
        l = new JLabel("Rim", JLabel.CENTER);
        gbc.gridx = 2; gbc.gridy = 0;
        padListPane.add(l, gbc);

        // TD6PadModel uses pad and isRim internally
        TD6PadModel m = new TD6PadModel(patch, 0x0, true);
        for (pad = 0; pad < padList.length; pad++) {
            l = new JLabel(padList[pad].name, JLabel.CENTER);
            gbc.gridx = 0; gbc.gridy = 1 + pad;
            padListPane.add(l, gbc);

            // head
            isRim = false;
            JButton b = createPadButton(treeWidget.getNode(m.get()).toString(), true);
            padList[pad].buttonHead = b;
            gbc.gridx = 1;
            padListPane.add(b, gbc);
            // rim
            if (padList[pad].dualTrigger) {
                isRim = true;
                b = createPadButton(treeWidget.getNode(m.get()).toString(), padList[pad].dualTriggerActive);
                padList[pad].buttonRim = b;
                gbc.gridx = 2;
                padListPane.add(b, gbc);
            }
        }

        // default selection
        pad = 0;
        isRim = false;
        padList[pad].buttonHead.setBorderPainted(true);
        padList[pad].buttonHead.setSelected(true);

        /*
         * Instrument Tree
         */
        JPanel treePane = new JPanel();
        treePane.setLayout(new GridBagLayout());
        addWidget(treePane, treeWidget,
        	  0, 0, 1, 1, snum++);
        treePane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
        				    "Instrument Selection",
        				    TitledBorder.CENTER,
        				    TitledBorder.CENTER));
        gbc.gridx = 1; gbc.gridy = 0; gbc.gridheight = 2;
        padPane.add(treePane, gbc);
        gbc.gridheight = 1;

        /*
         * Pad Parameters
         */
        JPanel padParamPane = new JPanel();
        padParamPane.setLayout(new GridBagLayout());
        padParamPane.setBorder
            (new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
        		      "Pad Setting",
        		      TitledBorder.CENTER,
        		      TitledBorder.CENTER));
        gbc.gridx = 0; gbc.gridy = 1;
        padPane.add(padParamPane, gbc);

        int lw = (int) (new JLabel("Pad Pattern")).getPreferredSize().getWidth();

        //   pitch
        addWidget(padParamPane,
        	  new ScrollBarWidget("Pitch", patch,
        			      0, 960, -480, lw,
        			      new TD6PadModel(patch, 0x4, true),
        			      new TD6PadSender(0x4, true)),
        	  0, 0, 1, 1, snum++);
        //   decay
        addWidget(padParamPane,
        	  new ScrollBarWidget("Decay", patch,
        			      0, 62, -31, lw,
        			      new TD6PadModel(patch, 0x8),
        			      new TD6PadSender(0x8)),
        	  0, 1, 1, 1, snum++);
        addWidget(padParamPane,
        	  new ScrollBarLookupWidget("Pad Pattern", patch,
        				    0, 250, lw,
        				    new TD6PadModel(patch, 0x9, true),
        				    new TD6PadSender(0x9, true),
        				    padPattern),
        	  0, 2, 1, 1, snum++);
        // Pad Pattern Velocity
        addWidget(padParamPane,
        	  new CheckBoxWidget("Pad Pattern Velocity", patch,
        			     new TD6PadModel(patch, 0xf),
        			     new TD6PadSender(0xf)),
        	  0, 3, 1, 1, 0);
        // Pitch Control Switch on/off
        addWidget(padParamPane,
        	  new CheckBoxWidget("Pedal Pitch Control", patch,
        			     new TD6PadModel(patch, 0x12),
        			     new TD6PadSender(0x12)),
        	  1, 0, 1, 1, 0);

        lw = (int) (new JLabel("Pedal Pitch Control")).getPreferredSize().getWidth();
        //   MIDI gate time (0.1 to 8.0)
        addWidget(padParamPane,
        	  new ScrollBarWidget("Gate Time (x 0.1s)", patch,
        			      1, 80, 1, lw,
        			      new TD6PadModel(patch, 0xd),
        			      new TD6PadSender(0xd)),
        	  1, 1, 1, 1, snum++);
        //   MIDI note number
        addWidget(padParamPane,
        	  new ScrollBarWidget("Note Number", patch,
        			      0, 127, 0, lw,
        			      new TD6PadModel(patch, 0xe),
        			      new TD6PadSender(0xe)),
        	  1, 2, 1, 1, snum++);
        return padPane;
    }

    // pad and isRim must be set before calling this method.
    private JButton createPadButton(String label, boolean enabled) {
        JButton b = new JButton(label);
        if (!enabled) {
            b.setEnabled(false);	// true by default
        } else {
            final int index = pad;
            final boolean rimp = isRim;
            b.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // deselect privious selected button
                    if (isRim) {
                        padList[pad].buttonRim.setBorderPainted(false);
                        padList[pad].buttonRim.setSelected(false);
                    } else {
                        padList[pad].buttonHead.setBorderPainted(false);
                        padList[pad].buttonHead.setSelected(false);
                    }
                    JButton b = (JButton) e.getSource();
                    b.setBorderPainted(true); // for Windows UI
                    b.setSelected(true); // for Metal UI
                    // pad and isRim are used by TD6PadSender and TD6PadModel.
                    pad = index;
                    isRim = rimp;

                    // update the state of each widget in Pad Pane when
                    // pad selection is changed.
                    for (int i = 0; i < widgetList.size(); i++) {
                        SysexWidget w = (SysexWidget) widgetList.get(i);
                        //ErrorMsg.reportStatus(((Object) w).toString());
                        w.setValue();
                    }
                }
            });
        }
        b.setBorderPainted(false);
        b.setSelected(false);
        return b;
    }

    private static final String[] panText = { 
            "L15", "L14", "L13", "L12", "L11", "L10", "L9",
            "L8", "L7", "L6", "L5", "L4", "L3", "L2", "L1",
            "Center",
            "R1", "R2", "R3", "R4", "R5", "R6", "R7", "R8",
            "R9", "R10", "R11", "R12", "R13", "R14", "R15",
            "Random", "Alternative"
    };

    private JPanel createMixerPane(Patch patch) {
        JPanel mixerPane = new JPanel();
        mixerPane.setLayout(new GridBagLayout());

        // Master Volume
        JPanel padMx = new JPanel();
        padMx.setLayout(new GridBagLayout());
        padMx.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
        				 "Master",
        				 TitledBorder.CENTER,
        				 TitledBorder.CENTER));
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        mixerPane.add(padMx, gbc);
        addWidget(padMx,
        	  new VertScrollBarWidget(" ", patch,
        				  0, 127, 0,
        				  new TD6KitModel(patch, 0x15),
        				  new TD6KitSender(0x15)),
        	  0, 0, 1, 1, snum++);

        for (int i = 0; i < padList.length; i++) {
            padMx = new JPanel();
            padMx.setLayout(new GridBagLayout());
            padMx.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
        				     padList[i].name,
        				     TitledBorder.CENTER,
        				     TitledBorder.CENTER));
            int w = 0;
            // head
            addWidget(padMx,
                    new VertScrollBarWidget("Head", patch, 0, 127, 0,
                            new TD6KitModel(patch, padList[i].offset + 0x00 + 0x10),
                            new TD6KitSender(padList[i].offset + 0x00 + 0x10)),
                            w++, 1, 1, 1, snum++);
            // rim
            if (padList[i].dualTrigger && padList[i].dualTriggerActive)
                addWidget(padMx, 
                        new VertScrollBarWidget("Head", patch, 0, 127, 0,
                                new TD6KitModel(patch, padList[i].offset + 0x13 + 0x10),
                                new TD6KitSender(padList[i].offset + 0x13 + 0x10)),
                                w++, 1, 1, 1, snum++);
           
            if (padList[i].name == "Hi-Hat") {
        	// Pedal Hi-Hat Volume
        	addWidget(padMx,
        		  new VertScrollBarWidget("Pedal", patch,
        					  0, 15, 0,
        					  new TD6KitModel(patch, 0x13),
        					  new TD6KitSender(0x13)),
        		  w++, 1, 1, 1, snum++);
            }
            // How can I move Center to top?  Change DKnob class or add
            // radio button? !!!FIXIT!!!
            addWidget(padMx,
        	      new KnobLookupWidget(null, patch,
        				   0, 32,
        				   new TD6KitModel(patch,
        						   padList[i].offset + 0x26),
        				   new TD6KitSender(padList[i].offset + 0x26),
        				   panText),
        	      0, 0, w, 1,
        	      GridBagConstraints.CENTER, GridBagConstraints.NONE,
        	      snum++);

            gbc.gridx = i + 1; gbc.gridy = 0;
            gbc.gridwidth = 1; gbc.gridheight = 1;
            gbc.anchor = GridBagConstraints.CENTER;
            gbc.fill = GridBagConstraints.NONE;
            mixerPane.add(padMx, gbc);
        }
        return mixerPane;
    }

    private static final String[] studio_type = {
            "Living Room", "Bathroom",
            "Recording Studio", "Garage",
            "Locker Room", "Theater",
            "Cave", "Gymnasium",
            "Domed Stadium"
    };
    private static final String[] wall_type = {
            "Wood", "Plaster", "Glass"
    };
    private static final String[] room_size = {
            "Small", "Medium", "Large"
    };

    private JPanel createEffectPane(Patch patch) {
        JPanel effectPane = new JPanel();
        effectPane.setLayout(new GridBagLayout());

        // Top Pane
        JPanel eTopPane = new JPanel();
        eTopPane.setLayout(new GridBagLayout());
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        effectPane.add(eTopPane, gbc);
        gbc.gridwidth = 1;

        // Equilizer Pane
        JPanel eqPane = new JPanel();
        eqPane.setLayout(new GridBagLayout());
        eqPane.setBorder
            (new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
        		      "Equalizer",
        		      TitledBorder.CENTER,
        		      TitledBorder.CENTER));
        gbc.gridx = 0; gbc.gridy = 1;
        effectPane.add(eqPane, gbc);

        // Ambience Pane
        JPanel ambPane = new JPanel();
        ambPane.setLayout(new GridBagLayout());
        ambPane.setBorder
            (new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
        		      "Ambience",
        		      TitledBorder.CENTER,
        		      TitledBorder.CENTER));
        gbc.gridx = 1; gbc.gridy = 1;
        effectPane.add(ambPane, gbc);

        // studio (1 to 9)
        addWidget(eTopPane,
        	  new ComboBoxWidget("Studio Type ", patch, 1,
        			     new TD6KitModel(patch, 0x8),
        			     new TD6KitSender(0x8),
        			     studio_type),
        	  0, 0, 1, 1, 0);
        // Wall Type
        addWidget(eTopPane,
        	  new ComboBoxWidget("Wall Type ", patch,
        			     new TD6KitModel(patch, 0xa),
        			     new TD6KitSender(0xa),
        			     wall_type),
        	  1, 0, 1, 1, 0);
        // Room Size (1 to 3)
        addWidget(eTopPane,
        	  new ComboBoxWidget("Room Size ", patch, 1,
        			     new TD6KitModel(patch, 0xb),
        			     new TD6KitSender(0xb),
        			     room_size),
        	  2, 0, 1, 1, 0);
        // Pedal Pitch Control Range
        addWidget(eTopPane,
        	  new ScrollBarWidget("Pedal Pitch Control Range", patch,
        			      0, 48, -24,
        			      new TD6KitModel(patch, 0x14),
        			      new TD6KitSender(0x14)),
        	  3, 0, 1, 1, snum++);

        // Master Equalizer Switch
        addWidget(eqPane,
        	  new CheckBoxWidget("Enable", patch,
        			     new TD6KitModel(patch, 0x11),
        			     new TD6KitSender(0x11)),
        	  0, 0, 1, 1, 0);

        // Equalizer Low Gain
        addWidget(eqPane,
        	  new VertScrollBarWidget("Low", patch,
        				  0, 24, -12,
        				  new TD6KitModel(patch, 0xd),
        				  new TD6KitSender(0xd)),
        	  0, 1, 1, 1, snum++);

        // Equalizer High Gain
        addWidget(eqPane,
        	  new VertScrollBarWidget("High", patch,
        				  0, 24, -12,
        				  new TD6KitModel(patch, 0xf),
        				  new TD6KitSender(0xf)),
        	  1, 1, 1, 1, snum++);

        // Master Ambience Switch
        addWidget(ambPane,
        	  new CheckBoxWidget("Enable", patch,
        			     new TD6KitModel(patch, 0x10),
        			     new TD6KitSender(0x10)),
        	  0, 0, 1, 1, 0);

        // Master Ambience Level
        JPanel padEq = new JPanel();
        padEq.setLayout(new GridBagLayout());
        padEq.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
        				 "Master",
        				 TitledBorder.CENTER,
        				 TitledBorder.CENTER));
        gbc.gridx = 0; gbc.gridy = 1;
        ambPane.add(padEq, gbc);
        addWidget(padEq,
        	  new VertScrollBarWidget(" ", patch,
        				  0, 127, 0,
        				  new TD6KitModel(patch, 0x9),
        				  new TD6KitSender(0x9)),
        	  0, 0, 1, 1, snum++);

        // Pad Ambience Level
        for (int i = 0; i < padList.length; i++) {
            padEq = new JPanel();
            padEq.setLayout(new GridBagLayout());
            // head
            addWidget(padEq,
                    new VertScrollBarWidget("Head", patch, 0, 127, 0,
                            new TD6KitModel(patch, (padList[i].offset + 0x00 + 0x11)),
                            new TD6KitSender(padList[i].offset + 0x00 + 0x11)),
                            0, 1, 1, 1, snum++);
            // rim
            if (padList[i].dualTrigger && padList[i].dualTriggerActive)
                addWidget(padEq,
                        new VertScrollBarWidget("Rim", patch, 0, 127, 0,
                                new TD6KitModel(patch, (padList[i].offset + 0x13 + 0x11)),
                                new TD6KitSender(padList[i].offset + 0x13 + 0x11)),
                                1, 1, 1, 1, snum++);
            padEq.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
        				     padList[i].name,
        				     TitledBorder.CENTER,
        				     TitledBorder.CENTER));
            gbc.gridx = 1 + i; gbc.gridy = 1;
            ambPane.add(padEq, gbc);
        }
        return effectPane;
    }

    /**
     * SysexSender for a selected pad.  Used by widgets in padParamPane.
     */
    final class TD6PadSender extends TD6KitSender {
	/** offset address in pad */
	private int param;
	/**
	 * Creates a new <code>TD6PadSender</code> instance.
	 *
	 * @param param relative offset address
	 * @param nibbled true if nibbled (4 byte) data
	 */
	TD6PadSender(int param, boolean nibbled) {
	    super(0, nibbled);	// 1st parameter is not used.
	    this.param = param;
	}

	/**
	 * Creates a new <code>TD6PadSender</code> instance for 1 byte data.
	 *
	 * @param param relative offset address
	 */
	TD6PadSender(int param) {
	    this(param, false);
	}

	/**
	 * Generates a byte array for system exclusive message.
	 *
	 * @param value an <code>int</code> value
	 */
	protected void setValue(int value) {
	    //ErrorMsg.reportStatus("TD6PadSender : value : " + value);
	    super.setValue(0x01000000 + ((99 - 1) << 16)
	            + padList[pad].offset
	            + (isRim ? 0x13 : 0) + param,
	            value);
	    //ErrorMsg.reportStatus("TD6PadSender.setValue: " + this);
	}
    }

    /**
     * ParamModel of a instruction for selected pad.  Used by instrument
     * tree widgets.
     */
    final class TD6InstModel extends TD6PadModel {
	/**
	 * Creates a new <code>TD6InstModel</code>
	 *
	 * @param patch a <code>Patch</code> value
	 */
	TD6InstModel(Patch patch) {
	    super(patch, 0x0, true);
	}

	/**
	 * Set data in sysex byte array.
	 *
	 * @param d an <code>int</code> value
	 */
	public void set(int d) {
	    super.set(d);
	    if (isRim)
		padList[pad].buttonRim.setText(treeWidget.getNode(d).toString());
	    else
		padList[pad].buttonHead.setText(treeWidget.getNode(d).toString());

	}
    }

    /**
     * ParamModel for a selected pad.  Used by widgets in padParamPane.
     */
    class TD6PadModel extends TD6KitModel {
	/** offset address in pad */
	private int param;

	/**
	 * Creates a new <code>TD6PadModel</code> instance.
	 *
	 * @param patch a <code>Patch</code> value
	 * @param param relative offset address
	 * @param nibbled true if nibbled (4 byte) data
	 */
	TD6PadModel(Patch patch, int param, boolean nibbled) {
	    // 'ofs' is calculated when set() and/or get() is called.
	    super(patch, 0, nibbled);
	    this.param = param;
	}

	/**
	 * Creates a new <code>TD6PadModel</code> instance for 1 byte data.
	 *
	 * @param patch a <code>Patch</code> value
	 * @param param relative offset address
	 */
	TD6PadModel(Patch patch, int param) {
	    this(patch, param, false);
	}

	/**
	 * Set data in sysex byte array.
	 *
	 * @param d an <code>int</code> value
	 */
	public void set(int d) {
	    ofs = getOffset(padList[pad].offset + (isRim ? 0x13 : 0) + param);
	    super.set(d);
	}

	/**
	 * Get data from sysex byte array.
	 *
	 * @return an <code>int</code> value
	 */
	public int get() {
	    //      ErrorMsg.reportStatus("TD6PadModel.get(): param =  " + param
	    //			    + "(0x" + Integer.toHexString(param) +")");
	    ofs = getOffset(padList[pad].offset + (isRim ? 0x13 : 0) + param);
	    //      ErrorMsg.reportStatus("TD6PadModel.get(): ofs =  " + ofs
	    //			    + "(0x" + Integer.toHexString(ofs) +")");
	    return super.get();
	}
    }
}

/**
 * SysexSender for widgets which are not in padParamPane.
 *
 * @author <a href="mailto:hiroo.hayashi@computer.org">Hiroo Hayashi</a>
 */
class TD6KitSender extends RolandDT1Sender { // extended by TD6PadSender
    /** true for nibbled data */
    private boolean nibbled;
    /**
     * Creates a new <code>TD6KitSender</code> instance.
     *
     * @param drumkit an <code>int</code> value which specifies drum kit [1 - 99]
     * @param param offset address in kit parameters.
     * @param nibbled true if nibbled (4 byte) data
     */
    TD6KitSender(int drumkit, int param, boolean nibbled) {
	super(0x41, 0x3f00, 4, 0x01000000 + ((drumkit - 1) << 16) + param,
	      nibbled ? 4 : 1);
	this.nibbled = nibbled;
    }

    /**
     * Creates a new <code>TD6KitSender</code> instance for editing
     * buffer (drum kit 99)
     *
     * @param param offset address in kit parameters.
     * @param nibbled true if nibbled (4 byte) data
     */
    TD6KitSender(int param, boolean nibbled) {
	this(99, param, nibbled);
    }
    /**
     * Creates a new <code>TD6KitSender</code> instance for editing
     * buffer (drum kit 99) and for 1 byte data.
     *
     * @param param offset address in kit parameters.
     */
    TD6KitSender(int param) {
	this(99, param, false);
    }

    /**
     * convert int value to nibbled value.
     *
     * @param value an <code>int</code> value
     * @return an <code>int</code> value
     */
    private int conv(int value) {
	return (nibbled ? ((value & 0xf000) << 12 | (value & 0x0f00) << 8
			   | (value & 0x00f0) << 4 | value & 0x000f)
		: value);
    }

    /**
     * Generates a byte array for system exclusive message.
     *
     * @param value an <code>int</code> value
     */
    protected void setValue(int value) {
	super.setValue(conv(value));
    }

    /**
     * Generates a byte array for system exclusive message.
     *
     * @param param offset address in kit parameters.
     * @param value an <code>int</code> value
     */
    protected void setValue(int param, int value) {
        super.setValue(param, conv(value));
    }
}

/**
 * ParamModel for widgets which are not in padParamPane.
 *
 * @author <a href="mailto:hiroo.hayashi@computer.org">Hiroo Hayashi</a>
 */
class TD6KitModel extends ParamModel { // extended by TD6PadModel
    /** true for nibbled data */
    private boolean nibbled;

    /**
     * Creates a new <code>TD6KitModel</code> instance.
     *
     * @param patch a <code>Patch</code> value
     * @param param offset address in kit parameters.
     * @param nibbled true if nibbled (4 byte) data
     */
    TD6KitModel(Patch patch, int param, boolean nibbled) {
	super(patch, getOffset(param));
	this.nibbled = nibbled;		// need to check if 1 or 4?
    }

    /**
     * Creates a new <code>TD6KitModel</code> instance for 1 byte data.
     *
     * @param p a <code>Patch</code> value
     * @param param offset address in kit parameters.
     */
    TD6KitModel(Patch p, int param) {
	this(p, param, false);
    }

    /**
     * Convert the relative offset address param to offset address in
     * sysex byte array.
     *
     * @param param offset address in kit parameters.
     * @return an <code>int</code> value
     */
    static int getOffset(int param) {
	//    ErrorMsg.reportStatus("TD6KitModel.getoffset(): param =  " + param
	//			  + "(0x" + Integer.toHexString(param) +")");
	int base = (param >> 8) & 0xf;
	return (((base == 0) ? 0 : ((base == 1) ? 37 : 37 + 55 * (base - 1)))
		+ 10 + (param & 0xff));
    }

    /**
     * Set data in sysex byte array.
     *
     * @param d an <code>int</code> value
     */
    public void set(int d) {
	ErrorMsg.reportStatus("TD6KitModel.set(): d =  " + d + " :" + nibbled);
	ErrorMsg.reportStatus("TD6KitModel.set(): ofs =  " + ofs
			      + "(0x" + Integer.toHexString(ofs) + ")");
	if (nibbled) {
	    // 4bit each, MSB first
	    patch.sysex[ofs]     = (byte) ((d >> 12) & 0xf);
	    patch.sysex[ofs + 1] = (byte) ((d >>  8) & 0xf);
	    patch.sysex[ofs + 2] = (byte) ((d >>  4) & 0xf);
	    patch.sysex[ofs + 3] = (byte)  (d	     & 0xf);
	} else {
	    patch.sysex[ofs] = (byte) d;
	}
	//ErrorMsg.reportStatus(patch.sysex);
    }

    /**
     * Get data from sysex byte array.
     *
     * @return an <code>int</code> value
     */
    public int get() {
	if (nibbled) {
	    // MSB first nibbled word data
	    int d = (patch.sysex[ofs + 0] << 12 | patch.sysex[ofs + 1] << 8
		     | patch.sysex[ofs + 2] << 4 | patch.sysex[ofs + 3]);
	    /*
	      ErrorMsg.reportStatus("TD6KitModel.get(): " + d);
	      ErrorMsg.reportStatus("TD6KitModel.get(): ofs =  " + ofs
	      + "(0x" + Integer.toHexString(ofs) + ")");
	    */
	    return d;
	} else {
	    return patch.sysex[ofs];
	}
    }
}
