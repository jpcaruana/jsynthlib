package core;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.FocusListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyVetoException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenuBar;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

// addJSLFrameListener should probably be implemented in JSLFrame
// for dynamic switching of type
/**
 * A Frame class which supports JInternalFrame mothods and also can creates a
 * non-internal frame using JFrame. For the details of each method, refer the
 * documentation of JInternalFrame.
 * 
 * @see JSLDesktop
 * @see JSLWindowMenu
 * @see javax.swing.JInternalFrame
 * @see javax.swing.JFrame
 * @author Rib Rdb
 * @author Hiroo Hayashi
 */
public class JSLFrame {
    private JSLFrameProxy proxy;
    /** parent JSLDesktop. */
    private JSLDesktop desktop;

    /**
     * Creates a non-resizable, non-closable, non-maximizable, non-iconifiable
     * JSLFrame with no title.
     */
    public JSLFrame(JSLDesktop desktop) {
        this.desktop = desktop;
	if (JSLDesktop.useMDI())
	    proxy = new JSLIFrame(this);
	else
	    proxy = new JSLJFrame(this);
    }
    /**
     * Creates a JSLFrame with the specified title, resizability, closability,
     * maximizability, and iconifiability.
     */
    public JSLFrame(JSLDesktop desktop, String title, boolean resizable, boolean closable,
		    boolean maximizable, boolean iconifiable) {
        this.desktop = desktop;
	if (JSLDesktop.useMDI())
	    proxy = new JSLIFrame(this, title, resizable, closable, maximizable,
				  iconifiable);
	else
	    proxy = new JSLJFrame(this, title);
    }

    // JInternalFrame compatible methods
    /** Convenience method that moves this component to position 0 if its parent is a JLayeredPane. */
    public void moveToFront() { proxy.moveToFront(); }
    /** Selects or deselects the frame if it's showing. */
    public void setSelected(boolean b)
	    throws java.beans.PropertyVetoException { proxy.setSelected(b); }
    /** Closes this internal frame if the argument is true. */
    public void setClosed(boolean b)
    	    throws java.beans.PropertyVetoException { proxy.setClosed(b); }
    /** If the minimum size has been set to a non-null value just returns it. */
    public Dimension getMinimumSize() { return proxy.getMinimumSize(); }
    /** Returns the content pane for this internal frame. */
    public Container getContentPane() { return proxy.getContentPane(); }
    /** Sets the frame title. */
    public void setTitle(String title) { proxy.setTitle(title); }
    /** Repaints this component. */
    public void repaint() { proxy.repaint(); }
    /** Returns the title of the frame. */
    public String getTitle() { return proxy.getTitle(); }
    /**
     * Adds the specified focus listener to receive focus events from this
     * component when this component gains input focus.
     */
    public void addFocusListener(FocusListener l) { proxy.addFocusListener(l); }
    /** Resizes this component so that it has width width and height height. */
    public void setSize(int w, int h) { proxy.setSize(w, h); }
    /** Returns the size of this component in the form of a Dimension object. */
    public Dimension getSize() { return proxy.getSize(); }
    /** Stores the width/height of this component into "return value" rv and returns rv. */
    public Dimension getSize(Dimension rv) { return proxy.getSize(rv); }
    /** Moves this component to a new location. */
    public void setLocation(int x, int y) { proxy.setLocation(x,y); }
    /** Moves this component to a new location. */
    public void setLocation(Point p) { proxy.setLocation(p); }
    /** Shows or hides this component depending on the value of parameter b. */
    public void setVisible(boolean b) { proxy.setVisible(b); }
    /**
     * Determines whether this component should be visible when its parent is
     * visible.
     */
    public boolean isVisible() { return proxy.isVisible(); }
    /** Causes subcomponents of this frame to be laid out at their preferred size. */
    public void pack() { proxy.pack(); }
    /** Makes this frame invisible, unselected, and closed. */
    public void dispose() { proxy.dispose(); }
    /** Returns the current x coordinate of the components origin. */
    public int getX() { return proxy.getX(); }
    /** Returns the current y coordinate of the components origin. */
    public int getY() { return proxy.getY(); }
    /** Returns whether the frame is the currently "selected" or active frame. */
    public boolean isSelected() { return proxy.isSelected(); }
    /** Returns whether this Window is iconified. */
    public boolean isIcon() { return proxy.isIcon(); }
    /** Returns whether this Window is closing. */
    public boolean isClosing() { return proxy.isClosing(); }
    /** Adds the specified listener to receive frame events from this frame. */
    public void addJSLFrameListener(JSLFrameListener l) {
	proxy.addJSLFrameListener(l);
    }
    /** Returns an array of all the JSLFrameListener added to this frame with addJSLFrameListener. */
    public JSLFrameListener[] getJSLFrameListeners() {
	return proxy.getJSLFrameListeners();
    }
    /** Removes the specified frame listener so that it no longer receives frame events from this frame. */
    public void removeJSLFrameListener(JSLFrameListener l) {
	proxy.removeJSLFrameListener(l);
    }
    /** Sets the menuBar property for this JInternalFrame. */
    public void setJMenuBar(JMenuBar m) { proxy.setJMenuBar(m); }

