/* $Id$ */

package core;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.tree.*;
import java.util.Enumeration;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;

public class DeviceAddDialog extends JDialog {

    JTree deviceTree;
    DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Devices");
    //DevicesConfig devConf = null;

    public DeviceAddDialog(JFrame Parent) {
        super(Parent,"Synthesizer Device Install",true);
        JPanel container= new JPanel();
        container.setLayout(new BorderLayout());

	// Read in list of available devices
	//this.devConf = new DevicesConfig();
        //deviceTree = new JTree(PatchEdit.devConfig.deviceNames());

        deviceTree = new JTree(rootNode);
        TreeSelectionModel tsm = new DefaultTreeSelectionModel();
        tsm.setSelectionMode(DefaultTreeSelectionModel.SINGLE_TREE_SELECTION);
        deviceTree.setSelectionModel(tsm);
        //deviceTree.setRootVisible(false);

        String[] devicenames = PatchEdit.devConfig.deviceNames();
        for(int i=0; i<devicenames.length; i++) {
            String manufacturerName = PatchEdit.devConfig.getManufacturerForDevice(devicenames[i]);
            DefaultMutableTreeNode manufNode = null;
            for(Enumeration e = rootNode.children(); e.hasMoreElements();) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
                if(node.getUserObject().equals(manufacturerName)) {
                    manufNode = node;
                }
            }
            if(manufNode == null) {
                manufNode = new DefaultMutableTreeNode(manufacturerName);
                rootNode.add(manufNode);
            }
            manufNode.add(new DefaultMutableTreeNode(devicenames[i]));
        }
        deviceTree.expandRow(0);
        JScrollPane scrollpane = new JScrollPane(deviceTree);
        container.add(scrollpane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout( new FlowLayout(FlowLayout.CENTER) );

        JButton ok = new JButton("OK");
        ok.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    OKPressed();
		}
	    });
        buttonPanel.add( ok );
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    CancelPressed();
		}
	    });
        buttonPanel.add( cancel );

        getRootPane().setDefaultButton(ok);

        container.add(buttonPanel, BorderLayout.SOUTH);
        getContentPane().add(container);
        setSize(400,300);

        Utility.centerDialog(this);
    }

    void OKPressed() {
        this.setVisible(false);
        String s = deviceTree.getSelectionPath().getLastPathComponent().toString();
	if (s == null)
	    return;

	String cls = PatchEdit.devConfig.classNameForDevice(s);
	Device device = AppConfig.addDevice(cls);
	if (device == null)
	    return;

	String info = device.getInfoText();
	if (info != null && info.length() > 0) {
	    JTextArea jta = new JTextArea(info, 15, 40);
	    jta.setEditable(false);
	    jta.setLineWrap(true);
	    jta.setWrapStyleWord(true);
	    jta.setCaretPosition(0);
	    JScrollPane jasp = new JScrollPane(jta);
	    JOptionPane.showMessageDialog(null, jasp,
					  "Device Information",
					  JOptionPane.INFORMATION_MESSAGE);
	}
    }

    void CancelPressed() {
        this.setVisible(false);
    }
}
