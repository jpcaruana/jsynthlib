package core;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import com.dreamfabric.DKnob;

/**
	Widget de type potentiometre rotatif.
*/
public class KnobLookupWidget extends KnobWidget {
	
	private String[] mValueLabels;
	
	public KnobLookupWidget(String l, Patch p, ParamModel ofs,SysexSender s, String[] aValueLabels) {
		this(l, p, 0, aValueLabels.length-1, ofs, s, aValueLabels);
	}

	public KnobLookupWidget(String l, Patch p, int aMin, int aMax, ParamModel ofs,SysexSender s, String[] aValueLabels) {
		super(l, p, aMin, aMax, ofs, s);
        mBase=0;
		mValueLabels = aValueLabels;
        setupUI();
		//		System.out.println("value = "+getValue());
		mKnob.setToolTipText(mValueLabels[getValue()]);
	}

	
	protected void setupListener() {
	    // Add a change listener to the knob
	    mKnob.addChangeListener(new ChangeListener() {
		    public void stateChanged(ChangeEvent e) {
				DKnob t = (DKnob) e.getSource();
				int oValue = Math.round(t.getValue()*(valueMax - valueMin)) + valueMin;
				t.setToolTipText(mValueLabels[oValue]);
				t.setValueAsString(mValueLabels[oValue]);
				KnobLookupWidget.super.setValue(oValue);
				//System.out.println("value = "+(oValue + mBase));
				sendSysex();
		    }
		});
	}
	
}

