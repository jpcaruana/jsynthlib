
package core; //TODO org.jsynthlib.midi;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
//import core.*;

/**
 * The panel that configures the MIDI layer. Taken out of PrefsDialog.
 * @author Joe Emenaker
 * @version $Id$
 */
public class MidiConfigPanel extends ConfigPanel {
    /** CheckBox for MIDI */
    private JCheckBox cbxEnMidi;
    /** ComboBox for MIDI Out port. */
    private JComboBox cbOut = null;
    /** ComboBox for MIDI In port. */
    private JComboBox cbIn = null;
    /** ComboBox for MIDI In port for Master Controller. */
    private JComboBox cbMC = null;
    /** CheckBox for Master Controller */
    private JCheckBox cbxEnMC;

    private JPanel channelPanel = null;
    private JPanel cbPanel = null;

    public MidiConfigPanel(AppConfig appConfig) {
	super(appConfig);

	setLayout (new core.ColumnLayout ());
	//setPreferredSize(new Dimension(500,250));

	cbxEnMidi = new JCheckBox ("Enable MIDI Interface");
	cbxEnMidi.addActionListener(new ActionListener() {
		public void actionPerformed (ActionEvent e) {
		    setContainerEnabled(channelPanel, cbxEnMidi.isSelected());
		    cbMC.setEnabled(cbxEnMC.isSelected());
		    disableUnavailableWidgets();
		}
	});

	add(cbxEnMidi);

	// panel for other settings
	channelPanel = new JPanel();
        channelPanel.setLayout(new core.ColumnLayout());

	GridBagLayout gridbag = new GridBagLayout();
	GridBagConstraints gbc = new GridBagConstraints();
	cbPanel = new JPanel(gridbag);
	gbc.fill=GridBagConstraints.HORIZONTAL;
	gbc.ipadx=1;
	gbc.anchor=GridBagConstraints.WEST;


	// make a space
	gbc.gridx=0; gbc.gridy=2; gbc.gridheight=1; gbc.gridwidth=1;
	JLabel l3=new JLabel (" ");
	gridbag.setConstraints(l3, gbc);
        cbPanel.add (l3);
	// Output Port/Input Port selection
	gbc.gridx=0; gbc.gridy=3; gbc.gridheight=1; gbc.gridwidth=3; //gbc.gridwidth=gbc.REMAINDER;
        JLabel cbLabel = new JLabel ("Run Startup Initialization on MIDI Ports:");
	gridbag.setConstraints(cbLabel, gbc);
	cbPanel.add (cbLabel);

	gbc.gridx=0; gbc.gridy=4; gbc.gridheight=1; gbc.gridwidth=1;
	JLabel cbOutLabel = new JLabel("Out Port:");
	gridbag.setConstraints(cbOutLabel, gbc);
	cbPanel.add (cbOutLabel);
	cbOut = new JComboBox(MidiUtil.getOutputMidiDeviceInfo());
	gbc.gridx=1; gbc.gridy=4; gbc.gridheight=1; gbc.gridwidth=GridBagConstraints.REMAINDER;
	gridbag.setConstraints(cbOut, gbc);
        cbPanel.add (cbOut);

	gbc.gridx=0; gbc.gridy=5; gbc.gridheight=1; gbc.gridwidth=1;
	JLabel cbInLabel = new JLabel("In Port:");
	gridbag.setConstraints(cbInLabel, gbc);
        cbPanel.add (cbInLabel);
	cbIn = new JComboBox(MidiUtil.getInputMidiDeviceInfo());
	gbc.gridx=1; gbc.gridy=5; gbc.gridheight=1; gbc.gridwidth=GridBagConstraints.REMAINDER;
	gridbag.setConstraints(cbIn, gbc);
        cbPanel.add (cbIn);

	// MIDI loopback test
	gbc.gridx=1; gbc.gridy=6; gbc.gridheight=1; gbc.gridwidth=1;gbc.fill=GridBagConstraints.NONE;
        JButton testButton = new JButton("MIDI Loopback Test...");
	testButton.addActionListener (new ActionListener () {
		public void actionPerformed (ActionEvent e) {
		    MidiTest.runLoopbackTest(cbIn.getSelectedIndex(),
					     cbOut.getSelectedIndex());
		}
	    });
	gridbag.setConstraints(testButton, gbc);
	cbPanel.add(testButton);

	// make a space
	gbc.gridx=0; gbc.gridy=7; gbc.gridheight=1; gbc.gridwidth=1; gbc.fill=GridBagConstraints.HORIZONTAL;
	JLabel l0=new JLabel (" ");
	gridbag.setConstraints(l0, gbc);
        cbPanel.add (l0);
	// master controller selection
	gbc.gridx=0; gbc.gridy=8; gbc.gridheight=1; gbc.gridwidth=GridBagConstraints.REMAINDER; // gbc.gridwidth=3;
	JLabel l1=new JLabel ("Receive from Master Controller on MIDI Port:");
	gridbag.setConstraints(l1, gbc);
        cbPanel.add (l1);

	cbxEnMC = new JCheckBox ("Enable Master Controller");
	gbc.gridx = 1; gbc.gridy = 9; gbc.gridwidth = 1; gbc.gridheight = 1;
	cbPanel.add(cbxEnMC, gbc);
	cbxEnMC.addActionListener(new ActionListener() {
		public void actionPerformed (ActionEvent e) {
		    cbMC.setEnabled(cbxEnMC.isSelected());
		}
	    });

	gbc.gridx=1; gbc.gridy=10; gbc.gridheight=1; gbc.gridwidth=GridBagConstraints.REMAINDER;
	cbMC = new JComboBox(MidiUtil.getInputMidiDeviceInfo());
	gridbag.setConstraints(cbMC, gbc);
        cbPanel.add (cbMC);

	channelPanel.add(cbPanel);
	add(channelPanel);
    }

