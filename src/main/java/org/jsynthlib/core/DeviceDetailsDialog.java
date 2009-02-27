package org.jsynthlib.core;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.table.*;

public class DeviceDetailsDialog extends JDialog {
    private static final int DRIVER_NAME = 0;
    private static final int TYPE	 = 1;
    private static final int AUTHORS	 = 2;

    //private JTable table;
    //private JTable table2;
    private Device device;

    public DeviceDetailsDialog (Frame owner, Device d) {
        super(owner,"Device Details",true);
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
        ok.addActionListener (new ActionListener () {
            public void actionPerformed (ActionEvent e) {
                OKPressed ();
            }
        });
        buttonPanel1.add ( ok );
        infoPane.add (buttonPanel1, BorderLayout.SOUTH);
	container.add(new JLabel("This device contains the following installed drivers:"),BorderLayout.NORTH);


	DeviceDetailsTableModel dataModel = new DeviceDetailsTableModel (device);
        JTable table = new JTable (dataModel);
        //table2=table;
        table.setPreferredScrollableViewportSize (new Dimension (500, 250));
        JScrollPane scrollpane = new JScrollPane (table);
        container.add (scrollpane, BorderLayout.CENTER);

        TableColumn column = null;
        column = table.getColumnModel ().getColumn (DRIVER_NAME);
        column.setPreferredWidth (150);
        column = table.getColumnModel ().getColumn (TYPE);
        column.setPreferredWidth (25);
        column = table.getColumnModel ().getColumn (AUTHORS);
        column.setPreferredWidth (100);

        JPanel buttonPanel = new JPanel ();
        buttonPanel.setLayout ( new FlowLayout (FlowLayout.CENTER) );

//        JButton rem = new JButton ("Remove Driver");
//        rem.addActionListener (new ActionListener () {
//            public void actionPerformed (ActionEvent e) {
//                RemovePressed ();
//            }
//        });
//        buttonPanel.add ( rem );
        JButton ok3 = new JButton ("Close");
        ok3.addActionListener (new ActionListener () {
            public void actionPerformed (ActionEvent e) {
                OKPressed ();
            }
        });
        buttonPanel.add ( ok3 );

        JPanel buttonPane2 = new JPanel ();
	JButton ok2 = new JButton ("Close");
        ok2.addActionListener (new ActionListener () {
            public void actionPerformed (ActionEvent e) {
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
        Utility.centerDialog(this);
    }

    private void OKPressed () {
	this.setVisible (false);
    }

    private class DeviceDetailsTableModel extends AbstractTableModel {
	private final String[] columnNames = {
		"Driver Name",
		"Type",
		"Authors",
	    };
	private Device d;

	public DeviceDetailsTableModel (Device de) {
	    d = de;
	}
	public int getColumnCount () {
	    return columnNames.length;
	}
	public String getColumnName (int col) {
	    return columnNames[col];
	}
	public int getRowCount () {
	    return d.driverCount();
	}
	public Class getColumnClass (int c) {
	    return getValueAt (0, c).getClass ();
	}
	public Object getValueAt (int row, int col) {
	    IDriver myDriver = d.getDriver(row);

	    if (col == DRIVER_NAME) {
		return (d.getManufacturerName() + " " + d.getModelName() + " "
			+ myDriver.getPatchType());
	    } else if (col == TYPE) {
		if (myDriver.isConverter())
		    return "Converter";
		else
		    return "Driver";
	    } else {		//  AUTHORS
		return myDriver.getAuthors();
	    }
	}

	public boolean isCellEditable (int row, int col) {
	    //Note that the data/cell address is constant,
	    //no matter where the cell appears onscreen.
	    return false;
	}
    }
}

