package core;

import java.awt.datatransfer.Transferable;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;

/**
 * @version $Id$
 */
class LibraryFrame extends AbstractLibraryFrame {
    private static int openFrameCount = 0;
    // column indices
    private static final int SYNTH      = 0;
    private static final int TYPE       = 1;
    private static final int PATCH_NAME = 2;
    private static final int FIELD1     = 3;
    private static final int FIELD2     = 4;
    private static final int COMMENT    = 5;

    static {
        pth = new PatchListTransferHandler();
        UNSAVED_MSG = "This Library may contain unsaved data.\nSave before closing?";
    }
    LibraryFrame(File file) {
        super(file.getName(),
                true, //resizable
                true, //closable
                true, //maximizable
                true); //iconifiable
    }

    LibraryFrame() {
        super("Unsaved Library #" + (++openFrameCount),
                true, //resizable
                true, //closable
                true, //maximizable
                true); //iconifiable
    }

    PatchTableModel createTableModel() {
        return new PatchListModel(false);
    }

    void setupColumns(final JTable table) {
        TableColumn column = null;
        column = table.getColumnModel().getColumn(SYNTH);
        column.setPreferredWidth(50);
        column = table.getColumnModel().getColumn(TYPE);
        column.setPreferredWidth(50);
        column = table.getColumnModel().getColumn(PATCH_NAME);
        column.setPreferredWidth(100);
        column = table.getColumnModel().getColumn(FIELD1);
        column.setPreferredWidth(50);
        column = table.getColumnModel().getColumn(FIELD2);
        column.setPreferredWidth(50);
        column = table.getColumnModel().getColumn(COMMENT);
        column.setPreferredWidth(200);
    }

    void frameActivated() {
        Actions.setEnabled(true, Actions.EN_GET | Actions.EN_IMPORT
                | Actions.EN_IMPORT_ALL | Actions.EN_NEW_PATCH);
        enableActions();
    }

    /** change state of Actions based on the state of the table. */
    void enableActions() {
        Actions.setEnabled(table.getRowCount() > 0, Actions.EN_SAVE
                | Actions.EN_SAVE_AS | Actions.EN_SEARCH);
        // 			   | Actions.EN_TRANSFER_SCENE);

        Actions.setEnabled(table.getRowCount() > 1, Actions.EN_CROSSBREED
                | Actions.EN_DELETE_DUPLICATES | Actions.EN_SORT);

        Actions.setEnabled(table.getSelectedRowCount() > 0, Actions.EN_COPY
                | Actions.EN_CUT | Actions.EN_DELETE | Actions.EN_EXPORT
                | Actions.EN_EXTRACT | Actions.EN_PLAY | Actions.EN_REASSIGN
                | Actions.EN_SEND | Actions.EN_SEND_TO | Actions.EN_STORE
                | Actions.EN_UPLOAD);

        Actions.setEnabled(table.getSelectedRowCount() > 0
                && myModel.getPatchAt(table.getSelectedRow()).getDriver()
                        .hasEditor(), Actions.EN_EDIT);
    }

    void deleteDuplicates() {
        Collections.sort(myModel.getList(), new SysexSort());
        int numDeleted = 0;
        IPatch p, q;
        Iterator it = myModel.getList().iterator();
        p = (IPatch) it.next();
        while (it.hasNext()) {
            q = (IPatch) it.next();
            if (Arrays.equals(p.getByteArray(), q.getByteArray())) {
                it.remove();
                numDeleted++;
            } else
                p = q;
        }
        JOptionPane.showMessageDialog(null, numDeleted
                + " Patches were Deleted", "Delete Duplicates",
                JOptionPane.INFORMATION_MESSAGE);
        myModel.fireTableDataChanged();
        statusBar.setText(myModel.getRowCount() + " Patches");
    }

    //This is a comparator class used by the delete duplicated action
    //to sort based on the sysex data
    //Sorting this way makes the Dups search much easier, since the
    //dups must be next to each other
    private static class SysexSort implements Comparator {
        public int compare(Object a1, Object a2) {
            String s1 = new String(((IPatch) (a1)).getByteArray());
            String s2 = new String(((IPatch) (a2)).getByteArray());
            return s1.compareTo(s2);
        }
    }

    // for SortDialog
    void sortPatch(Comparator c) {
        Collections.sort(myModel.getList(), c);
        myModel.fireTableDataChanged();
    }

