/*
 * $Id$
 */
package core;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;

public class PatchNameWidget extends SysexWidget {
  int base;
  JTextField name;
  String label;
 
    public PatchNameWidget(Patch p, String n) {
        patch = p;
        label = n;
        setup();
    }
    
    public void setup() {
 // super.setup();  
  setLayout(new BorderLayout());
        add(new JLabel(label), BorderLayout.WEST);
        name = new JTextField(((Driver) (PatchEdit.getDriver(patch.deviceNum, patch.driverNum))).getPatchName(patch),
			      ((Driver) (PatchEdit.getDriver(patch.deviceNum, patch.driverNum))).patchNameSize);
    name.addFocusListener(new FocusListener() {
		public void focusGained(FocusEvent e) {
		}
            public void focusLost(FocusEvent e) {
		    ((Driver) (PatchEdit.getDriver(patch.deviceNum, patch.driverNum))).setPatchName(patch, name.getText());
            }
	   });
        add(name, BorderLayout.EAST);
  }

}
