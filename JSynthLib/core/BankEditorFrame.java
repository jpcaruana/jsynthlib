/* $Id$ */
package core;

import javax.swing.JInternalFrame;
//import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
//import javax.swing.SwingUtilities;
import javax.swing.event.*;
import java.util.*;
import java.awt.event.*;
import java.awt.*;
import java.io.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;

public class BankEditorFrame extends JInternalFrame implements PatchBasket {
//     protected static final int xOffset = 30;
//     protected static final int yOffset = 30;
    /** This is the patch we are working on. */
    protected Patch bankData;
    /** bank driver. */
    protected BankDriver bankDriver;
    /** This BankEditorFrame instance. */
    protected final BankEditorFrame instance; // accessed by YamahaFS1RBankEditor
    /** A table model. */
    protected PatchGridModel myModel;
    // These refer a same JTable object.  For what table2 is?
    protected DNDPatchTable table;
    protected DNDPatchTable table2;

    protected Dimension preferredScrollableViewportSize = new Dimension(500, 70);
    protected int autoResizeMode = JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS;
    protected int preferredColumnWidth = 75;

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
        table = new DNDPatchTable(myModel);
        table2 = table;
        table.setPreferredScrollableViewportSize(preferredScrollableViewportSize);
        table.setRowSelectionAllowed(false);
        table.setColumnSelectionAllowed(false);
	table.setAutoResizeMode(autoResizeMode);
        table.addMouseListener(new MouseAdapter() {
		public void mousePressed(MouseEvent e) {
		    table2.setRowSelectionInterval
			(table2.rowAtPoint(new Point(e.getX(), e.getY())),
			 table2.rowAtPoint(new Point(e.getX(), e.getY())));
		    table2.setColumnSelectionInterval
			(table2.columnAtPoint(new Point(e.getX(), e.getY())),
			 table2.columnAtPoint(new Point(e.getX(), e.getY())));

		    PatchEdit.extractAction.setEnabled(true);
		    PatchEdit.sendAction.setEnabled(true);
		    PatchEdit.sendToAction.setEnabled(false);	// not available yet!
		    PatchEdit.reassignAction.setEnabled(false);	// not available yet!
		    PatchEdit.playAction.setEnabled(true);
		    PatchEdit.storeAction.setEnabled(true);

		    // All entries are of the same type, so we can check the first one....
		    Patch myPatch = ((Patch) myModel.getPatchAt(0, 0));
		    try {
			myPatch.getDriver().getClass().getDeclaredMethod("editPatch", new Class[] {myPatch.getClass()});
			PatchEdit.editAction.setEnabled(true);
		    } catch (NoSuchMethodException ex) {
			if (myPatch.getDriver() instanceof BankDriver)
			    PatchEdit.editAction.setEnabled(true);
		    }
		    PatchEdit.cutAction.setEnabled(true);
		    PatchEdit.copyAction.setEnabled(true);
		    PatchEdit.deleteAction.setEnabled(true);
		    PatchEdit.exportAction.setEnabled(true);

		    if (e.isPopupTrigger())
			PatchEdit.menuPatchPopup.show(table2, e.getX(), e.getY());
		}

		public void mouseReleased(MouseEvent e) {
		    if (e.isPopupTrigger())
			PatchEdit.menuPatchPopup.show(table2, e.getX(), e.getY());
		}
		public void mouseClicked(MouseEvent e) {
		    if (e.getClickCount() == 2)
			PlaySelectedPatch();
		}
	    });

        this.addInternalFrameListener(new InternalFrameListener() {
		public void internalFrameOpened(InternalFrameEvent e) {
		}
		public void internalFrameClosed(InternalFrameEvent e) {
		}
		public void internalFrameDeiconified(InternalFrameEvent e) {
		}
		public void internalFrameIconified(InternalFrameEvent e) {
		}
		public void internalFrameActivated(InternalFrameEvent e) {
		    PatchEdit.receiveAction.setEnabled(false);
		    PatchEdit.pasteAction.setEnabled(true);
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
			PatchEdit.dupAction.setEnabled(true);
		    }
		    if (table.getSelectedRowCount() > 0) {
			PatchEdit.extractAction.setEnabled(true);
			PatchEdit.sendAction.setEnabled(true);
			PatchEdit.sendToAction.setEnabled(false); // not available yet!
			PatchEdit.reassignAction.setEnabled(false); // not available yet!
			PatchEdit.playAction.setEnabled(true);
			PatchEdit.storeAction.setEnabled(true);

			// All entries are of the same type, so we can check the first one....
			Patch myPatch = ((Patch) myModel.getPatchAt(0, 0));
			try {
			    myPatch.getDriver().getClass().getDeclaredMethod("editPatch", new Class[] {myPatch.getClass()});
			    PatchEdit.editAction.setEnabled(true);
			} catch (NoSuchMethodException ex) {
			    if (myPatch.getDriver() instanceof BankDriver)
				PatchEdit.editAction.setEnabled(true);
			}
			PatchEdit.cutAction.setEnabled(true);
			PatchEdit.copyAction.setEnabled(true);
			PatchEdit.deleteAction.setEnabled(true);
			PatchEdit.exportAction.setEnabled(true);
		    }
		    //System.out.println("Frame activated"+table.getSelectedRowCount());
		}
		public void internalFrameClosing(InternalFrameEvent e) {
		    JInternalFrame[] jList = PatchEdit.desktop.getAllFrames();
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

		public void internalFrameDeactivated(InternalFrameEvent e) {
		    //PatchEdit.receiveAction.setEnabled(false);
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
		    PatchEdit.dupAction.setEnabled(false);
		    PatchEdit.cutAction.setEnabled(false);
		    PatchEdit.copyAction.setEnabled(false);
		    PatchEdit.pasteAction.setEnabled(false);
		    PatchEdit.deleteAction.setEnabled(false);
		    PatchEdit.importAction.setEnabled(false);
		    PatchEdit.importAllAction.setEnabled(false);
		    PatchEdit.exportAction.setEnabled(false);
		    PatchEdit.newPatchAction.setEnabled(false);
		    //System.out.println("Frame deactivated");
		}
	    });

        //Create the scroll pane and add the table to it.
        JScrollPane scrollPane = new JScrollPane(table);

        //Add the scroll pane to this window.
        getContentPane().add(scrollPane, BorderLayout.CENTER);

	for (int col = 0; col < table.getColumnCount(); col++) {
	    TableColumn column = table.getColumnModel().getColumn(col);
	    column.setPreferredWidth(preferredColumnWidth);
	}

        //...Then set the window size or call pack...
        setSize(600, 300);

        //Set the window's location.
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
        if (!checkSelected()) return;
        Patch p = getSelectedPatch();
        if (p == null) {
	    ErrorMsg.reportError("Error", "That patch is blank.");
	    return;
	}
        PatchEdit.Clipboard = (Patch) p.clone();
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

    public JInternalFrame EditSelectedPatch() {
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
        if (!checkSelected()) return;
        bankDriver.checkAndPutPatch(bankData, PatchEdit.Clipboard, getSelectedPatchNum());
        myModel.fireTableDataChanged();
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
}
