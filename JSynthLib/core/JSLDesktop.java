package core;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.Action;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

/**
 * A virtual JDesktopPane class which supports both MDI (Multiple Document
 * Interface: using JInternalFrame) and SDI (Single Document Interface: using
 * JFrame) mothods. In MDI mode JDesktopPane is used. In SDI mode a ToolBar
 * window is created and JDesktopPane methods are emulated. For the details of
 * each method, refer the documentation of JDesktopPane.
 * 
 * @see JDesktopPane
 * @see JSLWindowMenu
 * @see JSLFrame
 * @author Rib Rdb
 * @author Hiroo Hayashi
 */
public class JSLDesktop implements JSLFrameListener {
    private JSLDesktopProxy proxy;
    private Boolean in_fake_activation = Boolean.FALSE;
    private static int frame_count = 0;
    private int xdecoration = 0, ydecoration = 0;
    private Action exitAction;
    /** @see #setGUIMode(boolean) */
    private static boolean useMDI = true;
    /** a list of JSLFrames added to the JSLDesktop. */
    protected ArrayList windows = new ArrayList();
    /** just for efficiency. */
    private static boolean isMac = MacUtils.isMac();

    /** Creates a new JSLDesktop. */
    protected JSLDesktop(String title, JMenuBar mb, JToolBar tb, Action exitAction) {
        this.exitAction = exitAction;
	if (useMDI) {
	    proxy = new JSLJDesktop(title, mb, tb);
	} else {
	    proxy = new JSLFakeDesktop();
	    if (isMac && System.getProperty("apple.laf.useScreenMenuBar").equals("true"))
	        ((JSLFakeDesktop) proxy).createInvisibleWindow(mb);
	    else
	        ((JSLFakeDesktop) proxy).createToolBarWindow(title + " Tool Bar", mb, tb);
	}
    }

    /**
     * Select GUI mode. This method must be called before the first JSLDesktop
     * constructor call. If this method is not call, MDI is used.
     * 
     * @param useMDI
     *            if true MDI (single window mode) is used, otherwise SDI
     *            (multiple window mode) is used.
     */
    public static void setGUIMode(boolean useMDI) {
        JSLDesktop.useMDI = useMDI;
    }
    /**
     * @return <code>true</code> in MDI mode, <code>false</code> in SDI
     *         mode.
     */
    public static boolean useMDI() {
        return useMDI;
    }

    /** @see JSLFrame#moveToDefaultLocation() */
    Point getDefaultLocation(Dimension frameSize) {
        int xofs = 0;
        int yofs = 0;
        if (!useMDI()) {
            if (isMac) {
                xofs = 100;
                yofs = 100;
            } else {
                JFrame tb = ((JSLFakeDesktop) proxy).toolbar.getJFrame();
                if (tb.getLocation().getY() < 100) { // Do we need this check?
                    xofs = (int) tb.getLocation().getX();
                    yofs = (int) (tb.getLocation().getY() + tb.getSize()
                            .getHeight());
                }
            }
        }

        int xsep = 30;
        int ysep = isMac || useMDI() ? 30 : ydecoration;
        Dimension screenSize = getSize();
        int x = xofs + (xsep * frame_count) % (int) (screenSize.getWidth() - frameSize.getWidth() - xofs);
        if (x < 0)
            x = 0;
        int y = yofs + (ysep * frame_count) % (int) (screenSize.getHeight() - frameSize.getHeight() - yofs);
        if (y < 0)
            y = yofs + ysep;

        frame_count++;
        return new Point(x, y);
    }

    // JDesktopPane compatible methods
    /** Returns all JInternalFrames currently displayed in the desktop. */
    // TODO use getJSLFrameIterator() instead of this
    public JSLFrame[] getAllFrames() { return proxy.getAllJSLFrames(); }
    /**
     * Returns the currently active JSLFrame, or last active JSLFrame if no
     * JSLFrame is currently active, or null if any JSLFrame has never been
     * activated.
     */
    public JSLFrame getSelectedFrame() { return proxy.getSelectedJSLFrame(); }
    /** Returns the size of this component in the form of a Dimension object. */
    public Dimension getSize() { return proxy.getSize(); }

    // original (non-JDesktopPane compatible) methods
    /**
     * Returns the current active JFrame. Used for the <code>owner</code>
     * parameter for <code>JDialog</code> constructor. In MDI mode returns the
     * root JFrame created. In SDI mode returns the current active JFrame (may
     * be the Toolbar frame).
     * 
     * @see #getSelectedWindow()
     */
    public JFrame getSelectedWindow() { return proxy.getSelectedWindow(); }

