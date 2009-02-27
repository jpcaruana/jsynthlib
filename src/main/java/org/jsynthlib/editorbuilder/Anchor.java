package org.jsynthlib.editorbuilder;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import javax.swing.Spring;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.SpringLayout.Constraints;

import org.jsynthlib.editorbuilder.widgets.AnchoredWidget;
import org.jsynthlib.editorbuilder.widgets.ContainerWidget;
import org.jsynthlib.editorbuilder.widgets.Widget;

public class Anchor implements Cloneable {
    public static final int NORTH = 0;
    public static final int SOUTH = 1;
    public static final int EAST = 2;
    public static final int WEST = 3;
    /** Don't use this one. It is only used for determining how to 
     *  layout components. Anchors cannot connect to the center of 
     *  a component.
     **/
    static final int CENTER = 4;
    
    static final int ANCHORED = 0;
    static final int UNANCHORED = 1;
    static final int MAYBE_ANCHORED = 2;
    
    protected static final String[] SIDES = new String[] {
            SpringLayout.NORTH,
            SpringLayout.SOUTH,
            SpringLayout.EAST,
            SpringLayout.WEST,
    };
    
    protected Widget cWidget;
    protected int cSide;
    protected Widget tWidget;
    protected String targetId;
    protected int tSide;
    protected Spring padding;
    protected Collection listeners = new LinkedList();
    protected int status;
    protected boolean side_editable = true;
    
    public Anchor(Widget c, int cs,
            Widget t, int ts, int _padding) {
        this(c, cs, t, ts, Spring.constant(_padding));
    }
    
    public Anchor(Widget c, int cs,
            Widget t, int ts, Spring _padding) {
        this();
        setTargetSide(ts);
        setTarget(t);
        setPadding(_padding);
        setSide(cs);
        setWidget(c);
    }

    public void setSide(int cs) {
        if (!side_editable)
            return;
        if (cs < 0 || cs >= CENTER)
            throw new IllegalArgumentException();
        int old = cSide;
        cSide = cs;
        apply();
        notifyListeners(new AnchorChangeEvent(this, AnchorChangeEvent.CONSTRAINED_SIDE, old));
    }
    
    public void setWidget(Widget c) {
        Widget old = cWidget;
        cWidget = c;
        apply();
        notifyListeners(new AnchorChangeEvent(this, AnchorChangeEvent.CONSTRAINED_WIDGET, old));
        addAnchorListener((AnchoredWidget)c);
    }
    
    private void apply() {
        if (cWidget != null) {
            Container parent = container();
            if (parent == null)
                return;
            SpringLayout l = (SpringLayout)parent.getLayout();
            Constraints c = l.getConstraints(cWidget);
            c.setConstraint(SIDES[opposite(cSide)], null);
            c.setConstraint(SIDES[cSide], getSpring());
            parent.invalidate();
        }
    }

    public Anchor() {
        status = MAYBE_ANCHORED;
    }
    
    public Anchor(Widget c, int side) {
        this();
        setSide(side);
        setTargetSide(side);
        setWidget(c);
        setPadding(0);
    }
    
    public int getPadding() { return padding.getValue(); }
    public Spring getPaddingSpring() { return padding; }
    public void setPadding(int p) { setPadding ( Spring.constant(p) ); }
    public void setPadding(Spring p) {
        Spring oldPadding = padding;
        padding = p;
        apply();
        notifyListeners(new AnchorChangeEvent(this, oldPadding));
    }
    public Widget getWidget() { return cWidget; }
    public int getSide() { return cSide; }
    public Widget getTarget() {
        if (tWidget == null && targetId != null) {
            Widget w = ((GlassPane)EditorBuilder.getDesignerFrame().getGlassPane()).getWidget(targetId);
            if (w != null) {
                setTarget(w);
                targetId = null;
            }
        }
        return tWidget;
    }
    public void setTarget(Widget c) {
        if (c == null)
            return;
        if (c == tWidget && side_editable)
            return;
        if (validTarget(c, tSide)) {
            Widget old = tWidget; 
            tWidget = c;
            apply();
            notifyListeners(new AnchorChangeEvent(this, AnchorChangeEvent.TARGET_WIDGET, old));
        }
    }
    private Spring getSpring() {
        SpringLayout l = (SpringLayout) container().getLayout();
        return Spring.sum(l.getConstraint(SIDES[tSide], getTarget()),
                          padding);
    }

