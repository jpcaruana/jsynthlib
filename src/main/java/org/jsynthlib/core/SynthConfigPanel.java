package org.jsynthlib.core;

import java.awt.*;
import java.awt.event.*;

import javax.sound.midi.*;
import javax.swing.*;
import javax.swing.table.*;

/**
 * ConfigPanel for Synthesizer Configuration
 * @author ???
 * @author Hiroo Hayashi
 * @version $Id$
 */
class SynthConfigPanel extends ConfigPanel {
    {
	panelName = "Synth Driver";
	nameSpace = "synthDriver";
    }

    /** Multiple MIDI Interface CheckBox */
    private JCheckBox cbxMMI;
    private boolean multiMIDI;
    private JTable table;
    private MidiScan midiScan;
    private JPopupMenu popup;

    private static final int SYNTH_NAME	    = 0;
    private static final int DEVICE	    = 1;
    private static final int MIDI_IN	    = 2;
    private static final int MIDI_OUT	    = 3;
    private static final int MIDI_CHANNEL   = 4;
    private static final int MIDI_DEVICE_ID = 5;

    SynthConfigPanel(PrefsDialog parent) {
        super(parent);

        setLayout(new BorderLayout());
	JPanel p = new JPanel(new GridBagLayout());
	GridBagConstraints c = new GridBagConstraints();
	c.gridx = 0; c.gridy = 0;

	// create synth driver table
        table = new JTable(new TableModel());
	table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setPreferredScrollableViewportSize(new Dimension(750, 150)); // wirski@op.pl
        TableColumn column;
        column = table.getColumnModel().getColumn(SYNTH_NAME);
        column.setPreferredWidth(75);
        column = table.getColumnModel().getColumn(DEVICE);
        column.setPreferredWidth(250);

	JComboBox comboBox;
        column = table.getColumnModel().getColumn(MIDI_IN);
        column.setPreferredWidth(200); // wirski@op.pl
	comboBox = new JComboBox(MidiUtil.getInputNames());
	column.setCellEditor(new DefaultCellEditor(comboBox));

        column = table.getColumnModel().getColumn(MIDI_OUT);
        column.setPreferredWidth(200); // wirski@op.pl
	comboBox = new JComboBox(MidiUtil.getOutputNames());
	column.setCellEditor(new DefaultCellEditor(comboBox));

        column = table.getColumnModel().getColumn(MIDI_CHANNEL);
        column.setPreferredWidth(90); // wirski@op.pl

        JScrollPane scrollpane = new JScrollPane(table);
        p.add(scrollpane, c);
	//((TableModel) table.getModel()).fireTableDataChanged();
	//table.setRowSelectionInterval(0, 0);

	// multiple MIDI interface check box
	cbxMMI = new JCheckBox("Use Multiple MIDI Interface");
	cbxMMI.setToolTipText("Allows users to select different MIDI port for each synth.");
	cbxMMI.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    multiMIDI = cbxMMI.isSelected();
		    setModified(true);
		}
	});
	++c.gridy;
	p.add(cbxMMI, c);

	// create buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JButton add = new JButton("Add Device...");
        add.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    addDevice();
		}
	    });
        buttonPanel.add(add);

        // BUTTON ADDED BY GERRIT GEHNEN
        JButton scan = new JButton("Auto-Scan...");
        scan.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    scanMidi();
		}
	    });
	buttonPanel.add(scan);
        // END OF ADDED BUTTON

	++c.gridy;
	p.add(buttonPanel, c);
	add(p, BorderLayout.CENTER);

	// popup menu
	popup = new JPopupMenu();
	JMenuItem mi;
	mi = new JMenuItem("Delete");
	//This works only for JMenuBar.
        //mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
	mi.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
	        removeDevice();
	    }
	});
	popup.add(mi);
	mi = new JMenuItem("Property...");
	mi.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
	        showDeviceProperty();
	    }
	});
	popup.add(mi);

	table.addMouseListener(new MouseAdapter() {
	    public void mousePressed(MouseEvent e) {
	        maybeShowPopup(e);
	    }
	    public void mouseReleased(MouseEvent e) {
	        maybeShowPopup(e);
	    }
	    private void maybeShowPopup(MouseEvent e) {
	        if (e.isPopupTrigger()) {
	            popup.show(e.getComponent(), e.getX(), e.getY());
	        }
	    }
	});
    }

    private void removeDevice() {
        if ((table.getSelectedRow() == -1) || (table.getSelectedRow() == 0))
	    return;
        if (JOptionPane.showConfirmDialog
	    (null, "Are you sure?", "Remove Device?",
	     JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
	    return;
	AppConfig.removeDevice(table.getSelectedRow());
	Utility.revalidateLibraries();
	((TableModel) table.getModel()).fireTableDataChanged();
	table.repaint();
    }

    private void showDeviceProperty() {
        if ((table.getSelectedRow() == -1)) // not selected
            return;
        AppConfig.getDevice(table.getSelectedRow()).showDetails(Utility.getFrame(this));
        //((TableModel) table.getModel()).fireTableDataChanged();
    }

    private void addDevice() {
	DeviceAddDialog dad = new DeviceAddDialog(null);
        dad.setVisible(true);
        Utility.revalidateLibraries();
	((TableModel) table.getModel()).fireTableDataChanged();
    }

    // METHOD ADDED BY GERRIT GEHNEN
    private void scanMidi() {
        if (JOptionPane.showConfirmDialog
	    (null,
	     "Scanning the System for supported Synthesizers may take\n"
	     + "a few minutes if you have many MIDI ports. During the scan\n"
	     + "it is normal for the system to be unresponsive.\n"
	     + "Do you wish to scan?",
	     "Scan for Synthesizers",
	     JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
	    return;

        if (midiScan != null) {
	    midiScan.close();
	}

        ProgressMonitor pm = new ProgressMonitor
	    (null,
	     "Scanning for SupportedSynthesizers",
	     "Initializing Midi Devices", 0, 100);
        midiScan = new MidiScan((TableModel) table.getModel(), pm, null);

        midiScan.start();
        Utility.revalidateLibraries();
        ((TableModel) table.getModel()).fireTableDataChanged();
    }
    // END OF METHOD ADDED BY GERRIT GEHNEN

    // ConfigPanel interface methods
    void init() {
	multiMIDI = AppConfig.getMultiMIDI();
	cbxMMI.setSelected(multiMIDI);
	cbxMMI.setEnabled(AppConfig.getMidiEnable());
	//table.setRowSelectionInterval(0, 0); // why this does not work? Hiroo
	((TableModel) table.getModel()).fireTableDataChanged();
    }

    // I gave up using 'Apply' botton for Synth Table. It's is
    // difficult to defer 'add device' and 'remove device' event.
    void commitSettings() {
	AppConfig.setMultiMIDI(multiMIDI);
 	((TableModel) table.getModel()).fireTableDataChanged();
	if (!multiMIDI) {
	    int out = AppConfig.getInitPortOut();
	    int in = AppConfig.getInitPortIn();
	    for (int i = 0; i < AppConfig.deviceCount(); i++) {
		AppConfig.getDevice(i).setPort(out);
		AppConfig.getDevice(i).setInPort(in);
	    }
	}

	setModified(false);
    }

    private class TableModel extends AbstractTableModel {
        private final String[] columnNames = { "Synth ID", "Device",
                "MIDI In Port", "MIDI Out Port", "Channel #", "Device ID" };

        TableModel() {
        }

        public int getColumnCount() {
            return columnNames.length;
        }

        public String getColumnName(int col) {
            return columnNames[col];
        }

        public int getRowCount() {
            return AppConfig.deviceCount();
        }

        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }

        public Object getValueAt(int row, int col) {
            Device myDevice = AppConfig.getDevice(row);

            switch (col) {
            case SYNTH_NAME:
                return myDevice.getSynthName();
            case DEVICE:
                return (myDevice.getManufacturerName() + " " + myDevice
                        .getModelName());
            case MIDI_IN:
                if (MidiUtil.isInputAvailable()) {
                    try {
                        int port = multiMIDI ? myDevice.getInPort() : AppConfig
                                .getInitPortIn();
                        return MidiUtil.getInputName(port); // wirski@op.pl
                    } catch (Exception ex) {
                        return "not available";
                    }
                } else {
                    return "not available";
                }
            case MIDI_OUT:
                if (MidiUtil.isOutputAvailable()) {
                    try {
                        int port = multiMIDI ? myDevice.getPort() : AppConfig
                                .getInitPortOut();
                        return MidiUtil.getOutputName(port); // wirski@op.pl
                    } catch (Exception ex) {
                        return "not available";
                    }
                } else {
                    return "not available";
                }
            case MIDI_CHANNEL:
                return new Integer(myDevice.getChannel());
            case MIDI_DEVICE_ID:
                return new Integer(myDevice.getDeviceID());
            default:
                return null;
            }
        }

        public boolean isCellEditable(int row, int col) {
            //Note that the data/cell address is constant,
            //no matter where the cell appears onscreen.
            return (col == SYNTH_NAME || (col == MIDI_IN && multiMIDI)
                    || (col == MIDI_OUT && multiMIDI) || col == MIDI_CHANNEL || col == MIDI_DEVICE_ID);
        }

        public void setValueAt(Object value, int row, int col) { 
            Device dev = AppConfig.getDevice(row);
            switch (col) {
            case SYNTH_NAME:
                dev.setSynthName((String) value);
                break;
            case MIDI_IN:
                dev.setInPort(MidiUtil.getInPort((String) value)); // wirski@op.pl
                break;
            case MIDI_OUT:
                dev.setPort(MidiUtil.getOutPort((String) value)); // wirski@op.pl
                break;
            case MIDI_CHANNEL:
                dev.setChannel(((Integer) value).intValue());
                break;
            case MIDI_DEVICE_ID:
                dev.setDeviceID(((Integer) value).intValue());
                break;
            }
            fireTableCellUpdated(row, col); // really required???
        }
    }
}
