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
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

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
    /** spinner for MIDI output buffer size. */
    private JSpinner spBufSize;
    /** spinner for MIDI output delay time. */
    private JSpinner spDelay;

    MidiConfigPanel(PrefsDialog parent) {
	super(parent);

	setLayout(new BorderLayout());
	JPanel p = new JPanel(new GridBagLayout());
	GridBagConstraints c = new GridBagConstraints();

	cbxEnMidi = new JCheckBox("Enable MIDI Interface");
	c.gridx = 0; c.gridy = 0; c.gridwidth = 3;
	p.add(cbxEnMidi);

	c.fill = GridBagConstraints.HORIZONTAL;

	// Output Port/Input Port selection
	c.gridx = 0; c.gridy++; c.gridwidth = 1;
	c.insets = new Insets(10, 0, 0, 0);
	p.add(new JLabel("Output Port:"), c);
	cbOut = new JComboBox(MidiUtil.getOutputNames()); // wirski@op.pl
	c.gridx = 1; c.gridwidth = 2;
        p.add(cbOut, c);

	c.gridx = 0; c.gridy++; c.gridwidth = 1;
	c.insets = new Insets(0, 0, 0, 0);
        p.add(new JLabel("Input Port:"), c);
	cbIn = new JComboBox(MidiUtil.getInputNames()); // wirski@op.pl
	c.gridx = 1; c.gridwidth = 2;
        p.add(cbIn, c);

	// master controller selection
	cbxEnMC = new JCheckBox("Enable Master Controller Input Port");
	cbxEnMC.setToolTipText("If enabled MIDI messages from Master Input Port are sent to Output Port.");
	c.gridx = 0; c.gridy++; c.gridwidth = 3;
	p.add(cbxEnMC, c);

	c.gridx = 0; c.gridy++; c.gridwidth = 1;
	JLabel label = new JLabel("Master Input Port:");
	label.setToolTipText("MIDI notes from this port are echoed to the output MIDI port.");
        p.add(label, c);
	c.gridx = 1; c.gridwidth = 2;
	cbMC = new JComboBox(MidiUtil.getInputNames()); // wirski@op.pl
        p.add(cbMC, c);

	// MIDI output buffer size and delay
	c.gridx = 0; c.gridy++; c.gridwidth = 1;
	c.insets = new Insets(10, 0, 0, 0);
	c.fill = GridBagConstraints.NONE;
	c.anchor = GridBagConstraints.WEST;
	label = new JLabel("MIDI Output Buffer Size:");
	label.setToolTipText("MIDI message size." 
	        + " If zero, whole MIDI message is passed to lower MIDI driver."
	        + " Normally set to zero.");
	p.add(label, c);
	c.gridx = 1;
	spBufSize = new JSpinner(new SpinnerNumberModel(0, 0, 512, 16));
	p.add(spBufSize, c);
	c.gridx = 2;
	p.add(new JLabel(" (byte)"), c);

	c.gridx = 0; c.gridy++;
	c.insets = new Insets(0, 0, 0, 0);
	label = new JLabel("MIDI Output Delay:");
	label.setToolTipText("delay after every MIDI message output transfer.");
	p.add(label, c);
	c.gridx = 1;
	spDelay = new JSpinner(new SpinnerNumberModel(0, 0, 500, 10));
	p.add(spDelay, c);
	c.gridx = 2;
	p.add(new JLabel(" (msec)"), c);

	// MIDI loopback test
	c.gridx = 0; c.gridy++;
	c.insets = new Insets(10, 0, 0, 0);
	p.add(new JLabel("MIDI Loopback Test:"), c);
	c.gridx = 1;
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

	spBufSize.setValue(new Integer(AppConfig.getMidiOutBufSize()));
	spDelay.setValue(new Integer(AppConfig.getMidiOutDelay()));

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
	spBufSize.setEnabled(midiEn && MidiUtil.isOutputAvailable());
	spDelay.setEnabled(midiEn && MidiUtil.isOutputAvailable());
    }

    void commitSettings() {
	if (cbxEnMidi.isSelected()) {
	    AppConfig.setMidiEnable(true);
	    AppConfig.setMasterController(cbMC.getSelectedIndex());
	    AppConfig.setMasterInEnable(cbxEnMC.isSelected());
	    AppConfig.setMidiOutBufSize(((Integer) spBufSize.getValue()).intValue());
	    AppConfig.setMidiOutDelay(((Integer) spDelay.getValue()).intValue());

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
