package core;
import jmidi.*;
import javax.swing.JList.*;
import javax.swing.JScrollPane;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;
import javax.swing.JDialog;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.metal.*;
import javax.swing.event.*;
import java.beans.*;
import java.util.*;
import java.awt.event.*;
import java.awt.*;
import java.io.*;
public class PrefsDialog extends JDialog
{
	// AppConfig object used to store actual config values
	private AppConfig appConfig = null;

    //private String libPath=null;
    //private String sysexPath=null;
    //int    initPortOut=0;
    //int    initPortIn=0;
    //int    masterController=0;
    //int    faderPort=0;
    int    faderPortWas=0;
    //int    faderController[]=new int[33];
    //int    faderChannel[]=new int[33];
    //int    lookAndFeel=0;
    //int    midiPlatform=0;
    //boolean faderEnable;
    UIManager.LookAndFeelInfo [] installedLF;
    final JTextField t1=new JTextField (null,20);
    final JTextField t2=new JTextField (null,20);
    JComboBox cb1 = new JComboBox();
    JComboBox cb2 = new JComboBox();
    JComboBox cb3 = new JComboBox();
    JComboBox cb4 = new JComboBox();
    JComboBox cb5 = new JComboBox();
    JComboBox cbController;
    JComboBox cbChannel;
    JCheckBox enabledBox;
    JComboBox cbPlatform;
    JList lb1;
    int currentFader=0;
    JFrame p;

	/**
	 * Constructor
	 * @param Parent the parent JFrame
	 * @param appConfig the application config object
	 */
    public PrefsDialog (JFrame Parent, AppConfig appConfig)
    {
        super(Parent,"User Preferences",true);
        p=Parent;
		this.appConfig = appConfig;
    }
    public void init ()
    {
        installedLF =  UIManager.getInstalledLookAndFeels ();
        try
        {UIManager.setLookAndFeel (installedLF[appConfig.getLookAndFeel()].getClassName ());}catch (Exception e)
        {};
        JPanel container= new JPanel ();
        container.setLayout (new BorderLayout ());
        JTabbedPane jtp=new JTabbedPane ();
        jtp.addTab ("General",makeCard0 ());
        jtp.addTab ("Directories",makeCard1 ());
        jtp.addTab ("Midi",makeCard2 ());
        jtp.addTab ("Fader Box",makeCard3 ());
        
        jtp.addChangeListener (new ChangeListener ()
        {
            public void stateChanged (ChangeEvent e)
            {
                //System.out.println ("Tab changed: "+((JTabbedPane)e.getSource ()).getSelectedIndex ());
            }
        });
        container.add (jtp,BorderLayout.NORTH);
        JPanel buttonPanel = new JPanel ();
        buttonPanel.setLayout ( new FlowLayout (FlowLayout.CENTER) );
        
        JButton ok = new JButton ("OK");
        ok.addActionListener (new ActionListener ()
        {
            public void actionPerformed (ActionEvent e)
            {
                OKPressed ();
            }
        });
        buttonPanel.add ( ok );
        
        
        getRootPane ().setDefaultButton (ok);
        
        container.add (buttonPanel, BorderLayout.SOUTH);
        getContentPane ().add (container);
        // setSize(400,300);
        pack ();
        centerDialog ();
        faderPortWas=appConfig.getFaderPort();
    }
    public void show ()
    {
        
        t1.setText (appConfig.getLibPath());
        t2.setText (appConfig.getSysexPath());
        try
        {
            cb1.setSelectedIndex (appConfig.getInitPortOut());
            cb2.setSelectedIndex (appConfig.getInitPortIn());
            cb3.setSelectedIndex (appConfig.getMasterController());
            cb4.setSelectedIndex (appConfig.getFaderPort());
            cb5.setSelectedIndex (appConfig.getLookAndFeel());
        }catch (Exception e)
        {ErrorMsg.reportError ("Warning","Values for MidiPorts are out of range!",e);}
        cbPlatform.setSelectedIndex (appConfig.getMidiPlatform());
        cbController.setSelectedIndex (appConfig.getFaderController(lb1.getSelectedIndex()));
        cbChannel.setSelectedIndex (appConfig.getFaderChannel(lb1.getSelectedIndex()));
        
        enabledBox.setSelected (appConfig.getFaderEnable());
        super.show ();
    }
    