    /**
     * Returns the root JFrame for the <code>owner</code> parameter for
     * <code>JDialog</code> constructor to show a dialog window in the center
     * of screen. In MDI mode returns the root JFrame created. In SDI mode
     * returns null.
     * 
     * @see #getSelectedWindow()
     */
    public JFrame getRootFrame() {
        return useMDI ? ((JSLJDesktop) proxy).frame : null;
    }
    
    /** Returns invisible window.  Used only in SDI mode for Mac OS. */
    JFrame getInvisible() { return ((JSLFakeDesktop) proxy).invisible.getJFrame(); }

    /** add a JSLFrame under this JSLDesktop control. */
    public void add(JSLFrame f) {
        if (windows.contains(f)) {
            ErrorMsg.reportStatus("JSLDesktop.add : multiple add() call.");
            return;
        }
        proxy.add(f);
        f.addJSLFrameListener(this);
        windows.add(f);
    }
    /**
     * @return <code>Iterator</code> of JSLFrame added on the JSLDesktop.
     */
    public Iterator getJSLFrameIterator() {
        return windows.iterator();
    }
    public void JSLFrameActivated(JSLFrameEvent e) {
        proxy.FrameActivated(e.getJSLFrame());
    }
    public void JSLFrameClosing(JSLFrameEvent e) {
        proxy.FrameClosing(e.getJSLFrame());
    }
    public void JSLFrameClosed(JSLFrameEvent e) {
        windows.remove(e.getJSLFrame());
        proxy.FrameClosed(e.getJSLFrame());
    }
    public void JSLFrameDeactivated(JSLFrameEvent e) {}
    public void JSLFrameDeiconified(JSLFrameEvent e) {}
    public void JSLFrameIconified(JSLFrameEvent e) {}
    public void JSLFrameOpened(JSLFrameEvent e) {}

    /**
     * Notification from the UIManager that the L&F has changed. Replaces the
     * current UI object with the latest version from the UIManager.
     */
    public void updateLookAndFeel() { proxy.updateLookAndFeel(); }

    private boolean confirmExiting() {
        return JOptionPane.showConfirmDialog(getRootFrame(),
                "Exit JSynthLib?", "Confirmation",
                JOptionPane.OK_CANCEL_OPTION) == 0;
    }
    
    private interface JSLDesktopProxy {
        JFrame getSelectedWindow();
        JSLFrame[] getAllJSLFrames();
        JSLFrame getSelectedJSLFrame();
        Dimension getSize();
        void add(JSLFrame f);
        void updateLookAndFeel();
        void FrameActivated(JSLFrame f);
        void FrameClosing(JSLFrame f);
        void FrameClosed(JSLFrame f);
    }

    /** use JDesktopPane for MDI (Multiple Document Interface) mode. */
    private class JSLJDesktop extends JDesktopPane implements JSLDesktopProxy {
        private JFrame frame;
        private static final int INSET = 100;

        JSLJDesktop(String title, JMenuBar mb, JToolBar tb) {
	    super();
	    frame = new JFrame(title);
	    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	    frame.setBounds(INSET, INSET,
			       screenSize.width  - INSET * 2,
			       screenSize.height - INSET * 2);

	    //Quit this app when the big window closes.
	    frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	    frame.addWindowListener(new WindowAdapter() {
		    public void windowClosing(WindowEvent e) {
		        if (confirmExiting())
		            exitAction.actionPerformed(null);
		    }
		});

	    Container c = frame.getContentPane();
	    if (tb != null) {
	        c.add(tb, BorderLayout.NORTH);
	        tb.setVisible(true);
	    }
	    c.add(this, BorderLayout.CENTER);
	    if (mb != null)
	        frame.setJMenuBar(mb);
	    setOpaque(false);
	    putClientProperty("JDesktopPane.dragMode", "outline");

	    frame.setVisible(true);
	}

        public void add(JSLFrame f) {
            add(f.getJInternalFrame());
	}

        public void updateLookAndFeel() {
            SwingUtilities.updateComponentTreeUI(frame);
            //selected.pack();
	}
	public JFrame getSelectedWindow() { return frame; }
	public JSLFrame getSelectedJSLFrame() {
	    try {
		return ((JSLFrame.JSLFrameProxy)getSelectedFrame()).getJSLFrame();
	    } catch (NullPointerException e) {
		return null; // This is normal.
	    }
	}
	public JSLFrame[] getAllJSLFrames() {
	    JInternalFrame[] ifs = getAllFrames();
	    JSLFrame[] a = new JSLFrame[ifs.length];

	    for (int i = 0; i < ifs.length; i++) {
		if (ifs[i] instanceof JSLFrame.JSLFrameProxy) {
		    a[i] = ((JSLFrame.JSLFrameProxy) ifs[i]).getJSLFrame();
		}
	    }
	    return a;
	}
	public void FrameActivated(JSLFrame f) {}
	public void FrameClosing(JSLFrame f) {}
	public void FrameClosed(JSLFrame f) {}
    }

