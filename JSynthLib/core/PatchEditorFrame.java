package core;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Transmitter;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;

/**
 * A base class of a patch editor.
 *
 * @author ???
 * @version $Id$
 */
public class PatchEditorFrame extends JSLFrame implements PatchBasket {
    /** This is the patch we are working on. */
    protected ISinglePatch p;
    /** Note that calling addWidget() method may change the value of this. */
    protected GridBagConstraints gbc;
    /** Scroll Pane for the editor frame. */
    protected JPanel scrollPane;
    /**
     * A list of slider added by addWidget method.  This was defined
     * for old Java bug.  Now this is not used any more.
     * @deprecated This is not used any more.
     */
    // Used by PatchEdit.EditAction.Worker.  For what is this?!!!FIXIT!!!
    protected ArrayList sliderList = new ArrayList(); //workaround for Java Swing Bug
    /** A list of widget added by addWidget method. */
    protected ArrayList widgetList = new ArrayList();

    /** For Alignment, a size to scrollbar labels, zero disables*/
    protected int forceLabelWidth=0;

    /**
     * Information about BankEditorFrame which created this
     * PatchEditor frame (if applicable) so we can update that frame
     * with the edited data on close.
     */
    protected BankEditorFrame bankFrame = null; // used by YamahaFS1RPerformanceEditor.java

    /** Bank of fader.  Set by faderMoved method. */
    private int faderBank;
    /** Numfer of fader banks.  Set by show method. */
    private int numFaderBanks;

    /**
     * This is a copy of the patch when we started editing (in case
     * user wants to revert).
     */
    private final IPatch originalPatch;
    /** which patch in bank we're editing */
    private int patchRow;
    private int patchCol;
    /** The last recently moved widget by fader. */
    private SysexWidget recentWidget;
    private int lastFader;
    private JScrollPane scroller;

