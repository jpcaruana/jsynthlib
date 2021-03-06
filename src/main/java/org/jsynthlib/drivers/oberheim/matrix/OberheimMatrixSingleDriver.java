/*
 * @version $Id$
 */
package org.jsynthlib.drivers.oberheim.matrix;
import org.jsynthlib.core.Driver;
import org.jsynthlib.core.JSLFrame;
import org.jsynthlib.core.Patch;
import org.jsynthlib.core.SysexHandler;
public class OberheimMatrixSingleDriver extends Driver
{

   public OberheimMatrixSingleDriver()
   {
   super ("Single","Brian Klock");
   sysexID= "F010060**";
//   inquiryID="F07E**0602100600*************F7";
    sysexRequestDump=new SysexHandler("F0 10 06 04 01 *patchNum* F7");

   patchSize=275;
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
   }
public void calculateChecksum(Patch p)
 {
   calculateChecksum(p,5,272,273); 
 
 }

protected void calculateChecksum(Patch p,int start,int end,int ofs)
  {
    int sum=0;
    for (int i=start;i<=end;i++)
      if (i%2!=0) sum+=p.sysex[i]; else sum+=(p.sysex[i]*16);
    p.sysex[ofs]=(byte)(sum % 128);

  }
 
  public void setBankNum(int bankNum)
  {
        try {
	    send(new byte[] {
		(byte)0xF0,(byte)0x10,(byte)0x06,(byte)0x0A,
		(byte)bankNum,(byte)0xF7
	    });
	} catch (Exception e) {}
  }
  public void storePatch (Patch p, int bankNum,int patchNum)
  {   
   setBankNum(bankNum);
   setPatchNum(patchNum);
   ((Patch)p).sysex[3]=1;
   ((Patch)p).sysex[4]=(byte)patchNum;
   sendPatchWorker(p);
   
  }

  public void sendPatch (Patch p)
  {
    ((Patch)p).sysex[3]=0x0D;
    ((Patch)p).sysex[4]=0;
    sendPatchWorker(p);
  }

  public String getPatchName(Patch p) {
  	Patch ip = (Patch)p;
  	try {
            byte []b = new byte[8];
	    b[0]=((byte)(ip.sysex[5]+ip.sysex[6]*16));
	    b[1]=((byte)(ip.sysex[7]+ip.sysex[8]*16));
	    b[2]=((byte)(ip.sysex[9]+ip.sysex[10]*16));
	    b[3]=((byte)(ip.sysex[11]+ip.sysex[12]*16));
	    b[4]=((byte)(ip.sysex[13]+ip.sysex[14]*16));
	    b[5]=((byte)(ip.sysex[15]+ip.sysex[16]*16));
	    b[6]=((byte)(ip.sysex[17]+ip.sysex[18]*16));
	    b[7]=((byte)(ip.sysex[19]+ip.sysex[20]*16));
            StringBuffer s= new StringBuffer(new String(b,0,8,"US-ASCII"));
           return s.toString();
         } catch (Exception ex) {return "-";}
   }
  public void setPatchName(Patch p, String name)	
  {
	byte [] namebytes = new byte[32];
	try{
        if (name.length()<8) name=name+"        ";
        namebytes=name.getBytes("US-ASCII");
	((Patch)p).sysex[5]=((byte)(namebytes[0]%16));
	((Patch)p).sysex[6]=((byte)(namebytes[0]/16));
	((Patch)p).sysex[7]=((byte)(namebytes[1]%16));
	((Patch)p).sysex[8]=((byte)(namebytes[1]/16));
	((Patch)p).sysex[9]=((byte)(namebytes[2]%16));
	((Patch)p).sysex[10]=((byte)(namebytes[2]/16));
	((Patch)p).sysex[11]=((byte)(namebytes[3]%16));
	((Patch)p).sysex[12]=((byte)(namebytes[3]/16));
	((Patch)p).sysex[13]=((byte)(namebytes[4]%16));
	((Patch)p).sysex[14]=((byte)(namebytes[4]/16));
	((Patch)p).sysex[15]=((byte)(namebytes[5]%16));
	((Patch)p).sysex[16]=((byte)(namebytes[5]/16));
	((Patch)p).sysex[17]=((byte)(namebytes[6]%16));
	((Patch)p).sysex[18]=((byte)(namebytes[6]/16));
	((Patch)p).sysex[19]=((byte)(namebytes[7]%16));
	((Patch)p).sysex[20]=((byte)(namebytes[7]/16));
	}catch (Exception e) {}
  }
public Patch createNewPatch()
 {
	 byte [] sysex = new byte[275];
	 sysex[0]=(byte)0xF0; sysex[1]=(byte)0x10;sysex[2]=(byte)0x06;sysex[3]=(byte)0x0D;sysex[4]=(byte)0x00;
	 sysex[274]=(byte)0xF7;
         Patch p = new Patch(sysex, this);
	   setPatchName(p,"NewPatch");
	 calculateChecksum(p);	 
	 return p;
 }
public JSLFrame editPatch(Patch p)
 {
     return new OberheimMatrixSingleEditor((Patch)p);
 }
}

