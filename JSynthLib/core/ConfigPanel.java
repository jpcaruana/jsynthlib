
package core; //TODO org.jsynthlib;

import javax.swing.*;

/** Abstract class that is used by PrefsDialog to load an arbitrary
 *  number of configuration tabs.
 * @author Joe Emenaker
 * @version $Id$
 */

public abstract class ConfigPanel extends javax.swing.JPanel {
	protected AppConfig appConfig;
	protected String panelName;
	protected String namespace;
	
	public ConfigPanel() {
		/*
		 * If someone subclasses this class and then they override an existing (or define a new)
		 * constructor, then they need to run super(appConfig) in that constructor (or else we won't
		 * get a handler to the appConfig object. Our local appConfig variable will be null and
		 * we'll probably get a NullPointerException that will be tough to track down. As a result, we
		 * want to have a big, nasty error message pop up if they override a constructor without calling
		 * super(...something...). If they don't explicitly call super(....), then they get this one
		 * by default. - emenaker 2003.03.14
		 * 
		 * Probably, the "right" way to do this is to make this constuctor private, but that would
		 * probably cause some compiler error that would be really confusing to most people. - emenaker 2003.03.17 
		 */ 
		ErrorMsg.reportError("Error!",
			"This is the no-argument constructor for the abstract ConfigPanel class.\n"+
			"\n"+
			"If you're reading this message, then that means that the program just tried\n"+
			"to instantiate a subclass of ConfigPanel named:\n"+
			"    \""+getPanelName()+"\"\n"+
			"wherein the subclass overrode a constructor and that overridden constructor\n"+
			"did not explicitly call \"super(appConfig)\"... which is bad.\n"+
			"\n"+
			"If you don't understand what this means, contact the person who\n"+
			"wrote this configuration panel.\n"+
			"\n"+
			"If you ARE the person who wrote this configuration panel and YOU don't\n"+
			"understand, try adding \"super(appConfig);\" as the first line of all of\n"+
			"your constructors. If that doesn't work, then contact Joe Emenaker\n"+
			"(joe@emenaker.com) or someone from the JSynthLib development team."
		);
	}
	
	/**
	 * This is the main constructor. All it does is make a copy of the appConfig object so that
	 * the configuration panel can load/save settings without any other classes needing to know
	 * the details about what it's saving.
	 * @param appConfig The application's configuration setting storage
	 */
	public ConfigPanel(AppConfig appConfig) {
		this.appConfig = appConfig;
		this.panelName = this.getDefaultPanelName();
		this.namespace = this.getDefaultNamespace();
	}

	/**
	 * This is a constructor that you'd want to use if you wanted multiple config panels of the same
	 * function that controlled different parameters. For example, you might want two identical 
	 * directory-choosing config panels... one which let the user choose a directory for saving your own
	 * patches and another for saving, say, downloaded patches from the net. In that case, the tabs need
	 * to have different labels and they need to save their settings to a separate namespace in the
	 * AppConfig system. This constructor lets you construct panels and define their panel name and 
	 * their appConfig namespace root. 
	 * @param appConfig The application's configuration setting storage
	 * @param panelName The name that goes on the panel's tab
	 * @param namespace The root of the namespace in appConfig that the settings go under
	 */
	public ConfigPanel(AppConfig appConfig, String panelName, String namespace) {
		this(appConfig);
		// Set these AFTER calling this(appConfig), since the superclass' getDefaultPanelName and
		// getDefaultNamespace are set there... and we need to replace those.
		this.panelName = panelName;
		this.namespace = namespace;
	}

	/**
	* This is called every time the config dialog box is opened.
	* It tells the implementing class to set all GUI elements to
	* properly reflect the internal data. This is because we are
	* not assured that the GUI's are not set to a state that
	* does NOT match the internal data (in cases where the user
	* changes some stuff and then hits "Cancel".
	*
	* NOTE:On the current implementation the description above is
	* wrong.  This method is called only initialization time of
	* JSynthLib.  I think we need another method which is called
	* every time a dialog box is opened.  show() may be
	* used. -- Mar.06, 2004 Hiroo
	*/
	public abstract void init();
	
	/**
	* This is the opposite of init(). The implementing class should
	* copy all GUI settings to internal data elements (and also
	* save those settings in to the preference-saving system, if any
	*/
	public abstract void commitSettings();
	
	/**
	* This should return the name that should go on the tab (if using
	* a tabbed config dialog). Otherwise, it could be used for a frame
	* title, etc.
	*/
	public final String getPanelName() {
		return(panelName);
	}

	/**
	* This is for future use. The idea is that, in some cases, we might
	* want to instantiate a dialog/frame of JUST this panel (ie, prompting
	* the user to pick MIDI settings when JSynthLib is run for the first
	* time on a given machine. It's deprecated now... but probably won't
	* be in the future.
	* @deprecated
	*/
	public final void showInFrame(JFrame parent) { // not used now
		// Make a new, empty prefsDialog just for us.
		PrefsDialog myOwnDialog = new PrefsDialog(parent);
		myOwnDialog.setTitle(getPanelName());
		myOwnDialog.add(this);
		myOwnDialog.init();
		myOwnDialog.show();
	}

	/**
	 * This is a method that must be defined by the subclass. It determines what name to put
	 * on the frame title bar (if this is displayed in a stand-alone frame) or the name on the
	 * tab (if displayed in a tabbed pane). Notice that the method sets the *Default* name. The
	 * name can be overridden by specifying a different name in the constructor.
	 * @return The default panel name
	 */
	protected abstract String getDefaultPanelName();

	/**
	 * This is a method that must be defined by the subclass. It determines what namespace to use
	 * when saving settings to the AppConfig system. Notice that the method sets the *Default*
	 * namespace. The namespace can be overridden by specifying a different namespace in the
	 * constructor.
	 * @return The default config setting namespace
	 */
	protected abstract String getDefaultNamespace();

	/**
	 * This navigates up the container hierarchy looking for the first instance of Window. If it
	 * finds one, it calls pack(). This is so that, if any ConfigPanel changes any of its components,
	 * it can trigger a repack of the window that it's in. - emenaker 2003.03.19
	 *
	 */
	protected void repackContainer() { // now called by MidiConfigPanel.initNewMidiDriver
		java.awt.Container cont = getParent();
		// As long as we keep finding parents that aren't a window....
		while(cont != null && ! (cont instanceof java.awt.Window)) {
			// get that container's parent
			cont = cont.getParent();
		}
		// If we found a window...
		if(cont != null) {
			// Pack it
			((java.awt.Window) cont).pack();
		}
	}
}
//(setq c-basic-offset 8)
