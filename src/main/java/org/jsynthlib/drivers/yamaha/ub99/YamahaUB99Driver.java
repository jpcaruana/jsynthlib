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

package org.jsynthlib.drivers.yamaha.ub99;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.jsynthlib.core.Driver;
import org.jsynthlib.core.DriverUtil;
import org.jsynthlib.core.ErrorMsg;
import org.jsynthlib.core.JSLFrame;
import org.jsynthlib.core.Patch;

/** Single patch driver for Yamaha UB99
 *
 */
public class YamahaUB99Driver extends Driver {

    public YamahaUB99Driver() {
        super("Single", "Ton Holsink <a.j.m.holsink@chello.nl>");
        sysexID = null;

        patchSize = YamahaUB99Const.SINGLE_SIZE;
        patchNameStart = YamahaUB99Const.NAME_OFFSET;
        patchNameSize = YamahaUB99Const.NAME_SIZE;
        deviceIDoffset = -1;
        bankNumbers = new String[] {"Preset", "User"};
        patchNumbers = new String[YamahaUB99Const.NUM_PATCH];
        System.arraycopy(DriverUtil.generateNumbers(1, YamahaUB99Const.NUM_PATCH, "##"), 0, patchNumbers,  0, YamahaUB99Const.NUM_PATCH);
    }

    public void sendThisPatch (Patch p, int nr, int typ) {
        try{
            // SEND PATCH sysEx command
            byte[] buf = new byte[] {(byte)0xF0,(byte)0x43,(byte)(0x7D),(byte)0x30,(byte)0x55,(byte)0x42,(byte)0x39,(byte)0x39,(byte)0x00,(byte)0x00,(byte)0x30,(byte)typ,(byte)nr,(byte)0x00,(byte)0xF7};
            DriverUtil.calculateChecksum(buf, YamahaUB99Const.CHECKSUMSTART, buf.length-3, buf.length-2);
            send(buf);

            // send PATCH TYPE and NAME
            try {Thread.sleep(100); } catch (Exception e){}
            buf = new byte[47];
            buf[0] = (byte)0xF0; buf[1] = (byte)0x43; buf[2] = (byte)0x7D; buf[3] = (byte)0x30;
            buf[4] = (byte)0x55; buf[5] = (byte)0x42; buf[6] = (byte)0x39; buf[7] = (byte)0x39;
            buf[8] = (byte)0x00; buf[9] = (byte)0x20; buf[10] = (byte)0x20; buf[11] = (byte)0x00;
            buf[12] = (byte)0x00; buf[13] = (byte)0x00;
            for(int i=14; i<=44; i++){
                buf[i] = p.sysex[i-13];
            }
            buf[45] = (byte)0x00; buf[46] = (byte)0xF7;
            DriverUtil.calculateChecksum(buf, YamahaUB99Const.CHECKSUMSTART, buf.length-3, buf.length-2);
            send(buf);

            // send PATCH PARAMS
            try {Thread.sleep(100); } catch (Exception e){}
            buf = new byte[142];
            buf[0] = (byte)0xF0; buf[1] = (byte)0x43; buf[2] = (byte)0x7D; buf[3] = (byte)0x30;
            buf[4] = (byte)0x55; buf[5] = (byte)0x42; buf[6] = (byte)0x39; buf[7] = (byte)0x39;
            buf[8] = (byte)0x00; buf[9] = (byte)0x7F; buf[10] = (byte)0x20; buf[11] = (byte)0x01;
            buf[12] = (byte)0x00;
            for(int i=13; i<=139; i++){
                buf[i] = p.sysex[i+19];
            }
            buf[140] = (byte)0x00; buf[141] = (byte)0xF7;
            DriverUtil.calculateChecksum(buf, YamahaUB99Const.CHECKSUMSTART, buf.length-3, buf.length-2);
            send(buf);

            // send END sysEx command
            try {Thread.sleep(100); } catch (Exception e){}
            buf = new byte[] {(byte)0xF0,(byte)0x43,(byte)(0x7D),(byte)0x30,(byte)0x55,(byte)0x42,(byte)0x39,(byte)0x39,(byte)0x00,(byte)0x00,(byte)0x30,(byte)(typ+16),(byte)nr,(byte)0x00,(byte)0xF7};
            DriverUtil.calculateChecksum(buf, YamahaUB99Const.CHECKSUMSTART, buf.length-3, buf.length-2);
            send(buf);

        }catch (Exception e){ErrorMsg.reportError("Error","Unable to Send Patch",e);}

    }

    public void sendPatch (Patch p) {
        sendThisPatch(p, 0x00, 0x03);
    }

    protected void calculateChecksum(Patch p) {
    }

    protected void calculateChecksum(Patch patch, int start, int end, int offset) {
    }

    public byte[] createNewPatchArray() {
        byte[] b = new byte[YamahaUB99Const.SINGLE_SIZE];
        b[1] = 47;
        System.arraycopy(YamahaUB99Const.NEW_PATCH_NAME, 0, b, YamahaUB99Const.NAME_OFFSET, YamahaUB99Const.NAME_SIZE);
        return b;
    }

    public Patch createNewPatch() {
        return new Patch(createNewPatchArray(), this);
    }

    public JSLFrame editPatch(Patch p) {
        return new YamahaUB99Editor((Patch)p);
    }

    public Patch getDefaultValues(Patch p, int patchno) {
        int size = YamahaUB99Const.SINGLE_SIZE;
        String fileName = YamahaUB99Const.DEFAULT_FILENAME;
        byte[] buffer = new byte[YamahaUB99Const.DEFAULT_SIZE];

        try {
            InputStream fileIn = getClass().getResourceAsStream(fileName);

            if (fileIn != null) {
                fileIn.read(buffer);
                fileIn.close();
                System.arraycopy(buffer, patchno * size, p.sysex, 0, YamahaUB99Const.NAME_OFFSET);
                System.arraycopy(buffer, patchno * size + (YamahaUB99Const.NAME_OFFSET + YamahaUB99Const.NAME_SIZE), p.sysex, YamahaUB99Const.NAME_OFFSET + YamahaUB99Const.NAME_SIZE, size - (YamahaUB99Const.NAME_OFFSET + YamahaUB99Const.NAME_SIZE));
                return p;
            }
            else {
                throw new FileNotFoundException("File: " + fileName + " does not exist!");
            }
        } catch (IOException e) {
            ErrorMsg.reportError("Error", "Unable to open " + fileName, e);
            return null;
        }
    }

}
