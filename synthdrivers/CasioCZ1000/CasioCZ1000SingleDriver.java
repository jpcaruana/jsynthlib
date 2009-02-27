/*
 * Copyright 2004-5 Yves Lefebvre, Bill Zwicky
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

package synthdrivers.CasioCZ1000;

import org.jsynthlib.core.Driver;
import org.jsynthlib.core.ErrorMsg;
import org.jsynthlib.core.JSLFrame;
import org.jsynthlib.core.Patch;


/**
 * Single patch driver for Casio CZ-101 and CZ-1000.
 * Note : on the casio, if you initiate a dump (from the PC or the Casio itself), 
 * you will get a patch of 263 bytes. you CAN'T send that patch back to the Casio... 
 * The patch must be 264 bytes long.  supportsPatch and createPatch fix up short
 * patches.
 * 
 * @author Yves Lefebvre
 * @author Bill Zwicky
 */
public class CasioCZ1000SingleDriver extends Driver
{
    // mis-ordered to make Internal the default
    private String[] BANK_NAMES = {"Internal", "Cartridge", "Preset"};
    private int[]    BANK_NUMS  = { 0x20, 0x40, 0x00 };
    private int curBank = 0, curPatch = 0;

    public CasioCZ1000SingleDriver()
    {
		super ("Single", "Yves Lefebvre, Bill Zwicky");
        sysexID="F04400007*";
        patchSize=264;
        patchNameStart=0;
        patchNameSize=0;
        deviceIDoffset=0;
        checksumStart=0;
        checksumEnd=0;
        checksumOffset=0;
        bankNumbers = BANK_NAMES;
        patchNumbers=new String[] {"01-","02-","03-","04-","05-","06-","07-",
                                   "08-","09-","10-","11-","12-","13-","14-","15-",
                                   "16-"};
    }

    private int computeSlot(int bankNum, int patchNum) {
        return BANK_NUMS[bankNum]+patchNum;
    }
    
    public void storePatch (Patch p, int bankNum,int patchNum)
    {
        byte [] newsysex = new byte[264];
        System.arraycopy(((Patch)p).sysex,0,newsysex,0,264);
        newsysex[4] = (byte)(0x70 + getChannel() -1); // must do it ourselve since sendPatchWorker didn't support
                                                 // adding midi channel info in half a byte
        newsysex[5] = (byte)(0x20); // 20 is to send data to the Casio
        newsysex[6] = (byte)computeSlot(bankNum,patchNum);
        // need to convert to a "patch dump and write" format
        try {Thread.sleep(100); } catch (Exception e){}
        try {
            send(newsysex);
        }catch (Exception e) {ErrorMsg.reportStatus(e);}
        try {Thread.sleep(100); } catch (Exception e){}
        setBankNum(bankNum);
        setPatchNum(patchNum);
    }

    public void sendPatch (Patch p)
    {
        byte [] newsysex = new byte[264];
        System.arraycopy(((Patch)p).sysex,0,newsysex,0,264);
        newsysex[4] = (byte)(0x70 + getChannel() -1); // must do it ourselve since sendPatchWorker didn't support
                                                 // adding midi channel info in half a byte
        newsysex[5] = (byte)(0x20); // 0x20 is to send data to the Casio
        newsysex[6] = (byte)(0x60); // 0x60 is edit buffer location
        try {
            send(newsysex);
        }catch (Exception e) {ErrorMsg.reportStatus(e);}
    }

    public void requestPatchDump(int bankNum, int patchNum) {
        // Assembled by hand due to channel being sent twice.
        // Also, according to Casio docs, messages to and from
        // keyboard are interleaved; however, we can just shoot
        // the entire packet over, and JSL will catch everything
        // that comes back.
        byte chan = (byte)(0x70+getChannel()-1);
        byte slot = (byte)computeSlot(bankNum,patchNum);
        byte sysex[] = {
                (byte)0xF0, 0x44, 0, 0, chan, 0x10, slot,
                // Casio sends short sysex header here
                // F0 44 00 00 chan 30
                chan, 0x31,
                // Casio sends 'tone data' here
                (byte)0xF7
        };
        send(sysex);
    }

    protected void calculateChecksum(Patch p,int start,int end,int ofs)
    {
        // no checksum
    }

    /**
     * Whether createNewPatch works.
     */
    public boolean canCreatePatch() {
        return true;
    }

    public Patch createNewPatch()
    {
        byte [] sysex = new byte[264];
        sysex[0]=(byte)0xF0;
        sysex[1]=(byte)0x44;
        sysex[2]=(byte)0x00;
        sysex[3]=(byte)0x00;
        sysex[4]=(byte)(0x70+getChannel()-1);
        sysex[5]=(byte)0x20;
        sysex[6]=(byte)0x60;  // default is edit buffer
        sysex[263]=(byte)0xF7;
        Patch p = new Patch(sysex, this);
        calculateChecksum(p);
        return p;
    }

    public void setBankNum(int bankNum)
    {
        this.curBank = bankNum;
        try {
            send(0xC0 + (getChannel()-1), computeSlot(curBank,curPatch));
        } catch (Exception e) {};
    }

    public void setPatchNum(int patchNum)
    {
        this.curPatch = patchNum;
        try {
            send(0xC0 + (getChannel()-1), computeSlot(curBank,curPatch));
        } catch (Exception e) {};
    }

    public JSLFrame editPatch(Patch p) {
        return new CasioCZ1000SingleEditor(p);
    }

    /** No support, so ignore quietly. */
    protected void setPatchName(Patch p, String name) {
    }
}
