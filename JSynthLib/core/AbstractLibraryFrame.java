/*
 * AbstractLibraryFrame.java
 *
 * Created on 24. September 2002, 10:52
 */
package core;

import java.awt.BorderLayout;
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

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.TransferHandler;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

/**
 * Abstract interface for unified handling of Library and Scene frames.
 * 
 * @author Gerrit.Gehnen
 * @version $Id$
 */
abstract class AbstractLibraryFrame extends JSLFrame implements PatchBasket {
    protected PatchTableModel myModel;
    protected JTable table;
    protected JLabel statusBar;
    protected boolean changed = false;  //has the library been altered since it was last saved?

    protected static String UNSAVED_MSG;
    protected static PatchTransferHandler pth;

    private File filename;

    AbstractLibraryFrame(String s, boolean resizable, boolean closable,
            boolean maximizable, boolean iconifiable) {
        super(s, resizable, closable, maximizable, iconifiable);

        //...Create the GUI and put it in the window...
        addJSLFrameListener(new MyFrameListener());

        // create Table
        myModel = createTableModel();
        table = createTable();

        //Create the scroll pane and add the table to it.
        final JScrollPane scrollPane = new JScrollPane(table);
        // Enable drop on scrollpane
        scrollPane.getViewport().setTransferHandler(
                new ProxyImportHandler(table, pth));
        scrollPane.getVerticalScrollBar().addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
            }

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

    abstract PatchTableModel createTableModel();
    abstract void setupColumns(final JTable table);
    abstract void frameActivated();
    abstract void enableActions();

