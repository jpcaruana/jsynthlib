package org.jsynthlib.drivers.roland.gp16;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.UnsupportedEncodingException;

import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;

import org.jsynthlib.core.CheckBoxWidget;
import org.jsynthlib.core.ComboBoxWidget;
import org.jsynthlib.core.Driver;
import org.jsynthlib.core.ErrorMsg;
import org.jsynthlib.core.IPatch;
import org.jsynthlib.core.ParamModel;
import org.jsynthlib.core.Patch;
import org.jsynthlib.core.PatchNameWidget;
import org.jsynthlib.core.ScrollBarWidget;
import org.jsynthlib.core.SysexSender;

/**
 * Marker class that allows dependency checking to work.
 * (It fails if RolandGP16DataHandlers.class isn't found.)
 */
abstract class RolandGP16DataHandlers {
}

/** Standard Roland GP-16 value sender. */
class VSender extends SysexSender
{
  int parameter;
  byte []b = new byte [11];
  
  public VSender(int param){
    parameter=param;
    b[0]=(byte)0xF0; b[1]=(byte)0x41; b[3]=(byte)0x2A; b[4]=(byte)0x12; b[5]=(byte)0x08;
    b[7]=(byte)parameter;b[10]=(byte)0xF7;
  }
  
  public byte [] generate (int value){
	b[8]=(byte)value;b[2]=(byte)(channel-1);
	return b;
  }
}

/** Sender for joint data of effects Block B. */
class BBSender extends SysexSender
{
  int parameter;
  byte []b = new byte [11];
  
  public BBSender(int param){
    parameter=param;
    b[0]=(byte)0xF0; b[1]=(byte)0x41; b[3]=(byte)0x2A; b[4]=(byte)0x12; b[5]=(byte)0x08;
    b[7]=(byte)parameter; b[10]=(byte)0xF7;
  }
  
  public byte [] generate (int value){
	b[8]=(byte)(value+6);b[2]=(byte)(channel-1);
	return b;
  }
}

/** Sender for expression assign data. */
class ESender extends SysexSender
{
  int parameter;
  byte []b = new byte [11];
  
  public ESender(int param){
    parameter=param;
    b[0]=(byte)0xF0; b[1]=(byte)0x41; b[3]=(byte)0x2A; b[4]=(byte)0x12; b[5]=(byte)0x08;
    b[7]=(byte)parameter;b[10]=(byte)0xF7;
  }
  
  public byte [] generate (int value){
	if (value==0) { b[8]=(byte)127; } 
	else { b[8]=(byte)(value-1); }

	b[2]=(byte)(channel-1);
	return b;
  }
}

/** Sender for expression min/max level data. */
class BigValSender extends SysexSender
{
  int parameter;
  byte []b = new byte [12];
  
  public BigValSender(int param){
    parameter=param;
    b[0]=(byte)0xF0; b[1]=(byte)0x41; b[3]=(byte)0x2A; b[4]=(byte)0x12; b[5]=(byte)0x08;
    b[7]=(byte)parameter;b[11]=(byte)0xF7;
  }
  
  public byte [] generate (int value){
	b[8]=(byte)(value/128); 
	b[9]=(byte)(value%128);
	b[2]=(byte)(channel-1);
	return b;
  }
}

/** Almost a direct copy of the MKSBitSender. */
class GP16BitSender extends SysexSender
{
  Patch patch;
  int ofs;
  int bit;
  byte [] b; 
  
  public GP16BitSender(Patch p, int o, int bt) {
    patch = p;
    ofs = o;
    bit = bt;
    b = new byte[] { (byte)0xF0, (byte)0x41, (byte)0x00, (byte)0x2A, (byte)0x12,
	(byte)0x08, (byte)0x00, (byte)ofs, (byte)0x00, (byte)0x00, (byte)0xF7 };
  }
  
  public byte [] generate (int value) {
ErrorMsg.reportStatus("GP16BitSender: got value "+(byte)value);
    int mask = ~(1 << bit);
    int bitfield = patch.sysex[ofs+8] & mask;
ErrorMsg.reportStatus("GP16BitSender: computed bitfield "+(byte)bitfield);
    if (value == 1)
      bitfield |= (1 << bit);
    b[2] = (byte)(channel - 1);
    b[8] = (byte)bitfield;
ErrorMsg.reportStatus("GP16BitSender: sending data "+(byte)bitfield);
    return b;
  }
}

