/*
 * AbstractLibraryFrame.java
 *
 * Created on 24. September 2002, 10:52
 */
package core;

import javax.swing.JTable;

/**
 * Abstract interface for unified handling of Library and Scene frames.
 * 
 * @author Gerrit.Gehnen
 * @version $Id$
 */
public interface AbstractLibraryFrame extends PatchBasket {
    /**
     * @return The visual table component for this Frame.
     */
    public JTable getTable();

    /**
     * @return The abstractPatchListModel as unified source of patches in all
     *         types of Libraryframes
     */
    AbstractPatchListModel getAbstractPatchListModel();

    public void ExtractSelectedPatch();

}