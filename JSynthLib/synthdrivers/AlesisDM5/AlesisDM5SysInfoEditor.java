/*
 * Copyright 2004 Jeff Weber
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

package synthdrivers.AlesisDM5;

import core.*;
import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;

/** Alesis DM5 System Info Editor
* 
* @author Jeff Weber
*/
public class AlesisDM5SysInfoEditor extends PatchEditorFrame {
    /** Size of program patch header--7 bytes.
    */
    static final int headerSize = Constants.HDR_SIZE;
    
    /** Constructs a AlesisDM5SysInfoEditor for the selected patch.*/
    AlesisDM5SysInfoEditor(Patch patch)
    {
        super ("Alesis DM5 System Info Editor",patch);  
        scrollPane.setLayout(new GridLayout(0,1));
        addTrigPane(patch);
        pack();
        show();
    }
    
    private void addTrigPane(Patch patch) {
        JPanel dm5EditPanel = new JPanel();        
        dm5EditPanel.setLayout(new BoxLayout(dm5EditPanel, BoxLayout.Y_AXIS));
        dm5EditPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"System Info",TitledBorder.CENTER,TitledBorder.CENTER));
        addWidgets(dm5EditPanel, patch);
        scrollPane.add(dm5EditPanel,gbc);        
    }
    
    private void addWidgets(JPanel panel, Patch patch) {
        addWidget(panel,
                  new CheckBoxWidget("Omni Enable", patch,
                                     new DM5SysInfoModel(patch, headerSize + 0, 1, true),
                                     new DM5SysInfoSender(patch, 1)),
                  0, 0,
                  1, 1,
                  -1);

        addWidget(panel,
                  new CheckBoxWidget("MIDI Thru Enable", patch,
                                     new DM5SysInfoModel(patch, headerSize + 0, 2, false),
                                     new DM5SysInfoSender(patch, 1)),
                  0, 0,
                  1, 1,
                  -2);
        
        addWidget(panel,
                  new CheckBoxWidget("Program Change Enable", patch,
                                     new DM5SysInfoModel(patch, headerSize + 0, 4, false),
                                     new DM5SysInfoSender(patch, 1)),
                  0, 0,
                  1, 1,
                  -3);
        
        addWidget(panel,
                  new CheckBoxWidget("Controllers Enable", patch,
                                     new DM5SysInfoModel(patch, headerSize + 0, 8, false),
                                     new DM5SysInfoSender(patch, 1)),
                  0, 0,
                  1, 1,
                  -4);
        
        addWidget(panel,
                  new CheckBoxWidget("Drumset Edited", patch,
                                     new DM5SysInfoModel(patch, headerSize + 0, 16, false),
                                     new DM5SysInfoSender(patch, 1)),
                  0, 0,
                  1, 1,
                  -5);
        
        addWidget(panel,
                  new ComboBoxWidget("Footswitch Mode", patch,
                                     new DM5SysInfoModel(patch, headerSize + 0, 32, false),
                                     new DM5SysInfoSender(patch, 1),
                                     new String[] {"Hi Hat", "Drumset Advance"}),
                  0, 0,
                  1, 1,
                  1);
        
        addWidget(panel,
                  new CheckBoxWidget("Note Chase Enable", patch,
                                     new DM5SysInfoModel(patch, headerSize + 0, 64, false),
                                     new DM5SysInfoSender(patch, 1)),
                  0, 0,
                  1, 1,
                  -6);
        
        addWidget(panel,
                  new ComboBoxWidget("Currently Selected Drumset", patch,
                                     new ParamModel(patch, headerSize + 2),
                                     new DM5SysInfoSender(patch, 1),
                                     new String[] {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20"}),
                  0, 0,
                  1, 1,
                  2);
    }
    
    class DM5SysInfoModel extends ParamModel {
        public int bitmask;
        public int mult;
        public boolean reverse;
        
        public DM5SysInfoModel(Patch p, int o, int b, boolean r) {
            ofs = o; patch = p; bitmask = b; reverse = r;
            if ((bitmask&  1) ==   1) mult =   1; else
                if ((bitmask&  2) ==   2) mult =   2; else
                    if ((bitmask&  4) ==   4) mult =   4; else
                        if ((bitmask&  8) ==   8) mult =   8; else
                            if ((bitmask& 16) ==  16) mult =  16; else
                                if ((bitmask& 32) ==  32) mult =  32; else
                                    if ((bitmask& 64) ==  64) mult =  64; else
                                        if ((bitmask&128) == 128) mult = 128;
        }
        
        public void set(int i) {
            if (reverse) {
                i = (i - 1) * -1;
            }
            patch.sysex[ofs] = (byte) ((i*mult) + (patch.sysex[ofs]&(~bitmask)));
        }

        public int get() {
            int returnVal = (patch.sysex[ofs]&bitmask)/mult;
            if (reverse) {
                returnVal = (returnVal - 1) * -1;
            }
            return returnVal;
        }
    }
    
    class DM5SysInfoSender extends SysexSender {
        int parameter;
        Patch patch;
        public DM5SysInfoSender(Patch p, int param) {
            parameter = param;
            patch = p;
        }
        public byte[] generate (int value) {
            patch.sysex[0] = (byte)0xF0;
            return patch.sysex;
        }
    }
}