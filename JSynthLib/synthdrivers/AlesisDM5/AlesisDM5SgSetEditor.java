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
import javax.swing.event.*;
import java.awt.event.*;

// packages needed for class DummySender
import javax.sound.midi.SysexMessage;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.InvalidMidiDataException;

/** Alesis DM5 Single Drumset Editor
* 
* @author Jeff Weber
*/
public class AlesisDM5SgSetEditor extends PatchEditorFrame {
    /** Size of program patch header--7 bytes.
    */
    static final int headerSize = Constants.HDR_SIZE;
    
    static final String[] noteNames = new String[] {
            "C",
            "C#",
            "D",
            "D#",
            "E",
            "F",
            "F#",
            "G",
            "G#",
            "A",
            "A#",
            "B"        
    }; 
 
    private int ctrlBase = 1;
    private int widgetCount = 1;
    
    private Patch patch;
    
    private JTextField selectedNoteField;
    
    private PacketModel[] notePacketModel= new PacketModel[8];
    private SysexWidget[] sysexWidget = new SysexWidget[8];
    
    private ScrollBarLookupWidget rootNoteWidget;
    private UpdateableScrollBarLookupWidget closeNoteWidget;
    private UpdateableScrollBarLookupWidget heldNoteWidget;
    private UpdateableScrollBarLookupWidget[] triggerWidget = new UpdateableScrollBarLookupWidget[12];
    
   /** Constructs a AlesisDM5SgSetEditor for the selected patch.*/
    AlesisDM5SgSetEditor(Patch patch)
    {
        super ("Alesis DM5 Single Drumset Editor",patch);   
        scrollPane.setLayout(new GridLayout(0,1));
        addMainPanel(patch);
        this.patch = patch;
        pack();
        show();
    }
    
