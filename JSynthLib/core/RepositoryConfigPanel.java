
package core; //TODO org.jsynthlib;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/** Abstract class that is used by PrefsDialog to load an arbitrary
 *  number of configuration tabs.
 * @author Joe Emenaker
 * @version $Id$
 */

public class RepositoryConfigPanel extends ConfigPanel {
	private final JTextField t1=new JTextField (null,20);
	private final JTextField t2=new JTextField (null,20);
	private final JPasswordField t3=new JPasswordField (null,20);

	public RepositoryConfigPanel(core.AppConfig appConfig) {
		super(appConfig);
		setLayout (new core.ColumnLayout ());
		JLabel l1=new JLabel ("Repository Site: ");
		JPanel p1=new JPanel ();
		JLabel l2=new JLabel ("User Name:        ");
		JPanel p2=new JPanel ();
		JPanel p3=new JPanel();
		JLabel l3=new JLabel ("Password:          ");
		JLabel l0=new JLabel("Before you can upload patches to a repository, You will need to go to there");
		JLabel l00=new JLabel("in your web browser and make an account if you do not have one.");
		p1.add (l1);
		p2.add (l2);
		p1.add (t1);
		p2.add (t2);
		p3.add(l3);
		p3.add(t3);

		add (l0);
		add (l00);
		add (p1);
		add (p2);
		add (p3);
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
		t1.setText (appConfig.getRepositoryURL());
		t2.setText (appConfig.getRepositoryUser());
		t3.setText (appConfig.getRepositoryPass());
	}

	/**
	* This is the opposite of init(). The implementing class should
	* copy all GUI settings to internal data elements (and also
	* save those settings in to the preference-saving system, if any
	*/
	public void commitSettings() {
		appConfig.setRepositoryURL(t1.getText());
		appConfig.setRepositoryUser(t2.getText());
		appConfig.setRepositoryPass(new String(t3.getPassword()));
	}

	/**
	* This should return the name that should go on the tab (if using a tabbed config dialog).
	* Otherwise, it could be used for a frame title, etc. However, this can be overridden by
	* certain constructors.
	*/
	protected String getDefaultPanelName() {
		return("Repository");
	}

	/**
	* This should return the AppConfig namespace to use if one isn't specified in the constructor
	*/
	protected String getDefaultNamespace() {
		return("repository");
	}
}
