/* This file is the main Application object. It's called PatchEdit, which is probably ambiguous, but probably to late to change
now.*/

/* @version $Id$ */

package core;
import java.io.File;
import javax.swing.*;
import java.util.*;
import javax.swing.filechooser.*;
import java.awt.event.*;
import java.awt.*;
import java.io.*;
import javax.sound.midi.*;
public class PatchEdit extends JFrame
{
    public static JDesktopPane desktop;
    //    public static ArrayList DriverList = new ArrayList ();
    //public static ArrayList deviceList = new ArrayList ();
    public static Patch Clipboard;
    public static MidiWrapper MidiOut;
    public static MidiWrapper MidiIn;
    public static JPopupMenu menuPatchPopup;
    static JToolBar   toolBar;
    
    public static int currentPort;
    public static PrefsDialog prefsDialog;
	public static AppConfig appConfig;
    public static NoteChooserDialog noteChooserDialog;
    public static WaitDialog waitDialog;
    public static javax.swing.Timer echoTimer;
    public static int newFaderValue[] = new int[33];
    public static MidiMonitor midiMonitor;    
    public static ExtractAction extractAction;
    public static SendAction sendAction;
    public static SendToAction sendToAction;
    public static StoreAction storeAction;
    public static ReassignAction reassignAction;
    public static EditAction editAction;
    public static PlayAction playAction;
    public static GetAction receiveAction;
    public static SaveAction saveAction;
    public static JMenuItem menuSaveAs;
    public static SortAction sortAction;
    public static SearchAction searchAction;
    public static DeleteDuplicatesAction dupAction;
    public static CopyAction copyAction;
    public static CutAction cutAction;
    public static PasteAction pasteAction;
    public static DeleteAction deleteAction;
    public static ImportAction importAction;
    public static ExportAction exportAction;
    public static ImportAllAction importAllAction;
    public static NewPatchAction newPatchAction;
    public static CrossBreedAction crossBreedAction;    
    public static DocsAction docsAction;
    public static MonitorAction monitorAction;

    public static NewPerformanceAction newPerformanceAction;
    public static TransferPerformanceAction transferPerformanceAction;
    
    SearchDialog searchDialog;
    DocumentationWindow documentationWindow; 
   public static PatchEdit instance;                        // phil@muqus.com

