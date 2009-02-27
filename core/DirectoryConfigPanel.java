package core;

import javax.swing.*;

import core.Actions.ExtensionFilter;

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
	panelName = "File & Directories";
	nameSpace = "fileAndDirectories";
    }

    private final JTextField tFile = new JTextField(null, 35);
    private final JTextField tLib  = new JTextField(null, 35);
    private final JTextField tSyx  = new JTextField(null, 35);

    DirectoryConfigPanel(PrefsDialog parent) {
	super(parent);
	setLayout(new BorderLayout());
	JPanel p = new JPanel(new GridBagLayout());
	GridBagConstraints c = new GridBagConstraints();

	c.gridx = 0; c.gridy = 0; c.gridwidth = 3; c.fill = GridBagConstraints.HORIZONTAL;
	c.insets = new Insets(10, 0, 0, 0);
	p.add(new JLabel("Default File and Directories:"), c);
	c.gridwidth = 1;
	JButton b;

	// default library file
	c.gridx = 0; c.gridy++;
	p.add(new JLabel("Default Library File:"), c);
	c.gridx = 1;
	p.add(tFile, c);

	b = new JButton("Browse...");
	b.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    CompatibleFileDialog fc = new CompatibleFileDialog();
		    fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		    if (tFile.getText() != null)
			fc.setSelectedFile(new File(tFile.getText()));
		    // set filter
		    String lext = LibraryFrame.FILE_EXTENSION;
	            ExtensionFilter filter = new ExtensionFilter(
	                    "JSynthLib Library Files (*" + lext + ")",
	                    new String[] { lext });
	            fc.addChoosableFileFilter(filter);
	            fc.setFileFilter(filter);

	            fc.showDialog(PatchEdit.getInstance(),
				  "Choose Default Library File");
		    if (fc.getSelectedFile() != null) {
			tFile.setText(fc.getSelectedFile().getPath());
			setModified(true);
		    }
		}
	    });
	c.gridx = 2;
	p.add(b, c);

	// default library path
	c.gridx = 0; c.gridy++;
	p.add(new JLabel("Patch Library Path:"), c);
	c.gridx = 1;
	p.add(tLib, c);

	b = new JButton("Browse...");
	b.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    CompatibleFileDialog fc = new CompatibleFileDialog();
		    fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		    if (tLib.getText() != null)
			fc.setSelectedFile(new File(tLib.getText()));

		    fc.showDialog(PatchEdit.getInstance(),
				  "Choose Default Patch Library Directory");
		    if (fc.getSelectedFile() != null) {
			tLib.setText(fc.getSelectedFile().getPath());
			setModified(true);
		    }
		}
	    });
	c.gridx = 2;
	p.add(b, c);

	// default Sysex Path
	c.gridx = 0; c.gridy++;
	p.add(new JLabel("Sysex File Path:"), c);
	c.gridx = 1;
	p.add(tSyx, c);

	b = new JButton("Browse...");
	b.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    CompatibleFileDialog fc = new CompatibleFileDialog();
		    fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		    if (tSyx.getText() != null)
			fc.setSelectedFile(new File(tSyx.getText()));

		    fc.showDialog(PatchEdit.getInstance(),
				  "Choose Default Sysex File Directory");
		    if (fc.getSelectedFile() != null) {
			tSyx.setText(fc.getSelectedFile().getPath());
			setModified(true);
		    }
		}
	    });
	c.gridx = 2;
	p.add(b, c);

	add(p, BorderLayout.CENTER);

	tFile.setEditable(false);
	tLib.setEditable(false);
	tSyx.setEditable(false);
    }

    void init() {
	tFile.setText(AppConfig.getDefaultLibrary());
	tLib.setText(AppConfig.getLibPath());
	tSyx.setText(AppConfig.getSysexPath());
    }

    void commitSettings() {
	AppConfig.setDefaultLibrary(tFile.getText());
	AppConfig.setLibPath(tLib.getText());
	AppConfig.setSysexPath(tSyx.getText());
	setModified(false);
    }
}
