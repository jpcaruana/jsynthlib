package core;

import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.*;
import java.io.*;

/**
 * ConfigPanel for directory setting.
 * @author Joe Emenaker
 * @author Hiroo Hayashi
 * @version $Id$
 */

class DirectoryConfigPanel extends ConfigPanel {
    {
	panelName = "Directories";
	nameSpace = "directories";
    }

    private final JTextField t1 = new JTextField(null, 35);
    private final JTextField t2 = new JTextField(null, 35);

    DirectoryConfigPanel(PrefsDialog parent) {
	super(parent);
	setLayout(new BorderLayout());
	JPanel p = new JPanel(new GridBagLayout());
	GridBagConstraints c = new GridBagConstraints();

	c.gridx = 0; c.gridy = 0; c.gridwidth = 3; c.fill = GridBagConstraints.HORIZONTAL;
	c.insets = new Insets(10, 0, 0, 0);
	p.add(new JLabel("Default Directories:"), c);

	t1.setEditable(false);
	t2.setEditable(false);

	c.gridx = 0; c.gridy++; c.gridwidth = 1;
	p.add(new JLabel("Patch Library Path:"), c);
	c.gridx = 1;
	p.add(t1, c);

	JButton b = new JButton("Browse");
	b.addActionListener(new ActionListener() {
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
	c.gridx = 2;
	p.add(b, c);

	c.gridx = 0; c.gridy++;
	p.add(new JLabel("Sysex File Path:"), c);
	c.gridx = 1;
	p.add(t2, c);
	b = new JButton("Browse");
	b.addActionListener(new ActionListener() {
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
	c.gridx = 2;
	p.add(b, c);

	add(p, BorderLayout.CENTER);
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
