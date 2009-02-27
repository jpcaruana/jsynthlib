/*
 * LinuxCharDevMidiDeviceProvider.java - This file is part of JSynthLib
 *
 * MidiDeviceProvider for Character Device Midi interfaces of Linux OS
 * ====================================================================
 * 
 * @version $Id$
 * @author  Torsten Tittmann
 *
 * Copyright (C) 2004 Torsten.Tittmann@gmx.de
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */

package LinuxCharDevMidiProvider;

import java.io.*;
import java.util.*;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.spi.MidiDeviceProvider;


public class LinuxCharDevMidiDeviceProvider extends MidiDeviceProvider {
	private static List deviceList = new ArrayList();

	static {
		try {
			scanDevices();
		} catch (Exception e) {}
	}
	
  	public LinuxCharDevMidiDeviceProvider() {
	}


	public MidiDevice getDevice(MidiDevice.Info info) {
		if (!isDeviceSupported(info))
			throw new IllegalArgumentException();
		
		MidiDevice device   = null;
		Iterator   iterator = deviceList.iterator();
		while (iterator.hasNext()) {
			MidiDevice      tmpDevice = (MidiDevice) iterator.next();
			if ( info.equals( tmpDevice.getDeviceInfo() ) ) {
				device = tmpDevice;
				break;
			}
		}
		if (device == null) {
			throw new IllegalArgumentException("no device for: "+info);
		}
		
		return device;
  	}


  	public MidiDevice.Info[] getDeviceInfo() {
		if (deviceList == null)
			return new MidiDevice.Info[0];

		MidiDevice.Info[] info      = new MidiDevice.Info[deviceList.size()];
		Iterator          iterator  = deviceList.iterator();
		int               index     = 0;
		while (iterator.hasNext()) {
			info[index++] = ((MidiDevice)iterator.next()).getDeviceInfo();
		}

		return info;
	}


	/* FIXME looking for file "linuxdevices.conf" and read its content
	 * FIXME should be replaced by evaluation of /dev/sndstat (/proc/asound/oss/soundstat)
	 * FIXME and the rawmidi devices at /proc/asound/devices */
	private static void scanDevices() throws FileNotFoundException, IOException {
		String     line;
		
		try {
			FileReader fileReader = new FileReader("linuxdevices.conf");
			LineNumberReader lineNumberReader = new LineNumberReader(fileReader);
			while ((line = lineNumberReader.readLine())!=null) {
				deviceList.add( new LinuxCharDevMidiDevice(line) );
			}
		} catch (FileNotFoundException e) {
			throw new FileNotFoundException("LinuxCharDevMidiDeviceProvider.scanDevices: Unable to read from 'linuxdevices.conf'");
		}
	}
}