    protected JPanel makeCard1 ()
    {
        JPanel panel=new JPanel ();
        panel.setLayout (new ColumnLayout ());
        JLabel l1=new JLabel ("Patch Library Path: ");
        JPanel p1=new JPanel ();
        JLabel l2=new JLabel ("Sysex File Path:       ");
        JPanel p2=new JPanel ();
        JLabel l0=new JLabel ("Default Directories:");
        t1.setEditable (false);
        t2.setEditable (false);
        JButton b1=new JButton ("Browse");
        JButton b2=new JButton ("Browse");
        b1.addActionListener (new ActionListener ()
        {
            public void actionPerformed (ActionEvent e)
            {
                JFileChooser fc=new JFileChooser ();
                fc.setFileSelectionMode (JFileChooser.DIRECTORIES_ONLY);
                fc.setCurrentDirectory (new File (appConfig.getLibPath()));
                fc.setDialogTitle ("Choose Default Directory");
                int returnVal = fc.showOpenDialog (null);
                if (returnVal == JFileChooser.APPROVE_OPTION)
                {
                    File file = fc.getSelectedFile ();
                    appConfig.setLibPath(file.getAbsolutePath());
                    t1.setText (appConfig.getLibPath());
                }
            }
        });
        b2.addActionListener (new ActionListener ()
        {
            public void actionPerformed (ActionEvent e)
            {
                JFileChooser fc=new JFileChooser ();
                fc.setFileSelectionMode (JFileChooser.DIRECTORIES_ONLY);
                fc.setCurrentDirectory (new File (appConfig.getSysexPath()));
                fc.setDialogTitle ("Choose Default Directory");
                int returnVal = fc.showOpenDialog (null);
                if (returnVal == JFileChooser.APPROVE_OPTION)
                {
                    File file = fc.getSelectedFile ();
                    appConfig.setSysexPath(file.getAbsolutePath());
                    t2.setText (appConfig.getSysexPath());
                }
            }
        });
        p1.add (l1);
        p2.add (l2);
        p1.add (t1);
        p2.add (t2);
        p1.add (b1);
        p2.add (b2);
        
        panel.add (l0);
        panel.add (p1);
        panel.add (p2);
        return panel;
    }
    
    protected JPanel makeCard2 ()
    {
        JPanel panel=new JPanel ();
        panel.setLayout (new ColumnLayout ());
        JLabel l2 = new JLabel ("MIDI Access Method:");
        panel.add (l2);
        cbPlatform=new JComboBox ();
        cbPlatform.addItem ("MIDI Access Disabled.");
        cbPlatform.addItem ("MS Windows (JavaMIDI)");
        cbPlatform.addItem ("MS Windows (WireProvider)");
        cbPlatform.addItem ("GNU/Linux  (/dev/*)");
        cbPlatform.addItem ("MacOS X");
        cbPlatform.addItemListener (new ItemListener ()
        {
            public void itemStateChanged (ItemEvent e)
            {
                if (e.getStateChange ()==e.SELECTED)
                {
                    ((PatchEdit)p).unloadMidiDriver ();
                    appConfig.setMidiPlatform(((JComboBox)e.getSource ()).getSelectedIndex ());
                    //System.out.println("MakeCard2:stateChanged to "+appConfig.getMidiPlatform());
                
                    ((PatchEdit)p).loadMidiDriver ();
                    setPortCombos ((JPanel)((JComboBox)e.getSource ()).getParent ());
                }
            }
        }
        );
        panel.add (cbPlatform);
        
        JLabel l0=new JLabel ("Run Startup Initialization on Midi Ports:");
        panel.add (l0);
        cb1 = new JComboBox ();
        cb2 = new JComboBox ();
        cb3 = new JComboBox ();
        panel.add (cb1);
        panel.add (cb2);
        
        JLabel l1=new JLabel ("Receive from Master Controller on Midi Port:");
        panel.add (l1);
        panel.add (cb3);
        
        setPortCombos (panel);

        return panel;
    }
    
