/* $Id$ */
package core;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.TransferHandler;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

public class BankEditorFrame extends JSLFrame implements PatchBasket {
    /** This is the patch we are working on. */
    protected Patch bankData;
    /** bank driver. */
    protected BankDriver bankDriver;
    /** This BankEditorFrame instance. */
    protected final BankEditorFrame instance; // accessed by YamahaFS1RBankEditor
    /** A table model. */
    protected PatchGridModel myModel;
    // These refer a same JTable object.  For what table2 is?
    protected JTable table;
    protected JTable table2;

    protected Dimension preferredScrollableViewportSize = new Dimension(500, 70);
    protected int autoResizeMode = JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS;
    protected int preferredColumnWidth = 75;

    protected static PatchGridTransferHandler pth =
	new PatchGridTransferHandler();

    /**
     * Creates a new <code>BankEditorFrame</code> instance.
     *
     * @param p a <code>Patch</code> value
     */
    protected BankEditorFrame(Patch p) {
        super(p.getDevice().getModelName() + " "
	      + p.getDriver().getPatchType()
	      + " Window",
	      true, //resizable
	      true, //closable
	      true, //maximizable
	      true); // iconifiable
        instance = this;
	bankData = p;
        bankDriver = (BankDriver) p.getDriver();
        InitBankEditorFrame();
    }

