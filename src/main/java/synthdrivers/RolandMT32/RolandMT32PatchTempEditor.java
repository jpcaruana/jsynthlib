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
 * Patch Temp Editor for Roland MT32.
 *
 * @version $Id$
 */

package synthdrivers.RolandMT32;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.jsynthlib.core.CheckBoxWidget;
import org.jsynthlib.core.ComboBoxWidget;
import org.jsynthlib.core.ErrorMsg;
import org.jsynthlib.core.Patch;
import org.jsynthlib.core.PatchEditorFrame;
import org.jsynthlib.core.ScrollBarWidget;


class RolandMT32PatchTempEditor extends PatchEditorFrame {
                                            
    final String [] TimbreGroupName  = new String [] {"Group A", 
    		                                          "Group B", 
    		                                          "Memory", 
    		                                          "Rhythm"
    };
/* 
    final String [] TimbreNameGroupA  = new String [] {"Acou Piano 1", "Acou Piano 2"};
    final String [] TimbreNameGroupB  = new String [] {"Acou Bass 1", "Acou Bass 2"};
    final String [] TimbreNameGroupM  = new String [] {"Memory 1", "Memory 2"};
    final String [] TimbreNameGroupR  = new String [] {"Rhythm 1", "Rhythm 2"};
    String [] TimbreNameGroup = new String [64];
    
    for (int strCnt = 0; strCnt < 64; strCnt++) {
        TimbreNameGroup [strCnt] = ("" +(strCnt+1));
    };
*/    
    final String [] AssyModeName  = new String [] {"Poly 1", 
    		                                       "Poly 2", 
    		                                       "Poly 3", 
    		                                       "Poly 4"
    };
 
    /** For Alignment, a size to scrollbar labels */
    //private int labelWidth;

    public RolandMT32PatchTempEditor(Patch patch) {
	super ("Roland MT-32 Patch Temp Editor", patch);
        
        MT32Model PTAModH = new MT32Model(patch,-3);
        MT32Model PTAModM = new MT32Model(patch,-2);
        MT32Model PTAModL = new MT32Model(patch,-1);
        int PTAAddrH = PTAModH.get();
        int PTAAddrM = PTAModM.get();
        int PTAAddrL = PTAModL.get();
        
        ErrorMsg.reportStatus("Patch source address: " + PTAAddrH + " / " + PTAAddrM + " / " + PTAAddrL);  
        
//      Common Pane
        gbc.weightx=5;
        int gy = 0;  // row count
        int k = PTAAddrL + (PTAAddrM * 0x80);  // patch address offset.
        int basad = PTAAddrH; // point to base address range 03 (00 00) of Patch temp Area
        int lwc = getLabelWidth("Timbre Number  ");  // Longest label length
        JPanel cmnPane=new JPanel();
        cmnPane.setLayout(new GridBagLayout());	 
        gbc.weightx=0;
        ComboBoxWidget TGselect = new ComboBoxWidget("Timbre Group", patch, 
                new MT32Model(patch,0x00), new MT32Sender(k+0x00, basad), TimbreGroupName);
        addWidget (cmnPane, TGselect, 0, gy, 1, 1, 11);
//        addWidget(cmnPane, new ComboBoxWidget("Timbre Group", patch, 
//            new MT32Model(patch,0x00), new MT32Sender(k+0x00, basad), TimbreGroupName), 0, gy, 1, 1, 11);
        gy++;
        
/*        TGselect.addPropertyChangeListener(new ChangeListener() {
    	    public void stateChanged(ChangeEvent e) {
    	    	int tngc;
    	    	int groupNo = TGselect.getValue();
    	        if (groupNo == 0) { for ( tngc=0; tngc < 128; tngc++) {
    	        	TimbreNameGroup[tngc] = TimbreNameGroupA[tngc]; }
    	        }
    	        if (groupNo == 1) { for ( tngc=0; tngc < 128; tngc++) {
    	        	TimbreNameGroup[tngc] = TimbreNameGroupB[tngc]; }
    	        }
    	        if (groupNo == 2) { for ( tngc=0; tngc < 128; tngc++) {
    	        	TimbreNameGroup[tngc] = TimbreNameGroupM[tngc]; }
    	        }
    	        if (groupNo == 3) { for ( tngc=0; tngc < 128; tngc++) {
    	        	TimbreNameGroup[tngc] = TimbreNameGroupR[tngc]; }
    	        }
         }});

        ComboBoxWidget TimSelect = new ComboBoxWidget("Timbre Number", patch, 
                new MT32Model(patch,0x00), new MT32Sender(k+0x00, basad), TimbreNameGroup);
        addWidget (cmnPane, TimSelect, 0, gy, 1, 1, 11);
*/
        addWidget(cmnPane, new ScrollBarWidget("Timbre Number", patch,0,63,1,lwc, 
            new MT32Model(patch,0x01), new MT32Sender(k+0x01, basad)),0,gy,5,1,1);
        gy++;
        addWidget(cmnPane, new ScrollBarWidget("Key Shift", patch,0,48,-24,lwc, 
            new MT32Model(patch,0x02), new MT32Sender(k+0x02, basad)),0,gy,5,1,2);
        gy++;
        addWidget(cmnPane, new ScrollBarWidget("Fine Tune", patch,0,100,-50,lwc, 
            new MT32Model(patch,0x03), new MT32Sender(k+0x03, basad)),0,gy,5,1,3);
        gy++;
        addWidget(cmnPane, new ScrollBarWidget("Bender Range", patch,0,24,0,lwc, 
            new MT32Model(patch,0x04), new MT32Sender(k+0x04,basad)),0,gy,5,1,3);
        gy++;
        addWidget(cmnPane, new ComboBoxWidget("Assign Mode", patch, 
            new MT32Model(patch,0x05), new MT32Sender(k+0x05,basad), AssyModeName), 0, gy, 1, 1, 11);
        gy++;
        addWidget(cmnPane, new CheckBoxWidget ("Reverb Switch", patch, 
            new MT32Model(patch,0x06), new MT32Sender(k+0x06, 3)),0,gy,5,1,4);   
	// 0x07 dummy
        addWidget(cmnPane, new ScrollBarWidget("Output Level", patch,0,100,0,lwc, 
            new MT32Model(patch,0x08), new MT32Sender(k+0x08, basad)),0,gy,5,1,3);
        gy++;
        addWidget(cmnPane, new ScrollBarWidget("Output Level", patch,0,100,0,lwc, 
            new MT32Model(patch,0x08), new MT32Sender(k+0x08, basad)),0,gy,5,1,3);
        gy++;
        addWidget(cmnPane, new ScrollBarWidget("Panpot (R-L)", patch,0,14,0,lwc, 
            new MT32Model(patch,0x09), new MT32Sender(k+0x09, basad)),0,gy,5,1,3);

        gbc.gridx=0; gbc.gridy=0; gbc.gridwidth=5; gbc.gridheight=3;
        gbc.fill=GridBagConstraints.BOTH;
        gbc.anchor=GridBagConstraints.EAST;
        cmnPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
            "Patch Temp",TitledBorder.CENTER, TitledBorder.CENTER));  
        scrollPane.add(cmnPane,gbc);
   

        pack();
    }

    private int getLabelWidth(String s) {
	return (int) (new JLabel(s)).getPreferredSize().getWidth();
    }
}
