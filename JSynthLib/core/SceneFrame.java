package core;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.EventObject;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;

/**
 *
 * @author Gerrit Gehnen
 * @version $Id$
 */

class SceneFrame extends JSLFrame implements AbstractLibraryFrame {
    private static int openFrameCount = 0;
    // column indices
    private static final int SYNTH      = 0;
    private static final int TYPE       = 1;
    private static final int PATCH_NAME = 2;
    private static final int BANK_NUM   = 3;
    private static final int PATCH_NUM  = 4;
    private static final int COMMENT    = 5;

    private SceneListModel myModel;
    private JTable table;
    private JTable table2;
    private JLabel statusBar;
    private File filename;
    private boolean changed = false;  //has the library been altered since it was last saved?
    private SceneTableCellEditor rowEditor ;
    private static PatchTransferHandler pth = new PatchListTransferHandler();

    public SceneFrame(File file) {
        super(file.getName(),
          true,     //resizable
          true,     //closable
          true,     //maximizable
          true);        //iconifiable
        initLibraryFrame();
    }

    public SceneFrame() {
        super("Unsaved Scene #" + (++openFrameCount),
          true,     //resizable
          true,     //closable
          true,     //maximizable
          true);        //iconifiable
        initLibraryFrame();
    }

    protected void initLibraryFrame() {
        //...Create the GUI and put it in the window...
        addJSLFrameListener(new MyFrameListener());

    // create Table
    table = createTable();

        //Create the scroll  pane and add the table to it.
        final JScrollPane scrollPane = new JScrollPane(table);
    // Enable drop on scrollpane
    scrollPane.getViewport()
        .setTransferHandler(new ProxyImportHandler(table, pth));
        scrollPane.getVerticalScrollBar().addMouseListener(new MouseAdapter() {
        public void mousePressed(MouseEvent e) { }

        public void mouseReleased(MouseEvent e) {
            myModel.fireTableDataChanged();
        }
        });

        //Add the scroll pane to this window.
        JPanel statusPanel = new JPanel();
        statusBar = new JLabel(myModel.getRowCount() + " Patches");
        statusPanel.add(statusBar);

        //getContentPane().setLayout(new BorderLayout());
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        getContentPane().add(statusPanel, BorderLayout.SOUTH);

        //...Then set the window size or call pack...
        setSize(600, 300);

        //Set the window's location.
    moveToDefaultLocation();
    }

