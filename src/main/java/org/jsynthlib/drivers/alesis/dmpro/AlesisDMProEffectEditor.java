/*
 * Copyright 2004 Peter Hageus
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

package org.jsynthlib.drivers.alesis.dmpro;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import org.jsynthlib.core.ComboBoxWidget;
import org.jsynthlib.core.Patch;
import org.jsynthlib.core.PatchEditorFrame;
import org.jsynthlib.core.ScrollBarWidget;
import org.jsynthlib.core.SysexSender;
import org.jsynthlib.core.SysexWidget;




/**
 * Created on den 4 januari 2003, 23:01
 * @author  Peter Hageus
 */
class AlesisDMProEffectEditor extends PatchEditorFrame {

    /** Creates a new instance of AlesisDMProEffectEditor */
    public AlesisDMProEffectEditor(Patch patch)
    {
        super ("Alesis DM Pro Effect Editor",patch);

        AlesisDMProParser oParser = new AlesisDMProParser(patch);

        JPanel pnCommon = new JPanel();
        pnCommon.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        pnCommon.setLayout(new GridBagLayout());

        GridBagConstraints oGbc = new GridBagConstraints();

        JPanel pnRev = new JPanel();
        pnRev.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        pnRev.setLayout(new GridLayout(6,2));

        JPanel pnOverdrive = new JPanel();
        pnOverdrive.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        pnOverdrive.setLayout(new GridLayout(1,2));

        JPanel pnDelay = new JPanel();
        pnDelay.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        pnDelay.setLayout(new GridLayout(3,2));

        JPanel pnPitch = new JPanel();
        pnPitch.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        pnPitch.setLayout(new GridLayout(4,2));

        JPanel pnEQ = new JPanel();
        pnEQ.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        pnEQ.setLayout(new GridLayout(2,2));

        oGbc.gridx = 0;
        oGbc.gridy = 0;
        oGbc.gridwidth = 1;
        oGbc.gridheight = 1;
        oGbc.anchor = GridBagConstraints.NORTHEAST;
        oGbc.fill = GridBagConstraints.HORIZONTAL;
        oGbc.weightx = 0;
        oGbc.weighty = 0;

        scrollPane.add(pnCommon);

        pnCommon.add(pnRev, oGbc);
        oGbc.gridy = 1;
        pnCommon.add(pnOverdrive, oGbc);
        oGbc.gridy = 2;
        pnCommon.add(pnDelay, oGbc);
        oGbc.gridy = 3;
        pnCommon.add(pnPitch, oGbc);
        oGbc.gridy = 4;
        pnCommon.add(pnEQ, oGbc);


        //Reverb
        addWidget(pnRev, new ComboBoxWidget("Reverb type", patch,
                  new AlesisDMProFXModel(oParser, 0, 7, 3),
                  new AlesisDMProFXSender(0, 1),
                  new String[] {"Plate1","Plate2","Room","Hall","Large","Gate","Reverse"}
                  ), 0, 0, 2, 1, 0);

        pnRev.add(new JLabel(""));  //Placeholder...

        addWidget(pnRev, new ScrollBarWidget("Reverb level", patch, 0, 99, 0,
                  new AlesisDMProFXModel(oParser, 0, 0, 7),
                  new AlesisDMProFXSender(0, 0)), 0, 1, 1, 1, 1);

        addWidget(pnRev, new ScrollBarWidget("Predelay 10ms", patch, 0, 29, 0,
                  new AlesisDMProFXModel(oParser, 1, 2, 5),
                  new AlesisDMProFXSender(0, 2)), 2, 1, 1, 1, 2);

        addWidget(pnRev, new ScrollBarWidget("Input filter", patch, 0, 99, 0,
                  new AlesisDMProFXModel(oParser, 3, 3, 7),
                  new AlesisDMProFXSender(0, 5)), 0, 2, 1, 1, 2);

        addWidget(pnRev, new ScrollBarWidget("Predelay 1ms", patch, 0, 9, 0,
                  new AlesisDMProFXModel(oParser, 1, 7, 4),
                  new AlesisDMProFXSender(0, 3)), 2, 2, 1, 1, 3);

        addWidget(pnRev, new ScrollBarWidget("Input premix", patch, -99, 99, 0,
                  new AlesisDMProFXModel(oParser, 2, 3, 8),
                  new AlesisDMProFXSender(0, 4)), 0, 3, 1, 1, 4);

        addWidget(pnRev, new ScrollBarWidget("Input filter", patch, 0, 99, 0,
                  new AlesisDMProFXModel(oParser, 3, 3, 7),
                  new AlesisDMProFXSender(0, 5)), 2, 3, 1, 1, 5);

        addWidget(pnRev, new ScrollBarWidget("Decay", patch, 0, 99, 0,
                  new AlesisDMProFXModel(oParser, 4, 2, 7),
                  new AlesisDMProFXSender(0, 6)), 0, 4, 1, 1, 6);

        addWidget(pnRev, new ScrollBarWidget("Low decay", patch, 0, 99, 0,
                  new AlesisDMProFXModel(oParser, 5, 1, 7),
                  new AlesisDMProFXSender(0, 7)), 2, 4, 1, 1, 7);

        addWidget(pnRev, new ScrollBarWidget("High decay", patch, 0, 99, 0,
                  new AlesisDMProFXModel(oParser, 6, 0, 7),
                  new AlesisDMProFXSender(0, 8)), 0, 5, 1, 1, 8);

        addWidget(pnRev, new ScrollBarWidget("Density", patch, 0, 99, 0,
                  new AlesisDMProFXModel(oParser, 6, 7, 7),
                  new AlesisDMProFXSender(0, 9)), 2, 5, 1, 1, 9);


        //Overdrive
        addWidget(pnOverdrive, new ScrollBarWidget("Overdrive treshold", patch, 0, 99, 0,
                  new AlesisDMProFXModel(oParser, 7, 6, 7),
                  new AlesisDMProFXSender(1, 0)), 0, 0, 2, 1, 10);

        addWidget(pnOverdrive, new ScrollBarWidget("Overdrive brightness", patch, 0, 99, 0,
                  new AlesisDMProFXModel(oParser, 8, 5, 7),
                  new AlesisDMProFXSender(1, 1)), 2, 0, 2, 1, 11);


        //Delay
        addWidget(pnDelay, new ScrollBarWidget("Delay level", patch, 0, 99, 0,
                  new AlesisDMProFXModel(oParser, 9, 4, 7),
                  new AlesisDMProFXSender(2, 0)), 0, 0, 2, 1, 12);

        addWidget(pnDelay, new ScrollBarWidget("Delay input balance", patch, -99, 99, 0,
                  new AlesisDMProFXModel(oParser, 10, 3, 8),
                  new AlesisDMProFXSender(2, 1)), 2, 0, 2, 1, 13);

        addWidget(pnDelay, new ScrollBarWidget("Delay time 10ms", patch, 0, 79, 0,
                  new AlesisDMProFXModel(oParser, 11, 3, 7),
                  new AlesisDMProFXSender(2, 2)), 0, 1, 2, 1, 15);

        addWidget(pnDelay, new ScrollBarWidget("Delay time 1ms", patch, 0, 9, 0,
                  new AlesisDMProFXModel(oParser, 12, 2, 4),
                  new AlesisDMProFXSender(2, 3)), 2, 1, 2, 1, 16);

        addWidget(pnDelay, new ScrollBarWidget("Delay feedback", patch, 0, 99, 0,
                  new AlesisDMProFXModel(oParser, 12, 6, 7),
                  new AlesisDMProFXSender(2, 4)), 0, 2, 2, 1, 17);

        addWidget(pnDelay, new ScrollBarWidget("Delay out to reverb", patch, 0, 99, 0,
                  new AlesisDMProFXModel(oParser, 13, 5, 7),
                  new AlesisDMProFXSender(2, 5)), 2, 2, 2, 1, 18);


        //Pitch
        addWidget(pnPitch, new ScrollBarWidget("Pitch level", patch, 0, 99, 0,
                  new AlesisDMProFXModel(oParser, 14, 4, 7),
                  new AlesisDMProFXSender(3, 0)), 0, 0, 2, 1, 19);

        addWidget(pnPitch, new ScrollBarWidget("Pitch input balance", patch, -99, 99, 0,
                  new AlesisDMProFXModel(oParser, 15, 3, 8),
                  new AlesisDMProFXSender(3, 1)), 2, 0, 2, 1, 20);

        addWidget(pnPitch, new ComboBoxWidget("Pitch Type", patch,
                  new AlesisDMProFXModel(oParser, 16, 3, 3),
                  new AlesisDMProFXSender(3, 2),
                  new String[] {"Mono Chorus", "Stereo Chorus", "Mono Flanger", "Stereo Flanger","Resonator"}), 0, 1, 2, 1, 20);

        addWidget(pnPitch, new ComboBoxWidget("Pitch Waveform", patch,
                  new AlesisDMProFXModel(oParser, 16, 6, 1),
                  new AlesisDMProFXSender(3, 4),
                  new String [] {"Sine","Triangle"}), 2, 1, 2, 1, 21);

        addWidget(pnPitch, new ScrollBarWidget("Pitch speed", patch, 0, 99, 0,
                  new AlesisDMProFXModel(oParser, 16, 7, 7),
                  new AlesisDMProFXSender(3, 3)), 0, 2, 2, 1, 22);

        addWidget(pnPitch, new ScrollBarWidget("Pitch depth", patch, 0, 99, 0,
                  new AlesisDMProFXModel(oParser, 17, 6, 7),
                  new AlesisDMProFXSender(3, 5)), 2, 2, 2, 1, 23);

        addWidget(pnPitch, new ScrollBarWidget("Pitch feedback", patch, 0, 99, 0,
                  new AlesisDMProFXModel(oParser, 18, 5, 7),
                  new AlesisDMProFXSender(3, 6)), 0, 3, 2, 1, 24);

        addWidget(pnPitch, new ScrollBarWidget("Pitch output to reverb", patch, 0, 99, 0,
                  new AlesisDMProFXModel(oParser, 19, 4, 7),
                  new AlesisDMProFXSender(3, 7)), 2, 3, 2, 1, 25);

        //EQ
        addWidget(pnEQ, new ScrollBarWidget("EQ High freq", patch, 0, 5, 0,
                  new AlesisDMProFXModel(oParser, 20, 3, 3),
                  new AlesisDMProFXSender(4, 0)), 0, 0, 2, 1, 26);

        addWidget(pnEQ, new ScrollBarWidget("EQ high boost", patch, 0, 9, 0,
                  new AlesisDMProFXModel(oParser, 20, 6, 4),
                  new AlesisDMProFXSender(4, 1)), 2, 0, 2, 1, 27);

        addWidget(pnEQ, new ScrollBarWidget("EQ low freq", patch, 0, 7, 0,
                  new AlesisDMProFXModel(oParser, 21, 2, 3),
                  new AlesisDMProFXSender(4, 2)), 0, 1, 2, 1, 28);

        addWidget(pnEQ, new ScrollBarWidget("EQ low boost", patch, 0, 12, 0,
                  new AlesisDMProFXModel(oParser, 21, 5, 4),
                  new AlesisDMProFXSender(4, 3)), 2, 1, 2, 1, 29);

        pack();
    }



}

