/*
 * @version $Id$
 */
package synthdrivers.YamahaTX81z;
import core.*;

import java.io.*;
import javax.swing.*;
public class YamahaTX81zBankDriver extends BankDriver
{

   public YamahaTX81zBankDriver()
   {
   super ("Bank","Brian Klock",32,4);
   sysexID="F043**04*000";
   deviceIDoffset=2;
   bankNumbers =new String[] {"0-Internal"};
   patchNumbers=new String[] {"I01","I02","I03","I04","I05","I06","I07","I08",
                              "I09","I10","I11","I12","I13","I14","I15","I16", 
                              "I17","I18","I19","I20","I21","I22","I23","I24",
                              "I25","I26","I27","I28","I29","I30","I31","I32"};  
   singleSize=142;
   singleSysexID="F043**7E00214C4D2020383937364145";

  }

  public int getPatchStart(int patchNum)
   {
     int start=(128*patchNum);
     start+=6;  //sysex header
     
   return start;
   }
  public String getPatchName(IPatch p,int patchNum) {
     int nameStart=getPatchStart(patchNum);
     nameStart+=57; //offset of name in patch data
         try {
               StringBuffer s= new StringBuffer(new String(((Patch)p).sysex,nameStart,
               10,"US-ASCII"));
               return s.toString();
             } catch (UnsupportedEncodingException ex) {return "-";}   
     
  }

  public void setPatchName(IPatch p,int patchNum, String name)
  {
     patchNameSize=10;
     patchNameStart=getPatchStart(patchNum)+57;
    
    if (name.length()<patchNameSize) name=name+"            ";
    byte [] namebytes = new byte [64];
    try {
         namebytes=name.getBytes("US-ASCII");
         for (int i=0;i<patchNameSize;i++)
           ((Patch)p).sysex[patchNameStart+i]=namebytes[i];

        } catch (UnsupportedEncodingException ex) {return;}
    
  }
 


  public void calculateChecksum (IPatch p)
   {calculateChecksum (p,6,4101,4102);
 
   }                                     