    public int getTargetSide() { return tSide; }
    public void setTargetSide(int s) {
        if (tSide == s)
            return;
        if (s < 0 || s >= CENTER)
            throw new IllegalArgumentException();
        if (tWidget == null || validTarget(tWidget, s)) {
            int old = tSide;
            tSide = s; 
            apply();
            notifyListeners(new AnchorChangeEvent(this, AnchorChangeEvent.TARGET_SIDE, old));
        }
    }


    private boolean validTarget(Widget target, int s) {
        if (getWidget() == null)
            return true;
        if (target == container() &&
                (s == NORTH || s == WEST)) {
            status = ANCHORED;
            return true;
        }
        int oldStatus = status;
        status = UNANCHORED;
        boolean result = targetAnchor(target, s).isAnchored();
        status = oldStatus;
        return result;
    }

    public static int opposite (int side) {
        if (side % 2 == 0)
            return side + 1;
        else
            return side - 1;
    }
    
    protected Point getCenter(Component component, int side) {
        if (side < 0 || side > 3)
            throw new Error(side + " is an invalid side.");
        
        int x = 0, y = 0;
        
        if (side < 2) {
            x += component.getWidth()/2;
            if (side == 1)
                y += component.getHeight();
        } else {
            y += component.getHeight()/2;
            if (side == 2)
                x += component.getWidth();
        }
        Point result = new Point(x, y);
        SwingUtilities.convertPointToScreen(result,component);
        return result;
    }
        
    // Code for keeping track of all the anchors.
    
    public void drawConnector(Component c, Graphics g) {
        Graphics2D g2d = (Graphics2D)g;
        Point ptA = getCenter(cWidget, cSide);
        Point ptB = getCenter(getTarget(), tSide);
        
        SwingUtilities.convertPointFromScreen(ptA, c);
        SwingUtilities.convertPointFromScreen(ptB, c);
        
        int[] xpoints = new int[] { ptA.x, ptA.x, ptB.x, ptB.x };
        int[] ypoints = new int[] { ptA.y, ptA.y, ptB.y, ptB.y };
        
        if (cSide < EAST) { // vertical
            ypoints[1] = ypoints[2] = ptA.y + (ptB.y - ptA.y)/2;
        } else {
            xpoints[1] = xpoints[2] = ptA.x + (ptB.x - ptA.x)/2;
        }
        
        Stroke s = g2d.getStroke();
        Color color = g2d.getColor();
        g2d.setStroke(new BasicStroke(3));
        g2d.setColor(Color.blue);
        g2d.drawPolyline(xpoints, ypoints, 4);
        g2d.setColor(color);
        g2d.setStroke(s);
        
        /*    ptA = SwingUtilities.convertPoint(source, ptA, destination);
         ptB = SwingUtilities.convertPoint(source, ptB, destination);*/
    }
    public void addAnchorListener(AnchorListener l) {
        listeners.add(l);
    }
    private void notifyListeners(AnchorChangeEvent e) {
        Iterator it = listeners.iterator();
        while (it.hasNext()) {
            AnchorListener l = (AnchorListener) it.next();
            l.anchorChanged(e);
        }
    }
    public void setSideEditable(boolean b) {
        side_editable = b;
    }
    public boolean isSideEditable() {
        return side_editable;
    }
    protected boolean isAnchored() {
        if (status == ANCHORED)
            return true;
        if (status == UNANCHORED)
            return false;
        return targetAnchor(getTarget(), tSide).isAnchored();
    }

    private Anchor targetAnchor(Widget target, int side) {
        if (target == null)
            System.out.println("Debug me!");
        switch (side) {
            case EAST:
            case WEST:
                return ((AnchoredWidget)target).getEWAnchor(container());
            case NORTH:
            case SOUTH:
                return ((AnchoredWidget)target).getNSAnchor(container());
        }
        return null;
    }
    
    public Set getTargets() {
        Set result = new HashSet();
        Container c = container();
        result.add(c);
        if (c instanceof ContainerWidget) {
            Widget[] siblings = ((ContainerWidget)c).getWidgets();
            for (int i = 0; i < siblings.length; i++)
                if (validTarget(siblings[i], cSide))
                    result.add(siblings[i]);
        }
        return result;
    }
    
    private Container container() {
        if (cWidget == null)
            return null;
        if (side_editable)
            return cWidget.getParent();
        else
            return cWidget;
    }
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
