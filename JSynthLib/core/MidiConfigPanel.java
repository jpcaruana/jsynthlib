
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

    JComboBox cb1 = null;
    JComboBox cb2 = null;
    JComboBox cb3 = null;
    JComboBox cbDriver = null;
    JPanel channelPanel = null;
    Vector midiImps;
    Vector driverChangeListeners;
    MidiWrapper currentDriver;

    public MidiConfigPanel(AppConfig appConfig) {
	super(appConfig);
	currentDriver = null;
	driverChangeListeners = new Vector();

	midiImps = MidiWrapper.getSuitableWrappers();

	setLayout (new core.ColumnLayout ());
        JLabel l2 = new JLabel ("MIDI Access Method:");
        add (l2);

	cbDriver=new JComboBox ();
	add (cbDriver);
	// Fill the combo box with all Midi wrappers in the midiimps vector
	for(int i=0; i<midiImps.size(); i++) {
	    cbDriver.addItem(midiImps.elementAt(i));
	}
	cbDriver.addItemListener (new ItemListener () {
		public void itemStateChanged (ItemEvent e) {
		    if (e.getStateChange ()==ItemEvent.SELECTED) {
			System.out.println("itemStateChanged");
			midiDriverSelected((MidiWrapper) ((JComboBox)e.getSource()).getSelectedItem());
		    }
		}
	    });
	channelPanel = new JPanel();
	channelPanel.setLayout(new core.ColumnLayout());
        JLabel l0=new JLabel ("Run Startup Initialization on Midi Ports:");
        channelPanel.add (l0);
        cb1 = new JComboBox ();
        cb2 = new JComboBox ();
        cb3 = new JComboBox ();
        channelPanel.add (cb1);
        channelPanel.add (cb2);

        JLabel l1=new JLabel ("Receive from Master Controller on Midi Port:");
        channelPanel.add (l1);
        channelPanel.add (cb3);
        add(channelPanel);
        JButton testButton = new JButton("Test Midi");
	testButton.addActionListener (new ActionListener () {
		public void actionPerformed (ActionEvent e) {
		    MidiTest.runLoopbackTest(currentDriver, cb2.getSelectedIndex(), cb1.getSelectedIndex());
		}
	    });
	add(testButton);


        init();
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
	return("Midi");
    }

    /**
     * This should return the AppConfig namespace to use if one isn't
     * specified in the constructor
     */
    protected final String getDefaultNamespace() {
	return("midi");
    }

    public void commitSettings() {
	appConfig.setInitPortIn(cb2.getSelectedIndex ());
	appConfig.setInitPortOut(cb1.getSelectedIndex ());
	appConfig.setMasterController(cb3.getSelectedIndex ());
	appConfig.setMidiPlatform(cbDriver.getSelectedIndex());
    }

    public void init() {
	if (PatchEdit.appConfig.getMidiPlatform()<0) PatchEdit.appConfig.setMidiPlatform(0);
	if (PatchEdit.appConfig.getInitPortIn()<0) PatchEdit.appConfig.setInitPortIn(0);
	if (PatchEdit.appConfig.getInitPortOut()<0) PatchEdit.appConfig.setInitPortOut(0);
	if (PatchEdit.appConfig.getFaderPort()<0) PatchEdit.appConfig.setFaderPort(0);
	if (PatchEdit.appConfig.getMasterController()<0) PatchEdit.appConfig.setMasterController(0);
	setComboBox(cbDriver,PatchEdit.appConfig.getMidiPlatform());
	midiDriverSelected((MidiWrapper) cbDriver.getSelectedItem());
    }

    /**
     * This clears the port-selector combo boxes and re-populates them with the
     * ports available from the current MIDI driver - emenaker 2003.03.19
     */
    private void resetPortComboBoxes() {
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
	setContainerEnabled(channelPanel,currentDriver.isReady());
    }

    /**
     * This handles the issues surrounding switching to a new
     * driver. This breaks down to several tasks:

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
    private void midiDriverSelected(MidiWrapper selectedDriver) {
	System.out.println("Something selected \""+selectedDriver+"\"");
	if(currentDriver != null) {
	    // Did they select the driver we're already using?
	    if(currentDriver.toString().equals(selectedDriver.toString())) {
		// There's already a driver in use and it's the one
		// they selected. Do nothing.
		System.out.println("We're already using that driver");
		return;
	    }
	}
	// Either the current driver is null, or they selected a
	// different driver from the current one
	if(currentDriver != null) {
	    currentDriver.close();
	}
	System.out.println("Initializing driver:"+selectedDriver.toString());
	initNewMidiDriver(selectedDriver);
	resetPortComboBoxes();
	return;
    }

    /**
     * This initializes a new midi driver. Basically, it just trys to
     * call the "init(int,int)" method on it and, provided that there
     * weren't any exceptions, switch the currentDriver variable to
     * hold the new driver object
     * - emenaker 2003.03.19
     *
     * @param newDriver The driver to initialize
     */
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

    /**
     * This returns the currently-selected midi wrapper. At present,
     * it's only used by the main app to obtain the midi driver after
     * the initial instantiation (ie, before it ever gets a
     * midiDriverChanged() callback).
     * @return
     */
    public MidiWrapper getMidiWrapper() {
	return(currentDriver);
    }

    /**
     * This checks if the first item in the combobox is a
     * DoNothingMidiWrapper. If so, it switches to it. - emenaker
     * 2003.03.19
     *
     */
    private void switchToDoNothingMidiWrapper() {
	if(cbDriver.getItemAt(0) instanceof DoNothingMidiWrapper) {
	    cbDriver.setSelectedIndex(0);
	}
    }

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
