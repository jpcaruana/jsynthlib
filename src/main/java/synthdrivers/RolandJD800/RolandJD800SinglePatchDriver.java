/*
 * Copyright 2006 Robert Wirski
 *
 * This file is part of JSynthLib.
 *
 * JSynthLib is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
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

package synthdrivers.RolandJD800;

import java.io.InputStream;

import org.jsynthlib.core.Driver;
import org.jsynthlib.core.DriverUtil;
import org.jsynthlib.core.ErrorMsg;
import org.jsynthlib.core.IPatch;
import org.jsynthlib.core.ISinglePatch;
import org.jsynthlib.core.Patch;
import org.jsynthlib.core.SysexHandler;


/**
 * Single Patch Driver for Roland JD800. JD800 operates on memory areas instead
 * of a patch concept. The maximum size of data send in one sysex message is
 * 256 bytes. The single patch size is 384 bytes so it needs 2 sysex messages.
 * The author decided to join both sysex messages into one including the header
 * of the the first one at the beginning.
 */
public class RolandJD800SinglePatchDriver extends Driver {
    
    private static final SysexHandler SYS_REQ = new SysexHandler( "F0 41 @@ 3D 11 *AddrMSB* *Addr* 00 00 03 00 00 F7" );
    /** The MSB byte of the 3-byte patch address */
    final static int AddrMSB[] = {
            0x00,
            0x05, 0x05, 0x05, 0x05, 0x05, 0x05, 0x05, 0x05,
            0x05, 0x05, 0x05, 0x05, 0x05, 0x05, 0x05, 0x05,
            0x05, 0x05, 0x05, 0x05, 0x05, 0x05, 0x05, 0x05,
            0x05, 0x05, 0x05, 0x05, 0x05, 0x05, 0x05, 0x05,
            0x05, 0x05, 0x05, 0x05, 0x05, 0x05, 0x05, 0x05,
            0x05, 0x05, 0x05, 0x06, 0x06, 0x06, 0x06, 0x06,
            0x06, 0x06, 0x06, 0x06, 0x06, 0x06, 0x06, 0x06,
            0x06, 0x06, 0x06, 0x06, 0x06, 0x06, 0x06, 0x06};

    /** The middle byte of the 3-byte address*/
    final static int Addr[] = {
            0x00, 
            0x00, 0x03, 0x06, 0x09, 0x0C, 0x0F, 0x12, 0x15,
            0x18, 0x1B, 0x1E, 0x21, 0x24, 0x27, 0x2A, 0x2D,
            0x30, 0x33, 0x36, 0x39, 0x3C, 0x3F, 0x42, 0x45,
            0x48, 0x4B, 0x4E, 0x51, 0x54, 0x57, 0x5A, 0x5D,
            0x60, 0x63, 0x66, 0x69, 0x6C, 0x6F, 0x72, 0x75,
            0x78, 0x7B, 0x7E, 0x01, 0x04, 0x07, 0x0A, 0x0D,
            0x10, 0x13, 0x16, 0x19, 0x1C, 0x1F, 0x22, 0x25,
            0x28, 0x2B, 0x2E, 0x31, 0x34, 0x37, 0x3A, 0x3D};

    public RolandJD800SinglePatchDriver() {
        super("(Single)Patch", "Robert Wirski");
        
        sysexID = "F041**3D12";
        
        patchSize = JD800.SizeOfPatchSyx1 + JD800.SizeOfPatchSyx2; 
        patchNameStart = 8;
        patchNameSize = 16;
        deviceIDoffset = 2;
        
        bankNumbers = new String[] { "Internal" };

        patchNumbers = new String[JD800.nrOfPatchesInABank + 1];
        System.arraycopy(new String[] {"Patch Temporary"}, 0, patchNumbers, 0, 1);
        System.arraycopy(DriverUtil.generateNumbers(1, 8, "Patch I-10"), 0, patchNumbers, 1, 8);
        System.arraycopy(DriverUtil.generateNumbers(1, 8, "Patch I-20"), 0, patchNumbers, 9, 8);
        System.arraycopy(DriverUtil.generateNumbers(1, 8, "Patch I-30"), 0, patchNumbers, 17, 8);
        System.arraycopy(DriverUtil.generateNumbers(1, 8, "Patch I-40"), 0, patchNumbers, 25, 8);
        System.arraycopy(DriverUtil.generateNumbers(1, 8, "Patch I-50"), 0, patchNumbers, 33, 8);
        System.arraycopy(DriverUtil.generateNumbers(1, 8, "Patch I-60"), 0, patchNumbers, 41, 8);
        System.arraycopy(DriverUtil.generateNumbers(1, 8, "Patch I-70"), 0, patchNumbers, 49, 8);
        System.arraycopy(DriverUtil.generateNumbers(1, 8, "Patch I-80"), 0, patchNumbers, 57, 8);
                
    };

    /**
     * Creates a patch from a byte array. It must concatenate two sysex messages
     * into one.
     * @see org.jsynthlib.core.Driver#createPatch(byte[])
     */
    public IPatch createPatch(byte[] sysex) {
        byte[] out = new byte[JD800.SizeOfSinglePatch + JD800.SizeOfSyxHeader];
        System.arraycopy(sysex, 0,
                out, 0,JD800.SizeOfSyxHeader + JD800.MaxSyxDataBlock);   
        System.arraycopy(sysex, JD800.SizeOfSyxHeader + JD800.SizeOfPatchSyx1,
                out, JD800.SizeOfSyxHeader + JD800.MaxSyxDataBlock, 
                JD800.SizeOfSinglePatch - JD800.MaxSyxDataBlock);
        return new Patch(out, this);
    };

