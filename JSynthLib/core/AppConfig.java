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

import javax.swing.UIManager;

import org.jsynthlib.jsynthlib.Dummy;

final public class AppConfig {
    private static ArrayList deviceList = new ArrayList();
    private static Preferences prefs = Preferences.userNodeForPackage(Dummy.class);
    private static Preferences prefsDev = prefs.node("devices");

    /**
     * Initialize.
     */
    AppConfig () {
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
	PatchEdit.masterInEnable(masterInEnable);
        prefs.putBoolean("masterInEnable", masterInEnable);
    }

    /** Getter for masterController */
    public int getMasterController() {
	return  prefs.getInt("masterController", 0);
    }
    /** Setter for masterController */
    public void setMasterController(int masterController) {
        prefs.putInt("masterController", masterController);
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
        prefs.putInt("faderPort", faderPort);
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
	return ret;
    }

    /** Size query for deviceList */
    int deviceCount() {
	return this.deviceList.size();
    }

    /** Getter for the index of <code>device</code>. */
    int getDeviceIndex(Device device) {
	return deviceList.indexOf(device);
    }
}
