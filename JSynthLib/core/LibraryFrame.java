package core;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.event.*;
import javax.swing.JScrollPane;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;
import javax.swing.JInternalFrame;
import javax.swing.JFileChooser;
import javax.swing.*;
import java.util.*;
import java.awt.event.*;
import java.awt.*;
import java.io.*;


public class LibraryFrame extends JInternalFrame implements PatchBasket
{
    static int openFrameCount = 0;
    static final int xOffset = 30, yOffset = 30;
    PatchListModel myModel;
    public DNDLibraryTable table;
    DNDLibraryTable table2;
    JLabel statusBar;
    File filename;
    boolean changed=false;  //has the library been altered since it was last saved?
    public LibraryFrame (File file)
    {
        
        super(file.getName (),
        true, //resizable
        true, //closable
        true, //maximizable
        true);//iconifiable
        
        InitLibraryFrame ();
        
    }
    public LibraryFrame ()
    {
        super("Unsaved Library #" + (++openFrameCount),
        true, //resizable
        true, //closable
        true, //maximizable
        true);//iconifiable
        InitLibraryFrame ();
    }
    
    protected void InitLibraryFrame ()
    {
        //...Create the GUI and put it in the window...
        addInternalFrameListener (new InternalFrameListener ()
        {
            public void internalFrameClosing (InternalFrameEvent e)
            {
                if (!changed) return;
                if (JOptionPane.showConfirmDialog (null,"This Library may contain unsaved data.\nSave before closing?","Delete Duplicate Patches",JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) return;
                if (getTitle ().startsWith ("Unsaved Library"))
                {
                    JFileChooser fc2=new JFileChooser ();
                    javax.swing.filechooser.FileFilter type1 = new ExtensionFilter ("PatchEdit Library Files",".patchlib");
                    fc2.addChoosableFileFilter (type1);
                    fc2.setFileFilter (type1);
                    int returnVal = fc2.showSaveDialog (null);
                    if (returnVal == JFileChooser.APPROVE_OPTION)
                    {
                        File file = fc2.getSelectedFile ();
                        try
                        {
                            if (!file.getName ().toUpperCase ().endsWith (".PATCHLIB"))
                                file=new File (file.getPath ()+".patchlib");
                            if (file.isDirectory ())
                            { ErrorMsg.reportError ("Error", "Can not save over a directory");
                              return;
                            }
                            if (file.exists ())
                                if (JOptionPane.showConfirmDialog (null,"Are you sure?","File Exists",JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) return;
                            
                            save (file);
                        } catch (Exception ex)
                        {
                            ErrorMsg.reportError ("Error", "Error saving File",ex);
                        }
                        
                    }
                    return;
                }
                
                try
                {
                    save ();
                } catch (Exception ex)
                {};
            }
            public void internalFrameOpened (InternalFrameEvent e)
            {}
            public void internalFrameActivated (InternalFrameEvent e)
            {
                PatchEdit.receiveAction.setEnabled (true);
                PatchEdit.pasteAction.setEnabled (true);
                PatchEdit.importAction.setEnabled (true);
                PatchEdit.importAllAction.setEnabled (true);
                PatchEdit.newPatchAction.setEnabled(true);
                PatchEdit.crossBreedAction.setEnabled(true);
                             
                if (table.getRowCount ()>0)
                {
                    PatchEdit.saveAction.setEnabled (true);
                    PatchEdit.menuSaveAs.setEnabled (true);
                    PatchEdit.searchAction.setEnabled (true);
                }
                if (table.getRowCount ()>1)
                {
                    PatchEdit.sortAction.setEnabled (true);
                    PatchEdit.dupAction.setEnabled (true);
                }
                if (table.getSelectedRowCount ()>0)
                {
                    PatchEdit.extractAction.setEnabled (true);
                    PatchEdit.sendAction.setEnabled (true);
                    PatchEdit.playAction.setEnabled (true);
                    PatchEdit.storeAction.setEnabled (true);
                    PatchEdit.editAction.setEnabled (true);
                    PatchEdit.cutAction.setEnabled (true);
                    PatchEdit.copyAction.setEnabled (true);
                    PatchEdit.deleteAction.setEnabled (true);
                    PatchEdit.exportAction.setEnabled (true);
                }
                
//                System.out.println ("Frame activated"+table.getSelectedRowCount ());
            }
            public void internalFrameClosed (InternalFrameEvent e)
            {}
            
            public void internalFrameDeactivated (InternalFrameEvent e)
            {
                PatchEdit.receiveAction.setEnabled (false);
		PatchEdit.extractAction.setEnabled (false);
                PatchEdit.sendAction.setEnabled (false);
                PatchEdit.playAction.setEnabled (false);
                PatchEdit.storeAction.setEnabled (false);
                PatchEdit.editAction.setEnabled (false);
                PatchEdit.saveAction.setEnabled (false);
                PatchEdit.menuSaveAs.setEnabled (false);
                PatchEdit.sortAction.setEnabled (false);
                PatchEdit.searchAction.setEnabled (false);
                PatchEdit.dupAction.setEnabled (false);
                PatchEdit.cutAction.setEnabled (false);
                PatchEdit.copyAction.setEnabled (false);
                PatchEdit.pasteAction.setEnabled (false);
                PatchEdit.deleteAction.setEnabled (false);
                PatchEdit.importAction.setEnabled (false);
                PatchEdit.importAllAction.setEnabled (false);
                PatchEdit.exportAction.setEnabled (false);
                PatchEdit.newPatchAction.setEnabled(false);
                PatchEdit.crossBreedAction.setEnabled(false);
                
//                System.out.println ("Frame deactivated");
            }
            public void internalFrameDeiconified (InternalFrameEvent e)
            {}
            public void internalFrameIconified (InternalFrameEvent e)
            {}
            
        });
        
        
        
        myModel = new PatchListModel (changed);
        table = new DNDLibraryTable (myModel);
        table2=table;
        table.setPreferredScrollableViewportSize (new Dimension (500, 70));
        table.addMouseListener (new MouseAdapter ()
        {
            public void mousePressed (MouseEvent e)
            {
                
                if(e.isPopupTrigger ())
                {
                    PatchEdit.menuPatchPopup.show (table2, e.getX (), e.getY ());
                    table2.setRowSelectionInterval (
                    table2.rowAtPoint (new Point (e.getX (),e.getY ())),
                    table2.rowAtPoint (new Point (e.getX (),e.getY ()))
                    );
                    
                }
            }
            public void mouseReleased (MouseEvent e)
            {
                if(e.isPopupTrigger ())
                {
                    PatchEdit.menuPatchPopup.show (table2, e.getX (), e.getY ());
                    table2.setRowSelectionInterval (
                    table2.rowAtPoint (new Point (e.getX (),e.getY ())),
                    table2.rowAtPoint (new Point (e.getX (),e.getY ()))
                    );
                    
                }
            }
            public void mouseClicked (MouseEvent e)
            {
                if(e.getClickCount ()==2)
                {
                    PlaySelectedPatch ();
                }
            }
        });
        
        //Create the scroll  pane and add the table to it.
        JScrollPane scrollPane = new JScrollPane (table);
        
        scrollPane.getVerticalScrollBar ().addMouseListener (new MouseAdapter ()
        {
            public void mousePressed (MouseEvent e)
            {}
            public void mouseReleased (MouseEvent e)
            {
                myModel.fireTableDataChanged ();
            }
        });
        
        
        //Add the scroll pane to this window.
        JPanel statusPanel=new JPanel ();
        statusBar=new JLabel (myModel.PatchList.size ()+" Patches");
        statusPanel.add (statusBar);
        
        getContentPane ().add (scrollPane, BorderLayout.CENTER);
        getContentPane ().add (statusPanel, BorderLayout.SOUTH);
              
        TableColumn column = null;
        column = table.getColumnModel ().getColumn (0);
        column.setPreferredWidth (50);
        column = table.getColumnModel ().getColumn (1);
        column.setPreferredWidth (50);
        column = table.getColumnModel ().getColumn (2);
        column.setPreferredWidth (100);
        column = table.getColumnModel ().getColumn (3);
        column.setPreferredWidth (50);
        column = table.getColumnModel ().getColumn (4);
        column.setPreferredWidth (50);
        column = table.getColumnModel ().getColumn (5);
        column.setPreferredWidth (200);
        
        //...Then set the window size or call pack...
        setSize (600,300);
        
        //Set the window's location.
        setLocation (xOffset*openFrameCount, yOffset*openFrameCount);
        
        this.addFocusListener (new FocusListener ()
        {
            public void focusGained (FocusEvent e)
            {
               // System.out.println ("Focus Gained");
            }
            public void focusLost (FocusEvent e)
            {
                //System.out.println ("Focus Lost");
            }
        });
        
        table.getModel ().addTableModelListener ( new TableModelListener ()
        {
            public void tableChanged (TableModelEvent e)
            {
                if (((PatchListModel)e.getSource ()).getRowCount ()>0)
                {
                    PatchEdit.saveAction.setEnabled (true);
                    PatchEdit.menuSaveAs.setEnabled (true);
                    PatchEdit.searchAction.setEnabled (true);
		    PatchEdit.exportAction.setEnabled (true);
		    
                }
                if (((PatchListModel)e.getSource ()).getRowCount ()>1)
                {
                    PatchEdit.sortAction.setEnabled (true);
                    PatchEdit.dupAction.setEnabled (true);
                    PatchEdit.crossBreedAction.setEnabled(true);
              
                }
            }
        }
        );
        
        table.getSelectionModel ().addListSelectionListener (new ListSelectionListener ()
        {
            public void valueChanged (ListSelectionEvent e)
            {
                //System.out.println ("ValueChanged"+((ListSelectionModel)e.getSource ()).getMaxSelectionIndex ());
                if (((ListSelectionModel)e.getSource ()).getMaxSelectionIndex ()>=0)
                {
                    PatchEdit.extractAction.setEnabled (true);
                    PatchEdit.sendAction.setEnabled (true);
                    PatchEdit.playAction.setEnabled (true);
                    PatchEdit.storeAction.setEnabled (true);
                    PatchEdit.editAction.setEnabled (true);
                    PatchEdit.copyAction.setEnabled (true);
                    PatchEdit.cutAction.setEnabled (true);
                    PatchEdit.deleteAction.setEnabled (true);
                    PatchEdit.exportAction.setEnabled (true);
                    
                }
                else
                {
                    PatchEdit.extractAction.setEnabled (false);
                    PatchEdit.sendAction.setEnabled (false);
                    PatchEdit.playAction.setEnabled (false);
                    PatchEdit.storeAction.setEnabled (false);
                    PatchEdit.editAction.setEnabled (false);
                    PatchEdit.copyAction.setEnabled (false);
                    PatchEdit.cutAction.setEnabled (false);
                    PatchEdit.deleteAction.setEnabled (false);
                    
                }
            }
        });
    }
/*    Old Code
    public void ImportPatch (File file) throws IOException,FileNotFoundException
    {
        FileInputStream fileIn= new FileInputStream (file);
        byte [] buffer =new byte [(int)file.length ()];
        fileIn.read (buffer);
        fileIn.close ();
        if (table.getSelectedRowCount ()==0) myModel.PatchList.add (new Patch (buffer));
        else myModel.PatchList.add (table.getSelectedRow (),new Patch (buffer));
        myModel.fireTableDataChanged ();
        changed=true;
        statusBar.setText (myModel.PatchList.size ()+" Patches");
    }
 */
    public void ImportPatch (File file) throws IOException,FileNotFoundException
    {
        int i;
        FileInputStream fileIn= new FileInputStream (file);
        byte [] buffer =new byte [(int)file.length ()];
        fileIn.read (buffer);
        fileIn.close ();
        
        Patch firstpat=new Patch (buffer);
        
        Patch[] patarray=firstpat.dissect ();
        
        if (patarray.length>1)
        { // Conversion was sucessfull, we have at least one converted patch
            for (int j=0;j<patarray.length;j++)
            {
                myModel.PatchList.add (patarray[j]); // add all converted patches
            }
        }
        else
        { // No conversion. Try just the original patch....
            
            if  (table.getSelectedRowCount ()==0)
                myModel.PatchList.add (firstpat);
            else
                myModel.PatchList.add (table.getSelectedRow (),firstpat);
        }
        
        myModel.fireTableDataChanged ();
        changed=true;
        statusBar.setText (myModel.PatchList.size ()+" Patches");
    }
    
    
    public void ExportPatch (File file) throws IOException,FileNotFoundException
    {
        if (table.getSelectedRowCount ()==0)
        {ErrorMsg.reportError ("Error", "No Patch Selected.");return;}
        FileOutputStream fileOut= new FileOutputStream (file);
        fileOut.write (((Patch)myModel.PatchList.get (table.getSelectedRow ())).sysex);
        fileOut.close ();
    }
    
    public void DeleteSelectedPatch ()
    {
        if (table.getSelectedRowCount ()==0)
        {ErrorMsg.reportError ("Error", "No Patch Selected.");return;}
        myModel.PatchList.remove (table.getSelectedRow ());
        myModel.fireTableDataChanged ();
        statusBar.setText (myModel.PatchList.size ()+" Patches");
    }
    public void CopySelectedPatch ()
    {
        try
        {
            if (table.getSelectedRowCount ()==0)
            {ErrorMsg.reportError ("Error", "No Patch Selected.");return;}
            Patch myPatch=((Patch)myModel.PatchList.get (table.getSelectedRow ()));
            byte [] mySysex = new byte[myPatch.sysex.length];
            System.arraycopy (myPatch.sysex,0,mySysex,0,myPatch.sysex.length);
            PatchEdit.Clipboard=new Patch (mySysex,
            (myPatch.date.toString ()),
            (myPatch.author.toString ()),
            (myPatch.comment.toString ()));
        }catch (Exception e)
        {};
        
    }
    
    public void SendSelectedPatch ()
    {
        Patch myPatch=((Patch)myModel.PatchList.get (table.getSelectedRow ()));
        PatchEdit.getDriver (myPatch.deviceNum,myPatch.driverNum).calculateChecksum (myPatch);
        PatchEdit.getDriver (myPatch.deviceNum,myPatch.driverNum).sendPatch (myPatch);
    }
    public void PlaySelectedPatch ()
    {
        Patch myPatch=((Patch)myModel.PatchList.get (table.getSelectedRow ()));
        PatchEdit.getDriver (myPatch.deviceNum,myPatch.driverNum).calculateChecksum (myPatch);
        PatchEdit.getDriver (myPatch.deviceNum,myPatch.driverNum).playPatch (myPatch);
    }
    public void StoreSelectedPatch ()
    {
        Patch myPatch=((Patch)myModel.PatchList.get (table.getSelectedRow ()));
        PatchEdit.getDriver (myPatch.deviceNum,myPatch.driverNum).calculateChecksum (myPatch);
        PatchEdit.getDriver (myPatch.deviceNum,myPatch.driverNum).choosePatch (myPatch);
    }
    public JInternalFrame EditSelectedPatch ()
    {
        if (table.getSelectedRowCount ()==0)
        {ErrorMsg.reportError ("Error", "No Patch Selected.");return null;}
        Patch myPatch=((Patch)myModel.PatchList.get (table.getSelectedRow ()));
        changed=true;
        return(PatchEdit.getDriver (myPatch.deviceNum,myPatch.driverNum).editPatch (myPatch));
    }
    
    
    public void PastePatch ()
    {
        
        Patch myPatch=PatchEdit.Clipboard;
        if (myPatch!=null)
        {
            byte [] mySysex = new byte[myPatch.sysex.length];
            System.arraycopy (myPatch.sysex,0,mySysex,0,myPatch.sysex.length);
            if (table.getSelectedRowCount ()==0)
                myModel.PatchList.add (new Patch (mySysex,
                (myPatch.date.toString ()),
                (myPatch.author.toString ()),
                (myPatch.comment.toString ())));
            
            else
                myModel.PatchList.add (table.getSelectedRow (),new Patch (mySysex,
                (myPatch.date.toString ()),
                (myPatch.author.toString ()),
                (myPatch.comment.toString ())));
            
            
            changed=true;
            myModel.fireTableDataChanged ();
            statusBar.setText (myModel.PatchList.size ()+" Patches");
        }
    }
    public void save () throws Exception
    {
        PatchEdit.waitDialog.show ();
        FileOutputStream f = new FileOutputStream (filename);
        ObjectOutputStream s = new ObjectOutputStream (f);
        s.writeObject (myModel.PatchList);
        s.flush ();
        s.close ();
        f.close ();
        PatchEdit.waitDialog.hide ();
        changed=false;
    }
    
    public void ExtractSelectedPatch ()
    {
        if (table.getSelectedRowCount ()==0)
        {ErrorMsg.reportError ("Error","No Patch Selected.");return;}
        Patch myPatch=((Patch)myModel.PatchList.get (table.getSelectedRow ()));
        BankDriver myDriver=(BankDriver)PatchEdit.getDriver (myPatch.deviceNum,myPatch.driverNum);
        for (int i=0;i<myDriver.numPatches;i++)
            if (myDriver.getPatch (myPatch,i)!=null) myModel.PatchList.add (myDriver.getPatch (myPatch,i));
        myModel.fireTableDataChanged ();
        changed=true;
        statusBar.setText (myModel.PatchList.size ()+" Patches");
    }
    
    
    public int getSelectedRowCount ()
    {
        return table.getSelectedRowCount ();
    }
    
    public void save (File file) throws Exception
    {
        filename=file;
        setTitle (file.getName ());
        save ();
    }
    public void open (File file) throws Exception
    {
        PatchEdit.waitDialog.show ();
        setTitle (file.getName ());
        filename=file;
        FileInputStream f = new FileInputStream (file);
        ObjectInputStream s = new ObjectInputStream (f);
        myModel.PatchList=(ArrayList)s.readObject ();
        for (int i=0; i<myModel.PatchList.size ();i++)
            ((Patch)myModel.PatchList.get (i)).ChooseDriver ();
        s.close ();
        f.close ();
        PatchEdit.waitDialog.hide ();
        statusBar.setText (myModel.PatchList.size ()+" Patches");
    }
    public ArrayList getPatchCollection ()
    {
        return myModel.PatchList;
    }
    
}