  public void putPatch(IPatch bank,IPatch p,int patchNum)
   { 
   if (!canHoldPatch(p))
       {JOptionPane.showMessageDialog(null, "This type of patch does not fit in to this type of bank.","Error", JOptionPane.ERROR_MESSAGE); return;}
                        
   ((Patch)bank).sysex[getPatchStart(patchNum)+0]=(byte)((((Patch)p).sysex[47]));               //AR
   ((Patch)bank).sysex[getPatchStart(patchNum)+1]=(byte)((((Patch)p).sysex[48]));               //D1r
   ((Patch)bank).sysex[getPatchStart(patchNum)+2]=(byte)((((Patch)p).sysex[49]));               //D2r
   ((Patch)bank).sysex[getPatchStart(patchNum)+3]=(byte)((((Patch)p).sysex[50]));               //RR
   ((Patch)bank).sysex[getPatchStart(patchNum)+4]=(byte)((((Patch)p).sysex[51]));               //D1L
   ((Patch)bank).sysex[getPatchStart(patchNum)+5]=(byte)((((Patch)p).sysex[52]));               //LS
   ((Patch)bank).sysex[getPatchStart(patchNum)+6]=(byte)((((Patch)p).sysex[55]*64+((Patch)p).sysex[54]*8+((Patch)p).sysex[56]));//ame ebs kvs
   ((Patch)bank).sysex[getPatchStart(patchNum)+7]=(byte)((((Patch)p).sysex[57]));               //out
   ((Patch)bank).sysex[getPatchStart(patchNum)+8]=(byte)((((Patch)p).sysex[58]));               //freq
   ((Patch)bank).sysex[getPatchStart(patchNum)+9]=(byte)((((Patch)p).sysex[53]*8+((Patch)p).sysex[59])); //rs dbt
   
   ((Patch)bank).sysex[getPatchStart(patchNum)+10]=(byte)((((Patch)p).sysex[47+13*1]));               //AR
   ((Patch)bank).sysex[getPatchStart(patchNum)+11]=(byte)((((Patch)p).sysex[48+13*1]));               //D1r
   ((Patch)bank).sysex[getPatchStart(patchNum)+12]=(byte)((((Patch)p).sysex[49+13*1]));               //D2r
   ((Patch)bank).sysex[getPatchStart(patchNum)+13]=(byte)((((Patch)p).sysex[50+13*1]));               //RR
   ((Patch)bank).sysex[getPatchStart(patchNum)+14]=(byte)((((Patch)p).sysex[51+13*1]));               //D1L
   ((Patch)bank).sysex[getPatchStart(patchNum)+15]=(byte)((((Patch)p).sysex[52+13*1]));               //LS
   ((Patch)bank).sysex[getPatchStart(patchNum)+16]=(byte)((((Patch)p).sysex[55+13*1]*64+((Patch)p).sysex[54+13*1]*8+((Patch)p).sysex[56+13*1]));//ame ebs kvs
   ((Patch)bank).sysex[getPatchStart(patchNum)+17]=(byte)((((Patch)p).sysex[57+13*1]));               //out
   ((Patch)bank).sysex[getPatchStart(patchNum)+18]=(byte)((((Patch)p).sysex[58+13*1]));               //freq
   ((Patch)bank).sysex[getPatchStart(patchNum)+19]=(byte)((((Patch)p).sysex[53+13*1]*8+((Patch)p).sysex[59+13*1])); //rs dbt
   
   ((Patch)bank).sysex[getPatchStart(patchNum)+20]=(byte)((((Patch)p).sysex[47+13*2]));               //AR
   ((Patch)bank).sysex[getPatchStart(patchNum)+21]=(byte)((((Patch)p).sysex[48+13*2]));               //D1r
   ((Patch)bank).sysex[getPatchStart(patchNum)+22]=(byte)((((Patch)p).sysex[49+13*2]));               //D2r
   ((Patch)bank).sysex[getPatchStart(patchNum)+23]=(byte)((((Patch)p).sysex[50+13*2]));               //RR
   ((Patch)bank).sysex[getPatchStart(patchNum)+24]=(byte)((((Patch)p).sysex[51+13*2]));               //D1L
   ((Patch)bank).sysex[getPatchStart(patchNum)+25]=(byte)((((Patch)p).sysex[52+13*2]));               //LS
   ((Patch)bank).sysex[getPatchStart(patchNum)+26]=(byte)((((Patch)p).sysex[55+13*2]*64+((Patch)p).sysex[54+13*2]*8+((Patch)p).sysex[56+13*2]));//ame ebs kvs
   ((Patch)bank).sysex[getPatchStart(patchNum)+27]=(byte)((((Patch)p).sysex[57+13*2]));               //out
   ((Patch)bank).sysex[getPatchStart(patchNum)+28]=(byte)((((Patch)p).sysex[58+13*2]));               //freq
   ((Patch)bank).sysex[getPatchStart(patchNum)+29]=(byte)((((Patch)p).sysex[53+13*2]*8+((Patch)p).sysex[59+13*2])); //rs dbt
  
   ((Patch)bank).sysex[getPatchStart(patchNum)+30]=(byte)((((Patch)p).sysex[47+13*3]));               //AR
   ((Patch)bank).sysex[getPatchStart(patchNum)+31]=(byte)((((Patch)p).sysex[48+13*3]));               //D1r
   ((Patch)bank).sysex[getPatchStart(patchNum)+32]=(byte)((((Patch)p).sysex[49+13*3]));               //D2r
   ((Patch)bank).sysex[getPatchStart(patchNum)+33]=(byte)((((Patch)p).sysex[50+13*3]));               //RR
   ((Patch)bank).sysex[getPatchStart(patchNum)+34]=(byte)((((Patch)p).sysex[51+13*3]));               //D1L
   ((Patch)bank).sysex[getPatchStart(patchNum)+35]=(byte)((((Patch)p).sysex[52+13*3]));               //LS
   ((Patch)bank).sysex[getPatchStart(patchNum)+36]=(byte)((((Patch)p).sysex[55+13*3]*64+((Patch)p).sysex[54+13*3]*8+((Patch)p).sysex[56+13*3]));//ame ebs kvs
   ((Patch)bank).sysex[getPatchStart(patchNum)+37]=(byte)((((Patch)p).sysex[57+13*3]));               //out
   ((Patch)bank).sysex[getPatchStart(patchNum)+38]=(byte)((((Patch)p).sysex[58+13*3]));               //freq
   ((Patch)bank).sysex[getPatchStart(patchNum)+39]=(byte)((((Patch)p).sysex[53+13*3]*8+((Patch)p).sysex[59+13*3])); //rs dbt 
 
   ((Patch)bank).sysex[getPatchStart(patchNum)+40]=(byte)((((Patch)p).sysex[105]*64+((Patch)p).sysex[100]*8+((Patch)p).sysex[99]));//sync fbl alg
   ((Patch)bank).sysex[getPatchStart(patchNum)+41]=(byte)((((Patch)p).sysex[101]));               //lfs   
   ((Patch)bank).sysex[getPatchStart(patchNum)+42]=(byte)((((Patch)p).sysex[102]));               //lfd   
   ((Patch)bank).sysex[getPatchStart(patchNum)+43]=(byte)((((Patch)p).sysex[103]));               //pmd   
   ((Patch)bank).sysex[getPatchStart(patchNum)+44]=(byte)((((Patch)p).sysex[104]));               //amd   
   ((Patch)bank).sysex[getPatchStart(patchNum)+45]=(byte)((((Patch)p).sysex[107]*16+((Patch)p).sysex[108]*4+((Patch)p).sysex[106]));//pms ams lfw
   ((Patch)bank).sysex[getPatchStart(patchNum)+46]=(byte)((((Patch)p).sysex[109]));               //traspose  
   ((Patch)bank).sysex[getPatchStart(patchNum)+47]=(byte)((((Patch)p).sysex[111]));               //pbr   
   ((Patch)bank).sysex[getPatchStart(patchNum)+48]=(byte)((((Patch)p).sysex[117]*16+((Patch)p).sysex[110]*8+
   				                  ((Patch)p).sysex[115]*4+((Patch)p).sysex[116]*2+((Patch)p).sysex[112]));//ch mo su po pm
   ((Patch)bank).sysex[getPatchStart(patchNum)+49]=(byte)((((Patch)p).sysex[113]));               //porta time   
   ((Patch)bank).sysex[getPatchStart(patchNum)+50]=(byte)((((Patch)p).sysex[114]));               //footcontrol     
   
   System.arraycopy(((Patch)p).sysex,118,((Patch)bank).sysex,getPatchStart(patchNum)+51,22);

  ((Patch)bank).sysex[getPatchStart(patchNum)+73]=(byte)((((Patch)p).sysex[20]*16+((Patch)p).sysex[16]*8+((Patch)p).sysex[17]));//egsft,fix,fixrg
  ((Patch)bank).sysex[getPatchStart(patchNum)+74]=(byte)((((Patch)p).sysex[19]*16+((Patch)p).sysex[18]));//osw fine 
  ((Patch)bank).sysex[getPatchStart(patchNum)+75]=(byte)((((Patch)p).sysex[20+5]*16+((Patch)p).sysex[16+5]*8+((Patch)p).sysex[17+5]));//egsft,fix,fixrg
  ((Patch)bank).sysex[getPatchStart(patchNum)+76]=(byte)((((Patch)p).sysex[19+5]*16+((Patch)p).sysex[18+5]));//osw fine 
  ((Patch)bank).sysex[getPatchStart(patchNum)+77]=(byte)((((Patch)p).sysex[20+10]*16+((Patch)p).sysex[16+10]*8+((Patch)p).sysex[17+10]));//egsft,fix,fixrg
  ((Patch)bank).sysex[getPatchStart(patchNum)+78]=(byte)((((Patch)p).sysex[19+10]*16+((Patch)p).sysex[18+10]));//osw fine 
  ((Patch)bank).sysex[getPatchStart(patchNum)+79]=(byte)((((Patch)p).sysex[20+15]*16+((Patch)p).sysex[16+15]*8+((Patch)p).sysex[17+15]));//egsft,fix,fixrg
  ((Patch)bank).sysex[getPatchStart(patchNum)+80]=(byte)((((Patch)p).sysex[19+15]*16+((Patch)p).sysex[18+15]));//osw fine 
  ((Patch)bank).sysex[getPatchStart(patchNum)+81]=(byte)((((Patch)p).sysex[36]));               //pbr   
  ((Patch)bank).sysex[getPatchStart(patchNum)+82]=(byte)((((Patch)p).sysex[37]));               //pbr   
  ((Patch)bank).sysex[getPatchStart(patchNum)+83]=(byte)((((Patch)p).sysex[38]));               //pbr   
  
   calculateChecksum(bank);
   }
  public IPatch getPatch(IPatch bank, int patchNum)
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
 
