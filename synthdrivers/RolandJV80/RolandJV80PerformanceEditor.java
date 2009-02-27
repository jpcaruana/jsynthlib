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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jsynthlib.core.CheckBoxWidget;
import org.jsynthlib.core.ComboBoxWidget;
import org.jsynthlib.core.DriverUtil;
import org.jsynthlib.core.ParamModel;
import org.jsynthlib.core.Patch;
import org.jsynthlib.core.PatchEditorFrame;
import org.jsynthlib.core.PatchNameWidget;
import org.jsynthlib.core.ScrollBarWidget;
import org.jsynthlib.core.SysexSender;


/**
 * @author Sander Brandenburg
 * @version $Id$
 */
public class RolandJV80PerformanceEditor extends PatchEditorFrame {

    private final boolean isJV80;
    
    RolandJV80PerformanceEditor(Patch p) {
        super("Roland JV-880 Performance Editor", p);
        
        isJV80 = ((RolandJV80PerformanceDriver) p.getDriver()).isJV80;
        
        ((RolandJV80Device) p.getDevice()).getPerformanceDriver().setPerformanceNum(p.sysex, -1, 0);
        buildEditor(p);
    }
    
    void buildEditor(final Patch patch) {
        GridBagConstraints gbc = new GridBagConstraints();
        
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0; gbc.gridy = 0;
        scrollPane.add(buildCommon(patch), gbc);
        
        gbc.gridx = 1; gbc.gridy = 0;
        scrollPane.add(buildReserve(patch), gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        scrollPane.add(buildChorus(patch), gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        scrollPane.add(buildReverb(patch), gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        scrollPane.add(buildParts(patch), gbc);
        
        pack();
    }
    
    JPanel buildChorus(Patch patch) {
        GridBagConstraints gbc = new GridBagConstraints();
        JPanel panel = new JPanel(new GridBagLayout());
	    panel.setBorder(new TitledBorder(new EtchedBorder(
	                EtchedBorder.RAISED),"Chorus",
	                TitledBorder.CENTER,TitledBorder.CENTER));
	    
	    gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
	    gbc.gridy++; panel.add(new JLabel("Chorus Type"), gbc);
	    gbc.gridy++; panel.add(new JLabel("Chorus Level"), gbc);
	    gbc.gridy++; panel.add(new JLabel("Chorus Depth"), gbc);
	    gbc.gridy++; panel.add(new JLabel("Chorus Rate"), gbc);
	    gbc.gridy++; panel.add(new JLabel("Chorus Feedback"), gbc);
	    gbc.gridy++; panel.add(new JLabel("Chorus Output"), gbc);	
	    
	    gbc.gridx = 1; gbc.gridy = 0;
	    gbc.gridy++; panel.add(new ComboBoxWidget(null, patch,
		        new JVModel(patch, -1, 0x11),
		        new JVSender(-1, 0x11), 
		        new String[] {"CHORUS1", "CHORUS2", "CHORUS3" }), gbc);
	    gbc.gridy++; panel.add(new ScrollBarWidget(null, patch, 
                0, 127, 0, new JVModel(patch, -1, 0x12), 
                new JVSender(-1, 0x12)), gbc);
	    gbc.gridy++; panel.add(new ScrollBarWidget(null, patch, 
                0, 127, 0, new JVModel(patch, -1, 0x13), 
                new JVSender(-1, 0x13)), gbc);
	    gbc.gridy++; panel.add(new ScrollBarWidget(null, patch, 
                0, 127, 0, new JVModel(patch, -1, 0x14), 
                new JVSender(-1, 0x14)), gbc);
	    gbc.gridy++; panel.add(new ScrollBarWidget(null, patch, 
                0, 127, 0, new JVModel(patch, -1, 0x15), 
                new JVSender(-1, 0x15)),  gbc);
	    gbc.gridy++; panel.add(new ComboBoxWidget(null, patch,
		        new JVModel(patch, -1, 0x11),
		        new JVSender(-1, 0x11), 
		        new String[] {"MIX", "REV" }), gbc);
	    
	    return panel;
    }
    
    JPanel buildReverb(Patch patch) {
        GridBagConstraints gbc = new GridBagConstraints();
        
		JPanel panel = new JPanel(new GridBagLayout());
	    panel.setBorder(new TitledBorder(new EtchedBorder(
	                EtchedBorder.RAISED),"Reverb",
	                TitledBorder.CENTER,TitledBorder.CENTER));
	    
	    gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
	    gbc.gridy++; panel.add(new JLabel("Reverb Type"), gbc);
	    gbc.gridy++; panel.add(new JLabel("Reverb Level"), gbc);
	    gbc.gridy++; panel.add(new JLabel("Reverb Time"), gbc);
	    gbc.gridy++; panel.add(new JLabel("Delay Feedback"), gbc);
	    
	    gbc.gridx = 1; gbc.gridy = 0;
	    gbc.gridy++; panel.add(new ComboBoxWidget(null, patch,
		        new JVModel(patch, -1, 0x0D),
		        new JVSender(-1, 0x0D), 
		        new String[] {"ROOM1", "ROOM2", "STAGE1", "HALL1", 
	        "HALL2", "DELAY", "PAN-DLY" }), gbc);
	    gbc.gridy++; panel.add(new ScrollBarWidget(null, patch, 
                0, 127, 0, new JVModel(patch, -1, 0x0E), 
                new JVSender(-1, 0x0E)), gbc);
	    gbc.gridy++; panel.add(new ScrollBarWidget(null, patch, 
                0, 127, 0, new JVModel(patch, -1, 0x0F), 
                new JVSender(-1, 0x0F)), gbc);
	    gbc.gridy++; panel.add(new ScrollBarWidget(null, patch, 
                0, 127, 0, new JVModel(patch, -1, 0x10), 
                new JVSender(-1, 0x10)), gbc);
	    
        return panel;
    }
    
    JPanel buildCommon(Patch patch) {
        GridBagConstraints gbc = new GridBagConstraints();
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new TitledBorder(new EtchedBorder(
                EtchedBorder.RAISED),"Common",
                TitledBorder.CENTER,TitledBorder.CENTER));
        
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("Patch Name"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; panel.add(new PatchNameWidget(null, patch), gbc);

        if (isJV80) {
            gbc.gridx = 0; gbc.gridy = 1; panel.add(new JLabel("Key Mode"), gbc);
            
            gbc.gridx = 1; gbc.gridy = 1; 
            panel.add(new ComboBoxWidget(null, patch, new JVModel(patch, -1, 0x0C), 
	                new JVSender(-1, 0x0C), new String[] { "LAYER", "ZONE", "SINGLE" }), gbc);
        }
        return panel;
    }
    
    JPanel buildReserve(final Patch patch) {
        GridBagConstraints gbc = new GridBagConstraints();
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new TitledBorder(new EtchedBorder(
                EtchedBorder.RAISED),"Reserve",
                TitledBorder.CENTER,TitledBorder.CENTER));

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 8;
	    final JLabel poolLabel = new JLabel();
	    panel.add(poolLabel, gbc);
	    gbc.gridwidth = 1; gbc.gridy = 1;
	    
        final JSpinner spinners[] = new JSpinner[8];
	    for (int i = 0; i < 8; i++) {
	        spinners[i] = new JSpinner(new SpinnerNumberModel(0, 0, 28, 1));
	        
	        final int ctr = i;
	        spinners[i].addChangeListener(new ChangeListener() {
		        final JVModel jm = new JVModel(patch, -1, 0x17 + ctr);
		        final JVSender jsender = new JVSender(-1, 0x17 + ctr);

	            { 	spinners[ctr].setValue(new Integer(jm.get())); 
	                if (ctr == 7) 
	                    stateChanged(new ChangeEvent(spinners[ctr])); 
	            }
                public void stateChanged(ChangeEvent e) {
                    // if change caused the sum of all spinners to be over 28
                    // revert that change
                    int sum = 0;
                    for (int i = 0; i < spinners.length; i++) {
                        JSpinner js = spinners[i];
                        sum += ((Integer) js.getValue()).intValue();
                    }
                    JSpinner js = (JSpinner) e.getSource();
                    int value = ((Integer) js.getValue()).intValue();
                    if (sum > 28) {
                        //new change not accepted: sum over 28 voices
                        //reset old setting
                        sum -= value;
                        js.setValue(new Integer(jm.get()));
                    } else {
	                    jm.set(value);
	                    jsender.send(patch.getDriver(), value);
                    }
                    
                    poolLabel.setText("Left: " + Math.max((28 - sum), 0));
                }
	        });
	        
	        gbc.gridx = i; gbc.gridy = 2;
	        panel.add(spinners[i], gbc);
	        
	        gbc.gridy = 3;
	        panel.add(new JLabel("Part " + (i+1)), gbc);
	    }
	    
	    return panel;
    }
    
    
    JPanel buildParts(final Patch patch) {
        GridBagConstraints gbc = new GridBagConstraints();
        JPanel panel = new JPanel(new GridBagLayout());
	    panel.setBorder(new TitledBorder(new EtchedBorder(
                EtchedBorder.RAISED),"Performance Parts",
                TitledBorder.CENTER,TitledBorder.CENTER));
	    
	    gbc.weightx = 1; gbc.weighty = 1;
	    gbc.anchor = GridBagConstraints.CENTER;
	    gbc.fill = GridBagConstraints.BOTH;
	    
	    JTabbedPane jtp = new JTabbedPane();
	    panel.add(jtp, gbc);
	    //make 4 tabs of 2 parts each
	    for (int tabnr = 0; tabnr < 4; tabnr++) {
	        JPanel part = new JPanel(new GridBagLayout());
		    gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.HORIZONTAL;
		    gbc.weightx = 1; gbc.weighty = 0;
		    gbc.insets.left = gbc.insets.right = 4;
		    gbc.insets.top = gbc.insets.bottom = 1;
		    
		    gbc.gridy++; part.add(new JLabel("Receive Switch"), gbc);
		    gbc.gridy++; part.add(new JLabel("Receive Channel"), gbc);
		    gbc.gridy++; part.add(new JLabel("Patch Bank"), gbc);
		    gbc.gridy++; part.add(new JLabel("Patch Number"), gbc);
		    gbc.gridy++; part.add(new JLabel("Part Level"), gbc);
		    gbc.gridy++; part.add(new JLabel("Part Pan"), gbc);
		    gbc.gridy++; part.add(new JLabel("Part Coarse Tune"), gbc);
		    gbc.gridy++; part.add(new JLabel("Part Fine Tune"), gbc);
		    gbc.gridy++; part.add(new JLabel("Reverb Switch"), gbc);
		    gbc.gridy++; part.add(new JLabel("Chorus Switch"), gbc);
		    gbc.gridy++; part.add(new JLabel("Receive Program Change"), gbc);
		    gbc.gridy++; part.add(new JLabel("Receive Volume"), gbc);
		    gbc.gridy++; part.add(new JLabel("Receive Hold-1"), gbc);
		    gbc.gridy++; part.add(new JLabel("Output Select"), gbc);

		    gbc.anchor = GridBagConstraints.CENTER;
		    
		    for (int i = 0; i < 2; i++) {
		        gbc.gridx = 1 + i; gbc.gridy = 0;
		        int partnr = tabnr * 2 + i;
		        
		        gbc.gridy++; part.add(new CheckBoxWidget(null, patch, new JVModel(patch, partnr, 0x15), 
	                new JVSender(partnr, 0x15)), gbc);
		        gbc.gridy++; part.add(new ComboBoxWidget(null, patch, new JVModel(patch, partnr, 0x16), 
	                new JVSender(partnr, 0x16), DriverUtil.generateNumbers(1, 16, "0")), gbc);

		        final JComboBox bank    = new JComboBox(RolandJV80PatchBank.getBanks(patch.getDevice()));
		        final JComboBox patches = new JComboBox();
		        final int ctr = partnr;
		        ActionListener al = new ActionListener() {
		            final JV2Sender js = new JV2Sender(ctr, 0x17);
		            final JV2Model  jm = new JV2Model (patch, ctr, 0x17);
		            {
		                int patchnum = jm.get();
		                RolandJV80PatchBank pb = RolandJV80PatchBank.getBankByPatchNumber(patchnum);
		                // we don't have the data card, although the patch is on it
		                if (pb == null) { 
		                    bank.setSelectedIndex(0);
		                    patches.setSelectedIndex(0);
		                } else {
			                bank.setSelectedItem(pb.getName());
			                patches.setModel(new DefaultComboBoxModel(pb.getPatches()));
			                patches.setSelectedIndex(patchnum - pb.getOffset());
		                }
		            }
	                public void actionPerformed(ActionEvent e) {
		                String bankstr = (String) bank.getSelectedItem();
		                RolandJV80PatchBank pb = RolandJV80PatchBank.getBank(bankstr);
		                
		                if (e.getSource() == bank) {
			                int pidx = patches.getSelectedIndex();
			                patches.setModel(new DefaultComboBoxModel(pb.getPatches()));
			                if (pidx < patches.getItemCount()) 
			                    patches.setSelectedIndex(pidx);
		                }
		                int value = pb.getOffset() + patches.getSelectedIndex(); 
		                jm.set(value);
		                js.send(patch.getDriver(), value);
		            }
	            };
	            bank.addActionListener(al);
	            patches.addActionListener(al);
	            gbc.gridy++; part.add(bank, gbc);
	            gbc.gridy++; part.add(patches, gbc);
            
	            gbc.gridy++; part.add(new ScrollBarWidget(null, patch, 
                    0, 127, 0, new JVModel(patch, partnr, 0x19),
                    new JVSender(partnr, 0x19)), gbc);
	            gbc.gridy++; part.add(new ScrollBarWidget(null, patch, 
	                    0, 127, -64, new JVModel(patch, partnr, 0x1A),
	                    new JVSender(partnr, 0x1A)), gbc);
	            gbc.gridy++; part.add(new ScrollBarWidget(null, patch, 
	                    16, 112, -64, new JVModel(patch, partnr, 0x1B),
	                    new JVSender(partnr, 0x1B)), gbc);
	            gbc.gridy++; part.add(new ScrollBarWidget(null, patch, 
	                    14, 114, -64, new JVModel(patch, partnr, 0x1C),
	                    new JVSender(partnr, 0x1C)), gbc); 
	            gbc.gridy++; part.add(new CheckBoxWidget(null, patch, 
	                    new JVModel(patch, partnr, 0x1D), 
	                    new JVSender(partnr, 0x1D)), gbc);
	            gbc.gridy++; part.add(new CheckBoxWidget(null, patch, 
	                    new JVModel(patch, partnr, 0x1E), 
	                    new JVSender(partnr, 0x1E)), gbc);
	            gbc.gridy++; part.add(new CheckBoxWidget(null, patch, 
	                    new JVModel(patch, partnr, 0x1F), 
	                    new JVSender(partnr, 0x1F)), gbc);
	            gbc.gridy++; part.add(new CheckBoxWidget(null, patch, 
	                    new JVModel(patch, partnr, 0x20), 
	                    new JVSender(partnr, 0x20)), gbc);
	            gbc.gridy++; part.add(new CheckBoxWidget(null, patch, 
	                    new JVModel(patch, partnr, 0x21), 
	                    new JVSender(partnr, 0x21)), gbc);
	            gbc.gridy++; part.add(new ComboBoxWidget(null, patch,
	    		        new JVModel(patch, partnr, 0x22),
	    		        new JVSender(partnr, 0x22), 
	    		        new String[] {"MN", "SB", "PAT"}), gbc);
		    }
		    jtp.add("Part " + (2 * tabnr + 1) + "-" + (2 * tabnr + 2), part);
	    }
	    
	    return panel;
    }
    // sends to temporary performance
 	class JVSender extends SysexSender {
	    byte addr3; byte addr4; 
	    // retrieve default from patch
		public JVSender(int part, int msg_offset) {
		    super("F041@@461200000000**00F7");
		    addr3 = 0x10;
		    if (part > 0)
		        addr3 += 8 + part;
		    addr4 = (byte) msg_offset;
		}
		
        protected byte[] generate(int value) {
            byte[] data = super.generate(value);
            data[JV80Constants.ADDR3_IDX] = addr3;
            data[JV80Constants.ADDR4_IDX] = addr4;
            JV80Constants.calculateChecksum(data, 1);
            return data;
        }
	}
	class JVModel extends ParamModel {
	    final static int DATA_OFFSET = 9;
	    JVModel(Patch p, int part, int msg_offset) {
	        super(p, DATA_OFFSET + msg_offset);
	        if (part >= 0)
	            ofs += ((RolandJV80PerformanceDriver) p.getDriver()).performancePartOffsets[part];
	    }
	}

	class JV2Model extends JVModel {
	   JV2Model(Patch p, int tone, int msg_offset) {
 	       super(p, tone, msg_offset);
 	   }
 	   public int get() {
 	       return (patch.sysex[ofs] << 4) + (patch.sysex[ofs+1]);
 	   }
 	   public void set(int value) {
 	       patch.sysex[ofs] = (byte) (value >> 4);
 	       patch.sysex[ofs + 1] = (byte) (value & 0x0F);
 	   }
	}
 	class JV2Sender extends SysexSender {
	    byte addr3;
	    byte addr4;
	    // retrieve default from patch
		public JV2Sender(int part, int msg_offset) {
		    super("F041@@461200001000****00F7");
		    addr3 = 0x10;
		    if (part > 0)
		        addr3 += 8 + part;
		    addr4 = (byte) msg_offset;
		}
		
        protected byte[] generate(int value) {
            byte[] data = super.generate(value);
            data[JV80Constants.ADDR3_IDX] = addr3;
            data[JV80Constants.ADDR4_IDX] = addr4;
            data[JV80Constants.ADDR4_IDX + 1] = (byte) (value >> 4);
            data[JV80Constants.ADDR4_IDX + 2] = (byte) (value & 0x0F);
            JV80Constants.calculateChecksum(data, 2);
            return data;
        }
	}


}
