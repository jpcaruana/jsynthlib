package core;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;

public class CheckBoxWidget extends SysexWidget {
  public JCheckBox cb;

    public CheckBoxWidget(String l, Patch p, ParamModel ofs, SysexSender s) {
	paramModel = ofs;
	sysexString = s;
	setValue(p);
	label = l;
	patch = p;
	setup();
    }

    public void setSliderNum(int num) {
	sliderNum = num;
	if (num > 0)
	    cb.setToolTipText("Bank " + ((num - 1) / 16)
			      + "  Slider " + (((num - 1) % 16) + 1));
	if (num < 0) {
	    num = 0 - num;
	    cb.setToolTipText("Bank " + ((num - 1) / 16)
			      + "  Button " + (((num - 1) % 16) + 1));
	}
 }

    public void setup() {
  super.setup();  
  setLayout(new BorderLayout());
	if (valueCurr > 1)
	    valueCurr = valueMax;
	if (valueCurr == 0)
	    cb = new JCheckBox(label, false);
	else
	    cb = new JCheckBox(label, true);
    cb.addItemListener(new ItemListener() {
	   public void itemStateChanged(ItemEvent e) {
		    if (e.getStateChange() == ItemEvent.SELECTED)
			valueCurr = 1;
		    else
			valueCurr = 0;
	      sendSysex();
                     }});
    add(cb);
    }
    
    public void setValue(int v) {
	super.setValue(v);
	cb.doClick();
  }
}
