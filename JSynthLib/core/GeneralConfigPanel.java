
package core; //TODO org.jsynthlib;

import javax.swing.*;

/** Abstract class that is used by PrefsDialog to load an arbitrary
 *  number of configuration tabs.
 * @author Joe Emenaker
 * @version $Id$
 */

public class GeneralConfigPanel extends ConfigPanel {
	UIManager.LookAndFeelInfo [] installedLF;
	JComboBox cb5;

	public GeneralConfigPanel(core.AppConfig appConfig) {
		super(appConfig);
		setLayout (new core.ColumnLayout ());

		JLabel l0=new JLabel ("Application Look and Feel:");
		add (l0);
		installedLF =  UIManager.getInstalledLookAndFeels ();
		cb5 = new JComboBox ();
		for (int j=0; j< installedLF.length;j++)
			cb5.addItem (installedLF[j].getName ());
		add (cb5);
	}

	/**
	* This is called every time the config dialog box is opened.
	* It tells the implementing class to set all GUI elements to
	* properly reflect the internal data. This is because we are
	* not assured that the GUI's are not set to a state that
	* does NOT match the internal data (in cases where the user
	* changes some stuff and then hits "Cancel".
	*/
	public void init() {
		try {
			UIManager.setLookAndFeel (installedLF[appConfig.getLookAndFeel()].getClassName ());
		}catch (Exception e) {};
		cb5.setSelectedIndex (appConfig.getLookAndFeel());
	}
	
	/**
	* This is the opposite of init(). The implementing class should
	* copy all GUI settings to internal data elements (and also
	* save those settings in to the preference-saving system, if any
	* @param appConfig Instance of appConfig for storing settings
	*/
	public void commitSettings() {
		if (appConfig.getLookAndFeel()!=cb5.getSelectedIndex ())
			JOptionPane.showMessageDialog (null, "You must exit and restart the program for your changes to take effect","Changing L&F / Platform", JOptionPane.INFORMATION_MESSAGE);
		appConfig.setLookAndFeel(cb5.getSelectedIndex());
	}
	
	/**
	* This should return the name that should go on the tab (if using a tabbed config dialog).
	* Otherwise, it could be used for a frame title, etc. However, this can be overridden by
	* certain constructors.
	*/
	protected String getDefaultPanelName() {
		return("General");
	}

	/**
	* This should return the AppConfig namespace to use if one isn't specified in the constructor
	*/
	protected String getDefaultNamespace() {
		return("general");
	}


}