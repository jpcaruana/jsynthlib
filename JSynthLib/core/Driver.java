/* $Id$ */

package core;
import java.io.*;
import jmidi.MidiPort;
import javax.swing.*;

/**This is the base class for all Drivers*/
public class Driver extends Object implements Serializable
{
    /**The company which made the Synthesizer*/
    public String manufacturer;
    /**The models supported by this driver eg TG33/SY22*/
    public String model;
    /**The patch type eg. Single, Bank, Drumkit, etc. */
    public String patchType;
    /**A Shorthand alias for the Synth this driver supports (eg TG33,K5k)*/
    public String id;
    /**The hex header that sysex files of the format this driver supports will
     * have. It can have wildcards (*). The program will attempt to match
     * loaded sysex drivers with the sysexID of a loaded driver. It can be up
     * to 16 bytes*/
    public String sysexID;
    /**The response to the Universal Inquiry Message.
     * It can have wildcards (*). It can be up
     * to 16 bytes*/
    // ADDED BY GERRIT GEHNEN
       public String inquiryID;
    /**The offset in the patch where the patchname starts (0 if patch is not
     * named-- remember all offsets are zero based*/
    
    public int patchNameStart;
    /**Number of characters in the patch name (0 if no name)*/
    public int patchNameSize;
    /**Offset of deviceID in sysex*/
    public int deviceIDoffset;  //location of device id
    /**Offset of Checksum byte*/
    public int  checksumOffset;
    /**Start of Range that Checksum covers*/
    public int  checksumStart;
    /**End of Range that Checksum covers*/
    public int  checksumEnd;
    
    /**Array holding numbers for all patches*/
    public String [] patchNumbers;
    /**Array holding numbers/names for all banks*/
    public String [] bankNumbers;
    /**The size of the patch this Driver supports (or 0 for variable)*/
    public int patchSize;
    /** The size of the patch for trimming purposes - see trimSysex()-method */
    protected int trimSize=0;
    /**Number of sysex messages in patch dump*/
    public int numSysexMsgs;    
    
    /**The Midi Out Port the user assigns to this driver*/
    protected int port;
    /**The Midi In Port the user assigns to this driver*/  // phil@muqus.com
    public int inPort;                                     // phil@muqus.com
 
   /**The channel the user assigns to this driver*/
    protected int channel=1;

   /**SysexHandler object to request dump (see requestPatchDump) - phil@muqus.com*/
   public SysexHandler sysexRequestDump = null;

   /**The names of the authors of this driver*/
   protected String authors;
  
   /**Which device does this driver go with?*/
   protected Device device;

    /**Constructs a generic Driver*/
    public Driver ()
    {
        sysexID="MATCHNONE";
        //    inquiryID="NONE";
        authors="Brian Klock";
	manufacturer="Generic";
        model="";
        patchType="Sysex";
        id="???";
        patchNameSize=0;
        patchNumbers= new String [128];
        for (int i=0;i<128;i++) patchNumbers[i]=String.valueOf (i);
        bankNumbers = new String[] {"0"};
    }
    
    
    public String getManufacturerName ()
    {return manufacturer;}
    public String getModelName ()
    {return model;}
    public String getPatchType ()
    {return patchType;}
    public String getSynthName ()
    {return id;}
    public String getAuthors()
    {return authors;}
    public void   setSynthName (String s)
    {id=s;}
    public void setDevice(Device d)
    {device=d;}
    /**Gets the name of the patch from the sysex. If the patch uses some weird
     * format or encoding, this needs to be overidden in the particular driver*/
    public String getPatchName (Patch p)
    {
        if (patchNameSize==0) return ("-");
        try
        {
            StringBuffer s= new StringBuffer (new String (p.sysex,patchNameStart,
            patchNameSize,"US-ASCII"));
            return s.toString ();
        } catch (UnsupportedEncodingException ex)
        {return "-";}
    }
    
    /**Compares the header & size of a Patch to this driver to see if this driver
     * is the correct one to support the patch*/
    public boolean supportsPatch (StringBuffer patchString,Patch p)
    {
        //System.out.println("SupportsPatch:"+manufacturer+" "+model+" "+patchType+" "+patchSize+" "+p.sysex.length);
        if ((patchSize!=p.sysex.length) && (patchSize!=0)) return false;
        StringBuffer driverString=new StringBuffer (sysexID);
        StringBuffer compareString=new StringBuffer ();
        for (int j=0, i=0;j<driverString.length ();j++,i++)
        {
            switch (driverString.charAt (j))
            {
                case '*':
                    compareString.append (patchString.charAt (i));
                    break;
                default: compareString.append (driverString.charAt(j));
                
            }
        }
        System.out.println("Manufacturer:"+manufacturer+" Model:"+model+" Patch Type:"+patchType);
        System.out.println("Comp.String: "+compareString);
        System.out.println("DriverString:"+driverString);
        System.out.println("PatchString: "+patchString);
        return (compareString.toString ().equalsIgnoreCase (patchString.toString ().substring (0,driverString.length ())));
    }
    
