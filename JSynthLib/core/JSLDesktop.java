package core;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class JSLDesktop {
    private static JSLDesktopProxy proxy;
    protected static JFrame selected = null;
    static AbstractAction toolBarAction = new AbstractAction ("Tool Bar") {
	    public void actionPerformed(ActionEvent e) {
		JSLDesktop.showToolBar();
	    }
	};
    private static Boolean in_fake_activation = Boolean.FALSE;
    private static Boolean in_get_instance = Boolean.FALSE;
    private static int xdecoration = 0, ydecoration = 0;
    private static JSLDesktop instance = null;

    protected JSLDesktop() {
	if (JSLFrame.useInternalFrames()) {
	    proxy = new JSLJDesktop();
	} else {
	    proxy = new JSLFakeDesktop();
	}
    }

    public static JSLDesktop getInstance() {
	if (instance == null) {
	    synchronized (in_get_instance) {
		if (!in_get_instance.booleanValue()) {
		    in_get_instance = Boolean.TRUE;
		    instance = new JSLDesktop();
		    in_get_instance = Boolean.FALSE;
		}
	    }
	}
	return instance;
    }

    public static JSLFrame[] getAllFrames() { return proxy.getAllJSLFrames(); }
    public static JSLFrame getSelectedFrame() { return proxy.getSelectedJSLFrame(); }
    public static JFrame getSelectedWindow() { return selected; }
    static JFrame getLastSelectedWindow() { return proxy.getLastSelectedWindow(); }
    public static Dimension getSize() { return proxy.getSize(); }
    public static void add(JSLFrame f) { proxy.add(f); }
    public static JMenu createWindowMenu() { return proxy.createWindowMenu(); }
    static void registerFrame(JSLFrame f) { proxy.registerFrame(f); }
    static void setupInitialMenuBar(JToolBar tb) { proxy.setupInitialMenuBar(tb); }
    public static void showToolBar() { proxy.showToolBar(); }
    static JSLFrame getToolBar() { return proxy.getToolBar(); }
    static JSLFrame getInvisible() { return proxy.getInvisible(); }
    static int getXDecoration() { return xdecoration; }
    static int getYDecoration() { return ydecoration; }
    private interface JSLDesktopProxy {
	public JSLFrame[] getAllJSLFrames();
	public JSLFrame getSelectedJSLFrame();
	public Dimension getSize();
	public void add(JSLFrame f);
	public JMenu createWindowMenu();
	void registerFrame(JSLFrame f);
	public void setupInitialMenuBar(JToolBar tb);
	public void showToolBar();
	public JSLFrame getToolBar();
	public JSLFrame getInvisible();
	JFrame getLastSelectedWindow();
     }
    private class JSLJDesktop extends JDesktopPane implements JSLDesktopProxy {

	JSLJDesktop() {
	    super();
	    selected = new JFrame("JSynthLib");
	    int inset = 100;
	    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	    selected.setBounds(inset, inset,
			       screenSize.width  - inset * 2,
			       screenSize.height - inset * 2);

	    //Quit this app when the big window closes.
	    selected.addWindowListener(new WindowAdapter() {
		    public void windowClosing(WindowEvent e) {
			PatchEdit.exitAction.exit();
		    }
		});

	}
	public void setupInitialMenuBar( JToolBar tb ) {
	    Container c = selected.getContentPane();
	    c.add(tb, BorderLayout.NORTH);
	    tb.setVisible(true);

	    setOpaque(false);
	    putClientProperty("JDesktopPane.dragMode", "outline");
	    c.add(this, BorderLayout.CENTER);

	    selected.setJMenuBar(PatchEdit.createMenuBar());
	    selected.setVisible(true);
	}
	public JMenu createWindowMenu() {
	    JMenu menuWindow = new JMenu("Window");
	    if (!MacUtils.isMac()) {
		menuWindow.add(PatchEdit.prefsAction);
		menuWindow.setMnemonic(KeyEvent.VK_W);
	    }
	    menuWindow.add(PatchEdit.monitorAction);
	    return menuWindow;
	}
	public void registerFrame(JSLFrame f) {}
	public void add(JSLFrame f) {
	    if (f.getProxy() instanceof JInternalFrame) {
		add((JInternalFrame)f.getProxy());
	    }
	}
	public JSLFrame getSelectedJSLFrame() {
	    try {
		return (JSLFrame)
		    ((JSLFrame.JSLFrameProxy)getSelectedFrame()).getJSLFrame();
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

	JSLFakeDesktop() {
	    if (MacUtils.isMac()) {
		// Create invisible window to keep menus when no open windows
		invisible = new JSLFrame();
		invisible.setTitle("Please enable ScreenMenuBar.");
		invisible.getJFrame().setDefaultCloseOperation(
		        JFrame.DO_NOTHING_ON_CLOSE);
	    }
	    toolbar = new JSLFrame();
	    toolbar.setTitle("JSynthLib Tool Bar");
	    toolbar.getJFrame().setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
	}
	public void setupInitialMenuBar(JToolBar tb) {
	    if (invisible != null) {
		selected = invisible.getJFrame();
		selected.setJMenuBar(PatchEdit.createMenuBar());
		selected.setSize(0,0);
		selected.setUndecorated(true);
		//selected.setLocation(0,0x7FFFFFFF);
		selected.pack();
		selected.setVisible(true);
		//selected.addWindowListener(this);
	    }

	    registerFrame(toolbar);
	    //toolbar.addJSLFrameListener(this);
	    toolbar.setJMenuBar(PatchEdit.createMenuBar());
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
	public void registerFrame(JSLFrame f) {
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
		jf.setJMenuBar(PatchEdit.createMenuBar());
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

	public void JSLFrameActivated(JSLFrameEvent e) {
	    JSLFrame f = e.getJSLFrame();
	    synchronized (in_fake_activation) {
		if (in_fake_activation.booleanValue())
		    return;
		if (f == toolbar && last_selected != null 
		    && last_selected != toolbar.getJFrame()) {
		    in_fake_activation = Boolean.TRUE;
		    last_selected.fakeActivate();
		    in_fake_activation = Boolean.FALSE;
		}
	    }
	    selected = f.getJFrame();
	    ErrorMsg.reportStatus("\""+selected.getTitle()+"\" selected");
	}
	public void JSLFrameClosing(JSLFrameEvent e) {
	    JSLFrame f = e.getJSLFrame();
	    if (f == toolbar) {
		if (windows.size() < 2 && invisible == null) {
		    PatchEdit.exitAction.exit();
		} else {
		    ErrorMsg.reportStatus("\"" + f.getTitle() + "\" hidden. " +
					  (windows.size() - 1)
					  + " windows still open.");
		}
	    }
	}
	public void JSLFrameClosed(JSLFrameEvent e) {
	    JSLFrame f = e.getJSLFrame();
	    windows.remove(f);
	    if (windows.size() < 2) {
		// toolbar should never be closed, only hidden
		if (toolbar.isVisible()) {
		    selected = toolbar.getJFrame();
		    ErrorMsg.reportStatus("\""+selected.getTitle()+
					  "\" selected");
		} else if (invisible == null) {
		    PatchEdit.exitAction.exit();
		} else {
		    ErrorMsg.reportStatus("All windows closed, but invisible "+
					  "frame exists. Not exiting.");
		}
	    } else {
		ErrorMsg.reportStatus("\""+e.getJSLFrame().getTitle()+
				      "\" closed. " + (windows.size() - 1) + 
				      " windows still open.");
	    }
	}
	public void JSLFrameDeactivated(JSLFrameEvent e) {
	    try {
		last_selected = (JSLFrame.JSLJFrame)e.getJSLFrame().getJFrame();
	    } catch (Exception ex) {
		last_selected = null;
	    }
	}
	public void JSLFrameDeiconified(JSLFrameEvent e) {}
	public void JSLFrameIconified(JSLFrameEvent e) {}
	public void JSLFrameOpened(JSLFrameEvent e) {}

	public void showToolBar() {
	    toolbar.setVisible(true);
	}
	public JSLFrame getToolBar() { return toolbar; }
	public JFrame getLastSelectedWindow() { return last_selected; }
    }
}