     sysex[16]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+73]  & 8) / 8);    //FIX
     sysex[17]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+73]  & 7));       //FixRG
     sysex[18]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+74]  & 15));       //FreqRangeFine
     sysex[19]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+74]  & 112)/16);       //Operator WaveForm
     sysex[20]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+73]  & 48)/16);       //EGShift
      
     sysex[21]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+75]  & 8) / 8);    //FIX
     sysex[22]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+75]  & 7));       //FixRG
     sysex[23]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+76]  & 15));       //FreqRangeFine
     sysex[24]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+76]  & 112)/16);       //Operator WaveForm
     sysex[25]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+75]  & 48)/16);       //EGShift
   
     sysex[26]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+77]  & 8) / 8);    //FIX
     sysex[27]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+77]  & 7));       //FixRG
     sysex[28]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+78]  & 15));       //FreqRangeFine
     sysex[29]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+78]  & 112)/16);       //Operator WaveForm
     sysex[30]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+77]  & 48)/16);       //EGShift
   
     sysex[31]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+79]  & 8) / 8);    //FIX
     sysex[32]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+79]  & 7));       //FixRG
     sysex[33]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+80]  & 15));       //FreqRangeFine
     sysex[34]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+80]  & 112)/16);       //Operator WaveForm
     sysex[35]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+79]  & 48)/16);       //EGShift
    
     sysex[36]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+81]));                //Reverb Rate
     sysex[37]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+82]));                //FC Pitch
     sysex[38]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+83]));                //FC Amp
     sysex[40]=(byte)(0xF7);                                             //(Chksm to be added later)