    // original (non-JInternalFrame compatible) methods
    /** Move the frame to default location. */
    public void moveToDefaultLocation() {
        setLocation(desktop.getDefaultLocation(getSize()));
    }

    // only for Actions.PasteAction
    public boolean canImport(DataFlavor[] flavors) {
	return false;
    }
    // mothods for JSLDeskTop
    /** Returns JFrame object.  Only for SDI mode. */
    JFrame getJFrame() {
	return (JFrame) proxy;
    }
    /** Returns JInternalFrame object.  Only for MDI mode. */
    JInternalFrame getJInternalFrame() {
        return (JInternalFrame) proxy;
    }

    interface JSLFrameProxy {
	public void moveToFront();
	public void setSelected(boolean b)
	    throws java.beans.PropertyVetoException;
	public void setClosed(boolean b)
	    throws java.beans.PropertyVetoException;
	public Container getContentPane();
	public void setTitle(String title);
	public void repaint();
	public String getTitle();
	public void addJSLFrameListener(JSLFrameListener l);
	public void setJMenuBar(JMenuBar m);
	public JSLFrame getJSLFrame();
	public void addFocusListener(FocusListener l);
	public void setSize(int w, int h);
	public Dimension getSize();
	public Dimension getSize(Dimension rv);
	public Dimension getMinimumSize();
	public void pack();
	//public void show();
	public void dispose();
	public int getX();
	public int getY();
	public boolean isVisible();
	public void setVisible(boolean b);
	public void setLocation(int x, int y);
	public void setLocation(Point p);
	public JSLFrameListener[] getJSLFrameListeners();
	public void removeJSLFrameListener(JSLFrameListener l);
	//public void setPreferredSize(Dimension d);
	public boolean isSelected();
	public boolean isShowing();
	public boolean isIcon();
	public boolean isClosing();
    }

