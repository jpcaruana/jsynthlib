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
public class SortDialog extends JDialog
{

    public SortDialog (JFrame Parent)
    {

        super(Parent,"Library Sort",true);

        JPanel container= new JPanel ();
        container.setLayout (new BorderLayout ());

        JLabel myLabel=new JLabel ("Please select a Field to Sort the Library by.",JLabel.CENTER);
        try
        {
            container.add (myLabel, BorderLayout.NORTH);
            final ButtonGroup group=new ButtonGroup ();
            JRadioButton button1 = new JRadioButton ("Patch Name");
            button1.setActionCommand ("P");
            JRadioButton button2 = new JRadioButton ("Field 1");
            JRadioButton button4 = new JRadioButton ("Synth Name");
            button4.setActionCommand ("S");
            JRadioButton button5 = new JRadioButton ("Patch Type");
            button5.setActionCommand ("T");
            button2.setActionCommand ("1");
            JRadioButton button3 = new JRadioButton ("Field 2");
            button3.setActionCommand ("2");
            group.add (button1);
            group.add (button4);
            group.add (button5);
            group.add (button2);
            group.add (button3);
            button1.setSelected (true);
            JPanel radioPanel=new JPanel ();
            radioPanel.setLayout (new ColumnLayout ());
            radioPanel.add (button1);
            radioPanel.add (button4);
            radioPanel.add (button5);
            radioPanel.add (button2);
            radioPanel.add (button3);
            container.add (radioPanel,BorderLayout.CENTER);
            JPanel buttonPanel = new JPanel ();
            buttonPanel.setLayout ( new FlowLayout () );
            JButton done = new JButton (" OK ");
            done.addActionListener (new ActionListener ()
            {
                public void actionPerformed (ActionEvent e)
                {
                    setVisible (false);
                    PatchEdit.waitDialog.show ();
                    String command = group.getSelection ().getActionCommand ();
                    Collections.sort (((LibraryFrame)PatchEdit.desktop.getSelectedFrame ()).myModel.PatchList,new myOrder (command));
                    ((LibraryFrame)PatchEdit.desktop.getSelectedFrame ()).myModel.fireTableDataChanged ();
                    PatchEdit.waitDialog.hide ();
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

                    container.add (buttonPanel, BorderLayout.SOUTH);
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
                s1=((Patch) a1).getDriver().getPatchName((Patch) a1).toLowerCase();
                s2=((Patch) a2).getDriver().getPatchName((Patch) a2).toLowerCase();
            }
            else if (field==1)
            {
                s1=((Patch) a1).date.toString().toLowerCase();
                s2=((Patch) a2).date.toString().toLowerCase();
            }
            else if (field==2)
            {
                s1=((Patch) a1).author.toString().toLowerCase();
                s2=((Patch) a2).author.toString().toLowerCase();
            }
            else if (field==3)
            {
		s1=((Patch) a1).getDevice().getSynthName();
		s2=((Patch) a2).getDevice().getSynthName();
            }
            else
            {
                s1=((Patch) a1).getDriver().getPatchType();
                s2=((Patch) a2).getDriver().getPatchType();
            }
            return s1.compareTo(s2);
        }
    }
}


