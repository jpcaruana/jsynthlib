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
import javax.swing.JFormattedTextField;
import javax.swing.text.MaskFormatter;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.TransferHandler;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.SwingUtilities;

/**
 * Abstract class for unified handling of Library and Scene frames.
 * 
 * @author Gerrit.Gehnen
 * @version $Id$
 */
abstract class AbstractLibraryFrame extends Actions.MenuFrame implements PatchBasket {
    protected JTable table;
    protected PatchTableModel myModel;

    private final String TYPE;
    private PatchTransferHandler pth;
    /** Has the library been altered since it was last saved? */
    protected boolean changed = false; // wirski@op.pl
    private JLabel statusBar;
    private File filename;

    AbstractLibraryFrame(String title, String type, PatchTransferHandler pth) {
        super(PatchEdit.getDesktop(), title);
        TYPE = type;
        this.pth = pth;

        //...Create the GUI and put it in the window...
        addJSLFrameListener(new MyFrameListener());

        // create Table
        myModel = createTableModel();
        createTable();

        //Create the scroll pane and add the table to it.
        final JScrollPane scrollPane = new JScrollPane(table);
        // Enable drop on scrollpane
        scrollPane.getViewport().setTransferHandler(
                new ProxyImportHandler(table, pth));
// commented out by Hiroo
//        scrollPane.getVerticalScrollBar().addMouseListener(new MouseAdapter() {
//            public void mousePressed(MouseEvent e) {
//            }
//
//            public void mouseReleased(MouseEvent e) {
//                //myModel.fireTableDataChanged();
//            }
//        });

        //Add the scroll pane to this window.
        JPanel statusPanel = new JPanel();
        statusBar = new JLabel(myModel.getRowCount() + " Patches");
        statusPanel.add(statusBar);

        //getContentPane().setLayout(new BorderLayout());
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        getContentPane().add(statusPanel, BorderLayout.SOUTH);

        //...Then set the window size or call pack...
        setSize(800, 300); // wirski@op.pl
    }

    abstract PatchTableModel createTableModel();
    /** Before calling this method, table and myModel is setup. */
    abstract void setupColumns();
    abstract void frameActivated();
    abstract void enableActions();

    private void createTable() {
        table = new JTable(myModel);

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

        setupColumns();

        table.getModel().addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
                changed=true;
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
    }

