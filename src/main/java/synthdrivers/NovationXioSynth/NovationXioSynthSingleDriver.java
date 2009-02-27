/*
 * @version $Id: NovationXioSynthSingleDriver.java,v 1.9 2008/12/16 $
 */
package synthdrivers.NovationXioSynth;

import java.io.UnsupportedEncodingException;

import org.jsynthlib.core.Driver;
import org.jsynthlib.core.JSLFrame;
import org.jsynthlib.core.Patch;

public class NovationXioSynthSingleDriver extends Driver
{
    public NovationXioSynthSingleDriver()
    {
	super ("Single","Nicolas Boulicault");
	sysexID= "F000202901427F0";
	patchNameStart=0xA4;
	patchNameSize=16;
	deviceIDoffset=0;
    }
    
    public void setPatchName(Patch p, String name)
    {
	while (name.length() < patchNameSize)
	    name = name + " ";
	
	byte[] namebytes = new byte[patchNameSize];
	
	try {
	    namebytes = name.getBytes("US-ASCII");
	    for (int i = 0; i < patchNameSize; i++)
		p.sysex[patchNameStart + i] = namebytes[i];
	} catch (UnsupportedEncodingException ex) {
	    return;
	}
    }
    
    public String getPatchName(Patch p,int patchNum) 
    {
	int nameStart = 164;
	
	try {
	    StringBuffer s= new StringBuffer(new String(((Patch)p).sysex,nameStart,
							16,"US-ASCII"));
	    return s.toString();
	} catch (UnsupportedEncodingException ex) {return "-";}	  
    }
    
    public boolean supportsPatch(String patchString, byte[] sysex) 
    {
	if (sysex.length != 270) {
	    return false;
	}
	
	if ((patchSize != sysex.length) && (patchSize != 0))
	    return false;
	
	if (sysexID == null || patchString.length() < sysexID.length())
	    return false;
	
	StringBuffer compareString = new StringBuffer();
	for (int i = 0; i < sysexID.length(); i++) {
	    switch (sysexID.charAt(i)) {
	    case '*':
		compareString.append(patchString.charAt(i));
		break;
	    default:
		compareString.append(sysexID.charAt(i));
	    }
	}
	
	return (compareString.toString().equalsIgnoreCase
		(patchString.substring(0, sysexID.length())));
    }
    
    public void calculateChecksum(Patch p)
    {
	/* I think there's no checksum on xio.. */
    }
    
    protected void setPatchNum(int patchNum) 
    {
    }

    public void storePatch (Patch p, int bankNum,int patchNum)
    {	
	sendPatch(p);
    }

    /* I took new patch from InitProgram in Xio */

    public Patch createNewPatch() 
    {
	byte[] sysex = new byte [] {
	    (byte)0xF0,(byte)0x00,(byte)0x20,(byte)0x29,(byte)0x01,(byte)0x42,(byte)0x7F,(byte)0x00,(byte)0x00,(byte)0x10,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x38,(byte)0x19,(byte)0x00,(byte)0x00,(byte)0x40,(byte)0x00,(byte)0x15,(byte)0x40,(byte)0x40,(byte)0x42,(byte)0x40,(byte)0x40,(byte)0x40,(byte)0x40,(byte)0x40,(byte)0x40,(byte)0x40,(byte)0x42,(byte)0x40,(byte)0x40,(byte)0x40,(byte)0x40,(byte)0x40,
	    (byte)0x40,(byte)0x40,(byte)0x42,(byte)0x40,(byte)0x40,(byte)0x40,(byte)0x40,(byte)0x40,(byte)0x40,(byte)0x40,(byte)0x40,(byte)0x49,(byte)0x40,(byte)0x40,(byte)0x7F,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x40,(byte)0x7F,(byte)0x7F,(byte)0x40,(byte)0x40,(byte)0x40,(byte)0x40,(byte)0x40,(byte)0x40,(byte)0x40,(byte)0x40,(byte)0x00,(byte)0x40,(byte)0x40,
	    (byte)0x00,(byte)0x3C,(byte)0x40,(byte)0x02,(byte)0x5A,(byte)0x7F,(byte)0x28,(byte)0x40,(byte)0x02,(byte)0x41,(byte)0x00,(byte)0x41,(byte)0x00,(byte)0x44,(byte)0x00,(byte)0x32,(byte)0x44,(byte)0x00,(byte)0x32,(byte)0x00,(byte)0x00,(byte)0x3F,(byte)0x40,(byte)0x40,(byte)0x40,(byte)0x38,(byte)0x03,(byte)0x40,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x7F,(byte)0x40,(byte)0x40,(byte)0x40,(byte)0x0A,
	    (byte)0x00,(byte)0x00,(byte)0x00,(byte)0x40,(byte)0x64,(byte)0x00,(byte)0x40,(byte)0x40,(byte)0x00,(byte)0x40,(byte)0x7F,(byte)0x00,(byte)0x00,(byte)0x40,(byte)0x5A,(byte)0x00,(byte)0x40,(byte)0x14,(byte)0x00,(byte)0x4A,(byte)0x40,(byte)0x40,(byte)0x40,(byte)0x14,(byte)0x00,(byte)0x00,(byte)0x04,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x02,(byte)0x02,(byte)0x02,
	    (byte)0x01,(byte)0x01,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x40,(byte)0x40,(byte)0x40,(byte)0x40,(byte)0x40,(byte)0x40,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x40,(byte)0x49,(byte)0x6E,(byte)0x69,(byte)0x74,(byte)0x20,(byte)0x50,(byte)0x72,(byte)0x6F,(byte)0x67,(byte)0x72,(byte)0x61,(byte)0x6D,(byte)0x20,(byte)0x20,(byte)0x20,(byte)0x20,
	    (byte)0x00,(byte)0x00,(byte)0x40,(byte)0x40,(byte)0x40,(byte)0x40,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x07,(byte)0x02,(byte)0x00,(byte)0x7F,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x7F,(byte)0x40,(byte)0x40,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
	    (byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x7F,(byte)0x40,(byte)0x40,(byte)0x40,(byte)0x40,(byte)0x00,(byte)0x2C,(byte)0x00,(byte)0x40,(byte)0x20,(byte)0x00,(byte)0x7F,(byte)0x07,(byte)0x00,(byte)0x00,(byte)0x05,(byte)0x06,(byte)0x00,(byte)0x02,(byte)0x07,(byte)0x00,(byte)0x7F,(byte)0x10,(byte)0x40,(byte)0x40,(byte)0x3F,(byte)0x07,(byte)0x07,(byte)0x38,(byte)0x07,
	    (byte)0x07,(byte)0x07,(byte)0x07,(byte)0x07,(byte)0x00,(byte)0x3F,(byte)0x07,(byte)0x3F,(byte)0x00,(byte)0x3F,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0xF7
	};
	
	Patch p = new Patch(sysex, this);
	calculateChecksum(p);	 
	return p;
    }
    
    public JSLFrame editPatch(Patch p)
    {
	return new NovationXioSynthSingleEditor((Patch)p);
    }
}