    private class MyFrameListener implements JSLFrameListener {
    public void JSLFrameClosing(JSLFrameEvent e) {
        if (!changed) return;

        // close Patch/Bank Editor editing a patch in this frame.
        JSLFrame[] jList = JSLDesktop.getAllFrames();
        for (int j = 0; j < jList.length; j++) {
        if (jList[j] instanceof BankEditorFrame) {
            for (int i = 0; i < myModel.getRowCount(); i++)
            if (((BankEditorFrame) (jList[j])).bankData
                == myModel.getPatchAt(i)) {
                jList[j].moveToFront();
                try {
                jList[j].setSelected(true);
                jList[j].setClosed(true);
                } catch (Exception e1) {
                ErrorMsg.reportStatus(e1);
                }
                break;
            }
        }
        if (jList[j] instanceof PatchEditorFrame) {
            for (int i = 0; i < myModel.getRowCount(); i++)
            if (((PatchEditorFrame) (jList[j])).p == myModel.getPatchAt(i)) {
                jList[j].moveToFront();
                try {
                jList[j].setSelected(true);
                jList[j].setClosed(true);
                } catch (Exception e1) {
                ErrorMsg.reportStatus(e1);
                }
                break;
            }
        }
        }

        if (JOptionPane.showConfirmDialog
        (null,
         "This Scene may contain unsaved data.\nSave before closing?",
         "Unsaved Data", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
        return;

        moveToFront();
        Actions.saveFrame();
    }

    public void JSLFrameOpened(JSLFrameEvent e) { }

    public void JSLFrameActivated(JSLFrameEvent e) {
        Actions.setEnabled(true,
                   Actions.EN_GET
                   | Actions.EN_IMPORT
                   | Actions.EN_IMPORT_ALL
                   | Actions.EN_NEW_PATCH);
        // not implemented
        Actions.setEnabled(false,
                   Actions.EN_DELETE_DUPLICATES
                   | Actions.EN_SORT);
        enableActions();
    }

    public void JSLFrameClosed(JSLFrameEvent e) { }

    public void JSLFrameDeactivated(JSLFrameEvent e) {
        Actions.setEnabled(false, Actions.EN_ALL);
    }

    public void JSLFrameDeiconified(JSLFrameEvent e) { }

    public void JSLFrameIconified(JSLFrameEvent e) { }
    }

    private JTable createTable() {
        myModel = new SceneListModel(changed);
        final JTable table = new JTable(myModel);
        table2 = table;     // What's this?

        rowEditor = new SceneTableCellEditor(table);

        table.setPreferredScrollableViewportSize(new Dimension(500, 70));
        table.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    Actions.showMenuPatchPopup(table2, e.getX(), e.getY());
                    table2.setRowSelectionInterval
                        (table2.rowAtPoint(new Point(e.getX(), e.getY())),
                         table2.rowAtPoint(new Point(e.getX(), e.getY())));
                }
            }

            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    Actions.showMenuPatchPopup(table2, e.getX(), e.getY());
                    table2.setRowSelectionInterval
                        (table2.rowAtPoint(new Point(e.getX(), e.getY())),
                         table2.rowAtPoint(new Point(e.getX(), e.getY())));
                }
            }

            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    playSelectedPatch();
                }
            }
        });

    //table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    table.setTransferHandler(pth);
    table.setDragEnabled(true);

        TableColumn column = null;
        column = table.getColumnModel().getColumn(SYNTH);
        column.setPreferredWidth(50);
        column = table.getColumnModel().getColumn(TYPE);
        column.setPreferredWidth(50);
        column = table.getColumnModel().getColumn(PATCH_NAME);
        column.setPreferredWidth(100);
        column = table.getColumnModel().getColumn(BANK_NUM);
        column.setPreferredWidth(50);
        column.setCellEditor(rowEditor); // Set the special pop-up Editor for Bank numbers
        column = table.getColumnModel().getColumn(PATCH_NUM);
        column.setPreferredWidth(50);
        column.setCellEditor(rowEditor); // Set the special pop-up Editorfor Patch Numbers
        column = table.getColumnModel().getColumn(COMMENT);
        column.setPreferredWidth(200);

        table.getModel().addTableModelListener(new TableModelListener() {
        public void tableChanged(TableModelEvent e) {
            statusBar.setText(myModel.getRowCount() + " Patches");
            /*
            int c = ((AbstractPatchListModel) e.getSource()).getRowCount();
            Actions.setEnabled(c > 0,
                       Actions.EN_EXPORT
                       | Actions.EN_SAVE
                       | Actions.EN_SAVE_AS
                       | Actions.EN_SEARCH
                       | Actions.EN_TRANSFER_SCENE);

            Actions.setEnabled(c > 1,
                       Actions.EN_CROSSBREED
                       | Actions.EN_DELETE_DUPLICATES
                       | Actions.EN_SORT);
            */
            enableActions();
        }
        });

        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
        public void valueChanged(ListSelectionEvent e) {
            //ErrorMsg.reportStatus ("ValueChanged"+((ListSelectionModel)e.getSource ()).getMaxSelectionIndex ());
            /*
            int i = ((ListSelectionModel) e.getSource()).getMaxSelectionIndex();
            Actions.setEnabled(i >= 0,
                       Actions.EN_COPY
                       | Actions.EN_CUT
                       | Actions.EN_DELETE
                       | Actions.EN_EXPORT
                       | Actions.EN_EXTRACT
                       | Actions.EN_PLAY
                       | Actions.EN_REASSIGN
                       | Actions.EN_SEND
                       | Actions.EN_SEND_TO
                       | Actions.EN_STORE
                       | Actions.EN_UPLOAD);

            Actions.setEnabled(i >= 0
                       && myModel.getPatchAt(table.getSelectedRow()).getDriver().hasEditor(),
                       Actions.EN_EDIT);
            */
            enableActions();
        }
        });
    return table;
    }

    /** change state of Actions based on the state of the table. */
    private void enableActions() {
    Actions.setEnabled(table.getRowCount() > 0,
               Actions.EN_SAVE
               | Actions.EN_SAVE_AS
               | Actions.EN_SEARCH
               | Actions.EN_TRANSFER_SCENE);

    Actions.setEnabled(table.getRowCount() > 1,
               Actions.EN_CROSSBREED);

    Actions.setEnabled(table.getSelectedRowCount() > 0,
               Actions.EN_COPY
               | Actions.EN_CUT
               | Actions.EN_DELETE
               | Actions.EN_EXPORT
               | Actions.EN_EXTRACT
               | Actions.EN_PLAY
               | Actions.EN_REASSIGN
               | Actions.EN_SEND
               | Actions.EN_SEND_TO
               | Actions.EN_STORE
               | Actions.EN_UPLOAD);

    Actions.setEnabled(table.getSelectedRowCount() > 0
               && myModel.getPatchAt(table.getSelectedRow()).getDriver().hasEditor(),
               Actions.EN_EDIT);
    }

    // begin PatchBasket methods
    public void importPatch(File file) throws IOException,
            FileNotFoundException {
        FileInputStream fileIn = new FileInputStream(file);
        byte[] buffer = new byte[(int) file.length()];
        fileIn.read(buffer);
        fileIn.close();

        //ErrorMsg.reportStatus("Buffer length:" + buffer.length);
        IPatch[] patarray = Patch.valueOf(buffer);
        for (int j = 0; j < patarray.length; j++) {
            if (table.getSelectedRowCount() == 0)
                myModel.addPatch(patarray[j]);
            else
                myModel.setPatchAt(patarray[j], table.getSelectedRow());
        }

        myModel.fireTableDataChanged();
        changed = true;
        //statusBar.setText(myModel.getRowCount() + " Patches");
    }

    public void exportPatch(File file) throws IOException, FileNotFoundException {
        if (table.getSelectedRowCount() == 0) {
            ErrorMsg.reportError("Error", "No Patch Selected.");
            return;
        }
        FileOutputStream fileOut = new FileOutputStream(file);
        fileOut.write(getSelectedPatch().getByteArray());
        fileOut.close();
    }

    public void deleteSelectedPatch() {
        if (table.getSelectedRowCount() == 0) {
            ErrorMsg.reportError("Error", "No Patch Selected.");
            return;
        }
        myModel.removeSceneAt(table.getSelectedRow());
        myModel.fireTableDataChanged();
        //statusBar.setText(myModel.getRowCount() + " Patches");
    }

    public void copySelectedPatch() {
        pth.exportToClipboard(table,
                  Toolkit.getDefaultToolkit().getSystemClipboard(),
                  PatchTransferHandler.COPY);
    }

    public void pastePatch() {
        if (!pth.importData(table, Toolkit.getDefaultToolkit().getSystemClipboard().getContents(this)))
            Actions.setEnabled(false, Actions.EN_PASTE);
    }

    public void pastePatch(IPatch p) {
	pth.importData(table, p);
    }

    public IPatch getSelectedPatch() {
	try {
	    return myModel.getPatchAt(table.getSelectedRow());
	} catch (Exception e) {
	    ErrorMsg.reportStatus(e);
	    return null;
	}
    }

    public void sendSelectedPatch() {
	IPatch myPatch = myModel.getPatchAt(table.getSelectedRow());
	if (myPatch.getDriver() instanceof ISingleDriver) {
	    myPatch.calculateChecksum();
	    myPatch.send();
	}
    }

    public void sendToSelectedPatch() {
	IPatch myPatch = myModel.getPatchAt(table.getSelectedRow());
        myPatch.calculateChecksum();
        new SysexSendToDialog(myPatch);
    }

    public void reassignSelectedPatch() {
	IPatch myPatch = myModel.getPatchAt(table.getSelectedRow());
        myPatch.calculateChecksum();
        new ReassignPatchDialog(myPatch);
        myModel.fireTableDataChanged();
    }

    public void playSelectedPatch() {
	IPatch myPatch = myModel.getPatchAt(table.getSelectedRow());
	if (myPatch.getDriver() instanceof ISingleDriver) {
	    myPatch.calculateChecksum();
	    myPatch.send();
	    myPatch.play();
	}
    }

    public void storeSelectedPatch() {
	IPatch myPatch = myModel.getPatchAt(table.getSelectedRow());
        myPatch.calculateChecksum();
        new SysexStoreDialog(myPatch);
    }

    public JSLFrame editSelectedPatch() {
        if (table.getSelectedRowCount() == 0) {
            ErrorMsg.reportError("Error", "No Patch Selected. EditAction must be disabled.");
            return null;
        }
	IPatch myPatch = myModel.getPatchAt(table.getSelectedRow());
        changed = true;
        return myPatch.edit();
    }

    public ArrayList getPatchCollection() {
        ArrayList ar = new ArrayList();
        for (int i = 0; i < myModel.getRowCount(); i++)
            ar.add(myModel.getPatchAt(i));
        return ar;
    }
    // end of PatchBasket methods

    // begin AbstarctLibraryFrame methods
    public AbstractPatchListModel getAbstractPatchListModel() {
        return myModel;
    }

    public JTable getTable() {  // for SearchDialog
        return table;
    }

    public void ExtractSelectedPatch() {
        if (table.getSelectedRowCount() == 0) {
            ErrorMsg.reportError("Error", "No Patch Selected.");
            return;
        }
        IPatch myPatch = myModel.getPatchAt(table.getSelectedRow());
        IBankDriver myDriver = (IBankDriver) myPatch.getDriver();
        for (int i = 0; i < myDriver.getNumPatches(); i++)
            if (myDriver.getPatch(myPatch, i) != null)
                myModel.addPatch(myDriver.getPatch(myPatch, i));
        myModel.fireTableDataChanged();
        changed = true;
    //statusBar.setText(myModel.getRowCount() + " Patches");
    }
    // end AbstarctLibraryFrame methods

    // for open/save/save-as actions
    public void save() throws Exception {
        PatchEdit.showWaitDialog();
        FileOutputStream f = new FileOutputStream(filename);
        ObjectOutputStream s = new ObjectOutputStream(f);
        s.writeObject(myModel.getSceneList());
        s.flush();
        s.close();
        f.close();
        PatchEdit.hideWaitDialog();
        changed = false;
    }

    public void save(File file) throws Exception {
        filename = file;
        setTitle(file.getName());
        save();
    }

    public void open(File file) throws Exception {
        PatchEdit.showWaitDialog();
        setTitle(file.getName());
        filename = file;
        FileInputStream f = new FileInputStream(file);
        ObjectInputStream s = new ObjectInputStream(f);
        myModel.setSceneList((ArrayList) s.readObject());
        s.close();
        f.close();
        revalidateDrivers();
        PatchEdit.hideWaitDialog();
        statusBar.setText(myModel.getRowCount() + " Patches");
    }

    /**
     * Send all patches of the scene to the
     * configured places in the synth's.
     */
    void sendScene() {
        //     ErrorMsg.reportStatus("Transfering Scene");
        for (int i = 0; i < myModel.getRowCount(); i++) {
            int bankNum   = myModel.getSceneAt(i).getBankNumber();
            int patchNum  = myModel.getSceneAt(i).getPatchNumber();
            IPatch myPatch = myModel.getPatchAt(i);
            myPatch.calculateChecksum();
            myPatch.store(bankNum, patchNum);
        }
    }

    /**
     * Re-assigns drivers to all patches in libraryframe. Called after
     * new drivers are added or or removed
     */
    protected void revalidateDrivers() {
        for (int i = 0; i < myModel.getRowCount(); i++)
            chooseDriver(myModel.getPatchAt(i));
        myModel.fireTableDataChanged();
    }

    private void chooseDriver(IPatch patch) {
        byte[] sysex = patch.getByteArray();
        IPatchDriver driver = (IPatchDriver) Patch.chooseDriver(sysex);
  	patch.setDriver(driver);
        if (driver == null) {
            // Unkown patch, try to guess at least the manufacturer
            patch.setComment("Probably a "
                    + LookupManufacturer.get(sysex[1], sysex[2], sysex[3])
                    + " Patch, Size: " + sysex.length);
        }
    }

    // not used?
    public boolean canImport(DataFlavor[] flavors) {
        return pth.canImport(table, flavors);
    }

    // not used?
    public int getSelectedRowCount() {
        return table.getSelectedRowCount();
    }

    /**
     * Refactored from PerformanceListModel
     * @author  Gerrit Gehnen
     */
    private class SceneListModel extends AbstractTableModel implements AbstractPatchListModel {
        private final String[] columnNames = {
            "Synth",
            "Type",
            "Patch Name",
            "Bank Number",
            "Patch Number",
            "Comment"
        };
        private boolean changed;
        private ArrayList list = new ArrayList();

        public SceneListModel(boolean c) {
            changed = c;
        }

        public int getColumnCount() {
            return columnNames.length;
        }

        public String getColumnName(int col) {
            return columnNames[col];
        }

        public Object getValueAt(int row, int col) {
            Scene myScene = (Scene) list.get(row);
            try {
                switch (col) {
                    case SYNTH:
                        return myScene.getPatch().getDevice().getSynthName();
                    case TYPE:
                        return myScene.getPatch().getDriver().getPatchType();
                    case PATCH_NAME:
                        return myScene.getPatch().getName();
                    case BANK_NUM:
                        return myScene.getPatch().getDriver().getBankNumbers()[myScene.getBankNumber()];
                    case PATCH_NUM:
                        return myScene.getPatch().getDriver().getPatchNumbers()[myScene.getPatchNumber()];
                    case COMMENT:
                        return myScene.getComment();
                    default:
                        ErrorMsg.reportStatus("SceneFrame: internal error.");
                        return null;
                }
            } catch (Exception e) {
                ErrorMsg.reportStatus(e);
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
            try {
                return Class.forName("java.lang.String");
            } catch (Exception e) {
                return null;
            }
        }

        public boolean isCellEditable(int row, int col) {
            return (col > PATCH_NAME);
        }

        public void setValueAt(Object value, int row, int col) {
            //ErrorMsg.reportStatus("SetValue at "+row+"  "+col+" Value:"+value);
            changed = true;
            Scene myScene = getSceneAt(row);
            switch (col) {
                case SYNTH:
                    myScene.getPatch().getDevice().setSynthName((String) value);
                    break;
                case TYPE:
                case PATCH_NAME:
                    // don't allow to change the Patch Type/Name
                    break;
                case BANK_NUM:
                    myScene.setBankNumber(((Integer) value).intValue());
                    break;
                case PATCH_NUM:
                    myScene.setPatchNumber(((Integer) value).intValue());
                    break;
                case COMMENT:
                    myScene.setComment((String) value);
                    break;
            }
            list.set(row, myScene);
        }

        // begin AbstractPatchListModel interface methods
        public void addPatch(IPatch p) {
            list.add(new Scene(p));
            this.fireTableDataChanged();
        }

        public void setPatchAt(IPatch p, int row) {
            list.set(row, new Scene(p));
            fireTableRowsUpdated(row, row);
        }

        public IPatch getPatchAt(int row) {
            return ((Scene) list.get(row)).getPatch();
        }

        public String getCommentAt(int row) {
            return ((Scene) list.get(row)).getComment();
        }

        public int getRowCount() {
            return list.size();
        }
        // end AbstractPatchListModel interface methods

        public void setSceneAt(Scene p, int row) {
            list.set(row, p);
            fireTableRowsUpdated(row, row);
        }

        public Scene getSceneAt(int row) {
            return (Scene) list.get(row);
        }

        // begin
        public void removeSceneAt(int row) {
            this.list.remove(row);
            this.fireTableDataChanged();
        }

        public ArrayList getSceneList() {
            return this.list;
        }

        public void setSceneList(ArrayList newSceneList) {
            this.list = newSceneList;
            this.fireTableDataChanged();
        }

        public void addPatch(IPatch p, int row) {
            list.add(row, new Scene(p));
            this.fireTableDataChanged();
        }
        // end
    }

    /**
     * @author Gerrit Gehnen
     */
    private class SceneTableCellEditor implements TableCellEditor, TableModelListener {
        private TableCellEditor editor, defaultEditor;
        private JComboBox box;
        private JTable table;
        private int oldrow = -1;
        private int oldcol = -1;

        /**
         * Constructs a SceneTableCellEditor.
         * create default editor
         *
         * @see TableCellEditor
         * @see DefaultCellEditor
         */
        public SceneTableCellEditor(JTable table) {
            this.table = table;
            defaultEditor = new DefaultCellEditor(new JTextField());
            this.table.getModel().addTableModelListener(this);
        }

        public Component getTableCellEditorComponent(JTable table,
                                 Object value, boolean isSelected, int row, int column) {
            return editor.getTableCellEditorComponent(table, value, isSelected, row, column);
        }

        public Object getCellEditorValue() {
            //         ErrorMsg.reportStatus("getCellEditorValue "+box.getSelectedItem());
            return new Integer(box.getSelectedIndex());
        }
        public boolean stopCellEditing() {
            return editor.stopCellEditing();
        }
        public void cancelCellEditing() {
            editor.cancelCellEditing();
        }
        public boolean isCellEditable(EventObject anEvent) {
            selectEditor((MouseEvent) anEvent);
            return editor.isCellEditable(anEvent);
        }
        public void addCellEditorListener(CellEditorListener l) {
            editor.addCellEditorListener(l);
        }
        public void removeCellEditorListener(CellEditorListener l) {
            editor.removeCellEditorListener(l);
        }
        public boolean shouldSelectCell(EventObject anEvent) {
            selectEditor((MouseEvent) anEvent);
            return editor.shouldSelectCell(anEvent);
        }

        protected void selectEditor(MouseEvent e) {
            IPatchDriver driver;
            int row, col;

            if (e == null) {
            row = table.getSelectionModel().getAnchorSelectionIndex();
            col = table.getSelectedColumn();
            } else {
            row = table.rowAtPoint(e.getPoint());
            col = table.columnAtPoint(e.getPoint());
            }
            //    ErrorMsg.reportStatus("selectEditor "+ row);
            if ((row != oldrow) || (col != oldcol)) {
            oldrow = row;
            oldcol = col;
            box = new JComboBox();

            driver = ((SceneListModel) table.getModel()).getPatchAt(row).getDriver();
            String[] patchNumbers = driver.getPatchNumbers();
            String[] bankNumbers  = driver.getBankNumbers();
            if (patchNumbers.length > 1) {
                if (col == BANK_NUM) {
                for (int i = 0; i < bankNumbers.length; i++) {
                    box.addItem(bankNumbers[i]);
                }
                } else if (col == PATCH_NUM)
                for (int i = 0; i < patchNumbers.length; i++) {
                    box.addItem(patchNumbers[i]);
                }
            }
            editor = new DefaultCellEditor(box);
            if (editor == null) {
                editor = defaultEditor;
            }
            }
        }

        public void tableChanged(TableModelEvent tableModelEvent) {
            oldcol = -1;
            oldrow = -1;
        }
    }
}
