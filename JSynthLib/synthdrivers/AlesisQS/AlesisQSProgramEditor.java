/*******************************************************************************

        File:           AlesisQSProgramEditor.java
        Author:         Chris Halls
        Copyright:      Copyright (c) 2003 Chris Halls <halls@debian.org>

        Alesis QS Series single Program editor

        This library is free software; you can redistribute it and/or modify it
        under the terms of the GNU Lesser General Public License as published
        by the Free Software Foundation; either version 2.1 of the License, or
        (at your option) any later version.

        This library is distributed in the hope that it will be useful, but
        WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
        or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
        License for more details.

        You should have received a copy of the GNU Lesser General Public License
        along with this library; if not, write to the Free Software Foundation, Inc.,
        59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

        Change History:
        2003-12-12      Development version
*******************************************************************************/


package synthdrivers.AlesisQS;
import core.*;
import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Single program editor
 * @author Chris Halls
 * @version 0.1
 */
class AlesisQSProgramEditor extends PatchEditorFrame
{
  public AlesisQSProgramEditor(Patch patch)
  {
    super ("Alesis QS series Program Editor",patch);

    JPanel topPane = new JPanel();
    topPane.setLayout(new GridBagLayout());
    addWidget(topPane,new PatchNameWidget("Name",patch),0,0,1,1,0);
    gbc.gridx=0;gbc.gridy=0;gbc.gridwidth=1;gbc.gridheight=1;
    scrollPane.add(topPane, gbc);

    for(int i=1; i<5; i++) {
        String name = new String("Sound ");
        name += i;
        //120.	Sound enable	            16	    0	    3	0	    1	1	    84:3
        addWidget(scrollPane,new CheckBoxWidget(name, patch,
                                                new SoundModel(patch,i-1,1,84,3),new ProgSender(i-1,16,0,3)),
                                                i+1,0,1,1,i*-1);
    }

    JTabbedPane soundPane=new JTabbedPane();
    for (int i=0; i< 4; i++)
    {
        int controlIdx=0;
        int yPos=0;

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        soundPane.addTab("Sound "+(i+1),panel);

        //#	Parameter name	                Func	Page	Pot	Offset	Lim bits	bit address
        // 0.	Keyboard / drum mode (=0)	16	    0	    0	0	    1	1	    0:0
        // TODO

        JPanel voicePane=new voicePanel(patch, i);
        gbc.gridx=0;gbc.gridy=0;gbc.gridwidth=1;gbc.gridheight=1;
        panel.add(voicePane, gbc);

        controlPanel levelPane=new levelPanel(patch,i);
        gbc.gridx=0;gbc.gridy=1;gbc.gridwidth=1;gbc.gridheight=1;
        panel.add(levelPane, gbc);

        controlPanel pitchPane=new pitchPanel(patch,i);
        gbc.gridx=0;gbc.gridy=2;gbc.gridwidth=1;gbc.gridheight=2;
        panel.add(pitchPane, gbc);

        JPanel filterPane=new filterPanel(patch, i);
        gbc.gridx=0;gbc.gridy=4;gbc.gridwidth=1;gbc.gridheight=2;
        panel.add(filterPane, gbc);

        JPanel amplitudePane=new amplitudePanel(patch, i);
        gbc.gridx=0;gbc.gridy=6;gbc.gridwidth=1;gbc.gridheight=1;
        panel.add(amplitudePane, gbc);

        JPanel rangePane=new rangePanel(patch, i);
        gbc.gridx=0;gbc.gridy=7;gbc.gridwidth=1;gbc.gridheight=1;
        panel.add(rangePane, gbc);

        // 76.	Sound pitch env attack	12	0	0	0	99	7	53:0-52:2
        JPanel pitchEnvPane=new envelopePanel("Pitch envelope", patch, i, 12, 53, 0);
        gbc.gridx=1;gbc.gridy=2;gbc.gridwidth=2;gbc.gridheight=2;
        panel.add(pitchEnvPane, gbc);

        // 87.	Sound filter env attack	13	0	0	0	99	7	60:5-59:7
        JPanel filtEnvPane=new envelopePanel("Filter envelope", patch, i, 13, 60, 5);
        gbc.gridx=1;gbc.gridy=4;gbc.gridwidth=2;gbc.gridheight=2;
        panel.add(filtEnvPane, gbc);

        // 98.	Sound amp env attack	14	0	0	0	99	7	68:2-67:4
        JPanel ampEnvPane=new envelopePanel("Amplitude envelope", patch, i, 14, 68, 2);
        gbc.gridx=1;gbc.gridy=6;gbc.gridwidth=2;gbc.gridheight=2;
        panel.add(ampEnvPane, gbc);

    }
    gbc.gridx=0;gbc.gridy=1;gbc.gridwidth=6;gbc.gridheight=2;
    scrollPane.add(soundPane,gbc);
    pack();
    show();


  }

