package synthdrivers.YamahaTX81z;
import core.*;
import java.io.*;
import javax.swing.*;
public class YamahaTX81zBankDriver extends BankDriver
{

   public YamahaTX81zBankDriver()
   {
   manufacturer="Yamaha";
   model="TX81z";
   patchType="Bank";
   id="TX81z";
   sysexID="F043**04*000";
   deviceIDoffset=2;
   bankNumbers =new String[] {"0-Internal"};
   patchNumbers=new String[] {"I01","I02","I03","I04","I05","I06","I07","I08",
                              "I09","I10","I11","I12","I13","I14","I15","I16", 
                              "I17","I18","I19","I20","I21","I22","I23","I24",
                              "I25","I26","I27","I28","I29","I30","I31","I32"};  
   numPatches=patchNumbers.length;
   numColumns=4;
   singleSize=142;
   singleSysexID="F043**7E00214C4D2020383937364145";

  }

  public int getPatchStart(int patchNum)
   {
     int start=(128*patchNum);
     start+=6;  //sysex header
     
   return start;
   }
  public String getPatchName(Patch p,int patchNum) {
     int nameStart=getPatchStart(patchNum);
     nameStart+=57; //offset of name in patch data
         try {
               StringBuffer s= new StringBuffer(new String(p.sysex,nameStart,
               10,"US-ASCII"));
               return s.toString();
             } catch (UnsupportedEncodingException ex) {return "-";}   
     
  }

  public void setPatchName(Patch p,int patchNum, String name)
  {
     patchNameSize=10;
     patchNameStart=getPatchStart(patchNum)+57;
    
    if (name.length()<patchNameSize) name=name+"            ";
    byte [] namebytes = new byte [64];
    try {
         namebytes=name.getBytes("US-ASCII");
         for (int i=0;i<patchNameSize;i++)
           p.sysex[patchNameStart+i]=namebytes[i];

        } catch (UnsupportedEncodingException ex) {return;}
    
  }
 


  public void calculateChecksum (Patch p)
   {calculateChecksum (p,6,4101,4102);
 
   }                                     

