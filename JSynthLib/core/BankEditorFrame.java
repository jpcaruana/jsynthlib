/* $Id$ */
package core;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

public class BankEditorFrame extends JSLFrame implements PatchBasket {
    /** This is the patch we are working on. */
    protected IBankPatch bankData;
    /** This BankEditorFrame instance. */
    protected final BankEditorFrame instance;
    protected Dimension preferredScrollableViewportSize = new Dimension(500, 70);
    protected int autoResizeMode = JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS;
    protected int preferredColumnWidth = 75;

    private JTable table;
    private PatchGridModel myModel;
    private static PatchTransferHandler pth = new PatchGridTransferHandler();

    /**
     * Creates a new <code>BankEditorFrame</code> instance.
     * 
     * @param p
     *            a <code>Patch</code> value
     */
    protected BankEditorFrame(IBankPatch p) {
        super(p.getDevice().getModelName() + " " + p.getType() + " Window",
                true, //resizable
                true, //closable
                true, //maximizable
                true); // iconifiable
        instance = this;
        bankData = p;
        initBankEditorFrame();
    }

    /** Initialize the bank editor frame. */
    protected void initBankEditorFrame() {
        //...Create the GUI and put it in the window...
        myModel = new PatchGridModel(bankData);
        table = new JTable(myModel);
        table.setTransferHandler(pth);
        table.setDragEnabled(true);
        // Only one patch can be handled.
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // Select index (0, 0) to ensure a patch is selected.
        table.changeSelection(0, 0, false, false);
        table.setPreferredScrollableViewportSize(preferredScrollableViewportSize);
        //table.setRowSelectionAllowed(true);
        //table.setColumnSelectionAllowed(true);
        table.setCellSelectionEnabled(true);
        table.setAutoResizeMode(autoResizeMode);
        ListSelectionListener lsl = new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                enableActions();
            }
        };
        table.getSelectionModel().addListSelectionListener(lsl);
        table.getColumnModel().getSelectionModel()
                .addListSelectionListener(lsl);

        table.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger())
                    Actions.showMenuPatchPopup(table, e.getX(), e.getY());
            }

            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger())
                    Actions.showMenuPatchPopup(table, e.getX(), e.getY());
            }

            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2)
                    playSelectedPatch();
            }
        });

        this.addJSLFrameListener(new JSLFrameListener() {
            public void JSLFrameOpened(JSLFrameEvent e) {
            }

            public void JSLFrameClosed(JSLFrameEvent e) {
            }

            public void JSLFrameDeiconified(JSLFrameEvent e) {
            }

            public void JSLFrameIconified(JSLFrameEvent e) {
            }

            public void JSLFrameActivated(JSLFrameEvent e) {
                frameActivated();
            }

            public void JSLFrameClosing(JSLFrameEvent e) {
                JSLFrame[] jList = JSLDesktop.getAllFrames();
                for (int j = 0; j < jList.length; j++)
                    if (jList[j] instanceof PatchEditorFrame) {
                        if (((PatchEditorFrame) (jList[j])).bankFrame == instance) {
                            jList[j].moveToFront();
                            try {
                                jList[j].setSelected(true);
                                jList[j].setClosed(true);
                            } catch (Exception e1) {
                            }
                        }
                    }
            }

            public void JSLFrameDeactivated(JSLFrameEvent e) {
                Actions.setEnabled(false, Actions.EN_ALL);
            }
        });

        //Create the scroll pane and add the table to it.
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setTransferHandler(
                new ProxyImportHandler(table, pth));

        //Add the scroll pane to this window.
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        for (int col = 0; col < table.getColumnCount(); col++) {
            TableColumn column = table.getColumnModel().getColumn(col);
            column.setPreferredWidth(preferredColumnWidth);
        }

        //...Then set the window size or call pack...
        setSize(600, 300);

        //Set the window's location.
        moveToDefaultLocation();
    }

    private void frameActivated() {
        Actions.setEnabled(false, Actions.EN_ALL);

        // always enabled
        Actions.setEnabled(true,
                Actions.EN_IMPORT | Actions.EN_NEW_PATCH);

        enableActions();
    }

    /** change state of Actions based on the state of the table. */
    private void enableActions() {
        // one or more patches are included.
        Actions.setEnabled(table.getRowCount() > 0, Actions.EN_SAVE
                | Actions.EN_SAVE_AS | Actions.EN_SEARCH);

        // sort is not supported yet.
        //Actions.setEnabled(table.getRowCount() > 1, Actions.EN_SORT);

        // only one valid patch is selected.
        boolean selectedOne = (table.getSelectedRowCount() == 1
                && table.getSelectedColumnCount() == 1
                && getSelectedPatch() != null);

        Actions.setEnabled(selectedOne,
                Actions.EN_COPY | Actions.EN_CUT | Actions.EN_DELETE
                        | Actions.EN_EXPORT | Actions.EN_PLAY | Actions.EN_SEND
                        | Actions.EN_STORE);

        // All entries are of the same type, so we can check the first one....
        Actions.setEnabled(selectedOne && myModel.getPatchAt(0, 0).hasEditor(),
                Actions.EN_EDIT);
    }

    private int getPatchNum(int row, int col) {
        return col * bankData.getNumPatches() / bankData.getNumColumns() + row;
    }

    private int getSelectedPatchNum() {
        return getPatchNum(table.getSelectedRow(), table.getSelectedColumn());
    }

    // PatchBasket methods

    // This needs to use some sort of factory so correct IPatch can be created.
    public void importPatch(File file) throws IOException,
            FileNotFoundException {
        FileInputStream fileIn = new FileInputStream(file);
        byte[] buffer = new byte[(int) file.length()];
        fileIn.read(buffer);
        fileIn.close();

        IPatch p = (DriverUtil.createPatch(buffer));
        bankData.put(p, getSelectedPatchNum());
        myModel.fireTableDataChanged();
    }

    public void exportPatch(File file) throws IOException,
            FileNotFoundException {
        /*
         * Almost the same thing occurs in LibraryFrame and SceneFrame also.
         * Maybe we should have something like static final
         * writePatch(OutputStream, IPatch) in Patch.
         */
        FileOutputStream fileOut = new FileOutputStream(file);
        fileOut.write(getSelectedPatch().export());
        fileOut.close();
    }

    public void deleteSelectedPatch() { // XXX Do we really need this?
        bankData.delete(getSelectedPatchNum());
        myModel.fireTableDataChanged();
    }

    public void copySelectedPatch() {
        pth.exportToClipboard(table, Toolkit.getDefaultToolkit()
                .getSystemClipboard(), TransferHandler.COPY);
    }

    public IPatch getSelectedPatch() {
        return bankData.get(getSelectedPatchNum());
    }

    public void sendSelectedPatch() {
        // A Bank Patch consists from Single Patches.
        ((ISinglePatch) getSelectedPatch()).send();
    }

    public void sendToSelectedPatch() {
    }

    public void reassignSelectedPatch() {
    }

    public void playSelectedPatch() {
        // A Bank Patch consists from Single Patches.
        ISinglePatch p = (ISinglePatch) getSelectedPatch();
        p.send();
        p.play();
    }

    public void storeSelectedPatch() {
        new SysexStoreDialog(getSelectedPatch(), getSelectedPatchNum());
    }

    public JSLFrame editSelectedPatch() {
        PatchEditorFrame pf = (PatchEditorFrame) getSelectedPatch().edit();
        pf.setBankEditorInformation(this,
                table.getSelectedRow(),
                table.getSelectedColumn());
        return pf;
    }

    public void pastePatch() {
        if (!pth.importData(table, Toolkit.getDefaultToolkit()
                .getSystemClipboard().getContents(this)))
            Actions.setEnabled(false, Actions.EN_PASTE);
    }

    public void pastePatch(IPatch p) {
        myModel.setPatchAt(p, table.getSelectedRow(), table.getSelectedColumn());
    }

    public ArrayList getPatchCollection() {
        return null; //for now bank doesn't support this feature. Need to
                     // extract single and place in collection.
    }

    // end of PatchBasket methods

    // for PatchEditorFrame
    void setPatchAt(IPatch p, int row, int col) {
        myModel.setPatchAt(p, row, col);
    }

    void revalidateDriver() {
        bankData.setDriver();
        if (bankData.hasNullDriver()) {
            try {
                setClosed(true);
            } catch (PropertyVetoException e) {
                ErrorMsg.reportStatus(e);
            }
        }
    }

    // JSLFrame method
    public boolean canImport(DataFlavor[] flavors) {
        // changed by Hiroo July 5th, 2004
        // XXX Do we still need this check? Hiroo
        return (table.getSelectedRowCount() != 0
                && table.getSelectedColumnCount() != 0 
                && pth.canImport(table, flavors));
    }

    private static class PatchGridTransferHandler extends PatchTransferHandler {
        protected Transferable createTransferable(JComponent c) {
            JTable t = (JTable) c;
            PatchGridModel m = (PatchGridModel) t.getModel();
            return (Transferable) m.getPatchAt(t.getSelectedRow(), t
                    .getSelectedColumn());
        }

        protected boolean storePatch(IPatch p, JComponent c) {
            JTable t = (JTable) c;
            PatchGridModel m = (PatchGridModel) t.getModel();
            m.setPatchAt(p, t.getSelectedRow(), t.getSelectedColumn());
            return true;
        }
    }

    private class PatchGridModel extends AbstractTableModel {
        private IBankPatch bankData;

        PatchGridModel(IBankPatch p) {
            super();
            ErrorMsg.reportStatus("PatchGridModel");
            bankData = p;
        }

        public int getColumnCount() {
            return bankData.getNumColumns();
        }

        public int getRowCount() {
            return bankData.getNumPatches() / bankData.getNumColumns();
        }

        public String getColumnName(int col) {
            return "";
        }

        public Object getValueAt(int row, int col) {
            String[] patchNumbers = bankData.getDriver().getPatchNumbers();
            int i = getPatchNum(row, col);
            return (patchNumbers[i] + " " + bankData.getName(i));
        }

        public Class getColumnClass(int c) {
            return String.class;
        }

        public boolean isCellEditable(int row, int col) {
            //----- Start phil@muqus.com (allow patch name editing from a bank
            // edit window)
            //return false;
            return true;
            //----- End phil@muqus.com
        }

        public void setValueAt(Object value, int row, int col) { // not used
            int patchNum = getPatchNum(row, col);
            String[] patchNumbers = bankData.getDriver().getPatchNumbers();
            bankData.setName(patchNum,
                    ((String) value).substring((patchNumbers[patchNum] + " ").length()));
        }

        IPatch getPatchAt(int row, int col) {
            return bankData.get(getPatchNum(row, col));
        }

        void setPatchAt(IPatch p, int row, int col) {
            bankData.put(p, getPatchNum(row, col));
        }
    }
}