    /**
     * This should return the name that should go on the tab (if using
     * a tabbed config dialog).  Otherwise, it could be used for a
     * frame title, etc. However, this can be overridden by certain
     * constructors.
     */
    protected final String getDefaultPanelName() {
	return("MIDI");
    }

    /**
     * This should return the AppConfig namespace to use if one isn't
     * specified in the constructor
     */
    protected final String getDefaultNamespace() {
	return("midi");
    }

    public void commitSettings() {
	appConfig.setMidiEnable(cbxEnMidi.isSelected());
	if (!cbxEnMidi.isSelected())
	    return;

	appConfig.setInitPortIn(cbIn.getSelectedIndex());
	appConfig.setInitPortOut(cbOut.getSelectedIndex());
	appConfig.setMasterController(cbMC.getSelectedIndex());
	appConfig.setMasterInEnable(cbxEnMC.isSelected());
    }

    public void init() {
	if (!MidiUtil.isOutputAvailable() && !MidiUtil.isInputAvailable()) {
	    appConfig.setMidiEnable(false);
	    cbxEnMidi.setSelected(false);
	    cbxEnMidi.setEnabled(false);
	} else {
	    cbxEnMidi.setSelected(appConfig.getMidiEnable());
	}
	setContainerEnabled(channelPanel, cbxEnMidi.isSelected());
	cbxEnMC.setSelected(appConfig.getMasterInEnable());
	cbMC.setEnabled(cbxEnMC.isSelected());

	setComboBox(cbOut, PatchEdit.appConfig.getInitPortOut());
	setComboBox(cbIn,  PatchEdit.appConfig.getInitPortIn());
	setComboBox(cbMC,  PatchEdit.appConfig.getMasterController());

	disableUnavailableWidgets();
    }

    private void disableUnavailableWidgets() {
	// master controller requires both MIDI input and output
	if (!MidiUtil.isOutputAvailable() || !MidiUtil.isInputAvailable()) {
	    appConfig.setMasterInEnable(false);
	    cbxEnMC.setEnabled(false);
	    cbxEnMC.setSelected(false);
	    cbMC.setEnabled(false);
	}

	cbOut.setEnabled(MidiUtil.isOutputAvailable());
	cbIn.setEnabled(MidiUtil.isInputAvailable());
	//midiDriverSelected((MidiWrapper) cbDriver.getSelectedItem());
    }

    private static void setComboBox(JComboBox cb, int idx) {
	try {
	    cb.setSelectedIndex(idx);
	} catch (IllegalArgumentException e) {
	    ErrorMsg.reportStatus(e);
	}
    }
}
