/*
 * Copyright 2004 Hiroo Hayashi
 *
 * This file is part of JSynthLib.
 *
 * JSynthLib is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or(at your option) any later version.
 *
 * JSynthLib is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JSynthLib; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 */
package core;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Utility Routines for Synth Drivers.
 * @version $Id$
 * @author Hiroo Hayashi
 */
public class DriverUtil {
    // don't have to call constructor for Utility class.
    private DriverUtil() {
    }

    /**
     * Factory method of Patch. Look up the driver for sysex byte array, and
     * create a patch by using the driver found. This is used for a byte array
     * read from a Sysex file, for which a Driver is not known.
     */
    public static IPatch[] createPatches(byte[] sysex) {
        return createPatches(sysex, chooseDriver(sysex));
    }

    /**
     * Factory method of Patch. Look up the driver of the specified Device for
     * sysex byte array, and create a patch by using the driver found.
     * @param device Device whose driver is looked up.
     */
    public static IPatch[] createPatches(byte[] sysex, Device device) {
        return createPatches(sysex, chooseDriver(sysex, device));
    }

    private static IPatch[] createPatches(byte[] sysex, IDriver driver) {
        if (driver == null)
            return null;
        else if (driver.isConverter())
            return ((IConverter) driver).createPatches(sysex);
        else
            return new IPatch[] { ((IPatchDriver) driver).createPatch(sysex) };
    }

    public static IPatch createPatch(byte[] sysex) {
        IPatchDriver driver = (IPatchDriver) chooseDriver(sysex);
        return driver != null ? driver.createPatch(sysex) : null;
    }

    /**
     * choose proper driver for sysex byte array.
     * @param sysex System Exclusive data byte array.
     * @return Driver object chosen
     * @see IDriver#supportsPatch
     */
    public static IDriver chooseDriver(byte[] sysex) {
        String patchString = getPatchHeader(sysex);

        for (int idev = 0; idev < AppConfig.deviceCount(); idev++) {
            // Outer Loop, iterating over all installed devices
            Device dev = AppConfig.getDevice(idev);
            for (int idrv = 0; idrv < dev.driverCount(); idrv++) {
                IPatchDriver drv = (IPatchDriver) dev.getDriver(idrv);
                // Inner Loop, iterating over all Drivers of a device
                if (drv.supportsPatch(patchString, sysex))
                    return drv;
            }
        }
        return null;
    }

    /**
     * choose proper driver in a given device for sysex byte array.
     * @param sysex System Exclusive data byte array.
     * @param dev Device
     * @return Driver object chosen
     * @see IDriver#supportsPatch
     */
    public static IDriver chooseDriver(byte[] sysex, Device dev) {
        String patchString = getPatchHeader(sysex);
        for (int idrv = 0; idrv < dev.driverCount(); idrv++) {
            IPatchDriver drv = (IPatchDriver) dev.getDriver(idrv);
            // Inner Loop, iterating over all Drivers of a device
            if (drv.supportsPatch(patchString, sysex))
                return drv;
        }
        return null;
    }

    /**
     * Return a hexadecimal string for
     * {@link IDriver#supportsPatch IDriver.suppportsPatch} at most 16 byte
     * sysex data.
     *
     * @see IDriver#supportsPatch
     */
    public static String getPatchHeader(byte[] sysex) {
        StringBuffer patchstring = new StringBuffer("F0");

        // Some Sysex Messages are shorter than 16 Bytes!
        // 	for (int i = 1; (sysex.length < 16) ? i < sysex.length : i < 16; i++)
        // {
        for (int i = 1; i < Math.min(16, sysex.length); i++) {
            if ((int) (sysex[i] & 0xff) < 0x10)
                patchstring.append("0");
            patchstring.append(Integer.toHexString((int) (sysex[i] & 0xff)));
        }
        return patchstring.toString();
    }

    /**
     * Caluculate check sum of a byte array <code>sysex</code>.
     * <p>
     *
     * The checksum calculation method of this method is used by Roland, YAMAHA,
     * etc.<p>
     *
     * Compatibility Note: This method became 'static' method.
     *
     * @param sysex
     *            a byte array
     * @param start
     *            start offset
     * @param end
     *            end offset
     * @param ofs
     *            offset of the checksum data
     * @see Driver#calculateChecksum(Patch)
     */
    public static void calculateChecksum(byte[] sysex, int start, int end, int ofs) {
        int sum = 0;
        for (int i = start; i <= end; i++)
            sum += sysex[i];
	sysex[ofs] = (byte) (-sum & 0x7f);
	/*
	Equivalent with above.
	p.sysex[ofs] = (byte) (sum & 0x7f);
	p.sysex[ofs] = (byte) (p.sysex[ofs] ^ 0x7f);
	p.sysex[ofs] = (byte) (p.sysex[ofs] + 1);
	p.sysex[ofs] = (byte) (p.sysex[ofs] & 0x7f);
	*/
    }

    /**
     * A utility method to generates an array of formatted numbers.
     * For example,
     * <pre>
     *   patchNumbers = generateNumbers(1, 10, "Patch 00");
     * </pre>
     * setups the following array,
     * <pre>
     *   {
     *     "Patch 01", "Patch 02", "Patch 03", "Patch 04", "Patch 05"
     *     "Patch 06", "Patch 07", "Patch 08", "Patch 09", "Patch 10"
     *   }
     * </pre>
     *
     * @param min minumux value
     * @param max maximum value
     * @param format pattern String for java.text.DecimalFormat
     * @return an array of formatted numbers.
     * @see java.text.DecimalFormat
     * @see IPatchDriver#getPatchNumbers
     * @see IPatchDriver#getPatchNumbersForStore
     * @see IPatchDriver#getBankNumbers
     */
    public static String[] generateNumbers(int min, int max, String format){
        String retval[] = new String[max - min + 1];
        DecimalFormat df = (DecimalFormat)NumberFormat.getInstance().clone();
        df.applyPattern(format);
        while (max >= min)
            retval[max - min] = df.format(max--);
        return retval;
    }

    /**
     * Create new patch using a patch file <code>patchFileName</code>.
     *
     * @param driver IPatchDriver object
     * @param fileName file name (relative path to driver directory)
     * @param size Sysex data size
     * @return IPatch object
     * @see IPatchDriver#createPatch()
     */
    public static IPatch createNewPatch(IPatchDriver driver, String fileName,
            int size) { // Borrowed from DR660 driver
        byte[] buffer = new byte[size];

        try {
            InputStream fileIn = driver.getClass().getResourceAsStream(fileName);

            if (fileIn != null) {
            	fileIn.read(buffer);
            	fileIn.close();
            	return driver.createPatch(buffer);
            }
            else {
            	throw new FileNotFoundException("File: " + fileName + " does not exist!");
            }
        } catch (IOException e) {
            ErrorMsg.reportError("Error", "Unable to open " + fileName, e);
            return null;
        }
    }

}
