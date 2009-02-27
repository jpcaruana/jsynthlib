/*
 * Copyright 2005 Jeff Weber
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

package org.jsynthlib.drivers.behringer.vamp2;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.jsynthlib.core.CheckBoxWidget;
import org.jsynthlib.core.ComboBoxWidget;
import org.jsynthlib.core.KnobWidget;
import org.jsynthlib.core.ParamModel;
import org.jsynthlib.core.Patch;
import org.jsynthlib.core.PatchEditorFrame;
import org.jsynthlib.core.PatchNameWidget;
import org.jsynthlib.core.SysexWidget;

/**
 * Behringer VAmp2 Patch Editor
 * 
 * @author Jeff Weber
 */
public class VAmp2Editor extends PatchEditorFrame implements ActionListener {

    /** Array of strings representing the names of all amp models for the V-Amp 2.
     */
    private static final String[] AMP_MODEL_STRING = {
        "American Blues",
        "Modern Class A",
        "Tweed Combo",
        "Classic Clean",
        "Brit. Blues",
        "Brit. Class A",
        "Brit. Classic",
        "Brit. Hi Gain",
        "Rectified Hi Gain",
        "Modern Hi Gain",
        "Fuzz Box",
        "Ultimate V-Amp",
        "Drive V-Amp",
        "Crunch V-Amp",
        "Clean V-Amp",
        "Tube Preamp",
        "And Deluxe",
        "Custom Class A",
        "Small Combo",
        "Black Twin",
        "And Custom",
        "Non Top Boost",
        "Classic 50 W",
        "Brit. Class A 15 W",
        "Rectified Head",
        "Savage Beast",
        "Custom Hi Gain",
        "Ultimate Plus",
        "Calif. Drive",
        "Custom Drive",
        "Calif. Clean",
        "Custom Clean"
    };
    
    /** Array of strings representing the 16 cabinet models of the V-Amp 2.
     */
    private static final String[] CAB_MODEL_STRING = {
        "No Cabinet",
        "1 x 8'' Vintage Tweed",
        "4 x 10'' Vintage Bass",
        "4 x 10'' V-AMP Custom",
        "1 x 12'' Mid Combo",
        "1 x 12'' Blackface",
        "1 x 12'' Brit '60",
        "1 x 12'' Deluxe '52",
        "2 x 12'' Twin Combo",
        "2 x 12'' US Class A",
        "2 x 12'' V-AMP Custom",
        "2 x 12'' Brit '67",
        "4 x 12'' Vintage 30",
        "4 x 12'' Standard '78",
        "4 x 12'' Off Axis",
        "4 x 12'' V-AMP Custom"
    };
    
    /** Array of strings representing the 16 effects combos of the V-Amp 2.
     */
    private static final String[] EFF_NAME = {
        "Echo",
        "Delay",
        "Ping Pong",
        "Phaser/Delay",
        "Flanger/Delay 1",
        "Flanger/Delay 2",
        "Chorus/Delay 1",
        "Chorus/Delay 2",
        "Chorus/Compressor",
        "Compressor",
        "Auto Wah",
        "Phaser",
        "Chorus",
        "Flanger",
        "Tremolo",
        "Rotary"
    };
    
    /**
     * Array of strings representing the names of the seven effects panels
     * within the editor.
     */
    private static final String[] EFF_PANEL_NAME = {
        "Delay",
        "Mod/Delay",
        "Chorus/Comp",
        "Compressor",
        "Auto Wah",
        "Phaser/Flanger",
        "Chorus/Rotary",
        "Tremelo"
    };
    
    /**
     * Array of ints representing which of the eight effect panels are assigned
     * to each of the 16 effects combos within the editor.
     */
    private static final int[] EFF_PANEL_ASSGNMT = {
        0, 0, 0, 1, 1, 1, 1, 1, 2, 3, 4, 5, 6, 5, 7, 6
    };
    
    /**
     * Array of strings representing the available choices for the compressor
     * ratios.
     */
    private static final String[] COMPRESSOR_OPTIONS = {
        "1.2:1",
        "1.4:1",
        "2:1",
        "2.5:1",
        "3:1",
        "4.5:1"
    };
    
