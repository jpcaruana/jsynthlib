package core;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

// TODO: Lines marked with XXX are JSynthLib dependent code. They must be removed to
// make this class a generic class.
/**
 * A virtual JDesktopPane class which supports both MDI (using JInternalFrame)
 * and SDI (using JFrame) mothods. In MDI mode JDesktopPane is used. In SDI mode
 * a ToolBar window is created and JDesktopPane methods are emulated. For the
 * details of each method, refer the documentation of JDesktopPane.
 * 
 * @see JDesktopPane
 * @see JSLFrame
 * @author Rib Rdb
 */
public class JSLDesktop {
    private static JSLDesktopProxy proxy;
    protected static JFrame selected = null;
    // not used now
    static AbstractAction toolBarAction = new AbstractAction ("Tool Bar") {
	    public void actionPerformed(ActionEvent e) {
		JSLDesktop.showToolBar();
	    }
	};
    private static Boolean in_fake_activation = Boolean.FALSE;
    //private static Boolean in_get_instance = Boolean.FALSE;
    private static int xdecoration = 0, ydecoration = 0;
    private static JSLDesktop instance = null;
    private static boolean useMDI;
    private static Action exitAction;

    /** Creates a new JSLDesktop. */
    protected JSLDesktop(String title, boolean useMDI, JToolBar tb, Action exitAction) {
        JSLDesktop.useMDI = useMDI;
        JSLDesktop.exitAction = exitAction;
	if (useMDI) {
	    proxy = new JSLJDesktop(title, tb);
	    ((JSLJDesktop) proxy).setupInitialMenuBar(tb);
	} else {
	    proxy = new JSLFakeDesktop(title + " Tool Bar", tb);
	    ((JSLFakeDesktop) proxy).setupInitialMenuBar(tb);
	}
//	setupInitialMenuBar(tb);
	instance = this;
    }
    static boolean useMDI() {
        return useMDI;
    }

    static JSLDesktop getInstance() {
//	if (instance == null) {
//	    synchronized (in_get_instance) {
//		if (!in_get_instance.booleanValue()) {
//		    in_get_instance = Boolean.TRUE;
//		    instance = new JSLDesktop();
//		    in_get_instance = Boolean.FALSE;
//		}
//	    }
//	}
	return instance;
    }
    // JDesktopPane compatible methods
    /** Returns all JInternalFrames currently displayed in the desktop. */
    public static JSLFrame[] getAllFrames() { return proxy.getAllJSLFrames(); }
    /**
     * Returns the currently active JSLFrame, or null if no JSLFrame is
     * currently active.
     */
    public static JSLFrame getSelectedFrame() { return proxy.getSelectedJSLFrame(); }
    /** Returns the size of this component in the form of a Dimension object. */
    public static Dimension getSize() { return proxy.getSize(); }

    // original (non-JDesktopPane compatible) methods
    /**
     * In MDI mode returns the root JFrame created. In SDI mode returns the
     * current active JFrame including Toolbar frame.
     */
    public static JFrame getSelectedWindow() { return selected; }
    /**
     * Creates the Window Menu which is depenent on MDI/SDI mode. called by
     * Actions.createMenuBar().
     */
    public static JMenu createWindowMenu() { return proxy.createWindowMenu(); }
    /**  */
    //private static void setupInitialMenuBar(JToolBar tb) { proxy.setupInitialMenuBar(tb); }

    /** add frame in MDI mode. registerFrame() is called in SDI mode. */
    public static void add(JSLFrame f) { proxy.add(f); }
    /** add frame in SDI mode. Do nothing in MDI mode. */
    //static void registerFrame(JSLFrame f) { proxy.registerFrame(f); }
    /**  */
    static JFrame getLastSelectedWindow() { return proxy.getLastSelectedWindow(); }
    /**  */
    static JSLFrame getToolBar() { return proxy.getToolBar(); }
    /**  */
    static JSLFrame getInvisible() { return proxy.getInvisible(); }
    /**  */
    static int getXDecoration() { return xdecoration; }
    /**  */
    static int getYDecoration() { return ydecoration; }
    /**  */
    private static void showToolBar() { proxy.showToolBar(); }

    private interface JSLDesktopProxy {
        JSLFrame[] getAllJSLFrames();
        JSLFrame getSelectedJSLFrame();
        Dimension getSize();
        void add(JSLFrame f);
        JMenu createWindowMenu();
        //void registerFrame(JSLFrame f);
        //void setupInitialMenuBar(JToolBar tb);
        void showToolBar();
        JSLFrame getToolBar();
        JSLFrame getInvisible();
        JFrame getLastSelectedWindow();
    }
    private class JSLJDesktop extends JDesktopPane implements JSLDesktopProxy {

