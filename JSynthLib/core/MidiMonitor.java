package core;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.html.*;
import javax.swing.event.*;
import java.util.*;
import java.io.*;
import java.net.*;
public class MidiMonitor extends JDialog
{
 final MyEditorPane jt;
 MidiMonitor()
   {
     super(PatchEdit.instance,"JSynthLib Midi Monitor",false);
        JPanel container= new JPanel();
        container.setLayout (new BorderLayout());
	jt = new MyEditorPane(); 
	JScrollPane pane = new JScrollPane();
	pane.getViewport().add(jt);
	
	getContentPane().add (pane, BorderLayout.CENTER);
	 pane.getVerticalScrollBar ().addAdjustmentListener (new AdjustmentListener ()
        {
            public void adjustmentValueChanged (AdjustmentEvent e)
            {
		    jt.repaint ();
            }
        });
	try {
            //        jt.setContentType("text/html");
            //	FileInputStream in = new FileInputStream("documentation.html");
            //	jt.read(in,(new HTMLEditorKit()).createDefaultDocument());//new HTMLDocument());
      jt.setCaretPosition(0);
	 jt.setEditable(false);
      jt.setFont(new Font("monospaced", Font.PLAIN, 12));        
/*
     JButton ok = new JButton("Close");
        ok.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) {
                                   OKPressed();
			       }});
         getContentPane().add(ok,BorderLayout.SOUTH);
	getRootPane().setDefaultButton(ok);   
 */
            JButton clr = new JButton("Clear");
            clr.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    jt.setText("");
                }});
                getContentPane().add(clr,BorderLayout.SOUTH);
                
        setSize(500,400);
	

	 //pane.getVerticalScrollBar().setValue(pane.getVerticalScrollBar().getMinimum());
	centerDialog();
	}catch (Exception e) {ErrorMsg.reportError("Error","Error opening Monitor",e);}

	}
    protected void centerDialog() {
        Dimension screenSize = this.getToolkit().getScreenSize();
	Dimension size = this.getSize();
	screenSize.height = screenSize.height/2;
	screenSize.width = screenSize.width/2;
	size.height = size.height/2;
	size.width = size.width/2;
	int y = screenSize.height - size.height;
	int x = screenSize.width - size.width;
	this.setLocation(x,y);
    }

  void OKPressed() {
 	this.setVisible(false);
  }                           
  class MyEditorPane extends JEditorPane
   {
	  public void myScrollToReference (String s)  
	   {
		   super.scrollToReference(s.substring(1));
	   } 
   }
  void log (int port,boolean in, byte []sysex,int length)
  {
      // move the selection at the end of text
      jt.select(Integer.MAX_VALUE, Integer.MAX_VALUE);
	 jt.setEditable(true);
      jt.replaceSelection("Port: " + port + (in ? " RECV " : " XMIT ")
                          + length+ " bytes :\n"
                          + hexDump(sysex,length) + "\n");
      
	 jt.setEditable(false);

  }
  String hexDump(byte []data,int length){
    StringBuffer s = new StringBuffer();
    
  for (int i = 0; i < length; i++) {
      String sHex = Integer.toHexString((int) (data[i] & 0xFF));
      s.append(((sHex.length() == 1) ? "0" : "") + sHex+" ");
        s.append((i % 20 == 19) ? "\n" : "");
    }
  return s.toString();
  }
}
