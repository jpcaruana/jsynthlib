/*
 * $Id$
 */
package core;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;

public class PatchNameWidget extends SysexWidget {
    private JTextField name;

    /**
     * Creates a new <code>PatchNameWidget</code> instance.
     *
     * @param l a label text.
     * @param p a <code>Patch</code>, which is edited.
     * @see SysexWidget
     */
    public PatchNameWidget(String label, Patch patch) {
	super(label, patch);
        setup();
    }

    /** @deprecated Use Patch(String, Patch) */
    // The order of argument is not consistent with others!!!FIXIT!!!
    public PatchNameWidget(Patch patch, String label) {
	super(label, patch);
        setup();
    }

    protected void setup() {
	setLayout(new BorderLayout());
        add(getJLabel(), BorderLayout.WEST);
        name = new JTextField(getPatchName(), getPatchNameSize());
	name.addFocusListener(new FocusListener() {
		public void focusGained(FocusEvent e) {
		}
		// No system exclusive messages is sent.
		public void focusLost(FocusEvent e) {
		    setPatchName(name.getText());
		}
	    });
        add(name, BorderLayout.EAST);
    }

    public void setEnabled(boolean e) {
        name.setEnabled(e);
    }
}
