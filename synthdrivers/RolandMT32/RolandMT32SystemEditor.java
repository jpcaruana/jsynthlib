/*
 * Copyright 2004,2005 Fred Jan Kraan
 *
 * This file is part of JSynthLib.
 *
 * JSynthLib is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
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

/**
 * System Editor for Roland MT32.
 *
 * @version $Id$
 */

package synthdrivers.RolandMT32;
import core.*;
import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;

class RolandMT32SystemEditor extends PatchEditorFrame {
                                            
    final String [] RevModeName  = new String [] {"Room", "Hall", "Plate", "Tap9 delay" 
    };
 
    final String [] MidiChanName  = new String [] {"1",  "2",  "3",  "4",  "5",  "6",  "7",  "8",
                                                   "9", "10", "11", "12", "13", "14", "15", "16",
                                                   "Off"
    };
//    int length = 128;
                                        
    /** For Alignment, a size to scrollbar labels */
    //private int labelWidth;

    public RolandMT32SystemEditor(Patch patch) {
	super ("Roland MT-32 System Editor", patch);
//      Common Pane
        gbc.weightx=5;
        int gy = 0;  // row count
        int k = 0;  // timbre number offset. Not implemented yet.
        int sysad = 0x10;
        int lwc = getLabelWidth("Master Volume ");  // Longest label length
        JPanel cmnPane=new JPanel();
        cmnPane.setLayout(new GridBagLayout());	 
        gbc.weightx=0;   
        JTabbedPane sysTabPane = new JTabbedPane();
        
        JPanel sysPane = new JPanel();
        sysPane.setLayout(new GridBagLayout());
        
            final String[] freqText = new String[128];
    int start = 4321;
    int interval = 2;
    for (int i = 0; i < freqText.length; i++) {
        int freq10 = start + i * interval;
        freqText[i] = String.valueOf(freq10/10) + "." + String.valueOf(freq10-10*(freq10/10));
    }
	    addWidget(sysPane, new ScrollBarLookupWidget("Master Tune", patch, 0, 127, lwc,
	        new MT32Model(patch,0x00), new MT32Sender(0x0, sysad), freqText), 0, gy, 1, 1, 11);
        gy++;
        addWidget(sysPane, new ComboBoxWidget("Reverb Mode", patch, 
            new MT32Model(patch,0x01), new MT32Sender(0x01, sysad), RevModeName), 0, gy, 1, 1, 11);
        gy++;
        addWidget(sysPane, new ScrollBarWidget("Reverb Time", patch,0,7,1,lwc, 
            new MT32Model(patch,0x02), new MT32Sender(0x02, sysad)),0,gy,5,1,2);
        gy++;
        addWidget(sysPane, new ScrollBarWidget("Reverb Level", patch,0,7,0,lwc, 
            new MT32Model(patch,0x03), new MT32Sender(0x03, sysad)),0,gy,5,1,2);
        gy++;
        addWidget(sysPane, new ScrollBarWidget("Master Volume", patch,0,100,0,lwc, 
            new MT32Model(patch,0x16), new MT32Sender(0x16, sysad)),0,gy,5,1,3);
        sysTabPane.add(sysPane, gbc);
        sysTabPane.addTab("System", sysPane);
        
        JPanel partResPane = new JPanel();
        partResPane.setLayout(new GridBagLayout());	 
        gy=0;
        lwc = getLabelWidth("Partial Reserve (part 1) ");  // Longest label length
        addWidget(partResPane, new ScrollBarWidget("Partial Reserve (part 1)", patch,0,32,0,lwc, 
            new MT32Model(patch,0x04), new MT32Sender(0x04, sysad)),0,gy,5,1,3);
        gy++;
        addWidget(partResPane, new ScrollBarWidget("Partial Reserve (part 2)", patch,0,32,0,lwc, 
            new MT32Model(patch,0x05), new MT32Sender(0x05, sysad)),0,gy,5,1,3);
        gy++;
        addWidget(partResPane, new ScrollBarWidget("Partial Reserve (part 3)", patch,0,32,0,lwc, 
            new MT32Model(patch,0x06), new MT32Sender(0x06, sysad)),0,gy,5,1,3);
        gy++;
        addWidget(partResPane, new ScrollBarWidget("Partial Reserve (part 4)", patch,0,32,0,lwc, 
            new MT32Model(patch,0x07), new MT32Sender(0x07, sysad)),0,gy,5,1,3);
        gy++;
        addWidget(partResPane, new ScrollBarWidget("Partial Reserve (part 5)", patch,0,32,0,lwc, 
            new MT32Model(patch,0x08), new MT32Sender(0x08, sysad)),0,gy,5,1,3);
        gy++;
        addWidget(partResPane, new ScrollBarWidget("Partial Reserve (part 6)", patch,0,32,0,lwc, 
            new MT32Model(patch,0x09), new MT32Sender(0x09, sysad)),0,gy,5,1,3);
        gy++;
        addWidget(partResPane, new ScrollBarWidget("Partial Reserve (part 7)", patch,0,32,0,lwc, 
            new MT32Model(patch,0x0A), new MT32Sender(0x0A, sysad)),0,gy,5,1,3);
        gy++;
        addWidget(partResPane, new ScrollBarWidget("Partial Reserve (part 8)", patch,0,32,0,lwc, 
            new MT32Model(patch,0x0B), new MT32Sender(0x0B, sysad)),0,gy,5,1,3);
        gy++;
        addWidget(partResPane, new ScrollBarWidget("Partial Reserve (part R)", patch,0,32,0,lwc, 
            new MT32Model(patch,0x0C), new MT32Sender(0x0C, sysad)),0,gy,5,1,3);
        sysTabPane.add(partResPane, gbc);
        sysTabPane.addTab("Partial Reserve", partResPane);
        
        JPanel midiChanPane = new JPanel();
        midiChanPane.setLayout(new GridBagLayout());	 
        gy=0;
        lwc = getLabelWidth("Midi Channel (part 1) ");  // Longest label length
        addWidget(midiChanPane, new ComboBoxWidget("Midi Channel (part 1)", patch, 
            new MT32Model(patch,0x0D), new MT32Sender(0x0D, sysad), MidiChanName), 0, gy, 5, 1, 11);
        gy++;
        addWidget(midiChanPane, new ComboBoxWidget("Midi Channel (part 2)", patch, 
            new MT32Model(patch,0x0E), new MT32Sender(0x0E, sysad), MidiChanName), 0, gy, 5, 1, 12);
        gy++;
        addWidget(midiChanPane, new ComboBoxWidget("Midi Channel (part 3)", patch, 
            new MT32Model(patch,0x0F), new MT32Sender(0x0F, sysad), MidiChanName), 0, gy, 5, 1, 13);
        gy++;
        addWidget(midiChanPane, new ComboBoxWidget("Midi Channel (part 4)", patch, 
            new MT32Model(patch,0x10), new MT32Sender(0x10, sysad), MidiChanName), 0, gy, 5, 1, 14);
        gy++;
        addWidget(midiChanPane, new ComboBoxWidget("Midi Channel (part 5)", patch, 
            new MT32Model(patch,0x11), new MT32Sender(0x11, sysad), MidiChanName), 0, gy, 5, 1, 15);
        gy++;
        addWidget(midiChanPane, new ComboBoxWidget("Midi Channel (part 6)", patch, 
            new MT32Model(patch,0x12), new MT32Sender(0x12, sysad), MidiChanName), 0, gy, 5, 1, 16);
        gy++;
        addWidget(midiChanPane, new ComboBoxWidget("Midi Channel (part 7)", patch, 
            new MT32Model(patch,0x13), new MT32Sender(0x13, sysad), MidiChanName), 0, gy, 5, 1, 17);
        gy++;
        addWidget(midiChanPane, new ComboBoxWidget("Midi Channel (part 8)", patch, 
            new MT32Model(patch,0x14), new MT32Sender(0x14, sysad), MidiChanName), 0, gy, 5, 1, 18);
        gy++;
        addWidget(midiChanPane, new ComboBoxWidget("Midi Channel (part R)", patch, 
            new MT32Model(patch,0x15), new MT32Sender(0x15, sysad), MidiChanName), 0, gy, 5, 1, 19);
        sysTabPane.add(midiChanPane, gbc);
        sysTabPane.addTab("Midi Channel", midiChanPane);
        
        cmnPane.add(sysTabPane, gbc);
        gbc.gridx=0; gbc.gridy=0; gbc.gridwidth=5; gbc.gridheight=3;
        gbc.fill=GridBagConstraints.BOTH;
        gbc.anchor=GridBagConstraints.EAST;
        cmnPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
            "System",TitledBorder.CENTER, TitledBorder.CENTER));  
        scrollPane.add(cmnPane,gbc);
    
        pack();
    }

    private int getLabelWidth(String s) {
	return (int) (new JLabel(s)).getPreferredSize().getWidth();
    }
}
