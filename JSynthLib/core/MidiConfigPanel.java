
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
    /** ComboBox for MIDI Wrapper. */
    private JComboBox cbDriver = null;

    private JPanel channelPanel = null;
    private JPanel cbPanel = null;
    private Vector driverChangeListeners;

    public MidiConfigPanel(AppConfig appConfig) {
	super(appConfig);
	driverChangeListeners = new Vector();

	setLayout (new core.ColumnLayout ());
	//setPreferredSize(new Dimension(500,250));

	cbxEnMidi = new JCheckBox ("Enable MIDI Interface");
	cbxEnMidi.addActionListener(new ActionListener() {
		public void actionPerformed (ActionEvent e) {
		    setContainerEnabled(channelPanel, cbxEnMidi.isSelected());
		    cbMC.setEnabled(cbxEnMC.isSelected());
		}
	});

	add(cbxEnMidi);

	// MIDI wrapper selection Combobox
	if (!PatchEdit.newMidiAPI) { // only Javasound
	    JLabel l2 = new JLabel ("MIDI Access Method:");
	    add (l2);

	    cbDriver=new JComboBox ();
	    add (cbDriver);
	    // Fill the combo box with all Midi wrappers in the midiimps vector
	    Vector midiImps = AppConfig.midiWrappers;
	    for(int i=0; i<midiImps.size(); i++) {
		cbDriver.addItem(midiImps.elementAt(i));
	    }
	    cbDriver.addItemListener (new ItemListener () {
		    public void itemStateChanged (ItemEvent e) {
			if (e.getStateChange ()==ItemEvent.SELECTED) {
			    System.out.println("itemStateChanged");
			    resetMidiDriver();
			    //resetPortComboBoxes();
			    //midiDriverSelected((MidiWrapper) ((JComboBox)e.getSource()).getSelectedItem());
			}
		    }
		});
	}

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
	if (PatchEdit.newMidiAPI)
	    cbOut = new JComboBox(MidiUtil.getOutputMidiDeviceInfo());
	else
	    cbOut = new JComboBox();
	gbc.gridx=1; gbc.gridy=4; gbc.gridheight=1; gbc.gridwidth=GridBagConstraints.REMAINDER;
	gridbag.setConstraints(cbOut, gbc);
        cbPanel.add (cbOut);

	gbc.gridx=0; gbc.gridy=5; gbc.gridheight=1; gbc.gridwidth=1;
	JLabel cbInLabel = new JLabel("In Port:");
	gridbag.setConstraints(cbInLabel, gbc);
        cbPanel.add (cbInLabel);
	if (PatchEdit.newMidiAPI)
	    cbIn = new JComboBox(MidiUtil.getInputMidiDeviceInfo());
	else
	    cbIn = new JComboBox();
	gbc.gridx=1; gbc.gridy=5; gbc.gridheight=1; gbc.gridwidth=GridBagConstraints.REMAINDER;
	gridbag.setConstraints(cbIn, gbc);
        cbPanel.add (cbIn);

	// MIDI loopback test
	gbc.gridx=1; gbc.gridy=6; gbc.gridheight=1; gbc.gridwidth=1;gbc.fill=GridBagConstraints.NONE;
        JButton testButton = new JButton("MIDI Loopback Test...");
	testButton.addActionListener (new ActionListener () {
		public void actionPerformed (ActionEvent e) {
		    MidiTest.runLoopbackTest(AppConfig.getMidiWrapper(),
					     cbIn.getSelectedIndex(),
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
	if (PatchEdit.newMidiAPI)
	    cbMC = new JComboBox(MidiUtil.getInputMidiDeviceInfo());
	else
	    cbMC = new JComboBox();
	gridbag.setConstraints(cbMC, gbc);
        cbPanel.add (cbMC);

	channelPanel.add(cbPanel);
	add(channelPanel);
	//init();
    }

    /**
     * This allows other classes to sign up to be notified any time
     * the midi Driver changes. - emenake 3/12/2003
     * @param listener
     */
    public void addDriverChangeListener(MidiDriverChangeListener listener) {
	if(!driverChangeListeners.contains(listener)) {
	    driverChangeListeners.add(listener);
	}
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

	if (!PatchEdit.newMidiAPI)
	    appConfig.setMidiPlatform(cbDriver.getSelectedIndex());

	appConfig.setInitPortIn(cbIn.getSelectedIndex());
	appConfig.setInitPortOut(cbOut.getSelectedIndex());
	appConfig.setMasterController(cbMC.getSelectedIndex());
	appConfig.setMasterInEnable(cbxEnMC.isSelected());

	/*
	if (!PatchEdit.newMidiAPI) {
	    // Notify all listeners
	    for(int i = 0; i < driverChangeListeners.size(); i++) {
		((MidiDriverChangeListener) driverChangeListeners.elementAt(i)).midiDriverChanged(AppConfig.getMidiWrapper());
	    }
	    resetPortComboBoxes();
	}
	*/
    }

    public void init() {
	if (!PatchEdit.newMidiAPI)
	    setComboBox(cbDriver, PatchEdit.appConfig.getMidiPlatform());
	cbxEnMidi.setSelected(appConfig.getMidiEnable());
	setContainerEnabled(channelPanel, cbxEnMidi.isSelected());
	cbxEnMC.setSelected(appConfig.getMasterInEnable());
	cbMC.setEnabled(cbxEnMC.isSelected());
	if (PatchEdit.newMidiAPI) {
	    setComboBox(cbOut, PatchEdit.appConfig.getInitPortOut());
	    setComboBox(cbIn,  PatchEdit.appConfig.getInitPortIn());
	    setComboBox(cbMC,  PatchEdit.appConfig.getMasterController());
	} else {
	    resetPortComboBoxes();
	}
	//midiDriverSelected((MidiWrapper) cbDriver.getSelectedItem());
    }


    /**
     * This set the choosen Midi driver from the "Midi Access Method" ComboBox,
     * notify all MidiDriverChangeListener about the change and
     * reset the port-selector combo boxes of the now current Midi driver.
     */
    private void resetMidiDriver() { // only for old MIDI layer
	appConfig.setMidiPlatform(cbDriver.getSelectedIndex());
	// Notify all listeners
	for(int i = 0; i < driverChangeListeners.size(); i++) {
	    ((MidiDriverChangeListener) driverChangeListeners.elementAt(i)).midiDriverChanged(AppConfig.getMidiWrapper());
	}
	resetPortComboBoxes();
    }

    /**
     * This clears the port-selector combo boxes and re-populates them with the
     * ports available from the current MIDI driver - emenaker 2003.03.19
     */
    private void resetPortComboBoxes() { // only for old MIDI layer
	MidiWrapper currentDriver = AppConfig.getMidiWrapper();
	cbOut.removeAllItems ();
	cbIn.removeAllItems ();
	cbMC.removeAllItems ();
	try {
	    for (int j=0; j< currentDriver.getNumOutputDevices ();j++)
		cbOut.addItem (j+": "+ currentDriver.getOutputDeviceName (j));
	    for (int j=0; j< currentDriver.getNumInputDevices ();j++)
		cbIn.addItem (j+": "+currentDriver.getInputDeviceName (j));
	    for (int j=0; j< currentDriver.getNumInputDevices ();j++)
		cbMC.addItem (j+": "+currentDriver.getInputDeviceName (j));
	} catch (Exception e) {
	    ErrorMsg.reportStatus(e);
	}

	setComboBox(cbOut, PatchEdit.appConfig.getInitPortOut());
	setComboBox(cbIn,  PatchEdit.appConfig.getInitPortIn());
	setComboBox(cbMC,  PatchEdit.appConfig.getMasterController());
	setContainerEnabled(channelPanel, cbxEnMidi.isSelected() && currentDriver.isReady());
	cbMC.setEnabled(cbxEnMC.isEnabled() && cbxEnMC.isSelected());
	//cbxEnMC.setSelected(PatchEdit.appConfig.getMasterInEnable());

	repackContainer();
    }

    /**
     * This handles the issues surrounding switching to a new
     * driver. This breaks down to several tasks:
     *
     * 1 - Make sure that we weren't asked to switch to the driver
     *     we're already using
     * 2 - Close the old driver
     * 3 - Initialize/open the new driver
     * 4 - Notify all MidiDriverChangeListeners about the new driver
     * 5 - Re-initialize all of the comboboxes that hold the midi device names
     * - emenaker 2003.03.19
     *
     * @param selectedDriver The new driver to switch to
     */
    /*
    private void midiDriverSelected(MidiWrapper selectedDriver) {
	MidiWrapper currentDriver = appConfig.getMidiWrapper();
	System.out.println("Something selected \""+selectedDriver+"\"");
	if(currentDriver != null) {
	    // Did they select the driver we're already using?
	    if(currentDriver.toString().equals(selectedDriver.toString())) {
		// There's already a driver in use and it's the one
		// they selected. Do nothing.
		System.out.println("We're already using that driver");
		return;
	    }
	    // Either the current driver is null, or they selected a
	    // different driver from the current one
	    currentDriver.close();
	}

	System.out.println("Initializing driver:"+selectedDriver.toString());
	initNewMidiDriver(selectedDriver);
	resetPortComboBoxes();
	return;
    }
    */
    /**
     * This initializes a new midi driver. Basically, it just trys to
     * call the "init(int,int)" method on it and, provided that there
     * weren't any exceptions, switch the currentDriver variable to
     * hold the new driver object
     * - emenaker 2003.03.19
     *
     * @param newDriver The driver to initialize
     */
    /*
    private void initNewMidiDriver(MidiWrapper newDriver) {
	// We're probably supposed to show this to the user.....
	//JOptionPane.showMessageDialog (null, "You must exit and restart the program for your changes to take effect","Changing L&F / Platform", JOptionPane.INFORMATION_MESSAGE);

        try {
	    newDriver.init(PatchEdit.appConfig.getInitPortIn(),PatchEdit.appConfig.getInitPortOut());
	    // If an exception wasn't thrown by now, then I guess the
	    // driver change went smoothly.
	    // Change the MidiOut and MidiIn devices...
	    currentDriver = newDriver;
	    // Notify all listeners
	    for(int i=0; i<driverChangeListeners.size(); i++) {
		((MidiDriverChangeListener) driverChangeListeners.elementAt(i)).midiDriverChanged(newDriver);
	    }
	    repackContainer();
        } catch (DriverInitializationException e) {
	    core.ErrorMsg.reportError ("Error","There was an error initializing the Midi driver!",e);
            //e.printStackTrace ();
            switchToDoNothingMidiWrapper();
            return;
	} catch (Exception e) {
	    core.ErrorMsg.reportError ("Error","There was an unspecified problem while initializing the driver!",e);
	    e.printStackTrace ();
	    switchToDoNothingMidiWrapper();
	    return;
        }
    }
    */
    /**
     * This returns the currently-selected midi wrapper. At present,
     * it's only used by the main app to obtain the midi driver after
     * the initial instantiation (ie, before it ever gets a
     * midiDriverChanged() callback).
     * @return
     */
    /*
    public MidiWrapper getMidiWrapper() {
	return(currentDriver);
    }
    */
    /**
     * This checks if the first item in the combobox is a
     * DoNothingMidiWrapper. If so, it switches to it. - emenaker
     * 2003.03.19
     *
     */
    /*
    private void switchToDoNothingMidiWrapper() {
	if(cbDriver.getItemAt(0) instanceof DoNothingMidiWrapper) {
	    cbDriver.setSelectedIndex(0);
	}
    }
    */
    private static void setComboBox(JComboBox cb, int idx) {
	try {
	    cb.setSelectedIndex(idx);
	} catch (IllegalArgumentException e) {
	}
    }
}
