/*
 * PatchListModel.java
 *
 */

package core;

import javax.swing.table.*;
import java.util.ArrayList;
/**
 *
 * @version $Id$
 */
class PatchListModel extends AbstractTableModel implements AbstractPatchListModel
{
     String[] columnNames =
    {"Synth",
     "Type",
     "Patch Name",
     "Field 1",
     "Field 2",
     "Comment"};
     boolean changed;
     public ArrayList PatchList = new ArrayList ();
     
     
     public PatchListModel (boolean c)
     {super(); changed=c;}
     public int getColumnCount ()
     {
         return columnNames.length;
     }
     
     public int getRowCount ()
     {
         return PatchList.size ();
     }
     
     public String getColumnName (int col)
     {
         return columnNames[col];
     }
     
     public Object getValueAt (int row, int col)
     {
         Patch myPatch=(Patch)PatchList.get (row);
         if (col==0) return ((Device)PatchEdit.appConfig.getDevice (myPatch.deviceNum)).getSynthName ();
         if (col==1) return PatchEdit.getDriver(myPatch.deviceNum,myPatch.driverNum).getPatchType ();
         if (col==2) return PatchEdit.getDriver(myPatch.deviceNum,myPatch.driverNum).getPatchName (myPatch);
         if (col==3) return myPatch.date;
         if (col==4) return myPatch.author;
         return myPatch.comment;
         
     }
     
        /*
         * JTable uses this method to determine the default renderer/
         * editor for each cell.  If we didn't implement this method,
         * then the last column would contain text ("true"/"false"),
         * rather than a check box.
         */
     public Class getColumnClass (int c)
     {
         return getValueAt (0, c).getClass ();
     }
     
        /*
         * Don't need to implement this method unless your table's
         * editable.
         */
     public boolean isCellEditable (int row, int col)
     {
         //Note that the data/cell address is constant,
         //no matter where the cell appears onscreen.
         if (col>1) return true; else return false;
     }
     
        /*
         * Don't need to implement this method unless your table's
         * data can change.
         */
     public void setValueAt (Object value, int row, int col)
     {
         changed=true;
         if (col==2) ((Driver)PatchEdit.getDriver(((Patch)PatchList.get (row)).deviceNum,((Patch)PatchList.get (row)).driverNum)).setPatchName (((Patch)PatchList.get (row)),(String)value);
         if (col==3) ((Patch)PatchList.get (row)).date=(StringBuffer)value;
         if (col==4) ((Patch)PatchList.get (row)).author=(StringBuffer)value;
         if (col==5) ((Patch)PatchList.get (row)).comment=(StringBuffer)value;
         fireTableCellUpdated (row, col);
         
     }
     
     public Patch getPatchAt (int row)
     {
         return (Patch)PatchList.get (row);
     }
     
     public void setPatchAt (Patch p,int row)
     {
         PatchList.set (row,p);
         fireTableRowsUpdated (row, row);
     }
     
     public void addPatch(Patch p)
     {
         PatchList.add(p);
       //  fireTableRowsUpdated(PatchList.size(),PatchList.size());
         this.fireTableDataChanged();
     }
     
     public StringBuffer getCommentAt(int row) {
         return ((Patch)PatchList.get(row)).comment;
     }     
     
}
