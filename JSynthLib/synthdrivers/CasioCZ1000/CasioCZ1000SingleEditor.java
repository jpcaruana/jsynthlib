/*
 * Copyright 2005 Bill Zwicky
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
package synthdrivers.CasioCZ1000;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import core.CheckBoxWidget;
import core.EnvelopeWidget;
import core.IPatchDriver;
import core.ParamModel;
import core.Patch;
import core.PatchEditorFrame;
import core.ScrollBarLookupWidget;
import core.ScrollBarWidget;
import core.SysexWidget;


/**
 * Editor for a patch from a Casio CZ-101/1000.
 * Patch must be 264 bytes long, not the 263-byte version from the synth.
 * Detune Fine goes 0..63, while synth reads 0..60.
 * <P>
 * Released under GPL and all that.
 * 
 * @author Bill Zwicky
 */
public class CasioCZ1000SingleEditor extends PatchEditorFrame {
    /** Offset to first data byte in sysex. */
    public static final int TONE_DATA = 7;
    /** TRUE to edit all bits in sysex, FALSE to stick to official limits. */
    public static final boolean HACK_MODE = false;

    
    public CasioCZ1000SingleEditor(Patch patch) {
        super("Casio CZ-101/1000 Patch Editor", patch);
		gbc.weightx=1;
        
        JPanel miscPane=new JPanel();
        miscPane.setLayout(new GridBagLayout());
        miscPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Parameters",TitledBorder.LEFT,TitledBorder.CENTER));

        int row=-1;
        addWidget(miscPane,new ScrollBarLookupWidget("Line Select",patch,0,3,80,new LineModel(patch),null, new String[] {"1","2","1+1'","1+2'"}),0,++row,7,1,0);

		addWidget(miscPane,new CheckBoxWidget("Ring",  patch,new BitModel(patch,15,5),null),0,++row,1,1,0);
		if(HACK_MODE) {
	        addWidget(miscPane,new CheckBoxWidget("Noise1",patch,new BitModel(patch,15,4),null),1,row,1,1,0);
	        addWidget(miscPane,new CheckBoxWidget("Noise2",patch,new BitModel(patch,15,3),null),2,row,1,1,0);
		}
		else {
		    //TODO switch both or neither together
	        addWidget(miscPane,new CheckBoxWidget("Noise1",patch,new BitModel(patch,15,4),null),1,row,1,1,0);
	        addWidget(miscPane,new CheckBoxWidget("Noise2",patch,new BitModel(patch,15,3),null),2,row,1,1,0);
		}

		if(HACK_MODE)
		    addWidget(miscPane,new ScrollBarWidget("Detune Step",patch,-127,127,0,80,new DetuneStepModel(patch),null),0,++row,7,1,0);
		else
		    addWidget(miscPane,new ScrollBarWidget("Detune Step",patch,-47,47,0,80,new DetuneStepModel(patch),null),0,++row,7,1,0);
        addWidget(miscPane,new ScrollBarWidget("Detune Fine",patch,  0,63,0,80,new DetuneFineModel(patch),null),0,++row,7,1,0);
        
        // Octave val==3 is same as 1 (+1 octave)
        addWidget(miscPane,new ScrollBarWidget("Octave",patch,-1,1,0,80,new OctaveModel(patch),null),0,++row,7,1,0);
        add(miscPane, getVibratoGUI(patch),0,++row,7,4);

        // Note if either wf is >5, the other must be <=5.

		JPanel dco1Panel = new JPanel();
		dco1Panel.setLayout(new GridBagLayout());
		add(dco1Panel,getEnvelopeGUI(patch,108),0,0,1,1);

		JPanel dcw1Panel = new JPanel();
		dcw1Panel.setLayout(new GridBagLayout());
		if(HACK_MODE)
		    addWidget(dcw1Panel,new ScrollBarWidget("Key Follow",patch,0,0xFFFF,0,new WordFieldModel(patch,36,0xFFFF),null),0,0,7,1,0);
		else
		    addWidget(dcw1Panel,new ScrollBarWidget("Key Follow",patch,0,9,0,new KeyFollowWModel(patch,36),null),0,0,7,1,0);
		add(dcw1Panel,getEnvelopeGUI(patch,74),0,1,1,1);
		
		JPanel dca1Panel = new JPanel();
		dca1Panel.setLayout(new GridBagLayout());
		if(HACK_MODE)
		    addWidget(dca1Panel,new ScrollBarWidget("Key Follow",patch,0,0xFFFF,0,new WordFieldModel(patch,32,0xFFFF),null),0,0,7,1,0);
		else
		    addWidget(dca1Panel,new ScrollBarWidget("Key Follow",patch,0,9,0,new KeyFollowAModel(patch,32),null),0,0,7,1,0);
		add(dca1Panel,getEnvelopeGUI(patch,40),0,1,1,1);

		
		JPanel dco2Panel = new JPanel();
		dco2Panel.setLayout(new GridBagLayout());
		add(dco2Panel,getEnvelopeGUI(patch,222),0,0,1,1);

		JPanel dcw2Panel = new JPanel();
		dcw2Panel.setLayout(new GridBagLayout());
		if(HACK_MODE)
		    addWidget(dcw2Panel,new ScrollBarWidget("Key Follow",patch,0,0xFFFF,0,new WordFieldModel(patch,150,0xFFFF),null),0,0,7,1,0);
		else
		    addWidget(dcw2Panel,new ScrollBarWidget("Key Follow",patch,0,9,0,new KeyFollowWModel(patch,150),null),0,0,7,1,0);
		add(dcw2Panel,getEnvelopeGUI(patch,188),0,1,1,1);
		
		JPanel dca2Panel = new JPanel();
		dca2Panel.setLayout(new GridBagLayout());
		if(HACK_MODE)
		    addWidget(dca2Panel,new ScrollBarWidget("Key Follow",patch,0,0xFFFF,0,new WordFieldModel(patch,146,0xFFFF),null),0,0,7,1,0);
		else
		    addWidget(dca2Panel,new ScrollBarWidget("Key Follow",patch,0,9,0,new KeyFollowAModel(patch,146),null),0,0,7,1,0);
		add(dca2Panel,getEnvelopeGUI(patch,154),0,1,1,1);

		
		JTabbedPane oscPane=new JTabbedPane();
		oscPane.addTab("1 WF", getWaveformGUI(patch,28));
		oscPane.addTab("1 Pitch",dco1Panel);
		oscPane.addTab("1 Wave",dcw1Panel);
		oscPane.addTab("1 Amp",dca1Panel);
		oscPane.addTab("2 WF", getWaveformGUI(patch,142));
		oscPane.addTab("2 Pitch",dco2Panel);
		oscPane.addTab("2 Wave",dcw2Panel);
		oscPane.addTab("2 Amp",dca2Panel);

		gbc.gridy=0;
		gbc.gridx=gbc.RELATIVE;
        gbc.weightx = 1;
        scrollPane.add(miscPane,gbc);
        gbc.weightx = 1;
        scrollPane.add(oscPane,gbc);
    }
    
    
    public void add(JPanel parent, Component widget, 
            int gridx, int gridy,
            int gridwidth, int gridheight) {
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        gbc.gridwidth = gridwidth;
        gbc.gridheight = gridheight;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        parent.add(widget, gbc);
    }


