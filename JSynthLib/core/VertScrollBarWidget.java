package core;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
/**
 * @version $Id$
 */
public class VertScrollBarWidget extends ScrollBarWidget
{
  int base;
  JTextField text;
  public JSlider slider;
  
  public VertScrollBarWidget(String l,Patch p,int min, int max,int b,ParamModel ofs,SysexSender s)
  {super(l,p,min,max,b,ofs,s);}
 
  public void setup()
  {
  super.setup();  
  setLayout(new BorderLayout());
   add (new JLabel(label),BorderLayout.NORTH);
   if (valueCurr>valueMax) valueCurr=valueMax;
    slider=new JSlider(JSlider.VERTICAL,valueMin,valueMax,valueCurr);
    slider.addChangeListener(new ChangeListener() {
	   public void stateChanged(ChangeEvent e) {
	      setValue(slider.getValue());
	      sendSysex();
              text.setText(new Integer(valueCurr+base).toString()); 
                              }});
   text = new JTextField(new Integer(valueCurr+base).toString(),4);
    slider.setMinimumSize(new Dimension(25,50));
    slider.setMaximumSize(new Dimension(25,100));

    add (slider,BorderLayout.CENTER);add(text,BorderLayout.SOUTH);
    text.setEditable(false);
    
    
  }

}


