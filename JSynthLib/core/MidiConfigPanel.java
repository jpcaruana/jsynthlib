package core;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * The panel that configures the MIDI layer. Taken out of PrefsDialog.
 * @author Joe Emenaker
 * @author Hiroo Hayashi
 * @version $Id$
 */
class MidiConfigPanel extends ConfigPanel {
    {
	panelName = "MIDI";
	nameSpace = "midi";
    }

    /** CheckBox for MIDI */
    private JCheckBox cbxEnMidi;
    /** ComboBox for MIDI Out port. */
    private JComboBox cbOut;
    /** ComboBox for MIDI In port. */
    private JComboBox cbIn;
    /** ComboBox for MIDI In port for Master Controller. */
    private JComboBox cbMC;
    /** CheckBox for Master Controller. */
    private JCheckBox cbxEnMC;
    /** button for loop-back test. */
    private JButton testButton;

    MidiConfigPanel(PrefsDialog parent) {
	super(parent);

	setLayout(new BorderLayout());
	JPanel p = new JPanel(new GridBagLayout());
	GridBagConstraints c = new GridBagConstraints();

	cbxEnMidi = new JCheckBox("Enable MIDI Interface");
	c.gridx = 0; c.gridy = 0; c.gridwidth = 2;
	p.add(cbxEnMidi);

	c.fill = GridBagConstraints.HORIZONTAL;

	// Output Port/Input Port selection
	c.gridx = 0; c.gridy++; c.gridwidth = 1;
	c.insets = new Insets(10, 0, 0, 0);
	p.add(new JLabel("Output Port:"), c);
	cbOut = new JComboBox(MidiUtil.getOutputMidiDeviceInfo());
	c.gridx = 1;
        p.add(cbOut, c);

	c.gridx = 0; c.gridy++;
	c.insets = new Insets(0, 0, 0, 0);
        p.add(new JLabel("Input Port:"), c);
	cbIn = new JComboBox(MidiUtil.getInputMidiDeviceInfo());
	c.gridx = 1;
        p.add(cbIn, c);

	// master controller selection
	cbxEnMC = new JCheckBox("Enable Master Controller Input Port");
	cbxEnMC.setToolTipText("If enabled MIDI messages from Master Input Port are sent to Output Port.");
	c.gridx = 0; c.gridy++; c.gridwidth = 2;
	p.add(cbxEnMC, c);

	c.gridx = 0; c.gridy++; c.gridwidth = 1;
	JLabel cbMInLabel = new JLabel("Master Input Port:");
	cbMInLabel.setToolTipText("MIDI notes from this port are echoed to the output MIDI port.");
        p.add(cbMInLabel, c);
	c.gridx = 1;
	cbMC = new JComboBox(MidiUtil.getInputMidiDeviceInfo());
        p.add(cbMC, c);

	// MIDI loopback test
	c.gridx = 0; c.gridy++;
	c.insets = new Insets(10, 0, 0, 0);
	p.add(new JLabel("MIDI Loopback Test:"), c);
	c.gridx = 1;
	c.fill = GridBagConstraints.NONE;
	c.anchor = GridBagConstraints.WEST;
	testButton = new JButton("Run...");
	p.add(testButton, c);

	add(p, BorderLayout.CENTER);

	// add actionListeners
	cbxEnMidi.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    setEnable(cbxEnMidi.isSelected());
		    setModified(true);
		}
	    });

	cbxEnMC.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    cbMC.setEnabled(cbxEnMC.isSelected());
		    setModified(true);
		}
	    });

	ActionListener al = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    setModified(true);
		}
	    };
	cbOut.addActionListener(al);
	cbIn.addActionListener(al);
	cbMC.addActionListener(al);

	testButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    MidiTest.runLoopbackTest(cbIn.getSelectedIndex(),
					     cbOut.getSelectedIndex());
		}
	    });
    }

    void init() {
	cbxEnMidi.setSelected(AppConfig.getMidiEnable());
	cbxEnMC.setSelected(AppConfig.getMasterInEnable());

	try {
	    cbOut.setSelectedIndex(AppConfig.getInitPortOut());
	    cbIn.setSelectedIndex(AppConfig.getInitPortIn());
	    cbMC.setSelectedIndex(AppConfig.getMasterController());
	} catch (IllegalArgumentException e) {
	    ErrorMsg.reportStatus(e);
	}

	// disable MIDI when either MIDI input or MIDI output is unavailable.
	cbxEnMidi.setEnabled(MidiUtil.isOutputAvailable()
			     || MidiUtil.isInputAvailable());
	setEnable(AppConfig.getMidiEnable());
    }

    /**
     * enable/disable widgets according to the various settings..
     */
    private void setEnable(boolean midiEn) {
	cbxEnMC.setEnabled(midiEn
			   && MidiUtil.isOutputAvailable()
			   && MidiUtil.isInputAvailable());
	testButton.setEnabled(midiEn
			      && MidiUtil.isOutputAvailable()
			      && MidiUtil.isInputAvailable());

	cbOut.setEnabled(midiEn && MidiUtil.isOutputAvailable());
	cbIn.setEnabled(midiEn && MidiUtil.isInputAvailable());
	cbMC.setEnabled(midiEn && cbxEnMC.isSelected());
    }

    void commitSettings() {
	if (cbxEnMidi.isSelected()) {
	    AppConfig.setMidiEnable(true);
	    AppConfig.setMasterController(cbMC.getSelectedIndex());
	    AppConfig.setMasterInEnable(cbxEnMC.isSelected());

	    int out = cbOut.getSelectedIndex();
	    int in  = cbIn.getSelectedIndex();
	    AppConfig.setInitPortOut(out);
	    AppConfig.setInitPortIn(in);
	    if (!AppConfig.getMultiMIDI()) {
		// change MIDI ports of all Devices
		for (int i = 0; i < AppConfig.deviceCount(); i++) {
		    AppConfig.getDevice(i).setPort(out);
		    AppConfig.getDevice(i).setInPort(in);
		}
	    }
	} else {
	    AppConfig.setMidiEnable(false);
	}
	setModified(false);
    }
}
