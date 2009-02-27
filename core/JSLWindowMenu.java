package core;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.ref.WeakReference;
import java.util.HashMap;

import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

/**
 * A Window menu for JSLDesktop and JSLFrame. A JSLFrame is added by
 * <code>add</code> method or is removed when the JSLFrame is closed. A
 * JSLFrame can be activated by the menu entry added.
 * 
 * @see JSLDesktop
 * @see JSLFrame
 * @author Rib Rdb
 * @author Hiroo Hayashi
 */
public class JSLWindowMenu extends JMenu implements JSLFrameListener,
						    ActionListener {
    private ButtonGroup bg = new ButtonGroup();
    private HashMap windows = new HashMap();
    private JSLWindowMenuItem none = new JSLWindowMenuItem();
    private Boolean doing_selection = Boolean.FALSE;

    public JSLWindowMenu(String title) {
	super(title);
    }
    /**
     * Appends a menu item to the end of this menu.
     * @param f JSLFrame to be added.
     * @return the menu item added.
     */
    public JMenuItem add(JSLFrame f) {
	//if (f == null) return;
	f.addJSLFrameListener(this);
	JSLWindowMenuItem i = new JSLWindowMenuItem(f);
	i.addActionListener(this);
	windows.put(f, i);
	bg.add(i);
	return add(i);
    }
    /**
     * Removes the specified menu item from this menu. If there is no popup
     * menu, this method will have no effect.
     * @param f JSLFrame to be added.
     */
    private void remove(JSLFrame f) {
	//if (f == null) return;
        ErrorMsg.reportStatus("JSLWindowMenu.remove : " + f.getTitle());
	if (windows.containsKey(f)) {
            JSLWindowMenuItem i = (JSLWindowMenuItem) windows.remove(f);
            bg.remove(i);
            remove(i);
        }
    }
    private void setSelectedWindow(JSLFrame f) {
	if (windows.containsKey(f))
	    bg.setSelected((JSLWindowMenuItem) windows.get(f), true);
	else
	    bg.setSelected(none, true);	// XXX Why this is required?
    }

    // JSLFrameListener methods
    public void JSLFrameActivated(JSLFrameEvent e) {
        setSelectedWindow(e.getJSLFrame());
    }
    public void JSLFrameClosed(JSLFrameEvent e) {
        remove(e.getJSLFrame());
    }
    public void JSLFrameClosing(JSLFrameEvent e) {}
    public void JSLFrameDeactivated(JSLFrameEvent e) {}
    public void JSLFrameDeiconified(JSLFrameEvent e) {}
    public void JSLFrameIconified(JSLFrameEvent e) {}
    public void JSLFrameOpened(JSLFrameEvent e) {}
    // end of JSLFrameListener methods

    // ActionListener method
    public void actionPerformed(ActionEvent e) {
	if (e.getSource() instanceof JSLWindowMenuItem) {
	    JSLFrame f = ((JSLWindowMenuItem)e.getSource()).getJSLFrame();
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

    private class JSLWindowMenuItem extends JRadioButtonMenuItem implements ButtonModel {
        private WeakReference f;
        protected boolean roll = false, press = false;
        JSLWindowMenuItem() {
            super();
            f = null;
        }

        public JSLWindowMenuItem(JSLFrame frame) {
            super(frame.getTitle());
            f = new WeakReference(frame);
        }
        public void setGroup(ButtonGroup g) {}
        public void setRollover(boolean b) { roll = b;}
        public void setPressed(boolean b) {press = b;}
        public boolean isRollover() { return roll; }
        public boolean isPressed() { return press; }
        public JSLFrame getJSLFrame() { return (JSLFrame) f.get(); }
    }
}