	JSLJDesktop(String title, JToolBar tb) {
	    super();
	    selected = new JFrame(title);
	    int inset = 100;
	    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	    selected.setBounds(inset, inset,
			       screenSize.width  - inset * 2,
			       screenSize.height - inset * 2);

	    //Quit this app when the big window closes.
	    selected.addWindowListener(new WindowAdapter() {
		    public void windowClosing(WindowEvent e) {
			exitAction.actionPerformed(null);
		    }
		});
	}
	private void setupInitialMenuBar(JToolBar tb) {
	    Container c = selected.getContentPane();
	    c.add(tb, BorderLayout.NORTH);
	    tb.setVisible(true);

	    setOpaque(false);
	    putClientProperty("JDesktopPane.dragMode", "outline");
	    c.add(this, BorderLayout.CENTER);

	    selected.setJMenuBar(Actions.createMenuBar()); // XXX
	    selected.setVisible(true);
	}
	public JMenu createWindowMenu() {
	    JMenu menuWindow = new JMenu("Window");
	    if (!MacUtils.isMac()) {
		menuWindow.add(Actions.prefsAction); // XXX
		menuWindow.setMnemonic(KeyEvent.VK_W);
	    }
	    menuWindow.add(Actions.monitorAction); // XXX
	    return menuWindow;
	}
	//public void registerFrame(JSLFrame f) {}
	public void add(JSLFrame f) {
	    if (f.getProxy() instanceof JInternalFrame) {
		add((JInternalFrame)f.getProxy());
	    }
	}
	public JSLFrame getSelectedJSLFrame() {
	    try {
		return ((JSLFrame.JSLFrameProxy)getSelectedFrame()).getJSLFrame();
	    } catch (Exception e) {
		return null;
	    }
	}
	public JSLFrame[] getAllJSLFrames() {
	    JInternalFrame[] ifs = getAllFrames();
	    JSLFrame[] a = new JSLFrame[ifs.length];

	    for (int i = 0; i < ifs.length; i++) {
		if (ifs[i] instanceof JSLFrame.JSLFrameProxy) {
		    a[i] = ((JSLFrame.JSLFrameProxy)ifs[i]).getJSLFrame();
		}
	    }
	    return a;
	}
	public void showToolBar() {}
	public JSLFrame getToolBar() {return null;}
	public JSLFrame getInvisible() { return null; }
	public JFrame getLastSelectedWindow() { return null; }
    }
    private class JSLFakeDesktop implements JSLDesktopProxy, JSLFrameListener {
	protected ArrayList windows = new ArrayList();
	protected ArrayList windowMenus = new ArrayList();
	protected JSLFrame toolbar;
	private JSLFrame invisible = null;
	private JSLFrame.JSLJFrame last_selected = null;

	JSLFakeDesktop(String title, JToolBar tb) {
	    if (MacUtils.isMac()) {
		// Create invisible window to keep menus when no open windows
		invisible = new JSLFrame();
		invisible.setTitle("Please enable ScreenMenuBar.");
		invisible.getJFrame().setDefaultCloseOperation(
		        JFrame.DO_NOTHING_ON_CLOSE);
	    }
	    toolbar = new JSLFrame();
	    toolbar.setTitle(title);
	    toolbar.getJFrame().setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
	}
	private void setupInitialMenuBar(JToolBar tb) {
	    if (invisible != null) { // MacUtils.isMac()
		selected = invisible.getJFrame();
		selected.setJMenuBar(Actions.createMenuBar()); // XXX
		selected.setSize(0,0);
		selected.setUndecorated(true);
		//selected.setLocation(0,0x7FFFFFFF);
		selected.pack();
		selected.setVisible(true);
		//selected.addWindowListener(this);
	    }

	    registerFrame(toolbar);
	    //toolbar.addJSLFrameListener(this);
	    toolbar.setJMenuBar(Actions.createMenuBar()); // XXX
	    tb.setFloatable(false);
	    toolbar.getContentPane().add(tb);
	    toolbar.pack();
	    JFrame f = toolbar.getJFrame();
	    Dimension gs = f.getGlassPane().getSize();
	    Dimension ts = f.getSize();
	    xdecoration = (int)(ts.getWidth() - gs.getWidth());
	    ydecoration = (int)(ts.getHeight() - gs.getHeight());
	    toolbar.setLocation(xdecoration/2, ydecoration);
	    toolbar.setVisible(true);
	    JSLFrame.resetFrameCount();
	}
	public JSLFrame getInvisible() { return invisible; }
	public JMenu createWindowMenu() {
	    JSLWindowMenu wm = new JSLWindowMenu();
	    windowMenus.add(wm);
	    Iterator it = windows.iterator();
	    while (it.hasNext()) {
		wm.add(((JSLFrame)it.next()).getJFrame());
	    }
	    wm.setSelectedWindow(selected);
	    return wm;
	}
	private void registerFrame(JSLFrame f) {
	    if (windows.contains(f))
		return;
	    if (f.getProxy() instanceof JFrame) {
		f.addJSLFrameListener(this);
		JFrame jf = (JFrame)f.getProxy();
		Iterator it = windowMenus.iterator();
		while (it.hasNext()) {
		    ((JSLWindowMenu)it.next()).add(jf);
		}
		windows.add(f);
		jf.setJMenuBar(Actions.createMenuBar()); // XXX
		//ErrorMsg.reportStatus("isVisible = " + jf.isVisible());
		// Without the next line, menu bar does not show up sometimes. Why?
		jf.setVisible(true);
	    }
	}
	public void add(JSLFrame f) { registerFrame(f); }
	public Dimension getSize() {
	    return Toolkit.getDefaultToolkit().getScreenSize();
	}
	public JSLFrame getSelectedJSLFrame() {
	    try {
		if (selected == toolbar.getJFrame())
		    return last_selected.getJSLFrame();
		return ((JSLFrame.JSLFrameProxy)selected).getJSLFrame();
	    } catch (Exception e) { return null; }
	}
	public JSLFrame[] getAllJSLFrames() {
	    JSLFrame[] a = new JSLFrame[windows.size()];
	    Iterator it = windows.iterator();
	    int i = 0;
	    Object o;
	    while (it.hasNext()) {
		o = it.next();
		if (o instanceof JSLFrame) {
		    a[i] = (JSLFrame)o;
		}
		i++;
	    }
	    return a;
	}

