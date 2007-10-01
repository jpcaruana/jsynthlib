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

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import core.CheckBoxWidget;
import core.EnvelopeWidget;
import core.IPatchDriver;
import core.Patch;
import core.PatchEditorFrame;
import core.ScrollBarLookupWidget;
import core.ScrollBarWidget;
import core.SysexWidget;


/**
 * Editor for a patch from a Casio CZ-101/1000.
 * Patch must be 264 bytes long, not the 263-byte version from the synth.
 * <P>
 * Released under GPL and all that.
 * 
 * @author Bill Zwicky
 */
public class CasioCZ1000SingleEditor extends PatchEditorFrame {
    /** Offset to first data byte in sysex. */
    public static final int TONE_DATA = 7;
    /** TRUE to edit all bits in sysex, FALSE to stick to official limits. */
    protected boolean hackMode = false;
    /** FALSE for 'stack', TRUE for 'wide' */
    protected boolean wideGui = false;
    
    public CasioCZ1000SingleEditor(Patch patch) {
        super("Casio CZ-101/1000 Patch Editor", patch);

        hackMode =
            "Extended".equals(
                    patch.getDevice().getPreferences().get("paramMode",null));
        wideGui =
            "Wide".equals(
                    patch.getDevice().getPreferences().get("guiMode",null));
        
		gbc.gridx=0;
		gbc.gridy=0;
        if(wideGui) {
	        scrollPane.add(getWideGUI(patch),gbc);
        }
        else {
	        scrollPane.add(getStackedGUI(patch),gbc);
        }
    }

    /**
     * Control which envelope GUI we're going to use.
     */
    protected JPanel getEnvelopeGUI(Patch patch, int firstOffset) {
        //env widg is broke
        return getEnvelopeGUI1(patch,firstOffset);
    }
    
    /**
     * Small window, everything is stacked with tabs.
     * @param patch Patch to edit.
     * @return Complete GUI component.
     */
    public JComponent getStackedGUI(Patch patch) {
        
        JComponent paramgui = getParamGUI(patch);

		JTabbedPane osc1Pane=new JTabbedPane();
		osc1Pane.addTab("Waveform", getWaveformGUI(patch,CZModel.MFW));
		osc1Pane.addTab("Pitch",    getDco1Panel(patch));
		osc1Pane.addTab("Wave",     getDcw1Panel(patch));
		osc1Pane.addTab("Amp",      getDca1Panel(patch));

		JTabbedPane osc2Pane=new JTabbedPane();
		osc2Pane.addTab("Waveform", getWaveformGUI(patch,CZModel.SFW));
		osc2Pane.addTab("Pitch",    getDco2Panel(patch));
		osc2Pane.addTab("Wave",     getDcw2Panel(patch));
		osc2Pane.addTab("Amp",      getDca2Panel(patch));

        JTabbedPane mainTabs = new JTabbedPane();
        mainTabs.addTab("Parameters",   paramgui);
        mainTabs.addTab("Oscillator 1", osc1Pane);
        mainTabs.addTab("Oscillator 2", osc2Pane);
        
        return mainTabs;
    }