    protected JPanel getVibratoGUI(Patch patch) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Vibrato",TitledBorder.LEFT,TitledBorder.CENTER));
        int row=0, width=40;
        /*
        --disabled .. weird combos do nothing interesting.
        addWidget(panel,new CheckBoxWidget("Triangle",patch,new BitModel(patch,8,3),null),0,row,1,1,0);
        addWidget(panel,new CheckBoxWidget("Saw Up",patch,new BitModel(patch,8,2),null),1,row,1,1,0);
        addWidget(panel,new CheckBoxWidget("Saw Down",patch,new BitModel(patch,8,5),null),2,row,1,1,0);
        addWidget(panel,new CheckBoxWidget("Square",patch,new BitModel(patch,8,1),null),3,row,1,1,0);
        */
        addWidget(panel,new ScrollBarLookupWidget("Wave",patch,0,4,width,
                new VibratoWaveModel(patch),null,
                new String[] { "OFF", "1 Tri", "2 Up", "3 Dn", "4 Pul" }),
                0,row,7,1,0);

        if(HACK_MODE) {
            // Rate is limited by the computed 16-bit value
            addWidget(panel,new ScrollBarWidget("Delay",patch,0,255,0,width,new VibratoDelayModel(patch),null),0,++row,7,1,0);
            addWidget(panel,new ScrollBarWidget("Rate",patch,0,142,0,width,new VibratoRateModel(patch),null),0,++row,7,1,0);
            addWidget(panel,new ScrollBarWidget("Depth",patch,0,255,0,width,new VibratoDepthModel(patch),null),0,++row,7,1,0);
        }
        else {
            // Officially limited to 0-99.
            addWidget(panel,new ScrollBarWidget("Delay",patch,0,99,0,width,new VibratoDelayModel(patch),null),0,++row,7,1,0);
            addWidget(panel,new ScrollBarWidget("Rate",patch,0,99,0,width,new VibratoRateModel(patch),null),0,++row,7,1,0);
            addWidget(panel,new ScrollBarWidget("Depth",patch,0,99,0,width,new VibratoDepthModel(patch),null),0,++row,7,1,0);
        }
        return panel;
    }
    
    protected JPanel getWaveformGUI(Patch patch, int offset) {
        // Hack Mode:
        // Full access to all wave values, including undefined ones.
        //   W1=6 or W2=6 uses resonance wave R
        //TODO non-hack-mode == waveforms 0 1 2 4 5 6, but only one osc can use resonance (6); res 0 1 2 3 only.
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
        int row=-1, width=40;
        // Advanced wave tricks named and decoded by Sealed.
        // http://homepage.mac.com/synth_seal/html/cz1a.html
        String[] wfLabels = new String[] {
                "1 Saw",  "2 Pul",  "3 Tri",   "Null",
                "4 TrSi", "5 SiPu", "SinSy", "NPul"
                // "Sine Sync" and "Narrow Pulse"
        };
        String[] resLabels = new String[] {
                // "Window Functions"
                "OFF",  "6 Saw", "7 Tri", "8 Trap",
                "SawPu", "Spike"
                // "Trapezoid", "Saw-Pulse", "Spike"
                // last two are also spike
        };
        // Labeled sliders
        addWidget(panel,new ScrollBarLookupWidget("Waveform 1",patch,0,wfLabels.length-1,80,new WordFieldModel(patch,TONE_DATA+offset,0xE000),
                null,wfLabels),0,++row,7,1,0);
        addWidget(panel,new ScrollBarLookupWidget("Waveform 2",patch,0,wfLabels.length-1,80,new WordFieldModel(patch,TONE_DATA+offset,0x1C00),
                null,wfLabels),0,++row,7,1,0);
		addWidget(panel,new CheckBoxWidget("Enable WF 2",patch,new BitModel(patch,offset,1),null),0,++row,1,1,0);
        addWidget(panel,new ScrollBarLookupWidget("Window",patch,0,resLabels.length-1,80,new WordFieldModel(patch,TONE_DATA+offset,0x01C0),
                null,resLabels),0,++row,7,1,0);
        return panel;
    }

    /**
     * "Hack mode": sliders and boxes give access to full parameter range.
     * 
     * @param patch work area
     * @param firstOffset offset to "final stage" param
     * @return
     */
    protected JPanel getEnvelopeGUI(Patch patch, int firstOffset) {
        //TODO non-hack-mode == only one stage can be sustain
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Envelope",TitledBorder.LEFT,TitledBorder.CENTER));
        EnvelopeModel model = new EnvelopeModel(patch, firstOffset);
        int row=-1, width=40, maxval;
        if(HACK_MODE)
            maxval = 127;
        else
            maxval = 99;
        addWidget(panel,new ScrollBarWidget("1 Rate",patch,0,maxval,0,width,model.getRateModel(0),null),0,++row,1,1,0);
        addWidget(panel,new ScrollBarWidget("Level",patch,0,maxval,0,width,model.getLevelModel(0),null),1,row,1,1,0);
        addWidget(panel,new CheckBoxWidget("Sustain",patch,model.getSustainModel2(0),null),2,row,1,1,0);
        addWidget(panel,new ScrollBarWidget("2 Rate",patch,0,maxval,0,width,model.getRateModel(1),null),0,++row,1,1,0);
        addWidget(panel,new ScrollBarWidget("Level",patch,0,maxval,0,width,model.getLevelModel(1),null),1,row,1,1,0);
        addWidget(panel,new CheckBoxWidget("Sustain",patch,model.getSustainModel2(1),null),2,row,1,1,0);
        addWidget(panel,new ScrollBarWidget("3 Rate",patch,0,maxval,0,width,model.getRateModel(2),null),0,++row,1,1,0);
        addWidget(panel,new ScrollBarWidget("Level",patch,0,maxval,0,width,model.getLevelModel(2),null),1,row,1,1,0);
        addWidget(panel,new CheckBoxWidget("Sustain",patch,model.getSustainModel2(2),null),2,row,1,1,0);
        addWidget(panel,new ScrollBarWidget("4 Rate",patch,0,maxval,0,width,model.getRateModel(3),null),0,++row,1,1,0);
        addWidget(panel,new ScrollBarWidget("Level",patch,0,maxval,0,width,model.getLevelModel(3),null),1,row,1,1,0);
        addWidget(panel,new CheckBoxWidget("Sustain",patch,model.getSustainModel2(3),null),2,row,1,1,0);
        addWidget(panel,new ScrollBarWidget("5 Rate",patch,0,maxval,0,width,model.getRateModel(4),null),0,++row,1,1,0);
        addWidget(panel,new ScrollBarWidget("Level",patch,0,maxval,0,width,model.getLevelModel(4),null),1,row,1,1,0);
        addWidget(panel,new CheckBoxWidget("Sustain",patch,model.getSustainModel2(4),null),2,row,1,1,0);
        addWidget(panel,new ScrollBarWidget("6 Rate",patch,0,maxval,0,width,model.getRateModel(5),null),0,++row,1,1,0);
        addWidget(panel,new ScrollBarWidget("Level",patch,0,maxval,0,width,model.getLevelModel(5),null),1,row,1,1,0);
        addWidget(panel,new CheckBoxWidget("Sustain",patch,model.getSustainModel2(5),null),2,row,1,1,0);
        addWidget(panel,new ScrollBarWidget("7 Rate",patch,0,maxval,0,width,model.getRateModel(6),null),0,++row,1,1,0);
        addWidget(panel,new ScrollBarWidget("Level",patch,0,maxval,0,width,model.getLevelModel(6),null),1,row,1,1,0);
        addWidget(panel,new CheckBoxWidget("Sustain",patch,model.getSustainModel2(6),null),2,row,1,1,0);
        addWidget(panel,new ScrollBarWidget("8 Rate",patch,0,maxval,0,width,model.getRateModel(7),null),0,++row,1,1,0);
        addWidget(panel,new ScrollBarWidget("Level",patch,0,maxval,0,width,model.getLevelModel(7),null),1,row,1,1,0);
        addWidget(panel,new CheckBoxWidget("Sustain",patch,model.getSustainModel2(7),null),2,row,1,1,0);
        addWidget(panel,new ScrollBarWidget("Final Stage", patch, 0, 7, 1, model.getEndStageModel(), null), 0, ++row, 1, 1, 0);
        return panel;
    }

    
    /**
     * "Normal mode": envelope control give access to params as seen on LCD.
     * 
     * @param patch work area
     * @param firstOffset offset to "final stage" param
     * @return
     */
    protected JPanel getEnvelopeGUI2(Patch patch, int firstOffset) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
