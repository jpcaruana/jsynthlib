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


class DeviceDetailsTableModel extends AbstractTableModel
{
    final String[] columnNames =
    {
     "Driver Name",
     "Type",
     "Authors",
    };
    Device d; 
     public DeviceDetailsTableModel (Device de)
     { d=de;
     }
     public int getColumnCount ()
     {return columnNames.length;}
     public String getColumnName (int col)
     { return columnNames[col];}
     public int getRowCount ()
     { return d.driverList.size();}
     public Class getColumnClass (int c)
     {return getValueAt (0, c).getClass ();}
     public Object getValueAt (int row, int col)
     {
         
         Driver myDriver=(Driver)d.driverList.get (row);
         
	 if (col==0) return d.getManufacturerName()+" "+d.getModelName()+" "+myDriver.getPatchType();
         if (col==1) if (myDriver instanceof Converter) return "Converter"; else return "Driver";
	 else
         {return myDriver.getAuthors();}
         
         
     }
     
     
     public boolean isCellEditable (int row, int col)
     {
         //Note that the data/cell address is constant,
         //no matter where the cell appears onscreen.
         return false;
     }
   
}


