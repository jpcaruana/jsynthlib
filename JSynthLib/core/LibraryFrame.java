package core;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.TableColumn;
import java.util.ArrayList;

/**
 * @version $Id$
 */

public class LibraryFrame extends JSLFrame implements AbstractLibraryFrame
{
    private static int openFrameCount = 0;
    private static final int xOffset = 30, yOffset = 30;
    PatchListModel myModel;
    JTable table;
    private JTable table2;
    JLabel statusBar;
    private File filename;
    private boolean changed=false;  //has the library been altered since it was last saved?
    // This transferhandler could be shared with scene's too.
    protected static PatchTransferHandler pth = new PatchListTransferHandler();

    public LibraryFrame(File file)
    {
        super(file.getName(),
        true, //resizable
        true, //closable
        true, //maximizable
        true);//iconifiable
        InitLibraryFrame();
    }

    public LibraryFrame()
    {
        super("Unsaved Library #" + (++openFrameCount),
        true, //resizable
        true, //closable
        true, //maximizable
        true);//iconifiable
        InitLibraryFrame();
    }

    protected void InitLibraryFrame()
    {
        //...Create the GUI and put it in the window...
        addJSLFrameListener(new JSLFrameListener()
        {
            public void JSLFrameClosing(JSLFrameEvent e)
            {
                if (!changed) return;

                int i;
                JSLFrame[] jList =JSLDesktop.getAllFrames();

                for (int j=0;j<jList.length;j++)
                {
                    if (jList[j] instanceof BankEditorFrame) {
                        for (i=0;i<myModel.PatchList.size();i++)
                            if (((BankEditorFrame)(jList[j])).bankData==((Patch)(myModel.PatchList.get(i)))) {
				jList[j].moveToFront();
				try {
				    jList[j].setSelected(true);
				    jList[j].setClosed(true);
				} catch (Exception e1){
				}
				break;
			    }
                    }

                    if (jList[j] instanceof PatchEditorFrame) {
                        for (i=0;i<myModel.PatchList.size();i++)
                            if (((PatchEditorFrame)(jList[j])).p==((Patch)(myModel.PatchList.get(i)))) {
				jList[j].moveToFront();
				try{
				    jList[j].setSelected(true);
				    jList[j].setClosed(true);
				} catch (Exception e1){
				}
				break;
			    }
                    }
                }

                if (JOptionPane.showConfirmDialog(null,"This Library may contain unsaved data.\nSave before closing?","Unsaved Data",JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) return;

		moveToFront();
		PatchEdit.saveFrame();


            }

            public void JSLFrameOpened(JSLFrameEvent e)
            {}

            public void JSLFrameActivated(JSLFrameEvent e)
            {
                PatchEdit.receiveAction.setEnabled(true);
                //PatchEdit.pasteAction.setEnabled(true);
                PatchEdit.importAction.setEnabled(true);
                PatchEdit.importAllAction.setEnabled(true);
                PatchEdit.newPatchAction.setEnabled(true);
                PatchEdit.crossBreedAction.setEnabled(true);

                if (table.getRowCount()>0)
                {
                    PatchEdit.saveAction.setEnabled(true);
                    PatchEdit.saveAsAction.setEnabled(true);
                    PatchEdit.searchAction.setEnabled(true);
                }

                if (table.getRowCount()>1)
                {
                    PatchEdit.sortAction.setEnabled(true);
                    PatchEdit.dupAction.setEnabled(true);
                }

		enableMenus();
            }

            public void JSLFrameClosed(JSLFrameEvent e)
            {}

            public void JSLFrameDeactivated(JSLFrameEvent e)
            {
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
            }

            public void JSLFrameDeiconified(JSLFrameEvent e)
            {}

            public void JSLFrameIconified(JSLFrameEvent e)
            {}

        });

        myModel = new PatchListModel(changed);
        table = new JTable(myModel);
        table2=table;
        table.setPreferredScrollableViewportSize(new Dimension(500, 70));
	table.addMouseListener(new MouseAdapter()
        {
            public void mousePressed(MouseEvent e)
            {
                if(e.isPopupTrigger())
                {
                    PatchEdit.menuPatchPopup.show(table2, e.getX(), e.getY());
                    table2.setRowSelectionInterval(
                    table2.rowAtPoint(new Point(e.getX(),e.getY())),
                    table2.rowAtPoint(new Point(e.getX(),e.getY()))
                    );
                }
            }

            public void mouseReleased(MouseEvent e)
            {
                if(e.isPopupTrigger())
                {
                    PatchEdit.menuPatchPopup.show(table2, e.getX(), e.getY());
                    table2.setRowSelectionInterval(
                    table2.rowAtPoint(new Point(e.getX(),e.getY())),
                    table2.rowAtPoint(new Point(e.getX(),e.getY()))
                    );
                }
            }

            public void mouseClicked(MouseEvent e)
            {
                if(e.getClickCount()==2)
                {
                    PlaySelectedPatch();
                }
            }
        });
	//table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	table.setTransferHandler(pth);
	table.setDragEnabled(true);

        //Create the scroll  pane and add the table to it.
        JScrollPane scrollPane = new JScrollPane(table);
	// Enable drop on scrollpane
	scrollPane.getViewport()
	    .setTransferHandler( new ProxyImportHandler(table, pth) );
        scrollPane.getVerticalScrollBar().addMouseListener(new MouseAdapter()
        {
            public void mousePressed(MouseEvent e)
            {}

            public void mouseReleased(MouseEvent e)
            {
                myModel.fireTableDataChanged();
            }
        });

        //Add the scroll pane to this window.
        JPanel statusPanel=new JPanel();
        statusBar=new JLabel(myModel.PatchList.size()+" Patches");
        statusPanel.add(statusBar);

        getContentPane().add(scrollPane, BorderLayout.CENTER);
        getContentPane().add(statusPanel, BorderLayout.SOUTH);

        TableColumn column = null;
        column = table.getColumnModel().getColumn(0);
        column.setPreferredWidth(50);
        column = table.getColumnModel().getColumn(1);
        column.setPreferredWidth(50);
        column = table.getColumnModel().getColumn(2);
        column.setPreferredWidth(100);
        column = table.getColumnModel().getColumn(3);
        column.setPreferredWidth(50);
        column = table.getColumnModel().getColumn(4);
        column.setPreferredWidth(50);
        column = table.getColumnModel().getColumn(5);
        column.setPreferredWidth(200);

        //...Then set the window size or call pack...
        setSize(600,300);

        //Set the window's location.
	moveToDefaultLocation();

        table.getModel().addTableModelListener( new TableModelListener()
        {
            public void tableChanged(TableModelEvent e)
            {
                  statusBar.setText(myModel.PatchList.size()+" Patches");
                if (((PatchListModel)e.getSource()).getRowCount()>0)
                {
                    PatchEdit.saveAction.setEnabled(true);
                    PatchEdit.saveAsAction.setEnabled(true);
                    PatchEdit.searchAction.setEnabled(true);
                    PatchEdit.exportAction.setEnabled(true);
                }

                if (((PatchListModel)e.getSource()).getRowCount()>1)
                {
                    PatchEdit.sortAction.setEnabled(true);
                    PatchEdit.dupAction.setEnabled(true);
                    PatchEdit.crossBreedAction.setEnabled(true);
                }
            }
        }
        );

        table.getSelectionModel().addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent e)
            {
                //System.out.println ("ValueChanged"+((ListSelectionModel)e.getSource ()).getMaxSelectionIndex ());
                if (((ListSelectionModel)e.getSource()).getMaxSelectionIndex()>=0)
                {
                    PatchEdit.extractAction.setEnabled(true);
                    PatchEdit.sendAction.setEnabled(true);
                    PatchEdit.sendToAction.setEnabled(true);
                    PatchEdit.playAction.setEnabled(true);
                    PatchEdit.storeAction.setEnabled(true);
 		    PatchEdit.reassignAction.setEnabled(true);

                    Patch myPatch=((Patch)myModel.PatchList.get(table.getSelectedRow()));
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

                else

                {
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


    public void ImportPatch(File file) throws IOException,FileNotFoundException
    {
        int i;
        int offset=0;

        if (ImportMidiFile.doImport(file))
        {
          return;
        }

        FileInputStream fileIn= new FileInputStream(file);
        byte [] buffer =new byte [(int)file.length()];

        fileIn.read(buffer);
        fileIn.close();
        while (offset<buffer.length-1) {
            // There is still something unprocessed in the file
            Patch firstpat=new Patch(buffer,offset);
            offset+=firstpat.sysex.length;
            System.out.println("Buffer length:"+ buffer.length+" Patch Lenght: "+firstpat.sysex.length);
	    Patch[] patarray=firstpat.dissect();

	    if (patarray.length>1) {
		// Conversion was sucessfull, we have at least one converted patch
		for (int j=0;j<patarray.length;j++) {
		    myModel.PatchList.add(patarray[j]); // add all converted patches
		}
	    } else {
		// No conversion. Try just the original patch....
		if  (table.getSelectedRowCount()==0)
		    myModel.PatchList.add(firstpat);
		else
		    myModel.PatchList.add(table.getSelectedRow(),firstpat);
	    }
        }
        myModel.fireTableDataChanged();
        changed=true;
        //statusBar.setText(myModel.PatchList.size()+" Patches");
    }

    public void ExportPatch(File file) throws IOException,FileNotFoundException
    {
        if (table.getSelectedRowCount()==0)
        {
           ErrorMsg.reportError("Error", "No Patch Selected.");
           return;
	}
        FileOutputStream fileOut= new FileOutputStream(file);
        fileOut.write(((Patch)myModel.PatchList.get(table.getSelectedRow())).sysex);
        fileOut.close();
    }

    public void DeleteSelectedPatch()
    {
        if (table.getSelectedRowCount()==0)
        {
        ErrorMsg.reportError("Error", "No Patch Selected.");
        return;
        }
        myModel.PatchList.remove(table.getSelectedRow());
        myModel.fireTableDataChanged();
//        statusBar.setText(myModel.PatchList.size()+" Patches");
    }

    public void CopySelectedPatch()
    {
	pth.exportToClipboard(table,
			      Toolkit.getDefaultToolkit().getSystemClipboard(),
			      pth.COPY);
    }

    public Patch GetSelectedPatch() {
	try {
	    return ((Patch)myModel.PatchList.get(table.getSelectedRow()));
	} catch (Exception e) {
	    ErrorMsg.reportStatus(e);
	    return null;
	}
    }

    public void SendSelectedPatch()
    {
        Patch myPatch=((Patch)myModel.PatchList.get(table.getSelectedRow()));
        myPatch.getDriver().calculateChecksum(myPatch);
        myPatch.getDriver().sendPatch(myPatch);
    }
    public void SendToSelectedPatch()
     {
         Patch myPatch=((Patch)myModel.PatchList.get(table.getSelectedRow()));
         myPatch.getDriver().calculateChecksum(myPatch);
 	 new SysexSendToDialog(myPatch);
     }

     public void ReassignSelectedPatch()
     {
         Patch myPatch=((Patch)myModel.PatchList.get(table.getSelectedRow()));
         myPatch.getDriver().calculateChecksum(myPatch);
 	 new ReassignPatchDialog(myPatch);
 	 myModel.fireTableDataChanged();
     }

    public void PlaySelectedPatch()
    {
        Patch myPatch=((Patch)myModel.PatchList.get(table.getSelectedRow()));
        myPatch.getDriver().calculateChecksum(myPatch);
        myPatch.getDriver().sendPatch(myPatch);
	myPatch.getDriver().playPatch(myPatch);
    }

    public void StoreSelectedPatch()
    {
        Patch myPatch=((Patch)myModel.PatchList.get(table.getSelectedRow()));
        myPatch.getDriver().calculateChecksum(myPatch);
 	new SysexStoreDialog(myPatch);
    }

    public JSLFrame EditSelectedPatch()
    {
        if (table.getSelectedRowCount()==0)
        {
           ErrorMsg.reportError("Error", "No Patch Selected.");
           return null;
        }
        Patch myPatch=((Patch)myModel.PatchList.get(table.getSelectedRow()));
        changed=true;
        return myPatch.getDriver().editPatch(myPatch);
    }

    public void PastePatch()
    {
	pth.importData(table, Toolkit.getDefaultToolkit().getSystemClipboard().getContents(this));
    }
    public void PastePatch(Patch p) {
	pth.importData(table, p);
    }

    public void save() throws Exception
    {
        PatchEdit.waitDialog.show();
        FileOutputStream f = new FileOutputStream(filename);
        ObjectOutputStream s = new ObjectOutputStream(f);
        s.writeObject(myModel.PatchList);
        s.flush();
        s.close();
        f.close();
        PatchEdit.waitDialog.hide();
        changed=false;
    }

    public void ExtractSelectedPatch()
    {
        if (table.getSelectedRowCount()==0)
        {
ErrorMsg.reportError("Error","No Patch Selected.");
return;
}
        Patch myPatch=((Patch)myModel.PatchList.get(table.getSelectedRow()));
        BankDriver myDriver=(BankDriver)myPatch.getDriver();
        for (int i=0;i<myDriver.getNumPatches();i++)
            if (myDriver.getPatch(myPatch,i)!=null) myModel.PatchList.add(myDriver.getPatch(myPatch,i));
        myModel.fireTableDataChanged();
        changed=true;
     //   statusBar.setText(myModel.PatchList.size()+" Patches");
    }

    public int getSelectedRowCount()
    {
        return table.getSelectedRowCount();
    }

    public void save(File file) throws Exception
    {
        filename=file;
        setTitle(file.getName());
        save();
    }

    public void open(File file) throws Exception
    {
        PatchEdit.waitDialog.show();
        setTitle(file.getName());
        filename=file;
        FileInputStream f = new FileInputStream(file);
        ObjectInputStream s = new ObjectInputStream(f);
        myModel.PatchList=(ArrayList)s.readObject();
        for (int i=0; i<myModel.PatchList.size();i++)
            ((Patch)myModel.PatchList.get(i)).chooseDriver();
        s.close();
        f.close();
        PatchEdit.waitDialog.hide();
        statusBar.setText(myModel.PatchList.size()+" Patches");
    }

    public ArrayList getPatchCollection()
    {
        return myModel.PatchList;
    }

    //Re-assigns drivers to all patches in libraryframe. Called after new drivers are added or or removed
    protected void revalidateDrivers()
    {
        int i;
        for (i=0;i<myModel.PatchList.size();i++)
            ((Patch)(myModel.PatchList.get(i))).chooseDriver();
        myModel.fireTableDataChanged();
    }

    public AbstractPatchListModel getAbstractPatchListModel() {
        return myModel;
    }

    public JTable getTable() {
        return table;
    }

    protected void enableMenus() {
	boolean b = (table.getSelectedRowCount()>0);

	PatchEdit.extractAction.setEnabled(b);
	PatchEdit.sendAction.setEnabled(b);
	PatchEdit.sendToAction.setEnabled(b);
	PatchEdit.playAction.setEnabled(b);
	PatchEdit.storeAction.setEnabled(b);
	PatchEdit.reassignAction.setEnabled(b);

	if (b) {
	    Patch myPatch=
		((Patch)myModel.PatchList.get(table.getSelectedRow()));

	    // check if the driver for the selected patch has an editor
	    boolean hasEditor = false;
	    try {
		hasEditor = (myPatch.getDriver().getClass()
		     .getMethod("editPatch", new Class[]{myPatch.getClass()})
		     .getDeclaringClass() != Patch.class);
	    } catch (NoSuchMethodException e) {}
	    
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
}
