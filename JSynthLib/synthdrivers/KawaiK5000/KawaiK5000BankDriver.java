/*
 * @version $Id$
 */
package synthdrivers.KawaiK5000;
import core.*;

import java.io.*;
import javax.swing.*;
public class KawaiK5000BankDriver extends BankDriver
{

  // phil@muqus.com - see K5000W/S Midi Implementation, p23
  final static SysexHandler SYSEX_REQUEST_A_DUMP = new SysexHandler("F0 40 @@ 01 00 0A 00 00 00 F7");
  // N.B. Following message specified incorrectly in K5000W/2 Midi Implementation
  final static SysexHandler SYSEX_REQUEST_D_DUMP = new SysexHandler("F0 40 @@ 01 00 0A 00 02 00 F7");

   public int [] patchIndex = new int [129];
   public Patch indexedPatch;
   public KawaiK5000BankDriver()
   {
   super ("Bank","Brian Klock",128,4);
   sysexID="F040**21000A000*";
   deviceIDoffset=2;
   patchSize=0;
   numSysexMsgs = 1;                                        // phil@muqus.com
   bankNumbers =new String[] {"0-Bank A","1-------","2-------","3-Bank D"};
   patchNumbers=new String[] {"01-","02-","03-","04-","05-","06-","07-",
                              "08-","09-","10-","11-","12-","13-","14-","15-",
                              "16-","17-","18-","19-","20-","21-","22-","23-",
                              "24-","25-","26-","27-","28-","29-","30-","31-",
                              "32-","33-","34-","35-","36-","37-","38-","39-",
                              "40-","41-","42-","43-","44-","45-","46-","47-",
                              "48-","49-","50-","51-","52-","53-","54-","55-",
                              "56-","57-","58-","59-","60-","61-","62-","63-",
                              "64-","65-","66-","67-","68-","69-","70-","71-",
                              "72-","73-","74-","75-","76-","77-","78-","79-",
                              "80-","81-","82-","83-","84-","85-","86-","87-",
                              "88-","89-","90-","91-","92-","93-","94-","95-",
                              "96-","97-","98-","99-","100-","101-","102-","103-",
                              "104-","105-","106-","107-","108-","109-","110-","111-",
                              "112-","113-","114-","115-","116-","117-","118-","119-",
                              "120-","121-","122-","123-","124-","125-","126-","127-","128-" };

   singleSysexID="F040**20000A000*";
   singleSize=0;
  }

//K5000 Banks have a variable number of patches from 1-128. This funtion
//determines the number of patches in the bank
  public int numPatchesinBank(Patch p)
  {
    int num=0;
    for (int i=8;i<26;i++)
     {
       if ((p.sysex[i]&1)>0) num++;
       if ((p.sysex[i]&2)>0) num++;
       if ((p.sysex[i]&4)>0) num++;
       if ((p.sysex[i]&8)>0) num++;
       if ((p.sysex[i]&16)>0) num++;
       if ((p.sysex[i]&32)>0) num++;
       if ((p.sysex[i]&64)>0) num++;
     }
    return num;
  }
  public boolean patchExists(Patch p,int num)
   {
     int sub=p.sysex[8+(num/7)];
     if (num%7==0) return ((sub&1)>0);
     if (num%7==1) return ((sub&2)>0);
     if (num%7==2) return ((sub&4)>0);
     if (num%7==3) return ((sub&8)>0);
     if (num%7==4) return ((sub&16)>0);
     if (num%7==5) return ((sub&32)>0);
     if (num%7==6) return ((sub&64)>0);
     return false;
   }
  public void setPatchExists(Patch p, int num,boolean exists)
  {
     if (exists==patchExists(p,num)) return;
     int sub=p.sysex[8+(num/7)];
     System.out.println("bitmask was "+sub);
     if (num%7==0)  sub=sub^1;
     if (num%7==1)  sub=sub^2;
     if (num%7==2)  sub=sub^4;
     if (num%7==3)  sub=sub^8;
     if (num%7==4)  sub=sub^16;
     if (num%7==5)  sub=sub^32;
     if (num%7==6)  sub=sub^64;
     System.out.println("bitmask is "+sub);

     p.sysex[8+(num/7)]=(byte)sub;
  }
  public void generateIndex(Patch p)
   {
     int currentPatchStart=27;
     if (indexedPatch==p) return;
     for (int i=0;i<128;i++)
      {
        if (patchExists(p,i))
         {
           patchIndex[i]=currentPatchStart;
           int newPatchStart=currentPatchStart+(82+86*p.sysex[currentPatchStart+51]);
           int numWaveData=0;
           for (int j=0;j<p.sysex[currentPatchStart+51];j++)
             if (((p.sysex[81+currentPatchStart+29+(86*j)]&7)*128+
                   p.sysex[81+currentPatchStart+30+(86*j)])==512) numWaveData++;

           newPatchStart+=(numWaveData*806);
           currentPatchStart=newPatchStart;
         } else patchIndex[i]=0;

      }
     indexedPatch=p;
     patchIndex[128]=p.sysex.length-1;
   }