    /**
     * Array of strings representing the nine available reverb types.
     */
    private static final String[] REVERB_TYPE = {
        "Tiny Rm",
        "Small Rm",
        "Medium Rm",
        "Large Rm",
        "Ultra Rm",
        "Small Spr",
        "Med Spr",
        "Short Amb",
        "Long Amb"
    };
    
    /**
     * Used to set the widgetCount for all widgets.>
     */
    private int posWidgetCount = 1;

    private AmpSelectModel ampSelectModel = null;
    
    private JPanel effParmsPanel;

    private CheckBoxWidget effectsSwitch;
    
    /**
     * Constructs a VAmp2Editor for the selected patch.
     * @param patch
     *          The patch to be edited.
     */
    VAmp2Editor(Patch patch) {
        super("VAmp2 Patch Editor", patch);
                
        JPanel mainPanel = new JPanel();        
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
        
        mainPanel.add(newLeftPanel(patch));
        mainPanel.add(newRightPanel(patch));
        
//>>Uncomment after deleting prototye editor
        scrollPane.add(mainPanel);
        
//>>Delete after deleting prototye editor
//        addProtoEditor(patch, scrollPane, mainPanel);
    }

    /** Creates the left panel of the editor given the patch.
     * @param patch
     *          The patch to be edited.
     * @return
     *          A reference to left panel.
     */
    private JPanel newLeftPanel(Patch patch) {
        JPanel aPanel = new JPanel();        
        aPanel.setLayout(new BoxLayout(aPanel, BoxLayout.Y_AXIS));
        aPanel.add(newPatchNamePanel(patch));
        aPanel.add(newAmpModelPanel(patch));
        aPanel.add(newPreampPanel(patch));
        return aPanel;
    }

    /** Adds the patch name panel to the editor given the patch.
     * @param patch
     *          The patch to be edited.
     * @return
     *          A reference to the patch name panel.
     */
    private JPanel newPatchNamePanel(Patch patch) {
        JPanel aPanel = new JPanel();
        addWidget(aPanel, new PatchNameWidget("Preset Name ", patch),0,0,1,1,1);
        return aPanel;
    }
    
    /** Adds the Amp Model panel to the editor given the patch.
     * @param patch
     *          The patch to be edited.
     * @return
     *          A reference to the amp model panel.
     */
    private JPanel newAmpModelPanel(Patch patch) {
        JPanel aPanel = new JPanel();        
        aPanel.setLayout(new BoxLayout(aPanel, BoxLayout.Y_AXIS));
        aPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Amp Model",TitledBorder.CENTER,TitledBorder.CENTER));
        ComboBoxWidget ampSelectCB = newComboBoxWidget(aPanel, "Amp Model", patch, 7, 61, AMP_MODEL_STRING);
        ampSelectModel = new AmpSelectModel(ampSelectCB, aPanel);
        ComboBoxWidget cabSelectCB = newComboBoxWidget(aPanel, "Cabinet Model", patch, 11, 23, CAB_MODEL_STRING);
        return aPanel;
    }
    
    
    /** Adds the Preamp panel to the editor given the patch.
     * @param patch
     *          The patch to be edited.
     * @return
     *          A reference to the preamp panel.
     */
    private JPanel newPreampPanel(Patch patch) {
        JPanel aPanel = new JPanel();        
        aPanel.setLayout(new BoxLayout(aPanel, BoxLayout.Y_AXIS));
        aPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Preamp",TitledBorder.CENTER,TitledBorder.CENTER));
        aPanel.add(newDriveSwitchPanel(patch));
        aPanel.add(newPreampCtrlsPanel(patch));
        return aPanel;
    }
    
    /** Adds the Drive Switch panel to the editor given the patch.
     * @param patch
     *          The patch to be edited.
     * @return
     *          A reference to the drive switch panel.
     */
    private JPanel newDriveSwitchPanel(Patch patch) {
        JPanel aPanel = new JPanel();        
        aPanel.setLayout(new BoxLayout(aPanel, BoxLayout.X_AXIS));
        
        CheckBoxWidget c;
        c = newCheckBoxWidget(aPanel, "Drive",   patch, 14, 26);
        effectsSwitch = newCheckBoxWidget(aPanel, "Effects", patch,  9, 21);
        c = newCheckBoxWidget(aPanel, "Reverb",  patch, 10, 22);
        
        return aPanel;
    }
    
