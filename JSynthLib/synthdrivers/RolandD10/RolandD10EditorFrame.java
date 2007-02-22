/*
 * Copyright 2006 Roger Westerlund
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
package synthdrivers.RolandD10;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.Border;

import core.ComboBoxWidget;
import core.ISinglePatch;
import core.Patch;
import core.PatchEditorFrame;
import core.ScrollBarWidget;

public class RolandD10EditorFrame extends PatchEditorFrame {

    private static Border BORDER = BorderFactory.createEtchedBorder();

    protected static void setNamedBorder(JComponent panel, String name) {
        panel.setBorder(BorderFactory.createTitledBorder(BORDER,name));
    }

    protected RolandD10EditorFrame(String name, ISinglePatch patch) {
        super(name, patch);
    }

    public static Object[] getStructureImages() {
        List<Icon> iconList = new ArrayList<Icon>();
        StringBuffer buffer = new StringBuffer();
        for (int index = 1; index < 14; index++) {
            buffer.setLength(0);
            buffer.append("images/structure");
            if (index < 10) {
                buffer.append(0);
            }
            buffer.append(index);
            buffer.append(".png");
            URL resource = RolandD10Support.class.getResource(buffer.toString());
            iconList.add(new ImageIcon(resource));
        }
        return iconList.toArray();
    }

    protected JPanel createTimbrePanel(Patch patch, String name, int[] offsets, EditSender[] editSenders) {
        JPanel panel = new JPanel(new GridBagLayout());
    
        setNamedBorder(panel, name);
    
        addWidget(panel,new ComboBoxWidget("Tone group", patch,
                new D10ParamModel(patch,offsets[0]), 
                EditSender.getTimbreSender(offsets[0]),
                RolandD10Support.getToneBanks()),
                0,GridBagConstraints.RELATIVE,1,1,0);
        addWidget(panel,new ScrollBarWidget("Tone number",patch,0,63,1,
                new D10ParamModel(patch, offsets[1]),editSenders[1]),
                1,GridBagConstraints.RELATIVE,1,1,0);
        addWidget(panel,new ScrollBarWidget("Key shift",patch,0,48,-24,
                new D10ParamModel(patch, offsets[2]),editSenders[2]),
                0,GridBagConstraints.RELATIVE,GridBagConstraints.REMAINDER,1,0);
        addWidget(panel,new ScrollBarWidget("Fine tune",patch,0,100,-50,
                new D10ParamModel(patch, offsets[3]),editSenders[3]),
                0,GridBagConstraints.RELATIVE,GridBagConstraints.REMAINDER,1,0);
        addWidget(panel,new ScrollBarWidget("Bender range",patch,0,100,0,
                new D10ParamModel(patch, offsets[4]),editSenders[4]),
                0,GridBagConstraints.RELATIVE,GridBagConstraints.REMAINDER,1,0);
        addWidget(panel,new ComboBoxWidget("Assign mode", patch,
                new D10ParamModel(patch,offsets[5]), editSenders[5],
                new String[] {"Poly 1","Poly 2","Poly 3","Poly 4"}),
                0,GridBagConstraints.RELATIVE,1,1,0);
        addWidget(panel,new ComboBoxWidget("Reverb switch", patch,
                new D10ParamModel(patch,offsets[6]), editSenders[6],
                new String[] {"Off","On"}),
                1,GridBagConstraints.RELATIVE,1,1,0);
    
        return panel;
    }
}
