package core;

import javax.swing.table.AbstractTableModel;

// Made public by emenaker 3/12/2003 because stuff that uses it was moved out of core.*
public class SynthTableModel extends AbstractTableModel {
    private static final int SYNTH_NAME	    = 0;
    private static final int DEVICE	    = 1;
    private static final int MIDI_IN	    = 2;
    private static final int MIDI_OUT	    = 3;
    private static final int MIDI_CHANNEL   = 4;
    private static final int MIDI_DEVICE_ID = 5;

    private final String[] columnNames = {
	"Synth ID",
	"Device",
	"MIDI In Port",
	"MIDI Out Port",
	"MIDI Channel",
	"MIDI Device ID"
    };

    SynthTableModel () {
    }

    public int getColumnCount () {
	return columnNames.length;
    }
    public String getColumnName (int col) {
	return columnNames[col];
    }
    public int getRowCount () {
	return PatchEdit.appConfig.deviceCount ();
    }
    public Class getColumnClass (int c) {
	return getValueAt (0, c).getClass ();
    }
    public Object getValueAt (int row, int col) {
	Device myDevice = (Device) PatchEdit.appConfig.getDevice (row);

	if (col == SYNTH_NAME)
	    return myDevice.getSynthName ();
	if (col == DEVICE)
	    return (myDevice.getManufacturerName ()
		    + " " + myDevice.getModelName());
	if (col == MIDI_IN) {
	    try {
		return (myDevice.getInPort ()
			+ ": "
			+ PatchEdit.MidiOut.getInputDeviceName(myDevice.getInPort()));
	    } catch (Exception e) {
		return "-";
	    }
	}
	if (col == MIDI_OUT) {
	    try {
		return (myDevice.getPort ()
			+ ": " + PatchEdit.MidiOut.getOutputDeviceName(myDevice.getPort()));
	    } catch (Exception e) {
		return "-";
	    }
	}
	if (col == MIDI_CHANNEL)
	    return new Integer (myDevice.getChannel());
	if (col == MIDI_DEVICE_ID)
	    return new Integer (myDevice.getDeviceID());

	return null;
    }

    public boolean isCellEditable (int row, int col) {
	//Note that the data/cell address is constant,
	//no matter where the cell appears onscreen.
	return col != 1;
    }

    public void setValueAt (Object value, int row, int col) {
	Device dev = (Device) PatchEdit.appConfig.getDevice(row);
	if (col == SYNTH_NAME)
	    dev.setSynthName((String) value);
	if (col == MIDI_IN) {
	    String s = (String) value;
	    int port = Integer.parseInt(s.substring(0, s.indexOf(':')));
	    dev.setInPort(port);
	}
	if (col == MIDI_OUT) {
	    String s = (String) value;
	    int port = Integer.parseInt(s.substring(0, s.indexOf(':')));
	    dev.setPort(port);
	}
	if (col == MIDI_CHANNEL)
	    dev.setChannel(((Integer) value).intValue());

	if (col == MIDI_DEVICE_ID)
	    dev.setDeviceID(((Integer) value).intValue());

	fireTableCellUpdated(row, col);
    }
}