    /**Sends a patch to the synth's edit buffer. override this in the subclass if
     * parameters or warnings need to be sent to the user (aka if the particular
     * synth does not have a edit buffer or it is not midi accessable*/
    public void sendPatch (Patch p)
    { sendPatchWorker (p);}
    
    /**Sends a patch to a set location on a synth.*/
    public void storePatch (Patch p, int bankNum,int patchNum)
    {
        setBankNum (bankNum);
        setPatchNum (patchNum);
        sendPatch (p);
    }
    /**Prompts the user for the location to store the patch and stores it*/
//----- Start phil@muqus.com (so that BankEditorFrame can set the default patchNum to be the currently selected patch)
public void choosePatch (Patch p) {
  choosePatch (p, 0);
}
public void choosePatch (Patch p, int patchNum)
//----- End phil@muqus.com
  {
    int bank=0;
    int patch=0;
    String bankstr;
    String patchstr;
    try{
      if (bankNumbers.length>1)
       {
         bankstr=(String)JOptionPane.showInputDialog(null,"Please Choose a Bank","Storing Patch",
                         JOptionPane.QUESTION_MESSAGE,null,bankNumbers,bankNumbers[0]);
         if (bankstr==null) return;
         for (int i=0;i<bankNumbers.length;i++) if (bankstr.equals(bankNumbers[i])) bank=i;
       }
      if (patchNumbers.length>1)
        {
          patchstr=(String)JOptionPane.showInputDialog(null,"Please Choose a Patch Location","Storing Patch",
                          JOptionPane.QUESTION_MESSAGE,null,patchNumbers,patchNumbers[patchNum]); // phil@muqus.com
          if (patchstr==null) return;
          for (int i=0;i<patchNumbers.length;i++) if (patchstr.equals(patchNumbers[i])) patch=i;
        }
   }catch (Exception e) {ErrorMsg.reportStatus(e);}
    ErrorMsg.reportStatus("Driver:ChoosePatch  Bank = "+bank+"  Patch = "+patch);
    storePatch(p,bank,patch);
  }

    
    
    /**Does the actual work to send a patch to the synth*/
    protected void sendPatchWorker (Patch p)
    {

        if (deviceIDoffset>0) p.sysex[deviceIDoffset]=(byte)(channel-1);
        try
        {

            PatchEdit.MidiOut.writeLongMessage (port,p.sysex);
        }catch (Exception e)
        {ErrorMsg.reportStatus (e);}
    }
    
    public void calculateChecksum (Patch p,int start,int end,int ofs)
    {
        int i;
        int sum=0;
        ErrorMsg.reportStatus ("Driver:calcChecksum:1st byte is "+p.sysex[start]);
        ErrorMsg.reportStatus ("Last byte is "+p.sysex[end]);
        ErrorMsg.reportStatus ("Checksum was "+p.sysex[ofs]);
        for (i=start;i<=end;i++)
            sum+=p.sysex[i];
        p.sysex[ofs]=(byte)(sum % 128);
        p.sysex[ofs]=(byte)(p.sysex[ofs]^127);
        p.sysex[ofs]=(byte)(p.sysex[ofs]+1);
        p.sysex[ofs]=(byte)(p.sysex[ofs]&127);   //to ensure that checksum is in range 0-127;         
        ErrorMsg.reportStatus ("Checksum is now "+p.sysex[ofs]);
        
    }
    public void calculateChecksum (Patch p)
    { calculateChecksum (p,checksumStart,checksumEnd,checksumOffset);
    }
    
    public void setPatchName (Patch p, String name)
    {
        if (patchNameSize==0)
        {ErrorMsg.reportError ("Error", "The Driver for this patch does not support Patch Name Editing.");
         return;
        }
        if (name.length ()<patchNameSize) name=name+"            ";
        byte [] namebytes = new byte [64];
        try
        {
            namebytes=name.getBytes ("US-ASCII");
            for (int i=0;i<patchNameSize;i++)
                p.sysex[patchNameStart+i]=namebytes[i];
            
        } catch (UnsupportedEncodingException ex)
        {return;}
        calculateChecksum (p);
    }

