/* $Id$ */

package core;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;

public class DeviceAddDialog extends JDialog {

    JList AvailableDeviceList;
    //DevicesConfig devConf = null;

    public DeviceAddDialog(JFrame Parent) {
        super(Parent,"Synthesizer Device Install",true);
        JPanel container= new JPanel();
        container.setLayout(new BorderLayout());

	// Read in list of available devices
	//this.devConf = new DevicesConfig();
        AvailableDeviceList = new JList(PatchEdit.devConfig.deviceNames());
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

        Utility.centerDialog(this);
    }

    void OKPressed() {
        this.setVisible(false);
        String s = (String) AvailableDeviceList.getSelectedValue();
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
