/**
 * AppConfig.java - class to hold collect application configuration variables
 * in one place for easy saving and loading, and separation of data from
 * display code.  Implements the Storable interface, so it can be easily
 * persisted to a Properties file.
 * @author Zellyn Hunter (zellyn@zellyn.com)
 * @version $Id$
 */

package core;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Vector;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.sound.midi.Transmitter;
import javax.swing.UIManager;

import org.jsynthlib.jsynthlib.Dummy;

public class AppConfig {
	// static?
    private ArrayList deviceList = new ArrayList();
    private static MidiWrapper midiWrapper = null;
    private Transmitter masterInTrns;
    private Transmitter faderInTrns;

    
    private static Preferences prefs = Preferences.userNodeForPackage(Dummy.class);
    private static Preferences devices = prefs.node("devices");

    static Vector midiWrappers;
    {
	midiWrappers = MidiWrapper.getSuitableWrappers();
    }
    /**
     * This one loads the settings from the config file.
     */
    boolean loadPrefs() {
	try {
	    // Load the appconfig
	    load();
	    return true;
	} catch (Exception e) {
	    ErrorMsg.reportStatus("loadPrefs: " + e);
	    e.printStackTrace();
	    return false;
	} finally {
	    if (deviceCount() == 0) {
		addDevice(new synthdrivers.Generic.GenericDevice());
	    }
	}
    }

    /**
     * Load the properties from the file.
     * @throws IOException
     */
    private void load() throws IOException, BackingStoreException, NoSuchMethodException,
		IllegalAccessException,	InstantiationException, InvocationTargetException {
        prefs.sync();
        // Call setters that act on the value.
        setMidiPlatform(getMidiPlatform());
        setLookAndFeel(getLookAndFeel());
        loadDevices();
        try {
        	    masterInTrns = MidiUtil.getTransmitter(getFaderPort());
            faderInTrns = MidiUtil.getTransmitter(getFaderPort());
        } catch (Exception e) {}
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
     * Save the properties to the file
     * @throws IOException
     * @throws FileNotFoundException
     */
    private void store() throws IOException, BackingStoreException {
    	// This shouldn't be necessary unless the jvm crashes.
    	// Save prefs
        prefs.flush();
     }

    // Simple getters and setters

    /** Getter for libPath */
    public String getLibPath() { return prefs.get("libPath", "."); }
    /** Setter for libPath */
    public void setLibPath(String libPath) {prefs.put("libPath",libPath);}

    /** Getter for sysexPath */
    public String getSysexPath() { return prefs.get("sysexPath","."); }
    /** Setter for sysexPath */
    public void setSysexPath(String sysexPath) { 
        prefs.put("sysexPath", sysexPath);
    }

    /** Getter for note */
    public int getNote() { return  prefs.getInt("note",0); }
    /** Setter for note */
    public void setNote(int note) { prefs.putInt("note", note); }

    /** Getter for velocity */
    public int getVelocity() { return  prefs.getInt("velocity",0); }
    /** Setter for velocity */
    public void setVelocity(int velocity) { prefs.putInt("velocity", velocity); }

    /** Getter for delay */
    public int getDelay() { return  prefs.getInt("delay",0); }
    /** Setter for delay */
    public void setDelay(int delay) { prefs.putInt("delay", delay); }

    /**Getter for RepositoryURL */
    public String getRepositoryURL() {
        return prefs.get("repositoryURL","http://www.jsynthlib.org");
    }
    /**Setter for RepositoryURL */
    public void setRepositoryURL(String url) {
        prefs.put("repositoryURL",url);
    }
    
    /**Getter for RepositoryUser */
    public String getRepositoryUser() {
        return prefs.get("repositoryUser","");
    }
    /**Setter for RepositoryUser */
    public void setRepositoryUser(String user) {
        prefs.put("repositoryUser",user);
    }
    
    /**Getter for RepositoryPass */
    public String getRepositoryPass() {
        return prefs.get("repositoryPass","");
    }
    /**Setter for RepositoryPass */
    public void setRepositoryPass(String Pass) {
        prefs.put("repositoryPass",Pass);
    }


    /** Getter for lookAndFeel */
    public int getLookAndFeel() { return  prefs.getInt("lookAndFeel",0); }
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
        return prefs.getInt("guiStyle",MacUtils.isMac() ? 1 : 0);
    }
    /** Setter for guiStyle */
    public void setGuiStyle(int guiStyle) { 
        prefs.putInt("guiStyle", guiStyle);
    }

