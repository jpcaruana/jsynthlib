package core;
import java.awt.BorderLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;

/**
 * SysexWidget for patch name.
 * @version $Id$
 */
public class PatchNameWidget extends SysexWidget {
    /** JTextField object */
    protected JTextField name;
    protected int patchNameSize;

    /**
     * Creates a new <code>PatchNameWidget</code> instance.
     * @param label a label text.
     * @param patch a <code>Patch</code>, which is edited.
     * @param patchNameSize TODO
     */
    public PatchNameWidget(String label, IPatch patch) {
        this(label, patch, ((Driver)patch.getDriver()).patchNameSize);
    }

    public PatchNameWidget(String label, IPatch patch, int _patchNameSize) {
        super(label, patch, null, null);
        
        patchNameSize = _patchNameSize;
        createWidgets();
        layoutWidgets();
    }
    
    protected void createWidgets() {
	if (getDriver ()!= null) {
	    name = new JTextField(getPatch().getName(),
				  patchNameSize);
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
	IPatchDriver driver = getDriver();
	if (driver != null)
	    getPatch().setName(name.getText());
    }

    /** Adds an <code>FocusListener</code> to the JTextField. */
    public void addEventListener(FocusListener l) {
	name.addFocusListener(l);
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
