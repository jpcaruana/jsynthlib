package core;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.util.*;
import java.lang.ref.WeakReference;
import java.awt.datatransfer.DataFlavor;

// addJSLFrameListener should probably be implemented in JSLFrame
// for dynamic switching of type
public class JSLFrame {
    protected JSLFrameProxy proxy;
    private static boolean useIFrames =
	(PatchEdit.appConfig.getGuiStyle() == 0);

    // for JSLJFrame
    private static JFrame lastselection = null;
    private static int frame_count = 0;


    public JSLFrame() {
	if (useIFrames)
	    proxy = new JSLIFrame(this);
	else
	    proxy = new JSLJFrame(this);
	if (JSLDesktop.getInstance() != null)
	    JSLDesktop.registerFrame(this);
	frame_count++;
    }
    public JSLFrame(String s, boolean resizable, boolean closable,
		    boolean maximizable, boolean iconifiable) {
	if (useIFrames)
	    proxy = new JSLIFrame(this,s, resizable, closable, maximizable,
				  iconifiable);
	else
	    proxy = new JSLJFrame(this, s);
	if (JSLDesktop.getInstance() != null)
	    JSLDesktop.registerFrame(this);
	frame_count++;
    }
    static void resetFrameCount() { frame_count = 0; }
    JFrame getJFrame() {
	if (proxy instanceof JFrame)
	    return (JFrame)proxy;
	else
	    return null;
    }
    public void moveToFront() { proxy.moveToFront(); }
    public void setSelected(boolean b)
	    throws java.beans.PropertyVetoException { proxy.setSelected(b); }
    public void setClosed(boolean b)
    	    throws java.beans.PropertyVetoException { proxy.setClosed(b); }
    public void setPreferredSize(Dimension d) { proxy.setPreferredSize(d); }
    public Dimension getMinimumSize() { return proxy.getMinimumSize(); }
    public Container getContentPane() { return proxy.getContentPane(); }
    public void setTitle(String title) { proxy.setTitle(title); }
    public void repaint() { proxy.repaint(); }
    public String getTitle() { return proxy.getTitle(); }
    public void addFocusListener(FocusListener l) {proxy.addFocusListener(l);}
    public void setSize(int w, int h) { proxy.setSize(w, h); }
    public Dimension getSize() { return proxy.getSize(); }
    public Dimension getSize(Dimension rv) { return proxy.getSize(rv); }
    public void setLocation(int x, int y) { proxy.setLocation(x,y); }
    public void setVisible(boolean b) { proxy.setVisible(b); }
    public boolean isVisible() { return proxy.isVisible(); }
    public void show() { proxy.show(); }
    public void pack() { proxy.pack(); }
    public void dispose() { proxy.dispose(); }
    public int getX() { return proxy.getX(); }
    public int getY() { return proxy.getY(); }
    public void reshape(int a, int b, int c, int d) { proxy.reshape(a,b,c,d); }
    public boolean isSelected() { return proxy.isSelected(); }
    // This is probably naughty.
    public JSLDesktop getDesktopPane() { return JSLDesktop.getInstance(); }
    public JSLFrameListener[] getJSLFrameListeners() {
	return proxy.getJSLFrameListeners();
    }
    public void removeJSLFrameListener(JSLFrameListener l) {
	proxy.removeJSLFrameListener(l);
    }
    public void addJSLFrameListener(JSLFrameListener l) {
	proxy.addJSLFrameListener(l);
    }
    public void setJMenuBar(JMenuBar m) { proxy.setJMenuBar(m); }

    static boolean useInternalFrames() { return useIFrames; }

    JSLFrameProxy getProxy() { return proxy; }

    public void moveToDefaultLocation() { proxy.moveToDefaultLocation(); }

    public boolean canImport(DataFlavor[] flavors) {
	return false;
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
	public void show();
	public void dispose();
	public int getX();
	public int getY();
	public boolean isVisible();
	public void reshape(int a, int b, int c, int d);
	public void setVisible(boolean b);
	public void setLocation(int x, int y);
	public JSLFrameListener[] getJSLFrameListeners();
	public void removeJSLFrameListener(JSLFrameListener l);
	public void setPreferredSize(Dimension d);
	public boolean isSelected();
	public void moveToDefaultLocation();
    }

