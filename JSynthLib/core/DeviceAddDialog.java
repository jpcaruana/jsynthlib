/* $Id$ */

package core;

import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;
import javax.swing.JDialog;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.metal.*;
import java.beans.*;
import java.util.*;
import java.awt.event.*;
import java.awt.*;
import java.io.*;

public class DeviceAddDialog extends JDialog {

    JList AvailableDeviceList;
    DevicesConfig devConf = null;

    public DeviceAddDialog(JFrame Parent) {
        super(Parent,"Synthesizer Device Install",true);
        JPanel container= new JPanel();
        container.setLayout(new BorderLayout());

	// Read in list of available devices
	this.devConf = new DevicesConfig();
        AvailableDeviceList = new JList(this.devConf.deviceNames());
        AvailableDeviceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollpane = new JScrollPane(AvailableDeviceList);
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

        centerDialog();
    }

    protected void centerDialog() {
        Dimension screenSize = this.getToolkit().getScreenSize();
        Dimension size = this.getSize();
        screenSize.height = screenSize.height/2;
        screenSize.width = screenSize.width/2;
        size.height = size.height/2;
        size.width = size.width/2;
        int y = screenSize.height - size.height;
        int x = screenSize.width - size.width;
        this.setLocation(x,y);
    }

    void OKPressed() {
        this.setVisible(false);
        String s = (String) AvailableDeviceList.getSelectedValue();
	Device device = this.devConf.classForDevice(s);
	if (s != null) {
	    PatchEdit.appConfig.addDevice(device);
	    String info = device.getInfoText();
	    if (info != null && info.length() > 0) {
  		JOptionPane.showMessageDialog(null, fold(info, 72),
					      "Device Information",
  					      JOptionPane.INFORMATION_MESSAGE);
		/*
		JOptionPane pane = new JOptionPane(info,
						   JOptionPane.INFORMATION_MESSAGE);
		JDialog dialog = pane.createDialog(this, "Device Information");
		dialog.setSize(480, 400);
		dialog.show();
		*/
	    }
	}
    }

    private static String fold(String s, int limit) {
	String[] lines = s.split("\n");
	StringBuffer ret = new StringBuffer();
	for (int i = 0; i < lines.length; i++) {
	    StringBuffer buf = new StringBuffer(lines[i]);
	    int len = buf.length();
	    int index = 0;
	    for (int j = limit; j < len; j += limit + 1, len++) {
		buf.insert(j, "\n");
	    }
	    ret.append(buf);
	    if (i < lines.length - 1)
		ret.append("\n");
	}
	return ret.toString();
    }

    void CancelPressed() {
        this.setVisible(false);
    }
}