/** Almost a direct copy of the MKSBitModel. */
class GP16BitModel extends ParamModel {
  int bit;
  public GP16BitModel(Patch p, int o, int b)
  {
    super(p,o);
    bit = b;
  }
  public void set(int i)
  {
    int mask = ~(1 << bit);
    int value = patch.sysex[ofs] & mask;
    if (i == 1)
      value |= (1 << bit);
ErrorMsg.reportStatus("GP16BitModel: setting status "+(byte)value+" to adress "+ofs);
    patch.sysex[ofs] = (byte)value;
  }
  public int get()
  {
ErrorMsg.reportStatus("GP16BitModel: getting status "+patch.sysex[ofs]+" from adress "+ofs);
    int mask = 1 << bit;
    if ((patch.sysex[ofs] & mask) > 0)
      return 1;
    else
      return 0;
  }
}

/** Model for representing joint data of effects Block B. */
class BBParamModel extends ParamModel {
	public BBParamModel(Patch patch, int offset) {
		super(patch,offset);
	}

	public void set(int i) {
		patch.sysex[ofs] = (byte) (i+6);
	}

	public int get() {
		return patch.sysex[ofs]-6;
	}
}

/** Model for expression assign data. */
class EParamModel extends ParamModel {
	public EParamModel(Patch patch, int offset) {
		super(patch,offset);
	}

	public void set(int i) {
		if (i==0)
			i=128;
		patch.sysex[ofs] = (byte)(i-1);
	}

	public int get() {
		int temp = patch.sysex[ofs];
		if (temp==127)
			temp=-1;
		return temp+1;
	}
}

/** Model for expression min/max level data. */
class BigValParamModel extends ParamModel {
	public BigValParamModel(Patch patch, int offset) {
		super(patch,offset);
	}

	public void set(int i) {
		patch.sysex[ofs] = (byte)(i/128);
		patch.sysex[ofs+1] = (byte)(i%128);
	}

	public int get() {
		return patch.sysex[ofs]*128+patch.sysex[ofs+1];
	}
}

/** We want the patch name field to update the name in the temporary memory of the GP-16. */
class RolandGP16PatchNameWidget extends PatchNameWidget {
	byte[] b = new byte [26];
	int channel;

	public RolandGP16PatchNameWidget(String label, IPatch patch) {
		super(label, patch);
		Driver driver = (Driver) getDriver();
		if (driver != null) {channel=driver.getDeviceID();}
		else {channel=1;}

		b[0]=(byte)0xF0; b[1]=(byte)0x41; b[2]=(byte)(channel-1);b[3]=(byte)0x2A;
		b[4]=(byte)0x12; b[5]=(byte)0x08; b[7]=(byte)0x64; b[25]=(byte)0xF7;
	}

/** We want a reaction when the patch name changes, not when patch name loses focus. */
	protected void createWidgets() {
		Driver driver = (Driver) getDriver();
		if (driver != null) {
			name = new JTextField(getPatch().getName(), 16);
		} else {
	    	name = new JTextField("Patch Name", 16);
		}
	
		name.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {}
			public void keyReleased(KeyEvent e) { eventListener(e); }
			public void keyTyped(KeyEvent e) {}
	    	});
    	}
	
/** The reaction that a keyrelease in the Name field causes. */
	protected void eventListener(KeyEvent e) {
		Driver driver = (Driver) getDriver();
		byte[] shortName = new byte[(name.getText()).length()];
		byte[] longName = new byte[16];
		int shortLength = shortName.length;
		if (shortLength > 16)
			shortLength = 16;
		
		try { 
			shortName = (name.getText()).getBytes("US-ASCII"); 
			longName = new String("                ").getBytes("US-ASCII");
		}
		catch (UnsupportedEncodingException ex) {}

		System.arraycopy(shortName, 0, longName,  0, shortLength);
		System.arraycopy(longName, 0, b,  8, 16);

		if (driver != null){
		    getPatch().setName(name.getText());
			driver.send(b);}
	}
}

/** The ScrollBarWidgets Expression Min/Max Level need to have variable base and subdivision. 
/*  and also send a sound change request after a scrollbar is moved.				*/
class ExpLevScrollBarWidget extends ScrollBarWidget implements ItemListener
{
    protected int subDiv;
    private Patch thisPatch;
        