    /** Adds the Preamp Controls panel to the editor given the patch.
     * @param patch
     *          The patch to be edited.
     * @return
     *          A reference to the preamp controls panel.
     */
    private JPanel newPreampCtrlsPanel(Patch patch) {
        JPanel aPanel = new JPanel();        
        aPanel.setLayout(new BoxLayout(aPanel, BoxLayout.X_AXIS));

        // add KnobWidgets--parms--panel, label, patch, control min, control max, sysex offset, CC number
        addKnobWidget(aPanel, "Gain",     patch, 0, 127, 0, 12);
        addKnobWidget(aPanel, "Bass",     patch, 0, 127, 3, 15);
        addKnobWidget(aPanel, "Mid",      patch, 0, 127, 2, 14);
        addKnobWidget(aPanel, "Treble",   patch, 0, 127, 1, 13);
        addKnobWidget(aPanel, "Presence", patch, 0, 127, 5, 17);
        addKnobWidget(aPanel, "Volume",   patch, 0, 127, 4, 16);

        return aPanel;
    }
    
    /** Adds the right panel to the editor given the patch.
     * @param patch
     *          The patch to be edited.
     * @return
     *          A reference to the right panel.
     */
    private JPanel newRightPanel(Patch patch) {
        JPanel aPanel = new JPanel();        
        aPanel.setLayout(new BoxLayout(aPanel, BoxLayout.Y_AXIS));
        aPanel.add(new JLabel(" "));
        aPanel.add(new JLabel(" "));
        aPanel.add(newEffectsPanel(patch));
        aPanel.add(newMiscPanel(patch));
        return aPanel;
    }

    /** Adds the Effects panel to the editor given the patch.
     * @param patch
     *          The patch to be edited.
     * @return
     *          A reference to the effects panel.
     */
    private JPanel newEffectsPanel(Patch patch) {
        JPanel aPanel = new JPanel();        
        aPanel.setLayout(new BoxLayout(aPanel, BoxLayout.Y_AXIS));
        aPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Effects",TitledBorder.CENTER,TitledBorder.CENTER));
//        ComboBoxWidget effectsCB = newComboBoxWidget(aPanel, "Effects", patch, 8, 60, EFF_NAME); // Without Defaults
//        ComboBoxWidget effectsCB = newComboBoxWidget(aPanel, "Effects", patch, 8, 20, EFF_NAME); // With Defaults
//        ComboBoxWidget effectsCB = newComboBoxWidget(aPanel, "Effects", patch, 8, EFF_NAME); // Use EffectsParamModel & PatchSender
        ComboBoxWidget effectsCB = newEffectsComboBoxWidget(aPanel, "Effects", patch, 8, 20, EFF_NAME); // With Defaults EffectsParamModel and CCSender
        effectsCB.addEventListener(this);
  
        effParmsPanel = newEffParmsPanel(patch);
        setEffPanel(effectsCB.getValue());
        aPanel.add(effParmsPanel);
        return aPanel;
    }
    
    /** Handles ActionEvents for the effects combo box.*
     * @param e
     *          An ActionEvent.
     */
    public void actionPerformed(ActionEvent e) {
        JComboBox cb = (JComboBox)e.getSource();
        int selectedIndex = cb.getSelectedIndex();
        setEffPanel(selectedIndex);
        effectsSwitch.setValue();
    }
    
    /**
     * Displays the effects panel for the selected index. First calls the SysexWidget.setValue()
     * method for all of the widgets included within the selected effects
     * panel so that they display the true values. 
     * @param selectedIndex
     *              The index of the selected effects panel.
     */
    private void setEffPanel(int selectedIndex) {
        Container selFrame = (Container)effParmsPanel.getComponent(EFF_PANEL_ASSGNMT[selectedIndex]);
        for (int i = 0; i < selFrame.getComponentCount(); i++) {
            Container subFrame = (Container)selFrame.getComponent(i);
            for (int j = 0; j < subFrame.getComponentCount(); j++) {
                Component aComponent = subFrame.getComponent(j);
                if (aComponent instanceof SysexWidget) {
                    SysexWidget aWidget = (SysexWidget)aComponent;
                    aWidget.setValue();
                }
            }
        }
        CardLayout cl = (CardLayout)(effParmsPanel.getLayout());
        cl.show(effParmsPanel, EFF_PANEL_NAME[EFF_PANEL_ASSGNMT[selectedIndex]]);
    }
    
