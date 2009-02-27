/*
 * YamahaTX81zDevice.java
 *
 * Created on 10. Oktober 2001, 21:23
 */

package org.jsynthlib.drivers.yamaha.dx100;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.prefs.Preferences;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.jsynthlib.core.Device;


/**
 *
 * @author  Gerrit Gehnen
 * @version $Id$
 */
public class YamahaDX100Device extends Device implements ItemListener
{
    private static final String infoText="The Yamaha synth is susceptable to internal midi buffer overflow if you send it a lot of Data "+
    "quickly. With JSynthLib, this can happenif you are using a fader box and throwing the faders "+
    "around rapidly. Otherwise, it should not be a problem\n\n"+
    "JSynthLib supports the DX21/27/100 as both a Single and Bank Librarian and also supports Patch Editing."+
    "Note that though these three synths share one driver, some parameters may only effect the sound on certain "+
    "models. Therefore, under 'configuration' you can choose which of the three models you own.";

    JRadioButton b1;
    JRadioButton b2;
    JRadioButton b3;

    /** Creates new YamahaTX81zDevice */
    public YamahaDX100Device ()
    {
	super ("Yamaha","DX21 / DX27 / DX100",null,infoText,"Brian Klock");
    }

    /** Constructor for for actual work. */
    public YamahaDX100Device(Preferences prefs) {
	this();
	this.prefs = prefs;

        //setSynthName("DX21");
        addDriver (new YamahaDX100BankDriver ());
        addDriver (new YamahaDX100SingleDriver ());
	setWhichSynth(21);
    }

    public JPanel config() {
	JPanel panel= new JPanel();

	panel.add(new JLabel("Choose a supported Synthesizer"));
	ButtonGroup bg= new ButtonGroup();
	b1 = new JRadioButton ("DX21",getWhichSynth()==21);
	b2 = new JRadioButton ("DX27",getWhichSynth()==27);
	b3 = new JRadioButton ("DX100",getWhichSynth()==100);
	b1.addItemListener(this);
	b2.addItemListener(this);
	b3.addItemListener(this);

	bg.add(b1);bg.add(b2);bg.add(b3);
	panel.add(b1);panel.add(b2);panel.add(b3);

	return panel;
    }
    public void itemStateChanged(ItemEvent e){
        if (e.getStateChange()!=ItemEvent.SELECTED) return;
	if (e.getItemSelectable()==b1) {setWhichSynth(21);setSynthName("DX21");}
	if (e.getItemSelectable()==b2) {setWhichSynth(27);setSynthName("DX27");}
	if (e.getItemSelectable()==b3) {setWhichSynth(100);setSynthName("DX100");}
    }

    /** Getter for whichSynth */
    public int getWhichSynth() {
	return prefs.getInt("whichSynth", 0);
    }
    /** Setter for whichSynth */
    public void setWhichSynth(int whichSynth) {
	prefs.putInt("whichSynth", whichSynth);
    }
}
