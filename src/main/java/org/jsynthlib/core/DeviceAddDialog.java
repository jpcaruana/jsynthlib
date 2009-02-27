/* $Id$ */

package org.jsynthlib.core;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.tree.TreePath;
import javax.swing.tree.DefaultMutableTreeNode;

public class DeviceAddDialog extends JDialog {

    DeviceSelectionTree AvailableDeviceList;
    //DevicesConfig devConf = null;

    public DeviceAddDialog(JFrame Parent) {
        super(Parent,"Synthesizer Device Install",true);
        JPanel container= new JPanel();
        container.setLayout(new BorderLayout());

        AvailableDeviceList = new DeviceSelectionTree();
        JScrollPane scrollpane = new JScrollPane(AvailableDeviceList);
        container.add(scrollpane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout( new FlowLayout(FlowLayout.CENTER) );

        // The following code catches double-clicks on leafs and treats them like pressing "OK"
        MouseListener ml = new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                int selRow = AvailableDeviceList.getRowForLocation(e.getX(), e.getY());
                TreePath tp = AvailableDeviceList.getPathForRow(selRow);
                // Did they even click on a tree item
                if(tp != null) {
                    if(e.getClickCount() == 2) {
                        // User double-clicked. What did they click on?
                        DefaultMutableTreeNode o = (DefaultMutableTreeNode) tp.getLastPathComponent();
                        if(o.isLeaf()) {
                            // User double-clicked on a leaf. Treat it like "OK"
                            OKPressed();
                        }
                    }
                }
            }
        };
        AvailableDeviceList.addMouseListener(ml);

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
        String s = (String) AvailableDeviceList.getSelectedValue();
        if (s == null)
            return;

        String cls = PatchEdit.devConfig.getClassNameForDeviceName(s);
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
