/*
 * JSynthlib-Editor for MIDIbox FM
 * =====================================================================
 * @author  Thorsten Klose
 * @version $Id$
 *
 * Copyright (C) 2005  Thorsten.Klose@gmx.de
 *                     http://www.uCApps.de
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
 */

package synthdrivers.MIDIboxFM;

import java.awt.GridBagLayout;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import org.jsynthlib.core.CheckBoxWidget;
import org.jsynthlib.core.ComboBoxWidget;
import org.jsynthlib.core.KnobWidget;
import org.jsynthlib.core.Patch;
import org.jsynthlib.core.PatchEditorFrame;


class MIDIboxFMDrumEditor extends PatchEditorFrame
{

    final String[] NoteName = new String[] {
	"c-2","c#2","d-2","d#2","e-2","f-2","f#2","g-2","g#2","a-2","a#2","b-2",
	"c-1","c#1","d-1","d#1","e-1","f-1","f#1","g-1","g#1","a-1","a#1","b-1",
	"C-0","C#0","D-0","D#0","E-0","F-0","F#0","G-0","G#0","A-0","A#0","B-0",
	"C-1","C#1","D-1","D#1","E-1","F-1","F#1","G-1","G#1","A-1","A#1","B-1",
	"C-2","C#2","D-2","D#2","E-2","F-2","F#2","G-2","G#2","A-2","A#2","B-2",
	"C-3","C#3","D-3","D#3","E-3","F-3","F#3","G-3","G#3","A-3","A#3","B-3",
	"C-4","C#4","D-4","D#4","E-4","F-4","F#4","G-4","G#4","A-4","A#4","B-4",
	"C-5","C#5","D-5","D#5","E-5","F-5","F#5","G-5","G#5","A-5","A#5","B-5",
	"C-6","C#6","D-6","D#6","E-6","F-6","F#6","G-6","G#6","A-6","A#6","B-6",
	"C-7","C#7","D-7","D#7","E-7","F-7","F#7","G-7","G#7","A-7","A#7","B-7",
	"C-8","C#8","D-8","D#8","E-8","F-8","F#8","G-8"};

    ImageIcon wsIcon[]=new ImageIcon[8];

