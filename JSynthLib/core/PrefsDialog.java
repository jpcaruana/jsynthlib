/**
 * @version $Id$
 */
package core;

import javax.swing.JFrame;
import javax.swing.JDialog;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import java.awt.event.*;
import java.awt.*;
//TODO import org.jsynthlib.*;

public class PrefsDialog extends JDialog {
    private static final int OK = 0x01;
    private static final int CANCEL = 0x02;
    private static final int APPLY = 0x04;
    private static final int OK_CANCEL = OK | CANCEL;
    private static final int OK_CANCEL_APPLY = OK | CANCEL | APPLY;

    // We don't need appConfig object anymore. All panels handle it themselves - emenaker 2003.03.17
    // AppConfig object used to store actual config values
    //private AppConfig appConfig = null;

    private JFrame p;
    private Vector configpanels;
    private int buttonsAllowed = OK_CANCEL_APPLY;

    /**
     * Constructor
     * @param Parent the parent JFrame
     */
    public PrefsDialog (JFrame parent /*, AppConfig appConfig*/) {
        super(parent, "User Preferences", true);
        p = parent;
	// this.appConfig = appConfig;
	configpanels = new Vector();
    }

    /**
     * Allows for different buttons to be enabled on the dialog box
     * @param Parent Parent frame
     * @param buttonsAllowed Either OK, OK_CANCEL, or OK_CANCEL_APPLY
     */
    public PrefsDialog (JFrame parent, int buttonsAllowed) {
	this(parent);
	this.buttonsAllowed = buttonsAllowed;
    }

    public void init () {
	/* Regenerate the entire thing...
	 * It is possible that some of the panels that we were using have, since, been used in
	 * other containers (like a stand-alone frame for one of the panels). If this happened,
	 * then the Java Swing/AWT system automatically took that panel OUT of our container
	 * (because a component can only have one parent container). To "reclaim" those panels,
	 * we need to start over with a new panel and add the config panels. - emenaker 2003.03.17
	 */
	JPanel container = new JPanel ();
	container.setLayout (new BorderLayout ());

	/*
	 * We want different behaviors if we contain 0, 1, or many ConfigPanels. To do this,
	 * the switch block sets the "contents" Component to something depending upon what we
	 * need. At the end, this component is assed to the main panel. - emenaker 2003.03.26
	 */
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
	    JTabbedPane jtp = new JTabbedPane ();

	    // Add all ConfigPanels to tabs - emenaker
	    Enumeration enum = configpanels.elements();
	    while (enum.hasMoreElements()) {
		ConfigPanel cp = (ConfigPanel) enum.nextElement();
		cp.init();
		jtp.addTab(cp.getPanelName(), cp);
	    }

	    jtp.addChangeListener (new ChangeListener () {
		    public void stateChanged (ChangeEvent e) {
			//System.out.println ("Tab changed: "+((JTabbedPane)e.getSource ()).getSelectedIndex ());
		    }
		});
	    contents = jtp;
	    break;
	}
	container.add (contents, BorderLayout.NORTH);

	// Populate the button panel with OK, Cancel, and Apply buttons
        JPanel buttonPanel = new JPanel ();
        buttonPanel.setLayout (new FlowLayout (FlowLayout.CENTER));

	if ((buttonsAllowed & OK) > 0) {
	    JButton ok = new JButton ("OK");
	    ok.addActionListener (new ActionListener () {
		    public void actionPerformed (ActionEvent e) {
			commitChanges();
        	        exit();
		    }
		});
	    buttonPanel.add(ok);
	    getRootPane().setDefaultButton (ok);
	}

	if ((buttonsAllowed & CANCEL) > 0) {
	    JButton cancel = new JButton ("Cancel");
	    cancel.addActionListener (new ActionListener () {
		    public void actionPerformed (ActionEvent e) {
			exit ();
		    }
		});
	    buttonPanel.add (cancel);
	}

	// TODO: The Apply button should be disabled unless some value is actually changed - emenaker 2003.03.14
	if ((buttonsAllowed & APPLY) > 0) {
	    JButton apply = new JButton ("Apply");
	    apply.addActionListener (new ActionListener () {
		    public void actionPerformed (ActionEvent e) {
			commitChanges ();
		    }
		});
	    buttonPanel.add (apply);
	}

        container.add (buttonPanel, BorderLayout.SOUTH);
        getContentPane ().add (container);
	pack ();
	centerDialog ();
    }

    /**
     * This adds a tab to the configuration dialog box. Actually, it
     * merely adds it to the vector which holds all of the panels. The
     * actual tabbed panes won't be created unless you call
     * this.init() - emenaker 2003.03.12
     * @param panel The panel to add to the configuration dialog
     */
    public void addPanel(ConfigPanel panel) {
	configpanels.add(panel);
    }

    public void show () {
        super.show ();
    }

    /**
     * This centers the JDialog on the screen.
     * TODO Move this to a static method in some utility class so that we can call it
     * for any Component from anywhere. - emenaker 2003.03.26
     *
     */
    protected void centerDialog () {
        Dimension screenSize = this.getToolkit ().getScreenSize ();
        Dimension size = this.getSize ();
        screenSize.height = screenSize.height / 2;
        screenSize.width = screenSize.width / 2;
        size.height = size.height / 2;
        size.width = size.width / 2;
        int y = screenSize.height - size.height;
        int x = screenSize.width - size.width;
        this.setLocation (x, y);
    }

    /**
     * This used to be OKPressed, but I changed it to allow for the "Apply" button - emenaker 2003.03.12
     *
     */
    private void commitChanges () {
	// Tell every ConfigPanel to save their settings to appConfig. - emenaker 2003.03.12
	Enumeration enum = configpanels.elements();
	while (enum.hasMoreElements()) {
	    ((ConfigPanel) enum.nextElement()).commitSettings();
	}
    }

    /**
     * All this does is hide the dialog box. It was moved out of commitChanges (which used to be called
     *  OKPressed) so that the "Apply" button can save changes without exiting and the "Cancel" button
     * can exit without saving changes. - emenaker 2003.03.14
     */
    private void exit() {
	this.setVisible (false);
    }
}
