package synthdrivers.YamahaFS1R;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import core.*;

public class ComboActionWidget extends ComboBoxWidget
{
	protected ComboActionListener mListener;
    public ComboActionWidget (String l,Patch p,ParamModel ofs,SysexSender s, String []o, ComboActionListener aListener)
    {
		super(l, p, ofs, s, o);
		mListener = aListener;
		cb.addItemListener (new ItemListener ()
        {
            public void itemStateChanged (ItemEvent e)
            {
                mListener.notifyChange(cb.getSelectedIndex());
			}
        }
        );
    }
    
}


