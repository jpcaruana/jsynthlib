/*
 * Device.java
 *
 * Created on 5. Oktober 2001, 21:59
 *
 * $Id$
 *
 */

package core;
import javax.swing.*;
import java.util.*;
import java.io.Serializable;
/**
 *
 * @author Gerrit Gehnen
 * @version
 */

public abstract class Device implements Serializable, Storable {
    // All field should be private.
    /** Manufacturer name of the device. */
    protected String manufacturerName;
    /** Model name of the device. */
    protected String modelName;

    /** The ID-String returned by the device as response to the
     * universal inquiry command.
     */
    protected String inquiryID;

    /** Information about Device. */
    protected String infoText;
    /** Authors of the device driver. */
    protected String authors;

    // Fields above can be final.

    /** Holds value of property synthName. */
    protected String synthName;

    /** The channel (device ID) the user assigns to this driver. */
    protected int channel = 1;

    /** The MIDI input port number, where the cable <B>to</B> the
	device is connected. */
    // For simplicity every driver contains the port number as well.
    // So the setter must set the port in all drivers
    protected int inPort;
    /** The MIDI output port number, where the cable <B>to</B> the
	device is connected. */
    protected int port;   //outport

    /** The List for all available drivers of this device. */
    public ArrayList driverList = new ArrayList ();
    //private ListIterator li;

    /** Creates new Device. */
    // will be deprecated
    public Device () {
        inquiryID = "NONE";
	infoText = "There is no information about this Device.";
	inPort = PatchEdit.appConfig.getInitPortIn();
	port = PatchEdit.appConfig.getInitPortOut();
    }

    /** Create a new Device. */
    public Device (String manufactuerName, String modelName,
		   String inquiryID, String infoText, String authors) {
	this.manufacturerName = manufacturerName;
	this.modelName = modelName;
	this.inquiryID = inquiryID;
	this.infoText = infoText;
	this.authors = authors;
	inPort = PatchEdit.appConfig.getInitPortIn();
	port = PatchEdit.appConfig.getInitPortOut();
    }

    /**
     * Show configration Panel.  Overwrite if reqiured.
     */
    public JPanel config() {
	JPanel panel = new JPanel();
	panel.add(new JLabel("This Device has no configuration options."));
	return panel;
    }

    /**
     * Getter for property getManufacturerName.
     * @return Value of property getManufacturerName.
     */
    public String getManufacturerName () {
        return manufacturerName;
    }

    /**
     * Getter for property modelName.
     * @return Value of property modelName.
     */
    public  String getModelName () {
        return modelName;
    }

    /**
     * Getter for property inquiryID.
     * @return Value of property inquiryID.
     */
    public String getInquiryID() {
	return inquiryID;
    }

    /** Getter for property infoText.
     * @return Value of property infoText.
     */
    public String getInfoText() {
	return infoText;
    }

    /**
     * Getter for property authors.
     * @return Value of property authors.
     */
    public String getAuthors() {
	return authors;
    }

    /**
     * Getter for property synthName.
     * @return Value of property synthName.
     */
    public String getSynthName () {
        return synthName;
    }

    /**
     * Setter for property synthName.
     * @param synthName New value of property synthName.
     */
    public void setSynthName (String synthName) {
        this.synthName = synthName;
    }

    /**
     * Getter for property channel.
     * @return Value of property channel.
     */
    public int getChannel () {
        return channel;
    }

    /**
     * Setter for property channel.
     * @param channel New value of property channel.
     */
    public void setChannel (int channel) {
        Iterator iter;
        this.channel = channel;
        iter = driverList.iterator();
        while (iter.hasNext()) {
	    ((Driver) iter.next()).setChannel(channel);
        }
    }

    /**
     * Getter for property port.
     * @return Value of property port.
     */
    public int getPort () {
        return port;
    }

    /**
     * Getter for property inPort.
     * @return Value of property inPort.
     */
    public int getInPort () {
        return inPort;
    }

    /**
     * Setter for property port.
     * @param port New value of property port.
     */
    public void setPort (int port) {
        Iterator iter;
        this.port = port;
        iter = driverList.iterator();
        while (iter.hasNext ())
            ((Driver) iter.next()).setPort(port);
    }

