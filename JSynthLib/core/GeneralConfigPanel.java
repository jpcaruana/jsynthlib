package core;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * ConfigPanel for generic parameters.
 * @author Joe Emenaker
 * @version $Id$
 */
public class GeneralConfigPanel extends ConfigPanel {
    {
	panelName =  "General";
	nameSpace =  "general";
    }

    private JComboBox cbLF;
    private JComboBox cbGS;
    private static UIManager.LookAndFeelInfo[] installedLF;
    static {
	installedLF = UIManager.getInstalledLookAndFeels();
    }

    public GeneralConfigPanel(PrefsDialog parent) {
	super(parent);
	setLayout(new core.ColumnLayout());

	JLabel l0 = new JLabel("GUI Look and Feel:");
	cbLF = new JComboBox();
	for (int j = 0; j < installedLF.length; j++)
	    cbLF.addItem(installedLF[j].getName());
	cbLF.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    setModified(true);
		}
	    });
	JPanel p = new JPanel();
	p.add(l0);
	p.add(cbLF);
	add(p);

	JLabel l1 = new JLabel("GUI Style:                 ");
	cbGS = new JComboBox(new String[] {
	    "MDI (Single Window)",
	    "SDI (Multiple Windows)",
	});
	cbGS.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    setModified(true);
		}
	    });
	p = new JPanel();
	p.add(l1);
	p.add(cbGS);
	add(p);
    }

    void init() {
	cbLF.setSelectedIndex(AppConfig.getLookAndFeel());
	cbGS.setSelectedIndex(AppConfig.getGuiStyle());
    }

    void commitSettings() {
	if (AppConfig.getLookAndFeel() != cbLF.getSelectedIndex()) {
            AppConfig.setLookAndFeel(cbLF.getSelectedIndex());
            PatchEdit.getDesktop().updateLookAndFeel();
	}
	if (AppConfig.getGuiStyle() != cbGS.getSelectedIndex()) {
            JOptionPane.showMessageDialog(
                    null,
                    "You must exit and restart the program for your changes to take effect",
                    "Changing GUI Style",
                    JOptionPane.INFORMATION_MESSAGE);
            AppConfig.setGuiStyle(cbGS.getSelectedIndex());
        }
    }
}
