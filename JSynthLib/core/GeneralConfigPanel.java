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

    public GeneralConfigPanel(PrefsDialog parent, AppConfig appConfig) {
	super(parent, appConfig);
	setLayout(new core.ColumnLayout());

	JLabel l0 = new JLabel("Application Look and Feel:");
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
	    "SDI (Many Windows)",
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
	cbLF.setSelectedIndex(appConfig.getLookAndFeel());
	cbGS.setSelectedIndex(appConfig.getGuiStyle());
    }

    void commitSettings() {
	if (appConfig.getLookAndFeel() != cbLF.getSelectedIndex()
	    || appConfig.getGuiStyle() != cbGS.getSelectedIndex())
	    JOptionPane.showMessageDialog
		(null,
		 "You must exit and restart the program for your changes to take effect",
		 "Changing L&F / Platform", JOptionPane.INFORMATION_MESSAGE);
	appConfig.setLookAndFeel(cbLF.getSelectedIndex());
	appConfig.setGuiStyle(cbGS.getSelectedIndex());
    }
}