class AlesisDMProFXSender extends SysexSender
{

    private int m_nChannel; //Drum
    private int m_nFunc;    //Function
    private int m_nPage;    //Pagenumber

    public AlesisDMProFXSender(int nFunc, int nPage) {
        m_nChannel = 0; //Not used in effectedit
        m_nFunc = nFunc;
        m_nPage = nPage;
    }

    public byte [] generate (int value)
    {
        byte[] sysex = new byte[11];

        sysex[0] = (byte) 0xF0;
        sysex[1] = (byte) 0x00;
        sysex[2] = (byte) 0x00;
        sysex[3] = (byte) 0x0E;
        sysex[4] = (byte) 0x19;
        sysex[5] = (byte) 0x10;
        sysex[6] = (byte) (48 | (m_nFunc & 15));    //3 << 4 = Effectedit, low nibble = func
        sysex[7] = (byte) (((m_nFunc & 16) << 6) | (m_nPage & 15));  //MSB of func at bit 6, low nibble = page
        sysex[8] = (byte) (((m_nChannel << 1) & 127) | ((value >>> 7) & 1));   //Channel/drum, MSB of value
        sysex[9] = (byte) (value & 127);     //Reamining bits of value
        sysex[10] = (byte) 0xF7;

        return sysex;
    }

}


class AlesisDMProFXModel implements SysexWidget.IParamModel
{

    private AlesisDMProParser m_oParser;
    private int m_nByte;
    private int m_nBit;
    private int m_nBits;

     public AlesisDMProFXModel(AlesisDMProParser oParser, int nByte, int nBit, int nBits) {

         m_oParser = oParser;
         m_nByte = nByte;
         m_nBit = nBit;
         m_nBits = nBits;

     }

     public void set(int i) {
         m_oParser.setValue(m_nByte, m_nBit, m_nBits, i);
     }

     public int get() {
        return m_oParser.getValue(m_nByte, m_nBit, m_nBits);
     }
}

