package core;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MidiMonitor extends JDialog
{
    private final MyEditorPane jt;

    public MidiMonitor()
    {
	super(PatchEdit.getRootFrame(),"JSynthLib Midi Monitor",false);
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
// 	    jt.setContentType("text/html");
// 	    FileInputStream in = new FileInputStream("documentation.html");
// 	    jt.read(in,(new HTMLEditorKit()).createDefaultDocument());//new HTMLDocument());
	    jt.setCaretPosition(0);
	    jt.setEditable(false);
	    jt.setFont(new Font("monospaced", Font.PLAIN, 12));
	    
	    // create an own panel for "clear" and "close" buttons
	    JPanel buttonPanel= new JPanel();
	    buttonPanel.setLayout (new BorderLayout());
	    
	    JButton ok = new JButton("Close");
	    ok.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			OKPressed();
		    }});
	    buttonPanel.add(ok,BorderLayout.EAST);

	    JCheckBox csm = new JCheckBox("Complete SysexMessages?",MidiUtil.getCSM());
	    csm.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
			MidiUtil.toggleCSM();
		}});
	    buttonPanel.add(csm,BorderLayout.CENTER);
	    
	    JButton clr = new JButton("Clear");
	    clr.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			jt.setText("");
		    }});
	    buttonPanel.add(clr,BorderLayout.WEST);

	    getContentPane().add(buttonPanel,BorderLayout.SOUTH);
	    getRootPane().setDefaultButton(ok);
	    setSize(500,400);

// 	    pane.getVerticalScrollBar().setValue(pane.getVerticalScrollBar().getMinimum());
	    Utility.centerDialog(this);
	} catch (Exception e) {
	    ErrorMsg.reportError("Error","Error opening Monitor",e);
	}

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

    void log (String s)
    {
	// move the selection at the end of text
	jt.select(Integer.MAX_VALUE, Integer.MAX_VALUE);
	jt.setEditable(true);
	jt.replaceSelection(s);
	jt.setEditable(false);
    }
    /*
    void log (int port,boolean in, byte []sysex,int length)
    {
	log("Port: " + port + (in ? " RECV " : " XMIT ")
	    + length+ " bytes :\n" + hexDump(sysex,length) + "\n");
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
    */
}
