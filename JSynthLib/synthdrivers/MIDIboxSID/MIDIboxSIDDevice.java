/*
 * JSynthlib-Device for MIDIbox SID
 * =====================================================================
 * @author  Thorsten Klose
 * file:    MIDIboxSIDDevice.java
 * date:    2002-11-30
 * @version $Id$
 *
 * Copyright (C) 2002  Thorsten.Klose@gmx.de
 *                     http://www.uCApps.de
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
 */

package synthdrivers.MIDIboxSID;
import core.*;
import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.lang.Integer.*;
import java.awt.event.*;
import javax.sound.midi.*;

import synthdrivers.MIDIboxSID.MIDIboxSIDSlowSender;

public class MIDIboxSIDDevice extends Device
{
    private static final String infoText="This driver has been created for MIDIbox SID, a non-commercial DIY\n"+
        "synthesizer based on the famous Commodore SID soundchip.\n"+
        "\n"+
        "More informations about the features can be found under http://www.uCApps.de/midibox_sid.html\n"+
        "\n"+
        "The configuration menu provides a send button which allows you to change the device ID\n"+
        "and the MIDI channel of your MIDIbox SID(s) via remote. Normaly all MIDIboxes should get\n"+
        "their own unique ID, but sometimes it makes sense to assign the same ID to multiple SIDs\n"+
        "in order to realize nice stereo effects.\n"+
        "Since JSynthLib doesn't differ between channel and ID, both values will be set to the\n"+
        "same number to avoid any confusion.\n"+
        "\n"+
        "HowTo change the MIDI channel & ID:\n"+
        "   o connect only the SID(s) with the MIDI Out of your computer whose channel should be changed\n"+
        "   o go to the configuration tab and select the desired channel\n"+
        "   o press the send button\n"+
        "   o thereafter the channel has been changed and you can connect all other MIDIbox SIDs to\n"+
        "     the MIDI Out again\n"+
        "   o note that the channel and device number will be saved in a non-volatile memory and don't\n"+
	"     have to be set again after the next power-on";


    /** Creates new MIDIboxSIDDevice */
    public MIDIboxSIDDevice ()
    {
	super ("MIDIbox","SID","F000007E46000FF7",infoText,"Thorsten Klose");
        setSynthName("MIDIbox SID");

        addDriver(new MIDIboxSIDSingleDriver());
        addDriver(new MIDIboxSIDBankDriver());
    }

    public JPanel config()
    {
	JPanel panel= new JPanel();
	GridBagLayout gridbag = new GridBagLayout();
	GridBagConstraints c = new GridBagConstraints();

	panel.setLayout(gridbag);
	c.anchor = GridBagConstraints.WEST;

	c.gridx=0;c.gridy=0;c.gridwidth=9;c.gridheight=1;c.weightx=1;c.anchor=c.WEST;c.fill=c.HORIZONTAL;
	panel.add(new JLabel("                                                  "),c);

	c.gridx=0;c.gridy=1;c.gridwidth=3;c.gridheight=2;
	panel.add(new JLabel("Change Device ID and MIDI Channel to:"),c);

	String[] deviceNumbers = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16" };
	JComboBox synthList = new JComboBox(deviceNumbers);
	synthList.setSelectedItem(java.lang.Integer.toString(getChannel()));
	synthList.addActionListener(new ActionListener()
	    {
		public void actionPerformed(ActionEvent e) {
		    JComboBox cb = (JComboBox)e.getSource();
		    String index = (String)cb.getSelectedItem();
		    setChannel(java.lang.Integer.parseInt(index));
		}
	    });
	c.gridx=3;c.gridy=1;c.gridwidth=3;c.gridheight=2;
	panel.add(synthList,c);

	JButton send_button = new JButton("<< SEND >>");

	send_button.addActionListener(new ActionListener()
	    {
		public void actionPerformed(ActionEvent e) {
		    reconfigureDeviceID();
		}
	    });
	c.gridx=3;c.gridy=7;c.gridwidth=3;c.gridheight=2;
	panel.add(send_button,c);

	return panel;
    }

    private void reconfigureDeviceID()
    {
	MIDIboxSIDSlowSender SlowSender = new MIDIboxSIDSlowSender();

	for(int i=0; i<16; ++i) {
	    byte change_id_buffer[]= {
		(byte)0xf0, (byte)0x00, (byte)0x00, (byte)0x7e, (byte)0x46,
		(byte)i, (byte)0x0d,
		(byte)0x03, (byte)0x00, (byte)(getChannel()-1), (byte)0xf7
	    };
	    //SlowSender.sendSysEx(getPort(), change_id_buffer, 10);
	    sendSysEx(change_id_buffer, 10);
	}

	byte change_channel_buffer[]= {
	    (byte)0xf0, (byte)0x00, (byte)0x00, (byte)0x7e, (byte)0x46,
	    (byte)(getChannel()-1), (byte)0x0d,
	    (byte)0x02, (byte)0x00, (byte)(getChannel()-1), (byte)0xf7
	};
	//SlowSender.sendSysEx(getPort(), change_channel_buffer, 10);
	sendSysEx(change_channel_buffer, 10);
    }

    private void sendSysEx(byte[] buf, int delay) {
	SysexMessage m = new SysexMessage();
	try {
	    m.setMessage(buf, buf.length);
	} catch (InvalidMidiDataException e) {
	    ErrorMsg.reportStatus(e);
	}
	send(m);
	try { Thread.sleep(delay); } catch (Exception e) {};
    }
}

