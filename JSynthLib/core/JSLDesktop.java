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
		PatchEdit.desktop.showToolBar();
	    }
	};

    public JSLDesktop() {
	if (JSLFrame.useInternalFrames()) {
	    proxy = new JSLJDesktop();
	} else {
	    proxy = new JSLFakeDesktop();
	}
    }

    public JSLFrame[] getAllFrames() { return proxy.getAllJSLFrames(); }
    public JSLFrame getSelectedFrame() { return proxy.getSelectedJSLFrame(); }
    public static JFrame getSelectedWindow() { return selected; }
    public Dimension getSize() { return proxy.getSize(); }
    public void add(JSLFrame f) { proxy.add(f); }
    public JMenu createWindowMenu() { return proxy.createWindowMenu(); }
    void registerFrame(JSLFrame f) { proxy.registerFrame(f); }
    void setupInitialMenuBar(JToolBar tb) { proxy.setupInitialMenuBar(tb); }
    public void showToolBar() { proxy.showToolBar(); }
    JSLFrame getToolBar() { return proxy.getToolBar(); }
    JSLFrame getInvisible() { return proxy.getInvisible(); }
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
			PatchEdit.appConfig.savePrefs();
			// We shouldn't need to unload the midi driver if
			// the whole JVM is going away.
			// unloadMidiDriver();
			System.exit(0);
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
	    menuWindow.add(PatchEdit.synthAction);
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
    }
    private class JSLFakeDesktop implements JSLDesktopProxy, JSLFrameListener {
	protected ArrayList windows = new ArrayList();
	protected ArrayList windowMenus = new ArrayList();
	protected JSLFrame toolbar;
	private JSLFrame invisible = null;

	JSLFakeDesktop() {
	    if (MacUtils.isMac()) {
		// Create invisible window to keep menus when no open windows
		invisible = new JSLFrame();
		invisible.setTitle("Please enable ScreenMenuBar.");
		invisible.getJFrame().setDefaultCloseOperation(
		        JFrame.DO_NOTHING_ON_CLOSE);
	    }
	    toolbar = new JSLFrame("JSynthLib",false,false,false,false);
	    toolbar.getJFrame().setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
	}
	public void setupInitialMenuBar(JToolBar tb) {
	    if (invisible != null) {
		selected = invisible.getJFrame();
		selected.setJMenuBar(PatchEdit.createMenuBar());
		selected.setSize(0,0);
		//selected.setLocation(0,0x7FFFFFFF);
		selected.pack();
		selected.setVisible(true);
		//selected.addWindowListener(this);
	    }

	    //registerFrame(toolbar);
	    toolbar.setJMenuBar(PatchEdit.createMenuBar());
	    tb.setFloatable(false);
	    toolbar.getContentPane().add(tb);
	    toolbar.pack();
	    toolbar.setVisible(true);
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
		return (JSLFrame)
		    ((JSLFrame.JSLFrameProxy)selected).getJSLFrame();
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
	    selected = e.getJSLFrame().getJFrame();
	    ErrorMsg.reportStatus("\""+selected.getTitle()+"\" selected");
	}
	public void JSLFrameClosed(JSLFrameEvent e) {
	    windows.remove(e.getJSLFrame());
	    if (windows.size() < 1) {
		if (toolbar.isVisible()) {
		    selected = toolbar.getJFrame();
		    ErrorMsg.reportStatus("\""+selected.getTitle()+
					  "\" selected");
		} else if (invisible == null) {
		    PatchEdit.appConfig.savePrefs();
		    // We shouldn't need to unload the midi driver if
		    // the whole JVM is going away.
		    // unloadMidiDriver();
		    System.exit(0);
		}
	    }
		
	}
	public void JSLFrameClosing(JSLFrameEvent e) {}
	public void JSLFrameDeactivated(JSLFrameEvent e) {}
	public void JSLFrameDeiconified(JSLFrameEvent e) {}
	public void JSLFrameIconified(JSLFrameEvent e) {}
	public void JSLFrameOpened(JSLFrameEvent e) {}

	public void showToolBar() {
	    toolbar.setVisible(true);
	}
	public JSLFrame getToolBar() { return toolbar; }
    }
}