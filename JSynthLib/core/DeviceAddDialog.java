/* $Id$ */

package core;

import synthdrivers.KawaiK4.*;
import synthdrivers.KawaiK5000.*;
import synthdrivers.EmuProteusMPS.*;
import synthdrivers.OberheimMatrix.*;
import synthdrivers.BossDR660.*;
import synthdrivers.YamahaTG33.*;
import synthdrivers.YamahaTX81z.*;
import synthdrivers.EnsoniqESQ1.*;
import synthdrivers.YamahaDX100.*;
import synthdrivers.KorgER1.*;
import synthdrivers.NovationNova1.*;
import synthdrivers.CasioCZ1000.*;
import synthdrivers.RolandXV5080.*;       // phil@muqus.com
import synthdrivers.PeaveyPC1600.*;       // phil@muqus.com
import synthdrivers.YamahaDX7.*;
import synthdrivers.KorgWavestation.*;
import synthdrivers.AlesisQS.*;
import synthdrivers.RolandMKS50.*;
import synthdrivers.EnsoniqVFX.*;
import synthdrivers.SCIProphet600.*;        // KLM
import synthdrivers.NordLead.*;        // KLM
import synthdrivers.AccessVirus.*;        // KLM
import synthdrivers.AlesisA6.*;
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
    
    String [] availibleDevices = {
	"Access Virus Driver",
	"Alesis A6 Andromeda Driver",
        "Alesis QS Series Driver",
        "Boss DR660 Driver",
        "Casio CZ-1000 Driver",
        "Emu Proteus MPS Driver",
        "Ensoniq ESQ1/ESQm",
        "Ensoniq VFX",
        "Kawai K4/K4r Driver",
        "Kawai K5000 Driver",
        "Korg ER1 Driver",
        "Korg Wavestation Driver",
	"Nord Lead Driver",
        "Novation Nova 1 Driver",
        "Oberheim Matrix 1000 Driver",
        "Oberheim Matrix 6/6R Driver",
        "Peavey PC1600 Driver", // phil@muqus.com
        "Roland MKS-50 Driver", //KLM
        "Roland XV5080 Driver", // phil@muqus.com
	"Sequential Prophet-600 Driver",
        "Yamaha DX21 / DX27 / DX100 Driver",
        "Yamaha DX7 / TX7 Driver",
        "Yamaha TG33 Driver",
        "Yamaha TX81z Driver",
    };
    
    JList AvailibleDeviceList;
    
    public DeviceAddDialog(JFrame Parent) {
        super(Parent,"Synthesizer Device Install",true);
        JPanel container= new JPanel();
        container.setLayout(new BorderLayout());
        
        AvailibleDeviceList = new JList(availibleDevices);
        AvailibleDeviceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollpane = new JScrollPane(AvailibleDeviceList);
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
        String s=(String)AvailibleDeviceList.getSelectedValue();
        if (s.equals("Yamaha TG33 Driver")) {
            PatchEdit.deviceList.add(new YamahaTG33Device());
            return;
        }
        if (s.equals("Yamaha TX81z Driver")) {
            PatchEdit.deviceList.add(new YamahaTX81zDevice());
            return;
        }
        if (s.equals("Emu Proteus MPS Driver")) {
            PatchEdit.deviceList.add(new EmuProteusDevice());
            return;
        }
        if (s.equals("Boss DR660 Driver")) {
            PatchEdit.deviceList.add(new BossDR660Device());
            return;
        }
        if (s.equals("Kawai K4/K4r Driver")) {
            PatchEdit.deviceList.add(new K4Device());
            return;
        }
        if (s.equals("Oberheim Matrix 1000 Driver")) {
            PatchEdit.deviceList.add(new OberheimMatrix1000Device());
            return;
        }
        if (s.equals("Oberheim Matrix 6/6R Driver")) {
            PatchEdit.deviceList.add(new OberheimMatrix6Device());
            return;
        }
        if (s.equals("Kawai K5000 Driver")) {
            PatchEdit.deviceList.add(new KawaiK5000Device());
            return;
        }
        if (s.equals("Ensoniq ESQ1/ESQm")) {
            PatchEdit.deviceList.add(new EnsoniqESQ1Device());
            return;
        }
	if (s.equals("Ensoniq VFX")) {
            PatchEdit.deviceList.add(new EnsoniqVFXDevice());
            return;
        }
        if (s.equals("Yamaha DX21 / DX27 / DX100 Driver")) {
            PatchEdit.deviceList.add(new YamahaDX100Device());
            return;
        }
        if (s.equals("Yamaha DX7 / TX7 Driver")) {
            PatchEdit.deviceList.add(new YamahaDX7Device());
            return;
        }
        if (s.equals("Korg ER1 Driver")) {
            PatchEdit.deviceList.add(new KorgER1Device());
            return;
        }
        if (s.equals("Novation Nova 1 Driver")) {
            PatchEdit.deviceList.add(new NovationNova1Device());
            return;
        }
        if (s.equals("Peavey PC1600 Driver")) {
            PatchEdit.deviceList.add(new PeaveyPC1600Device());
            return;
        }
        if (s.equals("Roland XV5080 Driver")) {
            PatchEdit.deviceList.add(new RolandXV5080Device());
            return;
        }
        if (s.equals("Casio CZ-1000 Driver")) {
            PatchEdit.deviceList.add(new CasioCZ1000Device());
            return;
        }
        if (s.equals("Korg Wavestation Driver")) {
            PatchEdit.deviceList.add(new KorgWavestationDevice());
            return;
        }
        if (s.equals("Alesis QS Series Driver")) {
            PatchEdit.deviceList.add(new AlesisQSDevice());
            return;
        }
        if (s.equals("Roland MKS-50 Driver")) {
            PatchEdit.deviceList.add(new RolandMKS50Device());
            return;
        }
	if (s.equals("Alesis A6 Andromeda Driver"))                  // KLM
	    {PatchEdit.deviceList.add(new AlesisA6Device());
	    return;}
	if (s.equals("Sequential Prophet-600 Driver"))                  // KLM
	    {PatchEdit.deviceList.add(new SCIProphet600Device());
	    return;}
	if (s.equals("Nord Lead Driver"))                  // KLM
	    {PatchEdit.deviceList.add(new NordLeadDevice());
	    return;}
	if (s.equals("Access Virus Driver"))                  // KLM
	    {PatchEdit.deviceList.add(new AccessVirusDevice());
	    return;}


    }
    
    void CancelPressed() {
        this.setVisible(false);
    }
    
}