    public ExpLevScrollBarWidget(String label, IPatch patch, int min, int max, int base, int labelWidth,
			   ParamModel pmodel, SysexSender sender, int initSubDiv) {
        super(label, patch, min, max, base, labelWidth, pmodel, sender);
    	subDiv = initSubDiv;
	thisPatch = (Patch)patch;
	text.setText(new Double((double)(getValue() + base)/subDiv).toString());
    }

    public ExpLevScrollBarWidget(String label, IPatch patch, int min, int max, int base, 
			   ParamModel pmodel, SysexSender sender, int initSubDiv) {
        this(label, patch, min, max, base, -1, pmodel, sender, initSubDiv);
    }

/** Invoked when the slider is moved, implements variable subdivision and sends sound change request. */
    protected void eventListener(ChangeEvent e) {
	int v = slider.getValue();
	text.setText(new Double((double)(v + base)/subDiv).toString());
	sendSysex(v);
	try {Thread.sleep(100); } catch (Exception ex){}
	((Driver) thisPatch.getDriver()).send(sndChange(((Driver) thisPatch.getDriver()).getChannel()));
    }

/** Generate the sound change request on correct channel. */
    private byte [] sndChange(int chn){
    	return new byte[] { (byte)0xF0, (byte)0x41, (byte)(chn-1), (byte)0x2A, (byte)0x12,
		(byte)0x08, (byte)0x00, (byte)0x75, (byte)0x00, (byte)0x0B, (byte)0xF7 };
    }

/** Listens to the Expression Assign combo box. */
    public void itemStateChanged(ItemEvent e) {
	int chosen = ((JComboBox)e.getSource()).getSelectedIndex()-1;

ErrorMsg.reportStatus("ExpLevScrollBarWidget: Received ItemEvent "+chosen);
	switch (chosen) {
	    case -1: setRange(0,0,0,1); break; 						// Assign Off
	    case 0: case 4: case 7: case 46: setRange(0,100,-50,1); break; 		// Tone -50 - 50
            case 1: case 2: case 3: case 5: case 6: case 8: case 10: case 11: case 12: case 15:
		case 16: case 17: case 18: case 19: case 20: case 22: case 25: case 28: case 31:
		case 32: case 33: case 34: case 35: case 36: case 37: case 38: case 39: case 40:
		case 41: case 42: case 43: case 47: case 48: case 50: case 51: case 56: case 57:
		case 58: case 59: case 64: case 65: case 66: case 67: case 68: case 69: case 70:
                setRange(0,100,0,1); break; 						// Regular 0 - 100
	    case 9: case 14: case 52: setRange(0,1,0,1); break; 			// On/Off etc. 0 - 1
	    case 13: case 23: case 26: setRange(0,40,10,10); break; 			// Q-Values 1 - 5
	    case 21: case 24: case 27: case 29: case 30: setRange(0,48,-24,2); break; 	// EQ Levels -12 - 12
	    case 44: setRange(0,200,-100,1); break; 					// PS Balance 0 - 200
	    case 45: setRange(0,24,-12,1); break;					// Flanger Chro -12 - 12
	    case 49: setRange(0,3,0,1); break;						// Space D-Mode 0 - 3
	    case 53: case 54: case 55: setRange(0,1200,0,1); break;			// Tap Delay 0 - 1200
	    case 60: case 63: setRange(0,200,0,1); break; 				// Cutoff 0 - 200
	    case 61: setRange(0,75,0,1); break;						// Reverb Decay 0 - 75
	    case 62: setRange(0,9,0,1); break;						// Reverb Mode 0 - 9
	}
    }
    
/** Set the range parameters of the RolandGP16ScrollBarWidget. */
    private void setRange(int newMin, int newMax, int newBase, int newSubDiv) {
ErrorMsg.reportStatus("ExpLevScrollBarWidget: Setting quadruple "+newMin+","+newMax+","+newBase+","+newSubDiv);
    	//setMinMax(newMin, newMax);
        setMin(newMin);
        setMax(newMax);
	base = newBase; subDiv = newSubDiv;
	setValue((newMin+newMax)/2);
    }
}

/** It is necessary to send a sound change request to update the sound in the processor. 
/*  The joint data, however, may not be sent as inconisistent.					*/
class GP16ComboBoxWidget extends ComboBoxWidget {

   private Patch thisPatch;
   private boolean autoUpdate;
   private GP16JointPolice updater;

