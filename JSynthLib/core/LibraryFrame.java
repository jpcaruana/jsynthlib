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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

/**
 * @version $Id$
 */

class LibraryFrame extends JSLFrame implements AbstractLibraryFrame {
    private static int openFrameCount = 0;
    // column indices
    private static final int SYNTH      = 0;
    private static final int TYPE       = 1;
    private static final int PATCH_NAME = 2;
    private static final int FIELD1     = 3;
    private static final int FIELD2     = 4;
    private static final int COMMENT    = 5;

    private PatchListModel myModel;
    private JTable table;
    private JTable table2;
    private JLabel statusBar;
    private File filename;
    private boolean changed = false;  //has the library been altered since it was last saved?
    // This transferhandler could be shared with scene's too.
    private static PatchTransferHandler pth = new PatchListTransferHandler();

    public LibraryFrame(File file) {
        super(file.getName(),
	      true,		//resizable
	      true,		//closable
	      true,		//maximizable
	      true);		//iconifiable
        initLibraryFrame();
    }

    public LibraryFrame() {
        super("Unsaved Library #" + (++openFrameCount),
	      true,		//resizable
	      true,		//closable
	      true,		//maximizable
	      true);		//iconifiable
        initLibraryFrame();
    }

    protected void initLibraryFrame() {
        //...Create the GUI and put it in the window...
        addJSLFrameListener(new JSLFrameListener() {
		public void JSLFrameClosing(JSLFrameEvent e) {
		    if (!changed) return;

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
			 "This Library may contain unsaved data.\nSave before closing?",
			 "Unsaved Data", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
			return;

		    moveToFront();
		    PatchEdit.saveFrame();
		}

		public void JSLFrameOpened(JSLFrameEvent e) { }

		public void JSLFrameActivated(JSLFrameEvent e) {
		    enableMenus();
		}

		public void JSLFrameClosed(JSLFrameEvent e) { }

		public void JSLFrameDeactivated(JSLFrameEvent e) {
		    disableMenus();
		}

		public void JSLFrameDeiconified(JSLFrameEvent e) { }

		public void JSLFrameIconified(JSLFrameEvent e) { }
	    });

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

        getContentPane().add(scrollPane, BorderLayout.CENTER);
        getContentPane().add(statusPanel, BorderLayout.SOUTH);

        //...Then set the window size or call pack...
        setSize(600, 300);

        //Set the window's location.
	moveToDefaultLocation();
    }

