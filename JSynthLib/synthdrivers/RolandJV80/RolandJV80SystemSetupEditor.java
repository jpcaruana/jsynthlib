/*
 * Copyright 2004 Sander Brandenburg
 *
 * This file is part of JSynthLib.
 *
 * JSynthLib is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or(at your option) any later version.
 *
 * JSynthLib is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JSynthLib; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 */
package synthdrivers.RolandJV80;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.text.DecimalFormat;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import core.CheckBoxWidget;
import core.ComboBoxWidget;
import core.DriverUtil;
import core.ParamModel;
import core.Patch;
import core.PatchEditorFrame;
import core.ScrollBarWidget;
import core.SysexSender;

/**
 * @author Sander Brandenburg
 * @version $Id$
 */
public class RolandJV80SystemSetupEditor extends PatchEditorFrame {

    private final boolean isJV80;
    
    RolandJV80SystemSetupEditor(Patch p) {
        super("Roland JV80 System editor", p);

        isJV80 = ((RolandJV80SystemSetupDriver) p.getDriver()).isJV80;
        
        buildEditor(p);
    }
    
    void buildEditor(Patch patch) {
 		JTabbedPane systemPane=new JTabbedPane();
		
		systemPane.addTab("System Setup", 
		        buildSystemSetup(patch));
		systemPane.addTab("MIDI Receive Switches", 
		        buildReceiveSwitches(patch));
		if (isJV80) {
		    systemPane.addTab("MIDI Transmit Switches", 
		        buildTransmitSwitches(patch));
		}

		scrollPane.add(systemPane, gbc);
		pack();
		show();
    }
    
    JPanel buildReceiveSwitches(Patch patch) {
        GridBagConstraints gbc = new GridBagConstraints();
        JPanel panel = new JPanel(new GridBagLayout());
        
        gbc.gridx = 0; gbc.gridy = 0; 
        
        gbc.gridy++; panel.add(new JLabel("Volume"), gbc);
        gbc.gridy++; panel.add(new JLabel("Control Change"), gbc);
        gbc.gridy++; panel.add(new JLabel("Channel Presure"), gbc);
        gbc.gridy++; panel.add(new JLabel("Modulation"), gbc);
        gbc.gridy++; panel.add(new JLabel("Pitch Bend"), gbc);
        gbc.gridy++; panel.add(new JLabel("Program Change"), gbc);
        gbc.gridy++; panel.add(new JLabel("Bank Select"), gbc);
        

        gbc.gridx = 1; gbc.gridy = 0;
        gbc.gridy++; panel.add(new CheckBoxWidget(null, patch,
                new JVModel(patch, 0x10),
                new JVSender(0x10)), gbc);
        gbc.gridy++; panel.add(new CheckBoxWidget(null, patch,
                new JVModel(patch, 0x11),
                new JVSender(0x11)), gbc);
        gbc.gridy++; panel.add(new CheckBoxWidget(null, patch,
                new JVModel(patch, 0x12),
                new JVSender(0x12)), gbc);
        gbc.gridy++; panel.add(new CheckBoxWidget(null, patch,
                new JVModel(patch, 0x13),
                new JVSender(0x13)), gbc);
        gbc.gridy++; panel.add(new CheckBoxWidget(null, patch,
                new JVModel(patch, 0x14),
                new JVSender(0x14)), gbc);
        gbc.gridy++; panel.add(new CheckBoxWidget(null, patch,
                new JVModel(patch, 0x15),
                new JVSender(0x15)), gbc);
        gbc.gridy++; panel.add(new CheckBoxWidget(null, patch,
                new JVModel(patch, 0x16),
                new JVSender(0x16)), gbc);
        
        return panel;
    }
    