//        panel.setMinimumSize(new Dimension(100,100));
        EnvelopeModel model = new EnvelopeModel(patch, firstOffset);
        // CZ can have 1-8 stages, any can be sustain.
        EnvelopeWidget.Node[] nodes = new EnvelopeWidget.Node[] {
                new EnvelopeWidget.Node(0, 0, null, 0, 0, null, 0, false, null, null, null, null),
                new EnvelopeWidget.Node(0, 127, model.getRateModel(0), 0, 127, model.getLevelModel(0), 0, false, new NullSender(), new NullSender(), "1 Rate", "   Level"),
                new EnvelopeWidget.Node(0, 127, model.getRateModel(1), 0, 127, model.getLevelModel(1), 0, false, new NullSender(), new NullSender(), "2 Rate", "   Level"),
                new EnvelopeWidget.Node(0, 127, model.getRateModel(2), 0, 127, model.getLevelModel(2), 0, false, new NullSender(), new NullSender(), "3 Rate", "   Level"),
                new EnvelopeWidget.Node(0, 127, model.getRateModel(3), 0, 127, model.getLevelModel(3), 0, false, new NullSender(), new NullSender(), "4 Rate", "   Level"),
                new EnvelopeWidget.Node(0, 127, model.getRateModel(4), 0, 127, model.getLevelModel(4), 0, false, new NullSender(), new NullSender(), "5 Rate", "   Level"),
                new EnvelopeWidget.Node(0, 127, model.getRateModel(5), 0, 127, model.getLevelModel(5), 0, false, new NullSender(), new NullSender(), "6 Rate", "   Level"),
                new EnvelopeWidget.Node(0, 127, model.getRateModel(6), 0, 127, model.getLevelModel(6), 0, false, new NullSender(), new NullSender(), "7 Rate", "   Level"),
                new EnvelopeWidget.Node(0, 127, model.getRateModel(7), 0, 127, model.getLevelModel(7), 0, false, new NullSender(), new NullSender(), "8 Rate", "   Level"),
        };
        SysexWidget wid = new EnvelopeWidget("Envelope", patch, nodes);
