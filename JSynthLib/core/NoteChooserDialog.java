package core;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;
import javax.swing.JDialog;
import javax.swing.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.metal.*;
import java.beans.*;
import java.util.*;
import java.awt.event.*;
import java.awt.*;
import java.io.*;
public class NoteChooserDialog extends JDialog {
    // AppConfig object used to store actual config values
    private final AppConfig appConfig;

	//int note;
	//int velocity;
	//int delay;
    final JTextField t1 =new JTextField("0",5); 
    final JTextField t2 =new JTextField("0",5);
    final JTextField t3 =new JTextField("0",5);
    final JSlider s1 = new JSlider(JSlider.HORIZONTAL,0,120, 0); //note
    final JSlider s2 = new JSlider(JSlider.HORIZONTAL,0,127, 0); //velocity
    final JSlider s3 = new JSlider(JSlider.HORIZONTAL,0,2000, 0); //delay
    final String [] noteName = new String [] {"C","C#","D","D#","E","F","F#","G","G#","A","A#","B"};   

    /**
     * Constructor
     * @param Parent the parent JFrame
     * @param pAppConfig the application config object
     */
  public NoteChooserDialog(JFrame Parent, AppConfig pAppConfig) {
        super(Parent,"Note Chooser",false);
		this.appConfig = pAppConfig;

        JPanel container= new JPanel();
        container.setLayout (new BorderLayout());
        JPanel p4 = new JPanel();
        p4.setLayout (new ColumnLayout());
	JPanel p1 = new JPanel();
	JLabel l1 = new JLabel("Midi Note : ");
        JPanel p2 = new JPanel();
	JLabel l2 = new JLabel("Velocity  :  ");
        JPanel p3 = new JPanel();
	JLabel l3 = new JLabel("Duration  :");
	JLabel l4= new JLabel("msec");
	p1.add(l1); p2.add(l2);p3.add(l3);

	JPanel buttonPanel = new JPanel();
	JButton ok = new JButton("Close");
        ok.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) {
                                   OKPressed();
			       }});
        buttonPanel.add( ok );
	getRootPane().setDefaultButton(ok);
        p4.add(p1);
	p4.add(p2);
	p4.add(p3);
     

        s1.addChangeListener(new ChangeListener() {
	                     public void stateChanged(ChangeEvent e) {
							 int note = s1.getValue();
         	               appConfig.setNote(note);
   		               t1.setText(noteName[note%12]+note/12); 
                              }});
        s2.addChangeListener(new ChangeListener() {
	                     public void stateChanged(ChangeEvent e) {
							 int velocity = s2.getValue();
         	               appConfig.setVelocity(velocity);
			       t2.setText(new Integer(velocity).toString()); 
	                     }});
        s3.addChangeListener(new ChangeListener() {
	                     public void stateChanged(ChangeEvent e) {
         	               int delay=s3.getValue();
						   appConfig.setDelay(delay);
			       t3.setText(new Integer(delay).toString()); 
	                     }});
    
        s1.setMajorTickSpacing(120); s1.setPaintLabels(true);
        s2.setMajorTickSpacing(25); s2.setPaintLabels(true);
	s3.setMajorTickSpacing(500);s3.setPaintLabels(true);
	   t1.setEditable(false);t2.setEditable(false);t3.setEditable(false);
	p1.add(s1); p2.add(s2); p3.add(s3);
	p1.add(t1); p2.add(t2); p3.add(t3); p3.add(l4);
	container.add(p4,BorderLayout.CENTER);
	container.add(buttonPanel,BorderLayout.SOUTH);
	getContentPane().add(container);
       // setSize(400,300);
        pack();
	centerDialog();


  }
   public void show()
   {
       super.show();
	   int note = appConfig.getNote();
	   int velocity = appConfig.getVelocity();
	   int delay = appConfig.getDelay();
       t1.setText(noteName[note%12]+note/12); 
       t2.setText(new Integer(velocity).toString()); 
       t3.setText(new Integer(delay).toString()); 
       s1.setValue(note);
       s2.setValue(velocity);
       s3.setValue(delay);
	
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
                    
 
}

