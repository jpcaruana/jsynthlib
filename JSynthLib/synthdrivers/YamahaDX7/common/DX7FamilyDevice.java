/*
 * JSynthlib - generic Device class for Yamaha DX7 Family
 * (used by DX1, DX5, DX7 MKI, TX7, TX816, DX7-II, DX7s, TX802)
 * =============================================================
 * @version $Id$
 * @author  Torsten Tittmann
 *
 * Copyright (C) 2002-2004 Torsten.Tittmann@gmx.de
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
package synthdrivers.YamahaDX7.common;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Set;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import core.Device;

public class DX7FamilyDevice extends Device
{
	// flag interpretation: bit1 - 1=on, 0=off
	//			bit2 - 1=enabled, 0=disabled
	
	// Simulate panel button pushes by sending SysEx commands ?
	protected JCheckBox	sPBP;
	protected int		sPBPflag;
	// switch off memory protection?
	protected JCheckBox	swOffMemProt;
	protected int		swOffMemProtFlag;
	// switch off "Hints and Tips Messages"?
	protected JCheckBox	tipsMsg;
	protected int		tipsMsgFlag;

	// Creates a new YamahaDX7FamilyDevice
	// infact it calls only the constructor of the Device class
	public DX7FamilyDevice (String manufacturerName, String modelName, String inquiryID, String infoText, String authors)
	{
		super (manufacturerName, modelName, inquiryID, infoText, authors);
	}

	protected JPanel config()
	{
		JPanel panel= new JPanel();
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		panel.setLayout(gridbag);
		c.anchor = GridBagConstraints.WEST; 

		c.gridx=0;c.gridy=0;c.gridwidth=9;c.gridheight=1;c.weightx=1;c.anchor=GridBagConstraints.WEST;c.fill=GridBagConstraints.HORIZONTAL;
		panel.add(new JLabel("						    "),c);

		c.gridx=0;c.gridy=1;c.gridwidth=3;c.gridheight=2;
		panel.add(new JLabel("Synthesizer Name"),c);
		final JTextField sl = new JTextField(getSynthName());
		sl.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setSynthName(sl.getText());
			}
		});
		c.gridx=3;c.gridy=1;c.gridwidth=3;c.gridheight=2;
		panel.add(sl,c);
		c.gridx=4;c.gridy=1;c.gridwidth=3;c.gridheight=1;
		panel.add(new JLabel(" ( free choosable )"),c);

		c.gridx=0;c.gridy=3;c.gridwidth=3;c.gridheight=2;
		panel.add(new JLabel("Enable Remote Control?"),c);
		sPBP = new JCheckBox();
		sPBP.setSelected( (getSPBPflag() & 0x01) == 0x01);
		sPBP.setEnabled ( (getSPBPflag() & 0x02) == 0x02);
		sPBP.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JCheckBox chb = (JCheckBox)e.getSource();
				if (chb.isSelected()) setSPBPflag( getSPBPflag() | 0x01);
				else setSPBPflag( getSPBPflag() & 0xfe);
			}
		});
		c.gridx=3;c.gridy=3;c.gridwidth=1;c.gridheight=2;
		panel.add(sPBP,c);

		c.gridx=0;c.gridy=5;c.gridwidth=3;c.gridheight=2;
		panel.add(new JLabel("Disable Memory Protection?"),c);
		swOffMemProt = new JCheckBox();
		swOffMemProt.setSelected( (getSwOffMemProtFlag() & 0x01) == 0x01);
		swOffMemProt.setEnabled ( (getSwOffMemProtFlag() & 0x02) == 0x02);
		swOffMemProt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JCheckBox chb = (JCheckBox)e.getSource();
				if (chb.isSelected()) setSwOffMemProtFlag( getSwOffMemProtFlag() | 0x01);
				else setSwOffMemProtFlag( getSwOffMemProtFlag() & 0xfe);
			}
		});
		c.gridx=3;c.gridy=5;c.gridwidth=1;c.gridheight=2;
		panel.add(swOffMemProt,c);

		c.gridx=0;c.gridy=7;c.gridwidth=3;c.gridheight=2;
		panel.add(new JLabel("Display Hints and Tips?"),c);
		tipsMsg = new JCheckBox();
		tipsMsg.setSelected( (getTipsMsgFlag() & 0x01) == 0x01);
		tipsMsg.setEnabled ( (getTipsMsgFlag() & 0x02) == 0x02);
		tipsMsg.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JCheckBox chb = (JCheckBox)e.getSource();
				if (chb.isSelected()) setTipsMsgFlag( getTipsMsgFlag() | 0x01);
				else setTipsMsgFlag( getTipsMsgFlag() & 0xfe);
			}
		});
		c.gridx=3;c.gridy=7;c.gridwidth=1;c.gridheight=2;
		panel.add(tipsMsg,c);

		return panel;
	  }

	// For storable interface

	/** Getter for sPBPflag */
	public int getSPBPflag() { return this.sPBPflag; };
	/** Setter for sPBPflag */
	public void setSPBPflag(int sPBPflag) { this.sPBPflag = sPBPflag; };

	/** Getter for swOffMemProtFlag */
	public int getSwOffMemProtFlag() { return this.swOffMemProtFlag; };
	/** Setter for swOffMemProtFlag */
	public void setSwOffMemProtFlag(int swOffMemProtFlag) { this.swOffMemProtFlag = swOffMemProtFlag; };

	/** Getter for tipsMsgFlag */
	public int getTipsMsgFlag() { return this.tipsMsgFlag; };
	/** Setter for tipsMsgFlag */
	public void setTipsMsgFlag(int tipsMsgFlag) { this.tipsMsgFlag = tipsMsgFlag; };


	/**
	 * Get the names of properties that should be stored and loaded.
	 * @return a Set of field names
	 */
	public Set storedProperties()
	{
		final String[] storedPropertyNames = {
				 "sPBPflag", "swOffMemProtFlag", "tipsMsgFlag"
		};
		Set set = super.storedProperties();
		set.addAll(Arrays.asList(storedPropertyNames));
		return set;
	}

	/**
	 * Method that will be called after loading
	 */
	public void afterRestore() {}

}