    private void addMainPanel(Patch patch) {
        JPanel mainPanel = new JPanel();        
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"mainPanel",TitledBorder.CENTER,TitledBorder.CENTER));
        addWidget(mainPanel, new PatchNameWidget("Program Name ", patch),0,0,1,1,widgetCount++);
        addSubPanels(patch, mainPanel);
        addTriggerWidgets(patch, mainPanel);
        scrollPane.add(mainPanel,gbc);        
    }
    
    private void addSubPanels(Patch patch, JPanel panel) {
        JPanel noteSelectPanel = new JPanel();        
        noteSelectPanel.setLayout(new BoxLayout(noteSelectPanel, BoxLayout.Y_AXIS));
        noteSelectPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"noteSelectPanel",TitledBorder.CENTER,TitledBorder.CENTER));
        panel.add(noteSelectPanel,gbc);        

        addSelectedNoteWidget(patch, noteSelectPanel);

        JPanel parmsPanel = new JPanel();        
        parmsPanel.setLayout(new BoxLayout(parmsPanel, BoxLayout.Y_AXIS));
        parmsPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"parmsPanel",TitledBorder.CENTER,TitledBorder.CENTER));
        noteSelectPanel.add(parmsPanel,gbc);        
        addParmsSubPanels(patch, parmsPanel);
    }
    
    private void addParmsSubPanels(Patch patch, JPanel panel) {
        JPanel voicePanel = new JPanel();        
        voicePanel.setLayout(new BoxLayout(voicePanel, BoxLayout.X_AXIS));
//        voicePanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"voicePanel",TitledBorder.CENTER,TitledBorder.CENTER));
        panel.add(voicePanel,gbc);        
        addVoiceWidgets(patch, voicePanel);

        addParmWidgets(patch, panel);

        JPanel outputPanel = new JPanel();        
        outputPanel.setLayout(new BoxLayout(outputPanel, BoxLayout.X_AXIS));
//        outputPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"outputPanel",TitledBorder.CENTER,TitledBorder.CENTER));
        panel.add(outputPanel,gbc);
        addOutputWidgets(patch, outputPanel);
    }
    
    private String[] getNoteNames(int startValue, int numNotes) {
        String noteTitle[] = new String[numNotes];
        for (int i = 0; i < numNotes; i++) {
            noteTitle[i] = String.valueOf(i + startValue) + "/" + noteNames[(i + startValue) % 12] + String.valueOf(((i + startValue) / 12) - 2);
        }
        return noteTitle;
    }

    private void addSelectedNoteWidget(Patch patch, JPanel panel) {
        JPanel selectedNoteWidgetPanel = new JPanel();        
        selectedNoteWidgetPanel.setLayout(new BorderLayout());
        panel.add(selectedNoteWidgetPanel,gbc);
        
        ScrollBarLookupWidget selNote = new ScrollBarLookupWidget("Selected Note", patch, 0, 60, 0,
                                                                  null,
                                                                  new NRPNSender(NRPNSender.PREVIEW_NOTE, NRPNSender.CC_MAP_0_60),
                                                                  getNoteNames(36, 61));
        addWidget(panel, selNote, ctrlBase++, 0, 1, 1, widgetCount++);
        selNote.addEventListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                selNoteChanged(e);
            }
	    });
        
    }

    public void selNoteChanged(ChangeEvent e) {
        JSlider sl = (JSlider)e.getSource();
        int noteVal = (int)sl.getValue();
        if (!sl.getValueIsAdjusting()) {
            //            System.out.println("----------------------------------------------");
            //            System.out.println("ChangeEvent on Slider. New value is " + noteVal);
            setPacketModels(noteVal);
        }
        //        selectedNoteField.setText(getNoteNames(0, 61)[noteVal]);
    }    

    private void setPacketModels(int noteValue) {
        int newValue;
//        System.out.println("Setting value for ComboBox 0");
//        System.out.println("Running in thread " + Thread.currentThread().getName());
        notePacketModel[0].setPacketIndex(noteValue);
        int newValue0 = notePacketModel[0].get();
//        System.out.println("newValue0 = " + newValue0);

//        System.out.println("Setting value for ComboBox 1");
        notePacketModel[1].setPacketIndex(noteValue);
        int newValue1 = notePacketModel[1].get();
//        System.out.println("newValue1 = " + newValue1);

        notePacketModel[2].setPacketIndex(noteValue);
        int newValue2 = notePacketModel[2].get();

        notePacketModel[3].setPacketIndex(noteValue);
        int newValue3 = notePacketModel[3].get();

        notePacketModel[4].setPacketIndex(noteValue);
        int newValue4 = notePacketModel[4].get();

        notePacketModel[5].setPacketIndex(noteValue);
        int newValue5 = notePacketModel[5].get();

        notePacketModel[6].setPacketIndex(noteValue);
        int newValue6 = notePacketModel[6].get();
        
        notePacketModel[7].setPacketIndex(noteValue);
        int newValue7 = notePacketModel[7].get();
        
        ((ComboBoxWidget)sysexWidget[0]).setValue(newValue0);
//        ((ComboBoxWidget)sysexWidget[1]).setValue(newValue1);
        ((ScrollBarLookupWidget)sysexWidget[2]).setValue(newValue2);
        ((ScrollBarWidget)sysexWidget[3]).setValue(newValue3);
        ((ScrollBarWidget)sysexWidget[4]).setValue(newValue4);
        ((ScrollBarLookupWidget)sysexWidget[5]).setValue(newValue5);
        ((ComboBoxWidget)sysexWidget[6]).setValue(newValue6);
        ((ComboBoxWidget)sysexWidget[7]).setValue(newValue7);
    }
        
    private void addVoiceWidgets(Patch patch, JPanel panel) {
        notePacketModel[0] = new PacketModel(patch, 36+1, BitModel.BANK_MASK);
        sysexWidget[0] = new ComboBoxWidget("Family", patch,
                                            notePacketModel[0],
                                            new NRPNSender(NRPNSender.NOTE_BANK, 7),
                                            new String[] {
                                                "0--Kick      ", 
                                                "1--Snare     ",
                                                "2--Tom       ",
                                                "3--Hi-Hat    ",
                                                "4--Cymbal    ",
                                                "5--Percussion",
                                                "6--Effects   ",
                                                "7--Random    "
                                            });
        addWidget(panel, sysexWidget[0], ctrlBase++, 0, 1, 1, widgetCount++);
        ((ComboBoxWidget)sysexWidget[0]).addEventListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                familyChanged(e);
            }
	    });

        notePacketModel[1] = new PacketModel(patch, 36+2, BitModel.DRUM_MASK);
        int bankIndex = sysexWidget[0].getValue();
        int listMax = DM5SoundList.DRUM_NAME[bankIndex].length - 1;
        sysexWidget[1] = new UpdateableComboBoxWidget("Drum Sound", patch,
                                                      notePacketModel[1],
                                                      new NRPNSender(NRPNSender.NOTE_SOUND, listMax),
//                                                      new DummySender(0, 93, 0),
                                                      DM5SoundList.DRUM_NAME[bankIndex]);
        addWidget(panel, sysexWidget[1], ctrlBase++, 0, 1, 1, widgetCount++);
    }

    public void familyChanged(ActionEvent e) {
        JComboBox cb = (JComboBox)e.getSource();
        int selectedIndex = cb.getSelectedIndex();
        ((UpdateableComboBoxWidget)sysexWidget[1]).updateComboBoxWidgetList(selectedIndex);
    }
    
    private void addParmWidgets(Patch patch, JPanel panel) {
        notePacketModel[2] = new PacketModel(patch, 36+4, BitModel.CRSE_TUNE_MASK);
        sysexWidget[2] = new ScrollBarLookupWidget("Coarse Tune", patch, 0, 7, -1,
                                                   notePacketModel[2],
                                                   new NRPNSender(NRPNSender.NOTE_COARSE_TUNE, 7),
                                                   new String[] {"-4", "-3", "-2", "-1", "0", "1", "2", "3"});
        addWidget(panel, sysexWidget[2], ctrlBase++, 0, 1, 1, widgetCount++);
        
        notePacketModel[3] = new PacketModel(patch, 36+3, BitModel.FINE_TUNE_MASK);
        sysexWidget[3] = new ScrollBarWidget("Fine Tune",
                                             patch, 0, 99, 0,
                                             notePacketModel[3],
                                             new NRPNSender(NRPNSender.NOTE_FINE_TUNE, 99));
        addWidget(panel, sysexWidget[3], ctrlBase++, 0, 1, 1, widgetCount++);
        
        notePacketModel[4] = new PacketModel(patch, 36+0, BitModel.VOL_MASK);
        sysexWidget[4] = new ScrollBarWidget("Volume",
                                             patch, 0, 99, 0,
                                             notePacketModel[4],
                                             new NRPNSender(NRPNSender.NOTE_VOLUME, 99));
        addWidget(panel, sysexWidget[4], ctrlBase++, 0, 1, 1, widgetCount++);
        
        notePacketModel[5] = new PacketModel(patch, 36+1, BitModel.PAN_MASK);
        sysexWidget[5] = new ScrollBarLookupWidget("Pan", patch, 0, 6, -1,
                                                   notePacketModel[5],
                                                   new NRPNSender(NRPNSender.NOTE_PAN, 6),
                                                   new String[] {"-3", "-2", "-1", "0", "1", "2", "3"});
        addWidget(panel,sysexWidget[5], ctrlBase++, 0, 1, 1, widgetCount++);
    }

    private void addOutputWidgets(Patch patch, JPanel panel) {
        notePacketModel[6] = new PacketModel(patch, 36+1, BitModel.OUTP_MASK);
        sysexWidget[6] = new ComboBoxWidget("Output", patch,
                                            notePacketModel[6],
                                            new NRPNSender(NRPNSender.NOTE_OUTPUT, 1),
                                            new String[] {"Main", "Aux"});
        addWidget(panel, sysexWidget[6], ctrlBase++, 0, 1, 1, widgetCount++);

        notePacketModel[7] = new PacketModel(patch, 36+4, BitModel.GROUP_MASK);
        sysexWidget[7] = new ComboBoxWidget("Group", patch,
                                            notePacketModel[7],
                                            new NRPNSender(NRPNSender.NOTE_GROUP, 3),
                                            new String[] {"Multi", "Single", "Group 1", "Group 2"});
        addWidget(panel, sysexWidget[7], ctrlBase++, 0, 1, 1, widgetCount++);
    }
    
    private void addTriggerWidgets(Patch patch, JPanel panel) {
        rootNoteWidget = new ScrollBarLookupWidget("Root Note", patch, 0, 67, -1,
                                                   new BitModel(patch, 21, BitModel.ROOT_NOTE_MASK),
                                                   new NRPNSender(NRPNSender.SET_ROOT_NOTE, NRPNSender.CC_MAP_0_67),
                                                   getNoteNames(0, 68));
        addWidget(panel,
                  rootNoteWidget,
                  ctrlBase++, 0,
                  1, 1,
                  widgetCount++);
        rootNoteWidget.addEventListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                rootNoteChangeListener(e);
            }
	    });
                

        closeNoteWidget =  new UpdateableScrollBarLookupWidget("Closing Note", patch, 0, 60, -1,
                                                               new BitModel(patch, 22, BitModel.NOTE_MASK),
                                                               new NRPNSender(NRPNSender.FW_CLOSE_NOTE, NRPNSender.CC_MAP_0_60),
                                                               rootNoteWidget.getValue(), 61);
        addWidget(panel,
                  closeNoteWidget,
                  ctrlBase++, 0,
                  1, 1,
                  widgetCount++);
        

        heldNoteWidget = new UpdateableScrollBarLookupWidget("Held Note", patch, 0, 60, -1,
                                                             new BitModel(patch, 23, BitModel.NOTE_MASK),
                                                             new NRPNSender(NRPNSender.FW_HELD_NOTE, NRPNSender.CC_MAP_0_60),
                                                             rootNoteWidget.getValue(), 61);
        addWidget(panel,
                  heldNoteWidget,
                  ctrlBase++, 0,
                  1, 1,
                  widgetCount++);
        
        for (int i = 0; i < 12; i++) { 
            triggerWidget[i] = new UpdateableScrollBarLookupWidget("Trigger " + String.valueOf(i + 1),
                                                                   patch, 0, 60, 80,
                                                                   new BitModel(patch, 24 + i, BitModel.NOTE_MASK),
                                                                   new TrigSender(i, TrigSender.TR_NOTE_NBR, 60),
                                                                   rootNoteWidget.getValue(), 61);
            addWidget(panel,
                      triggerWidget[i],
                      ctrlBase++, 0,
                      1, 1,
                      widgetCount++);
        }
    }

    public void rootNoteChangeListener(ChangeEvent e) {
        JSlider sl = (JSlider)e.getSource();
        int noteVal = (int)sl.getValue();
        if (!sl.getValueIsAdjusting()) {            
            closeNoteWidget.updateRootNote(noteVal);
            heldNoteWidget.updateRootNote(noteVal);
            for (int i = 0; i < 12; i++) { 
                triggerWidget[i].updateRootNote(noteVal);
            }
        }
    }
    
    class UpdateableComboBoxWidget extends ComboBoxWidget {
        IParamModel UCBWModel;
        NRPNSender UCBWSender;
        /** <code>min</code> is set to 0. */
        UpdateableComboBoxWidget(String label, IPatch patch,
                                 IParamModel pmodel, ISender sender, Object [] options) {
            super(label, patch, 0, pmodel, sender, options);
            UCBWModel = pmodel;
            UCBWSender = (NRPNSender)sender;
        }
        
        void updateComboBoxWidgetList(int selectedIndex) {
            int newValue = UCBWModel.get();
            int newMax = DM5SoundList.DRUM_NAME[selectedIndex].length - 1;

            UCBWSender.setMax(newMax);
            
            String[] list = DM5SoundList.DRUM_NAME[selectedIndex];
            
            cb.removeAllItems();
            for (int i = 0; i < list.length; i++) {
                cb.addItem(list[i]);
            }

            System.out.println("New Value is " + newValue + ", max value is " + newMax + "##");
            setMax(newMax);
            setValue(Math.min(newValue, newMax));
        }
    }
    
    class UpdateableScrollBarLookupWidget extends ScrollBarLookupWidget {
        int numNotes;
        
        public UpdateableScrollBarLookupWidget(String label, IPatch patch, int min, int max,
                                               int labelWidth,
                                               IParamModel pmodel, ISender sender,
                                               int startValue,
                                               int numNotes) {
            super(label, patch, min, max, labelWidth, pmodel, sender, new String[numNotes]);
            this.numNotes = numNotes;
            updateRootNote(startValue);
        }

        void updateRootNote(int newRootNoteValue) {
            options = getNoteNames(newRootNoteValue, numNotes);
            int v = slider.getValue();
            text.setText(options[v]);
        }
            
        private String[] getNoteNames(int startValue, int numNotes) {
            String noteTitle[] = new String[numNotes];
            for (int i = 0; i < numNotes; i++) {
                noteTitle[i] = String.valueOf(i + startValue) + "/" + noteNames[(i + startValue) % 12] + String.valueOf(((i + startValue) / 12) - 2);
            }
            return noteTitle;
        }
        
        void updateNoteList() {
            
        }
    }
}

