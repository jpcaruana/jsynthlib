package core;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import javax.swing.JScrollPane;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;
import javax.swing.JInternalFrame;
import javax.swing.event.*;
import java.util.*;
import java.awt.event.*;
import java.awt.*;
import java.io.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;

public class BankEditorFrame extends JInternalFrame implements PatchBasket

{
    static final int xOffset = 30, yOffset = 30;
    PatchGridModel myModel;
    DNDPatchTable table;
    DNDPatchTable table2;
    Patch  bankData;
    BankDriver bankDriver;
    final BankEditorFrame instance;
 
    public BankEditorFrame (Patch p)
    {
        super(PatchEdit.getDriver (p.deviceNum,p.driverNum).model+" "+PatchEdit.getDriver (p.deviceNum,p.driverNum).patchType+" Window",
        true, //resizable
        true, //closable
        true, //maximizable
        true);//iconifiable
        instance=this;
	bankData=p;
        bankDriver=(BankDriver)PatchEdit.getDriver (p.deviceNum ,p.driverNum);
        InitBankEditorFrame ();
    }
    
    protected void InitBankEditorFrame ()
    {
        //...Create the GUI and put it in the window...
        
        myModel = new PatchGridModel (bankData,bankDriver);
        table = new DNDPatchTable (myModel);
        table2=table;
        table.setPreferredScrollableViewportSize (new Dimension (500, 70));
        table.setRowSelectionAllowed (false);
        table.setColumnSelectionAllowed (false);
        table.addMouseListener (new MouseAdapter ()
        {
            public void mousePressed (MouseEvent e)
            {
                table2.setRowSelectionInterval (
                table2.rowAtPoint (new Point (e.getX (),e.getY ())),
                table2.rowAtPoint (new Point (e.getX (),e.getY ()))
                );
                table2.setColumnSelectionInterval (
                table2.columnAtPoint (new Point (e.getX (),e.getY ())),
                table2.columnAtPoint (new Point (e.getX (),e.getY ()))
                );
                
                PatchEdit.extractAction.setEnabled (true);
                PatchEdit.sendAction.setEnabled (true);
                PatchEdit.playAction.setEnabled (true);
                PatchEdit.storeAction.setEnabled (true);
                Patch myPatch=((Patch)myModel.getPatchAt(0,0)); // All entries are of the same type, so we can check the first one....
                try{
                    myPatch.getDriver().getClass().getDeclaredMethod("editPatch", new Class[]{myPatch.getClass()});
                    PatchEdit.editAction.setEnabled(true);
                }
                catch(NoSuchMethodException ex) {
                    if (myPatch.getDriver() instanceof BankDriver)
                        PatchEdit.editAction.setEnabled(true);
                };
                PatchEdit.cutAction.setEnabled (true);
                PatchEdit.copyAction.setEnabled (true);
                PatchEdit.deleteAction.setEnabled (true);
                PatchEdit.exportAction.setEnabled (true);
                
                if(e.isPopupTrigger ())
                {
                    PatchEdit.menuPatchPopup.show (table2, e.getX (), e.getY ());
                }
            }

            public void mouseReleased (MouseEvent e)
            {
                if(e.isPopupTrigger ())
                {
                    PatchEdit.menuPatchPopup.show (table2, e.getX (), e.getY ());
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
        
        this.addInternalFrameListener (new InternalFrameListener ()
        {
            public void internalFrameOpened (InternalFrameEvent e)
            {}
            public void internalFrameClosed (InternalFrameEvent e)
            {}
            public void internalFrameDeiconified (InternalFrameEvent e)
            {}
            public void internalFrameIconified (InternalFrameEvent e)
            {}
            public void internalFrameActivated (InternalFrameEvent e)
            {
                PatchEdit.receiveAction.setEnabled (false);
                PatchEdit.pasteAction.setEnabled (true);
                PatchEdit.importAction.setEnabled (true);
                PatchEdit.importAllAction.setEnabled (true);
                PatchEdit.newPatchAction.setEnabled (true);
                
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
                    Patch myPatch=((Patch)myModel.getPatchAt(0,0)); // All entries are of the same type, so we can check the first one....
                    try{
                        myPatch.getDriver().getClass().getDeclaredMethod("editPatch", new Class[]{myPatch.getClass()});
                        PatchEdit.editAction.setEnabled(true);
                    }
                    catch(NoSuchMethodException ex) {
                        if (myPatch.getDriver() instanceof BankDriver)
                            PatchEdit.editAction.setEnabled(true);
                    };
                    PatchEdit.cutAction.setEnabled (true);
                    PatchEdit.copyAction.setEnabled (true);
                    PatchEdit.deleteAction.setEnabled (true);
                    PatchEdit.exportAction.setEnabled (true);
                }
                
                //System.out.println ("Frame activated"+table.getSelectedRowCount ());
            }
            public void internalFrameClosing (InternalFrameEvent e)
            {
              JInternalFrame[] jList =PatchEdit.desktop.getAllFrames ();
	       for (int j=0;j<jList.length;j++)
		    if (jList[j] instanceof PatchEditorFrame) 
		     {
    		             if (((PatchEditorFrame)(jList[j])).bankFrame==instance)
			       { jList[j].moveToFront();
			          try{jList[j].setSelected(true);
			          jList[j].setClosed(true); }catch (Exception e1){}
			       }
		     }
            }
            
            public void internalFrameDeactivated (InternalFrameEvent e)
            {
                //PatchEdit.receiveAction.setEnabled (false);
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
                PatchEdit.newPatchAction.setEnabled (false);
                
                //System.out.println ("Frame deactivated");
            }
        }
        );
        
        //Create the scroll pane and add the table to it.
        JScrollPane scrollPane = new JScrollPane (table);
        
        //Add the scroll pane to this window.
        getContentPane ().add (scrollPane, BorderLayout.CENTER);
                
        //...Then set the window size or call pack...
        setSize (600,300);
        
        //Set the window's location.
        
    }
    
    boolean checkSelected ()
    {
        if ((table.getSelectedRowCount ()==0) || (table.getSelectedColumnCount ()==0))
        {ErrorMsg.reportError ("Error","No patch is selected"); return false;}
        return true;
    }
    public int getSelectedPatchNum ()
    {
        table=table2;
        return table.getSelectedColumn ()*bankDriver.numPatches/bankDriver.numColumns+table.getSelectedRow ();
    }
    public Patch getSelectedPatch ()
    {
        return bankDriver.getPatch (bankData,getSelectedPatchNum ());
    }
    public void ImportPatch (File file) throws IOException,FileNotFoundException
    {
        if (!checkSelected ()) return;
        FileInputStream fileIn= new FileInputStream (file);
        byte [] buffer =new byte [(int)file.length ()];
        fileIn.read (buffer);
        fileIn.close ();
        Patch p = new Patch (buffer);
        bankDriver.putPatch (bankData,p,getSelectedPatchNum ());
        myModel.fireTableDataChanged ();
    }

    public void ExportPatch (File file) throws IOException,FileNotFoundException
    {
        if (!checkSelected ()) return;
        Patch p=getSelectedPatch ();
        FileOutputStream fileOut= new FileOutputStream (file);
        fileOut.write (p.sysex);
        fileOut.close ();
    }

    public void DeleteSelectedPatch ()
    {
        if (!checkSelected ()) return;
        bankDriver.deletePatch (bankData,getSelectedPatchNum ());
        myModel.fireTableDataChanged ();
    }

    public void CopySelectedPatch ()
    {
        if (!checkSelected ()) return;
        Patch p=getSelectedPatch ();
        if (p==null)
        {ErrorMsg.reportError ("Error","That patch is blank.");return;}
        byte [] mySysex = new byte[p.sysex.length];
        System.arraycopy (p.sysex,0,mySysex,0,p.sysex.length);
        PatchEdit.Clipboard=new Patch (mySysex,
        (p.date.toString ()),
        (p.author.toString ()),
        (p.comment.toString ()));
    }

    public void SendSelectedPatch ()
    {
        if (!checkSelected ()) return;
        Patch p=getSelectedPatch ();
        if (p==null)
        {ErrorMsg.reportError ("Error","That patch is blank.");return;}
        PatchEdit.getDriver (p.deviceNum,p.driverNum).sendPatch (p);
    }

    public void PlaySelectedPatch ()
    {
        if (!checkSelected ()) return;
        Patch p=getSelectedPatch ();
        if (p==null)
        {ErrorMsg.reportError ("Error","That patch is blank.");return;}
        PatchEdit.getDriver (p.deviceNum,p.driverNum).sendPatch (p);        
	PatchEdit.getDriver (p.deviceNum,p.driverNum).playPatch (p);
    }

    public void StoreSelectedPatch ()
    {
        if (!checkSelected ()) return;
        Patch p=getSelectedPatch ();
        if (p==null)
        {ErrorMsg.reportError ("Error","That patch is blank.");return;}
        PatchEdit.getDriver(p.deviceNum,p.driverNum).choosePatch(p, table.getSelectedColumn()*bankDriver.numPatches/bankDriver.numColumns+table.getSelectedRow()); // phil@muqus.com    
}

    public JInternalFrame EditSelectedPatch ()
    {
        if (!checkSelected ()) return null;
        Patch p=getSelectedPatch ();
        if (p==null)
        {ErrorMsg.reportError ("Error","That patch is blank.");return null;}
        PatchEditorFrame pf= (PatchEditorFrame)(PatchEdit.getDriver (p.deviceNum,p.driverNum).editPatch (p));
        pf.setBankEditorInformation (this,table.getSelectedRow (),table.getSelectedColumn ());
        return pf;
    }

    public void PastePatch ()
    {
        if (!checkSelected ()) return ;
        bankDriver.putPatch (bankData,PatchEdit.Clipboard,getSelectedPatchNum ());
        myModel.fireTableDataChanged ();
    }

    public ArrayList getPatchCollection ()
    {
        return null;   //for now bank doesn't support this feature. Need to extract single and place in collection.
    }

  public void revalidateDriver()
  {
  bankData.ChooseDriver();
  if (bankData.deviceNum==0) {try{setClosed(true);}catch (Exception e){}; return;}
  bankDriver=(BankDriver)PatchEdit.getDriver(bankData.deviceNum,bankData.driverNum);}    
}
