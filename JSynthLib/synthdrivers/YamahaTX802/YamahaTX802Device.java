/*
 * JSynthlib - Device for Yamaha TX802
 * ===================================
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
package synthdrivers.YamahaTX802;
import synthdrivers.YamahaDX7.common.DX7FamilyDevice;

public class YamahaTX802Device extends DX7FamilyDevice
{
	private static final String dxInfoText = YamahaTX802Strings.INFO_TEXT;

	/** Creates new Yamaha TX802 Device */
	public YamahaTX802Device ()
	{
		super("Yamaha","TX802",null,dxInfoText,"Torsten Tittmann");

		setSPBPflag(0x00);		// switched off 'Enable Remote Control?'	and disabled
		setSwOffMemProtFlag(0x02);	// switched off 'Disable Memory Protection?'	and  enabled
		setTipsMsgFlag(0x03);		// switched on	'Display Hints and Tips?'	and  enabled


		// voice patch
		addDriver (new YamahaTX802VoiceSingleDriver());
		addDriver (new YamahaTX802VoiceBankDriver());

		// additional voice patch
		addDriver (new YamahaTX802AdditionalVoiceSingleDriver());	// experimental !!!!
		addDriver (new YamahaTX802AdditionalVoiceBankDriver());		// experimental !!!!

		// performance patch
		addDriver (new YamahaTX802PerformanceSingleDriver());		// experimental !!!!
		addDriver (new YamahaTX802PerformanceBankDriver());		// experimental !!!!

		// system setup patch
		addDriver (new YamahaTX802SystemSetupDriver());			// experimental !!!!

		// fractional scaling patch
		addDriver (new YamahaTX802FractionalScalingSingleDriver());	// experimental !!!!
		addDriver (new YamahaTX802FractionalScalingBankDriver());	// experimental !!!!

		// micro tuning patch
		addDriver (new YamahaTX802MicroTuningSingleDriver());		// experimental !!!!
		addDriver (new YamahaTX802MicroTuningBankDriver());		// experimental !!!!
	}
}
