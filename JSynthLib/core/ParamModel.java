package core;

/**
 * An implementation of IParamModel for Patch class.
 * @see SysexWidget.IParamModel
 * @see Patch
 */
public class ParamModel implements SysexWidget.IParamModel {

    /** <code>Patch</code> data. */
    protected Patch patch;
    /** Offset of the data for which this model is. */
    protected int ofs;

    /**
     * Left as private not to be used by mistake.
     * @deprecated Use ParamModel(Patch, int) or implement
     *             SysexWidget.IParamModel.
     */
    private ParamModel() {
    }

    /**
     * Creates a new <code>ParamModel</code> instance.
     *
     * @param patch a <code>Patch</code> value
     * @param offset an offset in <code>patch.sysex</code>.
     */
    public ParamModel(Patch patch, int offset) {
	this.ofs = offset;
	this.patch = patch;
    }

    // SysexWidget.IParamModel interface methods
    /** Set a parameter value <code>value</code>. */
    public void set(int value) {
	patch.sysex[ofs] = (byte) value;
    }

    /** Get a parameter value. */
    public int get() {
	return patch.sysex[ofs];
    }
}
