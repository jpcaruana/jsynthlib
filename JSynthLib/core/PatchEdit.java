/* This file is the main Application object. It's called PatchEdit,
   which is probably ambiguous, but probably to late to change now.*/

/* @version $Id$ */

package core;
import java.io.File;
import javax.swing.*;
import java.util.*;
import java.awt.event.*;
import java.awt.*;
import java.io.*;
import javax.sound.midi.*;

import com.apple.eawt.ApplicationAdapter;
import com.apple.eawt.ApplicationEvent;

//TODO import /*TODO org.jsynthlib.*/midi.*;
public class PatchEdit extends JFrame implements MidiDriverChangeListener {
    public static JDesktopPane desktop;
    //   public static ArrayList DriverList = new ArrayList();
    //   public static ArrayList deviceList = new ArrayList();
    public static Patch Clipboard;
    public static MidiWrapper MidiOut;
    public static MidiWrapper MidiIn;
    public static JPopupMenu menuPatchPopup;
    static JToolBar   toolBar;

    public static int currentPort;
    public static PrefsDialog prefsDialog;
    public static AppConfig appConfig;
    // public static NoteChooserDialog noteChooserDialog; -- replaced by NoteChooserConfigPanel - emenaker 2003.03.17
    public static WaitDialog waitDialog;
    public static javax.swing.Timer echoTimer;
    public static int[] newFaderValue = new int[33];
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
    public static NewSceneAction newSceneAction;
    public static TransferSceneAction transferSceneAction;

    SearchDialog searchDialog;
    DocumentationWindow documentationWindow;
    public static PatchEdit instance; // phil@muqus.com

    /** Initialize Application: */
    public PatchEdit() {
        super("JSynthLib");
        instance = this; // phil@muqus.com (so can pop-up windows with PatchEdit as the
        boolean loadPrefsSuccessfull, loadDriverSuccessfull;
	this.appConfig = new AppConfig();

        prefsDialog = new PrefsDialog(this);
        loadPrefsSuccessfull = loadPrefs();
	// Add the configuration panels to the prefsDialog
        prefsDialog.addPanel(new /*TODO org.jsynthlib.*/GeneralConfigPanel(appConfig));
	prefsDialog.addPanel(new /*TODO org.jsynthlib.*/DirectoryConfigPanel(appConfig));
	/*TODO org.jsynthlib.midi.*/MidiConfigPanel midiConfigPanel = null;
	try {
	    midiConfigPanel = new /*TODO org.jsynthlib.midi.*/MidiConfigPanel(appConfig);
	    midiConfigPanel.addDriverChangeListener(this);
	    prefsDialog.addPanel(midiConfigPanel);
	    MidiIn = MidiOut = midiConfigPanel.getMidiWrapper();
	    // FaderBoxConfigPanel() have to be called after MidiIn is initialized.
	    /*TODO org.jsynthlib.*/FaderBoxConfigPanel faderbox = new /*TODO org.jsynthlib.*/FaderBoxConfigPanel(appConfig);
	    midiConfigPanel.addDriverChangeListener(faderbox); // Notify the faderbox, too... - emenaker 2003.03.19
	    prefsDialog.addPanel(faderbox);
	} catch (Exception e) {
	    System.err.println(e);
	    e.printStackTrace();
	}
	prefsDialog.addPanel(new /*TODO org.jsynthlib.*/NoteChooserConfigPanel(appConfig));

	prefsDialog.init();  //loads in the config file and sets parameters
        // Now lets set up how the pretty application should look
        int inset = 100;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
		  screenSize.width - inset * 2,
		  screenSize.height - inset * 2);

        //Quit this app when the big window closes.
        addWindowListener(new WindowAdapter() {
		public void windowClosing(WindowEvent e) {
		    savePrefs();
		    // We shouldn't need to unload the midi driver if
		    // the whole JVM is going away.
		    // unloadMidiDriver();
		    System.exit(0);
		}
	    });

        //Set up the GUI.
        desktop = new JDesktopPane();
        setJMenuBar(createMenus());
        desktop.setOpaque(false);
        Container c = getContentPane();
        c.add(desktop, BorderLayout.CENTER);

        desktop.putClientProperty("JDesktopPane.dragMode", "outline");
        setVisible(true);
        if (!loadPrefsSuccessfull)
            ErrorMsg.reportError("Error", "Unable to load user preferences. Defaults loaded\n If you've just installed or just upgraded this software, this is normal.");
	//if (!loadDriverSuccessfull)
	//ErrorMsg.reportError("Error","Unable to Initialize MIDI IN/OUT! \nMidi Transfer will be unavailable this session.\nChange the Initialization Port Settings under Preferences and restart.");

        //Set up a silly little dialog we can pop up for the user to
        //gawk at while we do time consuming work later on.
        waitDialog = new WaitDialog(this);