    /** Getter for midiPlatform */
    public int getMidiPlatform() { return  prefs.getInt("midiPlatform",0); }

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
    public boolean getMidiEnable() { return prefs.getBoolean("midiEnable",false); }
    /** Setter for midiEnable */
    public void setMidiEnable(boolean midiEnable) {
	prefs.putBoolean("midiEnable",midiEnable);
	ErrorMsg.reportStatus("setMidiEnable: " + midiEnable);
    }

    /** Getter for initPortIn */
    public int getInitPortIn() { return prefs.getInt("initPortIn",0); }
    /** Setter for initPortIn */
    public void setInitPortIn(int initPortIn) {
        if (initPortIn < 0) initPortIn = 0;
        prefs.putInt("initPortIn", initPortIn);
    }

    /** Getter for initPortOut */
    public int getInitPortOut() { return  prefs.getInt("initPortOut",0); }
    /** Setter for initPortOut */
    public void setInitPortOut(int initPortOut) {
        if (initPortOut < 0) initPortOut = 0;
        prefs.putInt("initPortOut", initPortOut);
    }

    /** Getter for masterInEnable */
    public boolean getMasterInEnable() { return prefs.getBoolean("masterInEnable", false); }
    /** Setter for masterInEnable */
    public void setMasterInEnable(boolean masterInEnable) { 
        prefs.putBoolean("masterInEnable", masterInEnable);
    }

    /** Getter for masterController */
    public int getMasterController() { return  prefs.getInt("masterController",0); }
    /** Setter for masterController */
    public void setMasterController(int masterController) {
	boolean changed = getMasterController() != masterController;
	if (masterController < 0) masterController = 0;
	if (PatchEdit.newMidiAPI && changed) {
	    // others may be using
	    //if (masterInTrns != null) masterInTrns.close();
	    masterInTrns = MidiUtil.getTransmitter(getFaderPort());
	}
        prefs.putInt("masterController", masterController);
    }

    Transmitter getMasterInTrns() {
	return masterInTrns;
    }

    /** Getter for faderEnable */
    public boolean getFaderEnable() { return prefs.getBoolean("faderEnable", false); }
    /** Setter for faderEnable */
    public void setFaderEnable(boolean faderEnable) { 
        prefs.putBoolean("faderEnable", faderEnable);
    }

    /** Getter for faderPort */
    public int getFaderPort() { return prefs.getInt("faderPort",0); }
    /** Setter for faderPort */
    public void setFaderPort(int faderPort) {
	boolean changed = getFaderPort() != faderPort;
	if (faderPort < 0) faderPort = 0;
	if (PatchEdit.newMidiAPI && changed) {
	    // others may be using (true?)
	    //if (faderInTrns != null) faderInTrns.close();
	    faderInTrns = MidiUtil.getTransmitter(getFaderPort());
	}
        prefs.putInt("faderPort", faderPort);
    }

    Transmitter getFaderInTrns() {
	return faderInTrns;
    }

    //int[] faderChannel (0 <= channel < 16, 16:off)
    /** Indexed getter for faderChannel */
    public int getFaderChannel(int i) { return  prefs.getInt("faderChannel"+i,0); }
    /** Indexed setter for faderChannel */
    public void setFaderChannel(int i, int faderChannel) { 
        prefs.putInt("faderChannel"+i, faderChannel);
    }

    //int[] faderController (0 <= controller < 256, 256:off)
    public int getFaderController(int i) { return  prefs.getInt("faderController"+i,0); }
    /** Indexed setter for faderController */
    public void setFaderController(int i, int faderController) { 
        prefs.putInt("faderController"+i, faderController);
    }

