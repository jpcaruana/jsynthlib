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
    String libPath=null;
    String sysexPath=null;
    int    initPortOut=0;
    int    initPortIn=0;
    int    masterController=0;
    int    faderPort=0;
    int    faderPortWas=0;
    int    faderController[]=new int[33];
    int    faderChannel[]=new int[33];
    int    lookAndFeel=0;
    int    midiPlatform=0;
    boolean faderEnable;
    UIManager.LookAndFeelInfo [] installedLF;
    final JTextField t1=new JTextField (libPath,20);
    final JTextField t2=new JTextField (sysexPath,20);
    JComboBox cb1;
    JComboBox cb2;
    JComboBox cb3;
    JComboBox cb4;
    JComboBox cb5;
    JComboBox cbController;
    JComboBox cbChannel;
    JCheckBox enabledBox;
    JComboBox cbPlatform;
    JList lb1;
    int currentFader=0;
    JFrame p;
    public PrefsDialog (JFrame Parent)
    {
        super(Parent,"User Preferences",true);
        p=Parent;
    }
    public void init ()
    {
        installedLF =  UIManager.getInstalledLookAndFeels ();
        try
        {UIManager.setLookAndFeel (installedLF[lookAndFeel].getClassName ());}catch (Exception e)
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
        faderPortWas=faderPort;
    }
    public void show ()
    {
        
        t1.setText (libPath);
        t2.setText (sysexPath);
        try
        {
            cb1.setSelectedIndex (initPortOut);
            cb2.setSelectedIndex (initPortIn);
            cb3.setSelectedIndex (masterController);
            cb4.setSelectedIndex (faderPort);
            cb5.setSelectedIndex (lookAndFeel);
        }catch (Exception e)
        {ErrorMsg.reportError ("Warning","Values for MidiPorts are out of range!",e);}
        cbPlatform.setSelectedIndex (midiPlatform);
        cbController.setSelectedIndex (faderController[lb1.getSelectedIndex ()]);
        cbChannel.setSelectedIndex (faderChannel[lb1.getSelectedIndex ()]);
        
        enabledBox.setSelected (faderEnable);
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
                fc.setCurrentDirectory (new File (libPath));
                fc.setDialogTitle ("Choose Default Directory");
                int returnVal = fc.showOpenDialog (null);
                if (returnVal == JFileChooser.APPROVE_OPTION)
                {
                    File file = fc.getSelectedFile ();
                    libPath=file.getAbsolutePath ();
                    t1.setText (libPath);
                }
            }
        });
        b2.addActionListener (new ActionListener ()
        {
            public void actionPerformed (ActionEvent e)
            {
                JFileChooser fc=new JFileChooser ();
                fc.setFileSelectionMode (JFileChooser.DIRECTORIES_ONLY);
                fc.setCurrentDirectory (new File (sysexPath));
                fc.setDialogTitle ("Choose Default Directory");
                int returnVal = fc.showOpenDialog (null);
                if (returnVal == JFileChooser.APPROVE_OPTION)
                {
                    File file = fc.getSelectedFile ();
                    sysexPath=file.getAbsolutePath ();
                    t2.setText (sysexPath);
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
                    midiPlatform=((JComboBox)e.getSource ()).getSelectedIndex ();
                    //System.out.println("MakeCard2:stateChanged to "+midiPlatform);
                
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
            cb1.setSelectedIndex (initPortOut);
            cb2.setSelectedIndex (initPortIn);
            cb3.setSelectedIndex (masterController);
            cb4.setSelectedIndex (faderPort);
            cb5.setSelectedIndex (lookAndFeel);

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
              faderController[currentFader]=cbController.getSelectedIndex ();
              faderChannel[currentFader]=cbChannel.getSelectedIndex ();
              cbController.setSelectedIndex (faderController[lb1.getSelectedIndex ()]);
              cbChannel.setSelectedIndex (faderChannel[lb1.getSelectedIndex ()]);
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
        initPortIn=cb2.getSelectedIndex ();
        initPortOut=cb1.getSelectedIndex ();
        masterController=cb3.getSelectedIndex ();
        faderPort=cb4.getSelectedIndex ();
        if ((lookAndFeel!=cb5.getSelectedIndex ())|| (midiPlatform!=cbPlatform.getSelectedIndex ()))
            JOptionPane.showMessageDialog (null, "You must exit and restart the program for your changes to take effect","Changing L&F / Platform", JOptionPane.INFORMATION_MESSAGE);
        lookAndFeel=cb5.getSelectedIndex ();
        midiPlatform=cbPlatform.getSelectedIndex ();
        
        if (faderPort!=faderPortWas)
        {
            //FIXME: This is not portable to other MIDI libraries--working around strange JavaMIDI bugs
            try
            {((JavaMidiWrapper)(PatchEdit.MidiIn)).faderMidiPort.setDeviceNumber (MidiPort.MIDIPORT_INPUT,faderPort);
             PatchEdit.MidiIn.setInputDeviceNum (faderPort);
             PatchEdit.MidiIn.setInputDeviceNum (masterController);
             JOptionPane.showMessageDialog (null, "You must exit and restart the program for this change to take effect","Changing Fader Port", JOptionPane.INFORMATION_MESSAGE);
             
            }catch (Exception e)
            {}
            faderPortWas=faderPort;
        }
        faderController[currentFader]=cbController.getSelectedIndex ();
        faderChannel[currentFader]=cbChannel.getSelectedIndex ();
        faderEnable=enabledBox.isSelected ();
        this.setVisible (false);
    }
    
    void PresetPC1600x ()
    {
        faderController[0]=128; faderChannel[0]=16;
        for (int i=1;i<17;i++)
        {faderController[i]=24; faderChannel[i]=i-1;}
        for (int i=17;i<33;i++)
        {faderController[i]=25; faderChannel[i]=i-17;}
        cbController.setSelectedIndex (faderController[lb1.getSelectedIndex ()]);
        cbChannel.setSelectedIndex (faderChannel[lb1.getSelectedIndex ()]);
    }
    
    void PresetKawaiK5000 ()
    {
        faderController[0]=1; faderChannel[0]=0;
        for (int i=1;i<17;i++)
        {faderChannel[i]=0;}
        for (int i=17;i<33;i++)
        {faderController[i]=128; faderChannel[i]=16;}
        faderController[1]=16;faderController[2]=18;faderController[3]=74;faderController[4]=73;
        faderController[5]=17;faderController[6]=19;faderController[7]=77;faderController[8]=78;
        faderController[9]=71;faderController[10]=75;faderController[11]=76;faderController[12]=72;
        faderController[13]=80;faderController[14]=81;faderController[15]=82;faderController[16]=83;
        
        cbController.setSelectedIndex (faderController[lb1.getSelectedIndex ()]);
        cbChannel.setSelectedIndex (faderChannel[lb1.getSelectedIndex ()]);
    }
}
