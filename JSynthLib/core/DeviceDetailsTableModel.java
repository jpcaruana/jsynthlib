package core;

import javax.swing.table.AbstractTableModel;

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
     { return d.driverCount();}
     public Class getColumnClass (int c)
     {return getValueAt (0, c).getClass ();}
     public Object getValueAt (int row, int col)
     {
         
         Driver myDriver=(Driver)d.getDriver(row);
         
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


