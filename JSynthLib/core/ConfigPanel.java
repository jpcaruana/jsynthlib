package core;

import java.awt.Component;
import java.awt.Container;
import java.awt.Window;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * An abstract class that is used by PrefsDialog to load an arbitrary
 * number of configuration tabs.
 * @author Joe Emenaker
 * @version $Id$
 * @see PrefsDialog
 */

abstract class ConfigPanel extends JPanel {
    /** The name that goes on the panel's tab */
    protected String panelName;
    /** The root of the namespace in appConfig that the settings go under. */
    protected String nameSpace;

    private PrefsDialog parent;

    /**
     * Use <code>ConfigPanel(AppConfig)</code> or
     * <code>ConfigPanel(AppConfig, String, String)</code>.
     */
    private ConfigPanel() {
    }

    /**
     * This is the main constructor. All it does is make a copy of the
     * appConfig object so that the configuration panel can load/save
     * settings without any other classes needing to know the details
     * about what it's saving.

     * @param parent parent dialog.
     */
    ConfigPanel(PrefsDialog parent) {
	this.parent = parent;
    }

    /**
     * This is a constructor that you'd want to use if you wanted
     * multiple config panels of the same function that controlled
     * different parameters. For example, you might want two identical
     * directory-choosing config panels... one which let the user
     * choose a directory for saving your own patches and another for
     * saving, say, downloaded patches from the net. In that case, the
     * tabs need to have different labels and they need to save their
     * settings to a separate nameSpace in the AppConfig system. This
     * constructor lets you construct panels and define their panel
     * name and their appConfig nameSpace root.

     * @param appConfig The application's configuration setting storage
     * @param panelName The name that goes on the panel's tab
     * @param nameSpace The root of the nameSpace in appConfig that
     * the settings go under
     */
    ConfigPanel(PrefsDialog parent, AppConfig appConfig,
		String panelName, String nameSpace) {
	this(parent);
	// Set these AFTER calling this(), since the default values
	// are set there... and we need to replace those.
	this.panelName = panelName;
	this.nameSpace = nameSpace;
    }

    /**
     * Return the name that should go on the tab (if using a tabbed
     * config dialog). Otherwise, it could be used for a frame title,
     * etc.
     */
    final String getPanelName() {
	return panelName;
    }

    /**
     * Return the name space.
     */
    final String getNameSpace() {
	return nameSpace;
    }

    /**
     * This is called every time the config dialog box is opened.  It
     * tells the implementing class to set all GUI elements to
     * properly reflect the internal data. This is because we are not
     * assured that the GUI's are not set to a state that does NOT
     * match the internal data (in cases where the user changes some
     * stuff and then hits "Cancel".
     */
    abstract void init();

    /**
     * This is the opposite of init(). The implementing class should
     * copy all GUI settings to internal data elements (and also save
     * those settings in to the preference-saving system, if any.
     */
    abstract void commitSettings();

    /**
     * This is for future use. The idea is that, in some cases, we
     * might want to instantiate a dialog/frame of JUST this panel
     * (ie, prompting the user to pick MIDI settings when JSynthLib is
     * run for the first time on a given machine. It's deprecated
     * now... but probably won't be in the future.
     * @deprecated
     */
    final void showInFrame(JFrame parent) { // not used now
	// Make a new, empty prefsDialog just for us.
	PrefsDialog myOwnDialog = new PrefsDialog(parent);
	myOwnDialog.setTitle(getPanelName());
	myOwnDialog.add(this);
	myOwnDialog.show();
    }

    /**
     * ConfigPanel have to call this method when a parameter is
     * changed or saved. This enable/disable the Apply button.
     */
    void setModified(boolean modified) {
	parent.setModified(modified);
    }

    //
    // Utility Methods (can be moved to utility class)
    //

    /**
     * This navigates up the container hierarchy looking for the first
     * instance of Window. If it finds one, it calls pack(). This is
     * so that, if any ConfigPanel changes any of its components, it
     * can trigger a repack of the window that it's in. - emenaker
     * 2003.03.19
     */
    protected static void repackContainer(Container cont) { // not used now
	// As long as we keep finding parents that aren't a window....
	while (cont != null && !(cont instanceof Window)) {
	    // get that container's parent
	    cont = cont.getParent();
	}
	// If we found a window...
	if (cont != null) {
	    // Pack it
	    ((Window) cont).pack();
	}
    }

    /**
     * Recursively enables or disables all components in a container -
     * emenaker 2003.03.08
     * @param container
     * @param enabled
     */
    protected static void setContainerEnabled(Container container, boolean enabled) {
	Component[] components = container.getComponents();
	for (int i = 0; i < components.length; i++) {
	    components[i].setEnabled(enabled);
	    if (components[i] instanceof Container) {
		setContainerEnabled((Container) components[i], enabled);
	    }
	}
    }
}
