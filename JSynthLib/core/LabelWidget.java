package core;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;

public class LabelWidget extends SysexWidget {

    /**
     * Creates a new <code>LabelWidget</code> instance.
     *
     * @param l a label text.
     * @see SysexWidget
     */
    public LabelWidget(String label) {
	super(label);
        add(getJLabel());
    }

    /**
     * Creates a new <code>LabelWidget</code> instance.
     *
     * @param jl a JLabel widget.
     */
    public LabelWidget(JLabel jl) {
        super(jl.toString());
	setJLabel(jl);		// overwrite jlabel
        add(jl);
    }
    /*
    private void setup() {
        //  setLayout(new BorderLayout());
        add(new JLabel(label));
        //add(new JLabel(label), BorderLayout.CENTER);
    }
    */
    public void setEnabled(boolean e) {
        getJLabel().setEnabled(e);
    }
}
