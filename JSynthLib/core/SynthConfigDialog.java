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
//TODO import org.jsynthlib.midi.*;
public class SynthConfigDialog extends JDialog
{
    JTable table;
    JTable table2;
    MidiScan midiScan;
    public SynthConfigDialog (JFrame Parent)
    {
        super(Parent,"Synthesizer Configuration",true);
        JPanel container= new JPanel ();
        container.setLayout (new BorderLayout ());
        
        SynthTableModel dataModel = new SynthTableModel ();
        JTable table = new JTable (dataModel);
        table2=table;
        table.setPreferredScrollableViewportSize (new Dimension (650, 150));
        JScrollPane scrollpane = new JScrollPane (table);
        container.add (scrollpane, BorderLayout.CENTER);
        
        TableColumn column = null;
        column = table.getColumnModel ().getColumn (0);
        column.setPreferredWidth (75);
        column = table.getColumnModel ().getColumn (1);
        column.setPreferredWidth (250);
        column = table.getColumnModel ().getColumn (2);
        column.setPreferredWidth (150);
        try
        {
            JComboBox comboBox2 = new JComboBox ();
            for (int j=0; j< PatchEdit.MidiOut.getNumInputDevices ();j++)
                comboBox2.addItem (j+": "+PatchEdit.MidiOut.getInputDeviceName (j));
            column.setCellEditor (new DefaultCellEditor (comboBox2));
        } catch (Exception e)
        {}
    
        column = table.getColumnModel ().getColumn (3);
        column.setPreferredWidth (150);

        try
        {
            JComboBox comboBox = new JComboBox ();
            for (int j=0; j< PatchEdit.MidiOut.getNumOutputDevices ();j++)
                comboBox.addItem (j+": "+PatchEdit.MidiOut.getOutputDeviceName (j));
            column.setCellEditor (new DefaultCellEditor (comboBox));
        } catch (Exception e)
        {}
        column = table.getColumnModel ().getColumn (4);
        column.setPreferredWidth (75);
        
        JPanel buttonPanel = new JPanel ();
        buttonPanel.setLayout ( new FlowLayout (FlowLayout.CENTER) );
        // BUTTON ADDED BY GERRIT GEHNEN
        JButton scan = new JButton ("Auto-Scan");
        scan.addActionListener (new ActionListener ()
        {
            public void actionPerformed (ActionEvent e)
            {
                ScanPressed ();
            }
        });
		// I changed this around a little bit. First, we're no longer comparing against
		// individual midi wrappers (like JavaMidiWrapper). Second, the scan button is
		// *always* there... it's just either enabled or disabled. Having GUI items appear
		// and disappear is probably not a good idea, because a user might go back to where
		// they *think* they saw a button once, and not find it... leading to general 
		// confusion regarding the interface. - emenaker 2003.03.13
		buttonPanel.add ( scan );
		scan.enable( PatchEdit.MidiOut.supportsScanning());
        // END OF ADDED BUTTON

        JButton add = new JButton ("Add Device");
        add.addActionListener (new ActionListener ()
        {
            public void actionPerformed (ActionEvent e)
            {
                AddPressed ();
            }
        });
        buttonPanel.add ( add );
        
        JButton rem = new JButton ("Remove Device");
        rem.addActionListener (new ActionListener ()
        {
            public void actionPerformed (ActionEvent e)
            {
                RemovePressed ();
            }
        });
        buttonPanel.add ( rem );
        JButton detail = new JButton ("Show Details");
        detail.addActionListener (new ActionListener ()
        {
            public void actionPerformed (ActionEvent e)
            {
                DetailPressed ();
            }
        });
        buttonPanel.add ( detail );
                
        JButton ok = new JButton ("Close");
        ok.addActionListener (new ActionListener ()
        {
            public void actionPerformed (ActionEvent e)
            {
                OKPressed ();
            }
        });
        buttonPanel.add ( ok );
        
        
        getRootPane ().setDefaultButton (ok);
        
        container.add (buttonPanel, BorderLayout.SOUTH);
        getContentPane ().add (container);
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
        if ((table2.getSelectedRow ()==-1) ||(table2.getSelectedRow()==0)) return;
        if (JOptionPane.showConfirmDialog (null,"Are you sure?","Remove Device?",JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) return;
	try
        {
            PatchEdit.appConfig.removeDevice(table2.getSelectedRow ());
            reassignDeviceDriverNums();
            revalidateLibraries();
	  ((SynthTableModel)table.getModel ()).fireTableDataChanged ();
            table2.repaint ();
        }catch (Exception e)
        {}
    }
    void DetailPressed ()
    {
        table=table2; 
        if ((table2.getSelectedRow ()==-1)) return;
        ((Device)(PatchEdit.appConfig.getDevice(table2.getSelectedRow()))).showDetails();  
        ((SynthTableModel)table.getModel ()).fireTableDataChanged ();  
    }
    
    void AddPressed ()
    {
        table=table2;
	DeviceAddDialog dad= new DeviceAddDialog (null);
        dad.show ();
        reassignDeviceDriverNums();
        revalidateLibraries();
	((SynthTableModel)table.getModel ()).fireTableDataChanged ();
        
    }
    void revalidateLibraries()
    {
	JInternalFrame[] jList =PatchEdit.desktop.getAllFrames ();
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

    /** Revalidate deviceNum element of drivers of each device */
    public void reassignDeviceDriverNums()
    {
      Device dev;

      for ( int i =0;i<PatchEdit.appConfig.deviceCount (); i++)
      {
        // Outer Loop, iterating over all installed devices
        dev=(Device)PatchEdit.appConfig.getDevice (i);

        for (int j=0;j<dev.driverList.size ();j++)
        {
          // Inner Loop, iterating over all Drivers of a device
          ((Driver)dev.driverList.get (j)).setDeviceNum(i);
          ((Driver)dev.driverList.get (j)).setDriverNum(j);
        }
      }
    }


    // METHOD ADDED BY GERRIT GEHNEN

    void ScanPressed ()
    {
        table=table2;
        
        if (JOptionPane.showConfirmDialog (null,"Scanning the System for supported Synthesizers may take\na few minutes if you have many Midi devices. During the scan\nit is normal for the system to be unresponsive.\nDo you wish to scan?","Scan for Synthesizers",JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) return;
        
        if (midiScan!=null)
        {
            midiScan.close ();
        }
        
        ProgressMonitor pm = new ProgressMonitor (null,
        "Scanning for SupportedSynthesizers",
        "Initializing Midi Devices",0,100);
        midiScan=new MidiScan ((SynthTableModel)table.getModel (),pm,this);
        
        midiScan.start ();
        reassignDeviceDriverNums();
        revalidateLibraries();
        ((SynthTableModel)table.getModel ()).fireTableDataChanged ();
    }
    // END OF METHOD ADDED BY GERRIT GEHNEN
}

