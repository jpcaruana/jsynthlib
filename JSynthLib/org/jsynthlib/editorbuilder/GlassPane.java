package org.jsynthlib.editorbuilder;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import javax.swing.ComboBoxModel;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.Spring;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.jsynthlib.editorbuilder.widgets.AnchoredWidget;
import org.jsynthlib.editorbuilder.widgets.ButtonWidget;
import org.jsynthlib.editorbuilder.widgets.LabelWidget;
import org.jsynthlib.editorbuilder.widgets.LookupParameterWidget;
import org.jsynthlib.editorbuilder.widgets.PanelWidget;
import org.jsynthlib.editorbuilder.widgets.RangeParameterWidget;
import org.jsynthlib.editorbuilder.widgets.StringParameterWidget;
import org.jsynthlib.editorbuilder.widgets.Strut;
import org.jsynthlib.editorbuilder.widgets.Widget;

public class GlassPane extends JPanel implements DropTargetListener,
        InvocationHandler, java.io.Serializable {

    protected DesignerFrame designer = EditorBuilder.getDesignerFrame();

    protected int default_padding = 6;

    protected static Widget[] wa = null;

    protected static HashMap wids = null;

    // static for WidgetComboBoxModel
    protected static Set containers = new HashSet();

    protected static Set widgets = new HashSet();

    protected Set mywidgets = null;

    protected Set mycontainers = null;

    public GlassPane() {
        setOpaque(false);
        new DropTarget(this, DnDConstants.ACTION_LINK, this);
        Object proxy = Proxy.newProxyInstance(MouseListener.class
                .getClassLoader(), new Class[] { MouseListener.class,
                KeyListener.class }, this);
        addMouseListener((MouseListener) proxy);
        addKeyListener((KeyListener) proxy);
    }

    public GlassPane(DesignerFrame _designer) {
        this();
        designer = _designer;
        mywidgets = widgets;
        mycontainers = containers;
    }

    public int getDefaultPadding() {
        return default_padding;
    }

    public void setDefaultPadding(int _padding) {
        default_padding = _padding;
    }

    public void dragEnter(DropTargetDragEvent e) {
    }

    public void dragExit(DropTargetEvent e) {
    }

    public void dragOver(DropTargetDragEvent e) {
    }

    public void dropActionChanged(DropTargetDragEvent e) {
    }

    public void drop(DropTargetDropEvent e) {
        if (e.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            e.acceptDrop(DnDConstants.ACTION_LINK);
            try {
                Transferable tr = e.getTransferable();
                String id = (String) tr
                        .getTransferData(DataFlavor.stringFlavor);
                e.dropComplete(addComponent(id, e.getLocation()));
            } catch (Exception ex) {
                ex.printStackTrace();
                e.dropComplete(false);
            }
        } else {
            e.rejectDrop();
        }
    }

    protected boolean addComponent(String id, Point p) {
        Container c = getContainer(p);
        if (!c.contains(p))
            return false;
        Widget component = null;
        if (id.startsWith("Strut")) {
            Strut s = new Strut(p.x, p.y);
            ((SpringLayout) c.getLayout()).addLayoutComponent(s, s.getConstraints());
            component = s;
        } else if (id.startsWith("Button"))
            component = new ButtonWidget();
        else if (id.startsWith("Label"))
            component = new LabelWidget();
        else if (id.startsWith("Panel")) {
            component = new PanelWidget();
            //containers.add(component);
            Strut s = new Strut(92, 0, 8, 100);
            addWidget(s);
            component.add(s);
            ((SpringLayout) component.getLayout()).addLayoutComponent(s, s
                    .getConstraints());
            s = new Strut(0, 92, 100, 8);
            addWidget(s);
            component.add(s);
            ((SpringLayout) component.getLayout()).addLayoutComponent(s, s
                    .getConstraints());
            s = new Strut(0, 0, default_padding, default_padding);
            addWidget(s);
            component.add(s);
            ((SpringLayout) component.getLayout()).addLayoutComponent(s, s
                    .getConstraints());
        } else {
            Parameter pm = ParameterFrame.getParameter(id);
            switch (pm.getType()) {
                case Parameter.STRING:
                    component = new StringParameterWidget(pm);
                    break;
                case Parameter.LOOKUP:
                    component = new LookupParameterWidget(pm);
                    break;
                case Parameter.RANGE:
                    component = new RangeParameterWidget(pm);
                    break;
                default:
                    throw new Error("Unknown parameter type " + pm);
            }

        }

        c.add(component);
        if (component instanceof AnchoredWidget)
            setConstraints(component, p, c);
        addWidget(component);
        for (;;) {
            c.validate();
            if (c == designer.getRootWidget())
                break;
            c = c.getParent();
        }
        //component.validate();
        designer.validate();
        //component.repaint();
        designer.repaint();

        //Anchor.printCmap();

        return true;

    }

    protected void setConstraints(JComponent component, Point p, Container c) {
        Component parent;
        int xside, yside;
        SpringLayout layout = (SpringLayout) c.getLayout();

        parent = c.getComponentAt(p);
        if (parent != c && parent != null) {
            // Dropped on a component -- it's the parent.
            yside = Anchor.CENTER;
            if (p.x - parent.getX() < parent.getWidth() / 2)
                xside = Anchor.WEST;
            else
                xside = Anchor.EAST;
        } else {
            // Find parents
            Component[] ca = c.getComponents();
            parent = null;
            for (int i = 0; i < ca.length; i++) {
                if (ca[i] != component && ca[i].getX() <= p.x
                        && ca[i].getY() <= p.y) {
                    if (parent == null || ca[i].getX() > parent.getX()
                            || ca[i].getY() > parent.getY())
                        parent = ca[i];
                }
            }
            if (parent == null)
                parent = c;

            // Find sides
            if (parent.getX() + parent.getWidth() > p.x) {
                xside = Anchor.CENTER;
            } else {
                xside = Anchor.EAST;
            }
            if (parent.getY() + parent.getHeight() > p.y) {
                yside = Anchor.CENTER;
            } else {
                yside = Anchor.SOUTH;
            }

        }
        if (parent == c) {
            new Anchor(component, Anchor.NORTH, c, Anchor.NORTH,
                    default_padding * 2);
            new Anchor(component, Anchor.WEST, c, Anchor.WEST,
                    default_padding * 2);
        } else {
            createNSAnchor(component, parent, yside);
            createEWAnchor(component, parent, xside);
        }
    }

    public void createNSAnchor(Component c, Component target, int side) {
        if (side == Anchor.CENTER) {
            new Anchor(c, Anchor.NORTH, target, Anchor.NORTH, 0);
            return;
        }
        Anchor ta = Anchor.getNSAnchor(target);
        if (ta != null && ta.getConstrainedSide() == side) {
            Component new_target = ta.getTargetComponent();
            int target_side = ta.getTargetSide();

            new Anchor(c, side, new_target, target_side, default_padding);

            ta.setTargetSide(Anchor.opposite(side));
            ta.setTargetComponent(c);
            if (new_target != target.getParent()) {
                Iterator it = Anchor.getSet(new_target, target_side).iterator();
                while (it.hasNext()) {
                    Anchor a = ((Anchor) it.next());
                    if (a.getConstrainedComponent() != c) {
                        it.remove();
                        a.setTargetComponent(c);
                    }
                }
            }
        } else {
            new Anchor(c, Anchor.opposite(side), target, side, default_padding);

            if (ta != null) {
                Iterator it = Anchor.getSet(target, side).iterator();
                while (it.hasNext()) {
                    Anchor a = ((Anchor) it.next());
                    if (a.getConstrainedComponent() != c) {
                        it.remove();
                        a.setTargetComponent(c);
                    }
                }
            }
        }
    }

    public void createEWAnchor(Component c, Component target, int side) {
        if (side == Anchor.CENTER) {
            new Anchor(c, Anchor.WEST, target, Anchor.WEST, 0);
            return;
        }
        Anchor ta = Anchor.getEWAnchor(target);
        if (ta != null && ta.getConstrainedSide() == side) {
            Component new_target = ta.getTargetComponent();
            int target_side = ta.getTargetSide();
            Spring padding = ta.getPaddingSpring();

            new Anchor(c, side, new_target, target_side, padding);

            ta.setTargetSide(Anchor.opposite(side));
            ta.setTargetComponent(c);
            ta.setPadding(default_padding);
            if (new_target != target.getParent()) {
                Iterator it = Anchor.getSet(new_target, target_side).iterator();
                while (it.hasNext()) {
                    Anchor a = ((Anchor) it.next());
                    if (a.getConstrainedComponent() != c) {
                        it.remove();
                        a.setTargetComponent(c);
                    }
                }
            }
        } else {
            new Anchor(c, Anchor.opposite(side), target, side, default_padding);

            if (ta != null) {
                Iterator it = Anchor.getSet(target, side).iterator();
                while (it.hasNext()) {
                    Anchor a = ((Anchor) it.next());
                    if (a.getConstrainedComponent() != c) {
                        it.remove();
                        a.setTargetComponent(c);
                    }
                }
            }
        }
    }

    protected Container getContainer(Point p) {
        Container c = designer.getRootWidget();
        Point new_point = SwingUtilities.convertPoint(this, p, c);
        Component new_container = c.getComponentAt(new_point);
        // find correct container and update p to new coordinates.
        while (new_container != null && new_container != c
                && containers.contains(new_container)) {
            new_point = SwingUtilities
                    .convertPoint(c, new_point, new_container);
            c = (Container) new_container;
            new_container = c.getComponentAt(new_point);
        }
        p.x = new_point.x;
        p.y = new_point.y;
        return c;
    }

    protected Point getOffset(Component c) {
        Container myparent = getParent(), parent = c.getParent();
        Point p = new Point();
        p.x = c.getX() - getX();
        p.y = c.getY() - getY();
        while (myparent != parent) {
            p.x += parent.getX();
            p.y += parent.getY();
            parent = parent.getParent();
        }
        return p;
    }

    public void paint(Graphics g) {
        try {
            Component selected = (Component) designer.getSelectedWidget();
            if (selected != null) {
                // todo: clip to viewport
                Anchor a = Anchor.getNSAnchor(selected);
                if (a != null)
                    a.drawConnector(this, g);
                a = Anchor.getEWAnchor(selected);
                if (a != null)
                    a.drawConnector(this, g);

                Point origin = SwingUtilities.convertPoint(
                        selected.getParent(), new Point(0, 0), this);

                ((Graphics2D) g).translate(origin.x, origin.y);

                Point p = selected.getLocation();
                Dimension d = selected.getSize();
                // todo: get a color from l&f
                g.setColor(new Color(50, 50, 255, 128));
                g.fillRect(p.x, p.y, d.width, d.height);

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {
        if (method.getDeclaringClass() == java.lang.Object.class)
            return method.invoke(this, args);

        if (args.length == 1 && args[0] instanceof MouseEvent) {
            javax.swing.JScrollPane sp = designer.getScrollPane();
            MouseEvent e = (MouseEvent) args[0];
            if (!sp.getViewport().contains(
                    SwingUtilities.convertPoint(this, e.getPoint(), sp))) {
                Point p = SwingUtilities.convertPoint(this, e.getPoint(),
                        designer.getLayeredPane());
                Component c = SwingUtilities.getDeepestComponentAt(designer
                        .getLayeredPane(), p.x, p.y);
                if (c == null)
                    return null;
                c.dispatchEvent(SwingUtilities.convertMouseEvent(this, e, c));
            } else if (method.getName().equals("mouseClicked")) {
                select(e.getPoint());
            }
        } else if (args.length == 1 && args[0] instanceof KeyEvent) {
            KeyEvent e = (KeyEvent) args[0];
            try {
                Component selected = (Component) designer.getSelectedWidget();
                if (selected != null
                        && (e.getKeyCode() == KeyEvent.VK_DELETE || e
                                .getKeyCode() == KeyEvent.VK_BACK_SPACE)) {
                    selected.getParent().remove(selected);
                    removeWidget((Widget) selected);
                    designer.setSelectedWidget(null);
                    repaint();
                }
            } catch (Exception ex) {
            }
        }
        return null;
    }

    protected void select(Point p) {
        Component c = getContainer(p).getComponentAt(p);
        if (c == designer.getRootWidget())
            designer.setSelectedWidget(null);
        else {
            designer.setSelectedWidget((Widget) c);
            requestFocus();
        }
        repaint();
    }

    public Widget getWidget(String id) {
        Iterator it = widgets.iterator();
        while (it.hasNext()) {
            Widget w = (Widget) it.next();
            if (w.getId().equals(id))
                return w;
        }
        return null;
    }

    public void addWidget(Widget w) {
        widgets.add(w);
        if (w instanceof PanelWidget)
            containers.add(w);
        WidgetComboBoxModel.update();
    }

    public void removeWidget(Widget w) {
        widgets.remove(w);
        WidgetComboBoxModel.update();
    }

    public ComboBoxModel newWidgetComboBoxModel() {
        return new WidgetComboBoxModel(this);
    }

    public static void updateWidgetLists() {
        WidgetComboBoxModel.update();
    }

    // Nothing but WidgetComboBoxModel should call this
    static Set getCommonWidgets() {
        return widgets;
    }

    public static Widget[] getWidgetArray() {
        PanelWidget ifw = EditorBuilder.getDesignerFrame().getRootWidget();
        widgets.remove(ifw);
        wa = (Widget[]) widgets.toArray(new Widget[] {});
        widgets.add(ifw);
        wids = new HashMap();
        for (int i = 0; i < wa.length; i++)
            wids.put(wa[i], "" + i);
        return wa;
    }

    public static String getWidgetIdentifier(Widget w) {
        if (wids == null)
            return w.getId();
        else
            return "widgets[" + (String) wids.get(w) + "]";
    }

    public static String getPatchIdentifier() {
        return "p";
    }

    /*  public Set getWidgets() { return mywidgets; }
     public void setWidgets(Set set) {
     mywidgets = widgets = set;
     WidgetComboBoxModel.update();
     }
     public Set getContainers() { return mycontainers; }
     public void setContainers(Set set) {
     mycontainers = containers = set;
     WidgetComboBoxModel.update();
     }*/
    public static boolean isWidgetModelNotifying() {
        return WidgetComboBoxModel.isNotifying();
    }
}
// should be protected inner class of the object holder

class WidgetComboBoxModel implements ComboBoxModel {
    protected static Set widgets = GlassPane.getCommonWidgets();

    protected static String[] ids = null;

    protected int selection = 0;

    protected static java.util.List listeners = new LinkedList();

    protected static boolean notifying = false;

    WidgetComboBoxModel(GlassPane gp) {
        if (ids == null)
            update();
    }

    public static void update() {
        int oldsize = (ids == null) ? 0 : ids.length;
        ids = new String[widgets.size()];
        Iterator it = widgets.iterator();
        int i = 0;
        while (it.hasNext())
            ids[i++] = ((Widget) it.next()).getId();

        QuickSort.sort(ids);
        notifyListeners(Math.max(oldsize, ids.length));
    }

    protected static void notifyListeners(int index) {
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

    public static boolean isNotifying() {
        return notifying;
    }
}