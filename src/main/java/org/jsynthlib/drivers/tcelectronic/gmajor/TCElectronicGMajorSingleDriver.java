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

package org.jsynthlib.drivers.tcelectronic.gmajor;
import javax.swing.JOptionPane;

import org.jsynthlib.core.Driver;
import org.jsynthlib.core.DriverUtil;
import org.jsynthlib.core.JSLFrame;
import org.jsynthlib.core.Patch;
import org.jsynthlib.core.PatchEdit;
import org.jsynthlib.core.SysexHandler;

/** Single patch driver for TC Electronic G-Major
 *
 */
public class TCElectronicGMajorSingleDriver extends Driver {

    private static final SysexHandler SYS_REQ = new SysexHandler("F0 00 20 1F 00 48 45 *bankNum* *patchNum* F7");

    public TCElectronicGMajorSingleDriver() {
        super("Single", "Ton Holsink <a.j.m.holsink@chello.nl>");
        sysexID="F000201F004820";

        patchSize=TCElectronicGMajorConst.SINGLE_SIZE;
        patchNameStart=TCElectronicGMajorConst.NAME_OFFSET;
        patchNameSize=TCElectronicGMajorConst.NAME_SIZE;
        deviceIDoffset=4;
        checksumStart=TCElectronicGMajorConst.CHECKSUMSTART;
        checksumEnd=TCElectronicGMajorConst.CHECKSUMEND;
        checksumOffset=TCElectronicGMajorConst.CHECKSUMOFFSET;
        bankNumbers =new String[] {"Factory","User"};
        patchNumbers=new String[TCElectronicGMajorConst.NUM_PATCH];
        System.arraycopy(DriverUtil.generateNumbers(1, TCElectronicGMajorConst.NUM_PATCH, "##"), 0, patchNumbers,  0, TCElectronicGMajorConst.NUM_PATCH);
    }

    public void storePatch(Patch p, int bankNum,int patchNum) {
        if (bankNum==0) {
            JOptionPane.showMessageDialog(PatchEdit.getInstance(),
            "You cannot store a patch in the factory bank.\n\nPlease try the user bank...",
            "Store Patch",
            JOptionPane.WARNING_MESSAGE);
            return;
        }

        setPatchNum(patchNum);
        try {Thread.sleep(100); } catch (Exception e){}
        ((Patch)p).sysex[7]=(byte)TCElectronicGMajorUtil.calcBankNum(bankNum,patchNum);
        ((Patch)p).sysex[8]=(byte)TCElectronicGMajorUtil.calcPatchNum(bankNum,patchNum);
        sendPatchWorker(p);
        try {Thread.sleep(100); } catch (Exception e){}
        setPatchNum(patchNum);
    }

    public void sendPatch (Patch p) {
        ((Patch)p).sysex[7]=(byte)0x00;
        ((Patch)p).sysex[8]=(byte)0x00;
        sendPatchWorker(p);
    }

    protected void calculateChecksum(Patch p) {
        calculateChecksum(p, checksumStart, checksumEnd, checksumOffset);
    }

    protected void calculateChecksum(Patch patch, int start, int end, int offset) {
        patch.sysex[offset] = TCElectronicGMajorUtil.calcChecksum(patch.sysex, start, end);
    }

    public Patch createNewPatch() {
        return (Patch) DriverUtil.createNewPatch(this, TCElectronicGMajorConst.PATCHFILENAME, TCElectronicGMajorConst.SINGLE_SIZE);
    }

    public JSLFrame editPatch(Patch p) {
        return new TCElectronicGMajorSingleEditor((Patch)p);
    }

    public void requestPatchDump(int bankNum, int patchNum) {
        System.out.println("BANKNUM: "+bankNum+"PATCHNUM: "+patchNum);
        send(SYS_REQ.toSysexMessage(getChannel(),
            new SysexHandler.NameValue("bankNum",TCElectronicGMajorUtil.calcBankNum(bankNum,patchNum)),
            new SysexHandler.NameValue("patchNum",TCElectronicGMajorUtil.calcPatchNum(bankNum,patchNum))));
    }

}
