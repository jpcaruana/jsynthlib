/*
 * Copyright 2005 Federico Ferri
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
// written by Federico Ferri
// @version $Id$

package org.jsynthlib.drivers.roland.mks7;

import java.awt.GridBagLayout;

import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.jsynthlib.core.ComboBoxWidget;
import org.jsynthlib.core.EnvelopeWidget;
import org.jsynthlib.core.ParamModel;
import org.jsynthlib.core.Patch;
import org.jsynthlib.core.PatchEditorFrame;
import org.jsynthlib.core.ScrollBarWidget;
import org.jsynthlib.core.SysexSender;


class MKS7ToneSingleEditor extends PatchEditorFrame
{
  public MKS7ToneSingleEditor(Patch patch)
  {
    super ("Roland MKS-7 Tone Single Editor", patch);

    JPanel leftPane = new JPanel();
    leftPane.setLayout(new GridBagLayout());
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    scrollPane.add(leftPane, gbc);
    JPanel rightPane = new JPanel();
    rightPane.setLayout(new GridBagLayout());
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    scrollPane.add(rightPane, gbc);

    JPanel namePane = new JPanel();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    leftPane.add(namePane, gbc);

    JPanel oscPane = new JPanel();
    oscPane.setLayout(new GridBagLayout());
    addWidget(oscPane, new ComboBoxWidget("Range", patch, new ParamModelBitExp(patch, 21, 0x07),
      new MKSToneSenderDirect(16, patch, 21), new String [] {"4'", "8'", "16'"}), 0, 0, 1, 1, 1);
    addWidget(oscPane, new ComboBoxWidget("Dynamic Affects", patch, new ParamModelBit(patch, 9, 0x60),
      new MKSToneSenderDirect(4, patch, 9), new String [] {"None", "VCA", "VCF", "VCA & VCF"}), 1, 0, 1, 1, 2);

    addWidget(oscPane, new ComboBoxWidget("Waveform", patch, new ParamModelBit(patch, 21, 24),
      new MKSToneSenderDirect(16, patch, 21), new String [] {"OFF", "PULSE", "SAW", "PULSE+SAW"}), 0, 1, 1, 1, 3);
    addWidget(oscPane, new ComboBoxWidget("Chorus", patch, new ParamModelBit(patch, 21, 0x20),
      new MKSToneSenderDirect(16, patch, 21), new String [] {"ON", "OFF"}), 1, 1, 1, 1, 4);
    addWidget(oscPane, new ComboBoxWidget("Noise (only for MELODY part)", patch, new ParamModelBit(patch, 22, 0x20),
      new MKSToneSenderDirect(17, patch, 22), new String [] {"ON", "OFF"}), 0, 2, 2, 1, 5);
    addWidget(oscPane, new ComboBoxWidget("PW Mod:", patch, new ParamModelBit(patch, 22, 0x01),
      new MKSToneSenderDirect(17, patch, 22), new String [] {"LFO", "Manual"}), 1, 3, 1, 1, 7);
    addWidget(oscPane, new ScrollBarWidget("Sub Level", patch, 0, 3, 0,
      new ParamModel(patch, 20), new MKSToneSender(15)), 0, 6, 3, 1, 8);
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    oscPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
      "DCO Settings", TitledBorder.CENTER, TitledBorder.CENTER));
    leftPane.add(oscPane, gbc);

    JPanel vcfPane = new JPanel();
    vcfPane.setLayout(new GridBagLayout());
    addWidget(vcfPane, new ScrollBarWidget("Cutoff Freq", patch, 0, 127, 0,
      new ParamModel(patch, 10), new MKSToneSender(5)), 0, 0, 3, 1, 9);
    addWidget(vcfPane, new ScrollBarWidget("Resonance", patch, 0, 127, 0,
      new ParamModel(patch, 11), new MKSToneSender(6)), 0, 1, 3, 1, 10);
    addWidget(vcfPane, new ScrollBarWidget("Env Amount", patch, 0, 127, 0,
      new ParamModel(patch, 12), new MKSToneSender(7)), 0, 2, 3, 1, 11);
    addWidget(vcfPane, new ScrollBarWidget("Key Follow", patch, 0, 127, 0,
      new ParamModel(patch, 14), new MKSToneSender(9)), 0, 3, 3, 1, 12);
    addWidget(vcfPane, new ComboBoxWidget("Hi-pass", patch, new ParamModelBit(patch, 22, 0x10),
      new MKSToneSenderDirect(17, patch, 22), new String [] {"ON", "OFF"}), 1, 4, 1, 1, 13);
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    vcfPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
      "VCF Settings", TitledBorder.CENTER, TitledBorder.CENTER));
    leftPane.add(vcfPane, gbc);

    JPanel lfoPane = new JPanel();
    lfoPane.setLayout(new GridBagLayout());
    gbc.weightx = 1;
    addWidget(lfoPane, new ScrollBarWidget("LFO Speed", patch, 0, 127, 0,
      new ParamModel(patch, 5), new MKSToneSender(0)), 0, 0, 7, 1, 14);
    addWidget(lfoPane, new ScrollBarWidget("LFO Delay", patch, 0, 127, 0,
      new ParamModel(patch, 6), new MKSToneSender(1)), 0, 1, 7, 1, 15);
    addWidget(lfoPane, new ScrollBarWidget("DCO Mod Depth", patch, 0, 127, 0,
      new ParamModel(patch, 7), new MKSToneSender(2)), 0, 2, 7, 1, 16);
    addWidget(lfoPane, new ScrollBarWidget("PWM Mod Depth", patch, 0, 127, 0,
      new ParamModel(patch, 8), new MKSToneSender(3)), 0, 3, 7, 1, 17);
    addWidget(lfoPane, new ScrollBarWidget("VCF Mod Depth", patch, 0, 127, 0,
      new ParamModel(patch, 13), new MKSToneSender(8)), 0, 4, 7, 1, 18);
    gbc.gridx = 0;
    gbc.gridy = 3;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    lfoPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
      "LFO Settings", TitledBorder.CENTER, TitledBorder.CENTER));
    rightPane.add(lfoPane, gbc);

    JPanel envPane = new JPanel();
    envPane.setLayout(new GridBagLayout());
    addWidget(envPane, new EnvelopeWidget(" ", patch, new EnvelopeWidget.Node [] {
      new EnvelopeWidget.Node(0, 0, null, 0, 0, null, 0, false, null, null, null, null),
      new EnvelopeWidget.Node(0, 127, new ParamModel(patch, 16), 127, 127,
        null, 10, false, new MKSToneSender(11), null, "ATK", null),
      new EnvelopeWidget.Node(0, 127, new ParamModel(patch, 17), 0, 127,
        new ParamModel(patch, 18), 10, false, new MKSToneSender(12), new MKSToneSender(13), "DEC", "SUS"),
      new EnvelopeWidget.Node(127,127,null,EnvelopeWidget.Node.SAME,0,null,0,false,null,null,null,null),
      new EnvelopeWidget.Node(0, 127, new ParamModel(patch, 19), 0, 0,
        null, 0, false, new MKSToneSender(14), null, "REL", null),
    }     ), 0, 0, 3, 5, 19);
    addWidget(envPane, new ComboBoxWidget("VCA:", patch, new ParamModelBit(patch, 22, 0x04),
      new MKSToneSenderDirect(17, patch, 22), new String [] {"ENV", "Gate"}), 0, 6, 1, 1, 20);
    addWidget(envPane, new ComboBoxWidget("ENV:", patch, new ParamModelBit(patch, 22, 0x02),
      new MKSToneSenderDirect(17, patch, 22), new String [] {"+", "-"}), 2, 6, 1, 1, 21);

    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    envPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
      "Envelope", TitledBorder.CENTER, TitledBorder.CENTER));
    rightPane.add(envPane, gbc);

    JPanel cmnPane =  new JPanel();
    cmnPane.setLayout(new GridBagLayout());
    addWidget(cmnPane, new ScrollBarWidget("VCA Level", patch, 0, 127, 0,
      new ParamModel(patch, 15), new MKSToneSender(10)), 0, 3, 3, 1, 22);
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    cmnPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
      "Common", TitledBorder.CENTER, TitledBorder.CENTER));
    rightPane.add(cmnPane, gbc);

    pack();
  }
}

class MKSToneSender extends SysexSender
{
  byte b[] = { (byte)0xF0, (byte)0x41, (byte)0x32, (byte)0x00, (byte)0x00,
               (byte)0x00, (byte)0xF7 };
  
  public MKSToneSender(int param)
  {
    b[4] = (byte)param;
  }
  public byte [] generate (int value)
  {
    b[3] = (byte)(channel - 1);
    b[5] = (byte)value;
    return b;
  }
}


// this sender send the sysex taking the value from (Patch)p.sysex[offset]
// instead of generating itself
class MKSToneSenderDirect extends SysexSender
{
  byte b[] = { (byte)0xF0, (byte)0x41, (byte)0x32, (byte)0x00, (byte)0x00,
               (byte)0x00, (byte)0xF7 };
  protected Patch patch;
  protected int patch_off;
  
  public MKSToneSenderDirect(int param, Patch p, int offset)
  {
	  this.patch = p;
	  this.patch_off = offset;
	  
    b[4] = (byte)param;
  }
  public byte [] generate (int value)
  {
    b[3] = (byte)(channel - 1);
    b[5] = patch.sysex[patch_off];
    return b;
  }
}

class ParamModelBit extends ParamModel {
    protected byte mask;
    protected int power = 0;
    
    ParamModelBit(Patch p, int offset, int mask) {
        super(p, offset);
        this.mask = (byte)mask;
        
        int bitPos = 1;
        int rem = 0;
        while (rem == 0) { 
            bitPos *= 2;
            power += 1;
            rem = mask % bitPos;
        }
        power -= 1;
    }
    
    public void set(int i) {
        patch.sysex[ofs] = (byte)((patch.sysex[ofs] & (~mask)) | ((i << power) & mask));
    }
    
    public int get() {
        return ((patch.sysex[ofs] & mask) >> power);
    }
}

// exponential version of the above (0=>1 1=>2 2=>4 3=>8 and so on..)
class ParamModelBitExp extends ParamModel {
    protected byte mask;
    protected int power = 0;

    ParamModelBitExp(Patch p, int offset, int mask) {
        super(p, offset);
        this.mask = (byte)mask;
        
        int bitPos = 1;
        int rem = 0;
        while (rem == 0) { 
            bitPos *= 2;
            power += 1;
            rem = mask % bitPos;
        }
        power -= 1;
    }
    
    public void set(int i) {
        patch.sysex[ofs] = (byte)((patch.sysex[ofs] & (~mask)) | (((1 << i) << power) & mask));
    }
    
    public int get() {
        int r = ((patch.sysex[ofs] & mask) >> power);
	return ((r<=1)?0: (r<=2)?1: (r<=4)?2: (r<=8)?3: (r<=16)?4: (r<=32)?5: (r<=64)?6: 7);
    }
}


