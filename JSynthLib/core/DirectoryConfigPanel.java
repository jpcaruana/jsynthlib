package core;

import javax.swing.*;
import java.awt.event.*;
import java.io.*;

/**
 * ConfigPanel for directory setting.
 * @author Joe Emenaker
 * @version $Id$
 */

class DirectoryConfigPanel extends ConfigPanel {
    {
	panelName = "Directories";
	nameSpace = "directories";
    }

    private final JTextField t1 = new JTextField(null, 20);
    private final JTextField t2 = new JTextField(null, 20);

    DirectoryConfigPanel(PrefsDialog parent) {
	super(parent);
	setLayout(new ColumnLayout());

	t1.setEditable(false);
	t2.setEditable(false);
	JLabel l0 = new JLabel("Default Directories:");

	JLabel l1 = new JLabel("Patch Library Path: ");
	JPanel p1 = new JPanel();
	JButton b1 = new JButton("Browse");
	b1.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    CompatibleFileDialog fc = new CompatibleFileDialog();
		    fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		    if (t1.getText() != null)
			fc.setSelectedFile(new File(t1.getText()));
		    fc.showDialog(PatchEdit.getInstance(),
				  "Choose Default Patch Library Directory");
		    if (fc.getSelectedFile() != null) {
			t1.setText(fc.getSelectedFile().getPath());
			setModified(true);
		    }
		}
	    });

	JLabel l2 = new JLabel("Sysex File Path:    ");
	JPanel p2 = new JPanel();
	JButton b2 = new JButton("Browse");
	b2.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    CompatibleFileDialog fc = new CompatibleFileDialog();
		    fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		    if (t2.getText() != null)
			fc.setSelectedFile(new File(t2.getText()));
		    fc.showDialog(PatchEdit.getInstance(),
				  "Choose Default Sysex File Directory");
		    if (fc.getSelectedFile() != null) {
			t2.setText(fc.getSelectedFile().getPath());
			setModified(true);
		    }
		}
	    });
	p1.add(l1);
	p1.add(t1);
	p1.add(b1);
	p2.add(l2);
	p2.add(t2);
	p2.add(b2);

	add(l0);
	add(p1);
	add(p2);
    }

    void init() {
	t1.setText(AppConfig.getLibPath());
	t2.setText(AppConfig.getSysexPath());
    }

    void commitSettings() {
	AppConfig.setLibPath(t1.getText());
	AppConfig.setSysexPath(t2.getText());
	setModified(false);
    }
}
