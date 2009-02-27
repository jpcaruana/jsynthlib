package org.jsynthlib.drivers.clavia.nordstage;

import java.util.prefs.Preferences;

import org.jsynthlib.core.Device;

/**
 * This is a beta driver for the Nord Stage Rev. B. It has been tested on OS
 * 2.16.
 * 
 * @author Fredrik Zetterberg & James Fry
 */
public class NordStageDevice extends Device {

	private static final String INFO_TEXT = "This is a beta driver for the "
			+ "Nord Stage Rev. B. The SysEx information was largely reverse "
			+ "engineered to create this driver, so there are almost certainly "
			+ "bugs present. It has been tested using a Nord Stage Rev. B with "
			+ "OS 2.16.";

	public NordStageDevice() {
		// FIXME: this should have the Universal SysEx Inquiry response, not
		// this shortened version. It can be up to 16 bytes, in response to F0
		// 7E channel 06 data F7 (is data 00?)
		// f0 7e 00 06 02 42 6e 00 08 00 00 00 00 01 f7

		super("Clavia", "Nord Stage", "F07E000602426E00080000000001F7",
				INFO_TEXT, "Fredrik Zetterberg & James Fry");

	}

	public NordStageDevice(Preferences prefs) {
		this();
		this.prefs = prefs;
		addDriver(new NordStagePatchSingleDriver());
	}
}
