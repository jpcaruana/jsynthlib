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

package synthdrivers.RolandTD6;

import core.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

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
   PadInfo[] padinfo = {
    //				offset,	DT pad, DT active,active
    new PadInfo("Kick",		0x100,	false,	false,	true),
    new PadInfo("Snare",	0x300,	true,	false,	true),
    new PadInfo("Hi-Hat",	0x700,	true,	false,	true),
    new PadInfo("Ride",		0xa00,	true,	true,	true),
    new PadInfo("Crash 1",	0x800,	true,	true,	true),
    new PadInfo("Crash 2",	0x900,	true,	true,	false),
    new PadInfo("Tom 1",	0x400,	true,	false,	true),
    new PadInfo("Tom 2",	0x500,	false,	false,	true),
    new PadInfo("Tom 3",	0x600,	false,	false,	true),
    new PadInfo("Tom 4",	0xc00,	false,	false,	false),
    new PadInfo("AUX",		0xb00,	false,	false,	false),
  };

  /**
   * Creates a new <code>RolandTD6Device</code> instance.
   *
   */
  public RolandTD6Device() {
    manufacturerName = "Roland";
    // What is the difference between modelName and synthName ?
    modelName	= "TD6";
    setSynthName("TD6");
    // Why both Device class and Driver class have to have inquiryID?
    inquiryID	= "F07E**0602413F01000000020000f7";
    //synthName	= "TD6";
    infoText	=
      "Driver for TD-6 Roland Percussion Sound Module.\n" +
      "This driver is still under development.\n" +
      "This driver uses Drum Kit 99 as edit buffer.\n" +
      "Set \"Sync Mode\" to \"EXT\" to stop sending Timing Clock system real time message (0xF8).";
    authors	= "Hiroo Hayashi <hiroo.hayashi@computer.org>";
    // Why both Device class and Driver class have to have channel?
    channel	= 17;		// default Device ID

    addDriver(new TD6SingleDriver());
  }

  /**
   * Create configuration pannel.
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
      // pad name label
      c.gridx = 0; c.gridy = 1 + i;
      panel.add(new JLabel(padinfo[i].name), c);

      // check box for pad enable
      JCheckBox cbox = new JCheckBox();
      cbox.setSelected(padinfo[i].padActive);
      final int	index = i;	// understand why this work!!!FIXIT!!!
      cbox.addActionListener(new ActionListener() {
	  public void actionPerformed(ActionEvent e) {
	    JCheckBox cb = (JCheckBox) e.getSource();
	    padinfo[index].padActive = cb.isSelected();
	  }
	});
      c.gridx = 1; c.gridy = 1 + i;
      panel.add(cbox, c);

      // check box for dual trigger
      cbox = new JCheckBox();
      cbox.setEnabled(padinfo[i].dualTrigger);
      cbox.setSelected(padinfo[i].dualTriggerActive);
      cbox.addActionListener(new ActionListener() {
	  public void actionPerformed(ActionEvent e) {
	    JCheckBox cb = (JCheckBox) e.getSource();
	    padinfo[index].dualTriggerActive = cb.isSelected();
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
  public PadInfo[] activePadInfo() {
    // count number of active pads
    int padNum = 0;
    for (int i = 0; i < padinfo.length; i++) {
      if (padinfo[i].padActive) {
	padNum++;
      }
    }

    // create array of padInfo
    PadInfo[] activePad = new PadInfo[padNum];

    // create array of active padInfo
    int n = 0;
    for (int i = 0; i < padinfo.length; i++) {
      if (padinfo[i].padActive)
	activePad[n++] = padinfo[i];
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
