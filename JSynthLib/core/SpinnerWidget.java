/* $Id$ */
package core;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
public class SpinnerWidget extends SysexWidget
{
    int base;
    JTextField text;
    public JSpinner spinner;
    
    
    /** Constructor for setting up the ScrollBarWidget without an initial value.
     * @param l
     * @param p
     * @param min
     * @param max
     * @param b
     * @param ofs
     * @param s  */    
    public SpinnerWidget (String l,Patch p,int min, int max,int b,ParamModel ofs,SysexSender s)
    {
        valueMin=min;
        valueMax=max;
        paramModel=ofs;
        sysexString=s;
        setValue (p);
        base=b;
        label=l;
        patch=p;
        setup ();
    }
    
    /** Constructor for setting up the ScrollBarWidget including an initial value.
     * @param l Label for the Widget
     * @param p The patch, which is edited
     * @param min Minimum value
     * @param max Maximum value
     * @param b base value. This value is added to the actual value for display purposes
     * @param ofs Offset of the parameter in the patch
     * @param s ysexSender for transmitting the value at editing the parameter
     * @param valueInit initial value, displayed at construction of the widget
     */    
    public SpinnerWidget (String l,Patch p,int min, int max,int b,ParamModel ofs,SysexSender s,int valueInit)
    {
        this(l,p,min,max,b,ofs,s);
        setValue (valueInit);
    }
    
    public void setup ()
    {
        super.setup ();
        setLayout (new BorderLayout ());
        jlabel= (new JLabel (label));
        add (jlabel,BorderLayout.WEST);
        
        if (valueCurr>valueMax) valueCurr=valueMax;

        SpinnerNumberModel model = new SpinnerNumberModel(valueCurr+base,valueMin+base,valueMax+base,1);
        spinner = new JSpinner(model);
        spinner.addChangeListener (new ChangeListener ()
        {
            public void stateChanged (ChangeEvent e)
            {
                setValue (((Integer)spinner.getValue ()).intValue()-base);	// Maybe the displayed value differ from sysex value for 'base'
                sendSysex ();
            }
        });
        add (spinner,BorderLayout.CENTER);
            
    }
    public void setValue (int v)
    {
      super.setValue (v);
      spinner.setValue (new Integer(v+base));	// Maybe the displayed value differ from sysex value for 'base'
    }
    public void setMinMax (int min, int max)
    {
        valueMin=min;valueMax=max;
        if (valueCurr>max) valueCurr=max;
        if (valueCurr<min) valueCurr=min;
        ((SpinnerNumberModel)(spinner.getModel())).setMinimum (new Integer(min));
        ((SpinnerNumberModel)(spinner.getModel())).setMaximum (new Integer(max));
        spinner.setValue (new Integer(valueCurr));
    }
    
    public void setEnabled(boolean e)
    {
        spinner.setEnabled (e);
    }                           
}