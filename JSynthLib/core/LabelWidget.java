package core;
import javax.swing.JLabel;

/**
 * A Label widget.
 * @version $Id$
 * @see SysexWidget
 */
public class LabelWidget extends SysexWidget {
    /**
     * Creates a new <code>LabelWidget</code> instance.
     *
     * @param label a label text.
     */
    public LabelWidget(String label) {
	super(label, null, null, null);

	layoutWidgets();
	createWidgets();
    }

    /**
     * Creates a new <code>LabelWidget</code> instance.
     *
     * @param jl a JLabel widget.
     * @deprecated
     */
    public LabelWidget(JLabel jl) {
        super(jl.toString(), null, null, null);
	setJLabel(jl);		// overwrite jlabel

	layoutWidgets();
    }

    protected void createWidgets() {
	// do nothing
    }

    protected void layoutWidgets() {
        //setLayout(new BorderLayout());
        add(getJLabel());
        //add(new JLabel(label), BorderLayout.CENTER);
    }

    public void setEnabled(boolean e) {
        getJLabel().setEnabled(e);
    }
}