    public MIDIboxFMDrumEditor(Patch patch)
    {
	super ("MIDIbox FM Drum Editor",patch);
	gbc.weightx=0; gbc.weighty=0;

        wsIcon[0]=new ImageIcon(MIDIboxFMDevice.class.getResource("images/ws_0.gif"));
        wsIcon[1]=new ImageIcon(MIDIboxFMDevice.class.getResource("images/ws_1.gif"));
        wsIcon[2]=new ImageIcon(MIDIboxFMDevice.class.getResource("images/ws_2.gif"));
        wsIcon[3]=new ImageIcon(MIDIboxFMDevice.class.getResource("images/ws_3.gif"));
        wsIcon[4]=new ImageIcon(MIDIboxFMDevice.class.getResource("images/ws_4.gif"));
        wsIcon[5]=new ImageIcon(MIDIboxFMDevice.class.getResource("images/ws_5.gif"));
        wsIcon[6]=new ImageIcon(MIDIboxFMDevice.class.getResource("images/ws_6.gif"));
        wsIcon[7]=new ImageIcon(MIDIboxFMDevice.class.getResource("images/ws_7.gif"));

///////////////////////////////////////////////////////////////////////////////
	JPanel UpperPanel=new JPanel();
	UpperPanel.setLayout(new GridBagLayout());
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  Bass Drum
///////////////////////////////////////////////////////////////////////////////
	JTabbedPane bdPane=new JTabbedPane();
	for(int i=0; i<1; ++i)
	{
	    JPanel panel = new JPanel();
	    panel.setLayout(new GridBagLayout());
	    bdPane.addTab("Bass Drum",panel);

	    ///////////////////////////////////////////////////////////////////
	    JPanel modpanel=new JPanel();
	    modpanel.setLayout(new GridBagLayout());

	    addWidget(modpanel,new KnobWidget("Attack",patch,0,15,0,new MIDIboxFMModel(patch,0x10),new MIDIboxFMSender(patch,0x10,0x10)),0,1,3,3,1);
	    addWidget(modpanel,new KnobWidget("Decay",patch,0,15,0,new MIDIboxFMModel(patch,0x18),new MIDIboxFMSender(patch,0x10,0x18)),3,1,3,3,2);
	    addWidget(modpanel,new KnobWidget("Sustain",patch,0,15,0,new MIDIboxFMModel(patch,0x20),new MIDIboxFMSender(patch,0x10,0x20)),6,1,3,3,3);
	    addWidget(modpanel,new KnobWidget("Release",patch,0,15,0,new MIDIboxFMModel(patch,0x28),new MIDIboxFMSender(patch,0x10,0x28)),9,1,3,3,4);

	    addWidget(modpanel,new KnobWidget("-> Carrier",patch,0,63,0,new MIDIboxFMModel(patch,0x08),new MIDIboxFMSender(patch,0x10,0x08)),0,4,3,3,5);
	    addWidget(modpanel,new KnobWidget("Multiplier",patch,0,15,0,new MIDIboxFMModel(patch,0x00),new MIDIboxFMSender(patch,0x10,0x00)),3,4,3,3,6);
	    final KnobWidget wsBar1=new KnobWidget("Waveform",patch,0,7,0,new MIDIboxFMModel(patch,0x30),new MIDIboxFMSender(patch,0x10,0x30));
	    addWidget(modpanel,wsBar1,6,4,3,3,7);

	    final JLabel wsImg1=new JLabel(wsIcon[wsBar1.getValue()&7]);
	    gbc.gridx=9;gbc.gridy=4;gbc.gridwidth=5;gbc.gridheight=3;
	    modpanel.add(wsImg1,gbc);
	    wsBar1.addEventListener(new ChangeListener() {
		    public void stateChanged(ChangeEvent e) {
			wsImg1.setIcon(wsIcon[wsBar1.getValue()&7]);
		    }});

      	    gbc.gridx=0;gbc.gridy=0;gbc.gridwidth=12;gbc.gridheight=1; modpanel.add(new JLabel("Modulator"),gbc);

	    gbc.gridx=0;gbc.gridy=0;gbc.gridwidth=5;gbc.gridheight=5;
	    panel.add(modpanel,gbc);

	    ///////////////////////////////////////////////////////////////////
	    JPanel carrierpanel=new JPanel();
	    carrierpanel.setLayout(new GridBagLayout());

      	    gbc.gridx=0;gbc.gridy=0;gbc.gridwidth=12;gbc.gridheight=1; carrierpanel.add(new JLabel("Carrier"),gbc);

	    addWidget(carrierpanel,new KnobWidget("Attack",patch,0,15,0,new MIDIboxFMModel(patch,0x11),new MIDIboxFMSender(patch,0x10,0x11)),0,1,3,3,8);
	    addWidget(carrierpanel,new KnobWidget("Decay",patch,0,15,0,new MIDIboxFMModel(patch,0x19),new MIDIboxFMSender(patch,0x10,0x19)),3,1,3,3,9);
	    addWidget(carrierpanel,new KnobWidget("Sustain",patch,0,15,0,new MIDIboxFMModel(patch,0x21),new MIDIboxFMSender(patch,0x10,0x21)),6,1,3,3,10);
	    addWidget(carrierpanel,new KnobWidget("Release",patch,0,15,0,new MIDIboxFMModel(patch,0x29),new MIDIboxFMSender(patch,0x10,0x29)),9,1,3,3,11);

	    addWidget(carrierpanel,new KnobWidget("Volume",patch,0,63,0,new MIDIboxFMModel(patch,0x09),new MIDIboxFMSender(patch,0x10,0x09)),0,4,3,3,12);
	    addWidget(carrierpanel,new KnobWidget("Multiplier",patch,0,15,0,new MIDIboxFMModel(patch,0x01),new MIDIboxFMSender(patch,0x10,0x01)),3,4,3,3,13);
	    final KnobWidget wsBar2=new KnobWidget("Waveform",patch,0,7,0,new MIDIboxFMModel(patch,0x31),new MIDIboxFMSender(patch,0x10,0x31));
	    addWidget(carrierpanel,wsBar2,6,4,3,3,14);

	    final JLabel wsImg2=new JLabel(wsIcon[wsBar2.getValue()&7]);
	    gbc.gridx=9;gbc.gridy=4;gbc.gridwidth=5;gbc.gridheight=3;
	    carrierpanel.add(wsImg2,gbc);
	    wsBar2.addEventListener(new ChangeListener() {
		    public void stateChanged(ChangeEvent e) {
			wsImg2.setIcon(wsIcon[wsBar2.getValue()&7]);
		    }});

	    gbc.gridx=5;gbc.gridy=0;gbc.gridwidth=5;gbc.gridheight=5;
	    panel.add(carrierpanel,gbc);

	    ///////////////////////////////////////////////////////////////////
	    JPanel frqpanel=new JPanel();
	    frqpanel.setLayout(new GridBagLayout());

	    addWidget(frqpanel,new CheckBoxWidget("Disable FM",  patch,new MIDIboxFMModel(patch,0x39,0),new MIDIboxFMSender(patch,0x10,0x39,0)),0,0,5,1,-1);
	    addWidget(frqpanel,new KnobWidget("Frq",patch,0,127,0,new MIDIboxFMModel(patch,0x50),new MIDIboxFMSender(patch,0x10,0x50)),1,1,3,3,15);
	    addWidget(frqpanel,new KnobWidget("Frq Decr.",patch,0,127,0,new MIDIboxFMModel(patch,0x51),new MIDIboxFMSender(patch,0x10,0x51)),1,4,3,3,16);

	    gbc.gridx=10;gbc.gridy=0;gbc.gridwidth=3;gbc.gridheight=5;
	    panel.add(frqpanel,gbc);
	}
	gbc.gridx=0;gbc.gridy=0;gbc.gridwidth=10;gbc.gridheight=5;
	UpperPanel.add(bdPane,gbc);

///////////////////////////////////////////////////////////////////////////////
	gbc.gridx=0;gbc.gridy=0;gbc.gridwidth=20;gbc.gridheight=5;
	scrollPane.add(UpperPanel,gbc);
///////////////////////////////////////////////////////////////////////////////


///////////////////////////////////////////////////////////////////////////////
	JPanel Upper2Panel=new JPanel();
	Upper2Panel.setLayout(new GridBagLayout());
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  HiHat
///////////////////////////////////////////////////////////////////////////////
	JTabbedPane hhPane=new JTabbedPane();
	for(int i=0; i<1; ++i)
	{
	    JPanel panel = new JPanel();
	    panel.setLayout(new GridBagLayout());
	    hhPane.addTab("HiHat",panel);

	    ///////////////////////////////////////////////////////////////////
	    JPanel hhopanel=new JPanel();
	    hhopanel.setLayout(new GridBagLayout());

      	    gbc.gridx=0;gbc.gridy=0;gbc.gridwidth=12;gbc.gridheight=1; hhopanel.add(new JLabel("Open HiHat"),gbc);

	    addWidget(hhopanel,new KnobWidget("Attack",patch,0,15,0,new MIDIboxFMModel(patch,0x12),new MIDIboxFMSender(patch,0x10,0x12)),0,1,3,3,17);
	    addWidget(hhopanel,new KnobWidget("Decay",patch,0,15,0,new MIDIboxFMModel(patch,0x1a),new MIDIboxFMSender(patch,0x10,0x1a)),3,1,3,3,18);
	    addWidget(hhopanel,new KnobWidget("Sustain",patch,0,15,0,new MIDIboxFMModel(patch,0x22),new MIDIboxFMSender(patch,0x10,0x22)),6,1,3,3,19);
	    addWidget(hhopanel,new KnobWidget("Release",patch,0,15,0,new MIDIboxFMModel(patch,0x2a),new MIDIboxFMSender(patch,0x10,0x2a)),9,1,3,3,20);

	    addWidget(hhopanel,new KnobWidget("Volume",patch,0,63,0,new MIDIboxFMModel(patch,0x0a),new MIDIboxFMSender(patch,0x10,0x0a)),0,4,3,3,21);
	    addWidget(hhopanel,new KnobWidget("Multiplier",patch,0,15,0,new MIDIboxFMModel(patch,0x02),new MIDIboxFMSender(patch,0x10,0x02)),3,4,3,3,22);
	    final KnobWidget wsBar1=new KnobWidget("Waveform",patch,0,7,0,new MIDIboxFMModel(patch,0x32),new MIDIboxFMSender(patch,0x10,0x32));
	    addWidget(hhopanel,wsBar1,6,4,3,3,23);

	    final JLabel wsImg1=new JLabel(wsIcon[wsBar1.getValue()&7]);
	    gbc.gridx=9;gbc.gridy=4;gbc.gridwidth=5;gbc.gridheight=3;
	    hhopanel.add(wsImg1,gbc);
	    wsBar1.addEventListener(new ChangeListener() {
		    public void stateChanged(ChangeEvent e) {
			wsImg1.setIcon(wsIcon[wsBar1.getValue()&7]);
		    }});

	    gbc.gridx=0;gbc.gridy=0;gbc.gridwidth=5;gbc.gridheight=5;
	    panel.add(hhopanel,gbc);

	    ///////////////////////////////////////////////////////////////////
	    JPanel hhcpanel=new JPanel();
	    hhcpanel.setLayout(new GridBagLayout());

      	    gbc.gridx=0;gbc.gridy=0;gbc.gridwidth=12;gbc.gridheight=1; hhcpanel.add(new JLabel("Close HiHat"),gbc);

	    addWidget(hhcpanel,new KnobWidget("Attack",patch,0,15,0,new MIDIboxFMModel(patch,0x13),new MIDIboxFMSender(patch,0x10,0x13)),0,1,3,3,24);
	    addWidget(hhcpanel,new KnobWidget("Decay",patch,0,15,0,new MIDIboxFMModel(patch,0x1b),new MIDIboxFMSender(patch,0x10,0x1b)),3,1,3,3,25);
	    addWidget(hhcpanel,new KnobWidget("Sustain",patch,0,15,0,new MIDIboxFMModel(patch,0x23),new MIDIboxFMSender(patch,0x10,0x23)),6,1,3,3,26);
	    addWidget(hhcpanel,new KnobWidget("Release",patch,0,15,0,new MIDIboxFMModel(patch,0x2b),new MIDIboxFMSender(patch,0x10,0x2b)),9,1,3,3,27);

	    addWidget(hhcpanel,new KnobWidget("Volume",patch,0,63,0,new MIDIboxFMModel(patch,0x0b),new MIDIboxFMSender(patch,0x10,0x0b)),0,4,3,3,28);
	    addWidget(hhcpanel,new KnobWidget("Multiplier",patch,0,15,0,new MIDIboxFMModel(patch,0x03),new MIDIboxFMSender(patch,0x10,0x03)),3,4,3,3,29);
	    final KnobWidget wsBar2=new KnobWidget("Waveform",patch,0,7,0,new MIDIboxFMModel(patch,0x33),new MIDIboxFMSender(patch,0x10,0x33));
	    addWidget(hhcpanel,wsBar2,6,4,3,3,30);

	    final JLabel wsImg2=new JLabel(wsIcon[wsBar2.getValue()&7]);
	    gbc.gridx=9;gbc.gridy=4;gbc.gridwidth=5;gbc.gridheight=3;
	    hhcpanel.add(wsImg2,gbc);
	    wsBar2.addEventListener(new ChangeListener() {
		    public void stateChanged(ChangeEvent e) {
			wsImg2.setIcon(wsIcon[wsBar2.getValue()&7]);
		    }});

	    gbc.gridx=5;gbc.gridy=0;gbc.gridwidth=5;gbc.gridheight=5;
	    panel.add(hhcpanel,gbc);

	}
	gbc.gridx=0;gbc.gridy=0;gbc.gridwidth=5;gbc.gridheight=5;
	Upper2Panel.add(hhPane,gbc);

///////////////////////////////////////////////////////////////////////////////
//  Noise Generator
///////////////////////////////////////////////////////////////////////////////
	JTabbedPane noisePane=new JTabbedPane();
	for(int i=0; i<1; ++i)
	{
	    JPanel panel = new JPanel();
	    panel.setLayout(new GridBagLayout());
	    noisePane.addTab("Noise Generator",panel);

	    gbc.gridx=0;gbc.gridy=0;gbc.gridwidth=3;gbc.gridheight=1; panel.add(new JLabel("   \"HH\""),gbc);
	    gbc.gridx=3;gbc.gridy=0;gbc.gridwidth=3;gbc.gridheight=1; panel.add(new JLabel(" \"Tom\""),gbc);
	    addWidget(panel,new KnobWidget("Frq",patch,0,127,0,new MIDIboxFMModel(patch,0x52),new MIDIboxFMSender(patch,0x10,0x52)),0,1,3,3,31);
	    addWidget(panel,new KnobWidget("Frq",patch,0,127,0,new MIDIboxFMModel(patch,0x54),new MIDIboxFMSender(patch,0x10,0x54)),3,1,3,3,32);
	    addWidget(panel,new KnobWidget("Decr.",patch,0,127,0,new MIDIboxFMModel(patch,0x53),new MIDIboxFMSender(patch,0x10,0x53)),0,4,3,3,33);
	    addWidget(panel,new KnobWidget("Decr.",patch,0,127,0,new MIDIboxFMModel(patch,0x55),new MIDIboxFMSender(patch,0x10,0x55)),3,4,3,3,34);
	}
	gbc.gridx=5;gbc.gridy=0;gbc.gridwidth=5;gbc.gridheight=5;
	Upper2Panel.add(noisePane,gbc);



///////////////////////////////////////////////////////////////////////////////
	gbc.gridx=0;gbc.gridy=5;gbc.gridwidth=20;gbc.gridheight=5;
	scrollPane.add(Upper2Panel,gbc);
///////////////////////////////////////////////////////////////////////////////


///////////////////////////////////////////////////////////////////////////////
	JPanel MidPanel=new JPanel();
	MidPanel.setLayout(new GridBagLayout());
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  Snare Drum
///////////////////////////////////////////////////////////////////////////////
	JTabbedPane sdPane=new JTabbedPane();
	for(int i=0; i<1; ++i)
	{
	    JPanel panel = new JPanel();
	    panel.setLayout(new GridBagLayout());
	    sdPane.addTab("Snare Drum",panel);

	    addWidget(panel,new KnobWidget("Attack",patch,0,15,0,new MIDIboxFMModel(patch,0x14),new MIDIboxFMSender(patch,0x10,0x14)),0,0,3,3,35);
	    addWidget(panel,new KnobWidget("Decay",patch,0,15,0,new MIDIboxFMModel(patch,0x1c),new MIDIboxFMSender(patch,0x10,0x1c)),3,0,3,3,36);
	    addWidget(panel,new KnobWidget("Sustain",patch,0,15,0,new MIDIboxFMModel(patch,0x24),new MIDIboxFMSender(patch,0x10,0x24)),6,0,3,3,37);
	    addWidget(panel,new KnobWidget("Release",patch,0,15,0,new MIDIboxFMModel(patch,0x2c),new MIDIboxFMSender(patch,0x10,0x2c)),9,0,3,3,38);

	    addWidget(panel,new KnobWidget("Volume",patch,0,63,0,new MIDIboxFMModel(patch,0x0c),new MIDIboxFMSender(patch,0x10,0x0c)),0,3,3,3,39);
	    addWidget(panel,new KnobWidget("Multiplier",patch,0,15,0,new MIDIboxFMModel(patch,0x04),new MIDIboxFMSender(patch,0x10,0x04)),3,3,3,3,40);
	    final KnobWidget wsBar=new KnobWidget("Waveform",patch,0,7,0,new MIDIboxFMModel(patch,0x34),new MIDIboxFMSender(patch,0x10,0x34));
	    addWidget(panel,wsBar,6,3,3,3,41);

	    final JLabel wsImg=new JLabel(wsIcon[wsBar.getValue()&7]);
	    gbc.gridx=9;gbc.gridy=3;gbc.gridwidth=5;gbc.gridheight=3;
	    panel.add(wsImg,gbc);
	    wsBar.addEventListener(new ChangeListener() {
		    public void stateChanged(ChangeEvent e) {
			wsImg.setIcon(wsIcon[wsBar.getValue()&7]);
		    }});
	}
	gbc.gridx=0;gbc.gridy=0;gbc.gridwidth=5;gbc.gridheight=5;
	MidPanel.add(sdPane,gbc);


///////////////////////////////////////////////////////////////////////////////
//  Cymbal
///////////////////////////////////////////////////////////////////////////////
	JTabbedPane cyPane=new JTabbedPane();
	for(int i=0; i<1; ++i)
	{
	    JPanel panel = new JPanel();
	    panel.setLayout(new GridBagLayout());
	    cyPane.addTab("Cymbal",panel);

	    addWidget(panel,new KnobWidget("Attack",patch,0,15,0,new MIDIboxFMModel(patch,0x16),new MIDIboxFMSender(patch,0x10,0x16)),0,0,3,3,42);
	    addWidget(panel,new KnobWidget("Decay",patch,0,15,0,new MIDIboxFMModel(patch,0x1e),new MIDIboxFMSender(patch,0x10,0x1e)),3,0,3,3,43);
	    addWidget(panel,new KnobWidget("Sustain",patch,0,15,0,new MIDIboxFMModel(patch,0x26),new MIDIboxFMSender(patch,0x10,0x26)),6,0,3,3,44);
	    addWidget(panel,new KnobWidget("Release",patch,0,15,0,new MIDIboxFMModel(patch,0x2e),new MIDIboxFMSender(patch,0x10,0x2e)),9,0,3,3,45);

	    addWidget(panel,new KnobWidget("Volume",patch,0,63,0,new MIDIboxFMModel(patch,0x0e),new MIDIboxFMSender(patch,0x10,0x0e)),0,3,3,3,46);
	    addWidget(panel,new KnobWidget("Multiplier",patch,0,15,0,new MIDIboxFMModel(patch,0x06),new MIDIboxFMSender(patch,0x10,0x06)),3,3,3,3,47);
	    final KnobWidget wsBar=new KnobWidget("Waveform",patch,0,7,0,new MIDIboxFMModel(patch,0x36),new MIDIboxFMSender(patch,0x10,0x36));
	    addWidget(panel,wsBar,6,3,3,3,48);

	    final JLabel wsImg=new JLabel(wsIcon[wsBar.getValue()&7]);
	    gbc.gridx=9;gbc.gridy=3;gbc.gridwidth=5;gbc.gridheight=3;
	    panel.add(wsImg,gbc);
	    wsBar.addEventListener(new ChangeListener() {
		    public void stateChanged(ChangeEvent e) {
			wsImg.setIcon(wsIcon[wsBar.getValue()&7]);
		    }});
	}
	gbc.gridx=5;gbc.gridy=0;gbc.gridwidth=5;gbc.gridheight=5;
	MidPanel.add(cyPane,gbc);
	
///////////////////////////////////////////////////////////////////////////////
//  Tom
///////////////////////////////////////////////////////////////////////////////
	JTabbedPane tomPane=new JTabbedPane();
	for(int i=0; i<1; ++i)
	{
	    JPanel panel = new JPanel();
	    panel.setLayout(new GridBagLayout());
	    tomPane.addTab("Tom",panel);

	    addWidget(panel,new KnobWidget("Attack",patch,0,15,0,new MIDIboxFMModel(patch,0x15),new MIDIboxFMSender(patch,0x10,0x15)),0,0,3,3,49);
	    addWidget(panel,new KnobWidget("Decay",patch,0,15,0,new MIDIboxFMModel(patch,0x1d),new MIDIboxFMSender(patch,0x10,0x1d)),3,0,3,3,50);
	    addWidget(panel,new KnobWidget("Sustain",patch,0,15,0,new MIDIboxFMModel(patch,0x25),new MIDIboxFMSender(patch,0x10,0x25)),6,0,3,3,51);
	    addWidget(panel,new KnobWidget("Release",patch,0,15,0,new MIDIboxFMModel(patch,0x2d),new MIDIboxFMSender(patch,0x10,0x2d)),9,0,3,3,52);

	    addWidget(panel,new KnobWidget("Volume",patch,0,63,0,new MIDIboxFMModel(patch,0x0d),new MIDIboxFMSender(patch,0x10,0x0d)),0,3,3,3,53);
	    addWidget(panel,new KnobWidget("Multiplier",patch,0,15,0,new MIDIboxFMModel(patch,0x05),new MIDIboxFMSender(patch,0x10,0x05)),3,3,3,3,54);
	    final KnobWidget wsBar=new KnobWidget("Waveform",patch,0,7,0,new MIDIboxFMModel(patch,0x35),new MIDIboxFMSender(patch,0x10,0x35));
	    addWidget(panel,wsBar,6,3,3,3,55);

	    final JLabel wsImg=new JLabel(wsIcon[wsBar.getValue()&7]);
	    gbc.gridx=9;gbc.gridy=3;gbc.gridwidth=5;gbc.gridheight=3;
	    panel.add(wsImg,gbc);
	    wsBar.addEventListener(new ChangeListener() {
		    public void stateChanged(ChangeEvent e) {
			wsImg.setIcon(wsIcon[wsBar.getValue()&7]);
		    }});

	}
	gbc.gridx=10;gbc.gridy=0;gbc.gridwidth=5;gbc.gridheight=5;
	MidPanel.add(tomPane,gbc);


///////////////////////////////////////////////////////////////////////////////
	gbc.gridx=0;gbc.gridy=10;gbc.gridwidth=20;gbc.gridheight=5;
	scrollPane.add(MidPanel,gbc);
///////////////////////////////////////////////////////////////////////////////



///////////////////////////////////////////////////////////////////////////////
	JPanel LowerPanel=new JPanel();
	LowerPanel.setLayout(new GridBagLayout());
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  Audio Panel
///////////////////////////////////////////////////////////////////////////////
	JTabbedPane audioPane=new JTabbedPane();
	for(int i=0; i<1; ++i)
	{
	    JPanel panel = new JPanel();
	    panel.setLayout(new GridBagLayout());
	    audioPane.addTab("Audio",panel);gbc.weightx=0;

	    gbc.gridx= 3+1+0*5;gbc.gridy=0;gbc.gridwidth=3;gbc.gridheight=1; panel.add(new JLabel("    BD"),gbc);
	    gbc.gridx= 3+1+1*5;gbc.gridy=0;gbc.gridwidth=3;gbc.gridheight=1; panel.add(new JLabel("  SD/HH"),gbc);
	    gbc.gridx= 3+1+2*5;gbc.gridy=0;gbc.gridwidth=3;gbc.gridheight=1; panel.add(new JLabel("Cymb/Tom"),gbc);

	    for(int k=0; k<4; ++k)
	    {
		gbc.gridx=0;gbc.gridy=9+k;gbc.gridwidth=3;gbc.gridheight=1; panel.add(new JLabel("Audio Chn #"+(k+1)),gbc);
		for(int j=0; j<3; ++j)
		{
		    gbc.gridx=3+0+j*5;gbc.gridy=9+k;gbc.gridwidth=1;gbc.gridheight=1; panel.add(new JLabel("        "),gbc);
		    gbc.gridx=3+1+j*5;gbc.gridy=9+k;gbc.gridwidth=1;gbc.gridheight=1; panel.add(new JLabel("     "),gbc);
		    addWidget(panel,new CheckBoxWidget("",  patch,new MIDIboxFMModel(patch,0x58+2*j,k),new MIDIboxFMSender(patch,0x10,0x58+2*j,k)),3+2+5*j,9+k,3,1,-2-2*j-k*4);
		}
	    }
	}
	gbc.gridx=0;gbc.gridy=0;gbc.gridwidth=5;gbc.gridheight=5;
	LowerPanel.add(audioPane,gbc);


///////////////////////////////////////////////////////////////////////////////
//  Note Assign Panel
///////////////////////////////////////////////////////////////////////////////
	JTabbedPane KAPane=new JTabbedPane();
	for(int i=0; i<1; ++i)
	{
	    JPanel panel = new JPanel();
	    panel.setLayout(new GridBagLayout());
	    KAPane.addTab("Note Assigns",panel);gbc.weightx=0;

	    gbc.gridx= 3+1+0*5;gbc.gridy=0;gbc.gridwidth=3;gbc.gridheight=1; panel.add(new JLabel("    BD"),gbc);
	    gbc.gridx= 3+1+1*5;gbc.gridy=0;gbc.gridwidth=3;gbc.gridheight=1; panel.add(new JLabel("  HH Open"),gbc);
	    gbc.gridx= 3+1+2*5;gbc.gridy=0;gbc.gridwidth=3;gbc.gridheight=1; panel.add(new JLabel("  HH Close"),gbc);
	    gbc.gridx= 3+1+3*5;gbc.gridy=0;gbc.gridwidth=3;gbc.gridheight=1; panel.add(new JLabel("  SD"),gbc);
	    gbc.gridx= 3+1+4*5;gbc.gridy=0;gbc.gridwidth=3;gbc.gridheight=1; panel.add(new JLabel("   Tom"),gbc);
	    gbc.gridx= 3+1+5*5;gbc.gridy=0;gbc.gridwidth=3;gbc.gridheight=1; panel.add(new JLabel(" Cymbal"),gbc);

	    gbc.gridx=0;gbc.gridy=1;gbc.gridwidth=3;gbc.gridheight=1; panel.add(new JLabel(" "),gbc);
	    gbc.gridx=0;gbc.gridy=2;gbc.gridwidth=3;gbc.gridheight=1; panel.add(new JLabel("Left Border"),gbc);
	    for(int j=0; j<6; ++j)
	    {
		addWidget(panel,new ComboBoxWidget("",patch,new MIDIboxFMModel(patch,0x41+j),new MIDIboxFMSender(patch,0x10,0x41+j),NoteName),3+5*j,1,5,2,0);
	    }

	    gbc.gridx=0;gbc.gridy=4;gbc.gridwidth=3;gbc.gridheight=1; panel.add(new JLabel(" "),gbc);
	    gbc.gridx=0;gbc.gridy=5;gbc.gridwidth=3;gbc.gridheight=1; panel.add(new JLabel("Right Border"),gbc);
	    for(int j=0; j<6; ++j)
	    {
		addWidget(panel,new ComboBoxWidget("",patch,new MIDIboxFMModel(patch,0x49+j),new MIDIboxFMSender(patch,0x10,0x49+j),NoteName),3+5*j,4,5,2,0);
	    }
	}
	gbc.gridx=5;gbc.gridy=0;gbc.gridwidth=5;gbc.gridheight=5;
	LowerPanel.add(KAPane,gbc);

///////////////////////////////////////////////////////////////////////////////
	gbc.gridx=0;gbc.gridy=15;gbc.gridwidth=20;gbc.gridheight=5;
	scrollPane.add(LowerPanel,gbc);
///////////////////////////////////////////////////////////////////////////////

	pack();
    }
}
