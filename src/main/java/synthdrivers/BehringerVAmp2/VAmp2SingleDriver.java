/*
 * Copyright 2005 Jeff Weber
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

package synthdrivers.BehringerVAmp2;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.jsynthlib.core.Driver;
import org.jsynthlib.core.JSLFrame;
import org.jsynthlib.core.Patch;
import org.jsynthlib.core.SysexHandler;

/** Behringer VAmp2 Single Driver.
 *
 * @author Jeff Weber
 */
public class VAmp2SingleDriver extends Driver {
    /** VAmp2 Dump Request
     */
    protected static final SysexHandler SYS_REQ = new SysexHandler(
            Constants.VAMP2_SINGLE_DUMP_REQ_ID); //VAmp2 Dump Request

    /** Constructs a VAmp2Driver.
     */
    public VAmp2SingleDriver() {
        this(Constants.VAMP2_PATCH_TYP_STR);
        bankNumbers = Constants.PRGM_BANK_LIST;
        patchNumbers = Constants.PRGM_PATCH_LIST;
    }

    /** Constructs a VAmp2Driver.
     */
    protected VAmp2SingleDriver(String patchType) {
        super(patchType, Constants.AUTHOR);
        sysexID = Constants.VAMP2_SINGLE_MATCH_ID;
        patchSize = Constants.HDR_SIZE + Constants.SINGLE_PATCH_SIZE + 1;
        deviceIDoffset = Constants.DEVICE_ID_OFFSET;
        patchNameStart = Constants.HDR_SIZE + Constants.PATCH_NAME_START;
        patchNameSize = Constants.PATCH_NAME_SIZE;
    }

    /* (non-Javadoc)
     * @see core.Driver#setPatchNum(int)
     */
    protected void setPatchNum(int patchNum) {
    }

    /* (non-Javadoc)
     * @see core.Driver#setBankNum(int)
     */
    protected void setBankNum(int bankNum) {
    }

    /* (non-Javadoc)
     * @see core.Driver#calculateChecksum(core.Patch)
     */
    protected void calculateChecksum(Patch p) {
    }

    /* (non-Javadoc)
     * @see core.Driver#calculateChecksum(core.Patch, int, int, int)
     */
    protected void calculateChecksum(Patch patch, int start, int end, int offset) {
    }

    /** Sends a single program patch to the edit buffer. 
     * @param p
     *              The patch to be sent.
     */
    protected void sendPatch(Patch p) {
        byte newSysex[] = new byte[p.sysex.length];
        System.arraycopy(p.sysex, 0, newSysex, 0, p.sysex.length);
        newSysex[7] = (byte) 0x7f; // Set the patch location to 127 (edit buffer)
        sendPatchWorker(new Patch(newSysex, this));
    }

    /** Sends a a single program patch to a set patch location in the device.
     * @param p
     *                  The patch to be send.
     * @param bankNum
     *                  The user bank number in the range 0 to 124.
     * @param patchNum
     *                  The patch number within the bank, in the range 0 to 4.
     */
    protected void storePatch(Patch p, int bankNum, int patchNum) {
        int progNum = bankNum * 5 + patchNum;
        p.sysex[7] = (byte) progNum;
        sendPatchWorker(p);
        try {
            Thread.sleep(Constants.PATCH_SEND_INTERVAL); // Delay so VAmp can keep up (pauses between each patch when sending a whole bank of patches)
        } catch (Exception e) {
        }
    }

    /** Presents a dialog instructing the user to play his instrument.
     * The V-Amp 2 does not "Play" patches, so a dialog is presented instead.
     * @param p
     *              The patch to be "played".
     */
    protected void playPatch(Patch p) {
        JFrame frame = new JFrame();
        JOptionPane.showMessageDialog(frame, Constants.PLAY_CMD_MSG);
    }

    /** Requests a dump of the VAmp2 patch.
     * This patch does not utilize bank select. To the user, the device appears
     * to have 25 banks of 5 patches each, but internally it has only one bank
     * of 125 patches, selected by a single program change. The program change
     * number is calculated as progNum = bankNum * 5 + patchNum.
     * @param bankNum
     *              The number of the bank containing the requested patch.
     * @param patchNum
     *              The number of the requested patch.
     */
    public void requestPatchDump(int bankNum, int patchNum) {
        int channel = getChannel();
        int progNum = bankNum * 5 + patchNum;
        SysexHandler.NameValue nVs[] = new SysexHandler.NameValue[2];
        nVs[0] = new SysexHandler.NameValue("channel", channel);
        nVs[1] = new SysexHandler.NameValue("progNum", progNum);
        send(SYS_REQ.toSysexMessage(channel, nVs));
    }

    /** Creates a new patch with default values.
     * @return
     *          A reference to a new patch containing default values
     */
    protected Patch createNewPatch() {
        Patch p = new Patch(Constants.NEW_SINGLE_SYSEX, this);
        p.sysex[4] = (byte) getChannel();
        return p;
    }

    /** Opens an edit window on the specified patch.
     * @param p
     *          The patch to be edited.
     */
    protected JSLFrame editPatch(Patch p) {
        return new VAmp2Editor((Patch) p);
    }
}