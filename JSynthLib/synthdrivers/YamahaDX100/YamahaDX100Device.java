/*
 * YamahaTX81zDevice.java
 *
 * Created on 10. Oktober 2001, 21:23
 */

package synthdrivers.YamahaDX100;

import core.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
/**
 *
 * @author  Gerrit Gehnen
 * @version 0.1
 */
public class YamahaDX100Device extends Device implements ItemListener
{
    public int whichSynth;    
    JRadioButton b1;
    JRadioButton b2;
    JRadioButton b3;

    /** Creates new YamahaTX81zDevice */
    public YamahaDX100Device ()
    {
        manufacturerName="Yamaha";
        modelName="DX21 / DX 27 / DX100";
        synthName="DX21";
        addDriver (new YamahaDX100BankDriver ());
        addDriver (new YamahaDX100SingleDriver ());
       infoText="The Yamaha synth is susceptable to internal midi buffer overflow if you send it a lot of Data"+
	          "quickly. With JSynthLib, this can happenif you are using a fader box and throwing the faders"+
		  "around rapidly. Otherwise, it should not be a problem\n\n"+
		  "JSynthLib supports the DX21/27/100 as both a Single and Bank Librarian and also supports Patch Editing."+
		  "Note that though these three synths share one driver, some parameters may only effect the sound on certain "+
		  "models. Therefore, under 'configuration' you can choose which of the three models you own.";
        whichSynth=21;         
    }
    public JPanel config()
   {
      JPanel panel= new JPanel();
 
      panel.add(new JLabel("Choose a supported Synthesizer"));
      ButtonGroup bg= new ButtonGroup();
      b1 = new JRadioButton ("DX21",whichSynth==21);
      b2 = new JRadioButton ("DX27",whichSynth==27);
      b3 = new JRadioButton ("DX100",whichSynth==100);
      b1.addItemListener(this);
      b2.addItemListener(this);
      b3.addItemListener(this);
      
      bg.add(b1);bg.add(b2);bg.add(b3);
      panel.add(b1);panel.add(b2);panel.add(b3);
      
 
      return panel;
   }
      public void itemStateChanged(ItemEvent e){
        if (e.getStateChange()!=ItemEvent.SELECTED) return;
	if (e.getItemSelectable()==b1) {whichSynth=21;setSynthName("DX21");}
	if (e.getItemSelectable()==b2) {whichSynth=27;setSynthName("DX27");}
	if (e.getItemSelectable()==b3) {whichSynth=100;setSynthName("DX100");}
      }  
}