/*
 * $Id$
 */
package core;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;

public class PatchNameWidget extends SysexWidget {
    /** JTextField object */
    protected JTextField name;

    /**
     * Creates a new <code>PatchNameWidget</code> instance.
     *
     * @param label a label text.
     * @param patch a <code>Patch</code>, which is edited.
     * @see SysexWidget
     */
    public PatchNameWidget(String label, Patch patch) {
	super(label, patch, null, null);

	createWidgets();
        layoutWidgets();
    }

    /** @deprecated Use PatchNameWidget(String, Patch) */
    // The order of argument is not consistent with others!!!FIXIT!!!
    public PatchNameWidget(Patch patch, String label) {
	super(label, patch, null, null);

	createWidgets();
        layoutWidgets();
    }

    protected void createWidgets() {
	Driver driver = getDriver();
	if (driver != null) {
	    name = new JTextField(driver.getPatchName(getPatch()),
				  driver.patchNameSize);
	} else {
	    name = new JTextField("Patch Name", 0);
	}
	name.addFocusListener(new FocusListener() {
		public void focusGained(FocusEvent e) {
		}
		// No system exclusive messages is sent.
		public void focusLost(FocusEvent e) {
		    eventListener(e);
		}
	    });
    }

    /**
     * invoked when focus is lost from the text field.
     * <code>driver.setPatchName()</code> is called.
     */
    protected void eventListener(FocusEvent e) {
	Driver driver = getDriver();
	if (driver != null)
	    driver.setPatchName(getPatch(), name.getText());
    }

    protected void layoutWidgets() {
	setLayout(new BorderLayout());
        add(getJLabel(), BorderLayout.WEST);
        add(name, BorderLayout.EAST);
    }

    public void setEnabled(boolean e) {
        name.setEnabled(e);
    }
}
