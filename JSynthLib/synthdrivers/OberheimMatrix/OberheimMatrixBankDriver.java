/*
 * @version $Id$
 */
package synthdrivers.OberheimMatrix;
import core.BankDriver;
import core.ErrorMsg;
import core.Patch;
public class OberheimMatrixBankDriver extends BankDriver
{

   public OberheimMatrixBankDriver()
   {
   super ("Bank","Brian Klock",100,5);
   sysexID= "F010060**";
   //inquiryID="F07E**06021006000200*********F7";
   patchSize=27500;
   patchNameStart=5;
   patchNameSize=8;
   deviceIDoffset=-1;
   bankNumbers =new String[] {"000 Bank","100 Bank"};
 
   patchNumbers = generateNumbers(0,99,"00-");
  singleSize=275;
  singleSysexID="F010060**";
  }
public int getPatchStart(int PatchNum)
{
	return PatchNum*275;
}

public void calculateChecksum(Patch p)
 {
   for (int i=0;i<100;i++)
     calculateChecksum(p,5+getPatchStart(i),272+getPatchStart(i),273+getPatchStart(i)); 
 
 }

public void calculateChecksum(Patch ip,int start,int end,int ofs)
  {
    Patch p = (Patch)ip;
    int i;
    int sum=0;
    for (i=start;i<=end;i++)
      if (i%2!=0) sum+=p.sysex[i]; else sum+=(p.sysex[i]*16);
    p.sysex[ofs]=(byte)(sum % 128);

 
  }
 
  public void setBankNum(int bankNum)
  {
      try{
	  send(new byte[] {
	      (byte)0xF0,(byte)0x10,(byte)0x06,(byte)0x0A,
	      (byte)bankNum,(byte)0xF7
	  });
      } catch (Exception e) {}
  }
  public void storePatch (Patch p, int bankNum,int patchNum)
  {   
   setBankNum(bankNum);
   for (int i=0;i<100;i++)
   {((Patch)p).sysex[3+getPatchStart(i)]=1;
   ((Patch)p).sysex[4+getPatchStart(i)]=(byte)i;}
   sendPatchWorker(p);
   
  }

 

  public String getPatchName(Patch p,int patchNum) {
          try {
            int start=getPatchStart(patchNum);
	    byte []b = new byte[8];
	    b[0]=((byte)(((Patch)p).sysex[start+5]+((Patch)p).sysex[start+6]*16));
	    b[1]=((byte)(((Patch)p).sysex[start+7]+((Patch)p).sysex[start+8]*16));
	    b[2]=((byte)(((Patch)p).sysex[start+9]+((Patch)p).sysex[start+10]*16));
	    b[3]=((byte)(((Patch)p).sysex[start+11]+((Patch)p).sysex[start+12]*16));
	    b[4]=((byte)(((Patch)p).sysex[start+13]+((Patch)p).sysex[start+14]*16));
	    b[5]=((byte)(((Patch)p).sysex[start+15]+((Patch)p).sysex[start+16]*16));
	    b[6]=((byte)(((Patch)p).sysex[start+17]+((Patch)p).sysex[start+18]*16));
	    b[7]=((byte)(((Patch)p).sysex[start+19]+((Patch)p).sysex[start+20]*16));
            StringBuffer s= new StringBuffer(new String(b,0,8,"US-ASCII"));
           return s.toString();
         } catch (Exception ex) {return "-";}
   }
  public void setPatchName(Patch p, int patchNum,String name)	
  {
	byte [] namebytes = new byte[32];
	try{
        int start=getPatchStart(patchNum);
	if (name.length()<8) name=name+"        ";
        namebytes=name.getBytes("US-ASCII");
	((Patch)p).sysex[start+5]=((byte)(namebytes[0]%16));
	((Patch)p).sysex[start+6]=((byte)(namebytes[0]/16));
	((Patch)p).sysex[start+7]=((byte)(namebytes[1]%16));
	((Patch)p).sysex[start+8]=((byte)(namebytes[1]/16));
	((Patch)p).sysex[start+9]=((byte)(namebytes[2]%16));
	((Patch)p).sysex[start+10]=((byte)(namebytes[2]/16));
	((Patch)p).sysex[start+11]=((byte)(namebytes[3]%16));
	((Patch)p).sysex[start+12]=((byte)(namebytes[3]/16));
	((Patch)p).sysex[start+13]=((byte)(namebytes[4]%16));
	((Patch)p).sysex[start+14]=((byte)(namebytes[4]/16));
	((Patch)p).sysex[start+15]=((byte)(namebytes[5]%16));
	((Patch)p).sysex[start+16]=((byte)(namebytes[5]/16));
	((Patch)p).sysex[start+17]=((byte)(namebytes[6]%16));
	((Patch)p).sysex[start+18]=((byte)(namebytes[6]/16));
	((Patch)p).sysex[start+19]=((byte)(namebytes[7]%16));
	((Patch)p).sysex[start+20]=((byte)(namebytes[7]/16));
	}catch (Exception e) {ErrorMsg.reportError("Error","Error in Matrix1000 Bank Driver",e);}
  }
public void putPatch(Patch bank,Patch p,int patchNum)
   { 
   if (!canHoldPatch(p))
       {ErrorMsg.reportError("Error", "This type of patch does not fit in to this type of bank."); return;}
                        
   System.arraycopy(((Patch)p).sysex,0,((Patch)bank).sysex,getPatchStart(patchNum),275);
   calculateChecksum(bank);
   }
  public Patch getPatch(Patch bank, int patchNum)
   {
  try{
     byte [] sysex=new byte[275];
     System.arraycopy(((Patch)bank).sysex,getPatchStart(patchNum),sysex,0,275);
     Patch p = new Patch(sysex, getDevice());
     p.getDriver().calculateChecksum(p);   
    return p;
    }catch (Exception e) {ErrorMsg.reportError("Error","Error in Matrix 1000 Bank Driver",e);return null;}
   }
  protected void sendPatch (Patch p)
   {
     byte []tmp=new byte[275];
    if (deviceIDoffset>0) ((Patch)p).sysex[deviceIDoffset]=(byte)(getChannel()-1);
    try {       
       for (int i=0;i<100;i++) 
       {
       System.arraycopy(((Patch)p).sysex,275*i,tmp,0,275);
       send(tmp);
       Thread.sleep(15);
       }
    }catch (Exception e) {ErrorMsg.reportError("Error","Unable to send Patch",e);}
   }
public Patch createNewPatch()
 {
	 byte [] sysex = new byte[27500];
	 for (int i=0;i<100;i++){
	 sysex[0+275*i]=(byte)0xF0; sysex[1+275*i]=(byte)0x10;sysex[2+275*i]=(byte)0x06;sysex[3+275*i]=(byte)0x0D;sysex[4+275*i]=(byte)0x00;
	 sysex[274+275*i]=(byte)0xF7;}
         Patch p = new Patch(sysex, this);
	 for (int i=0;i<100;i++)
	  setPatchName(p,i,"NewPatch");
	 calculateChecksum(p);	 
	 return p;
 }

}

