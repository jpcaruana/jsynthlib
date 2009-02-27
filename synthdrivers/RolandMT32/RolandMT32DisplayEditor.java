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
 * Display Editor for Roland MT32.
 *
 * @version $Id$
 */

package synthdrivers.RolandMT32;
import core.*;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;

class RolandMT32DisplayEditor extends PatchEditorFrame {
                                            
 
    /** For Alignment, a size to scrollbar labels */
    //private int labelWidth;

    public RolandMT32DisplayEditor(Patch patch) {
	    super ("Roland MT-32 Display Editor", patch);
//      Common Pane
        gbc.weightx=5;
       
        int gy = 0;  // row count
        JPanel cmnPane=new JPanel();
        cmnPane.setLayout(new GridBagLayout());	 
        gbc.weightx=0;
        addWidget(cmnPane, new PatchNameWidget("Text ", patch), 0, gy, 2, 1, 0);
        gy++;

        gbc.gridx=0; gbc.gridy=0; gbc.gridwidth=5; gbc.gridheight=3;
        gbc.fill=GridBagConstraints.BOTH;
        gbc.anchor=GridBagConstraints.EAST;
        cmnPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
            "Display", TitledBorder.CENTER, TitledBorder.CENTER));  
        scrollPane.add(cmnPane,gbc);
   

        pack();
    }

    private int getLabelWidth(String s) {
	return (int) (new JLabel(s)).getPreferredSize().getWidth();
    }
}
