/**
 * AppConfig.java - class to hold collect application configuration
 * variables in one place for easy saving and loading, and separation
 * of data from display code.  Persistent values are keeped by using
 * <code>java.util.prefs.Preferences</code>.
 * @author Zellyn Hunter (zellyn@zellyn.com)
 * @version $Id$
 */

package core;

//import java.io.FileNotFoundException;
//import java.io.IOException;
import java.lang.reflect.Constructor;
//import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Vector;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.sound.midi.Transmitter;
import javax.swing.UIManager;

import org.jsynthlib.jsynthlib.Dummy;

final public class AppConfig {
    private static ArrayList deviceList = new ArrayList();
    private static MidiWrapper midiWrapper = null;
    private static Transmitter masterInTrns;
    private static Transmitter faderInTrns;

    private static Preferences prefs = Preferences.userNodeForPackage(Dummy.class);
    private static Preferences prefsDev = prefs.node("devices");

    static Vector midiWrappers;
    {
	midiWrappers = MidiWrapper.getSuitableWrappers();
    }
    /**
     * Initialize.
     */
    AppConfig () {
	try {
	    prefs.sync();
	} catch (BackingStoreException e) {
	    ErrorMsg.reportStatus(e);
	}
        setMidiPlatform(getMidiPlatform());
        setLookAndFeel(getLookAndFeel());

	masterInTrns = MidiUtil.getTransmitter(getMasterController());
	faderInTrns = MidiUtil.getTransmitter(getFaderPort());
    }

    /**
     * Restore deviceList.
     */
    boolean loadPrefs() {
	try {
	    String[] devs;
	    // Some classes assume that the 1st driver is a Generic Driver.
	    if (prefsDev.nodeExists("Generic#0"))
		addDevice("synthdrivers.Generic.GenericDevice",
			  prefsDev.node("Generic#0"));
	    else
		// create for the 1st time.
		addDevice("synthdrivers.Generic.GenericDevice");

	    devs = prefsDev.childrenNames();

	    for (int i = 0; i < devs.length; i++) {
		if (devs[i].equals("Generic#0"))
		    continue;

		// get class name from preferences node name
		//ErrorMsg.reportStatus("loadDevices: \"" + devs[i] + "\"");
		String s = devs[i].substring(0, devs[i].indexOf('#'));
		//ErrorMsg.reportStatus("loadDevices: -> " + s);
		String className = PatchEdit.devConfig.classNameForShortName(s);
		//ErrorMsg.reportStatus("loadDevices: -> " + s);

		addDevice(className, prefsDev.node(devs[i]));
	    }
	    //ErrorMsg.reportStatus("deviceList: " + deviceList);
	    return true;
	} catch (BackingStoreException e) {
	    ErrorMsg.reportStatus("loadPrefs: " + e);
	    e.printStackTrace();
	    return false;
	} catch (Exception e) {
	    ErrorMsg.reportStatus("loadPrefs: " + e);
	    e.printStackTrace();
	    return false;
	}
    }

    /**
     * This routine just saves the current settings in the config
     * file. Its called when the user quits the app.
     */
    void savePrefs() {
	try {
	    // Save the appconfig
	    store();
	} catch (Exception e) {
	    ErrorMsg.reportError("Error", "Unable to Save Preferences", e);
	}
    }

    /**
     * @throws FileNotFoundException
     */
    private void store() throws BackingStoreException {
    	// This shouldn't be necessary unless the jvm crashes.
    	// Save prefs
        prefs.flush();
     }

    // Simple getters and setters

    /** Getter for libPath */
    public String getLibPath() { return prefs.get("libPath", "."); }
    /** Setter for libPath */
    public void setLibPath(String libPath) { prefs.put("libPath", libPath); }

    /** Getter for sysexPath */
    public String getSysexPath() { return prefs.get("sysexPath", "."); }
    /** Setter for sysexPath */
    public void setSysexPath(String sysexPath) {
        prefs.put("sysexPath", sysexPath);
    }

    /** Getter for note */
    public int getNote() { return  prefs.getInt("note", 0); }
    /** Setter for note */
    public void setNote(int note) { prefs.putInt("note", note); }

