/**
 * This is the main JSynthLib application object.  It's called
 * PatchEdit, which is probably ambiguous, but probably to late to
 * change now.
 * @version $Id$
 */

package core;
import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.util.*;
import java.awt.event.*;
import java.awt.*;
import java.io.*;
import javax.sound.midi.*;
import java.awt.datatransfer.*;
import javax.swing.event.*;
import java.net.URL;

import com.apple.eawt.ApplicationAdapter;
import com.apple.eawt.ApplicationEvent;

//TODO import /*TODO org.jsynthlib.*/midi.*;
public final class PatchEdit /*implements MidiDriverChangeListener*/ {
    static final String VERSION = "0.20-alpha";

    static DevicesConfig devConfig;
    static AppConfig appConfig;

    // accessed by BankEditorFrame
    static ExtractAction extractAction;
    static SendAction sendAction;
    static SendToAction sendToAction;
    static StoreAction storeAction;
    static ReassignAction reassignAction;
    static EditAction editAction;
    static PlayAction playAction;
    static GetAction receiveAction;
    static SaveAction saveAction;
    static SaveAsAction saveAsAction;
    static SortAction sortAction;
    static SearchAction searchAction;
    static DeleteDuplicatesAction dupAction;
    static CopyAction copyAction;
    static CutAction cutAction;
    static PasteAction pasteAction;
    static DeleteAction deleteAction;
    static ImportAction importAction;
    static ExportAction exportAction;
    static ImportAllAction importAllAction;
    static NewPatchAction newPatchAction;
    static CrossBreedAction crossBreedAction;

    static MonitorAction monitorAction;
    static TransferSceneAction transferSceneAction;
    static SynthAction synthAction;
    static PrefsAction prefsAction;
    static ExitAction exitAction;
    static UploadAction uploadAction;

    private static DocsAction docsAction;
    private static LicenseAction licenseAction;
    private static HomePageAction homePageAction;
    private static NewSceneAction newSceneAction;
    private static NextFaderAction nextFaderAction;
    private static NewAction newAction;
    private static OpenAction openAction;
    private static AboutAction aboutAction;

    private static MidiMonitor midiMonitor;
    private static JToolBar toolBar;
    private static PrefsDialog prefsDialog;
    private static SearchDialog searchDialog;
    private static WaitDialog waitDialog;
    private static DocumentationWindow docWin;
    private static DocumentationWindow licWin;
    private static DocumentationWindow hpWin;
    private static JPopupMenu menuPatchPopup;

    private static Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();

    /** Initialize Application: */
    public PatchEdit() {
	// Load synth database (synthdrivers.properties)
	devConfig = new DevicesConfig();

	// Load config file (JSynthLib.properties).
	appConfig = new AppConfig();
        boolean loadPrefsSuccessfull = appConfig.loadPrefs();

	// define event actions
	createActions();

	// Set up the GUI.
	JSLDesktop.getInstance();
	JSLDesktop.setupInitialMenuBar(createToolBar());
	if (MacUtils.isMac())
	    initForMac(exitAction, prefsAction, aboutAction);

	// Show dialog for the 1st invokation.
	//This is no longer normal. Maybe we shouldn't save prefs if this happens (could be difficult)
        if (!loadPrefsSuccessfull)
            ErrorMsg.reportError
		("Error",
		 "Unable to load user preferences. Defaults loaded instead.");

	// popup menu for Library window, etc.
	menuPatchPopup = createPopupMenu();

	// set up Preference Dialog Window
	prefsDialog = initPrefsDialog();

        //Set up a silly little dialog we can pop up for the user to
        //gawk at while we do time consuming work later on.
        waitDialog = new WaitDialog(JSLDesktop.getSelectedWindow());

        // Start pumping MIDI information from Input --> Output so the
        // user can play a MIDI Keyboard and make pretty music
	masterInEnable(appConfig.getMasterInEnable());
    }

    // PatchEdit.getInstance() can/should be replaced by
    // JSLDesktop.getSelectedWindow(). Hiroo
    public static JFrame getInstance() {
	return JSLDesktop.getSelectedWindow();
    }

    /**
     * Setup preference dialog window.
     */
    private static PrefsDialog initPrefsDialog() {
        PrefsDialog prefsDialog = new PrefsDialog(JSLDesktop.getSelectedWindow());
	// Add the configuration panels to the prefsDialog
        prefsDialog.addPanel(new GeneralConfigPanel(appConfig));
	prefsDialog.addPanel(new DirectoryConfigPanel(appConfig));

	MidiConfigPanel midiConfigPanel = null;
	midiConfigPanel = new MidiConfigPanel(appConfig);
	//midiConfigPanel.addDriverChangeListener(this);
	prefsDialog.addPanel(midiConfigPanel);

	// FaderBoxConfigPanel() have to be called after MidiIn is initialized.
	FaderBoxConfigPanel faderbox = new FaderBoxConfigPanel(appConfig);
	prefsDialog.addPanel(faderbox);

	prefsDialog.addPanel(new NoteChooserConfigPanel(appConfig));
	prefsDialog.addPanel(new RepositoryConfigPanel(appConfig));
	// Create preference dialog window and initialize each config
	// panel.
	prefsDialog.init();
	return prefsDialog;
    }

