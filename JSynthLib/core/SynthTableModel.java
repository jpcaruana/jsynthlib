package core;

import javax.swing.table.AbstractTableModel;

// Made public by emenaker 3/12/2003 because stuff that uses it was moved out of core.*
public class SynthTableModel extends AbstractTableModel {
    private final String[] columnNames = {
	"Synth ID",
	"Device",
	"Midi In Port",
	"Midi Out Port",
	"Midi Channel"
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

	if (col == 0)
	    return myDevice.getSynthName ();
	if (col == 1)
	    return (myDevice.getManufacturerName ()
		    + " " + myDevice.getModelName());
	if (col == 2) {
	    try {
		return (myDevice.getInPort ()
			+ ": "
			+ PatchEdit.MidiOut.getInputDeviceName(myDevice.getInPort()));
	    } catch (Exception e) {
		return "-";
	    }
	}
	if (col == 3) {
	    try {
		return (myDevice.getPort ()
			+ ": " + PatchEdit.MidiOut.getOutputDeviceName(myDevice.getPort()));
	    } catch (Exception e) {
		return "-";
	    }
	}
	return new Integer (myDevice.getChannel ());
    }

    public boolean isCellEditable (int row, int col) {
	//Note that the data/cell address is constant,
	//no matter where the cell appears onscreen.
	return col != 1;
    }

    public void setValueAt (Object value, int row, int col) {
	Device dev = (Device) PatchEdit.appConfig.getDevice(row);
	if (col == 0)
	    dev.setSynthName((String) value);
	if (col == 2)
	    try {
		dev.setInPort(new Integer(Integer.parseInt(((String) value).substring(0, 2))).intValue());
	    } catch (Exception e) {
		dev.setInPort(new Integer(Integer.parseInt(((String) value).substring(0, 1))).intValue());
	    }
	if (col == 3)
	    try {
		dev.setPort(new Integer(Integer.parseInt(((String) value).substring(0, 2))).intValue());
	    } catch (Exception e) {
		dev.setPort(new Integer(Integer.parseInt(((String) value).substring(0, 1))).intValue());
	    }

	if (col == 4)
	    dev.setChannel(((Integer) value).intValue());

	fireTableCellUpdated(row, col);
    }
}
