/**
 * SceneTableCellEditor
 *
 * @version $Id$
 * @author Gerrit Gehnen
 */

package core;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;

public class SceneTableCellEditor implements TableCellEditor, TableModelListener {
    
    protected TableCellEditor editor, defaultEditor;
    protected JComboBox box;
    JTable table;
    private int oldrow=-1;
    private int oldcol=-1;
    
    /**
     * Constructs a SceneTableCellEditor.
     * create default editor
     *
     * @see TableCellEditor
     * @see DefaultCellEditor
     */
    public SceneTableCellEditor(JTable table) {
        this.table = table;
        defaultEditor = new DefaultCellEditor(new JTextField());
        this.table.getModel().addTableModelListener(this);
    }
    
    public Component getTableCellEditorComponent(JTable table,
    Object value, boolean isSelected, int row, int column) {
        return editor.getTableCellEditorComponent(table, value, isSelected, row, column);
    }
    
    public Object getCellEditorValue() {
        //        System.out.println("getCellEditorValue "+box.getSelectedItem());
        return new Integer(box.getSelectedIndex());
    }
    public boolean stopCellEditing() {
        return editor.stopCellEditing();
    }
    public void cancelCellEditing() {
        editor.cancelCellEditing();
    }
    public boolean isCellEditable(EventObject anEvent) {
        selectEditor((MouseEvent)anEvent);
        return editor.isCellEditable(anEvent);
    }
    public void addCellEditorListener(CellEditorListener l) {
        editor.addCellEditorListener(l);
    }
    public void removeCellEditorListener(CellEditorListener l) {
        editor.removeCellEditorListener(l);
    }
    public boolean shouldSelectCell(EventObject anEvent) {
        selectEditor((MouseEvent)anEvent);
        return editor.shouldSelectCell(anEvent);
    }
    
    protected void selectEditor(MouseEvent e) {
        Driver driver;
        int row,col;
        
        if (e == null) {
            row = table.getSelectionModel().getAnchorSelectionIndex();
            col=table.getSelectedColumn();
        } else {
            row = table.rowAtPoint(e.getPoint());
            col=table.columnAtPoint(e.getPoint());
        }
        //    System.out.println("selectEditor "+ row);
        if ((row!=oldrow)||(col!=oldcol)) {
            oldrow=row;
            oldcol=col;
            box=new JComboBox();
            
            driver=((SceneListModel)table.getModel()).getSceneAt(row).getPatch().getDriver();
	    String patchNumbers[] = driver.getPatchNumbers();
            if (patchNumbers.length > 1) {
                if (col==3) {
                    for (int i = 0 ; i < driver.bankNumbers.length ; i++) {
                        box.addItem(driver.bankNumbers[i]);
                    }
                }
                if (col==4)
                    for (int i = 0 ; i < patchNumbers.length ; i++) {
                        box.addItem(patchNumbers[i]);
                    }
            }
            
            editor = new DefaultCellEditor(box );
            
            if (editor == null) {
                editor = defaultEditor;
            }
        }
    }
    
    public void tableChanged(javax.swing.event.TableModelEvent tableModelEvent) {
        oldcol=-1;
        oldrow=-1;
    }
    
}
