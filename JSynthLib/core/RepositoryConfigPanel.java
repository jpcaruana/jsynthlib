package core;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

/**
 * ConfigPanel for MIDI patch download repository.
 * @author Brian Klock
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

    RepositoryConfigPanel(PrefsDialog parent, AppConfig appConfig) {
	super(parent, appConfig);
	setLayout(new ColumnLayout());

	JLabel ll0 = new JLabel("Before you can upload patches to a repository, You will need to go to there");
	JLabel ll1 = new JLabel("in your web browser and make an account if you do not have one.");

	JPanel p1 = new JPanel();
	JLabel l1 = new JLabel("Repository Site: ");
	p1.add(l1);
	p1.add(t1);

	JPanel p2 = new JPanel();
	JLabel l2 = new JLabel("User Name:        ");
	p2.add(l2);
	p2.add(t2);

	JPanel p3 = new JPanel();
	JLabel l3 = new JLabel("Password:          ");
	p3.add(l3);
	p3.add(t3);

	add(ll0);
	add(ll1);
	add(p1);
	add(p2);
	add(p3);

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
	t1.setText(appConfig.getRepositoryURL());
	t2.setText(appConfig.getRepositoryUser());
	t3.setText(appConfig.getRepositoryPass());
    }

    public void commitSettings() {
	appConfig.setRepositoryURL(t1.getText());
	appConfig.setRepositoryUser(t2.getText());
	appConfig.setRepositoryPass(new String(t3.getPassword()));
	setModified(false);
    }
}
