/*
 * Copyright 2005 Federico Ferri
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
// written by Federico Ferri
// @version $Id$

package synthdrivers.RolandMKS7;

import core.*;
import java.util.prefs.Preferences;

public class RolandMKS7Device extends Device
{
  /** Creates new RolandMKS7Device */
  public RolandMKS7Device()
  {
    super ("Roland","MKS-7",null,null,"Federico Ferri");
  }

  /** Constructor for for actual work. */
  public RolandMKS7Device(Preferences prefs) {
    this();
    this.prefs = prefs;

    //addDriver(new MKS7ToneBankDriver());
    addDriver(new MKS7ToneSingleDriver());
    //addDriver(new MKS7PatchBankDriver());
    //addDriver(new MKS7PatchSingleDriver());
  }
}