  class controlPanel extends JPanel {
    public controlPanel(String name) {
        setLayout(new GridBagLayout());
        setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
                                   name,TitledBorder.CENTER,
                                   TitledBorder.CENTER));
    }
  }

//   class xPanel extends controlPanel {
//     public xPanel(Patch patch, int snd) {
//         super("xx");
//         int controlIdx=0, yPos=0;
//     }
//
//   }

  class voicePanel extends controlPanel {
    public voicePanel(Patch patch, int snd) {
        super("Voice");
        int controlIdx=0, yPos=0;
        // 1.	Sample group	            0	    0	    0	0	    47	6	    0:6-0:1
        ComboBoxWidget groupBox = new ComboBoxWidget("Group",patch,
                                                new SoundModel(patch,snd,6,0,6),new ProgSender(snd,0,0,0),
                                                QSConstants.VOICE_GROUP_NAMES_KYBD);
        addWidget(this, groupBox, 0, yPos, 2, 1, controlIdx++);

        // 2.	Sample number	            0	    0	    2	0	    127	7	    1:5-0:7
        ComboBoxWidget sampleBox = new ComboBoxWidget("Sample",patch,1,
                                                new SoundModel(patch,snd,7,1,5),new ProgSender(snd,0,0,2),
                                                QSConstants.VOICE_NAMES_KYBD[0]);
        addWidget(this, sampleBox, 2, yPos, 2, 1, controlIdx++);

        groupBox.cb.addActionListener( new GroupActionListener(groupBox, sampleBox) );

    }

  }
  class levelPanel extends controlPanel {
    public levelPanel(Patch patch, int snd) {
        super("Level");
        int controlIdx=0, yPos=0;
        // 3.	Sound volume	            1	    0	    0	0	    99	7	    2:4-1:6
        addWidget(this,new ScrollBarWidget("Volume",patch,0,99,0,
                                            new SoundModel(patch,snd,7,2,4),new ProgSender(snd,1,0,0)),
                                            0,yPos,3,1,controlIdx++);

        // 4.	Sound pan	                1	    0	    1	0	    6	3	    2:7-2:5
        addWidget(this,new ScrollBarWidget("Pan",patch,0,6,-3,
                                            new SoundModel(patch,snd,3,2,7),new ProgSender(snd,1,0,1)),
                                            0,++yPos,3,1,controlIdx++);

        // 6.	Sound effect level	        2	    0	    0	0	    99	7	    4:0-3:2
        addWidget(this,new ScrollBarWidget("Effect level",patch,0,99,0,
                                            new SoundModel(patch,snd,7,4,0),new ProgSender(snd,2,0,0)),
                                            0,++yPos,3,1,controlIdx++);

        // 5.	Sound output	            1	    0	    2	0	    2	2	    3:1-3:0
        addWidget(this,new ComboBoxWidget("Ouput",patch,
                                           new SoundModel(patch,snd,2,3,1),new ProgSender(snd,1,0,2),
                                           new String [] {"L/R","Aux","Off"}),
                                           0,++yPos,1,1,controlIdx++);

        // 7.	Sound effect bus	        2	    0	    1	0	    3	2	    4:2-4:1
        addWidget(this,new ComboBoxWidget("Effect bus",patch,
                                           new SoundModel(patch,snd,2,4,2),new ProgSender(snd,2,0,1),
                                           new String [] {"1","2","3","4"}),
                                           1,yPos,1,1,controlIdx++);

    }

  }

  class pitchPanel extends controlPanel {
    public pitchPanel(Patch patch, int snd) {
        super("Pitch");
        int controlIdx=0, yPos=0;

        // 8.	Sound pitch semitone	    3	    0	    0	-24	    49	6	    5:0-4:3
        addWidget(this,new ScrollBarWidget("Semitone",patch,-24,24,0,
                                            new SoundModel(patch,snd,6,5,0,-24),new ProgSender(snd,3,0,0)),
                                            0,yPos,2,1,controlIdx++);

        // 9.	Sound pitch detune	        3	    0	    2	-99	    199	8	    6:0-5:1
        addWidget(this,new ScrollBarWidget("Detune %",patch,-99,99,0,
                                            new SoundModel(patch,snd,8,6,0,-99),new ProgSender(snd,3,0,2)),
                                            0,++yPos,2,1,controlIdx++);

        // 10.	Sound pitch detune type	    3	    0	    3	0	    1	1	    6:1
        addWidget(this,new ComboBoxWidget("Detune type",patch,
                                        new SoundModel(patch,snd,1,6,1),new ProgSender(snd,3,0,3),
                                        new String [] {"Normal","Equal"}),
                                        0,++yPos,1,1,controlIdx++);

        // 11.	Sound pitch wheel mod	    3	    1	    0	0	    12	4	    6:5-6:2
        addWidget(this,new ScrollBarWidget("Pitchwheel range",patch,0,12,0,
                                            new SoundModel(patch,snd,4,6,5),new ProgSender(snd,3,1,0)),
                                            0,++yPos,2,1,controlIdx++);

        // 12.	Sound pitch aftertouch mod	3	    1	    1	-99	    199	8	    7:5-6:6
        addWidget(this,new ScrollBarWidget("Aftertouch depth",patch,-99,99,0,
                                            new SoundModel(patch,snd,8,7,5,-99),new ProgSender(snd,3,1,1)),
                                            0,++yPos,2,1,controlIdx++);

        // 13.	Sound pitch lfo mod	        3	    1	    2	-99	    199	8	    8:5-7:6
        addWidget(this,new ScrollBarWidget("Pitch LFO depth",patch,-99,99,0,
                                            new SoundModel(patch,snd,8,8,5,-99),new ProgSender(snd,3,1,2)),
                                            0,++yPos,2,1,controlIdx++);

        // 14.	Sound pitch env mod	        3	    1	    3	-99	    199	8	    9:5-8:6
        addWidget(this,new ScrollBarWidget("Pitch Envelope depth",patch,-99,99,0,
                                            new SoundModel(patch,snd,8,9,5,-99),new ProgSender(snd,3,1,3)),
                                            0,++yPos,2,1,controlIdx++);

        // 16.	Sound portamento rate	    3	    2	    2	0	    99	7	    10:6-10:0
        addWidget(this,new ScrollBarWidget("Portamento rate",patch,0,99,0,
                                            new SoundModel(patch,snd,7,10,6),new ProgSender(snd,3,2,2)),
                                            0,++yPos,2,1,controlIdx++);

        // 15.	Sound portamento mode	    3	    2	    0	0	    2	2	    9:7-9:6
        addWidget(this,new ComboBoxWidget("Portamento mode",patch,
                                        new SoundModel(patch,snd,2,9,7),new ProgSender(snd,3,2,0),
                                        new String [] {"Exponential", "Linear", "1 Speed"}),
                                        0,++yPos,1,1,controlIdx++);

        // 17.	Sound key mode	            3	    2	    3	0	    2	2	    11:0-10:7
        addWidget(this,new ComboBoxWidget("Keyboard mode",patch,
                                        new SoundModel(patch,snd,2,11,0),new ProgSender(snd,3,2,1),
                                        new String [] {"Mono", "Poly", "1-Pitch", "1-PMono"}),
                                        1,yPos,1,1,controlIdx++);

    }

  }

  class filterPanel extends controlPanel {
    public filterPanel(Patch patch, int snd) {
        super("Filter");
        int controlIdx=0, yPos=0;
        // 18.	Sound filter frequency	    4	    0	    0	0	    99	7	    11:7-11:1
        addWidget(this,new ScrollBarWidget("Filter freq",patch,0,99,0,
                                            new SoundModel(patch,snd,7,11,7),new ProgSender(snd,4,0,0)),
                                            0,++yPos,2,1,controlIdx++);

        // 19.	Sound filter keyboard track	4	    0	    1	0	    1	1	    12:0
        addWidget(this,new CheckBoxWidget("Keyboard track",patch,
                                            new SoundModel(patch,snd,1,12,0),new ProgSender(snd,4,0,1)),
                                            0,++yPos,1,1,-1);

        // 20.	Sound filter velocity mod	4	    0	    3	-99	    199	8	    13:0-12:1
        addWidget(this,new ScrollBarWidget("Velocity mod",patch,-99,99,0,
                                            new SoundModel(patch,snd,8,13,0,-99),new ProgSender(snd,4,0,3)),
                                            0,++yPos,2,1,controlIdx++);

        // 21.	Sound filter pitch wheel mod 4  	1	    0	-99	    199	8	    14:0-13:1
        addWidget(this,new ScrollBarWidget("ModWheel depth",patch,-99,99,0,
                                            new SoundModel(patch,snd,8,14,0,-99),new ProgSender(snd,4,1,0)),
                                            0,++yPos,2,1,controlIdx++);

        // 22.	Sound filter aftertouch mod	4	    1	    1	-99	    199	8	    15:0-14:1
        addWidget(this,new ScrollBarWidget("Aftertouch depth",patch,-99,99,0,
                                            new SoundModel(patch,snd,8,15,0,-99),new ProgSender(snd,4,1,1)),
                                            0,++yPos,2,1,controlIdx++);

        // 23.	Sound filter lfo mod	    4	    1	    2	-99	    199	8	    16:0-15:1
        addWidget(this,new ScrollBarWidget("LFO depth",patch,-99,99,0,
                                            new SoundModel(patch,snd,8,16,0,-99),new ProgSender(snd,4,1,2)),
                                            0,++yPos,2,1,controlIdx++);

        // 24.	Sound filter env mod	    4	    1	    3	-99	    199	8	    17:0-16:1
        addWidget(this,new ScrollBarWidget("Envelope depth",patch,-99,99,0,
                                            new SoundModel(patch,snd,8,17,0,-99),new ProgSender(snd,4,1,3)),
                                            0,++yPos,2,1,controlIdx++);
    }

  }

  class amplitudePanel extends controlPanel {
    public amplitudePanel(Patch patch, int snd) {
        super("Amplitude");
        int controlIdx=0, yPos=0;

        // 25.	Sound amp velocity curve	5	    0	    0	0	    12	4	    17:4-17:1
        addWidget(this,new ComboBoxWidget("Velocity curve",patch,
                                           new SoundModel(patch,snd,4,17,4),new ProgSender(snd,5,0,0),
                                           new String [] {"Linear", "Inverted", "Maximum", "Minimum",
                                                          "1of2", "2of2",
                                                          "1of3", "2of3", "3of3",
                                                          "1of4", "2of4", "3of4", "4of4"}),
                                           1,yPos,1,1,controlIdx++);

        // 26.	Sound amp aftertouch mod	5	    0	    1	-99	    199	8	    18:4-17:5
        addWidget(this,new ScrollBarWidget("Aftertouch depth",patch,-99,99,0,
                                            new SoundModel(patch,snd,8,18,4,-99),new ProgSender(snd,5,0,1)),
                                            0,++yPos,2,1,controlIdx++);

        // 27.	Sound amp alfo mod	        5	    0	    2	-99	    199	8	    19:4-18:5
        addWidget(this,new ScrollBarWidget("Amp LFO depth",patch,-99,99,0,
                                            new SoundModel(patch,snd,8,19,4,-99),new ProgSender(snd,5,0,2)),
                                            0,++yPos,2,1,controlIdx++);
    }

  }

  class rangePanel extends controlPanel {
    public rangePanel(Patch patch, int snd) {
        super("Range");
        int controlIdx=0, yPos=0;

        // 28.	Sound low note limit	    6	    0	    0	0	    127	7	    20:3-19:5
        addWidget(this,new ScrollBarLookupWidget("Lower limit",patch,0,127,
                                            new SoundModel(patch,snd,7,20,3),new ProgSender(snd,6,0,0),
                                            QSConstants.NOTE_NAMES),
                                            0,++yPos,2,1,controlIdx++);

        // 29.	Sound high note limit	    6	    0	    1	0	    127	7	    21:2-20:4
        addWidget(this,new ScrollBarLookupWidget("Upper limit",patch,0,127,
                                            new SoundModel(patch,snd,7,21,2),new ProgSender(snd,6,0,1),
                                            QSConstants.NOTE_NAMES),
                                            0,++yPos,2,1,controlIdx++);

        // 30.	Sound overlap	            6	    0	    2	0	    99	7	    22:1-21:3
        addWidget(this,new ScrollBarWidget("Sound overlap",patch,0,99,0,
                                            new SoundModel(patch,snd,7,21,1),new ProgSender(snd,6,0,2)),
                                            0,++yPos,2,1,controlIdx++);
    }

  }

  /**
   * Create a panel containing envelope controls
   * for pitch, filter and amplitude envelopes
   */
  class envelopePanel extends controlPanel {
    /**
     * Create panel
     * @param snd Sound number (0-3)
     * @param Func Function number for direct parameter editing
     * @param msByte Starting byte of first parameter (attack)
     * @param msBit Starting bit of attack parameter
     */
    public envelopePanel(String name, Patch p, int snd, int Func, int msByte, int msBit) {
        super(name);
        int ofs = (msByte-53)*8 + msBit;
        int controlIdx=0, yPos=0;
        // 76.	Sound pitch env attack	        12	0	0	0	99	7	53:0-52:2
        // 77.	Sound pitch env decay	        12	0	1	0	100	7	53:7-53:1
        // 78.	Sound pitch env sustain	        12	0	2	0	99	7	54:6-54:0
        // 79.	Sound pitch env release	        12	0	3	0	99	7	55:5-54:7
        // 80.	Sound pitch env delay	        12	1	0	0	99	7	56:4-55:6
        // 81.	Sound pitch env sustain decay	12	1	1	0	99	7	57:3-56:5

        addWidget(this,
                    new EnvelopeWidget("Envelope", p,
                            new EnvelopeNode[] {
                                new EnvelopeNode(0, 100,
                                        new SoundModel(p,snd,7,56,4+ofs),
                                        0,0,null,
                                        1,false,
                                        new ProgSender(snd,12,1,0),null,
                                        "Delay",null),
                                new EnvelopeNode(0,99,
                                        new SoundModel(p,snd,7,53,ofs),
                                        99,99,null,
                                        0, false,
                                        new ProgSender(snd,12,0,0),null,
                                        "Attack", null),
                                new EnvelopeNode(0,99,
                                        new SoundModel(p,snd,7,53,7+ofs),
                                        0,99,
                                        new SoundModel(p,snd,7,54,6+ofs),
                                        50, false,
                                        new ProgSender(snd,12,0,1),
                                        new ProgSender(snd,12,0,2),
                                        "Decay", "Sustain"),
                                new EnvelopeNode(50,50,null,
                                        0,99,
                                        new SoundModel(p,snd,7,57,3+ofs),
                                        0, false,
                                        null, new ProgSender(snd,12,1,1),
                                        null, "Sustain decay"),
                                new EnvelopeNode(0,99,
                                        new SoundModel(p,snd,7,55,5+ofs),
                                        0,0,null,
                                        0, false,
                                        new ProgSender(snd,12,0,3),null,
                                        "Release", null),
                            }),
                            0,++yPos,3,1,controlIdx++);

        // 82.	Sound pitch env trig type	    12	1	3	0	3	2	57:5-57:4
        addWidget(this,new ComboBoxWidget("Velocity curve",p,
                                           new SoundModel(p,snd,2,57,5),new ProgSender(snd,12,1,3),
                                           new String [] {"Normal", "Freerun", "Reset", "Reset-Freerun"}),
                                           0,++yPos,1,1,controlIdx++);
        // 83.	Sound pitch env time track	    12	2	0	0	1	1	57:6
        addWidget(this,new CheckBoxWidget("Time Track", p,
                                            new SoundModel(p,snd,1,57,6),new ProgSender(snd,12,2,0)),
                                           1,yPos,1,1,-1);

        // 84.	Sound pitch env sustain pedal	12	2	1	0	1	1	57:7
        addWidget(this,new CheckBoxWidget("Sustain Pedal", p,
                                            new SoundModel(p,snd,1,57,7),new ProgSender(snd,12,2,1)),
                                           2,yPos,1,1,-1);

        // 85.	Sound pitch env level	        12	2	2	0	99	7	58:6-58:0
        addWidget(this,new ScrollBarWidget("Level",p,0,99,0,
                                            new SoundModel(p,snd,7,58,6),new ProgSender(snd,12,2,2)),
                                            0,++yPos,3,1,controlIdx++);

        // 86.	Sound pitch env velocity mod	12	2	3	-99	199	8	59:6-58:7
        addWidget(this,new ScrollBarWidget("Velocity modulation",p,-99,99,0,
                                            new SoundModel(p,snd,8,59,6,-99),new ProgSender(snd,12,2,3)),
                                            0,++yPos,3,1,controlIdx++);

    }

  }
}

