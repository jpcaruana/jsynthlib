/*
 * JSynthlib - Device for Yamaha TX7
 * =================================
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
package synthdrivers.YamahaTX7;
import synthdrivers.YamahaDX7.common.DX7FamilyDevice;
import core.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Set;
import java.util.Arrays;

public class YamahaTX7Device extends DX7FamilyDevice
{
	private static final String dxInfoText = YamahaTX7Strings.INFO_TEXT;
	
	/** Creates new YamahaTX7Device */
	public YamahaTX7Device ()
	{
		super("Yamaha","TX7",null,dxInfoText,"Torsten Tittmann");
	  
		setSPBPflag(0x00);		// switched off 'Enable Remote Control?'	and disabled
		setSwOffMemProtFlag(0x02);	// switched off 'Disable Memory Protection?'	and  enabled
		setTipsMsgFlag(0x03);		// switched on	'Display Hints and Tips?'	and  enabled
		
		// voice patch - basic patch for all modells of the DX7 family
		addDriver (new YamahaTX7VoiceSingleDriver());
		addDriver (new YamahaTX7VoiceBankDriver());
	
		// TX7	Performance patch
		addDriver (new YamahaTX7PerformanceSingleDriver());		// experimental !!!!
		addDriver (new YamahaTX7PerformanceBankDriver());		// experimental !!!!
	}
}
