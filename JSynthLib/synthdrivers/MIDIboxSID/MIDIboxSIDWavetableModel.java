/*
 * JSynthlib Wavetable Model for MIDIbox SID
 * =====================================================================
 * @author  Thorsten Klose
 * file:    MIDIboxSIDWavetableModel.java
 * date:    2002-11-30
 * @version 1.0
 *
 * Copyright (C) 2002  Thorsten.Klose@gmx.de   
 *                     http://www.uCApps.de
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package synthdrivers.MIDIboxSID;

import javax.swing.table.AbstractTableModel;

class MIDIboxSIDWavetableModel extends AbstractTableModel
{
    boolean hex_view = true;

    byte[] dump = new byte[4*32];

    byte[] columnCC = new byte[3];

    final String[] columnNames = { 
	"Step",
	"Mode",
	"#1",
	"#2",
	"#3"
    };
     
    final String[] modeNames = { 
	"-",
	"Play",
	"Goto",
	"End"
    };

    final String [] NoteName = new String [] {
	"---","+++","_C-2","_D#2","_E-2","_F-2","_F#2","_G-2","_G#2","_A-2","_A#2","_B-2",
	"_C-1","_C#1","_D-1","_D#1","_E-1","_F-1","_F#1","_G-1","_G#1","_A-1","_A#1","_B-1",
	"C-0","C#0","D-0","D#0","E-0","F-0","F#0","G-0","G#0","A-0","A#0","B-0",
	"C-1","C#1","D-1","D#1","E-1","F-1","F#1","G-1","G#1","A-1","A#1","B-1",
	"C-2","C#2","D-2","D#2","E-2","F-2","F#2","G-2","G#2","A-2","A#2","B-2",
	"C-3","C#3","D-3","D#3","E-3","F-3","F#3","G-3","G#3","A-3","A#3","B-3",
	"C-4","C#4","D-4","D#4","E-4","F-4","F#4","G-4","G#4","A-4","A#4","B-4",
	"C-5","C#5","D-5","D#5","E-5","F-5","F#5","G-5","G#5","A-5","A#5","B-5",
	"C-6","C#6","D-6","D#6","E-6","F-6","F#6","G-6","G#6","A-6","A#6","B-6",
	"C-7","C#7","D-7","D#7","E-7","F-7","F#7","G-7","G#7","A-7","A#7","B-7",
	"C-8","C#8","D-8","D#8","KEY1","KEY2","KEY3","KEY4"};

    private byte convertStringToByte(String str)
    {
	int value = 0;

	str = str.trim();

	String sign = str.substring(0, 1);

	if( !(str.equals("+++") || str.equals("---")) && (sign.equals("+") || sign.equals("-")) ) {
	    str = str.substring(1).trim();
	}
	else {
	    // scan for note
	    for(int i=0; i<0x80; ++i) {
		if( str.toLowerCase().equals(NoteName[i].toLowerCase()) ) {
		    return (byte)(i | 0x80);
		}
	    }
	}

	while( str.length() > 0 && str.substring(0, 1).equals("0") ) { str = str.substring(1).trim(); }


	try { 
	    value = (int)java.lang.Integer.parseInt(str, getHexView() ? 16 : 10); 
	} catch(Exception e)  {}

	if( value < 0 || value >= 0x80 )
	    return 0;

	if( sign.equals("+") ) {
	    if( value >= 0x40 ) { return 0; }
	}
	else if( sign.equals("-") ) {
	    if( value > 0x40 ) { return 0; }
	    if( value <= 0x00 ) value = 0x00; else value = 0x80 - value;
	}
        else {
	    value |= 0x80;
	}

	return (byte)value;
    }

    private String convertByteToString(byte value, int cc)
    {
	int value_i = (int)(value & 0x7f);
	String sign = "";

	if( value >= 0 ) {
	    if( value_i < 0x40 ) { sign = "+"; value_i &= 0x3f; }
	    else if( value_i >= 0x40 ) { sign = "-"; value_i = 0x80-value_i; }
	}
	else
	{
	    if( cc >= 8 && cc <= 11 )
	    {
		return NoteName[value & 0x7f];
	    }
	}

	if( getHexView() ) {
	    String hex_str = "00" + java.lang.Integer.toString(value_i, 16).toUpperCase();
	    return sign + hex_str.substring(hex_str.length()-2);
	} else {
	    return sign + java.lang.Integer.toString(value_i, 10);
	}
    }

    MIDIboxSIDWavetableModel()
    {
    }

    public boolean getHexView()
    { return hex_view; }

    public void setHexView(boolean b)
    { hex_view = b; }

    public int getColumnCount()
    {return columnNames.length;}

    public String getColumnName(int col)
    { return columnNames[col];}

    public int getRowCount()
    { return 32;}

    public Class getColumnClass(int c)
    {return getValueAt(0, c).getClass();}

    public Object getValueAt(int row, int col)
    {
	if( col == 0 ) return convertByteToString((byte)(0x80+row), 0); 
	if( col == 1 ) return modeNames[dump[4*row+0]];
	if( col == 2 ) return convertByteToString(dump[4*row+1], columnCC[0]);
	if( col == 3 ) return convertByteToString(dump[4*row+2], columnCC[1]);
	if( col == 4 ) return convertByteToString(dump[4*row+3], columnCC[2]);
	return "Invalid";
    }
     
     
    public boolean isCellEditable(int row, int col)
    {
	if( col==0 ) return false; else return true;
    }

    public void setValueAt(Object value, int row, int col)
    {
	if( col == 1 ) { 
	    for(int i=0; i<modeNames.length; ++i) {
		if( modeNames[i] == value ) { dump[4*row+0] = (byte)i; break; }
	    }
	}
	if( col == 2 ) {
	    dump[4*row+1] = convertStringToByte((String)value);
	}
	if( col == 3 ) {
	    dump[4*row+2] = convertStringToByte((String)value);
	}
	if( col == 4 ) {
	    dump[4*row+3] = convertStringToByte((String)value);
	}
    }

    public byte[] getCookedDump() 
    {
	byte[] cooked_dump = new byte[4*32];

	for(int i=0; i<32; ++i) {
	    int mode_entry;

	    mode_entry = (int)dump[i*4+0];
	    if( dump[i*4+1] < 0) { mode_entry += 0x10; }
	    if( dump[i*4+2] < 0) { mode_entry += 0x20; }
	    if( dump[i*4+3] < 0) { mode_entry += 0x40; }
	    cooked_dump[i*4+0] = (byte)mode_entry;

	    cooked_dump[i*4+1] = (byte)((int)dump[i*4+1] & 0x7f);
	    cooked_dump[i*4+2] = (byte)((int)dump[i*4+2] & 0x7f);
	    cooked_dump[i*4+3] = (byte)((int)dump[i*4+3] & 0x7f);
	}
	return cooked_dump;
    }

    public void setCookedDump(byte[] cooked_dump)
    {
	for(int i=0; i<32; ++i) {
	    int mode_entry;

	    mode_entry = (int)cooked_dump[i*4+0];

	    dump[i*4+0] = (byte)(mode_entry & 0x0f);
	    dump[i*4+1] = (byte)((int)cooked_dump[i*4+1] | ((mode_entry&0x10)>0 ? 0x80 : 0x00));
	    dump[i*4+2] = (byte)((int)cooked_dump[i*4+2] | ((mode_entry&0x20)>0 ? 0x80 : 0x00));
	    dump[i*4+3] = (byte)((int)cooked_dump[i*4+3] | ((mode_entry&0x40)>0 ? 0x80 : 0x00));
	}
    }

    public void setColumnCC(int column, byte cc)
    {
        columnCC[column] = cc;
    }

    public byte getColumnCC(int column)
    {
        return columnCC[column];
    }

}