    class JSLIFrame extends JInternalFrame implements JSLFrameProxy,
						       InternalFrameListener {
	private WeakReference parent;
	protected ArrayList listeners = new ArrayList();
	public JSLIFrame(JSLFrame p) {
	    super();
	    parent = new WeakReference(p);
	    addInternalFrameListener(this);
	}
	public JSLIFrame(JSLFrame p, String title) {
	    super(title);
	    parent = new WeakReference(p);
	    addInternalFrameListener(this);
	}
	public JSLIFrame(JSLFrame p, String s, boolean resizable, boolean			 closable, boolean maximizable, boolean iconifiable) {
	    super(s, resizable, closable, maximizable, iconifiable);
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

	public void internalFrameActivated(InternalFrameEvent e) {
	    ErrorMsg.reportStatus("\""+getTitle()+"\" activated.");
	    JSLFrameEvent fe =
		new JSLFrameEvent(getJSLFrame(), JSLFrameEvent.ACTIVATED);
	    Iterator it = listeners.iterator();
	    while (it.hasNext()) {
		((JSLFrameListener)it.next()).JSLFrameActivated(fe);
	    }
	    // Enable pasteAction
	    Actions.setEnabled(true, Actions.EN_PASTE);
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
	    ErrorMsg.reportStatus("\""+getTitle()+"\" deactivated.");
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
	public void moveToDefaultLocation() {
	    setLocation(30*frame_count, 30*frame_count);
	}
    }
    class JSLJFrame extends JFrame implements JSLFrameProxy,
						      WindowListener {
	private WeakReference parent;
	protected ArrayList listeners = new ArrayList();

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
	public JSLFrame getJSLFrame() { return (JSLFrame)parent.get(); }
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
	    if (MacUtils.isMac()) {
		if (b && getJMenuBar() == null) {
		    setJMenuBar(Actions.createMenuBar());
		} else if (!b) {
		    // Remove menubar so frame can be disposed.
		    // http://archives:archives@lists.apple.com/archives/java-dev/2003/Dec/04/disposingofjframesusescr.001.txt
		    JMenuBar mb = getJMenuBar();
		    setJMenuBar(null);

		    JSLDesktop.getInvisible().getJFrame().requestFocus();
		    requestFocus();
		}
	    }
	    super.setVisible(b);
	}
	public void windowActivated(WindowEvent e) {
	    ErrorMsg.reportStatus("\""+((JFrame)e.getWindow()).getTitle()
				  +"\" activated.");

	    JSLFrameEvent fe =
		new JSLFrameEvent(getJSLFrame(),
				  JSLFrameEvent.ACTIVATED);
	    Iterator it = listeners.iterator();
	    while (it.hasNext()) {
		((JSLFrameListener)it.next()).JSLFrameActivated(fe);
	    }
	    // Enable pasteAction
	    Actions.setEnabled(true, Actions.EN_PASTE);
	}
	public void windowClosed(WindowEvent e) {
	    JSLFrameEvent fe =
		new JSLFrameEvent(getJSLFrame(), JSLFrameEvent.CLOSED);
	    Iterator it = listeners.iterator();
	    while (it.hasNext()) {
		((JSLFrameListener)it.next()).JSLFrameClosed(fe);
	    }
	}
	public void windowClosing(WindowEvent e) {
	    JSLFrameEvent fe =
		new JSLFrameEvent(getJSLFrame(), JSLFrameEvent.CLOSING);
	    Iterator it = listeners.iterator();
	    while (it.hasNext()) {
		((JSLFrameListener)it.next()).JSLFrameClosing(fe);
	    }
	    //if (!fe.isConsumed())
	    //	proxy.dispose();
	}
	public void windowDeactivated(WindowEvent e) {
	    ErrorMsg.reportStatus("\""+((JFrame)e.getWindow()).getTitle()
				  +"\" deactivated.");
	    JSLFrameEvent fe =
		new JSLFrameEvent(getJSLFrame(), JSLFrameEvent.DEACTIVATED);
	    Iterator it = listeners.iterator();
	    while (it.hasNext()) {
		((JSLFrameListener)it.next()).JSLFrameDeactivated(fe);
	    }
	}
	public void windowDeiconified(WindowEvent e) {
	    JSLFrameEvent fe =
		new JSLFrameEvent(getJSLFrame(), JSLFrameEvent.DEICONIFIED);
	    Iterator it = listeners.iterator();
	    while (it.hasNext()) {
		((JSLFrameListener)it.next()).JSLFrameDeiconified(fe);
	    }
	}
	public void windowIconified(WindowEvent e) {
	    JSLFrameEvent fe =
		new JSLFrameEvent(getJSLFrame(), JSLFrameEvent.ICONIFIED);
	    Iterator it = listeners.iterator();
	    while (it.hasNext()) {
		((JSLFrameListener)it.next()).JSLFrameIconified(fe);
	    }
	}
	public void windowOpened(WindowEvent e) {
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
	public void setPreferredSize(Dimension d) {}
	public boolean isSelected() {
	    //return isActive() || lastselection == this;
	    JSLDesktop d = JSLDesktop.getInstance();
	    JFrame f = JSLDesktop.getSelectedWindow();
	    return (f == this)
		|| (f == JSLDesktop.getToolBar().getJFrame()
		    && JSLDesktop.getLastSelectedWindow() == this);
	}
	void fakeActivate() {
	    WindowEvent we =
		new WindowEvent(this,
				WindowEvent.WINDOW_ACTIVATED, null);
	    processWindowEvent(we);
	}
	public void moveToDefaultLocation() {
	    int x, y, yofs = 0;
	    int xsep = 30;
	    int ysep = JSLDesktop.getYDecoration();
	    Dimension d = JSLDesktop.getSize();

	    JFrame tb = JSLDesktop.getToolBar().getJFrame();
	    if (tb != null && tb.getLocation().getY() < 100)
		yofs = (int)
		    (tb.getLocation().getY() - ysep + tb.getSize().getHeight());
	    x = (xsep*frame_count) %
		(int)(d.getWidth() - getSize().getWidth());
	    if (x < 0)
		x = 0;
	    y = yofs + (ysep*frame_count)
		% (int)(d.getHeight() - getSize().getHeight() - yofs);
	    if (y < 0)
		y = yofs + ysep;
	    setLocation(x,y);
	}
    }
}

