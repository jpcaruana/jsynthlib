package core;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.JLabel;
import javax.swing.table.TableColumn;

import javax.swing.JScrollPane;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;
import javax.swing.JDialog;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.metal.*;

import java.beans.*;
import java.util.*;
import java.awt.event.*;
import java.awt.*;
import java.io.*;
public class ImportAllDialog extends JDialog
{
    public ImportModel myModel;
    public ImportAllDialog (JFrame Parent,final File file)
    {
        
        super(Parent,"Import All Files In Directory",true);
        
        JPanel container= new JPanel ();
        container.setLayout (new ColumnLayout ());
        
        try
        {
            myModel = new ImportModel ();
            JTable table = new JTable (myModel);
            TableColumn column = null;
            column = table.getColumnModel ().getColumn (0);
            column.setPreferredWidth (25);
            column = table.getColumnModel ().getColumn (1);
            column.setPreferredWidth (250);
            table.setPreferredScrollableViewportSize (new Dimension (500, 250));
            JScrollPane scrollPane = new JScrollPane (table);
            container.add (scrollPane);
            
            final ButtonGroup group=new ButtonGroup ();
            JRadioButton button1 = new JRadioButton ("Nowhere");
            button1.setActionCommand ("0");
            JRadioButton button2 = new JRadioButton ("in Field 1");
            JRadioButton button3 = new JRadioButton ("in Field 2");
            button2.setActionCommand ("1");
            button3.setActionCommand ("2");
            group.add (button1);
            group.add (button2);
            group.add (button3);
            button1.setSelected (true);
            JPanel radioPanel=new JPanel ();
            JLabel myLabel=new JLabel ("Place the File name for each Patch:          ",JLabel.CENTER);
            radioPanel.setLayout (new FlowLayout ());
            radioPanel.add (myLabel, BorderLayout.NORTH);
            radioPanel.add (button1);
            radioPanel.add (button2);
            radioPanel.add (button3);
            container.add (radioPanel);
            
            final ButtonGroup group2=new ButtonGroup ();
            JRadioButton button4 = new JRadioButton ("No");
            button4.setActionCommand ("0");
            JRadioButton button5 = new JRadioButton ("Yes");
            
            button5.setActionCommand ("1");
            group2.add (button4);
            group2.add (button5);
            
            button4.setSelected (true);
            JPanel radioPanel2=new JPanel ();
            JLabel myLabel2=new JLabel ("Automatically Extract Patches from Banks?   ",JLabel.CENTER);
            radioPanel2.setLayout (new FlowLayout ());
            radioPanel2.add (myLabel2, BorderLayout.NORTH);
            radioPanel2.add (button4);
            radioPanel2.add (button5);
            
            container.add (radioPanel2);
            
            JPanel buttonPanel = new JPanel ();
            buttonPanel.setLayout ( new FlowLayout ());
            JButton done = new JButton (" OK ");
            done.addActionListener (new ActionListener ()
            {
                public void actionPerformed (ActionEvent e)
                {
                    setVisible (false);
                    String command1 = group.getSelection ().getActionCommand ();
                    String command2 = group2.getSelection ().getActionCommand ();
                    boolean extract = (command2=="1");
                    int putName=0;
                    if (command1=="1") putName=1;
                    if (command1=="2") putName=2;
                    doImport (putName,extract,file);
                }});
                buttonPanel.add ( done );
                
                JButton cancel = new JButton ("Cancel");
                cancel.addActionListener (new ActionListener ()
                {
                    public void actionPerformed (ActionEvent e)
                    {
                        setVisible (false);
                    }});
                    buttonPanel.add ( cancel );
                    
                    getRootPane ().setDefaultButton (done);
                    
                    container.add (buttonPanel,BorderLayout.SOUTH);
                    getContentPane ().add (container);
                    pack ();
                    centerDialog ();
        }catch(Exception e)
        {ErrorMsg.reportStatus (e);}
    }
    
    protected void centerDialog ()
    {
        Dimension screenSize = this.getToolkit ().getScreenSize ();
        Dimension size = this.getSize ();
        screenSize.height = screenSize.height/2;
        screenSize.width = screenSize.width/2;
        size.height = size.height/2;
        size.width = size.width/2;
        int y = screenSize.height - size.height;
        int x = screenSize.width - size.width;
        this.setLocation (x,y);
    }
    
