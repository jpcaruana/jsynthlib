/**
 * AppConfig.java - class to hold collect application configuration variables
 * in one place for easy saving and loading, and separation of data from
 * display code.  Implements the Storable interface, so it can be easily
 * persisted to a Properties file.
 * @author Zellyn Hunter (zellyn@zellyn.com)
 * @version $Id$
 */

package core;

import java.util.Properties;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;
//import java.util.Collection;
import java.util.Iterator;
import java.util.Arrays;

import java.io.InputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import javax.swing.UIManager;

public class AppConfig implements Storable {

	/* Configurable properties */
	private String libPath           = "."; // so both properties are defaulted to the startup
	private String sysexPath         = "."; // directory and doesn't generate a NPE when quitting
                                                // the application, if you didn't change them.
   	private int initPortIn           = 0;
   	private int initPortOut          = 0;
   	private int note                 = 0;
   	private int velocity             = 0;
   	private int delay                = 0;
   	private int masterController     = 0;
   	private int lookAndFeel          = 0;
	private int guiStyle		 = MacUtils.isMac() ? 1 : 0;
   	private int midiPlatform	 = 0;
   	private boolean masterInEnable   = false;
   	private int faderPort            = 0;
   	private boolean faderEnable      = false;
   	private int[] faderController    = new int[Constants.NUM_FADERS];
   	private int[] faderChannel       = new int[Constants.NUM_FADERS];

	private String repositoryURL     = "http://www.jsynthlib.org";
	private String repositoryUser    = "";
	private String repositoryPass    = "";

	private ArrayList deviceList = new ArrayList();

	/** MidiDevice.Info.name|vendor */
	private JSLMidiDevice midiIn;
	private JSLMidiDevice midiOut;
	private JSLMidiDevice midiMasterIn;
	private JSLMidiDevice midiFaderIn;
	private static MidiWrapper midiWrapper	 = null;

	static Vector midiWrappers;
	{
		midiWrappers = MidiWrapper.getSuitableWrappers();
	}

	/**
	 * Constructor
	 */
	public AppConfig() {
	}

