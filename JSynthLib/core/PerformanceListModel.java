/*
 * PerformanceListModel.java
 *
 */

package core;
/**
 *
 * @author  Gerrit Gehnen
 * @version $Id$
 */
class PerformanceListModel extends javax.swing.table.AbstractTableModel implements AbstractPatchListModel {
    final String[] columnNames =
    {"Synth",
     "Type",
     "Patch Name",
     "Bank Number",
     "Patch Number",
     "Comment"};
     boolean changed;
     public java.util.ArrayList performanceList = new java.util.ArrayList();
     
     public PerformanceListModel(boolean c) {
         changed=c;
     }
          
     public int getColumnCount() {
         return columnNames.length;
     }
     
     public int getRowCount() {
         return performanceList.size();
     }
     
     public String getColumnName(int col) {
         return columnNames[col];
     }
     
     public Object getValueAt(int row, int col) {
         Performance myPerformance=(Performance)performanceList.get(row);
         if (col==0) return ((Device)PatchEdit.deviceList.get(myPerformance.getPatch().deviceNum)).getSynthName();
         if (col==1) return PatchEdit.getDriver(myPerformance.getPatch().deviceNum,myPerformance.getPatch().driverNum).getPatchType();
         if (col==2) return PatchEdit.getDriver(myPerformance.getPatch().deviceNum,myPerformance.getPatch().driverNum).getPatchName(myPerformance.getPatch());
         if (col==3) return PatchEdit.getDriver(myPerformance.getPatch().deviceNum,myPerformance.getPatch().driverNum).bankNumbers[myPerformance.getBankNumber()];
         if (col==4) return PatchEdit.getDriver(myPerformance.getPatch().deviceNum,myPerformance.getPatch().driverNum).patchNumbers[myPerformance.getPatchNumber()];
         return myPerformance.getComment();
         
     }
     
        /*
         * JTable uses this method to determine the default renderer/
         * editor for each cell.  If we didn't implement this method,
         * then the last column would contain text ("true"/"false"),
         * rather than a check box.
         */
     public Class getColumnClass(int c) {
         try{
             return Class.forName("java.lang.String");
         }
         catch (Exception e) {
             return null;
         }
     }
     
     public boolean isCellEditable(int row, int col) {
         if (col>2) return true; else return false;
     }
     
     public void setValueAt(Object value, int row, int col) {
         System.out.println("SetValue at "+row+"  "+col+" Value:"+value);
         changed=true;
         Performance myPerformance=(Performance)performanceList.get(row);
         if (col==0) {
             ((Device)PatchEdit.deviceList.get(myPerformance.getPatch().deviceNum)).setSynthName((String)value);
         }
         if (col==1) {
             // don't allow to change the Patch Type
         }
         if (col==2) {
             // don't allow to change the Patch Name
         }
         if (col==3) {
             myPerformance.setBankNumber(((Integer)value).intValue());
         }
         if (col==4) {
             myPerformance.setPatchNumber(((Integer) value).intValue());
         }
         if (col==5) {
             /* Comment */
             myPerformance.setComment((StringBuffer)value);
         }
         performanceList.set(row,myPerformance);
     }
     
     public Performance getPerformanceAt(int row) {
         return (Performance)performanceList.get(row);
     }
     
     public void setPerformanceAt(Performance p,int row) {
         performanceList.set(row,p);
         fireTableRowsUpdated(row, row);
     }
     
     public Patch getPatchAt(int row) {
         return ((Performance)performanceList.get(row)).getPatch();
     }
     
     public void setPatchAt(Patch p,int row) {
         Performance perf;
         perf=((Performance)performanceList.get(row));
         perf.setPatch(p);
         fireTableRowsUpdated(row, row);
     }
     
     public void addPatch(Patch p) {
         Performance perf=new Performance(p);
         performanceList.add(perf);
         this.fireTableDataChanged();
     }
     
}