package core;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
/**
 * @version $Id$
 */
public class ComboBoxWidget extends SysexWidget
{
    public JComboBox cb;
    String []options;
    public ComboBoxWidget (String l,Patch p,int min,ParamModel ofs,SysexSender s, String []o)
    {
        valueMin=min;
        valueMax=valueMin+o.length-1;
        paramModel=ofs;
        sysexString=s;
        setValue (p);label=l;patch=p;
        options=o;
        setup ();
    }
    
    public ComboBoxWidget (String l,Patch p,ParamModel ofs,SysexSender s, String []o)
    {
        this(l, p, 0, ofs, s, o);
    }

    /** Constructor for setting up the ComboBoxWidget including an initial value.
     * @param l Label for the Widget
     * @param p The patch, which is edited
     * @param ofs Offset of the parameter in the patch
     * @param s SysexSender for transmitting the value at editing the parameter
     * @param o Array, which contains the list of the options in the combobox
     * @param valueInit Initial value, displayed at construction of the widget
     */    
    public ComboBoxWidget (String l,Patch p,ParamModel ofs,SysexSender s, String []o,int valueInit)
    {
    this(l,p,ofs,s,o);
    setValue(valueInit);
    }
    
    public void setup ()
    {
        super.setup ();
        setLayout (new FlowLayout ());
        if (valueCurr>valueMax)
            valueCurr=valueMax;
        cb=new JComboBox (options);
        cb.setSelectedIndex (valueCurr - valueMin);
        cb.addItemListener (new ItemListener ()
        {
            public void itemStateChanged (ItemEvent e)
            {
                valueCurr=cb.getSelectedIndex () + valueMin;
                sendSysex ();
            }
        }
        );
        
        jlabel=(new JLabel (label));
        add (jlabel);
        cb.setMaximumSize (new Dimension (125,25));
        add (cb);
        
    }
    public void setValue (int v)
    {super.setValue (v); cb.setSelectedIndex (v - valueMin);}
}