    /** use JInternalFrame for MDI (Multiple Document Interface) mode. */
    class JSLIFrame extends JInternalFrame implements JSLFrameProxy,
						       InternalFrameListener {
	private WeakReference parent;
	protected ArrayList listeners = new ArrayList();
	public JSLIFrame(JSLFrame p) {
	    super();
	    parent = new WeakReference(p);
	    addInternalFrameListener(this);
	}
	public JSLIFrame(JSLFrame p, String title, boolean resizable, boolean closable, boolean maximizable, boolean iconifiable) {
	    super(title, resizable, closable, maximizable, iconifiable);
	    parent = new WeakReference(p);
	    addInternalFrameListener(this);
	}
	public void setJMenuBar(JMenuBar m) {}
	public JSLFrame getJSLFrame() {
	    return (JSLFrame)parent.get();
	}
	public void addJSLFrameListener(JSLFrameListener l) {
	    listeners.add(l);
	}
	public void setVisible(boolean b) {
	    ErrorMsg.reportStatus(ErrorMsg.FRAME, "setVisible : " + getTitle());
	    super.setVisible(b);
	    try {
                setSelected(b);
            } catch (PropertyVetoException e) {
                // don't know how this exception occurs
                e.printStackTrace();
            }
	}

	public void internalFrameActivated(InternalFrameEvent e) {
	    ErrorMsg.reportStatus(ErrorMsg.FRAME,
	            "\"" + getTitle() + "\" activated.");
	    JSLFrameEvent fe =
		new JSLFrameEvent(getJSLFrame(), JSLFrameEvent.ACTIVATED);
	    Iterator it = listeners.iterator();
	    while (it.hasNext()) {
		((JSLFrameListener)it.next()).JSLFrameActivated(fe);
	    }
	}
	public void internalFrameClosed(InternalFrameEvent e) {
	    JSLFrameEvent fe =
		new JSLFrameEvent(getJSLFrame(), JSLFrameEvent.CLOSED);
	    Iterator it = listeners.iterator();
	    while (it.hasNext()) {
		((JSLFrameListener)it.next()).JSLFrameClosed(fe);
	    }
	}
	public void internalFrameClosing(InternalFrameEvent e) {
	    JSLFrameEvent fe =
		new JSLFrameEvent(getJSLFrame(), JSLFrameEvent.CLOSING);
	    Iterator it = listeners.iterator();
	    while (it.hasNext()) {
		((JSLFrameListener)it.next()).JSLFrameClosing(fe);
	    }
	    if (!fe.isConsumed())
		proxy.dispose();
	}
	public void internalFrameDeactivated(InternalFrameEvent e) {
	    ErrorMsg.reportStatus(ErrorMsg.FRAME,
	            "\"" + getTitle() + "\" deactivated.");
	    JSLFrameEvent fe =
		new JSLFrameEvent(getJSLFrame(), JSLFrameEvent.DEACTIVATED);
	    Iterator it = listeners.iterator();
	    while (it.hasNext()) {
		((JSLFrameListener)it.next()).JSLFrameDeactivated(fe);
	    }
	}
	public void internalFrameDeiconified(InternalFrameEvent e) {
	    JSLFrameEvent fe =
		new JSLFrameEvent(getJSLFrame(), JSLFrameEvent.DEICONIFIED);
	    Iterator it = listeners.iterator();
	    while (it.hasNext()) {
		((JSLFrameListener)it.next()).JSLFrameDeiconified(fe);
	    }
	}
	public void internalFrameIconified(InternalFrameEvent e) {
	    JSLFrameEvent fe =
		new JSLFrameEvent(getJSLFrame(), JSLFrameEvent.ICONIFIED);
	    Iterator it = listeners.iterator();
	    while (it.hasNext()) {
		((JSLFrameListener)it.next()).JSLFrameIconified(fe);
	    }
	}
	public void internalFrameOpened(InternalFrameEvent e) {
	    JSLFrameEvent fe =
		new JSLFrameEvent(getJSLFrame(), JSLFrameEvent.OPENED);
	    Iterator it = listeners.iterator();
	    while (it.hasNext()) {
		((JSLFrameListener)it.next()).JSLFrameOpened(fe);
	    }
	}
	public JSLFrameListener[] getJSLFrameListeners() {
	    JSLFrameListener[] type = new JSLFrameListener[0];
	    return (JSLFrameListener[])listeners.toArray(type);
	}
	public void removeJSLFrameListener(JSLFrameListener l) {
	    listeners.remove(l);
	}
	public boolean isClosing() {
	    return false;
	}

    }

    /** use JFrame for SDI (Single Document Interface) mode. */
    class JSLJFrame extends JFrame implements JSLFrameProxy, WindowListener {
	private WeakReference parent;
	protected ArrayList listeners = new ArrayList();
	private boolean closing = false;

	public JSLJFrame(JSLFrame p) {
	    super();
	    addWindowListener(this);
	    parent = new WeakReference(p);
	    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}
	public JSLJFrame(JSLFrame p, String title) {
	    super(title);
	    addWindowListener(this);
	    parent = new WeakReference(p);
	    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}
	public JSLFrame getJSLFrame() { return (JSLFrame) parent.get(); }
	public void moveToFront() { toFront(); }
	public void setSelected(boolean b) { if (!isFocused() && b) toFront(); }
	public void setClosed(boolean b) {
	    if (b)
		processWindowEvent(new WindowEvent(this,
						   WindowEvent.WINDOW_CLOSING));
	}

