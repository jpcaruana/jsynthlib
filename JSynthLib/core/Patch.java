/*
 * $Id$
 */

package core;
import java.io.*;
import java.awt.datatransfer.*;

public class Patch extends Object implements Serializable, Transferable {
    public StringBuffer comment;
    public StringBuffer date;
    public StringBuffer  author;
    public transient int driverNum;
    public transient int deviceNum;
    public   byte[]  sysex ;

    static final long serialVersionUID = 2220769917598497681L;

    public Patch() {
        comment = new StringBuffer();
        date = new StringBuffer();
        author = new StringBuffer();
        sysex = new byte[1024];
        ChooseDriver();
    }

    public Patch(byte[] gsysex) {
        comment = new StringBuffer();
        date = new StringBuffer();
        author = new StringBuffer();
        sysex = gsysex;
        ChooseDriver();
    }

    /**
     * Constructor - The device number is known, but not the driver number
     * @param deviceNum The known device number
     * @param gsysex The MIDI SysEx message
     */
    public Patch(int deviceNum, byte[] gsysex) {
	comment = new StringBuffer();
	date = new StringBuffer();
	author = new StringBuffer();
	sysex = gsysex;
 	this.deviceNum = deviceNum;
        ChooseDriver(deviceNum);
    }

    /**
     * Constructor - Device and driver number are known
     * @param gsysex The MIDI SysEx message
     * @param deviceNum The known device number
     * @param driverNum The known driver number
     */
    public Patch(byte[] gsysex, int deviceNum, int driverNum) {
	comment = new StringBuffer();
	date = new StringBuffer();
	author = new StringBuffer();
	sysex = gsysex;
 	this.deviceNum = deviceNum;
 	this.driverNum = driverNum;
    }

    public Patch(byte[] gsysex, int offset) {
	comment = new StringBuffer();
        date = new StringBuffer();
        author = new StringBuffer();
        sysex = new byte[gsysex.length - offset];
        System.arraycopy(gsysex, offset, sysex, 0, gsysex.length - offset);
        ChooseDriver();
    }

    public Patch(byte[] gsysex, String gdate, String gauthor, String gcomment) {
        this.comment = new StringBuffer(gcomment);
        this.date = new StringBuffer(gdate);
        this.author = new StringBuffer(gauthor);
        sysex = new byte[1024];
        this.sysex = gsysex;
        ChooseDriver();
    }

    /**
     * Constructor - all parameters are known
     * @param gsysex The MIDI SysEx message
     * @param deviceNum The known device number
     * @param driverNum The known driver number
     * @param gdate A comment
     * @param gauthor Another comment
     * @param gcomment A last comment
     */
    public Patch(byte[] gsysex, int deviceNum, int driverNum, String gdate, String gauthor, String gcomment) {
	this.comment = new StringBuffer(gcomment);
	this.date = new StringBuffer(gdate);
	this.author = new StringBuffer(gauthor);
	this.sysex = gsysex;
 	this.deviceNum = deviceNum;
 	this.driverNum = driverNum;
    }

    /**
     * Which driver of a device with known device number supports this patch
     * @param deviceNum The known device number
     */
    public void ChooseDriver(int deviceNum) {
        this.deviceNum = deviceNum;
        Device dev = (Device) PatchEdit.appConfig.getDevice(deviceNum);
        StringBuffer patchString = this.getPatchHeader();

        for (int j = 0; j < dev.driverList.size(); j++) {
	    // iterating over all Drivers of the given device
	    if (((Driver) dev.driverList.get(j)).supportsPatch(patchString, this)) {
		this.driverNum = j;
		getDriver().trimSysex(this);
		return;
	    }
        }
        // Unkown patch, try to guess at least the manufacturer
        comment = new StringBuffer("Probably a " + LookupManufacturer.get(sysex[1], sysex[2], sysex[3]) + " Patch, Size: " + sysex.length);
    }

    public void ChooseDriver() { // should be chooseDriver()
        Device dev;
        driverNum = 0;
        deviceNum = 0;
        Integer intg = new Integer(0);
        StringBuffer patchString = this.getPatchHeader();

        StringBuffer driverString = new StringBuffer();
        for (int i2 = 0; i2 < PatchEdit.appConfig.deviceCount(); i2++) {
            // Outer Loop, iterating over all installed devices
	    dev = (Device) PatchEdit.appConfig.getDevice(i2);
	    for (int j = 0; j < dev.driverList.size(); j++) {
                // Inner Loop, iterating over all Drivers of a device
		if (((Driver) dev.driverList.get(j)).supportsPatch(patchString, this)) {
		    driverNum = j;
		    deviceNum = i2;
                    getDriver().trimSysex(this);
                    return;
                }
            }
        }
        // Unkown patch, try to guess at least the manufacturer
        comment = new StringBuffer("Probably a " + LookupManufacturer.get(sysex[1], sysex[2], sysex[3]) + " Patch, Size: " + sysex.length);

    }

