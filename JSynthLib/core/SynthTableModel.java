package core;

import jmidi.MidiPort;
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


class SynthTableModel extends AbstractTableModel
{
    final String[] columnNames =
    {"Synth ID",
     "Device",
     "Midi In Port",
     "Midi Out Port",
     "Midi Channel"};
     
     SynthTableModel ()
     {
     }
     public int getColumnCount ()
     {return columnNames.length;}
     public String getColumnName (int col)
     { return columnNames[col];}
     public int getRowCount ()
     { return PatchEdit.deviceList.size ();}
     public Class getColumnClass (int c)
     {return getValueAt (0, c).getClass ();}
     public Object getValueAt (int row, int col)
     {
         
         Device myDevice=(Device)PatchEdit.deviceList.get (row);
         
         if (col==0) return myDevice.getSynthName ();
         if (col==1) return myDevice.getManufacturerName ()+" "+myDevice.getModelName ();
         if (col==2)
         {try
          {
              return myDevice.getInPort ()+": "+PatchEdit.MidiOut.getInputDeviceName (myDevice.getInPort ());
          }catch (Exception e)
          {return "-";}
         }
         if (col==3)
         {try
          {
              return myDevice.getPort ()+": "+PatchEdit.MidiOut.getOutputDeviceName (myDevice.getPort ());
          }catch (Exception e)
          {return "-";}
         }
         
         return new Integer (myDevice.getChannel ());
         
     }
     
     
     public boolean isCellEditable (int row, int col)
     {
         //Note that the data/cell address is constant,
         //no matter where the cell appears onscreen.
         if (col==1) return false; else return true;
     }
     public void setValueAt (Object value, int row, int col)
     {
         if (col==0)
             ((Device)PatchEdit.deviceList.get (row)).setSynthName ((String)value);
         if (col==2)
             try
             {
                 ((Device)PatchEdit.deviceList.get (row)).setInPort (new Integer (Integer.parseInt (((String)value).substring (0,2))).intValue ());
             }
             catch (Exception e)
             {
                 ((Device)PatchEdit.deviceList.get (row)).setInPort(new Integer (Integer.parseInt (((String)value).substring (0,1))).intValue ());
             }
      if (col==3)
             try
             {
                 ((Device)PatchEdit.deviceList.get (row)).setPort (new Integer (Integer.parseInt (((String)value).substring (0,2))).intValue ());
             }
             catch (Exception e)
             {
                 ((Device)PatchEdit.deviceList.get (row)).setPort(new Integer (Integer.parseInt (((String)value).substring (0,1))).intValue ());
             }
      
          if (col==4)
                 ((Device)PatchEdit.deviceList.get (row)).setChannel(((Integer)value).intValue ());
             fireTableCellUpdated (row, col);
             
     }
}


