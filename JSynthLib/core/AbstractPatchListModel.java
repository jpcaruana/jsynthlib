/*
 * AbstractPatchListModel.java
 *
 */

package core;

/** This is the general interface to unify the handling of
 * the LibraryTable and SceneTable.
 * @author  Gerrit
 * @version $Id$
 */
public interface AbstractPatchListModel {
 
    /** Add a patch to the end of the internal list.
     * @param p The patch to add
     */    
       public void addPatch(Patch p);
       /** Set (and replace) the patch at the specified row of the list.
        * @param p The patch to set
        * @param row The row of the table.
        */       
       public void setPatchAt (Patch p,int row);
       /** Get the patch at the specified row.
        * @param row The row specified
        * @return The patch
        */       
       public Patch getPatchAt (int row);
          
       public StringBuffer getCommentAt(int row);
       public int getRowCount();   
}