    /**
     * Params pulled out and visible at all times.
     * @param patch Patch to edit.
     * @return Complete GUI component.
     */
    public JComponent getWideGUI(Patch patch) {
        JComponent paramgui = getParamGUI(patch);
        paramgui.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Parameters",TitledBorder.LEFT,TitledBorder.CENTER));

		JTabbedPane oscPane=new JTabbedPane();
		oscPane.addTab("1 WF",    getWaveformGUI(patch,CZModel.MFW));
		oscPane.addTab("1 Pitch", getDco1Panel(patch));
		oscPane.addTab("1 Wave",  getDcw1Panel(patch));
		oscPane.addTab("1 Amp",   getDca1Panel(patch));
		oscPane.addTab("2 WF",    getWaveformGUI(patch,CZModel.SFW));
		oscPane.addTab("2 Pitch", getDco2Panel(patch));
		oscPane.addTab("2 Wave",  getDcw2Panel(patch));
		oscPane.addTab("2 Amp",   getDca2Panel(patch));

		JPanel mainPane = new JPanel();
		mainPane.setLayout(new GridBagLayout());
		gbc.gridy=0;
		gbc.gridx=GridBagConstraints.RELATIVE;
        //gbc.weightx = 1;
        mainPane.add(paramgui,gbc);
        //gbc.weightx = 1;
        mainPane.add(oscPane,gbc);
        
        return mainPane;
    }

    
    protected JPanel getDca2Panel(Patch patch) {
        JPanel dca2Panel = new JPanel();
		dca2Panel.setLayout(new GridBagLayout());
		if(hackMode)
		    addWidget(dca2Panel,new ScrollBarWidget("Key Follow",patch,0,0xFFFF,0,new WordFieldModel(patch,CZModel.SAMD,0xFFFF),null),0,0,7,1,0);
		else
		    addWidget(dca2Panel,new ScrollBarWidget("Key Follow",patch,0,9,0,new KeyFollowAModel(patch,CZModel.SAMD),null),0,0,7,1,0);
		add(dca2Panel,getEnvelopeGUI(patch,CZModel.PSAL),0,1,1,1);
        return dca2Panel;
    }

    protected JPanel getDcw2Panel(Patch patch) {
        JPanel dcw2Panel = new JPanel();
		dcw2Panel.setLayout(new GridBagLayout());
		if(hackMode)
		    addWidget(dcw2Panel,new ScrollBarWidget("Key Follow",patch,0,0xFFFF,0,new WordFieldModel(patch,CZModel.SWMD,0xFFFF),null),0,0,7,1,0);
		else
		    addWidget(dcw2Panel,new ScrollBarWidget("Key Follow",patch,0,9,0,new KeyFollowWModel(patch,CZModel.SWMD),null),0,0,7,1,0);
		add(dcw2Panel,getEnvelopeGUI(patch,CZModel.PSWL),0,1,1,1);
        return dcw2Panel;
    }

    protected JPanel getDco2Panel(Patch patch) {
        JPanel dco2Panel = new JPanel();
		dco2Panel.setLayout(new GridBagLayout());
		add(dco2Panel,getEnvelopeGUI(patch,CZModel.PSPL),0,0,1,1);
        return dco2Panel;
    }

    protected JPanel getDco1Panel(Patch patch) {
        JPanel dco1Panel = new JPanel();
		dco1Panel.setLayout(new GridBagLayout());
		add(dco1Panel,getEnvelopeGUI(patch,CZModel.PMPL),0,0,1,1);
        return dco1Panel;
    }

    protected JPanel getDca1Panel(Patch patch) {
        JPanel dca1Panel = new JPanel();
		dca1Panel.setLayout(new GridBagLayout());
		if(hackMode)
		    // direct access to computed value
		    addWidget(dca1Panel,new ScrollBarWidget("Key Follow",patch,0,0xFF,0,new CZModel(patch,TONE_DATA+CZModel.MAMV),null),0,0,7,1,0);
		else
		    // 10 steps only
		    addWidget(dca1Panel,new ScrollBarWidget("Key Follow",patch,0,9,0,new KeyFollowAModel(patch,CZModel.MAMD),null),0,0,7,1,0);
		add(dca1Panel,getEnvelopeGUI(patch,CZModel.PMAL),0,1,1,1);
        return dca1Panel;
    }

    protected JPanel getDcw1Panel(Patch patch) {
        JPanel dcw1Panel = new JPanel();
		dcw1Panel.setLayout(new GridBagLayout());
		if(hackMode)
		    // direct access to computed value
		    addWidget(dcw1Panel,new ScrollBarWidget("Key Follow",patch,0,0xFF,0,new CZModel(patch,TONE_DATA+CZModel.MWMV),null),0,0,7,1,0);
		else
		    // 10 steps only
		    addWidget(dcw1Panel,new ScrollBarWidget("Key Follow",patch,0,9,0,new KeyFollowWModel(patch,CZModel.MWMD),null),0,0,7,1,0);
		add(dcw1Panel,getEnvelopeGUI(patch,CZModel.PMWL),0,1,1,1);
        return dcw1Panel;
    }