    /**
     * Creates a new <code>PatchEditorFrame</code> instance.
     *
     * @param name a name to display in the title bar.
     * @param patch a <code>Patch</code> value
     */
    protected PatchEditorFrame(String name, ISinglePatch patch) {
        // create a resizable, closable, maximizable, and iconifiable frame.
        super(name, true, true, true, true);
        p = patch;
        // make a backup copy
        originalPatch = (IPatch)p.clone();

        gbc = new GridBagConstraints();
        scrollPane = new JPanel();
        scrollPane.setLayout(new GridBagLayout());
        scrollPane.setSize(600, 400);
        scroller = new JScrollPane(scrollPane);
        getContentPane().add(scroller);
        setSize(600, 400);
        moveToDefaultLocation();

        faderInEnable(AppConfig.getFaderEnable());

        scroller.getVerticalScrollBar().addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                }
                public void mouseReleased(MouseEvent e) {
                    repaint();
                }
            });
        scroller.getHorizontalScrollBar().addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                }
                public void mouseReleased(MouseEvent e) {
                    repaint();
                }
            });
        addJSLFrameListener(new JSLFrameListener() {
                public void JSLFrameClosing(JSLFrameEvent e) {
                    frameClosing();
                }

                public void JSLFrameOpened(JSLFrameEvent e) {
                    frameOpened();
                }

                public void JSLFrameActivated(JSLFrameEvent e) {
                    frameActivated();
                    gotFocus();

                    Actions.setEnabled(false,
                                         Actions.EN_GET
                                         | Actions.EN_PASTE);

                    Actions.setEnabled(true,
                                         Actions.EN_COPY
                                         | Actions.EN_EXPORT
                                         | Actions.EN_PLAY
                                         | Actions.EN_REASSIGN
                                         | Actions.EN_SEND
                                         | Actions.EN_SEND_TO
                                         | Actions.EN_STORE);
                }

                public void JSLFrameClosed(JSLFrameEvent e) {
                }

                public void JSLFrameDeactivated(JSLFrameEvent e) {
                    Actions.setEnabled(false,
                                         Actions.EN_COPY
                                         | Actions.EN_EXPORT
                                         | Actions.EN_PLAY
                                         | Actions.EN_REASSIGN
                                         | Actions.EN_SEND
                                         | Actions.EN_SEND_TO
                                         | Actions.EN_STORE);
                    lostFocus();
                }

                public void JSLFrameDeiconified(JSLFrameEvent e) {
                }

                public void JSLFrameIconified(JSLFrameEvent e) {
                }
            });
    }
	
	/** 
		Called when the frame is closed. Default ask for keep changes.
		May be redefined in sub-classes. 
	*/
	protected void frameClosing()
	{
		String[] choices = new String[] {"Keep Changes",
										"Revert to Original",
										"Place Changed Version on Clipboard"
		};
		int choice;
		do {
			choice = JOptionPane.showOptionDialog
					((Component) null,
                             "What do you wish to do with the changed copy of the Patch?",
                             "Save Changes?",
                             JOptionPane.OK_OPTION, JOptionPane.QUESTION_MESSAGE,
                             null, choices, choices[0]);
		} while (choice == JOptionPane.CLOSED_OPTION);
		if (choice == 0) { // "Keep Changes"
			if (bankFrame != null)
				bankFrame.myModel.setPatchAt(p, patchRow, patchCol);
		} else {
			if (choice == 2) // "Place Changed Version on Clipboard"
						//put on clipboard but don't 'return' just yet
				copySelectedPatch();
			//restore backup
			p.useSysexFromPatch(originalPatch);
			// XXX Why don't we simply do as follows?
			// p = originalPatch;
		}
	}
	
	/** 
		Called when the frame is opened. Default does nothing.
		May be redefined in sub-classes. 
	*/
	protected void frameOpened()
	{
	}
	
	/** 
		Called when the frame is activated. Default send the patch.
		May be redefined in sub-classes. 
	*/
	protected void frameActivated()
	{
	    // XXX: Do we really want to send the patch every
	    //      time the editor gets focus?
            p.send();
	}
	

    // PatchBasket methods

    public ArrayList getPatchCollection() {
        return null;
    }

    public void importPatch (File file) throws FileNotFoundException {
    }

    public void exportPatch (File file) throws FileNotFoundException {
    }

    public void deleteSelectedPatch () {
    }

    public void copySelectedPatch() {
        ClipboardUtil.storePatch(p);
    }
    public IPatch getSelectedPatch() {
        return p;
    }

    public void sendSelectedPatch() {
        p.send();
    }

    public void sendToSelectedPatch() {
        new SysexSendToDialog(p);
    }

    public void reassignSelectedPatch() {
        new ReassignPatchDialog(p);
    }

    public void playSelectedPatch() {
        p.send();
        p.play();
    }

    public void storeSelectedPatch() {
    }

    public JSLFrame editSelectedPatch() {
        return null;
    }

    public void pastePatch() {
    }
    public void pastePatch(IPatch _p) {
    }
    // end of PatchBasket methods

    /**
     * Add <code>SysexWidget</code> <code>widget</code> to
     * <code>JComponent</code> <code>parent</code> by using specified
     * GridBagConstraints.
     *
     * @param parent a parent <code>JComponent</code> to which
     * <code>widget</code> is added.
     * @param widget a <code>SysexWidget</code> to be added.
     * @param gridx see {@link GridBagConstraints#gridx}.
     * @param gridy see {@link GridBagConstraints#gridy}.
     * @param gridwidth see {@link GridBagConstraints#gridwidth}.
     * @param gridheight see {@link GridBagConstraints#gridheight}.
     * @param anchor see {@link GridBagConstraints#anchor}.
     * @param fill see {@link GridBagConstraints#fill}.
     * @param slidernum a slider number.  Only used by ScrollBar Widgets.
     * @see GridBagConstraints
     */
    protected void addWidget(JComponent parent, SysexWidget widget,
                             int gridx, int gridy, int gridwidth, int gridheight,
                             int anchor, int fill,
                             int slidernum) {
        try {
            gbc.gridx = gridx;
            gbc.gridy = gridy;
            gbc.gridwidth = gridwidth;
            gbc.gridheight = gridheight;
            gbc.anchor = anchor;
            gbc.fill = fill;
            parent.add(widget, gbc);

            widgetList.add(widget);

            widget.setSliderNum(slidernum);
            // This may be removed
            if (widget instanceof ScrollBarWidget)
                ((ScrollBarWidget)widget).setForceLabelWidth(forceLabelWidth);
            /*
            if (widget instanceof ScrollBarWidget) {
                sliderList.add(((ScrollBarWidget) widget).slider);
            } else if (widget instanceof VertScrollBarWidget) {
                sliderList.add(((VertScrollBarWidget) widget).slider);
            } else if (widget instanceof ScrollBarLookupWidget) {
                sliderList.add(((ScrollBarLookupWidget) widget).slider);
            }
            */
        } catch (Exception e) {
            ErrorMsg.reportStatus(e);
        }
    }

    /**
     * Add <code>SysexWidget</code> <code>widget</code> to
     * <code>JComponent</code> <code>parent</code> by using specified
     * GridBagConstraints.<p>
     *
     * <code>NORTHEAST</code> is used for the <code>anchor</code>
     * constraint and <code>HORIZONTAL</code> is used for the
     * <code>fill</code> constraint.
     */
    protected void addWidget(JComponent parent, SysexWidget widget,
                             int gridx, int gridy, int gridwidth, int gridheight,
                             int slidernum) {
        this.addWidget(parent, widget, gridx, gridy, gridwidth, gridheight,
                        GridBagConstraints.NORTHEAST, GridBagConstraints.HORIZONTAL, slidernum);
    }

    /**
     * Add <code>SysexWidget</code> <code>widget</code> to
     * <code>scrollPane</code> by using specified
     * GridBagConstraints.<p>
     *
     * <code>EAST</code> is used for the <code>anchor</code>
     * constraint and <code>BOTH</code> is used for the
     * <code>fill</code> constraint.
     */
    protected void addWidget(SysexWidget widget,
                             int gridx, int gridy, int gridwidth, int gridheight,
                             int slidernum) {
        this.addWidget(scrollPane, widget, gridx, gridy, gridwidth, gridheight,
                        GridBagConstraints.EAST, GridBagConstraints.BOTH, slidernum);
    }

    ////////////////////////////////////////////////////////////////////////
    // MIDI Fader Input

    private Transmitter trns;
    private Receiver rcvr;

    private void faderInEnable(boolean enable) {
        if (enable) {
            // get transmitter
            trns = MidiUtil.getTransmitter(AppConfig.getFaderPort());
            rcvr = new FaderReceiver();
            trns.setReceiver(rcvr);
        } else {
            if (trns != null)
                trns.close();
            if (rcvr != null)
                rcvr.close();
        }
    }

    protected void finalize() { // ???
        faderInEnable(false);
    }

    private class FaderReceiver implements Receiver {
        //Receiver interface
        public void close() {
        }

        /**
         * <pre>
         * Control Change MIDI Message
         *   1011nnnn : BnH, nnnn: Voice Channel number
         *   0ccccccc : control # (0-119)
         *   0vvvvvvv : control value
         */
        public void send(MidiMessage message, long timeStamp) {
            // ignore unless Editor Window is active.
            if (!isSelected())
                return;
            ShortMessage msg = (ShortMessage) message;
            if (msg.getCommand() == ShortMessage.CONTROL_CHANGE) {
                int channel = msg.getChannel(); // 0 <= channel < 16
                int controller = msg.getData1(); // 0 <= controller <= 119
                ErrorMsg.reportStatus("FaderReceiver: channel: " + channel
                                      + ", control: " + controller
                                      + ", value: " + msg.getData2());
                // use hash !!!FIXIT!!!
                for (int i = 0; i < Constants.NUM_FADERS; i++) {
                    // faderChannel: 0:channel l, ..., 15:channel 16, 16:off
                    // faderController: 0 <= value < 120, 120:off
                    if ((AppConfig.getFaderChannel(i) == channel)
                        && (AppConfig.getFaderControl(i) == controller)) {
                        faderMoved(i, msg.getData2());
                        break;
                    }
                }
            }
        }
    }

    /**
     * @param fader fader number
     * <pre>
     *  0    : active slider
     *  1-16 : fader 1-16
     * 17-30 : button 1-14
     * 31    : button 15 : prev fader bank
     * 32    : button 16 : next fader bank
     * </pre>
     * @param value data value
     */
    private void faderMoved(int fader, int value) {
        ErrorMsg.reportStatus("FaderMoved: fader: " + fader
                              + ", value: " + value);
        if (fader == 32) {      // button 16 : next fader bank
            faderBank = (faderBank + 1) % numFaderBanks;
            faderHighlight();
            return;
        } else if (fader == 31) { // button 15 : prev fader bank
            faderBank = faderBank - 1;
            if (faderBank < 0)
                faderBank = numFaderBanks - 1;
            faderHighlight();
            return;
        } else if (fader > 16)  // 17-30 : button 1-14
            fader = (byte) (0 - (fader - 16) - (faderBank * 16));
        else                    // 0 : active slder, 1-16 : fader 1-16
            fader += (faderBank * 16);

        if (recentWidget != null) {
            SysexWidget w = recentWidget;
            if (fader == faderBank * 16)
                fader = lastFader;
            if (w.getSliderNum() == fader && w.isShowing()) {
                if (w.getNumFaders() == 1)
                    w.setValue((int) (w.getValueMin() + ((float) (value) / 127 * (w.getValueMax() - w.getValueMin()))));
                else            // EnvelopeWidget
                    w.setFaderValue(fader, (int) value);
                w.repaint();
                return;
            }
        }
        lastFader = fader;

        for (int i = 0; i < widgetList.size(); i++) {
            SysexWidget w = (SysexWidget) widgetList.get(i);
            if ((w.getSliderNum() == fader
                 || (w.getSliderNum() < fader && w.getSliderNum() + w.getNumFaders() > fader))
                && w.isShowing()) {
                if (w.getNumFaders() == 1)
                    w.setValue((int) (w.getValueMin() + ((float) (value) / 127 * (w.getValueMax() - w.getValueMin()))));
                else            // EnvelopeWidget
                    w.setFaderValue(fader, (int) value);
                w.repaint();
                recentWidget = w;
                return;
            }
        }
    }

    /**
     * > P.S. btw, anyone an idea why some labels are grayed-out?<p>
     *
     * If I remember correctly, the label color becomes darker if the label
     * is currently assigned to an active fader. the addWidget calls have the
     * last parameter which defines a fader number. By default the first 16
     * are active. Using the "Next Fader bank" Button on the toolbar makes
     * the next 16 active. Its not supposed to look 'greyed out' though, I
     * just wanted a visual cue about what bank of faders was active. Maybe
     * the color scheme should be changed to be less confusing.<p>
     *
     * Brian
     */
    private void faderHighlight() {
        for (int i = 0; i < widgetList.size(); i++) {
            SysexWidget w = (SysexWidget) widgetList.get(i);
            if (w.getLabel() != null) {
                if (((Math.abs(w.getSliderNum() - 1) & 0xf0)) == faderBank * 16) {
                    Color c = UIManager.getColor("controlText");
                    if (c == null)
                        c = new Color(75, 75, 100);
                    w.getJLabel().setForeground(c);
                } else {
                    Color c = UIManager.getColor("textInactiveText");
                    if (c == null)
                        c = new Color(102, 102, 153);
                    w.getJLabel().setForeground(c);
                }
                w.getJLabel().repaint();
            }
        }
    }

    void nextFader() {
        faderBank = (faderBank + 1) % numFaderBanks;
        faderHighlight();
    }

    /**
     * When showing the dialog, also check how many components there
     * are to determine the number of widget banks needed.
     */
    public void show() {        // override a method of JSLFrame
        int high = 0;
        for (int i = 0; i < widgetList.size(); i++) {
            SysexWidget w = (SysexWidget) widgetList.get(i);
            if ((w.getSliderNum() + w.getNumFaders() - 1) > high)
                high = w.getSliderNum() + w.getNumFaders() - 1;
        }
        numFaderBanks = (high / 16) + 1;
        faderHighlight();
        ErrorMsg.reportStatus("PatchEditorFrame:Show   Num Fader Banks =  "
                              + numFaderBanks);

        Dimension screenSize = JSLDesktop.getSize();
        Dimension frameSize = this.getSize();
        if (frameSize.height > screenSize.height) {
            // Add necessary place for the vertical Scrollbar
            frameSize.width += scroller.getVerticalScrollBar().getPreferredSize().width;
            this.setSize(frameSize.width, screenSize.height);
        }
        if (frameSize.width > screenSize.width) {
            // Add necessary place for the horizontal Scrollbar.
            frameSize.height += scroller.getHorizontalScrollBar().getPreferredSize().height;
            // If the entire frame doen't fit in the window, then
            // rescale it to fit.
            if (frameSize.height > screenSize.height)
                frameSize.height = screenSize.height;
            this.setSize(screenSize.width, frameSize.height);
        }
        super.show();
    } // end of method

    /**
     * Let bankeditorframe set information about itself when it
     * creates a patch editor frame.
     */
    public void setBankEditorInformation(BankEditorFrame bf, int row, int col) { // accessed by YamahaFS1RBankEditor
        bankFrame = bf;
        patchRow = row;
        patchCol = col;
    }

    void revalidateDriver() {
        p.setDriver();
        if (p.hasNullDriver()) {
            try {
                setClosed(true);
            } catch (PropertyVetoException e) {
                ErrorMsg.reportStatus(e);
            }
            return;
        }
    }

    /** A hook called when the frame is activated. */
    protected void gotFocus() {
    }

    /** A hook called when the frame is deactivated. */
    protected void lostFocus() {
    }

    /**
     * return the Patch which is edited.
     */
    public IPatch getPatch() {
        return p;
    }

    /** Tells JSynthLib what the longest Label you plan to add in this
     *  set of Widgets is. This will make sure that sliders are lined up
     *  with each other horizontally. Using this is optional and will
     *  result in sliders that are aligned horizontally for asthetic
     *  reasons. Editors can call this more than once to create multiple
     *  aligned sets of sliders rather than align everything in the entire
     *  editor to one length.
     * @deprecated use the constructor of ScrollBarWidget with
     * labelWidth parameter.
     */
    public void setLongestLabel(String s)
    {
        JLabel j = new JLabel(s);
        Dimension d = j.getPreferredSize();
        forceLabelWidth=(int)d.getWidth();
    }
}
