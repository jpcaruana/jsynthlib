package core;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * This interface should be implemented by any window which serves as
 * a holder or "basket" for patches. It is implemented by
 * LibraryFrame, the Library window and also BankEditorFrame.
 *
 * @version $Id$
 */
public interface PatchBasket {
    // Are the following javadoc comments correct?
    // method name should start with a lower charactor.
    /** Import a patch. */
    void ImportPatch(File file) throws IOException;
    /** Export a patch. */
    void ExportPatch(File file) throws IOException;
    /** Delete the selected patch. */
    void DeleteSelectedPatch();
    /** Copy the selected patch. */
    void CopySelectedPatch();
    /** Send the selected patch. */
    void SendSelectedPatch();
    /** SendTo the selected patch. */
    void SendToSelectedPatch();
    /** Play the selected patch. */
    void PlaySelectedPatch();
    /** Store the selected patch. */
    void StoreSelectedPatch();
    /** Reassign the selected patch. */
    void ReassignSelectedPatch();
    /** Invoke an editor for the selected patch. */
    JSLFrame EditSelectedPatch();
    /** Paste a patch. */
    void PastePatch();
    /** Return collection of all patches in basket. */
    ArrayList getPatchCollection();
    Patch GetSelectedPatch();
    void PastePatch(Patch p);
}