    /** fake desktop for SDI (Single Document Interface) mode. */
    private class JSLFakeDesktop implements JSLDesktopProxy {
	protected JSLFrame toolbar;
	/** invisible frame to keep menus when no open windows on MacOSX. */
	private JSLFrame invisible = null;
	private JSLFrame selected = null;
	/** last selected (activated) frame except toolbar nor invisible frame. */
	private JSLFrame last_selected = null;

	private JSLFakeDesktop() {
	}

        /** Create invisible window to keep menus when no open windows */
	private void createInvisibleWindow(JMenuBar mb) {
            invisible = new JSLFrame(JSLDesktop.this);
            JFrame frame = invisible.getJFrame();
            frame.setTitle("Please enable ScreenMenuBar.");
            frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            if (mb != null)
                frame.setJMenuBar(mb);
            frame.setSize(0, 0);
            frame.setUndecorated(true);
            //frame(0,0x7FFFFFFF);
            frame.pack();
            frame.setVisible(true);
            //frame.addWindowListener(this);
            selected = invisible;
        }

	/** create a toolbar window */
	private void createToolBarWindow(String title, JMenuBar mb, JToolBar tb) {
	    toolbar = new JSLFrame(JSLDesktop.this);
	    JFrame frame = toolbar.getJFrame();
	    toolbar.setTitle(title);
	    frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

	    //toolbar.addJSLFrameListener(this);
	    if (mb != null)
	        toolbar.setJMenuBar(mb);
	    tb.setFloatable(false);
	    toolbar.getContentPane().add(tb);
	    toolbar.pack();

	    Dimension gs = frame.getGlassPane().getSize();
	    Dimension ts = frame.getSize();
	    xdecoration = (int)(ts.getWidth() - gs.getWidth());
	    ydecoration = (int)(ts.getHeight() - gs.getHeight());
	    toolbar.setLocation(xdecoration/2, ydecoration);

	    JSLDesktop.this.add(toolbar);
	    toolbar.setVisible(true);
	}
	public void add(JSLFrame f) {
	}

	public void updateLookAndFeel() {
            // update toolbar
            SwingUtilities.updateComponentTreeUI(toolbar.getJFrame());
            toolbar.pack();
            // update each Frame
            Iterator it = windows.iterator();
            while (it.hasNext()) {
                JFrame frame = ((JSLFrame) it.next()).getJFrame();
                SwingUtilities.updateComponentTreeUI(frame);
                frame.pack();
            }
        }

	public Dimension getSize() {
	    return Toolkit.getDefaultToolkit().getScreenSize();
	}
	public JFrame getSelectedWindow() { return selected.getJFrame(); }
	public JSLFrame getSelectedJSLFrame() {
	    return last_selected;
        }
	public JSLFrame[] getAllJSLFrames() {
	    JSLFrame[] a = new JSLFrame[windows.size()];
	    Iterator it = windows.iterator();
	    for (int i = 0; it.hasNext(); i++) {
		Object o = it.next();
		if (o instanceof JSLFrame) {
		    a[i] = (JSLFrame) o;
		}
	    }
	    return a;
	}

	// JSLFrameListener methods : called for both toolbar and JSLFrame
	private void showState(JSLFrame f, String s) {
	    ErrorMsg.reportStatus(ErrorMsg.FRAME, "\"" + f.getTitle() + "\" " + s);
	}
	public void FrameActivated(JSLFrame f) {
	    if (f == toolbar) {
                synchronized (in_fake_activation) {
                    if (in_fake_activation.booleanValue())
                        return;
                    // When toolbar is activated, activate the last selected
                    // frame if it is not iconified nor closing.
                    if (last_selected != null
                            //&& last_selected != toolbar
                            && !last_selected.isIcon()
                            && !last_selected.isClosing()) {
                        showState(last_selected, "FakeActivated");
                        in_fake_activation = Boolean.TRUE;
                        ((JSLFrame.JSLJFrame) last_selected.getJFrame()).fakeActivate();
                        in_fake_activation = Boolean.FALSE;
                    }
                }
            } else if (f != invisible)
                last_selected = f;

	    selected = f;
	    showState(f, "selected : " + selected);
	}
	public void FrameClosing(JSLFrame f) {
	    if (f == toolbar && confirmExiting())
	        exitAction.actionPerformed(null);
	}
	public void FrameClosed(JSLFrame f) {
            showState(f, "closed. " + (windows.size() - 1)
                    + " windows still open.");
            if (last_selected == f)
                last_selected = null;
        }
    }
}