    private JTable createTable() {
        final JTable table = new JTable(myModel);

        table.setPreferredScrollableViewportSize(new Dimension(500, 70));
        table.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    Actions.showMenuPatchPopup(table, e.getX(), e.getY());
                    table.setRowSelectionInterval(
                            table.rowAtPoint(new Point(e.getX(), e.getY())),
                            table.rowAtPoint(new Point(e.getX(), e.getY())));
                }
            }

            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    Actions.showMenuPatchPopup(table, e.getX(), e.getY());
                    table.setRowSelectionInterval(
                            table.rowAtPoint(new Point(e.getX(), e.getY())),
                            table.rowAtPoint(new Point(e.getX(), e.getY())));
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

        setupColumns(table);

        table.getModel().addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
                statusBar.setText(myModel.getRowCount() + " Patches");
                enableActions();
            }
        });

        table.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
                    public void valueChanged(ListSelectionEvent e) {
                        enableActions();
                    }
                });
        return table;
    }

    private class MyFrameListener implements JSLFrameListener {
        public void JSLFrameClosing(JSLFrameEvent e) {
            if (!changed)
                return;

            // close Patch/Bank Editor editing a patch in this frame.
            JSLFrame[] jList = JSLDesktop.getAllFrames();
            for (int j = 0; j < jList.length; j++) {
                if (jList[j] instanceof BankEditorFrame) {
                    for (int i = 0; i < myModel.getRowCount(); i++)
                        if (((BankEditorFrame) (jList[j])).bankData == myModel
                                .getPatchAt(i)) {
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
                        if (((PatchEditorFrame) (jList[j])).p == myModel
                                .getPatchAt(i)) {
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

            if (JOptionPane.showConfirmDialog(null, UNSAVED_MSG,
                    "Unsaved Data", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
                return;

            moveToFront();
            Actions.saveFrame();
        }

        public void JSLFrameOpened(JSLFrameEvent e) {
        }

        public void JSLFrameActivated(JSLFrameEvent e) {
            frameActivated();
        }

        public void JSLFrameClosed(JSLFrameEvent e) {
        }

        public void JSLFrameDeactivated(JSLFrameEvent e) {
            Actions.setEnabled(false, Actions.EN_ALL);
        }

        public void JSLFrameDeiconified(JSLFrameEvent e) {
        }

        public void JSLFrameIconified(JSLFrameEvent e) {
        }
    }

    // begin PatchBasket methods
    public void importPatch(File file) throws IOException,
            FileNotFoundException {
        if (ImportMidiFile.doImport(file)) {
            return;
        }
        FileInputStream fileIn = new FileInputStream(file);
        byte[] buffer = new byte[(int) file.length()];
        fileIn.read(buffer);
        fileIn.close();

        //ErrorMsg.reportStatus("Buffer length:" + buffer.length);
        IPatch[] patarray = DriverUtil.createPatches(buffer);
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

    public void exportPatch(File file) throws IOException,
            FileNotFoundException {
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
        myModel.removeAt(table.getSelectedRow());
        myModel.fireTableDataChanged();
        //statusBar.setText(myModel.getRowCount() + " Patches");
    }

    public void copySelectedPatch() {
        pth.exportToClipboard(table, Toolkit.getDefaultToolkit()
                .getSystemClipboard(), TransferHandler.COPY);
    }

    public void pastePatch() {
        if (!pth.importData(table, Toolkit.getDefaultToolkit()
                .getSystemClipboard().getContents(this)))
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
        if (myPatch.getDriver().isSingleDriver()) {
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
        if (myPatch.getDriver().isSingleDriver()) {
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
            ErrorMsg.reportError("Error",
                    "No Patch Selected. EditAction must be disabled.");
            return null;
        }
        IPatch myPatch = myModel.getPatchAt(table.getSelectedRow());
        changed = true;
        return myPatch.edit();
    }

    public ArrayList getPatchCollection() {
        return myModel.getList();
    }
    // end of PatchBasket methods

    /**
     * @return The abstractPatchListModel as unified source of patches in all
     *         types of Libraryframes
     */
    PatchTableModel getPatchTableModel() {
        return myModel;
    }

    /**
     * @return The visual table component for this Frame.
     */
    JTable getTable() { // for SearchDialog
        return table;
    }

    void extractSelectedPatch() {
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

    // for open/save/save-as actions
    void save() throws IOException {
        PatchEdit.showWaitDialog();
        FileOutputStream f = new FileOutputStream(filename);
        ObjectOutputStream s = new ObjectOutputStream(f);
        s.writeObject(myModel.getList());
        s.flush();
        s.close();
        f.close();
        PatchEdit.hideWaitDialog();
        changed = false;
    }

    void save(File file) throws IOException {
        filename = file;
        setTitle(file.getName());
        save();
    }

    void open(File file) throws IOException, ClassNotFoundException {
        PatchEdit.showWaitDialog();
        setTitle(file.getName());
        filename = file;
        FileInputStream f = new FileInputStream(file);
        ObjectInputStream s = new ObjectInputStream(f);
        myModel.setList((ArrayList) s.readObject());
        s.close();
        f.close();
        revalidateDrivers();
        PatchEdit.hideWaitDialog();
        statusBar.setText(myModel.getRowCount() + " Patches");
    }

    /**
     * Re-assigns drivers to all patches in libraryframe. Called after new
     * drivers are added or or removed
     */
    protected void revalidateDrivers() {
        for (int i = 0; i < myModel.getRowCount(); i++)
            chooseDriver(myModel.getPatchAt(i));
        myModel.fireTableDataChanged();
    }

    private void chooseDriver(IPatch patch) {
        patch.setDriver();
        if (patch.getDriver().isNullDriver()) {
            // Unkown patch, try to guess at least the manufacturer
            patch.setComment("Probably a "
                    + patch.lookupManufacturer()
                    + " Patch, Size: " + patch.getSize());
        }
    }

    // JSLFrame method
    public boolean canImport(DataFlavor[] flavors) {
        return pth.canImport(table, flavors);
    }

    int getSelectedRowCount() {    // not used now
        return table.getSelectedRowCount();
    }

    /**
     * This is the general interface to unify the handling of
     * the LibraryTable and SceneTable.
     * @author  Gerrit
     */
    abstract class PatchTableModel extends AbstractTableModel {
        /**
         * Add a patch to the end of the internal list.
         * @param p The patch to add
         */
        abstract void addPatch(IPatch p);
        /**
         * Set (and replace) the patch at the specified row of the list.
         * @param p The patch to set
         * @param row The row of the table.
         */
        abstract void setPatchAt (IPatch p,int row);
        /**
         * Get the patch at the specified row.
         * @param row The row specified
         * @return The patch
         */
        abstract IPatch getPatchAt (int row);

        /**
         * Get the comment at the specified row.
         * @param row The row specified
         * @return The comment.
         */
        abstract String getCommentAt(int row);
        abstract void removeAt(int row);
        abstract ArrayList getList();
        abstract void setList(ArrayList newList);
    }

}