    /** Creates the different panels for each of the effects configurations.
     * @param patch
     *          The patch to be edited.
     * @return
     *          A reference to the effect parms panel.
     */
    private JPanel newEffParmsPanel(Patch patch) {
        JPanel aPanel = new JPanel(new CardLayout());

        JPanel cardPanel[] = new JPanel[8];
        
        for (int i = 0; i < cardPanel.length; i++) {
            cardPanel[i] = new JPanel();
            aPanel.add(cardPanel[i], EFF_PANEL_NAME[i]);
        }

        setupDelayPanel(cardPanel[0], patch);
        setupModDelayPanel(cardPanel[1], patch);
        setupChorusCompPanel(cardPanel[2], patch);
        setupCompressorPanel(cardPanel[3], patch);
        setupAutoWahPanel(cardPanel[4], patch);
        setupPhaserFlangerPanel(cardPanel[5], patch);
        setupChorusRotaryPanel(cardPanel[6], patch);
        setupTremeloPanel(cardPanel[7], patch);
        
        return aPanel;
    }

    /** Sets up the Delay effects panel.
     * @param parentPanel
     *          The Panel the delay panel will be added to.
     * @param patch
     *          The patch to be edited.
     */
    private void setupDelayPanel(JPanel parentPanel, Patch patch) {
        JPanel delayParmPanel = new JPanel();        
        delayParmPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Delay Parameters",TitledBorder.CENTER,TitledBorder.CENTER));
        addKnobWidget(delayParmPanel, "Mix",      patch, 0, 127, 26, 54);
        addKnobWidget(delayParmPanel, "Feedback", patch, 0, 127, 25, 53);
        addKnobWidget(delayParmPanel, "Spread",   patch, 0, 127, 24, 52);
        parentPanel.add(delayParmPanel);

        JPanel delayTimePanel = new JPanel();        
        delayTimePanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Delay Time",TitledBorder.CENTER,TitledBorder.CENTER));
        addKnobWidget(delayTimePanel, "Coarse",   patch, 0, 117, 22, 50);
        addKnobWidget(delayTimePanel, "Fine",     patch, 0, 127, 23, 51);
        parentPanel.add(delayTimePanel);
    }
    
    /** Sets up the Modulation/Delay effects panel.
     * @param parentPanel
     *          The Panel the mod delay panel will be added to.
     * @param patch
     *          The patch to be edited.
     */
    private void setupModDelayPanel(JPanel parentPanel, Patch patch) {
        JPanel modPanel = new JPanel();        
        modPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Modulation",TitledBorder.CENTER,TitledBorder.CENTER));
        addKnobWidget(modPanel, "Delay Mix", patch, 0, 127, 26, 54);
        addKnobWidget(modPanel, "Mod. Mix",  patch, 0, 127, 31, 59);
        addKnobWidget(modPanel, "Spread",    patch, 0, 127, 24, 52);
        parentPanel.add(modPanel);

        JPanel delayTimePanel = new JPanel();        
        delayTimePanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Delay Time",TitledBorder.CENTER,TitledBorder.CENTER));
        addKnobWidget(delayTimePanel, "Coarse",    patch, 0, 117, 22, 50);
        addKnobWidget(delayTimePanel, "Fine",      patch, 0, 127, 23, 51);
        parentPanel.add(delayTimePanel);
    }
   
    /** Sets up the Chorus/Compressor effects panel.
     * @param parentPanel
     *          The Panel the delay panel will be added to.
     * @param patch
     *          The patch to be edited.
     */
    private void setupChorusCompPanel(JPanel parentPanel, Patch patch) {
        JPanel aPanel = new JPanel();        
        aPanel.setBorder(new EtchedBorder(EtchedBorder.RAISED));
        ComboBoxWidget cb = newScaledComboBoxWidget(aPanel, "Sense", patch, 17, 45, COMPRESSOR_OPTIONS);
        addKnobWidget(aPanel, "Mod. Mix", patch, 0, 127, 31, 59);
        addKnobWidget(aPanel, "Speed",    patch, 0, 127, 30, 58);
        parentPanel.add(aPanel);
    }

