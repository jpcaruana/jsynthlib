package core;

import javax.swing.JFrame;
import javax.swing.JDialog;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import java.awt.event.*;
import java.awt.*;

/**
 * User Preference Dialog Window.
 * @version $Id$
 * @see ConfigPanel
 */
public class PrefsDialog extends JDialog {
    /** Enable `OK' button. */
    public static final int OK = 0x01;
    /** Enable `Cancel' button. */
    public static final int CANCEL = 0x02;
    /** Enable `Apply' button. */
    public static final int APPLY = 0x04;

    private int buttonsAllowed;

    private Vector configpanels;
    private JTabbedPane jtp;
    private JButton ok;
    private JButton cancel;
    private JButton apply;

    /**
     * Creates user preference setting dialog window.
     * Allows for different buttons to be enabled on the dialog box
     * @param parent Parent frame
     * @param buttonsAllowed Either {@link #OK OK}, {@link #APPLY
     * APPLY}, and/or {@link #CANCEL CANCEL}.
     */
    public PrefsDialog(JFrame parent, AppConfig appConfig, int buttonsAllowed) {
        super(parent, "User Preferences", true);
	this.buttonsAllowed = buttonsAllowed;

	// Add the configuration panels
	configpanels = new Vector();
        addPanel(new GeneralConfigPanel(this, appConfig));
	addPanel(new DirectoryConfigPanel(this, appConfig));
	addPanel(new MidiConfigPanel(this, appConfig));
	// FaderBoxConfigPanel() have to be called after MidiIn is initialized.
	addPanel(new FaderBoxConfigPanel(this, appConfig));
	addPanel(new SynthConfigPanel(this, appConfig));
	addPanel(new NoteChooserConfigPanel(this, appConfig));
	addPanel(new RepositoryConfigPanel(this, appConfig));

	init();
    }

    /**
     * Constructor which both {@link #OK OK}, {@link #APPLY APPLY},
     * and {@link #CANCEL CANCEL} button are enabled.
     * @param parent the parent JFrame
     */
    public PrefsDialog(JFrame parent, AppConfig appConfig) {
	this(parent, appConfig, OK | CANCEL | APPLY);
    }

    /**
     * Regenerate the entire thing...
     *
     * It is possible that some of the panels that we were using have,
     * since, been used in other containers (like a stand-alone frame
     * for one of the panels). If this happened, then the Java
     * Swing/AWT system automatically took that panel OUT of our
     * container (because a component can only have one parent
     * container). To "reclaim" those panels, we need to start over
     * with a new panel and add the config panels.
     */
    // emenaker 2003.03.17
    private void init() {
	JPanel container = new JPanel();
	container.setLayout(new BorderLayout());

	// We want different behaviors if we contain 0, 1, or many
	// ConfigPanels. To do this, the switch block sets the
	// "contents" Component to something depending upon what we
	// need. At the end, this component is assed to the main
	// panel. - emenaker 2003.03.26
	Component contents;
	switch(configpanels.size()) {
	case 0:
	    // We have no panels to show
	    contents = new JLabel("No Configuration Panels to display");
	    break;
	case 1:
	    // Only 1 panel. Don't bother using a JTabbedPane. Show it alone
	    contents = (ConfigPanel) configpanels.elementAt(0);
	    break;
	default:
	    // More than 1 panel: We need a JTabbedPane.
	    jtp = new JTabbedPane();

	    // Add all ConfigPanels to tabs - emenaker
	    Enumeration e = configpanels.elements();
	    while (e.hasMoreElements()) {
		final ConfigPanel cp = (ConfigPanel) e.nextElement();
		//cp.init();
		cp.addComponentListener(new ComponentListener() {
			public void componentShown(ComponentEvent e) {
			    cp.init();
			    setModified(false);
			}
			public void componentResized(ComponentEvent e) {
			}
			public void componentMoved(ComponentEvent e) {
			}
			public void componentHidden(ComponentEvent e) {
			}
		    });
		jtp.addTab(cp.getPanelName(), cp);
	    }
	    /*
	    jtp.addChangeListener(new ChangeListener() {
		    public void stateChanged(ChangeEvent e) {
			//ErrorMsg.reportStatus("Tab changed: " + ((JTabbedPane) e.getSource()).getSelectedIndex());
		    }
		});
	    */
	    contents = jtp;
	    break;
	}
	container.add(contents, BorderLayout.NORTH);

	// Populate the button panel with OK, Cancel, and Apply buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

	if ((buttonsAllowed & OK) > 0) {
	    ok = new JButton("OK");
	    ok.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			commitChanges();
        	        exit();
		    }
		});
	    buttonPanel.add(ok);
	    getRootPane().setDefaultButton(ok);
	}

	if ((buttonsAllowed & APPLY) > 0) {
	    apply = new JButton("Apply");
	    apply.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			commitChanges();
		    }
		});
	    buttonPanel.add(apply);
	}

	if ((buttonsAllowed & CANCEL) > 0) {
	    cancel = new JButton("Cancel");
	    cancel.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			exit();
		    }
		});
	    buttonPanel.add(cancel);
	}

        container.add(buttonPanel, BorderLayout.SOUTH);
        getContentPane().add(container);
	pack();
	Utility.centerDialog(this);
    }

    /**
     * This adds a tab to the configuration dialog box. Actually, it
     * merely adds it to the vector which holds all of the panels. The
     * actual tabbed panes won't be created unless you call
     * this.init() - emenaker 2003.03.12
     * @param panel The panel to add to the configuration dialog
     */
    private void addPanel(ConfigPanel panel) {
	configpanels.add(panel);
    }

    /**
     * ConfigPanel have to call this method when a parameter is
     * changed or saved.
     */
    void setModified(boolean modified) {
	if ((buttonsAllowed & APPLY) > 0) {
	    apply.setEnabled(modified);
	}
    }

    /*
    public void show() {
        super.show();
    }
    */
    /*
     * Tell every ConfigPanel to save their settings to appConfig.
     */
    // emenaker 2003.03.12
    /*
    private void commitChanges() {
	// This used to be OKPressed, but I changed it to allow for
	// the "Apply" button - emenaker 2003.03.12
	Enumeration e = configpanels.elements();
	while (e.hasMoreElements()) {
	    ((ConfigPanel) e.nextElement()).commitSettings();
	}
    }
    */

    /**
     * Tell the ConfigPanel shown to save their settings to appConfig.
     */
    private void commitChanges() {
	((ConfigPanel) jtp.getSelectedComponent()).commitSettings();
    }

    /**
     * All this does is hide the dialog box. It was moved out of
     * commitChanges (which used to be called OKPressed) so that the
     * "Apply" button can save changes without exiting and the
     * "Cancel" button can exit without saving changes. - emenaker
     * 2003.03.14
     */
    private void exit() {
	// Work around Mac OS X multiple modal dialog bug.
	try {
	    Thread.sleep(30);
	} catch (Exception e) {
	}
	this.setVisible(false);
    }
}
