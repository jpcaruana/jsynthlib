package synthdrivers.YamahaFS1R;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import core.ComboBoxWidget;
import core.IPatch;
import core.ParamModel;
import core.SysexSender;

public class ComboActionWidget extends ComboBoxWidget
{
	protected ComboActionListener mListener;
    public ComboActionWidget (String l,IPatch p,ParamModel ofs,SysexSender s, String []o, ComboActionListener aListener)
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


