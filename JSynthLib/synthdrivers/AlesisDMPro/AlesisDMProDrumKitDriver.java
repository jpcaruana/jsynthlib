package synthdrivers.AlesisDMPro;
import core.Driver;
import core.JSLFrame;
import core.Patch;
import core.SysexHandler;

public class AlesisDMProDrumKitDriver extends Driver
{
    
   private final char[] m_arChars = new char[] {' ', '!', '"', '#', '$', '%', '&', '"', '(', ')', '*', '+', ',', '-', '.', '/', 
                                                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ':', ';', '<', '=', '>', '?', '@', 
                                                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 
                                                'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y',  'Z', '[', 'Y', ']', '^', '_', '"', 
                                                'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',  'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 
                                                'r', 's', 't', 'u', 'v', 'w', 'x', 'y',  'z', '{', '|', '}', '>', '<'};
   public AlesisDMProDrumKitDriver()
   {
   super("DrumKit", "Peter Hageus (peter.hageus@comhem.se)");
   sysexID="F000000E190E*";
   sysexRequestDump=new SysexHandler("F0 00 00 0E 19 0F *patchNum* F7");

   patchSize=648;
   patchNameStart=7;
   patchNameSize=10;

   bankNumbers =new String[] {"Internal bank"};
   patchNumbers=new String[64]; 
   
   for (int i=0;i < 64;i++) {
       patchNumbers[i] = "Kit " + i;
   }

  }
public void storePatch (Patch p, int bankNum,int patchNum)
  {   
   setBankNum(bankNum);
   setPatchNum(patchNum);
   p.sysex[6] = (byte) patchNum;
   sendPatchWorker(p);
   setPatchNum(patchNum);
  }
public void sendPatch (Patch p)
  { 
   //DM Pro editbuffer is named 64
   p.sysex[6] = 64;
   sendPatchWorker(p);
  }
  public void calculateChecksum(Patch p,int start,int end,int ofs)
  {
  }
  public Patch createNewPatch ()
 {
     
	 byte [] sysex = new byte[648];
         sysex[0] = (byte) 0xF0;
         sysex[1] = (byte) 0x00;
         sysex[2] = (byte) 0x00;
         sysex[3] = (byte) 0x0E;
         sysex[4] = (byte) 0x19;
         sysex[5] = (byte) 0x0E;
         sysex[6] = (byte) 0x64;
         
         for (int i=7;i<647;i++) 
             sysex[i] = 0;
         
         sysex[647] = (byte) 0xF7;
         
	 //ToDO: Load up a dump as a basis...
	 Patch p = new Patch(sysex, this);
	 setPatchName(p,"NewDrumKit");
	 calculateChecksum(p);	 
	 return p;
 }
public JSLFrame editPatch(Patch p)
 {
     return new AlesisDMProDrumKitEditor(p);
 }

/**Gets the name of the patch from the sysex. If the patch uses some weird
 * format or encoding, this needs to be overidden in the particular driver*/
public String getPatchName (Patch p)
{
  StringBuffer str = new StringBuffer("");

  try {
    	
    	AlesisDMProParser oParser = new AlesisDMProParser(p);

    	str.append((char) m_arChars[oParser.getValue(0,5,7)]);
    	str.append((char) m_arChars[oParser.getValue(1,4,7)]);
    	str.append((char) m_arChars[oParser.getValue(2,3,7)]);
    	str.append((char) m_arChars[oParser.getValue(3,2,7)]);
    	str.append((char) m_arChars[oParser.getValue(4,1,7)]);
    	str.append((char) m_arChars[oParser.getValue(5,0,7)]);
    	str.append((char) m_arChars[oParser.getValue(5,7,7)]);
    	str.append((char) m_arChars[oParser.getValue(6,6,7)]);
    	str.append((char) m_arChars[oParser.getValue(7,5,7)]);
    	str.append((char) m_arChars[oParser.getValue(8,4,7)]);

    } catch (Exception e) {
	    System.out.println("getPatchName: " + e.getMessage());
   }

    return str.toString();
}

public void setPatchName (Patch p, String name)
    {   

        AlesisDMProParser oParser = new AlesisDMProParser(p);
        byte[] ar = new byte[10];
        char arStr[] = name.toCharArray();

        for (int i = 0;i<10;i++) {
            
            if (i >= arStr.length)
                ar[i] = 0;
            else {
                for (int j = 0;j<96;j++) {
                    if (m_arChars[j] == arStr[i]) {
                        ar[i] = (byte) j;
                        break;
                    }
                }
            }
        }
    
        oParser.setValue(0,5,7,ar[0]);
        oParser.setValue(1,4,7,ar[1]);
        oParser.setValue(2,3,7,ar[2]);
        oParser.setValue(3,2,7,ar[3]);
        oParser.setValue(4,1,7,ar[4]);
        oParser.setValue(5,0,7,ar[5]);
        oParser.setValue(5,7,7,ar[6]);
        oParser.setValue(6,6,7,ar[7]);
        oParser.setValue(7,5,7,ar[8]);
        oParser.setValue(8,4,7,ar[9]);

        oParser = null;
 
    }
}