    public JComponent getParamGUI(Patch patch) {
		gbc.weightx=1;
        
        JPanel miscPane=new JPanel();
        miscPane.setLayout(new GridBagLayout());

        int row=-1;
        addWidget(miscPane,new ScrollBarLookupWidget("Line Select",patch,0,3,80,
                new LineModel(patch),null, 
                new String[] {"1","2","1+1'","1+2'"}),
                0,++row,7,1,0);

		if(hackMode) {
			addWidget(miscPane,new CheckBoxWidget("Ring",  patch,new BitModel(patch,CZModel.MFW+2,5),null),0,++row,1,1,0);
		    addWidget(miscPane,new CheckBoxWidget("Noise1",patch,new BitModel(patch,CZModel.MFW+2,4),null),1,row,1,1,0);
		    addWidget(miscPane,new CheckBoxWidget("Noise2",patch,new BitModel(patch,CZModel.MFW+2,3),null),2,row,1,1,0);
		    // undocumented bit, seems to mute first voice
			addWidget(miscPane,new CheckBoxWidget("Mute1", patch,new BitModel(patch,CZModel.MFW+2,2),null),3,row,1,1,0);
		}
		else {
	        addWidget(miscPane,new ScrollBarLookupWidget("Modulation",patch,0,3,80,
	                new ModModel(patch),null, 
	                new String[] {"OFF", "Ring", "Noise"}),
	                0,++row,7,1,0);
		}

		addWidget(miscPane,getDetuneStepGUI(patch),0,++row,7,1,0);
        addWidget(miscPane,new ScrollBarWidget("Detune Fine",patch,  0,63,0,80,new DetuneFineModel(patch),null),0,++row,7,1,0);
        
        // Octave val==3 is same as 1 (+1 octave)
        addWidget(miscPane,new ScrollBarWidget("Octave",patch,-1,1,0,80,new OctaveModel(patch),null),0,++row,7,1,0);
        add(miscPane, getVibratoGUI(patch),0,++row,7,4);

        return miscPane;
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

    
    protected SysexWidget getDetuneStepGUI(Patch patch) {
		int max = (hackMode ? 127 : 47);
		String[] dsopts = new String[2*max+1];
		dsopts[max] = "0";
		for(int i=1; i<=max; i++) {
		    dsopts[max+i] = "+"+i/12+"'"+i%12;
		    dsopts[max-i] = "-"+i/12+"'"+i%12;
		}
	    return new ScrollBarLookupWidget("Detune Step", patch, -max, max, 80, new DetuneStepModel(patch), null, dsopts);
	    //boring half-step number only:
	    //return new ScrollBarWidget("Detune Step",patch,-max,max,0,80,new DetuneStepModel(patch),null);
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

        if(hackMode) {
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
        addWidget(panel,new ScrollBarLookupWidget("Waveform 1",patch,0,wfLabels.length-1,80,
                new WordFieldModel(patch,TONE_DATA+offset,0xE000),null,wfLabels),
                0,++row,7,1,0);
        addWidget(panel,new ScrollBarLookupWidget("Waveform 2",patch,0,wfLabels.length-1,80,
                new WordFieldModel(patch,TONE_DATA+offset,0x1C00),null,wfLabels),
                0,++row,7,1,0);
		addWidget(panel,new CheckBoxWidget("Enable WF 2",patch,new BitModel(patch,offset,1),null),0,++row,1,1,0);
        addWidget(panel,new ScrollBarLookupWidget("Window",patch,0,resLabels.length-1,80,new WordFieldModel(patch,TONE_DATA+offset,0x01C0),
                null,resLabels),0,++row,7,1,0);
        return panel;
    }

    /**
     * "Normal mode": envelope control give access to params as seen on LCD.
     * Uses sliders for everything.
     * 
     * @param patch work area
     * @param firstOffset offset to "final stage" param
     * @return GUI for envelope.
     */
    protected JPanel getEnvelopeGUI1(Patch patch, int firstOffset) {
        //TODO non-hack-mode == only one stage can be sustain
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Envelope",TitledBorder.LEFT,TitledBorder.CENTER));
        EnvelopeModel model = new EnvelopeModel(patch, firstOffset);
        int row=-1, width=40, maxval;
        // models are sensitive to hack mode too
        if(hackMode)
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
     * This is an experimental panel that uses the EnvelopeWidget.
     * Doesn't work for some reason.
     * 
     * @param patch work area
     * @param firstOffset offset to "final stage" param
     * @return GUI for envelope.
     */
    protected JPanel getEnvelopeGUI2(Patch patch, int firstOffset) {
        JPanel panel = new JPanel();
        int maxval, row=0;
        panel.setLayout(new GridBagLayout());
        EnvelopeModel model = new EnvelopeModel(patch, firstOffset);
        // models are sensitive to hack mode too
        if(hackMode)
            maxval = 127;
        else
            maxval = 99;
        // CZ can have 1-8 stages, any can be sustain.
        EnvelopeWidget.Node[] nodes = new EnvelopeWidget.Node[] {
                new EnvelopeWidget.Node(0, 0, null, 0, 0, null, 0, false, null, null, null, null),
                new EnvelopeWidget.Node(0, maxval, model.getRateModel(0), 0, maxval, model.getLevelModel(0), 0, false, new NullSender(), new NullSender(), "1 Rate", "   Level"),
                new EnvelopeWidget.Node(0, maxval, model.getRateModel(1), 0, maxval, model.getLevelModel(1), 0, false, new NullSender(), new NullSender(), "2 Rate", "   Level"),
                new EnvelopeWidget.Node(0, maxval, model.getRateModel(2), 0, maxval, model.getLevelModel(2), 0, false, new NullSender(), new NullSender(), "3 Rate", "   Level"),
                new EnvelopeWidget.Node(0, maxval, model.getRateModel(3), 0, maxval, model.getLevelModel(3), 0, false, new NullSender(), new NullSender(), "4 Rate", "   Level"),
                new EnvelopeWidget.Node(0, maxval, model.getRateModel(4), 0, maxval, model.getLevelModel(4), 0, false, new NullSender(), new NullSender(), "5 Rate", "   Level"),
                new EnvelopeWidget.Node(0, maxval, model.getRateModel(5), 0, maxval, model.getLevelModel(5), 0, false, new NullSender(), new NullSender(), "6 Rate", "   Level"),
                new EnvelopeWidget.Node(0, maxval, model.getRateModel(6), 0, maxval, model.getLevelModel(6), 0, false, new NullSender(), new NullSender(), "7 Rate", "   Level"),
                new EnvelopeWidget.Node(0, maxval, model.getRateModel(7), 0, maxval, model.getLevelModel(7), 0, false, new NullSender(), new NullSender(), "8 Rate", "   Level"),
        };
        SysexWidget wid = new EnvelopeWidget("Envelope", patch, nodes);
//        wid.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Envelope",TitledBorder.LEFT,TitledBorder.CENTER));
//        wid.setMinimumSize(new Dimension(600,300));
        addWidget(panel, wid, 0, row, 1, 1, 0);
        //addWidget(panel, new ScrollBarWidget("Sustain Stage", patch, -1, 7, 1, 70, model.getSustainModel(),  null), 0, ++row, 1, 1, 0);
        addWidget(panel, new ScrollBarLookupWidget("Sustain Stage", patch, -1, 7, 70, 
                model.getSustainModel(),  null,
                new String[] {"OFF", "1", "2", "3", "4", "5", "6", "7", "8"}),
                0, ++row, 1, 1, 0);
        addWidget(panel, new ScrollBarWidget("Final Stage",   patch,  0, 7, 1, 70, model.getEndStageModel(), null), 0, ++row, 1, 1, 0);
        return panel;
    }
    
    
    
    

    /**
     * Dummy model for those widgets that won't take null for an answer.
     */
    class NullModel implements SysexWidget.IParamModel {
        int currValue;
        /** Constructs a NullModel.*/
        NullModel() {} 
        /** Null method for NullModel.*/
        public void set(int value) {currValue=value;}
        /** Null method for NullModel.*/
        public int get() {return currValue;}
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
            super(p,TONE_DATA+CZModel.PFLAG);
        }
        public void set(int i) {
            setByte(ofs, (getByte(ofs) & 0xFC) | (i & 3));
        }
        public int get() {
            return getByte(ofs) & 3;
        }
    }

    class ModModel extends CZModel {
        final int   MASK = 0x38;
        final int[] VALS = { 0, 0x20, 0x18 };
        
        public ModModel(Patch p) {
            super(p,TONE_DATA+CZModel.MFW+2);
        }
        public void set(int value) {
            value &= 0x3;
            setByte(ofs, (getByte(ofs) & ~MASK) | VALS[value]);
        }
        public int get() {
            int value = getByte(ofs) & MASK;
            switch(value){
            	case 0x00:
            	    value = 0;
            	    break;
        	    case 0x20:
        	        value = 1;
        	        break;
    	        default:
    	            value = 2;
            }
            return value;
        }
    }

    class OctaveModel extends CZModel {
        public OctaveModel(Patch p) {
            super(p, TONE_DATA+CZModel.PFLAG);
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
            super(p, TONE_DATA+CZModel.PDS);
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
            super(p, TONE_DATA+CZModel.PDL);
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
            super(p, TONE_DATA+CZModel.PVK);
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
            super(p, CZModel.PVDLD);
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
            super(p, CZModel.PVSD);
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
            super(p, CZModel.PVDD);
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
            setByte(ofs, value);
            setByte(ofs+2, map[value]);
        }
        public int get() {
            return getByte(ofs);
        }
    }

    class KeyFollowAModel extends KeyFollowModel {
        int[] MAP = { 
                0x00, 0x08, 0x11, 0x1A, 0x24,
                0x2F, 0x3A, 0x45, 0x52, 0x5F 
        };
        public KeyFollowAModel(Patch p, int offset) {
            super(p,offset);
            map = MAP;
        }
    }
    
    class KeyFollowWModel extends KeyFollowModel {
        int[] MAP = { 
                0x00, 0x1F, 0x2C, 0x39, 0x46, 
                0x53, 0x60, 0x6E, 0x92, 0xFF 
        };
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
         * @param stage 0..7, or -1 for no sustain.
         */
        public void setSustainStage(int stage) {
            if(stage<-1 || stage>7)  throw new IllegalArgumentException("stage="+stage+"; must be -1 or 0..7");
            for(int i=0; i<8; i++) {
                int o = ofs+4+4*i;                
                setByte(o, getByte(o) & 0x7F);
            }
            if(stage >= 0) {
	            int o = ofs+4+4*stage;                
	            setByte(o, getByte(o) | 0x80);
            }
        }
        public int getSustainStage() {
            for (int i = 0; i < 8; i++) {
                int o = ofs+4+4*i;                
                if ((getByte(o) & 0x80) != 0)
                    return i;
            }
            // no sustain
            return -1;
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
        
        /**
         * Only one stage can be sustain.
         */
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
        /**
         * Lets user freely flip sustain bits for all 8 stages.
         */
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
                    if(!hackMode)
                        value = 119 * value / 99;
                    setRate(stage, value);
                }
                public int get() {
                    int value = getRate(stage);
                    if(!hackMode) {
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
                    if(!hackMode) {
                        if(value == 1)
                            value = 0x10;
                        else if(value > 0)
                            value += 0x1C;
                    }
                    setLevel(stage, value);
                }
                public int get() {
                    int value = getLevel(stage);
                    if(!hackMode) {
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
    
    /**
     * Dummy sender for those widgets that won't take null for an answer.
     */
    class NullSender implements SysexWidget.ISender {
        public void send(IPatchDriver driver, int value) { }
    }
}