    JPanel buildTransmitSwitches(Patch patch) {
        GridBagConstraints gbc = new GridBagConstraints();
        JPanel panel = new JPanel(new GridBagLayout());
        
        gbc.gridx = 0; gbc.gridy = 0;
        
        gbc.gridy++; panel.add(new JLabel("Volume"), gbc);
        gbc.gridy++; panel.add(new JLabel("Control Change"), gbc);
        gbc.gridy++; panel.add(new JLabel("Channel Presure"), gbc);
        gbc.gridy++; panel.add(new JLabel("Modulation"), gbc);
        gbc.gridy++; panel.add(new JLabel("Pitch Bend"), gbc);
        gbc.gridy++; panel.add(new JLabel("Program Change"), gbc);
        gbc.gridy++; panel.add(new JLabel("Bank Select"), gbc);
        

        gbc.gridx = 1; gbc.gridy = 0;
        gbc.gridy++; panel.add(new CheckBoxWidget(null, patch,
                new JVModel(patch, 0x17),
                new JVSender(0x17)), gbc);
        gbc.gridy++; panel.add(new CheckBoxWidget(null, patch,
                new JVModel(patch, 0x18),
                new JVSender(0x18)), gbc);
        gbc.gridy++; panel.add(new CheckBoxWidget(null, patch,
                new JVModel(patch, 0x19),
                new JVSender(0x19)), gbc);
        gbc.gridy++; panel.add(new CheckBoxWidget(null, patch,
                new JVModel(patch, 0x1A),
                new JVSender(0x1A)), gbc);
        gbc.gridy++; panel.add(new CheckBoxWidget(null, patch,
                new JVModel(patch, 0x1B),
                new JVSender(0x1B)), gbc);
        gbc.gridy++; panel.add(new CheckBoxWidget(null, patch,
                new JVModel(patch, 0x1C),
                new JVSender(0x1C)), gbc);
        gbc.gridy++; panel.add(new CheckBoxWidget(null, patch,
                new JVModel(patch, 0x1D),
                new JVSender(0x1D)), gbc);
    	
        return panel;
    }
    