    /** Getter for velocity */
    public int getVelocity() { return  prefs.getInt("velocity", 0); }
    /** Setter for velocity */
    public void setVelocity(int velocity) { prefs.putInt("velocity", velocity); }

    /** Getter for delay */
    public int getDelay() { return  prefs.getInt("delay", 0); }
    /** Setter for delay */
    public void setDelay(int delay) { prefs.putInt("delay", delay); }

    /**Getter for RepositoryURL */
    public String getRepositoryURL() {
        return prefs.get("repositoryURL", "http://www.jsynthlib.org");
    }
    /**Setter for RepositoryURL */
    public void setRepositoryURL(String url) {
        prefs.put("repositoryURL", url);
    }

    /**Getter for RepositoryUser */
    public String getRepositoryUser() {
        return prefs.get("repositoryUser", "");
    }
    /**Setter for RepositoryUser */
    public void setRepositoryUser(String user) {
        prefs.put("repositoryUser", user);
    }

    /**Getter for RepositoryPass */
    public String getRepositoryPass() {
        return prefs.get("repositoryPass", "");
    }
    /**Setter for RepositoryPass */
    public void setRepositoryPass(String password) {
        prefs.put("repositoryPass", password);
    }


    /** Getter for lookAndFeel */
    public int getLookAndFeel() { return  prefs.getInt("lookAndFeel", 0); }
    /** Setter for lookAndFeel */
    public void setLookAndFeel(int lookAndFeel) {
        prefs.putInt("lookAndFeel", lookAndFeel);
        UIManager.LookAndFeelInfo [] installedLF;
        installedLF = UIManager.getInstalledLookAndFeels();
        try {
        UIManager.setLookAndFeel(installedLF[lookAndFeel].getClassName());
        } catch (Exception e) {
        ErrorMsg.reportStatus(e.toString());
        }
    }

    /** Getter for guiStyle */
    public int getGuiStyle() {
        return prefs.getInt("guiStyle", MacUtils.isMac() ? 1 : 0);
    }
    /** Setter for guiStyle */
    public void setGuiStyle(int guiStyle) {
        prefs.putInt("guiStyle", guiStyle);
    }

    /** Getter for midiPlatform */
    public int getMidiPlatform() { return  prefs.getInt("midiPlatform", 0); }

    /** Setter for midiPlatform */
    public void setMidiPlatform(int midiPlatform) {
	if (midiPlatform < 0 || PatchEdit.newMidiAPI)
	    midiPlatform = 0;
	MidiWrapper mw = (MidiWrapper) midiWrappers.elementAt(midiPlatform);
	ErrorMsg.reportStatus("Something selected \"" + mw + "\"");
	MidiWrapper currentDriver = getMidiWrapper();
	if (currentDriver != null) {
	    if (currentDriver.toString().equals(mw.toString())) {
		ErrorMsg.reportStatus("We're already using that driver");
		return;
	    }
	    currentDriver.close();
	}
	// initialize selected MIDI wrapper
	ErrorMsg.reportStatus("Initializing driver:" + mw.toString());
	try {
	    if (!PatchEdit.newMidiAPI)
		mw.init(getInitPortIn(), getInitPortOut());

	    prefs.putInt("midiPlatform", midiPlatform);
	    PatchEdit.MidiIn = PatchEdit.MidiOut = midiWrapper = mw;
	} catch (DriverInitializationException e) {
	    core.ErrorMsg.reportError
		("Error",
		 "There was an error initializing the MIDI Wrapper!", e);
	    //e.printStackTrace ();
	    setMidiEnable(false);
	    setMidiPlatform(0); // DoNothingMidiWrapper
	} catch (Exception e) {
	    core.ErrorMsg.reportError
		("Error",
		 "There was an unspecified problem while initializing the driver!", e);
	    e.printStackTrace ();
	    setMidiEnable(false);
	    setMidiPlatform(0); // DoNothingMidiWrapper
	}
    }

    /**
     * This returns the currently-selected midi wrapper. At present,
     * it's only used by the main app to obtain the midi driver after
     * the initial instantiation (ie, before it ever gets a
     * midiDriverChanged() callback).
     * @return a MidiWrapper object
     */
    public static MidiWrapper getMidiWrapper() {
	return (midiWrapper);
    }

