/*
 * Scene.java
 *
 * Created on 18. April 2002, 20:51
 *
 * Refactored from Performance.java
 */

package core;
import java.io.*;
import java.awt.datatransfer.*;
import javax.swing.JComboBox;
/**
 * @version $Id$
 * @author  Gerrit Gehnen
 */
public class Scene extends java.lang.Object implements Serializable, Transferable {
    
    private Patch patch;
    
    private int bankNumber;
    
    private int patchNumber;
    
    private StringBuffer comment;
    
    /** Creates a new instance of Scene */
    public Scene() {
        patch=new Patch(new byte[1024], (Driver) null);
        bankNumber=0;
        patchNumber=0;
        comment=new StringBuffer();
    }
    
    public Scene(Patch p) {
        patch=p;
        bankNumber=0;
        patchNumber=0;
        comment=p.comment;
        
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
    public core.Patch getPatch() {
        return patch;
    }
    
    /** Setter for property patch.
     * @param patch New value of property patch.
     */
    public void setPatch(core.Patch patch) {
        this.patch = patch;
    }
    
    public Object getTransferData(java.awt.datatransfer.DataFlavor dataFlavor) throws java.awt.datatransfer.UnsupportedFlavorException, java.io.IOException {
        return this;
    }
    
    public java.awt.datatransfer.DataFlavor[] getTransferDataFlavors() {
        System.out.println("getTransferDataFlavors "+patch.driverNum);
        DataFlavor[] df=new DataFlavor[1];
        df[0]= new DataFlavor(patch.getDriver().getClass(),patch.getDriver().toString());
        return df;
    }
    
    public boolean isDataFlavorSupported(java.awt.datatransfer.DataFlavor dataFlavor) {
        System.out.println("isDataFlavorSupported "+patch.driverNum);
        
        if( dataFlavor.equals(new DataFlavor(patch.getDriver().getClass(),patch.getDriver().toString()))) {
            return true;
        }
        return false;
    }
    
    /** Getter for property comment.
     * @return Value of property comment.
     */
    public java.lang.StringBuffer getComment() {
        return comment;
    }
    
    /** Setter for property comment.
     * @param comment New value of property comment.
     */
    public void setComment(java.lang.StringBuffer comment) {
        this.comment = comment;
    }
    
    
}