    // Standard getters/setters
    /** Indexed getter for deviceList elements */
    public Device getDevice(int i) { return (Device) this.deviceList.get(i); }
    /** Indexed setter for deviceList elements */

    /**
     * Adder for deviceList elements.  Called by DeviceAddDialog and
     * MidiScan.
     */
    public boolean addDevice(Device device) {
	//reassignDeviceDriverNums(deviceList.size(), device);
	// set default MIDI in/out port number
    	if (this.deviceList.add(device)) {
    		int i = deviceList.size() - 1;
    		device.setPort(PatchEdit.appConfig.getInitPortOut());
    		device.setInPort(PatchEdit.appConfig.getInitPortIn());
    		devices.put("device" + i, device.getClass().getName());
    		devices.put("node" + i, getNextDeviceNode());
    		device.setPreferences(getPreferences(i));
    		devices.putInt("count", deviceList.size());
    		return true; 
    	}
    	return false;
    }
    /**
     * Remover for deviceList elements.
     * The caller must call reassignDeviceDriverNums and revalidateLibraries.
     * @return <code>Device</code> object removed.
     */
    public Device removeDevice(int i) {
    		Device ret = (Device) this.deviceList.remove(i);
    		int size = deviceList.size();
    		try {
    			getPreferences(i).removeNode();
    		} catch (BackingStoreException ex) {}
    		while (i < size) {
    			devices.put("device" + (i), devices.get("device" + (i+1),""));
    			devices.put("node" + (i), devices.get("node" + (i+1),""));
    		}
    		devices.remove("device" + size);
    		devices.remove("node"+ size);
    		devices.putInt("count", size);
    		return ret;
    }
    /** Size query for deviceList */
    public int deviceCount() { return this.deviceList.size(); }

    	private Preferences getPreferences(int i) {
    		String key = devices.get("node" + i,null);
    		if (key == null)
    			return null;
    		return devices.node(key);
    	}
    
    	private void loadDevices() throws NoSuchMethodException, IllegalAccessException,
			InstantiationException, InvocationTargetException {
    		int size = devices.getInt("count",0);
    		Class args[] = new Class[0];
    		for (int i = 0; i < size; i++) {
    			try {
    				Class c = Class.forName(prefs.get("device" + (i),""));
    				Constructor cons = c.getConstructor(args);
    				Device dev = (Device)cons.newInstance(args);
    				dev.setPreferences(getPreferences(i));
    				deviceList.add(i, dev);
    			} catch (ClassNotFoundException ex) {
    			}
    		}
    	}
    	
    	private String getNextDeviceNode() {
    		int i = devices.getInt("next_node", 0);
    		devices.putInt("next_node", i + 1);
    		return Integer.toString(i);
    	}
    
    // Moved from SynthConfigDialog.java
    /** Revalidate deviceNum element of drivers of each device */
    /*
    public void reassignDeviceDriverNums() {
	for (int i = 0; i < deviceList.size(); i++) {
	    Device dev = (Device) deviceList.get(i);
	    reassignDeviceDriverNums(i, dev);
	}
    }
    */
    /** Revalidate deviceNum element of drivers of a device */
    // Only for backward compatibility.  Remove this when no
    // driver uses deviceNum and driverNum.
    /*
    private void reassignDeviceDriverNums(int i, Device dev) {
	for (int j = 0; j < dev.driverList.size(); j++) {
	    Driver drv = (Driver) dev.driverList.get(j);
	    drv.setDeviceNum(i);
	    drv.setDriverNum(j);
	}
    }
    */
    /** Getter for the index of <code>device</code>. */
    int getDeviceIndex(Device device) {
	return deviceList.indexOf(device);
	/*
	Iterator it = deviceList.iterator();
	for (int i = 0; it.hasNext(); i++) {
	    if ((Device) it.next() == device)
		return i;
	}
	return -1;		// throw error !!!FIXIT!!!
	*/
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
