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

/**
* Alesis DM5 Program Change Table Editor
 * 
 * @author Jeff Weber
 */
public class AlesisDM5PrChgEditor extends PatchEditorFrame {
    /** Size of DM5 sysex header--7 bytes.
    */
    private static final int headerSize = Constants.HDR_SIZE;
    
    /** Program locations for DM5. The DM5 has twenty-one locations for single
    * set patches, numbered 0 through 20.
    */
    private static final String[] setNums = new String[] { "0", "1", "2", "3",
        "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15",
        "16", "17", "18", "19", "20" };
    
    /** Constructs a AlesisDM5PrChgEditor for the selected patch.
        */
    AlesisDM5PrChgEditor(Patch patch) {
        super("Alesis DM5 Program Change Table Editor", patch);
        for (int i = 0; i < 128; i++) {
            addWidgets(scrollPane, patch, i);
        }
        pack();
    }
    
    /** Adds ComboBoxes to the AlesisDM5PrChgEditor for the selected patch.
        */
    private void addWidgets(JPanel panel, Patch patch, int prgChgNum) {
        addWidget(panel, new ComboBoxWidget(String.valueOf(prgChgNum), patch,
                                            new ParamModel(patch, headerSize + prgChgNum),
                                            new DM5ProgChgSender(patch, 1), setNums), prgChgNum / 16,
                  prgChgNum % 16, 1, 1, prgChgNum);
    }
    
    /** A sender which sends the whole patch.
        */
    /** A subclass of SysexSender used to send the program change table patch.
        * Since the editor uses all ComboBoxWidgets (no sliders) this sender
        * just sends the whole patch.
        */
    private class DM5ProgChgSender extends SysexSender {
        int parameter;        
        Patch patch;
        
        /** Constructs a DM5ProgChgSender given the patch and the parameter.
            */
        public DM5ProgChgSender(Patch p, int param) {
            parameter = param;
            patch = p;
        }
        
        /** Generate method for sending entire patch.
            */
        public byte[] generate(int value) {
            patch.sysex[0] = (byte) 0xF0;
            ((AlesisDM5PrChgDriver)(patch.getDriver())).calculateChecksum(patch);
            return patch.sysex;
        }
    }
}