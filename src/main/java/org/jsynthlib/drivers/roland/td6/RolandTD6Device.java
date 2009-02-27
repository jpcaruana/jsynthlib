/*
 * Copyright 2003 Hiroo Hayashi
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

package org.jsynthlib.drivers.roland.td6;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.Preferences;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jsynthlib.core.Device;

/**
 * RolandTD6Device.java
 *
 * Device class for Roland TD6 Percussion Module.
 *
 * @author <a href="mailto:hiroo.hayashi@computer.org">Hiroo Hayashi</a>
 * @version $Id$
 */
public final class RolandTD6Device extends Device {
    /** Array of all pads including inactive ones. */
    private PadInfo[] padinfo = {
	//			offset,	DT pad, DT active,active
	new PadInfo("Kick",	0x100,	false,	false,	true),
	new PadInfo("Snare",	0x300,	true,	false,	true),
	new PadInfo("Hi-Hat",	0x700,	true,	false,	true),
	new PadInfo("Ride",	0xa00,	true,	true,	true),
	new PadInfo("Crash 1",	0x800,	true,	true,	true),
	new PadInfo("Crash 2",	0x900,	true,	true,	false),
	new PadInfo("Tom 1",	0x400,	true,	false,	true),
	new PadInfo("Tom 2",	0x500,	false,	false,	true),
	new PadInfo("Tom 3",	0x600,	false,	false,	true),
	new PadInfo("Tom 4",	0xc00,	false,	false,	false),
	new PadInfo("AUX",	0xb00,	false,	false,	false),
    };

    // Preferences node for persistent data
    private Preferences prefsPad;
    /**
     * Creates a new <code>RolandTD6Device</code> instance.
     *
     */
    public RolandTD6Device() {
	super("Roland", "TD6",
	      "F07E..0602413F01000000020000f7",
	      "Driver for TD-6 Roland Percussion Sound Module.\n"
	      + "This driver is still under development.\n"
	      + "This driver uses Drum Kit 99 as edit buffer.\n"
	      + "Set \"Sync Mode\" to \"EXT\" to stop sending Timing Clock system real time message (0xF8).",
	      "Hiroo Hayashi <hiroo.hayashi@computer.org>");
    }

    public RolandTD6Device(Preferences prefs) {
	this();
	this.prefs = prefs;
	// create/get a Preferences node
	prefsPad = prefs.node("pad");

	//setSynthName("TD6");
	setDeviceID(17);	// default Device ID

	// add drivers
	TD6SingleDriver singleDriver = new TD6SingleDriver();
	addDriver(singleDriver);
	addDriver(new TD6BankDriver(singleDriver));
    }

    /**
     * Create a configuration pannel.
     *
     * @return a <code>JPanel</code> value
     */
    public JPanel config() {
	JPanel panel = new JPanel();
	GridBagLayout gridbag = new GridBagLayout();
	panel.setLayout(gridbag);
	GridBagConstraints c = new GridBagConstraints();
	//c.anchor = GridBagConstraints.WEST;
	//c.anchor = c.WEST;
	c.weightx = 1;
	//c.fill = c.HORIZONTAL;
	//c.gridwidth = 2;
	//c.gridheight = 1;

	// top labels
	c.gridx = 0; c.gridy = 0;
	panel.add(new JLabel("Pad Name"), c);
	c.gridx = 1; c.gridy = 0;
	panel.add(new JLabel("Enable"), c);
	c.gridx = 2; c.gridy = 0;
	panel.add(new JLabel("Dual Trigger"), c);

	for (int i = 0; i < padinfo.length; i++) {
	    final Preferences p = prefsPad.node(padinfo[i].name);
	    // pad name label
	    c.gridx = 0; c.gridy = 1 + i;
	    panel.add(new JLabel(padinfo[i].name), c);

	    // check box for pad enable
	    JCheckBox cbox = new JCheckBox();
	    cbox.setSelected(p.getBoolean("padActive", padinfo[i].padActive));
	    final int	index = i;
	    cbox.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			JCheckBox cb = (JCheckBox) e.getSource();
			padinfo[index].padActive = cb.isSelected();
			p.putBoolean("padActive", cb.isSelected());
		    }
		});
	    c.gridx = 1; c.gridy = 1 + i;
	    panel.add(cbox, c);

	    // check box for dual trigger
	    cbox = new JCheckBox();
	    cbox.setEnabled(padinfo[i].dualTrigger);
	    cbox.setSelected(p.getBoolean("dualTriggerActive",
					  padinfo[i].dualTriggerActive));
	    cbox.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			JCheckBox cb = (JCheckBox) e.getSource();
			padinfo[index].dualTriggerActive = cb.isSelected();
			p.putBoolean("dualTriggerActive", cb.isSelected());
		    }
		});
	    c.gridx = 2; c.gridy = 1 + i;
	    panel.add(cbox, c);
	}
	return panel;
    }

    /**
     * Return an array of PadInfo for pads which are enabled.
     *
     * @return a <code>PadInfo[]</code> value
     */
    PadInfo[] activePadInfo() {
	// count number of active pads
	int padNum = 0;
	for (int i = 0; i < padinfo.length; i++) {
	    Preferences p = prefsPad.node(padinfo[i].name);
	    if (p.getBoolean("padActive", padinfo[i].padActive)) {
		padNum++;
	    }
	}

	// create array of padInfo
	PadInfo[] activePad = new PadInfo[padNum];

	// create array of active padInfo
	int n = 0;
	for (int i = 0; i < padinfo.length; i++) {
	    Preferences p = prefsPad.node(padinfo[i].name);
	    if (p.getBoolean("padActive", padinfo[i].padActive)) {
		PadInfo d = (PadInfo) padinfo[i].clone();
		d.padActive = true;
		d.dualTriggerActive = p.getBoolean("dualTriggerActive",
						   padinfo[i].dualTriggerActive);
		activePad[n++] = d;
	    }
	}
	return activePad;
    }

    /*
    // config variable getter
    public String getPadName(int i) {
	return this.padinfo[i].name;
    };
    public boolean getPadEnableFlag(int i) {
	return this.padinfo[i].enable;
    };
    public boolean getDualTriggerFlag(int i) {
	return this.padinfo[i].dualTrigger;
    };
    // config variable setter
    public void setPadEnableFlag(int i, boolean val) {
	this.padinfo[i].enable = val;
    };
    public void setDualTriggerFlag(int i, boolean val) {
	this.padinfo[i].dualTrigger = val;
    };
    */
}
