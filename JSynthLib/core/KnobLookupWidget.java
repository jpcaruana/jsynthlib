package core;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import com.dreamfabric.DKnob;

/**
 * A rotary knob type widget.
 */
public class KnobLookupWidget extends KnobWidget {
    private String[] mValueLabels;

    public KnobLookupWidget(String l, Patch p, int aMin, int aMax,
			    ParamModel ofs, SysexSender s,
			    String[] aValueLabels) {
	super(l, p, aMin, aMax, 0, ofs, s);
        //mBase = 0;
	mValueLabels = aValueLabels;
	mKnob.setToolTipText(mValueLabels[getValue()]);
    }

    public KnobLookupWidget(String l, Patch p,
			    ParamModel ofs, SysexSender s,
			    String[] aValueLabels) {
	this(l, p, 0, aValueLabels.length - 1, ofs, s, aValueLabels);
    }

    protected void eventListener(ChangeEvent e) {
	DKnob t = (DKnob) e.getSource();
	int oValue = Math.round(t.getValue() * (getValueMax() - getValueMin()))
	    + getValueMin();
	t.setToolTipText(mValueLabels[oValue]);
	t.setValueAsString(mValueLabels[oValue]);
	sendSysex(oValue);
	//System.out.println("value = "+(oValue + mBase));
    }
}
