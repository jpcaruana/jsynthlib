package core;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * This interface should be implemented by any window which serves as
 * a holder or "basket" for patches.
 *
 * @version $Id$
 */
public interface PatchBasket {
    // Are the following javadoc comments correct?
    /** Import a patch from a file. */
    void importPatch(File file) throws IOException;
    /** Export a patch to a file. */
    void exportPatch(File file) throws IOException;

    /** Delete the selected patch. */
    void deleteSelectedPatch();
    /** Copy the selected patch. */
    void copySelectedPatch();
    /** Paste a patch. */
    void pastePatch();
    /** Paste a patch. */
    void pastePatch(IPatch p);
    /** Get the selected patch. */
    IPatch getSelectedPatch();

    /** Send the selected patch. */
    void sendSelectedPatch();
    /** SendTo the selected patch. */
    void sendToSelectedPatch();
    /** Play the selected patch. */
    void playSelectedPatch();
    /** Store the selected patch. */
    void storeSelectedPatch();
    /** Reassign the selected patch. */
    void reassignSelectedPatch();

    /** Invoke an editor for the selected patch. */
    JSLFrame editSelectedPatch();

    /** Return collection of all patches in basket. */
    ArrayList getPatchCollection();
}
