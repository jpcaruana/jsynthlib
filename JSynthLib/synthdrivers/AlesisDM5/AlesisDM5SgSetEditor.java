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
import javax.sound.midi.InvalidMidiDataException;

/** Alesis DM5 Single Drumset Editor
* 
* @author Jeff Weber
*/
public class AlesisDM5SgSetEditor extends PatchEditorFrame implements ChangeListener, ActionListener {
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
    
    private String[] getNoteNames(int numNotes, int startOctave) {
        String noteTitle[] = new String[numNotes];
        for (int i = 0; i < numNotes; i++) {
            noteTitle[i] = String.valueOf(i) + "/" + noteNames[i % 12] + String.valueOf((i / 12) + startOctave);
        }
        return noteTitle;
    }
    
    private void addSelectedNoteWidget(Patch patch, JPanel panel) {
        JPanel selectedNoteWidgetPanel = new JPanel();        
        selectedNoteWidgetPanel.setLayout(new BorderLayout());
        panel.add(selectedNoteWidgetPanel,gbc);

        JSlider selNote = new JSlider(0, 60, 0);
        selNote.addChangeListener(this);

        JLabel label = new JLabel("Selected Note");

        selectedNoteField = new JTextField(getNoteNames(61, 1)[selNote.getValue()], 4);
        selectedNoteField.setEditable(false);

        selNote.setMinimumSize(new Dimension(75, 25));
        selNote.setMaximumSize(new Dimension(125, 25));
        
        selectedNoteWidgetPanel.add(label, BorderLayout.WEST);
        selectedNoteWidgetPanel.add(selNote, BorderLayout.CENTER);
        selectedNoteWidgetPanel.add(selectedNoteField, BorderLayout.EAST);

    }
    
    public void stateChanged(ChangeEvent e) {
        JSlider sl = (JSlider)e.getSource();
        int noteVal = (int)sl.getValue();
        if (!sl.getValueIsAdjusting()) {
//            System.out.println("----------------------------------------------");
//            System.out.println("ChangeEvent on Slider. New value is " + noteVal);
            setPacketModels(noteVal);
        }
        selectedNoteField.setText(getNoteNames(61,1)[noteVal]);
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
                                            new DummySender(0, 3, 99),
                                            new String[] {"0--Kick", "1--Snare", "2--Tom", "3--Hi-Hat", "4--Cymbal", "5--Percussion", "6--Effects", "7--Random"});
        addWidget(panel, sysexWidget[0], ctrlBase++, 0, 1, 1, widgetCount++);
        ((ComboBoxWidget)sysexWidget[0]).addEventListener(this);

