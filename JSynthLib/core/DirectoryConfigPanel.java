
package core; //TODO org.jsynthlib;

import javax.swing.*;
import java.awt.event.*;
import java.io.*;

/** Abstract class that is used by PrefsDialog to load an arbitrary
 *  number of configuration tabs.
 * @author Joe Emenaker
 * @version $Id$
 */

public class DirectoryConfigPanel extends ConfigPanel {
	final JTextField t1=new JTextField (null,20);
	final JTextField t2=new JTextField (null,20);

	public DirectoryConfigPanel(core.AppConfig appConfig) {
		super(appConfig);
		setLayout (new core.ColumnLayout ());
		JLabel l1=new JLabel ("Patch Library Path: ");
		JPanel p1=new JPanel ();
		JLabel l2=new JLabel ("Sysex File Path:       ");
		JPanel p2=new JPanel ();
		JLabel l0=new JLabel ("Default Directories:");
		t1.setEditable (false);
		t2.setEditable (false);
		JButton b1=new JButton ("Browse");
		b1.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent e) {
				JFileChooser fc=new JFileChooser ();
				fc.setFileSelectionMode (JFileChooser.DIRECTORIES_ONLY);
				fc.setCurrentDirectory (new File (getLibPath()));
				fc.setDialogTitle ("Choose Default Directory");
				int returnVal = fc.showOpenDialog (null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile ();
					setLibPath(file.getAbsolutePath());
					t1.setText (getLibPath());
				}
			}
		});
		JButton b2=new JButton ("Browse");
		b2.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent e) {
				JFileChooser fc=new JFileChooser ();
				fc.setFileSelectionMode (JFileChooser.DIRECTORIES_ONLY);
				fc.setCurrentDirectory (new File (getSysexPath()));
				fc.setDialogTitle ("Choose Default Directory");
				int returnVal = fc.showOpenDialog (null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile ();
					setSysexPath(file.getAbsolutePath());
					t2.setText (getSysexPath());
				}
			}
		});
		p1.add (l1);
		p2.add (l2);
		p1.add (t1);
		p2.add (t2);
		p1.add (b1);
		p2.add (b2);
       
		add (l0);
		add (p1);
		add (p2);
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
		t1.setText (getLibPath());
		t2.setText (getSysexPath());
	}
	
	/**
	* This is the opposite of init(). The implementing class should
	* copy all GUI settings to internal data elements (and also
	* save those settings in to the preference-saving system, if any
	* @param appConfig Instance of appConfig for storing settings
	*/
	public void commitSettings() {
	}
	
	/**
	* This should return the name that should go on the tab (if using a tabbed config dialog).
	* Otherwise, it could be used for a frame title, etc. However, this can be overridden by
	* certain constructors.
	*/
	protected String getDefaultPanelName() {
		return("Directories");
	}

	/**
	* This should return the AppConfig namespace to use if one isn't specified in the constructor
	*/
	protected String getDefaultNamespace() {
		return("directories");
	}

	/*
	 * Private methods
	 */
	 
	private String getLibPath() {
		return(appConfig.getLibPath());
	}

	private void setLibPath(String str) {
		appConfig.setLibPath(str);
	}

	private String getSysexPath() {
		return(appConfig.getSysexPath());
	}

	private void setSysexPath(String str) {
		appConfig.setSysexPath(str);
	}
	

}