//        wid.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Envelope",TitledBorder.LEFT,TitledBorder.CENTER));
//        wid.setMinimumSize(new Dimension(600,300));
        addWidget(panel, wid, 1, 1, 7, 5, 0);
//        addWidget(panel, new ScrollBarWidget("Sustain Stage", patch, 0, 7, 1, model.getSustainModel(), null), 0, 6, 3, 1, 0);
        return panel;
    }
    
    
    
    
    
    class NullModel implements SysexWidget.IParamModel {
        int currValue;
        /** Constructs a NullModel.*/
        NullModel() {} 
        /** Null method for NullModel.*/
        public void set(int value) {currValue=value;}
        /** Null method for NullModel.*/
        public int get() {return currValue;}
    }

    /** 
     * Unmangle CZ data.  Nybbles in a byte are swapped, and bytes in a word 
     * are *sometimes* swapped, then each nybble is stored in its own byte.  
     * So 0xABCD is stored as 0D 0C 0B 0A.
     */
    class CZModel extends ParamModel {
        public CZModel(Patch p, int offset) {
            super(p,offset);
        }
        public int getByte(int offset) {
            int b = patch.sysex[offset] + (patch.sysex[offset+1]<<4);
            return b;
        }
        public void setByte(int offset, int value) {
            patch.sysex[offset]   = (byte)((value)&0x0F);
            patch.sysex[offset+1] = (byte)((value>>4)&0x0F);
        }
        /** LSB comes first */
        public int getWordL(int offset) {
            int w = patch.sysex[offset] + (patch.sysex[offset+1]<<4)
                  + (patch.sysex[offset+2]<<8) + (patch.sysex[offset+3]<<12);
            return w;
        }
        /** LSB comes first */
        public void setWordL(int offset, int value) {
            patch.sysex[offset]   = (byte)((value)&0x0F);
            patch.sysex[offset+1] = (byte)((value>>4)&0x0F);
            patch.sysex[offset+2] = (byte)((value>>8)&0x0F);
            patch.sysex[offset+3] = (byte)((value>>12)&0x0F);
        }
        /** MSB comes first */
        public int getWordH(int offset) {
            int w = patch.sysex[offset+2] + (patch.sysex[offset+3]<<4)
                  + (patch.sysex[offset]<<8) + (patch.sysex[offset+1]<<12);
            return w;
        }
        /** MSB comes first */
        public void setWordH(int offset, int value) {
            patch.sysex[offset+2] = (byte)((value)&0x0F);
            patch.sysex[offset+3] = (byte)((value>>4)&0x0F);
            patch.sysex[offset]   = (byte)((value>>8)&0x0F);
            patch.sysex[offset+1] = (byte)((value>>12)&0x0F);
        }
    }
    
    /** generic .. offset = sysex offset */
    class WordFieldModel extends CZModel {
        int mask, shift;
        WordFieldModel(Patch patch, int offset, int mask) {
            super(patch, offset);
            if(mask==0) throw new IllegalArgumentException("Mask cannot be zero.");
            this.mask = mask;
            int i, m;
            for(i=0, m=1; i<16; i++, m<<=1)
                if((mask & m) != 0)
                    break;
            shift = i;
        }
        public void set(int value) {
            setWordH(ofs, (getWordH(ofs) & ~mask) | ((value << shift) & mask));
        }
        public int get() {
            int value = (getWordH(ofs)&mask)>>shift;
            return value;
        }
    }

    class LineModel extends CZModel {
        public LineModel(Patch p) {
            super(p,TONE_DATA+0);
        }
        public void set(int i) {
            setByte(ofs, (getByte(ofs) & 0xFC) | (i & 3));
        }
        public int get() {
            return getByte(ofs) & 3;
        }
    }

    class OctaveModel extends CZModel {
        public OctaveModel(Patch p) {
            super(p, TONE_DATA);
        }
        public void set(int i) {
            if(i==-1) i=2;
            setByte(ofs, (getByte(ofs) & 0xF3) | ((i<<2) & 0xC));
        }
        public int get() {
            int i = (getByte(ofs) & 0x0C) >> 2;
            if(i==2) i=-1;
            return i;
        }
    }    
    
    class DetuneStepModel extends CZModel {
        public DetuneStepModel(Patch p) {
            super(p, TONE_DATA+2);
        }
        public void set(int value) {
            setByte(ofs, (value<0 ? 1 : 0));
            setByte(ofs+4, Math.abs(value) & 0x3F);
        }
        public int get() {
            int value = getByte(ofs+4);
            if((getByte(ofs)&1) != 0)
                value = -value;
            return value;
        }
    }    
    
    class DetuneFineModel extends CZModel {
        public DetuneFineModel(Patch p) {
            super(p, TONE_DATA+4);
        }
        public void set(int value) {
            setByte(ofs, value<<2);
        }
        public int get() {
            return getByte(ofs) >> 2;
        }
    }
    
    class VibratoWaveModel extends CZModel {
        final int[] VALS = { 0, 0x08, 0x04, 0x20, 0x02 }; 
        public VibratoWaveModel(Patch p) {
            super(p, TONE_DATA+8);
        }
        public void set(int value) {
            setByte(ofs, VALS[value]);
        }
        public int get() {
            int value = getByte(ofs);
            for(int i=1; i<VALS.length; i++)
                if((value & VALS[i]) != 0)
                    return i;
            return 0;
        }
    }

    /**
     * Vibrato params have this weird multi-segment curve, and are
     * stored in three bytes:  first byte is the raw value.  Second
     * and third are the low and high bytes from the appropriate point
     * on the curve.
     */
    abstract class VibratoModel extends CZModel {
        int[] stages;
        int[] multys;
        int[] biases;
        public VibratoModel(Patch p, int offset) {
            super(p, TONE_DATA+offset);
        }
        public void set(int i) {
            // save raw value
            setByte(ofs, i);
            // translate value
            for(int s=1; s<stages.length; s++) {
                if(i < stages[s]) {
                    i = (i - stages[s-1]) * multys[s-1] + biases[s-1];
                    break;
                }
            }
            setWordL(ofs+2, i);
        }
        public int get() {
            return getByte(ofs);
        }
    }
    
    class VibratoDelayModel extends VibratoModel {
        int[] STAGES = { 0,   32,   48,    64,    80,    96,    999 };
        int[] MULTYS = {    1,    2,    4,      8,    16,    32 };
        int[] BIASES = {    0,   33,   67,    135,   271,   543 };
        public VibratoDelayModel(Patch p) {
            super(p, 10);
            stages = STAGES;
            multys = MULTYS;
            biases = BIASES;
        }
    }    

    class VibratoRateModel extends VibratoModel {
        int[] STAGES = { 0,   32,      48,      64,       80,       96,       999 };
        int[] MULTYS = {   32,      64,     128,      256,      512,     1024 };
        int[] BIASES = {    0,   0x460,   0x8E0,   0x11E0,   0x23E0,   0x47E0 };
        public VibratoRateModel(Patch p) {
            super(p, 16);
            stages = STAGES;
            multys = MULTYS;
            biases = BIASES;
        }
    }    

    class VibratoDepthModel extends VibratoModel {
        int[] STAGES = { 0,   32,     48,     64,     80,      96,      99,      999 };
        int[] MULTYS = {    1,      2,      4,      8,      16,      32,      64 };
        int[] BIASES = {    1,   0x23,   0x47,   0x8F,   0x11F,   0x23F,   0x300 };
        public VibratoDepthModel(Patch p) {
            super(p, 22);
            stages = STAGES;
            multys = MULTYS;
            biases = BIASES;
        }
    }    

    abstract class KeyFollowModel extends CZModel {
        int[] map;
        public KeyFollowModel(Patch p, int offset) {
            super(p, TONE_DATA+offset);
        }
        public void set(int value) {
            if(value<0) value=0;
            if(value>9) value=9;
            value = map[value];
            setWordH(ofs, value);
        }
        public int get() {
            int value = getWordH(ofs);
            for(int i=0; i<map.length-1; i++) {
                if(value >= map[i] && value < map[i+1])
                    return i;
            }
            return map.length-1;
        }
    }

    class KeyFollowAModel extends KeyFollowModel {
        int[] MAP = { 0, 0x108, 0x211, 0x31A, 0x424, 0x52F, 0x63A, 0x745, 0x852, 0x95F };
        public KeyFollowAModel(Patch p, int offset) {
            super(p,offset);
            map = MAP;
        }
    }
    
    class KeyFollowWModel extends KeyFollowModel {
        int[] MAP = { 0, 0x11F, 0x22C, 0x339, 0x446, 0x553, 0x660, 0x76E, 0x892, 0x9FF };
        public KeyFollowWModel(Patch p, int offset) {
            super(p,offset);
            map = MAP;
        }
    }

    /**
     * Master controller for envelope params.  Create one of these for an 
     * envelope, then use get*Model to get the objects to feed to the widgets.
     */
    class EnvelopeModel extends CZModel {
        /**
         * @param patch
         * @param firstOffset Offset of 'end step' param within tone data.
         *    Envelope data is assumed to follow.
         *    Block is 17 bytes total.
         */
        public EnvelopeModel(Patch patch, int firstOffset) {
            super(patch, TONE_DATA+firstOffset);
        }
        /**
         * Mark one stage as sustain.
         * @param stage 0..7
         */
        public void setSustainStage(int stage) {
            if(stage<0 || stage>7)  throw new IllegalArgumentException("stage="+stage+"; must be 0..7");
            for(int i=0; i<8; i++) {
                int o = ofs+4+4*i;                
                setByte(o, getByte(o) & 0x7F);
            }
            int o = ofs+4+4*stage;                
            setByte(o, getByte(o) | 0x80);
        }
        public int getSustainStage() {
            for (int i = 0; i < 8; i++) {
                int o = ofs+4+4*i;                
                if ((getByte(o) & 0x80) != 0)
                    return i;
            }
            // default to last stage
            return 7;
        }
        /**
         * Mark a stage as sustain.
         * @param stage 0..7
         * @param value 0 or non-0
         */
        public void setSustainStage(int stage, int value) {
            if(stage<0 || stage>7)  throw new IllegalArgumentException("stage="+stage+"; must be 0..7");
            int o = ofs+4+4*stage;                
            if(value == 0)
                setByte(o, getByte(o) & 0x7F);
            else
                setByte(o, getByte(o) | 0x80);
        }
        public int getSustainStage(int stage) {
            int o = ofs+4+4*stage;                
            return (getByte(o) & 0x80) != 0 ? 1 : 0;
        }
        public void setEndStage(int stage) {
            setByte(ofs, stage);
        }
        public int getEndStage() {
            return getByte(ofs);
        }
        /**
         * @param stage 0..7
         * @param value angle 0..127, where 127 = 90degrees = no delay.
         */
        public void setRate(int stage, int value) {
            if(stage<0 || stage>7)  throw new IllegalArgumentException("stage="+stage+"; must be 0..7");
            int o = ofs+2+4*stage;
            value &= 0x7F;
            boolean down = (stage>0) && (getRate(stage-1) > value);
            setByte( o, (down?0x80:0) + (value));
        }
        public int getRate(int stage) {
            if(stage<0 || stage>7)  throw new IllegalArgumentException("stage="+stage+"; must be 0..7");
            int o = ofs+2+4*stage;
            int value = (getByte(o)&0x7F);
            return value;
        }
        public void setLevel(int stage, int value) {
            if(stage<0 || stage>7)  throw new IllegalArgumentException("stage="+stage+"; must be 0..7");
            int o = ofs+4+4*stage;
            setByte(o, (getByte(o)&0x80) | (value&0x7F));
        }
        public int getLevel(int stage) {
            if(stage<0 || stage>7)  throw new IllegalArgumentException("stage="+stage+"; must be 0..7");
            int o = ofs+4+4*stage;
            int value = (getByte(o) & 0x7F);
            return value;
        }
        
        public SysexWidget.IParamModel getSustainModel() {
            return new SysexWidget.IParamModel() {
                public void set(int value) {
                    setSustainStage(value);
                }
                public int get() {
                    return getSustainStage();
                }
            };
        }
        public SysexWidget.IParamModel getSustainModel2(final int stage) {
            return new SysexWidget.IParamModel() {
                public void set(int value) {
                    setSustainStage(stage, value);
                }
                public int get() {
                    return getSustainStage(stage);
                }
            };
        }
        public SysexWidget.IParamModel getEndStageModel() {
            return new SysexWidget.IParamModel() {
                public void set(int value) {
                    setEndStage(value);
                }
                public int get() {
                    return getEndStage();
                }
            };
        }
        public SysexWidget.IParamModel getRateModel(final int stage) {
            return new SysexWidget.IParamModel() {
                public void set(int value) {
                    if(!HACK_MODE)
                        value = 119 * value / 99;
                    setRate(stage, value);
                }
                public int get() {
                    int value = getRate(stage);
                    if(!HACK_MODE) {
                        if(value == 0x77)
                            value = 99;
                        else if(value>0)
                            value = 99 * value / 119;
                    }
                    return value;
                }
            };
        }
        public SysexWidget.IParamModel getLevelModel(final int stage) {
            return new SysexWidget.IParamModel() {
                public void set(int value) {
                    if(!HACK_MODE) {
                        if(value == 1)
                            value = 0x10;
                        else if(value > 0)
                            value += 0x1C;
                    }
                    setLevel(stage, value);
                }
                public int get() {
                    int value = getLevel(stage);
                    if(!HACK_MODE) {
                        if(value < 0x10)
                            value = 0;
                        else if (value < 0x1E)
                            value = 1;
                        else
                            value -= 0x1C;
                        if(value > 99)
                            value = 99;
                    }
                    return value;
                }
            };
        }
    }
    
    
    
    class BitModel extends CZModel {
        int pos, neg;
        /**
         * @param p patch
         * @param offset 0..end of tone data, not sysex
         * @param bit 0..7, from right (LSB)
         */
        public BitModel(Patch p, int offset, int bit) {
            super(p, TONE_DATA+offset);
            pos = 1 << bit;
            neg = ~pos;
        }
        public void set(int i) {
            if(i == 0)
                setByte(ofs, getByte(ofs) & neg);
            else
                setByte(ofs, getByte(ofs) | pos);
        }
        public int get() {
            return (getByte(ofs) & pos) != 0 ? 1 : 0;
        }
    }
    
    class NullSender implements SysexWidget.ISender {
        public void send(IPatchDriver driver, int value) { }
    }

}

