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
 * (Rhythm) Setup Temp Editor for Roland MT32.
 *
 * @version $Id$
 */

package synthdrivers.RolandMT32;
import core.*;
import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;

import org.jsynthlib.core.CheckBoxWidget;
import org.jsynthlib.core.ComboBoxWidget;
import org.jsynthlib.core.Patch;
import org.jsynthlib.core.PatchEditorFrame;
import org.jsynthlib.core.ScrollBarWidget;

class RolandMT32RhythmSetupTempEditor extends PatchEditorFrame {
                                            
    final String [] TimbreName  = new String [] {
    		" M1", " M2", " M3", " M4", " M5", " M6", " M7", " M8", 
	        " M9", "M10", "M11", "M12", "M13", "M14", "M15", "M16",
	        "M17", "M18", "M19", "M20", "M21", "M22", "M23", "M24",
	        "M25", "M26", "M27", "M28", "M29", "M30", "M31", "M32",
	        "M33", "M34", "M35", "M36", "M37", "M38", "M39", "M40",
	        "M41", "M42", "M43", "M44", "M45", "M46", "M47", "M48",
	        "M49", "M50", "M51", "M52", "M53", "M54", "M55", "M56",
	        "M57", "M58", "M59", "M60", "M61", "M62", "M63", "M64",
            " R1", " R2", " M3", " R4", " R5", " R6", " R7", " R8", 
	        " R9", "R10", "R11", "R12", "R13", "R14", "R15", "R16",
	        "R17", "R18", "R19", "R20", "R21", "R22", "R23", "R24",
	        "R25", "R26", "R27", "R28", "R29", "R30", 
	        "Off"
    };
 
    final String [] RevSwitchName  = new String [] {
    		"Off", "On"
    };
 
    /** For Alignment, a size to scrollbar labels */
    //private int labelWidth;

    public RolandMT32RhythmSetupTempEditor(Patch patch) {
	super ("Roland MT-32 Rhythm Setup Temp Editor", patch);
//      Common Pane
        gbc.weightx=5;
        int gy = 0;  // row count
        int k = 0x10 + 0x80;
        int basad = 3; // point to base address range 03 (00 00) of Patch temp Area
        int lwc = getLabelWidth("Reverb Switch  ");  // Longest label length
        JPanel cmnPane=new JPanel();
        cmnPane.setLayout(new GridBagLayout());	 
        gbc.weightx=0;
        addWidget(cmnPane, new ComboBoxWidget("Timbre", patch, 
            new MT32Model(patch,0x00), new MT32Sender(k+0x00, basad), TimbreName), 0, gy, 1, 1, 11);
        gy++;
        addWidget(cmnPane, new ScrollBarWidget("Output Level", patch,0,100,0,lwc, 
            new MT32Model(patch,0x01), new MT32Sender(k+0x01, basad)),0,gy,5,1,1);
        gy++;
        addWidget(cmnPane, new ScrollBarWidget("Pan Pot (R-L)", patch,0,14,-7,lwc, 
            new MT32Model(patch,0x02), new MT32Sender(k+0x02, basad)),0,gy,5,1,2);
	gy++;
        addWidget(cmnPane, new CheckBoxWidget ("Reverb", patch, 
            new MT32Model(patch,0x03), new MT32Sender(k+0x03, basad)),0,gy,5,1,4);   
//        addWidget(cmnPane, new ComboBoxWidget("Reverb Switch", patch, 
//            new MT32Model(patch,0x03), new MT32Sender(k+0x03,basad), RevSwitchName), 0, gy, 1, 1, 11);

        gbc.gridx=0; gbc.gridy=0; gbc.gridwidth=5; gbc.gridheight=3;
        gbc.fill=GridBagConstraints.BOTH;
        gbc.anchor=GridBagConstraints.EAST;
        cmnPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
            "Rhythm Setup",TitledBorder.CENTER, TitledBorder.CENTER));  
        scrollPane.add(cmnPane,gbc);
   

        pack();
    }

    private int getLabelWidth(String s) {
	return (int) (new JLabel(s)).getPreferredSize().getWidth();
    }
}