    public void setBankNum (int bankNum)
    {
        try
        {
            PatchEdit.MidiOut.writeShortMessage (port,(byte)(0xB0+(channel-1)),(byte)0x00,(byte)(bankNum/128));
            PatchEdit.MidiOut.writeShortMessage (port,(byte)(0xB0+(channel-1)),(byte)0x20,(byte)(bankNum%128));
        } catch (Exception e)
        {};
        
    }
    public void setPatchNum (int patchNum)
    {
        try
        {
            PatchEdit.MidiOut.writeShortMessage (port,(byte)(0xC0+(channel-1)),(byte)patchNum);
        } catch (Exception e)
        {};
    }
    public void playPatch (Patch p)
    {
        try
        {
            sendPatch (p);
            Thread.sleep (100);
        
            PatchEdit.MidiOut.writeShortMessage (port,
            (byte)(0x90+(channel-1)),(byte)PatchEdit.noteChooserDialog.note,(byte)PatchEdit.noteChooserDialog.velocity);
            Thread.sleep (PatchEdit.noteChooserDialog.delay);
            PatchEdit.MidiOut.writeShortMessage (port,
            (byte)(0x80+(channel-1)),(byte)PatchEdit.noteChooserDialog.note,(byte)0);
        } catch (Exception e)
        {ErrorMsg.reportStatus (e);}
    }
    /**Returns an Editor Window for this Patch*/
    public JInternalFrame editPatch (Patch p)
    {
        {ErrorMsg.reportError ("Error","The Driver for this patch does not support Patch Editing.");
         return (null);
        }
    }
    public Patch createNewPatch ()
    {return null;}
    
    /** Getter for property channel.
     * @return Value of property channel.
     */
    public int getChannel ()
    {
        return channel;
    }
    
    /** Setter for property channel.
     * @param channel New value of property channel.
     */
    public void setChannel (int channel)
    {
        this.channel = channel;
    }
    
    /** Getter for property port.
     * @return Value of property port.
     */
    public int getPort ()
    {
        return port;
    }
    
    /** Setter for property port.
     * @param port New value of property port.
     */
    public void setPort (int port)
    {
        this.port = port;
    }
    

//----- Start phil@muqus.com

//----------------------------------------------------------------------------------------------------------------------
// Driver->requestPatchDump
//----------------------------------------------------------------------------------------------------------------------


  public void requestPatchDump(int bankNum, int patchNum) {
      setBankNum(bankNum);
      setPatchNum(patchNum);
      if (sysexRequestDump == null)
       {
        JOptionPane.showMessageDialog(PatchEdit.instance,
        "The " + getDriverName() + " driver does not support patch getting.\n\nPlease start the patch dump manually...",
        "Get Patch",
        JOptionPane.WARNING_MESSAGE
        );
        byte buffer[] = new byte[256*1024];
        try {
           while (PatchEdit.MidiIn.messagesWaiting(inPort) > 0)
            PatchEdit.MidiIn.readMessage(inPort, buffer, 1024);
           } catch (Exception ex) {
          ErrorMsg.reportError("Error", "Error Clearing Midi In buffer.",ex);
         }
        
      }
    else
        
	sysexRequestDump.send(
        port, (byte)channel,
        new NameValue("bankNum", bankNum),
        new NameValue("patchNum", patchNum)
      );
  }

  

//----------------------------------------------------------------------------------------------------------------------
// Driver->getFullPatchName
// Returns: String .. full name for referring to this patch for debugging purposes
//----------------------------------------------------------------------------------------------------------------------

   public String getFullPatchName(Patch p) {
     return getManufacturerName() + " | " + getModelName() + " | " + getPatchType() + " | " + getSynthName() + " | " + getPatchName(p);
   }
//----- End phil@muqus.com
   public String getDriverName() {
     return getManufacturerName() + " " + getModelName() + " " + getPatchType() + " " ;
   }

    /** This method trims a patch, containing more than one real
     * patch to a correct size. Useful for files containg more than one
     * bank for example. Some drivers are incompatible with this method
     * so it reqires explicit activation with the trimSize variable.
     * @param p the patch, which should be trimmed to the right size
     * @return the size of the (modified) patch
     */    
 public int trimSysex(Patch p) {
        if (trimSize>0) {
            if ((p.sysex.length>trimSize)&&(p.sysex[trimSize-1]== (byte)0xf7)){
                byte [] sysex = new byte[trimSize];
                System.arraycopy(p.sysex,0,sysex,0,trimSize);
                p.sysex=sysex;
            }
        }
     return p.sysex.length;
 }
     
     
     
     
     
}
