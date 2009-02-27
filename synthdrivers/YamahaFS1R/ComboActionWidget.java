package synthdrivers.YamahaFS1R;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import org.jsynthlib.core.ComboBoxWidget;
import org.jsynthlib.core.ParamModel;
import org.jsynthlib.core.Patch;
import org.jsynthlib.core.SysexSender;


public class ComboActionWidget extends ComboBoxWidget {
    protected ComboActionListener mListener;

    public ComboActionWidget(String l, Patch p, ParamModel ofs, SysexSender s,
            String[] o, ComboActionListener aListener) {
        super(l, p, ofs, s, o);
        mListener = aListener;
        cb.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                mListener.notifyChange(cb.getSelectedIndex());
            }
        });
    }

}