class GroupActionListener implements ActionListener {
    ComboBoxWidget groupBox, sampleBox;
    public GroupActionListener(ComboBoxWidget group, ComboBoxWidget sample) {
        groupBox = group;
        sampleBox = sample;
        updateSample();
    }
    public void actionPerformed(ActionEvent e) {
        updateSample();
    }
    public void updateSample() {
        int subValue = sampleBox.getValue();
        int groupNum = groupBox.getValue();
        if (groupNum >= QSConstants.VOICE_NAMES_KYBD.length || groupNum < 0)
            groupNum = 0;
        sampleBox.cb.removeAllItems();
        sampleBox.cb.addItem("Off");
        for (int i = 0; i < QSConstants.VOICE_NAMES_KYBD[groupNum].length; i++)
            sampleBox.cb.addItem(QSConstants.VOICE_NAMES_KYBD[groupNum][i]);
        try {
            sampleBox.cb.setSelectedIndex(subValue);
        } catch (Exception e) {}
    }
}

 class SoundModel extends QSParamModel
{
    // Unsigned control
    public SoundModel(Patch p, int sndIndex, int bitSize, int addressByte, int addressBit)
    {
        // Sound 1 starts at 10, 2 at 95, 3 at 180, 4 at 265
        super(p, (10 + sndIndex*85 + addressByte)*8 + addressBit, bitSize, 0);
    }

    // Signed control
    public SoundModel(Patch p, int sndIndex, int bitSize, int addressByte, int addressBit, int offset)
    {
        // Sound 1 starts at 10, 2 at 95, 3 at 180, 4 at 265
        super(p, (10 + sndIndex*85 + addressByte)*8 + addressBit, bitSize, offset);
    }
}

class ProgSender extends MidiEditSender
{
    public ProgSender(int SndIndex, int Function, int Page, int Pot)
    {
        super(2, Function, SndIndex, Page, 0, Pot);
    }

}
