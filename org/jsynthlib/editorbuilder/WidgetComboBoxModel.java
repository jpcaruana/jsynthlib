package org.jsynthlib.editorbuilder;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.jsynthlib.editorbuilder.widgets.Widget;


public class WidgetComboBoxModel implements ComboBoxModel {
    protected Set widgets;
    
    protected String[] ids = null;
    
    protected int selection = 0;
    
    protected java.util.List listeners = new LinkedList();
    
    protected boolean notifying = false;
    
    WidgetComboBoxModel(Set widgets) {
        update(widgets);
    }
    
    public void update(Set widgets) {
        this.widgets = widgets;
        int oldsize = (ids == null) ? 0 : ids.length;
        ids = new String[widgets.size()];
        Iterator it = widgets.iterator();
        int i = 0;
        while (it.hasNext())
            ids[i++] = ((Widget) it.next()).getId();
        
        QuickSort.sort(ids);
        notifyListeners(Math.max(oldsize, ids.length));
    }
    
    protected void notifyListeners(int index) {
        if (notifying)
            return;
        notifying = true;
        try {
            Iterator it = listeners.iterator();
            while (it.hasNext()) {
                try {
                    ListDataListener l = (ListDataListener) it.next();
                    l.contentsChanged(new ListDataEvent(EditorBuilder
                            .getDesignerFrame(),
                            ListDataEvent.CONTENTS_CHANGED, 0, index));
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        } finally {
            notifying = false;
        }
    }
    
    public void addListDataListener(ListDataListener l) {
        listeners.add(l);
    }
    
    public void removeListDataListener(ListDataListener l) {
        listeners.remove(l);
    }
    
    public int getSize() {
        return ids.length;
    }
    
    public Object getElementAt(int index) {
        return ids[index];
    }
    
    public Object getSelectedItem() {
        if (selection < ids.length)
            return ids[selection];
        return null;
    }
    
    public void setSelectedItem(Object anItem) {
        for (int i = 0; i < ids.length; i++) {
            if (ids[i].equals(anItem))
                selection = i;
        }
    }
    
    public boolean isNotifying() {
        return notifying;
    }
}