   public GP16ComboBoxWidget(String l, IPatch p, int min, ParamModel ofs, SysexSender s, Object[] o, 
   	boolean  a, GP16JointPolice upd) {
   	super ( l, p, min, ofs, s, o);
	thisPatch = (Patch)p;
	autoUpdate = a;
	updater = upd;
   }
   
   public GP16ComboBoxWidget(String l, IPatch p, ParamModel ofs, SysexSender s, Object [] o, 
   	boolean a, GP16JointPolice upd) {
        this(l, p, 0, ofs, s, o, a, upd);
   }

   public GP16ComboBoxWidget(String l, IPatch p, ParamModel ofs, SysexSender s, Object [] o, 
   	boolean a) {
        this(l, p, 0, ofs, s, o, a, null);
   }

/** invoked when the an item is selected. Also send sound change request. */
   protected void eventListener(ItemEvent e) {
	if (e.getStateChange() == ItemEvent.SELECTED){
	    sendSysex(cb.getSelectedIndex() + getValueMin());
	    if (autoUpdate) {
	         try {Thread.sleep(100); } catch (Exception ex){}
	         ((Driver) thisPatch.getDriver()).send(sndChange(((Driver) thisPatch.getDriver()).getChannel()));
	    }
	    else
	    	updater.itemStateChanged();
	}
   }

/** Generate the sound change request on correct channel. */
    private byte [] sndChange(int chn){
    	return new byte[] { (byte)0xF0, (byte)0x41, (byte)(chn-1), (byte)0x2A, (byte)0x12,
		(byte)0x08, (byte)0x00, (byte)0x75, (byte)0x00, (byte)0x0B, (byte)0xF7 };
    }
}

/** It is necessary to send a sound change request to update the sound in the processor. */
class GP16CheckBoxWidget extends CheckBoxWidget {

    private Patch thisPatch;

    public GP16CheckBoxWidget(String l, IPatch p, ParamModel ofs, SysexSender s) {
	super(l, p, ofs, s);
	thisPatch = (Patch)p;
    }

/** invoked when the check box is toggled. Also send sound change request. */
    protected void eventListener(ItemEvent e) {
	if (e.getStateChange() == ItemEvent.SELECTED)
	    sendSysex(1);
	else
	    sendSysex(0);
	try {Thread.sleep(100); } catch (Exception ex){}
	((Driver) thisPatch.getDriver()).send(sndChange(((Driver) thisPatch.getDriver()).getChannel()));
    }

/** Generate the sound change request on correct channel. */
    private byte [] sndChange(int chn){
    	return new byte[] { (byte)0xF0, (byte)0x41, (byte)(chn-1), (byte)0x2A, (byte)0x12,
		(byte)0x08, (byte)0x00, (byte)0x75, (byte)0x00, (byte)0x0B, (byte)0xF7 };
    }
}

/** Requests a sound change after changes to the joint data, if the latter is consistent. */
class GP16JointPolice {
/** An array to store the Combos. */
	private Patch thisPatch;
	private int offset;

/** A simple constructor. */	
	public GP16JointPolice (Patch p, int ofs) {
		thisPatch = p; offset = ofs;
	}

/** This method gets called when an item is changed. Sends sound change req if joint data is ok. */
	public void itemStateChanged() {
ErrorMsg.reportStatus("GP16JointPolice: Got event, checking says "+sendOk());
		if (sendOk())
			((Driver) thisPatch.getDriver()).send(sndChange(((Driver) thisPatch.getDriver()).getChannel()));
	}

/** Checks if joint data is consistent. */
	private boolean sendOk(){
		int temp;
		boolean[] used = new boolean[] {false, false, false, false, false};
		for (int dum=0;dum<5;dum++){
			temp = thisPatch.sysex[dum+offset+8]-offset;
ErrorMsg.reportStatus("GP16JointPolice: dum="+dum+" temp="+temp+" used[temp]="+used[temp]);
			if (used[temp])
				return false;
			used[temp] = true;
		}
		return true;
	}

/** Generate the sound change request on correct channel. */
    private byte [] sndChange(int chn){
    	return new byte[] { (byte)0xF0, (byte)0x41, (byte)(chn-1), (byte)0x2A, (byte)0x12,
		(byte)0x08, (byte)0x00, (byte)0x75, (byte)0x00, (byte)0x0B, (byte)0xF7 };
    }
}