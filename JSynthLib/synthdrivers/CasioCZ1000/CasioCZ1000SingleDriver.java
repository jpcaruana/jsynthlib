/* Made by Yves Lefebvre
   email : ivanohe@abacom.com
   www.abacom.com/~ivanohe

   @version $Id$
*/

package synthdrivers.CasioCZ1000;
import core.*;
import javax.swing.*;
import java.io.*;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;
import javax.swing.JDialog;
import javax.swing.border.*;
import javax.swing.plaf.metal.*;
import java.beans.*;
import java.util.*;
import java.awt.event.*;
import java.awt.*;

// Note : on the casio, if you initiate a dump (from the PC or the Casio itself), you will get a patch
// of 263 bytes. you CAN'T send that patch back to the Casio... The patch must be 264 bytes long. 
// Anyway, since JSynthLib didn't support receiving dump from synth, this is not a problem for now
// Just keep that in mind however if you try to open a sysex file done by a dump request
//


public class CasioCZ1000SingleDriver extends Driver
{

    public CasioCZ1000SingleDriver()
    {
	super ("Single","Yves Lefebvre");
        sysexID="F04400007*";
        patchSize=264;
        patchNameStart=0;
        patchNameSize=0;
        deviceIDoffset=0;
        checksumStart=0;
        checksumEnd=0;
        checksumOffset=0;
        bankNumbers =new String[] {"Internal"};
        patchNumbers=new String[] {"01-","02-","03-","04-","05-","06-","07-",
                                   "08-","09-","10-","11-","12-","13-","14-","15-",
                                   "16-"};
    }

    public void storePatch (Patch p, int bankNum,int patchNum)
    {
        byte [] newsysex = new byte[264];
        System.arraycopy(p.sysex,0,newsysex,0,264);
        newsysex[4] = (byte)(0x70 + getChannel() -1); // must do it ourselve since sendPatchWorker didn't support
                                                 // adding midi channel info in half a byte
        newsysex[5] = (byte)(0x20); // 20 is to send data to the Casio
        newsysex[6] = (byte)(0x20+patchNum); //0x20 is the offset to internal memory location
        Patch patchtowrite = new Patch(newsysex, this);
        // need to convert to a "patch dump and write" format
        try {Thread.sleep(100); } catch (Exception e){}
        try {       
            send(patchtowrite.sysex);
        }catch (Exception e) {ErrorMsg.reportStatus(e);}
        try {Thread.sleep(100); } catch (Exception e){}
        setBankNum(bankNum);
        setPatchNum(patchNum);
    }

    public void sendPatch (Patch p)
    { 
        byte [] newsysex = new byte[264];
        System.arraycopy(p.sysex,0,newsysex,0,264);
        newsysex[4] = (byte)(0x70 + getChannel() -1); // must do it ourselve since sendPatchWorker didn't support
                                                 // adding midi channel info in half a byte
        newsysex[5] = (byte)(0x20); // 0x20 is to send data to the Casio
        newsysex[6] = (byte)(0x60); // 0x60 is edit buffer location
        Patch patchtowrite = new Patch(newsysex, this);
        try {       
            send(newsysex);
        }catch (Exception e) {ErrorMsg.reportStatus(e);}
    }

    public void calculateChecksum(Patch p,int start,int end,int ofs)
    {
        // no checksum
    }

    public Patch createNewPatch()
    {
        byte [] sysex = new byte[264];
        //System.arraycopy(CasioCZ1000InitPatch.initpatch,0,sysex,0,264);
        sysex[0]=(byte)0xF0;
        sysex[1]=(byte)0x44;
        sysex[2]=(byte)0x00;
        sysex[3]=(byte)0x00;
        sysex[4]=(byte)(0x70+getChannel()-1);
        sysex[5]=(byte)0x20;
        sysex[6]=(byte)0x60; // default to send for edit buffer
        sysex[263]=(byte)0xF7;
        Patch p = new Patch(sysex, this);
        calculateChecksum(p);	 
        return p;
    }

    //public JSLFrame editPatch(Patch p)
    // {
    //     
    // }

    public void setBankNum(int bankNum)
    {
    }

    public void setPatchNum(int patchNum)
    {
        // we only support internal memory now (add 0x20 to patchnum) since we can't write on preset or cartridge
        try {       
            send(0xC0 + (getChannel()-1), 0x20 + patchNum);
        } catch (Exception e) {};    
    }
}