	public void addJSLFrameListener(JSLFrameListener l) {
	    listeners.add(l);
	}

	public void setVisible(boolean b) {
	    // deiconified
	    if (b && isIcon())
	        setExtendedState(getExtendedState() & ~ICONIFIED);
	    super.setVisible(b);
	}
	private void showState(String s) {
	    ErrorMsg.reportStatus(ErrorMsg.FRAME, "\"" + this.getTitle() 
	            + "\" " + s + " (" + this.getExtendedState() + ")");
	}
	public void windowActivated(WindowEvent e) {
	    showState("activated");
            JSLFrameEvent fe =
                new JSLFrameEvent(getJSLFrame(), JSLFrameEvent.ACTIVATED);
	    Iterator it = listeners.iterator();
	    while (it.hasNext()) {
		((JSLFrameListener)it.next()).JSLFrameActivated(fe);
	    }
	}
	public void windowClosed(WindowEvent e) {
	    showState("closed");
	    JSLFrameEvent fe =
		new JSLFrameEvent(getJSLFrame(), JSLFrameEvent.CLOSED);
	    Iterator it = listeners.iterator();
	    while (it.hasNext()) {
		((JSLFrameListener)it.next()).JSLFrameClosed(fe);
	    }
	    closing = false;
	}
	public void windowClosing(WindowEvent e) {
	    showState("closing");
	    closing = true;
	    JSLFrameEvent fe =
		new JSLFrameEvent(getJSLFrame(), JSLFrameEvent.CLOSING);
	    Iterator it = listeners.iterator();
	    while (it.hasNext()) {
		((JSLFrameListener)it.next()).JSLFrameClosing(fe);
	    }
	    //if (!fe.isConsumed())
	    //	proxy.dispose();
	}
	public boolean isClosing() {
	    return closing;
	}

	public void windowDeactivated(WindowEvent e) {
	    showState("deactivated");
	    JSLFrameEvent fe =
		new JSLFrameEvent(getJSLFrame(), JSLFrameEvent.DEACTIVATED);
	    Iterator it = listeners.iterator();
	    while (it.hasNext()) {
		((JSLFrameListener)it.next()).JSLFrameDeactivated(fe);
	    }
	}
	public void windowDeiconified(WindowEvent e) {
	    showState("deiconified");
	    JSLFrameEvent fe =
		new JSLFrameEvent(getJSLFrame(), JSLFrameEvent.DEICONIFIED);
	    Iterator it = listeners.iterator();
	    while (it.hasNext()) {
		((JSLFrameListener)it.next()).JSLFrameDeiconified(fe);
	    }
	}
	public void windowIconified(WindowEvent e) {
	    showState("iconified");
	    JSLFrameEvent fe =
		new JSLFrameEvent(getJSLFrame(), JSLFrameEvent.ICONIFIED);
	    Iterator it = listeners.iterator();
	    while (it.hasNext()) {
		((JSLFrameListener)it.next()).JSLFrameIconified(fe);
	    }
	}
	public void windowOpened(WindowEvent e) {
	    showState("opened");
	    JSLFrameEvent fe =
		new JSLFrameEvent(getJSLFrame(), JSLFrameEvent.OPENED);
	    Iterator it = listeners.iterator();
	    while (it.hasNext()) {
		((JSLFrameListener)it.next()).JSLFrameOpened(fe);
	    }
	}
	public JSLFrameListener[] getJSLFrameListeners() {
	    JSLFrameListener[] type = new JSLFrameListener[0];
	    return (JSLFrameListener[])listeners.toArray(type);
	}
	public void removeJSLFrameListener(JSLFrameListener l) {
	    listeners.remove(l);
	}
	public boolean isSelected() {
	    return (desktop.getSelectedFrame().getJFrame() == this);
	}
	public boolean isIcon() {
	    return (getExtendedState() & Frame.ICONIFIED) != 0;
	}

	void fakeActivate() {
            WindowEvent we = new WindowEvent(this,
                    WindowEvent.WINDOW_ACTIVATED, null);
            processWindowEvent(we);
        }
    }
}

