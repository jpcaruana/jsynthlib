package core;

public interface JSLFrameListener {
    void JSLFrameActivated(JSLFrameEvent e);
    void JSLFrameClosed(JSLFrameEvent e);
    void JSLFrameClosing(JSLFrameEvent e);
    void JSLFrameDeactivated(JSLFrameEvent e);
    void JSLFrameDeiconified(JSLFrameEvent e);
    void JSLFrameIconified(JSLFrameEvent e);
    void JSLFrameOpened(JSLFrameEvent e);
}