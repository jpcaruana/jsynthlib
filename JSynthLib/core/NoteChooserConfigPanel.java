package core;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * ConfigPanel for play note.  Gives the user a way to change the note
 * played... when the user changes something in a synth editor.
 *
 * @author Unknown - Hacked up by emenaker 2003.03.17
 * @author Hiroo Hayashi
 */
public class NoteChooserConfigPanel extends ConfigPanel implements ActionListener {
    {
	panelName = "Play Note";
	nameSpace = "playnote";
    }

    /* enable MIDI sequencer for test purpose?  */
    private static final boolean	useSequencer = false;

    private JRadioButton seqButton;
    private JRadioButton toneButton;
    private JPanel sequencePanel;
    private final JTextField t0 = new JTextField(null, 20);
    
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
    private final String SEQ_LABEL = "MIDI Sequencer";
    private final String TONE_LABEL = "Single Tone";

    NoteChooserConfigPanel(PrefsDialog parent) {
	super(parent);

	setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
	c.anchor = GridBagConstraints.WEST;

	// display sequencer parts only if 'useSequencer=true"
	if (useSequencer == true) {
	    /*
	     * Radio button which method should be used
	     */
	    seqButton  = new JRadioButton(SEQ_LABEL);
	    toneButton = new JRadioButton(TONE_LABEL);
	    ButtonGroup group = new ButtonGroup();
	    group.add(seqButton);
	    group.add(toneButton);
	    seqButton.addActionListener(this);
	    toneButton.addActionListener(this);

	    c.gridx = 0; c.gridy = 0;
            add(seqButton, c);
	
	    /*
	     * create own sequence panel for file chooser
	     */
	    t0.setEditable(false);

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

	    sequencePanel = new JPanel();
	    sequencePanel.add(new JLabel("MIDI file to play: "));
	    sequencePanel.add(t0);
	    sequencePanel.add(b0);

	    c.gridy++;
	    add(sequencePanel, c);

	    c.gridy++; c.insets = new Insets(10, 0, 0, 0);
            add(toneButton, c);
            c.insets = new Insets(0, 0, 0, 0);
	}

	/*
	 * create own note chooser panel
	 */
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

	notePanel = new JPanel(new GridBagLayout());
	GridBagConstraints nc = new GridBagConstraints();
	nc.gridy = 0;
	nc.gridx = 0; notePanel.add(new JLabel("MIDI Note : "), nc);
	nc.gridx = 1; notePanel.add(s1, nc); 
	nc.gridx = 2; notePanel.add(t1, nc);
	nc.gridy = 1;
	nc.gridx = 0; notePanel.add(new JLabel("Velocity  :  "), nc);
	nc.gridx = 1; notePanel.add(s2, nc); 
	nc.gridx = 2; notePanel.add(t2, nc);
	nc.gridy = 2;
	nc.gridx = 0; notePanel.add(new JLabel("Duration  :"), nc);
	nc.gridx = 1; notePanel.add(s3, nc); 
	nc.gridx = 2; notePanel.add(t3, nc);
	nc.gridx = 3; notePanel.add(new JLabel("msec"), nc);

	c.gridy++;
	add(notePanel, c);
    }

    public void actionPerformed(ActionEvent e) {
        setContainerEnabled(sequencePanel, e.getActionCommand().equals(SEQ_LABEL));
        setContainerEnabled(notePanel, e.getActionCommand().equals(TONE_LABEL));

        setModified(true);
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

	if (useSequencer == true) {
	    /* Sequencer related parts */
	    boolean isSeq = AppConfig.getSequencerEnable();
	    seqButton.setSelected(isSeq);
	    toneButton.setSelected(!isSeq);
	    t0.setText(AppConfig.getSequencePath());
	    
	    setContainerEnabled(sequencePanel, isSeq);
            setContainerEnabled(notePanel, !isSeq);
	}
    }

    void commitSettings() {
	if (useSequencer == true) {
	    /* Sequencer related parts */
	    AppConfig.setSequencerEnable(seqButton.isSelected());
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
