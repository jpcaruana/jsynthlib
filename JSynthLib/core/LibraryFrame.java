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
		 "This Library may contain unsaved data.\nSave before closing?",
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
        myModel = new PatchListModel(changed);
        final JTable table = new JTable(myModel);
        table2 = table;		// What's this?

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
        column = table.getColumnModel().getColumn(FIELD1);
        column.setPreferredWidth(50);
        column = table.getColumnModel().getColumn(FIELD2);
        column.setPreferredWidth(50);
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
				       | Actions.EN_SEARCH);
// 				       | Actions.EN_TRANSFER_SCENE);

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
			   | Actions.EN_SEARCH);
// 			   | Actions.EN_TRANSFER_SCENE);

	Actions.setEnabled(table.getRowCount() > 1,
			   Actions.EN_CROSSBREED
			   | Actions.EN_DELETE_DUPLICATES
			   | Actions.EN_SORT);

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
    public void importPatch(File file) throws IOException, FileNotFoundException {
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
	    IPatch[] patarray = firstpat.dissect();

	    if (patarray.length > 0) {
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
        myModel.list.remove(table.getSelectedRow());
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
        myPatch.getDriver().calculateChecksum(myPatch);
        myPatch.getDriver().sendPatch(myPatch);
    }

    public void sendToSelectedPatch() {
	IPatch myPatch = myModel.getPatchAt(table.getSelectedRow());
	myPatch.getDriver().calculateChecksum(myPatch);
	new SysexSendToDialog(myPatch);
    }

    public void reassignSelectedPatch() {
	IPatch myPatch = myModel.getPatchAt(table.getSelectedRow());
	myPatch.getDriver().calculateChecksum(myPatch);
	new ReassignPatchDialog(myPatch);
	myModel.fireTableDataChanged();
    }

    public void playSelectedPatch() {
	IPatch myPatch = myModel.getPatchAt(table.getSelectedRow());
        myPatch.getDriver().calculateChecksum(myPatch);
        myPatch.getDriver().sendPatch(myPatch);
	myPatch.getDriver().playPatch(myPatch);
    }

    public void storeSelectedPatch() {
	IPatch myPatch = myModel.getPatchAt(table.getSelectedRow());
        myPatch.getDriver().calculateChecksum(myPatch);
 	new SysexStoreDialog(myPatch);
    }

    public JSLFrame editSelectedPatch() {
        if (table.getSelectedRowCount() == 0) {
	    ErrorMsg.reportError("Error", "No Patch Selected. EditAction must be disabled.");
	    return null;
	}
	IPatch myPatch = myModel.getPatchAt(table.getSelectedRow());
        changed = true;
        return myPatch.getDriver().editPatch(myPatch);
    }

    public ArrayList getPatchCollection() {
        return myModel.list;
    }
    // end of PatchBasket methods

    // begin AbstarctLibraryFrame methods
    public AbstractPatchListModel getAbstractPatchListModel() {
        return myModel;
    }

    public JTable getTable() {	// for SearchDialog
        return table;
    }

    public void ExtractSelectedPatch() {
        if (table.getSelectedRowCount() == 0) {
	    ErrorMsg.reportError("Error", "No Patch Selected.");
	    return;
	}
	IPatch myPatch = myModel.getPatchAt(table.getSelectedRow());
        BankDriver myDriver = (BankDriver) myPatch.getDriver();
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
        s.writeObject(myModel.list);
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
        myModel.list = (ArrayList) s.readObject();
        for (int i = 0; i < myModel.getRowCount(); i++)
            myModel.getPatchAt(i).chooseDriver();
        s.close();
        f.close();
        PatchEdit.hideWaitDialog();
        statusBar.setText(myModel.getRowCount() + " Patches");
    }

    /**
     * This needs to not use Patch.  Maybe IPatch should extend Comparable.
     */
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
    private static class SysexSort implements Comparator {
        public int compare(Object a1, Object a2) {
	    String s1 = new String(((Patch) (a1)).sysex);
	    String s2 = new String(((Patch) (a2)).sysex);
	    return s1.compareTo(s2);
        }
    }

    // for SortDialog
    void sortPatch(Comparator c) {
	Collections.sort(myModel.list, c);
	myModel.fireTableDataChanged();
    }

    /**
     * Re-assigns drivers to all patches in libraryframe. Called after
     * new drivers are added or or removed
     */
    protected void revalidateDrivers() {
        for (int i = 0; i < myModel.getRowCount(); i++)
            myModel.getPatchAt(i).chooseDriver();
        myModel.fireTableDataChanged();
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
	    IPatch myPatch = (IPatch) list.get(row);

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
	    IPatch myPatch = (IPatch) list.get(row);
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
	public void addPatch(IPatch p) {
	    list.add(p);
	    //  fireTableRowsUpdated(getRowCount(),getRowCount());
	    this.fireTableDataChanged();
	}

	public void setPatchAt(IPatch p, int row) {
	    list.set(row, p);
	    fireTableRowsUpdated(row, row);
	}

	public IPatch getPatchAt(int row) {
	    return (IPatch) list.get(row);
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
