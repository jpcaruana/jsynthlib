/*
 * Copyright 2005 Ton Holsink
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

package synthdrivers.TCElectronicGMajor;

import core.*;
import javax.swing.JOptionPane;
import java.io.UnsupportedEncodingException;

public class TCElectronicGMajorBankDriver extends BankDriver {

    private static final SysexHandler SYS_REQ = new SysexHandler("F0 00 20 1F 00 48 45 *bankNum* *patchNum* F7");

    private final TCElectronicGMajorSingleDriver singleDriver;

    public TCElectronicGMajorBankDriver(TCElectronicGMajorSingleDriver singleDriver) {
        super("Bank", "Ton Holsink <a.j.m.holsink@chello.nl>>",
        TCElectronicGMajorConst.NUM_PATCH, TCElectronicGMajorConst.NUM_COLUMNS);

        this.singleDriver = singleDriver;
        patchNameSize   = TCElectronicGMajorConst.NAME_SIZE;
        bankNumbers =new String[] {"Factory","User"};
        patchNumbers=new String[TCElectronicGMajorConst.NUM_PATCH];
        System.arraycopy(DriverUtil.generateNumbers(1, TCElectronicGMajorConst.NUM_PATCH, "##"), 0, patchNumbers,  0, TCElectronicGMajorConst.NUM_PATCH);
        patchSize   = TCElectronicGMajorConst.SINGLE_SIZE * TCElectronicGMajorConst.NUM_PATCH;

        sysexID   = "F000201F004820";
        singleSysexID   = "F000201F004820";
        singleSize  = TCElectronicGMajorConst.SINGLE_SIZE;
    }

    public String getPatchName(Patch p, int patchNum) {
        int nameOfst = singleSize * patchNum + TCElectronicGMajorConst.NAME_OFFSET;
        try {
            return new String(((Patch)p).sysex, nameOfst, patchNameSize, "US-ASCII");
        } catch (UnsupportedEncodingException e) {
            return "---";
        }
    }

    public void setPatchName(Patch p, int patchNum, String name) {
        int nameOfst = singleSize * patchNum + TCElectronicGMajorConst.NAME_OFFSET;
        byte[] namebytes = name.getBytes();
        for (int i = 0; i < patchNameSize; i++)
            ((Patch)p).sysex[nameOfst + i] = namebytes[i];
    }

    public void calculateChecksum (Patch p) {
        for (int i = 0; i < TCElectronicGMajorConst.NUM_PATCH; i++)
            singleDriver.calculateChecksum(p,
                (singleSize * i) + TCElectronicGMajorConst.CHECKSUMSTART,
                (singleSize * i) + TCElectronicGMajorConst.CHECKSUMEND,
                (singleSize * i) + TCElectronicGMajorConst.CHECKSUMOFFSET);
    }

    public void putPatch(Patch bank, Patch p, int patchNum) {
        System.arraycopy(((Patch)p).sysex, 0,
            ((Patch)bank).sysex, singleSize * patchNum, singleSize);
            singleDriver.calculateChecksum(bank,
                (singleSize * patchNum) + TCElectronicGMajorConst.CHECKSUMSTART,
                (singleSize * patchNum) + TCElectronicGMajorConst.CHECKSUMEND,
                (singleSize * patchNum) + TCElectronicGMajorConst.CHECKSUMOFFSET);
    }

    public Patch getPatch(Patch bank, int patchNum) {
        byte[] sysex = new byte[singleSize];
        System.arraycopy(((Patch)bank).sysex, singleSize * patchNum,
            sysex, 0, singleSize);

        return new Patch(sysex, singleDriver);
    }

    public Patch createNewPatch() {
        byte[] sysex = new byte[singleSize * TCElectronicGMajorConst.NUM_PATCH];
        Patch bank = new Patch(sysex, this);
        Patch p = singleDriver.createNewPatch();
        for (int i = 0; i < TCElectronicGMajorConst.NUM_PATCH; i++)
            putPatch(bank, p, i);
        return bank;
    }

    public void requestPatchDump(int bankNum, int patchNum) {
        for (int i = 0; i < TCElectronicGMajorConst.NUM_PATCH; i++) {
            singleDriver.requestPatchDump(bankNum, i);
            try {Thread.sleep(500); } catch (Exception e){}
        }
    }

    public void storePatch (Patch p, int bankNum, int patchNum) {
        if (bankNum==0) {
            JOptionPane.showMessageDialog(PatchEdit.getInstance(),
            "You cannot store patches in the factory bank.\n\nPlease try the user bank...",
            "Store Patch",
            JOptionPane.WARNING_MESSAGE);
            return;
        }

        byte[] sysex = new byte[singleSize];
        Patch tmpPatch = new Patch(sysex, singleDriver);
        for (int i = 0; i < TCElectronicGMajorConst.NUM_PATCH; i++) {
            System.arraycopy(((Patch)p).sysex, singleSize * i,
                tmpPatch.sysex, 0, singleSize);

            //TODO: CREATE FACTORYBANK=0 AND USERBANK=1 CONSTANTS
            ((Patch)tmpPatch).sysex[7]=(byte)TCElectronicGMajorUtil.calcBankNum(1,i);
            ((Patch)tmpPatch).sysex[8]=(byte)TCElectronicGMajorUtil.calcPatchNum(1,i);
            sendPatchWorker(tmpPatch);
            //TODO: SLEEPTIME IN FILE OR MENU OPTION IF I KEEP THIS STORE METHOD
            try {Thread.sleep(1000); } catch (Exception e){}
        }
    }
}
