package synthdrivers.RolandGP16;
import core.*;

import java.io.*;
import javax.swing.*;

/**
 * Group driver for ROLAND GP16.
 * @version $Id$
 */
public class RolandGP16GroupDriver extends BankDriver {
    /** Header Size */
    private static final int HSIZE = 5;
    /** Single Patch size */
    private static final int SSIZE = 121;
    /** The number of bank patches in a group patch. */
    private static final int NS = 8;
    /** The sysex message sent when requesting a patch (from a group). */
    private static final SysexHandler SYS_REQ = 
    	new SysexHandler("F0 41 @@ 2A 11 0D *patchnumber* 00 00 00 75 *checksum* F7");
    /** Time to sleep when doing sysex data transfers. */
    private static final int sleepTime = 100;

/** The constructor. */    
    public RolandGP16GroupDriver() {
	super("Group", "Mikael Kurula", NS, 2);

	sysexID="F041**2A";
	deviceIDoffset = 2;
	
	bankNumbers = new String[] {"Group A","Group B"};
 	patchNumbers = new String[NS * 1];
	System.arraycopy(DriverUtil.generateNumbers(1, NS, "Bank ##"), 0, patchNumbers,  0, NS);   
	
	singleSysexID = sysexID;
	singleSize = (HSIZE + SSIZE + 1) * 8;
	// To distinguish from a bank, which has the same sysexID
	patchSize = singleSize * NS;
    }

/** Return the starting index of a given bank in the group. */
    public int getPatchStart(int patchNum) {
	return singleSize * patchNum;
    }

/** Get bank names in group for group edit view. */
   public String getPatchName(IPatch p,int patchNum) {
     int nameStart=getPatchStart(patchNum);
     nameStart+=108; //offset of name in patch data
         try {
               StringBuffer s= new StringBuffer(new String(((Patch)p).sysex,nameStart, 16,"US-ASCII"));
               return s.toString();
             } catch (UnsupportedEncodingException ex) {return "-";}   
  }
    
/** Calculate the checksum for all patches in the group. */
  public void calculateChecksum (IPatch p)
   {
     for (int i=0;i<8*NS;i++)
      calculateChecksum (p,i*127+5,i*127+124,i*127+125);
   }                                     

/** Insert a given bank into a given position of a given group. */
  public void putPatch(IPatch bank,IPatch p,int patchNum)
   { 
   if (!canHoldPatch(p))
       {JOptionPane.showMessageDialog(null, "This type of patch does not fit in to this type of bank.","Error", JOptionPane.ERROR_MESSAGE); return;}
                        
   System.arraycopy(((Patch)p).sysex,0,((Patch)bank).sysex,getPatchStart(patchNum),singleSize);
   calculateChecksum(bank);
   }
   
/** Extract a given bank from a given group. */
  public IPatch getPatch(IPatch bank, int patchNum)
  {
     byte [] sysex=new byte[singleSize];
     System.arraycopy(((Patch)bank).sysex,getPatchStart(patchNum),sysex,0,singleSize);
  try{
	    // pass Single Driver !!!FIXIT!!!
     IPatch p = new Patch(sysex,getDevice());
     p.getDriver().calculateChecksum(p);   
    return p;
    }catch (Exception e) {ErrorMsg.reportError("Error","Error in GP16 Bank Driver",e);return null;}
   }

/** A nice group dump of the GP-16 is just all patches dumped one by one, with correct memory address. */
    public void requestPatchDump(int bankNum, int patchNum){
	for (int i=0;i<NS;i++)
	    requestSingleBankDump(bankNum,i);}    

/** Send the group back as it was received. */
    public void storePatch (IPatch group, int groupNum,int bankNum)
    { 
 	for (int i=0;i<NS;i++){
		IPatch p=getPatch(group,i);
		storeSingleBank(p,groupNum,i);}
    }

/** Worker for requestPatchDump. */
    public void requestSingleBankDump(int bankNum, int patchNum){
  	for (int i=0;i<8;i++)
	{
		try {Thread.sleep(sleepTime); } catch (Exception e){}
		SysexHandler.NameValue nVs[] = new SysexHandler.NameValue[2];
		nVs[0] = new SysexHandler.NameValue("patchnumber", bankNum * 64 + patchNum * 8 + i);
		nVs[1] = new SysexHandler.NameValue("checksum", 0);
		Patch p = new Patch(SYS_REQ.toByteArray(getChannel(),nVs));
		calculateChecksum(p,5,10,11);	// the gp-16 requires correct checksum when requesting a patch
		send(p.sysex);
		try {Thread.sleep(sleepTime); } catch (Exception e){}
	}
    }
    
/** Worker for storePatch. */
    public void storeSingleBank (IPatch p, int groupNum,int bankNum)
    {   
    	byte[] gsysex = ((Patch)p).sysex;
	byte[] ggsysex = new byte[127];
	for (int i=0;i<8;i++){
		gsysex[127 * i + 5]=(byte)0x0D;
		gsysex[127 * i + 6]=(byte)(groupNum * 64 + bankNum * 8 + i);
		gsysex[127 * i + 7]=(byte)0x00;
		System.arraycopy(gsysex,127 * i,ggsysex,0,127);
	sendPatchWorker(new Patch(ggsysex,this));
	try {Thread.sleep(sleepTime); } catch (Exception e){}}   
    }

/** Create a new group, that conforms to the format of the GP-16. */
 public IPatch createNewPatch()
  {
     	 byte [] sysex = new byte[NS * singleSize];
	 
	 RolandGP16SingleDriver patchCreator = new RolandGP16SingleDriver();
	 IPatch blankPatch = patchCreator.createNewPatch();
	 for (int i=0;i<NS*8;i++)
	     System.arraycopy(((Patch)blankPatch).sysex,0,sysex,getPatchStart(i)/8,singleSize/8);
	 IPatch p = new Patch(sysex, this);
 	 calculateChecksum(p);	 
	 return p;
 }
  
/** The name string of the GP-16 is 16 characters long. */
  public void deletePatch (IPatch p, int patchNum)
   {
     setPatchName(p,patchNum,"                ");
   }

/** Smarter group naming, name the group after the first patch in it. */
  public String getPatchName(IPatch p) {return getPatchName(p,0);}

}

