package core; //TODO org.jsynthlib;

import javax.swing.event.*;
import javax.swing.*;

/**
 * 
 * @author Unknown - Hacked up by emenaker 2003.03.17
 *
 * Gives the user a way to change the note played... when the user changes something in a synth editor, I guess
 */
public class NoteChooserConfigPanel extends ConfigPanel {

	private static final int DEFAULT_NOTE = 60;
	private static final int DEFAULT_VELOCITY = 100;
	private static final int DEFAULT_DELAY = 500;

	private int note;
	private int velocity;
	private int delay;
	private final JTextField t1 =new JTextField("0",5); 
	private final JTextField t2 =new JTextField("0",5);
	private final JTextField t3 =new JTextField("0",5);
	private final JSlider s1 = new JSlider(JSlider.HORIZONTAL,0,120, 0); //note
	private final JSlider s2 = new JSlider(JSlider.HORIZONTAL,0,127, 0); //velocity
	private final JSlider s3 = new JSlider(JSlider.HORIZONTAL,0,2000, 0); //delay
	private final String [] noteName = new String [] {"C","C#","D","D#","E","F","F#","G","G#","A","A#","B"};   

	/**
	 * Constructor
	 * @param appConfig the application config object
	 */
	public NoteChooserConfigPanel(core.AppConfig appConfig) {
		super(appConfig);

		setLayout (new core.ColumnLayout());
		JPanel p1 = new JPanel();
		JLabel l1 = new JLabel("Midi Note : ");
		JPanel p2 = new JPanel();
		JLabel l2 = new JLabel("Velocity  :  ");
		JPanel p3 = new JPanel();
		JLabel l3 = new JLabel("Duration  :");
		JLabel l4= new JLabel("msec");
		p1.add(l1); p2.add(l2);p3.add(l3);
		add(p1);
		add(p2);
		add(p3);

		// Update the text fields whenever the slider changes     
		s1.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				note = s1.getValue();
				refreshTextFields();
			}
		});
        s2.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				velocity = s2.getValue();
				refreshTextFields();
			}
		});
        s3.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				delay=s3.getValue();
				refreshTextFields();
			}
		});
    
		s1.setMajorTickSpacing(120); s1.setPaintLabels(true);
		s2.setMajorTickSpacing(25); s2.setPaintLabels(true);
		s3.setMajorTickSpacing(500);s3.setPaintLabels(true);
		t1.setEditable(false);t2.setEditable(false);t3.setEditable(false);
		p1.add(s1); p2.add(s2); p3.add(s3);
		p1.add(t1); p2.add(t2); p3.add(t3); p3.add(l4);
	}

	public void init() {
		restoreFromConfig();
		refreshTextFields();
		s1.setValue(note);
		s2.setValue(velocity);
		s3.setValue(delay);
	}

	public void commitSettings() {
		appConfig.setNote(note);
		appConfig.setVelocity(velocity);
		appConfig.setDelay(delay);
	}

	/**
	* This should return the name that should go on the tab (if using a tabbed config dialog).
	* Otherwise, it could be used for a frame title, etc. However, this can be overridden by
	* certain constructors.
	*/
	protected String getDefaultPanelName() {
		return("Play Note");
	}

	/**
	* This should return the AppConfig namespace to use if one isn't specified in the constructor
	*/
	protected String getDefaultNamespace() {
		return("playnote");
	}

	private void refreshTextFields() {
		t1.setText(noteName[note%12]+note/12); 
		t2.setText(new Integer(velocity).toString()); 
		t3.setText(new Integer(delay).toString()); 
	}

	private void restoreFromConfig() {
		note = appConfig.getNote();
		velocity = appConfig.getVelocity();
		delay = appConfig.getDelay();
		if(note <= 0 || note > 127) {
			note=DEFAULT_NOTE;
		} 
		if(velocity <= 0 || velocity > 127) {
			velocity = DEFAULT_VELOCITY;
		}
		if(delay <= 0) {
			delay = DEFAULT_DELAY;
		}
	}
}