    // Initialize Application:                                  *
    public PatchEdit ()
    {
        super("JSynthLib");
        instance = this;              // phil@muqus.com (so can pop-up windows with PatchEdit as the         
        boolean loadPrefsSuccessfull,loadDriverSuccessfull;
		this.appConfig = new AppConfig();
        prefsDialog=new PrefsDialog (this, this.appConfig);
        noteChooserDialog = new NoteChooserDialog (PatchEdit.this, this.appConfig);
        
        loadPrefsSuccessfull=loadPrefs ();
        loadDriverSuccessfull=loadMidiDriver ();
        MidiIn=MidiOut;
        
        prefsDialog.init ();  //loads in the config file and sets parameters
        // Now lets set up how the pretty application should look
        int inset = 100;
        Dimension screenSize = Toolkit.getDefaultToolkit ().getScreenSize ();
        setBounds (inset, inset,
        screenSize.width - inset*2,
        screenSize.height-inset*2);
        
        
        
        //Quit this app when the big window closes.
        addWindowListener (new WindowAdapter ()
        {
            public void windowClosing (WindowEvent e)
            {
                savePrefs ();
                unloadMidiDriver();
		System.exit (0);
            }
        });
        
        //Set up the GUI.
        desktop = new JDesktopPane ();
        setJMenuBar (createMenus ());
        desktop.setOpaque (false);
        Container c=getContentPane ();
        c.add (desktop,BorderLayout.CENTER);
        
        desktop.putClientProperty ("JDesktopPane.dragMode", "outline");
        setVisible (true);
        if (!loadPrefsSuccessfull)
            ErrorMsg.reportError ("Error", "Unable to load user preferences. Defaults loaded\n If you've just installed or just upgraded this software, this is normal.");
        if (!loadDriverSuccessfull)
            ErrorMsg.reportError ("Error","Unable to Initialize MIDI IN/OUT! \nMidi Transfer will be unavailable this session.\nChange the Initialization Port Settings under Preferences and restart.");
        
        //Set up a silly little dialog we can pop up for the user to gawk at while we do time consuming work later on.
        waitDialog=new WaitDialog (this);
        
        
        // Start pumping MIDI information from Input --> Output so the user can play a MIDI Keyboard and make pretty music
        beginEcho ();
    }
    
    
    // This sets up the Menubar as well as the main right-click Popup menu and the toolbar
    protected JMenuBar createMenus ()
    {
        JMenuBar menuBar = new JMenuBar ();
        JMenu menuLib = new JMenu ("Library");
        menuLib.setMnemonic (KeyEvent.VK_L);
        
        NewAction newAction=new NewAction ();
        menuLib.add (newAction);
        menuLib.getItem (menuLib.getItemCount ()-1).setAccelerator (
        KeyStroke.getKeyStroke (KeyEvent.VK_N, KeyEvent.CTRL_MASK));
        
        OpenAction openAction=new OpenAction ();
        menuLib.add (openAction);
        menuLib.getItem (menuLib.getItemCount ()-1).setAccelerator (

        KeyStroke.getKeyStroke (KeyEvent.VK_O, KeyEvent.CTRL_MASK));
        
        saveAction=new SaveAction ();
        menuLib.add (saveAction);
        menuLib.getItem (menuLib.getItemCount ()-1).setAccelerator (
        KeyStroke.getKeyStroke (KeyEvent.VK_S, KeyEvent.CTRL_MASK));
        
        menuSaveAs = new JMenuItem ("Save As");
        menuSaveAs.setMnemonic (KeyEvent.VK_A);
        menuSaveAs.addActionListener (new ActionListener ()
        {
            public void actionPerformed (ActionEvent e)
            {
                saveFrameAs ();
            }
        });
        menuSaveAs.setEnabled (false);
        menuLib.add (menuSaveAs);
        
        menuLib.add (new JSeparator ());
        newPerformanceAction=new NewPerformanceAction();
        menuLib.add(newPerformanceAction);
        
        transferPerformanceAction=new TransferPerformanceAction();
        menuLib.add(transferPerformanceAction);
        menuLib.add (new JSeparator ());
        
        sortAction=new SortAction ();
        menuLib.add (sortAction);
        
        searchAction=new SearchAction ();
        menuLib.add (searchAction);
        
        dupAction=new DeleteDuplicatesAction ();
        menuLib.add (dupAction);
        menuLib.add (new JSeparator ());
        ExitAction exitAction=new ExitAction ();
        menuLib.add (exitAction);
        menuBar.add (menuLib);
        
        JMenu menuPatch = new JMenu ("Patch");
        menuPatch.setMnemonic (KeyEvent.VK_P);
        copyAction=new CopyAction ();
        menuPatch.add (copyAction);
        menuPatch.getItem (menuPatch.getItemCount ()-1).setAccelerator (
        KeyStroke.getKeyStroke (KeyEvent.VK_C, KeyEvent.CTRL_MASK));
        cutAction=new CutAction ();
        menuPatch.add (cutAction);
        menuPatch.getItem (menuPatch.getItemCount ()-1).setAccelerator (
        KeyStroke.getKeyStroke (KeyEvent.VK_X, KeyEvent.CTRL_MASK));
        pasteAction=new PasteAction ();
        menuPatch.add (pasteAction);
        menuPatch.getItem (menuPatch.getItemCount ()-1).setAccelerator (
        KeyStroke.getKeyStroke (KeyEvent.VK_V, KeyEvent.CTRL_MASK));
        deleteAction=new DeleteAction ();
        menuPatch.add (deleteAction);
        menuPatch.add (new JSeparator ());
        importAction=new ImportAction ();
        menuPatch.add (importAction);
        exportAction=new ExportAction ();
        menuPatch.add (exportAction);
        importAllAction=new ImportAllAction ();
        menuPatch.add (importAllAction);
        menuPatch.add (new JSeparator ());
        sendAction=new SendAction ();
        menuPatch.add (sendAction);
        menuPatch.getItem (menuPatch.getItemCount ()-1).setAccelerator (
        KeyStroke.getKeyStroke (KeyEvent.VK_T, KeyEvent.CTRL_MASK));
        sendToAction=new SendToAction ();
        menuPatch.add (sendToAction);       
        storeAction=new StoreAction ();
        menuPatch.add (storeAction);
        receiveAction=new GetAction();
        menuPatch.add(receiveAction);                     // phil@muqus.com

        menuPatch.add (new JSeparator ());
        playAction=new PlayAction ();
        menuPatch.add (playAction);
        menuPatch.getItem (menuPatch.getItemCount ()-1).setAccelerator (
        KeyStroke.getKeyStroke (KeyEvent.VK_P, KeyEvent.CTRL_MASK));
        editAction=new EditAction ();
        menuPatch.add (editAction);
        menuPatch.getItem (menuPatch.getItemCount ()-1).setAccelerator (
        KeyStroke.getKeyStroke (KeyEvent.VK_E, KeyEvent.CTRL_MASK));
        menuPatch.add (new JSeparator ());
        reassignAction=new ReassignAction();
 	menuPatch.add (reassignAction);
        crossBreedAction=new CrossBreedAction ();
        menuPatch.add (crossBreedAction);
        newPatchAction=new NewPatchAction ();
        menuPatch.add (newPatchAction);
        extractAction=new ExtractAction ();
        menuPatch.add (extractAction);
        menuBar.add (menuPatch);
        
        JMenu menuConfig = new JMenu ("Config");
        menuConfig.setMnemonic (KeyEvent.VK_C);
        JMenuItem menuSynths = new JMenuItem ("Synths");
        menuSynths.setMnemonic (KeyEvent.VK_S);
        menuSynths.addActionListener (new ActionListener ()
        {
            public void actionPerformed (ActionEvent e)
            {
                SynthConfigDialog scd= new SynthConfigDialog (PatchEdit.this);
                scd.show ();
            }
        }
        );
        menuConfig.add (menuSynths);
        PrefsAction prefsAction=new PrefsAction ();
        menuConfig.add (prefsAction);
        NoteChooserAction noteChooserAction=new NoteChooserAction ();
        menuConfig.add (noteChooserAction);
        MonitorAction monitorAction=new MonitorAction ();
        menuConfig.add (monitorAction);

        menuBar.add (menuConfig);
        
        JMenu menuHelp = new JMenu ("Help");
        menuHelp.setMnemonic (KeyEvent.VK_H);
        JMenuItem menuAbout = new JMenuItem ("About");
        menuAbout.setMnemonic (KeyEvent.VK_A);
       
  
        menuAbout.addActionListener (new ActionListener ()
        {
            public void actionPerformed (ActionEvent e)
            {
                JOptionPane.showMessageDialog (null, "JSynthLib Version 0.18\nCopyright (C) 2000-02 Brian Klock et al.\nSee the file 'LICENSE.TXT' for more info","About JSynthLib", JOptionPane.INFORMATION_MESSAGE);return;
            }
        }
        );
        menuHelp.add (menuAbout);
        docsAction = new DocsAction();
	menuHelp.add(docsAction);
        menuBar.add (menuHelp);
        
        menuPatchPopup=new JPopupMenu ();
        menuPatchPopup.add (playAction);
        menuPatchPopup.add (editAction);
        menuPatchPopup.add (new JSeparator ());
        menuPatchPopup.add (reassignAction);
        menuPatchPopup.add (storeAction);
        menuPatchPopup.add (sendAction);
        menuPatchPopup.add (sendToAction);
        menuPatchPopup.add (new JSeparator ());
        menuPatchPopup.add (cutAction);
        menuPatchPopup.add (copyAction);
        menuPatchPopup.add (pasteAction);
        
        JButton b;
        toolBar = new JToolBar ();
        toolBar.setPreferredSize (new Dimension (500, 35));
        toolBar.setFloatable (true);
        b=toolBar.add (newAction);
        b.setToolTipText ("New Library");
        b.setIcon (loadIcon ("images/New24.gif"));
        b.setText (null);
        b=toolBar.add (openAction);
        b.setToolTipText ("Open Library");
        b.setIcon (loadIcon ("images/Open24.gif"));
        b.setText (null);
        b=toolBar.add (saveAction);
        b.setToolTipText ("Save Library");
        b.setIcon (loadIcon ("images/Save24.gif"));
        b.setText (null);
        toolBar.addSeparator ();
        b=toolBar.add (copyAction);
        b.setToolTipText ("Copy Patch");
        b.setIcon (loadIcon ("images/Copy24.gif"));
        b.setText (null);
        b=toolBar.add (cutAction);
        b.setToolTipText ("Cut Patch");
        b.setIcon (loadIcon ("images/Cut24.gif"));
        b.setText (null);
        b=toolBar.add (pasteAction);
        b.setToolTipText ("Paste Patch");
        b.setIcon (loadIcon ("images/Paste24.gif"));
        b.setText (null);
        b=toolBar.add (importAction);
        b.setToolTipText ("Import Patch");
        b.setIcon (loadIcon ("images/Import24.gif"));
        b.setText (null);
        b=toolBar.add (exportAction);
        b.setToolTipText ("Export Patch");
        b.setIcon (loadIcon ("images/Export24.gif"));
        b.setText (null);
        toolBar.addSeparator ();
        b=toolBar.add (playAction);
        b.setToolTipText ("Play Patch");
        b.setIcon (loadIcon ("images/Volume24.gif"));
        b.setText (null);
        b=toolBar.add (storeAction);
        b.setToolTipText ("Store Patch");
        b.setIcon (loadIcon ("images/ComposeMail24.gif"));
        b.setText (null);
        b=toolBar.add (editAction);
        b.setToolTipText ("Edit Patch");
        b.setIcon (loadIcon ("images/Edit24.gif"));
        b.setText (null);
        toolBar.addSeparator ();
        NextFaderAction nextFaderAction = new NextFaderAction ();
        b=toolBar.add (nextFaderAction);
        b.setToolTipText ("Go to Next Fader Bank");
        b.setIcon (loadIcon ("images/Forward24.gif"));
        b.setText (null);
        
        



        
        
        getContentPane ().add (toolBar,BorderLayout.NORTH);
        toolBar.setVisible (true); //necessary as of kestrel
        
        return menuBar;
    }
    // This creates a new [empty] Library Window
    protected void createFrame ()
    {
        LibraryFrame frame = new LibraryFrame ();
        frame.setVisible (true);
        desktop.add (frame);
        try
        {
            frame.setSelected (true);
        } catch (java.beans.PropertyVetoException e)
 {}
    }

