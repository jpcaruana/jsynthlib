package core;

import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.io.File;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.TableColumn;

/**
 *
 * @author  Gerrit Gehnen
 * @version $Id$
 */

public class SceneFrame extends JSLFrame implements AbstractLibraryFrame {
    private static int openFrameCount = 0;
    private static final int xOffset = 30, yOffset = 30;
    private SceneListModel myModel;
    JTable table;
    private JTable table2;
    private JLabel statusBar;
    private File filename;
    private boolean changed=false;  //has the library been altered since it was last saved?
    private SceneTableCellEditor rowEditor ;
    protected static PatchTransferHandler pth =
	new PatchListTransferHandler();

    /**
     * @param file
     */
    public SceneFrame(File file) {
        super(file.getName(),
        true, //resizable
        true, //closable
        true, //maximizable
        true);//iconifiable
        InitLibraryFrame();
    }

    public SceneFrame() {
        super("Unsaved Scene #" + (++openFrameCount),
        true, //resizable
        true, //closable
        true, //maximizable
        true);//iconifiable
        InitLibraryFrame();
    }

    protected void InitLibraryFrame() {
        //...Create the GUI and put it in the window...
        addJSLFrameListener(new JSLFrameListener() {
            public void JSLFrameClosing(JSLFrameEvent e) {
                if (!changed) return;

                int i;
                JSLFrame[] jList =JSLDesktop.getAllFrames();

                for (int j=0;j<jList.length;j++) {
                    if (jList[j] instanceof BankEditorFrame) {
                        for (i=0;i<myModel.sceneList.size();i++)
                            if (((BankEditorFrame)(jList[j])).bankData==((Scene)myModel.sceneList.get(i)).getPatch()) {
				jList[j].moveToFront();
				try {
				    jList[j].setSelected(true);
				    jList[j].setClosed(true);
				} catch (Exception e1) {
				}
				break;
			    }
                    }

                    if (jList[j] instanceof PatchEditorFrame) {
                        for (i=0;i<myModel.sceneList.size();i++)
                            if (((PatchEditorFrame)(jList[j])).p==((Patch)(myModel.sceneList.get(i)))) {
				jList[j].moveToFront();
				try {
				    jList[j].setSelected(true);
				    jList[j].setClosed(true);
				} catch (Exception e1) {
				}
				break;
			    }
                    }
                }

                if (JOptionPane.showConfirmDialog(null,"This Scene may contain unsaved data.\nSave before closing?","Unsaved Data",JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) return;

		moveToFront();
		PatchEdit.saveFrame();

            }

            public void JSLFrameOpened(JSLFrameEvent e)
            {}

            public void JSLFrameActivated(JSLFrameEvent e) {
                PatchEdit.receiveAction.setEnabled(true);
                //PatchEdit.pasteAction.setEnabled(true);
                PatchEdit.importAction.setEnabled(true);
                PatchEdit.importAllAction.setEnabled(true);
                PatchEdit.newPatchAction.setEnabled(true);

                if (table.getRowCount()>0) {
                    PatchEdit.saveAction.setEnabled(true);
                    PatchEdit.saveAsAction.setEnabled(true);
                    PatchEdit.searchAction.setEnabled(true);
                    PatchEdit.transferSceneAction.setEnabled(true);
                }

                if (table.getRowCount()>1) {
                    PatchEdit.sortAction.setEnabled(true);
                    PatchEdit.dupAction.setEnabled(true);
                    PatchEdit.crossBreedAction.setEnabled(true);
                }

                if (table.getSelectedRowCount()>0) {
                    PatchEdit.extractAction.setEnabled(true);
                    PatchEdit.sendAction.setEnabled(true);
                    PatchEdit.sendToAction.setEnabled(true);
                    PatchEdit.playAction.setEnabled(true);
                    PatchEdit.storeAction.setEnabled(true);
                    PatchEdit.reassignAction.setEnabled(true);


                    Patch myPatch=((Scene)myModel.sceneList.get(table.getSelectedRow())).getPatch();
                    try{
                        // look, if the driver for the selected patch brings his own editor
                        myPatch.getDriver().getClass().getDeclaredMethod("editPatch", new Class[]{myPatch.getClass()});
                        // since the call didn't throw an exception, the driver implements the method itself
                        PatchEdit.editAction.setEnabled(true);
                    }
                    catch(NoSuchMethodException ex) {
                        // oh, the driver has no own editor. Is it a bank driver?
                        if (myPatch.getDriver() instanceof BankDriver) {
                            // for a bankDriver is it ok, since the universal bankEditor works
                            PatchEdit.editAction.setEnabled(true);
                        }
                        else {
                            // don't allow editing
                            PatchEdit.editAction.setEnabled(false);
                        }
                    };

                    PatchEdit.cutAction.setEnabled(true);
                    PatchEdit.copyAction.setEnabled(true);
                    PatchEdit.deleteAction.setEnabled(true);
                    PatchEdit.exportAction.setEnabled(true);
                }

            }

            public void JSLFrameClosed(JSLFrameEvent e)
            {}

            public void JSLFrameDeactivated(JSLFrameEvent e) {
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
                PatchEdit.pasteAction.setEnabled(false);
                PatchEdit.deleteAction.setEnabled(false);
                PatchEdit.importAction.setEnabled(false);
                PatchEdit.importAllAction.setEnabled(false);
                PatchEdit.exportAction.setEnabled(false);
                PatchEdit.newPatchAction.setEnabled(false);
                PatchEdit.crossBreedAction.setEnabled(false);
                PatchEdit.transferSceneAction.setEnabled(false);

            }

            public void JSLFrameDeiconified(JSLFrameEvent e)
            {}

            public void JSLFrameIconified(JSLFrameEvent e)
            {}

        });

        myModel = new SceneListModel(changed);
        table = new JTable(myModel);
        table2=table;

        rowEditor=new SceneTableCellEditor(table);

        table.setPreferredScrollableViewportSize(new java.awt.Dimension(500, 70));
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if(e.isPopupTrigger()) {
                    PatchEdit.menuPatchPopup.show(table2, e.getX(), e.getY());
                    table2.setRowSelectionInterval(
                    table2.rowAtPoint(new java.awt.Point(e.getX(),e.getY())),
                    table2.rowAtPoint(new java.awt.Point(e.getX(),e.getY()))
                    );
                }
            }

            public void mouseReleased(MouseEvent e) {
                if(e.isPopupTrigger()) {
                    PatchEdit.menuPatchPopup.show(table2, e.getX(), e.getY());
                    table2.setRowSelectionInterval(
                    table2.rowAtPoint(new java.awt.Point(e.getX(),e.getY())),
                    table2.rowAtPoint(new java.awt.Point(e.getX(),e.getY()))
                    );
                }
            }

            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount()==2) {
                    PlaySelectedPatch();
                }
            }
        });

	//table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	table.setTransferHandler(pth);
	table.setDragEnabled(true);

        //Create the scroll  pane and add the table to it.
        final JScrollPane scrollPane = new JScrollPane(table);
	// Enable drop on scrollpane
	scrollPane.getViewport()
	    .setTransferHandler( new ProxyImportHandler(table, pth) );
        scrollPane.getVerticalScrollBar().addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(MouseEvent e)
            {}

            public void mouseReleased(MouseEvent e) {
                myModel.fireTableDataChanged();
            }
        });

        //Add the scroll pane to this window.
        JPanel statusPanel=new JPanel();
        statusBar=new JLabel(myModel.sceneList.size()+" Patches");
        statusPanel.add(statusBar);

        getContentPane().setLayout(new java.awt.BorderLayout());
        getContentPane().add(scrollPane, java.awt.BorderLayout.CENTER);
        getContentPane().add(statusPanel, java.awt.BorderLayout.SOUTH);

        TableColumn column = null;
        column = table.getColumnModel().getColumn(0); // Synth
        column.setPreferredWidth(50);
        column = table.getColumnModel().getColumn(1); // Type
        column.setPreferredWidth(50);
        column = table.getColumnModel().getColumn(2); // Patch Name
        column.setPreferredWidth(100);
        column = table.getColumnModel().getColumn(3); //Bank Number
        column.setPreferredWidth(50);

        column.setCellEditor(rowEditor); // Set the special pop-up Editor for Bank numbers

        column = table.getColumnModel().getColumn(4); //Patch Number
        column.setPreferredWidth(50);

        column.setCellEditor(rowEditor); // Set the special pop-up Editorfor Patch Numbers

        column = table.getColumnModel().getColumn(5); //Comment
        column.setPreferredWidth(200);

        //...Then set the window size or call pack...
        setSize(600,300);

        //Set the window's location.
	moveToDefaultLocation();

        table.getModel().addTableModelListener( new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
                statusBar.setText(myModel.sceneList.size()+" Patches");
                if (((SceneListModel)e.getSource()).getRowCount()>0) {
                    PatchEdit.saveAction.setEnabled(true);
                    PatchEdit.saveAsAction.setEnabled(true);
                    PatchEdit.searchAction.setEnabled(true);
                    PatchEdit.exportAction.setEnabled(true);
                    PatchEdit.transferSceneAction.setEnabled(true);
                }

                if (((SceneListModel)e.getSource()).getRowCount()>1) {
                    PatchEdit.sortAction.setEnabled(true);
                    PatchEdit.dupAction.setEnabled(true);
                    PatchEdit.crossBreedAction.setEnabled(true);
                }
            }
        }
        );

        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                //System.out.println ("ValueChanged"+((ListSelectionModel)e.getSource ()).getMaxSelectionIndex ());
                if (((ListSelectionModel)e.getSource()).getMaxSelectionIndex()>=0) {
                    PatchEdit.extractAction.setEnabled(true);
                    PatchEdit.sendAction.setEnabled(true);
                    PatchEdit.sendToAction.setEnabled(true);
                    PatchEdit.playAction.setEnabled(true);
                    PatchEdit.storeAction.setEnabled(true);
                    PatchEdit.reassignAction.setEnabled(true);


                    Patch myPatch=((Scene)myModel.sceneList.get(table.getSelectedRow())).getPatch();
                    try{
                        myPatch.getDriver().getClass().getDeclaredMethod("editPatch", new Class[]{myPatch.getClass()});
                        PatchEdit.editAction.setEnabled(true);
                    }
                    catch(NoSuchMethodException ex) {
                        if (myPatch.getDriver() instanceof BankDriver)
                            PatchEdit.editAction.setEnabled(true);
                        else
                            PatchEdit.editAction.setEnabled(false);
                    };

                    PatchEdit.copyAction.setEnabled(true);
                    PatchEdit.cutAction.setEnabled(true);
                    PatchEdit.deleteAction.setEnabled(true);
                    PatchEdit.exportAction.setEnabled(true);
                }

                else {
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
                }
            }
        });
    }


    /**
     * @param file
     * @throws IOException
     * @throws FileNotFoundException
     */
    public void ImportPatch(File file) throws java.io.IOException, java.io.FileNotFoundException {
        int i;
        int offset=0;
        java.io.FileInputStream fileIn= new java.io.FileInputStream(file);
        byte [] buffer =new byte [(int)file.length()];

        fileIn.read(buffer);
        fileIn.close();
        while (offset<buffer.length-1) {
            // There is still something unprocessed in the file
            Patch firstpat=new Patch(buffer,offset);
            offset+=firstpat.sysex.length;
            //System.out.println("Buffer length:"+ buffer.length+" Patch Lenght: "+firstpat.sysex.length);
            Patch[] patarray=firstpat.dissect();

            if (patarray.length>1) { // Conversion was sucessfull, we have at least one converted patch
                for (int j=0;j<patarray.length;j++) {
                    myModel.sceneList.add(patarray[j]); // add all converted patches
                }
            }
            else { // No conversion. Try just the original patch....
                if  (table.getSelectedRowCount()==0)
                    myModel.sceneList.add(firstpat);
                else
                    myModel.sceneList.add(table.getSelectedRow(),firstpat);
            }
        }
        myModel.fireTableDataChanged();
        changed=true;
    }

    /**
     * @param file
     * @throws IOException
     * @throws FileNotFoundException
     */
    public void ExportPatch(File file) throws java.io.IOException, java.io.FileNotFoundException {
        if (table.getSelectedRowCount()==0) {
            ErrorMsg.reportError("Error", "No Patch Selected.");
            return;
        }
        java.io.FileOutputStream fileOut= new java.io.FileOutputStream(file);
        fileOut.write(((Patch)myModel.sceneList.get(table.getSelectedRow())).sysex);
        fileOut.close();
    }

    public void DeleteSelectedPatch() {
        if (table.getSelectedRowCount()==0) {
            ErrorMsg.reportError("Error", "No Patch Selected.");
            return;
        }
        myModel.sceneList.remove(table.getSelectedRow());
        myModel.fireTableDataChanged();

    }

    public void CopySelectedPatch() {
	pth.exportToClipboard(table,
			      Toolkit.getDefaultToolkit().getSystemClipboard(),
			      pth.COPY);
    }
    public Patch GetSelectedPatch() {
	try {
	    return ((Scene)myModel.sceneList.get(table.getSelectedRow())).getPatch();
	} catch (Exception e) {
	    ErrorMsg.reportStatus(e);
	    return null;
	}
    }

    public void SendSelectedPatch() {
        Patch myPatch=((Scene)myModel.sceneList.get(table.getSelectedRow())).getPatch();
        myPatch.getDriver().calculateChecksum(myPatch);
        myPatch.getDriver().sendPatch(myPatch);
    }

    public void SendToSelectedPatch() {
        Patch myPatch=((Scene)myModel.sceneList.get(table.getSelectedRow())).getPatch();
        myPatch.getDriver().calculateChecksum(myPatch);
        new SysexSendToDialog(myPatch);
    }

    public void ReassignSelectedPatch() {
        Patch myPatch=((Scene)myModel.sceneList.get(table.getSelectedRow())).getPatch();
        myPatch.getDriver().calculateChecksum(myPatch);
        new ReassignPatchDialog(myPatch);
        myModel.fireTableDataChanged();
    }

    public void PlaySelectedPatch() {
        Patch myPatch=((Scene)myModel.sceneList.get(table.getSelectedRow())).getPatch();
        myPatch.getDriver().calculateChecksum(myPatch);
        myPatch.getDriver().sendPatch(myPatch);
        myPatch.getDriver().playPatch(myPatch);
    }

    public void StoreSelectedPatch() {
        Patch myPatch=((Scene)myModel.sceneList.get(table.getSelectedRow())).getPatch();
        myPatch.getDriver().calculateChecksum(myPatch);
        new SysexStoreDialog(myPatch);
    }

    /**
     * @return
     */
    public JSLFrame EditSelectedPatch() {
        if (table.getSelectedRowCount()==0) {
            ErrorMsg.reportError("Error", "No Patch Selected.");
            return null;
        }
        Patch myPatch=((Scene)myModel.sceneList.get(table.getSelectedRow())).getPatch();
        changed=true;
        return(myPatch.getDriver().editPatch(myPatch));
    }

    public void PastePatch() {
	pth.importData(table, Toolkit.getDefaultToolkit().getSystemClipboard().getContents(this));
    }
    public void PastePatch(Patch p) {
	pth.importData(table, p);
    }

    /**
     * @throws Exception
     */
    public void save() throws Exception {
        PatchEdit.waitDialog.show();
        java.io.FileOutputStream f = new java.io.FileOutputStream(filename);
        java.io.ObjectOutputStream s = new java.io.ObjectOutputStream(f);
        s.writeObject(myModel.sceneList);
        s.flush();
        s.close();
        f.close();
        PatchEdit.waitDialog.hide();
        changed=false;
    }

    /**
     * @return
     */
    public int getSelectedRowCount() {
        return table.getSelectedRowCount();
    }

    /**
     * @param file
     * @throws Exception
     */
    public void save(File file) throws Exception {
        filename=file;
        setTitle(file.getName());
        save();
    }

    /**
     * @param file
     * @throws Exception
     */
    public void open(File file) throws Exception {
        PatchEdit.waitDialog.show();
        setTitle(file.getName());
        filename=file;
        java.io.FileInputStream f = new java.io.FileInputStream(file);
        java.io.ObjectInputStream s = new java.io.ObjectInputStream(f);
        myModel.sceneList=(java.util.ArrayList)s.readObject();
        for (int i=0; i<myModel.sceneList.size();i++)
            ((Scene)myModel.sceneList.get(i)).getPatch().chooseDriver();
        s.close();
        f.close();
        PatchEdit.waitDialog.hide();
    }

    /**
     * @return
     */
    public java.util.ArrayList getPatchCollection() {
        java.util.ArrayList ar=new java.util.ArrayList();
        int i;
        for (i=0;i<myModel.sceneList.size();i++)
            ar.add(myModel.getPatchAt(i));
        return ar;
    }

    //Re-assigns drivers to all patches in libraryframe. Called after new drivers are added or or removed
    protected void revalidateDrivers() {
        int i;
        for (i=0;i<myModel.sceneList.size();i++)
            ((Scene)(myModel.sceneList.get(i))).getPatch().chooseDriver();
        myModel.fireTableDataChanged();
    }

    /**
     * Send all patches of the scene to the
     * configured places in the synth's.
     */
    void sendScene() {
        int i,bankNum,patchNum;
        //     System.out.println("Transfering Scene");
        for (i=0;i<myModel.sceneList.size();i++) {
            bankNum=((Scene)(myModel.sceneList.get(i))).getBankNumber();
            patchNum=((Scene)(myModel.sceneList.get(i))).getPatchNumber();
            Patch myPatch=((Scene)myModel.sceneList.get(i)).getPatch();
            myPatch.getDriver().calculateChecksum(myPatch);
            myPatch.getDriver().storePatch(myPatch,bankNum,patchNum);
        }
    }

    /**
     * @return
     */
    public AbstractPatchListModel getAbstractPatchListModel() {
        return myModel;
    }

    /**
     * @return
     */
    public JTable getTable() {
        return table;
    }

    public void ExtractSelectedPatch() {
        if (table.getSelectedRowCount()==0) {
            ErrorMsg.reportError("Error","No Patch Selected.");
            return;
        }
        Patch myPatch=myModel.getPatchAt(table.getSelectedRow());
        BankDriver myDriver=(BankDriver)myPatch.getDriver();
        for (int i=0;i<myDriver.getNumPatches();i++)
            if (myDriver.getPatch(myPatch,i)!=null) myModel.addPatch(myDriver.getPatch(myPatch,i));
        myModel.fireTableDataChanged();
        changed=true;
        //   statusBar.setText(myModel.PatchList.size()+" Patches");
    }

    public boolean canImport(java.awt.datatransfer.DataFlavor[] flavors) {
	return pth.canImport(table, flavors);
    }
}
