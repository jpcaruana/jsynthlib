/*
 * DNDLibraryTable.java
 *
 */

package core;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableCellRenderer;
import java.io.IOException;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.awt.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
//import javax.swing.DefaultListModel;

/**
 *
 * @author  Gerrit.Gehnen <Gerrit.Gehnen@gmx.de>
 * @version $Id$
 */

public class DNDLibraryTable extends JTable implements DNDComponentInterface, DropTargetListener, DragSourceListener, DragGestureListener {
    /**
     * enables this component to be a dropTarget
     */

    DropTarget dropTarget = null;
    AbstractTableModel plModel;

    /**
     * enables this component to be a Drag Source
     */
    DragSource dragSource = null;

    public DNDLibraryTable() {
        dropTarget = new DropTarget(this, this);
        dragSource = new DragSource();
        dragSource.createDefaultDragGestureRecognizer( this, DnDConstants.ACTION_COPY_OR_MOVE, this);
    }

    /**
     * @param dm  */
    public DNDLibraryTable(AbstractTableModel dm) {
        this();
        plModel=dm;
        setModel(dm);
    }

    /** Creates new DNDLibraryTable */

    public DNDLibraryTable(Patch soundType) {
        this();
        setSoundType(soundType);
    }

    /**
     * is invoked when you are dragging over the DropSite
     *
     */
    public void setSoundType(Patch soundType) {
        // this.soundType=soundType;
    }

    /**
     * @param event  */
    public void dragEnter(DropTargetDragEvent event) {
        event.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
    }

    /**
     * is invoked when you are exit the DropSite without dropping
     *
     */

    public void dragExit(DropTargetEvent event) {
   //     System.out.println( "DNDLibraryTable:DropTargetEvent:dragExit");
    }

    /**
     * is invoked when a drag operation is going on
     *
     */

    public void dragOver(DropTargetDragEvent event) {
        event.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
    }

    /**
     * a drop has occurred
     *
     */
    public void drop(DropTargetDropEvent event) {
   //     System.out.println("DNDLibraryTable drop");

        try {
//            Transferable transferable = event.getTransferable();
            Patch pat=(Patch)((AbstractPatchListModel)getModel()).getPatchAt(this.rowAtPoint(event.getLocation()));


            DataFlavor df=new DataFlavor(pat.getDriver().getClass(),pat.toString());
//            if (transferable.isDataFlavorSupported(df)) {
                System.out.println ("IsSupported");
                Patch so=(Patch)event.getTransferable().getTransferData(df);
                so.chooseDriver(); // Because the driverNum is transient in Patch
                //System.out.println(so.getType()+" "+so.getName());
                event.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                Point p=event.getLocation();
                //System.out.println ("Zieltabelle Zeile: "+this.rowAtPoint (p));
//                ((PatchListModel)getModel()).setPatchAt(so,this.rowAtPoint(p));
                ((AbstractPatchListModel)getModel()).addPatch(so);
                /*            String s = (String)transferable.getTransferData ( DataFlavor.stringFlavor);
            addElement( s );
 */
                event.getDropTargetContext().dropComplete(true);
                event.dropComplete(true);
//            }
//            else {
                //System.out.println (transferable.toString ()+" "+df.getMimeType ());
                //System.out.println ("DNDLibraryTable:rejectDrop");
//                event.rejectDrop();
//            }
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

    /**
     * is invoked if the use modifies the current drop gesture
     *
     */
    public void dropActionChanged( DropTargetDragEvent event ) {
    }

    /**
     * a drag gesture has been initiated
     *
     */
    public void dragGestureRecognized( DragGestureEvent event) {
   //     System.out.println("DNDLibraryTable dragGestureRecognized");
       Iterator iter;
        Object obj=new Object();
        if (((MouseEvent)(event.getTriggerEvent())).isPopupTrigger()) return;
        // Workaround for Java-Bug ID 4337114
        // Dnd doesn't work with JInternalFrame
        iter=event.iterator();
        while (iter.hasNext()) {
           obj=iter.next();
           System.out.println(obj.toString());
        }

        if (((MouseEvent)obj).getID()==MouseEvent.MOUSE_EXITED) return;
        int selR = getSelectedRow();
        int selC = getSelectedColumn();

        if ( selR != -1 ) {
            Patch sing =((AbstractPatchListModel) getModel()).getPatchAt(selR);
            // as the name suggests, starts the dragging
            dragSource.startDrag(event, DragSource.DefaultCopyDrop, sing, this);
        } else {
            //System.out.println ( "nothing was selected");
        }
    }

    /**
     * this message goes to DragSourceListener, informing it that the dragging
     * has ended
     *
     */
    public void dragDropEnd(DragSourceDropEvent event) {
   //     System.out.println( "DNDLibraryTable: dragDropEnd");

        if ( event.getDropSuccess()) {
            removeElement();
        }
    }

    /**
     * this message goes to DragSourceListener, informing it that the dragging
     * has entered the DropSite
     *
     */
    public void dragEnter(DragSourceDragEvent event) {
  //      System.out.println( "DNDLibraryTable: dragEnter");
    }

    /**
     * this message goes to DragSourceListener, informing it that the dragging
     * has exited the DropSite
     *
     */
    public void dragExit(DragSourceEvent event) {
   //     System.out.println( "DNDLibTable: DragSorceEvent:dragExit");
    }

    /**
     * this message goes to DragSourceListener, informing it that the dragging is currently
     * ocurring over the DropSite
     *
     */
    public void dragOver(DragSourceDragEvent event) {
    //        System.out.println( "DragSourceEvent:dragOver");
    }

    /**
     * is invoked when the user changes the dropAction
     *
     */

    public void dropActionChanged( DragSourceDragEvent event) {
   //     System.out.println( "dropActionChanged");
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

    public Component findComponentAt(int x,int y)
    {
        Component c;
        c=super.findComponentAt(x,y);
        System.out.println("Find Component At "+x+" "+y+" "+c.toString());
        return c;
    }

    public void setSize(int width, int height)
    {
        super.setSize(width, height);

        System.out.println("Set Size called "+width+" "+ height);
    }
}