    private static void createActions() {
        HashMap mnemonics = new HashMap();

        newAction		= new NewAction(mnemonics);
        openAction		= new OpenAction(mnemonics);
        saveAction		= new SaveAction(mnemonics);
        saveAsAction		= new SaveAsAction(mnemonics);
        newSceneAction		= new NewSceneAction(mnemonics);
        transferSceneAction	= new TransferSceneAction(mnemonics);
        sortAction		= new SortAction(mnemonics);
        searchAction		= new SearchAction(mnemonics);
        dupAction		= new DeleteDuplicatesAction(mnemonics);
        exitAction		= new ExitAction(mnemonics);

        copyAction		= new CopyAction(mnemonics);
        cutAction		= new CutAction(mnemonics);
        pasteAction		= new PasteAction(mnemonics);
        deleteAction		= new DeleteAction(mnemonics);
        importAction		= new ImportAction(mnemonics);
        exportAction		= new ExportAction(mnemonics);
        importAllAction		= new ImportAllAction(mnemonics);
        sendAction		= new SendAction(mnemonics);
        sendToAction		= new SendToAction(mnemonics);
        storeAction		= new StoreAction(mnemonics);
        receiveAction		= new GetAction(mnemonics);

        playAction		= new PlayAction(mnemonics);
        editAction		= new EditAction(mnemonics);
        reassignAction		= new ReassignAction(mnemonics);
        crossBreedAction	= new CrossBreedAction(mnemonics);
        newPatchAction		= new NewPatchAction(mnemonics);
        extractAction		= new ExtractAction(mnemonics);

	prefsAction		= new PrefsAction(mnemonics);
	synthAction		= new SynthAction(mnemonics);
	monitorAction		= new MonitorAction(mnemonics);

        aboutAction		= new AboutAction(mnemonics);
        docsAction		= new DocsAction(mnemonics);
        licenseAction		= new LicenseAction(mnemonics);
        homePageAction		= new HomePageAction(mnemonics);

        nextFaderAction		= new NextFaderAction(mnemonics);
	uploadAction		= new UploadAction(mnemonics);

	// set keyboard short cut
	if (!MacUtils.isMac())
	    setMnemonics(mnemonics);
    }