//Then create VCED Data
     sysex[41]=(byte)0xF0;sysex[42]=(byte)0x43;sysex[43]=(byte)0x00;
     sysex[44]=(byte)0x03;sysex[45]=(byte)0x00;sysex[46]=(byte)0x5D;
     
     sysex[47]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+0]));    //AR 
     sysex[48]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+1]));    //d1r
     sysex[49]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+2]));    //d2r
     sysex[50]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+3]));    //rr
     sysex[51]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+4]));    //d1l
     sysex[52]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+5]));    //ls
     sysex[53]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+9]  & 24) / 8);//rate scaling;      
     sysex[54]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+6]  & 56 ) / 8);//ebs      
     sysex[55]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+6]  & 64 ) / 64);//ame      
     sysex[56]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+6]  & 7 ));      //kvs      
     sysex[57]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+7]));            //out
     sysex[58]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+8]));            //frs
     sysex[59]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+9]  & 7 ));      //dbt(det)      
 
     sysex[60]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+0+10]));    //AR 
     sysex[61]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+1+10]));    //d1r
     sysex[62]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+2+10]));    //d2r
     sysex[63]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+3+10]));    //rr
     sysex[64]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+4+10]));    //d1l
     sysex[65]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+5+10]));    //ls
     sysex[66]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+9+10]  & 24) / 8);//rate scaling;      
     sysex[67]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+6+10]  & 56 ) / 8);//ebs      
     sysex[68]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+6+10]  & 64 ) / 64);//ame      
     sysex[69]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+6+10]  & 7 ));      //kvs      
     sysex[70]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+7+10]));            //out
     sysex[71]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+8+10]));            //frs
     sysex[72]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+9+10]  & 7 ));      //dbt(det)      
           
     sysex[73]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+0+20]));    //AR 
     sysex[74]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+1+20]));    //d1r
     sysex[75]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+2+20]));    //d2r
     sysex[76]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+3+20]));    //rr
     sysex[77]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+4+20]));    //d1l
     sysex[78]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+5+20]));    //ls
     sysex[79]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+9+20]  & 24) / 8);//rate scaling;      
     sysex[80]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+6+20]  & 56 ) / 8);//ebs      
     sysex[81]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+6+20]  & 64 ) / 64);//ame      
     sysex[82]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+6+20]  & 7 ));      //kvs      
     sysex[83]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+7+20]));            //out
     sysex[84]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+8+20]));            //frs
     sysex[85]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+9+20]  & 7 ));      //dbt(det)      

     sysex[86]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+0+30]));    //AR 
     sysex[87]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+1+30]));    //d1r
     sysex[88]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+2+30]));    //d2r
     sysex[89]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+3+30]));    //rr
     sysex[90]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+4+30]));    //d1l
     sysex[91]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+5+30]));    //ls
     sysex[92]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+9+30]  & 24) / 8);//rate scaling;      
     sysex[93]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+6+30]  & 56 ) / 8);//ebs      
     sysex[94]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+6+30]  & 64 ) / 64);//ame      
     sysex[95]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+6+30]  & 7 ));      //kvs      
     sysex[96]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+7+30]));            //out
     sysex[97]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+8+30]));            //frs
     sysex[98]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+9+30]  & 7 ));      //dbt(det)      

     sysex[99]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+40]  & 7 ));         //algorithem
    sysex[100]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+40]  & 56 )/8);      //feedback
    sysex[101]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+41]));               //lfo speed 
    sysex[102]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+42]));               //lfo delay
    sysex[103]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+43]));               //pmod depth
    sysex[104]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+44]));               //amod depth
    sysex[105]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+40]& 64)/ 64);       //sync
    sysex[106]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+45]& 3));          //lfw    
    sysex[107]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+45]&112 )/16);     //pms    
    sysex[108]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+45]&12 )/4);       //ams 
    sysex[109]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+46]));              //transpose 
    sysex[110]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+48]&8)/8);          //polymode ***
    sysex[111]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+47]));              //pitchbendrange
    sysex[112]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+48]&1));            //portamento mode***
    sysex[113]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+49]));              //portamento time
    sysex[114]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+50]));              //foot control volume
    sysex[115]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+48]&4)/4);          //sustain
    sysex[116]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+48]&2)/2);          //portamento***
    sysex[117]=(byte)((((Patch)bank).sysex[getPatchStart(patchNum)+48]&16)/16);        //chorus
  
     sysex[141]=(byte)0xF7;     
     System.arraycopy(((Patch)bank).sysex,getPatchStart(patchNum)+51,sysex,118,22);
     IPatch p = new Patch(sysex, getDevice());
     p.getDriver().calculateChecksum(p);   
     return p;
    }catch (Exception e) {ErrorMsg.reportError("Error","Error in TX81z Bank Driver",e);return null;}
   }
public IPatch createNewPatch()
 {
      byte [] sysex = new byte[4104];
     sysex[00]=(byte)0xF0;sysex[01]=(byte)0x43;sysex[02]=(byte)0x00;
     sysex[03]=(byte)0x04;sysex[04]=(byte)0x20;sysex[05]=(byte)0x00;
     sysex[4103]=(byte)0xF7;
     
	IPatch p = new Patch(sysex, this);
	 for (int i=0;i<32;i++) 
	   setPatchName(p,i,"NewPatch");
	 calculateChecksum(p);	 
	 return p;
 }


}
