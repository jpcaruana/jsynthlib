package synthdrivers.YamahaFS1R;

import java.awt.Dimension;

import javax.swing.JTable;

import core.BankEditorFrame;
import core.JSLFrame;
import core.Patch;
import core.PatchBasket;
import core.PatchEditorFrame;

/**
	Specific bank editor for YamahaFS1R. This bank holds 128 voices + 128 performances.
	@author denis queffeulou mailto:dqueffeulou@free.fr
*/
public class YamahaFS1RBankEditor extends BankEditorFrame implements PatchBasket
{ 
    {
	preferredScrollableViewportSize = new Dimension(100, 100);
	autoResizeMode = JTable.AUTO_RESIZE_OFF;
	preferredColumnWidth = 130;
    }

    public YamahaFS1RBankEditor (Patch p)
    {
		super(p);
    }
    
    /*
    protected void InitBankEditorFrame ()
    {
        //...Create the GUI and put it in the window...
        
        myModel = new PatchGridModel (bankData,bankDriver);
        table = new DNDPatchTable (myModel);
        table2=table;
        table.setPreferredScrollableViewportSize (new Dimension (100, 100));
        table.setRowSelectionAllowed (false);
        table.setColumnSelectionAllowed (false);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
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
                PatchEdit.sendToAction.setEnabled (false);	// not available yet!
                PatchEdit.reassignAction.setEnabled (false);	// not available yet!
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
        
        this.addJSLFrameListener (new JSLFrameListener ()
        {
            public void JSLFrameOpened (JSLFrameEvent e)
            {}
            public void JSLFrameClosed (JSLFrameEvent e)
            {}
            public void JSLFrameDeiconified (JSLFrameEvent e)
            {}
            public void JSLFrameIconified (JSLFrameEvent e)
            {}
            public void JSLFrameActivated (JSLFrameEvent e)
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
                    PatchEdit.sendToAction.setEnabled (false);	// not available yet!
                    PatchEdit.reassignAction.setEnabled (false);// not available yet!
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
            public void JSLFrameClosing (JSLFrameEvent e)
            {
              JSLFrame[] jList =PatchEdit.desktop.getAllFrames ();
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
            
            public void JSLFrameDeactivated (JSLFrameEvent e)
            {
                //PatchEdit.receiveAction.setEnabled (false);
                PatchEdit.extractAction.setEnabled (false);
                PatchEdit.sendAction.setEnabled (false);
                PatchEdit.sendToAction.setEnabled (false);
                PatchEdit.reassignAction.setEnabled (false);
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
                
		for (int col = 0; col < table.getColumnCount(); col++)
		{
			TableColumn column = table.getColumnModel().getColumn(col);
			column.setPreferredWidth(130);
		}

        //...Then set the window size or call pack...
        setSize (600,300);
        
        //Set the window's location.
    }
    */

	/**
		Edit a patch without select it. This allow the performance to edit
		a patch from the bank.
		@param aPart performance part number 1..4
	*/
    public JSLFrame EditPatch (int aNumPatch, int aPart)
    {
        Patch p = bankDriver.getPatch (bankData, aNumPatch);
        if (p==null) {
			return null;
		}
        PatchEditorFrame pf= (PatchEditorFrame)(YamahaFS1RVoiceDriver.getInstance().editPatch(p, aPart, aNumPatch-128));
        //pf.setBankEditorInformation (this,table.getSelectedRow (),table.getSelectedColumn ());
		pf.setBankEditorInformation(this, aNumPatch % YamahaFS1RBankDriver.NB_ROWS, aNumPatch/YamahaFS1RBankDriver.NB_ROWS);
        return pf;
    }

	public Patch getBankPatch() {
		return bankData;
	}

}