    /** Initialize the bank editor frame. */
    protected void InitBankEditorFrame() {
        //...Create the GUI and put it in the window...
        myModel = new PatchGridModel(bankData, bankDriver);
        table = new JTable(myModel);
        table2 = table;
	table.setTransferHandler(pth);
	table.setDragEnabled(true);
	//table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setPreferredScrollableViewportSize(preferredScrollableViewportSize);
        //table.setRowSelectionAllowed(false);
        //table.setColumnSelectionAllowed(false);
	table.setCellSelectionEnabled(true);
	table.setAutoResizeMode(autoResizeMode);
	ListSelectionListener lsl = new ListSelectionListener() {
		public void valueChanged(ListSelectionEvent e) {
		    enableMenus();
		}
	    };
	table.getSelectionModel().addListSelectionListener(lsl);
	table.getColumnModel().getSelectionModel().addListSelectionListener(lsl);

        table.addMouseListener(new MouseAdapter() {
		public void mousePressed(MouseEvent e) {
		    if (e.isPopupTrigger())
			PatchEdit.showMenuPatchPopup(table2, e.getX(), e.getY());
		}

		public void mouseReleased(MouseEvent e) {
		    if (e.isPopupTrigger())
			PatchEdit.showMenuPatchPopup(table2, e.getX(), e.getY());
		}
		public void mouseClicked(MouseEvent e) {
		    if (e.getClickCount() == 2)
			PlaySelectedPatch();
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
		    	PatchEdit.getAction.setEnabled(false);
			PatchEdit.importAction.setEnabled(true);
			PatchEdit.importAllAction.setEnabled(true);
			PatchEdit.newPatchAction.setEnabled(true);

			if (table.getRowCount() > 0) {
			    PatchEdit.saveAction.setEnabled(true);
			    PatchEdit.saveAsAction.setEnabled(true);
			    PatchEdit.searchAction.setEnabled(true);
			}
			if (table.getRowCount() > 1) {
			    PatchEdit.sortAction.setEnabled(true);
			    PatchEdit.deleteDuplicatesAction.setEnabled(true);
			}
		    enableMenus();
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
		    disableMenus();
		}
	    });

        //Create the scroll pane and add the table to it.
        JScrollPane scrollPane = new JScrollPane(table);
	scrollPane.getViewport()
	    .setTransferHandler(new ProxyImportHandler(table, pth));

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

    private boolean checkSelected() {
        if ((table.getSelectedRowCount() == 0) || (table.getSelectedColumnCount() == 0)) {
	    ErrorMsg.reportError("Error", "No patch is selected");
	    return false;
	}
        return true;
    }

    private int getSelectedPatchNum() {
        table = table2;
        return table.getSelectedColumn() * bankDriver.getNumPatches() / bankDriver.getNumColumns()
	    + table.getSelectedRow();
    }

    private Patch getSelectedPatch() {
        return bankDriver.getPatch(bankData, getSelectedPatchNum());
    }

    // PatchBasket methods

    public void ImportPatch(File file) throws IOException, FileNotFoundException {
        if (!checkSelected()) return;
        FileInputStream fileIn = new FileInputStream(file);
        byte [] buffer = new byte [(int) file.length()];
        fileIn.read(buffer);
        fileIn.close();
        Patch p = new Patch(buffer);
        bankDriver.checkAndPutPatch(bankData, p, getSelectedPatchNum());
        myModel.fireTableDataChanged();
    }

    public void ExportPatch(File file) throws IOException, FileNotFoundException {
        if (!checkSelected()) return;
        Patch p = getSelectedPatch();
        FileOutputStream fileOut = new FileOutputStream(file);
        fileOut.write(p.sysex);
        fileOut.close();
    }

    public void DeleteSelectedPatch() {
        if (!checkSelected()) return;
        bankDriver.deletePatch(bankData, getSelectedPatchNum());
        myModel.fireTableDataChanged();
    }

    public void CopySelectedPatch() {
	pth.exportToClipboard(table,
			      Toolkit.getDefaultToolkit().getSystemClipboard(),
			      TransferHandler.COPY);
    }
    public Patch GetSelectedPatch() {
	return getSelectedPatch();
    }

    public void SendSelectedPatch() {
        if (!checkSelected()) return;
        Patch p = getSelectedPatch();
        if (p == null) {
	    ErrorMsg.reportError("Error", "That patch is blank.");
	    return;
	}
        p.getDriver().sendPatch(p);
    }

    public void SendToSelectedPatch() {
    }

    public void ReassignSelectedPatch() {
    }

    public void PlaySelectedPatch() {
        if (!checkSelected()) return;
        Patch p = getSelectedPatch();
        if (p == null) {
	    ErrorMsg.reportError("Error", "That patch is blank.");
	    return;
	}
        p.getDriver().sendPatch(p);
	p.getDriver().playPatch(p);
    }

    public void StoreSelectedPatch() {
        if (!checkSelected()) return;
        Patch p = getSelectedPatch();
        if (p == null) {
	    ErrorMsg.reportError("Error", "That patch is blank.");
	    return;
	}
        //p.getDriver().choosePatch(p, table.getSelectedColumn()*bankDriver.numPatches/bankDriver.numColumns+table.getSelectedRow()); // phil@muqus.com
        new SysexStoreDialog(p, table.getSelectedColumn() * bankDriver.getNumPatches() / bankDriver.getNumColumns()
			     + table.getSelectedRow());
    }

    public JSLFrame EditSelectedPatch() {
        if (!checkSelected()) return null;
        Patch p = getSelectedPatch();
        if (p == null) {
	    ErrorMsg.reportError("Error", "That patch is blank.");
	    return null;
	}
        PatchEditorFrame pf = (PatchEditorFrame) (p.getDriver().editPatch(p));
        pf.setBankEditorInformation(this, table.getSelectedRow(), table.getSelectedColumn());
        return pf;
    }

    public void PastePatch() {
	if (!pth.importData(table, Toolkit.getDefaultToolkit().getSystemClipboard().getContents(this)))
	    PatchEdit.pasteAction.setEnabled(false);
    }
    public void PastePatch(Patch p) {
	pth.importData(table, p);
    }

    public ArrayList getPatchCollection() {
        return null;   //for now bank doesn't support this feature. Need to extract single and place in collection.
    }

    // end of PatchBasket methods

    void revalidateDriver() {
	if (!bankData.chooseDriver()) {
	    try {
		setClosed(true);
	    } catch (Exception e) {
	    }
	    return;
	}
	bankDriver = (BankDriver) bankData.getDriver();
    }

    protected void disableMenus() {
	//PatchEdit.getAction.setEnabled(false);
	PatchEdit.extractAction.setEnabled(false);
	PatchEdit.sendAction.setEnabled(false);
	PatchEdit.sendToAction.setEnabled(false);
	PatchEdit.reassignAction.setEnabled(false);
	PatchEdit.playAction.setEnabled(false);
	PatchEdit.storeAction.setEnabled(false);
	PatchEdit.editAction.setEnabled(false);
	PatchEdit.saveAction.setEnabled(false);
	PatchEdit.saveAsAction.setEnabled(false);
	PatchEdit.sortAction.setEnabled(false);
	PatchEdit.searchAction.setEnabled(false);
	PatchEdit.deleteDuplicatesAction.setEnabled(false);
	PatchEdit.cutAction.setEnabled(false);
	PatchEdit.copyAction.setEnabled(false);
	PatchEdit.deleteAction.setEnabled(false);
	PatchEdit.importAction.setEnabled(false);
	PatchEdit.importAllAction.setEnabled(false);
	PatchEdit.exportAction.setEnabled(false);
	PatchEdit.newPatchAction.setEnabled(false);
    }

    protected void enableMenus() {
	boolean b = (table.getSelectedRowCount() > 0);
	PatchEdit.extractAction.setEnabled(b);
	PatchEdit.sendAction.setEnabled(b);
	PatchEdit.sendToAction.setEnabled(false); // not available yet!
	PatchEdit.reassignAction.setEnabled(false); // not available yet!
	PatchEdit.playAction.setEnabled(b);
	PatchEdit.storeAction.setEnabled(b);

	// All entries are of the same type, so we can check the first one....
	if (b) {
	    Patch myPatch = ((Patch) myModel.getPatchAt(0, 0));
	    try {
		myPatch.getDriver().getClass().getDeclaredMethod("editPatch", new Class[] {myPatch.getClass()});
		PatchEdit.editAction.setEnabled(true);
	    } catch (NoSuchMethodException ex) {
		if (myPatch.getDriver() instanceof BankDriver)
		    PatchEdit.editAction.setEnabled(true);
	    }
	} else {
	    PatchEdit.editAction.setEnabled(false);
	}
	PatchEdit.cutAction.setEnabled(b);
	PatchEdit.copyAction.setEnabled(b);
	PatchEdit.deleteAction.setEnabled(b);
	PatchEdit.exportAction.setEnabled(b);
    }
    // Enable pasting
    public boolean canImport(java.awt.datatransfer.DataFlavor[] flavors) {
	// changed by Hiroo July 5th, 2004
// 	return checkSelected() && pth.canImport(table, flavors);
	return (table.getSelectedRowCount() != 0
		&& table.getSelectedColumnCount() != 0
		&& pth.canImport(table, flavors));
    }

    private static class PatchGridTransferHandler extends PatchTransferHandler {
	protected Patch getSelectedPatch(JComponent c) {
	    try {
		JTable t = (JTable)c;
		PatchGridModel m = (PatchGridModel) t.getModel();
		return m.getPatchAt(t.getSelectedRow(), t.getSelectedColumn());
	    } catch (Exception e) {
		ErrorMsg.reportStatus(e);
		return null;
	    }
	}

	protected boolean storePatch(Patch p, JComponent c) {
	    try {
		p.chooseDriver();
		JTable t = (JTable)c;
		PatchGridModel m =
		    (PatchGridModel)t.getModel();
		m.setPatchAt(p, t.getSelectedRow(), t.getSelectedColumn());
		return true;
	    } catch (Exception e) {
		ErrorMsg.reportStatus(e);
		return false;
	    }
	}
    }

    class PatchGridModel extends AbstractTableModel {
	public Patch bankData;
	public BankDriver bankDriver;

	public PatchGridModel (Patch p,BankDriver d) {
	    super();
	    ErrorMsg.reportStatus("PatchGridModel");
	    bankData=p;
	    bankDriver=d;
	}

	public int getColumnCount () {
	    return bankDriver.getNumColumns();
	}

	public int getRowCount () {
	    return bankDriver.getNumPatches()/bankDriver.getNumColumns();
	}

	public String getColumnName (int col) {
	    return "";
	}

	public Object getValueAt (int row, int col) {
	    String patchNumbers[] = bankDriver.getPatchNumbers();
	    int i = col*bankDriver.getNumPatches()/bankDriver.getNumColumns()+row;
	    return (patchNumbers[i] + " " + bankDriver.getPatchName(bankData, i));
	}

	public Patch getPatchAt(int row, int col) {
	    int i = col*bankDriver.getNumPatches()/bankDriver.getNumColumns()+row;
	    return bankDriver.getPatch(bankData, i);
	}
	public Class getColumnClass (int c) {
	    return getValueAt (0, c).getClass ();
	}

	public boolean isCellEditable (int row, int col) {
	    //Note that the data/cell address is constant,
	    //no matter where the cell appears onscreen.

	    //----- Start phil@muqus.com (allow patch name editing from a bank edit window)
	    //return false;
	    return true;
	    //----- End phil@muqus.com

	}

	public void setPatchAt(Patch p,int row,int col) {
	    bankDriver.checkAndPutPatch(bankData,p,col*bankDriver.getNumPatches()/bankDriver.getNumColumns()+row);
	    fireTableCellUpdated (row, col);
	}

	public void setValueAt (Object value, int row, int col) {
	    //----- Start phil@muqus.com (allow patch name editing from a bank edit window)
	    int patchNum = col * bankDriver.getNumPatches() / bankDriver.getNumColumns() + row;
	    String[] patchNumbers = bankDriver.getPatchNumbers();
	    bankDriver.setPatchName(bankData, patchNum,
				    ((String) value).substring((patchNumbers[patchNum] + " ").length()));
	    //----- End phil@muqus.com
	    fireTableCellUpdated (row, col);
	}
    }
}
