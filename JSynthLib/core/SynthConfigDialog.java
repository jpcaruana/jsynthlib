package core;

import java.awt.*;
import java.awt.event.*;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ProgressMonitor;
import javax.swing.table.TableColumn;
////TODO import org.jsynthlib.midi.*;

public class SynthConfigDialog extends JDialog {
    private JTable table;
    private JTable table2;
    private MidiScan midiScan;

    public SynthConfigDialog (JFrame parent) {
        super(parent, "Synthesizer Configuration", true);
        JPanel container = new JPanel ();
        container.setLayout (new BorderLayout ());

        SynthTableModel dataModel = new SynthTableModel ();
        JTable table = new JTable (dataModel);
        table2 = table;
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
        try {
	    JComboBox comboBox2 = new JComboBox ();
	    for (int j = 0; j < PatchEdit.MidiOut.getNumInputDevices (); j++)
		comboBox2.addItem (j + ": " + PatchEdit.MidiOut.getInputDeviceName (j));
	    column.setCellEditor (new DefaultCellEditor (comboBox2));
	} catch (Exception e) {
	}

        column = table.getColumnModel ().getColumn (3);
        column.setPreferredWidth (150);

        try {
	    JComboBox comboBox = new JComboBox ();
	    for (int j = 0; j < PatchEdit.MidiOut.getNumOutputDevices (); j++)
		comboBox.addItem (j + ": " + PatchEdit.MidiOut.getOutputDeviceName (j));
	    column.setCellEditor (new DefaultCellEditor (comboBox));
	} catch (Exception e) {
	}
        column = table.getColumnModel ().getColumn (4);
        column.setPreferredWidth (75);

        JPanel buttonPanel = new JPanel ();
        buttonPanel.setLayout (new FlowLayout (FlowLayout.CENTER));
        // BUTTON ADDED BY GERRIT GEHNEN
        JButton scan = new JButton ("Auto-Scan");
        scan.addActionListener (new ActionListener () {
		public void actionPerformed (ActionEvent e) {
		    scanPressed ();
		}
	    });

	// I changed this around a little bit. First, we're no longer comparing against
	// individual midi wrappers (like JavaMidiWrapper). Second, the scan button is
	// *always* there... it's just either enabled or disabled. Having GUI items appear
	// and disappear is probably not a good idea, because a user might go back to where
	// they *think* they saw a button once, and not find it... leading to general
	// confusion regarding the interface. - emenaker 2003.03.13
	buttonPanel.add (scan);
	scan.setEnabled(PatchEdit.MidiOut.supportsScanning());
        // END OF ADDED BUTTON

        JButton add = new JButton ("Add Device");
        add.addActionListener (new ActionListener () {
		public void actionPerformed (ActionEvent e) {
		    addPressed ();
		}
	    });
        buttonPanel.add (add);

        JButton rem = new JButton ("Remove Device");
        rem.addActionListener (new ActionListener () {
		public void actionPerformed (ActionEvent e) {
		    removePressed ();
		}
	    });
        buttonPanel.add (rem);
        JButton detail = new JButton ("Show Details");
        detail.addActionListener (new ActionListener () {
		public void actionPerformed (ActionEvent e) {
		    detailPressed ();
		}
	    });
        buttonPanel.add (detail);

        JButton ok = new JButton ("Close");
        ok.addActionListener (new ActionListener () {
		public void actionPerformed (ActionEvent e) {
		    OKPressed ();
		}
	    });
        buttonPanel.add (ok);

        getRootPane ().setDefaultButton (ok);

        container.add (buttonPanel, BorderLayout.SOUTH);
        getContentPane ().add (container);
        pack ();
        centerDialog ();
    }

    private void centerDialog () {
        Dimension screenSize = this.getToolkit ().getScreenSize ();
        Dimension size = this.getSize ();
        screenSize.height = screenSize.height / 2;
        screenSize.width = screenSize.width / 2;
        size.height = size.height / 2;
        size.width = size.width / 2;
        int y = screenSize.height - size.height;
        int x = screenSize.width - size.width;
        this.setLocation (x, y);
    }

    private void OKPressed () {
	this.setVisible (false);
    }

    private void removePressed () {
        table = table2;
        if ((table2.getSelectedRow () == -1) || (table2.getSelectedRow() == 0))
	    return;
        if (JOptionPane.showConfirmDialog (null,
					   "Are you sure?",
					   "Remove Device?",
					   JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
	    return;
	try {
	    PatchEdit.appConfig.removeDevice(table2.getSelectedRow ());
	    PatchEdit.appConfig.reassignDeviceDriverNums();
	    revalidateLibraries();
	    ((SynthTableModel) table.getModel ()).fireTableDataChanged ();
	    table2.repaint ();
	} catch (Exception e) {
	}
    }

    private void detailPressed () {
        table = table2;
        if ((table2.getSelectedRow () == -1))
	    return;
        ((Device) (PatchEdit.appConfig.getDevice(table2.getSelectedRow()))).showDetails();
        ((SynthTableModel) table.getModel ()).fireTableDataChanged ();
    }

    private void addPressed () {
        table = table2;
	DeviceAddDialog dad = new DeviceAddDialog (null);
        dad.show ();
	//	PatchEdit.appConfig.reassignDeviceDriverNums();
        revalidateLibraries();
	((SynthTableModel) table.getModel ()).fireTableDataChanged ();
    }

    private void revalidateLibraries() {
	JSLFrame[] jList = JSLDesktop.getAllFrames ();
	if (jList.length > 0) {
	    PatchEdit.waitDialog.show();
	    for (int i = 0; i < jList.length; i++) {
		if (jList[i] instanceof LibraryFrame)
		    ((LibraryFrame) (jList[i])).revalidateDrivers();
		else if (jList[i] instanceof BankEditorFrame)
		    ((BankEditorFrame) (jList[i])).revalidateDriver();
		else if (jList[i] instanceof PatchEditorFrame)
		    ((PatchEditorFrame) (jList[i])).revalidateDriver();
	    }
	    PatchEdit.waitDialog.hide();
	}
    }

    // METHOD ADDED BY GERRIT GEHNEN
    private void scanPressed () {
        table = table2;

        if (JOptionPane.showConfirmDialog (null,
					   "Scanning the System for supported Synthesizers may take\n"
					   + "a few minutes if you have many Midi devices. During the scan\n"
					   + "it is normal for the system to be unresponsive.\n"
					   + "Do you wish to scan?",
					   "Scan for Synthesizers",
					   JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
	    return;

        if (midiScan != null) {
	    midiScan.close ();
	}

        ProgressMonitor pm = new ProgressMonitor (null,
						  "Scanning for SupportedSynthesizers",
						  "Initializing Midi Devices", 0, 100);
        midiScan = new MidiScan ((SynthTableModel) table.getModel (), pm, this);

        midiScan.start ();
	//PatchEdit.appConfig.reassignDeviceDriverNums();
        revalidateLibraries();
        ((SynthTableModel) table.getModel ()).fireTableDataChanged ();
    }
    // END OF METHOD ADDED BY GERRIT GEHNEN
}