    /**
     * Refactored from PerformanceListModel
     * 
     * @author Gerrit Gehnen
     */
    private class PatchListModel extends PatchTableModel {
        private final String[] columnNames = { "Synth", "Type", "Patch Name",
                "Field 1", "Field 2", "Comment" };

        private boolean changed;

        private ArrayList list = new ArrayList();

        PatchListModel(boolean c) {
            changed = c;
        }

        public int getRowCount() {
            return list.size();
        }

        public int getColumnCount() {
            return columnNames.length;
        }

        public String getColumnName(int col) {
            return columnNames[col];
        }

        public Object getValueAt(int row, int col) {
            IPatch myPatch = (IPatch) list.get(row);
            try {
                switch (col) {
                case SYNTH:
                    return myPatch.getDevice().getSynthName();
                case TYPE:
                    return myPatch.getDriver().getPatchType();
                case PATCH_NAME:
                    return myPatch.getName();
                case FIELD1:
                    return myPatch.getDate();
                case FIELD2:
                    return myPatch.getAuthor();
                case COMMENT:
                    return myPatch.getComment();
                default:
                    ErrorMsg.reportStatus("LibraryFrame.getValueAt: internal error.");
                    return null;
                }
            } catch (NullPointerException e) {
                ErrorMsg.reportStatus("LibraryFrame.getValueAt: row=" + row + ", col=" + col + ", Patch=" + (Patch) myPatch);
                ErrorMsg.reportStatus("row count =" + getRowCount());
                //e.printStackTrace();
                return null;
            }
        }

        /*
         * JTable uses this method to determine the default renderer/
         * editor for each cell.  If we didn't implement this method,
         * then the last column would contain text ("true"/"false"),
         * rather than a check box.
         */
        public Class getColumnClass(int c) {
            return String.class;
        }

        /*
         * Don't need to implement this method unless your table's
         * editable.
         */
        public boolean isCellEditable(int row, int col) {
            return (col > TYPE);
        }

        /*
         * Don't need to implement this method unless your table's
         * data can change.
         */
        public void setValueAt(Object value, int row, int col) {
            changed = true;
            IPatch myPatch = (IPatch) list.get(row);
            switch (col) {
            case PATCH_NAME:
                myPatch.setName((String) value);
                break;
            case FIELD1:
                myPatch.setDate((String) value);
                break;
            case FIELD2:
                myPatch.setAuthor((String) value);
                break;
            case COMMENT:
                myPatch.setComment((String) value);
                break;
            default:
                ErrorMsg.reportStatus("LibraryFrame.setValueAt: internal error.");
            }
            fireTableCellUpdated(row, col);
        }

        // begin PatchTableModel interface methods
        void addPatch(IPatch p) {
            ErrorMsg.reportStatus("LibraryFrame.addPatch: Patch=" + (Patch) p);
            list.add(p);
            //  fireTableRowsUpdated(getRowCount(),getRowCount());
            this.fireTableDataChanged();
        }

        void setPatchAt(IPatch p, int row) {
            ErrorMsg.reportStatus("LibraryFrame.setPatchAt: row=" + row + ", Patch=" + (Patch) p);
            list.set(row, p);
            fireTableRowsUpdated(row, row);
        }

        IPatch getPatchAt(int row) {
            return (IPatch) list.get(row);
        }

        String getCommentAt(int row) {
            return getPatchAt(row).getComment();
        }

        void removeAt(int row) {
            this.list.remove(row);
            this.fireTableDataChanged();
        }

        ArrayList getList() {
            return this.list;
        }

        void setList(ArrayList newList) {
            this.list = newList;
            this.fireTableDataChanged();
        }
        // end PatchTableModel interface methods
    }

    private static class PatchListTransferHandler extends PatchTransferHandler {
        protected Transferable createTransferable(JComponent c) {
            PatchTableModel pm = (PatchTableModel) ((JTable) c).getModel();
            IPatch p = pm.getPatchAt(((JTable) c).getSelectedRow());
            ErrorMsg.reportStatus("PatchListTransferHandler.createTransferable " + p);
            return (Transferable) p;
        }

        protected boolean storePatch(IPatch p, JComponent c) {
            ((PatchTableModel) ((JTable) c).getModel()).addPatch(p);
            return true;
        }

        // only for debugging
        protected void exportDone(JComponent source, Transferable data, int action) {
            ErrorMsg.reportStatus("PatchListTransferHandler.exportDone " + data);
        }
    }
}
