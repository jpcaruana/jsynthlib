/*
 * JSynthlib - "System Setup" Editor No.2 for Yamaha DX7s
 * =====================================================================
 * @author  Torsten Tittmann
 * file:    YamahaDX7sSystemSetupEditor2.java
 * date:    25.02.2003
 * @version 0.1
 *
 * Copyright (C) 2002-2003  Torsten.Tittmann@t-online.de
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 *
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * CAUTION: This is an experimental driver. It is not tested on a real device yet!
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 *
 * history:
 *         25.02.2003 v0.1: first published release
 *                          
 *
 */

package synthdrivers.YamahaDX7s;
import core.*;
import java.lang.String.*;
import java.text.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;
import javax.swing.event.*;

class YamahaDX7sSystemSetupEditor2 extends PatchEditorFrame 
{
  static final String [] OnOffName          = new String [] {"Off","On"};

  static final String [] BlockSelName       = new String [] {" 1-32","33-64"};

  static final String [] NoteOnOffName      = new String [] {"all","odd","even"};

  static final String [] PrgChgName         = new String [] {"off","normal","Memory"};

  static final String [] MemProtName        = new String [] {"off - off"," on-off", "off- on"," on- on"};

  static final String [] SelectChgName      = new String [] {"0", "1", "2", "3", "4", "5", "6", "7", "8",
                                                             "9","10","11","12","13","14","15"}; 

  static final String [] MidiReceiveName    = new String [] { "0", "1", "2", "3", "4", "5", "6", "7", "8",
                                                              "9","10","11","12","13","14","15","off"}; 