    /** Sets up the Compressor effects panel.
     * @param parentPanel
     *          The Panel the compressor panel will be added to.
     * @param patch
     *          The patch to be edited.
     */
    private void setupCompressorPanel(JPanel parentPanel, Patch patch) {
        JPanel aPanel = new JPanel();        
        aPanel.setBorder(new EtchedBorder(EtchedBorder.RAISED));
        ComboBoxWidget cb = newScaledComboBoxWidget(aPanel, "Sense", patch, 17, 45, COMPRESSOR_OPTIONS);
        addKnobWidget(aPanel, "Attack", patch, 0, 127, 18, 46);
        parentPanel.add(aPanel);
    }
    
    /** Sets up the Auto Wah effects panel.
     * @param parentPanel
     *          The Panel the Auto Wah panel will be added to.
     * @param patch
     *          The patch to be edited.
     */
    private void setupAutoWahPanel(JPanel parentPanel, Patch patch) {
        JPanel aPanel = new JPanel();        
        aPanel.setBorder(new EtchedBorder(EtchedBorder.RAISED));
        addKnobWidget(aPanel, "Depth", patch, 0, 127, 17, 45);
        addKnobWidget(aPanel, "Speed", patch, 0, 127, 18, 46);
        parentPanel.add(aPanel);
    }
    
    /** Sets up the Phaser/Flanger effects panel.
     * @param parentPanel
     *          The Panel the Phaser/Flanger panel will be added to.
     * @param patch
     *          The patch to be edited.
     */
    private void setupPhaserFlangerPanel(JPanel parentPanel, Patch patch) {
        JPanel aPanel = new JPanel();        
        aPanel.setBorder(new EtchedBorder(EtchedBorder.RAISED));
        addKnobWidget(aPanel, "Mix",      patch, 0, 127, 31, 59);
        addKnobWidget(aPanel, "Feedback", patch, 0, 127, 30, 58);
        addKnobWidget(aPanel, "Speed",    patch, 0, 127, 28, 56);
        parentPanel.add(aPanel);
    }
    
    /** Sets up the Chorus/Rotary effects panel.
     * @param parentPanel
     *          The Panel the Chorus/Rotary panel will be added to.
     * @param patch
     *          The patch to be edited.
     */
    private void setupChorusRotaryPanel(JPanel parentPanel, Patch patch) {
        JPanel aPanel = new JPanel();        
        aPanel.setBorder(new EtchedBorder(EtchedBorder.RAISED));
        addKnobWidget(aPanel, "Mix",   patch, 0, 127, 31, 59);
        addKnobWidget(aPanel, "Depth", patch, 0, 127, 29, 57);
        addKnobWidget(aPanel, "Speed", patch, 0, 127, 28, 56);
        parentPanel.add(aPanel);
    }
    
    /** Sets up the Tremelo effects panel.
     * @param parentPanel
     *          The Panel the Tremelo panel will be added to.
     * @param patch
     *          The patch to be edited.
     */
    private void setupTremeloPanel(JPanel parentPanel, Patch patch) {
        JPanel aPanel = new JPanel();        
        aPanel.setBorder(new EtchedBorder(EtchedBorder.RAISED));
        addKnobWidget(aPanel, "Mix",   patch, 0, 127, 31, 59);
        addKnobWidget(aPanel, "Speed", patch, 0, 127, 28, 56);
        parentPanel.add(aPanel);
    }
    
    /** Adds the miscPanel panel to the editor given the patch. The
     * miscPanel holds the gate panel, the reverb panel and the wah
     * panel.
     * @param patch
     *          The patch to be edited.
     * @return
     *          A reference to the miscellaneous panel.
     */
    private JPanel newMiscPanel(Patch patch) {
        JPanel aPanel = new JPanel();        
        aPanel.setLayout(new BoxLayout(aPanel, BoxLayout.X_AXIS));
//        aPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"miscPanel",TitledBorder.CENTER,TitledBorder.CENTER));
        aPanel.add(newGatePanel(patch));
        aPanel.add(newReverbPanel(patch));
        aPanel.add(newWahPanel(patch));
        return aPanel;
    }
    