    /**
     * Setter for property inPort.
     * @param inPort New value of property inPort.
     */
    public void setInPort (int inPort) {
        Iterator iter;
        this.inPort = inPort;
        iter = driverList.iterator();
        while (iter.hasNext())
            ((Driver) iter.next()).inPort = inPort;
    }

    // Getters/Setters, etc for Drivers
    /**
     * Add Driver.
     * @param driver Driver to be added.
     */
    protected void addDriver (Driver driver) {
        driver.setChannel (channel);
        driver.setPort (port);
        driver.inPort = inPort;
        driverList.add (driver);
	driver.setDevice(this);
    }

    /**
     * Add Driver at the <code>index</code>.
     * @param index The index, where the driver is added in the list
     * Bulk converters must be added before simple drivers!
     * @param driver Driver to be added.
     */
    protected void addDriver (int index, Driver driver) {
        driver.setChannel (channel);
        driver.setPort (port);
	// intensionally omitted?
        //driver.inPort = inPort;
        driverList.add (index, driver);
	//driver.setDevice(this);
    }

    /** Indexed getter for driverList elements. */
    public Driver getDriver(int i) {
	return (Driver) this.driverList.get(i);
    }

    /** Indexed setter for driverList elements. */
    public Driver setDriver(int i, Driver drv) {
	return (Driver) this.driverList.set(i, drv);
    }

    /** Getter for driverList. */
    public Driver[] getDriver() {
	return (Driver[]) this.driverList.toArray(new Driver[0]);
    }

    /** setter for driverList. */
    public void setDriver(Driver[] drivers) {
	ArrayList newList = new ArrayList();
	newList.addAll(Arrays.asList(drivers));
	this.driverList = newList;
    }

    /** Remover for driverList elements. */
    public Driver removeDriver(int i) {
	return (Driver) this.driverList.remove(i);
    }

    /** Size query for driverList. */
    public int driverCount() {
	return this.driverList.size();
    }

    // For storable interface
    /**
     * Get the names of properties that should be stored and loaded.
     * @return a Set of field names.
     */
    public Set storedProperties() {
	final String[] storedPropertyNames = {
	    "inPort", "synthName", "port", "channel",
	    "driver",
	};
	HashSet set = new HashSet();
	set.addAll(Arrays.asList(storedPropertyNames));
	return set;
    }

    /**
     * Method that will be called after loading.
     */
    public void afterRestore() {
	// do nothing
    }

    /**
     * Getter for DeviceName.
     * @return String of Device Name with inPort and Channel.
     */
    public String getDeviceName() {
	try {
	    return getManufacturerName() + " " + getModelName()
		+ " <" + getSynthName() + ">  -  MIDI In Port: "
		+ PatchEdit.MidiOut.getInputDeviceName(getInPort())
		+ "  -  MIDI Channel: " + getChannel();
	} catch (Exception e) {
	    return getManufacturerName() + " " + getModelName() + ": "
		+ getSynthName();
	}
    }

    /**
     * Same as <code>getDeviceName()</code>.
     * See #getDeviceName
     */
    public String toString() {
	return getDeviceName();
    }

    /**
     * Show a dialog for the details of the device.
     */
    public void showDetails() {
        DeviceDetailsDialog ddd = new DeviceDetailsDialog(this);
	ddd.show();
    }

    /**
     * Compares the header & size of a Patch to this driver to see if
     * this driver is the correct one to support the patch.
     * @param patchString
     * @return true if the patchString matches the ID of the device
     */
    public boolean checkInquiry (StringBuffer patchString) {
        StringBuffer inquiryString = new StringBuffer (inquiryID);
	if (inquiryString.length () > patchString.length())
	    return false;
	for (int j = 0; j < inquiryString.length(); j++)
            if (inquiryString.charAt(j) == '*')
		inquiryString.setCharAt(j, patchString.charAt(j));
        return (inquiryString.toString().equalsIgnoreCase(patchString.toString().substring(0, inquiryString.length())));
    }
}
