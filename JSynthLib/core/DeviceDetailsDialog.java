package core;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
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
public class DeviceDetailsDialog extends JDialog
{
    JTable table;
    JTable table2;
    Device device;
    public DeviceDetailsDialog (Device d)
    {
        super(PatchEdit.getInstance(),"Device Details",true);
        device=d;
	
	JPanel container= new JPanel ();
        container.setLayout (new BorderLayout ());
        JPanel infoPane=new JPanel();
	infoPane.setLayout (new BorderLayout());
	infoPane.add(new JLabel("Device Name:  "+device.getManufacturerName()+" "+device.getModelName()),BorderLayout.NORTH);	
	JTextArea jta=new JTextArea(null,10,50);
	jta.append(device.getInfoText());
	jta.setLineWrap(true);
	jta.setEditable(false);
	jta.setWrapStyleWord(true);
        jta.setCaretPosition(0);
        
        JScrollPane jasp = new JScrollPane(jta);
        infoPane.add(jasp,BorderLayout.CENTER);
        JPanel buttonPanel1 = new JPanel ();
        buttonPanel1.setLayout ( new FlowLayout (FlowLayout.CENTER) );      
        JButton ok = new JButton ("Close");
        ok.addActionListener (new ActionListener ()
        {
            public void actionPerformed (ActionEvent e)
            {
                OKPressed ();
            }
        });
        buttonPanel1.add ( ok );          
        infoPane.add (buttonPanel1, BorderLayout.SOUTH);	
	container.add(new JLabel("This device contains the following installed drivers:"),BorderLayout.NORTH);
	
	
	DeviceDetailsTableModel dataModel = new DeviceDetailsTableModel (device);
        JTable table = new JTable (dataModel);
        table2=table;
        table.setPreferredScrollableViewportSize (new Dimension (500, 250));
        JScrollPane scrollpane = new JScrollPane (table);
        container.add (scrollpane, BorderLayout.CENTER);
        
        TableColumn column = null;
        column = table.getColumnModel ().getColumn (0);
        column.setPreferredWidth (150);
        column = table.getColumnModel ().getColumn (1);
        column.setPreferredWidth (25);
        column = table.getColumnModel ().getColumn (2);
        column.setPreferredWidth (100);
        
        JPanel buttonPanel = new JPanel ();
        buttonPanel.setLayout ( new FlowLayout (FlowLayout.CENTER) );
        
        JButton rem = new JButton ("Remove Driver");
        rem.addActionListener (new ActionListener ()
        {
            public void actionPerformed (ActionEvent e)
            {
                RemovePressed ();
            }
        });
        buttonPanel.add ( rem );    
        JButton ok3 = new JButton ("Close");
        ok3.addActionListener (new ActionListener ()
        {
            public void actionPerformed (ActionEvent e)
            {
                OKPressed ();
            }
        });
        buttonPanel.add ( ok3 );          
        
        JPanel buttonPane2 = new JPanel ();
	JButton ok2 = new JButton ("Close");
        ok2.addActionListener (new ActionListener ()
        {
            public void actionPerformed (ActionEvent e)
            {
                OKPressed ();
            }
        });
        buttonPane2.add ( ok2);          
	
        
        getRootPane ().setDefaultButton (ok);       
        container.add (buttonPanel, BorderLayout.SOUTH);
        JPanel configPane=new JPanel();
        configPane.setLayout (new BorderLayout ());
        configPane.add(device.config(),BorderLayout.NORTH);
	configPane.add(buttonPane2,BorderLayout.SOUTH);
        JTabbedPane jtp=new JTabbedPane ();
        jtp.addTab ("Information",infoPane);
        jtp.addTab ("Configuration",configPane);
        jtp.addTab ("Loaded Drivers",container);
        getContentPane ().add (jtp);
        pack ();
        centerDialog ();
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
    
    void OKPressed ()
    {this.setVisible (false);}
    void RemovePressed ()
    {
        table=table2;        
        if ((table2.getSelectedRow ()==-1)) return;
        if (JOptionPane.showConfirmDialog (null,"Do you really want to do this? Are you sure?","Remove Driver?",JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) return;
        try
        {
            device.driverList.remove (table2.getSelectedRow ());
	   ((DeviceDetailsTableModel)table.getModel ()).fireTableDataChanged ();
            table2.repaint ();
	    revalidateLibraries();
        }catch (Exception e)
        {}
    }
    void revalidateLibraries()
    {
	JSLFrame[] jList =JSLDesktop.getAllFrames ();
	if (jList.length >0)
	{
	  PatchEdit.waitDialog.show();
	  for (int i=0;i<jList.length;i++)
	  {
	    if (jList[i] instanceof LibraryFrame) ((LibraryFrame)(jList[i])).revalidateDrivers();
	    if (jList[i] instanceof BankEditorFrame) ((BankEditorFrame)(jList[i])).revalidateDriver();
	    if (jList[i] instanceof PatchEditorFrame) ((PatchEditorFrame)(jList[i])).revalidateDriver();
	    
	  }
	  PatchEdit.waitDialog.hide();
	}
    }

}