    public java.lang.Object getTransferData(java.awt.datatransfer.DataFlavor p1)
	throws java.awt.datatransfer.UnsupportedFlavorException, java.io.IOException {
        return this;
    }

    public boolean isDataFlavorSupported(final java.awt.datatransfer.DataFlavor p1) {
        // System.out.println("isDataFlavorSupported "+driverNum);
        if (p1.equals(new DataFlavor(getDriver().getClass(), getDriver().toString()))) {
	    //new DataFlavor(((Device)PatchEdit.appConfig.getDevice(deviceNum)).driverList.get(driverNum).getClass(),((Device)PatchEdit.appConfig.getDevice(driverNum)).driverList.get(driverNum).toString())
            return true;
        }
        return false;
    }

    public java.awt.datatransfer.DataFlavor[] getTransferDataFlavors() {
        // System.out.println("getTransferDataFlavors "+driverNum);
        DataFlavor[] df = new DataFlavor[1];
	// df[0] =  new DataFlavor(((Device)PatchEdit.appConfig.getDevice(deviceNum)).driverList.get(driverNum).getClass(),((Device)PatchEdit.appConfig.getDevice(driverNum)).driverList.get(driverNum).toString());
	df[0] = new DataFlavor(getDriver().getClass(), getDriver().toString());

        return df;
    }

    public Patch[] dissect() {
        Device dev;
        Driver drv;
        int looplength;
        Patch[] patarray = null;
        StringBuffer patchString = this.getPatchHeader();

        for (int k = 0; k < PatchEdit.appConfig.deviceCount(); k++) {
	    // Do it for all converters. They should be at the
	    // beginning of the driver list!
	    dev = (Device) PatchEdit.appConfig.getDevice(k);
	    for (int j = 0; j < dev.driverList.size(); j++) {
		if (!(dev.driverList.get(j) instanceof Converter))
                    continue;
		if (!((Driver) (dev.driverList.get(j))).supportsPatch(patchString, this)) {
		    // Try, until one converter was successfull
                    continue;
                }
		patarray = ((Converter) (dev.driverList.get(j))).extractPatch(this);
		if (patarray != null) {
                    break;
                }
            }
	} //    k++;
        if (patarray != null) {
	    // Conversion was sucessfull, we have at least one converted patch
	    looplength = patarray.length;

	    // assign the original deviceNum and individual driverNum
	    // to each patch of patarray
	    for (int i = 0; i < looplength; i++) {
		patarray[i].deviceNum = this.deviceNum;
		dev = (Device) PatchEdit.appConfig.getDevice(patarray[i].deviceNum);
		patchString = patarray[i].getPatchHeader();

		for (int j = 0; j < dev.driverList.size(); j++) {
		    if (((Driver) dev.driverList.get(j)).supportsPatch(patchString, patarray[i]))
			patarray[i].driverNum = j;
		}
	    }
	} else { // No conversion. Try just the original patch....
	    looplength = 1;
	    patarray = new Patch[1];
	    patarray[0] = this;
        }

        return patarray;
    }

    public Driver getDriver() {
	return PatchEdit.getDriver(deviceNum, driverNum);
    }

    public StringBuffer getPatchHeader() {
	StringBuffer patchstring = new StringBuffer("F0");

	for (int i = 1; (sysex.length < 16) ? i < sysex.length : i < 16; i++) {
	    // Some Sysex Messages are shorter than 16 Bytes!
	    if (sysex[i] < 16)
		patchstring.append("0");
	    patchstring.append(Integer.toHexString(sysex[i] & 0xff));
	}

	return patchstring;
    }

    /**
     * Dump byte data array.  Only for debugging.
     *
     * @return string like "f0 a3 00 "
     */
    public String toString() {
	StringBuffer buf = new StringBuffer();
	for (int i = 0; i < this.sysex.length; i++) {
	    String s = Integer.toHexString((int) this.sysex[i] & 0xff);
	    buf.append((s.length() == 1 ? "0" : "") + s + " ");
	}
	return buf.toString();
    }
}