    /** Adds the Noise Gate panel to the editor given the patch.
     * @param patch
     *          The patch to be edited.
     * @return
     *          A reference to the noise gate panel.
     */
    private JPanel newGatePanel(Patch patch) {
        JPanel aPanel = new JPanel();        
        aPanel.setLayout(new BoxLayout(aPanel, BoxLayout.X_AXIS));
        aPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Gate",TitledBorder.CENTER,TitledBorder.CENTER));
        addKnobWidget(aPanel, "Level", patch, 0, 15, 13, 25);
        return aPanel;
    }
    
    /** Adds the Reverb panel to the editor given the patch.
     * @param patch
     *          The patch to be edited.
     * @return
     *          A reference to the reverb panel.
     */
    private JPanel newReverbPanel(Patch patch) {
        JPanel aPanel = new JPanel();        
        aPanel.setLayout(new BoxLayout(aPanel, BoxLayout.X_AXIS));
        aPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Reverb",TitledBorder.CENTER,TitledBorder.CENTER));
        ComboBoxWidget reverbCB = newComboBoxWidget(aPanel, "Type", patch, 12, 24, REVERB_TYPE);
        addKnobWidget(aPanel, "Mix", patch, 0, 127, 6, 18);
        return aPanel;
    }
    
    /** Adds the Wah panel to the editor given the patch.
     * @param patch
     *          The patch to be edited.
     * @return
     *          A reference to the Wah panel.
     */
    private JPanel newWahPanel(Patch patch) {
        JPanel aPanel = new JPanel();        
        aPanel.setLayout(new BoxLayout(aPanel, BoxLayout.X_AXIS));
        aPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Wah",TitledBorder.CENTER,TitledBorder.CENTER));
        addKnobWidget(aPanel, "Off/Pos.", patch, 0, 127, 15, 27);
        return aPanel;
    }
    
    /**
     * Adds a KnobWidget to the given JComponent.
     * 
     * @param panel
     *            The JComponent to which the Widget is to be added.
     * @param wlabel
     *            The Label for the Widget.
     * @param patch
     *            The patch, which is edited.
     * @param min
     *            The minimum value.
     * @param max
     *            The maximum value.
     * @param sysexOffset
     *            The offset (not including the sysex header bytes) of the
     *            parameter within the sysex record.
     * @param ccNum
     *            The CC number of the parameter
     */
    private void addKnobWidget(JComponent panel, String wLabel, Patch patch, int min, int max, int sysexOffset, int ccNum) {
        addKnobWidget(panel, wLabel, patch, min, max, 0, sysexOffset, ccNum);
    }
    
    /**
     * Adds a KnobWidget to the given JComponent.
     * 
     * @param panel
     *            The JComponent to which the Widget is to be added.
     * @param wlabel
     *            The Label for the Widget.
     * @param patch
     *            The patch, which is edited.
     * @param min
     *            The minimum value.
     * @param max
     *            The maximum value.
     * @param base
     *            value display offset.
     * @param sysexOffset
     *            The offset (not including the sysex header bytes) of the
     *            parameter within the sysex record.
     * @param ccNum
     *            The CC number of the parameter
     */
    private void addKnobWidget(JComponent panel, String wLabel, Patch patch, int min, int max, int base, int sysexOffset, int ccNum) {
        addWidget(panel,
                      new KnobWidget(wLabel,
                                     patch,
                                     min, max, base,
                                     new ParamModel(patch, Constants.HDR_SIZE + sysexOffset),
                                     new CCSender(ccNum)),
                      0,0,
                      1,1,
                      posWidgetCount++);
    }

