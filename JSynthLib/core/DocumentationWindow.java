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
public class DocumentationWindow extends JDialog
{
    DocumentationWindow() {
        super(PatchEdit.getInstance(),"JSynthLib Documentation Viewer",false);
        JPanel container= new JPanel();
        container.setLayout (new BorderLayout());
	final MyEditorPane jt = new MyEditorPane(); 
	jt.addHyperlinkListener(new HyperlinkListener() {
	    public void hyperlinkUpdate (HyperlinkEvent e) {
		if (e.getEventType()==HyperlinkEvent.EventType.ACTIVATED) {
		    if ((e.getDescription()).startsWith("#")) {
		    // Link of the same file
		        try{
			    jt.myScrollToReference(e.getDescription());
			} catch (Exception e2){
			    ErrorMsg.reportError("Error",e.getDescription().toString(),e2);
			}
		    } else {
	  	    // Link to other file
			try {
		            jt.setPage(e.getURL());
			} catch (UnknownHostException uhe) {
			    JOptionPane.showMessageDialog(getContentPane(),"Unknown Host \""+e.getURL()+"\". Maybe you're not online.");
			} catch (Exception e3) {
			    ErrorMsg.reportError("Error",e.getURL().toString(),e3);
			}
		    }		
	        }
	    }
        });
    	JScrollPane pane = new JScrollPane();
	pane.getViewport().add(jt);
	
	getContentPane().add (pane, BorderLayout.CENTER);
	 pane.getVerticalScrollBar ().addAdjustmentListener (new AdjustmentListener () {
            public void adjustmentValueChanged (AdjustmentEvent e) {
		    jt.repaint ();
            }
        });
	try {
            jt.setContentType("text/html");
	    //FileInputStream in = new FileInputStream("doc/documentation.html");
	    //jt.read(in,(new HTMLEditorKit()).createDefaultDocument());//new HTMLDocument());
	    jt.setPage(new java.net.URL("file:./doc/documentation.html"));
            jt.setCaretPosition(0);
	    jt.setEditable(false);
	  
            JButton ok = new JButton("Close");
            ok.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
                    OKPressed();
		}
	    });
            getContentPane().add(ok,BorderLayout.SOUTH);
	    getRootPane().setDefaultButton(ok);   
            setSize(500,400);

	    //pane.getVerticalScrollBar().setValue(pane.getVerticalScrollBar().getMinimum());
	    centerDialog();
	} catch (Exception e) {
	    ErrorMsg.reportError("Error","Error opening documentation",e);
	}

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
        public void myScrollToReference (String s)  {
	    super.scrollToReference(s.substring(1));
	} 
    }
}