/** The BitModel class allows a control to set the individual bits in a byte
* of the patch.sysex record. This is used when a byte of the sysex record
* contains several distinct parameters for the device. Masks are provided for 
* the DM5 to define which bits are used for a given parameter.
*/
class BitModel extends ParamModel {
    static final byte ROOT_NOTE_MASK = (byte)0x7F;  //0111 1111
    static final byte NOTE_MASK      = (byte)0x3F;  //0011 1111
    static final byte VOL_MASK       = (byte)0x7F;  //0111 1111
    static final byte PAN_MASK       = (byte)0x70;  //0111 0000
    static final byte OUTP_MASK      = (byte)0x08;  //0000 1000
    static final byte BANK_MASK      = (byte)0x07;  //0000 0111
    static final byte DRUM_MASK      = (byte)0x7F;  //0111 1111
    static final byte FINE_TUNE_MASK = (byte)0x7F;  //0111 1111
    static final byte GROUP_MASK     = (byte)0x18;  //0001 1000
    static final byte CRSE_TUNE_MASK = (byte)0x07;  //0000 0111
    
    Patch patch;
    int ofs;
    byte mask;
//    int bitPos;
    int power = 0;
    
    /** Constructs a BitModel. */
    BitModel(Patch p, int offset, byte mask) {
        this.patch = p;
        this.ofs = offset;
        this.mask = mask;
        
        int bitPos = 1;
        int rem = 0;
        while (rem == 0) { 
            bitPos *= 2;
            power += 1;
            rem = mask % bitPos;
        }
        power -= 1;
    }
    