  public int getPatchStart(int patchNum)
   {ErrorMsg.reportStatus("K5kBankDriver:Calling old getPatchStart-- redirecting");
    return getPatchStart(indexedPatch,patchNum);}

  public int getPatchStart(Patch p,int patchNum)
   {
     generateIndex(p);
     return  patchIndex[patchNum];
   }

  public String getPatchName(IPatch ip)
   {return (((Patch)ip).sysex.length/1024)+" Kilobytes";}
  public String getPatchName(IPatch ip,int patchNum) {
  	Patch p = (Patch)ip;
     int nameStart=getPatchStart(p,patchNum);
     if (nameStart==0) return "[empty]";
     nameStart+=40; //offset of name in patch data
         try {
               StringBuffer s= new StringBuffer(new String(p.sysex,nameStart,
               8,"US-ASCII"));
               return s.toString();
             } catch (UnsupportedEncodingException ex) {return "-";}

  }


//----- Start phil@muqus.com

//----------------------------------------------------------------------------------------------------------------------
// KawaiK5000BankDriver->setPatchName(Patch, int, String)
//----------------------------------------------------------------------------------------------------------------------

  public void setPatchName(IPatch bank, int patchNum, String name) {
    IPatch p = (IPatch)getPatch(bank, patchNum);
    Driver singleDriver = p.getDriver();
    singleDriver.setPatchName(p, name);
    singleDriver.calculateChecksum(p);
    putPatch(bank, p, patchNum);
  }

//----- End phil@muqus.com

  public void calculateChecksum(IPatch p,int start,int end,int ofs)
  {

  }


  public void calculateChecksum (IPatch p)
   {
   }

//----- Start phil@muqus.com

//----------------------------------------------------------------------------------------------------------------------
// KawaiK5000BankDriver->putPatch
//----------------------------------------------------------------------------------------------------------------------

  public void putPatch(IPatch b, IPatch p, int patchNum) {
  	Patch bank = (Patch)b;
    if (!canHoldPatch(p)) {
      JOptionPane.showMessageDialog(null, "This type of patch does not fit in to this type of bank.","Error", JOptionPane.ERROR_MESSAGE);
      return;
    }

    // Update index
    generateIndex(bank);

    // Find next existing patch in bank, this gives us the index at which the data needs to be inserted
    int nextPatchNum = patchNum;
    while(patchIndex[nextPatchNum] == 0)
      nextPatchNum++;
    System.out.println("Insert at patchNum: " + nextPatchNum + " | index: " + patchIndex[nextPatchNum]);

    //  p.sysex <DATA> starts at 9, ends just before trailing F7
    ((Patch)bank).sysex = Utility.byteArrayReplace(((Patch)bank).sysex, patchIndex[nextPatchNum], patchSize(bank, patchNum), ((Patch)p).sysex, 9, ((Patch)p).sysex.length - 10);

    // Update index and checksum
    indexedPatch = null;
    setPatchExists(bank, patchNum, true);
    generateIndex(bank);
    calculateChecksum(bank);
  }

//----- End phil@muqus.com

