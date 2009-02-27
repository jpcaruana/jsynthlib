package core;
/**
 * Frame Event Listner for JSLFrame.
 * 
 * @see JSLFrame
 * @see javax.swing.event.InternalFrameListener
 * @author Rib Rdb
 */
public interface JSLFrameListener {
    /** Invoked when a internal frame has been opened. */
    void JSLFrameOpened(JSLFrameEvent e);
    /**
     * Invoked when an internal frame is in the process of being closed. The
     * close operation can be overridden at this point.
     */
    void JSLFrameClosing(JSLFrameEvent e);
    /** Invoked when an internal frame has been closed. */
    void JSLFrameClosed(JSLFrameEvent e);
    /** Invoked when an internal frame is iconified. */
    void JSLFrameIconified(JSLFrameEvent e);
    /** Invoked when an internal frame is de-iconified. */
    void JSLFrameDeiconified(JSLFrameEvent e);
    /** Invoked when an internal frame is activated. */
    void JSLFrameActivated(JSLFrameEvent e);
    /** Invoked when an internal frame is de-activated. */
    void JSLFrameDeactivated(JSLFrameEvent e);
}