    /** Getter for midiEnable */
    public boolean getMidiEnable() { return prefs.getBoolean("midiEnable", false); }
    /** Setter for midiEnable */
    public void setMidiEnable(boolean midiEnable) {
	prefs.putBoolean("midiEnable", midiEnable);
	ErrorMsg.reportStatus("setMidiEnable: " + midiEnable);
    }

    /** Getter for initPortIn */
    public int getInitPortIn() { return prefs.getInt("initPortIn", 0); }
    /** Setter for initPortIn */
    public void setInitPortIn(int initPortIn) {
        if (initPortIn < 0) initPortIn = 0;
        prefs.putInt("initPortIn", initPortIn);
    }

    /** Getter for initPortOut */
    public int getInitPortOut() { return  prefs.getInt("initPortOut", 0); }
    /** Setter for initPortOut */
    public void setInitPortOut(int initPortOut) {
        if (initPortOut < 0) initPortOut = 0;
        prefs.putInt("initPortOut", initPortOut);
    }

    /** Getter for masterInEnable */
    public boolean getMasterInEnable() {
	return prefs.getBoolean("masterInEnable", false);
    }
    /** Setter for masterInEnable */
    public void setMasterInEnable(boolean masterInEnable) {
        prefs.putBoolean("masterInEnable", masterInEnable);
    }

    /** Getter for masterController */
    public int getMasterController() {
	return  prefs.getInt("masterController", 0);
    }
    /** Setter for masterController */
    public void setMasterController(int masterController) {
	if (masterController < 0) masterController = 0;
	if (PatchEdit.newMidiAPI
	    && (getMasterController() != masterController)) {
	    // others may be using
	    //if (masterInTrns != null) masterInTrns.close();
	    masterInTrns = MidiUtil.getTransmitter(masterController);
	}
        prefs.putInt("masterController", masterController);
    }

    /** Return Transmitter of Master Input. */
    Transmitter getMasterInTrns() {
	return masterInTrns;
    }

    /** Getter for faderEnable */
    public boolean getFaderEnable() {
	return prefs.getBoolean("faderEnable", false);
    }
    /** Setter for faderEnable */
    public void setFaderEnable(boolean faderEnable) {
        prefs.putBoolean("faderEnable", faderEnable);
    }

    /** Getter for faderPort */
    public int getFaderPort() {
	return prefs.getInt("faderPort", 0);
    }
    /** Setter for faderPort */
    public void setFaderPort(int faderPort) {
	if (faderPort < 0) faderPort = 0;
	if (PatchEdit.newMidiAPI && (getFaderPort() != faderPort)) {
	    // others may be using (true?)
	    //if (faderInTrns != null) faderInTrns.close();
	    faderInTrns = MidiUtil.getTransmitter(getFaderPort());
	}
        prefs.putInt("faderPort", faderPort);
    }

    /** Return Transmitter of Fader Input. */
    Transmitter getFaderInTrns() {
	return faderInTrns;
    }

    //int[] faderChannel (0 <= channel < 16, 16:off)
    /** Indexed getter for faderChannel */
    public int getFaderChannel(int i) { return  prefs.getInt("faderChannel" + i, 0); }
    /** Indexed setter for faderChannel */
    public void setFaderChannel(int i, int faderChannel) {
        prefs.putInt("faderChannel" + i, faderChannel);
    }

    //int[] faderController (0 <= controller < 256, 256:off)
    public int getFaderController(int i) { return  prefs.getInt("faderController" + i, 0); }
    /** Indexed setter for faderController */
    public void setFaderController(int i, int faderController) {
        prefs.putInt("faderController" + i, faderController);
    }

    /**
     * Add Device into <code>deviceList</code>.
     *
     * @param className name of Device class
     * (ex. "synthdrivers.KawaiK4.KawaiK4Device").
     * @param prefs <code>Preferences</code> node for the Device.
     * @return a <code>Device</code> value created.
     */
    private Device addDevice(String className, Preferences prefs) {
	Device device;
	try {
	    Class c = Class.forName(className);
	    Class[] args = { Class.forName("java.util.prefs.Preferences") };
	    Constructor con = c.getConstructor(args);
	    device = (Device) con.newInstance(new Object[] { prefs });
	} catch (Exception e) {
	    ErrorMsg.reportError("Failed to create class for class",
				 "Failed to create class for class '"
				 + className + "'");
	    return null;
	}
	device.setup();
    	deviceList.add(device); // always returns true

	return device;
    }

