package synthdrivers.AccessVirus;

import core.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Properties;
import java.util.Arrays;
import java.util.Set;
import java.io.*;

/** Device class for the Access Virus
 * @version $Id$
 * @author Kenneth L. Martinez
 */
public class AccessVirusDevice extends Device implements ItemListener {
    static final String DRIVER_INFO =
    "Both the Virus desktop and Virus b can be used with this driver; note\n"
    + "that the Virus desktop doesn't have program banks E thru H.\n\n"
    + "The edit buffer for the global midi channel will be used to send and\n"
    + "play single programs and multis.  The midi device Id must match that\n"
    + "of the Virus to allow sysex send and receive to work.  Set the device\n"
    + "Id on the Configuration tab of Show Details.";
    String channels[] = { "1", "2", "3", "4", "5", "6", "7", "8",
    "9", "10", "11", "12", "13", "14", "15", "16", "Omni"
    };
    
    JComboBox channelList;
    
    /** Holds value of property deviceId. */
    private int deviceId;
    
    /** Creates new AccessVirus */
    public AccessVirusDevice() {
	super ("Access","Virus",null,DRIVER_INFO,"Kenneth L. Martinez");
        deviceId = 17;
        //avConfig = new AccessVirusConfig();
        Driver drv;
        JOptionPane.showMessageDialog(PatchEdit.getInstance(),
        DRIVER_INFO, "Access Virus Driver Release Notes",
        JOptionPane.WARNING_MESSAGE
        );
        
        addDriver(new VirusProgBankDriver());
        addDriver(new VirusProgSingleDriver());
        addDriver(new VirusMultiBankDriver());
        addDriver(new VirusMultiSingleDriver());
        
    }
    
    public JPanel config() {
        JPanel panel= new JPanel();
        
        panel.add(new JLabel("Select Virus MIDI Device Id"));
        channelList = new JComboBox(channels);
        channelList.setMaximumSize(new Dimension(150, 25));
        channelList.setSelectedIndex(deviceId - 1);
        channelList.addItemListener(this);
        panel.add(channelList);
        return panel;
    }
    
    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() != ItemEvent.SELECTED) {
            return;
        }
        if (e.getItemSelectable() == channelList) {
            //    avConfig.setDeviceId(channelList.getSelectedIndex() + 1);
            setDeviceId(channelList.getSelectedIndex()+1);
            
        }
    }
    
    /** Getter for property deviceId.
     * @return Value of property deviceId.
     *
     */
    public int getDeviceId() {
        return this.deviceId;
    }
    
    /** Setter for property deviceId.
     * @param deviceId New value of property deviceId.
     *
     */
    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }
    
    
    /**
     * Get the names of properties that should be stored and loaded.
     * @return a Set of field names
     */
    public Set storedProperties() {
        final String[] storedPropertyNames = {
            "deviceId"
        };
        Set set = super.storedProperties();
        set.addAll(Arrays.asList(storedPropertyNames));
        return set;
    }
    
    /**
     * Method that will be called after loading
     */
    public void afterRestore() {
    }
}
