/*
 * AbstractLibraryFrame.java
 *
 * Created on 24. September 2002, 10:52
 */

package core;

/** Abstract interface for unified handling of
 * Library and Scene frames.
 * @author Gerrit.Gehnen
 * @version $Id$
 */
public interface AbstractLibraryFrame extends PatchBasket {
    /**
     * @return The visual table component for this Frame.
     */    
    public DNDLibraryTable getTable();
    /**
     * @return The abstractPatchListModel as unified source of patches
     * in all types of Libraryframes
     */    
    AbstractPatchListModel getAbstractPatchListModel();
   
    public void ExtractSelectedPatch();
   
}