    /**
     * Compares the header & size of a Patch to this driver to see if
     * this driver is the correct one to support the patch. It is
     * overrided as it needs to serve for both sysex message sizes: original two
     * sysex messages and single concatenated one.
     * @see org.jsynthlib.core.Driver#supportsPatch(String, byte[])
     */
    public boolean supportsPatch(String patchString, byte[] sysex) {
        // The statement below has been changed when compared to original. The rest is the same.
        if (((JD800.SizeOfPatchSyx1 + JD800.SizeOfPatchSyx2) != sysex.length) &
                ((JD800.SizeOfSyxHeader + JD800.SizeOfSinglePatch) != sysex.length))
            return false;

        if (sysexID == null || patchString.length() < sysexID.length())
            return false;

        StringBuffer compareString = new StringBuffer();
        for (int i = 0; i < sysexID.length(); i++) {
            switch (sysexID.charAt(i)) {
            case '*':
                compareString.append(patchString.charAt(i));
                break;
            default:
                compareString.append(sysexID.charAt(i));
            }
        }
        return (compareString.toString().equalsIgnoreCase
                (patchString.substring(0, sysexID.length())));
    }

    /**
     * Sends a patch to a set location on a synth. The method creates
     * two sysex messages as it is expected for JD800.
     * <code>bankNum</code> is ignored.
     * @see Patch#send(int, int)
     */
    protected void storePatch(Patch p, int bankNum, int patchNum) {

        byte[] s1 = new byte[JD800.SizeOfPatchSyx1];
        byte[] s2 = new byte[JD800.SizeOfPatchSyx2];
        s1[0] = (byte) 0xF0;
        s1[1] = (byte) 0x41;
        s1[2] = (byte) getDeviceID();
        s1[3] = (byte) 0x3D;
        s1[4] = (byte) 0x12;
        s1[5] = (byte)AddrMSB[patchNum];
        s1[6] = (byte)Addr[patchNum];
        s1[7] = (byte) 0x00;
        System.arraycopy(p.sysex, JD800.SizeOfSyxHeader, s1,
                JD800.SizeOfSyxHeader, JD800.MaxSyxDataBlock);
        DriverUtil.calculateChecksum(s1, JD800.checksumStartSyx1,
                JD800.checksumEndSyx1, JD800.checksumOffsetSyx1);
        s1[265] = (byte) 0xF7;
        sendPatchWorker(new Patch(s1, this));
        try { 
            Thread.sleep(25);
        } catch (Exception e) {}
        s2[0] = (byte) 0xF0;
        s2[1] = (byte) 0x41;
        s2[2] = (byte) getDeviceID();
        s2[3] = (byte) 0x3D;
        s2[4] = (byte) 0x12;
        if (s2[6] == 0x7E) {
            s2[5] = (byte)(AddrMSB[patchNum] + 1);
            s2[6] = 0x00;}
        else
            s2[6] = (byte)(Addr[patchNum] + 2);
        System.arraycopy(p.sysex,
                JD800.SizeOfSyxHeader + JD800.MaxSyxDataBlock,
                s2, JD800.SizeOfSyxHeader,
                JD800.SizeOfSinglePatch - JD800.MaxSyxDataBlock);
        DriverUtil.calculateChecksum(s2, JD800.checksumStartSyx2,
                JD800.checksumEndSyx2, JD800.checksumOffsetSyx2);
        s2[137] = (byte) 0xF7;
        sendPatchWorker(new Patch(s2, this));
    };    
    
    /**
     * Sends a patch to the synth's edit buffer.<p>
     *
     * @see #storePatch(Patch, int, int)
     * @see Patch#send()
     * @see ISinglePatch#send()
     */
    protected void sendPatch(Patch p) {
        storePatch(p, 0, 0);
    };   
    
   
    /**
     * Creates a new initial patch. The patch is read from the JD800_InitialPatch.syx
     * file which is a dump received from the JD800 using Data Transfer button
     * and 'Patch dump' option. The messages are concatenated into one.
     */
    public Patch createNewPatch() {
            byte[] out = new byte[JD800.SizeOfSyxHeader + JD800.SizeOfSinglePatch];
            InputStream fileIn= getClass().getResourceAsStream("JD800_InitialPatch.syx");
            byte [] buffer =new byte [JD800.SizeOfPatchSyx1 + JD800.SizeOfPatchSyx2];
            try {
                fileIn.read(buffer);
                fileIn.close();
                System.arraycopy(buffer, 0, out, 0,
                        JD800.MaxSyxDataBlock + JD800.SizeOfSyxHeader);   
                System.arraycopy(buffer,
                        JD800.SizeOfPatchSyx1 + JD800.SizeOfSyxHeader,
                        out,
                        JD800.SizeOfSyxHeader + JD800.MaxSyxDataBlock,
                        JD800.SizeOfSinglePatch - JD800.MaxSyxDataBlock);
                return new Patch(out, this);
            }
            catch (Exception e)
                {ErrorMsg.reportError("Error","Unable to find JD800_InitialPatch.syx",e);return null;}
    };
    
    public void requestPatchDump(int bankNum, int patchNum) {
                   send(SYS_REQ.toSysexMessage(getDeviceID(), 
                           new SysexHandler.NameValue("AddrMSB", AddrMSB[patchNum]),
                           new SysexHandler.NameValue("Addr", Addr[patchNum])));
    }
}
