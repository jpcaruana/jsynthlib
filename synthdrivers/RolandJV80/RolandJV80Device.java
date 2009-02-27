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

/* TODO
 * 
 * JSynthlib UI criticism:
 * o Configuration panel of the synth driver is hard to find
 * o EnvelopeCanvas is hard to determine its borders - maybe use a different color background? 
 * 
 * JSynthlib bugs/quirks:
 * o The textbox of the SliderWidget is updated again after mouse release instead of during the drag  
 * o Reverse node selection criteria to select latest nodes instead of first
 *   nodes which may be constant (so they can't be moved)
 * o CheckBox widgets are anchored differently and have different insets from other sysexwidgets  
 * o Can't revert patch - overriding dialog box after frame is closing is useless because originalpatch is not visible
 * 
 * Goals of patch editor:
 * o Should fit in ~800 pixels width 
 * o Most used widgets should be as accessable as possible
 * o Widgets should be grouped logically according to functionality
 * o Give most used widgets a slider/fader number and mark that number 
 *   in the according label
 * o verify whether labels and its values are correct or may be labeled more 
 *   according to current day terminology (I'm no expert).
 * 
 * TODO:
 * o Make envelope curve images non-hideous
 * o Fix layout of checkboxwidgets
 * + Make envelopecanvas more visible
 * + Fix patch selection in performance editor
 * + implement awareness of PCM/EXP/DATA cards
 * o In Patch|Get menu: possible to disable banks you don't have?
 * + Layout patcheditor widgets
 * - send name when focus lost wrt patchnamewidgets
 * o layout parts in performance editor
 * o make performance editor aware of rhythm patch selection on channel 10
 * o in batchbankeditor: somehow mark patches which are not possible due to missing exp board/pcm card
 * o test this thing in Windows
 * o BankDriver should throw an exception when no sysex msg is accepted
 * o in converter: put name of imported sysex file in field1
 * o in patchdriver: disable tone when it's using a nonexistant bank/patchnumber in bank? 
 */

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import core.Device;

/**
 * @author Sander Brandenburg
 * @version $Id$
 */
public class RolandJV80Device extends Device {
    private final static String INFO_TEXT =
    	"Setup whether you have an expansion/pcm card/data card installed. " +
    	"This will enable these banks for the driver. Select the JV80 driver and " +
		"click on 'show details' to see the configuration menu.\n\n" +
    	"IMPORTANT: Set memory protect OFF *every time you power up your synth* " + 
    	"if you want to save patches to the internal banks. \n\n" +	
		"If you have found bugs please use the Tracker on the the JSynthLib SourceForge page.\n" + 
		"For suggestions or comments, please email me " + 
		"at sjbrande@sourceforge.net\n";
    
    // array order is used to determine model
    final static String MODEL_JV80 = "JV-80";
    final static String MODEL_JV880 = "JV-880";
    final static String[] MODELS = new String[] { MODEL_JV80, MODEL_JV880 };
    
    final static String PREF_EXPANSION = "expansion";
    final static String PREF_PCM       = "pcm";
    final static String PREF_DATA      = "card";
    final static String PREF_MODEL     = "model";
    
    final static Map PREF_DEFAULTS = new HashMap();
    static {
        PREF_DEFAULTS.put(PREF_MODEL,     MODEL_JV80);
        PREF_DEFAULTS.put(PREF_EXPANSION, RolandJV80WaveBank.EXP_NONE.getName());
        PREF_DEFAULTS.put(PREF_PCM,       RolandJV80WaveBank.PCM_NONE.getName());
        PREF_DEFAULTS.put(PREF_DATA,      "False");
    }
    
	public RolandJV80Device() {
	    super("Roland", "JV80", null,
	            INFO_TEXT, "Sander Brandenburg");
    }

	// these drivers aren't stateful except for the model version
	RolandJV80BankDriver bankDriver;
	RolandJV80PatchDriver patchDriver;
	RolandJV80Converter converter;
	RolandJV80SystemSetupDriver systemSetupDriver;
	RolandJV80PerformanceDriver performanceDriver;
	
