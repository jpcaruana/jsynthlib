package core;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
public class LabelWidget extends SysexWidget
{
    String label;
    public LabelWidget (String n)
    {
        label=n;
        setup ();
    }
    public LabelWidget (JLabel jl)
    {
        label=jl.toString ();
        add (jl);
    }
    public void setup ()
    {
        // super.setup();
        //  setLayout(new BorderLayout());
        add (new JLabel (label));
        //add (new JLabel(label),BorderLayout.CENTER);
    }
    
}


