
package core; //TODO org.jsynthlib.midi;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.lang.reflect.*;
//import core.*;

/**
 * The panel that configures the MIDI layer. Taken out of PrefsDialog.
 * @author Joe Emenaker
 * @version $Id$
 */
public class MidiConfigPanel extends /* TODO org.jsynthlib.*/ConfigPanel {
    /** ComboBox for MIDI Out port. */
    private JComboBox cb1 = null;
    /** ComboBox for MIDI In port. */
    private JComboBox cb2 = null;
    /** ComboBox for MIDI In port for Master Controller. */
    private JComboBox cb3 = null;
    /** CheckBox for Master Controller */
    private JCheckBox enabledBox;
    /** ComboBox for MIDI Wrapper. */
    private JComboBox cbDriver = null;

    private JPanel channelPanel = null;
    private JPanel cbPanel = null;
    private Vector driverChangeListeners;

    public MidiConfigPanel(AppConfig appConfig) {
	super(appConfig);
	driverChangeListeners = new Vector();

	setLayout (new core.ColumnLayout ());
	setPreferredSize(new Dimension(500,250));

	// MIDI wrapper selection Combobox
        JLabel l2 = new JLabel ("MIDI Access Method:");
        add (l2);

	cbDriver=new JComboBox ();
	add (cbDriver);
	// Fill the combo box with all Midi wrappers in the midiimps vector
	Vector midiImps = appConfig.midiWrappers;
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

	// panel for other settings
	channelPanel = new JPanel();
        channelPanel.setLayout(new core.ColumnLayout());

	GridBagLayout gridbag = new GridBagLayout();
	GridBagConstraints gbc = new GridBagConstraints();
	cbPanel = new JPanel(gridbag);
	gbc.fill=gbc.HORIZONTAL;
	gbc.ipadx=1;
	gbc.anchor=gbc.WEST;


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
	JLabel cb1Label = new JLabel("Out Port:");
	gridbag.setConstraints(cb1Label, gbc);
	cbPanel.add (cb1Label);
        cb1 = new JComboBox ();
	gbc.gridx=1; gbc.gridy=4; gbc.gridheight=1; gbc.gridwidth=gbc.REMAINDER;
	gridbag.setConstraints(cb1, gbc);
        cbPanel.add (cb1);

	gbc.gridx=0; gbc.gridy=5; gbc.gridheight=1; gbc.gridwidth=1;
	JLabel cb2Label = new JLabel("In Port:");
	gridbag.setConstraints(cb2Label, gbc);
        cbPanel.add (cb2Label);
	cb2 = new JComboBox ();
	gbc.gridx=1; gbc.gridy=5; gbc.gridheight=1; gbc.gridwidth=gbc.REMAINDER;
	gridbag.setConstraints(cb2, gbc);
        cbPanel.add (cb2);

	// MIDI loopback test
	gbc.gridx=1; gbc.gridy=6; gbc.gridheight=1; gbc.gridwidth=1;gbc.fill=gbc.NONE;
        JButton testButton = new JButton("MIDI Loopback Test...");
	testButton.addActionListener (new ActionListener () {
		public void actionPerformed (ActionEvent e) {
		    MidiTest.runLoopbackTest(AppConfig.getMidiWrapper(), cb2.getSelectedIndex(), cb1.getSelectedIndex());
		}
	    });
	gridbag.setConstraints(testButton, gbc);
	cbPanel.add(testButton);

	// make a space
	gbc.gridx=0; gbc.gridy=7; gbc.gridheight=1; gbc.gridwidth=1; gbc.fill=gbc.HORIZONTAL;
	JLabel l0=new JLabel (" ");
	gridbag.setConstraints(l0, gbc);
        cbPanel.add (l0);
	// master controller selection
	gbc.gridx=0; gbc.gridy=8; gbc.gridheight=1; gbc.gridwidth=gbc.REMAINDER; // gbc.gridwidth=3;
	JLabel l1=new JLabel ("Receive from Master Controller on MIDI Port:");
	gridbag.setConstraints(l1, gbc);
        cbPanel.add (l1);

	enabledBox = new JCheckBox ("Enable Master Controller");
	gbc.gridx = 1; gbc.gridy = 9; gbc.gridwidth = 1; gbc.gridheight = 1;
	cbPanel.add(enabledBox, gbc);
	enabledBox.addActionListener(new ActionListener() {
		public void actionPerformed (ActionEvent e) {
			JCheckBox chb = (JCheckBox)e.getSource();
			cb3.setEnabled(enabledBox.isSelected());
		}
	});

	gbc.gridx=1; gbc.gridy=10; gbc.gridheight=1; gbc.gridwidth=gbc.REMAINDER;
        cb3 = new JComboBox ();
	cb3.setEnabled(enabledBox.isEnabled() && enabledBox.isSelected());
	gridbag.setConstraints(cb3, gbc);
        cbPanel.add (cb3);

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
	appConfig.setMidiPlatform(cbDriver.getSelectedIndex());

	int midiIn = cb2.getSelectedIndex();
	int midiOut = cb1.getSelectedIndex();
	int masterIn = cb3.getSelectedIndex();

	appConfig.setInitPortIn(midiIn);
	appConfig.setInitPortOut(midiOut);
	appConfig.setMasterController(masterIn);

	if (PatchEdit.newMidiAPI) {
	    JavasoundMidiWrapper jw = (JavasoundMidiWrapper) appConfig.getMidiWrapper();
	    appConfig.setMidiIn(jw.getInputDevice(midiIn));
	    appConfig.setMidiOut(jw.getOutputDevice(midiOut));
	    appConfig.setMidiMasterIn(jw.getInputDevice(masterIn));
	}
	appConfig.setMasterInEnable(enabledBox.isSelected());

	// Notify all listeners
	for(int i = 0; i < driverChangeListeners.size(); i++) {
	    ((MidiDriverChangeListener) driverChangeListeners.elementAt(i)).midiDriverChanged(AppConfig.getMidiWrapper());
	}
	resetPortComboBoxes();
	repackContainer();
    }

