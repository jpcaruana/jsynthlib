/*
 * PatchGridModel.java
 *
 * Created on 16. Mai 2001, 21:58
 */


package core;

import javax.swing.table.AbstractTableModel;

public class PatchGridModel extends AbstractTableModel
{

    public Patch bankData;
    public BankDriver bankDriver;

    /**
     * @param p
     * @param d  */
    public PatchGridModel (Patch p,BankDriver d)
    {super();
ErrorMsg.reportStatus("PatchGridModel");
     bankData=p;
     bankDriver=d;
    }


    /**
     * @return  */
    public int getColumnCount ()
    {
        return bankDriver.getNumColumns();
    }

    /**
     * @return  */
    public int getRowCount ()
    {
        return bankDriver.getNumPatches()/bankDriver.getNumColumns();
    }

    /**
     * @param col
     * @return  */
    public String getColumnName (int col)
    {
        return "";
    }

    /**
     * @param row
     * @param col
     * @return  */
    public Object getValueAt (int row, int col)
    {
	String patchNumbers[] = bankDriver.getPatchNumbers();
	int i = col*bankDriver.getNumPatches()/bankDriver.getNumColumns()+row;
        return (patchNumbers[i] + " " + bankDriver.getPatchName(bankData, i));
    }
    /**
     * @param row
     * @param col
     * @return  */
    public Patch getPatchAt(int row, int col)
    {
	int i = col*bankDriver.getNumPatches()/bankDriver.getNumColumns()+row;
        return bankDriver.getPatch(bankData, i);
    }
    /**
     * @param c
     * @return  */
    public Class getColumnClass (int c)
    {
        return getValueAt (0, c).getClass ();
    }
    /**
     * @param row
     * @param col
     * @return  */
    public boolean isCellEditable (int row, int col)
    {
        //Note that the data/cell address is constant,
        //no matter where the cell appears onscreen.

//----- Start phil@muqus.com (allow patch name editing from a bank edit window)
      //return false;
      return true;
//----- End phil@muqus.com

    }
    /**
     * @param p
     * @param row
     * @param col  */
    public void setPatchAt(Patch p,int row,int col)
    {
        bankDriver.checkAndPutPatch(bankData,p,col*bankDriver.getNumPatches()/bankDriver.getNumColumns()+row);
        fireTableCellUpdated (row, col);
    }
    /**
     * @param value
     * @param row
     * @param col  */
    public void setValueAt (Object value, int row, int col)
    {
//----- Start phil@muqus.com (allow patch name editing from a bank edit window)
      int patchNum = col * bankDriver.getNumPatches() / bankDriver.getNumColumns() + row;
      String[] patchNumbers = bankDriver.getPatchNumbers();
      bankDriver.setPatchName(bankData, patchNum,
			      ((String) value).substring((patchNumbers[patchNum] + " ").length()));
//----- End phil@muqus.com
      fireTableCellUpdated (row, col);
    }
}
