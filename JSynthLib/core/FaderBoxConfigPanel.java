package core;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Config panel for Fader Input setting.
 * @author Joe Emenaker
 * @version $Id$
 */
class FaderBoxConfigPanel extends ConfigPanel {
    {
	panelName = "Fader Box";
	nameSpace = "faderbox";
    }

    private JPanel faderPanel;
    private JComboBox cbFdr;
    private JCheckBox enabledBox;
    private JList lstSl;
    private JComboBox cbControl;
    private JComboBox cbChannel;

    /* keep current values */
    private int slider = 0;
    private int[] control = new int[Constants.NUM_FADERS];
    private int[] channel = new int[Constants.NUM_FADERS];

    FaderBoxConfigPanel(PrefsDialog parent, AppConfig appConfig) {
	super(parent, appConfig);
	setLayout(new GridBagLayout());
	GridBagConstraints gbc = new GridBagConstraints();

	gbc.fill = GridBagConstraints.HORIZONTAL; gbc.ipadx = 1; gbc.anchor = GridBagConstraints.WEST;

	// Fader Port selection
	/*
	JLabel l0 = new JLabel("Receive Faders from MIDI Port:  ");
	gbc.gridx = 0; gbc.gridy = 1; gbc.gridheight = 1; gbc.gridwidth = 4;
	add(l0, gbc);
	*/
	enabledBox = new JCheckBox("Enable Fader Input Port");
	enabledBox.setToolTipText("sliders and buttons are controlled by Control Change MIDI message.");
	gbc.gridx = 1; gbc.gridy = 2; gbc.gridheight = 1; gbc.gridwidth = 2;
	add(enabledBox, gbc);
	enabledBox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
			setContainerEnabled(faderPanel, enabledBox.isSelected());
			setModified(true);
		}
	});

	// create own Fader/Slider Panel
	faderPanel = new JPanel(new BorderLayout(5, 5));
	// upper side
	cbFdr = new JComboBox(MidiUtil.getInputMidiDeviceInfo());
	cbFdr.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    setModified(true);
		}
	    });
	gbc.gridx = 1; gbc.gridy = 2; gbc.gridheight = 1; gbc.gridwidth=GridBagConstraints.REMAINDER;
	faderPanel.add(cbFdr, BorderLayout.NORTH);

	// Init Slider JList
	//  0    : active slider
	//  1-16 : fader 1-16
	// 17-30 : button 1-14
	// 31    : button 15 : prev fader bank
	// 32    : button 16 : next fader bank
	String[] n = new String[Constants.NUM_FADERS];
	n[0] = "Active Slider";
	for (int i = 1; i <= 16; i++) // slider : 1 to 16
	    n[i] = "Slider #" + i;
	for (int i = 1; i <= 14; i++) // button : 1 to 14
	    n[i + 16] = "Button #" + i;
	n[31] = "Button #15: Prev Slider Bank";
	n[32] = "Button #16: Next Slider Bank";
	lstSl = new JList(n);
	lstSl.addListSelectionListener(new ListSelectionListener() {
		public void valueChanged(ListSelectionEvent e) {
		    slider = lstSl.getSelectedIndex();
		    cbControl.setSelectedIndex(control[slider]);
		    cbChannel.setSelectedIndex(channel[slider]);
		    //setModified(true); // don't do that
		}
	    });
	lstSl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

	// MIDI Control Change Message controll # (0-119)
	// 120-127 are reserved for Channel Mode Messages
	String[] cc1 = new String[121];
	for (int i = 0; i < 120; i++)
	    cc1[i] = "" + i;
	cc1[120] = "Off";
	cbControl = new JComboBox(cc1);
	cbControl.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    control[slider] = cbControl.getSelectedIndex();
		    setModified(true);
		}
	    });

	String[] cc2 = new String[17]; // channel
	for (int i = 0; i < 16; i++)
	    cc2[i] = "" + (i + 1);
	cc2[16] = "Off";
	cbChannel = new JComboBox(cc2);
	cbChannel.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    channel[slider] = cbChannel.getSelectedIndex();
		    setModified(true);
		}
	    });

	// left side
	JScrollPane scroll = new JScrollPane(lstSl);
	faderPanel.add(scroll, BorderLayout.WEST);
	// right side
	JPanel dataPanel = new JPanel();
	dataPanel.setLayout(new GridBagLayout());
	gbc.anchor = GridBagConstraints.EAST;
	gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2; gbc.gridheight = 1;
	dataPanel.add(new JLabel("MIDI Channel    #  "), gbc);
	gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.gridheight = 1;
	dataPanel.add(new JLabel("MIDI Control #  "), gbc);
	gbc.gridx = 2; gbc.gridy = 1; gbc.gridwidth = 2; gbc.gridheight = 1;
	dataPanel.add(cbChannel, gbc);
	gbc.gridx = 2; gbc.gridy = 2; gbc.gridwidth = 2; gbc.gridheight = 1;
	dataPanel.add(cbControl, gbc);
	gbc.gridx = 2; gbc.gridy = 5; gbc.gridwidth = 5; gbc.gridheight = 3;
	gbc.fill = GridBagConstraints.BOTH;
	dataPanel.setBorder(new EtchedBorder(EtchedBorder.RAISED));
	faderPanel.add(dataPanel, BorderLayout.CENTER);
	// lower side
	JPanel buttonPanel = new JPanel();
	JButton b0 = new JButton("Reset");
	JButton b1 = new JButton("Peavey PC1600x Preset");
	JButton b2 = new JButton("Kawai K5000 Knobs Preset");
	b0.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    resetSliders();
		    setModified(true);
		}
	    });
	b1.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    presetPC1600x();
		    setModified(true);
		}
	    });
	b2.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    presetKawaiK5000();
		    setModified(true);
		}
	    });

	buttonPanel.add(b0);
	buttonPanel.add(b1);
	buttonPanel.add(b2);

	faderPanel.add(buttonPanel, BorderLayout.SOUTH);

	gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 7; gbc.gridheight = 1;
	add(faderPanel, gbc);
    }

    void init() {
	try {
	    if (MidiUtil.isInputAvailable())
		// index 0 may be unavailable
		cbFdr.setSelectedIndex(appConfig.getFaderPort());
	} catch (IllegalArgumentException e) {
	    cbFdr.setSelectedIndex(0);
	}

	// copy into the temporary array.
	for (int i = 0; i < Constants.NUM_FADERS; i++) {
	    control[i] = appConfig.getFaderControl(i);
	    channel[i] = appConfig.getFaderChannel(i);
	}
	lstSl.setSelectedIndex(slider);
	cbControl.setSelectedIndex(control[slider]);
	cbChannel.setSelectedIndex(channel[slider]);

	enabledBox.setSelected(appConfig.getFaderEnable());
	enabledBox.setEnabled(MidiUtil.isInputAvailable()
			      && appConfig.getMidiEnable());

	setContainerEnabled(faderPanel,
			    enabledBox.isEnabled() && enabledBox.isSelected());
    }

    void commitSettings() {
	appConfig.setFaderEnable(enabledBox.isSelected());
	appConfig.setFaderPort(cbFdr.getSelectedIndex());
	for (int i = 0; i < Constants.NUM_FADERS; i++) {
	    appConfig.setFaderControl(i, control[i]);
	    appConfig.setFaderChannel(i, channel[i]);
	}
	setModified(false);
    }

    /*
     * Private methods
     */
    private void resetSliders() { // disable all
	for (int i = 0; i < Constants.NUM_FADERS; i++) {
	    channel[i] = 0;
	    control[i] = 120;
	}
	cbControl.setSelectedIndex(control[slider]);
	cbChannel.setSelectedIndex(channel[slider]);
    }

    private void presetPC1600x() {
	channel[0] = 16;
	control[0] = 120;
	for (int i = 1; i < 17; i++) {
	    channel[i] = i - 1;
	    control[i] = 24;
	}
	for (int i = 17; i < Constants.NUM_FADERS; i++) {
	    channel[i] = i - 17;
	    control[i] = 25;
	}
	cbChannel.setSelectedIndex(channel[slider]);
	cbControl.setSelectedIndex(control[slider]);
    }

    private void presetKawaiK5000() {
	channel[0] = 0;
	control[0] = 1;
	for (int i = 1; i < 17; i++) {
	    channel[i] = 0;
	}
	for (int i = 17; i < Constants.NUM_FADERS; i++) {
	    channel[i] = 16;
	    control[i] = 120;
	}
	control[1]  = 16;
	control[2]  = 18;
	control[3]  = 74;
	control[4]  = 73;
	control[5]  = 17;
	control[6]  = 19;
	control[7]  = 77;
	control[8]  = 78;
	control[9]  = 71;
	control[10] = 75;
	control[11] = 76;
	control[12] = 72;
	control[13] = 80;
	control[14] = 81;
	control[15] = 82;
	control[16] = 83;

	cbControl.setSelectedIndex(control[slider]);
	cbChannel.setSelectedIndex(channel[slider]);
    }
}