	// JSLFrameListener methods : called for both toolbar and JSLFrame
	private void showState(JSLFrame f, String s) {
	    ErrorMsg.reportStatus(ErrorMsg.FRAME, "\"" + f.getTitle() + "\" " + s);
	}
	public void JSLFrameActivated(JSLFrameEvent e) {
	    JSLFrame f = e.getJSLFrame();
	    if (f == toolbar) {
                synchronized (in_fake_activation) {
                    if (in_fake_activation.booleanValue())
                        return;
                    // When toolbar is activated, activate the last selected
                    // frame if it is not iconified nor closing.
                    if (last_selected != null
                            && last_selected != toolbar.getJFrame()
                            && !last_selected.isIcon()
                            && !last_selected.isClosing()) {
                        showState(last_selected.getJSLFrame(), "FakeActivated");
                        in_fake_activation = Boolean.TRUE;
                        last_selected.fakeActivate();
                        in_fake_activation = Boolean.FALSE;
                    }
                }
            } else {
                last_selected = null;
            }
	    selected = f.getJFrame();
	    showState(f, "selected");
	}
	public void JSLFrameClosing(JSLFrameEvent e) {
	    JSLFrame f = e.getJSLFrame();
	    if (f == toolbar) {
		if (windows.size() < 2 && invisible == null) { // !MacUtils.isMac()
		    exitAction.actionPerformed(null);
		} else {
		    showState(f, "hidden. " + (windows.size() - 1)
                            + " windows still open.");
		}
	    } else
	        last_selected = null;
	}
	public void JSLFrameClosed(JSLFrameEvent e) {
	    JSLFrame f = e.getJSLFrame();
	    windows.remove(f);
	    if (windows.size() < 2) {
		// toolbar should never be closed, only hidden
		if (toolbar.isVisible()) {
		    selected = toolbar.getJFrame();
		    showState(toolbar, "selected");
		} else if (invisible == null) { // !MacUtils.isMac()
		    exitAction.actionPerformed(null);
		} else {
		    ErrorMsg.reportStatus(ErrorMsg.FRAME,
		            "All windows closed, but invisible "
		            + "frame exists. Not exiting.");
		}
	    } else {
	        showState(f, "closed. " + (windows.size() - 1)
                        + " windows still open.");
	    }
	}
	public void JSLFrameDeactivated(JSLFrameEvent e) {
	    JSLFrame f = e.getJSLFrame();
            if (f != toolbar
                    // ignore iconified or closing frame
                    && !f.isIcon() && !f.isClosing())
                last_selected = (JSLFrame.JSLJFrame) f.getJFrame();
	}
	public void JSLFrameDeiconified(JSLFrameEvent e) {}
	public void JSLFrameIconified(JSLFrameEvent e) {
	    if (e.getJSLFrame() != toolbar)
	        last_selected = null;
    	}
	public void JSLFrameOpened(JSLFrameEvent e) {}

	public void showToolBar() {
	    toolbar.setVisible(true);
	}
	public JSLFrame getToolBar() { return toolbar; }
	public JFrame getLastSelectedWindow() { return last_selected; }
    }
}
