/*
 * @version $Id: NovationXioSynthBankDriver.java,v 1.0 2008/16/12 $
 */
package synthdrivers.NovationXioSynth;
import core.*;

import java.io.*;
import javax.swing.*;

import org.jsynthlib.core.BankDriver;
import org.jsynthlib.core.ErrorMsg;
import org.jsynthlib.core.Patch;

public class NovationXioSynthBankDriver extends BankDriver
{
    private final NovationXioSynthSingleDriver singleDriver;
    Patch bank = null;
    /* I use this global to be able to delete in deletePatch... */
    /* Ugly hack.. */
    
    public NovationXioSynthBankDriver(NovationXioSynthSingleDriver singleDriver)
    {
	super ("Bank","Nicolas Boulicault",100,4);
	sysexID= "F000202901427F0";
	this.singleDriver = singleDriver;
	
	deviceIDoffset=0;
	bankNumbers =new String[] {"Sound Bank"};
	
	patchNumbers=new String[100];
	for (int i = 0; i < 100; i++) {
	    patchNumbers[i] = new String("P" + i + " ");
	}
	
	singleSize=270;
	singleSysexID="F000202901427F0";
    }

    public boolean supportsPatch(String patchString, byte[] sysex) 
    {
	if (sysex.length != 27000) {
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
    
    public int getPatchStart(int patchNum)
    {
	int start=(270*patchNum);
	
	return start;
    }
    
    public void copySelectedPatch() 
    {
    }

    public String getPatchName(Patch p,int patchNum) 
    {
	int nameStart=getPatchStart(patchNum);
	nameStart+=164; //offset of name in patch data
	String retname = new String();
	try {
	    StringBuffer s= new StringBuffer(new String(((Patch)p).sysex,nameStart,
							16,"US-ASCII"));
	    retname = s.toString();
	    while (retname.length() < 16)
		retname+=" ";
	    return retname;
	} catch (UnsupportedEncodingException ex) {return "-";}	  
    }
    
    protected void setPatchNum(int patchNum) {
    }

    public void setPatchName(Patch p,int patchNum, String name)
    {
	/* do nothing here, names can just be changed in single editor */
    }
 
    public void calculateChecksum (Patch p)
    {
    }		

    public void putPatch(Patch bank,Patch p,int patchNum) 
    { 
	if (!canHoldPatch(p))
	    {
		JOptionPane.showMessageDialog(null, "This type of patch does not fit in to this type of bank.","Error", JOptionPane.ERROR_MESSAGE); 
		return;
	    }
	
	p.sysex[7] = (byte)0x01;
	p.sysex[12] = (byte)patchNum;
	
	System.arraycopy(((Patch)p).sysex,0,((Patch)bank).sysex,getPatchStart(patchNum),270);
    }
    
    public Patch getPatch(Patch bank, int patchNum)
    {
	this.bank = bank;
	
	try{
	    byte [] sysex=new byte[270];
	    
	    System.arraycopy(((Patch)bank).sysex,getPatchStart(patchNum),sysex,0,270);
	    sysex[269]=(byte)0xF7;     
	    sysex[7] = (byte)0X00;
	    sysex[12] = (byte)patchNum;
	    
	    Patch p = new Patch(sysex, singleDriver);
	    
	    return p;
	}catch (Exception e) {ErrorMsg.reportError("Error","Error in Novation XioSynth Bank Driver",e);return null;}
    }
    
    protected void deletePatch(Patch single, int patchNum) {
	Patch p = singleDriver.createNewPatch();

	if (bank != null) {
	    putPatch(bank, p, patchNum);
	}
    }
    
    
    public Patch createNewPatch()
    {
	byte [] sysex = new byte[270 * 100];
	
	byte[] b = new byte [] {
	    (byte)0xF0,(byte)0x00,(byte)0x20,(byte)0x29,(byte)0x01,(byte)0x42,(byte)0x7F,(byte)0x01,(byte)0x00,(byte)0x10,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x38,(byte)0x19,(byte)0x00,(byte)0x00,(byte)0x40,(byte)0x00,(byte)0x15,(byte)0x40,(byte)0x40,(byte)0x42,(byte)0x40,(byte)0x40,(byte)0x40,(byte)0x40,(byte)0x40,(byte)0x40,(byte)0x40,(byte)0x42,(byte)0x40,(byte)0x40,(byte)0x40,(byte)0x40,(byte)0x40,
	    (byte)0x40,(byte)0x40,(byte)0x42,(byte)0x40,(byte)0x40,(byte)0x40,(byte)0x40,(byte)0x40,(byte)0x40,(byte)0x40,(byte)0x40,(byte)0x49,(byte)0x40,(byte)0x40,(byte)0x7F,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x40,(byte)0x7F,(byte)0x7F,(byte)0x40,(byte)0x40,(byte)0x40,(byte)0x40,(byte)0x40,(byte)0x40,(byte)0x40,(byte)0x40,(byte)0x00,(byte)0x40,(byte)0x40,
	    (byte)0x00,(byte)0x3C,(byte)0x40,(byte)0x02,(byte)0x5A,(byte)0x7F,(byte)0x28,(byte)0x40,(byte)0x02,(byte)0x41,(byte)0x00,(byte)0x41,(byte)0x00,(byte)0x44,(byte)0x00,(byte)0x32,(byte)0x44,(byte)0x00,(byte)0x32,(byte)0x00,(byte)0x00,(byte)0x3F,(byte)0x40,(byte)0x40,(byte)0x40,(byte)0x38,(byte)0x03,(byte)0x40,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x7F,(byte)0x40,(byte)0x40,(byte)0x40,(byte)0x0A,
	    (byte)0x00,(byte)0x00,(byte)0x00,(byte)0x40,(byte)0x64,(byte)0x00,(byte)0x40,(byte)0x40,(byte)0x00,(byte)0x40,(byte)0x7F,(byte)0x00,(byte)0x00,(byte)0x40,(byte)0x5A,(byte)0x00,(byte)0x40,(byte)0x14,(byte)0x00,(byte)0x4A,(byte)0x40,(byte)0x40,(byte)0x40,(byte)0x14,(byte)0x00,(byte)0x00,(byte)0x04,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x02,(byte)0x02,(byte)0x02,
	    (byte)0x01,(byte)0x01,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x40,(byte)0x40,(byte)0x40,(byte)0x40,(byte)0x40,(byte)0x40,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x40,(byte)0x49,(byte)0x6E,(byte)0x69,(byte)0x74,(byte)0x20,(byte)0x50,(byte)0x72,(byte)0x6F,(byte)0x67,(byte)0x72,(byte)0x61,(byte)0x6D,(byte)0x20,(byte)0x20,(byte)0x20,(byte)0x20,
	    (byte)0x00,(byte)0x00,(byte)0x40,(byte)0x40,(byte)0x40,(byte)0x40,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x07,(byte)0x02,(byte)0x00,(byte)0x7F,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x7F,(byte)0x40,(byte)0x40,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
	    (byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x7F,(byte)0x40,(byte)0x40,(byte)0x40,(byte)0x40,(byte)0x00,(byte)0x2C,(byte)0x00,(byte)0x40,(byte)0x20,(byte)0x00,(byte)0x7F,(byte)0x07,(byte)0x00,(byte)0x00,(byte)0x05,(byte)0x06,(byte)0x00,(byte)0x02,(byte)0x07,(byte)0x00,(byte)0x7F,(byte)0x10,(byte)0x40,(byte)0x40,(byte)0x3F,(byte)0x07,(byte)0x07,(byte)0x38,(byte)0x07,
	    (byte)0x07,(byte)0x07,(byte)0x07,(byte)0x07,(byte)0x00,(byte)0x3F,(byte)0x07,(byte)0x3F,(byte)0x00,(byte)0x3F,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0xF7
	};
	
	Patch p = new Patch(sysex, this);
	/* todo : replace this by an arraycopy */
	for (int i=0;i<100;i++) {
	    for (int j = 0; j < 270; j++) {
		if (j == 12)
		    sysex[i*270 +j] = (byte)i; /* this is the pg number */
		else 
		    sysex[i*270 +j] = b[j];
	    }
	}

	return p;
    }
}
