package org.jsynthlib.core;

/**
 * Created by IntelliJ IDEA.
 * User: jemenake
 * Date: Feb 28, 2005
 * Time: 6:24:24 PM
 */
public class DeviceDescriptor implements Comparable {
    private String shortName="";
	private String deviceName="";
    private String deviceClass="";
    private String IDString="";
    private String manufacturer="";
    private String type="";
    
    public DeviceDescriptor() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIDString() {
        return IDString;
    }

    public void setIDString(String IDString) {
        this.IDString = IDString;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getDeviceClass() {
        return deviceClass;
    }

    public void setDeviceClass(String deviceClass) {
        this.deviceClass = deviceClass;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String toString() {
        return(
            deviceName + ":\n" +
            "  ShortName: " + shortName + "\n" +
            "  deviceClass: " + deviceClass + "\n" +
            "  IDString: " + IDString + "\n" +
            "  Manufacturer: " + manufacturer + "\n" +
            "  Type: " + type);
    }

    //
    // The equals() and compareTo() methods are to make this work with all of the java Collections classes
    //
    public boolean equals(Object obj) {
        if(obj instanceof DeviceDescriptor) {
            DeviceDescriptor that = (DeviceDescriptor) obj;
            if(deviceClass.equals(that.deviceClass)
            && IDString.equals(that.IDString)
            && deviceName.equals(that.deviceName)
            && manufacturer.equals(that.manufacturer)
            && type.equals(that.type)) {
                return(true);
            }
        }
        return(false);
    }

    public int compareTo(Object obj) {
        if(obj instanceof DeviceDescriptor) {
            DeviceDescriptor that = (DeviceDescriptor) obj;
            return(this.deviceName.compareTo(that.deviceName));
        }
        return(0);
    }
}