    /**
     * Adds a ComboBoxWidget to the given JComponent The ComboBoxWidget uses a
     * standard ParamModel and a CCSender.
     * 
     * @param panel
     *            The JComponent to which the Widget is to be added.
     * @param wlabel
     *            The Label for the Widget.
     * @param patch
     *            The patch, which is edited.
     * @param sysexOffset
     *            The offset (not including the sysex header bytes) of the
     *            parameter within the sysex record.
     * @param ccNum
     *            The CC number of the parameter
     * @param options
     *            Array, which contains the list of the options in the combobox.
     * @return
     *            A reference to the ComboBoxWidget.
     */
    private ComboBoxWidget newComboBoxWidget(JComponent panel, String wLabel, Patch patch, int sysexOffset, int ccNum, Object[] options) {
        ComboBoxWidget cb = new ComboBoxWidget(wLabel,
                                               patch,
                                               new ParamModel(patch, Constants.HDR_SIZE + sysexOffset),
                                               new CCSender(ccNum),
                                               options);
        addWidget(panel,
                  cb,
                  0,0,
                  1,1,
                  posWidgetCount++);
        
        return cb;
    }
    
    /**
     * Adds a ComboBoxWidget to the given JComponent The ComboBoxWidget uses a
     * standard ParamModel and a CCSender.
     * 
     * @param panel
     *            The JComponent to which the Widget is to be added.
     * @param wlabel
     *            The Label for the Widget.
     * @param patch
     *            The patch, which is edited.
     * @param sysexOffset
     *            The offset (not including the sysex header bytes) of the
     *            parameter within the sysex record.
     * @param ccNum
     *            The CC number of the parameter
     * @param options
     *            Array, which contains the list of the options in the combobox.
     * @return
     *            A reference to the ComboBoxWidget.
     */
    private ComboBoxWidget newEffectsComboBoxWidget(JComponent panel, String wLabel, Patch patch, int sysexOffset, int ccNum, Object[] options) {
        ComboBoxWidget cb = new ComboBoxWidget(wLabel,
                                               patch,
                                               new EffectsParamModel(patch, Constants.HDR_SIZE + sysexOffset),
                                               new CCSender(ccNum),
                                               options);
        addWidget(panel,
                  cb,
                  0,0,
                  1,1,
                  posWidgetCount++);
        
        return cb;
    }
    
    /**
     * Adds a ScaledComboBoxWidget to the given JComponent. The values input are
     * scaled over the range 0-127 in equal steps determined by the number of
     * items in the options array.
     * 
     * @param panel
     *            The JComponent to which the Widget is to be added.
     * @param wlabel
     *            The Label for the Widget.
     * @param patch
     *            The patch, which is edited.
     * @param sysexOffset
     *            The offset (not including the sysex header bytes) of the
     *            parameter within the sysex record.
     * @param ccNum
     *            The CC number of the parameter
     * @param options
     *            Array, which contains the list of the options in the combobox.
     * @return A reference to the ComboBoxWidget.
     */
    private ComboBoxWidget newScaledComboBoxWidget(JComponent panel, String wLabel, Patch patch, int sysexOffset, int ccNum, Object[] options) {
        ComboBoxWidget cb = new ComboBoxWidget(wLabel,
                                               patch,
                                               new ScaledParamModel(patch, Constants.HDR_SIZE + sysexOffset, options.length, 132),
                                               new CCSender(ccNum, 132 / options.length),
                                               options);
        addWidget(panel,
                  cb,
                  0,0,
                  1,1,
                  posWidgetCount++);
        
        return cb;
    }
    
    /** Adds a CheckBoxWidget to the given JComponent.
     * @param panel The JComponent to which the Widget is to be added.
     * @param wlabel The Label for the Widget.
     * @param patch The patch, which is edited.
     * @param sysexOffset The offset (not including the sysex header bytes) of the parameter within the sysex record.
     * @param ccNum The CC number of the parameter.
     * @return a reference to the CheckBoxWidget.
     */
    private CheckBoxWidget newCheckBoxWidget(JComponent panel, String wLabel, Patch patch, int sysexOffset, int ccNum) {
        CheckBoxWidget cb = new CheckBoxWidget(wLabel,
                                               patch,
                                               new ParamModel(patch, Constants.HDR_SIZE + sysexOffset),
                                               new CCSender(ccNum, 127));
        addWidget(panel,
                  cb,
                  0,0,
                  1,1,
                  posWidgetCount++);
        
        return cb;
    }
    
    
/********************************************************************************************/    
//>> Prototype Editor - Delete when no longer needed    
    