    /** Updates the bits defined by mask within the byte in the sysex record 
        * defined by offset with the value supplied by i.
        */
    public void set(int i) {
        patch.sysex[ofs] = (byte)((patch.sysex[ofs] & (~mask)) | ((i << power) & mask));
    }
    
    /** Gets the value of the bits defined by mask within the byte in the
        * sysex record defined by offset and returns the value as in int.
        */
    public int get() {
        return ((patch.sysex[ofs] & mask) >> power);
    }
}

class PacketModel extends BitModel {
    private final static int PACKET_SIZE = 5;
    int packetIndex;
    
    /** Constructs a PacketModel. */
    PacketModel(Patch p, int offset, byte mask, int packetIndex) {
        super(p, offset, mask);
        this.packetIndex = packetIndex;
    }
    
    /** Constructs a PacketModel. */
    PacketModel(Patch p, int offset, byte mask) {
        super(p, offset, mask);
        this.packetIndex = 0;
    }
    
    /** Constructs a PacketModel. */
    PacketModel(Patch p, int offset, byte mask, byte test) {
        this(p, offset, mask, 0);
    }
    
    /** Updates the bits defined by mask within the byte in the sysex record 
        * defined by offset with the value supplied by i.
        */
    public void set(int i) {
 //       System.out.println("PacketModel.set was called for offset " + ofs + " setting value to " + i);
        patch.sysex[(PACKET_SIZE * packetIndex) + ofs] = (byte)((patch.sysex[(PACKET_SIZE * packetIndex) + ofs] & (~mask)) | ((i << power) & mask));
    }
    
