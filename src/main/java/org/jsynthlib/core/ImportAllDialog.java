package org.jsynthlib.core;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.util.Comparator;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

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
                    Utility.centerDialog(this);
        } catch(Exception e) {
	    ErrorMsg.reportStatus (e);
	}
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
                    IPatch[] patarray = DriverUtil.createPatches(buffer);
                    if(patarray == null) {
                        ErrorMsg.reportError("Import All",
                                "Can't import a file \""+files[i].getCanonicalPath()+"\". Load a proper synth driver.");
                        continue;
                    }

                    for (int k=0;k<patarray.length;k++) // Loop over all found sub-patches
                    {
                        IPatch pk = patarray[k];
                        if (putName==1) pk.setDate(pk.getDate() + files[i].getName());
                        if (putName==2) pk.setAuthor(pk.getAuthor() + files[i].getName());
                        if (myModel.includeDevice[pk.getDriver().getDevice().getDeviceNum()].booleanValue ())
                        {
                            LibraryFrame frame = (LibraryFrame) PatchEdit.getDesktop().getSelectedFrame();
                            if (extract && (pk.isBankPatch()))
                            {
                                String[] pn = pk.getDriver().getPatchNumbers();
                                for (int j=0; j<((IBankPatch) pk).getNumPatches(); j++)
                                {
                                    IPatch q = ((IBankPatch) pk).get(j);
                                    if (putName==1)
                                        q.setDate(q.getDate() + files[i].getName() + " " + pn[j]);
                                    if (putName==2)
                                        q.setAuthor(q.getAuthor() + files[i].getName() + " " + pn[j]);
                                    frame.pastePatch(q);
                                }
                            }
                            else
                            {
                                frame.pastePatch(pk);
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
                s1=((IPatch) a1).getName();
                s2=((IPatch) a2).getName();
            }
            else if (field==1)
            {
                s1=((IPatch) a1).getDate().toLowerCase();
                s2=((IPatch) a2).getDate().toLowerCase();
            }
            else if (field==2)
            {
                s1=((IPatch) a1).getAuthor().toLowerCase();
                s2=((IPatch) a2).getAuthor().toLowerCase();
            }
            else if (field==3)
            {
                s1=((IPatch) a1).getDevice().getSynthName();
                s2=((IPatch) a2).getDevice().getSynthName();
            }
            else
            {
                s1=((IPatch) a1).getType();
                s2=((IPatch) a2).getType();
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
         Boolean [] includeDevice = new Boolean[AppConfig.deviceCount ()];

         public ImportModel ()
         {super(); for (int i=0;i<includeDevice.length;i++) includeDevice[i]=new Boolean (true);}
         public int getColumnCount ()
         {
             return columnNames.length;
         }
         public int getRowCount ()
         {
             return AppConfig.deviceCount ();
         }

         public String getColumnName (int col)
         {
             return columnNames[col];
         }

         public Object getValueAt (int row, int col)
         {
             Device myDevice = AppConfig.getDevice(row);
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


