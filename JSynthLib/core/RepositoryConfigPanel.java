package core;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

/**
 * ConfigPanel for MIDI patch download repository.
 * @author Brian Klock
 * @author Hiroo Hayashi
 * @version $Id$
 */
class RepositoryConfigPanel extends ConfigPanel {
    {
	panelName = "Repository";
	nameSpace = "repository";
    }

    private final JTextField t1 = new JTextField(null, 20);
    private final JTextField t2 = new JTextField(null, 20);
    private final JPasswordField t3 = new JPasswordField(null, 20);

    RepositoryConfigPanel(PrefsDialog parent) {
	super(parent);
	setLayout(new BorderLayout());
	JPanel p = new JPanel(new GridBagLayout());
	GridBagConstraints c = new GridBagConstraints();
	c.fill = GridBagConstraints.HORIZONTAL;
	c.insets = new Insets(10, 0, 0, 0);
	
	JLabel l = new JLabel("Patch Repository:");
	l.setToolTipText("Before uploading patches to a repository, You need to make an account.");
	c.gridx = 0; c.gridy = 0; c.gridwidth = 1;
	p.add(l, c);

	c.gridx = 0; c.gridy++;
	p.add(new JLabel("Repository Site:"), c);
	c.gridx = 1;
	p.add(t1, c);

	c.gridx = 0; c.gridy++;
	p.add(new JLabel("User Name:"), c);
	c.gridx = 1;
	p.add(t2, c);

	c.gridx = 0; c.gridy++;
	p.add(new JLabel("Password:"), c);
	c.gridx = 1;
	p.add(t3, c);

	add(p, BorderLayout.CENTER);

	CaretListener cl = new CaretListener() {
		public void caretUpdate(CaretEvent e) {
		    setModified(true);
		}
	    };
	t1.addCaretListener(cl);
	t2.addCaretListener(cl);
	t3.addCaretListener(cl);
    }

    void init() {
	t1.setText(AppConfig.getRepositoryURL());
	t2.setText(AppConfig.getRepositoryUser());
	t3.setText(AppConfig.getRepositoryPass());
    }

    public void commitSettings() {
	AppConfig.setRepositoryURL(t1.getText());
	AppConfig.setRepositoryUser(t2.getText());
	AppConfig.setRepositoryPass(new String(t3.getPassword()));
	setModified(false);
    }
}