    private JTable createTable() {
        myModel = new PatchListModel(changed);
        final JTable table = new JTable(myModel);
        table2 = table;		// What's this?

        table.setPreferredScrollableViewportSize(new Dimension(500, 70));
	table.addMouseListener(new MouseAdapter() {
		public void mousePressed(MouseEvent e) {
		    if (e.isPopupTrigger()) {
			PatchEdit.showMenuPatchPopup(table2, e.getX(), e.getY());
			table2.setRowSelectionInterval
			    (table2.rowAtPoint(new Point(e.getX(), e.getY())),
			     table2.rowAtPoint(new Point(e.getX(), e.getY())));
		    }
		}

		public void mouseReleased(MouseEvent e) {
		    if (e.isPopupTrigger()) {
			PatchEdit.showMenuPatchPopup(table2, e.getX(), e.getY());
			table2.setRowSelectionInterval
			    (table2.rowAtPoint(new Point(e.getX(), e.getY())),
			     table2.rowAtPoint(new Point(e.getX(), e.getY())));
		    }
		}

		public void mouseClicked(MouseEvent e) {
		    if (e.getClickCount() == 2) {
			PlaySelectedPatch();
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
        column = table.getColumnModel().getColumn(FIELD1);
        column.setPreferredWidth(50);
        column = table.getColumnModel().getColumn(FIELD2);
        column.setPreferredWidth(50);
        column = table.getColumnModel().getColumn(COMMENT);
        column.setPreferredWidth(200);

        table.getModel().addTableModelListener(new TableModelListener() {
		public void tableChanged(TableModelEvent e) {
		    statusBar.setText(myModel.getRowCount() + " Patches");
		    if (((AbstractPatchListModel) e.getSource()).getRowCount() > 0) {
			PatchEdit.saveAction.setEnabled(true);
			PatchEdit.saveAsAction.setEnabled(true);
			PatchEdit.searchAction.setEnabled(true);
			PatchEdit.exportAction.setEnabled(true);
		    }

		    if (((AbstractPatchListModel) e.getSource()).getRowCount() > 1) {
			PatchEdit.sortAction.setEnabled(true);
			PatchEdit.dupAction.setEnabled(true);
			PatchEdit.crossBreedAction.setEnabled(true);
		    }
		}
	    });

        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
		public void valueChanged(ListSelectionEvent e) {
		    //ErrorMsg.reportStatus ("ValueChanged"+((ListSelectionModel)e.getSource ()).getMaxSelectionIndex ());
		    if (((ListSelectionModel) e.getSource()).getMaxSelectionIndex() >= 0) {
			PatchEdit.extractAction.setEnabled(true);
			PatchEdit.sendAction.setEnabled(true);
			PatchEdit.sendToAction.setEnabled(true);
			PatchEdit.playAction.setEnabled(true);
			PatchEdit.storeAction.setEnabled(true);
			PatchEdit.reassignAction.setEnabled(true);
			PatchEdit.uploadAction.setEnabled(true);

			Patch myPatch = myModel.getPatchAt(table.getSelectedRow());
			try {
 			    myPatch.getDriver().getClass()
				.getDeclaredMethod("editPatch", new Class[]{myPatch.getClass()});
			    PatchEdit.editAction.setEnabled(true);
			} catch (NoSuchMethodException ex) {
			    if (myPatch.getDriver() instanceof BankDriver)
				PatchEdit.editAction.setEnabled(true);
			    else
				PatchEdit.editAction.setEnabled(false);
			}

			PatchEdit.copyAction.setEnabled(true);
			PatchEdit.cutAction.setEnabled(true);
			PatchEdit.deleteAction.setEnabled(true);
			PatchEdit.exportAction.setEnabled(true);
		    } else {
			PatchEdit.extractAction.setEnabled(false);
			PatchEdit.sendAction.setEnabled(false);
			PatchEdit.sendToAction.setEnabled(false);
			PatchEdit.playAction.setEnabled(false);
			PatchEdit.storeAction.setEnabled(false);
			PatchEdit.reassignAction.setEnabled(false);
			PatchEdit.editAction.setEnabled(false);
			PatchEdit.copyAction.setEnabled(false);
			PatchEdit.cutAction.setEnabled(false);
			PatchEdit.deleteAction.setEnabled(false);
			PatchEdit.uploadAction.setEnabled(false);
		    }
		}
	    });
	return table;
    }

    private void enableMenus() {
	PatchEdit.receiveAction.setEnabled(true);
	PatchEdit.importAction.setEnabled(true);
	PatchEdit.importAllAction.setEnabled(true);
	PatchEdit.newPatchAction.setEnabled(true);
	PatchEdit.crossBreedAction.setEnabled(true);

	if (table.getRowCount() > 0) {
	    PatchEdit.saveAction.setEnabled(true);
	    PatchEdit.saveAsAction.setEnabled(true);
	    PatchEdit.searchAction.setEnabled(true);
	}

	if (table.getRowCount() > 1) {
	    PatchEdit.sortAction.setEnabled(true);
	    PatchEdit.dupAction.setEnabled(true);
	}

	if (table.getSelectedRowCount() > 0) {
	    PatchEdit.extractAction.setEnabled(true);
	    PatchEdit.sendAction.setEnabled(true);
	    PatchEdit.sendToAction.setEnabled(true);
	    PatchEdit.playAction.setEnabled(true);
	    PatchEdit.storeAction.setEnabled(true);
	    PatchEdit.reassignAction.setEnabled(true);
	    PatchEdit.uploadAction.setEnabled(true);
	}

	boolean b = (table.getSelectedRowCount() > 0);

	PatchEdit.extractAction.setEnabled(b);
	PatchEdit.sendAction.setEnabled(b);
	PatchEdit.sendToAction.setEnabled(b);
	PatchEdit.playAction.setEnabled(b);
	PatchEdit.storeAction.setEnabled(b);
	PatchEdit.reassignAction.setEnabled(b);

	if (b) {
	    Patch myPatch = myModel.getPatchAt(table.getSelectedRow());

	    // check if the driver for the selected patch has an editor
	    boolean hasEditor = false;
	    try {
		hasEditor = (myPatch.getDriver().getClass()
			     .getMethod("editPatch", new Class[]{myPatch.getClass()})
			     .getDeclaringClass() != Patch.class);
	    } catch (NoSuchMethodException e) {
		ErrorMsg.reportStatus(e);
	    }

	    if (hasEditor)
		PatchEdit.editAction.setEnabled(true);
	    // the driver has no editor. Is it a bank driver?
	    else if (myPatch.getDriver() instanceof BankDriver) {
		// for a bankDriver is it ok, since BankEditorFrame works
		PatchEdit.editAction.setEnabled(true);
	    } else {
		// don't allow editing
		PatchEdit.editAction.setEnabled(false);
	    }
	} else {
	    PatchEdit.editAction.setEnabled(false);
	}
	PatchEdit.cutAction.setEnabled(b);
	PatchEdit.copyAction.setEnabled(b);
	PatchEdit.deleteAction.setEnabled(b);
	PatchEdit.exportAction.setEnabled(b);
    }

    private void disableMenus() {
	PatchEdit.receiveAction.setEnabled(false);
	PatchEdit.extractAction.setEnabled(false);
	PatchEdit.sendAction.setEnabled(false);
	PatchEdit.sendToAction.setEnabled(false);
	PatchEdit.playAction.setEnabled(false);
	PatchEdit.storeAction.setEnabled(false);
	PatchEdit.reassignAction.setEnabled(false);
	PatchEdit.editAction.setEnabled(false);
	PatchEdit.saveAction.setEnabled(false);
	PatchEdit.saveAsAction.setEnabled(false);
	PatchEdit.sortAction.setEnabled(false);
	PatchEdit.searchAction.setEnabled(false);
	PatchEdit.dupAction.setEnabled(false);
	PatchEdit.cutAction.setEnabled(false);
	PatchEdit.copyAction.setEnabled(false);
	PatchEdit.deleteAction.setEnabled(false);
	PatchEdit.importAction.setEnabled(false);
	PatchEdit.importAllAction.setEnabled(false);
	PatchEdit.exportAction.setEnabled(false);
	PatchEdit.newPatchAction.setEnabled(false);
	PatchEdit.crossBreedAction.setEnabled(false);
	PatchEdit.uploadAction.setEnabled(false);
    }

    public void ImportPatch(File file) throws IOException, FileNotFoundException {
        if (ImportMidiFile.doImport(file)) {
	    return;
	}
        FileInputStream fileIn = new FileInputStream(file);
        byte [] buffer = new byte [(int) file.length()];
        fileIn.read(buffer);
        fileIn.close();

        int offset = 0;
        while (offset < buffer.length - 1) {
            // There is still something unprocessed in the file
            Patch firstpat = new Patch(buffer, offset);
            offset += firstpat.sysex.length;
            //ErrorMsg.reportStatus("Buffer length:" + buffer.length + " Patch Lenght: " + firstpat.sysex.length);
	    Patch[] patarray = firstpat.dissect();

	    if (patarray.length > 1) {
		// Conversion was sucessfull, we have at least one converted patch
		for (int j = 0; j < patarray.length; j++) {
		    myModel.list.add(patarray[j]); // add all converted patches
		}
	    } else {
		// No conversion. Try just the original patch....
		if  (table.getSelectedRowCount() == 0)
		    myModel.list.add(firstpat);
		else
		    myModel.list.add(table.getSelectedRow(), firstpat);
	    }
        }
        myModel.fireTableDataChanged();
        changed = true;
        //statusBar.setText(myModel.getRowCount() + " Patches");
    }

    public void ExportPatch(File file) throws IOException, FileNotFoundException {
        if (table.getSelectedRowCount() == 0) {
	    ErrorMsg.reportError("Error", "No Patch Selected.");
	    return;
	}
        FileOutputStream fileOut = new FileOutputStream(file);
        fileOut.write(myModel.getPatchAt(table.getSelectedRow()).sysex);
        fileOut.close();
    }

    public void DeleteSelectedPatch() {
        if (table.getSelectedRowCount() == 0) {
	    ErrorMsg.reportError("Error", "No Patch Selected.");
	    return;
	}
        myModel.list.remove(table.getSelectedRow());
        myModel.fireTableDataChanged();
	//statusBar.setText(myModel.getRowCount() + " Patches");
    }

    public void CopySelectedPatch() {
	pth.exportToClipboard(table,
			      Toolkit.getDefaultToolkit().getSystemClipboard(),
			      PatchTransferHandler.COPY);
    }

    public Patch GetSelectedPatch() {
	try {
	    return myModel.getPatchAt(table.getSelectedRow());
	} catch (Exception e) {
	    ErrorMsg.reportStatus(e);
	    return null;
	}
    }

    public void SendSelectedPatch() {
	Patch myPatch = myModel.getPatchAt(table.getSelectedRow());
        myPatch.getDriver().calculateChecksum(myPatch);
        myPatch.getDriver().sendPatch(myPatch);
    }

    public void SendToSelectedPatch() {
	Patch myPatch = myModel.getPatchAt(table.getSelectedRow());
	myPatch.getDriver().calculateChecksum(myPatch);
	new SysexSendToDialog(myPatch);
    }

    public void ReassignSelectedPatch() {
	Patch myPatch = myModel.getPatchAt(table.getSelectedRow());
	myPatch.getDriver().calculateChecksum(myPatch);
	new ReassignPatchDialog(myPatch);
	myModel.fireTableDataChanged();
    }

    public void PlaySelectedPatch() {
	Patch myPatch = myModel.getPatchAt(table.getSelectedRow());
        myPatch.getDriver().calculateChecksum(myPatch);
        myPatch.getDriver().sendPatch(myPatch);
	myPatch.getDriver().playPatch(myPatch);
    }

    public void StoreSelectedPatch() {
	Patch myPatch = myModel.getPatchAt(table.getSelectedRow());
        myPatch.getDriver().calculateChecksum(myPatch);
 	new SysexStoreDialog(myPatch);
    }

    public JSLFrame EditSelectedPatch() {
        if (table.getSelectedRowCount() == 0) {
	    ErrorMsg.reportError("Error", "No Patch Selected.");
	    return null;
	}
	Patch myPatch = myModel.getPatchAt(table.getSelectedRow());
        changed = true;
        return myPatch.getDriver().editPatch(myPatch);
    }

    public void PastePatch() {
	if (!pth.importData(table, Toolkit.getDefaultToolkit().getSystemClipboard().getContents(this)))
	    PatchEdit.pasteAction.setEnabled(false);
    }

    public void PastePatch(Patch p) {
	pth.importData(table, p);
    }

    public void save() throws Exception {
        PatchEdit.showWaitDialog();
        FileOutputStream f = new FileOutputStream(filename);
        ObjectOutputStream s = new ObjectOutputStream(f);
        s.writeObject(myModel.list);
        s.flush();
        s.close();
        f.close();
        PatchEdit.hideWaitDialog();
        changed = false;
    }

    public int getSelectedRowCount() {
        return table.getSelectedRowCount();
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
        myModel.list = (ArrayList) s.readObject();
        for (int i = 0; i < myModel.getRowCount(); i++)
            myModel.getPatchAt(i).chooseDriver();
        s.close();
        f.close();
        PatchEdit.hideWaitDialog();
        statusBar.setText(myModel.getRowCount() + " Patches");
    }

    public ArrayList getPatchCollection() {
        return myModel.list;
    }

    //Re-assigns drivers to all patches in libraryframe. Called after new drivers are added or or removed
    protected void revalidateDrivers() {
        for (int i = 0; i < myModel.getRowCount(); i++)
            myModel.getPatchAt(i).chooseDriver();
        myModel.fireTableDataChanged();
    }

    public AbstractPatchListModel getAbstractPatchListModel() {
        return myModel;
    }

    public JTable getTable() {
        return table;
    }

    public void ExtractSelectedPatch() {
        if (table.getSelectedRowCount() == 0) {
	    ErrorMsg.reportError("Error", "No Patch Selected.");
	    return;
	}
	Patch myPatch = myModel.getPatchAt(table.getSelectedRow());
        BankDriver myDriver = (BankDriver) myPatch.getDriver();
        for (int i = 0; i < myDriver.getNumPatches(); i++)
            if (myDriver.getPatch(myPatch, i) != null)
		myModel.addPatch(myDriver.getPatch(myPatch, i));
        myModel.fireTableDataChanged();
        changed = true;
	//statusBar.setText(myModel.getRowCount() + " Patches");
    }

    public boolean canImport(DataFlavor[] flavors) {
	return pth.canImport(table, flavors);
    }

    void sortPatch(Comparator c) {
	Collections.sort(myModel.list, c);
	myModel.fireTableDataChanged();
    }

    void deleteDuplicates() {
	Collections.sort(myModel.list, new SysexSort());
	int numDeleted = 0;
	Patch p, q;
	Iterator it = myModel.list.iterator();
	p = (Patch) it.next();
	while (it.hasNext()) {
	    q = (Patch) it.next();
	    if (Arrays.equals(p.sysex, q.sysex)) {
		it.remove();
		numDeleted++;
	    } else
		p = q;
	}
	JOptionPane.showMessageDialog(null, numDeleted + " Patches were Deleted",
				      "Delete Duplicates", JOptionPane.INFORMATION_MESSAGE);
	myModel.fireTableDataChanged();
	statusBar.setText(myModel.getRowCount() + " Patches");
    }

    //This is a comparator class used by the delete duplicated action
    //to sort based on the sysex data
    //Sorting this way makes the Dups search much easier, since the
    //dups must be next to each other
    static class SysexSort implements Comparator {
        public int compare(Object a1, Object a2) {
	    String s1 = new String(((Patch) (a1)).sysex);
	    String s2 = new String(((Patch) (a2)).sysex);
	    return s1.compareTo(s2);
        }
    }

    /**
     * Refactored from PerformanceListModel
     * @author  Gerrit Gehnen
     */
    private class PatchListModel extends AbstractTableModel implements AbstractPatchListModel {
	private final String[] columnNames = {
	    "Synth",
	    "Type",
	    "Patch Name",
	    "Field 1",
	    "Field 2",
	    "Comment"
	};
	private boolean changed;
	private ArrayList list = new ArrayList();

	public PatchListModel(boolean c) {
	    super();
	    changed = c;
	}

	public int getColumnCount() {
	    return columnNames.length;
	}

	public String getColumnName(int col) {
	    return columnNames[col];
	}

	public Object getValueAt(int row, int col) {
	    Patch myPatch = (Patch) list.get(row);

	    switch (col) {
	    case SYNTH:
		return myPatch.getDevice().getSynthName();
	    case TYPE:
		return myPatch.getDriver().getPatchType();
	    case PATCH_NAME:
		return myPatch.getDriver().getPatchName(myPatch);
	    case FIELD1:
		return myPatch.getDate();
	    case FIELD2:
		return myPatch.getAuthor();
	    case COMMENT:
		return myPatch.getComment();
	    default:
		ErrorMsg.reportStatus("LibraryFrame: internal error.");
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
	    Object obj = getValueAt (0, c);
	    if (obj != null)          // Sometimes setValueAt delivers null pointers as value.....
		return obj.getClass();
	    else
		return Object.class;
	}

        /*
         * Don't need to implement this method unless your table's
         * editable.
         */
	public boolean isCellEditable(int row, int col) {
	    //Note that the data/cell address is constant,
	    //no matter where the cell appears onscreen.
	    return (col > TYPE);
	}

        /*
         * Don't need to implement this method unless your table's
         * data can change.
         */
	public void setValueAt(Object value, int row, int col) {
	    changed = true;
	    Patch myPatch = (Patch) list.get(row);
	    switch (col) {
	    case PATCH_NAME:
		myPatch.getDriver().setPatchName(myPatch, (String) value);
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
	    }
	    fireTableCellUpdated(row, col);
	}

	// begin AbstractPatchListModel interface methods
	public void addPatch(Patch p) {
	    list.add(p);
	    //  fireTableRowsUpdated(getRowCount(),getRowCount());
	    this.fireTableDataChanged();
	}

	public void setPatchAt(Patch p, int row) {
	    list.set(row, p);
	    fireTableRowsUpdated(row, row);
	}

	public Patch getPatchAt(int row) {
	    return (Patch) list.get(row);
	}

	public String getCommentAt(int row) {
	    return getPatchAt(row).getComment();
	}

	public int getRowCount() {
	    return list.size();
	}
	// end AbstractPatchListModel interface methods
    }
}
