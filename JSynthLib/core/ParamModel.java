package core;

/**
 * A Paramter Model is a class to keep track of the changes to the
 * patch so that when we next call up this patch the changes are
 * there. This is also used to set the widgets to the correct values
 * for a particular patch when the Single Editor is opened.
 *
 * All you need is <code>set(int)</code> and <code>get()</code>
 * method.
 */
public class ParamModel {
	/* XXX: I'm not planning to use this with XML drivers, so I won't bother
	 *      making this use IPatches atm.
	 */
	/** <code>Patch</code> data. */
    protected Patch patch;
    /** Offset of the data for which this model is. */
    protected int ofs;


    /**
     * Creates a new <code>ParamModel</code> instance.
     */
    // @deprecated Use ParamModel(Patch) or ParamModel(Patch, int). */
    public ParamModel() {	// Can be removed?
    }

    /**
     * Creates a new <code>ParamModel</code> instance.
     *
     * @param patch a <code>Patch</code> value
     */
    public ParamModel(Patch patch) {
	this.patch = patch;
    }

    /**
     * Creates a new <code>ParamModel</code> instance.
     *
     * @param patch a <code>Patch</code> value
     * @param offset an offset in <code>patch.sysex</code>.
     */
    public ParamModel(Patch patch, int offset) {
	ofs = offset;
	this.patch = patch;
    }

    /** Set a parameter value <code>i</code>. */
    public void set(int i) {
	patch.sysex[ofs] = (byte) i;
    }

    /** Get a parameter value. */
    public int get() {
	return patch.sysex[ofs];
    }
}