    public void setPortCombos (JPanel panel)
    {
        try
        {
            cb1.removeAllItems ();
            for (int j=0; j< PatchEdit.MidiOut.getNumOutputDevices ();j++)
                cb1.addItem (j+": "+PatchEdit.MidiOut.getOutputDeviceName (j));
            cb2.removeAllItems ();
            for (int j=0; j< PatchEdit.MidiIn.getNumInputDevices ();j++)
                cb2.addItem (j+": "+PatchEdit.MidiIn.getInputDeviceName (j));
            cb3.removeAllItems ();
            for (int j=0; j< PatchEdit.MidiIn.getNumInputDevices ();j++)
                cb3.addItem (j+": "+PatchEdit.MidiOut.getInputDeviceName (j));
            cb4.removeAllItems ();
            for (int j=0; j< PatchEdit.MidiIn.getNumInputDevices ();j++)
				cb4.addItem (j+": "+PatchEdit.MidiIn.getInputDeviceName (j));
			cb5.removeAllItems ();
			for (int j=0; j< installedLF.length;j++)
				cb5.addItem (installedLF[j].getName ());
            cb1.setSelectedIndex (appConfig.getInitPortOut());
            cb2.setSelectedIndex (appConfig.getInitPortIn());
            cb3.setSelectedIndex (appConfig.getMasterController());
            cb4.setSelectedIndex (appConfig.getFaderPort());
            cb5.setSelectedIndex (appConfig.getLookAndFeel());

        } catch (Exception e)
        {ErrorMsg.reportStatus (e);}
        
    }
    
    protected JPanel makeCard3 ()
    { JPanel panel=new JPanel ();
      panel.setLayout (new GridBagLayout ());
      GridBagConstraints gbc=new GridBagConstraints ();
      JLabel l0=new JLabel ("Receive Faders from Midi Port:  ");
      gbc.gridx=0;gbc.gridy=1;gbc.gridwidth=3;gbc.gridheight=1;
      panel.add (l0,gbc);
      cb4 = new JComboBox ();
      try
      {
          for (int j=0; j< PatchEdit.MidiIn.getNumInputDevices ();j++)
              cb4.addItem (j+": "+PatchEdit.MidiIn.getInputDeviceName (j));
      }catch (Exception e)
      {}
      
      gbc.gridx=3;gbc.gridy=1;gbc.gridwidth=3;gbc.gridheight=1;
      panel.add (cb4,gbc);
      enabledBox=new JCheckBox ("Enable Faders");
      gbc.gridx=6;gbc.gridy=1;gbc.gridwidth=1;gbc.gridheight=1;
      panel.add (enabledBox,gbc);
      
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
      lb1.addListSelectionListener (new ListSelectionListener ()
      {
          public void valueChanged (ListSelectionEvent e)
          {
              appConfig.setFaderController(currentFader, cbController.getSelectedIndex());
              appConfig.setFaderChannel(currentFader, cbChannel.getSelectedIndex());
              cbController.setSelectedIndex (appConfig.getFaderController(lb1.getSelectedIndex()));
              cbChannel.setSelectedIndex (appConfig.getFaderChannel(lb1.getSelectedIndex()));
              currentFader=lb1.getSelectedIndex ();
          }
      });
      lb1.setSelectedIndex (0);
      lb1.setSelectionMode (0);
      gbc.gridx=0;gbc.gridy=5;gbc.gridwidth=2;gbc.gridheight=3;
      JScrollPane scroll=new JScrollPane (lb1);
      panel.add (scroll,gbc);
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
      panel.add (dataPanel,gbc);
      JPanel buttonPanel = new JPanel ();
      JButton b1 = new JButton ("Peavey PC1600x Preset");
      JButton b2 = new JButton ("Kawai K5000 Knobs Preset");
      b1.addActionListener (new ActionListener ()
      {
          public void actionPerformed (ActionEvent e)
          {
              PresetPC1600x ();
          }
      });
      b2.addActionListener (new ActionListener ()
      {
          public void actionPerformed (ActionEvent e)
          {
              PresetKawaiK5000 ();
          }
      });
      
      buttonPanel.add (b1); buttonPanel.add (b2);
      gbc.gridx=0;gbc.gridy=8;gbc.gridwidth=7;gbc.gridheight=1;  gbc.fill=GridBagConstraints.BOTH;
      panel.add (buttonPanel,gbc);
      return panel;
    }
    
