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
import javax.swing.ListSelectionModel;
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
	      true,		//resizable
	      true,		//closable
	      true,		//maximizable
	      true);		//iconifiable
        initLibraryFrame();
    }

    public SceneFrame() {
        super("Unsaved Scene #" + (++openFrameCount),
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
			 "This Scene may contain unsaved data.\nSave before closing?",
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

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        getContentPane().add(statusPanel, BorderLayout.SOUTH);

        //...Then set the window size or call pack...
        setSize(600, 300);

        //Set the window's location.
	moveToDefaultLocation();
    }

    private JTable createTable() {
        myModel = new SceneListModel(changed);
        final JTable table = new JTable(myModel);
        table2 = table;		// What's this?

        rowEditor = new SceneTableCellEditor(table);

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
		    if (((AbstractPatchListModel) e.getSource()).getRowCount() > 0) {
			PatchEdit.saveAction.setEnabled(true);
			PatchEdit.saveAsAction.setEnabled(true);
			PatchEdit.searchAction.setEnabled(true);
			PatchEdit.exportAction.setEnabled(true);
			PatchEdit.transferSceneAction.setEnabled(true);
		    }

		    if (((AbstractPatchListModel) e.getSource()).getRowCount() > 1) {
			PatchEdit.sortAction.setEnabled(true);
			PatchEdit.deleteDuplicatesAction.setEnabled(true);
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
	PatchEdit.getAction.setEnabled(true);
	PatchEdit.importAction.setEnabled(true);
	PatchEdit.importAllAction.setEnabled(true);
	PatchEdit.newPatchAction.setEnabled(true);

	if (table.getRowCount() > 0) {
	    PatchEdit.saveAction.setEnabled(true);
	    PatchEdit.saveAsAction.setEnabled(true);
	    PatchEdit.searchAction.setEnabled(true);
	    PatchEdit.transferSceneAction.setEnabled(true);
	}

	if (table.getRowCount() > 1) {
	    PatchEdit.sortAction.setEnabled(true);
	    PatchEdit.deleteDuplicatesAction.setEnabled(true);
	    PatchEdit.crossBreedAction.setEnabled(true);
	}

	boolean b = (table.getSelectedRowCount() > 0);

	PatchEdit.extractAction.setEnabled(b);
	PatchEdit.sendAction.setEnabled(b);
	PatchEdit.sendToAction.setEnabled(b);
	PatchEdit.playAction.setEnabled(b);
	PatchEdit.storeAction.setEnabled(b);
	PatchEdit.reassignAction.setEnabled(b);
	PatchEdit.uploadAction.setEnabled(b);

	if (b) {
	    Patch myPatch = myModel.getPatchAt(table.getSelectedRow());

	    try {
		// look, if the driver for the selected patch brings his own editor
		myPatch.getDriver().getClass()
		    .getDeclaredMethod("editPatch", new Class[]{myPatch.getClass()});
		// since the call didn't throw an exception, the driver implements the method itself
		PatchEdit.editAction.setEnabled(true);
	    } catch (NoSuchMethodException ex) {
		// oh, the driver has no own editor. Is it a bank driver?
		if (myPatch.getDriver() instanceof BankDriver) {
		    // for a bankDriver is it ok, since the universal bankEditor works
		    PatchEdit.editAction.setEnabled(true);
		} else {
		    // don't allow editing
		    PatchEdit.editAction.setEnabled(false);
		}
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
	PatchEdit.getAction.setEnabled(false);
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
	PatchEdit.deleteDuplicatesAction.setEnabled(false);
	PatchEdit.cutAction.setEnabled(false);
	PatchEdit.copyAction.setEnabled(false);
	PatchEdit.deleteAction.setEnabled(false);
	PatchEdit.importAction.setEnabled(false);
	PatchEdit.importAllAction.setEnabled(false);
	PatchEdit.exportAction.setEnabled(false);
	PatchEdit.newPatchAction.setEnabled(false);
	PatchEdit.crossBreedAction.setEnabled(false);
	PatchEdit.transferSceneAction.setEnabled(false);
	PatchEdit.uploadAction.setEnabled(false);
    }

    public void importPatch(File file) throws IOException, FileNotFoundException {
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
	// Scene does not have sysex field, Hiroo
        //fileOut.write(((Patch) myModel.list.get(table.getSelectedRow())).sysex);
        fileOut.write(myModel.getPatchAt(table.getSelectedRow()).sysex);
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

    public Patch getSelectedPatch() {
	try {
	    return myModel.getPatchAt(table.getSelectedRow());
	} catch (Exception e) {
	    ErrorMsg.reportStatus(e);
	    return null;
	}
    }

    public void sendSelectedPatch() {
	Patch myPatch = myModel.getPatchAt(table.getSelectedRow());
        myPatch.getDriver().calculateChecksum(myPatch);
        myPatch.getDriver().sendPatch(myPatch);
    }

    public void sendToSelectedPatch() {
	Patch myPatch = myModel.getPatchAt(table.getSelectedRow());
        myPatch.getDriver().calculateChecksum(myPatch);
        new SysexSendToDialog(myPatch);
    }

    public void reassignSelectedPatch() {
	Patch myPatch = myModel.getPatchAt(table.getSelectedRow());
        myPatch.getDriver().calculateChecksum(myPatch);
        new ReassignPatchDialog(myPatch);
        myModel.fireTableDataChanged();
    }

    public void playSelectedPatch() {
	Patch myPatch = myModel.getPatchAt(table.getSelectedRow());
        myPatch.getDriver().calculateChecksum(myPatch);
        myPatch.getDriver().sendPatch(myPatch);
        myPatch.getDriver().playPatch(myPatch);
    }

    public void storeSelectedPatch() {
	Patch myPatch = myModel.getPatchAt(table.getSelectedRow());
        myPatch.getDriver().calculateChecksum(myPatch);
        new SysexStoreDialog(myPatch);
    }

    public JSLFrame editSelectedPatch() {
        if (table.getSelectedRowCount() == 0) {
            ErrorMsg.reportError("Error", "No Patch Selected.");
            return null;
        }
	Patch myPatch = myModel.getPatchAt(table.getSelectedRow());
        changed = true;
        return myPatch.getDriver().editPatch(myPatch);
    }

    public void pastePatch() {
	if (!pth.importData(table, Toolkit.getDefaultToolkit().getSystemClipboard().getContents(this)))
	    PatchEdit.pasteAction.setEnabled(false);
    }

    public void pastePatch(Patch p) {
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
        ArrayList ar = new ArrayList();
        for (int i = 0; i < myModel.getRowCount(); i++)
            ar.add(myModel.getPatchAt(i));
        return ar;
    }

    //Re-assigns drivers to all patches in libraryframe. Called after new drivers are added or or removed
    protected void revalidateDrivers() {
        for (int i = 0; i < myModel.getRowCount(); i++)
            myModel.getPatchAt(i).chooseDriver();
        myModel.fireTableDataChanged();
    }

    /**
     * Send all patches of the scene to the
     * configured places in the synth's.
     */
    void sendScene() {
        //     ErrorMsg.reportStatus("Transfering Scene");
        for (int i = 0; i < myModel.getRowCount(); i++) {
            int bankNum   = ((Scene) myModel.list.get(i)).getBankNumber();
            int patchNum  = ((Scene) myModel.list.get(i)).getPatchNumber();
            Patch myPatch = ((Scene) myModel.list.get(i)).getPatch();
            myPatch.getDriver().calculateChecksum(myPatch);
            myPatch.getDriver().storePatch(myPatch, bankNum, patchNum);
        }
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
		    return myScene.getPatch().getDriver().getPatchName(myScene.getPatch());
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
	    Scene myScene = (Scene) list.get(row);
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
	public void addPatch(Patch p) {
	    Scene perf = new Scene(p);
	    list.add(perf);
	    this.fireTableDataChanged();
	}

	public void setPatchAt(Patch p, int row) {
	    Scene perf;
	    perf = ((Scene) list.get(row));
	    perf.setPatch(p);
	    fireTableRowsUpdated(row, row);
	}

	public Patch getPatchAt(int row) {
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
	    Driver driver;
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