/*
Rough structure of patch dump from synth.
Header is 6 bytes from synth, 7 bytes to synth.
1st column is offset from start of "tone data" area.

sysex:  f0 44 00 00 70 30
00 PFLAG   00 00 
02 PDS     00 00 
04 PDL,PDH 00 00 00 00
08 PVK     08 00
10 PVDLD,V 00 00 00 00 00 00 
16 PVSD,V  00 00 00 02 00 00 
22 PVDD,V  00 00 01 00 00 00 
28 MFW     00 00 00 00 
32 MAMD,V  00 00 00 00 
36 MWMD,V  00 00 00 00 
40 PMAL    07 00
42 PMA     07 07 0f 0f 0c 0b 00 00 0c 03 00 00 0c 03 00 00
58         0c 03 00 00 0c 03 00 00 0c 03 00 00 0c 0b 00 00
74 PMWL    07 00 
76 PMW     0f 07 0f 0f 04 0c 00 00 04 04 00 00 04 04 00 00 
92         04 04 00 00 04 04 00 00 04 04 00 00 04 0c 00 00 
108 PMPL    07 00 
110 PMP     00 04 00 08 00 04 00 00 00 04 00 00 00 04 00 00 
126         00 04 00 00 00 04 00 00 00 04 00 00 00 0c 00 00 
142 SFW     00 00 00 00
146 SAMD,V  00 00 00 00 
150 SWMD,V  00 00 00 00 
154 PSAL    07 00
156 PSA     07 07 0f 0f 0c 0b 00 00 0c 03 00 00 0c 03 00 00 
172         0c 03 00 00 0c 03 00 00 0c 03 00 00 0c 0b 00 00
188 PSWL    07 00 
190 PSW     0f 07 0f 0f 04 0c 00 00 04 04 00 00 04 04 00 00
206         04 04 00 00 04 04 00 00 04 04 00 00 04 0c 00 00
222 PSPL    07 00 
224 PSP     00 04 00 08 00 04 00 00 00 04 00 00 00 04 00 00
240         00 04 00 00 00 04 00 00 00 04 00 00 00 0c 00 00
258 end     f7
*/
