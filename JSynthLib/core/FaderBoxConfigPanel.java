
package core; //TODO org.jsynthlib;

// TODO: Why does FaderBox have to know about a specific midi implementation?
import jmidi.*;
//TODO import org.jsynthlib.midi.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

/** Abstract class that is used by PrefsDialog to load an arbitrary
 *  number of configuration tabs.
 * @author Joe Emenaker
 * @version $Id$
 */

public class FaderBoxConfigPanel extends ConfigPanel implements MidiDriverChangeListener {
	JComboBox cb4 = new JComboBox();
	JCheckBox enabledBox;
	JList lb1;
	JComboBox cbController;
	JComboBox cbChannel;
	int faderPortWas=0;
	int currentFader=0;


	public FaderBoxConfigPanel(core.AppConfig appConfig) {
		super(appConfig);
		setLayout (new GridBagLayout ());
		GridBagConstraints gbc=new GridBagConstraints ();
		JLabel l0=new JLabel ("Receive Faders from Midi Port:  ");
		gbc.gridx=0;gbc.gridy=1;gbc.gridwidth=3;gbc.gridheight=1;
		add (l0,gbc);
		cb4 = new JComboBox ();
		resetComboBoxes();
     
		gbc.gridx=3;gbc.gridy=1;gbc.gridwidth=3;gbc.gridheight=1;
		add (cb4,gbc);
		enabledBox=new JCheckBox ("Enable Faders");
		gbc.gridx=6;gbc.gridy=1;gbc.gridwidth=1;gbc.gridheight=1;
		add (enabledBox,gbc);
     
		String n[]=new String[37];
	 	n[0]="Active Slider";
		for (int i=0;i<16;i++) n[i+1]=("Slider #"+(i+1));
		for (int i=0;i<12;i++) n[i+17]=("Button #"+(i+1));
		n[29]="Button #13";
		n[30]="Button #14";
		n[31]="Slider Bank Prev";
		n[32]="Slider Bank Next";
		String cc1[] = new String[129];
		String cc2[] = new String[17];
		for (int i=0;i<128;i++) cc1[i]=(""+(i));
		cc1[128]="Off";
		for (int i=0;i<16;i++) cc2[i]=(""+(i+1));
		cc2[16]="Off";
		cbController=new JComboBox (cc1);
		cbChannel=new JComboBox (cc2);
		lb1=new JList (n);
		lb1.addListSelectionListener (new ListSelectionListener () {
			public void valueChanged (ListSelectionEvent e) {
				setFaderController(currentFader, cbController.getSelectedIndex());
				setFaderChannel(currentFader, cbChannel.getSelectedIndex());
				cbController.setSelectedIndex (getFaderController(lb1.getSelectedIndex()));
				cbChannel.setSelectedIndex (getFaderChannel(lb1.getSelectedIndex()));
				currentFader=lb1.getSelectedIndex ();
			}
		});
		lb1.setSelectedIndex (0);
		lb1.setSelectionMode (0);
		gbc.gridx=0;gbc.gridy=5;gbc.gridwidth=2;gbc.gridheight=3;
		JScrollPane scroll=new JScrollPane (lb1);
		add (scroll,gbc);
		JPanel dataPanel=new JPanel ();
		dataPanel.setLayout (new GridBagLayout ());
		gbc.anchor=GridBagConstraints.EAST;
		gbc.gridx=0;gbc.gridy=1;gbc.gridwidth=1;gbc.gridheight=1; dataPanel.add (new JLabel ("Midi Controller #  "),gbc);
		gbc.gridx=0;gbc.gridy=2;gbc.gridwidth=1;gbc.gridheight=1; dataPanel.add (new JLabel ("Midi Channel    #  "),gbc);
		gbc.gridx=1;gbc.gridy=1;gbc.gridwidth=1;gbc.gridheight=1;
		dataPanel.add (cbController,gbc);
		gbc.gridx=1;gbc.gridy=2;gbc.gridwidth=1;gbc.gridheight=1;
		dataPanel.add (cbChannel,gbc);
		gbc.gridx=2;gbc.gridy=5;gbc.gridwidth=5;gbc.gridheight=3;  gbc.fill=GridBagConstraints.BOTH;
		dataPanel.setBorder (new EtchedBorder (EtchedBorder.RAISED));
		add (dataPanel,gbc);
		JPanel buttonPanel = new JPanel ();
		JButton b1 = new JButton ("Peavey PC1600x Preset");
		JButton b2 = new JButton ("Kawai K5000 Knobs Preset");
		b1.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent e) {
				PresetPC1600x ();
			}
		});
		b2.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent e) {
				PresetKawaiK5000 ();
			}
		});
     
		buttonPanel.add (b1); buttonPanel.add (b2);
		gbc.gridx=0;gbc.gridy=8;gbc.gridwidth=7;gbc.gridheight=1;  gbc.fill=GridBagConstraints.BOTH;
		add (buttonPanel,gbc);
	}
	
	/**
	* This is called every time the config dialog box is opened.
	* It tells the implementing class to set all GUI elements to
	* properly reflect the internal data. This is because we are
	* not assured that the GUI's are not set to a state that
	* does NOT match the internal data (in cases where the user
	* changes some stuff and then hits "Cancel". - emenaker 2003.03.13
	*/
	public void init() {
		try {
			cb4.setSelectedIndex (appConfig.getFaderPort());
		} catch (Exception e) {
			core.ErrorMsg.reportError ("Warning",
						   "Values for MidiPorts are out of range!\n"
						   + "Please fix this in the "+getPanelName()+" configuration panel.",e);
		}
		cbController.setSelectedIndex (appConfig.getFaderController(lb1.getSelectedIndex()));
		cbChannel.setSelectedIndex (appConfig.getFaderChannel(lb1.getSelectedIndex()));
		enabledBox.setSelected (appConfig.getFaderEnable());
		faderPortWas=appConfig.getFaderPort();
	}
	
	/**
	* This is the opposite of init(). The implementing class should
	* copy all GUI settings to internal data elements (and also
	* save those settings in to the preference-saving system, if any
	*/
	public void commitSettings() {
		appConfig.setFaderPort(cb4.getSelectedIndex());
        
		if (appConfig.getFaderPort()!=faderPortWas)
		{
			//FIXME: This is not portable to other MIDI libraries--working around strange JavaMIDI bugs
			try {
				((JavaMidiWrapper)(core.PatchEdit.MidiIn)).faderMidiPort.setDeviceNumber (MidiPort.MIDIPORT_INPUT,appConfig.getFaderPort());
				 core.PatchEdit.MidiIn.setInputDeviceNum (appConfig.getFaderPort());
				 // no relation to Master Controller (Hiroo)
				 //core.PatchEdit.MidiIn.setInputDeviceNum (appConfig.getMasterController());
				 JOptionPane.showMessageDialog (null, "You must exit and restart the program for this change to take effect","Changing Fader Port", JOptionPane.INFORMATION_MESSAGE);
			} catch (Exception e) {
			}
			faderPortWas=appConfig.getFaderPort();
		}
		appConfig.setFaderController(currentFader, cbController.getSelectedIndex());
		appConfig.setFaderChannel(currentFader, cbChannel.getSelectedIndex());
		appConfig.setFaderEnable(enabledBox.isSelected());
	}

	public void midiDriverChanged(MidiWrapper driver) {
		resetComboBoxes();
	}

	/**
	 * This method re-populates the combobox(es) that contain things that depend upon which
	 * midi driver we're using. This is called by the constructor and also by the
	 * midiDriverChanged(MidiWrapper) callback method - emenaker 2003.03.18
	 *
	 */	
	private void resetComboBoxes() {
		cb4.removeAllItems();
		try {
			for (int j=0; j< core.PatchEdit.MidiIn.getNumInputDevices ();j++)
				cb4.addItem (j+": "+core.PatchEdit.MidiIn.getInputDeviceName (j));
		} catch (Exception e) {}
	}
	
	/**
	* This should return the name that should go on the tab (if using a tabbed config dialog).
	* Otherwise, it could be used for a frame title, etc. However, this can be overridden by
	* certain constructors.
	*/
	protected String getDefaultPanelName() {
		return("Fader Box");
	}

	/**
	* This should return the AppConfig namespace to use if one isn't specified in the constructor
	*/
	protected String getDefaultNamespace() {
		return("faderbox");
	}

	/*
	 * Private methods
	 */

	private int getFaderController(int fader) {
		return(appConfig.getFaderController(fader));
	}

	private void setFaderController(int currentFader, int controller) {
		appConfig.setFaderController(currentFader, controller);
	}

	private int getFaderChannel(int fader) {
		return(appConfig.getFaderChannel(fader));
	}

	private void setFaderChannel(int currentFader, int channel) {
		appConfig.setFaderChannel(currentFader, channel);
	}

	private void PresetPC1600x ()
	{
		appConfig.setFaderController(0,128); appConfig.setFaderChannel(0,16);
		for (int i=1;i<17;i++)
		{appConfig.setFaderController(i,24); appConfig.setFaderChannel(i, i-1);}
		for (int i=17;i<33;i++)
		{appConfig.setFaderController(i,25); appConfig.setFaderChannel(i, i-17);}
		cbController.setSelectedIndex (appConfig.getFaderController(lb1.getSelectedIndex()));
		cbChannel.setSelectedIndex (appConfig.getFaderChannel(lb1.getSelectedIndex()));
	}
    
	private void PresetKawaiK5000 ()
	{
		appConfig.setFaderController(0,1); appConfig.setFaderChannel(0,0);
		for (int i=1;i<17;i++)
		{appConfig.setFaderChannel(i,0);}
		for (int i=17;i<33;i++)
		{appConfig.setFaderController(i,128); appConfig.setFaderChannel(i,16);}
		appConfig.setFaderController(1,16);appConfig.setFaderController(2,18);
		appConfig.setFaderController(3,74);appConfig.setFaderController(4,73);
		appConfig.setFaderController(5,17);appConfig.setFaderController(6,19);
		appConfig.setFaderController(7,77);appConfig.setFaderController(8,78);
		appConfig.setFaderController(9,71);appConfig.setFaderController(10,75);
		appConfig.setFaderController(11,76);appConfig.setFaderController(12,72);
		appConfig.setFaderController(13,80);appConfig.setFaderController(14,81);
		appConfig.setFaderController(15,82);appConfig.setFaderController(16,83);
        
		cbController.setSelectedIndex (appConfig.getFaderController(lb1.getSelectedIndex()));
		cbChannel.setSelectedIndex (appConfig.getFaderChannel(lb1.getSelectedIndex()));
	}
}
