package core;
import jmidi.MidiPort;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.JLabel;
import javax.swing.table.TableColumn;

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
public class NewPatchDialog extends JDialog {
    
    public NewPatchDialog(JFrame Parent) {
        super(Parent,"Create New Patch",true);
        Device dev;
        
        JPanel container=new JPanel();
        container.setLayout(new BorderLayout());
        
        JLabel myLabel=new JLabel("Please select a Patch Type to Create.",JLabel.CENTER);
        try {
            container.add(myLabel, BorderLayout.NORTH);
            final JComboBox cb =new JComboBox();
            for (int i=0;i<PatchEdit.deviceList.size();i++) {
                dev=(Device)PatchEdit.deviceList.get(i);
                for (int j=0;j<dev.driverList.size();j++) {
                    if (!(Converter.class.isInstance(dev.driverList.get(j)))){
                        
                        try {
                            // If the actual driver doesn't override the method "createNewPatch"
                            // this command will throw an exception. 
                            // This means, that the driver doesn't support the creation of a new patch
                            PatchEdit.getDriver(i,j).getClass().getDeclaredMethod("createNewPatch",null);
                            cb.addItem(dev.getManufacturerName()+" "+dev.getModelName()+" "+((Driver)(dev.driverList.get(j))).patchType);
                        }
                        catch (Exception ex) {
                            // Simply do nothing....
                            //System.out.println(ex.toString());
                        }
                        
                    }
                }
            }
/*
 for (int i=1;i<PatchEdit.DriverList.size();i++) cb.addItem(
          ((Driver)PatchEdit.DriverList.get(i)).manufacturer+" "+((Driver)PatchEdit.DriverList.get(i)).model+" "+
          ((Driver)PatchEdit.DriverList.get(i)).patchType);
 */
            container.add(cb,BorderLayout.CENTER);
            JPanel buttonPanel=new JPanel();
            JButton done = new JButton(" OK ");
            done.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int index=0;
                    for (int i=0;i<PatchEdit.deviceList.size();i++) {
                        final Device dev=(Device)PatchEdit.deviceList.get(i);
                        for (int j=0;j<dev.driverList.size();j++) {
                            System.out.println("checking "+PatchEdit.getDriver(i,j).toString()+" "+cb.getSelectedIndex()+" "+index);
                            if ((cb.getSelectedIndex()==index)&&(!(Converter.class.isInstance(dev.driverList.get(j))))) {
                                
                                try {
                                    // Keep in mind, that there are drivers around, which can't create new patches.....
                                    dev.driverList.get(j).getClass().getDeclaredMethod("createNewPatch",null);
                                    System.out.println("Bingo "+index);
                                 System.out.println(i+ "&" + j);
                                    PatchEdit.Clipboard=PatchEdit.getDriver(i,j).createNewPatch();
                                    setVisible(false);
                                    i=PatchEdit.deviceList.size();
                                    break;
                                }
                                catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                                
                            }
                            try {
                                dev.driverList.get(j).getClass().getDeclaredMethod("createNewPatch",null);
                                if (!(Converter.class.isInstance(dev.driverList.get(j))))
                                    index++;
                            }
                            catch (Exception ex) {
                                //System.out.println(ex.toString());
                            }
                            
                        }
                    }
                }
            }
            );
            buttonPanel.add( done );
            
            JButton cancel = new JButton("Cancel");
            cancel.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    setVisible(false);
                }
            });
            buttonPanel.add( cancel );
            
            getRootPane().setDefaultButton(done);
            
            container.add(buttonPanel, BorderLayout.SOUTH);
            getContentPane().add(container);
            pack();
            centerDialog();
        }
        catch(Exception e)
        {ErrorMsg.reportStatus(e);}
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
}