    protected JPanel makeCard0 ()
    {
        JPanel panel=new JPanel ();
        panel.setLayout (new ColumnLayout ());

        JLabel l0=new JLabel ("Application Look and Feel:");
        panel.add (l0);
        cb5 = new JComboBox ();
        for (int j=0; j< installedLF.length;j++)
            cb5.addItem (installedLF[j].getName ());
        panel.add (cb5);
        return panel;
    }
    
    
    protected void centerDialog ()
    {
        Dimension screenSize = this.getToolkit ().getScreenSize ();
        Dimension size = this.getSize ();
        screenSize.height = screenSize.height/2;
        screenSize.width = screenSize.width/2;
        size.height = size.height/2;
        size.width = size.width/2;
        int y = screenSize.height - size.height;
        int x = screenSize.width - size.width;
        this.setLocation (x,y);
    }
    
    void OKPressed ()
    {
        appConfig.setInitPortIn(cb2.getSelectedIndex());
        appConfig.setInitPortOut(cb1.getSelectedIndex());
        appConfig.setMasterController(cb3.getSelectedIndex());
        appConfig.setFaderPort(cb4.getSelectedIndex());
        if ((appConfig.getLookAndFeel()!=cb5.getSelectedIndex ())|| (appConfig.getMidiPlatform()!=cbPlatform.getSelectedIndex ()))
            JOptionPane.showMessageDialog (null, "You must exit and restart the program for your changes to take effect","Changing L&F / Platform", JOptionPane.INFORMATION_MESSAGE);
        appConfig.setLookAndFeel(cb5.getSelectedIndex());
        appConfig.setMidiPlatform(cbPlatform.getSelectedIndex());
        
        if (appConfig.getFaderPort()!=faderPortWas)
        {
            //FIXME: This is not portable to other MIDI libraries--working around strange JavaMIDI bugs
            try
            {((JavaMidiWrapper)(PatchEdit.MidiIn)).faderMidiPort.setDeviceNumber (MidiPort.MIDIPORT_INPUT,appConfig.getFaderPort());
             PatchEdit.MidiIn.setInputDeviceNum (appConfig.getFaderPort());
             PatchEdit.MidiIn.setInputDeviceNum (appConfig.getMasterController());
             JOptionPane.showMessageDialog (null, "You must exit and restart the program for this change to take effect","Changing Fader Port", JOptionPane.INFORMATION_MESSAGE);
             
            }catch (Exception e)
            {}
            faderPortWas=appConfig.getFaderPort();
        }
        appConfig.setFaderController(currentFader, cbController.getSelectedIndex());
        appConfig.setFaderChannel(currentFader, cbChannel.getSelectedIndex());
        appConfig.setFaderEnable(enabledBox.isSelected());
        this.setVisible (false);
    }
    
    void PresetPC1600x ()
    {
        appConfig.setFaderController(0,128); appConfig.setFaderChannel(0,16);
        for (int i=1;i<17;i++)
        {appConfig.setFaderController(i,24); appConfig.setFaderChannel(i, i-1);}
        for (int i=17;i<33;i++)
        {appConfig.setFaderController(i,25); appConfig.setFaderChannel(i, i-17);}
        cbController.setSelectedIndex (appConfig.getFaderController(lb1.getSelectedIndex()));
        cbChannel.setSelectedIndex (appConfig.getFaderChannel(lb1.getSelectedIndex()));
    }
    
    void PresetKawaiK5000 ()
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