    public void doImport (int putName, boolean extract,File directory)
    {
        File []files = directory.listFiles ();
        
        try
        {
            for (int i=0;i<files.length;i++)
            {
                FileInputStream fileIn=null;
                byte [] buffer =new byte [(int)files[i].length ()];
                try
                {
                    fileIn= new FileInputStream (files[i]);
                    fileIn.read (buffer);
                    fileIn.close ();
                }
                catch (Exception e)
                {
                    buffer=new byte[1];
                }
                if (buffer.length>16)
                {
                    Patch p = new Patch (buffer);
                    // NEW CODE
                    Patch[] patarray=p.dissect ();
                    
                    for (int k=0;k<patarray.length;k++) // Loop over all found sub-patches
                    {
                        if (putName==1) patarray[k].date.append (files[i].getName ());
                        if (putName==2) patarray[k].author.append (files[i].getName ());
                        if (myModel.includeDevice[patarray[k].deviceNum].booleanValue ())
                        {
                            if (extract && (PatchEdit.getDriver(patarray[k].deviceNum,patarray[k].driverNum) instanceof BankDriver))
                            {
                                BankDriver myDriver=((BankDriver)PatchEdit.getDriver(patarray[k].deviceNum,patarray[k].driverNum));
                                for (int j=0;j<myDriver.numPatches;j++)
                                {
                                    Patch q=(myDriver.getPatch (patarray[k],j));
                                    if (putName==1)
                                        q.date.append (files[i].getName ()+" "+myDriver.patchNumbers[j]);
                                    if (putName==2)
                                        q.author.append (files[i].getName ()+" "+myDriver.patchNumbers[j]);
                                    PatchEdit.Clipboard=q;
                                    ((LibraryFrame)PatchEdit.desktop.getSelectedFrame ()).PastePatch ();
                                }
                            }
                            else
                            {
                                PatchEdit.Clipboard=patarray[k];
                                ((LibraryFrame)PatchEdit.desktop.getSelectedFrame ()).PastePatch ();
                            }
                        }
                    }
                }
            }
        } catch (Exception e)
        {e.printStackTrace ();
         ErrorMsg.reportError ("Error", "Unable to Import Patches",e);return;}
    }
    
    static class myOrder implements Comparator
    {
        int field=0;
        public myOrder (String s)
        {
            if (s.equals ("P")) field=0;
            if (s.equals ("1")) field=1;
            if (s.equals ("2")) field=2;
            if (s.equals ("S")) field=3;
            if (s.equals ("T")) field=4;
        }
        public int compare (Object a1,Object a2)
        {
            String s1; String s2;
            if (field==0)
            {
                s1=PatchEdit.getDriver (((Patch)a1).deviceNum,((Patch)a1).driverNum).getPatchName ((Patch)a1);
                s2=PatchEdit.getDriver (((Patch)a2).deviceNum,((Patch)a2).driverNum).getPatchName ((Patch)a2);
            }
            else if (field==1)
            {
                s1=((Patch)a1).date.toString ().toLowerCase ();
                s2=((Patch)a2).date.toString ().toLowerCase ();
            }
            else if (field==2)
            {
                s1=((Patch)a1).author.toString ().toLowerCase ();
                s2=((Patch)a2).author.toString ().toLowerCase ();
            }
            else if (field==3)
            {
                s1=((Device)(PatchEdit.deviceList.get (((Patch)a1).deviceNum))).getSynthName ();
                s2=((Device)(PatchEdit.deviceList.get (((Patch)a2).deviceNum))).getSynthName ();
            }
            else
            {
                s1=PatchEdit.getDriver (((Patch)a1).deviceNum,((Patch)a1).driverNum).getPatchType ();
                s2=PatchEdit.getDriver (((Patch)a1).deviceNum,((Patch)a1).driverNum).getPatchType ();
            }
            
            return s1.compareTo (s2);
        }
        
        public boolean equals (java.lang.Object obj)
        {
            return false;
        }
        
    }
    
    class ImportModel extends AbstractTableModel
    {
        final String[] columnNames =
        {"Include?",
         "Driver"};
         Boolean [] includeDevice = new Boolean[PatchEdit.deviceList.size ()];
         
         public ImportModel ()
         {super(); for (int i=0;i<includeDevice.length;i++) includeDevice[i]=new Boolean (true);}
         public int getColumnCount ()
         {
             return columnNames.length;
         }
         public int getRowCount ()
         {
             return PatchEdit.deviceList.size ();
         }
         
         public String getColumnName (int col)
         {
             return columnNames[col];
         }
         
         public Object getValueAt (int row, int col)
         {
             Device myDevice=(Device)PatchEdit.deviceList.get (row);
             if (col==1) return myDevice.getManufacturerName ()+" "+myDevice.getModelName ()/*+" "+myDriver.getPatchType ()*/;
             else return includeDevice[row];
         }
         
        /*
         * JTable uses this method to determine the default renderer/
         * editor for each cell.  If we didn't implement this method,
         * then the last column would contain text ("true"/"false"),
         * rather than a check box.
         */
         public Class getColumnClass (int c)
         {
             return getValueAt (0, c).getClass ();
         }
         
        /*
         * Don't need to implement this method unless your table's
         * editable.
         */
         public boolean isCellEditable (int row, int col)
         {
             //Note that the data/cell address is constant,
             //no matter where the cell appears onscreen.
             if (col==0) return true; else return false;
         }
         
        /*
         * Don't need to implement this method unless your table's
         * data can change.
         */
         public void setValueAt (Object value, int row, int col)
         {
             includeDevice[row]=(Boolean)value;
             fireTableCellUpdated (row, col);
         }
    }
    
}


