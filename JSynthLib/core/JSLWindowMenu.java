package core;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.lang.ref.*;

public class JSLWindowMenu extends JMenu implements WindowListener,
						    ActionListener {
    protected ButtonGroup bg = new ButtonGroup();
    protected HashMap windows = new HashMap();
    protected JSLWindowMenuItem none = new JSLWindowMenuItem();
    private Boolean doing_selection = Boolean.FALSE;
    private static Action close_action = new AbstractAction("Close") {
	    public void actionPerformed(ActionEvent ex) {
		try {
		    JFrame f = JSLDesktop.getSelectedWindow();
		    ((JSLFrame.JSLFrameProxy)f).getJSLFrame().setClosed(true);
		} catch (Exception e) {
		    if (JSLDesktop.getInstance() == null) {
		        ErrorMsg.reportStatus("Error: No JSLDesktkop. Can't close"
					   + " any windows.");
		    } else if (JSLDesktop.getSelectedFrame() == null) {
		        ErrorMsg.reportStatus("Error: No selected JSLFrame. " +
					   "Can't close window.");
		    } else {
			e.printStackTrace();
		    }
		}
	    }
	};

    public JSLWindowMenu() {
	super("Window");
	if (!MacUtils.isMac()) {
	    add(Actions.prefsAction);	// XXX
	    setMnemonic(KeyEvent.VK_W); // XXX
	}
	add(Actions.monitorAction);	// XXX
	//add(JSLDesktop.toolBarAction);
	addSeparator();
        int mask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
	JMenuItem mi = add(close_action);
	mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, mask));
	addSeparator();
    }
    public void add(JFrame f) {
	if (f == null)
	    return;
	f.addWindowListener(this);
	JSLWindowMenuItem i = new JSLWindowMenuItem(f);
	i.addActionListener(this);
	add(i);
	bg.add(i);
	windows.put(f, i);
    }
    public void remove(JFrame f) {
	if (f == null)
	    return;
	JSLWindowMenuItem i = (JSLWindowMenuItem)windows.remove(f);
	remove(i);
	bg.remove(i);
    }
    public void setSelectedWindow(JFrame f) {
	if (!windows.containsKey(f))
	    bg.setSelected(none, true);
	else
	    bg.setSelected((JSLWindowMenuItem)windows.get(f), true);
    }

    // WindowListener methods
    public void windowActivated(WindowEvent e) {
	try {
	    JFrame f = (JFrame)e.getWindow();
	    bg.setSelected((JSLWindowMenuItem)windows.get(f), true);
	} catch (Exception ex) {}
    }
    public void windowClosed(WindowEvent e) {
	if (windows.containsKey(e.getWindow())) {
	    JFrame f = (JFrame)e.getWindow();
	    remove(f);
	}
    }
    public void windowClosing(WindowEvent e) {}
    public void windowDeactivated(WindowEvent e) {}
    public void windowDeiconified(WindowEvent e) {}
    public void windowIconified(WindowEvent e) {}
    public void windowOpened(WindowEvent e) {}
    // end of WindowListener methods

    // ActionListener method
    public void actionPerformed(java.awt.event.ActionEvent e) {
	if (e.getSource() instanceof JSLWindowMenuItem) {
	    JFrame f = ((JSLWindowMenuItem)e.getSource()).getJFrame();
	    synchronized (doing_selection) {
		if (doing_selection.booleanValue())
		    return;
		doing_selection = Boolean.TRUE;
		f.setVisible(true);
		//f.toFront();
		doing_selection = Boolean.FALSE;
	    }
	}
    }
}
class JSLWindowMenuItem extends JRadioButtonMenuItem implements ButtonModel {
    private WeakReference f;
    protected boolean roll = false, press = false;
    JSLWindowMenuItem() {
	super();
	f = null;
    }
    public JSLWindowMenuItem(JFrame frame) {
	super(frame.getTitle());
	f = new WeakReference(frame);
    }
    public void setGroup(ButtonGroup g) {}
    public void setRollover(boolean b) { roll = b;}
    public void setPressed(boolean b) {press = b;}
    public boolean isRollover() { return roll; }
    public boolean isPressed() { return press; }
    public JFrame getJFrame() { return (JFrame)f.get(); }
}