    /** Gets the value of the bits defined by mask within the byte in the
        * sysex record defined by offset and returns the value as in int.
        */
    public int get() {
        int returnVal =  ((patch.sysex[(PACKET_SIZE * packetIndex) + ofs] & mask) >> power);
//        System.out.println("PacketModel.get was called for offset " + ofs + " getting value " + returnVal);
        return returnVal;
    }
    
    void setPacketIndex(int packetIndex) {
//        System.out.println("PacketModel.setPacketIndex was called for offset " + ofs + " new packetIndex is " + packetIndex);
        this.packetIndex = packetIndex;
    }
}

//class DM5PatchSender extends SysexSender {
class DM5PatchSender implements SysexWidget.ISender {
    Patch patch;
    DM5PatchSender(Patch p) {
        patch = p;
    }

//    public byte[] generate (int value) {
//        patch.sysex[0] = (byte)0xF0;
//        ((AlesisDM5SgSetDriver)(patch.getDriver())).calculateChecksum(patch);
//        return patch.sysex;
//    }

    public void send (IPatchDriver driver, int value) {
        patch.sysex[0] = (byte)0xF0;
//        ((AlesisDM5SgSetDriver)(patch.getDriver())).calculateChecksum(patch);
        ((AlesisDM5SgSetDriver)driver).calculateChecksum(patch);
        SysexMessage sgSetMessage = new SysexMessage();
        try {
            sgSetMessage.setMessage(patch.sysex, patch.sysex.length);
            driver.send(sgSetMessage);
        } catch  (InvalidMidiDataException e) {
            ErrorMsg.reportStatus(e);
        }
    }
}