    public void init() {
	setComboBox(cbDriver, PatchEdit.appConfig.getMidiPlatform());
	enabledBox.setSelected(appConfig.getMasterInEnable());
	resetPortComboBoxes();
	//midiDriverSelected((MidiWrapper) cbDriver.getSelectedItem());
    }


    /**
     * This set the choosen Midi driver from the "Midi Access Method" ComboBox,
     * notify all MidiDriverChangeListener about the change and
     * reset the port-selector combo boxes of the now current Midi driver.
     */
    private void resetMidiDriver() {
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
    private void resetPortComboBoxes() {
	MidiWrapper currentDriver = appConfig.getMidiWrapper();
	cb1.removeAllItems ();
	try {
	    for (int j=0; j< currentDriver.getNumOutputDevices ();j++)
		try {
		    cb1.addItem (j+": "+ currentDriver.getOutputDeviceName (j));
		} catch (Exception e) {}
	} catch (Exception e) {}
	cb2.removeAllItems ();
	try {
	    for (int j=0; j< currentDriver.getNumInputDevices ();j++)
		try {
		    cb2.addItem (j+": "+currentDriver.getInputDeviceName (j));
		} catch (Exception e) {}
	} catch (Exception e) {}
	cb3.removeAllItems ();
	try {
	    for (int j=0; j< currentDriver.getNumInputDevices ();j++)
		try {
		    cb3.addItem (j+": "+currentDriver.getInputDeviceName (j));
		} catch (Exception e) {}
	} catch (Exception e) {}
	setComboBox(cb1, PatchEdit.appConfig.getInitPortOut());
	setComboBox(cb2, PatchEdit.appConfig.getInitPortIn());
	setComboBox(cb3, PatchEdit.appConfig.getMasterController());
	enabledBox.setSelected(PatchEdit.appConfig.getMasterInEnable());
	setContainerEnabled(channelPanel,currentDriver.isReady());
	// if cb3 is enabled/disabled depends from enabledBox
	cb3.setEnabled(enabledBox.isEnabled() && enabledBox.isSelected());
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
    /**
     * Recursively enables or disables all components in a container - emenaker 2003.03.08
     * @param container
     * @param enabled
     */
    private void setContainerEnabled(Container container, boolean enabled) {
	Component[] components = container.getComponents();
	for(int i=0; i<components.length; i++) {
	    components[i].setEnabled(enabled);
	    if(components[i] instanceof Container) {
		setContainerEnabled((Container)components[i], enabled);
	    }
	}
    }

    private void setComboBox(JComboBox cb, int idx) {
	try {
	    cb.setSelectedIndex(idx);
	} catch (IllegalArgumentException e) {
	}
    }
}