	/**
	 * This one loads the settings from the config file.
	 */
	boolean loadPrefs() {
		/*
		 * These are handled by the individual ConfigPanels now - emenaker 2003.03.25
		 appConfig.setInitPortIn(0);
		 appConfig.setInitPortOut(0);
		 appConfig.setLibPath("");
		 appConfig.setSysexPath("");
		 appConfig.setNote(60);
		 appConfig.setVelocity(100);
		 appConfig.setDelay(500);
		*/

		try {
			// Load the appconfig
			load();
			return true;
		} catch (FileNotFoundException e) {
		    // When JSynthLib.properties does not exist (the very
		    // first invoke), setMidiPlafform is not called.
		    if (midiWrapper == null) // probably 'true'
			setMidiPlatform(0); // DoNothingMidiWrapper
		    return false;
		} catch (Exception e) {
		    ErrorMsg.reportStatus("loadPrefs: " + e);
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
	private void load() throws IOException
	{
		// Load in the properties file
		Properties props = new Properties();
		InputStream in = new FileInputStream(Constants.FILE_NAME_APP_CONFIG);
		props.load(in);

		// Use the properties object to restore the configuration
		Storage.restore(this, props);
		ErrorMsg.reportStatus("Config Loaded");
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
	private void store() throws IOException, FileNotFoundException
	{
		// Store the current configuration in a properties object
		Properties props = new Properties();
		Storage.store(this, props);

		// Store the properties object to a properties file
		OutputStream out = new FileOutputStream(Constants.FILE_NAME_APP_CONFIG);
		props.store(out, Constants.APP_CONFIG_HEADER);
	}

	// Simple getters and setters

	/** Getter for libPath */
	public String getLibPath() { return this.libPath; };
	/** Setter for libPath */
	public void setLibPath(String libPath) { this.libPath = libPath; };

	/** Getter for sysexPath */
	public String getSysexPath() { return this.sysexPath; };
	/** Setter for sysexPath */
	public void setSysexPath(String sysexPath) { this.sysexPath = sysexPath; };

	/** Getter for initPortIn */
	public int getInitPortIn() { return this.initPortIn; };
	/** Setter for initPortIn */
	public void setInitPortIn(int initPortIn) {
	    if (initPortIn < 0) initPortIn = 0;
	    this.initPortIn = initPortIn;
	};

	/** Getter for initPortOut */
	public int getInitPortOut() { return this.initPortOut; };
	/** Setter for initPortOut */
	public void setInitPortOut(int initPortOut) {
	    if (initPortOut < 0) initPortOut = 0;
	    this.initPortOut = initPortOut;
	};

	/** Getter for note */
	public int getNote() { return this.note; };
	/** Setter for note */
	public void setNote(int note) { this.note = note; };

	/** Getter for velocity */
	public int getVelocity() { return this.velocity; };
	/** Setter for velocity */
	public void setVelocity(int velocity) { this.velocity = velocity; };

	/** Getter for delay */
	public int getDelay() { return this.delay; };
	/** Setter for delay */
	public void setDelay(int delay) { this.delay = delay; };

	/** Getter for masterController */
	public int getMasterController() { return this.masterController; };
	/** Setter for masterController */
	public void setMasterController(int masterController) {
	    if (masterController < 0) masterController = 0;
	    this.masterController = masterController;
	};

	
	/**Getter for RepositoryURL */
	public String getRepositoryURL() {return this.repositoryURL;};
	/**Setter for RepositoryURL */
	public void setRepositoryURL(String url) {this.repositoryURL=url;};
	
	/**Getter for RepositoryUser */
	public String getRepositoryUser() {return this.repositoryUser;};
	/**Setter for RepositoryUser */
	public void setRepositoryUser(String user) {this.repositoryUser=user;};
	
	/**Getter for RepositoryPass */
	public String getRepositoryPass() {return this.repositoryPass;};
	/**Setter for RepositoryPass */
	public void setRepositoryPass(String Pass) {this.repositoryPass=Pass;};
	
	
	/** Getter for lookAndFeel */
	public int getLookAndFeel() { return this.lookAndFeel; };
	/** Setter for lookAndFeel */
	public void setLookAndFeel(int lookAndFeel) {
	    this.lookAndFeel = lookAndFeel;
	    UIManager.LookAndFeelInfo [] installedLF;
	    installedLF = UIManager.getInstalledLookAndFeels();
	    try {
		UIManager.setLookAndFeel(installedLF[lookAndFeel].getClassName());
	    } catch (Exception e) {
		ErrorMsg.reportStatus(e.toString());
	    }
	};

	/** Getter for guiStyle */
	public int getGuiStyle() { return this.guiStyle; };
	/** Setter for guiStyle */
	public void setGuiStyle(int guiStyle) { this.guiStyle = guiStyle; };

	/** Getter for midiPlatform */
	public int getMidiPlatform() { return this.midiPlatform; };

	/** Setter for midiPlatform */
	public void setMidiPlatform(int midiPlatform) {
	    if (midiPlatform < 0) midiPlatform = 0;
	    MidiWrapper mw = (MidiWrapper) midiWrappers.elementAt(midiPlatform);
	    ErrorMsg.reportStatus("Something selected \"" + mw + "\"");
	    MidiWrapper currentDriver = getMidiWrapper();
	    if(currentDriver != null) {
		if(currentDriver.toString().equals(mw.toString())) {
		    ErrorMsg.reportStatus("We're already using that driver");
		    return;
		}
		currentDriver.close();
	    }
	    // initialize selected MIDI wrapper
	    ErrorMsg.reportStatus("Initializing driver:" + mw.toString());
	    try {
		mw.init(getInitPortIn(), getInitPortOut());

		this.midiPlatform = midiPlatform;
		PatchEdit.MidiIn = PatchEdit.MidiOut = midiWrapper = mw;
	    } catch (DriverInitializationException e) {
		core.ErrorMsg.reportError
		    ("Error",
		     "There was an error initializing the MIDI Wrapper!", e);
		//e.printStackTrace ();
		setMidiPlatform(0); // DoNothingMidiWrapper
	    } catch (Exception e) {
		core.ErrorMsg.reportError
		    ("Error",
		     "There was an unspecified problem while initializing the driver!", e);
		e.printStackTrace ();
		setMidiPlatform(0); // DoNothingMidiWrapper
	    }
	}

	/**
	 * This returns the currently-selected midi wrapper. At present,
	 * it's only used by the main app to obtain the midi driver after
	 * the initial instantiation (ie, before it ever gets a
	 * midiDriverChanged() callback).
	 * @return
	 */
	public static MidiWrapper getMidiWrapper() {
		return(midiWrapper);
	}

	/** Getter for masterInEnable */
	public boolean getMasterInEnable() { return this.masterInEnable; };
	/** Setter for masterInEnable */
	public void setMasterInEnable(boolean masterInEnable) { this.masterInEnable = masterInEnable; };

	/** Getter for faderPort */
	public int getFaderPort() { return this.faderPort; };
	/** Setter for faderPort */
	public void setFaderPort(int faderPort) {
	    if (faderPort < 0) faderPort = 0;
	    this.faderPort = faderPort;
	};

	/** Getter for faderEnable */
	public boolean getFaderEnable() { return this.faderEnable; };
	/** Setter for faderEnable */
	public void setFaderEnable(boolean faderEnable) { this.faderEnable = faderEnable; };

   	//int[] faderController
	/** Indexed getter for faderController */
	public int getFaderController(int i) { return this.faderController[i]; };
	/** Indexed setter for faderController */
	public void setFaderController(int i, int faderController) { this.faderController[i] = faderController; };
	/** Getter for faderController */
	public int[] getFaderController() { return this.faderController; };
	/** Setter for faderController */
	public void setFaderController(int[] newFaderController) {
		this.faderController = newFaderController; };

   	//int[] faderChannel
	/** Indexed getter for faderChannel */
	public int getFaderChannel(int i) { return this.faderChannel[i]; };
	/** Indexed setter for faderChannel */
	public void setFaderChannel(int i, int faderChannel) { this.faderChannel[i] = faderChannel; };
	/** Getter for faderChannel */
	public int[] getFaderChannel() { return this.faderChannel; };
	/** Setter for faderChannel */
	public void setFaderChannel(int[] newFaderChannel) {
		this.faderChannel = newFaderChannel; };

	// Standard getters/setters
	/** Indexed getter for deviceList elements */
	public Device getDevice(int i) { return (Device)this.deviceList.get(i); };
	/** Indexed setter for deviceList elements */
	public Device setDevice(int i, Device dev) {
		reassignDeviceDriverNums(i, dev);
		return (Device)this.deviceList.set(i, dev);
	};
	/** Getter for deviceList */
	public Device[] getDevice() {
		return (Device[])this.deviceList.toArray(new Device[0]);
	};
	/** setter for deviceList */
	public void setDevice(Device[] devices) {
		ArrayList newList = new ArrayList();
		newList.addAll(Arrays.asList(devices));
		this.deviceList = newList;
		reassignDeviceDriverNums();
	}

	/** Adder for deviceList elements */
	public boolean addDevice(Device device) {
	    reassignDeviceDriverNums(deviceList.size(), device);
	    return this.deviceList.add(device);
	}
	/**
	 * Remover for deviceList elements.
	 * The caller must call reassignDeviceDriverNums and revalidateLibraries.
	 * @return <code>Device</code> object removed.
	 */
	public Device removeDevice(int i) { return (Device)this.deviceList.remove(i); };
	/** Size query for deviceList */
	public int deviceCount() { return this.deviceList.size(); };

	// Moved from SynthConfigDialog.java
	/** Revalidate deviceNum element of drivers of each device */
        public void reassignDeviceDriverNums() {
	    for (int i = 0; i < deviceList.size(); i++) {
		Device dev = (Device) deviceList.get(i);
		reassignDeviceDriverNums(i, dev);
	    }
	}

	/** Revalidate deviceNum element of drivers of a device */
	// Only for backward compatibility.  Remove this when no
	// driver uses deviceNum and driverNum.
        private void reassignDeviceDriverNums(int i, Device dev) {
	    for (int j = 0; j < dev.driverList.size(); j++) {
		Driver drv = (Driver) dev.driverList.get(j);
		drv.setDeviceNum(i);
		drv.setDriverNum(j);
	    }
	}

	/** Getter for the index of <code>device</code>. */
    	int getDeviceIndex(Device device) {
 	    return deviceList.indexOf(device);
// 	    Iterator it = deviceList.iterator();
// 	    for (int i = 0; it.hasNext(); i++) {
// 		if ((Device) it.next() == device)
// 		    return i;
// 	    }
// 	    return -1;		// throw error !!!FIXIT!!!
	}

	/** Getter/setter for MidiDevice.info */
	// These are experimental code.
	public JSLMidiDevice getMidiIn() { return midiIn; }
	public JSLMidiDevice getMidiOut() { return midiOut; }
	public JSLMidiDevice getMidiMasterIn() { return midiMasterIn; }
	public JSLMidiDevice getMidiFaderIn() { return midiFaderIn; }
	public void setMidiIn(JSLMidiDevice midiIn) { this.midiIn = midiIn; }
	public void setMidiOut(JSLMidiDevice midiOut) { this.midiOut = midiOut; }
	public void setMidiMasterIn(JSLMidiDevice midiMasterIn) { this.midiMasterIn = midiMasterIn; }
	public void setMidiFaderIn(JSLMidiDevice midiFaderIn) { this.midiFaderIn = midiFaderIn; }


	// temporally hack
    /*
	void setMidiDevices() {
	    JavasoundMidiWrapper jw = (JavasoundMidiWrapper) PatchEdit.MidiOut;
	    for (int i = 0; i < deviceList.size(); i++) {
		Device dev = (Device) deviceList.get(i);
		int port = dev.getPort();
		setMidiOut(jw.getOutputDevice(port));
		int inPort = dev.getInPort();
		setMidiIn(jw.getInputDevice(inPort));
	    }
	}
    */
	// For Storable interface
	private static final String[] storedPropertyNames = {
		"libPath", "sysexPath", "note",
		"velocity", "delay", "lookAndFeel", "guiStyle",
		"repositoryURL","repositoryUser","repositoryPass",
		"faderEnable", "faderController","faderChannel",
		"masterInEnable",
		"initPortIn", "initPortOut", "masterController", "faderPort",
		// must be restored after initPortIn and initPortOut
		"midiPlatform", "device",
	};
	private static final String[] storedPropertyNamesNew = {
		"libPath", "sysexPath", "note",
		"velocity", "delay", "lookAndFeel", "guiStyle",
		"faderEnable", "faderController","faderChannel",
		"masterInEnable",
		// for backward compatibility.
		"initPortIn", "initPortOut", "masterController", "faderPort",
		"midiPlatform", "device",
		// for javax.sound
		"midiIn", "midiOut", "midiMasterIn", "midiFaderIn",
	};

	/**
	 * Get the names of properties that should be stored and loaded.
	 * @return a Set of field names
	 */
	public Set storedProperties() {
		// TreeSet reserves the restore order
		TreeSet set = new TreeSet();
		if (PatchEdit.newMidiAPI)
		    set.addAll(Arrays.asList(storedPropertyNamesNew));
		else
		    set.addAll(Arrays.asList(storedPropertyNames));
		return set;
	}

	/**
	 * Method that will be called after loading
	 */
	public void afterRestore() {
		// do nothing - we don't need any special code to execute after restore
// 		ErrorMsg.reportStatus("AppConfig: " + deviceList);
	}

	// Shall we define JSLUtil for these mothods?
	// Returns the "os.name" system property - emenaker 2003.03.13
	public static String getOSName() {
		return(getSystemProperty("os.name"));
	}

	// Returns the "java.specification.version" system property - emenaker 2003.03.13
	public static String getJavaSpecVersion() {
		return(getSystemProperty("java.specification.version"));
	}

	// Looks up a system property and returns "" on exceptions
	private static String getSystemProperty(String key) {
		try {
			return(System.getProperty(key));
		} catch(Exception e) {}
		return("");
	}
}
//(setq c-basic-offset 8)
//(setq c-basic-offset 4)