        notePacketModel[1] = new PacketModel(patch, 36+2, BitModel.DRUM_MASK);
        sysexWidget[1] = new UpdateableComboBoxWidget("Drum Sound", patch,
                                            notePacketModel[1],
                                            new DummySender(0, 3, 99),
                                            DM5SoundList.DRUM_NAME[sysexWidget[0].getValue()]);
        addWidget(panel, sysexWidget[1], ctrlBase++, 0, 1, 1, widgetCount++);
//        ((UpdateableComboBoxWidget)sysexWidget[1]).cb.setMinimumSize(new Dimension(146, 27));
//        ((UpdateableComboBoxWidget)sysexWidget[1]).cb.setMaximumSize(new Dimension(146, 27));
//        ((UpdateableComboBoxWidget)sysexWidget[1]).cb.setSize(146, 27);
//        Dimension dim = ((UpdateableComboBoxWidget)sysexWidget[1]).cb.getSize();
//        System.out.println("Width = " + dim.getWidth() + "   Height = " + dim.getHeight());
    }

    public void actionPerformed(ActionEvent e) {
        JComboBox cb = (JComboBox)e.getSource();
        int selectedIndex = cb.getSelectedIndex();
//        System.out.println("ActionEvent on ComboBox 0");
//        System.out.println("Running in thread " + Thread.currentThread().getName());
//        Dimension dim = ((UpdateableComboBoxWidget)sysexWidget[1]).cb.getSize();
//        System.out.println("Width = " + dim.getWidth() + "   Height = " + dim.getHeight());

        int newValue = notePacketModel[1].get();
        int newMax = DM5SoundList.DRUM_NAME[selectedIndex].length - 1;
        ((UpdateableComboBoxWidget)sysexWidget[1]).updateComboBoxWidgetList(DM5SoundList.DRUM_NAME[selectedIndex]);
//        ((UpdateableComboBoxWidget)sysexWidget[1]).cb.setSize(146, 27);
//        System.out.println("New Value is " + newValue + ", max value is " + newMax);
        ((ComboBoxWidget)sysexWidget[1]).setMax(newMax);
        ((ComboBoxWidget)sysexWidget[1]).setValue(Math.min(newValue, newMax));
//        System.out.println("ActionEvent on ComboBox 0 has been processed");
    }
    
    private void addParmWidgets(Patch patch, JPanel panel) {
        notePacketModel[2] = new PacketModel(patch, 36+4, BitModel.CRSE_TUNE_MASK);
        sysexWidget[2] = new ScrollBarLookupWidget("Coarse Tune", patch, 0, 7, -1,
                                                   notePacketModel[2],
                                                   new DummySender(0, 3, 99),
                                                   new String[] {"-4", "-3", "-2", "-1", "0", "1", "2", "3"});
        addWidget(panel, sysexWidget[2], ctrlBase++, 0, 1, 1, widgetCount++);
        
        notePacketModel[3] = new PacketModel(patch, 36+3, BitModel.FINE_TUNE_MASK);
        sysexWidget[3] = new ScrollBarWidget("Fine Tune",
                                             patch, 0, 99, 0,
                                             notePacketModel[3],
                                             new DummySender(0, 6, 99));
        addWidget(panel, sysexWidget[3], ctrlBase++, 0, 1, 1, widgetCount++);
        
        notePacketModel[4] = new PacketModel(patch, 36+0, BitModel.VOL_MASK);
        sysexWidget[4] = new ScrollBarWidget("Volume",
                                             patch, 0, 99, 0,
                                             notePacketModel[4],
                                             new DummySender(0, 5, 99));        
        addWidget(panel, sysexWidget[4], ctrlBase++, 0, 1, 1, widgetCount++);
        
        notePacketModel[5] = new PacketModel(patch, 36+1, BitModel.PAN_MASK);
        sysexWidget[5] = new ScrollBarLookupWidget("Pan", patch, 0, 6, -1,
                                                   notePacketModel[5],
                                                   new DummySender(0, 3, 99),
                                                   new String[] {"-3", "-2", "-1", "0", "1", "2", "3"});
        addWidget(panel,sysexWidget[5], ctrlBase++, 0, 1, 1, widgetCount++);
    }

    private void addOutputWidgets(Patch patch, JPanel panel) {
        notePacketModel[6] = new PacketModel(patch, 36+1, BitModel.OUTP_MASK);
        sysexWidget[6] = new ComboBoxWidget("Output", patch,
                                            notePacketModel[6],
                                            new DummySender(0, 3, 99),
                                            new String[] {"Main", "Aux"});
        addWidget(panel, sysexWidget[6], ctrlBase++, 0, 1, 1, widgetCount++);

        notePacketModel[7] = new PacketModel(patch, 36+4, BitModel.GROUP_MASK);
        sysexWidget[7] = new ComboBoxWidget("Group", patch,
                                            notePacketModel[7],
                                            new DummySender(0, 3, 99),
                                            new String[] {"Multi", "Single", "Group 1", "Group 2"});
        addWidget(panel, sysexWidget[7], ctrlBase++, 0, 1, 1, widgetCount++);
    }
    
    private void addTriggerWidgets(Patch patch, JPanel panel) {
        addWidget(panel,
                  new ScrollBarLookupWidget("Root Note", patch, 0, 67, -1,
                        new BitModel(patch, 21, BitModel.ROOT_NOTE_MASK),
                        new DM5PatchSender(patch),
                        getNoteNames(68, -2)),
                  ctrlBase++, 0,
                  1, 1,
                  widgetCount++);
    
        addWidget(panel,
                new ScrollBarLookupWidget("Closing Note", patch, 0, 60, -1,
                        new BitModel(patch, 22, BitModel.NOTE_MASK),
                        new DM5PatchSender(patch),
                        getNoteNames(61, 1)),
                ctrlBase++, 0,
                1, 1,
                widgetCount++);
  
        addWidget(panel,
                new ScrollBarLookupWidget("Held Note", patch, 0, 60, -1,
                      new BitModel(patch, 23, BitModel.NOTE_MASK),
                      new DM5PatchSender(patch),
                      getNoteNames(61, 1)),
                ctrlBase++, 0,
                1, 1,
                widgetCount++);
  
        for (int i = 0; i < 12; i++) { 
            addWidget(panel,
                    new ScrollBarLookupWidget("Trigger " + String.valueOf(i + 1),
                          patch, 0, 60, -1,
                          new BitModel(patch, 24 + i, BitModel.NOTE_MASK),
                          new DM5PatchSender(patch),
                          getNoteNames(61, 1)),
                    ctrlBase++, 0,
                    1, 1,
                    widgetCount++);
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
    
    public void send(IPatchDriver driver, int value) {
/*
        if (trigNum != lastTrigNum) {
            ShortMessage msgMSB = new ShortMessage();
            ShortMessage msgTrigNumLSB = new ShortMessage();
            ShortMessage msgTrigNumDataEntry = new ShortMessage();
            try {
                msgMSB.setMessage(ShortMessage.CONTROL_CHANGE,
                                  driver.getDevice().getChannel() - 1, 99, 0);
                
                msgTrigNumLSB.setMessage(ShortMessage.CONTROL_CHANGE,
                                         driver.getDevice().getChannel() - 1, 98, 0);
                
                msgTrigNumDataEntry.setMessage(ShortMessage.CONTROL_CHANGE,
                                               driver.getDevice().getChannel() - 1, 6, trigNum * 127 / maxTrigNum);
                
                driver.send(msgMSB);
                driver.send(msgTrigNumLSB);
                driver.send(msgTrigNumDataEntry);
            } catch  (InvalidMidiDataException e) {
                ErrorMsg.reportStatus(e);
            }
            lastTrigNum = trigNum;            

            try {
                Thread.sleep (50);
            } catch (Exception e) {}
        }

        ShortMessage msgParamLSB = new ShortMessage();
        ShortMessage msgValueDataEntry = new ShortMessage();
        try {
            msgParamLSB.setMessage(ShortMessage.CONTROL_CHANGE,
                                   driver.getDevice().getChannel() - 1, 98, param);
            
            msgValueDataEntry.setMessage(ShortMessage.CONTROL_CHANGE,
                                         driver.getDevice().getChannel() - 1, 6, value * 127 / max);

            driver.send(msgParamLSB);
            driver.send(msgValueDataEntry);
        } catch (InvalidMidiDataException e) {
            ErrorMsg.reportStatus(e);
        }
*/
    }
}

class UpdateableComboBoxWidget extends ComboBoxWidget {
    /** <code>min</code> is set to 0. */
    UpdateableComboBoxWidget(String label, IPatch patch,
                             IParamModel pmodel, ISender sender, Object [] options) {
        super(label, patch, 0, pmodel, sender, options);
    }
    
    void updateComboBoxWidgetList(String[] list) {
        cb.removeAllItems();
        for (int i = 0; i < list.length; i++) {
            cb.addItem(list[i]);
        }
    }
}