        // Start pumping MIDI information from Input --> Output so the
        // user can play a MIDI Keyboard and make pretty music
        beginEcho();
    }

    /** This sets up the Menubar as well as the main right-click Popup
	menu and the toolbar */
    protected JMenuBar createMenus() {
        HashMap mnemonics = new HashMap();
        int mask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
        JMenuBar menuBar = new JMenuBar();
        JMenu menuLib = new JMenu("Library");
	mnemonics.put(menuLib, new Integer(KeyEvent.VK_L));

        NewAction newAction = new NewAction(mnemonics);
        menuLib.add(newAction);
        menuLib.getItem(menuLib.getItemCount() - 1).setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, mask));

        OpenAction openAction = new OpenAction(mnemonics);
        menuLib.add(openAction);
        menuLib.getItem(menuLib.getItemCount() - 1).setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, mask));

        saveAction = new SaveAction(mnemonics);
        menuLib.add(saveAction);
        menuLib.getItem(menuLib.getItemCount() - 1).setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, mask));

        menuSaveAs = new JMenuItem("Save As...");
	mnemonics.put(menuSaveAs, new Integer(KeyEvent.VK_A));
        menuSaveAs.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    saveFrameAs();
		}
	    });
        menuSaveAs.setEnabled(false);
        menuLib.add(menuSaveAs);

        menuLib.addSeparator();
        newSceneAction = new NewSceneAction(mnemonics);
        menuLib.add(newSceneAction);

        transferSceneAction = new TransferSceneAction(mnemonics);
        menuLib.add(transferSceneAction);
        menuLib.addSeparator();

        sortAction = new SortAction(mnemonics);
        menuLib.add(sortAction);

        searchAction = new SearchAction(mnemonics);
        menuLib.add(searchAction);

        dupAction = new DeleteDuplicatesAction(mnemonics);
        menuLib.add(dupAction);
        menuLib.addSeparator();
        final ExitAction exitAction = new ExitAction(mnemonics);
        if (!MacUtils.isMac())
	    menuLib.add(exitAction);
        menuBar.add(menuLib);

        JMenu menuPatch = new JMenu("Patch");
	mnemonics.put(menuPatch, new Integer(KeyEvent.VK_P));
        copyAction = new CopyAction(mnemonics);
        menuPatch.add(copyAction);
        menuPatch.getItem(menuPatch.getItemCount() - 1).setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, mask));
        cutAction = new CutAction(mnemonics);
        menuPatch.add(cutAction);
        menuPatch.getItem(menuPatch.getItemCount() - 1).setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, mask));
        pasteAction = new PasteAction(mnemonics);
        menuPatch.add(pasteAction);
        menuPatch.getItem(menuPatch.getItemCount() - 1).setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, mask));
        deleteAction = new DeleteAction(mnemonics);
        menuPatch.add(deleteAction);
        menuPatch.addSeparator();
        importAction = new ImportAction(mnemonics);
        menuPatch.add(importAction);
        exportAction = new ExportAction(mnemonics);
        menuPatch.add(exportAction);
        importAllAction = new ImportAllAction(mnemonics);
        menuPatch.add(importAllAction);
        menuPatch.addSeparator();
        sendAction = new SendAction(mnemonics);
        menuPatch.add(sendAction);
        menuPatch.getItem(menuPatch.getItemCount() - 1).setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, mask));
        sendToAction = new SendToAction(mnemonics);
        menuPatch.add(sendToAction);
        storeAction = new StoreAction(mnemonics);
        menuPatch.add(storeAction);
        receiveAction = new GetAction(mnemonics);
        menuPatch.add(receiveAction); // phil@muqus.com

        menuPatch.addSeparator();
        playAction = new PlayAction(mnemonics);
        menuPatch.add(playAction);
        menuPatch.getItem(menuPatch.getItemCount() - 1).setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, mask));
        editAction = new EditAction(mnemonics);
        menuPatch.add(editAction);
        menuPatch.getItem(menuPatch.getItemCount() - 1).setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, mask));
        menuPatch.addSeparator();
        reassignAction = new ReassignAction(mnemonics);
 	menuPatch.add(reassignAction);
        crossBreedAction = new CrossBreedAction(mnemonics);
        menuPatch.add(crossBreedAction);
        newPatchAction = new NewPatchAction(mnemonics);
        menuPatch.add(newPatchAction);
        extractAction = new ExtractAction(mnemonics);
        menuPatch.add(extractAction);
        menuBar.add(menuPatch);

	final PrefsAction prefsAction = new PrefsAction(mnemonics);

	if (!MacUtils.isMac()) {
	    JMenu menuConfig = new JMenu("Config");
	    mnemonics.put(menuConfig, new Integer(KeyEvent.VK_C));
	    menuConfig.add(prefsAction);
	    // NoteChooserAction noteChooserAction = new NoteChooserAction();
	    // menuConfig.add(noteChooserAction); -- replaced by NoteChooserConfigPanel - emenaker 2003.03.17

	    menuBar.add(menuConfig);
	}

	// Moved "Synths" and "Midi Monitor" to a "Window" menu -
	// emenaker 2003.03.24
	JMenu menuWindow = new JMenu("Window");
	mnemonics.put(menuWindow, new Integer(KeyEvent.VK_C));
	JMenuItem menuSynths = new JMenuItem("Synths...");
	mnemonics.put(menuSynths, new Integer(KeyEvent.VK_S));
	menuSynths.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    SynthConfigDialog scd = new SynthConfigDialog(PatchEdit.this);
		    scd.show();
		}
	    }
				     );
	menuWindow.add(menuSynths);
	monitorAction = new MonitorAction(mnemonics);
	menuWindow.add(monitorAction);

	menuBar.add(menuWindow);

        JMenu menuHelp = new JMenu("Help");
	mnemonics.put(menuHelp, new Integer(KeyEvent.VK_H));
        final AboutAction aboutAction = new AboutAction(mnemonics);

	if (!MacUtils.isMac())
	    menuHelp.add(aboutAction);
        docsAction = new DocsAction(mnemonics);
	menuHelp.add(docsAction);
        menuBar.add(menuHelp);

        menuPatchPopup = new JPopupMenu();
        menuPatchPopup.add(playAction);
        menuPatchPopup.add(editAction);
        menuPatchPopup.addSeparator();
        menuPatchPopup.add(reassignAction);
        menuPatchPopup.add(storeAction);
        menuPatchPopup.add(sendAction);
        menuPatchPopup.add(sendToAction);
        menuPatchPopup.addSeparator();
        menuPatchPopup.add(cutAction);
        menuPatchPopup.add(copyAction);
        menuPatchPopup.add(pasteAction);

        JButton b;
        toolBar = new JToolBar();
        toolBar.setPreferredSize(new Dimension(500, 35));
        toolBar.setFloatable(true);
        b = toolBar.add(newAction);
        b.setToolTipText("New Library");
        b.setIcon(loadIcon("images/New24.gif"));
        b.setText(null);
        b = toolBar.add(openAction);
        b.setToolTipText("Open Library");
        b.setIcon(loadIcon("images/Open24.gif"));
        b.setText(null);
        b = toolBar.add(saveAction);
        b.setToolTipText("Save Library");
        b.setIcon(loadIcon("images/Save24.gif"));
        b.setText(null);
        toolBar.addSeparator();
        b = toolBar.add(copyAction);
        b.setToolTipText("Copy Patch");
        b.setIcon(loadIcon("images/Copy24.gif"));
        b.setText(null);
        b = toolBar.add(cutAction);
        b.setToolTipText("Cut Patch");
        b.setIcon(loadIcon("images/Cut24.gif"));
        b.setText(null);
        b = toolBar.add(pasteAction);
        b.setToolTipText("Paste Patch");
        b.setIcon(loadIcon("images/Paste24.gif"));
        b.setText(null);
        b = toolBar.add(importAction);
        b.setToolTipText("Import Patch");
        b.setIcon(loadIcon("images/Import24.gif"));
        b.setText(null);
        b = toolBar.add(exportAction);
        b.setToolTipText("Export Patch");
        b.setIcon(loadIcon("images/Export24.gif"));
        b.setText(null);
        toolBar.addSeparator();
        b = toolBar.add(playAction);
        b.setToolTipText("Play Patch");
        b.setIcon(loadIcon("images/Volume24.gif"));
        b.setText(null);
        b = toolBar.add(storeAction);
        b.setToolTipText("Store Patch");
        b.setIcon(loadIcon("images/ComposeMail24.gif"));
        b.setText(null);
        b = toolBar.add(editAction);
        b.setToolTipText("Edit Patch");
        b.setIcon(loadIcon("images/Edit24.gif"));
        b.setText(null);
        toolBar.addSeparator();
        NextFaderAction nextFaderAction = new NextFaderAction(mnemonics);
        b = toolBar.add(nextFaderAction);
        b.setToolTipText("Go to Next Fader Bank");
        b.setIcon(loadIcon("images/Forward24.gif"));
        b.setText(null);

        getContentPane().add(toolBar, BorderLayout.NORTH);
        toolBar.setVisible(true); //necessary as of kestrel

	if (!MacUtils.isMac())
	    setMnemonics(mnemonics);
	MacUtils.init(new ApplicationAdapter() {
		public void handleAbout(ApplicationEvent e) {
		    final ActionEvent event =
			new ActionEvent(e.getSource(), 0, "About");
		    // opens dialog, so I think we need to do this to
		    // avoid deadlock
		    SwingUtilities.invokeLater(new Runnable() {
			    public void run() {
				try {
				    aboutAction.actionPerformed(event);
				} catch (Exception e) {
				}
			    }
			});
		    e.setHandled(true);
		}
		public void handleOpenFile(ApplicationEvent e) {
		    final File file = new File(e.getFilename());
		    SwingUtilities.invokeLater(new Runnable() {
			    public void run() {
				try {
				    openFrame(file);
				} catch (Exception e) {
				}
			    }
			});
		    e.setHandled(true);
		}
		public void handlePreferences(ApplicationEvent e) {
		    e.setHandled(true);
		    final ActionEvent event =
			new ActionEvent(e.getSource(), 0, "Preferences");
		    SwingUtilities.invokeLater(new Runnable() {
			    public void run() {
				try {
				    prefsAction.actionPerformed(event);
				} catch (Exception e) {
				}
			    }
			});
		}
		public void handleQuit(ApplicationEvent e) {
		    exitAction.actionPerformed(new ActionEvent(e.getSource(), 0,
							       "Exit"));
		    e.setHandled(true);
		}
	    });
        return menuBar;
    }

    /** This sets up the mnemonics */
    protected void setMnemonics(Map mnemonics) {
	Iterator it = mnemonics.keySet().iterator();
	Object key, value;
	while (it.hasNext()) {
	    key = it.next();
	    value = mnemonics.get(key);
	    if (key instanceof JMenuItem)
		((JMenuItem) key).setMnemonic(((Integer) value).intValue());
	    else if (key instanceof Action)
		((Action) key).putValue(Action.MNEMONIC_KEY, value);
	}
    }

    /** This creates a new [empty] Library Window */
    protected void createFrame() {
        LibraryFrame frame = new LibraryFrame();
        frame.setVisible(true);
        desktop.add(frame);
        try {
	    frame.setSelected(true);
	} catch (java.beans.PropertyVetoException e) {
	}
    }

    protected void createSceneFrame() {
        SceneFrame frame = new SceneFrame();
        frame.setVisible(true);
        desktop.add(frame);
        try {
	    frame.setSelected(true);
	} catch (java.beans.PropertyVetoException e) {
	}  //I don't *actually* know what this is for :-)
    }

    /** Create a new Library Window and load a Library from disk to
	fill it! Fun! */
    protected void openFrame(File file) {
        LibraryFrame frame = new LibraryFrame(file);
        try {
	    frame.setVisible(true);
	    frame.open(file);
	    desktop.add(frame);
	} catch (Exception e) {
	    SceneFrame frame2 = new SceneFrame(file);
	    try {
		frame2.setVisible(true);
		frame2.open(file);
		desktop.add(frame2);
	    } catch (Exception e2) {
		ErrorMsg.reportError("Error", "Error Loading Library", e2);
		return;
	    }
	    try {
		frame2.setSelected(true);
	    } catch (java.beans.PropertyVetoException e2) {
	    }
	}
        try {
	    frame.setSelected(true);
	} catch (java.beans.PropertyVetoException e) {
	}
    }

    /** This one saves a Library to Disk */
    protected void saveFrame() {
	try {
	    Object oFrame = desktop.getSelectedFrame();
	    if (oFrame instanceof LibraryFrame) {
		LibraryFrame libFrame = (LibraryFrame) oFrame;
		if (libFrame.getTitle().startsWith("Unsaved Library")) {
		    saveFrameAs();
		    return;
		}
		libFrame.save();
	    } else if (oFrame instanceof SceneFrame) {
		// use oFrame !!!FIXIT!!!
		SceneFrame sceneFrame = (SceneFrame) desktop.getSelectedFrame();
		if (sceneFrame.getTitle().startsWith("Unsaved ")) {
		    saveFrameAs();
		    return;
		}
		sceneFrame.save();
	    }
	} catch (Exception e) {
	    ErrorMsg.reportError("Error", "Unable to Save Library", e);
	    return;
	}
    }

    /** Save and specify a file name */
    protected void saveFrameAs() {
        FileDialog fc2 = new FileDialog(PatchEdit.this);
	fc2.setMode(fc2.SAVE);
        FilenameFilter type1 = new ExtensionFilter("PatchEdit Library Files (*.patchlib)", ".patchlib");
        fc2.setFilenameFilter(type1);
	File file = null;
	try {
	    try {
		file = ((LibraryFrame) desktop.getSelectedFrame()).filename;
	    } catch (Exception e) {
		file = ((SceneFrame) desktop.getSelectedFrame()).filename;
	    }
	    fc2.setDirectory(file.getParent());
	    fc2.setFile(file.getName());
	} catch (Exception e) {
	    fc2.setDirectory(appConfig.getLibPath());
	    fc2.setFile("Untitled.patchlib");
	}
	fc2.show();

	if (fc2.getFile() == null)
	    return;
	file = new File(fc2.getDirectory(), fc2.getFile());
	try {
	    if (desktop.getSelectedFrame() == null) {
		ErrorMsg.reportError("Error", "Unable to Save Library. Library to save must be focused");
	    } else {
		if (!file.getName().toUpperCase().endsWith(".PATCHLIB"))
		    file = new File(file.getPath() + ".patchlib");
		if (file.isDirectory()) {
		    ErrorMsg.reportError("Error", "Can not Save over a Directory");
		    return;
		}
		if (file.exists())
		    if (JOptionPane.showConfirmDialog(null, "Are you sure?", "File Exists", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
			return;
		try {
		    ((LibraryFrame) desktop.getSelectedFrame()).save(file);
		} catch (Exception pr) {
		    ((SceneFrame) desktop.getSelectedFrame()).save(file);
		}
	    }
	} catch (Exception ex) {
	    ErrorMsg.reportError("Error", "Unable to Save Library", ex);
	}
    }

    // Generally the app is started by running JSynthLib, so the
    // following few lines are not necessary, but I won't delete them
    // just yet.
    public static void main(String[] args) {
        PatchEdit frame = new PatchEdit();
        frame.setVisible(true);
    }

    /** This routine just saves the current settings in the config
	file. Its called when the user quits the app. */
    public void savePrefs() {
        try {
	    // Save the appconfig
	    this.appConfig.store();
	} catch (Exception e) {
	    ErrorMsg.reportError("Error", "Unable to Save Preferences", e);
	}
    }

    /** And this one loads the settings on start up. */
    public boolean loadPrefs() {
    	/*
    	 * These are handled by the individual ConfigPanels now - emenaker 2003.03.25
	 appConfig.setInitPortIn(0);
	 appConfig.setInitPortOut(0);
	 appConfig.setLibPath("");
	 appConfig.setSysexPath("");
	 appConfig.setNote(60);
	 appConfig.setVelocity(100);
	 appConfig.setDelay(500);
	*/

	try {
	    // Load the appconfig
	    this.appConfig.load();
	    return true;
	} catch (Exception e) {
	    return false;
	} finally {
	    if (this.appConfig.deviceCount() == 0) {
		appConfig.addDevice(new synthdrivers.Generic.GenericDevice());
	    }
	}
    }

    /**
     * This used to be loadMidiDriver() but it is now a callback
     * method that gets notified by MidiConfigPanel if the user
     * changes midi drivers. Initialization of the drivers is now
     * handled by the drivers themselves. - emenaker 2003.03.12
     * @param driver The new MidiWrapper
     */
    public void midiDriverChanged(MidiWrapper driver) {
	MidiIn = MidiOut = driver;
    }


    /** Now we start with the various action classes. Each of these
        preforms one of the menu commands and are called either from
        the menubar, popup menu or toolbar.*/
    public class AboutAction extends AbstractAction {
	public AboutAction(Map mnemonics) {
	    super("About");
	    mnemonics.put(this, new Integer('A'));
	}

	public void actionPerformed(ActionEvent e) {
	    JOptionPane.showMessageDialog(null, "JSynthLib Version 0.18\nCopyright (C) 2000-03 Brian Klock et al.\nSee the file 'LICENSE.TXT' for more info", "About JSynthLib", JOptionPane.INFORMATION_MESSAGE);
	    return;
	}
    }

    public class ReassignAction extends AbstractAction {
	public ReassignAction(Map mnemonics) {
	    super("Reassign", null); // show a dialog frame???
	    // mnemonics.put(this, new Integer('R'));
	    setEnabled(false);
	}
	public void actionPerformed(ActionEvent e) {
	    try {
		((PatchBasket) desktop.getSelectedFrame()).ReassignSelectedPatch();
	    } catch (Exception ex) {
		ErrorMsg.reportError("Error", "Patch to Reassign must be highlighted in the focused Window.", ex);
	    }
	}
    }

    public class PlayAction extends AbstractAction {
        public PlayAction(Map mnemonics) {
            super("Play", null);
            mnemonics.put(this, new Integer('P'));
            setEnabled(false);
        }
        public void actionPerformed(ActionEvent e) {
            try {
		((PatchBasket) desktop.getSelectedFrame()).PlaySelectedPatch();
	    } catch (Exception ex) {
		ErrorMsg.reportError("Error", "Patch to Play must be highlighted in the focused Window.", ex);
	    }
        }
    }

    public class StoreAction extends AbstractAction {
        public StoreAction(Map mnemonics) {
            super("Store...", null);
            mnemonics.put(this, new Integer('R'));
            setEnabled(false);
        }
        public void actionPerformed(ActionEvent e) {
            try {
		((PatchBasket) desktop.getSelectedFrame()).StoreSelectedPatch();
	    } catch (Exception ex) {
		ErrorMsg.reportError("Error", "Patch to Store must be highlighted in the focused Window.", ex);
	    }
        }
    }

    public class SendAction extends AbstractAction {
        public SendAction(Map mnemonics) {
            super("Send", null);
	    mnemonics.put(this, new Integer('S'));
            setEnabled(false);
        }
        public void actionPerformed(ActionEvent e) {
            try {
		((PatchBasket) desktop.getSelectedFrame()).SendSelectedPatch();
	    } catch (Exception ex) {
		ErrorMsg.reportError("Error", "Patch to Send must be highlighted in the focused Window.", ex);
	    }
        }
    }

    public class SendToAction extends AbstractAction {
	public SendToAction(Map mnemonics) {
	    super("Send to...", null);
	    // mnemonics.put(this, new Integer('S'));
	    setEnabled(false);
	}
	public void actionPerformed(ActionEvent e) {
	    try {
		((PatchBasket) desktop.getSelectedFrame()).SendToSelectedPatch();
	    } catch (Exception ex) {
		ErrorMsg.reportError("Error", "Patch to 'Send to...' must be highlighted in the focused Window.", ex);
	    }
	}
    }

    public class DeleteAction extends AbstractAction {
        public DeleteAction(Map mnemonics) {
            super("Delete", null);
            setEnabled(false);
	    mnemonics.put(this, new Integer('D'));
        }

        public void actionPerformed(ActionEvent e) {
            try {
		((PatchBasket) desktop.getSelectedFrame()).DeleteSelectedPatch();
	    } catch (Exception ex) {
		ErrorMsg.reportError("Error", "Patch to delete must be hilighted\nin the focused Window.", ex);
	    }
        }
    }

    public class CopyAction extends AbstractAction {
        public CopyAction(Map mnemonics) {
            super("Copy", null);
            setEnabled(false);
	    mnemonics.put(this, new Integer('C'));
        }
        public void actionPerformed(ActionEvent e) {
            try {
		((PatchBasket) desktop.getSelectedFrame()).CopySelectedPatch();
	    } catch (Exception ex) {
		ErrorMsg.reportError("Error", "Patch to copy must be highlighted\nin the focused Window.", ex);
	    }
        }
    }

    public class CutAction extends AbstractAction {
        public CutAction(Map mnemonics) {
            super("Cut", null);
            setEnabled(false);
	    mnemonics.put(this, new Integer('T'));
        }
        public void actionPerformed(ActionEvent e) {
            try {
		((PatchBasket) desktop.getSelectedFrame()).CopySelectedPatch();
		((PatchBasket) desktop.getSelectedFrame()).DeleteSelectedPatch();
	    } catch (Exception ex) {
		ErrorMsg.reportError("Error", "Patch to cut must be hilighted\nin the focused Window.", ex);
	    }
        }
    }

    public class PasteAction extends AbstractAction {
        public PasteAction(Map mnemonics) {
            super("Paste", null);
            setEnabled(false);
	    mnemonics.put(this, new Integer('P'));
        }
        public void actionPerformed(ActionEvent e) {
            try {
		((PatchBasket) desktop.getSelectedFrame()).PastePatch();
	    } catch (Exception ex) {
		ErrorMsg.reportError("Error", "Library to Paste into must be the focused Window.", ex);
	    }
        }
    }

    public class EditAction extends AbstractAction {
        public EditAction(Map mnemonics) {
            super("Edit...", null);
	    mnemonics.put(this, new Integer('E'));
            setEnabled(false);
        }
        public void actionPerformed(ActionEvent e) {
	    Worker w = new Worker();
	    w.setDaemon(true);
	    w.start();
 	}
	class Worker extends Thread {
	    public void run() {
		try {
		    JInternalFrame frm =
			((PatchBasket) desktop.getSelectedFrame()).EditSelectedPatch();
		    if (frm != null) {
			frm.setVisible(true);
			desktop.add(frm);
			if (frm instanceof PatchEditorFrame)
			    for (int i = 0; i < ((PatchEditorFrame) frm).sliderList.size(); i++) {
				JSlider slider = (JSlider) ((PatchEditorFrame) frm).sliderList.get(i);
				Dimension dim = slider.getSize();
				if (dim.width > 0) {
				    dim.width++;
				    slider.setSize(dim);
				}
			    }
			try {
			    frm.setSelected(true);
			} catch (java.beans.PropertyVetoException ex) {
			}
		    }
		} catch (Exception ex) {
		    ErrorMsg.reportError("Error", "Library holding Patch to Edit must be the focused Window.", ex);
		}
	    }
	}
    }

    public class ExportAction extends AbstractAction {
        public ExportAction(Map mnemonics) {
            super("Export...", null);
            mnemonics.put(this, new Integer('O'));
            setEnabled(false);
        }
        public void actionPerformed(ActionEvent e) {
            FileDialog fc3 = new FileDialog(PatchEdit.this);
	    fc3.setMode(fc3.SAVE);
            FilenameFilter type1 = new ExtensionFilter("Sysex Files (*.syx)", ".syx");
            fc3.setDirectory(appConfig.getSysexPath());
            fc3.setFilenameFilter(type1);
	    fc3.show();
	    if (fc3.getFile() == null)
		return;
	    File file = new File(fc3.getDirectory(), fc3.getFile());
	    try {
		if (desktop.getSelectedFrame() == null) {
		    ErrorMsg.reportError("Error", "Patch to export must be hilighted\n in the currently focuses Library");
		} else {
		    if (!file.getName().toUpperCase().endsWith(".SYX"))
			file = new File(file.getPath() + ".syx");
		    if (file.exists())
			if (JOptionPane.showConfirmDialog(null, "Are you sure?", "File Exists", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
			    return;

		    ((PatchBasket) desktop.getSelectedFrame()).ExportPatch(file);
		}
	    } catch (IOException ex) {
		ErrorMsg.reportError("Error", "Unable to Save Exported Patch", ex);
	    }
	}
    }

    //------ Start phil@muqus.com
    //=====================================================================
    // Sub Class: GetAction
    //=====================================================================

    public class GetAction extends AbstractAction {
	//-----------------------------------------------------------------
	// Constructor: GetAction
	//-----------------------------------------------------------------
	public GetAction(Map mnemonics) {
	    super("Get...", null);
	    mnemonics.put(this, new Integer('G'));
	    setEnabled(false);
	}

	//-----------------------------------------------------------------
	// GetAction->actionPerformed
	//-----------------------------------------------------------------
	public void actionPerformed(ActionEvent e) {
	    echoTimer.stop();
	    SysexGetDialog myDialog = new SysexGetDialog(PatchEdit.instance);
	    myDialog.show();
	    echoTimer.start();
	}

    } // End SubClass: GetAction
    //------ End phil@muqus.com

    // denis: mis en public toutes les classes Action
    public class ImportAction extends AbstractAction {
        public ImportAction(Map mnemonics) {
            super("Import...", null);
            mnemonics.put(this, new Integer('I'));
            setEnabled(false);
        }
        public void actionPerformed(ActionEvent e) {
            FileDialog fc2 = new FileDialog(PatchEdit.this);
	    fc2.setMode(fc2.LOAD);
            FilenameFilter type1 = new ExtensionFilter("Sysex Files (*.syx, *.mid)",
						       new String[]{".syx", ".mid"});
	    // core.ImportMidiFile extracts Sysex Messages from MidiFile
            fc2.setDirectory(appConfig.getSysexPath());
            fc2.setFilenameFilter(type1);
	    fc2.show();
	    if (fc2.getFile() == null)
		return;
	    File file = new File(fc2.getDirectory(), fc2.getFile());
	    try {
		if (desktop.getSelectedFrame() == null) {
		    ErrorMsg.reportError("Error", "Library to Import Patch\n into Must be in Focus");
		} else
		    ((PatchBasket) desktop.getSelectedFrame()).ImportPatch(file);
	    } catch (IOException ex) {
		ErrorMsg.reportError("Error", "Unable to Load Sysex Data", ex);
	    }
	}
    }

    public class NewAction extends AbstractAction {
        public NewAction(Map mnemonics) {
	    super("New", null);
	    mnemonics.put(this, new Integer('N'));
        }
        public void actionPerformed(ActionEvent e) {
	    System.out.println(UIManager.get("MenuItemUI"));
	    createFrame();
	}
    }

    public class NewSceneAction extends AbstractAction {
        public NewSceneAction(Map mnemonics) {
            super("New Scene", null);
        }

        public void actionPerformed(ActionEvent e) {
            createSceneFrame();
        }
    }

    public class TransferSceneAction extends AbstractAction {
        public TransferSceneAction(Map mnemonics){
            super("Transfer Scene", null); // show a dialog frame???
            // mnemonics.put(this, new Integer('S'));
            setEnabled(false);
        }
        public void actionPerformed(ActionEvent e) {
            try {
		((SceneFrame) desktop.getSelectedFrame()).sendScene();
	    } catch (Exception ex) {
		ErrorMsg.reportError("Error", "Scene Library must be the selected window.", ex);
	    }
        }
    }

    public class OpenAction extends AbstractAction {
        public OpenAction(Map mnemonics) {
            super("Open...", null);
            mnemonics.put(this, new Integer('O'));
        }
        public void actionPerformed(ActionEvent e) {
            FileDialog fc = new FileDialog(PatchEdit.this);
	    fc.setMode(fc.LOAD);
            FilenameFilter type1 = new ExtensionFilter("PatchEdit Library Files (*.patchlib, *.scenelib)", new String[] {".patchlib", ".scenelib"});
            fc.setDirectory(appConfig.getLibPath());
            fc.setFilenameFilter(type1);
	    fc.show();
	    if (fc.getFile() == null)
		return;
	    File file = new File(fc.getDirectory(), fc.getFile());
	    openFrame(file);
        }
    }

    public class SaveAction extends AbstractAction {
        public SaveAction(Map mnemonics) {
	    super("Save", null);
	    setEnabled(false);
	    mnemonics.put(this, new Integer('S'));
        }

        public void actionPerformed(ActionEvent e) {
            saveFrame();
        }
    }

    public class ExitAction extends AbstractAction {
        public ExitAction(Map mnemonics) {
	    super("Exit", null);
	    mnemonics.put(this, new Integer('X'));
        }

        public void actionPerformed(ActionEvent e) {
            savePrefs();
            System.exit(0);
        }
    }

    public class ExtractAction extends AbstractAction {
        public ExtractAction(Map mnemonics) {
            super("Extract", null);
            mnemonics.put(this, new Integer('E'));
            setEnabled(false);
        }

        public void actionPerformed(ActionEvent e) {
            try {
		((AbstractLibraryFrame) desktop.getSelectedFrame()).ExtractSelectedPatch();
	    } catch (Exception ex) {
		ErrorMsg.reportError("Error", "Can not Extract (Maybe its not a bank?)", ex);
	    }
        }
    }

    public class SortAction extends AbstractAction {
        public SortAction(Map mnemonics) {
            super("Sort...", null);
            setEnabled(false);
            mnemonics.put(this, new Integer('R'));
        }

        public void actionPerformed(ActionEvent e) {
            try {
		SortDialog sd = new SortDialog(PatchEdit.this);
		sd.show();
	    } catch (Exception ex) {
		ErrorMsg.reportError("Error", "Library to Sort must be Focused", ex);
	    }
        }
    }

    public class SearchAction extends AbstractAction {
        public SearchAction(Map mnemonics) {
            super("Search...", null);
            setEnabled(false);
            mnemonics.put(this, new Integer('E'));
        }

        public void actionPerformed(ActionEvent e) {
            try {
		if (searchDialog == null)
		    searchDialog = new SearchDialog(PatchEdit.this);
		searchDialog.show();
	    } catch (Exception ex) {
		ErrorMsg.reportError("Error", "Library to Sort must be Focused", ex);
	    }
        }
    }

    public class ImportAllAction extends AbstractAction {
        public ImportAllAction(Map mnemonics) {
            super("Import All...", null);
            setEnabled(false);
	    mnemonics.put(this, new Integer('A'));
        }

        public void actionPerformed(ActionEvent e) {
            try {
		CompatibleFileDialog fc = new CompatibleFileDialog();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (appConfig.getLibPath() != null)
		    fc.setSelectedFile(new File(appConfig.getLibPath()));
		fc.showDialog(PatchEdit.this, "Choose Import All Directory");
		File file = fc.getSelectedFile();
		if (file == null)
		    return;

		ImportAllDialog sd = new ImportAllDialog(PatchEdit.this, file);
		sd.show();
	    } catch (Exception ex) {
		ErrorMsg.reportError("Error", "Unable to Import Patches", ex);
	    }
        }
    }

    public class DeleteDuplicatesAction extends AbstractAction {
        public DeleteDuplicatesAction(Map mnemonics) {
            super("Delete Dups...", null);
            setEnabled(false);
            mnemonics.put(this, new Integer('D'));
        }

        public void actionPerformed(ActionEvent e) {
            try {
		if (JOptionPane.showConfirmDialog(null, "This Operation will change the ordering of the Patches. Continue?", "Delete Duplicate Patches", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
		    return;
		PatchEdit.waitDialog.show();
		Collections.sort(((LibraryFrame) desktop.getSelectedFrame()).myModel.PatchList, new SysexSort());
		int numDeleted = 0;
		Patch p, q;
		Iterator it = ((LibraryFrame) desktop.getSelectedFrame()).myModel.PatchList.iterator();
		p = (Patch) it.next();
		while (it.hasNext()) {
		    q = (Patch) it.next();
		    if (Arrays.equals(p.sysex, q.sysex)) {
			it.remove();
			numDeleted++;
		    } else
			p = q;
		}
		JOptionPane.showMessageDialog(null, numDeleted + " Patches were Deleted",
					      "Delete Duplicates", JOptionPane.INFORMATION_MESSAGE);
		((LibraryFrame) desktop.getSelectedFrame()).myModel.fireTableDataChanged();
		((LibraryFrame) desktop.getSelectedFrame()).statusBar.setText
		    (((LibraryFrame) desktop.getSelectedFrame()).myModel.PatchList.size() + " Patches");
	    } catch (Exception ex) {
		ErrorMsg.reportError("Error", "Library to Delete Duplicates in must be Focused", ex);
	    }
            PatchEdit.waitDialog.hide();
        }
    }

    public class NewPatchAction extends AbstractAction {
        public NewPatchAction(Map mnemonics) {
            super("New...", null);
            setEnabled(false);
	    mnemonics.put(this, new Integer('N'));
        }
        public void actionPerformed(ActionEvent e) {
            try {
		Patch p = Clipboard;
		NewPatchDialog np = new NewPatchDialog(PatchEdit.this);
		np.show();
		((PatchBasket) desktop.getSelectedFrame()).PastePatch();
		Clipboard = p;
	    } catch (Exception ex) {
		ErrorMsg.reportError("Error", "Unable to create this new patch.", ex);
	    }
        }
    }

    public class PrefsAction extends AbstractAction {
        public PrefsAction(Map mnemonics) {
            super("Preferences...", null);
	    mnemonics.put(this, new Integer('P'));
        }

        public void actionPerformed(ActionEvent e) {
            prefsDialog.show();
        }
    }

    /*
      noteChooserDialog got replaced by NoteChooserConfigPanel - emenaker 2003.03.17

      class NoteChooserAction extends AbstractAction
      {
      public NoteChooserAction(Map mnemonics)
      {
      super("Choose Note",null);
      mnemonics.put(this, new Integer('C'));
      }

      public void actionPerformed(ActionEvent e)
      {
      noteChooserDialog.show();
      }

      }
    */

    //This is a comparator class used by the delete duplicated action
    //to sort based on the sysex data
    //Sorting this way makes the Dups search much easier, since the
    //dups must be next to each other
    static class SysexSort implements Comparator {
        public int compare(Object a1, Object a2) {
	    String s1 = new String(((Patch) (a1)).sysex);
	    String s2 = new String(((Patch) (a2)).sysex);
	    return s1.compareTo(s2);
        }
    }

    public class CrossBreedAction extends AbstractAction {
        public CrossBreedAction(Map mnemonics) {
            super("Cross Breed...", null);
            setEnabled(false);
            mnemonics.put(this, new Integer('B'));
        }
        public void actionPerformed(ActionEvent e) {
            try {
		CrossBreedDialog cbd = new CrossBreedDialog(PatchEdit.this);
		cbd.show();
	    } catch (Exception ex) {
		ErrorMsg.reportError("Error", "Unable to perform Crossbreed. (No Library selected?)", ex);
	    }
        }
    }

    public class NextFaderAction extends AbstractAction {
        public NextFaderAction(Map mnemonics) {
	    super("Go to Next Fader Bank", null);
	    mnemonics.put(this, new Integer('F'));
        }
        public void actionPerformed(ActionEvent e) {
            if (!(desktop.getSelectedFrame() instanceof PatchEditorFrame))
		return;
            PatchEditorFrame pf = (PatchEditorFrame) desktop.getSelectedFrame();
            pf.faderBank = (pf.faderBank + 1) % pf.numFaderBanks; pf.faderHighlight();
	    return;
        }
    }

    public class DocsAction extends AbstractAction {
        public DocsAction(Map mnemonics) {
	    super("Documentation", null);
	    setEnabled(true);
	    mnemonics.put(this, new Integer('D'));
        }
        public void actionPerformed(ActionEvent e) {
	    try {
		if (documentationWindow == null)
		    documentationWindow = new DocumentationWindow();
		documentationWindow.show();
	    } catch (Exception ex) {
		ErrorMsg.reportError("Error", "Unable to show Documentation)", ex);
	    }
        }
    }

    public class MonitorAction extends AbstractAction {
        public MonitorAction(Map mnemonics) {
	    super("MIDI Monitor", null);
	    setEnabled(true);
	    mnemonics.put(this, new Integer('D'));
        }
        public void actionPerformed(ActionEvent e) {
	    try {
		if (midiMonitor == null)
		    midiMonitor = new MidiMonitor();
		midiMonitor.show();
	    } catch (Exception ex) {
		ErrorMsg.reportError("Error", "Unable to show Midi Monitor)", ex);
	    }
        }
    }

    // This allows icons to be loaded even if they are inside a Jar file
    private ImageIcon loadIcon(String name) {
        Object icon;
        String jarName = null;
        icon = new ImageIcon(name);
        if (((ImageIcon) icon).getIconWidth() == -1) {
	    jarName = new String("/");
	    jarName = jarName.concat(name);
	    try {
		icon = new ImageIcon(this.getClass().getResource(jarName));
	    } catch (java.lang.NullPointerException e) {
		ErrorMsg.reportStatus("ImageIcon:LoadIcon Could not find: " + name);
	    }
	}
        return (ImageIcon) icon;
    }

    protected void beginEcho() {
        echoTimer = new javax.swing.Timer(5, new ActionListener() {
		byte[] buffer = new byte[128];
		Patch p;
		public void actionPerformed(ActionEvent evt) {
		    try {
			//FIXME there is a bug in the javaMIDI classes
			//so this routine gets the input messages from
			//the faderbox as well as the master
			//controller. I cant figure out a way to fix
			//it so let's just handle them here.
			if (appConfig.getMasterController() > -1
			    && appConfig.getMasterController() < MidiIn.getNumInputDevices()) {
			    // '&' should be '&&' ? Hiroo !!!
			    if ((appConfig.getFaderEnable())
				& (PatchEdit.MidiIn.messagesWaiting(appConfig.getMasterController()) > 0)) {
				for (int i = 0; i < 33; i++)
				    newFaderValue[i] = 255;
				while (PatchEdit.MidiIn.messagesWaiting(appConfig.getMasterController()) > 0) {
				    int size;
				    int port;
				    size = PatchEdit.MidiIn.readMessage(appConfig.getMasterController(), buffer, 128);
				    p = PatchEdit.Clipboard;
				    if ((desktop.getSelectedFrame() instanceof PatchBasket)
					&& (!(desktop.getSelectedFrame() instanceof PatchEditorFrame))) {
					//!!!
					if ((desktop.getSelectedFrame() instanceof LibraryFrame)
					    & ((LibraryFrame) ((desktop.getSelectedFrame()))).table.getSelectedRowCount() == 0)
					    break;
					((PatchBasket) desktop.getSelectedFrame()).CopySelectedPatch();
				    } else
					Clipboard = ((PatchEditorFrame) desktop.getSelectedFrame()).p;
				    //port = (PatchEdit.deviceList.get(Clipboard.deviceNum)).
				    port = appConfig.getDevice(Clipboard.deviceNum).getPort();
				    //!!!
				    if ((appConfig.getFaderEnable())
					& (desktop.getSelectedFrame() instanceof PatchEditorFrame)
					&& (buffer[0] & 0xF0) == 0xB0)
					sendFaderMessage(buffer[0], buffer[1], buffer[2]);
				    else
					if ((buffer[0] & 0xF0) == 0xD0) {
					    //do nothing for now
					} else {
					    if (((buffer[0] & 0xF0) > 0x70)
						&& ((buffer[0] & 0xF0) < 0xF0))
						buffer[0] = (byte) ((buffer[0] & 0xF0) + appConfig.getDevice(Clipboard.deviceNum).getChannel() - 1);
					    PatchEdit.MidiOut.writeLongMessage(port, buffer, size);
					}
				    PatchEdit.Clipboard = p;
				}
			    }
			}
		    } catch (Exception ex) {
			ErrorMsg.reportStatus(ex);
		    }
		}
	    });
        echoTimer.start();
    }

    void sendFaderMessage(byte status, byte controller, byte value) {
        byte channel = (byte) (status & 0x0F);
        byte i = 0;
        while (i < 33) {
	    // & ??? !!!!
	    if ((appConfig.getFaderController(i) == controller)
		& (appConfig.getFaderChannel(i) == channel)) {
		((PatchEditorFrame) desktop.getSelectedFrame()).faderMoved(i, value);
		break;
	    }
	    i++;
	}
    }

    public static Driver getDriver(int deviceNumber, int driverNumber) {
        return (Driver) appConfig.getDevice(deviceNumber).driverList.get(driverNumber);
    }
}
