package core;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.sound.midi.MidiDevice;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ProgressMonitor;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

/**
 * ConfigPanel for Synthesizer Configuration
 * @author ???
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

    private static final int SYNTH_NAME	    = 0;
    private static final int DEVICE	    = 1;
    private static final int MIDI_IN	    = 2;
    private static final int MIDI_OUT	    = 3;
    private static final int MIDI_CHANNEL   = 4;
    private static final int MIDI_DEVICE_ID = 5;

    SynthConfigPanel(PrefsDialog parent, AppConfig appConfig) {
        super(parent, appConfig);
	setLayout(new ColumnLayout());

	// create synth driver table
        table = new JTable(new TableModel());
	table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setPreferredScrollableViewportSize(new Dimension(650, 150));

        TableColumn column;
        column = table.getColumnModel().getColumn(SYNTH_NAME);
        column.setPreferredWidth(75);
        column = table.getColumnModel().getColumn(DEVICE);
        column.setPreferredWidth(250);

	JComboBox comboBox;
        column = table.getColumnModel().getColumn(MIDI_IN);
        column.setPreferredWidth(150);
	comboBox = new JComboBox(MidiUtil.getInputMidiDeviceInfo());
	column.setCellEditor(new DefaultCellEditor(comboBox));

        column = table.getColumnModel().getColumn(MIDI_OUT);
        column.setPreferredWidth(150);
	comboBox = new JComboBox(MidiUtil.getOutputMidiDeviceInfo());
	column.setCellEditor(new DefaultCellEditor(comboBox));

        column = table.getColumnModel().getColumn(MIDI_CHANNEL);
        column.setPreferredWidth(75);

        JScrollPane scrollpane = new JScrollPane(table);
        add(scrollpane/*, BorderLayout.CENTER*/);
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
	add(cbxMMI);

	// create buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JButton detail = new JButton("Show Details");
        detail.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    detailPressed();
		}
	    });
        buttonPanel.add(detail);

        JButton add = new JButton("Add Device");
        add.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    addPressed();
		}
	    });
        buttonPanel.add(add);

        // BUTTON ADDED BY GERRIT GEHNEN
        JButton scan = new JButton("Auto-Scan");
        scan.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    scanPressed();
		}
	    });
	buttonPanel.add(scan);
        // END OF ADDED BUTTON

        JButton rem = new JButton("Remove Device");
        rem.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    removePressed();
		}
	    });
        buttonPanel.add(rem);

        add(buttonPanel);
    }

    private void removePressed() {
        if ((table.getSelectedRow() == -1) || (table.getSelectedRow() == 0))
	    return;
        if (JOptionPane.showConfirmDialog
	    (null, "Are you sure?", "Remove Device?",
	     JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
	    return;
	appConfig.removeDevice(table.getSelectedRow());
	revalidateLibraries();
	((TableModel) table.getModel()).fireTableDataChanged();
	table.repaint();
    }

    private void detailPressed() {
        if ((table.getSelectedRow() == -1)) // not selected
	    return;
        AppConfig.getDevice(table.getSelectedRow()).showDetails();
        //((TableModel) table.getModel()).fireTableDataChanged();
    }

    private void addPressed() {
	DeviceAddDialog dad = new DeviceAddDialog(null);
        dad.show();
        revalidateLibraries();
	((TableModel) table.getModel()).fireTableDataChanged();
    }

    private void revalidateLibraries() {
	JSLFrame[] jList = JSLDesktop.getAllFrames();
	if (jList.length > 0) {
	    PatchEdit.showWaitDialog();
	    for (int i = 0; i < jList.length; i++) {
		if (jList[i] instanceof LibraryFrame)
		    ((LibraryFrame) (jList[i])).revalidateDrivers();
		else if (jList[i] instanceof BankEditorFrame)
		    ((BankEditorFrame) (jList[i])).revalidateDriver();
		else if (jList[i] instanceof PatchEditorFrame)
		    ((PatchEditorFrame) (jList[i])).revalidateDriver();
	    }
	    PatchEdit.hideWaitDialog();
	}
    }

    // METHOD ADDED BY GERRIT GEHNEN
    private void scanPressed() {
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
        revalidateLibraries();
        ((TableModel) table.getModel()).fireTableDataChanged();
    }
    // END OF METHOD ADDED BY GERRIT GEHNEN

    // ConfigPanel interface methods
    void init() {
	multiMIDI = appConfig.getMultiMIDI();
	cbxMMI.setSelected(multiMIDI);
	cbxMMI.setEnabled(appConfig.getMidiEnable());
	//table.setRowSelectionInterval(0, 0); // why this does not work? Hiroo
	((TableModel) table.getModel()).fireTableDataChanged();
    }

    void commitSettings() {
	appConfig.setMultiMIDI(multiMIDI);
 	((TableModel) table.getModel()).fireTableDataChanged();
	if (!multiMIDI) {
	    int out = appConfig.getInitPortOut();
	    int in = appConfig.getInitPortIn();
	    for (int i = 0; i < appConfig.deviceCount(); i++) {
		AppConfig.getDevice(i).setPort(out);
		AppConfig.getDevice(i).setInPort(in);
	    }
	}

	setModified(false);
    }

    private class TableModel extends AbstractTableModel {
	private final String[] columnNames = {
	    "Synth ID",
	    "Device",
	    "MIDI In Port",
	    "MIDI Out Port",
	    "Channel #",
	    "Device ID"
	};

	TableModel() {
	}

	public int getColumnCount() {
	    return columnNames.length;
	}
	public String getColumnName(int col) {
	    return columnNames[col];
	}
	public int getRowCount() {
	    return appConfig.deviceCount();
	}
	public Class getColumnClass(int c) {
	    return getValueAt(0, c).getClass();
	}
	public Object getValueAt(int row, int col) {
	    Device myDevice = (Device) AppConfig.getDevice(row);

	    switch (col) {
	    case SYNTH_NAME:
		return myDevice.getSynthName();
	    case DEVICE:
		return (myDevice.getManufacturerName()
			+ " " + myDevice.getModelName());
	    case MIDI_IN:
		if (MidiUtil.isInputAvailable()) {
		    int port = multiMIDI ?
			myDevice.getInPort() : appConfig.getInitPortIn();
		    return MidiUtil.getInputMidiDeviceInfo(port).getName();
		} else {
		    return "not available";
		}
	    case MIDI_OUT:
		if (MidiUtil.isOutputAvailable()) {
		    int port = multiMIDI ?
			myDevice.getPort() : appConfig.getInitPortOut();
		    return MidiUtil.getOutputMidiDeviceInfo(port).getName();
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
	    return (col == SYNTH_NAME
		    || (col == MIDI_IN && multiMIDI)
		    || (col == MIDI_OUT && multiMIDI)
		    || col == MIDI_CHANNEL
		    || col == MIDI_DEVICE_ID);
	}

	public void setValueAt(Object value, int row, int col) {
	    Device dev = (Device) AppConfig.getDevice(row);
	    switch (col) {
	    case SYNTH_NAME:
		dev.setSynthName((String) value);
		break;
	    case MIDI_IN:
		dev.setInPort(MidiUtil.getInPort((MidiDevice.Info) value));
		break;
	    case MIDI_OUT:
		dev.setPort(MidiUtil.getOutPort((MidiDevice.Info) value));
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

    /*
    // I gave up using 'Apply' botton for Synth Table. It's is
    // difficult to defer 'add device' and 'remove device' event.
    private class DeviceInfo {
	private String synthName;
	private int midiIn;
	private int midiOut;
	private int channel;
	private int deviceID;

	private boolean synthNameChanged;
	private boolean midiInChanged;
	private boolean midiOutChanged;
	private boolean channelChanged;
	private boolean deviceIDChanged;

	DeviceInfo(Device dev) {
	    synthName = dev.getSynthName();
	    midiIn   = multiMIDI ? dev.getInPort() : appConfig.getInitPortIn();
	    midiOut  = multiMIDI ? dev.getPort() : appConfig.getInitPortOut();
	    channel  = dev.getChannel();
	    deviceID = dev.getDeviceID();
	}

	String getsynthName() { return synthName; }
	int getmidiIn()   { return midiIn; }
	int getmidiOut()  { return midiOut; }
	int getchannel()  { return channel; }
	int getdeviceID() { return deviceID; }

	void setSynthName(String synthName) {
	    this.synthName = synthName;
	    synthNameChanged = true;
	}
	void setMidiIn(int midiIn) {
	    this.midiIn    = midiIn;
	    midiInChanged = true;
	}
	void setMidiOut(int midiOut) {
	    this.midiOut   = midiOut;
	    midiOutChanged = true;
	}
	void setChannel(int channel) {
	    this.channel   = channel;
	    channelChanged = true;
	}
	void setDeviceID(int deviceID) {
	    this.deviceID  = deviceID;
	    deviceIDChanged = true;
	}

	void apply(Device dev) {
	    if (synthNameChanged) {
		dev.setSynthName(synthName);
		synthNameChanged = false;
	    }
	    if (midiInChanged) {
		dev.setInPort(midiIn);
		midiInChanged    = false;
	    }
	    if (midiOutChanged) {
		dev.setPort(midiOut);
		midiOutChanged   = false;
	    }
	    if (channelChanged) {
		dev.setChannel(channel);
		channelChanged   = false;
	    }
	    if (deviceIDChanged) {
		dev.setDeviceID(deviceID);
		deviceIDChanged  = false;
	    }
	}
    }
    */
}
