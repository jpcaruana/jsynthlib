/*
 * Scene.java
 *
 * Created on 18. April 2002, 20:51
 *
 * Refactored from Performance.java
 */

package core;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.Serializable;
/**
 * A scene is a container for all patches and their explicite
 * bank/patch locations at the synths for a concrete singular
 * song/sound.  You can put all needed patches of all used synths in
 * one scene and transfer the whole stuff in one step to your synths.
 *
 * @version $Id$
 * @author  Gerrit Gehnen
 */
public class Scene implements Cloneable, Transferable, Serializable {

    private IPatch patch;

    private int bankNumber;

    private int patchNumber;

    private String comment;

    // This is used by java to maintain backwords compatibility.
    static final long serialVersionUID = -7048122592493606437L;

    /** Creates a new instance of Scene */
    /*
    public Scene() {
        patch=new Patch(new byte[1024], (Driver) null);
        bankNumber=0;
        patchNumber=0;
        comment=new StringBuffer();
    }
    */
    public Scene(IPatch p) {
        patch=p;
        bankNumber=0;
        patchNumber=0;
        comment=p.getComment();

    }
    /** Getter for property bankNumber.
     * @return Value of property bankNumber.
     */
    public int getBankNumber() {
        return bankNumber;
    }

    /** Setter for property bankNumber.
     * @param bankNumber New value of property bankNumber.
     */
    public void setBankNumber(int bankNumber) {
        this.bankNumber = bankNumber;
    }

    /** Getter for property patchNumber.
     * @return Value of property patchNumber.
     */
    public int getPatchNumber() {
        return patchNumber;
    }

    /** Setter for property patchNumber.
     * @param patchNumber New value of property patchNumber.
     */
    public void setPatchNumber(int patchNumber) {
        this.patchNumber = patchNumber;
    }

    /** Getter for property patch.
     * @return Value of property patch.
     */
    public IPatch getPatch() {
        return patch;
    }

    /** Setter for property patch.
     * @param patch New value of property patch.
     */
    public void setPatch(IPatch patch) {
        this.patch = patch;
    }

    /** Getter for property comment.
     * @return Value of property comment.
     */
    public String getComment() {
        return comment;
    }

    /** Setter for property comment.
     * @param comment New value of property comment.
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    // Transferable interface methods
    public Object getTransferData(DataFlavor flavor)
            throws UnsupportedFlavorException {
        ErrorMsg.reportStatus("Scene.getTransferData: flavor=" + flavor);
        //ErrorMsg.reportStatus("Patch.getTransferData: Patch=" + p + ", " + p.comment);
        if (flavor.match(PatchTransferHandler.SCENE_FLAVOR))
            return clone();
        else if (flavor.match(PatchTransferHandler.PATCH_FLAVOR))
            return getPatch().clone();
        else
            throw new UnsupportedFlavorException(flavor);
    }

    public boolean isDataFlavorSupported(final DataFlavor flavor) {
        ErrorMsg.reportStatus("Scene.isDataFlavorSupported " + flavor);
        return (flavor.match(PatchTransferHandler.SCENE_FLAVOR)
                || flavor.match(PatchTransferHandler.PATCH_FLAVOR));
    }

    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[] { 
                PatchTransferHandler.SCENE_FLAVOR,
                PatchTransferHandler.PATCH_FLAVOR
        };
    }
    // end of Transferable interface methods

    // Clone interface method
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            // Cannot happen -- we support clone, and so do arrays
            throw new InternalError(e.toString());
        }
    }
    // end of Clone interface method
}
