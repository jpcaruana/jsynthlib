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
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.UIManager;

import org.jsynthlib.jsynthlib.Dummy;

final class AppConfig {
    private static ArrayList deviceList = new ArrayList();
    private static Preferences prefs = Preferences.userNodeForPackage(Dummy.class);
    private static Preferences prefsDev = prefs.node("devices");

    /**
     * Initialize.
     */
    static {
	try {
	    prefs.sync();
	} catch (BackingStoreException e) {
	    ErrorMsg.reportStatus(e);
	}
        setLookAndFeel(getLookAndFeel());
    }

    /**
     * Restore deviceList.
     */
    static boolean loadPrefs() {
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
    static void savePrefs() {
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
    private static void store() throws BackingStoreException {
    	// This shouldn't be necessary unless the jvm crashes.
    	// Save prefs
        prefs.flush();
     }

    // Simple getters and setters

    /** Getter for libPath */
    static String getLibPath() { return prefs.get("libPath", "."); }
    /** Setter for libPath */
    static void setLibPath(String libPath) { prefs.put("libPath", libPath); }

    /** Getter for libPath */
    static String getXMLpaths() { return prefs.get("XMLpaths", ""); }
    /** Setter for libPath */
    static void setXMLpaths(String libPath) { prefs.put("XMLpaths", libPath); }

    /** Getter for sysexPath */
    static String getSysexPath() { return prefs.get("sysexPath", "."); }
    /** Setter for sysexPath */
    static void setSysexPath(String sysexPath) {
        prefs.put("sysexPath", sysexPath);
    }


    /** Getter for sequencerEnable */
    static boolean getSequencerEnable() { return prefs.getBoolean("sequencerEnable",false); }
    /** Setter for sequencerEnable */
    static void setSequencerEnable(boolean sequencerEnable) { prefs.putBoolean("sequencerEnable", sequencerEnable); }
    
    /** Getter for midi file (Sequence) to play */
    static String getSequencePath() { return prefs.get("sequencePath", ""); }
    /** Setter for midi file (Sequence) to play */
    static void setSequencePath(String sequencePath) { prefs.put("sequencePath", sequencePath); }
    
    /** Getter for note */
    static int getNote() { return  prefs.getInt("note", 0); }
    /** Setter for note */
    static void setNote(int note) { prefs.putInt("note", note); }

    /** Getter for velocity */
    static int getVelocity() { return  prefs.getInt("velocity", 0); }
    /** Setter for velocity */
    static void setVelocity(int velocity) { prefs.putInt("velocity", velocity); }

    /** Getter for delay */
    static int getDelay() { return  prefs.getInt("delay", 0); }
    /** Setter for delay */
    static void setDelay(int delay) { prefs.putInt("delay", delay); }

    /**Getter for RepositoryURL */
    static String getRepositoryURL() {
        return prefs.get("repositoryURL", "http://www.jsynthlib.org");
    }
    /**Setter for RepositoryURL */
    static void setRepositoryURL(String url) {
        prefs.put("repositoryURL", url);
    }

    /**Getter for RepositoryUser */
    static String getRepositoryUser() {
        return prefs.get("repositoryUser", "");
    }
    /**Setter for RepositoryUser */
    static void setRepositoryUser(String user) {
        prefs.put("repositoryUser", user);
    }

    /**Getter for RepositoryPass */
    static String getRepositoryPass() {
        return prefs.get("repositoryPass", "");
    }
    /**Setter for RepositoryPass */
    static void setRepositoryPass(String password) {
        prefs.put("repositoryPass", password);
    }


    /** Getter for lookAndFeel */
    static int getLookAndFeel() { return  prefs.getInt("lookAndFeel", 0); }

    /** Setter for lookAndFeel */
    static void setLookAndFeel(int lookAndFeel) {
        prefs.putInt("lookAndFeel", lookAndFeel);
        UIManager.LookAndFeelInfo [] installedLF;
        installedLF = UIManager.getInstalledLookAndFeels();
        try {
	    UIManager.setLookAndFeel(installedLF[lookAndFeel].getClassName());
        } catch (Exception e) {
	    ErrorMsg.reportStatus(e);
        }
    }

    /** Getter for guiStyle */
    static int getGuiStyle() {
        return prefs.getInt("guiStyle", MacUtils.isMac() ? 1 : 0);
    }
    /** Setter for guiStyle */
    static void setGuiStyle(int guiStyle) {
        prefs.putInt("guiStyle", guiStyle);
    }

    /**
     * Getter for midiEnable. Returns false if either MIDI input nor
     * output is not available.
     */
    static boolean getMidiEnable() {
	return ((MidiUtil.isOutputAvailable() || MidiUtil.isInputAvailable())
		&& prefs.getBoolean("midiEnable", false));
    }
    /** Setter for midiEnable */
    static void setMidiEnable(boolean midiEnable) {
	prefs.putBoolean("midiEnable", midiEnable);
	//ErrorMsg.reportStatus("setMidiEnable: " + midiEnable);
    }

    /** Getter for initPortIn */
    static int getInitPortIn() { return prefs.getInt("initPortIn", 0); }
    /** Setter for initPortIn */
    static void setInitPortIn(int initPortIn) {
        if (initPortIn < 0) initPortIn = 0;
        prefs.putInt("initPortIn", initPortIn);
    }

    /** Getter for initPortOut */
    static int getInitPortOut() { return  prefs.getInt("initPortOut", 0); }
    /** Setter for initPortOut */
    static void setInitPortOut(int initPortOut) {
        if (initPortOut < 0) initPortOut = 0;
        prefs.putInt("initPortOut", initPortOut);
    }

    /**
     * Getter for masterInEnable. Returns false if either MIDI input or
     * output is unavailable.
     */
    static boolean getMasterInEnable() {
	return (MidiUtil.isOutputAvailable() && MidiUtil.isInputAvailable()
		&& getMidiEnable()
		&& prefs.getBoolean("masterInEnable", false));
    }
    /** Setter for masterInEnable */
    static void setMasterInEnable(boolean masterInEnable) {
	PatchEdit.masterInEnable(masterInEnable);
        prefs.putBoolean("masterInEnable", masterInEnable);
    }

    /** Getter for masterController */
    static int getMasterController() {
	return  prefs.getInt("masterController", 0);
    }
    /** Setter for masterController */
    static void setMasterController(int masterController) {
        prefs.putInt("masterController", masterController);
    }

    /**
     * Getter for faderEnable. Returns false if MIDI input is
     * unavailable.
     */
    static boolean getFaderEnable() {
	return (MidiUtil.isOutputAvailable()
		&& getMidiEnable()
		&& prefs.getBoolean("faderEnable", false));
    }
    /** Setter for faderEnable */
    static void setFaderEnable(boolean faderEnable) {
        prefs.putBoolean("faderEnable", faderEnable);
    }

    /** Getter for faderPort */
    static int getFaderPort() {
	return prefs.getInt("faderPort", 0);
    }
    /** Setter for faderPort */
    static void setFaderPort(int faderPort) {
        prefs.putInt("faderPort", faderPort);
    }

    //int[] faderChannel (0 <= channel < 16, 16:off)
    /** Indexed getter for fader Channel number */
    static int getFaderChannel(int i) {
	return  prefs.getInt("faderChannel" + i, 0);
    }
    /** Indexed setter for fader Channel number */
    static void setFaderChannel(int i, int faderChannel) {
        prefs.putInt("faderChannel" + i, faderChannel);
    }

    //int[] faderControl (0 <= controller < 120, 120:off)
    /** Indexed getter for fader Control number */
    static int getFaderControl(int i) {
	int n = prefs.getInt("faderControl" + i, 0);
	return n > 120 ? 120 : n; // for old JSynthLib bug
    }
    /** Indexed setter for fader Control number. */
    static void setFaderControl(int i, int faderControl) {
        prefs.putInt("faderControl" + i, faderControl);
    }

    /** Getter for Multiple MIDI Interface enable */
    static boolean getMultiMIDI() {
	return prefs.getBoolean("multiMIDI", false);
    }
    /** Setter for midiEnable */
    static void setMultiMIDI(boolean enable) {
	prefs.putBoolean("multiMIDI", enable);
    }

    /**
     * Add Device into <code>deviceList</code>.
     *
     * @param className name of Device class
     * (ex. "synthdrivers.KawaiK4.KawaiK4Device").
     * @param prefs <code>Preferences</code> node for the Device.
     * @return a <code>Device</code> value created.
     */
    private static Device addDevice(String className, Preferences prefs) {
    	Device device = PatchEdit.devConfig.createDevice(className, prefs);
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
    static Device addDevice(String className) {
	return addDevice(className, getDeviceNode(className));
    }

    /** returns the 1st unused device node name for Preferences. */
    private static Preferences getDeviceNode(String s) {
	ErrorMsg.reportStatus("getDeviceNode: " + s);
	try {
		s = s.substring(s.lastIndexOf('.') + 1, s.lastIndexOf("Device"));
	} catch (StringIndexOutOfBoundsException ex) {}
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
    static Device getDevice(int i) { return (Device) deviceList.get(i); }

    /**
     * Remover for deviceList elements.
     * The caller must call reassignDeviceDriverNums and revalidateLibraries.
     * @return <code>Device</code> object removed.
     */
    static Device removeDevice(int i) {
	Device ret = (Device) deviceList.remove(i);
	try {
	    ret.getPreferences().removeNode();
	} catch (BackingStoreException e) {
	    ErrorMsg.reportStatus(e);
	}
	return ret;
    }

    /** Size query for deviceList */
    static int deviceCount() {
	return deviceList.size();
    }

    /** Getter for the index of <code>device</code>. */
    static int getDeviceIndex(Device device) {
	return deviceList.indexOf(device);
    }

    /**
     * Returns null driver of Generic Device.  It is used when proper
     * driver is not found.
     */
    static IPatchDriver getNullDriver() {
	return (IPatchDriver) getDevice(0).getDriver(0);
    }
}