class DummySender implements SysexWidget.ISender {
    private final static int maxTrigNum = 11;
    private static int lastTrigNum = 99;
    private int param;
    private int trigNum;
    private int max;
    
    DummySender(int trigNum, int param, int max) {
        this.trigNum = trigNum;
        this.param = param;
        this.max = max;
    }
    
    DummySender() {
    }
    
    public void send(IPatchDriver driver, int value) {
    }
}

class NRPNSender implements SysexWidget.ISender {
    
    final static int NOTE_BANK         = 0x08;
    final static int NOTE_SOUND        = 0x09;
    final static int NOTE_COARSE_TUNE  = 0x0A;
    final static int NOTE_FINE_TUNE    = 0x0B;
    final static int NOTE_VOLUME       = 0x0C;
    final static int NOTE_PAN          = 0x0D;
    final static int NOTE_OUTPUT       = 0x0E;
    final static int NOTE_GROUP        = 0x0F;
    final static int SET_ROOT_NOTE     = 0x10;
    final static int PREVIEW_NOTE      = 0x19;
    final static int FW_CLOSE_NOTE     = 0x1C;
    final static int FW_HELD_NOTE      = 0x1D;
    
    private final static int NRPN_MSB          = 99;
    private final static int NRPN_LSB          = 98;
    private final static int DATA_ENTRY_MSB    = 6;
    
    private int param;
    private int max;
    private int ccMap[];
    
    static final int[] CC_MAP_0_60 = new int[] {
        1,   4,   5,   7,   9,  11,  13,  16,  17,  19,  22,  24,
        26,  28,  30,  32,  34,  36,  38,  40,  42,  45,  47,  49,
        51,  53,  55,  57,  59,  61,  64,  66,  68,  70,  72,  74, 
        77,  78,  80,  82,  84,  87,  89,  91,  93,  95,  97,  99,
        101, 103, 105, 108, 110, 112, 114, 116, 118, 120, 122, 124,
        126   
    }; 

    static final int[] CC_MAP_0_67 = new int[] {
        0,   2,   4,   6,   8,   10,  12,  14,  16,  17,  19,  21,
        23,  25,  27,  29,  31,  32,  34,  36,  38,  40,  42,  44,
        46,  48,  49,  51,  53,  55,  57,  59,  61,  63,  64,  66,
        68,  70,  72,  74,  76,  78,  80,  81,  83,  85,  87,  89,
        91,  93,  95,  96,  98,  100, 102, 104, 106, 108, 110, 112,
        113, 115, 117, 119, 121, 123, 125, 127
    }; 
    
    public NRPNSender(int param, int ccMap[]) {
        this.param = param;
        this.max = 0;
        this.ccMap = ccMap;
    }
    
    public NRPNSender(int param, int max) {
        this.param = param;
        this.max = max;
        this.ccMap = null;
    }
    
    public void setMax(int max) {
        this.max = max;
    }
    
    public void send(IPatchDriver driver, int value) {
        try {
            driver.send(newControlChange(driver, NRPN_MSB, 0));  // Send NRPN MSB
            driver.send(newControlChange(driver, NRPN_LSB, param));  // Command to select the parameter
            if (max == 0) {
                driver.send(newControlChange(driver, DATA_ENTRY_MSB, ccMap[value]));  // Set the NRPN value using the table
            } else {
                driver.send(newControlChange(driver, DATA_ENTRY_MSB, (value * 127) / max));  // Calculate the NRPN value
            }
        } catch (InvalidMidiDataException e) {
            ErrorMsg.reportStatus(e);
        }

        try {
            Thread.sleep (50);
        } catch (Exception e) {}
    }
    
    private ShortMessage newControlChange(IPatchDriver driver, int controlNumber, int value) throws InvalidMidiDataException {
        ShortMessage ccMessage = new ShortMessage();
        ccMessage.setMessage(ShortMessage.CONTROL_CHANGE, driver.getDevice().getChannel() - 1, controlNumber, value);
        return ccMessage;
    }
}