  public void putPatch(Patch bank,Patch p,int patchNum)
   { 
   if (!canHoldPatch(p))
       {JOptionPane.showMessageDialog(null, "This type of patch does not fit in to this type of bank.","Error", JOptionPane.ERROR_MESSAGE); return;}
                        
   bank.sysex[getPatchStart(patchNum)+0]=(byte)((p.sysex[47]));               //AR
   bank.sysex[getPatchStart(patchNum)+1]=(byte)((p.sysex[48]));               //D1r
   bank.sysex[getPatchStart(patchNum)+2]=(byte)((p.sysex[49]));               //D2r
   bank.sysex[getPatchStart(patchNum)+3]=(byte)((p.sysex[50]));               //RR
   bank.sysex[getPatchStart(patchNum)+4]=(byte)((p.sysex[51]));               //D1L
   bank.sysex[getPatchStart(patchNum)+5]=(byte)((p.sysex[52]));               //LS
   bank.sysex[getPatchStart(patchNum)+6]=(byte)((p.sysex[55]*64+p.sysex[54]*8+p.sysex[56]));//ame ebs kvs
   bank.sysex[getPatchStart(patchNum)+7]=(byte)((p.sysex[57]));               //out
   bank.sysex[getPatchStart(patchNum)+8]=(byte)((p.sysex[58]));               //freq
   bank.sysex[getPatchStart(patchNum)+9]=(byte)((p.sysex[53]*8+p.sysex[59])); //rs dbt
   
   bank.sysex[getPatchStart(patchNum)+10]=(byte)((p.sysex[47+13*1]));               //AR
   bank.sysex[getPatchStart(patchNum)+11]=(byte)((p.sysex[48+13*1]));               //D1r
   bank.sysex[getPatchStart(patchNum)+12]=(byte)((p.sysex[49+13*1]));               //D2r
   bank.sysex[getPatchStart(patchNum)+13]=(byte)((p.sysex[50+13*1]));               //RR
   bank.sysex[getPatchStart(patchNum)+14]=(byte)((p.sysex[51+13*1]));               //D1L
   bank.sysex[getPatchStart(patchNum)+15]=(byte)((p.sysex[52+13*1]));               //LS
   bank.sysex[getPatchStart(patchNum)+16]=(byte)((p.sysex[55+13*1]*64+p.sysex[54+13*1]*8+p.sysex[56+13*1]));//ame ebs kvs
   bank.sysex[getPatchStart(patchNum)+17]=(byte)((p.sysex[57+13*1]));               //out
   bank.sysex[getPatchStart(patchNum)+18]=(byte)((p.sysex[58+13*1]));               //freq
   bank.sysex[getPatchStart(patchNum)+19]=(byte)((p.sysex[53+13*1]*8+p.sysex[59+13*1])); //rs dbt
   
   bank.sysex[getPatchStart(patchNum)+20]=(byte)((p.sysex[47+13*2]));               //AR
   bank.sysex[getPatchStart(patchNum)+21]=(byte)((p.sysex[48+13*2]));               //D1r
   bank.sysex[getPatchStart(patchNum)+22]=(byte)((p.sysex[49+13*2]));               //D2r
   bank.sysex[getPatchStart(patchNum)+23]=(byte)((p.sysex[50+13*2]));               //RR
   bank.sysex[getPatchStart(patchNum)+24]=(byte)((p.sysex[51+13*2]));               //D1L
   bank.sysex[getPatchStart(patchNum)+25]=(byte)((p.sysex[52+13*2]));               //LS
   bank.sysex[getPatchStart(patchNum)+26]=(byte)((p.sysex[55+13*2]*64+p.sysex[54+13*2]*8+p.sysex[56+13*2]));//ame ebs kvs
   bank.sysex[getPatchStart(patchNum)+27]=(byte)((p.sysex[57+13*2]));               //out
   bank.sysex[getPatchStart(patchNum)+28]=(byte)((p.sysex[58+13*2]));               //freq
   bank.sysex[getPatchStart(patchNum)+29]=(byte)((p.sysex[53+13*2]*8+p.sysex[59+13*2])); //rs dbt
  
   bank.sysex[getPatchStart(patchNum)+30]=(byte)((p.sysex[47+13*3]));               //AR
   bank.sysex[getPatchStart(patchNum)+31]=(byte)((p.sysex[48+13*3]));               //D1r
   bank.sysex[getPatchStart(patchNum)+32]=(byte)((p.sysex[49+13*3]));               //D2r
   bank.sysex[getPatchStart(patchNum)+33]=(byte)((p.sysex[50+13*3]));               //RR
   bank.sysex[getPatchStart(patchNum)+34]=(byte)((p.sysex[51+13*3]));               //D1L
   bank.sysex[getPatchStart(patchNum)+35]=(byte)((p.sysex[52+13*3]));               //LS
   bank.sysex[getPatchStart(patchNum)+36]=(byte)((p.sysex[55+13*3]*64+p.sysex[54+13*3]*8+p.sysex[56+13*3]));//ame ebs kvs
   bank.sysex[getPatchStart(patchNum)+37]=(byte)((p.sysex[57+13*3]));               //out
   bank.sysex[getPatchStart(patchNum)+38]=(byte)((p.sysex[58+13*3]));               //freq
   bank.sysex[getPatchStart(patchNum)+39]=(byte)((p.sysex[53+13*3]*8+p.sysex[59+13*3])); //rs dbt 
 
   bank.sysex[getPatchStart(patchNum)+40]=(byte)((p.sysex[105]*64+p.sysex[100]*8+p.sysex[99]));//sync fbl alg
   bank.sysex[getPatchStart(patchNum)+41]=(byte)((p.sysex[101]));               //lfs   
   bank.sysex[getPatchStart(patchNum)+42]=(byte)((p.sysex[102]));               //lfd   
   bank.sysex[getPatchStart(patchNum)+43]=(byte)((p.sysex[103]));               //pmd   
   bank.sysex[getPatchStart(patchNum)+44]=(byte)((p.sysex[104]));               //amd   
   bank.sysex[getPatchStart(patchNum)+45]=(byte)((p.sysex[107]*16+p.sysex[108]*4+p.sysex[106]));//pms ams lfw
   bank.sysex[getPatchStart(patchNum)+46]=(byte)((p.sysex[109]));               //traspose  
   bank.sysex[getPatchStart(patchNum)+47]=(byte)((p.sysex[111]));               //pbr   
   bank.sysex[getPatchStart(patchNum)+48]=(byte)((p.sysex[117]*16+p.sysex[110]*8+
   				                  p.sysex[115]*4+p.sysex[116]*2+p.sysex[112]));//ch mo su po pm
   bank.sysex[getPatchStart(patchNum)+49]=(byte)((p.sysex[113]));               //porta time   
   bank.sysex[getPatchStart(patchNum)+50]=(byte)((p.sysex[114]));               //footcontrol     
   
   System.arraycopy(p.sysex,118,bank.sysex,getPatchStart(patchNum)+51,22);

  bank.sysex[getPatchStart(patchNum)+73]=(byte)((p.sysex[20]*16+p.sysex[16]*8+p.sysex[17]));//egsft,fix,fixrg
  bank.sysex[getPatchStart(patchNum)+74]=(byte)((p.sysex[19]*16+p.sysex[18]));//osw fine 
  bank.sysex[getPatchStart(patchNum)+75]=(byte)((p.sysex[20+5]*16+p.sysex[16+5]*8+p.sysex[17+5]));//egsft,fix,fixrg
  bank.sysex[getPatchStart(patchNum)+76]=(byte)((p.sysex[19+5]*16+p.sysex[18+5]));//osw fine 
  bank.sysex[getPatchStart(patchNum)+77]=(byte)((p.sysex[20+10]*16+p.sysex[16+10]*8+p.sysex[17+10]));//egsft,fix,fixrg
  bank.sysex[getPatchStart(patchNum)+78]=(byte)((p.sysex[19+10]*16+p.sysex[18+10]));//osw fine 
  bank.sysex[getPatchStart(patchNum)+79]=(byte)((p.sysex[20+15]*16+p.sysex[16+15]*8+p.sysex[17+15]));//egsft,fix,fixrg
  bank.sysex[getPatchStart(patchNum)+80]=(byte)((p.sysex[19+15]*16+p.sysex[18+15]));//osw fine 
  bank.sysex[getPatchStart(patchNum)+81]=(byte)((p.sysex[36]));               //pbr   
  bank.sysex[getPatchStart(patchNum)+82]=(byte)((p.sysex[37]));               //pbr   
  bank.sysex[getPatchStart(patchNum)+83]=(byte)((p.sysex[38]));               //pbr   
  
   calculateChecksum(bank);
   }
  public Patch getPatch(Patch bank, int patchNum)
   {
  try{
     byte [] sysex=new byte[142];
 //First create ACED Data
     sysex[00]=(byte)0xF0;sysex[01]=(byte)0x43;sysex[02]=(byte)0x00;
     sysex[03]=(byte)0x7E;sysex[04]=(byte)0x00;sysex[05]=(byte)0x21;
     sysex[06]=(byte)0x4C;sysex[07]=(byte)0x4D;sysex[8]=(byte)0x20;
     sysex[9]=(byte)0x20;sysex[10]=(byte)0x38;sysex[11]=(byte)0x39;
     sysex[12]=(byte)0x37;sysex[13]=(byte)0x36;sysex[14]=(byte)0x41;
     sysex[15]=(byte)0x45; 
 
     sysex[16]=(byte)((bank.sysex[getPatchStart(patchNum)+73]  & 8) / 8);    //FIX
     sysex[17]=(byte)((bank.sysex[getPatchStart(patchNum)+73]  & 7));       //FixRG
     sysex[18]=(byte)((bank.sysex[getPatchStart(patchNum)+74]  & 15));       //FreqRangeFine
     sysex[19]=(byte)((bank.sysex[getPatchStart(patchNum)+74]  & 112)/16);       //Operator WaveForm
     sysex[20]=(byte)((bank.sysex[getPatchStart(patchNum)+73]  & 48)/16);       //EGShift
      
     sysex[21]=(byte)((bank.sysex[getPatchStart(patchNum)+75]  & 8) / 8);    //FIX
     sysex[22]=(byte)((bank.sysex[getPatchStart(patchNum)+75]  & 7));       //FixRG
     sysex[23]=(byte)((bank.sysex[getPatchStart(patchNum)+76]  & 15));       //FreqRangeFine
     sysex[24]=(byte)((bank.sysex[getPatchStart(patchNum)+76]  & 112)/16);       //Operator WaveForm
     sysex[25]=(byte)((bank.sysex[getPatchStart(patchNum)+75]  & 48)/16);       //EGShift
   
     sysex[26]=(byte)((bank.sysex[getPatchStart(patchNum)+77]  & 8) / 8);    //FIX
     sysex[27]=(byte)((bank.sysex[getPatchStart(patchNum)+77]  & 7));       //FixRG
     sysex[28]=(byte)((bank.sysex[getPatchStart(patchNum)+78]  & 15));       //FreqRangeFine
     sysex[29]=(byte)((bank.sysex[getPatchStart(patchNum)+78]  & 112)/16);       //Operator WaveForm
     sysex[30]=(byte)((bank.sysex[getPatchStart(patchNum)+77]  & 48)/16);       //EGShift
   
     sysex[31]=(byte)((bank.sysex[getPatchStart(patchNum)+79]  & 8) / 8);    //FIX
     sysex[32]=(byte)((bank.sysex[getPatchStart(patchNum)+79]  & 7));       //FixRG
     sysex[33]=(byte)((bank.sysex[getPatchStart(patchNum)+80]  & 15));       //FreqRangeFine
     sysex[34]=(byte)((bank.sysex[getPatchStart(patchNum)+80]  & 112)/16);       //Operator WaveForm
     sysex[35]=(byte)((bank.sysex[getPatchStart(patchNum)+79]  & 48)/16);       //EGShift
    
     sysex[36]=(byte)((bank.sysex[getPatchStart(patchNum)+81]));                //Reverb Rate
     sysex[37]=(byte)((bank.sysex[getPatchStart(patchNum)+82]));                //FC Pitch
     sysex[38]=(byte)((bank.sysex[getPatchStart(patchNum)+83]));                //FC Amp
     sysex[40]=(byte)(0xF7);                                             //(Chksm to be added later)
//Then create VCED Data
     sysex[41]=(byte)0xF0;sysex[42]=(byte)0x43;sysex[43]=(byte)0x00;
     sysex[44]=(byte)0x03;sysex[45]=(byte)0x00;sysex[46]=(byte)0x5D;
     
     sysex[47]=(byte)((bank.sysex[getPatchStart(patchNum)+0]));    //AR 
     sysex[48]=(byte)((bank.sysex[getPatchStart(patchNum)+1]));    //d1r
     sysex[49]=(byte)((bank.sysex[getPatchStart(patchNum)+2]));    //d2r
     sysex[50]=(byte)((bank.sysex[getPatchStart(patchNum)+3]));    //rr
     sysex[51]=(byte)((bank.sysex[getPatchStart(patchNum)+4]));    //d1l
     sysex[52]=(byte)((bank.sysex[getPatchStart(patchNum)+5]));    //ls
     sysex[53]=(byte)((bank.sysex[getPatchStart(patchNum)+9]  & 24) / 8);//rate scaling;      
     sysex[54]=(byte)((bank.sysex[getPatchStart(patchNum)+6]  & 56 ) / 8);//ebs      
     sysex[55]=(byte)((bank.sysex[getPatchStart(patchNum)+6]  & 64 ) / 64);//ame      
     sysex[56]=(byte)((bank.sysex[getPatchStart(patchNum)+6]  & 7 ));      //kvs      
     sysex[57]=(byte)((bank.sysex[getPatchStart(patchNum)+7]));            //out
     sysex[58]=(byte)((bank.sysex[getPatchStart(patchNum)+8]));            //frs
     sysex[59]=(byte)((bank.sysex[getPatchStart(patchNum)+9]  & 7 ));      //dbt(det)      
 
     sysex[60]=(byte)((bank.sysex[getPatchStart(patchNum)+0+10]));    //AR 
     sysex[61]=(byte)((bank.sysex[getPatchStart(patchNum)+1+10]));    //d1r
     sysex[62]=(byte)((bank.sysex[getPatchStart(patchNum)+2+10]));    //d2r
     sysex[63]=(byte)((bank.sysex[getPatchStart(patchNum)+3+10]));    //rr
     sysex[64]=(byte)((bank.sysex[getPatchStart(patchNum)+4+10]));    //d1l
     sysex[65]=(byte)((bank.sysex[getPatchStart(patchNum)+5+10]));    //ls
     sysex[66]=(byte)((bank.sysex[getPatchStart(patchNum)+9+10]  & 24) / 8);//rate scaling;      
     sysex[67]=(byte)((bank.sysex[getPatchStart(patchNum)+6+10]  & 56 ) / 8);//ebs      
     sysex[68]=(byte)((bank.sysex[getPatchStart(patchNum)+6+10]  & 64 ) / 64);//ame      
     sysex[69]=(byte)((bank.sysex[getPatchStart(patchNum)+6+10]  & 7 ));      //kvs      
     sysex[70]=(byte)((bank.sysex[getPatchStart(patchNum)+7+10]));            //out
     sysex[71]=(byte)((bank.sysex[getPatchStart(patchNum)+8+10]));            //frs
     sysex[72]=(byte)((bank.sysex[getPatchStart(patchNum)+9+10]  & 7 ));      //dbt(det)      
           
     sysex[73]=(byte)((bank.sysex[getPatchStart(patchNum)+0+20]));    //AR 
     sysex[74]=(byte)((bank.sysex[getPatchStart(patchNum)+1+20]));    //d1r
     sysex[75]=(byte)((bank.sysex[getPatchStart(patchNum)+2+20]));    //d2r
     sysex[76]=(byte)((bank.sysex[getPatchStart(patchNum)+3+20]));    //rr
     sysex[77]=(byte)((bank.sysex[getPatchStart(patchNum)+4+20]));    //d1l
     sysex[78]=(byte)((bank.sysex[getPatchStart(patchNum)+5+20]));    //ls
     sysex[79]=(byte)((bank.sysex[getPatchStart(patchNum)+9+20]  & 24) / 8);//rate scaling;      
     sysex[80]=(byte)((bank.sysex[getPatchStart(patchNum)+6+20]  & 56 ) / 8);//ebs      
     sysex[81]=(byte)((bank.sysex[getPatchStart(patchNum)+6+20]  & 64 ) / 64);//ame      
     sysex[82]=(byte)((bank.sysex[getPatchStart(patchNum)+6+20]  & 7 ));      //kvs      
     sysex[83]=(byte)((bank.sysex[getPatchStart(patchNum)+7+20]));            //out
     sysex[84]=(byte)((bank.sysex[getPatchStart(patchNum)+8+20]));            //frs
     sysex[85]=(byte)((bank.sysex[getPatchStart(patchNum)+9+20]  & 7 ));      //dbt(det)      

     sysex[86]=(byte)((bank.sysex[getPatchStart(patchNum)+0+30]));    //AR 
     sysex[87]=(byte)((bank.sysex[getPatchStart(patchNum)+1+30]));    //d1r
     sysex[88]=(byte)((bank.sysex[getPatchStart(patchNum)+2+30]));    //d2r
     sysex[89]=(byte)((bank.sysex[getPatchStart(patchNum)+3+30]));    //rr
     sysex[90]=(byte)((bank.sysex[getPatchStart(patchNum)+4+30]));    //d1l
     sysex[91]=(byte)((bank.sysex[getPatchStart(patchNum)+5+30]));    //ls
     sysex[92]=(byte)((bank.sysex[getPatchStart(patchNum)+9+30]  & 24) / 8);//rate scaling;      
     sysex[93]=(byte)((bank.sysex[getPatchStart(patchNum)+6+30]  & 56 ) / 8);//ebs      
     sysex[94]=(byte)((bank.sysex[getPatchStart(patchNum)+6+30]  & 64 ) / 64);//ame      
     sysex[95]=(byte)((bank.sysex[getPatchStart(patchNum)+6+30]  & 7 ));      //kvs      
     sysex[96]=(byte)((bank.sysex[getPatchStart(patchNum)+7+30]));            //out
     sysex[97]=(byte)((bank.sysex[getPatchStart(patchNum)+8+30]));            //frs
     sysex[98]=(byte)((bank.sysex[getPatchStart(patchNum)+9+30]  & 7 ));      //dbt(det)      

     sysex[99]=(byte)((bank.sysex[getPatchStart(patchNum)+40]  & 7 ));         //algorithem
    sysex[100]=(byte)((bank.sysex[getPatchStart(patchNum)+40]  & 56 )/8);      //feedback
    sysex[101]=(byte)((bank.sysex[getPatchStart(patchNum)+41]));               //lfo speed 
    sysex[102]=(byte)((bank.sysex[getPatchStart(patchNum)+42]));               //lfo delay
    sysex[103]=(byte)((bank.sysex[getPatchStart(patchNum)+43]));               //pmod depth
    sysex[104]=(byte)((bank.sysex[getPatchStart(patchNum)+44]));               //amod depth
    sysex[105]=(byte)((bank.sysex[getPatchStart(patchNum)+40]& 64)/ 64);       //sync
    sysex[106]=(byte)((bank.sysex[getPatchStart(patchNum)+45]& 3));          //lfw    
    sysex[107]=(byte)((bank.sysex[getPatchStart(patchNum)+45]&112 )/16);     //pms    
    sysex[108]=(byte)((bank.sysex[getPatchStart(patchNum)+45]&12 )/4);       //ams 
    sysex[109]=(byte)((bank.sysex[getPatchStart(patchNum)+46]));              //transpose 
    sysex[110]=(byte)((bank.sysex[getPatchStart(patchNum)+48]&8)/8);          //polymode ***
    sysex[111]=(byte)((bank.sysex[getPatchStart(patchNum)+47]));              //pitchbendrange
    sysex[112]=(byte)((bank.sysex[getPatchStart(patchNum)+48]&1));            //portamento mode***
    sysex[113]=(byte)((bank.sysex[getPatchStart(patchNum)+49]));              //portamento time
    sysex[114]=(byte)((bank.sysex[getPatchStart(patchNum)+50]));              //foot control volume
    sysex[115]=(byte)((bank.sysex[getPatchStart(patchNum)+48]&4)/4);          //sustain
    sysex[116]=(byte)((bank.sysex[getPatchStart(patchNum)+48]&2)/2);          //portamento***
    sysex[117]=(byte)((bank.sysex[getPatchStart(patchNum)+48]&16)/16);        //chorus
  
     sysex[141]=(byte)0xF7;     
     System.arraycopy(bank.sysex,getPatchStart(patchNum)+51,sysex,118,22);
     Patch p = new Patch(sysex);
     p.ChooseDriver();
     PatchEdit.getDriver(p.deviceNum,p.driverNum).calculateChecksum(p);   
     return p;
    }catch (Exception e) {ErrorMsg.reportError("Error","Error in TX81z Bank Driver",e);return null;}
   }
public Patch createNewPatch()
 {
      byte [] sysex = new byte[4104];
     sysex[00]=(byte)0xF0;sysex[01]=(byte)0x43;sysex[02]=(byte)0x00;
     sysex[03]=(byte)0x04;sysex[04]=(byte)0x20;sysex[05]=(byte)0x00;
     sysex[4103]=(byte)0xF7;
     
	Patch p = new Patch(sysex);
	 p.ChooseDriver();
	 for (int i=0;i<32;i++) 
	   setPatchName(p,i,"NewPatch");
	 calculateChecksum(p);	 
	 return p;
 }


}
