/*
 * JSynthlib - "Micro Tuning" Single Driver for Yamaha TX802
 * =========================================================
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
package synthdrivers.YamahaTX802;
import synthdrivers.YamahaDX7.common.DX7FamilyMicroTuningSingleDriver;
import core.IPatch;
import core.JSLFrame;

public class YamahaTX802MicroTuningSingleDriver extends DX7FamilyMicroTuningSingleDriver
{
	public YamahaTX802MicroTuningSingleDriver()
	{
		super ( YamahaTX802MicroTuningConstants.INIT_MICRO_TUNING,
			YamahaTX802MicroTuningConstants.SINGLE_MICRO_TUNING_PATCH_NUMBERS,
			YamahaTX802MicroTuningConstants.SINGLE_MICRO_TUNING_BANK_NUMBERS
		);
	}


	public IPatch createNewPatch()
	{
		return super.createNewPatch();
	}


	public JSLFrame editPatch(IPatch p)
	{
		return super.editPatch(p);
	}
}
