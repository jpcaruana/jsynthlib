package org.jsynthlib.drivers.yamaha.fs1r;

import javax.swing.Box;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import org.jsynthlib.core.ComboBoxWidget;
import org.jsynthlib.core.ParamModel;
import org.jsynthlib.core.Patch;
import org.jsynthlib.core.PatchEditorFrame;
import org.jsynthlib.core.SpinnerWidget;
import org.jsynthlib.core.SysexSender;

 
/**
	Editor for system parameters. There are lots of parameters still to add,
	I made the most important.  
	@author denis queffeulou mailto:dqueffeulou@free.fr
 */
class YamahaFS1RSystemEditor extends PatchEditorFrame
{

	public YamahaFS1RSystemEditor(Patch patch)
	{
		super ("Yamaha FS1R System Editor",patch);   
		// Master Pane
		Box oMasterPane = Box.createVerticalBox();
		//gbc.weightx = 1;     
		oMasterPane.add(new SpinnerWidget("Tune", patch, 0, 127, -64, new FS1RModel(patch, 0), new FS1RSender(0)));
		oMasterPane.add(new SpinnerWidget("Note shift", patch, 0, 127, -64, new FS1RModel(patch, 6), new FS1RSender(6)));
		oMasterPane.add(new ComboBoxWidget("Vel curve", patch, new FS1RModel(patch, 0x0E), new FS1RSender(0x0E), new String []{"thru", "soft1","soft2","wide","hard"}));
		oMasterPane.add(new ComboBoxWidget("BC curve", patch, new FS1RModel(patch, 0x0D), new FS1RSender(0x0D), new String []{"thru", "1","2","3"}));
		oMasterPane.add(new ComboBoxWidget("KN CtrlMode", patch, new FS1RModel(patch, 0x0B), new FS1RSender(0x0B), new String []{"abs", "rel"}));
			
		oMasterPane.setBorder(new TitledBorder(LineBorder.createGrayLineBorder(), "Master", TitledBorder.CENTER, TitledBorder.CENTER));  
		scrollPane.add(oMasterPane,gbc);
	
		pack();
  }

static class FS1RSender extends SysexSender
{
	private int parameter; 
	private byte []b = new byte [10];
  
	public FS1RSender(int param) 
	{
		parameter=param;
		b[0]=(byte)0xF0; 
		b[1]=(byte)0x43;
		b[2]=(byte)0x10;
		b[3]=(byte)0x5E; 
		b[4]=0; 
		b[5]=0;
		b[6]=(byte)parameter ;
		b[9]=(byte)0xF7;
	}

	public byte [] generate (int value)
	{ 
		b[7]=(byte)((value/128));
		b[8]=(byte)(value&127);
		return b;
	}
}


	class FS1RModel extends ParamModel
	{ 
	/**
		@param offset dans la table
	*/
	public FS1RModel(Patch p,int offset) {
		super(p, offset + YamahaFS1RSystemDriver.DATA_OFFSET);
	}
	public void set(int i) {
		patch.sysex[ofs] = (byte)(i & 127);
	}
	public int get() {
		return patch.sysex[ofs];
	}
	
	}

}