  public YamahaDX7sSystemSetupEditor2(Patch patch)
  {
    super ("Yamaha DX7s \"System Setup\" Editor",patch);
    

    PatchEdit.waitDialog.show();        // Because it needs some time to build up the editor frame


    final DecimalFormat freqFormatter = new DecimalFormat("###0.00");

    JTabbedPane systemPane=new JTabbedPane();
    systemPane.setPreferredSize(new Dimension(800,400));

    JPanel cmnPane = new JPanel();
    cmnPane.setLayout(new GridBagLayout());gbc.weightx=1;gbc.anchor=gbc.EAST;
    JScrollPane cmnScrollPane = new JScrollPane(cmnPane);
    systemPane.addTab("System Setup",cmnScrollPane);

    final ScrollBarWidget MasterTune = new ScrollBarWidget(" "        ,patch,0,127,-64,new ParamModel(patch,16+20), new MasterTuneSender(64) );
    MasterTune.slider.addChangeListener(new ChangeListener()
      {
        public void stateChanged(ChangeEvent e)
        {
          MasterTune.jlabel.setText("Master Tuning (Concert Pitch: "
                                     + freqFormatter.format(440+(4.400*0.059463094359*1.2 * (MasterTune.slider.getValue()-64))) + " Hz)");
        }
      });
    MasterTune.jlabel.setText("Master Tuning (Concert Pitch: "
                               + freqFormatter.format(440+(4.400*0.059463094359*1.2 * (MasterTune.slider.getValue()-64))) + " Hz)");
    gbc.gridx=0;gbc.gridy=0;gbc.gridwidth=1;gbc.gridheight=1; cmnPane.add(new JLabel(" "),gbc);
    addWidget(cmnPane,MasterTune,0,1,8,1,1);
    addWidget(cmnPane,new ComboBoxWidget("Memory Protection (*)",patch,new ParamModel(patch,16+19),new SystemSetupSender(83),MemProtName),0,2,4,1,2);
    gbc.gridx=4;gbc.gridy=2;gbc.gridwidth=4;gbc.gridheight=1;gbc.anchor=gbc.CENTER; cmnPane.add(new JLabel("Internal - Cartridge"),gbc);
    gbc.gridx=0;gbc.gridy=3;gbc.gridwidth=8;gbc.gridheight=1; cmnPane.add(new JLabel("(*) be not included in bulk data (only parameter change)"),gbc);


    gbc.gridx=0;gbc.gridy=4;gbc.gridwidth=1;gbc.gridheight=1; cmnPane.add(new JLabel(" "),gbc);
    gbc.gridx=0;gbc.gridy=5;gbc.gridwidth=4;gbc.gridheight=1; cmnPane.add(new JLabel("System Exclusiv:"),gbc);
    addWidget(cmnPane,new ComboBoxWidget("Common "                  ,patch,new ParamModel(patch,16+15),new SystemSetupSender(79),OnOffName)      ,0,6,4,1, 2);
    addWidget(cmnPane,new ComboBoxWidget("Device Number"            ,patch,new ParamModel(patch,16+14),new SystemSetupSender(78),SelectChgName)  ,4,6,4,1, 3);

    addWidget(cmnPane,new ComboBoxWidget("Transmit Voice Message"   ,patch,new ParamModel(patch,16+ 1),new SystemSetupSender(65),OnOffName)      ,0,7,4,1, 5);
    addWidget(cmnPane,new ComboBoxWidget("Transmit Channel"         ,patch,new ParamModel(patch,16+ 0),new SystemSetupSender(64),SelectChgName)  ,4,7,4,1, 6);
 
    addWidget(cmnPane,new ComboBoxWidget("MIDI OMNI Mode "          ,patch,new ParamModel(patch,16+ 4),new SystemSetupSender(68),OnOffName)      ,0,8,4,1, 7);
    addWidget(cmnPane,new ComboBoxWidget("Receive Channel - A"      ,patch,new ParamModel(patch,16+ 2),new SystemSetupSender(66),MidiReceiveName),4,8,4,1, 8);
    addWidget(cmnPane,new ComboBoxWidget("Receive Channel - B"      ,patch,new ParamModel(patch,16+ 3),new SystemSetupSender(67),MidiReceiveName),8,8,4,1, 9);

    addWidget(cmnPane,new ComboBoxWidget("Local On/Off"             ,patch,new ParamModel(patch,16+11),new SystemSetupSender(75),OnOffName)      ,0,9,4,1,10);
    addWidget(cmnPane,new ComboBoxWidget("Key   On/Off"             ,patch,new ParamModel(patch,16+ 9),new SystemSetupSender(73),NoteOnOffName)  ,4,9,4,1,11);

    addWidget(cmnPane,new ComboBoxWidget("Data Transmit Block"      ,patch,new ParamModel(patch,16+12),new SystemSetupSender(76),BlockSelName)   ,0,10,4,1,12);
    addWidget(cmnPane,new ComboBoxWidget("Data Receive Block"       ,patch,new ParamModel(patch,16+13),new SystemSetupSender(77),BlockSelName)   ,4,10,4,1,13);


    gbc.gridx=0;gbc.gridy=11;gbc.gridwidth=1;gbc.gridheight=1; cmnPane.add(new JLabel(" "),gbc);
    gbc.gridx=0;gbc.gridy=12;gbc.gridwidth=4;gbc.gridheight=1; cmnPane.add(new JLabel("Bank Select:"),gbc);
    addWidget(cmnPane,new ComboBoxWidget("for Voice & Performance"  ,patch,new ParamModel(patch,16+16),new SystemSetupSender(80),SelectChgName)  ,0,13,4,1,14);
    addWidget(cmnPane,new ComboBoxWidget("for fractional scaling"   ,patch,new ParamModel(patch,16+17),new SystemSetupSender(81),SelectChgName)  ,4,13,4,1,15);
    addWidget(cmnPane,new ComboBoxWidget("for micro tuning"         ,patch,new ParamModel(patch,16+18),new SystemSetupSender(82),SelectChgName)  ,8,13,4,1,16);


    gbc.gridx=0;gbc.gridy=15;gbc.gridwidth=1;gbc.gridheight=1;cmnPane.add(new JLabel(" "),gbc);
    gbc.gridx=0;gbc.gridy=16;gbc.gridwidth=4;gbc.gridheight=1; cmnPane.add(new JLabel("Program Change:"),gbc);
    addWidget(cmnPane,new ComboBoxWidget("Transmission Mode"        ,patch,new ParamModel(patch,16+10),new SystemSetupSender(74),PrgChgName)     ,0,17,4,1,17);
    gbc.gridx=4;gbc.gridy=17;gbc.gridwidth=8;gbc.gridheight=1; cmnPane.add(new JLabel("if Memory --> see next Tab!"),gbc);


    gbc.gridx=0;gbc.gridy=20;gbc.gridwidth=1;gbc.gridheight=1;cmnPane.add(new JLabel(" "),gbc);
    gbc.gridx=0;gbc.gridy=21;gbc.gridwidth=4;gbc.gridheight=1;cmnPane.add(new JLabel("MIDI IN Control Number:"),gbc);
    addWidget(cmnPane,new ScrollBarWidget("Voice A"                 ,patch,9,31,0,new ParamModel(patch,16+5),new SystemSetupSender(69))          ,0,22,4,1,18);
    addWidget(cmnPane,new ScrollBarWidget("Voice B"                 ,patch,9,31,0,new ParamModel(patch,16+6),new SystemSetupSender(70))          ,4,22,4,1,19);


    gbc.gridx=0;gbc.gridy=25;gbc.gridwidth=1;gbc.gridheight=1;cmnPane.add(new JLabel(" "),gbc);
    gbc.gridx=0;gbc.gridy=26;gbc.gridwidth=4;gbc.gridheight=1;cmnPane.add(new JLabel("Continuous Slider Control Number:"),gbc);
    addWidget(cmnPane,new ScrollBarWidget("CS 1"                    ,patch,5,31,0,new ParamModel(patch,16+7),new SystemSetupSender(71))          ,0,27,4,1,20);
    addWidget(cmnPane,new ScrollBarWidget("CS 2"                    ,patch,5,31,0,new ParamModel(patch,16+8),new SystemSetupSender(72))          ,4,27,4,1,20);


    JPanel perfPane = new JPanel();
    perfPane.setLayout(new GridBagLayout());

    systemPane.addTab("Program Change Transmission Table",perfPane);

    gbc.gridx=0;gbc.gridy=0;gbc.gridwidth=35;gbc.gridheight=1;gbc.fill=gbc.BOTH;
    perfPane.add(new JLabel("If you set the Program Change Transmission Mode to Memory, this Table determines which"),gbc);
    gbc.gridx=0;gbc.gridy=1;gbc.gridwidth=35;gbc.gridheight=1;gbc.fill=gbc.BOTH;
    perfPane.add(new JLabel("program change number will be transmitted via MIDI."),gbc);

    gbc.gridx=0;gbc.gridy=3;gbc.gridwidth=1;gbc.gridheight=1; perfPane.add(new JLabel(" "),gbc);
    gbc.gridx=0;gbc.gridy=4;gbc.gridwidth=1;gbc.gridheight=1; perfPane.add(new JLabel(" "),gbc);
    for (int i=0, j=0; i<64; i++)
    {
      if (i%8==0) j++;

      gbc.gridx=4*(i%8);gbc.gridy=3+j;gbc.gridwidth=1;gbc.gridheight=1; perfPane.add(new JLabel(" "),gbc);
      gbc.gridx=4*(i%8)+1;gbc.gridy=3+j;gbc.gridwidth=1;gbc.gridheight=1;gbc.fill=gbc.BOTH;gbc.anchor=gbc.EAST;
      perfPane.add(new JLabel(Integer.toString(i),SwingConstants.RIGHT),gbc);

      addWidget(perfPane,new SpinnerWidget("",patch,0,127,0,new ParamModel(patch,16+21+i),null)    ,4*(i%8)+2, 3+j,1,1,150+i  );

      gbc.gridx=4*(i%8)+3;gbc.gridy=3+j;gbc.gridwidth=1;gbc.gridheight=1; perfPane.add(new JLabel(" "),gbc);
    }
    gbc.gridx=0;gbc.gridy=13;gbc.gridwidth=1;gbc.gridheight=1;
    perfPane.add(new JLabel(" "),gbc);
    gbc.gridx=0;gbc.gridy=14;gbc.gridwidth=35;gbc.gridheight=1;gbc.fill=gbc.BOTH;
    perfPane.add(new JLabel("be included in bulk data only (does not have parameter change code)"),gbc);

    gbc.gridx=0;gbc.gridy=1;gbc.gridwidth=1;gbc.gridheight=1;
    scrollPane.add(systemPane,gbc);
    pack();
    show();

    PatchEdit.waitDialog.hide();        // Okay, the editor frame is ready
  }
}