    /**
     * Add Device into <code>deviceList</code>. A new Preferences node
     * will be created for the Device.
     *
     * @param className name of Device class
     * (ex. "synthdrivers.KawaiK4.KawaiK4Device").
     * @return a <code>Device</code> value created.
     */
    // Called by DeviceAddDialog and MidiScan.
    Device addDevice(String className) {
	return addDevice(className, getDeviceNode(className));
    }

    /** returns the 1st unused device node name. */
    private Preferences getDeviceNode(String s) {
	ErrorMsg.reportStatus("getDeviceNode: " + s);
	s = s.substring(s.lastIndexOf('.') + 1, s.lastIndexOf("Device"));
	ErrorMsg.reportStatus("getDeviceNode: -> " + s);
	int i;
	try {
	    for (i = 0; prefsDev.nodeExists(s + "#" + i); i++)
		;		// do nothing
	    return prefsDev.node(s + "#" + i);
	} catch (BackingStoreException e) {
	    ErrorMsg.reportStatus(e);
	    return null;
	}
    }

    /** Indexed getter for deviceList elements */
    Device getDevice(int i) { return (Device) this.deviceList.get(i); }

    /**
     * Remover for deviceList elements.
     * The caller must call reassignDeviceDriverNums and revalidateLibraries.
     * @return <code>Device</code> object removed.
     */
    Device removeDevice(int i) {
	Device ret = (Device) deviceList.remove(i);
	try {
	    ret.getPreferences().removeNode();
	} catch (BackingStoreException e) {
	    ErrorMsg.reportStatus(e);
	}
	/*
	int size = deviceList.size();
	while (i < size) {
	    devices.put("device" + (i), devices.get("device" + (i+1),""));
	    devices.put("node" + (i), devices.get("node" + (i+1),""));
	}
	devices.remove("device" + size);
	devices.remove("node"+ size);
	devices.putInt("count", size);
	*/
	return ret;
    }

    /** Size query for deviceList */
    int deviceCount() {
	return this.deviceList.size();
    }

    /*
    private Preferences getPreferences(int i) {
	String key = devices.get("node" + i,null);
	if (key == null)
	    return null;
	return devices.node(key);
    }
    */
    /*
    private String getNextDeviceNode() {
	int i = devices.getInt("next_node", 0);
	devices.putInt("next_node", i + 1);
	return Integer.toString(i);
    }
    */

    /** Getter for the index of <code>device</code>. */
    int getDeviceIndex(Device device) {
	return deviceList.indexOf(device);
    }

    /** Getter/setter for MidiDevice.info */
    // These are experimental code.
    //public JSLMidiDevice getMidiIn() { return midiIn; }
    //public JSLMidiDevice getMidiOut() { return midiOut; }
    //public JSLMidiDevice getMidiMasterIn() { return midiMasterIn; }
    //public JSLMidiDevice getMidiFaderIn() { return midiFaderIn; }
    //public void setMidiIn(JSLMidiDevice midiIn) { this.midiIn = midiIn; }
    //public void setMidiOut(JSLMidiDevice midiOut) { this.midiOut = midiOut; }
    //public void setMidiMasterIn(JSLMidiDevice midiMasterIn) { this.midiMasterIn = midiMasterIn; }
    //public void setMidiFaderIn(JSLMidiDevice midiFaderIn) { this.midiFaderIn = midiFaderIn; }

    // Shall we define JSLUtil for these mothods?
    // Returns the "os.name" system property - emenaker 2003.03.13
    public static String getOSName() {
	return (getSystemProperty("os.name"));
    }

    // Returns the "java.specification.version" system property - emenaker 2003.03.13
    public static String getJavaSpecVersion() {
	return (getSystemProperty("java.specification.version"));
    }

    // Looks up a system property and returns "" on exceptions
    private static String getSystemProperty(String key) {
	try {
	    return (System.getProperty(key));
	} catch (Exception e) {
	    ErrorMsg.reportStatus(e);
	}
	return ("");
    }
}
