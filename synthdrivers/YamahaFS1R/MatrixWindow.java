package synthdrivers.YamahaFS1R;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.EventObject;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.jsynthlib.core.CheckBoxWidget;
import org.jsynthlib.core.ComboBoxWidget;
import org.jsynthlib.core.Patch;
import org.jsynthlib.core.SpinnerWidget;



/**
	Matrice de modulation.
	@author denis queffeulou mailto:dqueffeulou@free.fr
*/
class MatrixWindow extends JPanel 
{
	private Patch p;
	
	public Patch getPatch()
	{
		return p;
	}
	

	MatrixWindow(Patch aPatch) {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		p = aPatch;
                
		Component oTableWidgets[][] = new Component[20][8];
		MatrixCellRenderer oCellRender = new MatrixCellRenderer(p, oTableWidgets);
		MatrixCellEditor oCellEditor = new MatrixCellEditor(oTableWidgets);
		for (int i = 0; i < 8; i++) {
			// destinations
			ComboBoxWidget oDest = new ComboBoxWidget("", p, new YamahaFS1RPerformanceDriver.Model(p, 0x40+i), new YamahaFS1RPerformanceDriver.Sender(0x40+i), YamahaFS1RPerformanceEditor.mDestinations);
			oDest.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
			oTableWidgets[14][i] = oDest;
			// depth
			oTableWidgets[15][i] = new SpinnerWidget("",p , 0, 0x7F, -64, new YamahaFS1RPerformanceDriver.Model(p, 0x48+i), new YamahaFS1RPerformanceDriver.Sender(0x48+i));
			// sources
			for (int s = 0; s < 14; s++) {
				if (s < 7) {
					oTableWidgets[s][i] = new CheckBoxWidget("", p, new YamahaFS1RPerformanceDriver.BitModel(p, 0x30+i*2, 0, 1 << s, s), new YamahaFS1RPerformanceDriver.BitSender(p, 0x30+i*2));
				}
				else {
					oTableWidgets[s][i] = new CheckBoxWidget("", p, new YamahaFS1RPerformanceDriver.BitModel(p, 0x31+i*2, 0, 1 << (s-7), s-7), new YamahaFS1RPerformanceDriver.BitSender(p, 0x31+i*2));
				}
			}
			// part switches
			for (int part = 0; part < 4; part++) {
				oTableWidgets[15+part+1][i] = new CheckBoxWidget("", p, new YamahaFS1RPerformanceDriver.BitModel(p, 0x28+i, 0, 1 << part, part), new YamahaFS1RPerformanceDriver.BitSender(p, 0x28+i));
			}			
		}
		JTable oTable = new JTable(new MatrixTableModel(oTableWidgets));
		TableColumn column = oTable.getColumnModel().getColumn(0);
		column.setPreferredWidth(40);
		for (int i = 1; i <= 20; i++) {
			TableColumn oColumn = oTable.getColumnModel().getColumn(i);
			if (i == 15) {
				oColumn.setPreferredWidth(250);
			} 
			else if (i == 16) {
				oColumn.setPreferredWidth(70);
			}
			else {
				oColumn.setPreferredWidth(40); 
			}
			oColumn.setCellRenderer (oCellRender);
			oColumn.setCellEditor(oCellEditor);
		}
		oTable.setIntercellSpacing(new Dimension(0,0));
		oTable.setRowHeight(oTable.getRowHeight()+10);
		oTable.setShowGrid(false);
		oTable.setRowMargin(0);
		oTable.setAlignmentX(Component.LEFT_ALIGNMENT);
		JScrollPane oScrollPane = new JScrollPane(oTable);
		
		oTable.setPreferredScrollableViewportSize(new Dimension(800, 300)); 
		add(oScrollPane);
	}

	private static class MatrixTableModel extends AbstractTableModel {
		private static final String[] columnNames = {"Mod", "KN1", "KN2", "KN3", "KN4", "MC1", "MC2", "MC3", "MC4", "FC", "BC", "MW", "CAT", "PAT", "PB", "Destination", "Depth", "P1", "P2", "P3", "P4"
		};
		private Object mTable[];
		
		MatrixTableModel(Object aTable[]) {
			mTable = aTable;
		}
		public int getColumnCount() {
			return columnNames.length;
		}       
		public String getColumnName(int col) { 
			return columnNames[col];
		}
		public int getRowCount() { 
			return 8;
		}
		public Class getColumnClass(int c) {
			return getValueAt(0, c).getClass();
		}
		public Object getValueAt(int row, int col) {
			if (col == 0) {
				return Integer.toString(row+1);
			}
			return new Integer(row*col);
		}
        public boolean isCellEditable(int row, int col) {
             return col > 0;
        }
        public void setValueAt(Object value, int row, int col) {
			fireTableCellUpdated(row, col);
        }
	}

	static class MatrixCellRenderer  implements TableCellRenderer {
		Patch patch;
		private Component[][] mTable;
		
		MatrixCellRenderer(Patch p, Component[][] aTable) {
			patch=p;
			mTable = aTable;
		}	
		public Component getTableCellRendererComponent (JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
			return mTable[col-1][row]; 
		}							  
	}
	
	static class MatrixCellEditor implements TableCellEditor  {
		private Component[][] mCheck;
		MatrixCellEditor(Component[][] aCheck) {
			mCheck = aCheck;
		}
		public Component getTableCellEditorComponent (JTable table, Object value, boolean isSelected, int row, int col) {
			return mCheck[col-1][row]; 
		}							  
		public void cancelCellEditing(){};
		public Object getCellEditorValue() {return new Integer(0);}
		public void addCellEditorListener (CellEditorListener l) {}
		public boolean isCellEditable (EventObject e) {return true;}
		public void removeCellEditorListener (CellEditorListener l) {}
		public boolean shouldSelectCell (EventObject e) {return true;}
		public boolean stopCellEditing() {return true;}
	}
}
