// written by Kenneth L. Martinez
// @version $Id$

package org.jsynthlib.drivers.roland.mks50;

import java.util.prefs.Preferences;

import org.jsynthlib.core.Device;

public class RolandMKS50Device extends Device
{
  /** Creates new RolandMKS50Device */
  public RolandMKS50Device()
  {
    super ("Roland","MKS-50",null,null,"Kenneth L. Martinez");
  }

  /** Constructor for for actual work. */
  public RolandMKS50Device(Preferences prefs) {
    this();
    this.prefs = prefs;

    addDriver(new MKS50ToneBankDriver());
    addDriver(new MKS50ToneSingleDriver());
    addDriver(new MKS50PatchBankDriver());
    addDriver(new MKS50PatchSingleDriver());
  }
}