    /** This sets up the Menubar. Called from JSLDesktop. */
    static JMenuBar createMenuBar() {
	JMenuItem mi;
        HashMap mnemonics = new HashMap();
        int mask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
        JMenuBar menuBar = new JMenuBar();

	// create "File" Menu
        JMenu menuFile = new JMenu("File");
	mnemonics.put(menuFile, new Integer(KeyEvent.VK_F));
        mi = menuFile.add(openAction);
        mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, mask));
        mi = menuFile.add(saveAction);
        mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, mask));
        menuFile.add(saveAsAction);

        menuFile.addSeparator();
        menuFile.add(importAction);
        menuFile.add(exportAction);
        menuFile.add(importAllAction);

        if (!MacUtils.isMac()) {
	    menuFile.addSeparator();
	    menuFile.add(exitAction);
	}
	menuBar.add(menuFile);

	// create "Library" Menu
        JMenu menuLib = new JMenu("Library");
	mnemonics.put(menuLib, new Integer(KeyEvent.VK_L));

        menuLib.add(transferSceneAction);
        menuLib.addSeparator();

        menuLib.add(sortAction);
        menuLib.add(searchAction);
        menuLib.add(crossBreedAction);
        menuLib.add(dupAction);
        menuLib.addSeparator();

	mi = menuLib.add(newAction);
	mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, mask));
        menuLib.add(newSceneAction);
	menuBar.add(menuLib);

	// create "Patch" Menu
        JMenu menuPatch = new JMenu("Patch");
	mnemonics.put(menuPatch, new Integer(KeyEvent.VK_P));
        mi = menuPatch.add(copyAction);
        mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_COPY, 0));
        mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, mask));
        mi = menuPatch.add(cutAction);
        mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_CUT, 0));
        mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, mask));
        mi = menuPatch.add(pasteAction);
        mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PASTE, 0));
        mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, mask));
        mi = menuPatch.add(deleteAction);
        mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        menuPatch.addSeparator();

        mi = menuPatch.add(sendAction);
        mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, mask));
        menuPatch.add(sendToAction);
        menuPatch.add(storeAction);
        menuPatch.add(receiveAction); // phil@muqus.com
        menuPatch.addSeparator();

        mi = menuPatch.add(editAction);
        mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, mask));
        mi = menuPatch.add(playAction);
        mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, mask));
        menuPatch.addSeparator();

 	menuPatch.add(reassignAction);
        menuPatch.add(extractAction);
        menuPatch.addSeparator();

        menuPatch.add(newPatchAction);
	menuPatch.add(uploadAction);
        menuBar.add(menuPatch);
	menuPatch.addMenuListener(new MenuListener() {
		public void menuCanceled(MenuEvent e) {
		}
		public void menuDeselected(MenuEvent e) {
		}
		public void menuSelected(MenuEvent e) {
		    pasteAction.enable();
		}
	    });

	// create "Window" menu
	JMenu menuWindow = JSLDesktop.createWindowMenu();
	menuBar.add(menuWindow);

	// create "Help" menu
        JMenu menuHelp = new JMenu("Help");
	mnemonics.put(menuHelp, new Integer(KeyEvent.VK_H));
	mi = menuHelp.add(docsAction);
        mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_HELP, 0));
	// J2SE 1.4.2 is not ready for HTML of www.jsynthlib.org site
	//mi = menuHelp.add(homePageAction);
	mi = menuHelp.add(licenseAction);
	if (!MacUtils.isMac())
	    menuHelp.add(aboutAction);
        menuBar.add(menuHelp);

	// set keyboard short cut
	if (!MacUtils.isMac())
	    setMnemonics(mnemonics);

        return menuBar;
    }

    /** This sets up the mnemonics */
    private static void setMnemonics(Map mnemonics) {
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

    private static JPopupMenu createPopupMenu() {
	// crate popup menu
        JPopupMenu popup = new JPopupMenu();
        popup.add(playAction);
        popup.add(editAction);
        popup.addSeparator();

        popup.add(reassignAction);
        popup.add(storeAction);
        popup.add(sendAction);
        popup.add(sendToAction);
        popup.addSeparator();

        popup.add(cutAction);
        popup.add(copyAction);
        popup.add(pasteAction);
	popup.addSeparator();
	popup.add(uploadAction);
	return popup;
    }

    /** show popup menu for patch. */
    public static void showMenuPatchPopup(JTable tbl, int x, int y) {
	menuPatchPopup.show(tbl, x, y);
    }

    private static JToolBar createToolBar() {
	// create tool bar
        toolBar = new JToolBar();
        toolBar.setPreferredSize(new Dimension(500, 35));
        toolBar.setFloatable(true);

        toolBar.add(createToolBarButton(newAction, "New", "New Library"));
        toolBar.add(createToolBarButton(openAction, "Open", "Open Library"));
	toolBar.add(createToolBarButton(saveAction, "Save", "Save Library"));

        toolBar.addSeparator();

        toolBar.add(createToolBarButton(copyAction, "Copy", "Copy Patch"));
        toolBar.add(createToolBarButton(cutAction, "Cut", "Cut Patch"));
        toolBar.add(createToolBarButton(pasteAction, "Paste", "Paste Patch"));
        toolBar.add(createToolBarButton(importAction, "Import", "Import Patch"));
	toolBar.add(createToolBarButton(exportAction, "Export", "Export Patch"));

        toolBar.addSeparator();

        toolBar.add(createToolBarButton(playAction, "Play", "Play Patch"));
        toolBar.add(createToolBarButton(storeAction, "Store", "Store Patch"));
        toolBar.add(createToolBarButton(editAction, "Edit", "Edit Patch"));

        toolBar.addSeparator();

        toolBar.add(createToolBarButton(nextFaderAction, "Next", "Go to Next Fader Bank"));

        return toolBar;
    }

    private static JButton createToolBarButton(Action a, String label, String tooltip) {
	String label2 = label.toLowerCase();
	URL u1 = PatchEdit.class.getResource("/images/" + label2 + ".png");
	URL u2 = PatchEdit.class.getResource("/images/disabled-" + label2 + ".png");
	//Create and initialize the button.
	JButton button = new JButton(a);
	button.setToolTipText(tooltip);

	if (u1 != null) {                      //image found
	    button.setText(null);
	    button.setIcon(new ImageIcon(u1, label));
	    if (u2 != null) {
		button.setDisabledIcon(new ImageIcon(u2, label));
	    }
	} else {                                     //no image found
	    button.setText(label);
	    System.err.println("Resource not found: " + "images/" + label + ".png");
	}
	return button;
    }

    private static void initForMac(final ExitAction exitAction,
				   final PrefsAction prefsAction,
				   final AboutAction aboutAction) {
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
				    ErrorMsg.reportStatus(e);
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
				    ErrorMsg.reportStatus(e);
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
				    ErrorMsg.reportStatus(e);
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
    }

    /** This creates a new [empty] Library Window */
    private static void createLibraryFrame() {
        LibraryFrame frame = new LibraryFrame();
        frame.setVisible(true);
        JSLDesktop.add(frame);
        try {
	    frame.setSelected(true);
	} catch (java.beans.PropertyVetoException e) {
	    ErrorMsg.reportStatus(e);
	}
    }

    private static void createSceneFrame() {
        SceneFrame frame = new SceneFrame();
        frame.setVisible(true);
        JSLDesktop.add(frame);
        try {
	    frame.setSelected(true);
	} catch (java.beans.PropertyVetoException e) {
	    //I don't *actually* know what this is for :-)
	    ErrorMsg.reportStatus(e);
	}
    }

    /** Create a new Library Window and load a Library from disk to
	fill it! Fun! */
    private static void openFrame(File file) {
        LibraryFrame frame = new LibraryFrame(file);
        try {
	    // Does this need to be before open?
	    frame.setVisible(true);
	    frame.open(file);
	    JSLDesktop.add(frame);
	} catch (Exception e) {
	    frame.dispose();
	    SceneFrame frame2 = new SceneFrame(file);
	    try {
		frame2.setVisible(true);
		frame2.open(file);
		JSLDesktop.add(frame2);
	    } catch (Exception e2) {
		frame2.dispose();
		ErrorMsg.reportError("Error", "Error Loading Library", e2);
		return;
	    }
	    try {
		frame2.setSelected(true);
	    } catch (java.beans.PropertyVetoException e2) {
		ErrorMsg.reportStatus(e2);
	    }
	}
        try {
	    frame.setSelected(true);
	} catch (java.beans.PropertyVetoException e) {
	    ErrorMsg.reportStatus(e);
	}
    }

    /** This one saves a Library to Disk */
    static void saveFrame() {
	File fn = null;
	try {
	    JSLFrame oFrame = JSLDesktop.getSelectedFrame();
	    if (oFrame.getTitle().startsWith("Unsaved ")) {
		fn = showSaveDialog();
		if (fn == null)
		    return;
	    }
	    if (oFrame instanceof LibraryFrame) {
		saveFrame((LibraryFrame) oFrame, fn);
	    } else if (oFrame instanceof SceneFrame) {
		saveFrame((SceneFrame) oFrame, fn);
	    }
	} catch (Exception e) {
	    ErrorMsg.reportError("Error", "Unable to Save Library", e);
	    return;
	}
    }

    private static File showSaveDialog() {
	CompatibleFileDialog fc2 = new CompatibleFileDialog();
        FileFilter type1 =
	    new ExtensionFilter("PatchEdit Library Files (*.patchlib)",
				".patchlib");
        fc2.addChoosableFileFilter(type1);
        fc2.setFileFilter(type1);
        fc2.setCurrentDirectory(new File (appConfig.getLibPath()));
        if (fc2.showSaveDialog(PatchEdit.getInstance())
	        != JFileChooser.APPROVE_OPTION)
            return null;
        File file = fc2.getSelectedFile();

	if (!file.getName().toUpperCase().endsWith(".PATCHLIB"))
	    file = new File(file.getPath() + ".patchlib");
	if (file.isDirectory()) {
	    ErrorMsg.reportError("Error", "Can not Save over a Directory");
	    return null;
	}
	if (file.exists())
	    if (JOptionPane.showConfirmDialog(null, "Are you sure?",
		    "File Exists", JOptionPane.YES_NO_OPTION)
		    == JOptionPane.NO_OPTION)
		return null;
	return file;
    }

    /** Save and specify a file name */
    private static void saveFrameAs() {
	try {
	    JSLFrame oFrame = JSLDesktop.getSelectedFrame();
	    File fn = showSaveDialog();
	    if (fn == null)
		return;
	    if (oFrame instanceof LibraryFrame) {
		saveFrame((LibraryFrame) oFrame, fn);
	    } else if (oFrame instanceof SceneFrame) {
		saveFrame((SceneFrame) oFrame, fn);
	    }
	} catch (Exception ex) {
	    ErrorMsg.reportError("Error", "Unable to Save Library", ex);
	    return;
	}
    }

    private static void saveFrame(LibraryFrame lf, File file) throws Exception {
	if (file == null)
	    lf.save();
	else
	    lf.save(file);
    }

    private static void saveFrame(SceneFrame sf, File file) throws Exception {
	if (file == null)
	    sf.save();
	else
	    sf.save(file);
    }

    // Generally the app is started by running JSynthLib, so the
    // following few lines are not necessary, but I won't delete them
    // just yet.
    /*
    public static void main(String[] args) {
        PatchEdit frame = new PatchEdit();
        //frame.setVisible(true);
    }
    */

    /** @deprecated Don't use this. */
    public static Driver getDriver(int deviceNumber, int driverNumber) {
	if (appConfig == null)
	    return null;
        return appConfig.getDevice(deviceNumber).getDriver(driverNumber);
    }

    /**
     * Output string to MIDI Monitor Window.  Use MidiUtil.log()
     * instead of this.
     *
     * @param s string to be output
     */
    static void midiMonitorLog(String s) {
	if (midiMonitor != null && midiMonitor.isVisible())
	    midiMonitor.log(s);
    }

    ////////////////////////////////////////////////////////////////////////
    /*
     * Now we start with the various action classes. Each of these
     * preforms one of the menu commands and are called either from
     * the menubar, popup menu or toolbar.
     */
    static class AboutAction extends AbstractAction {
	public AboutAction(Map mnemonics) {
	    super("About");
	    mnemonics.put(this, new Integer('A'));
	}

	public void actionPerformed(ActionEvent e) {
	    JOptionPane.showMessageDialog
		(null,
		 "JSynthLib Version " + VERSION
		 + "\nCopyright (C) 2000-04 Brian Klock et al.\n"
		 + "See 'Help -> License' for more info.",
		 "About JSynthLib", JOptionPane.INFORMATION_MESSAGE);
	    return;
	}
    }

    static class ReassignAction extends AbstractAction {
	public ReassignAction(Map mnemonics) {
	    super("Reassign...", null); // show a dialog frame???
	    // mnemonics.put(this, new Integer('R'));
	    setEnabled(false);
	}
	public void actionPerformed(ActionEvent e) {
	    try {
		((PatchBasket) JSLDesktop.getSelectedFrame()).ReassignSelectedPatch();
	    } catch (Exception ex) {
		ErrorMsg.reportError("Error", "Patch to Reassign must be highlighted in the focused Window.", ex);
	    }
	}
    }

    static class PlayAction extends AbstractAction {
        public PlayAction(Map mnemonics) {
            super("Play", null);
            mnemonics.put(this, new Integer('P'));
            setEnabled(false);
        }
        public void actionPerformed(ActionEvent e) {
            try {
		((PatchBasket) JSLDesktop.getSelectedFrame()).PlaySelectedPatch();
	    } catch (Exception ex) {
		ErrorMsg.reportError("Error", "Patch to Play must be highlighted in the focused Window.", ex);
	    }
        }
    }

    static class StoreAction extends AbstractAction {
        public StoreAction(Map mnemonics) {
            super("Store...", null);
            mnemonics.put(this, new Integer('R'));
            setEnabled(false);
        }
        public void actionPerformed(ActionEvent e) {
            try {
		((PatchBasket) JSLDesktop.getSelectedFrame()).StoreSelectedPatch();
	    } catch (Exception ex) {
		ErrorMsg.reportError("Error", "Patch to Store must be highlighted in the focused Window.", ex);
	    }
        }
    }

    static class SendAction extends AbstractAction {
        public SendAction(Map mnemonics) {
            super("Send", null);
	    mnemonics.put(this, new Integer('S'));
            setEnabled(false);
        }
        public void actionPerformed(ActionEvent e) {
            try {
		((PatchBasket) JSLDesktop.getSelectedFrame()).SendSelectedPatch();
	    } catch (Exception ex) {
		ErrorMsg.reportError("Error", "Patch to Send must be highlighted in the focused Window.", ex);
	    }
        }
    }

    static class SendToAction extends AbstractAction {
	public SendToAction(Map mnemonics) {
	    super("Send to...", null);
	    // mnemonics.put(this, new Integer('S'));
	    setEnabled(false);
	}
	public void actionPerformed(ActionEvent e) {
	    try {
		((PatchBasket) JSLDesktop.getSelectedFrame()).SendToSelectedPatch();
	    } catch (Exception ex) {
		ErrorMsg.reportError("Error", "Patch to 'Send to...' must be highlighted in the focused Window.", ex);
	    }
	}
    }

    static class DeleteAction extends AbstractAction {
        public DeleteAction(Map mnemonics) {
            super("Delete", null);
            setEnabled(false);
	    mnemonics.put(this, new Integer('D'));
        }

        public void actionPerformed(ActionEvent e) {
            try {
		((PatchBasket) JSLDesktop.getSelectedFrame()).DeleteSelectedPatch();
	    } catch (Exception ex) {
		ErrorMsg.reportError("Error", "Patch to delete must be hilighted\nin the focused Window.", ex);
	    }
        }
    }

    static class CopyAction extends AbstractAction {
        public CopyAction(Map mnemonics) {
            super("Copy", null);
            setEnabled(false);
	    mnemonics.put(this, new Integer('C'));
        }
        public void actionPerformed(ActionEvent e) {
            try {
		((PatchBasket) JSLDesktop.getSelectedFrame()).CopySelectedPatch();
	    } catch (Exception ex) {
		ErrorMsg.reportError("Error", "Patch to copy must be highlighted\nin the focused Window.", ex);
	    }
        }
    }

    static class CutAction extends AbstractAction {
        public CutAction(Map mnemonics) {
            super("Cut", null);
            setEnabled(false);
	    mnemonics.put(this, new Integer('T'));
        }
        public void actionPerformed(ActionEvent e) {
            try {
		((PatchBasket) JSLDesktop.getSelectedFrame()).CopySelectedPatch();
		((PatchBasket) JSLDesktop.getSelectedFrame()).DeleteSelectedPatch();
	    } catch (Exception ex) {
		ErrorMsg.reportError("Error", "Patch to cut must be hilighted\nin the focused Window.", ex);
	    }
        }
    }

    static class PasteAction extends AbstractAction {
        public PasteAction(Map mnemonics) {
            super("Paste", null);
            setEnabled(false);
	    mnemonics.put(this, new Integer('P'));
        }
        public void actionPerformed(ActionEvent e) {
            try {
		((PatchBasket) JSLDesktop.getSelectedFrame()).PastePatch();
	    } catch (Exception ex) {
		ErrorMsg.reportError("Error", "Library to Paste into must be the focused Window.", ex);
	    }
        }
	public void enable() {
	    try {
		JSLFrame f = JSLDesktop.getSelectedFrame();
		boolean b = f.canImport(c.getContents(this)
					.getTransferDataFlavors());
		setEnabled(b);
	    } catch (Exception ex) {
		setEnabled(false);
	    }
	}
    }

    static class EditAction extends AbstractAction {
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
		    JSLFrame frm =
			((PatchBasket) JSLDesktop.getSelectedFrame()).EditSelectedPatch();
		    if (frm != null) {
			frm.setVisible(true);
			JSLDesktop.add(frm);
			// hack for old Java bug
			/*
			if (frm instanceof PatchEditorFrame)
			    for (int i = 0; i < ((PatchEditorFrame) frm).sliderList.size(); i++) {
				JSlider slider = (JSlider) ((PatchEditorFrame) frm).sliderList.get(i);
				Dimension dim = slider.getSize();
				if (dim.width > 0) {
				    dim.width++;
				    slider.setSize(dim);
				}
			    }
			*/
			try {
			    frm.setSelected(true);
			} catch (java.beans.PropertyVetoException e) {
			    ErrorMsg.reportStatus(e);
			}
		    }
		} catch (Exception ex) {
		    // For which Exception is this message for? !!!FIXIT!!!
		    //ErrorMsg.reportError("Error", "Library holding Patch to Edit must be the focused Window.", ex);
		    ErrorMsg.reportError("Error",
					 "Error in PatchEditor.", ex);
		}
	    }
	}
    }

    static class ExportAction extends AbstractAction {
        public ExportAction(Map mnemonics) {
            super("Export...", null);
            mnemonics.put(this, new Integer('O'));
            setEnabled(false);
        }
        public void actionPerformed(ActionEvent e) {
            CompatibleFileDialog fc3 = new CompatibleFileDialog();
            FileFilter type1 = new ExtensionFilter("Sysex Files (*.syx)", ".syx");
            fc3.addChoosableFileFilter(type1);
            fc3.setFileFilter(type1);
            fc3.setCurrentDirectory(new File (appConfig.getSysexPath()));
            if (fc3.showSaveDialog(PatchEdit.getInstance()) != JFileChooser.APPROVE_OPTION)
		return;
	    File file = fc3.getSelectedFile();
	    try {
		if (JSLDesktop.getSelectedFrame() == null) {
		    ErrorMsg.reportError("Error",
					 "Patch to export must be hilighted\n"
					 + "in the currently focuses Library");
		} else {
		    if (!file.getName().toUpperCase().endsWith(".SYX"))
			file = new File(file.getPath() + ".syx");
		    if (file.exists())
			if (JOptionPane.showConfirmDialog(null,
							  "Are you sure?",
							  "File Exists",
							  JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
			    return;

		    ((PatchBasket) JSLDesktop.getSelectedFrame()).ExportPatch(file);
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

    static class GetAction extends AbstractAction {
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
	    SysexGetDialog myDialog = new SysexGetDialog(PatchEdit.getInstance());
	    myDialog.show();
	}

    } // End SubClass: GetAction
    //------ End phil@muqus.com

    static class UploadAction extends AbstractAction {
	public UploadAction(Map mnemonics) {
	    super("Upload...", null);
	    mnemonics.put(this, new Integer('U'));
	    setEnabled(false);
	}

	public void actionPerformed(ActionEvent e) {
	    UploadPatchDialog myDialog = new UploadPatchDialog(PatchEdit.getInstance());
	    myDialog.show();
	}

    }

    // denis: all Action classes made public
    static class ImportAction extends AbstractAction {
        public ImportAction(Map mnemonics) {
            super("Import...", null);
            mnemonics.put(this, new Integer('I'));
            setEnabled(false);
        }
        public void actionPerformed(ActionEvent e) {
            CompatibleFileDialog fc2 = new CompatibleFileDialog();
            FileFilter type1 = new ExtensionFilter("Sysex Files (*.syx)", ".syx");
	    // core.ImportMidiFile extracts Sysex Messages from MidiFile
            FileFilter type2 = new ExtensionFilter("MIDI Files (*.mid)" , ".mid");
            fc2.addChoosableFileFilter(type1);
            fc2.addChoosableFileFilter(type2);
            fc2.setFileFilter(type1);
            fc2.setCurrentDirectory(new File(appConfig.getSysexPath()));
            if (fc2.showOpenDialog(PatchEdit.getInstance()) != JFileChooser.APPROVE_OPTION)
		return;
	    File file = fc2.getSelectedFile();
	    try {
		if (JSLDesktop.getSelectedFrame() == null) {
		    ErrorMsg.reportError("Error", "Library to Import Patch\n into Must be in Focus");
		} else
		    ((PatchBasket) JSLDesktop.getSelectedFrame()).ImportPatch(file);
	    } catch (IOException ex) {
		ErrorMsg.reportError("Error", "Unable to Load Sysex Data", ex);
	    }
	}
    }

    static class NewAction extends AbstractAction {
        public NewAction(Map mnemonics) {
	    super("New Library", null);
	    mnemonics.put(this, new Integer('N'));
        }
        public void actionPerformed(ActionEvent e) {
	    createLibraryFrame();
	}
    }

    static class NewSceneAction extends AbstractAction {
        public NewSceneAction(Map mnemonics) {
            super("New Scene", null);
        }

        public void actionPerformed(ActionEvent e) {
            createSceneFrame();
        }
    }

    static class TransferSceneAction extends AbstractAction {
        public TransferSceneAction(Map mnemonics) {
            super("Transfer Scene", null); // show a dialog frame???
            // mnemonics.put(this, new Integer('S'));
            setEnabled(false);
        }
        public void actionPerformed(ActionEvent e) {
            try {
		((SceneFrame) JSLDesktop.getSelectedFrame()).sendScene();
	    } catch (Exception ex) {
		ErrorMsg.reportError("Error", "Scene Library must be the selected window.", ex);
	    }
        }
    }

    static class OpenAction extends AbstractAction {
        public OpenAction(Map mnemonics) {
            super("Open...", null);
            mnemonics.put(this, new Integer('O'));
        }
        public void actionPerformed(ActionEvent e) {
            CompatibleFileDialog fc = new CompatibleFileDialog();
            FileFilter type1 = new ExtensionFilter("PatchEdit Library Files (*.patchlib, *.scenelib)",
						   new String[] {".patchlib", ".scenelib"});
            fc.addChoosableFileFilter(type1);
            fc.setFileFilter(type1);
            fc.setCurrentDirectory(new File(appConfig.getLibPath()));
            if (fc.showOpenDialog(PatchEdit.getInstance()) != JFileChooser.APPROVE_OPTION)
		return;
	    File file = fc.getSelectedFile();
	    openFrame(file);
        }
    }

    static class SaveAction extends AbstractAction {
        public SaveAction(Map mnemonics) {
	    super("Save", null);
	    setEnabled(false);
	    mnemonics.put(this, new Integer('S'));
        }

        public void actionPerformed(ActionEvent e) {
            saveFrame();
        }
    }

    static class SaveAsAction extends AbstractAction {
        public SaveAsAction(Map mnemonics) {
	    super("Save As...", null);
	    setEnabled(false);
	    mnemonics.put(this, new Integer('A'));
        }

        public void actionPerformed(ActionEvent e) {
            saveFrameAs();
        }
    }

    static class ExitAction extends AbstractAction {
        public ExitAction(Map mnemonics) {
	    super("Exit", null);
	    mnemonics.put(this, new Integer('X'));
        }

        public void actionPerformed(ActionEvent e) {
	    exit();
        }
	public void exit() {
            appConfig.savePrefs();
            System.exit(0);
	}
    }

    static class ExtractAction extends AbstractAction {
        public ExtractAction(Map mnemonics) {
            super("Extract", null);
            mnemonics.put(this, new Integer('E'));
            setEnabled(false);
        }

        public void actionPerformed(ActionEvent e) {
            try {
		((AbstractLibraryFrame) JSLDesktop.getSelectedFrame()).ExtractSelectedPatch();
	    } catch (Exception ex) {
		ErrorMsg.reportError("Error", "Can not Extract (Maybe its not a bank?)", ex);
	    }
        }
    }

    static class SortAction extends AbstractAction {
        public SortAction(Map mnemonics) {
            super("Sort...", null);
            setEnabled(false);
            mnemonics.put(this, new Integer('R'));
        }

        public void actionPerformed(ActionEvent e) {
            try {
		SortDialog sd = new SortDialog(PatchEdit.getInstance());
		sd.show();
	    } catch (Exception ex) {
		ErrorMsg.reportError("Error", "Library to Sort must be Focused", ex);
	    }
        }
    }

    static class SearchAction extends AbstractAction {
        public SearchAction(Map mnemonics) {
            super("Search...", null);
            setEnabled(false);
            mnemonics.put(this, new Integer('E'));
        }

        public void actionPerformed(ActionEvent e) {
            try {
		if (searchDialog == null)
		    searchDialog = new SearchDialog(PatchEdit.getInstance());
		searchDialog.show();
	    } catch (Exception ex) {
		ErrorMsg.reportError("Error", "Library to Sort must be Focused", ex);
	    }
        }
    }

    static class ImportAllAction extends AbstractAction {
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
		if (fc.showDialog(PatchEdit.getInstance(),
				  "Choose Import All Directory") != JFileChooser.APPROVE_OPTION)
		    return;
		File file = fc.getSelectedFile();

		ImportAllDialog sd = new ImportAllDialog(PatchEdit.getInstance(), file);
		sd.show();
	    } catch (Exception ex) {
		ErrorMsg.reportError("Error", "Unable to Import Patches", ex);
	    }
        }
    }

    static class DeleteDuplicatesAction extends AbstractAction {
        public DeleteDuplicatesAction(Map mnemonics) {
            super("Delete Dups...", null);
            setEnabled(false);
            mnemonics.put(this, new Integer('D'));
        }

        public void actionPerformed(ActionEvent e) {
            try {
		if (JOptionPane.showConfirmDialog(null,
						  "This Operation will change the ordering of the Patches. Continue?",
						  "Delete Duplicate Patches",
						  JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
		    return;
		PatchEdit.waitDialog.show();
		Collections.sort(((LibraryFrame) JSLDesktop.getSelectedFrame()).myModel.PatchList, new SysexSort());
		int numDeleted = 0;
		Patch p, q;
		Iterator it = ((LibraryFrame) JSLDesktop.getSelectedFrame()).myModel.PatchList.iterator();
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
		((LibraryFrame) JSLDesktop.getSelectedFrame()).myModel.fireTableDataChanged();
		((LibraryFrame) JSLDesktop.getSelectedFrame()).statusBar.setText
		    (((LibraryFrame) JSLDesktop.getSelectedFrame()).myModel.PatchList.size() + " Patches");
	    } catch (Exception ex) {
		ErrorMsg.reportError("Error", "Library to Delete Duplicates in must be Focused", ex);
	    }
            PatchEdit.waitDialog.hide();
        }
    }

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

    static class NewPatchAction extends AbstractAction {
        public NewPatchAction(Map mnemonics) {
            super("New Patch...", null);
            setEnabled(false);
	    mnemonics.put(this, new Integer('N'));
        }
        public void actionPerformed(ActionEvent e) {
            try {
		NewPatchDialog np = new NewPatchDialog(PatchEdit.getInstance());
		np.setVisible(true);
		Patch p = np.getNewPatch();
		if (p != null)
		    ((PatchBasket) JSLDesktop.getSelectedFrame()).PastePatch(p);
	    } catch (Exception ex) {
		ErrorMsg.reportError("Error", "Unable to create this new patch.", ex);
	    }
        }
    }

    static class PrefsAction extends AbstractAction {
        public PrefsAction(Map mnemonics) {
            super("Preferences...", null);
	    mnemonics.put(this, new Integer('P'));
        }

        public void actionPerformed(ActionEvent e) {
            prefsDialog.show();
        }
    }

    static class SynthAction extends AbstractAction {
        public SynthAction(Map mnemonics) {
            super("Synths...", null);
	    mnemonics.put(this, new Integer('S'));
        }

        public void actionPerformed(ActionEvent e) {
	    SynthConfigDialog scd = new SynthConfigDialog(PatchEdit.getInstance());
	    scd.show();
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

    static class CrossBreedAction extends AbstractAction {
        public CrossBreedAction(Map mnemonics) {
            super("Cross Breed...", null);
            setEnabled(false);
            mnemonics.put(this, new Integer('B'));
        }
        public void actionPerformed(ActionEvent e) {
            try {
		CrossBreedDialog cbd = new CrossBreedDialog(PatchEdit.getInstance());
		cbd.show();
	    } catch (Exception ex) {
		ErrorMsg.reportError("Error", "Unable to perform Crossbreed. (No Library selected?)", ex);
	    }
        }
    }

    static class NextFaderAction extends AbstractAction {
        public NextFaderAction(Map mnemonics) {
	    super("Go to Next Fader Bank", null);
	    mnemonics.put(this, new Integer('F'));
        }
        public void actionPerformed(ActionEvent e) {
            if (!(JSLDesktop.getSelectedFrame() instanceof PatchEditorFrame))
		return;
            PatchEditorFrame pf = (PatchEditorFrame) JSLDesktop.getSelectedFrame();
            pf.nextFader();
	    return;
        }
    }

    static class DocsAction extends AbstractAction {
        public DocsAction(Map mnemonics) {
	    super("Help", null);
	    setEnabled(true);
	    mnemonics.put(this, new Integer('H'));
        }
        public void actionPerformed(ActionEvent e) {
	    try {
		if (docWin == null)
		    docWin = new DocumentationWindow("text/html", "file:doc/documentation.html");
		docWin.show();
	    } catch (Exception ex) {
		ErrorMsg.reportError("Error", "Unable to show Documentation)", ex);
	    }
        }
    }

    static class LicenseAction extends AbstractAction {
        public LicenseAction(Map mnemonics) {
	    super("License", null);
	    setEnabled(true);
	    mnemonics.put(this, new Integer('L'));
        }
        public void actionPerformed(ActionEvent e) {
	    try {
		if (licWin == null)
		    licWin = new DocumentationWindow("text/plain", "file:license.txt");
		licWin.show();
	    } catch (Exception ex) {
		ErrorMsg.reportError("Error", "Unable to show Documentation)", ex);
	    }
        }
    }

    static class HomePageAction extends AbstractAction {
        public HomePageAction(Map mnemonics) {
	    super("JSynthLib Home Page", null);
	    setEnabled(true);
	    mnemonics.put(this, new Integer('P'));
        }
        public void actionPerformed(ActionEvent e) {
	    try {
		if (hpWin == null)
		    // www.gnu.org is simple enough for J2SE 1.4.2.
		    //hpWin = new DocumentationWindow("text/html", "http://www.gnu.org/");
		    hpWin = new DocumentationWindow("text/html", "http://www.jsynthlib.org/");
		hpWin.show();
	    } catch (Exception ex) {
		ErrorMsg.reportError("Error", "Unable to show Documentation)", ex);
	    }
        }
    }

    static class MonitorAction extends AbstractAction {
        public MonitorAction(Map mnemonics) {
	    super("MIDI Monitor", null);
	    setEnabled(true);
	    mnemonics.put(this, new Integer('M'));
        }
        public void actionPerformed(ActionEvent e) {
	    try {
		if (midiMonitor == null)
		    midiMonitor = new MidiMonitor();
		midiMonitor.show();
	    } catch (Exception ex) {
		ErrorMsg.reportError("Error", "Unable to show MIDI Monitor)", ex);
	    }
        }
    }

    ////////////////////////////////////////////////////////////////////////
    // This allows icons to be loaded even if they are inside a Jar file
    private static ImageIcon loadIcon(String name) {
        Object icon;
        String jarName = null;
        icon = new ImageIcon(name);
        if (((ImageIcon) icon).getIconWidth() == -1) {
	    jarName = new String("/");
	    jarName = jarName.concat(name);
	    try {
		//icon = new ImageIcon(this.getClass().getResource(jarName));
		icon = new ImageIcon(PatchEdit.class.getClass().getResource(jarName));
	    } catch (java.lang.NullPointerException e) {
		ErrorMsg.reportStatus("ImageIcon:LoadIcon Could not find: " + name);
	    }
	}
        return (ImageIcon) icon;
    }

    ////////////////////////////////////////////////////////////////////////
    public static void showWaitDialog() {
	waitDialog.show();
    }

    public static void hideWaitDialog() {
	waitDialog.hide();
    }

    private class WaitDialog extends JDialog {
	WaitDialog(JFrame parent) {
	    super(parent,
		  "Please wait while the operation is completed", false);
	    setSize(350, 24);
	    centerDialog();
	}

	void centerDialog() {
	    Dimension screenSize = this.getToolkit().getScreenSize();
	    Dimension size = this.getSize();
	    screenSize.height = screenSize.height / 2;
	    screenSize.width = screenSize.width / 2;
	    size.height = size.height / 2;
	    size.width = size.width / 2;
	    int y = screenSize.height - size.height;
	    int x = screenSize.width - size.width;
	    this.setLocation(x, y);
	}
    }

    ////////////////////////////////////////////////////////////////////////
    // MIDI Master Input
    // masterInTrans (trns) -> MasterReceiver (rcvr1) -> initPortOut(rcvr)
    private static class MasterReceiver implements Receiver {
	private Receiver rcvr;

	MasterReceiver(Receiver rcvr) {
	    this.rcvr = rcvr;
	}

	//Receiver interface
	public void close() {
	    // don't close a shared Receiver
	    //if (rcvr != null) rcvr.close();
	}

	public void send(MidiMessage message, long timeStamp) {
	    int status = message.getStatus();
	    if ((0x80 <= status) && (status < 0xF0)) {  // MIDI channel Voice Message
		// I believe Sysex message must be ignored.
		//|| status == SysexMessage.SYSTEM_EXCLUSIVE)
		ErrorMsg.reportStatus("MasterReceiver: " + message);
		this.rcvr.send(message, timeStamp);
		MidiUtil.log("RECV: ", message);
	    }
	}
    }

    private static Transmitter trns;
    private static Receiver rcvr1;

    static void masterInEnable(boolean enable) {
	if (enable) {
	    // disable previous master in port if enabled.
	    masterInEnable(false);
	    // get transmitter
	    trns = MidiUtil.getTransmitter(appConfig.getMasterController());
	    // create output receiver
	    try {
		Receiver rcvr = MidiUtil.getReceiver(appConfig.getInitPortOut());
		rcvr1 = new MasterReceiver(rcvr);
		trns.setReceiver(rcvr1);
	    } catch (MidiUnavailableException e) {
		ErrorMsg.reportStatus(e);
	    }
	} else {
	    if (trns != null)
		trns.close();
	    if (rcvr1 != null)
		rcvr1.close();
	}
    }

    protected void finalize() {	// ???
	masterInEnable(false);
    }

    ////////////////////////////////////////////////////////////////////////
    private static class ExtensionFilter extends FileFilter {
	private String[] exts;
	private String desc;

	public ExtensionFilter (String desc, String exts) {
	    this (desc, new String[] {exts});
	}

	public ExtensionFilter (String desc, String[] exts) {
	    this.desc = desc;
	    this.exts = (String[]) exts.clone();
	}

	public boolean accept (File file) {
	    if (file.isDirectory()) {
		return true;
	    }
	    int count = exts.length;
	    String path = file.getAbsolutePath();
	    for (int i = 0; i < count; i++) {
		String ext = exts[i];
		if (path.toUpperCase().endsWith(ext.toUpperCase())
		    && (path.charAt(path.length() - ext.length()) == '.')) {
		    return true;
		}
	    }
	    return false;
	}

	public String getDescription() {
	    return (desc == null ? exts[0] : desc);
	}
    }
}
