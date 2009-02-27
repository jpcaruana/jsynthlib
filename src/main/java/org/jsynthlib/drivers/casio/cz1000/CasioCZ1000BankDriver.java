/*
 * Copyright 2004 Yves Lefebvre
 *
 * This file is part of JSynthLib.
 *
 * JSynthLib is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or(at your option) any later version.
 *
 * JSynthLib is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JSynthLib; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 */
/* Made by Yves Lefebvre
   email : ivanohe@abacom.com
   www.abacom.com/~ivanohe

   @version $Id$
*/

package org.jsynthlib.drivers.casio.cz1000;
import javax.swing.JOptionPane;

import org.jsynthlib.core.BankDriver;
import org.jsynthlib.core.ErrorMsg;
import org.jsynthlib.core.Patch;

public class CasioCZ1000BankDriver extends BankDriver
{

    public CasioCZ1000BankDriver()
    {
	super ("Bank","Yves Lefebvre",16,4);
        sysexID="F04400007*";
        deviceIDoffset=0;
        bankNumbers =new String[] {"Internal Bank"};
        patchNumbers=new String[] {"01-","02-","03-","04-","05-","06-","07-",
                                   "08-","09-","10-","11-","12-","13-","14-","15-",
                                   "16-"};
   
        singleSysexID="F04400007*";
        singleSize=264;

    }

    public int getPatchStart(int patchNum)
    {
        int start=(264*patchNum);
        start+=7;  //sysex header
        return start;
    }

//    protected static void calculateChecksum(Patch p,int start,int end,int ofs)
//    {
//
//    }


    public void calculateChecksum (Patch p)
    {

    }                                     

    public void putPatch(Patch bank,Patch p,int patchNum)
    {
        // This method is called when doing a paste (from another bank or a single)
        // the patch received will be a single dump (meant for the edit buffer)
        // we need to extract the actual patch info and paste it in the bank itself

        if (!canHoldPatch(p))
        {
            JOptionPane.showMessageDialog(null, "This type of patch does not fit in to this type of bank.","Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        System.arraycopy(((Patch)p).sysex,7,((Patch)bank).sysex,getPatchStart(patchNum),264-7);
        calculateChecksum(bank);
    }
    public Patch getPatch(Patch bank, int patchNum)
    {
        // this method is call when you have a bank opened and want to send or play individual patches
        // OR when you do a Cut/Copy
        // The method is call to retreive a single patch to send using the default sendPatch()
        // Must remove the bank and patch number information and convert to a single dump for edit buffer
        try
        {
            byte [] sysex=new byte[264];

            sysex[0]=(byte)0xF0;
            sysex[1]=(byte)0x44;
            sysex[2]=(byte)0x00;
            sysex[3]=(byte)0x00;
            sysex[4]=(byte)(0x70 + getChannel()-1);
            sysex[5]=(byte)0x20;
            sysex[6]=(byte)(0x60); // to send to edit buffer
            sysex[263]=(byte)0xF7;
            System.arraycopy(((Patch)bank).sysex,getPatchStart(patchNum),sysex,7,264-7);
            Patch p = new Patch(sysex, getDevice());
            p.calculateChecksum();
            return p;
        }catch (Exception e) {ErrorMsg.reportError("Error","Error in Nova1 Bank Driver",e);return null;}
    }

    public Patch createNewPatch()
    {
        // There is no additionnal header on the Bank dump itself.

        byte [] sysex = new byte[(264*16)];
        byte [] sysexHeader = new byte[7];

        sysexHeader [0]=(byte)0xF0;
        sysexHeader [1]=(byte)0x44;
        sysexHeader [2]=(byte)0x00;
        sysexHeader [3]=(byte)0x00;
        sysexHeader [4]=(byte)(0x70 + getChannel()-1);
        sysexHeader [5]=(byte)0x20; // store command
        sysexHeader [6]=(byte)0x20; // patch number (internal start at 0x20)

        Patch p = new Patch(sysex, this);
        for (int i=0;i<16;i++) 
        {
            sysexHeader [6] = (byte)(0x20 + i); // patch nunmber
            System.arraycopy(sysexHeader,0,p.sysex,i*264,7);
        
            p.sysex[(263*(i+1))] = (byte)0xF7;
        }
        calculateChecksum(p);	 
        return p;
    }

    public void storePatch (Patch bank, int bankNum,int patchNum)
    { 
        // This is called when the user want to Store a bank.
        byte [] newsysex = new byte[264];
        Patch p = new Patch(newsysex, getDevice());
        try 
        {       
            for (int i=0;i<16;i++) 
            {
                System.arraycopy(((Patch)bank).sysex,264*i,p.sysex,0,264);
                sendPatchWorker(p);
                Thread.sleep(100); // a small delay to play safe
            }
        }catch (Exception e) {ErrorMsg.reportError("Error","Unable to send Patch",e);}
    }


    public void setBankNum(int bankNum)
    {
    }
 
    protected String getPatchName(Patch bank, int patchNum) {
        return "-";
    }

    protected void setPatchName(Patch bank, int patchNum, String name) {
        // do nothing
    }
}
