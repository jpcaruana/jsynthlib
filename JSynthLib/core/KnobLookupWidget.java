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
    public KnobLookupWidget(IPatch patch, Parameter param) {
        super(patch, param);

        mValueLabels = param.getValues();
        mKnob.setToolTipText(mValueLabels[getValue()]);
    }
    private String[] mValueLabels;

    /**
     * Creates a new <code>KnobWidget</code> instance.
     *
     * @param label label String.
     * @param patch a <code>Patch</code> value.
     * @param min minimum value.
     * @param max maximum value. (Why this is required? This can be
     * calculated by using valueLabels.legnth.)
     * @param pmodel a <code>ParamModel</code> object.
     * @param sender a <code>SysexSender</code> object.
     * @param valueLabels an array of labels for the knob values.
     */
    public KnobLookupWidget(String label, IPatch patch, int min, int max,
			    IParamModel pmodel, ISender sender,
			    String[] valueLabels) {
	super(label, patch, min, max, 0, pmodel, sender);
        //mBase = 0;
	mValueLabels = valueLabels;
	mKnob.setToolTipText(mValueLabels[getValue()]);
    }

    /**
     * Creates a new <code>KnobWidget</code> instance.
     *
     * @param label label String.
     * @param patch a <code>Patch</code> value.
     * @param min minimum value.
     * @param pmodel a <code>ParamModel</code> object.
     * @param sender a <code>SysexSender</code> object.
     * @param valueLabels an array of labels for the knob values.
     */
    public KnobLookupWidget(String label, IPatch patch, int min,
			    IParamModel pmodel, ISender sender,
			    String[] valueLabels) {
	super(label, patch, min, min + valueLabels.length - 1, 0, pmodel, sender);
        //mBase = 0;
	mValueLabels = valueLabels;
	mKnob.setToolTipText(mValueLabels[getValue()]);
    }

    /** <code>min</code> is set to 0. */
    public KnobLookupWidget(String label, IPatch patch,
			    IParamModel pmodel, ISender sender,
			    String[] valueLabels) {
	this(label, patch, 0, pmodel, sender, valueLabels);
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