  public IPatch getPatch(IPatch b, int patchNum)
   {
  	Patch bank = (Patch)b;
  try{
     generateIndex(bank);
     int patchSize;
     if (patchIndex[patchNum]==0) return null;
     int i=patchNum+1;
     while((i<128)&&(patchIndex[i]==0)) i++;
     patchSize=patchIndex[i]-patchIndex[patchNum];
     patchSize+=10;
     byte [] sysex=new byte[patchSize];
     sysex[00]=(byte)0xF0;sysex[01]=(byte)0x40;sysex[02]=(byte)0x00;
     sysex[03]=(byte)0x20;sysex[04]=(byte)0x00;sysex[05]=(byte)0x0A;
     sysex[06]=(byte)0x00;sysex[07]=(byte)0x00;sysex[8]=(byte)0x00;
     sysex[patchSize-1]=(byte)0xF7;
     System.arraycopy(((Patch)bank).sysex,getPatchStart(patchNum),sysex,9,patchSize-10);
     IPatch p = new Patch(sysex, getDevice());
     p.getDriver().calculateChecksum(p);
    return p;
    }catch (Exception e) {ErrorMsg.reportError("Error","Error in K5000 Bank Driver",e);return null;}
   }
public IPatch createNewPatch()
 {
         byte [] sysex = new byte[28];
	 sysex[0]=(byte)0xF0; sysex[1]=(byte)0x40;sysex[2]=(byte)0x00;sysex[3]=(byte)0x21;sysex[4]=(byte)0x00;
          sysex[5]=(byte)0x0a; sysex[6]=(byte)0x00;sysex[27]=(byte)0xF7;
         IPatch p = new Patch(sysex, this);
	 return p;
 }

  public void storePatch (IPatch p, int bankNum,int patchNum)
    {
     if (bankNum==0)((Patch)p).sysex[7]=0;   //bank a
     if (bankNum==3)((Patch)p).sysex[7]=2;   //bank d
      PatchEdit.showWaitDialog();
      setBankNum(bankNum);
      sendPatchWorker(p);
      PatchEdit.hideWaitDialog();  // phil@muqus.com

    };

//----- Start phil@muqus.com

//----------------------------------------------------------------------------------------------------------------------
// KawaiK5000BankDriver->deletePatch
//----------------------------------------------------------------------------------------------------------------------

  public void deletePatch (IPatch b, int patchNum) {
  	Patch bank = (Patch)b;
    if (!patchExists(bank, patchNum)) {
      JOptionPane.showMessageDialog(null, "Patch does not exist, so can not be deleted.", "Error", JOptionPane.ERROR_MESSAGE);
      return;
    }

    // Update index
    generateIndex(bank);

    // Delete patch from sysex
    ((Patch)bank).sysex = Utility.byteArrayDelete(((Patch)bank).sysex, patchIndex[patchNum], patchSize(bank, patchNum));

    // Update index and checksum
    indexedPatch = null;
    setPatchExists(bank, patchNum, false);
    System.out.println("NumPatches = " + numPatchesinBank(bank));
    generateIndex(bank);
    calculateChecksum(bank);
  }

//----------------------------------------------------------------------------------------------------------------------
// KawaiK5000BankDriver->patchSize
// Returns: Size of patch, patchNum ... or 0 if patch does not exist
//----------------------------------------------------------------------------------------------------------------------

  public int patchSize (Patch bank, int patchNum) {
    if (patchExists(bank, patchNum)) {
      int i = patchNum+1;
      while((i < 128) && (patchIndex[i] == 0))
        i++;
      return patchIndex[i] - patchIndex[patchNum];
    } else {
      return 0;
    }
  }

//----------------------------------------------------------------------------------------------------------------------
// KawaiK5000BankDriver->requestPatchDump
//----------------------------------------------------------------------------------------------------------------------

  public void requestPatchDump(int bankNum, int patchNum) {
    if (bankNum == 0)
      send(SYSEX_REQUEST_A_DUMP.toSysexMessage(getChannel()));
    else
      send(SYSEX_REQUEST_D_DUMP.toSysexMessage(getChannel()));
  }

//----- End phil@muqus.com
}
