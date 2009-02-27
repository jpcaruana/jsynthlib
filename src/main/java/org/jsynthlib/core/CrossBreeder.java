package org.jsynthlib.core;

import java.util.ArrayList;

import javax.sound.midi.ShortMessage;
import javax.sound.midi.SysexMessage;

/*
 * Generates a patch with random combinations of the patches for a
 * driver in a library.
 *
 * As of version 0.14 the actual functionality of the crossbreeder
 * dialog is hidden away in this file. It seems like a good idea to be
 * seperating functionality from GUI code, something I didn't do when
 * I first started JSynthLib.
 */
 /**
 * @author bklock
 * @version $Id$
 * @see CrossBreedDialog
 */
class CrossBreeder {
    /** The patch we are working on. */
    private IPatch patch;
    /** The patch library we are working on. */
    private ArrayList lib;
    /** The number of patches in the patch library */
    private int libSize;

    void generateNewPatch(PatchBasket library) {
        lib = library.getPatchCollection();
        libSize = lib.size();

        // get a base patch.
        IPatch base = library.getSelectedPatch(); 
        ErrorMsg.reportStatus("base : " + base);
        int sysexSize = base.getSize();
        ErrorMsg.reportStatus("length : " + sysexSize);
        IPatchDriver drv = base.getDriver();

        byte[] dsysex = new byte[sysexSize];
        dsysex[0] = (byte) SysexMessage.SYSTEM_EXCLUSIVE;
        for (int i = 1; i < sysexSize - 1; i++) {
            IPatch source;
            byte[] ssysex;
            // look for a patch with the same Driver and enough length
            do {
                source = getRandomPatch();
                ssysex = source.getByteArray();
            } while (source.getDriver() != drv || ssysex.length - 1 < i);
            dsysex[i] = ssysex[i];
        }
        dsysex[dsysex.length - 1] = (byte) ShortMessage.END_OF_EXCLUSIVE; // EOX

        patch = (drv.createPatch(dsysex));
        ErrorMsg.reportStatus("done : " + patch);
    }

    IPatch getCurrentPatch() {
	return patch;
    }

    private IPatch getRandomPatch() {
	int num = (int) (Math.random() * libSize);
	//ErrorMsg.reportStatus("num : " + num + " / " + libSize);
	return (IPatch) lib.get(num);
    }
}
