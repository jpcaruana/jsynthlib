package core;

import javax.swing.event.ChangeEvent;

import com.dreamfabric.DKnob;

/**
 * A rotary knob widget with label of values.
 * @version $Id$
 * @author denis queffeulou mailto:dqueffeulou@free.fr
 * @see KnobWidget
 */
public class KnobLookupWidget extends KnobWidget {
    private String[] mValueLabels;

    /**
     * Creates a new <code>KnobWidget</code> instance.
     *
     * @param l label String.
     * @param p a <code>Patch</code> value.
     * @param min minimum value.
     * @param max maximum value. (Why this is required? This can be
     * calculated by using valueLabels.legnth.)
     * @param ofs a <code>ParamModel</code> object.
     * @param s a <code>SysexSender</code> object.
     * @param valueLabels an array of labels for the knob values.
     */
    public KnobLookupWidget(String l, Patch p, int min, int max,
			    ParamModel ofs, SysexSender s,
			    String[] valueLabels) {
	super(l, p, min, max, 0, ofs, s);
        //mBase = 0;
	mValueLabels = valueLabels;
	mKnob.setToolTipText(mValueLabels[getValue()]);
    }

    /**
     * Creates a new <code>KnobWidget</code> instance.
     *
     * @param l label String.
     * @param p a <code>Patch</code> value.
     * @param min minimum value.
     * @param ofs a <code>ParamModel</code> object.
     * @param s a <code>SysexSender</code> object.
     * @param valueLabels an array of labels for the knob values.
     */
    public KnobLookupWidget(String l, Patch p, int min,
			    ParamModel ofs, SysexSender s,
			    String[] valueLabels) {
	super(l, p, min, min + valueLabels.length - 1, 0, ofs, s);
        //mBase = 0;
	mValueLabels = valueLabels;
	mKnob.setToolTipText(mValueLabels[getValue()]);
    }

    /** <code>min</code> is set to 0. */
    public KnobLookupWidget(String l, Patch p,
			    ParamModel ofs, SysexSender s,
			    String[] valueLabels) {
	this(l, p, 0, ofs, s, valueLabels);
    }

    /** invoked when the knob is moved. */
    protected void eventListener(ChangeEvent e) {
	DKnob t = (DKnob) e.getSource();
	int oValue = Math.round(t.getValue() * (getValueMax() - getValueMin()))
	    + getValueMin();
	t.setToolTipText(mValueLabels[oValue]);
	t.setValueAsString(mValueLabels[oValue]);
	sendSysex(oValue);
	//ErrorMsg.reportStatus("value = "+(oValue + mBase));
    }
}
