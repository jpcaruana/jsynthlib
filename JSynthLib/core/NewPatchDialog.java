/*
 * NewPatchDialog.java
 *$Id$
 */
package core;
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
    
/**
 *
 * NewPatchDialog.java - Dialog to create a new Patch of the loaded Devices resp. Drivers
 * Any 'Generic' device and 'Converter' driver are skipped.
 * 
 * @author  unascribed
 * @author  Torsten Tittmann
 * @version v 1.1, 2003-02-09 
 */
public class NewPatchDialog extends JDialog
{
  private Device device;
  private Driver driver;
  private JComboBox deviceComboBox;
  private JComboBox driverComboBox;
  private ArrayList deviceAssignmentList = new ArrayList();
    
  public NewPatchDialog(JFrame Parent)
  {
        super(Parent,"Create New Patch",true);
        
    JPanel container=new JPanel(new BorderLayout(5,5));
        
        JLabel myLabel=new JLabel("Please select a Patch Type to Create.",JLabel.CENTER);
            container.add(myLabel, BorderLayout.NORTH);
                        
    deviceComboBox =new JComboBox();
    deviceComboBox.addActionListener(new DeviceActionListener());
    deviceComboBox.setRenderer(new DeviceCellRenderer());

    driverComboBox =new JComboBox();
    driverComboBox.setRenderer(new DriverCellRenderer());

    //----- First Populate the Device/Driver List with Device/Driver. which supports the "createNewPatch" method
    try 
    {
      int index=0;	// maybe the enumeration of the devices differs from JS internal deviceList

      for (int i=0;i<PatchEdit.appConfig.deviceCount();i++)
      {
        device=(Device)PatchEdit.appConfig.getDevice(i);

        // Skipping the generic device
        if ( (device.getManufacturerName().compareTo("Generic"))!=0 )
        {
          deviceAssignmentList.add(new deviceAssignment( i, device) );        // the original deviceNum/device

          for (int j=0;j<device.driverList.size();j++)
          {
            // Skipping a converter
            if (!(Converter.class.isInstance(driver=(Driver)device.driverList.get(j))))
            {
              try
              {
                            // If the actual driver doesn't override the method "createNewPatch"
                            // this command will throw an exception. 
                            // This means, that the driver doesn't support the creation of a new patch
                            PatchEdit.getDriver(i,j).getClass().getDeclaredMethod("createNewPatch",null);
                ((deviceAssignment)deviceAssignmentList.get(index)).add(j, driver);     // the original driverNum/driver
                        }
              catch (Exception ex)
	      {
                            // Simply do nothing....
                // System.out.println(ex.toString());
                        }
                    }
                }
          index++;	// to next non-'Generic' device
            }
                                }
                                
      //----- Populate the combo boxes with the entries of the deviceAssignmentList
      for (int i=0; i<deviceAssignmentList.size();i++)
      {
        deviceComboBox.addItem( deviceAssignmentList.get(i) );
                            }
                            
      //----- Layout the labels in a panel.
      JPanel labelPanel = new JPanel(new GridLayout(0, 1, 5, 5));
      labelPanel.add(new JLabel("Device:", JLabel.LEFT));
      labelPanel.add(new JLabel("Driver:", JLabel.LEFT));

      //----- Layout the fields in a panel
      JPanel fieldPanel = new JPanel(new GridLayout(0, 1));
      fieldPanel.add(deviceComboBox);
      fieldPanel.add(driverComboBox);

      //----- Create the comboPanel, labels on left, fields on right
      JPanel comboPanel = new JPanel(new BorderLayout());
      comboPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
      comboPanel.add(labelPanel, BorderLayout.CENTER);
      comboPanel.add(fieldPanel, BorderLayout.EAST);
      container.add(comboPanel, BorderLayout.CENTER);

      //----- Create "Create" button
      JPanel buttonPanel=new JPanel();
      JButton create = new JButton(" Create ");
      create.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
	  device = (Device) ((deviceAssignment)deviceComboBox.getSelectedItem()).device;
	  driver = (Driver) ((driverAssignment)driverComboBox.getSelectedItem()).driver;

	  System.out.println("Bingo "+driver.toString());
	  //System.out.println(deviceComboBox.getSelectedIndex()+" & "+ driverComboBox.getSelectedIndex());
          PatchEdit.Clipboard=driver.createNewPatch();
	  PatchEdit.Clipboard.deviceNum = (int) ((deviceAssignment)deviceComboBox.getSelectedItem()).deviceNum;
	  PatchEdit.Clipboard.driverNum = (int) ((driverAssignment)driverComboBox.getSelectedItem()).driverNum;
 	  System.out.println("deviceNum="+PatchEdit.Clipboard.deviceNum+" & driverNum="+PatchEdit.Clipboard.driverNum);

          setVisible(false);
	  dispose();
                        }
      });
      buttonPanel.add( create );
            
      //----- Create "Cancel" button
            JButton cancel = new JButton("Cancel");
      cancel.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
                    setVisible(false);
          dispose();
                }
            });
            buttonPanel.add( cancel );
            
      getRootPane().setDefaultButton(create);
            
            container.add(buttonPanel, BorderLayout.SOUTH);
            getContentPane().add(container);
            pack();
            centerDialog();
        }
        catch(Exception e)
    {
      ErrorMsg.reportStatus(e);
    }
  }


 /**
  *
  */
  protected void centerDialog()
  {
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


 /**
  * Repopulate the Driver ComboBox with valid drivers after a Device change 
  */
  public class DeviceActionListener implements ActionListener
  {
    public void actionPerformed (ActionEvent evt)
    {
      deviceAssignment myDevAssign = (deviceAssignment)deviceComboBox.getSelectedItem();

      driverComboBox.removeAllItems();

      if (myDevAssign != null)
      {
        for (int j=0;j<myDevAssign.driverAssignmentList.size ();j++)
        {
          driverComboBox.addItem( (driverAssignment)myDevAssign.driverAssignmentList.get(j) );
        }
      }
      driverComboBox.setEnabled(driverComboBox.getItemCount() > 1);
    }
  }


 /**
  * New standard renderer for ComboBoxes
  */
  class ComboCellRenderer extends JLabel implements ListCellRenderer
  {
    public ComboCellRenderer()
    {
      setOpaque(true);
    }

    public Component getListCellRendererComponent (
        JList list,
        Object value,
        int index,
        boolean isSelected,
        boolean cellHasFocus)
    {
      setText(value == null ? "" : value.toString());
      setBackground(isSelected ? Color.red : Color.white);
      setForeground(isSelected ? Color.white : Color.black);
      return this;
  }
  }


 /**
  * Special renderer for Device ComboBox to display the valid DeviceName
  */
  class DeviceCellRenderer extends ComboCellRenderer
  {
    public Component getListCellRendererComponent (
        JList list,
        Object value,
        int index,
        boolean isSelected,
        boolean cellHasFocus)
    {
      super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
      setText(value == null ? "" : ((deviceAssignment)value).device.getDeviceName());
      return this;
    }
  }


 /**
  * Special renderer for Driver ComboBox to display the valid PatchType
  */
  class DriverCellRenderer extends ComboCellRenderer
  {
    public Component getListCellRendererComponent (
        JList list,
        Object value,
        int index,
        boolean isSelected,
        boolean cellHasFocus)
    {
      super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
      setText(value == null ? "" : ((driverAssignment)value).driver.getPatchType());
      return this;
    }
  }


 /**
  * We need to remember the original deviceNum variable of the Device and the Device itself.
  * Each deviceAssignment Object contains a List of driverAssignments which remembers
  * the original driverNum resp. driver. Maybe more than on driver supports the patch.
  *
  * @see driverAssignment
  */
  class deviceAssignment
  {
    protected int       deviceNum;
    protected Device    device;
    protected ArrayList driverAssignmentList = new ArrayList();

    deviceAssignment(int deviceNum, Device device)
    {
      this.deviceNum = deviceNum;
      this.device    = device;
    }

    void add(int driverNum, Driver driver)
    {
      this.driverAssignmentList.add(new driverAssignment(driverNum, driver));
    }
  }


 /**
  * We need to remember the original driverNum variable of the Driver.
  *
  * @see deviceAssignment
  */
  class driverAssignment
  {
    protected int    driverNum;
    protected Driver driver;

    driverAssignment(int driverNum, Driver driver)
    {
      this.driverNum = driverNum;
      this.driver    = driver;
    }
  }

}