    public RolandJV80Device(Preferences prefs) {
        this();
        
        this.prefs = prefs;
        int id = getDeviceID();
        
        // XXX isn't there a sane way to obtain the same result?
        if (id <= 16) { 
            setDeviceID(16 + id);
        }
        
        
        
        patchDriver       = new RolandJV80PatchDriver();
        bankDriver        = new RolandJV80BankDriver(patchDriver);
        systemSetupDriver = new RolandJV80SystemSetupDriver();
        performanceDriver = new RolandJV80PerformanceDriver();
        converter         = new RolandJV80Converter(bankDriver);
        
        String model = getPref(PREF_MODEL);
        setup(model);
        
        addDriver(patchDriver);
        addDriver(bankDriver);
        addDriver(performanceDriver);
        addDriver(systemSetupDriver);
        addDriver(converter);
    }
    
    void setup(String model) {
        patchDriver.setup(model);
        bankDriver.setup();
        systemSetupDriver.setup(model);
        performanceDriver.setup(model);
    }

    RolandJV80PatchDriver getPatchDriver() {
        return patchDriver;
    }
   
    RolandJV80BankDriver getBankDriver() {
        return bankDriver;
    }
    RolandJV80PerformanceDriver getPerformanceDriver() {
        return performanceDriver;
    }
    
    RolandJV80SystemSetupDriver getSystemSetupDriver() {
        return systemSetupDriver;
    }
        
    protected JPanel config() {
        JPanel pane1 = new JPanel(new GridBagLayout());

        JLabel lmodel = new JLabel("JV Model");
        final JComboBox jmodel = new JComboBox(MODELS);
        jmodel.setSelectedItem(getPref(PREF_MODEL));
        jmodel.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
			    String model = (String) jmodel.getSelectedItem();
			    setPref(PREF_MODEL, model);
			    setup(model);
			}
        });
        
        JLabel lexp = new JLabel("Expansion");
        final JComboBox jexp = new JComboBox(RolandJV80WaveBank.getBanks(RolandJV80WaveBank.BANKTYPE_EXPANSION));
        jexp.setSelectedItem(getPref(PREF_EXPANSION));
        jexp.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
			    setPref(PREF_EXPANSION, (String) jexp.getSelectedItem());
			}
        });
        
        JLabel lpcm = new JLabel("PCM Card");
        final JComboBox jpcm = new JComboBox(RolandJV80WaveBank.getBanks(RolandJV80WaveBank.BANKTYPE_PCM));
        jpcm.setSelectedItem(getPref(PREF_PCM));
        jpcm.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
			    setPref(PREF_PCM, (String) jpcm.getSelectedItem());
			}
        });
        
        JLabel ldata = new JLabel("Data Card");
        final JCheckBox jdata = new JCheckBox();
        jdata.setSelected(Boolean.valueOf(getPref(PREF_DATA)).booleanValue());
        jdata.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                setPref(PREF_DATA, String.valueOf(jdata.isSelected()));
            }
        });

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.gridy = 0; g.weightx = 0.0; g.weighty = 0.0; 
        g.anchor = GridBagConstraints.WEST; g.fill = GridBagConstraints.NONE;
        g.insets = new Insets(5, 5, 5, 5);
        
        g.gridy = 0; pane1.add(lmodel, g);
        g.gridy = 1; pane1.add(lexp, g);
        g.gridy = 2; pane1.add(lpcm, g);
        g.gridy = 3; pane1.add(ldata, g);

        g.gridx = 1;
        
        g.gridy = 0; pane1.add(jmodel, g);
        g.gridy = 1; pane1.add(jexp, g);
        g.gridy = 2; pane1.add(jpcm, g);
        g.gridy = 3; pane1.add(jdata, g);
        
        return pane1;
    }
    
    void setPref(String key, String value) {
        prefs.put(key, value);
    }
    
    String getPref(String key) {
        return getPref(prefs, key);
    }
    
    static String getPref(Preferences prefs, String key) {
        String value = prefs.get(key, null);
        if (value == null) {
            return (String) PREF_DEFAULTS.get(key); // may still return null
        }
        return value;
    }
    
    static String getPref(Device dev, String key) {
        RolandJV80Device jvdev = (RolandJV80Device) dev;
        return getPref(jvdev.prefs, key);
    }
    
}