    protected void createPerformanceFrame ()
    {
        PerformanceFrame frame = new PerformanceFrame ();
        frame.setVisible (true);
        desktop.add (frame);
        try
        {
            frame.setSelected (true);
        } catch (java.beans.PropertyVetoException e)
        {}  //I don't *actually* know what this is for :-)
    }
    
    // Lets create a new Library Window and load a Library from disk to fill it! Fun!
    protected void openFrame (File file)
    {
        LibraryFrame frame = new LibraryFrame (file);
        try
        {
            frame.setVisible (true);
            frame.open (file);
            desktop.add (frame);
        } catch (Exception e)
        {
            PerformanceFrame frame2=new PerformanceFrame(file);
            try
            {
                frame2.setVisible (true);
                frame2.open (file);
                desktop.add (frame2);
            } catch (Exception e2)
            {
                ErrorMsg.reportError ("Error","Error Loading Library",e2);return;}
             try
             {
                frame2.setSelected (true);
             } catch (java.beans.PropertyVetoException e2)
             {}
        }
        try
        {
            frame.setSelected (true);
        } catch (java.beans.PropertyVetoException e)
        {}
    }
    // And this one saves a Library to Disk
    protected void saveFrame ()
    {
        LibraryFrame libFrame;
        try
        {
            libFrame=(LibraryFrame)desktop.getSelectedFrame ();
            if (libFrame.getTitle ().startsWith ("Unsaved Library"))
            {saveFrameAs ();return;}
            libFrame.save ();
        } catch (Exception e)
        {
        PerformanceFrame perFrame;
        try
        {
            perFrame=(PerformanceFrame)desktop.getSelectedFrame ();
            if (perFrame.getTitle ().startsWith ("Unsaved "))
            {saveFrameAs ();return;}
            perFrame.save ();
        } catch (Exception e2)               {
        ErrorMsg.reportError ("Error", "Unable to Save Library",e2);return;}
    }
    }
    // Save and specify a file name
    protected void saveFrameAs ()
    {
        JFileChooser fc2=new JFileChooser ();
        javax.swing.filechooser.FileFilter type1 = new ExtensionFilter ("PatchEdit Library Files",".patchlib");
        fc2.setCurrentDirectory (new File (appConfig.getLibPath()));
        fc2.addChoosableFileFilter (type1);
        fc2.setFileFilter (type1);
        int returnVal = fc2.showSaveDialog (PatchEdit.this);
        if (returnVal == JFileChooser.APPROVE_OPTION)
        {
            File file = fc2.getSelectedFile ();
            try
            {
                if (desktop.getSelectedFrame ()==null)
                {ErrorMsg.reportError ("Error", "Unable to Save Library. Library to save must be focused");
                }
                else
                {
                    if (!file.getName ().toUpperCase ().endsWith (".PATCHLIB"))
                        file=new File (file.getPath ()+".patchlib");
                    if (file.isDirectory ())
                    { ErrorMsg.reportError ("Error", "Can not Save over a Directory");
                      return;
                    }
                    if (file.exists ())
                        if (JOptionPane.showConfirmDialog (null,"Are you sure?","File Exists",JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) return;
                    try {
                    ((LibraryFrame)desktop.getSelectedFrame ()).save (file);
                       } catch (Exception pr)
                       {
                         ((PerformanceFrame)desktop.getSelectedFrame()).save(file);
                       }
                }
            } catch (Exception ex)
            {ErrorMsg.reportError ("Error", "Unable to Save Library",ex);}
            
        }
    }
    // Generally the app is started by running JSynthLib, so the following few lines are not necessary, but I won't delete
    // them just yet.
    public static void main (String[] args)
    {
        PatchEdit frame = new PatchEdit ();
        frame.setVisible (true);
    }
    // This routine just saves the current settings in the config file. Its called when the user quits the app
    public void savePrefs ()
    {
        try
        {
			// Save the appconfig
			this.appConfig.store();
        } catch(Exception e)
        {ErrorMsg.reportError ("Error", "Unable to Save Preferences",e);}
    }
    
    //And this one loads the settings on start up.
    public boolean loadPrefs ()
    {
        appConfig.setInitPortIn(0);
        appConfig.setInitPortOut(0);
        appConfig.setLibPath("");
        appConfig.setSysexPath("");
        appConfig.setNote(60);
		appConfig.setVelocity(100);
		appConfig.setDelay(500);

        try
        {
			// Load the appconfig
			this.appConfig.load();
			return true;
        } catch(Exception e)
		{
			return false;
        }
		finally {
			if (this.appConfig.deviceCount()==0) {
				appConfig.addDevice (new synthdrivers.Generic.GenericDevice());
			}
		}
    }
    
    // This code loads the midi driver specified in the preferences.
    boolean loadMidiDriver ()
    {
        
        if (appConfig.getInitPortIn()<0) appConfig.setInitPortIn(0);
        if (appConfig.getInitPortOut()<0) appConfig.setInitPortOut(0);
        if (appConfig.getFaderPort()<0) appConfig.setFaderPort(0);
        if (appConfig.getMasterController()<0) appConfig.setMasterController(0);
        
        
        try
        {
            switch (appConfig.getMidiPlatform())
            {
                case 0: MidiOut=new DoNothingMidiWrapper (0,0); break;
                case 2: MidiOut=new WireMidiWrapper (appConfig.getInitPortIn(),appConfig.getInitPortOut()); break;
                case 3: MidiOut=new LinuxMidiWrapper (appConfig.getInitPortIn(),appConfig.getInitPortOut()); break;
                case 4: MidiOut=new MacOSXMidiWrapper (appConfig.getInitPortIn(),appConfig.getInitPortOut()); break;
            }
            MidiIn=MidiOut;
        } catch (Exception e)
        {
            e.printStackTrace ();
            return false;
            
        }
        if (appConfig.getMidiPlatform()!=1) return true;
        
        // Ugly Special Case code for Initializing JavaMIDI, which is very picky about initializing.
        
        try
        {
            MidiOut=new  JavaMidiWrapper (appConfig.getInitPortIn(),appConfig.getInitPortOut());
            currentPort=appConfig.getInitPortOut();
        }
        catch (Exception e6)
        {
            ErrorMsg.reportError ("Error!","Unable to Initialize MIDI IN/OUT! \nMidi Transfer will be unavailable this session.\nChange the Initialization Port Settings under Preferences and restart.",e6);
            try
            {
                appConfig.setFaderPort(1);
                MidiOut=new JavaMidiWrapper (1,1);
                currentPort=1;
            }
            catch (Exception e7)
            {
                try
                {appConfig.setFaderPort(2);MidiOut=new
                 JavaMidiWrapper (2,2);currentPort=2; } catch (Exception e8)
                 {
                     try
                     {appConfig.setFaderPort(3);MidiOut=new
                      JavaMidiWrapper (4,4);currentPort=3; } catch (Exception e9)
                      {
                          try
                          {appConfig.setFaderPort(3);MidiOut=new
                           JavaMidiWrapper (5,5);currentPort=3; } catch (Exception e10)
                           {
                               try
                               {appConfig.setFaderPort(3);MidiOut=new
                                JavaMidiWrapper (6,6);currentPort=3; } catch (Exception e11)
                                {
                                    try
                                    {appConfig.setFaderPort(3);MidiOut=new
                                     JavaMidiWrapper (7,7);currentPort=3; } catch (Exception e12)
                                     {
                                     }}}}}}
                                     
        }
        return true;
    }
    
    
/* Now we start with the various action classes. Each of these preforms one of the menu commands and are called either from
   the menubar, popup menu or toolbar.*/
    
     class ReassignAction extends AbstractAction
     {
         public ReassignAction ()
         {super ("Reassign",null);
          //putValue (Action.MNEMONIC_KEY, new Integer ('R'));
          setEnabled (false);
         }
         public void actionPerformed (ActionEvent e)
         {
             try
             {
                 ((PatchBasket)desktop.getSelectedFrame ()).ReassignSelectedPatch ();
             } catch (Exception ex)
             {ErrorMsg.reportError ("Error","Patch to Reassign must be highlighted in the focused Window.",ex);};
         }
     }
    
    class PlayAction extends AbstractAction
    {
        public PlayAction ()
        {super ("Play",null);
         putValue (Action.MNEMONIC_KEY, new Integer ('P'));
         setEnabled (false);
        }
        public void actionPerformed (ActionEvent e)
        {
            try
            {
                ((PatchBasket)desktop.getSelectedFrame ()).PlaySelectedPatch ();
            } catch (Exception ex)
            {ErrorMsg.reportError ("Error","Patch to Play must be highlighted in the focused Window.",ex);};
        }
    }
    
    class StoreAction extends AbstractAction
    {
        public StoreAction ()
        {
            super ("Store",null);
            putValue (Action.MNEMONIC_KEY, new Integer ('R'));
            setEnabled (false);
        }
        public void actionPerformed (ActionEvent e)
        {
            try
            {
                ((PatchBasket)desktop.getSelectedFrame ()).StoreSelectedPatch ();
            } catch (Exception ex)
             {ErrorMsg.reportError ("Error", "Patch to Store must be highlighted in the focused Window.",ex);};
        }
    }
    
    class SendAction extends AbstractAction
    {
        public SendAction ()
        {
            super ("Send",null);
            putValue (Action.MNEMONIC_KEY, new Integer ('S'));
            setEnabled (false);
        }
        public void actionPerformed (ActionEvent e)
        {
            try
            {
                ((PatchBasket)desktop.getSelectedFrame ()).SendSelectedPatch ();
            } catch (Exception ex)
            {ErrorMsg.reportError ("Error","Patch to Send must be highlighted in the focused Window.",ex);};
        }
    }
    
     class SendToAction extends AbstractAction
     {
         public SendToAction ()
         {
             super ("Send to...",null);
             //putValue (Action.MNEMONIC_KEY, new Integer ('S'));
             setEnabled (false);
         }
         public void actionPerformed (ActionEvent e)
         {
             try
             {
                 ((PatchBasket)desktop.getSelectedFrame ()).SendToSelectedPatch ();
             } catch (Exception ex)
             {ErrorMsg.reportError ("Error","Patch to 'Send to...' must be highlighted in the focused Window.",ex);};
         }
     }
     
    class DeleteAction extends AbstractAction
    {
        public DeleteAction ()
        {
            super ("Delete",null);
            setEnabled (false);
            putValue (Action.MNEMONIC_KEY, new Integer ('D'));
        }
        
        public void actionPerformed (ActionEvent e)
        {
            try
            {
                ((PatchBasket)desktop.getSelectedFrame ()).DeleteSelectedPatch ();
            } catch (Exception ex)
            {ErrorMsg.reportError ("Error", "Patch to delete must be hilighted\nin the focused Window.",ex);};
        }
    }
    
    class CopyAction extends AbstractAction
    {
        public CopyAction ()
        {
            super ("Copy",null);
            setEnabled (false);
            putValue (Action.MNEMONIC_KEY, new Integer ('C'));
        }
        public void actionPerformed (ActionEvent e)
        {
            try
            {
                ((PatchBasket)desktop.getSelectedFrame ()).CopySelectedPatch ();
            } catch (Exception ex)
            {ErrorMsg.reportError ("Error","Patch to copy must be highlighted\nin the focused Window.",ex);};
        }
    }
    
    class CutAction extends AbstractAction
    {
        public CutAction ()
        {
            super ("Cut",null);
            setEnabled (false);
            putValue (Action.MNEMONIC_KEY, new Integer ('T'));
        }
        public void actionPerformed (ActionEvent e)
        {
            try
            {
                ((PatchBasket)desktop.getSelectedFrame ()).CopySelectedPatch ();
                ((PatchBasket)desktop.getSelectedFrame ()).DeleteSelectedPatch ();
            } catch (Exception ex)
            {ErrorMsg.reportError ("Error","Patch to cut must be hilighted\nin the focused Window.",ex);};
        }
    }
    
    class PasteAction extends AbstractAction
    {
        public PasteAction ()
        {
            super ("Paste",null);
            setEnabled (false);
            putValue (Action.MNEMONIC_KEY, new Integer ('P'));
        }
        public void actionPerformed (ActionEvent e)
        {
            try
            {
                ((PatchBasket)desktop.getSelectedFrame ()).PastePatch ();
            } catch (Exception ex)
            {ErrorMsg.reportError ("Error","Library to Paste into must be the focused Window.",ex);};
        }
    }
    
    class EditAction extends AbstractAction
    {
        public EditAction ()
        {
            super ("Edit",null);
            putValue (Action.MNEMONIC_KEY, new Integer ('E'));
            setEnabled (false);
        }
        public void actionPerformed (ActionEvent e)
        {
            try
            {
                JInternalFrame frm=
                ((PatchBasket)desktop.getSelectedFrame ()).EditSelectedPatch ();
                if (frm!=null)
                {
                    frm.setVisible (true);
                    desktop.add (frm);
                    if (frm instanceof PatchEditorFrame)
                        for (int i=0;i<((PatchEditorFrame)frm).sliderList.size ();i++)
                        {JSlider slider=(JSlider)((PatchEditorFrame)frm).sliderList.get (i);
                         Dimension dim = slider.getSize (); if (dim.width > 0)
                         { dim.width++; slider.setSize (dim); }
                        }
                    
                    try
                    {
                        frm.setSelected (true);
                    } catch (java.beans.PropertyVetoException ex)
                    {}
                }
            } catch (Exception ex)
            {ErrorMsg.reportError ("Error", "Library holding Patch to Edit must be the focused Window.",ex);};
        }
    }
    
    
    class ExportAction extends AbstractAction
    {
        public ExportAction ()
        {super ("Export",null);
         putValue (Action.MNEMONIC_KEY, new Integer ('O'));
         setEnabled (false);
        }
        public void actionPerformed (ActionEvent e)
        {
            JFileChooser fc3=new JFileChooser ();
            javax.swing.filechooser.FileFilter type1 = new ExtensionFilter ("Sysex Files",".syx");
            fc3.addChoosableFileFilter (type1);
            fc3.setCurrentDirectory (new File (appConfig.getSysexPath()));
            fc3.setFileFilter (type1);
            int returnVal = fc3.showSaveDialog (PatchEdit.this);
            if (returnVal == JFileChooser.APPROVE_OPTION)
            {
                File file = fc3.getSelectedFile ();
                try
                {
                    if (desktop.getSelectedFrame ()==null)
                    {ErrorMsg.reportError ("Error", "Patch to export must be hilighted\n in the currently focuses Library");}
                    else
                        ((PatchBasket)desktop.getSelectedFrame ()).ExportPatch (file);
                } catch (IOException ex)
                {ErrorMsg.reportError ("Error", "Unable to Save Exported Patch",ex);}
            }
        }
    }
    
//------ Start phil@muqus.com
//======================================================================================================================
// Sub Class: GetAction
//======================================================================================================================

  class GetAction extends AbstractAction {

//----------------------------------------------------------------------------------------------------------------------
// Constructor: GetAction
//----------------------------------------------------------------------------------------------------------------------

    public GetAction() {
      super ("Get",null);
      putValue(Action.MNEMONIC_KEY, new Integer('G'));
      setEnabled(false);
    }

//----------------------------------------------------------------------------------------------------------------------
// GetAction->actionPerformed
//----------------------------------------------------------------------------------------------------------------------

    public void actionPerformed(ActionEvent e) {
      echoTimer.stop();    
      SysexGetDialog myDialog = new SysexGetDialog(PatchEdit.instance);
      myDialog.show();
      echoTimer.start();    
    }

  } // End SubClass: GetAction
//------ End phil@muqus.com

    class ImportAction extends AbstractAction
    {
        public ImportAction ()
        {super ("Import",null);
         putValue (Action.MNEMONIC_KEY, new Integer ('I'));
         setEnabled (false);
        }
        public void actionPerformed (ActionEvent e)
        {
            JFileChooser fc2=new JFileChooser ();
            javax.swing.filechooser.FileFilter type1 = new ExtensionFilter ("Sysex Files",".syx");
            javax.swing.filechooser.FileFilter type2 = new ExtensionFilter ("MIDI Files" ,".mid");	// core.ImportMidiFile extracts Sysex Messages from MidiFile
            fc2.addChoosableFileFilter (type1);
            fc2.addChoosableFileFilter (type2);
            fc2.setCurrentDirectory (new File (appConfig.getSysexPath()));
            fc2.setFileFilter (type1);
            int returnVal = fc2.showOpenDialog (PatchEdit.this);
            if (returnVal == JFileChooser.APPROVE_OPTION)
            {
                File file = fc2.getSelectedFile ();
                try
                {
                    if (desktop.getSelectedFrame ()==null)
                    {ErrorMsg.reportError ("Error","Library to Import Patch\n into Must be in Focus");
                    }
                    else
                        ((PatchBasket)desktop.getSelectedFrame ()).ImportPatch (file);
                } catch (IOException ex)
                {ErrorMsg.reportError ("Error","Unable to Load Sysex Data",ex);}

            };
            
        }
    }
    
    class NewAction extends AbstractAction
    {
        public NewAction ()
        {super ("New",null);
         putValue (Action.MNEMONIC_KEY, new Integer ('N'));
        }
        public void actionPerformed (ActionEvent e)
        {createFrame ();}

    }

    class NewPerformanceAction extends AbstractAction
    {
        public NewPerformanceAction ()
        {
            super ("NewPerformance",null);
        }

        public void actionPerformed (ActionEvent e)
        {
            createPerformanceFrame ();
        }
    }
    

   class TransferPerformanceAction extends AbstractAction
    {
        public TransferPerformanceAction ()
        {
            super ("Transfer Performance",null);
            //putValue (Action.MNEMONIC_KEY, new Integer ('S'));
            setEnabled (false);
        }
        public void actionPerformed (ActionEvent e)
        {
            try
            {
                ((PerformanceFrame)desktop.getSelectedFrame ()).sendPerformance ();
            } catch (Exception ex)
            {ErrorMsg.reportError ("Error","Performance Library must be the selected window.",ex);};
        }
    }
    class OpenAction extends AbstractAction
    {
        public OpenAction ()
        {super ("Open",null);
         putValue (Action.MNEMONIC_KEY, new Integer ('O'));
        }
        public void actionPerformed (ActionEvent e)
        {
            JFileChooser fc=new JFileChooser ();
            javax.swing.filechooser.FileFilter type1 = new ExtensionFilter ("PatchEdit Library Files",new String[] {".patchlib",".perflib"});
            fc.setCurrentDirectory (new File (appConfig.getLibPath()));
            fc.addChoosableFileFilter (type1);
            fc.setFileFilter (type1);
            int returnVal = fc.showOpenDialog (PatchEdit.this);
            if (returnVal == JFileChooser.APPROVE_OPTION)
            {
                File file = fc.getSelectedFile ();
                openFrame (file);
            }
        }
    }
    class SaveAction extends AbstractAction
    {
        public SaveAction ()
        {super ("Save",null);
         setEnabled (false);
         putValue (Action.MNEMONIC_KEY, new Integer ('S'));
        }
        
        public void actionPerformed (ActionEvent e)
        {
            saveFrame ();
        }
    }
    class ExitAction extends AbstractAction
    {
        public ExitAction ()
        {super ("Exit",null);
         putValue (Action.MNEMONIC_KEY, new Integer ('X'));
        }
        public void actionPerformed (ActionEvent e)
        {
            savePrefs ();
            System.exit (0);
        }
    }
    
    class ExtractAction extends AbstractAction
    {
        public ExtractAction ()
        {super ("Extract",null);
         putValue (Action.MNEMONIC_KEY, new Integer ('E'));
         setEnabled (false);
        }
        
        public void actionPerformed (ActionEvent e)
        {
            try
            {
                ((AbstractLibraryFrame)desktop.getSelectedFrame ()).ExtractSelectedPatch ();
            } catch (Exception ex)
            {ErrorMsg.reportError ("Error","Can not Extract (Maybe its not a bank?)",ex);};
        }
    }
    class SortAction extends AbstractAction
    {
        public SortAction ()
        {super ("Sort",null);
         setEnabled (false);
         putValue (Action.MNEMONIC_KEY, new Integer ('R'));
        }
        
        public void actionPerformed (ActionEvent e)
        {
            try
            {
                SortDialog sd = new SortDialog (PatchEdit.this);
                sd.show ();
            } catch (Exception ex)
            {ErrorMsg.reportError ("Error","Library to Sort must be Focused",ex);};
        }
    }
    
    class SearchAction extends AbstractAction
    {
        public SearchAction ()
        {super ("Search",null);
         setEnabled (false);
         putValue (Action.MNEMONIC_KEY, new Integer ('E'));
        }
        
        public void actionPerformed (ActionEvent e)
        {
            try
            {
                if (searchDialog==null) searchDialog = new SearchDialog (PatchEdit.this);
                searchDialog.show ();
            } catch (Exception ex)
            {ErrorMsg.reportError ("Error","Library to Sort must be Focused",ex);};
        }
    }
    
    
    class ImportAllAction extends AbstractAction
    {
        public ImportAllAction ()
        {
            super ("ImportAll",null);
            setEnabled (false);
            putValue (Action.MNEMONIC_KEY, new Integer ('A'));
        }
        
        public void actionPerformed (ActionEvent e)
        {
            try
            {
                JFileChooser fc2=new JFileChooser ();
                javax.swing.filechooser.FileFilter type1 = new ExtensionFilter ("Sysex Files",".syx");
                fc2.addChoosableFileFilter (type1);
                fc2.setFileFilter (type1);
                fc2.setCurrentDirectory (new File (appConfig.getSysexPath()));
                fc2.setFileSelectionMode (JFileChooser.DIRECTORIES_ONLY);
                fc2.setDialogTitle ("Choose Directory to Import All Files From");
                int returnVal = fc2.showOpenDialog (PatchEdit.this);
                if (returnVal != JFileChooser.APPROVE_OPTION) return;
                File file = fc2.getSelectedFile ();
                
                ImportAllDialog sd = new ImportAllDialog (PatchEdit.this,file);
                sd.show ();
            } catch (Exception ex)
            {ErrorMsg.reportError ("Error","Unable to Import Patches",ex);};
        }
    }
    class DeleteDuplicatesAction extends AbstractAction
    {
        public DeleteDuplicatesAction ()
        {super ("Delete Dups",null);
         setEnabled (false);
         putValue (Action.MNEMONIC_KEY, new Integer ('D'));
        }
        
        public void actionPerformed (ActionEvent e)

        {
            try
            {
                if (JOptionPane.showConfirmDialog (null,"This Operation will change the ordering of the Patches. Continue?","Delete Duplicate Patches",JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) return;
                PatchEdit.waitDialog.show ();
                Collections.sort (((LibraryFrame)desktop.getSelectedFrame ()).myModel.PatchList,new SysexSort ());
                int numDeleted=0;
                Patch p,q;
                Iterator it= ((LibraryFrame)desktop.getSelectedFrame ()).myModel.PatchList.iterator ();
                p=(Patch)it.next ();
                while (it.hasNext ())
                {
                    q=(Patch)it.next ();
                    if (Arrays.equals (p.sysex,q.sysex))
                    {it.remove (); numDeleted++;} else p=q;
                }
                JOptionPane.showMessageDialog (null, numDeleted+ " Patches were Deleted","Delete Duplicates", JOptionPane.INFORMATION_MESSAGE);
                ((LibraryFrame)desktop.getSelectedFrame ()).myModel.fireTableDataChanged ();
                ((LibraryFrame)desktop.getSelectedFrame ()).statusBar.setText
                (((LibraryFrame)desktop.getSelectedFrame ()).myModel.PatchList.size ()+" Patches");
            } catch (Exception ex)
            {ErrorMsg.reportError ("Error", "Library to Delete Duplicates in must be Focused",ex);};
            PatchEdit.waitDialog.hide ();
        }
    }
    
    
    class NewPatchAction extends AbstractAction
    {
        public NewPatchAction ()
        {
            super ("New",null);
            setEnabled (false);
            putValue (Action.MNEMONIC_KEY, new Integer ('N'));
        }
        public void actionPerformed (ActionEvent e)
        {
            try
            {
                Patch p=Clipboard;
                NewPatchDialog np=new NewPatchDialog (PatchEdit.this);
                np.show ();
                ((PatchBasket)desktop.getSelectedFrame ()).PastePatch ();
                Clipboard=p;
            } catch (Exception ex)
            {ErrorMsg.reportError ("Error","Unable to create this new patch.", ex);};
        }
    }
    
    
    class PrefsAction extends AbstractAction
    {
        public PrefsAction ()
        {
            super ("Preferences",null);
            putValue (Action.MNEMONIC_KEY, new Integer ('P'));
        }
        
        public void actionPerformed (ActionEvent e)
        {
            prefsDialog.show ();
        }
    }
    class NoteChooserAction extends AbstractAction
    {
        public NoteChooserAction ()
        {
            super ("Choose Note",null);
            putValue (Action.MNEMONIC_KEY, new Integer ('C'));
        }
        
        public void actionPerformed (ActionEvent e)
        {
            noteChooserDialog.show ();
        }
    }
    
    //This is a comparator class used by the delete duplicated action to sort based on the sysex data
    //Sorting this way makes the Dups search much easier, since the dups must be next to each other
    static class SysexSort implements Comparator
    {
        public int compare (Object a1, Object a2)
        { String s1=new String (((Patch)(a1)).sysex);
          String s2=new String (((Patch)(a2)).sysex);
          return s1.compareTo (s2);
        }
    }
    class CrossBreedAction extends AbstractAction
    {
        public CrossBreedAction ()
        {super ("Cross Breed",null);
         setEnabled (false);
         putValue (Action.MNEMONIC_KEY, new Integer ('B'));
        }
        public void actionPerformed (ActionEvent e)
        {
            try
            {
                CrossBreedDialog cbd = new CrossBreedDialog (PatchEdit.this);
                cbd.show ();
            } catch (Exception ex)
            {ErrorMsg.reportError ("Error", "Unable to perform Crossbreed. (No Library selected?)",ex);};
        }
    }
    class NextFaderAction extends AbstractAction
    {
        public NextFaderAction ()
        {super ("Go to Next Fader Bank",null);
         putValue (Action.MNEMONIC_KEY, new Integer ('F'));
        }
        public void actionPerformed (ActionEvent e)
        {
            if (!(desktop.getSelectedFrame () instanceof PatchEditorFrame)) return;
            PatchEditorFrame pf=(PatchEditorFrame)desktop.getSelectedFrame ();
            pf.faderBank=(pf.faderBank+1)% pf.numFaderBanks; pf.faderHighlight ();return;
        }
    }
    class DocsAction extends AbstractAction
    {
        public DocsAction ()
        {super ("Documentation",null);
         setEnabled (true);
         putValue (Action.MNEMONIC_KEY, new Integer ('D'));
        }
        public void actionPerformed (ActionEvent e)
        {
	    try
            {
                if (documentationWindow==null) documentationWindow = new DocumentationWindow ();
                documentationWindow.show ();
            } catch (Exception ex)
            {ErrorMsg.reportError ("Error", "Unable to show Documentation)",ex);};
        }
    }
    class MonitorAction extends AbstractAction
    {
        public MonitorAction ()
        {super ("MIDI Monitor",null);
         setEnabled (true);
         putValue (Action.MNEMONIC_KEY, new Integer ('D'));
        }
        public void actionPerformed (ActionEvent e)
        {
	    try
            {
                if (midiMonitor==null) midiMonitor = new MidiMonitor ();
                midiMonitor.show ();
            } catch (Exception ex)
            {ErrorMsg.reportError ("Error", "Unable to show Midi Monitor)",ex);};
        }
    }
    
    
    // This allows icons to be loaded even if they are inside a Jar file
    private ImageIcon loadIcon (String name)
    {
        Object icon;
        String jarName = null;
        icon = new ImageIcon (name);
        if (((ImageIcon)icon).getIconWidth () == -1)
        {
            jarName = new String ("/");
            jarName = jarName.concat (name);
            try
            {
                icon = new ImageIcon (this.getClass ().getResource (jarName));
            }
            catch (java.lang.NullPointerException e)
            {
                ErrorMsg.reportStatus ("ImageIcon:LoadIcon Could not find: " + name);
            }
        }
        return (ImageIcon)icon;
    }
    // This function sets up a Timer event to echo Midi Input to Midi Output
    protected void beginEcho ()
    {
        echoTimer = new javax.swing.Timer (5, new ActionListener ()
        {
            byte buffer[] = new byte[128];
            Patch p;
            public void actionPerformed (ActionEvent evt)
            {
                
                try
                {
                    //FIXME there is a bug in the javaMIDI classes so this routine gets the inputmessages from the faderbox as well as the
                    // master controller. I cant figure out a way to fix it so let's just handle them here.
                    
                    if ((appConfig.getFaderEnable()) &(PatchEdit.MidiIn.messagesWaiting (appConfig.getMasterController())>0))
                        for (int i=0;i<33;i++) newFaderValue[i]=255;
                    while (PatchEdit.MidiIn.messagesWaiting (appConfig.getMasterController())>0)
                    {
                        int size; 
                        int port;
                        size=PatchEdit.MidiIn.readMessage (appConfig.getMasterController(),buffer,128);
                        p=PatchEdit.Clipboard;
                        if ((desktop.getSelectedFrame () instanceof PatchBasket)&&(!(desktop.getSelectedFrame () instanceof PatchEditorFrame)))
                        {
                            if ((desktop.getSelectedFrame () instanceof LibraryFrame ) &
                            ((LibraryFrame)((desktop.getSelectedFrame ()))).table.getSelectedRowCount ()==0)
                                break;
                            ((PatchBasket)desktop.getSelectedFrame ()).CopySelectedPatch ();
                        }
                        else 
                            Clipboard=((PatchEditorFrame)desktop.getSelectedFrame ()).p;
                        //   port=(PatchEdit.deviceList.get (Clipboard.deviceNum)).
                        port=appConfig.getDevice(Clipboard.deviceNum).getPort();
                        if ((appConfig.getFaderEnable())&(desktop.getSelectedFrame () instanceof PatchEditorFrame) && (buffer[0]&0xF0) == 0xB0)
                            sendFaderMessage (buffer[0],buffer[1],buffer[2]);
			else
			    if ( (buffer[0]&0xF0) == 0xD0)
				{
				    //do nothing for now
				}
                        else
                         
			    {
                            if (((buffer[0] & 0xF0) > 0x70) && ((buffer[0] & 0xF0) <0xF0 ))
                                buffer[0]=(byte)((buffer[0] & 0xF0) +appConfig.getDevice(Clipboard.deviceNum).getChannel()-1);
                            PatchEdit.MidiOut.writeLongMessage (port,buffer,size);
                        }
                        PatchEdit.Clipboard=p;
                    }
                    
                } catch (Exception ex)
                {ErrorMsg.reportStatus (ex);};
                
            }
        }
        );
        echoTimer.start ();
    }
    
    void sendFaderMessage (byte status, byte controller, byte value)
    {
        byte channel=(byte)(status & 0x0F);
        byte i=0;
        while (i<33)
        {
            if ((appConfig.getFaderController(i)==controller) & (appConfig.getFaderChannel(i)==channel))
            {((PatchEditorFrame)desktop.getSelectedFrame ()).faderMoved (i,value);   break;}
            i++;
        }
    }
    
    public void unloadMidiDriver ()
    {
        if (MidiIn!=null)
            MidiIn.close ();
    }
    
    public static Driver getDriver (int deviceNumber, int driverNumber)
    {
        return (Driver)appConfig.getDevice(deviceNumber).driverList.get(driverNumber);
    }
    
}