    private class MyFrameListener implements JSLFrameListener {
        public void JSLFrameClosing(JSLFrameEvent e) {
            if (!changed)
                return;

            // close Patch/Bank Editor editing a patch in this frame.
            JSLFrame[] jList = PatchEdit.getDesktop().getAllFrames();
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

            if (JOptionPane.showConfirmDialog(null, "This " + TYPE
                    + " may contain unsaved data.\nSave before closing?",
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

        changed();
    }

    protected void changed() {
        myModel.fireTableDataChanged();
        // This is done in tableChanged for the TableModelListener
        // changed = true;
    }

    public boolean isChanged() {
        return(changed);
    }

    public void exportPatch(File file) throws IOException,
            FileNotFoundException {
        if (table.getSelectedRowCount() == 0) {
            ErrorMsg.reportError("Error", "No Patch Selected.");
            return;
        }
        FileOutputStream fileOut = new FileOutputStream(file);
        fileOut.write(getSelectedPatch().export());
        fileOut.close();
    }

    public void deleteSelectedPatch() {
        ErrorMsg.reportStatus("delete patch : " + table.getSelectedRowCount());
        int[] ia = table.getSelectedRows();
        // Without this we cannot delete the patch at the bottom.
        table.clearSelection();
        // delete from bottom not to change indices to be removed
        for (int i = ia.length; i > 0; i--) {
            ErrorMsg.reportStatus("i = " + ia[i - 1]);
            myModel.removeAt(ia[i - 1]);
        }
        changed();
    }

    public void copySelectedPatch() {
        pth.exportToClipboard(table, Toolkit.getDefaultToolkit()
                .getSystemClipboard(), TransferHandler.COPY);
    }

    public void pastePatch() {
        if (pth.importData(table, Toolkit.getDefaultToolkit()
                .getSystemClipboard().getContents(this))) {
            changed();
        } else {
            Actions.setEnabled(false, Actions.EN_PASTE);
        }
    }

    public void pastePatch(IPatch p) {
        myModel.addPatch(p);
        changed();
    }

    public void pastePatch(IPatch p, int bankNum, int patchNum) {// added by R. Wirski
        myModel.addPatch(p, bankNum, patchNum);
        changed();
    }

    public IPatch getSelectedPatch() {
        return myModel.getPatchAt(table.getSelectedRow());
    }

    public void sendSelectedPatch() {
        ((ISinglePatch) getSelectedPatch()).send();
    }

    public void sendToSelectedPatch() {
        new SysexSendToDialog(getSelectedPatch());
    }

    public void reassignSelectedPatch() {
        new ReassignPatchDialog(getSelectedPatch());
        changed();
    }

    public void playSelectedPatch() {
        ISinglePatch myPatch = (ISinglePatch) getSelectedPatch();
        myPatch.send();
        myPatch.play();
    }

    public void storeSelectedPatch() {
        new SysexStoreDialog(getSelectedPatch(), 0, 0); // wirski@op.pl
    }

    public JSLFrame editSelectedPatch() {
        // TODO: "changed" should only be set to true if the patch was modified.
        changed = true;
        return getSelectedPatch().edit();
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
        IBankPatch myPatch = (IBankPatch) getSelectedPatch();
        for (int i = 0; i < myPatch.getNumPatches(); i++){
            ISinglePatch p = myPatch.get(i);
            if (p != null)
                myModel.addPatch(p);
        }
        changed();
    }

    // for open/save/save-as actions
    void save() throws IOException {
        PatchEdit.showWaitDialog("Saving " + filename + "...");
        try {
            FileOutputStream f = new FileOutputStream(filename);
            ObjectOutputStream s = new ObjectOutputStream(f);
            s.writeObject(myModel.getList());
            s.flush();
            s.close();
            f.close();
            changed = false;

            XMLFileUtils.writePatchBasket(this, filename + ".xml");


        } catch (IOException e) {
            throw e;
        } finally {
            PatchEdit.hideWaitDialog();
        }
    }

    void save(File file) throws IOException {
        filename = file;
        setTitle(file.getName());
        save();
        changed = false;
    }

    void open(File file) throws IOException, ClassNotFoundException {
        boolean readXMLFile = true;
        boolean readOldFile = true;

        setTitle(file.getName());
        filename = file;

        if(readOldFile) {
            FileInputStream f = new FileInputStream(file);
            ObjectInputStream s = new ObjectInputStream(f);
            myModel.setList((ArrayList) s.readObject());
            s.close();
            f.close();
            if (myModel.getList().size() > 0)
              // Don't attempt XML if readObject succeeded.
              readXMLFile = false;
        }
        if(readXMLFile) {
            XMLFileUtils.readPatchBasket(this,file.getName() + ".xml");
        }
        revalidateDrivers();
        myModel.fireTableDataChanged();
        changed = false;

    }

    abstract FileFilter getFileFilter();

    abstract String getFileExtension();

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
        if (patch.hasNullDriver()) {
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
         * Add a patch to the end of the internal list.
         * and sets bank and patch numbers
         * @param p The patch to add
         */
        abstract void addPatch(IPatch p, int bankNum, int patchNum);// added by R. Wirski

        /**
         * Set (and replace) the patch at the specified row of the list.
         * @param p The patch to set
         * @param row The row of the table.
         * @param bankNum patch bank number
         * @param patchNum patch number
         */
        abstract void setPatchAt(IPatch p,int row, int bankNum, int patchNum); // added by R. Wirski

        /**
         * Set (and replace) the patch at the specified row of the list.
         * @param p The patch to set
         * @param row The row of the table.
         */
        abstract void setPatchAt(IPatch p,int row);

        /**
         * Get the patch at the specified row.
         * @param row The row specified
         * @return The patch
         */
        abstract IPatch getPatchAt(int row);

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