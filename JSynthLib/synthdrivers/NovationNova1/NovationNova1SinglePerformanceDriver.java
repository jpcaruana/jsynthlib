/* Made by Yves Lefebvre
   email : ivanohe@abacom.com
   www.abacom.com/~ivanohe
*/

package synthdrivers.NovationNova1;
import core.*;
import java.io.*;
import javax.swing.*;
public class NovationNova1SinglePerformanceDriver extends BankDriver
{

    public NovationNova1SinglePerformanceDriver()
    {
        manufacturer="Novation";
        model="Nova1";
        patchType="Performance (single)";
        id="Nova1";
        sysexID="F000202901210*000*";
        //sysexID="";
        sysexRequestDump=new SysexHandler("F0 00 20 29 01 21 @@ 08 F7");
        deviceIDoffset=6;
        bankNumbers =new String[] {"Single Performance"};
        patchNumbers=new String[] {"Part 1-","Part 2-","Part 3-","Part 4-","Part 5-","Part 6-" };

        numPatches=patchNumbers.length;
        numColumns=1;
        singleSysexID="F000202901210*000*";
        singleSize=296;
    }

    public int getPatchStart(int patchNum)
    {
        int start=(296*patchNum);
        start+=9;  //sysex header
        return start;
    }

  
    public String getPatchName(Patch p) 
    {
        // This method get the name of the performance
        try 
        {
            StringBuffer s= new StringBuffer(new String(p.sysex,(296*8)+8,
            16,"US-ASCII"));
            return s.toString();
        } catch (UnsupportedEncodingException ex) {return "-";}   
    }

    public String getPatchName(Patch p,int patchNum) 
    {
        // This method get the name of individual patch in the performance
        int nameStart=getPatchStart(patchNum);
        try 
        {
            StringBuffer s= new StringBuffer(new String(p.sysex,nameStart,
            16,"US-ASCII"));
            return s.toString();
        } catch (UnsupportedEncodingException ex) {return "-";}   
    }

    public void setPatchName(Patch p,int patchNum, String name)
    {
        patchNameSize=16;
        patchNameStart=getPatchStart(patchNum);

        if (name.length()<patchNameSize)
        {
            name=name+"            ";
        }
        byte [] namebytes = new byte [64];
        try 
        {
            namebytes=name.getBytes("US-ASCII");
            for (int i=0;i<patchNameSize;i++)
            p.sysex[patchNameStart+i]=namebytes[i];
        } catch (UnsupportedEncodingException ex) {return;}
    }
 

    public void calculateChecksum(Patch p,int start,int end,int ofs)
    {

    }


    public void calculateChecksum (Patch p)
    {

    }                                     

    public void putPatch(Patch bank,Patch p,int patchNum)
    {
        // This method is called when doing a paste (from another bank or a single)
        // the patch received will be a single dump (meant for the edit buffer)
        // we need to extract the actual patch info and paste it in the bank itself

        if (!canHoldPatch(p))
        {JOptionPane.showMessageDialog(null, "This type of patch does not fit in to this type of bank.","Error", JOptionPane.ERROR_MESSAGE); return;}

        System.arraycopy(p.sysex,9,bank.sysex,getPatchStart(patchNum),296-9);
        calculateChecksum(bank);
    }
    public Patch getPatch(Patch bank, int patchNum)
    {
        // this method is call when you have a single perf opened and want to send or play individual patches
        // OR when you do a Cut/Copy
        // The method is call to retreive a single patch
        // Must convert to a single dump for edit buffer
        try
        {
            byte [] sysex=new byte[296];

            sysex[0]=(byte)0xF0;sysex[1]=(byte)0x00;sysex[2]=(byte)0x20;
            sysex[3]=(byte)0x29;sysex[4]=(byte)0x01;sysex[5]=(byte)0x21;
            sysex[6]=(byte)(channel-1);
            sysex[7]=(byte)0x00;
            sysex[8]=(byte)0x09;
            sysex[295]=(byte)0xF7;
            System.arraycopy(bank.sysex,getPatchStart(patchNum),sysex,9,296-9);
            Patch p = new Patch(sysex);
            p.ChooseDriver();
            ((Driver) (PatchEdit.getDriver(p.deviceNum,p.driverNum))).calculateChecksum(p);   
            return p;
        }catch (Exception e) {ErrorMsg.reportError("Error","Error in Nova1 Bank Driver",e);return null;}
    }

    public Patch createNewPatch()
    {
        byte [] sysex = new byte[((296*8)+406)]; // 406 is the size of the actual Performance data
                                                 // Note that there is 8 part even if only 6 are usable 
                                                 // this is to be compatible with the supernova
        byte [] sysexHeader = new byte[10];
        sysexHeader [0]=(byte)0xF0;
        sysexHeader [1]=(byte)0x00;
        sysexHeader [2]=(byte)0x20;
        sysexHeader [3]=(byte)0x29;
        sysexHeader [4]=(byte)0x01;
        sysexHeader [5]=(byte)0x21; 
        sysexHeader [6]=(byte)(channel-1);
        sysexHeader [7]=(byte)0x00;
        sysexHeader [8] = (byte)(0x00); //this is the part number in the performance

        Patch p = new Patch(sysex);
        p.ChooseDriver();
        for (int i=0;i<8;i++) 
        {
            sysexHeader [8] = (byte)i;
            System.arraycopy(sysexHeader,0,p.sysex,i*296,9);
            System.arraycopy(NovationNova1InitPatch.initpatch,9,p.sysex,(i*296)+9,296-9);
        }
        // now, create a new performance
        // The default will be the same thing as "Multi Ch 1-6" Perf A126
        System.arraycopy(NovationNova1InitPatch.initperf,0,p.sysex,(8*296),406);

        //    calculateChecksum(p);	 
        return p;
    }

    public void storePatch (Patch bank, int bankNum,int patchNum)
    {
        JOptionPane.showMessageDialog(null,
        "You can not store performance data with this driver.\nUse send and save it from the Nova front pannel\n(you will have to decide where to save the actual patch)","Error", JOptionPane.ERROR_MESSAGE);
    };



    public void sendPatch (Patch bank)
    { 
        byte [] newsysex = new byte[296];
        Patch p = new Patch(newsysex);
        try 
        {       
            for (int i=0;i<8;i++) 
            {
                System.arraycopy(bank.sysex,296*i,p.sysex,0,296);
                sendPatchWorker(p);
                Thread.sleep(5); // Nova have problem receiving too fast, The loop itself introduce more delay so the sleep may not be necessary.
                                 // NOTE : Do not modify this to send all patch in one shot! It will be faster but some patch may not be received correctly on the Nova!
            }

            // Now, send the rest which is the performance data itself
            byte [] newsysex2 = new byte[406];
            Patch perf = new Patch(newsysex2);

            System.arraycopy(bank.sysex,296*8,perf.sysex,0,406);
            perf.sysex[387] = (byte)(channel-1); // there is a small sysex msg at the end that
                                              // need to have the channel byte set
            sendPatchWorker(perf);
        }catch (Exception e) {ErrorMsg.reportError("Error","Unable to send Patch",e);}
    }


    public void setBankNum(int bankNum)
    {
    }


}
