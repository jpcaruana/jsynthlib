/*
 * DNDViewport.java
 *
 * Drop-sensitive Enhancement of JViewport.
 * Enables dropping of Patches into the empty field below an Librarytable.
 *
 */

package core;

import javax.swing.JViewport;
import java.io.IOException;
import java.awt.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;
import javax.swing.JTable;

/**
 *
 * @author  Gerrit.Gehnen <Gerrit.Gehnen@gmx.de>
 * @version $Id$
 */

public class DNDViewport extends JViewport implements DNDComponentInterface, DropTargetListener {
    /**
     * enables this component to be a dropTarget
     */
    DropTarget dropTarget = null;

    public DNDViewport() {
        dropTarget = new DropTarget(this, this);
    }

    /**
     * is invoked when you are dragging over the DropSite
     *
     *
     * /**
     * @param event  */
    public void dragEnter(DropTargetDragEvent event) {
        event.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
    }

    /** is invoked when you are exit the DropSite without dropping
     * @param event  */

    public void dragExit(DropTargetEvent event) {
        //      System.out.println( "DNDViewPort:DropTargetEvent:dragExit");
    }

    /** is invoked when a drag operation is going on
     * @param event  */

    public void dragOver(DropTargetDragEvent event) {
        event.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
    }

    /** a drop has occurred
     * @param event  */
    public void drop(DropTargetDropEvent event) {
        //    System.out.println("DNDViewPort drop");

        try {
            // Create dummy Dataflavo for getting the Patch Data.
            DataFlavor df=new DataFlavor(PatchEdit.getDriver(1,1).getClass(),"Dummy");

            Patch so=(Patch)(event.getTransferable().getTransferData(df));
            so.chooseDriver(); // Because the driverNum is transient in Patch
            //System.out.println(so.getType()+" "+so.getName());
            event.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
            Point p=event.getLocation();

            ((AbstractPatchListModel)((JTable)this.getView()).getModel()).addPatch(so);

            event.getDropTargetContext().dropComplete(true);
            event.dropComplete(true);
        }
        catch (IOException exception) {
            ErrorMsg.reportError("Error", "IO Exception",exception);
            //        exception.printStackTrace ();
            //      System.err.println ( "Exception" + exception.getMessage ());
            event.rejectDrop();
        }
        catch (UnsupportedFlavorException ufException ) {
            ErrorMsg.reportError("Error", "Unsupported Dnd Flavor",ufException);
            // ufException.printStackTrace ();
            // System.err.println ( "Exception" + ufException.getMessage ());
            event.rejectDrop();
        }
    }

    /** is invoked if the use modifies the current drop gesture
     * @param event  */
    public void dropActionChanged( DropTargetDragEvent event ) {
    }

    /**
     * adds elements to itself
     *
     */
    public void addElement( Object s ) {
        //System.out.println ("AddElement "+s.toString ());
        //        (( AbstractTableModel )getModel()).addElement (s);
    }

    /**
     * removes an element from itself
     */
    public void removeElement() {
        //System.out.println ("RemoveElement ");
        //    (( AbstractTableModel)getModel()).removeElement( getSelectedRow());
    }

}

