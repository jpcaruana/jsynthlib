/*
 * JSynthlib - Device for Yamaha DX7-II
 * ====================================
 * @version $Id$
 * @author  Torsten Tittmann
 *
 * Copyright (C) 2002-2004 Torsten.Tittmann@gmx.de
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.i
 *
 */
package synthdrivers.YamahaDX7II;
import	synthdrivers.YamahaDX7.common.DX7FamilyDevice;
import core.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Set;
import java.util.Arrays;

public class YamahaDX7IIDevice extends DX7FamilyDevice
{
	private static final String dxInfoText = YamahaDX7IIStrings.INFO_TEXT;

	/** Creates new YamahaDX7-II Device */
	public YamahaDX7IIDevice ()
	{
		super("Yamaha","DX7-II",null,dxInfoText,"Torsten Tittmann");

		setSPBPflag(0x00);		// switched off 'Enable Remote Control?'	and disabled
		setSwOffMemProtFlag(0x02);	// switched off	'Disable Memory Protection?'	and  enabled
		setTipsMsgFlag(0x03);		// switched on	'Display Hints and Tips?'	and  enabled

		// voice patch
		addDriver (new YamahaDX7IIVoiceSingleDriver());
		addDriver (new YamahaDX7IIVoiceBankDriver());

		// additional voice patch
		addDriver (new YamahaDX7IIAdditionalVoiceSingleDriver());    // experimental !!!!
		addDriver (new YamahaDX7IIAdditionalVoiceBankDriver());      // experimental !!!!

		// performance patch
		addDriver (new YamahaDX7IIPerformanceSingleDriver());	     // experimental !!!!
		addDriver (new YamahaDX7IIPerformanceBankDriver());	     // experimental !!!!
	
		// system setup patch
		addDriver (new YamahaDX7IISystemSetupDriver());		     // experimental !!!!

		// fractional scaling patch
		addDriver (new YamahaDX7IIFractionalScalingSingleDriver());  // experimental !!!!
		addDriver (new YamahaDX7IIFractionalScalingBankDriver());    // experimental !!!!

		// micro tuning patch
		addDriver (new YamahaDX7IIMicroTuningSingleDriver());	     // experimental !!!!
		addDriver (new YamahaDX7IIMicroTuningBankDriver());	     // experimental !!!!
	}
}