    JPanel buildSystemSetup(Patch patch) {
        GridBagConstraints gbc = new GridBagConstraints();
        JPanel panel = new JPanel(new GridBagLayout());
        
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridy++; panel.add(new JLabel("Panel Mode"), gbc);
        gbc.gridheight = 2;
        gbc.gridy++; panel.add(new JLabel("Master Tune"), gbc);
        gbc.gridheight = 1; gbc.gridy++;
        if (isJV80) {
	        gbc.gridy++; panel.add(new JLabel("Key Transpose"), gbc);
	        gbc.gridy++; panel.add(new JLabel("Transpose Switch"), gbc);
        }
        gbc.gridy++; panel.add(new JLabel("Output Mode"), gbc);
        gbc.gridy++; panel.add(new JLabel("Reverb"), gbc);
        gbc.gridy++; panel.add(new JLabel("Chorus"), gbc);
        gbc.gridy++; panel.add(new JLabel("Patch Receive Channel"), gbc);
        if (isJV80) {
            gbc.gridy++; panel.add(new JLabel("Patch Transmit Channel"), gbc);
        }
        
        gbc.gridy++; panel.add(new JLabel("Control Channel"), gbc);
        
        if (isJV80) {
	        gbc.gridy++; panel.add(new JLabel("C1 Mode"), gbc);
	        gbc.gridy++; panel.add(new JLabel("C1 Assign"), gbc);
	        gbc.gridy++; panel.add(new JLabel("Aftertouch Treshold"), gbc);
	        gbc.gridy++; panel.add(new JLabel("Pedal 1 Polarity"), gbc);
	        gbc.gridy++; panel.add(new JLabel("Pedal 1 Mode"), gbc);
	        gbc.gridy++; panel.add(new JLabel("Pedal 1 Assign"), gbc);
	        gbc.gridy++; panel.add(new JLabel("Pedal 2 Polarity"), gbc);
	        gbc.gridy++; panel.add(new JLabel("Pedal 2 Mode"), gbc);
	        gbc.gridy++; panel.add(new JLabel("Pedal 2 Assign"), gbc);
        }

        gbc.gridx = 1; gbc.gridy = 0;
		gbc.gridy++; panel.add(new ComboBoxWidget(null, patch,
		        new JVModel(patch, 0),
		        new JVSender(0),
		        new String[] {"PERFORMANCE", "PATCH"}), gbc);
		
		final JLabel tuneLabel = new JLabel();
		final ScrollBarWidget masterTune = new ScrollBarWidget(null, patch, 
		        1, 127, -64, new JVModel(patch, 1), new JVSender(1) );
		masterTune.addEventListener(new ChangeListener() {
			final DecimalFormat freqFormatter = new DecimalFormat("0.0Hz");
			{ stateChanged(null); }
		    public void stateChanged(ChangeEvent e) {
		        double pitch = (4272 + 2 * masterTune.getValue())/10.0d;
		        tuneLabel.setText(freqFormatter.format(pitch));
		    }});
		gbc.gridy++; panel.add(masterTune, gbc);
		gbc.gridy++; panel.add(tuneLabel, gbc);
		
		if (isJV80) {
			gbc.gridy++; panel.add(new ScrollBarWidget(null, patch, 
			        28, 100, -64, new JVModel(patch, 2), new JVSender(2)), gbc);
			gbc.gridy++; panel.add(new CheckBoxWidget(null, patch,
					new JVModel(patch, 3),
					new JVSender(3)), gbc);
		}
		
		gbc.gridy++; panel.add(new ComboBoxWidget(null, patch,
		        new JVModel(patch, 0x21),
		        new JVSender(0x21),
		        new String[] {"OUT2", "OUT4"}), gbc);
		gbc.gridy++; panel.add(new CheckBoxWidget(null, patch,
				new JVModel(patch, 4),
				new JVSender(4)), gbc);
		gbc.gridy++; panel.add(new CheckBoxWidget(null, patch,
                new JVModel(patch, 5),
                new JVSender(5)), gbc);
		gbc.gridy++; panel.add(new ComboBoxWidget(null, patch,
		        new JVModel(patch, 0x1E),
		        new JVSender(0x1E),
		        DriverUtil.generateNumbers(1, 16, "Channel 00")), gbc);
		
		String[] txChannels = DriverUtil.generateNumbers(1, 18, "Channel 00");
		txChannels[16] = "RX channel"; txChannels[17] = "Off";
		
		String[] controlMode = new String[] { "Off", "Internal", "MIDI", "Internal + MIDI" };
		
		String[] controlPolarity = new String[] { "Standard", "Reversed" };
		String controlAssigns[] = DriverUtil.generateNumbers(0, 100, "Control Change 00");
		controlAssigns[96] = "Channel Aftertouch"; controlAssigns[97] = "Bend Up";
		controlAssigns[98] = "Bend Down"; controlAssigns[99] = "Prog. Up"; controlAssigns[100] = "Prog. Down";
		
		final String[] controlChannels = DriverUtil.generateNumbers(1, 17, "Channel 00");
		controlChannels[16] = "OFF";
		
		if (isJV80) {
	    	gbc.gridy++; panel.add(new ComboBoxWidget(null, patch,
			        new JVModel(patch, 0x1F),
			        new JVSender(0x1F), txChannels), gbc);
		}
		gbc.gridy++; panel.add(new ComboBoxWidget(null, patch,
		        new JVModel(patch, 0x20),
		        new JVSender(0x20), controlChannels), gbc);
		if (isJV80) {
			gbc.gridy++; panel.add(new ComboBoxWidget(null, patch,
			        new JVModel(patch, 0x0D),
			        new JVSender(0x0D), controlMode), gbc);
			gbc.gridy++; panel.add(new ComboBoxWidget(null, patch,
			        new JVModel(patch, 0x0E),
			        new JVSender(0x0E), controlAssigns), gbc);
			gbc.gridy++; panel.add(new ScrollBarWidget(null, patch, 0, 127, 0, 
			        new JVModel(patch, 0x0F), 
			        new JVSender(0x0F)), gbc);
			gbc.gridy++; panel.add(new ComboBoxWidget(null, patch,
			        new JVModel(patch, 0x07),
			        new JVSender(0x07), controlPolarity), gbc);
			gbc.gridy++; panel.add(new ComboBoxWidget(null, patch,
			        new JVModel(patch, 0x08),
			        new JVSender(0x08), controlMode), gbc);
			gbc.gridy++; panel.add(new ComboBoxWidget(null, patch,
			        new JVModel(patch, 0x09),
			        new JVSender(0x09), controlAssigns), gbc);
			gbc.gridy++; panel.add(new ComboBoxWidget(null, patch,
			        new JVModel(patch, 0x0A),
			        new JVSender(0x0A), controlPolarity), gbc);
			gbc.gridy++; panel.add(new ComboBoxWidget(null, patch,
			        new JVModel(patch, 0x0B),
			        new JVSender(0x0B),
			        controlMode), gbc);
			gbc.gridy++; panel.add(new ComboBoxWidget(null, patch,
			        new JVModel(patch, 0x0C),
			        new JVSender(0x0C), controlAssigns), gbc);
		}

		return panel;
    }
    
 	static class JVSender extends SysexSender {
	    int offset;
	    // retrieve default from patch
		public JVSender(int offset) {
		    super("F041@@461200000000**00F7");
		    this.offset = offset;
		}
		
        protected byte[] generate(int value) {
            byte[] data = super.generate(value);
            data[JV80Constants.ADDR4_IDX] = (byte) offset;
            JV80Constants.calculateChecksum(data, 1);
            return data;
        }
	}
	class JVModel extends ParamModel {
	    final static int DATA_OFFSET = 9;
	    JVModel(Patch p, int offset) {
	        super(p, DATA_OFFSET + offset);
	    }
	}

}

