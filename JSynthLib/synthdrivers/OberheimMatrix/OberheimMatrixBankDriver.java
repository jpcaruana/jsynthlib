package synthdrivers.OberheimMatrix;
import core.*;
import javax.swing.*;
public class OberheimMatrixBankDriver extends BankDriver
{

   public OberheimMatrixBankDriver()
   {
   manufacturer="Oberheim";
   model="Matrix 6/6R/1000";
   patchType="Bank";
   id="Matrix";
   sysexID= "F010060**";
   //inquiryID="F07E**06021006000200*********F7";
   patchSize=27500;
   patchNameStart=5;
   patchNameSize=8;
   deviceIDoffset=-1;
   bankNumbers =new String[] {"000 Bank","100 Bank"};
 
   patchNumbers=new String[] {"00-","01-","02-","03-","04-","05-","06-","07-",
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
                              "96-","97-","98-","99-"};
  numPatches=patchNumbers.length;
  numColumns=5;
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

public void calculateChecksum(Patch p,int start,int end,int ofs)
  {
    int i;
    int sum=0;
    for (i=start;i<=end;i++)
      if (i%2!=0) sum+=p.sysex[i]; else sum+=(p.sysex[i]*16);
    p.sysex[ofs]=(byte)(sum % 128);

 
  }
 
  public void setBankNum(int bankNum)
  {
       
	  try{  PatchEdit.MidiOut.writeLongMessage(port,new byte[] {(byte)0xF0,(byte)0x10,(byte)0x06,(byte)0x0A,
	  (byte)bankNum,(byte)0xF7});} catch (Exception e) {}
  }
  public void storePatch (Patch p, int bankNum,int patchNum)
  {   
   setBankNum(bankNum);
   for (int i=0;i<100;i++)
   {p.sysex[3+getPatchStart(i)]=1;
   p.sysex[4+getPatchStart(i)]=(byte)i;}
   sendPatchWorker(p);
   
  }

 

  public String getPatchName(Patch p,int patchNum) {
          try {
            int start=getPatchStart(patchNum);
	    byte []b = new byte[8];
	    b[0]=((byte)(p.sysex[start+5]+p.sysex[start+6]*16));
	    b[1]=((byte)(p.sysex[start+7]+p.sysex[start+8]*16));
	    b[2]=((byte)(p.sysex[start+9]+p.sysex[start+10]*16));
	    b[3]=((byte)(p.sysex[start+11]+p.sysex[start+12]*16));
	    b[4]=((byte)(p.sysex[start+13]+p.sysex[start+14]*16));
	    b[5]=((byte)(p.sysex[start+15]+p.sysex[start+16]*16));
	    b[6]=((byte)(p.sysex[start+17]+p.sysex[start+18]*16));
	    b[7]=((byte)(p.sysex[start+19]+p.sysex[start+20]*16));
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
	p.sysex[start+5]=((byte)(namebytes[0]%16));
	p.sysex[start+6]=((byte)(namebytes[0]/16));
	p.sysex[start+7]=((byte)(namebytes[1]%16));
	p.sysex[start+8]=((byte)(namebytes[1]/16));
	p.sysex[start+9]=((byte)(namebytes[2]%16));
	p.sysex[start+10]=((byte)(namebytes[2]/16));
	p.sysex[start+11]=((byte)(namebytes[3]%16));
	p.sysex[start+12]=((byte)(namebytes[3]/16));
	p.sysex[start+13]=((byte)(namebytes[4]%16));
	p.sysex[start+14]=((byte)(namebytes[4]/16));
	p.sysex[start+15]=((byte)(namebytes[5]%16));
	p.sysex[start+16]=((byte)(namebytes[5]/16));
	p.sysex[start+17]=((byte)(namebytes[6]%16));
	p.sysex[start+18]=((byte)(namebytes[6]/16));
	p.sysex[start+19]=((byte)(namebytes[7]%16));
	p.sysex[start+20]=((byte)(namebytes[7]/16));
	}catch (Exception e) {ErrorMsg.reportError("Error","Error in Matrix1000 Bank Driver",e);}
  }
public void putPatch(Patch bank,Patch p,int patchNum)
   { 
   if (!canHoldPatch(p))
       {ErrorMsg.reportError("Error", "This type of patch does not fit in to this type of bank."); return;}
                        
   System.arraycopy(p.sysex,0,bank.sysex,getPatchStart(patchNum),275);
   calculateChecksum(bank);
   }
  public Patch getPatch(Patch bank, int patchNum)
   {
  try{
     byte [] sysex=new byte[275];
     System.arraycopy(bank.sysex,getPatchStart(patchNum),sysex,0,275);
     Patch p = new Patch(sysex);
     p.ChooseDriver();
     PatchEdit.getDriver(p.deviceNum,p.driverNum).calculateChecksum(p);   
    return p;
    }catch (Exception e) {ErrorMsg.reportError("Error","Error in Matrix 1000 Bank Driver",e);return null;}
   }
  protected void sendPatchWorker (Patch p)
   {
     byte []tmp=new byte[275];
    if (deviceIDoffset>0) p.sysex[deviceIDoffset]=(byte)(channel-1);
    try {       
       for (int i=0;i<100;i++) 
       {
       System.arraycopy(p.sysex,275*i,tmp,0,275);
       PatchEdit.MidiOut.writeLongMessage(port,tmp);
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
         Patch p = new Patch(sysex);
	 p.ChooseDriver();
	 for (int i=0;i<100;i++)
	  setPatchName(p,i,"NewPatch");
	 calculateChecksum(p);	 
	 return p;
 }

}
