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
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

public class BankEditorFrame extends JSLFrame implements PatchBasket {
    /** This is the patch we are working on. */
    protected IPatch bankData;
    /** bank driver. */
    protected IBankDriver bankDriver;
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
    protected BankEditorFrame(IPatch p) {
        super(p.getDevice().getModelName() + " "
	      + p.getDriver().getPatchType()
	      + " Window",
	      true, //resizable
	      true, //closable
	      true, //maximizable
	      true); // iconifiable
        instance = this;
	bankData = p;
        bankDriver = (IBankDriver) p.getDriver();
        initBankEditorFrame();
    }

    /** Initialize the bank editor frame. */
    protected void initBankEditorFrame() {
        //...Create the GUI and put it in the window...
        myModel = new PatchGridModel(bankData, bankDriver);
        table = new JTable(myModel);
        table2 = table;
	table.setTransferHandler(pth);
	table.setDragEnabled(true);
	// Only one patch can be handled.
	table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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
	table.getColumnModel().getSelectionModel().addListSelectionListener(lsl);

        table.addMouseListener(new MouseAdapter() {
		public void mousePressed(MouseEvent e) {
		    if (e.isPopupTrigger())
			Actions.showMenuPatchPopup(table2, e.getX(), e.getY());
		}

		public void mouseReleased(MouseEvent e) {
		    if (e.isPopupTrigger())
			Actions.showMenuPatchPopup(table2, e.getX(), e.getY());
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
		    Actions.setEnabled(false,
				       Actions.EN_GET
				       // not available yet!
				       | Actions.EN_SEND_TO
				       | Actions.EN_REASSIGN);
		    Actions.setEnabled(true,
				       Actions.EN_IMPORT
				       | Actions.EN_IMPORT_ALL
				       | Actions.EN_NEW_PATCH);
		    enableActions();
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

    // PatchBasket methods

    // This needs to use some sort of factory so correct IPatch can be created.
    public void importPatch(File file) throws IOException, FileNotFoundException {
        if (!checkSelected()) return;
        FileInputStream fileIn = new FileInputStream(file);
        byte [] buffer = new byte [(int) file.length()];
        fileIn.read(buffer);
        fileIn.close();
        IPatch p = new Patch(buffer); // FIXME Factory
        bankDriver.checkAndPutPatch(bankData, p, getSelectedPatchNum());
        myModel.fireTableDataChanged();
    }

    public void exportPatch(File file) throws IOException, FileNotFoundException {
    	/* Almost the same thing occurs in LibraryFrame and SceneFrame also.
    	 * Maybe we should have something like
    	 * static final writePatch(OutputStream, IPatch) in Patch.
    	 */
        if (!checkSelected()) return;
        FileOutputStream fileOut = new FileOutputStream(file);
        fileOut.write(getSelectedPatch().getByteArray());
        fileOut.close();
    }

    public void deleteSelectedPatch() {
        if (!checkSelected()) return;
        bankDriver.deletePatch(bankData, getSelectedPatchNum());
        myModel.fireTableDataChanged();
    }

    public void copySelectedPatch() {
	pth.exportToClipboard(table,
			      Toolkit.getDefaultToolkit().getSystemClipboard(),
			      TransferHandler.COPY);
    }

    public IPatch getSelectedPatch() {
        return bankDriver.getPatch(bankData, getSelectedPatchNum());
    }

    public void sendSelectedPatch() {
        if (!checkSelected()) return;
        IPatch p = getSelectedPatch();
        if (p == null) {
	    ErrorMsg.reportError("Error", "That patch is blank.");
	    return;
	}
        if (p.getDriver() instanceof ISingleDriver)
            p.send();
    }

    public void sendToSelectedPatch() {
    }

    public void reassignSelectedPatch() {
    }

    public void playSelectedPatch() {
        if (!checkSelected()) return;
        IPatch p = getSelectedPatch();
        if (p == null) {
	    ErrorMsg.reportError("Error", "That patch is blank.");
	    return;
	}
        if (p.getDriver() instanceof ISingleDriver) {
            p.send();
            p.play();
        }
    }

    public void storeSelectedPatch() {
        if (!checkSelected()) return;
        IPatch p = getSelectedPatch();
        if (p == null) {
	    ErrorMsg.reportError("Error", "That patch is blank.");
	    return;
	}
        //p.getDriver().choosePatch(p, table.getSelectedColumn()*bankDriver.numPatches/bankDriver.numColumns+table.getSelectedRow()); // phil@muqus.com
        new SysexStoreDialog(p, table.getSelectedColumn() * bankDriver.getNumPatches() / bankDriver.getNumColumns()
			     + table.getSelectedRow());
    }

    public JSLFrame editSelectedPatch() {
        if (!checkSelected()) return null;
        IPatch p = getSelectedPatch();
        if (p == null) {
	    ErrorMsg.reportError("Error", "That patch is blank.");
	    return null;
	}
        PatchEditorFrame pf = (PatchEditorFrame) p.edit();
        pf.setBankEditorInformation(this, table.getSelectedRow(), table.getSelectedColumn());
        return pf;
    }

    public void pastePatch() {
	if (!pth.importData(table, Toolkit.getDefaultToolkit().getSystemClipboard().getContents(this)))
	    Actions.setEnabled(false, Actions.EN_PASTE);
    }
    public void pastePatch(IPatch p) {
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
	bankDriver = (IBankDriver) bankData.getDriver();
    }

    /** change state of Actions based on the state of the table. */
    private void enableActions() {
	Actions.setEnabled(table.getRowCount() > 0,
			   Actions.EN_SAVE
			   | Actions.EN_SAVE_AS
			   | Actions.EN_SEARCH);
	Actions.setEnabled(table.getRowCount() > 1,
			   Actions.EN_SORT
			   | Actions.EN_DELETE_DUPLICATES);

	// Why don't we select select at relase one item?
	Actions.setEnabled(table.getSelectedRowCount() > 0,
			   Actions.EN_COPY
			   | Actions.EN_CUT
			   | Actions.EN_DELETE
			   | Actions.EN_EXPORT
			   | Actions.EN_EXTRACT
			   | Actions.EN_PLAY
			   | Actions.EN_SEND
			   | Actions.EN_STORE);

	// All entries are of the same type, so we can check the first one....
	IPatch myPatch = myModel.getPatchAt(0, 0);
	Actions.setEnabled(table.getSelectedRowCount() > 0
			   && myPatch.getDriver().hasEditor(),
			   Actions.EN_EDIT);
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
	protected IPatch getSelectedPatch(JComponent c) {
	    try {
		JTable t = (JTable)c;
		PatchGridModel m = (PatchGridModel) t.getModel();
		return m.getPatchAt(t.getSelectedRow(), t.getSelectedColumn());
	    } catch (Exception e) {
		ErrorMsg.reportStatus(e);
		return null;
	    }
	}

	protected boolean storePatch(IPatch p, JComponent c) {
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
	public IPatch bankData;
	public IBankDriver bankDriver;

	public PatchGridModel (IPatch p,IBankDriver d) {
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

	public IPatch getPatchAt(int row, int col) {
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

	public void setPatchAt(IPatch p,int row,int col) {
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
