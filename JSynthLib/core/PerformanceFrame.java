package core;

import java.awt.event.MouseEvent;
import javax.swing.JOptionPane;
import java.io.File;
import javax.swing.event.InternalFrameEvent;

/**
 *
 * @author  Gerrit.Gehnen <Gerrit.Gehnen@gmx.de>
 * @version $Id$
 */

public class PerformanceFrame extends javax.swing.JInternalFrame implements AbstractLibraryFrame {
    static int openFrameCount = 0;
    static final int xOffset = 30, yOffset = 30;
    PerformanceListModel myModel;
    public DNDLibraryTable table;
    DNDLibraryTable table2;
    javax.swing.JLabel statusBar;
    File filename;
    boolean changed=false;  //has the library been altered since it was last saved?
    PerformanceTableCellEditor rowEditor ;
    
    
    public PerformanceFrame(File file) {
        super(file.getName(),
        true, //resizable
        true, //closable
        true, //maximizable
        true);//iconifiable
        InitLibraryFrame();
    }
    
    public PerformanceFrame() {
        super("Unsaved Performance #" + (++openFrameCount),
        true, //resizable
        true, //closable
        true, //maximizable
        true);//iconifiable
        InitLibraryFrame();
    }
    
    protected void InitLibraryFrame() {
        //...Create the GUI and put it in the window...
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameClosing(InternalFrameEvent e) {
                if (!changed) return;
                
                int i;
                javax.swing.JInternalFrame[] jList =PatchEdit.desktop.getAllFrames();
                
                for (int j=0;j<jList.length;j++) {
                    if (jList[j] instanceof BankEditorFrame) {
                        for (i=0;i<myModel.performanceList.size();i++)
                            if (((BankEditorFrame)(jList[j])).bankData==((Performance)myModel.performanceList.get(i)).getPatch())
                            { jList[j].moveToFront();
                              try{jList[j].setSelected(true);
                              jList[j].setClosed(true); }catch (Exception e1){}
                              break;}
                    }
                    
                    if (jList[j] instanceof PatchEditorFrame) {
                        for (i=0;i<myModel.performanceList.size();i++)
                            if (((PatchEditorFrame)(jList[j])).p==((Patch)(myModel.performanceList.get(i))))
                            { jList[j].moveToFront();
                              try{jList[j].setSelected(true);
                              jList[j].setClosed(true); }catch (Exception e1){}
                              break;}
                    }
                }
                
                if (JOptionPane.showConfirmDialog(null,"This Performance may contain unsaved data.\nSave before closing?","Unsaved Data",JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) return;
                
                if (getTitle().startsWith("Unsaved Performance")) {
                    javax.swing.JFileChooser fc2=new javax.swing.JFileChooser();
                    javax.swing.filechooser.FileFilter type1 = new ExtensionFilter("PatchEdit Performance Files",".perflib");
                    
                    fc2.addChoosableFileFilter(type1);
                    fc2.setFileFilter(type1);
                    
                    int returnVal = fc2.showSaveDialog(null);
                    
                    if (returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {
                        File file = fc2.getSelectedFile();
                        
                        try {
                            if (!file.getName().toUpperCase().endsWith(".PERFLIB"))
                                file=new File(file.getPath()+".perflib");
                            
                            if (file.isDirectory())
                            { ErrorMsg.reportError("Error", "Can not save over a directory");
                              return;
                            }
                            
                            if (file.exists())
                                if (JOptionPane.showConfirmDialog(null,"Are you sure?","File Exists",JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) return;
                            
                            save(file);
                        } catch (Exception ex) {
                            ErrorMsg.reportError("Error", "Error saving File",ex);
                        }
                    }
                    return;
                }
                
                try {
                    save();
                } catch (Exception ex)
                {};
            }
            
            public void internalFrameOpened(InternalFrameEvent e)
            {}
            
            public void internalFrameActivated(InternalFrameEvent e) {
                PatchEdit.receiveAction.setEnabled(true);
                PatchEdit.pasteAction.setEnabled(true);
                PatchEdit.importAction.setEnabled(true);
                PatchEdit.importAllAction.setEnabled(true);
                PatchEdit.newPatchAction.setEnabled(true);
                
                if (table.getRowCount()>0) {
                    PatchEdit.saveAction.setEnabled(true);
                    PatchEdit.menuSaveAs.setEnabled(true);
                    PatchEdit.searchAction.setEnabled(true);
                    PatchEdit.transferPerformanceAction.setEnabled(true);
                }
                
                if (table.getRowCount()>1) {
                    PatchEdit.sortAction.setEnabled(true);
                    PatchEdit.dupAction.setEnabled(true);
                    PatchEdit.crossBreedAction.setEnabled(true);
                }
                
                if (table.getSelectedRowCount()>0) {
                    PatchEdit.extractAction.setEnabled(true);
                    PatchEdit.sendAction.setEnabled(true);
                    PatchEdit.playAction.setEnabled(true);
                    PatchEdit.storeAction.setEnabled(true);
                    
                    Patch myPatch=((Performance)myModel.performanceList.get(table.getSelectedRow())).getPatch();
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
            
            public void internalFrameClosed(InternalFrameEvent e)
            {}
            
            public void internalFrameDeactivated(InternalFrameEvent e) {
                PatchEdit.receiveAction.setEnabled(false);
                PatchEdit.extractAction.setEnabled(false);
                PatchEdit.sendAction.setEnabled(false);
                PatchEdit.playAction.setEnabled(false);
                PatchEdit.storeAction.setEnabled(false);
                PatchEdit.editAction.setEnabled(false);
                PatchEdit.saveAction.setEnabled(false);
                PatchEdit.menuSaveAs.setEnabled(false);
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
                PatchEdit.transferPerformanceAction.setEnabled(false);

            }
            
            public void internalFrameDeiconified(InternalFrameEvent e)
            {}
            
            public void internalFrameIconified(InternalFrameEvent e)
            {}
            
        });
        
        myModel = new PerformanceListModel(changed);
        table = new DNDLibraryTable(myModel);
        table2=table;
        
        rowEditor=new PerformanceTableCellEditor(table);
        
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
        
        //Create the scroll  pane and add the table to it.
        final javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane(table);
        DNDViewport myviewport=new DNDViewport();
        scrollPane.setViewport(myviewport);
        myviewport.setView(table);
        scrollPane.getVerticalScrollBar().addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(MouseEvent e)
            {}
            
            public void mouseReleased(MouseEvent e) {
                myModel.fireTableDataChanged();
            }
        });
        
        //Add the scroll pane to this window.
        javax.swing.JPanel statusPanel=new javax.swing.JPanel();
        statusBar=new javax.swing.JLabel(myModel.performanceList.size()+" Patches");
        statusPanel.add(statusBar);
        
        getContentPane().setLayout(new java.awt.BorderLayout());
        getContentPane().add(scrollPane, java.awt.BorderLayout.CENTER);
        getContentPane().add(statusPanel, java.awt.BorderLayout.SOUTH);
        
        javax.swing.table.TableColumn column = null;
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
        setLocation(xOffset*openFrameCount, yOffset*openFrameCount);
        
        this.addFocusListener(new java.awt.event.FocusListener() {
            public void focusGained(java.awt.event.FocusEvent e) {
                // System.out.println ("Focus Gained");
            }
            
            public void focusLost(java.awt.event.FocusEvent e) {
                //System.out.println ("Focus Lost");
            }
            
        });
        
        table.getModel().addTableModelListener( new javax.swing.event.TableModelListener() {
            public void tableChanged(javax.swing.event.TableModelEvent e) {
                statusBar.setText(myModel.performanceList.size()+" Patches");
                if (((PerformanceListModel)e.getSource()).getRowCount()>0) {
                    PatchEdit.saveAction.setEnabled(true);
                    PatchEdit.menuSaveAs.setEnabled(true);
                    PatchEdit.searchAction.setEnabled(true);
                    PatchEdit.exportAction.setEnabled(true);
                    PatchEdit.transferPerformanceAction.setEnabled(true);
                }
                
                if (((PerformanceListModel)e.getSource()).getRowCount()>1) {
                    PatchEdit.sortAction.setEnabled(true);
                    PatchEdit.dupAction.setEnabled(true);
                    PatchEdit.crossBreedAction.setEnabled(true);
                }
            }
        }
        );
        
        table.getSelectionModel().addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent e) {
                //System.out.println ("ValueChanged"+((ListSelectionModel)e.getSource ()).getMaxSelectionIndex ());
                if (((javax.swing.ListSelectionModel)e.getSource()).getMaxSelectionIndex()>=0) {
                    PatchEdit.extractAction.setEnabled(true);
                    PatchEdit.sendAction.setEnabled(true);
                    PatchEdit.playAction.setEnabled(true);
                    PatchEdit.storeAction.setEnabled(true);
                    
                    Patch myPatch=((Performance)myModel.performanceList.get(table.getSelectedRow())).getPatch();
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
                    PatchEdit.playAction.setEnabled(false);
                    PatchEdit.storeAction.setEnabled(false);
                    PatchEdit.editAction.setEnabled(false);
                    PatchEdit.copyAction.setEnabled(false);
                    PatchEdit.cutAction.setEnabled(false);
                    PatchEdit.deleteAction.setEnabled(false);
                }
            }
        });
    }
    
    
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
                    myModel.performanceList.add(patarray[j]); // add all converted patches
                }
            }
            else { // No conversion. Try just the original patch....
                if  (table.getSelectedRowCount()==0)
                    myModel.performanceList.add(firstpat);
                else
                    myModel.performanceList.add(table.getSelectedRow(),firstpat);
            }
        }
        myModel.fireTableDataChanged();
        changed=true;
        //statusBar.setText(myModel.performanceList.size()+" Patches");
    }
    
    public void ExportPatch(File file) throws java.io.IOException, java.io.FileNotFoundException {
        if (table.getSelectedRowCount()==0) {
            ErrorMsg.reportError("Error", "No Patch Selected.");
            return;
        }
        java.io.FileOutputStream fileOut= new java.io.FileOutputStream(file);
        fileOut.write(((Patch)myModel.performanceList.get(table.getSelectedRow())).sysex);
        fileOut.close();
    }
    
    public void DeleteSelectedPatch() {
        if (table.getSelectedRowCount()==0) {
            ErrorMsg.reportError("Error", "No Patch Selected.");
            return;
        }
        myModel.performanceList.remove(table.getSelectedRow());
        myModel.fireTableDataChanged();
        //statusBar.setText(myModel.performanceList.size()+" Patches");
    }
    
    public void CopySelectedPatch() {
        try {
            if (table.getSelectedRowCount()==0) {
                ErrorMsg.reportError("Error", "No Patch Selected.");
                return;
            }
            Patch myPatch=((Performance)myModel.performanceList.get(table.getSelectedRow())).getPatch();
            byte [] mySysex = new byte[myPatch.sysex.length];
            System.arraycopy(myPatch.sysex,0,mySysex,0,myPatch.sysex.length);
            PatchEdit.Clipboard=new Patch(mySysex,
            (myPatch.date.toString()),
            (myPatch.author.toString()),
            (myPatch.comment.toString()));
        }catch (Exception e)
        {};
    }
    
    public void SendSelectedPatch() {
        Patch myPatch=((Performance)myModel.performanceList.get(table.getSelectedRow())).getPatch();
        PatchEdit.getDriver(myPatch.deviceNum,myPatch.driverNum).calculateChecksum(myPatch);
        PatchEdit.getDriver(myPatch.deviceNum,myPatch.driverNum).sendPatch(myPatch);
    }
    
    public void PlaySelectedPatch() {
        Patch myPatch=((Performance)myModel.performanceList.get(table.getSelectedRow())).getPatch();
        PatchEdit.getDriver(myPatch.deviceNum,myPatch.driverNum).calculateChecksum(myPatch);
        PatchEdit.getDriver(myPatch.deviceNum,myPatch.driverNum).playPatch(myPatch);
    }
    
    public void StoreSelectedPatch() {
        Patch myPatch=((Performance)myModel.performanceList.get(table.getSelectedRow())).getPatch();
        PatchEdit.getDriver(myPatch.deviceNum,myPatch.driverNum).calculateChecksum(myPatch);
        PatchEdit.getDriver(myPatch.deviceNum,myPatch.driverNum).choosePatch(myPatch);
    }
    
    public javax.swing.JInternalFrame EditSelectedPatch() {
        if (table.getSelectedRowCount()==0) {
            ErrorMsg.reportError("Error", "No Patch Selected.");
            return null;
        }
        Patch myPatch=((Performance)myModel.performanceList.get(table.getSelectedRow())).getPatch();
        changed=true;
        return(PatchEdit.getDriver(myPatch.deviceNum,myPatch.driverNum).editPatch(myPatch));
    }
    
    public void PastePatch() {
        Patch myPatch=PatchEdit.Clipboard;
        if (myPatch!=null) {
            byte [] mySysex = new byte[myPatch.sysex.length];
            System.arraycopy(myPatch.sysex,0,mySysex,0,myPatch.sysex.length);
            if (table.getSelectedRowCount()==0)
                myModel.performanceList.add(new Performance(new Patch(mySysex,
                (myPatch.date.toString()),
                (myPatch.author.toString()),
                (myPatch.comment.toString()))));
            else
                myModel.performanceList.add(table.getSelectedRow(),new Performance(new Patch(mySysex,
                (myPatch.date.toString()),
                (myPatch.author.toString()),
                (myPatch.comment.toString()))));
            
            changed=true;
            myModel.fireTableDataChanged();
          //  statusBar.setText(myModel.performanceList.size()+" Patches");
        }
    }
    
    public void save() throws Exception {
        PatchEdit.waitDialog.show();
        java.io.FileOutputStream f = new java.io.FileOutputStream(filename);
        java.io.ObjectOutputStream s = new java.io.ObjectOutputStream(f);
        s.writeObject(myModel.performanceList);
        s.flush();
        s.close();
        f.close();
        PatchEdit.waitDialog.hide();
        changed=false;
    }
    
    public int getSelectedRowCount() {
        return table.getSelectedRowCount();
    }
    
    public void save(File file) throws Exception {
        filename=file;
        setTitle(file.getName());
        save();
    }
    
    public void open(File file) throws Exception {
        PatchEdit.waitDialog.show();
        setTitle(file.getName());
        filename=file;
        java.io.FileInputStream f = new java.io.FileInputStream(file);
        java.io.ObjectInputStream s = new java.io.ObjectInputStream(f);
        myModel.performanceList=(java.util.ArrayList)s.readObject();
        for (int i=0; i<myModel.performanceList.size();i++)
            ((Performance)myModel.performanceList.get(i)).getPatch().ChooseDriver();
        s.close();
        f.close();
        PatchEdit.waitDialog.hide();
        //statusBar.setText(myModel.performanceList.size()+" Patches");
    }
    
    public java.util.ArrayList getPatchCollection() {
        java.util.ArrayList ar=new java.util.ArrayList();
        int i;
        for (i=0;i<myModel.performanceList.size();i++)
            ar.add(myModel.getPatchAt(i));
        return ar;
    }
    
    //Re-assigns drivers to all patches in libraryframe. Called after new drivers are added or or removed
    protected void revalidateDrivers() {
        int i;
        for (i=0;i<myModel.performanceList.size();i++)
            ((Performance)(myModel.performanceList.get(i))).getPatch().ChooseDriver();
        myModel.fireTableDataChanged();
    }
    
    void sendPerformance() {
        int i,bankNum,patchNum;
   //     System.out.println("Transfering Performance");
        for (i=0;i<myModel.performanceList.size();i++) {
            bankNum=((Performance)(myModel.performanceList.get(i))).getBankNumber();
            patchNum=((Performance)(myModel.performanceList.get(i))).getPatchNumber();
            Patch myPatch=((Performance)myModel.performanceList.get(i)).getPatch();
            PatchEdit.getDriver(myPatch.deviceNum,myPatch.driverNum).calculateChecksum(myPatch);
            PatchEdit.getDriver(myPatch.deviceNum,myPatch.driverNum).storePatch(myPatch,bankNum,patchNum);
        }
    }
    
    public AbstractPatchListModel getAbstractPatchListModel() {
        return myModel;
    }
    
    public DNDLibraryTable getTable() {
        return table;
    }
    
}