    int protoWidgetCount = 1;

    private String[] ctlNames = {
        "(0)",
        "(1)Wah Pdl",
        "(2)",
        "(3)",
        "(4)",
        "(5)",
        "(6)",
        "(7)Vol Pdl",
        "(8)",
        "(9)",
        "(10)",
        "(11)",
        "(12)Gain",
        "(13)Treble",
        "(14)Mid",
        "(15)Bass",
        "(16)Vol",
        "(17)Pres",
        "(18)Rev Mix",
        "(19)Amp Tp w/def",
        "(20)Fx Tp w/def",
        "(21)Fx 0/1",
        "(22)Rev Send 0/1",
        "(23)Cab Type",
        "(24)Rev Type",
        "(25)Gate Lev",
        "(26)Drive 0/1",
        "(27)Wah 0/pos",
        "(28)",
        "(29)",
        "(30)",
        "(31)",
        "(32)",
        "(33)",
        "(34)",
        "(35)",
        "(36)",
        "(37)",
        "(38)",
        "(39)",
        "(40)",
        "(41)",
        "(42)",
        "(43)",
        "(44)pre Eff Tp",
        "(45)pre Eff Par 1",
        "(46)pre Eff Par 2",
        "(47)pre Eff Par 3",
        "(48)pre Eff Par 4",
        "(49)Del Tp",
        "(50)Del Tm hi",
        "(51)Del Tm lo",
        "(52)Del Spr",
        "(53)Del Fdbk",
        "(54)Del Mix",
        "(55)post Fx Mode",
        "(56)post Fx Par 1",
        "(57)post Fx Par 2",
        "(58)post Fx Par 3",
        "(59)post Fx Mix",
        "(60)Assgn Eff Ctrl",
        "(61)Amp Type no cab",
        "(62)",
        "(62)",
        "(64)Tap",
        "(65)",
        "(66)",
        "(67)",
        "(68)",
        "(69)",
        "(70)",
        "(71)",
        "(72)",
        "(73)",
        "(74)",
        "(75)",
        "(76)",
        "(77)",
        "(78)",
        "(79)",
        "(80)Req Ctrls",
        "(81)Curs Pos/Char",
        "(82)Tune Bp Vol",
        "(83)Tune Ctr Frq",
        "(84)Config",
        "(85)Live EQ Trb",
        "(86)Live EQ Mid",
        "(87)Live EQ Bass",
        "(88)Dig Out",
        "(89)Inp Gain",
        "(90)Wah char"
    };
    
    private void addProtoEditor(Patch patch, JPanel scrollPane, JPanel mainPanel) {
        JPanel glPanel = new JPanel();
        glPanel.setLayout(new BoxLayout(glPanel, BoxLayout.Y_AXIS));
                
        int[] group1 = {18, 24, 25, 27, 44, 47, 48};
        glPanel.add(newProtoPane(patch, group1));

        int[] group2 = {45, 49, 55, 60, 64, 80, 81, 82};
        glPanel.add(newProtoPane(patch, group2));

        int[] group3 = {83, 84, 85, 86, 87, 88, 89};
        glPanel.add(newProtoPane(patch, group3));

        JTabbedPane tabbedPane = new JTabbedPane();
        scrollPane.add(tabbedPane);
        
        tabbedPane.addTab("Development Editor", mainPanel);
        
        tabbedPane.addTab("Prototype Editor", glPanel);
        
        pack();
    }
    
    private JPanel newProtoPane(Patch patch, int[] cc) {
        JPanel devPanel = new JPanel();
        devPanel.setLayout(new BoxLayout(devPanel, BoxLayout.X_AXIS));
        devPanel.setBorder(new EtchedBorder(EtchedBorder.RAISED));
        for (int i = 0; i < cc.length; i++) {
            ParamModel pModel = null;
            addWidget(devPanel,
                      new KnobWidget(ctlNames[cc[i]],
                                     patch,
                                     0, 127,0,
                                     null,
                                     new CCSender(cc[i])),
                      0,0,
                      1,1,
                      protoWidgetCount);
            protoWidgetCount++;
        }

        return devPanel;
    }

//>> End of code for Prototype Editor - Delete when no longer needed    
    
}
    