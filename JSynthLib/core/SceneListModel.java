/*
 * SceneListModel.java
 *
 * Refactored from PerformanceListModel
 */

package core;
/**
 *
 * @author  Gerrit Gehnen
 * @version $Id$
 */
class SceneListModel extends javax.swing.table.AbstractTableModel implements AbstractPatchListModel {
    final String[] columnNames =
    {"Synth",
     "Type",
     "Patch Name",
     "Bank Number",
     "Patch Number",
     "Comment"};
     boolean changed;
     public java.util.ArrayList sceneList = new java.util.ArrayList();

     public SceneListModel(boolean c) {
         changed=c;
     }

     public int getColumnCount() {
         return columnNames.length;
     }

     public int getRowCount() {
         return sceneList.size();
     }

     public String getColumnName(int col) {
         return columnNames[col];
     }

     public Object getValueAt(int row, int col) {
	 try {
         Scene myScene=(Scene)sceneList.get(row);
         if (col==0) return myScene.getPatch().getDevice().getSynthName();
         if (col==1) return myScene.getPatch().getDriver().getPatchType();
         if (col==2) return myScene.getPatch().getDriver().getPatchName(myScene.getPatch());
         if (col==3) return myScene.getPatch().getDriver().bankNumbers[myScene.getBankNumber()];
         if (col==4) return myScene.getPatch().getDriver().patchNumbers[myScene.getPatchNumber()];
         return myScene.getComment();
	 } catch (Exception e) {
	     ErrorMsg.reportStatus(e);
	     return null;
	 }
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
         //System.out.println("SetValue at "+row+"  "+col+" Value:"+value);
         changed=true;
         Scene myScene=(Scene)sceneList.get(row);
         if (col==0) {
             myScene.getPatch().getDevice().setSynthName((String)value);
         }
         if (col==1) {
             // don't allow to change the Patch Type
         }
         if (col==2) {
             // don't allow to change the Patch Name
         }
         if (col==3) {
             myScene.setBankNumber(((Integer) value).intValue());
         }
         if (col==4) {
             myScene.setPatchNumber(((Integer) value).intValue());
         }
         if (col==5) {
             /* Comment */
             myScene.setComment((String) value);
         }
         sceneList.set(row,myScene);
     }

     public Scene getSceneAt(int row) {
         return (Scene)sceneList.get(row);
     }

     public void setSceneAt(Scene p,int row) {
         sceneList.set(row,p);
         fireTableRowsUpdated(row, row);
     }

     public Patch getPatchAt(int row) {
         return ((Scene)sceneList.get(row)).getPatch();
     }

     public void setPatchAt(Patch p,int row) {
         Scene perf;
         perf=((Scene)sceneList.get(row));
         perf.setPatch(p);
         fireTableRowsUpdated(row, row);
     }

     public void addPatch(Patch p) {
         Scene perf=new Scene(p);
         sceneList.add(perf);
         this.fireTableDataChanged();
     }

     public String getCommentAt(int row) {
         return ((Scene) sceneList.get(row)).getComment();
     }

}
