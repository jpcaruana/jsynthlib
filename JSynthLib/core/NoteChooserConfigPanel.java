package core;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * ConfigPanel for play note.  Gives the user a way to change the note
 * played... when the user changes something in a synth editor.
 *
 * @author Unknown - Hacked up by emenaker 2003.03.17
 */
public class NoteChooserConfigPanel extends ConfigPanel {
    {
	panelName = "Play Note";
	nameSpace = "playnote";
    }

    /* enable MIDI sequencer for test purpose?  */
    private static final boolean 	useSequencer = false;

    private JCheckBox			enabledSequencer;
    private JPanel 			sequencePanel;
    private final 			JTextField t0 = new JTextField(null, 20);
    
    // not used yet.
    //private boolean sequencerEnable;
    //private String  sequencePath;

    /* note chooser parts */
    private static final int DEFAULT_NOTE = 60;
    private static final int DEFAULT_VELOCITY = 100;
    private static final int DEFAULT_DELAY = 500;
    private int note;
    private int velocity;
    private int delay;

    private JPanel notePanel;
    private final JTextField t1 = new JTextField("0", 5);
    private final JTextField t2 = new JTextField("0", 5);
    private final JTextField t3 = new JTextField("0", 5);
    // MIDI note
    private final JSlider s1 = new JSlider(JSlider.HORIZONTAL, 0, 120, 0);
    // Velocity
    private final JSlider s2 = new JSlider(JSlider.HORIZONTAL, 0, 127, 0);
    // Duration
    private final JSlider s3 = new JSlider(JSlider.HORIZONTAL, 0, 2000, 0);

    private final String[] noteName = new String[] {
	"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"
    };

    NoteChooserConfigPanel(PrefsDialog parent) {
	super(parent);

	setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
	gbc.fill = GridBagConstraints.BOTH; gbc.ipadx = 1; gbc.anchor = GridBagConstraints.WEST;

	// display sequencer parts only if 'useSequencer=true"
	if (useSequencer==true) {
	    /*
	     * Checkbox which method should be used
	     */
	    enabledSequencer = new JCheckBox("Playing MIDI files?");
	    enabledSequencer.setToolTipText("if enabled a MIDI file is played for hearing a patch. Otherwise a single tone is played.");
	    gbc.gridx = 1; gbc.gridy = 0; gbc.gridheight = 1; gbc.gridwidth = 2;
            add(enabledSequencer, gbc);
	    enabledSequencer.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    setContainerEnabled(sequencePanel, enabledSequencer.isSelected());
		    setContainerEnabled(notePanel, !enabledSequencer.isSelected());
		    setModified(true);
		}
	    });
	
	    /*
	     * create own sequence panel for file chooser
	     */
	    sequencePanel = new JPanel(new ColumnLayout());
	    t0.setEditable(false);

	    JLabel l0 = new JLabel("MIDI file to play: ");
            JPanel p0 = new JPanel();
	    JButton b0 = new JButton("Browse");
	    b0.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
		    CompatibleFileDialog fc = new CompatibleFileDialog();
		    fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		    if (t0.getText() != null)
		        fc.setSelectedFile(new File(t0.getText()));
		    fc.showDialog(PatchEdit.getInstance(),
		        "Choose MIDI file");
		    if (fc.getSelectedFile() != null) {
		        t0.setText(fc.getSelectedFile().getPath());
		        setModified(true);
		    }
	        }
	    });

	    p0.add(l0);
            p0.add(t0);
	    p0.add(b0);
	    sequencePanel.add(p0);

	    gbc.gridx = 1; gbc.gridy = 2; gbc.gridheight = 1; gbc.gridwidth = 2;
	    add(sequencePanel, gbc);
	}

	// add a little distance between both panels
	gbc.gridx = 1; gbc.gridy = 3; gbc.gridheight = 1; gbc.gridwidth = 2;
	add(new JLabel(" "),gbc);
	
	/*
	 * create own note chooser panel
	 */
	notePanel = new JPanel(new ColumnLayout());

	JPanel p1 = new JPanel();
	JLabel l1 = new JLabel("MIDI Note : ");
	JPanel p2 = new JPanel();
	JLabel l2 = new JLabel("Velocity  :  ");
	JPanel p3 = new JPanel();
	JLabel l3 = new JLabel("Duration  :");
	JLabel l4 = new JLabel("msec");

	// Update the text fields whenever the slider changes
	s1.addChangeListener(new ChangeListener() {
		public void stateChanged(ChangeEvent e) {
		    note = s1.getValue();
		    refreshTextFields();
		    setModified(true);
		}
	    });
        s2.addChangeListener(new ChangeListener() {
		public void stateChanged(ChangeEvent e) {
		    velocity = s2.getValue();
		    refreshTextFields();
		    setModified(true);
		}
	    });
        s3.addChangeListener(new ChangeListener() {
		public void stateChanged(ChangeEvent e) {
		    delay = s3.getValue();
		    refreshTextFields();
		    setModified(true);
		}
	    });

	s1.setMajorTickSpacing(120); s1.setPaintLabels(true);
	s2.setMajorTickSpacing(25);  s2.setPaintLabels(true);
	s3.setMajorTickSpacing(500); s3.setPaintLabels(true);
	t1.setEditable(false); t2.setEditable(false); t3.setEditable(false);

	p1.add(l1); p1.add(s1); p1.add(t1);
	p2.add(l2); p2.add(s2); p2.add(t2);
	p3.add(l3); p3.add(s3); p3.add(t3); p3.add(l4);
	notePanel.add(p1);
	notePanel.add(p2);
	notePanel.add(p3);

	gbc.gridx = 1; gbc.gridy = 4; gbc.gridheight = 6; gbc.gridwidth = 2;
	add(notePanel, gbc);
    }

    void init() {
	note     = AppConfig.getNote();
	velocity = AppConfig.getVelocity();
	delay    = AppConfig.getDelay();
	if (note <= 0 || note > 127) {
	    note = DEFAULT_NOTE;
	}
	if (velocity <= 0 || velocity > 127) {
	    velocity = DEFAULT_VELOCITY;
	}
	if (delay <= 0) {
	    delay = DEFAULT_DELAY;
	}

	s1.setValue(note);
	s2.setValue(velocity);
	s3.setValue(delay);
	refreshTextFields();

	if (useSequencer==true) {
	    /* Sequencer related parts */
            enabledSequencer.setSelected(AppConfig.getSequencerEnable());
	    t0.setText(AppConfig.getSequencePath());
	    
	    setContainerEnabled(sequencePanel, enabledSequencer.isSelected());
            setContainerEnabled(notePanel, !enabledSequencer.isSelected());
	}
    }

    void commitSettings() {
	if (useSequencer==true) {
	    /* Sequencer related parts */
	    AppConfig.setSequencerEnable(enabledSequencer.isSelected());
	    AppConfig.setSequencePath(t0.getText());
	}

	AppConfig.setNote(note);
	AppConfig.setVelocity(velocity);
	AppConfig.setDelay(delay);
	setModified(false);
    }

    private void refreshTextFields() {
	t1.setText(noteName[note % 12] + note / 12);
	t2.setText(String.valueOf(velocity));
	t3.setText(String.valueOf(delay));
    }
}
